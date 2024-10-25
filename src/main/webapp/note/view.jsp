<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: asus
  Date: 2024/9/23
  Time: 16:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<html>
<head>
    <title>Title</title>
</head>
<body>
<div class="col-md-9">
    <div class="data_list">
        <div class="data_list_title">
            <span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;
            <c:if test="${empty noteInfo}">
                添加云记
            </c:if>
            <c:if test="${!empty noteInfo}">
                修改云记
            </c:if>
        </div>
        <div class="container-fluid">
            <div class="container-fluid">
                <div class="row" style="padding-top: 20px;">
                    <div class="col-md-12">
                        <%--判断类型id是否为空如果为空提示用户先添加类型--%>
                        <c:if test="${empty typeList }">
                            <h2>暂未查询到云计类型</h2>
                            <h4><a href="type?actionName=list">添加类型</a></h4>
                        </c:if>
                        <c:if test="${!empty typeList }">
                        <form class="form-horizontal" method="post" action="note">
    <%--                         设置隐藏域：用来存放用户行为--%>
                                <input type="hidden" name="actionName" value="addOrUpdate">
    <%--                               设置隐藏域存放noteId--%>
                                <input type="hidden" name="noteId" value="${noteInfo.noteId}">
    <%--                                    设置隐藏域用来存放用户发布云记时所在地区的经纬度--%>
    <%--                                    经度--%>
                                <input type="hidden" name="lon" id="lon">
    <%--                                        纬度--%>
                                <input type="hidden" name="lat" id="lat">`
                            <div class="form-group">
                                <label for="typeId" class="col-sm-2 control-label">类别:</label>
                                <div class="col-sm-8">
                                    <select id="typeId" class="form-control" name="typeId">
                                        <option value="">请选择云记类别...</option>
                                        <c:forEach var="item" items="${typeList}">
                                            <c:choose>
                                                <c:when test="${!empty resultInFo}">
                                                    <option <c:if test="${resultInFo.result.typeId==item.typeId}">selected</c:if> value="${item.typeId}"> ${item.typeName}</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option <c:if test="${noteInfo.typeId==item.typeId}">selected</c:if> value="${item.typeId}"> ${item.typeName}</option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="title" class="col-sm-2 control-label">标题:</label>
                                <div class="col-sm-8">
                                    <c:choose>
                                        <c:when test="${!empty resultInFo}">
                                            <input class="form-control" name="title" id="title" placeholder="云记标题" value="${resultInFo.result.title}">
                                        </c:when>
                                        <c:otherwise>
                                            <input class="form-control" name="title" id="title" placeholder="云记标题" value="${noteInfo.title}">
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="title" class="col-sm-2 control-label">内容:</label>
                                <div class="col-sm-8">
                                    <c:choose>
                                        <c:when test="${!empty resultInFo}">
                                            <textarea id="content" name="content">${resultInFo.result.content}</textarea>
                                        </c:when>
                                        <c:otherwise>
                                            <textarea id="content" name="content">${noteInfo.content}</textarea>
                                        </c:otherwise>
                                    </c:choose>
                                        <%--准备容器加载富文本编辑器--%>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-offset-6 col-sm-4">
                                    <input type="submit" class="btn btn-primary" onclick="return checkFrom()"  value="保存">
                                    &nbsp;<span id="msg" style="font-size: 12px;color: red">${resultInFo.msg}</span>
                                </div>
                            </div>
                        </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script type="text/javascript">
        var ue;
        $(function (){
            // 加载富文本编辑器（容器id）
            ue = UE.getEditor("content");
        })
/*
*  1. 获取表单元素的值
                获取下拉框选中的选项  .val()
                获取文本框的值       .val()
                获取富文本编辑器的内容
                    ue.getContent() 获取富文本编辑器的内容（包含html标签）
                    ue.getContentTxt() 获取富文本编辑器的纯文本内容（不包含html标签）
            2. 参数的非空判断
                如果为空，提示用户，并return fasle
            3. 如果参数不为空，则return true，提交表单
* */
        function checkFrom(){
            // 获取下拉框选中的选项  .val()
            var typeId = $("#typeId").val();
            // 获取文本框的值       .val()
            var title = $("#title").val();
            // 获取富文本编辑器的内容
            var content = ue.getContent();
            // 参数的非空判断
            if (isEmpty(typeId)){
                $("#msg").html("请选择云计类型");
                return false;
            }
            if (isEmpty(title)){
                $("#msg").html("云计标题不能为空");
                return false;
            }
            if (isEmpty(content)){
                $("#msg").html("云计内容不能为空");
                return false;
            }
            return true;
        }
    </script>
    <script type="text/javascript" src="https://api.map.baidu.com/api?v=2.0&ak=Yr2uGUrQPbSjWyxLuCZK55jnVvmLKStY"></script>
    <script type="text/javascript" >
<%--        百度地图获取当前用户位置的经纬度--%>
        var geolocation = new BMap.Geolocation();
        geolocation.getCurrentPosition(function (r){
            if (this.getStatus() == BMAP_STATUS_SUCCESS){
                console.log("您的位置："+r.point.lng+','+r.point.lat)
                // 获取经纬度
                $("#lon").val(r.point.lng)
                $("#lat").val(r.point.lat)

            }else {
                console.log("failed："+this.getStatus())
            }
        });
    </script>
</div>
</body>
</html>
