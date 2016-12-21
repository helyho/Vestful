package org.voovan.vestful.handler;

import org.voovan.http.server.*;
import org.voovan.http.server.context.WebContext;
import org.voovan.vestful.RestfulContext;
import org.voovan.vestful.dto.Error;
import org.voovan.vestful.exception.RestfulException;
import org.voovan.vestful.dto.MethodElement;
import org.voovan.vestful.dto.ParamElement;
import org.voovan.tools.TString;
import org.voovan.tools.json.JSON;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Restful 接口处理类
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class RestfulRouter implements HttpRouter {
    private String routePath;
    private MethodElement methodElement;

    /**
     * 构造函数
     * @param methodElement methodElement 对象
     */
    public RestfulRouter(String routePath, MethodElement methodElement){
        this.routePath = routePath;
        this.methodElement = methodElement;
    }

    @Override
    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        String result = "";
        String requestPath = httpRequest.protocol().getPath();

        try{
            List<ParamElement> paramElements = methodElement.getParamElements();

            //取 URL 中的参数变量
            String paramPath = canFetctPathVariables(requestPath,routePath);
            if(paramPath!=null){
                Map<String,String> params = HttpDispatcher.fetchPathVariables(requestPath,paramPath);
                httpRequest.getParameters().putAll(params);
            }

            //通过 Request 中的参数数量确定方法是否可以调用
            if(httpRequest.getParameters().size() >= paramElements.size()){

                //将 HTTP 请求中的参数,转换为参数数组
                Object[] methodParams = new Object[paramElements.size()];
                for(int i=0;i<paramElements.size();i++){
                    ParamElement paramElement = paramElements.get(i);
                    String value = httpRequest.getParameter(paramElement.getName());
                    if(value==null){
                        //未找到命名参数异常
                        throw new RestfulException("not found param named by "+paramElement.getName()+". ");
                    }
                    try {
                        //传入的"null"字符串转换成 null 对象
                        Object paramValue = null;
                        if(!value.equals("null")) {

                            //如果是数组,则将参数转换成数组形式
                            if(paramElement.getClazz().isArray() && TString.searchByRegex(value,"^\\s*\\[[\\s\\S]*\\]\\s*$").length == 0) {
                                value="["+value+"]";
                            }

                            //转换参数为指定 Java 类型
                            paramValue = TString.toObject(value, paramElement.getClazz());
                        }

                        //填充参数数组
                        methodParams[i] = paramValue;
                    }catch(Exception e){
                        throw new RestfulException("Convert param named by [" + paramElement.getName()+"] " +
                                "to type ["+paramElement.getClazz()+"] error: "+e.getMessage());
                    }
                }

                //调用指定的方法
                Object methodResult = methodElement.methodInvoke(methodParams);
                if(methodResult!=null && methodResult.getClass().getName().startsWith("java.lang")){
                    result = methodResult.toString();
                } else {
                    result = JSON.toJSON(methodResult);
                }
            }else{
                throw new RestfulException("need "+paramElements.size()+" params. got "+
                                            httpRequest.getParameters().size()+" params");
            }
        }catch(Exception e){

            //如果是反射的异常类型,取出真实的异常
            if(e instanceof InvocationTargetException){
                Throwable throwable = e.getCause();
                e = new Exception(e.getCause());
            }

            //对RestfulException异常的特殊处理
            if(e instanceof RestfulException){
                RestfulException restfulException = (RestfulException)e;
                httpResponse.protocol().setStatus(restfulException.getHttpStatusCode());
                httpResponse.protocol().setStatusCode(restfulException.getHttpStatusDesc());
            }else {
                httpResponse.protocol().setStatus(500);
                httpResponse.protocol().setStatusCode("Invoke Error");
            }


            String message = e.getMessage();
            if(message!=null) {
                message = message.replace("\\", "\\\\");
                message = message.replace("\"", "\\\"");
            }
            if(!(e instanceof RestfulException) ){
                message = message +", ErrorClass:["+e.getClass().getName()+"]";
            }
            result = Error.newInstance("URL: "+requestPath+"\r\nMessage: " + message).toString();
        }

        httpResponse.body().write(result);
    }

    /**
     * 是否能从路径中取参数
     * @param requestPath  请求路径
     * @param routePath    匹配路径
     * @return 获取路径变量
     */
    public String canFetctPathVariables(String requestPath,String routePath){
        boolean matchRouteIgnoreCase = WebContext.getWebServerConfig().isMatchRouteIgnoreCase();
        String paramPath = RestfulContext.getParamPath(routePath,methodElement);
        if(HttpDispatcher.matchPath(requestPath, paramPath,matchRouteIgnoreCase)){
            return paramPath;
        }else{
            return null;
        }
    }


}
