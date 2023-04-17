
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<script src="scripts/ckeditor_cust/ckeditor.js"></script>
<g:compress>

<style>
.ul_class li {
	margin: 10px 0px 10px 100px;
}
</style>

<script type="text/javascript">
	/* $("body").on('click','#formID1_submit',function(){
		$(".validateRequired").prop('required',true);
	}); */
CKEDITOR.config.width='915px';


function setDatepickerDefault(id1,id2){
	
    $( '#'+id1 ).datepicker({format: 'dd/mm/yyyy'});
    $( '#'+id2 ).datepicker({format: 'dd/mm/yyyy'});
}

function checkFrequencyOnload() {
	var value = document.getElementById("frequency").value;
	if (value == '3') {
		//alert("dfgsdf");
		document.getElementById("weekly").style.display = "none";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "block";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
	} else if (value == '2') {
		
		document.getElementById("weekly").style.display = "block";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "none";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
					
	}else if (value == '6') {
		document.getElementById("weekly").style.display = "none";
		document.getElementById("annualy").style.display = "block";
		document.getElementById("monthly").style.display = "none";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
		
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
	} else {
		document.getElementById("weekly").style.display = "none";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "none";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
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
	//alert("strOrg == "+strOrg);
	var action = 'getEmployeeList.action?strOrg=' + strOrg;
	
	getContent('myEmployee', action);
	setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
}

function getEmployeebyLocation() {
	var location = getSelectedValue("wlocation");
	var strOrg = getSelectedValue("strOrg");
	//alert("strOrg == "+strOrg +" location == "+location);
	var action = 'getEmployeeList.action?strOrg='+ strOrg+'&location='+ location;
	getContent('myEmployee', action);
	setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
	setTimeout(function(){ $("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); }, 500);
}


function getEmployeebyDepart() {
	var location = getSelectedValue("wlocation");
	var depart = getSelectedValue("depart");
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


var dialogEdit = '#showChoosePopupDiv';
function showChoosePopupEdit(hideID,lblID, type, appID) {
	var hideIdValue = document.getElementById(hideID).value;
	var dialogEdit = '#modal-body1';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$(".modal-title1").html('Choose '+ type);
	$("#modalInfo1").show();
	$(".modal-dialog1").removeAttr("style");
	$.ajax({
		url : "ShowOrientationWiseEmpChoosePopup.action?hideID=" + hideID + "&lblID="+lblID + "&appID="+appID + "&type="+type+"&hideIdValue="+hideIdValue,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
 
function showOrientData(value){
	if(value=="1"){
		//hrDiv managerDiv peerDiv
		document.getElementById("hrDivEdit").style.display = "block";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "none";
		//document.getElementById("managerIdDiv").style.display = "none";
		document.getElementById("peerDivEdit").style.display = "none";
		//document.getElementById("peerIdDiv").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "none";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	} else if(value=="2"){
		document.getElementById("hrDivEdit").style.display = "block";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		//document.getElementById("managerIdDiv").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "none";
		//document.getElementById("peerIdDiv").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "none";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	} else if(value=="3"){
		document.getElementById("hrDivEdit").style.display = "block";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		//document.getElementById("managerIdDiv").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "block";
		//document.getElementById("peerIdDiv").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "none";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	} else if(value=="4"){
		document.getElementById("hrDivEdit").style.display = "block";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		//document.getElementById("managerIdDiv").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "block";
		//document.getElementById("peerIdDiv").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "none";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	} else if(value=="5"){
		document.getElementById("hrDivEdit").style.display = "none";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "none";
		//document.getElementById("managerIdDiv").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "none";
		//document.getElementById("peerIdDiv").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "block";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	}
	
}

function showOrientDataonload(){
	var value = document.getElementById("hideorientvaledit").value;
	if(value=="1"){
		document.getElementById("hrDivEdit").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "none";
		document.getElementById("peerDivEdit").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "none";
	} else if(value=="2"){
		document.getElementById("hrDivEdit").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "none";
	} else if(value=="3"){
		document.getElementById("hrDivEdit").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "none";
	} else if(value=="4"){
		document.getElementById("hrDivEdit").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "none";
	} else if(value=="5"){
		document.getElementById("hrDivEdit").style.display = "none";
		document.getElementById("managerDivEdit").style.display = "none";
		document.getElementById("peerDivEdit").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "block";
	}
	
}

function showMembersSelectEdit(orientation) {
	var appId  = document.getElementById("appraisalId").value;
    xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
    } else {
            var xhr = $.ajax({
            url : "GetMembersByOrientation.action?orientation_id="+orientation+"&operation=E&appraisalId="+appId,
            cache : false,
            success : function(data) {
            		document.getElementById("td_choose_members_edit").innerHTML = data;
           		 }
            });
     }
 }
</script>

</g:compress>
<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();
String appFreqId = (String) request.getParameter("appFreqId");
String fromPage = (String) request.getParameter("fromPage");
String id = (String) request.getParameter("id");
List<String> appraisalList = (List<String>) request.getAttribute("appraList");
Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
Map<String, String> questMp = (Map<String, String>) request.getAttribute("questMp");
Map<String, Map<String,String>> memberMp =(Map<String, Map<String,String>>) request.getAttribute("memberMp");

Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
List<String> memberList = (List<String>) request.getAttribute("memberList");
Map<String, String> hmOrientMemberID = (Map<String, String>) request.getAttribute("hmOrientMemberID");
if(hmOrientMemberID == null) {
	hmOrientMemberID = new HashMap<String,String>();
}

if(appraisalList == null) appraisalList = new ArrayList<String>();

if(memberList == null) memberList = new ArrayList<String>();

if(questMp == null) {
	questMp = new HashMap<String,String>();
}

if(memberMp == null) {
	memberMp = new HashMap<String, Map<String,String>>();
}
if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
String policy_id = (String) request.getAttribute("policy_id");
%>

<div class="reportWidth" style="font-size: 12px;">
	<s:form action="EditMyReview" id="formID1" method="POST" theme="simple">
		<%	String appsystem = request.getParameter("appsystem");
			System.out.println("appsystem ===>> " + appsystem);
		  if (appsystem != null && (appsystem.trim().equals("appraisal") || appsystem.trim().equals("editexistapp"))) {
			List<String> appraisalData = (List<String>) request.getAttribute("appraisalData");
			if(appraisalData == null ) appraisalData = new ArrayList<String>();
			System.out.println("appraisalData ----->> " + appraisalData);
			if(appraisalData != null && appraisalData.size() > 0 && !appraisalData.isEmpty()) {
			%>
				<s:hidden name="id"></s:hidden>
				<s:hidden name="appsystem"></s:hidden>
				<s:hidden name="appFreqId"></s:hidden>
				<s:hidden name="fromPage"></s:hidden>
				<input type="hidden" name="appraisalId" id="appraisalId" value="<%=appraisalData.get(0) %>"> 
				<input type="hidden" name="hideorientval" id="hideorientvaledit" value="<%=request.getAttribute("orientedValue") %>"> 
				<table class="table" width="100%">
	                 <tr>
	                     <th width="15%" style="text-align:right" >Review Name:<sup>*</sup></th>
	                     <td colspan="6">
	                     	<input type="hidden" name="policy_id" id="policy_id" value="<%=(String)request.getAttribute("policy_id") %>"/>
	                     	<input type="text" name="appraiselName" id="appraiselName" class="validateRequired" value="<%=appraisalData.get(1) %>" style="width: 50%;"/>
	                     </td>
	                 </tr>
	                 <tr>
	                     <th style="text-align:right">Review Type:<sup>*</sup></th>
	                     <td colspan="6">
	                          <s:select theme="simple" name="appraisalType" headerKey="Self Review" cssClass="validateRequired"
	                              headerValue="Self Review" list="#{}" value="appraisal_typeValue"/>
	                     </td>
	                 </tr>
	                  <tr>
	                     <th style="text-align: right;" valign="top">Description:</th>
	                     <td colspan="6">
	                         <textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="appraisal_description"><%=appraisalData.get(25) %></textarea>  <!-- id="editor22" -->
	                     </td>
	                 </tr>
	                 <tr>
	                     <th style="text-align:right" valign="top">Instructions:</th>
	                     <td colspan="6">
	                         <textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="appraisal_instruction"><%=appraisalData.get(27) %></textarea>  <!-- id="editor11" -->
	                         <s:hidden name="frequency" ></s:hidden>                               
	                     </td>
	                 </tr>
	                 
	             <%-- <tr>
	                 <th style="text-align:right" valign="top">Select Frequency:</th>
	                 <td colspan="6">
	                 <div style="position:reletive;">
	                <span style="float: left; margin-right: 20px"> <s:select theme="simple" name="frequency" id="frequency" list="frequencyList"
	                             listKey="id" listValue="name" onchange="checkFrequency(this.value)" value="frequencyValue"/> </span>                           
	                 
	                 <span id="weekly" style="display: none; float: left;">Day:
	                         <s:select theme="simple" name="weekday" id="weekday" cssStyle="width:100px;" headerKey="" headerValue="Select Day" cssStyle="width:120px;" 
	                         value="weekdayValue" cssClass="validate[required]"
	                             list="#{'Monday':'Monday','Tuesday':'Tuesday', 'Wednesday':'Wednesday','Thursday':'Thursday','Friday':'Friday','Saturday':'Saturday','Sunday':'Sunday'}" />
	                 </span> 
	                 <span id="annualy" style="display: none; float: left;"> 
	                     <s:select theme="simple" name="annualDay" id="annualDay" cssStyle="width:55px;" headerKey="" 
	                     headerValue="Day" value="annualDayValue" cssClass="validate[required]"
	                             list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
	                             '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
	                             '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
	                     &nbsp;
	                         <s:select theme="simple" name="annualMonth" id="annualMonth" cssStyle="width:120px;" headerKey="" 
	                         headerValue="Select Month" value="annualMonthValue" cssClass="validate[required]"
	                             list="#{'January':'January','February':'February', 'March':'March','April':'April','May':'May',
	                             'June':'June','July':'July','August':'August','September':'September', 'October':'October','November':'November','December':'December'}" />
	                             
	                 </span> 
	                 <span id="monthly" style="display: none; float: left;">Date of Month: 
	                     <s:select theme="simple" name="day" id="day" headerKey="" headerValue="Date" value="dayValue" 
	                     cssClass="validate[required]" cssStyle="width:70px;"
	                             list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
	                             '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
	                             '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
	                     
	                 </span> 
	                 <span id="dayMonth" style="display: none; float: left; height: 70px;">
	                     <s:select theme="simple" name="monthday" id="monthday" cssStyle="width:55px; position:absolute;" headerKey="" 
	                     headerValue="Day" value="monthdayValue" cssClass="validate[required]"
	                             list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
	                             '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
	                             '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
	                     &nbsp;
	                         <s:select theme="simple" name="month" id="month" cssStyle="width:135px; position:absolute; margin-left:65px" headerKey=""
	                             headerValue="Select First Month" multiple="true"size="4" value="monthValue" cssClass="validate[required]"
	                             list="#{'January':'January','February':'February', 'March':'March','April':'April','May':'May',
	                             'June':'June','July':'July','August':'August','September':'September', 'October':'October','November':'November','December':'December'}" />
	                 </span>
	                 <span id="quarterly" style="display: none; float: left; margin-left: 210px;"><i>
	                 	Eg. 'Jan' as first month, Qrt1: Jan, Feb, Mar <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                 	'Feb' as first month, Qrt1: Feb, Mar, Apr</i>
	                 </span>
	                 <span id="halfYearly" style="display: none; float: left; margin-left: 210px;"><i>
	                 	Eg. 'Jan' as first month, Half1: Jan, Feb, Mar, Apr, May, Jun <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                 	'Feb' as first month, Half1: Feb, Mar, Apr, May, Jun, Jul</i>
	                 </span>
	                 </div>
	                 </td>
	             </tr> --%>
	                        
	             <tr>
	                 <th style="text-align:right">Start Date:<sup>*</sup></th>
	                 <td><input type="text" name="from" id="fromEdit" class="validateRequired" style="width: 70px;" value="<%=appraisalData.get(17) %>"/>
	                 </td>
	                 <th style="text-align:right">End Date:<sup>*</sup></th>
	                 <td colspan="4"><input type="text" name="to" id="toEdit" class="validateRequired" style="width: 70px;"  value="<%=appraisalData.get(18) %>"/></td>
	
	             </tr>
	             <%-- <tr>
	                <th style="text-align:right" valign="top">Group</th>
	                <td style="text-align: center;">                                   
	                    
	                         <s:select theme="simple" name="strOrg" list="organisationList" id="strOrg" listKey="orgId" listValue="orgName" headerKey=""
	                             headerValue="All Organisations" required="true" onchange="getEmployeebyOrg();" multiple="true"
	                             size="4" cssStyle="width:150px;"></s:select>
	                    
	                 </td>
	                 <td style="text-align: center;">
	                              <s:select name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName" headerKey=""
	                             headerValue="All WorkLocation" required="true" value="wlocationvalue" onchange="getEmployeebyLocation();" 
	                             multiple="true" size="4" cssStyle="width:150px;"></s:select>
	                         <!-- multiple="true" size="4" onchange="getContent('myEmployee','getEmployeeList.action?location='+this.options[this.selectedIndex].value);" -->
	                    
	                 </td>
	
	                 <td style="text-align: center;">
	                         <s:select name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
	                             headerValue="All Department" required="true" onchange="getEmployeebyDepart();" multiple="true"
	                             size="4" value="departmentvalue" cssStyle="width:150px;"></s:select>
	                
	                 </td>
	
	                 <td style="text-align: center;">
	                         <s:select name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName" headerKey=""
	                             headerValue="All Level" required="true" onchange="getEmployeebyLevel()" multiple="true"
	                             size="4" value="levelvalue" cssStyle="width:150px;"></s:select>
	                 </td>
	
	                 <td style="text-align: center;">
	                     <div id="myDesig">
	                             <s:select name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
	                                 headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();" multiple="true"
	                                 size="4" value="desigvalue" cssStyle="width:150px;"></s:select>
	                             <!-- onchange="getContent('myGrade','getGrade.action?DId='+this.options[this.selectedIndex].value)" -->
	                     </div>
	                 </td>
	
	                 <td style="text-align: center;">
	                     <div id="myGrade">
	                             <s:select name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode" headerKey="" id="gradeIdV"
	                                 headerValue="All Grade" onchange="getEmployeebyGrade();" multiple="true"
	                                 size="4" value="gradevalue" cssStyle="width:150px;"></s:select>
	                             <!-- onchange=" setEmployee('myEmployee','getEmployeeList.action?grade='+this.options[this.selectedIndex].value);" -->
	                        
	                     </div>
	                 </td>
	             </tr>                       
	              <tr>
	                 <th style="text-align:right" valign="top">Reviewee</th>
	                 <td id="myEmployee" colspan="6">
	                         <s:select name="employee" list="empList" theme="simple" listKey="employeeId" id="employee" 
	                         listValue="employeeCode" headerKey="" headerValue="All Appraisee" required="true"
	                             multiple="true" size="4" value="empvalue"></s:select>
	                 </td>
	
	             </tr>
	             
	             <tr>
	                 <th style="text-align:right" valign="top">Final Reviewer</th>
	                 <td id="myFinalization" colspan="6">
	                         <s:select name="finalizationName" list="finalizationList" theme="simple"
	                             listKey="employeeId" id="finalizationName" listValue="employeeCode"
	                             headerKey="" headerValue="All User" required="true" multiple="true" size="4"></s:select>
	                 </td>
	
	             </tr> --%>
	             
	              <tr>
	                 <th style="text-align:right">Orientation Type:</th>
	                 <td><input type="hidden" name="oldOrientVal" id="oldOrientVal" value="<%=request.getAttribute("orientedValue") %>"/> 
	                        <s:select theme="simple" name="oreinted"  list="orientationList" listKey="id" listValue="name" 
	                         value="orientedValue" onchange="showMembersSelectEdit(this.value);"/><span style="float: right;" id="spanOreint"></span>
	                 </td>
	                  <td colspan="5" >
	                  	<div id="div_Reviewer_edit">Reviewer: 
							<s:select name="reviewerId" cssClass="form-control" list="reviewerList" theme="simple" listKey="employeeId" 
								id="reviewerId" listValue="employeeCode" multiple="true" size="4" value="reviewervalue" />
						<!-- <input type="hidden" name="hideReviewerId" id="hideReviewerId" /> -->
						</div>
								
						<div id="td_choose_members_edit">
	                  	<%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR"))) { %>
	                     	<input type="hidden" name="hidehrIdEdit" id="hidehrIdEdit" value="<%=appraisalData.get(21) %>"/>
	                     	 <div id="hrDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hidehrIdEdit','lblHridEdit','HR','<%=appraisalData.get(0) %>');">Choose HR</a>:&nbsp;<label id="lblHridEdit"><%=uF.showData(appraisalData.get(29), "Not Choosen") %></label></div>
	                     <% } %>
	              		
	              		<%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("Manager") != null && memberList.contains(hmOrientMemberID.get("Manager"))) { %>
	                     	<input type="hidden" name="hidemanagerIdEdit" id="hidemanagerIdEdit" value="<%=appraisalData.get(8) %>"/>
	                     	 <div id="managerDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hidemanagerIdEdit','lblManageridEdit','Manager','<%=appraisalData.get(0) %>');">Choose Manager</a>:&nbsp;<label id="lblManageridEdit"><%=uF.showData(appraisalData.get(28), "Not Choosen") %></label></div>
	                     <% } %>
	                     
	                     <%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("Peer") != null &&  memberList.contains(hmOrientMemberID.get("Peer"))) { %>
	                     	<input type="hidden" name="hidepeerIdEdit" id="hidepeerIdEdit" value="<%=appraisalData.get(9) %>"/>
	                     	<div id="peerDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hidepeerIdEdit','lblPeeridEdit','Peer','<%=appraisalData.get(0) %>');">Choose Peer</a>:&nbsp;<label id="lblPeeridEdit"><%=uF.showData(appraisalData.get(30), "Not Choosen") %></label></div>
	                     <% } %>
	                     
	                     <%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("Anyone") != null &&  memberList.contains(hmOrientMemberID.get("Anyone"))) { %>
	                     	 <input type="hidden" name="hideotherIdEdit" id="hideotherIdEdit" value="<%=appraisalData.get(32) %>"/>
	                     	 <div id="otherDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hideotherIdEdit','lblOtheridEdit','Other','<%=appraisalData.get(0) %>');">Choose Anyone</a>:&nbsp;<label id="lblOtheridEdit"><%=uF.showData(appraisalData.get(31), "Not Choosen") %></label>
	                     		<%-- &nbsp;&nbsp;&nbsp;&nbsp;<label id="lblSelfFillEmpIds"><%=uF.showData(appraisalData.get(32), "") %></label>
	                   			<input type="hidden" name="hideSelfFillEmpIds" id="hideSelfFillEmpIds" value="<%=(String)request.getAttribute("hideSelfFillEmpIds") %>" /> --%>
	                     	 </div>
	                     <% } %>
	                     
	                      <%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("CEO") != null &&  memberList.contains(hmOrientMemberID.get("CEO"))) { %>
	                     	 <input type="hidden" name="hideCeoIdEdit" id="hideCeoIdEdit" value="<%=appraisalData.get(33) %>"/>
	                     	  <div id="ceoDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hideCeoIdEdit','lblCeoIdEdit','CEO','<%=appraisalData.get(0) %>');">Choose CEO</a>:&nbsp;<label id="lblCeoIdEdit"><%=uF.showData(appraisalData.get(35), "Not Choosen") %></label></div>
	                     <% } %>
	                     
	                     <%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("HOD") != null &&  memberList.contains(hmOrientMemberID.get("HOD"))) { %>
	                     	  <input type="hidden" name="hideHodIdEdit" id="hideHodIdEdit" value="<%=appraisalData.get(34) %>"/>
	                     	  <div id="hodDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hideHodIdEdit','lblHodIdEdit','HOD','<%=appraisalData.get(0) %>');">Choose HOD</a>:&nbsp;<label id="lblHodIdEdit"><%=uF.showData(appraisalData.get(36), "Not Choosen") %></label></div>
	                     <% } %>
	                  	</div>																																																						
	                 </td>
	             </tr>
	             <tr>
	             	<th style="text-align: right">Workflow for Publish review:</th>
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
								<th style="text-align:right">&nbsp;</th>
							    <td colspan="6">Your work flow is not defined. Please, speak to your hr for your work flow.</td>
							</tr>
					<%  } %>
				<%	} %>
			    </table>
			    <div>
				    <%if(fromPage != null && fromPage.equalsIgnoreCase("SRR")) { %>
				    	<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="updateMyReview('<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
				    <% } else { %>
				    		<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
				    <% } %>
			    </div>
		  <%}%>
	<% } %>

	</s:form>
</div>

<g:compress>

	<script>
			// Replace the <textarea id="editor1"> with an CKEditor instance.
		CKEDITOR.replace( 'editor11', {
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
		
		CKEDITOR.replace( 'editor22', {
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

$(function() {
	$("#fromEdit").datepicker({autoclose: true,format : 'dd/mm/yyyy'});
	$("#toEdit").datepicker({autoclose: true,format : 'dd/mm/yyyy'}); 

	$("input[type='submit']").click(function(){
		//$(".formIDvalidateRequired").prop('required',true);
		$("#formID1").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formID1").find('.validateRequired').filter(':visible').prop('required',true);
	});
});

function updateMyReview(appId,appFreqId,fromPage){
//	alert("updateMyReview jsp ==appId==>"+appId+"==>appFreqId==>"+appFreqId);
	var form_data = $("#formID1").serialize();
	$.ajax({ 
		type : 'POST',
	//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
		url: "EditMyReview.action",
		data: form_data+"&submit=Save",
		cache: true,
		success: function(result){
			getMyReviewSummary('MyReviewSummary', appId, appFreqId, fromPage);
   		},
		error: function(result){
			getMyReviewSummary('MyReviewSummary', appId, appFreqId, fromPage);
		}
	});
}
</script>
</g:compress>