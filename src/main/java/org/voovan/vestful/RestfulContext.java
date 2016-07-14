package org.voovan.vestful;

import org.voovan.http.server.HttpServer;
import org.voovan.vestful.dto.ClassElement;
import org.voovan.vestful.dto.MethodElement;
import org.voovan.vestful.handler.ClassDescRouter;
import org.voovan.vestful.handler.RestfulRouter;
import org.voovan.vestful.handler.MethodDescRouter;
import org.voovan.tools.TFile;
import org.voovan.tools.TObject;
import org.voovan.tools.TReflect;
import org.voovan.tools.json.JSON;
import org.voovan.tools.log.Logger;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Restful 上下文类
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class RestfulContext {

    /**
     * 读取 Restful 接口的配置文件
     * @return List<ClassElement> IntefaceConfig对象的列表
     */
    private static List<ClassElement> loadConfig() {

        List<ClassElement> classElements = new ArrayList<ClassElement>();
        try {
            byte[] fileContent = TFile.loadFileFromContextPath("/conf/vestful.json");
            List<Map<String, Object>> classConfigs = TObject.cast(JSON.parse(new String(fileContent, "UTF-8")));
            for (Map<String, Object> classConfig : classConfigs) {
                //通过反射构造ClassElement 元素
                ClassElement classElement = TObject.cast(TReflect.getObjectFromMap(ClassElement.class, classConfig, true));
                //填充方法信息
                classElement.fillMethodInfo();
                //增加类元素到 List
                classElements.add(classElement);
            }
        }catch(UnsupportedEncodingException | ParseException | ReflectiveOperationException e){
            Logger.error("Load /conf/vestful.json file error.",e);
        }
        return classElements;
    }

    /**
     * 初始化 Restful 的接口到 HttpServer 对象
     * @param server HttpServer 对象
     */
    public static void initWithConfig(HttpServer server) {
        List<ClassElement> classElemenets = loadConfig();
        for (ClassElement classElemenet : classElemenets) {
            String route = classElemenet.getRoute();
            for(MethodElement methodElement : classElemenet.getMethodElements()) {
                //增加路由控制
                String httpRoutePath = route+"/"+methodElement.getName();
                server.otherMethod(methodElement.getHttpMethod(), httpRoutePath, new RestfulRouter(httpRoutePath,methodElement));
                server.otherMethod("GET", httpRoutePath+"!", new MethodDescRouter(methodElement));
            }
            server.otherMethod("GET", route+"!", new ClassDescRouter(classElemenet));
        }
    }
}
