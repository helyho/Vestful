package org.voovan.vestful.handler;

import org.voovan.http.server.HttpRequest;
import org.voovan.http.server.HttpResponse;
import org.voovan.http.server.HttpRouter;
import org.voovan.tools.TFile;
import org.voovan.tools.json.JSON;
import org.voovan.tools.log.Logger;
import org.voovan.vestful.dto.MethodElement;

import java.io.UnsupportedEncodingException;

/**
 * Restful 方法文档页面处理类
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class MethodDescRouter implements HttpRouter {
    private MethodElement methodElement;
    private String htmlContent;

    /**
     * 构造函数
     *
     * @param methodElement methodElement 对象
     */
    public MethodDescRouter(MethodElement methodElement) {
        this.methodElement = methodElement;
        try {
            this.htmlContent = new String(TFile.loadResource("MethodDesc.html"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.error("Load MethodDesc template error.", e);
        }
    }

    @Override
    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        String methodData = JSON.toJSON(methodElement).replace("\"class ", "\"");
        //替换方法的描述 JSON 到页面
        htmlContent =  htmlContent.replace(":methodData",methodData);
        httpResponse.body().write(htmlContent);
    }

}
