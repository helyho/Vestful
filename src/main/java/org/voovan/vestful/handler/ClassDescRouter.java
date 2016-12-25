package org.voovan.vestful.handler;

import org.voovan.http.server.HttpRequest;
import org.voovan.http.server.HttpResponse;
import org.voovan.http.server.HttpRouter;
import org.voovan.tools.TFile;
import org.voovan.tools.json.JSON;
import org.voovan.tools.log.Logger;
import org.voovan.vestful.dto.ClassElement;

import java.io.UnsupportedEncodingException;

/**
 * Restful 类文档处理类
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class ClassDescRouter implements HttpRouter {
    private ClassElement classElement;
    private String htmlContent;

    /**
     * 构造函数
     * @param classElement ClassElement 对象
     */
    public ClassDescRouter(ClassElement classElement) {
        this.classElement = classElement;
        try {
            this.htmlContent = new String(TFile.loadResource("ClassDesc.html"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.error("Load ClassDesc template error.", e);
        }
    }

    @Override
    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        String methodData = JSON.toJSON(classElement).replace("\"class ", "\"");
        //替换类的描述 JSON 到页面
        htmlContent =  htmlContent.replace(":classData",methodData);
        httpResponse.body().write(htmlContent);
    }

}
