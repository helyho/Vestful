package org.voovan.restful;

import org.voovan.http.server.HttpServer;
import org.voovan.restful.annotation.Param;
import org.voovan.restful.annotation.Restful;
import org.voovan.tools.TObject;
import org.voovan.tools.TReflect;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Restful 接口服务类
 *
 * @author helyho
 * <p>
 * RestfulService Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class RestfulService {
    private HttpServer httpServer;

    /**
     * 构造函数
     */
    public RestfulService(){
        httpServer = HttpServer.newInstance();
    }

    /**
     * 启动服务
     */
    public void serve() throws UnsupportedEncodingException {
        RestfulContext.initWithConfig(httpServer);
        httpServer.serve();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        RestfulService restfulService = new RestfulService();
        restfulService.serve();
    }


}
