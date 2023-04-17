<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	String anstype = (String) request.getAttribute("anstype");
	
	String formId = (String) request.getAttribute("formId");
	Map<String, String> hmSecQuestion = (Map<String, String>)request.getAttribute("hmSecQuestion");
	if(hmSecQuestion == null) hmSecQuestion = new HashMap<String, String>();
%>
<style>
input[type='text'] {
width: auto !important;
}
</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(document).ready( function () {
	$("#frmEditQuestion_submit").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateNumber").prop('type','number');	 		
	});
});

function showAnswerTypeDiv(ansType) {
	document.getElementById("otherQuestionMainDiv").innerHTML = '';
	
	var action = 'ShowAnswerType.action?ansType=' + ansType;
	getContent("anstypediv", action);
}

/* var dialogEdit = '#SelectQueDiv';
function openQuestionBank(count) {
	
	removeLoadingDiv('the_div');
	var ansType=document.getElementById('ansType').value;
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog(
				{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 200,
				width : 400,
				modal : true,
				title : 'Question Bank',
				open : function() {
					var xhr = $.ajax({
						url : "SelectQuestion.action?count="+count+"&ansType="+ansType,
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
					xhr = null;
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});
	$(dialogEdit).dialog('open');
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
} */

/* function validateScore1(value1, weightageid, remweightageid) {
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
} */

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

</script>

<div>

	<s:form name="frmEditQuestion" id="frmEditQuestion" theme="simple" action="EditQuestion" method="POST">
		<s:hidden name="formId" id="formId"></s:hidden>
		<s:hidden name="sectionId" id="sectionId"></s:hidden>
		<s:hidden name="sectionQuestId" id="sectionQuestId"></s:hidden>
		<s:hidden name="questBankId" id="questBankId"></s:hidden>
		<s:hidden name="operation" value="U"/>
		<s:hidden name="strOrg" id="strOrg"></s:hidden>
       	<s:hidden name="userscreen" id="userscreen"></s:hidden>
		<s:hidden name="navigationId" id="navigationId"></s:hidden>
		<s:hidden name="toPage" id="toPage"></s:hidden>
		<div>
			<div id="otherDiv">
				<div id="otherQuestionMainDiv">
					<%
						List<String> queList = (List<String>)request.getAttribute("queList");
						if(queList != null && !queList.isEmpty()){
					%>
						<table class="table table_no_border">
							<tr>
								<th>&nbsp;</th>
								<th width="17%" style="text-align: right;">Add Question<sup>*</sup></th>
								<td colspan="3">
									<span id="newquespan1" style="float: left;">
										<input type="hidden" name="hidequeid" id="hidequeid1" value="<%=queList.get(0) %>"/>
										<textarea rows="2" name="question" id="question1" style="width: 330px;" class="validateRequired"><%=queList.get(1) %></textarea>
									</span>&nbsp;
									<span style="float: left; margin-left: 10px;">
										<input type="hidden" name="orientt" value="1"/><sup>*</sup>
										<input type="text" style="width: 35px !important;" name="weightage" id="weightage1" class="validateNumber" value="<%=uF.showData(hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE"),"0") %>" onkeyup="validateScoreEdit(this.value,'weightage1','hideweightage1','<%=request.getAttribute("totalQuestWeightage") %>');" onkeypress="return isNumberKey(event)"/>
										<input type="hidden" name="hideweightage" id="hideweightage1" value="<%=uF.showData(hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE"),"0") %>"/>
									</span>&nbsp;&nbsp;
									<%-- <span style="float:left; margin-left: 10px;">
										<a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('1');" > +Q </a>
									</span>&nbsp; --%>
									<span id=checkboxspan1" style="float: left; margin-left: 10px;">
										<input name="addFlag" type="checkbox" id="addFlag1" title="Add to Question Bank" onclick="changeStatus('1')"/>
										<input type="hidden" id="status1" name="status" value="0"/>
									</span>
									<input type="hidden" name="questiontypename" value=""/>
								</td>
							</tr>
							<%if(uF.parseToInt(queList.get(7)) == 1 || uF.parseToInt(queList.get(7)) == 2 || uF.parseToInt(queList.get(7)) == 8){ %>
								<tr id="answerType1">
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<td>a)&nbsp;<input type="text" name="optiona" id="optiona1" value="<%=uF.showData(queList.get(2),"") %>"/>
										<input type="radio" value="a" name="correct1" <%if(queList.get(6).contains("a")){%>checked="checked"<%} %>/> 
									</td>
									<td colspan="2">b)&nbsp;<input type="text" name="optionb" id="optionb1" value="<%=uF.showData(queList.get(3),"") %>"/>
										<input type="radio" name="correct1" value="b" <%if(queList.get(6).contains("b")){%> checked="checked"<%} %>/></td>
								</tr>
								<tr id="answerType11">
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<td>c)&nbsp;<input type="text" name="optionc" id="optionc1" value="<%=uF.showData(queList.get(4),"") %>"/>
										<input type="radio" name="correct1" value="c" <%if(queList.get(6).contains("c")){%>checked="checked"<%} %>/>
									</td>
									<td colspan="2">d)&nbsp;<input type="text" name="optiond" id="optiond1" value="<%=uF.showData(queList.get(5),"") %>"/>
										<input type="radio" name="correct1" value="d" <%if(queList.get(6).contains("d")){%>checked="checked"<%} %>/>
									</td>
								</tr>
							<%}else if(uF.parseToInt(queList.get(7)) == 9 ){ %>	
								<tr id="answerType1">
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<td>a)&nbsp;<input type="text" name="optiona" id="optiona1" value="<%=uF.showData(queList.get(2),"") %>"/>
										<input type="checkbox" value="a" name="correct1" <%if(queList.get(6).contains("a")){%>checked="checked"<%} %>/>
									</td>
									<td colspan="2">b)&nbsp;<input type="text" name="optionb" id="optionb1" value="<%=uF.showData(queList.get(3),"") %>"/>
										<input type="checkbox" name="correct1" value="b" <%if(queList.get(6).contains("b")){%>checked="checked" <%} %>/>
									</td>
								</tr>
								<tr id="answerType11">
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<td>c)&nbsp;<input type="text" name="optionc" id="optionc1" value="<%=uF.showData(queList.get(4),"") %>"/>
										<input type="checkbox" name="correct1" value="c" <%if(queList.get(6).contains("c")){%>checked="checked"<%} %>/>
									</td>
									<td colspan="2">d)&nbsp;<input type="text" name="optiond" id="optiond1" value="<%=uF.showData(queList.get(5),"") %>"/>
										<input type="checkbox" name="correct1" value="d" <%if(queList.get(6).contains("d")){%>checked="checked"<%} %>/>
									</td>
								</tr>
							<%}else if(uF.parseToInt(queList.get(7)) == 6 ){ %>	
								<tr id="answerType1">
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<td><input type="hidden" name="optiona" id="optiona1" value="<%=uF.showData(queList.get(2),"") %>"/>
										<input type="hidden" name="optionb" id="optionb1" value="<%=uF.showData(queList.get(3),"") %>"/>
										<input type="hidden" name="optionc" id="optionc1" value="<%=uF.showData(queList.get(4),"") %>"/>
										<input type="hidden" name="optiond" id="optiond1" value="<%=uF.showData(queList.get(5),"") %>"/>
										<input type="radio" name="correct1" value="1" <%if(queList.get(6).contains("1")){%>checked="checked"<%} %>>True&nbsp;
										<input type="radio" name="correct1" value="0" <%if(queList.get(6).contains("0")){%>checked="checked"<%} %>>False
									</td>
								</tr>
							<%}else if(uF.parseToInt(queList.get(7)) == 5 ){ %>
								<tr id="answerType1">
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<td><input type="hidden" name="optiona" id="optiona1" value="<%=uF.showData(queList.get(2),"") %>"/>
										<input type="hidden" name="optionb" id="optionb1" value="<%=uF.showData(queList.get(3),"") %>"/>
										<input type="hidden" name="optionc" id="optionc1" value="<%=uF.showData(queList.get(4),"") %>"/>
										<input type="hidden" name="optiond" id="optiond1" value="<%=uF.showData(queList.get(5),"") %>"/>
										<input type="radio" name="correct1" value="1" <%if(queList.get(6).contains("1")){%>checked="checked"<%} %>>Yes&nbsp;
										<input type="radio" name="correct1" value="0" <%if(queList.get(6).contains("0")){%>checked="checked"<%} %>>No
									</td>
								</tr>	
							<%} %>
						</table>
					<%} %>
				</div>
			</div>
		</div>
		
		<div id="firstdiv" style="float:left; margin-top:20px;">
			<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
			<!-- <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();"/> -->
		</div>
		<div id="secWeightdiv" style="float:left; margin-top:20px; display: none" >
			<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
			<s:submit value="Save & Add New Section" cssClass="btn btn-primary" name="saveandnew"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
			<!-- <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();"/> -->
		</div>
	</s:form>
</div>


<div id="SelectQueDiv"></div>