<%@page import="com.konnect.jpms.select.FillYears"%>
<%@page import="com.konnect.jpms.select.FillDegreeDuration"%>
<%@page import="com.konnect.jpms.recruitment.FillEducational"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%> 
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ taglib prefix="s" uri="/struts-tags" %> 
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<script>
// perform JavaScript after the document is scriptable.
$(function() {
	// setup ul.tabs to work as tabs for each div directly under div.panes 
	$("ul.tabs").tabs("div.panes > div");
	
	/* $("#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy'}); */
	
});


function checkEmpORContractorCode(val) {
	  var empORContractor = '<%=(String)request.getAttribute("EMP_OR_CONTRACTOR")%>';
	  var empCodeAlpha = '<%=(String)request.getAttribute("EMP_CODE_ALPHA")%>';
	  var empCodeNum = '<%=(String)request.getAttribute("EMP_CODE_NUM")%>';
	 //var empCodeNumber= document.getElementById('empCodeNumber').value;
	 
	 if(val == empORContractor) {
		if(empCodeAlpha == null || empCodeAlpha == 'null') {
			empCodeAlpha = '';
		}
		if(empCodeNum == null || empCodeNum == 'null') {
			empCodeNum = '';
 		}
		if(val == 1) {
			document.getElementById('empORContractorSpan').innerHTML = "Employee";
		} else if(val == 2) {
			document.getElementById('empORContractorSpan').innerHTML = "Contractor";
		}
		
		document.getElementById('empCodeAlphabet').value = empCodeAlpha;
		if(document.getElementById('empCodeAlphabetDis')) {
			document.getElementById('empCodeAlphabetDis').value = empCodeAlpha;
		}
		document.getElementById('empCodeNumber').value = empCodeNum;
		return;
	}

	 	if(val == 1) {
			document.getElementById('empORContractorSpan').innerHTML = "Employee";
		} else if(val == 2) {
			document.getElementById('empORContractorSpan').innerHTML = "Contractor";
		}
	 	createEmpCodeByOrg();
	 
	 /* var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "EmailValidation.action?strEmpOrContractor="+ val +"&empId="+ empId,
				cache : false,
				success : function(data) {
					//alert("data ===>> " +data.length + " --- " + data);
					if(data.trim() == "") {
						if(val == 1) {
			    			document.getElementById('empORContractorSpan').innerHTML = "Employee";
			    		} else if(val == 2) {
			    			document.getElementById('empORContractorSpan').innerHTML = "Contractor";
			    		}
						document.getElementById('empCodeAlphabet').value = '';
						document.getElementById('empCodeNumber').value = '';
						
           	} else {
					var allData = data.split("::::");
						document.getElementById('empORContractorSpan').innerHTML = allData[0];
						document.getElementById('empCodeAlphabet').value = allData[1];
						if(document.getElementById('empCodeAlphabetDis')) {
							document.getElementById('empCodeAlphabetDis').value = allData[1];
						}
						document.getElementById('empCodeNumber').value = allData[2];
					}
				}
			});
		} */
}


var empCode='<%=(String)request.getAttribute("EMP_CODE")%>';
function checkCodeValidation() {
	  //alert(" checkCodeValidation ...");
	  var empId = document.getElementById("empId").value;
	  //alert("empId ==>>> " + empId);
	 var empCodeAlphabet='';
	 if(document.getElementById('empCodeAlphabet')){
		 empCodeAlphabet =document.getElementById('empCodeAlphabet').value;
	 }
	 var empCodeNumber= document.getElementById('empCodeNumber').value;
	 //alert("empCode ==>>> " + empCode + " -- empCodeAlphabet+empCodeNumber ==>>> " + empCodeAlphabet+empCodeNumber);
	 if(empCode==(empCodeAlphabet+empCodeNumber)){
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
				url : "EmailValidation.action?empCodeAlphabet=" +empCodeAlphabet+empCodeNumber+"&empId="+ empId,
				cache : false,
				success : function(data) {
					document.getElementById('empCodeMessege').innerHTML=data;
					if(data.length>1){
						document.getElementById('empCodeNumber').value='';
					}
				}
			});
		}
}


var val='<%=(String)request.getAttribute("EMPLOYEE_EMAIL")%>';
function emailValidation(id,id1,val1,action){
	var empId = document.getElementById("empId").value;
	//document.getElementById("empPEmail").value = val1;
	if(val == val1) {
		document.getElementById(id).innerHTML='';
		return;
	}
	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : action+"&empId="+ empId,
				cache : false,
				success : function(data) {
					document.getElementById(id).innerHTML = data;
					if(data.length > 1) {
						document.getElementById(id1).value = '';
						//document.getElementById("empPEmail").value = '';
					}
				}
			});
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

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
        		<div class="box-header with-border">
                    <h4 class="box-title">
                    	<%if(session.getAttribute(IConstants.USERID)!=null){ %>
						      <%=(request.getParameter("operation")!=null ||(request.getParameter("mode")!=null && request.getParameter("mode").equals("onboard"))) ? "Edit" : "Enter" %> Resource Detail
						<%}else { %>
							  <%=(request.getParameter("operation")!=null ||(request.getParameter("mode")!=null && request.getParameter("mode").equals("onboard"))) ? "Edit" : "Enter" %> your Details
						<%}%>
                    </h4>
                </div>
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                
			<%
				UtilityFunctions uF = new UtilityFunctions();
				CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
				List<FillSkills> skillsList = (List<FillSkills>) request.getAttribute("skillsList");
				List<List<String>> alSkills = (List<List<String>>) request.getAttribute("alSkills"); 
				List<List<String>> alEducation = (List<List<String>>) request.getAttribute("alEducation");
				if (alEducation == null)alEducation = new ArrayList<List<String>>();
				ArrayList educationalList = (ArrayList) request.getAttribute("educationalList");
				List<FillDegreeDuration> degreeDurationList = (List<FillDegreeDuration>) request.getAttribute("degreeDurationList");
				if (degreeDurationList == null)	degreeDurationList = new ArrayList<FillDegreeDuration>();
				List<FillYears> yearsList = (List<FillYears>) request.getAttribute("yearsList");
				if (yearsList == null)yearsList = new ArrayList<FillYears>();
				String currentYear = (String) request.getAttribute("currentYear");
				
				String strImage = (String) request.getAttribute("strImage");
				String strEmpType = (String) session.getAttribute("BASEUSERTYPE");
				String strMessage = (String) request.getAttribute("MESSAGE");
				if (strMessage == null) {
					strMessage = "";
				}
				
				int nEmpAlphaCodeLength = 2;
				if (CF != null && CF.getStrOEmpCodeAlpha() != null) {
					nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();
				}
				
				//Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
				//String validReqOpt = "";
				//String validAsterix = "";
			%>
			<%if(!"U".equalsIgnoreCase(request.getParameter("operation")) && request.getParameter("mode")!=null && request.getParameter("mode").equals("onboard")) { %>
				<div class="steps">
					<s:if test="step==1">
					  <span class="current">Resource Information :</span>
					  <span class="next"><a href="AddPeople.action?mode=onboard&step=1&operation=EO&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Official Information :</a></span>
					</s:if>
					<s:if test="step==2">
					  <span class="prev"><a href="AddPeople.action?mode=onboard&step=0&operation=EO&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Resource Information :</a></span>
					  <span class="current">Official Information :</span>
					</s:if>
				</div>
			<%} else if(!"U".equalsIgnoreCase(request.getParameter("operation"))) { %>
				<div class="steps">
					<s:if test="step==1">
					  <span class="current">Resource Information :</span>
					  <span class="next">Official Information :</span>
					</s:if>
					<s:if test="step==2">
					  <span class="prev"><a href="AddPeople.action?mode=onboard&step=0&operation=EO&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Resource Information :</a></span>
					  <span class="current">Official Information :</span>
					</s:if>
				</div>
			<%} %>
	
			<p class="message"><%=strMessage%></p>

			<ul class="tabs">
			
				<s:if test="step==1 || mode=='report'">
					<li><a href="#tab1">Resource Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
				</s:if>
				
				<s:if test="step==2 || mode=='report'">
					<li><a href="#tab4">Official Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
				</s:if>
						
			</ul>
	
			<div class="panes">
				<s:if test="step==1 || mode=='report'">
					<script>
					
					$(document).ready( function () {
						$("#btnSubmit").click(function(){
			    			$(".validateRequired").prop('required',true);
			    			$("#empEmail").prop('type','email');
			    			$(".validateNumber").prop('type','number');
			    			$(".validateNumber").prop('step','any');
			    			
			    		});
			    		$("#btnUpdate").click(function(){
			    			$(".validateRequired").prop('required',true);
			    			$("#empEmail").prop('type','email');
			    			$(".validateNumber").prop('type','number');
			    			$(".validateNumber").prop('step','any');
			    			
			    		});
			    	  
			    	  	<%-- $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy',yearRange: '<%=uF.parseToInt(currentYear)-80%>:<%=uF.parseToInt(currentYear)-14%>', changeYear: true}); --%>
			    	  
			    	  	$( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy'});
			    	  	
			    	  	var country = document.getElementById("country").value;
			    		var state = document.getElementById("state").value;
			    		getContentAcs('stateTD1','GetStates.action?country='+country+'&type=PADD&validReq=1&state='+state);
			    	  
					});
					
				      var skillcnt =0;
				      <% if (alSkills!=null) {%>
				      	 skillcnt=<%=alSkills.size()%>;
				      <%}%>
		
				      function addSkills() {
				      	
				      	skillcnt++;
				      	var divTag = document.createElement("div");
				          divTag.id = "row_skill"+skillcnt;
				          divTag.setAttribute("class", "row_skill");
				      	divTag.innerHTML = 	"<%=request.getAttribute("sbSkills")%>" +
				          			    	"<td><a href=\"javascript:void(0)\" onclick=\"addSkills()\" class=\"fa fa-fw fa-plus\">&nbsp;</a></td>" +
				          			    	"<td><a href=\"javascript:void(0)\" onclick=\"removeSkills(this.id)\" id=\""+skillcnt+"\" class=\"fa fa-fw fa-remove\">&nbsp;</a></td></tr></table>"; 
				          document.getElementById("div_skill").appendChild(divTag);
				          
				      }
		
				      function removeSkills(removeId) {
				      	
				      	var remove_elem = "row_skill"+removeId;
				      	var row_skill = document.getElementById(remove_elem); 
				      	document.getElementById("div_skill").removeChild(row_skill);
				      	
				      }
				      
				      var educationcnt =0;
				      <% if (alEducation!=null && !alEducation.isEmpty()) {%>
				      	educationcnt=<%=alEducation.size()%>;
				      <%}
		
				      String sbdegreeDuration=(String)request.getAttribute("sbdegreeDuration");
				      %>
		
		
				      function addEducation() {
				      	
				      	educationcnt++;
				      	var divTag = document.createElement("div");
				          divTag.id = "row_education"+educationcnt;
				          divTag.setAttribute("class", "row_education");
				          divTag.innerHTML = "<table class=\"table form-table\"><tr><td><select name=\"degreeName\" style=\"width:110px !important;\" onchange=\"checkEducation(this.value,"+educationcnt+")\"> "+
				      					 "<%=request.getAttribute("sbdegreeDuration")%>" +
				      			"<td><a href=\"javascript:void(0)\" onclick=\"addEducation()\" id=\""+educationcnt+"\" class=\"fa fa-fw fa-plus\" >&nbsp;</a></td>"
				      			+"<td><a href=\"javascript:void(0)\" onclick=\"removeEducation(this.id)\" id=\""+educationcnt+"\" class=\"fa fa-fw fa-remove\" >&nbsp;</a></td></tr>" +
				       			" <tr id=\"degreeNameOtherTR"+educationcnt+"\"  style=\"display:none;\"><td style=\"text-align: right;\">Enter Education :</td><td colspan=\"3\"> " +
				      			"<input type=\"text\" name=\"degreeNameOther\" style=\"height: 25px;\"></td></tr>"+ 
				      			"</table>"; 
				          document.getElementById("div_education").appendChild(divTag);
				      }
		
				      function checkEducation(value,count){
				      	if(value=="other"){
				      		document.getElementById("degreeNameOtherTR"+count).style.display="table-row";
				      	}
				      }
		
				      function removeEducation(removeId) {
				      	var remove_elem = "row_education"+removeId;
				      	var row_education = document.getElementById(remove_elem); 
				      	document.getElementById("div_education").removeChild(row_education);
				      } 
				      
				      function readImageURL(input, targetDiv) {
				  	    if (input.files && input.files[0]) {
				  	        var reader = new FileReader();
				  	        reader.onload = function (e) {
				  	            $('#'+targetDiv)
				  	                .attr('src', e.target.result)
				  	                .width(100)
				  	                .height(100);
				  	        };
				  	        reader.readAsDataURL(input.files[0]);
				  	    }
				  	}
				      
			      </script>
					<div>
						<s:form theme="simple" action="AddPeople" name="frmPersonalInfo" id="frmPersonalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkImageSize();">
							<div style="float: left; width: 54%;" >
								<table class="table form-table">
									<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px; padding:5px;">
							            Step 1 : </span> Enter Resource Information
							            <s:hidden name="empId" id="empId" />
							            <s:hidden name="step" />
						            </td></tr>
									<tr><td colspan=2>Personal Information:<hr style="background-color:#346897; height:1px;"></td></tr>
									<tr>
										<td class="txtlabel alignRight">Salutation:<sup>*</sup></td>
										<td>
											<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
												listKey="salutationId" listValue="salutationName" cssClass="validateRequired"/>
										</td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">First Name:<sup>*</sup></td>
										<td>
											<s:textfield name="empFname" id="empFname" cssClass="validateRequired"/>
										</td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Middle Name:</td>
										<td>
											<s:textfield name="empMname" id="empMname"/>
										</td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Last Name:<sup>*</sup></td>
										<td>
											<s:textfield  name="empLname" id="empLname" cssClass="validateRequired"/>
										</td>   
									</tr>
									
									<tr>
										<td class="txtlabel alignRight">Date Of Birth:<sup>*</sup></td><td>
										<s:textfield name="empDateOfBirth" id="empDateOfBirth" cssClass="validateRequired" readonly="readonly" />
										</td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Gender:<sup>*</sup></td>
										<td>
											<s:select theme="simple" label="Select Gender" name="empGender" listKey="genderId"
												listValue="genderName" headerKey="" headerValue="Select Gender" list="empGenderList" cssClass="validateRequired"/>
										</td>
									</tr>
									
									<tr><td colspan=2 style="border-bottom:1px solid #346897">Contact Information:</td></tr>							
									<tr>
										<td class="txtlabel alignRight">Corporate Mobile:</td>
										<td><s:textfield name="corporateMobile" id="corporateMobile"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Corporate Desk:</td>
										<td><s:textfield name="corporateDesk" id="corporateDesk"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Corporate e-mail:</td>
										<td><s:textfield name="corporateEmail" id="corporateEmail"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Corporate Skype:</td>
										<td><s:textfield name="corporateSkype" id="corporateSkype"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Personal Mobile:<sup>*</sup></td>
										<td><s:textfield name="personalMobile" id="personalMobile" cssClass="validateRequired"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Residence No:</td>
										<td><s:textfield name="residenceNo" id="residenceNo"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Personal Email Id:<sup>*</sup></td>
										<td>
											<s:textfield name="empEmail" id="empEmail" cssClass="validateRequired" onchange="emailValidation('emailValidatorMessege','empEmail',this.value, 'EmailValidation.action?email='+this.value);"/>
											<div id="emailValidatorMessege"></div>
										</td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">House No.:</td>
										<td><s:textfield name="houseNo" id="houseNo"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Street Address:<sup>*</sup></td>
										<td><s:textfield name="streetAddress" id="streetAddress" cssClass="validateRequired"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Suburb:<sup>*</sup></td>
										<td><s:textfield name="suburb" id="suburb" cssClass="validateRequired"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">City:<sup>*</sup></td>
										<td><s:textfield name="city" id="city" cssClass="validateRequired"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Country:<sup>*</sup></td>
										<td>
											<s:select id="country" name="country" listKey="countryId" listValue="countryName" headerKey="" cssClass="validateRequired"
												headerValue="Select Country" onchange="getContentAcs('stateTD1','GetStates.action?country='+this.value+'&type=PADD&validReq=1');" list="countryList" />
										</td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">State:<sup>*</sup></td>
										<td id="stateTD1">
											<s:select theme="simple" id="state" name="state" listKey="stateId" listValue="stateName" headerKey="" 
												headerValue="Select State" list="stateList" cssClass="validateRequired"/>
										</td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Pin code:</td>
										<td><s:textfield name="pincode" id="pincode" onkeypress="return isNumberKey(event)"/></td>
									</tr>
									
									<tr><td colspan=2 style="border-bottom:1px solid #346897">Skill Sets:</td></tr>
									<tr>
										<td colspan=2>
											<div id="div_skill">
												<div id="row_skill" >
													<div style="float:left; width:46%; margin-left:50px"><label>Skill Name</label></div>
													<div style="float:left; width:45%;"><label>Skill Rating</label></div>
												</div>
												<% 	
													if(alSkills!=null && alSkills.size()!=0) {
														String empId = alSkills.get(0).get(3);
													 	for(int i=0; i<alSkills.size(); i++) {
													 		List<String> innerList=alSkills.get(i);
												%>
														<div id="row_skill<%=i%>" class="row_skill">
										                    <table class="table form-table">
																<tr>
																	<td>
																	<%if(i==0) { %>
																		[PRI]
																	<% } else { %>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																	<% } %>
																		<select name="skillName" id="skillName<%=i %>">
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
																	<td> 
																		<select name="skillValue" id="skillValue<%=i %>">
																		<option value="">Select Skill Rating</option>
														                	<%for(int k=1; k< 11; k++) { 
														                		if( (k+"").equals(innerList.get(2))) {
														                	%>
														                		<option value="<%=k%>" selected="selected"> <%=k%> </option>
														                	<%}else {%>
														                		<option value="<%=k%>"> <%=k%> </option>
														                	<% }
														                	}%>
													                	</select>
														    	    </td>
														    	    <td><a href="javascript:void(0)" onclick="addSkills()" class="fa fa-fw fa-plus">&nbsp;</a></td>
														            <%if(i>0){ %>
														            <td><a href="javascript:void(0)" onclick="removeSkills(this.id)" id=<%=i%> class="fa fa-fw fa-remove" >&nbsp;</a></td>
														            <% } %>
											                     </tr>   
													    	</table>
													    </div>
												 <%}
												 }else {
												 %>
												 	<div id="row_skill" class="row_skill">
									                    <table class="table form-table">
									                        <tr>
									                        	<td>
									                        	[Pri]
										                        	<select name="skillName" id="skillName0">
										                        		<option value="">Select Skill</option>
													                	<%for(int k=0; k< skillsList.size(); k++) {
													                		FillSkills fillSkills=skillsList.get(k);
													                	%> 
													                		<option value="<%=fillSkills.getSkillsId() %>"> <%=fillSkills.getSkillsName() %> </option>
													                	<%}%>
												                	</select>
									                        	</td>
									                        	<td>
									                        		<select name="skillValue" id="skillValue0">
										                        		<option value="">Select Skill Rating</option>
													                	<%for(int k=1; k< 11; k++) {%>
													                		<option value="<%=k%>"> <%=k%> </option>
												                		<%}%>
												                	</select>
									                        	</td>
									                            <td><a href="javascript:void(0)" onclick="addSkills()" class="fa fa-fw fa-plus">&nbsp;</a></td>
									                        </tr>   
									                    </table>    
													</div>
												 <%}%>
											</div>
										</td>
									</tr>
									
									<tr><td colspan=2 style="border-bottom:1px solid #346897">Educational Background:</td></tr>
									<tr>
										<td colspan=2>
											<div id="div_education">
												<div id="row_education" class="row_education">  
									                <table class="table form-table">
													<tr><th width="120px"><label>Degree Name</label></th>
													    <th width="120px"><label>Duration</label></th>
													    <th width="120px"><label>Completion Year</label></th>
													    <th width="120px"><label>Grade</label></th>
									                </tr> 
									                </table>   
												</div>
												<% 	
													if(alEducation!=null && alEducation.size()!=0){
												 	for(int i=0; i<alEducation.size(); i++) {
												 		List<String> innerList=alEducation.get(i);
												%>
												<div id="row_education<%=i%>" class="row_education">
								                    <table class="table form-table">    
														<tr>
															<td>
															<select name="degreeName" id="degreeName<%=i %>" style="width: 120px !important;">
											                	<%for(int k=0; k< educationalList.size(); k++) { 
											                		if( (((FillEducational)educationalList.get(k)).getEduId()+"").equals( (String)((ArrayList)alEducation.get(i)).get(0) )) {
											                	%>
											                		<option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" selected="selected"><%=((FillEducational)educationalList.get(k)).getEduName() %></option>
											                	<%} else { %>
											                		<option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" ><%=((FillEducational)educationalList.get(k)).getEduName() %></option>
											                	<% } } %>
											                	</select>
															</td>
											                <td>
											                	<select name="degreeDuration" id="degreeDuration<%=i %>" style="width: 110px !important;">
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
											                	<select name="completionYear" id="completionYear<%=i %>" style="width: 110px !important;">
											                	<option value="">Completion Year</option>
											                	<%for(int k=0; k< yearsList.size(); k++) { 
											                		FillYears fillYears = yearsList.get(k);
											                		if( (fillYears.getYearsID()+"").equals( innerList.get(3) )) {
											                	%>
											                		<option value="<%=fillYears.getYearsID() %>" selected="selected"><%=fillYears.getYearsName() %></option>
											                	<% } else { %>
											                		<option value="<%=fillYears.getYearsID() %>"><%=fillYears.getYearsName() %></option>
											                	<% } } %>
											                	</select>
											                </td>
											                <td>
											                	<input type="text" style="width: 110px !important;" name="grade" id="grade<%=i %>" value="<%=innerList.get(4)%>" /></td>
															<td><a href="javascript:void(0)" onclick="addEducation()" class="fa fa-fw fa-plus">&nbsp;</a></td>
															<% if(i>0) { %>
															<td><a href="javascript:void(0)" onclick="removeEducation(this.id)" id=<%=i%> class="fa fa-fw fa-remove" >&nbsp;</a></td>
															<% } %>
									                    </tr>
								                    </table>
												</div>
												<%}
												}else { %>
												<div id="row_education" class="row_education">
									                <table class="table form-table">
														<tr>
															<td>
																<select name="degreeName" id="degreeName0" style="width: 110px !important;" onchange="checkEducation(this.value,0);" >
																	<option value="">Degree</option>
																	<%for(int k=0; k< educationalList.size(); k++) {%> 
																	<option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" ><%=((FillEducational)educationalList.get(k)).getEduName() %></option>
													                <%} %>
													                <option value="other">Other</option> 
													            </select>
															</td>
															<td>
																<s:select name="degreeDuration"	cssStyle="width: 110px !important;" listKey="degreeDurationID" listValue="degreeDurationName" headerKey="-1"
																	headerValue="Duration" list="degreeDurationList" key="" />
															</td>
										                    <td>
																<s:select name="completionYear"	cssStyle="width: 110px !important;" listKey="yearsID" listValue="yearsName" headerKey="-1"
																	headerValue="Completion Year" list="yearsList" key="" />
															</td>
										                    <td> 
										                    	<input type="text" style="width: 110px !important;" name="grade" id="grade0"/></td>
														    <td><a href="javascript:void(0)" onclick="addEducation()" class="fa fa-fw fa-plus">&nbsp;</a></td>
										                </tr> 
										                <tr id="degreeNameOtherTR0" style="display:none;">
											                <td style="text-align:right;">Enter Education :</td>
															<td colspan="3"><input type="text" name="degreeNameOther" id="degreeNameOther0" style="height: 25px;" />
											                </td>
										                </tr>   
													</table>
												</div>
												<%} %>
											</div>
										</td>
									</tr>
									
									<tr><td colspan=2 style="border-bottom:1px solid #346897">Total Years of Experience:</td></tr>
									<tr>
										<td class="txtlabel alignRight">Relevant Years of Experience:</td>
										<td><s:textfield name="relevantExperience" id="relevantExperience" onkeypress="return isNumberKey(event)"/></td>
									</tr>
									<tr>
										<td class="txtlabel alignRight">Total Experience:</td>
										<td><s:textfield name="totalExperience" id="totalExperience" onkeypress="return isNumberKey(event)"/></td>
									</tr>
								</table>
							</div>
							<div style="float:left; width: 25%; padding:10px; border:solid 5px #CCCCCC; margin: 80px 100px; padding: 10px 30px;" >
								<table>
									<tr><td><strong>Update image</strong></td></tr>
									<tr>
										<td>
											<%if(CF.getStrDocRetriveLocation() == null) { %>
												<img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + strImage %>" />
											<%} else { %>
												<img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(Integer)request.getAttribute("empId")+"/"+strImage %>" />
											<%} %>
										</td>
									</tr>
								    <tr>
								    	<td>
									    	<s:file name="empImage" id="empImage" onchange="readImageURL(this, 'profilecontainerimg');"></s:file>
									     	<span style="color:#efefef">Image size must be smaller than or equal to 500kb.</span>
								    	</td>
								    </tr>
								</table>
							</div>
							<div class="clr"></div>
							<div style="float:right;">
								<table>
								<s:if test="mode == null">
									<tr><td colspan="2" align="center">
										<s:submit cssClass="btn btn-primary" name="btnSubmit" id="btnSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center"/>
									</td></tr>
								</s:if>
								<s:else>
									<tr><td colspan="2" align="center">
										<s:hidden name="mode" />
										<s:submit cssClass="btn btn-primary" name="btnUpdate" id="btnUpdate" value="Update Information" align="center" />
									</td></tr>
								</s:else>
								</table>
							</div>
						</s:form>
					</div>
				</s:if>
				
				<%if(strEmpType!=null && !strEmpType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
					<s:if test="step==2 || mode=='report'">
						<script>
						$(function() {
							$("#strChangeOrg").multiselect().multiselectfilter();
						});
						
						$(document).ready(function () {
							$("#btnSubmit").click(function() {
								$("#frmOfficialInfo").find('.validateRequired').filter(':hidden').prop('required',false);
								$("#frmOfficialInfo").find('.validateRequired').filter(':visible').prop('required',true);
				    			$("#empEmail").prop('type','email');
				    			$(".validateNumber").prop('type','number');
				    			$(".validateNumber").prop('step','any');
				    			
				    		});

							<%-- $("#empStartDate").datepicker({dateFormat: 'dd/mm/yyyy',yearRange: '<%=uF.parseToInt(currentYear)-80%>:<%=uF.parseToInt(currentYear)-14%>', changeYear: true}); --%>
							$( "#empStartDate" ).datepicker({format: 'dd/mm/yyyy'});
							
							var userType = document.getElementById("userType").value;
							showOrgAndWLoc(userType);
						});
					      
					    
					      function getDataFromAjax(val) {
					    	  var xmlhttp = GetXmlHttpObject();
						  		if (xmlhttp == null) {
						  			alert("Browser does not support HTTP Request");
						  			return;
						  		} else {
						  			var xhr = $.ajax({
						  				url : "GetOrgData.action?OID="+ val,
						  				cache : false,
						  				success : function(data) {
						  					if(data == "") {
					                    		
					                    	} else {
					                    		var allData = data.split("::::");
					                            document.getElementById("idWLocId").innerHTML = allData[0];
					                            document.getElementById("idService").innerHTML = allData[1];
					                           	document.getElementById("idDepartment").innerHTML = allData[2];
					                           	document.getElementById("idLevel").innerHTML = allData[3];
					                           	
					                           	getHRList(val);
					    						getHODList(val);
					    						getSupervisorList(val);
					                    	}
						  				}
						  			});
						  		}
						  		createEmpCodeByOrg();
					      }
					      
					      function getDesig(val) {
								var strOrg = document.getElementById("strOrg").value; 
								getContentAcs('myDesig', 'GetDesigList.action?strLevel='+val+'&fromPage=AddPeople');
							}
					      
					      function GetXmlHttpObject() {
								if (window.XMLHttpRequest) {
									// code for IE7+, Firefox, Chrome, Opera, Safari
									return new XMLHttpRequest();
								}
								if (window.ActiveXObject) {
									// code for IE6, IE5
									return new ActiveXObject("Microsoft.XMLHTTP");
								}
								return null;
							}
					      
					      function createEmpCodeByOrg() {
					    		var strOrg = document.getElementById("strOrg").value;
					    		var empContractor = document.getElementById("empContractor").value;
					    		var validReqOpt = document.getElementById("empCodeValidReqOpt").value;
					    		//alert("empContractor ===>> " + empContractor);
					    		getContent('empCodeTD', 'CreateEmployeeCodeByOrg.action?empContractor='+empContractor+'&strOrg='+strOrg+'&validReqOpt='+validReqOpt);
					    	}
					      
					      
					    function showOrgAndWLoc(val) {
				    		if(val == 4 || val == 5 || val == 7) {
				    			document.getElementById("orgTR").style.display = "table-row";
				    			document.getElementById("wLocTR").style.display = "table-row";
				    			getWlocation();
				    		} else {
				    			document.getElementById("orgTR").style.display = "none";
				    			document.getElementById("wLocTR").style.display = "none";
				    		}
				    	}
					      
					      function getWlocation() {
					  		 var org = $('#strChangeOrg').val();
					  		 var empId = $('#empId').val();
					  	 	 var xmlhttp = GetXmlHttpObject();
					  			if (xmlhttp == null) {
					  				alert("Browser does not support HTTP Request");
					  				return;
					  			} else {
					  				var xhr = $.ajax({
					  					url : 'GetOrgWLocationList.action?strOrgId='+org+'&type=people&empid='+empId,
					  					cache : false,
					  					success : function(data) {
					  						document.getElementById('idWlocIdUType').innerHTML = data;
					  						$("#wLocation1").multiselect().multiselectfilter();
					  					}
					  				});
					  			}
					  	   }
					      

						function showCurrentOrgSelectedDepartment(isHOD, callFrom) {
							if(isHOD == 1) {
								var departId = document.getElementById('department').value;
								var xmlhttp = GetXmlHttpObject();
								if (xmlhttp == null) {
									alert("Browser does not support HTTP Request");
									return;
								} else {
									var xhr = $.ajax({
										url : "GetOrgDepartmentName.action?departId="+departId,
										cache : false,
										success : function(data) {
											document.getElementById('locDivHOD').innerHTML = data;
										}
									});
								}
							} else {
								document.getElementById('locDivHOD').innerHTML = "";
								if(isHOD == 0) {
									document.getElementById('userTypeLblTR').style.display = 'table-row';
									document.getElementById('userTypeTR').style.display = 'table-row';
								}
							}
						}

						
						function showCurrentOrgLoaction(isCXO) { 
							if(isCXO == 1) {	
							 	var xmlhttp = GetXmlHttpObject();
								if (xmlhttp == null) {
									alert("Browser does not support HTTP Request");
									return;
								} else {
									var orgId = document.getElementById('strOrg').value;
									var xhr = $.ajax({
										url : 'GetOrgWLocationList.action?type=CXOLOCATION&strOrgId='+orgId,
										cache : false,
										success : function(data) {
											document.getElementById('locDivCXO').innerHTML = data;
											$("#locationCXO").multiselect().multiselectfilter();
										}
									});
								}
							} else {
								document.getElementById('locDivCXO').innerHTML = "";
								if(isCXO == 0) {
									document.getElementById('userTypeLblTR').style.display = 'table-row';
									document.getElementById('userTypeTR').style.display = 'table-row';
								}
							}
						}
						
						function checkCXOHOD(val) {
							if(val == 1) {
								document.getElementById('locDivCXO').style.display = 'block';
								document.getElementById('locDivCXOLbl').style.display = 'block';
								document.getElementById('locDivHOD').innerHTML = '';
								document.getElementById('locDivHOD').style.display = 'none';
								document.getElementById('locDivHODLbl').style.display = 'none';
								showCurrentOrgLoaction(1);
							} else if(val == 2) {
								document.getElementById('locDivCXO').innerHTML = '';
								document.getElementById('locDivCXO').style.display = 'none';
								document.getElementById('locDivCXOLbl').style.display = 'none';
								document.getElementById('locDivHOD').style.display = 'block';
								document.getElementById('locDivHODLbl').style.display = 'block';
								showCurrentOrgSelectedDepartment(1, 'HOD');
							} else {
								document.getElementById('locDivHOD').innerHTML = '';
								document.getElementById('locDivCXO').innerHTML = '';
								document.getElementById('locDivCXO').style.display = 'none';
								document.getElementById('locDivCXOLbl').style.display = 'none';
								document.getElementById('locDivHOD').style.display = 'none';
								document.getElementById('locDivHODLbl').style.display = 'none';
							}
						}
						
						
						function getHRList(val) {
						 var xmlhttp = GetXmlHttpObject();
							if (xmlhttp == null) {
								alert("Browser does not support HTTP Request");
								return;
							} else {
								var hrValidReq = document.getElementById('hrValidReq').value;
								var xhr = $.ajax({
									url : 'GetEmployeeList.action?type=HR&f_org='+val+'&hrValidReq='+hrValidReq,
									cache : false,
									success : function(data) {
										document.getElementById('hrListID').innerHTML = data;
										
									}
								});
							}
						}
						
						function getHODList(val) {
						 var xmlhttp = GetXmlHttpObject();
							if (xmlhttp == null) {
								alert("Browser does not support HTTP Request");
								return;
							} else {
								var hodValidReq = document.getElementById('hodValidReq').value;
								var strEmpId = document.getElementById('empId').value;
								var xhr = $.ajax({
									url : 'GetEmployeeList.action?type=HOD&f_org='+val+'&hodValidReq='+hodValidReq+'&strEmpId='+strEmpId,
									cache : false,
									success : function(data) {
										document.getElementById('hodListID').innerHTML = data;
									}
								});
							}
						}
						
						function getSupervisorList(val) {
						 var xmlhttp = GetXmlHttpObject();
							if (xmlhttp == null) {
								alert("Browser does not support HTTP Request");
								return;
							} else {
								var supervisorValidReq = document.getElementById('supervisorValidReq').value;
								var strEmpId = document.getElementById('empId').value;
								var xhr = $.ajax({
									url : 'GetEmployeeList.action?type=SUPERVISOR&f_org='+val+'&supervisorValidReq='+supervisorValidReq+'&strEmpId='+strEmpId,
									cache : false,
									success : function(data) {
										document.getElementById('supervisorIdsSpan').innerHTML = data;
									}
								});
							}
						}
						
					</script>
					
						<div>
							<s:form theme="simple" name="frmOfficial" action="AddPeople" id="frmOfficialInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
								<div style="float: left; width: 98%;" >
									<s:hidden name="empId" id="empId"/>
									<s:hidden name="step" />
									<table class="table form-table">
										<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 2 : </span>Official Information</td></tr>
										<tr><td colspan=2><s:fielderror ><s:param>empCodeAlphabet</s:param></s:fielderror></td></tr>
										<tr><td colspan=2><s:fielderror ><s:param>empCodeNumber</s:param></s:fielderror></td></tr>
										<tr><td colspan=2>Working Information (Current Job):<hr style="background-color: #346897; height: 1px;"></td></tr>
										
										<tr>
											<td class="txtlabel alignRight">Organisation:<sup>*</sup></td>
											<td>
												<s:select label="Select Organisation" name="orgId" id="strOrg" listKey="orgId" listValue="orgName" cssClass="validateRequired" 
													headerKey="" headerValue="Select Organisation" list="orgList" key="" onchange="getDataFromAjax(this.value);"/>
											</td>
										</tr>
										<tr>
											<td class="txtlabel alignRight">Work Location:<sup>*</sup></td>
											<td id="idWLocId">
												<s:select theme="simple" name="wLocation" id="wLocation" listKey="wLocationId" listValue="wLocationName" cssClass="validateRequired"
													headerKey="" headerValue="Select Location" list="wLocationList" />
											</td>
										</tr>	
										<tr>
											<td class="txtlabel alignRight" valign="top">SBU:<sup>*</sup></td>
											<td class="" id="idService">
												<s:select theme="simple" name="service" listKey="serviceId" headerKey="" headerValue="Select SBU"
												listValue="serviceName" list="serviceList" key="" cssClass="validateRequired"/>
											</td> 
										</tr>
										<tr>
											<td class="txtlabel alignRight">Department:<sup>*</sup></td>
											<td id="idDepartment">
												<s:select theme="simple" name="department" id="department" listKey="deptId" cssClass="validateRequired"
													listValue="deptName" headerKey="" headerValue="Select Department" list="deptList" onchange="showCurrentOrgSelectedDepartment();"/>
											</td>
										</tr>
										<tr>
										<td class="txtlabel alignRight">Level:<sup>*</sup></td>
										<td id="idLevel">
											<s:select name="strLevel" list="levelList" listKey="levelId" id="levelIdV" listValue="levelCodeName" cssClass="validateRequired" 
												headerKey="" headerValue="Select Level" onchange="getDesig(this.value);"></s:select>		
										</td></tr>
										<tr>
											<td class="txtlabel alignRight">Designation:<sup>*</sup></td>
											<td>
												<div id="myDesig">
													<s:select theme="simple" name="strDesignation" list="desigList" listKey="desigId" id="strDesignation" 
													listValue="desigCodeName" headerKey="" headerValue="Select Designation" cssClass="validateRequired" required="true"/>
												</div>
											</td>
										</tr>
										
										<tr>
											<td class="txtlabel alignRight">Employee/Contractor:<sup>*</sup></td>
											<td><s:select theme="simple" name="empContractor" id="empContractor" headerKey="1" headerValue="Employee" list="#{'2':'Contractor'}" 
													cssClass="validateRequired" onchange="checkEmpORContractorCode(this.value);" />									
											</td>
										</tr>
										<tr>
											<td class="txtlabel alignRight">Employment Type:<sup>*</sup></td><td>
												<s:select name="empType" listKey="empTypeId" listValue="empTypeName" headerKey="" cssClass="validateRequired"
												headerValue="Select Emp Type" list="empTypeList" onchange="validateMandatory(this.options[this.options.selectedIndex].value)"/>
											</td>
										</tr>
										<tr>
											<td class="txtlabel alignRight"><input type="hidden" name="empCodeValidReqOpt" id="empCodeValidReqOpt" value="validateRequired" />
												<span id="empORContractorSpan">Employee</span>&nbsp;Code:<sup>*</sup>
											</td>
											<td id="empCodeTD">
												<s:hidden name="autoGenerate"/>
												<s:if test="autoGenerate==true">
													 <input type="text" name="empCodeAlphabet" id="empCodeAlphabetDis" style="width:90px !important;" value="<%=uF.showData((String)request.getAttribute("empCodeAlphabet"), "") %>" disabled="disabled"/>&nbsp;
														<s:hidden name="empCodeAlphabet" id="empCodeAlphabet"/>
													<input type="text" name="empCodeNumber" id="empCodeNumber" class="validateRequired validateNumber"style="width:90px !important;" value="<%=uF.showData((String)request.getAttribute("empCodeNumber"), "") %>" onkeypress="return isOnlyNumberKey(event)"/>
													<div id="empCodeMessege"></div>
												</s:if>
												<s:else>
										 			<input type="text" name="empCodeAlphabet" id="empCodeAlphabet" onchange="checkCodeValidation()" class="validateRequired" style="width:90px !important;" value="<%=uF.showData((String)request.getAttribute("empCodeAlphabet"), "") %>"/>&nbsp;
										 			<input type="text" name="empCodeNumber" id="empCodeNumber" onchange="checkCodeValidation()" class="validateRequired validateNumber" style="width:90px !important;" value="<%=uF.showData((String)request.getAttribute("empCodeNumber"), "") %>" onkeypress="return isOnlyNumberKey(event)"/>
													<div id="empCodeMessege"></div>
												</s:else>
											</td>
										</tr>
										
										<tr>
											<td class="txtlabel alignRight">Joining Date:<sup>*</sup></td>
											<td><input type="text" name="empStartDate" id="empStartDate" class="validateRequired" value="<%=uF.showData((String)request.getAttribute("empStartDate"), "") %>" readonly="readonly" /></td>
										</tr>
										
										
										<tr><td colspan=2>Structure:<hr style="background-color: #346897; height: 1px;"></td></tr>
									
										<tr><td class="txtlabel alignRight" valign="top">Is this employee a CXO/ a HOD:</td>
											<td><s:select headerKey="0" headerValue="Not COX/HOD" list="#{'1':'CXO', '2':'HOD'}" name="strCXOHOD" id="strCXOHOD" onchange="checkCXOHOD(this.value);"/>
											<% String defaultCXO = (String)request.getAttribute("defaultCXO");
												String defaultHOD = (String)request.getAttribute("defaultHOD");
												String cxoDisplay = "none";
												String hodDisplay = "none";
												String utlTRDisplay = "table-row";
												if(uF.parseToInt(defaultCXO) == 1) {
													cxoDisplay = "block";
													utlTRDisplay = "none";
												}
												if(uF.parseToInt(defaultHOD) == 1) {
													hodDisplay = "block";
													utlTRDisplay = "none";
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
										
										<%-- <tr><td class="txtlabel alignRight" valign="top">Is this employee a CXO:</td>
											<td><s:radio name="isCXO" id="isCXO" list="#{'1':'Yes','0':'No'}" value="defaultCXO" cssClass="validateRequired" onclick="showCurrentOrgLoaction();"/>
											<div id="locDivCXO">
												<% 
												String utlTRDisplay = "table-row";
												String defaultCXO = (String)request.getAttribute("defaultCXO");
												if(uF.parseToInt(defaultCXO) == 1) { 
													utlTRDisplay = "none";
												%>
												<s:select  name="locationCXO" id="locationCXO" theme="simple" listKey="wLocationId" listValue="wLocationName" list="wLocationList" 
													cssClass="validateRequired" multiple="true" value="cxoLocationAccess"/>
												<% } %>	
											</div>
											<div style="line-height: 15px; font-size: 11px; color: gray;">(Work Locations are based on above filter)</div>
											</td>
										</tr>
										
										<tr><td class="txtlabel alignRight" valign="top">Is this employee a HOD:</td>
											<td><s:radio name="isHOD" id="isHOD" list="#{'1':'Yes','0':'No'}" value="defaultHOD" cssClass="validateRequired" onclick="showCurrentOrgSelectedDepartment();" />	<!-- onclick="showCurrentOrgSelectedDepartment();" -->
											<div id="locDivHOD">
											<%	String defaultHOD = (String)request.getAttribute("defaultHOD");
												String HOD_DEPART_NAME = (String)request.getAttribute("HOD_DEPART_NAME");
												if(uF.parseToInt(defaultHOD) == 1) {
													utlTRDisplay = "none";
											%>
												<%=uF.showData(HOD_DEPART_NAME, "") %>
											<% } %>
											</div>
											<div style="line-height: 15px; font-size: 11px; color: gray;">(Department is based on above filter)</div>
											</td>
										</tr> --%>
										
										<tr><td colspan=2>Reporting Structure:<hr style="background-color: #346897; height: 1px;"></td></tr>		
										<tr>
										<td class="txtlabel alignRight">H.O.D.:<sup>*</sup>
										<input type="hidden" name="hodValidReq" id="hodValidReq" value="validateRequired"/>
										</td>
										<td id="hodListID">
											<s:select name="hod" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired" headerValue="Select H.O.D."
												list="HodList" key="" required="true" />
										<span class="hint">Employee's H.O.D. as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
										</td></tr>
										
										<tr>
										<td class="txtlabel alignRight">HR:<input type="hidden" name="hrValidReq" id="hrValidReq"/></td>
										<td id="hrListID">
											<s:select name="HR" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="Select HR" list="HRList" key="" required="true" />
										<span class="hint">Employee's HR as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
										</td></tr>
										
								<!-- ===start parvez date: 29-07-2022=== -->		
										<tr>
										<td class="txtlabel alignRight">Manager:<sup>*</sup>
										<input type="hidden" name="supervisorValidReq" id="supervisorValidReq" value="validateRequired"/>
										</td>
											<td><span id="supervisorIdsSpan">
												<s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired" 
													headerValue="Select Manager" list="supervisorList" key="" />
											</span>	
											<span class="hint">Employee's manager/superior as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
										</td></tr>
								<!-- ===end parvez date: 29-07-2022=== -->
										
										<%-- <tr>
										<td class="txtlabel alignRight">Supervisor:<sup>*</sup>
										<input type="hidden" name="supervisorValidReq" id="supervisorValidReq" value="validateRequired"/>
										</td>
											<td><span id="supervisorIdsSpan">
												<s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey="" cssClass="validateRequired" 
													headerValue="Select Supervisor" list="supervisorList" key="" />
											</span>	
											<span class="hint">Employee's manager/superior as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span>
										</td></tr> --%>
										
										
										<%-- <tr>
											<td class="txtlabel alignRight">Supervisor:<sup>*</sup></td>
											<td>
												<span id="supervisorIdsSpan">
													<s:select name="supervisor" listKey="employeeId" listValue="employeeCode" headerKey=""  
														headerValue="Select Supervisor" list="supervisorList" key="" cssClass="validateRequired" />
												</span>	
											</td>
										</tr> --%>
										<%-- <tr>
											<td class="txtlabel alignRight">Profile:<sup>*</sup></td>
											<td>
				                                    <s:select theme="simple" name="empProfile" cssClass="validateRequired" list="resourceList" listKey="resourceTypeId" listValue="resourceTypeName"/>
											</td>
										</tr> --%>
										<%  %>
										<tr id="userTypeLblTR" style="display: <%=utlTRDisplay %>"><td colspan=2>User Type:<hr style="background-color: #346897; height: 1px;"></td></tr>
										<tr id="userTypeTR" style="display: <%=utlTRDisplay %>">
											<td class="txtlabel alignRight">User Type:<sup>*</sup></td>
											<td>
												<s:hidden name="userid"></s:hidden>
												<s:select label="Select User Type" name="userType" id="userType" cssClass="validateRequired" listKey="userTypeId" 
												listValue="userTypeName" headerKey="" headerValue="Select User Type" list="userTypeList" onchange="showOrgAndWLoc(this.options[this.selectedIndex].value);"/> 				
											</td>
										</tr>
										
										<tr id="orgTR" style="display: none;">
											<td class="txtlabel alignRight">Organisation:<sup>*</sup></td>
											<td>
												<s:select label="Select Organisation" name="orgId1" id="strChangeOrg" cssClass="validateRequired" listKey="orgId" listValue="orgName" 
													list="orgList1" multiple="true" size="3" value="userTypeOrgValue" onchange="getWlocation();"/> <!-- headerKey="" headerValue="Select Organisation" -->
											</td>
										</tr>
										
										<tr id="wLocTR" style="display: none;">
											<td class="txtlabel alignRight">Work Location:<sup>*</sup></td>
											<td id="idWlocIdUType"><b>Please select organisation.</b></td>
										</tr>
										
										
										<tr><td colspan=2>Cost to Company:<hr style="background-color: #346897; height: 1px;"></td></tr>
										<tr>
											<td class="txtlabel alignRight">Monthly:</td>
											<td><%=uF.showData((String)request.getAttribute("MONTHLY_AMT"),"-") %></td>
										</tr>
										<tr>
											<td class="txtlabel alignRight">Annually:</td>
											<td><%=uF.showData((String)request.getAttribute("ANNUALY_AMT"),"-") %></td>
										</tr>
									</table>
								</div>
								<div class="clr"></div>
								<div style="float:right;">
									<table>
										<s:if test="mode==null">
											<tr>
												<td colspan="2" align="center">
													<s:submit cssClass="btn btn-primary" name="btnSubmit" id="btnSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
												</td>
											</tr>
										</s:if>
										<s:else>
											<tr>
												<td colspan="2" align="center">
												<s:hidden name="mode" /><s:submit cssClass="btn btn-primary" name="btnSubmit" id="btnSubmit" value="Update Information" align="center" />
												</td>
											</tr>
										</s:else>
									</table>
								</div>
							</s:form>
						</div>
					</s:if>
				<%} %>
		
				</div>
			</div>
		</div>
		</section>
	</div>
</section>