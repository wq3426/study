Date.prototype.Format = function (fmt) { //author: meizz   
    var o = {
        "M+": this.getMonth() + 1,                 //月份   
        "d+": this.getDate(),                    //日   
        "h+": this.getHours(),                   //小时   
        "m+": this.getMinutes(),                 //分   
        "s+": this.getSeconds(),                 //秒   
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度   
        "S": this.getMilliseconds()             //毫秒   
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

Utility = function () { }

Utility.InsertButton = function (parentDomID, type, text, action) {
    var s = "<table style=\"width:auto\"><tr><td style=\"border:0px;padding:0px\">";
    if (type == "ok") {
        s += "<a class=\"btn_ok_link\" href=\"" + action + "\">";
        s += "<span class=\"btn_ok_text\">" + text + "</span>";
        s += "<span class=\"btn_ok_end\"></span>";
        s += "</a>";
    } else if (type == "back") {
        s += "<a class=\"btn_item_link\" href=\"" + action + "\">";
        s += "<span class=\"btn_item_text\"><span style=\"font-family:simsun\">&lt;&lt;&nbsp;</span>" + text + "</span>";
        s += "<span class=\"btn_item_end\"></span>";
        s += "</a>";
    } else {
        s += "<a class=\"btn_item_link\" href=\"" + action + "\">";
        s += "<span class=\"btn_item_text\">" + text + "</span>";
        s += "<span class=\"btn_item_end\"></span>";
        s += "</a>";
    }
    s += "</td></tr></table>";
    $("#" + parentDomID).html(s);
}

//打开门店选择器
Utility.ShowShopSelector = function(maxCount, callbackFuncName) {
    parent.WindowsManager.Open({
        title: "选择门店",
        w: 750,
        h: 400,
        isFrame: true,
        isDialog: true,
        data: "/shop/shop_selector.aspx?max_count=" + maxCount,
        callbackFuncName: callbackFuncName,
        srcWindow: window
    });
}

Utility.PostAsync = function (url, data, successHandle, errorHandle) {
    if (!errorHandle) {
        errorHandle = function (e) { alert("Request Error: " + e.responseText); }
    }
    $.ajax({
        url: url,
        type: "post",
        async: true,
        data: data,
        error: errorHandle,
        success: successHandle
    });
}

Utility.PostSync = function (url, data, successHandle, errorHandle) {
    if (!errorHandle) {
        errorHandle = function (e) { alert("Request Error: " + e.responseText); }
    }
    $.ajax({
        url: url,
        type: "post",
        async: false,
        data: data,
        error: errorHandle,
        success: successHandle
    });
}

Utility.ParseToJSON = function (s) {
    var m;
    try {
        m = eval('(' + s + ')');
        return m;
    } catch (e) {
        return;
    }
}

Utility.IsJSON = function (obj) {
    return typeof(obj) == "object";
    //return (typeof (obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length);
}

//$("#tbxKeyword").bind("keypress", function (e) { return Utility.PassEnterKeyPress(e) });
Utility.PassEnterKeyPress = function (e) {
    var evt = window.event || e;
    var keyCode = evt.keyCode || evt.wicth;
    if (keyCode == 13) {
        return false;
    }
}

Utility.HtmlEncode = function (text) {
    var ret = "";
    if (typeof (text) != "string") return ret;
    var arr = text.split('');
    for (var i = 0; i < arr.length; i++) {
        var s = arr[i];
        switch (s) {
            case "<": ret += "&lt;"; break;
            case ">": ret += "&gt;"; break;
            case "&": ret += "&amp;"; break;
            case 13: ret += "<br />"; break;
            case " ": ret += "&nbsp;"; break;
            default: ret += s;
        }
    }
    return ret;
}

Utility.FixNumber = function (number, fractionDigits) {
    return (parseInt(number * Math.pow(10, fractionDigits) + 0.5) / Math.pow(10, fractionDigits)).toString();
}

Utility.SetLengthLimit = function (elementID, limit) {
    $("#" + elementID).keydown(function (e) {
        var key = e.keyCode;
        if (key == 8 || key == 46 || key == 37 || key == 38 || key == 39 || key == 40) return true;
        if ($(this).val().length >= limit) return false
    });
}

Utility.GetQueryString = function (sArgName, sHref) {
    if (sHref == undefined) sHref = window.location.href;
    var args = sHref.split("?");
    var retval = "";
    if (args[0] == sHref) return retval;
    var str = args[1];
    args = str.split("&");
    for (var i = 0; i < args.length; i++) {
        str = args[i];
        var arg = str.split("=");
        if (arg.length <= 1) continue;
        if (arg[0] == sArgName) retval = arg[1];
    }
    return retval;
}

Utility.SetLoading = function (parentElementID) {
    $("#" + parentElementID).text("查询数据中，请稍候……");
}

Utility.CopyTableResult = function (tableDom) {
    var s = "";
    var tab = String.fromCharCode(09);
    if (tableDom == undefined) {
        alert("请先查询出结果后再使用复制功能！");
        return;
    }
    for (var i = 0; i < tableDom.rows.length; i++) {
        if (s != "") s += "\r\n";
        for (var j = 0; j < tableDom.rows[i].cells.length; j++) {
            if (j > 0) s += tab;
            s += tableDom.rows[i].cells[j].innerText;
        }
    }
    if (window.clipboardData != undefined) {
        if (window.clipboardData.setData("text", s)) {
            alert("复制成功！");
        }
        else {
            alert("复制失败，可能您的浏览器不支持复制功能！");
        }
    }
    else {
        alert("复制失败，可能您的浏览器不支持复制功能！");
    }
}

Utility.SetTab = function (name, cursel, n) {
    for (i = 1; i <= n; i++) {
        var menu = document.getElementById(name + i);
        var con = document.getElementById("con_" + name + "_" + i);
        menu.className = i == cursel ? "hover" : "";
        con.style.display = i == cursel ? "block" : "none";
    }
}

//检查电话号码，格式如：010-12345678。
Utility.CheckPhone = function (phone) {
    var reg = /^(([0\+]\d{2,3}-)?(0\d{2,3})-)?(\d{7,8})(-(\d{1,}))?$/;
    if (reg.exec(phone))
        return true;
    else
        return false;
}

//n表示第几项，从0开始算起
Array.prototype.del = function (n) {
    if (n < 0)
        return this;
    else
        return this.slice(0, n).concat(this.slice(n + 1, this.length));
}

/*****CookieHelper*****/
CookieHelper = function () { }
//CookieHelper.Get = function (key)
//{
//    var cookieStr = document.cookie;
//    var cookies = cookieStr.split(";");
//    for(var i=0;i<cookies.length;i++)
//    {
//        var objCookie = cookies[i].split("=");
//        if (objCookie[0]==key)
//        {
//            return objCookie[1];break;
//        }
//    }
//    return "";
//}
CookieHelper.SetCookie = function (name, value) { //设置名称为name,值为value的Cookie 
    var argc = SetCookie.arguments.length;
    var argv = SetCookie.arguments;
    var path = (argc > 3) ? argv[3] : null;
    var domain = (argc > 4) ? argv[4] : null;
    var secure = (argc > 5) ? argv[5] : false;


    document.cookie = name + "=" + value +
    ((path == null) ? "" : ("; path=" + path)) +
    ((domain == null) ? "" : ("; domain=" + domain)) +
    ((secure == true) ? "; secure" : "");
}

CookieHelper.DeleteCookie = function (name) { //删除名称为name的Cookie 
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval = GetCookie(name);
    document.cookie = name + "=" + cval + "; expires=" + exp.toGMTString();
}

CookieHelper.getCookieVal = function (offset) { //取得项名称为offset的cookie值 
    var endstr = document.cookie.indexOf(";", offset);
    if (endstr == -1)
        endstr = document.cookie.length;
    return document.cookie.substring(offset, endstr);
    //return unescape(document.cookie.substring(offset, endstr));
}

CookieHelper.GetCookie = function (name) { //取得名称为name的cookie值 
    var arg = name + "=";
    var alen = arg.length;
    var clen = document.cookie.length;
    var i = 0;
    while (i < clen) {
        var j = i + alen;
        if (document.cookie.substring(i, j) == arg)
            return CookieHelper.getCookieVal(j);
        i = document.cookie.indexOf(" ", i) + 1;
        if (i == 0) break;
    }
    return null;
}

/***CookieHelper end***/
/*--------------------*/

Utility.IsNullOrEmpty = function (value) {
    if (value == null) return true;
    if (value.length == 0) return true;
    return false;
}
/*--------------------*/

Utility.CheckEmail = function (value) {
    var resss = /^[a-zA-Z0-9_\-\.]{1,}@[a-zA-Z0-9_\-\.]{1,}\.[a-zA-Z0-9_\-.\.]{1,}$/;
    if (!resss.test(value)) {
        return false;
    }
    return true;
}

Utility.CheckNumber = function (value) {
    var resss = /^\d{11}$/;
    if (!resss.test(value)) {
        return false;
    }
    return true;
}
//验证类别名称只能输入英文字母,数字,汉字
Utility.CheckClassName = function (value) {
    var res = /^[a-zA-Z0-9\u4e00-\u9fa5]+$/;
    if (!res.test(value)) {
        return false;
    }
    return true;
}

Utility.getSaveTable = function (tableDom) {
    if (tableDom == undefined) {
        //alert("请先选择输出方式为表格的结果后再使用保存功能！");
        return;
    }
    var tab = String.fromCharCode(09);
    var s = "";
    for (var i = 0; i < tableDom.rows.length; i++) {
        if (i > 0) s += '#';
        for (var j = 0; j < tableDom.rows[i].cells.length; j++) {
            //if (j > 0) s += tab;
            if (j > 0) s += '|';
            s += tableDom.rows[i].cells[j].innerText;
        }
    }
    return s;
}

