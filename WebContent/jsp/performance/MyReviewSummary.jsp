<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<script src="scripts/ckeditor_cust/ckeditor.js"></script>
<jsp:include page="../performance/CustomAjaxForReview.jsp"></jsp:include> 

<script src='scripts/charts/jquery.min.js'></script>
<g:compress>
    <link href="scripts/ckeditor/samples/sample.css" rel="stylesheet">
    <script>
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
        
        function isNumberKey(evt){
        	  var charCode = (evt.which) ? evt.which : event.keyCode;
        	  if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
        	     return false;
        	  }
        	  return true;
        	}
        
    </script>
</g:compress>
<g:compress>
    <style>
        .divvalign {
        /*    width: 50px;
        height: 50px;
        background: #999;
        display: inline-block;
        */
        vertical-align: top; /* here */
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
        .ul_class li {
        margin: 10px 0px 10px 100px;
        }
        .ul_section li {
        margin: 0px 0px 0px 5px;
        }
        .ul_subsection li {
        margin: 0px 0px 0px 20px;
        }
    </style>
    <script type="text/javascript">
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
        	function openQuestionBank(count) {
        		var ansType="";
        		if(document.getElementById('othrqueanstype'+count) != null){
        			ansType=document.getElementById('othrqueanstype'+count).value;
        		}
        		if(document.getElementById('queanstype'+count) != null){
        			ansType=document.getElementById('queanstype'+count).value;
        		}
        		if(document.getElementById('ansType') != null){
        			ansType=document.getElementById('ansType').value;
        		}
        		var dialogEdit = '#modal-body1';
        		 $(dialogEdit).empty();
        		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        		 $("#modalInfo1").show();
        		 $(".modal-title1").html('Question Bank');
        		 $.ajax({
        				url : "SelectQuestion.action?count="+count+"&ansType="+ansType,
        				cache : false,
        				success : function(data) {
        					$(dialogEdit).html(data);
        				}
        		 });
        		 
        	}
        	
        	function openEditAppraisal(id,appsystem,appFreqId) {
        		var dialogEdit = '.modal-body';
        		 $(dialogEdit).empty();
        		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        		 $("#modalInfo").show();
        		 $(".modal-title").html('Edit My Review');	
        		 if($(window).width() >= 900){
        			 $(".modal-dialog").width(900);
        		 }	
        		 $.ajax({
        				url : "EditMyReviewPopUp.action?id="+id+"&appsystem="+appsystem+"&appFreqId="+appFreqId,
        				cache : false,
        				success : function(data) {
        					$(dialogEdit).html(data);
        				}
        			});
        	}
      	
        	var dialogEdit1 = '#EditSectionDiv';
        	function openEditSection(id,sID,sNO,type,oreinteId,sectionName,totWeightage,sectionID,value,appFreqId,fromPage) {
        		var dialogEdit = '.modal-body';
        		 $(dialogEdit).empty();
        		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        		 $("#modalInfo").show();
        		 $(".modal-title").html('Edit Section');
        		 if($(window).width() >= 900){
        			 $(".modal-dialog").width(900);
        		 }
        		 $.ajax({
        				url : "EditMyReviewSectionAndSubsectionPopUp.action?id="+id+"&sID="+sID+"&sNO="+sNO+"&type="+type+"&oreinteId="
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
        	
        	function openSectionDiv(type,value){
        		//alert("in openSectionDiv type : "+type);
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
        	
        	
    </script>
</g:compress>
<script type="text/javascript">
    $(function() {
    	var a = '#from';
    	$("#from").datepicker({format : 'dd/mm/yyyy'});
    	$("#to").datepicker({format : 'dd/mm/yyyy'}); 
    });	
    
    	 
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
    
    
    function openAddNewLevel(id,sysdiv,newlvlno,totWeightage,appFreqId,fromPage) {
    var remainWeightage = 100 - parseFloat(totWeightage);
     if(parseInt(remainWeightage) <= 0){
    	 alert("Unable to add section because of no weightage available ");
     }else{
    	 document.getElementById(sysdiv).style.display="block";
    	 $("#"+sysdiv).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 var action = 'openMyReviewSectionAndSubsection.action?id=' + id + '&sysdiv=' + sysdiv + '&newlvlno=' 
    			 + newlvlno+'&weightage='+remainWeightage+'&appFreqId='+appFreqId+'&fromPage='+fromPage;
    	 getContent(sysdiv, action);
     }
    }
    
    function openAddNewSystem(id,MLID,type,sysdiv,newsysno,totWeightage,linkType,linkDiv,divCount,appFreqId,fromPage) {
    
    var remainWeightage = 100 - parseFloat(totWeightage);
     if(parseInt(remainWeightage) <= 0){
    	 alert("Unable to add subsection because of no weightage available ");
     }else{
    	 if(linkType == 'section'){
    		 document.getElementById("sectionLinkSpan"+divCount).style.display="none";
    		 document.getElementById("assessLinkSpan"+divCount).style.display="block"; 
    	 }else{
    		 document.getElementById("assessLinkSpan"+divCount).style.display="none";
    		 document.getElementById("sectionLinkSpan"+divCount).style.display="block";
    	 }
    	 document.getElementById(sysdiv).style.display="block";
    	$("#"+sysdiv).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	var action = 'openMyReviewSectionAndSubsection.action?id=' + id + '&MLID=' + MLID + '&type=' + type +'&sysdiv=' + sysdiv 
    			+ '&newsysno=' + newsysno+'&subWeightage='+remainWeightage+'&linkDiv='+linkDiv+'&divCount='+divCount+'&linkType='+linkType
    			+'&appFreqId='+appFreqId+'&fromPage='+fromPage;
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
     if(document.getElementById("EditQuestionDiv")) {
    	document.getElementById("EditQuestionDiv").innerHTML='';
     }
    $.ajax({
    	url : "GetMyReviewEditQuestionDetails.action?id=" + id + "&appsystem=" + appsystem + "&scoreType=" + scoreType + "&editID=" + editID + 
    			"&type=" + type + "&quediv=EditQuestionDiv" + "&othrquetype=" + othrquetype + "&queno=" + queno + "&ansid=" + ansid + 
    			"&selectanstype=" + selectanstype + "&totWeightage=" + totWeightage + "&sectionID=" + sectionID + "&subsectionID=" + subsectionID
    			+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
    	cache : false,
    	success : function(data) {
    		$(dialogEdit).html(data);
    	}
    });
    }
    
    
    var dialogEdit3 = '#EditBalanceCardDiv';
    function openOtherSystemEditQue(id,appsystem,scoreType,editID,type,quediv,ansid,selectanstype,totWeightage,queno,sectionID,subsectionID,appFreqId,fromPage) {
    document.getElementById("EditBalanceCardDiv").innerHTML='';
    var dialogEdit = '.modal-body';
     $(dialogEdit).empty();
     $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     $("#modalInfo").show();
     $(".modal-title").html('Edit Balance Score Card/Question');
     $.ajax({
    		url : "GetMyReviewEditQuestionDetails.action?id=" + id + "&appsystem=" + appsystem + "&scoreType=" + scoreType + "&editID=" + editID
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
    	}else if(parseFloat(value1) <= 0 ){
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
    }else if(parseFloat(value) <= 0 ){
    	alert("Invalid Weightage");
    	document.getElementById(weightageid).value = remainWeightage;
    }
    }
    
    
    $(function() {
    	$("#slidermultipleopen").slider({
    		value : 50,
    		min : 0,
    		max : 100,
    		step : 1,
    		slide : function(event, ui) {
    			$("#marks").val(ui.value);
    			$("#slidemarksmultipleopen").html(ui.value);
    		}
    	});
    	$("#marks").val($("#slidermultipleopen").slider("value"));
    	$("#slidemarksmultipleopen").html($("#slidermultipleopen").slider("value"));
    });
    
           $(function() {
           	$('#starPrimary').raty({
           		readOnly: false,
           		start: 5,
           		half: true,
           		targetType: 'number',
           		click: function(score, evt) {
           			$('#gradewithrating').val(score);
    					}
    				});
    	});
    
    $(function() {
    	$("#slidersingleopen").slider({
    		value : 50,
    		min : 0,
    		max : 100,
    		step : 2,
    		slide : function(event, ui) {
    			$("#marks").val(ui.value);
    			$("#slidemarkssingleopen").html(ui.value);
    		}
    	});
    	$("#marks").val($("#slidersingleopen").slider("value"));
    	$("#slidemarkssingleopen").html($("#slidersingleopen").slider("value"));
    });
    
    $(function() {
    	$("#sliderscore").slider({
    		value : 50,
    		min : 0,
    		max : 100,
    		step : 1,
    		slide : function(event, ui) {
    			$("#marks").val(ui.value);
    			$("#slidemarksscore").html(ui.value);
    		}
    	});
    	$("#marks").val($("#sliderscore").slider("value"));
    	$("#slidemarksscore").html($("#sliderscore").slider("value"));
    });
</script>
<%
    String orientation = (String)request.getAttribute("oreinted");
    
    String appFreqId = (String)request.getAttribute("appFreqId");
    String id = (String)request.getAttribute("id");
    String fromPage = (String)request.getAttribute("fromPage");
    
    if(fromPage == null) fromPage ="MyReview";
    List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
    Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
    Map<String, List<List<String>>> hmKRA1 =(Map<String, List<List<String>>>)request.getAttribute("hmKRA1");
    Map<String, List<List<String>>> hmKRA =(Map<String, List<List<String>>>)request.getAttribute("hmKRA");
    Map<String, String> hmMesures = (Map<String, String>) request.getAttribute("hmMesures");
    Map<String, String> questMp = (Map<String, String>) request.getAttribute("questMp");
    Map<String, Map<String,String>> memberMp =(Map<String, Map<String,String>>) request.getAttribute("memberMp");
    int newlvlno =1;
    double sectionTotWeightage = 0;
    
    	List<List<String>> mainLevelList = (List<List<String>>) request.getAttribute("mainLevelList");
    	Map<String, List<List<String>>> hmSystemLevelMp =(Map<String, List<List<String>>>)request.getAttribute("hmSystemLevelMp");
    	Map<String, List<Map<String, List<List<String>>>>> levelMp = (Map<String, List<Map<String, List<List<String>>>>>) request.getAttribute("levelMp");
    		UtilityFunctions uF = new UtilityFunctions();
    	%>
<%
    if(mainLevelList != null && !mainLevelList.isEmpty()){
    	newlvlno = mainLevelList.size()+1;
    	for(int i=0;i<mainLevelList.size();i++){
    		List<String> mainInnerlist = mainLevelList.get(i);
    		sectionTotWeightage += uF.parseToDouble(mainInnerlist.get(5));
    	}
    }
    %>
<%-- <s:if test="type =='choose'">
    <jsp:include page="../common/SubHeader.jsp">
    	<jsp:param value="Review Preview" name="title" />
    </jsp:include>
    </s:if>
    <s:else> 
    <jsp:include page="../common/SubHeader.jsp">
    	<jsp:param value="Review Summary" name="title" />
    </jsp:include>
    
    </s:else> --%>
<div class="leftbox reportWidth">
    <section class="content">
        <div class="row jscroll">
            <section class="col-lg-12 connectedSortable">
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">Review Summary</h3>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body" style="padding: 5px; overflow-y: auto;">
                        <h4>
                            <%=appraisalList.get(1)%>&nbsp;&nbsp;<%=uF.showData(appraisalList.get(5), "")%>&nbsp;&nbsp;<%=uF.showData(appraisalList.get(9), "")%>&nbsp;&nbsp;<%=uF.showData(appraisalList.get(8), "")%>
                            <span style="float:right; margin-right: 2cm;">
                                <s:if test="type =='choose'">
                                    <a href="javascript:void(0);" onclick="if(confirm('Are you sure, You want to create Review from this template?')) window.location='CreateAppraisalFromTemplate.action?appFreqId=<%=appFreqId%>&existID=<s:property value="id"/>';">
                                    Choose this Review</a>
                                </s:if>
                            </span>
                        </h4>
                        <table class="table table_no_border" width="100%">
                            <tr>
                                <th width="15%" align="right">
                                    <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                    <a href="javascript: void(0)" onclick="openEditAppraisal('<%=appraisalList.get(0) %>','appraisal','<%=appFreqId %>')" title="Edit Appraisal"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                    <% if(fromPage != null && fromPage.equalsIgnoreCase("MyReview")) { %>
                                    	<a title="Delete" href="DeleteAppraisal.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId%>&fromPage=<%=fromPage %>" onclick="return confirm('Are you sure you want to delete this appraisal?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                    <% } else { %>
                                    	<a title="Delete" style="color:#F02F37;" href="javascript:void();" onclick="deleteAppraisal('<%=id %>','<%=appFreqId %>','<%=fromPage %>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                    <% } %>
                                    <%} %>
                                    Review Type:
                                </th>
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
                                <td><%=appraisalList.get(23)%></td>
                            </tr>
                            <tr>
                                <th align="right">Due Date:</th>
                                <td><%=appraisalList.get(24)%></td>
                            </tr>
                            <tr>
                                <th align="right">Orientation:</th>
                                <td colspan="1"><%=appraisalList.get(2)%></td>
                            </tr>
                            <tr>
                                <th valign="top" align="right">Reviewee:</th>
                                <td colspan="1"><%=appraisalList.get(12)%></td>
                            </tr>
                            <tr>
                                <th valign="top" align="right">Appraiser:</th>
                                <td colspan="1"><%=appraisalList.get(26)%></td>
                            </tr>
                            <tr>
                                <th valign="top" align="right">Reviewer:</th>
                                <td colspan="1"><%=appraisalList.get(25)%></td>
                            </tr>
                            <tr>
                                <th align="right">Workflow for Publish review:</th>
                                <td colspan="1">&nbsp;</td>
                            </tr>
                            <%=(String)request.getAttribute("sbWorkflow") %>
                        </table>
                        <h4><img src="images1/bottom-right-arrow.png">Sections <span class="badge bg-blue"><%=mainLevelList.size() %></span></h4>
                        <%
                            int z=0;
                            int newsysno=1;
                            for (int a = 0; mainLevelList != null && a < mainLevelList.size(); a++) {
                            	double subsectionTotWeightage=0;
                            		List<String> maininnerList = mainLevelList.get(a);
                            		List<List<String>> outerList1 =hmSystemLevelMp.get(maininnerList.get(0));
                            		if(outerList1 != null && !outerList1.isEmpty()){
                            			newsysno = outerList1.size()+1; 
                            			for(int i=0;i<outerList1.size();i++){
                            				List<String> subInnerlist = outerList1.get(i);
                            				subsectionTotWeightage += uF.parseToDouble(subInnerlist.get(7));
                            			}
                            		} 
                            %>
                        <div class="box box-primary">
                            <div class="box-header with-border">
                            	<h3 class="box-title">
	                                <div class="row row_without_margin">
	                                    <div class="col-lg-3 col-md-3 col-sm-4">
	                                        <%=a+1%>) <%=maininnerList.get(1)%> 
	                                        <ul id="" class="ul_section">
	                                            <li>
	                                                <%=uF.showData(maininnerList.get(2), "")%>
	                                            </li>
	                                        </ul>
	                                    </div>
	                                    <div class="col-lg-2 col-md-2 col-sm-3">
	                                        <strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(maininnerList.get(5),"0")%>%
	                                    </div>
	                                    <div class="col-lg-7 col-md-7 col-m-5">
	                                        <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
	                                        <span style="float: right;margin-right: 20px;">
	                                        <a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=maininnerList.get(0) %>','<%=a+1%>','section','<%=orientation %>','','<%=sectionTotWeightage %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage%>')" title="Edit Section"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
	                                        <% if(fromPage != null && fromPage.equals("SRR")) { %>
	                                        <a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','','Level','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
	                                        <% } else { %>
	                                        <a  title="Delete" href="DeleteAppraisalLevelAndSystem.action?from=SR&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&type=Level" onclick="return confirm('Are you sure you want to delete this section?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
	                                        <% } %>	
	                                        </span>
	                                        <% } %>
	                                        <p style="float: right; margin-right: 20px; font-weight: normal; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(maininnerList.get(6),"")%>&nbsp;<%=uF.showData(maininnerList.get(7),"")%></p>
	                                    </div>
	                                </div>
                                </h3>
                                <div class="box-tools pull-right">
                                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                </div>
                            </div>
                            <!-- /.box-header -->
                            <div class="box-body" style="padding: 5px; overflow-y: auto;">
                                <div class="content1">
                                    <%
                                        for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
                                        		List<String> innerList1 = outerList1.get(i);
                                        		
                                        		if (uF.parseToInt(innerList1.get(3)) == 2) {
                                        			List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                        			Map<String, List<List<String>>> scoreMp = list.get(0);
                                        			List<List<String>> queList = scoreMp.get(innerList1.get(0));
                                        %>
                                    <!-- <div class="content1"> -->
                                    <%
                                        double totothersysWeightage = 0;
                                        int newquecnt = 0;
                                        String otherQueAnstype="";
                                        String otherQuetype ="",sectionattribute="";
                                        %>
                                    <ul id="" class="ul_class1">
                                        <li>
                                            <div style="overflow: auto; margin-top: 10px; border: 1px solid #EEEEEE;padding: 10px 10px 10px 10px;">
                                                <div style="text-align:left; height: 35px;">
                                                    <%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) {%>
                                                    &nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
                                                    <span style="float: right; margin-right: 333px;"><strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"0")%>%</span>
                                                    <div style="width: 70%">
                                                        <ul id="" class="ul_subsection">
                                                            <li><%=innerList1.get(4)%>
                                                                <%=innerList1.get(5)%>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <%} %>
                                                    <div style="width: 100%; float: left;">
                                                        <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                        <span style="float: right;">
                                                        <a href="javascript: void(0)"  onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage%>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                        <% if(fromPage != null && fromPage.equals("SRR")) { %>
                                                        <a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                        <% } else { %>
                                                        <a  title="Delete" href="DeleteAppraisalLevelAndSystem.action?from=SR&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                        <% } %>
                                                        </span>
                                                        <%} %>
                                                        <span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
                                                        <!-- </blockquote> -->
                                                    </div>
                                                </div>
                                                <div style="overflow: hidden; float: left; border: 1px solid #eee; width: 99.9%;padding: 10px 10px 10px 10px;">
                                                    <table class="table" style="width: 100%; float: left;">
                                                        <tr>
                                                            <td width="90%"><b>Question</b></td>
                                                            <td><b>Weightage</b></td>
                                                        </tr>
                                                        <%
                                                            totothersysWeightage = 0;	
                                                            List<List<String>> goalList = scoreMp.get(innerList1.get(0));
                                                            	newquecnt= goalList != null ? goalList.size()+1 : 1 ;
                                                            	for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                            		List<String> goalinnerList = goalList.get(k);
                                                            		totothersysWeightage += uF.parseToDouble(goalinnerList.get(1));
                                                            	}
                                                            	String anstype=null;
                                                            	for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                            		List<String> goalinnerList = goalList.get(k);
                                                            		z++;
                                                            		otherQuetype = goalinnerList.get(3);
                                                            		otherQueAnstype = goalinnerList.get(2);
                                                            		sectionattribute = goalinnerList.get(7);
                                                            		anstype = goalinnerList.get(2) ;
                                                            %>
                                                        <tr>
                                                            <td>
                                                                <span style="float: left;"><%=a+1%>.
                                                                <%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) {%>
                                                                <%=i+1%>.
                                                                <% } %>
                                                                <%=k+1%>)&nbsp;<%=goalinnerList.get(0)%></span>
                                                                <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")) { %>
                                                                <span style="float: left; margin-left: 10px;">
                                                                <a id="editexist<%=z%>" href="javascript:void(0)"  onclick="openOtherSystemEditQue1('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=goalinnerList.get(5)%>','quest','OTHEReditquedivOfQ<%=a %>_<%=i %>','<%=goalinnerList.get(3)%>','<%=a+1%>.<%=i+1%>.<%=k+1%>)','<%=a %>_<%=i %>e','<%=goalinnerList.get(2) %>','<%=totothersysWeightage %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>'); " title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=goalinnerList.get(5) %>','Q','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                <%} else {%>
                                                                <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId%>&queID=<%=goalinnerList.get(5) %>&type=Q" onclick="return confirm('Are you sure you want to delete this Question?')"  style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                <%} %>
                                                                </span> 
                                                                <%} %>
                                                            </td>
                                                            <td style="text-align: right"><%=goalinnerList.get(1)%>%</td>
                                                        </tr>
                                                        <% }%>
                                                        <tr>
                                                            <td colspan="2">
                                                                <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                <span> <a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="openOtherSystemNewQue('<%=a %>_<%=i %>','<%=totothersysWeightage %>');changeNewQuestionTypeOther('<%=anstype %>','answerType<%=a %>_<%=i %>','answerType1<%=a %>_<%=i %>','0')" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
                                                                <%} %>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmothersystem_<%=a %>_<%=i %>" name="frmothersystem_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <div id="OTHERnewquedivOfQ<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                            <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                            <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                            <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                            <input type="hidden" name="UID" id="UID" value="<%=innerList1.get(0)%>" />
                                            <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                            <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                            <input type="hidden" name="type1" id="type1" value="quest" />
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table table-striped" width="100%">
                                                        <tr>
                                                            <th><%=a+1%>.<%=i+1%>.<%=newquecnt %>)</th>
                                                            <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                <input type="hidden" name="othrqueanstype" id="othrqueanstype<%=a %>_<%=i %>" value="<%=otherQueAnstype%>"/>
                                                                <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>" value="<%=sectionattribute %>"/>
                                                            </th>
                                                            <td colspan="3">
                                                                <span id="newquespan<%=a %>_<%=i %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>" value="0"/>
                                                                <textarea rows="2" name="question" id="question<%=a %>_<%=i %>" class="validateRequired" style="width: 330px;"></textarea>
                                                                <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>" style="width: 330px;"/> --%>
                                                                </span>
                                                                <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                <input type="text" style="width: 35px!important;" name="weightage" id="weightage<%=a %>_<%=i %>" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>','hideweightage<%=a %>_<%=i %>')"/>
                                                                <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>" value="100" /></span>
                                                                <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>');" > +Q </a></span>
                                                                <%if(otherQuetype.equals("With Short Description")) { %>
                                                                <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" onclick="openOtheQueShortD('<%=a %>_<%=i %>')" > D </a></span>
                                                                <%}else { %>
                                                                <span style="float: left; margin-left: 10px;"> D </span>
                                                                <%} %>
                                                                <span id="checkboxspan<%=a %>_<%=i %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>')"/>
                                                                <input type="hidden" id="status<%=a %>_<%=i %>" name="status" value="0"/></span>
                                                                <input type="hidden" name="questiontypename" value="0" />
                                                            </td>
                                                        </tr>
                                                        <tr id="shortdescTr<%=a %>_<%=i %>" style="display: none;">
                                                            <th></th>
                                                            <th style="text-align: right;">Short Description</th>
                                                            <td colspan="3"><input type="hidden" name="hideotherSD" id="hideotherSD<%=a %>_<%=i %>" value="f"/>
                                                                <input type="text" name="otherSDescription" id="otherSDescription" style="width: 450px;" />
                                                            </td>
                                                        </tr>
                                                        <%if(otherQueAnstype == null || otherQueAnstype.equals("") || otherQueAnstype.equals("0")){ %>
                                                        <tr>
                                                            <th>&nbsp;</th>
                                                            <th>Select Answer Type</th>
                                                            <td width="280px">
                                                                <select name="ansType" id="ansType" onchange="showAnswerTypeDiv(this.value);changeNewQuestionType1(this.value,'answerType<%=a %>_<%=i %>','answerType1<%=a %>_<%=i %>','0')"><%=request.getAttribute("anstype") %></select>
                                                            </td>
                                                            <td colspan="2">
                                                                <div id="anstypedivAdd">
                                                                    <div id="anstype9">
                                                                        a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"/><br />
                                                                        c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"/><br />
                                                                    </div>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                        <%} %>
                                                        <tr id="answerType<%=a %>_<%=i %>" style="display: <%if(otherQueAnstype == null || otherQueAnstype.equals("") || otherQueAnstype.equals("0")){ %>table-row; <%}else{%>none; <%}%>">
                                                            <th>&nbsp;</th>
                                                            <th>&nbsp;</th>
                                                            <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                            <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                            </td>
                                                        </tr>
                                                        <tr id="answerType1<%=a %>_<%=i %>" style="display: <%if(otherQueAnstype == null || otherQueAnstype.equals("") || otherQueAnstype.equals("0")){ %> table-row; <%}else{%> none; <%}%>">
                                                            <th>&nbsp;</th>
                                                            <th>&nbsp;</th>
                                                            <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                            <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                        </tr>
                                                        <tr id="answerType2<%=a %>_<%=i %>" style="display: none;">
                                                            <th>&nbsp;</th><th>&nbsp;</th>
                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
                                                        </tr>
                                                    </table>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="OTHERsavebtndivOfQ<%=a %>_<%=i %>" style="display: none" align="center">
                                           
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit" id="addQButton"/>
                                            
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('OTHERsavebtndivOfQ<%=a %>_<%=i %>','OTHERnewquedivOfQ<%=a %>_<%=i %>');"/>
                                        </div>
                                    </form>
                                    <div id="OTHEReditquedivOfQ<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                    </div>
                                    <%
                                        } else if (uF.parseToInt(innerList1.get(3)) == 1){
                                        			if (uF.parseToInt(innerList1.get(2)) == 1) {
                                        				String CGOMScoreUID="";
                                        				String CGOMGoalUID="";
                                        				String CGOMObjectiveUID="";
                                        				String CGOMMeasureUID="";
                                        				String CGOMQueUID="";
                                        				double CGOMtotScoreWeight = 0;
                                        				String queAnstype="",sectionattribute="";
                                        				String newscorecnt = null;
                                        				String newgoalcnt = null;
                                        				String newobjcnt = null;
                                        				String newmeasurecnt = null;
                                        				String newquecnt = null;
                                        				List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                        				Map<String, List<List<String>>> scoreMp = list.get(0);
                                        				Map<String, List<List<String>>> measureMp = list.get(1);
                                        				Map<String, List<List<String>>> questionMp = list.get(2);
                                        				Map<String, List<List<String>>> GoalMp = list.get(3);
                                        				Map<String, List<List<String>>> objectiveMp = list.get(4);
                                        				List<List<String>> scoreList1 = scoreMp.get(innerList1.get(0));
                                        %>
                                    <ul id="" class="ul_class1">
                                        <li>
                                            <div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
                                                <div style="text-align:left; height: 35px;">
                                                    <%-- <blockquote><strong><%=a+1%>.<%=i+1%>)&nbsp;</strong>Competencies + Goals + Objectives + Measures --%>
                                                    <%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) {%>
                                                    &nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
                                                    <span style="float: right; margin-right: 333px;"><strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"0")%>%</span>
                                                    <div style="width: 70%">
                                                        <ul id="" class="ul_subsection">
                                                            <li><%=innerList1.get(4)%><br/>
                                                                <%=innerList1.get(5)%>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <%} %>
                                                    <div style="width: 100%; float: left;">
                                                        <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                        <span style="float: right;">
                                                        <a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage%>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                        <% if(fromPage != null && fromPage.equals("SRR")) { %>
                                                        <a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                        <% } else { %>
                                                        <a  title="Delete" href="DeleteAppraisalLevelAndSystem.action?from=SR&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                        <% } %>
                                                        </span>
                                                        <%} %>
                                                        <span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
                                                        <!-- </blockquote> -->
                                                    </div>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
                                                    <!-- <b>Score Card</b> -->
                                                    <b>Competencies</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.6%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
                                                    <b>Goal </b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.6%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
                                                    <b>Objective </b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.6%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 10%;  text-align: center;">
                                                    <b>Measure</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.6%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 17%;  text-align: center;">
                                                    <b>Question</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 6.5%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <%
                                                    CGOMtotScoreWeight = 0;
                                                    List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
                                                    int intscorecnt = scoreList != null ? scoreList.size()+1 : 1 ;
                                                    newscorecnt = intscorecnt+"";
                                                    for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                    	List<String> innerList = scoreList.get(j);
                                                    	CGOMtotScoreWeight += uF.parseToDouble(innerList.get(2));
                                                    }
                                                    
                                                    CGOMScoreUID = innerList1.get(0);
                                                    	for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                    		List<String> innerList = scoreList.get(j);
                                                    		z++;
                                                    		queAnstype = innerList.get(4);
                                                    		sectionattribute = innerList.get(5);
                                                    %>
                                                <div style="overflow: hidden; float: left; width: 99.9%;">
                                                    <div
                                                        style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.1%;">
                                                        <%-- <p style="padding-left:10px" onmouseover="mover('addnew<%=z %>','editexist<%=z %>');" onmouseout="mout('addnew<%=z %>','editexist<%=z %>');"> --%>
                                                        <p>
                                                            <span style="float: left; margin-left: 4px;"><%=innerList.get(1)%></span>
                                                            <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                            <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'score','<%=CGOMtotScoreWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>s','answerType1<%=a %>_<%=i %>s','0')" title="Add New">Add</a></span> --%>
                                                            <span style="float: left; margin-left: 5px;">
                                                            <a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=innerList.get(0)%>','score','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>se','<%=innerList.get(4) %>','<%=CGOMtotScoreWeight %>','<%=j+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                            <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                            <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=innerList.get(0) %>','C','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                            <%} else {%>
                                                            <a  title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=innerList.get(0) %>&type=C" onclick="return confirm('Are you sure you want to delete this Compentency?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                            <%} %>
                                                            <%} %>
                                                        </p>
                                                    </div>
                                                    <div
                                                        style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 6%; text-align: right;">
                                                        <p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
                                                    </div>
                                                    <div
                                                        style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 79.6%;">
                                                        <%
                                                            double CGOMtotGoalWeight = 0;
                                                            List<List<String>> goalList = GoalMp.get(innerList.get(0));
                                                            int intgoalcnt = goalList != null ? goalList.size()+1 : 1 ;
                                                            newgoalcnt = (j+1)+"."+intgoalcnt+"";
                                                            for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                            	List<String> goalinnerList = goalList.get(k);
                                                            	CGOMtotGoalWeight += uF.parseToDouble(goalinnerList.get(2));
                                                            }
                                                            
                                                            CGOMGoalUID =  innerList.get(0);
                                                            	for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                            		List<String> goalinnerList = goalList.get(k);
                                                            		z++;
                                                            						
                                                            %>
                                                        <div style="overflow: hidden; float: left; width: 100%;">
                                                            <div
                                                                style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 17.8%;">
                                                                <p>
                                                                    <span style="float: left; margin-left: 4px;"><%=goalinnerList.get(1)%></span>
                                                                    <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                    <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'goal','<%=CGOMtotGoalWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>g','answerType1<%=a %>_<%=i %>g','0')" title="Add New">Add</a></span> --%>
                                                                    <span style="float: left; margin-left: 5px;">
                                                                    <a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=goalinnerList.get(0)%>','goal','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>ge','<%=innerList.get(4) %>','<%=CGOMtotGoalWeight %>','<%=j+1 %>.<%=k+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                    <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                    <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=goalinnerList.get(0) %>','G','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                    <%} else {%>
                                                                    <a  title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId%>&queID=<%=goalinnerList.get(0)%>&type=G" onclick="return confirm('Are you sure you want to delete this Goal?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                    <%} %>
                                                                    </span>	
                                                                    <%} %>
                                                                </p>
                                                            </div>
                                                            <div
                                                                style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 7.3%; text-align: right;">
                                                                <p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
                                                            </div>
                                                            <div
                                                                style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 74.7%;">
                                                                <%
                                                                    double CGOMtotObjectiveWeight = 0;
                                                                    List<List<String>> objectiveList = objectiveMp.get(goalinnerList.get(0));
                                                                    int intobjcnt = objectiveList != null ? objectiveList.size()+1 : 1 ;
                                                                    newobjcnt = (j+1)+"."+(k+1)+"."+intobjcnt+"";
                                                                    for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
                                                                    	List<String> objectivelinnerList = objectiveList.get(l);
                                                                    	CGOMtotObjectiveWeight += uF.parseToDouble(objectivelinnerList.get(2));
                                                                    }
                                                                    
                                                                    CGOMObjectiveUID =  goalinnerList.get(0);
                                                                    	for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
                                                                    		List<String> objectivelinnerList = objectiveList.get(l);
                                                                    		z++;
                                                                    							
                                                                    %>
                                                                <div style="overflow: hidden; float: left; width: 100%;">
                                                                    <div
                                                                        style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24.6%;">
                                                                        <p>
                                                                            <span style="float: left; margin-left: 4px;"><%=objectivelinnerList.get(1)%></span>
                                                                            <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                            <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'objective','<%=CGOMtotObjectiveWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>o','answerType1<%=a %>_<%=i %>o','0')" title="Add New">Add</a></span> --%>
                                                                            <span style="float: left; margin-left: 5px;">
                                                                            <a id="editexist<%=z%>" href="javascript:void(0)"  onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=objectivelinnerList.get(0)%>','objective','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>oe','<%=innerList.get(4) %>','<%=CGOMtotObjectiveWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                            <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                            <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=objectivelinnerList.get(0) %>','O','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                            <%} else {%>
                                                                            <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId%>&queID=<%=objectivelinnerList.get(0)%>&type=O" onclick="return confirm('Are you sure you want to delete this Objective?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                            <%} %>
                                                                            </span>  <%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>oe','answerType1<%=a %>_<%=i %>oe','0') --%>
                                                                            <%} %>
                                                                        </p>
                                                                    </div>
                                                                    <div
                                                                        style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 10.2%; text-align: right;">
                                                                        <p style="margin: 0px 10px 0px 0px;"><%=objectivelinnerList.get(2)%>%</p>
                                                                    </div>
                                                                    <div
                                                                        style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 64.7%;">
                                                                        <%
                                                                            double CGOMtotMeasureWeight = 0;
                                                                            List<List<String>> measureList = measureMp.get(objectivelinnerList.get(0));
                                                                            int intmeasurecnt = measureList != null ? measureList.size()+1 : 1 ;
                                                                            newmeasurecnt = (j+1)+"."+(k+1)+"."+(l+1)+"."+intmeasurecnt+"";
                                                                            for (int m = 0; measureList != null && m < measureList.size(); m++) {
                                                                            	List<String> measureinnerList = measureList.get(m);
                                                                            	CGOMtotMeasureWeight += uF.parseToDouble(measureinnerList.get(2));
                                                                            }
                                                                            
                                                                            CGOMMeasureUID =  objectivelinnerList.get(0);
                                                                            		for (int m = 0; measureList != null && m < measureList.size(); m++) {
                                                                            			List<String> measureinnerList = measureList.get(m);
                                                                            			z++;
                                                                            %>
                                                                        <div style="overflow: hidden; float: left; width: 100%;">
                                                                            <div
                                                                                style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24%;">
                                                                                <p>
                                                                                    <span style="float: left; margin-left: 4px;"><%=measureinnerList.get(1)%></span>
                                                                                    <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                                    <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'measure','<%=CGOMtotMeasureWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>m','answerType1<%=a %>_<%=i %>m','0')" title="Add New">Add</a></span> --%>
                                                                                    <span style="float: left; margin-left: 5px;">
                                                                                    <a id="editexist<%=z%>" href="javascript:void(0)"  onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=measureinnerList.get(0)%>','measure','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>me','<%=innerList.get(4) %>','<%=CGOMtotMeasureWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>.<%=m+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                                    <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                                    <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=measureinnerList.get(0) %>','M','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                                    <%} else {%>
                                                                                    <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId%>&queID=<%=measureinnerList.get(0)%>&type=M" onclick="return confirm('Are you sure you want to delete this Measure?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                                    <%} %>
                                                                                    </span> 	<%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>me','answerType1<%=a %>_<%=i %>me','0') --%>
                                                                                    <%} %>
                                                                                </p>
                                                                            </div>
                                                                            <div
                                                                                style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.5%; text-align: right;">
                                                                                <p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
                                                                            </div>
                                                                            <div style="overflow: hidden; float: left; width: 60.6%;">
                                                                                <%
                                                                                    double CGOMtotQueWeight = 0;
                                                                                    List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
                                                                                    int intquecnt = questionList != null ? questionList.size()+1 : 1 ;
                                                                                    newquecnt = (j+1)+"."+(k+1)+"."+(l+1)+"."+(m+1)+"."+intquecnt+"";
                                                                                    for (int n = 0; questionList != null && n < questionList.size(); n++) {
                                                                                    	List<String> question1List = questionList.get(n);
                                                                                    	CGOMtotQueWeight += uF.parseToDouble(question1List.get(1));
                                                                                    }
                                                                                    
                                                                                    //System.out.println("CGOMtotQueWeight :: "+ CGOMtotQueWeight);
                                                                                    CGOMQueUID =  measureinnerList.get(0);
                                                                                    		for (int n = 0; questionList != null && n < questionList.size(); n++) {
                                                                                    			List<String> question1List = questionList.get(n);
                                                                                    			z++;
                                                                                    									//queAnstype = goalinnerList.get(4);
                                                                                    									//sectionattribute = goalinnerList.get(5);
                                                                                    %>
                                                                                <div style="overflow: hidden; float: left; width: 100%;">
                                                                                    <div
                                                                                        style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 73%;">
                                                                                        <p>
                                                                                            <span style="float: left; margin-left: 4px;"><%=question1List.get(0)%></span>
                                                                                            <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                                            <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'quest','<%=CGOMtotQueWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>q','answerType1<%=a %>_<%=i %>q','0')" title="Add New">Add</a></span> --%>
                                                                                            <span style="float: left; margin-left: 10px;">
                                                                                            <a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=question1List.get(3)%>','quest','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>qe','<%=innerList.get(4) %>','<%=CGOMtotQueWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>.<%=m+1 %>.<%=n+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                                            <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                                            <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=question1List.get(3) %>','Q','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                                            <%} else {%>
                                                                                            <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId%>&queID=<%=question1List.get(3)%>&type=Q" onclick="return confirm('Are you sure you want to delete this Question?')" ><i class="fa fa-trash aria-hidden="true"></i></a>
                                                                                            <%} %>
                                                                                            </span>	<%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>qe','answerType1<%=a %>_<%=i %>qe','0') --%>
                                                                                            <%} %>
                                                                                        </p>
                                                                                    </div>
                                                                                    <div
                                                                                        style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 26%; text-align: right;">
                                                                                        <p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
                                                                                    </div>
                                                                                </div>
                                                                                <%
                                                                                    }
                                                                                    %>
                                                                                <%
                                                                                    if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                                <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'quest','<%=CGOMtotQueWeight %>','<%=CGOMQueUID %>','<%=newquecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>q','answerType1<%=a %>_<%=i %>q','0')" title="Add New">Add</a></span>
                                                                                <%} %>
                                                                            </div>
                                                                        </div>
                                                                        <%
                                                                            }
                                                                            %>
                                                                        <%
                                                                            if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                        <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'measure','<%=CGOMtotMeasureWeight %>','<%=CGOMMeasureUID %>','<%=newmeasurecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>m','answerType1<%=a %>_<%=i %>m','0')" title="Add New">Add</a></span>
                                                                        <%} %>
                                                                    </div>
                                                                </div>
                                                                <%
                                                                    }
                                                                    %>
                                                                <%
                                                                    if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'objective','<%=CGOMtotObjectiveWeight %>','<%=CGOMObjectiveUID %>','<%=newobjcnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>o','answerType1<%=a %>_<%=i %>o','0')" title="Add New">Add</a></span>
                                                                <%} %>
                                                            </div>
                                                        </div>
                                                        <%
                                                            }
                                                            %>
                                                        <%
                                                            if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                        <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'goal','<%=CGOMtotGoalWeight %>','<%=CGOMGoalUID %>','<%=newgoalcnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>g','answerType1<%=a %>_<%=i %>g','0')" title="Add New">Add</a></span>
                                                        <%} %>					
                                                    </div>
                                                </div>
                                                <%
                                                    }
                                                    %>
                                                <%
                                                    if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'score','<%=CGOMtotScoreWeight %>','<%=CGOMScoreUID %>','<%=newscorecnt %>');changeNewQuestionTypeOther('<%=queAnstype%>','answerType<%=a %>_<%=i %>s','answerType1<%=a %>_<%=i %>s','0')" title="Add New">Add</a></span>
                                                <%} %>
                                            </div>
                                        </li>
                                    </ul>
                                    <%-- <%} %> --%>
                                    <!-- this div is only created for some prblm -->			
                                    <%-- <div id="CGOMscorenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none"> --%>	
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCGOMsystemOfS_<%=a %>_<%=i %>" name="frmCGOMsystemOfS_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID20" id="UID20" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type20" id="type20" value="" />
                                        <div id="CGOMscorenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" style="width: 100%;">
                                                        <tr>
                                                            <th width="15%" style="text-align: right;">
                                                                <span id="CGOMScoreCntS" style="float: left;"></span>Level Type
                                                            </th>
                                                            <td>Competency
                                                                <%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGOMsavebtndivOfS<%=a %>_<%=i %>','CGOMscorenewquediv<%=a %>_<%=i %>')"/></span>  --%>
                                                                <span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGOMsavebtndivOfS<%=a %>_<%=i %>','CGOMscorenewquediv<%=a %>_<%=i %>')" ></i></span> 
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                            <td><input type="text" name="scoreSectionName" id="scoreSectionName" class="validateRequired" style="width: 450px;"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Description</th>
                                                            <td><input type="text" name="scoreCardDescription" style="width: 450px;"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                            <td>
                                                                <input type="text" name="scoreCardWeightage" id="scoreCardWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'scoreCardWeightage<%=a %>_<%=i %>s','hidescoreCardWeightage<%=a %>_<%=i %>s')"/>
                                                                <input type="hidden" name="hidescoreCardWeightage" id="hidescoreCardWeightage<%=a %>_<%=i %>s" value="100"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </li>
                                                <li>
                                                    <ul class="ul_class">
                                                        <li>
                                                            <table class="table" style="width: 100%;">
                                                                <tr>
                                                                    <th width="15%" style="text-align: right;">
                                                                        <span id="CGOMGoalCntS" style="float: left;"></span>Level Type
                                                                    </th>
                                                                    <td>Goals
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                    <td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Description</th>
                                                                    <td><input type="text" name="goalDescription" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                    <td>
                                                                        <input type="text" name="goalWeightage" id="goalWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'goalWeightage<%=a %>_<%=i %>s','hidegoalWeightage<%=a %>_<%=i %>s')"/>
                                                                        <input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=a %>_<%=i %>s" value="100"/>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </li>
                                                        <li>
                                                            <ul class="ul_class">
                                                                <li>
                                                                    <table class="table" style="width: 100%;">
                                                                        <tr>
                                                                            <th width="15%" style="text-align: right;">
                                                                                <span id="CGOMObjCntS" style="float: left;"></span>Level Type
                                                                            </th>
                                                                            <td>Objective 
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                            <td><input type="text" name="objectiveSectionName" id="objectiveSectionName" class="validateRequired" style="width: 450px;"/></td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th style="text-align: right;">Description</th>
                                                                            <td><input type="text" name="objectiveDescription" style="width: 450px;"/></td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                            <td>
                                                                                <input type="text" name="objectiveWeightage" id="objectiveWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'objectiveWeightage<%=a %>_<%=i %>s','hideobjectiveWeightage<%=a %>_<%=i %>s')"/>
                                                                                <input type="hidden" name="hideobjectiveWeightage" id="hideobjectiveWeightage<%=a %>_<%=i %>s" value="100"/>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </li>
                                                                <li>
                                                                    <ul class="ul_class">
                                                                        <li>
                                                                            <table class="table" style="width: 100%;">
                                                                                <tr>
                                                                                    <th width="15%" style="text-align: right;">
                                                                                        <span id="CGOMMeasureCntS" style="float: left;"></span>Level Type
                                                                                    </th>
                                                                                    <td>Measures <input type="hidden" name="measureID"/>
                                                                                    </td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                                    <td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
                                                                                    </td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <th style="text-align: right;">Description</th>
                                                                                    <td><input type="text" name="measuresDescription" style="width: 450px;"/>
                                                                                    </td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                                    <td>
                                                                                        <input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>s','hidemeasureWeightage<%=a %>_<%=i %>s')"/>
                                                                                        <input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>s" value="100"/>
                                                                                    </td>
                                                                                </tr>
                                                                            </table>
                                                                        </li>
                                                                        <li>
                                                                            <ul>
                                                                                <li>
                                                                                    <table class="table" width="100%">
                                                                                        <tr>
                                                                                            <th><span id="CGOMQueCntS" style="float: left;"></span></th>
                                                                                            <%-- <%=a+1%>.<%=i+1%>.1) --%>
                                                                                            <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                                                <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
                                                                                                <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/>
                                                                                            </th>
                                                                                            <td colspan="3">
                                                                                                <span id="newquespan<%=a %>_<%=i %>s" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>s" value="0"/>
                                                                                                <textarea rows="2" name="question" id="question<%=a %>_<%=i %>s" class="validateRequired" style="width: 330px;"></textarea>
                                                                                                <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>s" style="width: 330px;"/> --%>
                                                                                                </span>
                                                                                                <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                                                <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>s','hideweightage<%=a %>_<%=i %>s')"/>
                                                                                                <input type="hidden" style="width: 35px !important;" name="hideweightage" id="hideweightage<%=a %>_<%=i %>s" value="100"/>
                                                                                                </span>
                                                                                                <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>s');" > +Q </a></span>
                                                                                                <span id="checkboxspan<%=a %>_<%=i %>s" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>s" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>s')"/>
                                                                                                <input type="hidden" id="status<%=a %>_<%=i %>s" name="status" value="0"/></span>
                                                                                                <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                                                    <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                                                <input type="hidden" name="questiontypename" value="0" />
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr id="answerType<%=a %>_<%=i %>s" style="display: none">
                                                                                            <th>&nbsp;</th>
                                                                                            <th>&nbsp;</th>
                                                                                            <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                                                            <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr id="answerType1<%=a %>_<%=i %>s" style="display: none">
                                                                                            <th>&nbsp;</th>
                                                                                            <th>&nbsp;</th>
                                                                                            <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                                                            <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                                                        </tr>
                                                                                        <tr id="answerType2<%=a %>_<%=i %>s" style="display: none;">
									                                                            <th>&nbsp;</th><th>&nbsp;</th>
									                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
									                                                        </tr>
                                                                                    </table>
                                                                                </li>
                                                                            </ul>
                                                                        </li>
                                                                    </ul>
                                                                </li>
                                                            </ul>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CGOMsavebtndivOfS<%=a %>_<%=i %>" style="display: none" align="center">
                                            
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                            
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfS<%=a %>_<%=i %>','CGOMscorenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <!-- </div> -->
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCGOMsystemOfG_<%=a %>_<%=i %>" name="frmCGOMsystemOfG_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID21" id="UID21" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type21" id="type21" value="" />
                                        <div id="CGOMgoalnewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" style="width: 100%;">
                                                        <tr>
                                                            <th width="15%" style="text-align: right;">
                                                                <span id="CGOMGoalCntG" style="float: left;"></span>Level Type
                                                            </th>
                                                            <td>Goals
                                                                <%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGOMsavebtndivOfG<%=a %>_<%=i %>','CGOMgoalnewquediv<%=a %>_<%=i %>')"/></span> --%>
                                                                <span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGOMsavebtndivOfG<%=a %>_<%=i %>','CGOMgoalnewquediv<%=a %>_<%=i %>')" ></i></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                            <td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Description</th>
                                                            <td><input type="text" name="goalDescription" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                            <td>
                                                                <input type="text" name="goalWeightage" id="goalWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'goalWeightage<%=a %>_<%=i %>g','hidegoalWeightage<%=a %>_<%=i %>g')"/>
                                                                <input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=a %>_<%=i %>g" value="100"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </li>
                                                <li>
                                                    <ul class="ul_class">
                                                        <li>
                                                            <table class="table" style="width: 100%;">
                                                                <tr>
                                                                    <th width="15%" style="text-align: right;">
                                                                        <span id="CGOMObjCntG" style="float: left;"></span>Level Type
                                                                    </th>
                                                                    <td>Objective 
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                    <td><input type="text" name="objectiveSectionName" id="objectiveSectionName" class="validateRequired" style="width: 450px;"/></td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Description</th>
                                                                    <td><input type="text" name="objectiveDescription" style="width: 450px;"/></td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                    <td>
                                                                        <input type="text" name="objectiveWeightage" id="objectiveWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'objectiveWeightage<%=a %>_<%=i %>g','hideobjectiveWeightage<%=a %>_<%=i %>g')"/>
                                                                        <input type="hidden" name="hideobjectiveWeightage" id="hideobjectiveWeightage<%=a %>_<%=i %>g" value="100"/>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </li>
                                                        <li>
                                                            <ul class="ul_class">
                                                                <li>
                                                                    <table class="table" style="width: 100%;">
                                                                        <tr>
                                                                            <th width="15%" style="text-align: right;">
                                                                                <span id="CGOMMeasureCntG" style="float: left;"></span>Level Type
                                                                            </th>
                                                                            <td>Measures <input type="hidden" name="measureID"/>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                            <td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th style="text-align: right;">Description</th>
                                                                            <td><input type="text" name="measuresDescription" style="width: 450px;"/>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                            <td>
                                                                                <input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>g','hidemeasureWeightage<%=a %>_<%=i %>g')"/>
                                                                                <input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>g" value="100"/>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </li>
                                                                <li>
                                                                    <ul>
                                                                        <li>
                                                                            <table class="table" width="100%">
                                                                                <tr>
                                                                                    <th><span id="CGOMQueCntG" style="float: left;"></span></th>
                                                                                    <%-- <%=a+1%>.<%=i+1%>.1) --%>
                                                                                    <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                                        <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>g" value="<%=queAnstype%>"/>
                                                                                        <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>g" value="<%=sectionattribute %>"/>
                                                                                    </th>
                                                                                    <td colspan="3">
                                                                                        <span id="newquespan<%=a %>_<%=i %>g" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>g" value="0"/>
                                                                                        <textarea rows="2" name="question" id="question<%=a %>_<%=i %>g" class="validateRequired" style="width: 330px;"></textarea>
                                                                                        <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>g" style="width: 330px;"/> --%>
                                                                                        </span>
                                                                                        <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                                        <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>g','hideweightage<%=a %>_<%=i %>g')"/>
                                                                                        <input type="hidden" style="width: 35px !important;" name="hideweightage" id="hideweightage<%=a %>_<%=i %>g" value="100"/>
                                                                                        </span>
                                                                                        <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>g');" > +Q </a></span>
                                                                                        <span id="checkboxspan<%=a %>_<%=i %>g" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>g" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>g')"/>
                                                                                        <input type="hidden" id="status<%=a %>_<%=i %>g" name="status" value="0"/></span>
                                                                                        <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                                            <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                                        <input type="hidden" name="questiontypename" value="0" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr id="answerType<%=a %>_<%=i %>g" style="display: none">
                                                                                    <th>&nbsp;</th>
                                                                                    <th>&nbsp;</th>
                                                                                    <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                                                    <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr id="answerType1<%=a %>_<%=i %>g" style="display: none">
                                                                                    <th>&nbsp;</th>
                                                                                    <th>&nbsp;</th>
                                                                                    <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                                                    <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                                                </tr>
                                                                                <tr id="answerType2<%=a %>_<%=i %>g" style="display: none;">
						                                                            <th>&nbsp;</th><th>&nbsp;</th>
						                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
						                                                        </tr>
                                                                            </table>
                                                                        </li>
                                                                    </ul>
                                                                </li>
                                                            </ul>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CGOMsavebtndivOfG<%=a %>_<%=i %>" style="display: none" align="center">
                                            
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                           
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfG<%=a %>_<%=i %>','CGOMgoalnewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCGOMsystemOfO_<%=a %>_<%=i %>" name="frmCGOMsystemOfO_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID22" id="UID22" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type22" id="type22" value="" />
                                        <div id="CGOMobjectivenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" style="width: 100%;">
                                                        <tr>
                                                            <th width="15%" style="text-align: right;">
                                                                <span id="CGOMObjCntO" style="float: left;"></span>Level Type
                                                            </th>
                                                            <td>Objective
                                                                <%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGOMsavebtndivOfO<%=a %>_<%=i %>','CGOMobjectivenewquediv<%=a %>_<%=i %>')"/></span> --%>
                                                                <span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGOMsavebtndivOfO<%=a %>_<%=i %>','CGOMobjectivenewquediv<%=a %>_<%=i %>')"></i></span>
                                                                
                                                                
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                            <td><input type="text" name="objectiveSectionName" id="objectiveSectionName" class="validateRequired" style="width: 450px;"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Description</th>
                                                            <td><input type="text" name="objectiveDescription" style="width: 450px;"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                            <td>
                                                                <input type="text" name="objectiveWeightage" id="objectiveWeightage<%=a %>_<%=i %>o" class="validateRequired" value="100" onkeyup="validateScore(this.value,'objectiveWeightage<%=a %>_<%=i %>o','hideobjectiveWeightage<%=a %>_<%=i %>o')"/>
                                                                <input type="hidden" name="hideobjectiveWeightage" id="hideobjectiveWeightage<%=a %>_<%=i %>o" value="100"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </li>
                                                <li>
                                                    <ul class="ul_class">
                                                        <li>
                                                            <table class="table" style="width: 100%;">
                                                                <tr>
                                                                    <th width="15%" style="text-align: right;">
                                                                        <span id="CGOMMeasureCntO" style="float: left;"></span>Level Type
                                                                    </th>
                                                                    <td>Measures <input type="hidden" name="measureID"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                    <td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Description</th>
                                                                    <td><input type="text" name="measuresDescription" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                    <td>
                                                                        <input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>o" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>o','hidemeasureWeightage<%=a %>_<%=i %>o')"/>
                                                                        <input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>o" value="100"/>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </li>
                                                        <li>
                                                            <ul>
                                                                <li>
                                                                    <table class="table" width="100%">
                                                                        <tr>
                                                                            <th><span id="CGOMQueCntO" style="float: left;"></span></th>
                                                                            <%-- <%=a+1%>.<%=i+1%>.1) --%>
                                                                            <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                                <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>o" value="<%=queAnstype%>"/>
                                                                                <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>o" value="<%=sectionattribute %>"/>
                                                                            </th>
                                                                            <td colspan="3">
                                                                                <span id="newquespan<%=a %>_<%=i %>o" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>o" value="0"/>
                                                                                <textarea rows="2" name="question" id="question<%=a %>_<%=i %>o" class="validateRequired" style="width: 330px;"></textarea>
                                                                                <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>o" style="width: 330px;"/> --%>
                                                                                </span>
                                                                                <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                                <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>o" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>o','hideweightage<%=a %>_<%=i %>o')"/>
                                                                                <input type="hidden" style="width: 35px !important;" name="hideweightage" id="hideweightage<%=a %>_<%=i %>o" value="100"/>
                                                                                </span>
                                                                                <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>o');" > +Q </a></span>
                                                                                <span id="checkboxspan<%=a %>_<%=i %>o" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>o" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>o')"/>
                                                                                <input type="hidden" id="status<%=a %>_<%=i %>o" name="status" value="0"/></span>
                                                                                <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                                    <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                                <input type="hidden" name="questiontypename" value="0" />
                                                                            </td>
                                                                        </tr>
                                                                        <tr id="answerType<%=a %>_<%=i %>o" style="display: none">
                                                                            <th>&nbsp;</th>
                                                                            <th>&nbsp;</th>
                                                                            <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                                            <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                                            </td>
                                                                        </tr>
                                                                        <tr id="answerType1<%=a %>_<%=i %>o" style="display: none">
                                                                            <th>&nbsp;</th>
                                                                            <th>&nbsp;</th>
                                                                            <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                                            <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                                        </tr>
                                                                        <tr id="answerType2<%=a %>_<%=i %>o" style="display: none;">
				                                                            <th>&nbsp;</th><th>&nbsp;</th>
				                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
				                                                        </tr>
                                                                    </table>
                                                                </li>
                                                            </ul>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CGOMsavebtndivOfO<%=a %>_<%=i %>" style="display: none" align="center">
                                           
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                           
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfO<%=a %>_<%=i %>','CGOMobjectivenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCGOMsystemOfM_<%=a %>_<%=i %>" name="frmCGOMsystemOfM_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID23" id="UID23" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type23" id="type23" value="" />
                                        <div id="CGOMmeasurenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" style="width: 100%;">
                                                        <tr>
                                                            <th width="15%" style="text-align: right;">
                                                                <span id="CGOMMeasureCntM" style="float: left;"></span>Level Type
                                                            </th>
                                                            <td>Measures <input type="hidden" name="measureID"/>
                                                                <%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGOMsavebtndivOfM<%=a %>_<%=i %>','CGOMmeasurenewquediv<%=a %>_<%=i %>')"/></span> --%>
                                                                <span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGOMsavebtndivOfM<%=a %>_<%=i %>','CGOMmeasurenewquediv<%=a %>_<%=i %>')"></i></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                            <td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Description</th>
                                                            <td><input type="text" name="measuresDescription" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                            <td>
                                                                <input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>m','hidemeasureWeightage<%=a %>_<%=i %>m')"/>
                                                                <input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>m" value="100"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </li>
                                                <li>
                                                    <ul>
                                                        <li>
                                                            <table class="table" width="100%">
                                                                <tr>
                                                                    <th><span id="CGOMQueCntM" style="float: left;"></span></th>
                                                                    <%-- <%=a+1%>.<%=i+1%>.1) --%>
                                                                    <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                        <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>m" value="<%=queAnstype%>"/>
                                                                        <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>m" value="<%=sectionattribute %>"/>
                                                                    </th>
                                                                    <td colspan="3">
                                                                        <span id="newquespan<%=a %>_<%=i %>m" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>m" value="0"/>
                                                                        <textarea rows="2" name="question" id="question<%=a %>_<%=i %>m" class="validateRequired" style="width: 330px;"></textarea>
                                                                        <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>m" style="width: 330px;"/> --%>
                                                                        </span>
                                                                        <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                        <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>m','hideweightage<%=a %>_<%=i %>m')"/>
                                                                        <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>m" value="100" />
                                                                        </span>
                                                                        <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>m');" > +Q </a></span>
                                                                        <span id="checkboxspan<%=a %>_<%=i %>m" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>m" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>m')"/>
                                                                        <input type="hidden" id="status<%=a %>_<%=i %>m" name="status" value="0"/></span>
                                                                        <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                            <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                        <input type="hidden" name="questiontypename" value="0" />
                                                                    </td>
                                                                </tr>
                                                                <tr id="answerType<%=a %>_<%=i %>m" style="display: none">
                                                                    <th>&nbsp;</th>
                                                                    <th>&nbsp;</th>
                                                                    <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                                    <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                                    </td>
                                                                </tr>
                                                                <tr id="answerType1<%=a %>_<%=i %>m" style="display: none">
                                                                    <th>&nbsp;</th>
                                                                    <th>&nbsp;</th>
                                                                    <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                                    <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                                </tr>
                                                                <tr id="answerType2<%=a %>_<%=i %>m" style="display: none;">
		                                                            <th>&nbsp;</th><th>&nbsp;</th>
		                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
		                                                        </tr>
                                                            </table>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CGOMsavebtndivOfM<%=a %>_<%=i %>" style="display: none" align="center">
                                           
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                            
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfM<%=a %>_<%=i %>','CGOMmeasurenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCGOMsystemOfQ_<%=a %>_<%=i %>" name="frmCGOMsystemOfQ_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID24" id="UID24" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type24" id="type24" value="" />
                                        <div id="CGOMquenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" width="100%">
                                                        <tr>
                                                            <th><span id="CGOMQueCntQ" style="float: left;"></span></th>
                                                            <%-- <%=a+1%>.<%=i+1%>.1) --%>
                                                            <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>q" value="<%=queAnstype%>"/>
                                                                <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>q" value="<%=sectionattribute %>"/>
                                                            </th>
                                                            <td colspan="3">
                                                                <span id="newquespan<%=a %>_<%=i %>q" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>q" value="0"/>
                                                                <textarea rows="2" name="question" id="question<%=a %>_<%=i %>q" class="validateRequired" style="width: 330px;"></textarea>
                                                                <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>q" style="width: 330px;"/> --%>
                                                                </span>
                                                                <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>q" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>q','hideweightage<%=a %>_<%=i %>q')"/>
                                                                <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>q" value="100" />
                                                                </span>
                                                                <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>q');" > +Q </a></span>
                                                                <span id="checkboxspan<%=a %>_<%=i %>q" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>q" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>q')"/>
                                                                <input type="hidden" id="status<%=a %>_<%=i %>q" name="status" value="0"/></span>
                                                                <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                    <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                <input type="hidden" name="questiontypename" value="0" />
                                                            </td>
                                                        </tr>
                                                        <tr id="answerType<%=a %>_<%=i %>q" style="display: none">
                                                            <th>&nbsp;</th>
                                                            <th>&nbsp;</th>
                                                            <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                            <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                            </td>
                                                        </tr>
                                                        <tr id="answerType1<%=a %>_<%=i %>q" style="display: none">
                                                            <th>&nbsp;</th>
                                                            <th>&nbsp;</th>
                                                            <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                            <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                        </tr>
                                                        <tr id="answerType2<%=a %>_<%=i %>q" style="display: none;">
                                                            <th>&nbsp;</th><th>&nbsp;</th>
                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
                                                        </tr>
                                                    </table>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CGOMsavebtndivOfQ<%=a %>_<%=i %>" style="display: none" align="center">
                                           
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                            
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfQ<%=a %>_<%=i %>','CGOMquenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <div id="CGOMeditquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                    </div>
                                    <%
                                        } else if (uF.parseToInt(innerList1.get(2)) == 2) {
                                        	String CMScoreUID="";
                                        	String CMMeasureUID="";
                                        	String CMQueUID="";
                                        	String newscorecnt=null;
                                        	String newmeasurecnt=null;
                                        	String newquecnt=null;
                                        	//System.out.println("in 1 2 levelMp.get(innerList1.get(0))=====>"+levelMp.get(innerList1.get(0)));
                                        				List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                        				Map<String, List<List<String>>> scoreMp = list.get(0);
                                        				Map<String, List<List<String>>> measureMp = list.get(1);
                                        				Map<String, List<List<String>>> questionMp = list.get(2);
                                        %>
                                    <ul id="" class="ul_class1">
                                        <li>
                                            <div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
                                                <div style="text-align:left; height: 35px;">
                                                    <%-- <blockquote><strong><%=a+1%>.<%=i+1%>)&nbsp;</strong>Competencies + Measures --%>
                                                    <%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) { %>
                                                    &nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
                                                    <span style="float: right; margin-right: 333px;"><strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"0")%>%</span>
                                                    <div style="width: 70%">
                                                        <ul id="" class="ul_subsection">
                                                            <li><%=innerList1.get(4)%><br/>
                                                                <%=innerList1.get(5)%>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <%} %>
                                                    <div style="width: 100%; float: left;">
                                                        <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                        <span style="float: right;">
                                                        <a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage%>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                        <% if(fromPage != null && fromPage.equals("SRR")) { %>
                                                        <a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                        <% } else { %>
                                                        <a title="Delete" href="DeleteAppraisalLevelAndSystem.action?from=SR&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId%>&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                        <% } %>
                                                        </span>
                                                        <%} %>
                                                        <span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
                                                        <!-- </blockquote> -->
                                                    </div>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 24.9%;  text-align: center;">
                                                    <!-- <b>Score Card</b> -->
                                                    <b>Competencies</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 9%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 18.3%;  text-align: center;">
                                                    <b>Measure</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 9.1%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 28.4%;  text-align: center;">
                                                    <b>Question</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 9%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div style="overflow: hidden; float: left; width: 99.9%;">
                                                    <%
                                                        double CMtotScoreWeight = 0;
                                                        List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
                                                        int intscorecnt = scoreList != null ? scoreList.size()+1 : 1;
                                                        newscorecnt = intscorecnt+"";
                                                        for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                        	List<String> innerList = scoreList.get(j);
                                                        	CMtotScoreWeight += uF.parseToDouble(innerList.get(2));
                                                        }
                                                        
                                                        	String queAnstype="",sectionattribute="";
                                                        	
                                                        	CMScoreUID =innerList1.get(0);
                                                        	for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                        		List<String> innerList = scoreList.get(j);
                                                        		z++;
                                                        		queAnstype = innerList.get(4);
                                                        		sectionattribute = innerList.get(5);
                                                        %>
                                                    <div style="overflow: hidden; float: left; width: 100%;">
                                                        <div
                                                            style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24.9%;">
                                                            <p>
                                                                <span style="float: left; margin-left: 4px;"><%=innerList.get(1)%></span>
                                                                <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCMnewquediv('<%=a %>_<%=i %>', 'score', '<%=CMtotScoreWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>s','answerType1<%=a %>_<%=i %>s','0')" title="Add New">Add</a></span> --%>
                                                                <span style="float: left; margin-left: 5px;">
                                                                <a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=innerList.get(0)%>','score','CMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>se','<%=innerList.get(4) %>','<%=CMtotScoreWeight %>','<%=j+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=innerList.get(0) %>','C','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                <%} else {%>
                                                                <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=innerList.get(0)%>&type=C" onclick="return confirm('Are you sure you want to delete this Compentency?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                <%} %>
                                                                </span>  <%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>se','answerType1<%=a %>_<%=i %>se','0') --%>
                                                                <%} %>
                                                            </p>
                                                        </div>
                                                        <div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 9.4%; text-align: right;">
                                                            <p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
                                                        </div>
                                                        <div
                                                            style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 64.7%;">
                                                            <%
                                                                double CMtotMeasureWeight = 0;
                                                                List<List<String>> measureList = measureMp.get(innerList.get(0));
                                                                int intmeasurecnt = measureList != null ? measureList.size()+1 : 1;
                                                                newmeasurecnt = ""+(j+1)+"."+intmeasurecnt;
                                                                for (int k = 0; measureList != null && k < measureList.size(); k++) {
                                                                	List<String> measureinnerList = measureList.get(k);
                                                                	CMtotMeasureWeight += uF.parseToDouble(measureinnerList.get(2));
                                                                }
                                                                
                                                                CMMeasureUID =innerList.get(0);
                                                                		for (int k = 0; measureList != null && k < measureList.size(); k++) {
                                                                			List<String> measureinnerList = measureList.get(k);
                                                                			z++;
                                                                %>
                                                            <div style="overflow: hidden; float: left; width: 100%;">
                                                                <div
                                                                    style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 28.3%;">
                                                                    <p>
                                                                        <span style="float: left; margin-left: 4px;"><%=measureinnerList.get(1)%></span>
                                                                        <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                        <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCMnewquediv('<%=a %>_<%=i %>', 'measure', '<%=CMtotMeasureWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>m','answerType1<%=a %>_<%=i %>m','0')" title="Add New">Add</a></span> --%>
                                                                        <span style="float: left; margin-left: 5px;">
                                                                        <a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=measureinnerList.get(0)%>','measure','CMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>me','<%=innerList.get(4) %>','<%=CMtotMeasureWeight %>','<%=j+1 %>.<%=k+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                        <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                        <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=measureinnerList.get(0) %>','M','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                        <%} else {%>
                                                                        <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=measureinnerList.get(0)%>&type=M" onclick="return confirm('Are you sure you want to delete this Measure?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                        <%} %>
                                                                        </span>	<%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>me','answerType1<%=a %>_<%=i %>me','0') --%>
                                                                        <%} %>
                                                                    </p>
                                                                </div>
                                                                <div
                                                                    style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.4%; text-align: right;">
                                                                    <p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
                                                                </div>
                                                                <div
                                                                    style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 57.1%;">
                                                                    <%
                                                                        double CMtotQueWeight = 0;
                                                                        List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
                                                                        int intquecnt = questionList != null ? questionList.size()+1 : 1;
                                                                        newquecnt = ""+(j+1)+"."+(k+1)+"."+intquecnt;
                                                                        for (int l = 0; questionList != null && l < questionList.size(); l++) {
                                                                        	List<String> question1List = questionList.get(l);
                                                                        	CMtotQueWeight += uF.parseToDouble(question1List.get(1));
                                                                        }
                                                                        CMQueUID =measureinnerList.get(0);
                                                                        		for (int l = 0; questionList != null && l < questionList.size(); l++) {
                                                                        			List<String> question1List = questionList.get(l);
                                                                        			z++;
                                                                        %>
                                                                    <div style="overflow: hidden; float: left; width: 100%;">
                                                                        <div
                                                                            style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 77%;">
                                                                            <p>
                                                                                <span style="float: left; margin-left: 4px;"><%=question1List.get(0)%></span>
                                                                                <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                                <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCMnewquediv('<%=a %>_<%=i %>', 'quest', '<%=CMtotQueWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>q','answerType1<%=a %>_<%=i %>q','0')" title="Add New">Add</a></span> --%>
                                                                                <span style="float: left; margin-left: 10px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=question1List.get(3)%>','quest','CMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>qe','<%=innerList.get(4) %>','<%=CMtotQueWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                                <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                                <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=question1List.get(3) %>','Q','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                                <%} else {%>
                                                                                <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=question1List.get(3)%>&type=Q" onclick="return confirm('Are you sure you want to delete this Question?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                                <%} %>
                                                                                </span>	<%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>qe','answerType1<%=a %>_<%=i %>qe','0') --%>
                                                                                <%} %>
                                                                            </p>
                                                                        </div>
                                                                        <div
                                                                            style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 22%; text-align: right;">
                                                                            <p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
                                                                        </div>
                                                                    </div>
                                                                    <%
                                                                        }
                                                                        %>
                                                                    <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                    <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCMnewquediv('<%=a %>_<%=i %>', 'quest', '<%=CMtotQueWeight %>','<%=CMQueUID %>','<%=newquecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>q','answerType1<%=a %>_<%=i %>q','0')" title="Add New">Add</a></span>
                                                                    <%} %>
                                                                </div>
                                                            </div>
                                                            <%
                                                                }
                                                                %>
                                                            <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                            <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCMnewquediv('<%=a %>_<%=i %>', 'measure', '<%=CMtotMeasureWeight %>','<%=CMMeasureUID %>','<%=newmeasurecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>m','answerType1<%=a %>_<%=i %>m','0')" title="Add New">Add</a></span>
                                                            <%} %>
                                                        </div>
                                                    </div>
                                                    <%
                                                        }
                                                        %>
                                                    <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                    <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCMnewquediv('<%=a %>_<%=i %>', 'score', '<%=CMtotScoreWeight %>','<%=CMScoreUID %>','<%=newscorecnt %>');changeNewQuestionTypeOther('<%=queAnstype %>','answerType<%=a %>_<%=i %>s','answerType1<%=a %>_<%=i %>s','0')" title="Add New">Add</a></span>
                                                    <%} %>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                    <!-- this div is only created for some prblm -->			
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCMsystemOfS_<%=a %>_<%=i %>" name="frmCMsystemOfS_<%=a %>_<%=i %>"  method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID30" id="UID30" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type30" id="type30" value="" />
                                        <div id="CMscorenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" style="width: 100%;">
                                                        <tr>
                                                            <th width="15%" style="text-align: right;">
                                                                <span id="CMScoreCntS" style="float: left;"></span>Level Type
                                                            </th>
                                                            <td>Competency
                                                                <%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CMsavebtndivOfS<%=a %>_<%=i %>','CMscorenewquediv<%=a %>_<%=i %>')"/></span> --%>
                                                                <span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CMsavebtndivOfS<%=a %>_<%=i %>','CMscorenewquediv<%=a %>_<%=i %>')"></i></span>
                                                                
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                            <td><input type="text" name="scoreSectionName" id="scoreSectionName" class="validateRequired" style="width: 450px;"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Description</th>
                                                            <td><input type="text" name="scoreCardDescription" style="width: 450px;"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                            <td>
                                                                <input type="text" name="scoreCardWeightage" id="scoreCardWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'scoreCardWeightage<%=a %>_<%=i %>s','hidescoreCardWeightage<%=a %>_<%=i %>s')"/>
                                                                <input type="hidden" name="hidescoreCardWeightage" id="hidescoreCardWeightage<%=a %>_<%=i %>s" value="100"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </li>
                                                <li>
                                                    <ul class="ul_class">
                                                        <li>
                                                            <table class="table" style="width: 100%;">
                                                                <tr>
                                                                    <th width="15%" style="text-align: right;">
                                                                        <span id="CMMeasureCntS" style="float: left;"></span>Level Type
                                                                    </th>
                                                                    <td>Measures <input type="hidden" name="measureID"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                    <td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Description</th>
                                                                    <td><input type="text" name="measuresDescription" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                    <td>
                                                                        <input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>s','hidemeasureWeightage<%=a %>_<%=i %>s')"/>
                                                                        <input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>s" value="100"/>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </li>
                                                        <li>
                                                            <ul>
                                                                <li>
                                                                    <table class="table" width="100%">
                                                                        <tr>
                                                                            <th><span id="CMQueCntS" style="float: left;"></span></th>
                                                                            <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                                <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
                                                                                <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/>
                                                                            </th>
                                                                            <td colspan="3">
                                                                                <span id="newquespan<%=a %>_<%=i %>s" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>s" value="0"/>
                                                                                <textarea rows="2" name="question" id="question<%=a %>_<%=i %>s" class="validateRequired" style="width: 330px;"></textarea>
                                                                                <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>s" style="width: 330px;"/> --%>
                                                                                </span>
                                                                                <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                                <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>s','hideweightage<%=a %>_<%=i %>s')"/>
                                                                                <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>s" value="100"/>
                                                                                </span>
                                                                                <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>s');" > +Q </a></span>
                                                                                <span id="checkboxspan<%=a %>_<%=i %>s" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>s" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>s')"/>
                                                                                <input type="hidden" id="status<%=a %>_<%=i %>s" name="status" value="0"/></span>
                                                                                <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                                    <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                                <input type="hidden" name="questiontypename" value="0" />
                                                                            </td>
                                                                        </tr>
                                                                        <tr id="answerType<%=a %>_<%=i %>s" style="display: none">
                                                                            <th>&nbsp;</th>
                                                                            <th>&nbsp;</th>
                                                                            <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                                            <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                                            </td>
                                                                        </tr>
                                                                        <tr id="answerType1<%=a %>_<%=i %>s" style="display: none">
                                                                            <th>&nbsp;</th>
                                                                            <th>&nbsp;</th>
                                                                            <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                                            <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                                        </tr>
                                                                        <tr id="answerType2<%=a %>_<%=i %>s" style="display: none;">
				                                                            <th>&nbsp;</th><th>&nbsp;</th>
				                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
				                                                        </tr>
                                                                    </table>
                                                                </li>
                                                            </ul>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CMsavebtndivOfS<%=a %>_<%=i %>" style="display: none" align="center">
                                            
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                           
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CMsavebtndivOfS<%=a %>_<%=i %>','CMscorenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCMsystemOfM_<%=a %>_<%=i %>" name="frmCMsystemOfM_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID31" id="UID31" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type31" id="type31" value="" />
                                        <div id="CMmeasurenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" style="width: 100%;">
                                                        <tr>
                                                            <th width="15%" style="text-align: right;">
                                                                <span id="CMMeasureCntM" style="float: left;"></span>Level Type
                                                            </th>
                                                            <td>Measures <input type="hidden" name="measureID"/>
                                                                <%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CMsavebtndivOfM<%=a %>_<%=i %>','CMmeasurenewquediv<%=a %>_<%=i %>')"/></span> --%>
                                                                <span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CMsavebtndivOfM<%=a %>_<%=i %>','CMmeasurenewquediv<%=a %>_<%=i %>')"></i></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                            <td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Description</th>
                                                            <td><input type="text" name="measuresDescription" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                            <td>
                                                                <input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>m','hidemeasureWeightage<%=a %>_<%=i %>m')"/>
                                                                <input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>m" value="100"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </li>
                                                <li>
                                                    <ul>
                                                        <li>
                                                            <table class="table" width="100%">
                                                                <tr>
                                                                    <th><span id="CMQueCntM" style="float: left;"></span></th>
                                                                    <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                        <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>m" value="<%=queAnstype%>"/>
                                                                        <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>m" value="<%=sectionattribute %>"/>
                                                                    </th>
                                                                    <td colspan="3">
                                                                        <span id="newquespan<%=a %>_<%=i %>m" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>m" value="0"/>
                                                                        <textarea rows="2" name="question" id="question<%=a %>_<%=i %>m" class="validateRequired" style="width: 330px;"></textarea>
                                                                        <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>m" style="width: 330px;"/> --%>
                                                                        </span>
                                                                        <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                        <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>m','hideweightage<%=a %>_<%=i %>m')"/>
                                                                        <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>m" value="100"/>
                                                                        </span>
                                                                        <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>m');" > +Q </a></span>
                                                                        <span id="checkboxspan<%=a %>_<%=i %>m" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>m" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>m')"/>
                                                                        <input type="hidden" id="status<%=a %>_<%=i %>m" name="status" value="0"/></span>
                                                                        <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                            <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                        <input type="hidden" name="questiontypename" value="0" />
                                                                    </td>
                                                                </tr>
                                                                <tr id="answerType<%=a %>_<%=i %>m" style="display: none">
                                                                    <th>&nbsp;</th>
                                                                    <th>&nbsp;</th>
                                                                    <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                                    <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                                    </td>
                                                                </tr>
                                                                <tr id="answerType1<%=a %>_<%=i %>m" style="display: none">
                                                                    <th>&nbsp;</th>
                                                                    <th>&nbsp;</th>
                                                                    <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                                    <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                                </tr>
                                                                <tr id="answerType2<%=a %>_<%=i %>m" style="display: none;">
		                                                            <th>&nbsp;</th><th>&nbsp;</th>
		                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
		                                                        </tr>
                                                            </table>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CMsavebtndivOfM<%=a %>_<%=i %>" style="display: none" align="center">
                                            
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                           
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CMsavebtndivOfM<%=a %>_<%=i %>','CMmeasurenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCMsystemOfQ_<%=a %>_<%=i %>" name="frmCMsystemOfQ_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID32" id="UID32" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type32" id="type32" value="" />
                                        <div id="CMquenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" width="100%">
                                                        <tr>
                                                            <th><span id="CMQueCntQ" style="float: left;"></span></th>
                                                            <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>q" value="<%=queAnstype%>"/>
                                                                <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>q" value="<%=sectionattribute %>"/>
                                                            </th>
                                                            <td colspan="3">
                                                                <span id="newquespan<%=a %>_<%=i %>q" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>q" value="0"/>
                                                                <textarea rows="2" name="question" id="question<%=a %>_<%=i %>q" class="validateRequired" style="width: 330px;"></textarea>
                                                                <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>q" style="width: 330px;"/> --%>
                                                                </span>
                                                                <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>q" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>q','hideweightage<%=a %>_<%=i %>q')"/>
                                                                <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>q" value="100" />
                                                                </span>
                                                                <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>q');" > +Q </a></span>
                                                                <span id="checkboxspan<%=a %>_<%=i %>q" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>q" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>q')"/>
                                                                <input type="hidden" id="status<%=a %>_<%=i %>q" name="status" value="0"/></span>
                                                                <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                    <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                <input type="hidden" name="questiontypename" value="0" />
                                                            </td>
                                                        </tr>
                                                        <tr id="answerType<%=a %>_<%=i %>q" style="display: none">
                                                            <th>&nbsp;</th>
                                                            <th>&nbsp;</th>
                                                            <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                            <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                            </td>
                                                        </tr>
                                                        <tr id="answerType1<%=a %>_<%=i %>q" style="display: none">
                                                            <th>&nbsp;</th>
                                                            <th>&nbsp;</th>
                                                            <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                            <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                        </tr>
                                                        <tr id="answerType2<%=a %>_<%=i %>q" style="display: none;">
                                                            <th>&nbsp;</th><th>&nbsp;</th>
                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
                                                        </tr>
                                                    </table>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CMsavebtndivOfQ<%=a %>_<%=i %>" style="display: none" align="center">
                                           
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                            
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CMsavebtndivOfQ<%=a %>_<%=i %>','CMquenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <div id="CMeditquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                    </div>
                                    <%
                                        } else {
                                        	String CGMScoreUID="";
                                        	String CGMGoalUID="";
                                        	String CGMMeasureUID="";
                                        	String CGMQueUID="";
                                        	String newscorecnt = null;
                                        	String newgoalcnt = null;
                                        	String newmeasurecnt = null;
                                        	String newquecnt = null;
                                        	//System.out.println("in 1 3 levelMp.get(innerList1.get(0))=====>"+levelMp.get(innerList1.get(0)));
                                        				List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
                                        				Map<String, List<List<String>>> scoreMp = list.get(0);
                                        				Map<String, List<List<String>>> measureMp = list.get(1);
                                        				Map<String, List<List<String>>> questionMp = list.get(2);
                                        				Map<String, List<List<String>>> GoalMp = list.get(3);
                                        %>
                                    <ul id="" class="ul_class1">
                                        <li>
                                            <div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
                                                <div style="text-align:left; height: 35px;">
                                                    <%-- <blockquote><strong><%=a+1%>.<%=i+1%>)&nbsp;</strong>Competencies + Goals + Measures --%>
                                                    <%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) {%>
                                                    &nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
                                                    <span style="float: right; margin-right: 333px;"><strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"0")%>%</span>
                                                    <div style="width: 70%">
                                                        <ul id="" class="ul_subsection">
                                                            <li><%=innerList1.get(4)%><br/>
                                                                <%=innerList1.get(5)%>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <%} %>
                                                    <div style="width: 100%; float: left;">
                                                        <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                        <span style="float: right;">
                                                        <a href="javascript: void(0)"  onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage%>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                        <% if(fromPage != null && fromPage.equals("SRR")) { %>
                                                        <a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                        <% } else { %>
                                                        <a title="Delete" href="DeleteAppraisalLevelAndSystem.action?from=SR&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                        <% } %>
                                                        </span>
                                                        <%} %>
                                                        <span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
                                                        <!-- </blockquote> -->
                                                    </div>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 16%;  text-align: center;">
                                                    <!-- <b>Score Card</b> -->
                                                    <b>Competencies</b>
                                                </div>
                                                <div 
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 16.5%;  text-align: center;">
                                                    <b>Goal </b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 8%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 12%;  text-align: center;">
                                                    <b>Measure</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.2%;  text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left;  border: 1px solid #eee; width: 25.8%; text-align: center;">
                                                    <b>Question</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left;  border: 1px solid #eee; width: 8.1%; text-align: center;">
                                                    <b>Weightage</b>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 99.9%;">
                                                    <%
                                                        double CGMtotScoreWeight = 0;
                                                        List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
                                                        int intscorecnt = scoreList != null ? scoreList.size()+1 : 1;
                                                        newscorecnt = intscorecnt+"";
                                                        String queAnstype="",sectionattribute="";
                                                        for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                        	List<String> innerList = scoreList.get(j);
                                                        	CGMtotScoreWeight += uF.parseToDouble(innerList.get(2));
                                                        }
                                                        
                                                        	CGMScoreUID =innerList1.get(0);
                                                        		for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
                                                        			List<String> innerList = scoreList.get(j);
                                                        			z++;
                                                        			queAnstype = innerList.get(4);
                                                        			sectionattribute = innerList.get(5);
                                                        %>
                                                    <div style="overflow: hidden; float: left; width: 100%;">
                                                        <div
                                                            style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 16%;">
                                                            <p>
                                                                <span style="float: left; margin-left: 4px;"><%=innerList.get(1)%></span>
                                                                <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'score', '<%=CGMtotScoreWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>s','answerType1<%=a %>_<%=i %>s','0')" title="Add New">Add</a></span> --%>
                                                                <span style="float: left; margin-left: 5px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=innerList.get(0)%>','score','CGMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>se','<%=innerList.get(4) %>','<%=CGMtotScoreWeight %>','<%=j+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=innerList.get(0) %>','C','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                <%} else {%>
                                                                <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=innerList.get(0) %>&type=C" onclick="return confirm('Are you sure you want to delete this Compentency?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                <%} %>
                                                                </span>  <%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>se','answerType1<%=a %>_<%=i %>se','0') --%>
                                                                <%} %>
                                                            </p>
                                                        </div>
                                                        <div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 7.2%; text-align: right;">
                                                            <p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
                                                        </div>
                                                        <div
                                                            style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 76.6%;">
                                                            <%
                                                                double CGMtotGoalWeight = 0;
                                                                List<List<String>> goalList = GoalMp.get(innerList.get(0));
                                                                int intgoalcnt = goalList != null ? goalList.size()+1 : 1;
                                                                newgoalcnt = (j+1)+"."+intgoalcnt+"";
                                                                for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                                	List<String> goalinnerList = goalList.get(k);
                                                                	CGMtotGoalWeight += uF.parseToDouble(goalinnerList.get(2));
                                                                }
                                                                
                                                                CGMGoalUID =innerList.get(0);
                                                                		for (int k = 0; goalList != null && k < goalList.size(); k++) {
                                                                			List<String> goalinnerList = goalList.get(k);
                                                                			z++;
                                                                %>
                                                            <div style="overflow: hidden; float: left; width: 100%;">
                                                                <div
                                                                    style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 22%;">
                                                                    <p>
                                                                        <span style="float: left; margin-left: 4px;"><%=goalinnerList.get(1)%></span>
                                                                        <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                        <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'goal', '<%=CGMtotGoalWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>g','answerType1<%=a %>_<%=i %>g','0')" title="Add New">Add</a></span> --%>
                                                                        <span style="float: left; margin-left: 5px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=innerList.get(0)%>','score','CGMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>se','<%=innerList.get(4) %>','<%=CGMtotScoreWeight %>','<%=j+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                        <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                        <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=goalinnerList.get(0) %>','G','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                        <%} else {%>
                                                                        <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=goalinnerList.get(0) %>&type=G" onclick="return confirm('Are you sure you want to delete this Goal?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                        <%} %>
                                                                        </span>		<%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>ge','answerType1<%=a %>_<%=i %>ge','0') --%>
                                                                        <%} %>
                                                                    </p>
                                                                </div>
                                                                <div
                                                                    style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 10.5%; text-align: right;">
                                                                    <p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
                                                                </div>
                                                                <div
                                                                    style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 66.4%;">
                                                                    <%
                                                                        double CGMtotMeasureWeight = 0;
                                                                        List<List<String>> measureList = measureMp.get(goalinnerList.get(0));
                                                                        int intmeasurecnt = measureList != null ? measureList.size()+1 : 1;
                                                                        newmeasurecnt = (j+1)+"."+(k+1)+"."+intmeasurecnt+"";
                                                                        for (int l = 0; measureList != null && l < measureList.size(); l++) {
                                                                        	List<String> measureinnerList = measureList.get(l);
                                                                        	CGMtotMeasureWeight += uF.parseToDouble(measureinnerList.get(2));
                                                                        }
                                                                        
                                                                        CGMMeasureUID =goalinnerList.get(0);
                                                                        		for (int l = 0; measureList != null && l < measureList.size(); l++) {
                                                                        			List<String> measureinnerList = measureList.get(l);
                                                                        			z++;
                                                                        %>
                                                                    <div style="overflow: hidden; float: left; width: 100%;">
                                                                        <div
                                                                            style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24%;">
                                                                            <p>
                                                                                <span style="float: left; margin-left: 4px;"><%=measureinnerList.get(1)%></span>
                                                                                <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                                <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'measure','<%=CGMtotMeasureWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>m','answerType1<%=a %>_<%=i %>m','0')" title="Add New">Add</a></span> --%>
                                                                                <span style="float: left; margin-left: 5px;"><a id="editexist<%=z%>" href="javascript:void(0)"  onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=goalinnerList.get(0)%>','goal','CGMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>ge','<%=innerList.get(4) %>','<%=CGMtotGoalWeight %>','<%=j+1 %>.<%=k+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                                <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                                <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=measureinnerList.get(0) %>','M','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                                <%} else {%>
                                                                                <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=measureinnerList.get(0) %>&type=M" onclick="return confirm('Are you sure you want to delete this Measure?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                                <%} %>
                                                                                </span>	<%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>me','answerType1<%=a %>_<%=i %>me','0') --%>
                                                                                <%} %>
                                                                            </p>
                                                                        </div>
                                                                        <div
                                                                            style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 10.2%; text-align: right;">
                                                                            <p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
                                                                        </div>
                                                                        <div style="overflow: hidden; float: left; width: 65.8%;">
                                                                            <%
                                                                                double CGMtotQueWeight = 0;
                                                                                List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
                                                                                int intquecnt = questionList != null ? questionList.size()+1 : 1;
                                                                                newquecnt = (j+1)+"."+(k+1)+"."+(l+1)+"."+intquecnt+"";
                                                                                for (int m = 0; questionList != null && m < questionList.size(); m++) {
                                                                                	List<String> question1List = questionList.get(m);
                                                                                	CGMtotQueWeight += uF.parseToDouble(question1List.get(1));
                                                                                }
                                                                                
                                                                                CGMQueUID =measureinnerList.get(0);
                                                                                			for (int m = 0; questionList != null && m < questionList.size(); m++) {
                                                                                				List<String> question1List = questionList.get(m);
                                                                                				z++;
                                                                                %>
                                                                            <div style="overflow: hidden; float: left; width: 100%;">
                                                                                <div
                                                                                    style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 77%;">
                                                                                    <p>
                                                                                        <span style="float: left; margin-left: 4px;"><%=question1List.get(0)%></span>
                                                                                        <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                                        <%-- <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'quest', '<%=CGMtotQueWeight %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>q','answerType1<%=a %>_<%=i %>q','0')" title="Add New">Add</a></span> --%>
                                                                                        <span style="float: left; margin-left: 10px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=measureinnerList.get(0)%>','measure','CGMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>me','<%=innerList.get(4) %>','<%=CGMtotMeasureWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                                                                                        <%if(fromPage != null && fromPage.equals("SRR")) { %>
                                                                                        <a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=question1List.get(3) %>','Q','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                                        <%} else {%>
                                                                                        <a title="Delete" href="DeleteCGOMAndQuestion.action?fromPage=MRS&id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=question1List.get(3) %>&type=Q" onclick="return confirm('Are you sure you want to delete this Measure?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
                                                                                        <%} %>
                                                                                        </span>	<%-- changeNewQuestionType('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>qe','answerType1<%=a %>_<%=i %>qe','0') --%>
                                                                                        <%} %>
                                                                                    </p>
                                                                                </div>
                                                                                <div
                                                                                    style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 22%; text-align: right;">
                                                                                    <p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
                                                                                </div>
                                                                            </div>
                                                                            <%}%>
                                                                            <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                            <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'quest', '<%=CGMtotQueWeight %>','<%=CGMQueUID %>','<%=newquecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>q','answerType1<%=a %>_<%=i %>q','0')" title="Add New">Add</a></span>
                                                                            <%} %>
                                                                        </div>
                                                                    </div>
                                                                    <%  } %>
                                                                    <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                                    <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'measure','<%=CGMtotMeasureWeight %>','<%=CGMMeasureUID %>','<%=newmeasurecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>m','answerType1<%=a %>_<%=i %>m','0')" title="Add New">Add</a></span>
                                                                    <%} %>
                                                                </div>
                                                            </div>
                                                            <!-- </div> -->
                                                            <%
                                                                }
                                                                %>
                                                            <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                            <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'goal', '<%=CGMtotGoalWeight %>','<%=CGMGoalUID %>','<%=newgoalcnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>g','answerType1<%=a %>_<%=i %>g','0')" title="Add New">Add</a></span>
                                                            <%} %>		
                                                        </div>
                                                    </div>
                                                    <%
                                                        }
                                                        %>
                                                    <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                                    <span><a id="addnew<%=z %>"  href="javascript:void(0);" class="add1" onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'score', '<%=CGMtotScoreWeight %>','<%=CGMScoreUID %>','<%=newscorecnt %>');changeNewQuestionTypeOther('<%=queAnstype%>','answerType<%=a %>_<%=i %>s','answerType1<%=a %>_<%=i %>s','0')" title="Add New">Add</a></span>
                                                    <%} %>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                    <!-- this div is only created for some prblm -->			
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCGMsystemOfS_<%=a %>_<%=i %>" name="frmCGMsystemOfS_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID40" id="UID40" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type40" id="type40" value="" />
                                        <div id="CGMscorenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" style="width: 100%;">
                                                        <tr>
                                                            <th width="15%" style="text-align: right;">
                                                                <span id="CGMScoreCntS" style="float: left;"></span>Level Type
                                                            </th>
                                                            <td>Competency
                                                                <%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGMsavebtndivOfS<%=a %>_<%=i %>','CGMscorenewquediv<%=a %>_<%=i %>')"/></span> --%>
                                                                <span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGMsavebtndivOfS<%=a %>_<%=i %>','CGMscorenewquediv<%=a %>_<%=i %>')" ></i></span>
                                                                
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                            <td><input type="text" name="scoreSectionName" id="scoreSectionName" class="validateRequired" style="width: 450px;"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Description</th>
                                                            <td><input type="text" name="scoreCardDescription" style="width: 450px;"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                            <td>
                                                                <input type="text" name="scoreCardWeightage" id="scoreCardWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'scoreCardWeightage<%=a %>_<%=i %>s','hidescoreCardWeightage<%=a %>_<%=i %>s')"/>
                                                                <input type="hidden" name="hidescoreCardWeightage" id="hidescoreCardWeightage<%=a %>_<%=i %>s" value="100"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </li>
                                                <li>
                                                    <ul class="ul_class">
                                                        <li>
                                                            <table class="table" style="width: 100%;">
                                                                <tr>
                                                                    <th width="15%" style="text-align: right;">
                                                                        <span id="CGMGoalCntS" style="float: left;"></span>Level Type
                                                                    </th>
                                                                    <td>Goals
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                    <td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Description</th>
                                                                    <td><input type="text" name="goalDescription" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                    <td>
                                                                        <input type="text" name="goalWeightage" id="goalWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'goalWeightage<%=a %>_<%=i %>s','hidegoalWeightage<%=a %>_<%=i %>s')"/>
                                                                        <input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=a %>_<%=i %>s" value="100"/>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </li>
                                                        <li>
                                                            <ul class="ul_class">
                                                                <li>
                                                                    <table class="table" style="width: 100%;">
                                                                        <tr>
                                                                            <th width="15%" style="text-align: right;">
                                                                                <span id="CGMMeasureCntS" style="float: left;"></span>Level Type
                                                                            </th>
                                                                            <td>Measures <input type="hidden" name="measureID"/>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                            <td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th style="text-align: right;">Description</th>
                                                                            <td><input type="text" name="measuresDescription" style="width: 450px;"/>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                            <td>
                                                                                <input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>s','hidemeasureWeightage<%=a %>_<%=i %>s')"/>
                                                                                <input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>s" value="100"/>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </li>
                                                                <li>
                                                                    <ul>
                                                                        <li>
                                                                            <table class="table" width="100%">
                                                                                <tr>
                                                                                    <th><span id="CGMQueCntS" style="float: left;"></span></th>
                                                                                    <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                                        <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
                                                                                        <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/>
                                                                                    </th>
                                                                                    <td colspan="3">
                                                                                        <span id="newquespan<%=a %>_<%=i %>s" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>s" value="0"/>
                                                                                        <textarea rows="2" name="question" id="question<%=a %>_<%=i %>s" class="validateRequired" style="width: 330px;"></textarea>
                                                                                        <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>s" style="width: 330px;"/> --%>
                                                                                        </span>
                                                                                        <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                                        <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>s','hideweightage<%=a %>_<%=i %>s')"/>
                                                                                        <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>s" value="100"/>
                                                                                        </span>
                                                                                        <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>s');" > +Q </a></span>
                                                                                        <span id="checkboxspan<%=a %>_<%=i %>s" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>s" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>s')"/>
                                                                                        <input type="hidden" id="status<%=a %>_<%=i %>s" name="status" value="0"/></span>
                                                                                        <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                                            <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                                        <input type="hidden" name="questiontypename" value="0" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr id="answerType<%=a %>_<%=i %>s" style="display: none">
                                                                                    <th>&nbsp;</th>
                                                                                    <th>&nbsp;</th>
                                                                                    <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                                                    <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr id="answerType1<%=a %>_<%=i %>s" style="display: none">
                                                                                    <th>&nbsp;</th>
                                                                                    <th>&nbsp;</th>
                                                                                    <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                                                    <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                                                </tr>
                                                                                <tr id="answerType2<%=a %>_<%=i %>s" style="display: none;">
						                                                            <th>&nbsp;</th><th>&nbsp;</th>
						                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
						                                                        </tr>
                                                                            </table>
                                                                        </li>
                                                                    </ul>
                                                                </li>
                                                            </ul>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CGMsavebtndivOfS<%=a %>_<%=i %>" style="display: none" align="center">
                                           
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                          
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGMsavebtndivOfS<%=a %>_<%=i %>','CGMscorenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCGMsystemOfG_<%=a %>_<%=i %>" name="frmCGMsystemOfG_<%=a %>_<%=i %>"  method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID41" id="UID41" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type41" id="type41" value="" />
                                        <div id="CGMgoalnewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" style="width: 100%;">
                                                        <tr>
                                                            <th width="15%" style="text-align: right;">
                                                                <span id="CGMGoalCntG" style="float: left;"></span>Level Type
                                                            </th>
                                                            <td>Goals
                                                                <%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGMsavebtndivOfG<%=a %>_<%=i %>','CGMgoalnewquediv<%=a %>_<%=i %>')"/></span> --%>
                                                                <span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGMsavebtndivOfG<%=a %>_<%=i %>','CGMgoalnewquediv<%=a %>_<%=i %>')" ></i></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                            <td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Description</th>
                                                            <td><input type="text" name="goalDescription" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                            <td>
                                                                <input type="text" name="goalWeightage" id="goalWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'goalWeightage<%=a %>_<%=i %>g','hidegoalWeightage<%=a %>_<%=i %>g')"/>
                                                                <input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=a %>_<%=i %>g" value="100"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </li>
                                                <li>
                                                    <ul class="ul_class">
                                                        <li>
                                                            <table class="table" style="width: 100%;">
                                                                <tr>
                                                                    <th width="15%" style="text-align: right;">
                                                                        <span id="CGMMeasureCntG" style="float: left;"></span>Level Type
                                                                    </th>
                                                                    <td>Measures <input type="hidden" name="measureID"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                                    <td><input type="text" name="measuresSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Description</th>
                                                                    <td><input type="text" name="measuresDescription" style="width: 450px;"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                                    <td>
                                                                        <input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>g','hidemeasureWeightage<%=a %>_<%=i %>g')"/>
                                                                        <input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>g" value="100"/>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </li>
                                                        <li>
                                                            <ul>
                                                                <li>
                                                                    <table class="table" width="100%">
                                                                        <tr>
                                                                            <th><span id="CGMQueCntG" style="float: left;"></span></th>
                                                                            <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                                <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>g" value="<%=queAnstype%>"/>
                                                                                <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>g" value="<%=sectionattribute %>"/>
                                                                            </th>
                                                                            <td colspan="3">
                                                                                <span id="newquespan<%=a %>_<%=i %>g" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>g" value="0"/>
                                                                                <textarea rows="2" name="question" id="question<%=a %>_<%=i %>g" class="validateRequired" style="width: 330px;"></textarea>
                                                                                <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>g" style="width: 330px;"/> --%>
                                                                                </span>
                                                                                <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                                <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>g','hideweightage<%=a %>_<%=i %>g')"/>
                                                                                <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>g" value="100"/>
                                                                                </span>
                                                                                <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>g');" > +Q </a></span>
                                                                                <span id="checkboxspan<%=a %>_<%=i %>g" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>g" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>g')"/>
                                                                                <input type="hidden" id="status<%=a %>_<%=i %>g" name="status" value="0"/></span>
                                                                                <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                                    <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                                <input type="hidden" name="questiontypename" value="0" />
                                                                            </td>
                                                                        </tr>
                                                                        <tr id="answerType<%=a %>_<%=i %>g" style="display: none">
                                                                            <th>&nbsp;</th>
                                                                            <th>&nbsp;</th>
                                                                            <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                                            <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                                            </td>
                                                                        </tr>
                                                                        <tr id="answerType1<%=a %>_<%=i %>g" style="display: none">
                                                                            <th>&nbsp;</th>
                                                                            <th>&nbsp;</th>
                                                                            <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                                            <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                                        </tr>
                                                                        <tr id="answerType2<%=a %>_<%=i %>g" style="display: none;">
				                                                            <th>&nbsp;</th><th>&nbsp;</th>
				                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
				                                                        </tr>
                                                                    </table>
                                                                </li>
                                                            </ul>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CGMsavebtndivOfG<%=a %>_<%=i %>" style="display: none" align="center">
                                          
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                          
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGMsavebtndivOfG<%=a %>_<%=i %>','CGMgoalnewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCGMsystemOfM_<%=a %>_<%=i %>" name="frmCGMsystemOfM_<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID42" id="UID42" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type42" id="type42" value="" />
                                        <div id="CGMmeasurenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" style="width: 100%;">
                                                        <tr>
                                                            <th width="15%" style="text-align: right;">
                                                                <span id="CGMMeasureCntM" style="float: left;"></span>Level Type
                                                            </th>
                                                            <td>Measures <input type="hidden" name="measureID"/>
                                                                <%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGMsavebtndivOfM<%=a %>_<%=i %>','CGMmeasurenewquediv<%=a %>_<%=i %>')"/></span> --%>
                                                                <span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGMsavebtndivOfM<%=a %>_<%=i %>','CGMmeasurenewquediv<%=a %>_<%=i %>')" ></i></span>
                                                                
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Section Name<sup>*</sup></th>
                                                            <td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Description</th>
                                                            <td><input type="text" name="measuresDescription" style="width: 450px;"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th style="text-align: right;">Weightage %<sup>*</sup></th>
                                                            <td>
                                                                <input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>m','hidemeasureWeightage<%=a %>_<%=i %>m')"/>
                                                                <input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>m" value="100"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </li>
                                                <li>
                                                    <ul>
                                                        <li>
                                                            <table class="table" width="100%">
                                                                <tr>
                                                                    <th><span id="CGMQueCntM" style="float: left;"></span></th>
                                                                    <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                        <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>m" value="<%=queAnstype%>"/>
                                                                        <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>m" value="<%=sectionattribute %>"/>
                                                                    </th>
                                                                    <td colspan="3">
                                                                        <span id="newquespan<%=a %>_<%=i %>m" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>m" value="0"/>
                                                                        <textarea rows="2" name="question" id="question<%=a %>_<%=i %>m" class="validateRequired" style="width: 330px;"></textarea>
                                                                        <%-- <input type="text" name="question" id="question<%=a %>_<%=i %>m" style="width: 330px;"/> --%>
                                                                        </span>
                                                                        <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                        <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>m" class="validateRequired" value="100"  onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>m','hideweightage<%=a %>_<%=i %>m')"/>
                                                                        <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>m" value="100"/>
                                                                        </span>
                                                                        <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>m');" > +Q </a></span>
                                                                        <span id="checkboxspan<%=a %>_<%=i %>m" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>m" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>m')"/>
                                                                        <input type="hidden" id="status<%=a %>_<%=i %>m" name="status" value="0"/></span>
                                                                        <%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
                                                                            <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
                                                                        <input type="hidden" name="questiontypename" value="0" />
                                                                    </td>
                                                                </tr>
                                                                <tr id="answerType<%=a %>_<%=i %>m" style="display: none">
                                                                    <th>&nbsp;</th>
                                                                    <th>&nbsp;</th>
                                                                    <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                                    <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                                    </td>
                                                                </tr>
                                                                <tr id="answerType1<%=a %>_<%=i %>m" style="display: none">
                                                                    <th>&nbsp;</th>
                                                                    <th>&nbsp;</th>
                                                                    <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                                    <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                                </tr>
                                                                <tr id="answerType2<%=a %>_<%=i %>m" style="display: none;">
		                                                            <th>&nbsp;</th><th>&nbsp;</th>
		                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
		                                                        </tr>
                                                            </table>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CGMsavebtndivOfM<%=a %>_<%=i %>" style="display: none" align="center">
                                           
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                           
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGMsavebtndivOfM<%=a %>_<%=i %>','CGMmeasurenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <form class="frmClass" action="AddMyReviewCompentencyQuestions.action" id="frmCGMsystemOfQ_<%=a %>_<%=i %>" name="frmCGMsystemOfQ<%=a %>_<%=i %>" method="POST" theme="simple">
                                        <input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
                                        <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
                                        <input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
                                        <input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
                                        <input type="hidden" name="UID43" id="UID43" value="" />
                                        <input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
                                        <input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
                                        <input type="hidden" name="type43" id="type43" value="" />
                                        <div id="CGMquenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                            <ul class="ul_class">
                                                <li>
                                                    <table class="table" width="100%">
                                                        <tr>
                                                            <th><span id="CGMQueCntQ" style="float: left;"></span></th>
                                                            <th width="17%" style="text-align: right;">Add Question<sup>*</sup>
                                                                <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>q" value="<%=queAnstype%>"/>
                                                                <input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>q" value="<%=sectionattribute %>"/>
                                                            </th>
                                                            <td colspan="3">
                                                                <span id="newquespan<%=a %>_<%=i %>q" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>q" value="0"/>
                                                                <textarea rows="2" name="question" id="question<%=a %>_<%=i %>q" class="validateRequired" style="width: 330px;"></textarea>
                                                                </span>
                                                                <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
                                                                <input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>q" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>q','hideweightage<%=a %>_<%=i %>q')"/>
                                                                <input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>q" value="100" />
                                                                </span>
                                                                <span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>q');" > +Q </a></span>
                                                                <span id="checkboxspan<%=a %>_<%=i %>q" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>q" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>q')"/>
                                                                <input type="hidden" id="status<%=a %>_<%=i %>q" name="status" value="0"/></span>
                                                                
                                                                <input type="hidden" name="questiontypename" value="0" />
                                                            </td>
                                                        </tr>
                                                        <tr id="answerType<%=a %>_<%=i %>q" style="display: none">
                                                            <th>&nbsp;</th>
                                                            <th>&nbsp;</th>
                                                            <td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
                                                            <td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
                                                            </td>
                                                        </tr>
                                                        <tr id="answerType1<%=a %>_<%=i %>q" style="display: none">
                                                            <th>&nbsp;</th>
                                                            <th>&nbsp;</th>
                                                            <td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
                                                            <td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
                                                        </tr>
                                                        <tr id="answerType2<%=a %>_<%=i %>q" style="display: none;">
                                                            <th>&nbsp;</th><th>&nbsp;</th>
                                                            <td>&nbsp;</td><td colspan="2">&nbsp;</td>
                                                        </tr>
                                                    </table>
                                                </li>
                                            </ul>
                                        </div>
                                        <div id="CGMsavebtndivOfQ<%=a %>_<%=i %>" style="display: none" align="center">
                                            
                                            <input type="submit" value="Save" class="btn btn-primary" name="submit"/>
                                      
                                            <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGMsavebtndivOfQ<%=a %>_<%=i %>','CGMquenewquediv<%=a %>_<%=i %>')"/>
                                        </div>
                                    </form>
                                    <div id="CGMeditquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
                                    </div>
                                    <%
                                        }
                                        		}else if (uF.parseToInt(innerList1.get(3)) == 4){
                                        			
                                        %>
                                    <ul id="" class="ul_class1">
                                        <li>
                                            <div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
                                                <div style="text-align:left; height: 35px;">
                                                    <%-- <blockquote><strong><%=a+1%>.<%=i+1%>)&nbsp;</strong>KRA --%>
                                                    <%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) {%>
                                                    &nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
                                                    <span style="float: right; margin-right: 333px;"><strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"")%>%</span>
                                                    <div style="width: 70%">
                                                        <ul id="" class="ul_subsection">
                                                            <li><%=innerList1.get(4)%><br/>
                                                                <%=innerList1.get(5)%>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <%} %>
                                                </div>
                                                <div
                                                    style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
                                                    <table class="table" style="width: 100%; float: left;">
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
                                                            	//System.out.println("outerList===>"+outerList);
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
                                        </li>
                                    </ul>
                                    <%
                                        }else if (uF.parseToInt(innerList1.get(3)) == 3 || uF.parseToInt(innerList1.get(3)) == 5){
                                        	
                                        	String systemtype="";
                                        	if(uF.parseToInt(innerList1.get(3)) == 3){
                                        		systemtype="Goal";
                                        	}else{
                                        		systemtype="Target";
                                        	}
                                        	%>
                                    <ul id="" class="ul_class1">
                                        <li>
                                            <div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
                                                <div style="text-align:left; height: 35px;">
                                                    <%-- <blockquote><strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=systemtype %> --%>
                                                    <%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) {%>
                                                    &nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
                                                    <span style="float: right; margin-right: 333px;"><strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"")%>%</span>
                                                    <div style="width: 70%">
                                                        <ul id="" class="ul_subsection">
                                                            <li><%=innerList1.get(4)%><br/>
                                                                <%=innerList1.get(5)%>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <%} %>
                                                </div>
                                                <div style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
                                                    <table class="table" style="width: 100%; float: left;">
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
                                                            	//System.out.println("outerList===>"+outerList);
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
                                        </li>
                                    </ul>
                                    <%
                                        }
                                        }
                                        %>
                                    <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                                    <div id="sectionAssessLinkDiv<%=a %>" style="float:left; margin-top: 15px; margin-bottom: 10px; width: 100%;">
                                        <span id="sectionLinkSpan<%=a %>" style="float: left; margin-left: 100px;"><a href="javascript:void(0)" title="Add Subsections" onclick="openAddNewSystem('<%=appraisalList.get(0) %>','<%=maininnerList.get(0) %>','system','addnewSystemdiv<%=a %>','<%=a+1%>.<%=newsysno %>','<%=subsectionTotWeightage %>','section','sectionAssessLinkDiv<%=a %>','<%=a %>','<%=appFreqId %>','<%=fromPage %>');"> +Subsections </a></span>
                                        <span id="assessLinkSpan<%=a %>" style="float: left; margin-left: 100px;"><a href="javascript:void(0)" title="Add Assessments" onclick="openAddNewSystem('<%=appraisalList.get(0) %>','<%=maininnerList.get(0) %>','system','addnewSystemdiv<%=a %>','<%=a+1%>.<%=newsysno %>','<%=subsectionTotWeightage %>','assessment','sectionAssessLinkDiv<%=a %>','<%=a %>','<%=appFreqId %>','<%=fromPage %>');"> +Assessments </a></span>
                                    </div>
                                    <% } %>
                                    <div id="addnewSystemdiv<%=a %>" style="clear: both; box-shadow: rgba(0, 0, 0, 0.180392) 0px 2px 18px 0px;margin: 5px;padding: 5px;display: none">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <%}
                            %>
                    </div>
                    <%if(appraisalList.get(19).equalsIgnoreCase("FALSE")){ %>
                    <p style="margin: 0px 0px 0px 15px;padding-bottom: 30px;" class="addnew desgn">
                        <a class="add_lvl" href="javascript:void(0)" onclick="openAddNewLevel('<%=appraisalList.get(0) %>','addnewLeveldiv','<%=newlvlno %>','<%=sectionTotWeightage %>','<%=appFreqId %>','<%=fromPage %>')"; title="Add New Section">Add New Section</a>
                    </p>
                    <%} %>
                    <div id="addnewLeveldiv" style="clear: both; box-shadow: rgba(0, 0, 0, 0.180392) 0px 2px 18px 0px;margin: 5px;padding: 5px;display: none">
                    	
                    </div>
                    <!-- /.box-body -->
                </div>
            </section>
        </div>
    </section>
</div>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog modal-dialog1">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1">-</h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
    $(document).ready(function(){
    	$("body").on('click',"input[type = 'submit']",function(){
    	    $("form[name='"+this.form.name+"']").find('.validateRequired').filter(':hidden').prop('required',false);
    		$("form[name='"+this.form.name+"']").find('.validateRequired').filter(':visible').prop('required',true);
        });
    	
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr("style");
    	   $("#modalInfo").hide();
    	   
    	});
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr("style");
    	    $("#modalInfo").hide();
    	});
    	
    	$("body").on('click','#closeButton1',function(){
    		$(".modal-dialog1").removeAttr('style');
    		$("#modal-body1").height(400);
    		$("#modalInfo1").hide();
        });
    });
    
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
    					getMyReviewSummary('MyReviewSummary',appId,appFreqId,fromPage);
	    			},
					error: function(result){
						//alert("result2==>"+result);
						getMyReviewSummary('MyReviewSummary',appId,appFreqId,fromPage);
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
    						getMyReviewSummary('MyReviewSummary',appId,appFreqId,fromPage);
    					},
    					error: function(result){
    						//alert("result2==>"+result);
    						getMyReviewSummary('MyReviewSummary',appId,appFreqId,fromPage);
    					}
    			});
    		  }
    		}
    	   
    	    <%if(fromPage != null && fromPage.equalsIgnoreCase("SRR")){%>
	    	    $("form.frmClass").submit(function(event){
	    	    	event.preventDefault();
	    	    	var form_data = $("form[name = '"+$(this)[0].name+"']").serialize();
	    			$.ajax({ 
	    				type : 'POST',
	    				url: "AddMyReviewCompentencyQuestions.action",
	    				data: form_data+"&submit=Save",
	    			    success: function(result){
	    					//alert("result getQuestion==>"+result);
	    					getMyReviewSummary('MyReviewSummary','<%=id%>','<%=appFreqId%>','<%=fromPage%>');
	    			      },
		    			    error: function(result){
		    					//alert("result getQuestion==>"+result);
		    					getMyReviewSummary('MyReviewSummary','<%=id%>','<%=appFreqId%>','<%=fromPage%>');
		    			    }
	    			 });
	    	    });
    	    <%}%>
    	
    	   
</script>