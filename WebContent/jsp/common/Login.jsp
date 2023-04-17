<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
 
<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

if (CF == null) { 
	CF = new CommonFunctions();  
	CF.setRequest(request);
}
String strLogo = (String) session.getAttribute("ORG_LOGO"); 
//System.out.println("strLogo ===> " + strLogo);
if (strLogo == null) {
	strLogo = CF.getOrgLogo(request, CF); 
} 

String strLogoSmall = (String) session.getAttribute("ORG_LOGO_SMALL");
String strUITheme = CF.getStrUI_Theme();
if (strLogoSmall == null) {
	strLogoSmall = CF.getOrgLogoSmall(request, CF);
} 

/* if(strLogoSmall != null && !strLogoSmall.equals("")) {
	strLogo = CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE_SMALL+"/"+strLogoSmall;
} else if(strLogoSmall != null && !strLogoSmall.equals("")) {
	strLogo = CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo;
} */

strLogo = CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo;

String isSuspended = CF.getCheckClientStatus(request, CF);

String isSserviceTerminated = CF.getCheckServiceStatus(request, CF);
String isUnderImplementation = CF.getCheckImplementationStatus(request, CF);
%>

<% if(strUITheme != null && strUITheme.equals("1")) { %>
	<div class="top-content" style="background-size: cover;background-image: url(images1/login-background1.jpg);"><!-- background-color: #38789C; -->
<% } else { %>
	<div class="top-content" style="background-size: cover;background-image: url(images1/login-background.jpg);"><!-- background-color: #38789C; -->
<% } %>

		<%!
	        String showMessage(String str) {
	        	if(str!=null) {
	        		return str;
	        	} else {
	        		return "";
	        	}
	        } 
	        %>
	    
            <div class="inner-bg">
                <div class="container">
                    <div class="row">
                        <%-- <div class="col-sm-8 col-sm-offset-2 text" style="padding: 30px 0px;">
                        	<div style="float: left; width: 50%; text-align: right;"><img src="images1/icons/icons/workrig_white.png" style="width: 70%;"></div>
                        	<div style="float: left; width: 49%; text-align: center;"><img style="max-height: 85px;" src="userImages/company_avatar_photo.png" class="lazy" data-original="<%=strLogo %>"></div>
                        </div> --%>
                        <div class="col-sm-8 col-sm-offset-2 text " style="text-align: center;">
                            <img src="images1/icons/icons/workrig_white.png" style="width: 44%">
                        </div>
                        <!-- <div class="col-sm-2" style="text-align: center; margin-left: -80px; margin-top: -33px;">
                            <img src="images1/icons/beta-ribbon.png">
                        </div>
                        <div class="col-sm-6" style="text-align: center; margin-left: 150px;">
                            <img src="images1/icons/icons/workrig_white.png" style="width: 44%">
                        </div> -->
                    </div>
                    <div class="row">
                    	<div class="col-sm-3 col-md-3 col-lg-4"></div>
                        <div class="col-sm-6 col-md-6 col-lg-4 form-box" style="padding: 0px 0px;margin-top:0px !important;">
                        	<div class="form-top">
                        		<div class="form-top-left">
  									<% if(uF.parseToBoolean(isSuspended)) { %>
  										<h3 style="float: left; width: 110%; padding-top: 11px;">Temporary Termination of Service!</h3>
  									<% } else if(uF.parseToBoolean(isSserviceTerminated)) { %>
  										<h3 style="float: left; width: 110%; padding-top: 11px;">Welcome to Workrig!</h3>
  									<% } else { %>
  										<!-- <img src="images1/icons/beta-ribbon.png" style="float: left; width: 60px;"> -->
  										<h3 style="float: left; width: 76%; padding-top: 11px;">Welcome to Workrig!</h3>
  									<% } %>
                        			<!-- <h3>Welcome to Workrig!</h3> -->
                        		</div>
                        		<div class="form-top-right">
                        			<!-- <i class="fa fa-key"></i>  -->
                        			<i class="fa fa-key"></i>
                        			<!-- <i class="glyphicon glyphicon-key"></i> -->
                        		</div>
                            </div>
                            <% if(uF.parseToBoolean(isSuspended)) { %>
                            <div class="form-bottom">
			                    <form role="form" action="Login.action" method="post" class="login-form">
			                    	<div class="form-group">
			                    		Dear Customer,
										<br/><br/>
										We regret to inform you that the Cloud Service for this account has been suspended temporarily 
										due to non-payment of invoice (s). Please get in touch with your administrator or get in touch with us 
										at <a href="mailto:support@workrig.com">support@workrig.com</a>/ <a href="mailto:accounts@workrig.com">accounts@workrig.com</a> to resolve the issue and continue accessing the software again.
			                        </div>
						            <div style="padding-top: 25px;">
						                We are sorry for the inconvenience met!
						            </div>
			                    </form>
		                    </div>
		                    <% } else if(uF.parseToBoolean(isSserviceTerminated)) { %>
		                    <div class="form-bottom">
			                    <form role="form" action="Login.action" method="post" class="login-form">
			                    	<div class="form-group">
										<br/><br/>
										<div style="padding-top: 25px; font-size: 32px;">
							                Service Terminated!
							            </div>
							            <br/><br/>
							            <br/><br/>
			                        </div>
						            
			                    </form>
		                    </div>
		                    
		                    <% } else if(uF.parseToBoolean(isUnderImplementation)) { %>
		                    <div class="form-bottom">
			                    <form role="form" action="Login.action" method="post" class="login-form">
			                    	<div class="form-group">
										<br/><br/>
										<div style="padding-top: 25px; font-size: 32px;">
							                Under Implementation.
							            </div>
							            <br/><br/>
							            <br/><br/>
			                        </div>
						            
			                    </form>
		                    </div>
		                    
		                    <% } else { %>
		                    	<div class="form-bottom">
				                    <form role="form" action="Login.action" method="post" class="login-form">
				                    	<span style="color:red;"><%=showMessage((String)request.getAttribute("MESSAGE")) %></span>
				                    	<div class="form-group">
				                    		<label class="sr-only" for="form-username">Username</label>
				                        	<input type="text" name="username" placeholder="Username" class="form-username form-control" id="form-username" required>
				                        </div>
				                        <div class="form-group">
				                        	<label class="sr-only" for="form-password">Password</label>
				                        	<input type="password" name="password" placeholder="Password" class="form-password form-control" id="form-password" required>
				                        </div>
				                        <div class="form-group">
					                        <s:checkbox  name="isRemember" label="Remember Me"/>
					                        <br/>
					                        <s:hidden name="loginType" value="1" />
							                <%-- <s:radio name="loginType" list="#{'1':' Employee '}" value="1" /> --%> <!-- , '2':' Customer ' -->
				                        </div>
				                        <button type="submit" class="btn btn-primary">Sign in!</button>
				                        <a href="ForgotPassword.action">I forgot my password</a><br>
							            <div style="padding-top: 25px;">
							                For any further information about the software, please get in touch with <a href="mailto:info@workrig.com">info@workrig.com</a> 
							                or complete the <a href="http://www.workrig.com/contact/" target="_blank">form here</a>. Our team will get back to you shortly.
							            </div>
				                    </form>
			                    </div>
		                    <% } %>
                        </div>
                        <div class="col-sm-3 col-md-3 col-lg-4"></div>
                    </div>
                </div>
            </div>
        </div>
        
	<script type="text/javascript" src="scripts/jquery.lazyload.js"></script>
	<script type="text/javascript">
		$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	</script>
	