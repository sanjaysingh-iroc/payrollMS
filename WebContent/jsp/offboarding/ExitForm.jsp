<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<script type="text/javascript" src="scripts/customAjax.js"></script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript"
	src="scripts/_rating/js/jquery.raty.min.js"></script>
<%-- <script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script> --%>
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
});

var cnt = 2;

function changestatus(id) {
	document.getElementById(id).value = '1';
}

function addRow(tableID) {

	var table = document.getElementById(tableID);

	var rowCount = table.rows.length;
	var row = table.insertRow(rowCount);
	row.id = cnt;
	row.setAttribute("style", "height:40px;");
	var cell1 = row.insertCell(0);
	cell1.setAttribute("align", "center");
	var element1 = document.createElement("input");
	element1.type = "text";
	element1.name = "documentname";
	element1.required = true;//Created by Dattatray Date : 03-July-21
	cell1.appendChild(element1);

	var divTag = document.createElement("div");
	divTag.id = "milestone" + cnt;
	divTag.setAttribute("style", "width:100%;");
	divTag.innerHTML = "<table style=\"width:100%;\"><tr>"
			+ "<td><input type=\"file\" name=\"document\" style=\"width:270px\" onchange=\"changestatus('status"+ cnt+ "')\" /> "
			+ "<input type=\"hidden\" name=\"status\" id=\"status"+cnt+"\" value=\"0\" /> "
			+ "</td>" +

			"<td width=\"100px\"><a href=\"javascript:void(0)\" onclick=\"addRow('"+ tableID + "')\" class=\"add-font\" title=\"Add\"></a>"
			+ "<a href=\"javascript:void(0)\" style=\"float: left;\" onclick=\"deleteRow1('"+ tableID + "'," + cnt + ")\" class=\"remove-font\"></a></td></tr>";

	var cell3 = row.insertCell(1);
	var element2 = document.createElement("input");
	element2.type = "file";
	cell3.appendChild(divTag);

	cnt++;

}

function deleteRow1(tableID, rowno) {
	try {
		var table = document.getElementById(tableID);
		var row = document.getElementById(rowno);
		row.parentNode.removeChild(row);

	} catch (e) {
		alert(e);
	}
}
	
function savecomment(actiontype) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Add Or Update Comment');
	 $.ajax({
			url : actiontype,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}
	 
	 
 function salaryPreview(id,paycycle) {

	 var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Salary Preview');
	 $.ajax({
			url : "OffboardSalaryPreview.action?emp_id="+id+"&payCycle="+paycycle,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	 
	 
 function fullSalaryPreview(id,resignId) {

	 var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Salary Preview');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url : "OffboardSalaryPreview.action?emp_id="+id+"&resignId="+resignId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	 
	 
function setTotalExemption(type,id,resignId){
	
	if(type==1){
		var val=document.getElementById("accruedAmt").value;
		var url="DeleteOffboardDocument.action?id="+id+"&amount="+val+"&resignId="+resignId+"&comment=ACCRUED&operation=I";
		getContent('accrued',url);
	}
	if(type==2){
		var val=document.getElementById("deductionAmt").value;
		var url="DeleteOffboardDocument.action?id="+id+"&amount="+val+"&resignId="+resignId+"&comment=DEDUCTION&operation=I";
		getContent('deduction',url);
	}
}
	
function approveFeedbackForm() {
	
	var id = document.getElementById("id").value;
	var resignId = document.getElementById("resignId").value;
	var feedbackformRemark = document.getElementById("feedbackformRemark").value;
	var feedbackFormRating = document.getElementById("feedbackFormRating").value;
	var section = ''+<%=IConstants.OFFBOARD_FEEDBACKFORM_SECTION%>;
	//alert("section ===>> " + section);
	//alert("resignId ===>> " + resignId);
	//alert("feedbackformRemark ===>> " + feedbackformRemark);
	//alert("feedbackFormRating ===>> " + feedbackFormRating);
	
	window.location = "DeleteOffboardDocument.action?id=" + id + "&resignId=" + resignId + "&element="+section+"&operation=E&feedbackFormRating=" + feedbackFormRating 
			+ "&feedbackformRemark=" + feedbackformRemark;
	//window.location = url;
}	
	
function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
      return false;
   }
   return true;
}

function feedBackForm(empId,resignId) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Feedback Form');
	 $.ajax({
			url : "ResigFeedbackForm.action?strEmpId=" + empId + "&resignId=" + resignId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); 
}

function feedbackFormSummary(empId,resignId) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Feedback Summary Form');
	 $.ajax({
			url : "ResigFeedbackFormPreview.action?strEmpId=" + empId + "&resignId=" + resignId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); 
}

function clearenceForm(empId,resignId) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Clearence Form');
	 $.ajax({
			url : "ResigClearenceForm.action?strEmpId=" + empId + "&resignId=" + resignId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}
function clearenceFormSummary(empId,resignId) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Clearence Summary Form');
	 $.ajax({
			url : "ResigClearenceFormPreview.action?strEmpId=" + empId + "&resignId=" + resignId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); 
}

function generatePdf(empId,resignId,type){
	window.location = 'ExitEmpPdf.action?strEmpId=' + empId + '&resignId=' + resignId + '&type=' + type;
}
</script>
<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Exit Form" name="title" />
</jsp:include> --%>



<%
	UtilityFunctions uF = new UtilityFunctions();
	/* EncryptionUtility eU = new EncryptionUtility(); */
	CommonFunctions CF=new CommonFunctions();
	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
	if (hmEmpProfile == null) {
		hmEmpProfile = new HashMap<String, String>();
	}
	String probationRemaining = (String) request.getAttribute("PROBATION_REMAINING");
	String noticePeriod = (String) request.getAttribute("NOTICE_PERIOD");

	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	List alSkills = (List) request.getAttribute("alSkills");

	Map<String, String> empMap = (Map<String, String>) request.getAttribute("empDetailsMp");

	Boolean isClose = (Boolean) request.getAttribute("userAccStatus");
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String id = (String) request.getAttribute("id");
	String resignId = (String) request.getAttribute("resignId");
	String from = (String) request.getAttribute("from");
	String feedbackFlag = (String) request.getAttribute("feedbackFlag");
	String clearanceFlag = (String) request.getAttribute("clearanceFlag");

	String resigAcceptedBy = (String) request.getAttribute("resigAcceptedBy");
	Map<String, String> hmApprovalStatus = (Map<String, String>) request.getAttribute("hmApprovalStatus");
	if (hmApprovalStatus == null)
		hmApprovalStatus = new HashMap<String, String>();
	String effectiveid = (String) request.getAttribute("effectiveid");
	String strReason = (String) request.getAttribute("strReason");
	//EncryptionUtils EU = new EncryptionUtils();//Created by Dattatray Date : 21-07-21 Note : Encryption
	
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
%>
<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			<div class="leftbox reportWidth">

				<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
				<div class="tableblock"
					style="padding: 5px; border: solid 1px #E7E7E7; width: 99%; margin-top: 10px;">
					<table width="100%" class="table table_no_border">
						<tr>
							<th colspan="4" align="left" style="padding-left: 20px"><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%>'s full and final report</th>
						</tr>

						<tr>
							<td valign="top" style="width: 30%;">
								<div class="box box-widget widget-user widget-user1">
									<!-- Add the bg color to the header using any of the bg-* classes -->
									<!-- <div class="widget-user-header bg-aqua-active"> -->
								<!-- ====start parvez on 27-10-2022===== -->
										<%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation + IConstants.I_PEOPLE + "/" + IConstants.I_IMAGE_COVER + "/" + hmEmpProfile.get("EMP_ID") + "/" + hmEmpProfile.get("COVER_IMAGE")%>'> --%>
										<%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
											List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
												//System.out.println("alPhotoInner=="+alPhotoInner+"--size=="+alPhotoInner.size());
										%>
											<div class="widget-user-header bg-aqua-active" style="height: 155px !important">
											<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation + IConstants.I_PEOPLE + "/" + IConstants.I_IMAGE_COVER + "/" + hmEmpProfile.get("EMP_ID") + "/" + hmEmpProfile.get("COVER_IMAGE")%>' style="height: auto !important">
										<% } else{ %>
											<div class="widget-user-header bg-aqua-active">
											<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation + IConstants.I_PEOPLE + "/" + IConstants.I_IMAGE_COVER + "/" + hmEmpProfile.get("EMP_ID") + "/" + hmEmpProfile.get("COVER_IMAGE")%>'>
										<% } %>
								<!-- ====end parvez on 27-10-2022===== -->		
										<h3 class="widget-user-username"
											style="font-size: 16px; color: #fff; font-weight: 600;">
											<span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span>
											<!-- Created By Dattatray Date:21-07-21 Note:empId encrypt -->
											<span style="float: right;"><a href="MyProfile.action?empId=<%=hmEmpProfile.get("EMP_ID") %>" title="Go to FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
										</h3>
										<h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
									</div>
									<div class="widget-user-image">
										<% if (docRetriveLocation == null) { %>
										<img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
										<% } else { %>
										<img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation + IConstants.I_PEOPLE + "/" + IConstants.I_IMAGE + "/" + (String) session.getAttribute(IConstants.EMPID) + "/" + IConstants.I_100x100 + "/" + hmEmpProfile.get("IMAGE")%>">
										<% } %>
									</div>
									<%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){%>
										<div class="box-footer" style="padding-top: 5Px;">
									<% } else{ %>
										<div class="box-footer">
									<% } %>
										<div class="row">
											<div class="col-sm-12">
												<div class="description-block">
													<h5 class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%>
														[<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>]
														[<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>]
													</h5>
													<span class="description-text"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%>
														[<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>]
														[<%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%>]</span>
													<p class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></p>
												</div>
												<!-- /.description-block -->
											</div>
										</div>
										<!-- /.row -->
									</div>
								</div></td>

							<td valign="top" style="width: 50%;">
								<div class="box box-default">
									<div class="box-body"
										style="padding: 5px; overflow-y: auto; padding: 6px;">
										<div class="trow">

											<table style="margin-left: 10px; margin-bottom: 0px;"
												class="table table_no_border autoWidth">
												<tr>
													<td>Employee Type:</td>
													<td class="textblue"><%=uF.showData((String) hmEmpProfile.get("EMP_TYPE"), "-")%></td>
												</tr>
												<tr>
													<td>Date of Joining:</td>
													<td class="textblue"><%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></td>
												</tr>
												<tr>
													<td>Probation Status:</td>
													<td class="textblue">
														<%
															if (empMap != null && empMap.size() > 0 && empMap.get("EMP_STATUS") != null && !((String) empMap.get("EMP_STATUS")).equalsIgnoreCase("PERMENENT")) {
																if (probationRemaining != null) {
																	if (uF.parseToInt(probationRemaining) > 0) {
														%> <%=probationRemaining + " days remaining."%>
														<% } else { %> 
															Probation completed. 
														<% } %>
														<% } else { %> 
															No probation. 
														<% } } else { %> 
															Probation completed. 
														<% } %>
													</td>
												</tr>
												<tr>
													<td>Notice Period:</td>
													<td class="textblue"><%=uF.showData(empMap.get("NOTICE_PERIOD"), "0")%> days</td>
												</tr>
												<tr>
													<td>&nbsp;</td>
													<td class="textblue"></td>
												</tr>
												<tr>
													<td>Total Experience:</td>
													<td class="textblue"><%=uF.showData((String) hmEmpProfile.get("TOTAL_EXP"), "-")%></td>
												</tr>
												<tr>
													<td>Exp with Current Org:</td>
													<td class="textblue"><%=uF.showData((String) request.getAttribute("TIME_DURATION"), "-")%></td>
												</tr>
												<tr>
													<td>Education Qualification:</td>
													<td class="textblue"><%=uF.showData((String) request.getAttribute("educationsName"), "-")%></td>
												</tr>
												<tr>
													<td>Skills:</td>
													<td class="textblue"><%=uF.showData((String) hmEmpProfile.get("SKILLS_NAME"), "-")%></td>
												</tr>
											</table>

											<% if (!uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS"))) { %>
											<div>
												<img src="images1/warning.png" />
											</div>
											<%
												}
											%>

										</div>
									</div>
									<!-- /.box-body -->
								</div></td>
						</tr>
						<tr>
							<td colspan="4">
								<div class="row row_without_margin"
									style="background-color: #fff; padding-top: 20px;">
									<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
										Last Day Date:&nbsp;<span class="label label-info"><%=uF.showData(empMap.get("LAST_DAY_DATE"), "")%></span>
									</div>
									<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
										Off Board Type:&nbsp;<span class="label label-danger"><%=uF.showData(empMap.get("OFF_BOARD_TYPE"), "")%></span>
									</div>
									<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
										Date of Resignation:&nbsp;<span class="label label-info"><%=uF.showData(empMap.get("ENTRY_DATE"), "")%></span>
									</div>
									<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
										Resignation Accepted by:&nbsp;<span
											class="label label-success"><%=uF.showData(empMap.get("ACCEPTED_BY"), "")%></span>
									</div>
								</div></td>
						</tr>
						<tr>
							<td colspan="4">
								<div class="row row_without_margin"
									style="background-color: #fff;">
									<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
										Resignation Reason:&nbsp;<span class="textblue"><%=uF.showData(empMap.get("EMP_RESIGN_REASON"), "")%></span>
									</div>
								</div></td>
						</tr>
						<tr>
							<td colspan="4">
								<div class="trow"
									style="background: #fff; margin: 0px; width: 100%;">
									<table class="table table_no_border"
										style="background-color: aliceblue;">
										<%
											if (hmApprovalStatus != null && hmApprovalStatus.size() > 0) {
												Set approvedSet = hmApprovalStatus.keySet();
												Iterator<String> it = approvedSet.iterator();
												while (it.hasNext()) {
													String workFlowId = it.next();
										%>
										<tr>
											<td colspan="8">
												<div style="margin-left: 20px; text-align: left;"><%=uF.showData(
							hmApprovalStatus.get(workFlowId.trim()), "")%></div>
											</td>
										</tr>
										<%
											}
											}
										%>
										<%
											if (strReason != null && !strReason.trim().equals("")
													&& !strReason.trim().equalsIgnoreCase("NULL")) {
										%>
										<tr>
											<td colspan="8"><div
													style="text-align: left; margin-left: 20px; margin-top: 10px;"><%=strReason%></div>
											</td>
										</tr>
										<%
											}
										%>
									</table>
								</div></td>
						</tr>
					</table>
				</div>
				<br />
				<div class="box box-primary collapsed-box"
					style="border-top-color: #FFFFFF;">
					<% Map<String, String> statusMp = (Map<String, String>) request.getAttribute("statusMp"); %>
					<div class="box-header with-border" style="<%if (uF.parseToBoolean(statusMp.get("1"))) { %> background-color:lightgreen; <% } else { %> background-color:orange; <% } %>">
						<h3 class="box-title">Feedback Form</h3>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div>
					<!-- /.box-header -->
					<div class="box-body"
						style="padding: 5px; overflow-y: auto; display: none;">
						<div id="profilecontainer">
							<div class="content1">
								<div class="holder">
									<%
										if (statusMp.get("1") != null && uF.parseToBoolean(statusMp.get("1"))) {
									%>
									<div class="msg savesuccess">
										<%=(((String) session.getAttribute(IConstants.USERTYPE)).equalsIgnoreCase(IConstants.EMPLOYEE)) ? "Your" : uF.showData((String) hmEmpProfile.get("NAME"), "-") + "'s"%>
										feedback form has been approved by <%=statusMp.get("1_APPROVED_BY")%>.
									</div>
									<% } %>
									<%
										if (strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) {
											if (uF.parseToBoolean(statusMp.get("1"))) {
									%>
									<span style="float: left;"> <a href="javascript: void(0);" onclick="feedbackFormSummary('<%=id%>','<%=resignId%>')" title="Feedback Form">View Feedback Form</a> 
										<a onclick="generatePdf('<%=id%>','<%=resignId%>','<%=IConstants.NODE_EXIT_FORM_ID%>');" href="javascript:void(0)" class="fa fa-file-pdf-o"> </a> </span>
									<table class="table table_no_border" cellpadding="0" cellspacing="0" width="99%" style="float: left;">
										<tr id="remarkTr">
											<td valign="top" align="right">Remark:</td>
											<td valign="top"><input type="hidden" id="feedbackFormRating" value="<%=statusMp.get("1_RATING")%>" name="feedbackFormRating" /> 
												<textarea rows="3" cols="50" name="feedbackformRemark" id="feedbackformRemark" style="width: 70%"><%=uF.showData(statusMp.get("1_REMARK"), "")%></textarea>
												<div id="starPrimaryfeedbackForm"></div> 
												<script type="text/javascript">
										        	$('#starPrimaryfeedbackForm').raty({
										        		readOnly: false,
										        		start: <%=statusMp.get("1_RATING") != null ? statusMp.get("1_RATING") : "0"%>,
										        		half: true,
										        		targetType: 'number',
										        		click: function(score, evt) {
										        			$('#feedbackFormRating').val(score);
																}
															});
												</script>
											</td>
										</tr>
									</table>
									<% } else { %>
									<span style="float: left;"><a href="javascript: void(0);" onclick="feedBackForm('<%=id%>','<%=resignId%>')" title="Feedback Form">Feedback Form</a> </span>
									<% }
										} else {
											if (uF.parseToBoolean(feedbackFlag)) {
									%>
									<span style="float: left;">
										<a href="javascript: void(0);" onclick="feedbackFormSummary('<%=id%>','<%=resignId%>')" title="Feedback Form">View Feedback Form</a> 
										<a onclick="generatePdf('<%=id%>','<%=resignId%>','<%=IConstants.NODE_EXIT_FORM_ID%>');" href="javascript:void(0)" class="fa fa-file-pdf-o"> </a> </span>
									<% } %>

									<% if (statusMp.get("1") == null || !uF.parseToBoolean(statusMp.get("1"))) { %>
									<s:form theme="simple" id="formAddNewRow" action="ExitForm" method="POST" cssClass="formcss">
										<s:hidden name="resignId" id="resignId"></s:hidden>
										<s:hidden name="operation" value="A"></s:hidden>
										<s:hidden name="id" id="id" value="%{id}"></s:hidden>
										<s:hidden name="appId"></s:hidden>
										<s:hidden name="from"></s:hidden>
										<%-- <s:submit cssClass="btn btn-primary" value="Save Feedback" /> --%>
										<table class="table table_no_border" cellpadding="0"
											cellspacing="0" width="99%" style="float: left;">
											<tr id="remarkTr">
												<td valign="top" align="right">Remark:</td>
												<td valign="top"><input type="hidden" id="feedbackFormRating" value="" name="feedbackFormRating" />
													<textarea rows="3" cols="50" name="feedbackformRemark" id="feedbackformRemark" style="width: 70%"></textarea>
													<div id="starPrimaryfeedbackForm"></div>
													<script type="text/javascript">
											        	$('#starPrimaryfeedbackForm').raty({
											        		readOnly: false,
											        		start: 0,
											        		half: true,
											        		targetType: 'number',
											        		click: function(score, evt) {
											        			$('#feedbackFormRating').val(score);
																	}
																});
													</script></td>
											</tr>

											<tr id="remarkTr">
												<td align="center" colspan="2">
													<%-- <input type="button" class="btn btn-primary" value="Approve" onclick="window.location.href='DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&element=1&operation=E'" /> --%>
													<% if (!isClose) { %>
														<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" /> 
													<% } else { %>
														<input type="button" class="btn btn-primary" value="Approve" onclick="approveFeedbackForm();" /> 
													<% } %>
												</td>
											</tr>
										</table>
									</s:form>
									<% } else if (statusMp.get("1") != null && uF.parseToBoolean(statusMp.get("1"))) { %>
									<table class="tb_style" cellpadding="0" cellspacing="0" width="99%" style="float: left;">
										<tr id="remarkTr">
											<td valign="top" align="right">Remark:</td>
											<td valign="top"><input type="hidden" id="feedbackFormRating" value="<%=statusMp.get("1_RATING")%>" name="feedbackFormRating" /> 
												<textarea rows="3" cols="50" name="feedbackformRemark" id="feedbackformRemark" style="width: 70%"><%=uF.showData(statusMp.get("1_REMARK"), "")%></textarea>
												<div id="starPrimaryfeedbackForm"></div> 
												<script type="text/javascript">
										        	$('#starPrimaryfeedbackForm').raty({
										        		readOnly: false,
										        		start: <%=statusMp.get("1_RATING") != null ? statusMp.get("1_RATING") : "0"%>,
										        		half: true,
										        		targetType: 'number',
										        		click: function(score, evt) {
										        			$('#feedbackFormRating').val(score);
														}
													});
												</script>
											</td>
										</tr>
									</table>
									<% } %>
									<% } %>
								</div>
							</div>
						</div>
					</div>
					<!-- /.box-body -->
				</div>


				<div class="box box-primary collapsed-box"
					style="border-top-color: #FFFFFF;">

					<div class="box-header with-border" style="<%boolean flag = (Boolean) request.getAttribute("flag");
						if (statusMp.get("2") != null && uF.parseToBoolean(statusMp.get("2"))) { %>
						background-color:lightgreen;
						<%} else if ((statusMp.get("1") != null && uF.parseToBoolean(statusMp.get("1"))) && (statusMp.get("2") == null || !uF.parseToBoolean(statusMp.get("2")))) {%>
						background-color:orange;
						<%}%>">
						<h3 class="box-title">Handover Documents</h3>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div>
					<!-- /.box-header -->
					<div class="box-body"
						style="padding: 5px; overflow-y: auto; display: none;">
						<div id="profilecontainer">
							<div class="content1">
								<div class="holder" style="width: 99%">
									<s:form theme="simple" action="ExitForm" enctype="multipart/form-data">
										<s:hidden name="resignId"></s:hidden>
										<s:hidden name="operation" value="B"></s:hidden>
										<s:hidden name="id" value="%{id}"></s:hidden>
										<table class="table table-bordered" align="center"
											id="documenttable">
											<% if (statusMp.get("2") != null && uF.parseToBoolean(statusMp.get("2"))) { %>
											<tr>
												<td colspan="2">
													<div class="msg savesuccess" style="width: 98%"><%=(((String) session.getAttribute(IConstants.USERTYPE)).equalsIgnoreCase(IConstants.EMPLOYEE)) ? "Your": uF.showData((String) hmEmpProfile.get("NAME"), "-") + "'s"%>
														handover documents are approved by <%=statusMp.get("2_APPROVED_BY")%>.
													</div>
												</td>
											</tr>
											<% } else if (statusMp.get("2") != null && !uF.parseToBoolean(statusMp.get("2"))) { %>
											<tr>
												<td colspan="2">
													<div class="msg_error" style="width: 98%"><%=(((String) session.getAttribute(IConstants.USERTYPE)).equalsIgnoreCase(IConstants.EMPLOYEE)) ? "You have": uF.showData((String) hmEmpProfile.get("NAME"), "-") + " has"%>
														uploaded handover documents &amp; waiting for approval.
													</div>
												</td>
											</tr>
											<% } %>
											<%
												if (strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) {
														if (uF.parseToBoolean(statusMp.get("2"))) {
															if (uF.parseToBoolean(clearanceFlag)) {
											%>
											<tr>
												<td colspan="2"><span style="float: left;"> <a href="javascript: void(0);" onclick="clearenceFormSummary('<%=id%>','<%=resignId%>')" title="View Clearance Form">View Clearance Form</a> 
													<a onclick="generatePdf('<%=id%>','<%=resignId%>','<%=IConstants.NODE_CLEARANCE_FORM_ID%>');" href="javascript:void(0)" class="fa fa-file-pdf-o"> </a> </span>
												</td>
											</tr>
											<% }
												} else {
											%>
											<tr>
												<td colspan="2"><a href="javascript: void(0);" onclick="clearenceForm('<%=id%>','<%=resignId%>')" title="Clearance Form">Clearance Form</a></td>
											</tr>
											<%
												}
													} else {
														if (uF.parseToBoolean(clearanceFlag)) {
											%>
											<tr>
												<td colspan="2"><span style="float: left;"> <a
														href="javascript: void(0);"
														onclick="clearenceFormSummary('<%=id%>','<%=resignId%>')"
														title="View Clearance Form">View Clearance Form</a> <a
														onclick="generatePdf('<%=id%>','<%=resignId%>','<%=IConstants.NODE_CLEARANCE_FORM_ID%>');"
														href="javascript:void(0)" class="fa fa-file-pdf-o"> </a> </span>
												</td>
											</tr>
											<%
												}
													}
													if (statusMp.get("1") != null
															&& uF.parseToBoolean(statusMp.get("1"))) {
											%>
											<tr>
												<th align="center">Document Name</th>
												<th align="center">Documents</th>
											</tr>
											<%
												if (uF.parseToInt((String) session
																.getAttribute(IConstants.USERTYPEID)) == 3 && flag) {
											%>
											<%
												if (statusMp.get("2") == null
																	|| !uF.parseToBoolean(statusMp.get("2"))) {
											%>
											<tr>
												<td valign="top" colspan="2"><a
													href="javascript:void(0)" onclick="addRow('documenttable')"
													class="add-font" style="float: right;"></a>
												</td>
											</tr>
											<%
												}
											%>
											<%
												}
											%>

											<%
												List<List<String>> outerList = (List<List<String>>) request.getAttribute("outerList");
												Map<String, String> hmDocumentComment = (Map<String, String>) request.getAttribute("hmDocumentComment");
													for (int i = 0; i < outerList.size(); i++) {
														List<String> innerList = outerList.get(i);
											%>

											<tr id="myDiv_<%=i%>">
												<td style="width: 257px;"><%=innerList.get(1)%> <input type="hidden" value="<%=innerList.get(0)%>" name="documentId"></td>

												<td>
													<table class="tb_style" style="width: 100%;">
														<tr>
															<% if (innerList.get(3) != null && innerList.get(3).length() > 0) { %>
															<td style="width: 100px;">
																<%-- <a href="<%=innerList.get(3)%>" target="_blank" title="Reference Document" style="float: right"><img src="images1/payslip.png"> </a> --%>
																<%
																	if (docRetriveLocation == null) {
																%> <a href="<%=IConstants.DOCUMENT_LOCATION + innerList.get(3)%>" target="_blank" title="Reference Document" style="float: right"><i class="fa fa-file-o" aria-hidden="true"></i></a> 
															<%
 																} else {
 															%> 
 															<a href="<%=docRetriveLocation+ IConstants.I_PEOPLE + "/"+ IConstants.I_OFFBOARD + "/"+ innerList.get(5) + "/"+ innerList.get(3)%>" target="_blank" title="Reference Document" style="float: right"><i class="fa fa-file-o" aria-hidden="true"></i></a> 
 															<% } %>
															</td>

															<td>
																<% if (statusMp.get("2") == null || !uF.parseToBoolean(statusMp.get("2"))) { %>
																<a href="javascript:void(0)" onclick="savecomment('DeleteOffboardDocument.action?docid=<%=innerList.get(0)%>&operation=D&userId=<%=(String) session.getAttribute(IConstants.EMPID)%>&status=<%=statusMp.get("2")%>');">Comment</a>
																: <% } else { %> 
																	Comment:<%=uF.showData(hmDocumentComment.get(innerList.get(0)),"")%>
																<% } %>
															</td>
															<%-- <td id="tdd<%=i%>" style="width: 100px;">
											<% if (uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 1 || uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 2) { %>
											<% if (statusMp.get("2") == null || !uF.parseToBoolean(statusMp.get("2"))) { %> 
												<% if (uF.parseToInt(innerList.get(4)) == 0) { %>
												<img onclick="getContent('tdd<%=i%>', 'DeleteOffboardDocument.action?id=<%=innerList.get(0)%>&element=tdd<%=i%>&operation=C');" title="Disable" src="images1/tick.png"> 
												<% } else { %>
												<img onclick="getContent('tdd<%=i%>', 'DeleteOffboardDocument.action?id=<%=innerList.get(0)%>&element=tdd<%=i%>&operation=C');" title="Enable" src="images1/cross.png"> 
												<% } %>
											<% } else { %>
												<% if (uF.parseToInt(innerList.get(4)) == 0) { %>
													Disable 
												<% } else { %>
													Enable
												<% } %>
											<% } %>
											<% } %> 
										</td>  --%>
															<% } %>
														</tr>

														<tr>

															<% if (uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 3 && flag) { %>
															<% if (uF.parseToInt(innerList.get(4)) == 0) { %>
															<td style="width: 100px;"><input type="hidden" value="0" id="statuss<%=i%>" name="status"></td>
															<td><input type="file" name="document" onchange="changestatus('statuss<%=i%>')" /></td>
															<td style="width: 100px;">
																<% if (uF.parseToInt(innerList.get(7)) == uF.parseToInt((String) session.getAttribute(IConstants.EMPID))) { %>
																<% if (statusMp.get("2") == null || !uF.parseToBoolean(statusMp.get("2"))) { %>
																<a class="remove-font" onclick="getContent('myDiv_<%=i%>','DeleteOffboardDocument.action?id=<%=innerList.get(0)%>&operation=A')" href="javascript:void(0)"></a>
																<% } %>
																<% } %>
															</td>
															<% } %>
															<% } %>

														</tr>
													</table>
												</td>
											</tr>
											<% } %>

											<% } else if (statusMp.get("1") == null || !uF.parseToBoolean(statusMp.get("1"))) { %>

											<tr>
												<td><div class="msg_error">Still waiting for Previous block Approval</div></td>
											</tr>

											<% } %>
										</table>

										<table style="float: left; width: 75%;" align="center">
											<tr id="handoverTr">
												<td colspan="2" align="center">
													<% if (uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 3 && flag) { %>
													<% if ((statusMp.get("2") == null || !uF.parseToBoolean(statusMp.get("2"))) && (statusMp.get("1") != null && uF.parseToBoolean(statusMp.get("1")))) { %>
													<% if (!isClose) { %>
														<input type="button" class="input_reset" value="Save" onclick="alert('Account is already closed.');" />
													<% } else { %>
														<s:submit cssClass="btn btn-primary" name="" value="Save" />
													<% } %>
													<% }
														}
 													%>
 													<% if (uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 1 || uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 2 || uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 7) { %>

													<% if ((statusMp.get("2") == null || !uF.parseToBoolean(statusMp.get("2"))) && (statusMp.get("1") != null && uF.parseToBoolean(statusMp.get("1")))) { %>
													<% if (!isClose) { %>
														<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" />
													<% } else { %>
														<input type="button" class="btn btn-primary" value="Approve" onclick="window.location.href='DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&element=<%=IConstants.OFFBOARD_HANDOVER_DOC_SECTION%>&operation=E'" />
													<% } %>
													<% } %>
													<% } %>
												</td>
											</tr>
										</table>
									</s:form>
								</div>
							</div>
						</div>
					</div>
					<!-- /.box-body -->
				</div>
				<%
					//UtilityFunctions uF = new UtilityFunctions();
					if (uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 1 || uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 2 || uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 7) {
				%>
				<div class="box box-primary collapsed-box" style="border-top-color: #FFFFFF;">

					<div class="box-header with-border" style="<%if (statusMp.get("3") != null && uF.parseToBoolean(statusMp.get("3"))) { %>
						background-color:lightgreen;
						<% } else if ((statusMp.get("2") != null && uF.parseToBoolean(statusMp.get("2"))) && (statusMp.get("3") == null || !uF.parseToBoolean(statusMp.get("3")))) { %>
						background-color:orange;
						<% } %>">
						<h3 class="box-title">HR Document</h3>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div>
					<!-- /.box-header -->
					<div class="box-body"
						style="padding: 5px; overflow-y: auto; display: none;">
						<div id="profilecontainer">
							<div class="content1">
								<div class="holder" style="width: 99%">
									<s:form theme="simple" action="ExitForm" enctype="multipart/form-data">
										<s:hidden name="resignId"></s:hidden>
										<s:hidden name="operation" value="C"></s:hidden>
										<s:hidden name="id" value="%{id}"></s:hidden>
										<% if (uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 1 || uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID)) == 7) { %>
										<table class="table" id="hrDocumentTable">
											<% if (statusMp.get("2") != null && uF.parseToBoolean(statusMp.get("2"))) { %>
											<% if (statusMp.get("3") != null && uF.parseToBoolean(statusMp.get("3"))) { %>
											<tr>
												<td colspan="2">
													<div class="msg savesuccess" style="width: 98%"><%=(((String) session.getAttribute(IConstants.USERTYPE)).equalsIgnoreCase(IConstants.EMPLOYEE)) ? "Your" : uF.showData((String) hmEmpProfile.get("NAME"), "-") + "'s"%>
														HR documents are uploaded and approved by <%=statusMp.get("3_APPROVED_BY")%>.
													</div>
												</td>
											</tr>
											<% } %>
											<tr>
												<th>Document Name</th>
												<th>Action</th>
											</tr>
											<tr>
												<td valign="top" colspan="2"><a href="javascript:void(0)" onclick="addRow('hrDocumentTable')" class="add-font" style="float: right;"></a></td>
											</tr>

											<%
												List<List<String>> outerList = (List<List<String>>) request.getAttribute("HRDocumentList");
													for (int i = 0; i < outerList.size(); i++) {
														List<String> innerList = outerList.get(i);
											%>
											<tr id="myHRDiv_<%=i%>">
												<td valign="top" style="width: 200px;"><%=innerList.get(1)%>
													<input type="hidden" value="<%=innerList.get(0)%>" name="documentId">
												</td>
												<td valign="top">
													<table class="tb_style" style="width: 100%;">
														<tr>
															<td valign="top">
																<%if (innerList.get(3) != null && innerList.get(3).length() > 0) { %>
																<%-- <a href="<%=innerList.get(3)%>" target="_blank" title="Reference Document" style="float: left; margin-right: 20px;"> <img src="images1/payslip.png"> </a> --%>
																<% if (docRetriveLocation == null) { %> 
																	<a href="<%=IConstants.DOCUMENT_LOCATION + innerList.get(3)%>" target="_blank" title="Reference Document" style="float: right"><i class="fa fa-file-o" aria-hidden="true"></i> </a> 
																<% } else { %>
																	<a href="<%=docRetriveLocation + IConstants.I_PEOPLE + "/" + IConstants.I_OFFBOARD + "/" + innerList.get(5) + "/" + innerList.get(3)%>" target="_blank" title="Reference Document" style="float: right"><i class="fa fa-file-o" aria-hidden="true"></i> </a> 
																<% } %>
																<% } %>
																 <input type="file" name="document" onchange="changestatus('hrstatuss<%=i%>')" /> <input type="hidden" value="0" id="hrstatuss<%=i%>" name="status" /></td>
															<td style="width: 100px;">
																<% if (statusMp.get("2") != null && uF.parseToBoolean(statusMp.get("2"))) { %>
																	<a class="remove-font" onclick="getContent('myHRDiv_<%=i%>','DeleteOffboardDocument.action?id=<%=innerList.get(0)%>&operation=A')" href="javascript:void(0)"></a>
																<% } %>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<% } %>

											<% } else { %>
											<tr>
												<td><div class="msg_error">Still waiting for Previous block Approval</div></td>
											</tr>
											<% } %>

										</table>

										<table class="tb_style" width="75%" align="center">
											<% if ((statusMp.get("2") != null && uF.parseToBoolean(statusMp.get("2")))) { %>
											<tr id="HRTr">
												<td colspan="2" align="center">
													<% if (!isClose) { %>
														<input type="button" class="input_reset" value="Save and Approve" onclick="alert('Account is already closed.');" />
													<% } else { %>
														<s:submit cssClass="btn btn-primary" name="" value="Save and Approve" />
													<% } %>
												</td>
											</tr>
											<% } %>
										</table>
										<% } else { %>
										<div class="nodata msg">
											<span>This section is not available for you.</span>
										</div>
										<% } %>

									</s:form>
								</div>
							</div>
						</div>
					</div>
					<!-- /.box-body -->
				</div>
				<% } %>
				<div class="box box-primary collapsed-box" style="border-top-color: #FFFFFF;">

					<div class="box-header with-border" style="<%if (statusMp.get("4") != null && uF.parseToBoolean(statusMp.get("4"))) {%>
						background-color:lightgreen;
						<%} else if ((statusMp.get("3") != null && uF.parseToBoolean(statusMp.get("3"))) && (statusMp.get("4") == null || !uF.parseToBoolean(statusMp.get("4")))) {%>
						background-color:orange;
						<%}%>">
						<h3 class="box-title">Full & Final Settlement</h3>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div>
					<!-- /.box-header -->
					<div class="box-body"
						style="padding: 5px; overflow-y: auto; display: none;">
						<div id="profilecontainer">
							<div class="content1">
								<div class="holder" style="width: 99%">
									<%
										Map<String, String> hmPayCycle = (Map<String, String>) request.getAttribute("hmPayCycle");
										Map<String, String> totalSalry = (LinkedHashMap<String, String>) request.getAttribute("totalSalry");
										Map<String, String> grossSalry = (Map<String, String>) request.getAttribute("grossSalry");
										Map<String, String> paycycleSalry = (Map<String, String>) request.getAttribute("paycycleSalry");
										Map<String, Boolean> statusSalry = (Map<String, Boolean>) request.getAttribute("statusSalry");
										//int user_type = uF.parseToInt((String) session.getAttribute(IConstants.USERTYPEID));
										boolean isFlag = true;
										/* if (strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.MANAGER))) {
											isFlag = true;
										} */
									%>
									<% if (totalSalry != null) { %>
									<div style="float: left;">
										<table class="table table-bordered table-striped">
											<tr>
												<th>Paycycle</th>
												<th>Net Salary</th>
												<th>Gross Salary</th>
												<th>Status</th>
												<% if (isFlag) { %>
												<th>Action</th>
												<% } %>
												<th>Preview</th>
											</tr>
											<%
											//System.out.println("EF.jsp/985--totalSalry="+totalSalry);
												Set<String> keySet = totalSalry.keySet();
													double total = 0.0;
													double grosstotal = 0.0;
													double approveTotal = 0.0;
													Iterator it = keySet.iterator();
													while (it.hasNext()) {
														String key = (String) it.next();
														String net = totalSalry.get(key);
														total += uF.parseToDouble(net);
														grosstotal += uF.parseToDouble(grossSalry.get(key));

														if (statusSalry.get(key)) {
															approveTotal += uF.parseToDouble(net);
														}
											%>

											<tr>
												<td><%=hmPayCycle.get(key)%></td>
												<td><%-- <%=net%> --%>
												<!--Created By dattatray Date:14-12-21  -->
												<%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(net)))) %>
												</td>
												<td><%=grossSalry.get(key)%></td>

												<% if (paycycleSalry.get(key) != null) { %>
												<td>Already Processed</td>
												<%if (isFlag) { %>
												<td>
													<% if (!statusSalry.get(key)) { %>
													<% if (strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
													UnApproved <% } else {
														if (!isClose) { 
													%>
													<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" />
													<% } else { %>
													<input type="button" class="btn btn-primary" value="Approve" onclick="window.location.href='DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&comment=<%=paycycleSalry.get(key)%>&element=<%=IConstants.OFFBOARD_FULL_FINAL_SETTELMENT_SECTION%>&operation=H'" />
													<% } } %>
													<% } else { %> 
														Approved
													 <% } %>
												</td>
												<% } %>
												<% } else { %>
												<td>Not processed yet</td>
												<% if (isFlag) { %>
												<td>
													<% if (strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {%>
													UnApproved 
													<% } else {
														if (!isClose) {
													%> 
													<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" /> 
													<% } else { %>
											<!-- ===start parvez date: 15-04-2022=== -->		
													<%-- <input type="button" class="btn btn-primary"
													value="Approve"
													onclick="window.location.href='DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&month=<%=key%>&paycycle=<%=key%>&element=<%=IConstants.OFFBOARD_FULL_FINAL_SETTELMENT_SECTION%>&operation=H'" /> --%>
													<input type="button" class="btn btn-primary"
													value="Approve"
													onclick="window.location.href='DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&month=<%=key%>&paycycle=<%=key%>&element=<%=IConstants.OFFBOARD_FULL_FINAL_SETTELMENT_SECTION%>&operation=H&fromPage=ExitForm'" />
											<!-- ===end parvez date: 15-04-2022=== -->
													<% }
														}
													%>
												</td>
												<% } %>
												<% } %>
												<td>
													<%-- <div style="float: right">
												<a title="Preview" class="preview" href="javascript:void(0)" onclick="salaryPreview(<s:property value="id" />,'<%=key%>')">Preview</a> 
											</div> --%></td>
											</tr>
											<% } %>
											<tr>
												<th>Total</th>
												
												<th><%-- <%=uF.formatIntoTwoDecimal(total)%> --%>
												<!--Created By dattatray Date:14-12-21  -->
												<%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),total))) %>
												
												</th>
												<th><%=uF.formatIntoTwoDecimal(grosstotal)%></th>
												<% if (isFlag) { %>
												<th>--</th>
												<% } %>
												<th>--</th>
												<th><div style="float: right">
													<a title="Preview" class="preview" href="javascript:void(0)" onclick="fullSalaryPreview(<s:property value="id" />,<s:property value="resignId" />)">Preview</a>
												</div></th>
											</tr>

											<% if (statusMp.get("4") != null && !uF.parseToBoolean(statusMp.get("4")) && statusMp.get("3") != null && uF.parseToBoolean(statusMp.get("3"))) { %>
											<%-- <tr>
						<th colspan="4"><input type="button" class="btn btn-primary" value="Approve"
							onclick="window.location.href='DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&element=4&operation=E'" />
						</th>
					</tr> --%>
											<% } %>

											<% if (isFlag && (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) {
											%>

											<% if ((statusMp.get("4") == null || !uF.parseToBoolean(statusMp.get("4"))) && (statusMp.get("3") != null && uF.parseToBoolean(statusMp.get("3")))) { %>
											<tr>
												<td colspan="6">
													<% if (!isClose) { %> 
													<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" />
													<% } else { %>
													<input type="button" class="btn btn-primary" value="Approve" onclick="window.location.href='DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&element=<%=IConstants.OFFBOARD_FULL_FINAL_SETTELMENT_SECTION%>&operation=E'" />
												</td>
												<% } %>
											</tr>
											<% } %>

											<% } %>
										</table>
									</div>
									<div style="float: left; margin-left: 10px">
										<% Map<String, Boolean> exemptionStatusMp = (Map<String, Boolean>) request.getAttribute("exemptionStatusMp");
												if (exemptionStatusMp == null)
													exemptionStatusMp = new HashMap<String, Boolean>();

												double reimbursement = (Double) request.getAttribute("reimbursement");
												double gratuity = (Double) request.getAttribute("gratuity");
												double dblLTA = (Double) request.getAttribute("dblLTA");
												double dblPerk = (Double) request.getAttribute("dblPerk");
												double deductAmt = (Double) request.getAttribute("deductAmt");

												double sattletotal = approveTotal;
										%>
										<table class="table table-striped table-bordered">
											<tr>
												<th>Details</th>
												<th>Amount</th>
												<% if (isFlag) { %>
												<th>Action</th>
												<% } %>
											</tr>
											<tr>
												<th>Salary</th>
												<td>
												<%-- <%=uF.formatIntoTwoDecimal(approveTotal)%> --%>
												
												<!--Created By dattatray Date:14-12-21  -->
												<%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),approveTotal))) %>
												</td>
												<% if (isFlag) { %>
												<td></td>
												<% } %>
											</tr>
											<tr>
												<th>Reimbursement</th>
												<td><%=uF.formatIntoTwoDecimal(reimbursement)%></td>
												<%
													if (isFlag) {
												%>
												<td id="reimbursement">
													<%
														if (statusMp.get("100") == null) {
													%> <% if (strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
													UnApproved <% } else { 
														if (!isClose) {
													%>
													<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" />
													<% } else { %>
													<input type="button" class="btn btn-primary" value="Approve" onclick="getContent('reimbursement','DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&element=100&comment=REIMBURSEMENT&operation=G')" />
													<% } %>
													<% }
 													} else {
 														sattletotal += reimbursement;
 													%> Approved <% } %>
												</td>
												<% } %>
											</tr>
											<tr>
												<th>Gratuity</th>
												<td><%=uF.formatIntoTwoDecimal(gratuity)%></td>
												<%
													if (isFlag) {
												%>
												<td id="gretuity">
													<% if (statusMp.get("101") == null) { %>
													<% if (strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
													UnApproved <% } else { 
														if (!isClose) { %>
														<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" />
														<% } else { %>
													<input type="button" class="btn btn-primary" value="Approve" onclick="getContent('gretuity','DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&element=101&amount=<%=gratuity%>&comment=GRATUITY&operation=G')" />
													<% } %>
													<% } 
													} else { 
														sattletotal += gratuity;
 													%> Approved <% } %>
												</td>
												<% } %>
											</tr>

											<tr>
												<th>LTA</th>
												<td><%=uF.formatIntoTwoDecimal(dblLTA)%></td>
												<% if (isFlag) { %>
												<td id="lta">
													<% if (statusMp.get("102") == null) { %>
													<% if (strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
													%> UnApproved <% } else { 
														if (!isClose) { %>
														<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" />
														<% } else { %>
													<input type="button" class="btn btn-primary" value="Approve" onclick="getContent('lta','DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&element=102&amount=<%=dblLTA%>&comment=LTA&operation=G')" />
													<% } %>
													<% } 
													} else { 
														sattletotal += dblLTA; %>
														 Approved 
														 <% } %>
												</td>
												<% } %>
											</tr>
											<tr>
												<th>Perk</th>
												<td><%=uF.formatIntoTwoDecimal(dblPerk)%></td>
												<% if (isFlag) { %>
												<td id="Perk">
													<% if (statusMp.get("103") == null) { %>
													<% if (strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
													UnApproved <% } else { 
														if (!isClose) { %>
														<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" />
														<% } else { %>
													<input type="button" class="btn btn-primary" value="Approve" onclick="getContent('Perk','DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&element=103&amount=<%=dblPerk%>&comment=PERK&operation=G')" />
													<% } %>
													<% } 
													} else { 
														sattletotal += dblPerk;
													%> Approved <% } %>
												</td>
												<% } %>
											</tr>
											<tr>
												<th>Other Deduction</th>
												<td><input type="text" name="deductAmt" id="deductAmt" value="<%=deductAmt%>" onkeypress="return isNumberKey(event)"
													<%if (statusMp.get("104") != null) {%> readonly="readonly"
													<%}%> style="width: 100px;" />
												</td>
												<% if (isFlag) { %>
												<td id="otherDeduct">
													<% if (statusMp.get("104") == null) { %>
													<% if (strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %> 
														UnApproved 
													<% } else {
														if (!isClose) {
 													%>
 														<input type="button" class="input_reset" value="Approve" onclick="alert('Account is already closed.');" />
 													<% } else { %>
														<input type="button" class="btn btn-primary" value="Approve" onclick="getContent('otherDeduct','DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&element=104&amount=<%=dblPerk%>&comment=OTHERDEDUCT&operation=G&deductAmt='+document.getElementById('deductAmt').value)" />
													<% } %>
													<% } 
													} else { 
													%>
														Approved 
													<% } %>
												</td>
												<% }
													sattletotal -= deductAmt;
												%>
											</tr>

											<tr>
												<th>Settlement Amount</th>
												<td>
												<%-- <%=uF.formatIntoTwoDecimal(sattletotal)%> --%>
												<!--Created By dattatray Date:14-12-21  -->
												<%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),sattletotal))) %>
												</td>
												<%
													if (isFlag) {
												%>
												<td>--</td>
												<% } %>
											</tr>
										</table>
									</div>
									<div style="width: 100%; float: left;">
										<span id="pdfAccDiv" style="float: left; margin-right: 5px;">
											<s:form theme="simple" action="ExportPDF">
												<s:hidden name="emp_id" value="%{id}"></s:hidden>
												<s:hidden name="resignId"></s:hidden>
												<s:submit value="PDF" cssClass="btn btn-primary"></s:submit>
											</s:form> </span> <span id="excelAccDiv"
											style="float: left; margin-right: 5px;"> <s:form
												theme="simple" action="ExportFullFinalExcel">
												<s:hidden name="emp_id" value="%{id}"></s:hidden>
												<s:hidden name="resignId"></s:hidden>
												<s:submit value="Excel" cssClass="btn btn-primary"></s:submit>
											</s:form> </span>

										<% if (!isClose) { %>
										<span
											style="float: left; margin-right: 5px; margin-top: 10px; color: red;"><b>
												Account closed. </b> </span>
										<% } else if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
										<span id="closeAccDiv" style="float: left; margin-right: 5px;">
											<%-- <input type="button" class="btn btn-danger" value="Close Account" onclick="getContent('closeAccDiv','DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&operation=J')" /> --%>
											<input type="button" class="btn btn-danger"
											value="Close Account"
											onclick="if(confirm('Are you sure, do you want to close this account?'))window.location='DeleteOffboardDocument.action?id=<s:property value="id" />&resignId=<s:property value="resignId" />&operation=J';" />
										</span>
										<% } %>
									</div>
									<% } else { %>
									<div class="msg_error">Still waiting for previous block
										Approval.</div>
									<% } %>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
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