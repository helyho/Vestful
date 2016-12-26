package org.voovan.vestful;


import org.voovan.http.server.WebServer;

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
    private WebServer webServer;

    /**
     * 构造函数
     */
    public RestfulService(){
        webServer = webServer.newInstance();
    }

    /**
     * 启动服务
     */
    public void serve() {
        webServer.serve();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        RestfulService restfulService = new RestfulService();
        restfulService.serve();
    }


}
