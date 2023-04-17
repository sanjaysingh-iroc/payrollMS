<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.mail.MailCountClass"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%> 
<%@ page import="com.konnect.jpms.util.IPages"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%@page import="java.util.*"%>  
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
 
<style type="text/css"> 
.hideScrollBar {
  /* width: 100%; */ 
  height: 100%;
  overflow: auto;
  margin-right: 14px;
  padding-right: 28px; /*This would hide the scroll bar of the right. To be sure we hide the scrollbar on every browser, increase this value*/
  padding-bottom: 15px; /*This would hide the scroll bar of the bottom if there is one*/
} 

.grow { /*background: #3c8dbc !important;color: #f6f6f6 !important;*/}
/*.grow:hover {
  height: 50px;
}*/

grow:hover {
  height: 400px;
  overflow-y:auto;
 }
  
.grow {
   transition: all 0.2s ease-in-out;
  height: 20px;
  transition: height 0.5s;
  text-align: center;
  /*overflow: hidden;*/
  overflow-y: scroll;
    height: 20px;
}
.grow:hover { 
 overflow-y: scroll;
 height: 50px;
 /*max-height: 500px;
 min-height: 50px;*/
}

.navbar-nav>.notifications-menu>.dropdown-menu>li .menu>li>a {
	white-space: break-word;
}

.control_panel {
	background: url("images1/icons/icons/screw_drawer_spanner_icon_w.png") no-repeat scroll 0 0 transparent;
    padding: 2px; 
    text-indent: -9999px;
    width: 25px; 
}

.h2class { 
	background-color: #E9222C;
    border: 0 solid;
    border-radius: 3px;
    box-shadow: 2px 2px 2px #888888;
    color: #fcfcfc;
    cursor: pointer;
    display: block;
    font-size: 11px;
    height: 12px;
    left: 5px;
    padding: 1px 1px 2px 1px;
    position: relative;
    top: -26px;
    width: 18px;
	text-align:center;
	line-height: 12px;
}


.proshortname {
	background-color: #E9222C;
    border: 0 solid;  
    border-radius: 3px;
    box-shadow: 2px 2px 2px #888888;
    color: #fcfcfc;
    cursor: pointer;
    display: block;
    font-size: 11px;
    height: 12px;
    left: 5px;
    padding: 1px 1px 2px 1px;
    position: relative;
    top: -26px;
    width: 18px;
	text-align:center;
	line-height: 12px;
} 

.divclass {
    /* background-color: #E9222C;
    border: 0 solid;  
    border-radius: 3px;
    box-shadow: 2px 2px 2px #888888; 
    color: #fcfcfc; */
    cursor: pointer;
    display: block;
    font-size: 11px;
    height: 12px;
    left: 5px;
    padding: 1px 1px 2px 1px;
    position: relative;
    top: -14px;
    width: 18px;
	text-align:center;
	line-height: 12px;
}

.answer {
	margin-left: 25px;	
	background: #EBEBEB;
    -webkit-border-radius: .5em;
    -moz-border-radius: .5em;
    border-radius: .5em;
    width: 160px;
    -webkit-box-shadow: 0px 0px 3px rgba(0,0,0,.5);
    -moz-box-shadow: 0px 0px 3px rgba(0,0,0,.5);
    box-shadow: 0px 0px 3px rgba(0,0,0,.5);
    padding: 1px;
	position: relative;
    top: 1px;
    left:-25px;
    z-index: 999;
    padding-bottom: 10px;
}

.openans {
	margin-left: 25px;	
	background: #EBEBEB;
    -webkit-border-radius: .5em;
    -moz-border-radius: .5em;
    border-radius: .5em;
    width: 160px;
    -webkit-box-shadow: 0px 0px 3px rgba(0,0,0,.5);
    -moz-box-shadow: 0px 0px 3px rgba(0,0,0,.5);
    box-shadow: 0px 0px 3px rgba(0,0,0,.5);
    padding: 1px;
	position: relative;
    top: -12px;
    left:-25px;
    z-index: 999;
    padding-bottom: 10px;
}

 #triangle {
    border: 1px solid #d9d9d9;
    border-width: 2px 0 0 2px;
    width:10px;
    height:10px;
    -webkit-transform: rotate(45deg);
    -moz-transform: rotate(45deg);
    -o-transform:rotate(45deg);
    z-index: 1;
    position: relative;
    bottom: 9px;
    margin-left: 9px;
    background: #EBEBEB;
}

.digital {
	color: #3F82BF;
	font-family: Digital;
	font-size: 16px;
	font-weight: bold;
	text-align: center;
}
.fa{
padding-left: 5px;
padding-right: 5px;
}


.nav1 .dropdown-menu {
	-webkit-border-top-right-radius: 0px;
	-webkit-border-bottom-right-radius: 0px;
	border-top-right-radius: 0px;
	border-bottom-right-radius: 0px;
	-webkit-box-shadow: 5px 5px 10px rgba(0, 0, 0, 0.2);
	-moz-box-shadow: 5px 5px 10px rgba(0, 0, 0, 0.2);
	box-shadow: 5px 5px 10px rgba(0, 0, 0, 0.2);
}

.nav1 .dropdown-menu > li > a:hover {
	background-image: none;
	color: white;
	background-color: rgb(52, 104, 151);
	background-color: rgba(52, 104, 151);   
}

.nav1 .dropdown-menu > li > a.maintainHover {
	color: white;
	background-color: #0081C2;
}

.nav1 {
	list-style: none outside none;
    margin-bottom: 20px;
    margin-left: 0;
}

 .nav1 {
	display: block;
    float: left;
    left: 0;
    margin: 0 10px 0 0;
    position: relative;
}

li {
    line-height: 20px;
}

.nav1 > li {
  float:left;
  position:relative;
  padding: 10px 6px;
  
}

.dropdown-toggle {
}

.nav1 > li > a {
	display:block;
}

.nav1 > li > a {
	color: #777777;
    float: none;
   /* padding: 10px 15px; */
    text-decoration: none;
    text-shadow: 0 1px 0 #FFFFFF;
}

.nav1 > li > a {
    color: #999999;
    /*text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);*/
}

 .nav1 > .active > a, .nav1 > .active > a:hover, .nav1 > .active > a:focus {
    /*background-color: #E5E5E5;*/
    /*box-shadow: 0 3px 8px rgba(0, 0, 0, 0.125) inset;*/
    color: #555555;
    text-decoration: none;
}

.nav1 > .active > a {
 background-image:url('images1/down_icon_gray1.png');
 background-repeat:no-repeat; 
 background-position: right center;
}

 .nav1 .active > a:hover, .nav1 .active > a:focus {
    /*background-color: #4C66A4;*/
    background-image:url('images1/down_icon_white1.png');
    background-repeat:no-repeat; 
 	background-position: right center;
    color: #555555;
}

.nav1 .dropdown-menu {
    background-clip: padding-box;
    background-color: #FFFFFF;
    border: 1px solid rgba(0, 0, 0, 0.2);
    border-radius: 3px;
    box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
    display: none;
    float: right;
    /* left: 0; */
    right: -11px;
    list-style: none outside none;
    margin: 2px 0 0;
    min-width: 160px;
    padding: 5px 0;
    position: absolute;
    top: 90%;
	
    z-index: 9999;
}

.nav1 .dropdown-menu {
    border-bottom-right-radius: 3px;
    border-top-right-radius: 3px;
    box-shadow: 5px 5px 10px rgba(0, 0, 0, 0.2);
}

.nav1 > li > .dropdown-menu:after {
    border-bottom: 10px solid #FFFFFF;
    border-left: 9px solid rgba(0, 0, 0, 0);
    border-right: 9px solid rgba(0, 0, 0, 0);
    content: "";
    display: inline-block;  
    /* left: 82px; */
    right:13px;
    position: absolute;
    top: -10px;
    z-index:9999;
}

.nav1 > li > .dropdown-menu:before {
    border-bottom: 10px solid rgba(0, 0, 0, 0.2);
    border-left: 10px solid rgba(0, 0, 0, 0);
    border-right: 10px solid rgba(0, 0, 0, 0);
    content: "";
    display: inline-block;
    /* left: 81px; */
    right:12px;
    position: absolute;
    top: -10px;
    z-index:9999;
}

.open > .dropdown-menu {
    display: block;
    z-index:9999;
}

.dropdown-menu > li > a {
    clear: both;
    color: #333333;
    display: block;
    font-weight: normal;
    line-height: 20px;
    padding: 3px 20px;
    white-space: nowrap;
}




@media screen and (-webkit-min-device-pixel-ratio:0) {
    li > span {
		position:relative;
		top:-9px;
	}
}

.header-menu>li>ul{
padding: 5px;

border: 1px solid rgb(128, 128, 128);
border-color: #daaa3d;
box-shadow: 5px 15px 5px gray;


/*background-color: #3d0f0f;*/
}

.header-menu>li>ul>li a{
color: white;
}

.dropdown-menu>li>a:hover {
background-color: #FFFFFF;
color: #333;
}


/* /*XANADU 

.skin-blue .sidebar-menu>li>.treeview-menu {
background: #542418;
}

.skin-blue .main-header .navbar {
background-color: #542418;
}

.workrig-users-button .btn-sm {
background-color: #542418;
}

.workrig-users-button a.btn.btn-default.btn-sm.active:hover {
background-color: #542418;
}

.skin-blue .main-header .logo {
background-color: #542418;
}

.skin-blue .main-header .logo:hover {
background-color: #542418;
}

.skin-blue .wrapper, .skin-blue .main-sidebar, .skin-blue .left-side {
background-color: #542418;
}

.skin-blue .main-header .navbar .sidebar-toggle:hover {
background-color: #542418;
}

.skin-blue .treeview-menu>li>a {
color: #a3652d;
}

.skin-blue .main-header .navbar .nav>li>a>span {
color: #a3652d !important;
}

.skin-blue .user-panel>.info, .skin-blue .user-panel>.info>a {
color: #a3652d;
}

.skin-blue .sidebar a {
color: #a3652d;
}

.workrig-users-button .btn-sm {
color: #a3652d;
}

.topbar_time{
color: #a3652d !important;
}

.skin-blue .main-header .navbar .nav>li>a {
color: #a3652d !important;
}

.skin-blue .main-header .navbar .sidebar-toggle {
color: #a3652d !important;
}

.workrig-users-button .active {
color: #deaf54 !important;
}

.skin-blue .sidebar-menu>li:hover>a, .skin-blue .sidebar-menu>li.active>a {
color: #deaf54;
background: #542418;
border-left: none;
}

.treeview-menu .active {
background-color: #542418;
color: #deaf54 !important;
} */


.input-group.input-group-unstyled input.form-control {
    -webkit-border-radius: 4px;
    -moz-border-radius: 4px;
     border-radius: 4px;
}
.input-group-unstyled .input-group-addon {
    border-radius: 4px;
    border: 0px;
    background-color: transparent;
}

</style>
  <%
  
  	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	//String sbData = CF.getSearchingData();
	String sbData = (String)session.getAttribute("sbSearchData");	
	if(sbData == null) {
		sbData = "";
	}
	String strSearchData = (String) request.getAttribute("strSearchData"); 
	String action = null;

  %>

  <script type="text/javascript">

  	function changePassword() {
  		
  		var dialogEdit = '.modal-bodyCP';
  		 $(dialogEdit).empty();
  		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  		 $("#modalInfoCP").show();
  		 $(".modal-titleCP").html('Change Password');
  		$.ajax({
  			url : "ChangePassword.action",
  			cache : false,
  			success : function(data) {
  			$(dialogEdit).html(data);
  			}
  		});
  	}
  	
  	 <%-- function submitForm() {
		var strSearch = document.getElementById("strSearchData").value;
		$.ajax({
			type : 'POST',
			url: 'SearchData.action?strSearchJob=' + strSearch,
			cache: true,
			success: function(result) {
			}
		});
		<% String action2 = (String)request.getAttribute("action");
	 //	System.out.println("action222:::"+action2);
	 	if(action2 != null){%>
		var action = '<%= action2 %>';
		 <%}%>
		$.ajax({
			type : 'POST',
			url: 'MyPay.action?callFrom=MyDashLeaveSummary',
			//url: action,
			cache: true,
			success: function(result) {
				window.location.href = "MyPay.action?callFrom=MyDashLeaveSummary";
				//window.location.href =  action;
			}
		});
			
  	 } --%>
  	 
  	 
  	function searchData() {
		xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
	    } else {
	    	var strSearch = document.getElementById("strSearchData").value;
	    	if(strSearch != '') {
				var xhr = $.ajax({
					url : 'SearchData.action?strSearchJob=' + strSearch,
					cache : false,
					success : function(data) {
	                	if(data == "" || data.length<2) {
	                	} else {
	                		window.location = data;
						}
					}
				});
	    	}
		}
	}
	
	
	function GetXmlHttpObject() {
	    if (window.XMLHttpRequest) {
	            return new XMLHttpRequest();
	    }
	    if (window.ActiveXObject) {
	            return new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    return null;
	}
		</script>



				


<link href="<%=request.getContextPath() %>/css/autocomplete/jquery-ui.css" rel="stylesheet" type="text/css"/>
  <script src="<%=request.getContextPath() %>/scripts/autocomplete/jquery.min.js"></script>
  <script src="<%=request.getContextPath() %>/scripts/autocomplete/jquery-ui.min.js"></script> 
  
 


<%
	
	String[] arrEnabledModules = (String[]) session.getAttribute("arrEnabledModules");

	

	UtilityFunctions uF = new UtilityFunctions();
	String PRODUCTTYPE = (String) session.getAttribute(IConstants.PRODUCT_TYPE);
	String LOGINTYPE = (String) session.getAttribute(IConstants.LOGIN_TYPE);
	String RESOURCE_OR_CONTRACTOR = (String) session.getAttribute(IConstants.RESOURCE_OR_CONTRACTOR);
	
	String BASEUSERTYPEID = (String) session.getAttribute(IConstants.BASEUSERTYPEID);
	String USERTYPEID = (String) session.getAttribute(IConstants.USERTYPEID);
	String USERTYPE = (String) session.getAttribute(IConstants.USERTYPE);
	String BASEUSERTYPE = (String) session.getAttribute(IConstants.BASEUSERTYPE);
	
	String EMPNAME = (String) session.getAttribute(IConstants.EMPNAME);
	String WLOCATION_NAME = (String) session.getAttribute(IConstants.WLOCATION_NAME);
	String DESIGNATION = (String) session.getAttribute(IConstants.DESIGNATION);
	String DEPARTMENT = (String) session.getAttribute(IConstants.DEPARTMENT);
	String ORG_NAME = (String) session.getAttribute(IConstants.ORG_NAME);
	
	String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
	
	
	String strCurrentTime = null;

	if (CF != null) {
		CF.getTaskBarNotification(CF, USERTYPE, strSessionEmpId, request, session); 
		
		CF.getNewsAndAlerts(CF, USERTYPE, strSessionEmpId, request);
		strCurrentTime = uF.getDateFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", IConstants.DBTIME, CF.getStrReportTimeFormat());
	}

	if (BASEUSERTYPE == null) {
		BASEUSERTYPE = "";
	}
	if (USERTYPEID == null) {
		USERTYPEID = "";
	}
	
	Map hmUserModules = (Map) session.getAttribute("hmUserModules");
	if (hmUserModules == null)
		hmUserModules = new HashMap();

	if (CF == null){
		CF = new CommonFunctions();
		CF.setRequest(request);
	}
	
	String strLogo = (String) session.getAttribute("ORG_LOGO");
	//System.out.println("strLogo ===> " + strLogo);
	if (strLogo == null) {
		strLogo = CF.getOrgLogo(request, CF);
		strCurrentTime = uF.getDateFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", IConstants.DBTIME, CF.getStrReportTimeFormat());
	}

	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	
	Map<String, String> hmTaskNotification = (Map<String, String>) request.getAttribute("hmTaskNotification");
	//System.out.println("hmTaskNotification ===> " + hmTaskNotification);
	if (hmTaskNotification == null) hmTaskNotification = new HashMap<String, String>();
	
	String strUITheme = CF.getStrUI_Theme();
%> 


<!-- ************************************************************* WORK RIG ************************************************************** --> 

<% if(PRODUCTTYPE == null || PRODUCTTYPE.equals("2")) { 
	String sbNotificationsWR = (String)request.getAttribute("sbNotificationsWR");
	String notificationCountWR = (String)request.getAttribute("notificationCountWR");
	/* System.out.println("isApproved ===>> " + session.getAttribute("isApproved"));
	System.out.println("USERTYPE ===>> " + USERTYPE + " --- EMPNAME ===>> " + EMPNAME);
	System.out.println("CF.isForcePassword() ===>> " + CF.isForcePassword());
	System.out.println("CF.isTermsCondition() ===>> " + CF.isTermsCondition()); */
%>

		<% if (session.getAttribute("isApproved") == null) { %>
		<%-- <% if (CF.isTermsCondition() && !CF.isForcePassword() && USERTYPE != null && (USERTYPE.equalsIgnoreCase(IConstants.ADMIN) || USERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE) 
       		 || USERTYPE.equalsIgnoreCase(IConstants.MANAGER) || USERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || USERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) 
       		 || USERTYPE.equalsIgnoreCase(IConstants.RECRUITER) || USERTYPE.equalsIgnoreCase(IConstants.CEO) || USERTYPE.equalsIgnoreCase(IConstants.HOD) )) { %> --%>
        <% if (CF.isTermsCondition() && !CF.isForcePassword() && USERTYPE != null && (USERTYPE.equalsIgnoreCase(IConstants.ADMIN) || USERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE) 
       		 || USERTYPE.equalsIgnoreCase(IConstants.MANAGER) || USERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || USERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) 
       		 || USERTYPE.equalsIgnoreCase(IConstants.RECRUITER) || USERTYPE.equalsIgnoreCase(IConstants.CEO) || USERTYPE.equalsIgnoreCase(IConstants.HOD) || USERTYPE.equalsIgnoreCase(IConstants.OTHER_HR) )) { %>
       	<% if (USERTYPE != null && EMPNAME != null) { %> 
       	<div class="navbar-custom-menu pull-left col-lg-3 col-md-6 col-sm-12"> <!-- style="width:20%;" -->
       		<span class="active-workrig-user hidden"><%=(String) session.getAttribute(IConstants.USERTYPE)%></span>
       		<div class="box-tools" style="margin-top: 8px;">
              <div class="btn-group workrig-users-button" data-toggle="btn-toggle">
              	<!-- <ul class="nav navbar-nav header-menu"> -->
              		<%-- <%if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) 
  						|| BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO) 
  						|| BASEUSERTYPE.equalsIgnoreCase(IConstants.HOD) || BASEUSERTYPE.equalsIgnoreCase(IConstants.RECRUITER)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.EMPLOYEE))) {
		    		%> --%>
		    		<%if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) 
  						|| BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO) 
  						|| BASEUSERTYPE.equalsIgnoreCase(IConstants.HOD) || BASEUSERTYPE.equalsIgnoreCase(IConstants.RECRUITER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.OTHER_HR)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.EMPLOYEE))) {
		    		%>
		    			<a href="javascript:void(0)" onclick="window.location='Login.action?role=3'" data-placement="bottom" title="Myself" data-toggle="tooltip" class="btn btn-default btn-sm" for="<%=(String) IConstants.EMPLOYEE%>"><i class="fa fa-user" aria-hidden="true"></i></a>
		    		<%
		    		}
		    		if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) 
		    			|| BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HOD) || (BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) 
		    			&& uF.parseToBoolean((String)session.getAttribute(IConstants.IS_SUPERVISOR))) || (BASEUSERTYPE.equalsIgnoreCase(IConstants.RECRUITER) 
						&& uF.parseToBoolean((String)session.getAttribute(IConstants.IS_SUPERVISOR))) || BASEUSERTYPE.equalsIgnoreCase(IConstants.OTHER_HR) ) && uF.parseToBoolean((String) hmUserModules.get(IConstants.MANAGER)) ) {
			    		%>
		    			<a href="javascript:void(0)" onclick="window.location='Login.action?role=2'" class="btn btn-default btn-sm" data-placement="bottom" title="My Team" data-toggle="tooltip" for="<%=(String) IConstants.MANAGER%>"><i class="fa fa-users" aria-hidden="true"></i></a>
		    		<%
		    		}
		    		if (BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) && uF.parseToBoolean((String) hmUserModules.get(IConstants.ACCOUNTANT))) {
		    		%>
		    			<a href="javascript:void(0)" onclick="window.location='Login.action?role=4'"  class="btn btn-default btn-sm" data-placement="bottom" title="Accountant" data-toggle="tooltip" for="<%=(String) IConstants.ACCOUNTANT%>"><i class="fa fa-calculator" aria-hidden="true"></i></a></a>
		    		<%
		    		}
	  				if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.CEO))) {
		    		%>
		    		<%
		    		}
	  				if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CFO)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.CFO))) {
		    		%>
		    		<%
		    		}
	  				if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER))&& uF.parseToBoolean((String) hmUserModules.get(IConstants.HRMANAGER))) {
		    		%>
		    			<a href="javascript:void(0)" onclick="window.location='Login.action?role=7'" class="btn btn-default btn-sm" data-placement="bottom" title="HR" data-toggle="tooltip" for="<%=(String) IConstants.HRMANAGER%>"><i class="fa fa-user-plus" aria-hidden="true"></i></a>
		    		<%
		    		}
	  				if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.OTHER_HR))&& uF.parseToBoolean((String) hmUserModules.get(IConstants.OTHER_HR))) {
		    		%>
		    			<a href="javascript:void(0)" onclick="window.location='Login.action?role=14'" class="btn btn-default btn-sm" data-placement="bottom" title="Other HR" data-toggle="tooltip" for="<%=(String) IConstants.OTHER_HR%>"><i class="fa fa-user-plus" aria-hidden="true"></i></a>
		    		<%
		    		}
	  				if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ARTICLE)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.ARTICLE))) {
		    		%>
		    		<%
	    			}
					if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CONSULTANT)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.CONSULTANT))) {
		    		%>
		    		<%
		    		}
	  				if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.TRAINER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) 
	  					|| BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.TRAINER))) {
		    		%>
		    		<%
		    		}
	  				if (BASEUSERTYPE.equalsIgnoreCase(IConstants.RECRUITER) && uF.parseToBoolean((String) hmUserModules.get(IConstants.RECRUITER))) {
		    		%>
		    			<a href="javascript:void(0)" onclick="window.location='Login.action?role=11'" class="btn btn-default btn-sm" data-placement="bottom" title="Recruiter" data-toggle="tooltip" for="<%=(String) IConstants.RECRUITER %>"><i class="fa fa-street-view" aria-hidden="true"></i></a>
		    		<%
		    		}
	  				if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.CUSTOMER)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.CUSTOMER))) {
			    	%>
			    		<a href="javascript:void(0)" onclick="window.location='Login.action?role=12'"  class="btn btn-default btn-sm" data-placement="bottom" title="Customer" data-toggle="tooltip" for="<%=(String) IConstants.CUSTOMER %>"><%=(String) IConstants.CUSTOMER %></a>
			    	<%
		    		}
	  				if (BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) && uF.parseToBoolean((String) hmUserModules.get(IConstants.ADMIN))) {
	  				%>
						<a href="javascript:void(0)" onclick="window.location='Login.action?role=1'" class="btn btn-default btn-sm" data-placement="bottom" title="Global HR" data-toggle="tooltip" for="<%=(String) IConstants.ADMIN %>"><i class="fa fa-universal-access" aria-hidden="true"></i></a>
		    		<% } %>
	   			<!-- </ul> -->
              </div>
            </div>
   		</div>
   		<% } %>
   		
   		<div class="navbar-custom-menu navbar-custom-menu pull-left" style="padding-left:0px; margin-top: 7px;"> <!-- width:40%;   col-lg-5-->
			<ul class="nav navbar-nav">
   			<li class="dropdown notifications-menu">
   			    <div class="col-lg-12 col-md-12 col-sm-12" style="text-align: right;">
   				 <div style="margin: 5px 0px;">
   				
   		    	 <%-- <div style="margin-top:10px;">
	                 <div style="margin: 0px 0px 0px 0px;  -webkit-border-radius: 3px; border-radius: 3px;">
	                     <div style="float: left;width:80%">
	                         <input type="text" id="strSearchData" class="form-control" name="strSearchData" placeholder="Search anything" style="margin-left: 0px; width: 360px !important; max-width: 360px !important; box-shadow: 0px 0px 0px #ccc" value="<%=uF.showData(strSearchData, "") %>" />
	                 	  </div>
	                     <div style="float: right">
	                         <input type="button" value="Search" class="btn btn-primary" name="submit" onclick="searchData();" />
	                     </div>
	                 </div>
         		 </div> --%>
         		 <div class="input-group">
                    <input type="text" class="form-control" placeholder="  Search" id="strSearchData" name="strSearchData" style="height: 32px !important; width: 360px !important; max-width: 360px !important;" value="<%=uF.showData(strSearchData, "") %>"/>
                    <span class="input-group-addon" style="border-radius: 0px 3px 3px 0px !important; float: left; padding: 8px 30px 7px 10px;"><a href="javascript:void(0);" onclick="searchData();"><i class="fa fa-search" <%if(strUITheme != null && strUITheme.equals("2")) { %> style="color: #d9650c" <% } %>></i></a></span>
                </div>
         		 
         		 <script>
                 $( "#strSearchData" ).autocomplete({
                 	source: [ <%=uF.showData(sbData,"") %> ]
                 });
             </script>
         		 
         	 </div>
         	 </div>
   			</li>
   		 	</ul>
   		 	
   		 	<%-- <ul class="dropdown-menu">
              <li class="header">You have <%=notificationCountWR %> notifications</li>
              <li>
                <ul class="menu" style="width: 100%; height: 200px;">
                  <%=sbNotificationsWR %>
                </ul>
              </li>
            </ul> --%>
   		 	
   		</div>
       	<div class="navbar-custom-menu">
       	
       	<!-- <div class="column-6of12">
   					    <div class="longText" id="hidingScrollBar">
					      <div class="hideScrollBar"> -->
					      
					      
    	<ul class="nav navbar-nav">	 
		 <!-- <li>
		 	<a href="Login.action?role=3&userscreen=myhome" class="grow" title="My Home" style="padding: 15px 3px 5px;">
		 		<i class="fa fa-home"></i>	
		 	</a>
		 </li>
		<li>
		 	<a href="OrganisationalChart.action" class="grow" title="My Team" style="padding: 14px 3px 5px;">
		 		<i class="fa fa-sitemap"></i>	
		 	</a>
		</li> -->
		
		
		<!-- <li id="empty" style="color:#ccc;font-size:16px; padding: 12px 0px;">|</li> -->
		<li class="dropdown notifications-menu">
            <a href="#" class="dropdown-toggle faa-parent animated-hover" data-toggle="dropdown" aria-expanded="true">
              <i class="fa fa-bell faa-ring"></i>
              <% if (uF.parseToInt(notificationCountWR) > 0) { %>
					<span class="label label-warning"><%=notificationCountWR %></span>
			  <% } %>
            </a>
            <ul class="dropdown-menu">
              <li class="header">You have <%=notificationCountWR %> notifications</li>
              <li>
                <ul class="menu" style="width: 100%; height: 200px;">
                  <%=sbNotificationsWR %>
                </ul>
              </li>
            </ul>
          </li>
		<% } %>
		
		<%-- <% if (USERTYPE != null && !USERTYPE.equalsIgnoreCase(IConstants.ADMIN)) { %> --%>
		<li>
			<div class="topbar_time" style="padding: 15px; color: white;">
				<span style="float:left; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px;">It's </span>
				<span id="serverTime" style="float:left; padding: 0 3px;font-size: 18px;"><%=strCurrentTime%> </span>
				<span style="float:left; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px"> now </span>
			</div> 
		</li>
		<%-- <% } %> --%>
			
		<% if (USERTYPE != null) { %> 
		
		<% if (EMPNAME != null) { %> 
		      <!-- User Name & profile: style can be found in dropdown.less -->
				<li class="dropdown user user-menu">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" style="padding-right: 5px;padding-left: 5px;" id="open-dropdown"> 
					<%if(CF.getStrDocSaveLocation()==null) { %>
						<img class="img-circle lazy" height="22" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px"/>
					<%} else { %>
						<img class="img-circle lazy" height="22" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID) +"/"+ IConstants.I_22x22+"/"+ (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px"/>
					<%} %>
						<span style="font-weight: bold; color: white;"><%=EMPNAME %></span>
		            </a>
		                <ul class="dropdown-menu">
		                  <!-- User image -->
		                  <li class="user-header">
		                  	<div style="float: left; height: 100%;">
			                   <%if(CF.getStrDocSaveLocation()==null) { %>
									<img class="img-circle lazy" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px"/>
								<% } else { %>
									<img class="img-circle lazy" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID) +"/"+ IConstants.I_100x100+"/"+ (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px"/>
								<% } %>
							</div>
		                    <p><%=uF.showData(EMPNAME, "") %></p>
		                    <p style="font-size: 14px; margin: 0px;"><%=uF.showData(DESIGNATION, "") %> </p>
		                    <p style="font-size: 14px; margin: 0px;"><%=uF.showData(DEPARTMENT, "") %> </p>
		                    <p style="font-size: 14px; margin: 0px;"><%=uF.showData(WLOCATION_NAME, "") %> </p>
		                    <p style="font-size: 14px; margin: 0px;"><%=uF.showData(ORG_NAME, "") %> </p>
		                  </li>
		                  <!-- Menu Body -->
		                  <li class="user-body" style="padding: 7px 10px;">
		        			<div class="pull-left">&nbsp;</div>
							<div class="pull-right">
		                      <a class="btn btn-default btn-flat" href="javascript:void(0);" onclick="changePassword();">Change Password</a>
		                    </div>
		                  </li>
		                  <!-- Menu Footer-->
		                  <li class="user-footer">
		                    <div class="pull-left">
		                      <a href="MyProfile.action" class="btn btn-default btn-flat">Profile</a>
		                    </div>
		                    <div class="pull-right">
								<a href="Logout.action" class="btn btn-default btn-flat">Sign out</a>
		                    </div>
		                  </li>
		                </ul>
				</li>
				<% if (EMPNAME != null) {
				if((BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.ADMIN)) || (BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.HRMANAGER)) || (BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.OTHER_HR))) { 
					if((uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER)) && hmFeatureUserTypeId.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER).contains(BASEUSERTYPEID)) || (!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER)) && hmFeatureUserTypeId != null && hmFeatureUserTypeId.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER+"_USER_IDS") != null && hmFeatureUserTypeId.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER+"_USER_IDS").contains(strSessionEmpId))) {
				%>
					<li>
						<a href="#" data-toggle="control-sidebar" class="faa-parent animated-hover"><i class="fa fa-gear faa-spin animated-hover" style="font-size: 14px;"></i></a>
					</li>
					<% } %>
				<% } } %>
				</ul>
				</div>
				<!-- </div>
				</div>
				</div> -->
				
				
			<% } %>

		<% } %>
	<% } %>



<% } else if(PRODUCTTYPE != null && PRODUCTTYPE.equals("3")) {
	String sbNotifications = (String)request.getAttribute("sbNotifications");
	String notificationCount = (String)request.getAttribute("notificationCount");
%>

		<div class="navbar-custom-menu pull-left col-lg-3 col-md-6 col-sm-12">  <!-- style="width:20%;" -->
			<div class="topbar_time" style="padding: 15px; color: white;">
				<span><a href="MyWork.action" style="color: white; font-weight: bold;"> My Work</a></span> <!--  <i class="fa fa-tasks"> </i> -->
			</div> 
		</div>
	
		<%-- <div class="navbar-custom-menu navbar-custom-menu pull-left" style="padding-left: 0px; margin-top: 7px;"> <!-- width:40%;   col-lg-5 col-md-6 col-sm-12 -->
			<ul class="nav navbar-nav">
   			<li class="dropdown notifications-menu">
   			    <div class="col-lg-12 col-md-12 col-sm-12" style="text-align: right;">
   				 <div style="margin: 5px 0px;">
   				
         		 <div class="input-group">
                    <input type="text" class="form-control" placeholder="  Search" id="strSearchData" name="strSearchData" style="height: 32px !important; width: 360px !important; max-width: 360px !important;" value="<%=uF.showData(strSearchData, "") %>"/>
                    <span class="input-group-addon" style="border-radius: 0px 3px 3px 0px !important; float: left; padding: 8px 30px 7px 10px;"><a href="javascript:void(0);" onclick="searchData();"><i class="fa fa-search" <%if(strUITheme != null && strUITheme.equals("2")) { %> style="color: #d9650c" <% } %>></i></a></span>
                </div>
                
         		 <script>
                 $( "#strSearchData" ).autocomplete({
                 	source: [ <%=uF.showData(sbData, "") %> ]
                 });
             </script>
         		 
         	 </div>
         	 </div>
   			</li>
   		 	</ul>
   		</div> --%>
   		
<div class="navbar-custom-menu">
    <ul class="nav navbar-nav header-menu">
 <% if (session.getAttribute("isApproved") == null) { %>
    
    <% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && !BASEUSERTYPE.equalsIgnoreCase(IConstants.CUSTOMER)) { %>
     
     <% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && (BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || 
		 BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) 
		 || BASEUSERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE) || BASEUSERTYPE.equalsIgnoreCase(IConstants.OTHER_HR))) { %>
	      <!-- <li class="dropdown notifications-menu">
         	<a href="MyDashboard.action" title="My Dashboard" class="grow">
				<i class="fa fa-home"></i>	
         	</a>
         </li> -->
     
     <% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && (BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || 
		 BASEUSERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || 
		 BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CUSTOMER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.OTHER_HR))) { %>
		 <li class="dropdown notifications-menu">
			<a href="#" class="dropdown-toggle faa-parent animated-hover" data-toggle="dropdown" class="grow">
				<i class="fa fa-bell faa-ring"></i>
				<% if (uF.parseToInt(notificationCount) > 0) { %>
					<span class="label label-warning"><%=notificationCount %></span>
				<% } %>
			</a>	
				<ul class="dropdown-menu">
					<li class="header">You have <%=notificationCount %> notifications</li>
					<li>
						<ul class="menu grow" style="width: 100%; height: 200px;">
							<%=sbNotifications %>
						</ul>
					</li>
				</ul>
		</li>
	<% } %>
		<%-- <% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && !BASEUSERTYPE.equalsIgnoreCase(IConstants.CUSTOMER)) { %>
	         <li>
				<a href="#" class="dropdown-toggle" data-toggle="dropdown" class="grow">
						<i class="fa fa-envelope"></i>
					<% if (uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) { %>
						<span class="label label-danger" style="right: 3px; top: 6px;"><%=uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) %></span>
					<% } %>
				</a>	
				<ul class="dropdown-menu" style="min-width: 170px;">
					<li>
						<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>" style="float: left; width: 100%; padding: 2px;">
							<div style="float: left;">Mail </div>
							<div style="float: right;"><%=(uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) ? uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) : ""%></div>
						</a>	
					</li>
				</ul>
			</li>
		<% } %> --%>
     <% } %>
     
     <% } else if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && BASEUSERTYPE.equalsIgnoreCase(IConstants.CUSTOMER)) { %>
     
     <% } %>
     
     <%-- <% if (USERTYPE != null && !USERTYPE.equalsIgnoreCase(IConstants.ADMIN)) { %>
		<li class="dropdown user user-menu">
			<div class="topbar_time" style="padding: 15px; color: white;">
				<span>It's </span>
				<span id="serverTime" style="padding: 0 3px;font-size: 18px;"><%=strCurrentTime%> </span>
				<span> now </span>
			</div> 
		</li>
  	<% } %> --%>
  		<li>
			<div class="topbar_time" style="padding: 15px; color: white;">
				<span style="float:left; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px;">It's </span>
				<span id="serverTime" style="float:left; padding: 0 3px;font-size: 18px;"><%=strCurrentTime%> </span>
				<span style="float:left; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px"> now </span>
			</div> 
		</li>
  	
    <% if (USERTYPE != null) { %>
    
        <% if (EMPNAME != null) { %> 
              <!-- User Name & profile: style can be found in dropdown.less style="padding: 15px 2px;" -->
			<li class="dropdown user user-menu">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown">
				<%if(CF.getStrDocSaveLocation()==null) { %>
					<img class="img-circle lazy" height="22" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String) session.getAttribute(IConstants.PROFILE_IMG)%>"/>
				<%} else { %>
					<img class="img-circle lazy" height="22" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID) +"/"+ IConstants.I_22x22+"/"+ (String) session.getAttribute(IConstants.PROFILE_IMG)%>"/>
				<%} %>
					<span style="font-weight: 600;"><%=EMPNAME%></span>
	            </a>
	                <ul class="dropdown-menu">
	                  <!-- User image -->
	                  <li class="user-header autoHeight">
	                   <%if(CF.getStrDocSaveLocation()==null) { %>
							<img class="img-circle lazy" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px"/>
						<%} else { %>
							<img class="img-circle lazy" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID) +"/"+ IConstants.I_100x100+"/"+ (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px"/>
						<%} %>
	                    <p><%=EMPNAME%></p>
	                    <%-- <small></small> --%>
	                  </li>
	                  <!-- Menu Body -->
	                  <li class="user-body" style="padding: 7px 10px;">
            			<div class="pull-left">&nbsp;</div>
						<div class="pull-right">
	                      <a class="btn btn-default btn-flat" href="javascript:void(0);" onclick="changePassword();">Change Password</a>
	                    </div>
	                  </li>
	                  <!-- Menu Footer-->
	                  <li class="user-footer">
	                    <div class="pull-left">
	                      <a href="PeopleProfile.action?empId=<%=strSessionEmpId %>" class="btn btn-default btn-flat">Profile</a>
	                    </div>
	                    <div class="pull-right">
							<a href="Logout.action" class="btn btn-default btn-flat">Sign out</a>
	                    </div>
	                  </li>
	                </ul>
			</li>
		<% } %>
	
		<% } %>

     <% } %>
     <% if (EMPNAME != null) { %>
	     <% if((BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.ADMIN)) || (BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.HRMANAGER)) || (BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.OTHER_HR))) {
	    	 if((uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER)) && hmFeatureUserTypeId.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER).contains(BASEUSERTYPEID)) || (!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER)) && hmFeatureUserTypeId != null && hmFeatureUserTypeId.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER+"_USER_IDS") != null && hmFeatureUserTypeId.get(IConstants.F_SHOW_CONTROL_PANEL_ALL_USER+"_USER_IDS").contains(strSessionEmpId))) {
	     %>
			<li>
				<a href="#" data-toggle="control-sidebar" class="faa-parent animated-hover"><i class="fa fa-gear faa-spin animated-hover" style="font-size: 14px;"></i></a>
			</li>
			<% } %>
		<% } } %>
	</ul>
</div>
<% } %>
<!-- *********************************************************** END TASK RIG ************************************************************** -->

	
	<div class="modal" id="modalInfoCP" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title modal-titleCP"></h4>
            </div>
            <div class="modal-bodyCP" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
