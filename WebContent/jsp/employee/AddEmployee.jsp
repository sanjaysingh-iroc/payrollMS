<%@page import="com.konnect.jpms.recruitment.FillEducational"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@page import="com.konnect.jpms.select.FillMaritalStatus"%>
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.select.FillYears"%> 
<%@page import="com.konnect.jpms.select.FillDegreeDuration"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%> 
<%@page import="com.konnect.jpms.util.*"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/customAjax.js" ></script>
<jsp:include page="../employee/CustomAjaxForAddEmployee.jsp"></jsp:include>
<jsp:include page="../employee/CssForAddEmployee.jsp"></jsp:include>
<script src="scripts/ckeditor_cust/ckeditor.js"></script> 

<%  String struserType = (String)session.getAttribute(IConstants.USERTYPE);
	String sessionUserId = (String)session.getAttribute(IConstants.EMPID);
	ArrayList educationalList = (ArrayList) request.getAttribute("educationalList");
	List<List<String>> alSkills = (List<List<String>>) request.getAttribute("alSkills"); 
	List<List<String>> alHobbies = (List<List<String>>) request.getAttribute("alHobbies");
	List<List<String>> alLanguages = (List<List<String>>) request.getAttribute("alLanguages");
	List<List<String>> alEducation = (List<List<String>>) request.getAttribute("alEducation");
	if (alEducation == null)alEducation = new ArrayList<List<String>>();
	ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
	List<List<String>> alSiblings = (List<List<String>>) request.getAttribute("alSiblings");
	List<List<String>> alchilds = (List<List<String>>) request.getAttribute("alchilds");
	List<List<String>> alPrevEmployment = (List<List<String>>) request.getAttribute("alPrevEmployment");
	List<List<String>> alEmpReferences = (List<List<String>>) request.getAttribute("alEmpReferences");
	List<FillDegreeDuration> degreeDurationList = (List<FillDegreeDuration>) request.getAttribute("degreeDurationList");
	if (degreeDurationList == null)	degreeDurationList = new ArrayList<FillDegreeDuration>();
	List<FillYears> yearsList = (List<FillYears>) request.getAttribute("yearsList");
	if (yearsList == null) yearsList= new ArrayList<FillYears>();
	List<FillGender> empGenderList = (List<FillGender>) request.getAttribute("empGenderList");
	List<FillMaritalStatus> maritalStatusList = (List<FillMaritalStatus>) request.getAttribute("maritalStatusList");
	String countryOptions = (String) request.getAttribute("countryOptions");
	String statesOptions = (String) request.getAttribute("statesOptions");
	List<FillSkills> skillsList = (List<FillSkills>) request.getAttribute("skillsList");
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	String strImage = (String) request.getAttribute("strImage");
	String strCoverImage = (String) request.getAttribute("strCoverImage");
	UtilityFunctions uF = new UtilityFunctions();
	String currentYear = (String) request.getAttribute("currentYear");
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	int nEmpAlphaCodeLength = 2;
	if (CF != null && CF.getStrOEmpCodeAlpha() != null) {nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();	}
	Map<String,String> hmPrevEmpTds=(Map<String,String>)request.getAttribute("hmPrevEmpTds");
	if(hmPrevEmpTds==null)hmPrevEmpTds=new HashMap<String, String>();
	
	String strEmImgPath = (String) request.getAttribute("strEmImgPath");
	String strEmpImgCoverPath = (String) request.getAttribute("strEmpImgCoverPath"); 
	String empBankName=(String)request.getAttribute("empBankName");
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus==null) hmFeatureStatus = new HashMap<String, String>();
%>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
        		<div class="box-header with-border">
                    <h4 class="box-title">
						<%=(request.getParameter("operation")!=null ||(request.getParameter("mode")!=null && request.getParameter("mode").equals("onboard"))) ? "Edit" : "Enter" %> <%=session.getAttribute(IConstants.USERID)!=null ? "Employee" : "your" %> Detail
                    </h4>
                </div>
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                 	<div class="leftbox reportWidth" >
                    <div class="wizard">
                    	<%  String strEmpType = (String) session.getAttribute("USERTYPE");
							String strMessage = (String) request.getAttribute("MESSAGE");
							if (strMessage == null) {strMessage = "";}
							Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
							String validReqOpt = "";
							String validAsterix = "";
							
							String step = (String) request.getAttribute("step");
							String mode = (String) request.getAttribute("mode");
							String classStep1= "";
							String classStep2= "";
							String classStep3= "";
							String classStep4= "";
							String classStep5= "";
							String classStep6= "";
							String classStep7= "";
							String classStep8= "";
							String classStep9= "";
							
							if(mode !=null && mode.equals("report")) {
								classStep2= "disabled";
								classStep3= "disabled";
								classStep4= "disabled";
								classStep5= "disabled";
								classStep6= "disabled";
								classStep7= "disabled";
								classStep8= "disabled";
								classStep9= "disabled";
							}
							if(uF.parseToInt(step)==1) {
								classStep1= "active";
							} else if(uF.parseToInt(step)==2) {
								if(mode !=null && mode.equals("report")) {
									classStep1= "";
								}
								classStep2= "active";
							} else if(uF.parseToInt(step)==3) {
								if(mode !=null && mode.equals("report")) {
									classStep1= "";
									classStep2= "";
								}
								classStep3= "active";
							} else if(uF.parseToInt(step)==4) {
								if(mode !=null && mode.equals("report")) {
									classStep1= "";
									classStep2= "";
									classStep3= "";
								}
								classStep4= "active";
							} else if(uF.parseToInt(step)==5) {
								if(mode !=null && mode.equals("report")) {
									classStep1= "";
									classStep2= "";
									classStep3= "";
									classStep4= "";
								}
								classStep5= "active";
							} else if(uF.parseToInt(step)==6) {
								if(mode !=null && mode.equals("report")) {
									classStep1= "";
									classStep2= "";
									classStep3= "";
									classStep4= "";
									classStep5= "";
								}
								classStep6= "active";
							} else if(uF.parseToInt(step)==7) {
								if(mode !=null && mode.equals("report")) {
									classStep1= "";
									classStep2= "";
									classStep3= "";
									classStep4= "";
									classStep5= "";
									classStep6= "";
								}
								classStep7= "active";
							} else if(uF.parseToInt(step)==8) {
								if(mode !=null && mode.equals("report")) {
									classStep1= "";
									classStep2= "";
									classStep3= "";
									classStep4= "";
									classStep5= "";
									classStep6= "";
									classStep7= "";
								}
								classStep8= "active";
							} else if(uF.parseToInt(step)==9) {
								if(mode !=null && mode.equals("report")) {
									classStep1= "";
									classStep2= "";
									classStep3= "";
									classStep4= "";
									classStep5= "";
									classStep6= "";
									classStep7= "";
									classStep8= "";
								}
								classStep9= "active";
							}
						%>
						<%if(!"U".equalsIgnoreCase(request.getParameter("operation")) && request.getParameter("mode")!=null && request.getParameter("mode").equals("onboard")) { %>
			            <div class="wizard-inner">
			            	<%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) { %>
			                <div class="connecting-line"></div>
			                <% }else{ %>
			                <div class="connecting-line" style="width: 63%;"></div>
			                <% } %>
			                <ul class="nav nav-tabs" role="tablist">
			                    <s:if test="mode=='report'">
				                    <li role="presentation" class="<%=classStep1 %>"><a href="AddEmployee.action?mode=onboard&step=0&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" aria-controls="step1" role="tab" title="Personal Information"><span class="round-tab"><i class="fa fa-user"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep2 %>"><a href="AddEmployee.action?mode=onboard&step=1&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" aria-controls="step2" role="tab" title="Background Information"><span class="round-tab"><i class="fa fa-info" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep3 %>"><a href="AddEmployee.action?mode=onboard&step=2&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" aria-controls="step3" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep4 %>"><a href="AddEmployee.action?mode=onboard&step=3&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" aria-controls="step4" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep5 %>"><a href="AddEmployee.action?mode=onboard&step=4&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" aria-controls="step5" role="tab" title="Family Information"><span class="round-tab"><i class="fa fa-users" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep6 %>"><a href="AddEmployee.action?mode=onboard&step=5&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep7 %>"><a href="AddEmployee.action?mode=onboard&step=6&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text" aria-hidden="true"></i></span></a></li>
				                    <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) { %>
				                    <li role="presentation" class="<%=classStep8 %>"><a href="AddEmployee.action?mode=onboard&step=7&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" aria-controls="step8" role="tab" title="Official Information"><span class="round-tab"><i class="fa fa-building-o" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep9 %>"><a href="AddEmployee.action?mode=onboard&step=8&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" aria-controls="complete" role="tab" title="Salary Information "><span class="round-tab"><i class="fa fa-money" aria-hidden="true"></i></span></a></li>
				                    <% } %>
			                    </s:if>
			                </ul>
			            </div>
						<% } else if(!"U".equalsIgnoreCase(request.getParameter("operation"))) { %> 
						<div class="wizard-inner">
			                <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
			                <div class="connecting-line"></div>
			                <% }else{ %>
			                <div class="connecting-line" style="width: 63%;"></div>
			                <% } %>
			                <ul class="nav nav-tabs" role="tablist">
								<s:if test="mode=='report'">
				                    <li role="presentation" class="<%=classStep1 %>"><a <% if(uF.parseToInt(step)>1) { %> href="AddEmployee.action?mode=onboard&step=0&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" <% } else { %> href="#pi" data-toggle="tab" <% } %> aria-controls="step1" role="tab" title="Personal Information"><span class="round-tab"><i class="fa fa-user"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep2 %>"><a <% if(uF.parseToInt(step)>2) { %> href="AddEmployee.action?mode=onboard&step=1&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" <% } else { %> href="#bi" data-toggle="tab" <% } %> aria-controls="step2" role="tab" title="Background Information"><span class="round-tab"><i class="fa fa-info" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep3 %>"><a <% if(uF.parseToInt(step)>3) { %> href="AddEmployee.action?mode=onboard&step=2&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" <% } else { %> href="#pe" data-toggle="tab" <% } %> aria-controls="step3" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep4 %>"><a <% if(uF.parseToInt(step)>4) { %> href="AddEmployee.action?mode=onboard&step=3&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" <% } else { %> href="#rf" data-toggle="tab" <% } %> aria-controls="step4" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep5 %>"><a <% if(uF.parseToInt(step)>5) { %> href="AddEmployee.action?mode=onboard&step=4&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" <% } else { %> href="#fi" data-toggle="tab" <% } %> aria-controls="step5" role="tab" title="Family Information"><span class="round-tab"><i class="fa fa-users" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep6 %>"><a <% if(uF.parseToInt(step)>6) { %> href="AddEmployee.action?mode=onboard&step=5&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" <% } else { %> href="#mi" data-toggle="tab" <% } %> aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep7 %>"><a <% if(uF.parseToInt(step)>7) { %> href="AddEmployee.action?mode=onboard&step=6&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" <% } else { %> href="#dc" data-toggle="tab" <% } %> aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text" aria-hidden="true"></i></span></a></li>
				                    <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) { %>
				                    <li role="presentation" class="<%=classStep8 %>"><a <% if(uF.parseToInt(step)>8) { %> href="AddEmployee.action?mode=onboard&step=7&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" <% } else { %> href="#oi" data-toggle="tab" <% } %> aria-controls="step8" role="tab" title="Official Information"><span class="round-tab"><i class="fa fa-building-o" aria-hidden="true"></i></span></a></li>
				                    <li role="presentation" class="<%=classStep9 %>"><a <% if(uF.parseToInt(step)>9) { %> href="AddEmployee.action?mode=onboard&step=8&operation=EO&empId=<%=request.getAttribute("empId")%>" data-toggle="" <% } else { %> href="#si" data-toggle="tab" <% } %> aria-controls="complete" role="tab" title="Salary Information"><span class="round-tab"><i class="fa fa-money" aria-hidden="true"></i></span></a></li>
				                    <% } %>
			                    </s:if>
			                </ul>
			            </div>	
						<% } %>
			                <div class="tab-content">
			                	<s:if test="step==1 || mode=='report'">
			                	<script type="text/javascript">
			                		$("#frmPersonalInfo").submit(function(e){
			                		   document.getElementById("btnSubmit").style.display = 'none';
			                		});
			                	
			                	</script>
			                	
			                    <div class="tab-pane active" role="tabpanel" id="pi"> 
										<div class="row row_without_margin">
											<div class="col-lg-12 col-md-12">
											<s:form theme="simple" action="AddEmployee" name="frmPersonalInfo" id="frmPersonalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkImageSize();">
											<div>
											<s:hidden name="empId" id="empId" />
											<s:hidden name="step" />
											<span style="color:#68AC3B; font-size:22px; padding:5px;">
								            Step 1: </span><span style="font-size: 18px;font-weight: 600;"> Enter Employee Personal Information</span>
								            <br/>Personal Information:<hr style="background-color:#346897; height:1px;">
											<table border="0" class="table form-table" style="margin-top: 10px;width: 82%;">
												<tr>
													<td class="txtlabel alignRight">Salutation:<%=(String)request.getAttribute("salutationValidAsterix") %></td>
													<td>
													<%if(session.getAttribute("isApproved")==null) { %>
													<%if(request.getAttribute("salutationValidReqOpt") != null && !request.getAttribute("salutationValidReqOpt").toString().equals("")) { %>
														<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
														listKey="salutationId" listValue="salutationName" cssClass="validateRequired " />
													<% } else { %>
														<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
														listKey="salutationId" listValue="salutationName" />
													<% } %>
													<% } else { %>
														<input type="text" name="salutation" id="salutation" class="<%=(String)request.getAttribute("salutationValidReqOpt") %> " disabled="disabled"/>
														<s:hidden name="salutation" />
													<% } %>
													</td>
												</tr>
												
												<tr>
												<td class="txtlabel alignRight">First Name:<%=(String)request.getAttribute("empFNameValidAsterix") %></td>
													<td>
													<% if(session.getAttribute("isApproved") == null) { %>
														<input type="text" name="empFname" id="empFname" class="<%=(String)request.getAttribute("empFNameValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empFname"), "") %>"/>
													<% } else { %>
														<input type="text" name="empFname" id="empFname" class="<%=(String)request.getAttribute("empFNameValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empFname"), "") %>" disabled="disabled"/>
														<s:hidden name="empFname" />
													<% } %>
													</td>
												</tr>
												
												<tr>
													<td class="txtlabel alignRight">Middle Name:<%=(String)request.getAttribute("empMNameValidAsterix") %></td>
													<td>
													<%if(session.getAttribute("isApproved")==null) {%>
														<input type="text" name="empMname" id="empMname" class="<%=(String)request.getAttribute("empMNameValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empMname"), "") %>"/>
													<%}else{%>
														<input type="text" name="empMname" id="empMname" class="<%=(String)request.getAttribute("empMNameValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empMname"), "") %>" disabled="disabled"/>
														<s:hidden name="empMname" />
													<%}%>
													</td>
												</tr>
												<tr>
													<% 	
													%>
													<td class="txtlabel alignRight">Last Name:<%=(String)request.getAttribute("empLNameValidAsterix") %></td>
													<td>
													<%if(session.getAttribute("isApproved")==null) {%>
														<input type="text" name="empLname" id="empLname" class="<%=(String)request.getAttribute("empLNameValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empLname"), "") %>"/>
													<%}else{%>
														<input type="text" name="empLname" id="empLname" class="<%=(String)request.getAttribute("empLNameValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empLname"), "") %>" disabled="disabled"/>
														<s:hidden name="empLname" />
													<%}%>
													</td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Personal Email Id:<%=(String)request.getAttribute("empPersonalEmailIdValidAsterix") %>
													</td>
													<td>
														<%if(session.getAttribute("isApproved") == null) {
														String email = (String)request.getAttribute("EMPLOYEE_EMAIL");
														if(email == null) {
															email = "";
														}
														%>
														<input type="text" name="empEmail" id="empEmail" class="<%=(String)request.getAttribute("empPersonalEmailIdValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empEmail"), "") %>" onchange="emailValidation('emailValidatorMessege','empEmail',this.value, 'EmailValidation.action?email='+this.value);"/>
														<div id="emailValidatorMessege"></div>
														<% } else { %>
														<input type="text" name="empEmail" id="empEmail" class="<%=(String)request.getAttribute("empPersonalEmailIdValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empEmail"), "") %>" disabled="disabled"/>
														<s:hidden name="empEmail" />
														<% } %>
														<div style="font-style: italic;">This email id is going to be validated. If found invalid, email id will be removed.</div>
													</td>
												</tr>
											</table>
											Current Address:<hr style="background-color:#346897; height:1px;">
											<table class="table form-table" style="margin-top: 10px;width:93%;">
												<tr>
													<td class="txtlabel alignRight" valign="top">Address:<%=(String)request.getAttribute("empTemporaryAddressValidAsterix") %></td>
													<td><textarea name="empAddress1Tmp" id="empAddress1Tmp" class="<%=(String)request.getAttribute("empTemporaryAddressValidReqOpt") %> " style="width: 208px; height: 108px;" ><%=uF.showData((String)request.getAttribute("empAddress1Tmp"), "") %></textarea></td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">City:<%=(String)request.getAttribute("empTemporaryCityValidAsterix") %></td>
													<td><input type="text" name="cityTmp" id="cityTmp" class="<%=(String)request.getAttribute("empTemporaryCityValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("cityTmp"), "") %>"/></td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Select Country:<%=(String)request.getAttribute("empTemporaryCountryValidAsterix") %></td>
													<td>
													<% if(request.getAttribute("empTemporaryCountryValidReqOpt") != null && !request.getAttribute("empTemporaryCountryValidReqOpt").toString().equals("")) { %>
														<% if(request.getAttribute("empTemporaryCountryValidReqOpt") != null && !request.getAttribute("empTemporaryCountryValidReqOpt").toString().equals("")) { %>
														<s:select id="countryTmp" cssClass="validateRequired " name="countryTmp" listKey="countryId" listValue="countryName" headerKey="" 
															headerValue="Select Country" onchange="getContentAcs('stateTD1','GetStates.action?country='+this.value+'&type=TADD&validReq=1');" list="countryList" />
														<% } else { %>
														<s:select id="countryTmp" name="countryTmp" listKey="countryId" listValue="countryName" headerKey="" 
															headerValue="Select Country" onchange="getContentAcs('stateTD1','GetStates.action?country='+this.value+'&type=TADD');" list="countryList" />	
														<% } %>
													<% } else { %>
														<% if(request.getAttribute("empTemporaryCountryValidReqOpt") != null && !request.getAttribute("empTemporaryCountryValidReqOpt").toString().equals("")) { %>
														<s:select id="countryTmp" name="countryTmp" listKey="countryId" listValue="countryName" headerKey="" headerValue="Select Country" 
															onchange="getContentAcs('stateTD1','GetStates.action?country='+this.value+'&type=TADD&validReq=1');" list="countryList" cssClass="validateRequired " />
														<% } else { %>
														<s:select id="countryTmp" name="countryTmp" listKey="countryId" listValue="countryName" headerKey="" headerValue="Select Country" 
															onchange="getContentAcs('stateTD1','GetStates.action?country='+this.value+'&type=TADD');" list="countryList"/>
														<% } %>	
													<% } %>	
													</td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Select State:<%=(String)request.getAttribute("empTemporaryStateValidAsterix") %></td>
													<td id="stateTD1">
													<% if(request.getAttribute("empTemporaryStateValidReqOpt") != null && !request.getAttribute("empTemporaryStateValidReqOpt").toString().equals("")) { %>
													<s:select theme="simple" cssClass="validateRequired " id="stateTmp" name="stateTmp" listKey="stateId" 
														listValue="stateName" headerKey="" headerValue="Select State" list="stateList" />
													<% } else { %>
													<s:select theme="simple" id="stateTmp" name="stateTmp" listKey="stateId" listValue="stateName" headerKey="" 
														headerValue="Select State" list="stateList"/>
													<% } %>	
													</td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Pincode:<%=(String)request.getAttribute("empTemporaryPostcodeValidAsterix") %></td>
													<td><input type="text" name="empPincodeTmp" id="empPincodeTmp" class="<%=(String)request.getAttribute("empTemporaryPostcodeValidReqOpt") %> " onkeypress="return isOnlyNumberKey(event)"  value="<%=uF.showData((String)request.getAttribute("empPincodeTmp"), "") %>"/></td>
												</tr>
											</table>
											
											Permanent Address:<hr style="background-color:#346897; height:1px;">
											
											<div style="text-align: center;"><input type="checkbox" onclick="copyAddress(this);" />Same as above</div>
											<table class="table form-table" style="margin-top: 10px;width:93%;">
												<tr>
													<td class="txtlabel alignRight" valign="top">Address:<%=(String)request.getAttribute("empPermanentAddressValidAsterix") %></td>
													<td><textarea name="empAddress1" id="empAddress1" class="<%=(String)request.getAttribute("empPermanentAddressValidReqOpt") %> " style="width: 208px; height: 108px;"><%=uF.showData((String)request.getAttribute("empAddress1"), "") %> </textarea></td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">City:<%=(String)request.getAttribute("empPermanentCityValidAsterix") %></td>
													<td><input type="text" name="city" id="city" class="<%=(String)request.getAttribute("empPermanentCityValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("city"), "") %>"/></td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Select Country:<%=(String)request.getAttribute("empPermanentCountryValidAsterix") %></td>
													<td>
													<% if(request.getAttribute("empPermanentCountryValidReqOpt") != null && !request.getAttribute("empPermanentCountryValidReqOpt").toString().equals("")) { %>
														<% if(request.getAttribute("empPermanentCountryValidReqOpt") != null && !request.getAttribute("empPermanentCountryValidReqOpt").toString().equals("")) { %>
														<s:select id="country" cssClass="validateRequired " name="country" listKey="countryId"	listValue="countryName" headerKey="" 
															headerValue="Select Country" onchange="getContentAcs('stateTD','GetStates.action?country='+this.value+'&type=PADD&validReq=1');" list="countryList" />
														<% } else { %>
														<s:select id="country" cssClass="validateRequired " name="country" listKey="countryId"	listValue="countryName" headerKey="" 
															headerValue="Select Country" onchange="getContentAcs('stateTD','GetStates.action?country='+this.value+'&type=PADD');" list="countryList" />
														<% } %>	
													<% } else { %>
														<% if(request.getAttribute("empPermanentCountryValidReqOpt") != null && !request.getAttribute("empPermanentCountryValidReqOpt").toString().equals("")) { %>
														<s:select id="country" name="country" cssClass="validateRequired " listKey="countryId"	listValue="countryName" headerKey="" headerValue="Select Country" 
															onchange="getContentAcs('stateTD','GetStates.action?country='+this.value+'&type=PADD&validReq=1');" list="countryList" />
														<% } else { %>
														<s:select id="country" name="country" listKey="countryId"	listValue="countryName" headerKey="" headerValue="Select Country" 
															onchange="getContentAcs('stateTD','GetStates.action?country='+this.value+'&type=PADD');" list="countryList" />
														<% } %>	
													<% } %>	
													</td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Select State:<%=(String)request.getAttribute("empPermanentStateValidAsterix") %></td>
													<td id="stateTD">
													<% if(request.getAttribute("empPermanentStateValidReqOpt") != null && !request.getAttribute("empPermanentStateValidReqOpt").toString().equals("")) { %>
													<s:select theme="simple" cssClass="validateRequired " id="state" name="state" listKey="stateId" listValue="stateName" 
														headerKey="" headerValue="Select State" list="stateList" />
													<% } else { %>
													<s:select theme="simple" id="state" name="state" listKey="stateId" listValue="stateName" headerKey="" 
														headerValue="Select State" list="stateList"/>
													<% } %>	
													</td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Pincode:<%=(String)request.getAttribute("empPermanentPostcodeValidAsterix") %></td>
													<td><input type="text" name="empPincode" onkeypress="return isOnlyNumberKey(event)"  id="empPincode" class="<%=(String)request.getAttribute("empPermanentPostcodeValidReqOpt") %> validateNumber " value="<%=uF.showData((String)request.getAttribute("empPincode"), "") %>"/></td>
												</tr>
											</table>
											
											Personal Contact Information:<hr style="background-color:#346897; height:1px;">
											<table class="table form-table" style="margin-top: 10px;width: 84%;">
												<tr>
													<td class="txtlabel alignRight">Landline Number:<%=(String)request.getAttribute("empLandlineNoValidAsterix") %></td>
													<td><input type="text" name="empContactno" onkeypress="return isOnlyNumberKey(event)"  class="<%=(String)request.getAttribute("empLandlineNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empContactno"), "") %>"/></td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Mobile Number:<%=(String)request.getAttribute("empMobileNoValidAsterix") %></td>
													<td><input type="text" name="empMobileNo" onkeypress="return isOnlyNumberKey(event)" id="empMobileNo" class="<%=(String)request.getAttribute("empMobileNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empMobileNo"), "") %>"/></td>
												</tr>
											</table>
											Emergency Contact Information:<hr style="background-color:#346897; height:1px;">
											<table class="table form-table" style="margin-top: 10px;width: 65%;">
												<tr>
													<td class="txtlabel alignRight">Emergency Contact Name:<%=(String)request.getAttribute("empEmergencyContactNameValidAsterix") %></td>
													<td><input type="text" class="<%=(String)request.getAttribute("empEmergencyContactNameValidReqOpt") %> " name="empEmergencyContactName" id="empEmergencyContactName" value="<%=uF.showData((String)request.getAttribute("empEmergencyContactName"), "") %>"/></td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Emergency Contact Number:<%=(String)request.getAttribute("empEmergencyContactNoValidAsterix") %></td>
													<td><input type="text" name="empEmergencyContactNo" onkeypress="return isOnlyNumberKey(event)" id="empEmergencyContactNo" class="<%=(String)request.getAttribute("empEmergencyContactNoValidReqOpt") %> validateNumber" value="<%=uF.showData((String)request.getAttribute("empEmergencyContactNo"), "") %>"/></td>
												</tr>
										<!-- ===start parvez date: 30-07-2022=== -->
												<tr>
													<td class="txtlabel alignRight">Relation:<%=(String)request.getAttribute("empEmergencyContactRelationValidAsterix") %></td>
													<td><input type="text" class="<%=(String)request.getAttribute("empEmergencyContactRelationValidReqOpt") %> " name="empEmergencyContactRelation" id="empEmergencyContactRelation" value="<%=uF.showData((String)request.getAttribute("empEmergencyContactRelation"), "") %>"/></td>
												</tr>
										<!-- ===end parvez date: 30-07-2022=== -->				
												<tr>
													<td class="txtlabel alignRight">Doctor's Name:<%=(String)request.getAttribute("empDoctorsNameValidAsterix") %></td>
													<td><input type="text" name="empDoctorName" id="empDoctorName" class="<%=(String)request.getAttribute("empDoctorsNameValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empDoctorName"), "") %>"/> </td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Doctor's Contact Number:<%=(String)request.getAttribute("empDoctorsContactNoValidAsterix") %></td>
													<td><input type="text" name="empDoctorNo" onkeypress="return isOnlyNumberKey(event)"  id="empDoctorNo" class="<%=(String)request.getAttribute("empDoctorsContactNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empDoctorNo"), "") %>"/> </td>
												</tr>
											</table>
											Personal Information (II):<hr style="background-color:#346897; height:1px;">
											<table class="table form-table" style="margin-top: 10px;width: 96%;">
												<tr>
												<td class="txtlabel alignRight">Blood Group:<%=(String)request.getAttribute("empBloodGroupValidAsterix") %></td>
												<td>
												<% if(request.getAttribute("empBloodGroupValidReqOpt") != null && !request.getAttribute("empBloodGroupValidReqOpt").toString().equals("")) { %>
													<s:select theme="simple" name="empBloodGroup" cssClass="validateRequired " listKey="bloodGroupId" listValue="bloodGroupName" 
														headerKey="0" headerValue="Select Blood Group" list="bloodGroupList" />
												<% } else { %>
													<s:select theme="simple" name="empBloodGroup" listKey="bloodGroupId" listValue="bloodGroupName" headerKey="0" 
														headerValue="Select Blood Group" list="bloodGroupList"/>
												<% } %>		
												</td></tr>
												<tr>
												<td class="txtlabel alignRight">Date of Birth:<%=(String)request.getAttribute("empDateOfBirthValidAsterix") %></td><td>
												<input type="text" name="empDateOfBirth" id="empDateOfBirth" class="<%=(String)request.getAttribute("empDateOfBirthValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empDateOfBirth"), "") %>" />
												</td></tr>
												<tr>
												<td class="txtlabel alignRight">Marital Status:<%=(String)request.getAttribute("empMaritalStatusValidAsterix") %></td>
												<td>
												<% if(request.getAttribute("empMaritalStatusValidReqOpt") != null && !request.getAttribute("empMaritalStatusValidReqOpt").toString().equals("")) { %>
												<s:select theme="simple" name="empMaritalStatus" id="empMaritalStatus" cssClass="validateRequired " listKey="maritalStatusId" listValue="maritalStatusName" 
													headerKey="0" headerValue="Select Marital Status" list="maritalStatusList" onchange="showMarriageDate();"/>
												<% } else { %>
												<s:select theme="simple" name="empMaritalStatus" id="empMaritalStatus" listKey="maritalStatusId" listValue="maritalStatusName" headerKey="0" 
													headerValue="Select Marital Status" list="maritalStatusList" onchange="showMarriageDate();" />
												<% } %>
												</td></tr>
												<tr id="trMarriageDate">
												<td class="txtlabel alignRight">Date of Marriage:<%=(String)request.getAttribute("empDateOfMarriageValidAsterix") %></td><td>
												<input type="text" name="empDateOfMarriage" id="empDateOfMarriage" class="<%=(String)request.getAttribute("empDateOfMarriageValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empDateOfMarriage"), "") %>"/>
												</td></tr>
												<tr>
												<% 	
												%>
												<td class="txtlabel alignRight">Gender:<%=(String)request.getAttribute("empGenderValidAsterix") %></td>
												<td>
												<% if(request.getAttribute("empGenderValidReqOpt") != null && !request.getAttribute("empGenderValidReqOpt").toString().equals("")) { %>
												<s:select theme="simple" cssClass="validateRequired " label="Select Gender" name="empGender" listKey="genderId"
													listValue="genderName" headerKey="" headerValue="Select Gender" list="empGenderList" />
												<% } else { %>
												<s:select theme="simple" label="Select Gender" name="empGender" listKey="genderId"
													listValue="genderName" headerKey="" headerValue="Select Gender" list="empGenderList" />
												<% } %>		
												</td></tr>
											</table>
											Other Personal Information:<hr style="background-color:#346897; height:1px;">
											<table class="table form-table" style="margin-top: 10px;width: 76%;	">
												<tr>
												<td class="txtlabel alignRight">Passport Number:<%=(String)request.getAttribute("empPassportNoValidAsterix") %></td>
												<td><input type="text" name="empPassportNo" id="empPassportNo" class="<%=(String)request.getAttribute("empPassportNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empPassportNo"), "") %>"/></td></tr>
												<tr><td class="txtlabel alignRight">Passport Expiry Date:<%=(String)request.getAttribute("empPassportExpiryDateValidAsterix") %></td>
												<td><input type="text" name="empPassportExpiryDate" id="empPassportExpiryDate" class="<%=(String)request.getAttribute("empPassportExpiryDateValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empPassportExpiryDate"), "") %>"/></td></tr>
												<tr><td class="txtlabel alignRight">PAN No.:<%=(String)request.getAttribute("empPanNoValidAsterix") %></td>
												<td>
													<input type="text" name="empPanNo" id="empPanNo" class="<%=(String)request.getAttribute("empPanNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empPanNo"), "") %>" onchange="fnValidatePAN();" onblur="fnValidatePAN();" />
													<p><span class="panmsg hidden" id="empPanNoMsg">Please Enter a Valid PAN No.</span></p>
												</td></tr>
												<%-- <tr><td class="txtlabel alignRight">MRD No.:<%=(String)request.getAttribute("empMRDNoValidAsterix") %></td>
												<td><input type="text" name="empMRDNo" id="empMRDNo" class="<%=(String)request.getAttribute("empMRDNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empMRDNo"), "") %>"/><span class="hint">Employee's MRD (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
											</table>
											Personal Statutory Information:<hr style="background-color:#346897; height:1px;">
											<table class="table form-table" style="margin-top: 10px;width: 81%;">
												<tr><td class="txtlabel alignRight">Provident Fund No.:<%=(String)request.getAttribute("empProvidentFundNoValidAsterix") %></td>
												<td><input type="text" name="empPFNo" id="empPFNo" class="<%=(String)request.getAttribute("empProvidentFundNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empPFNo"), "") %>"/></td></tr>
												<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_PF_START_DATE_IN_ADD_EMPLOYEE))) { %>
													<tr><td class="txtlabel alignRight">PF Start Date:<%=(String)request.getAttribute("empProvidentFundStartDateValidAsterix") %></td>
													<td><input type="text" name="empPFStartDate" id="empPFStartDate" class="<%=(String)request.getAttribute("empProvidentFundStartDateValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empPFStartDate"), "") %>"/></td></tr>
												<% } %>
												<tr><td class="txtlabel alignRight">GPF Acc No.:<%=(String)request.getAttribute("empGPFAccountNoValidAsterix") %></td>
												<td><input type="text" name="empGPFNo" id="empGPFNo" class="<%=(String)request.getAttribute("empGPFAccountNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empGPFNo"), "") %>"/><span class="hint">Employee's GPF Number <span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr><td class="txtlabel alignRight">ESIC No.:<%=(String)request.getAttribute("empESICNoValidAsterix") %></td>
												<td><input type="text" name="empESICNo" id="empESICNo" class="<%=(String)request.getAttribute("empESICNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empESICNo"), "") %>"/><span class="hint">Employee's ESIC Number (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr><td class="txtlabel alignRight">UAN No.:<%=(String)request.getAttribute("empUANNoValidAsterix") %></td>
												<td><input type="text" name="empUANNo" id="empUANNo" class="<%=(String)request.getAttribute("empUANNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empUANNo"), "") %>"/><span class="hint">Employee's UAN (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr><td class="txtlabel alignRight">Aadhaar No.:<%=(String)request.getAttribute("empUIDNoValidAsterix") %></td>
												<td>
													<input type="text" name="empUIDNo" id="empUIDNo" class="<%=(String)request.getAttribute("empUIDNoValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("empUIDNo"), "") %>" onchange="fnValidateADHAR();" onblur="fnValidateADHAR();" /><span class="hint">Employee's UID (optional but recommended)<span class="hint-pointer">&nbsp;</span></span>
													<p><span class="panmsg hidden" id="empUIDNoMsg">Please Enter a Valid Adhar No.</span></p>
												</td></tr>
												<tr> <td colspan="1" style="border-bottom:1px solid #346897">Only if you are medical professional</td>
													<td style="border-bottom:1px solid #346897">
												<%String defaultCheck = (String)request.getAttribute("isMedicalCheck"); 
													String checkedValue = "";
													if(uF.parseToInt(defaultCheck) == 1) { 
														checkedValue = "checked";
													} else {
														defaultCheck = "0";
													}
													List<String> empKmcNoValidList = hmValidationFields.get("EMP_KMC_NO"); 
													validAsterix = "";
													validReqOpt = "";
													if(empKmcNoValidList != null && uF.parseToBoolean(empKmcNoValidList.get(0))) {
														validAsterix = "<sup>*</sup>";
														validReqOpt = "validateRequired";
													}
											%>
												<input type="hidden" name="isMedicalCheck" id="isMedicalCheck" value="<%=defaultCheck%>"/>
												<input type="checkbox" name ="isMedicalProfessional" id="isMedicalProfessional" <%=checkedValue%> onchange="checkInfo(this)"/></td></tr>
												<tr id="tr_kmc_knc" style="display:none"><td class="txtlabel alignRight" >Select type:</td>
													<td><s:select headerKey="0" headerValue="None" list="#{'1':'KMC No.', '2':'KNC No.'}" name="strEmpKncKmc" id="strEmpKncKmc" onchange="checkKmcKnc(this.value);"/></td>
												</tr>
												<tr id="kmcNo_tr" style="display:none"><td class="txtlabel alignRight">KMC No.:<%=validAsterix%></td>
													<td><input type="text" name="strKmcNo" id="strKmcNo" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("strKmcNo"), "") %>" /></td>
												</tr>
												<% List<String> empKncNoValidList = hmValidationFields.get("EMP_KNC_NO"); 
													validAsterix = "";
													validReqOpt = "";
													if(empKncNoValidList != null && uF.parseToBoolean(empKncNoValidList.get(0))) {
														validAsterix = "<sup>*</sup>";
														validReqOpt = "validateRequired";
													}
											   %>
												<tr id="kncNo_tr" style="display:none"><td class="txtlabel alignRight">KNC No.:<%=validAsterix%></td>
													<td><input type="text" name="strKncNo" id="strKncNo" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("strKncNo"), "") %>"/></td>
												</tr>
												<% List<String> empRenewalValidList = hmValidationFields.get("EMP_KMCKNC_RENEWAL_DATE"); 
													validAsterix = "";
													validReqOpt = "";
													if(empRenewalValidList != null && uF.parseToBoolean(empRenewalValidList.get(0))) {
														validAsterix = "<sup>*</sup>";
														validReqOpt = "validateRequired";
													}
											   %>
												<tr id="renewalDate_tr" style="display:none"><td class="txtlabel alignRight">Renewal Date:<%=validAsterix%></td>
													<td><input type="text" name="strRenewalDate" id="strRenewalDate" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("strRenewalDate"), "") %>"/></td>
												</tr>
											</table>
											
											Upload Photo:<hr style="background-color:#346897; height:1px;">
											<div class="col-lg-4 col-md-12 col-sm-12">
											<table class="table " style="margin-top: 10px;border:solid 5px #CCCCCC;">
												<tr><td><strong>Upload employee image</strong><%=(String)request.getAttribute("empProfilePhotoValidAsterix") %></td></tr>
												<tr>
													<td>
													<% if(strEmImgPath!=null && !strEmImgPath.equals("")) { %>
														<%=strImage%>
													<% } else { %>
														<img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"/>
													<% } %>
													</td> 
												</tr>
												<tr>
													<td>
												     <% if(request.getAttribute("empProfilePhotoValidReqOpt") != null && !request.getAttribute("empProfilePhotoValidReqOpt").toString().equals("")) { %>
												     	<s:file name="empImage" id="empImage"  accept=".gif,.jpg,.png,.tif,.svg,.svgz" cssClass="validateRequired" onchange="readImageURL(this, 'profilecontainerimg');"></s:file>
												     <% } else { %>
												     	<s:file name="empImage" id="empImage" accept=".gif,.jpg,.png,.tif,.svg,.svgz" onchange="readImageURL(this, 'profilecontainerimg');"></s:file>
												     <% } %>
												     <span style="color:#929090;">Image size must be smaller than or equal to 500kb.</span>
												     </td>
												</tr>
											</table>
											</div>
											<div class="col-lg-4 col-md-12 col-sm-12">
											<table class="table" style="margin-top: 10px;border:solid 5px #CCCCCC;">
												<tr><td><strong>Upload employee cover image</strong><%=(String)request.getAttribute("empProfilePhotoValidAsterix") %></td></tr>
												<tr>
													<td>
														<% if(strEmpImgCoverPath!=null && !strEmpImgCoverPath.equals("")) { %>
															<%=strCoverImage%>
														<% } else { %>
															<img height="100" width="100" class="lazy img-circle" id="profilecontainerimgCover" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"/>
														<% } %>
													</td> 
												</tr>
												<tr><td>
												     <% if(request.getAttribute("empProfilePhotoValidReqOpt") != null && !request.getAttribute("empProfilePhotoValidReqOpt").toString().equals("")) { %>
												     	<s:file name="empCoverImage" id="empCoverImage"  accept=".gif,.jpg,.png,.tif,.svg,.svgz" cssClass="validateRequired" onchange="readImageURL(this, 'profilecontainerimgCover');"></s:file>
												     <% } else { %>
												     	<s:file name="empCoverImage" id="empCoverImage"  accept=".gif,.jpg,.png,.tif,.svg,.svgz" onchange="readImageURL(this, 'profilecontainerimgCover');"></s:file>
												     <% } %>
												     <span style="color:#929090;">Image size must be smaller than or equal to 500kb.</span>
												     </td>
												</tr>
											</table>
											</div>
											</div>
											<div class="clr"></div>
											<br/><br/>
											<div>
												<table class="table">
												<s:if test="mode == null">
													<tr>
													<td></td><td>
														<input type="submit" class="btn btn-primary next-step" id="btnSubmit" style="width:200px; float:left;" value="Submit & Proceed"/>
													</td></tr>
												</s:if>
												<s:else>
													<tr><td></td><td>
														<s:hidden name="mode" />
														<input type="submit" class="btn btn-primary" id="btnSubmit" value="Update Information"/>  <!-- onclick="submitForm();" -->
													</td></tr>
												</s:else>
												</table>
											</div>
										</s:form>
										</div>
									</div>
			                    </div>
			                    <script type="text/javascript">
			                    	showMarriageDate();
			                    </script>
			                    </s:if>
			                    
			                    <s:if test="step==2 || mode=='report'">
			                     <div class="tab-pane active" role="tabpanel" id="bi">
											<div>
												<s:form theme="simple" action="AddEmployee" id="frmBackgroundInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
												<s:hidden name="empId" />
												<s:hidden name="step" />
											    <div><span style="color:#68AC3B; font-size:22px">Step 2: </span><span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;">Enter Employees Background Information</span> </div>
												<div class="row row_without_margin" style="margin-top: 20px;">
													<div id="div_skills" class=" col-lg-5 col-md-5 col-sm-12" style="padding-left: 5px;padding-right: 5px;border: solid 2px #F5F5F5;height: 300px;overflow-y: auto;margin-left: 10px; margin-right: 10px;">
											        <h4 style="margin:5px 0px 10px 0px;background: aliceblue;padding: 5px;">Enter employee skills</h4>
													   <table class="table table-head-highlight">
													   	 <tbody id="table-skills">
													   	  <tr>
													   	  	<th></th>
													   	  	<th>Skill Name</th>
													   	  	<th>Skill Rating (1: min & 10: max)</th>
													   	  	<th style="width:10%"></th>
													   	  </tr>
													   	  <% 	
															if(alSkills!=null && alSkills.size()!=0) {
																String empId = alSkills.get(0).get(3);
															 	for(int i=0; i<alSkills.size(); i++) {
															 		List<String> innerList=alSkills.get(i);
															%>
																<tr id="row_skill_<%=i%>" class="row_skill">
																	<td><%if(i==0) { %>
																			[PRI]
																		<% } %>
																	</td>
																	<td>
																		<%=(String)request.getAttribute("empSkillNameValidAsterix") %> 
																		<select name="skillName" class="  " id="skillName<%=i %>" class="<%=(String)request.getAttribute("empSkillNameValidReqOpt") %>">
																		<option value="">Select Skill</option>
														                	<%for(int k=0; k< skillsList.size(); k++) {
														                		FillSkills fillSkills=skillsList.get(k);
														                		if((fillSkills.getSkillsId()+"").equals(innerList.get(0).trim())) {
														                	%>
														                		<option value="<%=fillSkills.getSkillsId() %>" selected="selected"> <%=fillSkills.getSkillsName() %> </option>
														                	<% } else { %>
														                		<option value="<%=fillSkills.getSkillsId() %>"><%=fillSkills.getSkillsName() %></option>
														                	<% } } %>
													                	</select>
																	</td>
																	<td><%=(String)request.getAttribute("empSkillRatingValidAsterix") %> 
																		<select name="skillValue" class=" " id="skillValue<%=i %>" class="<%=(String)request.getAttribute("empSkillRatingValidReqOpt") %>">
																		<option value="">Select Skill Rating</option>
														                	<%for(int k=1; k< 11; k++) { 
														                		if( (k+"").equals(innerList.get(2))) {
														                	%>
														                		<option value="<%=k%>" selected="selected"> <%=k%> </option>
														                	<% } else {%>
														                		<option value="<%=k%>"> <%=k%> </option>
														                	<% }
														                	}%>
													                	</select>
													                </td>
													                <td><a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a>
													                	<%if(i>0) { %>
															            <a href="javascript:void(0)" onclick="removeSkills(this.id)" id="<%=i%>" class="remove-font"></a>
															            <% } %>
													                </td>
																</tr>
																<% }
																 } else {
																 %>
																 <tr id="row_skill" class="row_skill">
																 	<td>[Pri]</td>
																 	<td><%=(String)request.getAttribute("empSkillNameValidAsterix") %> 
											                        	<select name="skillName" id="skillName0" class="<%=(String)request.getAttribute("empSkillNameValidReqOpt") %>" >
											                        		<option value="">Select Skill</option>
														                	<% for(int k=0; k< skillsList.size(); k++) {
														                		FillSkills fillSkills=skillsList.get(k);
														                	%> 
														                		<option value="<%=fillSkills.getSkillsId() %>"> <%=fillSkills.getSkillsName() %> </option>
														                	<% } %>
													                	</select>
																 	</td>
																 	<td><%=(String)request.getAttribute("empSkillRatingValidAsterix") %> 
											                        	<select name="skillValue" id="skillValue0" class="<%=(String)request.getAttribute("empSkillRatingValidReqOpt") %>  ">
											                        		<option value="">Select Skill Rating</option>
														                	<%for(int k=1; k< 11; k++) { %>
														                		<option value="<%=k%>"> <%=k%> </option>
													                		<% } %>
													                	</select>
																 	</td>
																 	<td><a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a></td>
																 </tr>
																 <% } %>
															</tbody>
													   </table>
												</div>
												<div id="div_education" class=" col-lg-5 col-md-5 col-sm-12" style="padding-left: 5px;padding-right: 5px;margin-left: 10px; margin-right: 10px;overflow-y: auto;">
											       <h4 style="padding:0px;margin:5px 0px 10px 0px;background: aliceblue;padding: 5px;">Enter employee educational details</h4>
													<div id="row_education" class="row_education">  
											                <table class="table table-head-highlight">
											                	<tbody id="table-education">
																<tr>
																	<th>Academics Name<%=(String)request.getAttribute("empDegreeNameValidAsterix") %></th>
															<!-- ===start parvez date: 30-07-2022=== -->		
																	<%-- <th>Inst Name<%=(String)request.getAttribute("empDegreeInstituteNameValidAsterix") %></th> --%>
																    <th>Institute Name<%=(String)request.getAttribute("empDegreeInstituteNameValidAsterix") %></th>
															<!-- ===end parvez date: 30-07-2022=== -->	    
																    <th>Duration<%=(String)request.getAttribute("empDegreeDurationValidAsterix") %></th>
																    <th>Completion Year<%=(String)request.getAttribute("empDegreeCompletionYearValidAsterix") %></th>
															<!-- ===start parvez date: 30-07-2022=== -->	    
																    <%-- <th>Grade<%=(String)request.getAttribute("empDegreeGradeValidAsterix") %></th> --%>
																    <th>Grade/Percentage<%=(String)request.getAttribute("empDegreeGradeValidAsterix") %></th>
															<!-- ===end parvez date: 30-07-2022=== -->	    
																    <th>Certificate<%=(String)request.getAttribute("empDegreeCertificateValidAsterix") %></th>
																    <th></th>
												                </tr> 
												                <% 	
																	if(alEducation!=null && alEducation.size()!=0) {
																 	for(int i=0; i<alEducation.size(); i++) {
																 		List<String> innerList=alEducation.get(i);
														
																%>
																<tr id="row_education_<%=i%>" class="row_education">
																	<td>
																		<input type="hidden" name="degreeId" value="<%=innerList.get(0) %>"/>
																		<select name="degreeName" id="degreeName<%=i %>" class="<%=(String)request.getAttribute("empDegreeNameValidReqOpt") %>" onchange="checkEducation(this.value, '<%=i %>');" >
													                	<%for(int k=0; k< educationalList.size(); k++) { 
													                		if((((FillEducational)educationalList.get(k)).getEduName()+"").equals(innerList.get(1))) {
													                	%>
													                		<option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" selected="selected"><%=((FillEducational)educationalList.get(k)).getEduName() %></option>
													                	<%} else { %>
													                		<option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" ><%=((FillEducational)educationalList.get(k)).getEduName() %></option>
													                	<% } } %>
													                	</select>
																	</td>
																	<td><input type="hidden" name="degreeNameOther" id="hidedegreeNameOther<%=i %>" />
																		<input type="text" name="instName" id="instName" class="<%=(String)request.getAttribute("empDegreeInstituteNameValidReqOpt") %>" value="<%=innerList.get(5)%>" />
													                </td>
													                <td>
													                	<select name="degreeDuration" style="width: 90px !important;" class="<%=(String)request.getAttribute("empDegreeDurationValidReqOpt") %>">
													                	<option value=0>Duration</option>
													                	<%for(int k=0; k< degreeDurationList.size(); k++) { 
													                		FillDegreeDuration fillDegreeDuration = degreeDurationList.get(k);
													                		if( (fillDegreeDuration.getDegreeDurationID()+"").equals( innerList.get(2) )) {
													                	%>
													                		<option value="<%=fillDegreeDuration.getDegreeDurationID() %>" selected="selected"><%=fillDegreeDuration.getDegreeDurationName() %></option>
													                	<% } else { %>
													                		<option value="<%=fillDegreeDuration.getDegreeDurationID() %>"><%=fillDegreeDuration.getDegreeDurationName() %></option>
													                	<% }
													                	} %>
													                	</select>
													                </td>
													                <td>
													                	<select name="completionYear" style="width: 100px !important;" class="<%=(String)request.getAttribute("empDegreeCompletionYearValidReqOpt") %>" >
													                		<option value="">Completion Year</option>
													                	<%for(int k=0; k< yearsList.size(); k++) { 
													                		FillYears fillYears = yearsList.get(k);
													                		if((fillYears.getYearsID()+"").equals(innerList.get(3))) {
													                	%>
													                		<option value="<%=fillYears.getYearsID() %>" selected="selected"><%=fillYears.getYearsName() %></option>
													                	<% } else { %>
													                		<option value="<%=fillYears.getYearsID() %>"><%=fillYears.getYearsName() %></option>
													                	<% } } %>
													                	</select>
													                </td>
													                <td> <input type="text"  name="grade" style="width: 80px !important;" class="<%=(String)request.getAttribute("empDegreeGradeValidReqOpt") %>" value="<%=innerList.get(4)%>" /> </td>
													                <td><input type="hidden" name="degreeCertiStatus" id="degreeCertiStatus<%=i %>" value="0">
													                	<div id="degreeCertiDiv<%=i %>"><input type="hidden" name="degreeCertiSubDivCnt<%=i %>" id="degreeCertiSubDivCnt<%=i %>" value="0" />
																			<div id="degreeCertiSubDiv<%=i %>_0">
																				<input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="degreeCertificate<%=i %>" id="degreeCertificate<%=i %>"  class="<%=(String)request.getAttribute("empDegreeCertificateValidReqOpt") %>" onchange="fillFileStatus('degreeCertiStatus<%=i %>');" />
																				<a href="javascript:void(0)" onclick="addEducationCerti('<%=i %>')" class="add-font"></a>
																			</div>
																		</div>
														            </td>
																	<td><a href="javascript:void(0)" onclick="addEducation()" class="add-font"></a>
																	<% if(i>0) { %>
																		<a href="javascript:void(0)" onclick="removeEducation(this.id)" id="<%=i%>" class="remove-font" ></a>
																	<% } %>
																	</td>
																</tr>
																<%} %>
																<div><input type="hidden" name="educationCnt" id="educationCnt" value="<%=alEducation.size()-1 %>" /></div>
																<% } else { %>
																	<tr id="row_education" class="row_education">
																		<td>
																			<input type="hidden" name="educationCnt" id="educationCnt" value="0"/>
																			<select name="degreeName" id="degreeName0" class="<%=(String)request.getAttribute("empDegreeNameValidReqOpt") %>" onchange="checkEducation(this.value, 0);" >
																				<option value="">Academics Degree</option>
																				<%for(int k=0; k< educationalList.size(); k++) {%> 
																				<option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" ><%=((FillEducational)educationalList.get(k)).getEduName() %></option>
																                <%} %>
																                <option value="other">Other</option> 
																            </select>
																		</td>
																		<td><input type="text" name="instName" id="instName" class="<%=(String)request.getAttribute("empDegreeInstituteNameValidReqOpt") %>" /> </td>
																		<td>
																		<%if(request.getAttribute("empDegreeDurationValidReqOpt") != null && !request.getAttribute("empDegreeDurationValidReqOpt").toString().equals("")) { %>
																			<s:select name="degreeDuration"	cssClass="validateRequired" cssStyle="width: 90px !important;" listKey="degreeDurationID" listValue="degreeDurationName" headerKey="-1"
																				headerValue="Duration" list="degreeDurationList" />
																		<% } else { %>
																			<s:select name="degreeDuration"	cssStyle="width: 100px !important;" listKey="degreeDurationID" listValue="degreeDurationName" headerKey="-1"
																				headerValue="Duration" list="degreeDurationList" />
																		<% } %>	
																		</td>
													                    <td>
													                    <%if(request.getAttribute("empDegreeCompletionYearValidReqOpt") != null && !request.getAttribute("empDegreeCompletionYearValidReqOpt").toString().equals("")) { %>
																			<s:select name="completionYear"	cssClass="validateRequired" cssStyle="width: 100px !important;" listKey="yearsID" listValue="yearsName" headerKey="-1"
																				headerValue="Completion Year" list="yearsList" />
																		<% } else { %>
																			<s:select name="completionYear" cssStyle="width: 100px !important;" listKey="yearsID" listValue="yearsName" headerKey="-1" headerValue="Completion Year" list="yearsList" />
																		<% } %>	
																		</td>
													                    <td><input type="text" name="grade" style="width: 80px !important;" class="<%=(String)request.getAttribute("empDegreeGradeValidReqOpt") %>" /> </td>
																		<td><input type="hidden" name="degreeCertiStatus" id="degreeCertiStatus0" value="0">
																			<div id="degreeCertiDiv0"><input type="hidden" name="degreeCertiSubDivCnt0" id="degreeCertiSubDivCnt0" value="0" />
																				<div id="degreeCertiSubDiv0_0">
																					<input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="degreeCertificate0" id="degreeCertificate0"  class="<%=(String)request.getAttribute("empDegreeCertificateValidReqOpt") %>" onchange="fillFileStatus('degreeCertiStatus0');" />
																					<a href="javascript:void(0)" onclick="addEducationCerti('0')" class="add-font"></a>
																				</div>
																			</div>
																		</td>
																		<td><a href="javascript:void(0);" onclick="addEducation()" class="add-font"></a></td>
																	</tr>
																	<% boolean flag = true; %>
																	<tr id="degreeNameOtherTR" style="display:none;">
																		<td colspan="1" style="text-align:right;">Enter Academics Degree:</td>
																		<td colspan="5"> 
																			<input type="text" name="degreeNameOther" id="degreeNameOther" class="<%=(String)request.getAttribute("empDegreeNameValidReqOpt") %>"/>
																			<%flag = false; %>
																		</td>
																		<td>
																			<input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="degreeCertificate0" id="degreeCertificate0"  class="<%=(String)request.getAttribute("empDegreeCertificateValidReqOpt") %>" onchange="fillFileStatus('degreeCertiStatus0');" />
																		</td>
																	</tr>   
																	<% if(flag) { %>
																		<tr><td colspan="6"><input type="hidden" name="degreeNameOther" /></td></tr>
																	<% } %>
																<% } %>
																</tbody>
											                </table>   
														</div>
													</div>
												</div>
												
												<div class="row row_without_margin" style="margin-top: 20px;">
													<div id="div_language" class=" col-lg-5 col-md-5 col-sm-12" style="margin-left: 10px; margin-right: 10px;overflow-y: auto;padding-left: 5px;padding-right: 5px;">
														<h4 style="padding:0px;margin:5px 0px 10px 0px;background: aliceblue;padding: 5px;">Enter employee languages</h4>
											           <div id="row_language" class="row_language">  
											                <table class="table table-head-highlight">   
											                	<tbody id="table-language">           
															  <tr>
																  <th>Language Name<%=(String)request.getAttribute("empLanguageNameValidAsterix") %> </th>
																  <th>Read</th>
																  <th>Write</th>
																  <th>Speak</th>
																  <th style="width: 60px;">Mother Tongue</th>
																  <th></th>
															  </tr>
															    <% 	
															    	List<String> hiddenIdList = new ArrayList<String>();
																	if(alLanguages!=null && alLanguages.size()!=0){
																 	for(int i=0; i<alLanguages.size(); i++) {
																 		List<String> innerList=alLanguages.get(i);
																%>
																<tr id="row_language_<%=i%>" class="row_language">
																	<td><input type="text" name="languageName" id="languageName<%=i %>" class="<%=(String)request.getAttribute("empLanguageNameValidReqOpt") %> " value="<%=innerList.get(1)%>" /></td>
																	<td align="center" >
												                    <% 	if(uF.parseToBoolean( innerList.get(2)) ) { %>
												                    	<input type="checkbox" name="isReadcheckbox" value="1" id="isRead_<%=i%>" checked="checked" onchange="showHideHiddenField(this.id)" />
												                    	<input type="hidden" name="isRead" value="1" id="hidden_isRead_<%=i%>" />
												                    <%}else { %>
												                    	<input type="checkbox" name="isReadcheckbox" value="0" id="isRead_<%=i%>" onchange="showHideHiddenField(this.id)" />
												                    	<input type="hidden" name="isRead" value="0" id="hidden_isRead_<%=i%>" />
												                    <%} %>
												                    </td>
												                     <td align="center" id="td_isWrite_<%=innerList.get(0)%>">
												                    <% if(uF.parseToBoolean( innerList.get(3)) ) { %>
												                    	<input type="checkbox" name="isWritecheckbox" value="1" checked="checked" id="isWrite_<%=i%>" onchange="showHideHiddenField(this.id)"	/>
												                    	<input type="hidden" name="isWrite" value="1" id="hidden_isWrite_<%=i%>" />
												                    <%}else { %>
												                    	<input type="checkbox" name="isWritecheckbox" value="0" id="isWrite_<%=i%>" onchange="showHideHiddenField(this.id)"	/>
												                    	<input type="hidden" name="isWrite" value="0" id="hidden_isWrite_<%=i%>" />
												                    <%} %>
												                    </td>
												                     <td align="center" id="td_isSpeak_<%=innerList.get(0)%>">
												                    <% if(uF.parseToBoolean( innerList.get(4)) ) { %>
												                    	<input type="checkbox" name="isSpeakcheckbox" value="1" checked="checked" id="isSpeak_<%=i%>" onchange="showHideHiddenField(this.id)"	/>
												                    	<input type="hidden" name="isSpeak" value="1" id="hidden_isSpeak_<%=i%>" />
												                    <%} else { %>
												                    	<input type="checkbox" name="isSpeakcheckbox" value="0" id="isSpeak_<%=i%>" onchange="showHideHiddenField(this.id)"	/>
												                    	<input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_<%=i%>" />
												                    <%} %>
												                    </td>
												                    
												                     <td align="center" id="td_isMotherTounge_<%=innerList.get(0)%>">
												                    <% if(uF.parseToBoolean( innerList.get(5)) ) { %>
												                    	<input type="radio" name="isMotherToungeRadio" value="1" checked="checked" id="isMotherTounge_" onchange="showHideHiddenField1(this.id, '<%=i%>')"	/>
												                    	<input type="hidden" name="isMotherTounge" value="1" id="hidden_isMotherTounge_<%=i%>" />
												                    <%} else { %>
												                    	<input type="radio" name="isMotherToungeRadio" value="0" id="isMotherTounge_" onchange="showHideHiddenField1(this.id, '<%=i%>')"	/>
												                    	<input type="hidden" name="isMotherTounge" value="0" id="hidden_isMotherTounge_<%=i%>" /> 	
												                    <%} %>
												                    </td>
															    	<td align="center">
															    	<%if(i==0) { %>
															    		<input type="hidden" name="hideLanguageRowCount" id="hideLanguageRowCount" value="<%=alLanguages.size() %>" />
															    	<% } %>
															    	<a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a>
															    	<%if(i>0) { %>
															        	<a href="javascript:void(0)" onclick="removeLanguages(this.id)" id="<%=i%>" class="remove-font" ></a>
															        <% } %>
															        </td>
																</tr>
																 <%}
																	 } else {
																%>
																<tr id="row_language" class="row_language" >
																	<td><input type="hidden" name="hideLanguageRowCount" id="hideLanguageRowCount" value="1" />
																 		<input type="text" name="languageName" id="languageName0" class="<%=(String)request.getAttribute("empLanguageNameValidReqOpt") %> " /></td>
																 	<td width="50px" align="center"><input type="checkbox" id="isRead_0" name="isReadcheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
																 		<input type="hidden" name="isRead" value="0" id="hidden_isRead_0" /></td>
																	<td width="50px" align="center"><input type="checkbox" id="isWrite_0" name="isWritecheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
																		<input type="hidden" name="isWrite" value="0" id="hidden_isWrite_0" /></td>
														            <td width="50px" align="center"><input type="checkbox" id="isSpeak_0" name="isSpeakcheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
														            	<input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_0" /></td>
														            <td width="50px" align="center"><input type="radio" id="isMotherTounge_" name="isMotherToungeRadio" value="1" onclick="showHideHiddenField1(this.id, '0')"/>
														                <input type="hidden" name="isMotherTounge" value="0" id="hidden_isMotherTounge_0" /></td>
															    	<td align="center"><a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a></td>
																</tr>
																<%} %>
																</tbody>
											                </table>
													   </div>	
												</div>
												
												<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EMPLOYEE_HOBBIES_DISABLE))){ %>
														<div id="div_hobbies" class=" col-lg-5 col-md-5 col-sm-12" style="padding-left: 5px;padding-right: 5px;border: solid 2px #F5F5F5;height: 300px;margin-left: 10px; margin-right: 10px;overflow-y: auto;">
															<h4 style="padding:0px;margin:5px 0px 10px 0px;background: aliceblue;padding: 5px;">Enter employee hobbies</h4>
														    	<div id="row_hobby" class="row_hobby"> 
														           <table class="table table-head-highlight">   
														              <tbody id="table-hobbies">           
																		  <tr>
																			  <th>Hobby Name<%=(String)request.getAttribute("empHobbyNameValidAsterix") %></th>
																			  <th></th>
																		  </tr>
																		  <% if(alHobbies!=null && alHobbies.size()!=0){
																			String empId = ((List<String>)alHobbies.get(0)).get(2);
																			for(int i=0; i<alHobbies.size(); i++) {
																			 	List<String> innerList=alHobbies.get(i);
																			%>
																			<tr id="row_hobby_<%=i%>" class="row_hobby">
																				<td><input type="text" name="hobbyName" id="hobbyName<%=i %>" class="<%=(String)request.getAttribute("empHobbyNameValidReqOpt") %> " value="<%=innerList.get(1)%>" /></td> 
														                            <td><a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a>
														                            <%if(i>0){ %>
														                          	  	<a href="javascript:void(0)" onclick="removeHobbies(this.id)" id="<%=i%>" class="remove-font" ></a>
														                            <% } %></td>
																			</tr>
																			<% }
																		 } else {
																		 %>
																		 	<tr><td><%=(String)request.getAttribute("empHobbyNameValidAsterix") %>
														                          <input type="text" name="hobbyName" id="hobbyName0" class="<%=(String)request.getAttribute("empHobbyNameValidReqOpt") %> " /></td>
														                              <td><a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a></td>
														                          </tr> 
																		 <% } %>
																	</tbody>
																</table>
															</div>	
														</div>
												<% } %>
												<%-- <div id="div_hobbies" class=" col-lg-5 col-md-5 col-sm-12" style="padding-left: 5px;padding-right: 5px;border: solid 2px #F5F5F5;height: 300px;margin-left: 10px; margin-right: 10px;overflow-y: auto;">
												<h4 style="padding:0px;margin:5px 0px 10px 0px;background: aliceblue;padding: 5px;">Enter employee hobbies</h4>
											           <div id="row_hobby" class="row_hobby"> 
											           <table class="table table-head-highlight">   
											              <tbody id="table-hobbies">           
															  <tr>
																  <th>Hobby Name<%=(String)request.getAttribute("empHobbyNameValidAsterix") %></th>
																  <th></th>
															  </tr>
															  <% if(alHobbies!=null && alHobbies.size()!=0){
																String empId = ((List<String>)alHobbies.get(0)).get(2);
																for(int i=0; i<alHobbies.size(); i++) {
																 	List<String> innerList=alHobbies.get(i);
																%>
																<tr id="row_hobby_<%=i%>" class="row_hobby">
																	<td><input type="text" name="hobbyName" id="hobbyName<%=i %>" class="<%=(String)request.getAttribute("empHobbyNameValidReqOpt") %> " value="<%=innerList.get(1)%>" /></td> 
											                            <td><a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a>
											                            <%if(i>0){ %>
											                          	  	<a href="javascript:void(0)" onclick="removeHobbies(this.id)" id="<%=i%>" class="remove-font" ></a>
											                            <% } %></td>
																</tr>
																<% }
															 } else {
															 %>
															 	<tr><td><%=(String)request.getAttribute("empHobbyNameValidAsterix") %>
											                          <input type="text" name="hobbyName" id="hobbyName0" class="<%=(String)request.getAttribute("empHobbyNameValidReqOpt") %> " /></td>
											                              <td><a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a></td>
											                          </tr> 
															 <% } %>
													      </tbody>
														</table>
													   </div>	
												</div> --%>
												</div>
												<div class="clr"></div>
												<div style="float:right;margin-top: 30px;">
														<table>
															<tr><td colspan="2" align="center">
															<s:if test="mode==null">
																<s:submit cssClass="btn btn-primary next-step" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
															</s:if>
															<s:else>
																<s:hidden name="mode" />
																<s:submit cssClass="btn btn-primary" value="Update Information" align="center" />
															</s:else>
															</td></tr>
															</table>
														</div>
												</s:form>
											</div>
			                    </div>
			                    </s:if>
			                    <s:if test="step==5 || mode=='report'">
			                    <div class="tab-pane active" role="tabpanel" id="fi">
										<div>	<!-- Family Information -->
										<s:form theme="simple" action="AddEmployee" id="frmFamilyInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
											<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" >
											<s:hidden name="empId" />
											<s:hidden name="step" />
											<table border="0" class="formcss">
											<tr><td  class=" tdLabelheadingBg " colspan="2"><span style="color:#68AC3B; font-size:22px">Step 5: </span>
										    <span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;">Enter Employees Family Information </span></td></tr>
											</table>
										    <table>
										      <tr><td>
												<table class="table form-table">	
												       <tr><td  style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Father's Information </td></tr>
													 	<tr>
													 	<td class="txtlabel alignRight">Name:<%=(String)request.getAttribute("empFatherNameValidAsterix") %></td>
													 	<td><input type="text" name="fatherName" id="fatherName" class="<%=(String)request.getAttribute("empFatherNameValidReqOpt") %> " value="<%=uF.showData((String)request.getAttribute("fatherName"),"")%>"/></td></tr>
														<tr>
														<td class="txtlabel alignRight">Date of birth:<%=(String)request.getAttribute("empFatherDateOfBirthValidAsterix") %></td>
														<td><input type="text" name="fatherDob" id="fatherDob" value="<%=uF.showData((String)request.getAttribute("fatherDob"),"")%>" class="<%=(String)request.getAttribute("empFatherDateOfBirthValidReqOpt") %>" /></td></tr>
														<tr>
														<td class="txtlabel alignRight">Education:<%=(String)request.getAttribute("empFatherEductionValidAsterix") %></td>
														<td><input type="text" name="fatherEducation" id="fatherEducation" value="<%=uF.showData((String)request.getAttribute("fatherEducation"),"")%>" class="<%=(String)request.getAttribute("empFatherEductionValidReqOpt") %> "/></td></tr>
														<tr>
														<td class="txtlabel alignRight">Occupation:<%=(String)request.getAttribute("empFatherOccupationValidAsterix") %></td>
														<td><input type="text" name="fatherOccupation" id="fatherOccupation" value="<%=uF.showData((String)request.getAttribute("fatherOccupation"),"")%>" class="<%=(String)request.getAttribute("empFatherOccupationValidReqOpt") %> "/></td></tr>
														<tr>
														<td class="txtlabel alignRight">Contact Number:<%=(String)request.getAttribute("empFatherContactNoValidAsterix") %></td>
														<td><input type="text" name="fatherContactNumber" onkeypress="return isOnlyNumberKey(event)" id="fatherContactNumber" value="<%=uF.showData((String)request.getAttribute("fatherContactNumber"),"")%>" class="<%=(String)request.getAttribute("empFatherContactNoValidReqOpt") %> "/></td></tr>
														<tr>
														<td class="txtlabel alignRight">Email Id:<%=(String)request.getAttribute("empFatherEmailIdValidAsterix") %></td>
														<td><input type="text" name="fatherEmailId" id="fatherEmailId" value="<%=uF.showData((String)request.getAttribute("fatherEmailId"),"")%>" class="<%=(String)request.getAttribute("empFatherEmailIdValidReqOpt") %> "/></td></tr>
														<tr><td class="txtlabel alignRight">MRD No.:<%=(String)request.getAttribute("empFatherMRDNoValidAsterix")%></td>
														<td><input type="text" style="width: 180px;" name="fatherMRDNo" id="fatherMRDNo" class="<%=(String)request.getAttribute("empFatherMRDNoValidReqOpt") %>"  value="<%=uF.showData((String)request.getAttribute("fatherMRDNo"),"")%>"/></td></tr>
														<tr><td colspan="2">&nbsp;</td></tr>
											   </table>     
												</td>
										        <td>
										       <table class="table form-table">	 	 
												   <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Mother's Information </td></tr>
												 	<tr>
												 	<td class="txtlabel alignRight">Name:<%=(String)request.getAttribute("empMotherNameValidAsterix") %></td>
												 	<td><input type="text" name="motherName" id="motherName" value="<%=uF.showData((String)request.getAttribute("motherName"),"")%>" class="<%=(String)request.getAttribute("empMotherNameValidReqOpt") %> "/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Date of birth:<%=(String)request.getAttribute("empMotherDateOfBirthValidAsterix") %></td>
													<td> <input type="text" name="motherDob" id="motherDob" value="<%=uF.showData((String)request.getAttribute("motherDob"),"")%>" class="<%=(String)request.getAttribute("empMotherDateOfBirthValidReqOpt") %>"/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Education:<%=(String)request.getAttribute("empMotherEducationValidAsterix") %></td>
													<td> <input type="text" name="motherEducation" id="motherEducation" value="<%=uF.showData((String)request.getAttribute("motherEducation"),"")%>" class="<%=(String)request.getAttribute("empMotherEducationValidReqOpt") %> "/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Occupation:<%=(String)request.getAttribute("empMotherOccupationValidAsterix") %></td>
													<td> <input type="text" name="motherOccupation" id="motherOccupation" value="<%=uF.showData((String)request.getAttribute("motherOccupation"),"")%>" class="<%=(String)request.getAttribute("empMotherOccupationValidReqOpt") %> "/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Contact Number:<%=(String)request.getAttribute("empMotherContactNoValidAsterix") %></td>
													<td> <input type="text" name="motherContactNumber" onkeypress="return isOnlyNumberKey(event)" id="motherContactNumber" value="<%=uF.showData((String)request.getAttribute("motherContactNumber"),"")%>" class="<%=(String)request.getAttribute("empMotherContactNoValidReqOpt") %> "/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Email Id:<%=(String)request.getAttribute("empMotherEmailIdValidAsterix") %></td>
													<td> <input type="text" name="motherEmailId" id="motherEmailId" value="<%=uF.showData((String)request.getAttribute("motherEmailId"),"")%>" class="<%=(String)request.getAttribute("empMotherEmailIdValidReqOpt") %> "/></td></tr>
													<tr><td class="txtlabel alignRight">MRD No.:<%=(String)request.getAttribute("empMotherMRDNoValidAsterix")%></td>
													<td><input type="text" style="width: 180px;" name="motherMRDNo" id="motherMRDNo" class="<%=(String)request.getAttribute("empMotherMRDNoValidReqOpt") %>"  value="<%=uF.showData((String)request.getAttribute("motherMRDNo"),"")%>"/></td></tr>
													<tr><td colspan="2">&nbsp;</td></tr>
												</table>     
												</td>
										        <td>
										       <table class="table form-table">	 	 
												   	<tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Spouse's Information </td></tr>
												 	<tr>
												 	<td class="txtlabel alignRight">Name:<%=(String)request.getAttribute("empSpouseNameValidAsterix") %></td>
												 	<td><input type="text" name="spouseName" id="spouseName" value="<%=uF.showData((String)request.getAttribute("spouseName"),"")%>" class="<%=(String)request.getAttribute("empSpouseNameValidReqOpt") %> "/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Date of birth:<%=(String)request.getAttribute("empSpouseDateOfBirthValidAsterix") %></td>
													<td> <input type="text" name="spouseDob" id="spouseDob" value="<%=uF.showData((String)request.getAttribute("spouseDob"),"")%>" class="<%=(String)request.getAttribute("empSpouseDateOfBirthValidReqOpt") %>" /></td></tr>
													<tr>
													<td class="txtlabel alignRight">Education:<%=(String)request.getAttribute("empSpouseEducationValidAsterix") %></td>
													<td> <input type="text" name="spouseEducation" id="spouseEducation" value="<%=uF.showData((String)request.getAttribute("spouseEducation"),"")%>" class="<%=(String)request.getAttribute("empSpouseEducationValidReqOpt") %> "/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Occupation:<%=(String)request.getAttribute("empSpouseOccupationValidAsterix") %></td>
													<td> <input type="text" name="spouseOccupation" id="spouseOccupation" value="<%=uF.showData((String)request.getAttribute("spouseOccupation"),"")%>"  class="<%=(String)request.getAttribute("empSpouseOccupationValidReqOpt") %> "/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Contact Number:<%=(String)request.getAttribute("empSpouseContactNoValidAsterix") %></td>
													<td> <input type="text" onkeypress="return isOnlyNumberKey(event)" name="spouseContactNumber" id="spouseContactNumber" value="<%=uF.showData((String)request.getAttribute("spouseContactNumber"),"")%>" class="<%=(String)request.getAttribute("empSpouseContactNoValidReqOpt") %> "/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Email Id:<%=(String)request.getAttribute("empSpouseEmailIdValidAsterix") %></td>
													<td> <input type="text" name="spouseEmailId" id="spouseEmailId" value="<%=uF.showData((String)request.getAttribute("spouseEmailId"),"")%>" class="<%=(String)request.getAttribute("empSpouseEmailIdValidReqOpt") %> "/></td></tr>
													<tr><td class="txtlabel alignRight">MRD No.:<%=(String)request.getAttribute("empSpouseMRDNoValidAsterix")%></td>
													<td><input type="text" style="width: 180px;" name="spouseMRDNo" id="spouseMRDNo" class="<%=(String)request.getAttribute("empSpouseMRDNoValidReqOpt") %>"  value="<%=uF.showData((String)request.getAttribute("spouseMRDNo"),"")%>"/></td></tr>
													<tr>
													<td class="txtlabel alignRight">Gender:<%=(String)request.getAttribute("empSpouseGenderValidAsterix") %></td>
													<td>
													<%if(request.getAttribute("empSpouseEmailIdValidReqOpt") != null && !request.getAttribute("empSpouseGenderValidReqOpt").toString().equals("")) { %>
														<s:select theme="simple" label="Select Gender" name="spouseGender" cssClass="validateRequired " cssStyle="width: 189px;" listKey="genderId" 
															listValue="genderName" headerKey="" headerValue="Select Gender" list="empGenderList" key="" />
													<% } else { %>
														<s:select theme="simple" label="Select Gender" name="spouseGender" cssStyle="width: 189px;" listKey="genderId" listValue="genderName" 
															headerKey="" headerValue="Select Gender" list="empGenderList" key="" />
													<% } %>	
													</td>
													</tr>
												</table>
												</td></tr>	 
										    </table>    
										             </div>
										             <div id="div_id_family">
													<%	if(alSiblings!=null && alSiblings.size()>0) {
															for(int i=0; i<alSiblings.size(); i++) { 
																List<String> innerList=alSiblings.get(i);
															%>
													<div id="col_family_siblings_<%=i%>" style="float:left; border:solid 0px #ccc;" >
										               <table class="table form-table autoWidth"> 
										                <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td></tr>    
													 	<tr>
													 	<td class="txtlabel alignRight">Name:<%=(String)request.getAttribute("empSiblingNameValidAsterix") %></td>
													 	<td><input type="text" name="memberName" id="memberName<%=i %>" class="<%=(String)request.getAttribute("empSiblingNameValidReqOpt") %> " value="<%=innerList.get(1)%>" ></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Date of birth:<%=(String)request.getAttribute("empSiblingDateOfBirthValidAsterix") %></td>
														<td> <input type="text" name="memberDob" id="memberDob<%=i %>" class="<%=(String)request.getAttribute("empSiblingDateOfBirthValidReqOpt") %>" value="<%=innerList.get(2)%>"></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Education:<%=(String)request.getAttribute("empSiblingEducationValidAsterix") %></td>
														<td><input type="text" name="memberEducation" id="memberEducation<%=i %>" class="<%=(String)request.getAttribute("empSiblingEducationValidReqOpt") %> " value="<%=innerList.get(3)%>"></input></td></tr>    
														<tr>
														<% 	
														%>
														<td class="txtlabel alignRight">Occupation:<%=(String)request.getAttribute("empSiblingOccupationValidAsterix") %></td>
														<td> <input type="text" name="memberOccupation" id="memberOccupation<%=i %>" class="<%=(String)request.getAttribute("empSiblingOccupationValidReqOpt") %> " value="<%=innerList.get(4)%>"></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Contact Number:<%=(String)request.getAttribute("empSiblingContactNoValidAsterix") %></td>
														<td><input type="text" name="memberContactNumber" onkeypress="return isOnlyNumberKey(event)" id="memberContactNumber<%=i %>" class="<%=(String)request.getAttribute("empSiblingContactNoValidReqOpt") %> " value="<%=innerList.get(5)%>"></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Email Id:<%=(String)request.getAttribute("empSiblingEmailIdValidAsterix") %></td>
														<td> <input type="text" name="memberEmailId" id="memberEmailId<%=i %>" class="<%=(String)request.getAttribute("empSiblingEmailIdValidReqOpt") %> " value="<%=innerList.get(6)%>"></input></td></tr>    
														<tr><td class="txtlabel alignRight">MRD No.:<%=(String)request.getAttribute("empSiblingsMRDNoValidAsterix")%></td>
														<td><input type="text" style="width: 180px;" name="siblingsMRDNo" id="siblingsMRDNo<%=i%>" class="<%=(String)request.getAttribute("empSiblingsMRDNoValidReqOpt")%>"  value="<%=innerList.get(9)%>"/></td></tr>
														<tr>
														<td class="txtlabel alignRight">Gender:<%=(String)request.getAttribute("empSiblingGenderValidAsterix") %></td>
															<td>
											                	<select name="memberGender" id="memberGender" class="<%=(String)request.getAttribute("empSiblingGenderValidReqOpt") %> " style="width: 189px;" >
												                	<option value="" >Select Gender</option>
												                	<%for(int k=0; k< empGenderList.size(); k++) { 
												                		FillGender fillGender=empGenderList.get(k);
												                		if( (fillGender.getGenderId()+"").equals(innerList.get(7) )) {
												                	%>
												                		<option value="<%=fillGender.getGenderId() %>" selected="selected"><%=fillGender.getGenderName() %></option>
												                	<%}else { %>
												                		<option value="<%=fillGender.getGenderId() %>"><%=fillGender.getGenderName() %></option>
												                	<% }
												                	}%>
											                	</select>
												            </td>
												        </tr>
												        <tr>
												        <td class="txtlabel alignRight">Marital Status:<%=(String)request.getAttribute("empSiblingMaritalStatusValidAsterix") %></td>
														  <td>
														  <select name="siblingMaritalStatus" id="siblingMaritalStatus" class="<%=(String)request.getAttribute("empSiblingMaritalStatusValidReqOpt") %> " style="width: 189px;">
												                	<option>Select Marital Status</option>
												                	<%for(int k=0; k< maritalStatusList.size(); k++) { 
												                		FillMaritalStatus fillMaritalStatus=maritalStatusList.get(k);
												                		if((fillMaritalStatus.getMaritalStatusId()+"").equals(innerList.get(8))) {
												                	%>
												                		<option value="<%=fillMaritalStatus.getMaritalStatusId() %>" selected="selected">
												                		<%=fillMaritalStatus.getMaritalStatusName() %>
												                		</option>
												                	<% } else { %>
												                		<option value="<%=fillMaritalStatus.getMaritalStatusId() %>">
												                		<%=fillMaritalStatus.getMaritalStatusName() %>
												                		</option>
												                	<% }
												                	}%>
											                	</select>
														 </td></tr>
														  
												    	<tr><td class="txtlabel alignRight" colspan="2">
												    	<a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add-font">&nbsp;</a>
												    	<% if(i > 0 ) { %>
												    		<a href="javascript:void(0)" onclick="removeSibling(this.id)" id="<%=i%>" class="remove-font" ></a>
												    	<%} %>
												    	</td></tr>    
										                    
										               </table>     
													</div>
													<%}
													} else { %>
													<div id="col_family_siblings" style="float: left;border:solid 0px #f00;" >
										               <table class="table form-table autoWidth">
										                <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information</td></tr>        
													 	<tr>
													 	<td class="txtlabel alignRight">Name:<%=(String)request.getAttribute("empSiblingNameValidAsterix") %></td>
													 	<td><input type="text" name="memberName" id="memberName0" class="<%=(String)request.getAttribute("empSiblingNameValidReqOpt") %> " ></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Date of birth:<%=(String)request.getAttribute("empSiblingDateOfBirthValidAsterix") %></td>
														<td> <input type="text" name="memberDob" id="memberDob0" class="<%=(String)request.getAttribute("empSiblingDateOfBirthValidReqOpt") %>"></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Education:<%=(String)request.getAttribute("empSiblingEducationValidAsterix") %></td>
														<td><input type="text" name="memberEducation" id="memberEducation0" class="<%=(String)request.getAttribute("empSiblingEducationValidReqOpt") %> " ></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Occupation:<%=(String)request.getAttribute("empSiblingOccupationValidAsterix") %></td>
														<td> <input type="text" name="memberOccupation" id="memberOccupation0" class="<%=(String)request.getAttribute("empSiblingOccupationValidReqOpt") %> " ></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Contact Number:<%=(String)request.getAttribute("empSiblingContactNoValidAsterix") %></td>
														<td><input type="text" name="memberContactNumber" onkeypress="return isOnlyNumberKey(event)" id="memberContactNumber0" class="<%=(String)request.getAttribute("empSiblingContactNoValidReqOpt") %> " ></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Email Id:<%=(String)request.getAttribute("empSiblingEmailIdValidAsterix") %></td>
														<td> <input type="text" name="memberEmailId" id="memberEmailId0" class="<%=(String)request.getAttribute("empSiblingEmailIdValidReqOpt") %> " ></input></td></tr>    
													 	<tr><td class="txtlabel alignRight">MRD No.:<%=(String)request.getAttribute("empSiblingsMRDNoValidAsterix")%></td>
														<td><input type="text" style="width: 180px;" name="siblingsMRDNo" id="siblingsMRDNo0" class="<%=(String)request.getAttribute("empSiblingsMRDNoValidReqOpt")%>" /></td>
														</tr>
													 	<tr>
													 	<td class="txtlabel alignRight">Gender:<%=(String)request.getAttribute("empSiblingGenderValidAsterix") %></td>
														<td>
														<% if(request.getAttribute("empSiblingGenderValidReqOpt") != null && !request.getAttribute("empSiblingGenderValidReqOpt").toString().equals("")) { %>
															<s:select theme="simple" label="Select Gender" name="memberGender" cssClass="validateRequired " cssStyle="width: 189px;" listKey="genderId" 
																listValue="genderName" headerKey="" headerValue="Select Gender" list="empGenderList" key="" required="true" />
														<% } else { %>
															<s:select theme="simple" label="Select Gender" name="memberGender" cssStyle="width: 189px;" listKey="genderId" listValue="genderName" 
																headerKey="" headerValue="Select Gender" list="empGenderList" key="" />
														<% } %>	
														</td></tr>
														<tr>
														<td class="txtlabel alignRight">Marital Status:<%=(String)request.getAttribute("empSiblingMaritalStatusValidAsterix") %></td>
														<td>
														<% if(request.getAttribute("empSiblingMaritalStatusValidReqOpt") != null && !request.getAttribute("empSiblingMaritalStatusValidReqOpt").toString().equals("")) { %>
														   	<s:select theme="simple" name="siblingMaritalStatus" cssClass="validateRequired " cssStyle="width: 189px;" listKey="maritalStatusId"
															listValue="maritalStatusName" headerKey="" headerValue="Select Marital Status" list="maritalStatusList" key="" required="true" />
														<% } else { %>
															<s:select theme="simple" name="siblingMaritalStatus" cssStyle="width: 189px;" listKey="maritalStatusId" listValue="maritalStatusName" headerKey="" 
															headerValue="Select Marital Status" list="maritalStatusList" key=""/>
														<% } %>	
														</td></tr>
												    	<tr><td class="txtlabel alignRight" colspan="2"><a href="javascript:void(0)" onclick="addSibling()" class="add-font" style="float:right">&nbsp;</a></td></tr>
												       </table>
													</div>
													<% } %>
													</div>
													<div class="clr"></div>
													<div id="div_id_child">
													<%	if(alchilds != null && alchilds.size() != 0) {
															for(int i=0; i<alchilds.size(); i++) {
																List<String> innerList=alchilds.get(i);
													%>
													<div id="col_family_child_<%=i%>" style="float:left; border:solid 0px #ccc;" >
										               <table class="table form-table autoWidth"> 
										                  <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Child's Information </td></tr>    
													 	<tr>
													 	<td class="txtlabel alignRight">Name:<%=(String)request.getAttribute("empChildNameValidAsterix") %></td>
													 	<td><input type="text" name="childName" id="childName<%=i %>" class="<%=(String)request.getAttribute("empChildNameValidReqOpt") %> " value="<%=innerList.get(1)%>" /></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Date of birth:<%=(String)request.getAttribute("empChildDateOfBirthValidAsterix") %></td>
														<td> <input type="text" name="childDob" id="childDob<%=i %>" class="<%=(String)request.getAttribute("empChildDateOfBirthValidReqOpt") %>" value="<%=innerList.get(2)%>"></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Education:<%=(String)request.getAttribute("empChildEducationValidAsterix") %></td>
														<td><input type="text" name="childEducation" id="childEducation<%=i %>" class="<%=(String)request.getAttribute("empChildEducationValidReqOpt") %> " value="<%=innerList.get(3)%>"></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Occupation:<%=(String)request.getAttribute("empChildOccupationValidAsterix") %></td>
														<td> <input type="text" name="childOccupation" id="childOccupation<%=i %>" class="<%=(String)request.getAttribute("empChildOccupationValidReqOpt") %> " value="<%=innerList.get(4)%>"></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Contact Number:<%=(String)request.getAttribute("empChildContactNoValidAsterix") %></td>
														<td> <input type="text" onkeypress="return isOnlyNumberKey(event)" name="childContactNumber" id="childContactNumber<%=i %>" class="<%=(String)request.getAttribute("empChildContactNoValidReqOpt") %> " value="<%=innerList.get(5)%>"></input></td></tr>    
														<tr>
														<td class="txtlabel alignRight">Email Id:<%=(String)request.getAttribute("empChildEmailIdValidAsterix") %></td>
														<td> <input type="text" name="childEmailId" id="childEmailId<%=i %>" class="<%=(String)request.getAttribute("empChildEmailIdValidReqOpt") %> " value="<%=innerList.get(6)%>"></input></td></tr>    
														<tr><td class="txtlabel alignRight">MRD No.:<%=(String)request.getAttribute("empChildMRDNoValidAsterix")%></td>
														<td><input type="text" style="width: 180px;" name="childMRDNo" id="childMRDNo<%=i %>" class="<%=(String)request.getAttribute("empChildMRDNoValidReqOpt")%>" value="<%=innerList.get(9)%>"/></td>
														</tr>
														<tr>
														<td class="txtlabel alignRight">Gender:<%=(String)request.getAttribute("empChildGenderValidAsterix") %></td>
															<td>
											                	<select name="childGender" id="childGender<%=i %>" class="<%=(String)request.getAttribute("empChildGenderValidReqOpt") %> " style="width: 189px;">
												                	<option value="" >Select Gender</option>
												                	<% for(int k=0; k< empGenderList.size(); k++) { 
												                		FillGender fillGender = empGenderList.get(k);
												                		if( (fillGender.getGenderId()+"").equals(innerList.get(7) )) {
												                	%>
												                		<option value="<%=fillGender.getGenderId() %>" selected="selected"><%=fillGender.getGenderName() %></option>
												                	<% } else { %>
												                		<option value="<%=fillGender.getGenderId() %>"><%=fillGender.getGenderName() %></option>
												                	<% } } %>
											                	</select>     
												            </td>
												        </tr>
														  <tr>
														  <td class="txtlabel alignRight">Marital Status:<%=(String)request.getAttribute("empChildMaritalStatusValidAsterix") %></td>
														  <td>
														  <select name="childMaritalStatus" id="childMaritalStatus<%=i %>" class="<%=(String)request.getAttribute("empChildMaritalStatusValidReqOpt") %> " style="width: 189px;">
										                	<option value="" >Select Marital Status</option>
										                	<%for(int k=0; k< maritalStatusList.size(); k++) { 
										                		FillMaritalStatus fillMaritalStatus = maritalStatusList.get(k);
										                		if((fillMaritalStatus.getMaritalStatusId()+"").equals(innerList.get(8) )) {
										                	%>
										                		<option value="<%=fillMaritalStatus.getMaritalStatusId() %>" selected="selected"><%=fillMaritalStatus.getMaritalStatusName() %></option>
										                	<% } else { %>
										                		<option value="<%=fillMaritalStatus.getMaritalStatusId() %>"><%=fillMaritalStatus.getMaritalStatusName() %></option>
										                	<% } } %>
										               	</select>
														 </td></tr>
												    	<tr><td class="txtlabel alignRight" colspan="2">
												    		<a href="javascript:void(0)" onclick="addChildren()"  style="float:right" class="add-font">&nbsp;</a>
												    	 <% if(i > 0) { %>
												    		<a href="javascript:void(0)" onclick="removeChildren(this.id)" id="<%=i%>" class="remove-font" ></a>
												    	<%} %>
												    	</td></tr>    
										               </table>     
													</div>
													<%}}else { %>
													<div id="col_family_child" style="float: left;border:solid 0px #f00;" >
										               <table class="table form-table autoWidth"> 
										                <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Child's Information </td></tr>        
													 	<tr><td class="txtlabel alignRight">Name:<%=(String)request.getAttribute("empChildNameValidAsterix") %></td>
													 	<td><input type="text" name="childName" id="childName0" class="<%=(String)request.getAttribute("empChildNameValidReqOpt") %> " /></td></tr>    
														<tr><td class="txtlabel alignRight">Date of birth:<%=(String)request.getAttribute("empChildDateOfBirthValidAsterix") %></td>
														<td> <input type="text" name="childDob" id="childDob0" class="<%=(String)request.getAttribute("empChildDateOfBirthValidReqOpt") %>"></input></td></tr>    
														<tr><td class="txtlabel alignRight">Education:<%=(String)request.getAttribute("empChildEducationValidAsterix") %></td>
														<td><input type="text" name="childEducation" id="childEducation0" class="<%=(String)request.getAttribute("empChildEducationValidReqOpt") %> " ></input></td></tr>    
														<tr><td class="txtlabel alignRight">Occupation:<%=(String)request.getAttribute("empChildOccupationValidAsterix") %></td>
														<td> <input type="text" name="childOccupation" id="childOccupation0" class="<%=(String)request.getAttribute("empChildOccupationValidReqOpt") %> " ></input></td></tr>    
														<tr><td class="txtlabel alignRight">Contact Number:<%=(String)request.getAttribute("empChildContactNoValidAsterix") %></td>
														<td> <input type="text" onkeypress="return isOnlyNumberKey(event)" name="childContactNumber" id="childContactNumber0" class="<%=(String)request.getAttribute("empChildContactNoValidReqOpt") %> " ></input></td></tr>    
														<tr><td class="txtlabel alignRight">Email Id:<%=(String)request.getAttribute("empChildEmailIdValidAsterix") %></td>
														<td> <input type="text" name="childEmailId" id="childEmailId0" class="<%=(String)request.getAttribute("empChildEmailIdValidReqOpt") %> " ></input></td></tr>    
													 	<tr><td class="txtlabel alignRight">MRD No.:<%=(String)request.getAttribute("empChildMRDNoValidAsterix")%></td>
														<td><input type="text" style="width: 180px;" name="childMRDNo" id="childMRDNo0" class="<%=(String)request.getAttribute("empChildMRDNoValidReqOpt")%>" /></td></tr>
													 	<tr><td class="txtlabel alignRight">Gender:<%=(String)request.getAttribute("empChildGenderValidAsterix") %></td>
														<td>
														<% if(request.getAttribute("empChildGenderValidReqOpt") != null && !request.getAttribute("empChildGenderValidReqOpt").toString().equals("")) { %>
															<s:select theme="simple" label="Select Gender" name="childGender" cssClass="validateRequired " cssStyle="width: 189px;" listKey="genderId" listValue="genderName" 
																headerKey="" headerValue="Select Gender" list="empGenderList" key="" required="true" />
														<% } else { %>
															<s:select theme="simple" label="Select Gender" name="childGender" cssStyle="width: 189px;" listKey="genderId" listValue="genderName" 
																headerKey="" headerValue="Select Gender" list="empGenderList" key="" />
														<% } %>		
														</td></tr>
														<tr><td class="txtlabel alignRight">Marital Status:<%=(String)request.getAttribute("empChildMaritalStatusValidAsterix") %></td>
														<td>
														<% if(request.getAttribute("empChildMaritalStatusValidReqOpt") != null && !request.getAttribute("empChildMaritalStatusValidReqOpt").toString().equals("")) { %>
														   <s:select theme="simple" name="childMaritalStatus" cssClass="validateRequired " cssStyle="width: 189px;" listKey="maritalStatusId" 
														   listValue="maritalStatusName" headerKey="0" headerValue="Select Marital Status" list="maritalStatusList" key="" required="true" />
														<% } else { %>
															<s:select theme="simple" name="childMaritalStatus" cssStyle="width: 189px;" listKey="maritalStatusId" listValue="maritalStatusName" 
														   	headerKey="0" headerValue="Select Marital Status" list="maritalStatusList" key="" />
														<% } %>   	
														</td></tr>
												    	<tr><td class="txtlabel alignRight" colspan="2"><a href="javascript:void(0)" onclick="addChildren()" class="add-font" style="float:right">&nbsp;</a></td></tr>
												       </table>
													</div>
													<%}%>
													</div>
													<div class="clr">
														<div style="float:right;">
															<table class="table">
															<tr><td colspan="2" align="center">
															<s:if test="mode==null">
																<s:submit cssClass="btn btn-primary next-step" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
															</s:if>
															<s:else>
																<s:hidden name="mode" />
																<s:submit cssClass="btn btn-primary" value="Update Information" align="center" />
															</s:else>
															</td></tr>
															</table>
														</div>
												</div> 
											</s:form> 
										</div>
			                    </div>
			                    </s:if>
			                    <s:if test="step==3 || mode=='report'">
			                    <div class="tab-pane active" role="tabpanel" id="pe">
										<div>	<!-- Previous Employment -->
										<s:form theme="simple" action="AddEmployee" id="frmPrevEmployment" method="POST" cssClass="formcss" enctype="multipart/form-data">
										<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
											<s:hidden name="empId" />
											<s:hidden name="step" />
											<table border="0" class="table">
												<tr><td class=" tdLabelheadingBg " colspan="2"><span style="color:#68AC3B; font-size:22px">Step 3: </span><span style="font-size: 18px;font-weight: 600;">Enter Employees Previous Employment </span></td></tr>
											</table>
												 <%	if(alPrevEmployment!=null && alPrevEmployment.size()!=0) {
														for(int i=0; i<alPrevEmployment.size(); i++) {
														List<String> innerList=alPrevEmployment.get(i);
														%>
													<div id="col_prev_employer_<%=i%>" style="float: left;">
										             <table class="table form-table">
													 	<tr>
													 	<td class="txtlabel alignRight">Company Name:<%=(String)request.getAttribute("empPrevCompNameValidAsterix") %></td>
													 	<td><input type="text" name="prevCompanyName" id="prevCompanyName<%=i %>" class="<%=(String)request.getAttribute("empPrevCompNameValidReqOpt") %> " name="prevCompanyLocation" value="<%=innerList.get(1)%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Location:<%=(String)request.getAttribute("empPrevCompLocationValidAsterix") %></td>
														<td><input type="text" name="prevCompanyLocation" id="prevCompanyLocation<%=i %>" class="<%=(String)request.getAttribute("empPrevCompLocationValidReqOpt") %> " value="<%=innerList.get(2)%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">City:<%=(String)request.getAttribute("empPrevCompCityValidAsterix") %></td>
														<td><input type="text" name="prevCompanyCity" id="prevCompanyCity<%=i %>" class="<%=(String)request.getAttribute("empPrevCompCityValidReqOpt") %> " value="<%=innerList.get(3)%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Country:<%=(String)request.getAttribute("empPrevCompCountryValidAsterix") %></td>
														<td><select id="prevCompanyCountry<%=i%>" class="<%=(String)request.getAttribute("empPrevCompCountryValidReqOpt") %> " name="prevCompanyCountry" 
																onchange="getContentAcs('prevEmpmentstateTD<%=i%>','GetStatesCountrywise.action?country='+this.value);"> 
															<option value="">Select Country</option> <%=innerList.get(5)%> </select>
														</td></tr>
														<tr>
														<td class="txtlabel alignRight">State:<%=(String)request.getAttribute("empPrevCompStateValidAsterix") %></td>
														<td id="prevEmpmentstateTD<%=i%>"> <select id="prevCompanyState<%=i %>" class="<%=(String)request.getAttribute("empPrevCompStateValidReqOpt") %> " name="prevCompanyState"> 
															<option value="">Select State</option> <%=innerList.get(4)%> </select>
														</td></tr> 
														<tr>
														<td class="txtlabel alignRight">Phone Number:<%=(String)request.getAttribute("empPrevCompContactNoValidAsterix") %></td>
														<td><input type="text" name="prevCompanyContactNo" id="prevCompanyContactNo<%=i %>" class="<%=(String)request.getAttribute("empPrevCompContactNoValidReqOpt") %> " onkeypress="return isOnlyNumberKey(event)" value="<%=innerList.get(6)%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Reporting To:<%=(String)request.getAttribute("empPrevCompReportingToValidAsterix") %></td>
														<td><input type="text" name="prevCompanyReportingTo" id="prevCompanyReportingTo<%=i %>" class="<%=(String)request.getAttribute("empPrevCompReportingToValidReqOpt") %> " value="<%=innerList.get(7)%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Reporting Manager Phone Number:<%=(String)request.getAttribute("empPrevCompReportingToPhNoValidAsterix") %></td>
														<td><input type="text" name="prevCompanyReportManagerPhNo" id="prevCompanyReportManagerPhNo<%=i %>" class="<%=(String)request.getAttribute("empPrevCompReportingToPhNoValidReqOpt") %> " onkeypress="return isOnlyNumberKey(event)" value="<%=innerList.get(8)%>"></input></td></tr>
														<tr>
														<td class="txtlabel alignRight">HR Manager:<%=(String)request.getAttribute("empPrevCompHRManagerValidAsterix") %></td>
														<td><input type="text" name="prevCompanyHRManager" id="prevCompanyHRManager<%=i %>" class="<%=(String)request.getAttribute("empPrevCompHRManagerValidReqOpt") %> " value="<%=innerList.get(9)%>"></input></td></tr>
														<tr>
														<td class="txtlabel alignRight">HR Manager Phone Number:<%=(String)request.getAttribute("empPrevCompHRManagerPhNoValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyHRManagerPhNo" id="prevCompanyHRManagerPhNo<%=i %>" class="<%=(String)request.getAttribute("empPrevCompHRManagerPhNoValidReqOpt") %> " onkeypress="return isOnlyNumberKey(event)" value="<%=innerList.get(10)%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">From:<%=(String)request.getAttribute("empPrevCompFromDateValidAsterix") %></td>
														<td><input type="text" name="prevCompanyFromDate" id="prevCompanyFromDate<%=i %>" class="<%=(String)request.getAttribute("empPrevCompFromDateValidReqOpt") %> " value="<%=innerList.get(11)%>" onchange="setcompanyTodd(this.value)"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">To:<%=(String)request.getAttribute("empPrevCompFromDateValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyToDate" id="prevCompanyToDate<%=i %>" class="<%=(String)request.getAttribute("empPrevCompFromDateValidReqOpt") %> " value="<%=innerList.get(12)%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Designation:<%=(String)request.getAttribute("empPrevCompDesignationValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyDesination" id="prevCompanyDesination<%=i %>" class="<%=(String)request.getAttribute("empPrevCompDesignationValidReqOpt") %> " value="<%=innerList.get(13)%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Responsibility:<%=(String)request.getAttribute("empPrevCompResponsibilityValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyResponsibilities" id="prevCompanyResponsibilities<%=i %>" class="<%=(String)request.getAttribute("empPrevCompResponsibilityValidReqOpt") %> " value="<%=innerList.get(14)%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Skills:<%=(String)request.getAttribute("empPrevCompSkillsValidAsterix") %></td>
														<td><input type="text" name="prevCompanySkills" id="prevCompanySkills<%=i %>" class="<%=(String)request.getAttribute("empPrevCompSkillsValidReqOpt") %> " value="<%=innerList.get(15)%>"></input></td></tr>
												<!-- ===start parvez date: 08-08-2022=== -->
														<tr>
														<td class="txtlabel alignRight">ESIC No.:<%=(String)request.getAttribute("empPrevCompSkillsValidAsterix") %></td>
														<td><input type="text" name="prevCompanyESICNo" id="prevCompanyESICNo<%=i %>" class="<%=(String)request.getAttribute("empPrevCompESICNoValidReqOpt") %> " value="<%=innerList.get(17)%>"></input></td></tr>
														<tr>
														<td class="txtlabel alignRight">UAN No.:<%=(String)request.getAttribute("empPrevCompUANNoValidAsterix") %></td>
														<td><input type="text" name="prevCompanyUANNo" id="prevCompanyUANNo<%=i %>" class="<%=(String)request.getAttribute("empPrevCompUANNoValidReqOpt") %> " value="<%=innerList.get(16)%>"></input></td></tr>
														
												<!-- ===end parvez date: 08-08-2022=== -->		 
														<tr>
															<td></td>
															<td>
															<%if(i > 0) { %>
																<a href="javascript:void(0)" onclick="removePrevEmployment(this.id)" id="<%=i %>" class="remove-font" >&nbsp;</a>
														    <%} %>
															<a href="javascript:void(0)" style="float: right;" onclick="addPrevEmployment()" class="add-font">&nbsp;</a>
															</td>
														</tr>
														<script>
															$(function(){
																$("#prevCompanyFromDate<%=i %>").datepicker({
														            format: 'dd/mm/yyyy',
														            autoclose: true
														        }).on('changeDate', function (selected) {
														            var minDate = new Date(selected.date.valueOf());
														            $("#prevCompanyToDate<%=i %>").datepicker('setStartDate', minDate);
														        });
														        
														        $("#prevCompanyToDate<%=i %>").datepicker({
														        	format: 'dd/mm/yyyy',
														        	autoclose: true
														        }).on('changeDate', function (selected) {
														            var minDate = new Date(selected.date.valueOf());
														            $("#prevCompanyFromDate<%=i %>").datepicker('setEndDate', minDate);
														        });
															});
														</script>
										              </table> 
													</div>
													<% }	
													 } else { %>
													<div id="col_prev_employer" style="float: left;" >
										              <table class="table form-table">
										              <tr>
													 	<td class="txtlabel alignRight">Company Name:<%=(String)request.getAttribute("empPrevCompNameValidAsterix") %></td>
													 	<td><input type="text" name="prevCompanyName" id="prevCompanyName0" class="<%=(String)request.getAttribute("empPrevCompNameValidReqOpt") %> " name="prevCompanyLocation" /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Location:<%=(String)request.getAttribute("empPrevCompLocationValidAsterix") %></td>
														<td><input type="text" name="prevCompanyLocation" id="prevCompanyLocation0" class="<%=(String)request.getAttribute("empPrevCompLocationValidReqOpt") %> " /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">City:<%=(String)request.getAttribute("empPrevCompCityValidAsterix") %></td>
														<td><input type="text" name="prevCompanyCity" id="prevCompanyCity0" class="<%=(String)request.getAttribute("empPrevCompCityValidReqOpt") %> " /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Country:<%=(String)request.getAttribute("empPrevCompCountryValidAsterix") %></td>
														<td><select id="prevCompanyCountry0" class="<%=(String)request.getAttribute("empPrevCompCountryValidReqOpt") %> " name="prevCompanyCountry" 
																onchange="getContentAcs('prevEmpmentstateTD0','GetStatesCountrywise.action?country='+this.value);"> 
															<option value="">Select Country</option> <%=countryOptions %> </select>
														</td></tr>
														<tr>
														<td class="txtlabel alignRight">State:<%=(String)request.getAttribute("empPrevCompStateValidAsterix") %></td>
														<td id="prevEmpmentstateTD0"> <select id="prevCompanyState0" class="<%=(String)request.getAttribute("empPrevCompStateValidReqOpt") %> " name="prevCompanyState"> 
															<option value="">Select State</option><%=statesOptions %></select>
														</td></tr> 
														<tr>
														<td class="txtlabel alignRight">Phone Number:<%=(String)request.getAttribute("empPrevCompContactNoValidAsterix") %></td>
														<td><input type="text" name="prevCompanyContactNo" id="prevCompanyContactNo0" onkeypress="return isOnlyNumberKey(event)" class="<%=(String)request.getAttribute("empPrevCompContactNoValidReqOpt") %> " /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Reporting To:<%=(String)request.getAttribute("empPrevCompReportingToValidAsterix") %></td>
														<td><input type="text" name="prevCompanyReportingTo" id="prevCompanyReportingTo0" class="<%=(String)request.getAttribute("empPrevCompReportingToValidReqOpt") %> " /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Reporting Manager Phone Number:<%=(String)request.getAttribute("empPrevCompReportingToPhNoValidAsterix") %></td>
														<td><input type="text" name="prevCompanyReportManagerPhNo" onkeypress="return isOnlyNumberKey(event)" id="prevCompanyReportManagerPhNo0" class="<%=(String)request.getAttribute("empPrevCompReportingToPhNoValidReqOpt") %> " /></td></tr>
														<tr>
														<td class="txtlabel alignRight">HR Manager:<%=(String)request.getAttribute("empPrevCompHRManagerValidAsterix") %></td>
														<td><input type="text" name="prevCompanyHRManager" id="prevCompanyHRManager0" class="<%=(String)request.getAttribute("empPrevCompHRManagerValidReqOpt") %> " /></td></tr>
														<tr>
														<td class="txtlabel alignRight">HR Manager Phone Number:<%=(String)request.getAttribute("empPrevCompHRManagerPhNoValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyHRManagerPhNo" onkeypress="return isOnlyNumberKey(event)" id="prevCompanyHRManagerPhNo0" class="<%=(String)request.getAttribute("empPrevCompHRManagerPhNoValidReqOpt") %> " /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">From:<%=(String)request.getAttribute("empPrevCompFromDateValidAsterix") %></td>
														<td><input type="text" name="prevCompanyFromDate" id="prevCompanyFromDate0" class="<%=(String)request.getAttribute("empPrevCompFromDateValidReqOpt") %> " onchange="setcompanyTodd(this.value)" /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">To:<%=(String)request.getAttribute("empPrevCompFromDateValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyToDate" id="prevCompanyToDate0" class="<%=(String)request.getAttribute("empPrevCompFromDateValidReqOpt") %> " /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Designation:<%=(String)request.getAttribute("empPrevCompDesignationValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyDesination" id="prevCompanyDesination0" class="<%=(String)request.getAttribute("empPrevCompDesignationValidReqOpt") %> " /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Responsibility:<%=(String)request.getAttribute("empPrevCompResponsibilityValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyResponsibilities" id="prevCompanyResponsibilities0" class="<%=(String)request.getAttribute("empPrevCompResponsibilityValidReqOpt") %> " /></td></tr> 
														<tr>
														<td class="txtlabel alignRight">Skills:<%=(String)request.getAttribute("empPrevCompSkillsValidAsterix") %></td>
														<td> <input type="text" name="prevCompanySkills" id="prevCompanySkills0" class="<%=(String)request.getAttribute("empPrevCompSkillsValidReqOpt") %> "/></td></tr>
												<!-- ===start parvez date: 08-08-2022=== -->		
														<tr>
														<td class="txtlabel alignRight">ESIC No.:<%=(String)request.getAttribute("empPrevCompESICNoValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyESICNo" id="prevCompanyESICNo0" class="<%=(String)request.getAttribute("empPrevCompESICNoValidReqOpt") %> "/></td></tr>
														<tr>
														<td class="txtlabel alignRight">UAN No.:<%=(String)request.getAttribute("empPrevCompUANNoValidAsterix") %></td>
														<td> <input type="text" name="prevCompanyUANNo" id="prevCompanyUANNo0" class="<%=(String)request.getAttribute("empPrevCompUANNoValidReqOpt") %> "/></td></tr>
														
												<!-- ===end parvez date: 08-08-2022=== -->		
														<tr><td class="txtlabel alignRight" colspan="2"> <a href="javascript:void(0)" style="float: right;" onclick="addPrevEmployment()" class="add-font">&nbsp;</a></td></tr> 
														<script>
															$(function(){
																$("#prevCompanyFromDate0").datepicker({
														            format: 'dd/mm/yyyy',
														            autoclose: true
														        }).on('changeDate', function (selected) {
														            var minDate = new Date(selected.date.valueOf());
														            $('#prevCompanyToDate0').datepicker('setStartDate', minDate);
														        });
														        $("#prevCompanyToDate0").datepicker({
														        	format: 'dd/mm/yyyy',
														        	autoclose: true
														        }).on('changeDate', function (selected) {
														                var minDate = new Date(selected.date.valueOf());
														                $("#prevCompanyFromDate0").datepicker('setEndDate', minDate);
														        });
															});
														</script>
										              </table>   
													</div>
													<% } %>
												</div>
													<span class="tdLabelheadingBg" style="font-size:16px"> Do you wish to enter previous company TDS details? 
													<%String tdsStatus = hmPrevEmpTds.get("PREV_EMP_TDS_DETAILS_ENABLE");
														if(tdsStatus!=null && uF.parseToBoolean(tdsStatus)) {%>
														<input type="checkbox" name="isTdsDetails" id="isTdsDetails" <%if(uF.parseToInt(hmPrevEmpTds.get("PREV_EARN_DEDUCT_ID"))>0) { %> checked <% } %> onchange="getEnableTds(this)" >
												    <% } else { %>
														<input type="checkbox" name="isTdsDetails" id="isTdsDetails" <%if(uF.parseToInt(hmPrevEmpTds.get("PREV_EARN_DEDUCT_ID"))>0){ %> checked <% } %>
														 onchange="if(this.checked==true){document.getElementById('prevTdsDiv').style.display='block';}else{document.getElementById('prevTdsDiv').style.display='none';}" >
												    <%} %>
													</span>
												<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto; display: <%if(uF.parseToInt(hmPrevEmpTds.get("PREV_EARN_DEDUCT_ID"))>0){ %>block<%}else{ %>none<%} %>;" id="prevTdsDiv">
													<div style="float: left;" >
														<input type="hidden" name="prevEarnDeductId" value="<%=uF.showData(hmPrevEmpTds.get("PREV_EARN_DEDUCT_ID"),"")%>">
										              <table class="table form-table">
													 	<tr>
													 	<td class="txtlabel alignRight"> Financial Year:<%=(String)request.getAttribute("empPrevCompTdsFinancialYearValidAsterix") %></td>
													 	<td>
													 	<% if(request.getAttribute("empPrevCompTdsFinancialYearValidReqOpt") != null && !request.getAttribute("empPrevCompTdsFinancialYearValidReqOpt").toString().equals("")) { %>
													 	<s:select name="prevEmpFYear" listKey="financialYearId" listValue="financialYearName" headerKey="" 
													 		headerValue="Select Financial Year" list="financialYearList" key="" cssClass="validateRequired " cssStyle="width: 190px;"/>
													 	<% } else { %>
													 	<s:select name="prevEmpFYear" listKey="financialYearId" listValue="financialYearName" headerKey="" 
													 		headerValue="Select Financial Year" list="financialYearList" key="" cssStyle="width: 190px;" cssClass=" "/>	
													 	<% } %>
													 	</td></tr> 
														<tr>
														<td class="txtlabel alignRight"> Gross Amount:<%=(String)request.getAttribute("empPrevCompGrossAmtValidAsterix") %></td>
														<td><input type="text" name="prevTotalEarning" id="prevTotalEarning" class="<%=(String)request.getAttribute("empPrevCompGrossAmtValidReqOpt") %> " onkeypress="return isNumberKey(event)" value="<%=uF.showData(hmPrevEmpTds.get("PREV_TOTAL_EARN"),"")%>"></input></td></tr> 
														<tr>
														<td class="txtlabel alignRight"> TDS Amount:<%=(String)request.getAttribute("empPrevCompTdsAmountValidAsterix") %></td>
														<td><input type="text" name="prevTotalDeduction" id="prevTotalDeduction" class="<%=(String)request.getAttribute("empPrevCompTdsAmountValidReqOpt") %> " onkeypress="return isNumberKey(event)" value="<%=uF.showData(hmPrevEmpTds.get("PREV_TOTALDEDUCT"),"")%>"></input></td></tr>
												<!-- ===start parvez date: 31-03-2022=== -->
														<tr>
														<td class="txtlabel alignRight"> PAN Number:<%=(String)request.getAttribute("empPrevCompPanNumberValidAsterix") %></td>
														<td><input type="text" name="prevPANNumber" id="prevTotalDeduction" class="<%=(String)request.getAttribute("empPrevCompPanNumberValidReqOpt") %> " value="<%=uF.showData(hmPrevEmpTds.get("PREV_PAN_NUMBER"),"")%>"></input></td></tr>
														<tr>
														<td class="txtlabel alignRight"> TAN Number:<%=(String)request.getAttribute("empPrevCompTanNumberValidAsterix") %></td>
														<td><input type="text" name="prevTANNumber" id="prevTotalDeduction" class="<%=(String)request.getAttribute("empPrevCompTanNumberValidReqOpt") %> " value="<%=uF.showData(hmPrevEmpTds.get("PREV_TAN_NUMBER"),"")%>"></input></td></tr>
												<!-- ===end parvez date: 31-03-2022=== -->		 
														<tr>
														<td class="txtlabel alignRight"> Please upload relevant document (form 16):<%=(String)request.getAttribute("empPrevCompTdsForm16ValidAsterix") %></td>
														<td> <input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="prevFileEarnDeduct" id="prevFileEarnDeduct" class="<%=(String)request.getAttribute("empPrevCompTdsForm16ValidReqOpt") %>"></input></td></tr>
														<% if(hmPrevEmpTds.get("PREV_DOCUMENT") !=null && !hmPrevEmpTds.get("PREV_DOCUMENT").equals("")) { %>
															<tr><td class="txtlabel alignRight">&nbsp;</td><td><%=uF.showData(hmPrevEmpTds.get("PREV_DOCUMENT"),"")%></td></tr>
														<% } %>
														<tr><td class="txtlabel" colspan="2" style="font-weight: normal;"><i>This data is purely used for calculation of your TDS with the existing organization only.</i></td></tr> 
										              </table>   
												</div>
											</div> 
											<div class="clr"></div>
														<div style="float:right;">
														<table>
														<tr><td colspan="2" align="center">
														<s:if test="mode==null">
															<s:submit cssClass="btn btn-primary next-step" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
														</s:if>
														<s:else>
															<s:hidden name="mode" />
															<s:submit cssClass="btn btn-primary" value="Update Information" align="center" />
														</s:else>
														</td></tr>
														</table>
													</div>
										</s:form>  
										</div>
			                    </div>
			                    </s:if>
			                    <s:if test="step==4 || mode=='report'">
			                    <div class="tab-pane active" role="tabpanel" id="rf">
									<div>
									<s:form theme="simple" action="AddEmployee" id="frmReferences" method="POST" cssClass="formcss" enctype="multipart/form-data">
									<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_emp_references">		
											<s:hidden name="empId" />
											<s:hidden name="step" />
									<table border="0" class="formcss">
										<tr><td class="tdLabelheadingBg" colspan="2"><span style="color:#68AC3B; font-size:22px">Step 4: </span><span style="font-size: 18px;font-weight: 600;">Enter Employee References</span></td></tr>
									</table>
											<% 
											String empPrevEmployment = (String) request.getAttribute("empPrevEmployment");
												if(alEmpReferences!=null && alEmpReferences.size()!=0) {	
												for(int i=0; i<alEmpReferences.size(); i++) {
													List<String> innerList = alEmpReferences.get(i);
											%>
										<div id="emp_references_div_<%=i %>" style="float: left;">
										<table border="0" class="table form-table">
											<tr>
											<td class="txtlabel alignRight">Name:<%=(String)request.getAttribute("empRefNameValidAsterix") %></td>
											<td><input type="text" name="refName" id="refName<%=i %>" class="<%=(String)request.getAttribute("empRefNameValidReqOpt") %> " value="<%=uF.showData(innerList.get(1), "") %>"/></td></tr>
											<tr><td class="txtlabel alignRight">Company:<%=(String)request.getAttribute("empRefCompanyValidAsterix") %></td>
												<td><select name="refCompany" id="refCompany<%=i %>" class="<%=(String)request.getAttribute("empRefCompanyValidReqOpt") %> " onchange="showOtherCompTextField(this.value, '<%=i %>');">
													<option value="">Select Company</option><%=innerList.get(2) %></select>
												</td>
											</tr>
											<tr id="refCompOtherTR<%=i %>" style="display:<%if(innerList.get(3) != null && !innerList.get(3).equals("")) { %>table-row; <% } else { %> none; <% } %>;"><td></td>
												<td>
													<input type="text" name="refCompanyOther" id="refCompanyOther<%=i %>" value="<%=uF.showData(innerList.get(3), "") %>"/>
												</td>
											</tr>
											<tr><td class="txtlabel alignRight">Designation:<%=(String)request.getAttribute("empRefDesigValidAsterix") %></td>
											<td><input type="text" name="refDesignation" id="refDesignation<%=i %>" class="<%=(String)request.getAttribute("empRefDesigValidReqOpt") %> " value="<%=uF.showData(innerList.get(4), "") %>"/></td></tr>
											<tr><td class="txtlabel alignRight">Contact No:<%=(String)request.getAttribute("empRefContactNoValidAsterix") %></td>
											<td><input type="text" name="refContactNo" onkeypress="return isOnlyNumberKey(event)" name="refContactNo<%=i %>" class="<%=(String)request.getAttribute("empRefContactNoValidReqOpt") %> " value="<%=uF.showData(innerList.get(5), "") %>"/></td></tr>
											<tr><td class="txtlabel alignRight">Email Id:<%=(String)request.getAttribute("empRefEmailIdValidAsterix") %></td>
											<td><input type="text" name="refEmail" id="refEmail<%=i %>" class="<%=(String)request.getAttribute("empRefEmailIdValidReqOpt") %> " value="<%=uF.showData(innerList.get(6), "") %>"/></td></tr>
											<tr><td class="txtlabel alignRight" colspan="2"> 
											<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_DEFAULT_TWO_REFERENCE_IN_ADD_EMPLOYEE)) && i> 1) { %>
												<a href="javascript:void(0)" style="float: right;" onclick="removeEmpReferences(this.id)" id="<%=i%>" class="remove-font" ></a>
											<% } else if(i> 0) { %>
												<a href="javascript:void(0)" style="float: right;" onclick="removeEmpReferences(this.id)" id="<%=i%>" class="remove-font" ></a>
											<% } %>
											<a href="javascript:void(0)" style="float: right;" onclick="addEmpReferences();" class="add-font">&nbsp;</a></td></tr>
										</table>
										</div>
											<% } 
											} else { %>
										
										<div id="emp_references_div" style="float: left;">
										<table border="0" class="table form-table">
											<tr><td class="txtlabel alignRight">Name:<%=(String)request.getAttribute("empRefNameValidAsterix") %></td>
											<td><input type="text" name="refName" id="refName0" class="<%=(String)request.getAttribute("empRefNameValidReqOpt") %> "/></td></tr>
											<tr><td class="txtlabel alignRight">Company:<%=(String)request.getAttribute("empRefCompanyValidAsterix") %></td>
												<td><select name="refCompany" id="refCompany0" class="<%=(String)request.getAttribute("empRefCompanyValidReqOpt") %> " onchange="showOtherCompTextField(this.value, '0');">
													<option value="">Select Company</option><%=empPrevEmployment %></select>
												</td>
											</tr>
											<tr id="refCompOtherTR0" style="display:none;"><td></td>
												<td><input type="text" name="refCompanyOther" id="refCompanyOther0"/></td>
											</tr>
											<tr><td class="txtlabel alignRight">Designation:<%=(String)request.getAttribute("empRefDesigValidAsterix") %></td>
											<td><input type="text" name="refDesignation" id="refDesignation0" class="<%=(String)request.getAttribute("empRefDesigValidReqOpt") %> "/></td></tr>
											<tr><td class="txtlabel alignRight">Contact No:<%=(String)request.getAttribute("empRefContactNoValidAsterix") %></td>
											<td><input type="text" name="refContactNo" id="refContactNo0" onkeypress="return isOnlyNumberKey(event)" class="<%=(String)request.getAttribute("empRefContactNoValidReqOpt") %> "/></td></tr>
											<tr><td class="txtlabel alignRight">Email Id:<%=(String)request.getAttribute("empRefEmailIdValidAsterix") %></td>
											<td><input type="text" name="refEmail" id="refEmail0" class="<%=(String)request.getAttribute("empRefEmailIdValidReqOpt") %> "/></td></tr>
											<tr><td class="txtlabel alignRight" colspan="2"> <a href="javascript:void(0)" style="float: right;" onclick="addEmpReferences();" class="add-font">&nbsp;</a></td></tr>
										</table>
										</div>
										<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_DEFAULT_TWO_REFERENCE_IN_ADD_EMPLOYEE))) { %>
										<script type="text/javascript">
											$(function() {
												//alert("addEmpReferences");
												addEmpReferences();
											});
										</script>
										<% } %>
										<% } %>
									</div>	
										<div class="clr"></div>
									<div style="float:right;">
										<table>
											<tr><td colspan="2" align="center">
											<s:if test="mode==null">
												<s:submit cssClass="btn btn-primary next-step" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" /> 
											</s:if>
											<s:else>
												<s:hidden name="mode" /> <s:submit cssClass="btn btn-primary" value="Update Information" align="center" />
											</s:else>
											 </td></tr>
										</table>
									</div>
									</s:form>
									</div>
			                    </div>
			                    </s:if>
			                    <s:if test="step==6 || mode=='report'">
			                    <div class="tab-pane active" role="tabpanel" id="mi">
										<div> <!-- Medical Information -->
										<s:form theme="simple" action="AddEmployee" id="frmMedicalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkFile();">
										<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
											<s:hidden name="empId" />
											<s:hidden name="step" />
											<table border="0" class="formcss">
											    <tr><td  class=" tdLabelheadingBg " colspan="2"><span style="color:#68AC3B; font-size:22px">Step 6: </span><span style="font-size: 18px;font-weight: 600;">Medical Information</span></td></tr>
											</table>
											<table border="0" class="formcss">
												<tr><td class=" tdLabelheadingBg " colspan="2">Enter Employee's Medical Information </td></tr>
												<tr>
													<td>
											            <table class="table form-table">
														 	<tr>
														 	<% 	List<String> empMedicalDoc1ValidList = hmValidationFields.get("EMP_MEDICAL_DOC1"); 
																validReqOpt = "";
																validAsterix = "";
																if(empMedicalDoc1ValidList != null && uF.parseToBoolean(empMedicalDoc1ValidList.get(0))) {
																	validReqOpt = "validateRequired";
																	validAsterix = "<sup>*</sup>";
																}
															%>
														 	<td class="txtlabel alignRight" style="width: 35%;">Are you now receiving medical attention:<%=validAsterix %></td>
														 		<td nowrap="nowrap"><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue1" onclick="checkRadio(this,'text1');"></s:radio>
														 			<s:hidden name="empMedicalId1" />
														 			<s:hidden name="que1Id" value="1"></s:hidden>
														 			<s:hidden name="que1IdFileStatus" id="que1IdFileStatus" value="0"></s:hidden>
														 		</td>
														 		<s:if test="checkQue1==true">
															 		<td><textarea rows="7" cols="63" id="text1" name="que1Desc" class="<%=validReqOpt %> "><%=uF.showData((String)request.getAttribute("que1Desc"), "") %></textarea></td>
															 		<td><input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="que1DescFile" id="text1File"  class="<%=validReqOpt %>"  onchange="fillFileStatus('que1IdFileStatus')"/></td>
															 		<td>
																 		<% if(request.getAttribute("que1DocName") != null && !((String)request.getAttribute("que1DocName")).equalsIgnoreCase("null") && !((String)request.getAttribute("que1DocName")).equalsIgnoreCase("")) {
																 			String empMedicalId1 = (String)request.getAttribute("empMedicalId1");
															 			%>
																 			<div id="removeDivMedicalDocument_<%=empMedicalId1 %>">
																	 			<%if(docRetriveLocation == null) { %>
																					<a target="blank" style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + (String)request.getAttribute("que1DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
																				<%} else { %>
																					<a target="blank" style="float: left; padding-top: 5px;" href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_MEDICAL+"/"+request.getAttribute("empId")+"/"+ (String)request.getAttribute("que1DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
																				<%} %>
																	 			<%if (struserType != null && (struserType.equalsIgnoreCase(IConstants.ADMIN) || struserType.equalsIgnoreCase(IConstants.HRMANAGER))){ %>
																		  			<a href="javascript:void(0)" style="float: left; padding: 0px 30px 20px 0px;" onclick="deleteMedicalDocuments('<%=empMedicalId1 %>')" class="remove-font" title="click to delete document"></a>
																		  		<%} %>
																		  	</div>	
																 		<% } else { %>
																 		-
																 		<% } %>
															 		</td> 
														 		</s:if>
														 		<s:else>
															 		<td><textarea rows="7" cols="63" id="text1" name="que1Desc" class="<%=validReqOpt %> " disabled="disabled"><%=uF.showData((String)request.getAttribute("que1Desc"), "") %></textarea></td>
															 		<td><input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="que1DescFile" id="text1File" class="<%=validReqOpt %>" disabled="disabled" onchange="fillFileStatus('que1IdFileStatus')"/></td> 
														 		</s:else>
														 	</tr>
															<tr>
															<% 	List<String> empMedicalDoc2ValidList = hmValidationFields.get("EMP_MEDICAL_DOC2"); 
																validReqOpt = "";
																validAsterix = "";
																if(empMedicalDoc2ValidList != null && uF.parseToBoolean(empMedicalDoc2ValidList.get(0))) {
																	validReqOpt = "validateRequired";
																	validAsterix = "<sup>*</sup>";
																}
															%>
															<td class="txtlabel alignRight">Have you had any form of serious illness or operation:<%=validAsterix %></td>
																<td nowrap="nowrap"><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue2" onclick="checkRadio(this,'text2');"></s:radio>
																	<s:hidden name="empMedicalId2" />
																	<s:hidden name="que2Id" value="2"></s:hidden>
																	<s:hidden name="que1IdFileStatus" id="que2IdFileStatus" value="0"></s:hidden>
																</td>
																<s:if test="checkQue2==true">
															 		<td><textarea rows="7" cols="63" id="text2" name="que2Desc" class="<%=validReqOpt %> "><%=uF.showData((String)request.getAttribute("que2Desc"), "") %></textarea></td>
															 		<td><input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="que1DescFile" id="text2File" class="<%=validReqOpt %>" onchange="fillFileStatus('que2IdFileStatus')"/></td>
															 		<td>
															 		<% if(request.getAttribute("que2DocName") != null && !((String)request.getAttribute("que2DocName")).equalsIgnoreCase("null") && !((String)request.getAttribute("que2DocName")).equalsIgnoreCase("")) { 
															 			String empMedicalId2 = (String)request.getAttribute("empMedicalId2");
															 		%>
																 		<div id="removeDivMedicalDocument_<%=empMedicalId2 %>">
																 			<%if(docRetriveLocation == null) { %>
																				<a target="blank" style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + (String)request.getAttribute("que2DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
																			<%} else { %>
																				<a target="blank" style="float: left; padding-top: 5px;" href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_MEDICAL+"/"+request.getAttribute("empId")+"/"+ (String)request.getAttribute("que2DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
																			<%} %>
																 			<%if (struserType != null && (struserType.equalsIgnoreCase(IConstants.ADMIN) || struserType.equalsIgnoreCase(IConstants.HRMANAGER))){ %>
																	  			<a href="javascript:void(0)" style="float: left; padding: 0px 30px 20px 0px;" onclick="deleteMedicalDocuments('<%=empMedicalId2 %>')" class="remove-font" title="click to delete document"></a>
																	  		<% } %>
																	  	</div>	
															 		<% } else { %>
															 		-
															 		<% } %>
															 		</td> 
														 		</s:if>
														 		<s:else>
															 		<td><textarea rows="7" cols="63" id="text2" name="que2Desc" class="<%=validReqOpt %> " disabled="disabled"><%=uF.showData((String)request.getAttribute("que2Desc"), "") %></textarea></td>
															 		<td><input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="que1DescFile" id="text2File" class="<%=validReqOpt %>" disabled="disabled" onchange="fillFileStatus('que2IdFileStatus')"/></td> 
														 		</s:else> 
															</tr>
															<tr>
															<% 	List<String> empMedicalDoc3ValidList = hmValidationFields.get("EMP_MEDICAL_DOC3"); 
																validReqOpt = "";
																validAsterix = "";
																if(empMedicalDoc3ValidList != null && uF.parseToBoolean(empMedicalDoc3ValidList.get(0))) {
																	validReqOpt = "validateRequired";
																	validAsterix = "<sup>*</sup>";
																}
															%>
															<td class="txtlabel alignRight">Have you had any illness in the last two years? YES/NO If YES, 
																	please give the details about the same and any absences from work:<%=validAsterix %></td>
																<td nowrap="nowrap"><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue3" onclick="checkRadio(this,'text3');"></s:radio>
																	<s:hidden name="empMedicalId3" />
																	<s:hidden name="que3Id" value="3"></s:hidden>
																	<s:hidden name="que1IdFileStatus" id="que3IdFileStatus" value="0"></s:hidden>
																</td>
																<s:if test="checkQue3==true">
															 		<td><textarea rows="7" cols="63" id="text3" name="que3Desc" class="<%=validReqOpt %> "><%=uF.showData((String)request.getAttribute("que3Desc"), "") %></textarea></td>
															 		<td> <input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="que1DescFile" id="text3File" class="<%=validReqOpt %>" disabled="disabled" onchange="fillFileStatus('que3IdFileStatus')" /></td>
															 		<td>
																 		<% if(request.getAttribute("que3DocName") != null && !((String)request.getAttribute("que3DocName")).equalsIgnoreCase("null") && !((String)request.getAttribute("que3DocName")).equalsIgnoreCase("")) { 
																 			String empMedicalId3 = (String)request.getAttribute("empMedicalId3");
																 		%>
																	 		<div id="removeDivMedicalDocument_<%=empMedicalId3 %>">
																	 			<%if(docRetriveLocation == null) { %>
																					<a target="blank" style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + (String)request.getAttribute("que3DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
																				<%} else { %>
																					<a target="blank" style="float: left; padding-top: 5px;" href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_MEDICAL+"/"+request.getAttribute("empId")+"/"+ (String)request.getAttribute("que3DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
																				<%} %>
																	 			<%if (struserType.equalsIgnoreCase(IConstants.ADMIN) || struserType.equalsIgnoreCase(IConstants.HRMANAGER)){ %>
																		  			<a href="javascript:void(0)" style="float: left; padding: 0px 30px 20px 0px;" onclick="deleteMedicalDocuments('<%=empMedicalId3 %>')" class="remove-font" title="click to delete document"></a>
																		  		<%} %>
																		  	</div>	
																 		<% } else { %> -<% } %>
															 		</td> 
														 		</s:if>
														 		<s:else>
															 		<td><textarea rows="7" cols="63" id="text3" name="que3Desc" class="<%=validReqOpt %> " disabled="disabled"><%=uF.showData((String)request.getAttribute("que3Desc"), "") %></textarea></td>
															 		<td><input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="que1DescFile" id="text3File" class="<%=validReqOpt %>" disabled="disabled" onchange="fillFileStatus('que3IdFileStatus')" /></td> 
														 		</s:else>
															</tr>
														</table>
													</td> 
												</tr>
											</table>
											<div class="clr"></div>
											<div style="float:right;">
												<table>
												<tr><td colspan="2" align="center">
												<s:if test="mode==null">
													<s:submit cssClass="btn btn-primary next-step" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
												</s:if>
												<s:else>
													<s:hidden name="mode" />
													<s:submit cssClass="btn btn-primary" value="Update Information" align="center" />
												</s:else>
												</td></tr>
												</table>
											</div>
										</div>
										</s:form>	     
										</div>
			                    </div>
			                    </s:if>
			                    <s:if test="step==7 || mode=='report'">
			                    <div class="tab-pane active" role="tabpanel" id="dc">
										<div>	
										<form action="AddEmployee.action" id="frmDocumentation" method="POST" class="formcss" enctype="multipart/form-data">
										<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" >
											<s:hidden name="empId" />
											<s:hidden name="step" />
												<table border="0" class="table">
											    <tr><td  class="tdLabelheadingBg " colspan="2"><span style="color:#68AC3B; font-size:22px">Step 7: </span><span style="font-size: 18px;font-weight: 600;">Attach Documents</span></td></tr>
											    </table>
														<% 	
															if(alDocuments!=null && alDocuments.size()!=0) {
															String empId = (String)((ArrayList)alDocuments.get(0)).get(3);
															boolean strFlag = false;	//added by parvez date: 29-10-2022
														%>
													 <table id="supportDocTable" style="width:70%" id="row_document_table" class="table form-table">
										               <tr>
										                <td class="txtlabel alignRight" style="text-align: -moz-center">
										                	<input type="hidden" name="documentCnt" id="documentCnt" value="<%=alDocuments.size()-1 %>" />
										                	
										                	<label><b>Document Name</b></label>
										                </td>
										                <td class="txtlabel alighcenter" style="text-align: -moz-center" ><label><b>Attached Document</b></label></td>
										                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Added By</b></label></td>
										                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Entry Date</b></label></td>
										           	   </tr>  
														<% 	for(int i=0; i<alDocuments.size(); i++) { %>
															<% if(((String)((ArrayList)alDocuments.get(i)).get(2)).equalsIgnoreCase(IConstants.DOCUMENT_COMPANY_PROFILE)){
																	strFlag = true;
																}
															%>
														  	<tr>
														  		<td class="txtlabel alignRight">
															  		<input type="hidden" name="idDocType" value="<%=((ArrayList)alDocuments.get(i)).get(2)%>"></input>
														  			<input type="hidden" name="docId" value="<%=((ArrayList)alDocuments.get(i)).get(0)%>"/> 
														  			<% if(((ArrayList)alDocuments.get(i)).get(2) != null && ((String)((ArrayList)alDocuments.get(i)).get(2)).equalsIgnoreCase(IConstants.DOCUMENT_OTHER)) { %>
														  				<input type="text" name="idDocName" class="validateRequired" style="width: 180px;" value="<%=((ArrayList)alDocuments.get(i)).get(1)%>" ></input>
														  			<% } else { %>
														  				<input type="hidden" name="idDocName" value="<%=((ArrayList)alDocuments.get(i)).get(1)%>" />
														  				<%=((ArrayList)alDocuments.get(i)).get(1)%>:
														  			<% } %>
														  		</td>
														  		<td class="txtlabel alignRight" style="text-align: -moz-center">
														  		     <input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs,.docx" name="idDoc<%=i%>" id="idDoc<%=i%>" class="<%=validReqOpt %>" onchange="fillFileStatus('idDocStatus<%=i%>');"/>
														  		     <input type="hidden" name="idDocStatus" id="idDocStatus<%=i%>" value="0"></input>
														  			<% if(((ArrayList)alDocuments.get(i)).get(4) != null && !((ArrayList)alDocuments.get(i)).get(4).toString().equalsIgnoreCase("null") && !((ArrayList)alDocuments.get(i)).get(4).toString().equals("")) { %>
															  			<%if(docRetriveLocation == null) { %>
																			<a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alDocuments.get(i)).get(4) %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
																		<% } else { %>
																			<a href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+request.getAttribute("empId")+"/"+ ((ArrayList)alDocuments.get(i)).get(4) %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
																		<% } %>
																	<% } %>
																	<% if(((ArrayList)alDocuments.get(i)).get(2).toString().equalsIgnoreCase(IConstants.DOCUMENT_COMPANY_PROFILE)){ %>
																		<a title="Download Sample File" href="import/Exusia Profile.docx" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
																	<% } %>
														  		</td>
														  		<td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(6)%></td>
														  		<td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(5)%></td>
														  		<td>
														  <!-- ===start parvez date: 29-10-2022=== -->		
														  		<%-- <% if(i == alDocuments.size()-1) { %> --%>
														  		<% if(strFlag && i == alDocuments.size()-1) { %>
														  			<a href="javascript:void(0);" onclick="addDocuments('EDIT')" class="add-font"></a>
														  		<% } %>	
														  		</td>
										                 	</tr>
										                 
											                 <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_COMPANY_PROFILE_DOCUMENT)) && (i == alDocuments.size()-1)){ 
											                 		if(!strFlag){
											                 			String docSize = alDocuments.size()+"";
											                 			List<String> empDocCompanyProfileValidList = hmValidationFields.get("EMP_DOC_COMPANY_PROFILE"); 
											            				validReqOpt = "";
											            				validAsterix = "";
											            				if(empDocCompanyProfileValidList != null && uF.parseToBoolean(empDocCompanyProfileValidList.get(0))) {
											            					validReqOpt = "validateRequired";
											            					validAsterix = "<sup>*</sup>";
											            				}
											                 %>
											                 		<tr>
																	  	<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_COMPANY_PROFILE %>:<%=validAsterix %>
																	 		<input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_COMPANY_PROFILE %>"></input>
																	 		<input type="hidden" value="<%=IConstants.DOCUMENT_COMPANY_PROFILE %>" name="idDocName" ></input>
																	 		<input type="hidden" name="idDocStatus" id="idDocStatus<%=docSize %>" value="0"></input>
																 		</td>
																		<td class="txtlabel alignRight" style="text-align: -moz-center">
																			<input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs,.docx" name="idDoc<%=docSize %>" id="idDoc<%=docSize %>" class="<%=validReqOpt %>" onchange="fillFileStatus('idDocStatus<%=docSize %>')"/>
																		<!-- ===start parvez date: 01-11-2022=== -->	
																			<% if(((ArrayList)alDocuments.get(i)).get(2).toString().equalsIgnoreCase(IConstants.DOCUMENT_COMPANY_PROFILE)){ %>
																				<a title="Download Sample File" href="import/Exusia Profile.docx" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
																			<% } %>
																		<!-- ===end parvez date: 01-11-2022=== -->	
																		</td>
												                    	<td></td>
												                    	<td></td>
												                    	<td>
												                    		<a href="javascript:void(0);" onclick="addDocuments('EDIT')" class="add-font"></a>
												                    	</td>
												                 	</tr>
												                 	
												                 	<script>
												                 		$(function(){
												                 			document.getElementById("documentCnt").value = '<%=alDocuments.size() %>';
												                 		});
												                 	</script>
												                 	
											                 	<% } %>
											                 <% } %>
											            <!-- ===end parvez date: 29-10-2022=== -->    
														 <% } %>
														 </table>
														 <% } else { 
															 List<List<String>> alDefaultSupportingDocs = (List<List<String>>) request.getAttribute("alDefaultSupportingDocs");
															 
														 %>
														 <div id="row_document">
										                    <table id="supportDocTable" class="table form-table autoWidth">
											                    <tr>
													                <td class="txtlabel alignRight">
													              <!-- ===start parvez date: 29-10-2022=== -->     	
													                   	<!-- <input type="hidden" name="documentCnt" id="documentCnt" value="2" /> -->
													                   	<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_COMPANY_PROFILE_DOCUMENT))){ %>
														             		<input type="hidden" name="documentCnt" id="documentCnt" value="3" />
														             	<% } else{ %>
														             		<input type="hidden" name="documentCnt" id="documentCnt" value="2" />
														             	<% } %>
														          <!-- ===end parvez date: 29-10-2022=== -->   	
														                <label><b>Document Name</b></label>
													                </td>
													                <td class="txtlabel alignRight"><label><b>Attached Document</b></label></td>
													                <td></td>
													            </tr>
													            
													            <% 	for(int i=0; alDefaultSupportingDocs !=null && i<alDefaultSupportingDocs.size(); i++) { 
													            		List<String> innerList = alDefaultSupportingDocs.get(i);
													            %>
																  <tr>
																  	<td class="txtlabel alignRight"><%=innerList.get(0) %>:<%=innerList.get(2) %>
															 		<input type="hidden" name="idDocType" value="<%=innerList.get(0) %>"></input>
															 		<input type="hidden" value="<%=innerList.get(0) %>" name="idDocName" ></input>
															 		<input type="hidden" name="idDocStatus" id="idDocStatus<%=i %>" value="0"></input>
															 		</td>
																	<td class="txtlabel alignRight">
																		<input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="idDoc<%=i %>" id="idDoc<%=i %>" class="<%=innerList.get(1) %>" onchange="fillFileStatus('idDocStatus<%=i %>')"/>
																	<!-- ===start parvez date: 01-11-2022=== -->	
																		<% if(innerList.get(0).equalsIgnoreCase(IConstants.DOCUMENT_COMPANY_PROFILE)){ %>
																			<a title="Download Sample File" href="import/Exusia Profile.docx" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
																		<% } %>
																	<!-- ===end parvez date: 01-11-2022=== -->	
																	</td>
											                    	<td>
											                    		<% if(i == alDefaultSupportingDocs.size()-1) { %>
																  			<a href="javascript:void(0);" onclick="addDocuments('ADD')" class="add-font"></a>
																  		<% } %>
											                    	</td>
												                 </tr>
																 <% } %>
													    	</table>
													    </div>
														 <% } %>
														 <div class="clr"></div>
														 <div style="float:right;">
														<s:if test="mode==null">
															<% if(struserType != null && (struserType.equalsIgnoreCase(IConstants.EMPLOYEE))) { %>
																<s:submit cssClass="btn btn-primary" value="Submit & Finish" cssStyle="width:200px; float:right;" align="center"/>
															<% } else { %>	
																<!-- <button type="submit" class="btn btn-primary next-step" value="Submit & Proceed"></button> -->
																<s:submit cssClass="btn btn-primary next-step" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
															<% } %>	
														</s:if>
														<s:else>
															<s:hidden name="mode" />
															<s:submit cssClass="btn btn-primary" value="Update Information" />
														</s:else>
													</div>
												</div>				 
											</form>
										</div>
			                    </div>
			                    </s:if>
			                    <s:if test="step==8 || mode=='report'">
			                    <div class="tab-pane active" role="tabpanel" id="oi">
			                        <%if(strEmpType!=null && !strEmpType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
										<script>
										jQuery(document).ready(function() {
											var orgId = document.getElementById("strOrg").value;
											var desigId = document.getElementById("desigIdV").value;
											var empId = document.getElementById("empId").value;
											getContentAcs('designKRADiv', 'GetDesignationKRAs.action?desigId='+ desigId + '&orgId=' + orgId + '&empId=' + empId);
										});
										</script>
											<div>
											<% 	List<String> empLeavesDuringProbationValidList = hmValidationFields.get("EMP_LEAVES_DURING_PROBATION"); 
													String leavesValidReqOpt = "";
													if(empLeavesDuringProbationValidList != null && uF.parseToBoolean(empLeavesDuringProbationValidList.get(0))) {
														leavesValidReqOpt = "validateRequired";
													}
												%>
											<s:form theme="simple" name="frmOfficial" action="AddEmployee" id="frmOfficialInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
												<table border="0" class="table form-table">
												<tr><td>
												</td></tr>
												<tr><td class=" tdLabelheadingBg " colspan="2"><span style="color:#68AC3B; font-size:22px">Step 8: </span><span style="font-size: 18px;font-weight: 600;">Enter Employee Official Information</span></td></tr>
												<s:hidden name="empId" id="empId"/>
												<s:hidden name="step" />
												<input type="hidden" name="userType" id="userType" value="<%=struserType %>"/>
												<input type="hidden" name="sessionUserId" id="sessionUserId" value="<%=sessionUserId %>"/>
												<input type="hidden" name="leavesValidReqOpt" id="leavesValidReqOpt" value="<%=leavesValidReqOpt %>"/>
												<tr><td colspan=2>Official Information:<hr style="background-color: #346897; height: 1px;"></td></tr>
												<tr>
												<% 	List<String> empOrganisationValidList = hmValidationFields.get("EMP_ORGANISATION"); 
													validReqOpt = "";
													validAsterix = "";
													if(empOrganisationValidList != null && uF.parseToBoolean(empOrganisationValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Organisation:<%=validAsterix %></td>
												<td>
												<% if(empOrganisationValidList != null && uF.parseToBoolean(empOrganisationValidList.get(0))) { %>
													<s:select label="Select Organisation" name="orgId" id="strOrg" cssClass="validateRequired " listKey="orgId" listValue="orgName" 
													headerKey="" headerValue="Select Organisation" list="orgList" key="" onchange="getDataFromAjax(this.value);"/>
												<% } else { %>
													<s:select label="Select Organisation" name="orgId" id="strOrg" listKey="orgId" listValue="orgName" 
													headerKey="" headerValue="Select Organisation" list="orgList" key="" onchange="getDataFromAjax(this.value);"/>
												<% } %>		
												</td></tr>	
												<tr>
												<% 	List<String> empLocationValidList = hmValidationFields.get("EMP_LOCATION"); 
													validReqOpt = "";
													validAsterix = "";
													if(empLocationValidList != null && uF.parseToBoolean(empLocationValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Work Location:<%=validAsterix %></td>
												<td id="idOrgId">
												<% if(empLocationValidList != null && uF.parseToBoolean(empLocationValidList.get(0))) { %>	
													<s:select label="Select Work Location" name="wLocation" id="wLocation" cssClass="validateRequired " listKey="wLocationId" 
													listValue="wLocationName" headerKey="" headerValue="Select Location" list="wLocationList" /> <!-- onchange="javascript:show_department();return false;" -->
												<% } else { %>
													<s:select label="Select Work Location" name="wLocation" id="wLocation" listKey="wLocationId" listValue="wLocationName" 
													headerKey="" headerValue="Select Location" list="wLocationList" />
												<% } %>
													<span class="hint">Employee's work location.<span class="hint-pointer">&nbsp;</span></span>
												</td></tr>
												<tr>
												<% 	List<String> empSBUValidList = hmValidationFields.get("EMP_SBU"); 
													validReqOpt = "";
													validAsterix = "";
													if(empSBUValidList != null && uF.parseToBoolean(empSBUValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight" valign="top">SBU:<%=validAsterix %></td>
												<td class="" id="idService">
												<% if(empSBUValidList != null && uF.parseToBoolean(empSBUValidList.get(0))) { %>
													<s:select label="Cost Centre" name="service" listKey="serviceId" cssClass="validateRequired " headerKey="" headerValue="Select SBU"
													listValue="serviceName" list="serviceList" key="" /> <!-- multiple="true" size="3" -->
												<% } else { %>
													<s:select label="Cost Centre" name="service" listKey="serviceId" headerKey="" headerValue="Select SBU"
													listValue="serviceName" list="serviceList" key="" />
												<% } %>	
													<span class="hint">The SBU where the employee is supposed to work. This field will be used while calculating roster.<span class="hint-pointer">&nbsp;</span></span>
												</td></tr>
												<tr>
												<% 	List<String> empDepartmentValidList = hmValidationFields.get("EMP_DEPARTMENT"); 
													validReqOpt = "";
													validAsterix = "";
													if(empDepartmentValidList != null && uF.parseToBoolean(empDepartmentValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Department:<%=validAsterix %></td>
												<td id="idDepartment">
												<% if(empDepartmentValidList != null && uF.parseToBoolean(empDepartmentValidList.get(0))) { %>
													<s:select theme="simple" label="Select Department" name="department" id="department" listKey="deptId" cssClass="validateRequired "
														listValue="deptName" headerKey="" headerValue="Select Department" list="deptList" key="" onchange="showCurrentOrgSelectedDepartment();"/>
												<% } else { %>
													<s:select theme="simple" label="Select Department" name="department" id="department" listKey="deptId" listValue="deptName" 
														headerKey="" headerValue="Select Department" list="deptList" key="" onchange="showCurrentOrgSelectedDepartment();"/>
												<% } %>		
												</td></tr>
												<tr><td colspan=2></td></tr>		
													<tr>
													<% 	List<String> empLevelValidList = hmValidationFields.get("EMP_LEVEL"); 
														validReqOpt = "";
														validAsterix = "";
														if(empLevelValidList != null && uF.parseToBoolean(empLevelValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Level:<%=validAsterix %></td>
													<td id="idLevel">
													<% if(empLevelValidList != null && uF.parseToBoolean(empLevelValidList.get(0))) { %>
													<s:select name="strLevel" list="levelList" listKey="levelId" id="levelIdV" listValue="levelCodeName" cssClass="validateRequired " 
														headerKey="" headerValue="Select Level" onchange="getDesigAndLeave(this.value);"></s:select>		
													<% } else { %>
													<s:select name="strLevel" list="levelList" listKey="levelId" id="levelIdV" listValue="levelCodeName" headerKey=""
														headerValue="Select Level" onchange="getDesigAndLeave(this.value);" ></s:select>
													<% } %>	
													</td></tr>
												<tr><td colspan=2></td></tr>		
													<tr>
													<% 	List<String> empDesignationValidList = hmValidationFields.get("EMP_DESIGNATION"); 
														validReqOpt = "";
														validAsterix = "";
														if(empDesignationValidList != null && uF.parseToBoolean(empDesignationValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Designation:<%=validAsterix %></td><td>
													<div id="myDesig">
													<% if(empDesignationValidList != null && uF.parseToBoolean(empDesignationValidList.get(0))) { %>
													<s:select name="strDesignation" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName" cssClass="validateRequired " 
														headerKey="" headerValue="Select Designation" required="true" onchange="getGrades(this.options[this.selectedIndex].value)" />
													<% } else { %>
													<s:select name="strDesignation" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName" headerKey=""
														headerValue="Select Designation" required="true" onchange="getGrades(this.options[this.selectedIndex].value)" />
													<% } %>	
													</div></td>
												</tr>		
												<tr>
													<% 	List<String> empGradeValidList = hmValidationFields.get("EMP_GRADE"); 
														validReqOpt = "";
														validAsterix = "";
														if(empGradeValidList != null && uF.parseToBoolean(empGradeValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Grade:<%=validAsterix %></td><td>
													<div id="myGrade">
													<% if(empGradeValidList != null && uF.parseToBoolean(empGradeValidList.get(0))) { %> 
													<s:select name="empGrade" cssClass="validateRequired" list="gradeList" listKey="gradeId" listValue="gradeCode" 
													headerKey="" id="gradeIdV" headerValue="Select Grade" required="true" />
													<% } else { %>
													<s:select name="empGrade" cssClass="validateRequired " list="gradeList" listKey="gradeId" listValue="gradeCode" headerKey="" 
														id="gradeIdV" headerValue="Select Grade" />
													<% } %>
													</div></td>
												</tr>
												<tr>
													<td class="txtlabel alignRight">Employee/Contractor:</td>
													<td><s:select name="empContractor" id="empContractor" cssClass="validateRequired " headerKey="1" headerValue="Employee" 
													list="#{'2': 'Contractor'}" onchange="createEmpCodeByOrg(this.value);"/> </td> <!-- checkEmpORContractorCode -->
												</tr>
												<tr>
													<% 	List<String> empEmploymentTypeValidList = hmValidationFields.get("EMP_EMPLOYMENT_TYPE"); 
														validReqOpt = "";
														validAsterix = "";
														if(empEmploymentTypeValidList != null && uF.parseToBoolean(empEmploymentTypeValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Employee Type:<%=validAsterix %></td><td>
														<% if(empEmploymentTypeValidList != null && uF.parseToBoolean(empEmploymentTypeValidList.get(0))) { %>
														<s:select name="empType" cssClass="validateRequired " listKey="empTypeId" listValue="empTypeName" headerKey="" 
														headerValue="Select Employee Type" list="empTypeList" /> <!-- onchange="validateMandatory(this.options[this.options.selectedIndex].value)" -->
														<% } else { %>
														<s:select name="empType" id="empType" listKey="empTypeId" listValue="empTypeName" headerKey="" headerValue="Select Employee Type" 
														list="empTypeList" /> <!-- onchange="validateMandatory(this.options[this.options.selectedIndex].value)"  -->
														<% } %>
														<span class="hint">Employment type as part time or full time. It will be used while calculating payroll.<span class="hint-pointer">&nbsp;</span></span>
													</td>
												</tr>
												<tr>
													<% 	List<String> empCodeValidList = hmValidationFields.get("EMP_CODE"); 
														validReqOpt = "";
														validAsterix = "";
														if(empCodeValidList != null && uF.parseToBoolean(empCodeValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight"><input type="hidden" name="empCodeValidReqOpt" id="empCodeValidReqOpt" value="<%=validReqOpt %>" />
														<span id="empORContractorSpan">Employee</span>&nbsp;Code:<%=validAsterix %>
													</td>
													<td id="empCodeTD"><s:hidden name="autoGenerate"/>
														<s:if test="autoGenerate==true">
															<input type="text" name="empCodeAlphabet" id="empCodeAlphabetDis" style="width:100px !important;" value="<%=uF.showData((String)request.getAttribute("empCodeAlphabet"), "") %>" disabled="disabled"/>
																<s:hidden name="empCodeAlphabet" id="empCodeAlphabet"/>
															<input type="text" name="empCodeNumber" id="empCodeNumber" onchange="checkCodeValidation()" class="<%=validReqOpt %> " style="width:100px !important;" value="<%=uF.showData((String)request.getAttribute("empCodeNumber"), "") %>" onkeypress="return isOnlyNumberKey(event)"/>
																<div id="empCodeMessege"></div>
														</s:if>
														<s:else>
												 			<input type="text" name="empCodeAlphabet" id="empCodeAlphabet" onchange="checkCodeValidation()" style="width:100px !important;" value="<%=uF.showData((String)request.getAttribute("empCodeAlphabet"), "") %>"/>
												 			<input type="text" name="empCodeNumber" id="empCodeNumber" onchange="checkCodeValidation()" style="width:100px !important;" class="<%=validReqOpt %> " value="<%=uF.showData((String)request.getAttribute("empCodeNumber"), "") %>" onkeypress="return isOnlyNumberKey(event)"/>
															<div id="empCodeMessege"></div>
														</s:else>
														<span class="hint">Employee Code represents the employee in a company. Code can be in any format e.g. KT001, E01, etc<span class="hint-pointer">&nbsp;</span></span>
													</td>
												</tr>	
												<tr>
													<% 	List<String> empJoiningDateValidList = hmValidationFields.get("EMP_JOINING_DATE"); 
														validReqOpt = "";
														validAsterix = "";
														if(empJoiningDateValidList != null && uF.parseToBoolean(empJoiningDateValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Joining Date:<%=validAsterix %></td>
													<td><input type="text" name="empStartDate" id="empStartDate" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empStartDate"), "") %>" /><span class="hint">Employee's date of joining.<span class="hint-pointer">&nbsp;</span></span></td>
												</tr>
												<tr>
													<% 	List<String> empSeparationDateValidList = hmValidationFields.get("EMP_SEPARATION_DATE"); 
														validReqOpt = "";
														validAsterix = "";
														if(empSeparationDateValidList != null && uF.parseToBoolean(empSeparationDateValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Separation Date:<%=validAsterix %></td>
													<td><input type="text" name="empSeparationDate" id="empSeparationDate" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empSeparationDate"), "") %>" readonly="readonly" /><%-- <span class="hint">Employee's date of separation.<span class="hint-pointer">&nbsp;</span></span> --%></td>
												</tr>
												<tr>
													<% 	List<String> empConfirmationDateValidList = hmValidationFields.get("EMP_CONFIRMATION_DATE"); 
														validReqOpt = "";
														validAsterix = "";
														if(empConfirmationDateValidList != null && uF.parseToBoolean(empConfirmationDateValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Confirmation Date:<%=validAsterix %></td>
													<td><input type="text" name="empConfirmationDate" id="empConfirmationDate" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empConfirmationDate"), "") %>" readonly="readonly" /><%-- <span class="hint">Employee's date of confirmation.<span class="hint-pointer">&nbsp;</span></span> --%></td>
												</tr>
												<tr>
													<% 	List<String> empActConfirmgDateValidList = hmValidationFields.get("EMP_ACTUAL_CONFIRM_DATE"); 
														validReqOpt = "";
														validAsterix = "";
														if(empActConfirmgDateValidList != null && uF.parseToBoolean(empActConfirmgDateValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Actual Confirmation Date:<%=validAsterix %></td>
													<td><input type="text" name="empActConfirmDate" id="empActConfirmDate" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empActConfirmDate"), "") %>" readonly="readonly" /><%-- <span class="hint">Employee's date of actual confirmation.<span class="hint-pointer">&nbsp;</span></span> --%></td>
												</tr>
												<tr>
													<% 	List<String> empPromotionDateValidList = hmValidationFields.get("EMP_PROMOTION_DATE"); 
														validReqOpt = "";
														validAsterix = "";
														if(empPromotionDateValidList != null && uF.parseToBoolean(empPromotionDateValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Promotion Date:<%=validAsterix %></td>
													<td><input type="text" name="empPromotionDate" id="empPromotionDate" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empPromotionDate"), "") %>" readonly="readonly" /><%-- <span class="hint">Employee's date of promotion.<span class="hint-pointer">&nbsp;</span></span> --%></td>
												</tr>
												<tr>
													<% 	List<String> empIncrementDateValidList = hmValidationFields.get("EMP_INCREMENT_DATE"); 
														validReqOpt = "";
														validAsterix = "";
														if(empIncrementDateValidList != null && uF.parseToBoolean(empIncrementDateValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Increment Date:<%=validAsterix %></td>
													<td><input type="text" name="empIncrementDate" id="empIncrementDate" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empIncrementDate"), "") %>" readonly="readonly" /><span class="hint">Employee's date of promotion.<span class="hint-pointer">&nbsp;</span></span></td>
												</tr>
												<tr><td colspan=2>Structure:<hr style="background-color: #346897; height: 1px;"></td></tr>
												
												<tr><td class="txtlabel alignRight" valign="top">Is this employee a CXO/ a HOD:</td>
													<td><s:select headerKey="0" headerValue="Not CXO/HOD" list="#{'1':'CXO', '2':'HOD'}" name="strCXOHOD" id="strCXOHOD" onchange="checkCXOHOD(this.value);"/>
													<% String defaultCXO = (String)request.getAttribute("defaultCXO");
														String defaultHOD = (String)request.getAttribute("defaultHOD");
														String cxoDisplay = "none";
														String hodDisplay = "none";
														if(uF.parseToInt(defaultCXO) == 1) {
															cxoDisplay = "block";
														}
														if(uF.parseToInt(defaultHOD) == 1) {
															hodDisplay = "block";
														}
													%>
													<div id="locDivCXO" style="display: <%=cxoDisplay %>; margin: 5px 0px;">
														<% if(uF.parseToInt(defaultCXO) == 1) { %>
														<s:select  name="locationCXO" id="locationCXO" theme="simple" listKey="wLocationId" listValue="wLocationName" list="wLocationList" 
															cssClass="validateRequired" multiple="true" value="cxoLocationAccess"/>
														<% } %>	
													</div>
													<div id="locDivCXOLbl" style="display: <%=cxoDisplay %>; line-height: 15px; font-size: 11px; color: gray;">(Work Locations are based on above filter)</div>
													
													<div id="locDivHOD" style="display: <%=hodDisplay %>; margin: 5px 0px;">
													<%	String HOD_DEPART_NAME = (String)request.getAttribute("HOD_DEPART_NAME");
														if(uF.parseToInt(defaultHOD) == 1) { 
													%>
														<%=uF.showData(HOD_DEPART_NAME, "") %>
													<% } %>	
													</div>
													<div id="locDivHODLbl" style="display: <%=hodDisplay %>; line-height: 15px; font-size: 11px; color: gray;">(Department is based on above filter)</div>
													</td>
												</tr>
												
												<tr><td colspan=2>Reporting Structure:<hr style="background-color: #346897; height: 1px;"></td></tr>		
											
												<tr>
												<% 	List<String> empHODValidList = hmValidationFields.get("EMP_HOD"); 
													validReqOpt = "";
													validAsterix = "";
													if(empHODValidList != null && uF.parseToBoolean(empHODValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">H.O.D.:<%=validAsterix %>
												<input type="hidden" name="hodValidReq" id="hodValidReq" value="<%=validReqOpt %>"/>
												</td>
												<td id="hodListID">
												<% if(empHODValidList != null && uF.parseToBoolean(empHODValidList.get(0))) { %>
													<s:select name="hod" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired " headerValue="Select H.O.D."
														list="HodList" key="" required="true" />
												<% } else { %>
													<s:select name="hod" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="Select H.O.D." list="HodList" />
												<% } %>	
												<span class="hint">Employee's H.O.D. as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
												</td></tr>
												<tr>
												<% 	List<String> empHRValidList = hmValidationFields.get("EMP_HR"); 
													validReqOpt = "";
													validAsterix = "";
													if(empHRValidList != null && uF.parseToBoolean(empHRValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">HR:<%=validAsterix %>
												<input type="hidden" name="hrValidReq" id="hrValidReq" value="<%=validReqOpt %>"/>
												</td>
												<td id="hrListID">
												<% if(empHRValidList != null && uF.parseToBoolean(empHRValidList.get(0))) { %>	
													<s:select name="HR" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired " headerValue="Select HR"
														list="HRList" key="" required="true" />
												<% } else { %>
													<s:select name="HR" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="Select HR" list="HRList" />
												<% } %>
												<span class="hint">Employee's HR as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
												</td></tr>
										<!-- ===start parvez date: 29-07-2022=== -->		
												<tr>
												<% 	List<String> empSupervisorValidList = hmValidationFields.get("EMP_SUPERVISOR"); 
													validReqOpt = "";
													validAsterix = "";
													if(empSupervisorValidList != null && uF.parseToBoolean(empSupervisorValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Manager:<%=validAsterix %>
												<input type="hidden" name="supervisorValidReq" id="supervisorValidReq" value="<%=validReqOpt %>"/>
												</td>
													<td><span id="supervisorIdsSpan">
													<% if(empSupervisorValidList != null && uF.parseToBoolean(empSupervisorValidList.get(0))) { %>
														<s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired " 
															headerValue="Select Manager" list="supervisorList" key="" />
													<% } else { %>
														<s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey=""  
															headerValue="Select Manager" list="supervisorList" key="" />
													<% } %>	
													</span>	
													<span class="hint">Employee's manager/superior as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
												</td></tr>
										<!-- ===end parvez date: 29-07-2022=== -->	
												
												<tr><td colspan=2>Other HR Policies:<hr style="background-color: #346897; height: 1px;"></td></tr>
												<tr><td class="txtlabel alignRight">Employee Status:<sup>*</sup></td>
													<td>
														<s:radio name="empStatus" id="empStatus"  list="#{'1':'PROBATION','2':'PERMANENT','4':'TEMPORARY'}" value="defaultStatus" cssClass="validateRequired" onclick="showEmpStatus(this.value);"/>
														
													</td>
												</tr>
												<tr id="trProbationId" style="display: none;">
												<% 	List<String> empProbationPeriodValidList = hmValidationFields.get("EMP_PROBATION_PERIOD"); 
													validReqOpt = "";
													validAsterix = "";
													if(empProbationPeriodValidList != null && uF.parseToBoolean(empProbationPeriodValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>"; 
													}
												%>
												<td class="txtlabel alignRight">Probation Period:<%=validAsterix %></td>
												<td>
												<% if(empProbationPeriodValidList != null && uF.parseToBoolean(empProbationPeriodValidList.get(0))) { %>
													<s:select name="probationDuration" listKey="probationDurationID" listValue="probationDurationName" headerKey="" 
													cssClass="validateRequired " headerValue="Select Probation Period" list="probationDurationList" key="" /> 
												<% } else {%>
													<s:select name="probationDuration" listKey="probationDurationID" listValue="probationDurationName" headerKey="" 
													headerValue="Select Probation Period" list="probationDurationList" key="" /> 
												<% } %>
												<span class="hint">This field is used for the Probation Period of Employee.<span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr>
												<% 	List<String> empNoticePeriodValidList = hmValidationFields.get("EMP_NOTICE_PERIOD"); 
													validReqOpt = "";
													validAsterix = "";
													if(empNoticePeriodValidList != null && uF.parseToBoolean(empNoticePeriodValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Notice Period:<%=validAsterix %></td>
												<td>
												<% if(empNoticePeriodValidList != null && uF.parseToBoolean(empNoticePeriodValidList.get(0))) { %>	
													<s:select name="noticeDuration" listKey="noticeDurationID" listValue="noticeDurationName" headerKey="" cssClass="validateRequired "
													headerValue="Select Notice Period" list="noticeDurationList" key="" /> 
												<% } else { %>
													<s:select name="noticeDuration" listKey="noticeDurationID" listValue="noticeDurationName" headerKey="" 
													headerValue="Select Notice Period" list="noticeDurationList" key="" /> 
												<% } %>
												<span class="hint">This field is used for the Notice Period of Employee.<span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr>
												<% 	List<String> empPayrollDurationValidList = hmValidationFields.get("EMP_PAYCYCLE_DURATION"); 
													validReqOpt = "";
													validAsterix = "";
													if(empPayrollDurationValidList != null && uF.parseToBoolean(empPayrollDurationValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Paycycle Duration:<%=validAsterix %></td>
												<td>
												<% if(empPayrollDurationValidList != null && uF.parseToBoolean(empPayrollDurationValidList.get(0))) { %>
													<s:select theme="simple" name="strPaycycleDuration" cssClass="validateRequired " listKey="paycycleDurationId" listValue="paycycleDurationName"  
										             list="paycycleDurationList" key="" />
										        <% } else { %>
										        	<s:select theme="simple" name="strPaycycleDuration" listKey="paycycleDurationId" listValue="paycycleDurationName" 
										             list="paycycleDurationList" key="" />
										        <% } %>
										        <span class="hint">Choose the paycycle duration. e.g., Weekly, Fortnightly, Monthly, etc. <span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr>
												<% 	List<String> empTimeValidationRequiredValidList = hmValidationFields.get("EMP_TIME_VALIDATION_REQUIRED"); 
													validReqOpt = "";
													validAsterix = "";
													if(empTimeValidationRequiredValidList != null && uF.parseToBoolean(empTimeValidationRequiredValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Roster Dependency:<%=validAsterix %></td>
												<td>
												<% if(empTimeValidationRequiredValidList != null && uF.parseToBoolean(empTimeValidationRequiredValidList.get(0))) { %>
													<s:select label="Roster Dependency" name="rosterDependency" cssClass="validateRequired " listKey="approvalId" listValue="approvalName" 
													headerKey="" headerValue="Select Dependency" list="rosterDependencyList" key="" required="true" />
												<% } else { %>
													<s:select label="Roster Dependency" name="rosterDependency" listKey="approvalId" listValue="approvalName" headerKey="" 
													headerValue="Select Dependency" list="rosterDependencyList" key=""/>
												<% } %>
												<span class="hint">Do you want this employee be dependent on roster entries?<span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr>
												<% 	List<String> empPayrollDependentOnAttendanceValidList = hmValidationFields.get("EMP_PAYROLL_DEPENDENT_ON_ATTENDANCE"); 
													validReqOpt = "";
													validAsterix = "";
													if(empPayrollDependentOnAttendanceValidList != null && uF.parseToBoolean(empPayrollDependentOnAttendanceValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Is Payroll dependent on Attendance?:<%=validAsterix %></td>
												<td>
												<% if(empPayrollDependentOnAttendanceValidList != null && uF.parseToBoolean(empPayrollDependentOnAttendanceValidList.get(0))) { %>
													<s:select name="attendanceDependency" cssClass="validateRequired " listKey="approvalId" listValue="approvalName" headerKey=""
													headerValue="Select Dependency" list="rosterDependencyList" key="" />
												<% } else { %>
													<s:select name="attendanceDependency" listKey="approvalId" listValue="approvalName" headerKey=""
													headerValue="Select Dependency" list="rosterDependencyList" key="" />
												<% } %>
												<span class="hint">Do you want this employee be dependent on roster entries?<span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr>
												<% 	validReqOpt = "";
													validAsterix = "";
													if(empLeavesDuringProbationValidList != null && uF.parseToBoolean(empLeavesDuringProbationValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight" valign="top">Select Leave Type:<%=validAsterix %></td>
												<td id="leaveProbationListID">
												<% if(empLeavesDuringProbationValidList != null && uF.parseToBoolean(empLeavesDuringProbationValidList.get(0))) { %>
													<s:select name="probationLeaves" id="probationLeaves" multiple="true" size="3"  cssClass="validateRequired "  
													listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key=""  onchange="getEmpLeaveBalance();"/>
												<% } else { %>
													<s:select name="probationLeaves" id="probationLeaves" multiple="true" size="3" 
													listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key="" onchange="getEmpLeaveBalance();" />
												<% } %>	
												<span class="hint">This field specifies Leaves Allowed During Probation Period of Employee.<span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr>
												<% 	List<String> empBiomatricMachineIdValidList = hmValidationFields.get("EMP_BIOMATRIC_MACHINE_ID"); 
													validReqOpt = "";
													validAsterix = "";
													if(empBiomatricMachineIdValidList != null && uF.parseToBoolean(empBiomatricMachineIdValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Biometric Machine Id:<%=validAsterix %></td>
												<td><input type="text" name="bioId" id="bioId" class="<%=validReqOpt %> " value="<%=uF.showData((String)request.getAttribute("bioId"), "") %>" onchange="emailValidation1('biometricValidatorMessege','bioId',this.value,'EmailValidation.action?biometricId='+this.value);" /><span class="hint">Employee's Boimetric Machine Id<span class="hint-pointer">&nbsp;</span></span></td>
												</tr>
												<tr><td></td><td><div id="biometricValidatorMessege"></div></td></tr>
												<tr>
												<% 	
												String paymentMode = (String) request.getAttribute("empPayMode");// @auther : Dattatray Note: Added Style
												List<String> empPaymentModeValidList = hmValidationFields.get("EMP_PAYMENT_MODE"); 
													validReqOpt = "";
													validAsterix = "";
													if(empPaymentModeValidList != null && uF.parseToBoolean(empPaymentModeValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Payment Mode:<%=validAsterix %></td>
												<td>
												<% if(empPaymentModeValidList != null && uF.parseToBoolean(empPaymentModeValidList.get(0))) { %>
													<s:select id="idEmpPaymentMode" name="empPaymentMode" listKey="payModeId" listValue="payModeName" cssClass="validateRequired " 
													headerValue="Select Payment Mode" list="paymentModeList" onchange="showHideBankName();"/>
												<% } else { %>
													<s:select id="idEmpPaymentMode" name="empPaymentMode" listKey="payModeId" listValue="payModeName" headerValue="Select Payment Mode" 
													list="paymentModeList" onchange="showHideBankName();" />
												<% } %>	
												</td></tr>
												<!-- Start @auther : Dattatray Note: Paymnode check with bank transfer -->
												<%
													 	String hideBankName = "none";
														if(uF.parseToInt(paymentMode) == 1) {
															hideBankName = "table-row";
														}
												%>
												<!-- End @auther : Dattatray -->
												<tr id="idBankName" style="display: <%=hideBankName %>;"><!-- @auther : Dattatray Note: Added Style -->
												<% 	List<String> empBankNameValidList = hmValidationFields.get("EMP_BANK_NAME"); 
													validReqOpt = "";
													validAsterix = "";
													if(empBankNameValidList != null && uF.parseToBoolean(empBankNameValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Bank Name:<%=validAsterix %></td>
												<td>
												<% if(empBankNameValidList != null && uF.parseToBoolean(empBankNameValidList.get(0))) {
												%>
													<s:select id="empBankName" onchange="showHideBankAccNo();" name="empBankName" listKey="bankId" listValue="bankName" headerKey="" headerValue="Select Bank" list="bankList" cssClass="validateRequired "/>
												<% } else { %>
													<s:select id="empBankName" onchange="showHideBankAccNo();" name="empBankName" listKey="bankId" listValue="bankName" headerKey="" headerValue="Select Bank" list="bankList" />
												<% } %>
												<span class="hint">Employee's Bank Name<span class="hint-pointer">&nbsp;</span></span></td>
												</tr>
												
												<tr id="idEmpOtherBankNameTr" hidden>
													<% 	List<String> empOtherBankNameValidList = hmValidationFields.get("EMP_OTHER_BANK_NAME"); 
														validReqOpt = "";
														validAsterix = "";
														if(empOtherBankNameValidList != null && uF.parseToBoolean(empOtherBankNameValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Employee Bank Name:<%=validAsterix %></td>
													<td><input type="text" id="idEmpOtherBankName" name="empOtherBankName" class="<%=validReqOpt %> " value="<%=uF.showData((String)request.getAttribute("empOtherBankName"), "") %>" /><span class="hint">Employee's Bank Name<span class="hint-pointer">&nbsp;</span></span></td>
												</tr>
										<!-- ===start parvez date: 09-01-2023 -->		
												<tr id="idEmpOtherBankBranchTr" hidden>
													<% 	List<String> empOtherBankBranchValidList = hmValidationFields.get("EMP_OTHER_BANK_BRANCH"); 
														validReqOpt = "";
														validAsterix = "";
														if(empOtherBankBranchValidList != null && uF.parseToBoolean(empOtherBankBranchValidList.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}
													%>
													<td class="txtlabel alignRight">Employee Bank Branch:<%=validAsterix %></td>
													<td><input type="text" id="idEmpOtherBankBranch" name="empOtherBankBranch" class="<%=validReqOpt %> " value="<%=uF.showData((String)request.getAttribute("empOtherBankBranch"), "") %>" /><span class="hint">Employee's Bank Branch<span class="hint-pointer">&nbsp;</span></span></td>
												</tr>
										<!-- ====end parvez date: 09-01-2023==== -->			
												
												<tr id="idEmpAccount" style="display: <%=hideBankName %>;"><!-- @auther : Dattatray Note: Added Style -->
												<% 	List<String> empBankAccountNoValidList = hmValidationFields.get("EMP_BANK_ACCOUNT_NO"); 
													validReqOpt = "";
													validAsterix = "";
													if(empBankAccountNoValidList != null && uF.parseToBoolean(empBankAccountNoValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Employee Bank Account Number:<%=validAsterix %></td>
												<td><input type="text" id="idEmpBankAcctNbr" name="empBankAcctNbr" class="<%=validReqOpt %> " value="<%=uF.showData((String)request.getAttribute("empBankAcctNbr"), "") %>" /><span class="hint">Employee's Bank Account no.<span class="hint-pointer">&nbsp;</span></span></td></tr>
											
												
												<tr id="idEmpIFSC" hidden>
												<% 	List<String> empBankIFSCNoValidList = hmValidationFields.get("EMP_BANK_IFSC_NO"); 
													validReqOpt = "" ;
													validAsterix = "" ;
													if(empBankIFSCNoValidList != null && uF.parseToBoolean(empBankIFSCNoValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Employee Bank IFSC Code:<%=validAsterix %></td>
												<td><input type="text" id="idEmpBankIFSCNbr" name="empBankIFSCNbr" class="<%=validReqOpt %> " value="<%=uF.showData((String)request.getAttribute("empBankIFSCNbr"), "") %>"  /><span class="hint">Employee's Bank IFSC Code<span class="hint-pointer">&nbsp;</span></span></td></tr>
											
												<tr id="idBankName2" style="display: <%=hideBankName %>;"><!-- @auther : Dattatray Note: Added Style -->
												<% 	List<String> empBankNameValidList1 = hmValidationFields.get("EMP_SECOND_BANK_NAME"); 
													validReqOpt = "";
													validAsterix = "";
													if(empBankNameValidList1 != null && uF.parseToBoolean(empBankNameValidList1.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
													<td class="txtlabel alignRight">Secondary Bank Name:<%=validAsterix %></td>
													<td>
														<% if(empBankNameValidList1 != null && uF.parseToBoolean(empBankNameValidList.get(0))) { %>
															<s:select id="empBankName2" name="empBankName2" onchange="showHideBankAccNo_second();" listKey="bankId" listValue="bankName" headerKey="" headerValue="Select Bank" list="bankList" cssClass="validateRequired"/>
														<% } else { %>
															<s:select id="empBankName2" name="empBankName2" listKey="bankId" onchange="showHideBankAccNo_second();" listValue="bankName" headerKey="" headerValue="Select Bank" list="bankList"/>
														<% } %>
															<span class="hint">Employee's Secondary Bank Name<span class="hint-pointer">&nbsp;</span></span>
													</td>
												</tr>
												
												<tr id="idEmpOtherBankNameTr2" hidden>
													<% 	List<String> empOtherBankNameValidList1 = hmValidationFields.get("EMP_SECOND_OTHER_BANK_NAME"); 
													validReqOpt = "";
													validAsterix = "";
													if(empOtherBankNameValidList1 != null && uF.parseToBoolean(empOtherBankNameValidList1.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}%>
													<td class="txtlabel alignRight">Employee Secondary Bank Name:<%=validAsterix %></td>
													<td>
														<input id="idEmpOtherBankName2" type="text" name="empOtherBankName2" class="<%=validReqOpt%> "  value="<%=uF.showData((String)request.getAttribute("empOtherBankName2"), "") %>" />
														<span class="hint">Employee's Secondary Bank Name :<span class="hint-pointer">&nbsp;</span></span>
													</td>
												</tr>
												
									<!-- ===start parvez date: 09-01-2023=== -->		
												<tr id="idEmpOtherBankBranchTr2" hidden>
													<% 	List<String> empOtherBankBranchValidList1 = hmValidationFields.get("EMP_SECOND_OTHER_BANK_BRANCH"); 
													validReqOpt = "";
													validAsterix = "";
													if(empOtherBankBranchValidList1 != null && uF.parseToBoolean(empOtherBankBranchValidList1.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}%>
													<td class="txtlabel alignRight">Employee Secondary Bank Branch:<%=validAsterix %></td>
													<td>
														<input id="idEmpOtherBankBranch2" type="text" name="empOtherBankBranch2" class="<%=validReqOpt%> "  value="<%=uF.showData((String)request.getAttribute("empOtherBankBranch2"), "") %>" />
														<span class="hint">Employee's Secondary Bank Branch :<span class="hint-pointer">&nbsp;</span></span>
													</td>
												</tr>
									<!-- ===end parvez date: 09-01-2023=== -->				
												
												<tr id="idEmpAccount2" style="display: <%=hideBankName %>;"><!-- @auther : Dattatray Note: Added Style -->
													<% 	List<String> empBankAccountNoValidList1 = hmValidationFields.get("EMP_SECOND_BANK_ACCOUNT_NO"); 
														validReqOpt = "";
														validAsterix = "";
														if(empBankAccountNoValidList1 != null && uF.parseToBoolean(empBankAccountNoValidList1.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}%>
														<td class="txtlabel alignRight">Employee Secondary Bank Account Number:<%=validAsterix %></td>
														<td>
															<input type="text" id="idEmpBankAcctNbr2" name="empBankAcctNbr2" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empBankAcctNbr2"), "") %>"/>
															<span class="hint">Employee's Secondary Bank Account no.<span class="hint-pointer">&nbsp;</span></span>
														</td>
												</tr>
														
													
													  <tr id="idEmpIFSC2" hidden>
														<% 	List<String> empBankIFSCNoValidList1 = hmValidationFields.get("EMP_SECOND_BANK_IFSC_NO"); 
														validReqOpt = "";
														validAsterix = "";
														if(empBankIFSCNoValidList1 != null && uF.parseToBoolean(empBankIFSCNoValidList1.get(0))) {
															validReqOpt = "validateRequired";
															validAsterix = "<sup>*</sup>";
														}%>
														<td class="txtlabel alignRight">Employee Secondary Bank IFSC Code:<%=validAsterix %></td>
														<td>
															<input id="idEmpBankIFSCNbr2" type="text" name="empBankIFSCNbr2" class="<%=validReqOpt%> "  value="<%=uF.showData((String)request.getAttribute("empBankIFSCNbr2"), "") %>" />
															<span class="hint">Employee's Secondary Bank IFSC Code :<span class="hint-pointer">&nbsp;</span></span>
														</td>
													</tr>
													
												
												<tr><td colspan=2>Corporate Contact:<hr style="background-color:#346897; height:1px;"></td></tr>
												<tr>
												<% 	List<String> empCorporateMobileNoValidList = hmValidationFields.get("EMP_CORPORATE_MOBILE_NO"); 
													validReqOpt = "";
													validAsterix = "";
													if(empCorporateMobileNoValidList != null && uF.parseToBoolean(empCorporateMobileNoValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Corporate Mobile:<%=validAsterix %></td>
												<td><input type="text" name="empCorporateMobileNo"  id="empCorporateMobileNo" class="<%=validReqOpt %> " value="<%=uF.showData((String)request.getAttribute("empCorporateMobileNo"), "") %>" /></td></tr>
												<tr>
												<% 	List<String> empCorporateDeskValidList = hmValidationFields.get("EMP_CORPORATE_DESK"); 
													validReqOpt = "";
													validAsterix = "";
													if(empCorporateDeskValidList != null && uF.parseToBoolean(empCorporateDeskValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Corporate Desk:<%=validAsterix %></td>
												<td><input type="text" name="empCorporateDesk" id="empCorporateDesk" class="<%=validReqOpt %> " value="<%=uF.showData((String)request.getAttribute("empCorporateDesk"), "") %>" /></td></tr>
												<tr>
												<% 	List<String> empCorporateEmailIdValidList = hmValidationFields.get("EMP_CORPORATE_EMAIL_ID"); 
													validReqOpt = "validateEmail";
													validAsterix = "";
													if(empCorporateEmailIdValidList != null && uF.parseToBoolean(empCorporateEmailIdValidList.get(0))) {
														validReqOpt = "validateEmailRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Corporate Email Id:<%=validAsterix %></td>
												<td><input type="text" name="empEmailSec"  id="empEmailSec" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empEmailSec"), "") %>" onchange="emailValidation1('corporateemailValidatorMessege','empEmailSec',this.value,'EmailValidation.action?cemail='+this.value);" /><span class="hint">Employee's Secondary Email Id<span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr><td></td><td><div id="corporateemailValidatorMessege"></div></td></tr>
												<tr>
												<% 	List<String> empSkypeIdValidList = hmValidationFields.get("EMP_SKYPE_ID"); 
													validReqOpt = "";
													validAsterix = "";
													if(empSkypeIdValidList != null && uF.parseToBoolean(empSkypeIdValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td class="txtlabel alignRight">Corporate Skype Id:<%=validAsterix %></td>
												<td><input type="text" name="skypeId" class="<%=validReqOpt %> " value="<%=uF.showData((String)request.getAttribute("skypeId"), "") %>"/><span class="hint">Employee's Skype Id<span class="hint-pointer">&nbsp;</span></span></td></tr>
												<tr><td colspan=2>Statutory Compliance Applied:<hr style="background-color:#346897; height:1px;"></td></tr>
												<tr>
												<% 	List<String> empForm16ValidList = hmValidationFields.get("EMP_FORM_16"); 
													validReqOpt = "";
													validAsterix = "";
													if(empForm16ValidList !=  null && uF.parseToBoolean(empForm16ValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
													String strForm16Selected = "";
													if(uF.parseToBoolean((String)request.getAttribute("isForm16"))){
														strForm16Selected = "checked";
													}
												%>
												<td class="txtlabel alignRight" valign="top">Form 16:<%=validAsterix %></td><td>
													<input type="checkbox" name="isForm16" class="<%=validReqOpt %>" <%=strForm16Selected %>/>
												</td></tr>
												<tr>
												<% 	List<String> empForm16AValidList = hmValidationFields.get("EMP_FORM_16_A"); 
													validReqOpt = "";
													validAsterix = "";
													if(empForm16AValidList != null && uF.parseToBoolean(empForm16AValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
													String strForm16ASelected = "";
													if(uF.parseToBoolean((String)request.getAttribute("isForm16A"))){
														strForm16ASelected = "checked";
													}
												%>
												<td class="txtlabel alignRight" valign="top">Form 16 A:<%=validAsterix %></td><td><input type="checkbox" name="isForm16A" class="<%=validReqOpt %>" <%=strForm16ASelected %>/></td></tr>
												<tr>
												<td class="alignRight">Select Income Tax Slab:<sup>*</sup></td><td><select rel="5" name="slabType" id="slabType" class="validateRequired">
													<option value="0" <%=(request.getAttribute("slabType") != null && ((String)request.getAttribute("slabType")).equals("0")) ? "selected" : "" %>>Standard Slab</option>
													<option value="1" <%=(request.getAttribute("slabType") != null && ((String)request.getAttribute("slabType")).equals("1")) ? "selected" : "" %>>New Slab</option>
													</select><span class="hint">Select the slab type.<br>Standard Slab- Standard slab.<br>New Slab - New slab<span class="hint-pointer">&nbsp;</span></span></td>
												</tr>
											<%
												List<List<String>> leaveTypeListWithBalance = (List<List<String>>) request.getAttribute("leaveTypeListWithBalance");
												Map<String, String> hmEmpLeaveBalance = (Map<String, String>) request.getAttribute("hmEmpLeaveBalance");
											%>	
												<tr><td colspan=2>Other Company Policies: <hr style="background-color:#346897; height:1px;"></td></tr>
												<tr>
												<% 	List<String> empLeaveBalanceValidList = hmValidationFields.get("EMP_LEAVE_BALANCE"); 
													validReqOpt = "";
													validAsterix = "";
													if(empLeaveBalanceValidList != null && uF.parseToBoolean(empLeaveBalanceValidList.get(0))) {
														validReqOpt = "validateRequired";
														validAsterix = "<sup>*</sup>";
													}
												%>
												<td colspan=2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Leave Balance:</td></tr>
												<tr><td colspan=2 class="txtlabel alignRight" valign="top">
														<div id="divLeaveBal">
														<% 
														List<String> alAccrueLeave = (List<String>)request.getAttribute("alAccrueLeave");
														
														if(alAccrueLeave == null) alAccrueLeave = new ArrayList<String>();
														for(int i=0; leaveTypeListWithBalance != null && !leaveTypeListWithBalance.isEmpty() && i<leaveTypeListWithBalance.size(); i++) {
															List<String> innerList = leaveTypeListWithBalance.get(i);
															if(alAccrueLeave.contains(innerList.get(0))){
																continue;
															}
														%>
															<div style="float: left; width: 100%;">
																<div style="float: left; width: 31%;"><%=innerList.get(1) %>:<%=validAsterix %></div>
																<div style="float: left; width: 58%; text-align: left; margin-left: 10px;">
																	<% if(hmEmpLeaveBalance != null && hmEmpLeaveBalance.get(innerList.get(0)) != null) { %>
																			<%=hmEmpLeaveBalance.get(innerList.get(0)) %>
																	<% } else { %>
																		<input type="hidden" name="<%=innerList.get(0) %>" value="1" />
																		<span style="float: left; margin-top: 7px;"> <input type="text" name="leaveBal<%=innerList.get(0) %>" id="leaveBal<%=innerList.get(0) %>" class="<%=validReqOpt %>" value="<%=innerList.get(2) %>" style="width: 41px; text-align: right;"/> </span>
																		<span style="float: left; margin-left: 10px;"><i> If edited, it will replace the current balance.<br/>This edition is one time only. </i></span>
																	<% } %>
																</div>
															</div> 
														<% } %>
														</div>	
													</td>
												</tr>
											</table>
											<div style="margin-top: 20px;">
											<div id="designKRADiv" style="width: 100%;"> </div>
												<div style="width: 100%;">
												<% 
													List<List<String>> empKraDetails = (List<List<String>>) request.getAttribute("empKraDetails");
												%> 
												<table id="tbl_all_kras" class="table" style="padding-left: 10px;width: auto;">
													<% 	List<String> empKRAElementValidList = hmValidationFields.get("EMP_KRA_ELEMENT"); 
														String KRAElementValidReqOpt = "";
														String KRAElementValidAsterix = "";
														if(empKRAElementValidList != null && uF.parseToBoolean(empKRAElementValidList.get(0))) {
															KRAElementValidReqOpt = "validateRequired";
															KRAElementValidAsterix = "<sup>*</sup>";
														}
														
														List<String> empKRAAttributeValidList = hmValidationFields.get("EMP_KRA_ATTRIBUTE"); 
														String KRAAttributeValidReqOpt = "";
														String KRAAttributeValidAsterix = "";
														if(empKRAAttributeValidList != null && uF.parseToBoolean(empKRAAttributeValidList.get(0))) {
															KRAAttributeValidReqOpt = "validateRequired";
															KRAAttributeValidAsterix = "<sup>*</sup>";
														}
														
														List<String> empKRAValidList = hmValidationFields.get("EMP_KRA"); 
														String KRAValidReqOpt = "";
														String KRAValidAsterix = "";
														if(empKRAValidList != null && uF.parseToBoolean(empKRAValidList.get(0))) {
															KRAValidReqOpt = "validateRequired";
															KRAValidAsterix = "<sup>*</sup>";
														}
													%>
														<tr><td><b>Key Responsibility Areas</b></td></tr>
														<% if(empKraDetails != null && empKraDetails.size() > 0) { %> 
														<tr><td><input type="hidden" name="kracount" id="kracount" value="<%=empKraDetails.size()-1 %>" /></td></tr>
															<% 
															for(int i=0; i<empKraDetails.size(); i++) {
																List<String> innerList = empKraDetails.get(i); 
															%>
															<tr id="emp_kra_TR<%=i %>">
																<td style="border-bottom: 1px solid #B6B6B6;">
																<table>
																	<tr>
																		<td>
																			<span><%=KRAElementValidAsterix %>
																				<select name="goalElements" id="goalElements<%=i %>" class="<%=KRAElementValidReqOpt %>" style="width: 130px !important;" onchange="getAttributes(this.value, '<%=i %>');">
																				<option value="">Select Element</option><%=innerList.get(2) %></select>
																			</span>
																			<span id="attributeDiv<%=i %>" style="float: left;"><%=KRAAttributeValidAsterix %>
																				<select name="elementAttribute" id="elementAttribute<%=i %>" class="<%=KRAAttributeValidReqOpt %>" style="width: 130px !important;">
																				<option value="">Select Attribute</option><%=innerList.get(3) %></select>
																			</span>
																		</td>
																	</tr>
																	<tr>
																		<td colspan="2">
																			<input type="hidden" name="empKraId" id="empKraId" value="<%=innerList.get(0) %>"/>
																			<input type="hidden" name="empKraTaskId" id="empKraTaskId" value="<%=innerList.get(4) %>"/>
																			<span><%=KRAValidAsterix %>
																				<p>KRA: <input type="text" name="empKRA" id="empKRA<%=i %>" class="<%=KRAValidReqOpt %> " value="<%=innerList.get(1) %>"/></p>
																				<p>Task: <input type="text" name="empKRATask" id="empKRATask<%=i %>" class="<%=KRAValidReqOpt %>" value="<%=innerList.get(5) %>"/></p>
																				<%if(i > 0) { %>
																					<a href="javascript:void(0)" onclick="removeKRA(this.parentNode.parentNode.parentNode.rowIndex)" class="remove-font" >&nbsp;</a>
																				<% } %>
																				<a href="javascript:void(0)" onclick="addNewKRA();" class="add-font">&nbsp;</a>
																			</span>	
																		</td>
																	</tr>
																</table>
																</td>
															</tr>
															<% } %>	
														<% } else { %>
														<tr id="emp_kra_TR0"><td style="border-bottom: 1px solid #EEEEEE;">
														<table class="table">
															<tr>
																<td>
																	<span><%=KRAElementValidAsterix %>
																		<select name="goalElements" id="goalElements0" class="<%=KRAElementValidReqOpt %> " style="width: 130px !important;" onchange="getAttributes(this.value, '0');">
																		<option value="">Select Element</option>
																		<%=request.getAttribute("elementOptions") %>
																		</select>
																	</span>
																</td>
																<td>
																	<span id="attributeDiv0"><%=KRAAttributeValidAsterix %>
																		<select name="elementAttribute" id="elementAttribute0" class="<%=KRAAttributeValidReqOpt %> " style="width: 130px !important;">
																		<option value="">Select Attribute</option></select>
																	</span>
																</td>
															</tr>
															<tr>
																<td colspan="2">
																	<input type="hidden" name="kracount" id="kracount" value="0"/>
																	<span><%=KRAValidAsterix %>
																		<p>KRA: <input type="text" name="empKRA" id="empKRA0" class="<%=KRAValidReqOpt %> "/></p>
																		<p>Task: <input type="text" name="empKRATask" id="empKRATask0" class="<%=KRAValidReqOpt %> "/></p>
																		<a href="javascript:void(0)" style="float: right; margin: 0px;" onclick="addNewKRA();" class="add-font">&nbsp;</a>
																	</span>
																</td>
															</tr>
														</table>
														</td> </tr>
														<% } %>
												</table>
												</div>
											</div>
												<div class="clr"></div>
												<div style="margin-top: 30px;margin-left: 15px;float: right;">
													<table>
													<tr><td colspan="2" align="center">
														<s:if test="mode==null">
															<s:submit cssClass="btn btn-primary  next-ste" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
														</s:if>
														<s:else>
															<s:hidden name="mode" />
															<s:submit cssClass="btn btn-primary" value="Update Information" align="center" onclick="alert('This data will not be updated in the timeline.\n Please use the employee activity to update the timeline and impact employee life cycle.');"/>
														</s:else>
														</td></tr>
													</table>
												</div>
											</s:form>
											</div>
											<script type="text/javascript">
											//showHideBankName();
											<%-- showEmpStatus('<%=(String) request.getAttribute("defaultStatus")%>', ''); --%>
										</script>
									<% } %>
			                    </div>
			                    </s:if>
			                    <s:if test="step==9 || mode=='report'">
			                    <div class="tab-pane active" role="tabpanel" id="si">
									    <div><span style="color:#68AC3B; font-size:22px">Step 9: </span><strong style="font-size: 18px;font-weight: 600;">Salary Information</strong></div>
										<div id="salDiv">
										<s:if test="mode=='report' || mode=='profile' || mode=='onboard'">
											<s:action name="EmployeeSalaryDetails" executeResult="true">
												<s:param name="empId"><s:property value="empId"/> </s:param>
												<s:param name="CCID"><s:property value="serviceId"/></s:param>
												<s:param name="step"><s:property value="step"/></s:param>
												<s:param name="mode">E</s:param>
											</s:action>
										</s:if>
										<s:else>
											<s:action name="EmployeeSalaryDetails" executeResult="true">
												<s:param name="empId"><s:property value="empId"/> </s:param>
												<s:param name="CCID"><s:property value="serviceId"/></s:param>
												<s:param name="step"><s:property value="step"/></s:param>
												<s:param name="mode">A</s:param>
											</s:action>
										</s:else>
										</div>
										<div style="float:right;">
										</div>
			                    </div>
			                    </s:if>
			                    <div class="clearfix"></div>
			                </div>
			        </div>
				<p class="message"><%=strMessage%></p>
				<script>
				/* if(document.getElementById("empType")) {validateMandatory(document.frmOfficial.empType.options[document.frmOfficial.empType.options.selectedIndex].value);} */
				</script>
				</div>
                </div>
            </div> </section> </div></section>