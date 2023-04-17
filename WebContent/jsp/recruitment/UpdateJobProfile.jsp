<%@page import="com.konnect.jpms.select.FillGender"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript"> 
$("#minEducation").multiselect().multiselectfilter();
	function approvedRequest(divId, actionUrl) {
		var checkEmp;
		var publishProfile;
		//alert(document.getElementById("checkEmp").checked);
		if(document.getElementById("checkEmp")) {
			if (document.getElementById("checkEmp").checked == true) {
				checkEmp = 1;
			} else {
				checkEmp = 0;
			}
		}
		
		if(document.getElementById("publishProfile")) {
			if (document.getElementById("publishProfile").checked == true) {
				publishProfile = 1;
			} else {
				publishProfile = 0;
			}
		}
		//alert(checkEmp);
		getContent(divId, actionUrl+"&checkEmp="+checkEmp+"&publishProfile="+publishProfile);
		getReqDashboardData('JobProfilesApproval','');
		$(".nav-tabs-custom .nav-tabs").find("li").removeClass("active");
		$(document).find('a:contains(Job Profile Approvals)').parent().addClass("active");
		
	}


	function showConsultantList(checkval,value){
		 
		if(checkval == true){
			if (document.getElementById("consultantTR")){
				document.getElementById("consultantTR").style.display = "table-row";
			}
			
		} else {
			if (document.getElementById("consultantTR")){
				document.getElementById("consultantTR").style.display = "none";
			}
			
		}
	}
	
	
	function getSelectedValue(selectId) {
		var choice = document.getElementById(selectId);
		var exportchoice = "";
		for ( var i = 0, j = 0; i < choice.options.length; i++) {
			if (choice.options[i].selected == true) {
				if (j == 0) {
					exportchoice = choice.options[i].value;
					j++;
				} else {
					exportchoice += "," + choice.options[i].value;
					j++;
				}
			}
		}
		return exportchoice;
	} 
	
	 
	function checkUncheckValue() {
		var allEmp=document.getElementById("allEmp");		
		var strHiringEmpId = document.getElementsByName('strHiringEmpId');
		var selectID="";
		var status=false;
		if(allEmp.checked==true){
			status=true;
			 for(var i=0;i<strHiringEmpId.length;i++){
				 strHiringEmpId[i].checked = true;
				  if(i==0){
					  selectID=strHiringEmpId[i].value;
				  }else{
					  selectID+=","+strHiringEmpId[i].value;
				  }
			 }
		}else{		
			status=false;
			 for(var i=0;i<strHiringEmpId.length;i++){
				 strHiringEmpId[i].checked = false;
				  if(i==0){
					  selectID=strHiringEmpId[i].value;
				  }else{
					  selectID+=","+strHiringEmpId[i].value;
				  }
			 }
		}
		
		var empselect ="";
		if(document.getElementById("empselected")) {
			empselect=document.getElementById("empselected").value;
		}
		
		
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetSelectedEmployeeForHiring.action?type=one&chboxStatus="+status+"&selectedEmp="+selectID+"&existemp="+empselect,		
				cache : false,
				success : function(data) {
					//alert("data == "+data);
                	if(data == ""){
                		
                	}else{
                		var allData = data.split("::::");
                		if(document.getElementById("idEmployeeInfo")) {
                			 document.getElementById("idEmployeeInfo").innerHTML = allData[0];
                		}
                       
                	}
                }
			});
		}
	}
 
	function onloadFilterByOrg(){
		//alert("cbcbdf dfgd ");
		var strID = null;
		if(document.getElementById("strOrg")){
			//strID = getSelectedValue("strOrg");
			strID = document.getElementById("strOrg").value;  
	    }
		
	    getWLocDepartLevelDesigByOrg(strID);
	     if(document.getElementById("sourceofRec2"))  {
			var sourceofRec2 = document.getElementById("sourceofRec2").checked;
			//alert(sourceofRec2);
			showConsultantList(sourceofRec2,'');
	     }
	}
	
	
	function getWLocDepartLevelDesigByOrg(strID){
		//alert("strID ===> " + strID);
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetHiringFilters.action?strOrg="+strID ,		
				cache : false,
				success : function(data) {
					//alert("data == "+data);
                	if(data == ""){
                		
                	}else{
                		//alert("data ==>    "+data);
                		var allData = data.split("::::");
                		if(document.getElementById("wlocationDiv")) {
                			document.getElementById("wlocationDiv").innerHTML = allData[0];
                		}
                		if(document.getElementById("departDiv")) {
                			document.getElementById("departDiv").innerHTML = allData[1];
                		}
                		if(document.getElementById("levelDiv")) {
                			document.getElementById("levelDiv").innerHTML = allData[2];
                		}
                		if(document.getElementById("myDesig")) {
                			document.getElementById("myDesig").innerHTML = allData[3];
                		}
                   }
                }
			});
		}
		
		 window.setTimeout(function() {  
		    	getEmployeebyOrg();
			}, 200); 
	}
	
	 
	function getEmployeebyOrg(){
		var strID = null;
		if(document.getElementById("strOrg")){
			//strID = getSelectedValue("strOrg");
			strID = document.getElementById("strOrg").value; 
	    }
		
	    //alert("strID ===> " +  strID);
		var action = 'GetHiringEmployeeList.action?strOrg=' + strID;
		var cnt=0;
		
		var rslt = getContent('myEmployee', action);
		cnt++;
		//getWLocDepartLevelDesigByOrg(strOrg,'org');
		if(parseInt(cnt) != 0){
			searchTextField();
		}
	} 
	 
	function getEmployeebyLocation() {
		var location = getSelectedValue("wlocation");
		var strID = null;
		if(document.getElementById("strOrg")){
			//strID = getSelectedValue("strOrg");
			strID = document.getElementById("strOrg").value; 
	    }
	    var action = 'GetHiringEmployeeList.action?strOrg='+ strID +'&location='+ location;
		getContent('myEmployee', action);
		//getWLocDepartLevelDesigByOrg(strID,'wloc');
		searchTextField();
	}

	function getEmployeebyDepart() {
		var strID = null;
		if(document.getElementById("strOrg")){
			//strID = getSelectedValue("strOrg");
			strID = document.getElementById("strOrg").value; 
	    }
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");

		var action = 'GetHiringEmployeeList.action?depart=' + depart+'&strOrg='+ strID +'&location='+ location;
		
		/* if (location != '') {
			action += '&location=' + location;
		} */
		getContent('myEmployee', action);
		searchTextField();
	}

	function getEmployeebyLevel() {
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		var level = getSelectedValue("strLevel1");
		
		var strID = null;
		if(document.getElementById("strOrg")){
			//strID = getSelectedValue("strOrg");
			strID = document.getElementById("strOrg").value; 
	    }
		var action = 'GetHiringEmployeeList.action?level=' + level;
		
		if (location == '' && depart == '' && strID == '') {
		} else {
			if (strID != '') {
				action += '&strOrg=' + strID;
			}
			if (location != '') {
				action += '&location=' + location;
			}
			if (depart != '') {
				action += '&depart=' + depart;
			}
		}
		getContent('myEmployee', action);
		//getWLocDepartLevelDesigByOrg(strID,'level');
		window.setTimeout(function() {  
			getContent('myDesig', 'GetDesigfromLevel.action?pagefrom=updateJobProfile&strLevel=' + level);
		}, 200);      
			
		searchTextField();

	}

	function getEmployeebyDesig() {
		var strID = null;
		if(document.getElementById("strOrg")){
			//strID = getSelectedValue("strOrg");
			strID = document.getElementById("strOrg").value; 
	    }
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		var Level = getSelectedValue("strLevel1");
		var design = getSelectedValue("desigIdV");

		var action = 'GetHiringEmployeeList.action?design=' + design;
		
		if (location == '' && depart == '' && Level == '' && strID == '') {

		} else {
			if (strID != '') {
				action += '&strOrg=' + strID;
			}
			if (location != '') {
				action += '&location=' + location;
			}
			if (depart != '') {
				action += '&depart=' + depart;
			}
			if (Level != '') {
				action += '&level=' + Level;
			}
		}
			//alert("action ==> " + action); 
		getContent('myEmployee', action);
			/* window.setTimeout(function() {
				getContent('myGrade', 'getGrade.action?strDesignation=' + design);
			}, 200); */
		searchTextField();
	}
	
	 
	function getEmployeebyGrade() {
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		var Level = getSelectedValue("strLevel1");
		var design = getSelectedValue("desigIdV");
		var grade = getSelectedValue("gradeIdV");

		var action = 'GetHiringEmployeeList.action?grade=' + grade;

		//document.getElementById("employee").selectedIndex = 0;
	
			if (location != '')  {
				action += '&location=' + location;
			}if (depart != '')  {
				action += '&depart=' + depart;
			}if (Level != '')  {
				action += '&level=' + Level;
			}if (design != '')  {
				action += '&design=' + design;
			}
			
			getContent('myEmployee', action);
			searchTextField();
	}
	
	
function getHiringSelectedEmp(checked,emp){
	if(checked == true) {
		alrtMsg = "Are you sure, you want to add this employee?";
	} else {
		alrtMsg = "Are you sure, you want to remove this employee?";
	}
	if(confirm(alrtMsg)) {
		var empselect=document.getElementById("empselected").value;
		//alert(empselect);
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
				url : "GetSelectedEmployeeForHiring.action?type=one&chboxStatus="+checked+"&selectedEmp="+emp+"&existemp="+empselect,		
				cache : false,
				success : function(data) {
					//alert("data == "+data);
                	if(data == ""){
                		
                	}else{
                		var allData = data.split("::::");
                        document.getElementById("idEmployeeInfo").innerHTML = allData[0];
                	}
                }
			});
		} 
	}
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
	

	function openPanelEmpProfilePopup(empId) {
		/* removeLoadingDiv('the_div'); */
		var dialogEdit = '#PanelEmpProfilePopup';
			/* dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
					.appendTo('body'); */
			$(dialogEdit).dialog({
				autoOpen : false,
				bgiframe : true,
				resizable : false, 
				height : 750,  
				width : 1170,
				modal : true,
				title : 'Employee Information',
				open : function() {
					var xhr = $.ajax({
						url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
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

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
 		/* jQuery("#formID1").validationEngine(); */

		onloadFilterByOrg();
				
		$('#lt').dataTable({
			bJQueryUI : true,
			"sPaginationType" : "full_numbers",
			"aaSorting" : []
		})
	}); 
	
	function searchTextField(){
		//alert("searchTextField");
		$('#lt1').dataTable({
			bJQueryUI : true,
			"sPaginationType" : "full_numbers",
			"aaSorting" : []
		});
	}
</script>

<style>

	.txtlabel{
		color: #777777;
	    font-family: verdana,arial,helvetica,sans-serif;
	    font-size: 11px;
	    font-style: normal;
	    font-weight: 600;
	    padding: 0px;
	    width: 190px;
	}

</style>

<div 
<%String viewCheck=(String)request.getAttribute("view") ;
if(viewCheck==null){%>
id="updateJobProfileDIV"
<%} %>
>

	<%
		String strUserType =(String) session.getAttribute(IConstants.USERTYPE); 
		List<String> jobProfileList = (List<String>) request.getAttribute("jobProfileList");
		List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
		Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
		Map<String, String> hmSourceOfReq = (Map<String, String>) request.getAttribute("hmSourceOfReq");
		Map<String, String> hmAdvMedia = (Map<String, String>) request.getAttribute("hmAdvMedia");
		UtilityFunctions uF = new UtilityFunctions();
		String view = (String) request.getAttribute("view");
		String frmPage = (String) request.getAttribute("frmPage");
		String currUserType = (String) request.getAttribute("currUserType");
		StringBuilder sbApproveDeny = new StringBuilder();
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		//EncryptionUtils EU = new EncryptionUtils();// Created by Dattatray Date : 21-07-21 Note : Encryption
		
		//System.out.println("jobProfileList in jsp ===>> " + jobProfileList);
	%>


	<s:form id="formID" name="frmUpdateJobProfile" theme="simple" action="UpdateJobProfile" method="POST" cssClass="formcss" enctype="multipart/form-data">
		
		<s:hidden name="pageFrom"></s:hidden>
		<s:hidden name="orgID" id="orgID"></s:hidden> 
       <s:hidden name="wlocID" id="wlocID"></s:hidden>
       <s:hidden name="desigID" id="desigID"></s:hidden>
       <s:hidden name="checkStatus" id="checkStatus"></s:hidden>
       <s:hidden name="fdate" id="fdate"></s:hidden>
       <s:hidden name="tdate" id="tdate"></s:hidden>
	  
	   <s:hidden name="currUserType" id="currUserType"></s:hidden>
	   <input type="hidden" name="frmPage" id=frmPage value="<%=frmPage %>"/>
	   <input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
	   <table class="table table_no_border">

			<tr>
				<th class="txtlabel alignRight">
				<input type="hidden" name="recruitID" id="recruitID" value="<%=jobProfileList.get(0)%>" />
				<input type="hidden" name="updateJobProfile" id="updateJobProfile" value="update" />
				Job ID:   </th>
				<td><%=jobProfileList.get(6)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Job Title:</th>
				<td><%=jobProfileList.get(35)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Designation:</th>
				<td><%=jobProfileList.get(1)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Grade:</th>
				<td><%=jobProfileList.get(2)%></td>
			</tr>
			<!-- Added later -->
			<tr>
				<th class="txtlabel alignRight">Organisation:   </th>
				<td><%=jobProfileList.get(17)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Work Location:   </th>
				<td><%=jobProfileList.get(3)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Ideal Candidate:   </th>
				<td><%=jobProfileList.get(18)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Essential Skills:</th>
				<td><%=jobProfileList.get(22)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Desirable Skills:</th>
				<td><%=jobProfileList.get(19)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">No. of Position(s):</th>
				<td><%=jobProfileList.get(4)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Type of Employment:</th>
				<td><%=jobProfileList.get(34)%></td>
			</tr>
			<%if(jobProfileList.get(23) != null && !jobProfileList.get(23).equals("") && (jobProfileList.get(23).equals("2") || jobProfileList.get(23).equals("3"))) { %>
			<tr>
				<th class="txtlabel alignRight">If Temporary/Casual,plase give justification & period required for:</th>
				<td><%=jobProfileList.get(30)%></td>
			</tr>
			<% } %>
			
			<tr>
				<th class="txtlabel alignRight">Vacancy:</th>
				<td><%=jobProfileList.get(33)%></td>
			</tr>
			<%if(jobProfileList.get(26) != null && !jobProfileList.get(26).equals("") && jobProfileList.get(26).equals("1")) { %>
			<tr>
				<th class="txtlabel alignRight">Give justification:</th>
				<td><%=jobProfileList.get(27)%></td>
			</tr>
			<% } %>
			
			<%if(jobProfileList.get(26) != null && !jobProfileList.get(26).equals("") && jobProfileList.get(26).equals("0")) { %>
			<tr>
				<th class="txtlabel alignRight">Name of person to be replaced for:</th>
				<td><%=jobProfileList.get(28)%></td>
			</tr>
			<% } %>
			<tr>
				<th class="txtlabel alignRight">Reporting to:</th>
				<td><%=jobProfileList.get(29)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight" valign="top">Hiring Manager/ Recruiter:</th>
				<td><%=jobProfileList.get(36)%></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Customer Name:</th>
				<td><%=jobProfileList.get(37)%></td>
			</tr>
		
			<tr>
				<th class="txtlabel alignRight" valign="top">Notes:</th>
				<td><%=jobProfileList.get(5)%></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight" valign="top">Job Description:</th>
				<td>
				<% if (view == null) { %> 
					<textarea name="jobDescription" class=" form-control" rows="10" cols="5" style="width:350px;height:100px;" id="editor1"><%=jobProfileList.get(7)%></textarea> 
				<% } else { %>
					<%=jobProfileList.get(7)%> 
				<% } %>
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight" valign="top">Candidate Profile:</th>
				<td>
					<% if (view == null ) { %>
						<textarea name="candidateProfile" rows="10" class=" form-control" cols="5" style="width:350px;height:100px;" id="editor2"><%=jobProfileList.get(13)%></textarea> 
					<% } else { %>
						<%=jobProfileList.get(13)%> 
					<% } %>
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Gender:</th>
				<td>
				<%List<FillGender> genderList = (List<FillGender>)request.getAttribute("genderList"); %>
					<% if (view == null ) { %>
						<select name="gender" id="gender" style="width: 80px;" class="form-control ">
							<option value="0">Any</option>
							<% for (int i = 0; i < genderList.size(); i++) {
								//System.out.println("genderList.get(i).getGenderId() ----- >>>> " + genderList.get(i).getGenderId());
								//System.out.println("jobProfileList.get(32) ----- >>>> " + jobProfileList.get(32));
								if (genderList.get(i).getGenderId().equals(jobProfileList.get(24))) {
							%>
							<option value="<%=genderList.get(i).getGenderId()%>" selected="selected"><%=genderList.get(i).getGenderName()%></option>
							<% } else { %>
							<option value="<%=genderList.get(i).getGenderId()%>"><%=genderList.get(i).getGenderName()%></option>
							<% } } %>
						</select>
					<% } else { %> 
						<%=jobProfileList.get(32) %>
					<% } %>	
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Min. Age:</th>
				<td>
					<% if (view == null ) { %> 
						<select name="minAge" id="minAge" style="width: 100px;" class="form-control ">
							<%
							for (int i = 0; i <=42; i++) {
								int minAge = 18;
								minAge += i;
								if (minAge == uF.parseToInt(jobProfileList.get(25))) {
							%>
								<option value="<%=minAge%>" selected="selected"><%=minAge%> Years</option>
							<% } else if (i == 0) { %>
								<option value="0" selected="selected">Select Age</option>
							<% }else { %>
								<option value="<%=minAge%>"><%=minAge%> Years</option>
							<% } } %>
						</select>
					<% } else if(uF.parseToInt(jobProfileList.get(25)) == 0) { %>
						 No Age Required.
					<% } else { %> 
						<%=jobProfileList.get(25)%> Years		 
					<% } %>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Min. CTC:</th>
				<td>
					<% if (view == null ) { %> 
						<s:textfield name="strMinCTC" id="strMinCTC" cssClass="validateRequired form-control " cssStyle="width:120px;" onkeypress="return isNumberKey(event)"/>
					<% } else if(uF.parseToDouble(jobProfileList.get(38)) == 0) { %>
						 No Min. CTC Required.
					<% } else { %> 
						<%=jobProfileList.get(38) %>		 
					<% } %>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Max. CTC:</th>
				<td>
					<% if (view == null ) { %> 
						<s:textfield name="strMaxCTC" id="strMaxCTC" cssClass="validateRequired form-control " cssStyle="width:120px;" onkeypress="return isNumberKey(event)"/>
					<% } else if(uF.parseToDouble(jobProfileList.get(39)) == 0) { %>
						 No Max. CTC Required.
					<% } else { %> 
						<%=jobProfileList.get(39) %>		 
					<% } %>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Min. Experience:</th>
				<td>
					<% if (view == null ) { %>
					 Year&nbsp;&nbsp;<select name="minYear" id="minYear" style="width: 50px !important;" class="form-control ">
						<%
							for (int i = 0; i <= 20; i++) {
								if (i == uF.parseToInt(jobProfileList.get(8))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<%
							}
								}
						%>
				</select>&nbsp;&nbsp;Month &nbsp;&nbsp;<select name="minMonth" id="minMonth" style="width: 50px !important;" class="form-control ">
						<%
							for (int i = 0; i < 12; i++) {
										if (i == uF.parseToInt(jobProfileList.get(9))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<%
							}
								}
						%>
				</select> 
				<% } else if(uF.parseToInt(jobProfileList.get(8)) == 0 && uF.parseToInt(jobProfileList.get(9)) == 0) { %>
						 No Experience Required.
					<% } else { %>
						<%=uF.parseToInt(jobProfileList.get(8))%>Years and <%=uF.parseToInt(jobProfileList.get(9))%>Months
					<% } %>
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Max. Experience:</th>
				<td>
					<% if (view == null) { %>
					Year&nbsp;&nbsp;<select name="maxYear" id="maxYear" style="width: 50px !important;" class="form-control ">
						<%
							for (int i = 0; i <= 20; i++) {
										if (i == uF.parseToInt(jobProfileList.get(10))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<%
							}
								}
						%>
				</select>&nbsp;&nbsp;Month &nbsp;&nbsp;<select name="maxMonth" id="maxMonth" style="width: 50px !important;" class="form-control ">
						<%
							for (int i = 0; i < 12; i++) {
								if (i == uF.parseToInt(jobProfileList.get(11))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<%
							}
					}
						%>
				</select> 
				<% } else if(uF.parseToInt(jobProfileList.get(10)) == 0 && uF.parseToInt(jobProfileList.get(11)) == 0) { %>
					 No Experience Required.
				<% } else { %>
					<%=uF.parseToInt(jobProfileList.get(10))%>Years and <%=uF.parseToInt(jobProfileList.get(11))%>Months
				<% } %>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Essential Qualification:</th>
				<td>
					<% if (view == null) { %>
					<%-- <input type="text" name="essentialQualification" id="essentialQualification" value="<%=uF.showData(jobProfileList.get(20), "")%>"> --%>
					<s:select list="essentialEduList" theme="simple" cssClass=" form-control " listKey="eduId" listValue="eduName" name="essentialQualification" id="essentialQualification"
					 headerKey="" headerValue="Select Education" cssStyle="width:170px"></s:select>
					<% } else if(jobProfileList.get(20)==null || jobProfileList.get(20).equals("")) { %>
					 Essential Qualificatin Not Specified.
				<% } else { %> 
					<%=jobProfileList.get(20)%> 
 				<% } %>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Desirable Qualification:</th>
				<td>
					<% if (view == null) { %>
					<s:select list="eduList" theme="simple" cssClass=" form-control " listKey="eduId" listValue="eduName" name="minEducation" id="minEducation" headerKey="" 
					cssStyle="width:170px" value="eduListSelected" multiple="true" size="4"></s:select>
					<% }   else if(jobProfileList.get(12)==null || jobProfileList.get(12).equals("")) { %>
					
					 Desirable Qualificatin Not Specified.
					 
				<% } else { %>
					<%=jobProfileList.get(12)%> 
 				<% } %>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Alternate Qualification:</th>
				<td>
					<% if (view == null) { %>
					<input type="text" class="form-control " name="alternateQualification" id="alternateQualification" value="<%=uF.showData(jobProfileList.get(21), "")%>">
					<%-- <s:select list="eduList" theme="simple" listKey="eduName" listValue="eduName" name="minEducation" id="minEducation"
					headerKey="" headerValue="Select Education" cssStyle="width:170px" value="eduListSelected" multiple="true" size="4"></s:select> --%>
					<% }   else if(jobProfileList.get(21)==null || jobProfileList.get(21).equals("")) { %>
					
					 Alternate Qualificatin Not Specified.
					 
				<% } else { %> 
					<%=jobProfileList.get(21)%> 
				<% } %>
				</td>
			</tr>
			
			
			<tr>
				<th class="txtlabel alignRight" valign="top">Additional	Information:</th>
				<td>
				<% if (view == null ) { %>
					<textarea name="addInformation" rows="10" class=" form-control" cols="5" style="width:350px;height:100px;" id="editor3"><%=jobProfileList.get(14)%></textarea> 
				<% }  else if(jobProfileList.get(14)==null || jobProfileList.get(14).equals("")) { %>
					
					 Additional	Information Not Specified.
				<% }else { %>
					<%=jobProfileList.get(14)%> 
				<% } %>
				</td>
			</tr>
			
			<% if (view == null) { %>
			<tr>
				<td colspan="2" class="txtlabel alignCenter" valign="top">To be filled in by HR & ADMIN</td>
			</tr>
			<tr>
				<td colspan="2" class="txtlabel">
				<table class="table table-bordered" border="0" style="float: left;font-size:12px;padding: 0px;" width="100%" >
				<tr>
					<td class="txtlabel alignCenter">Source of Recruitment</td>
					<td class="txtlabel alignCenter">Advertisement Media</td>
				</tr>
				<tr>
					<td class="txtlabel" valign="top">
						<input type="checkbox" name="sourceofRec" id="sourceofRec1" value="Advertisement" onclick="" <%=hmSourceOfReq.get("_ADV") != null ? hmSourceOfReq.get("_ADV") : "" %>>Advertisement 
						<br/><input type="checkbox" name="sourceofRec" id="sourceofRec2" value="Consultant" onclick="showConsultantList(this.checked,this.value);" <%=hmSourceOfReq.get("_CON") != null ? hmSourceOfReq.get("_CON") : "" %>>Consultant
						<br/><input type="checkbox" name="sourceofRec" id="sourceofRec3" value="Reference" onclick="" <%=hmSourceOfReq.get("_REF") != null ? hmSourceOfReq.get("_REF") : "" %>>Reference
						<br/><input type="checkbox" name="sourceofRec" id="sourceofRec4" value="Colleges" onclick="" <%=hmSourceOfReq.get("_COL") != null ? hmSourceOfReq.get("_COL") : "" %>>Colleges
						<br/><input type="checkbox" name="sourceofRec" id="sourceofRec5" value="EmployementExchange" onclick="" <%=hmSourceOfReq.get("_EMP") != null ? hmSourceOfReq.get("_EMP") : "" %>>Employement Exchange
						<br/><input type="checkbox" name="sourceofRec" id="sourceofRec6" value="Inhouse" onclick="" <%=hmSourceOfReq.get("_INH") != null ? hmSourceOfReq.get("_INH") : "" %>>Inhouse
					</td>
					<td class="txtlabel" valign="top">
						<input type="checkbox" name="advertisementMedia" id="advertisementMedia" value="Periodicals" <%=hmAdvMedia.get("_PERI") != null ? hmAdvMedia.get("_PERI") : "" %>>Periodicals 
						<br/><input type="checkbox" name="advertisementMedia" id="advertisementMedia" value="Magazines" <%=hmAdvMedia.get("_MAGA") != null ? hmAdvMedia.get("_MAGA") : "" %>>Magazines
						<br/><input type="checkbox" name="advertisementMedia" id="advertisementMedia" value="Newspaper" <%=hmAdvMedia.get("_NEWS") != null ? hmAdvMedia.get("_NEWS") : "" %>>Newspaper
						<br/><input type="checkbox" name="advertisementMedia" id="advertisementMedia" value="Websites" <%=hmAdvMedia.get("_WEBS") != null ? hmAdvMedia.get("_WEBS") : "" %>>Websites
						<br/><input type="checkbox" name="advertisementMedia" id="advertisementMedia" value="Anyother" <%=hmAdvMedia.get("_ANYO") != null ? hmAdvMedia.get("_ANYO") : "" %>>Any other,please specify
					</td>
				</tr>
				</table>
				</td>
			</tr>
			<tr id="consultantTR" style="display: none;">
				<td colspan="2" valign="top">
				<table class="table" style="width: 100%">
						<tr>
							<td colspan="5">
							<div class="filter_div" style="float: left; width: 97.7%;">
							<div style="float: left; padding-left: 10px;"><strong>Filter:</strong> </div>
							 			<div style="float: left; padding-left: 10px;">
			                               <s:select theme="simple" name="strOrg" list="orgList" id="strOrg" listKey="orgId" listValue="orgName" 
			                               	required="true" cssClass=" form-control autoWidth"  onchange="getWLocDepartLevelDesigByOrg(this.value)" cssStyle="width:150px;"></s:select>
				                        </div>
				                        <div id="wlocationDiv" style="float: left; padding-left: 10px;">
			                            	<s:select theme="simple" cssClass=" form-control autoWidth" name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName" headerKey=""
			                                   headerValue="All WorkLocation" required="true" value="{strEmpWLocId}" onchange="getEmployeebyLocation();" cssStyle="width:150px;"></s:select>
			                            </div> 
			                            <div id="departDiv" style="float: left; padding-left: 10px;">
			                               <s:select theme="simple" cssClass=" form-control autoWidth" name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
			                                   headerValue="All Department" required="true" onchange="getEmployeebyDepart();" cssStyle="width:150px;"></s:select>
										</div>
										<div id="levelDiv" style="float: left; padding-left: 10px;">
			                               <s:select theme="simple" cssClass=" form-control autoWidth" name="strLevel1" list="levelList" listKey="levelId" id="strLevel1" listValue="levelCodeName" headerKey=""
			                                   headerValue="All Level" required="true" onchange="getEmployeebyLevel()" cssStyle="width:150px;"></s:select>
										</div>
			                           <div id="myDesig" style="float: left; padding-left: 10px;">
			                                   <s:select theme="simple" cssClass=" form-control autoWidth" name="strDesignation" list="designationList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
			                                       headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();" cssStyle="width:150px;"></s:select>
			                           </div>
							</div>
							
								<div id="myEmployee" style="float: left; width: 60%; overflow-y: auto; height: 300px; margin-top: 10px; margin-left: 4px; border: 2px solid #ccc;">
								<table id="table" class="tb_style" style="width: 100%">
									<%
										if (empList != null && !empList.equals("") && !empList.isEmpty()) {
											Map<String, String> hmEmpLocation = (Map<String, String>)request.getAttribute("hmEmpLocation");
											Map<String, String> hmWLocation = (Map<String, String>)request.getAttribute("hmWLocation");
											Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
									%>
									<thead>
									<tr>
										<th width="10%"><input onclick="checkUncheckValue();"
											type="checkbox" name="allEmp" id="allEmp">
										</th>
										<th align="center">Employee</th>
										<th align="center">Designation</th>
										<th align="center">Location</th>
									</tr>
			</thead>
			<tbody>
									<%
										for (int i = 0; i < empList.size(); i++) {
			
														String empID = ((FillEmployee) empList.get(i)).getEmployeeId();
														String empName = ((FillEmployee) empList.get(i)).getEmployeeName();
			
														String emplocationID = (empID == null || empID.equals("")) ? "" : hmEmpLocation.get(empID);
														String location = (emplocationID == null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");
			
														String desig = (empID == null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID), "");
									%>
									<tr>
										 <td><input type="checkbox" name="strHiringEmpId" id="strHiringEmpId<%=i%>" onclick="getHiringSelectedEmp(this.checked,this.value);"
											value="<%=empID%>" <%if (hmCheckEmpList != null && hmCheckEmpList.get(empID) != null) {%>
											checked="checked" <%}%>><%--  --%>
										</td>
										<!-- Created by Dattatray Date:21-07-21 Note:empId encryption -->
										<td><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID %>')"><%=uF.showData(empName,"") %></a></td>
										<td><%=desig%></td>
										<td><%=location%></td>
									</tr>
									<%
										}
												} else {
									%>
									<tr>
										<td colspan="3"><div class="nodata msg" style="width: 88%">
												<span>No Employee Found</span>
											</div></td>
									</tr>
									<% } %>
									</tbody>
								</table>
								</div>
							<!-- </td>
							<td> -->
							
							<div id="idEmployeeInfo" style="float: left; left: 70%; top: 46px; width: 350px; padding: 5px; overflow-y: auto; border: 2px solid rgb(204, 204, 204); margin-left: 20px; margin-top: 10px; height: 290px;">
								<%
									List<String> selectEmpList = (List<String>) request.getAttribute("selectEmpList");
											if (selectEmpList != null) {
								%>
								<div style="border: 2px solid #ccc;">
									<div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
										<%
											for (int i = 0; i < selectEmpList.size(); i++) {
										%>
										<div style="float: left; width: 100%; margin: 5px;"><strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=selectEmpList.get(i)%></div>
										<% } %>
								</div>
								<% } else { %>
								<div class="nodata msg" style="width: 85%">
									<span>No Employee selected</span>
								</div>
								<% } %>
								<input type="hidden" name="empselected" id="empselected" value="<%=uF.showData((String)request.getAttribute("empselected"), "0") %>"/>
							</div>
							
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<% } %>
			
			
			<% if (view != null && view.equals("view")) { %>
			<tr>
				<td colspan="2" class="txtlabel alignCenter" valign="top">Send mails to employees for reference : 
				<input type="checkbox" name="checkEmp" id="checkEmp" value="1"/></td>
				<!-- <td><input type="checkbox" name="checkEmp" id="checkEmp" value="1" />
				</td> -->
			</tr>
			<tr>
				<td colspan="2" class="txtlabel alignCenter" valign="top">Publish Profile : 
				<input type="checkbox" name="publishProfile" id="publishProfile" value="1"/></td>
				<!-- <td><input type="checkbox" name="publishProfile" id="publishProfile" value="1" />
				</td> -->
			</tr>
			<% } %>
			<tr>
				<!-- <td>&nbsp;</td> -->
				<td colspan="2" align="center">
				 <%
 			if (view != null && view.equals("view")) {
 				//System.out.println("strUserType ===> " + strUserType);
 				if (uF.parseToInt(jobProfileList.get(15)) == 0) {

 				sbApproveDeny.append("<div id=\"myDivM1\"> ");
 				sbApproveDeny.append("<input type=\"button\" class=\"btn btn-primary\" value=\"Approve\" align=\"center\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))"
 						+"approvedRequest('myDivM1','JobADRequest.action?S=1&RID="+ jobProfileList.get(0) + "&currUserType="+currUserType+"');\" /> ");
 								
				sbApproveDeny.append("<input type=\"button\" class=\"btn btn-danger\" value=\"Decline\" align=\"center\" onclick=\"if(confirm('Are you sure, you want to decline this request?'))denyProfile('1','"+ jobProfileList.get(0)	+ "','"+currUserType+"');\">");
			
 				if(view!=null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER)) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) { 
 				sbApproveDeny.append("<input type=\"button\" class=\"btn btn-primary\" style=\"margin-left:3px;\" value=\"Edit\" align=\"center\" onclick=\"closePopUP('"+currUserType+"');\" ");
              	 } 
 			
 				sbApproveDeny.append("</div>");

 			} else if (uF.parseToInt(jobProfileList.get(15)) == 1) {
 				sbApproveDeny
 						/* .append("<img src=\"images1/icons/approved.png\" title=\"Approved\" />  "); */
 				     .append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"  title=\"Approved\" ></i> ");
 			} else if (uF.parseToInt(jobProfileList.get(15)) == -1) {
 				/* sbApproveDeny.append("<img src=\"images1/icons/denied.png\" title=\"Denied\" />"); */
 				sbApproveDeny.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i> ");
 			}
			 %> <%=sbApproveDeny.toString()%> 
			<% } else if(view==null) { %> 
			 
					 <%if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
						 	<s:submit cssClass="btn btn-primary" name="update" value="Update" align="center" /> 
							<s:submit cssClass="btn btn-primary" name="updateApprove" value="Update and Approve" align="center" /> 
						<% } else { %>
							<s:submit cssClass="btn btn-primary" value="Update and Send for Confirmation" align="center" /> 
					<% } %>
			<% } %>
	
				</td>
			</tr>
		</table>
		
	</s:form>

<script>

	$("input[type='submit']").click(function(){
		$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formID").find('.validateRequired').filter(':visible').prop('required',true);
		for ( instance in CKEDITOR.instances ) {
            CKEDITOR.instances[instance].updateElement();
        }  
	});

	$("#formID").submit(function(event){
		event.preventDefault();
		var from = '<%=frmPage%>';
		if(from != null) {
			updateRequest('<%=currUserType%>', from);
		}
	});

	function updateRequest(currUserType,from) {
		
		var orgID = document.getElementById("orgID").value;
		var wlocID = document.getElementById("wlocID").value;
		var desigID = document.getElementById("desigID").value;
		var checkStatus = document.getElementById("checkStatus").value;
		var fdate = document.getElementById("fdate").value;
		var tdate = document.getElementById("tdate").value;
		
		var form_data = $("#formID").serialize();
		$.ajax({
			type :'POST',
			url  :'UpdateJobProfile.action',
			data :form_data,
			cache:true/* ,
			success : function(result) {
				if(from != "" && from == "WF") {
					$("#divWFResult").html(result);
				}else {
					$("#divResult").html(result);	
				}
			} */ 
		});
		
		$.ajax({
			url: 'JobProfilesApproval.action?f_org='+orgID+'&location1='+wlocID+'&designation='+desigID+'&checkStatus='+checkStatus
				+'&fdate='+fdate+'&tdate='+tdate+'&fromPage='+from,
			cache: true,
			success: function(result){
				if(from != "" && from == "WF") {
					$("#divWFResult").html(result);
				} else {
					$("#divResult").html(result);	
				}
	   		}
		});
	}
	
	// Replace the <textarea id="editor1"> with an CKEditor instance.
	if(document.getElementById("editor1")){
		CKEDITOR.replace( 'editor1', {
			on: {
				focus: onFocus,
				blur: onBlur,
				
				// Check for availability of corresponding plugins.
				pluginsLoaded: function( evt ) {
					var doc = CKEDITOR.document, ed = evt.editor;
					if ( !ed.getCommand( 'bold' ) )
						doc.getById( 'exec-bold' ).hide();
					if ( !ed.getCommand( 'link' ) )
						doc.getById( 'exec-link' ).hide();
				}
			}
		});
	}
	
	if(document.getElementById("editor2")){
		CKEDITOR.replace( 'editor2', {
			on: {
				focus: onFocus,
				blur: onBlur,
				
				// Check for availability of corresponding plugins.
				pluginsLoaded: function( evt ) {
					var doc = CKEDITOR.document, ed = evt.editor;
					if ( !ed.getCommand( 'bold' ) )
						doc.getById( 'exec-bold' ).hide();
					if ( !ed.getCommand( 'link' ) )
						doc.getById( 'exec-link' ).hide();
				}
			}
		});
	}
	
	if(document.getElementById("editor3")){
		CKEDITOR.replace( 'editor3', {
			on: {
				focus: onFocus,
				blur: onBlur,
				
				// Check for availability of corresponding plugins.
				pluginsLoaded: function( evt ) {
					var doc = CKEDITOR.document, ed = evt.editor;
					if ( !ed.getCommand( 'bold' ) )
						doc.getById( 'exec-bold' ).hide();
					if ( !ed.getCommand( 'link' ) )
						doc.getById( 'exec-link' ).hide();
				}
			}
		});
	}

</script>

