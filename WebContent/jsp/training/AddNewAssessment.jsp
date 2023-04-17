<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

	<style type="text/css">
	/* CSS for the demo. CSS needed for the scripts are loaded dynamically by the scripts */
		
	.txtlbl {
		color: #777777;
	    font-family: verdana,arial,helvetica,sans-serif;
	    font-size: 11px;
	    font-style: normal;
	    font-weight: 600;
	    width: 100px;
	} 
		
	.ui-tabs-hide{
		display: none !important;
	}
	.ul_class li {
		margin: 10px 0px 10px 100px;
	}
			
	.clear{
		clear:both;
	}
	img{
		border:0px;
	}	
	</style>	 
<style>
.ui-state-default{
padding-right: 5px !important;
padding-left: 5px !important;
padding-top: 5px !important;
padding-bottom: 5px !important;
}
.ui-widget {
font-family: 'Source Sans Pro','Helvetica Neue',Helvetica,Arial,sans-serif;
font-weight: 400;
font-size: 14px;
}
.ui-widget input, .ui-widget select, .ui-widget textarea, .ui-widget button {
font-family: 'Source Sans Pro','Helvetica Neue',Helvetica,Arial,sans-serif;
font-weight: 400;
font-size: 14px;
}

a.close-font:before{
 font-size: 24px;
}
</style>
<script src="scripts/ckeditor_cust/ckeditor.js"></script>
<script>

	// The instanceReady event is fired, when an instance of CKEditor has finished
	// its initialization.  
	CKEDITOR.on( 'instanceReady', function( ev ) {
		// Show the editor name and description in the browser status bar.
		if(document.getElementById( 'eMessage' )) {
			document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
		}
	
		// Show this sample buttons.
		if(document.getElementById( 'eButtons' )) {
			document.getElementById( 'eButtons' ).style.display = 'block';
		}
	}); 
	
	function InsertHTML() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		var value = document.getElementById( 'htmlArea' ).value;
	
		// Check the active editing mode.
		if ( editor.mode == 'wysiwyg' )
		{
			// Insert HTML code.
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertHtml
			editor.insertHtml( value );
		}
		else
			alert( 'You must be in WYSIWYG mode!' );
	}
	
	function InsertText() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		var value = document.getElementById( 'txtArea' ).value;
	
		// Check the active editing mode.
		if ( editor.mode == 'wysiwyg'){
			// Insert as plain text.
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertText
			editor.insertText( value );
		} else
			alert( 'You must be in WYSIWYG mode!' );
	}
	
	function SetContents() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		var value = document.getElementById( 'htmlArea' ).value;
	
		// Set editor contents (replace current contents).
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-setData
		editor.setData( value );
	}
	
	function GetContents() {
		// Get the editor instance that you want to interact with.
		var editor = CKEDITOR.instances.editor1;
	
		// Get editor contents
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-getData
		alert( editor.getData() );
	}
	
	function ExecuteCommand( commandName ) {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
	
		// Check the active editing mode.
		if ( editor.mode == 'wysiwyg' ) {
			// Execute the command.
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-execCommand
			editor.execCommand( commandName );
		} else
			alert( 'You must be in WYSIWYG mode!' );
	}
	
	function CheckDirty() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		// Checks whether the current editor contents present changes when compared
		// to the contents loaded into the editor at startup
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-checkDirty
		alert( editor.checkDirty() );
	}
	
	function ResetDirty() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		// Resets the "dirty state" of the editor (see CheckDirty())
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-resetDirty
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

</script>

<script type="text/javascript">
$(document).ready(function() {
	
	/* jQuery("#frmAddNewCourse").validationEngine(); */
	var val = '<%=(String)request.getAttribute("marksGradeStandardDefault") %>';
		//alert("val =====> " +val);
		showGradeStandard(val);
		
		$('#container').tabs({
			fxAutoHeight : true
		});
		
		$('#container').tabs( "option",'active', <%=request.getAttribute("tab")%>);
    		
    });
    
    $(function(){
    	$("input[type='submit']").click(function(){
			//$(".validateRequired").prop('required',true);
			for ( instance in CKEDITOR.instances ) {
	            CKEDITOR.instances[instance].updateElement();
	        }
			$("#"+this.form.id).find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#"+this.form.id).find('.validateRequired').filter(':visible').prop('required',true);
		});
    	
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

var cxtpath='<%=request.getContextPath()%>';
var divcnt = 0;
var questionCnt = 0;
var anstype = '<%=(String) request.getAttribute("anstype")%>';

function showAnswerTypeDiv(ansType, cnt, id, id1, divCnt, matrixHeading) {
	//alert("ansType ===> " + ansType); 
	var action = 'ShowAnswerType.action?ansType=' + ansType;
	getContent("anstypediv"+divCnt+"_"+cnt, action);
	changeNewAnswerType(ansType, cnt, id, id1, divCnt, matrixHeading);
}

function checkValue(id) {
	var value = document.getElementById(id).value;
	if(value == 0 ) {
		alert("Invalid value!");
		document.getElementById(id).value = "";
	}
}

function changeNewAnswerType(val, cnt, id, id1,divCnt, matrixHeading) {
	//alert("id ===> " + id + " id1 ===> " + id1 + " val ===> " + val + " matrixHeading ===> " + matrixHeading);
	 if (val == 8) {
		addQuestionType1(id,cnt,divCnt);
		document.getElementById(id).style.display = 'table-row';
	
		addQuestionType2(id1,cnt,divCnt);
		document.getElementById(id1).style.display = 'table-row';
		
		if(document.getElementById(matrixHeading)) {
			addMatrixHeading(matrixHeading, cnt, divCnt);
			//document.getElementById(matrixHeading).innerHTML ="";
			document.getElementById(matrixHeading).style.display = 'none';
		}
	} else if (val == 1 || val == 2 || val == 9) {
		addQuestionType3(id,cnt,divCnt);
		document.getElementById(id).style.display = 'table-row';
	
		addQuestionType4(id1,cnt,divCnt);
		document.getElementById(id1).style.display = 'table-row';
		if(document.getElementById(matrixHeading)) {
			document.getElementById(matrixHeading).innerHTML ="";
			document.getElementById(matrixHeading).style.display = 'none';
		}
	 } else if (val == 6) {
		addTrueFalseType(id,cnt,divCnt);
		document.getElementById(id).style.display = 'table-row';
		
		document.getElementById(id1).innerHTML ="";
		document.getElementById(id1).style.display = 'none';
		if(document.getElementById(matrixHeading)) {
			document.getElementById(matrixHeading).innerHTML ="";
			document.getElementById(matrixHeading).style.display = 'none';
		}
	} else if (val == 5) {
		addYesNoType(id,cnt,divCnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).innerHTML ="";
		document.getElementById(id1).style.display = 'none';
		if(document.getElementById(matrixHeading)) {
			document.getElementById(matrixHeading).innerHTML ="";
			document.getElementById(matrixHeading).style.display = 'none';
		}
	} else if (val == 14) {
		addMatrixHeading(matrixHeading, cnt, divCnt);
		document.getElementById(matrixHeading).style.display = 'table-row';

		addQuestionType1(id, cnt, divCnt);
		document.getElementById(id).style.display = 'table-row';

		addQuestionType2(id1, cnt, divCnt);
		document.getElementById(id1).style.display = 'table-row';
	} else {
		addQuestionType1(id, cnt, divCnt);
		addQuestionType2(id1, cnt, divCnt);
		if(document.getElementById(matrixHeading)) {
			document.getElementById(matrixHeading).innerHTML ="";
			document.getElementById(matrixHeading).style.display = 'none';
		}
		document.getElementById(id).style.display = 'none';
		document.getElementById(id1).style.display = 'none';
		
	}

}

function addTrueFalseType(id,cnt,divCnt){
	document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"2\"><input type=\"hidden\" name=\"matrixHeading"+divCnt+"\" /><input type=\"hidden\" name=\"optiona"+divCnt+"\"/><input type=\"hidden\" name=\"optionb"+divCnt+"\"/><input type=\"hidden\" name=\"optionc"+divCnt+"\"/><input type=\"hidden\" name=\"optiond"+divCnt+"\"/><input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True"
	+ "<input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+ "\" value=\"0\">False</td>";
}

function addYesNoType(id,cnt,divCnt){
	document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"2\"><input type=\"hidden\" name=\"matrixHeading"+divCnt+"\" /><input type=\"hidden\" name=\"optiona"+divCnt+"\"/><input type=\"hidden\" name=\"optionb"+divCnt+"\"/><input type=\"hidden\" name=\"optionc"+divCnt+"\"/><input type=\"hidden\" name=\"optiond"+divCnt+"\"/><input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes"
	+ "<input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+ "\" value=\"0\">No</td>";
}

function addMatrixHeading(matrixHeading, cnt, divCnt) {
	document.getElementById(matrixHeading).innerHTML = "<th></th><th>Matrix Heading:</th><td colspan=\"3\">&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"text\" name=\"matrixHeading"+divCnt+"\" id=\"matrixHeading"+divCnt+"\" class=\"validateRequired form-control\"/></td>";
}

function addQuestionType1(id,cnt,divCnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)<input type=\"text\" name=\"optiona"+divCnt+"\" id=\"optiona"+divCnt+"\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+divCnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb"+divCnt+"\" id=\"optionb"+divCnt+"\" class=\"validateRequired form-control\" /><input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"b\" /></td>";
}

function addQuestionType2(id1,cnt,divCnt) {
	document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)<input type=\"text\" name=\"optionc"+divCnt+"\" id=\"optionc"+divCnt+"\"  class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond"+divCnt+"\"  id=\"optiond"+divCnt+"\" class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"d\" /></td>";
}

function addQuestionType3(id,cnt,divCnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td><input type=\"hidden\" name=\"matrixHeading"+divCnt+"\" />a)<input type=\"text\" name=\"optiona"+divCnt+"\" id=\"optiona"+divCnt+"\" class=\"validateRequired form-control\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+divCnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb"+divCnt+"\" id=\"optionb"+divCnt+"\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"b\" /></td>";
}

function addQuestionType4(id1,cnt,divCnt) {
	document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)<input type=\"text\" name=\"optionc"+divCnt+"\" id=\"optionc"+divCnt+"\"  class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+divCnt+"_"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond"+divCnt+"\" id=\"optiond"+divCnt+"\" class=\"validateRequired form-control \" /> <input type=\"checkbox\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"d\" /></td>";
}

function changeStatus(id) {
	if (document.getElementById('addFlag' + id).checked == true) {
		document.getElementById('status' + id).value = '1';
	} else {
		document.getElementById('status' + id).value = '0';
	}
}

function getQuestion(oldcnt, callFrom, sectionCnt) {
	//alert("oldcnt ===> " + oldcnt +"==>callFrom==>"+callFrom+ "==> sectionCnt ===> " + sectionCnt);
	if(parseInt(questionCnt) < parseInt(oldcnt) ){
		questionCnt = oldcnt;
	}
	questionCnt++;
//	alert("questionCnt ===> " + questionCnt);
	var cnt=questionCnt;
	var ultag = document.createElement('ul');
	var aa = getQuestoinContentType(cnt,callFrom, sectionCnt);
	
	ultag.id = "questionUl"+sectionCnt+"_"+cnt;
	document.getElementById("questionLi"+sectionCnt).style.display="block";
   
	var a = "<li><table class=\"table sectionfont\" width=\"100%\">"
			+ "<tr><th>"+sectionCnt+"."+questionCnt+")</th><th width=\"17%\" style=\"text-align: right;\">Add Question<sup>*</sup></th>"
			+ "<td colspan=\"3\"><span id=\"newquespan"+sectionCnt+"_"+cnt+"\" style=\"float: left; \"><input type=\"hidden\" name=\"hidequeid"+sectionCnt+"\" id=\"hidequeid"+sectionCnt+"_"+cnt+"\" value=\"0\"/>"
			+"<textarea rows=\"2\" name=\"question"+sectionCnt+"\" id=\"question"+sectionCnt+"_"+cnt+"\" class=\"validateRequired form-control\" style=\"width: 399px !important; height: 110px;\"></textarea>"
			//+"<input type=\"text\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\"/>"
			+"</span>"

			+ "&nbsp;<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt"+sectionCnt+"\" value=\""+cnt+"\"/></span>&nbsp;&nbsp;"
			+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"','"+callFrom+"', '"+sectionCnt+"');\" > +Q </a></span>&nbsp;"
			+"<span id=\"checkboxspan"+sectionCnt+"_"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag"+sectionCnt+"\" type=\"checkbox\" id=\"addFlag"+sectionCnt+"_"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+sectionCnt+"_"+ cnt+ "')\" />"
			+"<input type=\"hidden\" id=\"status"+sectionCnt+"_"+ cnt+ "\" name=\"status"+sectionCnt+"\" value=\"0\"/></span>"
			
			+"<a href=\"javascript:void(0)\" title=\"Add New Question\" onclick=\"getQuestion('"+cnt+"','"+callFrom+"','"+sectionCnt+"')\" ><i class=\"fa fa-plus-circle\"></i></a>&nbsp;&nbsp; "
			+"<a href=\"javascript:void(0)\" title=\"Remove Question\" onclick=\"removeQuestion('questionUl"+sectionCnt+"_"+cnt+"')\" class=\"close-font\"></a>"
			//+"<a href=\"javascript:void(0)\" class=\"add_lvl\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\" ></a>"
			+"<input type=\"hidden\" name=\"questiontypename"+sectionCnt+"\" value=\""+ cnt+"\" /></td></tr>" //+othrQtype
			+"<tr><th></th><th>Select Answer Type<sup>*</sup></th><td><select name=\"ansType"+sectionCnt+"\" class=\"validateRequired form-control\" id=\"ansType"+sectionCnt+"_"+cnt+"\" onchange=\"showAnswerTypeDiv(this.value, '"+cnt+"', 'answerType"+sectionCnt+"_"+cnt+"', 'answerType1"+sectionCnt+"_"+cnt+"', '"+sectionCnt+"', 'matrixHeading"+sectionCnt+"_"+cnt+"');\"> <option value=\"\">Select</option>" +anstype+"</select>"
			+"</td><td><div id=\"anstypediv"+sectionCnt+"_"+cnt+"\"><div id=\"anstype9\">"
			+"a) Option1&nbsp;<input type=\"checkbox\" value=\"a\" name=\"correct\" disabled=\"disabled\"/> b) Option2&nbsp;<input type=\"checkbox\" name=\"correct\" value=\"b\" disabled=\"disabled\"/><br />"
			+"c) Option3&nbsp;<input type=\"checkbox\" value=\"c\" name=\"correct\" disabled=\"disabled\"/> d) Option4&nbsp;<input type=\"checkbox\" name=\"correct\" value=\"d\" disabled=\"disabled\"/><br />"
			+"</div></div></td></tr>"
			+aa
			+"</table></li>";
			//alert("questionCnt 5 ===> " + questionCnt);
			ultag.innerHTML = a;
			document.getElementById("questionLi"+sectionCnt).appendChild(ultag);
	}


function removeQuestion(id){
	var row_skill = document.getElementById(id);
	if (row_skill && row_skill.parentNode
			&& row_skill.parentNode.removeChild) {
		row_skill.parentNode.removeChild(row_skill);
	}
}

function removeEditQuestion(id, delId, type) {
	if(confirm('Are you sure, You want to delete this question?')) {
	//alert("delId ===> "+  delId + " type ===> " + type);
		var action = 'DeleteAssessmentQuestions.action?delId=' + delId + '&type=' + type;
		getContent("", action);
	
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
		}
	}
}


	function getQuestoinContentType(cnt, callFrom, sectionCnt, divCnt){
		var val = 9;
		var a="";
		if(val == 8) {
			a="<tr id=\"matrixHeading"+sectionCnt+"_"+cnt+"\" style=\"display: none;\"><th></th></tr><tr id=\"answerType"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"matrixHeading"+sectionCnt+"\" />a)&nbsp;<input type=\"text\" name=\"optiona"+sectionCnt+"\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+sectionCnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb"+sectionCnt+"\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+"\" value=\"b\" /></td></tr>"
			+ "<tr id=\"answerType1"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc"+sectionCnt+"\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond"+sectionCnt+"\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+"\" value=\"d\" /></td></tr>";
		
		}else if (val == 1 || val == 2 ||  val == 9) {
			a="<tr id=\"matrixHeading"+sectionCnt+"_"+cnt+"\" style=\"display: none;\"><th></th></tr><tr id=\"answerType"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"matrixHeading"+sectionCnt+"\" />a)&nbsp;<span id=\"aspan\"><input type=\"text\" name=\"optiona"+sectionCnt+"\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control\"/></span> <input type=\"checkbox\" value=\"a\" name=\"correct"+sectionCnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<span id=\"bspan\"><input type=\"text\" name=\"optionb"+sectionCnt+"\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control\"/></span> <input type=\"checkbox\" name=\"correct"+sectionCnt+"_"+ cnt+"\" value=\"b\" /></td></tr>"
			+"<tr id=\"answerType1"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td>c)&nbsp;<span id=\"cspan\"><input type=\"text\" name=\"optionc"+sectionCnt+"\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+sectionCnt+"_"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<span id=\"dspan\"><input type=\"text\" name=\"optiond"+sectionCnt+"\" id=\"optiond"+cnt+"\" class=\" validateRequired form-control\"/></span> <input type=\"checkbox\" name=\"correct"+sectionCnt+"_"+ cnt+"\" value=\"d\" /></td></tr>";
		
		 }else if (val == 6) {
			a= "<tr id=\"matrixHeading"+sectionCnt+"_"+cnt+"\" style=\"display: none;\"><th></th></tr><tr id=\"answerType"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"matrixHeading"+sectionCnt+"\" id=\"matrixHeading"+cnt+"\"/><input type=\"hidden\" name=\"optiona"+sectionCnt+"\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb"+sectionCnt+"\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc"+sectionCnt+"\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond"+sectionCnt+"\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
				+ "<input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+ "\" value=\"0\">False</td></tr>";
		
		}else if (val == 5) {
			a= "<tr id=\"matrixHeading"+sectionCnt+"_"+cnt+"\" style=\"display: none;\"><th></th></tr><tr id=\"answerType"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"matrixHeading"+sectionCnt+"\" id=\"matrixHeading"+cnt+"\"/><input type=\"hidden\" name=\"optiona"+sectionCnt+"\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb"+sectionCnt+"\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc"+sectionCnt+"\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond"+sectionCnt+"\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
			+ "<input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+ "\" value=\"0\">No</td></tr>";
		} else if(val == 13) {
			a="<tr id=\"matrixHeading"+sectionCnt+"_"+cnt+"\" style=\"display: none;\"><th></th></tr><tr id=\"answerType"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"matrixHeading"+sectionCnt+"\" />a)&nbsp;<input type=\"text\" name=\"optiona"+sectionCnt+"\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ sectionCnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb"+sectionCnt+"\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ sectionCnt+"_"+ cnt+"\" value=\"b\" /></td></tr>"
			+ "<tr id=\"answerType1"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc"+sectionCnt+"\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ sectionCnt+"_"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond"+sectionCnt+"\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ sectionCnt+"_"+ cnt+"\" value=\"d\" /></td></tr>";
			+ "<tr id=\"answerType2"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optione"+sectionCnt+"\" id=\"optione"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ sectionCnt+"_"+ cnt+"\" value=\"e\" /></td><td colspan=\"2\">&nbsp;</td></tr>";
		
		} else if(val == 14) {
			a="<tr id=\"matrixHeading"+sectionCnt+"_"+cnt+"\"><th></th><th>Martix Heading</th><td colspan=\"3\"><input type=\"text\" name=\"matrixHeading"+sectionCnt+"\" id=\"matrixHeading"+cnt+"\" class=\"validateRequired form-control \"/></td></tr>"
			+ "<tr id=\"answerType"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona"+sectionCnt+"\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+sectionCnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb"+sectionCnt+"\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+"\" value=\"b\" /></td></tr>"
			+ "<tr id=\"answerType1"+sectionCnt+"_"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc"+sectionCnt+"\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond"+sectionCnt+"\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+sectionCnt+"_"+ cnt+"\" value=\"d\" /></td></tr>";
			
		} else {
		
			a="";
		}
		return a;
	}

var dialogEdit = '#SelectQueDiv';
function openQuestionBank(count,callFrom,sectionCnt) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html('Question Bank');
	$("#modalInfo").show();
	var ansType = document.getElementById('ansType'+sectionCnt+'_'+count).value;
	//alert("ansType ===> " + ansType);
	$.ajax({
		url : "SelectAssessmentQuestion.action?count="+count+"&ansType="+ansType+"&sectionCnt="+sectionCnt,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function setQuestionInTextfield() {
	var queid = document.getElementById("questionSelect").value;
	var count = document.getElementById("count").value;
	var sectionCnt = document.getElementById("sectionCnt").value;
	//alert("divCnt ---> "+divCnt);
    xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
    } else {
            var xhr = $.ajax({
            url : "SetAssessmentQuestionToTextfield.action?queid=" + queid + '&count=' +count + '&sectionCnt=' +sectionCnt,
            cache : false,
            success : function(data) {
            	//alert("data ---> "+data);
            	if(data != "" && data.trim().length > 0) {
            		var allData = data.split("::::");
                    document.getElementById("newquespan"+sectionCnt+"_"+count).innerHTML = allData[0];
                    document.getElementById("answerType"+sectionCnt+"_"+count).innerHTML = allData[1];
                    if(allData.length > 2) {
                    	document.getElementById("answerType1"+sectionCnt+"_"+count).style.display = 'table-row';
                    	document.getElementById("answerType1"+sectionCnt+"_"+count).innerHTML = allData[2];
                    } else {
                    	document.getElementById("answerType1"+sectionCnt+"_"+count).style.display = 'none';
                    }
            	}
            }
		});
    }
    $(".modal").hide();
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

function closeForm() {
	$("#divCDResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		url:'CourseDashboardData.action?dataType=A',
		cache:false,
		success:function(data){
			$("#divCDResult").html(data);
		}
	});
}


function showGradeStandard(value) {
	//alert("value ===> " + value);
	if(value == '1') { //typicalGradeDiv standardGradeDiv internationalGradeDiv
		if(document.getElementById("typicalGradeDiv")) {
			document.getElementById("typicalGradeDiv").style.display="block";
		}
	
		if(document.getElementById("standardGradeDiv")) {
			document.getElementById("standardGradeDiv").style.display="none";
		}
		
		if(document.getElementById("internationalGradeDiv")) {
			document.getElementById("internationalGradeDiv").style.display="none";
		}
	} else if(value == '2') {
		if(document.getElementById("typicalGradeDiv")) {
			document.getElementById("typicalGradeDiv").style.display="none";
		}
		if(document.getElementById("standardGradeDiv")) {
			document.getElementById("standardGradeDiv").style.display="block";
		}
		
		if(document.getElementById("internationalGradeDiv")) {
			document.getElementById("internationalGradeDiv").style.display="none";
		}
	} else if(value == '3') {
		if(document.getElementById("typicalGradeDiv")) {
			document.getElementById("typicalGradeDiv").style.display="none";
		}
		
		if(document.getElementById("standardGradeDiv")) {
			document.getElementById("standardGradeDiv").style.display="none";
		}
		
		if(document.getElementById("internationalGradeDiv")) {
			document.getElementById("internationalGradeDiv").style.display="block";
		}
	} else {
		
	}
}

</script>
<%
	String op = (String) request.getAttribute("operation");
String fromPage = (String) request.getAttribute("fromPage");
%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth" >
					<%
					UtilityFunctions uF = new UtilityFunctions();
					String anstype = (String) request.getAttribute("anstype");
					List<List<String>> ansTypeList = (List<List<String>>) request.getAttribute("ansTypeList");
					String tab = (String)request.getAttribute("tab"); 
					String assessmentId = (String)request.getAttribute("assessmentId");
					String assessPreface = (String)request.getAttribute("assessPreface");
					List<List<String>> sectionList = (List<List<String>>) request.getAttribute("sectionList");
					Map<String, List<List<String>>> hmAssessmentQueData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentQueData");
					Map<String, String> hmAssessQueTotWeight = (Map<String, String>) request.getAttribute("hmAssessQueTotWeight");
					Map<String, List<List<String>>> hmGradeStandardwiseValue = (Map<String, List<List<String>>>) request.getAttribute("hmGradeStandardwiseValue");
					
					%>
					<div style="width: 100%; background-color: #868686; box-shadow:3px 4px 12px #616161;height: 80%;">
						<div class="pull-right">
								<a href="javascript:void(0);" onclick="closeForm()" class="close-font" style="margin-right:20px;"/>
							</div>
						<div id="container" style="width: 99%; float: left;height:98%;"> 
							 <ul>
								<li><a href="#course"><span>Assessment</span> </a> </li>
								<%if(assessmentId != null){ %>
									<li id="tabindex" ><a href="#index"><span>Index</span> </a></li>
									<% if(sectionList != null && !sectionList.isEmpty()){
											for(int i=0; i< sectionList.size(); i++){
										%>
												<li  id="tabassessment<%=i+1 %>"><a href="#assessment<%=i+1 %>"><span>Section <%=i+1 %> </span></a></li>
										<%
											}
								      }
								%>
									<li  id="tabassessment<%=uF.parseToInt(""+sectionList.size())+1 %>"><a href="#assessment<%=uF.parseToInt(""+sectionList.size())+1 %>"><span> + </span></a></li>	
				    			<%} %>
							</ul>
 						
 							<div style=" border: solid 0px #ff0000;width:96% " id="course">
 								<s:form theme="simple" action="AddNewAssessment" id="frmAddNewAssessment" method="POST" cssClass="formcss" enctype="multipart/form-data">
									<s:hidden name="operation"></s:hidden>
									<s:hidden name="assessmentId"></s:hidden>
									<s:hidden name="assignToExist"></s:hidden>
									<input type="hidden" name="tab" value="0"/>
									<input type="hidden" name="fromPage" value="<%=fromPage%>" id="fromPage"/>
									<div style="float: left; width: 100%;">
										<table border="0" class="table" style="width: 85%;">
											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right; width: 200px;">Assessment Name:<sup>*</sup></td>
												<td><s:textfield name="assessmentName" cssClass="validateRequired form-control" cssStyle="width: 450px;"></s:textfield>
												</td>
											</tr>
											
											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right">Subject:<sup>*</sup></td>
												<td>
													<s:select theme="simple" name="assessmentSubject" headerKey="" headerValue="Select Subject" list="subjectList" 
													listKey="subjectId" id="assessmentSubject" listValue="subjectName" cssClass="validateRequired form-control" value="subjectID"/>
												</td>
											</tr>

											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right">Author:<sup>*</sup></td>
												<td><s:textfield name="assessmentAuthor" cssClass="validateRequired form-control"></s:textfield>
												<%-- <s:textarea name="trainingObjective" cssClass="validateRequired text-input" required="true" rows="5" cols="70"></s:textarea> --%>
												</td>
											</tr>
											
											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right">Version:<sup>*</sup></td>
												<td><s:textfield name="assessmentVersion" cssClass="validateRequired form-control" onkeypress="return isNumberKey(event)"></s:textfield>
												</td>
											</tr>
											
											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right">Assessment Description:</td>
												<td>
												<%-- <s:textarea rows="3" cols="72" name="coursePreface" id="editor1"></s:textarea> --%>
													<textarea rows="3" cols="72" name="assessmentPreface" id="editor1" class=" form-control"><%=uF.showData(assessPreface, "") %></textarea>
												</td>
											</tr>
											
											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right">Times to attempt the assessment ?:<sup>*</sup></td>
												<td>
												<s:select theme="simple" cssClass="validateRequired form-control" name="timestoAttemptAssessment" id="timestoAttemptAssessment" headerKey="0"
					                                  headerValue="Default" list="#{'1':'Once', '2':'Twice', '3':'Thrice', '4':'Four Times', '5':'Five Times', '6':'Six Times', '7':'Seven Times', '8':'Eight Times', '9':'Nine Times', '10':'Ten Times'}"/>
												</td>
											</tr>
						
											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right">Time Duration:<sup>*</sup></td>
												<td><s:textfield name="assessmentTimeDuration" id="assessmentTimeDuration" cssClass="validateRequired form-control" onkeyup="checkValue('assessmentTimeDuration')"   onkeypress="return isNumberKey(event)"></s:textfield>mins
												</td>
											</tr>
											
											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right">Select Grading Type :</td>
												<td>
												<s:radio list="#{'1':'Numerical', '2':'Alphabetical'}" theme="simple" name="marksGradeType" value="marksGradeTypeDefault"></s:radio>
										 		</td>
											</tr>
											
											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right">Select Grading Standard :</td>
												<td>
												<s:radio list="#{'1':'Typical Grade', '2':'Standard Grade', '3':'International Grade'}" theme="simple" name="marksGradeStandard" id="marksGradeStandard" onclick="showGradeStandard(this.value);"  value="marksGradeStandardDefault"></s:radio>
										 		</td>
											</tr>
						
											<tr>
												<td class="txtlabel" style="vertical-align: top; text-align: right">&nbsp;</td>
												<td>
													<div id="typicalGradeDiv" style="display: none">
														<table class="table table-bordered table-head-highlight">
																<tr>
																	<th> Percentage (%)</th>
																	<th> Grade</th>
																</tr>
																<%List<List<String>> typicalGradeList = hmGradeStandardwiseValue.get("1");
																 for(int i=0; typicalGradeList != null && !typicalGradeList.isEmpty() && i<typicalGradeList.size(); i++){
																		List<String> innerList = typicalGradeList.get(i);
																%>
																		<tr>
																			<td><%=innerList.get(0) %></td>
																			<td><%=innerList.get(1) %></td>
																		</tr>
															<%	} %>
														</table>
						 						   </div>
												
													<div id="standardGradeDiv" style="display: none"> 
														<table class="table table-bordered table-head-highlight">
															<tr>
																<th> Percentage (%)</th>
																<th> Grade</th>
																<%List<List<String>> standardGradeList = hmGradeStandardwiseValue.get("2");
																  for(int i=0; standardGradeList != null && !standardGradeList.isEmpty() && i<standardGradeList.size(); i++){
																		List<String> innerList = standardGradeList.get(i);
																%>
																		<tr>
																			<td><%=innerList.get(0) %></td>
																			<td><%=innerList.get(1) %></td>
																		</tr>
																<% } %>
														</table>
													</div>
							
													<div id="internationalGradeDiv" style="display: none"> 
														<table class="table table-bordered table-head-highlight">
															<tr>
																<th> Percentage (%)</th>
																<th> Grade</th>
															</tr>
																<%List<List<String>> internationalGradeList = hmGradeStandardwiseValue.get("3");
																for(int i=0; internationalGradeList != null && !internationalGradeList.isEmpty() && i<internationalGradeList.size(); i++){
																	List<String> innerList = internationalGradeList.get(i);
																%>
																		<tr>
																			<td><%=innerList.get(0) %></td>
																			<td><%=innerList.get(1) %></td>
																		</tr>
																<% } %>
															
													   	</table>
												  	</div>
					 						</td>
										</tr>
								</table>
							</div>

							<div style="width: 100%; float: right;">
								
								<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSubmit" value="Submit And Proceed"/>
								<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save And Exit"/>
							</div>
      			   </s:form>
	          <!-- </div> -->
               </div>
               
  				<% if(assessmentId != null) { %>
  					<div style="border: solid 0px #ff0000; width:96%;" id="index">
						<s:form theme="simple" action="AddNewAssessment" id="frmAddNewAssessment1" method="POST" cssClass="formcss" enctype="multipart/form-data">
							<s:hidden name="operation"></s:hidden>
							<s:hidden name="assessmentId"></s:hidden>
							<s:hidden name="assignToExist"></s:hidden>
							<input type="hidden" name="fromPage" value="<%=fromPage%>" id="fromPage"/>
							<input type="hidden" name="tab" value="1"/>
				
							<div style="float: left; width: 100%; min-height: 355px;">
								<table border="0" class="table" style="width: 85%;">
									<tr>
										<td class="tdLabelheadingBg alignCenter" colspan="2">Index</td>
									</tr>
									<% 
								 	if(sectionList != null && !sectionList.isEmpty()) {
										for(int i=0; i< sectionList.size(); i++) {
											List<String> innerList = sectionList.get(i);
									%> 
											<tr>
												<td class="txtlbl" height="10px" align="right"><%=i+1 %>)&nbsp;&nbsp;</td>
												<td class="txtlbl" height="10px" colspan="2"><%=innerList.get(1) %></td>
											</tr>
									<%  } 
								   } %>
						
									<% if(sectionList == null || sectionList.isEmpty()){ %>
											<tr>
												<td class="txtlbl" colspan="3"><b>'No Content Added'</b></td>
											</tr>
									<% } %>
								</table>
							</div>

							<div style="width: 100%; float: right;">
								
								<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSubmit" value="Submit And Proceed"/>
								<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save And Exit"/>
							</div>
         			</s:form> 
			  </div>
			  <% 
			  String sectionCnt = (String) request.getAttribute("sectionCnt");
			  if(sectionList != null && !sectionList.isEmpty()){
					for(int i=0; i< sectionList.size(); i++){
						List<String> innerList = sectionList.get(i);
						
				%>
					  <div style="border: solid 0px #ff0000;width:96%" id="assessment<%=i+1 %>">
					 	<s:form theme="simple" action="AddNewAssessment" id="frmAddNewAssessment2" method="POST" cssClass="formcss" enctype="multipart/form-data">
							<s:hidden name="operation"></s:hidden>
							<s:hidden name="assessmentId"></s:hidden>
							<s:hidden name="assignToExist"></s:hidden>
							<input type="hidden" name="fromPage" value="<%=fromPage%>" id="fromPage"/>
							<input type="hidden" name="sectionCnt" value="<%=i+1 %>"/>
							<input type="hidden" name="tab" value="<%=i+2 %>"/>
				
							<table border="0" class="table" style="width: 85%;">
								<tr>
									<td class="txtlabel" style="vertical-align: top; text-align: right">Section Name:<sup>*</sup>
										<input type="hidden" name="hidesectionid" value="<%=innerList.get(0) %>">
									</td>
									<td>
										<input type="text" name="sectionName" class="validateRequired form-control" style="width: 450px;" value="<%=uF.showData(innerList.get(1), "") %>"/>
									</td>
								</tr>

								<tr>
									<td class="txtlabel" style="vertical-align: top; text-align: right">Section Description:</td>
									<td>
										<textarea rows="3" cols="72" class=" form-control" name="sectionDescription" id="editor<%=i+2 %>"><%=uF.showData(innerList.get(2), "") %></textarea>
									</td>
								</tr>

								<tr>
									<td class="txtlabel" style="vertical-align: top; text-align: right">Total marks for this section:<sup>*</sup>
									</td>
									<td><input type="text" name="marksForSection" class="validateRequired form-control" style="width: 150px;" value="<%=uF.showData(innerList.get(3), "") %>"/>
									</td>
								</tr>
								<tr>
									<td class="txtlabel" style="vertical-align: top; text-align: right">How many questions to be attempted ?:<sup>*</sup>
									</td>
									<td><input type="text" name="questionAttempt" class="validateRequired form-control" style="width: 150px;" value="<%=uF.showData(innerList.get(4), "") %>"/>
									</td>
								</tr>
						   </table>
									
							<div id="subchapterDiv<%=i+1 %>">
								<%
								String showAssessDiv = "none";
								int assessSize = 0;
								if(hmAssessmentQueData != null && !hmAssessmentQueData.isEmpty()) {
									List<List<String>> questionList = hmAssessmentQueData.get(innerList.get(0));
								    if(questionList != null) { 
										showAssessDiv = "block";
										assessSize = questionList.size();
									} 
								}
								
								%>
								<div id="assessmentDiv<%=i+1 %>" style="float: left; width: 100%; min-height: 200px; display: block;"> <!-- border: 1px solid; display: none; -->
								   <ul class="level_list ul_class">
										<li id="questionLi<%=i+1 %>" style="margin-left: 100px;display: <%=showAssessDiv %>;">
										<%
											if(hmAssessmentQueData != null && !hmAssessmentQueData.isEmpty()) {
												List<List<String>> questionList = hmAssessmentQueData.get(innerList.get(0));
												String totWeightage = hmAssessQueTotWeight.get(innerList.get(0));
													if(questionList != null) {
												for(int k=0; questionList != null && k < questionList.size(); k++) {
													List<String> innerList1 = questionList.get(k);
													if(innerList1 != null) {
											%>
														<ul id="questionUl<%=(i+1)+"_"+(k+1) %>">
															<li>
																<table class="table" width="100%">
																	<tr>
																		<th><%=i+1 %>.<%=k+1 %>)</th>
																		<th width="17%" style="text-align: right;">Add Question<sup>*</sup></th>
																		<td colspan="3">
																			<div>
																				<input type="hidden" name="questionID<%=i+1 %>" value="<%=innerList1.get(2)%>" />
																				<input type="hidden" name="hideassessmentid<%=i+1 %>" value="<%=innerList1.get(3) %>">
																				<span id="newquespan<%=i+1 %>_<%=k+1 %>" style="float: left;"><input type="hidden" name="hidequeid<%=i+1 %>" id="hidequeid<%=i+1 %>_<%=k+1 %>" value="<%=innerList1.get(3) %>"/>
																					<textarea rows="2" name="question<%=i+1 %>" id="question<%=i+1 %>_<%=k+1 %>" class="validateRequired form-control" style="width: 399px !important; height: 110px;"><%=innerList1.get(0) %></textarea>
																				</span>
																				<span style="float: left; margin-left: 10px;">
																					<input type="hidden" name="orientt<%=i+1 %>" value="<%=k+1 %>"/>
																				</span>
																				<span style="float: left; margin-left: 10px;">
																					<a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=k+1 %>','','<%=i+1 %>');" > +Q </a>
																				</span>
																				<span id="checkboxspan<%=i+1 %>_<%=k+1 %>" style="float: left; margin-left: 10px;">
																					<input name="addFlag<%=i+1 %>" type="checkbox" id="addFlag<%=i+1 %>_<%=k+1 %>" title="Add to Question Bank" onclick="changeStatus('<%=i+1 %>_<%=k+1 %>')"
																				 
																					<%if(innerList1.get(10) != null && uF.parseToBoolean(innerList1.get(10))) { %> checked <% } %>/>
																					<input type="hidden" id="status<%=i+1 %>_<%=k+1 %>" name="status<%=i+1 %>" <%if(innerList1.get(10) != null && uF.parseToBoolean(innerList1.get(10))) { %>value="1" <% }else { %>
																					value="0" <%} %>/>
																				</span>
																				<a href="javascript:void(0)" title="Add New Question" onclick="getQuestion('<%=assessSize %>','','<%=i+1 %>');" ><i class="fa fa-plus-circle"></i></a>&nbsp;&nbsp;
																				<a href="javascript:void(0)" title="Remove Question" onclick="removeEditQuestion('questionUl<%=i+1 %>_<%=k+1 %>','<%=innerList1.get(3) %>','assess');" class="close-font"></a>
																				<input type="hidden" name="questiontypename<%=i+1 %>" value="<%=k+1 %>" />
																			</div>
																			<div><input name="questionImage<%=i+1 %>" id="questionImage<%=i+1 %>_<%=k+1 %>" type="file" accept=".gif,.jpg,.png,.tif,.svg,.svgz"></div>
																		</td>
																	</tr>
																	<tr>
																			<th></th><th>Select Answer Type:<sup>*</sup></th>
																			<td>
																				<select name="ansType<%=i+1 %>" class="form-control " id="ansType<%=i+1 %>_<%=k+1 %>" onchange="showAnswerTypeDiv(this.value, '<%=k+1 %>', 'answerType<%=i+1 %>_<%=k+1 %>', 'answerType1<%=i+1 %>_<%=k+1 %>', '<%=i+1 %>', '');">
																					 <option value="">Select</option>
																					 <%
																						 for(int b = 0; ansTypeList != null && !ansTypeList.isEmpty() && b< ansTypeList.size(); b++){
																								 String checkStatus = "";
																							 List<String> ansInnerList = ansTypeList.get(b);
																							 
																							 if(ansInnerList.get(0).equals(innerList1.get(9))){
																								 checkStatus = "selected";
																							 }
																							 %>
																						<option value="<%=ansInnerList.get(0) %>" <%=checkStatus %>><%=ansInnerList.get(1) %></option>		 
																							 <%
																						 }
																					 %>
																				 </select>
											 								</td>
											 								<td>
											 									<div id="anstypediv<%=i+1 %>_<%=k+1 %>">
																				<%
																						int getanstype = uF.parseToInt(innerList1.get(9));
																						if(getanstype == 1) { %>
																							<div id="anstype1">
																								a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/><br />
																								c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/><br />
																								<textarea rows="2" cols="50" name="textara" style="width:200px;" disabled="disabled" class=" form-control"></textarea>
																							</div>
																						<%}else if(getanstype == 2){ %>
																							<div id="anstype2">
																								a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/><br />
																								c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/><br />
																							</div>	
																						<%}else if(getanstype == 3){ %>
																							<div id="anstype3">
																								<img border="0" style="padding: 5px 2px 0pt; height: 30px; width: 31%;" src="<%=request.getContextPath()%>/images1/scorebar_img.png"/>
																								<div id="marksscore" style="width:31%;">0 <span style="float:right;">100</span></div>
																							</div>	
																						<%}else if(getanstype == 4){ %>
																							<div id="anstype4">
																								<input type="radio" name="excellent" value="Excellent" disabled="disabled"/>Excellent 
																								<input type="radio" name="verygood" value="Very Good" disabled="disabled"/>Very Good 
																								<input type="radio" name="average" value="Average" disabled="disabled"/>Average 
																								<input type="radio" name="good" value="Good" disabled="disabled"/>Good 
																								<input type="radio" name="poor" value="Poor" disabled="disabled"/>Poor 
																								<!-- <input type="radio" name="bad" value="Bad" disabled="disabled"/>Bad  -->
																							</div>
																						<%}else if(getanstype == 5){ %>
																							<div id="anstype5">
																								<input type="radio" name="yes" value="Yes" disabled="disabled"/>Yes &nbsp;
																								<input type="radio" name="no" value="No" disabled="disabled"/>No &nbsp;
																							</div>
																						<%}else if(getanstype == 6){ %>
																							<div id="anstype6">
																								<input type="radio" name="true" value="True" disabled="disabled"/>True &nbsp;
																								<input type="radio" name="false" value="False" disabled="disabled"/>False &nbsp;
																							</div>
																						<%}else if(getanstype == 7){ %>
																							<div id="anstype7">
																								<div class="divvalign">
																									<textarea rows="2" cols="50" name="singleopentext" style="width:200px" disabled="disabled" class=" form-control"></textarea>
																								</div>
																								<img border="0" style="padding: 5px 2px 0pt; height: 30px; width: 21%;" src="<%=request.getContextPath()%>/images1/scorebar_img.png"/>
																								<div id="markssingleopen" style="width:21%;">0 <span style="float:right;">100</span></div>
																							</div>
																						<%}else if(getanstype == 8){ %>
																							<div id="anstype8">
																								a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/><br />
																								c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/><br />
																							</div> 
																						<%}else if(getanstype == 9){ %>
																							<div id="anstype9">
																								a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"/><br />
																								c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"/><br />
																							</div>
																						<%}else if(getanstype == 10){ %>
																							<div id="anstype10">
																								<div class="divvalign" style="vertical-align: text-top; float: left; width: 100%; margin-bottom: 10px;">
																									<span style="float: left; vertical-align: text-top; margin-right: 7px;">a)</span><span style="float: left; vertical-align: text-top; margin-right: 20px;"><textarea rows="2" cols="50" name="amultiopen" style="width:170px;" disabled="disabled" class=" form-control"></textarea></span>
																									<span style="float: left; vertical-align: text-top; margin-right: 7px;">b)</span><span style="vertical-align: text-top; float: left"><textarea rows="2" cols="50" name="bmultiopen" style="width:170px;" disabled="disabled" class=" form-control"></textarea></span>
																								</div>
																								<div class="divvalign" style="vertical-align: text-top; float: left; width: 100%;">
																									<span style="float: left; vertical-align: text-top; margin-right: 7px;">c)</span><span style="float: left; vertical-align: text-top; margin-right: 20px;"><textarea rows="2" cols="50" name="amultiopen" style="width:170px;" disabled="disabled" class=" form-control"></textarea></span>
																									<span style="float: left; vertical-align: text-top; margin-right: 7px;">d)</span><span style="vertical-align: text-top; float: left"><textarea rows="2" cols="50" name="bmultiopen" style="width:170px;" disabled="disabled" class=" form-control"></textarea></span>
																								</div>
																								<img border="0" style="padding: 5px 2px 0pt; height: 30px; width: 31%;" src="<%=request.getContextPath()%>/images1/scorebar_img.png"/>
																								<div id="marksmultipleopen" style="width:31%;">0 <span style="float:right;">100</span></div>
																							</div>
																							<%}else if(getanstype == 11){ %>						
																								<div id="anstype11">
																									<img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
																									<img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
																									<img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
																									<img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
																									<img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
																								</div>
																							<%}else if(getanstype == 12) { %>		
																								<div id="anstype12">
																									<textarea rows="2" cols="50" name="singleopenwithoutmarks" style="width:200px;" disabled="disabled" class=" form-control"></textarea>
																								</div>
																							<%}else if(getanstype == 13) { %>		
																								<div id="anstype13">
																									a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/>&nbsp;Rate&nbsp;<input value="5" style="width: 20px !important; height: 22px !important;" disabled="disabled" type="text"> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/>&nbsp;Rate&nbsp;<input value="3" style="width: 20px !important; height: 22px !important;" disabled="disabled" type="text"> <br />
																									c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/>&nbsp;Rate&nbsp;<input value="4" style="width: 20px !important; height: 22px !important;" disabled="disabled" type="text"> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/>&nbsp;Rate&nbsp;<input value="2" style="width: 20px !important; height: 22px !important;" disabled="disabled" type="text"> <br />
																									e) Option5&nbsp;<input type="radio" value="e" name="correct" disabled="disabled"/>&nbsp;Rate&nbsp;<input value="4" style="width: 20px !important; height: 22px !important;" disabled="disabled" type="text"><br />
																								</div>
																							<%} else if(getanstype == 14) { %>		
																								<div id="anstype14">
																									Matrix Heading:&nbsp;&nbsp;&nbsp;&nbsp;<input value="A B C D/ I II III VI" style="height: 22px !important;" disabled="disabled" type="text"><br />
																									a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/><br />
																									c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/><br />
																								</div>
																							<%} %>
																					</div>
																				</td>
																			</tr>
																		<%														
																			if(getanstype == 8){ %>
																				<tr id="matrixHeading<%=i+1 %>_<%=k+1 %>" style="display: none"><th></th><th></th><td colspan="3"></td></tr>
																				<tr id="answerType<%=i+1 %>_<%=k+1 %>">
																					<th></th><th></th>
																					<td>
																						<input type="hidden" name="matrixHeading<%=i+1 %>" value="<%=innerList1.get(13)%>"/>
																						a)&nbsp;<input type="text" name="optiona<%=i+1 %>" id="optiona<%=i+1 %>" value="<%=innerList1.get(4)%>" class=" validateRequired form-control"/> <input type="radio" value="a" name="correct<%=i+1 %>_<%=k+1 %>"
																						<%if(innerList1.get(8).contains("a")){ %> checked="checked" <%} %>/>
																					</td>
																					<td colspan="2">
																						b)&nbsp;<input type="text" name="optionb<%=i+1 %>" id="optionb<%=i+1 %>" value="<%=innerList1.get(5)%>" class=" validateRequired form-control"/><input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="b" 
																						<%if(innerList1.get(8).contains("b")){ %> checked="checked" <%} %>/>
																					</td>
																			   </tr>
																				<tr id="answerType1<%=i+1 %>_<%=k+1 %>">
																					<th></th><th></th>
																					<td>
																						c)&nbsp;<input type="text" name="optionc<%=i+1 %>" id="optionc<%=i+1 %>" value="<%=innerList1.get(6)%>" class=" validateRequired form-control"/> <input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="c"
																						<%if(innerList1.get(8).contains("c")){ %> checked="checked" <%} %>/>
																					</td>
																					<td colspan="2">
																						d)&nbsp;<input type="text" name="optiond<%=i+1 %>" id="optiond<%=i+1 %>" value="<%=innerList1.get(7)%>" class=" validateRequired form-control"/> <input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="d"
																						<%if(innerList1.get(8).contains("d")){ %> checked="checked" <%} %>/>
																					</td>
																				</tr>
																				<%} else if(getanstype == 1 || getanstype == 2 || getanstype == 9){ %>
																					<tr id="matrixHeading<%=i+1 %>_<%=k+1 %>" style="display: none"><th></th><th></th><td colspan="3"></td></tr>
																					<tr id="answerType<%=i+1 %>_<%=k+1 %>">
																						<th></th><th></th>
																						<td>
																							<input type="hidden" name="matrixHeading<%=i+1 %>" value="<%=innerList1.get(13)%>"/>
																							a)&nbsp;<input type="text" name="optiona<%=i+1 %>" id="optiona<%=i+1 %>" value="<%=innerList1.get(4)%>" class=" validateRequired form-control"/> <input type="checkbox" value="a" name="correct<%=i+1 %>_<%=k+1 %>"
																							<%if(innerList1.get(8).contains("a")){ %> checked="checked" <%} %>/>
																					    </td>
																						<td colspan="2">
																							b)&nbsp;<input type="text" name="optionb<%=i+1 %>" id="optionb<%=i+1 %>" value="<%=innerList1.get(5)%>" class=" validateRequired form-control"/> <input type="checkbox" name="correct<%=i+1 %>_<%=k+1 %>" value="b" 
																							<%if(innerList1.get(8).contains("b")){ %> checked="checked" <%} %>/>
																						</td>
																					</tr>
																					<tr id="answerType1<%=i+1 %>_<%=k+1 %>">
																						<th></th><th></th>
																						<td>
																							c)&nbsp;<input type="text" name="optionc<%=i+1 %>" id="optionc<%=i+1 %>"  value="<%=innerList1.get(6)%>" class=" validateRequired form-control"/> <input type="checkbox" name="correct<%=i+1 %>_<%=k+1 %>" value="c"
																							<%if(innerList1.get(8).contains("c")){ %> checked="checked" <%} %>/>
																						</td>
																						<td colspan="2">
																							d)&nbsp;<input type="text" name="optiond<%=i+1 %>" id="optiond<%=i+1 %>" value="<%=innerList1.get(7)%>" class=" validateRequired form-control"/> <input type="checkbox" name="correct<%=i+1 %>_<%=k+1 %>" value="d" 
																							<%if(innerList1.get(8).contains("d")){ %> checked="checked" <%} %>/>
																						</td>
																					</tr>
																				<%} else if(getanstype == 6) { %>
																						<tr id="matrixHeading<%=i+1 %>_<%=k+1 %>" style="display: none"><th></th><th></th><td colspan="3"></td></tr>
																						<tr id="answerType<%=i+1 %>_<%=k+1 %>">
																							<th></th><th></th>
																							<td colspan="3">
																								<input type="hidden" name="matrixHeading<%=i+1 %>" value="<%=innerList1.get(13)%>"/>
																								<input type="hidden" name="optiona<%=i+1 %>" value="<%=innerList1.get(4)%>"/><input type="hidden" name="optionb<%=i+1 %>" value="<%=innerList1.get(5)%>"/>
																								<input type="hidden" name="optionc<%=i+1 %>" value="<%=innerList1.get(6)%>"/><input type="hidden" name="optiond<%=i+1 %>" value="<%=innerList1.get(7)%>"/>
																								<input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="1" 
																								<%if(innerList1.get(8) != null && innerList1.get(8).contains("1")){ %> checked="checked" <%} %>>True&nbsp; 
																								<input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="0" 
																								<%if(innerList1.get(8) != null && innerList1.get(8).contains("0")){ %> checked="checked" <%} %>>False
																							</td>
																						</tr>
																						<tr id="answerType1<%=i+1 %>_<%=k+1 %>" style="display: none"><th></th><th></th><td colspan="3"></td></tr>
																					<%} else if(getanstype == 5) { %>
																						<tr id="matrixHeading<%=i+1 %>_<%=k+1 %>" style="display: none"><th></th><th></th><td colspan="3"></td></tr>
																						<tr id="answerType<%=i+1 %>_<%=k+1 %>">
																							<th></th><th></th>
																							<td colspan="3">
																								<input type="hidden" name="matrixHeading<%=i+1 %>" value="<%=innerList1.get(13)%>"/>
																								<input type="hidden" name="optiona<%=i+1 %>" value="<%=innerList1.get(4)%>"/><input type="hidden" name="optionb<%=i+1 %>" value="<%=innerList1.get(5)%>"/>
																								<input type="hidden" name="optionc<%=i+1 %>" value="<%=innerList1.get(6)%>"/><input type="hidden" name="optiond<%=i+1 %>" value="<%=innerList1.get(7)%>"/>
																								<input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="1" 
																								<%if(innerList1.get(8) != null && innerList1.get(8).contains("1")) { %>
																									checked="checked"
																								<% } %>>Yes&nbsp; 
																								<input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="0" 
																								<%if(innerList1.get(8) != null && innerList1.get(8).contains("0")) { %>
																									checked="checked"
																								<% } %>>No
																							</td>
																						</tr>
																						<tr id="answerType1<%=i+1 %>_<%=k+1 %>" style="display: none"><th></th><th></th><td colspan="3"></td></tr>
																						<% } else if(getanstype == 14) { %>
																						<tr id="matrixHeading<%=i+1 %>_<%=k+1 %>">
																							<th></th><th>Matrix Heading: </th>
																							<td colspan="3">&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" name="matrixHeading<%=i+1 %>" id="matrixHeading<%=i+1 %>" value="<%=innerList1.get(13)%>" class=" validateRequired"/></td>
																					   </tr>
																						<tr id="answerType<%=i+1 %>_<%=k+1 %>">
																							<th></th><th></th>
																							<td>a)&nbsp;<input type="text" name="optiona<%=i+1 %>" id="optiona<%=i+1 %>" value="<%=innerList1.get(4)%>" class=" validateRequired form-control"/> <input type="radio" value="a" name="correct<%=i+1 %>_<%=k+1 %>"
																								<%if(innerList1.get(8).contains("a")){ %> checked="checked" <%} %>/>
																							</td>
																							<td colspan="2"> b)&nbsp;<input type="text" name="optionb<%=i+1 %>" id="optionb<%=i+1 %>" value="<%=innerList1.get(5)%>" class=" validateRequired form-control"/><input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="b" 
																								<%if(innerList1.get(8).contains("b")){ %> checked="checked" <%} %>/>
																							</td>
																					   </tr>
																						<tr id="answerType1<%=i+1 %>_<%=k+1 %>">
																							<th></th><th></th>
																							<td>c)&nbsp;<input type="text" name="optionc<%=i+1 %>" id="optionc<%=i+1 %>" value="<%=innerList1.get(6)%>" class=" validateRequired form-control"/> <input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="c"
																								<%if(innerList1.get(8).contains("c")){ %> checked="checked" <%} %>/>
																							</td>
																							<td colspan="2"> d)&nbsp;<input type="text" name="optiond<%=i+1 %>" id="optiond<%=i+1 %>" value="<%=innerList1.get(7)%>" class=" validateRequired form-control"/> <input type="radio" name="correct<%=i+1 %>_<%=k+1 %>" value="d"
																								<%if(innerList1.get(8).contains("d")){ %> checked="checked" <%} %>/>
																							</td>
																						</tr>
																					<%} else { %>
																						<tr id="matrixHeading<%=i+1 %>_<%=k+1 %>" style="display: none"><th></th><th></th><td colspan="3"><input type="hidden" name="matrixHeading<%=i+1 %>" value="<%=innerList1.get(13)%>"/></td></tr>
																						<tr id="answerType<%=i+1 %>_<%=k+1 %>" style="display: none"><th></th><th></th><td colspan="3"><input type="hidden" name="optiona<%=i+1 %>" value="<%=innerList1.get(4)%>"/><input type="hidden" name="optionb<%=i+1 %>" value="<%=innerList1.get(5)%>"/></td></tr>
																						<tr id="answerType1<%=i+1 %>_<%=k+1 %>" style="display: none"><th></th><th></th><td colspan="3"><input type="hidden" name="optionc<%=i+1 %>" value="<%=innerList1.get(6)%>"/><input type="hidden" name="optiond<%=i+1 %>" value="<%=innerList1.get(7)%>"/></td></tr>
																					<%} %>
																			</table>
																		</li>
																</ul>
														<%   } 
														}
													}
												} %>
										</li>
										<li style="margin-left: 100px; border: none;">
											<a href="javascript:void(0)" class="add_lvl" onclick="getQuestion('<%=assessSize %>','','<%=i+1 %>');">Add Questions</a>
										</li>
									</ul>
								</div>
					<!-- </div> -->
						 	</div>
							<div style="width: 100%; float: right;">
								
								<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSubmit" value="Submit And Proceed"/>
								<%-- <%if(op != null && o<%-- <%if(op != null && op.equals("E")) %> --%>
								<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save And Exit"/>
							</div>
						</s:form>
 					 </div>
  				<%
					}
				}
				%>
  
			  <div style="border: solid 0px #ff0000;width:96%" id="assessment<%=uF.parseToInt(""+sectionList.size())+1 %>">
			 	<s:form theme="simple" action="AddNewAssessment" id="frmAddNewAssessment3" method="POST" cssClass="formcss" enctype="multipart/form-data">
					<s:hidden name="operation"></s:hidden>
					<s:hidden name="assessmentId"></s:hidden>
					<s:hidden name="assignToExist"></s:hidden>
					<input type="hidden" name="fromPage" value="<%=fromPage%>" id="fromPage"/>
					<input type="hidden" name="sectionCnt" value="<%=uF.parseToInt(sectionCnt)+1 %>"/>
					<input type="hidden" name="tab" value="<%=uF.parseToInt(""+sectionList.size())+2 %>"/>
			 	
			 		<table border="0" class="table" style="width: 85%;">
						<tr>
							<td class="tdLabelheadingBg alignCenter" colspan="2">Section <%=uF.parseToInt(""+sectionList.size())+1 %></td>
						</tr>
					
						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Section Name:<sup>*</sup></td>
							<td><input type="text" name="sectionName" class="validateRequired form-control" style="width: 450px;"/></td>
					   </tr>
					
						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Section Description:</td>
							<td>
								<textarea rows="3" cols="72" name="sectionDescription" id="editor<%=uF.parseToInt(""+sectionList.size())+2 %>" class=" form-control"></textarea>
							</td>
						</tr>
					
						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Marks for section:<sup>*</sup>
							</td>
							<td><input type="text" name="marksForSection" id="marksForSection"  class="validateRequired form-control " style="width: 150px;"  onkeyup="checkValue('marksForSection')" onkeypress="return isOnlyNumberKey(event)"/>
							</td>
						</tr>
						
						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">How many question attempt:<sup>*</sup>
							</td>
							<td><input type="text" name="questionAttempt" id="questionAttempt" class="validateRequired form-control" style="width: 150px;" onkeyup="checkValue('questionAttempt')" onkeypress="return isOnlyNumberKey(event)"/>
							</td>
						</tr>
				</table>
				
					<div style="width: 100%; float: left;">
						<ul class="level_list ul_class">
							<li id="questionLi<%=uF.parseToInt(sectionCnt)+1 %>" style="margin-left: 100px;"></li>
							<li style="margin-left: 100px; border: none;"><a href="javascript:void(0)" class="add_lvl" onclick="getQuestion('0','','<%=uF.parseToInt(sectionCnt)+1 %>');">Add Questions</a></li>
						</ul>
					</div>
					<div style="width: 100%; float: right;">
						
						<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSubmit" value="Submit And Proceed"/>
						<%-- <%if(op != null && o<%-- <%if(op != null && op.equals("E")) { %> --%>
						<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave"  value="Save And Exit"/>
					</div>
			   </s:form>
		   </div>
	   <% } %>
    </div>
  </div>
</div>
</div>
<!-- /.box-body -->
</section>
 
 <div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
  </div>
</section>

<div id="debug"></div>
<div id="SelectQueDiv"></div>


<script>
	// Replace the <textarea id="editor1"> with an CKEditor instance.
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
	<% if(assessmentId != null) { %>
	<% if(sectionList != null && !sectionList.isEmpty()){
		for(int i=0; i< sectionList.size(); i++){
			List<String> innerList = sectionList.get(i);
			%>
	// Replace the <textarea id="editor2"> with an CKEditor instance.
	CKEDITOR.replace( 'editor<%=i+2 %>', {
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
	
	<% } } %>
	// Replace the <textarea id="editor2"> with an CKEditor instance.
	CKEDITOR.replace( 'editor<%=uF.parseToInt(""+sectionList.size())+2 %>', {
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

   <% } %>	
   var submitActor = null;
   var submitButtons = $('form').find('input[type=submit]').filter(':visible');
   $("form").bind('submit',function(event) {
		  event.preventDefault();
		  if (null === submitActor) {
              // If no actor is explicitly clicked, the browser will
              // automatically choose the first in source-order
              // so we do the same here
              submitActor = submitButtons[0];
          }
		  var form_data = $("#"+this.id).serialize();
	   	  var stepSubmit=$('input[name = stepSubmit ]').val();
	   	  var stepSave=$('input[name = stepSave ]').val();
		  var submit = submitActor.name;
          
          if(submit != null && submit == "stepSave") {
        	  form_data = form_data +"&stepSave="+stepSave;
          } else if(submit != null && submit == "stepSubmit"){
        	  form_data = form_data +"&stepSubmit="+stepSubmit;
          }
          $("#divCDResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 // alert("form_data==>"+form_data);
	      $.ajax({
	     		url: "AddNewAssessment.action",
	     		type: 'POST',
	     		data: form_data,
	     	    success: function(result){
	     			$("#divCDResult").html(result);
	     			
	     	    }, 
	 			error : function(err) {
	 				$.ajax({ 
						url: 'CourseDashboardData.action?dataType=A',
						cache: true,
						success: function(result){
							$("#divCDResult").html(result);
				   		}
					});
	 			}
	       });
	     
   });
   submitButtons.click(function(event) {
       submitActor = this;
   });
</script>


