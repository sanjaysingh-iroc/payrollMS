<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<script src="scripts/ckeditor_cust/ckeditor.js"></script>
<!-- Start Dattatray -->

<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript"
	src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<script type="text/javascript"
	src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript"
	src="scripts/select/jquery.multiselect.js"></script>

<!-- End Dattatray -->
<g:compress>
	<style>
.ul_class li {
	margin: 10px 0px 10px 100px;
}

.box-header {
	padding: 6px;
}

.list-unstyled {
	margin-left: 20px;
	list-style-type: circle;
}

a.close-font:before {
	font-size: 24px;
}
</style>
	<link href="scripts/ckeditor/samples/sample.css" rel="stylesheet">
	<script>
        // The instanceReady event is fired, when an instance of CKEditor has finished
        // its initialization.  
        CKEDITOR.on( 'instanceReady', function( ev ) {
        	// Show the editor name and description in the browser status bar.
        	if(document.getElementById( 'eMessage' )) {
        		document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
        	}
        	
        	if(document.getElementById( 'eButtons' )) {
        		document.getElementById( 'eButtons' ).style.display = 'block';
        	}
        }); 
        
        $(document).ready(function(){
        	$("body").on('click','#closeButton',function(){
       			$(".modal-dialog").removeAttr("style");
       		    $("#modalInfo").hide();
       		});
        	$("body").on('click','#closeButton1',function(){
    			$(".modal-dialog1").removeAttr('style');
    			$("#modal-body1").height(400);
    			$("#modalInfo1").hide();
    	    });
        	
       		$("body").on('click','.close',function(){
       			$(".modal-dialog").removeAttr("style");
       		    $("#modalInfo").hide();
       		});
       		
	       	 $("#from").datepicker({
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
	         });
         
       	    // Replace the <textarea id="editor1"> with an CKEditor instance.
            if(document.getElementById("editor1")) {
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
            }
            
            // Replace the <textarea id="editor2"> with an CKEditor instance.
            if(document.getElementById("editor2")) {
            	CKEDITOR.replace( 'editor2', {
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
            
            
            // Replace the <textarea id="editor3"> with an CKEditor instance.
            if(document.getElementById("editor3")) {
            	CKEDITOR.replace( 'editor3', {
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
            // Replace the <textarea id="editor4"> with an CKEditor instance.
            if(document.getElementById("editor4")) {
            	CKEDITOR.replace( 'editor4', {
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
            
            /*created by dattatray  */
            createRevieweePanelForReview();
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
        	if(document.getElementById( 'eMessage' )) {
        		document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
        	}
        	
        }
        
        function onBlur() {
        	if(document.getElementById( 'eMessage' )) {
        		document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
        	}
        }
        
        CKEDITOR.config.width='915px';
        
        
        $(function() {
        	/* Dattatray : This function moved from $(document).ready(function()) */
        	 $("input[type='submit']").click(function(){
    	    		$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
    	    		$("#formID").find('.validateRequired').filter(':visible').prop('required',true);
    	    		
    	    		for ( instance in CKEDITOR.instances ) {
    	    	        CKEDITOR.instances[instance].updateElement();
    	    	    }
    	    });
        	 
        	$("#from").datepicker({
        		format : 'dd/mm/yyyy', 
        		onClose: function(selectedDate){
        			$("#to").datepicker("option", "minDate", selectedDate);
        		}
        	});
        	
            $( "#to").datepicker({
            	format: 'dd/mm/yyyy',
            	onClose: function(selectedDate){
        			$("#from").datepicker("option", "maxDate", selectedDate);
        		}	
            });
            
            $("#startFrom").datepicker({
        		format : 'dd/mm/yyyy', 
        		onClose: function(selectedDate){
        			$("#endTo").datepicker("option", "minDate", selectedDate);
        		}
        	});
        	
            $( "#endTo").datepicker({
            	format: 'dd/mm/yyyy',
            	onClose: function(selectedDate){
        			$("#startFrom").datepicker("option", "maxDate", selectedDate);
        		}	
            });
            
            $("#reviewerId").multiselect().multiselectfilter();
            
            $("select[multiple='multiple']").multiselect().multiselectfilter();/* created by dattatray  */
        }); 
        
        
         
        function setDatepickerDefault(id1,id2){
        	
            $( '#'+id1 ).datepicker({format: 'dd/mm/yyyy'});
            $( '#'+id2 ).datepicker({format: 'dd/mm/yyyy'});
        }
        
        jQuery(document).ready(function(){
            // binds form submission and fields to the validation engine
           // jQuery("#formID").validationEngine();
        
            if(document.getElementById('oreinted') != null){
            	var value = document.getElementById('oreinted').value;
            	showOrientData(value);
            }
            
            onloadAddNewSection();
            
        }); 
         
        function isOnlyNumberKey(evt) {
        	var charCode = (evt.which) ? evt.which : event.keyCode;
        	if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
        		return true; 
        	}
        	return false;
        }
        
      		var cxtpath='<%=request.getContextPath()%>'; 
         
        	function selectAttributeskill(id, val) {
        		if (val == 1) {
        			document.getElementById("attributeTr" + id).style.display = 'table-row';
        		} else {
        			document.getElementById("attributeTr" + id).style.display = 'none';
        		}
        	}
        
        	function checkFrequency(value) {
        		
        		if (value == '3') {
        			//alert("dfgsdf");
        			document.getElementById("weekly").style.display = "none";
        			document.getElementById("annualy").style.display = "none";
        			document.getElementById("monthly").style.display = "block";
        			document.getElementById("dayMonth").style.display = "none";
        			document.getElementById("quarterly").style.display = "none";
        			document.getElementById("halfYearly").style.display = "none";
        			
        			document.getElementById("weekday").selectedIndex = 0;
        			document.getElementById("annualDay").selectedIndex = 0;
        			document.getElementById("annualMonth").selectedIndex = 0;
        			document.getElementById("day").selectedIndex = 0;
        			document.getElementById("monthday").selectedIndex = 0;
        			document.getElementById("month").selectedIndex = 0;
        
        		} else if (value == '2') {
        			document.getElementById("weekly").style.display = "block";
        			document.getElementById("annualy").style.display = "none";
        			document.getElementById("monthly").style.display = "none";
        			document.getElementById("dayMonth").style.display = "none";
        			document.getElementById("quarterly").style.display = "none";
        			document.getElementById("halfYearly").style.display = "none";
        			
        			document.getElementById("weekday").selectedIndex = 0;
        			document.getElementById("annualDay").selectedIndex = 0;
        			document.getElementById("annualMonth").selectedIndex = 0;
        			document.getElementById("day").selectedIndex = 0;
        			document.getElementById("monthday").selectedIndex = 0;
        			document.getElementById("month").selectedIndex = 0;
        
        		}else if (value == '6') {
        			document.getElementById("weekly").style.display = "none";
        			document.getElementById("annualy").style.display = "block";
        			document.getElementById("monthly").style.display = "none";
        			document.getElementById("dayMonth").style.display = "none";
        			document.getElementById("quarterly").style.display = "none";
        			document.getElementById("halfYearly").style.display = "none";
        			
        			document.getElementById("weekday").selectedIndex = 0;
        			document.getElementById("annualDay").selectedIndex = 0;
        			document.getElementById("annualMonth").selectedIndex = 0;
        			document.getElementById("day").selectedIndex = 0;
        			document.getElementById("monthday").selectedIndex = 0;
        			document.getElementById("month").selectedIndex = 0;
        			
        		}else if (value == '4' || value == '5') {	
        			document.getElementById("weekly").style.display = "none";
        			document.getElementById("annualy").style.display = "none";
        			document.getElementById("monthly").style.display = "none";
        			document.getElementById("dayMonth").style.display = "block";
        			if (value == '4') {
        				document.getElementById("quarterly").style.display = "block";
        				document.getElementById("halfYearly").style.display = "none";
        			} else {
        				document.getElementById("halfYearly").style.display = "block";
        				document.getElementById("quarterly").style.display = "none";
        			}
        			document.getElementById("weekday").selectedIndex = 0;
        			document.getElementById("annualDay").selectedIndex = 0;
        			document.getElementById("annualMonth").selectedIndex = 0;
        			document.getElementById("day").selectedIndex = 0;
        			document.getElementById("monthday").selectedIndex = 0;
        			document.getElementById("month").selectedIndex = 0;
        			
        		} else {
        			document.getElementById("weekly").style.display = "none";
        			document.getElementById("annualy").style.display = "none";
        			document.getElementById("monthly").style.display = "none";
        			document.getElementById("dayMonth").style.display = "none";
        			document.getElementById("quarterly").style.display = "none";
        			document.getElementById("halfYearly").style.display = "none";
        			
        			document.getElementById("weekday").selectedIndex = 0;
        			document.getElementById("annualDay").selectedIndex = 0;
        			document.getElementById("annualMonth").selectedIndex = 0;
        			document.getElementById("day").selectedIndex = 0;
        			document.getElementById("monthday").selectedIndex = 0;
        			document.getElementById("month").selectedIndex = 0;
        			
        		}
        	}
        
        	function getEmployeebyOrg(){
        		var strOrg = getSelectedValue("strOrg");
        		var action = 'getEmployeeList.action?strOrg=' + strOrg;
        		
        		/* document.getElementById("wlocation").selectedIndex = 0;
        		document.getElementById("depart").selectedIndex = 0;
        		document.getElementById("strLevel").selectedIndex = 0;
        		document.getElementById("desigIdV").selectedIndex = 0;
        		document.getElementById("gradeIdV").selectedIndex = 0;
        		document.getElementById("employee").selectedIndex = 0; */
        
        		getContent('myEmployee', action);
        		setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
        	}
        	
        	function getEmployeebyLocation() {
        		var location = getSelectedValue("wlocation");
        		var strOrg = getSelectedValue("strOrg");
        		var action = 'getEmployeeList.action?strOrg='+ strOrg+'&location='+ location;
        
        		/* document.getElementById("depart").selectedIndex = 0;
        		document.getElementById("strLevel").selectedIndex = 0;
        		document.getElementById("desigIdV").selectedIndex = 0;
        		document.getElementById("gradeIdV").selectedIndex = 0;
        		document.getElementById("employee").selectedIndex = 0; */
        
        		getContent('myEmployee', action);
        		setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
        		setTimeout(function(){ $("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); }, 500);
        
        	}
        
        	function getEmployeebyDepart() {
        		var location = getSelectedValue("wlocation");
        		var depart = getSelectedValue("depart");
        
        		/* document.getElementById("strLevel").selectedIndex = 0;
        		document.getElementById("desigIdV").selectedIndex = 0;
        		document.getElementById("gradeIdV").selectedIndex = 0;
        		document.getElementById("employee").selectedIndex = 0; */
        		
        		var action = 'getEmployeeList.action?depart=' + depart;
        		if (location == '') {
        		} else {
        			if (location != '') {
        				action += '&location=' + location;
        			}
        			getContent('myEmployee', action);
        			setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
        		}
        		
        	}
        
        	function getEmployeebyLevel() {
        		var location = getSelectedValue("wlocation");
        		var depart = getSelectedValue("depart");
        		var Level = getSelectedValue("strLevel");
        
        	/* 	document.getElementById("desigIdV").selectedIndex = 0;
        		document.getElementById("gradeIdV").selectedIndex = 0;
        		document.getElementById("employee").selectedIndex = 0; */
        
        		var action = 'getEmployeeList.action?level=' + Level;
        		if (location == '' && depart == '') {
        		} else {
        			if (location != '') {
        				action += '&location=' + location;
        			}
        			if (depart != '') {
        				action += '&depart=' + depart;
        			}
        
        			getContent('myEmployee', action);
        			setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
        			window.setTimeout(function() {
        				getContent('myDesig', 'getDesignation.action?strLevel=' + Level);
        			}, 200);
        			
        		}
        
        	}
        
        	function getEmployeebyDesig() {
        		var location = getSelectedValue("wlocation");
        		var depart = getSelectedValue("depart");
        		var Level = getSelectedValue("strLevel");
        		var design = getSelectedValue("desigIdV");
        
        		/* document.getElementById("gradeIdV").selectedIndex = 0;
        		document.getElementById("employee").selectedIndex = 0; */
        
        		var action = 'getEmployeeList.action?design=' + design;
        
        		if (location == '' && depart == '' && Level == '') {
        
        		} else {
        			if (location != '') {
        				action += '&location=' + location;
        			}
        			if (depart != '') {
        				action += '&depart=' + depart;
        			}
        			if (Level != '') {
        				action += '&level=' + Level;
        			}
        			getContent('myEmployee', action);
        			setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
        			
        			window.setTimeout(function() {
        				getContent('myGrade', 'getGrade.action?strDesignation=' + design);
        			}, 200);
        		}
        	}
        	
        	function getEmployeebyGrade() {
        		var location = getSelectedValue("wlocation");
        		var depart = getSelectedValue("depart");
        		var Level = getSelectedValue("strLevel");
        		var design = getSelectedValue("desigIdV");
        		var grade = getSelectedValue("gradeIdV");
        
        		var action = 'getEmployeeList.action?grade=' + grade;
        
        		/* document.getElementById("employee").selectedIndex = 0; */
        		
        			if (location != '')  {
        				action += '&location=' + location;
        			}if (depart != '')  {
        				action += '&depart=' + depart;
        			}if (Level != '')  {
        				action += '&level=' + Level;
        			}if (design != '')  {
        				action += '&design=' + design;
        			}
        				
        			
        			getContent('myEmployee', action);
        			setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
        	}
        
        	function getSelectedValue(selectId) {
        		var choice = document.getElementById(selectId);
        		var exportchoice = "";
        		for ( var i = 0, j = 0; i < choice.options.length; i++) {
        			if (choice.options[i].selected == true) {
        				if (j == 0) {
        					exportchoice = choice.options[i].value;
        					j++;
        				} else {
        					exportchoice += "," + choice.options[i].value;
        					j++;
        				}
        			}
        		}
        		return exportchoice;
        	}
        
        	function showSystem(value, divcount) {
        		//alert("showSystem(value, divcount) " + divcount+" , "+ value);
        		if (value == '1') {
        			document.getElementById("mainDiv").style.display='block';
        			document.getElementById("otherDiv").style.display='none';
        			document.getElementById("assessOfSubsectionDiv").style.display='none';
        			document.getElementById("otherQuestionLi").innerHTML='';
        			document.getElementById("scoreCardID" + divcount).style.display = 'table-row';
        			document.getElementById("otherqueTypeTr").style.display = 'none';
        			document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
        			//document.getElementById("weightageTr" + divcount).style.display = 'table-row';
        			
        		} else if (value == '2') {
        			questionCnt=0;
        			document.getElementById("otherqueTypeTr").style.display = 'table-row';
        			document.getElementById("mainDiv").style.display='none';
        			document.getElementById("otherDiv").style.display='block';
        			document.getElementById("assessOfSubsectionDiv").style.display='block';
        			//document.getElementById("otherDiv").innerHTML=otherDivData();
        			document.getElementById("mainDiv").innerHTML='';
        			document.getElementById("scoreCardID" + divcount).style.display = 'none';
        			document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
        			//document.getElementById("weightageTr" + divcount).style.display = 'table-row';
        		} else {
        			document.getElementById("scoreCardID" + divcount).style.display = 'none';
        			document.getElementById("otherqueTypeTr").style.display = 'none';
        			document.getElementById("otherDiv").style.display='none';
        			document.getElementById("assessOfSubsectionDiv").style.display='none';
        			//document.getElementById("otherDiv" + divcount).style.display = 'none';
        			//document.getElementById("scoreCardDiv" + divcount).style.display = "none";
        			document.getElementById("mainDiv").innerHTML='';
        			document.getElementById("otherQuestionLi").innerHTML='';
        			document.getElementById("anstypeTr" + divcount).style.display = 'none';
        			//document.getElementById("weightageTr" + divcount).style.display = 'none';
        		}
        		document.getElementById("subsectionnamespan").innerHTML = document.getElementById("subsectionname").value;
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
        						+"<li><a href=\"javascript:void(0)\" onclick=\"addScoreCard()\">Add Competency</a></li>"
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
        						/* +"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\"><img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/> Delete Goal</a></li>" */
        						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i> Delete Goal</a></li>"
        						
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
        									/* +"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\"><img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/> Delete Goal</a></li>" */
        									+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i> Delete Goal</a></li>"
        									
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
        						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ objectiveID + "','"+ objectiveDivId + "','objectivecount');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i> Delete Objective</a>"
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
        						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i>Delete Competency</a></li>"
        						
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
        							+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i> Delete Competency</a></li>"
        					
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
        											+"<li>"
        												+ getquestion(scoreCnt+'.'+(parseInt(val)+1),'.1)',measureID)
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
        	
        	    
        
        	 function deleteBlock(childId, parentIds,type){
        			//alert("childId===>> " + childId+"==>parentIds==>"+parentIds+"==>type==>"+type);
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
        				+"<input type=\"number\" style=\"width: 35px;\" name=\"weightage\" id=\"weightage"+id+cnt+"\" class=\"validateRequired form-control \" onkeypress=\"return isNumberKey(event)\" onkeyup=\"validateScore(this.value,'weightage"+id+cnt+"','"+id+"','weightage');\"/>"
        				+ "<input type=\"hidden\" name=\"hideweightage\" id=\"hideweightage"+id+cnt+"\" value=\"100\"/></span>&nbsp;&nbsp;"
        
        				+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"');openOtheQueMode('NQ','"+cnt+"');\" > +Q </a></span>&nbsp;"
        				+"<span id=\"checkboxspan"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+ cnt+ "')\" />"
        				+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/></span>"
        				
        				+"<a href=\"javascript:void(0)\"  title=\"Add New Question\" onclick=\"addQuestions('"+id+"','"+scoreCnt+"')\" ><i class=\"fa fa-plus-circle\"></i></a>&nbsp;&nbsp; "
        				+"<a href=\"javascript:void(0)\" title=\"Remove Question\" class=\"close-font\" onclick=\"deleteBlock('"+QuestionID+"','"+ id+ "','questioncount');\"/>"
        
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
        				+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"number\" name=\"objectiveWeightage\" id=\"objectiveWeightage"+id+objectiveCnt+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired form-control \" value=\"100\" onkeyup=\"validateScore(this.value,'objectiveWeightage"+id+objectiveCnt+"','"+id+"','objectiveWeightage');\"/>"
        				+ "<input type=\"hidden\" name=\"hideobjectiveWeightage\" id=\"hideobjectiveWeightage"+id+objectiveCnt+"\" value=\"100\" /></td></tr></table>";
        				//jQuery("#formID").validationEngine();
        		return a;
        		//jQuery("#formID").validationEngine();
        	} 
        
        	function getMeasureData(val,measureCnt,id) {
        
        		var a = "<table class=\"table\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Measures</td></tr>"
        				+ "<tr><th></th><th style=\"text-align: right;\">Section name<sup>*</sup></th><td><input type=\"text\" name=\"measuresSectionName\" id=\"measuresSectionName\" class=\"validateRequired form-control \" style=\"width: 450px;\" /></td></tr>"
        				+ "<tr><th></th><th style=\"text-align: right;\">Description</th><td><input type=\"text\" name=\"measuresDescription\" style=\"width: 450px;\" /></td></tr>"
        				+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"number\" name=\"measureWeightage\" id=\"measureWeightage"+id+measureCnt+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired form-control \" value=\"100\" onkeyup=\"validateScore(this.value,'measureWeightage"+id+measureCnt+"','"+id+"','measureWeightage');\"/>"
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
        					+ "<tr><th></th><th style=\"text-align: right;\">Weightage %<sup>*</sup></th><td><input type=\"number\" name=\"goalWeightage\" id=\"goalWeightage"+id+goalCnt+"\" onkeypress=\"return isNumberKey(event)\"  class=\"validateRequired form-control \" value=\"100\" onkeyup=\"validateScore(this.value,'goalWeightage"+id+goalCnt+"','"+id+"','goalWeightage');\"/>"
        					+ "<input type=\"hidden\" name=\"hidegoalWeightage\" id=\"hidegoalWeightage"+id+goalCnt+"\" value=\"100\" /></td>"
        					+ "</tr></table>";
        					//jQuery("#formID").validationEngine();
        			return a;
        		///	jQuery("#formID").validationEngine();
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
        
        			document.getElementById("QuestionName" + cnt1).style.display = 'table-row';
        			document.getElementById("AddQuestion" + cnt1).style.display = 'table-row';
        			document.getElementById("selectanstype" + cnt1).style.display = 'table-row';
        			document.getElementById("answerType" + cnt1).style.display = 'table-row';
        			document.getElementById("answerType1" + cnt1).style.display = 'table-row';
        			if(document.getElementById("answerType2" + cnt1)){
        				document.getElementById("answerType2" + cnt1).style.display = 'table-row';
        			}
        
        		} else {
        
        			document.getElementById("QuestionName" + cnt1).style.display = 'none';
        			document.getElementById("AddQuestion" + cnt1).style.display = 'none';
        			document.getElementById("selectanstype" + cnt1).style.display = 'none';
        			document.getElementById("answerType" + cnt1).style.display = 'none';
        			document.getElementById("answerType1" + cnt1).style.display = 'none';
        			if(document.getElementById("answerType2" + cnt1)){
        				document.getElementById("answerType2" + cnt1).style.display = 'none';
        			}
        		}
        	}
        	
        	function addNewQuestionOther(id, val, cnt1) {
        
        		if (val == '0') {
        			//document.getElementById("selectanstype" + cnt1).style.display = 'table-row';
        			document.getElementById("answerType" + cnt1).style.display = 'table-row';
        			document.getElementById("answerType1" + cnt1).style.display = 'table-row';
        			document.getElementById("checkboxspan" + cnt1).style.display = 'block';
        			if(document.getElementById("answerType2" + cnt1)){
        				document.getElementById("answerType2" + cnt1).style.display = 'table-row';
        			}
        			
        
        		} else {
        			//document.getElementById("selectanstype" + cnt1).style.display = 'none';
        			document.getElementById("answerType" + cnt1).style.display = 'none';
        			document.getElementById("answerType1" + cnt1).style.display = 'none';
        			document.getElementById("checkboxspan" + cnt1).style.display = 'none';
        			if(document.getElementById("answerType2" + cnt1)){
        				document.getElementById("answerType2" + cnt1).style.display = 'none';
        			}
        		}
        	}
        
        	function changeNewQuestionType(val, id, id1, id2, cnt) {
        		//alert(" val : "+val +" id : "+id +" id1 : "+id1);	
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
        
        		 } else if (val == 6) {
        			addTrueFalseType(id,cnt);
        			document.getElementById(id).style.display = 'table-row';
        			document.getElementById(id1).innerHTML ="";
        			document.getElementById(id1).style.display = 'none';
        			document.getElementById(id2).innerHTML = "";
        			document.getElementById(id2).style.display = 'none';
        
        		} else if (val == 5) {
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
        		document.getElementById(id).innerHTML = "<th></th><th></th><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
        		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
        	}
        	
        	function addYesNoType(id,cnt){
        		document.getElementById(id).innerHTML = "<th></th><th></th><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
        		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
        	} 
        	function addQuestionType1(id,cnt) {
        		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona\"  class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionc\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
        	}
        	function addQuestionType2(id,cnt) {
        		document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\"  id=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
        	}
        	function addQuestionType3(id,cnt) {
        		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb\"  class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
        	}
        	function addQuestionType4(id1,cnt) {
        		document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc\"  class=\"validateRequired form-control\"/> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
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
        	
          function otherDivData(){
        		
        		var a="<ul class=\"level_list ul_class\"><li><table class=\"table table_no_border\"><tr><th width=\"15%\" style=\"text-align: right;\">Question Type</th><td><select name=\"otherQuestionType\" id=\"otherQuestionType\">"
        		+"<option value=\"\">Select Type</option><option value=\"With Short Description\">With Short Description</option>"
        		+"<option value=\"Without Short Description\">Without Short Description</option></select>"
        		+"</td></tr>"
        		
        		+"<tr><th align=\"right\" style=\"padding-right:20px\" width=\"17%\">System<sup>*</sup></th>"
        		+"<td colspan=\"5\"><select name=\"appraisalSystem\" id=\"appraisalSystem\" class=\"validateRequired\">"
        		+"<option value=\"\">Select System</option><option value=\"1\">Balance Score Card Review System</option>"
        		+"<option value=\"2\">Other</option></select>"
        		+"<option value=\"3\">Goal</option></select>"
        		+"<option value=\"4\">KRA</option></select>"
        		+"<option value=\"5\">Target</option></select>"
        		+"</td></tr>"
        		
        		+"<tr id=\"scoreCardID\" style=\"display:none;\"> <th width=\"17%\" style=\"padding-right:20px\" align=\"right\">Competency</th>"
        		+"<td colspan=\"5\"><select name=\"scoreCard\" id=\"scoreCard\" onchange=\"getcontent(this.value);\">"
        		+"<option value=\"\">Select Competency</option><option value=\"1\">Competencies + Goals + Objectives + Measures</option>"
        		+"<option value=\"3\">Competencies + Goals + Measures</option></select>"
        		+"<option value=\"2\">Competencies + Measures</option></select>"
        		//+"<tr><th style=\"text-align: right;\">Select Attribute</th><td><select name=\"attribute\">"+attribute+"</select></td></tr>"
        		//+"<tr><th style=\"text-align: right;\">Weightage</th><td><input type=\"radio\" name=\"checkWeightage\" checked=\"checked\" id=\"checkWeightage\" value=\"1\" onclick=\"getcheckWeightage(this.value);\"/>Yes"
        		//+"<input type=\"radio\" name=\"checkWeightage\" id=\"checkWeightage\" value=\"0\" onclick=\"getcheckWeightage(this.value);\"/>no"
        		//+"<input type=\"hidden\" name=\"hidecheckWeightage\" id=\"hidecheckWeightage\" /></td></tr>"
        		+"</table></li>"
        		+"<li><a href=\"javascript:void(0)\" onclick=\"getOtherquestion('0');\"><i class=\"fa fa-plus-circle\"></i>Assess info / points</a></li>"
        		+"<li id=\"otherQuestionLi\"></li></ul>";
        		return a;
        	} 
        	
          function showAssessOfSubsection(){
        	  //alert("showAssessOfSubsection ");
        		var a="<ul class=\"level_list ul_class\">"
        			+"<p style=\"font-weight: 600;\">Assessments of \" <span id=\"subsectionnamespan\">New Subsection</span> \"</p>"
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
        		
        		 var a = "<li style=\"margin: 0px !important;\"><table class=\"table table_no_border\">"
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
        				
        				+"<a href=\"javascript:void(0)\" title=\"Add New Question\" onclick=\"getOtherquestion('"+cnt+"')\" ><i class=\"fa fa-plus-circle\"></i></a>&nbsp;&nbsp; "
        				+"<a href=\"javascript:void(0)\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\" class=\"close-font\"></a>"
        				//+"<a href=\"javascript:void(0)\" class=\"add_lvl\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\" ></a>"
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
        		//jQuery("#formID").validationEngine();
        	 }	
        	}
          
        
          function getQuestoinContentType(cnt){
        		//alert("getQuestoinContentType ");
        		var val = document.getElementById("ansType").value;
        		//alert("getQuestoinContentType val : "+val);
        		var a="";
        		if( val == 8){
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
        	
        	
        	function openSectionDiv(type){
        
        		var cnt = '<%=(request.getAttribute("divCount"))%>';
        		
        		if(type== 'section'){
        			document.getElementById("sectionnamespan").innerHTML = document.getElementById("levelTitle").value;
        			document.getElementById("assessdiv"+cnt).style.display = 'none';
        			document.getElementById("sectionAssessmentdiv").style.display = 'block';
        			document.getElementById("sectiondiv").style.display = 'block';
        			document.getElementById("assessLinkDiv").style.display = 'block';
        			document.getElementById("sectionLinkDiv").style.display = 'none';
        			addNewSection();
        			document.getElementById("subSectionWeightage").value = document.getElementById("hidesubSectionWeightage").value;
        			
        		}else {
        			document.getElementById("assessdiv"+cnt).style.display = 'block';
        			document.getElementById("sectionAssessmentdiv").style.display = 'block';
        			document.getElementById("sectionLinkDiv").style.display = 'block';
        			document.getElementById("sectiondiv").style.display = 'none';
        			document.getElementById("assessLinkDiv").style.display = 'none';
        			document.getElementById("subsectionname").value = '';
        			document.getElementById("subSectionWeightage").value = document.getElementById("hidesubSectionWeightage").value;
        			addNewSection();
        			
        		}
        	}
        	
        	
        	function closeSectionAssessDiv(divname){
        		//document.getElementById("sectionAssessdiv").style.display = 'block';
        		if(document.getElementById("cke_editor1")) {
        			document.getElementById("cke_editor1").value="";
        		}
        		
        		if(document.getElementById("cke_editor2")) {
        			document.getElementById("cke_editor2").value="";
        		}
        		
        		if(document.getElementById("cke_editor3")) {
        			document.getElementById("cke_editor3").value="";
        		}
        		
        		if(document.getElementById("cke_editor4")) {
        			document.getElementById("cke_editor4").value="";
        		}
        		
        		document.getElementById(divname).style.display = 'none';
        		document.getElementById("firstdiv").style.display = 'block';
        		document.getElementById("seconddiv").style.display = 'none';
        	
        		document.getElementById("sectionLinkDiv").style.display = 'block';
        		document.getElementById("subSectionWeightage").value = document.getElementById("hidesubSectionWeightage").value;
        		addNewSection();
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
        		if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
        			row_skill.parentNode.removeChild(row_skill);
        		}
        	}
        	
        	var dialogEdit = '#SelectQueDiv';
        	function openQuestionBank(count) {
        		var ansType=document.getElementById('ansType').value;
        		var dialogEdit = '.modal-body';
            	$(dialogEdit).empty();
            	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
            	$(".modal-title").html('Question Bank');
            	$("#modalInfo").show();
            	$.ajax({
        			url : "SelectQuestion.action?count="+count+"&ansType="+ansType,
        			cache : false,
        			success : function(data) {
        				$(dialogEdit).html(data);
        			}
        		});
        	}
        	
        	
        	function openEditAppraisal(id,appsystem) {
        		if(confirm('Are you sure, You want to create Review from this template?')){
        			
        			var dialogEdit = '.modal-body';
        	    	$(dialogEdit).empty();
        	    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	    	$(".modal-title").html('Edit My Review');
        	    	if($(window).width() >= 1100){
        	    		$(".modal-dialog").width(1100);
        	    	}else{
        	    		$(".modal-dialog").removeAttr("style");
        	    	}
        	    	
        	    	$("#modalInfo").show();
        	    	$.ajax({
        				url : "EditMyReviewPopUp.action?id="+id+"&appsystem="+appsystem,
        				cache : false,
        				success : function(data) {
        					$(dialogEdit).html(data);
        				}
        			});
        		}
        	}
        	
        	function createExistAppraisalPopup() {
        		
        		var dialogEdit = '.modal-body';
            	$(dialogEdit).empty();
            	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
            	$(".modal-title").html('Exist My Reviews');
            	$("#modalInfo").show();
            	if($(window).width() >= 800){
            		$(".modal-dialog").width(800);
            	}
            	$.ajax({
        			url : "ExistMyReviewReportPopup.action",
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
                        url : "SetQuestionToTextfield.action?queid="+queid+'&count='+count,
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
                               //document.getElementById("statetitle").style.display = 'block';
                       		}
                       	}
                    });
                }
                $(dialogEdit).dialog('close');
        	}
        	
        	
        	var dialogEdit = '#showChoosePopupDiv';
        	function showChoosePopup(hideID, lblID, type) {
        		var hideIdValue = document.getElementById(hideID).value;
        		var dialogEdit = '#modal-body1';
            	$(dialogEdit).empty();
            	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
            	$(".modal-title1").html('Choose '+ type);
            	$("#modalInfo1").show();
            	$(".modal-dialog1").removeAttr("style");
            	$.ajax({
        			url : "ShowOrientationWiseEmpChoosePopup.action?hideID="+hideID+"&lblID="+lblID+"&type="+type+"&hideIdValue="+hideIdValue,
        			cache : false,
        			success : function(data) {
        				$(dialogEdit).html(data);
        			}
        		});
        	}
        	 
        
        	function showOrientData(value){
        		if(value=="1"){
        			 if(document.getElementById("hrDiv")) {
        				 document.getElementById("hrDiv").style.display = "block";
        			 }
        			
        			 if(document.getElementById("managerDiv")) {
        				 document.getElementById("managerDiv").style.display = "none";
        			 }
        			
        			 if(document.getElementById("peerDiv")) {
        				 document.getElementById("peerDiv").style.display = "none";
        			 }
        			
        			 if(document.getElementById("otherDiv")) {
        				 document.getElementById("otherDiv").style.display = "none";
        			 }
        			
        		} else if(value=="2"){
        			 if(document.getElementById("hrDiv")) {
        				document.getElementById("hrDiv").style.display = "block";
	       			 }
	       			
	       			 if(document.getElementById("managerDiv")) {
	       				document.getElementById("managerDiv").style.display = "block";
	       			 }
	       			
	       			 if(document.getElementById("peerDiv")) {
	       				document.getElementById("peerDiv").style.display = "none";
	       			 }
	       			
	       			 if(document.getElementById("otherDiv")) {
	       				document.getElementById("otherDiv").style.display = "none";
	       			 }
        			
        		} else if(value=="3"){
        			if(document.getElementById("hrDiv")) {
        				document.getElementById("hrDiv").style.display = "block";
	       			 }
	       			
	       			 if(document.getElementById("managerDiv")) {
	       				document.getElementById("managerDiv").style.display = "block";
	       			 }
	       			
	       			 if(document.getElementById("peerDiv")) {
	       				document.getElementById("peerDiv").style.display = "block";
	       			 }
	       			
	       			 if(document.getElementById("otherDiv")) {
	       				document.getElementById("otherDiv").style.display = "none";
	       			 }
        			
        			
        		} else if(value=="4"){
        			if(document.getElementById("hrDiv")) {
        				document.getElementById("hrDiv").style.display = "block";
	       			 }
	       			
	       			 if(document.getElementById("managerDiv")) {
	       				document.getElementById("managerDiv").style.display = "block";
	       			 }
	       			
	       			 if(document.getElementById("peerDiv")) {
	       				document.getElementById("peerDiv").style.display = "block";
	       			 }
	       			
	       			 if(document.getElementById("otherDiv")) {
	       				document.getElementById("otherDiv").style.display = "none";
	       			 }
        			
        		} else if(value=="5"){
        			if(document.getElementById("hrDiv")) {
        				document.getElementById("hrDiv").style.display = "none";
	       			 }
	       			
	       			 if(document.getElementById("managerDiv")) {
	       				document.getElementById("managerDiv").style.display = "none";
	       			 }
	       			
	       			 if(document.getElementById("peerDiv")) {
	       				document.getElementById("peerDiv").style.display = "none";
	       			 }
	       			
	       			 if(document.getElementById("otherDiv")) {
	       				document.getElementById("otherDiv").style.display = "block";
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
        	
        
        	function closePopup(){
        		$(dialogEdit).dialog('close');
        	}
        	
        	function showAnswerTypeDiv(ansType) {
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
        			}else if(parseFloat(value1) <= 0 ){
        				alert("Invalid Weightage");
        				document.getElementById(weightageid).value = remainweight;
        			}
        	}
        	
        	
        	
        	function validateScore1(value1, weightageid, remweightageid) {
        		//var weightCnt = document.getElementsByName(remweightageid);
        		//scoreCnt questionCnt objectiveCnt measureCnt goalCnt;
        		
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
        			}else if(parseFloat(value1) <= 0 ){
        				alert("Invalid Weightage");
        				document.getElementById(weightageid).value = remainweight;
        			}  
        	}
        	
        	
        	function validateSECANDSUBSECScore(value1,weightageid,weightagehideid) {
        		var remainWeightage = document.getElementById(weightagehideid).value;
        		  
        		  if(parseFloat(value1) > parseFloat(remainWeightage)){
        				alert("Entered value greater than Weightage");
        				document.getElementById(weightageid).value = remainWeightage;
        			}else if(parseFloat(value1) <= 0 ){
        				alert("Invalid Weightage");
        				document.getElementById(weightageid).value = remainWeightage;
        			}  
        		}
        	
        	
        	/* function validateScore(value1,weightageid,weightagehideid) {
        		var remainWeightage = document.getElementById(weightagehideid).value;
        		  
        		  if(parseFloat(value1) > parseFloat(remainWeightage)){
        				alert("Entered value greater than Weightage");
        				document.getElementById(weightageid).value = remainWeightage;
        			}else if(parseFloat(value1) <= 0 ){
        				alert("Invalid Weightage");
        				document.getElementById(weightageid).value = remainWeightage;
        			}  
        	} */
        
        	
        	function addNewSection() {
        		var hideSubSecTotWeight = document.getElementById("hideSubSecTotWeight").value;
        		var hideSecTotWeight = document.getElementById("hideSecTotWeight").value;
        		var sectionWeightage = document.getElementById("sectionWeightage").value;
        		var subSectionWeightage = document.getElementById("subSectionWeightage").value;
        		var appSystem = "";
        		if(document.getElementById("appraisalSystem")) {
        			appSystem = getSelectedValue("appraisalSystem");
        		}
        		
        		if(sectionWeightage == ""){
        			sectionWeightage=0;
        		}
        		if(subSectionWeightage == ""){
        			subSectionWeightage=0;
        		}
        		//alert("hideSubSecTotWeight = "+hideSubSecTotWeight+"  hideSecTotWeight = "+hideSecTotWeight+"  sectionWeightage = "+sectionWeightage+"  subSectionWeightage = "+subSectionWeightage);
        		var secRemainWeightage = 100 - (parseFloat(sectionWeightage)+parseFloat(hideSecTotWeight));
        		var subSecRemainWeightage = 100 - (parseFloat(subSectionWeightage)+parseFloat(hideSubSecTotWeight));
        	//	alert("secRemainWeightage = "+secRemainWeightage+"  subSecRemainWeightage = "+subSecRemainWeightage);
        		
        		 if(parseInt(secRemainWeightage) <= 0 && parseInt(subSecRemainWeightage) <= 0){
        			//alert("1");
        			 document.getElementById("firstdiv").style.display = 'none';
        			 document.getElementById("seconddiv").style.display = 'none';
        			 document.getElementById("secWeightdiv").style.display = 'none';
        			 document.getElementById("subSecWeightdiv").style.display = 'none';
        			 document.getElementById("subSecSecWeightdiv").style.display = 'block';
        		 }else if(parseInt(secRemainWeightage) <= 0 && parseInt(subSecRemainWeightage) > 0){
        			// alert("2");
        			 document.getElementById("secWeightdiv").style.display = 'block';
        			 document.getElementById("firstdiv").style.display = 'none';
        			
        			
        			 document.getElementById("seconddiv").style.display = 'none';
        			 document.getElementById("subSecWeightdiv").style.display = 'none';
        			 document.getElementById("subSecSecWeightdiv").style.display = 'none';
        		 }else if(parseInt(secRemainWeightage) > 0 && parseInt(subSecRemainWeightage) <= 0){
        			// alert("3");
        			 document.getElementById("seconddiv").style.display = 'none';
        			 document.getElementById("secWeightdiv").style.display = 'none';
        			 document.getElementById("subSecWeightdiv").style.display = 'block';
        			 document.getElementById("firstdiv").style.display = 'none';
        			 document.getElementById("subSecSecWeightdiv").style.display = 'none';
        		 }else {
        			// alert("4");
        			 document.getElementById("seconddiv").style.display = 'block';
        			 document.getElementById("firstdiv").style.display = 'none';
        			 document.getElementById("firstdiv").style.display = 'none';
        			// document.getElementById("seconddiv").style.display = 'block';
        			 document.getElementById("secWeightdiv").style.display = 'none';
        			 document.getElementById("subSecWeightdiv").style.display = 'none';
        			 document.getElementById("subSecSecWeightdiv").style.display = 'none';
        		 }
        	}
        	
        	function onloadAddNewSection() {
        		var hideSubSecTotWeight = document.getElementById("hideSubSecTotWeight").value;
        		var hideSecTotWeight = document.getElementById("hideSecTotWeight").value;
        		var sectionWeightage = document.getElementById("sectionWeightage").value;
        		var subSectionWeightage = document.getElementById("subSectionWeightage").value;
        		var appSystem = "";
        		if(document.getElementById("appraisalSystem")) {
        			appSystem = getSelectedValue("appraisalSystem");
        		}
        		
        		if(sectionWeightage == ""){
        			sectionWeightage=0;
        		}
        		if(subSectionWeightage == ""){
        			subSectionWeightage=0;
        		}
        		//alert("hideSubSecTotWeight = "+hideSubSecTotWeight+"  hideSecTotWeight = "+hideSecTotWeight+"  sectionWeightage = "+sectionWeightage+"  subSectionWeightage = "+subSectionWeightage);
        		var secRemainWeightage = 100 - (parseFloat(sectionWeightage)+parseFloat(hideSecTotWeight));
        		var subSecRemainWeightage = 100 - (parseFloat(subSectionWeightage)+parseFloat(hideSubSecTotWeight));
        	//	alert("secRemainWeightage = "+secRemainWeightage+"  subSecRemainWeightage = "+subSecRemainWeightage);
        		
        		 if(parseInt(secRemainWeightage) <= 0 && parseInt(subSecRemainWeightage) <= 0){
        			//alert("1");
        			 document.getElementById("firstdiv").style.display = 'none';
        			 document.getElementById("seconddiv").style.display = 'none';
        			 document.getElementById("secWeightdiv").style.display = 'none';
        			 document.getElementById("subSecWeightdiv").style.display = 'none';
        			 document.getElementById("subSecSecWeightdiv").style.display = 'block';
        		 }else if(parseInt(secRemainWeightage) <= 0 && parseInt(subSecRemainWeightage) > 0){
        			 //alert("2");
        			 document.getElementById("secWeightdiv").style.display = 'block';
        			 document.getElementById("firstdiv").style.display = 'none';
        			
        			
        			 document.getElementById("seconddiv").style.display = 'none';
        			 document.getElementById("subSecWeightdiv").style.display = 'none';
        			 document.getElementById("subSecSecWeightdiv").style.display = 'none';
        		 }else if(parseInt(secRemainWeightage) > 0 && parseInt(subSecRemainWeightage) <= 0){
        			//alert("3");
        			 document.getElementById("seconddiv").style.display = 'none';
        			 document.getElementById("secWeightdiv").style.display = 'none';
        			 document.getElementById("subSecWeightdiv").style.display = 'block';
        			 document.getElementById("firstdiv").style.display = 'none';
        			 document.getElementById("subSecSecWeightdiv").style.display = 'none';
        		 }else {
        			//alert("4");
        			 document.getElementById("seconddiv").style.display = 'block';
        			 document.getElementById("firstdiv").style.display = 'none';
        			 document.getElementById("firstdiv").style.display = 'none';
        			// document.getElementById("seconddiv").style.display = 'block';
        			 document.getElementById("secWeightdiv").style.display = 'none';
        			 document.getElementById("subSecWeightdiv").style.display = 'none';
        			 document.getElementById("subSecSecWeightdiv").style.display = 'none';
        		 }
        	}
        	
        	
        	function closeForm() {
        		 $("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
          	 	$.ajax({ 
          		   url: "KRATarget.action?fromPage=MyHR",
          		   cache: true,
          		    success: function(result){
          			  //alert("result1==>"+result);
          			  $("#divMyHRData").html(result);
             	    }
          	    });
        	}
        	
        	function showMembersSelect(orientation) {
        		//alert("orientation==>"+orientation);
                xmlhttp = GetXmlHttpObject();
                if (xmlhttp == null) {
                        alert("Browser does not support HTTP Request");
                        return;
                } else {
                        var xhr = $.ajax({
                                url : "GetMembersByOrientation.action?orientation_id="+orientation,
                                cache : false,
                                success : function(data) {
                                	//alert("data==>"+data);
                                	document.getElementById("td_choose_members").innerHTML = data;
                                }
                        });
                 }
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
    </script>
</g:compress>
<%
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    UtilityFunctions uF = new UtilityFunctions();
    Map<String, String> orientPosition = (Map<String, String>)request.getAttribute("orientPosition");
    	String attribute = (String) request.getAttribute("attribute");
    String anstype = (String) request.getAttribute("anstype");
    String id = (String) request.getAttribute("id");
    String step = (String) request.getAttribute("step");
    
    Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
    if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
    String policy_id = (String) request.getAttribute("policy_id");
    %>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Review Form" name="title" />
    </jsp:include> --%>
<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
				<div class="steps" style="font-size: 14px;">
					<s:if test="id == null">
						<span class="current"> Review Details: &nbsp;&nbsp;</span>
						<span class="next"> Review System: </span>
					</s:if>
					<s:if test="id != null && step != null && step == 2">
						<span class="next"> Review Details: &nbsp;&nbsp;</span>
						<span class="current"> Review System: </span>
					</s:if>
				</div>
			</h3>
			<div class="box-tools pull-right">
				<a href="javascript:void(0);" title="Close Review Form" onclick="closeForm();" class="close-font" style="margin-right: 20px;"></a>
			</div>
		</div>
		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			<div class="reportWidth">
				<s:form action="CreateMyReview" id="formID" name="formID"
					method="POST" theme="simple">
					<s:if test="id == null">
						<s:hidden name="step"></s:hidden>
						<div class="table-responsive">
							<table class="table table_no_border col-sm-12 cf">
								<tr>
									<th width="15%" style="text-align: right">Review Name:<sup>*</sup>
									</th>
									<td colspan="6"><input type="hidden" name="policy_id"
										id="policy_id"
										value="<%=(String)request.getAttribute("policy_id") %>" /> <s:textfield
											cssClass="validateRequired form-control" name="appraiselName"
											cssStyle="width: 50%;"></s:textfield> <!-- <input type="text" class="validateRequired form-control" name="appraiselName" style="width: 50%;"/> -->
										<span style="float: right;"> <a
											href="javascript: void(0)"
											onclick="createExistAppraisalPopup();">Choose from
												Existing My Review</a> </span></td>
								</tr>
								<tr>
									<th style="text-align: right">Review Type:<sup>*</sup>
									</th>
									<td colspan="6"><s:select theme="simple"
											cssClass="validateRequired form-control" name="appraisalType"
											headerKey="Self Review" headerValue="Self Review" list="#{}" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right" valign="top">Description:</th>
									<td colspan="6"><textarea rows="3" cols="72"
											name="appraisal_description"></textarea> <!-- id="editor2" -->
									</td>
								</tr>
								<tr>
									<th style="text-align: right" valign="top">Instructions:</th>
									<td colspan="6"><textarea rows="3" cols="72"
											name="appraisal_instruction"></textarea> <!-- id="editor1" -->
										<s:hidden name="frequency" value="1"></s:hidden></td>
								</tr>
								<tr>
									<th style="text-align: right">Start Date:<sup>*</sup>
									</th>
									<td><input type="text" name="from" id="from"
										class="validateRequired" />
									</td>
									<th style="text-align: right">End Date:<sup>*</sup>
									</th>
									<td colspan="4"><input type="text" name="to" id="to"
										class="validateRequired" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right">Orientation Type:</th>
									<td colspan="6"><s:select theme="simple" name="oreinted" id="oreinted" list="orientationList" 
										listKey="id" listValue="name" value="5" 
										onchange="createRevieweePanelForReview();" /> <span<%-- showMembersSelect(this.value); --%>
										style="float: right;" id="spanOreint"></span></td>
										
										
								</tr>								
								<tr>
									<th style="text-align: right">Appraiser:</th>
									<%-- <td colspan="6">
										<div id="td_choose_members">
											<input type="hidden" name="hideotherId" id="hideotherId" />
											<div id="otherDiv"
												style="display: block; float: left; width: 100%;">
												<!-- <a href="javascript: void(0);"
													onclick="showChoosePopup('hideotherId','lblOtherid','Other');">Choose
													Anyone</a>:&nbsp;<label id="lblOtherid">Not Choosen</label> -->
													
											</div>
										</div></td> --%>
										<!-- Created by dattatray -->
										<td colspan="6">
											<div id="divAppraiser"> Please select reviewee. </div>
										</td>
								</tr>
								<tr>
									<th style="text-align: right">Reviewer:</th>
									<td colspan="6"><s:select name="reviewerId"
											cssClass="form-control" list="reviewerList" theme="simple"
											listKey="employeeId" id="reviewerId" listValue="employeeCode"
											multiple="true" size="4" /></td>
								</tr>
								<tr class="graybackground">
									<th style="text-align: right">Review publishing workflow:</th>
									<th colspan="6" style="text-align: left"></th>
								</tr>
								<%
                                            if(uF.parseToBoolean(CF.getIsWorkFlow())) {		
                                              		if(hmMemberOption!=null && !hmMemberOption.isEmpty() ) {
                                            		Iterator<String> it1=hmMemberOption.keySet().iterator();
                                            		while(it1.hasNext()) {
                                            			String memPosition=it1.next();
                                            			String optiontr=hmMemberOption.get(memPosition);					
                                            			out.println(optiontr); 
                                            		}
                                              		} else {	
                                            %>
								<tr>
									<th style="text-align: right">&nbsp;</th>
									<td colspan="6">Your work flow is not defined. Please,
										speak to your hr for your work flow.</td>
								</tr>
								<% } %>
								<%	} %>
							</table>
						</div>
					</s:if>
					<s:if test="id != null && step != null && step == 2">
						<%-- <div class="steps"> 
                                    <span class="prev"> Review Details : &nbsp;&nbsp;</span>
                                    <span class="current" > Review System : </span>
                                </div> --%>
						<%
                                    System.out.println("Step Count ---> "+ request.getAttribute("step"));
                                     List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
                                     List<String> appTiltleList = (List<String>) request.getAttribute("appTiltleList");
                                     
                                     if(appraisalList == null) appraisalList = new ArrayList<String>();
                                     if(appTiltleList == null) appTiltleList = new ArrayList<String>();
                                     
                         %>
                         <%if(appraisalList != null && !appraisalList.isEmpty() && appraisalList.size() > 0) { %>
						<!-- Start Dattatray -->
						<!--  <div id="profilecontainer" class="clr row row_without_margin margintop20">
										<div class="col-lg-12"> -->
						<div class="box box-default" style="border-top: 3px solid #d2d6de;">
							<div class="box-header with-border">
								<h3 class="box-title"><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=appraisalList.get(5)%>&nbsp;&nbsp;<%=appraisalList.get(9)%>&nbsp;&nbsp;<%=appraisalList.get(8)%></h3>
							</div>
							<!-- /.box-header -->
							<div class="box-body" style="padding: 5px; overflow-y: auto;">

								<div class="con2">
									<div class="holder">

										<div class="col-lg-12 col-md-12 col-sm-12"
											style="margin-top: 1%;">
											<div style="float: left; width: 100%;">
												<span
													style="float: left; font-size: 12px; line-height: 32px;">
													<span title="Review Type"><%=appraisalList.get(14)%>,</span>
													<span title="Frequency"><%=appraisalList.get(7)%>,</span>

													<span title="Effective Date"><%=appraisalList.get(17)%>,</span>
													<span title="Due Date"><%=appraisalList.get(18)%>,</span> <span
													title="Orientation"><%=appraisalList.get(2)%></span> </span>
											</div>

											<div style="float: left; width: 100%;">
												<span
													style="float: left; font-size: 12px; line-height: 32px;"><b>Description:&nbsp;&nbsp;
												</b><span class="description"><%=appraisalList.get(15)%></span>
												</span>
											</div>

											<div style="float: left; width: 100%;">
												<span
													style="float: left; font-size: 12px; line-height: 32px;"><b>Instruction:&nbsp;&nbsp;
												</b><span class="instruction"><%=appraisalList.get(16)%></span>
												</span>
											</div>

										</div>
									</div>
								</div>
							</div>

						</div>
						<div class="box box-primary" style="border-top: 3px solid #d2d6de;">
							<div class="box-header with-border">
								<h4 class="box-title">Reviewers & Reviewee</h4>
							</div>

							<div class="box-body" style="padding: 5px; overflow-y: auto;">
								<div class="row row_without_margin">
									<div class="col-lg-12 col-md-12 col-sm-12">
										<div style="float: left; width: 100%;">
											<span style="float: left; font-size: 12px; line-height: 32px;"><b>Reviewee:&nbsp;&nbsp;&nbsp;&nbsp;</b></span>
											<div class="col-sm-offset-1">
												<span style="float: left; font-size: 12px; line-height: 35px;"><%=appraisalList.get(12)%></span>
											</div>
										</div>
										<div style="float: left; width: 100%;">
											<span style=" font-size: 12px; line-height: 35px;"><b>Appraiser:&nbsp;&nbsp; </b><a href="javascript:void(0);" onclick="getRevieweeAppraisers('<%=appraisalList.get(0)%>', '', '');">Click Here</a></span>
										</div>
										<div style="float: left; width: 100%;">
											<span
												style="float: left; font-size: 12px; line-height: 35px;"><b>Reviewer:&nbsp;&nbsp;&nbsp;&nbsp;</b><%=appraisalList.get(25)%></span>
										</div>
										<div style="float: left; width: 100%;">
											<span
												style="float: left; font-size: 12px; line-height: 35px;"><b>Review
													publishing workflow:&nbsp;</b>
											</span>
										</div>
										<%=(String)request.getAttribute("sbWorkflow1") %>
									</div>
								</div>
							</div>
						</div>
						<!-- End Dattatray -->
						<!-- </div>
								</div> -->
						<!-- Original View -->
						<%-- 	<div class="box box-default collapsed-box" style="margin-top: 10px;border-top: 2px solid #d2d6de;">
						                <div class="box-header with-border">
						                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=uF.showData(appraisalList.get(5),"")%>&nbsp;&nbsp;<%=uF.showData(appraisalList.get(9),"")%>&nbsp;&nbsp;<%=uF.showData(appraisalList.get(8),"") %></h3>
						                    <div class="box-tools pull-right">
						                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						                    </div>
						                </div>
						                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
						                    <table class="table">
                                                <tr>
                                                    <th width="15%" align="right">Review Type:</th>
                                                    <td><%=appraisalList.get(14)%></td>
                                                </tr>
                                                <tr>
                                                    <th valign="top" align="right">Description:</th>
                                                    <td colspan="1"><%=appraisalList.get(15)%></td>
                                                </tr>
                                                <tr>
                                                    <th valign="top" align="right">Instruction:</th>
                                                    <td colspan="1"><%=appraisalList.get(16)%></td>
                                                </tr>
                                                <tr>
                                                    <th align="right">Frequency:</th>
                                                    <td><%=appraisalList.get(7)%></td>
                                                </tr>
                                                <tr>
                                                    <th align="right">Effective Date:</th>
                                                    <td><%=appraisalList.get(17)%></td>
                                                </tr>
                                                <tr>
                                                    <th align="right">Due Date:</th>
                                                    <td><%=appraisalList.get(18)%></td>
                                                </tr>
                                                <tr>
                                                    <th valign="top" align="right">Reviewee:</th>
                                                    <td colspan="1"><%=appraisalList.get(12)%></td>
                                                </tr>
                                                <tr>
                                                    <th align="right">Orientation:</th>
                                                    <td colspan="1"><%=appraisalList.get(2)%></td>
                                                </tr>
                                                <tr>
                                                    <th align="right">Appraiser:</th>
                                                    <td colspan="1"><%=appraisalList.get(26)%></td>
                                                </tr>
                                                <tr>
                                                    <th align="right">Reviewer:</th>
                                                    <td colspan="1"><%=appraisalList.get(25)%></td>
                                                </tr>
                                                <tr class="graybackground">
                                                    <th align="right">Review publishing workflow:</th>
                                                    <td colspan="1">&nbsp;</td>
                                                </tr>
                                                <%=(String)request.getAttribute("sbWorkflow") %>
                                            </table>
						                </div>
						            </div> --%>


						<%} %>
						<%
                                    int sectioncnt1=0,subsectioncnt1=0;
                                    List<List<String>> mainLevelList = (List<List<String>>) request.getAttribute("mainLevelList");
                                    Map<String, List<List<String>>> hmSystemLevelMp =(Map<String, List<List<String>>>)request.getAttribute("hmSystemLevelMp");
                                    Map<String, List<Map<String, List<List<String>>>>> levelMp = (Map<String, List<Map<String, List<List<String>>>>>) request.getAttribute("levelMp");
                                    
                                    int sectionTotWeightage = 0, subSectionTotWeightage = 0;
                                    for (int a = 0; mainLevelList != null && a < mainLevelList.size(); a++) {
                                    List<String> maininnerList = mainLevelList.get(a);
                                    subsectioncnt1=1;
                                              	sectioncnt1++;
                                              	subSectionTotWeightage = 0;
                                              	sectionTotWeightage += uF.parseToInt(maininnerList.get(5));
                                              	List<List<String>> outerList1 =hmSystemLevelMp.get(maininnerList.get(0));
                                              	for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
                                                  	subsectioncnt1++;
                                                  	List<String> innerList1 = outerList1.get(i);
                                                  	subSectionTotWeightage += uF.parseToInt(innerList1.get(7));
                                              	}
                                              	
                                    }
                                    %>
						<%if(request.getAttribute("mainlevelTitle")==null){	%>
						<ul class="list-unstyled">
							<%if(mainLevelList.size() != 0){ %><li><img
								src="images1/bottom-right-arrow.png"
								style="width: 20px; vertical-align: super; opacity: 0.6;">
								<span style="font-size: 16px; font-weight: 600;">Sections</span>
							</li>
							<%} %>
							<%
                                    for (int a = 0; mainLevelList != null && a < mainLevelList.size(); a++) {
                                    	List<String> maininnerList = mainLevelList.get(a);
                                    %>
							<li><div class="box box-default collapsed-box autoWidth"
									style="margin-top: 10px; border-top: 2px solid #d2d6de;">
									<div class="box-header with-border">
										<h3 class="box-title"
											style="font-size: 14px; padding-right: 10px;"><%=maininnerList.get(1)%></h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-plus"></i>
											</button>
											<button class="btn btn-box-tool" data-widget="remove">
												<i class="fa fa-times"></i>
											</button>
										</div>
									</div>
									<!-- /.box-header -->
									<div class="box-body"
										style="padding: 5px; overflow-y: auto; display: none;">
										<%	
                                                    List<List<String>> outerList1 =hmSystemLevelMp.get(maininnerList.get(0));
                                                            	int jj=0;
                                                                for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
                                                                 	List<String> innerList1 = outerList1.get(i);
                                                                    if (uF.parseToInt(innerList1.get(3)) == 2) {
                                                                        List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                                                        Map<String, List<List<String>>> scoreMp = list.get(0);
                                                            	   %>
										<div
											style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
											<div style="text-align: left;">
												<blockquote>
													<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=uF.showData(innerList1.get(1),"") %>
													</strong>
												</blockquote>
											</div>
											<div style="overflow: hidden; float: left; width: 100%;">
												<table class="table" style="width: 100%; float: left;">
													<tr>
														<td width="90%"><b>Question</b>
														</td>
														<td><b>Weightage</b>
														</td>
													</tr>
													<%
                                                                List<List<String>> goalList = scoreMp.get(innerList1.get(0));
                                                                for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                                	List<String> goalinnerList = goalList.get(k);
                                                                %>
													<tr>
														<td><%=goalinnerList.get(0)%></td>
														<td style="text-align: right"><%=goalinnerList.get(1)%>%</td>
													</tr>
													<%  }%>
												</table>
											</div>
										</div>
										<%
                                                    } else {
                                                    
                                                        if (uF.parseToInt(innerList1.get(2)) == 1) {
                                                            List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                                            Map<String, List<List<String>>> scoreMp = list.get(0);
                                                            Map<String, List<List<String>>> measureMp = list.get(1);
                                                            Map<String, List<List<String>>> questionMp = list.get(2);
                                                            Map<String, List<List<String>>> GoalMp = list.get(3);
                                                            Map<String, List<List<String>>> objectiveMp = list.get(4);
                                                    %>
										<div
											style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
											<div style="text-align: left;">
												<%-- <blockquote><strong>Competencies + Goals + Objectives + Measures</strong></blockquote> --%>
												<blockquote>
													<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=uF.showData(innerList1.get(1),"") %></strong>
												</blockquote>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Competency</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 7%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Goal </b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 7%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Objective </b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 7%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 7%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Measure</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 6.9%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14.5%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Question</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 6.9%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; width: 100%; border: 1px solid #000000">
												<%
                                                            List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
                                                                for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                                    List<String> innerList = scoreList.get(j);
                                                            %>
												<div style="overflow: hidden; float: left; width: 100%;">
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 14.2%;">
														<center>
															<p><%=innerList.get(1)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 7.1%;">
														<center>
															<p><%=innerList.get(2)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 78.6%;">
														<%
                                                                    List<List<String>> goalList = GoalMp.get(innerList.get(0));
                                                                       for(int k = 0; goalList != null && k < goalList.size(); k++) {
                                                                           List<String> goalinnerList = goalList.get(k);
                                                                    %>
														<div style="overflow: hidden; float: left; width: 100%;">
															<div
																style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 18%;">
																<center>
																	<p><%=goalinnerList.get(1)%></p>
																</center>
															</div>
															<div
																style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 9.1%;">
																<center>
																	<p><%=goalinnerList.get(2)%></p>
																</center>
															</div>
															<div
																style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 72.7%;">
																<%
                                                                            List<List<String>> objectiveList = objectiveMp.get(goalinnerList.get(0));
                                                                            for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
                                                                            List<String> objectivelinnerList = objectiveList.get(l);
                                                                            %>
																<div style="overflow: hidden; float: left; width: 100%;">
																	<div
																		style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 24.6%;">
																		<center>
																			<p><%=objectivelinnerList.get(1)%></p>
																		</center>
																	</div>
																	<div
																		style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 12.5%;">
																		<center>
																			<p><%=objectivelinnerList.get(2)%></p>
																		</center>
																	</div>
																	<div
																		style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 62.7%;">
																		<%
                                                                                    List<List<String>> measureList = measureMp.get(objectivelinnerList.get(0));
                                                                                        for (int m = 0; measureList != null && m < measureList.size(); m++) {
                                                                                            List<String> measureinnerList = measureList.get(m);
                                                                                    %>
																		<div
																			style="overflow: hidden; float: left; width: 100%;">
																			<div
																				style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 19.7%;">
																				<center>
																					<p><%=measureinnerList.get(1)%></p>
																				</center>
																			</div>
																			<div
																				style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 19.5%;">
																				<center>
																					<p><%=measureinnerList.get(2)%></p>
																				</center>
																			</div>
																			<div
																				style="overflow: hidden; float: left; width: 60.6%;">
																				<%
                                                                                            List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
                                                                                                for (int n = 0; questionList != null && n < questionList.size(); n++) {
                                                                                                    List<String> question1List = questionList.get(n);
                                                                                            %>
																				<div
																					style="overflow: hidden; float: left; width: 100%;">
																					<div
																						style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 67%;">
																						<center>
																							<p><%=question1List.get(0)%></p>
																						</center>
																					</div>
																					<div
																						style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 32.1%;">
																						<center>
																							<p><%=question1List.get(1)%></p>
																						</center>
																					</div>
																				</div>
																				<% } %>
																			</div>
																		</div>
																		<% } %>
																	</div>
																</div>
																<% } %>
															</div>
														</div>
														<% } %>
													</div>
												</div>
												<% } %>
											</div>
										</div>
										<%
                                                    } else if (uF.parseToInt(innerList1.get(2)) == 2) {
                                                         List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                                         Map<String, List<List<String>>> scoreMp = list.get(0);
                                                         Map<String, List<List<String>>> measureMp = list.get(1);
                                                         Map<String, List<List<String>>> questionMp = list.get(2);
                                                    %>
										<div
											style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
											<div style="text-align: left;">
												<%-- <blockquote><strong>Competencies + Measures</strong></blockquote> --%>
												<blockquote>
													<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=uF.showData(innerList1.get(1),"") %></strong>
												</blockquote>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 22.9%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Competency</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Competency Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 13.4%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Measure</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 13.2%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 22.9%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Question</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 12.6%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Question Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; width: 100%; border: 1px solid #000000">
												<%
                                                            List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
                                                            for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                                 List<String> innerList = scoreList.get(j);
                                                            %>
												<div style="overflow: hidden; float: left; width: 100%;">
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 23.1%;">
														<center>
															<p><%=innerList.get(1)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 14.1%;">
														<center>
															<p><%=innerList.get(2)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 62.7%;">
														<%
                                                                    List<List<String>> measureList = measureMp.get(innerList.get(0));
                                                                        for (int k = 0; measureList != null && k < measureList.size(); k++) {
                                                                           List<String> measureinnerList = measureList.get(k);
                                                                    %>
														<div
															style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 21.4%;">
															<center>
																<p><%=measureinnerList.get(1)%></p>
															</center>
														</div>
														<div
															style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 21.2%;">
															<center>
																<p><%=measureinnerList.get(2)%></p>
															</center>
														</div>
														<div
															style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 57.1%;">
															<%
                                                                        List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
                                                                                                for (int l = 0; questionList != null && l < questionList.size(); l++) {
                                                                                                    List<String> question1List = questionList.get(l);
                                                                        %>
															<div style="overflow: hidden; float: left; width: 100%;">
																<div
																	style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 64.2%;">
																	<center>
																		<p><%=question1List.get(0)%></p>
																	</center>
																</div>
																<div
																	style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 35.4%;">
																	<center>
																		<p><%=question1List.get(1)%></p>
																	</center>
																</div>
															</div>
															<%  } %>
														</div>
														<% } %>
													</div>
												</div>
												<% } %>
											</div>
										</div>
										<%
                                                    } else  if (uF.parseToInt(innerList1.get(2)) == 3) {
                                                                    List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                                                    Map<String, List<List<String>>> scoreMp = list.get(0);
                                                                    Map<String, List<List<String>>> measureMp = list.get(1);
                                                                    Map<String, List<List<String>>> questionMp = list.get(2);
                                                                    Map<String, List<List<String>>> GoalMp = list.get(3);
                                                    %>
										<div
											style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
											<div style="text-align: left;">
												<%-- <blockquote><strong>Competencies + Goals + Measures</strong></blockquote> --%>
												<blockquote>
													<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=uF.showData(innerList1.get(1),"") %></strong>
												</blockquote>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Competency</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 10%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 16.5%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Goal </b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 10%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Goal Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 8.3%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Measure</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 8.1%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; background-color: rgb(255, 255, 255); border: 1px solid rgb(0, 0, 0); width: 19%; text-align: center;">
												<b>Question</b>
											</div>
											<div
												style="overflow: hidden; float: left; background-color: rgb(255, 255, 255); border: 1px solid rgb(0, 0, 0); width: 12.7%; text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 100%;">
												<%
                                                            List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
                                                            for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                                List<String> innerList = scoreList.get(j);
                                                            %>
												<div style="overflow: hidden; float: left; width: 100%;">
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 10%;">
														<center>
															<p><%=innerList.get(1)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 14.2%;">
														<center>
															<p><%=innerList.get(2)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 75.6%;">
														<%
                                                                    List<List<String>> goalList = GoalMp.get(innerList.get(0));
                                                                    for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                                        List<String> goalinnerList = goalList.get(k);
                                                                    %>
														<div style="overflow: hidden; float: left; width: 100%;">
															<div
																style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 22.1%;">
																<center>
																	<p><%=goalinnerList.get(1)%></p>
																</center>
															</div>
															<div
																style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 13.3%;">
																<center>
																	<p><%=goalinnerList.get(2)%></p>
																</center>
															</div>
															<div
																style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 64.4%;">
																<%
                                                                            List<List<String>> measureList = measureMp.get(goalinnerList.get(0));
                                                                            for (int l = 0; measureList != null && l < measureList.size(); l++) {
                                                                                 List<String> measureinnerList = measureList.get(l);
                                                                            %>
																<div style="overflow: hidden; float: left; width: 100%;">
																	<div
																		style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 17%;">
																		<center>
																			<p><%=measureinnerList.get(1)%></p>
																		</center>
																	</div>
																	<div
																		style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 17%;">
																		<center>
																			<p><%=measureinnerList.get(2)%></p>
																		</center>
																	</div>
																	<div
																		style="overflow: hidden; float: left; width: 65.8%;">
																		<%
                                                                                    List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
                                                                                    for (int m = 0; questionList != null && m < questionList.size(); m++) {
                                                                                         List<String> question1List = questionList.get(m);
                                                                                    %>
																		<div
																			style="overflow: hidden; float: left; width: 100%;">
																			<div
																				style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 59.6%;">
																				<center>
																					<p><%=question1List.get(0)%></p>
																				</center>
																			</div>
																			<div
																				style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 39.8%;">
																				<center>
																					<p><%=question1List.get(1)%></p>
																				</center>
																			</div>
																		</div>
																		<%  } %>
																	</div>
																</div>
																<%  } %>
															</div>
														</div>
														<% }%>
													</div>
												</div>
												<% } %>
											</div>
										</div>
										<% } 
                                                    }  %>
										<%    } %>
									</div>
									<!-- /.box-body -->
								</div></li>
							<%} %>
						</ul>
						<%} %>

						<%if(request.getAttribute("mainlevelTitle")!=null) { %>
						<ul class="list-unstyled">
							<%if(mainLevelList.size() != 0){ %>
							<li><img
								src="https://d30y9cdsu7xlg0.cloudfront.net/png/577227-200.png"
								style="width: 20px; vertical-align: super; opacity: 0.6;">
								<span style="font-size: 16px; font-weight: 600;">Sections</span>
							</li>
							<%} %>
							<%for (int a = 0; mainLevelList != null && a < mainLevelList.size(); a++) {
                                    List<String> maininnerList = mainLevelList.get(a);
                                    %>
							<li><div class="box box-default collapsed-box"
									style="margin-top: 10px; border-top: 2px solid #d2d6de;">
									<div class="box-header with-border">
										<h3 class="box-title"
											style="font-size: 14px; padding-right: 10px;"><%=maininnerList.get(1)%></h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-plus"></i>
											</button>
											<button class="btn btn-box-tool" data-widget="remove">
												<i class="fa fa-times"></i>
											</button>
										</div>
									</div>
									<!-- /.box-header -->
									<div class="box-body"
										style="padding: 5px; overflow-y: auto; display: none;">
										<%	
                                                    List<List<String>> outerList1 =hmSystemLevelMp.get(maininnerList.get(0));
                                                    int jj=0;
                                                       for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
                                                       	List<String> innerList1 = outerList1.get(i);
                                                       	if (uF.parseToInt(innerList1.get(3)) == 2) {
                                                              List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                                              Map<String, List<List<String>>> scoreMp = list.get(0);
                                                    %>
										<div
											style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
											<div style="text-align: left;">
												<%-- <blockquote><strong>Other</strong></blockquote> --%>
												<blockquote>
													<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=uF.showData(innerList1.get(1),"") %></strong>
												</blockquote>
											</div>
											<div style="overflow: hidden; float: left; width: 100%;">
												<table class="table" style="width: 100%; float: left;">
													<tr>
														<td width="90%"><b>Question</b>
														</td>
														<td><b>Weightage</b>
														</td>
													</tr>
													<%
                                                                List<List<String>> goalList = scoreMp.get(innerList1.get(0));
                                                                			for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                                				List<String> goalinnerList = goalList.get(k);
                                                                %>
													<tr>
														<td><%=goalinnerList.get(0)%></td>
														<td style="text-align: right"><%=goalinnerList.get(1)%>%</td>
													</tr>
													<%
                                                                }
                                                                %>
												</table>
											</div>
										</div>
										<%
                                                    } else {
                                                    
                                                                if (uF.parseToInt(innerList1.get(2)) == 1) {
                                                                    List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                                                    Map<String, List<List<String>>> scoreMp = list.get(0);
                                                                    Map<String, List<List<String>>> measureMp = list.get(1);
                                                                    Map<String, List<List<String>>> questionMp = list.get(2);
                                                                    Map<String, List<List<String>>> GoalMp = list.get(3);
                                                                    Map<String, List<List<String>>> objectiveMp = list.get(4);
                                                    %>
										<%--  <div
                                                    style="overflow: hidden;float: left; border: 1px solid rgb(0,0,0); width: 100%; background-color: rgb(192, 192, 192);text-align: left; font-size: 20px; font-weight: bold;"><%=innerList1.get(1)%>
                                                    <br/>
                                                    <%=innerList1.get(4)%>
                                                    </div> --%>
										<div
											style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
											<div style="text-align: left;">
												<%-- <blockquote><strong>Competencies + Goals + Objectives + Measures</strong></blockquote> --%>
												<blockquote>
													<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=uF.showData(innerList1.get(1),"") %></strong>
												</blockquote>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Competency</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 7%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Goal </b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 7%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Objective </b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 7%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 7%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Measure</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 6.9%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14.5%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Question</b>
												<div id="div_Reviewer">
													Reviewer:
													<s:select name="reviewerId" cssClass="form-control "
														list="reviewerList" theme="simple" listKey="employeeId"
														id="reviewerId" listValue="employeeCode" multiple="true"
														size="4" />
													<!-- <input type="hidden" name="hideReviewerId" id="hideReviewerId" /> -->
												</div>

											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 6.9%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; width: 100%; border: 1px solid #000000">
												<%
                                                            List<List<String>> scoreList = scoreMp
                                                                                    .get(innerList1.get(0));
                                                                            for (int j = 0; scoreList != null
                                                                                    && j < scoreList.size(); j++) {
                                                            
                                                                                List<String> innerList = scoreList.get(j);
                                                            %>
												<div style="overflow: hidden; float: left; width: 100%;">
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 14.2%;">
														<center>
															<p><%=innerList.get(1)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 7.1%;">
														<center>
															<p><%=innerList.get(2)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 78.6%;">
														<%
                                                                    List<List<String>> goalList = GoalMp
                                                                                                .get(innerList.get(0));
                                                                                        for (int k = 0; goalList != null
                                                                                                && k < goalList.size(); k++) {
                                                                                            List<String> goalinnerList = goalList
                                                                                                    .get(k);
                                                                    %>
														<div style="overflow: hidden; float: left; width: 100%;">
															<div
																style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 18%;">
																<center>
																	<p><%=goalinnerList.get(1)%></p>
																</center>
															</div>
															<div
																style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 9.1%;">
																<center>
																	<p><%=goalinnerList.get(2)%></p>
																</center>
															</div>
															<div
																style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 72.7%;">
																<%
                                                                            List<List<String>> objectiveList = objectiveMp
                                                                                                            .get(goalinnerList.get(0));
                                                                                                    for (int l = 0; objectiveList != null
                                                                                                            && l < objectiveList.size(); l++) {
                                                                                                        List<String> objectivelinnerList = objectiveList
                                                                                                                .get(l);
                                                                            %>
																<div style="overflow: hidden; float: left; width: 100%;">
																	<div
																		style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 24.6%;">
																		<center>
																			<p><%=objectivelinnerList.get(1)%></p>
																		</center>
																	</div>
																	<div
																		style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 12.5%;">
																		<center>
																			<p><%=objectivelinnerList.get(2)%></p>
																		</center>
																	</div>
																	<div
																		style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 62.7%;">
																		<%
                                                                                    List<List<String>> measureList = measureMp.get(objectivelinnerList.get(0));
                                                                                                                for (int m = 0; measureList != null && m < measureList.size(); m++) {
                                                                                                                    List<String> measureinnerList = measureList.get(m);
                                                                                    %>
																		<div
																			style="overflow: hidden; float: left; width: 100%;">
																			<div
																				style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 19.7%;">
																				<center>
																					<p><%=measureinnerList.get(1)%></p>
																				</center>
																			</div>
																			<div
																				style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 19.5%;">
																				<center>
																					<p><%=measureinnerList.get(2)%></p>
																				</center>
																			</div>
																			<div
																				style="overflow: hidden; float: left; width: 60.6%;">
																				<%
                                                                                            List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
                                                                                                                            for (int n = 0; questionList != null && n < questionList.size(); n++) {
                                                                                                                                List<String> question1List = questionList.get(n);
                                                                                            %>
																				<div
																					style="overflow: hidden; float: left; width: 100%;">
																					<div
																						style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 67%;">
																						<center>
																							<p><%=question1List.get(0)%></p>
																						</center>
																					</div>
																					<div
																						style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 32.1%;">
																						<center>
																							<p><%=question1List.get(1)%></p>
																						</center>
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
										</div>
										<%
                                                    } else if (uF.parseToInt(innerList1.get(2)) == 2) {
                                                                    List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                                                    Map<String, List<List<String>>> scoreMp = list.get(0);
                                                                    Map<String, List<List<String>>> measureMp = list.get(1);
                                                                    Map<String, List<List<String>>> questionMp = list.get(2);
                                                    %>
										<%--  <div
                                                    style="overflow: hidden;float: left; border: 1px solid rgb(0,0,0); width: 100%; background-color: rgb(192, 192, 192);text-align: left; font-size: 20px; font-weight: bold;"><%=innerList1.get(1)%>
                                                    <br/>
                                                    <%=innerList1.get(4)%>
                                                    </div> --%>
										<div
											style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
											<div style="text-align: left;">
												<%-- <blockquote><strong>Competencies + Measures</strong></blockquote> --%>
												<blockquote>
													<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=uF.showData(innerList1.get(1),"") %></strong>
												</blockquote>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 22.9%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Competency</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Competency Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 13.4%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Measure</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 13.2%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 22.9%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Question</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 12.6%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Question Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; width: 100%; border: 1px solid #000000">
												<%
                                                            List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
                                                                            for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                                                List<String> innerList = scoreList.get(j);
                                                            %>
												<div style="overflow: hidden; float: left; width: 100%;">
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 23.1%;">
														<center>
															<p><%=innerList.get(1)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 14.1%;">
														<center>
															<p><%=innerList.get(2)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 62.7%;">
														<%
                                                                    List<List<String>> measureList = measureMp.get(innerList.get(0));
                                                                                        for (int k = 0; measureList != null && k < measureList.size(); k++) {
                                                                                            List<String> measureinnerList = measureList.get(k);
                                                                    %>
														<div
															style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 21.4%;">
															<center>
																<p><%=measureinnerList.get(1)%></p>
															</center>
														</div>
														<div
															style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 21.2%;">
															<center>
																<p><%=measureinnerList.get(2)%></p>
															</center>
														</div>
														<div
															style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 57.1%;">
															<%
                                                                        List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
                                                                                                for (int l = 0; questionList != null && l < questionList.size(); l++) {
                                                                                                    List<String> question1List = questionList.get(l);
                                                                        %>
															<div style="overflow: hidden; float: left; width: 100%;">
																<div
																	style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 64.2%;">
																	<center>
																		<p><%=question1List.get(0)%></p>
																	</center>
																</div>
																<div
																	style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 35.4%;">
																	<center>
																		<p><%=question1List.get(1)%></p>
																	</center>
																</div>
															</div>
															<% }  %>
														</div>
														<% } %>
													</div>
												</div>
												<% }%>
											</div>
										</div>
										<%
                                                    } else  if (uF.parseToInt(innerList1.get(2)) == 3) {
                                                             List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                                             Map<String, List<List<String>>> scoreMp = list.get(0);
                                                             Map<String, List<List<String>>> measureMp = list.get(1);
                                                             Map<String, List<List<String>>> questionMp = list.get(2);
                                                             Map<String, List<List<String>>> GoalMp = list.get(3);
                                                    %>
										<div
											style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
											<div style="text-align: left;">
												<%-- <blockquote><strong>Competencies + Goals + Measures</strong></blockquote> --%>
												<blockquote>
													<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=uF.showData(innerList1.get(1),"") %></strong>
												</blockquote>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 14%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Competency</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 10%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 16.5%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Goal </b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 10%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Goal Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 8.3%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Measure</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 8.1%; background-color: rgb(255, 255, 255); text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; background-color: rgb(255, 255, 255); border: 1px solid rgb(0, 0, 0); width: 19%; text-align: center;">
												<b>Question</b>
											</div>
											<div
												style="overflow: hidden; float: left; background-color: rgb(255, 255, 255); border: 1px solid rgb(0, 0, 0); width: 12.7%; text-align: center;">
												<b>Weightage</b>
											</div>
											<div
												style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 100%;">
												<%
                                                            List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
                                                               for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                                  List<String> innerList = scoreList.get(j);
                                                               %>
												<div style="overflow: hidden; float: left; width: 100%;">
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 10%;">
														<center>
															<p><%=innerList.get(1)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 14.2%;">
														<center>
															<p><%=innerList.get(2)%></p>
														</center>
													</div>
													<div
														style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 75.6%;">
														<%
                                                                    List<List<String>> goalList = GoalMp.get(innerList.get(0));
                                                                    for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                                        List<String> goalinnerList = goalList.get(k);
                                                                    %>
														<div style="overflow: hidden; float: left; width: 100%;">
															<div
																style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 22.1%;">
																<center>
																	<p><%=goalinnerList.get(1)%></p>
																</center>
															</div>
															<div
																style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 13.3%;">
																<center>
																	<p><%=goalinnerList.get(2)%></p>
																</center>
															</div>
															<div
																style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); width: 64.4%;">
																<%
                                                                            List<List<String>> measureList = measureMp.get(goalinnerList.get(0));
                                                                            for (int l = 0; measureList != null && l < measureList.size(); l++) {
                                                                                List<String> measureinnerList = measureList.get(l);
                                                                            %>
																<div style="overflow: hidden; float: left; width: 100%;">
																	<div
																		style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 17%;">
																		<center>
																			<p><%=measureinnerList.get(1)%></p>
																		</center>
																	</div>
																	<div
																		style="overflow: hidden; float: left; border-top: 1px solid rgb(0, 0, 0); width: 17%;">
																		<center>
																			<p><%=measureinnerList.get(2)%></p>
																		</center>
																	</div>
																	<div
																		style="overflow: hidden; float: left; width: 65.8%;">
																		<%
                                                                                    List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
                                                                                    for (int m = 0; questionList != null && m < questionList.size(); m++) {
                                                                                    	List<String> question1List = questionList.get(m);
                                                                                    %>
																		<div
																			style="overflow: hidden; float: left; width: 100%;">
																			<div
																				style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 59.6%;">
																				<center>
																					<p><%=question1List.get(0)%></p>
																				</center>
																			</div>
																			<div
																				style="overflow: hidden; float: left; border-left: 1px solid rgb(0, 0, 0); border-top: 1px solid rgb(0, 0, 0); width: 39.8%;">
																				<center>
																					<p><%=question1List.get(1)%></p>
																				</center>
																			</div>
																		</div>
																		<%  } %>
																	</div>
																</div>
																<%  } %>
															</div>
														</div>
														<%} %>
													</div>
												</div>
												<%} %>
											</div>
										</div>
										<% } 
                                                    }
                                                                 %>
										<%    }   %>
									</div>
									<!-- /.box-body -->
								</div>
							</li>

							<%} %>
						</ul>
						<%} %>
						<input type="hidden" name="appraisalHeadingDivCounter"
							id="appraisalHeadingDivCounter" value="0">
						<s:hidden name="id"></s:hidden>
						<s:hidden name="step"></s:hidden>
						<s:hidden name="oreinted"></s:hidden>
						<div id="appraisalHeadingDiv">
							<table class="table table_no_border">
								<%if(request.getAttribute("mainlevelTitle")==null){
                                            sectioncnt1 = sectioncnt1+1;
                                            	subsectioncnt1 = 1;
                                            	subSectionTotWeightage = 0;
                                            }
                                            %>
								<tr>
									<th align="right" style="padding-right: 20px;" width="17%"><span><%=sectioncnt1 %>.<br />
									</span> Section Title <%if(request.getAttribute("mainlevelTitle")==null){ %>
										<sup>*</sup> <%} %>
									</th>
									<td colspan="5" style="vertical-align: bottom;"><input
										type="hidden" name="main_level_id" id="main_level_id"
										value="<%=request.getAttribute("main_level_id")%>" /> <%if(request.getAttribute("mainlevelTitle")==null){
                                                    %> <input
										type="text" name="levelTitle" id="levelTitle"
										class="validateRequired form-control" style="width: 80%" /> <%}else{%>
										<%=request.getAttribute("mainlevelTitle")%> <input
										type="hidden" name="levelTitle" id="levelTitle"
										value="<%=request.getAttribute("mainlevelTitle")%>" /> <%} %>
									</td>
								</tr>
								<tr>
									<th align="right" style="padding-right: 20px" valign="top">Short
										Description</th>
									<td colspan="5">
										<%if(request.getAttribute("mainshortDesrciption")==null){ %> <!-- <input type="text" name="shortDesrciption" id="shortDesrciption" style="width:80%"/> -->
										<textarea rows="3" cols="72" name="shortDesrciption"></textarea>
										<!-- id="editor1" --> <%}else{%> <%=request.getAttribute("mainshortDesrciption")%>
										<input type="hidden" name="shortDesrciption"
										id="shortDesrciption"
										value="<%=request.getAttribute("mainshortDesrciption")%>" /> <%} %>
									</td>
								</tr>
								<tr>
									<th align="right" style="padding-right: 20px" valign="top">Long
										Description</th>
									<td colspan="5">
										<!-- <input type="text" name="longDesrciption" id="longDesrciption" /> -->
										<%if(request.getAttribute("mainlongDesrciption")==null){ %> <!-- <textarea rows="4" cols="10" name="longDesrciption" id="longDesrciption" style="width:80%"></textarea> -->
										<textarea rows="3" cols="72" name="longDesrciption"></textarea>
										<!-- id="editor2" --> <%}else{%> <%=request.getAttribute("mainlongDesrciption")%>
										<input type="hidden" name="longDesrciption"
										id="longDesrciption"
										value="<%=request.getAttribute("mainlongDesrciption")%>" /> <%} %>
									</td>
								</tr>
								<tr>
									<th align="right" style="padding-right: 20px">Weightage %
										<%if(request.getAttribute("mainlevelTitle")==null){ %> <sup>*</sup>
										<%} %>
									</th>
									<td><input type="hidden" name="hideSecTotWeight"
										id="hideSecTotWeight" value="<%=sectionTotWeightage%>" /> <%if(request.getAttribute("sectionWeightage")==null) { %>
										<input type="number" name="sectionWeightage"
										id="sectionWeightage" class="validateRequired"
										onkeypress="return isNumberKey(event)"
										value="<%=(100 - sectionTotWeightage) %>"
										onkeyup="validateSECANDSUBSECScore(this.value,'sectionWeightage','hidesectionWeightage');addNewSection();" />
										<input type="hidden" name="hidesectionWeightage"
										id="hidesectionWeightage"
										value="<%=(100 - sectionTotWeightage) %>" /> <%} else {  %> <%=request.getAttribute("sectionWeightage")%>
										<input type="hidden" name="sectionWeightage"
										id="sectionWeightage"
										value="<%=request.getAttribute("sectionWeightage") %>" /> <input
										type="hidden" name="hidesectionWeightage"
										id="hidesectionWeightage"
										value="<%=request.getAttribute("sectionWeightage") %>" /> <%} %>
									</td>
								</tr>
								<tr>
									<th align="right" style="padding-right: 20px">Select
										Attribute</th>
									<td>
										<%if(request.getAttribute("attribname")==null) { %> <select
										name="attribute" id="attribute"
										class="validateRequired form-control "><%=attribute %>
									</select> <%} else { %> <input type="hidden" name="attribute"
										id="attribute" class="validateRequired form-control "
										value="<%=request.getAttribute("attribid")%>" /> <%=request.getAttribute("attribname")%>
										<%} %>
									</td>
								</tr>
								<tr>
									<th align="right" style="padding-right: 20px">Work Flow</th>
									<td colspan="5">
										<%
                                                String member1=(String)request.getAttribute("member");
                                                String[] memberArray1=member1.split(",");
                                                //System.out.println("member ==== > "+member);
                                                for(int i=0;i<memberArray1.length;i++) {
                                                %> <span
										style="float: left; width: 60px; text-align: center;">Step<%=i+1%></span>
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
										<%if(request.getAttribute("attribname")==null) { %> <%for(int j=1;j<=memberArray.length;j++) { %>
										<span style="float: left; width: 60px; text-align: center;">
											<input type="radio" name="<%=memberArray[i]%>"
											value="<%=j %>" <%if(j==1){ %> checked="checked" <%} %> /> </span> <% } %>
										<% } else { %> <input type="hidden" name="<%=memberArray[i]%>"
										<%if(orientPosition != null){%>
										value="<%=uF.parseToInt(orientPosition.get(memberArray[i])) %>"
										<%}else if(orientPosition == null){ %> value="1" <%} %> /> <%for(int j=1; j<=memberArray.length; j++) { %>
										<span style="float: left; width: 60px; text-align: center;">
											<input type="radio" name="<%=memberArray[i]%>"
											value="<%=j %>"
											<%if(orientPosition != null && uF.parseToInt(orientPosition.get(memberArray[i]))==j){%>
											checked="checked" <%}else if(j==1){ %> checked="checked"
											<%} %> disabled="disabled" /> </span> <% } %> <% } %>
									</td>
								</tr>
								<% } %>
							</table>
						</div>
						<div id="sectionAssessmentdiv"
							style="box-shadow: 0px 2px 18px 0px rgba(0, 0, 0, 0.18);margin: 5px;padding:5px;display:<%if(subsectioncnt1==1){ %> none;<%}else{%>block;<%}%>">
							<%-- <span style="float: right">
                                       	<a href="javascript:void(0)" onclick="closeSectionAssessDiv('sectionAssessmentdiv')" class="close-font"></a>
                                    </span> --%>
							<div id="sectiondiv">
								<p
									style="padding-left: 5px; text-align: left; font-size: 16px; margin-bottom: 10px;"><%=sectioncnt1 %>.<%=subsectioncnt1 %>
									Subsection of ' <span id="sectionnamespan">
										<%if(request.getAttribute("mainlevelTitle")!=null){%><%=request.getAttribute("mainlevelTitle")%>
										<%}else{ %>New Section<%} %>
									</span> ' <span style="float: right"> <a
										href="javascript:void(0)"
										onclick="closeSectionAssessDiv('sectionAssessmentdiv')"
										class="close-font"></a> </span>
								</p>
								<table class="table table_no_border clr">
									<tr id="sectionnameTr">
										<th align="right" style="padding-right: 20px" width="15%">Subsection
											Title<sup>*</sup>
										</th>
										<td colspan="5"><input type="hidden"
											name="hidesectioncnt" id="hidesectioncnt"
											value="<%=sectioncnt1%>" /> <input type="hidden"
											name="hidesubsectioncnt" id="hidesubsectioncnt"
											value="<%=subsectioncnt1%>" /> <input type="text"
											name="subsectionname" id="subsectionname"
											class="validateRequired form-control" style="width: 450px;" />
										</td>
									</tr>
									<tr id="sectionDescTr">
										<th align="right" style="padding-right: 20px" width="15%">Subsection
											Short Description</th>
										<td colspan="5">
											<!-- <input type="text" name="subsectionDescription" id="subsectionDescription" style="width: 450px;"/> -->
											<textarea rows="3" cols="72" name="subsectionDescription"></textarea>
											<!-- id="editor3" --></td>
									</tr>
									<tr id="sectionLongDescTr">
										<th align="right" style="padding-right: 20px" width="15%">Subsection
											Long Description</th>
										<td colspan="5">
											<!-- <input type="text" name="subsectionLongDescription" id="subsectionLongDescription" style="width: 450px;"/> -->
											<textarea rows="3" cols="72" name="subsectionLongDescription"></textarea>
											<!-- id="editor4" --></td>
									</tr>
									<tr>
										<th align="right" style="padding-right: 20px;">Weightage
											%<sup>*</sup>
										</th>
										<td><input type="hidden" name="hideSubSecTotWeight"
											id="hideSubSecTotWeight" value="<%=subSectionTotWeightage%>" />
											<input type="number" name="subSectionWeightage"
											id="subSectionWeightage" class="validateRequired"
											onkeypress="return isNumberKey(event)"
											value="<%=(100 - subSectionTotWeightage) %>"
											onkeyup="validateSECANDSUBSECScore(this.value,'subSectionWeightage','hidesubSectionWeightage');addNewSection();" />
											<input type="hidden" name="hidesubSectionWeightage"
											id="hidesubSectionWeightage"
											value="<%=(100 - subSectionTotWeightage) %>" /></td>
									</tr>
								</table>
							</div>
							<div>
								<ul class="level_list ul_class">
									<li>
										<div
											style="margin-top: 10px; background-color: lemonchiffon; border: 1px solid; padding: 10px;">
											<table class="table table_no_border">
												<tr>
													<td>System:<Sup>*</Sup>
													</td>
													<td><s:select theme="simple" name="appraisalSystem"
															headerKey="" headerValue="Select System"
															list="#{'2':'Other'}"
															onchange="showSystem(this.value,'');"
															cssClass="validateRequired" /> <%if(request.getAttribute("type") != null){ %>
														<%-- <span id="assessdiv<%=(request.getAttribute("divCount"))%>"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>')"/></span> --%>
														<span
														id="assessdiv<%=(request.getAttribute("divCount"))%>"><i
															class=" fa fa-times-circle cross" aria-hidden="true"
															onclick="closeEditDiv('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>')"></i>
													</span> <%} else{%> <span
														id="assessdiv<%=(request.getAttribute("divCount"))%>"></span>
														<%} %>
													</td>
													<td></td>
												</tr>
												<tr id="scoreCardID" style="display: none;">
													<td>Competency<sup>*</sup>
													</td>
													<td><s:select theme="simple" name="scoreCard"
															headerKey="" headerValue="Select Competency"
															id="scoreCard" cssClass="validateRequired"
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
															id="otherQuestionType" headerKey=""
															headerValue="Select Type" cssClass="validateRequired"
															list="#{'With Short Description':'With Short Description', 'Without Short Description':'Without Short Description'}" />
													</td>
													<td></td>
												</tr>
												<tr id="anstypeTr" style="display: none;">
													<td>Select Answer Type</td>
													<td><select name="ansType" id="ansType"
														onchange="showAnswerTypeDiv(this.value)"><%=anstype %></select>
													</td>
													<td>
														<div id="anstypediv">
															<div id="anstype9">
																a) Option1&nbsp;<input type="checkbox" value="a"
																	name="correct" disabled="disabled" /> b) Option2&nbsp;<input
																	type="checkbox" name="correct" value="b"
																	disabled="disabled" /><br /> c) Option3&nbsp;<input
																	type="checkbox" value="c" name="correct"
																	disabled="disabled" /> d) Option4&nbsp;<input
																	type="checkbox" name="correct" value="d"
																	disabled="disabled" /><br />
															</div>
														</div></td>
												</tr>
											</table>
											<!-- <div id="weightageTr" style="width: 100%; float: left; margin-top: 10px; ">
                                                        <div style="float: left; text-align: right; width: 17%; margin-right: 10px; margin-bottom: 10px; font-weight: bold;">Weightage</div>
                                                        <div style="float: left; width: 80%; margin-left: 10px; margin-bottom: 10px;">
                                                        	<input type="radio" name="checkWeightage" id="checkWeightage" value="1" checked="checked" onclick="getcheckWeightage(this.value);"/>Yes
                                                        	<input type="radio" name="checkWeightage" id="checkWeightage" value="0" onclick="getcheckWeightage(this.value);"/>no
                                                        	<input type="hidden" name="hidecheckWeightage" id="hidecheckWeightage" />
                                                        </div>
                                                        </div> -->
										</div></li>
								</ul>
							</div>
							<div id="mainDiv" style="margin: 10px 0px 0px 0px;"></div>
							<div id="assessOfSubsectionDiv"></div>
							<div id="otherDiv" style="display: none;">
								<ul class="level_list ul_class">
									<li><a href="javascript:void(0)"
										onclick="getOtherquestion('0');"><i
											class="fa fa-plus-circle"></i>Assess info / points</a>
									</li>
									<li id="otherQuestionLi"></li>
								</ul>
							</div>
						</div>
						<div style="float: left; margin-top: 15px; width: 100%;">
							<span id="sectionLinkDiv"
								style="float: left; margin-left: 100px; display:<%if(subsectioncnt1==1){ %> block;<%}else{%>none;<%}%>"><a
								href="javascript:void(0)" title="Add Subsections"
								onclick="openSectionDiv('section');"> +Subsections </a>
							</span> <span id="assessLinkDiv"
								style="float: left; margin-left: 100px;"><a
								href="javascript:void(0)" title="Add Assessments"
								onclick="openSectionDiv('assessment');"> +Assessments </a>
							</span>
						</div>
					</s:if>
					<div id="firstdiv">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
						&nbsp;&nbsp;&nbsp;&nbsp;
						 <s:if test="id != null">
							<s:if test="step==3">
								<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
                            </s:if>
						 </s:if>
						<input type="button" value="Cancel" class="btn btn-danger"
							name="cancel" onclick="closeForm();">
					</div>
					<div id="seconddiv"
						style="float: right; margin-top: 20px; display: none">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<s:submit value="Save & Publish" cssClass="btn btn-primary"
							name="submitandpublish"></s:submit>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="id != null">
							<s:submit value="Save & Add New Section"
								cssClass="btn btn-primary" name="saveandnew"></s:submit>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                    <s:submit
								value="Save & Add New Subsection" cssClass="btn btn-primary"
								name="saveandnewsystem"></s:submit>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                </s:if>
						<input type="button" value="Cancel" class="btn btn-danger"
							name="cancel" onclick="closeForm();">
					</div>
					<div id="secWeightdiv"
						style="float: right; margin-top: 20px; display: none">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="id != null">
							<s:submit value="Save & Publish" cssClass="btn btn-primary"
								name="submitandpublish"></s:submit>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                    <s:submit
								value="Save & Add New Subsection" cssClass="btn btn-primary"
								name="saveandnewsystem"></s:submit>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                </s:if>
						<input type="button" value="Cancel" class="btn btn-danger"
							name="cancel" onclick="closeForm();">
					</div>
					<div id="subSecWeightdiv"
						style="float: right; margin-top: 20px; display: none">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="id != null">
							<s:submit value="Save & Publish" cssClass="btn btn-primary"
								name="submitandpublish"></s:submit>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                    <s:submit
								value="Save & Add New Section" cssClass="btn btn-primary"
								name="saveandnew"></s:submit>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                </s:if>
						<input type="button" value="Cancel" class="btn btn-danger"
							name="cancel" onclick="closeForm();">
					</div>
					<div id="subSecSecWeightdiv"
						style="float: right; margin-top: 20px; display: none">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="id != null">
							<s:submit value="Save & Publish" cssClass="btn btn-primary"
								name="submitandpublish"></s:submit>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                </s:if>
						<input type="button" value="Cancel" class="btn btn-danger"
							name="cancel" onclick="closeForm();">
					</div>
					<s:hidden name="plancount" id="plancount"></s:hidden>
				</s:form>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	</section>
</div>
</section>

<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">Candidate Information</h4>
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

<div class="modal" id="modalInfo1" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title modal-title1"></h4>
			</div>
			<div class="modal-body" id="modal-body1"
				style="height: 400px; overflow-y: auto; padding-left: 25px;">
			</div>
			<div class="modal-footer">
				<button type="button" id="closeButton1" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>


<script>
var submitActor = null;
var submitButtons = $('form').find('input[type=submit]');
submitButtons.click(function(event) {
	    submitActor = this;
}); 

$("form").bind('submit',function(event) {
		  event.preventDefault();
		  
		  if (null === submitActor) {
			
           // If no actor is explicitly clicked, the browser will
           // automatically choose the first in source-order
           // so we do the same here
           submitActor = submitButtons[0];
       }
		  var form_data = $("#formID").serialize();
		  var submit = submitActor.name;
		
		   if(submit != null && submit == "submitandpublish") {
	     	  form_data = form_data +"&submitandpublish=Save And Publish";
	       } else if(submit != null && submit == "submit"){
	     	  form_data = form_data +"&submit=Save";
	       }else if(submit != null && submit == "saveandnewsystem"){
	     	  form_data = form_data +"&saveandnewsystem=Save And Add New Subsection";
	       }else if(submit != null && submit == "saveandnew"){
	     	  form_data = form_data +"&saveandnew=Save And Add New Section";
	       }
		
			$("#divMyHRData").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
	     		type: 'POST',
	     		url: "CreateMyReview.action",
	     		data: form_data,
	     	    success: function(result){
	     	    	//alert("result==>"+result);
	     			$('#divMyHRData').html(result);
	     		},
				error: function(result){
					$.ajax({
						url: 'KRATarget.action?fromPage=MyHR',
						cache: true,
						success: function(result){
							$("#divMyHRData").html(result);
				   		}
					});
				}
	       	});
		});
/*created by dattatray  */
function createRevieweePanelForReview() {
	if(document.getElementById("divAppraiser")) {
		var employeeIds = '<%=(String) session.getAttribute(IConstants.EMPID)%>';
		var orientationType = document.getElementById("oreinted").value;
		xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
        } else {
            var xhr = $.ajax({
                url : "AutoGeneratedRevieweePanel.action?empids="+employeeIds+"&orientationType="+orientationType,
                cache : false,
                success : function(data) {
                	if(data == "") {
                	} else {
                		document.getElementById("divAppraiser").innerHTML = data;
                	}
                }
            });
    	}
	}
}
/*created by dattatray  */
function removeRevieweePanelMember(spanName) {
	//alert("spanName ===>> " + spanName);
	document.getElementById(spanName).innerHTML = "";
}
		
</script>