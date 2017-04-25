/**
 * 对象方法模板
 * 为了兼容 JS 语法, 替换的模板参数定位采用: T/×参数名称×/
*/
function T/*CLASS_NAME*/() {
    this.objectId = T/*OBJECTID*/;
    this.type = "ServerObject";

    className = "T/*CLASS_FULL_NAME*/";
    /*构造器*/
    {
        if(this.objectId==null) {
            var constructorArgsArray = Array.prototype.slice.call(arguments);
            this.objectId = createObject(className, constructorArgsArray);
        }
    };

    /**
     * 释放对象
     */
    this.release = function() {
        release(this.objectId);
    };

    T/*METHODS*/
}

/**
 * 在服务端构造对象
 */
function createObject(v_className, v_params) {
    return ajax({
        url: "T/*ROUTE*//createObject",
        type: "get",
        data: { className: v_className, params: v_params },
        async: false,
        dataType: "json"
    });
}

/**
 * 调用对象的某个方法
 */
function invokeMathod(v_objectId, v_methodName, v_params) {
    if (v_objectId == null) {
        throw new error("this object is not instance");
    }

    if (v_methodName == null) {
        throw new error(" if you want to invoke method , they need a method name.");
    }

    if (v_params == null) {
        v_params = [];
    }
    return ajax({
        url: "T/*ROUTE*//invoke",
        type: "get",
        data: { pooledObjectId: v_objectId, methodName: v_methodName, params: v_params },
        async: false,
        dataType: "json"
    });
}

/**
 * 在服务端构造对象
 */
function release(v_objectId) {
    ajax({
        url: "T/*ROUTE*//release",
        type: "get",
        data: { pooledObjectId: v_objectId },
        async: false,
        dataType: "json"
    });
}

/**
 * ajax调用类的封装
 * @param options 参数
 */
function ajax(options) {
    options = options || {};
    options.type = (options.type || "GET").toUpperCase();
    options.dataType = options.dataType || "json";
    var params = formatParams(options.data);

    if (window.XMLHttpRequest) {
        var xhr = new XMLHttpRequest();
    } else {
        var xhr = new ActiveXObject('Microsoft.XMLHTTP');
    }

    if (options.async) {
        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4) {
                var status = xhr.status;
                if (status >= 200 && status < 300) {
                    options.success && options.success(xhr.responseText, xhr.responseXML);
                } else {
                    options.fail && options.fail(status);
                }
            }
        }
    }

    if (options.type == "GET") {
        xhr.open("GET", options.url + "?" + params, options.async);
        xhr.send(null);
    } else if (options.type == "POST") {
        xhr.open("POST", options.url, options.async);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.send(params);
    }

    if (!options.async) {
        if (xhr.readyState == 4) {
            if (xhr.status >= 200 && xhr.status < 300) {
                return xhr.responseText
            }else{
                throw new Error(xhr.responseText);
            }
        }
    }
}

function formatParams(data) {
    var arr = [];
    for (var name in data) {
        var key = encodeURIComponent(name);
        var value = null;
        if (data[name] instanceof Array || data[name] instanceof Object) {
            value = encodeURIComponent(JSON.stringify(data[name]));
        } else {
            value = encodeURIComponent(data[name]);
        }

        arr.push(key + "=" + value);
    }
    return arr.join("&");
}