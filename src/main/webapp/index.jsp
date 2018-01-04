<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8">
    <script src="${pageContext.request.contextPath}js/jquery-3.1.0.min.js"></script>
    <script type="text/javascript">
        $(function () {
            var affectItem="${requestScope.affectItem}";
            if (affectItem>0)
                alert("添加成功！");
            if (affectItem==-1)
                alert("添加失败！");
        })
    </script>
</head>
<body>
<h2>Hello World!</h2>
        <form action="/begin" method="post">
            <label>用户id</label><input type="text" name="userId">
            <label>群号</label><input type="text" name="groupId">
            <label>状态</label><input type="text" name="status">
            <button>添加</button>
        </form>
</body>
</html>
