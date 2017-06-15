// 全选
function selectAll(obj,id){
	$('#' + id).find("input[type=checkbox]").each(function(){
		$(this).prop("checked",$(obj).prop("checked"));
	});
}

// 处理页面加载事件
$(function () {

    // 绑定仓库切换事件
    $('.nav.navbar-nav.navbar-right').on('click', '.dropdown-menu li', function () {
        // 获取需要切换仓库的编号
        var code = $(this).find('span').text();
        if (code) {
            // 提交切换仓库请求
            $.post($('#baseUrl').attr('href') + 'user/change-ware-house', {code: code}, function (result) {
                if (result) {
                    // 刷新当前页面
                    window.location.reload();
                }
            });
        }
    });
});

// 退出
function logout(){
	window.location.href = $('#baseUrl').attr('href') + 'logout';
}

// 构造LoadingOverlay的custom属性
function getCustomElement(tip){
	return $("<div>", {
        css : {
            "font-size" : "15px"
        },
        text    : tip
    });
}