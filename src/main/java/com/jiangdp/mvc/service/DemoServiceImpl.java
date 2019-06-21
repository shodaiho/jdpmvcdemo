package com.jiangdp.mvc.service;

import com.jiangdp.mvc.framework.annotation.JDPService;

/**
 * DemoServiceImpl
 * <p>
 * Created by morningrain on 2019/6/21.
 */
@JDPService
public class DemoServiceImpl implements DemoService {
    public String query(String name) {
        return "DemoServiceImpl Query Result By " + name;
    }
}
