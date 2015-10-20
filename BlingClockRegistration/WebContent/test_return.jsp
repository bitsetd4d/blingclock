<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body> 
<table>
  <tr>
    <td>tx!</td><td><c:out value="${param.tx}" /></td>
  </tr>
  <tr>
    <td>tx!</td><td>${param.tx}</td>
  </tr>
  <!--  When doing this, remember to make it read a static template via http!! -->
</table>
</body>
</html>