package org.voovan.vestful.handler;

import org.voovan.http.server.HttpDispatcher;
import org.voovan.http.server.HttpRequest;
import org.voovan.http.server.HttpResponse;
import org.voovan.http.server.HttpRouter;
import org.voovan.http.server.context.WebContext;
import org.voovan.tools.TString;
import org.voovan.tools.json.JSON;
import org.voovan.vestful.RestfulModule;
import org.voovan.vestful.dto.Error;
import org.voovan.vestful.dto.MethodElement;
import org.voovan.vestful.dto.ParamElement;
import org.voovan.vestful.exception.RestfulException;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
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
                    value = TString.unConvertEscapeChar(value);
                    if(value==null){
                        //未找到命名参数异常
                        throw new RestfulException("not found param named by "+paramElement.getName()+". ");
                    }

                    try {
                        value = TString.fromUnicode(value);

                        //传入的"null"字符串转换成 null 对象
                        Object paramValue = null;
                        if(!"null".equals(value)) {

                            //如果是数组,则将参数转换成数组形式
                            if( (paramElement.getClazz().isArray() || paramElement.getClazz() == Collections.class) &&
                                    TString.searchByRegex(value,"^\\s*\\[[\\s\\S]*\\]\\s*$").length == 0) {
                                value="[\""+value+"\"]";
                            }

                            //转换参数为指定 Java 类型
                            paramValue = TString.toObject(value, paramElement.getClazz());
                        }

                        //填充参数数组
                        methodParams[i] = paramValue;
                    }catch(Exception e){
                        throw new RestfulException("Convert which param named by [" + paramElement.getName()+"] " +
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

            httpResponse.protocol().setStatus(500);
            httpResponse.protocol().setStatusCode("INVOKE_ERROR");

            //如果是反射的异常类型,取出真实的异常
            while(true) {
                if (e instanceof InvocationTargetException) {
                    Throwable throwable = e.getCause();
                    e = (Exception) throwable;

                    //对RestfulException异常的特殊处理
                    if (throwable instanceof RestfulException) {
                        RestfulException restfulException = (RestfulException) throwable;
                        httpResponse.protocol().setStatus(restfulException.getHttpStatusCode());
                        httpResponse.protocol().setStatusCode(restfulException.getHttpStatusDesc());
                    }
                } else {
                    break;
                }
            }

            e.printStackTrace();


            String message = e.getMessage();
            if(message!=null) {
                message = message.replace("\\", "\\\\");
                message = message.replace("\"", "\\\"");
            }

            result = Error.newInstance(requestPath, e.getClass().getName(), message).toString();
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
        String paramPath = RestfulModule.getParamPath(routePath,methodElement);
        if(HttpDispatcher.matchPath(requestPath, paramPath,matchRouteIgnoreCase)){
            return paramPath;
        }else{
            return null;
        }
    }


}
