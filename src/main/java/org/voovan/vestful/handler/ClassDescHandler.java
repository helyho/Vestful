package org.voovan.vestful.handler;

import org.voovan.http.server.HttpBizHandler;
import org.voovan.http.server.HttpRequest;
import org.voovan.http.server.HttpResponse;
import org.voovan.vestful.dto.ClassElement;
import org.voovan.tools.TFile;
import org.voovan.tools.json.JSON;

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
public class ClassDescHandler implements HttpBizHandler {
    private ClassElement classElement;
    private String htmlContent;

    /**
     * 构造函数
     * @param classElement ClassElement 对象
     */
    public ClassDescHandler(ClassElement classElement) throws UnsupportedEncodingException {
        this.classElement = classElement;
        this.htmlContent = new String(TFile.loadResource("ClassDesc.html"),"UTF-8");
    }

    @Override
    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        String methodData = JSON.toJSON(classElement).replace("\"class ", "\"");
        htmlContent =  htmlContent.replaceAll(":classData",methodData);
        httpResponse.body().write(htmlContent);
    }

}
