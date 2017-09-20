package com.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.bean.Msg;

/**
 * Created by sang on 2017/1/10.
 */
@Controller
@RequestMapping("/allow2")
public class AllowAuthorityController2 {
    
    /**
     * 返回视图的形式
     * @param model
     * @return
     * @author chenwen 2017-8-14
     */
    @RequestMapping("/view")
    public String index(Model model) {
        Msg msg = new Msg("允许访问1", "测试内容2", "额外信息，只对管理员显示");
        model.addAttribute("msg", msg);
        return "index";
    }
    
    /**
     * 返回json的形式
     * @param model
     * @return
     * @author chenwen 2017-8-14
     */
    @RequestMapping("/json")
    @ResponseBody
    public Msg json(Model model) {
        Msg msg = new Msg("允许访问1", "测试内容2", "额外信息，只对管理员显示");
        model.addAttribute("msg", msg);
        return msg;
    }
}
