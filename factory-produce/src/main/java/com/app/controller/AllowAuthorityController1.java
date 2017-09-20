package com.app.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.bean.Msg;
import com.app.bean.RequestLoginBean;
import com.app.dao.UserMapper;
import com.app.entity.sys.SysUserEntity;
import com.app.service.StudentService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by sang on 2017/1/10.
 * 
 * @RestController 直接返回数据 restful
 * @Controller 返回视图模板 
 */
@RestController
@RequestMapping("/allow1")
public class AllowAuthorityController1 {
    public static Log logger = LogFactory.getLog(AllowAuthorityController1.class);
    @Autowired
    private StudentService studentService;
    
    @Autowired
    UserMapper userMapper;
    
    @RequestMapping(method=RequestMethod.GET,value="/home")
    public String index(Model model) {
        Msg msg = new Msg("允许访问1", "测试内容2", "额外信息，只对管理员显示");
        model.addAttribute("msg", msg);
        List<SysUserEntity> list = studentService.getList();
        SysUserEntity user= userMapper.findUserByName("ssd");
        logger.error("----------------"+user.getPassword());
        return new Gson().toJson(list);
    }
    
    @RequestMapping(method=RequestMethod.POST,value="/post")
    public String post(Model model) {
        Msg msg = new Msg("允许访问1", "测试内容2", "额外信息，只对管理员显示");
        model.addAttribute("msg", msg);
        
        return "{'age':123,'name':'"+msg+"'}";
    }
    
    /** 
     * 通过post请求去登陆 
     *  
     * @param name 
     * @param pwd
     * required 表示是否可以为空
     * @return 
     */  
    @RequestMapping(value = "/loginbypost", method = RequestMethod.POST)  
    public String loginByPost(@RequestParam(value = "name", required = true) String name,  
            @RequestParam(value = "pwd", required = true) String pwd) {  
        System.out.println("hello post");  
        return login4Return(name, pwd);  
    }
    
    /** 
     * 参数为一个bean对象.spring会自动为我们关联映射 
     * @param loginBean 
     * @return 
     */  
    @RequestMapping(value = "/loginbypost1", method = { RequestMethod.POST, RequestMethod.GET })  
    public String loginByPost1(RequestLoginBean loginBean) {  
        if (null != loginBean) {  
            return login4Return(loginBean.getName(), loginBean.getPwd());  
        } else {  
            return "error";  
        }  
    }  
    
    /** 
     * 对登录做出响应处理的方法 
     *  
     * @param name 
     *            用户名 
     * @param pwd 
     *            密码 
     * @return 返回处理结果 
     */  
    private String login4Return(String name, String pwd) {
        
        JsonObject jo = new JsonObject();
        jo.addProperty("name", name);
        jo.addProperty("PWD", pwd);
        return jo.toString();  
    }  
}
