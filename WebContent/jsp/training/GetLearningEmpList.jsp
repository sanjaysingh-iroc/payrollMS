<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
		
$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	
	$("#f_level").multiselect().multiselectfilter();
	$('#lt').DataTable();
});

	function checkUncheckValue(boolPublished) {
		
		//alert("fdsafasda dasd asasdasd ........");
		var allEmp=document.getElementById("allEmp");		
		var strTrainerId = document.getElementsByName('strTrainerId');
		var selectID="";
		var status=false;
		if(allEmp.checked==true){
			status=true;
			 for(var i=0;i<strTrainerId.length;i++){
				 strTrainerId[i].checked = true;
				  if(i==0){
					  selectID=strTrainerId[i].value;
				  }else{
					  selectID+=","+strTrainerId[i].value;
				  }
			 }
		}else{		
			status=false;
			 for(var i=0;i<strTrainerId.length;i++){
				 strTrainerId[i].checked = false;
				  if(i==0){
					  selectID=strTrainerId[i].value;
				  }else{
					  selectID+=","+strTrainerId[i].value;
				  }
			 }
		}
		
		var empselect=document.getElementById("empselected").value;
		var oldempids=document.getElementById("oldempids").value;
		
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
				url : "GetSelectedEmployeeAjax.action?type=one&chboxStatus="+status+"&selectedEmp="+selectID+"&existemp="+empselect
						+"&oldempids="+oldempids+"&boolPublished="+boolPublished+"&pageFrom=IFRAME",		
				cache : false,
				success : function(data) {
					//alert("data == "+data);
                	if(data == "") {
                		
                	}else{
                		var allData = data.split("::::");
                        document.getElementById("idEmployeeInfo").innerHTML = allData[0];
                        parent.document.getElementById("selectedEmpIdSpan").innerHTML = allData[1];
                	}
                }
			});
		}
		 
	}
	
	
function getSelectedLearner(checked,emp, boolPublished){
	//alert("boolPublished -----> " + boolPublished);
	var empselect=document.getElementById("empselected").value;
		var oldempids=document.getElementById("oldempids").value;
		//alert("checked ==> " + checked + " emp ==> " + emp+ " boolPublished ==> " + boolPublished);
	var alrtMsg = "";
	if(checked == true) {
		alrtMsg = "Are you sure, you want to add this learner?";
	} else {
		alrtMsg = "Are you sure, you want to remove this learner?";
	}
	if(confirm(alrtMsg)) {	
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetSelectedEmployeeAjax.action?type=one&chboxStatus="+checked+"&selectedEmp="+emp+"&existemp="+empselect+
						"&oldempids="+oldempids+"&boolPublished="+boolPublished+"&pageFrom=IFRAME",		
				cache : false,
				success : function(data) {
					//alert("data == "+data);
                	if(data == ""){
                		
                	}else{
                		var allData = data.split("::::");
                        document.getElementById("idEmployeeInfo").innerHTML = allData[0];
                        parent.document.getElementById("selectedEmpIdSpan").innerHTML = allData[1];
                        if(checked == 'false') {
                        	//alert(checked);
                        	document.getElementById("strTrainerId"+emp).checked = false;
                        }
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

function openCloseFilter() {
		
		var filterStatus = document.getElementById("filterStatus").value;
		
		if(filterStatus == "0") {
			document.getElementById("multiFilterDiv").style.display = "block";
			document.getElementById("FLTRuparrowSpan").style.display = "block";
			document.getElementById("FLTRdownarrowSpan").style.display = "none";
			document.getElementById("filterStatus").value = "1";
		} else {
			document.getElementById("multiFilterDiv").style.display = "none";
			document.getElementById("FLTRuparrowSpan").style.display = "none";
			document.getElementById("FLTRdownarrowSpan").style.display = "block";
			document.getElementById("filterStatus").value = "0";
		}
	}
    
</script> 
	
<%
	UtilityFunctions uF = new UtilityFunctions();
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
	//Map<String, String> hmExistTrainer = (Map<String, String>) request.getAttribute("hmExistTrainer");
	String planId = (String) request.getParameter("lPlanID");
	boolean boolPublished = (Boolean)request.getAttribute("boolPublished");
	
	Map<String,String> hmEmpLocation = (Map<String,String>)request.getAttribute("hmEmpLocation");
	Map<String, String> hmWLocation = (Map<String,String>)request.getAttribute("hmWLocation"); 
	Map<String, String> hmEmpCodeDesig = (Map<String,String>)request.getAttribute("hmEmpCodeDesig");
	List<String> alAttendEmp = (List<String>)request.getAttribute("alAttendEmp");
	if(alAttendEmp == null) alAttendEmp = new ArrayList<String>();
%>
<fieldset style="height: 520px;">
<legend>Add Learners</legend>
<s:form name="frmGetLearningEmpList" id="frmGetLearningEmpList" action="GetLearningEmpList" theme="simple">
		<input type="hidden" name="lPlanID" id="lPlanID" value="<%=planId %>" />
		<input type="hidden" name="filterStatus" id="filterStatus" value="0" />
		<s:hidden name="alignWith" id="alignWith"></s:hidden>
		<div class="filter_caption">
			<a href="javascript:void(0)" onclick="openCloseFilter();"><span style="float: left;">Filter</span>
			<span id="FLTRdownarrowSpan" style="float: left; margin-left: 5px;">
			<i class="fa fa-angle-down" aria-hidden="true" style="width: 12px;"></i>
			</span>
			<span id="FLTRuparrowSpan" style="float: left;margin-left: 5px; display: none;">
			<<i class="fa fa-angle-up" aria-hidden="true" style="width: 12px;"></i>
			</span></a>
		</div>
		
		<div id="multiFilterDiv" class="filter_div" style="display: none;">

			<div style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Organisation</p>
				<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;" listValue="orgName"
					 onchange="getEmpList();" list="organisationList" key="" />
			</div>
			<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Location</p>
				<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
					listValue="wLocationName" multiple="true" list="wLocationList" key="" />
			</div> 

			<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Department</p>
				<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId"
					cssStyle="float:left;margin-right: 10px;" listValue="deptName" multiple="true"></s:select>
			</div>


			<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Level</p>
				<s:select theme="simple" name="f_level" id="f_level" listKey="levelId"
					cssStyle="float:left;margin-right: 10px;width:100px;" listValue="levelCodeName" multiple="true" list="levelList" key="" />
			</div>

			
			<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">&nbsp;</p>
				<input type="button" value="Submit" class="btn btn-primary" name="filterSubmit" id="filterSubmit" onclick="getEmpList()"  style="margin:0px" />
			</div>

			</div>
	</s:form>
   
   	<div id="empidlist" style="float: left; width: 60%; overflow-y: auto; height: 300px; margin-top: 10px; margin-left: 4px; border: 2px solid #ccc;clear: both;">
		<table id="lt" class="table table-bordered">
			<%
				if (empList != null && !empList.equals("") && !empList.isEmpty()) {
			%>
			<thead>
			<tr>
				<th width="10%">
				<input onclick="checkUncheckValue('<%=boolPublished%>');" type="checkbox" name="allEmp" id="allEmp">
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
					String empName = ((FillEmployee) empList.get(i)).getEmployeeCode();
	
					String emplocationID = (empID == null || empID.equals("")) ? "" : uF.showData(hmEmpLocation.get(empID), "-");
					String location = (emplocationID == null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "-");
					String desig = (empID == null || empID.equals("")) ? "": uF.showData(hmEmpCodeDesig.get(empID), "-");
			%>
			<tr>
				<td>
				<input type="checkbox" name="strTrainerId" id="strTrainerId<%=empID%>" onclick="<%if(alAttendEmp.contains(empID.trim())){%>alert('This learner is already using the program. Therefore, can not be remove?');<%} else {%>getSelectedLearner(this.checked,this.value,'<%=boolPublished%>');<%} %>"
					value="<%=empID%>" <%if (hmCheckEmpList != null && hmCheckEmpList.get(empID) != null) { 
						if (boolPublished == true) { %> disabled="disabled" <% } %>  checked="checked" <%}%>>
				</td>
				<td><a href="javascript: void(0);" onclick="parent.openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a></td>
				<td><%=desig%></td>
				<td><%=location%></td>

			</tr>
			<% } %>
		</tbody>
		<%} else { %>
			<tr>
				<td colspan="3"><div class="nodata msg" style="width: 88%">
						<span>No Employee Found</span>
					</div></td>
			</tr>
			<%
				}
			%>
		</table>
	</div>
	
	<div id="idEmployeeInfo" style="float: left; left: 70%; top: 46px; width: 350px; padding: 5px; overflow-y: auto; border: 2px solid rgb(204, 204, 204); margin-left: 20px; margin-top: 10px; height: 290px;">
		<%
			String learnerIds = (String)request.getAttribute("selectLearnerIDs");
			List<List<String>> selectEmpList = (List<List<String>>) request.getAttribute("selectEmpList");
			
			if (selectEmpList != null) {
		%>
				<div style="border: 2px solid #ccc;">
					<div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Learners</b></div>
						<%
							for (int i = 0; i < selectEmpList.size(); i++) {
								List<String> innerList = selectEmpList.get(i); 
						%>
						<div style="float: left; width: 100%; margin: 5px;"><strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=innerList.get(1) %>&nbsp;
						<%if(boolPublished == false) { %>
							<a href="javascript: void(0)" onclick="<%if(alAttendEmp.contains(innerList.get(0).trim())){%>alert('This learner is already using the program. Therefore, can not be remove?');<%} else {%>getSelectedLearner('false','<%=innerList.get(0)%>','<%=boolPublished%>');<%} %>">
							<img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
						<% } %>
									
						</div>
						<% } %>
				</div>
			<% } else { %>
				<div class="nodata msg" style="width: 85%"><span>No Employee selected</span></div>
			<% }%>
		<input type="hidden" name="oldempids" id="oldempids" value="<%=learnerIds!=null && !learnerIds.equals("") ? learnerIds :"0" %>"/>
		<input type="hidden" name="empselected" id="empselected" value="<%=learnerIds!=null && !learnerIds.equals("") ? learnerIds :"0" %>"/>
	</div>
	
</fieldset>	

<script type="text/javascript">
function getEmpList(){	
	
	 var strOrg = document.getElementById("f_org").value;
	 var location = $("#f_strWLocation").val();
	 var dept = $("#f_department").val();
	 var level = $("#f_level").val();
	 var paramValues = "&strLocation="+location+"&strDepartment="+dept+"&strLevel="+level;
	 //alert("paramValues==>"+paramValues);
	 $("#addLearnerTD").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $.ajax({
		 type :'POST',
		 url :"GetLearningEmpList.action?f_org="+strOrg+paramValues,
		 success:function(result){
			 //alert("result==>"+result);
		 	 $("#addLearnerTD").html(result);
		 }
	 });
}    
	
</script>
		
	