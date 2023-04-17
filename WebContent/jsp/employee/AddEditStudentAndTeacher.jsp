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

<%-- <jsp:include page="../employee/CustomAjaxForAddEmployee.jsp"></jsp:include> --%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<script>
   $(function(){
	   $("#userStartDate").datepicker({format: 'dd/mm/yyyy'});
	   $("#userDateOfBirth").datepicker({format: 'dd/mm/yyyy'});
	   
	   $("body").on("click","#btnSubmt",function() {
	    	$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
			$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
			$("#"+ this.form.id ).find('.validateEmailRequired').filter(':visible').attr('type','email').prop('required',true);
			$("#"+ this.form.id ).find('.validateEmail').filter(':visible').attr('type','email').prop('required',false);
	    });
   });
   
   
   $("#frmAddEditStudentAndTeacher").submit(function(e){
	   document.getElementById("btnSubmt").style.display = 'none';
	   document.getElementById("btnSubmit").value = 'Submit';
	});
   
   
   function getWlocation(val) {
	 	var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetOrgWLocationList.action?type=AEMP&OID='+val,
				cache : false,
				success : function(data) {
					document.getElementById('idOrgId').innerHTML = data;
				}
			});
		}
	}
	
	
   var empCode='<%=(String)request.getAttribute("userCode")%>';
   function checkCodeValidation() {
 	  //alert(" checkCodeValidation ...");
 	  var userId = document.getElementById("userId").value;
 	  //alert("empId ==>>> " + empId);
 	 var userCode='';
 	 if(document.getElementById('userCode')){
 		userCode = document.getElementById('userCode').value;
 	 }
 	 if(empCode == userCode) {
 		 document.getElementById('empCodeMessege').innerHTML='';
   		return;
   	}
 	 //alert("empCode 11 ==>>> " + empCode + " -- empCodeAlphabet+empCodeNumber ==>>> " + empCodeAlphabet+empCodeNumber);
	   	var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "EmailValidation.action?empCodeAlphabet=" +userCode+"&empId="+ userId,
				cache : false,
				success : function(data) {
					document.getElementById('empCodeMessege').innerHTML = data;
					if(data.length>1){
						document.getElementById('userCode').value='';
					}
				}
			});
		}
   }
   
	
	function getUsertypewiseData(usertypeId) {
	   if(usertypeId == '3') {
		   document.getElementById("userDateOfBirthTR").style.display = 'table-row';
		   document.getElementById("fatherNameTR").style.display = 'table-row';
		   document.getElementById("motherNameTR").style.display = 'table-row';
		   document.getElementById("userCodeTR").style.display = 'table-row';
		   document.getElementById("userJoiningDateTR").style.display = 'table-row';
	   } else {
		   document.getElementById("userDateOfBirthTR").style.display = 'none';
		   document.getElementById("fatherNameTR").style.display = 'none';
		   document.getElementById("motherNameTR").style.display = 'none';
		   document.getElementById("userCodeTR").style.display = 'none';
		   document.getElementById("userJoiningDateTR").style.display = 'none';
	   }
   }
    
</script>


<%
    String struserType = (String)session.getAttribute(IConstants.USERTYPE);
    String sessionUserId = (String)session.getAttribute(IConstants.EMPID);
    
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    String strImage = (String) request.getAttribute("strImage");
    String strCoverImage = (String) request.getAttribute("strCoverImage");
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    String userType = (String) request.getAttribute("userType");
    String dispBlock = "none";
    if(uF.parseToInt(userType)==3) {
    	dispBlock = "table-row";
    }
    %>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title">
                        <%if(session.getAttribute(IConstants.USERID)!=null){ %>
                        <span><%=(request.getParameter("operation")!=null) ? "Edit" : "Enter" %> user detail</span>
                        <%}else { %>
                        <span><%=(request.getParameter("operation")!=null) ? "Edit" : "Enter" %> your detail</span>
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
                        <s:form theme="simple" action="AddEditStudentAndTeacher" name="frmAddEditStudentAndTeacher" id="frmAddEditStudentAndTeacher" method="POST" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkImageSize();">
                            <div class="col-lg-7 col-md-12 col-sm-12">
                                <table border="0" class="table table_no_border form-table">
                                    <s:hidden name="userId" id="userId" />
                                    <s:hidden name="operation" id="operation" />
                                    <%-- <tr>
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
                                    </tr> --%>
                                    <tr>
                                        <td class="txtlabel alignRight">Usertype:<sup>*</sup></td>
                                        <td>
											<s:select label="Select Usertype" name="userType" id="userType" cssClass="validateRequired" listKey="userTypeId" listValue="userTypeName" 
												headerKey="" headerValue="Select Usertype" list="userTypeList" key="" onchange="getUsertypewiseData(this.value);"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">First Name:<%=(String)request.getAttribute("empFNameValidAsterix") %></td>
                                        <td>
                                            <input type="text" name="userFname" id="userFname" class="<%=(String)request.getAttribute("empFNameValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("userFname"), "") %>"/>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td class="txtlabel alignRight">Middle Name:<%=(String)request.getAttribute("empMNameValidAsterix") %></td>
                                        <td>
                                            <input type="text" name="userMname" id="userMname" class="<%=(String)request.getAttribute("empMNameValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("userMname"), "") %>"/>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td class="txtlabel alignRight">Last Name:<%=(String)request.getAttribute("empLNameValidAsterix") %></td>
                                        <td>
                                            <input type="text" name="userLname" id="userLname" class="<%=(String)request.getAttribute("empLNameValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("userLname"), "") %>"/>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td class="txtlabel alignRight">Personal Email Id:<%=(String)request.getAttribute("empPersonalEmailIdValidAsterix") %></td>
                                        <td>
                                            <input type="text" name="userEmail" id="userEmail" class="<%=(String)request.getAttribute("empPersonalEmailIdValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("userEmail"), "") %>"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">Gender:<sup>*</sup></td>
                                        <td>
                                            <s:select theme="simple" label="Select Gender" name="userGender" listKey="genderId" listValue="genderName" headerKey="" headerValue="Select Gender" list="empGenderList" cssClass="validateRequired"/>
                                        </td>
                                    </tr>
                                    
                                    <tr id="userDateOfBirthTR" style="display: <%=dispBlock %>;">
										<td class="txtlabel alignRight">Date Of Birth:<%=(String)request.getAttribute("empDateOfBirthValidAsterix") %></td><td>
											<input type="text" name="userDateOfBirth" id="userDateOfBirth" class="<%=(String)request.getAttribute("empDateOfBirthValidReqOpt") %>" value="<%=uF.showData((String)request.getAttribute("userDateOfBirth"), "") %>"/>
										</td>
									</tr>
									<tr id="fatherNameTR" style="display: <%=dispBlock %>;">
										<% 	List<String> empFatherNameValidList = hmValidationFields.get("EMP_FATHER_NAME"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empFatherNameValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                        }
                                        %>
                                        <td class="txtlabel alignRight">Father Name:<%=validAsterix %></td>
                                        <td>
                                            <input type="text" name="userFatherName" id="userFatherName" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("userFatherName"), "") %>"/>
                                        </td>
                                    </tr>
                                    
                                    <tr id="motherNameTR" style="display: <%=dispBlock %>;">
                                    	<% 	List<String> empMotherNameValidList = hmValidationFields.get("EMP_MOTHER_NAME"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empMotherNameValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                        }
                                        %>
                                        <td class="txtlabel alignRight">Mother Name:<%=validAsterix %></td>
                                        <td>
                                            <input type="text" name="userMotherName" id="userMotherName" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("userMotherName"), "") %>"/>
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
												<s:select label="Select Organisation" name="orgId" id="orgId" cssClass="validateRequired" listKey="orgId" listValue="orgName" 
												headerKey="" headerValue="Select Organisation" list="orgList" key="" onchange="getWlocation(this.value);"/>
											<% } else { %>
												<s:select label="Select Organisation" name="orgId" id="orgId" listKey="orgId" listValue="orgName" 
												headerKey="" headerValue="Select Organisation" list="orgList" key="" onchange="getWlocation(this.value);"/>
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
										</td>
									</tr>
									
                                    <tr id="userCodeTR" style="display: <%=dispBlock %>;">
                                        <% 	List<String> empCodeValidList = hmValidationFields.get("EMP_CODE"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empCodeValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight"><input type="hidden" name="empCodeValidReqOpt" id="empCodeValidReqOpt" value="<%=validReqOpt %>" />
                                            <span id="empORContractorSpan">Student</span>&nbsp;Code:<%=validAsterix %>
                                        </td>
                                        <td id="empCodeTD">
                                            <input type="text" name="userCode" id="userCode" onchange="checkCodeValidation()" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("userCode"), "") %>"/>
                                            <div id="empCodeMessege"></div>
                                        </td>
                                    </tr>

                                    <tr id="userJoiningDateTR" style="display: <%=dispBlock %>;">
                                        <% 	List<String> empJoiningDateValidList = hmValidationFields.get("EMP_JOINING_DATE"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empJoiningDateValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                        %>
                                        <td class="txtlabel alignRight">Joining Date:<%=validAsterix %></td>
                                        <td><input type="text" name="userStartDate" id="userStartDate" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("userStartDate"), "") %>"  /><span class="hint">Employee's date of joining.<span class="hint-pointer">&nbsp;</span></span></td>
                                    </tr>
                                    
                                    <tr id="hodTR" style="display: none;">
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
                                        </td>
                                    </tr>
                                    
                                    <tr id="supervisorTR" style="display: none;">
                                        <% 	List<String> empSupervisorValidList = hmValidationFields.get("EMP_SUPERVISOR"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(uF.parseToBoolean(empSupervisorValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Supervisor:<%=validAsterix %>
                                            <input type="hidden" name="supervisorValidReq" id="supervisorValidReq" value="<%=validReqOpt %>"/>
                                        </td>
                                        <td>
                                            <span id="supervisorIdsSpan">
                                                <% if(uF.parseToBoolean(empSupervisorValidList.get(0))) { %>
                                                	<s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired" 
                                                    headerValue="Select Supervisor" list="supervisorList" key="" />
                                                <% } else { %>
                                                	<s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="Select Supervisor" list="supervisorList" key=""/>
                                                <% } %>	
                                            </span>
                                        </td>
                                    </tr>
                                    
                                    <tr>
										<% 	List<String> empSkypeIdValidList = hmValidationFields.get("EMP_SKYPE_ID"); 
											validReqOpt = "";
											validAsterix = "";
											if(uF.parseToBoolean(empSkypeIdValidList.get(0))) {
												validReqOpt = "validateRequired";
												validAsterix = "<sup>*</sup>";
											}
										%>
										<td class="txtlabel alignRight">Skype Id:<%=validAsterix %></td>
										<td><input type="text" name="skypeId" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("skypeId"), "") %>"/><span class="hint">Employee's Skype Id<span class="hint-pointer">&nbsp;</span></span></td>
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
                            </div>

                            <div class="col-lg-4 col-md-12 col-sm-12" >
                                <table class="table" style="border:solid 3px #CCCCCC;">
                                    <tr>
                                        <td><strong>Upload user image</strong><%=(String)request.getAttribute("empProfilePhotoValidAsterix") %></td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <%if(docRetriveLocation == null) { %>
                                            <img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border: 1px solid  #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + strImage %>" />
                                            <%} else { %>
                                            <img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border: 1px solid  #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)request.getAttribute("userId")+"/"+strImage %>" />
                                            <%} %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <% if(request.getAttribute("empProfilePhotoValidReqOpt") != null && !request.getAttribute("empProfilePhotoValidReqOpt").toString().equals("")) { %>
                                            <s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz" name="userImage" id="userImage" cssClass="validateRequired" onchange="readImageURL(this, 'profilecontainerimg');"></s:file>
                                            <% } else { %>
                                            <s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz" name="userImage" id="userImage" onchange="readImageURL(this, 'profilecontainerimg');"></s:file>
                                            <% } %>
                                            <span style="color:#868686;">Image size must be smaller than or equal to 500kb.</span>
                                        </td>
                                    </tr>
                                </table>
                                
                            </div>
                            
                            <div class="col-lg-4 col-md-12 col-sm-12">
								<table class="table" style="margin-top: 10px;border:solid 3px #CCCCCC;">
									<tr><td></td><td><strong>Upload user cover image</strong><%=(String)request.getAttribute("empProfilePhotoValidAsterix") %></td></tr>
									<tr>
										<td></td>
										<td>
											<%if(docRetriveLocation == null) { %>
												<img height="100" width="100" class="lazy img-circle" id="profilecontainerimgCover" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + strCoverImage %>" />
											<%} else { %>
												<img height="100" width="100" class="lazy img-circle" id="profilecontainerimgCover" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+(String)request.getAttribute("userId")+"/"+strCoverImage %>" />
											<%} %>
										</td> 
									</tr>
									<tr><td></td><td>
									     <% if(request.getAttribute("empProfilePhotoValidReqOpt") != null && !request.getAttribute("empProfilePhotoValidReqOpt").toString().equals("")) { %>
									     	<s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz" name="userCoverImage" id="userCoverImage" cssClass="validateRequired" onchange="readImageURL(this, 'profilecontainerimgCover');"></s:file>
									     <% } else { %>
									     	<s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz" name="userCoverImage" id="userCoverImage" onchange="readImageURL(this, 'profilecontainerimgCover');"></s:file>
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
