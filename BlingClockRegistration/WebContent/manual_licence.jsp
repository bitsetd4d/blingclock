<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Manual Licence</title>
</head>
<body>
<form method="post" action="manual-license-gen">
<table>
  <tr>
    <td>Name</td>
    <td><input name="name" value=""></td>
  </tr>
  <tr>
  	<td>Email</td>
    <td><input name="email" value=""></td>
  </tr>
  <tr>
  	<td>Special</td>
    <td><input name="special" value=""></td>
  </tr>
    <tr>
  	<td>Password</td>
    <td><input type="password" name="password"></td>
  </tr>
  <tr>
  	<td>&nbsp;</td>
    <td><input type="submit" name="submit" value="Submit"></td>
  </tr>
</table>
</form>
</body>
</html>