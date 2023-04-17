<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

 
 
<%
	String strPage = (String) request.getAttribute("PAGE");
	String strMenu = (String) session.getAttribute("MENU");
	String strSubMenu = (String) session.getAttribute("SUBMENU");
	String strTitle = (String) request.getAttribute("TITLE");
	
	String productType = (String) request.getAttribute(IConstants.PRODUCT_TYPE);

	if (strPage == null) {
		strPage = "../common/Login.jsp";
	}

	if (strTitle == null) {
		if(productType != null && productType.equals("3")) {
			strTitle = "Project & Portfolio Management";
		} else {
			strTitle = "Human Capital Management";
		}
	}
	     
	if (strMenu == null && strPage.equalsIgnoreCase("/jsp/common/ForgotPassword.jsp")) {
		strMenu = "../common/PreMenu.jsp";
	}else if(strMenu == null && !strPage.equalsIgnoreCase("/jsp/common/ForgotPassword.jsp")){
		strPage = "../common/Login.jsp";
	}


	if (strMenu == null) {
		strPage = "../common/Login.jsp";
		strMenu = "../common/PreMenu.jsp";		
	}

	
	if (strSubMenu == null) {
		strSubMenu = "../common/PreSubMenu.jsp";
	}
%>


  
  
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>


 
<jsp:include page="../common/Links.jsp" flush="true" />
<title><%=strTitle%></title>
<script type="text/javascript">

onload=function(){
	
    document.getElementById('the_div').className="nothere";
  
   document.getElementById("ajaxLoadImage").style.visibility="hidden";
   // document.getElementById("ajaxLoadImage").style.backgroundImage="none";
      
};


</script>


<jsp:include page="../common/Links.jsp" flush="true" />

</head>

<body>

 <div id="wrapper">
         <div class="container">
       
	       <div id="the_div" class="there">
			   <div id="ajaxLoadImage"></div>
			</div>
  
         	<jsp:include page="../common/Header.jsp" flush="true" />
			<jsp:include page="<%=strMenu %>" flush="true" />
			<div class="leftbox reportWidth">
				<div class="msg_error">
         		<span>
         		<strong>Server Error</strong><br/>
         		The server could not find the request which you are looking for. <br/>
         		Either the server is updating or server is processing other requests.<br/>
         		Please try refreshing this page and if problem persists please send an email to info@taskrig.com<br/>
         		</span> 
			</div> 
			</div>
        	<jsp:include page="../common/Footer.jsp" flush="true" />
         
      </div> 
       </div>     
         
</body>

</html>