<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script>

$(function(){
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
	
	$("body").on('click','#closeButton1',function(){
		$(".modal-dialog1").removeAttr('style');
		$("#modal-body1").height(400);
		$("#modalInfo1").hide();
    });

	var a = '#from';

	/*$("#from").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#to').datepicker('setStartDate', minDate);
    });
    
    $("#to").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#from').datepicker('setEndDate', minDate);
    });*/
});	
$("#frmCGOMsystemOfQ_submit").click(function(){
	
	$("#frmCGOMsystemOfQ").find('.validateRequired').filter(':hidden').prop('required',false);
	$("#frmCGOMsystemOfQ").find('.validateRequired').filter(':visible').prop('required',true);
});

$("#frmCGOMsystemOfM_submit").click(function(){
	
	$("#frmCGOMsystemOfM").find('.validateRequired').filter(':hidden').prop('required',false);
	$("#frmCGOMsystemOfM").find('.validateRequired').filter(':visible').prop('required',true);
});

$("#frmCGOMsystemOfO_submit").click(function(){
	
	$("#frmCGOMsystemOfO").find('.validateRequired').filter(':hidden').prop('required',false);
	$("#frmCGOMsystemOfO").find('.validateRequired').filter(':visible').prop('required',true);
});

$("#frmCGOMsystemOfG_submit").click(function(){
	
	$("#frmCGOMsystemOfG").find('.validateRequired').filter(':hidden').prop('required',false);
	$("#frmCGOMsystemOfG").find('.validateRequired').filter(':visible').prop('required',true);
});

$("#frmCGOMsystemOfS_submit").click(function(){
	
	$("#frmCGOMsystemOfS").find('.validateRequired').filter(':hidden').prop('required',false);
	$("#frmCGOMsystemOfS").find('.validateRequired').filter(':visible').prop('required',true);
});

	CKEDITOR.on( 'instanceReady', function( ev ) {
			if(document.getElementById( 'eMessage' )) {
				document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
			}	
			if(document.getElementById( 'eButtons' )) { 
				document.getElementById( 'eButtons' ).style.display = 'block';
			}	
		});
		function InsertHTML() {
			var editor = CKEDITOR.instances.editor1;
			var value = document.getElementById( 'htmlArea' ).value;
			if (editor.mode == 'wysiwyg') {
				editor.insertHtml(value);
			} else{
				alert( 'You must be in WYSIWYG mode!' );
			}
		}
		function InsertText() {
			var editor = CKEDITOR.instances.editor1;
			var value = document.getElementById( 'txtArea' ).value;
			if ( editor.mode == 'wysiwyg'){
				editor.insertText( value );
			}
			else {
				alert( 'You must be in WYSIWYG mode!' );
			}
		}
		
		function SetContents() {
			var editor = CKEDITOR.instances.editor1;
			var value = document.getElementById( 'htmlArea' ).value;
			editor.setData( value );
		}
		
		function GetContents() {
			var editor = CKEDITOR.instances.editor1;
			alert( editor.getData() );
		}
		
		function ExecuteCommand( commandName ) {
			var editor = CKEDITOR.instances.editor1;
			if ( editor.mode == 'wysiwyg' ) {
				editor.execCommand( commandName );
			}
			else {
				alert( 'You must be in WYSIWYG mode!' );
			}
		}
		
		function CheckDirty() {
			var editor = CKEDITOR.instances.editor1;
			alert( editor.checkDirty() );
		}
		
		function ResetDirty() {
			var editor = CKEDITOR.instances.editor1;
			editor.resetDirty();
			alert( 'The "IsDirty" status has been reset' );
		}
		
		function Focus() {
			CKEDITOR.instances.editor1.focus();
		}
		
		function onFocus() {
			if(document.getElementById( 'eMessage' )) {
				document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
			}
		}
		
		function onBlur() {
			if(document.getElementById( 'eMessage' )) {
				document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
			}
		}



var dialogEdit = '#SelectQueDiv';
	function openQuestionBank(count,callFrom) {
		//alert("open questionbank");
		var ansType="";
		if(callFrom == "addSectionSubsection"){
			ansType=document.getElementById('ansTypeAddSAndSubS').value;
		} else if(callFrom == "editQue"){
			if(document.getElementById('othrqueanstype'+count) != null){
				ansType=document.getElementById('othrqueanstype'+count).value;
			}
			if(document.getElementById('queanstype'+count) != null){
				ansType=document.getElementById('queanstype'+count).value;
			}
		} else if(callFrom == "addQue"){
			if(document.getElementById('othrqueanstype'+count) != null){
				ansType=document.getElementById('othrqueanstype'+count).value;
			}
			if(document.getElementById('queanstype'+count) != null){
				ansType=document.getElementById('queanstype'+count).value;
			}
		} else{
			if(document.getElementById('ansType') != null){
				ansType=document.getElementById('ansType').value;
			}
		}
		var dialogEdit = '#modal-body1';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo1").show();
		$('.modal-title1').html('Question Bank');
		$.ajax({
			url : "SelectQuestion.action?count="+count+"&ansType="+ansType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	function openEditAppraisal(id,appsystem, appFreqId,fromPage) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html('Edit Review');
		/* if($(window).width() >= 900){
			$(".modal-dialog1").addClass("width900imp");
		} */
		
		var height = $(window).height()* 0.99;
		var width = $(window).width()* 0.99;
		$(".modal-dialog1").css("height", height);
		$(".modal-dialog1").css("width", width);
		$(".modal-dialog1").css("max-height", height);
		$(".modal-dialog1").css("max-width", width);
		
		$.ajax({
			url : "EditAppraisalPopUp.action?id="+id+"&appsystem="+appsystem+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

	var dialogEdit1 = '#EditSectionDiv';
	function openEditSection(id,sID,sNO,type,oreinteId,value,sectionName,totWeightage,sectionID,appFreqId,fromPage) {
		
		var titType = "";
				if(type == "section"){
					titType = "Section";
				}else{
					titType = "Subsection";
				} 
				var dialogEdit = '.modal-body';
				$(dialogEdit).empty();
				$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$("#modalInfo").show();
				$('.modal-title').html('Edit '+titType);
				if($(window).width() >= 900){
					$(".modal-dialog").width(900);
				}
				$.ajax({
					url : "EditAppraisalSectionAndSubsectionPopUp.action?id="+id+"&sID="+sID+"&sNO="+sNO+"&type="+type+"&oreinteId="
							+oreinteId+"&sectionName="+sectionName+"&totWeightage="+totWeightage+"&sectionID="+sectionID
							+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
	}
	

	function setQuestionInTextfield() {
		var queid = document.getElementById("questionSelect").value;
		var count = document.getElementById("count").value;
        xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
                alert("Browser does not support HTTP Request");
                return;
        } else {
                var xhr = $.ajax({
                        url : "SetQuestionToTextfield.action?queid=" + queid + '&count=' +count,
                        cache : false,
                        success : function(data) {
                       	if(data != "" && data.trim().length > 0){
                       		var allData = data.split("::::");
                            document.getElementById("newquespan"+count).innerHTML = allData[0];
                            document.getElementById("answerType"+count).innerHTML = allData[1];
                            if(allData.length > 3) {
                           		document.getElementById("answerType1"+count).style.display = 'table-row';
                           		document.getElementById("answerType1"+count).innerHTML = allData[2];
                           		document.getElementById("answerType2"+count).style.display = 'table-row';
                           		document.getElementById("answerType2"+count).innerHTML = allData[3];
                           	} else if(allData.length > 2) {
                           		document.getElementById("answerType1"+count).style.display = 'table-row';
                           		document.getElementById("answerType1"+count).innerHTML = allData[2];
                           		document.getElementById("answerType2"+count).style.display = 'none';
                           	} else {
                           		document.getElementById("answerType1"+count).style.display = 'none';
                           		document.getElementById("answerType2"+count).style.display = 'none';
                           	}
                       	}
					}
                });
        }
        $("#modalInfo1").hide();
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
	
	function closePopup(){
		$(dialogEdit).dialog('close');
	}
	function closeEditPopup(){
		$(dialogEdit1).dialog('close');
	}
	
	function openOtherSystemNewQue(value, totWeightage) {
		
		 var remainWeightage = 100 - parseFloat(totWeightage);
		 if(parseInt(remainWeightage) <= 0){
			 alert("Unable to add questions because of no weightage available.");
		 } else {
			 //alert("val 0 ===>> " + val);
			document.getElementById("weightage"+value).value=remainWeightage;
			document.getElementById("hideweightage"+value).value=remainWeightage;
			document.getElementById("OTHERnewquedivOfQ"+value).style.display="block";
			document.getElementById("OTHERsavebtndivOfQ"+value).style.display="block";
			//alert("val 1 ===>> " + val);
		 }
	}

	
function openAddNewLevel(id,sysdiv,newlvlno,totWeightage,orientation,appFreqId,fromPage) {
	//alert(fromPage);
	var remainWeightage = 100 - parseFloat(totWeightage);

//===start parvez date: 15-12-2021=== 
	/* if(parseInt(remainWeightage) <= 0){ */
	if(parseInt(remainWeightage) < 0){
//===end parvez date: 15-12-2021=== 
		 alert("Unable to add section because of no weightage available ");
	 }else{
		 console.log(sysdiv);
		 $("#"+sysdiv).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 document.getElementById(sysdiv).style.display="block";
		 var action = 'openAppraisalLevelAndSystem.action?id='+id+'&sysdiv='+sysdiv+'&newlvlno='+newlvlno+"&weightage="+remainWeightage
				 +"&oreinted="+orientation+"&appFreqId="+appFreqId+"&fromPage="+fromPage;
		 getContent(sysdiv, action);
    }
}

function openAddNewSystem(id,MLID,type,sysdiv,newsysno,totWeightage,linkType,linkDiv,divCount,orientation,appFreqId,fromPage) {
	var remainWeightage = 100 - parseFloat(totWeightage);
	 if(parseInt(remainWeightage) <= 0){
		 alert("Unable to add subsection because of no weightage available ");
	 }else{
		 if(linkType == 'section'){
			 document.getElementById("sectionLinkSpan"+divCount).style.display="none";
			 document.getElementById("assessLinkSpan"+divCount).style.display="block"; 
		 }else{
			 document.getElementById("assessLinkSpan"+divCount).style.display="nonse";
			 document.getElementById("sectionLinkSpan"+divCount).style.display="block";
		 }
		 document.getElementById(sysdiv).style.display="block";
		 $("#"+sysdiv).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		var action = 'openAppraisalLevelAndSystem.action?id='+id+'&MLID='+MLID+'&type='+type+'&sysdiv='+sysdiv+'&newsysno='+newsysno
				+"&subWeightage="+remainWeightage+"&linkDiv="+linkDiv+"&divCount="+divCount+"&linkType="+linkType+"&oreinted="+orientation
				+"&appFreqId="+appFreqId+"&fromPage="+fromPage;
		getContent(sysdiv, action);
	 }
}

	var dialogEdit2 = '#EditQuestionDiv';
	function openOtherSystemEditQue1(id,appsystem,scoreType,editID,type,quediv,othrquetype,queno,ansid,selectanstype,totWeightage,sectionID,subsectionID,appFreqId,fromPage) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Edit Question');
		$.ajax({
			url : "getEditQuestionDetails.action?id=" + id + "&appsystem=" + appsystem + "&scoreType=" + scoreType + "&editID=" + editID + 
					"&type=" + type + "&quediv=EditQuestionDiv" + "&othrquetype=" + othrquetype + "&queno=" + queno + "&ansid=" + ansid + 
					"&selectanstype=" + selectanstype + "&totWeightage=" + totWeightage + "&sectionID=" + sectionID + "&subsectionID=" + subsectionID
					+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

	
	function importSectionSubsectionPopup(reviewId,orientation,appFreqId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Import Review Section');
		$(".modal-body").height('auto');
		$.ajax({
			url : "ImportReviewSectionSubsection.action?reviewId="+reviewId+"&orientation="
					+ orientation+"&appFreqId="+appFreqId+"&importType=Section",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	function importRevieweePopup(reviewId,orientation,appFreqId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Import Reviewee');
		$(".modal-body").height('auto');
		$.ajax({
			url : "ImportReviewSectionSubsection.action?reviewId="+reviewId+"&orientation="
					+ orientation+"&appFreqId="+appFreqId+"&importType=Reviewee",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	

var dialogEdit3 = '#EditBalanceCardDiv';
function openOtherSystemEditQue(id,appsystem,scoreType,editID,type,quediv,ansid,selectanstype,totWeightage,queno,sectionID,subsectionID,appFreqId,fromPage) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Edit Balance Score Card/Question');
	 $.ajax({
			url : "getEditQuestionDetails.action?id=" + id + "&appsystem=" + appsystem + "&scoreType=" + scoreType + "&editID=" + editID
				+ "&type=" + type + "&quediv=EditBalanceCardDiv" + "&ansid=" + ansid + "&selectanstype=" + selectanstype 
				+ "&totWeightage=" + totWeightage + "&queno=" + queno + "&sectionID=" + sectionID + "&subsectionID=" + subsectionID
				+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}

function closeEditDiv(quediv){
	if(quediv == "EditBalanceCardDiv"){
		$(dialogEdit3).dialog('close');
	}else{
		$(dialogEdit2).dialog('close');
	}
}

function closeEditDiv1(quediv,linkDiv){
	document.getElementById(quediv).style.display="none";
	document.getElementById(linkDiv).style.display="block";
}

function closeDiv(btndiv,quediv){
	document.getElementById(quediv).style.display="none";
	document.getElementById(btndiv).style.display="none";
}

function addNewQuestion(id, val, cnt1) {
	if (val == '0') {
		document.getElementById("QuestionName" + cnt1).style.display = 'table-row';
		document.getElementById("AddQuestion" + cnt1).style.display = 'table-row';
		document.getElementById("WeightageIn" + cnt1).style.display = 'table-row';
		document.getElementById("selectanstype" + cnt1).style.display = 'table-row';
		document.getElementById("answerType" + cnt1).style.display = 'table-row';
		document.getElementById("answerType1" + cnt1).style.display = 'table-row';
		document.getElementById("Weightage" + cnt1).style.display = 'none';
	} else {
		document.getElementById("Weightage" + cnt1).style.display = 'table-row';
		document.getElementById("QuestionName" + cnt1).style.display = 'none';
		document.getElementById("AddQuestion" + cnt1).style.display = 'none';
		document.getElementById("WeightageIn" + cnt1).style.display = 'none';
		document.getElementById("selectanstype" + cnt1).style.display = 'none';
		document.getElementById("answerType" + cnt1).style.display = 'none';
		document.getElementById("answerType1" + cnt1).style.display = 'none';
	}
}

function addNewQuestionByLink(id, val, cnt1, selectindex) {
		if (val == '0') { 
			document.getElementById("QuestionName" + cnt1).style.display = 'table-row';
			document.getElementById("AddQuestion" + cnt1).style.display = 'table-row';
			document.getElementById("WeightageIn" + cnt1).style.display = 'table-row';
			document.getElementById("selectanstype" + cnt1).style.display = 'table-row';
			document.getElementById("answerType" + cnt1).style.display = 'table-row';
			document.getElementById("answerType1" + cnt1).style.display = 'table-row';
			document.getElementById("Weightage" + cnt1).style.display = 'none';
			document.getElementById("addnewquespan" + cnt1).style.display = 'none';
			document.getElementById("existquespan" + cnt1).style.display = 'block';
			document.getElementById("questionSelect" + cnt1).selectedIndex = selectindex;
		} else {
			document.getElementById("Weightage" + cnt1).style.display = 'table-row';
			document.getElementById("QuestionName" + cnt1).style.display = 'none';
			document.getElementById("AddQuestion" + cnt1).style.display = 'none';
			document.getElementById("WeightageIn" + cnt1).style.display = 'none';
			document.getElementById("selectanstype" + cnt1).style.display = 'none';
			document.getElementById("answerType" + cnt1).style.display = 'none';
			document.getElementById("answerType1" + cnt1).style.display = 'none';
			document.getElementById("addnewquespan" + cnt1).style.display = 'block';
			document.getElementById("existquespan" + cnt1).style.display = 'none';
			document.getElementById("questionSelect" + cnt1).selectedIndex = selectindex;
		}
	}

function changeStatus(id) {
	if (document.getElementById('addFlag' + id).checked == true) {
		document.getElementById('status' + id).value = '1';
	} else {
		document.getElementById('status' + id).value = '0';
	}
}

function showAnswerTypeDiv(ansType) {
	var action = 'ShowAnswerType.action?ansType=' + ansType;
	getContent("anstypedivAdd", action);
}

function showAnswerTypeDiv1(ansType) {
	var action = 'ShowAnswerType.action?ansType=' + ansType;
	getContent("anstypediv", action);
}

function validateScore(value1,weightageid,weightagehideid) {
	var remainWeightage = document.getElementById(weightagehideid).value;
	  
	  if(parseFloat(value1) > parseFloat(remainWeightage)){
			alert("Entered value greater than Weightage");
			document.getElementById(weightageid).value = remainWeightage;
//===start parvez date: 14-01-2022===		
	  /* }else if(parseFloat(value1) <= 0 ){ */
		}else if(parseFloat(value1) < 0 ){
//===end parvez date: 14-01-2022===		  
			alert("Invalid Weightage");
			document.getElementById(weightageid).value = remainWeightage;
		}  
}

function validateScoreEdit(value,weightageid,weightagehideid,totweightage) {
	var singleWeightage = document.getElementById(weightagehideid).value;
	var othertotweight = parseFloat(totweightage) - parseFloat(singleWeightage);
	var remainWeightage = 100 - parseFloat(othertotweight);
	if(parseFloat(value) > parseFloat(remainWeightage)){
		alert("Entered value greater than Weightage");
		document.getElementById(weightageid).value = remainWeightage;
//===start parvez date: 14-01-2022===	
	/* }else if(parseFloat(value) <= 0 ){ */
	}else if(parseFloat(value) < 0 ){	
//===end parvez date: 14-01-2022===	
		alert("Invalid Weightage");
		document.getElementById(weightageid).value = remainWeightage;
	}
}


function isNumberKey(evt){
  var charCode = (evt.which) ? evt.which : event.keyCode;
  if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
     return false;
  }
  return true;
}

function getQuestoinContentType(cnt){
	//alert("getQuestoinContentType ");
	var val = document.getElementById("ansType").value;
	//alert("getQuestoinContentType val : "+val);
	var a="";
	if( val == 8){
		a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
		+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
	
	} else if (val == 1 || val == 2 || val == 9) {
		a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<span id=\"aspan\"><input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<span id=\"bspan\"><input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
		+"<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<span id=\"cspan\"><input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<span id=\"dspan\"><input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";

	 }else if (val == 6) {
		a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
			+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td></tr>";
	
	}else if (val == 5) {
		a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td></tr>";
	}  else if(val == 13) {
		a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiona\" id=\"rateoptiona"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
		+"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionb\" id=\"rateoptionb"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td></tr>"
		+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionc\" id=\"rateoptionc"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
		+"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiond\" id=\"rateoptiond"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td></tr>"
		+ "<tr id=\"answerType2"+cnt+"\"><th></th><th></th><td>e)&nbsp;<input type=\"text\" name=\"optione\" id=\"optione"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptione\" id=\"rateoptione"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
		+"<td colspan=\"2\">&nbsp;</td></tr>";
		
	} else {
		a="";
	}
	return a;
}

function isOnlyNumberKey(evt) {
	var charCode = (evt.which) ? evt.which : event.keyCode;
	if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
		return true; 
	}
	return false;
}

function addQuestionSystemOther(formID,appId,appFreqId,fromPage){
//	alert("updateMyReview jsp ==appId==>"+appId+"==>appFreqId==>"+appFreqId);
	var form_data = $("#"+formID).serialize();
	$.ajax({ 
		type : 'POST',
	//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
		url: "AddQuestionSystemOther.action",
		data: form_data+"&submit=Save",
		cache: true,
		success: function(result){
			getReviewSummary('AppraisalSummary',appId,appFreqId,fromPage);
   		},
		error: function(result){
			getReviewSummary('AppraisalSummary',appId,appFreqId,fromPage);
		}
	});
}


function deleteReviewLevelAndSystem(appId,appFreqId,levelID,lvlID,type,fromPage) {
	//alert("type jsp ===>> " + type+"==appId==>"+appId+"==>appFreqId==>"+appFreqId+"==>fromPage==>"+fromPage);
	    var msg = "Are you sure you want to delete this subsection?";
	    if(type != "" && type == "Level") {
	    	msg = "Are you sure you want to delete this section?";
	    }
		if(confirm(msg)) {
			$.ajax({ 
				type : 'POST',
				url: 'DeleteAppraisalLevelAndSystem.action?id='+appId+'&appFreqId='+appFreqId+'&from='+fromPage+'&levelID='+levelID+'&lvlID='+lvlID
						+'&type='+type,
				//data: $("#"+this.id).serialize(),
				cache: true,
				success: function(result){
					//alert("result2==>"+result);
					getReviewSummary('AppraisalSummary',appId,appFreqId,fromPage);
				},
				error: function(result){
					getReviewSummary('AppraisalSummary',appId,appFreqId,fromPage);
				}
			});
		}
	}


function deleteCGOMAndQuestion(appId,appFreqId,queID,type,fromPage) {
	//alert("type jsp ===>> " + type+"==appId==>"+appId+"==>appFreqId==>"+appFreqId+"==>fromPage==>"+fromPage);
		var msg = "Are you sure you want to delete this question?";
		if(type != null && type == "C") {
			msg = "Are you sure you want to delete this Competency?";
		} 
		
		if(type != null && type == "G") {
			msg = "Are you sure you want to delete this Goal?";
		} 
		
		if(type != null && type == "O") {
			msg = "Are you sure you want to delete this Objective?";
		} 
		
		if(type != null && type == "M") {
			msg = "Are you sure you want to delete this Measure?";
		} 
		
		if(confirm(msg)) {
			$.ajax({ 
				type : 'POST',
				url: 'DeleteCGOMAndQuestion.action?id='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&queID='+queID+'&type='+type,
				//data: $("#"+this.id).serialize(),
				cache: true,
				success: function(result){
					//alert("result2==>"+result);
					getReviewSummary('AppraisalSummary',appId,appFreqId,fromPage);
				},
				error: function(result){
					getReviewSummary('AppraisalSummary',appId,appFreqId,fromPage);
				}
			});
		}
	}


function deleteAppraisal(appId, appFreqId, fromPage) {
	if(confirm('Are you sure you want to delete this appraisal?')) {
		//alert("fromPage 00 ===>> " + fromPage);
		$.ajax({
			url: 'DeleteAppraisal.action?id='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage
		});
		//alert("fromPage ===>> " + fromPage);
		
		if(fromPage != null && fromPage == 'MyReview') {
			/*$.ajax({
				url: 'MyHR.action',
				cache: true,
				success: function(result){
					$("#reviewResult").html(result);
		   		}
			});*/
			
			window.location = 'MyHR.action';
		} else {
			//alert("else fromPage ===>> " + fromPage);
			$.ajax({
				url: 'ReviewNamesList.action',
				cache: true,
				success: function(result){
					//alert("result ===>> " + result);
					$("#reviewResult").html(result);
		   		}
			});
		}
		
	}
}


function openAppraisalPreview(id,appFreqId) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Review Preview');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url : "AppraisalPreview.action?id="+id+"&appFreqId="+appFreqId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	

	function getRevieweeAppraisers(reviewId, revieweeId, revieweeName) {
		var pageTitle = "All Reviewee's Appraisers";
		if(revieweeName != '') {
			pageTitle = revieweeName+"'s Appraisers";
		}
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html(pageTitle);
		 $.ajax({
			url : "RevieweeAppraisers.action?reviewId="+reviewId+"&revieweeId="+revieweeId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	 
	}
	
	

function closeReview(reviewId, type,appFreqId,fromPage) {
	var pageTitle = 'Close Review';
	if(type=='view') {
		pageTitle = 'Close Review Reason';
	}
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html(''+pageTitle);
	 $.ajax({
			url : "CloseReview.action?reviewId="+reviewId+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
		
	
	function getPublishAppraisal(id, empId,appFreqId,fromPage) {
		
		//alert("empId==>"+empId);
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
	    } else {
            var xhr = $.ajax({
               url : "PublishAppraisal.action?id=" + id + '&empId='+empId+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
               cache : false,
               success : function(data) {
            	   getReviewSummary('AppraisalSummary',id,appFreqId,fromPage);
               },
	       		error: function(result){
	    			getReviewSummary('AppraisalSummary',appId,appFreqId,fromPage);
	    		}
            });
	    }
	    //$(dialogEdit).dialog('close');
	}
	

</script>