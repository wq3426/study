/**
 * 描述： AJAX分页工具标签
 * 参数： pageNum:当前页,
 * 		 pageSize：每页显示条数,
 * 		 totalCount：总计数,
 * 		 func：调用分页的js function,如 see_base_info.html下的showPage方法，格式：javascript:showPage
 * 
 */
var pageNum;
var pageSize;
var totalCount;
var pageCount;
var showCount = 10;
var func;
function pagerUtils(_pageNum, _pageSize, _totalCount, _func) {
	pageNum = _pageNum;
	pageSize = _pageSize;
	totalCount = _totalCount;
	func = _func;

	pageSize = pageSize < 1 ? 1 : pageSize;
	pageCount = Math.floor(totalCount / pageSize);
	pageCount = totalCount % pageSize > 0 ? pageCount + 1 : pageCount;
	pageNum = pageNum > pageCount ? pageCount : pageNum;
	pageNum = pageNum < 1 ? 1 : pageNum;

	pageStr = ("<div class=\"page\" >");
	pageStr += getHeadString();
	pageStr += getBodyString();
	pageStr += getFooterString();
	pageStr += ("</div>");
	return pageStr;
}

function getHeadString() {

	var headString = "<span class=\"info\" >";
	headString += "共";
	headString += totalCount;
	headString += "条记录";
	headString += "</span>\n";

	headString += "<span class=\"info\">";
	headString += pageNum;
	headString += "/";
	headString += pageCount;
	headString += "</span>\n";

	headString += "<ul>";
	if (pageNum > 1) {
		headString += "<li><a ";
		headString += " class=\"unselected\" ";
		headString += " href=\"" + func + "(" + 1 + ");\">";
		headString += "|&lt;";
		headString += "</a></li>\n";

		headString += "<li><a  ";
		headString += " class=\"unselected\" ";
		headString += " href=\"" + func + "(" + (pageNum - 1) + ");\">";
		headString += "&lt;&lt;";
		headString += "</a></li>\n";
	}
	return headString;
}

function getBodyString() {
	var start = pageNum - Math.floor(showCount / 2);
	start = start <= 1 ? 1 : start;
	var end = start + showCount;

	end = end > pageCount ? pageCount : end;
	var bodyString = "";
	for (var i = start; i <= end; i++) {

		bodyString += "<li><a ";
		if (i != pageNum) {
			bodyString += " class=\"unselected\"";
			bodyString += "href=\"" + func + "(" + i + ");\">";
		} else {
			bodyString += " class=\"selected\">";
		}

		bodyString += i;
		bodyString += "</a></li>\n";

	}
	return bodyString;
}

function getFooterString() {
	var footerStr = "";
	if (pageNum < pageCount) {
		footerStr += "<li><a ";
		footerStr += " class=\"unselected\" ";
		footerStr += "href=\"" + func + "(" + (pageNum + 1) + ");\">";
		footerStr += "&gt;&gt;";
		footerStr += "</a></li>\n";

		footerStr += "<li><a ";
		footerStr += " class=\"unselected\" ";
		footerStr += "href=\"" + func + "(" + (pageCount) + ");\">";
		footerStr += "&gt;|";
		footerStr += "</a></li>\n";

	}
	footerStr += "</ul>";
	return footerStr;
}// 分页结束
