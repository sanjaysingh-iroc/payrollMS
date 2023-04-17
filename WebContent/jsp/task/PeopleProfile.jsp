
<%@page import="ChartDirector.*"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="scripts/timeline/css/reset.css"> <!-- CSS reset -->
<link rel="stylesheet" href="scripts/timeline/css/style.css"> <!-- Resource style -->
<script src="scripts/timeline/js/modernizr.js"></script> <!-- Modernizr -->

<!-- Include jQuery form & jQuery script file. -->
<%-- <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8/jquery.js" ></script> --%>
<script src="http://malsup.github.com/jquery.form.js" ></script>
<script src="scripts/fileupload/js/fileUploadScript.js" ></script>
<!-- Include css styles here -->
<link href="scripts/fileupload/css/style.css" rel="stylesheet" type="text/css" />

<script src="scripts/charts/justgage/raphael-2.1.4.min.js" type="text/javascript"></script>
<script src="scripts/charts/justgage/justgage.js" type="text/javascript"></script>

   
   <g:compress>
		<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/jquery.jOrgChart.css" />
		<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/prettify.css" />
		<script src="<%= request.getContextPath()%>/scripts/organisational/prettify.js" type="text/javascript"></script>
		<script src="<%= request.getContextPath()%>/scripts/organisational/jquery.jOrgChart.js" type="text/javascript"></script>
	</g:compress>

<%
   	UtilityFunctions uF = new UtilityFunctions();
   	String strUserType = (String) session.getAttribute(IConstants.BASEUSERTYPE);
   	String strEmpID = (String) request.getAttribute("EMPID");
   	String strProID = (String) request.getParameter("PROFILEID");
   	String strSessionEmpID = (String) session.getAttribute("EMPID");

   	List<List<String>> alSkills = (List<List<String>>) request.getAttribute("alSkills");
   	List<List<String>> alEducation = (List<List<String>>) request.getAttribute("alEducation");

   	AngularMeter semiWorkedAbsent = (AngularMeter) request.getAttribute("KPI");
   	String semiWorkedAbsent1URL = semiWorkedAbsent.makeSession(request, "chart3");
   	
   	AngularMeter proMoneyKPI = (AngularMeter) request.getAttribute("PRO_MONEY_KPI");
   	String proMoneyKPIURL = proMoneyKPI.makeSession(request, "chart4");
   	
   	AngularMeter taskMoneyKPI = (AngularMeter) request.getAttribute("TASK_MONEY_KPI");
   	String taskMoneyKPIURL = taskMoneyKPI.makeSession(request, "chart5");
   	
   	AngularMeter proTimeKPI = (AngularMeter) request.getAttribute("PRO_TIME_KPI");
   	String proTimeKPIURL = proTimeKPI.makeSession(request, "chart6");
   	
   	AngularMeter taskTimeKPI = (AngularMeter) request.getAttribute("TASK_TIME_KPI");
   	String taskTimeKPIURL = taskTimeKPI.makeSession(request, "chart7");

   	boolean isFilledStatus = uF.parseToBoolean((String) request.getAttribute("isFilledStatus"));
   	//boolean isOfficialFilledStatus = uF.parseToBoolean((String) request.getAttribute("isOfficialFilledStatus"));
   	String AGGREGATE_SCORE = (String) request.getAttribute("AGGREGATE_SCORE");

   	if (strEmpID != null) {
   		strProID = strEmpID;
   	} else if (strProID != null) {
   	} else if (strSessionEmpID != null) {
   		strProID = strSessionEmpID;
   	}

   	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
   	if (hmEmpProfile == null) {
   		hmEmpProfile = new HashMap<String, String>();
   	}
   	
   	String strTitle = "";
   	if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
   			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) {
   		strTitle = hmEmpProfile.get("NAME") + "'s Profile";
   	} else {
   		strTitle = "My Profile";
   	}

   	String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
   	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
   	String isDeviceIntegration = (String) request.getAttribute("IS_DEVICE_INTEGRATION");
   	
   	Map<String, String> hmEmpSkillRate = (Map<String, String>) request.getAttribute("hmEmpSkillRate");
   	if (hmEmpSkillRate == null)  hmEmpSkillRate = new HashMap<String, String>();
   	Map<String, String> hmInfoDisplay = (Map<String, String>) request.getAttribute("hmInfoDisplay");
   	if (hmInfoDisplay == null)  hmInfoDisplay = new HashMap<String, String>(); 
   	
   	boolean isProjectOwner = (Boolean) request.getAttribute("isProjectOwner");
   	
   %>

	<style>
	.textblue {
		 color: #006699;
		}
		
	.alignRight {
		text-align: right;
	}	
	</style>


	<script type="text/javascript">
			
	jQuery(document).ready(function() {
        $("#org").jOrgChart({
			chartElement : '#chart',
			dragAndDrop  : false
		});
	});
	
		
		
    $(document).ready(function() {
		  jQuery(".content1").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".heading_dash").click(function()
		  {
			jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("filter_close");
		  });
		  
		  /* $(function() { */
				//$('#default').raty();
				 <%
				 //System.out.println("alSkills ===>> " + alSkills);
				if (alSkills != null && alSkills.size() != 0) {
					double dblOverall = 0.0d;
					int nOverall = 0;
					for (int i = 0; i < alSkills.size(); i++) {
						List<String> alInner = alSkills.get(i);
						nOverall++;
	          			dblOverall += uF.parseToDouble(alInner.get(2));		
				%>
						$('#star<%=i%>').raty({
							  readOnly: true,
							  start: <%=uF.parseToDouble(alInner.get(2))/2 %>,
							  half: true
							});
						<%}
					double dblOverallRating = 0.0d;
					if(dblOverall > 0.0d){
						dblOverallRating = (dblOverall/nOverall) / 2;
					}
						%>
					$('#skillPrimaryOverall').raty({
						  readOnly: true,
						  start:    <%=dblOverallRating%>,
						  half: true
						});
				<%}%>
				
				
			/* }); */
    });
    
    function showEditPhoto(){
    	document.getElementById("uploadPhotoDiv").style.display= "block";
    }
    function hideEditPhoto(){
    	document.getElementById("uploadPhotoDiv").style.display= "none";    	
    }

</script>

<ul id="org" style="display:none">
	<%=request.getAttribute("sbPosition")%>
</ul>


<%--  <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>


        <!-- Main content -->
		<section class="content">

			<div class="row">
				<div class="col-md-4">
					<!-- Profile Image -->
					<div class="box box-primary">
						<div class="box-body box-profile">
							<%if(docRetriveLocation==null) { %>
								<img class="lazy profile-user-img img-responsive img-circle" style="width: 100px; height: 100px;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>" />
							<%} else { %>
	                            <img class="lazy profile-user-img img-responsive img-circle" style="width: 100px; height: 100px;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strProID+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>" />
	                         <%} %>
	                         <div style="text-align:center; float:left; width:100%;"><a href="javascript:void(0)" onclick="showEditPhoto();">Edit Photo</a></div>
							<h5 class="profile-username text-center"><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%>]</h5>
							<p class="text-muted text-center" style="margin-bottom: 3px;">Working as <%=uF.showData((String) hmEmpProfile.get("EMPLOYEE_CONTRACTOR"), "-")%></p>
	                        <p class="text-muted text-center" style="margin-bottom: 3px;">Date of Joining: <%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></p>
	                        <p class="text-muted text-center" style="margin-bottom: 3px;">Reporting to: <%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></p>
	                        <p class="text-muted text-center">Profile: <%=uF.showData((String) hmEmpProfile.get("PROFILE"), "-")%></p>
	
							<ul class="list-group list-group-unbordered">
							<% double dblOverallRating = 0.0d;
							if(alSkills!=null && alSkills.size()!=0) { %>
								<li class="list-group-item">
		                        	<% 
		                        	int skillCnt=0;
		                        	for(int i=0; i<alSkills.size(); i++) { 
		                        		List<String> alInner = alSkills.get(i);
				              			double dblRating = 0.0d;
				              			if(uF.parseToDouble(alInner.get(2))>0) {
				              				dblRating = uF.parseToDouble(alInner.get(2))/2;
				              				skillCnt++;
				              			}
				              			dblOverallRating += dblRating;
		                        	%>
		                            	<strong><%=(i<alSkills.size()-1) ? ((List)alSkills.get(i)).get(1) + ", " : ((List)alSkills.get(i)).get(1)%></strong>
		                        	<% } 
		                        	if(dblOverallRating>0 && skillCnt>0) {
		                        		dblOverallRating = dblOverallRating/skillCnt;
		                        	}
		                        	%>
		                        </li>
		                        <li class="list-group-item"><div id="skillPrimary"></div></li>
	                        <% } %>    
                       
	                       <%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %> 
								<li class="list-group-item">Overall: <%=uF.formatIntoOneDecimalWithOutComma(dblOverallRating) %> <!-- <div id="skillPrimaryOverall"></div> --></li>
		         			<% } %>
		         			</ul>
		         			<div id="uploadPhotoDiv" style="float:left; padding:5px; width:98%; display:none;">
			                	<form id="UploadForm" action="PeopleProfile.action" method="post" enctype="multipart/form-data">
									<input type="file" name="empImage" id="empImage" size="20"> 
									<input type="hidden" name="empId" id="empId" value="<%=strProID%>"/>
									<input type="submit" class="btn btn-success" value="Upload" name="submit" style="margin-top: 7px; padding: 3px 7px;">
									<input type="button" class="btn btn-danger" onclick="hideEditPhoto();" value="Cancel" name="cancel" style="padding: 3px 7px; margin-top: 7px;">
									<div id="progressbox" style="width: 100%; margin-top: 9px;">
										<div id="progressbar" style="height: 14px;"></div>
										<div id="percent" style="top: -2px;">0%</div>
									</div>
									<br/>
									<div id="message"></div>
								</form>
			                </div>
						</div><!-- /.box-body -->
					</div><!-- /.box -->
				
				
					<div class="box box-default">
						<div class="box-header with-border">
							<h3 class="box-title">Skills</h3>
							<div class="box-tools pull-right">
								<a href="AddPeople.action?operation=U&mode=profile&empId=<%=strProID%>&step=1" class="fa fa-edit" title="Update Skill">&nbsp;</a>
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
							<% if (alSkills != null && alSkills.size() != 0) { %>
								<table class="table table-hover">
				               		<tr>
				                        <th style="text-align: center;">Skill</th>
				                        <th>Rating</th>
				                        <%if(uF.parseToBoolean(hmInfoDisplay.get("IS_RATE"))) { %>
					                        <th style="text-align: center;">Hourly</th>
					                        <th style="text-align: center;">Daily</th>
					                        <th style="text-align: center;">Monthly</th>
					                    <% } %>
				                    </tr> 	
						              <%
					              		for (int i = 0; i < alSkills.size(); i++) {
					              			List<String> alInner = alSkills.get(i);
					              			double dblRating = 0.0d;
					              			if(uF.parseToDouble(alInner.get(2))>0) {
					              				dblRating = uF.parseToDouble(alInner.get(2))/2;
					              			}
						              %>
				                    <tr>
				                        <td><strong><%=alInner.get(1)%></strong></td>
				                        <td><div id="star<%=i%>"><%=uF.formatIntoOneDecimalWithOutComma(dblRating) %> </div></td>
				                         <%if(uF.parseToBoolean(hmInfoDisplay.get("IS_RATE"))) { %>
					                        <td style="text-align: right;"><%=uF.showData(hmEmpSkillRate.get("HOURLY_"+alInner.get(0)),"-") %></td>
					                        <td style="text-align: right;"><%=uF.showData(hmEmpSkillRate.get("DAILY_"+alInner.get(0)),"-") %></td>
					                        <td style="text-align: right;"><%=uF.showData(hmEmpSkillRate.get("MONTHLY_"+alInner.get(0)),"-") %></td>
				                        <% } %>
				                    </tr>
				          	      <% } %>
				               </table>
			               <% } else { %>
								<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No skill sets added.</div>
			               <% }  %>
						</div><!-- /.box-body -->
					</div><!-- /.box -->


					<div class="box box-default">
						<div class="box-header with-border">
							<h3 class="box-title">Current Job</h3>
							<div class="box-tools pull-right">
							<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
  					  			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
							%>
								<a href="AddPeople.action?operation=U&mode=profile&empId=<%=strProID%>&step=2" class="fa fa-edit" title="Update Current Job">&nbsp;</a>
			        		<% } %>
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
						    <table class="table table-hover">
							    <tr><td class="alignRight">Employee Type:</td> <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_TYPE"), "-")%></td></tr>
							    <tr><td class="alignRight">Organization: </td> <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ORG_NAME"), "-")%></td></tr>
							    <tr><td class="alignRight">Location: </td> <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("WLOCATION_NAME"), "-")%></td></tr>
							    <tr><td class="alignRight">Department: </td> <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("DEPARTMENT_NAME"), "-")%></td></tr>
							    <tr><td class="alignRight">SBU:</td> <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("SBU_NAME"), "-")%></td></tr>
							    <tr><td class="alignRight">Designation:</td> <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-")%></td></tr>
							    <tr><td class="alignRight">Joining Date:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "-")%></td></tr>
							</table>
							<%if(!uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS"))) {%>
								<div style="margin-top:60px; margin-left:60px;"><img src="images1/warning.png" /> </div>
							<% } %>
						</div><!-- /.box-body -->
					</div><!-- /.box -->
					

					<div class="box box-default">
						<div class="box-header with-border">
							<h3 class="box-title">Employee History</h3>
							<div class="box-tools pull-right">
							<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
  					  			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
							%>
								<a href="AddPeople.action?operation=U&mode=profile&empId=<%=strProID%>&step=1" class="fa fa-edit" title="Update Employee History">&nbsp;</a>
			        		<% } %>
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
						    <table class="table table-hover">
								<tr><td class="alignRight">Relevant Years of Experience:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("RELEVANT_EXPERIENCE"), "-")%></td></tr>
								<tr><td class="alignRight">Total Experience:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("TOTAL_EXPERIENCE"), "-")%></td></tr>
							</table>
						</div><!-- /.box-body -->
					</div><!-- /.box -->
					
					
					<div class="box box-default">
						<div class="box-header with-border">
							<h3 class="box-title">Cost To Company</h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
						<%
							double netHourly = uF.parseToDouble(hmEmpProfile.get("HOURLY_CTC"));
							double netDaily = uF.parseToDouble(hmEmpProfile.get("DAILY_CTC"));
							double netAmount = uF.parseToDouble(hmEmpProfile.get("MONTH_CTC"));
							double netYearAmount = uF.parseToDouble(hmEmpProfile.get("ANNUAL_CTC"));
		            	%>
						    <table class="table table-hover">
							    <tr><td class="alignRight">Hourly:</td><td class="textblue" valign="bottom"> <%=uF.formatIntoTwoDecimal(netHourly)%></td></tr>
								<tr><td class="alignRight">Daily:</td><td class="textblue" valign="bottom"> <%=uF.formatIntoTwoDecimal(netDaily)%></td></tr>
								<tr><td class="alignRight">Monthly:</td><td class="textblue" valign="bottom"> <%=uF.formatIntoTwoDecimal(netAmount)%></td></tr>
								<tr><td class="alignRight">Annually:</td><td class="textblue" valign="bottom"> <%=uF.formatIntoTwoDecimal(netYearAmount)%></td></tr>
							</table>
						</div><!-- /.box-body -->
					</div><!-- /.box -->
					
          
					<div class="box box-default">
						<div class="box-header with-border">
							<h3 class="box-title">Corporate Information</h3>
							<div class="box-tools pull-right">
								<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
	  					  			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
								%>
									<a href="AddPeople.action?operation=U&mode=profile&empId=<%=strProID%>&step=2" class="fa fa-edit" title="Update Corporate Information">&nbsp;</a>
				        		<% } %>
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
						    <table class="table table-hover">
							    <tr><td class="alignRight">Corporate Mobile:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("CORPORATE_MOBILE"), "-")%></td></tr>
								<tr><td class="alignRight">Corporate Desk:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("CORPORATE_DESK"), "-")%></td></tr>
								<tr><td class="alignRight">Corporate id:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMAIL_SEC"), "-")%></td></tr>
								<tr><td class="alignRight">Skype id:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("SKYPE_ID"), "-")%></td></tr>
							</table>
						</div><!-- /.box-body -->
					</div><!-- /.box -->
					
					
					<div class="box box-default">
						<div class="box-header with-border">
							<h3 class="box-title">Personal Information</h3>
							<div class="box-tools pull-right">
								<a href="AddPeople.action?operation=U&mode=profile&empId=<%=strProID%>&step=1" class="fa fa-edit" title="Update Personal Information">&nbsp;</a>
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
						    <table class="table table-hover">
								<tr><td class="alignRight">Current Address:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("CURRENT_ADDRESS"), "") + ", " + uF.showData(hmEmpProfile.get("CURRENT_CITY"), "") + ", " + uF.showData(hmEmpProfile.get("CURRENT_STATE"), "") + ", "
								+ uF.showData(hmEmpProfile.get("CURRENT_COUNTRY"), "")%></td></tr>
								<tr><td class="alignRight">Permanent Address:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("ADDRESS"), "") + ", " + uF.showData(hmEmpProfile.get("CITY"), "") + ", " + uF.showData(hmEmpProfile.get("STATE"), "") + ", "
								+ uF.showData(hmEmpProfile.get("COUNTRY"), "")%></td></tr>
								<tr><td class="alignRight">Suburb: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("SUBURB"), "-")%></td></tr>
								<tr><td class="alignRight">Landline: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CONTACT"), "-")%></td></tr>
								<tr><td class="alignRight">Mobile: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CONTACT_MOB"), "-")%></td></tr>
								<tr><td class="alignRight">Email id:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMAIL"), "-")%></td></tr>
								<tr><td class="alignRight">Date of Birth:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DOB"), "-")%></td></tr>
							</table>
						</div><!-- /.box-body -->
					</div><!-- /.box -->
					
					
					<div class="box box-default">
						<div class="box-header with-border">
							<h3 class="box-title">Education</h3>
							<div class="box-tools pull-right">
								<a href="AddPeople.action?operation=U&mode=profile&empId=<%=strProID%>&step=1" class="fa fa-edit" title="Update Education">&nbsp;</a>
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
							<% if (alEducation != null && alEducation.size() != 0) { %>
								<table class="table table-hover">
				               		<tr>
				                        <th>Degree</th>
				                        <th>Duration</th>
				                        <th>Completion Year</th>
				                        <th>Grade</th>
				                    </tr> 	
						              <%
						              	for (int i = 0; i < alEducation.size(); i++) {
				  				   			List<String> innerList = alEducation.get(i);
						              %>
				                    <tr>
				                        <td><strong><%=innerList.get(1)%></strong></td>
				                        <td><%=innerList.get(2) + " Years"%></td>
				                        <td><%=innerList.get(3)%></td>
				                        <td><%=innerList.get(4)%></td>
				                    </tr>
				          	      <% } %>
				               </table>
			               <% } else { %>
								<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No Education information added.</div>
			               <% }  %>
						</div><!-- /.box-body -->
					</div><!-- /.box -->
					

				</div>
				
				
				<div class="col-md-8">
				
					<div class="box box-primary">
						<div class="box-header with-border">
							<h3 class="box-title">My Position</h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
							<div class="table-responsive" id="chart" class="orgChart"> </div>
						</div><!-- /.box-body -->
					</div><!-- /.box -->
					
					
					<div class="box box-default">
						<div class="box-header with-border">
							<h3 class="box-title">My Work KPI</h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
							<div style="float: left; width: 100%; text-align: right;">
			                	<s:form theme="simple" name="frmWorkKpi" action="PeopleProfile" id="frmWorkKpi" method="POST" cssClass="formcss">
			                 		<s:hidden name="empId"></s:hidden>
				                 	<s:select theme="simple" name="taskWorking" id="taskWorking" headerKey="1" headerValue="Since last 1 Year"
			                            list="#{'2':'Since last 6 months','3':'Since last 3 months','4':'Since last 1 month'}"
				                        onchange="document.frmWorkKpi.submit();"/>
		                        </s:form>
			                 </div>
			                 <div style="float:left; width:100%;">
				             	 <div style="float:left; width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
			                       <div id="guageTaskTimeKpi" class="gauge"></div>
			                     </div>
			                     <div style="float:left; width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
			                       <div id="guageTaskMoneyKpi" class="gauge"></div>
			                     </div>
			                     <script >
								    document.addEventListener("DOMContentLoaded", function(event) {
								        var g1 = new JustGage({
								            id: "guageTaskTimeKpi",
								            title: "",
								            label: "Time",
								            value: <%=uF.parseToDouble((String)request.getAttribute("TASK_ACTUAL_TIME_KPI"))%>,
								            min: 0,
								            max: <%=uF.parseToDouble((String)request.getAttribute("TASK_BUDGET_TIME_KPI"))%>,
								            decimals: 0,
								            gaugeWidthScale: 0.6
								        });
								        
								        var g2 = new JustGage({
								            id: "guageTaskMoneyKpi",
								            title: "",
								            label: "Money",
								            value: <%=uF.parseToDouble((String)request.getAttribute("TASK_ACTUAL_MONEY_KPI"))%>,
								            min: 0,
								            max: <%=uF.parseToDouble((String)request.getAttribute("TASK_BUDGET_MONEY_KPI"))%>,
								            decimals: 0,
								            gaugeWidthScale: 0.6
								        });
								        
								    });
							    </script>
			                     
			                     <div style="width:100%; font-size: 14px; text-align: center; margin-bottom: 15px;">
									<span style="font-weight: bolder; font-family: digital; color: green; font-size: 26px;">
									<%=uF.showData((String) request.getAttribute("completedTasks"),"0") %></span>&nbsp;Tasks delivered during the period 
								 </div>
				                  <div style="width:95%; height: 250px; float:left; text-align: center;">
									<%-- <jsp:include page="/jsp/chart/WorkingKPIDonutChart.jsp" /> --%>
				                </div>
				             </div>
						</div><!-- /.box-body -->
					</div><!-- /.box -->
					
					
					<%if(isProjectOwner){ %>
						<div class="box box-default">
							<div class="box-header with-border">
								<h3 class="box-title">Project KPI</h3>
								<div class="box-tools pull-right">
									<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
									<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
								</div>
							</div><!-- /.box-header -->
							<div class="box-body">
								<div style="float: left; width: 100%; text-align: right;">
				                	<s:form theme="simple" name="frmProKpi" action="PeopleProfile" id="frmProKpi" method="POST" cssClass="formcss">
				                 		<s:hidden name="empId"></s:hidden>
					                 	<s:select theme="simple" name="proWorking" id="proWorking" headerKey="1" headerValue="Since last 1 Year"
				                            list="#{'2':'Since last 6 months','3':'Since last 3 months','4':'Since last 1 month'}" 
				                            onchange="document.frmProKpi.submit();"/>
		                        	</s:form>
				                 </div>
				                 <div style="float:left; width:100%;">
					             	<div style="float:left;width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
				                      <div id="guageProTimeKpi" class="gauge"></div>
				                    </div>
				                    <div style="float:left;width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
				                      <div id="guageProMoneyKpi" class="gauge"></div>
				                    </div>
				                    <script>
								    document.addEventListener("DOMContentLoaded", function(event) {
								        var g1 = new JustGage({
								            id: "guageProTimeKpi",
								            title: "",
								            label: "Time",
								            value: <%=uF.parseToDouble((String)request.getAttribute("PRO_ACTUAL_TIME_KPI"))%>,
								            min: 0,
								            max: <%=uF.parseToDouble((String)request.getAttribute("PRO_BUDGET_TIME_KPI"))%>,
								            decimals: 0,
								            gaugeWidthScale: 0.6
								        });
								        
								        var g2 = new JustGage({
								            id: "guageProMoneyKpi",
								            title: "",
								            label: "Money",
								            value: <%=uF.parseToDouble((String)request.getAttribute("PRO_ACTUAL_MONEY_KPI"))%>,
								            min: 0,
								            max: <%=uF.parseToDouble((String)request.getAttribute("PRO_BUDGET_MONEY_KPI"))%>,
								            decimals: 0,
								            gaugeWidthScale: 0.6
								        });
								        
								    });
							    </script>
				                    
				                    <div style="width:100%; font-size: 14px; text-align: center; margin-bottom: 15px;">
										<span style="font-weight: bolder; font-family: digital; color: green; font-size: 26px;">
										<%=uF.showData((String) request.getAttribute("completedProjects"),"0") %></span>&nbsp;Projects delivered during the period 
									</div>  
					                <div style="width:95%; float:left; text-align: center;">
					                	<div class="box-body chart-responsive">
						                  <div class="chart" id="sales-chart" style="height: 300px; position: relative;"></div>
						                </div>
										<%-- <jsp:include page="/jsp/chart/ProjectKPIDonutChart.jsp" /> --%>
									</div>
								</div>
							</div><!-- /.box-body -->
						</div><!-- /.box -->
					<% } %>
					
					
					<div class="box box-default">
							<div class="box-header with-border">
								<h3 class="box-title">Timeline</h3>
								<div class="box-tools pull-right">
									<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
									<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
								</div>
							</div><!-- /.box-header -->
							<div class="box-body">
								<%
			             		List<Map<String, String>> alResourceTimeLine = (List<Map<String, String>>) request.getAttribute("alResourceTimeLine");
			              		if (alResourceTimeLine !=null && alResourceTimeLine.size()>0) {
			              		%>
			              			<section id="cd-timeline" class="cd-container">
			              				<%
				             				for (int i = 0; i < alResourceTimeLine.size(); i++) {
			           							Map<String, String> hmInner = alResourceTimeLine.get(i);
			           							String strColor = "#00FF00";
			           							String strStatus = "";
			           							String strMsg = "";
			           							String strDate = "";
			           							if(uF.parseToInt(hmInner.get("PROJECT_TASK")) == 2){
			           								strColor = "#FFA500";
			           								if(hmInner.get("APPROVE_STATUS").trim().equalsIgnoreCase("approved")){
			           									strStatus = "Task Completed";
			           									strMsg = hmInner.get("ACTIVITY_NAME")+" (<font style=\"font-style: italic;\">"+hmInner.get("PRO_NAME")+"</font>)"
			           										+" was completed on "+hmInner.get("COMPLETED_DATE");
			           									strDate = hmInner.get("COMPLETED_DATE");
			           								} else {
			           									strStatus = "Task Started";
			           									strMsg = hmInner.get("ACTIVITY_NAME")+" (<font style=\"font-style: italic;\">"+hmInner.get("PRO_NAME")+"</font>)"
			           										+" is started on "+hmInner.get("START_DATE");
			           									strDate = hmInner.get("START_DATE");
			           								}
			           							} else {
			           								if(hmInner.get("APPROVE_STATUS").trim().equalsIgnoreCase("approved")){
			           									strStatus = "Project Completed";
			           									strMsg = hmInner.get("PRO_NAME")+" was completed on "+hmInner.get("COMPLETED_DATE");
			           									strDate = hmInner.get("COMPLETED_DATE");
			           								} else {
			           									strStatus = "Project Started";
			           									strMsg = hmInner.get("PRO_NAME")+" is started on "+hmInner.get("START_DATE");
			           									strDate = hmInner.get("START_DATE");
			           								}
			           							}
				             			%>
				             				<div class="cd-timeline-block">
												<div class="cd-timeline-img cd-picture">
													&nbsp;
												</div>
									
												<div class="cd-timeline-content" style="border: 1px solid #D1D1D1;">
													<span class="act_title" style="float:left; width:100%; background:<%=strColor %>;font-size: 12px; padding-left: 10px;"><strong><%=strStatus%></strong></span>
													<span style="float:left; width:100%; font-size: 12px;"><%=strMsg %></span> 
													<span class="cd-date" style="font-size: 12px;"><%=strDate %></span>
												</div>
											</div>
				             			<%} %>
			              			</section>
			              		<%} %>
				                 
							</div><!-- /.box-body -->
						</div><!-- /.box -->
						
				</div>
				
			</div> <!-- /.row -->
		</section> <!-- /.section -->


 <!-- Morris.js charts -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/raphael/2.1.0/raphael-min.js"></script>
    <script src="../../plugins/morris/morris.min.js"></script>
    
    	
<script type="text/javascript">
	jQuery(document).ready(function() {
		$(function () {
			//DONUT CHART
		    var donut = new Morris.Donut({
		      element: 'sales-chart',
		      resize: true,
		      colors: ["#3c8dbc", "#f56954", "#00a65a"],
		      data: [
		        {label: "Download Sales", value: 12},
		        {label: "In-Store Sales", value: 30},
		        {label: "Mail-Order Sales", value: 20}
		      ],
		      hideHover: 'auto'
		    });
				
			});
	});
</script>


<script>
	jQuery(document).ready(function() {
		$("img.lazy").lazyload({ threshold : 200, effect : "fadeIn", failure_limit : 10});
	});
</script>

