package org.voovan.vestful.dto;

import org.voovan.vestful.annotation.Param;
import org.voovan.vestful.annotation.Restful;
import org.voovan.tools.TObject;
import org.voovan.tools.TReflect;
import org.voovan.tools.TString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

/**
 * 类文字命名
 *
 * @author helyho
 *         <p>
 *         Restful Framework.
 *         WebSite: https://github.com/helyho/Vestful
 *         Licence: Apache v2 License
 */
public class ClassElement {
    private String name;
    private String route;
    private String classPath;
    private String desc;
    private List<MethodElement> methodElements;

    public List<MethodElement> getMethodElements() {
        return methodElements;
    }

    public String getName() {
        return name;
    }

    public String getRoute() {

        if(route==null || route.isEmpty()){
            route = "/"+classPath.substring(classPath.lastIndexOf(".")+1);
        }

        if (route.endsWith("/")){
            route = TString.removeSuffix(route);
        }

        route = route.replace('\\','/');

        return route;
    }

    public String getClassPath() {
        return classPath;
    }

    public String getDesc() {
        return desc;
    }


    public void fillMethodInfo() throws ClassNotFoundException {
        this.methodElements = getRestfulMethodInfo(this.classPath);
    }

    private List<MethodElement> getRestfulMethodInfo(String clazzName) throws ClassNotFoundException {
        List<MethodElement> elementsInClass = new Vector<MethodElement>();
        Class clazz = Class.forName(clazzName);
        Method[] methods = TReflect.getMethods(clazz);
        for(Method method : methods){
            Restful restfulAnnotation = TObject.cast(method.getAnnotation(Restful.class));
            if(restfulAnnotation!=null){
                MethodElement methodElement = new MethodElement(this.route,method.getName(),restfulAnnotation.desc(), method,restfulAnnotation.method());
                Class<?>[]  paramTypes = method.getParameterTypes();
                Annotation[][] annotationArray = method.getParameterAnnotations();
                for(int i=0; i<paramTypes.length; i++){
                    Annotation[] annotations = annotationArray[i];
                    Class<?> paramClazz = paramTypes[i];
                    for(Annotation annocation : annotations){
                        if(annocation instanceof Param){
                            Param paramAnnocation = TObject.cast(annocation);
                            ParamElement paramElement = new ParamElement(paramAnnocation.name(),paramAnnocation.desc(),paramClazz);
                            methodElement.addParamElements(paramElement);
                        }
                    }
                }
                elementsInClass.add(methodElement);
            }else{
                continue;
            }
        }
        return elementsInClass;
    }
}
