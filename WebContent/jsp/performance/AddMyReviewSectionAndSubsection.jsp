
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<script src="scripts/ckeditor_cust/ckeditor.js"></script>
<script src="js/customAjax1.js"></script>
<script src='scripts/charts/jquery.min.js'></script>
<g:compress>

<script>
$(document).ready(function(){ 
	$("input[type='submit']").click(function(){
   		$("#formAddMyreviewLevelAndSystem").find('.validateRequired').filter(':hidden').prop('required',false);
   		$("#formAddMyreviewLevelAndSystem").find('.validateRequired').filter(':visible').prop('required',true);
   	});
});
 
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
	if ( editor.mode == 'wysiwyg' )
	{
		// Insert as plain text.
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertText
		editor.insertText( value );
	}
	else
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
	if ( editor.mode == 'wysiwyg' )
	{
		// Execute the command.
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-execCommand
		editor.execCommand( commandName );
	}
	else
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
	if(document.getElementById( 'eMessage')) {
		document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
	}
	
}

function onBlur() {
	if(document.getElementById( 'eMessage')) {
		document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
	}
}

function isNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	      return false;
	   }
	   return true;
	}
	
function isOnlyNumberKey(evt) {
	var charCode = (evt.which) ? evt.which : event.keyCode;
	if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
		return true; 
	}
	return false;
}

</script>


</g:compress>
<script type="text/javascript">
 
var cxtpath='<%=request.getContextPath()%>';

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
		//alert("in getcontent ... ");
		var scoreID='scoreID'+scoreCnt;
		var questionID='questionID'+questionCnt;
		var objectiveID='objectiveID'+objectiveCnt;
		var goalID='goalID'+goalCnt;
		var measureID='measureID'+measureCnt;

		var val = document.getElementById("scoreCard").value;
		//alert("in getcontent ... val : "+val);
	 	if (val == '1') {
	 		//alert("in getcontent ...in val_1 : "+val);
	 		var a = "<ul id=\""+scoreID+"\" class=\"level_list ul_class\">"
	 					+"<li>" + getScoreData(scoreCnt) + "</li>"
						+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" >Add Competency</a></li>"
						+ "<li id=\"goalDiv_"+scoreID+"\"><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\" class=\"form-control \"/>"
							+"<ul id=\""+goalID+"\">"
								+"<li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
								+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"');\">Add Goal</a></li>"
								+ "<li id=\"objDiv_"+goalID+"\"><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
									+"<ul id=\""+objectiveID+"\">"
										+"<li>" + getObjectiveData((scoreCnt+1)+'.1.1)',goalID,'0') + "</li>"
										+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addObjective('"+ goalID + "','"+ (scoreCnt+1)+'.1' + "');\">Add Objective</a></li>"
										+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" value=\"1\" class=\"form-control \"/>"
											+"<ul id=\""+measureID+"\">"
												+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1.1)','0',objectiveID) + "</li>"
												+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','"+ (scoreCnt+1)+'.1.1' + "');\">Add Measure</a></li>"
												+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\" class=\"form-control \"/>"
														+"<ul id=\""+questionID+"\" >"
														+"<li>"+ getquestion((scoreCnt+1)+'.1.1.1','.1)',measureID) + "</li>"
													+"</ul></li>"
												+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1.1.1' + "');\">Add Question</a></li>"
					+ "</ul></li></ul></li></ul></li></ul></li></ul></li></ul>";
			//alert("in getcontent ... a : "+a);
			document.getElementById("mainDiv").innerHTML = a; 
		} else if (val == '2') {
				
			var a = "<ul id=\""+scoreID+"\" class=\"level_list ul_class\">"
						+"<li>" + getScoreData(scoreCnt) + "</li>"
						+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" >Add Competency</a></li>"
						+ "<li id=\"measureDiv_"+scoreID+"\">"
							+"<input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+scoreID+"\" value=\"1\" class=\"form-control \"/>"
						  	 +"<ul id=\""+measureID+"\">"
						  	 	+"<li>"+ getMeasureData((scoreCnt+1)+'.1)','0',scoreID) + "</li>"
								+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ scoreID + "','"+ (scoreCnt+1) + "');\">Add Measure</a></li>"+
									"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\" class=\"form-control \"/>"
									+ "<ul id=\""+questionID+"\">"
										+"<li>"+ getquestion((scoreCnt+1)+'.1','.1)',measureID) + "</li>"
									+"</ul></li>"
									+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1' +"');\">Add Question</a></li>"
					+ "</ul></li></ul>";
					document.getElementById("mainDiv").innerHTML = a; 
			} else if (val == '3') { 

			var a = "<ul id=\""+scoreID+"\" class=\"level_list ul_class\">"
						+"<li>" + getScoreData(scoreCnt) + "</li>"
						+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" >Add Competency</a></li>"
						+ "<li id=\"goalDiv_"+scoreID+"\" ><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\" class=\"form-control \"/>"
							+"<ul id=\""+goalID+"\">"
								+"<li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
								+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"');\">Add Goal</a></li>"
								+ "<li id=\"measureDiv_"+goalID+"\"><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
									+"<ul id=\""+measureID+"\">"
										+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1)','0',goalID) + "</li>"
										+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ goalID + "','"+ (scoreCnt+1)+'.1' + "');\">Add Measure</a></li>"
										+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\" class=\"form-control \"/>"
											+"<ul id=\""+questionID+"\">"
												+"<li>"+ getquestion((scoreCnt+1)+'.1.1','.1)',measureID) + "</li>"
											+"</ul></li>"
										+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+(scoreCnt+1)+'.1.1'+"');\">Add Question</a></li>"
					+ "</ul></li></ul></li></ul></li></ul>";
					document.getElementById("mainDiv").innerHTML = a;
		} else {
			document.getElementById("mainDiv").innerHTML ="";
		}
	 	//jQuery("#formID").validationEngine();
	}

 
	  function addGoal(id,scoreCnt) {
		var cnt = goalCnt;
		var scnt = parseInt(scoreCnt-1);
		var totweight=0;
		 //alert("addGoal scoreCnt : " + scoreCnt);
			for(var i=0; i <= parseInt(cnt); i++){
				var weight = document.getElementById("goalWeightage"+id+i);
				if (weight == null){
					continue;	
				}
				weight = document.getElementById("goalWeightage"+id+i).value;
				if(weight == undefined){
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
			}
				var remainweight = 100 - parseFloat(totweight);
			if(remainweight == 0){
				alert("Unable to add goal because of no weightage available");			
			}else{
		
				 	goalCnt++;
					questionCnt++;
					measureCnt++;
					objectiveCnt++;
					
					var goalID='goalID'+goalCnt;
					var measureID='measureID'+measureCnt;
					var questionID='questionID'+questionCnt;
					var objectiveID='objectiveID'+objectiveCnt;
					var litag = document.createElement('ul');
					litag.id = goalID;
					
					var val=document.getElementById("goalcount"+id).value;
					
					document.getElementById("goalcount"+id).value=parseInt(val)+1;
					var val1 = document.getElementById("scoreCard").value;
					var goalDivId = "goalDiv_"+id;
				
					if (val1 == '1') {
						
						var a ="<li>" + getGoalData(scoreCnt+'.'+ (parseInt(val)+1)+')',id,goalCnt) + "</li>"
						+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ id + "','"+ scoreCnt +"');\">Add Goal</a>"
						 /* +"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\"> <img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/>  Delete Goal</a></li>" */
						 +"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" ></i> Delete Goal</a></li>"
						//+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"deleteBlock('"+ goalID + "','"+ id + "','goalcount');\">Delete Goal</a>"
			
						+ "<li id=\"objDiv_"+goalID+"\"><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
						+"<ul id=\""+objectiveID+"\">"
							+"<li>"+ getObjectiveData(scoreCnt+'.'+ (parseInt(val)+1)+'.1)',goalID,'0') + "</li>"
							+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addObjective('"+ goalID + "','"+ scoreCnt+'.'+ (parseInt(val)+1) +"');\">Add Objective</a></li>"
							+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+objectiveID+"\" value=\"1\"/>"
								+"<ul id=\""+measureID+"\">"
									+"<li>"+ getMeasureData(scoreCnt+'.'+ (parseInt(val)+1)+'.1.1)','0',objectiveID) + "</li>"
									+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','"+ scoreCnt+'.'+ (parseInt(val)+1)+'.1' + "');\">Add Measure</a></li>"
									+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
										+"<ul id=\""+questionID+"\" >"
										+"<li>"+ getquestion(scoreCnt+'.'+ (parseInt(val)+1)+'.1.1','.1)',measureID) + "</li>"
										+"</ul>"
									+"</li>"
									+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+scoreCnt+'.'+ (parseInt(val)+1)+'.1.1'+"');\">Add Question</a></li>"
								 +"</ul>"
							+"</li>"
						 +"</ul></li></ul></li>";
						
						litag.innerHTML = a;
					
					} else if (val1 == '3') {
						var a = "<li>" + getGoalData(scoreCnt+'.'+ (parseInt(val)+1)+')',id,goalCnt) + "</li>"
								+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ id + "','"+ scoreCnt +"');\">Add Goal</a>"
									/* +"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\"><img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/>  Delete Goal</a></li>" */
									+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" ></i> Delete Goal</a></li>"
								+ "<li id=\"measureDiv_"+goalID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+goalID+"\" value=\"1\"/>"
									+"<ul id=\""+measureID+"\">"
										+"<li>"+ getMeasureData(scoreCnt+'.'+ (parseInt(val)+1)+'.1)','0',goalID) + "</li>"
										+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ goalID + "','"+ scoreCnt+'.'+ (parseInt(val)+1) + "');\">Add Measure</a></li>"
										+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" class=\"form-control \" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
											+"<ul id=\""+questionID+"\">"
												+"<li>"+ getquestion(scoreCnt+'.'+ (parseInt(val)+1)+'.1','.1)',measureID) + "</li>"
											+"</ul></li>"
										+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ scoreCnt+'.'+ (parseInt(val)+1)+'.1' + "');\">Add Question</a></li>"
						+ "</ul></li></ul></li>";
						litag.innerHTML = a;
					}
					$('#'+id).find('#goalDiv_'+id).append(litag);
				    cnt = parseInt(cnt)+1;
				   	document.getElementById("goalWeightage"+id+cnt).value = remainweight;
					document.getElementById("hidegoalWeightage"+id+cnt).value = remainweight; 
				//	jQuery("#formID").validationEngine();
			}
	}
	  

	 function addObjective(id,scoreCnt) {
		 var cnt = objectiveCnt;
		 var scnt = parseInt(scoreCnt)-1;
		 var totweight=0;
		 //alert("addObjective scoreCnt : " + scoreCnt);
			for(var i=0; i <= parseInt(cnt); i++){
				var weight = document.getElementById("objectiveWeightage"+id+i);
				if (weight == null){
					continue;	
				}
				weight = document.getElementById("objectiveWeightage"+id+i).value;
				if(weight == undefined){
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
			}
				var remainweight = 100 - parseFloat(totweight);
			if(remainweight == 0){
				alert("Unable to add objective because of no weightage available");			
			}else{
				 objectiveCnt++;
				questionCnt++;
				measureCnt++;
				
				var objectiveID='objectiveID'+objectiveCnt;
				var measureID='measureID'+measureCnt;
				var questionID='questionID'+questionCnt;
				var litag = document.createElement('ul');
				litag.id = objectiveID;
				var objectiveDivId = "objDiv_"+id;
				var val=document.getElementById("objectivecount"+id).value;
				var measureDivCnt = parseInt(objectiveCnt)-1;
				document.getElementById("objectivecount"+id).value=parseInt(val)+1;
				
				var a = "<li>" + getObjectiveData(scoreCnt+'.'+(parseInt(val)+1)+')',id,objectiveCnt) + "</li>"
						+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addObjective('"+ id + "','"+ scoreCnt + "');\">Add Objective</a>"
						/* +"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ objectiveID + "','"+ objectiveDivId + "','objectivecount');\"><img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/> Delete Objective</a>" */
						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ objectiveID + "','"+ objectiveDivId + "','objectivecount');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" ></i> Delete Objective</a>"
						+"</li>"
						+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+objectiveID+"\" value=\"1\"/>"
							+"<ul id=\""+measureID+"\">" 
								+"<li>"+ getMeasureData(scoreCnt+'.'+(parseInt(val)+1)+'.1)','0',objectiveID) + "</li>"
								+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','"+scoreCnt+'.'+(parseInt(val)+1)+"');\">Add Measure</a></li>"
								+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
								+"<ul id=\""+questionID+"\">"
								+"<li>"+ getquestion(scoreCnt+'.'+(parseInt(val)+1)+'.1','.1)',measureID) 
								+ "</li></ul></li>"
								+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ scoreCnt+'.'+(parseInt(val)+1)+'.1' + "');\">Add Question</a></li>"
							+ "</ul></li></ul></li></ul>";
		
				litag.innerHTML = a;
				$('#'+id).find('#objDiv_'+id).append(litag);
				cnt = parseInt(cnt)+1;
				
				document.getElementById("objectiveWeightage"+id+cnt).value = remainweight;
				document.getElementById("hideobjectiveWeightage"+id+cnt).value = remainweight;
			//	jQuery("#formID").validationEngine();
		}
	} 
	
	  function addScoreCard(){
		 scoreCnt++;
		 var cnt = scoreCnt;
		 var totweight=0;
		 //alert("cnt : " + cnt);
			for(var i=1; i <= parseInt(cnt); i++){
				var weight = document.getElementById("scoreCardWeightage"+i);
				if (weight == null){
					continue;	
				}
				weight = document.getElementById("scoreCardWeightage"+i).value;
				if(weight == undefined){
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
			}
				var remainweight = 100 - parseFloat(totweight);
			if(remainweight == 0){
				alert("Unable to add compentency because of no weightage available");
				scoreCnt--;
			}else{
				//scoreCnt++;
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
						+"<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" addScoreCard()\" >Add Competency</a>"
						/* +"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\"><img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/> Delete Competency</a></li>" */
						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" ></i> Delete Competency</a></li>"
						//+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" >Delete Competency</a></li>"
						+ "<li id=\"goalDiv_"+scoreID+"\"><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\" class=\"form-control \"/><ul id=\""+goalID+"\"><li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
						+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"');\">Add Goal</a></li>"
						
						+ "<li id=\"objDiv_"+goalID+"\" ><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
							+"<ul id=\""+objectiveID+"\">"
								+"<li>" + getObjectiveData((scoreCnt+1)+'.1.1)',goalID,'0') + "</li>"
								+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addObjective('"+ goalID + "','"+ (scoreCnt+1)+'.1' + "');\">Add Objective</a></li>"
						    	+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" class=\"form-control \" value=\"1\"/>"
						    			+"<ul id=\""+measureID+"\">"
						    				+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1.1)','0',objectiveID) + "</li>"
											+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','" + (scoreCnt+1)+'.1.1' + "');\">Add Measure</a></li>"
											+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
												+"<ul id=\""+questionID+"\">"
													+"<li>"+ getquestion((scoreCnt+1)+'.1.1.1','.1)',measureID) + "</li>"
												+"</ul>"
											+"</li>"
											+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1.1.1' + "');\">Add Question</a></li>"
								+ "</ul></li></ul></li></ul></ul></li></ul>";
								
					 litag.innerHTML = a;
					 document.getElementById("mainDiv").appendChild(litag);
						
				}else if (val == '2') {
					var a = "<li>" + getScoreData(scoreCnt) + "</li>"
							+"<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" addScoreCard()\" >Add Competency</a>"
							/* +"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\"><img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/> Delete Competency</a></li>" */
							+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" ></i> Delete Competency</a></li>"
					
							+ "<li id=\"measureDiv_"+scoreID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+scoreID+"\" value=\"1\" />"
								+"<ul id=\""+measureID+"\">"
									+"<li>"+ getMeasureData((scoreCnt+1)+'.1)','0',scoreID) + "</li>"
										+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('" + scoreID + "','" + (scoreCnt+1) + "');\">Add Measure</a>"
										+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" class=\"form-control \" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
											+ "<ul id=\""+questionID+"\">"
												+"<li>"+ getquestion((scoreCnt+1)+'.1','.1)',measureID)
									+ "</li></ul></li>"
									+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1' + "');\">Add Question</a></li>"
							+ "</ul></li></ul></li>";
					litag.innerHTML = a;
					document.getElementById("mainDiv").appendChild(litag);
				}
				 else if (val == '3') {
					 var a = "<li>" + getScoreData(scoreCnt) + "</li>"
							+"<li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" addScoreCard()\" >Add Competency</a>"
							/* +"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\"><img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/> Delete Competency</a></li>" */
							+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i> Delete Competency</a></li>"
						
								+ "<li id=\"goalDiv_"+scoreID+"\"><input type=\"hidden\" name=\"goalcount\" class=\"form-control \" id=\"goalcount"+scoreID+"\" value=\"1\"/>"
									+"<ul id=\""+goalID+"\">"
										+"<li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
										+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"');\">Add Goal</a></li>"
										+ "<li id=\"measureDiv_"+goalID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+goalID+"\" value=\"1\"/>"
											+"<ul id=\""+measureID+"\">"
												+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1)','0',goalID) + "</li>"
												+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addMeasure('"+ goalID + "','" + (scoreCnt+1)+'.1' + "');\">Add Measure</a></li>"
												+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" class=\"form-control \" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
													+"<ul id=\""+questionID+"\">"
														+"<li>"+ getquestion((scoreCnt+1)+'.1.1','.1)',measureID) 
												+ "</li></ul></li>"
												+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1.1' + "');\">Add Question</a></li>"
							+ "</ul></li></ul></li></ul></li>";
				 	litag.innerHTML = a;
					document.getElementById("mainDiv").appendChild(litag);
				}
				 cnt = parseInt(cnt)+1;
				 //alert("parseInt(cnt)+1  :: "+cnt);
				document.getElementById("scoreCardWeightage"+cnt).value = remainweight;
				document.getElementById("hidescoreCardWeightage"+cnt).value = remainweight;
				//jQuery("#formID").validationEngine();
		}
 	}



	    function addQuestions(id,scoreCnt) {
			 var cnt =questionCnt;
			// alert("id==>"+id+"=>scoreCnt==>"+scoreCnt+"=>cnt=>"+cnt+"=>questionCnt=>"+questionCnt);
			 var totweight=0;
			 for(var i=0; i <= parseInt(cnt); i++) {
				var weight = 0;
				if(document.getElementById("weightage"+id+i)) {
					weight = document.getElementById("weightage"+id+i);
					if (weight == null){
						continue;	
					}
					weight = document.getElementById("weightage"+id+i).value;
					if(weight == undefined){
						weight = 0;
					}
				}
				totweight = totweight + parseFloat(weight);
			}
			
			var remainweight = 100 - parseFloat(totweight);
			
			if(remainweight == 0) {
				alert("Unable to add questions because of no weightage available");			
			}else{ 
				var litag = document.createElement('ul');
				questionCnt++;
				//alert("questionCnt==>"+questionCnt);
				var QuestionID = "questionID" + questionCnt;
				var val= 0;
				//alert("val==>"+val);
				if(document.getElementById("questioncount"+id)) {
					val = document.getElementById("questioncount"+id).value;
					document.getElementById("questioncount"+id).value=parseInt(val)+1;
				}
				//alert("QuestionID==>"+QuestionID+"==>val==>"+val);
				litag.id = QuestionID;
				var a =  getquestion(scoreCnt,'.'+(parseInt(val)+1)+')',id) ;
				// +"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ id+ "');\">Add Question</a>"
				//+"&nbsp;&nbsp;<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"deleteBlock('"+QuestionID+"','"+ id+ "','questioncount');\">Remove Question</a>";
				litag.innerHTML = a;
				//alert("a==>"+a);
				//alert("id==>"+id);
				document.getElementById("questDiv_"+id).appendChild(litag);
				cnt = parseInt(cnt)+1;
				document.getElementById("weightage"+id+cnt).value = remainweight;
				document.getElementById("hideweightage"+id+cnt).value = remainweight;
				//jQuery("#formID").validationEngine();
		}
	 }

	    function addMeasure(id,scoreCnt) {
	    
			var cnt = measureCnt;
			var scnt = parseInt(scoreCnt)-1;
			 var totweight=0;
			// alert("cnt : " + cnt);
				for(var i=0; i <= parseInt(cnt); i++){
					var weight = document.getElementById("measureWeightage"+id+i);
					if (weight == null){
						continue;	
					}
					weight = document.getElementById("measureWeightage"+id+i).value;
					if(weight == undefined){
						weight = 0;
					}
					totweight = totweight + parseFloat(weight);
				}
					var remainweight = 100 - parseFloat(totweight);
				if(remainweight == 0){
					alert("Unable to add measure because of no weightage available");			
				}else{
					
					questionCnt++;
					measureCnt++;
					
					var measureID='measureID'+measureCnt;
					var questionID='questionID'+questionCnt;
					var litag = document.createElement('ul');
					var measureDivId = "measureDiv_"+id;
					
					litag.id = measureID;
					var val=document.getElementById("measurecount"+id).value;
					
					document.getElementById("measurecount"+id).value=parseInt(val)+1;
					var a = "<li>"+ getMeasureData(scoreCnt+'.'+(parseInt(val)+1)+')',measureCnt,id)+ "</li>"
									+"<li><a href=\"javascript:void(0)\" class=\"add_lvl\"  onclick=\"addMeasure('"+ id + "','"+ scoreCnt + "')\"  >Add Measure</a>"
										+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+measureID+"','" +measureDivId+ "','measurecount');\">"
										/* +"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/> Delete Measure</a>" */
										+"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i> Delete Measure</a>"
									+"</li>"
									+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
										+"<ul id=\""+questionID+"\">"
										+"<li>"+ getquestion(scoreCnt+'.'+(parseInt(val)+1),'.1)',measureID)
									+ "</li></ul></li>"
									+"<li><a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ scoreCnt+'.'+(parseInt(val)+1)+ "');\">Add Question</a>"
							+"</li></ul>";
					//alert(a);
					litag.innerHTML = a;
				//	console.log($('#'+id).find('#measureDiv_'+id));
					$('#'+id).find('#measureDiv_'+id).append(litag);
					cnt = parseInt(cnt)+1;
					document.getElementById("measureWeightage"+id+cnt).value = remainweight;
					document.getElementById("hidemeasureWeightage"+id+cnt).value = remainweight;
					//jQuery("#formID").validationEngine();
				}
		}
	

	    function getquestion(scoreCnt,val,id) {
			
			var cnt=questionCnt;
			//var selec = getOrientationData(cnt);
			var QuestionID = "QuestionID" + questionCnt;
			var aa = getQuestoinContentType(cnt);
			//alert("getquestion aa : "+ aa);
			var sectioncount = document.getElementById("hidesectioncnt").value;
			var subsectioncount = document.getElementById("hidesubsectioncnt").value;
			var subsectiontitle = document.getElementById("subsectionname").value;
			//alert("getquestion sectioncount "+ sectioncount + " subsectioncount :"+subsectioncount);
			var gencntid = "";
			if(subsectiontitle == ""){
				gencntid = sectioncount;
			}else{
				gencntid = sectioncount+subsectioncount;
			}
			
			var a = "<li>"
				 	//+"<p style=\"margin-bottom: 10px; text-align: left; font-size: 12px; font-weight: bold;\">Assessments of \" <span id=\"subsectionnamespan\">New Subsection</span> \"</p>"
			 		+"<table class=\"table\" width=\"100%\">"
					+ "<tr><th>"+scoreCnt+val+"</th><th width=\"17%\" style=\"text-align: right;\">Add Question<sup>*</sup></th>"
					+"<td colspan=\"3\"><span id=\"newquespan"+cnt+"\" style=\"float: left; \"><input type=\"hidden\" name=\"hidequeid\" id=\"hidequeid"+cnt+"\" value=\"0\"/>"
					+"<textarea rows=\"2\" name=\"question\" id=\"question"+cnt+"\" class=\"validateRequired form-control \"  style=\"width: 330px;\"></textarea>"
					//+"<input type=\"text\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\"/>"
					+"</span>"
					
					+ "&nbsp;<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt\" value=\""+cnt+"\"/><sup>*</sup>"
					+"<input type=\"number\" style=\"width: 35px;\" name=\"weightage\" id=\"weightage"+id+cnt+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired form-control \" onkeyup=\"validateScore(this.value,'weightage"+id+cnt+"','"+id+"','weightage');\"/>"
					+ "<input type=\"hidden\" name=\"hideweightage\" id=\"hideweightage"+id+cnt+"\" value=\"100\"/></span>&nbsp;&nbsp;"

					+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"');openOtheQueMode('NQ','"+cnt+"');\" > +Q </a></span>&nbsp;"
					+"<span id=\"checkboxspan"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+ cnt+ "')\" />"
					+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/></span>"
					
					+"<a href=\"javascript:void(0)\" class=\"add_lvl\" title=\"Add New Question\" onclick=\"addQuestions('"+id+"','"+scoreCnt+"')\" ></a>&nbsp;&nbsp; "
					/* +"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Question\" onclick=\"deleteBlock('"+QuestionID+"','"+ id+ "','questioncount');\"/>" */
					+"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove Question\" onclick=\"deleteBlock('"+QuestionID+"','"+ id+ "','questioncount');\"></i>"

					+"<input type=\"hidden\" name=\"questiontypename\" value=\""+ cnt+"\" /></td></tr>"
					+aa
					+"</table></li>";
					//jQuery("#formID").validationEngine();
			return a;
			//jQuery("#formID").validationEngine();
		}

		function getScoreData(count) {
			count++;
			var a = "<table class=\"table\" width=\"100%\"><tr><th style=\"width: 4%;\">"+count+")</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Competency</td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Section name<sup>*</sup></th><td><input type=\"text\" name=\"scoreSectionName\" id=\"scoreSectionName\" class=\"validateRequired form-control \" style=\"width: 450px;\" /></td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"scoreCardDescription\" style=\"width: 450px;\"	/></td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"number\" name=\"scoreCardWeightage\"	id=\"scoreCardWeightage"+count+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired form-control \" value=\"100\" onkeyup=\"validateScore1(this.value,'scoreCardWeightage"+count+"','scoreCardWeightage');\"/>"
					+ "<input type=\"hidden\" name=\"hidescoreCardWeightage\" id=\"hidescoreCardWeightage"+count+"\" value=\"100\" /></td></tr>"
					+"</table>";
					//jQuery("#formID").validationEngine();
			return a;
		//	jQuery("#formID").validationEngine();
		}

		 function getObjectiveData(val,id,objectiveCnt) {
			var a = "<table class=\"table\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Objective"
					+ "</td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Section name<sup>*</sup></th><td><input type=\"text\" name=\"objectiveSectionName\" id=\"objectiveSectionName\" class=\"validateRequired form-control \" style=\"width: 450px;\" /></td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"objectiveDescription\" style=\"width: 450px;\" /></td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"text\" name=\"objectiveWeightage\" id=\"objectiveWeightage"+id+objectiveCnt+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired form-control \" value=\"100\" onkeyup=\"validateScore(this.value,'objectiveWeightage"+id+objectiveCnt+"','"+id+"','objectiveWeightage');\"/>"
					+ "<input type=\"hidden\" name=\"hideobjectiveWeightage\" id=\"hideobjectiveWeightage"+id+objectiveCnt+"\" value=\"100\" /></td></tr></table>";
					//jQuery("#formID").validationEngine();
			return a;
			//jQuery("#formID").validationEngine();
		} 

		function getMeasureData(val,measureCnt,id) {

			var a = "<table class=\"table\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Measures</td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Section name<sup>*</sup></th><td><input type=\"text\" name=\"measuresSectionName\" id=\"measuresSectionName\" class=\"validateRequired form-control \" style=\"width: 450px;\" /></td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"measuresDescription\" style=\"width: 450px;\" /></td></tr>"
					+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"text\" name=\"measureWeightage\" id=\"measureWeightage"+id+measureCnt+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired form-control \" value=\"100\" onkeyup=\"validateScore(this.value,'measureWeightage"+id+measureCnt+"','"+id+"','measureWeightage');\"/>"
					+ "<input type=\"hidden\" name=\"hidemeasureWeightage\" id=\"hidemeasureWeightage"+id+measureCnt+"\" value=\"100\" /></td></tr>"
					+ "</table>";
				//	jQuery("#formID").validationEngine();
			return a;
			//jQuery("#formID").validationEngine();
		}

		 function getGoalData(val,id,goalCnt) {
				var a = "<table class=\"table\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th>"
						+ "<td>Goals</td></tr>"
						+ "<tr><th></th><th style=\"text-align: right;\">Section name<sup>*</sup></th><td><input type=\"text\" name=\"goalSectionName\" id=\"goalSectionName\" class=\"validateRequired form-control \" style=\"width: 450px;\" /></td></tr>"
						+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"goalDescription\" style=\"width: 450px;\" /></td></tr>"
						+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"text\" name=\"goalWeightage\" id=\"goalWeightage"+id+goalCnt+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired form-control \" value=\"100\" onkeyup=\"validateScore(this.value,'goalWeightage"+id+goalCnt+"','"+id+"','goalWeightage');\"/>"
						+ "<input type=\"hidden\" name=\"hidegoalWeightage\" id=\"hidegoalWeightage"+id+goalCnt+"\" value=\"100\" /></td>"
						+ "</tr></table>";
						//jQuery("#formID").validationEngine();
				return a;
			///	jQuery("#formID").validationEngine();
			}
	 
	 
  function showAssessOfSubsection(){
		var a="<ul class=\"level_list ul_class\">"
			+"<p style=\"margin-bottom: -15px; text-align: left; margin-left: 110px; font-size: 12px; font-weight: bold;\">Assessments of \" <span id=\"subsectionnamespan\">New Subsection</span> \"</p>"
		+"</ul>";
		document.getElementById("assessOfSubsectionDiv").innerHTML = a; 
	} 
  
  function getOtherquestion(oldcnt) {
	  var totweight=0;
//	  if(parseInt(oldcnt) == 0)
		  oldcnt = questionCnt;
		for(var i=1; i <= parseInt(oldcnt); i++){
			var weight = document.getElementById("weightage"+i);
			if (weight == null){
				continue;	
			}
			weight = document.getElementById("weightage"+i).value;
			if(weight == undefined){
				weight = 0;
			}
			totweight = totweight + parseFloat(weight);
		}
		var remainweight = 100 - parseFloat(totweight);
		if(parseInt(remainweight) <= 0){
			alert("Unable to add questions because of no weightage available");			
		}else{
	  	showAssessOfSubsection();
		questionCnt++;
		var cnt=questionCnt;
		var ultag = document.createElement('ul');
		var aa = getQuestoinContentType(cnt);
		var otherQueType = document.getElementById("otherQuestionType").value;
		var sectioncount = document.getElementById("hidesectioncnt").value;
		var subsectioncount = document.getElementById("hidesubsectioncnt").value;
		var subsectiontitle = document.getElementById("subsectionname").value;
		var gencntid = "";
		 if(subsectiontitle == ""){
			gencntid = sectioncount;
		}else{
			gencntid = sectioncount+"."+subsectioncount;
		} 
		var othrQtype="",otherSD="";
		if(otherQueType == "With Short Description"){
			otherSD = "<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Description\" onclick=\"openOtheQueShortD('"+cnt+"')\" > D </a></span>";
			othrQtype = "<tr id=\"shortdescTr"+cnt+"\" style=\"display: none;\"><th></th><th style=\"text-align: right;\">Short Description</th><td colspan=\"3\"><input type=\"hidden\" name=\"hideotherSD\" id=\"hideotherSD"+cnt+"\" value=\"f\"/><input type=\"text\" name=\"otherSDescription\" id=\"otherSDescription\" style=\"width: 450px;\" /></td></tr>";
		}else{
			otherSD = "<span style=\"float: left; margin-left: 10px;\"> D </span>";
			othrQtype="";
		}
		ultag.id = "otherQuestionUl"+cnt;
		
		 var a = "<li><table class=\"table table_no_border\">"
				+ "<tr><th>"+gencntid+"."+questionCnt+")</th><th width=\"17%\" style=\"text-align: right;\">Add Question<sup>*</sup></th>"
				+ "<td colspan=\"3\"><span id=\"newquespan"+cnt+"\" style=\"float: left; \"><input type=\"hidden\" name=\"hidequeid\" id=\"hidequeid"+cnt+"\" value=\"0\"/>"
				+"<textarea rows=\"2\" name=\"question\" id=\"question"+cnt+"\" class=\"validateRequired form-control\"  style=\"width: 330px;\"></textarea>"
				//+"<input type=\"text\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\"/>"
				+"</span>"

				+ "&nbsp;<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt\" value=\""+cnt+"\"/><sup>*</sup>"
				+"<input type=\"number\" style=\"width: 35px !important;\" name=\"weightage\" id=\"weightage"+cnt+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired form-control\" value=\"100\" onkeyup=\"validateScore1(this.value,'weightage"+cnt+"','weightage');\"/>"
				+"<input type=\"hidden\" name=\"hideweightage\" id=\"hideweightage"+cnt+"\" value=\"100\"/></span>&nbsp;&nbsp;"
				+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"');\" > +Q </a></span>&nbsp;"+ otherSD +"&nbsp;"
				+"<span id=\"checkboxspan"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+ cnt+ "')\" />"
				+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/></span>"
				
				+"<a href=\"javascript:void(0)\" class=\"add-font\" title=\"Add New Question\" onclick=\"getOtherquestion('"+cnt+"')\" ></a>&nbsp;&nbsp; "
				+"<a href=\"javascript:void(0)\" class=\"remove-font\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\"></a>"
/* 				+"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\"/>"
 */				//+"<a href=\"javascript:void(0)\" class=\"add_lvl\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\" ></a>"
				+"<input type=\"hidden\" name=\"questiontypename\" value=\""+ cnt+"\" /></td></tr>"+othrQtype
				+aa
				+"</table></li>";

		ultag.innerHTML = a;
		
			document.getElementById("otherQuestionLi").appendChild(ultag); 	
		
		var subsname = document.getElementById("subsectionname").value;
		var lvltitle = document.getElementById("levelTitle").value;
		//alert("subsname : " + subsname);
		if(subsname == '' && lvltitle == ''){
			document.getElementById("subsectionnamespan").innerHTML = document.getElementById("main_level_name").value;
		}else if(subsname == ''){
			document.getElementById("subsectionnamespan").innerHTML = document.getElementById("levelTitle").value;	
		}else {
			document.getElementById("subsectionnamespan").innerHTML = document.getElementById("subsectionname").value;	
		}
		
		document.getElementById("weightage"+cnt).value = remainweight;
		document.getElementById("hideweightage"+cnt).value = remainweight;
		if(document.getElementById("answerTypeSelect")) {
			var spanAnsType=document.getElementById("answerTypeSelect").value;
			if(spanAnsType=='1'){
				document.getElementById("spanAnsType"+cnt).innerHTML="With Remark";
			}else if(spanAnsType=='2'){
				document.getElementById("spanAnsType"+cnt).innerHTML="Without Remark";
			}
		}
		if(document.getElementById("hidecheckWeightage")) {
			var checkWeightage=document.getElementById("hidecheckWeightage").value;
			if(checkWeightage=='1'){ 
				document.getElementById("weightageTr"+cnt).style.display = 'table-row';
			}else if(checkWeightage=='0'){
				document.getElementById("weightageTr"+cnt).style.display = 'none';
			}
		}
		//jQuery("#formID").validationEngine();
	 }	
	}
  

  function getQuestoinContentType(cnt){
		//alert("getQuestoinContentType ");
		var val = document.getElementById("ansType").value;
		//alert("getQuestoinContentType val : "+val);
		var a="";
		if(val == 8){
			a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
			+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
		
		}else if (val == 1 || val == 2 || val == 9) {
			a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<span id=\"aspan\"><input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<span id=\"bspan\"><input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
			+"<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<span id=\"cspan\"><input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<span id=\"dspan\"><input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";

		 }else if (val == 6) {
			a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
				+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td></tr>";
		
		}else if (val == 5) {
			a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
			+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td></tr>";
		} else if(val == 13) {
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
  
	function openOtheQueMode(mode,cnt){
		if(mode== 'NQ'){
			document.getElementById("question"+cnt).value = '';
		document.getElementById("checkboxspan" + cnt1).style.display = 'block';
		}else{
			document.getElementById("question"+cnt).value = '';
		document.getElementById("checkboxspan").style.display = 'none';
		}
	}
  
	function openOtheQueShortD(cnt){
		var dstatus = document.getElementById("hideotherSD"+cnt).value;
		if(dstatus == 'f'){
			document.getElementById("hideotherSD"+cnt).value='t';
			document.getElementById("shortdescTr"+cnt).style.display = 'table-row';
		}else{
			document.getElementById("hideotherSD"+cnt).value='f';
			document.getElementById("shortdescTr"+cnt).style.display = 'none';
		}
	}
  

	function removeOtherquestion(id){
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
		}
	}
	
	
	var dialogEdit = '#SelectQueDiv';
	function openQuestionBank(count,callFrom) {
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
		} else{
			if(document.getElementById('ansType') != null){
				ansType=document.getElementById('ansType').value;
			}
		}
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Question Bank');
		$.ajax({
			url : "SelectQuestion.action?count="+count+"&ansType="+ansType,
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
                        	if(data != "" && data.trim().length > 0) {
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
	
	
	function closePopup(){
		$(dialogEdit).dialog('close');
	}
	function closeEditPopup(){
		$(dialogEdit1).dialog('close');
	}
	
	function openSectionDiv(type){
		//alert("in openSectionDiv type : " + type);
		var value = '<%=(request.getAttribute("divCount"))%>';
		if (type == 'section') {
			var lvltitle = document.getElementById("levelTitle").value;
			if (lvltitle == '') {
				document.getElementById("sectionnamespan").innerHTML = document
						.getElementById("main_level_name").value;
			} else {
				document.getElementById("sectionnamespan").innerHTML = document
						.getElementById("levelTitle").value;
			}
			document.getElementById("assessdiv" + value).style.display = 'none';
			document.getElementById("sectionAssessmentdiv" + value).style.display = 'block';
			document.getElementById("assessLinkDiv" + value).style.display = 'block';
			document.getElementById("sectiondiv" + value).style.display = 'block';
			document.getElementById("sectionLinkDiv" + value).style.display = 'none';
			document.getElementById("subSectionWeightage").value = document
					.getElementById("hidesubSectionWeightage").value;
		} else {
			document.getElementById("assessdiv" + value).style.display = 'block';
			document.getElementById("sectionAssessmentdiv" + value).style.display = 'block';
			document.getElementById("sectionLinkDiv" + value).style.display = 'block';
			document.getElementById("sectiondiv" + value).style.display = 'none';
			document.getElementById("assessLinkDiv" + value).style.display = 'none';
			document.getElementById("subsectionname").value = '';
			document.getElementById("subsectionDescription").value = '';
			document.getElementById("subSectionWeightage").value = document
					.getElementById("hidesubSectionWeightage").value;
		}

	}

	function changeNewQuestionType1(val, id, id1, cnt) {
		//alert("val : "+ val +" id : "+ id + " id1 : "+ id1 + " cnt : " + cnt);
		if (val == 1 || val == 2 || val == 8) {
			addQuestionType11(id, cnt);
			document.getElementById(id).style.display = 'table-row';
			addQuestionType21(id1, cnt);
			document.getElementById(id1).style.display = 'table-row';
		} else if (val == 9) {
			addQuestionType31(id, cnt);
			document.getElementById(id).style.display = 'table-row';
			addQuestionType41(id1, cnt);
			document.getElementById(id1).style.display = 'table-row';
		} else if (val == 6) {
			addTrueFalseType1(id, cnt);
			document.getElementById(id).style.display = 'table-row';
			document.getElementById(id1).innerHTML = "";
			document.getElementById(id1).style.display = 'none';
		} else if (val == 5) {
			addYesNoType1(id, cnt);
			document.getElementById(id).style.display = 'table-row';
			document.getElementById(id1).innerHTML = "";
			document.getElementById(id1).style.display = 'none';
		} else {
			addQuestionType11(id, cnt);
			addQuestionType21(id1, cnt);
			document.getElementById(id).style.display = 'none';
			document.getElementById(id1).style.display = 'none';
		}
		//showAnswerTypeDiv(val);
	}

	function addTrueFalseType1(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"3\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
				+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
	}
	function addYesNoType1(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"3\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
				+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
	}
	function addQuestionType11(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired\" /><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
	}
	function addQuestionType21(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired\"  /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
	}
	function addQuestionType31(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
	}
	function addQuestionType41(id1, cnt) {
		document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired\"/> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
	}

	function openOtherSystemNewQue(value, totWeightage) {
		var remainWeightage = 100 - parseFloat(totWeightage);
		if (parseInt(remainWeightage) <= 0) {
			alert("Unable to add questions because of no weightage available ");
		} else {
			document.getElementById("weightage" + value).value = remainWeightage;
			document.getElementById("hideweightage" + value).value = remainWeightage;
			document.getElementById("OTHERnewquedivOfQ" + value).style.display = "block";
			document.getElementById("OTHERsavebtndivOfQ" + value).style.display = "block";
		}
	}

	/* function openAddNewSystem(id,MLID,type,sysdiv,newsysno,totWeightage,linkType,linkDiv,divCount) {
	 var remainWeightage = 100 - parseFloat(totWeightage);
	 if(parseInt(remainWeightage) <= 0){
	 alert("Unable to add subsection because of no weightage available ");
	 }else{
	 document.getElementById(linkDiv).style.display="none";
	 var action = 'openAppraisalLevelAndSystem.action?id=' + id + '&MLID=' + MLID + '&type=' + type +'&sysdiv=' + sysdiv 
	 + '&newsysno=' + newsysno+"&subWeightage="+remainWeightage+"&linkDiv="+linkDiv+"&divCount="+divCount;
	 getContent(sysdiv, action);
	 document.getElementById(sysdiv).style.display="block";
	 if(document.getElementById(sysdiv).style.display =='block'){
	 openSectionDiv(linkType,divCount);
	 } 
	 }
	 } */

	function closeIframe(quediv, linkDiv, value) {
		if (parent.document.getElementById("sectionLinkSpan" + value)) {
			parent.document.getElementById("sectionLinkSpan" + value).style.display = 'block';
		}
		if (parent.document.getElementById("assessLinkSpan" + value)) {
			parent.document.getElementById("assessLinkSpan" + value).style.display = 'block';
		}

		$("#" + quediv).empty();
		$("#" + quediv).css("display", 'none');
	}

	function addNewQuestion(id, val, cnt1) {
		//alert("val : " + val + "  cnt1 : " + cnt1);
		if (val == '0') {
			document.getElementById("QuestionName" + cnt1).style.display = 'table-row';
			document.getElementById("AddQuestion" + cnt1).style.display = 'table-row';
			document.getElementById("WeightageIn" + cnt1).style.display = 'table-row';
			document.getElementById("selectanstype" + cnt1).style.display = 'table-row';
			document.getElementById("answerType" + cnt1).style.display = 'table-row';
			document.getElementById("answerType1" + cnt1).style.display = 'table-row';
			if(document.getElementById("answerType2" + cnt1)) {
				document.getElementById("answerType2" + cnt1).style.display = 'table-row';
			}
			document.getElementById("Weightage" + cnt1).style.display = 'none';
		} else {
			document.getElementById("Weightage" + cnt1).style.display = 'table-row';
			document.getElementById("QuestionName" + cnt1).style.display = 'none';
			document.getElementById("AddQuestion" + cnt1).style.display = 'none';
			document.getElementById("WeightageIn" + cnt1).style.display = 'none';
			document.getElementById("selectanstype" + cnt1).style.display = 'none';
			document.getElementById("answerType" + cnt1).style.display = 'none';
			document.getElementById("answerType1" + cnt1).style.display = 'none';
			if(document.getElementById("answerType2" + cnt1)) {
				document.getElementById("answerType2" + cnt1).style.display = 'none';
			}
		}
	}

	function addNewQuestionByLink(id, val, cnt1, selectindex) {
		//alert("val : " + val + "  cnt1 : " + cnt1 + " selectindex : " + selectindex);
		if (val == '0') {
			document.getElementById("QuestionName" + cnt1).style.display = 'table-row';
			document.getElementById("AddQuestion" + cnt1).style.display = 'table-row';
			document.getElementById("WeightageIn" + cnt1).style.display = 'table-row';
			document.getElementById("selectanstype" + cnt1).style.display = 'table-row';
			document.getElementById("answerType" + cnt1).style.display = 'table-row';
			document.getElementById("answerType1" + cnt1).style.display = 'table-row';
			if(document.getElementById("answerType2" + cnt1)) {
				document.getElementById("answerType2" + cnt1).style.display = 'table-row';
			}
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
			if(document.getElementById("answerType2" + cnt1)) {
				document.getElementById("answerType2" + cnt1).style.display = 'none';
			}
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
		getContent("anstypediv", action);
	}

	function showAnswerTypeDiv1(ansType) {
		var action = 'ShowAnswerType.action?ansType=' + ansType;
		getContent("anstypediv", action);
	}

	function validateScore(value1, weightageid, id, remweightageid) {
		//alert("value1 ===>> " + value1 +"  weightageid ===>> " + weightageid + " remweightageid ===>> " + remweightageid);
		//var weightCnt = document.getElementsByName(remweightageid);
		//alert("weightCnt ===>>>> " + weightCnt.length);

		var weightCnt = 0;
		if (remweightageid == 'weightage') {
			weightCnt = questionCnt;
		} else if (remweightageid == 'objectiveWeightage') {
			weightCnt = objectiveCnt;
		} else if (remweightageid == 'measureWeightage') {
			weightCnt = measureCnt;
		} else if (remweightageid == 'goalWeightage') {
			weightCnt = goalCnt;
		}
		var totweight = 0;
		for ( var i = 0; i <= parseInt(weightCnt); i++) {
			var checkCurrId = remweightageid + id + i;
			var weight = document.getElementById(checkCurrId);
			//alert("weight ===>>>> " + weight);
			if (weight == null) {
				continue;
			}

			if (weightageid == checkCurrId) {
				//	alert("same id");
			} else {
				weight = document.getElementById(checkCurrId).value;
				if (weight == undefined) {
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
			}
		}
		var remainweight = 100 - parseFloat(totweight);

		//var remainWeightage = document.getElementById(weightagehideid).value;
		// alert("remainweight =========>> " + remainweight);
		if (parseFloat(value1) > parseFloat(remainweight)) {
			alert("Entered value greater than Weightage");
			document.getElementById(weightageid).value = remainweight;
		} else if (parseFloat(value1) <= 0) {
			alert("Invalid Weightage");
			document.getElementById(weightageid).value = remainweight;
		}
	}

	function validateScore1(value1, weightageid, remweightageid) {
		//var weightCnt = document.getElementsByName(remweightageid);

		var weightCnt = 0;
		if (remweightageid == 'weightage') {
			weightCnt = questionCnt;
		} else if (remweightageid == 'scoreCardWeightage') {
			weightCnt = scoreCnt;
		}
		var totweight = 0;
		for ( var i = 1; i <= (parseInt(weightCnt) + 1); i++) {
			var checkCurrId = remweightageid + i;
			var weight = document.getElementById(checkCurrId);
			if (weight == null) {
				continue;
			}

			if (weightageid == checkCurrId) {
			} else {
				weight = document.getElementById(checkCurrId).value;
				if (weight == undefined) {
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
			}
		}
		var remainweight = 100 - parseFloat(totweight);
		if (parseFloat(value1) > parseFloat(remainweight)) {
			alert("Entered value greater than Weightage");
			document.getElementById(weightageid).value = remainweight;
		} else if (parseFloat(value1) <= 0) {
			alert("Invalid Weightage");
			document.getElementById(weightageid).value = remainweight;
		}
	}

	function validateSECANDSUBSECScore(value1, weightageid, weightagehideid) {
		var remainWeightage = document.getElementById(weightagehideid).value;

		if (parseFloat(value1) > parseFloat(remainWeightage)) {
			alert("Entered value greater than Weightage");
			document.getElementById(weightageid).value = remainWeightage;
		} else if (parseFloat(value1) <= 0) {
			alert("Invalid Weightage");
			document.getElementById(weightageid).value = remainWeightage;
		}
	}
	CKEDITOR.config.width = '915px';
</script>

<%
List<String> mainLevelList =(List<String>)request.getAttribute("mainLevelList");
String anstype = (String) request.getAttribute("anstype");
String type = (String) request.getAttribute("type");
String linkType = (String) request.getAttribute("linkType");
String attribute = (String) request.getAttribute("attribute");
String id = (String) request.getAttribute("id");
String appFreqId = (String) request.getAttribute("appFreqId");
String fromPage = (String) request.getAttribute("fromPage");
%>
<div style="border: 1px; border-color: black;">
	<s:form action="AddMyReviewSectionAndSubsection"
		id="formAddMyreviewLevelAndSystem" name="formAddMyreviewLevelAndSystem" target="_parent" method="POST"
		theme="simple">
		<div id="appraisalHeadingDiv"
			style="width: 100%; float: left; margin-top: 10px;">
			<input type="hidden" name="appraisalHeadingDivCounter"
				id="appraisalHeadingDivCounter" value="0">
			<s:hidden name="id"></s:hidden>
			<s:hidden name="oreinted"></s:hidden>
			<s:hidden name="appFreqId"></s:hidden>
			<s:hidden name="MLID"></s:hidden>
			<s:hidden name="fromPage"></s:hidden>
			<input type="hidden" name="main_level_id" id="main_level_id"
				value="<%=mainLevelList!=null ? mainLevelList.get(0) : null %>" /> <input
				type="hidden" name="main_level_name" id="main_level_name"
				value="<%=mainLevelList!=null ? mainLevelList.get(1) : "" %>" /> <span
				style="float: right"><img border="0"
				style="padding: 5px 5px 0pt; height: 18px; width: 18px; float: right;"
				<%-- src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" --%>
				<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i>
				onclick="closeIframe('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>','<%=request.getAttribute("divCount") %>')" />
			</span>
			<%if(type == null){ %>
			<table class="table table_no_border">
				<tr>
					<th align="right" style="padding-right: 20px" width="15%"><%=request.getAttribute("newlvlno") %>)</th>
					<th align="left" style="padding-left: 20px" colspan="5">New
						Section</th>
				</tr>
				<tr>
					<th align="right" style="padding-right: 20px" width="15%">Section
						Title<sup>*</sup>
					</th>
					<td colspan="5"><input type="text" name="levelTitle"
						id="levelTitle" class="validateRequired"
						style="height: 25px; width: 80%;" /></td>
				</tr>
				<tr>
					<th align="right" style="padding-right: 20px">Short
						Description</th>
					<td colspan="5">
						<!-- <input type="text" name="shortDesrciption" id="shortDesrciption" style="width:80%"/> -->
						<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="shortDesrciption"></textarea>  <!-- id="editor1" -->
					</td>
				</tr>
				<tr>
					<th align="right" style="padding-right: 20px" valign="top">Long
						Description</th>
					<td colspan="5">
						<!-- <textarea rows="4" cols="10" name="longDesrciption" id="longDesrciption" style="width:80%"></textarea> -->
						<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="longDesrciption"></textarea>  <!-- id="editor2" -->
					</td>
				</tr>
				<tr>
					<th align="right" style="padding-right: 20px">Weightage %<sup>*</sup>
					</th>
					<td><input type="text" name="sectionWeightage"
						id="sectionWeightage" style="height: 25px;"
						class="validateRequired"
						value="<%=request.getAttribute("weightage") %>"
						onkeyup="validateSECANDSUBSECScore(this.value,'sectionWeightage','hidesectionWeightage');"
						onkeypress="return isNumberKey(event)" /> <input type="hidden"
						name="hidesectionWeightage" id="hidesectionWeightage"
						value="<%=request.getAttribute("weightage") %>" /></td>
				</tr>
				<tr>
					<th align="right" style="padding-right: 20px">Select Attribute</th>
					<td><select name="attribute"><%=attribute %>
					</select>
					</td>
				</tr>
				<tr>
					<th align="right" style="padding-right: 20px">Work Flow</th>
					<td colspan="5">
						<%
						String member1=(String)request.getAttribute("member");
						String[] memberArray1=member1.split(",");
						//System.out.println("member ==== > "+member);
						for(int i=0; i<memberArray1.length; i++) {
						%>
						<span style="float: left; width: 60px; text-align: center;">Step<%=i+1%></span> 
						<% } %>
					</td>
				</tr>
				<%
					String member=(String)request.getAttribute("member");
					String[] memberArray=member.split(",");
					//System.out.println("member ==== > "+member);
					for(int i=0; i<memberArray.length; i++) {
				%>
				<tr>
					<th align="right" style="padding-right: 20px"><%=memberArray[i]%></th>
					<td colspan="5">
						<%for(int j=1; j<=memberArray.length; j++) { %>
							<span style="float: left; width: 60px; text-align: center;"> 
								<input type="radio" name="<%=memberArray[i]%>" value="<%=j %>" <%if(j==1){ %> checked="checked" <% } %> />
							</span>
						<% } %>
					</td>
				</tr>
				<% } %>
			</table>
			<% } else { %>
				<input type="hidden" name="levelTitle" id="levelTitle" value="" /> 
				<input type="hidden" name="attribute" value="<%=mainLevelList!=null ? mainLevelList.get(5) : "0" %>" />
			<%} %>
		</div>

		<%if(type == null){ %>
		<div style="float: left; margin-top: 15px; width: 100%;">
			<span id="sectionLinkDiv<%=(request.getAttribute("divCount"))%>"
				style="float: left; margin-left: 100px;"><a
				href="javascript:void(0)" title="Add Subsections"
				onclick="openSectionDiv('section');"> +Subsections </a>
			</span> <span id="assessLinkDiv<%=(request.getAttribute("divCount"))%>"
				style="float: left; margin-left: 100px;"><a
				href="javascript:void(0)" title="Add Assessments"
				onclick="openSectionDiv('assessment');"> +Assessments </a>
			</span>
		</div>
		<% } %>

		<%
			String secsunsec = request.getAttribute("newsysno")!=null ? (String)request.getAttribute("newsysno") : ((String)request.getAttribute("newlvlno")+".1") ;
			String sectioncnt = secsunsec.substring(0,1);
			String subsectioncnt = secsunsec.substring(1,secsunsec.length());
		%>

		<div id="sectionAssessmentdiv<%=(request.getAttribute("divCount"))%>"
			style="width:100%; display: <%if(type == null){ %>none;<%} else {%>block;<% }%> ">
			<div id="sectiondiv<%=(request.getAttribute("divCount"))%>"
				style="width:100%; float:left; display: <%if(linkType != null && linkType.equals("section")){ %>block;<%} else {%>none;<% }%>">
				<p style="padding-left: 5px; text-align: left; margin-bottom: 10px;"><%=request.getAttribute("newsysno")!=null ? request.getAttribute("newsysno") : request.getAttribute("newlvlno")+".1"%>
					Subsection of "
					<%if(type != null && linkType != null && linkType.equals("section")){ %>
					<%=mainLevelList.get(1) %>
					<%} else { %><span id="sectionnamespan">New Section</span>
					<%} %>"
				</p>

				<table class="table table_no_border">
					<tr>
						<th align="right" style="padding-right: 20px" width="15%"><%=request.getAttribute("newsysno")!=null ? request.getAttribute("newsysno") : request.getAttribute("newlvlno")+".1"%>)</th>
						<th align="left" style="padding-left: 20px" colspan="5">New
							Subsection <%-- <%if(type != null){ %>
									<span style="float: right">
									<img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeIframe('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>','<%=request.getAttribute("divCount")%>')"/>
									</span>									
									<%} %> --%></th>
					</tr>
					<tr id="sectionnameTr">
						<th align="right" style="padding-right: 20px" width="15%">Subsection
							Title<sup>*</sup>
						</th>
						<td colspan="5"><input type="hidden" name="hidesectioncnt"
							id="hidesectioncnt" value="<%=sectioncnt%>" /> <input
							type="hidden" name="hidesubsectioncnt" id="hidesubsectioncnt"
							value="<%=subsectioncnt%>" /> <input type="text"
							name="subsectionname" id="subsectionname"
							class="validateRequired" style="height: 25px; width: 450px;" /></td>
					</tr>
					<tr id="sectionDescTr">
						<th align="right" style="padding-right: 20px">Subsection
							Short Description</th>
						<td colspan="5">
							<!-- <input type="text" name="subsectionDescription" id="subsectionDescription" style="width: 450px;"/> -->
							<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="subsectionDescription"></textarea></td>  <!-- id="editor3" -->
					</tr>
					<tr id="sectionLongDescTr">
						<th align="right" style="padding-right: 20px">Subsection Long
							Description</th>
						<td colspan="5">
							<!-- <input type="text" name="subsectionLongDescription" id="subsectionLongDescription" style="width: 450px;"/> -->
							<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="subsectionLongDescription"></textarea></td>  <!-- id="editor4" -->
					</tr>
					<tr>
						<th align="right" style="padding-right: 20px">Weightage %<sup>*</sup>
						</th>
						<td><input type="text" name="subSectionWeightage"
							id="subSectionWeightage" style="height: 25px;"
							class="validateRequired"
							value="<%=(request.getAttribute("weightage")!=null && !request.getAttribute("weightage").toString().equals("")) ? "100" : request.getAttribute("subWeightage") %>"
							onkeyup="validateSECANDSUBSECScore(this.value,'subSectionWeightage','hidesubSectionWeightage');"
							onkeypress="return isNumberKey(event)" /> <input type="hidden"
							name="hidesubSectionWeightage" id="hidesubSectionWeightage"
							value="<%=(request.getAttribute("weightage")!=null && !request.getAttribute("weightage").toString().equals("")) ? "100" : request.getAttribute("subWeightage") %>" />
						</td>
					</tr>
				</table>
			</div>


			<div style="float: left; width: 100%;">
				<ul class="level_list ul_class">
					<li>
						<div
							style="margin-top: 10px; background-color: lemonchiffon; border: 1px solid; padding: 10px;">
							<table class="table table_no_border">
								<tr>
									<td>System:<Sup>*</Sup>
									</td>
									<td><s:select theme="simple" name="appraisalSystem" headerKey="" headerValue="Select System"
											list="#{'2':'Other'}" onchange="showSystem(this.value,'');"
											cssClass="validateRequired" /> <%if(type != null && linkType != null && !linkType.equals("section")){ %>
										<%-- <span id="assessdiv<%=(request.getAttribute("divCount"))%>"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>')"/></span> --%>
										<%} else{%> <span
										id="assessdiv<%=(request.getAttribute("divCount"))%>"></span>
										<%} %>
									</td>
									<td></td>
								</tr>
								<tr id="scoreCardID" style="display: none;">
									<td>Competency<sup>*</sup>
									</td>
									<td><s:select theme="simple" name="scoreCard" headerKey=""
											headerValue="Select Competency" id="scoreCard"
											cssClass="validateRequired"
											onchange="getcontent(this.value);"
											list="#{'1':'Competencies + Goals + Objectives + Measures', 
                                                    '3':'Competencies + Goals + Measures','2':'Competencies + Measures'}" />
									</td>
									<td></td>
								</tr>
								<tr id="otherqueTypeTr" style="display: none;">
									<td>Question Type:<sup>*</sup>
									</td>
									<td><s:select theme="simple" name="otherQuestionType"
											id="otherQuestionType" headerKey="" headerValue="Select Type"
											cssClass="validateRequired"
											list="#{'Without Short Description':'Without Short Description', 'With Short Description':'With Short Description'}" />
									</td>
									<td></td>
								</tr>
								<tr id="anstypeTr">
									<td>Select Answer Type</td>
									<td><select name="ansType" id="ansType"
										onchange="showAnswerTypeDiv(this.value)"><%=anstype %></select>
									</td>
									<td>
										<div id="anstypediv">
											<div id="anstype9">
												a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled" /> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled" /><br /> 
												c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled" /> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled" /><br />
											</div>
										</div></td>
								</tr>
							</table>
						</div></li>
				</ul>
			</div>

			<div class="sectionfont" id="mainDiv"
				style="float: left; margin: 10px 0px 0px 0px; width: 100%;"></div>

			<div class="sectionfont" id="assessOfSubsectionDiv"
				style="float: left; width: 100%;"></div>

			<div class="sectionfont" id="otherDiv"
				style="float: left; width: 100%; display: none;">
				<ul class="level_list ul_class">
					<li><a href="javascript:void(0)" class="add_lvl"
						onclick="getOtherquestion('0');">Assess info / points</a>
					</li>
					<li id="otherQuestionLi"></li>
				</ul>
			</div>
		</div>
		<div align="center">
			<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
			<input type="button" value="Cancel" class="btn btn-danger" name="cancel"
				onclick="closeIframe('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>','<%=request.getAttribute("divCount")%>')" />
		</div>
		<s:hidden name="plancount" id="plancount"></s:hidden>
	</s:form>
</div>


<!-- <g:compress> -->

<script>
<%if(fromPage != null && fromPage.equals("SRR")) {%>
	$("#formAddMyreviewLevelAndSystem").submit(function(event){
		event.preventDefault();
		for ( instance in CKEDITOR.instances ) {
	        CKEDITOR.instances[instance].updateElement();
	    }
		var form_data = $("#formAddMyreviewLevelAndSystem").serialize();
		$.ajax({
			type : 'POST',
			url : "AddMyReviewSectionAndSubsection.action",
			data : form_data + "&submit=Save",
			cache : true,
			success : function(result) { 
				getMyReviewSummary('MyReviewSummary','<%=id%>','<%=appFreqId%>','<%=fromPage%>');
			},
			error: function(result){
				getMyReviewSummary('MyReviewSummary','<%=id%>','<%=appFreqId%>','<%=fromPage%>');
			}
		});
		
	});
		
<%}%>

<%if(type == null){ %>
	// Replace the <textarea id="editor1"> with an CKEditor instance.
	if (document.getElementById("editor1")) {
		CKEDITOR.replace('editor1', {
			on : {
				focus : onFocus,
				blur : onBlur,
				// Check for availability of corresponding plugins.
				pluginsLoaded : function(evt) {
					var doc = CKEDITOR.document, ed = evt.editor;
					if (!ed.getCommand('bold'))
						doc.getById('exec-bold').hide();
					if (!ed.getCommand('link'))
						doc.getById('exec-link').hide();
				}
			}
		});

	}

	// Replace the <textarea id="editor2"> with an CKEditor instance.
	if (document.getElementById("editor2")) {
		CKEDITOR.replace('editor2', {
			on : {
				focus : onFocus,
				blur : onBlur,
				// Check for availability of corresponding plugins.
				pluginsLoaded : function(evt) {
					var doc = CKEDITOR.document, ed = evt.editor;
					if (!ed.getCommand('bold'))
						doc.getById('exec-bold').hide();
					if (!ed.getCommand('link'))
						doc.getById('exec-link').hide();
				}
			}
		});
	}
<% } %>
	// Replace the <textarea id="editor1"> with an CKEditor instance.
	if (document.getElementById("editor3")) {
		CKEDITOR.replace('editor3', {
			on : {
				focus : onFocus,
				blur : onBlur,
				// Check for availability of corresponding plugins.
				pluginsLoaded : function(evt) {
					var doc = CKEDITOR.document, ed = evt.editor;
					if (!ed.getCommand('bold'))
						doc.getById('exec-bold').hide();
					if (!ed.getCommand('link'))
						doc.getById('exec-link').hide();
				}
			}
		});
	}

	// Replace the <textarea id="editor2"> with an CKEditor instance.
	if (document.getElementById("editor4")) {
		CKEDITOR.replace('editor4', {
			on : {
				focus : onFocus,
				blur : onBlur,
				// Check for availability of corresponding plugins.
				pluginsLoaded : function(evt) {
					var doc = CKEDITOR.document, ed = evt.editor;
					if (!ed.getCommand('bold'))
						doc.getById('exec-bold').hide();
					if (!ed.getCommand('link'))
						doc.getById('exec-link').hide();
				}
			}
		});
	}
</script>

<!-- </g:compress> -->

