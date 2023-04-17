<%@page import="java.util.HashMap"%>
<%@page import="org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%> 
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Requirement Request" name="title" />
    </jsp:include> --%>
<style>
    .form-control{
    margin-top: 5px;
    margin-right: 5px;
    margin-left: 5px;
    margin-bottom: 5px;
    }
</style>
<%
    String formName = (String)request.getAttribute("formName");
    List<String> panelNameList = (List) request.getAttribute("panelNameList");
    System.out.println("formName ===>> " + formName);
    %>
<%if(formName == null || formName.equals("") || formName.equalsIgnoreCase("null")) { %>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<% } %>
<script type="text/javascript" src="scripts/customAjax.js"></script> 
<script type="text/javascript">
   
$(document).ready(function() {
	$('#lt2').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': []
  	});
});


   	/* function deactivatecss() {
   		$("strDesignationUpdate0").removeAttribute("class");
   	} */    
   	
    function getOrgLocationDepartLevelDesigGrade(value) {
    //alert("value ===>> " + value);
    xmlhttp = GetXmlHttpObject();
       if (xmlhttp == null) {
               alert("Browser does not support HTTP Request");
               return;
       } else {
              var xhr = $.ajax({
                  url : "GetOrgwiseLocationDepartLevelDesigGrade.action?strOrgId="+value+"&type=single",
                  cache : false,
                  success : function(data) {
                  	if(data == "") {
                  	} else {
                  		//alert("data --------->> " + data);     
                  		var allData = data.split("::::");
                  	
                         document.getElementById("wlocation").innerHTML = allData[0];
                         //document.getElementById("departDiv").innerHTML = allData[1];
                         document.getElementById("strLevel").innerHTML = allData[2];
                         document.getElementById("desigIdV").innerHTML = allData[3];
                         document.getElementById("gradeIdV").innerHTML = allData[4];
                  	}
				}
			});
       }
       window.setTimeout(function() {  
       	getEmployeebyOrg();
    }, 200); 
       
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
    
    
    	
    function getEmployeebyOrg() {
    
	    var recruitId=document.getElementById('recruitId').value;
	    var organisation = getSelectedValue("organisation");	
	    var action = 'GetCandidateEmployeeList.action?viewSelection=list&organisation='+organisation+'&recruitmentID='+recruitId;
	    //alert("action =====>> " + action);
	    
	    document.getElementById("wlocation").selectedIndex = 0;
	    //alert("wlocation");
	    document.getElementById("strLevel").selectedIndex = 0;
	    document.getElementById("desigIdV").selectedIndex = 0;
	    document.getElementById("gradeIdV").selectedIndex = 0;
	    //alert("gradeIdV");
	     getContent('empidlist', action);
    }
    
    function getEmployeebyLocation() {
    
	    var recruitId=document.getElementById('recruitId').value;
	    var organisation = getSelectedValue("organisation");
	    var location = getSelectedValue("wlocation");		
	    var action = 'GetCandidateEmployeeList.action?viewSelection=list&location='+location+'&recruitmentID='+recruitId;
	    if (organisation!='') {
	    	action+='&organisation='+organisation;
	    }
	    document.getElementById("strLevel").selectedIndex = 0;
	    document.getElementById("desigIdV").selectedIndex = 0;
	    document.getElementById("gradeIdV").selectedIndex = 0;
	    getContent('empidlist', action);
    }
    
    
    function getEmployeebyLevel() {
	    var recruitId=document.getElementById('recruitId').value;
	    var organisation = getSelectedValue("organisation");
	    var location = getSelectedValue("wlocation");
	    var Level = getSelectedValue("strLevel");
	    
	    var action = 'GetCandidateEmployeeList.action?viewSelection=list&level='+Level+'&recruitmentID='+recruitId;
	    
	    if (location!='') {
	    		action+='&location='+location;
	    	}
	    if (organisation!='') {
	    	action+='&organisation='+organisation;
	    }
	          	getContent('empidlist', action);
	    	
	     	window.setTimeout(function() {  
	    		getContent('desigIdV', 'GetDesigfromLevel.action?pagefrom=addpanel&strLevel='+Level);
	    	}, 200); 
	     	
	    	document.getElementById("desigIdV").selectedIndex = 0;
	    	document.getElementById("gradeIdV").selectedIndex = 0;
    }
    
    
    function getEmployeebyDesig() {
    var recruitId=document.getElementById('recruitId').value;
    var organisation = getSelectedValue("organisation");
    var location = getSelectedValue("wlocation");
    var Level = getSelectedValue("strLevel");
    var design = getSelectedValue("desigIdV");
    
    var action = 'GetCandidateEmployeeList.action?viewSelection=list&design='+design+'&recruitmentID='+recruitId;
    
    	if (location!='') {
    		action += '&location=' + location;
    	}	
    	if (Level != '') {
    		action += '&level=' + Level;
    	}
    	if (organisation!='') {
    		action+='&organisation='+organisation;
    	}
    	getContent('empidlist', action);
    	
     	window.setTimeout(function() {  
     	  getContent('gradeIdV', 'GetGradefromDesig.action?pagefrom=addpanel&strDesignation=' + design);
    	}, 200); 
     	
    	document.getElementById("gradeIdV").selectedIndex = 0;
    }
    
    
    
    function getEmployeebyGrade() {
    var recruitId=document.getElementById('recruitId').value;
    var organisation = getSelectedValue("organisation");
    var location = getSelectedValue("wlocation");
    var Level = getSelectedValue("strLevel");
    var design = getSelectedValue("desigIdV");
    var grade = getSelectedValue("gradeIdV");
    
    var action = 'GetCandidateEmployeeList.action?viewSelection=list&grade='+grade+'&recruitmentID='+recruitId;
    
    if (location!='') {
    	action += '&location=' + location;
    }	
    if (Level != '') {
    	action += '&level=' + Level;
    }
    if (design != '') {
    	action += '&design=' + design;
    }
    if (organisation!='') {
    	action+='&organisation='+organisation;
    }
    getContent('empidlist', action);
    }
    
    
    function getLocationOrg(orgId){
    
    }
    
    function getSelectedValue(selectId) {
    var choice = document.getElementById(selectId);
    var exportchoice="";
    for (var i = 0, j = 0; i < choice.options.length; i++) {
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
    
    
    $(document).ready(function() {
	    $('#lt1').dataTable({
	    	bJQueryUI : true,
	    	"sPaginationType" : "full_numbers",
	    	"aaSorting" : []
	    });
	    //alert("asdf----");
    });
    
    	
    function checkUncheckValueInd() {
    if(confirm("Are you sure, you want to add this employee in round?")){
    var allEmp=document.getElementById("allEmpInd");		
    var strTrainerId = document.getElementsByName('strTrainerId');
    var selectID="";
    var status=false;
    if(allEmp.checked==true){
    	status=true;
    	 for(var i=0;i<strTrainerId.length;i++){
    		 //strTrainerId[i].checked = true;
    		  if(i==0){
    			  selectID=strTrainerId[i].value;
    		  }else{
    			  selectID+=","+strTrainerId[i].value;
    		  }
    		  //document.getElementById("strTrainerId"+i).checked = false;
    		  document.getElementById("allEmpInd").checked = false;
    	 }
    	// alert("selectID ===> " + selectID);
    }/* else{		
    	status=false;
    	 for(var i=0;i<strTrainerId.length;i++){
    		 strTrainerId[i].checked = false;
    		  if(i==0){
    			  selectID=strTrainerId[i].value;
    		  }else{
    			  selectID+=","+strTrainerId[i].value;
    		  }
    	 }
    } */
    	var roundId = document.getElementById("selectedRoundId").value;
    	getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?allEmpId='+selectID+'&recruitId=<s:property value="recruitId"/>&roundId='+roundId+'&&mode=addallemp');
    }
    
    }		
    	
    
    function checkSelectEmp(checked,value,i,roundId){
    if(confirm("Are you sure, you want to add this employee in round?")){
    	/* alert("You can not modify this employee list ..."); */
    	//alert("roundId ..."+roundId);
    	roundId = document.getElementById("selectedRoundId").value;
    	//alert("roundId after select ..."+roundId);
    	getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?empId='+value+'&recruitId=<s:property value="recruitId"/>&roundId='+roundId+'&mode=addemp');
    	document.getElementById("strTrainerId"+i).checked = false;
    }else{
    	/* getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?chboxStatus='+checked+'&selectedEmp='+value+'&recruitmentID=<s:property value="recruitID"/>') */	
    }
    }
    
    
    
    function resetEmployee(empId,roundId,recruitId){
    if(confirm("Are you sure, you want to remove this employee from this round?")){
    	/* alert("You can not modify this employee list ..."); */
    	getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?empId='+empId+'&recruitId='+recruitId+'&roundId='+roundId+'&mode=removeemp');
    }else{
    	/* getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?chboxStatus='+checked+'&selectedEmp='+value+'&recruitmentID=<s:property value="recruitID"/>') */	
    }
    }	
    
    
    function changePanelRound(empId,roundId,recruitId,strRound){
    /* var selrndID = document.getElementById("strRound"+recruitID+roundId+empId).value; */
    //alert(empId+" "+roundId+" "+recruitID+" "+strRound+" "+selrndID);
    if(confirm("Are you sure, you want to change round?")){
    	/* alert("You can not modify this employee list ..."); */
    	getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?empId='+empId+'&recruitId='+recruitId+'&roundId='+roundId+'&strRound='+strRound+'&mode=emproundchange');
    }else{
    	/* getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?chboxStatus='+checked+'&selectedEmp='+value+'&recruitmentID=<s:property value="recruitID"/>') */	
    }
    }
    
    
    function sendNotification(formName){
	    var rid = document.getElementById("recruitId").value;
	    if(formName != "" && (formName == "WF" || formName == 'A')) {
	    	
	    	$.ajax({
				type :'POST',
				url  :'SendOpenJobNotification.action?recruitId='+rid+'&fromPage='+formName,
				cache:true/* ,
				success : function(result) {
					$("#subSubDivResult").html(result);
				} */
			});
	    	
	    	$.ajax({
				url: 'Applications.action?recruitId='+rid,
				cache: true,
				success: function(result){
					$("#subSubDivResult").html(result);
		   		}
			});
	    	$("#modalInfo").hide();
	    } else {
	    	$.ajax({
				type :'POST',
				url  :'SendJobReportNotification.action?recruitId='+rid+'&fromPage='+formName,
				cache:true,
				success : function(result) {
					$("#subSubDivResult").html(result);
				},
				error : function(error) {
					$.ajax({
						url: 'ReportJobProfilePopUp.action?recruitId='+rid+'&fromPage='+formName+'&view=jobreport',
						cache: true,
						success: function(result){
							$("#subSubDivResult").html(result);
				   		}
					});
				}
			});
	    	$("#modalInfo").hide();
	    }
    }			
    
    
    function showRounds(cnt){
	    document.getElementById("roundchangeImgDiv"+cnt).style.display = 'none';
	    document.getElementById("roundsDiv"+cnt).style.display = 'block';
    }	
    
    
    function openEmployeeProfilePopup(empId) {
    	var dialogEdit = '#modal-body1';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo1").show();
		$(".modal-title1").html('Employee Information');
		if($(window).width() >= 1100){
			$(".modal-dialog1").width(1100);
		}
    	$.ajax({
    		//url : "ApplyLeavePopUp.action",  
    		url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
     }
    
    
    function addRound(rountCnt,recruitId) {
    var cnt =rountCnt;
    cnt++;
    //alert("cnt ===> "+cnt);
    if(confirm("Are you sure, you want to add new round?")){
    	getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?roundId='+cnt+'&recruitId='+recruitId+'&mode=add');
    }else{	}
    }	
    
    
    function removeRound(removeId,recruitId,removeStatus) {
	    if(removeStatus == "no"){
	    	alert("You can not delete this round.");
	    }else{
	    if(confirm("Are you sure, you want to delete this round?")){
	    	getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?roundId='+removeId+'&recruitId='+recruitId+'&mode=remove');
	    	}
	    }
    }
    
    
	//====start parvez=====
    function setRoundTitle1(cnt){
	    document.getElementById("roundTitleImgDiv"+cnt).style.display = 'none';
	    document.getElementById("roundTitleDiv"+cnt).style.display = 'block';
    }
    //====end parvez=====
    
    
    function setRoundTitle(interviewRoundId, recruitId) {
    	
    	var dialogEdit = '#modal-body1';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo1").show();
		$('.modal-title1').html('Set Round '+interviewRoundId+' Title');
		
		if($(window).width() >= 600){
			$(".modal-dialog1").width(600);
		}
		//alert("interviewRoundId 2 ===>> " + interviewRoundId);
	    $.ajax({
	    	//start parvez on 10-07-2021==== 
	    	url :"SetInterviewRoundTitle.action?interviewRoundId="+interviewRoundId+"&recruitId="+recruitId+'&operation=E',
	    	//url :"SetInterviewRoundTitle.action?interviewRoundId="+interviewRoundId+"&recruitId="+recruitId+"&roundName="+roundName+'&operation=E',
	    	cache : false,
	    	success : function(data) {
	    		//alert("data ===>> " + data);
	    		//$(dialogEdit).html(data); 
	    		if(data != "" && data.trim().length > 0){
                	document.getElementById("roundTitDiv"+interviewRoundId).innerHTML = data;
               	 }
	    	}
	    	//====end parvez on 10-07-2021==== 
	    });
    }
    
    
    function setInterviewRoundTitle() {
    var interviewRoundId = document.getElementById("interviewRoundId").value;
    var recruitId = document.getElementById("recruitId").value;
    var operation = document.getElementById("operation").value;
    var roundName = document.getElementById("roundName").value;
    //alert("interviewRoundId ===> " + interviewRoundId + "recruitID ===> " + recruitID + "roundName ===> " + roundName);
          xmlhttp = GetXmlHttpObject();
          if (xmlhttp == null) {
                  alert("Browser does not support HTTP Request");
                  return;
          } else {
                 var xhr = $.ajax({
                   url : "SetInterviewRoundTitle.action?interviewRoundId=" + interviewRoundId + '&recruitId=' +recruitId + '&roundName=' +roundName+'&operation=A',
                   cache : false,
                   success : function(data) {
                	   if(data != "" && data.trim().length > 0){
                    	document.getElementById("roundTitDiv"+interviewRoundId).innerHTML = data;
                   	   }
                   }
                 });
          }
          $(".modal-dialog1").removeAttr('style');
	  	  $("#modal-body1").height(400);
	      $("#modalInfo1").hide();
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
    
    function setRoundIdForPanelist(roundId) {
    document.getElementById("selectedRoundId").value = roundId;
    
    var totalRound = document.getElementById("totalRound").value;
    
    for(var i=0; i<= parseInt(totalRound); i++) {
    if(roundId == i) {
    	//alert('roundId ===>> ' + roundId);
    	document.getElementById("roundDIV_"+roundId).style.color = '#ff851b';
    	//alert('roundId if ===>> ' + roundId);
    } else {
    	if(document.getElementById("roundDIV_"+i)) {
    		document.getElementById("roundDIV_"+i).style.color = '#535353';
    	}
    }
    }
    }    
    
    function viewAllAssessment(roundId, assessmentId) {
    var recruitId = document.getElementById("recruitId").value;
    //alert("assessmentId ===>> " + assessmentId+' -- assessId ===>> ' + assessId);
    getContent('roundAssessDiv_'+roundId, 'ViewAssessmentsForRound.action?roundId='+roundId+'&recruitId='+recruitId+'&assessmentId='+assessmentId);
    }
    
    function setAssessmentToRound(recruitId, roundId) {
    var assessmentId = document.getElementById('strAssessment_'+roundId).value;
    //alert("assessmentId ===>> " + assessmentId+' -- assessId ===>> ' + assessId);
    getContent('roundAssessDiv_'+roundId, 'ViewAssessmentsForRound.action?roundId='+roundId+'&recruitId='+recruitId+'&assessmentId='+assessmentId+'&operation=add');
    }
    
</script>
<!-- <div id="panelDiv"> -->
<div>
     <input type="hidden" name="formName" id="formName" value="<%=formName%>"/>
    <s:hidden name="jobid" id="hiddenjobid"/>
    <s:hidden name="recruitId" id="recruitId"/>
    <s:hidden name="selectedRoundId" id="selectedRoundId" value="1"/>
    <s:hidden name="insertAddCriteriaPanel" value="insert" />
    
    <s:select theme="simple" name="organisation" id="organisation" listKey="orgId" theme="simple" listValue="orgName" 
        list="organisationList" key="" onchange="getOrgLocationDepartLevelDesigGrade(this.value);" cssStyle="margin: 5px;"/>
        
    <s:select name="strLocation" list="workList" id="wlocation" theme="simple" listKey="wLocationId"  cssStyle="margin: 5px;"
        listValue="wLocationName" headerKey="" headerValue="All WorkLocation" required="true" onchange="getEmployeebyLocation();"></s:select>
        
    <s:select name="strLevel" list="levelList" listKey="levelId"  theme="simple" id="strLevel"  cssStyle="margin: 5px;"
        listValue="levelCodeName" headerKey="" headerValue="All Level" required="true" 
        onchange="getEmployeebyLevel()"></s:select>
        
    <s:select name="strDesignationUpdate" list="desigList"  theme="simple" listKey="desigId"  cssStyle="margin: 5px;"
        id="desigIdV" listValue="desigCodeName" headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();"></s:select>
        
    <s:select name="strGrade" list="gradeList" listKey="gradeId"  theme="simple" listValue="gradeCode"  cssStyle="margin: 5px;"
        headerKey="" id="gradeIdV" headerValue="All Grade" onchange="getEmployeebyGrade();"></s:select>
</div>
<%  List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
    List<FillEmployee> selectEmpIds = (List<FillEmployee>) request.getAttribute("selectEmpIds");
    Map<String, String> sltEmpDtCmprHm = (Map<String, String>) request.getAttribute("sltEmpDtCmprHm");
    Map<String, List<List<String>>> hmEmpIdsRoundwise = (Map<String, List<List<String>>>) request.getAttribute("hmEmpIdsRoundwise");
    Map<String, String> hmRoundIds = (Map<String, String>)request.getAttribute("hmRoundIds");
    //EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 20-July-2021 Note : Encryption
%>
<div id="empidlist" class="col-lg-6 col-md-6 col-sm-12" style="padding: 5px;border: 1px solid rgb(238, 238, 238);height: 250px;overflow-y: auto;">
    <table class="table" id="lt2">  <!--  id="lt2" table-striped -->
	    <thead>
	        <tr>
	            <th width="10%"><input onclick="checkUncheckValueInd();" type="checkbox" name="allEmpInd" id="allEmpInd"></th>
	            <th align="center">Employee</th>
	            <th align="center">Location</th>
	            <!-- <th align="center">Factsheet</th> -->
	        </tr>
        </thead>
        <%	
            Map<String, String> hmRoundName = (Map<String, String>)request.getAttribute("hmRoundName");
            if(hmRoundName == null) hmRoundName = new HashMap<String, String>();
            List<String> listRoundId = (List<String>) request.getAttribute("listRoundId");
            Map<String, String> hmRoundAssessment = (Map<String, String>) request.getAttribute("hmRoundAssessment");
            if(hmRoundAssessment == null) hmRoundAssessment = new HashMap<String, String>();
            String maxRountId = null;
            if(listRoundId != null){
            	maxRountId = listRoundId.get(listRoundId.size()-1);
            }else{
            	maxRountId = "1";
            }
            %>
        <tbody> 
        <%
            UtilityFunctions uF=new UtilityFunctions();
            Map<String,String> hmWlocation=(Map<String,String>)request.getAttribute("hmWlocation");	
            if(hmWlocation == null) hmWlocation = new HashMap<String, String>();
            
            for(int i=0;empList!=null && i<empList.size();i++){
            		 String empID=((FillEmployee)empList.get(i)).getEmployeeId();
                    String empName=((FillEmployee)empList.get(i)).getEmployeeCode();
                     	 %>
	        <tr>
	            <td><input onclick="checkSelectEmp(this.checked,this.value,'<%=i %>','<%=maxRountId %>');"
	                type="checkbox" name="strTrainerId" id="strTrainerId<%=i%>" value="<%=empID %>">
	            </td>
	            <!-- Created By Dattatray Date : 21-July-2021 Note : empId encrypt -->
	            <td nowrap="nowrap"><a href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empID %>');"><%=empName %></a></td>
	            <td nowrap="nowrap"><%=uF.showData(hmWlocation.get(empID),"")%></td>
	            <%-- <td><a class="factsheet" href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empID %>');"></a></td> --%>
	        </tr>
	        <%} if(empList==null){%>
	        <tr>
	            <td colspan="3">
	                <div class="nodata msg" style="width: 85%">
	                    <span>No Employee within selection</span>
	                </div>
	            </td>
	        </tr>
	        <%} %>
		</tbody>	        
    </table>
</div>

<div id="idEmployeeInfo" class="col-lg-6 col-md-6 col-sm-12" style="padding: 5px;border: 2px solid rgb(238, 238, 238);height: 250px;overflow-y: auto;">
    <div align="center" style="border: 1px solid rgb(204, 204, 204);"><b>Round Information</b></div>
    <input type="hidden" name="totalRound" id="totalRound" value="<%=(listRoundId != null && !listRoundId.isEmpty()) ? listRoundId.size() : "0" %>">
    <%	
        String recruitId = (String)request.getAttribute("recruitId");
        for (int i = 0; listRoundId!= null && i < listRoundId.size(); i++) {
        	List<List<String>> listEmpIds = hmEmpIdsRoundwise.get(listRoundId.get(i));
        	String assessmentId = hmRoundAssessment.get(listRoundId.get(i)+"_ASSESSID");
        	String assessmentName = hmRoundAssessment.get(listRoundId.get(i)+"_ASSESSNAME");
        	String roundRemoveStatus = "yes";
        	if(listEmpIds != null) {
        		roundRemoveStatus = "no";
        	}
        %>
    <div id="row_round<%=listRoundId.get(i) %>" class="row_round" style="border-bottom: 1px solid rgb(235, 235, 235);padding: 4px;">
        <table width="100%;">
            <tr>
                <td nowrap="nowrap" style="width: 50%;">
                    <input type="hidden" name="roundId" id="roundId" value="<%=listRoundId.get(i) %>">
                    <a href="javascript:void(0);" id="roundDIV_<%=listRoundId.get(i) %>" style="float: left; margin-left: 20px; font-weight: bold; width: 100%; color: <%if(i==0) { %> #ff851b; <% } else { %>#535353; <% } %>" onclick="setRoundIdForPanelist('<%=listRoundId.get(i) %>');">
                        <span style="float: left;">Round <%=listRoundId.get(i) %></span>
                    </a>
                    <div id="roundTitDiv<%=listRoundId.get(i) %>" style="float: left;margin-left:20px; font-size: 11px; font-style: oblique;"><%=uF.showData(hmRoundName.get(listRoundId.get(i)), "") %></div>
                </td>
                <td colspan="2">
                    <!-- =====start parvez -->
                	<%--  <a href="javascript:void(0)" onclick="setRoundTitle('<%=listRoundId.get(i) %>','<s:property value="recruitId"/>');" title="Set Round Title">
                    <i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> --%> 
                	<%-- <span id="roundTitleImgDiv<%=listRoundId.get(i) %>">
                		<a href="javascript:void(0)" onclick="setRoundTitle1('<%=listRoundId.get(i) %>','<s:property value="recruitId"/>');" title="Set Round Title">
                    	<i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                	</span>
                	<span id="roundTitleDiv<%=listRoundId.get(i) %>" style="display: none;float: left; margin-left: 5px;">
                		 <s:form id="frmSetInterviewRoundTitle" name="frmSetInterviewRoundTitle" >
                		 <!-- <div style="height:20px"> -->
                		 	<input type="text" name="roundName<%=listRoundId.get(i) %>" id="roundName<%=listRoundId.get(i) %>" value="<%=uF.showData(hmRoundName.get(listRoundId.get(i)), "") %>" style="width:110px !important; height:22px !important" />
	                		<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="setRoundTitle('<%=listRoundId.get(i) %>','<s:property value="recruitId"/>');" style="height:22px !important" />
	                		<!-- <input type="button" value="Submit" class="btn btn-primary" name="ok" onclick="setInterviewRoundTitle();" /> -->
                		 <!-- </div>  -->
	                	</s:form>
                	</span> --%>
                	<!-- =====end parvez===== -->

                	<!-- =====start parvez on 10-07-021 -->
                	 <a href="javascript:void(0)" onclick="setRoundTitle('<%=listRoundId.get(i) %>','<s:property value="recruitId"/>');" title="Set Round Title">
                    <i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> 
                	<!-- =====end parvez on 10-07-021===== -->
                	
                    <a href="javascript:void(0)" onclick="addRound('<%=maxRountId %>','<s:property value="recruitId"/>');" title="Add New Round"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
                    <%if(i>0){ %>
                    <a href="javascript:void(0)" onclick="removeRound(this.id,'<s:property value="recruitId"/>','<%=roundRemoveStatus %>')" id="<%=listRoundId.get(i) %>" title="Remove Round"><i class="fa fa-trash" aria-hidden="true"></i></a>
                    <%} %>	
                </td>
                <td>&nbsp;</td>
                <!-- <a href="javascript:void(0)" onclick="removeRound(this.id)" id="0" class="remove">Remove</a> -->
            </tr>
            <tr>
                <td colspan="3">
                    <div id="roundAssessDiv_<%=listRoundId.get(i) %>">
                        <span>
                        <a href="javascript: void(0);" onclick="viewAllAssessment('<%=listRoundId.get(i) %>', '<%=assessmentId %>')">Add Assessment:</a> &nbsp;
                        </span>
                        <span id="roundAssessLblSpan_<%=listRoundId.get(i) %>"> <%=uF.showData(assessmentName, "") %> </span>
                    </div>
                </td>
            </tr>
            <%for (int j = 0; listEmpIds!= null && j < listEmpIds.size(); j++) {
                List<String> innerList = listEmpIds.get(j);
                %>
            <tr>
                <td colspan="3" nowrap="nowrap"><%-- <%=j+1 %>.&nbsp; --%>
                    <input type="hidden" name="empId" id="empId" value="<%=innerList.get(0) %>">
                    <span style="float: left;"><%=innerList.get(1) %> 
                    <a href="javascript: void(0)" onclick="resetEmployee('<%=innerList.get(0) %>','<%=listRoundId.get(i) %>','<s:property value="recruitId"/>');" title="Remove Panel">
                    <i class="fa fa-arrow-circle-o-left" aria-hidden="true"></i></a>
                    </span>
                    <span id="roundchangeImgDiv<%=request.getAttribute("recruitId") %><%=listRoundId.get(i) %><%=innerList.get(0) %>" style="float: left;  margin-top: 4px;">
                    <a href="javascript: void(0)" onclick="showRounds('<%=request.getAttribute("recruitId") %><%=listRoundId.get(i) %><%=innerList.get(0) %>');" title="Change Panel Round">
                    <i class="fa fa-arrows-v" aria-hidden="true"></i></a>
                    </span>
                    <span id="roundsDiv<%=request.getAttribute("recruitId") %><%=listRoundId.get(i) %><%=innerList.get(0) %>" style="display: none; float: left; margin-left: 5px;">
                    <select name="strRound" id="strRound<%=request.getAttribute("recruitId") %><%=listRoundId.get(i) %><%=innerList.get(0) %>" style="width:95px !important;" onchange="changePanelRound('<%=innerList.get(0) %>','<%=listRoundId.get(i) %>','<%=recruitId %>',this.value);">
                    <%=request.getAttribute("option") %>
                    </select>
                    </span>
                </td>
            </tr>
            <%} %>
        </table>
    </div>
    <%} %>
</div>
<div class="clr paddingtop10">
    <div align="center">
        <input type="button" value="Finalize Plan & Send Notification" class="btn btn-primary" onclick="sendNotification('<%=formName%>');">
    </div>
</div>
