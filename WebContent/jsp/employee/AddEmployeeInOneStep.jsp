<%@page import="com.konnect.jpms.recruitment.FillEducational"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@page import="com.konnect.jpms.select.FillMaritalStatus"%>
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.select.FillYears" %> 
<%@page import="com.konnect.jpms.select.FillDegreeDuration"%> 
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%-- <script src="js/CustomAjaxAddEmployee.jsp"></script> --%>

<jsp:include page="../employee/CustomAjaxForAddEmployee.jsp"></jsp:include>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<script>
   $(function(){
	   $("#empStartDate").datepicker({format: 'dd/mm/yyyy'});
	   $("#empDateOfBirth").datepicker({format: 'dd/mm/yyyy'});
	   $("#probationLeaves").multiselect().multiselectfilter();
	   
	    <%-- $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy',yearRange: '<%=uF.parseToInt(currentYear)-80%>:<%=uF.parseToInt(currentYear)-14%>', changeYear: true, defaultDate: new Date("01/01/<%=""+(uF.parseToInt(currentYear)-80)%>"); --%>
	    /* } */
	    
	   $("body").on("click","#btnSubmt",function() {
	    	$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
			$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
			$("#"+ this.form.id ).find('.validateEmailRequired').filter(':visible').attr('type','email').prop('required',true);
			$("#"+ this.form.id ).find('.validateEmail').filter(':visible').attr('type','email').prop('required',false);
	    });
	   
	   /* $("input[name='btnSubmit']").click(function(){
		   $("#"+ this.form.id).find('.validateRequired').filter(':hidden').prop('required', false);
		   $("#"+ this.form.id).find('.validateRequired').filter(':visible').prop('required', true);
	   }); */
   });
   
   
	/* function submitForm() {
		document.getElementById("btnSubmt").style.display = 'none';
		document.getElementById("btnSubmit").value = 'Submit';
	    document.frmPersonalInfo.submit();
    } */
   
   $("#frmPersonalInfo").submit(function(e){
	   document.getElementById("btnSubmt").style.display = 'none';
	   document.getElementById("btnSubmit").value = 'Submit';
	});
    
</script>


<%
    String struserType = (String)session.getAttribute(IConstants.USERTYPE);
    String sessionUserId = (String)session.getAttribute(IConstants.EMPID);
    
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    String strImage = (String) request.getAttribute("strImage");
    String strCoverImage = (String) request.getAttribute("strCoverImage");
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    int nEmpAlphaCodeLength = 2;
    if (CF != null && CF.getStrOEmpCodeAlpha() != null) {
    	nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();
    }
    %>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title">
                        <%if(session.getAttribute(IConstants.USERID)!=null){ %>
                        <span><%=(request.getParameter("operation")!=null) ? "Edit" : "Enter" %> Employee Detail</span>
                        <%}else { %>
                        <span><%=(request.getParameter("operation")!=null) ? "Edit" : "Enter" %> your Details</span>
                        <%}%>
                    </h3>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 420px;">
                    <div class="leftbox reportWidth row row_without_margin">
                        <%
                            String strEmpType = (String) session.getAttribute("USERTYPE");
                            String strMessage = (String) request.getAttribute("MESSAGE");
                            if (strMessage == null) {
                            	strMessage = "";
                            }
                            
                            Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
                            String validReqOpt = "";
                            String validAsterix = "";
                            %>
                        <p class="message"><%=strMessage%></p>
                        <s:form theme="simple" action="AddEmployeeInOneStep" name="frmPersonalInfo" id="frmPersonalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkImageSize();">
                            <div class="col-lg-7 col-md-12 col-sm-12">
                                <table border="0" class="table table_no_border form-table">
                                            <s:hidden name="empId" id="empId" />
                                            <s:hidden name="operation" id="operation" />
                                            <%-- <s:hidden name="btnSubmit" id="btnSubmit" /> --%>
                                    <tr>
                                        <td colspan=2>
                                           	<strong> Personal Information:</strong>
                                            <hr style="background-color:#346897; height:1px;">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">Salutation:<%=(String)request.getAttribute("salutationValidAsterix") %></td>
                                        <td>
                                            <%if(session.getAttribute("isApproved")==null) { %>
                                            <%if(request.getAttribute("salutationValidReqOpt") != null && !request.getAttribute("salutationValidReqOpt").toString().equals("")) { %>
                                            <s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
                                                listKey="salutationId" listValue="salutationName" cssClass="validateRequired" />
                                            <% } else { %>
                                            <s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
                                                listKey="salutationId" listValue="salutationName"/>
                                            <% } %>
                                            <% } else { %>
                                            <input type="text" name="salutation" id="salutation" class="<%=(String)request.getAttribute("salutationValidReqOpt") %>" disabled="disabled"/>
                                            <s:hidden name="salutation" />
                                            <% } %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">First Name:<%=(String)request.getAttribute("empFNameValidAsterix") %></td>
                                        <td>
                                            <% if(session.getAttribute("isApproved") == null) { %>
                                            <input type="text" name="empFname" id="empFname" class="<%=(String)request.getAttribute("empFNameValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empFname"), "") %>"/>
                                            <% } else { %>
                                            <input type="text" name="empFname" id="empFname" class="<%=(String)request.getAttribute("empFNameValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empFname"), "") %>" disabled="disabled"/>
                                            <s:hidden name="empFname" />
                                            <% } %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">Middle Name:<%=(String)request.getAttribute("empMNameValidAsterix") %></td>
                                        <td>
                                            <%if(session.getAttribute("isApproved")==null) {%>
                                            <input type="text" name="empMname" id="empMname" class="<%=(String)request.getAttribute("empMNameValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empMname"), "") %>"/>
                                            <%}else{%>
                                            <input type="text" name="empMname" id="empMname" class="<%=(String)request.getAttribute("empMNameValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empMname"), "") %>" disabled="disabled"/>
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
                                            <input type="text" name="empLname" id="empLname" class="<%=(String)request.getAttribute("empLNameValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empLname"), "") %>"/>
                                            <%}else{%>
                                            <input type="text" name="empLname" id="empLname" class="<%=(String)request.getAttribute("empLNameValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empLname"), "") %>" disabled="disabled"/>
                                            <s:hidden name="empLname" />
                                            <%}%>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">Personal Email Id:<%=(String)request.getAttribute("empPersonalEmailIdValidAsterix") %></td>
                                        <td>
                                            <%if(session.getAttribute("isApproved") == null) {
                                                String email = (String)request.getAttribute("EMPLOYEE_EMAIL");
                                                if(email == null) {
                                                	email = "";
                                                }
                                                %>
                                            <input type="text" name="empEmail" id="empEmail" class="<%=(String)request.getAttribute("empPersonalEmailIdValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empEmail"), "") %>" onchange="emailValidation('emailValidatorMessege','empEmail',this.value, 'EmailValidation.action?email='+this.value);"/>
                                            <div id="emailValidatorMessege"></div>
                                            <% } else { %>
                                            <input type="text" name="empEmail" id="empEmail" class="<%=(String)request.getAttribute("empPersonalEmailIdValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empEmail"), "") %>" disabled="disabled"/>
                                            <s:hidden name="empEmail" />
                                            <% } %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">Gender:<%=(String)request.getAttribute("empGenderValidAsterix") %></td>
                                        <td>
                                            <% if(request.getAttribute("empGenderValidReqOpt") != null && !request.getAttribute("empGenderValidReqOpt").toString().equals("")) { %>
                                            <s:select theme="simple" cssClass="validateRequired " label="Select Gender" name="empGender" listKey="genderId"
                                                listValue="genderName" headerKey="" headerValue="Select Gender" list="empGenderList" />
                                            <% } else { %>
                                            <s:select theme="simple" label="Select Gender" name="empGender" listKey="genderId"
                                                listValue="genderName" headerKey="" headerValue="Select Gender" list="empGenderList" cssClass=" "/>
                                            <% } %>		
                                        </td>
                                    </tr>
                                    
                                    <tr>
										<td class="txtlabel alignRight">Date Of Birth:<%=(String)request.getAttribute("empDateOfBirthValidAsterix") %></td><td>
											<input type="text" name="empDateOfBirth" id="empDateOfBirth" class="<%=(String)request.getAttribute("empDateOfBirthValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("empDateOfBirth"), "") %>"/>
										</td>
									</tr>
			
                                    <tr>
                                        <td colspan=2>
                                            <strong>Official Information:</strong>
                                            <hr style="background-color:#346897; height:1px;">
                                        </td>
                                    </tr>
                                    <tr>
                                        <% 	List<String> empOrganisationValidList = hmValidationFields.get("EMP_ORGANISATION"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empOrganisationValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Organisation:<%=validAsterix %></td>
                                        <td>
                                            <% if(uF.parseToBoolean(empOrganisationValidList.get(0))) { %>
												<s:select label="Select Organisation" name="orgId" id="strOrg" cssClass="validateRequired" listKey="orgId" listValue="orgName" 
												headerKey="" headerValue="Select Organisation" list="orgList" key="" onchange="getDataFromAjax(this.value);"/>
											<% } else { %>
												<s:select label="Select Organisation" name="orgId" id="strOrg" listKey="orgId" listValue="orgName" 
												headerKey="" headerValue="Select Organisation" list="orgList" key="" onchange="getDataFromAjax(this.value);"/>
											<% } %>		
                                        </td>
                                    </tr>
                                    <tr>
										<% 	List<String> empLocationValidList = hmValidationFields.get("EMP_LOCATION"); 
											validReqOpt = "";
											validAsterix = "";
											if(uF.parseToBoolean(empLocationValidList.get(0))) {
												validReqOpt = "validateRequired";
												validAsterix = "<sup>*</sup>";
											}
										%>
										<td class="txtlabel alignRight">Work Location:<%=validAsterix %></td>
										<td id="idOrgId">
										<% if(uF.parseToBoolean(empLocationValidList.get(0))) { %>	
											<s:select label="Select Work Location" name="wLocation" id="wLocation" cssClass="validateRequired" listKey="wLocationId" 
											listValue="wLocationName" headerKey="" headerValue="Select Location" list="wLocationList" onchange="getReportingByLocation();"/> <!-- onchange="javascript:show_department();return false;" -->
										<% } else { %>
											<s:select label="Select Work Location" name="wLocation" id="wLocation" listKey="wLocationId" listValue="wLocationName" 
											headerKey="" headerValue="Select Location" list="wLocationList" onchange="getSupervisorList();"/>
										<% } %>
											<span class="hint">Employee's work location.<span class="hint-pointer">&nbsp;</span></span>
										</td>
									</tr>
									<tr>
										<% 	List<String> empSBUValidList = hmValidationFields.get("EMP_SBU"); 
											validReqOpt = "";
											validAsterix = "";
											if(uF.parseToBoolean(empSBUValidList.get(0))) {
												validReqOpt = "validateRequired";
												validAsterix = "<sup>*</sup>";
											}
										%>
										<td class="txtlabel alignRight" valign="top">SBU:<%=validAsterix %></td>
										<td class="" id="idService">
										<% if(uF.parseToBoolean(empSBUValidList.get(0))) { %>
											<s:select name="service" listKey="serviceId" cssClass="validateRequired" headerKey="" headerValue="Select SBU"
											listValue="serviceName" list="serviceList" key="" /> <!-- multiple="true" size="3" -->
										<% } else { %>
											<s:select name="service" listKey="serviceId" headerKey="" headerValue="Select SBU"
											listValue="serviceName" list="serviceList" key="" />
										<% } %>	
											<span class="hint">The SBU where the employee is supposed to work. This field will be used while calculating roster.<span class="hint-pointer">&nbsp;</span></span>
										</td>
									</tr>
									<tr>
										<% 	List<String> empDepartmentValidList = hmValidationFields.get("EMP_DEPARTMENT"); 
											validReqOpt = "";
											validAsterix = "";
											if(uF.parseToBoolean(empDepartmentValidList.get(0))) {
												validReqOpt = "validateRequired";
												validAsterix = "<sup>*</sup>";
											}
										%>
										<td class="txtlabel alignRight">Department:<%=validAsterix %></td>
										<td id="idDepartment">
										<% if(uF.parseToBoolean(empDepartmentValidList.get(0))) { %>
											<s:select theme="simple" name="department" id="department" listKey="deptId" cssClass="validateRequired"
												listValue="deptName" headerKey="" headerValue="Select Department" list="deptList" key="" onchange="showCurrentOrgSelectedDepartment();"/>
										<% } else { %>
											<s:select theme="simple" name="department" id="department" listKey="deptId" listValue="deptName" 
												headerKey="" headerValue="Select Department" list="deptList" key="" onchange="showCurrentOrgSelectedDepartment();"/>
										<% } %>		
										</td>
									</tr>
									
									<tr>
										<% 	List<String> empLevelValidList = hmValidationFields.get("EMP_LEVEL"); 
											validReqOpt = "";
											validAsterix = "";
											if(uF.parseToBoolean(empLevelValidList.get(0))) {
												validReqOpt = "validateRequired";
												validAsterix = "<sup>*</sup>";
											}
										%>
										<td class="txtlabel alignRight">Level:<%=validAsterix %></td>
										<td id="idLevel">
										<% if(uF.parseToBoolean(empLevelValidList.get(0))) { %>
										<s:select name="strLevel" list="levelList" listKey="levelId" id="levelIdV" listValue="levelCodeName" cssClass="validateRequired" 
											headerKey="" headerValue="Select Level" onchange="getDesigAndLeave(this.value);"></s:select>		
										<% } else { %>
										<s:select name="strLevel" list="levelList" listKey="levelId" id="levelIdV" listValue="levelCodeName" headerKey=""
											headerValue="Select Level" onchange="getDesigAndLeave(this.value);"></s:select>
										<% } %>	
										<!-- onchange="getContentAcs('myDesig','GetDesigList.action?strLevel='+this.options[this.selectedIndex].value+'&fromPage=AddEmp');" -->
										</td>
									</tr>
										
									<tr>
										<% 	List<String> empDesignationValidList = hmValidationFields.get("EMP_DESIGNATION"); 
											validReqOpt = "";
											validAsterix = "";
											if(uF.parseToBoolean(empDesignationValidList.get(0))) {
												validReqOpt = "validateRequired";
												validAsterix = "<sup>*</sup>";
											}
										%>
										<td class="txtlabel alignRight">Designation:<%=validAsterix %></td><td>
											<div id="myDesig">
											<% if(uF.parseToBoolean(empDesignationValidList.get(0))) { %>
											<s:select name="strDesignation" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName" cssClass="validateRequired" 
												headerKey="" headerValue="Select Designation" required="true" onchange="getGrades(this.options[this.selectedIndex].value)" />
											<% } else { %>
											<s:select name="strDesignation" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName" headerKey=""
												headerValue="Select Designation" required="true" onchange="getGrades(this.options[this.selectedIndex].value)" />
											<% } %>	
											</div>
										</td>
									</tr>
									<tr>
										<% 	List<String> empGradeValidList = hmValidationFields.get("EMP_GRADE"); 
											validReqOpt = "";
											validAsterix = "";
											if(uF.parseToBoolean(empGradeValidList.get(0))) {
												validReqOpt = "validateRequired";
												validAsterix = "<sup>*</sup>";
											}
										%>
										<td class="txtlabel alignRight">Grade:<%=validAsterix %></td><td>
										<div id="myGrade">
											<% if(uF.parseToBoolean(empGradeValidList.get(0))) { %> 
											<s:select name="empGrade" cssClass="validateRequired" list="gradeList" listKey="gradeId" listValue="gradeCode"
											headerKey="" id="gradeIdV" headerValue="Select Grade" required="true" />
											<% } else { %>
											<s:select name="empGrade" cssClass="validateRequired" list="gradeList" listKey="gradeId" listValue="gradeCode" headerKey="" 
												id="gradeIdV" headerValue="Select Grade" />
											<% } %>
											</div>
										</td>
									</tr>
                                    <tr>
                                        <td class="txtlabel alignRight">Employee/Contractor:</td>
                                        <td>
                                            <s:select name="empContractor" id="empContractor" cssClass="validateRequired " headerKey="1" headerValue="Employee" 
                                                list="#{'2': 'Contractor'}" onchange="createEmpCodeByOrg();"/>
                                        </td>
                                        <!-- checkEmpORContractorCode -->
                                    </tr>
                                    <tr>
										<% 	List<String> empEmploymentTypeValidList = hmValidationFields.get("EMP_EMPLOYMENT_TYPE"); 
											validReqOpt = "";
											validAsterix = "";
											if(uF.parseToBoolean(empEmploymentTypeValidList.get(0))) {
												validReqOpt = "validate[required]";
												validAsterix = "<sup>*</sup>";
											}
										%>
										<td class="txtlabel alignRight">Employee Type:<%=validAsterix %></td><td>
											<% if(uF.parseToBoolean(empEmploymentTypeValidList.get(0))) { %>
											<s:select name="empType" cssClass="validateRequired" listKey="empTypeId" listValue="empTypeName" headerKey="" 
												headerValue="Select Employee Type" list="empTypeList"/>  <!-- onchange="validateMandatory(this.options[this.options.selectedIndex].value)" -->
											<% } else { %>
											<s:select name="empType" listKey="empTypeId" listValue="empTypeName" headerKey="" headerValue="Select Employee Type" 
												list="empTypeList"/>  <!-- onchange="validateMandatory(this.options[this.options.selectedIndex].value)" -->
											<% } %>
											<span class="hint">Employment type as part time or full time. It will be used while calculating payroll.<span class="hint-pointer">&nbsp;</span></span>
										</td>
									</tr>
			
                                    <tr>
                                        <% 	List<String> empCodeValidList = hmValidationFields.get("EMP_CODE"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empCodeValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight"><input type="hidden" name="empCodeValidReqOpt" id="empCodeValidReqOpt" value="<%=validReqOpt %>" />
                                            <span id="empORContractorSpan">Employee</span>&nbsp;Code:<%=validAsterix %>
                                        </td>
                                        <td id="empCodeTD">
                                            <s:hidden name="autoGenerate"/>
                                            <s:if test="autoGenerate==true">
                                                <input type="text" name="empCodeAlphabet" id="empCodeAlphabetDis" style="width:98px !important; display: inline;" value="<%=uF.showData((String)request.getAttribute("empCodeAlphabet"), "") %>" disabled="disabled"/> <%-- class="<%=validReqOpt %>" --%>
                                                <s:hidden name="empCodeAlphabet" id="empCodeAlphabet"/>
                                                <input type="text" name="empCodeNumber" id="empCodeNumber" onchange="checkCodeValidation()" class="<%=validReqOpt %>" style="width:98px !important; display: inline;" value="<%=uF.showData((String)request.getAttribute("empCodeNumber"), "") %>" onkeypress="return isOnlyNumberKey(event)"/>
                                                <%-- <s:hidden name="empCodeNumber"/> --%>
                                                <div id="empCodeMessege"></div>
                                            </s:if>
                                            <s:else>
                                                <input type="text" name="empCodeAlphabet" id="empCodeAlphabet" onchange="checkCodeValidation()" style="width:98px !important; display: inline;" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empCodeAlphabet"), "") %>"/>
                                                <input type="text" name="empCodeNumber" id="empCodeNumber" onchange="checkCodeValidation()" style="width:98px !important; display: inline;" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empCodeNumber"), "") %>" onkeypress="return isOnlyNumberKey(event)"/>
                                                <div id="empCodeMessege"></div>
                                            </s:else>
                                            <span class="hint">Employee Code represents the employee in a company. Code can be in any format e.g. KT001, E01, etc<span class="hint-pointer">&nbsp;</span></span>
                                        </td>
                                    </tr>
                                            <s:fielderror >
                                                <s:param>empStartDate</s:param>
                                            </s:fielderror>

                                    <tr>
                                        <% 	List<String> empJoiningDateValidList = hmValidationFields.get("EMP_JOINING_DATE"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empJoiningDateValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                        %>
                                        <td class="txtlabel alignRight">Joining Date:<%=validAsterix %></td>
                                        <td><input type="text" name="empStartDate" id="empStartDate" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empStartDate"), "") %>"  /><span class="hint">Employee's date of joining.<span class="hint-pointer">&nbsp;</span></span></td>
                                    </tr>
                                    <tr>
                                        <td colspan=2>
                                            <strong>Structure:</strong>
                                            <hr style="background-color: #346897; height: 1px;">
                                        </td>
                                    </tr>
                                    
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
											if(uF.parseToInt(defaultCXO) == 1) { 
										%>
											<%=uF.showData(HOD_DEPART_NAME, "") %>
										<% } %>	
										</div>
										<div id="locDivHODLbl" style="display: <%=hodDisplay %>; line-height: 15px; font-size: 11px; color: gray;">(Department is based on above filter)</div>
										</td>
									</tr>
			
                                    <%-- <tr>
                                        <td class="txtlabel alignRight" valign="top">Is this employee a CXO:</td>
                                        <td>
                                            <s:radio name="isCXO" id="isCXO" list="#{'1':'Yes','0':'No'}" value="defaultCXO" cssClass="validateRequired" onclick="showCurrentOrgLoaction();"/>
                                            <div id="locDivCXO">
                                                <% 
                                                    String defaultCXO = (String)request.getAttribute("defaultCXO");
                                                    if(uF.parseToInt(defaultCXO) == 1) { %>
                                                	<s:select  name="locationCXO" id="locationCXO" theme="simple" cssStyle="height: auto !important;" listKey="wLocationId" listValue="wLocationName" list="wLocationList" 
                                                    	cssClass="validateRequired " multiple="true" value="cxoLocationAccess"/>
                                                <% } %>	
                                            </div>
                                            <div style="line-height: 15px; font-size: 11px; color: gray;">(Work Locations are based on above filter)</div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight" valign="top">Is this employee a HOD:</td>
                                        <td>
                                            <s:radio name="isHOD" id="isHOD" list="#{'1':'Yes','0':'No'}" value="defaultHOD" cssClass="validateRequired" />
                                            <!-- onclick="showCurrentOrgSelectedDepartment();" -->
                                            <div id="locDivHOD">
                                                <%	String defaultHOD = (String)request.getAttribute("defaultHOD");
                                                    String HOD_DEPART_NAME = (String)request.getAttribute("HOD_DEPART_NAME");
                                                    if(uF.parseToInt(defaultCXO) == 1) {
                                                    %>
                                                <%=uF.showData(HOD_DEPART_NAME, "") %>
                                                <% } %>
                                            </div>
                                            <div style="line-height: 15px; font-size: 11px; color: gray;">(Department is based on above filter)</div>
                                        </td>
                                    </tr> --%>
                                    
                                    <tr>
                                        <td colspan=2>
                                            <strong>Reporting Structure:</strong>
                                            <hr style="background-color: #346897; height: 1px;">
                                        </td>
                                    </tr>
                                   
                                    <tr>
                                        <% 	List<String> empHODValidList = hmValidationFields.get("EMP_HOD"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empHODValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">H.O.D.:<%=validAsterix %>
                                            <input type="hidden" name="hodValidReq" id="hodValidReq" value="<%=validReqOpt %>"/>
                                        </td>
                                        <td id="hodListID">
                                            <% if(uF.parseToBoolean(empHODValidList.get(0))) { %>
                                            <s:select name="hod" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired " headerValue="Select H.O.D."
                                                list="HodList" key="" required="true" />
                                            <% } else { %>
                                            <s:select name="hod" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="Select H.O.D." list="HodList" />
                                            <% } %>	
                                            <span class="hint">Employee's H.O.D. as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <% 	List<String> empHRValidList = hmValidationFields.get("EMP_HR"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empHRValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">HR:<%=validAsterix %>
                                            <input type="hidden" name="hrValidReq" id="hrValidReq" value="<%=validReqOpt %>"/>
                                        </td>
                                        <td id="hrListID">
                                            <% if(uF.parseToBoolean(empHRValidList.get(0))) { %>	
                                            <s:select name="HR" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired " headerValue="Select HR"
                                                list="HRList" key="" required="true" />
                                            <% } else { %>
                                            <s:select name="HR" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="Select HR" list="HRList" />
                                            <% } %>
                                            <span class="hint">Employee's HR as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
                                        </td>
                                    </tr>
                           <!-- ===start parvez date: 29-07-2022=== -->      
                                    <tr>
                                        <td colspan=2>
                                            <s:fielderror >
                                                <s:param>Manager</s:param>
                                            </s:fielderror>
                                        </td>
                                    </tr>
                                    <tr>
                                        <% 	List<String> empSupervisorValidList = hmValidationFields.get("EMP_SUPERVISOR"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empSupervisorValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Manager:<%=validAsterix %>
                                            <input type="hidden" name="supervisorValidReq" id="supervisorValidReq" value="<%=validReqOpt %>"/>
                                        </td>
                                        <td>
                                            <span id="supervisorIdsSpan">
                                                <% if(uF.parseToBoolean(empSupervisorValidList.get(0))) { %>
                                                	<%-- <s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired" 
                                                    headerValue="Select Supervisor" list="supervisorList" key="" /> --%>
                                                    <s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired" 
                                                    	headerValue="Select Manager" list="supervisorList" key="" />
                                                    <span class="hint">Employee's manager/supervisor as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
                                                <% } else { %>
                                                	<%-- <s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey=""  
                                                    headerValue="Select Supervisor" list="supervisorList" key=""/> --%>
                                                    <s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey=""  
                                                    	headerValue="Select Manager" list="supervisorList" key=""/>
                                                	<span class="hint">Employee's manager/supervisor as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>    
                                                <% } %>	
                                            </span>
                                            
                                        </td>
                                    </tr>
                            <!-- ===end parvez date: 29-07-2022=== -->  
                                    
                                    <tr><td colspan=2>Other HR Policies:<hr style="background-color: #346897; height: 1px;"></td></tr>
									<tr><td colspan=2><s:fielderror ><s:param>probationDuration</s:param></s:fielderror></td></tr>
									<tr><td class="txtlabel alignRight">Employee Status:<sup>*</sup></td>
										<td><s:radio name="empStatus" id="empStatus" list="#{'1':'PROBATION','2':'PERMANENT','4':'TEMPORARY'}"
												value="defaultStatus" cssClass="validateRequired" onclick="showEmpStatus(this.value);"/></tr>
									
									<tr id="trProbationId" style="display: none;">
									<% 	List<String> empProbationPeriodValidList = hmValidationFields.get("EMP_PROBATION_PERIOD"); 
										validReqOpt = "";
										validAsterix = "";
										if(uF.parseToBoolean(empProbationPeriodValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>"; 
										}
									%>
									<td class="txtlabel alignRight">Probation Period:<%=validAsterix %></td>
									<td>
									<% if(uF.parseToBoolean(empProbationPeriodValidList.get(0))) { %>
										<s:select name="probationDuration" listKey="probationDurationID" listValue="probationDurationName" headerKey="" 
										cssClass="validateRequired" headerValue="Select Probation Period" list="probationDurationList" key="" /> 
									<% } else {%>
										<s:select name="probationDuration" listKey="probationDurationID" listValue="probationDurationName" headerKey="" 
										headerValue="Select Probation Period" list="probationDurationList" key="" /> 
									<% } %>
									<span class="hint">This field is used for the Probation Period of Employee.<span class="hint-pointer">&nbsp;</span></span></td></tr>
									<tr>
									<% 	List<String> empNoticePeriodValidList = hmValidationFields.get("EMP_NOTICE_PERIOD"); 
										validReqOpt = "";
										validAsterix = "";
										if(uF.parseToBoolean(empNoticePeriodValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>";
										}
									%>
									<td class="txtlabel alignRight">Notice Period:<%=validAsterix %></td>
									<td>
									<% if(uF.parseToBoolean(empNoticePeriodValidList.get(0))) { %>	
										<s:select name="noticeDuration" listKey="noticeDurationID" listValue="noticeDurationName" headerKey="" cssClass="validateRequired"
										headerValue="Select Notice Period" list="noticeDurationList" key="" /> 
									<% } else { %>
										<s:select name="noticeDuration" listKey="noticeDurationID" listValue="noticeDurationName" headerKey="" 
										headerValue="Select Notice Period" list="noticeDurationList" key="" /> 
									<% } %>
									<span class="hint">This field is used for the Probation Period of Employee.<span class="hint-pointer">&nbsp;</span></span></td></tr>
									<tr>
									<% 	List<String> empPayrollDurationValidList = hmValidationFields.get("EMP_PAYCYCLE_DURATION"); 
										validReqOpt = "";
										validAsterix = "";
										if(uF.parseToBoolean(empPayrollDurationValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>";
										}
									%>
									<td class="txtlabel alignRight">Paycycle Duration:<%=validAsterix %></td>
									<td>
									<% if(uF.parseToBoolean(empPayrollDurationValidList.get(0))) { %>
										<s:select theme="simple" name="strPaycycleDuration" cssClass="validateRequired" listKey="paycycleDurationId" listValue="paycycleDurationName"  
							             list="paycycleDurationList" key="" />
							        <% } else { %>
							        	<s:select theme="simple" name="strPaycycleDuration" listKey="paycycleDurationId" listValue="paycycleDurationName"  
							             list="paycycleDurationList" key="" />
							        <% } %>
							        <span class="hint">Choose the paycycle duration. e.g., Weekly, Fortnightly, Monthly, etc. <span class="hint-pointer">&nbsp;</span></span></td>
							        </tr>
									<tr>
									<% 	List<String> empTimeValidationRequiredValidList = hmValidationFields.get("EMP_TIME_VALIDATION_REQUIRED"); 
										validReqOpt = "";
										validAsterix = "";
										if(uF.parseToBoolean(empTimeValidationRequiredValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>";
										}
									%>
									<td class="txtlabel alignRight">Roster Dependency:<%=validAsterix %></td>
									<td>
									<% if(uF.parseToBoolean(empTimeValidationRequiredValidList.get(0))) { %>
										<s:select label="Roster Dependency" name="rosterDependency" cssClass="validateRequired" listKey="approvalId" listValue="approvalName" 
										headerKey="" headerValue="Select Dependency" list="rosterDependencyList" key="" required="true" />
									<% } else { %>
										<s:select label="Roster Dependency" name="rosterDependency" listKey="approvalId" listValue="approvalName" headerKey="" 
										headerValue="Select Dependency" list="rosterDependencyList" key="" />
									<% } %>
									<span class="hint">Do you want this employee be dependent on roster entries?<span class="hint-pointer">&nbsp;</span></span></td></tr>
									<tr><td colspan=2><s:fielderror ><s:param>probationLeaves</s:param></s:fielderror></td></tr>
									<tr>
									<% 	
										List<String> empLeavesDuringProbationValidList = hmValidationFields.get("EMP_LEAVES_DURING_PROBATION"); 
										validReqOpt = "";
										validAsterix = "";
										if(uF.parseToBoolean(empLeavesDuringProbationValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>";
										}
										String leavesValidReqOpt = "";
										if(uF.parseToBoolean(empLeavesDuringProbationValidList.get(0))) {
											leavesValidReqOpt = "validateRequired";
										}
									%>
									<td class="txtlabel alignRight" valign="top">Select Leave Type:<%=validAsterix %>
										<input type="hidden" name="leavesValidReqOpt" id="leavesValidReqOpt" value="<%=leavesValidReqOpt %>"/>
									</td>
									<td id="leaveProbationListID">
									<% if(uF.parseToBoolean(empLeavesDuringProbationValidList.get(0))) { %>
										<s:select name="probationLeaves" id="probationLeaves" multiple="true" size="3" cssClass="validateRequired"  
										listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key="" onchange="getEmpLeaveBalance();"/> <!-- headerKey="" headerValue="Select Leave Types" --> 
									<% } else { %>
										<s:select name="probationLeaves" id="probationLeaves" multiple="true" size="3" listKey="leaveTypeId" listValue="leavetypeName" 
										list="leaveTypeList" key="" onchange="getEmpLeaveBalance();"/> <!-- headerKey="" headerValue="Select Leave Types"  -->
									<% } %>	
									<span class="hint">This field specifies Leaves Allowed During Probation Period of Employee.<span class="hint-pointer">&nbsp;</span></span></td></tr>
									<tr>
									<% 	List<String> empBiomatricMachineIdValidList = hmValidationFields.get("EMP_BIOMATRIC_MACHINE_ID"); 
										validReqOpt = "";
										validAsterix = "";
										if(uF.parseToBoolean(empBiomatricMachineIdValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>";
										}
									%>
									<td class="txtlabel alignRight">Biometric Machine Id:<%=validAsterix %></td>
									<td><input type="text" name="bioId" id="bioId" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("bioId"), "") %>" onchange="emailValidation1('biometricValidatorMessege','bioId',this.value,'EmailValidation.action?biometricId='+this.value);" /><span class="hint">Employee's Boimetric Machine Id<span class="hint-pointer">&nbsp;</span></span></td>
									</tr>
									<tr><td></td><td><div id="biometricValidatorMessege"></div></td></tr>
									<tr><td colspan=2>Corporate Contact:<hr style="background-color:#346897; height:1px;"></td></tr>
									<tr>
									<% 	List<String> empCorporateMobileNoValidList = hmValidationFields.get("EMP_CORPORATE_MOBILE_NO"); 
										validReqOpt = "";
										validAsterix = "";
										if(uF.parseToBoolean(empCorporateMobileNoValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>";
										}
									%>
									<td class="txtlabel alignRight">Corporate Mobile:<%=validAsterix %></td>
									<td><input type="text" name="empCorporateMobileNo"  id="empCorporateMobileNo" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empCorporateMobileNo"), "") %>" /></td></tr>
									<tr>
									<% 	List<String> empCorporateDeskValidList = hmValidationFields.get("EMP_CORPORATE_DESK"); 
										validReqOpt = "";
										validAsterix = "";
										if(uF.parseToBoolean(empCorporateDeskValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>";
										}
									%>
									<td class="txtlabel alignRight">Corporate Desk:<%=validAsterix %></td>
									<td><input type="text" name="empCorporateDesk" id="empCorporateDesk" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empCorporateDesk"), "") %>" /></td></tr>
									<tr>
									<% 	List<String> empCorporateEmailIdValidList = hmValidationFields.get("EMP_CORPORATE_EMAIL_ID"); 
										validReqOpt = "validateEmail";
										validAsterix = "";
										if(uF.parseToBoolean(empCorporateEmailIdValidList.get(0))) {
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
										if(uF.parseToBoolean(empSkypeIdValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>";
										}
									%>
									<td class="txtlabel alignRight">Corporate Skype Id:<%=validAsterix %></td>
									<td><input type="text" name="skypeId" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("skypeId"), "") %>"/><span class="hint">Employee's Skype Id<span class="hint-pointer">&nbsp;</span></span></td>
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
										if(uF.parseToBoolean(empLeaveBalanceValidList.get(0))) {
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
                                    
                                    <s:if test="operation == null">
                                        <tr>
                                            <td colspan="2" align="center">
                                            	<!-- <input type="button" class="btn btn-primary" id="btnSubmt" name="btnSubmit" value="Submit" onclick="submitForm();" /> -->
                                                <s:submit cssClass="btn btn-primary" id="btnSubmt" name="btnSubmit" value="Submit" align="center" />
                                            </td>
                                        </tr>
                                    </s:if>
                                    <s:else>
                                        <tr>
                                            <td colspan="2" align="center">
                                                <!-- <input type="button" class="btn btn-primary" id="btnSubmt" name="btnSubmit" value="Update Information" onclick="submitForm();" /> -->
                                                <s:submit cssClass="btn btn-primary" id="btnSubmt" name="btnSubmit" value="Update Information" align="center" />
                                            </td>
                                        </tr>
                                    </s:else>
                                </table>
                                
                                <script type="text/javascript">
									showEmpStatus('<%=(String) request.getAttribute("defaultStatus")%>', '');
								</script>
                            </div>
                            <div class="col-lg-4 col-md-12 col-sm-12" >
                                <table class="table" style="border:solid 5px #CCCCCC;">
                                    <tr>
                                        <td><strong>Upload employee image</strong><%=(String)request.getAttribute("empProfilePhotoValidAsterix") %></td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <%if(docRetriveLocation == null) { %>
                                            <img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border: 1px solid  #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + strImage %>" />
                                            <%} else { %>
                                            <img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border: 1px solid  #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)request.getAttribute("empId")+"/"+strImage %>" />
                                            <%} %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <% if(request.getAttribute("empProfilePhotoValidReqOpt") != null && !request.getAttribute("empProfilePhotoValidReqOpt").toString().equals("")) { %>
                                            <s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz" name="empImage" id="empImage" cssClass="validateRequired" onchange="readImageURL(this, 'profilecontainerimg');"></s:file>
                                            <% } else { %>
                                            <s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz" name="empImage" id="empImage" onchange="readImageURL(this, 'profilecontainerimg');"></s:file>
                                            <% } %>
                                            <span style="color:#868686;">Image size must be smaller than or equal to 500kb.</span>
                                        </td>
                                    </tr>
                                </table>
                                
                            </div>
                            
                            <div class="col-lg-4 col-md-12 col-sm-12">
								<table class="table" style="margin-top: 10px;border:solid 5px #CCCCCC;">
									<tr><td></td><td><strong>Upload employee cover image</strong><%=(String)request.getAttribute("empProfilePhotoValidAsterix") %></td></tr>
									<tr>
										<td></td>
										<td>
											<%if(docRetriveLocation == null) { %>
												<img height="100" width="100" class="lazy img-circle" id="profilecontainerimgCover" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + strCoverImage %>" />
											<%} else { %>
												<img height="100" width="100" class="lazy img-circle" id="profilecontainerimgCover" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+(String)request.getAttribute("empId")+"/"+strCoverImage %>" />
											<%} %>
										</td> 
									</tr>
									<tr><td></td><td>
									     <% if(request.getAttribute("empProfilePhotoValidReqOpt") != null && !request.getAttribute("empProfilePhotoValidReqOpt").toString().equals("")) { %>
									     	<s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz" name="empCoverImage" id="empCoverImage" cssClass="validateRequired" onchange="readImageURL(this, 'profilecontainerimgCover');"></s:file>
									     <% } else { %>
									     	<s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz" name="empCoverImage" id="empCoverImage" onchange="readImageURL(this, 'profilecontainerimgCover');"></s:file>
									     <% } %>
									     <span style="color:#929090;">Image size must be smaller than or equal to 500kb.</span>
									     </td>
									</tr>
								</table>
								<p>All fields marked with <sup>*</sup> are mandatory.</p>
							</div>
                            <div class="clr"></div>
                        </s:form>
                        
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>
