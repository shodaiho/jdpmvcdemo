package com.jiangdp.mvc.framework.servlet;

import com.jiangdp.mvc.framework.annotation.JDPAutowired;
import com.jiangdp.mvc.framework.annotation.JDPController;
import com.jiangdp.mvc.framework.annotation.JDPRequestMapping;
import com.jiangdp.mvc.framework.annotation.JDPService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * JDPDispatcherServlet
 * <p>
 * Created by morningrain on 2019/6/21.
 */
public class JDPDispatcherServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private List<String> classList = new ArrayList<String>();

    private Map<String, Object> ioc = new HashMap<String, Object>();

    private Map<String, Method> handlerMapping = new HashMap<String, Method>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 6、运行阶段，通过分发，调用对应的方法
        try {
            doDisPatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        // 1、加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        // 2、扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));

        // 3、初始化相关的类，并放入IOC容器
        doInstance();

        // 4.完成依赖注入
        doDI();

        // 5.初始化HandlerMapping对象
        initHandlerMapping();

        System.out.println("JDP 手写SpringMVC 启动完毕");

    }

    private void doLoadConfig(String contextLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextLocation);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());

        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class", "");
                classList.add(className);
            }
        }
    }

    private void doInstance() {
        if (classList.isEmpty()) {
            return;
        }
        for (String className : classList) {
            try {
                Class clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(JDPController.class)) {
                    Object instance = clazz.newInstance();
                    ioc.put(toLowerFirstChar(clazz.getSimpleName()), instance);
                } else if (clazz.isAnnotationPresent(JDPService.class)) {
                    Object instance = clazz.newInstance();
                    ioc.put(toLowerFirstChar(clazz.getSimpleName()), instance);


                    for (Class aClass : clazz.getInterfaces()) {
                        if (ioc.containsKey(aClass.getName())) {
                            throw new Exception("this Bean is Exist");
                        }
                        ioc.put(aClass.getName(), instance);
                    }
                } else {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // 依赖注入
    private void doDI() {
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                String className = field.getType().getName();
                if (field.isAnnotationPresent(JDPAutowired.class)) {
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(), ioc.get(className));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(JDPController.class)) continue;

            String classBaseUrl = "";
            if (clazz.isAnnotationPresent(JDPRequestMapping.class)) {
                JDPRequestMapping jdpRequestMapping = (JDPRequestMapping) clazz.getAnnotation(JDPRequestMapping.class);
                classBaseUrl = jdpRequestMapping.value();
            }

            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(JDPRequestMapping.class)) {
                    JDPRequestMapping methodValue = method.getAnnotation(JDPRequestMapping.class);
                    String url = (classBaseUrl + methodValue.value()).replaceAll("/+", "/");
                    handlerMapping.put(url, method);
                    System.out.println(url);
                }
            }
        }
    }


    private void doDisPatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        String uri = req.getRequestURI();
        uri = uri.replace(req.getContextPath(), "").replaceAll("/+", "/");

        if (!handlerMapping.containsKey(uri)) {
            resp.getWriter().write("404 method is not exist!");
            return;
        } else {
            Method method = this.handlerMapping.get(uri);

            Map<String, String[]> params = req.getParameterMap();
            String beanName = toLowerFirstChar(method.getDeclaringClass().getSimpleName());
            method.invoke(ioc.get(beanName), new Object[]{req, resp, params.get("name")[0]});

        }
    }

    private String toLowerFirstChar(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
