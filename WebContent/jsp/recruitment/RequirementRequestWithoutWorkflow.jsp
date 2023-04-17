<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%
UtilityFunctions uF=new UtilityFunctions();// Created by Dattatray Date:05-July-21
%>
<script type="text/javascript">
	$(function(){

		$("input[type='submit']").click(function(){
			$('.validateRequired').filter(':hidden').prop('required',false);
			$('.validateRequired').filter(':visible').prop('required',true);
		});
		
		/* $("#rdate").datepicker({
			format : 'dd/mm/yyyy'
		});

		$("#targetdeadline").datepicker({
			format : 'dd/mm/yyyy'
		}); */
		
		/* ===start parvez date: 14-10-2021=== */
		$("#rdate").datepicker({
	        format: 'dd/mm/yyyy',
	        autoclose: true
	    }).on('changeDate', function (selected) {
	        var minDate = new Date(selected.date.valueOf());
	        $('#targetdeadline').datepicker('setStartDate', minDate);
	    });
	    
	    $("#targetdeadline").datepicker({
	    	format: 'dd/mm/yyyy',
	    	autoclose: true
	    }).on('changeDate', function (selected) {
	            var minDate = new Date(selected.date.valueOf());
	            $('#rdate').datepicker('setEndDate', minDate);
	    });
		/* ===end parvez date: 14-10-2021=== */
		
		$("#formID_WO_skills").multiselect().multiselectfilter();
		$("#hiringManager").multiselect().multiselectfilter();
		$("#formID_WO_essentialSkills").multiselect().multiselectfilter();
		/* var config = {
		  '.chosen-select'           : {},
		  '.chosen-select-deselect'  : {allow_single_deselect:true},
		  '.chosen-select-no-single' : {disable_search_threshold:10},
		  '.chosen-select-no-results': {no_results_text:'Oops, nothing found!'},
		  '.chosen-select-width'     : {width:"95%"}
		}
		for (var selector in config) {
		  $(selector).chosen(config[selector]);
		} */
		
		//getGradebyDesig(desig,type);
		
		//getDesigRequiredAttribute(desig);
	});
	

	
	function getGradebyDesig(desig,type){
  		//alert("desig " + desig + " type " + type);
		getContent('myGrade1','GetGradefromDesig.action?strDesignation='+desig);
		window.setTimeout(function() {
			getPlannedbyDesig(desig,type);
		}, 200);
		//alert("desig 1 " + desig + " type 1 " + type);
		
	/* 	document.getElementById("gradeIdV").selectedIndex = 0;	 */

		window.setTimeout(function() {
			getDesigRequiredAttribute(desig, type);
		}, 300);
		
	}
	
	
	function getDesigRequiredAttribute(desigID, type) {
		//alert("value ===> "+value);
	     xmlhttp = GetXmlHttpObject();
	     if (xmlhttp == null) {
	             alert("Browser does not support HTTP Request");
	             return;
	     } else {
             var xhr = $.ajax({
               url : "DesigRequiredAttribute.action?desigID=" + desigID+ "&type=" + type,
               cache : false,
               success : function(data) {
               //alert("data ===> "+data);
           	   if(data == "") {
                  	} else {
                  		var allData = data.split("::::");
                  		if(document.getElementById("genderTD")) {
                  			document.getElementById("genderTD").innerHTML = allData[0];
                  		}
                  		
                  		if(document.getElementById("minAgeSpan")) {
                  			document.getElementById("minAgeSpan").innerHTML = allData[1];
                  		}
                  		document.getElementById("jobDescription").value = allData[2];
   	                 	document.getElementById("idealCandidate").value = allData[3];
                  	}
               }
             });
     	}
	}
	
				
	function getDesig(level){
		getContent('myDesig1','GetDesigfromLevel.action?pagefrom=RRequestWWF&strLevel='+level);
	}
	
	function getPlannedbyDesig(desig){
		
		//var date=document.getElementById("rdate").value;
		//var action='GetPlannedAjax.action?designation='+desig+'&date='+date;
		var action='GetPlannedAjax.action?designation='+desig;
		getContent('Planned', action);
		
	}
	function getPlannedbyDate(date){	
		var desig=document.getElementById("strDesignationUpdate").value;
		var action='GetPlannedAjax.action?date='+date+'&designation='+desig ;
		getContent('Planned', action);
		
	}
	
	
	function showTempCasualJastification(value) {
		//alert("value ===> "+value);
	     if (value == "2" || value == "3") { //addtionalVacancyTR replacementVacancyTR
	    	 document.getElementById("typeOfEmploymentTR").style.display = "table-row";
	     } else {
	    	 document.getElementById("typeOfEmploymentTR").style.display = "none";
     	}
	}
	
	
	function changeVacancyType(value) {
		//alert("value ===> "+value);
	     if (value == "1" || value == "3") { //addtionalVacancyTR replacementVacancyTR
	    	 document.getElementById("replacementVacancyTR").style.display = "none";
	    	 document.getElementById("replacementVacancyTR1").style.display = "none";
	    	 //document.getElementById("addtionalVacancyTR").style.display = "table-row";
	     } else {
	    	 document.getElementById("replacementVacancyTR").style.display = "table-row";
	    	 document.getElementById("replacementVacancyTR1").style.display = "table-row";
	    	 //document.getElementById("addtionalVacancyTR").style.display = "none";
     	}
	}
	
	
	function getLocationOrg(orgid){
		var action='GetLocationOrg.action?strOrg='+orgid ;
		getContent('locationdivid', action);
		getLevelByOrg(orgid);
		setTimeout(function(){ 
			$("form[name='frmRequirementRequestWithoutWorkflow']").find("select[multiple='multiple']").multiselect().multiselectfilter(); 
		}, 1000);
		/* getSkillsByOrg(orgid); */
	}
	
	function getLevelByOrg(orgid){
		//alert("orgid== "+ orgid);
		var action='GetLevelByOrg.action?strOrg='+orgid ;
		getContent('leveldivid', action);
		
		window.setTimeout(function() {
			getSkillsByOrg(orgid);
		}, 300);
	}
	
	function getSkillsByOrg(orgid){
		//alert("abcd");
		var action='GetSkillsByOrg.action?strOrg='+orgid ;
		//getContent('skilldivid', action);
		//alert("action====>"+action);
		 xmlhttp = GetXmlHttpObject();
	     if (xmlhttp == null) {
             alert("Browser does not support HTTP Request");
             return;
	     } else {
             var xhr = $.ajax({
               url : 'GetSkillsByOrg.action?strOrg='+orgid,
               cache : false,
               success : function(data) {
               	//alert("data ===> "+data);
	           	   if(data == "") {
	               } else {
	               		var allData = data.split("::::");    
	               		document.getElementById("essentialskilldivid").innerHTML = allData[0];
	                    document.getElementById("skilldivid").innerHTML = allData[1];
	               }
              }
            });
	     }
	}
	
	
	function checkValue() {
    	
		var val = document.getElementById("position").value;
		if(parseInt(val) <= 0){
			alert("Invalid value!");
			document.getElementById("position").value="";
		}
		// Start Dattatray Date:05-July-21
		<%-- var exceedPlan =<%=uF.showData((String)request.getAttribute("Output"),"0")%>;
		console.log(exceedPlan); --%>
		 var exceedPlan = document.getElementById("intPlannedCount").value; // Created by Dattatray Date:12-July-21 Note : checked count from intPlannedCount.
		if(parseInt(val) > parseInt(exceedPlan)){
			alert("No. of Positions requested exceeds Planned positions.");
		}// End Dattatray Date:05-July-21
		
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

	
	function checkOtherCustomer(val) {
   		if(parseInt(val) == 0) {
   			document.getElementById("otherCustomerDiv").style.display = "block";
   		} else {
   			document.getElementById("otherCustomerDiv").style.display = "none";
   		}
   	}
   	
    	
   	function getClient(divid, val) {
   		var xmlhttp = GetXmlHttpObject();
   		if (xmlhttp == null) {
   			alert("Browser does not support HTTP Request");
   			return;
   		} else {
   			var xhr = $.ajax({
   				url : "GetClientName.action?strClientName="+ val,
   				cache : false,
   				success : function(data) {
   					document.getElementById(divid).innerHTML=data;
   					if(data.trim().length >1){
   						document.getElementById("strOtherCustomer").value='';
   					}
   				}
   			});
   		}
   	 }
   	
   	function addEssentialSkillTextarea() {
   		document.getElementById("divEssentialSkillTextarea").style.display = "block";
   	}
   	function removeEssentialSkillTextarea() {
   		document.getElementById("divEssentialSkillTextarea").style.display = "none";
   	}
   	
   	function addDesirableSkillTextarea() {
   		document.getElementById("divDesirableSkillTextarea").style.display = "block";
   	}
   	function removeDesirableSkillTextarea() {
   		document.getElementById("divDesirableSkillTextarea").style.display = "none";
   	}
   	
   	
</script>

	<div  id="requirementRequestDiv">

	<%
	
	String strUserType = (String) session.getAttribute("USERTYPE");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
    if(hmFeatureStatus==null) hmFeatureStatus = new HashMap<String, String>();
    
    Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
    if(hmFeatureUserTypeId==null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
    
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
    List<FillEmployee> empList1 = (List<FillEmployee>) request.getAttribute("empList1");
    Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
    Map<String, String> hmCheckEmpList1 = (Map<String, String>) request.getAttribute("hmCheckEmpList1");
    
	//System.out.println("strUserType ===>> " + strUserType);
	String frmPage = (String) request.getAttribute("frmPage");
	//System.out.println("frmPage=>"+frmPage);
	String currUserType = (String) request.getAttribute("currUserType");
	
	Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
    String validReqOpt = "";
	String validAsterix = "";
	
	%>

	<s:form id="formID_WO" name="frmRequirementRequestWithoutWorkflow" theme="simple" action="RequirementRequestWithoutWorkflow" method="POST" cssClass="formcss" enctype="multipart/form-data">
	   <s:hidden name="recruitmentID" id="recruitmentID"></s:hidden>
        <s:hidden name="orgID" id="orgID" />
        <s:hidden name="wlocID" id="wlocID" />
        <s:hidden name="desigID" id="desigID" />
        <s:hidden name="checkStatus" id="checkStatus" />
        <s:hidden name="fdate" id="fdate" />
        <s:hidden name="tdate" id="tdate" />
        <s:hidden name="frmPage" id="frmPage" />
        <s:hidden name="currUserType" id="currUserType" />
		<input type="hidden" name="frmPage" id=frmPage value="<%=frmPage %>"/>
		<input type="hidden" name="strBaseUserType" id="strBaseUserType" value="<%=strBaseUserType %>"/>
		<table border="0" class="table table_no_border">
			<s:fielderror />
			<s:hidden name="insertRecruitReq" value="insert" />
			<tr>
			<% 
	            List<String> reqJobTitle = hmValidationFields.get("REQ_JOB_TITLE"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqJobTitle != null && uF.parseToBoolean(reqJobTitle.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
				<td class="txtlabel alignRight" style="width: 35%;">Job Title:<%=validAsterix %></td>
				<td>
					<% if(reqJobTitle != null && uF.parseToBoolean(reqJobTitle.get(0))) { %>
                    	<s:textfield name="jobTitle" id="jobTitle" cssClass="validateRequired" />
                    <% } else { %>
                    	<s:textfield name="jobTitle" id="jobTitle" />
                    <% } %>
				</td>
            </tr>
               
			<tr>
			<% 
	            List<String> reqOrg = hmValidationFields.get("REQ_ORGANIZATION"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqOrg != null && uF.parseToBoolean(reqOrg.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
				<td class="txtlabel alignRight">Organisation:<%=validAsterix %></td>
				<td>
				<% if(reqOrg != null && uF.parseToBoolean(reqOrg.get(0))) { %>
					<% if(strUserType != null && strUserType.equals(IConstants.MANAGER)) { %>
					<s:select theme="simple" name="organisation" listKey="orgId" listValue="orgName" list="organisationList" 
						key="" onchange="getLocationOrg(this.value);" disabled="true" value="strOrg"/>
						<input type="hidden" name="organisation" value="<%=(String)request.getAttribute("strOrg") %>">
					<% } else { %>
					<s:select theme="simple" name="organisation" listKey="orgId" listValue="orgName" list="organisationList" 
						key="" onchange="getLocationOrg(this.value);" value="strOrg"/>
					<% } %>
				<% } else { %>
					<% if(strUserType != null && strUserType.equals(IConstants.MANAGER)) { %>
                    <s:select theme="simple" name="organisation" id="organisation" listKey="orgId" listValue="orgName" list="organisationList" 
                        key="" onchange="getLocationOrg(this.value);" disabled="true" value="strOrg"/>
                    	<input type="hidden" name="organisation" value="<%=(String)request.getAttribute("strOrg") %>">
                    <% } else { %>
                    <s:select theme="simple" name="organisation" id="organisation" listKey="orgId" listValue="orgName" list="organisationList" 
                        key="" onchange="getLocationOrg(this.value);" value="strOrg"/>
                    <% } %>
				<% } %>
               </td> 
               </tr>
              
               <tr>
               <% 
		            List<String> reqLoc = hmValidationFields.get("REQ_LOCATION"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqLoc != null && uF.parseToBoolean(reqLoc.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	            %>
				<td class="txtlabel alignRight">Location:<%=validAsterix %></td>
				<td>
					<div id="locationdivid">
					<% if(reqLoc != null && uF.parseToBoolean(reqLoc.get(0))) { %>
                     <s:select cssClass="validateRequired" name="location" id="locationid" theme="simple" listKey="wLocationId" listValue="wLocationName" 
                     	headerKey="" headerValue="Select Location" list="workLocationList" value="{manlocation}" />
                     <% } else { %>
                     <s:select name="location" id="locationid" theme="simple" listKey="wLocationId" listValue="wLocationName" 
                     	headerKey="" headerValue="Select Location" list="workLocationList" value="{manlocation}" />
                     <% } %>
					</div>
				</td>
			</tr>
			<tr>
			<% 
	            List<String> reqLevel = hmValidationFields.get("REQ_LEVEL"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqLevel != null && uF.parseToBoolean(reqLevel.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
				<td class="txtlabel alignRight">Level:<%=validAsterix %></td>
				<td>
				<div id="leveldivid">
				<% if(reqLevel != null && uF.parseToBoolean(reqLevel.get(0))) { %>
                <s:select cssClass="validateRequired" name="strLevel" id="strLevel" theme="simple" listKey="levelId" listValue="levelCodeName" 
                	headerKey="" headerValue="Select Level" list="levelslist" required="true" onchange="getDesig(this.value);" />
                <% } else { %>
                <s:select name="strLevel" id="strLevel" theme="simple" listKey="levelId" listValue="levelCodeName" 
                	headerKey="" headerValue="Select Level" list="levelslist" required="true" onchange="getDesig(this.value);" />
                <% } %>
			 	</div>
				</td>
			</tr>

			<tr>
			<% 
	            List<String> reqDesig = hmValidationFields.get("REQ_DESIGNATION"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqDesig != null && uF.parseToBoolean(reqDesig.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
				<td class="txtlabel alignRight">Designation:<%=validAsterix %></td>
				<td><div id="myDesig1" style="float: left;">
					<% if(reqDesig != null && uF.parseToBoolean(reqDesig.get(0))) { %>
                       <s:select theme="simple" name="strDesignationUpdate" id="strDesignationUpdate" listKey="desigId" listValue="desigCodeName" 
                           headerKey="" headerValue="Select Designation" list="desigList" key="" cssClass="validateRequired" 
                           value="strDesignationUpdate" onchange="getGradebyDesig(this.value,'add');" />
					<% } else { %>
						<s:select theme="simple" name="strDesignationUpdate" id="strDesignationUpdate" listKey="desigId" listValue="desigCodeName" 
							headerKey="" headerValue="Select Designation" list="desigList" key="" value="strDesignationUpdate" onchange="getGradebyDesig(this.value,'add');" />
					<% } %>
					</div>
				</td>			
			</tr>
			
			<tr id="existGradeTR">
				<% 
		            List<String> reqGrade = hmValidationFields.get("REQ_GRADE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqGrade != null && uF.parseToBoolean(reqGrade.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	            %>
				<td class="txtlabel alignRight">Grade:<%=validAsterix %></td>
				<td>
					<div id="myGrade1">
					<% if(reqGrade != null && uF.parseToBoolean(reqGrade.get(0))) { %>
                        <s:select  name="strGrade" id="empGrade" theme="simple" listKey="gradeId" listValue="gradeCode" cssClass="validateRequired"
                            headerKey="" headerValue="Select Grade" list="gradeList" value="strGrade"/>
					<% } else { %>
						<s:select  name="strGrade" id="empGrade" theme="simple" listKey="gradeId" listValue="gradeCode"
                            headerKey="" headerValue="Select Grade" list="gradeList" value="strGrade"/>
					<% } %>
					</div>
				</td>
			</tr>
			
			<tr>
			<% 
	            List<String> reqPriority = hmValidationFields.get("REQ_PRIORITY"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqPriority != null && uF.parseToBoolean(reqPriority.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
				<td class="txtlabel alignRight">Priority:<%=validAsterix %></td>
				<td>
					<div id="myPriority">
					<% if(reqPriority != null && uF.parseToBoolean(reqPriority.get(0))) { %>
                        <s:select cssClass="validateRequired" name="priority" theme="simple" list="#{'0':'Low', '2':'Medium', '1':'High' }" />
					<% } else { %>
						<s:select name="priority" theme="simple" list="#{'0':'Low', '2':'Medium', '1':'High' }"   />
					<% } %>
					</div>
				</td>
			</tr>
			
			<tr>
			<%
	            List<String> reqJobDescription = hmValidationFields.get("REQ_JOB_DESCRIPTION"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqJobDescription != null && uF.parseToBoolean(reqJobDescription.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
				<td class="txtlabel alignRight" valign="top">Job Description:<%=validAsterix %></td>
				<td valign="top">
					<% if(reqJobDescription != null && uF.parseToBoolean(reqJobDescription.get(0))) { %>
                        <s:textarea name="jobDescription" id="jobDescription" cols="30" rows="4" cssClass="validateRequired"></s:textarea>
					<% } else { %>
						<s:textarea name="jobDescription" id="jobDescription" cols="30" rows="4"></s:textarea>
					<% } %>
			    </td>
			</tr>
			
			<tr>
            <% 
	            List<String> reqEssentialSkills = hmValidationFields.get("REQ_ESSENTIAL_SKILLS"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqEssentialSkills != null && uF.parseToBoolean(reqEssentialSkills.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight" valign="top">Essential Skills:<%=validAsterix %></td>
                <td><div style=" float: left; width: 100%;">
	                    <div id="essentialskilldivid" style="float: left;">
	                    <% if(reqEssentialSkills != null && uF.parseToBoolean(reqEssentialSkills.get(0))) { %>
	                        <s:select name="essentialSkills" theme="simple" listKey="skillsId" listValue="skillsName" list="essentialSkillsList" multiple="true" cssClass="validateRequired"/>
						<% } else { %>
							<s:select name="essentialSkills" theme="simple" listKey="skillsId" listValue="skillsName" list="essentialSkillsList" multiple="true"/>
						<% } %>
	                    </div>
	                    <div style="float: left; margin-left: 5px;"><a href="javascript:void(0)" onclick="addEssentialSkillTextarea();" class="add-font"></a></div>
                    </div>
                    <% String essentialSkillsText = (String) request.getAttribute("essentialSkillsText"); %>
                    <div id="divEssentialSkillTextarea" style="display: <%=(essentialSkillsText!=null && essentialSkillsText.trim().length()>0) ? "block" : "none" %>; float: left; width: 100%; margin-top: 5px;">
                    	<div style="float: left;"><s:textarea name="essentialSkillsText" id="essentialSkillsText"></s:textarea></div>
                    	<div style="float: left; margin-left: 5px;"><a href="javascript:void(0)" onclick="removeEssentialSkillTextarea();" class="remove-font"></a></div>
                    </div>
                </td>
            </tr>
            <tr>
            <% 
	            List<String> reqDesirableSkills = hmValidationFields.get("REQ_DESIRABLE_SKILLS"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqDesirableSkills != null && uF.parseToBoolean(reqDesirableSkills.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight" valign="top">Desirable Skills:<%=validAsterix %></td>
                <td><div style="float: left; width: 100%;">
	                    <div id="skilldivid" style="float: left;">
	                    <% if(reqDesirableSkills != null && uF.parseToBoolean(reqDesirableSkills.get(0))) { %>
	                        <s:select name="skills" theme="simple" listKey="skillsId" listValue="skillsName" list="skillslist" multiple="true" cssClass="validateRequired"/>
						<% } else { %>
							<s:select name="skills" theme="simple" listKey="skillsId" listValue="skillsName" list="skillslist" multiple="true"/>
						<% } %>
	                    </div>
	                    <div style="float: left; margin-left: 5px;"><a href="javascript:void(0)" onclick="addDesirableSkillTextarea();" class="add-font"></a></div>
                    </div>
                    <% String desirableSkillsText = (String) request.getAttribute("desirableSkillsText"); %>
                    <div id="divDesirableSkillTextarea" style="display: <%=(desirableSkillsText!=null && desirableSkillsText.trim().length()>0) ? "block" : "none" %>; float: left; width: 100%; margin-top: 5px;">
                    	<div style="float: left;"><s:textarea name="desirableSkillsText" id="desirableSkillsText"></s:textarea></div>
                    	<div style="float: left; margin-left: 5px;"><a href="javascript:void(0)" onclick="removeDesirableSkillTextarea();" class="remove-font"></a></div>
                    </div>
                </td>
            </tr>
            
			<tr>
                <%
                    String idealCandidate="";
                    if(request.getAttribute("idealCandidate")!=null)
                    	idealCandidate=(String)request.getAttribute("idealCandidate");
                %>
                <%
		            List<String> reqIdealCandidate = hmValidationFields.get("REQ_IDEAL_CANDIDATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqIdealCandidate != null && uF.parseToBoolean(reqIdealCandidate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
            	%>	
                <td class="txtlabel alignRight" valign="top">Ideal Candidate:<%=validAsterix %></td>
                <td valign="top">
                    <div id="idealdivid">
                    <% if(reqIdealCandidate != null && uF.parseToBoolean(reqIdealCandidate.get(0))) { %>
                        <s:textarea name="idealCandidate" id="idealCandidate" cols="30" rows="4" cssClass="validateRequired"></s:textarea>
                    <% } else { %>
                    	<s:textarea name="idealCandidate" id="idealCandidate" cols="30" rows="4"></s:textarea>
                    <% } %>
                    </div>
                </td>
            </tr>
            
			<tr>
				<th class="txtlabel alignRight">Min. Experience:<%=validAsterix %></th>
				<td>
					Year&nbsp;&nbsp;<select name="minYear" id="minYear" class="autoWidth">
						<% for (int i = 0; i <= 20; i++) {
							if (i == uF.parseToInt((String)request.getAttribute("minYear"))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<% } } %>
				</select>&nbsp;&nbsp;Month &nbsp;&nbsp;<select name="minMonth" id="minMonth" class="autoWidth">
						<% for (int i = 0; i < 12; i++) {
							if (i == uF.parseToInt((String)request.getAttribute("minMonth"))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<% } } %>
				</select> 
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Max. Experience:<%=validAsterix %></th>
				<td>
					Year&nbsp;&nbsp;<select name="maxYear" id="maxYear" class="autoWidth">
						<% for (int i = 0; i <= 20; i++) {
							if (i == uF.parseToInt((String)request.getAttribute("maxYear"))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<% } } %>
				</select>&nbsp;&nbsp;Month &nbsp;&nbsp;<select name="maxMonth" id="maxMonth" class="autoWidth">
						<% for (int i = 0; i < 12; i++) {
							if (i == uF.parseToInt((String)request.getAttribute("maxMonth"))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<% } } %>
				</select> 
				</td>
			</tr>
			 <!-- Start Dattatray Date : 05-July-21 Note : Position changed-->
			<tr>
                <%
                    String effectiveDate="";
                    if(request.getAttribute("rdate")==null) {
                    	effectiveDate="";
                    } else {
                    	effectiveDate=(String)request.getAttribute("rdate");
                    }
				%>
				<%
		            List<String> reqEffectiveDate = hmValidationFields.get("REQ_EFFECTIVE_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqEffectiveDate != null && uF.parseToBoolean(reqEffectiveDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight">Position Open Date:<%=validAsterix %></td>
                <td><input type="text" name="rdate" id="rdate" onchange="getPlannedbyDate(this.value);" class="<%=validReqOpt %>" value="<%=effectiveDate%>"/></td>
            </tr>
            <tr>
                <%
                    String targetDead="";
                    if(request.getAttribute("targetdeadline")==null) {
                    	targetDead="";
                    } else {
                    	targetDead=(String)request.getAttribute("targetdeadline");
                    }
				%>
				<%
		            List<String> reqTargetDeadline = hmValidationFields.get("REQ_TARGET_DEADLINE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqTargetDeadline != null && uF.parseToBoolean(reqTargetDeadline.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>	
                <td class="txtlabel alignRight">Target Deadline:<%=validAsterix %></td>
                <td><input type="text" name="targetdeadline" id="targetdeadline" class="<%=validReqOpt %>" value="<%=targetDead%>"/></td>
            </tr>
             <!-- End Dattatray Date : 05-July-21 -->
			<tr>
			<%
	            List<String> reqNoOfPosition = hmValidationFields.get("REQ_NO_OF_POSITIONS"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqNoOfPosition != null && uF.parseToBoolean(reqNoOfPosition.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
				<td class="txtlabel alignRight">No. of Position(s):<%=validAsterix %></td>
				<td width = "400px;">
				<% if(reqNoOfPosition != null && uF.parseToBoolean(reqNoOfPosition.get(0))) { %>
                    <s:textfield name="position" id="position" cssClass="validateRequired" onkeyup="checkValue();" onkeypress="return isOnlyNumberKey(event)"/>
				<% } else { %>
					<s:textfield name="position" id="position" onkeyup="checkValue();" onkeypress="return isOnlyNumberKey(event)"/>
				<% } %>
				</td>
			</tr>
			<tr>
				<td></td>
				<td id="Planned" >
				<%if(request.getAttribute("recruitmentID")!=null) { %>
					<div class="skill_div" style="width: 80px;">
						<input type="hidden" id="intPlannedCount" value="<%=uF.showData((String)request.getAttribute("Output"),"0") %>"/><!-- Created by Dattatray Date:12-July-21  -->
	                    <p class="sk_value marginbottom0 alignCenter"><%=uF.showData((String)request.getAttribute("Output"),"0")%></p>             
	                    <p class="sk_name marginbottom0 alignCenter">Planned</p>                
		            </div>
		            <div class="skill_div" style="width: 80px;">
	                    <p class="sk_value marginbottom0 alignCenter"><%=uF.showData((String)request.getAttribute("strExistCount"),"0")%></p>             
	                    <p class="sk_name marginbottom0 alignCenter">Existing</p>                
		            </div>
				<%} %>
				</td>	
			</tr>
			
			<tr>
            <%
	            List<String> reqGender = hmValidationFields.get("REQ_GENDER"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqGender != null && uF.parseToBoolean(reqGender.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Gender:<%=validAsterix %></td>
                <td id="genderTD">
                    <select name="gender" id="gender" class="<%=validReqOpt %>">
                        <%
                            String gender = (String) request.getAttribute("strSex");
                            if(gender != null && !gender.equals("")) {
						%>
                        	<%=gender %>
                        <% } else { %>
                        <option value="0">Any</option>
                        <option value="M">Male</option>
                        <option value="F">Female</option>
                        <% } %>
                    </select>
                </td>
            </tr>
            <tr>
            <%
	            List<String> reqMinAge = hmValidationFields.get("REQ_MIN_AGE"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqMinAge != null && uF.parseToBoolean(reqMinAge.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Min. Age:<%=validAsterix %></td>
                <td>
                    <span id="minAgeSpan">
                        <select name="minAge" id="minAge" class="<%=validReqOpt %>">
                            <%
                                String minAge = (String) request.getAttribute("strAge");
                                if(minAge != null && !minAge.equals("")) {
                                %>
                            <%=minAge %>
                            <% } else { %>
                            <option value="0">Select Age</option>
                            <% for (int i = 0; i <=42; i++) {
                                int intMinAge = 18;
                                intMinAge += i;
                                %>
                            <option value="<%=intMinAge %>"><%=intMinAge %></option>
                            <% }
                                } %>
                        </select>
                    </span>
                </td>
            </tr>
            <tr>
            <%
	            List<String> reqMinCTC = hmValidationFields.get("REQ_MIN_CTC"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqMinCTC != null && uF.parseToBoolean(reqMinCTC.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Min. CTC:<%=validAsterix %></td>
                <td>
                <% if(reqMinCTC != null && uF.parseToBoolean(reqMinCTC.get(0))) { %>
                    <s:textfield name="strMinCTC" id="strMinCTC" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<% } else { %>
					<s:textfield name="strMinCTC" id="strMinCTC" onkeypress="return isNumberKey(event)"/>
				<% } %>
                </td>
            </tr>
            <tr>
            <%
	            List<String> reqMaxCTC = hmValidationFields.get("REQ_MAX_CTC"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqMaxCTC != null && uF.parseToBoolean(reqMaxCTC.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Max. CTC:<%=validAsterix %></td>
                <td>
                <% if(reqMaxCTC != null && uF.parseToBoolean(reqMaxCTC.get(0))) { %>
                    <s:textfield name="strMaxCTC" id="strMaxCTC" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
                <% } else { %>
                	<s:textfield name="strMaxCTC" id="strMaxCTC" onkeypress="return isNumberKey(event)"/>
                <% } %>
                </td>
            </tr>
            <tr>
            <%
	            List<String> reqTypeofEmployment = hmValidationFields.get("REQ_TYPE_OF_EMPLOYMENT"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqTypeofEmployment != null && uF.parseToBoolean(reqTypeofEmployment.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Type of Employment:<%=validAsterix %></td>
                <td>
                    <div id="typeOfEmployment">
                    <% if(reqTypeofEmployment != null && uF.parseToBoolean(reqTypeofEmployment.get(0))) { %>
                        <s:select cssClass="validateRequired" name="strEmploymentType" id="strEmploymentType" theme="simple" listKey="empTypeId" listValue="empTypeName" headerKey="" 
                        	headerValue="Select Employment Type" list="employmentList" value="strEmployment" onchange="showTempCasualJastification(this.value);"/>
					<% } else { %>
						<s:select name="strEmploymentType" id="strEmploymentType" theme="simple" listKey="empTypeId" listValue="empTypeName" headerKey="" 
							headerValue="Select Employment Type" list="employmentList" value="strEmployment" onchange="showTempCasualJastification(this.value);"/>
					<% } %>
                    </div>
                </td>
            </tr>
            <tr id="typeOfEmploymentTR" style="display: none;">
                <td class="txtlabel alignRight">If Temporary/Casual,please give justification & period required for:</td>
                <td valign="top">
                    <div id="tempOrCasualDiv">
                        <s:textarea name="tempOrCasualJastification" cols="30" rows="4"></s:textarea>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Vacancy:</td>
                <td>
                	<s:select cssClass="validateRequired" name="vacancy" headerKey="1" headerValue="New Requirement" theme="simple"
						list="#{'2':'Replacement', '3':'Staffing Requirement' }" onchange="changeVacancyType(this.value);" />
                </td>
            </tr>
            <tr id="addtionalVacancyTR">
            <%
	            List<String> reqGiveJustification = hmValidationFields.get("REQ_GIVE_JUSTIFICATION"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqGiveJustification != null && uF.parseToBoolean(reqGiveJustification.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight" valign="top">Give Justification:<%=validAsterix %></td>
                <td valign="top">
                    <div id="addtionalvacancydivid">
                    <% if(reqGiveJustification != null && uF.parseToBoolean(reqGiveJustification.get(0))) { %>
                        <s:textarea name="addtionalJastification" cols="30" rows="4" cssClass="validateRequired"></s:textarea>
					<% } else { %>
						<s:textarea name="addtionalJastification" cols="30" rows="4"></s:textarea>
					<% } %>
                    </div>
                </td>
            </tr>
            <tr id="replacementVacancyTR" style="display: none;">
                <td  class="txtlabel alignRight">Name of person to be replaced with:</td>
                <td></td>
            </tr>
            <tr  id="replacementVacancyTR1" style="display: none;">
                <td colspan="3">
                    <table class="table table-bordered">
                        <tr>
                            <td colspan="5">
                                <div class="filter_div">
                                    <div class="row row_without_margin">
                                        <div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
                                       		<i class="fa fa-filter"></i>
                                        </div>
                                        <div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="margin: 5px;">
                                                <s:select theme="simple" name="strOrg" list="orgList" id="strOrg" listKey="orgId" listValue="orgName"
                                                    required="true"  onchange="getWLocDepartLevelDesigByOrg(this.value);"></s:select>
                                            </div>
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="wlocationDiv" style="margin: 5px;">
                                                <s:select theme="simple" name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName" headerKey=""
                                                    headerValue="All WorkLocation" required="true" value="{strEmpWLocId}" onchange="getEmployeebyLocation();"></s:select>
                                            </div>
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="departDiv" style="margin: 5px;">
                                                <s:select theme="simple" name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
                                                    headerValue="All Department" required="true" onchange="getEmployeebyDepart();"></s:select>
                                            </div>
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="levelDiv" style="margin: 5px;">
                                                <s:select theme="simple" name="strLevel1" list="levelList" listKey="levelId" id="strLevel1" listValue="levelCodeName" headerKey=""
                                                    headerValue="All Level" required="true" onchange="getEmployeebyLevel()"></s:select>
                                            </div>
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="myDesig" style="margin: 5px;">
                                                <s:select theme="simple" name="strDesignation" list="designationList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
                                                    headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();"></s:select>
                                            </div>
                                        </div>
                                    </div>
                                    <br>	
                                </div>
                                <div class="row row_without_margin">
                                    <div class="col-lg-5 col-md-5 col-sm-12" id="myEmployee" style="height:250px;overflow-y:auto;border: 2px solid rgb(238, 238, 238);padding: 5px;margin: 5px;">
                                        <table id="lt" class="table table-bordered">
                                            <%
                                                if (empList != null && !empList.equals("") && !empList.isEmpty()) {
                                                	Map<String, String> hmEmpLocation = (Map<String, String>)request.getAttribute("hmEmpLocation");
                                                	if(hmEmpLocation==null) hmEmpLocation = new HashMap<String, String>();
                                                	Map<String, String> hmWLocation = (Map<String, String>)request.getAttribute("hmWLocation");
                                                	if(hmWLocation==null) hmWLocation = new HashMap<String, String>();
                                                	Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
                                                	if(hmEmpCodeDesig==null) hmEmpCodeDesig = new HashMap<String, String>();
                                                %>
                                            <thead>
                                                <tr>
                                                    <th align="center"><input onclick="checkUncheckValue();" type="checkbox" name="allEmp" id="allEmp"></th>
                                                    <th align="center">Employee</th>
                                                    <th align="center">Designation</th>
                                                    <th align="center">Location</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                    for (int i = 0; i < empList.size(); i++) {
                                           				String empID = ((FillEmployee) empList.get(i)).getEmployeeId();
                                           				String empName = ((FillEmployee) empList.get(i)).getEmployeeCode();
                                           
                                           				String emplocationID = (empID == null || empID.equals("")) ? "" : hmEmpLocation.get(empID);
                                           				String location = (emplocationID == null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");
                                           
                                           				String desig = (empID == null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID), "");
                                                    %>
                                                <tr>
                                                    <td><!-- Created By Dattatray Date : 09-July-2021 Note: strHiringEmpId_-->
                                                    <td><input type="checkbox" name="strHiringEmpId" id="strHiringEmpId_<%=i%>" onclick="getHiringSelectedEmp(this.checked,this.value);"
                                                        value="<%=empID%>" <%if (hmCheckEmpList != null && hmCheckEmpList.get(empID) != null) {%>
                                                        checked="checked" <% } %>>
                                                    </td>
                                                    <td nowrap="nowrap"><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a></td>
                                                    <td nowrap="nowrap"><%=desig%></td>
                                                    <td nowrap="nowrap"><%=location%></td>
                                                </tr>
                                                <%
													}
														} else {
												%>
                                                <tr>
                                                    <td colspan="3">
                                                        <div class="nodata msg">
                                                            <span>No Employee Found</span>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <% } %>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="col-lg-5 col-md-5 col-sm-12" id="idEmployeeInfo" style="height:250px;overflow-y:auto;border: 2px solid rgb(238, 238, 238);padding: 5px;margin: 5px;">
                                        <%
                                            List<String> selectEmpList = (List<String>) request.getAttribute("selectEmpList");
                                            	if (selectEmpList != null) {
                                            %>
                                        <div style="border: 2px solid #ccc;">
                                            <div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
                                            <% for (int i = 0; i < selectEmpList.size(); i++) { %>
                                            <div style="float: left; width: 100%; margin: 5px;"><strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=selectEmpList.get(i)%></div>
                                            <% } %>
                                        </div>
                                        <% } else { %>
                                        <div class="nodata msg" style="width: 85%">
                                            <span>No Employee selected</span>
                                        </div>
                                        <%	} %>
                                        <input type="hidden" name="empselected" id="empselected" value="<%=uF.showData((String)request.getAttribute("empselected"), "0") %>"/>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Reporting to:</td>
                <td>
                    <% String reportToType = (String)request.getAttribute("reportToType");
                        String managerName = "";
                        if(strUserType != null && strUserType.equals(IConstants.MANAGER)) {
                        	managerName = "("+hmEmpName.get(strSessionEmpId)+")"; 
                        }
                        %>
                    <input type="hidden" name="reporttoHidden" id="reporttoHidden" value="<%=uF.showData((String)request.getAttribute("reportToType"), "Myself")%>">
                    <select name="reportToType" onchange="changeReportType(this.value);">
                        <option value="Myself">Myself <%=managerName %></option>
                        <% if(reportToType != null && reportToType.equals("Other")) { %>
                        <option value="Other" selected="selected">Other</option>
                        <% } else { %>
                        <option value="Other">Other</option>
                        <% } %>
                    </select>
                </td>
            </tr>
            <tr id="reportingToTR" style="display: none;">
                <!-- <td class="txtlabel alignRight">Reporting to:</td> -->
                <td colspan="2">
                    <table class="table table-bordered">
                        <tr>
                            <td colspan="5">
                                <div class="row row_without_margin">
                                    <div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
                                    	<i class="fa fa-filter"></i>
                                    </div>
                                    <div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="margin: 5px;">
                                            <s:select theme="simple" name="strOrg1" list="orgList1" id="strOrg1" listKey="orgId" listValue="orgName"
                                                required="true"  onchange="getWLocDepartLevelDesigByOrg1(this.value);"></s:select>
                                        </div>
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="wlocationDiv1" style="margin: 5px;">
                                            <s:select theme="simple" name="strWlocation1" list="workList1" id="wlocation1" listKey="wLocationId" listValue="wLocationName" headerKey=""
                                                headerValue="All WorkLocation" required="true" value="{strEmpWLocId}" onchange="getEmployeebyLocation1();"></s:select>
                                        </div>
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="departDiv1" style="margin: 5px;">
                                            <s:select theme="simple" name="strDepart1" list="departmentList1" id="depart1" listKey="deptId" listValue="deptName" headerKey=""
                                                headerValue="All Department" required="true" onchange="getEmployeebyDepart1();"></s:select>
                                        </div>
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="levelDiv1" style="margin: 5px;">
                                            <s:select theme="simple" name="strLevel11" list="levelList1" listKey="levelId" id="strLevel11" listValue="levelCodeName" headerKey=""
                                                headerValue="All Level" required="true" onchange="getEmployeebyLevel1()"></s:select>
                                        </div>
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="myDesig11" style="margin: 5px;">
                                            <s:select theme="simple" name="strDesignation1" list="designationList1" listKey="desigId" id="desigIdV1" listValue="desigCodeName"
                                                headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig1();"></s:select>
                                        </div>
                                    </div>
                                </div>
                                <br>	
                                <div class="row row_without_margin">
									<div class="col-lg-6 col-md-6 col-sm-12" id="myEmployee1"
										style="border: 2px solid rgb(238, 238, 238); padding: 5px; margin: 5px; height: 250px; overflow-y: auto;">
										<table id="lt1" class="table table-bordered">
											<%
                                                //System.out.println("empList1 ===> " + empList1);
                                                if (empList1 != null && !empList1.equals("") && !empList1.isEmpty()) {
                                                	Map<String, String> hmEmpLocation = (Map<String, String>)request.getAttribute("hmEmpLocation");
                                                	if(hmEmpLocation==null) hmEmpLocation = new HashMap<String, String>();
                                                	Map<String, String> hmWLocation = (Map<String, String>)request.getAttribute("hmWLocation");
                                                	if(hmWLocation==null) hmWLocation = new HashMap<String, String>();
                                                	Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
                                                	if(hmEmpCodeDesig==null) hmEmpCodeDesig = new HashMap<String, String>();
                                                %>
											<thead>
												<tr>
													<th><input
														onclick="checkUncheckValue1();" type="checkbox"
														name="allEmp1" id="allEmp1">
													</th>
													<th align="center">Employee</th>
													<th align="center">Designation</th>
													<th align="center">Location</th>
												</tr>
											</thead>
											<tbody>
												<%
                                                    for (int i = 0; i < empList1.size(); i++) {
                                                    
                                          				String empID = ((FillEmployee) empList1.get(i)).getEmployeeId();
                                          				String empName = ((FillEmployee) empList1.get(i)).getEmployeeCode();
                                          				String emplocationID = (empID == null || empID.equals("")) ? "" : hmEmpLocation.get(empID);
                                          				String location = (emplocationID == null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");
                                          				String desig = (empID == null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID), "");
                                                    %>
												<tr>
													<td><input type="checkbox" name="strHiringEmpId1" id="strHiringEmpId1<%=i%>" 
														onclick="getHiringSelectedEmp1(this.checked, this.value);" value="<%=empID%>"
														<%if (hmCheckEmpList1 != null && hmCheckEmpList1.get(empID) != null) { %>
														checked="checked" <% } %> >
													</td>
													<td nowrap="nowrap"><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a>
													</td>
													<td nowrap="nowrap"><%=desig %></td>
													<td nowrap="nowrap"><%=location %></td>
												</tr>
												<%
                                                    }
                                                    	} else {
												%>
												<tr>
													<td colspan="3">
														<div class="nodata msg">
															<span>No Employee Found</span>
														</div>
													</td>
												</tr>
												<% } %>
											</tbody>
										</table>
									</div>
									<div class="col-lg-5 col-md-5 col-sm-12" id="idEmployeeInfo1" style="border: 2px solid rgb(238, 238, 238);padding: 5px;margin: 5px;height:250px;overflow-y:auto;">
                                        <%
                                            List<String> selectEmpList1 = (List<String>) request.getAttribute("selectEmpList1");
                                            	if (selectEmpList1 != null) {
                                            %>
                                        <div style="border: 2px solid #ccc;">
                                            <div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
                                            <% for (int i = 0; i < selectEmpList1.size(); i++) { %>
                                            <div style="float: left; width: 100%; margin: 5px;"><strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=selectEmpList1.get(i)%></div>
                                            <% } %>
                                        </div>
                                        <% } else { %>
                                        <div class="nodata msg" style="width: 85%">
                                            <span>No Employee selected</span>
                                        </div>
                                        <%	} %>
                                        <input type="hidden" name="empselected1" id="empselected1" value="<%=uF.showData((String)request.getAttribute("empselected1"), "0") %>"/>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            
            
            <tr>
                <%
                    String notes="";
                    if(request.getAttribute("notes")!=null){
                    	notes=(String)request.getAttribute("notes");
                    }
				%>
				<%
		            List<String> reqBusinessBenefits = hmValidationFields.get("REQ_BUSINESS_BENEFITS"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqBusinessBenefits != null && uF.parseToBoolean(reqBusinessBenefits.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>	
                <td class="txtlabel alignRight" valign="top">Business Benefits:<%=validAsterix %></td>
                <td><textarea name="notes" id="notes" cols="30" rows="4" class="<%=validReqOpt %>"><%=notes%></textarea>
                </td>
            </tr>
            
			<tr>
			<%
	            List<String> reqHiringManager = hmValidationFields.get("REQ_HIRING_MANAGER"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqHiringManager != null && uF.parseToBoolean(reqHiringManager.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
				<td class="txtlabel alignRight" valign="top">Hiring Manager/ Recruiter:<%=validAsterix %></td>
				<td >
					<% if(reqHiringManager != null && uF.parseToBoolean(reqHiringManager.get(0))) { %>
	                    <s:select theme="simple" name="hiringManager" cssClass="validateRequired" list="hrAndGlobalHrList" listKey="employeeId" id="hiringManager" listValue="employeeCode"
	                         size="5" multiple="true"></s:select>
					<% } else { %>
						<s:select theme="simple" name="hiringManager" list="hrAndGlobalHrList" listKey="employeeId" id="hiringManager" listValue="employeeCode" size="5" multiple="true"></s:select>
					<% } %>
				</td>
			</tr>
			
			<tr>
			<%
	            List<String> reqCustomerName = hmValidationFields.get("REQ_CUSTOMER_NAME"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqCustomerName != null && uF.parseToBoolean(reqCustomerName.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
				<td class="txtlabel alignRight">Customer Name:<%=validAsterix %></td>
				<td>
				<% if(reqCustomerName != null && uF.parseToBoolean(reqCustomerName.get(0))) { %>
                    <s:select theme="simple" name="strCustomer" id="strCustomer" cssClass="validateRequired" listKey="clientId" listValue="clientName" list="clientList" onchange="checkOtherCustomer(this.value);"/>
				<% } else { %>
					<s:select theme="simple" name="strCustomer" id="strCustomer" listKey="clientId" listValue="clientName" list="clientList" onchange="checkOtherCustomer(this.value);"/>
				<% } %>
				<div id="otherCustomerDiv" style="margin-top: 5px;">
					<% if(reqCustomerName != null && uF.parseToBoolean(reqCustomerName.get(0))) { %>
						<s:textfield name="strOtherCustomer" id="strOtherCustomer" cssClass="validateRequired" onblur ="getClient('otherCustomerMsgDiv', this.value)" onchange ="getClient('otherCustomerMsgDiv', this.value)"/>
					<% } else { %>
						<s:textfield name="strOtherCustomer" id="strOtherCustomer" onblur ="getClient('otherCustomerMsgDiv', this.value)" onchange ="getClient('otherCustomerMsgDiv', this.value)"/>
					<% } %>	
					<div id="otherCustomerMsgDiv"></div>
				</div>
				</td>
			</tr>
			
			<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(IConstants.INTELIMENT)) { %>
				<!-- Start Dattatray Date:21-08-21 -->
				<tr id="jd_category_id">
	            <%
		            List<String> reqJDCategory = hmValidationFields.get("REQ_JD_CATEGORY"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqJDCategory != null && uF.parseToBoolean(reqJDCategory.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
	                <td class="txtlabel alignRight">Category:<%=validAsterix %></td>
	                <td>
	                    <!-- Start Dattatray Date:25-08-21  -->
                <%
                	if(reqJDCategory != null && uF.parseToBoolean(reqJDCategory.get(0))) {
                %>
                    <s:select theme="simple" name="strCategory" id="strCategory" cssClass="validateRequired" headerKey="" headerValue="Select Category" list="#{'1':'Technical','2':'Functional'}"/>
                <% } else { %>
                	<s:select theme="simple" name="strCategory" id="strCategory" cssClass="" headerKey="" headerValue="Select Category" list="#{'1':'Technical','2':'Functional'}"/>
                <% } %>
                <!-- End Dattatray Date:25-08-21  -->
	                </td>
	            </tr>
	            
	             <tr>
	            <%
		            List<String> reqTechnology = hmValidationFields.get("REQ_TECHNOLOGY"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqTechnology != null && uF.parseToBoolean(reqTechnology.get(0))) {//Created Dattatray Date:25-08-21 Note:reqTechnology
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
	                <td class="txtlabel alignRight">Technology:<%=validAsterix %></td>
	                <td>
	                   <!-- Start Dattatray Date:25-08-21  -->
                 <%
                	if(reqTechnology != null && uF.parseToBoolean(reqTechnology.get(0))) {
                  %>
                    <s:select theme="simple" name="strTechnology" id="strTechnology" listKey="technologyId" listValue="technologyName" list="technologyList" cssClass="validateRequired"  headerKey="" headerValue="Select Technology"/>
                 <% } else { %>
                 	<s:select theme="simple" name="strTechnology" id="strTechnology" listKey="technologyId" listValue="technologyName" list="technologyList" cssClass=""  headerKey="" headerValue="Select Technology"/>
                 <% } %>
                 <!-- End Dattatray Date:25-08-21  -->
	                </td>
	            </tr>
	            <!-- End Dattatray Date:21-08-21 -->
            <% } %>
			<tr>
				<td class="txtlabel alignRight" valign="top">Share with:</td>
				<td> <span>
						<s:checkbox name="chkWebsite" id="chkWebsite" disabled="true" value="1"> Website</s:checkbox>
						<s:checkbox name="chkLinkedin" id="chkLinkedin" disabled="true"> Linkedin</s:checkbox>
						<s:checkbox name="chkFacebook" id="chkFacebook" disabled="true"> Facebook</s:checkbox>
						<s:checkbox name="chkTwitter" id="chkTwitter" disabled="true"> Twitter</s:checkbox>
					</span>	<br/>
					<span style="margin-left: 7px; font-size: 11px;"> <i>(This is for premium customer)</i> </span>
				</td>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td>
					<s:if test="recruitmentID==null">
					<s:submit cssClass="btn btn-primary"  id="strInsert" name="strInsert" value="Create Job Requirement" align="center" />
					</s:if>	
					<s:else>
						<s:submit cssClass="btn btn-primary" id="strInsert"  name="strInsert" value="Update Job Requirement" align="center" />
					</s:else>			
				</td>
			</tr>
		</table>
	</s:form>
</div>
<script>


<%if(frmPage != null){ %>
$("#formID_WO").submit(function(event){
	event.preventDefault();
	var from = '<%=frmPage%>';
	//alert("from ===>> " + from);
	var form_data = $("#formID_WO").serialize();
	if(from != null && (from == "RAD" || from == "JR" || from == "WF")) {
		var divResult = 'divResult';
		var strBaseUserType = document.getElementById("strBaseUserType").value;
		var strCEO = '<%=IConstants.CEO %>';
		var strHOD = '<%=IConstants.HOD %>';
		
		if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
			divResult = 'subDivResult';
		}
		
		var recruitmentID = "";
		if(document.getElementById("recruitmentID")) {
			recruitmentID = document.getElementById("recruitmentID").value;
		}
		//alert("recruitmentID ===>> " + recruitmentID);
		var orgID = document.getElementById("orgID").value;
		var wlocID = document.getElementById("wlocID").value;
		var desigID = document.getElementById("desigID").value;
		var checkStatus = document.getElementById("checkStatus").value;
		var fdate = document.getElementById("fdate").value;
		var tdate = document.getElementById("tdate").value;
		var currUserType = document.getElementById("currUserType").value;
		
		var strInsert = "Create Job Requirement";
		if(parseInt(recruitmentID) > 0) {
			strInsert = "Update Job Requirement";
		} 
		//alert("strInsert ===>> " + strInsert + " -- recruitmentID ===>> " +recruitmentID);	
		$.ajax({
			type :'POST',
			url  :'RequirementRequestWithoutWorkflow.action',
			data :form_data+"&strInsert="+strInsert,
			cache:true,
			success : function(result) {
				//$("#"+divResult).html(result);
				if(from != null && (from == "JR" || from == "WF")) {
					$.ajax({
						url: 'RecruitmentDashboard.action?fromPage='+from,
						cache: true,
						success: function(result){
							$("#"+divResult).html(result);
				   		}
					});
				} else {
					//alert("after success ");
					$.ajax({
						url: 'RequirementApproval.action?f_org='+orgID+'&location1='+wlocID+'&designation='+desigID+'&checkStatus='+checkStatus
							+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType,
						cache: true,
						success: function(result){
							//alert("after success -- result");
							$("#divResult").html(result);
				   		}
					});
				}
			},
			error : function(result) {
				//$("#"+divResult).html(result);
				if(from != null && (from == "JR" || from == "WF")) {
					$.ajax({
						url: 'RecruitmentDashboard.action?fromPage='+from,
						cache: true,
						success: function(result){
							$("#"+divResult).html(result);
				   		}
					});
				} else {
					$.ajax({
						url: 'RequirementApproval.action?f_org='+orgID+'&location1='+wlocID+'&designation='+desigID+'&checkStatus='+checkStatus
							+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType,
						cache: true,
						success: function(result){
							$("#divResult").html(result);
				   		}
					});
				}
			}
		});
		//alert("111");
	}
});
<% } %>
	</script>