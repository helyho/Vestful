package org.voovan.vestful.dto;

import org.voovan.tools.TDateTime;
import org.voovan.tools.json.JSON;

/**
 * 返回的错误信息
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class Error {
    private String errURL;
    private String errClass;
    private String errMsg;
    private String errTime= TDateTime.now();

    public Error(String errURL, String errClass, String errMsg){
        this.errMsg = errMsg;
        this.errURL = errURL;
        this.errClass = errClass;
    }

    public String getErrURL() {
        return errURL;
    }

    public void setErrURL(String url) {
        this.errURL = errURL;
    }

    public String getErrClass() {
        return errClass;
    }

    public void setErrClass(String errClass) {
        this.errClass = errClass;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString(){
        return JSON.toJSON(this);
    }

    public static Error newInstance(String url, String errClass, String errMsg){
        return new Error(url,errClass,errMsg);
    }
}
