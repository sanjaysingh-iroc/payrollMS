<%@page import="java.util.Iterator"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.lang.reflect.Array"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%> 
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>


<style>
 
#billed {
	float: left;
	font-size: 10px;
	text-align: center;
	padding: 2px 0px;
	height: 22px;
	background-color: #b1def0; /* the critical component */
}

#unbilled {
	float: left;
	font-size: 10px;
	text-align: center;
	padding: 2px 0px;
	height: 22px;
	background-color: #85afde; /* the critical component */
}

#available {
	float: left;
	font-size: 10px;
	text-align: center;
	padding: 2px 0px;
	height: 22px;
	background-color: #68f147; /* the critical component */
}

#outbox {
	height: 22px;
	width: 100%;
	background-color: #68f147; /* the critical component */
}

</style>

<!--[if IE]>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<![endif]-->

<% 
	List<List<String>> alResReqData = (List<List<String>>) request.getAttribute("alResReqData");
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmEmpTotExp = (Map<String, String>) request.getAttribute("hmEmpTotExp");
%>
 
<script>
function viewWorkAllocation(emp_id,proStartDate,proEndDate) {
	//alert("xcv cxvx");
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Task Summary');
	$.ajax({
		url : 'ProjectWorkAllocation.action?emp_id='+emp_id+'&proStartDate='+proStartDate+'&proEndDate='+proEndDate,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editEmpRateAndCost(proID, empID, billingType, type, currId) {

	var tit = 'Edit Employee Cost';
	if(type == 'rate') {
		tit = 'Edit Employee Rate';
	}
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html(''+ tit);
	$.ajax({
		url : 'EditEmpRateAndCost.action?proID='+proID+'&empID='+empID+'&billingType='+billingType+'&type='+type+'&strProCurrId='+currId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addEmpToProject(chboxStatus, empId, proId, type, strActualBillingType, rateDayAmount, rateHourAmount, costAmount, empTaskCnt, rateMonthAmount, proType) {
	//alert("empTaskCnt --->> " + empTaskCnt+ " chboxStatus --->> " +chboxStatus);
	if(parseFloat(empTaskCnt) > 0 && chboxStatus == false) {
		if(type == 'tl') {
			document.getElementById("strTeamLeadId"+empId).checked = true;
		} else {
			document.getElementById("strEmpId"+empId).checked = true;
		}
		alert("Task is assign to this resource, you can not remove this resource from project.");
	} else {
		/* if(chboxStatus == false) {
			if(type == 'tl') {
				document.getElementById("strTeamLeadId"+empId).checked = false;
			} else {
				document.getElementById("strEmpId"+empId).checked = false;
			}
		} */
		
		var empSkillId = document.getElementById("empSkillId_"+empId).value;
		var empTotExp = document.getElementById("empTotExp_"+empId).value;
		//alert("empSkillId --->> " + empSkillId + " -- empTotExp --->> " + empTotExp + " -- chboxStatus --->> " + chboxStatus);
		<%  
		for (int j=0; alResReqData!=null && j<alResReqData.size(); j++) {
			List<String> innerlist = alResReqData.get(j);
			//System.out.println("innerlist ==>> " + innerlist);
		%>
			var skillId = ""+'<%=innerlist.get(4)%>';
			var skillMinExp = ""+'<%=innerlist.get(0)%>';
			var skillMaxExp = ""+'<%=innerlist.get(1)%>';
			var reqResource = ""+'<%=innerlist.get(2)%>';
			var proResReqId = ""+'<%=innerlist.get(5)%>';
			//alert("skillId ===>> " + skillId + " -- proResReqId ===>> " + proResReqId);
			if(parseInt(skillId) == parseInt(empSkillId) && parseFloat(skillMinExp) <= parseFloat(empTotExp) && parseFloat(skillMaxExp) >= parseFloat(empTotExp)) {
				var resGap = document.getElementById("hideReqResourceGap"+proResReqId).value;
				//alert("1 resGap ===>> " + resGap);
				if(chboxStatus == false) {
					resGap++;
				} else {
					resGap--;
				}
				//alert("2 resGap ===>> " + resGap);
				if(document.getElementById("spanReqResourceGap"+proResReqId)) {
					if(resGap<0) {
						document.getElementById("spanReqResourceGap"+proResReqId).innerHTML = "0";
					} else {
						document.getElementById("spanReqResourceGap"+proResReqId).innerHTML = resGap;
					}
				} else {
					if(resGap<0) {
						document.getElementById("reqResourceGap"+proResReqId).value = "0";
					} else {
						document.getElementById("reqResourceGap"+proResReqId).value = resGap;
					}
				}
				document.getElementById("hideReqResourceGap"+proResReqId).value = resGap;
				updateResReqGapInDB(proResReqId, resGap);
				//alert("3 resGap ===>> " + resGap);
			}
			
		<% } %>
		$('#idTeamInfo').html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		getContent('idTeamInfo', 'GetTeamInfoAjax.action?chboxStatus='+ chboxStatus +'&empId='+ empId +'&proId='+ proId +'&type='+ type 
			+'&strActualBillingType='+ strActualBillingType +'&rateDayAmount='+ rateDayAmount +'&rateHourAmount='+ rateHourAmount 
			+'&costAmount='+ costAmount+'&rateMonthAmount='+ rateMonthAmount+'&proType='+proType);
	}
}

function updateResReqGapInDB(proResReqId, resGap) {
	getContent('', 'EditEmpRateAndCost.action?submitType=UpdateSRRGAP&proResReqId='+proResReqId+'&resGap='+resGap);
}

/* function closeForm() {
	var pageType = document.getElementById('pageType').value;
	window.parent.location = "ViewAllProjects.action?pageType="+pageType;
} */

</script>
 

<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
String proType = (String)request.getAttribute("proType");
String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);

Map<String, String> hmProInfoDisplay = (Map<String, String>) request.getAttribute("hmProInfoDisplay");
if(hmProInfoDisplay == null) hmProInfoDisplay = new HashMap<String, String>();

List<String> alEmpId = (List<String>)request.getAttribute("alEmpId");

List<String> alExistEmpId = (List<String>) request.getAttribute("alExistEmpId");

Map<String, String> hmEmpNames = (Map<String, String>)request.getAttribute("hmEmpNames");
Map<String, String> hmEmpLevel = (Map<String, String>)request.getAttribute("hmEmpLevel");
Map<String, String> hmLevel = (Map<String, String>)request.getAttribute("hmLevel");
Map<String, Map<String, String>>  hmWLocation = (Map<String, Map<String, String>> )request.getAttribute("hmWLocation");
Map<String, Map<String, String>> hmEmpWLocation = (Map<String, Map<String, String>>)request.getAttribute("hmEmpWLocation");

Map<String, String> empMp = (Map<String,String>)request.getAttribute("empMp");

Map<String, String> mp1 = (Map<String, String>)request.getAttribute("mp1");
Map<String, String> hmEmpCostAndRate = (Map<String, String>)request.getAttribute("hmEmpCostAndRate");

Map<String, String> hmEmpAllocatePercentAndBilledUnbilled = (Map<String, String>)request.getAttribute("hmEmpAllocatePercentAndBilledUnbilled");

Map<String, String> hmEmpSalaryMap  = (Map<String, String>)request.getAttribute("hmEmpSalaryMap");
Map<String, Map<String, String>> hmLeaves = (Map<String, Map<String, String>>)request.getAttribute("hmLeaves");

Map<String, String> hmEmpSkills = (Map<String, String>)request.getAttribute("hmEmpSkills");
Map<String, String> hmEmpSkillId = (Map<String, String>)request.getAttribute("hmEmpSkillId");
Map<String, String> hmEmployeeExperience = (Map<String, String>)request.getAttribute("hmEmployeeExperience");

Map<String, Map<String, List<String>>> hmEmpProDetails = (Map<String, Map<String, List<String>>>) request.getAttribute("hmEmpProDetails");

Map<String, String> hmEmpAllocationPercent = (Map<String, String>)request.getAttribute("hmEmpAllocationPercent");
if(hmEmpAllocationPercent==null) hmEmpAllocationPercent = new HashMap<String, String>();

Map<String, String> hmEmpSkillsDayRate = (Map<String, String>)request.getAttribute("hmEmpSkillsDayRate");
Map<String, String> hmEmpSkillDayCount = (Map<String, String>)request.getAttribute("hmEmpSkillDayCount");

Map<String, String> hmEmpSkillsHourRate = (Map<String, String>)request.getAttribute("hmEmpSkillsHourRate");
Map<String, String> hmEmpSkillHourCount = (Map<String, String>)request.getAttribute("hmEmpSkillHourCount");

Map<String, String> hmEmpSkillsMonthRate = (Map<String, String>)request.getAttribute("hmEmpSkillsMonthRate");
Map<String, String> hmEmpSkillMonthCount = (Map<String, String>)request.getAttribute("hmEmpSkillMonthCount");

Map<String, String> hmTaskAllocation = (Map<String, String>)request.getAttribute("hmTaskAllocation");

Map<String, String> hmTLMembEmp = (Map<String, String>) request.getAttribute("hmTLMembEmp");
String strActualBillingType = (String) request.getAttribute("strActualBillingType");
String strProCurrId = (String) request.getAttribute("strProCurrId");

Map<String, String> hmEmpTaskCount = (Map<String, String>)request.getAttribute("hmEmpTaskCount");

List<String> alUsertype = new ArrayList<String>();

String strProOwnerOrTL = (String) request.getAttribute("strProOwnerOrTL");

/* if(hmProInfoDisplay != null && hmProInfoDisplay.get("COST_RATE_DISP_USERTYPE") != null) {
	alUsertype = Arrays.asList(hmProInfoDisplay.get("COST_RATE_DISP_USERTYPE").split(","));
} */
%>

 
<script type="text/javascript" charset="utf-8">
	$(document).ready( function () {
		$('#lt').DataTable({
			"order": [],
			"columnDefs": [ {
			      "targets"  : 'no-sort',
			      "orderable": false
			    }],
			'dom': 'lBfrtip',
	        'buttons': [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ]
	  	});
	});
</script>



<script type="text/javascript">

onload=function() {
   
	document.getElementById('the_div').className="nothere";
   	document.getElementById("ajaxLoadImage").style.visibility="hidden";
   	document.getElementById('ui-datepicker-div')
   	document.getElementById('ui-datepicker-div').style.display = 'none';
	
};


function checkSelect(form) {
	
	 var hideExistEmps = document.getElementById('hideExistEmps').value;
	 if(parseFloat(hideExistEmps) > 0) {
		 return true;
		 //document.getElementById('formID_2').submit();
	 } else {
		 alert('Please select one or more Resource');
		 return false;
	 }
}


	function updateAllocationDate(empId, proId) {
		if(confirm('Are you sure, you want to update allocation date?')) { 
			var allocationDate = document.getElementById("allocationDate_"+empId).value;
			getContent('spanAllocationDate_'+empId, 'EditEmpRateAndCost.action?submitType=DATEALLOCATION&proID='+proId+'&empID='+empId+'&allocationDate='+allocationDate);
			document.getElementById("divAllocationDateEdit_"+empId).style.display = "none";
			document.getElementById('divAllocationDateReadonly_'+empId).style.display = "block";
		}
	}
	
	function openForUpdateAllocationDate(empId) {
		if(confirm('Are you sure, you want to edit allocation date?')) {
			var allocationDate = document.getElementById("spanAllocationDate_"+empId).innerHTML;
			document.getElementById("allocationDate_"+empId).value = allocationDate;
			document.getElementById("divAllocationDateEdit_"+empId).style.display = "block";
			document.getElementById('divAllocationDateReadonly_'+empId).style.display = "none";
		}
	}
	
	function updateReleaseDate(empId, proId) {
		if(confirm('Are you sure, you want to update release date?')) {
			var releaseDate = document.getElementById("releaseDate_"+empId).value;
			getContent('spanReleaseDate_'+empId, 'EditEmpRateAndCost.action?submitType=DATEALLOCATION&proID='+proId+'&empID='+empId+'&releaseDate='+releaseDate);
			document.getElementById("divReleaseDateEdit_"+empId).style.display = "none";
			document.getElementById('divReleaseDateReadonly_'+empId).style.display = "block";
		}
	}

	function openForUpdateReleaseDate(empId) {
		if(confirm('Are you sure, you want to edit release date?')) {
			var releaseDate = document.getElementById("spanReleaseDate_"+empId).innerHTML;
			document.getElementById("releaseDate_"+empId).value = releaseDate;
			document.getElementById("divReleaseDateEdit_"+empId).style.display = "block";
			document.getElementById('divReleaseDateReadonly_'+empId).style.display = "none";
		}
	}
	
	function updateAllocationPercent(empId, proId) {
		if(confirm('Are you sure, you want to update allocation percentage?')) { 
			var allocationPercent = document.getElementById("allocation_"+empId).value;
			getContent('spanAllocation_'+empId, 'EditEmpRateAndCost.action?submitType=ALLOCATION&proID='+proId+'&empID='+empId+'&allocationPercent='+allocationPercent);
			document.getElementById("divAllocationEdit_"+empId).style.display = "none";
			document.getElementById('divAllocationReadonly_'+empId).style.display = "block";
		}
	}	

	
	function openForUpdateAllocationPercent(empId) {
		if(confirm('Are you sure, you want to edit allocation percentage?')) {
			var allocationPercent = document.getElementById("spanAllocation_"+empId).innerHTML;
			document.getElementById("allocation_"+empId).value = allocationPercent;
			document.getElementById("divAllocationEdit_"+empId).style.display = "block";
			document.getElementById('divAllocationReadonly_'+empId).style.display = "none";
		}
	}

	function checkAllocationPercent(empId) {
		var allocatedPercent = document.getElementById("allocatedPercent_"+empId).value;
		var allocationPercent = document.getElementById("spanAllocation_"+empId).innerHTML;
		//alert("allocatedPercent ===>> " + allocatedPercent);
		var remainPercent = 100 - (parseFloat(allocatedPercent)-parseFloat(allocationPercent));
		//alert("remainPercent ===>> " + remainPercent);
		var allocationPercent = document.getElementById("allocation_"+empId).value;
		//alert("allocationPercent ===>> " + allocationPercent);
		if(parseFloat(remainPercent) < parseFloat(allocationPercent)) {
			alert("Entered value greater than available Percentage. ("+remainPercent+") ");
			document.getElementById("allocation_"+empId).value='';
		}
	}
	
	
	function updateBilledUnbilledStatus(empId, proId) {
		if(confirm('Are you sure, you want to update the billed/ unbilled status?')) {
			var billedUnbilled = document.getElementById("billedUnbilled_"+empId).value;
			getContent('', 'EditEmpRateAndCost.action?submitType=BILLED&proID='+proId+'&empID='+empId+'&isBilledUnbilled='+billedUnbilled);
		}
	}

/* function submitForm(val) {
	var pro_id = document.getElementById('pro_id').value;
	var operation = document.getElementById('operation').value;
	var pageType = document.getElementById('pageType').value;
	//var actBillingType = document.getElementById('actBillingType').value;
	if(val == 'SP') {
		window.parent.location = "PreAddNewProject1.action?step=2&operation="+operation+'&pro_id='+pro_id+'&pageType='+pageType;
	} else {
		window.parent.location = 'ViewAllProjects.action?pageType='+pageType;
	}
} */

</script>


<%-- <% 	String[] strSkill = (String[])request.getAttribute("skill");
	Map<String, String> hmSkillName = (Map<String, String>)request.getAttribute("hmSkillName");
	Map<String, String> hmProjectJobProfile = (Map<String, String>) request.getAttribute("hmProjectJobProfile");
%>
<% if(strSkill!=null && proType!=null && proType.equals("P")) { %>
	<div style="float:left; width: 100%;">
	<div style="float:left; width: 100%;"><b><i>Enter skillwise number of resources required:</i></b> </div>
		<% 
			int cnt=0;
			for(int i=0; strSkill!=null && i<strSkill.length; i++) { %>
			<div style="float: left; margin: 5px 10px;">
				<span style="float: left; margin-right: 10px;"><%=hmSkillName.get(strSkill[i]) %>: </span>
				<span style="float: left;"><input type="text" name="reqResourceForSkill_<%=strSkill[i] %>" id="reqResourceForSkill_<%=strSkill[i] %>" style="width: 100px !important;" /></span>
			</div>
		<% } %>
		
	</div>
<% } %> --%>
	
<div id="idTeamInfo" class="col-lg-12 col-md-12 col-sm-12" style="border: 1px solid #ccc; top: 16px; padding:5px; margin-bottom: 15px; min-height: 125px; max-height: 300px; overflow-y: auto;">
<div  style="font-size: 14px;"><b>Project Team Summary</b></div>
<table class="table-bordered table"> <!-- overflowtable -->
  <thead>
	<tr>
		<% if(proType == null || proType.equals("") || proType.equals("null") || !proType.equalsIgnoreCase("P")) { %>
			<th>TL</th>
		<% } %>
		<th>Team</th>
		<th>Employee Name</th>
		<th>Skill </th>
		<th>Exp </th>
		<!-- <th>Project Name </th>
		<th>Earliest Release Date </th> -->
		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L") || proType.equalsIgnoreCase("P")) { %>
			<th nowrap="nowrap">Allocation Date </th>
			<th nowrap="nowrap">Release Date </th>
			<th>Allocation % </th>
			<th>Billed </th>
		<% } %>
		<th>Level </th>
		<th>Work Location </th>
		
		<!-- Rate/Day & Cost/Day hide for KPCA  -->
		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE")) && !strUserType.equals(IConstants.MANAGER) && uF.parseToInt(strProOwnerOrTL) != 2) { %>
			<th align="center">Rate/
			<% if(strActualBillingType != null && strActualBillingType.equals("H")) { %>
			Hour
			<% } else if(strActualBillingType != null && strActualBillingType.equals("M")) { %>
			Month
			<% } else { %>
			Day
			<% } %>
			<br/>(<%=uF.showData((String)request.getAttribute("strShortCurrency"), "-") %>)</th>
		<% } %>
		
		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST")) && !strUserType.equals(IConstants.MANAGER) && uF.parseToInt(strProOwnerOrTL) != 2) { %>
			<th align="center">Cost/
			<% if(strActualBillingType != null && strActualBillingType.equals("H")) { %>
			Hour
			<% } else if(strActualBillingType != null && strActualBillingType.equals("M")) { %>
			Month
			<% } else { %>
			Day
			<% } %>
			<br/>(<%=uF.showData((String)request.getAttribute("strShortCurrency"), "-") %>)</th>
		<% } %>
		
		<th>Availability</th> 
		<th>Work Allocation
		<input type="hidden" name="hideExistEmps" id="hideExistEmps" value="<%=alExistEmpId != null ? alExistEmpId.size() : "0" %>">
		</th>
	</tr>
  </thead>
	
	<%
	String strEmpIdNew1 = null;
	
	for(int i=0; alExistEmpId != null && i<alExistEmpId.size(); i++) {
		
		strEmpIdNew1 = (String)alExistEmpId.get(i);
		/* if(strEmpIdOld1!=null && !strEmpIdOld1.equalsIgnoreCase(strEmpIdNew1)){
			count1 = 0;
		} */
		
	Map<String, String>  hmLocation = hmWLocation.get(hmEmpWLocation.get(alExistEmpId.get(i)));
	if(hmLocation == null)hmLocation = new HashMap<String, String>();
	
	Map<String, String> hmInnerLeave = hmLeaves.get((String)alExistEmpId.get(i));
	if(hmInnerLeave == null)hmInnerLeave = new HashMap<String, String>();	
	
	Map<String, List<String>> hmProData = hmEmpProDetails.get((String)alExistEmpId.get(i));
	if(hmProData == null)hmProData = new HashMap<String, List<String>>();
	
	%> 
	<tr>
		<% if(proType == null || proType.equals("") || proType.equals("null") || !proType.equalsIgnoreCase("P")) { %>
			<td>
				<input type="hidden" name="strAllEmpIdS" value="<%=alExistEmpId.get(i)%>">
				<input type="checkbox" name="strTeamLeadIdS" disabled="disabled" <%if(hmTLMembEmp != null && hmTLMembEmp.get(alExistEmpId.get(i)+"_T") != null && hmTLMembEmp.get(alExistEmpId.get(i)+"_T").equals("TL")) { %> checked="checked" <% } %> >
			</td>
		<% } %>
		<td>
			<input type="checkbox" name="strEmpIdS" disabled="disabled" <%if(hmTLMembEmp != null && hmTLMembEmp.get(alExistEmpId.get(i)+"_M") != null &&  hmTLMembEmp.get(alExistEmpId.get(i)+"_M").equals("MEMB")) { %> checked="checked" <% } %> >
		</td>
		<td nowrap="nowrap"><%=hmEmpNames.get(alExistEmpId.get(i))%></td>
		<td nowrap="nowrap"><%=uF.showData(hmEmpSkills.get(strEmpIdNew1), "")%></td>
		<td nowrap="nowrap"><%=uF.showData(hmEmployeeExperience.get(strEmpIdNew1), "")%></td>
		<!-- <td> -->
		<% 	Iterator<String> it = hmProData.keySet().iterator();
			String earliestReleaseDate = null;
			double dblBilledPercent = 0;
			double dblUnbilledPercent = 0;
			double dblAvailablePercent = 0;
			while(it.hasNext()) {
				String proId = it.next();
				List<String> innerList = hmProData.get(proId);
				if(earliestReleaseDate ==null) {
					earliestReleaseDate = innerList.get(1);
				}
				if(uF.parseToBoolean(innerList.get(2))) {
					dblBilledPercent += uF.parseToDouble(innerList.get(3));
				} else {
					dblUnbilledPercent += uF.parseToDouble(innerList.get(3));
				}
				
		%>
			<%-- <%=uF.showData(innerList.get(0), "")%><br/> --%>
		<% } %>
		<!-- </td> -->
		<%-- <td nowrap="nowrap"><%=uF.showData(earliestReleaseDate, "") %></td> --%>
		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L") || proType.equalsIgnoreCase("P")) { %>
			<td nowrap="nowrap">
				<%-- <input type="hidden" name="hideAllocationDate_<%=alExistEmpId.get(i) %>" id="hideAllocationDate_<%=alExistEmpId.get(i) %>" value="<%=uF.showData(hmEmpAllocationPercent.get(alExistEmpId.get(i)), "0") %>"> --%>
				<div id="divAllocationDateEdit_<%=alExistEmpId.get(i) %>" style="display: none;">
					<input type="text" name="allocationDate_<%=alExistEmpId.get(i) %>" id="allocationDate_<%=alExistEmpId.get(i) %>" style="width: 90px !important;" readonly="readonly"/> <a href="javascript:void(0);" onclick="updateAllocationDate('<%=alExistEmpId.get(i) %>','<%=(String)request.getAttribute("pro_id") %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div>
				<div id="divAllocationDateReadonly_<%=alExistEmpId.get(i) %>">
					<span id="spanAllocationDate_<%=alExistEmpId.get(i) %>"><%=hmEmpAllocatePercentAndBilledUnbilled.get(alExistEmpId.get(i)+"_ALLOCATION_DATE") %></span> <a href="javascript:void(0);" style="float: right;" onclick="openForUpdateAllocationDate('<%=alExistEmpId.get(i) %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div> 
			</td>
			
			<td nowrap="nowrap">
				<%-- <input type="hidden" name="hideReleaseDate_<%=alExistEmpId.get(i) %>" id="hideReleaseDate_<%=alExistEmpId.get(i) %>" value="<%=uF.showData(hmEmpAllocationPercent.get(alExistEmpId.get(i)), "0") %>"> --%>
				<div id="divReleaseDateEdit_<%=alExistEmpId.get(i) %>" style="display: none;">
					<input type="text" name="releaseDate_<%=alExistEmpId.get(i) %>" id="releaseDate_<%=alExistEmpId.get(i) %>" style="width: 90px !important;" readonly="readonly"/> <a href="javascript:void(0);" onclick="updateReleaseDate('<%=alExistEmpId.get(i) %>','<%=(String)request.getAttribute("pro_id") %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div>
				<div id="divReleaseDateReadonly_<%=alExistEmpId.get(i) %>">
					<span id="spanReleaseDate_<%=alExistEmpId.get(i) %>"><%=hmEmpAllocatePercentAndBilledUnbilled.get(alExistEmpId.get(i)+"_RELEASE_DATE") %></span> <a href="javascript:void(0);" style="float: right;" onclick="openForUpdateReleaseDate('<%=alExistEmpId.get(i) %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div> 
			</td>
			<script type="text/javascript">
				$("#allocationDate_<%=alExistEmpId.get(i) %>").datepicker({format : 'dd/mm/yyyy'});
				$("#releaseDate_<%=alExistEmpId.get(i) %>").datepicker({format : 'dd/mm/yyyy'});
			</script>
			<td nowrap="nowrap">
				<input type="hidden" name="allocatedPercent_<%=alExistEmpId.get(i) %>" id="allocatedPercent_<%=alExistEmpId.get(i) %>" value="<%=uF.showData(hmEmpAllocationPercent.get(alExistEmpId.get(i)), "0") %>">
				<div id="divAllocationEdit_<%=alExistEmpId.get(i) %>" style="display: none;">
					<input type="text" name="allocation_<%=alExistEmpId.get(i) %>" id="allocation_<%=alExistEmpId.get(i) %>" style="width: 40px !important;" onkeypress="return isNumberKey(event)" onkeyup="checkAllocationPercent('<%=alExistEmpId.get(i) %>')"/> <a href="javascript:void(0);" onclick="updateAllocationPercent('<%=alExistEmpId.get(i) %>','<%=(String)request.getAttribute("pro_id") %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div>
				<div id="divAllocationReadonly_<%=alExistEmpId.get(i) %>">
					<span id="spanAllocation_<%=alExistEmpId.get(i) %>"><%=hmEmpAllocatePercentAndBilledUnbilled.get(alExistEmpId.get(i)+"_ALLOCATION_PERCENT") %></span>% <a href="javascript:void(0);" style="float: right;" onclick="openForUpdateAllocationPercent('<%=alExistEmpId.get(i) %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div> 
			</td>
			<td nowrap="nowrap"><input type="checkbox" name="billedUnbilled_<%=alExistEmpId.get(i) %>" id="billedUnbilled_<%=alExistEmpId.get(i) %>" <%=uF.parseToBoolean(hmEmpAllocatePercentAndBilledUnbilled.get(alExistEmpId.get(i)+"_BILLED_UNBILLED")) ? "checked" : "" %> onclick="updateBilledUnbilledStatus('<%=alExistEmpId.get(i) %>','<%=(String)request.getAttribute("pro_id") %>');"/></td>
		<% } %>
		<td nowrap="nowrap"><%=hmLevel.get(hmEmpLevel.get(alExistEmpId.get(i)))%> </td>
		<td nowrap="nowrap"><%=uF.showData(hmLocation.get("WL_NAME"), "")%></td>
		
		<!-- Rate/Day & Cost/Day hide for KPCA  -->
		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE")) && !strUserType.equals(IConstants.MANAGER) && uF.parseToInt(strProOwnerOrTL) != 2) { %>
			<%
			if(hmEmpCostAndRate.get(alExistEmpId.get(i)+"_RATE") != null) {
				//System.out.println(" totEmpRate ===>>>> "+alExistEmpId.get(i)+"   --  " + uF.parseToDouble(hmEmpCostAndRate.get(alExistEmpId.get(i)+"_RATE")));
				double totEmpRate = uF.parseToDouble(hmEmpCostAndRate.get(alExistEmpId.get(i)+"_RATE"));
			%>
			<td class="alignRight padRight20">
				<input type="hidden" name="rate" value="<%=totEmpRate %>">
				<span id="<%=alExistEmpId.get(i) %>RateSpan">
					<%=uF.formatIntoTwoDecimalWithOutComma(totEmpRate) %>
				</span>
				<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
					<a title="Edit Rate" class="fa fa-edit" onclick="editEmpRateAndCost('<%=(String)request.getAttribute("pro_id") %>','<%=alExistEmpId.get(i) %>', '<%=strActualBillingType %>', 'rate', '<%=strProCurrId %>');" href="javascript:void(0);">&nbsp;</a>
				<% } %>
			</td>
			<% } else { 
				String rateDayAmount = "0";
				String rateHourAmount = "0";
				String rateMonthAmount = "0";
				
				String rateAmount = "0";
				double dblRateDayAmount = 0;
				if(uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpIdNew1)) > 0) {
					dblRateDayAmount = uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpIdNew1)) / uF.parseToDouble(hmEmpSkillDayCount.get(strEmpIdNew1));
				}
				rateDayAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateDayAmount);
				
				double dblRateHourAmount = 0;
				if(uF.parseToDouble(hmEmpSkillsHourRate.get(strEmpIdNew1)) > 0) {
					dblRateHourAmount = uF.parseToDouble(hmEmpSkillsHourRate.get(strEmpIdNew1)) / uF.parseToDouble(hmEmpSkillHourCount.get(strEmpIdNew1));
				}
				rateHourAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateHourAmount);
				
				double dblRateMonthAmount = 0;
				if(uF.parseToDouble(hmEmpSkillsMonthRate.get(strEmpIdNew1)) > 0) {
					dblRateMonthAmount = uF.parseToDouble(hmEmpSkillsMonthRate.get(strEmpIdNew1)) / uF.parseToDouble(hmEmpSkillMonthCount.get(strEmpIdNew1));
				}
				rateMonthAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateMonthAmount);
				
				//System.out.println("strEmpIdNew === >>> " + strEmpIdNew + "  hmEmpSkillsRates_strEmpIdNew ===>>> " + hmEmpSkillsRates.get(strEmpIdNew+"_"+count));
			
				if(strActualBillingType != null && strActualBillingType.equals("H")) {
					rateAmount = rateHourAmount;
				} else if(strActualBillingType != null && strActualBillingType.equals("M")) {
					rateAmount = rateMonthAmount;
				} else {
					rateAmount = rateDayAmount;
				}
			//System.out.println("strEmpIdNew1 === >>> " + strEmpIdNew1 + "  hmEmpSkillsRates_strEmpIdNew1 ===>>> " + hmEmpSkillsRates.get(strEmpIdNew1+"_"+count1));
			%>
			<td class="alignRight padRight20">
				<input type="hidden" name="rate" value="<%=uF.showData(rateAmount, "0")%>">
				<span id="<%=alExistEmpId.get(i) %>RateSpan">
					<%=uF.showData(rateAmount, "0")%>
				</span>
				<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
					<a title="Edit Rate" class="fa fa-edit" onclick="editEmpRateAndCost('<%=(String)request.getAttribute("pro_id") %>','<%=alExistEmpId.get(i) %>', '<%=strActualBillingType %>', 'rate', '<%=strProCurrId %>');" href="javascript:void(0);">&nbsp;</a>
				<% } %>
			</td>
			<% } %>
		<% } %>
		
		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST")) && !strUserType.equals(IConstants.MANAGER) && uF.parseToInt(strProOwnerOrTL) != 2) { %>
			<%if(hmEmpCostAndRate.get(alExistEmpId.get(i))+"_COST" != null) { 
				double totEmpCost = uF.parseToDouble(hmEmpCostAndRate.get(alExistEmpId.get(i)+"_COST"));
			%>
			<td class="alignRight padRight20">
			<input type="hidden" value="<%=uF.formatIntoTwoDecimalWithOutComma(totEmpCost) %>" name="actualRate">
			<span id="<%=alExistEmpId.get(i) %>CostSpan"> <%=uF.formatIntoTwoDecimalWithOutComma(totEmpCost) %> </span>
			<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
				<a title="Edit Cost" class="fa fa-edit" onclick="editEmpRateAndCost('<%=(String)request.getAttribute("pro_id") %>','<%=alExistEmpId.get(i) %>', '<%=strActualBillingType %>', 'cost', '<%=strProCurrId %>');" href="javascript:void(0);">&nbsp;</a>
			<% } %>
			</td>
			<% } else { %>
			<td class="alignRight padRight20"><input type="hidden" name="actualRate">
			<span id="<%=alExistEmpId.get(i)%>CostSpan"> N/A </span>
			<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
				<a title="Edit Cost" class="fa fa-edit" onclick="editEmpRateAndCost('<%=(String)request.getAttribute("pro_id") %>','<%=alExistEmpId.get(i) %>', '<%=strActualBillingType %>', 'cost', '<%=strProCurrId %>');" href="javascript:void(0);">&nbsp;</a>
			<% } %>
			</td>
			<% } %>
		<% } %>

		<td nowrap="nowrap">
			<%if(hmInnerLeave.size()==0) { %>
				<div style="background-color: #00CC00">No Leave</div>
			<%} else if(hmInnerLeave.size() < 4) { %>
				<div style="background-color: #99FF00"><%=hmInnerLeave.size() %> leaves</div>
			<%} else if(hmInnerLeave.size() < 7) { %>
				<div style="background-color: #FFFF33"><%=hmInnerLeave.size() %> leaves</div>
			<%} else if(hmInnerLeave.size() < 15) { %>
				<div style="background-color: #FF9900"><%=hmInnerLeave.size() %> leaves</div>
			<%} else if(hmInnerLeave.size() >= 15) { %>
				<div style="background-color: #FF3300"><%=hmInnerLeave.size() %> leaves</div>
			<% } %>
		</td>
		
		<td align="center"><%=uF.showData(hmTaskAllocation.get(alExistEmpId.get(i)), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\">&nbsp;</div>") %></td>

	</tr>
	<% 
	} if(alExistEmpId!=null && alExistEmpId.size()==0) { %>
	<tr><td colspan="10"><div class="nodata msg" style="width:92%"><span>No resource added in this project.</span></div></td></tr>
	<% } %>
</table>		
</div>


<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L") || proType.equalsIgnoreCase("P")) { %>
<div id="submitBtnTable_2" class="col-lg-12 col-md-12 col-sm-12">
	<s:form id="formID_2" cssClass="formcss" action="PreAddNewProject1" name="formID_2" method="post" theme="simple"> <!--  onsubmit="return checkSelect(this)" enctype="multipart/form-data" -->
		<s:hidden name="step" id="step" value="2"></s:hidden>
		<s:hidden name="pro_id" id="pro_id"></s:hidden>
		<s:hidden name="actBillingType" id="actBillingType"></s:hidden>
		<s:hidden name="operation" id="operation"></s:hidden>
		<s:hidden name="pageType" id="pageType"></s:hidden>
		<s:hidden name="proType" id="proType"></s:hidden>
			<div style="margin: 20px 0px 0px 0px">
			<%-- <% if(strSkill!=null && proType!=null && proType.equals("P")) { %>
				<div style="float:left; width: 100%">
				<div style="float:left; width: 100%;"><b><i>Skillwise number of resources shortfall:</i></b> </div>
					<% 	int cnt=0;
						for(int i=0; strSkill!=null && i<strSkill.length; i++) { %>
						<div style="float: left; margin: 5px 10px;">
							<span style="float: left; margin-right: 10px;"><%=hmSkillName.get(strSkill[i]) %>: </span>
							<span style="float: left;"><input type="text" name="reqResourceForSkill_<%=strSkill[i] %>" id="reqResourceForSkill_<%=strSkill[i] %>" style="width: 100px !important;" /></span>
						</div>
					<% } %>
				</div>
			<% } %> --%>
				<table class="table-no-bordered table">
					<tr>
						<td>
						<%
							String operation = (String)request.getAttribute("operation");
						 	String pageType = (String)request.getAttribute("pageType");
							if(operation != null && operation.equals("E") && (proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L"))) {
						%>
								<input type="button" name="skipProcced" value="Skip & Proceed" class="btn btn-primary" style="float:right; margin-right: 5px;" onclick="skipAndProcced('<%=request.getAttribute("pro_id") %>', '2', '<%=pageType %>');"/>
							<% } %>
							<%-- <% if(proType != null && proType.equalsIgnoreCase("P")) { %> --%>
							<% if((proType != null && proType.equalsIgnoreCase("P")) || ((proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) && strUserType != null && !strUserType.equals(IConstants.MANAGER))) { %>
								<% if(strUserType!=null && strUserType.equals(IConstants.RECRUITER)) { %>
									<input type="button" name="stepSave" value="Save & Request for Resource" class="btn btn-primary" style="float:right; margin-right: 5px;" onclick="saveAndExit('formID_2');"/>
								<% } else { %>
									<input type="button" name="stepSave" value="Save & Exit" class="btn btn-primary" style="float:right; margin-right: 5px;" onclick="saveAndExit('formID_2');"/>
								<% } %>
							<% } else { %>
								<input type="button" name="submit" value="Submit & Proceed" class="btn btn-primary" style="float:right; margin-right: 5px;" onclick="submitAndProcced('formID_2', '2');"/>
								<input type="button" name="stepSave" value="Save & Exit" class="btn btn-primary" style="float:right; margin-right: 5px;" onclick="saveAndExit('formID_2');"/>
							<% } %>
							<input type="button" value="Cancel" class="btn btn-danger" style="float:right; margin-right: 5px;" name="cancel" onclick="closeForm('<%=pageType %>');">
						</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					</tr>
				</table>
			</div>	
	</s:form>
</div>
<% } %>
		
<div class="col-lg-12 col-md-12 col-sm-12">			
		<div  style="font-size: 14px;"><b>Available Resource for Project</b></div>
		<table id="lt" border="0" class="table-bordered table"> <!-- overflowtable  -->
		  <thead>
			<tr>
				<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
					<th>TL</th>
				<% } %>
				<th>Team</th>
				<th>Employee Name</th>
				<th>Skill </th>
				<th>Exp </th>
				<th>Project Name </th>
				<th>Earliest Release Date </th>
				<th style="width: 200px !important;">Capacity
					<div id="outbox">
						<div id="billed" style="width: 33.33%;" title="Billed">B</div>
						<div id="unbilled" style="width: 33.33%;" title="Unbilled">U</div>
						<div id="available" style="width: 33.33%;" title="Available">A</div>
					</div>
				 </th>
				<th>Level </th>
				<th>Work Location </th>
				<!-- Rate/Day & Cost/Day hide for KPCA  -->
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE")) && !strUserType.equals(IConstants.MANAGER) && uF.parseToInt(strProOwnerOrTL) != 2) { %>
					<th align="center">Rate/
					<% if(strActualBillingType != null && strActualBillingType.equals("H")) { %>
					Hour
					<% } else if(strActualBillingType != null && strActualBillingType.equals("M")) { %>
					Month
					<% } else { %>
					Day
					<% } %>
					<br/>(<%=uF.showData((String)request.getAttribute("strShortCurrency"), "-") %>)
					</th>
				<% } %>
				
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST")) && !strUserType.equals(IConstants.MANAGER) && uF.parseToInt(strProOwnerOrTL) != 2) { %>
					<th align="center">Cost/
					<% if(strActualBillingType != null && strActualBillingType.equals("H")) { %>
					Hour
					<% } else if(strActualBillingType != null && strActualBillingType.equals("M")) { %>
					Month
					<% } else { %>
					Day
					<% } %>
					<br/>(<%=uF.showData((String)request.getAttribute("strShortCurrency"), "-") %>)
					</th>
				<% } %>
				
				<th>Availability</th> 
				<th>Work Allocation</th>
			</tr>
		  </thead>
			
			<tbody>
			<%
			String strEmpIdNew = null;
			//System.out.println("hmEmpSkillsMonthRate ===>>  " + hmEmpSkillsMonthRate);
			for(int i=0; alEmpId!=null && i<alEmpId.size(); i++) { 
				strEmpIdNew = alEmpId.get(i);
				String empTaskCnt = hmEmpTaskCount.get(alEmpId.get(i));
				
			Map<String, String> hmLocation = hmWLocation.get(hmEmpWLocation.get(alEmpId.get(i)));
			if(hmLocation==null)hmLocation=new HashMap<String, String>();
			
			Map<String, String> hmInnerLeave = (Map<String, String>)hmLeaves.get(alEmpId.get(i));
			if(hmInnerLeave==null)hmInnerLeave=new HashMap<String, String>();
			
			Map<String, List<String>> hmProData = hmEmpProDetails.get((String)alEmpId.get(i));
			if(hmProData == null)hmProData = new HashMap<String, List<String>>();
			/* if(uF.parseToInt(alEmpId.get(i)) == 30) {
				System.out.println("hmProData ===>> " + hmProData);
			} */
			String rateDayAmount = "0";
			String rateHourAmount = "0";
			String rateMonthAmount = "0";
			
			String rateAmount = "0";
			String costAmount = "0";
			if(mp1.get(alEmpId.get(i)) != null) {
				
				rateDayAmount = uF.showData(mp1.get(alEmpId.get(i)+"_D"), "0");
				rateHourAmount = uF.showData(mp1.get(alEmpId.get(i)+"_H"), "0");
				rateMonthAmount = uF.showData(mp1.get(alEmpId.get(i)+"_M"), "0");
				
			} else {
				double dblRateDayAmount = 0;
				if(uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpIdNew)) > 0) {
					dblRateDayAmount = uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpIdNew)) / uF.parseToDouble(hmEmpSkillDayCount.get(strEmpIdNew));
				}
				rateDayAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateDayAmount);
				double dblRateHourAmount = 0;
				if(uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpIdNew)) > 0) {
					dblRateHourAmount = uF.parseToDouble(hmEmpSkillsHourRate.get(strEmpIdNew)) / uF.parseToDouble(hmEmpSkillHourCount.get(strEmpIdNew));
				}
				rateHourAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateHourAmount);
				
				double dblRateMonthAmount = 0;
				if(uF.parseToDouble(hmEmpSkillsMonthRate.get(strEmpIdNew)) > 0) {
					dblRateMonthAmount = uF.parseToDouble(hmEmpSkillsMonthRate.get(strEmpIdNew)) / uF.parseToDouble(hmEmpSkillMonthCount.get(strEmpIdNew));
				}
				rateMonthAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateMonthAmount);
				//System.out.println("strEmpIdNew === >>> " + strEmpIdNew + "  hmEmpSkillsRates_strEmpIdNew ===>>> " + hmEmpSkillsRates.get(strEmpIdNew+"_"+count));
			}
			
			if(strActualBillingType != null && strActualBillingType.equals("H")) {
				rateAmount = rateHourAmount;
			} else if(strActualBillingType != null && strActualBillingType.equals("M")) {
				rateAmount = rateMonthAmount;
			}  else {
				rateAmount = rateDayAmount;
			}
			//System.out.println("strEmpIdNew === >>> " + strEmpIdNew +"  rateAmount === >>> " + rateAmount);
			if(hmEmpSalaryMap.get(alEmpId.get(i)) != null) {
				costAmount = hmEmpSalaryMap.get(alEmpId.get(i));
				/* if(uF.parseToInt(alEmpId.get(i)) == 450) {
					System.out.println("costAmount ===>> " + costAmount);
				} */
			} else {
				costAmount = "0";
			}
			%>  
			<tr>
			
				<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
					<td>
						<input type="hidden" name="strAllEmpId" value="<%=(String)alEmpId.get(i)%>">
						<input onclick="addEmpToProject(this.checked, this.value, '<%=request.getAttribute("pro_id") %>', 'tl', '<%=strActualBillingType %>','<%=rateDayAmount %>','<%=rateHourAmount %>','<%=costAmount %>', '<%=empTaskCnt %>', '<%=rateMonthAmount %>', '<%=(String)request.getAttribute("proType") %>');" type="checkbox" name="strTeamLeadId" id="strTeamLeadId<%=(String)alEmpId.get(i)%>" value="<%=(String)alEmpId.get(i)%>"
						<% if(proType != null && (proType.equalsIgnoreCase("C") || proType.equalsIgnoreCase("B"))) { %> disabled="disabled" <% } %>
						>
					</td>
				<% } %>
					<td>
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L") || (proType.equalsIgnoreCase("P") && strUserType.equals(IConstants.RECRUITER))) { %>
						<input onclick="addEmpToProject(this.checked, this.value, '<%=request.getAttribute("pro_id") %>', 'emp', '<%=strActualBillingType %>','<%=rateDayAmount %>','<%=rateHourAmount %>','<%=costAmount %>', '<%=empTaskCnt %>', '<%=rateMonthAmount %>', '<%=(String)request.getAttribute("proType") %>');" type="checkbox" name="strEmpId" id="strEmpId<%=(String)alEmpId.get(i)%>" value="<%=(String)alEmpId.get(i)%>"
							<% if(proType != null && (proType.equalsIgnoreCase("C") || proType.equalsIgnoreCase("B"))) { %> disabled="disabled" <% } %>
							>
					<% } %>
					</td>
				
				<td><span class="users-list-name"><%=(String)hmEmpNames.get((String)alEmpId.get(i)) %></span></td>
				<%//System.out.println("Data Employee Name"); %>
				<td nowrap="nowrap">
					<input type="hidden" name="empSkillId_<%=strEmpIdNew %>" id="empSkillId_<%=strEmpIdNew %>" value="<%=(hmEmpSkillId!=null && hmEmpSkillId.get(strEmpIdNew)!=null) ? hmEmpSkillId.get(strEmpIdNew) : "" %>" >
					<%=uF.showData((String)hmEmpSkills.get(strEmpIdNew), "") %>
				</td>
				<td nowrap="nowrap">
				<input type="hidden" name="empTotExp_<%=strEmpIdNew %>" id="empTotExp_<%=strEmpIdNew %>" value="<%=(hmEmpTotExp!=null && hmEmpTotExp.get(strEmpIdNew)!=null) ? hmEmpTotExp.get(strEmpIdNew) : "" %>" >
				<%=uF.showData(hmEmployeeExperience.get(strEmpIdNew), "")%></td>
				<td>
				<% 	Iterator<String> it = hmProData.keySet().iterator();
					String earliestReleaseDate = null;
					double dblBilledPercent = 0;
					double dblUnbilledPercent = 0;
					double dblAvailablePercent = 0;
					while(it.hasNext()) {
						String proId = it.next();
						List<String> innerList = hmProData.get(proId);
						if(earliestReleaseDate ==null) {
							earliestReleaseDate = innerList.get(1);
						}
						if(uF.parseToBoolean(innerList.get(2))) {
							dblBilledPercent += uF.parseToDouble(innerList.get(3));
						} else {
							dblUnbilledPercent += uF.parseToDouble(innerList.get(3));
						}
						/* if(uF.parseToInt(alEmpId.get(i)) == 30) {
							System.out.println("proId ===>> " + proId +" -- dblBilledPercent ===>> " + dblBilledPercent + " -- dblUnbilledPercent ===>> " + dblUnbilledPercent);
						} */
				%>
					<%=uF.showData(innerList.get(0), "")%><br/>
				<% }
					dblAvailablePercent = 100 - (dblBilledPercent+dblUnbilledPercent);
					%>
				</td>
				<%//System.out.println("Data Project Name"); %>
				<td nowrap="nowrap"><%=uF.showData(earliestReleaseDate, "") %></td>
				<%//System.out.println("Data earliestReleaseDate"); %>
				<td>
					<div id="outbox" style="width: 160px !important;">
						<% if(dblBilledPercent>0) { %>
							<div id="billed" style="width: <%=dblBilledPercent %>%;" title="Billed"><%=uF.formatIntoOneDecimalIfDecimalValIsThere(dblBilledPercent) %>%</div>
						<% } %>
						<% if(dblUnbilledPercent>0) { %>
							<div id="unbilled" style="width: <%=dblUnbilledPercent %>%;" title="Unbilled"><%=uF.formatIntoOneDecimalIfDecimalValIsThere(dblUnbilledPercent) %>%</div>
						<% } %>
						<% if(dblAvailablePercent>0) { %>
							<div id="available" style="width: <%=dblAvailablePercent %>%;" title="Available"><%=uF.formatIntoOneDecimalIfDecimalValIsThere(dblAvailablePercent) %>%</div>
						<% } %>
					</div>
				 </td>
				 <%//System.out.println("Data Capacity"); %>
				<td nowrap="nowrap"><%=(String)hmLevel.get((String)hmEmpLevel.get(alEmpId.get(i))) %> </td>
				<%//System.out.println("Data Level"); %>
				<td nowrap="nowrap"><%=uF.showData(hmLocation.get("WL_NAME"), "") %></td>
				<%//System.out.println("Data Work Location"); %>
				<!-- Rate/Day & Cost/Day hide for KPCA  -->
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE")) && !strUserType.equals(IConstants.MANAGER) && uF.parseToInt(strProOwnerOrTL) != 2) { %>
					<td class="alignRight padRight20"><input type="hidden" name="rate" value="<%=rateAmount %>"><%=rateAmount %></td>
					<%//System.out.println("Data Rate"); %>
				<% } %>
				
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST")) && !strUserType.equals(IConstants.MANAGER) && uF.parseToInt(strProOwnerOrTL) != 2) { %>
					<%if(hmEmpSalaryMap.get(alEmpId.get(i))!=null) { %>
					<td class="alignRight padRight20">
					<input type="hidden" value="<%=hmEmpSalaryMap.get(alEmpId.get(i)) %>" name="actualRate"><%=hmEmpSalaryMap.get(alEmpId.get(i)) %></td>
					<%//System.out.println("Data Cost"); %>
					<%} else { %>
					<td><input type="hidden" name="actualRate">N/A</td>
					<%//System.out.println("Data Cost hidden"); %>
					<% } %>
				<% } %>
		
				<td nowrap="nowrap">
					<%if(hmInnerLeave.size()==0) { %>
						<div style="background-color: #00CC00">No Leave</div>
					<%} else if(hmInnerLeave.size() < 4) { %>
						<div style="background-color: #99FF00"><%=hmInnerLeave.size() %> leaves</div>
					<%} else if(hmInnerLeave.size() < 7) { %>
						<div style="background-color: #FFFF33"><%=hmInnerLeave.size() %> leaves</div>
					<%} else if(hmInnerLeave.size() < 15) { %>
						<div style="background-color: #FF9900"><%=hmInnerLeave.size() %> leaves</div>
					<%} else if(hmInnerLeave.size() >= 15) { %>
						<div style="background-color: #FF3300"><%=hmInnerLeave.size() %> leaves</div>
					<% } %>
				</td>
				<%//System.out.println("Data Availability"); %>
				<td align="center"><%=uF.showData(hmTaskAllocation.get(alEmpId.get(i)), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\">&nbsp;</div>") %></td>
				<%//System.out.println("Data Work Allocation"); %>
			</tr>
			<% } %>
			</tbody>
		</table>
		<%-- <% if(alEmpId != null && alEmpId.size() == 0) { %>
			<div><div class="nodata msg" style="width:92%"><span>No employee available in chosen skill category</span></div></div>
		<% } %> --%>
	</div>

<!-- <div id="idTeamInfo" style="border: 2px solid red;    float: right;    height: 100px;    left: 90%;    position: fixed;    top: 20px;    width: 100px;"> -->

<div id="viewworkallocation"></div>
<div id="editEmpRateAndCost"></div>

<script>
var tl = '<%=(String)request.getAttribute("TL") %>';
var tm = '<%=(String)request.getAttribute("TM") %>';
var i=0;

var a=tm.split(",");
var b=tl.split(",");
for(i=0;i<a.length;i++){
	$("#strEmpId"+a[i]).attr('checked',true);
}
for(i=0;i<b.length;i++){
	$("#strTeamLeadId"+b[i]).attr('checked',true);						
}	


</script>