<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="context" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Create | SAS Techstudio SSO</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
<!--===============================================================================================-->
	<link rel="icon" type="image/png" href="https://static.wixstatic.com/media/bd2c79_0c5dd9ffd2fd4f3eaeeba6aa250b7ebf~mv2.png/v1/fill/w_106,h_113,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/SAS_Techstudio_Logo.png"/>
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="${context}/static/vendor/bootstrap/css/bootstrap.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="${context}/static/fonts/font-awesome-4.7.0/css/font-awesome.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="${context}/static/vendor/animate/animate.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="${context}/static/vendor/css-hamburgers/hamburgers.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="${context}/static/vendor/select2/select2.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="${context}/static/css/util.css">
	<link rel="stylesheet" type="text/css" href="${context}/static/css/main.css">
<!--===============================================================================================-->


<style>

</style>
</head>
<body>

	<div class="limiter">
		<div style="    background: #db2228;" class="container-create100">
			<div class="wrap-create100">
				<div class="create100-pic js-tilt" data-tilt>
					<img style="height:75%" src="https://static.wixstatic.com/media/bd2c79_0c5dd9ffd2fd4f3eaeeba6aa250b7ebf~mv2.png/v1/fill/w_106,h_113,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/SAS_Techstudio_Logo.png" alt="IMG">
				</div>

				<form:form id="create-form" class="create100-form validate-form" method="POST" action="${context}/auth/updatePassword" modelAttribute="createPasswordDto">

					<span class="create100-form-title">
						Create Password
					</span>

					<form:input type="hidden" path="userName"/>
					<form:input type="hidden" path="companyCode"/>
				    <form:input type="hidden" path="otp"/>

					<div class="wrap-input100 validate-input" data-validate = "Valid password is required: ex@abc.xyz">
						<form:input class="input100" type="text"  path="newPassword"  placeholder="Enter newPassword" required="true" />
						<span class="focus-input100"></span>
						<span class="symbol-input100">
							<i class="fa fa-envelope" aria-hidden="true"></i>
						</span>
						<c:if test="${not empty error_message_newPassword}">
                                                					 	<br>
                                                    					<p style="text-align:center"><strong>${error_message_newPassword}</strong></p>
                                                    					<br>
                                                					  </c:if>
					</div>

					<div class="wrap-input100 validate-input" data-validate = "Valid confirmPassword is required: ex@abc.xyz">
                    						<form:input class="input100" type="text"  path="confirmPassword"  placeholder="Enter confirmPassword" required="true" />
                    						<span class="focus-input100"></span>
                    						<span class="symbol-input100">
                    							<i class="fa fa-envelope" aria-hidden="true"></i>
                    						</span>
                    						<c:if test="${not empty error_message_confirmPassword}">
                                                                    					 	<br>
                                                                        					<p style="text-align:center"><strong>${error_message_confirmPassword}</strong></p>
                                                                        					<br>
                                                                    					  </c:if>
                    					</div>

					<div class="container-create100-form-btn">

						<button class="create100-form-btn">
							Create
						</button>
						 <c:if test="${not empty error_message}">
					 	<br>
    					<p style="text-align:center"><strong>${error_message}</strong></p>
    					<br>
					  </c:if>
					  <c:if test="${not empty success_message}">
                      					 	<br>
                          					<p style="text-align:center"><strong>${success_message}</strong></p>
                          					<br>
                      					  </c:if>
					</div>

				</form:form>
			</div>
		</div>
	</div>




<!--===============================================================================================-->
	<script src="${context}/static/vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->
	<script src="${context}/static/vendor/bootstrap/js/popper.js"></script>
	<script src="${context}/static/vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
	<script src="${context}/static/vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
	<script src="${context}/static/vendor/tilt/tilt.jquery.min.js"></script>
	<script src="https://malsup.github.io/jquery.blockUI.js"></script>

	<script >
		$('.js-tilt').tilt({
			scale: 1.1
		});
		$("#create-form").submit(function() {

$.blockUI({

                    // blockUI code with custom
                    // message and styling
                    message: "<h3>Loading...<h3>",
                    css: { color: 'green', borderColor: 'green' }
                });
		});
	</script>
<!--===============================================================================================-->
	<script src="${context}/static/js/main.js"></script>

</body>
</html>