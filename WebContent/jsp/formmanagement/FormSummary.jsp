<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%
    UtilityFunctions uF = new UtilityFunctions();
    String strTitle = (String) request.getAttribute(IConstants.TITLE);
    String anstype = (String) request.getAttribute("anstype");
    
    String formId = (String) request.getAttribute("formId");
    Map<String, String> hmForm = (Map<String, String>)request.getAttribute("hmForm");
    if(hmForm == null) hmForm = new HashMap<String, String>();
    List<Map<String, String>> alSection = (List<Map<String, String>>)request.getAttribute("alSection");
    if(alSection == null) alSection = new ArrayList<Map<String,String>>();
    Map<String, List<Map<String, String>>> hmSectionQuestion = (Map<String, List<Map<String, String>>>)request.getAttribute("hmSectionQuestion");
    if(hmSectionQuestion == null) hmSectionQuestion = new HashMap<String, List<Map<String,String>>>();
    %>
<script src="scripts/ckeditor_cust/ckeditor.js"></script>  
<g:compress>
    <link href="scripts/ckeditor/samples/sample.css" rel="stylesheet">
    <script type="text/javascript">
        // The instanceReady event is fired, when an instance of CKEditor has finished
        // its initialization.  
        CKEDITOR.on( 'instanceReady', function( ev ) {
        	// Show the editor name and description in the browser status bar.
        	document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
        
        	// Show this sample buttons.
        	document.getElementById( 'eButtons' ).style.display = 'block';
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
        	document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
        }
        
        function onBlur() {
        	document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
        }
        
        
        CKEDITOR.config.width='700px';
        
        
        
    </script>
</g:compress>
<style>
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
</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">

	$(document).ready(function(){
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
		$("#frmFormSummary_submit").click(function(){
			$(".validateRequired").prop('required',true);
			$(".validateNumber").prop('type','number');	 		
		});
	});

    var cxtpath='<%=request.getContextPath()%>';
    
    $(document).ready( function () {
    	jQuery("#frmFormSummary").validationEngine();
    });	
    
    
    function isNumberKey(evt){
       var charCode = (evt.which) ? evt.which : event.keyCode;
       if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
          return false;
       }
       return true;
    }
    
    function showAnswerTypeDiv(ansType) {
    	document.getElementById("otherQuestionMainDiv").innerHTML = '';
    	
    	var action = 'ShowAnswerType.action?ansType=' + ansType;
    	getContent("anstypediv", action);
    }
    
    var dialogEdit = '#SelectQueDiv';
    function openQuestionBank(count) {
    	
    	var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Question Bank');
    	var ansType=document.getElementById('ansType').value;
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
                        //document.getElementById("statetitle").style.display = 'block';
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
    
    var questionCnt = 0;
    function getQuestion(oldcnt){
    	var totweight=0;
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
    		questionCnt++;
    		var cnt=questionCnt;
    		var questContentType = getQuestoinContentType(cnt);
    		var divtag = document.createElement('div');
    		divtag.id = "otherQuestionDiv"+cnt;
    		
    		var divData = "<table class=\"table table-striped table-bordered\" width=\"100%\">"
    		+"<tr><th>1."+questionCnt+")</th><th width=\"17%\" style=\"text-align: right;\">Add Question<sup>*</sup></th>"
    		+"<td colspan=\"3\">"
    		+"<span id=\"newquespan"+cnt+"\" style=\"float: left;\"><input type=\"hidden\" name=\"hidequeid\" id=\"hidequeid"+cnt+"\" value=\"0\"/>"
    		+"<textarea rows=\"2\" name=\"question\" id=\"question"+cnt+"\" class=\"validateRequired\" style=\"width: 330px !important;\"></textarea>"
    		+"</span>&nbsp;"
    		+"<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt\" value=\""+cnt+"\"/><sup>*</sup>"
    		+"<input type=\"text\" style=\"width: 35px !important;\" name=\"weightage\" id=\"weightage"+cnt+"\" class=\"validateNumber\" value=\""+remainweight+"\" onkeyup=\"validateScore1(this.value,'weightage"+cnt+"','weightage');\" onkeypress=\"return isNumberKey(event)\"/>"
    		+"<input type=\"hidden\" name=\"hideweightage\" id=\"hideweightage"+cnt+"\" value=\"100\"/></span>&nbsp;&nbsp;"
    		+"<span style=\"float: left; margin-left: 10px;\">"
    		+"<a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"');\" > +Q </a>"
    		+"</span>&nbsp;"
    		+"<span id=\"checkboxspan"+cnt+"\" style=\"float: left; margin-left: 10px;\">"
    		+"<input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+cnt+"\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+cnt+"')\"/>"
    		+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/>"
    		+"</span>"
    		+"<a href=\"javascript:void(0)\" class=\"add_lvl\" title=\"Add New Question\" onclick=\"getQuestion('"+cnt+"')\" ></a>&nbsp;&nbsp;"
    		/* +"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionDiv"+cnt+"')\"/>" */
    		+"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" style=\"height: 16px; width: 16px;\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionDiv"+cnt+"')\"></i>"
    		+"<input type=\"hidden\" name=\"questiontypename\" value=\""+cnt+"\"/>"
    		+"</td></tr>"
    		+questContentType
    		+"</table>";
    		
    		divtag.innerHTML = divData;
    		
    		document.getElementById("otherQuestionMainDiv").appendChild(divtag);
    		
    		jQuery("#frmFormSummary").validationEngine();
    	}
    }
    
    function getQuestoinContentType(cnt){
    	var val = document.getElementById("ansType").value;
    	var a="";
    	if( val == 8) {
    		a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
    		+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
    	
    	} else if (val == 1 || val == 2 || val == 9) {
    		a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<span id=\"aspan\"><input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\"/></span> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<span id=\"bspan\"><input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
    		+"<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<span id=\"cspan\"><input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<span id=\"dspan\"><input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
    
    	 } else if (val == 6) {
    		a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
    			+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td></tr>";
    	
    	} else if (val == 5) {
    		a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
    		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td></tr>";
    	} else if(val == 13) {
			a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
			+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>"
			+ "<tr id=\"answerType2"+cnt+"\"><th></th><th></th><td>e)&nbsp;<input type=\"text\" name=\"optione\" id=\"optione"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" /></td><td colspan=\"2\">&nbsp;</td></tr>";
		
		} else {
    		a="";
    	}
    	return a;
    } 
    
    function validateScore1(value1, weightageid, remweightageid) {
    	var weightCnt = 0;
    	if(remweightageid == 'weightage') {
    		weightCnt = questionCnt;
    	} 
    
    	var totweight=0;
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
    		
    		document.getElementById(weightageid).value = ''+remainweight;
    	}else if(parseFloat(value1) <= 0 ){
    		alert("Invalid Weightage");
    		
    		document.getElementById(weightageid).value = ''+remainweight;
    	}  
    }
    
    function changeStatus(id) {
    	if (document.getElementById('addFlag' + id).checked == true) {
    		document.getElementById('status' + id).value = '1';
    	} else {
    		document.getElementById('status' + id).value = '0';
    	}
    }
    
    function removeOtherquestion(id){
    	var row_skill = document.getElementById(id);
    	if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
    		row_skill.parentNode.removeChild(row_skill);
    	}
    }
    
    function closeForm() {
    	var formId = document.getElementById("formId").value;
    	var strOrg = document.getElementById("strOrg").value;
    	var userscreen = document.getElementById("userscreen").value;
    	var navigationId = document.getElementById("navigationId").value; 
    	var toPage = document.getElementById("toPage").value;
    	window.location = "FormSummary.action?formId="+formId+"&strOrg="+strOrg+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage;
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
    
    function showSection(){
    	document.getElementById("sectionDiv").style.display = 'block';
    }
    
    function editForm(formId,strOrg,userscreen,navigationId,toPage) {
    	var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Edit Form');
		 $.ajax({
				url : "EditForm.action?formId="+formId+"&strOrg="+strOrg+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
    }
    
    function editSection(formId,sectionId,sectionName,strOrg,userscreen,navigationId,toPage) {
    	
    	var totalWeightage = document.getElementById("totalWeightage").value;
    	var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Edit Section');
		 if($(window).width() >= 900){
			 $(".modal-dialog").width(900);
		 }
		 $.ajax({
				url : "EditSection.action?formId="+formId+"&sectionId="+sectionId+"&totalWeightage="+totalWeightage+"&strOrg="+strOrg+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
    }
    
    function editQuestion(formId,sectionId,sectionQuestId,strOrg,userscreen,navigationId,toPage) {
    	var totalQuestWeightage = document.getElementById("totalQuestWeightage_"+sectionId).value;
    	var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Edit Question');
		 $.ajax({
				url : "EditQuestion.action?formId="+formId+"&sectionId="+sectionId+"&sectionQuestId="+sectionQuestId+"&totalQuestWeightage="+totalQuestWeightage+"&strOrg="+strOrg+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
    }
    
    function addNewSectionQuestion(value,formId,sectionId,totWeightage) {
    	 var remainWeightage = 100 - parseFloat(totWeightage);
    	 if(parseInt(remainWeightage) <= 0){
    		 alert("Unable to add questions because of no weightage available ");
    	 }else{
    		document.getElementById("editweightage"+value).value=remainWeightage;
    		document.getElementById("hideeditweightage"+value).value=remainWeightage;
    	    document.getElementById("addNewQuestion_"+formId+"_"+sectionId+"_"+value).style.display="block";
    	 }
    
    }
    
    function validateEditQuest(value1,weightageid,weightagehideid) {
    	var remainWeightage = document.getElementById(weightagehideid).value;
    	  
    	  if(parseFloat(value1) > parseFloat(remainWeightage)){
    			alert("Entered value greater than Weightage");
    			document.getElementById(weightageid).value = remainWeightage;
    		}else if(parseFloat(value1) <= 0 ){
    			alert("Invalid Weightage");
    			document.getElementById(weightageid).value = remainWeightage;
    		}  
    }
    
</script>
<%-- 
    <jsp:include page="../common/SubHeader.jsp">
    	<jsp:param value="<%=strTitle %>" name="title" />
    </jsp:include> --%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                        <div style="float:right; margin:0px 0px 10px 0px">
                            <a href="MyDashboard.action?strOrg=<%=request.getAttribute("strOrg") %>&userscreen=<%=request.getAttribute("userscreen") %>&navigationId=<%=request.getAttribute("navigationId") %>&toPage=<%=request.getAttribute("toPage") %>">Go Back to Form Management</a>
                        </div>
                        <div style="width:100%; float:left">
                            <table class="table table_no_border" style="float: left; width: 25%;">
                                <tr>
                                    <th class="txtlabel alignRight">
                                        <a href="javascript: void(0)" onclick="editForm('<%=formId%>','<%=request.getAttribute("strOrg") %>','<%=request.getAttribute("userscreen") %>','<%=request.getAttribute("navigationId") %>','<%=request.getAttribute("toPage") %>')" title="Edit Form"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                        <a class="del" title="Delete" href="AddForm.action?operation=D&formId=<%=formId%>&strOrg=<%=request.getAttribute("strOrg") %>&userscreen=<%=request.getAttribute("userscreen") %>&navigationId=<%=request.getAttribute("navigationId") %>&toPage=<%=request.getAttribute("toPage") %>" onclick="return confirm('Are you sure you want to delete this form?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                        Form Title:
                                    </th>
                                    <td><%=uF.showData(hmForm.get("FORM_NAME"),"") %></td>
                                </tr>
                                <tr>
                                    <th class="txtlabel alignRight">Organisation:</th>
                                    <td><%=uF.showData(hmForm.get("FORM_ORG_NAME"),"") %></td>
                                </tr>
                                <tr>
                                    <th class="txtlabel alignRight">Form Node:</th>
                                    <td><%=uF.showData(hmForm.get("FORM_NODE"),"") %></td>
                                </tr>
                            </table>
                        </div>
                        <div style="width:100%; float:left;width: 100%; float: left; color: #346897; border-bottom: 1px solid #346897"><strong>Section Details (<%=alSection.size() %>)</strong></div>
                        <%
                            int nWeightage = 0;
                            if(uF.parseToInt(formId) > 0){ 
                            	for(int i = 0; alSection !=null && i < alSection.size(); i++ ){	
                            		Map<String, String> hmSection = (Map<String, String>) alSection.get(i);
                            		nWeightage += uF.parseToDouble(hmSection.get("SECTION_WEIGHTAGE")); 
                            %>
                        <div style="overflow: hidden; float: left; width: 100%;  text-align: left; margin-bottom:10px; margin-top: 10px;">
                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                                <div class="box-header with-border" style="padding-bottom: 5px;padding-top: 5px;">
                                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                                        <%=i+1%>)&nbsp;<%=uF.showData(hmSection.get("SECTION_NAME"),"")%> 
                                        <span style="float: right;">
                                        <strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(hmSection.get("SECTION_WEIGHTAGE"),"0")%>%&nbsp;&nbsp;
                                        <strong>Answer Type :-&nbsp;&nbsp;</strong> <%=uF.showData(hmSection.get("SECTION_ANSWER_TYPE"),"")%>
                                        </span>
                                        <ul id="" class="ul_section">
                                            <li>
                                                <%=uF.showData(hmSection.get("SECTION_SHORT_DESCRIPTION"), "")%>
                                            </li>
                                        </ul>
                                        <span style="float: right;">
                                        <a href="javascript: void(0)" onclick="editSection('<%=formId %>','<%=hmSection.get("SECTION_ID") %>','<%=hmSection.get("SECTION_NAME") %>','<%=request.getAttribute("strOrg") %>','<%=request.getAttribute("userscreen") %>','<%=request.getAttribute("navigationId") %>','<%=request.getAttribute("toPage") %>');" title="Edit Section"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                        <a class="del" title="Delete" href="FormSummary.action?operation=D&formId=<%=formId %>&sectionId=<%=hmSection.get("SECTION_ID") %>&strOrg=<%=request.getAttribute("strOrg") %>&userscreen=<%=request.getAttribute("userscreen") %>&navigationId=<%=request.getAttribute("navigationId") %>&toPage=<%=request.getAttribute("toPage") %>" onclick="return confirm('Are you sure you want to delete this section?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                        </span>
                                        <span style="float: right; margin-right: 10px; font-weight: normal; font-style: italic;font-size: 12px;">Last Updated By -&nbsp;<%=uF.showData(hmSection.get("SECTION_ADDED_BY"),"")%>&nbsp;<%=uF.showData(hmSection.get("SECTION_ENTRY_DATE"),"")%></span>
                                    </h3>
                                    <div class="box-tools pull-right">
                                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                    </div>
                                </div>
                                <!-- /.box-header -->
                                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                    <div class="content1">
                                        <%
                                            List<Map<String, String>> alSecQueList = (List<Map<String, String>>) hmSectionQuestion.get(hmSection.get("SECTION_ID"));
                                            int nTotalQuestWeightage = 0;
                                            
                                            %>
                                        <div style="overflow: hidden;float: left;  width: 100%; margin-top: 15px;">
                                            <table class="table table-striped table-bordered" style="width: 100%; float: left;">
                                                <tr>
                                                    <td width="90%"><b>Question</b></td>
                                                    <td><b>Weightage</b></td>
                                                </tr>
                                                <%
                                                int nQuestWeightage = 0;
                                                if(alSecQueList != null && alSecQueList.size() > 0){
                                                    for(int j = 0; j < alSecQueList.size(); j++){
                                                    	Map<String, String> hmSecQuestion = (Map<String, String>) alSecQueList.get(j);
                                                    	nQuestWeightage += uF.parseToDouble(hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")); 
                                                    %>
	                                                <tr>
	                                                    <td>
	                                                        <span style="float: left;">
	                                                        <%=i+1%>.<%=j+1%>)&nbsp;<%=uF.showData(hmSecQuestion.get("SECTION_QUEST_QUESTION_NAME"),"")%>
	                                                        </span>
	                                                        <span style="float: left; margin-left: 10px;">
	                                                        <a href="javascript:void(0)" onclick="editQuestion('<%=formId %>','<%=hmSection.get("SECTION_ID") %>','<%=hmSecQuestion.get("SECTION_QUEST_ID") %>','<%=request.getAttribute("strOrg") %>','<%=request.getAttribute("userscreen") %>','<%=request.getAttribute("navigationId") %>','<%=request.getAttribute("toPage") %>');" title="Edit Question"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
	                                                        <a class="del" title="Delete" href="FormSummary.action?operation=D&formId=<%=formId %>&questionId=<%=hmSecQuestion.get("SECTION_QUEST_ID") %>&strOrg=<%=request.getAttribute("strOrg") %>&userscreen=<%=request.getAttribute("userscreen") %>&navigationId=<%=request.getAttribute("navigationId") %>&toPage=<%=request.getAttribute("toPage") %>" onclick="return confirm('Are you sure you want to delete this Question?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
	                                                        </span>
	                                                    </td>
	                                                    <td style="text-align: right"><%=uF.showData(hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE"),"")%>%</td>
	                                                </tr>
	                                                <%}%>
	                                                <tr>
	                                                    <td colspan="2"><input type="hidden" name="totalQuestWeightage" id="totalQuestWeightage_<%=hmSection.get("SECTION_ID") %>" value="<%=nQuestWeightage%>"/></td>
	                                                </tr>
	                                                <%
	                                                  nTotalQuestWeightage = nQuestWeightage;
	                                                  //System.out.println("SECTION_ID==>" +hmSection.get("SECTION_ID") +"==>nTotalQuestWeightage==>"+nTotalQuestWeightage);
	                                                  if(nQuestWeightage < 100){
	                                                  %>
		                                                <tr>
		                                                    <td colspan="2">
		                                                        <span><a href="javascript:void(0);" onclick="addNewSectionQuestion('<%=i %>','<%=formId %>','<%=hmSection.get("SECTION_ID") %>','<%=nQuestWeightage %>');" title="Add New question"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
		                                                    </td>
		                                                </tr>
	                                                <%} %>
                                                <% } %>
                                                <%if(alSecQueList == null ||  alSecQueList.isEmpty()) {%>
                                                	<tr>
                                                       <td colspan="2">
                                                         <span><a href="javascript:void(0);" onclick="addNewSectionQuestion('<%=i %>','<%=formId %>','<%=hmSection.get("SECTION_ID") %>','<%=nQuestWeightage %>');" title="Add New question"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
                                                       </td>
                                                    </tr>
                                                <%} %>
                                            </table>
                                        </div>
                                        
                                    </div>
                                    <div id="addNewQuestion_<%=formId %>_<%=hmSection.get("SECTION_ID") %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none;">
		                                <s:form action="FormSummary" method="POST" theme="simple">
		                                    <s:hidden name="formId"></s:hidden>
		                                    <input type="hidden" name="sectionId" value="<%=hmSection.get("SECTION_ID") %>"/>
		                                    <s:hidden name="operation" value="QU"/>
		                                    <s:hidden name="strOrg" id="strOrg"></s:hidden>
		                                    <input type="hidden" name="ansType" value="<%=hmSection.get("SECTION_ANSWER_TYPE_ID") %>"/>
		                                    <s:hidden name="strOrg" id="strOrg"></s:hidden>
				                            <s:hidden name="userscreen" id="userscreen"></s:hidden>
											<s:hidden name="navigationId" id="navigationId"></s:hidden>
											<s:hidden name="toPage" id="toPage"></s:hidden>
		                                    <table class="table table-striped table-bordered" width="100%;">
		                                        <tr>
		                                            <th>&nbsp;</th>
		                                            <th width="17%" style="text-align: right;">Add Question<sup>*</sup></th>
		                                            <td colspan="3">
		                                                <span id="newquespan<%=i %>" style="float: left;">
		                                                <input type="hidden" name="hidequeid" id="hidequeid<%=i %>" value="0"/>
		                                                <textarea rows="2" name="question" id="question<%=i %>" class="validateRequired" style="width: 330px !important;"></textarea>
		                                                </span>&nbsp;
		                                                <span style="float: left; margin-left: 10px;">
		                                                <input type="hidden" name="orientt" value="<%=i %>"/><sup>*</sup>
		                                                <input type="text" style="width: 35px !important;" name="weightage" id="editweightage<%=i %>" 
		                                                    class="validateNumber" value="<%=(100-nTotalQuestWeightage) %>" 
		                                                    onkeyup="validateEditQuest(this.value,'editweightage<%=i %>','hideeditweightage<%=i %>');" 
		                                                    onkeypress="return isNumberKey(event)"/>
		                                                <input type="hidden" name="hideweightage" id="hideeditweightage<%=i %>" value="<%=(100-nTotalQuestWeightage) %>"/></span>&nbsp;&nbsp;
		                                                <span style="float: left; margin-left: 10px;">
		                                                <a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=i %>');" > +Q </a>
		                                                </span>&nbsp;
		                                                <span id="checkboxspan<%=i %>" style="float: left; margin-left: 10px;">
		                                                <input name="addFlag" type="checkbox" id="addFlag<%=i %>" title="Add to Question Bank" onclick="changeStatus('<%=i %>')"/>
		                                                <input type="hidden" id="status<%=i %>" name="status" value="0"/>
		                                                </span>
		                                                <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getQuestion('<%=i %>')"></a>&nbsp;&nbsp;
		                                                    <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath() %>/images1/icons/icons/close_button_icon.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionDiv<%=i %>')"/> --%>
		                                                <input type="hidden" name="questiontypename" value="<%=i %>"/>
		                                            </td>
		                                        </tr>
		                                        <%if(uF.parseToInt(hmSection.get("SECTION_ANSWER_TYPE_ID")) == 1 || uF.parseToInt(hmSection.get("SECTION_ANSWER_TYPE_ID")) == 2 || uF.parseToInt(hmSection.get("SECTION_ANSWER_TYPE_ID")) == 8){ %>
		                                        <tr id="answerType<%=i %>">
		                                            <th>&nbsp;</th>
		                                            <th>&nbsp;</th>
		                                            <td>a)&nbsp;<input type="text" name="optiona" id="optiona<%=i %>"/>
		                                                <input type="radio" value="a" name="correct<%=i %>"/>
		                                            </td>
		                                            <td colspan="2">b)&nbsp;<input type="text" name="optionb" id="optionb<%=i %>"/>
		                                                <input type="radio" name="correct<%=i %>" value="b"/>
		                                            </td>
		                                        </tr>
		                                        <tr id="answerType1<%=i %>">
		                                            <th>&nbsp;</th>
		                                            <th>&nbsp;</th>
		                                            <td>c)&nbsp;<input type="text" name="optionc" id="optionc<%=i %>"/>
		                                                <input type="radio" name="correct<%=i %>" value="c"/>
		                                            </td>
		                                            <td colspan="2">d)&nbsp;<input type="text" name="optiond" id="optiond<%=i %>"/>
		                                                <input type="radio" name="correct<%=i %>" value="d"/>
		                                            </td>
		                                        </tr>
		                                        <%}else if(uF.parseToInt(hmSection.get("SECTION_ANSWER_TYPE_ID")) == 9 ){ %>	
		                                        <tr id="answerType<%=i %>">
		                                            <th>&nbsp;</th>
		                                            <th>&nbsp;</th>
		                                            <td>a)&nbsp;<span id="aspan"><input type="text" name="optiona" id="optiona<%=i %>"/></span>
		                                                <input type="checkbox" value="a" name="correct<%=i %>" />
		                                            </td>
                                            <td colspan="2">b)&nbsp;<span id="bspan"><input type="text" name="optionb" id="optionb<%=i %>"/></span>
                                                <input type="checkbox" name="correct<%=i %>" value="b"/>
                                            </td>
                                        </tr>
                                        <tr id="answerType1<%=i %>">
                                            <th>&nbsp;</th>
                                            <th>&nbsp;</th>
                                            <td>c)&nbsp;<span id="cspan"><input type="text" name="optionc" id="optionc<%=i %>"/></span>
                                                <input type="checkbox" name="correct<%=i %>" value="c"/>
                                            </td>
                                            <td colspan="2">d)&nbsp;<span id="dspan"><input type="text" name="optiond" id="optiond<%=i %>"/></span>
                                                <input type="checkbox" name="correct<%=i %>" value="d"/>
                                            </td>
                                        </tr>
                                        <%}else if(uF.parseToInt(hmSection.get("SECTION_ANSWER_TYPE_ID")) == 6 ){ %>	
                                        <tr id="answerType<%=i %>">
                                            <th>&nbsp;</th>
                                            <th>&nbsp;</th>
                                            <td><input type="hidden" name="optiona" id="optiona<%=i %>"/>
                                                <input type="hidden" name="optionb" id="optionb<%=i %>"/>
                                                <input type="hidden" name="optionc" id="optionc<%=i %>"/>
                                                <input type="hidden" name="optiond" id="optiond<%=i %>"/>
                                                <input type="radio" name="correct<%=i %>" checked="checked" value="1">True&nbsp;
                                                <input type="radio" name="correct<%=i %>" value="0">False
                                            </td>
                                        </tr>
                                        <%}else if(uF.parseToInt(hmSection.get("SECTION_ANSWER_TYPE_ID")) == 5 ){ %>
                                        <tr id="answerType<%=i %>">
                                            <th>&nbsp;</th>
                                            <th>&nbsp;</th>
                                            <td><input type="hidden" name="optiona" id="optiona<%=i %>"/>
                                                <input type="hidden" name="optionb" id="optionb<%=i %>"/>
                                                <input type="hidden" name="optionc" id="optionc<%=i %>"/>
                                                <input type="hidden" name="optiond" id="optiond<%=i %>"/>
                                                <input type="radio" name="correct<%=i %>" checked="checked" value="1">Yes&nbsp;
                                                <input type="radio" name="correct<%=i %>" value="0">No
                                            </td>
                                        </tr>
                                        <%} %>
                                    </table>
                                    <div id="firstdiv" style="float:left; margin-top:20px;" >
                                        <s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
                                        &nbsp;&nbsp;&nbsp;&nbsp;
                                        <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
                                    </div>
                                </s:form>
                            </div>
                                </div>
                                <!-- /.box-body -->
                            </div>
                            
                        </div>
                        <%	} 
                         }%>
                         <div style="float: left;">
	                        <p><input type="hidden" name="totalWeightage" id="totalWeightage" value="<%=nWeightage%>"/> </p>
	                        <%if(nWeightage < 100){ %>
	                        <p style="margin: 0px 0px 0px 15px" class="addnew desgn">
	                            <a class="add_lvl" href="javascript:void(0)" onclick="showSection();"; title="Add New Section">Add New Section</a>
	                        </p>
	                        <%} %>
                        </div>
                        <div id="sectionDiv" style="width:100%; float:left; display:none;">
                            <s:form name="frmFormSummary" id="frmFormSummary" theme="simple" action="FormSummary" method="POST">
                                <s:hidden name="formId" id="formId"></s:hidden>
                                <s:hidden name="operation" value="U"/>
                                <s:hidden name="strOrg" id="strOrg"></s:hidden>
                                <s:hidden name="strOrg" id="strOrg"></s:hidden>
	                            <s:hidden name="userscreen" id="userscreen"></s:hidden>
								<s:hidden name="navigationId" id="navigationId"></s:hidden>
								<s:hidden name="toPage" id="toPage"></s:hidden>
                                <div style="width:100%; float:left">
                                    <table border="0" class="table table_no_border" style="float: left; width: 100%;">
                                        <tr>
                                            <td class="txtlabel alignRight">Section Title:<sup>*</sup></td>
                                            <td colspan="5"><input type="text" name="sectionTitle" id="sectionTitle" class="validateRequired" style="width:39%"/></td>
                                        </tr>
                                        <tr>
                                            <td class="txtlabel alignRight" valign="top">Short Description:</td>
                                            <td colspan="5"><textarea rows="5" cols="50" name="shortDesrciption" id="editor1"></textarea></td>
                                        </tr>
                                        <tr>
                                            <td class="txtlabel alignRight" valign="top">Long Description:</td>
                                            <td colspan="5"><textarea rows="5" cols="50" name="longDesrciption" id="editor2"></textarea></td>
                                        </tr>
                                        <tr>
                                            <td class="txtlabel alignRight">Weightage %:<sup>*</sup></td>
                                            <td>
                                                <input type="hidden" name="hideSecTotWeight" id="hideSecTotWeight" value="<%=nWeightage%>"/>
                                                <input type="text" name="sectionWeightage"	id="sectionWeightage" class="validateNumber" value="<%=(100 - nWeightage)%>"onkeyup="validateSECANDSUBSECScore(this.value,'sectionWeightage','hidesectionWeightage');addNewSection();" onkeypress="return isNumberKey(event)" style="text-align: right; width: 40px !important;"/>
                                                <input type="hidden" name="hidesectionWeightage" id="hidesectionWeightage" value="<%=(100 - nWeightage) %>" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="txtlabel alignRight" valign="top">Select Answer Type:<sup>*</sup></td>
                                            <td>
                                                <div style="float: left; width: 23%; margin-bottom: 10px;">
                                                    <select name="ansType" id="ansType" onchange="showAnswerTypeDiv(this.value)" class="validateRequired">
                                                    <%=anstype %>
                                                    </select>
                                                </div>
                                                <div style="float: left; width: 55%; margin-left: 10px; margin-bottom: 10px;">
                                                    <div id="anstypediv">
                                                        <div id="anstype9">
                                                            a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"/><br />
                                                            c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"/><br />
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>&nbsp;</td>
                                            <td><a onclick="getQuestion('0');" class="add_lvl" href="javascript:void(0)">Add Question</a></td>
                                        </tr>
                                        <tr>
                                            <td>&nbsp;</td>
                                            <td>
                                                <div id="otherDiv" style="float:left;width:100%;">
                                                    <div id="otherQuestionMainDiv">
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                                <div id="firstdiv" style="float:left; margin-top:20px;" >
                                    <s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                    <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
                                </div>
                            </s:form>
                        </div>
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
        
        
        // Replace the <textarea id="editor2"> with an CKEditor instance.
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
    </script>
</g:compress>
<div id="SelectQueDiv"></div>
<div id="editFormDiv"></div>
<div id="editSectionDiv"></div>
<div id="editQuestionDiv"></div>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
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