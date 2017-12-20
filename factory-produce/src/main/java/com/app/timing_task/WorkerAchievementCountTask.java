/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2014-12-28
 */
package com.app.timing_task;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.app.controller.common.Result;
import com.app.dao.JdbcDao;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.dao.sql.cnd.LTCnd;
import com.app.dao.sql.cnd.RTCnd;
import com.app.entity.event.EventSanctionEntity;
import com.app.entity.report.WorkerAchievementDayEntity;
import com.app.entity.task.TaskProduceEntity;
import com.app.entity.task.TaskReviewEntity;
import com.app.entity.task.TaskWorkerEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;

/**
 * 功能说明：数据清理的线程
 * 
 * @author chenwen 2014-12-28
 */
public class WorkerAchievementCountTask implements Runnable
{
    public static Log logger = LogFactory.getLog(WorkerAchievementCountTask.class);

    private static boolean isRun = false;

    private static int day = 0;
    

    private WorkerAchievementCountTask() {
		super();
	}


	@Override
    public void run()
    {
        
		Calendar cal = Calendar.getInstance();
		int nowDay = cal.get(Calendar.DAY_OF_YEAR);
		JdbcDao jdbcDao = null;
        try
        {
        	jdbcDao = new Result().getJdbcDao();
        	jdbcDao.useTransaction();
        	if(nowDay == day){
        		Thread.sleep(1800000);//休眠三十分钟
    			return ;
    		}else{
    			day = nowDay;
    		}
        	cal.add(Calendar.DAY_OF_YEAR, -1);
        	createCountDay(cal,jdbcDao);
        	if(day == 1){//一月的开始
        		createCountMonth(cal,jdbcDao);
        	}
        }
        catch (Exception e)
        {
        	if(jdbcDao != null){
        		jdbcDao.rollback();
        	}
        	day = 0;
            e.printStackTrace();
            logger.error("员工生效绩效统计任务====================" + e.getMessage());
           
        }
        finally
        {
            
            isRun = false;
        }

    }
	public void createCountMonth(Calendar cal,JdbcDao jdbcDao) throws Exception{
		
	}
	
	
	public void createCountDay(Calendar cal,JdbcDao jdbcDao) throws Exception{
		
    	int countDay = Integer.parseInt(PublicMethod.formatDateStr(cal.getTime(), "yyyyMMdd"));
    	List<WorkerAchievementDayEntity> list = new WorkerAchievementDayEntity(jdbcDao).getListVO(new SQLWhere(new EQCnd(WorkerAchievementDayEntity.COUNT_DAY, countDay)));
    	for(WorkerAchievementDayEntity workerCount : list){
    		workerCount.delete();
    	}
    	list.clear();//清空集合
    	
    	String date = PublicMethod.formatDateStr(cal.getTime(),"yyyy-MM-dd");
    	List<TaskProduceEntity> list2 = new TaskProduceEntity(jdbcDao).getListVO(new SQLWhere(new EQCnd(TaskProduceEntity.STATUS, TaskProduceEntity.PRODUC_STATUS_FINISH))
    	.and(new RTCnd(TaskProduceEntity.OPERATOR_TIME, date+" 00:00:00")).and(new LTCnd(TaskProduceEntity.OPERATOR_TIME, date+" 23:59:59")));
    	Map<Long,WorkerAchievementDayEntity> map = new HashMap<Long,WorkerAchievementDayEntity>();
    	for(TaskProduceEntity task : list2){
    		List<TaskWorkerEntity> workerList = new TaskWorkerEntity(jdbcDao).setProduceId(task.getProduceId()).queryCustomCacheValue(0);
    		for(TaskWorkerEntity worker : workerList){//获取员工
    			WorkerAchievementDayEntity workerCount = null;
    			if(map.containsKey(worker.getUserId())){
    				workerCount = map.get(worker.getUserId());
    			}else{
    				workerCount = new WorkerAchievementDayEntity(jdbcDao,worker.getUserId(),countDay);
    				map.put(worker.getUserId(), workerCount);
    			}
    			
    			//任务数
    			workerCount.setTaskTime(workerCount.getTaskTime()+1);//任务加1
    			//工钱
    			workerCount.setProduceMoney(worker.getNumber()*task.getWages() + workerCount.getProduceMoney());//生产绩效
    			//超时任务数
    			if(task.getEndTime() != null && task.getOperatorTime().getTime() > task.getEndTime().getTime()){
    				workerCount.setOvertime(workerCount.getOvertime()+1);
    			}
    			
    			//返工数
    			List<TaskReviewEntity> list3 = new TaskReviewEntity(jdbcDao).setProduceId(task.getProduceId()).queryCustomCacheValue(0);
    			if(list3.size() > 1){
    				workerCount.setReworkTime(workerCount.getReworkTime() + list3.size() -1);
    			}
    			
    		}
    	}
    	
    	
    	List<EventSanctionEntity> sanctionList = new EventSanctionEntity(jdbcDao).getListVO(new SQLWhere(new EQCnd(EventSanctionEntity.CHECK_STATUS, StaticBean.YES))
    	.and(new RTCnd(EventSanctionEntity.OPERATOR_TIME, date+" 00:00:00")).and(new LTCnd(EventSanctionEntity.OPERATOR_TIME, date+" 23:59:59")));
    	for(EventSanctionEntity san : sanctionList){
    		WorkerAchievementDayEntity workerCount = null;
			if(map.containsKey(san.getUserId())){
				workerCount = map.get(san.getUserId());
			}else{
				workerCount = new WorkerAchievementDayEntity(jdbcDao,san.getUserId(),countDay);
				map.put(san.getUserId(), workerCount);
			}
			
			if(san.getType() == EventSanctionEntity.SANCTION_TYPE_REWARD){
				workerCount.setBounty(workerCount.getBounty() + san.getMoney());
			}else if(san.getType() == EventSanctionEntity.SANCTION_TYPE_PENALTY){
				workerCount.setFines(workerCount.getFines() + san.getMoney());
			}
			
    	}
    	
    	
    	
    	if(map.size() > 0){
    		Iterator<Long> iterator = map.keySet().iterator();
    		while(iterator.hasNext()){
    			map.get(iterator.next()).insert();
    		}
    	}
    	
    	
    	
    	
    	jdbcDao.commit();
	}
    

    /**
     * 关闭线程
     * 
     * @return
     * @author chenwen 2015-1-22
     */
    public static boolean closeThread()
    {
        isRun = false;
        return isRun;
    }

    /**
     * 启动线程
     * 
     * @author chenwen 2015-1-22
     */
    public synchronized static void startThread()
    {
        if (!isRun)
        {
        	isRun = true;
            new Thread(new WorkerAchievementCountTask()).start();
        }
    }

}
