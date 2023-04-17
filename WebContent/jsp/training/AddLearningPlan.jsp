
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.training.FillCertificate"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%> 


<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

 
<% 	String op = (String) request.getAttribute("operation");%>


<style type="text/css">  
/* CSS for the demo. CSS needed for the scripts are loaded dynamically by the scripts */
.steps .next,.steps .current {
	padding-right: 5px;
	padding-left: 5px;
}

.add1 {
	background-image: url("images1/add-item.png");
	background-position: right center;
	background-repeat: no-repeat;
	display: block;
	float: left;
	padding: 0 20px 0 0;
	text-decoration: none;
	text-indent: -9999px;
	width: 10px;
}

.ul_class li {
	margin: 10px 0px 10px 100px;
}

.leftColumn {
	width: 22%;
	border: 1px solid #868686;
	box-shadow: 2px 1px 7px #AAAAAA;
	background-color: #E2EBED;
	padding-bottom: 25px;
	float: left;
	margin-left: 10px;
	/* height:175px; */
}


.dragableBox {
	min-width: 90%;
	min-height: 10px;
	border: 1px solid #666666;
	background-color: #81b73a;
	float: left;
	margin-bottom: 2px;
	margin-top: 3px;
	padding: 2px 3px;
	font-weight: bold;
	font-size: 12px;
	text-align: left;
	margin-left: 5px;
	margin-right: 5px;
	box-shadow: 0 3px 3px #919191;
}

.dragableBox:hover {
	cursor: move;
}

.dropBox {
	width: 700px;
	border: 1px solid #666666;
	background-color: #E2EBED;
	height: 35px;
	margin-bottom: 5px;
	padding: 3px;
	overflow: auto;
	box-shadow: 0 3px 3px #919191;
}

a.close-font:before{
	font-size: 24px;
}
</style>

<script type="text/javascript" src="<%= request.getContextPath()%>/js/drag-drop-custom.js"></script>	
 
<script type="text/javascript">



$(function() {
	var date_yest = new Date();
    var date_tom = new Date();
    date_yest.setHours(0,0,0);
    date_tom.setHours(23,59,59); 
   
	$('input[name=starttime]').datetimepicker({
		format: 'HH:mm',
		minDate: date_yest
    }).on('dp.change', function(e){ 
    	$('input[name=endtime]').data("DateTimePicker").minDate(e.date);
    });
	
	$('input[name=endtime]').datetimepicker({
		format: 'HH:mm',
		maxDate: date_tom
    }).on('dp.change', function(e){ 
    	$('input[name=starttime]').data("DateTimePicker").maxDate(e.date);
    });
	/* $("input[name=startdate]").datepicker({format: 'dd/mm/yyyy'});
	$("input[name=enddate]").datepicker({format: 'dd/mm/yyyy'}); */
	
	/* ===start parvez date: 14-10-2021=== */
	$("input[name=startdate]").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('input[name=enddate]').datepicker('setStartDate', minDate);
    });
    
    $("input[name=enddate]").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('input[name=startdate]').datepicker('setEndDate', minDate);
    });
	/* ===end parvez date: 14-10-2021=== */
    
	$("input[name=oneTimeDate]").datepicker({format: 'dd/mm/yyyy'});
	
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	
	
}); 


 
	function changeStatus(id) {
		if (document.getElementById('addFlag' + id).checked == true) {
			document.getElementById('status' + id).value = '1';
		} else {
			document.getElementById('status' + id).value = '0';
		}
	}
	
	
	function openPanelEmpProfilePopup(empId) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Employee Information');
		 if($(window).width() >= 1100){
			 $(".modal-dialog").width(1100);
		 }
		 $.ajax({
				url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}	
	
function checkAllDays(count) {
	//alert(count); 
	var everyday = document.getElementById("everyday"+count);		
	var weekdays = document.getElementsByName('weekdays'+count);
	if(everyday.checked==true){
		for(var i=0;i<weekdays.length;i++){
			weekdays[i].checked = true;
		 }
	}else{
		for(var i=0;i<weekdays.length;i++){
			weekdays[i].checked = false;
		 }
	}
}

function uncheckAllDays(count) {
	//alert(count);
	var everyday = document.getElementById("everyday"+count);	
	var weekdays = document.getElementsByName('weekdays'+count);
	
	var cnt = 0;
		for(var i=0;i<weekdays.length;i++){
			if(weekdays[i].checked==true){
				cnt++;
			}
		 }
		if(weekdays.length == cnt){
			everyday.checked = true;
		} else{
			everyday.checked = false;
		}
}


	var questionCnt = 0;
	var cxtpath = '<%=request.getContextPath()%>';
	var anstype = '<%=(String) request.getAttribute("anstype")%>';
	<%-- <% String anstype = (String) request.getAttribute("anstype"); %> --%>
	
function showAnswerTypeDiv(ansType, cnt, id, id1) {
	var action = 'ShowAnswerType.action?ansType=' + ansType;
	getContent("anstypediv"+cnt, action);
	changeNewAnswerType(ansType, cnt, id, id1);
}


function changeNewAnswerType(val, cnt, id, id1) {

	 if (val == 1 || val == 2 || val == 8) {
		addQuestionType1(id,cnt);
		document.getElementById(id).style.display = 'table-row';

		addQuestionType2(id1,cnt);
		document.getElementById(id1).style.display = 'table-row';
	} else if (val == 9) {
		addQuestionType3(id,cnt);
		document.getElementById(id).style.display = 'table-row';

		addQuestionType4(id1,cnt);
		document.getElementById(id1).style.display = 'table-row';

	 }else if (val == 6) {
		addTrueFalseType(id,cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).innerHTML ="";
		document.getElementById(id1).style.display = 'none';

	}else if (val == 5) {
		addYesNoType(id,cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).innerHTML ="";
		document.getElementById(id1).style.display = 'none';

	} else {
		addQuestionType1(id,cnt);
		addQuestionType2(id1,cnt);
		document.getElementById(id).style.display = 'none';
		document.getElementById(id1).style.display = 'none';
	}

}


function addTrueFalseType(id,cnt){
	document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"2\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True"
	+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
}

function addYesNoType(id,cnt){
	document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"2\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes"
	+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
} 
function addQuestionType1(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb\"  class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType2(id1,cnt) {
	document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)<input type=\"text\" name=\"optionc\"  class=\"validateRequired form-control \" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond\"  class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}
function addQuestionType3(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control \"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb\"   class=\"validateRequired form-control \"/> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType4(id1,cnt) {
	document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)<input type=\"text\" name=\"optionc\"  class=\"validateRequired form-control \" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond\"  class=\"validateRequired form-control \"/> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}


function getQuestion(oldcnt, callFrom) {
	//alert(anstype);
	  var totweight=0;
		  oldcnt = questionCnt;
		
		questionCnt++;
		var cnt=questionCnt;
		var ultag = document.createElement('ul');
		var aa = getQuestoinContentType(cnt,callFrom);
		
		ultag.id = "questionUl"+cnt;
		 var a = "<li><table class=\"table\" width=\"100%\">"
				+ "<tr><th>"+questionCnt+")</th><th width=\"17%\" style=\"text-align: right;\">Add Question<sup>*</sup></th>"
				+ "<td colspan=\"3\"><span id=\"newquespan"+cnt+"\" style=\"float: left; \"><input type=\"hidden\" name=\"hidequeid\" id=\"hidequeid"+cnt+"\" value=\"0\"/>"
				+"<textarea rows=\"2\" name=\"question\" id=\"question"+cnt+"\" class=\"validateRequired form-control \"  style=\"width: 330px;\"></textarea>"
				//+"<input type=\"text\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\"/>"
				+"</span>"

				+ "&nbsp;<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt\" value=\""+cnt+"\"/>"
				/* +"<sup>*</sup><input type=\"text\" style=\"width: 35px;\" name=\"weightage\" id=\"weightage"+cnt+"\" class=\"validate[required,custom[integer]]\" value=\"100\" onkeyup=\"validateScore(this.value,'weightage"+cnt+"','hideweightage"+cnt+"');\"/>"
				+"<input type=\"hidden\" name=\"hideweightage\" id=\"hideweightage"+cnt+"\" value=\"100\"/>" */
				+"</span>&nbsp;&nbsp;"
				+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"','"+callFrom+"');\" > +Q </a></span>&nbsp;"
				+"<span id=\"checkboxspan"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+ cnt+ "')\" />"
				+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/></span>"
				
				+"<a href=\"javascript:void(0)\" title=\"Add New Question\" onclick=\"getQuestion('"+cnt+"','"+callFrom+"')\" ><i class=\"fa fa-plus-circle\"></i></a>&nbsp;&nbsp; "
				+"<a href=\"javascript:void(0)\" title=\"Remove Question\" onclick=\"removeQuestion('questionUl"+cnt+"')\" class=\"close-font\"></a>"
				//+"<a href=\"javascript:void(0)\" class=\"add_lvl\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\" ></a>"
				+"<input type=\"hidden\" name=\"questiontypename\" value=\""+ cnt+"\" /></td></tr>" //+othrQtype
				+"<tr><th></th><th>Select Answer Type</th><td><select name=\"ansType\" class=\"form-control \" id=\"ansType"+cnt+"\" onchange=\"showAnswerTypeDiv(this.value, '"+cnt+"', 'answerType"+cnt+"', 'answerType1"+cnt+"');\"> <option value=\"\">Select</option>" +anstype+"</select>"
				+"</td><td><div id=\"anstypediv"+cnt+"\"><div id=\"anstype9\">"
				+"a) Option1&nbsp;<input type=\"checkbox\" value=\"a\" name=\"correct\" disabled=\"disabled\"/> b) Option2&nbsp;<input type=\"checkbox\" name=\"correct\" value=\"b\" disabled=\"disabled\"/><br />"
				+"c) Option3&nbsp;<input type=\"checkbox\" value=\"c\" name=\"correct\" disabled=\"disabled\"/> d) Option4&nbsp;<input type=\"checkbox\" name=\"correct\" value=\"d\" disabled=\"disabled\"/><br />"
				+"</div></div></td></tr>"
				+aa
				+"</table></li>";

		ultag.innerHTML = a;
			document.getElementById("questionLi").appendChild(ultag);
			//alert("questionCnt 9 ===> "+questionCnt);
		
		/* document.getElementById("weightage"+cnt).value = remainweight;
		document.getElementById("hideweightage"+cnt).value = remainweight; */
		
	 /* }	 */
	}


function removeQuestion(id){
	var row_skill = document.getElementById(id);
	if (row_skill && row_skill.parentNode
			&& row_skill.parentNode.removeChild) {
		row_skill.parentNode.removeChild(row_skill);
	}
}


function getQuestoinContentType(cnt,callFrom){
	var val = 9;
	
	var a="";
	if( val == 8){
		a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
		+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
	
	}else if (val == 1 || val == 2 || val == 9) {
		a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<span id=\"aspan\"><input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"form-control \"/></span> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<span id=\"bspan\"><input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
		+"<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<span id=\"cspan\"><input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<span id=\"dspan\"><input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";

	 }else if (val == 6) {
		a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
			+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td></tr>";
	
	}else if (val == 5) {
		a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td></tr>";
	} else {
		a="";
	}
	return a;
}


var dialogEdit = '#SelectQueDiv';
function openQuestionBank(count,callFrom) {
	//removeLoadingDiv('the_div');
	
	var ansType = document.getElementById('ansType'+count).value;
	//alert("ansType==>"+ansType);
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Question Bank');
	 $.ajax({
			url : "SelectLearningPlanQuestion.action?count="+count+"&ansType="+ansType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}


function setQuestionInTextfield() {
	var queid = document.getElementById("questionSelect").value;
	var count = document.getElementById("count").value;
	
	var action = 'SetLearningPlanQuestionToTextfield.action?queid=' + queid + '&count=' +count;
	
    xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
    } else {
            var xhr = $.ajax({
                    url : action,
                    cache : false,
                    success : function(data) {
                    	if(data != "" && data.trim().length > 0){
                    		var allData = data.split("::::");
                            document.getElementById("newquespan"+count).innerHTML = allData[0];
                            document.getElementById("answerType"+count).innerHTML = allData[1];
                            if(allData.length > 2){
                            	document.getElementById("answerType1"+count).style.display = 'table-row';
                            	document.getElementById("answerType1"+count).innerHTML = allData[2];
                            }else{
                            	document.getElementById("answerType1"+count).style.display = 'none';
                            }
                    	}
                    	$('.modal').hide();
                    }
            });
    }
    $(dialogEdit).dialog('close');
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


var dialogEdit2 = '#EditQuestionDiv';
function openEditFeedbackQue(ID,queID,queno,operation,step,queAnstype) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Edit Question');
	 $.ajax({
			url : "getLearningFeedbackQuestion.action?fromPage=LD&ID=" + ID + "&queno=" + queno + "&queID=" + queID + "&operation=" + operation + "&step=" + step + 
					"&queAnstype=" + queAnstype,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}


function closeEditDiv(){

	$(dialogEdit2).dialog('close');
}


function openNewQue(value) {
		
	   document.getElementById("newquediv"+value).style.display="block";
	   document.getElementById("savebtndiv"+value).style.display="block";
	 
}

function closeDiv(btndiv,quediv){
	document.getElementById(quediv).style.display="none";
	document.getElementById(btndiv).style.display="none";
}

function validateScoreEdit(value,weightageid,weightagehideid,totweightage) {
	var singleWeightage = document.getElementById(weightagehideid).value;
	var othertotweight = parseFloat(totweightage) - parseFloat(singleWeightage);
	var remainWeightage = 100 - parseFloat(othertotweight);
	if(parseFloat(value) > parseFloat(remainWeightage)){
		alert("Entered value greater than Weightage");
		document.getElementById(weightageid).value = remainWeightage;
	}else if(parseFloat(value) <= 0 ){
		alert("Invalid Weightage");
		document.getElementById(weightageid).value = remainWeightage;
	} 
}


function previewcertificate(){
	 var id=document.getElementById("strCertificateId").value ;
	  
	  if(id=='-1'){
		  alert("You have not selected any certificate");
	  }else{
		  var certid = id;
		  var certiName = document.getElementById('strCertificateId')[document.getElementById('strCertificateId').selectedIndex].innerHTML;
		  var dialogEdit = '.modal-body';
			 $(dialogEdit).empty();
			 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			 $("#modalInfo").show();
			 $(".modal-title").html('' + certiName);
			 $.ajax({
					url : "ViewCertificate.action?ID="+certid,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
	  }
}


function createNewCertificate(){
	
	  var id=document.getElementById("strCertificateId").value ;
	  if(id=='0'){
		 //'A',''		  
	  var dialogEdit = '.modal-body';
			 $(dialogEdit).empty();
			 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			 $("#modalInfo").show();
			 $(".modal-title").html('Add New Certificate');
			 if($(window).width() >= 1100){
				 $(".modal-dialog").width(1100);
			 }
			 $.ajax({
					url : "AddCertificate.action?operation=A&type=type&ID=",
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
	  }
 }
 
 
function viewCourseDetail(courseId, courseName) {
	//alert("openQuestionBank id "+ id)
	 		var dialogEdit = '.modal-body';
			 $(dialogEdit).empty();
			 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			 $("#modalInfo").show();
			 $(".modal-title").html(''+courseName+'');
			 if($(window).width() >= 900){
				 $(".modal-dialog").width(900);
			 }
			 $.ajax({
					url : "ViewCourseDetails.action?courseId="+courseId,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
	}


function viewAssessmentDetail(assessmentId, assessmentName) {
	//alert("openQuestionBank id "+ id)
	var dialogEdit = '.modal-body';
			 $(dialogEdit).empty();
			 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			 $("#modalInfo").show();
			 $(".modal-title").html(''+assessmentName+'');
			 $.ajax({
					url : "ViewAssessmentDetails.action?assessmentId="+assessmentId,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
	}
	
	/* ===start parvez date: 22-09-2021=== */
	function viewVideoDetail(videoId, videoName) {
		//alert("frompage=AL ");
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html(''+videoName+'');
		$.ajax({
			url : "LearningVideoDetails.action?fromPage=AL&learningVideoId="+videoId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	/* ===end parvez date: 20-09-2021=== */
	
function changeAddLeaner(val) {
	var planId = document.getElementById("planId").value;
	var strGapEmpId = document.getElementById("strGapEmpId").value;
	
	if(val == 1 || val == 4) {
	
		document.getElementById("tr_attributes").style.display = "table-row";
		document.getElementById("tr_skills").style.display = "table-row";
		document.getElementById("addLearnerTR").style.display = "table-row";
		
		$.ajax({
			url:'GetLearningEmpList.action?lPlanID='+planId+'&alignWith='+val,
			cache:false,
			success:function(result){
				document.getElementById("addLearnerTD").innerHTML = result;
			}
				
		});
	
		
	} else if(val == 2) {
		
		document.getElementById("tr_attributes").style.display = "none";
		document.getElementById("tr_skills").style.display = "none";
		document.getElementById("addLearnerTR").style.display = "table-row";
		var attribLen = document.getElementById("strAttribute2").length;
		 var attribIds = "";
		 for (var i = 0; i < attribLen; i++) {
	        var isSelected = document.getElementById("strAttribute2").options[i].selected;
	       if(isSelected = (isSelected)) {
	       	if(attribIds == "") {
	       		attribIds += document.getElementById("strAttribute2").options[i].value;
	       	} else {
	       		attribIds += "," + document.getElementById("strAttribute2").options[i].value;
	       	}
	       }
	    }
		 
		 var skillLen = document.getElementById("skills2").length;
		 var skillIds = "";
		 for (var i = 0; i < skillLen; i++) {
	        var isSelected = document.getElementById("skills2").options[i].selected;
	       if(isSelected = (isSelected)) {
	       	if(skillIds == "") {
	       		skillIds += document.getElementById("skills2").options[i].value;
	       	} else {
	       		skillIds += "," + document.getElementById("skills2").options[i].value;
	       	}
	       }
	    }
		 var action="GetLearningEmpList.action?lPlanID="+planId+"&alignWith=2&attribIds="+attribIds+"&skillIds="+skillIds;
		 var strGapEmpId = document.getElementById("strGapEmpId").value;
		 action +="&strGapEmpId="+strGapEmpId;
		 $.ajax({
				url:action,
				cache:false,
				success:function(result){
					document.getElementById("addLearnerTD").innerHTML = result;
				}
					
			});
		
		
	} else {
		
		document.getElementById("tr_attributes").style.display = "table-row";
		document.getElementById("tr_skills").style.display = "table-row";
		
		$.ajax({
			url:'GetLearningEmpList.action?lPlanID='+planId+'&alignWith=3',
			cache:false,
			success:function(result){
				document.getElementById("addLearnerTD").innerHTML = result;
			}
				
		});
	
		document.getElementById("addLearnerTR").style.display = "table-row";
		
	}
	
}

function getLocDeptLvlAndDesigByOrg(value) {
	//alert("boolPublished =====> " + boolPublished);
	var action = "GetLearningLocDeptLvlAndDesig.action?f_org="+value;
	
	getContent('multiFilterDiv', action);

}


function getGapEmpList() {
	var attribLen = document.getElementById("strAttribute2").length;
	 var attribIds = "";
	 for (var i = 0; i < attribLen; i++) {
        var isSelected = document.getElementById("strAttribute2").options[i].selected;
       if(isSelected = (isSelected)) {
       	if(attribIds == "") {
       		attribIds += document.getElementById("strAttribute2").options[i].value;
       	} else {
       		attribIds += "," + document.getElementById("strAttribute2").options[i].value;
       	}
       }
    }
	 
	 var skillLen = document.getElementById("skills2").length;
	 var skillIds = "";
	 for (var i = 0; i < skillLen; i++) {
        var isSelected = document.getElementById("skills2").options[i].selected;
       if(isSelected = (isSelected)) {
       	if(skillIds == "") {
       		skillIds += document.getElementById("skills2").options[i].value;
       	} else {
       		skillIds += "," + document.getElementById("skills2").options[i].value;
       	}
       }
    }
	 var action="GetLearningEmpList.action?lPlanID="+planId+"&alignWith=2&attribIds="+attribIds+"&skillIds="+skillIds;
	 var strGapEmpId = document.getElementById("strGapEmpId").value;
	 
		$.ajax({
			url:action,
			cache:false,
			success:function(result){
				document.getElementById("addLearnerTD").innerHTML = result;
			}
				
		});

}

function closeForm() {
	$("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		url:'LearningPlanDashboard.action',
		cache:false,
		success:function(data){
			$("#divResult").html(data);
		}
	});
	
}

function checkFields() {
   /*  var iframe = document.getElementById("empiframe");
	var innerDoc = iframe.contentDocument || iframe.contentWindow.document; */
	var learnerSelected = "";
	if(document.getElementById("empselected")) {
		learnerSelected = document.getElementById("empselected").value;
	}
		
	if(learnerSelected == "" || learnerSelected == null || learnerSelected == "null" || learnerSelected == "0" ) {
		alert("Please select learner");
		return false;
		
	} 
	
	return true;
}

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
	
	Map<String,String> hmWLocation= (Map<String, String>) request.getAttribute("hmWLocation");
	List<List<String>> trainingList = (List<List<String>>) request.getAttribute("trainingList");
	List<List<String>> coursesList = (List<List<String>>) request.getAttribute("coursesList");
	List<List<String>> assessmentList = (List<List<String>>) request.getAttribute("assessmentList");
	List<List<String>> stageList = (List<List<String>>) request.getAttribute("stageList");
	Map<String, String> hmWeekdays = (Map<String, String>) request.getAttribute("hmWeekdays");
	
	List<FillCertificate> certificateList = (List<FillCertificate>) request.getAttribute("certificateList");
	Map<String,String> hmCertificatePrintMode=(Map<String,String>)request.getAttribute("hmCertificatePrintMode");
	String certificateId = (String) request.getAttribute("certificateId");
	
	//===start parvez date: 08-10-2021===
		List<List<String>> videoList = (List<List<String>>) request.getAttribute("videoList");
	//===end parvez date: 08-10-2021===

%>
<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">

		<div class="box-header with-border">
			<h4 class="box-title">
				<%
		if (request.getAttribute("operation") != null && ((String) request.getAttribute("operation")).equalsIgnoreCase("A")) {
	%>
				<div class="steps">

					<s:if test="step==1">
						<span class="current"> Learning Plan Information :</span>
						<span class="next"> Learning Plan :</span>
						<span class="next">Learning Plan Feedback :</span>
					</s:if>

					<s:if test="step==2">
						<span class="next">
							<a	href="javascript:void(0);" onclick="loadStepOnClick('AddLearningPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Learning Plan Information : </a> 
					   </span>
						<span class="current"> Learning Plan :</span>
						<span class="next">Learning Plan Feedback :</span>
					</s:if>

					<s:if test="step==3">
						<span class="next">
							<a href="javascript:void(0);" onclick="loadStepOnClick('AddLearningPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Learning Plan Information : </a>
						 </span>
						<span class="next">
							<a href="javascript:void(0);" onclick="loadStepOnClick('AddLearningPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Learning Plan : </a> 
						</span>
						<span class="current">Learning Plan Feedback :</span>
					</s:if>

				</div>

				<%
		} else if (request.getAttribute("operation") != null && ((String) request.getAttribute("operation")).equalsIgnoreCase("E")) {
	%>


				<div class="steps">
					<s:if test="step==1">
						<span class="current"> Learning Plan Information :</span>
						<span class="next">
							<a href="javascript:void(0);" onclick="loadStepOnClick('AddLearningPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Learning Plan :</a> 
						</span>
						<span class="next">
							<a href="javascript:void(0);" onclick="loadStepOnClick('AddLearningPlan.action?operation=E&step=2&ID=<%=request.getAttribute("plan_Id")%>')" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Learning Plan Feedback : </a> 
						</span>

					</s:if>

					<s:if test="step==2">
						<span class="next">
							<a href="javascript:void(0);" onclick="loadStepOnClick('AddLearningPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Learning Plan Information : </a> 
						</span>
						<span class="current"> Learning Plan :</span>
						<span class="next">
						    <a href="javascript:void(0);" onclick="loadStepOnClick('AddLearningPlan.action?operation=E&step=2&ID=<%=request.getAttribute("plan_Id")%>')" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Learning Plan Feedback : </a> 
						</span>
					</s:if>

					<s:if test="step==3">
						<span class="next">
							<a href="javascript:void(0);" onclick="loadStepOnClick('AddLearningPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Learning Plan Information :</a> 
						</span>
						<span class="next">
							<a href="javascript:void(0);" onclick="loadStepOnClick('AddLearningPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Learning Plan :</a> 
						</span>
						<span class="current"> Learning Plan Feedback :</span>
					</s:if>

				</div>
				<%
		}
	%>
			</h4>
			
			<div class="pull-right">
				<a href="javascript:void(0);" onclick="closeForm()" class="close-font" style="margin-right:20px;"/>
			</div>
		</div>
		<div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
			<div class="leftbox reportWidth">
				<!-- the tabs -->
				<ul class="tabs" style="background-color: rgba(45, 157, 231, 0.14); padding-top: 5px; padding-bottom: 5px;"> 

					<s:if test="step==1 || mode=='report'">
						<li>
							<a class="current" href="#tab1">Learning Plan Information <% if (op != null && op.equals("E")) { %> Updation<% } else { %>Addition <% } %> </a>
						</li>
					</s:if>

					<s:if test="step==2 || mode=='report'">
						<li>
							<a class="current" href="#tab2">Learning Plan <% if (op != null && op.equals("E")) { %> Updation<% } else { %>Addition <% } %> </a>
						</li>
					</s:if>

					<s:if test="step==3 || mode=='report'">
						<li>
							<a class="current" href="#tab3">Learning Plan Feedback </a>
						</li>
					</s:if>
				</ul>
				<% boolean boolPublished = (Boolean)request.getAttribute("boolPublished");
				   String alignedwith = (String)request.getAttribute("alignedwith"); 
				   //System.out.println("alignedwith --->>>> " + alignedwith);
				   String planId = (String)request.getAttribute("planId");
				%>
				<div class="panes">
					<s:if test="step==1 || mode=='report'">
						<s:form theme="simple" action="AddLearningPlan" id="frmAddLearningPlan1" method="POST" cssClass="formcss" enctype="multipart/form-data">
							<s:hidden name="operation"></s:hidden>
							<s:hidden name="ID"></s:hidden>
							<s:hidden name="step"></s:hidden>
							<s:hidden name="strGapId"></s:hidden>
							<input type="hidden" name="boolPublished" id="boolPublished" value="<%=boolPublished %>" />
							<input type="hidden" name="planId" id="planId" value="<%=planId %>" />
							<s:hidden name="strGapEmpId" id="strGapEmpId"></s:hidden>
							<div style="float: left; width: 100%;">
								<table border="0" class="table" style="width: 100%;">
									<tr>
										<td class="tdLabelheadingBg" colspan="2">
											<span style="color: #68AC3B; font-size: 18px; padding: 5px;">
												Step 1 : </span> Learning Plan Information
										</td>
									</tr>
									<tr>
										<td height="10px">&nbsp;</td>
									</tr>

									<tr>
										<td class="txtlabel" style="vertical-align: top; text-align: right; width: 15%;">Learning Title:<sup>*</sup></td>
										<td>
											<s:textfield name="learningTitle" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield>
										</td>
									</tr>

									<tr>
										<td class="txtlabel" style="vertical-align: top; text-align: right">Learning Objective:<sup>*</sup></td>
										<td>
											<s:textfield name="learningObjective" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> <%-- <s:textarea name="trainingObjective" cssClass="validateRequired text-input" required="true" rows="5" cols="70"></s:textarea> --%>
										</td>
									</tr>

									<tr>
										<td class="txtlabel" style="vertical-align: top; text-align: right">Aligned with:<sup>*</sup></td>
										
										<td>
											<select class="validateRequired form-control " name="alignedwith" id="alignedwith" <%if(boolPublished == true){ %> disabled="disabled" <%} %> onchange="changeAddLeaner(this.value);">
												<option value="3" <%if(alignedwith != null && alignedwith.equals("3")){ %> selected="selected" <%} %>>General</option>
												<option value="2" <%if(alignedwith != null && alignedwith.equals("2")){ %> selected="selected" <%} %>>Gap</option>
												<option value="1" <%if(alignedwith != null && alignedwith.equals("1")){ %> selected="selected" <%} %>>Induction</option>
												<option value="4" <%if(alignedwith  != null && alignedwith.equals("4")){ %> selected="selected" <%} %>>Probation</option>
											</select> 
										</td>
									</tr>

									<tr>
										<td class="txtlabel" style="vertical-align: top; text-align: right">Select Certificate:</td>
										<td>
											<div id="divCertificate">
												<s:select theme="simple" name="strCertificateId" id="strCertificateId" listKey="id" headerKey="-1"
													headerValue="Select Certificate" listValue="name" list="certificateList" cssClass=" form-control "
													value="certificateId" onchange="createNewCertificate();" />

												<span>
													 <a href="javascript:void(0)" onclick="previewcertificate();">Preview</a>
											   </span>
											</div>
										</td>
									</tr>
									<%   
										String learnerIds = (String)request.getAttribute("selectLearnerIDs1");
										String orgids = (String)request.getAttribute("orgids");
										String wLocids = (String)request.getAttribute("wLocids");
										String deptartids = (String)request.getAttribute("deptartids");
										String levelids = (String)request.getAttribute("levelids");
										String desigids = (String)request.getAttribute("desigids");
										 
										%>
									<tr id="tr_attributes">
										<td class="txtlabel" style="vertical-align: top; text-align: right">
											<input type="hidden" name="orgids" id="orgids" value="<%=orgids!=null && !orgids.equals("") ? orgids :"0" %>" />
											<input type="hidden" name="wLocids" id="wLocids" value="<%=wLocids!=null && !wLocids.equals("") ? wLocids :"0" %>" />
											<input type="hidden" name="deptartids" id="deptartids" value="<%=deptartids!=null && !deptartids.equals("") ? deptartids :"0" %>" />
											<input type="hidden" name="levelids" id="levelids" value="<%=levelids!=null && !levelids.equals("") ? levelids :"0" %>" />
											<input type="hidden" name="desigids" id="desigids" value="<%=desigids!=null && !desigids.equals("") ? desigids :"0" %>" />
											<span id="selectedEmpIdSpan"> 
												<%-- <input type="hidden" name="oldempids" id="oldempids1" value="<%=learnerIds!=null && !learnerIds.equals("") ? learnerIds :"0" %>" />
												<input type="hidden" name="empselected" id="empselected1" value="<%=learnerIds!=null && !learnerIds.equals("") ? learnerIds :"0" %>" /> --%>
										</span> Associated With Attribute:<sup>*</sup></td>
										<td>
											<span id="attributeSpan"> 
												<s:select theme="simple" name="strAttribute" list="attributeList"
													listKey="id" id="strAttribute" listValue="name" size="4" multiple="true" value="attributeID" cssClass="validateRequired  form-control " /> 
											</span>
											
											 <span id="gapAttributeSpan" style="display: none; float: left;">
												<s:select theme="simple" name="strAttribute" list="attributeList" listKey="id" id="strAttribute2"
													listValue="name" size="4" multiple="true" value="attributeID" disabled="true" cssClass="validateRequired form-control " onchange="getGapEmpList();" /> 
											</span>
											
											 <span id="gapAttributeSpanLbl" style="display: none; float: left; font-style: italic; margin-top: 10px;" class="txtlabel">(also being used as a filter)</span>
										</td>
									</tr>

									<tr id="tr_skills">
										<td class="txtlabel" style="vertical-align: top; text-align: right">Skills:</td>
										<td>
											<span id="skillsSpan"> 
												<s:select name="skills" id="skills" theme="simple" listKey="skillsId" listValue="skillsName" cssClass=" form-control "
													list="skillslist" size="4" multiple="true" value="skillsID" />
											</span> 
											<span id="gapSkillsSpan" style="display: none; float: left;">
												<s:select name="skills" id="skills2" cssClass=" form-control " headerKey="" theme="simple" listKey="skillsId" listValue="skillsName" 
												list="skillslist" size="4" multiple="true" value="skillsID" disabled="true" onchange="getGapEmpList();" /> 
											</span> 
												
											<span id="gapSkillsSpanLbl" style="display: none; float: left; font-style: italic; margin-top: 10px;" class="txtlabel">(also being used as a filter)</span>
										</td>
									</tr>

									<tr id="addLearnerTR" style="display: <%if(alignedwith != null && (alignedwith.equals("1") || alignedwith.equals("4") || alignedwith.equals("5"))){ %>none; <%} else { %>table-row; <% } %>">
										<td id= "addLearnerTD" colspan="2" style="width: 100%; height: 100%;">
											<s:action name="GetLearningEmpList" executeResult="true">
					        						<s:param name="lPlanID"><%=planId%></s:param>
					        						<s:param name="boolPublished"><%=boolPublished%></s:param>
					        						<s:param name="strGapEmpId"><%=(String) request.getAttribute("strGapEmpId")%></s:param>
					        						<s:param name="attribIds"><%=(String) request.getAttribute("strAttribute")%></s:param>
					        						<s:param name="alignWith"><%=(String) request.getAttribute("alignedwith")%></s:param>
					        					</s:action>
												<%-- <iframe name="empiframe" id="empiframe" style="width: 100%; height: 100%" 
													src="GetLearningEmpList.action?lPlanID=<%=planId %>&boolPublished=<%=boolPublished %>&strGapEmpId=<%=(String) request.getAttribute("strGapEmpId")  %>&attribIds=<s:property value="strAttribute"/>" frameborder="0">
												</iframe> --%>
									    </td>
									</tr>
								</table>
							</div>

							<div style="width: 100%; float: right;margin-top: 20px;">
									<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSubmit" value="Submit & Proceed" />
								<%if(op != null && op.equals("E")) { %>
									<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save & Exit" />
								<% } %>
									
							</div>
						</s:form>
						
						<script>
							changeAddLeaner(<%=(String) request.getAttribute("alignedwith")%>);
						</script>
					</s:if>

					<s:if test="step==2 || mode=='report'">

						<s:form theme="simple" action="AddLearningPlan" id="frmAddLearningPlan2" method="POST" cssClass="formcss" enctype="multipart/form-data">
							<s:hidden name="planId" id="hiddenplanId"></s:hidden>
							<s:hidden name="operation"></s:hidden>
							<s:hidden name="ID"></s:hidden>
							<s:hidden name="step"></s:hidden>
							<s:hidden name="existstageIDs"></s:hidden>
							<div class="row row_without_margin">
								<div class="col-lg-6 col-md-6 col-sm-6" style="border-right: 1px solid gray; ">
									<span style="color: #68AC3B; font-size: 18px; padding: 5px;">
										Step 2 : </span><strong> Learning Plan</strong> <br />
									<br />

									<div id="allStageDiv" style="padding-right: 20px; min-height: 250px;">	<!-- overflow-y: auto; max-height: 250px; -->
										<%
											int count = 1;
										//System.out.println("ALP.jsp/1035--stageList="+stageList);
											for(int i=0; stageList != null && i<stageList.size(); i++){
												List<String> allList = stageList.get(i);
												
										%>
										<% if(allList.get(3) != null && !allList.get(3).equals("Training")) { %>
										<div id="stageDiv<%=count %>" style="width: 100%;">
											<div style="float: left; margin-right: 5px; margin-left: 10px; margin-left: 10px; padding: 5px 0 28px 10px; width: 60px; color: #346897; font-size: 14px;">
												<p>
													<b>Stage <%=count %>: </b>
												</p>
											</div>
											<div id="dropBoxStages<%=count %>" class="dropBox" style="float: right; width: 82%;">
												<%-- <div <%if(boolPublished == false){ %>id="dropContent<%=count %>" <% } %>> --%>
												<div id="dropContent<%=count %>">
													<div class="dragableBox" id="<%=allList.get(3).substring(0,1) %><%=allList.get(1) %>">
														<div style="float: left; text-align: left; width: 100%;"><%=allList.get(2) %></div>
													</div>
												</div>
											</div>

											<div id="stageDataDiv<%=count %>" style="width: 100%;">
												<table border="0" class="table table-bordered">
													<tr>
														<td class="txtlabel" align="right">Learning Plan Type:</td>
														<td colspan="3">
															<input type="hidden" name="lexiststageid" value="<%=allList.get(0) %>" /> 
															<input type="hidden" name="lstagetypeid" value="<%=allList.get(1) %>" /> 
															<input type="hidden" name="lstagetype" value="<%=allList.get(3) %>" />
															<input type="hidden" name="lstagename" value="<%=allList.get(2) %>" />
															 <%=allList.get(3) %> 
															<span style="float: right;"> <%if(allList.get(3) != null && allList.get(3).equals("Course")) { %>
																<a href="javascript:void(0)" onclick="viewCourseDetail('<%=allList.get(1) %>','<%=allList.get(2) %>')">Preview</a>
																<% } else if(allList.get(3) != null && allList.get(3).equals("Assessment")) { %>
																<a href="javascript:void(0)" onclick="viewAssessmentDetail('<%=allList.get(1) %>','<%=allList.get(2) %>')">Preview</a>
														<!-- ===start parvez date: 27-09-2021=== -->
																<% } else if(allList.get(3) != null && allList.get(3).equals("Video")) { %>
																<a href="javascript:void(0)" onclick="viewVideoDetail('<%=allList.get(1) %>','<%=allList.get(2) %>')">Preview</a>
														<!-- ===end parvez date: 27-09-2021=== -->
																<% } %>
															 </span>
													</tr>
													<tr>
														<td class="txtlabel" align="right">Learning Stage Name:</td>
														<td colspan="3"><%=allList.get(2) %></td>
													</tr>
													<tr>
														<td class="txtlabel" align="right">Start Date:<sup>*</sup></td>
														<td>
															<input type="text" name="startdate" id="startdate<%=count %>" class="validateRequired form-control " style="width: 100px !important;" value="<%=allList.get(4) %>" />
														</td>
														<td class="txtlabel" align="right">End Date:<sup>*</sup>
														</td>
														<td>
															<input type="text" name="enddate" id="enddate<%=count %>" class="validateRequired form-control " style="width: 100px !important;" value="<%=allList.get(5) %>" />
														</td>
													</tr>
													
													<tr>
													   <td class="txtlabel" align="right">
													   		<input type="checkbox" name="everyday" id="everyday<%=allList.get(3) %>_<%=allList.get(1) %>" onclick="checkAllDays('<%=allList.get(3) %>_<%=allList.get(1) %>');"
															value="everyday" <%=hmWeekdays.get(allList.get(0)+"_EVERYDAY") %>>Everyday &nbsp;
													  </td>
														<td class="txtlabel" colspan="3">
															<input type="checkbox" name="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>" id="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>"
																onclick="uncheckAllDays('<%=allList.get(3) %>_<%=allList.get(1) %>');" value="Mon"
																<%=hmWeekdays.get(allList.get(0)+"_MON") != null ? hmWeekdays.get(allList.get(0)+"_MON") : "" %>>Mon
															
															<input type="checkbox" name="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>"
																id="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>" onclick="uncheckAllDays('<%=allList.get(3) %>_<%=allList.get(1) %>');"
																value="Tue" <%=hmWeekdays.get(allList.get(0)+"_TUE") != null ? hmWeekdays.get(allList.get(0)+"_TUE") : "" %>>Tue
															
															<input type="checkbox" name="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>"
																id="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>" onclick="uncheckAllDays('<%=allList.get(3) %>_<%=allList.get(1) %>');"
																value="Wed" <%=hmWeekdays.get(allList.get(0)+"_WED") != null ? hmWeekdays.get(allList.get(0)+"_WED") : "" %>>Wed
															
															<input type="checkbox" name="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>"
																id="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>" onclick="uncheckAllDays('<%=allList.get(3) %>_<%=allList.get(1) %>');"
																value="Thu" <%=hmWeekdays.get(allList.get(0)+"_THU") != null ? hmWeekdays.get(allList.get(0)+"_THU") : "" %>>Thu
														
															<input type="checkbox" name="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>"
																id="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>" onclick="uncheckAllDays('<%=allList.get(3) %>_<%=allList.get(1) %>');"
																value="Fri" <%=hmWeekdays.get(allList.get(0)+"_FRI") != null ? hmWeekdays.get(allList.get(0)+"_FRI") : "" %>>Fri
															
															<input type="checkbox" name="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>"
																id="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>" onclick="uncheckAllDays('<%=allList.get(3) %>_<%=allList.get(1) %>');"
																value="Sat" <%=hmWeekdays.get(allList.get(0)+"_SAT") != null ? hmWeekdays.get(allList.get(0)+"_SAT") : "" %>>Sat
															
															<input type="checkbox" name="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>"
																id="weekdays<%=allList.get(3) %>_<%=allList.get(1) %>" onclick="uncheckAllDays('<%=allList.get(3) %>_<%=allList.get(1) %>');"
																value="Sun" <%=hmWeekdays.get(allList.get(0)+"_SUN") != null ? hmWeekdays.get(allList.get(0)+"_SUN") : "" %>>Sun
														</td>
													</tr>
													<tr>
														<td class="txtlabel" align="right">Start Time:<sup>*</sup> </td>
														<td>
															<input type="text" name="starttime" id="starttime<%=count %>" class="validateRequired form-control " style="width: 100px !important;" value="<%=allList.get(6) %>" />
														</td>
														<td class="txtlabel" align="right">End Time:<sup>*</sup></td>
														<td>
															<input type="text" name="endtime" id="endtime<%=count %>" class="validateRequired form-control " style="width: 100px !important;" value="<%=allList.get(7) %>" />
														</td>
													</tr>
												</table>
											</div>
										</div>

										<% } else {
											
										
											Map<String, List<String>> hmTrainingList = (Map<String, List<String>>) request.getAttribute("hmTrainingList");
											Map<String, List<String>> hmTrainingDataList = (Map<String, List<String>>) request.getAttribute("hmTrainingDataList");
											//String scheduleType = (String) request.getAttribute("scheduleTypeValue");
											//String trainingSchedulePeriod = (String) request.getAttribute("trainingSchedulePeriod");
											//String planId = (String) request.getAttribute("planId");
											Map<String, List<List<String>>> hmSessionData = (Map<String, List<List<String>>>) request.getAttribute("hmSessionData");
											//System.out.println("ALP.jsp/1145--allList.get(1) ---> "+allList.get(1));
											
											List<String> trainingList1 = hmTrainingList.get(allList.get(1));
											List<String> trainingDataList = hmTrainingDataList.get(allList.get(1));
											List<List<String>> alSessionData = hmSessionData.get(allList.get(1));
										%>

										<div id="stageDiv<%=count %>" style="width: 100%;">
											<div style=" margin-right: 5px; margin-left: 10px; margin-left: 10px; padding: 5px 0 28px 10px; width: 60px; color: #346897; font-size: 14px;">
												<p>
													<b>Stage <%=count %>: </b>
												</p>
											</div>
											<div id="dropBoxStages<%=count %>" class="dropBox" style="float: right; width: 82%;">
												 <div id="dropContent<%=count %>"> 
													<div class="dragableBox" id="<%=allList.get(3).substring(0,1) %><%=allList.get(1) %>">
														<div style="float: left; text-align: left; width: 100%;"><%=allList.get(2) %></div>
													</div>
												</div>
											</div>

											<div id="stageDataDiv<%=count %>" style="width: 100%;">
												<table border="0" class="table table-bordered" id="trainingScheduleTableId"> 
    												<tr>
														<td class="txtlabel" align="right">Learning Stage Type:</td>
														<td colspan="3">
															<input type="hidden" name="lexiststageid" value="<%=allList.get(0) %>" /> 
															<input type="hidden" name="lstagetypeid" value="<%=trainingList1.get(0) %>" />
															<input type="hidden" name="lstagename" value="<%=trainingList1.get(1) %>" /> 
															<input type="hidden" name="lstagetype" value="<%=trainingList1.get(2) %>" /> <%=trainingList1.get(2) != null && trainingList1.get(2).equals("Training") ? "Classroom Training" : "" %>
															<span style="float: right;">
																 <a href="javascript:void(0)" onclick="editTraining('AddTrainingPlan.action?operation=E&step=2&frmpage=LPlan&ID=<%=trainingList1.get(0) %>&lPlanId=<%=planId %>')">Edit Classroom Training</a> 
															</span>
														</td>
													</tr>
													<tr>
														<td class="txtlabel" align="right">Learning Stage Name:</td>
														<td colspan="3"><%=trainingList1.get(1) %></td>
													</tr>

													<tr>
														<td class="txtlabel" style="vertical-align: top; text-align: right">Periodic:</td>
														<td>
															<div style="position: reletive;">
																<span style="float: left; margin-right: 20px"> <%if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("1")) { %>
																	One Time <% } else  if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("2")) { %>
																	Weekly <% } else  if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("3")) { %>
																	Monthly <% } %> 
																</span>
															</div>
														</td>
														<td colspan="2">
															<div style="position: reletive;">
																<span id="weekly" style="<%if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("2")) { %>display: block; <% } else { %> display: none; <% } %> float: left;">Day:
																	<%=(String)request.getAttribute("weekdayValue") %> 
																</span> 
																
																<span id="monthly" style="<%if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("3")) { %>display: block; <% } else { %> display: none; <% } %> float: left;">
																	<%=(String)request.getAttribute("weekdayValue") %>
																</span>
															</div>
														</td>
													</tr>

													<tr>
														<td class="txtlabel" style="text-align: right">Start Date:</td>
														<td>
															<input type="hidden" name="startdate" id="startdate<%=count %>" value="<%=trainingDataList.get(3) %>" /> <%=trainingDataList.get(1) %>
														</td>
														<td class="txtlabel" style="text-align: right; width: 100px;">End Date:</td>
														<td>
															<input type="hidden" name="enddate" id="enddate<%=count %>" value="<%=trainingDataList.get(4) %>" /> <%=trainingDataList.get(2) %>
														</td>
													</tr>

													<tr>
														<td style="text-align: right;" class="txtlabel">Day Schedule</td>
														<td id="dayScheduleTD" style="display: table-cell;" colspan="3">
															<% if(trainingDataList.get(7) != null && trainingDataList.get(7).equals("1")) { %>
															Daily <% } else if(trainingDataList.get(7) != null && trainingDataList.get(7).equals("2")) { %>
															Occasionally <% } %>
														</td>
													</tr>

													<%
													if (alSessionData != null && alSessionData.size() != 0) {
														Map<String, String> hmWeekdays1 = (Map<String, String>) request.getAttribute("hmWeekdays1");	
													%>
														<tr id="weekDaysTR" <%if (trainingDataList.get(7) != null && trainingDataList.get(7).equals("1") && trainingDataList.get(5) != null && trainingDataList.get(5).equals("1")) { %>
															style="display: table-row;" <%} else { %> style="display: none;" <%}%>>
														<td class="txtlabel" align="right" colspan="4">
															<input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>"
																value="Mon" <%=hmWeekdays1.get(trainingList1.get(0)+"_MON") != null ? hmWeekdays1.get(trainingList1.get(0)+"_MON") : "" %>disabled="disabled">Mon 
																
															<input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>"
																value="Tue" <%=hmWeekdays1.get(trainingList1.get(0)+"_TUE") != null ? hmWeekdays1.get(trainingList1.get(0)+"_TUE") : "" %> disabled="disabled">Tue 
															
															<input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>"
																value="Wed" <%=hmWeekdays1.get(trainingList1.get(0)+"_WED") != null ? hmWeekdays1.get(trainingList1.get(0)+"_WED") : "" %> disabled="disabled">Wed
															
															 <input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>"
																value="Thu" <%=hmWeekdays1.get(trainingList1.get(0)+"_THU") != null ? hmWeekdays1.get(trainingList1.get(0)+"_THU") : "" %> disabled="disabled">Thu
																
															 <input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>"
																value="Fri" <%=hmWeekdays1.get(trainingList1.get(0)+"_FRI") != null ? hmWeekdays1.get(trainingList1.get(0)+"_FRI") : "" %> disabled="disabled">Fri 
														
															<input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>"
																value="Sat" <%=hmWeekdays1.get(trainingList1.get(0)+"_SAT") != null ? hmWeekdays1.get(trainingList1.get(0)+"_SAT") : "" %> disabled="disabled">Sat 
																
															<input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>"
															value="Sun" <%=hmWeekdays1.get(trainingList1.get(0)+"_SUN") != null ? hmWeekdays1.get(trainingList1.get(0)+"_SUN") : "" %> disabled="disabled">Sun
														</td>
													</tr>
									<%				for (int j = 0; alSessionData != null && j < alSessionData.size(); j++) {
														List<String> alInner = (List<String>) alSessionData.get(j);
									%>
														<tr id="trainingSchedulePeriodTR<%=i%>"
															<% if (alInner.get(0) != null && alInner.get(0).equals("1") && alInner.get(4) != null && alInner.get(4).equals("2")) {%>
																style="display: table-row;" <%} else { %> style="display: none;" <%}%>>
															
															<td class="txtlabel" style="text-align: right">
																<input type="hidden" name="hideScheduleType" id="hideScheduleType" value="<%=alInner.get(4) %>" />
																<span style="float: left; margin-left: 50px;">Day <%=j+1 %></span>
																 Select Date:
														   </td>
														   
														   <td>
														   		<input type="text" id="oneTimeDate<%=j%>" name="oneTimeDate" value="<%=alInner.get(1)%>" class=" form-control " />
														  </td>
													</tr>

													<tr id="startTimeTR<%=j%>">
														<td class="txtlabel" style="text-align: right">Start Time:</td>
														<td><input type="hidden" name="starttime" id="starttime<%=count %>" value="<%=alInner.get(2)%>" /> <%=alInner.get(2)%></td>
														<td class="txtlabel" style="text-align: right; width: 100px;">End Time:</td>
														<td><input type="hidden" name="endtime" id="endtime<%=count %>" value="<%=alInner.get(3)%>" /> <%=alInner.get(3)%>
														</td>
													</tr>
													<% } 
													} %>
												</table>
											</div>
										</div>
										<% } %>
								<%
								count++;
							}
						%>
										<div id="stageDiv<%=count %>" style="width: 100%;">
											<div style="float: left; margin-right: 5px; margin-left: 10px; margin-left: 10px; padding: 5px 0 28px 10px; width: 60px; color: #346897; font-size: 14px;">
												<p>
													<b>Stage <%=count %>: </b>
												</p>
											</div>
											<div id="dropBoxStages<%=count %>" class="dropBox" style="float: right; width: 82%;">
												<div id="dropContent<%=count %>">
													<strong> ...drop here</strong>
												</div>
											</div>
										</div>

									</div>
								</div>
								<div class="col-lg-6 col-md-6 col-sm-6" >	<!-- style="overflow-y: auto; max-height: 400px;" -->
									<div class="tdLabelheadingBg" style="float: left; width: 100%;">
										<strong>Learnings</strong>
									</div>
									Drag and drop to left coloumn <br />
									<br />
									<br />
									<br />
									<div>
										<!-- <div style="float: left; width: 100%; text-align: center; margin-bottom: 20px;"><h1>Learnings</h1> Drag and drop to left coloumn </div> -->
										<div id="trainingsDiv" class="leftColumn">
											<div style="text-align: center; background-color: #DDDDDD; font-size: 15px; border-bottom: 1px solid #868686; padding: 6px; color: #444444;">
												<p>
													<b>Classroom Trainings</b>
												</p>
											</div>
									<!-- ===start parvez date: 16-10-2021=== -->
											<div id="dropContentT" style="overflow-y: auto; max-height: 200px;">
									<!-- ===end parvez date: 16-10-2021=== -->
												<% 
													if(trainingList != null && !trainingList.isEmpty()){
														for(int i=0; i<trainingList.size(); i++){
															List<String> innerList = trainingList.get(i);
												%>
															<div class="dragableBox" id="T<%=innerList.get(0) %>">
																<div style="float: left; text-align: left; width: 100%;"><%=innerList.get(1) %></div>
															</div>
														<%
														}
													}
											 %>
											</div>
										</div>

										<div id="coursesDiv" class="leftColumn">
											<div style="text-align: center; background-color: #DDDDDD; font-size: 15px; border-bottom: 1px solid #868686; padding: 6px; color: #444444;">
												<p>
													<b>Courses</b>
												</p>
											</div>
									<!-- ===start parvez date: 16-10-2021=== -->
											<div id="dropContentC" style="overflow-y: auto; max-height: 200px;">
									<!-- ===end parvez date: 16-10-2021=== -->
												<% 
													if(coursesList != null && !coursesList.isEmpty()){
														for(int i=0; i<coursesList.size(); i++){
															List<String> innerList = coursesList.get(i);
												%>
															<div class="dragableBox" id="C<%=innerList.get(0) %>">
																<div style="float: left; text-align: left; width: 100%;"><%=innerList.get(1) %></div>
															</div>
														<%
														}
													}
											 %>
											</div>
										</div>

										<div id="assessmentsDiv" class="leftColumn">
											<div style="text-align: center; background-color: #DDDDDD; font-size: 15px; border-bottom: 1px solid #868686; padding: 6px; color: #444444;">
												<p>
													<b>Assessments</b>
												</p>
											</div>
										<!-- ===start parvez date: 16-10-2021=== -->
											<div id="dropContentA" style="overflow-y: auto; max-height: 200px;">
										<!-- ===end parvez date: 16-10-2021=== -->
												<% 
												if(assessmentList != null && !assessmentList.isEmpty()){
													for(int i=0; i<assessmentList.size(); i++){
													List<String> innerList = assessmentList.get(i);
													%>
														<div class="dragableBox" id="A<%=innerList.get(0) %>">
															<div style="float: left; text-align: left; width: 100%;"><%=innerList.get(1) %></div>
														</div>
													<%
													}
												}
											 %>
											</div>
										</div>
										
										<!-- ===start parvez date: 22-09-2021 -->
										<div id="videosDiv" class="leftColumn">
											<div style="text-align: center; background-color: #DDDDDD; font-size: 15px; border-bottom: 1px solid #868686; padding: 6px; color: #444444;">
												<p>
													<b>Videos</b>
												</p>
											</div>
									<!-- ===start parvez date: 16-10-2021=== -->
											<div id="dropContentV" style="overflow-y: auto; max-height: 200px;">
									<!-- ===end parvez date: 16-10-2021=== -->
												<% 
												if(videoList != null && !videoList.isEmpty()){
													for(int i=0; i<videoList.size(); i++){
													List<String> innerList = videoList.get(i);
													%>
														<div class="dragableBox" id="V<%=innerList.get(0) %>">
															<div style="float: left; text-align: left; width: 100%;"><%=innerList.get(1) %></div>
														</div>
													<%
													}
												}
											 %>
											</div>
										</div>
									<!-- ===end parvez date: 22-09-2021=== -->
									</div>
								</div>
							</div>

							<div style="width: 100%; float: right;margin-top: 20px;">
								<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="savePublish" value="Save & Publish" />
								<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSubmit" value="Submit & Proceed" />
								<%if(op.equals("E")) { %>
								<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save & Exit" />
								<% } %>
								
							</div>

						</s:form>

					</s:if>

					<s:if test="step==3 || mode=='report'">
				<%
					List<List<String>> feedbackQueList = (List<List<String>>)request.getAttribute("feedbackQueList");
				%>
						<div style="float: left; width: 100%;">
							<table border="0" class="formcss" style="width: 100%;">
								<tr>
									<td class="tdLabelheadingBg" colspan="2">
									<span style="color: #68AC3B; font-size: 18px; padding: 5px;">Step 3 :</span>Learning Plan Feedback</td>
								</tr>
								<tr>
									<td height="10px">&nbsp;</td>
								</tr>

							</table>
							<%if(feedbackQueList != null && !feedbackQueList.isEmpty()) { %>
								<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 99.9%;">
									<table class="table" style="width: 100%; float: left;">
									<tr>
										<td width="90%"><b>Question</b></td>
									</tr>
									<%
									//double totWeightage = 0;
									//totWeightage = 0;	
									//List<List<String>> goalList = scoreMp.get(innerList1.get(0));
									int newquecnt= feedbackQueList != null ? feedbackQueList.size()+1 : 1 ;
									for (int k = 0; feedbackQueList != null && k < feedbackQueList.size(); k++) {
										List<String> innerList = feedbackQueList.get(k);
										//totWeightage += uF.parseToDouble(innerList.get(2));
									}
									String anstype=null;
									for (int k = 0; feedbackQueList != null && k < feedbackQueList.size(); k++) {
										List<String> innerList = feedbackQueList.get(k);
						%>
									<tr>
										<td>
											<span style="float: left;"><%=k+1 %>)&nbsp;<%=innerList.get(1) %></span>
											<span style="float: left; margin-left: 10px;">
												<%-- <a id="editexist<%=k%>" href="javascript:void(0)" class="edit_lvl"
													onclick="openEditFeedbackQue('<%=innerList.get(3) %>','<%=innerList.get(0) %>','<%=k+1 %>','E','2','<%=innerList.get(4) %>'); "
													title="Edit Exist">Edit
												</a> --%>
												<a id="editexist<%=k%>" href="javascript:void(0)" onclick="openEditFeedbackQue('<%=innerList.get(3) %>','<%=innerList.get(0) %>','<%=k+1 %>','E','2','<%=innerList.get(4) %>'); " title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
												<%if(boolPublished == false) { %> 
													<a title="Delete" href="javascript:void(0)" onclick="deleteLearningFBQuestion('<%=innerList.get(3) %>','<%=innerList.get(0) %>','E','2')"><i class="fa fa-trash" aria-hidden="true"></i></a>
													<%-- <a class="del" title="Delete" href="DeleteLearningFeedbackQuestion.action?ID=<%=innerList.get(3) %>&queID=<%=innerList.get(0) %>&operation=E&step=2"
													onclick="return confirm('Are you sure, you want to delete this Question?')"></a> --%>
											    <%} %>
											</span>
										</td>
										
									</tr>
									<%
							   } 
						%>
									<tr>
										<td colspan="2">
											<span> 
												<a href="javascript:void(0)" class="add_lvl" onclick="openNewQue('<%=newquecnt %>');">Add Question</a> 
											</span>
										</td>
									</tr>
								</table>

								 <s:form action="AddQuestionOfLearningFeedback" id="frmAddQuestionOfLearningFeedback" name="frmAddQuestionOfLearningFeedback" method="POST" theme="simple">

									<div id="newquediv<%=newquecnt %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">

										<s:hidden name="planId" id="hiddenplanId"></s:hidden>
										<s:hidden name="operation"></s:hidden>
										<s:hidden name="ID"></s:hidden>
									
										<input type="hidden" name="step" id="step" value="2" />
										<ul class="ul_class" style="margin-left: 0px;">
											<li style="margin-left: 0px;">
												<table class="table" width="100%">
													<tr>
														<th><%=newquecnt %>)</th>
														<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
														 <input type="hidden" name="queCount" id="queCount" value="<%=newquecnt %>" />
														</th>
														<td colspan="3">
															<span id="newquespan<%=newquecnt %>" style="float: left;">
																<input type="hidden" name="hidequeid" id="hidequeid<%=newquecnt %>" value="0" />
																<textarea rows="2" name="question" id="question<%=newquecnt %>" class="validateRequired form-control " style="width: 330px;"></textarea>
															 </span> 
															 
															 <span style="float: left; margin-left: 10px;">
															 	<input type="hidden" name="orientt" value="0" /> 
															</span>
															
															 <span style="float: left; margin-left: 10px;">
															 	<a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=newquecnt %>','');"> +Q </a> 
															 </span> 
															 
															 <span id="checkboxspan" style="float: left; margin-left: 10px;">
															 	<input name="addFlag" type="checkbox" id="addFlag" title="Add to Question Bank" onclick="changeStatus('<%=newquecnt %>');" /> 
															 	<input type="hidden" id="status" name="status<%=newquecnt %>" value="0" />
															  </span>
														</td>
													</tr>

													<tr>
														<th>&nbsp;</th>
														<th>Select Answer Type</th>
														<td width="280px">
															<select name="ansType" class=" form-control " id="ansType<%=newquecnt %>"
																onchange="showAnswerTypeDiv(this.value,'<%=newquecnt %>','answerType<%=newquecnt %>','answerType1<%=newquecnt %>');"><%=request.getAttribute("anstype") %>
															</select>
														</td>
														<td colspan="2">
															<div id="anstypediv<%=newquecnt %>">
																<div id="anstype9">
																	a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled" />
																	b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled" /><br />
																    c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled" /> 
																    d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled" /><br />
																</div>
															</div>
														</td>
													</tr>

													<tr id="answerType<%=newquecnt %>">
														<th>&nbsp;</th>
														<th>&nbsp;</th>
														<td>
															a)<input type="text" name="optiona" class=" validateRequired form-control " />
																<input type="checkbox" value="a" name="correct<%=newquecnt %>" />
														</td>
														<td colspan="2">
															b)<input type="text" name="optionb" class=" validateRequired form-control " /> 
															  <input type="checkbox" name="correct<%=newquecnt %>" value="b" />
														</td>
													</tr>
													<tr id="answerType1<%=newquecnt %>">
														<th>&nbsp;</th>
														<th>&nbsp;</th>
														<td>
															c)<input type="text" name="optionc" class="validateRequired  form-control " />
															  <input type="checkbox" name="correct<%=newquecnt %>" value="c" />
														</td>
														<td colspan="2">
															d)<input type="text" name="optiond" class=" validateRequired form-control " />
															  <input type="checkbox" name="correct<%=newquecnt %>" value="d" />
													   </td>
													</tr>
												</table>
											</li>
										</ul>
									</div>
									<div id="savebtndiv<%=newquecnt %>" style="display: none" align="center">

										<s:submit value="Save" cssClass="btn btn-primary" name="submit" id="submitBtn"></s:submit>
										<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('savebtndiv<%=newquecnt %>','newquediv<%=newquecnt %>');" />
									</div>
								</s:form>

							</div>
							<script>
								$("#frmAddQuestionOfLearningFeedback").submit(function(event){
									event.preventDefault();
									var form_data = $("#frmAddQuestionOfLearningFeedback").serialize();
									$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
									   $.ajax({
								  		url: 'AddQuestionOfLearningFeedback.action',
								  		data: form_data,
								  	    success: function(result){
								  	    	$('#divResult').html(result);
								  		}
								    });
								});
							</script>
							<s:form theme="simple" action="AddLearningPlan" id="frmAddLearningPlan3" method="POST" cssClass="formcss" enctype="multipart/form-data">

								<s:hidden name="planId" id="hiddenplanId"></s:hidden>
								<s:hidden name="operation"></s:hidden>
								<s:hidden name="ID"></s:hidden>
								<s:hidden name="step"></s:hidden>
								<s:hidden name="alignedwith"></s:hidden>
								<s:hidden name="trainingType"></s:hidden>

								<div style="width: 100%; float: right;margin-top: 20px;">
									<%-- <s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save And Exit"/> --%>
									<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="savePublish" value="Save & Publish" />
									<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save & Exit" />
									
								</div>
							</s:form>
							<%} %>

							<%
				if(feedbackQueList == null || feedbackQueList.isEmpty()){
				%>
							<s:form theme="simple" action="AddLearningPlan" id="frmAddLearningPlan4" method="POST" cssClass="formcss" enctype="multipart/form-data">

								<s:hidden name="planId" id="hiddenplanId"></s:hidden>
								<s:hidden name="operation"></s:hidden>
								<s:hidden name="ID"></s:hidden>
								<s:hidden name="step"></s:hidden>
								<s:hidden name="alignedwith"></s:hidden>
								<s:hidden name="trainingType"></s:hidden>


								<div>
									<ul class="level_list ul_class">
										<li style="margin-left: 0px;"><a href="javascript:void(0)" class="add_lvl" onclick="getQuestion('0','');">Add Question</a></li>
										<li id="questionLi" style="margin-left: -75px; border: 0px;"></li>
									</ul>
								</div>

								<div style="width: 100%; float: right;margin-top: 20px;">

									<%-- <s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSubmit" value="Submit And Proceed"/> --%>
									<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="savePublish" value="Save & Publish" />
									<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save & Exit" />
									
								</div>

							</s:form>
							<%
				}
				%>
						</div>

					</s:if>

				</div>
			</div>
		</div>
	</div>
	</section>
</div>
</section>

<div id="CreateCertificate"></div>

<script type="text/javascript">

$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	
	$("#f_level").multiselect().multiselectfilter();
	$("#strAttribute").multiselect({
		noneSelectedText: 'Select Something (required)'
	}).multiselectfilter();
	$("#skills").multiselect().multiselectfilter(); 
	$("#strAttribute2").multiselect({
		noneSelectedText: 'Select Something (required)'
	}).multiselectfilter();
	$("#skills2").multiselect().multiselectfilter(); 
});
	$('input[type="submit"]').click(function(){
		//$(".validateRequired").prop('required',true);
		$("#"+this.form.id).find('.validateRequired').filter(':hidden').prop('required',false);
        $("#"+this.form.id).find('.validateRequired').filter(':visible').prop('required',true);
	});

	<%
	String step = (String) request.getAttribute("step");
	if(step.equals("2")){ %>
	var cnt =1;
	var stgCnt = <%=stageList != null ? stageList.size() : 0 %>;
	if(stgCnt > 0){
		cnt = stgCnt + 1;
	}
	//alert("cnt =====> "+cnt);

	// Custom drop actions for <div id="dropBox"> and <div id="leftColumn">
	function dropItems(idOfDraggedItem,targetId,x,y)
	{
		var obj = document.getElementById(idOfDraggedItem);
		var parentId=obj.parentNode.id;
		var a = idOfDraggedItem.substring(0,1);
		// alert("a --->" + a +" targetId ===> "+ targetId); 
		var flag=false;
		if(targetId=='trainingsDiv' && a == 'T') {	// Item dropped on <div id="dropBox">
			var obj = document.getElementById(idOfDraggedItem);
			//alert(obj.parentNode.id);
			//var objParent = obj.parentNode.id;
			if(obj.parentNode.id=='dropContentT')return;
			//removeCurrentDiv(obj.parentNode.id);
			document.getElementById('dropContentT').appendChild(obj);	// Appending dragged element as child of target box
			//removeCurrentDiv(objParent);
			obj.style.backgroundColor='#81b73a'; //68AC3B
			obj.style.color='#000000'; //ffffff
			flag = true;
		} else if(targetId=='coursesDiv' && a == 'C') {	// Item dropped on <div id="dropBox">
			var obj = document.getElementById(idOfDraggedItem);
			//alert(obj.parentNode.id);
			//var objParent = obj.parentNode.id;
			if(obj.parentNode.id=='dropContentC')return;
			//removeCurrentDiv(obj.parentNode.id);
			document.getElementById('dropContentC').appendChild(obj);	// Appending dragged element as child of target box
			//removeCurrentDiv(objParent);
			obj.style.backgroundColor='#68AC3B'; //68AC3B
			obj.style.color='#000000'; //ffffff
			flag = true;
		} else if(targetId=='assessmentsDiv' && a == 'A') {	// Item dropped on <div id="dropBox"> 
			var obj = document.getElementById(idOfDraggedItem);
			//alert(obj.parentNode.id);
			//var objParent = obj.parentNode.id;
			if(obj.parentNode.id=='dropContentA')return;
			//removeCurrentDiv(obj.parentNode.id);
			document.getElementById('dropContentA').appendChild(obj);	// Appending dragged element as child of target box
			//removeCurrentDiv(objParent);
			obj.style.backgroundColor='#81b73a'; //68AC3B
			obj.style.color='#000000'; //ffffff
			flag = true;
	/* ===start parvez date: 27-09-2021=== */		
		}else if(targetId=='videosDiv' && a == 'V'){ // Item dropped on <div id="dropBox"> 
			var obj = document.getElementById(idOfDraggedItem);
			//alert(obj.parentNode.id);
			//var objParent = obj.parentNode.id;
			if(obj.parentNode.id=='dropContentV')return;
			//removeCurrentDiv(obj.parentNode.id);
			document.getElementById('dropContentV').appendChild(obj);	// Appending dragged element as child of target box
			//removeCurrentDiv(objParent);
			obj.style.backgroundColor='#81b73a'; //68AC3B 
			obj.style.color='#000000'; //ffffff 
			flag = true;
		}else{
	/* ===end parvez date: 27-09-2021=== */
	
			for(var i=1; i<=cnt; i++){
				//var i = j+1;
				if(targetId=='dropBoxStages'+i){
					//alert(targetId);
					var obj = document.getElementById(idOfDraggedItem);
					//alert(obj.parentNode.id);
					var targetObj = document.getElementById('dropContent'+i);	// Creating reference to target obj
					var subDivs = targetObj.getElementsByTagName('DIV');
					if(subDivs.length>0 || obj.parentNode.id=='dropContent'+i)return;
					document.getElementById('dropContent'+i).innerHTML = "";
					document.getElementById('dropContent'+i).appendChild(obj);	// Appending dragged element as child of target box
					addNewDiv(idOfDraggedItem, targetId);
					obj.style.background='none #346897'; //346897
					obj.style.color='#ffffff'; //ffffff
					flag = true;
				}
			}
	
		}
	/*alert("flag ---> "+flag+" parentId ===> "+ parentId); */
	
	/* ===start parvez date: 27-09-2021=== */
	/* if(parentId!='dropContentT' && parentId!='dropContentC' && parentId!='dropContentA' && flag == true) { */
	if(parentId!='dropContentT' && parentId!='dropContentC' && parentId!='dropContentA' && parentId!='dropContentV' && flag == true) { 
		//alert(parentId);
		removeCurrentDiv(parentId);
	}
	/* ===end parvez date: 27-09-2021=== */
}

function onCancelDimension(id,parentId,targetId){
	//alert(id);
	//alert(parentId);
	//alert(targetId);
	var obj = document.getElementById(parentId);
	var row_document = document.getElementById('col_prev_employer'); 
	obj.removeChild(row_document);
	if(targetId==''){
		var targetobj = document.getElementById(targetId);
		targetobj.appendChild(obj);
	}else{
		alert("Go");
	}
}


function onDragFunction(cloneId,origId)
{
	//self.status = 'Started dragging element with id ' + cloneId;

	//var obj = document.getElementById(cloneId);
	
}

var dragDropObj = new DHTMLgoodies_dragDrop();

<% 
if(stageList != null && !stageList.isEmpty()){
	for(int i=0; i<stageList.size(); i++){
	List<String> innerList = stageList.get(i);
	String stageID = innerList.get(3).substring(0,1)+innerList.get(1).trim();
	%>
dragDropObj.addSource('<%=stageID %>',true,true,true,false,'onDragFunction');	// Make <div id="box1"> dragable. slide item back into original position after drop
	<% 
		}
		}
	%>
	
<% 
if(trainingList != null && !trainingList.isEmpty()){
	for(int i=0; i<trainingList.size(); i++){
	List<String> innerList = trainingList.get(i);
	String trainingID = "T"+innerList.get(0).trim();
	%>
dragDropObj.addSource('<%=trainingID %>',true,true,true,false,'onDragFunction');	// Make <div id="box1"> dragable. slide item back into original position after drop
	<% 
		}
		}
	%>
	
	<% 
	if(coursesList != null && !coursesList.isEmpty()){
		for(int i=0; i<coursesList.size(); i++){
		List<String> innerList = coursesList.get(i);
		String courseID = "C"+innerList.get(0).trim();
		%>
	dragDropObj.addSource('<%=courseID %>',true,true,true,false,'onDragFunction');	// Make <div id="box1"> dragable. slide item back into original position after drop
	<% 
		}
		}
	%>
	
	<% 
	if(assessmentList != null && !assessmentList.isEmpty()){
		for(int i=0; i<assessmentList.size(); i++){
		List<String> innerList = assessmentList.get(i);
		String assessmentID = "A"+innerList.get(0).trim();
		%>
	dragDropObj.addSource('<%=assessmentID %>',true,true,true,false,'onDragFunction');	// Make <div id="box1"> dragable. slide item back into original position after drop
	<% 
		}
		}
	%>
	
	/* ===start parvez date: 27-09-2021=== */
	<% 
	if(videoList != null && !videoList.isEmpty()){
		for(int i=0; i<videoList.size(); i++){
		List<String> innerList = videoList.get(i);
		String videoID = "V"+innerList.get(0).trim();
		%>
	dragDropObj.addSource('<%=videoID %>',true,true,true,false,'onDragFunction');	// Make <div id="box1"> dragable. slide item back into original position after drop
	<% 
		}
		}
	%>
	/* ===end parvez date: 27-09-2021=== */
	
/* dragDropObj.addSource('box2',true,true,true,false,'onDragFunction');	// Make <div id="box2"> dragable. slide item back into original position after drop
dragDropObj.addSource('box12',true,true,true,false,'onDragFunction'); */	// Make <div id="box4"> dragable. slide item back into original position after drop

dragDropObj.addTarget('trainingsDiv','dropItems');	// Set <div id="dropBox"> as a drop target. Call function dropItems on drop
dragDropObj.addTarget('coursesDiv','dropItems');	// Set <div id="dropBox"> as a drop target. Call function dropItems on drop
dragDropObj.addTarget('assessmentsDiv','dropItems');	// Set <div id="dropBox"> as a drop target. Call function dropItems on drop
//===start parvez date: 27-09-2021=== 
dragDropObj.addTarget('videosDiv','dropItems');		// Set <div id="dropBox"> as a drop target. Call function dropItems on drop
//===end parvez date: 27-09-2021=== 

for(var i=0; stgCnt != null && i<=stgCnt; i++){
	var count = i+1;
	var drpstgbx = "dropBoxStages"+count;
	dragDropObj.addTarget(drpstgbx,'dropItems');
}
	
dragDropObj.init();

function addNewDiv(stageTypeAndID, targetId){
	
   	cnt++;
	var divTag = document.createElement("div");
	divTag.id = "stageDiv"+cnt;
	divTag.setAttribute("style", "float:left;width: 100%;");
	divTag.innerHTML = "<div style=\"float: left; margin-right: 5px;margin-left:10px;margin-left:10px;padding:5px 0 28px 10px; width: 70px;color:#346897; font-size: 13px;\"><p><b>Stage "+ cnt +": </b></p></div>"
		+"<div id=\"dropBoxStages"+cnt+"\" class=\"dropBox\" style=\"float: right; width: 82%;\">"
		+"<div id=\"dropContent"+cnt+"\"><strong> ...drop here</strong></div>"
		+"</div>";
	document.getElementById("allStageDiv").appendChild(divTag);
	getStageData(stageTypeAndID, targetId);
		var id='dropBoxStages'+cnt;
		dragDropObj.addTarget(id,'dropItems');
}


function removeCurrentDiv(remove_elem){
	//alert("remove_elem ===> "+remove_elem);
	var obj = document.getElementById(remove_elem);
	var obj1 = document.getElementById(obj.parentNode.id);
		//alert("remove_elem obj.parentNode.id ===> "+obj.parentNode.id);
		//alert("obj1.parentNode.id ===> "+obj1.parentNode.id);
	document.getElementById("allStageDiv").removeChild(document.getElementById(obj1.parentNode.id));
}
	
var planId = '<%=(String)request.getAttribute("planId") %>';

function getStageData(stageTypeAndID, targetId){
	   
	  var cntID = targetId.substring(13, targetId.length);
	  var obj = document.getElementById(targetId);
	  var obj1 = document.getElementById(obj.parentNode.id);
	  var parentId = obj.parentNode.id;
		//alert("parentId ---> " + parentId); 
	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetStageDetails.action?stageTypeAndID=" +stageTypeAndID+"&count="+cntID+"&planId="+planId,
				cache : false,
				success : function(data) {
					//alert("data ===> "+data);
					var divTag = document.createElement("div");
					divTag.id = "stageDataDiv"+cntID;
					divTag.setAttribute("style", "width: 100%;");
					divTag.innerHTML = data;
					document.getElementById(parentId).appendChild(divTag);
					$(function() {
						var date_yest = new Date();
					    var date_tom = new Date();
					    date_yest.setHours(0,0,0);
					    date_tom.setHours(23,59,59); 
					   
						$('input[name=starttime]').datetimepicker({
							format: 'HH:mm',
							minDate: date_yest,
							defaultDate: date_yest
					    }).on('dp.change', function(e){ 
					    	$('input[name=endtime]').data("DateTimePicker").minDate(e.date);
					    });
						
						$('input[name=endtime]').datetimepicker({
							format: 'HH:mm',
							maxDate: date_tom,
							defaultDate: date_tom
					    }).on('dp.change', function(e){ 
					    	$('input[name=starttime]').data("DateTimePicker").maxDate(e.date);
					    });
						/* $("input[name=startdate]").datepicker({format: 'dd/mm/yyyy'});
						$("input[name=enddate]").datepicker({format: 'dd/mm/yyyy'}); */
						/* ===start parvez date: 14-10-2021=== */
						$("input[name=startdate]").datepicker({
					        format: 'dd/mm/yyyy',
					        autoclose: true
					    }).on('changeDate', function (selected) {
					        var minDate = new Date(selected.date.valueOf());
					        $('input[name=enddate]').datepicker('setStartDate', minDate);
					    });
					    
					    $("input[name=enddate]").datepicker({
					    	format: 'dd/mm/yyyy',
					    	autoclose: true
					    }).on('changeDate', function (selected) {
					            var minDate = new Date(selected.date.valueOf());
					            $('input[name=startdate]').datepicker('setEndDate', minDate);
					    });
						/* ===end parvez date: 14-10-2021=== */
						$("input[name=oneTimeDate]").datepicker({format: 'dd/mm/yyyy'});
					}); 
				}
			});
		}
      }
 
 <% } %>

	  var submitActor = null;
	   var submitButtons = $('form').find('input[type=submit]').filter(':visible');
	   $("form").bind('submit',function(event) {
			  event.preventDefault();
			 var step = $("input[name = 'step']").val();
			  if (null === submitActor) {
				
	              // If no actor is explicitly clicked, the browser will
	              // automatically choose the first in source-order
	              // so we do the same here
	              submitActor = submitButtons[0];
	          }
			  var form_data = $("#"+this.id).serialize();
		   	  var stepSubmit=$('input[name = stepSubmit ]').val();
		   	  var stepSave=$('input[name = stepSave ]').val();
		   	  var savePublish = $('input[name = savePublish]').val();
			  var submit = submitActor.name;
		   	 // alert("submit==>"+submit);
			 if((step == "1" && checkFields()) || (step != "1")) {
				 if(submit != null && submit == "stepSave") {
	        	  form_data = form_data +"&stepSave=Save And Exit";
	          } else if(submit != null && submit == "stepSubmit"){
	        	  form_data = form_data +"&stepSubmit=Submit And Proceed";
	          }else if(submit != null && submit == "savePublish"){
	        	  form_data = form_data +"&savePublish=Save And Publish";
	          }
	          $("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
		      $.ajax({
		     		
		     		type: 'POST',
		     		url: "AddLearningPlan.action",
		     		data: form_data,
		     	    success: function(result){
		     	    	//console.log("result==>"+result);
		     			$('#divResult').html(result);
		     		}, 
		 			error : function(err) {
		 				$.ajax({ 
							url: 'LearningPlanDashboard.action',
							cache: true,
							success: function(result){
								$("#divResult").html(result);
					   		}
						});
		 			}
		       });
			 }
				 
			
		     
			});
	   
		   submitButtons.click(function(event) {
		       submitActor = this;
		   }); 
		   
		   function deleteLearningFBQuestion(id,queID,op,step){
			   if(confirm('Are you sure, you want to delete this Question?')) {
				   var action = "DeleteLearningFeedbackQuestion.action?ID="+id+"&queID="+queID+"&operation="+op+"&step="+step;
				   $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				   $.ajax({
			     		url: action,
			     		cache:true,
			     	    success: function(result){
			     	    	$('#divResult').html(result);
			     		}
			       });
			   }
		   }
		   function loadStepOnClick(action){
				 
				  $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				  $.ajax({
			  		url: action,
			  		type: 'GET',
			  		success: function(result){
			  			$("#divResult").html(result);
			  			
			  	    }
			    });
			}
		   
		   function editTraining(strAction){
			   $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				  $.ajax({
			  		url: strAction,
			  		type: 'GET',
			  		success: function(result){
			  			$("#divResult").html(result);
			  			
			  	    }
			    });
			  
		   }
		   
</script>

<div id="SelectQueDiv"></div>
<div id="EditQuestionDiv"></div>
<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">View Information</h4>
			</div>
			<div class="modal-body"
				style="height: 400px; overflow-y: auto; padding-left: 25px;">
			</div>
			<div class="modal-footer">
				<button type="button" id="closeButton" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>