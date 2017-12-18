package com.app.controller.v1.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.app.dao.sql.cnd.LikeCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.sys.SysUserBindingInfo;
import com.app.util.NetworkUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：用户绑定管理
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/sys/user_binding")
@Scope("prototype")//设置成多例
public class UserBindingAPI extends Result{
    public static Log logger = LogFactory.getLog(UserBindingAPI.class);
    
    @Autowired  
    private HttpServletRequest request;
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) {
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(SysUserBindingInfo.USER_ID));
    	int iDisplayStart = 0;// 起始  
    	int iDisplayLength = 10;// size 
    	int sEcho = 0;
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(SysUserBindingInfo.NICKNAME)){  
            	sql.and(new LikeCnd(SysUserBindingInfo.NICKNAME,jsonObject.get(VALUE).getAsString()));
            }
    	}
    	
    	SysUserBindingInfo entity = new SysUserBindingInfo(jdbcDao);
    	entity.outPutOther(SysUserBindingInfo.USER_NAME);
    	List<SysUserBindingInfo> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
    	long count = entity.getCount(sql);
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", count);
    	map.put("iTotalDisplayRecords", count);
        return success(map);
    }
    
   
    
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id,@RequestParam String application_code) {
    	SysUserBindingInfo entity = new SysUserBindingInfo(jdbcDao);
    	entity.setUserBindingInfoId(id);
    	entity.loadVo();
    	try {
    		insertLog(application_code,id,NetworkUtil.getIpAddress(request),getLoginUser().getUserName()+"-解除-"+entity.getUserId()+"-"+entity.getType()+"-"+entity.getOpenId());//插入日志
			entity.delete();
			return success("解除绑定成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("解除绑定", e);
			return error("解除绑定"+e.getMessage());
		}
    	
    }
    
   
    
    
    
    
}
