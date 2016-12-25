package org.voovan.vestful.dto;

import org.voovan.tools.TObject;
import org.voovan.tools.TString;
import org.voovan.tools.reflect.TReflect;
import org.voovan.vestful.annotation.Param;
import org.voovan.vestful.annotation.Restful;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Vector;

/**
 * 类元素模型
 *
 * @author helyho
 * <p>
 * Restful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
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


    public void getMethodElement() throws ClassNotFoundException {
        this.methodElements = getMethodElement(this.classPath);
    }

    private List<MethodElement> getMethodElement(String clazzName) throws ClassNotFoundException {
        List<MethodElement> elementsInClass = new Vector<MethodElement>();
        Class clazz = Class.forName(clazzName);
        Method[] methods = TReflect.getMethods(clazz);
        for(Method method : methods){

            //只暴露静态方法
            if(!Modifier.isStatic(method.getModifiers())){
                continue;
            }
            //读取方法的注解
            Restful restfulAnnotation = TObject.cast(method.getAnnotation(Restful.class));
            if(restfulAnnotation!=null){
                MethodElement methodElement = new MethodElement(this.route, method.getName(), restfulAnnotation.desc(),
                        method,restfulAnnotation.method());
                //获取方法参数类型定义
                Class<?>[]  paramTypes = method.getParameterTypes();
                //获取方法参数注解
                Annotation[][] annotationArray = method.getParameterAnnotations();

                //遍历方法参数
                for(int i=0; i<paramTypes.length; i++){
                    Annotation[] annotations = annotationArray[i];
                    Class<?> paramClazz = paramTypes[i];

                    if(annotations.length>0){
                        //遍历方法参数注解
                        for(Annotation annocation : annotations){
                            if(annocation instanceof Param){
                                Param paramAnnocation = TObject.cast(annocation);
                                //构造参数元素
                                ParamElement paramElement = new ParamElement(paramAnnocation.name(),paramAnnocation.desc(),paramClazz);
                                methodElement.addParamElements(paramElement);
                            }
                        }
                    } else {
                        //给没有注解的参数使用默认参数名
                        ParamElement paramElement = new ParamElement("arg"+(i+1),"",paramClazz);
                        methodElement.addParamElements(paramElement);
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
