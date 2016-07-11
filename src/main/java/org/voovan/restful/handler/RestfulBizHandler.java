package org.voovan.restful.handler;

import org.voovan.http.server.*;
import org.voovan.restful.dto.Error;
import org.voovan.restful.dto.MethodElement;
import org.voovan.restful.dto.ParamElement;
import org.voovan.tools.TObject;
import org.voovan.tools.TReflect;
import org.voovan.tools.TString;
import org.voovan.tools.json.JSON;

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
public class RestfulBizHandler implements HttpBizHandler {
    private String routePath;
    private MethodElement methodElement;

    /**
     * 构造函数
     * @param methodElement methodElement 对象
     */
    public RestfulBizHandler(String routePath, MethodElement methodElement){
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
            if(canFetctPathVariables(requestPath,routePath)){
                String paramPath = getParamPath(routePath);
                Map<String,String> params = HttpDispatcher.fetchPathVariables(requestPath,paramPath);
                httpRequest.getParameters().putAll(params);
            }

            //通过 Request 中的参数数量调用指定的方法
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
                    Object paramValue = TString.toObject(value,paramElements.get(i).getClazz());
                    methodParams[i] = paramValue;
                }

                //调用指定的方法
                Object methodResult = methodElement.methodInvoke(methodParams);
                if(methodResult.getClass().getName().startsWith("java.lang")){
                    result = methodResult.toString();
                } else {
                    result = JSON.toJSON(methodResult);
                }
            }else{
                throw new RestfulException("need "+paramElements.size()+" params. got "+
                                            httpRequest.getParameters().size()+" params");
            }
        }catch(Exception e){
            String message = e.getMessage();
            message = message.replace("\\","\\\\");
            message =  message.replace("\"","\\\"");
            if(!(e instanceof RestfulException) ){
                message = e.getClass().getName()+" "+message;
            }
            result = Error.newInstance("URL ["+requestPath+"] error. Message:" + message).toString();
        }

        httpResponse.body().write(result);
    }

    /**
     * 是否能从路径中取参数
     * @param requestPath  请求路径
     * @param routePath    匹配路径
     * @return
     */
    public boolean canFetctPathVariables(String requestPath,String routePath){
        boolean matchRouteIgnoreCase = WebContext.getWebServerConfig().isMatchRouteIgnoreCase();
        if(HttpDispatcher.matchPath(requestPath,getParamPath(routePath),matchRouteIgnoreCase)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取参数路径
     * @param routePath
     * @return
     */
    public String getParamPath(String routePath){
        StringBuilder matchRoute = new StringBuilder(routePath);
        for (ParamElement paramElement : methodElement.getParamElements()) {
            matchRoute.append("/:");
            matchRoute.append(paramElement.getName());
        }
        return matchRoute.toString();
    }
}
