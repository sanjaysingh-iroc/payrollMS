<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>


<style>
.ul_class li {
	margin: 10px 10px 10px 100px;
}
 
p.pclass{padding: 5px 0px 0px 0px}
</style>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Add New Goal" name="title" />
</jsp:include> 
<script type="text/javascript">
$(function() {
	/* $( "#cgoalDueDate0" ).datepicker({format: 'dd/mm/yyyy'});
	$( "#mgoalDueDate0" ).datepicker({format: 'dd/mm/yyyy'});
	$( "#tgoalDueDate0" ).datepicker({format: 'dd/mm/yyyy'});
	$("#igoalDueDate0").datepicker({
		format : 'dd/mm/yyyy'
	}); */
	//$( ".duedatepick" ).datepicker({format: 'dd/mm/yyyy'});
	
	$( "input[name='cgoalDueDate']" ).datepicker({ format: 'dd/mm/yyyy'});
	$( "input[name='mgoalDueDate']" ).datepicker({ format: 'dd/mm/yyyy'});
	$( "input[name='tgoalDueDate']" ).datepicker({ format: 'dd/mm/yyyy'});
	$( "input[name='igoalDueDate']" ).datepicker({ format: 'dd/mm/yyyy'});
	
});




var orientation = '<%=request.getAttribute("orientation")%>';


 var strlevelList = "<%=request.getAttribute("levelListOption")%>";
 var strgradeList = "<%=request.getAttribute("gradeListOption")%>";
var strempList ="<%=request.getAttribute("empListOption")%>";

var strattribute="<%=request.getAttribute("attribute")%>";

var pcount=0;
 function addKRA(ch,count){
	 /* <p><input type="text" name="tKRA" id="tKRA" /><a href="javascript:void(0)" class="add_lvl"
			onclick="addKRA('t',0);">Add KRA</a></p> addKRA('t',0) tKRAtdID0 */
			//cKRACount0 
			var KRACount=document.getElementById(ch+"KRACount"+count).value;
			var KRACountID=ch+"KRACount"+count;
		pcount++;
		var pid=ch+"KRAtd"+count+pcount;
	 	var ptag = document.createElement('p');
		ptag.setAttribute("class", "pclass");
		ptag.id = pid;
		
		var data="<input type=\"text\" name=\""+ch+"KRA\" id=\""+ch+"KRA\" /><a href=\"javascript:void(0)\" class=\"add_lvl\" "
		+"onclick=\"addKRA('"+ch+"',"+count+");\">Add KRA</a><a href=\"javascript:void(0)\" class=\"add_lvl\" "
		+"onclick=\"removeKRAID('"+pid+"','"+KRACountID+"');\">Remove KRA</a>";
		
		ptag.innerHTML=data;
		document.getElementById(ch+"KRAtdID"+count).appendChild(ptag);
		KRACount++;
		document.getElementById(ch+"KRACount"+count).value=KRACount;
			
 }
 function removeKRAID(id,KRACountID) {
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
			
			var a=document.getElementById(KRACountID).value;
			document.getElementById(KRACountID).value=parseInt(a)-1;

		}
	}


	function getMeasureWith(value, ch, count) {
		//$ Effort cdollarAmtid0 cmeasureEffortsid0  
		if (value == '$') {
			document.getElementById(ch + "dollarAmtid" + count).style.display = "table-row";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
		} else if (value == 'Effort') {
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "table-row";
		} else {
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
		}
	}

	function getMeasureKRA(value, ch, count) {
		//cMKRAID0 Yes No 
		if (value == 'Yes') {
			document.getElementById(ch + "MKRAID" + count).style.display = "table-row";
		} else {
			document.getElementById(ch + "MKRAID" + count).style.display = "none";
			document.getElementById(ch + "kraID" + count).style.display = "none";
			document.getElementById(ch + "measureID" + count).style.display = "none";
		}
	}

	function addFeedback(value, ch, count) {
		//Yes No iOrientation0 
		if (value == 'Yes') {
			document.getElementById(ch + "Orientation" + count).style.display = "table-row";
		} else {
			document.getElementById(ch + "Orientation" + count).style.display = "none";
		}
	}

	function addMeasureKRA(value, ch, count) {
		//KRA Measure ckraID0 cmeasureID0 
		if (value == 'KRA') {
			document.getElementById(ch + "kraID" + count).style.display = "table-row";
			document.getElementById(ch + "measureID" + count).style.display = "none";
		} else if (value == 'Measure') {
			document.getElementById(ch + "measureID" + count).style.display = "table-row";
			document.getElementById(ch + "kraID" + count).style.display = "none";
		} else {
			document.getElementById(ch + "kraID" + count).style.display = "none";
			document.getElementById(ch + "measureID" + count).style.display = "none";
		}
	}
	function addMeasureWith(value, ch, count) {
		//$ Effort cmkdollarAmtid0 cmkEffortsid0 
		if (value == '$') {
			document.getElementById(ch + "mkdollarAmtid" + count).style.display = "table-row";
			document.getElementById(ch + "mkEffortsid" + count).style.display = "none";
		} else if (value == 'Effort') {
			document.getElementById(ch + "mkdollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "mkEffortsid" + count).style.display = "table-row";
		} else {
			document.getElementById(ch + "mkEffortsid" + count).style.display = "none";
			document.getElementById(ch + "mkdollarAmtid" + count).style.display = "none";
		}
	}

	var corporateCnt = 0;
	var managerCnt = 0;
	var teamCnt = 0;
	var individualCnt = 0;

	function getCorporate() {
		corporateCnt++;
		managerCnt++;
		teamCnt++;
		individualCnt++;

		var corporateID = 'corporateID' + corporateCnt;
		var managerID = 'managerID' + managerCnt;
		var teamID = 'teamID' + teamCnt;
		var individualID = 'individualID' + individualCnt;
		//alert("sdfgdf");
//alert(strlevelList);
		var litag = document.createElement('ul');
		litag.setAttribute("class", "level_list ul_class");
		litag.id = corporateID;

		var a = "<li>"
				+ getCorporateData(corporateCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"getCorporate();\" >Add New Corporate</a>&nbsp;&nbsp;"
				
				
				+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"deleteBlock1('"
				+ corporateID +"','mainDiv')\">remove Corporate</a>"
				
				+"</li>"

				+ "<li><input type=\"hidden\" name=\"managercount\" id=\"managercount"+corporateID+"\" value=\"1\"/><ul id=\""+managerID+"\"><li>"
				+ getManagerData(managerCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addManager('"
				+ corporateID
				+ "')\" >Add New Manager Goal</a></li>"

				+ "<li><input type=\"hidden\" name=\"teamcount\" id=\"teamcount"+managerID+"\" value=\"1\"/><ul id=\""+teamID+"\"><li>"
				+ getTeamData(teamCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addTeam('"
				+ managerID
				+ "')\" >Add New Team Goal</a></li>"

				+ "<li><input type=\"hidden\" name=\"individualcount\" id=\"individualcount"+teamID+"\" value=\"1\"/><ul id=\""+individualID+"\"><li>"
				+ getIndividualData(individualCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addIndividual('"
				+ teamID + "');\" >Add New Individual Goal</a></li>"
				+ "</ul></li></ul></li></ul></li>";

		//alert(a);

		litag.innerHTML = a;
		document.getElementById("mainDiv").appendChild(litag);

	}

	function getCorporateData(count) {
		count++;
		
		var goaldate="#cgoalDueDate"+count;
		var format="dd/mm/yyyy";
		var a = "<table class=\"tb_style\"><tr><th width=\"20%\"></th><td>Corporate Goal</td></tr>"
				+ "<tr><th align=\"right\" nowrap>Goal</th><td><input type=\"text\" name=\"corporateGoal\" style=\"width:600px;\"/></td></tr>"
				+ "<tr><th align=\"right\" nowrap>Objective</th><td><input type=\"text\" name=\"cgoalObjective\" style=\"width:600px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Description</th><td><input type=\"text\" name=\"cgoalDescription\" style=\"width:600px;\"/></td></tr>"
				
				+ "<tr><th align=\"right\">Align an Attribute</th><td><select name=\"cgoalAlignAttribute\"><option value=\"\" selected=\"selected\">Select</option>"
				+strattribute+"</select></td></tr>"
				
				+ "<tr><th align=\"right\">Measure with</th><td><select name=\"cmeasurewith\" onchange=\"getMeasureWith(this.value,'c',"
				+ count
				+ ")\">"
				+ "<option value=\"\">Select</option><option value=\"$\">$</option><option value=\"Effort\">Effort</option></select></td></tr>"
				+ "<tr id=\"cdollarAmtid"+count+"\" style=\"display: none;\"><th align=\"right\">$</th><td><input type=\"text\" name=\"cmeasureDollar\"/></td></tr>"
				+ "<tr id=\"cmeasureEffortsid"+count+"\" style=\"display: none;\"><th align=\"right\">Efforts</th><td>Days&nbsp;<input type=\"text\" name=\"cmeasureEffortsDays\" style=\"width: 40px;\"/>&nbsp;"
				+ "HRs&nbsp;<input type=\"text\" name=\"cmeasureEffortsHrs\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\" nowrap>Does it have a Measure/KRA</th>"
				
				+"<td><select name=\"cmeasureKra\" onchange=\"getMeasureKRA(this.value,'c',"+count+");\"> <option value=\"Yes\">Yes</option>"
				+"<option value=\"No\" selected>No</option></select></td>"
				
				/* +"<td><input type=\"radio\" name=\"cmeasureKra\" value=\"Yes\" onclick=\"getMeasureKRA(this.value,'c',"
				+ count
				+ ");\"/>Yes"
				+ "<input type=\"radio\" name=\"cmeasureKra\" value=\"No\" onclick=\"getMeasureKRA(this.value,'c',"
				+ count
				+ ");\" checked=\"checked\"/>No</td>" */
				
				+"</tr>"
				+ "<tr id=\"cMKRAID"+count+"\" style=\"display: none;\"><th align=\"right\">Add measure/KRA</th>"
				+"<td><select name=\"cAddMKra\" onclick=\"addMeasureKRA(this.value,'c',"+count+");\"> <option value=\"KRA\">KRA</option>"
				+"<option value=\"Measure\" selected=\"selected\">Measure</option></select></td>"
				
				
				/* +"<td><input type=\"radio\" name=\"cAddMKra\" value=\"KRA\" onclick=\"addMeasureKRA(this.value,'c',"
				+ count
				+ ");\"/>KRA"
				+ "<input type=\"radio\" name=\"cAddMKra\" value=\"Measure\" onclick=\"addMeasureKRA(this.value,'c',"
				+ count
				+ ");\" checked=\"checked\"/>Measure</td>" */
				
				+"</tr>"
				+ "<tr id=\"ckraID"+count+"\" style=\"display: none;\"><th align=\"right\" valign=\"top\">KRA</th>"
				+"<td id=\"cKRAtdID"+count+"\"><input type=\"hidden\" name=\"cKRACount\" id=\"cKRACount"+count+"\" value=\"1\"/><p><input type=\"text\" name=\"cKRA\" id=\"cKRA\" /><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addKRA('c',"+count+");\">Add KRA</a></p></tr>"
				+ "<tr id=\"cmeasureID"+count+"\" style=\"display: none;\"><th align=\"right\">Measure with</th><td><select name=\"cmkwith\" onchange=\"addMeasureWith(this.value,'c',"
				+ count
				+ ")\">"
				+ "<option value=\"\">Select</option><option value=\"$\">$</option><option value=\"Effort\">Effort</option></select></td></tr>"
				+ "<tr id=\"cmkdollarAmtid"+count+"\" style=\"display: none;\"><th align=\"right\">$</th><td><input type=\"text\" name=\"cmeasurekraDollar\"/></td></tr>"
				+ "<tr id=\"cmkEffortsid"+count+"\" style=\"display: none;\"><th align=\"right\">Efforts</th><td>Days&nbsp;<input type=\"text\" name=\"cmeasurekraEffortsDays\" style=\"width: 40px;\"/>&nbsp;"
				+ "HRs&nbsp;<input type=\"text\" name=\"cmeasurekraEffortsHrs\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Due Date</th><td><input type=\"text\" name=\"cgoalDueDate\" id=\"cgoalDueDate"+count+"\" style=\"width: 70px;\" class=\"duedatepick\"/></td></tr>"
				+ "<tr><th align=\"right\">Feedback</th>"
				
				+"<td><select name=\"cgoalFeedback\" onclick=\"addFeedback(this.value,'c',"+count+");\"><option value=\"Yes\">Yes</option>"
				+"<option value=\"No\" selected=\"selected\">No</option></select></td>"
				
				/* +"<td><input type=\"radio\" name=\"cgoalFeedback\" value=\"Yes\" onclick=\"addFeedback(this.value,'c',"
				+ count
				+ ");\"/>Yes"
				+ "<input type=\"radio\" name=\"cgoalFeedback\" value=\"No\" onclick=\"addFeedback(this.value,'c',"
				+ count
				+ ");\" checked=\"checked\"/>No</td>" */
				
				+"</tr>"

				+ "<tr id=\"cOrientation"+count+"\" style=\"display: none;\"><th align=\"right\">Orientation</th><td><select name=\"corientation\">"
				+ orientation
				+ "</select></td></tr>"

				+ "<tr><th align=\"right\">Weightage</th><td><input type=\"text\" name=\"cgoalWeightage\" value=\"100\" id=\"cgoalWeightage\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Assign Goal</th><td>"
				
				+"<div id=\"clevelID"+count+"\" style=\"float: left;\"><select name=\"clevel\" multiple size=\"4\"><option value=\"\" selected>All Level</option>"
				+strlevelList+"</select></div>"
				+"<div id=\"cgradeID"+count+"\" style=\"float: left;\"><select name=\"cgrade\" multiple size=\"4\"><option value=\"\" selected>All Grade</option>"
				+strgradeList+"</select></div>"
				+"<div id=\"cempID"+count+"\"><select name=\"cemp\" multiple size=\"4\"><option value=\"\" selected>All Employee</option>"
				+strempList+"</select></div>"  
				
				+"</td></tr>"
				+ "</table>";
				
				<%-- a+="<script type=\"text/javascript\">$(function() {$( '"+goaldate+"').datepicker({format: '"+format+"'});});</script>"; --%>
				
				//getCpickdate();

		return a;
	}
	/* function getCpickdate(){
	$(function() {
		
		$( "input[name='cgoalDueDate']" ).datepicker({ format: 'dd/mm/yyyy'});
		
	});} */

	function getManagerData(count) {
		count++;

		var a = "<table class=\"tb_style\"><tr><th width=\"20%\"></th><td>Manager Goal</td></tr>"
				+ "<tr><th align=\"right\" nowrap>Goal</th><td><input type=\"text\" name=\"managerGoal\" style=\"width:600px;\"/></td></tr>"
				+ "<tr><th align=\"right\" nowrap>Objective</th><td><input type=\"text\" name=\"mgoalObjective\" style=\"width:600px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Description</th><td><input type=\"text\" name=\"mgoalDescription\" style=\"width:600px;\"/></td></tr>"
				
				+ "<tr><th align=\"right\">Align an Attribute</th><td><select name=\"mgoalAlignAttribute\"><option value=\"\" selected=\"selected\">Select</option>"
				+strattribute+"</select></td></tr>"
				
				+ "<tr><th align=\"right\">Measure with</th><td><select name=\"mmeasurewith\" onchange=\"getMeasureWith(this.value,'m',"
				+ count
				+ ")\">"
				+ "<option value=\"\">Select</option><option value=\"$\">$</option><option value=\"Effort\">Effort</option></select></td></tr>"
				+ "<tr id=\"mdollarAmtid"+count+"\" style=\"display: none;\"><th align=\"right\">$</th><td><input type=\"text\" name=\"mmeasureDollar\"/></td></tr>"
				+ "<tr id=\"mmeasureEffortsid"+count+"\" style=\"display: none;\"><th align=\"right\">Efforts</th><td>Days&nbsp;<input type=\"text\" name=\"mmeasureEffortsDays\" style=\"width: 40px;\"/>&nbsp;"
				+ "HRs&nbsp;<input type=\"text\" name=\"mmeasureEffortsHrs\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\" nowrap>Does it have a Measure/KRA</th>"
				
				+"<td><select name=\"mmeasureKra\" onchange=\"getMeasureKRA(this.value,'m',"+count+");\"> <option value=\"Yes\">Yes</option>"
				+"<option value=\"No\" selected>No</option></select></td>"
				/* +"<td><input type=\"radio\" name=\"mmeasureKra\" value=\"Yes\" onclick=\"getMeasureKRA(this.value,'m',"
				+ count
				+ ");\"/>Yes"
				+ "<input type=\"radio\" name=\"mmeasureKra\" value=\"No\" onclick=\"getMeasureKRA(this.value,'m',"
				+ count
				+ ");\" checked=\"checked\"/>No</td>" */
				
				+"</tr>"
				+ "<tr id=\"mMKRAID"+count+"\" style=\"display: none;\"><th align=\"right\">Add measure/KRA</th>"
				
				+"<td><select name=\"mAddMKra\" onclick=\"addMeasureKRA(this.value,'m',"+count+");\"> <option value=\"KRA\">KRA</option>"
				+"<option value=\"Measure\" selected=\"selected\">Measure</option></select></td>"
				
				/* +"<td><input type=\"radio\" name=\"mAddMKra\" value=\"KRA\" onclick=\"addMeasureKRA(this.value,'m',"
				+ count
				+ ");\"/>KRA"
				+ "<input type=\"radio\" name=\"mAddMKra\" value=\"Measure\" onclick=\"addMeasureKRA(this.value,'m',"
				+ count
				+ ");\" checked=\"checked\"/>Measure</td>" */
				
				+"</tr>"
				+ "<tr id=\"mkraID"+count+"\" style=\"display: none;\"><th align=\"right\" valign=\"top\">KRA</th>"
				+"<td id=\"mKRAtdID"+count+"\"><input type=\"hidden\" name=\"mKRACount\" id=\"mKRACount"+count+"\" value=\"1\"/><p><input type=\"text\" name=\"mKRA\" id=\"mKRA\" /><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addKRA('m',"+count+");\">Add KRA</a></p></tr>"
				+ "<tr id=\"mmeasureID"+count+"\" style=\"display: none;\"><th align=\"right\">Measure with</th><td><select name=\"mmkwith\" onchange=\"addMeasureWith(this.value,'m',"
				+ count
				+ ")\">"
				+ "<option value=\"\">Select</option><option value=\"$\">$</option><option value=\"Effort\">Effort</option></select></td></tr>"
				+ "<tr id=\"mmkdollarAmtid"+count+"\" style=\"display: none;\"><th align=\"right\">$</th><td><input type=\"text\" name=\"mmeasurekraDollar\"/></td></tr>"
				+ "<tr id=\"mmkEffortsid"+count+"\" style=\"display: none;\"><th align=\"right\">Efforts</th><td>Days&nbsp;<input type=\"text\" name=\"mmeasurekraEffortsDays\" style=\"width: 40px;\"/>&nbsp;"
				+ "HRs&nbsp;<input type=\"text\" name=\"mmeasurekraEffortsHrs\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Due Date</th><td><input type=\"text\" name=\"mgoalDueDate\"  id=\"mgoalDueDate"+count+"\"style=\"width: 70px;\" class=\"duedatepick\"/></td></tr>"
				+ "<tr><th align=\"right\">Feedback</th>"
				
				+"<td><select name=\"mgoalFeedback\" onclick=\"addFeedback(this.value,'m',"+count+");\"><option value=\"Yes\">Yes</option>"
				+"<option value=\"No\" selected=\"selected\">No</option></select></td>"
				/* +"<td><input type=\"radio\" name=\"mgoalFeedback\" value=\"Yes\" onclick=\"addFeedback(this.value,'m',"
				+ count
				+ ");\"/>Yes"
				+ "<input type=\"radio\" name=\"mgoalFeedback\" value=\"No\" onclick=\"addFeedback(this.value,'m',"
				+ count
				+ ");\" checked=\"checked\"/>No</td>" */
				
				+"</tr>"

				+ "<tr id=\"mOrientation"+count+"\" style=\"display: none;\"><th align=\"right\">Orientation</th><td><select name=\"morientation\">"
				+ orientation
				+ "</select></td></tr>"

				+ "<tr><th align=\"right\">Weightage</th><td><input type=\"text\" name=\"mgoalWeightage\" value=\"100\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Assign Goal</th><td>"
				
				+"<div id=\"mlevelID"+count+"\" style=\"float: left;\"><select name=\"mlevel\" multiple size=\"4\"><option value=\"\" selected>All Level</option>"
				+strlevelList+"</select></div>"
				+"<div id=\"mgradeID"+count+"\" style=\"float: left;\"><select name=\"mgrade\" multiple size=\"4\"><option value=\"\" selected>All Grade</option>"
				+strgradeList+"</select></div>"
				+"<div id=\"mempID"+count+"\"><select name=\"memp\" multiple size=\"4\"><option value=\"\" selected>All Employee</option>"
				+strempList+"</select></div>"
				
				+"</td></tr>"
				+ "</table>";

		return a;
	}

	function getTeamData(count) {
		count++;

		var a = "<table class=\"tb_style\"><tr><th width=\"20%\"></th><td>Team Goal</td></tr>"
				+ "<tr><th align=\"right\" nowrap>Goal</th><td><input type=\"text\" name=\"teamGoal\" style=\"width:600px;\"/></td></tr>"
				+ "<tr><th align=\"right\" nowrap>Objective</th><td><input type=\"text\" name=\"tgoalObjective\" style=\"width:600px;\" /></td></tr>"
				+ "<tr><th align=\"right\">Description</th><td><input type=\"text\" name=\"tgoalDescription\" style=\"width:600px;\"/></td></tr>"
				
				+ "<tr><th align=\"right\">Align an Attribute</th><td><select name=\"tgoalAlignAttribute\"><option value=\"\"  selected=\"selected\">Select</option>"
				+strattribute+"</select></td></tr>"
				
				+ "<tr><th align=\"right\">Measure with</th><td><select name=\"tmeasurewith\" onchange=\"getMeasureWith(this.value,'t',"
				+ count
				+ ")\">"
				+ "<option value=\"\">Select</option><option value=\"$\">$</option><option value=\"Effort\">Effort</option></select></td></tr>"
				+ "<tr id=\"tdollarAmtid"+count+"\" style=\"display: none;\"><th align=\"right\">$</th><td><input type=\"text\" name=\"tmeasureDollar\"/></td></tr>"
				+ "<tr id=\"tmeasureEffortsid"+count+"\" style=\"display: none;\"><th align=\"right\">Efforts</th><td>Days&nbsp;<input type=\"text\" name=\"tmeasureEffortsDays\" style=\"width: 40px;\"/>&nbsp;"
				+ "HRs&nbsp;<input type=\"text\" name=\"tmeasureEffortsHrs\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\" nowrap>Does it have a Measure/KRA</th>"
				
				+"<td><select name=\"tmeasureKra\" onchange=\"getMeasureKRA(this.value,'t',"+count+");\"> <option value=\"Yes\">Yes</option>"
				+"<option value=\"No\" selected>No</option></select></td>"
				/* +"<td><input type=\"radio\" name=\"tmeasureKra\" value=\"Yes\" onclick=\"getMeasureKRA(this.value,'t',"
				+ count
				+ ");\"/>Yes"
				+ "<input type=\"radio\" name=\"tmeasureKra\" value=\"No\" onclick=\"getMeasureKRA(this.value,'t',"
				+ count
				+ ");\" checked=\"checked\"/>No</td>" */
				
				+"</tr>"
				+ "<tr id=\"tMKRAID"+count+"\" style=\"display: none;\"><th align=\"right\">Add measure/KRA</th>"
				
				+"<td><select name=\"tAddMKra\" onclick=\"addMeasureKRA(this.value,'t',"+count+");\"> <option value=\"KRA\">KRA</option>"
				+"<option value=\"Measure\" selected=\"selected\">Measure</option></select></td>"
				/* +"<td><input type=\"radio\" name=\"tAddMKra\" value=\"KRA\" onclick=\"addMeasureKRA(this.value,'t',"
				+ count
				+ ");\"/>KRA"
				+ "<input type=\"radio\" name=\"tAddMKra\" value=\"Measure\" onclick=\"addMeasureKRA(this.value,'t',"
				+ count
				+ ");\" checked=\"checked\"/>Measure</td>" */
				
				+"</tr>"
				+ "<tr id=\"tkraID"+count+"\" style=\"display: none;\"><th align=\"right\" valign=\"top\">KRA</th>"
				+"<td id=\"tKRAtdID"+count+"\"><input type=\"hidden\" name=\"tKRACount\" id=\"tKRACount"+count+"\" value=\"1\"/><p><input type=\"text\" name=\"tKRA\" id=\"tKRA\" /><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addKRA('t',"+count+");\">Add KRA</a></p></tr>"
				+ "<tr id=\"tmeasureID"+count+"\" style=\"display: none;\"><th align=\"right\">Measure with</th><td><select name=\"tmkwith\" onchange=\"addMeasureWith(this.value,'t',"
				+ count
				+ ")\">"
				+ "<option value=\"\">Select</option><option value=\"$\">$</option><option value=\"Effort\">Effort</option></select></td></tr>"
				+ "<tr id=\"tmkdollarAmtid"+count+"\" style=\"display: none;\"><th align=\"right\">$</th><td><input type=\"text\" name=\"tmeasurekraDollar\"/></td></tr>"
				+ "<tr id=\"tmkEffortsid"+count+"\" style=\"display: none;\"><th align=\"right\">Efforts</th><td>Days&nbsp;<input type=\"text\" name=\"tmeasurekraEffortsDays\" style=\"width: 40px;\"/>&nbsp;"
				+ "HRs&nbsp;<input type=\"text\" name=\"tmeasurekraEffortsHrs\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Due Date</th><td><input type=\"text\" name=\"tgoalDueDate\" id=\"tgoalDueDate"+count+"\" style=\"width: 70px;\" class=\"duedatepick\"/></td></tr>"
				+ "<tr><th align=\"right\">Feedback</th>"
				
				+"<td><select name=\"tgoalFeedback\" onclick=\"addFeedback(this.value,'t',"+count+");\"><option value=\"Yes\">Yes</option>"
				+"<option value=\"No\" selected=\"selected\">No</option></select></td>"
				/* +"<td><input type=\"radio\" name=\"tgoalFeedback\" value=\"Yes\" onclick=\"addFeedback(this.value,'t',"
				+ count
				+ ");\"/>Yes"
				+ "<input type=\"radio\" name=\"tgoalFeedback\" value=\"No\" onclick=\"addFeedback(this.value,'t',"
				+ count
				+ ");\" checked=\"checked\"/>No</td>" */
				
				+"</tr>"

				+ "<tr id=\"tOrientation"+count+"\" style=\"display: none;\"><th align=\"right\">Orientation</th><td><select name=\"torientation\">"
				+ orientation
				+ "</select></td></tr>"

				+ "<tr><th align=\"right\">Weightage</th><td><input type=\"text\" name=\"tgoalWeightage\" value=\"100\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Assign Goal</th><td>"
				
				+"<div id=\"tlevelID"+count+"\" style=\"float: left;\"><select name=\"tlevel\" multiple size=\"4\"><option value=\"\" selected>All Level</option>"
				+strlevelList+"</select></div>"
				+"<div id=\"tgradeID"+count+"\" style=\"float: left;\"><select name=\"tgrade\" multiple size=\"4\"><option value=\"\" selected>All Grade</option>"
				+strgradeList+"</select></div>"
				+"<div id=\"tempID"+count+"\"><select name=\"temp\" multiple size=\"4\"><option value=\"\" selected>All Employee</option>"
				+strempList+"</select></div>"
				
				+"</td></tr>"
				+ "</table>";

		return a;
	}

	function getIndividualData(count) {
		count++;

		var a = "<table class=\"tb_style\"><tr><th width=\"20%\"></th><td>Individual Goal</td></tr>"
				+ "<tr><th align=\"right\" nowrap>Goal</th><td><input type=\"text\" name=\"individualGoal\" style=\"width:600px;\"/></td></tr>"
				+ "<tr><th align=\"right\" nowrap>Objective</th><td><input type=\"text\" name=\"igoalObjective\" style=\"width:600px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Description</th><td><input type=\"text\" name=\"igoalDescription\" style=\"width:600px;\"/></td></tr>"
				
				+ "<tr><th align=\"right\">Align an Attribute</th><td><select name=\"igoalAlignAttribute\"><option value=\"\"  selected=\"selected\">Select</option>"
				+strattribute+"</select></td></tr>"
				
				+ "<tr><th align=\"right\">Measure with</th><td><select name=\"imeasurewith\" onchange=\"getMeasureWith(this.value,'i',"
				+ count
				+ ")\">"
				+ "<option value=\"\">Select</option><option value=\"$\">$</option><option value=\"Effort\">Effort</option></select></td></tr>"
				+ "<tr id=\"idollarAmtid"+count+"\" style=\"display: none;\"><th align=\"right\">$</th><td><input type=\"text\" name=\"imeasureDollar\"/></td></tr>"
				+ "<tr id=\"imeasureEffortsid"+count+"\" style=\"display: none;\"><th align=\"right\">Efforts</th><td>Days&nbsp;<input type=\"text\" name=\"imeasureEffortsDays\" style=\"width: 40px;\"/>&nbsp;"
				+ "HRs&nbsp;<input type=\"text\" name=\"imeasureEffortsHrs\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\" nowrap>Does it have a Measure/KRA</th>"
				
				+"<td><select name=\"imeasureKra\" onchange=\"getMeasureKRA(this.value,'i',"+count+");\"> <option value=\"Yes\">Yes</option>"
				+"<option value=\"No\" selected>No</option></select></td>"
				/* +"<td><input type=\"radio\" name=\"imeasureKra\" value=\"Yes\" onclick=\"getMeasureKRA(this.value,'i',"
				+ count
				+ ");\"/>Yes"
				+ "<input type=\"radio\" name=\"imeasureKra\" value=\"No\" onclick=\"getMeasureKRA(this.value,'i',"
				+ count
				+ ");\" checked=\"checked\"/>No</td>" */
				
				+"</tr>"
				+ "<tr id=\"iMKRAID"+count+"\" style=\"display: none;\"><th align=\"right\">Add measure/KRA</th>"
				
				+"<td><select name=\"iAddMKra\" onclick=\"addMeasureKRA(this.value,'i',"+count+");\"> <option value=\"KRA\">KRA</option>"
				+"<option value=\"Measure\" selected=\"selected\">Measure</option></select></td>"
				/* +"<td><input type=\"radio\" name=\"iAddMKra\" value=\"KRA\" onclick=\"addMeasureKRA(this.value,'i',"
				+ count
				+ ");\"/>KRA"
				+ "<input type=\"radio\" name=\"iAddMKra\" value=\"Measure\" onclick=\"addMeasureKRA(this.value,'i',"
				+ count
				+ ");\" checked=\"checked\"/>Measure</td>" */
				
				+"</tr>"
				+ "<tr id=\"ikraID"+count+"\" style=\"display: none;\"><th align=\"right\" valign=\"top\">KRA</th>"
				+"<td id=\"iKRAtdID"+count+"\"><input type=\"hidden\" name=\"iKRACount\" id=\"iKRACount"+count+"\" value=\"1\"/><p><input type=\"text\" name=\"iKRA\" id=\"iKRA\" /><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addKRA('i',"+count+");\">Add KRA</a></p></tr>"
				+ "<tr id=\"imeasureID"+count+"\" style=\"display: none;\"><th align=\"right\">Measure with</th><td><select name=\"imkwith\" onchange=\"addMeasureWith(this.value,'i',"
				+ count
				+ ")\">"
				+ "<option value=\"\">Select</option><option value=\"$\">$</option><option value=\"Effort\">Effort</option></select></td></tr>"
				+ "<tr id=\"imkdollarAmtid"+count+"\" style=\"display: none;\"><th align=\"right\">$</th><td><input type=\"text\" name=\"imeasurekraDollar\"/></td></tr>"
				+ "<tr id=\"imkEffortsid"+count+"\" style=\"display: none;\"><th align=\"right\">Efforts</th><td>Days&nbsp;<input type=\"text\" name=\"imeasurekraEffortsDays\" style=\"width: 40px;\"/>&nbsp;"
				+ "HRs&nbsp;<input type=\"text\" name=\"imeasurekraEffortsHrs\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Due Date</th><td><input type=\"text\" name=\"igoalDueDate\"  id=\"igoalDueDate"+count+"\"style=\"width: 70px;\" class=\"duedatepick\"/></td></tr>"
				+ "<tr><th align=\"right\">Feedback</th>"
				
				+"<td><select name=\"igoalFeedback\" onclick=\"addFeedback(this.value,'i',"+count+");\"><option value=\"Yes\">Yes</option>"
				+"<option value=\"No\" selected=\"selected\">No</option></select></td>"
				/* +"<td><input type=\"radio\" name=\"igoalFeedback\" value=\"Yes\" onclick=\"addFeedback(this.value,'i',"
				+ count
				+ ");\"/>Yes"
				+ "<input type=\"radio\" name=\"igoalFeedback\" value=\"No\" onclick=\"addFeedback(this.value,'i',"
				+ count
				+ ");\" checked=\"checked\"/>No</td>" */
				
				+"</tr>"

				+ "<tr id=\"iOrientation"+count+"\" style=\"display: none;\"><th align=\"right\">Orientation</th><td><select name=\"iorientation\">"
				+ orientation
				+ "</select></td></tr>"

				+ "<tr><th align=\"right\">Weightage</th><td><input type=\"text\" name=\"igoalWeightage\" value=\"100\" style=\"width: 40px;\"/></td></tr>"
				+ "<tr><th align=\"right\">Assign Goal</th><td>"
				
				+"<div id=\"ilevelID"+count+"\" style=\"float: left;\"><select name=\"ilevel\" multiple size=\"4\"><option value=\"\" selected>All Level</option>"
				+strlevelList+"</select></div>"
				+"<div id=\"igradeID"+count+"\" style=\"float: left;\"><select name=\"igrade\" multiple size=\"4\"><option value=\"\" selected>All Grade</option>"
				+strgradeList+"</select></div>"
				+"<div id=\"iempID"+count+"\"><select name=\"iemp\" multiple size=\"4\"><option value=\"\" selected>All Employee</option>"
				+strempList+"</select></div>"
				
				+"</td></tr>"
				+ "</table>";

		return a;
	}

	function addIndividual(id) {

		individualCnt++;

		var individualID = 'individualID' + individualCnt;

		var litag = document.createElement('li');
		litag.id = individualID;

		var val = document.getElementById("individualcount" + id).value;

		document.getElementById("individualcount" + id).value = parseInt(val) + 1;

		var a = "<ul id=\""+individualID+"\"><li>"
				+ getIndividualData(individualCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addIndividual('"
				+ id
				+ "');\" >Add New Individual Goal</a>&nbsp;"
				
				
				+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"deleteBlock('"
				+ individualID + "','" +id 
				+ "','individualcount')\"  >remove Individual</a>"
				
				+"</li>"
				+ "</ul>";

		litag.innerHTML = a;
		document.getElementById(id).appendChild(litag);
	}
	function addTeam(id) {
		teamCnt++;
		individualCnt++;

		var teamID = 'teamID' + teamCnt;
		var individualID = 'individualID' + individualCnt;

		var litag = document.createElement('li');
		litag.id = teamID;

		var val = document.getElementById("teamcount" + id).value;

		document.getElementById("teamcount" + id).value = parseInt(val) + 1;

		var a = "<ul id=\""+teamID+"\"><li>"
				+ getTeamData(teamCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addTeam('"
				+ id
				+ "')\" >Add New Team Goal</a>&nbsp;"
				
				

				+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"deleteBlock('"
				+ teamID + "','" +id 
				+ "','teamcount')\"  >remove Team</a>"

				+ "<li><input type=\"hidden\" name=\"individualcount\" id=\"individualcount"+teamID+"\" value=\"1\"/><ul id=\""+individualID+"\"><li>"
				+ getIndividualData(individualCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addIndividual('"
				+ teamID
				+ "');\" >Add New Individual Goal</a></li>"
				+ "</ul></li></ul>";
		litag.innerHTML = a;
		document.getElementById(id).appendChild(litag);
	}

	function addManager(id) {
		managerCnt++;
		teamCnt++;
		individualCnt++;

		var managerID = 'managerID' + managerCnt;
		var teamID = 'teamID' + teamCnt;
		var individualID = 'individualID' + individualCnt;

		var litag = document.createElement('li');
		litag.id = managerID;

		var val = document.getElementById("managercount" + id).value;

		document.getElementById("managercount" + id).value = parseInt(val) + 1;

		var a = "<ul id=\""+managerID+"\"><li>"
				+ getManagerData(managerCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addManager('"
				+ id
				+ "')\" >Add New Manager Goal</a>&nbsp;"
				
				
				+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"deleteBlock('"
				+ managerID + "','" +id 
				+ "','managercount')\"  >remove Manager</a></li>"
				
				+ "<li><input type=\"hidden\" name=\"teamcount\" id=\"teamcount"+managerID+"\" value=\"1\"/><ul id=\""+teamID+"\"><li>"
				+ getTeamData(teamCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addTeam('"
				+ managerID
				+ "')\" >Add New Team Goal</a></li>"

				+ "<li><input type=\"hidden\" name=\"individualcount\" id=\"individualcount"+teamID+"\" value=\"1\"/><ul id=\""+individualID+"\"><li>"
				+ getIndividualData(individualCnt)
				+ "</li>"
				+ "<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addIndividual('"
				+ teamID
				+ "');\" >Add New Individual Goal</a></li>"
				+ "</ul></li></ul></li></ul>";
		litag.innerHTML = a;
		document.getElementById(id).appendChild(litag);
	}


	function deleteBlock(childId, parentIds,type){
	
		//var a=document.getElementById(type+parentIds).value;
		//document.getElementById(type+parentIds).value=parseInt(a)-1;
			
			var row_skill = document.getElementById(childId); 
			document.getElementById(parentIds).removeChild(row_skill);
	}
	

	 function deleteBlock1(childId, parentIds){	
			var row_skill = document.getElementById(childId); 
			document.getElementById(parentIds).removeChild(row_skill);
	}
	
	
	
	
</script>
<%
	
%>
<div class="leftbox reportWidth">

	<s:form action="#" id="formID" method="POST" theme="simple"> 
	<%-- <input type="hidden" name="strlevelList1" id="strlevelList1" value="<%=strlevelList1 %>" />
	<input type="hidden" name="strgradeList1" id="strgradeList1" value="<%=strgradeList1 %>" />
	<input type="hidden" name="strempList1" id="strempList1" value="<%=strempList1 %>" /> --%>
		<div id="mainDiv">
			<ul id="corporateID0" class="level_list ul_class">
				<li>
					<table class="tb_style">
						<tr>
							<th width="20%"></th>
							<td>Corporate Goal</td>
						</tr>
						<tr>
							<th nowrap align="right">Goal</th>
							<td><input type="text" name="corporateGoal" style="width:600px;" />
							</td>
						</tr>
						<tr>
							<th nowrap align="right">Objective</th>
							<td><input type="text" name="cgoalObjective" style="width:600px;" />
							</td>
						</tr>
						<tr>
							<th align="right">Description</th>
							<td><input type="text" name="cgoalDescription" style="width:600px;" />
							</td>
						</tr>
						<tr>
							<th align="right">Align an Attribute</th>
							<td>
							<%--  <s:select name="cgoalAlignAttribute" list="attributeList" listKey="id"
                                         listValue="name" headerKey=""
                                        headerValue="Select Attribute"></s:select> --%>
							<select name="cgoalAlignAttribute">
											<option value="" selected="selected">Select</option>
											<%=request.getAttribute("attribute")%>
									</select>							
							</td>
						</tr>
						<tr>
							<th align="right">Measure with</th>
							<td><select name="cmeasurewith"
								onchange="getMeasureWith(this.value,'c',0)">
									<option value="">Select</option>
									<option value="$">$</option>
									<option value="Effort">Effort</option>
							</select>
							</td>
						</tr>
						<tr id="cdollarAmtid0" style="display: none;">
							<th align="right">$</th>
							<td><input type="text" name="cmeasureDollar" />
							</td>
						</tr>
						<tr id="cmeasureEffortsid0" style="display: none;">
							<th align="right">Efforts</th>
							<td>Days&nbsp;<input type="text" name="cmeasureEffortsDays"
								style="width: 40px;" />&nbsp; HRs&nbsp;<input type="text"
								name="cmeasureEffortsHrs" style="width: 40px;" />
							</td>
						</tr>

						<tr>
							<th nowrap align="right">Does it have a Measure/KRA</th>
							<td>
							<select name="cmeasureKra"
								onclick="getMeasureKRA(this.value,'c',0);">
									<option value="Yes">Yes</option>
									<option value="No" selected="selected">No</option>
							</select>
							
							<!-- <input type="radio" name="cmeasureKra" value="Yes"
								onclick="getMeasureKRA(this.value,'c',0);" />Yes <input
								type="radio" name="cmeasureKra" value="No"
								onclick="getMeasureKRA(this.value,'c',0);" checked="checked"/>No -->
								</td>
						</tr>
						<tr id="cMKRAID0" style="display: none;">
							<th align="right">Add measure/KRA</th>
							<td>
							<select name="cAddMKra"
								onclick="addMeasureKRA(this.value,'c',0);">
									<option value="KRA">KRA</option>
									<option value="Measure" selected="selected">Measure</option>
							</select>
														 
							<!-- <input type="radio" name="cAddMKra" value="KRA"
								onclick="addMeasureKRA(this.value,'c',0);" />KRA <input
								type="radio" name="cAddMKra" value="Measure"
								onclick="addMeasureKRA(this.value,'c',0);" checked="checked"/>Measure</td> -->
						</tr>

						<tr id="ckraID0" style="display: none;">
							<th valign="top" align="right">KRA</th>
							<td id="cKRAtdID0">
							<input type="hidden" name="cKRACount" id="cKRACount0" value="1"/>
							<p><input type="text" name="cKRA" id="cKRA" /><a href="javascript:void(0)" class="add_lvl"
							onclick="addKRA('c',0);">Add KRA</a></p>											
											</td>
						</tr>

						<tr id="cmeasureID0" style="display: none;">
							<th align="right">Measure with</th>
							<td><select name="cmkwith"
								onchange="addMeasureWith(this.value,'c',0)">
									<option value="">Select</option>
									<option value="$">$</option>
									<option value="Effort">Effort</option>
							</select>
							</td>
						</tr>
						<tr id="cmkdollarAmtid0" style="display: none;">
							<th align="right">$</th>
							<td><input type="text" name="cmeasurekraDollar" />
							</td>
						</tr>
						<tr id="cmkEffortsid0" style="display: none;">
							<th align="right">Efforts</th>
							<td>Days&nbsp;<input type="text" name="cmeasurekraEffortsDays"
								style="width: 40px;" />&nbsp; HRs&nbsp;<input type="text"
								name="cmeasurekraEffortsHrs" style="width: 40px;" />
							</td>
						</tr>


						<tr>
							<th align="right">Due Date</th>
							<td><input type="text" name="cgoalDueDate" id="cgoalDueDate0" class="duedatepick"
								style="width: 70px;" />
							</td>
						</tr>
						<tr>
							<th align="right">Feedback</th>
							<td>
							<select name="cgoalFeedback"
								onclick="addFeedback(this.value,'c',0);">
									<option value="Yes">Yes</option>
									<option value="No" selected="selected">No</option>
							</select>
							
							<!-- <input type="radio" name="cgoalFeedback" value="Yes"
								onclick="addFeedback(this.value,'c',0);" />Yes <input
								type="radio" name="cgoalFeedback" value="No"
								onclick="addFeedback(this.value,'c',0);" checked="checked"/>No -->
								
								</td>
						</tr>
						<tr id="cOrientation0" style="display: none;">
							<th align="right">Orientation</th>
							<td><select name="corientation">
									<%=request.getAttribute("orientation")%>
							</select></td>
						</tr>
						<tr>
							<th align="right">Weightage</th>
							<td><input type="text" name="cgoalWeightage" value="100"
								style="width: 40px;" /></td>
						</tr>
						<tr>
							<th align="right">Assign Goal</th>
							<td>
								<div id="clevelID0" style="float: left;">
									
									
									 <s:select name="clevel" list="levelList" listKey="levelId"
                                         listValue="levelCodeName" headerKey="0"
                                        headerValue="All Level" 
                                         multiple="true"
                                        size="4" value="0"></s:select>
								</div>
								<div id="cgradeID0" style="float: left;">
									
									
									 <s:select name="cgrade" list="gradeList" listKey="gradeId"
                                            listValue="gradeCode" headerKey="0" 
                                            headerValue="All Grade"
                                            multiple="true"
                                            size="4" value="0"></s:select>
								</div>
								<div id="cempID0">
									
									
									<s:select name="cemp" list="empList" listKey="employeeId"
                                            listValue="employeeCode" headerKey="0" 
                                            headerValue="All Employee"
                                             multiple="true"
                                            size="4" value="0"></s:select>
								</div></td>
						</tr>
						<tr>
							<th><a href="javascript:void(0);" class="add_lvl" id="a"
								onclick="getCorporate();">Add Corporate</a>
							</th>
							<td></td>
						</tr>

					</table></li>
				<li><input type="hidden" name="managercount"
					id="managercountcorporateID0" value="1" />
					<ul id="managerID0" class="level_list ul_class">
						<li>
							<table class="tb_style">
								<tr>
									<th width="20%"></th>
									<td>Manager Goal</td>
								</tr>
								<tr>
									<th nowrap align="right">Goal</th>
									<td><input type="text" name="managerGoal" style="width:600px;"/>
									</td>
								</tr>
								<tr>
									<th align="right" nowrap>Objective</th>
									<td><input type="text" name="mgoalObjective" style="width:600px;"/>
									</td>
								</tr>
								<tr>
									<th align="right">Description</th>
									<td><input type="text" name="mgoalDescription" style="width:600px;"/>
									</td>
								</tr>
								<tr>
									<th align="right">Align an Attribute</th>
									<td>
									<%-- <s:select name="mgoalAlignAttribute" list="attributeList" listKey="id"
                                         listValue="name" headerKey=""
                                        headerValue="Select Attribute"></s:select> --%>
									
									<select name="mgoalAlignAttribute">
											<option value="" selected="selected">Select</option>
											<%=request.getAttribute("attribute")%>
									</select>
									</td>
								</tr>
								<tr>
									<th align="right">Measure with</th>
									<td><select name="mmeasurewith"
										onchange="getMeasureWith(this.value,'m',0)">
											<option value="">Select</option>
											<option value="$">$</option>
											<option value="Effort">Effort</option>
									</select>
									</td>
								</tr>
								<tr id="mdollarAmtid0" style="display: none;">
									<th align="right">$</th>
									<td><input type="text" name="mmeasureDollar" />
									</td>
								</tr>
								<tr id="mmeasureEffortsid0" style="display: none;">
									<th align="right">Efforts</th>
									<td>Days&nbsp;<input type="text"
										name="mmeasureEffortsDays" style="width: 40px;" />&nbsp;
										HRs&nbsp;<input type="text" name="mmeasureEffortsHrs"
										style="width: 40px;" />
									</td>
								</tr>

								<tr>
									<th align="right" nowrap>Does it have a Measure/KRA</th>
									<td>
									<select name="mmeasureKra"
								onchange="getMeasureKRA(this.value,'m',0);">
									<option value="Yes">Yes</option>
									<option value="No" selected="selected">No</option>
							</select>
									<!-- <input type="radio" name="mmeasureKra" value="Yes"
										onclick="getMeasureKRA(this.value,'m',0);" />Yes <input
										type="radio" name="mmeasureKra" value="No"
										onclick="getMeasureKRA(this.value,'m',0);" checked="checked" />No -->
										</td>
								</tr>
								<tr id="mMKRAID0" style="display: none;">
									<th align="right">Add measure/KRA</th>
									<td>
									<select name="mAddMKra"
								onclick="addMeasureKRA(this.value,'m',0);">
									<option value="KRA">KRA</option>
									<option value="Measure" selected="selected">Measure</option>
							</select>
									<!-- <input type="radio" name="mAddMKra" value="KRA"
										onclick="addMeasureKRA(this.value,'m',0);" />KRA <input
										type="radio" name="mAddMKra" value="Measure"
										onclick="addMeasureKRA(this.value,'m',0);" checked="checked"/>Measure -->
										</td>
								</tr>

								<tr id="mkraID0" style="display: none;">
									<th align="right" valign="top">KRA</th>
									<!-- <td><input type="text" name="mKRA" /></td> -->
									<td id="mKRAtdID0">
									<input type="hidden" name="mKRACount" id="mKRACount0" value="1"/>
									<p><input type="text" name="mKRA" id="mKRA" /><a href="javascript:void(0)" class="add_lvl"
							onclick="addKRA('m',0);">Add KRA</a></p>											
											</td>
								</tr>

								<tr id="mmeasureID0" style="display: none;">
									<th align="right">Measure with</th>
									<td><select name="mmkwith"
										onchange="addMeasureWith(this.value,'m',0)">
											<option value="">Select</option>
											<option value="$">$</option>
											<option value="Effort">Effort</option>
									</select>
									</td>
								</tr>
								<tr id="mmkdollarAmtid0" style="display: none;">
									<th align="right">$</th>
									<td><input type="text" name="mmeasurekraDollar" />
									</td>
								</tr>
								<tr id="mmkEffortsid0" style="display: none;">
									<th align="right">Efforts</th>
									<td>Days&nbsp;<input type="text"
										name="mmeasurekraEffortsDays" style="width: 40px;" />&nbsp;
										HRs&nbsp;<input type="text" name="mmeasurekraEffortsHrs"
										style="width: 40px;" />
									</td>
								</tr>


								<tr>
									<th align="right">Due Date</th>
									<td><input type="text" name="mgoalDueDate" id="mgoalDueDate0" class="duedatepick"
										style="width: 70px;" />
									</td>
								</tr>
								<tr>
									<th align="right">Feedback</th>
									<td>
									<select name="mgoalFeedback"
								onclick="addFeedback(this.value,'m',0);">
									<option value="Yes">Yes</option>
									<option value="No" selected="selected">No</option>
							</select>
									<!-- <input type="radio" name="mgoalFeedback" value="Yes"
										onclick="addFeedback(this.value,'m',0);" />Yes <input
										type="radio" name="mgoalFeedback" value="No"
										onclick="addFeedback(this.value,'m',0);" checked="checked"/>No -->
										</td>
								</tr>
								<tr id="mOrientation0" style="display: none;">
									<th align="right">Orientation</th>
									<td><select name="morientation">
											<%=request.getAttribute("orientation")%>
									</select></td>
								</tr>
								<tr>
									<th align="right">Weightage</th>
									<td><input type="text" name="mgoalWeightage" value="100"
										style="width: 40px;" /></td>
								</tr>
								<tr>
									<th align="right">Assign Goal</th>
									<td>
										<div id="mlevelID0" style="float: left;">
											
											
											 <s:select name="mlevel" list="levelList" listKey="levelId"
                                         listValue="levelCodeName" headerKey="0"
                                        headerValue="All Level" 
                                         multiple="true"
                                        size="4" value="0"></s:select>
										</div>
										<div id="mgradeID0" style="float: left;">
											
											
											 <s:select name="mgrade" list="gradeList" listKey="gradeId"
                                            listValue="gradeCode" headerKey="0" 
                                            headerValue="All Grade"
                                            multiple="true"
                                            size="4" value="0"></s:select>
										</div>
										<div id="mempID0">
											
											
											<s:select name="memp" list="empList" listKey="employeeId"
                                            listValue="employeeCode" headerKey="0" 
                                            headerValue="All Employee"
                                             multiple="true"
                                            size="4" value="0"></s:select>
										</div></td>
								</tr>

							</table></li>
						<li><a href="javascript:void(0)" class="add_lvl"
							onclick="addManager('corporateID0')">Add New Manager Goal</a></li>

						<li><input type="hidden" name="teamcount"
							id="teamcountmanagerID0" value="1" />
							<ul id="teamID0" class="level_list ul_class">
								<li>
									<table class="tb_style">
										<tr>
											<th width="20%"></th>
											<td>Team Goal</td>
										</tr>
										<tr>
											<th align="right" nowrap>Goal</th>
											<td><input type="text" name="teamGoal" /> 
											</td>
										</tr>
										<tr>
											<th align="right" nowrap>Objective</th>
											<td><input type="text" name="tgoalObjective" style="width:600px;"/>
											</td>
										</tr>
										<tr>
											<th align="right">Description</th>
											<td><input type="text" name="tgoalDescription" style="width:600px;"/>
											</td>
										</tr>
										<tr>
											<th align="right">Align an Attribute</th>
											<td>
											<%-- <s:select name="tgoalAlignAttribute" list="attributeList" listKey="id"
                                         listValue="name" headerKey=""
                                        headerValue="Select Attribute"></s:select> --%>
											
											
											<select name="tgoalAlignAttribute">
													<option value="" selected="selected">Select</option>
													<%=request.getAttribute("attribute")%>
											</select>
											</td>
										</tr>
										<tr>
											<th align="right">Measure with</th>
											<td><select name="tmeasurewith"
												onchange="getMeasureWith(this.value,'t',0)">
													<option value="">Select</option>
													<option value="$">$</option>
													<option value="Effort">Effort</option>
											</select>
											</td>
										</tr>
										<tr id="tdollarAmtid0" style="display: none;">
											<th align="right">$</th>
											<td><input type="text" name="tmeasureDollar" />
											</td>
										</tr>
										<tr id="tmeasureEffortsid0" style="display: none;">
											<th align="right">Efforts</th>
											<td>Days&nbsp;<input type="text"
												name="tmeasureEffortsDays" style="width: 40px;" />&nbsp;
												HRs&nbsp;<input type="text" name="tmeasureEffortsHrs"
												style="width: 40px;" />
											</td>
										</tr>

										<tr>
											<th align="right" nowrap>Does it have a Measure/KRA</th>
											<td>
											<select name="tmeasureKra"
								onchange="getMeasureKRA(this.value,'t',0);">
									<option value="Yes">Yes</option>
									<option value="No" selected="selected">No</option>
							</select>
											<!-- <input type="radio" name="tmeasureKra" value="Yes"
												onclick="getMeasureKRA(this.value,'t',0);" />Yes <input
												type="radio" name="tmeasureKra" value="No"
												onclick="getMeasureKRA(this.value,'t',0);" checked="checked" />No -->
												</td>
										</tr>
										<tr id="tMKRAID0" style="display: none;">
											<th align="right">Add measure/KRA</th>
											<td>
											<select name="tAddMKra"
								onclick="addMeasureKRA(this.value,'t',0);">
									<option value="KRA">KRA</option>
									<option value="Measure" selected="selected">Measure</option>
							</select>
											<!-- <input type="radio" name="tAddMKra" value="KRA"
												onclick="addMeasureKRA(this.value,'t',0);" />KRA <input
												type="radio" name="tAddMKra" value="Measure"
												onclick="addMeasureKRA(this.value,'t',0);" checked="checked"/>Measure -->
												</td>
										</tr>

										<tr id="tkraID0" style="display: none;">
											<th align="right" valign="top">KRA</th>
											<td id="tKRAtdID0">
											<input type="hidden" name="tKRACount" id="tKRACount0" value="1"/>
											<p><input type="text" name="tKRA" id="tKRA" /><a href="javascript:void(0)" class="add_lvl"
							onclick="addKRA('t',0);">Add KRA</a></p>											
											</td> 
										</tr>

										<tr id="tmeasureID0" style="display: none;">
											<th align="right">Measure with</th>
											<td><select name="tmkwith"
												onchange="addMeasureWith(this.value,'t',0)">
													<option value="">Select</option>
													<option value="$">$</option>
													<option value="Effort">Effort</option>
											</select>
											</td>
										</tr>
										<tr id="tmkdollarAmtid0" style="display: none;">
											<th align="right">$</th>
											<td><input type="text" name="tmeasurekraDollar"/>
											</td>
										</tr>
										<tr id="tmkEffortsid0" style="display: none;">
											<th align="right">Efforts</th>
											<td>Days&nbsp;<input type="text"
												name="tmeasurekraEffortsDays" style="width: 40px;" />&nbsp;
												HRs&nbsp;<input type="text" name="tmeasurekraEffortsHrs"
												style="width: 40px;" />
											</td>
										</tr>


										<tr>
											<th align="right">Due Date</th>
											<td><input type="text" name="tgoalDueDate" id="tgoalDueDate0" class="duedatepick"
												style="width: 70px;" />
											</td>
										</tr>
										<tr>
											<th align="right">Feedback</th>
											<td>
											<select name="tgoalFeedback"
								onclick="addFeedback(this.value,'t',0);">
									<option value="Yes">Yes</option>
									<option value="No" selected="selected">No</option>
							</select>
											
											<!-- <input type="radio" name="tgoalFeedback" value="Yes"
												onclick="addFeedback(this.value,'t',0);" />Yes <input
												type="radio" name="tgoalFeedback" value="No"
												onclick="addFeedback(this.value,'t',0);" checked="checked" />No -->
												</td>
										</tr>
										<tr id="tOrientation0" style="display: none;">
											<th align="right">Orientation</th>
											<td><select name="torientation">
													<%=request.getAttribute("orientation")%>
											</select></td>
										</tr>
										<tr>
											<th align="right">Weightage</th>
											<td><input type="text" name="tgoalWeightage" value="100"
												style="width: 40px;" /></td>
										</tr>
										<tr>
											<th align="right">Assign Goal</th>
											<td>
												<div id="tlevelID0" style="float: left;">
													
													
													 <s:select name="tlevel" list="levelList" listKey="levelId"
                                         listValue="levelCodeName" headerKey="0"
                                        headerValue="All Level" 
                                         multiple="true"
                                        size="4" value="0"></s:select>
												</div>
												<div id="tgradeID0" style="float: left;">
													
													
													 <s:select name="tgrade" list="gradeList" listKey="gradeId"
                                            listValue="gradeCode" headerKey="0" 
                                            headerValue="All Grade"
                                            multiple="true"
                                            size="4" value="0"></s:select>
												</div>
												<div id="tempID0">
													
													
													<s:select name="temp" list="empList" listKey="employeeId"
                                            listValue="employeeCode" headerKey="0" 
                                            headerValue="All Employee"
                                             multiple="true"
                                            size="4" value="0"></s:select>
												</div></td>
										</tr>

									</table></li>
								<li><a href="javascript:void(0)" class="add_lvl"
									onclick="addTeam('managerID0')">Add New Team Goal</a>
								</li>

								<li><input type="hidden" name="individualcount"
									id="individualcountteamID0" " value="1" />
									<ul id="individualID0" class="level_list ul_class">
										<li>
											<table class="tb_style">
												<tr>
													<th width="20%"></th>
													<td>Individual Goal</td>
												</tr>
												<tr>
													<th align="right" nowrap>Goal</th>
													<td><input type="text" name="individualGoal" style="width:600px;"/>
													</td>
												</tr>
												<tr>
													<th align="right" nowrap>Objective</th>
													<td><input type="text" name="igoalObjective" style="width:600px;"/>
													</td>
												</tr>
												<tr>
													<th align="right">Description</th>
													<td><input type="text" name="igoalDescription" style="width:600px;"/>
													</td>
												</tr>
												<tr>
													<th align="right">Align an Attribute</th>
													<td>
													<%-- <s:select name="igoalAlignAttribute" list="attributeList" listKey="id"
                                         listValue="name" headerKey=""
                                        headerValue="Select Attribute"></s:select> --%>
													
													<select name="igoalAlignAttribute">
															<option value="" selected="selected">Select</option>
															<%=request.getAttribute("attribute")%>
													</select>
													</td>
												</tr>
												<tr>
													<th align="right">Measure with</th>
													<td><select name="imeasurewith"
														onchange="getMeasureWith(this.value,'i',0)">
															<option value="">Select</option>
															<option value="$">$</option>
															<option value="Effort">Effort</option>
													</select>
													</td>
												</tr>
												<tr id="idollarAmtid0" style="display: none;">
													<th align="right">$</th>
													<td><input type="text" name="imeasureDollar" />
													</td>
												</tr>
												<tr id="imeasureEffortsid0" style="display: none;">
													<th align="right">Efforts</th>
													<td>Days&nbsp;<input type="text"
														name="imeasureEffortsDays" style="width: 40px;" />&nbsp;
														HRs&nbsp;<input type="text" name="imeasureEffortsHrs"
														style="width: 40px;" />
													</td>
												</tr>

												<tr>
													<th align="right" nowrap>Does it have a Measure/KRA</th>
													<td>
													<select name="imeasureKra"
								onchange="getMeasureKRA(this.value,'i',0);">
									<option value="Yes">Yes</option>
									<option value="No" selected="selected">No</option>
							</select>
													<!-- <input type="radio" name="imeasureKra" value="Yes"
														onclick="getMeasureKRA(this.value,'i',0);" />Yes <input
														type="radio" name="imeasureKra" value="No"
														onclick="getMeasureKRA(this.value,'i',0);" checked="checked" />No -->
														</td>
												</tr>
												<tr id="iMKRAID0" style="display: none;">
													<th align="right">Add measure/KRA</th>
													<td>
													<select name="iAddMKra"
								onclick="addMeasureKRA(this.value,'i',0);">
									<option value="KRA">KRA</option>
									<option value="Measure" selected="selected">Measure</option>
							</select>
													<!-- <input type="radio" name="iAddMKra" value="KRA"
														onclick="addMeasureKRA(this.value,'i',0);" />KRA <input
														type="radio" name="iAddMKra" value="Measure"
														onclick="addMeasureKRA(this.value,'i',0);" checked="checked"/>Measure -->
														</td>
												</tr>

												<tr id="ikraID0" style="display: none;">
													<th align="right" valign="top">KRA</th>
													<!-- <td><input type="text" name="iKRA" id="iKRA" /></td> -->
													<td id="iKRAtdID0">
													<input type="hidden" name="iKRACount" id="iKRACount0" value="1"/>
													<p><input type="text" name="iKRA" id="iKRA" /><a href="javascript:void(0)" class="add_lvl"
							onclick="addKRA('i',0);">Add KRA</a></p>											
											</td>
												</tr>

												<tr id="imeasureID0" style="display: none;">
													<th align="right">Measure with</th>
													<td><select name="imkwith"
														onchange="addMeasureWith(this.value,'i',0)">
															<option value="">Select</option>
															<option value="$">$</option>
															<option value="Effort">Effort</option>
													</select>
													</td>
												</tr>
												<tr id="imkdollarAmtid0" style="display: none;">
													<th align="right">$</th>
													<td><input type="text" name="imeasurekraDollar" />
													</td>
												</tr>
												<tr id="imkEffortsid0" style="display: none;">
													<th align="right">Efforts</th>
													<td>Days&nbsp;<input type="text"
														name="imeasurekraEffortsDays" style="width: 40px;" />&nbsp;
														HRs&nbsp;<input type="text" name="imeasurekraEffortsHrs"
														style="width: 40px;" />
													</td>
												</tr>


												<tr>
													<th align="right">Due Date</th>
													<td><input type="text" name="igoalDueDate" id="igoalDueDate0" class="duedatepick" style="width: 70px;" />
													</td>
												</tr>
												<tr>
													<th align="right">Feedback</th>
													<td>
													<select name="igoalFeedback"
								onclick="addFeedback(this.value,'i',0);">
									<option value="Yes">Yes</option>
									<option value="No" selected="selected">No</option>
							</select>
													<!-- <input type="radio" name="igoalFeedback"
														value="Yes" onclick="addFeedback(this.value,'i',0);" />Yes
														<input type="radio" name="igoalFeedback" value="No"
														onclick="addFeedback(this.value,'i',0);" checked="checked" />No -->
														</td>
												</tr>
												<tr id="iOrientation0" style="display: none;">
													<th align="right">Orientation</th>
													<td><select name="iorientation">
															<%=request.getAttribute("orientation")%>
													</select></td>
												</tr>
												<tr>
													<th align="right">Weightage</th>
													<td><input type="text" name="igoalWeightage"
														value="100" style="width: 40px;" /></td>
												</tr>
												<tr>
													<th align="right">Assign Goal</th>
													<td>
														<div id="ilevelID0" style="float: left;">
															
															 <s:select name="ilevel" list="levelList" listKey="levelId"
                                         listValue="levelCodeName" headerKey="0"
                                        headerValue="All Level" 
                                         multiple="true"
                                        size="4" value="0"></s:select>
														</div>
														<div id="igradeID0" style="float: left;">
															
															 <s:select name="igrade" list="gradeList" listKey="gradeId"
                                            listValue="gradeCode" headerKey="0" 
                                            headerValue="All Grade"
                                            multiple="true"
                                            size="4" value="0"></s:select>
														</div>
														<div id="iempID0">
															
															
															<s:select name="iemp" list="empList" listKey="employeeId"
                                            listValue="employeeCode" headerKey="0" 
                                            headerValue="All Employee"
                                             multiple="true"
                                            size="4" value="0"></s:select>
														</div></td>
												</tr>

											</table>
										<li></li>
										<li><a href="javascript:void(0)" class="add_lvl"
											onclick="addIndividual('teamID0');">Add New Individual
												Goal</a>
										</li>
									</ul></li>
							</ul></li>
					</ul></li>
			</ul>
		</div>
		<div>
		<s:submit value="Save" cssClass="input_button" name="submit"></s:submit> 
					
					</div>
	</s:form>
</div>

