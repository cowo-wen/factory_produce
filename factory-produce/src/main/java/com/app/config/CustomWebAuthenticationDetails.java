/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-15
 */
package com.app.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 功能说明：
 * @author chenwen 2017-8-15
 *
 */
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    /**
     * 
     */
    private static final long serialVersionUID = 6975601077710753878L;
    private final String token;
    private final String code;
    
    

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        token = request.getParameter("token");
        code = request.getParameter("code");
    }

    public String getToken() {
        return token;
    }
    
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("; Token: ").append(this.getToken()).append("; code: ").append(this.getCode());
        return sb.toString();
    }
}
