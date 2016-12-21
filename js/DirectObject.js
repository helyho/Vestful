function JavaString() {
    objectId = null;
    className = "java.lang.String";
    //构造器
    {
        if (v_className != null) {
            if (v_params == null) {
                v_params = []
            }
            var constructorArgsArray = Array.prototype.slice.call(arguments);
            objectId = createObject(className, constructorArgsArray).text
        }
    }

    /**
     * 获取 ID
     */
    this.getId = function() {
        return objectId;
    }

    /**
     * 释放对象
     */
    this.release = function() {
        release(objectId)
    }

    /**
     * Java 类的方法,在代码里动态构建
     */
    this.getString = function() {
        var argsArray = Array.prototype.slice.call(arguments);
        return invokeMathod(objectId, "toString", argsArray).text;
    }
}

/**
 * 在服务端构造对象
 */
function createObject(v_className, v_params) {
    return ajax({
        url: "/DirectObject/createObject", //请求地址
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
    if (objectId == null) {
        throw new error("this object is not instance");
    }

    if (v_methodName == null) {
        throw new error(" if you want to invoke method , they need a method name.");
    }

    if (v_params == null) {
        v_params = [];
    }
    return ajax({
        url: "/DirectObject/invoke", //请求地址
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
    return ajax({
        url: "/DirectObject/release", //请求地址
        type: "get", //请求方式
        data: { pooledObjectId: v_objectId }, //请求参数
        async: false, //是否同步
        dataType: "json"
    });
}