/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PublicMethod
{
    //private static final Logger logger = Logger.getLogger(PublicMethod.class);

   

    protected PublicMethod()
    {
    }

    /**
     * 判断一个字符串是否为空 null 和 空字符
     * 
     * @param str
     *            String
     * @return Boolean 为空返回True 不为空返回 False
     */
    public static boolean isEmptyStr(Object str)
    {
        return str == null || str.toString().trim().length() < 1 ? true : false;
    }

    /**
     * 判断一个值是否为空 null、（int或long）0 和 空字符,
     * 
     * @param str
     *            String
     * @return Boolean 为空返回True 不为空返回 False
     */
    public static boolean isEmptyValue(Object str)
    {
        if (str == null || str.toString().trim().length() < 1 || str.toString().toLowerCase().equals("null"))
        {
            return true;
        }
        if (str.getClass().toString().endsWith("java.lang.Integer") || str.getClass().toString().endsWith("java.lang.Long"))
        {
            return str.toString().equals("0") ? true : false;
        }
        return false;
    }

    /**
     * 判断多个字符串是否为空、（int或long）0 或null; 有一个也返回True
     * 
     * @param str
     *            ... 多个字符串
     * @return Boolean 为空返回True 不为空返回 False
     */
    public static boolean isEmptyValue(Object... str)
    {
        for (Object temp : str)
        {
            if (temp == null || temp.toString().trim().length() < 1)
            {
                return true;
            }
            if (temp.getClass().toString().endsWith("Integer") || temp.getClass().toString().endsWith("Long"))
            {
                if (PublicMethod.objectToLong(temp) == 0)
                {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * 判断多个字符串是否为空 有一个也返回True
     * 
     * @param str
     *            ... 多个字符串
     * @return Boolean 为空返回True 不为空返回 False
     */
    public static boolean isEmptyStr(Object... str)
    {
        for (Object temp : str)
        {
            if (temp == null || temp.toString().trim().length() < 1)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 将字符串转换为整型
     * 
     * @param str
     *            String
     * @return Integer 当为Null或转换错误时返回Null
     */
    public static Integer stringToInteger(String str)
    {
        return stringToInteger(str, null);
    }

    /**
     * 将字符串转换为整型
     * 
     * @param str
     *            String
     * @return Integer 当为Null或转换错误时返回Null
     */
    public static Long stringToLong(String str)
    {
        return stringToLong(str, null);
    }

    /**
     * 将Object转换为整型
     * 
     * @param str
     *            String
     * @return Integer 当为Null或转换错误时返回Null
     */
    public static Integer objectToInteger(Object str)
    {
        return objectToInteger(str, null);
    }

    /**
     * 将Object转换为整型 检查长度
     * 
     * @param str
     *            String
     * @return Integer 当为Null或转换错误时返回Null
     */
    public static Integer objectToInteger(Object str, Integer def, int maxlen)
    {
        Integer i = objectToInteger(str, def);
        if (null != i && i.toString().length() > maxlen)
        {
            return null;
        }
        else
        {
            return i;
        }
    }

    /**
     * 将Object转换为Long
     * 
     * @param str
     *            String
     * @return Integer 当为Null或转换错误时返回Null
     */
    public static Long objectToLong(Object str)
    {
        return objectToLong(str, null);
    }

    /**
     * 将Object转换为整型
     * 
     * @param str
     *            String
     * @return Integer 当为Null或转换错误时返回Null
     */
    public static Integer objectToInteger(Object str, Integer def)
    {
        try
        {
            return stringToInteger(str.toString(), null);
        }
        catch (Exception e)
        {
            return def;
        }
    }

    /**
     * 将Object转换为整型
     * 
     * @param str
     *            String
     * @return Integer 当为Null或转换错误时返回Null
     */
    public static Long objectToLong(Object str, Long def)
    {
        try
        {
            return stringToLong(str.toString(), null);
        }
        catch (Exception e)
        {
            return def;
        }
    }

    /**
     * 将Object转换为Float
     * 
     * @param str
     *            String
     * @return Float
     */
    public static Float objectToFloat(Object str)
    {
        return objectToFloat(str, null);
    }

    /**
     * 将Object转换为Float
     * 
     * @param str
     *            String
     * @param def
     *            默认
     * @return Float
     */
    public static Float objectToFloat(Object str, Float def)
    {
        try
        {
            return Float.valueOf(str.toString());
        }
        catch (Exception e)
        {
            return def;
        }
    }

    /**
     * 获取字符串 如果为空返回""
     * 
     * @param str
     *            字符串
     * @return String
     */
    public static String getNotNullString(String str)
    {
        return str == null ? "" : str;
    }

    /**
     * object 转换为String 为null 返回null
     * 
     * @param str
     *            Object
     * @return String
     */
    public static String objectToString(Object str)
    {
        if (null == str)
        {
            return null;
        }
        else
        {
            return str.toString();
        }
    }

    /**
     * object 转换为String 为null 返回null
     * 
     * @param str
     *            对象
     * @param maxlen
     *            最大长度 如果超出 会根据长度截断
     * @return String
     */
    public static String objectToString(Object str, int maxlen)
    {
        if (null == str)
        {
            return null;
        }
        else
        {
            String temp = str.toString();
            if (temp.length() > maxlen)
            {
                return temp.substring(0, maxlen - 1);
            }
            else
            {
                return temp;
            }
        }
    }

    /**
     * 将字符串转换为整型
     * 
     * @param str
     *            String
     * @param def
     *            错误时返回的缺省值
     * @return Integer 当为Null或转换错误时返回def 值
     */
    public static Integer stringToInteger(String str, Integer def)
    {
        if (isEmptyStr(str))
        {
            return def;
        }
        else
        {
            try
            {
                return Integer.parseInt(str.trim());
            }
            catch (Exception e)
            {
                return def;
            }
        }
    }

    /**
     * 将字符串转换为Long
     * 
     * @param str
     *            String
     * @param def
     *            错误时返回的缺省值
     * @return Integer 当为Null或转换错误时返回def 值
     */
    public static Long stringToLong(String str, Long def)
    {
        if (isEmptyStr(str))
        {
            return def;
        }
        else
        {
            try
            {
                return Long.parseLong(str.trim());
            }
            catch (Exception e)
            {
                return def;
            }
        }
    }

    /**
     * 将字符串转换为浮点型
     * 
     * @param str
     *            String
     * @return Integer 当为Null或转换错误时返回Null
     */
    public static Float stringToFloat(String str)
    {
        return stringToFloat(str, null);
    }

    /**
     * 将字符串转换为浮点型
     * 
     * @param str
     *            String
     * @param def
     *            错误时返回的缺省值
     * @return Integer 当为Null或转换错误时返回def 值
     */
    public static Float stringToFloat(String str, Float def)
    {
        if (isEmptyStr(str))
        {
            return def;
        }
        else
        {
            try
            {
                return Float.parseFloat(str.trim());
            }
            catch (Exception e)
            {
                return def;
            }
        }
    }

    /**
     * 格式化时间 默认yyyy-MM-dd HH:mm:ss
     * 
     * @param mat
     *            格式化形式 为null 使用默认样式
     * @return 返回Date
     */
    public static Date formatDate(String mat)
    {
        String str = "yyyy-MM-dd HH:mm:ss";
        if (mat != null)
        {
            str = mat;
        }
        SimpleDateFormat format = new SimpleDateFormat(str);
        TimeZone t = TimeZone.getTimeZone("GMT+08:00");// 获取东8区TimeZone
        Calendar c = Calendar.getInstance(t);
        String d = format.format(c.getTime());
        Date formatdate = null;
        try
        {
            formatdate = format.parse(d);
        }
        catch (Exception e)
        {
            // e.printStackTrace();
        }
        return formatdate;
    }

    /**
     * 转换时间,控制时区东八区
     * 
     * @param date
     *            时间
     * @return Date
     */
    public static Date formatDateZone(Date date)
    {
        TimeZone t = TimeZone.getTimeZone("GMT+08:00");// 获取东8区TimeZone
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.setTimeZone(t);
        return c.getTime();
    }
    
    /**
     * 格式化时间 默认yyyy-MM-dd HH:mm:ss
     * 
     * @return 返回Date
     */
    public synchronized static String formatDateStr(Date date)
    {
       return formatDateStr(date,null);
    }

    /**
     * 格式化时间 默认yyyy-MM-dd HH:mm:ss
     * 
     * @param mat
     *            格式化形式 为null 使用默认样式
     * @return 返回Date
     */
    public synchronized static String formatDateStr(Date date, String mat)
    {
        String str = "yyyy-MM-dd HH:mm:ss";
        if (mat != null)
        {
            str = mat;
        }
        try
        {
            SimpleDateFormat format = new SimpleDateFormat(str);
            return format.format(date.getTime());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 格式化当前时间 默认yyyy-MM-dd HH:mm:ss
     * 
     * @param mat
     *            格式化形式 为null 使用默认样式
     * @return 返回Date
     */
    public static String formatDateStr(String mat)
    {
        String str = "yyyy-MM-dd HH:mm:ss";
        if (mat != null)
        {
            str = mat;
        }
        SimpleDateFormat format = new SimpleDateFormat(str);
        TimeZone t = TimeZone.getTimeZone("GMT+08:00");// 获取东8区TimeZone
        Calendar c = Calendar.getInstance(t);
        return format.format(c.getTime());
    }

    /**
     * String 型时间到 Data型
     * 
     * @param mat
     *            为null 默认 yyyy-MM-dd HH:mm:ss
     * @param date
     *            字符串时间
     * @return Date
     */
    public static Date stringToDate(String date, String mat)
    {
        mat = mat == null ? "yyyy-MM-dd HH:mm:ss" : mat;
        SimpleDateFormat format = new SimpleDateFormat(mat);
        Date formatdate = null;
        try
        {
            formatdate = format.parse(date);
        }
        catch (Exception e)
        {
            // e.printStackTrace();
        }
        return formatdate;
    }

    /**
     * 返回指定月数后的时间
     * 
     * @param month
     *            int 月数
     * @return Date
     */
    public static Date getAfterByMonth(int month)
    {
        return getAfterDateByNumber(Calendar.MONTH, month);
    }

    /**
     * 返回指定月数前的时间
     * 
     * @param month
     *            int 月数
     * @return Date
     */
    public static Date getBeforByMonth(int month)
    {
        return getBeforeDateByNumber(Calendar.MONTH, month);
    }

    private static Date getAfterDateByNumber(int type, int num)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(type, calendar.get(type) + num);
        return calendar.getTime();
    }

    private static Date getBeforeDateByNumber(int type, int num)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(type, calendar.get(type) - num);
        return calendar.getTime();
    }

    /**
     * 计算年龄
     * 
     * @param birthday
     *            出生年月
     * @param now
     *            截至时间
     * @return int
     */
    public static int getAge(Date birthday, Date now)
    {
        long day = (now.getTime() - birthday.getTime()) / (24 * 60 * 60 * 1000) + 1;
        return (int) day / 365;
    }

    /**
     * 返回今天开始时间
     * 
     * @return String
     *         <p>
     *         字符串形式 yyyy-MM-dd HH:mm:ss
     */
    public static String getDayFirstTimeString()
    {
        return getDayFirstTimeString(new Date());
    }

    /**
     * 返回指定天开始时间
     * 
     * @param date
     *            Date
     * @return String
     *         <p>
     *         字符串形式 yyyy-MM-dd HH:mm:ss
     */
    public static String getDayFirstTimeString(Date date)
    {
        return formatDateStr(date, "yyyy-MM-dd") + "_00:00:00";
    }

    /**
     * 返回今天的结束时间
     * 
     * @return String
     *         <p>
     *         字符串形式 yyyy-MM-dd HH:mm:ss
     */
    public static String getDayEndTimeString()
    {
        return getDayEndTimeString(new Date());
    }

    /**
     * 返回指定天的结束时间
     * 
     * @param date
     *            Date
     * @return String
     *         <p>
     *         字符串形式 yyyy-MM-dd HH:mm:ss
     */
    public static String getDayEndTimeString(Date date)
    {
        return formatDateStr(date, "yyyy-MM-dd") + "_23:59:59";
    }

    /**
     * 获取指定天的开始时间和结束时间
     * 
     * @param date
     *            Date
     * @return String
     *         <p>
     *         字符串形式 yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm:ss
     */
    public static String getDayTimeString(Date date)
    {
        String day = formatDateStr(date, "yyyy-MM-dd");
        StringBuffer a = new StringBuffer();
        a.append(day);
        a.append("_00:00:00,");
        a.append(day);
        a.append("_23:59:59");
        return a.toString();
    }

    /**
     * 获取两个时间的开始和结束时间
     * 
     * @return String
     *         <p>
     *         字符串形式 yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm:ss
     *         <p>
     *         当某一个参数为空时返回值中对应位置为空字符串,当两个都为空是,返回null
     */
    public static String getDayTimeString(Date beginDate, Date endTime)
    {
        StringBuffer day = new StringBuffer();
        if (null == beginDate)
        {
            if (null == endTime)
            {
                return null;
            }
            else
            {
                day.append("");
                day.append(",");
                day.append(getDayEndTimeString(endTime));
            }
        }
        else
        {
            if (null == endTime)
            {
                day.append(getDayFirstTimeString(beginDate));
                day.append(",");
                day.append("");
            }
            else
            {
                day.append(getDayFirstTimeString(beginDate));
                day.append(",");
                day.append(getDayEndTimeString(endTime));
            }
        }
        return day.toString();
    }

    /**
     * 返回今天的开始时间和结束时间
     * <p>
     * 字符串形式 yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm:ss
     * 
     * @return String
     */
    public static String getDayTimeString()
    {
        return getDayTimeString(new Date());
    }

    /**
     * 获取一个日期所在的周数是第几年的第几周（2013-52）
     * 
     * @param dateStr
     * @return
     * @author Administrator 2013-12-23
     */
    public static String getYearWeekNum(String dateStr)
    {
        int week = 0;
        int year = 0;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cl = Calendar.getInstance();
            cl.setTime(sdf.parse(dateStr));
            week = cl.get(Calendar.WEEK_OF_YEAR);
            cl.add(Calendar.DAY_OF_MONTH, -7);
            year = cl.get(Calendar.YEAR);
            if (week < cl.get(Calendar.WEEK_OF_YEAR))
            {
                year += 1;
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
        return year + "-" + week;
    }

    /**
     * 获取月份的最大天数
     * 
     * @param date
     * @return
     * @author Huanghuahui 2014-1-26
     */
    public static int getMonthMaxDay(Date date)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return maxDay;
    }

    /**
     * 获取月份的第一天日期
     * 
     * @param date
     * @return
     * @author Huanghuahui 2014-1-26
     */
    public static String getMonthFirstDayStr(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        String firstDay = formatter.format(c.getTime());
        return firstDay;
    }

    /**
     * 获取月份的第一天日期
     * 
     * @param date
     * @return
     * @author Huanghuahui 2014-1-26
     */
    public static Date getMonthFirstDay(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        Date firstDay = c.getTime();
        return firstDay;
    }

    /**
     * 获取月份的最后一天日期
     * 
     * @param date
     * @return
     * @author Huanghuahui 2014-1-26
     */
    public static String getMonthLastDay(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 0);
        // 获取当前月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = formatter.format(c.getTime());
        return lastDay;
    }

    /**
     * 获取当前月份的所有日期
     * 
     * @author HuangHuaHui 2014-1-26
     */
    @SuppressWarnings("static-access")
    public static String[] getMonthAllDay(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String firstDay = getMonthFirstDayStr(date);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(getMonthFirstDay(date));
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] allDayStr = new String[maxDay];
        allDayStr[0] = firstDay;
        for (int i = 1; i < maxDay; i++)
        {
            calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
            String dateString = formatter.format(date);
            allDayStr[i] = dateString;
        }
        return allDayStr;
    }

    /**
     * 生成指定位数字符串,不足位数用指定字符串填充
     * 
     * @param len
     *            字符串长度
     * @param str
     *            字符串
     * @param fillStr
     *            填充的字符串
     * @return String
     */
    public static String fillString(int len, String str, String fillStr)
    {
        int strLen = str.length();
        if (strLen < len)
        {
            StringBuffer temp = new StringBuffer();
            for (int i = 0; i < len - strLen; i++)
            {
                temp.append(fillStr);
            }
            temp.append(str);
            return temp.toString();
        }
        else
        {
            return str;
        }
    }

    /**
     * 生成Bean 并拷贝Obj中对应属性的值
     * <p>
     * 支持String转Integer String转Date
     * <p>
     * 当类型不匹配时 Bean对应属性为Null
     * 
     * @param clazz
     *            需要生存的Bean Class
     * @param obj
     *            拷贝值的对象
     * @return Bean
     */
    public static Object createBean(Class<?> clazz, Object obj)
    {
        PropertyDescriptor[] beanFields;
        PropertyDescriptor[] objFields;
        try
        {
            beanFields = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
            objFields = Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors();
        }
        catch (IntrospectionException e)
        {
            return null;
        }
        Object beanObj;
        try
        {
            beanObj = Class.forName(clazz.getName()).newInstance();
        }
        catch (Exception e)
        {
            return null;
        }
        Object tmp = null;
        for (PropertyDescriptor beanField : beanFields)
        {
            if (beanField.getName().equals("class"))
            {
                continue;
            }
            for (PropertyDescriptor objField : objFields)
            {
                if (beanField.getName().equals(objField.getName()))
                {
                    try
                    {
                        tmp = objField.getReadMethod().invoke(obj);
                        if (null != tmp)
                        {
                            if (beanField.getPropertyType().equals(java.util.Date.class))
                            {
                                beanField.getWriteMethod().invoke(beanObj, stringToDate(tmp.toString(), null));
                                continue;
                            }
                            if (beanField.getPropertyType().equals(java.lang.Integer.class))
                            {
                                beanField.getWriteMethod().invoke(beanObj, stringToInteger(tmp.toString()));
                                continue;
                            }
                            if (beanField.getPropertyType().equals(java.lang.Long.class))
                            {
                                beanField.getWriteMethod().invoke(beanObj, stringToLong(tmp.toString()));
                                continue;
                            }
                            try
                            {
                                beanField.getWriteMethod().invoke(beanObj, tmp);
                                continue;
                            }
                            catch (Exception e)
                            {
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        return null;
                    }
                }
            }
        }
        return beanObj;
    }

  
    /**
     * 根据属性名称设置属性值
     * 
     * @param obj
     *            Object 实例化对象
     * @param prop
     *            属性名
     * @param value
     *            值
     * @return boolean 返回是否执行成功
     */
    public static boolean setPropertyValue(Object obj, String prop, Object value)
    {
        try
        {
            Method method = getProperty(obj, prop).getWriteMethod();
            Class<?> paramClass = method.getParameterTypes()[0];
            if (paramClass.equals(value.getClass()))
            {
                method.invoke(obj, value);
            }
            else
            {
                method.invoke(obj, conventObject(paramClass, value));
            }
            return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * 根据属性名称读取属性值
     * 
     * @param obj
     *            Object 实例化对象
     * @param prop
     *            属性名
     * @return Object 返回属性值
     */
    public static Object getPropertyValue(Object obj, String prop)
    {
        try
        {
            return getProperty(obj, prop).getReadMethod().invoke(obj);
        }
        catch (Exception e)
        {
        }
        return null;
    }

    /**
     * @param owner
     * @param methodName
     * @param args
     * @return ObjectObject
     * @throws Exception
     */
    public static Object invokeMethod(Object owner, String methodName, Object[] args) throws Exception
    {
        Class<? extends Object> ownerClass = owner.getClass();
        Class<? extends Object>[] argsClass = null;
        if (args != null)
        {
            argsClass = new Class<?>[args.length];
            for (int i = 0, j = args.length; i < j; i++)
            {
                argsClass[i] = args[i].getClass();
            }
        }
        Method method = ownerClass.getMethod(methodName, argsClass);
        return method.invoke(owner, args);
    }

    /**
     * 根据属性名称读取属性
     * 
     * @param obj
     *            Object 实例化对象
     * @param property
     *            属性名
     * @return PropertyDescriptor
     */
    public static PropertyDescriptor getProperty(Object obj, String property) throws Exception
    {
        return new PropertyDescriptor(property, obj.getClass());
        /*
         * PropertyDescriptor props[] = getPropertys(obj); for (PropertyDescriptor beanField : props) { if
         * (beanField.getName().equals(property)) { return beanField; } }
         */
        // throw new Exception("getProperty Not Fond property:" + property);
    }

    /**
     * 根据属性名称读取属性
     * 
     * @param obj
     *            Object 实例化对象
     * @return PropertyDescriptor[]
     */
    public static PropertyDescriptor[] getPropertys(Object obj) throws Exception
    {
        return Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors();
    }

    /**
     * 获取IP地址
     * 
     * @param request
     *            HttpServletRequest
     * @return String
     */
    public static String getIpAddr(HttpServletRequest request)
    {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 效验是否是IP地址
     * 
     * @param s
     *            IP地址
     * @return Boolean
     */
    public static Boolean isIpAddress(String s)
    {
        String regex = "(((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        return m.matches();
    }

    /**
     * 追加内容到文件末尾
     * 
     * @param filePath
     *            文件路径
     * @param content
     *            文件内容
     * @param codeing
     *            编码
     */
    public static void writeFile(String filePath, String content, String codeing) throws Exception
    {
        BufferedWriter out = null;
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                file.createNewFile();
            }
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), codeing));
            out.write(content);
        }
        catch (Exception e)
        {
            throw new Exception(e);
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    /**
     * 合并Txt文件
     * 
     * @param newfilePath
     *            合并后的文件路径
     * @param code
     *            编码
     * @param files
     *            需要合并的文件,支持多个,依此合并
     */
    public static void mergeFiles(String newfilePath, String code, String... files) throws Exception
    {
        BufferedWriter out = null;
        try
        {
            File newFile = new File(newfilePath);
            if (!newFile.exists())
            {
                newFile.createNewFile();
            }
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile, true), code));
            for (String filePath : files)
            {
                File file = new File(filePath);
                if (!file.exists())
                {
                    continue;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), code));
                int line;
                while ((line = br.read()) != -1)
                {
                    out.write(line);
                }
                if (br != null)
                    br.close();
            }
        }
        catch (Exception e)
        {
            throw new Exception(e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
            }
        }
    }
    
    public synchronized static OutputStream assemblyHeader(HttpServletRequest request, HttpServletResponse response, String fileName) throws Exception
    {
        response.reset();
        response.setContentType("application/x-msdownload");
        String agent = request.getHeader("User-Agent");
        if ((agent != null && agent.indexOf("MSIE") != -1))
        {
            fileName = URLEncoder.encode(fileName, StaticBean.CHAR_CODE.utf_8.toString());
        }
        else
        {
            fileName = new String(fileName.getBytes(StaticBean.CHAR_CODE.utf_8.toString()), StaticBean.CHAR_CODE.iso.toString());
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        return response.getOutputStream();
    }

    /**
     * 将List转换为String
     * 
     * @param list
     *            List
     * @param spiltStr
     *            间隔字符串
     * @return String 如果list为空或长度为0,将返回Null
     */
    public static String listToString(List<?> list, String spiltStr)
    {
        if (null == list)
        {
            return null;
        }
        StringBuffer str = new StringBuffer();
        for (Object obj : list)
        {
            if (null != obj)
            {
                str.append(obj.toString());
                str.append(spiltStr);
            }
        }
        if (str.length() > 0)
        {
            return str.substring(0, str.length() - spiltStr.length());
        }
        else
        {
            return null;
        }
    }

    /**
     * 动态类型转换,支持从String转换为Integer,Long,Date;"null"字符串转换为null
     * 
     * @param clazz
     *            类型
     * @param obj
     *            对象值
     * @return Object 转换后的对象
     */
    @SuppressWarnings("unchecked")
    public static <TT> TT conventObject(Class<TT> clazz, Object obj)
    {
        if ("null".equals(obj.toString()))
        {
            return null;
        }
        if (java.lang.Integer.class.equals(clazz))
        {
            return (TT) Integer.valueOf(obj.toString());
        }
        if (java.lang.Long.class.equals(clazz))
        {
            return (TT) Long.valueOf(obj.toString());
        }
        if (java.util.Date.class.equals(clazz))
        {
            return (TT) PublicMethod.stringToDate(obj.toString(), "yyyy-MM-dd HH:mm:ss");
        }
        return (TT) obj;
    }

    /**
     * 将字符串转换为List<String>
     * 
     * @param str
     *            字符串
     * @param formart
     *            分隔符
     * @return List<String>
     */
    public static List<String> stringToList(String str, String formart)
    {
        String[] s = str.split(formart);
        List<String> ss = new ArrayList<String>();
        for (String v : s)
        {
            if (!PublicMethod.isEmptyStr(v))
            {
                ss.add(v);
            }
        }
        return ss;
    }

    /**
     * 把Bytes转成字符串
     * 
     * @param data
     *            byte[]
     * @return String
     */
    public static String bytesToString(byte[] data)
    {
        StringBuffer str = new StringBuffer();
        for (byte b : data)
        {
            str.append(toHexString(b));
        }
        return str.toString();
    }

    /**
     * byte转字符串形式
     * 
     * @param value
     *            byte
     * @return String
     */
    public static String toHexString(byte value)
    {
        String tmp = Integer.toHexString(value & 0xFF);
        if (tmp.length() == 1)
        {
            tmp = "0" + tmp;
        }
        return tmp.toUpperCase();
    }

    /**
     * 过滤xml非法字符
     * 
     * @param in
     *            字符串
     * @return String
     */
    public static String stripNonValidXMLCharacters(String in)
    {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.
        if (in == null || ("".equals(in)))
        {
            return ""; // vacancy test.
        }
        for (int i = 0; i < in.length(); i++)
        {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught
            // here; it should not happen.
            if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF)) || ((current >= 0xE000) && (current <= 0xFFFD)) || ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }

    /**
     * 复制文件
     * 
     * @param sourceFile
     *            原文件
     * @param targetFile
     *            新文件
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException
    {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try
        {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1)
            {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        }
        finally
        {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    /**
     * 复制文件夹
     * 
     * @param sourceDir
     *            原目录
     * @param targetDir
     *            新目录
     */
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException
    {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++)
        {
            if (file[i].isFile())
            {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory())
            {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    /**
     * 删除文件或文件夹
     * 
     * @param filepath
     *            文件路径
     * @throws IOException
     */
    public static void del(String filepath) throws IOException
    {
        File f = new File(filepath);// 定义文件路径
        if (f.exists() && f.isDirectory())
        {// 判断是文件还是目录
            if (f.listFiles().length == 0)
            {// 若目录下没有文件则直接删除
                f.delete();
            }
            else
            {// 若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++)
                {
                    if (delFile[j].isDirectory())
                    {
                        del(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();// 删除文件
                }
            }
        }
    }

    
}
