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
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
    $(document).ready( function () {
    	$("#frmAddForm_submit").click(function(){
    		$(".validateRequired").prop('required',true);
    		$(".validateNumber").prop('type','number');
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
</script>
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
<script type="text/javascript">
    var cxtpath='<%=request.getContextPath()%>';
    
    $(document).ready( function () {
    	onloadAddNewSection();
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
    		/* +"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionDiv"+cnt+"')\"/>"  */
    		+"<i class=\"fa fa-times-circle cross\" style=\"height: 16px; width: 16px;\" aria-hidden=\"true\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionDiv"+cnt+"')\"></i>"
    		+"<input type=\"hidden\" name=\"questiontypename\" value=\""+cnt+"\"/>"
    		+"</td></tr>"
    		+questContentType
    		+"</table>";
    		
    		divtag.innerHTML = divData;
    		
    		document.getElementById("otherQuestionMainDiv").appendChild(divtag);
    		
    		jQuery("#frmAddForm").validationEngine();
    	}
    }
    
    function getQuestoinContentType(cnt){
    	var val = document.getElementById("ansType").value;
    	var a="";
    	if(val == 8){
    		a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
    		+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
    	
    	}else if (val == 1 || val == 2 || val == 9) {
    		a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<span id=\"aspan\"><input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\"/></span> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<span id=\"bspan\"><input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
    		+"<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<span id=\"cspan\"><input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<span id=\"dspan\"><input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
    
    	 }else if (val == 6) {
    		a= "<tr id=\"answerType"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
    			+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td></tr>";
    	
    	}else if (val == 5) {
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
    			document.getElementById(weightageid).value = remainweight;
    		}else if(parseFloat(value1) <= 0 ){
    			alert("Invalid Weightage");
    			document.getElementById(weightageid).value = remainweight;
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
    
    function onloadAddNewSection() {
    	var hideSecTotWeight = document.getElementById("hideSecTotWeight").value;
    	var sectionWeightage = document.getElementById("sectionWeightage").value;
    	if(sectionWeightage == ""){
    		sectionWeightage=0;
    	}
    	var secRemainWeightage = 100 - (parseFloat(sectionWeightage)+parseFloat(hideSecTotWeight));
    	 if(parseInt(secRemainWeightage) > 0){
    		 document.getElementById("firstdiv").style.display = 'none';
    		 document.getElementById("secWeightdiv").style.display = 'block';
    	 }else {
    		 document.getElementById("firstdiv").style.display = 'block';
    		 document.getElementById("secWeightdiv").style.display = 'none';
    	 }
    }
    
    function closeForm() {
    	var strOrg = document.getElementById("strOrg").value;
    	var userscreen = document.getElementById("userscreen").value;
    	var navigationId = document.getElementById("navigationId").value; 
    	var toPage = document.getElementById("toPage").value;
    	window.location = "MyDashboard.action?strOrg="+strOrg+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage;
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
    
    function addNewSection() {
    	var hideSecTotWeight = document.getElementById("hideSecTotWeight").value;
    	var sectionWeightage = document.getElementById("sectionWeightage").value;
    	if(sectionWeightage == ""){
    		sectionWeightage=0;
    	}
    	
    	var secRemainWeightage = 100 - (parseFloat(sectionWeightage)+parseFloat(hideSecTotWeight));
    	 if(parseInt(secRemainWeightage) > 0){
    		 document.getElementById("firstdiv").style.display = 'none';
    		 document.getElementById("secWeightdiv").style.display = 'block';
    	 }else {
    		 document.getElementById("firstdiv").style.display = 'block';
    		 document.getElementById("secWeightdiv").style.display = 'none';
    	 }
    }
    
</script>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=strTitle %>" name="title" />
    </jsp:include> --%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                        <s:form name="frmAddForm" id="frmAddForm" theme="simple" action="AddForm" method="POST">
                            <s:hidden name="formId" id="formId"></s:hidden>
                            <s:hidden name="strOrg" id="strOrg"></s:hidden>
                            <s:hidden name="userscreen" id="userscreen"></s:hidden>
							<s:hidden name="navigationId" id="navigationId"></s:hidden>
							<s:hidden name="toPage" id="toPage"></s:hidden>
                            <div>
                                <%if(uF.parseToInt(formId) == 0){ %>
                                <s:hidden name="operation" value="A"/>
                                <table border="0" class="table table_no_border autoWidth">
                                    <tr>
                                        <td class="txtlabel alignRight">Organisation:</td>
                                        <td><%=uF.showData(((String)request.getAttribute("strOrgName")),"") %></td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">Form Title:<sup>*</sup></td>
                                        <td>
                                            <s:textfield name="strFormName" id="strFormName" cssClass="validateRequired" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">Form Node:<sup>*</sup></td>
                                        <td>
                                            <s:select name="strNode" id="strNode" list="nodeList" listKey="nodeId" listValue="nodeName" headerKey=""
                                                headerValue="Select Node" cssClass="validateRequired"></s:select>
                                        </td>
                                    </tr>
                                </table>
                                <%} else { %>
                                <s:hidden name="operation" value="U"/>
                                <table class="table table_no_border autoWidth">
                                    <tr>
                                        <th class="txtlabel alignRight">Form Title:</th>
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
                                <%} %>
                            </div>
                            <div class="clr"><strong>Section Details</strong></div>
                            <%
                                int nWeightage = 0;
                                if(uF.parseToInt(formId) > 0){ 
                                	for(int i = 0; alSection !=null && i < alSection.size(); i++ ){	
                                		Map<String, String> hmSection = (Map<String, String>) alSection.get(i);
                                		nWeightage += uF.parseToDouble(hmSection.get("SECTION_WEIGHTAGE")); 
                                %>
                                <div class="box box-default collapsed-box" style="margin-top: 10px;">
                					
					                <div class="box-header with-border">
					                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=uF.showData(hmSection.get("SECTION_NAME"),"") %></h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					                    <div>
                                            <table class="table table_no_border autoWidth">
                                                <tr>
                                                    <th class="txtlabel alignRight">Short Description:</th>
                                                    <td><%=uF.showData(hmSection.get("SECTION_SHORT_DESCRIPTION"),"") %></td>
                                                </tr>
                                                <tr>
                                                    <th class="txtlabel alignRight">Long Description:</th>
                                                    <td><%=uF.showData(hmSection.get("SECTION_LONG_DESCRIPTION"),"") %></td>
                                                </tr>
                                                <tr>
                                                    <th class="txtlabel alignRight">Weightage %:</th>
                                                    <td><%=uF.showData(hmSection.get("SECTION_WEIGHTAGE"),"") %></td>
                                                </tr>
                                                <tr>
                                                    <th class="txtlabel alignRight">Answer Type:</th>
                                                    <td><%=uF.showData(hmSection.get("SECTION_ANSWER_TYPE"),"") %></td>
                                                </tr>
                                            </table>
                                        </div>
                                        <%
                                            List<Map<String, String>> alSecQueList = (List<Map<String, String>>) hmSectionQuestion.get(hmSection.get("SECTION_ID"));
                                            if(alSecQueList != null && alSecQueList.size() > 0){
                                            %>
                                        <div>
                                            <table class="table table-bordered autoWidth">
                                                <tr>
                                                    <td><b>Question</b></td>
                                                    <td><b>Weightage</b></td>
                                                </tr>
                                                <%
                                                    for(int j = 0; j < alSecQueList.size(); j++){
                                                    	Map<String, String> hmSecQuestion = (Map<String, String>) alSecQueList.get(j);
                                                    %>
                                                <tr>
                                                    <td><%=uF.showData(hmSecQuestion.get("SECTION_QUEST_QUESTION_NAME"),"")%></td>
                                                    <td style="text-align: right"><%=uF.showData(hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE"),"")%>%</td>
                                                </tr>
                                                <%}%>
                                            </table>
                                        </div>
                                        <%	 
                                            }%>
					                </div>
					                <!-- /.box-body -->
					            </div>
                            <%	} 
                                }%>
                            <div>
                                <table border="0" class="table table_no_border">
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
                                            <div style="float: left; width: 23%;margin-bottom: 10px;">
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
                                        <td><a onclick="getQuestion('0');"  href="javascript:void(0)"> <i class="fa fa-plus-circle" aria-hidden="true"></i>Add Question</a></td>
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
                            <div id="firstdiv" style="float:left;" >
                                <s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
                                &nbsp;&nbsp;&nbsp;&nbsp;
                                <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
                            </div>
                            <div id="secWeightdiv" style="float:left; margin-top:20px; display: none" >
                                <s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
                                &nbsp;&nbsp;&nbsp;&nbsp;
                                <s:submit value="Save & Add New Section" cssClass="btn btn-primary" name="saveandnew"></s:submit>
                                &nbsp;&nbsp;&nbsp;&nbsp;
                                <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
                            </div>
                        </s:form>
                    </div>
                </div>
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