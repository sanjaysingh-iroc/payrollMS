<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<script src="scripts/ckeditor_cust/ckeditor.js"></script>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<g:compress>

	<script>
	
	$(document).ready(function(){
		
		$("body").on('click','#closeButton',function(){
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
	    });
		$("body").on('click','#closeButton1',function(){
			$(".modal-dialog1").removeAttr('style');
			$("#modal-body1").height(400);
			$("#modalInfo1").hide();
	    });
		$("body").on('click','#close1',function(){
			$(".modal-dialog1").removeAttr('style');
			$(".modal-body1").height(400);
			$("#modalInfo1").hide();
		});
		
		$("body").on('click','.close',function(){
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
		});
		
		$("#reviewerId").multiselect().multiselectfilter();
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
		if(document.getElementById( 'eMessage' )) {
			document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
		}
	}
	
	function onBlur() {
		if(document.getElementById( 'eMessage' )) {
			document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
		}
	}

	CKEDITOR.config.width='700px';

	$(function() {
	    
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
	    
        $("#startFrom").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#endTo').datepicker('setStartDate', minDate);
        });
        
        $("#endTo").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#startFrom').datepicker('setEndDate', minDate);
        });
	    $("#formID_month").multiselect().multiselectfilter();
	    $("select[multiple='multiple']").multiselect().multiselectfilter();
	    
	});
 
	function setDatepickerDefault(id1,id2){
		
	    $( '#'+id1 ).datepicker({format: 'dd/mm/yyyy'});
	    $( '#'+id2 ).datepicker({format: 'dd/mm/yyyy'});
	
	}
	
	jQuery(document).ready(function(){
	    // binds form submission and fields to the validation engine
	    // onloadAddNewSection();
	}); 
 

	var cxtpath='<%=request.getContextPath()%>';
 	function selectAttributeskill(id, val) {
		if (val == 1) {
			document.getElementById("attributeTr" + id).style.display = 'table-row';
		} else {
			document.getElementById("attributeTr" + id).style.display = 'none';
		}
	}

	function checkFrequency(value) {
		// dayMonth  monthly annualy weekly
		//alert("value ===>> " + value);
		if (value == '3') {
			//alert("dfgsdf"); halfYearly quarterly
			document.getElementById("weekly").style.display = "none";
			document.getElementById("monthly").style.display = "block";
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
		//	document.getElementById("annualy").style.display = "block";
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
	
	function getOrgLocationDepartLevelDesigGrade() {
		//alert("value ===>> " + value);
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	            alert("Browser does not support HTTP Request");
	            return;
	    } else {
	    	var strOrg = getSelectedValue("strOrg");
            var xhr = $.ajax({
                 url : "GetOrgwiseLocationDepartLevelDesigGrade.action?strOrgId=" + strOrg,
                 cache : false,
                 success : function(data) {
                 	if(data == "") {
                 		
                 	} else {
                 		//alert("data --------->> " + data);     
                 		var allData = data.split("::::");
                        document.getElementById("wlocDiv").innerHTML = allData[0];
                        document.getElementById("departDiv").innerHTML = allData[1];
                       	document.getElementById("levelDiv").innerHTML = allData[2];
                       	document.getElementById("myDesig").innerHTML = allData[3];
                       	document.getElementById("myGrade").innerHTML = allData[4];
                    	$("select[name='strOrg']").multiselect().multiselectfilter(); 
                   		$("select[name='strWlocation']").multiselect().multiselectfilter(); 
                   		$("select[name='strDepart']").multiselect().multiselectfilter(); 
                   		$("select[name='strLevel']").multiselect().multiselectfilter(); 
                   		$("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); 
                   		$("select[name='empGrade']").multiselect().multiselectfilter();
                       	getEmployeebyOrg();
                 	}
                 }
            });
	    }
	    $(dialogEdit).dialog('close');
	}

	
	function getLevelwiseDesigGrade(value) {
		//alert("value ===>> " + value);
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	       alert("Browser does not support HTTP Request");
	       return;
	    } else {
	       var xhr = $.ajax({
	           url : "GetLevelwiseDesigAndGrade.action?strLevelId=" + value,
	           cache : false,
	           success : function(data) {
	           	if(data == "") {
	           		
	           	} else {
	           		//alert("data --------->> " + data);     
	           		var allData = data.split("::::");
	                  	document.getElementById("myDesig").innerHTML = allData[0];
	                  	document.getElementById("myGrade").innerHTML = allData[1];
	                    $("select[name='empGrade']").multiselect().multiselectfilter(); 
	            	    $("select[name='strDesignationUpdate']").multiselect().multiselectfilter();
	           	}
	           }
	       });
	    }
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
	
	
	function getEmployeebyOrg(){
		var strOrg = getSelectedValue("strOrg");
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	       alert("Browser does not support HTTP Request");
	       return;
	    } else {
	    	var action = 'getEmployeeList.action?strOrg=' + strOrg+'&page=SOrient' + "&type="+type;
	       	var xhr = $.ajax({
	           	url : action,
	           	cache : false,
	           	success : function(data) {
		           	if(data == "") {
		           	} else {
	                  	document.getElementById("myEmployee").innerHTML = data;
	                  	$("select[name='employee']").multiselect().multiselectfilter();  
		           	}
	           	}
	       	});
	    }
		/* var action = 'getEmployeeList.action?strOrg=' + strOrg;
		getContent('myEmployee', action);
		setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500); */
	}
	
	
	function getEmployeebyLocation() {
		var location = getSelectedValue("wlocation");
		var strOrg = getSelectedValue("strOrg");
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	       alert("Browser does not support HTTP Request");
	       return;
	    } else {
	    	var action = 'getEmployeeList.action?strOrg='+ strOrg+'&location='+ location;
	       	var xhr = $.ajax({
	           	url : action,
	           	cache : false,
	           	success : function(data) {
		           	if(data == "") {
		           	} else {
	                  	document.getElementById("myEmployee").innerHTML = data;
        		   		$("select[name='employee']").multiselect().multiselectfilter();   
		           	}
	           	}
	       	});
	    }
		
	    /* var action = 'getEmployeeList.action?strOrg='+ strOrg+'&location='+ location;
		getContent('myEmployee', action);
		setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
		setTimeout(function(){ $("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); }, 500); */
	}

	
	function getEmployeebyDepart() {
		var strOrg = getSelectedValue("strOrg");
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	       alert("Browser does not support HTTP Request");
	       return;
	    } else {
	    	var action = 'getEmployeeList.action?depart=' + depart+ '&strOrg='+ strOrg;
	    	if (location == '') {
			} else {
				if (location != '') {
					action += '&location=' + location;
				}
			}
	       	var xhr = $.ajax({
	           	url : action,
	           	cache : false,
	           	success : function(data) {
		           	if(data == "") {
		           	} else {
	                  	document.getElementById("myEmployee").innerHTML = data;
        		   		$("select[name='employee']").multiselect().multiselectfilter();   
		           	}
	           	}
	       	});
	    }
	    
		/* var action = 'getEmployeeList.action?depart=' + depart + '&strOrg='+ strOrg+'&location='+ location;
		getContent('myEmployee', action);
		setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500); */
	}

	function getEmployeebyLevel() {
		var strOrg = getSelectedValue("strOrg");
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		var Level = getSelectedValue("strLevel");

		getLevelwiseDesigGrade(Level);
		
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	       alert("Browser does not support HTTP Request");
	       return;
	    } else {
	    	var action = 'getEmployeeList.action?level=' + Level;
			if (location == '' && depart == '') {
			} else {
				if (location != '') {
					action += '&location=' + location;
				}
				if (depart != '') {
					action += '&depart=' + depart;
				}
			}
	       	var xhr = $.ajax({
	           	url : action,
	           	cache : false,
	           	success : function(data) {
		           	if(data == "") {
		           	} else {
	                  	document.getElementById("myEmployee").innerHTML = data;
	    		   		$("select[name='employee']").multiselect().multiselectfilter();  
		           	}
	           	}
	       	});
	    }
	    
		/* var action = 'getEmployeeList.action?level=' + Level +'&depart=' + depart + '&strOrg='+ strOrg+'&location='+ location;
		getContent('myEmployee', action);
		setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500); */
	}
	

	function getEmployeebyDesig() {
		var strOrg = getSelectedValue("strOrg");
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		var Level = getSelectedValue("strLevel");
		var design = getSelectedValue("desigIdV");

		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	       alert("Browser does not support HTTP Request");
	       return;
	    } else {
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
			}
	       	var xhr = $.ajax({
	           	url : action,
	           	cache : false,
	           	success : function(data) {
		           	if(data == "") {
		           	} else {
	                  	document.getElementById("myEmployee").innerHTML = data;
	    		   		$("select[name='employee']").multiselect().multiselectfilter();  
		           	}
	           	}
	       	});
	    }
		
	    
	    xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	       alert("Browser does not support HTTP Request");
	       return;
	    } else {
	    	var action = 'getGrade.action?strDesignation=' + design;
	       	var xhr = $.ajax({
	           	url : action,
	           	cache : false,
	           	success : function(data) {
		           	if(data == "") {
		           	} else {
	                  	document.getElementById("myGrade").innerHTML = data;
	                  	$("select[name='empGrade']").multiselect().multiselectfilter();  
		           	}
	           	}
	       	});
	    }

		/* var action = 'getEmployeeList.action?design=' + design+'&level=' + Level +'&depart=' + depart + '&strOrg='+ strOrg+'&location='+ location;
		getContent('myEmployee', action);
		setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
		getContent('myGrade', 'getGrade.action?strDesignation=' + design);
		setTimeout(function(){ $("select[name='empGrade']").multiselect().multiselectfilter(); }, 500); */

	}
	
	
	function getEmployeebyGrade() {
		var strOrg = getSelectedValue("strOrg");
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		var Level = getSelectedValue("strLevel");
		var design = getSelectedValue("desigIdV");
		var grade = getSelectedValue("gradeIdV");

		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	       alert("Browser does not support HTTP Request");
	       return;
	    } else {
	    	var action = 'getEmployeeList.action?grade=' + grade;
			if (location != '')  {
				action += '&location=' + location;
			}if (depart != '')  {
				action += '&depart=' + depart;
			}if (Level != '')  {
				action += '&level=' + Level;
			}if (design != '')  {
				action += '&design=' + design;
			}
	       	var xhr = $.ajax({
	           	url : action,
	           	cache : false,
	           	success : function(data) {
		           	if(data == "") {
		           	} else {
	                  	document.getElementById("myEmployee").innerHTML = data;
	                  	$("select[name='employee']").multiselect().multiselectfilter();  
		           	}
	           	}
	       	});
	    }
	    
		/* var action = 'getEmployeeList.action?grade=' + grade +'&design=' + design+'&level=' + Level +'&depart=' + depart + '&strOrg='+ strOrg+'&location='+ location;
		document.getElementById("employee").selectedIndex = 0;
		getContent('myEmployee', action);
		setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500); */
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

	
	function goalDivData(systemType, showText){
		var a="<ul class=\"level_list ul_class\"><li><a href=\"javascript:void(0)\" onclick=\"getGoalsForReview('"+systemType+"');\"><i class=\"fa fa-plus-circle\"></i>"+showText+"</a></li>"
			//+"<div class=\"sectionfont\" id=\"goalDataDiv\" style=\"float:left; width:100%;\"></div></ul>";
			+"<li id=\"goalDataDiv\"></li></ul>";
			return a;
		} 

	function getGoalsForReview(systemType){
		//alert(callFrom + "  " +systemType);
		var id = null;
		if(document.getElementById("id")){
			id = document.getElementById("id").value;
			//alert(systemType + "  " +id);
	    }
		var answerType = null;
		if(document.getElementById("ansType")){
			answerType = document.getElementById("ansType").value; 
	    }
		//alert("answerType  " +answerType);
		var action = 'GetGoalKRATargetForReview.action?id='+id+'&systemType='+systemType+'&ansType='+answerType;
		getContent('goalDataDiv', action);
		
	}
	
	function showSystem(value, divcount) {
		//	alert("showSystem(value, divcount) " + divcount+" , "+ value);
			if (value == '1') {
				document.getElementById("mainDiv").style.display='block';
				document.getElementById("otherDiv").style.display='none';
				document.getElementById("goalDiv").style.display='none';
				document.getElementById("assessOfSubsectionDiv").style.display='none';
				document.getElementById("otherQuestionLi").innerHTML='';
				document.getElementById("goalDiv").innerHTML='';
				document.getElementById("scoreCardID" + divcount).style.display = 'table-row';
				document.getElementById("otherqueTypeTr").style.display = 'none';
				document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
				//document.getElementById("weightageTr" + divcount).style.display = 'table-row';
				
			} else if (value == '2') {
				questionCnt=0;
				document.getElementById("otherqueTypeTr").style.display = 'table-row';
				document.getElementById("mainDiv").style.display='none';
				document.getElementById("goalDiv").style.display='none';
				document.getElementById("otherDiv").style.display='block';
				document.getElementById("assessOfSubsectionDiv").style.display='block';
				//document.getElementById("otherDiv").innerHTML=otherDivData();
				document.getElementById("mainDiv").innerHTML='';
				document.getElementById("goalDiv").innerHTML='';
				document.getElementById("scoreCardID" + divcount).style.display = 'none';
				document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
				//document.getElementById("weightageTr" + divcount).style.display = 'table-row';
			} else if (value == '3' || value == '4' || value == '5') {
				document.getElementById("otherqueTypeTr").style.display = 'none';
				document.getElementById("mainDiv").style.display='none';
				document.getElementById("goalDiv").style.display='block';
				document.getElementById("otherDiv").style.display='none';
				document.getElementById("assessOfSubsectionDiv").style.display='none';
				//document.getElementById("otherDiv").innerHTML=otherDivData();
				document.getElementById("mainDiv").innerHTML='';
				if(value == '3'){
					document.getElementById("goalDiv").innerHTML=goalDivData('goal', 'Goals');
				} else if(value == '4'){
					document.getElementById("goalDiv").innerHTML=goalDivData('KRA', 'KRAs');
				} else if(value == '5'){
					document.getElementById("goalDiv").innerHTML=goalDivData('target', 'Targets');
				}
				document.getElementById("scoreCardID" + divcount).style.display = 'none';
				document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
				//document.getElementById("weightageTr" + divcount).style.display = 'table-row';
	//===start parvez date: 21-12-2021===	
			} else if (value == '6') {
				questionCnt=0;
				document.getElementById("otherqueTypeTr").style.display = 'table-row';
				document.getElementById("mainDiv").style.display='none';
				document.getElementById("goalDiv").style.display='none';
				document.getElementById("otherDiv").style.display='none';
				
				document.getElementById("otherQuestionTypeTd").style.display='none';
				document.getElementById("pgQuestionTd").style.display='block';
				
				document.getElementById("assessOfSubsectionDiv").style.display='block';
				document.getElementById("mainDiv").innerHTML='';
				document.getElementById("goalDiv").innerHTML='';
				document.getElementById("scoreCardID" + divcount).style.display = 'none';
				document.getElementById("anstypeTr" + divcount).style.display = 'none';
				
	//===end parvez date: 21-12-2021===			
			} else {
				document.getElementById("scoreCardID" + divcount).style.display = 'none';
				document.getElementById("otherqueTypeTr").style.display = 'none';
				document.getElementById("otherDiv").style.display='none';
				document.getElementById("goalDiv").style.display='none';
				document.getElementById("assessOfSubsectionDiv").style.display='none';
				//document.getElementById("otherDiv" + divcount).style.display = 'none';
				//document.getElementById("scoreCardDiv" + divcount).style.display = "none";
				document.getElementById("mainDiv").innerHTML='';
				document.getElementById("otherQuestionLi").innerHTML='';
				document.getElementById("goalDiv").innerHTML='';
				document.getElementById("anstypeTr" + divcount).style.display = 'none';
				//document.getElementById("weightageTr" + divcount).style.display = 'none';
			/* ===start parvez date: 28-02-2023=== */	
				//document.getElementById("caculationBasisTr" + divcount).style.display = 'none';
			/* ===end parvez date: 28-02-2023=== */
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
								+"<li><a href=\"javascript:void(0);\"  id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a></li>"
								+ "<li id=\"objDiv_"+goalID+"\"><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
									+"<ul id=\""+objectiveID+"\">"
										+"<li>" + getObjectiveData((scoreCnt+1)+'.1.1)',goalID,'0') + "</li>"
										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addObjective('"+ goalID + "','"+ (scoreCnt+1)+'.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Objective</a></li>"
										+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" value=\"1\" class=\"form-control\"/>"
											+"<ul id=\""+measureID+"\">"
												+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1.1)','0',objectiveID) + "</li>"
												+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','"+ (scoreCnt+1)+'.1.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
												+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\" class=\"form-control\"/>"
													+"<ul id=\""+questionID+"\" >"
														+"<li>"+ getquestion((scoreCnt+1)+'.1.1.1','.1)',measureID) + "</li>"
													+"</ul></li>"
												+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1.1.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
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
								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ scoreID + "','"+ (scoreCnt+1) + "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"+
									"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\" class=\"form-control \"/>"
									+ "<ul id=\""+questionID+"\">"
										+"<li>"+ getquestion((scoreCnt+1)+'.1','.1)',measureID) + "</li>"
									+"</ul></li>"
									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1' +"');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
					+ "</ul></li></ul>";
					document.getElementById("mainDiv").innerHTML = a; 
			} else if (val == '3') { 

			var a = "<ul id=\""+scoreID+"\" class=\"level_list ul_class\">"
						+"<li>" + getScoreData(scoreCnt) + "</li>"
						+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" >Add Competency</a></li>"
						+ "<li id=\"goalDiv_"+scoreID+"\" ><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\" class=\"form-control \"/>"
							+"<ul id=\""+goalID+"\">"
								+"<li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a></li>"
								+ "<li id=\"measureDiv_"+goalID+"\"><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
									+"<ul id=\""+measureID+"\">"
										+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1)','0',goalID) + "</li>"
										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ goalID + "','"+ (scoreCnt+1)+'.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
										+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\" class=\"form-control \"/>"
											+"<ul id=\""+questionID+"\">"
												+"<li>"+ getquestion((scoreCnt+1)+'.1.1','.1)',measureID) + "</li>"
											+"</ul></li>"
										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+(scoreCnt+1)+'.1.1'+"');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
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
						+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ id + "','"+ scoreCnt +"');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a>"
						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\" class=\"close-font\"></a></li>"
						//+"<a href=\"javascript:void(0);\" class=\"add_lvl\" id=\"a\" onclick=\"deleteBlock('"+ goalID + "','"+ id + "','goalcount');\">Delete Goal</a>"
			
						+ "<li id=\"objDiv_"+goalID+"\"><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
						+"<ul id=\""+objectiveID+"\">"
							+"<li>"+ getObjectiveData(scoreCnt+'.'+ (parseInt(val)+1)+'.1)',goalID,'0') + "</li>"
							+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addObjective('"+ goalID + "','"+ scoreCnt+'.'+ (parseInt(val)+1) +"');\"><i class=\"fa fa-plus-circle\"></i>Add Objective</a></li>"
							+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+objectiveID+"\" value=\"1\"/>"
								+"<ul id=\""+measureID+"\">"
									+"<li>"+ getMeasureData(scoreCnt+'.'+ (parseInt(val)+1)+'.1.1)','0',objectiveID) + "</li>"
									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','"+ scoreCnt+'.'+ (parseInt(val)+1)+'.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
									+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
										+"<ul id=\""+questionID+"\" >"
										+"<li>"+ getquestion(scoreCnt+'.'+ (parseInt(val)+1)+'.1.1','.1)',measureID) + "</li>"
										+"</ul>"
									+"</li>"
									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+scoreCnt+'.'+ (parseInt(val)+1)+'.1.1'+"');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
								 +"</ul>"
							+"</li>"
						 +"</ul></li></ul></li>";
						
						litag.innerHTML = a;
					
					} else if (val1 == '3') {
						var a = "<li>" + getGoalData(scoreCnt+'.'+ (parseInt(val)+1)+')',id,goalCnt) + "</li>"
								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ id + "','"+ scoreCnt +"');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a>"
									+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ goalID + "','"+ goalDivId + "','goalcount');\" class=\"close-font\"></a></li>"
								+ "<li id=\"measureDiv_"+goalID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+goalID+"\" value=\"1\"/>"
									+"<ul id=\""+measureID+"\">"
										+"<li>"+ getMeasureData(scoreCnt+'.'+ (parseInt(val)+1)+'.1)','0',goalID) + "</li>"
										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ goalID + "','"+ scoreCnt+'.'+ (parseInt(val)+1) + "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
										+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" class=\"form-control \" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
											+"<ul id=\""+questionID+"\">"
												+"<li>"+ getquestion(scoreCnt+'.'+ (parseInt(val)+1)+'.1','.1)',measureID) + "</li>"
											+"</ul></li>"
										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ scoreCnt+'.'+ (parseInt(val)+1)+'.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
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
						+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addObjective('"+ id + "','"+ scoreCnt + "');\"><i class=\"fa fa-plus-circle\"></i>Add Objective</a>"
						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+ objectiveID + "','"+ objectiveDivId + "','objectivecount');\" class=\"close-font\"></a>"
						+"</li>"
						+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+objectiveID+"\" value=\"1\"/>"
							+"<ul id=\""+measureID+"\">" 
								+"<li>"+ getMeasureData(scoreCnt+'.'+(parseInt(val)+1)+'.1)','0',objectiveID) + "</li>"
								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','"+scoreCnt+'.'+(parseInt(val)+1)+"');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
								+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
									+"<ul id=\""+questionID+"\">"
									+"<li>"+ getquestion(scoreCnt+'.'+(parseInt(val)+1)+'.1','.1)',measureID) 
								+ "</li></ul></li>"
								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ scoreCnt+'.'+(parseInt(val)+1)+'.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
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
						+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" ><i class=\"fa fa-plus-circle\"></i>Add Competency</a>"
						+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" class=\"close-font\"></a></li>"
						//+"<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" >Delete Competency</a></li>"
						+ "<li id=\"goalDiv_"+scoreID+"\"><input type=\"hidden\" name=\"goalcount\" id=\"goalcount"+scoreID+"\" value=\"1\" class=\"form-control \"/><ul id=\""+goalID+"\"><li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
						+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a></li>"
						
						+ "<li id=\"objDiv_"+goalID+"\" ><input type=\"hidden\" name=\"objectivecount\" id=\"objectivecount"+goalID+"\" value=\"1\" class=\"form-control \"/>"
							+"<ul id=\""+objectiveID+"\">"
								+"<li>" + getObjectiveData((scoreCnt+1)+'.1.1)',goalID,'0') + "</li>"
								+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addObjective('"+ goalID + "','"+ (scoreCnt+1)+'.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Objective</a></li>"
						    	+ "<li id=\"measureDiv_"+objectiveID+"\"><input type=\"hidden\" name=\"measurecount\" id=\"measurecount"+objectiveID+"\" class=\"form-control \" value=\"1\"/>"
						    			+"<ul id=\""+measureID+"\">"
						    				+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1.1)','0',objectiveID) + "</li>"
											+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ objectiveID + "','" + (scoreCnt+1)+'.1.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
											+ "<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
												+"<ul id=\""+questionID+"\">"
													+"<li>"+ getquestion((scoreCnt+1)+'.1.1.1','.1)',measureID) + "</li>"
												+"</ul>"
											+"</li>"
											+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1.1.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
								+ "</ul></li></ul></li></ul></ul></li></ul>";
								
					 litag.innerHTML = a;
					 document.getElementById("mainDiv").appendChild(litag);
						
				}else if (val == '2') {
					var a = "<li>" + getScoreData(scoreCnt) + "</li>"
							+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" ><i class=\"fa fa-plus-circle\"></i>Add Competency</a>"
							+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" class=\"close-font\"></a></li>"
					
							+ "<li id=\"measureDiv_"+scoreID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+scoreID+"\" value=\"1\" />"
								+"<ul id=\""+measureID+"\">"
									+"<li>"+ getMeasureData((scoreCnt+1)+'.1)','0',scoreID) + "</li>"
										+"<a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('" + scoreID + "','" + (scoreCnt+1) + "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a>"
										+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" class=\"form-control \" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
											+ "<ul id=\""+questionID+"\">"
												+"<li>"+ getquestion((scoreCnt+1)+'.1','.1)',measureID)
									+ "</li></ul></li>"
									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
							+ "</ul></li></ul></li>";
					litag.innerHTML = a;
					document.getElementById("mainDiv").appendChild(litag);
				}
				 else if (val == '3') {
					 var a = "<li>" + getScoreData(scoreCnt) + "</li>"
							+"<li><a href=\"javascript:void(0)\" onclick=\" addScoreCard()\" ><i class=\"fa fa-plus-circle\"></i>Add Competency</a>"
							+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\" deleteBlock1('"+scoreID+"','mainDiv')\" class=\"close-font\"></a></li>"
						
								+ "<li id=\"goalDiv_"+scoreID+"\"><input type=\"hidden\" name=\"goalcount\" class=\"form-control \" id=\"goalcount"+scoreID+"\" value=\"1\"/>"
									+"<ul id=\""+goalID+"\">"
										+"<li>" + getGoalData((scoreCnt+1)+'.1)',scoreID,'0') + "</li>"
										+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addGoal('"+ scoreID + "','"+ (scoreCnt+1) +"');\"><i class=\"fa fa-plus-circle\"></i>Add Goal</a></li>"
										+ "<li id=\"measureDiv_"+goalID+"\"><input type=\"hidden\" name=\"measurecount\" class=\"form-control \" id=\"measurecount"+goalID+"\" value=\"1\"/>"
											+"<ul id=\""+measureID+"\">"
												+"<li>"+ getMeasureData((scoreCnt+1)+'.1.1)','0',goalID) + "</li>"
												+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addMeasure('"+ goalID + "','" + (scoreCnt+1)+'.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Measure</a></li>"
												+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" class=\"form-control \" name=\"questioncount\" id=\"questioncount"+measureID+"\" value=\"1\"/>"
													+"<ul id=\""+questionID+"\">"
														+"<li>"+ getquestion((scoreCnt+1)+'.1.1','.1)',measureID) 
												+ "</li></ul></li>"
												+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ (scoreCnt+1)+'.1.1' + "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a></li>"
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
									+"<li><a href=\"javascript:void(0)\"  onclick=\"addMeasure('"+ id + "','"+ scoreCnt + "')\"  ><i class=\"fa fa-plus-circle\"></i>Add Measure</a>"
										+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"deleteBlock('"+measureID+"','" +measureDivId+ "','measurecount');\" class=\"close-font\">"
										+"</a>"
									+"</li>"
									+"<li id=\"questDiv_"+measureID+"\"><input type=\"hidden\" name=\"questioncount\" class=\"form-control \" id=\"questioncount"+measureID+"\" value=\"1\"/>"
										+"<ul id=\""+questionID+"\">"
											+"<li>"
												+ getquestion(scoreCnt+'.'+(parseInt(val)+1),'.1)',measureID)
									+ "</li></ul></li>"
									+"<li><a href=\"javascript:void(0);\" id=\"a\" onclick=\"addQuestions('"+ measureID+ "','"+ scoreCnt+'.'+(parseInt(val)+1)+ "');\"><i class=\"fa fa-plus-circle\"></i>Add Question</a>"
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
		var QuestionID = "questionID" + questionCnt;
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
		var questDivId = "questDiv_"+id;
		var a = "<li>"
			 	//+"<p style=\"margin-bottom: 10px; text-align: left; font-size: 12px; font-weight: bold;\">Assessments of \" <span id=\"subsectionnamespan\">New Subsection</span> \"</p>"
		 		+"<table class=\"table\" width=\"100%\">"
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
				
				+"<a href=\"javascript:void(0)\" title=\"Add New Question\" onclick=\"addQuestions('"+id+"','"+scoreCnt+"')\" ><i class=\"fa fa-plus-circle\"></i></a>&nbsp;&nbsp; "
				+"<a href=\"javascript:void(0)\" title=\"Remove Question\" class=\"close-font\" onclick=\"deleteBlock('"+QuestionID+"','"+ questDivId+ "','questioncount');\"/>"

				+"<input type=\"hidden\" class=\"form-control \" name=\"questiontypename\" value=\""+ cnt+"\" /></td></tr>"
				+aa
				+"</table></li>";
			//	jQuery("#formID").validationEngine();
		return a;
		//jQuery("#formID").validationEngine();
	}

	function getScoreData(count) {
		count++;
		var a = "<table class=\"table\" width=\"100%\"><tr><th style=\"width: 4%;\">"+count+")</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Competency</td></tr>"
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
		var a = "<table class=\"table\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Objective"
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

		var a = "<table class=\"table\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th><td>Measures</td></tr>"
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
			var a = "<table class=\"table\" width=\"100%\"><tr><th style=\"width: 4%;\">"+val+"</th><th width=\"15%\" style=\"text-align: right;\">Level Type</th>"
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
	
	function removeID(id) {
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
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
		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\" validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\" validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
	}
	function addQuestionType2(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\" validateRequired form-control \"  /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\" validateRequired form-control \" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
	}
	function addQuestionType3(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\" validateRequired form-control \"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\"  class=\" validateRequired form-control \" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
	}
	function addQuestionType4(id1,cnt) {
		document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\"  class=\" validateRequired form-control \" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\" validateRequired form-control \" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
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
			//<option value=\"\">Select Type</option>
			var a="<ul class=\"level_list ul_class\"><li><table class=\"table table_no_border\" width=\"100%\"><tr><th width=\"15%\" style=\"text-align: right;\">Question Type</th><td><select name=\"otherQuestionType\" id=\"otherQuestionType\" class=\" form-control \">"
			+"<option value=\"Without Short Description\">Without Short Description</option>"
			+"<option value=\"With Short Description\">With Short Description</option>"
			+"</select>"
			+"</td></tr>"
			
			+"<tr><th align=\"right\" style=\"padding-right:20px\" width=\"17%\">System<sup>*</sup></th>"
			+"<td colspan=\"5\"><select name=\"appraisalSystem\" id=\"appraisalSystem\" class=\" validateRequired form-control \">"
			+"<option value=\"\">Select System</option><option value=\"1\">Balance Score Card Review System</option>"
			+"<option value=\"2\">Other</option></select>"
			+"<option value=\"3\">Goal</option></select>"
			+"<option value=\"4\">KRA</option></select>"
			+"<option value=\"5\">Target</option></select>"
			+"</td></tr>"
			
			+"<tr id=\"scoreCardID\" style=\"display:none;\"> <th width=\"17%\" style=\"padding-right:20px\" align=\"right\">Competency</th>"
			+"<td colspan=\"5\"><select name=\"scoreCard\" id=\"scoreCard\" onchange=\"getcontent(this.value);\" class=\" form-control \">"
			+"<option value=\"\">Select Competency</option><option value=\"1\">Competencies + Goals + Objectives + Measures</option>"
			+"<option value=\"3\">Competencies + Goals + Measures</option></select>"
			+"<option value=\"2\">Competencies + Measures</option></select>"
			
			+"</table></li>"
			+"<li><a href=\"javascript:void(0)\"  onclick=\"getOtherquestion('0');\"><i class=\"fa fa-plus-circle\"></i>Assessments</a></li>"
			+"<li id=\"otherQuestionLi\"></li></ul>";
			return a;
		} 
	
	  function showAssessOfSubsection(){
		  //alert("showAssessOfSubsection ");
			var a="<ul class=\"level_list ul_class\">"
				+"<p style=\"font-size: 14px;\">Assessments of \" <span id=\"subsectionnamespan\">New Subsection</span> \"</p>"
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
				otherSD = "<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" onclick=\"openOtheQueShortD('"+cnt+"')\" > D </a></span>";
				othrQtype = "<tr id=\"shortdescTr"+cnt+"\" style=\"display: none;\"><th></th><th style=\"text-align: right;\">Short Description</th><td colspan=\"3\"><input type=\"hidden\" name=\"hideotherSD\" id=\"hideotherSD"+cnt+"\" value=\"f\"/><input type=\"text\" name=\"otherSDescription\" id=\"otherSDescription\" style=\"width: 450px;\"  class=\" form-control \" /></td></tr>";
			}else{
				otherSD = "<span style=\"float: left; margin-left: 10px;\"> D </span>";
				othrQtype="";
			}
			ultag.id = "otherQuestionUl"+cnt;
			
			 var a = "<li><table class=\"table table_no_border\">"
					+ "<tr><th>"+gencntid+"."+questionCnt+")</th><th width=\"17%\" style=\"text-align: right;\">Add Question<sup>*</sup></th>"
					+ "<td colspan=\"3\"><span id=\"newquespan"+cnt+"\" style=\"float: left; \"><input type=\"hidden\" name=\"hidequeid\" id=\"hidequeid"+cnt+"\" value=\"0\"/>"
					+"<textarea rows=\"2\" name=\"question\" id=\"question"+cnt+"\" class=\"validateRequired form-control \"  style=\"width: 330px;\"></textarea>"
					//+"<input type=\"text\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\"/>"
					+"</span>"
	
					+ "&nbsp;<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt\" value=\""+cnt+"\"/><sup>*</sup>"
					+"<input type=\"number\" style=\"width: 35px !important;\" name=\"weightage\" id=\"weightage"+cnt+"\" class=\"validateRequired form-control\" value=\"100\" onkeyup=\"validateScore1(this.value,'weightage"+cnt+"','weightage');\" onkeypress=\"return isNumberKey(event)\"/>"
					+"<input type=\"hidden\" name=\"hideweightage\" id=\"hideweightage"+cnt+"\" value=\"100\"/></span>&nbsp;&nbsp;"
					+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"');\" > +Q </a></span>&nbsp;"+ otherSD +"&nbsp;"
					+"<span id=\"checkboxspan"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+ cnt+ "')\" />"
					+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/></span>"
					
					+"<a href=\"javascript:void(0)\"  title=\"Add New Question\" onclick=\"getOtherquestion('"+cnt+"')\" ><i class=\"fa fa-plus-circle\"></i></a>&nbsp;&nbsp; "
					+"<a href=\"javascript:void(0)\" title=\"Remove Question\" class=\"close-font\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\"/>"
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
		var val = getSelectedValue("ansType");//document.getElementById("ansType").value;
		//alert("getQuestoinContentType val : "+val);
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
			a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiona\" id=\"rateoptiona"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
			+"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionb\" id=\"rateoptionb"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td></tr>"
			+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionc\" id=\"rateoptionc"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
			+"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiond\" id=\"rateoptiond"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td></tr>"
			+ "<tr id=\"answerType2"+cnt+"\"><th></th><th></th><td>e)&nbsp;<input type=\"text\" name=\"optione\" id=\"optione"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptione\" id=\"rateoptione"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
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
	
	function openQuestionBank(count) {
		
		var ansType=document.getElementById('ansType').value;
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
	
	function openEditAppraisal(id,appsystem) {
		if(confirm('Are you sure, You want to create Review from this template?')) {
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$("#modalInfo").show();
			$(".modal-title").html('Edit Review');
			if($(window).width() >= 1100) {
			  $(".modal-dialog").width(1100);
			}
		 	$.ajax({
				url : "EditAppraisalPopUp.action?id="+id+"&appsystem="+appsystem,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		}
		
		$("#from").datepicker({format : 'dd/mm/yyyy'});
		$("#to").datepicker({format : 'dd/mm/yyyy'}); 
		
	}
	
	function createExistAppraisalPopup() {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Exist Review Systems');
		 $.ajax({
				url : "ExistAppraisalReportPopup.action",
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
                            //document.getElementById("statetitle").style.display = 'block';
                    	}
                    }
            });
        }
        $(".modal").hide();
	}
	
	var dialogEdit = '#showChoosePopupDiv';
	function showChoosePopup(hideID,lblID, type) {
		var hideIdValue = document.getElementById(hideID).value;
		//alert("hideIdValue==>"+hideIdValue);
		var dialogEdit = '#modal-body1';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo1").show();
		 $(".modal-title1").html('Choose '+type);
		 $(".modal-dialog1").removeAttr("style");
		 $.ajax({
				url : "ShowOrientationWiseEmpChoosePopup.action?hideID=" + hideID+"&lblID="+lblID + "&type="+type+"&hideIdValue="+hideIdValue,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	function goalChart(corpGoalID, managerGoalID, teamGoalID, goalID) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Goal Chart');
		 $.ajax({
//				url : "GoalChartIndividualGoal.action?corpGoalID=" + corpGoalID + "&managerGoalID=" + managerGoalID +"&teamGoalID=" + teamGoalID+"&goalID=" + goalID,
				url : 'GoalChartIndividualGoal.action?corpGoalID=' + corpGoalID + '&managerGoalID=' + managerGoalID +'&teamGoalID=' + teamGoalID+'&goalID=' + goalID,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	function showOrientData(value){
		if(value=="1"){
			document.getElementById("hrDiv").style.display = "block";
			document.getElementById("managerDiv").style.display = "none";
			document.getElementById("peerDiv").style.display = "none";
			document.getElementById("otherDiv").style.display = "none";
		} else if(value=="2"){
			document.getElementById("hrDiv").style.display = "block";
			document.getElementById("managerDiv").style.display = "block";
			document.getElementById("peerDiv").style.display = "none";
			document.getElementById("otherDiv").style.display = "none";
		} else if(value=="3"){
			document.getElementById("hrDiv").style.display = "block";m
			document.getElementById("managerDiv").style.display = "block";
			document.getElementById("peerDiv").style.display = "block";
			document.getElementById("otherDiv").style.display = "none";
		} else if(value=="4"){
			document.getElementById("hrDiv").style.display = "block";
			document.getElementById("managerDiv").style.display = "block";
			document.getElementById("peerDiv").style.display = "block";
			document.getElementById("otherDiv").style.display = "none";
		} else if(value=="5"){
			document.getElementById("hrDiv").style.display = "none";
			document.getElementById("managerDiv").style.display = "none";
			document.getElementById("peerDiv").style.display = "none";
			document.getElementById("otherDiv").style.display = "block";
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
		
	/* ===start parvez date: 28-02-2023=== */	
		/* if(ansType==='11'){
			document.getElementById("caculationBasisTr").style.display = 'table-row';
		} else{
			document.getElementById("caculationBasisTr").style.display = 'none';
		} */
	/* ===end parvez date: 28-02-2023=== */
	}
	
	function validateScore(value1, weightageid, id, remweightageid) {
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
		
		  if(parseFloat(value1) > parseFloat(remainweight)){
				alert("Entered value greater than Weightage");
				document.getElementById(weightageid).value = remainweight;
		  } else if(parseFloat(value1) <= 0 ){
			alert("Invalid Weightage");
			document.getElementById(weightageid).value = remainweight;
		  }
	}
	
	function validateScore1(value1, weightageid, remweightageid) {
		var weightCnt = 0;
		if(remweightageid == 'weightage') {
			weightCnt = questionCnt;
		} else if(remweightageid == 'scoreCardWeightage') {
			weightCnt = scoreCnt;
		}
		//alert("weightCnt ===>> "+ weightCnt+ " -- remweightageid ===>> " + remweightageid);
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
	//===start parvez date: 09-12-2021===	
		}else if(parseFloat(value1) < 0 ){
	//===end parvez date: 09-12-2021=== 
			alert("Invalid Weightage");
			document.getElementById(weightageid).value = remainWeightage;
		}  
	}
	
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
	
	
	function closeForm() {
		$("#divReviewsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  	 	$.ajax({ 
  		   url: "AppraisalDashboard.action?fromPage=R",
  		   cache: true,
  		   success: function(result){
  			  //alert("result1==>"+result);
  			  $("#divReviewsResult").html(result);
	   		}
  	    });
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
	Map<String, String> orientPosition = (Map<String, String>)request.getAttribute("orientPosition");
 	String attribute = (String) request.getAttribute("attribute");
	String anstype = (String) request.getAttribute("anstype");
	String id = (String) request.getAttribute("id");
	String step = (String) request.getAttribute("step");
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	//System.out.println("id===> " + id);
	
%>

<section class="notranslate content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<div class="reportWidth">

				<s:form action="CreateAppraisal" id="formID" name="formID" method="POST" theme="simple">
					<s:if test="id == null">
						<s:hidden name="step"></s:hidden>
						<div class="steps">
							<span class="current"> Review Details: &nbsp;&nbsp;</span>
							<span class="next"> Assign to a team: &nbsp;&nbsp;</span>
							<span class="next"> Review System: </span>
						</div>
						<table class="table" width="100%">

							<tr>
								<th width="15%" style="text-align: right">Review Name:<sup>*</sup>
								</th>
								<td colspan="6"><s:textfield cssClass="validateRequired form-control " name="appraiselName" cssStyle="display:inline;"></s:textfield> 
									<span><a href="javascript: void(0)" onclick="createExistAppraisalPopup();" style="float: right;">Choose from Existing Review</a> </span>
								</td>
							</tr>

							<tr>
								<th style="text-align: right">Review Type:<sup>*</sup>
								</th>
								<td colspan="6"><s:select theme="simple" cssClass="validateRequired form-control " name="appraisalType"
										headerKey="" list="#{'Annual Review':'Annual Review', 'Monthly Review':'Monthly Review','Mid Term Review':'Mid Term Review', 'Ad hoc Review':'Ad hoc Review',
	                   					'Feedback':'Feedback', 'Review':'Review'}" />
								</td>
							</tr>

							<tr>
								<th style="text-align: right" valign="top">Description:</th>
								<td colspan="6"><textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="appraisal_description" class="form-control "></textarea></td>  <!-- id="editor2" -->
							</tr>
							<tr>
								<th style="text-align: right" valign="top">Instructions:</th>
								<td colspan="6"><textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="appraisal_instruction" class="form-control "></textarea></td>  <!-- id="editor1" -->
							</tr>

							<tr>
								<th style="text-align: right" valign="top">Select Frequency:</th>
								<td colspan="6">
									<div style="position: reletive;">
										<span style="float: left; margin-right: 20px"> 
											<s:select theme="simple" name="frequency" list="frequencyList" listKey="id" listValue="name"
												cssClass=" validateRequired form-control " onchange="checkFrequency(this.value)" /> </span> 
										<span id="weekly" style="display: none; float: left;">
											<%-- Day:<sup>*</sup>  --%>
											<s:select theme="simple" name="weekday" cssStyle="width:100px;display: inline;"
												cssClass=" validateRequired" headerKey="" headerValue="Select Day"
												list="#{'Monday':'Monday','Tuesday':'Tuesday', 'Wednesday':'Wednesday','Thursday':'Thursday','Friday':'Friday','Saturday':'Saturday','Sunday':'Sunday'}" />
										</span> <span id="annualy" style="display: none; float: left;">
											<s:select theme="simple" name="annualDay" cssClass="validateRequired " cssStyle="width:55px;"
												headerKey="" headerValue="Select DDay"
												list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
	                         					'11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
	                          					'21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />

											<s:select theme="simple" name="annualMonth" cssStyle="width:110px;" headerKey=""
												cssClass="form-control " headerValue="Select Month"
												list="#{'January':'January','February':'February', 'March':'March','April':'April','May':'May',
	                          					'June':'June','July':'July','August':'August','September':'September', 'October':'October','November':'November','December':'December'}" />
										</span> <span id="monthly" style="display: none; float: left;">
											<%-- Date of Month:<sup>*</sup>  --%> 
											<s:select theme="simple" name="day" cssClass="validateRequired " cssStyle="width:110px;display: inline;" headerKey=""
												headerValue="Select Date" list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
		                          			'11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
		                        			'21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
										</span> <span id="dayMonth" style="display: none; float: left; height: 70px;">
											<s:select theme="simple" name="monthday" cssClass="validateRequired " cssStyle="width:55px;" headerKey="" headerValue="Day"
												list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
		                         			'11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
		                         			'21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />

											<s:select theme="simple" name="month" headerKey="" headerValue="Select First Month" multiple="true" size="4"
												cssClass="validateRequired form-control " list="#{'January':'January','February':'February', 'March':'March','April':'April','May':'May',
	                          				'June':'June','July':'July','August':'August','September':'September', 'October':'October','November':'November','December':'December'}" />
										</span> <span id="quarterly" style="display: none; float: left; margin-left: 210px;"><i>
												Eg. 'Jan' as first month, Qrt1: Jan, Feb, Mar <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
												'Feb' as first month, Qrt1: Feb, Mar, Apr</i> </span> 
										<span id="halfYearly" style="display: none; float: left; margin-left: 210px;"><i>
												Eg. 'Jan' as first month, Half1: Jan, Feb, Mar, Apr, May,
												Jun <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 'Feb' as first
												month, Half1: Feb, Mar, Apr, May, Jun, Jul</i> </span>
									</div></td>
							</tr>
							<tr>
								<th style="text-align: right"> Start Date:<sup>*</sup></th>
								<td><input type="text" name="from" id="from" class="validateRequired form-control" style="width: 85px !important;" /></td>
								<th style="text-align: right">End Date:<sup>*</sup></th>
								<td colspan="4"><input type="text" name="to" id="to" class="validateRequired form-control" style="width: 85px !important;" /></td>
							</tr>
							<tr>
								<th style="text-align: right">Eligibility min days before start date:<sup>*</sup></th>
								<td><input type="text" name="eligibilityMinDaysBeforeStartDate" id="eligibilityMinDaysBeforeStartDate" class="validateRequired" style="width: 85px !important;" /></td>
								<th style="text-align: right">OR Eligibility min days <br/> before end date:</th>
								<td colspan="4"><input type="text" name="eligibilityMinDaysBeforeEndDate" id="eligibilityMinDaysBeforeEndDate" style="width: 85px !important;" /></td>
							</tr>
							<%-- <tr>
								<th style="text-align: right">Eligibility min days before start date:<sup>*</sup></th>
								<td><input type="text" name="eligibilityMinDaysBeforeStartDate" id="eligibilityMinDaysBeforeStartDate" class="validateRequired" style="width: 85px !important;" /></td>
								<th style="text-align: right">OR Eligibility min days <br/> before end date:</th>
								<td colspan="4"><input type="text" name="eligibilityMinDaysBeforeEndDate" id="eligibilityMinDaysBeforeEndDate" style="width: 85px !important;" /></td>
							</tr> --%>
							<tr>
								<th style="text-align: right">Anonymous Review:</th>
								<td colspan="3"><input type="checkbox" name="anonymousReview" /></td>
							</tr>
						</table>
					</s:if>

					<s:if test="id != null && step != null && step == 2">
						<script type="text/javascript">
							function addRevieweeForSelfReview(employee) {
								//alert("employee ===>> ");
								if(document.getElementById("hideSelfFillEmpIds")) {
									//alert(" ---->>> ");
									var employeeIds = getSelectedValue(employee);
									//  hideSelfFillEmpIds
									//alert("employeeIds ===>> "+employeeIds);
							        xmlhttp = GetXmlHttpObject();
							        if (xmlhttp == null) {
							            alert("Browser does not support HTTP Request");
							            return;
							        } else {
						                var xhr = $.ajax({
					                        url : "SetSelfFillEmployee.action?empids=" + employeeIds,
					                        cache : false,
					                        success : function(data) {
					                        	//alert("data ===>> " + data);
					                        	if(data == "") {
					                        	} else {
					                        		var allData = data.split("::::");
					                        		document.getElementById("hideSelfFillEmpIds").value = allData[0];
					                        		document.getElementById("lblSelfFillEmpIds").innerHTML = allData[1];
					                                //document.getElementById("statetitle").style.display = 'block';
					                        	}
					                        }
						                });
						        	}
								}
							}
						
							function removeRevieweeForSelfReview(removeEmpid) {
								if(document.getElementById("hideSelfFillEmpIds")) {
									//alert(" ---->>> ");
									var employeeIds = document.getElementById("hideSelfFillEmpIds").value;
									//  hideSelfFillEmpIds
									//alert("employeeIds ===>> "+employeeIds);
							        xmlhttp = GetXmlHttpObject();
							        if (xmlhttp == null) {
							            alert("Browser does not support HTTP Request");
							            return;
							        } else {
						                var xhr = $.ajax({
					                        url : "SetSelfFillEmployee.action?empids=" + employeeIds + "&removeEmpid=" + removeEmpid,
					                        cache : false,
					                        success : function(data) {
					                        	//alert("data ===>> " + data);
					                        	if(data == "") {
					                        	} else {
					                        		var allData = data.split("::::");
					                        		document.getElementById("hideSelfFillEmpIds").value = allData[0];
					                        		document.getElementById("lblSelfFillEmpIds").innerHTML = allData[1];
					                                //document.getElementById("statetitle").style.display = 'block';
					                        	}
					                        }
						                });
						        	}
								}
							}
							
							
							function createRevieweePanelForReview(employee) {
								if(document.getElementById("divAppraiser")) {
									var employeeIds = getSelectedValue(employee);
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
							
							
							function removeRevieweePanelMember(spanName) {
								//alert("spanName ===>> " + spanName);
								document.getElementById(spanName).innerHTML = "";
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
							
							
							function GetXmlHttpObject() {
							    if (window.XMLHttpRequest) {
							            return new XMLHttpRequest();
							    }
							    if (window.ActiveXObject) {
							            return new ActiveXObject("Microsoft.XMLHTTP");
							    }
							    return null;
							}
							
					</script>

						<s:hidden name="step"></s:hidden>
						<s:hidden name="id"></s:hidden>
						<div class="steps">
							<span class="prev"> Review Details: &nbsp;&nbsp;</span>
							<span class="current"> Assign to a team: &nbsp;&nbsp;</span>
							<span class="next"> Review System: </span>
						</div>
						<table class="table table_no_border autoWidth" width="100%">
							<tr>
								<th style="text-align: right">Orientation Type:</th>
								<td colspan="6"><s:select theme="simple" name="oreinted" id="oreinted" cssClass="form-control" list="orientationList" listKey="id" listValue="name" value="orientedValue" onchange="createRevieweePanelForReview('employee');" />  <!-- onchange="showMembersSelect(this.value);" -->
										<span style="float: right;" id="spanOreint"></span>
								</td>
							</tr>
							<% if(strUserType != null && !strUserType.equals(IConstants.MANAGER)) { %>
							<tr>
								<th style="text-align: right" valign="top">Group:</th>
								<td><s:select theme="simple" cssClass="form-control " name="strOrg" list="organisationList" id="strOrg" listKey="orgId" 
									listValue="orgName" required="true" onchange="getOrgLocationDepartLevelDesigGrade();" multiple="true" size="4"></s:select>
								</td>

								<td>
									<div id="wlocDiv">
										<s:select theme="simple" name="strWlocation" list="workList" cssClass="form-control " id="wlocation" 
											listKey="wLocationId" listValue="wLocationName" required="true" value="{userlocation}" 
											onchange="getEmployeebyLocation();" multiple="true" size="4">
										</s:select>
									</div></td>

								<td>
									<div id="departDiv">
										<s:select theme="simple" name="strDepart" cssClass="form-control " list="departmentList" id="depart"
											listKey="deptId" listValue="deptName" required="true" onchange="getEmployeebyDepart();" 
											multiple="true" size="4">
										</s:select>
									</div></td>
							</tr>
							<tr>
								<td></td>
								<td>
									<div id="levelDiv">
										<s:select theme="simple" name="strLevel" list="levelList" cssClass="form-control " listKey="levelId" id="strLevel"
											listValue="levelCodeName" required="true" onchange="getEmployeebyLevel()" multiple="true" size="4">
										</s:select>
									</div></td>

								<td>
									<div id="myDesig">
										<s:select theme="simple" name="strDesignationUpdate" cssClass="form-control " list="desigList" listKey="desigId"
											id="desigIdV" listValue="desigCodeName" onchange="getEmployeebyDesig();" multiple="true" size="4">
										</s:select>
									</div></td>

								<td>
									<div id="myGrade">
										<s:select theme="simple" name="empGrade" list="gradeList" cssClass="form-control " listKey="gradeId"
											listValue="gradeCode" id="gradeIdV" onchange="getEmployeebyGrade();" multiple="true" size="4">
										</s:select>
									</div></td>
							</tr>
							<% } %>

							<tr>
								<th style="text-align: right" valign="top">Reviewee:</th>
								<td id="myEmployee" colspan="3" style="border: 0px !important;">
									<s:select name="employee" cssClass="validateRequired form-control" list="empList" theme="simple" listKey="employeeId" 
										id="employee" listValue="employeeCode" required="true" multiple="true" size="4" onchange="createRevieweePanelForReview('employee');" />  <!-- onclick="addRevieweeForSelfReview('employee');" -->
								</td>
							</tr>

							<!-- <tr>
								<th style="text-align: right">Appraiser:</th>
								<td colspan="6">
									<div id="td_choose_members">
										<input type="hidden" name="hidehrId" id="hidehrId" />
										<div id="hrDiv" style="display: block; float: left; width: 100%;">
											<a href="javascript: void(0);" onclick="showChoosePopup('hidehrId','lblHrid','HR');">Choose HR</a>:&nbsp;<label id="lblHrid">Not Choosen</label>
										</div>
									</div>
								</td>
							</tr> -->
							
							<tr>
								<th style="text-align: right">Appraiser:</th>
								<td colspan="6">
									<div id="divAppraiser"> Please select reviewee. </div>
								</td>
							</tr>
							
							<tr>
								<th style="text-align: right">Reviewer:</th>
								<td colspan="6">
									<div id="div_Reviewer">
									<s:select name="reviewerId" cssClass="form-control " list="reviewerList" theme="simple" listKey="employeeId" 
										id="reviewerId" listValue="employeeCode" multiple="true" size="4" />
									</div>
								</td>
							</tr>
						</table>
					</s:if>

					<s:if test="id != null && step != null && step != 2">
						<div class="steps">
							<span class="prev"> Review Details: &nbsp;&nbsp;</span>
							<span class="prev"> Assign to a team: &nbsp;&nbsp;</span> 
							<span class="current"> Review System: </span>
						</div>
						<%
   
    				List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
   					List<String> appTiltleList = (List<String>) request.getAttribute("appTiltleList");
   					if(appraisalList == null) appraisalList = new ArrayList<String>();
   				    if(appTiltleList == null) appTiltleList = new ArrayList<String>();
%>
						<% if(appraisalList != null && !appraisalList.isEmpty() && appraisalList.size()>0) { %>
						<div id="profilecontainer"
							class="clr row row_without_margin margintop20">
							<div class="col-lg-12">
								<div class="box box-default">
									<div class="box-header with-border">
										<h3 class="box-title"><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=appraisalList.get(5)%>&nbsp;&nbsp;<%=appraisalList.get(9)%>&nbsp;&nbsp;<%=appraisalList.get(8)%></h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-minus"></i>
											</button>
											<button class="btn btn-box-tool" data-widget="remove">
												<i class="fa fa-times"></i>
											</button>
										</div>
									</div>
									<!-- /.box-header -->
									<div class="box-body" style="padding: 5px; overflow-y: auto;">

										<div class="con2">
											<div class="holder">
												
												<!-- Start Dattatray -->
												<div class="col-lg-12 col-md-12 col-sm-12" style="margin-top:1%;">
													<div style="float: left; width: 100%;">
														<span
															style="float: left; font-size: 12px; line-height: 32px;">
															<span title="Review Type"><%=appraisalList.get(14)%>,</span>
															<span title="Frequency" ><%=appraisalList.get(7)%>,</span>
															
													         <span title="Effective Date"><%=appraisalList.get(17)%>,</span>
															 <span title="Due Date"><%=appraisalList.get(18)%>,</span>
															
													         <span title="Orientation"><%=appraisalList.get(2)%></span>
														</span>
													</div>
	
													<div style="float: left; width: 100%;">
														<span
															style="float: left; font-size: 12px; line-height: 32px;"><b>Description:&nbsp;&nbsp;
														</b><span class="description"><%=appraisalList.get(15)%></span></span>
													</div>
	
													<div style="float: left; width: 100%;">
														<span
															style="float: left; font-size: 12px; line-height: 32px;"><b>Instruction:&nbsp;&nbsp;
														</b><span class="instruction"><%=appraisalList.get(16)%></span></span>
													</div>
	
												</div>
												<!-- End Dattatray -->
											</div>
										</div>
									</div>
								</div>
								
								<!-- Start Dattatray -->
									<div class="box box-primary" style="border-top: 3px solid #d2d6de;">
									<div class="box-header with-border">  <!-- style="background-color:#d2d6de;" -->
										<!-- created by seema -->
										<h4 class="box-title">Reviewers & Reviewee</h4>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-minus"></i>
											</button>
											<button class="btn btn-box-tool" data-widget="remove">
												<i class="fa fa-times"></i>
											</button>
										</div>
									</div>

									<div class="box-body" style="padding: 5px; overflow-y: auto;">
										<div class="row row_without_margin">
											<div class="col-lg-12 col-md-12 col-sm-12">
												<div style="float: left; width: 100%;">
													<span style="float: left; font-size: 12px; line-height: 32px;">
														<b>Reviewee:&nbsp;&nbsp;</b>
													</span>
													<div class="col-sm-offset-1">
														<span style="float: left; font-size: 12px; line-height: 35px;">
															<%=appraisalList.get(12)%>
														</span>
													</div>
												</div>
												<div style="float: left; width: 100%;">
													<span style=" font-size: 12px; line-height: 35px;"><b>Appraiser:&nbsp;&nbsp; </b><a href="javascript:void(0);" onclick="getRevieweeAppraisers('<%=appraisalList.get(0)%>', '', '');">Click Here</a></span>
												</div>
												<div style="float: left; width: 100%;">
													<span
														style="float: left; font-size: 12px; line-height: 35px;"><b>Reviewer:&nbsp;&nbsp;&nbsp;&nbsp;</b><%=appraisalList.get(23)%></span>
												</div>
												
											</div>
										</div>
									</div>
								</div>
								<!-- End Dattatray -->
								
							</div>
							<%--  <p class="past heading_dash" style="padding-left: 45px; text-align: left"><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=appraisalList.get(5)%>&nbsp;&nbsp;<%=appraisalList.get(9)%>&nbsp;&nbsp;<%=appraisalList.get(8)%></p> --%>
						</div>
						<% } %>
						<%
    				int sectioncnt1=0,subsectioncnt1=0;
       			   //List<List<String>> outerList1 = (List<List<String>>) request.getAttribute("outerList1");
   	 				List<List<String>> mainLevelList = (List<List<String>>) request.getAttribute("mainLevelList");
					Map<String, List<List<String>>> hmSystemLevelMp =(Map<String, List<List<String>>>)request.getAttribute("hmSystemLevelMp");
	                Map<String, List<Map<String, List<List<String>>>>> levelMp = (Map<String, List<Map<String, List<List<String>>>>>) request.getAttribute("levelMp");
             	    UtilityFunctions uF = new UtilityFunctions();
                
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
					//System.out.println("subsectioncnt1==>"+subsectioncnt1+"==>subSectionTotWeightage==>"+subSectionTotWeightage);
			%>
						<%
					if(request.getAttribute("mainlevelTitle")==null) { %>
						<img src="images1/bottom-right-arrow.png" style="width: 20px;margin-left:15px;opacity: 0.6;">Sections
						<%for (int a = 0; mainLevelList != null && a < mainLevelList.size(); a++) {
							List<String> maininnerList = mainLevelList.get(a);
					%>
						<div id="profilecontainer" style="margin: 0px 15px; clear: both;">
							<%-- <p class="past heading_dash" style="padding-left: 45px; text-align: left"><%=maininnerList.get(1)%> </p> --%>
							<div class="box box-primary collapsed-box"
								style="border-top-color: #EEEEEE; margin-top: 10px;">

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

									<div class="con2">
										<div class="holder">
											<div style="overflow: hidden; float: left; width: 100%;">
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
												<div
													style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
													<div style="text-align: left;">
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
	                           							 List<List<String>> goalList = GoalMp .get(innerList.get(0));
	                                               		 for (int k = 0; goalList != null && k < goalList.size(); k++) {
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
	                                                                	List<String> objectivelinnerList = objectiveList .get(l);
	                                						%>
																		<div
																			style="overflow: hidden; float: left; width: 100%;">
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
																		<% }%>
																	</div>
																</div>
																<% }%>
															</div>
														</div>
														<%} %>
													</div>
												</div>
												<%
                		} else if (uF.parseToInt(innerList1.get(2)) == 2) {
                               List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                               Map<String, List<List<String>>> scoreMp = list.get(0);
                               Map<String, List<List<String>>> measureMp = list.get(1);
                               Map<String, List<List<String>>> questionMp = list.get(2);
                               System.out.println("CAp/2927---");
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
																	<div
																		style="overflow: hidden; float: left; width: 100%;">
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
																	<% } %>
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
                                System.out.println("CAp/3048--");
            %>
												<div
													style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
													<div style="text-align: left;">
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
																		<div
																			style="overflow: hidden; float: left; width: 100%;">
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
																				<%} %>
																			</div>
																		</div>
																		<%}%>
																	</div>
																</div>
																<%   } %>
															</div>
														</div>
														<%  } %>
													</div>
												</div>
												<% }
                            }
                          %>
												<%    } %>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<%} %>
						<%} %>
						<input type="hidden" name="appraisalHeadingDivCounter"
							id="appraisalHeadingDivCounter" value="0">
						<s:hidden name="id" id="id"></s:hidden>
						<s:hidden name="step" id="step"></s:hidden>
						<s:hidden name="oreinted"></s:hidden>
						

						<%if(request.getAttribute("mainlevelTitle")!=null) { %>
							<img src="images1/bottom-right-arrow.png" style="width: 20px;margin-left:15px;opacity: 0.6;">Sections
							<%for (int a = 0; mainLevelList != null && a < mainLevelList.size(); a++) {
								List<String> maininnerList = mainLevelList.get(a);
							%>
						<div id="profilecontainer" style="margin:0px 15px; clear: both;">
							<%-- <p class="past heading_dash" style="padding-left: 45px; text-align: left"><%=maininnerList.get(1)%> </p> --%>
							<div class="box box-primary collapsed-box"
								style="border-top-color: #EEEEEE; margin-top: 10px;">

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
									<div class="con2">
										<div class="holder">
											<div style="overflow: hidden; float: left; width: 100%;">
												<%	
									List<List<String>> outerList1 =hmSystemLevelMp.get(maininnerList.get(0));
          						    int jj=0;
               						for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
                						List<String> innerList1 = outerList1.get(i);
                					%>
												<%
				                        if (uF.parseToInt(innerList1.get(3)) == 2) {
				                            List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
				                            Map<String, List<List<String>>> scoreMp = list.get(0);
				            	%>
												<div
													style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
													<div style="text-align: left;">
														<blockquote>
															<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=uF.showData(innerList1.get(1),"") %></strong>
														</blockquote>
													</div>

													<div style="overflow: hidden; float: left; width: 100%;">
														<table class="table" style="width: 100%; float: left;">
															<tr>
																<td width="90%"><b>Question</b></td>
																<td><b>Weightage</b></td>
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
															<%      }%>
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
											                            for (int k = 0; goalList != null && k < goalList.size(); k++) {
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
																		<div
																			style="overflow: hidden; float: left; width: 100%;">
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
																				<% }%>
																			</div>
																		</div>
																		<%} %>
																	</div>
																</div>
																<%} %>
															</div>
														</div>
														<% }%>
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
																	<div
																		style="overflow: hidden; float: left; width: 100%;">
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
																		<div
																			style="overflow: hidden; float: left; width: 100%;">
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
																				<%   } %>
																			</div>
																		</div>
																		<% }%>
																	</div>
																</div>
																<% } %>
															</div>
														</div>
														<% } %>
													</div>
												</div>
												<% }
                          											  }
                          										   }
                    										%>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<%} %>
						<%} %>
						<div id="appraisalHeadingDiv">
							<table class="table table_no_border">
								<%if(request.getAttribute("mainlevelTitle")==null) {
				 sectioncnt1 = sectioncnt1+1;
				 subsectioncnt1 = 1;
				 subSectionTotWeightage = 0;
			  }
			%>
								<tr>
									<th align="right" width="20%">
										<p><%=sectioncnt1 %>.
										</p> Section Title: <%if(request.getAttribute("mainlevelTitle")==null){ %>
										<sup>*</sup> <%} %>
									</th>
									<td colspan="5" style="vertical-align: bottom;"><input
										type="hidden" name="main_level_id" id="main_level_id"
										value="<%=request.getAttribute("main_level_id")%>" /> <%if(request.getAttribute("mainlevelTitle")==null) { %>
										<input type="text" name="levelTitle" id="levelTitle"
										class="validateRequired form-control " style="width: 80%" />
										<%} else {%> <%=request.getAttribute("mainlevelTitle")%> <input
										type="hidden" name="levelTitle" id="levelTitle"
										value="<%=request.getAttribute("mainlevelTitle")%>" /> <%} %>
									</td>
								</tr>

								<tr>
									<th align="right" style="padding-right: 20px" valign="top">Short
										Description:</th>
									<td colspan="5">
										<%if(request.getAttribute("mainshortDesrciption")==null) { %> 
											<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="shortDesrciption"></textarea>  <!-- id="editor1" -->
										<%} else {%> <%=request.getAttribute("mainshortDesrciption")%> 
											<input type="hidden" name="shortDesrciption" id="shortDesrciption" class="form-control " value="<%=request.getAttribute("mainshortDesrciption")%>" />
										<%} %>
									</td>
								</tr>

								<tr>
									<th align="right" style="padding-right: 20px" valign="top">Long Description:</th>
									<td colspan="5">
										<%if(request.getAttribute("mainlongDesrciption")==null) { %>
											<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="longDesrciption" class="form-control"></textarea>  <!-- id="editor2" -->
										<%} else {%>
											<%=request.getAttribute("mainlongDesrciption")%>
											<input type="hidden" name="longDesrciption" id="longDesrciption" value="<%=request.getAttribute("mainlongDesrciption")%>" /> 
										<%} %>
									</td>
								</tr>

								<tr>
									<th align="right" style="padding-right: 20px">Weightage %:
										<% if(request.getAttribute("mainlevelTitle")==null) { %> <sup>*</sup>
										<% } %>
									</th>
									<td><input type="hidden" name="hideSecTotWeight" id="hideSecTotWeight" value="<%=sectionTotWeightage %>" />
										<%if(request.getAttribute("sectionWeightage")==null) { %>
											<input type="text" name="sectionWeightage" id="sectionWeightage" class="validateRequired form-control" value="<%=(100 - sectionTotWeightage) %>" onkeyup="validateSECANDSUBSECScore(this.value,'sectionWeightage','hidesectionWeightage');addNewSection();" onkeypress="return isNumberKey(event)" />
											<input type="hidden" name="hidesectionWeightage" id="hidesectionWeightage" value="<%=(100 - sectionTotWeightage) %>" />
										<% } else { %>
											<%=request.getAttribute("sectionWeightage") %>
											<input type="hidden" name="sectionWeightage" id="sectionWeightage" value="<%=request.getAttribute("sectionWeightage") %>" />
											<input type="hidden" name="hidesectionWeightage" id="hidesectionWeightage" value="<%=request.getAttribute("sectionWeightage") %>" /> 
										<% } %>
									</td>
								</tr>
								<tr>
									<th align="right" style="padding-right: 20px">Select Attribute:</th>
									<td>
										<%if(request.getAttribute("attribname")==null) { %> 
											<select name="attribute" class=" validateRequired form-control "><%=attribute %></select>
										<% } else { %>
											<input type="hidden" name="attribute" id="attribute" value="<%=request.getAttribute("attribid")%>" /> <%=request.getAttribute("attribname")%>
										<%} %>
									</td>
								</tr>

								<tr>
									<th align="right" style="padding-right: 20px">Work Flow:</th>
									<td colspan="5">
										<%
											String member1=(String)request.getAttribute("member");
											String[] memberArray1=member1.split(",");
											for(int i=0;i<memberArray1.length;i++) {
										%>
											<span style="float: left; width: 60px; text-align: center;">Step<%=i+1%></span>
										<% } %>
									</td>
								</tr>
								<%
								String member=(String)request.getAttribute("member");
								//System.out.println("CAp.jsp/3881--member="+member);
								//if(request.getAttribute("oreinted") != null && !request.getAttribute("oreinted").equals("5")) {
									String[] memberArray=member.split(",");
									for(int i=0;i<memberArray.length;i++) {
								%>
								<tr>
									<th align="right" style="padding-right: 20px"><%=memberArray[i]%></th>
									<td colspan="5">
										<%if(request.getAttribute("attribname")==null) { %>
											<%for(int j=1;j<=memberArray.length;j++) { %>
												<span style="float: left; width: 60px; text-align: center;">
													<input type="radio" name="<%=memberArray[i]%>" value="<%=j %>" <%if(j==1){ %> checked="checked" <%} %> />
												</span>
											<% } %> 
										<% } else { %>
											<input type="hidden" name="<%=memberArray[i]%>" <%if(orientPosition != null) { %> value="<%=uF.parseToInt(orientPosition.get(memberArray[i])) %>" <% } else if(orientPosition == null) { %> value="1" <%} %> /> 
											<%for(int j=1;j<=memberArray.length;j++) { %>
												<span style="float: left; width: 60px; text-align: center;">
													<input type="radio" name="<%=memberArray[i]%>" value="<%=j %>" <%if(orientPosition != null && uF.parseToInt(orientPosition.get(memberArray[i]))==j) { %> checked="checked" <%} else if(j==1) { %> checked="checked" <%} %> disabled="disabled" />
												</span>
											<% } %>
										<% } %>
									</td>
								</tr>
								<% }
								//} %>
							</table>
						</div>
						<div id="sectionAssessmentdiv"
							style="box-shadow: 0px 2px 18px 0px rgba(0, 0, 0, 0.18);margin: 5px;padding:5px; display:<%if(subsectioncnt1==1){ %> none;<%}else{%>block;<%}%>">
							<div id="sectiondiv">
								<p
									style="padding-left: 5px; text-align: left; font-size: 16px; margin-bottom: 10px; float: left"><%=sectioncnt1 %>.<%=subsectioncnt1 %>
									Subsection of " <span id="sectionnamespan"> <%if(request.getAttribute("mainlevelTitle")!=null) {%><%=request.getAttribute("mainlevelTitle")%>
										<%} else { %>New Section<%} %> </span> "
								</p>
								<a href="javascript:void(0)"
									onclick="closeSectionAssessDiv('sectionAssessmentdiv')"
									class="close-font pull-right"></a>

								<table class="table table_no_border clr">
									<tr id="sectionnameTr">
										<th align="right" style="padding-right: 20px" width="15%">Subsection Title:<sup>*</sup>
										</th>
										<td colspan="5"><input type="hidden" name="hidesectioncnt" id="hidesectioncnt" value="<%=sectioncnt1%>" /> 
										<input type="hidden" name="hidesubsectioncnt" id="hidesubsectioncnt" value="<%=subsectioncnt1%>" /> 
										<input type="text" name="subsectionname" id="subsectionname" class="validateRequired form-control " style="width: 450px;" />

										</td>
									</tr>

									<tr id="sectionDescTr">
										<th align="right" style="padding-right: 20px" width="15%">Subsection Short Description:</th>
										<td colspan="5"><textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="subsectionDescription" class="form-control "></textarea></td>  <!-- id="editor3" -->
									</tr>

									<tr id="sectionLongDescTr">
										<th align="right" style="padding-right: 20px" width="15%">Subsection Long Description:</th>
										<td colspan="5"><textarea rows="3" cols="72" style="width: 100% !important; resize: none;" class="form-control" name="subsectionLongDescription"></textarea></td>  <!-- id="editor4" -->
									</tr>

									<tr>
										<th align="right" style="padding-right: 20px;">Weightage %:<sup>*</sup></th>
										<td><input type="hidden" name="hideSubSecTotWeight" id="hideSubSecTotWeight" value="<%=subSectionTotWeightage%>" />
											<input type="number" name="subSectionWeightage" id="subSectionWeightage" class="validateRequired form-control" value="<%=(100 - subSectionTotWeightage) %>"
											onkeyup="validateSECANDSUBSECScore(this.value,'subSectionWeightage','hidesubSectionWeightage');addNewSection();" onkeypress="return isNumberKey(event)" /> 
											<input type="hidden" name="hidesubSectionWeightage" id="hidesubSectionWeightage" value="<%=(100 - subSectionTotWeightage) %>" /></td>
									</tr>
								</table>
							</div>

							<div>
								<ul class="level_list ul_class">
									<li>
										<div style="margin-top: 10px; background-color: lemonchiffon; border: 1px solid; padding: 10px;">
											<table class="table table_no_border">
												<tr>
													<td>System:<Sup>*</Sup>
													</td>
											<!-- ===start parvez date: 13-01-2022=== -->
												<td>
												<% if(member.contains("Manager")){ %>	
													 
													<s:select theme="simple" name="appraisalSystem" headerKey="" headerValue="Select System"
															list="#{'1':'Competency, Goal & Objective Martix','2':'Other','3':'Goal','4':'KRA','5':'Target','6':'New Goal'}"
															onchange="showSystem(this.value,'');" cssClass="validateRequired" />
												<%} else{ %>
													<s:select theme="simple" name="appraisalSystem" headerKey="" headerValue="Select System"
															list="#{'1':'Competency, Goal & Objective Martix','2':'Other','3':'Goal','4':'KRA','5':'Target'}"
															onchange="showSystem(this.value,'');" cssClass="validateRequired" />
												<%} %>											
											<!-- ===end parvez date: 13-01-2022=== -->		
													<% if(request.getAttribute("type") != null) { %>
														<span id="assessdiv<%=(request.getAttribute("divCount"))%>"><a href="javascript:void(0)" onclick="closeEditDiv('<%=request.getAttribute("sysdiv") %>','<%=request.getAttribute("linkDiv") %>')" class="close-font" /></span>
													<% } else { %>
														<span id="assessdiv<%=(request.getAttribute("divCount"))%>"></span>
													<% } %>
													</td>
													<td></td>
												</tr>
												<tr id="scoreCardID" style="display: none;">
													<td>Competency<sup>*</sup>
													</td>
													<td><s:select theme="simple" name="scoreCard" headerKey="" headerValue="Select Competency" id="scoreCard" cssClass="validateRequired"
															onchange="getcontent(this.value);" list="#{'1':'Competencies + Goals + Objectives + Measures', '3':'Competencies + Goals + Measures','2':'Competencies + Measures'}" />
													</td>
													<td></td>
												</tr>
												<tr id="otherqueTypeTr" style="display: none;">
													<td>Question Type:<sup>*</sup>
													</td>
											
										<!-- ===start parvez date: 21-12-2021=== -->
										
													<%-- <td><s:select theme="simple" name="otherQuestionType" id="otherQuestionType" cssClass="validateRequired"
															list="#{'Without Short Description':'Without Short Description', 'With Short Description':'With Short Description'}" />
																
													</td> --%>
											
													<td id="otherQuestionTypeTd" >
														<s:select theme="simple" name="otherQuestionType" id="otherQuestionType" cssClass="validateRequired"
															list="#{'Without Short Description':'Without Short Description', 'With Short Description':'With Short Description'}" />
																
													</td>
													<td id="pgQuestionTd" style="display:none" >
														<input type="text" name="pgTask" id="pgTask" class="form-control"></input>
													</td>
										<!-- ===end parvez date: 21-12-2021=== -->
													<td></td>
												</tr>
												
												<tr id="anstypeTr">
													<td>Select Answer Type</td>
													<td><select name="ansType" id="ansType" onchange="showAnswerTypeDiv(this.value)"><%=anstype %></select>
													</td>
													<td>
														<div id="anstypediv">
															<div id="anstype9">
																a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled" /> 
																b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled" /><br /> 
																c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled" /> 
																d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled" /><br />
															</div>
														</div></td>
												</tr>
										<!-- ===start parvez date: 27-02-2023=== -->	
												<%-- <tr id="caculationBasisTr" style="display: none;">
													<td>Calculation (On basis of actual or 100%): 
													</td>
													
													<td id="caculationBasisTd" >
														<input type="checkbox" name="chkCaculationBasis" id="chkCaculationBasis" />
													</td>
													<td>
													</td>
										
													<td></td>
												</tr> --%>
										<!-- ===end parvez date: 27-02-2023=== -->		
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

							<div id="mainDiv" style="margin: 10px 0px 0px 0px; width: 100%;"></div>
							<div id="assessOfSubsectionDiv" style="width: 100%;"></div>
							<div id="otherDiv" style="width: 100%; display: none;">
								<ul class="level_list ul_class">
									<li><a href="javascript:void(0)" onclick="getOtherquestion('0');"><i class="fa fa-plus-circle"></i>Assess info/points</a></li>
									<li id="otherQuestionLi"></li>
								</ul>
							</div>

							<div class="sectionfont" id="goalDiv" style="display: none;"></div>
						</div>

						<div style="float: left; margin-top: 15px; width: 100%;">
							<span id="sectionLinkDiv" style="float: left; margin-left: 100px; display:<%if(subsectioncnt1==1){ %> block;<%}else{%>none;<%}%>">
								<a href="javascript:void(0)" title="Add Subsections" onclick="openSectionDiv('section');"> +Subsections </a>
							</span>
							<span id="assessLinkDiv" style="float: left; margin-left: 100px;">
								<a href="javascript:void(0)" title="Add Assessments" onclick="openSectionDiv('assessment');"> +Assessments </a>
							</span>
						</div>
					</s:if>

					<div id="firstdiv" style="float: left; margin-top: 20px;">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="step==3">
							<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
					<!-- ===start parvez date: 13-12-2021=== -->	
							<s:submit value="Save & Add New Section" cssClass="btn btn-primary" name="saveandnew"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
					<!-- ===end parvez date: 13-12-2021=== -->
						</s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
					</div>

					<div id="seconddiv" style="float: left; margin-top: 20px; display: none">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="id != null">
							<s:submit value="Save & Add New Section" cssClass="btn btn-primary" name="saveandnew"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
							<s:submit value="Save & Add New Subsection" cssClass="btn btn-primary" name="saveandnewsystem"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						</s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
					</div>

					<div id="secWeightdiv" style="float: left; margin-top: 20px; display: none">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="id != null">
							<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
							<s:submit value="Save & Add New Subsection" cssClass="btn btn-primary" name="saveandnewsystem"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						</s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
					</div>

					<div id="subSecWeightdiv" style="float: left; margin-top: 20px; display: none">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="id != null">
							<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
							<s:submit value="Save & Add New Section" cssClass="btn btn-primary" name="saveandnew"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
					 	</s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
					</div>

					<div id="subSecSecWeightdiv" style="float: left; margin-top: 20px; display: none">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="id != null">
							<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
					<!-- ===start parvez date: 13-12-2021=== -->
							<s:submit value="Save & Add New Section" cssClass="btn btn-primary" name="saveandnew"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
					<!-- ===end parvez date: 13-12-2021=== -->
						</s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
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


<g:compress>
	<script>
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
	</script>
</g:compress>

<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"></h4>
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
            	<button type="button" id="close1" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title modal-title1"></h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


<script>

$("input[type='submit']").click(function(){
	//$(".formIDvalidateRequired").prop('required',true);
	$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
	$("#formID").find('.validateRequired').filter(':visible').prop('required',true);
	for ( instance in CKEDITOR.instances ) {
        CKEDITOR.instances[instance].updateElement();
    }
});

/* var step = $("input[name = 'step']").val();
alert("step==>"+step);
if(step != "" && parseInt(step) == 3 ) { */
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
	       } else if(submit != null && submit == "submit") {
	     	  form_data = form_data +"&submit=Save";
	       }else if(submit != null && submit == "saveandnewsystem"){
	     	  form_data = form_data +"&saveandnewsystem=Save And Add New Subsection";
	       }else if(submit != null && submit == "saveandnew"){
	     	  form_data = form_data +"&saveandnew=Save And Add New Section";
	       }
		   
		  // alert("form_data==>"+form_data);
		$("#divReviewsResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
		      $.ajax({
	     		type: 'POST',
	     		url: "CreateAppraisal.action",
	     		data: form_data,
	     	    success: function(result){
	     	    	//alert("result==>"+result);
	     			$('#divReviewsResult').html(result);
	     		},
				error: function(result){
					$.ajax({
						url: 'AppraisalDashboard.action',
						cache: true,
						success: function(result){
							$("#divReviewsResult").html(result);
				   		}
					});
				}
			});
		});

	/* function getReviewSummary(strAction,appId,appFreqId,fromPage){
		//alert("ReviewDetails App summary jsp strAction ===>> " + strAction+"==>appId==>"+appId+"==appFreqId==>"+appFreqId);
		$("#reviewInfo").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?id='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage,
			data: $("#"+this.id).serialize(),
			cache: true,
			success: function(result){
				//alert("result3==>"+result);
				$("#reviewInfo").html(result);
	   		}
		});
	} */ 

</script>