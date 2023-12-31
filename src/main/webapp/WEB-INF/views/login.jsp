<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="context" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
body {font-family: Arial, Helvetica, sans-serif;}
form {border: 3px solid #f1f1f1;}

input[type=text], input[type=password] {
  width: 100%;
  padding: 12px 20px;
  margin: 8px 0;
  display: inline-block;
  border: 1px solid #ccc;
  box-sizing: border-box;
}

button {
  background-color: #04AA6D;
  color: white;
  padding: 14px 20px;
  margin: 8px 0;
  border: none;
  cursor: pointer;
  width: 100%;
}

button:hover {
  opacity: 0.8;
}

.cancelbtn {
  width: auto;
  padding: 10px 18px;
  background-color: #f44336;
}

.imgcontainer {
  text-align: center;
  margin: 24px 0 12px 0;
}

img.avatar {
  width: 40%;
  border-radius: 50%;
}

.container {
  padding: 16px;
}

span.psw {
  float: right;
  padding-top: 16px;
}

/* Change styles for span and cancel button on extra small screens */
@media screen and (max-width: 300px) {
  span.psw {
     display: block;
     float: none;
  }
  .cancelbtn {
     width: 100%;
  }
}
</style>
</head>
<body>

<h2>Login Form</h2>

<form:form class="form" method="POST" action="${context}/auth/login" modelAttribute="loginDTO">
  <div class="imgcontainer">
    <img src="https://static.wixstatic.com/media/bd2c79_0c5dd9ff…m_0.66_1.00_0.01,enc_auto/SAS_Techstudio_Logo.png" alt="Sas TechStudio" class="avatar">
  </div>

  <div class="container">
  <label for="uname"><b>Company Code</b></label>
    <form:input type="text" placeholder="Enter Company Code" path="companyCode" required="true" />
    <label for="uname"><b>Username</b></label>
    <form:input type="text" placeholder="Enter Username" path="userName" required="true" />
    
	<form:hidden path="appName"/>
    <label for="psw"><b>Password</b></label>
    <form:input type="password" placeholder="Enter Password" path="password" required="true"/>
   <c:if test="${not empty error_message}">
    <p style="text-align:center"><strong>${error_message}</strong></p>
	</c:if>  
    <button type="submit">Login</button>
    
  </div>

  <div class="container" style="background-color:#f1f1f1">
    
  </div>
</form:form>

</body>
</html>
