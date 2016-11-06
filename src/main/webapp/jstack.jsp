<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<html>
<head>
    <title>Jstack</title>
</head>
<body>
<pre>
<%
    Map<Thread, StackTraceElement[]> stackMap = Thread.getAllStackTraces();
    for (Map.Entry<Thread, StackTraceElement[]> entry : stackMap.entrySet()) {
        Thread thread = entry.getKey();
        if (!thread.equals(Thread.currentThread())) {
            out.print("<hr/><h3>线程: " + thread.getName() + "</h3>");

            for (StackTraceElement element : entry.getValue()) {
                out.println("\t" + element);
            }
        }
    }
%>
</pre>
</body>
</html>
