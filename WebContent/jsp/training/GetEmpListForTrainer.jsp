<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">

$(function(){
	
	$("#empLocation").multiselect().multiselectfilter();
	$("#strlearnerLevel").multiselect().multiselectfilter();
	$("#strstrlearnerDesignation").multiselect().multiselectfilter();
	$('#lt').DataTable();
	
});

function checkUncheckValue() {
	var allEmp=document.getElementById("allEmp");
	var strTrainerId = document.getElementsByName('strTrainerId');
	if(allEmp.checked==true){
		 for(var i=0;i<strTrainerId.length;i++){
			  strTrainerId[i].checked = true;
		 }
	}else{		
		 for(var i=0;i<strTrainerId.length;i++){
			  strTrainerId[i].checked = false;
		 }
		 
	}	 
}
 
function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		var value = choice.options[i].value;
		if(choice.options[i].selected == true && value != "") {
			
			if (j == 0) {
				exportchoice = "," + choice.options[i].value + ",";
				j++;
			} else {
				exportchoice += choice.options[i].value + ",";
				j++;
			}
		}else if(choice.options[i].selected == true && value == ""){
			exportchoice = "";
			break;
		}
	}
	return exportchoice;
}
</script>
	
<%
	UtilityFunctions uF = new UtilityFunctions();
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	List<String> existTrainerList = (List<String>) request.getAttribute("existTrainerList");
	Map<String, String> hmExistTrainer = (Map<String, String>) request.getAttribute("hmExistTrainer");
	String planId = (String) request.getParameter("planId");
	String fromPage = (String) request.getAttribute("fromPage");
	Map<String,String> hmEmpLocation = (Map<String,String>)request.getAttribute("hmEmpLocation");
	Map<String, String> hmWLocation = (Map<String,String>)request.getAttribute("hmWLocation"); 
	Map<String, String> hmEmpCodeDesig = (Map<String,String>)request.getAttribute("hmEmpCodeDesig");
	
%>

<s:form theme="simple" action="GetEmpListForTrainer" name="frmGetEmpListForTrainer" id="frmGetEmpListForTrainer" method="POST" cssClass="formcss"
 enctype="multipart/form-data">
   <input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>"/>
	<div style="float: left; width: 100%;margin-bottom: 20px;">
		<div style="float: left; margin-top: 8px; margin-left: 10px;">
			<p style="padding-left: 5px;">Organization</p>
			<s:select theme="simple" name="f_organization" id="f_organization" listKey="orgId" listValue="orgName" 
					list="organisationList" key="" onchange="submitForm(this.value);" cssClass="form-control" />
           </div>
           <div style="float: left; margin-top: 10px; margin-left: 10px;">
			<p style="padding-left: 5px;">Location</p>
			<s:select theme="simple" name="empLocation" listKey="wLocationId" listValue="wLocationName"
			 	list="locationList" id="empLocation" key="" multiple="true"/>
           </div>
           <div style="float: left; margin-top: 10px; margin-left: 10px;">
			<p style="padding-left: 5px;">Level</p>
			<s:select name="strlearnerLevel" list="levelList" listKey="levelId" id="strlearnerLevel"
					listValue="levelCodeName" required="true" multiple="true" key=""/>
		</div>		
		<!-- Start :  Dattatray Date : 17-July-2021 -->
		<div style="float: left; margin-top: 10px; margin-left: 10px;">
			<p style="padding-left: 5px;">Designation</p>
			<s:select name="strstrlearnerDesignation" list="desigList" listKey="desigId" id="strstrlearnerDesignation"
					listValue="desigCodeName" required="true" multiple="true" key=""/>
		</div>
		<!-- End : Dattatray Date : 17-July-2021 -->
		<div style="float: left; margin-top: 8px; margin-left: 10px;">
			<p style="padding-left: 5px;">&nbsp;</p>
			<input type="button" class="btn btn-primary" name="filterSubmit" id = "filterSubmit" style="float:right;" value="Submit" align="center" />
		</div>			
	</div>

</s:form>
	<div style="margin-top: 40px;clear: both;"></div>
	<s:form name="frm_CheckTrainer1" id = "frm_CheckTrainer1" action="TrainerCheck" target="_parent" theme="simple" method="post">
		<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>"/>
		<input type="hidden" name="trainerType" id="trainerType" value="1"/>
		<div>
			<table id="lt" class="table table-bordered">
				<!-- Created Dattatray Date : 17-July-2021 Note : thead block moved outsite-->
				<thead>
					<tr>
						<!-- <th width="10%">Sr No</th> -->
						<th width="5%"><input onclick="checkUncheckValue();" type="checkbox" name="allEmp" id="allEmp"></th>
						<th align="center">Employee</th>
						<th align="center">Designation</th>
						<th align="center">Location</th>
						<!-- <th align="center">Factsheet</th> -->
					</tr>
				</thead>
				<%if (empList != null && !empList.equals("") && !empList.isEmpty()) { %>
				<tbody>
				<%
					for (int i = 0; i < empList.size(); i++) {
			
							String empID = ((FillEmployee) empList.get(i)).getEmployeeId();
							String empName = ((FillEmployee) empList.get(i)).getEmployeeCode();
							String emplocationID=(empID==null || empID.equals(""))?"":hmEmpLocation.get(empID);
							String location=(emplocationID==null || emplocationID.equals(""))?"":hmWLocation.get(emplocationID);
							 
							String desig=(empID==null || empID.equals(""))?"":hmEmpCodeDesig.get(empID);
				%>
				<tr>
					<td width="5%"><input type="checkbox" name="strTrainerId" id="strTrainerId<%=i%>" value="<%=empID %>"
					<%if(hmExistTrainer.get(empID) != null) { %> checked="checked" <% } %> ></td>
					<td><a href="javascript: void(0);" onclick="parent.openPanelEmpProfilePopup('<%=empID %>')"><%=empName %></a></td>
					<td><%=uF.showData(desig, "-")%></td>
					<td><%=uF.showData(location, "-")%></td>
				</tr>
				<% }%>
					</tbody>
				<% } %>
				<!-- Created Dattatray Date : 17-July-2021 Removed block-->
				<%-- <% else { %>
				<tr>
					<td colspan="3"><div class="nodata msg" style="width: 88%">
							<span>No Employee Found</span>
						</div>
					</td>
				</tr>
				<% }%> --%>
			</table>
	</div>
	
		<div style="width: 100%; float: left; text-align: center;">
			<s:submit cssClass="btn btn-primary" value="Submit & Proceed" name="submit" id="submit"/>
		</div>
		
	</s:form>
        
<script>
function submitForm(value) {
	var from = '<%=fromPage%>';
	
	$("#empidlistOuter").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $.ajax({
		 type :'GET',
		 url :'GetEmpListForTrainer.action?f_organization='+value+'&fromPage='+from,
		 success:function(result){
			// alert("empidlistOuter ==> "+result);
			 $("#empidlistOuter").html(result);
		 }
	 });
}

<%if(fromPage != null && fromPage.equals("LD")) { %>
    
	 $('#filterSubmit').click(function(event){
		 event.preventDefault();
		 var from = '<%=fromPage%>';
		 var submit = document.getElementById("submit").value;
		 var form_data = $('#frmGetEmpListForTrainers').serialize();
		/*  Start Dattatray Date : 17-July-2021 */
		 var location = getSelectedValue("empLocation");
		 var strlearnerLevel = getSelectedValue("strlearnerLevel");
		 var strstrlearnerDesignation = getSelectedValue("strstrlearnerDesignation");
		  /* End Dattatray Date : 17-July-2021 */
		 $("#empidlistOuter").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $.ajax({
			 type :'POST',
			 url :'GetEmpListForTrainer.action',
			 data : form_data+'&fromPage='+from+'&location='+location+'&level='+strlearnerLevel+'&desig='+strstrlearnerDesignation,/* Created Dattatray Date : 17-July-2021 Note : Added parameter */
			 success:function(result){
				 $("#empidlistOuter").html(result);
			 }
		 });
	});

	 
	 $('#frm_CheckTrainer1').submit(function(event){
		 event.preventDefault();
		 var submit = document.getElementById("submit").value;
		 var form_data = $('#frm_CheckTrainer1').serialize();
		 
		 $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $.ajax({
			 type :'POST',
			 url :'TrainerCheck.action',
			 data:form_data+"&submit="+submit,
			 success:function(result){
				 //alert("TrainerCheck result1==> "+result);
				 $("#divResult").html(result);
			 },
	/* ===start parvez date: 10-03-2022=== */		
			 error:function(result){
				$.ajax({
		        	url:'TrainerInfo.action',
		        	cache:true,
		        	success:function(data){
		        		$("#divResult").html(data);
		        	}
		        });
			 }
	/* ===end parvez date: 10-03-2022=== */		 
		 });
	 });
 
 <% } %>
 
 </script>