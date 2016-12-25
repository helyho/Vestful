package org.voovan.vestful;

import org.voovan.http.server.HttpModule;
import org.voovan.http.server.WebServer;
import org.voovan.tools.TFile;
import org.voovan.tools.TObject;
import org.voovan.tools.TString;
import org.voovan.tools.json.JSON;
import org.voovan.tools.log.Logger;
import org.voovan.tools.reflect.TReflect;
import org.voovan.vestful.dto.ClassElement;
import org.voovan.vestful.dto.MethodElement;
import org.voovan.vestful.dto.ParamElement;
import org.voovan.vestful.handler.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Restful 上下文类
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class RestfulContext extends HttpModule{

    public static void addClassConfig(List<Map<String, Object>> classConfigs, String name, String route, String classPath, String desc){
        classConfigs.add(TObject.newMap("name",name,"route",route,"classPath",classPath,"desc",desc));
    }

    /**
     * 读取 Restful 接口的配置文件
     * @return List<ClassElement> IntefaceConfig对象的列表
     */
    private static List<ClassElement> loadConfig() {

        List<ClassElement> classElements = new ArrayList<ClassElement>();
        try {
            byte[] fileContent = TFile.loadFileFromContextPath("conf/vestful.json");
            List<Map<String, Object>> classConfigs = TObject.cast(JSON.parse(new String(fileContent, "UTF-8")));
            //从配置中读取 restful 配置的 class
            for (Map<String, Object> classConfig : classConfigs) {
                //通过反射构造ClassElement 元素
                ClassElement classElement = TObject.cast(TReflect.getObjectFromMap(ClassElement.class, classConfig, true));
                //填充方法信息
                classElement.getMethodElement();
                //增加类元素到 List
                classElements.add(classElement);

                //通过params配置使用对应的 set 方法设置参数
                Map<String,Object> paramsMap = (Map<String,Object>)classConfig.get("params");
                if(paramsMap!=null) {
                    for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                        try {
                            Class clazz = Class.forName(classConfig.get("classPath").toString());
                            //根据 Java 命名规范,首字母转换成大写的
                            String methodName = TString.uppercaseFirstChar(entry.getKey().toString());
                            TReflect.invokeMethod(clazz, "set" + methodName, entry.getValue());

                        } catch (Exception e) {
                        }
                    }
                }
            }
        }catch(Exception e) {
            Logger.error(e);
        }
        return classElements;
    }

    /**
     * 初始化 Restful 的接口到 HttpServer 对象
     * @param server HttpServer 对象
     */
    public static void installRestful(WebServer server) {
        List<ClassElement> classElemenets = loadConfig();
        for (ClassElement classElemenet : classElemenets) {
            String route = classElemenet.getRoute();
            for(MethodElement methodElement : classElemenet.getMethodElements()) {
                //增加路由控制
                String httpRoutePath = route+"/"+methodElement.getName();
                //注册通过 HTTP 的报文传递参数的路由
                server.otherMethod(methodElement.getHttpMethod(), httpRoutePath, new RestfulRouter(httpRoutePath,methodElement));
                //注册通过 url 来传递参数的路由
                server.otherMethod(methodElement.getHttpMethod(), getParamPath(httpRoutePath,methodElement), new RestfulRouter(httpRoutePath,methodElement));
                //注册说明页面路由
                server.otherMethod("GET", httpRoutePath+"!", new MethodDescRouter(methodElement));
                server.otherMethod("GET", httpRoutePath+"!!", new MethodModelRouter(methodElement));
            }

            server.otherMethod("GET", route+"!", new ClassDescRouter(classElemenet));
            server.otherMethod("GET", route+"!!", new ClassModelRouter(classElemenet));
        }
    }

    /**
     * 获取参数路径
     * @param routePath 路由路径
     * @return  参数路径
     */
    public static String getParamPath(String routePath,MethodElement methodElement){
        StringBuilder matchRoute = new StringBuilder(routePath);
        for (ParamElement paramElement : methodElement.getParamElements()) {
            matchRoute.append("/:");
            matchRoute.append(paramElement.getName());
        }
        return matchRoute.toString();
    }

    @Override
    public void install() {
        List<ClassElement> classElemenets = loadConfig();
        for (ClassElement classElemenet : classElemenets) {
            String route = classElemenet.getRoute();
            for(MethodElement methodElement : classElemenet.getMethodElements()) {
                //增加路由控制
                String httpRoutePath = route+"/"+methodElement.getName();
                otherMethod(methodElement.getHttpMethod(), httpRoutePath, new RestfulRouter(httpRoutePath,methodElement));
                otherMethod(methodElement.getHttpMethod(), getParamPath(httpRoutePath,methodElement), new RestfulRouter(httpRoutePath,methodElement));
                otherMethod("GET", httpRoutePath+"!", new MethodDescRouter(methodElement));
            }

            otherMethod("GET", route+"!", new ClassDescRouter(classElemenet));
        }
    }
}
