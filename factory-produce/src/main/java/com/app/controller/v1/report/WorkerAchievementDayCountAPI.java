package com.app.controller.v1.report;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.LTEQCnd;
import com.app.dao.sql.cnd.LikeCnd;
import com.app.dao.sql.cnd.RTEQCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.report.WorkerAchievementDayEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：日绩效统计
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/report/worker_day")
@Scope("prototype")
public class WorkerAchievementDayCountAPI extends Result{
    public static Log logger = LogFactory.getLog(WorkerAchievementDayCountAPI.class);
    
    @Autowired  
    private HttpServletRequest request;
    
    @Autowired  
    private HttpServletResponse response;
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) {
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(WorkerAchievementDayEntity.ID));
    	int iDisplayStart = 0;// 起始  
    	int iDisplayLength = 10;// size 
    	int sEcho = 0;
    	Date date = new Date();
    	String beiginTime = PublicMethod.formatDateStr(date, "yyyyMMdd");
    	String endTime = PublicMethod.formatDateStr(date,"yyyyMMdd");
    	
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(WorkerAchievementDayEntity.COUNT_DAY)){  
            	sql.and(new LikeCnd(WorkerAchievementDayEntity.COUNT_DAY,jsonObject.get(VALUE).getAsString()));
            }else if(jsonObject.get(NAME).getAsString().equals("beigin_time")){
            	beiginTime = jsonObject.get(VALUE).getAsString().replaceAll("-", "");
            }else if(jsonObject.get(NAME).getAsString().equals("end_time")){
            	endTime = jsonObject.get(VALUE).getAsString().replaceAll("-", "");
            }
    	}
    	sql.and(new RTEQCnd(WorkerAchievementDayEntity.COUNT_DAY, Integer.parseInt(beiginTime)));
    	sql.and(new LTEQCnd(WorkerAchievementDayEntity.COUNT_DAY, Integer.parseInt(endTime)));
    	
    	WorkerAchievementDayEntity entity = new WorkerAchievementDayEntity(jdbcDao);
    	entity.outPutOther(WorkerAchievementDayEntity.USER_NAME);
    	List<WorkerAchievementDayEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
    	long count = entity.getCount(sql);
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", count);
    	map.put("iTotalDisplayRecords", count);
        return success(map);
    }
    
    /**
     * 获取单个对象
     * @param id
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/vo/{id}")
    public String vo(@PathVariable("id") Long id) {
    	WorkerAchievementDayEntity entity = new WorkerAchievementDayEntity(jdbcDao);
    	entity.setId(id);
    	entity.loadVo();
        return success(entity);
    }
    
    
    /**
     * 导出数据
     * @param id
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/do_export")
    public String doExport(@RequestParam String aoData) {
    	
        OutputStream out = null;
        ByteArrayInputStream in = null;
        try
        {
        	
        	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
        	SQLWhere sql = new SQLWhere().orderBy(new DescSort(WorkerAchievementDayEntity.COUNT_DAY,WorkerAchievementDayEntity.ID));
        	Date date = new Date();
        	String beiginTime = PublicMethod.formatDateStr(date, "yyyyMMdd");
        	String endTime = PublicMethod.formatDateStr(date,"yyyyMMdd");
        	
        	if(jo.has("beigin_time")){
        		beiginTime = jo.get("beigin_time").getAsString().replaceAll("-", "");
        	}
        	
        	if(jo.has("end_time")){
        		endTime = jo.get("end_time").getAsString().replaceAll("-", "");
        	}
        	
        	
        	sql.and(new RTEQCnd(WorkerAchievementDayEntity.COUNT_DAY, Integer.parseInt(beiginTime)));
        	sql.and(new LTEQCnd(WorkerAchievementDayEntity.COUNT_DAY, Integer.parseInt(endTime)));
        	
        	WorkerAchievementDayEntity entity = new WorkerAchievementDayEntity(jdbcDao);
        	entity.outPutOther(WorkerAchievementDayEntity.USER_NAME);
        	List<WorkerAchievementDayEntity> list = entity.getListVO(sql);
        	String fileName = beiginTime+"-"+endTime+"日生产绩效.xls";
        	StringBuilder sb = new StringBuilder();
        	sb.append("员工姓名").append(StaticBean._T).append("日期").append(StaticBean._T).append("完成任务").append(StaticBean._T).append("超时任务").append(StaticBean._T).append("返工次数").append(StaticBean._T)
        	.append("生产收入").append(StaticBean._T).append("奖励收入").append(StaticBean._T).append("惩罚支出").append(StaticBean._T).append("总收入(元)").append(StaticBean._T).append(StaticBean._R).append(StaticBean._N);
        	
        	
        	for(WorkerAchievementDayEntity count : list){
        		sb.append(count.getUserName()).append(StaticBean._T).append(count.getCountDay()).append(StaticBean._T).append(count.getTaskTime()).append(StaticBean._T).append(count.getOvertime()).append(StaticBean._T).append(count.getReworkTime()).append(StaticBean._T)
            	.append(count.getProduceMoney()).append(StaticBean._T).append(count.getBounty()).append(StaticBean._T).append(count.getFines()).append(StaticBean._T).append(count.getProduceMoney()+count.getBounty()-count.getFines()).append(StaticBean._T).append(StaticBean._R).append(StaticBean._N);
            	
        	}
        	
            
                
                
            out = PublicMethod.assemblyHeader(request, response, fileName);
            in = new ByteArrayInputStream(sb.toString().getBytes(StaticBean.CHAR_CODE.gbk.toString()));
            int i = 0;
            byte[] buffer = new byte[1024];
            while ((i = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, i);
            }
            return success("成功");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return error("导出出错" + e.getMessage());
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                    out.flush();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
   
    
    
}
