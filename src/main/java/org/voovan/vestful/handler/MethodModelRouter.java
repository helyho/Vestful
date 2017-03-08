package org.voovan.vestful.handler;

import org.voovan.http.server.HttpRequest;
import org.voovan.http.server.HttpResponse;
import org.voovan.http.server.HttpRouter;
import org.voovan.tools.json.JSON;
import org.voovan.vestful.dto.MethodElement;

/**
 * Restful 方法文理类
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class MethodModelRouter implements HttpRouter {
    private MethodElement methodElement;
    private String htmlContent;

    /**
     * 构造函数
     *
     * @param methodElement methodElement 对象
     */
    public MethodModelRouter(MethodElement methodElement) {
        this.methodElement = methodElement;
    }

    @Override
    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        String methodData = JSON.toJSON(methodElement).replace("\"class ", "\"");
        httpResponse.body().write(methodData);
    }

}
