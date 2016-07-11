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
    private String errMsg;
    private String currentDate = TDateTime.now();

    public Error(String errMsg){
        this.errMsg = errMsg;
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

    public static Error newInstance(String errMsg){
        return new Error(errMsg);
    }
}
