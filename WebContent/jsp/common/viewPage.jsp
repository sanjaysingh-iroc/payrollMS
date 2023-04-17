<!DOCTYPE html>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%> 
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%
    String strPage = (String) request.getAttribute("PAGE");
    String strMenu = (String) session.getAttribute("MENU");
    String strSubMenu = (String) session.getAttribute("SUBMENU");  
    String strTitle = (String) request.getAttribute("TITLE");
    boolean loginFlag = true;  
     
    /* System.out.println("strPage ************ ===>> " + strPage);
    System.out.println("strMenu ===>> " + strMenu);
    System.out.println("strSubMenu ===>> " + strSubMenu);
    System.out.println("strTitle ===>> " + strTitle);  */
     
    if (strPage == null) {
    	strPage = "Login.jsp";
    }
    
    if (strTitle == null) {
    	loginFlag = false;
	    //strTitle = "Enterprise Human Capital & Payroll Management Solution";
	    strTitle = "Workrig | Human Capital Management";
    }
    
    if (strMenu == null && (strPage.equalsIgnoreCase("/jsp/common/ForgotPassword.jsp") || strPage.equalsIgnoreCase("/jsp/common/CommonClock.jsp"))) {
    	strMenu = "PreMenu.jsp";
    }else if(strMenu == null && !strPage.equalsIgnoreCase("/jsp/common/ForgotPassword.jsp") && strPage.equalsIgnoreCase("/jsp/common/CommonClock.jsp")){
    	strPage = "Login.jsp";
    }
    
    if (strMenu == null) {
	    strPage = "Login.jsp";
	    strMenu = "PreMenu.jsp";
    }
    
    if (strSubMenu == null) {
    	strSubMenu = "PreSubMenu.jsp";
    }
    
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
    %>
<html>
<head>
    
<style> 
       
.longText{
  width: 275px;
  height: 650px;
  margin: auto;
  margin-bottom: 2em;
  /* background-color: #e9e9e9; */
  border-radius: 2px;
  /* text-align: justify; */
}

#NOThidingScrollBar{
  overflow-y: scroll;
}

/*------THE TRICK------*/
#hidingScrollBar{
  overflow: hidden;
}
.hideScrollBar{
  /* width: 100%; */
  height: 100%;
  overflow: auto;
  margin-right: 14px;
  padding-right: 28px; /*This would hide the scroll bar of the right. To be sure we hide the scrollbar on every browser, increase this value*/
  padding-bottom: 15px; /*This would hide the scroll bar of the bottom if there is one*/
}

/*---------------------*/

/* p{
  padding: 1em;
  color: #444444;
} */
.column-6of12{
  float: left;
  min-height: 1px;
   width: 50%;
  -webkit-box-sizing: border-box; /* Safari/Chrome, other WebKit */
  -moz-box-sizing: border-box;    /* Firefox */
  box-sizing: border-box;         /* Opera/IE 8+ */
}
@media (max-width: 796px) {
  .column-6of12{
    width: 100%;
  }
  
  
  
.flexcroll{ width:200px;
            height:100px;
            overflow:scroll;
           }
.flexcroll{
    scrollbar-face-color: #367CD2;
    scrollbar-shadow-color: #FFFFFF;
    scrollbar-highlight-color: #FFFFFF;
    scrollbar-3dlight-color: #FFFFFF;
    scrollbar-darkshadow-color: #FFFFFF;
    scrollbar-track-color: #FFFFFF;
    scrollbar-arrow-color: #FFFFFF;
}

/* Let's get this party started */
.flexcroll::-webkit-scrollbar {
    width: 5px;
    height:5px;
}
 
/* Track */
.flexcroll::-webkit-scrollbar-track {
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3); 
    -webkit-border-radius: 10px;
    border-radius: 10px;
}
 
/* Handle */
.flexcroll::-webkit-scrollbar-thumb {
    -webkit-border-radius: 10px;
    border-radius: 10px;
   /*  background: rgba(255,0,0,0.8);  */
   background: rgb(85, 102, 108);
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.5); 
}
</style>


 <meta charset="utf-8">
        <!-- <meta http-equiv="X-UA-Compatible" content="IE=edge"> --> 
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1"> 
        <!-- <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport"> -->
        <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
        <!-- <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; connect-src 'self'; img-src 'self'; style-src 'self';"> -->
        <!-- child-src 'none'; object-src 'none' --> 
        <!-- <meta http-equiv="Content-Security-Policy" content="connect-src 'self'; sandbox allow-forms allow-same-origin allow-scripts allow-popups, allow-modals, allow-orientation-lock, allow-pointer-lock, allow-presentation, allow-popups-to-escape-sandbox, and allow-top-navigation;"> -->
         <!-- created by seema -->
        <title class="notranslate"><%=strTitle%></title>
        <!-- created by seema -->
        <!-- <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport"> -->
        <link rel="icon" href="images1/icons/icons/w_green.png">
        <!-- <link rel="stylesheet" href="css/compressed-1.css"> -->
       <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
		<% if(strUITheme != null && strUITheme.equals("2")) { %>
			<link rel="stylesheet" href="dist/css/AdminLTENew_two.min.css">
	        <link rel="stylesheet" href="dist/css/skins/_all-skinsNew_two.min.css">
	         <link rel="stylesheet" href="bootstrap/css/bootstrapNew_two.css">
	         <link rel="stylesheet" href="dist/css/skins/close-popupNew_two.css">
	         
		<% } else if(strUITheme != null && strUITheme.equals("1")) { %>
			<link rel="stylesheet" href="dist/css/AdminLTENew.min.css">
	        <link rel="stylesheet" href="dist/css/skins/_all-skinsNew.min.css">
	         <link rel="stylesheet" href="bootstrap/css/bootstrapNew.css">
	         <link rel="stylesheet" href="dist/css/skins/close-popupNew.css">
	         
		<% } else { %>
	        <link rel="stylesheet" href="dist/css/AdminLTE.min.css">
	        <link rel="stylesheet" href="dist/css/skins/_all-skins.min.css">
	         <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
	        <link rel="stylesheet" href="dist/css/skins/close-popup.css"> 
	        
        <% } %>
        
        <link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
		<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
        <link href="js_bootstrap/timepicker/bootstrap-timepicker.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
        <link rel="stylesheet" type="text/css" href="js/datatables_new/buttons.dataTables.min.css" />
        <link rel="stylesheet" type="text/css" href="js/datatables_new/dataTables.bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="js/datatables_new/responsive.bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="css/jquery.poplight.css"/>
		<link rel="stylesheet" type="text/css" href="js_bootstrap/bootstrap-datetimepicker/bootstrap-datetimepicker.min.css"/>
		<!-- <link rel="stylesheet" href="css/font-awesome/4.7.0/css/font-awesome.min.css"> 
		<link rel="stylesheet" href="css/font-awesome/font-awesome.min.css">		-->
		<!-- <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.13/css/all.css" integrity="sha384-DNOHZ68U8hZfKXOrtjWvjxusGo9WQnrNx2sqG0tfsghAvtVlRW3tvkXWZh58N9jp" crossorigin="anonymous"> -->
		<!-- <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"> --> 
		<link rel="stylesheet" href="css/font-awesome-4.7.0/css/font-awesome.min.css">
		<link rel="stylesheet" href="css/font-awesome-4.7.0/css/font-awesome.css">
        <link rel="stylesheet" href="css/font-awesome/font-awesome-animation.min.css">
        <link rel="stylesheet" href="bootstrap/css/bootstrap-glyphicons.css"><!-- Created by Dattatray Date:25-June-2021 -->
		
		
		
		<% if(strPage.equalsIgnoreCase("Login.jsp")) { %>
    		<% if(strUITheme != null && strUITheme.equals("2")) { %>
        		<link rel="stylesheet" href="css/login-style.css">
        	<% } else { %>
        		<link rel="stylesheet" href="css/login-styleNew.css">
        	<% } %>
    		
        <% } else { %>
        	<% if(strUITheme != null && strUITheme.equals("2")) { %>
        		<link rel="stylesheet" href="css/new_customNew_two.css">
        		<link rel="stylesheet" href="css/login-form-elementsNew_two.css" id="login-form-elementsNew_two">
        		<link rel="stylesheet" href="bootstrap/css/New_two.css">
        	<% } else if(strUITheme != null && strUITheme.equals("1")) { %>
	    		<link rel="stylesheet" href="css/new_customNew.css">
	    		<link rel="stylesheet" href="css/login-form-elementsNew.css">
	    		<link rel="stylesheet" href="bootstrap/css/New.css">
	    	<% } else { %>
        		<link rel="stylesheet" href="css/new_custom.css">
        		<link rel="stylesheet" href="css/login-form-elements.css">
        	<% } %>
        <% } %>
    </head>
    <body class="hold-transition skin-blue sidebar-mini sidebar-collapse">
        <% if(loginFlag) {
        	String PRODUCTTYPE = (String) session.getAttribute(IConstants.PRODUCT_TYPE);
        %>
        <div class="wrapper">
        <!-- created by seema -->
            <header class="notranslate main-header" style="width: 100% !important;position: fixed !important;">
            <!-- created by seema -->
                <!-- Logo -->
                
                <% if(PRODUCTTYPE == null || PRODUCTTYPE.equals("2")) { %>
                <a class="logo" title="My Home" href="Login.action?role=3&userscreen=myhome">
                    <!-- mini logo for sidebar mini 50x50 pixels -->
                    <span class="logo-mini">
	                    <!-- <img src="images1/icons/beta-ribbon.png" style="position: absolute; margin-left: -5px; width: 35px;">
	                    <img src="images1/icons/icons/mini-workrig.png" style="width: 40px;"> -->
	                    <img src="images1/icons/icons/mini-workrig.png" style="width: 50px;"> 
                    </span>
                    <!-- logo for regular state and mobile devices -->
                    <span class="logo-lg">
	                    <!-- <img src="images1/icons/beta-ribbon.png" style="width: 50px; margin-top: -8px; margin-left: -81px;">
	                    <img src="images1/icons/icons/workrig_white.png" style="width: 90px;"> -->
	                    <img src="images1/icons/icons/workrig_white.png" style="width: 90px;">
                    </span> 
                </a>    
				<% } else if(PRODUCTTYPE == null || PRODUCTTYPE.equals("3")) { %>
				<a class="logo">
					<!-- mini logo for sidebar mini 50x50 pixels -->
                    <span class="logo-mini">
	                    <!-- <img src="images1/icons/beta-ribbon.png" style="position: absolute; margin-left: -5px; width: 35px;">
	                    <img src="images1/icons/icons/mini-taskrig.png" style="width: 30px; margin-left: 10px;"> -->
	                    <img src="images1/icons/icons/mini-taskrig.png" style="width: 45px;">
                    </span>
                    <!-- logo for regular state and mobile devices -->
                    <span class="logo-lg">
	                    <!-- <img src="images1/icons/beta-ribbon.png" style="width: 50px; margin-top: -8px; margin-left: -81px;">
	                    <img src="images1/icons/icons/taskrig_white.png" style="width: 90px;"> -->
	                    <img src="images1/icons/icons/taskrig_white.png" style="width: 90px;">
                    </span>
                </a>    
				<% } %>
                
                <!-- Header Navbar: style can be found in header.less -->
                <nav class="navbar navbar-static-top" role="navigation">
                    <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    </a>
                    <jsp:include page="Header.jsp" flush="true" />
                </nav>
            </header>
            <!-- Left side column. contains the logo and sidebar -->
            <aside class="main-sidebar" style="position: fixed;">
                <!-- sidebar: style can be found in sidebar.less -->
                <!-- created by seema -->
                <section class="notranslate sidebar">
                <!-- created by seema -->
                    <!-- Sidebar user panel --> 
                    
                    <!-- <span class="logo-mini"><img src="images1/icons/icons/mini-workrig.png" style="width: 40px;"></span>

                    <span class="logo-lg"><img src="images1/icons/icons/workrig_white.png" style="width: 90px;"></span> --> 
                    
                    <div class="user-panel main-header">
                        <%
                            String EMPNAME = (String) session.getAttribute(IConstants.EMPNAME);
                            UtilityFunctions uF = new UtilityFunctions();
                            String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
						%>
						<div class="pull-left image logo">
							<span class="logo-mini">
								<%
								if (EMPNAME != null) {
									boolean logoFlag = uF.isFileExist(CF.getStrDocSaveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+strLogo);
									if(logoFlag) {
								%>
							  		<img class="img-circle lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE_SMALL+"/"+strLogoSmall%>" />
								<%	} else { %>
							  		<img class="img-circle lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE_SMALL+"/"+strLogoSmall%>" />
							  	<%}
							    	} else {
							    %>
							    	<img class="img-circle lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE_SMALL+"/"+strLogoSmall%>" />
							    <% } %>
						    </span>
						    <span class="logo-lg">
						    	<%
								if (EMPNAME != null) {
									boolean logoFlag = uF.isFileExist(CF.getStrDocSaveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+strLogo);
									if(logoFlag) {
								%>
							  		<img class="img-circle lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
								<%	} else { %>
							  		<img class="img-circle lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
							  	<%}
							    	} else {
							    %>
							    	<img class="img-circle lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
							    <% } %>
						    </span>
                        
                            <%-- <%if(CF.getStrDocSaveLocation()!=null) { %>
                            <img class="img-circle lazy" height="100" width="100" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID) +"/"+ IConstants.I_100x100+"/"+ (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px"/>
                            <% } else { %>
                            <img class="img-circle lazy" height="100" width="100" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px"/>
                            <% } %> --%>
                            
                        </div>
                        <div class="pull-left info">
	                        <%-- <%
							if (EMPNAME != null) {
								boolean logoFlag = uF.isFileExist(CF.getStrDocSaveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+strLogo);
								if(logoFlag) {
							%>
						  		<img height="60" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
							<%	} else { %>
						  		<img height="60" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
						  	<%}
						    	} else {
						    %>
						    	<img height="60" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
						    <% } %> --%>
						    
                            <%-- <p><%=EMPNAME %></p> --%>
                        </div>
                    </div>
                    
                    <!-- search form -->
                    <!-- <form action="#" method="get" class="sidebar-form">
                        <div class="input-group">
                            <input type="text" name="q" class="form-control" placeholder="Search..." style="height: 35px !important;max-width: 160px !important;">
                            <span class="input-group-btn">
                            <button type="submit" name="search" id="search-btn" class="btn btn-flat"><i class="fa fa-search"></i></button>
                            </span>
                        </div>
                    </form> -->
                    <!-- /.search form -->
                    <!-- sidebar menu: : style can be found in sidebar.less -->
                    <ul class="sidebar-menu">
                        <jsp:include page="<%=strMenu %>" flush="true" />
                    </ul>
                </section>
                <!-- /.sidebar -->
            </aside>
            <% } %>
            <!-- Content Wrapper. Contains page content -->
            <% if(strPage.equalsIgnoreCase("Login.jsp")) { %>
            	<div class="content-wrapper" style="margin-left: 0px !important;">
            <% } else { %>
            	<div class="content-wrapper" style="padding-top: 3.8%; min-height: 700px;<%=(!loginFlag)? "margin-left: 0px;" : "" %> ">
            	<%-- <section class="content-header paddigtop2" id="pageTitleAndNaviTrail" style="position: fixed;">
			      <% String PAGETITLE_NAVITRAIL = (String) request.getAttribute("PAGETITLE_NAVITRAIL"); %>
			      <% if(PAGETITLE_NAVITRAIL != null && !PAGETITLE_NAVITRAIL.trim().equals("")) { %>
			      	<ol class="breadcrumb" style="position: fixed;width: 94.5%;margin-top: 2.5%;">
			      		<%=PAGETITLE_NAVITRAIL %>
			      	</ol>
			    <% } %>
			    </section> --%>
            <% } %>
            
                <!-- Main content -->
                <% if(strPage.equalsIgnoreCase("Login.jsp")) { %>
                <%}else{ %>
                <section class="content paddigtop0" style="margin-top: -5px !important;">
                <%} %>
                    <div class="row">
                      	<jsp:include page="<%=strPage%>"/>
                    </div>
                    <!-- /.row -->
                <% if(strPage.equalsIgnoreCase("Login.jsp")) { %>
                <%}else{ %>
                </section>
                <%} %>
                <!-- /.content -->
            </div>
            
            <!-- /.content-wrapper -->
            <%-- <% if(strPage.equalsIgnoreCase("Login.jsp")) { %>
            	
            <% } else { %> --%>
            	<%-- <footer class="main-footer" style="position: fixed;left: 0;bottom: 0;width: -moz-available;width: -webkit-fill-available;<%=(!loginFlag)? "margin-left: 0px !important;" : "" %> ">
	            	<div class="pull-right hidden-xs">
	                    <b>Version</b> 3.5 
	                </div>
	                <strong>Copyright &copy; 2017-2018 <a href="http://www.workrig.com">Workrig</a>.</strong> All rights reserved.
	            </footer> --%>
	            <!-- created by seema -->
	            <footer class="notranslate main-footer" style="position: fixed;left: 0;bottom: 0;width: -moz-available;width: -webkit-fill-available;<%=(!loginFlag)? "margin-left: 0px !important;" : "" %> ">
	            <!-- created by seema -->
	            	<div class="pull-right hidden-xs">
	                    <b>Version</b> 1.8
	                </div>
	                <strong>Copyright &copy; 2021-2022 <a href="http://www.workrig.com">Workrig</a>.</strong> All rights reserved.
	            </footer>
            <%-- <% } %> --%>
            
                
            <% if(loginFlag) { 
            	String PRODUCTTYPE = (String) session.getAttribute(IConstants.PRODUCT_TYPE); 
            	String BASEUSERTYPE = (String) session.getAttribute(IConstants.BASEUSERTYPE);
            	//if((BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.ADMIN)) || (BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.HRMANAGER)) || (BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.OTHER_HR))) {
            	if((BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.ADMIN)) || (BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.HRMANAGER))) {	
            %>
            <!-- Control Sidebar -->
            <aside class="control-sidebar control-sidebar-dark" id="rightSideBar" style="position:fixed"><!-- style="position:fixed" -->
            <div class="column-6of12">
   					    <div class="longText" id="hidingScrollBar">
					      <div class="hideScrollBar">
                <!-- Create the tabs -->
                <ul class="nav nav-tabs nav-justified control-sidebar-tabs">
                    <li><a href="#control-sidebar-home-tab" data-toggle="tab"><i class="fa fa-sliders"></i></a></li>
                    <li class="active"><a href="#control-sidebar-settings-tab" data-toggle="tab"><i class="fa fa-cogs"></i></a></li>
                </ul>
                <%
                    String []arrModules = (String[])request.getAttribute("arrModules");
                         
                    UtilityFunctions uF = new UtilityFunctions();
                    List alParentNavL = (List) session.getAttribute("alParentNavL");
                    Map hmChildNavL = (Map) session.getAttribute("hmChildNavL");
                    
                    List alParentNavR = (List) session.getAttribute("alParentNavR");
                    Map hmChildNavR = (Map) session.getAttribute("hmChildNavR");
                    Map hmNavigation = (Map) session.getAttribute("hmNavigation");
                    Map hmNavigationParent = (Map) session.getAttribute("hmNavigationParent");
                    
                    String []arrEnabledModules = CF.getArrEnabledModules();
                    String []arrAllModules = CF.getArrAllModules();
                    String strProductType = (String) session.getAttribute(IConstants.PRODUCT_TYPE);
                %>
                
                <!-- Tab panes -->
                <div class="tab-content">
                    
                    
                    
                    <!-- Home tab content -->
                    <div class="tab-pane" id="control-sidebar-home-tab">
                        <!-- <h3 class="control-sidebar-heading">Recent Activity</h3> -->
                        <ul class="control-sidebar-menu">
                            <li style="padding-left:8px;">
                                <a href="javascript::;">
                                    <i class="menu-icon fa  fa-info-circle bg-orange"></i>
                                    <div class="menu-info">
                                        <h4 class="control-sidebar-subheading">My Software Version</h4>
                                        <%if(arrModules != null && ArrayUtils.contains(arrModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
                                        <p>Workrig - Version 1.8</p>
                                        <% } else { %>
                                        <p>Workrig - Version 1.8</p>
                                        <% } %>
                                    </div>
                                </a>
                            </li>
                            <li style="padding-left: 20px;">
                                <i class="menu-icon fa  fa-file-text-o bg-light-blue"></i>
                                <div class="menu-info">
                                    <h4 class="control-sidebar-subheading">My software licence information</h4>
                                </div>
                            </li>
                            
                            <hr style="margin-top:15px;width: 187px;">
                            
                            <div class="menu-info" style="margin-top: 10px;">
                                    <% if(PRODUCTTYPE == null || PRODUCTTYPE.equals("2")) { %>
                                    	<p style="line-height: 15px; margin-left: -25px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_HUMAN_CAPITAL_MANAGEMENT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Human Capital Management (HCM)</p>
                                        <p style="line-height: 15px; margin-left: 10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) ? "menu-icon glyphicon glyphicon-ok-circle cp" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;OnBoarding, Hiring, Exit</p>
                                        <p style="line-height: 15px; margin-left: 10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0) ? "menu-icon glyphicon glyphicon-ok-circle cp" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Career Development &amp; Planning</p>
                                        <p style="line-height: 15px; margin-left: 10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) ? "menu-icon glyphicon glyphicon-ok-circle cp" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Performance Management</p>
                                        <p style="line-height: 15px; margin-left: -25px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Compensation, Payroll, Tax</p>
                                        <p style="line-height: 15px; margin-left: -25px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Absence Management</p>
                                        <p style="line-height: 15px; margin-left: -25px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_STATUORY_COMPLIANCE+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Statutory Compliances</p>
                                        <p style="line-height: 15px; margin-left: -25px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_SCHEDUING+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Time &amp; Scheduling</p>
                                        <p style="line-height: 15px; margin-left: 10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0) ? "menu-icon glyphicon glyphicon-ok-circle cp" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Time &amp; Attendance</p>
                                        <p style="line-height: 15px; margin-left: 10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_SCHEDUING+"")>=0) ? "menu-icon glyphicon glyphicon-ok-circle cp" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Roster module</p>
                                        <p style="line-height: 15px; margin-left: -25px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_DOCUMENT_MGMT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Document Management</p>
										
                                    <% } else if(PRODUCTTYPE == null || PRODUCTTYPE.equals("3")) { %>
                                    	<p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PROJECT_MANAGEMENT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Project &amp; Portfolio Management</p>
	                                    <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIMESHEET_MANAGEMENT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Timesheet Management</p>
	                                    <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_RESOURCE_MANAGEMENT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Resource Management</p>
	                                    <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Hiring Management</p>
	                                    <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PROJECT_BILLING_MGMT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Billing &amp; Receipts</p>
	                                    <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CUSTOMER_MANAGEMENT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Customer Management</p>
	                                    <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_DOCUMENT_MGMT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Document Management</p>
	                                    <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="<%= ((arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) ? "menu-icon glyphicon glyphicon-ok bg-green" : "menu-icon glyphicon glyphicon-remove bg-red") %>" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;Absence Management</p>
                                    <% } %>
                                    
                                    <%--  --%>
                                </div>
                                
                                <hr style="margin-top:15px;width: 187px;">
                             <li style="padding-left: 15px;     margin-top: 23px;">
                                <i class="menu-icon fa  fa fa-ellipsis-v bg-red"></i>
                                <div class="menu-info">
                                    <h4 class="control-sidebar-subheading">Your plan allows</h4>
                                     
                                     	<p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;<%=uF.showData(CF.getStrMaxOrganisation(),"N/A") %> Organisations</p>
                                        <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;<%=uF.showData(CF.getStrMaxLocations(),"N/A") %> Locations</p>
                                        <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;<%=uF.showData(CF.getStrMaxAdmin(),"N/A") %> Sub administrators</p>
                                        <p style="line-height: 15px; margin-left: -10px; padding: 5px 2px; height: 35px;"> <i class="" style="width: 18px; height: 18px; line-height: 18px;"></i>&nbsp;&nbsp;<%=uF.showData(CF.getStrMaxEmployee(),"N/A") %> Employees</p>
                                     
                                </div>
                            </li>
                        </ul>
                        <!-- /.control-sidebar-menu -->
                    </div>
                    
                    
                    
                    
                    
                    <!-- /.tab-pane -->
                   
                    
                    
                     <!-- Stats tab content -->
                    <div class="tab-pane" id="control-sidebar-stats-tab flexcroll">Stats Tab Content</div>
                    
                    
                    <!-- /.tab-pane -->                    
                    <!-- Settings tab content -->
                    <div id="control-sidebar-settings-tab" class="tab-pane active ">
                       
                       
      
      
       <h3 class="control-sidebar-heading" style="margin-left: 45px;">Control Panel</h3>
                        <ul class="control-sidebar-menu">
                            <%
                                List alList = (List) hmChildNavL.get("1");
                                if(alList==null)alList=new ArrayList();
                                for(int i=0; i<alList.size(); i++) {
                                	Navigation nav = (Navigation)alList.get(i);
                                %>
                            <li>
                                <a href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=<%=nav.getStrNavId() %>&toPage=<%=uF.showData(nav.getStrLabelCode(), "-")  %>">
                                    <i class="menu-icon fa fa-support bg-yellow"></i>
                                    <!-- <i class="menu-icon bg-yellow"><img src="images1/icons/fancy-globe.png" style="margin-top: -5px;"> </i> -->
                                    <div class="menu-info">
                                      <h4 class="control-sidebar-subheading"><%=nav.getStrLabel() %></h4>
                                      <p><%=uF.showData(nav.getStrDescription(), "-") %></p>
                                    </div>
                                </a>
                            </li>
                            <% } %>
                            <%-- <%
                                for(int i=0; i<alParentNavL.size(); i++){
                                 	Navigation nav = (Navigation)alParentNavL.get(i);
                                 	if(uF.parseToInt(nav.getStrNavId())!=46){
                                 		continue;
                                 	}
                            		%>
                                 <li>
                                   <a href="javascript::;">
                                     <i class="menu-icon fa fa-user bg-yellow"></i>
                                     <div class="menu-info">
                                       <h4 class="control-sidebar-subheading"><%=nav.getStrLabel() %></h4>
                                       <p><%=uF.showData(nav.getStrDescription(), "-") %></p>
                                     </div>
                                   </a>
                                 </li>
                                <% } %> --%>
                        </ul>
                        <!-- /.control-sidebar-menu -->
                    </div>
                    
                    
                </div>
                
                </div></div></div>
            </aside>
            <div class="control-sidebar-bg"></div>
            <% }
            } %>
            <% if(loginFlag) { %>
        </div>
        <% } %>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
       
			<!-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> -->
        	<script type="text/javascript" src="js/jquery.sparkline.js"></script>
	        <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>	
	        <script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
	        <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
	        <script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
			<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
	        <script type="text/javascript" src="scripts/waypoints.js"></script> 
	        <script type="text/javascript" src="js/datatables_new/jquery.dataTables.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/pdfmake.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/buttons.print.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/jszip.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/dataTables.bootstrap.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/dataTables.buttons.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/buttons.flash.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/vfs_fonts.js"></script>
	        <script type="text/javascript" src="js/datatables_new/buttons.html5.min.js"></script>
			<script type="text/javascript" src="js/datatables_new/dataTables.responsive.min.js"></script>
			<script type="text/javascript" src="js/datatables_new/buttons.bootstrap.min.js"></script>
			<script type="text/javascript" src="js/datatables_new/responsive.bootstrap.min.js"></script>
			<script type="text/javascript" src="scripts/jquery.lazyload.js"></script>
	        <script type="text/javascript" src="dist/js/app1.min.js"></script>
			<script type="text/javascript" src="scripts/_rating/js/jquery.raty.min.js"> </script>
	        <script type="text/javascript" src="scripts/organisational/jquery.jOrgChart.js"></script>
	        <script type="text/javascript" src="scripts/customAjax.js" ></script>
	        <script type="text/javascript" src="js/jquery.rateyo.min.js"></script>
	        <script type="text/javascript" src="js/moment.js"></script>
	        <script type="text/javascript" src="js_bootstrap/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js"></script>
	        <script type="text/javascript" src="js_bootstrap/Jcrop/jquery.Jcrop.min.js"></script>
	        <script type="text/javascript">
	        $(".selectedL").parent().addClass("active");
	        $(".selectedL").next('.treeview-menu').css('display','block');
	        $(".selectedL").next('.treeview-menu').addClass("menu-open");
	        

	        $(document).on('mouseover', 'input, select, textarea',function(){
	        	$(this).next('.hint').css("visibility", "visible");
	        });
	        $(document).on('mouseout', 'input, select, textarea',function(){
	        	$(this).next('.hint').css("visibility", "hidden");
	        });

	        $(document).on('click', '.products-list a',function(){
	        	$('a').removeClass("activelink");
	        	$(this).addClass("activelink");
	        });

	        $(window).on('load',function(){
	        	$("input[type='number']").prop('step','any');
	        	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	        });

	        $('body').on('onkeypress','input[type="number"]',function(evt){
	        	var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	               return false;
	            }
	            return true;
	        });


	        var nowTemp = new Date();
	        var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

	        jQuery.browser = {};
	        (function () {
	            jQuery.browser.msie = false;
	            jQuery.browser.version = 0;
	            if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
	                jQuery.browser.msie = true;
	                jQuery.browser.version = RegExp.$1;
	            }
	        })();

	        $(function(){ 	
	        	
	        	loadLazyImages();
	        	
	        	$("body").on("click",".fc-button",function(){
	        		if($(this).hasClass("fc-month-button")){
	        			setTimeout(function(){ $(".fc-time").hide();}, 500);
		        	}else{
		        		setTimeout(function(){ $(".fc-time").show();}, 500);
		        	}
	        	});
	        	
	        	$("input[type='number']").keydown(function(e) {
	        		var n = (window.Event) ? e.which : e.keyCode;
	        		if(n==38 || n==40) return false;
	        	});
	        	
	        	$("input[type='number']").attr("onmousewheel", "return false;");

	        	if($(".fc-month-button").hasClass("fc-state-active")){
	        		$(".fc-time").hide();
	        	}
	        	
	        	$("body").on("click",".external-event input[type='checkbox']",function(){
	        		$(".fc-time").hide();
	        	});
	        	
	        	$("body").on('click','#closeButton',function(){
	        		$(".modal-dialog").removeAttr('style');
	        		$(".modal-bodyCP").height(400);
	        		$("#modalInfoCP").hide();
	        	});
	        	
	        	$("body").on('click','.close',function(){
	        		$(".modal-dialog").removeAttr('style');
	        		$(".modal-bodyCP").height(400);
	        		$("#modalInfoCP").hide();
	        	});
	        	
	        	var active = $(".active-workrig-user").html();
	        	$(".workrig-users-button>.btn-sm[for='"+active+"']").addClass("active");
	        	
	        	if($('body').hasClass('sidebar-collapse')){
	        		$(".treeview").removeClass("arrow_box");
	        		$(".treeview.active").addClass("arrow_box");
	        	}
	        	
	        	$('body').on('click','.sidebar-toggle',function(){
	        		if($('body').hasClass('sidebar-collapse')){
	        			$(".treeview").removeClass("arrow_box");	
	        			
	        		}else{
	        			$(".treeview").removeClass("arrow_box");
	        			$(".treeview.active").addClass("arrow_box");
	        			//$(".treeview.active").addClass("arrow_box");
	        		}
	        	});
				
	        	$('body').on('click','.sidebar-toggle,#open-dropdown',function(){
	        		loadLazyImages();
	        	});
	        		        	
	        	$(document).on("click",".nav-tabs li",function(e){
	        		//console.log(e);
	        		$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
	        			//console.log(options);
	        			//console.log(originalOptions);
	        			//console.log(jqXHR);
	        			//console.log("----------------");
	        			options.async = false;
	        		});
	        	});
	        	
	        	/* var currentRequests = {};
	        	$(document).on("click",".nav-tabs li",function(e){
	        		console.log(currentRequests);
	        		console.log("----------------");
	        		currentRequests.forEach(function(url){
	        			url.abort();
	        		});
	        		$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
	        			currentRequests[ options.url ] = jqXHR;
		        	});
	        	}); */
	        	$(document).find(".box-tools").parent().attr("data-widget","collapse-full");
	        
	        	$( document ).on('ajaxComplete',function() {
	        		$(document).find(".box-tools").parent().attr("data-widget","collapse-full");
	        		setTimeout(function(){ $(".fc-time").hide();}, 500);
	        		$("input[type='number']").keydown(function(e) {
		        		var n = (window.Event) ? e.which : e.keyCode;
		        		if(n==38 || n==40) return false;
		        	});
		        	
		        	$("input[type='number']").attr("onmousewheel", "return false;");
		        	
		        	//lazy load
		        	loadLazyImages();
	        	});
	        		        	
	        	$('body').on('click','.box-header', function(e) { 
	        	  var e_target = e.target;
	        	  if (e_target !== this){
	        		  if($(e_target).hasClass('box-title') || $(e_target).parent().hasClass('box-title') || $(e_target).attr('data-widget') === "collapse" || 
	        				  $(e_target).hasClass('fa-minus') || $(e_target).hasClass('fa-plus')){
	        			  
	        		  }else{
	        			  e.stopPropagation();
	        		  }
	        	  }
	        	});
	        	$('body').on('keydown','.readonly',function(e) {
        		    e.preventDefault();
        		});
	        	
	        	$('body').on('keydown','.no-press-enter',function(e) {
	        	    if(e.which == 13) {
	        	       return false;
	        	    }
	        	});
	        });
	        
	        function loadLazyImages(){
	        	$("img.lazy").each(function(i,element) {
	        	    if($(element).attr("src") !== $(element).attr("data-original")){
	        	    	$(element).lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	        	    }
	        	});
	        }
	        
	        function fadeForm(form_id){
	        	if($('#'+form_id).find('.there').length === 0){
	        		$('#'+form_id).prepend('<div class="there"><div id="ajaxLoadImage"></div></div>');
	        	}
	        } 

	        function unfadeForm(form_id){
	        	$("#"+form_id).find('.there').remove();
	        }

	        function isNumberKey(evt){
	            var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	               return false;
	            }
	            return true;
	         } 


	        function isOnlyNumberKey(evt) {
	            var charCode = (evt.which) ? evt.which : event.keyCode;
	            if ((charCode <= 31) || (charCode >= 48 && charCode <= 57)) {
	         		return true;
	            }
	            return false;
	         }

	        function clearField(elementId){
	        	document.getElementById(elementId).value = '';
	        }
	        
	        
	        
	        </script>
    </body>
</html>