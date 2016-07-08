package org.voovan.restful.handler;

import org.voovan.http.server.HttpBizHandler;
import org.voovan.http.server.HttpRequest;
import org.voovan.http.server.HttpResponse;
import org.voovan.restful.dto.ClassElement;
import org.voovan.tools.TFile;
import org.voovan.tools.json.JSON;

/**
 * Restful 类文档处理类
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class ClassDescHandler implements HttpBizHandler {
    private String routePath;
    private ClassElement classElement;
    private String htmlContent;

    /**
     * 构造函数
     * @param classElement ClassElement 对象
     */
    public ClassDescHandler(ClassElement classElement){
        this.routePath = routePath;
        this.classElement = classElement;
        htmlContent = new String(TFile.loadResource("ClassDesc.html"));
    }

    @Override
    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        String methodData = JSON.toJSON(classElement).replace("\"class ", "\"");
        htmlContent =  htmlContent.replaceAll(":classData",methodData);
        httpResponse.body().write(htmlContent);
    }

}
