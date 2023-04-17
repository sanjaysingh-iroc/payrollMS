<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.mail.MailCountClass"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page import="com.konnect.jpms.util.IPages"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

     

<style type="text/css">

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


</style>

<style>

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

</style>
<%
	String[] arrEnabledModules = (String[]) session.getAttribute("arrEnabledModules");

	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	UtilityFunctions uF = new UtilityFunctions();
	String PRODUCTTYPE = (String) session.getAttribute(IConstants.PRODUCT_TYPE);
	String LOGINTYPE = (String) session.getAttribute(IConstants.LOGIN_TYPE);
	String RESOURCE_OR_CONTRACTOR = (String) session.getAttribute(IConstants.RESOURCE_OR_CONTRACTOR);
	
	String BASEUSERTYPEID = (String) session.getAttribute(IConstants.BASEUSERTYPEID);
	String USERTYPEID = (String) session.getAttribute(IConstants.USERTYPEID);
	String USERTYPE = (String) session.getAttribute(IConstants.USERTYPE);
	String BASEUSERTYPE = (String) session.getAttribute(IConstants.BASEUSERTYPE);
	
	String EMPNAME = (String) session.getAttribute(IConstants.EMPNAME);
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
	}

	Map<String, String> hmTaskNotification = (Map<String, String>) request.getAttribute("hmTaskNotification");
	//System.out.println("hmTaskNotification ===> " + hmTaskNotification);
	if (hmTaskNotification == null) hmTaskNotification = new HashMap<String, String>();
	
%> 

<!-- ************************************************************* WORK RIG ************************************************************** --> 

<% if(PRODUCTTYPE == null || PRODUCTTYPE.equals("2")) { %>
<%
	String strNotNewManual = (String) session.getAttribute("NEW_MANUAL");
	if (strNotNewManual == null || (strNotNewManual != null && strNotNewManual.equalsIgnoreCase("0")))
		strNotNewManual = "";
%> 

<script type="text/javascript">
	hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
	hs.outlineType = 'rounded-white';
	hs.wrapperClassName = 'draggable-header';
</script>


<div id="header">     

<% if (session.getAttribute("isApproved") == null) { %>
               
   <% if (USERTYPE != null && USERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
   <div class="topbar_emp">
   <% } else { %>  
   <div class="topbar">
   <% } %>
   
    <div class="topicons">    

         <% if (CF.isTermsCondition() && !CF.isForcePassword() && USERTYPE != null && USERTYPE.equalsIgnoreCase(IConstants.ADMIN)) { %>
         <ul id="topnav_manager" style="background: none;"> 
	       <li id="dashboard">
       		<div style="background-image:url('images1/icons/icons/list_view_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 17px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/list_view_g.png\')'"
			onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/list_view_b.png\')'" class="divclass"></div>
         	<div class="openans" style="display: none;">
				<div id="triangle"></div>
				<table border="0" width="100%" style="border-collapse: collapse;">
					<% if(CF.isWorkRig()){ %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
								<div style="float: left;"><a href="Login.action?role=3&product=2" title="Workrig"><img src="images1/icons/icons/workrig.png" style="width: 130px;"/></a></div>
								<div style="float: right; padding-right: 10px;"><img src="images1/icons/hd_tick_20x20.png" style="width: 20px; margin-top: 19px;"></div>
							</td>
						</tr>
					<%} %>
					<% if(CF.isTaskRig()){ %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
								<a href="Login.action?role=3&product=3" title="TaskRig"> <img src="images1/icons/icons/taskrig.png" style="width: 130px;"/></a>
							</td>
						</tr>
					<%} %>
				</table>
			</div>
							
         </li>
         <li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
         
	    <li id="dashboard">
         	<!-- <a href="MyDashboard.action" title="Dashboard"> -->
         	<a href="Login.action?role=3&userscreen=myhome" title="My Home">
         		<div style="background-image:url('images1/icons/icons/Home_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/home_icon_g.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/Home_icon_b.png\')'"></div>	
         	</a>
         </li>
       	 
       	 <li id="dashboard">
         	<a href="OrganisationalChart.action" title="My Team">
         		<div style="background-image:url('images1/icons/icons/org_chat_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_g.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_b.png\')'"></div>	
         	</a>
		</li>
		
		<li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
		<%-- <%
			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE + "") >= 0) {
		%>
		<li id="dashboard">
		<%
			int nExceptionCount = uF.parseToInt(hmTaskNotification.get("EXCEPTION_CNT"));
		%>
         	<!-- <a href="UpdateClockEntries.action" title="Exceptions"> -->
         	<!-- <a href="#" title="Exceptions">  -->
         		<!-- <div style="float:left;" id="idEexception"></div> -->
         		<div style="background-image:url('images1/icons/icons/exclamation_point_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/exclamation_point_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/exclamation_point_icon_b.png\')'" class="divclass"></div>
					<div class="openans" style="display: none;" >
							<div id="triangle"></div>
							<table border="0" width="100%" style="border-collapse: collapse;">
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="UpdateClockEntries.action" title="Exceptions">Exceptions</a>
									</td>
									<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nExceptionCount > 0) ? nExceptionCount : "" %></td>
								</tr>
							</table>
						</div> <!-- </a> -->
         	<div id="idEexception">
         	
         	<%
         	         		if (nExceptionCount > 0) {
         	         	%>
							<div style="float: left;" class="h2class"><%=nExceptionCount%></div>
							<div class="answer" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%" style="border-collapse: collapse;">
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="UpdateClockEntries.action" title="Exceptions">Exceptions</a>
										</td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=nExceptionCount%></td>
									</tr>
								</table>
							</div>

							<%
								}
							%>
         	
         	
         	</div>
		</li>
		<%
			}
		%> --%>
		
		<li id="dashboard">
		<%
		
		  int nLeaveCount = 0;
		  int nReimbursementCount = 0;
		  int nTravelCount = 0;
		  int nPerkCount = 0;
		  int nLTACount = 0;
		  int nLeaveEncashCount = 0;
		  int nRequisitionCount = 0;
		  int nLoanCount = 0;
		  int nLibraryRequestCount = 0;
		  int nFoodRequestCount = 0;
		  int nMeetingRoomBookingReqCount = 0;
		  int nApprovalRequestCount =  0;
		  
	      int requirementRequestCount = 0;
		  int jobApprovalRequestCount = 0;
		  int newCandidateFillCount = 0;
		  int hrReviewsCount = 0;
		
		  int reviewFinalizationCnt = 0;
		  int newJoineeCnt = 0;
		  int newJoineePendingCnt = 0;
		  int learningGapCount = 0;
		  int candidateFinalizationCount = 0;
		  int candidateOfferAcceptRejectCount = 0;
		  int hrLearningFinalizationCount = 0;
		  int selfReviewRequestCnt = 0;
		  int confirmations = 0;
		  int resignations = 0;
		  int finalDay = 0;
		  
		  if( arrEnabledModules != null && arrEnabledModules.length >0) {
			  
			
			  if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT + "") >= 0) { 
				 nLeaveCount = uF.parseToInt(hmTaskNotification.get("LEAVE_REQUEST_CNT"));
			  } 
			
			  if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) { 
				  nReimbursementCount = uF.parseToInt(hmTaskNotification.get("REIM_REQUEST_CNT"));
				  nTravelCount = uF.parseToInt(hmTaskNotification.get("TRAVEL_CNT"));
			  }
			
			  if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { 
				  nPerkCount = uF.parseToInt(hmTaskNotification.get("PERK_REQUEST_CNT"));
				  nLTACount = uF.parseToInt(hmTaskNotification.get("LTA_REQUEST_CNT"));
				  nLeaveEncashCount = uF.parseToInt(hmTaskNotification.get(IConstants.LEAVE_ENCASH_REQUEST_ALERT));
				  nLoanCount = uF.parseToInt(hmTaskNotification.get(IConstants.LOAN_REQUEST_ALERT));
			  }
			  
			  if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { 
				  nRequisitionCount = uF.parseToInt(hmTaskNotification.get(IConstants.REQUISITION_REQUEST_ALERT));
				  newJoineeCnt = uF.parseToInt(hmTaskNotification.get("NEWJOINEE_CNT"));
				  confirmations = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_CONFIRMATIONS_ALERT)); 
				  resignations = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_RESIGNATIONS_ALERT));
				  finalDay = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_FINAL_DAY_ALERT));
			  }
				
			  if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LIBRARY + "") >= 0) { 
				  nLibraryRequestCount = uF.parseToInt(hmTaskNotification.get(IConstants.LIBRARY_REQUEST_ALERT));
			  }	 
				
			  if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAFETERIA + "") >= 0) { 
				  nFoodRequestCount = uF.parseToInt(hmTaskNotification.get(IConstants.FOOD_REQUEST_ALERT));
			  }		
			
			  if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_MEETING_ROOM + "") >= 0) { 
				  nMeetingRoomBookingReqCount = uF.parseToInt(hmTaskNotification.get(IConstants.MEETING_ROOM_BOOKING_REQUEST_ALERT));
			  }	 
			     
			  if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
					hrReviewsCount = uF.parseToInt(hmTaskNotification.get("HR_REVIEWS_CNT"));
					reviewFinalizationCnt = uF.parseToInt(hmTaskNotification.get("REVIEW_FINALIZATION_CNT"));
					selfReviewRequestCnt = uF.parseToInt(hmTaskNotification.get(IConstants.SELF_REVIEW_REQUEST_ALERT));
			}
			
			if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) {
				requirementRequestCount = uF.parseToInt(hmTaskNotification.get("REQUI_REQUEST_CNT"));
				jobApprovalRequestCount = uF.parseToInt(hmTaskNotification.get("JOBCODE_REQUEST_CNT"));
				candidateFinalizationCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_FINALIZATION_CNT"));
				candidateOfferAcceptRejectCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_OFFER_ACCEPT_REJECT_CNT"));
				newCandidateFillCount = uF.parseToInt(hmTaskNotification.get(IConstants.NEW_CANDIDATE_FILL_ALERT));
			}
			
			if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) {
				learningGapCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_GAPS_CNT"));
				hrLearningFinalizationCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_FINALIZATION_CNT"));
			}	
			
		//	System.out.println("header newJoineeCnt==>"+newJoineeCnt+"==>confirmations==>"+confirmations+"==>resignations==>"+resignations+"==>finalDay==>"+finalDay);
			nApprovalRequestCount = nLeaveCount + nReimbursementCount + nTravelCount + nPerkCount + nLTACount + nLeaveEncashCount + nRequisitionCount + nLoanCount + nLibraryRequestCount + nFoodRequestCount + nMeetingRoomBookingReqCount;
			
			int requirementCount = requirementRequestCount + jobApprovalRequestCount + hrReviewsCount + reviewFinalizationCnt + newJoineeCnt + learningGapCount + candidateFinalizationCount
			+ candidateOfferAcceptRejectCount + newCandidateFillCount + hrLearningFinalizationCount + selfReviewRequestCnt +confirmations+resignations+finalDay;
			
			nApprovalRequestCount = nApprovalRequestCount + requirementCount;
				
         }
			
			/* int requirementRequestCount = 0;
			int jobApprovalRequestCount = 0;
			int hrReviewsCount = 0;
			
			int reviewFinalizationCnt = 0;
			int newJoineeCnt = 0;
			int newJoineePendingCnt = 0;
			int learningGapCount = 0;
			int candidateFinalizationCount = 0;
			int candidateOfferAcceptRejectCount = 0;
			int hrLearningFinalizationCount = 0;
			int selfReviewRequestCnt = 0;
			int confirmations = 0;
			int resignations = 0;
			int finalDay = 0; 
			
			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
					hrReviewsCount = uF.parseToInt(hmTaskNotification.get("HR_REVIEWS_CNT"));
					reviewFinalizationCnt = uF.parseToInt(hmTaskNotification.get("REVIEW_FINALIZATION_CNT"));
					selfReviewRequestCnt = uF.parseToInt(hmTaskNotification.get(IConstants.SELF_REVIEW_REQUEST_ALERT));
			}
			
			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) {
				requirementRequestCount = uF.parseToInt(hmTaskNotification.get("REQUI_REQUEST_CNT"));
				jobApprovalRequestCount = uF.parseToInt(hmTaskNotification.get("JOBCODE_REQUEST_CNT"));
				newJoineeCnt = uF.parseToInt(hmTaskNotification.get("NEWJOINEE_CNT"));
				candidateFinalizationCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_FINALIZATION_CNT"));
				candidateOfferAcceptRejectCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_OFFER_ACCEPT_REJECT_CNT"));
				confirmations = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_CONFIRMATIONS_ALERT)); 
				resignations = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_RESIGNATIONS_ALERT));
				finalDay = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_FINAL_DAY_ALERT));
			}
			
			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) {
				learningGapCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_GAPS_CNT"));
				hrLearningFinalizationCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_FINALIZATION_CNT"));
				
			}	
				
				
				int requirementCount = requirementRequestCount + jobApprovalRequestCount + hrReviewsCount + reviewFinalizationCnt + newJoineeCnt + learningGapCount + candidateFinalizationCount
				+ candidateOfferAcceptRejectCount + hrLearningFinalizationCount + selfReviewRequestCnt +confirmations+resignations+finalDay;
				
				nApprovalRequestCount = nApprovalRequestCount + requirementCount;
			}*/
		%>		
         	<div style="background-image:url('images1/icons/icons/tick_on_file_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/tick_on_file_icon_o.png\')'"
					onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/tick_on_file_icon_b.png\')'" class="divclass"></div>
				
							<div class="openans" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%"
									style="border-collapse: collapse;">
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+ "") >= 0) { %>
										<tr>  
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="ManagerLeaveApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_REQUEST_ALERT%>"
												title="Requests">Leave Request</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nLeaveCount > 0) ? nLeaveCount : ""%></td>
										</tr>
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_REQUEST_ALERT%>"
												title="Claims">Claims Request</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nReimbursementCount > 0) ? nReimbursementCount : ""%></td>
										</tr>
									  							  
									  
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="TravelApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_REQUEST_ALERT%>"
												title="Travel">Travel Request</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nTravelCount > 0) ? nTravelCount : ""%></td>
										</tr>
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_REQUEST_ALERT%>"
												title="Perk">Perk Request</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nPerkCount > 0) ? nPerkCount : ""%></td>
										</tr>
										
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_REQUEST_ALERT%>"
												title="CTC Variable Request">CTC Variable</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nLTACount > 0) ? nLTACount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_REQUEST_ALERT%>"
												title="Leave Encashment">Leave Encashment</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nLeaveEncashCount > 0) ? nLeaveEncashCount : ""%></td>
										</tr>
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="RequisitionApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_REQUEST_ALERT%>"
												title="Requisition">Requisition</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nRequisitionCount > 0) ? nRequisitionCount : ""%></td>
										</tr>
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_REQUEST_ALERT%>"
												title="Loan">Loan</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nLoanCount > 0) ? nLoanCount : ""%></td>
										</tr>
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_REQUEST_ALERT%>"
												title="Requirements">Requirements</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(requirementRequestCount>0) ? requirementRequestCount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_REQUEST_ALERT%>"
												title="Job Approvals">Job Approvals</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(jobApprovalRequestCount>0) ? jobApprovalRequestCount : ""%></td>
										</tr>
									<% } %>
										
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
										<%-- <tr>
											<td nowrap
												style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.NEW_REVIEW_ALERT%>"
												title="Reviews">Reviews</a>
											</td>
											<td
												style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(newReviewCount>0) ? newReviewCount : "" %></td>
										</tr> --%>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="Reviews.action?dataType=SRR&alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
												title="Hr Reviews">Self Review Requests</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(selfReviewRequestCnt>0) ? selfReviewRequestCnt : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.HR_REVIEW_ALERT%>"
												title="Hr Reviews">Hr Reviews</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(hrReviewsCount>0) ? hrReviewsCount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.REVIEW_FINALIZATION_ALERT%>"
												title="Review Finalization">Review Finalization</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(reviewFinalizationCnt>0) ? reviewFinalizationCnt : ""%></td>
										</tr>
									<% } %>
										
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="Offers.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_OFFER_ACCEPTREJECT_ALERT%>"
												title="Offer Accept/Reject">Offer Accept/Reject</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(candidateOfferAcceptRejectCount>0) ? candidateOfferAcceptRejectCount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="Applications.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_FINALIZATION_ALERT%>"
												title="Candidate Finalization">Candidate Finalization</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(candidateFinalizationCount>0) ? candidateFinalizationCount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="CandidateReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_CANDIDATE_FILL_ALERT%>"
												title="New Candidate">New Candidate</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(newCandidateFillCount>0) ? newCandidateFillCount : ""%></td>
										</tr>
										<% } %>
										<%-- <tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
												title="New Joinee Pending">New Joinee Pending</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(newJoineePendingCnt>0) ? newJoineePendingCnt : ""%></td>
										</tr> --%>
										<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="EmployeeActivity.action?empType=I&alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
												title="New Joinees">New Joinees</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(newJoineeCnt>0) ? newJoineeCnt : ""%></td>
										</tr>
										
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="EmployeeActivity.action?empType=C&alertStatus=alert&alert_type=<%=IConstants.EMP_CONFIRMATIONS_ALERT%>"
												title="confirmations">Confirmations</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=confirmations %></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="EmployeeActivity.action?empType=R&alertStatus=alert&alert_type=<%=IConstants.EMP_RESIGNATIONS_ALERT%>"
												title="resignations">Resignations</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=resignations %></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="EmployeeActivity.action?empType=FD&alertStatus=alert&alert_type=<%=IConstants.EMP_FINAL_DAY_ALERT%>"
												title="Final Day">Final Day</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=finalDay%></td>
										</tr>
								<% } %>
									
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_GAPS_ALERT%>"
												title="Learning Gaps">Learning Gaps</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(learningGapCount>0) ? learningGapCount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="LearningPlanAssessmentStatus.action?alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_FINALIZATION_ALERT%>"
												title="Learning Finalization">Learning Finalization</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(hrLearningFinalizationCount>0) ? hrLearningFinalizationCount : ""%></td>
										</tr>
								 <% } %>
									
								
									
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LIBRARY + "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="Library.action?alertStatus=alert&alert_type=<%=IConstants.LIBRARY_REQUEST_ALERT%>"
											title="Loan">Library</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLibraryRequestCount > 0) ? nLibraryRequestCount : ""%></td>
									</tr>
								<% } %>
								
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAFETERIA + "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="Cafeteria.action?alertStatus=alert&alert_type=<%=IConstants.FOOD_REQUEST_ALERT%>"
											title="Loan">Cafeteria</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nFoodRequestCount > 0) ? nFoodRequestCount : ""%></td>
									</tr>
								<% } %>
								
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_MEETING_ROOM + "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="MeetingRooms.action?dataType=MBR&alertStatus=alert&alert_type=<%=IConstants.MEETING_ROOM_BOOKING_REQUEST_ALERT%>"
											title="Loan">Meeting Room</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nMeetingRoomBookingReqCount > 0) ? nMeetingRoomBookingReqCount : ""%></td>
									</tr>
								<% } %>
						</table>
				</div>	
         	<!-- </a> -->
         	<div id="idRequest">
         		<%
         			if (nApprovalRequestCount > 0) {
         		%>
							<div style="float: left;" class="h2class"><%=nApprovalRequestCount%></div>
							<div class="answer" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%"
									style="border-collapse: collapse;">
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="ManagerLeaveApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_REQUEST_ALERT%>"
												title="Requests">Leave Request</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nLeaveCount > 0) ? nLeaveCount : ""%></td>
										</tr>
									
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_REQUEST_ALERT%>"
												title="Claims">Claims Request</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nReimbursementCount > 0) ? nReimbursementCount : ""%></td>
										</tr>
	
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="TravelApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_REQUEST_ALERT%>"
												title="Travel">Travel Request</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nTravelCount > 0) ? nTravelCount : ""%></td>
										</tr>
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_REQUEST_ALERT%>"
												title="Perk">Perk Request</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nPerkCount > 0) ? nPerkCount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_REQUEST_ALERT%>"
												title="CTC Variable Request">CTC Variable</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nLTACount > 0) ? nLTACount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_REQUEST_ALERT%>"
												title="Leave Encashment">Leave Encashment</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nLeaveEncashCount > 0) ? nLeaveEncashCount : ""%></td>
										</tr>
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="RequisitionApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_REQUEST_ALERT%>"
												title="Requisition">Requisition</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nRequisitionCount > 0) ? nRequisitionCount : ""%></td>
										</tr>
									
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_REQUEST_ALERT%>"
												title="Loan">Loan</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nLoanCount > 0) ? nLoanCount : ""%></td>
										</tr>
									<% } %>
									
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+ "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_REQUEST_ALERT%>"
												title="Requirements">Requirements</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=requirementRequestCount >0 ? requirementRequestCount : "" %></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_REQUEST_ALERT%>"
												title="Job Approvals">Job Approvals</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=jobApprovalRequestCount > 0 ? jobApprovalRequestCount : ""%></td>
										</tr>
									<% } %>
										
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
										<%-- <tr>
											<td nowrap
												style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
												href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.NEW_REVIEW_ALERT%>"
												title="Reviews">Reviews</a>
											</td>
											<td
												style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=newReviewCount%></td>
										</tr> --%>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="Reviews.action?dataType=SRR&alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
												title="Hr Reviews">Self Review Requests</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=selfReviewRequestCnt > 0 ? selfReviewRequestCnt : "" %></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.HR_REVIEW_ALERT%>"
												title="Hr Reviews">Hr Reviews</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=hrReviewsCount > 0 ? hrReviewsCount : "" %></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.REVIEW_FINALIZATION_ALERT%>"
												title="Review Finalization">Review Finalization</a>
											</td>
											<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=reviewFinalizationCnt > 0 ? reviewFinalizationCnt : "" %></td>
										</tr>
									
									<% } %>
										
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="Offers.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_OFFER_ACCEPTREJECT_ALERT%>"
											title="Offer Accept/Reject">Offer Accept/Reject</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=candidateOfferAcceptRejectCount > 0 ? candidateOfferAcceptRejectCount : "" %></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="Applications.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_FINALIZATION_ALERT%>"
											title="Candidate Finalization">Candidate Finalization</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=candidateFinalizationCount > 0 ? candidateFinalizationCount : "" %></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="CandidateReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_CANDIDATE_FILL_ALERT%>"
											title="New Candidate">New Candidate</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(newCandidateFillCount>0) ? newCandidateFillCount : ""%></td>
									</tr>
									<% } %>
									<%-- <tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
											title="New Joinee  Pending">New Joinee Pending</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=newJoineePendingCnt > 0 ? newJoineePendingCnt : "" %></td>
									</tr> --%>
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="EmployeeActivity.action?empType=I&alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
											title="New Joinees">New Joinees</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=newJoineeCnt > 0 ? newJoineeCnt : "" %></td>
									</tr>
									
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="EmployeeActivity.action?empType=C&alertStatus=alert&alert_type=<%=IConstants.EMP_CONFIRMATIONS_ALERT%>"
											title="confirmations">Confirmations</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=confirmations > 0 ? confirmations : "" %></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="EmployeeActivity.action?empType=R&alertStatus=alert&alert_type=<%=IConstants.EMP_RESIGNATIONS_ALERT%>"
											title="resignations">Resignations</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=resignations > 0 ? resignations : "" %></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="EmployeeActivity.action?empType=FD&alertStatus=alert&alert_type=<%=IConstants.EMP_FINAL_DAY_ALERT%>"
											title="Final Day">Final Day</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=finalDay > 0 ? finalDay : "" %></td>
									</tr>
								<% } %>
									
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_GAPS_ALERT%>"
											title="Learning Gaps">Learning Gaps</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=learningGapCount > 0 ? learningGapCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="LearningPlanAssessmentStatus.action?alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_FINALIZATION_ALERT%>"
											title="Learning Finalization">Learning Finalization</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=hrLearningFinalizationCount > 0 ? hrLearningFinalizationCount : "" %></td>
									</tr>
								<% } %>
									
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LIBRARY + "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="Library.action?alertStatus=alert&alert_type=<%=IConstants.LIBRARY_REQUEST_ALERT%>"
											title="Loan">Library</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLibraryRequestCount > 0) ? nLibraryRequestCount : ""%></td>
									</tr>
								<% } %>
									
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAFETERIA + "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="Cafeteria.action?alertStatus=alert&alert_type=<%=IConstants.FOOD_REQUEST_ALERT%>"
											title="Loan">Cafeteria</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nFoodRequestCount > 0) ? nFoodRequestCount : ""%></td>
									</tr>
								<% } %>
									
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_MEETING_ROOM + "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="MeetingRooms.action?dataType=MBR&alertStatus=alert&alert_type=<%=IConstants.MEETING_ROOM_BOOKING_REQUEST_ALERT%>"
											title="Loan">Meeting Room</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nMeetingRoomBookingReqCount > 0) ? nMeetingRoomBookingReqCount : ""%></td>
									</tr>
								<% } %>
									
							</table>
						</div>
					<% } %>
         	</div>
		</li>
		
		<%-- <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
		<li id="dashboard">
			<%
				int requirementRequestCount = uF.parseToInt(hmTaskNotification.get("REQUI_REQUEST_CNT"));
				int jobApprovalRequestCount = uF.parseToInt(hmTaskNotification.get("JOBCODE_REQUEST_CNT"));
				int hrReviewsCount = uF.parseToInt(hmTaskNotification.get("HR_REVIEWS_CNT"));
				
				int reviewFinalizationCnt = uF.parseToInt(hmTaskNotification.get("REVIEW_FINALIZATION_CNT"));
				int newJoineeCnt = uF.parseToInt(hmTaskNotification.get("NEWJOINEE_CNT"));
				int newJoineePendingCnt = uF.parseToInt(hmTaskNotification.get(IConstants.NEW_JOINEE_PENDING_ALERT));
				int learningGapCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_GAPS_CNT"));
				int candidateFinalizationCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_FINALIZATION_CNT"));
				int candidateOfferAcceptRejectCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_OFFER_ACCEPT_REJECT_CNT"));
				int hrLearningFinalizationCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_FINALIZATION_CNT"));
				int selfReviewRequestCnt = uF.parseToInt(hmTaskNotification.get(IConstants.SELF_REVIEW_REQUEST_ALERT));
				
				int requirementCount = requirementRequestCount + jobApprovalRequestCount + hrReviewsCount + reviewFinalizationCnt + newJoineeCnt + newJoineePendingCnt + learningGapCount + candidateFinalizationCount
				+ candidateOfferAcceptRejectCount + hrLearningFinalizationCount + selfReviewRequestCnt; //+ newReviewCount
			%>
         		<div style="background-image:url('images1/icons/icons/human_resource_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/human_resource_icon_o.png\')'"
					onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/human_resource_icon_b.png\')'" class="divclass"></div>
					<div class="openans" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_REQUEST_ALERT%>"
									title="Requirements">Requirements</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(requirementRequestCount>0) ? requirementRequestCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_REQUEST_ALERT%>"
									title="Job Approvals">Job Approvals</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(jobApprovalRequestCount>0) ? jobApprovalRequestCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?dataType=SRR&alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
									title="Hr Reviews">Self Review Requests</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(selfReviewRequestCnt>0) ? selfReviewRequestCnt : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.HR_REVIEW_ALERT%>"
									title="Hr Reviews">Hr Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(hrReviewsCount>0) ? hrReviewsCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.REVIEW_FINALIZATION_ALERT%>"
									title="Review Finalization">Review Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(reviewFinalizationCnt>0) ? reviewFinalizationCnt : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Offers.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_OFFER_ACCEPTREJECT_ALERT%>"
									title="Offer Accept/Reject">Offer Accept/Reject</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(candidateOfferAcceptRejectCount>0) ? candidateOfferAcceptRejectCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Applications.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_FINALIZATION_ALERT%>"
									title="Candidate Finalization">Candidate Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(candidateFinalizationCount>0) ? candidateFinalizationCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
									title="New Joinee Pending">New Joinee Pending</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(newJoineePendingCnt>0) ? newJoineePendingCnt : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
									title="New Joinees">New Joinees</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(newJoineeCnt>0) ? newJoineeCnt : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_GAPS_ALERT%>"
									title="Learning Gaps">Learning Gaps</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(learningGapCount>0) ? learningGapCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="LearningPlanAssessmentStatus.action?alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_FINALIZATION_ALERT%>"
									title="Learning Finalization">Learning Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(hrLearningFinalizationCount>0) ? hrLearningFinalizationCount : ""%></td>
							</tr>
						</table>
					</div>
						
         	<div id="idApproval">
         		<% if (requirementCount > 0) { %>
					<div style="float: left;" class="h2class"><%=requirementCount%></div>
					<div class="answer" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_REQUEST_ALERT%>"
									title="Requirements">Requirements</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=requirementRequestCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_REQUEST_ALERT%>"
									title="Job Approvals">Job Approvals</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=jobApprovalRequestCount > 0 ? jobApprovalRequestCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?dataType=SRR&alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
									title="Hr Reviews">Self Review Requests</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=selfReviewRequestCnt %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.HR_REVIEW_ALERT%>"
									title="Hr Reviews">Hr Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=hrReviewsCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.REVIEW_FINALIZATION_ALERT%>"
									title="Review Finalization">Review Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=reviewFinalizationCnt %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Offers.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_OFFER_ACCEPTREJECT_ALERT%>"
									title="Offer Accept/Reject">Offer Accept/Reject</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=candidateOfferAcceptRejectCount %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Applications.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_FINALIZATION_ALERT%>"
									title="Candidate Finalization">Candidate Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=candidateFinalizationCount%></td>
							</tr>
							
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
									title="New Joinee  Pending">New Joinee Pending</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=newJoineePendingCnt %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
									title="New Joinees">New Joinees</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=newJoineeCnt %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_GAPS_ALERT%>"
									title="Learning Gaps">Learning Gaps</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=learningGapCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="LearningPlanAssessmentStatus.action?alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_FINALIZATION_ALERT%>"
									title="Learning Finalization">Learning Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=hrLearningFinalizationCount %></td>
							</tr>
						</table>
					</div>
				<% } %>
         	</div>
		</li>
		<% } %> --%>
		
		<li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
		

		<%-- <li id="dashboard">   
         		<div style="background-image:url('images1/icons/icons/mail_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_b.png\')'" class="divclass" ></div>
				
					<div class="openans" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>"
									title="Mail">Mail</a>
								</td>
								<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) ? uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) : ""%></td>
							</tr>
						</table>
					</div>	
         		<div id="idMail">
         		<% if (uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) { %>
						<div style="float: left;" class="h2class"></div>
						<div class="answer" style="display: none;">
							<div id="triangle"></div>
							<table border="0" width="100%" style="border-collapse: collapse;">
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>" title="Mail">Mail</a>
									</td>
									<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=uF.parseToInt(hmTaskNotification.get("MAIL_CNT"))%></td>
								</tr>
							</table>
						</div>
						<%
							}
						%>
					</div>
			</li> --%>
		
       </ul> 
        
        <% } else if (CF.isTermsCondition() && !CF.isForcePassword() && USERTYPE != null && USERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
        
         <ul id="topnav_emp" style="background: none;"> 
	       <li id="dashboard">
       		<div style="background-image:url('images1/icons/icons/list_view_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 17px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/list_view_g.png\')'"
			onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/list_view_b.png\')'" class="divclass"></div>
         	<div class="openans" style="display: none;">
				<div id="triangle"></div>
				<table border="0" width="100%" style="border-collapse: collapse;">
					<% if(CF.isWorkRig()){ %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
								<div style="float: left;"><a href="Login.action?role=3&product=2" title="Workrig"><img src="images1/icons/icons/workrig.png" style="width: 130px;"/></a></div>
								<div style="float: right; padding-right: 10px;"><img src="images1/icons/hd_tick_20x20.png" style="width: 20px; margin-top: 19px;"></div>
							</td>
						</tr>
					<%} %>
					<% if(CF.isTaskRig()){ %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
								<a href="Login.action?role=3&product=3" title="TaskRig"> <img src="images1/icons/icons/taskrig.png" style="width: 130px;"/></a>
							</td>
						</tr>
					<%} %>
				</table>
			</div>
							
         </li>
         <li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
         
           
	      <li id="dashboard">
         	<a href="Login.action?role=3&userscreen=myhome" title="My Home">
         	<% String IS_HOME = (String)session.getAttribute("IS_HOME");
         	//System.out.println("IS_HOME ===>> " + IS_HOME);
         	if(IS_HOME != null && IS_HOME.equals("YES")) { %>
         		<div style="background-image:url('images1/icons/icons/home_icon_g.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;"></div>
         	<% } else { %>
         		<div style="background-image:url('images1/icons/icons/Home_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/home_icon_g.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/Home_icon_b.png\')'"></div>
		<% } 
        	session.setAttribute("IS_HOME", "NO");
		%>		
         	</a>
         </li>
	       
	    <li id="dashboard">
       		<a href="MyProfile.action" title="MyFacts">
        		<div style="background-image:url('images1/icons/icons/profile_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/profile_icon_g.png\')'"
					onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/profile_icon_b.png\')'"></div>	
        	</a>
		</li>
		
		<li id="dashboard">
         	<a href="OrganisationalChart.action" title="My Team">
         		<div style="background-image:url('images1/icons/icons/org_chat_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_g.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_b.png\')'"></div>	
         	</a>
		</li>

		<li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
		
		<% if(PRODUCTTYPE != null && PRODUCTTYPE.equals("2")) { %>
		
		<li id="dashboard">
		
		<%
			int nPayChequeCount = 0;
			int nPayPerkCount = 0;
			int nPayLtaCount = 0;
			int nPayGratuityCount = 0;
			int nPayReimbursementCount = 0;
			int nTotalPay = 0;
			if (arrEnabledModules != null && arrEnabledModules.length>0) {
				if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+ "") >= 0) {
					nPayChequeCount = uF.parseToInt(hmTaskNotification.get("MY_PAY_CNT"));
					nPayPerkCount = uF.parseToInt(hmTaskNotification.get("PAY_PERK_CNT"));
					nPayLtaCount = uF.parseToInt(hmTaskNotification.get("PAY_LTA_CNT"));
					nPayGratuityCount = uF.parseToInt(hmTaskNotification.get(IConstants.PAY_GRATUITY));
				}
				
				if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+ "") >= 0) {
					nPayReimbursementCount = uF.parseToInt(hmTaskNotification.get(IConstants.PAY_REIM));
				}
				
				nTotalPay = nPayChequeCount + nPayPerkCount + nPayLtaCount + nPayGratuityCount + nPayReimbursementCount;
			}
		%>
         	<!-- <a href="ViewPaySlips.action" title="Pay Checks"> -->
         	<!-- <a href="#" title="Pay Checks"> -->
         		<!-- <div style="float:left;" id="idPayCheck"></div> -->
         		<div class="divclass" style="background-image:url('images1/icons/icons/pay_cheque_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/pay_cheque_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/pay_cheque_icon_b.png\')'" class="divclass"></div>
				<div class="openans" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%"
									style="border-collapse: collapse;">
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+ "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
												<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.MY_PAY_ALERT%>"
												title="Paid Checks">Paid Checks</a></td>
											<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nPayChequeCount > 0) ? nPayChequeCount : ""%></td>
										</tr>
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+ "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
												<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.PAY_REIM%>"
												title="Paid Claims">Paid Claims</a></td>
											<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nPayReimbursementCount > 0) ? nPayReimbursementCount : ""%></td>
										</tr>
									
									<% } %>
									
									<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+ "") >= 0) { %>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
												<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.PAY_PERK%>"
												title="Paid Perks">Paid Perks</a></td>
											<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nPayPerkCount > 0) ? nPayPerkCount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
												<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.PAY_LTA%>"
												title="Paid CTC Variable">Paid CTC Variable</a></td>
											<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nPayLtaCount > 0) ? nPayLtaCount : ""%></td>
										</tr>
										<tr>
											<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
												<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.PAY_GRATUITY%>"
												title="Paid Gratuity">Paid Gratuity</a></td>
											<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
												class="digital"><%=(nPayGratuityCount > 0) ? nPayGratuityCount : ""%></td>
										</tr>
									<% } %>
								</table>
				</div>	
         	<!-- </a> -->
         	<div id="idPayCheck">
							<%
								if (nTotalPay > 0) {
							%>
							<div style="float: left;" class="h2class"><%=nTotalPay%></div>
							<div class="answer" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%" style="border-collapse: collapse;">
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+ "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.MY_PAY_ALERT%>"
											title="Paid Checks">Paid Checks</a></td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nPayChequeCount > 0) ? nPayChequeCount : ""%></td>
									</tr>
								<% } %>
								
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+ "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.PAY_REIM%>"
											title="Paid Claims">Paid Claims</a></td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nPayReimbursementCount > 0) ? nPayReimbursementCount : ""%></td>
									</tr>
								<% } %>
								
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+ "") >= 0) { %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.PAY_PERK%>"
											title="Paid Perks">Paid Perks</a></td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nPayPerkCount > 0) ? nPayPerkCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.PAY_LTA%>"
											title="Paid CTC Variable">Paid CTC Variable</a></td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nPayLtaCount > 0) ? nPayLtaCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="ViewPaySlips.action?alertStatus=alert&alert_type=<%=IConstants.PAY_GRATUITY%>"
											title="Paid Gratuity">Paid Gratutity</a></td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nPayGratuityCount > 0) ? nPayGratuityCount : ""%></td>
									</tr>
									
								<% } %>
								</table>
							</div> 
							<%
								}
							%>
						</div></li>
		
		<li id="dashboard">
		<%
			int nLeaveApproveCount = 0;
			int nReimbApproveCount = 0;
			int nTravelApproveCount = 0;
			int nPerkApproveCount = 0;
			int nLtaApproveCount = 0;
			int nLeaeEncashApproveCount = 0;
			int nRequisitionApproveCount = 0;
			int nLoanApproveCount = 0;
			int nTravelBookingCount = 0;
			int nLibraryReqApprovedCount = 0;
			int nMeetingRoomBookingReqApprovedCount = 0;
			
			int goalsCount = 0;
			int kRAsCount = 0;
			int targetsCount = 0;
			int reviewsCount = 0;
			int learningPlanCount = 0;
			int myInterviewsScheduledCount = 0;
			int selfReviewApprovalCnt = 0;
			int reviewGoalKraTargetCount = 0;
			int nApprovalCount = 0;
		
			if (arrEnabledModules != null && arrEnabledModules.length > 0) {
				
				if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+ "") >= 0) {
					 nLeaveApproveCount = uF.parseToInt(hmTaskNotification.get("LEAVE_APPOVAL_CNT"));
				}
			
				if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+ "") >= 0) {
					nReimbApproveCount = uF.parseToInt(hmTaskNotification.get("REIM_APPOVAL_CNT"));
					nTravelApproveCount = uF.parseToInt(hmTaskNotification.get("TRAVEL_APPOVAL_CNT"));
					nTravelBookingCount = uF.parseToInt(hmTaskNotification.get(IConstants.TRAVEL_BOOKING_ALERT));
				}
				
				if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+ "") >= 0) {
					 nPerkApproveCount = uF.parseToInt(hmTaskNotification.get("PERK_APPOVAL_CNT"));
				     nLtaApproveCount = uF.parseToInt(hmTaskNotification.get("LTA_APPOVAL_CNT"));
					 nLeaeEncashApproveCount = uF.parseToInt(hmTaskNotification.get(IConstants.LEAVE_ENCASH_APPROVAL_ALERT));
					 nLoanApproveCount = uF.parseToInt(hmTaskNotification.get(IConstants.LOAN_APPROVAL_ALERT));
				}
				
				if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+ "") >= 0) {
					myInterviewsScheduledCount = uF.parseToInt(hmTaskNotification.get("MY_INTERVIEW_SCHEDULED_CNT"));
				}
				
				if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT+ "") >= 0) {
					 nRequisitionApproveCount = uF.parseToInt(hmTaskNotification.get(IConstants.REQUISITION_APPROVAL_ALERT));
				}
				
				if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+ "") >= 0) {
					selfReviewApprovalCnt = uF.parseToInt(hmTaskNotification.get(IConstants.SELF_REVIEW_APPROVAL_ALERT));
					reviewGoalKraTargetCount = reviewsCount + goalsCount + kRAsCount + targetsCount;
					goalsCount = uF.parseToInt(hmTaskNotification.get("MY_GOALS_CNT"));
					kRAsCount = uF.parseToInt(hmTaskNotification.get("MY_KRAS_CNT"));
					targetsCount = uF.parseToInt(hmTaskNotification.get("MY_TARGETS_CNT"));
					reviewsCount = uF.parseToInt(hmTaskNotification.get("MY_REVIEWS_CNT"));
				}
			 
				if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LIBRARY+ "") >= 0) {
					nLibraryReqApprovedCount = uF.parseToInt(hmTaskNotification.get(IConstants.LIBRARY_REQUEST_APPROVED_ALERT));
				}
			
				if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_MEETING_ROOM+ "") >= 0) {
					nMeetingRoomBookingReqApprovedCount = uF.parseToInt(hmTaskNotification.get(IConstants.MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT));
				}
			   
				if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+ "") >= 0) {
					learningPlanCount = uF.parseToInt(hmTaskNotification.get("MY_LEARNING_PLAN_CNT"));
				}
			
			    nApprovalCount = nLeaveApproveCount + nReimbApproveCount + nTravelApproveCount + nPerkApproveCount + nLtaApproveCount + nLeaeEncashApproveCount + nRequisitionApproveCount + nLoanApproveCount + nTravelBookingCount + nLibraryReqApprovedCount + nMeetingRoomBookingReqApprovedCount;
			
			
				int allCount = learningPlanCount + myInterviewsScheduledCount;
				reviewGoalKraTargetCount = reviewsCount + goalsCount + kRAsCount + targetsCount;
				allCount = allCount + reviewGoalKraTargetCount + selfReviewApprovalCnt;
				nApprovalCount = nApprovalCount + allCount;
			}
			
		%>
         		<div style="background-image:url('images1/icons/icons/tickmark_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/tickmark_icon_o.png\')'"
					onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/tickmark_icon_b.png\')'" class="divclass"></div>	
				
					<div class="openans" style="display: none; width: 250px;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
						  <% if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+ "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="EmployeeLeaveEntryReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_APPROVAl_ALERT%>" 
									title="My Leave Request">Leave Approved</a>
								</td>
								<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLeaveApproveCount > 0) ? nLeaveApproveCount : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+ "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_APPROVAL_ALERT%>"
										title="Claims">Claims Approved</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nReimbApproveCount > 0) ? nReimbApproveCount : ""%></td>
							</tr>

							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="EmployeeLeaveEntryReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_APPROVAL_ALERT%>"
										title="Travel">Travel Approved</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nTravelApproveCount > 0) ? nTravelApproveCount : ""%></td>
							</tr>
												
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="EmployeeLeaveEntryReport.action?dataType=T&alertStatus=alert&alert_type=<%=IConstants.TRAVEL_BOOKING_ALERT%>"
										title="Travel Booking">Travel Booking</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nTravelBookingCount > 0) ? nTravelBookingCount : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+ "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_APPROVAL_ALERT%>"
									title="Perk">Perk Approved</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nPerkApproveCount > 0) ? nPerkApproveCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_APPROVAL_ALERT%>"
									title="CTC Variable">CTC Variable</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLtaApproveCount > 0) ? nLtaApproveCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_APPROVAL_ALERT%>"
									title="Leave Encashment">Leave Encashment</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLeaeEncashApproveCount > 0) ? nLeaeEncashApproveCount : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT+ "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyRequests.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_APPROVAL_ALERT%>"
									title="Requisition">Requisition</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nRequisitionApproveCount > 0) ? nRequisitionApproveCount : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+ "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_APPROVAL_ALERT%>"
									title="Loan">Loan</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLoanApproveCount > 0) ? nLoanApproveCount : ""%></td>
							</tr>
						<% } %>
						
						
					     <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
			     			<%-- <tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.MY_REVIEW_ALERT%>"
									title="Reviews">Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(reviewsCount>0) ? reviewsCount : ""%></td>
							</tr> --%>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyHR.action?alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_APPROVAL_ALERT %>"
									title="Interviews">Self Review Approval</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(selfReviewApprovalCnt>0) ? selfReviewApprovalCnt : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyHR.action?alertStatus=alert&alert_type=<%=IConstants.GOAL_KRA_TARGET_ALERT%>"
									title="Goals, KRAs, Targets">Goals, KRAs, Targets, Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(reviewGoalKraTargetCount>0)? reviewGoalKraTargetCount : "" %></td>
							</tr>
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="Calendar.action?alertStatus=alert&alert_type=<%=IConstants.ADD_MY_INTERVIEWS_SCHEDULED_ALERT%>"
										title="Interviews">Interviews</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(myInterviewsScheduledCount>0) ? myInterviewsScheduledCount : ""%></td>
								</tr>
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) { %>
							
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="MyHR.action?callFrom=LPDash&alertStatus=alert&alert_type=<%=IConstants.MY_LEARNING_PLAN_ALERT%>"
										title="My Learnings">My Learnings</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(learningPlanCount>0) ? learningPlanCount : "" %></td>
								</tr>
							<% } %>
								
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LIBRARY + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="Library.action?alertStatus=alert&alert_type=<%=IConstants.LIBRARY_REQUEST_APPROVED_ALERT%>"
										title="Loan">Library</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nLibraryReqApprovedCount > 0) ? nLibraryReqApprovedCount : ""%></td>
								</tr>
							<% } %>
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_MEETING_ROOM + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="Calendar.action?dataType=MRB&alertStatus=alert&alert_type=<%=IConstants.MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT%>"
										title="Loan">Meeting Room</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nMeetingRoomBookingReqApprovedCount > 0) ? nMeetingRoomBookingReqApprovedCount : ""%></td>
								</tr>
							<% } %>
								
						</table>
					</div>
				
         		<div id="idLeaveApprove">
					<% if (nApprovalCount > 0) { %>
					<div style="float: left;" class="h2class"><%=nApprovalCount%></div>
					<div class="answer" style="display: none; width: 250px;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="EmployeeLeaveEntryReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_APPROVAl_ALERT%>" 
									title="My Leave Request">Leave Approved</a>
								</td>
								<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLeaveApproveCount > 0) ? nLeaveApproveCount : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_APPROVAL_ALERT%>"
									title="Claims">Claims Approved</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nReimbApproveCount > 0) ? nReimbApproveCount : ""%></td>
							</tr>
						
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="EmployeeLeaveEntryReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_APPROVAL_ALERT%>"
									title="Travel">Travel Approved</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nTravelApproveCount > 0) ? nTravelApproveCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="EmployeeLeaveEntryReport.action?dataType=T&alertStatus=alert&alert_type=<%=IConstants.TRAVEL_BOOKING_ALERT%>"
										title="Travel Booking">Travel Booking</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nTravelBookingCount > 0) ? nTravelBookingCount : ""%></td>
							</tr>
						<% } %>	
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_APPROVAL_ALERT%>"
									title="Perk">Perk Approved</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nPerkApproveCount > 0) ? nPerkApproveCount : ""%></td>
							</tr>
							
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_APPROVAL_ALERT%>"
									title="CTC Variable">CTC Varaible</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLtaApproveCount > 0) ? nLtaApproveCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_APPROVAL_ALERT%>"
									title="Leave Encashment">LeaveEncashment</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLeaeEncashApproveCount > 0) ? nLeaeEncashApproveCount : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyRequests.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_APPROVAL_ALERT%>"
									title="Requisition">Requisition</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nRequisitionApproveCount > 0) ? nRequisitionApproveCount : ""%></td>
							</tr>
						<%} %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_APPROVAL_ALERT%>"
									title="Loan">Loan</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLoanApproveCount > 0) ? nLoanApproveCount : ""%></td>
							</tr>
							
						<% } %>	
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
							<%-- <tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.MY_REVIEW_ALERT%>"
									title="Reviews">Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=reviewsCount%></td>
							</tr> --%>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyHR.action?alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_APPROVAL_ALERT %>"
									title="Interviews">Self Review Approval</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(selfReviewApprovalCnt > 0) ? selfReviewApprovalCnt : "" %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyHR.action?alertStatus=alert&alert_type=<%=IConstants.GOAL_KRA_TARGET_ALERT%>"
									title="Goals, KRAs, Targets">Goals, KRAs, Targets, Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(reviewGoalKraTargetCount > 0) ? reviewGoalKraTargetCount : "" %></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Calendar.action?alertStatus=alert&alert_type=<%=IConstants.ADD_MY_INTERVIEWS_SCHEDULED_ALERT%>"
									title="Interviews">Interviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(myInterviewsScheduledCount > 0) ? myInterviewsScheduledCount : "" %></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyHR.action?callFrom=LPDash&alertStatus=alert&alert_type=<%=IConstants.MY_LEARNING_PLAN_ALERT%>"
									title="My Learnings">My Learnings</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(learningPlanCount > 0) ? learningPlanCount : "" %></td>
							</tr>
						<% } %>
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LIBRARY + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="Library.action?alertStatus=alert&alert_type=<%=IConstants.LIBRARY_REQUEST_APPROVED_ALERT%>"
										title="Loan">Library</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nLibraryReqApprovedCount > 0) ? nLibraryReqApprovedCount : ""%></td>
								</tr>
							<% } %>
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_MEETING_ROOM + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="Calendar.action?dataType=MRB&alertStatus=alert&alert_type=<%=IConstants.MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT%>"
										title="Loan">Meeting Room</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nMeetingRoomBookingReqApprovedCount > 0) ? nMeetingRoomBookingReqApprovedCount : ""%></td>
								</tr>
							<% } %>
						</table>
					</div>
					<% } %>
				</div>
			</li>
		
				<%-- <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
				<li id="dashboard">
				<%
					int goalsCount = uF.parseToInt(hmTaskNotification.get("MY_GOALS_CNT"));
					int kRAsCount = uF.parseToInt(hmTaskNotification.get("MY_KRAS_CNT"));
					int targetsCount = uF.parseToInt(hmTaskNotification.get("MY_TARGETS_CNT"));
					int reviewsCount = uF.parseToInt(hmTaskNotification.get("MY_REVIEWS_CNT"));
					int learningPlanCount = uF.parseToInt(hmTaskNotification.get("MY_LEARNING_PLAN_CNT"));
					int myInterviewsScheduledCount = uF.parseToInt(hmTaskNotification.get("MY_INTERVIEW_SCHEDULED_CNT"));
					int selfReviewApprovalCnt = uF.parseToInt(hmTaskNotification.get(IConstants.SELF_REVIEW_APPROVAL_ALERT));
					
					int allCount = reviewsCount + goalsCount + kRAsCount + targetsCount + learningPlanCount + myInterviewsScheduledCount;
					int reviewGoalKraTargetCount = reviewsCount + goalsCount + kRAsCount + targetsCount;
					allCount = allCount + reviewGoalKraTargetCount + selfReviewApprovalCnt;
				%>
	         		<div style="background-image:url('images1/icons/icons/human_resource_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/human_resource_icon_o.png\')'"
						onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/human_resource_icon_b.png\')'" class="divclass"></div>
						<div class="openans" style="display: none; width: 250px">
							<div id="triangle"></div>
							<table border="0" width="100%" style="border-collapse: collapse;">
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="MyHR.action?alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_APPROVAL_ALERT %>"
										title="Interviews">Self Review Approval</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(selfReviewApprovalCnt>0) ? selfReviewApprovalCnt : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="MyHR.action?alertStatus=alert&alert_type=<%=IConstants.GOAL_KRA_TARGET_ALERT%>"
										title="Goals, KRAs, Targets">Goals, KRAs, Targets, Reviews</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(reviewGoalKraTargetCount>0)? reviewGoalKraTargetCount : "" %></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="Calendar.action?alertStatus=alert&alert_type=<%=IConstants.ADD_MY_INTERVIEWS_SCHEDULED_ALERT%>"
										title="Interviews">Interviews</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(myInterviewsScheduledCount>0) ? myInterviewsScheduledCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="MyHR.action?callFrom=LPDash&alertStatus=alert&alert_type=<%=IConstants.MY_LEARNING_PLAN_ALERT%>"
										title="My Learnings">My Learnings</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(learningPlanCount>0) ? learningPlanCount : "" %></td>
								</tr>
							</table>
						</div>
         	<div id="idApproval">
         	<%
         		if (allCount > 0) {
         	%>
					<div style="float: left;" class="h2class"><%=allCount%></div>
					<div class="answer" style="display: none; width: 250px;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyHR.action?alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_APPROVAL_ALERT %>"
									title="Interviews">Self Review Approval</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=selfReviewApprovalCnt %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyHR.action?alertStatus=alert&alert_type=<%=IConstants.GOAL_KRA_TARGET_ALERT%>"
									title="Goals, KRAs, Targets">Goals, KRAs, Targets, Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=reviewGoalKraTargetCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Calendar.action?alertStatus=alert&alert_type=<%=IConstants.ADD_MY_INTERVIEWS_SCHEDULED_ALERT%>"
									title="Interviews">Interviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=myInterviewsScheduledCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="MyHR.action?callFrom=LPDash&alertStatus=alert&alert_type=<%=IConstants.MY_LEARNING_PLAN_ALERT%>"
									title="My Learnings">My Learnings</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=learningPlanCount%></td>
							</tr>
						</table>
					</div>
				<% } %>
         	</div>
		</li>
		<% } %> --%>
       	 
		<li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
		
		<% } %>
		
		
		<%-- <li id="dashboard">
         		<div style="background-image:url('images1/icons/icons/mail_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_b.png\')'" class="divclass" ></div>
				
				<div class="openans" style="display: none;">
					<div id="triangle"></div>
					<table border="0" width="100%"
						style="border-collapse: collapse;">
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>" title="Mail">Mail</a>
							</td>
							<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) ? uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) : ""%></td>
						</tr>
					</table>
				</div>	
         	<div id="idMail">
         		<%
         			if (uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) {
         		%>
					<div style="float: left;" class="h2class"></div>
					<div class="answer" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>"
									title="Mail">Mail</a>
								</td>
								<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=uF.parseToInt(hmTaskNotification.get("MAIL_CNT"))%></td>
							</tr>
						</table>
					</div>
					<% } %>
				</div>
		</li> --%>
		
		
		<li id="dashboard">
         	<%
         	String sbNotifications1 = (String)request.getAttribute("newsAndalerts");
    		String notificationCount1 = (String)request.getAttribute("newsCount");
    		
    		int newManualCount = uF.parseToInt(hmTaskNotification.get(IConstants.NEW_MANUAL_ALERT));
    		
         	%>
         	<div style="background-image:url('images1/icons/icons/mic_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/mic_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/mic_icon_b.png\')'" class="divclass"></div>
				<div class="openans" style="display: none; float: left; width: 322px; top: -16px;">
					<div id="triangle"></div>
						<div>
								<% if (uF.parseToInt(notificationCount1) > 0) { %>
									<div style="float: left; width: 100%; font-weight: bold; border-bottom: 2px solid lightgray;">
										<span style="float: left; margin-left: 5px;">News And Alerts</span>
										<a href="Notifications.action?operation=RAN" style="font-weight: normal; float: right; margin: 0px 20px; width: auto; height: 0px;">Mark Read All </a>
									</div>
								<% } %>
								<div id="idTask" style="float: left; width: 100%; max-height: 250px; overflow-y: auto;">
								<% if (uF.parseToInt(notificationCount1) > 0) { %>
									<%=sbNotifications1 %>
								<% } else { %>
									<div style="float: left; color: gray; margin-left: 10px;">No notifications available.</div>
								<% } %>
								</div>
								<% if (uF.parseToInt(notificationCount1) > 25) { %>
									<a href="Notifications.action?operation=NA" style="float: left; width: 100%; text-align: center; border-top: 2px solid lightgray; margin: 7px 0px -17px;">See All </a>
								<% } %>
							</div>
						</div>	
						<% if (uF.parseToInt(notificationCount1) > 0) { %>
								<div style="float: left;" class="h2class"><%=notificationCount1%></div>
									<div class="answer" style="display: none; float: left; width: 322px; top: -16px;">
										<div id="triangle"></div>
									<div>
									<% if (uF.parseToInt(notificationCount1) > 0) { %>
										<div style="float: left; width: 100%; font-weight: bold; border-bottom: 2px solid lightgray;">
											<span style="float: left; margin-left: 5px;">News And alerts</span>
											<a href="Notifications.action?operation=RAN" style="font-weight: normal; float: right; margin: 0px 20px; width: auto; height: 0px;">Mark Read All </a>
										</div>
									<% } %>
									<div id="idTask" style="float: left; width: 100%; max-height: 250px; overflow-y: auto;">
									<% if (uF.parseToInt(notificationCount1) > 0) { %>
										 <%=sbNotifications1 %>
									<% } else { %>
										 <div style="float: left; color: gray; margin-left: 10px;">No notifications available.</div>
									<% } %>
									</div>
									<% if (uF.parseToInt(notificationCount1) > 25) { %>
										<a href="Notifications.action?operation=NA" style="float: left; width: 100%; text-align: center; border-top: 2px solid lightgray; margin: 7px 0px -17px;">See All </a>
									<% } %>
									</div>	
								</div>
						<% } %>
						<%--<table border="0" width="100%" style="border-collapse: collapse;">
				
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Notifications.action?operation=NA" title="News & Alerts">News & Alerts</a>
							</td>
							<%
								if(uF.parseToInt(notificationCount1)>0){
							%>
							<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=uF.parseToInt(notificationCount1)%>
							</td>
							<%
								}
							%>
						</tr>  
						<%--
						
	         			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0){%>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Calendar.action" title="My Calendar">My Calendar</a>
								</td>
							</tr>
						<%} else { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="HolidayReport.action" title="Company Holidays">Company Holidays</a>
								</td>
							</tr>
						<%} %>
						<!-- <tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="PolicyReport.action" title="Business Rule">Business Rule</a>
							</td>
						</tr> --> 
					</table>--%>
		</li>
		<li id="dashboard">
         	<a href="Hub.action?type=M" title="Company Manual">
         		<div style="background-image:url('images1/icons/icons/rule_policy_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/rule_policy_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/rule_policy_icon_b.png\')'"></div>
				<% if(newManualCount > 0) { %>
					<span style="margin-left: 9px; margin-top: -28px; position: relative;"><img border="0" src="/PayrollMS/images1/icons/news_icon.gif"></span>
				<% } %>	
         	</a>
		</li>
		
        </ul> 
        
        <% } else if (CF.isTermsCondition() && !CF.isForcePassword() && USERTYPE != null && USERTYPE.equalsIgnoreCase(IConstants.MANAGER)) { %>
        
         <ul id="topnav_manager" style="background: none;"> 
        
           <%-- <li id="dashboard"><a href="MyDashboard.action" title="My Dashboard"></a></li>
           <li id="empty"></li>
           <li id="email"><a href="MyMail.action" title="Email"><span class="notification_green"><%=strMailCount %></span></a></li>
           <li id="myleave"><a href="EmployeeLeaveEntry.action" title="Leave Requests"><span class="notification"><%=strLeaveRequestCount %></span></a></li>
	       <li id="pending_exceptions"><a href="UpdateClockEntries.action" title="Pending Exceptions"><span class="notification"><%=strPendingExceptionCount %></span></a></li>
	       <li id="reimbursement_requests"><a href="Reimbursements.action" title="Reimbursement Requests"><span class="notification"><%=strPendingReimbursementCount %></span></a></li>
	       <li id="requisition_requests"><a href="Requisitions.action" title="Requisition Requests"><span class="notification"><%=strPendingRequisitionCount %></span></a></li>
	       <li id="daily_reports"><a href="ApproveExtraActivity.action" title="Daily Reports"><span class="notification_green"><%=strReportSentCount %></span></a></li>
	       <li id="completed_tasks"><a href="ViewAllProjects.action" title="Completed Tasks"><span class="notification_green"><%=strCompletedTaskCount %></span></a></li> --%>
	       
	       <li id="dashboard">
       		<div style="background-image:url('images1/icons/icons/list_view_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 17px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/list_view_g.png\')'"
			onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/list_view_b.png\')'" class="divclass"></div>
         	<div class="openans" style="display: none;">
				<div id="triangle"></div>
				<table border="0" width="100%" style="border-collapse: collapse;">
					<% if(CF.isWorkRig()){ %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
								<div style="float: left;"><a href="Login.action?role=3&product=2" title="Workrig"><img src="images1/icons/icons/workrig.png" style="width: 130px;"/></a></div>
								<div style="float: right; padding-right: 10px;"><img src="images1/icons/hd_tick_20x20.png" style="width: 20px; margin-top: 19px;"></div>
							</td>
						</tr>
					<%} %>
					<% if(CF.isTaskRig()){ %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
								<a href="Login.action?role=3&product=3" title="TaskRig"> <img src="images1/icons/icons/taskrig.png" style="width: 130px;"/></a>
							</td>
						</tr>
					<%} %>
				</table>
			</div>
							
         </li>
         <li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
         
         
	       <li id="dashboard">
         	<!-- <a href="MyDashboard.action" title="Dashboard"> -->
         	<a href="Login.action?role=3&userscreen=myhome" title="My Home"> 
         		<div style="background-image:url('images1/icons/icons/Home_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/home_icon_g.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/Home_icon_b.png\')'"></div>	
         	</a>
         </li>
       	
       	<li id="dashboard">
         	<a href="OrganisationalChart.action" title="My Team">
         		<div style="background-image:url('images1/icons/icons/org_chat_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_g.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_b.png\')'"></div>	
         	</a>
		</li>
		
		<li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
		
		
		<%-- <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE + "") >= 0) { %>
		<li id="dashboard">
		<% int nExceptionCount = uF.parseToInt(hmTaskNotification.get("EXCEPTION_CNT")); %>
         	<!-- <a href="UpdateClockEntries.action" title="Exceptions"> -->
         	<!-- <a href="#" title="Exceptions"> -->
         		<!-- <div style="float:left;" id="idEexception"></div> -->
         		<div style="background-image:url('images1/icons/icons/exclamation_point_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/exclamation_point_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/exclamation_point_icon_b.png\')'" class="divclass"></div>
				<div class="openans" style="display: none;" >
					<div id="triangle"></div>
					<table border="0" width="100%" style="border-collapse: collapse;">
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
							<a href="UpdateClockEntries.action" title="Exceptions">Exceptions</a>
							</td>
							<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nExceptionCount>0) ? nExceptionCount : "" %></td>
						</tr>
					</table>
				</div> 	
         	<!-- </a> -->
         	<div id="idEexception">
	         	<% if (nExceptionCount > 0) { %>
					<div style="float: left;" class="h2class"><%=nExceptionCount%></div>
					<div class="answer" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%"
							style="border-collapse: collapse;">
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="UpdateClockEntries.action" title="Exceptions">Exceptions</a>
								</td>
								<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=nExceptionCount%></td>
							</tr>
						</table>
					</div>
	
				<% } %>
         	
         	</div>
		</li>
		<% } %> --%>
		
		<li id="dashboard"> 
		<%
			int nLeaveCount = 0;
			int nReimbursementCount = 0;
			int nTravelCount = 0;
			int nPerkCount = 0;
			int nLTACount = 0;
			int nLeaveEncashCount = 0;
			int nRequisitionCount = 0;
			int nLoanCount = 0;
			int nApprovalRequestCount = 0;

			int jobcodeApprovalCount = 0;
			int requirementApprovalCount = 0;
			int reviewsCount = 0;
			int newJoineeCnt = 0;
			int newJoineePendingCnt = 0;
			int learningGapsCount = 0;
			int managerGoalsCount = 0;
			int selfReviewRequestCnt = 0;
			/* int confirmations = 0;
			int resignations = 0;
			int finalDay = 0; */
			
			if (arrEnabledModules != null && arrEnabledModules.length>0) {

				if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT + "") >= 0) {
					nLeaveCount = uF.parseToInt(hmTaskNotification.get("LEAVE_REQUEST_CNT"));
				}
				
				if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) {
					nReimbursementCount = uF.parseToInt(hmTaskNotification.get("REIM_REQUEST_CNT"));
					nTravelCount = uF.parseToInt(hmTaskNotification.get("TRAVEL_CNT"));
				}
				
				if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) {
					requirementApprovalCount = uF.parseToInt(hmTaskNotification.get("REQUI_APPROVE_CNT"));
					jobcodeApprovalCount = uF.parseToInt(hmTaskNotification.get("JOBCODE_APPROVE_CNT"));
				}
				
				if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) {
					nPerkCount = uF.parseToInt(hmTaskNotification.get("PERK_REQUEST_CNT"));
				    nLTACount = uF.parseToInt(hmTaskNotification.get("LTA_REQUEST_CNT"));
					nLeaveEncashCount = uF.parseToInt(hmTaskNotification.get(IConstants.LEAVE_ENCASH_REQUEST_ALERT));
					nLoanCount = uF.parseToInt(hmTaskNotification.get(IConstants.LOAN_REQUEST_ALERT));
				}
				
				if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) {
					nRequisitionCount = uF.parseToInt(hmTaskNotification.get(IConstants.REQUISITION_REQUEST_ALERT));
					
				}
				
				if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
					reviewsCount = uF.parseToInt(hmTaskNotification.get("MANAGER_REVIEWS_CNT"));
					selfReviewRequestCnt = uF.parseToInt(hmTaskNotification.get(IConstants.SELF_REVIEW_REQUEST_ALERT));
					managerGoalsCount = uF.parseToInt(hmTaskNotification.get("MANAGER_GOALS_CNT"));
				}
				
				if (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) {
					learningGapsCount = uF.parseToInt(hmTaskNotification.get("MANGER_LEARNING_GAPS_CNT"));
				}
				
				
			        
            }
			
			nApprovalRequestCount = nLeaveCount + nReimbursementCount + nTravelCount + nPerkCount + nLTACount + nLeaveEncashCount + nRequisitionCount + nLoanCount;
			
			int requirementApprCount = requirementApprovalCount + jobcodeApprovalCount + reviewsCount+ managerGoalsCount + learningGapsCount + selfReviewRequestCnt;
			
			nApprovalRequestCount = nApprovalRequestCount + requirementApprCount;

			/* if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
				//newJoineeCnt = uF.parseToInt(hmTaskNotification.get("NEWJOINEE_CNT"));
				//newJoineePendingCnt = uF.parseToInt(hmTaskNotification.get(IConstants.NEW_JOINEE_PENDING_ALERT));
				learningGapsCount = uF.parseToInt(hmTaskNotification.get("MANGER_LEARNING_GAPS_CNT"));
				/* confirmations = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_CONFIRMATIONS_ALERT));
				resignations = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_RESIGNATIONS_ALERT));
				finalDay = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_FINAL_DAY_ALERT)); 
				
				int requirementApprCount = requirementApprovalCount + jobcodeApprovalCount + reviewsCount+ managerGoalsCount + learningGapsCount + selfReviewRequestCnt;
				
				nApprovalRequestCount = nApprovalRequestCount + requirementApprCount;
			} */
		%>		
		
       		<div style="background-image:url('images1/icons/icons/tick_on_file_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/tick_on_file_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/tick_on_file_icon_b.png\')'" class="divclass"></div>
				
				<div class="openans" style="display: none;">
					<div id="triangle"></div>
					<table border="0" width="100%"
						style="border-collapse: collapse;">
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT + "") >= 0) { %>
							<tr>  
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="ManagerLeaveApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_REQUEST_ALERT%>"
									title="Requests">Leave Request</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLeaveCount > 0) ? nLeaveCount : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_REQUEST_ALERT%>"
									title="Claims">Claims Request</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nReimbursementCount > 0) ? nReimbursementCount : ""%></td>
							</tr>
	
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="TravelApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_REQUEST_ALERT%>"
									title="Travel">Travel Request</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nTravelCount > 0) ? nTravelCount : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_REQUEST_ALERT%>"
									title="Perk">Perk Request</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nPerkCount > 0) ? nPerkCount : ""%></td>
							</tr>
							
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_REQUEST_ALERT%>"
									title="CTC Variable Request">CTC Variable</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLTACount > 0) ? nLTACount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_REQUEST_ALERT%>"
									title="Leave Encashment">Leave Encashment</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLeaveEncashCount > 0) ? nLeaveEncashCount : ""%></td>
							</tr>
					  <% } %>
					  
					  <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="RequisitionApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_REQUEST_ALERT%>"
									title="Requisition">Requisition</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nRequisitionCount > 0) ? nRequisitionCount : ""%></td>
							</tr>
						<% } %>
					
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_REQUEST_ALERT%>"
								title="Loan">Loan</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nLoanCount > 0) ? nLoanCount : ""%></td>
						</tr>
						
					<% } %>	
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_APPROVAL_ALERT%>"
								title="Requirements">Requirements</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(requirementApprovalCount > 0) ? requirementApprovalCount : ""%></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_APPROVAL_ALERT%>"
								title="Job Approvals">Job Approvals</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(jobcodeApprovalCount > 0) ? jobcodeApprovalCount : ""%></td>
						</tr>
					<% } %>
					
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
							<a href="Reviews.action?dataType=SRR&alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
								title="Hr Reviews">Self Review Requests</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=selfReviewRequestCnt > 0 ? selfReviewRequestCnt : "" %></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.MANAGER_REVIEW_ALERT%>"
								title="Reviews">Reviews</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(reviewsCount > 0) ? reviewsCount : ""%></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="GoalKRATargets.action?alertStatus=alert&alert_type=<%=IConstants.MANAGER_GOALS_ALERT%>"
								title="Goals">Goals</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(managerGoalsCount > 0) ? managerGoalsCount : ""%></td>
						</tr>
					<% } %>
						
						<%-- 
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>	
						<tr>
						<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
						<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
							title="New Joinee Pending">New Joinee Pending</a>
						</td>
						<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
							class="digital"><%=(newJoineePendingCnt>0) ? newJoineePendingCnt : ""%></td>
						</tr>
						<tr>
						<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
						<a href="EmployeeActivity.action?empType=I&alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
							title="New Joinees">New Joinees</a>
						</td>
						<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
							class="digital"><%=(newJoineeCnt>0) ? newJoineeCnt : ""%></td>
						</tr>
						
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
							<a href="EmployeeActivity.action?empType=C&alertStatus=alert&alert_type=<%=IConstants.EMP_CONFIRMATIONS_ALERT%>"
								title="confirmations">Confirmations</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(confirmations>0) ? confirmations : "" %></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
							<a href="EmployeeActivity.action?empType=R&alertStatus=alert&alert_type=<%=IConstants.EMP_RESIGNATIONS_ALERT%>"
								title="resignations">Resignations</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(resignations>0) ? resignations : "" %></td>
						</tr>
						
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
							<a href="EmployeeActivity.action?empType=FD&alertStatus=alert&alert_type=<%=IConstants.EMP_FINAL_DAY_ALERT%>"
								title="Final Day">Final Day</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(finalDay>0) ? finalDay : "" %></td>
						</tr> 
					  <%  } %>
						--%>
					  <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) { %>	
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.MANAGER_LEARNING_GAPS_ALERT%>"
								title="Learning Gaps">Learning Gaps</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(learningGapsCount > 0) ? learningGapsCount : ""%></td>
						</tr>
					 <% } %>
				</table>
			</div>	

         	<div id="idRequest">
         		<% if (nApprovalRequestCount > 0) { %>
				<div style="float: left;" class="h2class"><%=nApprovalRequestCount%></div>
				<div class="answer" style="display: none;">
					<div id="triangle"></div>
					<table border="0" width="100%"
						style="border-collapse: collapse;">
					  <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT + "") >= 0) { %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="ManagerLeaveApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_REQUEST_ALERT%>"
								title="Requests">Leave Request</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nLeaveCount > 0) ? nLeaveCount : ""%></td>
						</tr>
					<% } %>
					
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) { %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_REQUEST_ALERT%>"
								title="Claims">Claims Request</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nReimbursementCount > 0) ? nReimbursementCount : ""%></td>
						</tr>

						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="TravelApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_REQUEST_ALERT%>"
								title="Travel">Travel Request</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nTravelCount > 0) ? nTravelCount : ""%></td>
						</tr>
					<% } %>
					
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_REQUEST_ALERT%>"
								title="Perk">Perk Request</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nPerkCount > 0) ? nPerkCount : ""%></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_REQUEST_ALERT%>"
								title="CTC Variable Request">CTC Variable</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nLTACount > 0) ? nLTACount : ""%></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_REQUEST_ALERT%>"
								title="Leave Encashment">Leave Encashment</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nLeaveEncashCount > 0) ? nLeaveEncashCount : ""%></td>
						</tr>
					<% } %>
					
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="RequisitionApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_REQUEST_ALERT%>"
								title="Requisition">Requisition</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nRequisitionCount > 0) ? nRequisitionCount : ""%></td>
						</tr>
					<% } %>
					
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_REQUEST_ALERT%>"
								title="Loan">Loan</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nLoanCount > 0) ? nLoanCount : ""%></td>
						</tr>
					<% } %>
						
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_APPROVAL_ALERT%>"
									title="Requirements">Requirements</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=requirementApprovalCount > 0 ? requirementApprovalCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_APPROVAL_ALERT%>"
									title="Job Approvals">Job Approvals</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(jobcodeApprovalCount > 0) ? jobcodeApprovalCount : "" %></td>
							</tr>
					<% } %>
					
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?dataType=SRR&alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
									title="Hr Reviews">Self Review Requests</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=selfReviewRequestCnt > 0 ? selfReviewRequestCnt : "" %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.MANAGER_REVIEW_ALERT%>"
									title="Reviews">Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(reviewsCount > 0) ? reviewsCount : "" %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="GoalKRATargets.action?alertStatus=alert&alert_type=<%=IConstants.MANAGER_GOALS_ALERT%>"
									title="Goals">Goals</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(managerGoalsCount > 0) ? managerGoalsCount : "" %></td>
							</tr>
					<% } %>
							
							<%--
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>	
						 <tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
							<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
								title="New Joinee Pending">New Joinee Pending</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(newJoineePendingCnt > 0) ? newJoineePendingCnt : "" %></td>
							</tr>
							
							<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
							<a href="EmployeeActivity.action?empType=I&alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
								title="New Joinees">New Joinees</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(newJoineeCnt > 0) ? newJoineeCnt : "" %></td>
							</tr>
							
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeActivity.action?empType=C&alertStatus=alert&alert_type=<%=IConstants.EMP_CONFIRMATIONS_ALERT%>"
									title="confirmations">Confirmations</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(confirmations > 0) ? confirmations : "" %></td>
							</tr>
							
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeActivity.action?empType=R&alertStatus=alert&alert_type=<%=IConstants.EMP_RESIGNATIONS_ALERT%>"
									title="resignations">Resignations</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(resignations > 0) ? resignations : "" %></td>
							</tr>
							
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeActivity.action?empType=FD&alertStatus=alert&alert_type=<%=IConstants.EMP_FINAL_DAY_ALERT%>"
									title="Final Day">Final Day</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(finalDay > 0) ? finalDay : "" %></td>
							</tr> 
						<% }%>	
						--%>
					
					   <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) { %>			
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.MANAGER_LEARNING_GAPS_ALERT%>"
									title="Learning Gaps">Learning Gaps</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(learningGapsCount > 0) ? learningGapsCount : "" %></td>
							</tr>
					  <% } %>
					</table>
				</div>
				<% } %>
         	</div>
		</li>
		
		<!-- <li id="dashboard">
         	<a href="Approvals.action?NN=357" title="Approvals">
         		<div style="background-image:url('images1/icons/icons/tick_box_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/tick_box_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/tick_box_icon_b.png\')'"></div>
         	</a>
		</li> -->
		
		
  		<%-- <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
		<li id="dashboard">
		<%
			int jobcodeApprovalCount = uF.parseToInt(hmTaskNotification.get("JOBCODE_APPROVE_CNT"));
			int requirementApprovalCount = uF.parseToInt(hmTaskNotification.get("REQUI_APPROVE_CNT"));
			int reviewsCount = uF.parseToInt(hmTaskNotification.get("MANAGER_REVIEWS_CNT"));
			int newJoineeCnt = uF.parseToInt(hmTaskNotification.get("NEWJOINEE_CNT"));
			int newJoineePendingCnt = uF.parseToInt(hmTaskNotification.get(IConstants.NEW_JOINEE_PENDING_ALERT));
			int learningGapsCount = uF.parseToInt(hmTaskNotification.get("MANGER_LEARNING_GAPS_CNT"));
			int managerGoalsCount = uF.parseToInt(hmTaskNotification.get("MANAGER_GOALS_CNT"));
			int requirementApprCount = requirementApprovalCount + jobcodeApprovalCount + reviewsCount + managerGoalsCount + newJoineeCnt + newJoineePendingCnt + learningGapsCount;
		%>
         		<div style="background-image:url('images1/icons/icons/human_resource_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/human_resource_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/human_resource_icon_b.png\')'" class="divclass" ></div>
				
   				<div class="openans" style="display: none;">
					<div id="triangle"></div>
					<table border="0" width="100%" style="border-collapse: collapse;">
						<tr>
							<td nowrap
								style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_APPROVAL_ALERT%>"
								title="Requirements">Requirements</a>
							</td>
							<td
								style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(requirementApprovalCount > 0) ? requirementApprovalCount : ""%></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_APPROVAL_ALERT%>"
								title="Job Approvals">Job Approvals</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(jobcodeApprovalCount > 0) ? jobcodeApprovalCount : ""%></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.MANAGER_REVIEW_ALERT%>"
								title="Reviews">Reviews</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(reviewsCount > 0) ? reviewsCount : ""%></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="GoalKRATargets.action?alertStatus=alert&alert_type=<%=IConstants.MANAGER_GOALS_ALERT%>"
								title="Goals">Goals</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(managerGoalsCount > 0) ? managerGoalsCount : ""%></td>
						</tr>
						<tr>
						<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
						<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
							title="New Joinee Pending">New Joinee Pending</a>
						</td>
						<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
							class="digital"><%=(newJoineePendingCnt>0) ? newJoineePendingCnt : ""%></td>
						</tr>
						<tr>
						<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
						<a href="EmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
							title="New Joinees">New Joinees</a>
						</td>
						<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
							class="digital"><%=(newJoineeCnt>0) ? newJoineeCnt : ""%></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.MANAGER_LEARNING_GAPS_ALERT%>"
								title="Learning Gaps">Learning Gaps</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(learningGapsCount > 0) ? learningGapsCount : ""%></td>
						</tr>
					</table>
				</div> 
				
         	<div id="idApproval">
         		<%
         			if (requirementApprCount > 0) {
         		%>
					<div style="float: left;" class="h2class"><%=requirementApprCount %></div>
					<div class="answer" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_APPROVAL_ALERT%>"
									title="Requirements">Requirements</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=requirementApprovalCount > 0 ? requirementApprovalCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_APPROVAL_ALERT%>"
									title="Job Approvals">Job Approvals</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=jobcodeApprovalCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.MANAGER_REVIEW_ALERT%>"
									title="Reviews">Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=reviewsCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="GoalKRATargets.action?alertStatus=alert&alert_type=<%=IConstants.MANAGER_GOALS_ALERT%>"
									title="Goals">Goals</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=managerGoalsCount %></td>
							</tr>
							<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
							<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
								title="New Joinee Pending">New Joinee Pending</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=newJoineePendingCnt %></td>
							</tr>
							<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
							<a href="EmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
								title="New Joinees">New Joinees</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=newJoineeCnt%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.MANAGER_LEARNING_GAPS_ALERT%>"
									title="Learning Gaps">Learning Gaps</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=learningGapsCount%></td>
							</tr>
						</table>
					</div>
				<% } %>
         	</div>
		</li>
		<% } %> --%>
		
		
		
		<li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
		
		<%--  <li id="dashboard">
         	<div style="background-image:url('images1/icons/icons/mail_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_b.png\')'" class="divclass" ></div>
				
							<div class="openans" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%" style="border-collapse: collapse;">
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>"
											title="Mail">Mail</a>
										</td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) ? uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) : ""%></td>
									</tr>
								</table>
							</div>	
         	<div id="idMail">
         		<% if (uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) { %>
					<div style="float: left;" class="h2class"></div>
					<div class="answer" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>"
									title="Mail">Mail</a>
								</td>
								<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=uF.parseToInt(hmTaskNotification.get("MAIL_CNT"))%></td>
							</tr>
						</table>
					</div>
					<% } %>
				</div>
		</li> --%>
		
        </ul> 
        
        <% } else if (CF.isTermsCondition() && !CF.isForcePassword() && USERTYPE != null && USERTYPE.equalsIgnoreCase(IConstants.HRMANAGER)) { %>
        
         <ul id="topnav_manager" style="background: none;"> 
        <%--  <!-- <li id="dashboard"><a href="MyDashboard.action" title="My Dashboard"><img src="images/icons/grey/hr_dashboard.png" width="22px"/></a></li> -->
		   <li id="dashboard"><a href="MyDashboard.action" title="My Dashboard"></a></li>
           <li id="empty"></li>
           <li id="email"><a href="MyMail.action" title="Email"><span class="notification_green"><%=strMailCount %></span></a></li>
           <li id="myleave"><a href="EmployeeLeaveEntry.action" title="Leave Requests"><span class="notification"><%=strLeaveRequestCount %></span></a></li>
	       <li id="pending_exceptions"><a href="UpdateClockEntries.action" title="Pending Exceptions"><span class="notification"><%=strPendingExceptionCount %></span></a></li>
	       <li id="reimbursement_requests"><a href="Reimbursements.action" title="Reimbursement Requests"><span class="notification"><%=strPendingReimbursementCount %></span></a></li>
	       <li id="requisition_requests"><a href="Requisitions.action" title="Requisition Requests"><span class="notification"><%=strPendingRequisitionCount %></span></a></li>
	       <li id="daily_reports"><a href="ApproveExtraActivity.action" title="Daily Reports"><span class="notification_green"><%=strReportSentCount %></span></a></li>
	       <li id="completed_tasks"><a href="ViewAllProjects.action" title="Completed Tasks"><span class="notification_green"><%=strCompletedTaskCount %></span></a></li> --%>
	       
	       <li id="dashboard">
       		<div style="background-image:url('images1/icons/icons/list_view_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 17px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/list_view_g.png\')'"
			onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/list_view_b.png\')'" class="divclass"></div>
         	<div class="openans" style="display: none;">
				<div id="triangle"></div>
				<table border="0" width="100%" style="border-collapse: collapse;">
					<% if(CF.isWorkRig()){ %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
								<div style="float: left;"><a href="Login.action?role=3&product=2" title="Workrig"><img src="images1/icons/icons/workrig.png" style="width: 130px;"/></a></div>
								<div style="float: right; padding-right: 10px;"><img src="images1/icons/hd_tick_20x20.png" style="width: 20px; margin-top: 19px;"></div>
							</td>
						</tr>
					<%} %>
					<% if(CF.isTaskRig()){ %>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
								<a href="Login.action?role=3&product=3" title="TaskRig"> <img src="images1/icons/icons/taskrig.png" style="width: 130px;"/></a>
							</td>
						</tr>
					<%} %>
				</table>
			</div>
							
         </li>
         <li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
         
	    <li id="dashboard">
         	<!-- <a href="MyDashboard.action" title="Dashboard"> -->
         	<a href="Login.action?role=3&userscreen=myhome" title="My Home">
         		<div style="background-image:url('images1/icons/icons/Home_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/home_icon_g.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/Home_icon_b.png\')'"></div>	
         	</a>
         </li>
       	 
       	 <li id="dashboard">
         	<a href="OrganisationalChart.action" title="My Team">
         		<div style="background-image:url('images1/icons/icons/org_chat_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_g.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_b.png\')'"></div>	
         	</a>
		</li>
		
		<li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
		
		<%-- <%
			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE + "") >= 0) {
		%>
		<li id="dashboard">
		<%
			int nExceptionCount = uF.parseToInt(hmTaskNotification.get("EXCEPTION_CNT"));
		%>
         	<!-- <a href="UpdateClockEntries.action" title="Exceptions"> -->
         	<!-- <a href="#" title="Exceptions">  -->
         		<!-- <div style="float:left;" id="idEexception"></div> -->
       		<div style="background-image:url('images1/icons/icons/exclamation_point_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/exclamation_point_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/exclamation_point_icon_b.png\')'" class="divclass"></div>
				<div class="openans" style="display: none;" >
					<div id="triangle"></div>
					<table border="0" width="100%" style="border-collapse: collapse;">
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="UpdateClockEntries.action" title="Exceptions">Exceptions</a>
							</td>
							<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nExceptionCount > 0) ? nExceptionCount : "" %></td>
						</tr>
					</table>
				</div> <!-- </a> -->
				
         	<div id="idEexception">
         	<% if (nExceptionCount > 0) { %>
				<div style="float: left;" class="h2class"><%=nExceptionCount%></div>
				<div class="answer" style="display: none;">
					<div id="triangle"></div>
					<table border="0" width="100%" style="border-collapse: collapse;">
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="UpdateClockEntries.action" title="Exceptions">Exceptions</a>
							</td>
							<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;" class="digital"><%=nExceptionCount%></td>
						</tr>
					</table>
				</div>
			<% } %>
         	
         	</div>
		</li>
		<% } %> --%>
		
		<li id="dashboard">
		<%
			int nLeaveCount = 0;
			int nReimbursementCount = 0;
			int nTravelCount = 0;
			int nPerkCount = 0;
			int nLTACount = 0;
			int nLeaveEncashCount = 0;
			int nRequisitionCount = 0;
			int nLoanCount = 0;
			int nApprovalRequestCount = 0;
			
			int requirementRequestCount = 0;
			int jobApprovalRequestCount = 0;
			int newCandidateFillCount = 0;
			int hrReviewsCount = 0;
			
			int reviewFinalizationCnt = 0;
			int newJoineeCnt = 0;
			int newJoineePendingCnt = 0;
			int learningGapCount = 0;
			int candidateFinalizationCount = 0;
			int candidateOfferAcceptRejectCount = 0;
			int hrLearningFinalizationCount = 0;
			int selfReviewRequestCnt = 0;
			
			int confirmations = 0;
			int resignations = 0;
			int finalDay = 0;
			
			if (arrEnabledModules != null && arrEnabledModules.length>0 ) {
				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { 
					requirementRequestCount = uF.parseToInt(hmTaskNotification.get("REQUI_REQUEST_CNT"));
					jobApprovalRequestCount = uF.parseToInt(hmTaskNotification.get("JOBCODE_REQUEST_CNT"));
					candidateFinalizationCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_FINALIZATION_CNT"));
					candidateOfferAcceptRejectCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_OFFER_ACCEPT_REJECT_CNT"));
					newCandidateFillCount  = uF.parseToInt(hmTaskNotification.get(IConstants.NEW_CANDIDATE_FILL_ALERT));
				}	
			
				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT + "") >= 0) { 
					nLeaveCount = uF.parseToInt(hmTaskNotification.get("LEAVE_REQUEST_CNT"));
				}
				
				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { 
					nRequisitionCount = uF.parseToInt(hmTaskNotification.get(IConstants.REQUISITION_REQUEST_ALERT));
					newJoineeCnt = uF.parseToInt(hmTaskNotification.get("NEWJOINEE_CNT"));
					confirmations = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_CONFIRMATIONS_ALERT));
					resignations = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_RESIGNATIONS_ALERT));
					finalDay = uF.parseToInt(hmTaskNotification.get(IConstants.EMP_FINAL_DAY_ALERT));
				}
				
				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) { 
					nReimbursementCount = uF.parseToInt(hmTaskNotification.get("REIM_REQUEST_CNT"));
					nTravelCount = uF.parseToInt(hmTaskNotification.get("TRAVEL_CNT"));
				}
				
				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { 
					nPerkCount = uF.parseToInt(hmTaskNotification.get("PERK_REQUEST_CNT"));
					nLTACount = uF.parseToInt(hmTaskNotification.get("LTA_REQUEST_CNT"));
					nLeaveEncashCount = uF.parseToInt(hmTaskNotification.get(IConstants.LEAVE_ENCASH_REQUEST_ALERT));
					nLoanCount = uF.parseToInt(hmTaskNotification.get(IConstants.LOAN_REQUEST_ALERT));
				}
				
				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { 	
					hrReviewsCount = uF.parseToInt(hmTaskNotification.get("HR_REVIEWS_CNT"));
					reviewFinalizationCnt = uF.parseToInt(hmTaskNotification.get("REVIEW_FINALIZATION_CNT"));
					learningGapCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_GAPS_CNT"));
					hrLearningFinalizationCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_FINALIZATION_CNT"));
					selfReviewRequestCnt = uF.parseToInt(hmTaskNotification.get(IConstants.SELF_REVIEW_REQUEST_ALERT));
				}

				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) { 	
					learningGapCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_GAPS_CNT"));
					hrLearningFinalizationCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_FINALIZATION_CNT"));
				}
				
				nApprovalRequestCount = nLeaveCount + nReimbursementCount + nTravelCount + nPerkCount + nLTACount + nLeaveEncashCount + nRequisitionCount + nLoanCount;
				
				int requirementCount = requirementRequestCount + jobApprovalRequestCount + hrReviewsCount + reviewFinalizationCnt + newJoineeCnt + learningGapCount + candidateFinalizationCount
				+ candidateOfferAcceptRejectCount + newCandidateFillCount + hrLearningFinalizationCount + selfReviewRequestCnt + confirmations + resignations + finalDay;
				
				nApprovalRequestCount = nApprovalRequestCount + requirementCount;
			}
		%>		
		
       		<div style="background-image:url('images1/icons/icons/tick_on_file_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/tick_on_file_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/tick_on_file_icon_b.png\')'" class="divclass"></div>
			
				<div class="openans" style="display: none;">
					<div id="triangle"></div>
					<table border="0" width="100%"
						style="border-collapse: collapse;">
					  <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT + "") >= 0) { %>	
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="ManagerLeaveApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_REQUEST_ALERT%>"
								title="Requests">Leave Request</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nLeaveCount > 0) ? nLeaveCount : ""%></td>
						</tr>
					  <% } %>
					  
					  <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) { %>	
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_REQUEST_ALERT%>"
								title="Claims">Claims Request</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nReimbursementCount > 0) ? nReimbursementCount : ""%></td>
						</tr>

						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="TravelApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_REQUEST_ALERT%>"
								title="Travel">Travel Request</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nTravelCount > 0) ? nTravelCount : ""%></td>
						</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>	
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_REQUEST_ALERT%>"
								title="Perk">Perk Request</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nPerkCount > 0) ? nPerkCount : ""%></td>
						</tr>
						
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_REQUEST_ALERT%>"
								title="CTC Variable Request">CTC Variable</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nLTACount > 0) ? nLTACount : ""%></td>
						</tr>
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_REQUEST_ALERT%>"
								title="Leave Encashment">Leave Encashment</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nLeaveEncashCount > 0) ? nLeaveEncashCount : ""%></td>
						</tr>
					 <% } %>
					 
					 <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>	
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="RequisitionApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_REQUEST_ALERT%>"
								title="Requisition">Requisition</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nRequisitionCount > 0) ? nRequisitionCount : ""%></td>
						</tr>
					 <% } %>
					 
					 <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+ "") >= 0) { %>	
						<tr>
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
								href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_REQUEST_ALERT%>"
								title="Loan">Loan</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
								class="digital"><%=(nLoanCount > 0) ? nLoanCount : ""%></td>
						</tr>
					 <% } %>
						
					<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_REQUEST_ALERT%>"
									title="Requirements">Requirements</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(requirementRequestCount>0) ? requirementRequestCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_REQUEST_ALERT%>"
									title="Job Approvals">Job Approvals</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(jobApprovalRequestCount>0) ? jobApprovalRequestCount : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
							<%-- <tr>
								<td nowrap
									style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.NEW_REVIEW_ALERT%>"
									title="Reviews">Reviews</a>
								</td>
								<td
									style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(newReviewCount>0) ? newReviewCount : "" %></td>
							</tr> --%>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
									title="Hr Reviews">Self Review Requests</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(selfReviewRequestCnt>0) ? selfReviewRequestCnt : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.HR_REVIEW_ALERT%>"
									title="Hr Reviews">Hr Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(hrReviewsCount>0) ? hrReviewsCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.REVIEW_FINALIZATION_ALERT%>"
									title="Review Finalization">Review Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(reviewFinalizationCnt>0) ? reviewFinalizationCnt : ""%></td>
							</tr>
						<% } %>
						
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Offers.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_OFFER_ACCEPTREJECT_ALERT%>"
									title="Offer Accept/Reject">Offer Accept/Reject</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(candidateOfferAcceptRejectCount>0) ? candidateOfferAcceptRejectCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Applications.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_FINALIZATION_ALERT%>"
									title="Candidate Finalization">Candidate Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(candidateFinalizationCount>0) ? candidateFinalizationCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="CandidateReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_CANDIDATE_FILL_ALERT%>"
									title="New Candidate">New Candidate</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(newCandidateFillCount>0) ? newCandidateFillCount : ""%></td>
							</tr>
							<% } %>
							<%-- <tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
									title="New Joinee Pending">New Joinee Pending</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(newJoineePendingCnt>0) ? newJoineePendingCnt : ""%></td>
							</tr> --%>
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeActivity.action?empType=I&alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
									title="New Joinees">New Joinees</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(newJoineeCnt>0) ? newJoineeCnt : ""%></td>
							</tr>
							
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeActivity.action?empType=C&alertStatus=alert&alert_type=<%=IConstants.EMP_CONFIRMATIONS_ALERT%>"
									title="confirmations">Confirmations</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(confirmations > 0) ? confirmations : "" %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeActivity.action?empType=R&alertStatus=alert&alert_type=<%=IConstants.EMP_RESIGNATIONS_ALERT%>"
									title="resignations">Resignations</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(resignations > 0) ? resignations : "" %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeActivity.action?empType=FD&alertStatus=alert&alert_type=<%=IConstants.EMP_FINAL_DAY_ALERT%>"
									title="Final Day">Final Day</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(finalDay > 0) ? finalDay : "" %></td>
							</tr>
						  <% } %>	
						  
						  <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_GAPS_ALERT%>"
									title="Learning Gaps">Learning Gaps</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(learningGapCount>0) ? learningGapCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="LearningPlanAssessmentStatus.action?alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_FINALIZATION_ALERT%>"
									title="Learning Finalization">Learning Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(hrLearningFinalizationCount>0) ? hrLearningFinalizationCount : ""%></td>
							</tr>
						<% } %>
					</table>
				</div>	
				
         	<div id="idRequest">
         		<% if (nApprovalRequestCount > 0) { %>
					<div style="float: left;" class="h2class"><%=nApprovalRequestCount%></div>
					<div class="answer" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%"
							style="border-collapse: collapse;">
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="ManagerLeaveApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_REQUEST_ALERT%>"
										title="Requests">Leave Request</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nLeaveCount > 0) ? nLeaveCount : ""%></td>
								</tr>
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_REQUEST_ALERT%>"
										title="Claims">Claims Request</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nReimbursementCount > 0) ? nReimbursementCount : ""%></td>
								</tr>
		
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="TravelApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_REQUEST_ALERT%>"
										title="Travel">Travel Request</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nTravelCount > 0) ? nTravelCount : ""%></td>
								</tr>
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_REQUEST_ALERT%>"
										title="Perk">Perk Request</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nPerkCount > 0) ? nPerkCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_REQUEST_ALERT%>"
										title="CTC Variable Request">CTC Variable</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nLTACount > 0) ? nLTACount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_REQUEST_ALERT%>"
										title="Leave Encashment">Leave Encashment</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nLeaveEncashCount > 0) ? nLeaveEncashCount : ""%></td>
								</tr>
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="RequisitionApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_REQUEST_ALERT%>"
										title="Requisition">Requisition</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nRequisitionCount > 0) ? nRequisitionCount : ""%></td>
								</tr>
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT + "") >= 0) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_REQUEST_ALERT%>"
									title="Loan">Loan</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nLoanCount > 0) ? nLoanCount : ""%></td>
							</tr>
							
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_REQUEST_ALERT%>"
										title="Requirements">Requirements</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(requirementRequestCount>0) ? requirementRequestCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_REQUEST_ALERT%>"
										title="Job Approvals">Job Approvals</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(jobApprovalRequestCount>0) ? jobApprovalRequestCount : ""%></td>
								</tr>
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
										title="Hr Reviews">Self Review Requests</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(selfReviewRequestCnt>0) ? selfReviewRequestCnt : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.HR_REVIEW_ALERT%>"
										title="Hr Reviews">Hr Reviews</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(hrReviewsCount>0) ? hrReviewsCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.REVIEW_FINALIZATION_ALERT%>"
										title="Review Finalization">Review Finalization</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(reviewFinalizationCnt>0) ? reviewFinalizationCnt : ""%></td>
								</tr>
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Offers.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_OFFER_ACCEPTREJECT_ALERT%>"
										title="Offer Accept/Reject">Offer Accept/Reject</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(candidateOfferAcceptRejectCount>0) ? candidateOfferAcceptRejectCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Applications.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_FINALIZATION_ALERT%>"
										title="Candidate Finalization">Candidate Finalization</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(candidateFinalizationCount>0) ? candidateFinalizationCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="CandidateReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_CANDIDATE_FILL_ALERT%>"
										title="New Candidate">New Candidate</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(newCandidateFillCount>0) ? newCandidateFillCount : ""%></td>
								</tr>
								<% } %>
								<%-- <tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
										title="New Joinee Pending">New Joinee Pending</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(newJoineePendingCnt>0) ? newJoineePendingCnt : ""%></td>
								</tr> --%>
								<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT + "") >= 0) { %>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="EmployeeActivity.action?empType=I&alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
										title="New Joinees">New Joinees</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(newJoineeCnt>0) ? newJoineeCnt : ""%></td>
								</tr>
								
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="EmployeeActivity.action?empType=C&alertStatus=alert&alert_type=<%=IConstants.EMP_CONFIRMATIONS_ALERT%>"
										title="confirmations">Confirmations</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(confirmations > 0) ? confirmations : "" %></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="EmployeeActivity.action?empType=R&alertStatus=alert&alert_type=<%=IConstants.EMP_RESIGNATIONS_ALERT%>"
										title="resignations">Resignations</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(resignations > 0) ? resignations : "" %></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="EmployeeActivity.action?empType=FD&alertStatus=alert&alert_type=<%=IConstants.EMP_FINAL_DAY_ALERT%>"
										title="Final Day">Final Day</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(finalDay > 0) ? finalDay : "" %></td>
								</tr>
							<% } %>
							
							<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) { %>	
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_GAPS_ALERT%>"
										title="Learning Gaps">Learning Gaps</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(learningGapCount>0) ? learningGapCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="LearningPlanAssessmentStatus.action?alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_FINALIZATION_ALERT%>"
										title="Learning Finalization">Learning Finalization</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(hrLearningFinalizationCount>0) ? hrLearningFinalizationCount : ""%></td>
								</tr>
								
							<% } %>
						</table>
					</div>
				<% } %>
         	</div>
		</li>
		
		<!-- <li id="dashboard">
         	<a href="Approvals.action?NN=357" title="Approvals">
         		<div style="background-image:url('images1/icons/icons/tick_box_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/tick_box_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/tick_box_icon_b.png\')'"></div>
         	</a>
		</li> -->
		
		
		<%-- <% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
		<li id="dashboard">
			<%
				int requirementRequestCount = uF.parseToInt(hmTaskNotification.get("REQUI_REQUEST_CNT"));
				int jobApprovalRequestCount = uF.parseToInt(hmTaskNotification.get("JOBCODE_REQUEST_CNT"));
				int hrReviewsCount = uF.parseToInt(hmTaskNotification.get("HR_REVIEWS_CNT"));
				
				int reviewFinalizationCnt = uF.parseToInt(hmTaskNotification.get("REVIEW_FINALIZATION_CNT"));
				int newJoineeCnt = uF.parseToInt(hmTaskNotification.get("NEWJOINEE_CNT"));
				int newJoineePendingCnt = uF.parseToInt(hmTaskNotification.get(IConstants.NEW_JOINEE_PENDING_ALERT));
				int learningGapCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_GAPS_CNT"));
				int candidateFinalizationCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_FINALIZATION_CNT"));
				int candidateOfferAcceptRejectCount = uF.parseToInt(hmTaskNotification.get("CANDIDATE_OFFER_ACCEPT_REJECT_CNT"));
				int hrLearningFinalizationCount = uF.parseToInt(hmTaskNotification.get("HR_LEARNING_FINALIZATION_CNT"));
				int selfReviewRequestCnt = uF.parseToInt(hmTaskNotification.get(IConstants.SELF_REVIEW_REQUEST_ALERT));
				
				int requirementCount = requirementRequestCount + jobApprovalRequestCount + hrReviewsCount + reviewFinalizationCnt + newJoineeCnt + newJoineePendingCnt + learningGapCount + candidateFinalizationCount
				+ candidateOfferAcceptRejectCount + hrLearningFinalizationCount + selfReviewRequestCnt; //+ newReviewCount
			%>
         		<div style="background-image:url('images1/icons/icons/human_resource_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/human_resource_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/human_resource_icon_b.png\')'" class="divclass"></div>
					<div class="openans" style="display: none;">
							<div id="triangle"></div>
							<table border="0" width="100%" style="border-collapse: collapse;">
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_REQUEST_ALERT%>"
										title="Requirements">Requirements</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(requirementRequestCount>0) ? requirementRequestCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_REQUEST_ALERT%>"
										title="Job Approvals">Job Approvals</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(jobApprovalRequestCount>0) ? jobApprovalRequestCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
										title="Hr Reviews">Self Review Requests</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(selfReviewRequestCnt>0) ? selfReviewRequestCnt : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.HR_REVIEW_ALERT%>"
										title="Hr Reviews">Hr Reviews</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(hrReviewsCount>0) ? hrReviewsCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.REVIEW_FINALIZATION_ALERT%>"
										title="Review Finalization">Review Finalization</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(reviewFinalizationCnt>0) ? reviewFinalizationCnt : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Offers.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_OFFER_ACCEPTREJECT_ALERT%>"
										title="Offer Accept/Reject">Offer Accept/Reject</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(candidateOfferAcceptRejectCount>0) ? candidateOfferAcceptRejectCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Applications.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_FINALIZATION_ALERT%>"
										title="Candidate Finalization">Candidate Finalization</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(candidateFinalizationCount>0) ? candidateFinalizationCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT%>"
										title="New Joinee Pending">New Joinee Pending</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(newJoineePendingCnt>0) ? newJoineePendingCnt : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="EmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
										title="New Joinees">New Joinees</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(newJoineeCnt>0) ? newJoineeCnt : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_GAPS_ALERT%>"
										title="Learning Gaps">Learning Gaps</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(learningGapCount>0) ? learningGapCount : ""%></td>
								</tr>
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="LearningPlanAssessmentStatus.action?alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_FINALIZATION_ALERT%>"
										title="Learning Finalization">Learning Finalization</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(hrLearningFinalizationCount>0) ? hrLearningFinalizationCount : ""%></td>
								</tr>
							</table>
						</div> 
         	<div id="idApproval">
         	<%
         		if (requirementCount > 0) {
         	%>
					<div style="float: left;" class="h2class"><%=requirementCount%></div>
					<div class="answer" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%" style="border-collapse: collapse;">
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="RequirementApproval.action?alertStatus=alert&alert_type=<%=IConstants.REQUIREMENT_REQUEST_ALERT%>"
									title="Requirements">Requirements</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=requirementRequestCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="JobProfilesApproval.action?alertStatus=alert&alert_type=<%=IConstants.JOBCODE_REQUEST_ALERT%>"
									title="Job Approvals">Job Approvals</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=jobApprovalRequestCount > 0 ? jobApprovalRequestCount : ""%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.SELF_REVIEW_REQUEST_ALERT%>"
									title="Hr Reviews">Self Review Requests</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=selfReviewRequestCnt %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?callFrom=Dash&alertStatus=alert&alert_type=<%=IConstants.HR_REVIEW_ALERT%>"
									title="Hr Reviews">Hr Reviews</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=hrReviewsCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Reviews.action?alertStatus=alert&alert_type=<%=IConstants.REVIEW_FINALIZATION_ALERT%>"
									title="Review Finalization">Review Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=reviewFinalizationCnt %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Offers.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_OFFER_ACCEPTREJECT_ALERT%>"
									title="Offer Accept/Reject">Offer Accept/Reject</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=candidateOfferAcceptRejectCount %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Applications.action?alertStatus=alert&alert_type=<%=IConstants.CANDIDATE_FINALIZATION_ALERT%>"
									title="Candidate Finalization">Candidate Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=candidateFinalizationCount%></td>
							</tr>
							
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="PendingEmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEE_PENDING_ALERT %>"
									title="New Joinee Pending">New Joinee Pending</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=newJoineePendingCnt %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="EmployeeReport.action?alertStatus=alert&alert_type=<%=IConstants.NEW_JOINEES_ALERT%>"
									title="New Joinees">New Joinees</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=newJoineeCnt %></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="Learnings.action?callFrom=LA&alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_GAPS_ALERT%>"
									title="Learning Gaps">Learning Gaps</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=learningGapCount%></td>
							</tr>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="LearningPlanAssessmentStatus.action?alertStatus=alert&alert_type=<%=IConstants.HR_LEARNING_FINALIZATION_ALERT%>"
									title="Learning Finalization">Learning Finalization</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=hrLearningFinalizationCount %></td>
							</tr>
						</table>
					</div>
				<% } %>
         	</div>
		</li>
		<% } %> --%>
		
		
		
		<li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>

		<%-- <li id="dashboard">   
         		<div style="background-image:url('images1/icons/icons/mail_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_b.png\')'" class="divclass" ></div>
				
							<div class="openans" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%" style="border-collapse: collapse;">
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>"
											title="Mail">Mail</a>
										</td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) ? uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) : ""%></td>
									</tr>
								</table>
							</div>	
         	<div id="idMail">
         		<% if (uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) { %>
							<div style="float: left;" class="h2class"></div>
							<div class="answer" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%" style="border-collapse: collapse;">
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>" title="Mail">Mail</a>
										</td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=uF.parseToInt(hmTaskNotification.get("MAIL_CNT"))%></td>
									</tr>
								</table>
							</div>
							<%
								}
							%>
						</div></li> --%>
		
       </ul> 
         
       		<% } else if (CF.isTermsCondition() && !CF.isForcePassword() && USERTYPE != null && USERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT)) { %>
                        
			<ul id="topnav_admin" style="background: none;"> 
                      
				<li id="dashboard">
	       			<div style="background-image:url('images1/icons/icons/list_view_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 17px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/list_view_g.png\')'"
						onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/list_view_b.png\')'" class="divclass"></div>
			         	<div class="openans" style="display: none;">
							<div id="triangle"></div>
							<table border="0" width="100%" style="border-collapse: collapse;">
								<% if(CF.isWorkRig()){ %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
											<div style="float: left;"><a href="Login.action?role=3&product=2" title="Workrig"><img src="images1/icons/icons/workrig.png" style="width: 130px;"/></a></div>
											<div style="float: right; padding-right: 10px;"><img src="images1/icons/hd_tick_20x20.png" style="width: 20px; margin-top: 19px;"></div>
										</td>
									</tr>
								<%} %>
								<% if(CF.isTaskRig()){ %>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
											<a href="Login.action?role=3&product=3" title="TaskRig"> <img src="images1/icons/icons/taskrig.png" style="width: 130px;"/></a>
										</td>
									</tr>
								<%} %>
							</table>
						</div> 
	         	</li>
	         	
		         <li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
			         
				<li id="dashboard">
					<!-- <a href="MyDashboard.action" title="Control Panel"> -->
					<a href="Login.action?role=3&userscreen=myhome" title="My Home">
						<div style="background-image:url('images1/icons/icons/Home_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/home_icon_g.png\')'"
							onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/Home_icon_b.png\')'"></div>	
					</a>
				</li>
				
                    <li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
                        
	             <%-- <li id="dashboard">
					<%
						int nLeaveCount = uF.parseToInt(hmTaskNotification.get("LEAVE_REQUEST_CNT"));
						int nReimbursementCount = uF.parseToInt(hmTaskNotification.get("REIM_REQUEST_CNT"));
						int nTravelCount = uF.parseToInt(hmTaskNotification.get("TRAVEL_CNT"));
						int nPerkCount = uF.parseToInt(hmTaskNotification.get("PERK_REQUEST_CNT"));
						int nLTACount = uF.parseToInt(hmTaskNotification.get("LTA_REQUEST_CNT"));
						int nLeaveEncashCount = uF.parseToInt(hmTaskNotification.get(IConstants.LEAVE_ENCASH_REQUEST_ALERT));
						int nRequisitionCount = uF.parseToInt(hmTaskNotification.get(IConstants.REQUISITION_REQUEST_ALERT));
						int nLoanCount = uF.parseToInt(hmTaskNotification.get(IConstants.LOAN_REQUEST_ALERT));
						int nApprovalRequestCount = nLeaveCount + nReimbursementCount + nTravelCount + nPerkCount + nLTACount + nLeaveEncashCount + nRequisitionCount + nLoanCount;
					%>		
		
         		<div style="background-image:url('images1/icons/icons/tick_on_file_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/tick_on_file_icon_o.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/tick_on_file_icon_b.png\')'" class="divclass"></div>
				
							<div class="openans" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%"
									style="border-collapse: collapse;">
									<tr>  
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="ManagerLeaveApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_REQUEST_ALERT%>"
											title="Requests">Leave Request</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLeaveCount > 0) ? nLeaveCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_REQUEST_ALERT%>"
											title="Claims">Claims Request</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nReimbursementCount > 0) ? nReimbursementCount : ""%></td>
									</tr>

									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="TravelApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_REQUEST_ALERT%>"
											title="Travel">Travel Request</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nTravelCount > 0) ? nTravelCount : ""%></td>
									</tr>
									
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_REQUEST_ALERT%>"
											title="Perk">Perk Request</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nPerkCount > 0) ? nPerkCount : ""%></td>
									</tr>
									
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_REQUEST_ALERT%>"
											title="CTC Variable Request">CTC Variable</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLTACount > 0) ? nLTACount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_REQUEST_ALERT%>"
											title="Leave Encashment">Leave Encashment</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLeaveEncashCount > 0) ? nLeaveEncashCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="RequisitionApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_REQUEST_ALERT%>"
											title="Requisition">Requisition</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nRequisitionCount > 0) ? nRequisitionCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_REQUEST_ALERT%>"
											title="Loan">Loan</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLoanCount > 0) ? nLoanCount : ""%></td>
									</tr>
								</table>
				</div>	
         	<div id="idRequest">
         		<% if (nApprovalRequestCount > 0) { %>
							<div style="float: left;" class="h2class"><%=nApprovalRequestCount%></div>
							<div class="answer" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%"
									style="border-collapse: collapse;">
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="ManagerLeaveApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_REQUEST_ALERT%>"
											title="Requests">Leave Request</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLeaveCount > 0) ? nLeaveCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="Reimbursements.action?alertStatus=alert&alert_type=<%=IConstants.REIM_REQUEST_ALERT%>"
											title="Claims">Claims Request</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nReimbursementCount > 0) ? nReimbursementCount : ""%></td>
									</tr>

									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="TravelApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.TRAVEL_REQUEST_ALERT%>"
											title="Travel">Travel Request</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nTravelCount > 0) ? nTravelCount : ""%></td>
									</tr>
									
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="Perks.action?alertStatus=alert&alert_type=<%=IConstants.PERK_REQUEST_ALERT%>"
											title="Perk">Perk Request</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nPerkCount > 0) ? nPerkCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="CTCVariable.action?alertStatus=alert&alert_type=<%=IConstants.LTA_REQUEST_ALERT%>"
											title="CTC Variable Request">CTC Variable</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLTACount > 0) ? nLTACount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="LeaveEncashment.action?alertStatus=alert&alert_type=<%=IConstants.LEAVE_ENCASH_REQUEST_ALERT%>"
											title="Leave Encashment">Leave Encashment</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLeaveEncashCount > 0) ? nLeaveEncashCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="RequisitionApprovalReport.action?alertStatus=alert&alert_type=<%=IConstants.REQUISITION_REQUEST_ALERT%>"
											title="Requisition">Requisition</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nRequisitionCount > 0) ? nRequisitionCount : ""%></td>
									</tr>
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="LoanApplicationReport.action?alertStatus=alert&alert_type=<%=IConstants.LOAN_REQUEST_ALERT%>"
											title="Loan">Loan</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nLoanCount > 0) ? nLoanCount : ""%></td>
									</tr>
									
								</table>
							</div>
							<%
								}
							%>
	         	</div>
			</li>
                        
                        
                   	 <li id="dashboard">
                     	<div style="background-image:url('images1/icons/icons/mail_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_o.png\')'"
							onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_b.png\')'" class="divclass" ></div>
			
						<div class="openans" style="display: none;">
							<div id="triangle"></div>
							<table border="0" width="100%" style="border-collapse: collapse;">
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>"
										title="Mail">Mail</a>
									</td>
									<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) ? uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) : ""%></td>
								</tr>
							</table>
						</div>	
         			<div id="idMail">
         				<% if (uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) { %>
							<div style="float: left;" class="h2class"></div>
							<div class="answer" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%" style="border-collapse: collapse;">
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
											<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>"
											title="Mail">Mail</a>
										</td>
										<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=uF.parseToInt(hmTaskNotification.get("MAIL_CNT"))%></td>
									</tr>
								</table>
							</div>
							<%
								}
							%>
						</div>
               		</li> --%>
               		
                   </ul>
         
         <% } else if (CF.isTermsCondition() && !CF.isForcePassword() && USERTYPE != null && USERTYPE.equalsIgnoreCase(IConstants.CUSTOMER)) {
        	 
         } else if (CF.isTermsCondition() && !CF.isForcePassword() && USERTYPE != null) {
           %>
        <%} %> 
    </div> 
    
     <% if (USERTYPE != null) { 
     // && !USERTYPE.equalsIgnoreCase(IConstants.ADMIN)
     %>
	    <div class="topbar_time">
	       <span style="float:left; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px">It's </span><span id="serverTime"><%=strCurrentTime%> </span>
           <span style="float:left; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px"> now </span>
           <%-- <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0){ 
           	System.out.println("if");
           %> --%>
	          <%--  <a href="javascript:void(0)" onclick="getClockOnOff();" title="Clock on/off" style="width:30px;display:block;float:right;">
		       	<img src="<%=request.getContextPath() %>/images1/clock_red.png" />
	           </a> --%>
          <%--  <%}else{
        	   %> --%>
        	  <%--  <% if(PRODUCTTYPE != null && PRODUCTTYPE.equals("2")) { %>
		           <a href="MyDashboard.action" title="Clock on/off" style="width:30px;display:block;float:right;">
			      	 <img src="<%=request.getContextPath()%>/images1/clock_red.png" />
		           </a>     
	           <% } %> --%>
           <%-- <%} %> --%>
           
	    </div>
	    <script type="text/javascript">
	   /*  function getServerTime(){
	    	getContentAcs('serverTime','GetServerTime.action'); 	
	    }

	    setInterval ( "getServerTime()", 1000 ); */
	    </script>
  	<% } %>
  	
  	<% if (USERTYPE != null) { %>
    <div class="signout">
    <!-- <a href="javascript:void(0);" class="chat" onclick="return hs.htmlExpand(this);" title="chat"> |</a>
    <div class="highslide-maincontent">
			<h3>Please upgrade to get this feature</h3>
	</div> -->
	
	<% 
		if((BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.ADMIN)) || (BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.HRMANAGER))) {
	%>
    	<a href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>" style="background: none;" title="Control Panel">
    		<!-- <div style="background-image:url('images1/icons/icons/spanner_black.png');background-repeat:no-repeat; height:16px;" onmouseover="javascript:this.style.backgroundImage='url('images1/icons/icons/spanner_blue.png')'" onmouseout="this.style.backgroundImage='url('images1/icons/icons/spanner_black.png')'"></div> -->
    		<img src="<%=request.getContextPath()%>/images1/icons/icons/spanner_black.png" style="height: 16px; margin-top: 3px;" onmouseover="javascript:this.style.backgroundImage='url('images1/icons/icons/spanner_blue.png')'" onmouseout="this.style.backgroundImage='url('images1/icons/icons/spanner_black.png')'"/>
		</a> <%-- <img src="<%=request.getContextPath()%>/images1/icons/icons/spanner_black.png" style="height: 16px;"/> --%>
    <% } %>
    
    <a href="ChangePassword.action" class="setting_head" title="Change Password">s |</a> 
    <a href="Logout.action">Sign Out</a>
    </div>
    <%
    	if (EMPNAME != null) {
    %>  
    <div class="logininfo">
      
      
    <div class="usertypes">
	    <form name="frmRole" action="Login.action" style="width:auto;">
	    <!-- <input type="hidden" name="role" id="role"/> -->
	    <ul class="nav1">
		<li class="active" style="padding:0px 6px 7px 0px;">
			<a class="dropdown-toggle" data-toggle="dropdown" href="#" style="width:auto;vertical-align:top;padding-right: 12px; "><%=(String) session.getAttribute(IConstants.USERTYPE)%></a> 
	    	<ul class="dropdown-menu" role="menu">
				<li style="padding:3px 20px;color:#CCC;line-height:20px;">Use Workrig as</li> 
				 	<%-- <%
					 	if (BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) && uF.parseToBoolean((String) hmUserModules.get(IConstants.ADMIN))) {
					 %>
		    		<li> <a href="Login.action?role=1"><%=(String) IConstants.ADMIN%></a></li>  --%>
		    		<%
 		    			//}
  						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || BASEUSERTYPE
  							.equalsIgnoreCase(IConstants.HRMANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.MANAGER)) ) {
 		    		%>
		    		<li> <a href="Login.action?role=2"><%=(String) IConstants.MANAGER%></a></li>
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE)
							|| BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) 
							|| BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO)) 
							&& uF.parseToBoolean((String) hmUserModules.get(IConstants.EMPLOYEE))) {
		    		%>
		    		<li> <a href="Login.action?role=3"><%=(String) IConstants.EMPLOYEE%></a></li> 
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT)
							|| BASEUSERTYPE.equalsIgnoreCase(IConstants.CFO))
							&& uF.parseToBoolean((String) hmUserModules.get(IConstants.ACCOUNTANT))) {
		    		%>
		    		<li> <a href="Login.action?role=4"><%=(String) IConstants.ACCOUNTANT%></a></li>
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO))
   							&& uF.parseToBoolean((String) hmUserModules.get(IConstants.CEO))) {
		    		%>
		    		<%-- <li> <a href="Login.action?role=5"><%=(String)IConstants.CEO %></a></li> --%>
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CFO))
   							&& uF.parseToBoolean((String) hmUserModules.get(IConstants.CFO))) {
		    		%>
		    		<%-- <li> <a href="Login.action?role=6"><%=(String)IConstants.CFO %></a></li> --%>
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CFO) 
							|| BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER))
							&& uF.parseToBoolean((String) hmUserModules.get(IConstants.HRMANAGER))) {
		    		%>
		    		<li> <a href="Login.action?role=7"><%=(String) IConstants.HRMANAGER%></a></li>
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ARTICLE))
   							&& uF.parseToBoolean((String) hmUserModules.get(IConstants.ARTICLE))) {
		    		%>
		    		<li> <a href="Login.action?role=8"><%=(String) IConstants.ARTICLE%></a></li>
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CONSULTANT))
   							&& uF.parseToBoolean((String) hmUserModules.get(IConstants.CONSULTANT))) {
		    		%>
		    		<li> <a href="Login.action?role=9"><%=(String) IConstants.CONSULTANT%></a></li>
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.TRAINER)
   							|| BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER))
		    				&& uF.parseToBoolean((String) hmUserModules.get(IConstants.TRAINER))) {
		    		%>
		    		<li> <a href="Login.action?role=10"><%=(String) IConstants.TRAINER%></a></li>
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.RECRUITER) 
   							|| BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER)) 
   							&& uF.parseToBoolean((String) hmUserModules.get(IConstants.RECRUITER))) {
		    		%>
		    		<li> <a href="Login.action?role=11"><%=(String) IConstants.RECRUITER%></a></li>
		    		<%
		    			}
   						if ((BASEUSERTYPE.equalsIgnoreCase(IConstants.CUSTOMER)) && uF.parseToBoolean((String) hmUserModules.get(IConstants.CUSTOMER))) {
			    		%>
			    		<li> <a href="Login.action?role=12"><%=(String) IConstants.CUSTOMER%></a></li>
			    	<%
		    			}
   						if (BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) && uF.parseToBoolean((String) hmUserModules.get(IConstants.ADMIN))) {
   					 %>
    				    <li> <a href="Login.action?role=1"><%=(String) IConstants.ADMIN%></a></li>
		    		<% } %>
				</ul>
	    	</li>   
	    </ul>
     
    <%-- <select style="width:100px" name="role" onchange ="document.frmRole.submit();">
    <%
    	if(BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) && uF.parseToBoolean((String)hmUserModules.get(IConstants.ADMIN))){
    		%>
    		<option <%=(USERTYPEID.equalsIgnoreCase("1")?"selected":"") %> value="1">Admin</option>
    		<%
    	}if((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER)) && uF.parseToBoolean((String)hmUserModules.get(IConstants.MANAGER))){ 
    		%>
    		<option <%=(USERTYPEID.equalsIgnoreCase("2")?"selected":"") %> value="2">Manager</option>
    		<%
    	}if((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT)) && uF.parseToBoolean((String)hmUserModules.get(IConstants.EMPLOYEE))){
    		%>
    		<option <%=(USERTYPEID.equalsIgnoreCase("3")?"selected":"") %> value="3">Executive</option>
    		<%
    	}if((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CFO)) && uF.parseToBoolean((String)hmUserModules.get(IConstants.ACCOUNTANT))){
    		%>
    		<option <%=(USERTYPEID.equalsIgnoreCase("4")?"selected":"") %> value="4">Accountant</option>
    		<%
    	}
    	
    	 if((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO)) && uF.parseToBoolean((String)hmUserModules.get(IConstants.CEO))){
    		%>
    		 <option <%=(USERTYPEID.equalsIgnoreCase("5")?"selected":"") %> value="5">CEO</option> 
    		<%
    	}
    	   
    	if((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CFO)) && uF.parseToBoolean((String)hmUserModules.get(IConstants.CFO))){
    		%>
    		 <option <%=(USERTYPEID.equalsIgnoreCase("6")?"selected":"") %> value="6">CFO</option> 
    		<%
    	}
    	 
    	if((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CEO) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CFO) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER)) && uF.parseToBoolean((String)hmUserModules.get(IConstants.HRMANAGER))){
    		%>
    		<option <%=(USERTYPEID.equalsIgnoreCase("7")?"selected":"") %> value="7">HR Manager</option>
    		<%	
    	}
    	
    	if((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ARTICLE)) && uF.parseToBoolean((String)hmUserModules.get(IConstants.ARTICLE))){
    		%>
    		 <option <%=(USERTYPEID.equalsIgnoreCase("8")?"selected":"") %> value="8">Article</option> 
    		<%	
    	}if((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.CONSULTANT)) && uF.parseToBoolean((String)hmUserModules.get(IConstants.CONSULTANT))){
    		%>
    		 <option <%=(USERTYPEID.equalsIgnoreCase("9")?"selected":"") %> value="9">Consultant</option> 
    		<%	
    	} 
    	if((BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || BASEUSERTYPE.equalsIgnoreCase(IConstants.TRAINER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER)) && uF.parseToBoolean((String)hmUserModules.get(IConstants.TRAINER))){
    		%>
    		 <option <%=(USERTYPEID.equalsIgnoreCase("10")?"selected":"") %> value="10">Trainer</option> 
    		<%	
    	} 
    %> 
    </select> --%>
    </form>    
    </div>   
    
    <%-- <div style="float:right">Welcome <span> <%=EMPNAME%>!</span> You are logged in as </div> --%>
    <div style="float:right"> 
    <%-- <img class="lazy" height="22" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() + (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px"/> --%>
    <%if(CF.getStrDocSaveLocation()==null) { %>
		<img class="lazy" height="22" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px;border:1px solid #CCCCCC;"/>
	<% } else { %>
		<img class="lazy" height="22" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID) +"/"+ IConstants.I_22x22+"/"+ (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px;border:1px solid #CCCCCC;"/>
	<% } %>
    
    <span> <%=EMPNAME%></span> as
    
    </div>
    </div>
         
	<% } } %>
  </div>
  
  	<% } %>
  
  <div class="logo">
  	<%-- <img height="60" src="<%=CF.getStrDocRetriveLocation() + strLogo%>" /> --%>
  	<%
  	if (EMPNAME != null) {  // && uF.parseToInt(LOGINTYPE) == 1
  		
  		boolean logoFlag = uF.isFileExist(CF.getStrDocSaveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+strLogo);
  		if(logoFlag){
  %>
  		<img height="60" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
  	<%	} else { %>
  		<img height="60" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
  	<%}
    	} else {
    %>
    	<img height="60" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
    <%
    	}
    %>
  </div>
  
  	<div style="float: left; margin: 31px 0px 0px;"><div style="float: left; font-weight: bold; font-size: 10px; margin-top: 14px;">Powered By </div>
	<div style="float: left;"><img src="images1/icons/icons/workrig.png" style="width: 90px;"></div></div>
  
  <%
  	if (EMPNAME != null && uF.parseToInt(LOGINTYPE) == 1) {
  %>
  <div class="" style="float:left; margin:21px 0px 0px 1%;">        
             <form method="post" name="SearchEmployee" action="SearchEmployee.action" >
            <div style="float:left; font-size:12px; line-height:22px; width:380px">
                <span style="float:left;display:block; width:110px">Search Buddy :</span>
                <div style="border:solid 1px #68AC3B; margin:0px 0px 0px 5px; float:right; -moz-border-radius: 3px;	-webkit-border-radius: 3px;	border-radius: 3px;">
	                <div style="float:left">
	                	<input type="text" style="margin-left: 0px; border:0px solid #ccc; width:170px; box-shadow:0px 0px 0px #ccc" 
                        id="strFirstName" name="strFirstName" onclick="clearField(this.id);" onblur="fillField(this.id, 1);" value="First Name"> 
	              		<!--  <input type="text" style="margin-left: 5px;" id="strLastName" name="strLastName" onclick="clearField(this.id);" onblur="fillField(this.id, 2);" value="Last Name">-->
	              	</div>
	             	 <div style="float:right">
	                	<input type="submit" value="Search"  class="input_search" >
	                </div>
            	</div>
            </div>
            </form>
    </div>
    <% } %>
  
</div>

<!-- ************************************************************* END WORK RIG ************************************************************** -->

	<% 	} else if(PRODUCTTYPE != null && PRODUCTTYPE.equals("3")) { 
		String sbNotifications = (String)request.getAttribute("sbNotifications");
		String notificationCount = (String)request.getAttribute("notificationCount");
		//System.out.println("notificationCount ===>>> " + notificationCount);
	%>

<!-- ************************************************************* TASK RIG ************************************************************** 
							Date: 25-Sep-15  // --------- change code for flat tool bar --------- 
-->
	<%-- <%
		String strNotNewManual = (String) session.getAttribute("NEW_MANUAL");
		if (strNotNewManual == null || (strNotNewManual != null && strNotNewManual.equalsIgnoreCase("0")))
			strNotNewManual = "";
	
		String strMailCount = (String) request.getAttribute("MAIL_COUNT");
		if (strMailCount == null || (strMailCount != null && strMailCount.equalsIgnoreCase("0")))
			strMailCount = "";
	
		String strTaskCount = (String) session.getAttribute("TASK_COUNT");
		if (strTaskCount == null || (strTaskCount != null && strTaskCount.equalsIgnoreCase("0")))
			strTaskCount = "";
	
		String strCompletedTaskCount = (String) session.getAttribute("COMPLETED_TASK_COUNT");
		if (strCompletedTaskCount == null || (strCompletedTaskCount != null && strCompletedTaskCount.equalsIgnoreCase("0")))
			strCompletedTaskCount = "";
	%> --%> 

<script type="text/javascript">
	hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
	hs.outlineType = 'rounded-white';
	hs.wrapperClassName = 'draggable-header';
</script>


<div id="header">     

<% if (session.getAttribute("isApproved") == null) { %>
               
   <% if (USERTYPE != null && USERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
   <div class="topbar_emp">
   <% } else { %>  
   <div class="topbar">
   <% } %>
    <div class="topicons">    

         
      <% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && !BASEUSERTYPE.equalsIgnoreCase(IConstants.CUSTOMER)) { %>
         <ul id="topnav_admin" style="background: none;"> 
        
        <% if(uF.parseToInt(RESOURCE_OR_CONTRACTOR) != 2) { %> 
	        <li id="dashboard">
	        <% if(PRODUCTTYPE != null && PRODUCTTYPE.equals("2")) { %>
	         	<div style="float: left;" class="proshortname">WR</div>
	        <% } if(PRODUCTTYPE != null && PRODUCTTYPE.equals("3")) { %>
	       		<div style="float: left;" class="proshortname">TR</div>
	       	<% } %>	
	       		<div style="background-image:url('images1/icons/icons/list_view_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 17px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/list_view_g.png\')'"
				onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/list_view_b.png\')'" class="divclass"></div>
	         	<div class="openans" style="display: none;">
					<div id="triangle"></div>
					<table border="0" width="100%" style="border-collapse: collapse;">
						<% if(CF.isWorkRig()) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 20px;">
									<a href="Login.action?role=3&product=2" title="Workrig"><img src="images1/icons/icons/workrig.png" style="width: 130px;"/></a>
								</td>
							</tr>
						<%} %>
						<% if(CF.isTaskRig()) { %>
							<tr>
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px; padding-bottom: 17px;">
									<div style="float: left;"><a href="Login.action?role=3&product=3" title="TaskRig"> <img src="images1/icons/icons/taskrig.png" style="width: 130px;"/></a></div>
									<div style="float: right; padding-right: 10px;"><img src="images1/icons/hd_tick_20x20.png" style="width: 20px; margin-top: 19px;"></div>
								</td>
							</tr>
						<%} %>
					</table>
				</div>
	         </li>
	         
	         <li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li> 
         <% } %>
         
         <% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && (BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN) || 
        		 BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) 
        		 || BASEUSERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE))) { %>
	         <li id="dashboard">
	         	<!-- <a href="MyDashboard.action" title="My Dashboard"> -->
	         	<a href="Login.action?role=3&product=2&userscreen=myhome" title="My Home">
	         		<div style="background-image:url('images1/icons/icons/Home_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/home_icon_g.png\')'"
						onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/Home_icon_b.png\')'"></div>
				</a>
	         </li>
	         
	         <!-- <li id="dashboard">
	         	<a href="PeopleProfile.action" title="MyFacts">
	         		<div style="background-image:url('images1/icons/icons/profile_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/profile_icon_g.png\')'"
					onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/profile_icon_b.png\')'"></div>	
	         	</a>
			</li>

			<li id="dashboard">
	         	<a href="OrganisationalChart.action" title="My Team">
	         		<div style="background-image:url('images1/icons/icons/org_chat_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_g.png\')'"
					onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/org_chat_icon_b.png\')'"></div>	
	         	</a>
			</li> -->
		
	         <li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
	         
	         <% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && (BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) || 
        		 BASEUSERTYPE.equalsIgnoreCase(IConstants.EMPLOYEE) || BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN))) { %>
		         <li id="dashboard">
	         		<% 	/* int nMyNewTaskCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_ALLOCATE_ALERT));
	         			int nTaskRequestRescheduleCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_REQUEST_RESCHEDULE_ALERT));
	         			int nTaskRequestReassignCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_REQUEST_REASSIGN_ALERT));
	         			int nTaskRescheduleCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_RESCHEDULE_ALERT));
	         			int nTaskReassignCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_REASSIGN_ALERT));
	         			int nSharedNewDocumentsCount = uF.parseToInt(hmTaskNotification.get(IConstants.SHARE_DOCUMENTS_ALERT));
	         			
						int nMyTaskCount = nMyNewTaskCount + nTaskRequestRescheduleCount + nTaskRequestReassignCount + nTaskRescheduleCount + nTaskReassignCount + nSharedNewDocumentsCount;
						
						int nProCreateCount = uF.parseToInt(hmTaskNotification.get(IConstants.PRO_CREATED_ALERT));
	        			int nProCompleteCount = uF.parseToInt(hmTaskNotification.get(IConstants.PRO_COMPLETED_ALERT));
	        			int nNewResourceCount = uF.parseToInt(hmTaskNotification.get(IConstants.PRO_NEW_RESOURCE_ALERT));
	        			//int nNewTaskCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_ALLOCATE_ALERT));
	        			
	        			int nTaskRequestCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_NEW_REQUEST_ALERT));
	        			//int nTaskRecheduleReqCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_REQUEST_RESCHEDULE_ALERT));
	        			//int nTaskReassignReqCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_REQUEST_REASSIGN_ALERT));
	        			int nTaskAcceptCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_ACCEPT_ALERT));
	        			int nTaskCompletedCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_COMPLETED_ALERT));
	        			
	        			int nTimeSheetReceivedCount = uF.parseToInt(hmTaskNotification.get(IConstants.TIMESHEET_RECEIVED_ALERT));
	        			int nProInvoiceRequestCount = uF.parseToInt(hmTaskNotification.get(IConstants.PRO_RECURRING_BILLING_ALERT));
	        			int nInvoiceGeneratedCount = uF.parseToInt(hmTaskNotification.get(IConstants.INVOICE_GENERATED_ALERT));
	        			int nAddMemberCount = uF.parseToInt(hmTaskNotification.get(IConstants.ADD_MYTEAM_MEMBER_ALERT));
	        			
					nMyTaskCount += nProCreateCount + nProCompleteCount + nNewResourceCount; // + nNewTaskCount
					nMyTaskCount += nTaskRequestCount + nTaskAcceptCount + nTaskCompletedCount; //+ nTaskRecheduleReqCount + nTaskReassignReqCount 
					nMyTaskCount += nTimeSheetReceivedCount + nProInvoiceRequestCount + nInvoiceGeneratedCount + nAddMemberCount; */
					
					//System.out.println("sbNotifications --->> " + sbNotifications);
					%>
	         		
	         		<div style="background-image:url('images1/icons/icons/bell_grey.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/bell_orange.png\')'"
						onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/bell_grey.png\')'" class="divclass">
					</div>
						<div class="openans" style="display: none; float: left; width: 322px;">
						<div id="triangle"></div>
							<div>
								<% if (uF.parseToInt(notificationCount) > 0) { %>
									<div style="float: left; width: 100%; font-weight: bold; border-bottom: 2px solid lightgray;">
										<span style="float: left; margin-left: 5px;">Notifications</span>
										<a href="Notifications.action?operation=RAN" style="font-weight: normal; float: right; margin: 0px 20px; width: auto; height: 0px;">Mark Read All </a>
									</div>
								<% } %>
								<div id="idTask" style="float: left; width: 100%; max-height: 250px; overflow-y: auto;">
								<% if (uF.parseToInt(notificationCount) > 0) { %>
									<%=sbNotifications %>
								<% } else { %>
									<div style="float: left; color: gray; margin-left: 10px;">No notifications available.</div>
								<% } %>
								</div>
								<% if (uF.parseToInt(notificationCount) > 25) { %>
									<a href="Notifications.action" style="float: left; width: 100%; text-align: center; border-top: 2px solid lightgray; margin: 7px 0px -17px;">See All </a>
								<% } %>
							</div>
						</div>
					
						<% if (uF.parseToInt(notificationCount) > 0) { %>
							<div style="float: left;" class="h2class"><%=notificationCount%></div>
							<div class="answer" style="display: none; float: left; width: 322px; top: -16px;">
							<div id="triangle"></div>
								<div>
								<% if (uF.parseToInt(notificationCount) > 0) { %>
									<div style="float: left; width: 100%; font-weight: bold; border-bottom: 2px solid lightgray;">
										<span style="float: left; margin-left: 5px;">Notifications</span>
										<a href="Notifications.action?operation=RAN" style="font-weight: normal; float: right; margin: 0px 20px; width: auto; height: 0px;">Mark Read All </a>
									</div>
								<% } %>
								<div id="idTask" style="float: left; width: 100%; max-height: 250px; overflow-y: auto;">
								<% if (uF.parseToInt(notificationCount) > 0) { %>
									<%=sbNotifications %>
								<% } else { %>
									<div style="float: left; color: gray; margin-left: 10px;">No notifications available.</div>
								<% } %>
								</div>
								<% if (uF.parseToInt(notificationCount) > 25) { %>
									<a href="Notifications.action" style="float: left; width: 100%; text-align: center; border-top: 2px solid lightgray; margin: 7px 0px -17px;">See All </a>
								<% } %>
							</div>	
							</div>
						<% } %>
					
					
					<!-- <div class="openans" style="display: none; width: 322px;"> -->
						<!-- <div id="triangle"></div> -->
						<%-- <table border="0" width="100%" style="border-collapse: collapse;">
							<tr>  
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_ALLOCATE_ALERT%>"
									title="New Task Allocated">My New Task</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
									<%=(nMyNewTaskCount > 0) ? nMyNewTaskCount : ""%>
								</td>
							</tr>
							<tr>  
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_REQUEST_RESCHEDULE_ALERT%>"
									title="Task Request-Reschedule">My Task Request-Reschedule</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
									<%=(nTaskRequestRescheduleCount > 0) ? nTaskRequestRescheduleCount : ""%>
								</td>
							</tr>
							<tr>  
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_REQUEST_REASSIGN_ALERT%>"
									title="Task Request-Reassign">My Task Request-Reassign</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
									<%=(nTaskRequestReassignCount > 0) ? nTaskRequestReassignCount : ""%>
								</td>
							</tr>
							<tr>  
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_RESCHEDULE_ALERT%>"
									title="Task Rescheduled">Task Rescheduled</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
									<%=(nTaskRescheduleCount > 0) ? nTaskRescheduleCount : ""%>
								</td>
							</tr>
							<tr>  
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_REASSIGN_ALERT%>"
									title="Task Reassigned">Task Reassigned</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
									<%=(nTaskReassignCount > 0) ? nTaskReassignCount : ""%>
								</td>
							</tr>
							<tr>  
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
									<a href="DocumentListView.action?alertStatus=alert&alert_type=<%=IConstants.SHARE_DOCUMENTS_ALERT%>" 
										title="New Document shared">New Document shared</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
									<%=(nSharedNewDocumentsCount > 0) ? nSharedNewDocumentsCount : ""%>
								</td>
							</tr>
							
							<% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && (BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) 
								|| BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN))) { %>
			        		 <tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_CREATED_ALERT%>"
											title="New Project Created">New Project Created</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nProCreateCount > 0) ? nProCreateCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_COMPLETED_ALERT%>"
											title="Project Completed">Project Completed</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nProCompleteCount > 0) ? nProCompleteCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_NEW_RESOURCE_ALERT%>"
											title="New Resource added to Project">New Resource added to Project</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nNewResourceCount > 0) ? nNewResourceCount : ""%>
									</td>
								</tr>

								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.TASK_NEW_REQUEST_ALERT%>"
											title="New Task Requested by Resource">New Task Requested by Resource</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskRequestCount > 0) ? nTaskRequestCount : ""%>
									</td>
								</tr>
								
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.TASK_ACCEPT_ALERT%>"
											title="Task Accepted by Resource">Task Accepted by Resource</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskAcceptCount > 0) ? nTaskAcceptCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.TASK_COMPLETED_ALERT%>"
											title="Task Completed">Task Completed</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskCompletedCount > 0) ? nTaskCompletedCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ProjectTimesheets.action?alertStatus=alert&alert_type=<%=IConstants.TIMESHEET_RECEIVED_ALERT%>"
											title="New Timesheet received">New Timesheet received</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTimeSheetReceivedCount > 0) ? nTimeSheetReceivedCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ProjectBilling.action?alertStatus=alert&alert_type=<%=IConstants.PRO_RECURRING_BILLING_ALERT%>"
											title="Request for Invoice generated">Request for Invoice generated</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nProInvoiceRequestCount > 0) ? nProInvoiceRequestCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ProjectBilling.action?alertStatus=alert&alert_type=<%=IConstants.INVOICE_GENERATED_ALERT%>"
											title="Invoice">Invoice</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nInvoiceGeneratedCount > 0) ? nInvoiceGeneratedCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="People.action?alertStatus=alert&alert_type=<%=IConstants.ADD_MYTEAM_MEMBER_ALERT%>"
											title="New Member added in my Team">New Member added in my Team</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nAddMemberCount > 0) ? nAddMemberCount : ""%>
									</td>
								</tr>
			        		 <% } %>
						</table> --%>
					<!-- </div> -->
					
					<%-- <div id="idTask">
		         	<% if (nMyTaskCount > 0) { %>
						<div style="float: left;" class="h2class"><%=nMyTaskCount%></div>
						<div class="answer" style="display: none; width: 322px;">
							<div id="triangle"></div>
							<table border="0" width="100%" style="border-collapse: collapse;">
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_ALLOCATE_ALERT%>"
											title="New Task Allocated">My New Task</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(nMyNewTaskCount > 0) ? nMyNewTaskCount : ""%></td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_REQUEST_RESCHEDULE_ALERT%>"
											title="Task Request-Reschedule">My Task Request-Reschedule</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskRequestRescheduleCount > 0) ? nTaskRequestRescheduleCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_REQUEST_REASSIGN_ALERT%>"
											title="Task Request-Reassign">My Task Request-Reassign</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskRequestReassignCount > 0) ? nTaskRequestReassignCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
										href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_RESCHEDULE_ALERT%>"
										title="Task Rescheduled">Task Rescheduled</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskRescheduleCount > 0) ? nTaskRescheduleCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="EmpViewProject.action?alertStatus=alert&alert_type=<%=IConstants.TASK_REASSIGN_ALERT%>"
											title="Task Reassigned">Task Reassigned</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskReassignCount > 0) ? nTaskReassignCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="DocumentListView.action?alertStatus=alert&alert_type=<%=IConstants.SHARE_DOCUMENTS_ALERT%>" 
											title="New Document shared">New Document shared</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nSharedNewDocumentsCount > 0) ? nSharedNewDocumentsCount : ""%>
									</td>
								</tr>
								
							<% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && (BASEUSERTYPE.equalsIgnoreCase(IConstants.MANAGER) 
								|| BASEUSERTYPE.equalsIgnoreCase(IConstants.HRMANAGER) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN))) { %>
			        		 	<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_CREATED_ALERT%>"
										 	title="New Project Created">New Project Created</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nProCreateCount > 0) ? nProCreateCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_COMPLETED_ALERT%>"
											title="Project Completed">Project Completed</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nProCompleteCount > 0) ? nProCompleteCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_NEW_RESOURCE_ALERT%>"
											title="New Resource added to Project">New Resource added to Project</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nNewResourceCount > 0) ? nNewResourceCount : ""%>
									</td>
								</tr>
								
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.TASK_NEW_REQUEST_ALERT%>"
											title="New Task Requested by Resource">New Task Requested by Resource</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskRequestCount > 0) ? nTaskRequestCount : ""%>
									</td>
								</tr>
								
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.TASK_ACCEPT_ALERT%>"
											title="Task Accepted by Resource">Task Accepted by Resource</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskAcceptCount > 0) ? nTaskAcceptCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.TASK_COMPLETED_ALERT%>"
											title="Task Completed">Task Completed</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTaskCompletedCount > 0) ? nTaskCompletedCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="ProjectTimesheets.action?alertStatus=alert&alert_type=<%=IConstants.TIMESHEET_RECEIVED_ALERT%>"
											title="New Timesheet received">New Timesheet received</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nTimeSheetReceivedCount > 0) ? nTimeSheetReceivedCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="Billing.action?alertStatus=alert&alert_type=<%=IConstants.PRO_RECURRING_BILLING_ALERT%>"
											title="Request for Invoice generated">Request for Invoice generated</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nProInvoiceRequestCount > 0) ? nProInvoiceRequestCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="Billing.action?alertStatus=alert&alert_type=<%=IConstants.INVOICE_GENERATED_ALERT%>"
											title="Invoice">Invoice</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nInvoiceGeneratedCount > 0) ? nInvoiceGeneratedCount : ""%>
									</td>
								</tr>
								<tr>  
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="People.action?alertStatus=alert&alert_type=<%=IConstants.ADD_MYTEAM_MEMBER_ALERT%>"
											title="New Member added in my Team">New Member added in my Team</a>
									</td>
									<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
										<%=(nAddMemberCount > 0) ? nAddMemberCount : ""%>
									</td>
								</tr>
			        		 <% } %>
			        		 
							</table>
						</div>
						<% } %>
		         	</div> --%>
				</li>
	         <% } %>
	         
	         <%-- <% if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && (BASEUSERTYPE.equalsIgnoreCase(IConstants.ACCOUNTANT) || BASEUSERTYPE.equalsIgnoreCase(IConstants.ADMIN))) { %>
        		 <li id="dashboard">
					<% 	int nProRecurringBillCount = uF.parseToInt(hmTaskNotification.get(IConstants.PRO_RECURRING_BILLING_ALERT));
						int nProBillingCount = nProRecurringBillCount;
					%>
			
	         		<div style="background-image:url('images1/icons/icons/billing_icon.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/billing_icon.png\')'"
						onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/billing_icon.png\')'" class="divclass"></div>
				
					<div class="openans" style="display: none;">
						<div id="triangle"></div>
						<table border="0" width="100%"
							style="border-collapse: collapse;">
							<tr>  
								<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
									href="Billing.action?alertStatus=alert&alert_type=<%=IConstants.PRO_RECURRING_BILLING_ALERT%>"
									title="Recurring Billing">Recurring Billing</a>
								</td>
								<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
									class="digital"><%=(nProRecurringBillCount > 0) ? nProRecurringBillCount : ""%></td>
							</tr>
						</table>
					</div>	
		         	<div id="idProBilling">
		         		<% if (nProBillingCount > 0) { %>
							<div style="float: left;" class="h2class"><%=nProBillingCount%></div>
							<div class="answer" style="display: none;">
								<div id="triangle"></div>
								<table border="0" width="100%"
									style="border-collapse: collapse;">
									<tr>
										<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;"><a
											href="Billing.action?alertStatus=alert&alert_type=<%=IConstants.PRO_RECURRING_BILLING_ALERT%>"
											title="Recurring Billing">Recurring Billing</a>
										</td>
										<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"
											class="digital"><%=(nProRecurringBillCount > 0) ? nProRecurringBillCount : ""%></td>
									</tr>
								</table>
							</div>
						<% } %>
		         	</div>
				</li>
        	 <% } %> --%>
        	 	 
		       	 <%-- <li id="dashboard">
		         		<div style="background-image:url('images1/icons/icons/mail_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_o.png\')'"
							onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/mail_icon_b.png\')'" class="divclass" ></div>
						<div class="openans" style="display: none;">
							<div id="triangle"></div>
							<table border="0" width="100%"
								style="border-collapse: collapse;">
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>" title="Mail">Mail</a>
									</td>
									<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=(uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) ? uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) : ""%></td>
								</tr>
							</table>
						</div>	
		         	<div id="idMail">
		         		<% if (uF.parseToInt(hmTaskNotification.get("MAIL_CNT")) > 0) { %>
						<div style="float: left;" class="h2class"></div>
						<div class="answer" style="display: none;">
							<div id="triangle"></div>
							<table border="0" width="100%"
								style="border-collapse: collapse;">
								<tr>
									<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
										<a href="MyMail.action?alertStatus=alert&alert_type=<%=IConstants.UNREAD_MAIL_ALERT%>"
										title="Mail">Mail</a>
									</td>
									<td nowrap style="border-bottom: 1px solid #cccccc; vertical-align: top;"
										class="digital"><%=uF.parseToInt(hmTaskNotification.get("MAIL_CNT"))%></td>
								</tr>
							</table>
						</div>
						<% } %>
					</div>
				</li> --%>
			<% } %>
        </ul>
      <% } else if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null && BASEUSERTYPE.equalsIgnoreCase(IConstants.CUSTOMER)) { %>
           
          <ul id="topnav_admin" style="background: none;"> 
		       <li id="dashboard">
		         	<a href="ViewAllProjects.action" title="My Projects">
		         		<div style="background-image:url('images1/icons/icons/Home_icon_b.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 4px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/home_icon_g.png\')'"
						onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/Home_icon_b.png\')'"></div>	
		         	</a>
	         	
	        	 </li>
	        	 <li id="empty" style="color:#ccc;font-size:16px;width:15px">|</li>
	        	 
	        	 <li id="dashboard">
				<%
					int nProCreateCount = uF.parseToInt(hmTaskNotification.get(IConstants.PRO_CREATED_ALERT));
					int nProCompleteCount = uF.parseToInt(hmTaskNotification.get(IConstants.PRO_COMPLETED_ALERT));
					int nTaskCompletedCount = uF.parseToInt(hmTaskNotification.get(IConstants.TASK_COMPLETED_ALERT));
					int nInvoiceGeneratedCount = uF.parseToInt(hmTaskNotification.get(IConstants.INVOICE_GENERATED_ALERT));
					int nSharedNewDocumentsCount = uF.parseToInt(hmTaskNotification.get(IConstants.SHARE_DOCUMENTS_ALERT));
	     			
					int nTaskCount = nProCreateCount + nProCompleteCount + nTaskCompletedCount + nInvoiceGeneratedCount + nSharedNewDocumentsCount;
				%>
         		
         		<div style="background-image:url('images1/icons/icons/bell_grey.png');background-repeat:no-repeat;width:24px;height:24px;margin-top: 18px;" onmouseover="javascript:this.style.backgroundImage='url(\'images1/icons/icons/bell_orange.png\')'"
					onmouseout="this.style.backgroundImage='url(\'images1/icons/icons/bell_grey.png\')'" class="divclass">
				</div>
				<div class="openans" style="display: none; width: 200px;">
					<div id="triangle"></div>
					<table border="0" width="100%" style="border-collapse: collapse;">
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_CREATED_ALERT%>"
									title="New Project Created">New Project Created</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nProCreateCount > 0) ? nProCreateCount : ""%>
							</td>
						</tr>
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_COMPLETED_ALERT%>"
									title="Project Completed">Project Completed</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nProCompleteCount > 0) ? nProCompleteCount : ""%>
							</td>
						</tr>
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.TASK_COMPLETED_ALERT%>"
									title="Task Completed">Task Completed</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nTaskCompletedCount > 0) ? nTaskCompletedCount : ""%>
							</td>
						</tr>
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="ProjectBilling.action?alertStatus=alert&alert_type=<%=IConstants.INVOICE_GENERATED_ALERT%>" title="Invoice">Invoice</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nInvoiceGeneratedCount > 0) ? nInvoiceGeneratedCount : ""%>
							</td>
						</tr>
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="DocumentListView.action?alertStatus=alert&alert_type=<%=IConstants.SHARE_DOCUMENTS_ALERT%>" 
									title="New Document shared">New Document shared</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nSharedNewDocumentsCount > 0) ? nSharedNewDocumentsCount : ""%>
							</td>
						</tr>
					</table>
				</div>
				
				<div id="idTask">
         	<% if (nTaskCount > 0) { %>
				<div style="float: left;" class="h2class"><%=nTaskCount%></div>
				<div class="answer" style="display: none; width: 200px;">
					<div id="triangle"></div>
					<table border="0" width="100%" style="border-collapse: collapse;">
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_CREATED_ALERT%>"
									title="New Project Created">New Project Created</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nProCreateCount > 0) ? nProCreateCount : ""%>
							</td>
						</tr>
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.PRO_COMPLETED_ALERT%>"
									title="Project Completed">Project Completed</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nProCompleteCount > 0) ? nProCompleteCount : ""%>
							</td>
						</tr>
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="ViewAllProjects.action?alertStatus=alert&alert_type=<%=IConstants.TASK_COMPLETED_ALERT%>"
									title="Task Completed">Task Completed</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nTaskCompletedCount > 0) ? nTaskCompletedCount : ""%>
							</td>
						</tr>
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="ProjectBilling.action?alertStatus=alert&alert_type=<%=IConstants.INVOICE_GENERATED_ALERT%>"
									title="Invoice">Invoice</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nInvoiceGeneratedCount > 0) ? nInvoiceGeneratedCount : ""%>
							</td>
						</tr>
						<tr>  
							<td nowrap style="border-bottom: 1px solid #cccccc; padding-left: 4px;">
								<a href="DocumentListView.action?alertStatus=alert&alert_type=<%=IConstants.SHARE_DOCUMENTS_ALERT%>" 
									title="New Document shared">New Document shared</a>
							</td>
							<td style="border-bottom: 1px solid #cccccc; vertical-align: top;"class="digital">
								<%=(nSharedNewDocumentsCount > 0) ? nSharedNewDocumentsCount : ""%>
							</td>
						</tr>
					</table>
				</div>
				<% } %>
         	</div>
					
			</li>
	        	 
        	</ul> 
        <% } else if (CF.isTermsCondition() && !CF.isForcePassword() && BASEUSERTYPE != null) { %>
        
        <%} %> 
    </div> 
    
	    <div class="topbar_time">
	       <span style="float:left; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px">It's </span><span id="serverTime"><%=strCurrentTime%> </span>
           <span style="float:left; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px"> now </span>
	    </div>
  	
  	<% if (USERTYPE != null) { %>
    <div class="signout">
    
    <% if((BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.ADMIN)) || (BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.HRMANAGER))) { %>
    	<a href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>" style="background: none;" title="Control Panel">
    		<img src="<%=request.getContextPath()%>/images1/icons/icons/spanner_black.png" style="height: 16px; margin-top: 3px;" onmouseover="javascript:this.style.backgroundImage='url('images1/icons/icons/spanner_blue.png')'" onmouseout="this.style.backgroundImage='url('images1/icons/icons/spanner_black.png')'"/>
		</a>
    <% } %>
    
    <a href="ChangePassword.action" class="setting_head" title="Change Password">s |</a> 
    <a href="Logout.action">Sign Out</a>
    </div>
    <% if (EMPNAME != null) { %>
    <div class="logininfo">
	    <div style="float:right"> 
	    <%if(CF.getStrDocSaveLocation()==null) { %>
			<img class="lazy" height="22" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px;border:1px solid #CCCCCC;"/>
		<%} else { %>
			<img class="lazy" height="22" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID) +"/"+ IConstants.I_22x22+"/"+ (String) session.getAttribute(IConstants.PROFILE_IMG)%>" style="float:left;margin-right:5px;border:1px solid #CCCCCC;"/>
		<%} %>
	   	<a href="PeopleProfile.action?empId=<%=(String) session.getAttribute(IConstants.EMPID) %>"> <span style="color: black;"> <%=EMPNAME%></span> </a>
	    </div>
    </div>
	<% } } %>
  </div>
  
  	<% } %>
  
  <div class="logo">
  <%
  	if (EMPNAME != null) {  // && uF.parseToInt(LOGINTYPE) == 1
  		
  		boolean logoFlag = uF.isFileExist(CF.getStrDocSaveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+strLogo);
  		if(logoFlag){
  %>
  		<img height="60" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
  	<%	} else { %>
  		<img height="60" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
  	<%}
    	} else {
    %>
    	<img height="60" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strLogo%>" />
    <% } %>
  </div>
  
  	<div style="float: left; margin: 31px 0px 0px;">
  		<div style="float: left; font-weight: bold; font-size: 10px; margin-top: 14px;">Powered By </div>
		<div style="float: left;"><img src="images1/icons/icons/taskrig.png" style="width: 90px;"></div>
	</div>
  <%
  	if (EMPNAME != null && uF.parseToInt(LOGINTYPE) == 1 && uF.parseToInt(RESOURCE_OR_CONTRACTOR) != 2) {
  %>
  <div class="" style="float:left; margin:21px 0px 0px 1%;">        
             <form method="post" name="SearchEmployee" action="SearchEmployee.action" >
            <div style="float:left; font-size:12px; line-height:22px; width:380px">
                <span style="float:left;display:block; width:110px">Search Buddy :</span>
                <div style="border:solid 1px #68AC3B; margin:0px 0px 0px 5px; float:right; -moz-border-radius: 3px;	-webkit-border-radius: 3px;	border-radius: 3px;">
	                <div style="float:left">
	                	<input type="text" style="margin-left: 0px; border:0px solid #ccc; width:170px; box-shadow:0px 0px 0px #ccc" 
                        id="strFirstName" name="strFirstName" onclick="clearField(this.id);" onblur="fillField(this.id, 1);" value="First Name"> 
	              	</div>
	             	 <div style="float:right">
	                	<input type="submit" value="Search"  class="input_search" >
	                </div>
            	</div>
            </div>
            </form>
    </div>
    <% } %>
</div>

<% } %>

<!-- *********************************************************** END TASK RIG ************************************************************** -->





<script>
jQuery(document).ready(function() {
	//getContentAcs("idMail", "TaskBarNotifications.action?type=1");
	//getContentAcs("idEexception", "TaskBarNotifications.action?type=2");
	//getContentAcs("idRequest", "TaskBarNotifications.action?type=3");
	//getContentAcs("idApproval", "TaskBarNotifications.action?type=4"); 
	//getContentAcs("idPayCheck", "TaskBarNotifications.action?type=5");
	//getContentAcs("idLeaveApprove", "TaskBarNotifications.action?type=6");
	
	call();
	$('.openans').hide();
	$('.divclass').toggle(
			function() {
		       $(this).next('.openans').fadeIn();       
			   $(this).addClass('close');	
			   $('.answer').hide();
			},
			function() {
			   $(this).next('.openans').fadeOut();
			   $(this).removeClass('close');
		  }
		); // end toggle

		$('a').click(function(){
			$('.openans').fadeOut();
			$(this).removeClass('close');
		});
	    
		$('html').click(function(){
			$('.openans').fadeOut();
			$(this).removeClass('close');
		});
	
});

function clearField(elementId){
	document.getElementById(elementId).value = '';
}
function fillField(elementId, num){
	if(document.getElementById(elementId).value=='' && num==1){
		document.getElementById(elementId).value="First Name";
	}
	if(document.getElementById(elementId).value=='' && num==2){
		document.getElementById(elementId).value="Last Name";
	} 
	if(document.getElementById(elementId).value=='' && num==3){
		document.getElementById(elementId).value="From Date";
	}
	if(document.getElementById(elementId).value=='' && num==4){
		document.getElementById(elementId).value="To Date";
	}
}


</script>




<script>

function call() {

//	alert('calling...');
$('.answer').hide();
	$('.h2class').toggle(
			function() {
		       $(this).next('.answer').fadeIn();
			   $(this).addClass('close');
			   $('.openans').hide();
			},
			function() {
			   $(this).next('.answer').fadeOut();
			   $(this).removeClass('close');
		  }
		); // end toggle

		$('a').click(function(){
			$('.answer').fadeOut();
			$(this).removeClass('close');
		});
	    
		$('html').click(function(){
			$('.answer').fadeOut();
			$(this).removeClass('close');
		});

	
}

function getClockOnOff() {
	
	
	removeLoadingDiv('the_div');
	
	var dialogEdit = '#clockOnOffDiv';
	var data1 = "<div id=\"the_div\"><div id=\"ajaxLoadImage\"></div></div>";
	var dialogEdit = $(data1).appendTo('body');
	
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 260,
				width : 395,
				modal : true,
				title : 'Clock On/Off',
				open : function() {
					var xhr = $.ajax({  
						url : 'ClockOnOff.action',
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
					xhr = null;
  
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});

	$(dialogEdit).dialog('open');
}

</script>      
<div id="clockOnOffDiv"></div>

<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>
