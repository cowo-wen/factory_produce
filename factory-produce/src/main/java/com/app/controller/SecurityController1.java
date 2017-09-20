package com.app.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.bean.Msg;

/**
 * Created by sang on 2017/1/10.
 */
@RestController
@RequestMapping("/v1")
public class SecurityController1
{
    @RequestMapping(method=RequestMethod.GET,value="/home")
    public String home(Model model)
    {
        Msg msg = new Msg("认证1", "测试内容home", "额外信息，只对管理员显示");
        model.addAttribute("msg", msg);
        return "index";
    }

    @RequestMapping(method=RequestMethod.GET,value="/index")
    public String index(Model model)
    {
        Msg msg = new Msg("认证1", "测试内容index", "额外信息，只对管理员显示");
        model.addAttribute("msg", msg);
        return "index";
    }
}
