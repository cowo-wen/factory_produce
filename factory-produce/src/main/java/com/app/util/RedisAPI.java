/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2016-12-25
 */
package com.app.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;


public class RedisAPI
{
    public static Log logger = LogFactory.getLog(RedisAPI.class);
    private static Map<String, JedisPool> poolMap = new HashMap<String, JedisPool>();

    private static Map<String, RedisBean> jedisBeanMap = new HashMap<String, RedisBean>();
    
    //@Autowired
    //private SysConfigProperties sysConfigProperties;
    
    @Autowired 
    private Environment env;

    /**
     * 数据缓存库
     */
    public final static String REDIS_CORE_DATABASE = "redis_core_database";
    
    
    
    private final static int MAX_TOTAL_NUM = 5000;
    

    private RedisBean bean;

    private JedisPool pool;
    
    
    
    
    
    /**
     * 获取redis对象
     * @param num
     * @return
     * @author chenwen 2017-7-18
     */
    private synchronized Jedis getJedisObj(int num){
        Jedis jedis = pool.getResource();
        if(!jedis.isConnected()){
            if(num >= MAX_TOTAL_NUM && num % MAX_TOTAL_NUM == 0){
                //关闭原来的连接池
                pool.close();
                pool = null;
                poolMap.remove(this.bean.getJedisIp()+this.bean.getJedisPort());
                logger.error("关闭原来的连接池"+this.bean.getJedisIp()+"_"+this.bean.getJedisPort());
                getPool(this.bean.getJedisIp(),this.bean.getJedisPort());
                logger.error("重新创建连接池"+this.bean.getJedisIp()+"_"+this.bean.getJedisPort());
                if(num / MAX_TOTAL_NUM > 5){
                    logger.error("获取连接池异常，超过次数"+this.bean.getJedisIp()+"_"+this.bean.getJedisPort());
                    return null;
                }
            }
            return getJedisObj(++num);
        }else{
            return jedis;
        }
    }

    public RedisAPI(String name)
    {
        super();
        if (!PublicMethod.isEmptyStr(name))
        {
            this.bean = getRedisBean(name);
        }
        else
        {
            this.bean = getRedisBean(REDIS_CORE_DATABASE);
        }
        pool = getPool(this.bean.getJedisIp(), this.bean.getJedisPort());
    }
    
    public synchronized static void setJedisBeanMap(String name,RedisBean bean){
        if(!jedisBeanMap.containsKey(name)){
            jedisBeanMap.put(name, bean);
        }
    }
    
    public RedisBean getRedisBean(){
        return bean;
    }
    
    public Jedis getJedis()
    {      
        return getJedisObj(0);
    }
    
    public void returnResource( Jedis jedis){
        returnResource(pool, jedis,1);
    }

    public synchronized RedisBean getRedisBean(String name)
    {
    	logger.equals(jedisBeanMap.size()+"-------"+name);
        if (jedisBeanMap.containsKey(name))
        {
            return jedisBeanMap.get(name);
        }
        else
        {
            
            RedisBean bean = new RedisBean();
            bean.setJedisIp("192.168.1.1");
            bean.setJedisPort(6380);
            jedisBeanMap.put(name, bean);
            return bean;
        }

    }

    /**
     * 构建redis连接池
     * 
     * @param ip
     * @param port
     * @return JedisPool
     */
    public synchronized static JedisPool getPool(String ip, Integer port)
    {
        if (!poolMap.containsKey(ip + port) || poolMap.get(ip + port) == null)
        {
            // String jedis_ip = SysParam.getValue(AppParam.Data_Exchange.getGroup(), AppParam.Data_Exchange.jedis_ip.toString());
         // 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
            JedisPoolConfig config = new JedisPoolConfig();
            // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            config.setMaxTotal(MAX_TOTAL_NUM);
            // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(30);
            //设置淘汰空闲连接的最小时间
            config.setMinEvictableIdleTimeMillis(1000*60*5);
            // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(1000 * 60 * 5);
            // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);
            poolMap.put(ip + port, new JedisPool(config, ip, port, 10 * 1000));
        }
        return poolMap.get(ip + port);
    }

    /**
     * 返还到连接池
     * 
     * @param pool
     * @param redis
     */
    @SuppressWarnings("deprecation")
	public synchronized static void returnResource(JedisPool pool, Jedis redis,int type)
    {
        if (redis != null)
        {
            if (!redis.isConnected() || type == 2)
            {
                pool.returnBrokenResource(redis);
            }
            else
            {
                pool.returnResource(redis);
            }

        }
    }

   

    /**
     * 通过对象获取数据
     * 
     * @param key
     * @return
     */
    public String get(String key)
    {
        String value = null;
        
        Jedis jedis = null;
        try
        {
            jedis = getJedis();
            value = jedis.get(key);
        }
        catch (Exception e)
        {
            // 释放redis对象
        	returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("get",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return value;
    }

    
    /**
     * 通过对象删除数据
     * 
     * @param key
     * @return
     */
    public long del(String key)
    {

        
        Jedis jedis = null;
        try
        {

            
            jedis = getJedis();
            return jedis.del(key);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return 0;
    }

    /**
     * 存放数据 有效时间2小时
     * 
     * @param key  key
     * @param value 值
     * @return
     */

    public void putTowHours(String key, String value)
    {
        put(key, value, 7200);
    }

    /**
     * 存放数据 有效时间1天
     * 
     * @param key
     *            key
     * @param value
     *            值
     * @return
     */
    public void putOneDay(String key, String value)
    {
        put(key, value, 86400);
    }

    /**
     * 存放数据 永久有效
     * 
     * @param key
     *            key
     * @param value
     *            值
     *            端口
     * @return
     */

    public void put(String key, String value)
    {
        put(key, value, -1);
    }

    /**
     * 存放数据
     * 
     * @param key
     *            key
     * @param value
     *            值
     * @param time
     *            有效时间
     * @return
     */

    public void put(String key, String value, int time)
    {
        Jedis jedis = null;
        try
        {
            if (!PublicMethod.isEmptyStr(key, value))
            {

                jedis = getJedis();
                if (time == -1)
                {
                    jedis.set(key, value);
                }
                else
                {
                    jedis.setex(key, time, value);
                }
            }

        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

    }

    /**
     * @author ql
     * @param key
     * @param field
     * @return
     */

    public String hget(String key, String field)
    {
        String value = null;
        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            value = jedis.hget(key, field);
        }
        catch (JedisDataException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return value;
    }

    public Long hincrBy(String key, String field)
    {
       
        return hincrBy(key, field, 1L);
    }
    
    public Long hincrBy(String key, String field,Long value)
    {
        Long result = 0L;
        Jedis jedis = null;
        try
        {
            jedis = getJedis();
            result = jedis.hincrBy(key, field, value);
        }catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return result;
    }

    /**
     * @author ql
     * @param key
     * @param field
     * @param ip
     * @param port
     * @return
     */
    public void expire(String key, int vailTime)
    {
        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            jedis.expire(key, vailTime);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
    }

    /**
     * @author 
     * @param key
     * @param field
     * @return
     */

    public String hget(String key, String field, int num)
    {
        String value = null;
        
        Jedis jedis = null;
        try
        {
            jedis = getJedis();
            value = jedis.hget(key, field);
        }
        catch (JedisConnectionException e)
        {
            if (num > 0)
            {
                return hget(key, field, num - 1);
            }
        }
        catch (JedisDataException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return value;
    }

    /**
     * 获取多个字段数据
     * 
     * @param key
     *            主键值
     * @return
     */

    /**
     * 获取多个字段数据
     * 
     * @param key 主键值
     * @return
     */
    public Map<String, String> hgetAll(String key)
    {
        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.hgetAll(key);
        }
        catch (JedisDataException e)
        {
            // 释放redis对象
            // returnResource(pool,jedis,2);

            logger.error("redis异常",e);
            throw e;
            // e.printStackTrace();
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return null;
    }
    
    /**
     * 获取多个字段数据
     * 
     * @param key 主键值
     * @param num 连接超时执行的次数
     * @return
     */
    public Map<String, String> hgetAll(String key,int num)
    {
        Jedis jedis = null;
        try
        {
            jedis = getJedis();
            return jedis.hgetAll(key);
        }catch (JedisConnectionException e)
        {
            if (num > 0)
            {
                return hgetAll(key, num - 1);
            }
        }
        catch (JedisDataException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return null;
    }



    /**
     * 将数据存放到缓存的key列表头
     * 
     * @param key
     *            主键值
     * @param value
     *            值
     * @param ip
     *            ip
     * @param port
     *            端口
     * @return
     */
    public Long lPush(String key, String value)
    {
        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.lpush(key, value);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return 0L;
    }

    /**
     * 将数据存放到缓存的key列表尾
     * 
     * @param key
     *            主键值
     * @param value
     *            值
     * @return
     */
    

    public Long rPush(String key, String value)
    {
        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.rpush(key, value);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return 0L;
    }

    /**
     * 获取缓存的key列表的长度
     * 
     * @param key
     *            主键值
     * @return
     */

    public Long lLen(String key)
    {
        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.llen(key);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return 0L;
    }

    /**
     * 删除并取得LIST头部一个元素
     * 
     * @param key
     *            主键值
     * @return
     */
    

    public String lPop(String key)
    {
        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.lpop(key);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return null;
    }


    /**
     * 删除并取得LIST 尾部一个元素
     * 
     * @param key
     *            主键值
     * @param ip
     *            ip
     * @param port
     *            端口
     * @return
     */
    public String rPop(String key)
    {
        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.rpop(key);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return null;
    }
    
    /**
     * 返回LIST集合指定,不删除原来元素
     * @param key
     * @param start 从0开始
     * @param end
     * @return
     * @author chenwen 2016-1-27
     */
    public List<String> lRange(String key, long start, long end) {
        
        Jedis jedis = null;
        try
        {
            jedis = getJedis();
            return jedis.lrange(key,start,end);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return null;
    }
    
    /**
     * 返回LIST集合的第一个元素,不删除原来元素
     * @param key
     * @param start
     * @param end
     * @return
     * @author chenwen 2016-1-27
     */
    public String lRangeFirst(String key) {
        List<String> list = lRange(key,0,0);
        if(list.isEmpty()){
            return null;
        }else{
            return list.get(0);
        }
    }
    
    /**
     * 返回LIST集合的最后一个元素,不删除原来元素
     * @param key
     * @return
     * @author chenwen 2016-1-27
     */
    public String lRangeEnd(String key) {
        long len = lLen(key);
        if(len > 0){
            --len;
            List<String> list = lRange(key,len,len);
            if(!list.isEmpty()){
                return list.get(0);
            }
        }
        return null;
    }

    

    /**
     * 通过对象删除数据
     * 
     * @param key
     * @return
     */
    public long hDel(String key, String... fields)
    {

        Jedis jedis = null;
        try
        {
            jedis = getJedis();
            return jedis.hdel(key, fields);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return 0;
    }

    public void hSet(String key, String[] fileds, String[] values)
    {
        
        Jedis jedis = null;
        try
        {
            int length = 0;
            if (fileds != null && (length = fileds.length) > 0)
            {
                
                jedis = getJedis();
                if (values == null)
                    values = new String[length];

                if (values.length != length)
                {
                    String[] valuesNew = new String[length];
                    for (int i = 0; i < values.length; i++)
                    {
                        valuesNew[i] = values[i];
                    }
                    values = valuesNew;
                }
                for (int i = 0; i < length; i++)
                {
                    jedis.hset(key, fileds[i], PublicMethod.isEmptyStr(values[i]) ? "" : values[i]);
                }
            }

        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
    }
    
    /**
     * 根据len的长度顺序保存hashset的值
     * @param key key值
     * @param len 长度（field：1，2，3...len）
     * @param value 保存的值
     * @author chenwen 2015-11-23
     */
    public int hSetByLen(String key, int len, String value)
    {
        Jedis jedis = null;
        int keyNum = 0;
        try
        {      
            if(len == 0)len = 1;
            jedis = getJedis();
            Map<String,String> map = jedis.hgetAll(key);
            Transaction tx = jedis.multi();
            if(map == null || map.size() == 0){
                tx.hset(key, String.valueOf(0), value);
            }else{
                if(PublicMethod.isEmptyStr(map.get(String.valueOf(len-1)))){
                    for(int i=0;i<len;i++){
                        if(PublicMethod.isEmptyStr(map.get(String.valueOf(i)))){
                            keyNum = i;
                            tx.hset(key, String.valueOf(i), value);
                            break;
                        }
                    }
                }else{
                    for(int i=0;i<len;i++){
                        if(i == (len-1)){
                            keyNum = i;
                            tx.hset(key, String.valueOf(i), value);
                        }else{
                            tx.hset(key, String.valueOf(i), map.get(String.valueOf(i+1)));
                        }
                    }
                }
            }
            tx.expire(key, 5184000);//保存两个月
            tx.exec();
            return keyNum;
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
            return -1;
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
    }

    /**
     * 向SET中添加一个成员
     * 
     * @param key
     *            key
     * @param value
     *            值数
     * @return
     */
   

    public void sAdd(String key, String value)
    {

        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            jedis.sadd(key, value);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

    }

    /**
     * 从SET中删除一个成员
     * 
     * @param key
     *            key
     * @param value
     *            值数
     * @return
     */
    public void sRem(String key, String value)
    {

        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            jedis.srem(key, value);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

    }
    
    public boolean exists(String key)
    {
        Jedis jedis = null;
        try
        {
            jedis = getJedis();
            return jedis.exists(key);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return false;
    }

    
    public Set<String> smembers(String key)
    {
        Jedis jedis = null;
        try
        {
            jedis = getJedis();
            return jedis.smembers(key);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

        return null;
    }
    /**
     * 取得SET成员总数
     * 
     * @param key
     * @return
     */

    public long sCard(String key)
    {

        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.scard(key);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return 0;
    }

    /**
     * 判断给定值是否为SET成员
     * 
     * @param key
     * @param value
     *            value
     * @return
     */

    public boolean sisMember(String key, String value)
    {

        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.sismember(key, value);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return false;
    }

    /**
     * 删除并返回SET任一成员
     * 
     * @param key
     * @return
     */

    public String sPop(String key)
    {

        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.spop(key);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return "";
    }

    /**
     * @param key
     *            redis list Key
     * @param beginIndex
     *            查询List开始位置
     * @param endIndex
     *            查询List结束位置
     * @param num
     *            超时重新查询的次数
     * @return String
     * @author zy 2014-9-12
     */

    public List<String> lrange(String key, int beginIndex, int endIndex, int num)
    {

        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            return jedis.lrange(key, beginIndex, endIndex);
        }
        catch (JedisConnectionException e)
        {
            if (num > 0)
            {
                return lrange(key, beginIndex, endIndex, num - 1);
            }
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return null;
    }

    /**
     * @param key
     *            redis list Key
     * @param value
     *            要删除的值
     * @param num
     *            超时重新删除的次数
     * @return String 删除成功后返回value
     * @author zy 2014-9-11
     */

    public String lrem(String key, String value, int num)
    {

        
        Jedis jedis = null;
        try
        {
            
            jedis = getJedis();
            jedis.lrem(key, 1, value);
            return value;
        }
        catch (JedisConnectionException e)
        {
            if (num > 0)
            {
                return lrem(key, value, num - 1);
            }
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return null;

    }
    
    /**
     * @author ql
     * @param key
     * @param field
     * @param ip
     * @param port
     * @return
     */
    public void publish(String key, String value)
    {
        Jedis jedis = null;
        try
        {
            jedis = getJedis();
            jedis.publish(key, value);
        }
        catch (Exception e)
        {
            // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
    }
    
    
    public Set<String> keys(String key)
    {
        Jedis jedis = null;
        try
        {

            jedis = getJedis();
            return jedis.keys(key);
        }
        catch (Exception e)
        {
         // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return null;
    }
    
    public void set(String key, String value)
    {
        Jedis jedis = null;
        try
        {

            jedis = getJedis();
            jedis.set(key, value);
        }
        catch (Exception e)
        {
         // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }

    }
    
    public List<String> sort(String key,SortingParams sortingParameters)
    {
        Jedis jedis = null;
        try
        {

            jedis = getJedis();
            return jedis.sort(key, sortingParameters);
        }
        catch (Exception e)
        {
         // 释放redis对象
            returnResource(pool,jedis,2);
            e.printStackTrace();
            logger.error("redis异常",e);
        }
        finally
        {
            // 返还到连接池
            returnResource(pool, jedis,1);
        }
        return new ArrayList<String>();
    }
    
    public static void testSort3()
    {
        Jedis jedis = getPool("127.0.0.1", 6379).getResource();// RedisUtil.getJedis();
        jedis.del("tom:friend:list", "score:uid:123", "score:uid:456", "score:uid:789", "score:uid:101", "uid:123", "uid:456", "uid:789", "uid:101");

        jedis.sadd("tom:friend:list", "123"); // tom的好友列表
        jedis.sadd("tom:friend:list", "456");
        jedis.sadd("tom:friend:list", "789");
        jedis.sadd("tom:friend:list", "101");

        jedis.set("score:uid:123", "1000"); // 好友对应的成绩
        jedis.set("score:uid:456", "6000");
        jedis.set("score:uid:789", "100");
        jedis.set("score:uid:101", "5999");

        jedis.set("uid:123", "123"); // 好友的详细信息
        jedis.set("uid:456", "456");
        jedis.set("uid:789", "789");
        jedis.set("uid:101", "101");

        SortingParams sortingParameters = new SortingParams();

        sortingParameters.asc();
         sortingParameters.limit(0, 30);
        // 注意GET操作是有序的，GET user_name_* GET user_password_*
        // 和 GET user_password_* GET user_name_*返回的结果位置不同
        // sortingParameters.get("#");// GET 还有一个特殊的规则—— "GET #"
        // ，用于获取被排序对象(我们这里的例子是 user_id )的当前元素。
        sortingParameters.get("uid:*");
        //sortingParameters.get("score:uid:*");
        sortingParameters.by("score:uid:*");
        // 对应的redis 命令是./redis-cli sort tom:friend:list by score:uid:* get # get
        // uid:* get score:uid:*
        List<String> result = jedis.sort("tom:friend:list", sortingParameters);
        for (String item : result)
        {
            System.out.println("item..." + item);
        }

    }
    /*
    public static void main(String[] args) throws Exception
    {

        // RedisAPI.put("monitor:show_log", "true", "10.10.0.18", 6385);
        // RedisAPI.put("monitor:show_log:show_school_channel", "true", "192.168.1.26",6385);
        // System.out.println("".equals(RedisAPI.get("monitor:show_log:show_school_channel2", "192.168.1.26", 6385)));

        RedisBean bean = new RedisBean();
        bean.setJedisIp("127.0.0.1");
        bean.setJedisPort(6379);
        
        jedisBeanMap.put("myRedis", bean);
        RedisAPI api = new RedisAPI("myRedis");
        //api.lPush("key", "1");
        //api.lPush("key", "2");
        //api.lPush("key", "3");
        
        String s = api.lRangeFirst("key");
            System.out.println(s);
        
        String [] r = new String[]{"4","66","22"};
        System.out.println(r);
        //testSort3();

    }*/
}
