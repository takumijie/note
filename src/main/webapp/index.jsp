<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" isThreadSafe="false" %>
<html lang="zh"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>云R记</title>
    <link href="static/css/note.css" rel="stylesheet">
    <link href="static/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="static/sweetalert/sweetalert2.min.css" rel="stylesheet">
    <script src="static/js/jquery-1.11.3.js"></script>
    <script src="static/bootstrap/js/bootstrap.js"></script>
    <script src="static/sweetalert/sweetalert2.min.js"></script>
    <script src="static/js/util.js"></script>

    <!-- 配置文件 -->
    <script type="text/javascript" src="static/ueditor/ueditor.config.js"></script>
    <!-- 编辑器源码文件 -->
    <script type="text/javascript" src="static/ueditor/ueditor.all.js"></script>
    <style type="text/css">
        body {
            padding-top: 60px;
            padding-bottom: 40px;
            background: url(static/images/bg.gif) repeat;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" style="font-size:25px" href="index">云日记</a>
        </div>
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li <c:if test="${menu_page=='index'}"> class="active"</c:if>><a href="index"><i class="glyphicon glyphicon-cloud"></i>&nbsp;主&nbsp;&nbsp;页</a></li>
                <li <c:if test="${menu_page=='note'}"> class="active"</c:if>><a href="note?actionName=view"><i class="glyphicon glyphicon-pencil"></i>&nbsp;发表云记</a></li>
                <li <c:if test="${menu_page=='type'}"> class="active"</c:if>><a href="type?actionName=list"><i class="glyphicon glyphicon-list"></i>&nbsp;类别管理</a></li>
                <li <c:if test="${menu_page=='user'}"> class="active"</c:if>><a href="user?actionName=userCenter"><i class="glyphicon glyphicon-user"></i>&nbsp;个人中心</a>
                <li <c:if test="${menu_page=='report'}"> class="active"</c:if>><a href="report?actionName=info"><i class="glyphicon glyphicon-signal"></i>&nbsp;数据报表</a></li>
            </ul>
            <form class="navbar-form navbar-right" role="search" action="index">
                <div class="form-group">
                    <input type="hidden" name="actionName" value="searchTitle">
                    <input type="text" name="title" class="form-control" placeholder="搜索云记" value="${title}">
                </div>
                <button type="submit" class="btn btn-default">查询</button>
            </form>
        </div>
    </div>
</nav>
<div class="container">
    <div class="row-fluid">
        <div class="col-md-3">
            <div class="data_list">
                <div class="data_list_title"><span class="glyphicon glyphicon-user"></span>&nbsp;个人中心&nbsp;&nbsp;&nbsp;&nbsp;<a href="user?&actionName=logout">退出</a></div>
                <div class="userimg">
                    <img src="user?actionName=userHead&imageName=${user.head}">
                </div>
                <div class="nick">${user.nick}</div>
                <div class="mood">(${user.mood})</div>
            </div>
            <div class="data_list">
                <div class="data_list_title">
					<span class="glyphicon glyphicon-calendar">
					</span>&nbsp;云记日期
                </div>

                <div>
                    <ul class="nav nav-pills nav-stacked">
                        <c:forEach items="${dateInfo}" var="item" >
                            <li><a href="index?actionName=searchDate&date=${item.groupName}">${item.groupName}<span class="badge">${item.noteCount}</span></a></li>
                        </c:forEach>
                    </ul>
                </div>

            </div>
            <div class="data_list">
                <div class="data_list_title">
					<span class="glyphicon glyphicon-list-alt">
					</span>&nbsp;云记类别
                </div>
                <div>
                    <ul id="typeUl" class="nav nav-pills nav-stacked">
                        <c:forEach items="${typeInfo}" var="item">
                            <li id="li_${item.typeId}"><a href="index?actionName=searchType&typeId=${item.typeId}"><span id="sp_${item.typeId}">${item.groupName}</span> <span class="badge">${item.noteCount}</span></a></li>
                        </c:forEach>
                    </ul>
                </div>

            </div>
        </div>
    </div>
<%--动态包含页面--%>
<%--    云记列表--%>
<%--<jsp:include page="note/list.jsp"></jsp:include>--%>
<%--    个人中心--%>
<%--    <jsp:include page="user/info.jsp"></jsp:include>--%>
<%--类型列表--%>
<%--    <jsp:include page="type/list.jsp"></jsp:include>--%>
<%--  后台动态设置显示的页面,通过包含设置  --%>
<c:if test="${empty changePage}">
    <jsp:include page="note/list.jsp"></jsp:include>
</c:if>
<c:if test="${!empty changePage}">
    <jsp:include page="${changePage}"></jsp:include>
</c:if>
</div>

</body></html>
