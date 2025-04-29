package com.xjx.example.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseServlet extends HttpServlet {

    /**
     * 重写service方法以实现基于URI的动态方法调用
     *
     * @param req 用于获取请求信息的HttpServletRequest对象
     * @param resp 用于向客户端发送响应的HttpServletResponse对象
     * @throws ServletException 如果Servlet遇到异常
     * @throws IOException 如果发生输入或输出异常
     */
    private static final Logger logger = Logger.getLogger(BaseServlet.class.getName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取请求的统一资源标识符URI
        String uri = req.getRequestURI();
        // 查找URI中最后一个'/'的索引，用于确定方法名的起始位置
        int index = uri.lastIndexOf('/');
        // 提取URI中最后一个'/'之后的部分作为方法名
        String methodName = uri.substring(index + 1);
        // 获取当前实例的类，用于反射调用方法
        Class<? extends BaseServlet> cls = this.getClass();
        try {
            // 通过反射获取与提取出的方法名对应的方法对象
            Method method = cls.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            // 使用反射调用方法，并传入请求和响应对象作为参数
            method.invoke(this, req, resp);
        } catch (NoSuchMethodException e) {
            // 如果找不到对应的方法，抛出运行时异常
            logger.log(Level.SEVERE, "Method not found: " + methodName, e);
            System.out.println("null");
            throw new RuntimeException(e);

        } catch (InvocationTargetException e) {
            // 如果方法调用目标对象上抛出了异常，抛出运行时异常
            logger.log(Level.SEVERE, "Error invoking method: " + methodName, e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // 如果访问方法出现非法访问错误，抛出运行时异常
            logger.log(Level.SEVERE, "Illegal access to method: " + methodName, e);
            throw new RuntimeException(e);
        }
    }

}
