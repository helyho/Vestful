package org.voovan.vestful.exception;

/**
 * 异常信息描述
 *     通过抛出该类的异常可以控制 http 响应的状态码和状态描述
 *
 * @author helyho
 *         <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Restful
 * Licence: Apache v2 License
 */
public class RestfulException extends Exception {

    private int httpStatusCode = 500;
    private String httpStatusDesc = "";

    public RestfulException(String message){
        super(message);
    }

    public RestfulException(String message,int httpStatusCode,String httpStatusDesc){
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.httpStatusDesc = httpStatusDesc;
    }

    public RestfulException(Exception exception){
        super(exception);
        this.httpStatusDesc = exception.getMessage();
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getHttpStatusDesc() {
        return httpStatusDesc;
    }

    public void setHttpStatusDesc(String httpStatusDesc) {
        this.httpStatusDesc = httpStatusDesc;
    }
}
