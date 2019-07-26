<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>测试页面</title>

   <script type="text/javascript">
       function getFullInfoList() {
            window.location.href = "${pageContext.request.contextPath}/fullInfo?username=wzh12";
       }

   </script>
</head>
<body>
    <table>
        <c:forEach items="${page.records}" var="info" varStatus="">
            <tr>
                <td>${info.id}</td>
                <td>${info.username}</td>
                <td>${info.password}</td>
                <td>${info.age}</td>
                <td>${info.sex}</td>
            </tr>
        </c:forEach>
    </table>
<h1>哈哈哈哈</h1>
</body>
</html>
