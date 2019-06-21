package com.jiangdp.mvc.controller;

import com.jiangdp.mvc.framework.annotation.JDPAutowired;
import com.jiangdp.mvc.framework.annotation.JDPController;
import com.jiangdp.mvc.framework.annotation.JDPRequestMapping;
import com.jiangdp.mvc.framework.annotation.JDPRequestParam;
import com.jiangdp.mvc.service.DemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DemoController
 * <p>
 * Created by morningrain on 2019/6/21.
 */
@JDPController
@JDPRequestMapping("/demo")
public class DemoController {

    @JDPAutowired
    private DemoService demoService;

    @JDPRequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp,
                      @JDPRequestParam("name") String name) {

        String queryResult = demoService.query(name);

        try {
            resp.getWriter().write(queryResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @JDPRequestMapping("/test")
    public void test(HttpServletRequest req, HttpServletResponse resp,
                     @JDPRequestParam("name") String name) {
        try {
            resp.getWriter().write("/demo/test?" + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
