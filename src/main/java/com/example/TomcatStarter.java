package com.example;

import com.example.servlet.DemoServlet;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;

public class TomcatStarter {
    private int port = 8080;
    private static final String contextPath = "/helloweb";

    public void start() throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);

        URL url = getClass().getClassLoader().getResource("spring-servlet.xml");
        String pwd = StringUtils.substringBefore(url.getPath(), "/target/classes");
        // tomcat.setBaseDir(pwd);
        StringBuilder webAppBuilder = new StringBuilder();
        webAppBuilder.append(pwd).append(File.separator).append("webapp");
        String webapp = webAppBuilder.toString();

        Context context = tomcat.addWebapp(contextPath, webapp);
        WebResourceRoot resources = new StandardRoot(context);
        // 如果添加jar包，则webAppMount为 /WEB-INF/lib
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", pwd + "/target/classes", "/"));
        context.setResources(resources);
        context.addLifecycleListener(e -> {
            System.out.format("\33[32;4m" +
                    String.format("event type: %s, lifecycle state: %s", e.getType(), e.getLifecycle().getStateName())
            + "%n");
        });

        // 注册servelet
        tomcat.addServlet(contextPath, "demoServlet", new DemoServlet());
        // servlet mapping
        context.addServletMappingDecoded("/demo.do", "demoServlet");

        tomcat.enableNaming();
        tomcat.getConnector();
        tomcat.start();
        tomcat.getServer().await();
    }

    public static void main(String[] args) throws Exception {
        TomcatStarter starter = new TomcatStarter();
        starter.start();
    }
}
