package org.voovan.vestful.handler;

import org.voovan.http.server.*;
import org.voovan.vestful.RestfulContext;
import org.voovan.vestful.dto.Error;
import org.voovan.vestful.dto.MethodElement;
import org.voovan.vestful.dto.ParamElement;
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
