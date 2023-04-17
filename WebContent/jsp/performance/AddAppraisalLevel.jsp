
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<g:compress>
<style>
.ul_class li{ 
    margin: 10px 0px 10px 100px;
} 

.add1 {
    background-image: url("images1/add-item.png");
    background-position: right center;
    background-repeat: no-repeat;
    display: block;
    float: left;
    /* margin: 5px 0; */
    padding: 0 20px 0 0;
    text-decoration: none;
    text-indent: -9999px;
    width: 10px;
}
</style> 

<script type="text/javascript">
jQuery(document).ready(function(){
    // binds form submission and fields to the validation engine
    jQuery("#formID").validationEngine();
    
    
}); 
 
var opt='<%=request.getAttribute("option")%>';
var orientation = '<%=request.getAttribute("oreinted")%>';
var anstype='<%=request.getAttribute("anstype")%>';
var attribute='<%=request.getAttribute("attribute")%>';
var member = '<%=request.getAttribute("member")%>';


function isOnlyNumberKey(evt) {
	var charCode = (evt.which) ? evt.which : event.keyCode;
	if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
		return true; 
	}
	return false;
}


	function showSystem(value, divcount) {
		//alert("in showSystem"); 
		if (value == '1') {
			
			document.getElementById("mainDiv").style.display='block';
			document.getElementById("otherDiv").style.display='none';
			document.getElementById("otherDiv").innerHTML='';
			document.getElementById("scoreCardID" + divcount).style.display = 'table-row';
			
		} else if (value == '2') {
			
			questionCnt=0;
			document.getElementById("mainDiv").style.display='none';
			document.getElementById("otherDiv").style.display='block';
			document.getElementById("otherDiv").innerHTML=otherDivData();
			
			document.getElementById("mainDiv").innerHTML='';
			document.getElementById("scoreCardID" + divcount).style.display = 'none';

		} else {
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("otherDiv" + divcount).style.display = 'none';
			document.getElementById("scoreCardDiv" + divcount).style.display = "none";

		}

	}

	var scoreCnt = 0;
	var questionCnt = 0;
	var objectiveCnt = 0;
	var measureCnt = 0;
	var goalCnt=0;
	
	function getcontent() {
		scoreCnt = 0;
		questionCnt = 0;
		objectiveCnt = 0;
		measureCnt = 0;
		goalCnt=0;
		
		var scoreID='scoreID'+scoreCnt;
		var questionID='questionID'+questionCnt;
		var objectiveID='objectiveID'+objectiveCnt;
		var goalID='goalID'+goalCnt;
		var measureID='measureID'+measureCnt;

		
		var val = document.getElementById("scoreCard").value;
	 	if (val == '1') {

	 		var a = "<ul id=\""+scoreID+"\" class=\"level_list ul_class\"><li>" + getScoreData(scoreCnt) + "</li>"
			+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" >Add New Competency</a></li>"
			+ "<li><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\"/><ul id=\""+goalID+"\"><li>" + getGoalData('1') + "</li>"
			+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ scoreID + "');\">Add Goal</a></li>"
			
			+ "<li><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\"/><ul id=\""+objectiveID+"\"><li>" + getObjectiveData('1') + "</li>"
			+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addObjective('"+ goalID + "');\">Add Objective</a></li>"
			
			+ "<li><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" value=\"1\"/><ul id=\""+measureID+"\"><li>"
			+ getMeasureData('1') + "</li>"
			+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "');\">Add Measure</a></li>"
			+ "<li><ul id=\""+questionID+"\"><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
			+ getquestion('1') + "</li>"
			+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a></li>"
			+"</ul>"
			+ "</li></ul></li></ul></li></ul></li></ul></li></ul>      </li></ul>";
			
	
			
	document.getElementById("mainDiv").innerHTML = a; 
		} else 
			
			if (val == '2') {
				
			var a = "<ul id=\""+scoreID+"\" class=\"level_list ul_class\"><li>" + getScoreData(scoreCnt) + "</li>"
					+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" >Add New Competency</a></li>"
					+ "<li><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+scoreID+"\" value=\"1\"/><ul id=\""+measureID+"\"><li>"
					+ getMeasureData('1') + "</li>"+
					"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ scoreID + "');\">Add Measure</a></li>"+
					"<li>"
					+ "<ul id=\""+questionID+"\"><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
					+ getquestion('1') + "</li>"+
					"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a></li>"
					+"</ul>"
					+ "</li></ul></li></ul>";
							
			document.getElementById("mainDiv").innerHTML = a; 
			} 
		 else 
			
			if (val == '3') { 

			var a = "<ul id=\""+scoreID+"\" class=\"level_list ul_class\"><li>" + getScoreData(scoreCnt) + "</li>"
					+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" >Add New Competency</a></li>"
					+ "<li><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\"/><ul id=\""+goalID+"\"><li>" + getGoalData('1') + "</li>"
					+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ scoreID + "');\">Add Goal</a></li>"
					+ "<li><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+goalID+"\" value=\"1\"/><ul id=\""+measureID+"\"><li>"
					+ getMeasureData('1') + "</li>"
					+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ goalID + "');\">Add Measure</a></li>"
					+ "<li><ul id=\""+questionID+"\"><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
					+ getquestion('1') + "</li>"
					+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a></li>"
					+"</ul>"
					+ "</li></ul></li></ul></li></ul>     </li></ul>";
			document.getElementById("mainDiv").innerHTML = a;
		} else{
			document.getElementById("mainDiv").innerHTML ="";
		}

	}

	
 
	function addGoal(id) {
	 	goalCnt++;
		questionCnt++;
		measureCnt++;
		objectiveCnt++;
		var goalID='goalID'+goalCnt;
		var measureID='measureID'+measureCnt;
		var questionID='questionID'+questionCnt;
		var objectiveID='objectiveID'+objectiveCnt;
		var litag = document.createElement('li');
		litag.id = goalID;
		
var val=document.getElementById("goalcount"+id).value;
		
		document.getElementById("goalcount"+id).value=parseInt(val)+1;
		var val = document.getElementById("scoreCard").value;
		if (val == '1') {
			
			var a ="<ul><li>" + getGoalData(parseInt(val)+1) + "</li>"
			+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ id + "');\">Add Goal</a>"
			+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"deleteBlock('"+ goalID + "','"+ id + "','goalcount');\">Delete Goal</a>"

			+ "<li><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\"/><ul id=\""+objectiveID+"\"><li>" + getObjectiveData('1') + "</li>"
			+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addObjective('"+ goalID + "');\">Add Objective</a></li>"
			
			+ "<li><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" value=\"1\"/><ul id=\""+measureID+"\"><li>"
			+ getMeasureData('1') + "</li>"
			+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "');\">Add Measure</a></li>"
			+ "<li><ul id=\""+questionID+"\"><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
			+ getquestion('1') + "</li>"
			+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a></li>"
			+"</ul>"
			+ "</li></ul></li></ul></li></ul></li></ul></li></ul>";
			
			litag.innerHTML = a;
			document.getElementById(id).appendChild(litag);
			
		}else if (val == '3') {
		var a = "<ul><li>" + getGoalData(parseInt(val)+1) + "</li>"
		+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ id + "');\">Add Goal</a>"
		+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"deleteBlock('"+ goalID + "','"+ id + "','goalcount');\">Delete Goal</a>"
		+"</li>"
		+ "<li><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+goalID+"\" value=\"1\"/><ul id=\""+measureID+"\"><li>"
		+ getMeasureData('1') + "</li>"
		+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ goalID + "');\">Add Measure</a></li>"
		+ "<li><ul id=\""+questionID+"\"><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
		+ getquestion('1') + "</li>"
		+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a></li>"
		+"</ul>"
		+ "</li></ul></li></ul></li></ul>";
		litag.innerHTML = a;
		document.getElementById(id).appendChild(litag);
		}

}
	

	 function addObjective(id) {
		objectiveCnt++;
		
		questionCnt++;
		measureCnt++;
		var objectiveID='objectiveID'+objectiveCnt;
		var measureID='measureID'+measureCnt;
		var questionID='questionID'+questionCnt;
		var litag = document.createElement('li');
		litag.id = objectiveID;
		

		var val=document.getElementById("objectivecount"+id).value;
		
		document.getElementById("objectivecount"+id).value=parseInt(val)+1;
		
		
		var a = "<ul><li>" + getObjectiveData(parseInt(val)+1) + "</li>"
		+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addObjective('"+ id + "');\">Add Objective</a>"
		+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"deleteBlock('"+ objectiveID + "','"+ id + "','objectivecount');\">Delete Objective</a>"
		+"</li>"
		+ "<li><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" value=\"1\"/><ul id=\""+measureID+"\"><li>"
		+ getMeasureData('1') + "</li>"
		+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "');\">Add Measure</a></li>"
		+ "<li><ul id=\""+questionID+"\"><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
		+ getquestion('1') + "</li>"
		+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a></li>"
		+"</ul>"
		+ "</li></ul></li></ul></li></ul>";

		litag.innerHTML = a;
		document.getElementById(id).appendChild(litag);
	} 
	
	 function addScoreCard(){
		scoreCnt ++;
		questionCnt++;
		measureCnt++;
		goalCnt++;
		objectiveCnt++;
		var measureID='measureID'+measureCnt;
		var questionID='questionID'+questionCnt;
		var scoreID='scoreID'+scoreCnt;
		var goalID='goalID'+goalCnt;
		var objectiveID='objectiveID'+objectiveCnt;
		var litag = document.createElement('ul');
		litag.setAttribute("class","level_list ul_class");
		litag.id = scoreID;
		var val = document.getElementById("scoreCard").value;

		 if (val == '1') {
			 var a = "<li>" + getScoreData(scoreCnt) + "</li>"
				+"<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" addScoreCard()\" >Add New Competency</a>"
				+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" >Delete Competency</a></li>"
				+ "<li><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\"/><ul id=\""+goalID+"\"><li>" + getGoalData('1') + "</li>"
				+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ scoreID + "');\">Add Goal</a></li>"
				
				+ "<li><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\"/><ul id=\""+objectiveID+"\"><li>" + getObjectiveData('1') + "</li>"
				+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addObjective('"+ goalID + "');\">Add Objective</a></li>"
				
				+ "<li><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" value=\"1\"/><ul id=\""+measureID+"\"><li>"
				+ getMeasureData('1') + "</li>"
				+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "');\">Add Measure</a></li>"
				+ "<li><ul id=\""+questionID+"\"><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
				+ getquestion('1') + "</li>"
				+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a></li>"
				+"</ul>"
				+ "</li></ul></li></ul></li></ul></li></ul></li></ul>      </li>";
			 litag.innerHTML = a;
				document.getElementById("mainDiv").appendChild(litag);
				
		}else 
			
			if (val == '2') {
			

			var a = "<li>" + getScoreData(scoreCnt) + "</li>"
			+"<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" addScoreCard()\" >Add New Competency</a>"
			+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" >Delete Competency</a></li>"
			+ "<li><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+scoreID+"\" value=\"1\" /><ul id=\""+measureID+"\"><li>"
			+ getMeasureData('1') + "</li>"+
			"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"
			+ scoreID + "');\">Add Measure</a>"+
			"<li>"
			+ "<ul id=\""+questionID+"\"><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
			+ getquestion('1') + "</li>"+
			"<li>"
			+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a>"
			+"</li></ul>"
			+ "</li></ul></li>";
			litag.innerHTML = a;
			document.getElementById("mainDiv").appendChild(litag);

		}
		 
		 else if (val == '3') {
			 var a = "<li>" + getScoreData(scoreCnt) + "</li>"
				+"<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" addScoreCard()\" >Add New Competency</a>"
				+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" >Delete Competency</a></li>"
				+ "<li><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\"/><ul id=\""+goalID+"\"><li>" + getGoalData('1') + "</li>"
				+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ scoreID + "');\">Add Goal</a></li>"
				+ "<li><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+goalID+"\" value=\"1\"/><ul id=\""+measureID+"\"><li>"
				+ getMeasureData('1') + "</li>"
				+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ goalID + "');\">Add Measure</a></li>"
				+ "<li><ul id=\""+questionID+"\"><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
				+ getquestion('1') + "</li>"
				+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a></li>"
				+"</ul>"
				+ "</li></ul></li></ul></li></ul>     </li>";
			 litag.innerHTML = a;
				document.getElementById("mainDiv").appendChild(litag);

		}
 	}


	 function addQuestions(id) {
		var litag = document.createElement('li');
		questionCnt++;
		var QuestionID = "QuestionID" + questionCnt;
		
		var val=document.getElementById("questioncount"+id).value;
		
		document.getElementById("questioncount"+id).value=parseInt(val)+1;
		
		litag.id = QuestionID;

		var a = "<ul><li>" + getquestion(parseInt(val)+1) + "</li></ul>"
		+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ id+ "');\">Add Question</a>"
		+"&nbsp;&nbsp;<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"deleteBlock('"+QuestionID+"','"+ id+ "','questioncount');\">Remove Question</a>";
		
		litag.innerHTML = a;
		document.getElementById(id).appendChild(litag);

	}

	

	 function addMeasure(id) {
		
		questionCnt++;
		measureCnt++;
		var measureID='measureID'+measureCnt;
		var questionID='questionID'+questionCnt;

		var litag = document.createElement('li');

		
		litag.id = measureID;
		
		var val=document.getElementById("measurecount"+id).value;
		
		document.getElementById("measurecount"+id).value=parseInt(val)+1;
		
		var a = "<ul>"
				+ "<li>"
				+ getMeasureData(parseInt(val)+1)
				+ "</li><li><a href=\"javascript:void(0)\" class=\"add_lvl\"  onclick=\"addMeasure('"
				+ id + "'"
				+ ")\"  >Add Measure</a>"+
				"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"deleteBlock('"
				+ measureID + "','" +id 
				+ "','measurecount')\"  >remove Measure</a></li><li><ul><li><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
				+ getquestion('1') + "</li>" +
				"<li>"
				+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "');\">Add Question</a>"
				+"</li></ul>"+
				"</li></ul>";

		//alert(a);

		litag.innerHTML = a;
		document.getElementById(id).appendChild(litag);

	} 
	 

	function deleteBlock(childId, parentIds,type){
		
	
		//var a=document.getElementById(type+parentIds).value;
		//document.getElementById(type+parentIds).value=parseInt(a)-1;
			//alert("type AAL===>> " + type);
			var row_skill = document.getElementById(childId); 
			document.getElementById(parentIds).removeChild(row_skill);
	}
	

	 function deleteBlock1(childId, parentIds){
		
			var row_skill = document.getElementById(childId); 
			document.getElementById(parentIds).removeChild(row_skill);
	}
	
	function deleteMeasure(id, ids) {

		var row_skill = document.getElementById(ids);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);

		}
	}

	function deleteQuestion(ids) {
		var row_skill = document.getElementById(ids);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);

		}
	}
	
	

	
	
function getquestion(val) {
		
		var cnt=questionCnt;
		
		
		var selec = getOrientationData(cnt);
		

		var a = "<table class=\"tb_style\" width=\"100%\">"
			+ "<tr><th style=\"width: 4%;\">"+val+"</th><th width=\"17%\" style=\"text-align: right;\">Select Question</th>"
			+ "<td colspan=\"3\"><select name=\"questionSelect\" onchange=\"addNewQuestion('addNewQuestionId"+ cnt+ "',this.value,'"+ cnt+ "')\" >"
			+"<option value=\"\">Select Question</option>"+ opt+ "</select>"
			
			+ "</td>"
			+ "</tr>"
			+ "<tr><th></th><th style=\"text-align: right;\">Weightage in(%)<input type=\"hidden\" name=\"orientt\" value=\""+cnt+"\" /></td><td colspan=\"3\"><input type=\"text\" name=\"weightage\" value=\"100\" /> </td></tr>"

			+ "<tr id=\"QuestionName"+cnt+"\" style=\"display:none\"><th></th><th style=\"text-align: right;\">Question Name:</th><td colspan=\"3\"><input type=\"text\" name=\"question\" /></td></tr>"
			+ "<tr id=\"AddQuestion"+cnt+"\" style=\"display:none\"><th></th><th style=\"text-align: right;\">Add to Question Bank:</th>"
			+"<td colspan=\"3\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" onclick=\"changeStatus('"+ cnt+ "')\" />"
			+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/> </td></tr>"
			+ "<tr id=\"selectanstype"+cnt+"\" style=\"display:none\"><th></th><th style=\"text-align: right;\">Select Answer Type</th>"
			
			+ "<td colspan=\"3\"><select name=\"ansType"+ cnt+ "\" onchange=\"changeNewQuestionType(this.value,'answerType"+ cnt+ "','answerType1"+ cnt+ "','answerType2"+ cnt+ "','"+ cnt+ "')\" value=\"1\">"
			+anstype
			+"</select>" 
			+ "<input type=\"hidden\" name=\"questiontypename\" value=\""+ cnt+"\" /></td></tr>"
			
			
			+ "<tr id=\"answerType"+cnt+"\" style=\"display:none\"><th></th><th style=\"text-align: right;\">&nbsp;</th><td>a)<input type=\"text\" name=\"optiona\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb\"  /><input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
			+ "<tr id=\"answerType1"+cnt+"\" style=\"display:none\"><th></th><th style=\"text-align: right;\">&nbsp;</th><td>c)<input type=\"text\" name=\"optionc\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond\" /><input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>"
			+ "<tr id=\"answerType2"+cnt+"\" style=\"display:none\"><th></th><td>&nbsp;</td><td>&nbsp;</td><td colspan=\"2\">&nbsp;</td></tr>"+
			+ selec + "</table>";

		return a;
	}

	function getScoreData(count) {
		count++;
		
		var a = "<table class=\"tb_style\" width=\"100%\"><tr><th style=\"width: 4%;\">"+count+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Competency</td></tr>"
				+ "<tr><th></th><th style=\"text-align: right;\">Section name</th><td><input type=\"text\" name=\"scoreSectionName\"	 /></td></tr>"
				+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"scoreCardDescription\"	/></td></tr>"
				+ "<tr><th></th><th style=\"text-align: right;\">Weightage</th><td><input type=\"text\" name=\"scoreCardWeightage\"	 value=\"100\" /></td></td></tr>"
				+"<tr><th></th><th style=\"text-align: right;\">Select Attribute</th><td><select name=\"attribute\">"+attribute+"</select></td></tr>"
				+"</table>";
		return a;
	}

	 function getObjectiveData(val) {
		var a = "<table class=\"tb_style\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Objective"
				+ "</td></tr>"
				+ "<tr><th></th><th style=\"text-align: right;\">Section name</th><td><input type=\"text\" name=\"objectiveSectionName\"  /></td></tr>"
				+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"objectiveDescription\"  /></td></tr>"
				+ "<tr><th></th><th style=\"text-align: right;\">Weightage</th><td><input type=\"text\" name=\"objectiveWeightage\"  value=\"100\" /></td></tr></table>";
		return a;
	} 

	function getMeasureData(val) {

		var a = "<table class=\"tb_style\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Measures</td></tr>"
				+ "<tr><th></th><th style=\"text-align: right;\">Section name</th><td><input type=\"text\" name=\"measuresSectionName\"  /></td></tr>"
				+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"measuresDescription\"  /></td></tr>"
				+ "<tr><th></th><th style=\"text-align: right;\">Weightage</th><td><input type=\"text\" name=\"measureWeightage\"  value=\"100\" /></td></tr>"
				+ "</table>";
				
		return a;
	}

	 function getGoalData(val) {
			var a = "<table class=\"tb_style\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th>"
					+ "<td>Goals</td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Section name</th><td><input type=\"text\" name=\"goalSectionName\"  /></td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"goalDescription\"  /></td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Weightage</th><td><input type=\"text\" name=\"goalWeightage\"  value=\"100\" /></td>"
					+ "</tr></table>";
			return a;
		}
	 
	function removeID(id) {
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);

		}
	}
	
	function addNewQuestion(id, val, cnt1) {

		if (val == '0') {
			//cnt++;

			document.getElementById("QuestionName" + cnt1).style.display = 'table-row';
			document.getElementById("AddQuestion" + cnt1).style.display = 'table-row';
			document.getElementById("selectanstype" + cnt1).style.display = 'table-row';
			document.getElementById("answerType" + cnt1).style.display = 'table-row';
			document.getElementById("answerType1" + cnt1).style.display = 'table-row';
			if(document.getElementById("answerType2" + cnt1)) {
				document.getElementById("answerType2" + cnt1).style.display = 'table-row';
			}

		} else {

			document.getElementById("QuestionName" + cnt1).style.display = 'none';
			document.getElementById("AddQuestion" + cnt1).style.display = 'none';
			document.getElementById("selectanstype" + cnt1).style.display = 'none';
			document.getElementById("answerType" + cnt1).style.display = 'none';
			document.getElementById("answerType1" + cnt1).style.display = 'none';
			if(document.getElementById("answerType2" + cnt1)) {
				document.getElementById("answerType2" + cnt1).style.display = 'none';
			}
		}
	}

	function changeNewQuestionType(val, id, id1, id2, cnt) {

		 if (val == 1 || val == 2 || val == 8) {
			addQuestionType1(id,cnt);
			document.getElementById(id).style.display = 'table-row';

			addQuestionType2(id1,cnt);
			document.getElementById(id1).style.display = 'table-row';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';
			
		} else if (val == 9) {
			addQuestionType3(id,cnt);
			document.getElementById(id).style.display = 'table-row';

			addQuestionType4(id1,cnt);
			document.getElementById(id1).style.display = 'table-row';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';

		 }else if (val == 6) {
			addTrueFalseType(id,cnt);
			document.getElementById(id).style.display = 'table-row';
			document.getElementById(id1).innerHTML ="";
			document.getElementById(id1).style.display = 'none';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';

		}else if (val == 5) {
			addYesNoType(id,cnt);
			document.getElementById(id).style.display = 'table-row';
			document.getElementById(id1).innerHTML ="";
			document.getElementById(id1).style.display = 'none';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';
 
		} else if (val == 13) {
			addQuestionType1_1(id, cnt);
			addQuestionType2_1(id1, cnt);
			addQuestionType5_1(id2, cnt);
			document.getElementById(id).style.display = 'table-row';
			document.getElementById(id1).style.display = 'table-row';
			document.getElementById(id2).style.display = 'table-row';
		} else {
			addQuestionType1(id,cnt);
			addQuestionType2(id1,cnt);
			document.getElementById(id).style.display = 'none';
			document.getElementById(id1).style.display = 'none';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';
		}

	}
	 
	function addTrueFalseType(id,cnt){
		document.getElementById(id).innerHTML = "<th></th><td></td><td></td><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True"
		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
	}
	
	function addYesNoType(id,cnt){
		document.getElementById(id).innerHTML = "<th></th><td ></td><td ></td><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">yes"
		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
	} 
	function addQuestionType1(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><td></td><td>a)<input type=\"text\" name=\"optiona\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb\" /><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
	}
	function addQuestionType2(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><td ></td><td>d)<input type=\"text\" name=\"optionc\"  /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
	}
	function addQuestionType3(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><td ></td><td>a)<input type=\"text\" name=\"optiona\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
	}
	function addQuestionType4(id1,cnt) {
		document.getElementById(id1).innerHTML = "<th></th><td></td><td>d)<input type=\"text\" name=\"optionc\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
	}
	
	function addQuestionType1_1(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiona\" id=\"rateoptiona"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
		+"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionb\" id=\"rateoptionb"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
	}
	function addQuestionType2_1(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionc\" id=\"rateoptionc"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
		+"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiond\" id=\"rateoptiond"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
	}
	function addQuestionType5_1(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>e)&nbsp;<input type=\"text\" name=\"optione\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptione\" id=\"rateoptione"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td><td colspan=\"2\">&nbsp;</td>";
	}
	
	
	function changeStatus(id) {
		if (document.getElementById('addFlag' + id).checked == true) {
			document.getElementById('status' + id).value = '1';
		} else {
			document.getElementById('status' + id).value = '0';
		}
	}
	
	
	function getcheckWeightage(value){
		 
        if(value=='1'){
        	for(var i=0;i<=questionCnt;i++){
        		
        		if(document.getElementById("weightageTr"+i)!=null){
                document.getElementById("weightageTr"+i).style.display = 'table-row';
        		}
        	}
        	
        }else if(value=='0'){
        	
for(var i=0;i<=questionCnt;i++){
        		
        		if(document.getElementById("weightageTr"+i)!=null){
                document.getElementById("weightageTr"+i).style.display = 'none';
        		}
        	}
        	
        	
        
        }
		document.getElementById("hidecheckWeightage").value=value;
	}
	/*  function setOtherAnsType(value){
		if(value=='1'){
			document.getElementById("otherAnsType").value=value;
			document.getElementById("spanAnsType").innerHTML="With Remark";
		}else if(value=='2'){
			document.getElementById("otherAnsType").value=value;
			document.getElementById("spanAnsType").innerHTML="Without Remark";
		}else{
			document.getElementById("otherAnsType").value='';
			document.getElementById("spanAnsType").innerHTML='';
		}
	}  */
	
  function otherDivData(){
		
		//<option value=\"\">Select Type</option>
		var a="<ul class=\"level_list ul_class\"><li><table class=\"tb_style\" width=\"100%\"><tr><th width=\"17%\" nowrap=\"nowrap\">Question Type</th><td><select name=\"otherQuestionType\">"
		+"<option value=\"Without Short Description\">Without Short Description</option>"
		+"<option value=\"With Short Description\">With Short Description</option>"
		+"</select>"
		+"</td></tr>"
		+"<tr><th>Select Attribute</th><td><select name=\"attribute\">"+attribute+"</select></td></tr>"

		+"<tr><th>Weightage</th><td><input type=\"radio\" name=\"checkWeightage\" checked=\"checked\" id=\"checkWeightage\" value=\"1\" onclick=\"getcheckWeightage(this.value);\"/>Yes"
		+"<input type=\"radio\" name=\"checkWeightage\" id=\"checkWeightage\" value=\"0\" onclick=\"getcheckWeightage(this.value);\"/>no"
		+"<input type=\"hidden\" name=\"hidecheckWeightage\" id=\"hidecheckWeightage\" /></td></tr></table></li>"
		+"<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"getOtherquestion();\">Add Appraisal Questions</a></li>"
		+"<li id=\"otherQuestionLi\"></li></ul>";
	
		return a;
	
	} 
	
function getOtherquestion() {
		
		questionCnt++;
		var cnt=questionCnt;
		var selec = getOrientationData(cnt);

		
		var ultag = document.createElement('ul');
	
		ultag.id = "otherQuestionUl"+cnt;
		
		 var a = "<li><table class=\"tb_style\" width=\"100%\">"
				+ "<tr><th>"+questionCnt+"</th><th width=\"17%\" style=\"text-align: right;\">Select Question</td>"
				+ "<td colspan=\"3\"><select name=\"questionSelect\" onchange=\"addNewQuestion('addNewQuestionId"+ cnt+ "',this.value,'"+ cnt+ "')\" >"
				+"<option value=\"\">Select Question</option>"+ opt+ "</select>"
				
				+"<a href=\"javascript:void(0)\" onclick=\"getOtherquestion()\" > Add Question</a> <a href=\"javascript:void(0)\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\" > Remove Question</a></td>"
				+ "</tr>"
				+"<tr><th></th><th style=\"text-align: right;\">Short Description</th><td colspan=\"3\"><input type=\"text\" name=\"otherSDescription\" id=\"otherSDescription\" /></td></tr>"
				+ "<tr id=\"weightageTr"+cnt+"\"><th></th><th style=\"text-align: right;\">Weightage in(%)<input type=\"hidden\" name=\"orientt\" value=\""+cnt+"\" /></td><td colspan=\"3\"><input type=\"text\" name=\"weightage\" id=\"weightage"+cnt+"\" value=\"100\" /> </td></tr>"

				+ "<tr id=\"QuestionName"+cnt+"\" style=\"display:none\"><th></th><th style=\"text-align: right;\">Question Name:</th><td colspan=\"3\"><input type=\"text\" name=\"question\" /></td></tr>"
				+ "<tr id=\"AddQuestion"+cnt+"\" style=\"display:none\"><th></th><th style=\"text-align: right;\">Add to Question Bank:</th>"
				+"<td colspan=\"3\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" onclick=\"changeStatus('"+ cnt+ "')\" />"
				+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/> </td></tr>"
				+ "<tr id=\"selectanstype"+cnt+"\" style=\"display:none\"><th></th><th style=\"text-align: right;\">Select Answer Type</th>"
				
				+ "<td colspan=\"3\"><select name=\"ansType"+ cnt+ "\" onchange=\"changeNewQuestionType(this.value,'answerType"+ cnt+ "','answerType1"+ cnt+ "','answerType2"+ cnt+ "','"+ cnt+ "')\" value=\"1\">"
				+anstype
				+"</select>" 
				+ "<input type=\"hidden\" name=\"questiontypename\" value=\""+ cnt+"\" /></td></tr>"
				
				
				+ "<tr id=\"answerType"+cnt+"\" style=\"display:none\"><th></th><td>&nbsp;</td><td><input type=\"text\" name=\"optiona\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\"><input type=\"text\" name=\"optionb\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
				+ "<tr id=\"answerType1"+cnt+"\" style=\"display:none\"><th></th><td>&nbsp;</td><td><input type=\"text\" name=\"optionc\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\"><input type=\"text\" name=\"optiond\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>"+
				+ "<tr id=\"answerType2"+cnt+"\" style=\"display:none\"><th></th><td>&nbsp;</td><td>&nbsp;</td><td colspan=\"2\">&nbsp;</td></tr>"+
				selec+
				"</table></li>";

		ultag.innerHTML = a;
		document.getElementById("otherQuestionLi").appendChild(ultag); 	
		
		var spanAnsType=document.getElementById("answerTypeSelect").value;
		if(spanAnsType=='1'){
			document.getElementById("spanAnsType"+cnt).innerHTML="With Remark";
		}else if(spanAnsType=='2'){
			document.getElementById("spanAnsType"+cnt).innerHTML="Without Remark";
		}
		var checkWeightage=document.getElementById("hidecheckWeightage").value;
		
		
		if(checkWeightage=='1'){ 
			document.getElementById("weightageTr"+cnt).style.display = 'table-row';
		}else if(checkWeightage=='0'){
			document.getElementById("weightageTr"+cnt).style.display = 'none';
		}
	}
	
	function removeOtherquestion(id){
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);

		}
	}
	
	function getOrientationData(cnt){
		
		var memberArray=member.split(",");
		var selec="";
		for(var i=0;i<memberArray.length;i++){
			
			selec += "<tr><th></th><td>"+memberArray[i]+"</td><td colspan=\"3\"><input type=\"radio\" name=\""+memberArray[i]+cnt+"\" value=\"1\" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\" name=\""+memberArray[i]+cnt+"\" value=\"0\" /></td></tr>";

		}
		return selec;
	}
</script>


</g:compress>

<script type="text/javascript">
	$(function() {
		var a = '#from';
		$("#from").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("#to").datepicker({
			format : 'dd/mm/yyyy'
		}); 
	});

	
	jQuery(document).ready(function() {
		
		//jQuery("#frmClockEntries").validationEngine();
			
		  jQuery(".content1").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".heading_dash").click(function()
		  {
		    jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("close_div"); 
		  });
		});
		
function mover(id,id1){
    document.getElementById(id).style.display="block";
    document.getElementById(id1).style.display="block";
}
function mout(id,id1) {
    document.getElementById(id).style.display="none";
    document.getElementById(id1).style.display="none";
}
		
		
		
</script>

<%
	List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
	Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
	
	List<String> mainLevelList =(List<String>)request.getAttribute("mainLevelList");
	
	Map<String, List<List<String>>> hmKRA1 =(Map<String, List<List<String>>>)request.getAttribute("hmKRA1");
	Map<String, List<List<String>>> hmKRA =(Map<String, List<List<String>>>)request.getAttribute("hmKRA");
	Map<String, String> hmMesures = (Map<String, String>) request.getAttribute("hmMesures");
%>

	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Add New Level" name="title" />
	</jsp:include>


<div class="leftbox reportWidth">

	<s:form action="AddAppraisalLevel" id="formID" method="POST" theme="simple">
		
<h2>Appraisal</h2>
<br/>
<div style="float: left; width: 100%;">

		<table class="tb_style" width="98%">
			<tr>
				<th width="15%" align="right">Appraisal Name</th>
				<td colspan="1"><b><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=appraisalList.get(5)%>&nbsp;&nbsp;<%=appraisalList.get(9)%>&nbsp;&nbsp;<%=appraisalList.get(8)%></b>
				</td>
			</tr>
			<tr>
				<th align="right">Appraisal Type</th>
				<td><%=appraisalList.get(14)%></td>
			</tr>
			<tr>	
				<th align="right">Appraisal Frequency</th>
				<td><%=appraisalList.get(7)%></td>
			</tr>
			
			<tr>
				<th align="right">Orientation</th>
				<td colspan="1"><%=appraisalList.get(2)%> </td>
			</tr>
			<tr>
				<th valign="top" align="right">Appraisee</th>
				<td colspan="1"><%=appraisalList.get(12)%></td>
			</tr>
			<tr>
				<th valign="top" align="right">Description</th>
				<td colspan="1"><%=appraisalList.get(15)%></td>
			</tr>
		</table>
		</div>
		<br/><br/><br/>
		
		<%
			//List<List<String>> outerList1 = (List<List<String>>) request.getAttribute("outerList1");
			List<List<String>> mainLevelList1 = (List<List<String>>) request.getAttribute("mainLevelList1");
			System.out.println("mainLevelList1 ==============>" + mainLevelList1);
			Map<String, List<List<String>>> hmSystemLevelMp =(Map<String, List<List<String>>>)request.getAttribute("hmSystemLevelMp");
			Map<String, List<Map<String, List<List<String>>>>> levelMp = (Map<String, List<Map<String, List<List<String>>>>>) request.getAttribute("levelMp");
				UtilityFunctions uF = new UtilityFunctions();
		%>
		<%
				int z=0;
		
				for (int a = 0; mainLevelList1 != null && a < mainLevelList1.size(); a++) {
						List<String> maininnerList = mainLevelList1.get(a);
			%>
		<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 98%;  text-align: left; margin-top:50px;">
			<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
			<%=maininnerList.get(1)%> 
				<%-- <a style="height: 2px; width: 2px;  float: right;" href="AddAppraisalLevel.action?id=<%=appraisalList.get(0) %>&MLID=<%=maininnerList.get(0) %>&type=system" class="add" title="Add New System">Add</a> --%>
				<p style="font-weight: normal; font-size: 10px;">
				<%=uF.showData(maininnerList.get(2), "")%>
				</p>
			</div>
			
		<div class="content1">
			<%
			System.out.println("hmSystemLevelMp.get(maininnerList.get(0))=====>"+hmSystemLevelMp.get(maininnerList.get(0)));
			List<List<String>> outerList1 =hmSystemLevelMp.get(maininnerList.get(0));
				for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
						List<String> innerList1 = outerList1.get(i);
						if (uF.parseToInt(innerList1.get(3)) == 2) {
							System.out.println("in 2 levelMp.get(innerList1.get(0))=====>"+levelMp.get(innerList1.get(0)));
							List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
							Map<String, List<List<String>>> scoreMp = list.get(0);
			%>

			

			<!-- <div class="content1"> -->
			<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<blockquote><strong>System Type - </strong>Other</blockquote>
				</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
				
					<table class="tb_style" style="width: 100%; float: left;">
						<tr>
							<td width="90%"><b>Question</b></td>
							<td><b>Weightage</b></td>
							<!-- <th>Description</th> -->
						</tr> 
						<%
							List<List<String>> goalList = scoreMp.get(innerList1.get(0));
										for (int k = 0; goalList != null && k < goalList.size(); k++) {
											List<String> goalinnerList = goalList.get(k);
											z++;
						%>
						<tr>
							<%-- <td onmouseover="mover('addnew<%=z %>','editexist<%=z %>');" onmouseout="mout('addnew<%=z %>','editexist<%=z %>');"> --%>
							<td>
							<span style="float: left;"> <%=k+1%>)&nbsp;<%=goalinnerList.get(0)%></span>
							<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList1.get(0) %>&type=quest';" title="Add New">Add</a></span>
							<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=goalinnerList.get(3) %>&type=quest';" title="Edit Exist">Edit</a></span>
							
							</td>							
							<td style="text-align: right"><%=goalinnerList.get(1)%>%</td> 
						</tr>

						<%
							}
						%>
					</table>
				
			</div>
			</div>
			<!-- </div> -->

			<%
				} else if (uF.parseToInt(innerList1.get(3)) == 1){
							if (uF.parseToInt(innerList1.get(2)) == 1) {
								System.out.println("in 1 1 levelMp.get(innerList1.get(0))=====>"+levelMp.get(innerList1.get(0)));
								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
								Map<String, List<List<String>>> scoreMp = list.get(0);
								Map<String, List<List<String>>> measureMp = list.get(1);
								Map<String, List<List<String>>> questionMp = list.get(2);
								Map<String, List<List<String>>> GoalMp = list.get(3);
								Map<String, List<List<String>>> objectiveMp = list.get(4);
			%>

			<%--<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
				 <div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
				<%=innerList1.get(1)%>
				<p style="font-weight: normal; font-size: 10px;">
				<%=innerList1.get(4)%>
				</p>
				</div> --%>
				
				<!-- <div class="content1"> -->
				<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<blockquote><strong>System Type - </strong>Competencies + Goals + Objectives + Measures</blockquote>
				</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<!-- <b>Score Card</b> -->
				<b>Competencies</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<b>Goal </b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<b>Objective </b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;">
				<b>Measure</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 6.9%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14.5%;  text-align: center;">
				<b>Question</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 6.9%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<!-- <div style="overflow: hidden; float: left; width: 100%;"> -->
				<%
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
									for (int j = 0; scoreList != null && j < scoreList.size(); j++) {

										List<String> innerList = scoreList.get(j);
										z++;
				%>

				<div style="overflow: hidden; float: left; width: 100%;">
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.2%;">
						<%-- <p style="padding-left:10px" onmouseover="mover('addnew<%=z %>','editexist<%=z %>');" onmouseout="mout('addnew<%=z %>','editexist<%=z %>');"> --%>
						<p>
						<span style="float: left;"> <%=innerList.get(1)%></span>
						<span> <a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList1.get(0) %>&type=score';" title="Add New">Add</a></span>
						<span> <a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" 
						onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=innerList.get(0) %>&type=score';" title="Edit Exist">Edit</a></span>
						</p>					
						
					</div>
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 7.1%; text-align: right;">
						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
					</div>

					<div
						style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 78.6%;">
						<%
							List<List<String>> goalList = GoalMp.get(innerList.get(0));
												for (int k = 0; goalList != null && k < goalList.size(); k++) {
													List<String> goalinnerList = goalList.get(k);
													z++;
						%>



						<div style="overflow: hidden; float: left; width: 100%;">
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 18%;">
								
									<p>
									<span style="float: left;"><%=goalinnerList.get(1)%></span>
									<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList.get(0) %>&type=goal';" title="Add New">Add</a></span>
									<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=goalinnerList.get(0) %>&type=goal';" title="Edit Exist">Edit</a></span>
									</p>
								
							</div>
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 9.1%; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
							</div>

							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 72.7%;">
								<%
									List<List<String>> objectiveList = objectiveMp.get(goalinnerList.get(0));
															for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
																List<String> objectivelinnerList = objectiveList.get(l);
																z++;
								%>


								<div style="overflow: hidden; float: left; width: 100%;">
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24.6%;"> 
										<p>
											<span style="float: left;"><%=objectivelinnerList.get(1)%></span>
											<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=goalinnerList.get(0) %>&type=objective';" title="Add New">Add</a></span>
											<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=objectivelinnerList.get(0) %>&type=objective';" title="Edit Exist">Edit</a></span>
										</p>										
									</div>
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 12.5%; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=objectivelinnerList.get(2)%>%</p>
									</div>
									<div
										style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 62.7%;">
										<%
											List<List<String>> measureList = measureMp.get(objectivelinnerList.get(0));
																		for (int m = 0; measureList != null && m < measureList.size(); m++) {
																			List<String> measureinnerList = measureList.get(m);
																			z++;
										%>


										<div style="overflow: hidden; float: left; width: 100%;">
											<div
												style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 19.5%;">
												<p>
													<span style="float: left;"><%=measureinnerList.get(1)%></span>
													<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=objectivelinnerList.get(0) %>&type=measure';" title="Add New">Add</a></span>
												 	<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=measureinnerList.get(0) %>&type=measure';" title="Edit Exist">Edit</a></span>
												</p>
											</div>

											<div
												style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 19.5%; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
											</div>

											<div style="overflow: hidden; float: left; width: 60.6%;">
												<%
													List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
																					for (int n = 0; questionList != null && n < questionList.size(); n++) {
																						List<String> question1List = questionList.get(n);
																						z++;
												%>
												<div style="overflow: hidden; float: left; width: 100%;">
													<div
														style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 67%;">
															<p>
														 		<span style="float: left;"><%=question1List.get(0)%></span>
														 		<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=measureinnerList.get(0) %>&type=quest';" title="Add New">Add</a></span>
																<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=question1List.get(3) %>&type=quest';" title="Edit Exist">Edit</a></span>
															</p>
													</div>
													<div
														style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 32.2%; text-align: right;">
														<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
													</div>
													
												</div>
												<%
													}
												%>
											</div>
										</div>
										<%
											}
										%>
									</div>
								</div>

								<%
									}
								%>
							</div>
						</div>
						<%
							}
						%>					
				</div>
				</div>				
				<%
					}
				%>
			</div>
			<!-- </div> -->

			<%
				} else if (uF.parseToInt(innerList1.get(2)) == 2) {
					System.out.println("in 1 2 levelMp.get(innerList1.get(0))=====>"+levelMp.get(innerList1.get(0)));
								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
								Map<String, List<List<String>>> scoreMp = list.get(0);
								Map<String, List<List<String>>> measureMp = list.get(1);
								Map<String, List<List<String>>> questionMp = list.get(2);
			%>
			<%--<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
				 <div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
				
				<%=innerList1.get(1)%>
				<p style="font-weight: normal; font-size: 10px;">
				<%=innerList1.get(4)%>
				</p>
			</div> --%>

			<!-- <div class="content1"> -->
				<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<blockquote><strong>System Type - </strong>Competencies + Measures</blockquote>
				</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 22.9%;  text-align: center;">
				<!-- <b>Score Card</b> -->
				<b>Competencies</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 13.3%;  text-align: center;">
				<b>Measure</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 13.3%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 22.9%;  text-align: center;">
				<b>Question</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 12.6%;  text-align: center;">
				<b>Question Weightage</b>
			</div>

			<div style="overflow: hidden; float: left; width: 100%;">
				<%
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
									for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
										List<String> innerList = scoreList.get(j);
										z++;
				%>

				<div style="overflow: hidden; float: left; width: 100%;">
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 23.1%;">
							<p>
							<span style="float: left;"><%=innerList.get(1)%></span>
							<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList1.get(0) %>&type=score';" title="Add New">Add</a></span>
							<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=innerList.get(0) %>&type=score';" title="Edit Exist">Edit</a></span>							
							</p>
					</div>
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.1%; text-align: right;">

						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>

					</div>

					<div
						style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 62.7%;">
						<%
							List<List<String>> measureList = measureMp.get(innerList.get(0));
												for (int k = 0; measureList != null && k < measureList.size(); k++) {
													List<String> measureinnerList = measureList.get(k);
													z++;
						%>

						<div
							style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 21.3%;">
								<p>
									<span style="float: left;"><%=measureinnerList.get(1)%></span>
									<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList.get(0) %>&type=measure';" title="Add New">Add</a></span>
									<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=measureinnerList.get(0) %>&type=measure';" title="Edit Exist">Edit</a></span>
								</p>
						</div>

						<div
							style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 21.3%; text-align: right;">
							<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
						</div>

						<div
							style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 57.1%;">

							<%
								List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
														for (int l = 0; questionList != null && l < questionList.size(); l++) {
															List<String> question1List = questionList.get(l);
															z++;
							%>
							<div style="overflow: hidden; float: left; width: 100%;">
								<div
									style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 64.2%;">
										<p>
										<span style="float: left;"><%=question1List.get(0)%></span>
										<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=measureinnerList.get(0) %>&type=quest';" title="Add New">Add</a></span>
										<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=question1List.get(3) %>&type=quest';" title="Edit Exist">Edit</a></span>
										</p>
								</div>
								<div
									style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 35.4%; text-align: right;">
									<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
								</div>
							</div>
							<%
								}
							%>
						</div>

						<%
							}
						%>
					</div>
				</div>
				</div>
				<%
					}
				%>

			</div>
			<!-- </div> -->

			<%
				} else {
					System.out.println("in 1 3 levelMp.get(innerList1.get(0))=====>"+levelMp.get(innerList1.get(0)));
								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
								Map<String, List<List<String>>> scoreMp = list.get(0);
								Map<String, List<List<String>>> measureMp = list.get(1);
								Map<String, List<List<String>>> questionMp = list.get(2);
								Map<String, List<List<String>>> GoalMp = list.get(3);
			%>


			<%--<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
				 <div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
				<%=innerList1.get(1)%>
				<p style="font-weight: normal; font-size: 10px;">
				<%=innerList1.get(4)%>
				</p>
				</div> --%>
			<!-- <div class="content1"> -->
				<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<blockquote><strong>System Type - </strong>Competencies + Goals + Measures</blockquote>
				</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 10%;  text-align: center;">
				<!-- <b>Score Card</b> -->
				<b>Competencies</b>
			</div>
			<div 
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 16.5%;  text-align: center;">
				<b>Goal </b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 10%;  text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 8.2%;  text-align: center;">
				<b>Measure</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 8.2%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left;  border: 1px solid #eee; width: 19%; text-align: center;">
				<b>Question</b>
			</div>
			<div
				style="overflow: hidden; float: left;  border: 1px solid #eee; width: 12.8%; text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
				<%
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
									for (int j = 0; scoreList != null && j < scoreList.size(); j++) {

										List<String> innerList = scoreList.get(j);
										z++;
				%>

				<div style="overflow: hidden; float: left; width: 100%;">
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 10%;">
						<p>
							<span style="float: left;"><%=innerList.get(1)%></span>
							<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList1.get(0) %>&type=score';" title="Add New">Add</a></span>
							<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=innerList.get(0) %>&type=score';" title="Edit Exist">Edit</a></span>
						</p>						
					</div>
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.2%; text-align: right;">

						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>

					</div>

					<div
						style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 75.6%;">
						<%
							List<List<String>> goalList = GoalMp.get(innerList.get(0));
												for (int k = 0; goalList != null && k < goalList.size(); k++) {
													List<String> goalinnerList = goalList.get(k);
													z++;
						%>

						<div style="overflow: hidden; float: left; width: 100%;">
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 22.1%;">
								<p>
								<span style="float: left;"><%=goalinnerList.get(1)%></span>
								<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList.get(0) %>&type=goal';" title="Add New">Add</a></span>
								<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=goalinnerList.get(0) %>&type=goal';" title="Edit Exist">Edit</a></span>
								</p>
							</div>
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 13.3%; text-align: right;">

								<p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
							</div>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 64.4%;">
								<%
									List<List<String>> measureList = measureMp.get(goalinnerList.get(0));
															for (int l = 0; measureList != null && l < measureList.size(); l++) {
																List<String> measureinnerList = measureList.get(l);
																z++;
								%>


								<div style="overflow: hidden; float: left; width: 100%;">
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 17%;">
											<p>
											<span style="float: left;"><%=measureinnerList.get(1)%></span>
											<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=goalinnerList.get(0) %>&type=measure';" title="Add New">Add</a></span>
											<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=measureinnerList.get(0) %>&type=measure';" title="Edit Exist">Edit</a></span>
											</p>
									</div>
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 17%; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
									</div>
									<div style="overflow: hidden; float: left; width: 65.8%;">
										<%
											List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
																		for (int m = 0; questionList != null && m < questionList.size(); m++) {
																			List<String> question1List = questionList.get(m);
																			z++;
										%>
										<div style="overflow: hidden; float: left; width: 100%;">
											<div
												style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 59.6%;">
													<p>
													<span style="float: left;"><%=question1List.get(0)%></span>
													<span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=measureinnerList.get(0) %>&type=quest';" title="Add New">Add</a></span>
													<span><a id="editexist<%=z %>" href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=question1List.get(3) %>&type=quest';" title="Edit Exist">Edit</a></span>
													</p>
											</div>
											<div
												style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 39.8%; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
											</div>
										</div>
										<%
											}
										%>
									</div>
								</div>
								<%
									}
								%>
							</div>
						</div>
						<!-- </div> -->
						<%
							}
						%>
			</div>		
				</div>
			
				<%
					}
				%>

			</div>
			</div>
			
			<%
				}
						}else if (uF.parseToInt(innerList1.get(3)) == 4){
							
			%>

			<%-- <div style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
			<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
			<%=innerList1.get(1)%>
			</div> --%>

			<!-- <div class="content1"> -->
				<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<blockquote><strong>System Type - </strong>KRA</blockquote>
				</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
				
					<table class="tb_style" style="width: 100%; float: left;">
						<tr>
							<td width="90%"><b>KRA</b></td>
							<td><b>Weightage</b></td>
						</tr>
						<%
						Iterator<String> it = hmKRA1.keySet().iterator();
						int count=0;
						while(it.hasNext()){
							String key = it.next();
							List<List<String>> outerList = hmKRA1.get(key);
							System.out.println("outerList===>"+outerList);
							for(int aa=0;outerList!=null && aa<outerList.size();aa++){
								List<String> innerList=outerList.get(aa);
						%>
						<tr>
							<td><%=innerList.get(7)%></td>							
							<td style="text-align: right"><%=innerList.get(9)%>%</td>
						</tr>

						<%
							}
						}
						%>
					</table>
				
			</div>
			<!-- </div> -->
			</div>

			<%
				
						}else if (uF.parseToInt(innerList1.get(3)) == 3 || uF.parseToInt(innerList1.get(3)) == 5){
							
							String systemtype="";
							if(uF.parseToInt(innerList1.get(3)) == 3){
								systemtype="Goal";
							}else{
								systemtype="Target";
							}
							%>

							<%-- <div style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
							<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
							<%=innerList1.get(1)%>
							</div> --%>

							<!-- <div class="content1"> -->
				<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<blockquote><strong>System Type - </strong><%=systemtype %></blockquote>
				</div>
							<div
								style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
								
									<table class="tb_style" style="width: 100%; float: left;">
										<tr>
											<%-- <th width="90%">&nbsp;<%=systemtype %></th> --%>
											<td width="90%"><b><%=systemtype %></b></td>
											<td><b>Measures</b></td>
										</tr>
										<%
										Iterator<String> it = hmKRA.keySet().iterator();
										int count=0;
										while(it.hasNext()){
											String key = it.next();
											List<List<String>> outerList = hmKRA.get(key);
											System.out.println("outerList===>"+outerList);
											for(int aa=0;outerList!=null && aa<outerList.size();aa++){
												count++;
												List<String> innerList=outerList.get(aa);
										%>
										<tr>
											<td><%=count%>)&nbsp;<%=innerList.get(7)%></td>							
											<td style="text-align: right"><%=hmMesures.get(innerList.get(1))%></td>
										</tr>

										<%
											}
										}
										%>
									</table>
								
							</div>
							</div>
							<!-- </div> -->

							<%
								
										}
					 } %>
					</div>
			</div>
			
		<% } %>	
			
			
			
		
		
		
					<div id="appraisalHeadingDiv" style="width:100%;float:left;margin-top:50px;">
						<input type="hidden" name="appraisalHeadingDivCounter"
						id="appraisalHeadingDivCounter" value="0"> <s:hidden
							name="id"></s:hidden><s:hidden
							name="oreinted"></s:hidden>
							<table class="tb_style" width="98%">
								<tr>
									<th align="right" style="padding-right:20px" width="15%">Title</th>
									<td colspan="5">
									<input type="hidden" name="main_level_id" id="main_level_id" style="width:80%" value="<%=mainLevelList!=null ? mainLevelList.get(0) : null  %>"/>
									<%if(mainLevelList==null){ %>
									<input type="text" name="levelTitle" id="levelTitle" style="width:80%"/>
									<%}else{%>
									<%=mainLevelList.get(1)==null ? "" : mainLevelList.get(1)%>
									<input type="hidden" name="levelTitle" id="levelTitle" style="width:80%" value="<%=mainLevelList.get(1) %>"/>
									<%} %>
																		
									<!-- <input type="text" name="levelTitle" id="levelTitle" style="width:80%"/> -->
									</td>
								</tr>
								<tr>
									<th align="right" style="padding-right:20px">Short Description</th>
									<td colspan="5">
									<%if(mainLevelList==null){ %>
									<input type="text" name="shortDesrciption" id="shortDesrciption" style="width:80%"/>
									<%}else{%>
									<%=mainLevelList.get(2)==null ? "" : mainLevelList.get(2)%>
									<input type="hidden" name="shortDesrciption" id="shortDesrciption" style="width:80%" value="<%=mainLevelList.get(2)%>"/>
									<%} %>
										<!-- <input type="text" name="shortDesrciption" id="shortDesrciption" style="width:80%"/> -->
									</td> 
								</tr>
								<tr>
									<th align="right" style="padding-right:20px" valign="top">Long Description</th>
									<td colspan="5">
									<%if(mainLevelList==null){ %>
									<textarea rows="4" cols="10" name="longDesrciption" id="longDesrciption" style="width:80%"></textarea>
									<%}else{%>
									<%=mainLevelList.get(3)==null ? "" : mainLevelList.get(3)%>
									<input type="hidden" name="longDesrciption" id="longDesrciption" style="width:80%" value="<%=mainLevelList.get(3)%>"/>
									<%} %>
									
									<!-- <textarea rows="4" cols="10" name="longDesrciption" id="longDesrciption" style="width:80%"></textarea> -->
									
										</td>
								</tr>
								<%-- 
								<tr>
									<th align="right" style="padding-right:20px">System</th>
									<td colspan="5"><s:select theme="simple"
											name="appraisalSystem" headerKey=""
											headerValue="Select System"
											list="#{'1':'Balance Score Card Appraisal System', '2':'Other', '3':'Goal', '4':'KRA', '5':'Target'}"
											onchange="showSystem(this.value,'');" /></td>
								</tr>
								<tr id="scoreCardID" style="display:none;" >
									<th width="15%" style="padding-right:20px" align="right">Competency</th>
									<td colspan="5"><s:select theme="simple" name="scoreCard"
											headerKey="" headerValue="Select Competency" id="scoreCar<div style="width:100%;float:left;margin-top:50px;" id="appraisalHeadingDiv">
						<input type="hidden" value="0" id="appraisalHeadingDivCounter" name="appraisalHeadingDivCounter"> <input type="hidden" id="formID_id" value="6" name="id"><input type="hidden" id="formID_oreinted" value="1" name="oreinted">
							<table width="98%" class="tb_style">
								<tbody><tr>
									<th width="15%" align="right" style="padding-right:20px">Title</th>
									<td colspan="5">
									<input type="hidden" value="null" style="width:80%" id="main_level_id" name="main_level_id">
									
									<input type="text" style="width:80%" id="levelTitle" name="levelTitle">
									
																		
									<!-- <input type="text" name="levelTitle" id="levelTitle" style="width:80%"/> -->
									</td>
								</tr>
								<tr>
									<th align="right" style="padding-right:20px">Short Description</th>
									<td colspan="5">
									
									<input type="text" style="width:80%" id="shortDesrciption" name="shortDesrciption">
									
										<!-- <input type="text" name="shortDesrciption" id="shortDesrciption" style="width:80%"/> -->
									</td> 
								</tr>
								<tr>
									<th valign="top" align="right" style="padding-right:20px">Long Description</th>
									<td colspan="5">
									
									<textarea style="width:80%" id="longDesrciption" name="longDesrciption" cols="10" rows="4"></textarea>
									
									
									<!-- <textarea rows="4" cols="10" name="longDesrciption" id="longDesrciption" style="width:80%"></textarea> -->
									
										</td>
								</tr>
								
							</tbody></table>
						</div>d"
											onchange="getcontent();"
											list="#{'1':'Competencies + Goals + Objectives + Measures', 
										'3':'Competencies + Goals + Measures','2':'Competencies + Measures'}" />
									</td>
								</tr> --%>
							</table>
						</div>
						
						<div id="appraisalHeadingDiv1" style="width:100%;float:left;margin-top:50px;">
							<table class="table table_no_border" width="98%">
								
								<tr>
									<th align="right" style="padding-right:20px" width="15%">System</th>
									<td colspan="5"><s:select theme="simple"
											name="appraisalSystem" headerKey=""
											headerValue="Select System"
											list="#{'1':'Balance Score Card Appraisal System','2':'Other','3':'Goal','4':'KRA','5':'Target'}"
											onchange="showSystem(this.value,'');" /></td>
								</tr>
								<tr id="scoreCardID" style="display:none;" >
									<th width="15%" style="padding-right:20px" align="right">Competency</th>
									<td colspan="5"><s:select theme="simple" name="scoreCard"
											headerKey="" headerValue="Select Competency" id="scoreCard"
											onchange="getcontent(this.value);"
											list="#{'1':'Competencies + Goals + Objectives + Measures', 
										'3':'Competencies + Goals + Measures','2':'Competencies + Measures'}" />
									</td>
								</tr>
							</table>
						</div>
						
						
						
						<div id="mainDiv" style="float:left; margin: 10px 0px 0px 0px; width: 100%;">
							
							
						</div>
						
						<div id="otherDiv" style="float:left;width:100%;display: none;">
								<ul class="level_list ul_class">
									<li>
										<table class="tb_style">
											<tr>
												<th width="15%" nowrap="nowrap">Question Type</th>
												<td><s:select theme="simple" name="otherQuestionType"
														headerKey="" headerValue="Select Type"
														list="#{'With Short Description':'With Short Description', 'Without Short Description':'Without Short Description'}" />


												</td>
											</tr>

											<tr>
												<th nowrap>Answer Type</th>
												<td>
												<s:select theme="simple" name="answerTypeSelect" id="answerTypeSelect"
														headerKey="" headerValue="Select Answer Type"
														list="ansTypeList" listKey="id" listValue="name" onchange="setOtherAnsType(this.value);" />
														
  													<input type="hidden" name="otherAnsType" id="otherAnsType" />

												</td>
											</tr>
											<tr>
												<th>Weightage</th>
												<td><input type="radio" name="checkWeightage" id="checkWeightage" value="1" onclick="getcheckWeightage(this.value);"/>Yes
													<input type="radio" name="checkWeightage" id="checkWeightage" value="0" onclick="getcheckWeightage(this.value);"/>no
													 <input type="hidden" name="hidecheckWeightage" id="hidecheckWeightage" />
													</td>
											</tr>
										</table>										
									</li>

									<li><a href="javascript:void(0)" class="add_lvl" onclick="getOtherquestion();">Add
											Appraisal Questions</a></li>
									<li id="otherQuestionLi"> 
										
										</li>
								</ul>
							</div>
						
		
					<div>
						
						<s:submit value="Save" cssClass="input_button" name="submit"></s:submit>
						
					</div> <s:hidden name="plancount" id="plancount"></s:hidden>	
		
		
	</s:form>
</div>

