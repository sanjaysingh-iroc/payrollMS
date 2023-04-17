<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
    <script type="text/javascript"> 
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
        
        	if ( editor.mode == 'wysiwyg' )
        	{
        		editor.insertHtml( value );
        	}
        	else
        		alert( 'You must be in WYSIWYG mode!' );
        }
        
        function InsertText() {
        	var editor = CKEDITOR.instances.editor1;
        	var value = document.getElementById( 'txtArea' ).value;
        
        	if ( editor.mode == 'wysiwyg' )
        	{
        		editor.insertText( value );
        	}
        	else
        		alert( 'You must be in WYSIWYG mode!' );
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
        
        	if ( editor.mode == 'wysiwyg' )
        	{
        		editor.execCommand( commandName );
        	}
        	else
        		alert( 'You must be in WYSIWYG mode!' );
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
        
        function isNumberKey(evt) {
           var charCode = (evt.which) ? evt.which : event.keyCode;
           if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
              return false;
        
           return true;
        }

        function isOnlyNumberKey(evt) {
    		var charCode = (evt.which) ? evt.which : event.keyCode;
    		if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
    			return true; 
    		}
    		return false;
    	}
        
        
        var cxtpath='<%=request.getContextPath()%>';
        
        	var scoreCnt = 0;
        	var questionCnt = 0;
        	var objectiveCnt = 0;
        	var measureCnt = 0;
        	var goalCnt=0;
        	
        	function getcontent(val, callFrom) {
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
        						+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard('"+ callFrom+ "')\" ><i class=\"fa fa-plus-circle\"></i>Add Competency</a></li>"
        						+ "<li id=\"goalDiv_"+scoreID+"\"><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\" class=\"form-control \"/>"
        							+"<ul id=\""+goalID+"\">"
        								+"<li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
        								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a></li>"
        								+ "<li id=\"objDiv_"+goalID+"\"><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
        									+"<ul id=\""+objectiveID+"\">"
        										+"<li>" + getObjectiveData((scoreCnt+1)+'.1.1)',goalID,'0') + "</li>"
        										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addObjective('"+ goalID + "','"+ (scoreCnt+1)+'.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Objective</a></li>"
        										+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" value=\"1\" class=\"form-control \"/>"
        											+"<ul id=\""+measureID+"\">"
        												+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1.1)','0',objectiveID) + "</li>"
        												+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','"+ (scoreCnt+1)+'.1.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
        												+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\" class=\"form-control \"/>"
        														+"<ul id=\""+questionID+"\">"
        														+"<li>"+ getquestion((scoreCnt+1)+'.1.1.1','.1)',measureID,callFrom) + "</li>"
        													+"</ul></li>"
        												+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1.1.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
        					+ "</ul></li></ul></li></ul></li></ul></li></ul></li></ul>";
        			//alert("in getcontent ... a : "+a);
        			document.getElementById("mainDiv").innerHTML = a; 
        		} else if (val == '2') {
        				
        			var a = "<ul id=\""+scoreID+"\" class=\"level_list ul_class\">"
        						+"<li>" + getScoreData(scoreCnt) + "</li>"
        						+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard('"+ callFrom+ "')\" ><i class=\"fa fa-plus-circle\"></i>Add Competency</a></li>"
        						+ "<li id=\"measureDiv_"+scoreID+"\">"
        							+"<input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+scoreID+"\" value=\"1\" class=\"form-control \"/>"
        						  	 +"<ul id=\""+measureID+"\">"
        						  	 	+"<li>"+ getMeasureData((scoreCnt+1)+'.1)','0',scoreID) + "</li>"
        								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ scoreID + "','"+ (scoreCnt+1) + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"+
        									"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\" class=\"form-control \"/>"
        									+ "<ul id=\""+questionID+"\">"
        										+"<li>"+ getquestion((scoreCnt+1)+'.1','.1)',measureID, callFrom) + "</li>"
        									+"</ul></li>"
        									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1' +"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
        					+ "</ul></li></ul>";
        					document.getElementById("mainDiv").innerHTML = a; 
        			} else if (val == '3') { 
        
        			var a = "<ul id=\""+scoreID+"\" class=\"level_list ul_class\">"
        						+"<li>" + getScoreData(scoreCnt) + "</li>"
        						+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard('"+ callFrom+ "')\" ><i class=\"fa fa-plus-circle\"></i>Add Competency</a></li>"
        						+ "<li id=\"goalDiv_"+scoreID+"\" ><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\" class=\"form-control \"/>"
        							+"<ul id=\""+goalID+"\">"
        								+"<li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
        								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a></li>"
        								+ "<li id=\"measureDiv_"+goalID+"\"><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
        									+"<ul id=\""+measureID+"\">"
        										+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1)','0',goalID) + "</li>"
        										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ goalID + "','"+ (scoreCnt+1)+'.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
        										+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\" class=\"form-control \"/>"
        											+"<ul id=\""+questionID+"\">"
        												+"<li>"+ getquestion((scoreCnt+1)+'.1.1','.1)',measureID,callFrom) + "</li>"
        											+"</ul></li>"
        										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+(scoreCnt+1)+'.1.1'+"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
        					+ "</ul></li></ul></li></ul></li></ul>";
        					document.getElementById("mainDiv").innerHTML = a;
        		} else {
        			document.getElementById("mainDiv").innerHTML ="";
        		}
        	 	//jQuery("#formID").validationEngine();
        	}
        
         
        	  function addGoal(id,scoreCnt,callFrom) {
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
        						+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ id + "','"+ scoreCnt +"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a>"
        						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\" class=\"close-font\">Delete Goal</a></li>"
        						//+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"deleteBlock('"+ goalID + "','"+ id + "','goalcount');\">Delete Goal</a>"
        			
        						+ "<li id=\"objDiv_"+goalID+"\"><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
        						+"<ul id=\""+objectiveID+"\">"
        							+"<li>"+ getObjectiveData(scoreCnt+'.'+ (parseInt(val)+1)+'.1)',goalID,'0') + "</li>"
        							+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addObjective('"+ goalID + "','"+ scoreCnt+'.'+ (parseInt(val)+1) +"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Objective</a></li>"
        							+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+objectiveID+"\" value=\"1\"/>"
        								+"<ul id=\""+measureID+"\">"
        									+"<li>"+ getMeasureData(scoreCnt+'.'+ (parseInt(val)+1)+'.1.1)','0',objectiveID) + "</li>"
        									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','"+ scoreCnt+'.'+ (parseInt(val)+1)+'.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
        									+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
        										+"<ul id=\""+questionID+"\">"
        										+"<li>"+ getquestion(scoreCnt+'.'+ (parseInt(val)+1)+'.1.1','.1)',measureID,callFrom) + "</li>"
        										+"</ul>"
        									+"</li>"
        									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+scoreCnt+'.'+ (parseInt(val)+1)+'.1.1'+"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
        								 +"</ul>"
        							+"</li>"
        						 +"</ul></li></ul></li>";
        						
        						litag.innerHTML = a;
        					
        					} else if (val1 == '3') {
        						var a = "<li>" + getGoalData(scoreCnt+'.'+ (parseInt(val)+1)+')',id,goalCnt) + "</li>"
        								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ id + "','"+ scoreCnt +"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a>"
        									+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\" class=\"close-font\">Delete Goal</a></li>"
        								+ "<li id=\"measureDiv_"+goalID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+goalID+"\" value=\"1\"/>"
        									+"<ul id=\""+measureID+"\">"
        										+"<li>"+ getMeasureData(scoreCnt+'.'+ (parseInt(val)+1)+'.1)','0',goalID) + "</li>"
        										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ goalID + "','"+ scoreCnt+'.'+ (parseInt(val)+1) + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
        										+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" class=\"form-control \" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
        											+"<ul id=\""+questionID+"\">"
        												+"<li>"+ getquestion(scoreCnt+'.'+ (parseInt(val)+1)+'.1','.1)',measureID,callFrom) + "</li>"
        											+"</ul></li>"
        										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ scoreCnt+'.'+ (parseInt(val)+1)+'.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
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
        	  
        
        	 function addObjective(id,scoreCnt,callFrom) {
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
        						+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addObjective('"+ id + "','"+ scoreCnt + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Objective</a>"
        						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ objectiveID + "','"+ objectiveDivId + "','objectivecount');\" class=\"close-font\">Delete Objective</a>"
        						+"</li>"
        						+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+objectiveID+"\" value=\"1\"/>"
        							+"<ul id=\""+measureID+"\">" 
        								+"<li>"+ getMeasureData(scoreCnt+'.'+(parseInt(val)+1)+'.1)','0',objectiveID) + "</li>"
        								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','"+scoreCnt+'.'+(parseInt(val)+1)+"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
        								+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
        								+"<ul id=\""+questionID+"\">"
        								+"<li>"+ getquestion(scoreCnt+'.'+(parseInt(val)+1)+'.1','.1)',measureID,callFrom) 
        								+ "</li></ul></li>"
        								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ scoreCnt+'.'+(parseInt(val)+1)+'.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
        							+ "</ul></li></ul></li></ul>";
        		
        				litag.innerHTML = a;
        				$('#'+id).find('#objDiv_'+id).append(litag);
        				cnt = parseInt(cnt)+1;
        				
        				document.getElementById("objectiveWeightage"+id+cnt).value = remainweight;
        				document.getElementById("hideobjectiveWeightage"+id+cnt).value = remainweight;
        			//	jQuery("#formID").validationEngine();
        		}
        	} 
        	
        	  function addScoreCard(callFrom ){
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
        						+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard('"+ callFrom+ "')\" ><i class=\"fa fa-plus-circle\"></i>Add Competency</a>"
        						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" class=\"close-font\">Delete Competency</a></li>"
        						//+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" >Delete Competency</a></li>"
        						+ "<li id=\"goalDiv_"+scoreID+"\"><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\" class=\"form-control \"/><ul id=\""+goalID+"\"><li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
        						+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a></li>"
        						
        						+ "<li id=\"objDiv_"+goalID+"\" ><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
        							+"<ul id=\""+objectiveID+"\">"
        								+"<li>" + getObjectiveData((scoreCnt+1)+'.1.1)',goalID,'0') + "</li>"
        								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addObjective('"+ goalID + "','"+ (scoreCnt+1)+'.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Objective</a></li>"
        						    	+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" class=\"form-control \" value=\"1\"/>"
        						    			+"<ul id=\""+measureID+"\">"
        						    				+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1.1)','0',objectiveID) + "</li>"
        											+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','" + (scoreCnt+1)+'.1.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
        											+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
        												+"<ul id=\""+questionID+"\">"
        													+"<li>"+ getquestion((scoreCnt+1)+'.1.1.1','.1)',measureID, callFrom) + "</li>"
        												+"</ul>"
        											+"</li>"
        											+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1.1.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
        								+ "</ul></li></ul></li></ul></ul></li></ul>";
        								
        					 litag.innerHTML = a;
        					 document.getElementById("mainDiv").appendChild(litag);
        						
        				}else if (val == '2') {
        					var a = "<li>" + getScoreData(scoreCnt) + "</li>"
        							+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard('"+ callFrom+ "')\" ><i class=\"fa fa-plus-circle\"></i>Add Competency</a>"
        							+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" class=\"close-font\">Delete Competency</a></li>"
        					
        							+ "<li id=\"measureDiv_"+scoreID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+scoreID+"\" value=\"1\" />"
        								+"<ul id=\""+measureID+"\">"
        									+"<li>"+ getMeasureData((scoreCnt+1)+'.1)','0',scoreID) + "</li>"
        										+"<a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('" + scoreID + "','" + (scoreCnt+1) + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a>"
        										+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" class=\"form-control \" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
        											+ "<ul id=\""+questionID+"\">"
        												+"<li>"+ getquestion((scoreCnt+1)+'.1','.1)',measureID,callFrom)
        									+ "</li></ul></li>"
        									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
        							+ "</ul></li></ul></li>";
        					litag.innerHTML = a;
        					document.getElementById("mainDiv").appendChild(litag);
        				}
        				 else if (val == '3') {
        					 var a = "<li>" + getScoreData(scoreCnt) + "</li>"
        							+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard('"+ callFrom+ "')\" ><i class=\"fa fa-plus-circle\"></i>Add Competency</a>"
        							+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" class=\"close-font\">Delete Competency</a></li>"
        						
        								+ "<li id=\"goalDiv_"+scoreID+"\"><input type=\"hidden\" name=\"goalcount\" class=\"form-control \" id=\"goalcount"+scoreID+"\" value=\"1\"/>"
        									+"<ul id=\""+goalID+"\">"
        										+"<li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
        										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a></li>"
        										+ "<li id=\"measureDiv_"+goalID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+goalID+"\" value=\"1\"/>"
        											+"<ul id=\""+measureID+"\">"
        												+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1)','0',goalID) + "</li>"
        												+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ goalID + "','" + (scoreCnt+1)+'.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
        												+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" class=\"form-control \" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
        													+"<ul id=\""+questionID+"\">"
        														+"<li>"+ getquestion((scoreCnt+1)+'.1.1','.1)',measureID, callFrom) 
        												+ "</li></ul></li>"
        												+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1.1' + "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
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
        
        	    function addQuestions(id,scoreCnt,callFrom) {
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
        				//alert("id==>"+id);
        				if(document.getElementById("questioncount"+id)) {
        					//alert('in if ----------> ');
        					val = document.getElementById("questioncount"+id).value;
        					document.getElementById("questioncount"+id).value=parseInt(val)+1;
        				}
        				//alert("questioncount+id ==> " + document.getElementById("questioncount"+id).value + " ==> val ==> " + val);
        				
        				litag.id = QuestionID;
        				var a =  getquestion(scoreCnt, '.'+(parseInt(val)+1)+')', id, callFrom) ;
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
        
        	    function addMeasure(id,scoreCnt,callFrom) {
        	    
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
        									+"<li><a href=\"javascript:void(0)\" onclick=\"addMeasure('"+ id + "','"+ scoreCnt + "','"+ callFrom+ "')\"  ><i class=\"fa fa-plus-circle\"></i>Add Measure</a>"
        										+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+measureID+"','" +measureDivId+ "','measurecount');\" class=\"close-font\">"
        										+"Delete Measure</a>"
        									+"</li>"
        									+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
        										+"<ul id=\""+questionID+"\">"
        										+"<li>"+ getquestion(scoreCnt+'.'+(parseInt(val)+1),'.1)',measureID, callFrom)
        									+ "</li></ul></li>"
        									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ scoreCnt+'.'+(parseInt(val)+1)+ "','"+ callFrom+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a>"
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
        		 
        
        	    function getquestion(scoreCnt,val,id,callFrom) {
        			
        			var cnt=questionCnt;
        			//var selec = getOrientationData(cnt);
        			var QuestionID = "questionID" + questionCnt;
        			var aa = getQuestoinContentType(cnt,callFrom);
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
        			var questDivId = "questDiv_"+id;
        			var a = "<li>"
        				 	//+"<p style=\"margin-bottom: 10px; text-align: left; font-size: 12px; font-weight: bold;\">Assessments of \" <span id=\"subsectionnamespan\">New Subsection</span> \"</p>"
        			 		+"<table class=\"table table_no_border\" width=\"100%\">"
        					+ "<tr><th>"+scoreCnt+val+"</th><th width=\"17%\" style=\"text-align: right;\">Add Question<sup>*</sup></th>"
        					+"<td colspan=\"3\"><span id=\"newquespan"+cnt+"\" style=\"float: left; \"><input type=\"hidden\" name=\"hidequeid\" class=\"form-control \" id=\"hidequeid"+cnt+"\" value=\"0\"/>"
        					+"<textarea rows=\"2\" name=\"question\" id=\"question"+cnt+"\" class=\"validateRequired form-control \"  style=\"width: 330px;\"></textarea>"
        					//+"<input type=\"text\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\"/>"
        					+"</span>"
        					
        					+ "&nbsp;<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt\" class=\"form-control \" value=\""+cnt+"\"/><sup>*</sup>"
        					+"<input type=\"number\" style=\"width: 35px !important;\" name=\"weightage\" id=\"weightage"+id+cnt+"\" class=\"validateRequired form-control\" value=\"100\" onkeyup=\"validateScore(this.value,'weightage"+id+cnt+"','"+id+"','weightage');\" onkeypress=\"return isNumberKey(event)\"/>"
        					+ "<input type=\"hidden\" name=\"hideweightage\" class=\"form-control \" id=\"hideweightage"+id+cnt+"\" value=\"100\"/></span>&nbsp;&nbsp;"
        
        					+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"');openOtheQueMode('NQ','"+cnt+"');\" > +Q </a></span>&nbsp;"
        					+"<span id=\"checkboxspan"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+ cnt+ "')\" />"
        					+"<input type=\"hidden\" class=\"form-control \" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/></span>"
        					
        					+"<a href=\"javascript:void(0)\" title=\"Add New Question\" onclick=\"addQuestions('"+id+"','"+scoreCnt+"','"+callFrom+"')\"><i class=\"fa fa-plus-circle\"></i></a>&nbsp;&nbsp; "
        					+"<a href=\"javascript:void(0)\" title=\"Remove Question\" onclick=\"deleteBlock('"+QuestionID+"','"+ questDivId+ "','questioncount');\" class=\"close-font\"></a>"
        
        					+"<input type=\"hidden\" class=\"form-control \" name=\"questiontypename\" value=\""+ cnt+"\" /></td></tr>"
        					+aa
        					+"</table></li>";
        				//	jQuery("#formID").validationEngine();
        			return a;
        			//jQuery("#formID").validationEngine();
        		}
        
        		function getScoreData(count) {
        			count++;
        			var a = "<table class=\"table table_no_border\" width=\"100%\"><tr><th style=\"width: 4%;\">"+count+")</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Competency</td></tr>"
        					+ "<tr><th></th><th style=\"text-align: right;\">Section name<sup>*</sup></th><td><input type=\"text\" name=\"scoreSectionName\"  id=\"scoreSectionName\" class=\"validateRequired form-control \" style=\"width: 450px;\" /></td></tr>"
        					+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"scoreCardDescription\" class=\"form-control \" style=\"width: 450px;\"	/></td></tr>"
        					+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"number\" name=\"scoreCardWeightage\" id=\"scoreCardWeightage"+count+"\" class=\"validateRequired form-control\" value=\"100\" onkeyup=\"validateScore1(this.value,'scoreCardWeightage"+count+"','scoreCardWeightage');\" onkeypress=\"return isNumberKey(event)\"/>"
        					+ "<input type=\"hidden\" name=\"hidescoreCardWeightage\" class=\"form-control \" id=\"hidescoreCardWeightage"+count+"\" value=\"100\" /></td></tr>"
        					+"</table>";
        					//jQuery("#formID").validationEngine();
        			return a;
        		//	jQuery("#formID").validationEngine();
        		}
        
        		 function getObjectiveData(val,id,objectiveCnt) {
        			var a = "<table class=\"table table_no_border\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Objective"
        					+ "</td></tr>"
        					+ "<tr><th></th><th style=\"text-align: right;\">Section name<sup>*</sup></th><td><input type=\"text\" name=\"objectiveSectionName\" id=\"objectiveSectionName\" class=\"validateRequired form-control \" style=\"width: 450px;\" /></td></tr>"
        					+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"objectiveDescription\" style=\"width: 450px;\" class=\" form-control \"/></td></tr>"
        					+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"number\" name=\"objectiveWeightage\" id=\"objectiveWeightage"+id+objectiveCnt+"\" class=\"validateRequired form-control\" value=\"100\" onkeyup=\"validateScore(this.value,'objectiveWeightage"+id+objectiveCnt+"','"+id+"','objectiveWeightage');\" onkeypress=\"return isNumberKey(event)\"/>"
        					+ "<input type=\"hidden\" name=\"hideobjectiveWeightage\" id=\"hideobjectiveWeightage"+id+objectiveCnt+"\" value=\"100\" class=\" form-control \"/></td></tr></table>";
        					//jQuery("#formID").validationEngine();
        			return a;
        			//jQuery("#formID").validationEngine();
        		} 
        
        		function getMeasureData(val,measureCnt,id) {
        
        			var a = "<table class=\"table table_no_border\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Measures</td></tr>"
        					+ "<tr><th></th><th style=\"text-align: right;\">Section name<sup>*</sup></th><td><input type=\"text\" name=\"measuresSectionName\" id=\"measuresSectionName\" class=\"validateRequired form-control \" style=\"width: 450px;\" /></td></tr>"
        					+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"measuresDescription\" style=\"width: 450px;\" class=\" form-control \"/></td></tr>"
        					+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"number\" name=\"measureWeightage\" id=\"measureWeightage"+id+measureCnt+"\" class=\"validateRequired form-control \" value=\"100\" onkeyup=\"validateScore(this.value,'measureWeightage"+id+measureCnt+"','"+id+"','measureWeightage');\" onkeypress=\"return isNumberKey(event)\"/>"
        					+ "<input type=\"hidden\" name=\"hidemeasureWeightage\" id=\"hidemeasureWeightage"+id+measureCnt+"\" value=\"100\" class=\" form-control \"/></td></tr>"
        					+ "</table>";
        					//jQuery("#formID").validationEngine();
        			return a;
        			//jQuery("#formID").validationEngine();
        		}
        
        		function getGoalData(val,id,goalCnt) {
        				var a = "<table class=\"table table_no_border\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th>"
        						+ "<td>Goals</td></tr>"
        						+ "<tr><th></th><th style=\"text-align: right;\">Section name<sup>*</sup></th><td><input type=\"text\" name=\"goalSectionName\" id=\"goalSectionName\" class=\"validateRequired form-control \" style=\"width: 450px;\" /></td></tr>"
        						+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"goalDescription\" style=\"width: 450px;\" class=\" form-control \"/></td></tr>"
        						+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"number\" name=\"goalWeightage\" id=\"goalWeightage"+id+goalCnt+"\" class=\"validateRequired form-control \" value=\"100\" onkeyup=\"validateScore(this.value,'goalWeightage"+id+goalCnt+"','"+id+"','goalWeightage');\" onkeypress=\"return isNumberKey(event)\"/>"
        						+ "<input type=\"hidden\" name=\"hidegoalWeightage\" id=\"hidegoalWeightage"+id+goalCnt+"\" value=\"100\" class=\" form-control \" /></td>"
        						+ "</tr></table>";
        					//	jQuery("#formID").validationEngine();
        				return a;
        				//jQuery("#formID").validationEngine();
        		}
        		
        	 
        	
        function showAssessOfSubsection(){
        		var a="<ul class=\"level_list ul_class\">"
        			+"<p style=\"font-weight: 600;\">Assessments of \" <span id=\"subsectionnamespan\">New Subsection</span> \"</p>"
        		+"</ul>";
        		document.getElementById("assessOfSubsectionDiv").innerHTML = a; 
        } 
          
        function getOtherquestion(oldcnt, callFrom) {
       		 // alert ("inside getOtherQuestions");
       			var totweight=0;
       			oldcnt = questionCnt;
       			  //alert("oldcnt AALS===>> " + oldcnt);
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
       			var aa = getQuestoinContentType(cnt,callFrom);
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
       			
       			 var a = "<li><table class=\"table table_no_border\" width=\"100%\">"
       					+ "<tr><th>"+gencntid+"."+questionCnt+")</th><th width=\"17%\" style=\"text-align: right;\">Add Question<sup>*</sup></th>"
       					+ "<td colspan=\"3\"><span id=\"newquespan"+cnt+"\" style=\"float: left; \"><input type=\"hidden\" name=\"hidequeid\" id=\"hidequeid"+cnt+"\" value=\"0\"/>"
       					+"<textarea rows=\"2\" name=\"question\" id=\"question"+cnt+"\" class=\"validateRequired\"  style=\"width: 330px;\"></textarea>"
       					//+"<input type=\"text\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\"/>"
       					+"</span>"
       
       					+ "&nbsp;<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt\" value=\""+cnt+"\"/><sup>*</sup>"
       					+"<input type=\"text\" style=\"height: 25px; width: 35px !important;\" name=\"weightage\" id=\"weightage"+cnt+"\" class=\"validate[required,custom[integer]]\" value=\"100\" onkeyup=\"validateScore1(this.value,'weightage"+cnt+"','weightage');\" onkeypress=\"return isNumberKey(event)\" />"
       					+"<input type=\"hidden\" name=\"hideweightage\" id=\"hideweightage"+cnt+"\" value=\"100\"/></span>&nbsp;&nbsp;"
       					+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"','"+callFrom+"');\" > +Q </a></span>&nbsp;"+ otherSD +"&nbsp;"
       					+"<span id=\"checkboxspan"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+ cnt+ "')\" />"
       					+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/></span>"
       					
       					+"<a href=\"javascript:void(0)\" title=\"Add New Question\" onclick=\"getOtherquestion('"+cnt+"','"+callFrom+"')\" ><i class=\"fa fa-plus-circle\"></i></a>&nbsp;&nbsp; "
       					+"<a href=\"javascript:void(0)\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\" class=\"close-font\"></a>"
       					//+"<a href=\"javascript:void(0)\" class=\"add_lvl\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\" ></a>"
       					+"<input type=\"hidden\" name=\"questiontypename\" value=\""+ cnt+"\" /></td></tr>"+othrQtype
       					+aa
       					+"</table></li>";
       			ultag.innerHTML = a;
       			//alert("aa ===>> " + aa);
       			
       			document.getElementById("otherQuestionLi").appendChild(ultag);
       			
       			var subsname = document.getElementById("subsectionname").value;
       			var lvltitle = document.getElementById("levelTitle").value;
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
       		}	
       	}
          
        
          
        	function getQuestoinContentType(cnt,callFrom){
        		
        		if(callFrom == "addSectionSubsection"){
        			var val = document.getElementById("ansTypeAddSAndSubS").value;
        		}else{
        			var val = document.getElementById("ansType").value;
        		}
        		
        		var a="";
        		if(val == 8) {
        			a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
        			+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
        		
        		} else if (val == 1 || val == 2 || val == 9) {
        			a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<span id=\"aspan\"><input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<span id=\"bspan\"><input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
        			+"<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<span id=\"cspan\"><input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<span id=\"dspan\"><input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
        
        		} else if (val == 6) {
        			a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
        				+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td></tr>";
        		
        		} else if (val == 5) {
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
        
        	
        	function goalChart(corpGoalID, managerGoalID, teamGoalID, goalID) {
        		//alert("corpGoalID ===> "+corpGoalID+" managerGoalID ===> "+managerGoalID+" teamGoalID ===> "+teamGoalID+" goalID ===> "+goalID);
        		var dialogEdit = '.modal-body';
        		 $(dialogEdit).empty();
        		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        		 $("#modalInfo").show();
        		 $(".modal-title").html('Goal Chart');
        		 $.ajax({
						url : "GoalChartIndividualGoal.action?corpGoalID=" + corpGoalID + "&managerGoalID=" + managerGoalID +"&teamGoalID=" + teamGoalID 
						+"&goalID=" + goalID,
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
        	
        	function openSectionDiv(type, value) {
        		//alert("in openSectionDiv type : " + type);
        		if(type== 'section'){
        			var lvltitle = document.getElementById("levelTitle").value;
        			if(lvltitle == ''){
        				document.getElementById("sectionnamespan").innerHTML = document.getElementById("main_level_name").value;
        			}else{
        				document.getElementById("sectionnamespan").innerHTML = document.getElementById("levelTitle").value;	
        			}
        			document.getElementById("assessdiv"+value).style.display = 'none';
        			document.getElementById("sectionAssessmentdiv"+value).style.display = 'block';
        			document.getElementById("assessLinkDiv"+value).style.display = 'block';
        			document.getElementById("sectiondiv"+value).style.display = 'block';
        			document.getElementById("sectionLinkDiv"+value).style.display = 'none';
        		}else{
        			document.getElementById("assessdiv"+value).style.display = 'block';
        			document.getElementById("sectionAssessmentdiv"+value).style.display = 'block';
        			document.getElementById("sectionLinkDiv"+value).style.display = 'block';
        			document.getElementById("sectiondiv"+value).style.display = 'none';
        			document.getElementById("assessLinkDiv"+value).style.display = 'none';
        			document.getElementById("subsectionname").value = '';
        			document.getElementById("subsectionDescription").value = '';
        		}
        		
        	}
        	
        	
        	function changeNewQuestionType1(val, id, id1,cnt) {
        		//alert("val : "+ val +" id : "+ id + " id1 : "+ id1 + " cnt : " + cnt);
        		 if (val == 1 || val == 2 || val == 8) {
        			addQuestionType11(id,cnt);
        			document.getElementById(id).style.display = 'table-row';
        			addQuestionType21(id1,cnt);
        			document.getElementById(id1).style.display = 'table-row';
        		} else if (val == 9) {
        			addQuestionType31(id,cnt);
        			document.getElementById(id).style.display = 'table-row';
        			addQuestionType41(id1,cnt);
        			document.getElementById(id1).style.display = 'table-row';
        		 }else if (val == 6) {
        			addTrueFalseType1(id,cnt);
        			document.getElementById(id).style.display = 'table-row';
        			document.getElementById(id1).innerHTML ="";
        			document.getElementById(id1).style.display = 'none';
        		}else if (val == 5) {
        			addYesNoType1(id,cnt);
        			document.getElementById(id).style.display = 'table-row';
        			document.getElementById(id1).innerHTML ="";
        			document.getElementById(id1).style.display = 'none';
        		} else {
        			addQuestionType11(id,cnt);
        			addQuestionType21(id1,cnt);
        			document.getElementById(id).style.display = 'none';
        			document.getElementById(id1).style.display = 'none';
        		}
        		 //showAnswerTypeDiv(val);
        	}
        	 
        	function addTrueFalseType1(id,cnt){
        		document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"3\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
        		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
        	}
        	function addYesNoType1(id,cnt){
        		document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"3\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
        		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
        	} 
        	function addQuestionType11(id,cnt) {
        		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\" /><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
        	}
        	function addQuestionType21(id,cnt) {
        		document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
        	}
        	function addQuestionType31(id,cnt) {
        		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
        	}
        	function addQuestionType41(id1,cnt) {
        		document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
        	}

    function openOtherSystemNewQue(value,totWeightage) {
    	 var remainWeightage = 100 - parseFloat(totWeightage);
    	 if(parseInt(remainWeightage) <= 0){
    		 alert("Unable to add questions because of no weightage available ");
    	 }else{
    		document.getElementById("weightage"+value).value=remainWeightage;
    		document.getElementById("hideweightage"+value).value=remainWeightage;
    	    document.getElementById("OTHERnewquedivOfQ"+value).style.display="block";
    	    document.getElementById("OTHERsavebtndivOfQ"+value).style.display="block";
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
    
    
    	function closeSubsectionDiv(quediv, linkDiv, value) {	
    	
    		//alert("before");
    		if(parent.document.getElementById("sectionLinkSpan"+value)) {
    			parent.document.getElementById("sectionLinkSpan"+value).style.display = 'block';
    		}
    		if(parent.document.getElementById("assessLinkSpan"+value)) {
    			parent.document.getElementById("assessLinkSpan"+value).style.display = 'block';
    		}
    		var ifr = document.getElementById(quediv);
    		ifr.style.display = 'none';
    		while (ifr.firstChild) {
    		    ifr.removeChild(ifr.firstChild);
    		}
    	}
    
    function closeIframe(quediv, linkDiv, value){
    	//alert("before");
		if(parent.document.getElementById("sectionLinkSpan"+value)) {
			parent.document.getElementById("sectionLinkSpan"+value).style.display = 'block';
		}
		if(parent.document.getElementById("assessLinkSpan"+value)) {
			parent.document.getElementById("assessLinkSpan"+value).style.display = 'block';
		}
		var ifr = document.getElementById(quediv);
		ifr.style.display = 'none';
		while (ifr.firstChild) {
		    ifr.removeChild(ifr.firstChild);
		}
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
    		//alert("val : " + val + "  cnt1 : " + cnt1 + " selectindex : " + selectindex);
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
    
    function validateScore(value1, weightageid, id, remweightageid) {
    	//alert("value1 ===>> " + value1 +"  weightageid ===>> " + weightageid + " remweightageid ===>> " + remweightageid);
    	//var weightCnt = document.getElementsByName(remweightageid);
    	//alert("weightCnt ===>>>> " + weightCnt.length);
    	
    	var weightCnt = 0;
    	if(remweightageid == 'weightage') {
    		weightCnt = questionCnt;
    	} else if(remweightageid == 'objectiveWeightage') {
    		weightCnt = objectiveCnt;
    	} else if(remweightageid == 'measureWeightage') {
    		weightCnt = measureCnt;
    	} else if(remweightageid == 'goalWeightage') {
    		weightCnt = goalCnt;
    	}
    	var totweight=0;
    	for(var i=0; i <= parseInt(weightCnt); i++) {
    		var checkCurrId = remweightageid+id+i;
    		var weight = document.getElementById(checkCurrId);
    		//alert("weight ===>>>> " + weight);
    		if (weight == null) {
    			continue;	
    		}
    		
    		if(weightageid == checkCurrId) {
    		//	alert("same id");
    		} else {
    			weight = document.getElementById(checkCurrId).value;
    			if(weight == undefined) {
    				weight = 0;
    			}
    			totweight = totweight + parseFloat(weight);
    		}
    	}
    	var remainweight = 100 - parseFloat(totweight);
    		
    	//var remainWeightage = document.getElementById(weightagehideid).value;
    	 // alert("remainweight =========>> " + remainweight);
    	  if(parseFloat(value1) > parseFloat(remainweight)){
    			alert("Entered value greater than Weightage");
    			document.getElementById(weightageid).value = remainweight;
    //===start parvez date: 14-01-2022===
    	  /* }else if(parseFloat(value1) <= 0 ){ */
    	  }else if(parseFloat(value1) < 0 ){
    //===end parvez date: 14-01-2022=== 
    		  alert("Invalid Weightage");
    			document.getElementById(weightageid).value = remainweight;
    		}
    }
    
    
    function validateScore1(value1, weightageid, remweightageid) {
    	//var weightCnt = document.getElementsByName(remweightageid);
    	//scoreCnt questionCnt objectiveCnt measureCnt goalCnt;
    	//alert("weightageid ===>> " + weightageid + " -- remweightageid ===>> " + remweightageid);
    	var weightCnt = 0;
    	if(remweightageid == 'weightage') {
    		weightCnt = questionCnt;
    	} else if(remweightageid == 'scoreCardWeightage') {
    		weightCnt = scoreCnt;
    	}
    	//alert("weightCnt ===>> "+ weightCnt);
    	var totweight=0; //weightCnt.length
    	for(var i=1; i <= (parseInt(weightCnt)+1); i++) {
    		var checkCurrId = remweightageid+i;
    		var weight = document.getElementById(checkCurrId);
    		if (weight == null) {
    			continue;	
    		}
    		
    		if(weightageid == checkCurrId) {
    		} else {
    			weight = document.getElementById(checkCurrId).value;
    			if(weight == undefined) {
    				weight = 0;
    			}
    			totweight = totweight + parseFloat(weight);
    		}
    	}
    	var remainweight = 100 - parseFloat(totweight);
    	  if(parseFloat(value1) > parseFloat(remainweight)){
    			alert("Entered value greater than Weightage");
    			document.getElementById(weightageid).value = remainweight;
    //===start parvez date: 14-01-2022===		
    	  /* }else if(parseFloat(value1) <= 0 ){ */
    		}else if(parseFloat(value1) < 0 ){
    //===end parvez date: 14-01-2022===		  
    			alert("Invalid Weightage");
    			document.getElementById(weightageid).value = remainweight;
    		}  
    }
    
    function submitForm(){
    	var id = document.getElementById('id').value;
    	//alert("id ===> "+id);
    	document.getElementById('formAddAppraisalLevelAndSystem').submit();
    	
    	window.setTimeout(function() {  
    		parent.window.location="AppraisalSummary.action?id="+id;
    		}, 500); 
    }
    
    
    function validateSECANDSUBSECScore(value1,weightageid,weightagehideid) {
    	var remainWeightage = document.getElementById(weightagehideid).value;
    	  
    	  if(parseFloat(value1) > parseFloat(remainWeightage)){
    			alert("Entered value greater than Weightage");
    			document.getElementById(weightageid).value = remainWeightage;
    /* ===start parvez date: 13-01-2022 Note: change condition <= to <*/			
    		}else if(parseFloat(value1) < 0 ){
    /* ===end parvez date: 13-01-2022=== */			
    			alert("Invalid Weightage");
    			document.getElementById(weightageid).value = remainWeightage;
    		}  
    	} 
</script> 
<%

//===start parvez date: 31-12-2021===
	UtilityFunctions uF = new UtilityFunctions();
//===end parvez date: 31-12-2021===
    List<String> mainLevelList =(List<String>)request.getAttribute("mainLevelList");
    String anstype = (String) request.getAttribute("anstype");
    String attribute = (String) request.getAttribute("attribute");
    String type = (String) request.getAttribute("type");
    String linkType = (String) request.getAttribute("linkType");
    String id = (String) request.getAttribute("id");
    String appFreqId = (String) request.getAttribute("appFreqId");
    String fromPage = (String) request.getAttribute("fromPage");
    //System.out.println("linkType =====> " + linkType);
    //System.out.println("type =====> " + type);
    %>
<div>
	
    <s:form action="addAppraisalLevelAndSystem" id="formAddAppraisalLevelAndSystem" name="formAddAppraisalLevelAndSystem" target="_parent" method="POST" theme="simple" cssClass="clr">
        <div id="appraisalHeadingDiv">
            <input type="hidden" name="appraisalHeadingDivCounter" id="appraisalHeadingDivCounter" value="0"> 
            <s:hidden name="id" id="id"></s:hidden>
            <s:hidden name="oreinted"></s:hidden>
            <s:hidden name="MLID"></s:hidden>
            <s:hidden name="appFreqId" id="appFreqId"></s:hidden>
            <s:hidden name="fromPage" id="fromPage"></s:hidden>
            <input type="hidden" name="main_level_id" id="main_level_id" value="<%=mainLevelList!=null ? mainLevelList.get(0) : null %>"/>
            <input type="hidden" name="main_level_name" id="main_level_name" value="<%=mainLevelList!=null ? mainLevelList.get(1) : "" %>"/>
            <%if(type == null){ %>
            <table class="table table_no_border clr">
                <tr>
                    <th align="right" style="padding-right:20px" width="15%"><%=request.getAttribute("newlvlno") %>)</th>
                    <th align="left" style="padding-left: 20px" colspan="5"> New Section
                    </th>
                </tr>
                <tr>
                    <th align="right" style="padding-right:20px" width="15%">Section Title<sup>*</sup></th>
                    <td colspan="5">
                        <input type="text" name="levelTitle" id="levelTitle" class="validateRequired" style="height: 25px; width: 80%"/>
                    </td>
                </tr>
                <tr>
                    <th align="right" style="padding-right:20px">Short Description</th>
                    <td colspan="5">
                        <!-- <input type="text" name="shortDesrciption" id="shortDesrciption" style="width:80%"/> -->
                        <textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="shortDesrciption"></textarea>  <!-- id="editor1111" -->
                    </td>
                </tr>
                <tr>
                    <th align="right" style="padding-right:20px" valign="top">Long Description</th>
                    <td colspan="5">
                        <!-- <textarea rows="4" cols="10" name="longDesrciption" id="longDesrciption" style="width:80%"></textarea> -->
                        <textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="longDesrciption"></textarea>  <!-- id="editor2222" -->
                    </td>
                </tr>
                <tr>
                    <th align="right" style="padding-right:20px">Weightage %<sup>*</sup></th>
                    <td>
                        <!-- <input type="text" name="testFrom"	id="testFrom" class="validate[required]"/> -->
                        <input type="text" name="sectionWeightage" id="sectionWeightage" style="height: 25px;" class="validateRequired" value="<%=request.getAttribute("weightage") %>" onkeyup="validateSECANDSUBSECScore(this.value,'sectionWeightage','hidesectionWeightage');" onkeypress="return isNumberKey(event)"/>
                        <input type="hidden" name="hidesectionWeightage" id="hidesectionWeightage" value="<%=request.getAttribute("weightage") %>" />
                    </td>
                </tr>
                <tr>
                    <th align="right" style="padding-right:20px">Select Attribute</th>
                    <td><select name="attribute"><%=attribute %> </select></td>
                </tr>
                <tr>
                    <th align="right" style="padding-right:20px">Work Flow</th>
                    <td colspan="5">
                        <%
                        String member1=(String)request.getAttribute("member");
                        //if(request.getAttribute("oreinted") != null && !request.getAttribute("oreinted").equals("5")){
                        String[] memberArray1=member1.split(",");
                        //System.out.println("member ==== > "+member);
                        for(int i=0;i<memberArray1.length;i++){
                        %>
                        	<span style="float: left; width: 60px; text-align: center;">Step<%=i+1%></span>
                        <%} %>
                    </td>
                </tr>
                <%
                    String member=(String)request.getAttribute("member");
                    //if(request.getAttribute("oreinted") != null && !request.getAttribute("oreinted").equals("5")) {								
                    String[] memberArray=member.split(",");
                    //System.out.println("member ==== > "+member);
                    for(int i=0;i<memberArray.length;i++) {
                    %>			
                <tr>
                    <th align="right" style="padding-right: 20px;"><%=memberArray[i] %></th>
                    <td colspan="5">
                        <%for(int j=1; j<=memberArray.length; j++) { %>
                        <span style="float: left; width: 60px; text-align: center;">
                        	<input type="radio" name="<%=memberArray[i]%>" value="<%=j %>"<%if(j==1) { %> checked="checked" <% } %>/>
                        </span>
                        <% } %>
                    </td>
                </tr>
                <% } 
                    //} %>
            </table>
            <% } else { %>
            <input type="hidden" name="levelTitle" id="levelTitle" value=""/>
            <input type="hidden" name="attribute" value="<%=mainLevelList!=null ? mainLevelList.get(5) : "0" %>"/>
            <%} %>
        </div>
        <%if(type == null){ %>
        <div style="float:left; margin-top: 15px; width: 100%;">
            <span id="sectionLinkDiv<%=(request.getAttribute("divCount"))%>" style="float: left; margin-left: 100px;"><a href="javascript:void(0)" title="Add Subsections" onclick="openSectionDiv('section','<%=(request.getAttribute("divCount"))%>');"> +Subsections </a></span>
            <span id="assessLinkDiv<%=(request.getAttribute("divCount"))%>" style="float: left; margin-left: 100px;"><a href="javascript:void(0)" title="Add Assessments" onclick="openSectionDiv('assessment','<%=(request.getAttribute("divCount"))%>');"> +Assessments </a></span>
            <%-- <span id="sectionLinkDiv<%=(request.getAttribute("divCount"))%>" style="float: left; margin-left: 100px;"><a href="javascript:void(0)" title="Add Subsections" onclick="openSectionDiv1();"> +Subsections </a></span>
                <span id="assessLinkDiv<%=(request.getAttribute("divCount"))%>" style="float: left; margin-left: 100px;"><a href="javascript:void(0)" title="Add Assessments" onclick="openSectionDiv1();"> +Assessments </a></span> --%>
        </div>
        <% } %>
        <%
            String secsunsec = request.getAttribute("newsysno")!=null ? (String)request.getAttribute("newsysno") : ((String)request.getAttribute("newlvlno")+".1") ;
            String sectioncnt = secsunsec.substring(0,1);
            String subsectioncnt = secsunsec.substring(1,secsunsec.length());
            %>
        <div id="sectionAssessmentdiv<%=(request.getAttribute("divCount"))%>" style="clear:both;box-shadow: rgba(0, 0, 0, 0.180392) 0px 2px 18px 0px;margin: 5px;margin-left: 20px;padding: 5px;display: <%if(type == null){ %>none;<%} else {%>block;<% }%> ">
            <%if(type != null && linkType != null && !linkType.equals("section")){ %>
				<a id="assessdiv<%=(request.getAttribute("divCount"))%>" style="float: right" class="close-font" onclick="closeIframe('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>','<%=(request.getAttribute("divCount"))%>')"/></a>
				<%} else{%>
				<span id="assessdiv<%=(request.getAttribute("divCount"))%>" style="float: right"></span>
				<%} %>
				<div class="clr"></div>
            <div id="sectiondiv<%=(request.getAttribute("divCount"))%>" style="display: <%if(linkType != null && linkType.equals("section")){ %>block;<%} else {%>none;<% }%>">
                <p style="padding-left: 5px; text-align: left;float:left; font-size: 16px; margin-bottom: 10px;"><%=request.getAttribute("newsysno")!=null ? request.getAttribute("newsysno") : request.getAttribute("newlvlno")+".1"%> Subsection of " 
                    <%if(type != null && linkType != null && linkType.equals("section")){ %>
                    <%=mainLevelList.get(1) %> <%} else { %><span id="sectionnamespan">New Section</span><%} %>" 
                </p>
                
                <a href="javascript:void(0)" style="float: right" class="close-font" onclick="closeSubsectionDiv('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>')"/></a>
                <table class="table table_no_border clr">
                    <tr>
                        <th align="right" style="padding-right:20px" width="15%"><%=request.getAttribute("newsysno")!=null ? request.getAttribute("newsysno") : request.getAttribute("newlvlno")+".1"%>)</th>
                        <th align="left" style="padding-left:20px" colspan="5">New Subsection
                        </th>
                    </tr>
                    <tr id="sectionnameTr">
                        <th align="right" style="padding-right:20px" width="15%">Subsection Title<sup>*</sup></th>
                        <td colspan="5">
                            <input type="hidden" name="hidesectioncnt" id="hidesectioncnt" value="<%=sectioncnt%>"/>
                            <input type="hidden" name="hidesubsectioncnt" id="hidesubsectioncnt" value="<%=subsectioncnt%>"/> 
                            <input type="text" name="subsectionname" id="subsectionname" class="validateRequired" style="height: 25px; width: 450px;"/>
                        </td>
                    </tr>
                    <tr id="sectionDescTr">
                        <th align="right" style="padding-right:20px">Subsection Short Description</th>
                        <td colspan="5">
                            <!-- <input type="text" name="subsectionDescription" id="subsectionDescription" style="width: 450px;"/> -->
                            <textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="subsectionDescription"></textarea>  <!-- id="editor3333" -->
                        </td>
                    </tr>
                    <tr id="sectionLongDescTr">
                        <th align="right" style="padding-right:20px">Subsection Long Description</th>
                        <td colspan="5">
                            <!-- <input type="text" name="subsectionLongDescription" id="subsectionLongDescription" style="width: 450px;"/> -->
                            <textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="subsectionLongDescription"></textarea>  <!-- id="editor4444" -->
                        </td>
                    </tr>
                    <tr>
                        <th align="right" style="padding-right:20px">Weightage %<sup>*</sup></th>
                        <td>
                            <input type="text" name="subSectionWeightage" id="subSectionWeightage" style="height: 25px;" class="validateRequired" value="<%=(request.getAttribute("weightage")!=null && !request.getAttribute("weightage").toString().equals("")) ? "100" : request.getAttribute("subWeightage") %>" onkeyup="validateSECANDSUBSECScore(this.value,'subSectionWeightage','hidesubSectionWeightage');" onkeypress="return isNumberKey(event)"/>
                            <input type="hidden" name="hidesubSectionWeightage" id="hidesubSectionWeightage" value="<%=(request.getAttribute("weightage")!=null && !request.getAttribute("weightage").toString().equals("")) ? "100" : request.getAttribute("subWeightage") %>" />
                        </td>
                    </tr>
                </table>
            </div>
            <div>
                <ul class="level_list ul_class">
                    <li>
                        <div style="margin-top: 10px; background-color: lemonchiffon; border: 1px solid;padding: 10px;">
                            <table class="table table_no_border">
                                <tbody>
                                    <tr>
                                        <td>System:<sup>*</sup></td>
                                        <td>
                                        
                                   <!-- ===start parvez date: 21-12-2021=== -->
                                            <%-- <s:select theme="simple" name="appraisalSystem" headerKey="" headerValue="Select System"
                                                list="#{'1':'Competency, Goal & Objective Martix','2':'Other','3':'Goal','4':'KRA','5':'Target'}"
                                            onchange="showSystem(this.value,'','addSectionSubsection');"  cssClass="validateRequired"/> --%>
                                        <% 
                                        String sectionCnt11 = (String)request.getAttribute("newlvlno");
                                        String strMember = (String)request.getAttribute("member");
                                    /* ===start parvez date: 13-01-2022=== */    
                                        if(strMember.contains("Manager")){ %>
                                    <!-- ===end parvez date: 13-01-2022=== -->    
                                        	<s:select theme="simple" name="appraisalSystem" headerKey="" headerValue="Select System"
                                                list="#{'1':'Competency, Goal & Objective Martix','2':'Other','3':'Goal','4':'KRA','5':'Target','6':'New Goal'}"
                                            onchange="showSystem(this.value,'','addSectionSubsection');"  cssClass="validateRequired"/>
                                        <% } else{ %> 
                                        		<s:select theme="simple" name="appraisalSystem" headerKey="" headerValue="Select System"
                                                list="#{'1':'Competency, Goal & Objective Martix','2':'Other','3':'Goal','4':'KRA','5':'Target'}"
                                            onchange="showSystem(this.value,'','addSectionSubsection');"  cssClass="validateRequired"/> 
                                        <% } %> 
                                            
                                  <!-- ===end parvez date: 21-12-2021=== -->          
                                            
                                            <%-- <%if(type != null && linkType != null && !linkType.equals("section")){ %>
                                            <a href="javascript:void(0)" id="assessdiv<%=(request.getAttribute("divCount"))%>" style="float: right" class="close-font" onclick="closeSubsectionDiv('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>','<%=(request.getAttribute("divCount"))%>')"/></a>
                                            <%} else{%>
                                            <a href="javascript:void(0)" id="assessdiv<%=(request.getAttribute("divCount"))%>"  class="close-font"  style="float: right"/></a>
                                            <%} %> --%>
                                            
                                            <span id="assessdivnull" style="display: none;"></span>
                                        </td>
                                        <td></td>
                                    </tr>
                                    <tr id="scoreCardID" style="display: none;">
                                        <td>Competency<sup>*</sup></td>
                                        <td>
                                            <s:select theme="simple" name="scoreCard"
                                                headerKey="" headerValue="Select Competency" cssClass="validateRequired" id="scoreCard"
                                                onchange="getcontent(this.value,'addSectionSubsection');"
                                                list="#{'1':'Competency, Goal & Objective Martix', 
                                                '3':'Competency & Goal Martix','2':'Competency Martix'}" />
                                        </td>
                                        <td></td>
                                    </tr>
                                    <tr id="otherqueTypeTr" style="display: none;">
                                        <td>Question Type:<sup>*</sup></td>
                                       
                         <!-- ===start parvez date: 13-01-2022=== -->
                                       
                                        <%-- <td>
                                            <s:select theme="simple" name="otherQuestionType" id="otherQuestionType" cssClass="validateRequired"
                                                list="#{'Without Short Description':'Without Short Description', 'With Short Description':'With Short Description'}" />
                                        </td> --%>
                                        <td id="otherQuestionTypeTd" >
                                            <s:select theme="simple" name="otherQuestionType" id="otherQuestionType" cssClass="validateRequired"
                                                list="#{'Without Short Description':'Without Short Description', 'With Short Description':'With Short Description'}" />
                                        </td>
                                        <td id="pgQuestionTd" style="display:none" >
											<input type="text" name="pgTask" id="pgTask" class="form-control"></input>
										</td>
                         <!-- ===end parvez date: 13-01-2022=== -->
                                        <td></td>
                                    </tr>
                                    <tr id="anstypeTr">
                                        <td>Select Answer Type</td>
                                        <td>
                                            <select name="ansType" id="ansTypeAddSAndSubS" onchange="showAnswerTypeDiv1(this.value)"><%=anstype %></select>
                                        </td>
                                        <td>
                                            <div id="anstypediv">
                                                <div id="anstype9">
                                                    a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"><br>
                                                    c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"><br>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </li>
                </ul>
            </div>
            <div class="sectionfont clr" id="mainDiv" style="margin: 10px 0px 0px 0px;"></div>
            <div class="sectionfont" id="assessOfSubsectionDiv"></div>
            <div class="sectionfont" id="otherDiv" style="display: none;">
                <ul class="level_list ul_class">
                    <li> <a href="javascript:void(0)" onclick="getOtherquestion('0');"><i class="fa fa-plus-circle"></i>Assess info/points</a></li>
                    <li id="otherQuestionLi"></li>
                </ul>
            </div>
            <div class="sectionfont" id="goalDiv" style="display: none;"></div>
        </div>
        <div align="center">
            <s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeSubsectionDiv('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>','<%=(request.getAttribute("divCount"))%>')"/>
        </div>
        <s:hidden name="plancount" id="plancount"></s:hidden>
    </s:form>
</div>
<!-- <g:compress> -->
<script> 
	$(function(){
	    $("input[name='submit']").click(function(){
	    	//$(".formIDvalidateRequired").prop('required',true);
	    	$("#formAddAppraisalLevelAndSystem").find('.validateRequired').filter(':hidden').prop('required',false);
	    	$("#formAddAppraisalLevelAndSystem").find('.validateRequired').filter(':visible').prop('required',true);
	    });
	});
	
	$("#formAddAppraisalLevelAndSystem").submit(function(event){
		event.preventDefault();
		
		for ( instance in CKEDITOR.instances ) {
	        CKEDITOR.instances[instance].updateElement();
	    }
		var id = document.getElementById("id").value;
		var appFreqId = document.getElementById("appFreqId").value;
		var fromPage = document.getElementById("fromPage").value;
		
		var form_data = $("#formAddAppraisalLevelAndSystem").serialize();
	   	$.ajax({ 
	   		type : 'POST',
	     		url: "addAppraisalLevelAndSystem.action",
	   		data: form_data+"&submit=Save",
	   		success: function(result){
	   			$("#reviewInfo").html(result);
	      	},
			error: function(result){
				$.ajax({
					url: 'AppraisalSummary.action?id='+id+'&appFreqId='+appFreqId+'&fromPage='+fromPage,
					cache: true,
					success: function(result){
						$("#reviewInfo").html(result);
			   		}
				});
			}
	   	});
	});
	
   /* function addAppLevelAndSystem(appId,appFreqId,fromPage){
   	var form_data = $("#formAddAppraisalLevelAndSystem").serialize();
   	$.ajax({ 
   		type : 'POST',
     		url: "addAppraisalLevelAndSystem.action",
   		data: form_data+"&submit=Save",
   		success: function(result){
   			$("#reviewInfo").html(result);
      	}
   	});
   } */
   

    $(function(){
    	<%if(type == null){ %>
			// Replace the <textarea id="editor1"> with an CKEditor instance.
			if(document.getElementById( 'editor1111' )) {
				CKEDITOR.replace('editor1111', {
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
			
			// Replace the <textarea id="editor2"> with an CKEditor instance.
			if(document.getElementById( 'editor2222' )) {
				CKEDITOR.replace('editor2222', {
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
	<% } %>			
			
			// Replace the <textarea id="editor1"> with an CKEditor instance.
			if(document.getElementById( 'editor3333' )) {
				CKEDITOR.replace('editor3333', {
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
		
		// Replace the <textarea id="editor2"> with an CKEditor instance.
		if(document.getElementById( 'editor4444' )) {
			CKEDITOR.replace('editor4444', {
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
    });
    
    		
     	/* function getReviewSummary(strAction,appId,appFreqId,fromPage){
    			$("#reviewInfo").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    			$.ajax({ 
    				type : 'POST',
    				url: strAction+'.action?id='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage,
    			//	data: $("#"+this.id).serialize(),
    				cache: true,
    				success: function(result){
    					// alert("result3==>"+result);
    					$("#reviewInfo").html(result);
    		   		}
    			});
    		} */ 
    			 
    		
</script> 
<!-- </g:compress> -->

