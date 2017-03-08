package org.voovan.vestful.dto;

import org.voovan.tools.TString;
import org.voovan.tools.json.annotation.NotJSON;
import org.voovan.tools.reflect.TReflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 方法描述对象
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

    public MethodElement(String route, String name, String desc, Method method, String httpMethod){
        this.route = route.endsWith("/") ? TString.removeSuffix(route) : route;
        this.route+="/"+name;
        this.name = name;
        this.desc = desc;
        this.method = method;
        this.httpMethod = httpMethod;
        paramElements = new ArrayList<ParamElement>();
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

    public Object methodInvoke(Object ...args) throws Exception {
        return TReflect.invokeMethod(null, method, args);
    }

    public enum ElementType{
        METHOD,PARAM
    }
}
