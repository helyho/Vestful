package org.voovan.vestful.dto;

import org.voovan.tools.TReflect;
import org.voovan.tools.TString;
import org.voovan.tools.json.annotation.NotJSON;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 类文字命名
 *
 * @author helyho
 *         <p>
 *         Restful Framework.
 *         WebSite: https://github.com/helyho/Vestful
 *         Licence: Apache v2 License
 */
public class MethodElement {
    private String route;
    private String name;
    private String desc;
    private String httpMethod;
    private String returnModel;
    private List<ParamElement> paramElements;

    @NotJSON
    private Method method;
    @NotJSON
    private Object object;

    public MethodElement(String route, String name, String desc, Method method, String httpMethod){
        this.route = route.endsWith("/") ? TString.removeSuffix(route) : route;
        this.route+="/"+name;
        this.name = name;
        this.desc = desc;
        this.method = method;
        this.httpMethod = httpMethod;
        paramElements = new ArrayList<ParamElement>();
        try {
            object = TReflect.newInstance(method.getDeclaringClass());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        returnModel = TReflect.getClazzJSONModel(method.getReturnType()).replace("\"","\'");
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getReturnModel() {
        return returnModel;
    }

    public List<ParamElement> getParamElements() {
        return paramElements;
    }

    public void addParamElements(ParamElement paramElement) {
        this.paramElements.add(paramElement);
    }

    public Method getMethod() {
        return method;
    }

    public Object methodInvoke(Object ...args) throws ReflectiveOperationException {
        return TReflect.invokeMethod(object, method, args);
    }

    public enum ElementType{
        METHOD,PARAM
    }
}
