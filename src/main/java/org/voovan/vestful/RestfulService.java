package org.voovan.vestful;

import org.voovan.http.server.HttpServer;

import java.io.UnsupportedEncodingException;

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
    public void serve() {
        RestfulContext.installRestful(httpServer);
        httpServer.serve();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        RestfulService restfulService = new RestfulService();
        restfulService.serve();
    }


}
