<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.training.FillCertificate"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/customAjax.js"></script>

<%
	String op = (String) request.getAttribute("operation");
	String strTitle = "Add Training Plan";
	if (op != null && op.equals("E")) {
		strTitle = "Edit Training Plan";
	}
	
	
%>
<style>
.steps .next,.steps .current{
padding-right: 5px;
padding-left: 5px;
}

a.close-font:before{
	 font-size: 24px;
    }
</style>


<script type="text/javascript">
$(function() {
	
	$("body").on('click','#closeButton1',function(){
		$(".modal-dialog1").removeAttr('style');
		$("#modal-body1").height(400);
		$("#modalInfo1").hide();
    });
	
});

 
 function closeForm() {
	 $("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
	 $.ajax({
 		url:'TrainingPlanInfo.action',
 		cache:false,
 		success:function(data){
 			$("#divResult").html(data);
 		}
 	});
	 
 }

</script>

<%
	List<FillCertificate> certificateList = (List<FillCertificate>) request.getAttribute("certificateList");
	String certificateId = (String) request.getAttribute("certificateId");
	String operation = (String) request.getAttribute("operation");
	UtilityFunctions uF = new UtilityFunctions();
	List<List<String>> trainerOuterList = (List<List<String>>) request.getAttribute("trainerOuterList");
	List<List<String>> selectedTrainerList = (List<List<String>>) request.getAttribute("selectedTrainerList");
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	Map<String, String> hmSelectedTrainer = (Map<String, String>) request.getAttribute("hmSelectedTrainer");
	Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
	
	List<String> plan_locationList=(List<String>)request.getAttribute("plan_locationList");
	Map<String,String> hmWLocation= (Map<String, String>) request.getAttribute("hmWLocation");
	Map<String,String> hmCertificatePrintMode=(Map<String,String>)request.getAttribute("hmCertificatePrintMode");
	//Map<String, List<List<String>>> hmGradeStandardwiseValue = (Map<String, List<List<String>>>) request.getAttribute("hmGradeStandardwiseValue");
	//System.out.println("step ===>>>> " + (String)request.getAttribute("step"));
	//EncryptionUtils EU = new EncryptionUtils();//Created by Dattatray Date:21-07-21 Note:Encryption
%>

<section class="content">
	<div class="row jscroll">
		 <section class="">
		 	  <div class="">
		 	  	<div class="box-header with-border">
                    <h3 class="box-title">
                    	<%
		if (request.getAttribute("operation") != null && ((String) request.getAttribute("operation")).equalsIgnoreCase("A")) {
	%>

	<div class="steps">


		<s:if test="step==1">
			<span class="current"> Training Plan:</span>
			<span class="next"> Add Trainer:</span>
			<span class="next"> Training Schedule:</span>
			<span class="next"> Plan:</span>
			<%-- <span class="next"> Assessment Association:</span> --%>
			<span class="next">Training Feedback:</span>

		</s:if>

		<s:if test="step==2">
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Plan: </a> </span>
			<span class="current"> Add Trainer:</span>
			<span class="next"> Training Schedule:</span>
			<span class="next"> Plan:</span>
			<%-- <span class="next"> Assessment Association:</span> --%>
			<span class="next">Training Feedback:</span>

		</s:if>

		<s:if test="step==3">
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Plan: </a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Add Trainer:</a> </span>
			<%-- 	<span class="next"><a href="AddTrainingPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id") %>" style="color:#5A87B4;font-weight:normal;font-size:11px;"> Training Schedule:</a></span>
		 --%>
			<span class="current"> Training Schedule:</span>
			<span class="next"> Plan:</span>
			<%-- <span class="next"> Assessment Association:</span> --%>
			<span class="next">Training Feedback:</span>

		</s:if>

		<s:if test="step==4">
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Plan: </a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Add Trainer:</a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=2&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Schedule: </a> </span>
			<span class="current"> Plan:</span>
			<%-- <span class="next"> Assessment Association:</span> --%>
			<span class="nTrainingext">Training Feedback:</span>

		</s:if>

				<%-- <s:if test="step==5">
			<span class="next"><a href="AddTrainingPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Plan:</a> </span>
			<s:if test="trainingType==1">
			<span class="next"><a href="AddTrainingPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Add Trainer:</a> </span>
			</s:if>	
			<span class="next"><a href="AddTrainingPlan.action?operation=E&step=2&ID=<%=request.getAttribute("plan_Id")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Schedule:</a> </span>
			<span class="next"><a href="AddTrainingPlan.action?operation=E&step=3&ID=<%=request.getAttribute("plan_Id")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Learners:</a> </span>
			<span class="current"> Assessment Association:</span>
			<span class="next">Feedback:</span>

		</s:if> --%>

		<s:if test="step==5">
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Plan:</a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Add Trainer:</a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=2&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Schedule:</a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=3&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Plan:</a> </span>
			<%-- <span class="next"><a href="AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>&trainingType=<%=request.getAttribute("trainingType")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Assessment Association:</a> </span> --%>
			<span class="current"> Training Feedback:</span>

		</s:if>

	</div>

	<%
		} else if (request.getAttribute("operation") != null && ((String) request.getAttribute("operation")).equalsIgnoreCase("E")) {
	%>


	<div class="steps">


		<s:if test="step==1">
			<span class="current"> Training Plan:</span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Add Trainer: </a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=2&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Training Schedule:</a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=3&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Plan: </a> </span>
			<%-- <span class="next"><a href="AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Assessment Association: </a> </span> --%>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Feedback: </a> </span>

		</s:if>

		<s:if test="step==2">
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Plan: </a> </span>
			<span class="current"> Add Trainer:</span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=2&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Schedule: </a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=3&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Plan: </a> </span>
			<%-- <span class="next"><a href="AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Assessment Association: </a> </span> --%>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Feedback: </a> </span>
		</s:if>

		<s:if test="step==3">
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Plan:</a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Add Trainer: </a> </span>
			<span class="current"> Training Schedule:</span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=3&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Plan: </a> </span>
			<%-- <span class="next"><a href="AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Assessment Association: </a> </span> --%>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Feedback: </a> </span>
		</s:if>

		<s:if test="step==4">
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Training Plan:</a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Add Trainer: </a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=2&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Training Schedule:</a> </span>
			<span class="current"> Plan:</span>
			<%-- <span class="next"><a href="AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Assessment Association: </a> </span> --%>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Training Feedback: </a> </span>
		</s:if>

		<s:if test="step==5">
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=0&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Training Plan:</a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=1&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Add Trainer: </a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=2&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Training Schedule:</a> </span>
			<span class="next"><a href="javascript:void(0);" onclick="loadStepOnClick('AddTrainingPlan.action?operation=E&step=3&ID=<%=request.getAttribute("plan_Id")%>')"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;">Plan: </a> </span>
			<%-- <span class="next"><a href="AddTrainingPlan.action?operation=E&step=4&ID=<%=request.getAttribute("plan_Id")%>"
				style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Assessment Association: </a> </span> --%>
			<span class="current"> Training Feedback:</span>
		</s:if>

	</div>


	<%
		}
	%>
                    </h3>
                    <div class="pull-right">
						<a href="javascript:void(0);" onclick="closeForm()" class="close-font" style="margin-right:20px;"/>
					</div>
                </div>
                <div class="box-body" style="padding: 5px; overflow-y: auto;">
                    <div class="leftbox reportWidth">


	
	<!-- the tabs -->
	<ul class="tabs" style="background-color: rgba(45, 157, 231, 0.14);padding-top: 5px;padding-bottom: 5px;">

		<s:if test="step==1 || mode=='report'">
			<li><a class="current" href="#tab1">Add Training Plan</a></li>
		</s:if>

		<s:if test="step==2 || mode=='report'">
			<li><a class="current" href="#tab2">Add Trainer</a>
			</li>
		</s:if>

		<s:if test="step==3 || mode=='report'">
			<li><a class="current" href="#tab3">Add Training Schedule</a></li>
		</s:if>

		<s:if test="step==4 || mode=='report'">
			<li><a class="current" href="#tab4">Plan</a></li>
		</s:if>

		<s:if test="step==5 || mode=='report'">
			<li><a class="current" href="#tab5">Add Training Feedback </a></li>
		</s:if>


	</ul>

	<!-- tab "panes" -->
	<div class="panes">
		<s:if test="step==1 || mode=='report'">
	<script type="text/javascript">
		
		function displayCertificateRow(certificateValue){
			if(document.getElementById("strCertificate").checked==true)	{
				document.getElementById("trCertificate").style.display = "table-row";
				//$("#strCertificateId").addClass("validateRequired");
		
		    }else {
				document.getElementById("trCertificate").style.display = "none";
				/* $("#strCertificateId").removeClass("validateRequired");
				$("#strCertificateId").prop("required",false); */
			}
		}
		
		function createNewCertificate(){
			
			 var id1=document.getElementById("idAddCertificate");
		        if(id1){
		            id1.parentNode.removeChild(id1);
		        }
			  var id=document.getElementById("strCertificateId").value ;
			  if(id=='0'){
				 //'A',''		  
			  var dialogEdit = '.modal-body';
				 $(dialogEdit).empty();
				 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				 $("#modalInfo").show();
				 if($(window).width() >= 1100){
					 $(".modal-dialog").width(1100);
				 }
				 if($(window).height() >= 600){
					 $(".modal-body").height(600);
				 }
				 $(".modal-title").html('Add New Certificate');
				 $.ajax({
						url : "AddCertificate.action?operation=A&type=training&ID=",
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
			  }
		  }
		
		function previewcertificate(){
			  
			  //removeLoadingDiv('the_div');
			  var id=document.getElementById("strCertificateId").value ;
			  
			  if(id=='-1'){
				  alert("You have not selected any Certificate");
			  }else{
				  var cert = new Array();
				  cert=id.split("::::");
				  var certid=cert[0];
				  var printMode=cert[1];
				  
				  var dialogEdit = '.modal-body';
					 $(dialogEdit).empty();
					 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
					 $("#modalInfo").show();
					 $(".modal-title").html('View Certificate');
					 $.ajax({
							url : "ViewCertificate.action?ID="+certid,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
			  
			  }
		  }
		
		function getWLocations(value) {
			var action = "GetWorkLocationList.action?orgId="+value;
			getContent('wLocSpan', action);
			setTimeout(function(){ $("#plan_idwlocation").multiselect({
				noneSelectedText: 'Select Something (required)'
			}).multiselectfilter(); }, 500);
		}
		
	</script>
			<s:form theme="simple" name="frmAddTrainingPlan1" id="frmAddTrainingPlan1" action="AddTrainingPlan" method="POST" cssClass="formcss" enctype="multipart/form-data">

				<s:hidden name="operation"></s:hidden>
				<s:hidden name="ID"></s:hidden>
				<s:hidden name="step"></s:hidden>
				<s:hidden name="planId" id="hiddenplanId"></s:hidden>

				<div style="float: left;">
					<table border="0" class="table  table_no_border">

						<tr>
							<td class="tdLabelheadingBg " colspan="2"><span
								style="color: #68AC3B; font-size: 18px; padding: 5px;">
									Step 1: </span> Training Plan Information</td>
						</tr>
				

						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Training Title:<sup>*</sup> </td>
							<td><s:textfield name="trainingTitle" id="trainingTitle" cssClass="validateRequired form-control "></s:textfield>
							</td>
						</tr>


						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Training Objective:<sup>*</sup></td>
							<td><s:textarea name="trainingObjective" cssClass="validateRequired form-control " rows="5" cols="70"></s:textarea>
							</td>
						</tr>

						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Training Preface/Summary:</td>
							<td><s:textarea name="trainingSummary" rows="5" cols="70" cssClass=" form-control "></s:textarea>
							</td>
						</tr>

					
						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Certificate:</td>
							<td><s:checkbox id="strCertificate" name="strCertificate" fieldValue="true" onchange="displayCertificateRow();"></s:checkbox>
							</td>
						</tr>


						<tr id="trCertificate"
							<s:if test="strCertificate==false"> style="display: none" </s:if>>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Select Certificate:<sup>*</sup></td>
							<td id="tdCertificate">
								<div id="divCertificate">
								
								<s:select theme="simple" name="strCertificateId" id="strCertificateId" listKey="id" 
									listValue="name" cssClass="form-control " list="certificateList" value="certificateId" onchange="createNewCertificate();"/>
								<span><a href="javascript:void(0)" onclick="previewcertificate();">Preview</a> </span>
								</div></td>


						</tr>

						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Associated With Attribute:<sup>*</sup> </td>
							<td>
								<span> <s:select theme="simple" name="strAttribute" cssClass="validateRequired form-control " list="attributeList" listKey="id" id="strAttribute" listValue="name" 
								size="4" multiple="true" value="attributeID"/> </span>
							</td>
						</tr>
						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Organization:<sup>*</sup></td>
							<td>
								<span> <s:select theme="simple" name="plan_organization" listKey="orgId" listValue="orgName" headerKey="" 
								cssClass="validateRequired form-control " headerValue="select Organisation" list="organisationList" key="" onchange="getWLocations(this.value);" /> </span>
							</td>
						</tr>

						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Location:<sup>*</sup></td>
							<td>
								<span id="wLocSpan">
									<s:select theme="simple" name="plan_idwlocation" listKey="wLocationId" listValue="wLocationName" headerKey="" 
									cssClass="validateRequired  form-control " list="workList" key="" multiple="true" size="4" 
								 		value="plan_idwlocationvalue"/> 
							 	</span>
					 		</td>
						</tr>
						

					</table>
				</div>

				<div style="width: 100%; float: right;">
					<s:submit cssClass="btn btn-primary" cssStyle="width:160px; float:right;" name="stepSubmit" value="Submit & Proceed"/>
				</div>

			</s:form>
		</s:if>
		
		<s:if test="step==2 || mode=='report'">
		<script type="text/javascript">

			
			function getSelectedTrainer(checked,emp){
				var empselect=document.getElementById("trnselected").value;
				var alrtMsg = "";
				if(checked == true) {
					alrtMsg = "Are you sure, you want to add this trainer?";
				} else {
					alrtMsg = "Are you sure, you want to remove this trainer?";
				}
				if(confirm(alrtMsg)) {
					var xmlhttp = GetXmlHttpObject();
					if (xmlhttp == null) {
						alert("Browser does not support HTTP Request");
						return;
					} else {
						var xhr = $.ajax({
							url : "GetSelectedTrainerAjax.action?type=one&chboxStatus="+checked+"&selectedEmp="+emp+"&existemp="+empselect,		
							cache : false,
							success : function(data) {
								//alert("data == "+data);
				            	if(data == "") {
				            		
				            	}else{
				            		var allData = data.split("::::");
				                    document.getElementById("idTrainerInfo").innerHTML = allData[0];
				                    if(checked == 'false') {
				                    	document.getElementById("strTrainerId"+emp).checked = false;
				                    }
				            	}
				            }
						});
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
			
			function openTrainerProfilePopup(empId) {
				
				var dialogEdit = '.modal-body';
				 $(dialogEdit).empty();
				 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				 $("#modalInfo").show();
				 $(".modal-title").html('Trainer Information');
				 if($(window).width()>=1100){
						$(".modal-dialog").width(1100);
					}
				 $.ajax({
						//url : "ApplyLeavePopUp.action",  
						url :"TrainerMyProfile.action?empId="+empId+"&proPopup=proPopup" ,
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
				 }
			
			function openPanelEmpProfilePopup(empId) {
				
					var dialogEdit = '.modal-body';
					$(dialogEdit).empty();
					$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
					$("#modalInfo").show();
					$(".modal-title").html('Employee Information');
					if($(window).width()>=1100){
						$(".modal-dialog").width(1100);
					}
					$.ajax({
						url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
			}
			
			
		</script>
			<s:form theme="simple" name="frmAddTrainingPlan2" id="frmAddTrainingPlan2" action="AddTrainingPlan" method="POST" cssClass="formcss" enctype="multipart/form-data">
				<s:hidden name="planId" id="hiddenplanId"></s:hidden>
				<s:hidden name="step"></s:hidden>
				<s:hidden name="operation"></s:hidden>
				<s:hidden name="ID"></s:hidden>
				<s:hidden name="alignedwith"></s:hidden>
				<s:hidden name="trainingType"></s:hidden>

				<div style="float: left; width: 60%;">
					<table border="0" class="table  table_no_border" id="trainerInfo" style="width: 100%;">
						<tr>
							<td class="tdLabelheadingBg " colspan="3">
							<span style="color: #68AC3B; font-size: 18px; padding: 5px;">Step 2: </span> Add Trainer</td>
						</tr>
						<tr>
							<td colspan="3">
								
								<div id="idTrainerList" style="float: left; width: 49%; padding-right: 20px;">
								<div style="width: 100%; border: 2px solid rgb(204, 204, 204);height: 400px;overflow: auto;">
									<table class="table  table_no_border" width="100%">
										<tr>
											<th width="10%"></th>
											<th align="center">Trainer</th>
											<th align="center">Factsheet</th>
										</tr>
										<%
											for (int i = 0; trainerOuterList != null && i < trainerOuterList.size(); i++) {
													List<String> innerList = trainerOuterList.get(i);
													String trainerID = innerList.get(0);
													String trainerEmpID = innerList.get(1);
													String trainerTYPE = innerList.get(2);
													String trainerName = innerList.get(3);
										%>
													<tr>
														<td>
														<input type="checkbox" name="strTrainerId" id="strTrainerId<%=trainerID%>" onclick="getSelectedTrainer(this.checked,this.value);"
															value="<%=trainerID%>" <%if (hmSelectedTrainer.get(trainerID) != null) {%> checked="checked" <%}%>>
																										
														</td>
														<td>
															<%=trainerName%>
														</td>
														<td>
														<%if(trainerTYPE != null && trainerTYPE.equals("EXTrainer")){ %>
														<a  href="javascript: void(0)" onclick="openTrainerProfilePopup('<%=trainerEmpID %>');"><i class="fa fa-address-card"></i></a>
														<%} else { %>
														<!-- Created by Dattatray Date:21-07-21 Note:empid Encryption -->
														<a  href="javascript: void(0)" onclick="openPanelEmpProfilePopup('<%=trainerEmpID %>');"><i class="fa fa-address-card"></i></a>
														<% } %>
														
														</td>
				      							</tr>
										<% } %>
									</table> 
								</div>
								</div>
								
								<div id="idTrainerInfo" style="float: left; width: 40%; padding: 5px; border: 2px solid rgb(204, 204, 204);">
									<%
									String trainerIds = (String)request.getAttribute("trainerIds");
									//System.out.println("trainerIds ---> " + trainerIds);
									//System.out.println("selectedTrainerList ---> " + selectedTrainerList);
									if (selectedTrainerList != null && !selectedTrainerList.isEmpty()) {%>
										<div style="border: 2px solid #ccc;">
											<div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Trainers</b></div>
											<%	int trnCnt =0;
												for (int i = 0; i < selectedTrainerList.size(); i++) {
													List<String> innerList = selectedTrainerList.get(i);
													if(innerList.get(1) != null && !innerList.get(1).equals("null")) {
														trnCnt++;
											%>
														<div style="float: left; width: 100%; margin: 5px;"><strong><%=trnCnt %>.</strong>&nbsp;&nbsp;<%=innerList.get(1)%>&nbsp;
															<a href="javascript: void(0)" onclick="getSelectedTrainer('false','<%=innerList.get(0)%>');">
															<img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
														</div>
												  <% } 
												} %>
								   </div>
								<% } else { %>
										<div class="nodata msg" style="width: 85%"> <span>No Trainer selected</span> </div>
								<% 	} %>
									<input type="hidden" name="trnselected" id="trnselected" value="<%=trainerIds!=null && !trainerIds.equals("") ? trainerIds :"0" %>"/>
								</div>
							</td>
						</tr>
					</table>
				</div>	
				
				<div style="float: right">
					<iframe id="ifrmTrainerCalender" scrolling="no" frameborder="0" width="300" height="320"> </iframe>

				</div>

				<div style="width: 100%; margin-top: 0cm; float: right;">
					<s:submit cssClass="btn btn-primary" cssStyle="width:160px; float:right;" value="Submit & Proceed" name="stepSubmit"/>
				</div>
			</s:form>
		</s:if>
			
		<s:if test="step==3 || mode=='report'">
		
<script type="text/javascript">
	
	$(function(){
	    // binds form submission and fields to the validation engine
		 checkPeriodOnload();
	});	
	
	function checkPeriod(value) {
		// dayMonth  monthly annualy weekly 
		var scheduleType = document.getElementById("hideScheduleType").value;
		
		if (value == '3') {
			document.getElementById("monthly").style.display = "block";
			document.getElementById("weekday").selectedIndex = 0;
			document.getElementById("day").selectedIndex = 0;
			document.getElementById("weekly").style.display = "none";
			document.getElementById("dayScheduleTD").style.display = "none";
			document.getElementById("dayScheduleLabelTD").style.display = "none";
			document.getElementById("startdateTraining").value = "";
			document.getElementById("enddateTraining").value = "";
			/* $("#day").addClass("validateRequired");
			$("#weekday").removeClass("validateRequired");
			$("#weekday").prop("required",false); */
			var alSessionDataSize=document.getElementById("alSessionDataSize").value;
			
			if(alSessionDataSize==0){
				document.getElementById("trainingSchedulePeriodTR0").style.display = "none";
				document.getElementById("weekDaysTR").style.display = "none";
				$("input[name=weekDays]").each(function(index) {
					$(this).prop('checked', false);
				});
				document.getElementById("oneTimeDate0").value = "";
			}else{			
				for(var i=0;i<alSessionDataSize;i++){
					var id=document.getElementById("trainingSchedulePeriodTR"+i);
					var id1=document.getElementById("weekDaysTR");
					var id2=document.getElementById("addSessionTD"+i);
					
					if(id){
						document.getElementById("trainingSchedulePeriodTR"+i).style.display = "none";
						if(i > 0) {
							document.getElementById("startTimeTR"+i).style.display = "none";
							document.getElementById("endTimeTR"+i).style.display = "none";
							document.getElementById("startTime"+i).value = "";
							document.getElementById("endTime"+i).value = "";
						} else {
							document.getElementById("startTime"+i).value = "";
							document.getElementById("endTime"+i).value = "";
						}
						document.getElementById("oneTimeDate"+i).value = "";
					}
					if(id1){
						document.getElementById("weekDaysTR").style.display = "none";
						$("input[name=weekDays]").each(function(index) {
							$(this).prop('checked', false);
						});
					}
					if(id2){
						document.getElementById("addSessionTD"+i).style.display = "none";
					}
				}
			}
			
		} else if (value == '2') {
			document.getElementById("weekly").style.display = "block";
			document.getElementById("weekday").selectedIndex = 0;
			document.getElementById("day").selectedIndex = 0;
			document.getElementById("monthly").style.display = "none";
			document.getElementById("dayScheduleTD").style.display = "none";
			document.getElementById("dayScheduleLabelTD").style.display = "none";
			document.getElementById("startdateTraining").value = "";
			document.getElementById("enddateTraining").value = "";
			
			/* $("#weekday").addClass("validateRequired");
			$("#day").removeClass("validateRequired");
			$("#day").prop("required",false); */
			var alSessionDataSize=document.getElementById("alSessionDataSize").value;
			
			if(alSessionDataSize==0){
				document.getElementById("trainingSchedulePeriodTR0").style.display = "none";
				document.getElementById("weekDaysTR").style.display = "none";
				$("input[name=weekDays]").each(function(index) {
					$(this).prop('checked', false);
				});
				document.getElementById("oneTimeDate0").value = "";
			}else{			
				for(var i=0;i<alSessionDataSize;i++){
					var id=document.getElementById("trainingSchedulePeriodTR"+i);
					var id1=document.getElementById("weekDaysTR");
					var id2=document.getElementById("addSessionTD"+i);
					
					if(id){
						document.getElementById("trainingSchedulePeriodTR"+i).style.display = "none";
						if(i > 0) {
							document.getElementById("startTimeTR"+i).style.display = "none";
							document.getElementById("endTimeTR"+i).style.display = "none";
							document.getElementById("startTime"+i).value = "";
							document.getElementById("endTime"+i).value = "";
						} else {
							document.getElementById("startTime"+i).value = "";
							document.getElementById("endTime"+i).value = "";
						}
						
						document.getElementById("oneTimeDate"+i).value = "";
					}
					if(id1){
						document.getElementById("weekDaysTR").style.display = "none";
						$("input[name=weekDays]").each(function(index) {
							$(this).prop('checked', false);
						});
					}
					if(id2){
						document.getElementById("addSessionTD"+i).style.display = "none";
					}
				}
			}
		} else {
			document.getElementById("weekday").selectedIndex = 0;
			document.getElementById("day").selectedIndex = 0;
			document.getElementById("weekly").style.display = "none";
			document.getElementById("monthly").style.display = "none";
			document.getElementById("dayScheduleTD").style.display = "table-cell";
			document.getElementById("dayScheduleLabelTD").style.display = "table-cell";
			
			/* $("#day").removeClass("validateRequired");
			$("#weekday").removeClass("validateRequired");
			$("#weekday").prop("required",false);
			$("#day").prop("required",false); */
			var alSessionDataSize=document.getElementById("alSessionDataSize").value;
			if(alSessionDataSize==0){
				if(scheduleType == '2'){
					document.getElementById("trainingSchedulePeriodTR0").style.display = "table-row";
				}else{
					document.getElementById("weekDaysTR").style.display = "table-row";
					$("input[name=weekDays]").each(function(index) {
						$(this).prop('checked', true);
					});
				}
			}else{	
				//alert("scheduleType ===> " + scheduleType);
				//alert("alSessionDataSize ===> " + alSessionDataSize);
				for(var i=0;i<alSessionDataSize;i++){
					var id=document.getElementById("trainingSchedulePeriodTR"+i);
					var id1=document.getElementById("weekDaysTR");
					var id2=document.getElementById("addSessionTD"+i);
					if(id){
						if(scheduleType == '2'){
							document.getElementById("trainingSchedulePeriodTR"+i).style.display = "table-row";
							document.getElementById("startTimeTR"+i).style.display = "table-row";
							document.getElementById("endTimeTR"+i).style.display = "table-row";
							document.getElementById("weekDaysTR").style.display = "none";
							if(id2){
								document.getElementById("addSessionTD"+i).style.display = "table-cell";
							}
						}else{
							if(id) {
								document.getElementById("trainingSchedulePeriodTR"+i).style.display = "none";
								if(i > 0) {
									document.getElementById("startTimeTR"+i).style.display = "none";
									document.getElementById("endTimeTR"+i).style.display = "none";
								}
							}
							if(id1){
								document.getElementById("weekDaysTR").style.display = "table-row";
								$("input[name=weekDays]").each(function(index) {
									$(this).prop('checked', true);
								});
								if(id2){
									document.getElementById("addSessionTD"+i).style.display = "none";
								}
							}
						}
					}
				}
			}
			
		}
	}
	
	function checkPeriodOnload() {
		var value = document.getElementById("trainingSchedulePeriod").value;
		var scheduleType = document.getElementById("hideScheduleType").value;
		
		// dayMonth  monthly annualy weekly 
		if (value == '3') {
			document.getElementById("monthly").style.display = "block";
			document.getElementById("weekly").style.display = "none";
			document.getElementById("dayScheduleTD").style.display = "none";
			document.getElementById("dayScheduleLabelTD").style.display = "none";
			//$("#day").addClass("validateRequired");
			
			var alSessionDataSize=document.getElementById("alSessionDataSize").value;
			
			if(alSessionDataSize==0){
				document.getElementById("trainingSchedulePeriodTR0").style.display = "none";
				document.getElementById("weekDaysTR").style.display = "none";
				$("input[name=weekDays]").each(function(index) {
					$(this).prop('checked', false);
				});
				document.getElementById("oneTimeDate0").value = "";
			}else{			
				for(var i=0;i<alSessionDataSize;i++){
					var id=document.getElementById("trainingSchedulePeriodTR"+i);
					var id1=document.getElementById("weekDaysTR");
					var id2=document.getElementById("addSessionTD"+i);
								
					if(id){
						document.getElementById("trainingSchedulePeriodTR"+i).style.display = "none";
						if(i > 0) {
							document.getElementById("startTimeTR"+i).style.display = "none";
							document.getElementById("endTimeTR"+i).style.display = "none";
							document.getElementById("startTime"+i).value = "";
							document.getElementById("endTime"+i).value = "";
						}
						document.getElementById("oneTimeDate"+i).value = "";
					}
					if(id1){
						document.getElementById("weekDaysTR").style.display = "none";
						$("input[name=weekDays]").each(function(index) {
							$(this).prop('checked', false);
						});
					}
					if(id2){
						document.getElementById("addSessionTD"+i).style.display = "none";
					}
				}
			}
		} else if (value == '2') {
			document.getElementById("weekly").style.display = "block";
			document.getElementById("monthly").style.display = "none";
			document.getElementById("dayScheduleTD").style.display = "none";
			document.getElementById("dayScheduleLabelTD").style.display = "none";
			//$("#weekday").addClass("validateRequired");
			
			
			var alSessionDataSize=document.getElementById("alSessionDataSize").value;
			
			if(alSessionDataSize==0){
				document.getElementById("trainingSchedulePeriodTR0").style.display = "none";
				document.getElementById("weekDaysTR").style.display = "none";
				$("input[name=weekDays]").each(function(index) {
					$(this).prop('checked', false);
				});
				document.getElementById("oneTimeDate0").value = "";
			}else{			
				for(var i=0;i<alSessionDataSize;i++){
					var id=document.getElementById("trainingSchedulePeriodTR"+i);
					var id1=document.getElementById("weekDaysTR");
					var id2=document.getElementById("addSessionTD"+i);
					
					if(id){
						document.getElementById("trainingSchedulePeriodTR"+i).style.display = "none";
						if(i > 0) {
							document.getElementById("startTimeTR"+i).style.display = "none";
							document.getElementById("endTimeTR"+i).style.display = "none";
							document.getElementById("startTime"+i).value = "";
							document.getElementById("endTime"+i).value = "";
						}
						document.getElementById("oneTimeDate"+i).value = "";
					}
					if(id1){
						document.getElementById("weekDaysTR").style.display = "none";
						$("input[name=weekDays]").each(function(index) {
							$(this).prop('checked', false);
						});
					}
					if(id2){
						document.getElementById("addSessionTD"+i).style.display = "none";
					}
				}
			}
		} else {
			document.getElementById("weekly").style.display = "none";
			document.getElementById("monthly").style.display = "none";
			document.getElementById("dayScheduleTD").style.display = "table-cell";
			document.getElementById("dayScheduleLabelTD").style.display = "table-cell";
			
			var alSessionDataSize=document.getElementById("alSessionDataSize").value;
			
			if(alSessionDataSize==0){
				if(scheduleType == '2') {
					document.getElementById("trainingSchedulePeriodTR0").style.display = "table-row";
				} else {
					document.getElementById("weekDaysTR").style.display = "table-row";
					$("input[name=weekDays]").each(function(index) {
						$(this).prop('checked', true);
					});
				}
			} else {
				//alert("scheduleType ===> " + scheduleType);
				//alert("alSessionDataSize ===> " + alSessionDataSize);
				
				for(var i=0;i<alSessionDataSize;i++) {
					var id=document.getElementById("trainingSchedulePeriodTR"+i);
					var id1=document.getElementById("weekDaysTR");
					var id2=document.getElementById("addSessionTD"+i);
					if(id) {
						if(scheduleType == '2') {
							document.getElementById("trainingSchedulePeriodTR"+i).style.display = "table-row";
							document.getElementById("startTimeTR"+i).style.display = "table-row";
							document.getElementById("endTimeTR"+i).style.display = "table-row";
							document.getElementById("weekDaysTR").style.display = "none";
							if(id2){
								document.getElementById("addSessionTD"+i).style.display = "table-cell";
							}
						} else {
							if(id) {
								document.getElementById("trainingSchedulePeriodTR"+i).style.display = "none";
								if(i > 0) {
									document.getElementById("startTimeTR"+i).style.display = "none";
									document.getElementById("endTimeTR"+i).style.display = "none";
								}
							}
							if(id1) {
								document.getElementById("weekDaysTR").style.display = "table-row";
								/* $("input[name=weekDays]::checkbox").each(function(index) {
									$(this).attr('checked', true);
								}); */
								if(id2) {
									document.getElementById("addSessionTD"+i).style.display = "none";
								}
							}
						}
					}
				}
			}
		
		}
	}
	
	function changeScheduleType(value) {
	
			if (value == '1') {
			//weekDaysTR trainingSchedulePeriodTR addSessionTD
			var alSessionDataSize=document.getElementById("alSessionDataSize").value;
			if(alSessionDataSize==0){
				$("input[name=weekDays]").each(function(index) {
					$(this).prop('checked', true);
				});
				document.getElementById("weekDaysTR").style.display = "table-row";
											
				document.getElementById("trainingSchedulePeriodTR0").style.display = "none";
				document.getElementById("addSessionTD0").style.display = "none";
				document.getElementById("oneTimeDate0").value = "";
								
			}else{		
				if(document.getElementById("weekDaysTR")) {
					document.getElementById("weekDaysTR").style.display = "table-row";
					 $("input[name=weekDays]").each(function(index) {
						$(this).prop('checked', true);
					}); 
				}
			
				for(var i=0;i<alSessionDataSize;i++){
					var id=document.getElementById("trainingSchedulePeriodTR"+i);
					if(id){
				
						document.getElementById("trainingSchedulePeriodTR"+i).style.display = "none";
						if(i > 0) {
							document.getElementById("startTimeTR"+i).style.display = "none";
							document.getElementById("endTimeTR"+i).style.display = "none";
							document.getElementById("startTime"+i).value = "";
							document.getElementById("endTime"+i).value = "";
							/* $("#startTime"+i).removeClass("validateRequired");
							$("#endTime"+i).removeClass("validateRequired");
							$("#oneTimeDate"+i).removeClass("validateRequired");
							
							$("#startTime"+i).prop("required",false);
							$("#endTime"+i).prop("required",false);
							$("#oneTimeDate"+i).prop("required",false); */
						}
						if(document.getElementById("addSessionTD"+i)) {
							document.getElementById("addSessionTD"+i).style.display = "none";
						}
						
						document.getElementById("oneTimeDate"+i).value = "";
					}
				}
			}
			
		} else {
			var alSessionDataSize=document.getElementById("alSessionDataSize").value;
			if(alSessionDataSize==0){
				$("input[name=weekDays]").each(function(index) {
					$(this).prop('checked', false);
				});
			
				document.getElementById("weekDaysTR").style.display = "none";
				document.getElementById("trainingSchedulePeriodTR0").style.display = "table-row";
				document.getElementById("addSessionTD0").style.display = "table-cell";
			
			}else{		
				if(document.getElementById("weekDaysTR")) {
					document.getElementById("weekDaysTR").style.display = "none";
					$("input[name=weekDays]").each(function(index) {
						$(this).prop('checked', false);
					});
				}
				for(var i=0;i<alSessionDataSize;i++){
					var id=document.getElementById("trainingSchedulePeriodTR"+i);
					if(id) {
						document.getElementById("trainingSchedulePeriodTR"+i).style.display = "table-row";
						document.getElementById("startTimeTR"+i).style.display = "table-row";
						document.getElementById("endTimeTR"+i).style.display = "table-row";
						
						/* $("#startTime"+i).addClass("validateRequired");
						$("#endTime"+i).addClass("validateRequired");
						$("#oneTimeDate"+i).addClass("validateRequired"); */
											
						if(document.getElementById("addSessionTD"+i)) {
							document.getElementById("addSessionTD"+i).style.display = "table-cell";
						}
					}
				}
			}
		}
	}
	
	function AddSessionAjax() {
		
	    var trCnt = document.getElementById("alSessionDataSize").value;
		if(trCnt == 0){
			trCnt = 1;
		}
		
		var trainingPeriod=document.getElementById("trainingSchedulePeriod").value;
		var trstyle="display:none;";
		if(trainingPeriod=='1'){
			trstyle="display:table-row;";	
		}	
		
		var tr= document.createElement('tr');
		tr.id = "trainingSchedulePeriodTR"+trCnt;
		tr.setAttribute("style",trstyle);
		var idOneTimeDate = "oneTimeDate"+trCnt;
		
		var innerhtml = "<td style=\"vertical-align: top; text-align: right;\" class=\"txtlabel\"><span style=\"float: left;margin-left: 50px;\">Day "+(parseInt(trCnt)+1)+"</span>Select Date :</td>"
		+ "<td colspan=\"2\"><input type=\"text\" class=\"validateRequired form-control\" id=\"oneTimeDate"+trCnt+"\"  name=\"oneTimeDate\" onchange=\"checkDates(this.value,'"+idOneTimeDate+"','" + trCnt + "')\"> "
		+ "</td>";
		
		tr.innerHTML =innerhtml;
		
		document.getElementById('trainingScheduleTableId').appendChild(tr);
		
		$("#oneTimeDate"+trCnt).datepicker({
			format : 'dd/mm/yyyy',
			yearRange : '1980:2020',
			changeYear : true
		}); 
		
		
		var trtag = document.createElement('tr');
		trtag.id = "startTimeTR" + trCnt;
		var a = "<td style=\" text-align: right\" class=\"txtlabel\">Start Time :</td>"
			+ "<td  colspan=\"2\"><input type=\"text\" class=\"validateRequired  form-control\" id=\"startTime"+trCnt+"\" name=\"startTime\"> "
			+ "</td>";
			
		
		trtag.innerHTML = a;
		
		var trtag1 = document.createElement('tr');
		trtag1.id = "endTimeTR" + trCnt;
		var b = "<td style=\"text-align: right\" class=\"txtlabel\">End Time :</td>"
			+ "<td><input type=\"text\" class=\"validateRequired form-control\" id=\"endTime"+trCnt+"\" name=\"endTime\"> "
			+ "</td> "
			+"<td style=\"float: left;\"><a href=\"javascript:void(0)\" onclick=\"AddSessionAjax();\" class=\"add\" title=\"Add Another Session\">Add Another Session</a>"
			+"<a href=\"javascript:void(0)\" onclick=\"DeleteSessionAjax('startTimeTR"+ trCnt + "','endTimeTR" + trCnt + "','trainingSchedulePeriodTR"+trCnt+"')\" class=\"remove\" title=\"Remove Session\">Remove</a><td>";
		
		trtag1.innerHTML = b;
		
		document.getElementById('trainingScheduleTableId').appendChild(trtag);
		document.getElementById('trainingScheduleTableId').appendChild(trtag1);
		
		
		 var date_yest = new Date();
	    var date_tom = new Date();
	    date_yest.setHours(0,0,0);
	    date_tom.setHours(23,59,59); 
	    
		 $("input[name='startTime']").datetimepicker({
			format: 'HH:mm',
			minDate: date_yest
	    }).on('dp.change', function(e){ 
	    	$("input[name='endTime']").data("DateTimePicker").minDate(e.date);
	    });
		
		$("input[name='endTime']").datetimepicker({
			format: 'HH:mm',
			maxDate: date_tom
	    }).on('dp.change', function(e){ 
	    	$("input[name='startTime']").data("DateTimePicker").maxDate(e.date);
	    });  
		trCnt++;
		//document.getElementById("alSessionDataSize").value=parseInt(document.getElementById("alSessionDataSize").value)+1;
		document.getElementById("alSessionDataSize").value=trCnt;

	}

function checkDates(date,id,count) {
		
		var trCnt = document.getElementById("alSessionDataSize").value;
		if(trCnt == 0){
			trCnt = 1;
		}
		
		var existDate = "";
		for (var i = 0; i < trCnt; i++) {
			if(i != count) {	
				existDate = document.getElementById("oneTimeDate"+i).value;
				if(existDate != null && existDate != "" && existDate!="null" ) {
				//	alert("existDate==>"+existDate+"==>date==>"+date);
					if(existDate == date) {
						alert("Already scheduled, please select another date!");
						document.getElementById(id).value="";
					}
				}
			}
		 }
	}
	
	function DeleteSessionAjax(startTimeTR, endTimeTR,oneTimeTR) {
		//	alert("startTimeTr==>"+startTimeTR+"==>endTimeTR==>"+endTimeTR+"==>oneTimeTR==>"+oneTimeTR);
			removeRow(startTimeTR);
			removeRow(endTimeTR);
			
			removeRow(oneTimeTR);
			var trCnt = document.getElementById("alSessionDataSize").value;
			var cnt = (parseInt(trCnt))-1;
			document.getElementById("alSessionDataSize").value=cnt;
		}


	function removeRow(id) {
		
		var row_id = document.getElementById(id);
		if (row_id && row_id.parentNode && row_id.parentNode.removeChild) {
			row_id.parentNode.removeChild(row_id);
			
		}
	}
	
</script>
		

			<s:form theme="simple" name="frmAddTrainingPlan3" id="frmAddTrainingPlan3" action="AddTrainingPlan" method="POST" cssClass="formcss" enctype="multipart/form-data">
				<s:hidden name="planId" id="hiddenplanId"></s:hidden>
				<s:hidden name="step"></s:hidden>
				<s:hidden name="operation"></s:hidden>
				<s:hidden name="ID"></s:hidden>
				<s:hidden name="trainingType"></s:hidden>
				<s:hidden name="alignedwith"></s:hidden>
				<s:hidden name="lPlanId"></s:hidden>
				<s:hidden name="weekdaysValue"></s:hidden>
				<div style="float: left;">
					<%
						String schedulePeriod = (String) request.getAttribute("trainingSchedulePeriod");
								List<List<String>> alSessionData = (List<List<String>>) request.getAttribute("alSessionData");
								String alSessionDataSize = "0";
								if (alSessionData != null) {
									alSessionDataSize = "" + alSessionData.size();
								}
					%>
					<input type="hidden" name="alSessionDataSize" id="alSessionDataSize" value="<%=alSessionDataSize%>" />
					<table border="0" class="table  table_no_border">
						<tbody  id="trainingScheduleTableId">
						<tr>
							<td class="tdLabelheadingBg" colspan="2">
							<span style="color: #68AC3B; font-size: 18px; padding: 5px;"> Step 3: </span>
							 Training Schedule Information</td>
						</tr>
				
						<tr>
                            <td class="txtlabel" style="vertical-align: top; text-align: right">Periodic:</td>
                            <td>
                            <div style="position:reletive;">
                           <span style="float: left; margin-right: 20px">
                           <s:select theme="simple" cssClass=" form-control" id="trainingSchedulePeriod" name="trainingSchedulePeriod" headerKey="1" 
									headerValue="One Time" list="#{ '2':'Weekly','3':'Monthly'}" onchange="checkPeriod(this.value);"/> 
                           </span>                           
                            </div>
                            </td>
                            <td>
                            <div style="position:reletive;">
                            <span id="weekly" style="display: none; float: left;"><%-- Day:<sup>*</sup> --%>
                                    <s:select cssClass=" validateRequired form-control" theme="simple" name="weekday" id="weekday" headerKey="" cssStyle="width:115px;" headerValue="Select Day" value="weekdayValue"
                                        list="#{'Monday':'Monday','Tuesday':'Tuesday', 'Wednesday':'Wednesday','Thursday':'Thursday','Friday':'Friday','Saturday':'Saturday','Sunday':'Sunday'}" />
                            </span>
                            
                            <span id="monthly" style="display: none; float: left;"> 
                                <s:select theme="simple" cssClass=" validateRequired form-control" name="day" id="day" headerKey="" cssStyle="width:75px;" headerValue="Day" value="dayValue"
                                        list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
                                        '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
                                        '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
                            </span>
                            
                            </div>
                            </td>
                        </tr> 

						<tr>
							<td class="txtlabel" style="text-align: right">Start Date:<sup>*</sup></td>
							<td><s:textfield name="startdateTraining" id="startdateTraining" cssClass="validateRequired form-control "/>
							</td> 
						</tr>

						<tr>
							<td class="txtlabel" style="text-align: right">End Date:<sup>*</sup></td>
							<td><s:textfield name="enddateTraining" id="enddateTraining" cssClass="validateRequired form-control "/>
							</td>
						</tr>

						<tr>
							<td style="text-align: right;" class="txtlabel" id="dayScheduleLabelTD" style="display: table-cell;">Day Schedule</td>
							<td id="dayScheduleTD" style="display: table-cell;">
							<s:radio list="#{'1':'Daily','2':'Occasionally'}" value="{scheduleTypeValue}" name="scheduleType" id="scheduleType" onclick="changeScheduleType(this.value);"></s:radio></td>
						</tr>
						
						<%
							if (alSessionData != null && alSessionData.size() != 0) {

								String scheduleType = (String) request.getAttribute("scheduleTypeValue");
								String trainingSchedulePeriod = (String) request.getAttribute("trainingSchedulePeriod");
						%>
						<tr id="weekDaysTR" <%if (scheduleType != null && scheduleType.equals("1") && trainingSchedulePeriod != null && trainingSchedulePeriod.equals("1")) { %>
							style="display: table-row;" <%} else { %> style="display: none;"
							<%}%>>
							<td style="text-align: right;" class="txtlabel">
							<input type="hidden" name="hideScheduleType" id="hideScheduleType" value="<%=scheduleType %>"/>
							&nbsp;</td>
							<td>
							
							<s:checkboxlist name="weekDays" id="weekDays" list="#{'Mon':'Mon','Tue':'Tue','Wed':'Wed','Thu':'Thu','Fri':'Fri','Sat':'Sat','Sun':'Sun'}" value="weekdaysValue" />
							</td>
						</tr>
						
						<%	for (int i = 0; i < alSessionData.size(); i++) {
								List<String> alInner = (List<String>) alSessionData.get(i);
							//System.out.println("alInner.get(4) ===> "+alInner.get(4));
						%>
							<tr id="trainingSchedulePeriodTR<%=i%>"
							<% if (alInner.get(0) != null && alInner.get(0).equals("1") && alInner.get(4) != null && alInner.get(4).equals("2")) { %>
								style="display: table-row;" <%} else { %> style="display: none;" <%}%>>
								<td class="txtlabel" style="text-align: right">
									<input type="hidden" name="hideScheduleType" id="hideScheduleType" value="<%=alInner.get(4) %>"/>
									<span style="float: left;margin-left: 50px;">Day <%=i+1 %></span> Select Date:<sup>*</sup> 
								</td>
								<td colspan="2"><input type="text" id="oneTimeDate<%=i%>" name="oneTimeDate" class="validateRequired form-control" value="<%=alInner.get(1)%>" onchange="checkDates(this.value,'oneTimeDate<%=i%>','<%=i%>');" /></td>
						</tr>

						<tr id="startTimeTR<%=i%>"> 
							<td class="txtlabel" style="text-align: right">Start Time:<sup>*</sup></td>
							<td colspan="2"><input type="text" id="startTime<%=i%>" name="startTime" class="validateRequired  form-control" value="<%=alInner.get(2)%>" />
							</td>

						</tr>

						<tr id="endTimeTR<%=i%>"> 
							<td class="txtlabel" style="text-align: right">End Time:<sup>*</sup></td>
							<td><input type="text" id="endTime<%=i%>" name="endTime" class="validateRequired  form-control" value="<%=alInner.get(3)%>" />
							</td> 
							<td id="addSessionTD<%=i%>" <%if (alInner.get(0) != null && alInner.get(0).equals("1") && alInner.get(4) != null && alInner.get(4).equals("2")) { %>
							style="display: table-cell; float: left;" <%} else { %> style="display: none; float: left; " <%}%>>
							<!-- style="display: none; float: left; padding: 10px;"> -->
							<a href="javascript:void(0)" onclick="AddSessionAjax();" class="add" title="Add Another Session">Add Another Session</a>
								<%
									if (i > 0) {
								%> <a href="javascript:void(0)" onclick="DeleteSessionAjax('startTimeTR<%=i%>','endTimeTR<%=i%>','trainingSchedulePeriodTR<%=i%>')" class="remove" title="Remove Session">Remove</a> 
								<%
								 	}
								 %>
							</td>

						</tr>
						<tr>
							<td colspan="3"><hr />
							</td>

						</tr>
						<% }
						} else {
						%>
							
							<tr id="weekDaysTR" style="display: table-row">
								<td style="text-align: right;" class="txtlabel"><input type="hidden" name="hideScheduleType" id="hideScheduleType" value="1"/></td>
								<td>
								
								<s:checkboxlist name="weekDays" list="#{'Mon':'Mon','Tue':'Tue','Wed':'Wed','Thu':'Thu','Fri':'Fri','Sat':'Sat','Sun':'Sun'}" value="weekdaysValue"/>
								</td>
							</tr>
						
							<tr id="trainingSchedulePeriodTR0" style="display: none;">
								<td class="txtlabel" style="vertical-align: top; text-align: right"><span style="float: left;margin-left: 50px;">Day 1&nbsp;</span>Select Date:</td>
								<td><s:textfield name="oneTimeDate" id="oneTimeDate0" cssClass="validateRequired form-control" required="true"  onchange="checkDates(this.value,'oneTimeDate0','0')"></s:textfield>
								</td>
							</tr>

							<tr id="startTimeTR0">
								<td class="txtlabel" style="vertical-align: top; text-align: right">Start Time:</td>
								<td><s:textfield name="startTime" id="startTime0" cssClass="validateRequired form-control " required="true"></s:textfield>
								</td>
							</tr>

							<tr id="endTimeTR0">
								<td class="txtlabel" style="vertical-align: top; text-align: right">End Time:</td>
								<td><s:textfield name="endTime" id="endTime0" cssClass="validateRequired form-control " required="true"></s:textfield></td>
	
								<td id="addSessionTD0" style="display: none; float: left;"><a href="javascript:void(0)" onclick="AddSessionAjax();" class="add" title="Add Another Session">Add Another Session</a></td>
							</tr>
						<% }%>
						</tbody>
					</table>
				</div>
				<div style="width: 100%; float: right;">
					<%
					String planId = (String) request.getAttribute("lPlanId");
					String frmpage = (String) request.getAttribute("frmpage");
					if(frmpage != null && frmpage.equals("LPlan")) {
					%>
					<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" value="Save & Go To Learning Plan" name="stepSaveGoBack"/>
					<% } %>
					<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" value="Submit & Proceed" name="stepSubmit"/>
					<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save & Exit"/>
				</div>

			</s:form>

		</s:if>
		
		<s:if test="step==4 || mode=='report'">
			<script type="text/javascript">
								
				function showLongDescription(val) {
					var status = document.getElementById("status"+val).value;
					if(status == '0') {
						document.getElementById("longDesSpan"+val).style.display="block";
						document.getElementById("status"+val).value = "1";
					} else {
						document.getElementById("longDesSpan"+val).style.display="none";
						document.getElementById("status"+val).value = "0";
					}
				}
				
			</script>
			<%
			List<List<String>> daysList = (List<List<String>>)request.getAttribute("daysList");
			Map<String, String> hmdaysDate = (Map<String, String>) request.getAttribute("hmdaysDate");
			String scheduleId = (String) request.getAttribute("scheduleId");
			Map<String, String> hmDayDescription = (Map<String, String>) request.getAttribute("hmDayDescription");
			
			%>
			<s:form theme="simple" name="frmAddTrainingPlan4" id="frmAddTrainingPlan4" action="AddTrainingPlan" method="POST" cssClass="formcss" enctype="multipart/form-data">
				<s:hidden name="planId" id="hiddenplanId"></s:hidden>
				<s:hidden name="step"></s:hidden>
				<s:hidden name="operation"></s:hidden>
				<s:hidden name="ID"></s:hidden>
				<s:hidden name="alignedwith" id="hiddenalignedwith"></s:hidden>
				<s:hidden name="trainingType"></s:hidden>
				<input type="hidden" name="scheduleId" id="scheduleId" value="<%=scheduleId %>"/>

				<div style="float: left; width: 100%">
					<table id="tableID" class="table autoWidth table_no_border">

						<tr><td class="tdLabelheadingBg " colspan="3">
							<span style="color: #68AC3B; font-size: 18px; padding: 5px;"> Step  4: </span>Plan
						</td> </tr>
						
					<% 
					for(int i=0; daysList != null && !daysList.isEmpty() && i< daysList.size(); i++) {
						%>
						<tr><td class="txtlabel" align="right" width="150px;" valign="top">Day <%=i+1 %>:</td>
						<td class="txtlabel" align="center" width="150px;" valign="top">
						<input type="hidden" name="dayDate" id="dayDate" value="<%=daysList.get(i) %>"/>
						<%=hmdaysDate.get(daysList.get(i)) %>
						</td>
						<td valign="top">
						<input type="text" class=" form-control " name="daydescription" id="daydescription" value="<%=uF.showData(hmDayDescription.get(scheduleId+"_"+daysList.get(i)+"_S"),"") %>">
						<%-- <s:textfield name="daydescription" id="daydescription" /> --%>
						</td>
						<td valign="top">
						<% 	String strDisplayLD = "none";
							if(hmDayDescription != null && hmDayDescription.get(scheduleId+"_"+daysList.get(i)+"_L")!= null && !hmDayDescription.get(scheduleId+"_"+daysList.get(i)+"_L").isEmpty()){
								strDisplayLD = "block";
							}
							
						%>
						<span style="float: left;"><a href="javascript:void(0);" onclick="showLongDescription('<%=i %>');">LD</a></span>
						<input type="hidden" name="status" id="status<%=i %>" value="0">
						<span id="longDesSpan<%=i %>" style="display: <%=strDisplayLD %>; float: left; margin-left: 10px;"> <textarea rows="2" cols="75" name="longdescription" id="longdescription" class=" form-control "><%=uF.showData(hmDayDescription.get(scheduleId+"_"+daysList.get(i)+"_L"),"") %></textarea></span>
						</td>
						</tr>
						
						<% } %>
					</table>
					
				</div>

				<div style="width: 100%; float: right;">
					<s:submit cssClass="btn btn-primary" cssStyle="width:160px; float:right;" value="Submit & Proceed" name="stepSubmit"/>
				</div>

			</s:form>
		</s:if>
		
		
		<s:if test="step==5 || mode=='report'">
			<script type="text/javascript">

				
				
				function openEditFeedbackQue(ID,queID,queno,operation,step,queAnstype,trainingType) {
					var dialogEdit = '.modal-body';
					$(dialogEdit).empty();
					$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
					$("#modalInfo").show();
					$(".modal-title").html('Edit Question');
					$.ajax({
						url : "getTrainingFeedbackQuestion.action?ID=" + ID + "&queno=" + queno + "&queID=" + queID + "&operation=" + operation + "&step=" + step + 
								"&queAnstype=" + queAnstype+"&trainingType=" + trainingType,
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
				}

			  function changeStatus(id) {
			    	//alert("id==>"+id);
					if (document.getElementById('addFlag' + id).checked == true) {
						document.getElementById('status' + id).value = '1';
					} else {
						document.getElementById('status' + id).value = '0';
					}
				}

				function closeEditDiv(){
					$(dialogEdit2).dialog('close');
				}
				
				var questionCnt = 0;

				var cxtpath = '<%=request.getContextPath()%>';
				var anstype = '<%=(String) request.getAttribute("anstype")%>';
				
				function getQuestionForEdit(oldcnt, callFrom) {
					//alert(anstype);
					  var totweight=0;
						  oldcnt = questionCnt;
						
						questionCnt++;
						var cnt=questionCnt;
						var ultag = document.createElement('ul');
						var aa = getQuestoinContentType(cnt,callFrom);
						
						ultag.id = "questionUl"+cnt;
						 var a = "<li><table class=\"table sectionfont\" width=\"100%\">"
								+ "<tr><th>"+questionCnt+")</th><th width=\"17%\" style=\"text-align: right;\">Add Question:<sup>*</sup></th>"
								+ "<td colspan=\"3\"><span id=\"newquespan"+cnt+"\" style=\"float: left; \"><input type=\"hidden\" name=\"hidequeid\" id=\"hidequeid"+cnt+"\" value=\"0\"/>"
								+"<textarea rows=\"2\" name=\"question\" id=\"question"+cnt+"\" class=\"validateRequired form-control\"  style=\"width: 330px;\"></textarea>"
								+"</span>"

								+ "&nbsp;<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt\" value=\""+cnt+"\"/>"
								+"</span>&nbsp;&nbsp;"
								+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"','"+callFrom+"');\" > +Q </a></span>&nbsp;"
								+"<span id=\"checkboxspan"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag\" type=\"checkbox\" id=\"addFlag"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+ cnt+ "')\" />"
								+"<input type=\"hidden\" id=\"status"+cnt+"\" name=\"status\" value=\"0\"/></span>"
								
								//+"<a href=\"javascript:void(0)\"  title=\"Add New Question\" onclick=\"getQuestion('"+cnt+"','"+callFrom+"')\" ></a>&nbsp;&nbsp; "
								//+"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Question\" onclick=\"removeQuestion('questionUl"+cnt+"')\"/>"
								+"<input type=\"hidden\" name=\"questiontypename\" value=\""+ cnt+"\" /></td></tr>" //+othrQtype
								+"<tr><th></th><th style=\"text-align: right;\">Select Answer Type:</th><td><select name=\"ansType\" id=\"ansType"+cnt+"\" onchange=\"showAnswerTypeDiv(this.value, '"+cnt+"', 'answerType"+cnt+"', 'answerType1"+cnt+"');\" class=\"form-control\"> <option value=\"\">Select</option>" +anstype+"</select>"
								+"</td><td><div id=\"anstypediv"+cnt+"\"><div id=\"anstype9\">"
								+"a) Option1&nbsp;<input type=\"checkbox\" value=\"a\" name=\"correct\" disabled=\"disabled\"/> b) Option2&nbsp;<input type=\"checkbox\" name=\"correct\" value=\"b\" disabled=\"disabled\"/><br />"
								+"c) Option3&nbsp;<input type=\"checkbox\" value=\"c\" name=\"correct\" disabled=\"disabled\"/> d) Option4&nbsp;<input type=\"checkbox\" name=\"correct\" value=\"d\" disabled=\"disabled\"/><br />"
								+"</div></div></td></tr>"
								+aa
								+"<tr><th></th><th class=\"alignRight\">Questions For:</th><td colspan=\"2\"><input type=\"radio\" name=\"question_for"+cnt+"\" value=\"1\" checked=\"checked\">Trainer/Content &nbsp;<input type=\"radio\" name=\"question_for"+cnt+"\" value=\"2\">Participant</td></tr>"
								+"</table>"
								+"<div style=\"width: 100%; text-align: center;\"><input type=\"button\" class=\"btn btn-primary\" name=\"add\" value=\"Add\" onclick=\"getQuestionForEdit('"+cnt+"','"+callFrom+"');\" />"
								+"&nbsp;<input type=\"button\" value=\"Cancel\" class=\"btn btn-danger\" name=\"cancel\" onclick=\"removeQuestion('questionUl"+cnt+"');\"/></div>"
								+"</li>";

							ultag.innerHTML = a;
							document.getElementById("questionLi").appendChild(ultag);
						
					}


				function removeQuestion(id){
					var row_skill = document.getElementById(id);
					if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
						row_skill.parentNode.removeChild(row_skill);
					}
				}
				
				function getQuestoinContentType(cnt,callFrom){
					var val = 9;
					
					var a="";
					if( val == 8){
						a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\" form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\" validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
						+ "<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\" form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\" validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
					
					}else if (val == 1 || val == 2 || val == 9) {
						a="<tr id=\"answerType"+cnt+"\"><th></th><th></th><td>a)&nbsp;<span id=\"aspan\"><input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" class=\" validateRequired form-control \"/></span> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<span id=\"bspan\"><input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" class=\" validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td></tr>"
						+"<tr id=\"answerType1"+cnt+"\"><th></th><th></th><td>c)&nbsp;<span id=\"cspan\"><input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" class=\" validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<span id=\"dspan\"><input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" class=\" validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td></tr>";
					
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
				
				var dialogEdit = '#SelectQueDiv';
				function openQuestionBank(count,callFrom) {

						var ansType = document.getElementById('ansType'+count).value;
						//var dialogEdit = '#SelectQueDiv';
						var dialogEdit = '#modal-body1';
						$(dialogEdit).empty();
						$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
						$("#modalInfo1").show();
						$('.modal-title1').html('Question Bank');
						$.ajax({
							url : "SelectTrainingFeedbackQuestion.action?count="+count+"&ansType="+ansType+"&callFrom="+callFrom,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
				}


				function setQuestionInTextfield(callFrom) {
					var queid = document.getElementById("questionSelect").value;
					var count = document.getElementById("count").value;
					xmlhttp = GetXmlHttpObject();
					if (xmlhttp == null) {
					        alert("Browser does not support HTTP Request");
					        return;
					} else {
					        var xhr = $.ajax({
					                url : "SetTrainingFeedbackQueToTextfield.action?queid=" + queid + '&count=' +count,
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
					                	}
					                }
					        });
					}
					$(dialogEdit).dialog('close');
					
					if(callFrom != ""  && callFrom === "E") {
						$("#modalInfo1").hide();
					} else {
						$("#modalInfo").hide();
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
				
				function openNewQue(value) {
					 
				    document.getElementById("newquediv"+value).style.display="block";
				    document.getElementById("savebtndiv"+value).style.display="block";
				}

				function closeDiv(btndiv,quediv){
					document.getElementById(quediv).style.display="none";
					document.getElementById(btndiv).style.display="none";
				}
				
				function showAnswerTypeDiv(ansType, cnt, id, id1) {
					var action = 'ShowAnswerType.action?ansType=' + ansType;
					getContent("anstypediv"+cnt, action);
					changeNewAnswerType(ansType, cnt, id, id1);
				}

				function changeStatus(id) {
					if (document.getElementById('addFlag' + id).checked == true) {
						document.getElementById('status' + id).value = '1';
					} else {
						document.getElementById('status' + id).value = '0';
					}
				}

				function changeNewAnswerType(val, cnt, id, id1) {

				 if (val == 1 || val == 2 || val == 8) {
					addQuestionType1(id,cnt);
					document.getElementById(id).style.display = 'table-row';

					addQuestionType2(id1,cnt);
					document.getElementById(id1).style.display = 'table-row';
				} else if (val == 9) {
					addQuestionType3(id,cnt);
					document.getElementById(id).style.display = 'table-row';

					addQuestionType4(id1,cnt);
					document.getElementById(id1).style.display = 'table-row';

				 }else if (val == 6) {
					addTrueFalseType(id,cnt);
					document.getElementById(id).style.display = 'table-row';
					document.getElementById(id1).innerHTML ="";
					document.getElementById(id1).style.display = 'none';

				}else if (val == 5) {
					addYesNoType(id,cnt);
					document.getElementById(id).style.display = 'table-row';
					document.getElementById(id1).innerHTML ="";
					document.getElementById(id1).style.display = 'none';

				} else {
					addQuestionType1(id,cnt);
					addQuestionType2(id1,cnt);
					document.getElementById(id).style.display = 'none';
					document.getElementById(id1).style.display = 'none';
				}

				}


				function addTrueFalseType(id,cnt){
					document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"2\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True"
					+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
				}

				function addYesNoType(id,cnt){
					document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"2\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes"
					+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
				} 
				function addQuestionType1(id,cnt) {
					document.getElementById(id).innerHTML = "<th></th><th></th><td>a)<input type=\"text\" name=\"optiona\" id = \"optiona\" class=\" validateRequired form-control\" /> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb\" id=\"optionb\" class=\" validateRequired form-control\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
				}
				function addQuestionType2(id1,cnt) {
					document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)<input type=\"text\" name=\"optionc\" id = \"optionc\" class=\" validateRequired form-control\"  /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond\" id=\"optiond\" class=\" validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
				}
				function addQuestionType3(id,cnt) {
					document.getElementById(id).innerHTML = "<th></th><th></th><td>a)<input type=\"text\" name=\"optiona\" id = \"optiona\" class=\" validateRequired form-control\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb\" id = \"optionb\" class=\" validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
				}
				function addQuestionType4(id1,cnt) {
					document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)<input type=\"text\" name=\"optionc\"  id = \"optionc\"class=\" validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\" /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond\" id = \"optiond\" class=\" validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
				} 
			
			</script>	
		<%
		List<List<String>> learnerFeedbackQueList =(List<List<String>>)request.getAttribute("learnerFeedbackQueList");
		List<List<String>> trainerFeedbackQueList =(List<List<String>>)request.getAttribute("trainerFeedbackQueList");
		%>
			<div style="float: left; width:100%;">
				<table border="0" class="table" style="width: 100%;">
					<tr>
						<td class="tdLabelheadingBg " colspan="2">
						<span style="color: #68AC3B; font-size: 18px; padding: 5px;">Step 5: 
						</span> Training Feedback</td>
					</tr>
						
					<tr>
						<td>
							<div class="box box-default collapsed-box" style="margin-top: 10px;">
                
				                <div class="box-header with-border">
				                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Learners Feedback</h3>
				                    <div class="box-tools pull-right">
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				                    <table class="table" cellpadding="0" cellspacing="0" width="100%"
												align="center">
												<tr>
													<th width="60%">Questions</th>
													<!-- <th width="10%">Weightage</th> -->
													<!-- <th width="20%">Questions For</th> -->
												</tr>
												<%
												int newquecntL = learnerFeedbackQueList != null ? learnerFeedbackQueList.size()+1 : 1 ;
												for(int i=0; learnerFeedbackQueList != null && !learnerFeedbackQueList.isEmpty() && i<learnerFeedbackQueList.size();i++){ 
													List<String> innerList = learnerFeedbackQueList.get(i);
												%>
												<tr>
													<td>
													<span style="float: left;"><%=i+1 %>)&nbsp;<%=innerList.get(2) %></span>
													<span style="float: left; margin-left: 10px;"><a id="editexist<%=i%>" href="javascript:void(0)" class="edit_lvl" onclick="openEditFeedbackQue('<%=innerList.get(4) %>','<%=innerList.get(1) %>','<%=i+1 %>','E','4','<%=innerList.get(3) %>','<%=request.getAttribute("trainingType") %>'); " title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
													<a class="del" style="color:rgb(221, 0, 0);" title="Delete" onclick="deleteQuestion('<%=innerList.get(4)%>','<%=innerList.get(1) %>','E','4','<%=request.getAttribute("trainingType")%>')" href="javascript:void(0)" ><i class="fa fa-trash" aria-hidden="true"></i></a></span>
													</td>
												</tr>
												<%} %>
												
											</table>
				                </div>
				                <!-- /.box-body -->
				            </div>
						</td>
					 </tr>
						
					<tr>
						<td>
							<div class="box box-default collapsed-box" style="margin-top: 10px;">
				                
				                <div class="box-header with-border">
				                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Trainers Feedback</h3>
				                    <div class="box-tools pull-right">
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				                    <table class="table" cellpadding="0" cellspacing="0" width="100%" align="center">
												<tr><th width="60%">Questions</th></tr>
												<%
												int newquecntT = trainerFeedbackQueList != null ? trainerFeedbackQueList.size()+1 : 1 ;
												for(int i=0;trainerFeedbackQueList != null && !trainerFeedbackQueList.isEmpty() && i<trainerFeedbackQueList.size();i++) { 
													List<String> innerList=trainerFeedbackQueList.get(i);
												%>
													<tr>
														<td>
														<span style="float: left;"><%=i+1 %>)&nbsp;<%=innerList.get(2) %></span>
														<span style="float: left; margin-left: 10px;"><a id="editexist<%=i%>" href="javascript:void(0)" class="edit_lvl" onclick="openEditFeedbackQue('<%=innerList.get(4) %>','<%=innerList.get(1) %>','<%=i+1 %>','E','4','<%=innerList.get(3) %>','<%=request.getAttribute("trainingType") %>'); " title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
														<a class="del" style="color:rgb(221, 0, 0);" title="Delete" onclick="deleteQuestion('<%=innerList.get(4)%>','<%=innerList.get(1) %>','E','4','<%=request.getAttribute("trainingType") %>')" href="javascript:void(0)" ><i class="fa fa-trash" aria-hidden="true"></i></a></span>
														</td>
													</tr>
												<%} %>
										 </table>
				                </div>
				                <!-- /.box-body -->
				            </div>
						</td>
					</tr>
				</table>
				
		<%if(op != null && op.equals("E")) { %>
				
					<s:form theme="simple" action="AddTrainingPlan" name="frmAddTrainingPlan" id="frmAddTrainingPlan" method="POST" cssClass="formcss" enctype="multipart/form-data">
						<s:hidden name="planId" id="hiddenplanId"></s:hidden>
						<s:hidden name="operation"></s:hidden>
						<s:hidden name="ID"></s:hidden>
						<s:hidden name="step"></s:hidden>
						<s:hidden name="alignedwith"></s:hidden>
						<s:hidden name="trainingType"></s:hidden>
						<div>
							<ul class="level_list ul_class"><li style="margin-left: 0px;"><a href="javascript:void(0)"  onclick="getQuestionForEdit('0','');"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Question</a></li>
							<li id="questionLi" style="margin-left: -65px; border: 0px;"></li></ul>
						</div>
					
						<div style="width: 100%; float: right;">
							<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSubmit" value="Submit & Proceed"/>
							<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save & Exit"/>
						</div>
				  </s:form>
			<% } else { %>
				 <s:form theme="simple" action="AddTrainingPlan" name="frmAddTrainingPlan" id="frmAddTrainingPlan" method="POST" cssClass="formcss" enctype="multipart/form-data">
					<s:hidden name="planId" id="hiddenplanId"></s:hidden>
					<s:hidden name="operation"></s:hidden>
					<s:hidden name="ID"></s:hidden>
					<s:hidden name="step"></s:hidden>
					<s:hidden name="alignedwith"></s:hidden>
					<s:hidden name="trainingType"></s:hidden>
					<div>
						<ul class="level_list ul_class"><li style="margin-left: 0px;"><a href="javascript:void(0)"  onclick="getQuestionForEdit('0','');"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Question</a></li>
						<li id="questionLi" style="margin-left: -65px; border: 0px;"></li></ul>
					</div>
					<div style="width: 100%; float: right;">
						<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSubmit" value="Submit & Proceed"/>
						<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="stepSave" value="Save & Exit"/>
					</div>
				</s:form>
		    <% } %>
		  </div>
		</s:if>
	</div>
</div>
                </div>
            </div>
		 </section>
	</div>
</section>

<%
	for (int i = 0; trainerOuterList != null && i < trainerOuterList.size(); i++) {
			List<String> innerList = trainerOuterList.get(i);
			String trainerID = innerList.get(0);
			String trainerTYPE = innerList.get(1);
			if(trainerTYPE != null && trainerTYPE.equals("EXTrainer")) {
	%>
				<div id="TrainerProfilePopup<%=trainerID %>"></div>
		<%  } 
	} %> 


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
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
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1">Select Question</h4>
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
	
	$(function(){
		$("body").on('click','#closeButton',function(){
			$(".modal-body").height(400);
			$(".modal-dialog").removeAttr('style');
			$("#modalInfo").hide();
	    });
		$("body").on('click','.close',function(){
			$(".modal-body").height(400);
			$(".modal-dialog").removeAttr('style');
			$("#modalInfo").hide();
		});
		$("#strAttribute").multiselect({
			noneSelectedText: 'Select Something (required)'
		}).multiselectfilter();
		$("#frmAddTrainingPlan1_plan_idwlocation").multiselect({
			noneSelectedText: 'Select Something (required)'
		}).multiselectfilter();
		$("input[name='oneTimeDate']").datepicker({format: 'dd/mm/yyyy'});  
		
		
		var date_yest = new Date();
	    var date_tom = new Date();
	    date_yest.setHours(0,0,0);
	    date_tom.setHours(23,59,59); 
	   
		$("input[name='startTime']").datetimepicker({
			format: 'HH:mm',
			minDate: date_yest
	    }).on('dp.change', function(e){ 
	    	$("input[name='endTime']").data("DateTimePicker").minDate(e.date);
	    });
		
		$("input[name='endTime']").datetimepicker({
			format: 'HH:mm',
			maxDate: date_tom
	    }).on('dp.change', function(e){ 
	    	$("input[name='startTime']").data("DateTimePicker").maxDate(e.date);
	    });
		
		$("input[name='startdateTraining']").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $("input[name='enddateTraining']").datepicker('setStartDate', minDate);
        });
        
        $("input[name='enddateTraining']").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $("input[name='startdateTraining']").datepicker('setEndDate', minDate);
        });
	});
	
	   
$('input[name="stepSubmit"]').click(function(){
	//$(".validateRequired").prop('required',true);
	$("#"+this.form.id).find('.validateRequired').filter(':hidden').prop('required',false);
	$("#"+this.form.id).find('.validateRequired').filter(':visible').prop('required',true);
});
$('input[name="stepSave"]').click(function(){
	//$(".validateRequired").prop('required',true);
	$("#"+this.form.id).find('.validateRequired').filter(':hidden').prop('required',false);
	$("#"+this.form.id).find('.validateRequired').filter(':visible').prop('required',true);
});

$("form").bind('submit',function(event) {
	  event.preventDefault();
	  var op = '<%=op%>';
  	  var form_data = $("#"+this.id).serialize();
  	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  	  var stepSave = $('input[name=stepSave]').val();
  	  var stepSubmit = $('input[name=stepSubmit]').val();
  	  $.ajax({
    		url: "AddTrainingPlan.action",
    		type: 'POST',
    		data: form_data+"&stepSubmit"+stepSubmit+"&stepSave="+stepSave,
    		success: function(result) {
    			$("#divResult").html(result);
    	    },
			error: function(result) {
				getLearningDashboardData('TrainingPlanInfo','LD');
			}
     });
    
	});
	
function loadStepOnClick(action){
	 
	  $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	  $.ajax({
  		url: action,
  		type: 'GET',
  		success: function(result){
  			$("#divResult").html(result);
  			
  	    }
    });
}

	function deleteQuestion(id,queId,op,step,trainingType){
		//alert("id=>"+id+"==>queId=>"+queId+"=>op=>"+op+"=>step=>"+step+"=>trainingType=>"+trainingType);
		if(confirm('Are you sure you want to delete this Question?')) {
			$.ajax({
				type:'GET',
				url:'DeleteTrainingFeedbackQuestion.action?ID='+id+'&queID='+queId+'&operation='+op+'&step='+step+'&trainingType='+trainingType,
				cache:true,
				success:function(result){
				//	alert("result==>"+result);
					$("#divResult").html(result);
				}
				
			});
		}
	}
	

</script>