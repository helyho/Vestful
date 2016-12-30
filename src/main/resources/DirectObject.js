/**
 * 对象方法模板
 * 为了兼容 JS 语法, 替换的模板参数定位采用: T/×参数名称×/
*/
function T/*CLASS_NAME*/() {
    this.objectId = null;
    this.type = "ServerObject";

    className = "T/*CLASS_FULL_NAME*/";
    //构造器
    {
        var constructorArgsArray = Array.prototype.slice.call(arguments);
        this.objectId = parseInt(createObject(className, constructorArgsArray).text);
    }

    /**
     * 释放对象
     */
    this.release = function() {
        release(this.objectId)
    }

    T/*METHODS*/
}

/**
 * 在服务端构造对象
 */
function createObject(v_className, v_params) {
    return ajax({
        url: "/T/*ROUTE*//createObject", //请求地址
        type: "get", //请求方式
        data: { className: v_className, params: v_params }, //请求参数
        async: false, //是否同步
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
        url: "/T/*ROUTE*//invoke", //请求地址
        type: "get", //请求方式
        data: { pooledObjectId: v_objectId, methodName: v_methodName, params: v_params }, //请求参数
        async: false, //是否同步
        dataType: "json"
    });
}

/**
 * 在服务端构造对象
 */
function release(v_objectId) {
     var response =  ajax({
        url: "/T/*ROUTE*//release", //请求地址
        type: "get", //请求方式
        data: { pooledObjectId: v_objectId }, //请求参数
        async: false, //是否同步
        dataType: "json"
    });
}

/**
 * ajax调用类的封装
 * @param options 参数
 * @returns 返回的数据模型 {code: number, statusText: string, text: null, xml: null}
 */
function ajax(options) {
    options = options || {};
    options.type = (options.type || "GET").toUpperCase();
    options.dataType = options.dataType || "json";
    var params = formatParams(options.data);

    //创建Ajax对象,浏览器兼容
    if (window.XMLHttpRequest) {
        var xhr = new XMLHttpRequest();
    } else {
        //IE 浏览器创建 Ajax
        var xhr = new ActiveXObject('Microsoft.XMLHTTP');
    }

    //状态变更事件回调函数
    if (options.async) {
        xhr.onreadystatechange = function() {
            //4请求完成
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

    //连接&发送
    if (options.type == "GET") {
        xhr.open("GET", options.url + "?" + params, options.async);
        xhr.send(null);
    } else if (options.type == "POST") {
        xhr.open("POST", options.url, options.async);
        //设置表单提交时的内容类型
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.send(params);
    }

    if (!options.async) {
        if (xhr.readyState == 4) {
            var respObject = { "statusCode": xhr.status, "statusText": xhr.statusText, "text": xhr.responseText, "xml": xhr.responseXML };
            if (respObject.statusCode >= 200 && respObject.statusCode < 300) {
                return respObject
            }else{
                throw new Error(respObject.text);
            }
        }
    }
}

//格式化参数
function formatParams(data) {
    var arr = [];
    for (var name in data) {
        var key = encodeURIComponent(name);
        var value = null;
        if (data[name] instanceof Array || data[name] instanceof Object) {
            value = encodeURIComponent(JSON.stringify(data[name]));
        } else {
            value = encodeURIComponent(data[name])
        }

        arr.push(key + "=" + value);
    }
    return arr.join("&");
}