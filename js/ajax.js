//使用案例
ajax({
    url: "./DirectObject!!", //请求地址
    type: "get", //请求方式
    data: { name: "super", age: 20 }, //请求参数
    async: false, //是否同步
    dataType: "json"
});

function ajax(options) {
    options = options || {};
    options.type = (options.type || "GET").toUpperCase();
    options.dataType = options.dataType || "json";
    var params = formatParams(options.data);
    responseText = null;
    responseXML = null;
    status = null;

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
            var status = xhr.status;
            if (status >= 200 && status < 300) {
                responseText = xhr.responseText;
                responseXML = xhr.responseXML;
            }
            return { "code": status, "statusText": xhr.statusText, "text": responseText, "xml": responseXML };
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