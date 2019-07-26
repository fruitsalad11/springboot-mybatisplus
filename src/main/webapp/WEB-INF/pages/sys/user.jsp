<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>测试页面</title>

   <script type="text/javascript">
       function getFullInfoList() {
            window.location.href = "${pageContext.request.contextPath}/sys/user/fullInfo?username=wzh12";
       }

   </script>
</head>
<body>
    <table>
        <c:forEach items="${page.records}" var="user" varStatus="">
            <tr>
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.password}</td>
            </tr>
        </c:forEach>

    </table>
    <input type="button" name="submit" id="submit" value="跳转" onclick="javascript:getFullInfoList()"/>
</body>
</html>
