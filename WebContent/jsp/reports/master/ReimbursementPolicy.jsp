<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	List<FillLevel> levelList=(List<FillLevel>)request.getAttribute("levelList");
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	Map<String, List<Map<String, String>>> hmMobileBill = (Map<String, List<Map<String, String>>>)request.getAttribute("hmMobileBill");
	if(hmMobileBill == null) hmMobileBill = new HashMap<String, List<Map<String,String>>>();
	
	Map<String, List<Map<String, String>>> hmLocalBill = (Map<String, List<Map<String, String>>>)request.getAttribute("hmLocalBill");
	if(hmLocalBill == null) hmLocalBill = new HashMap<String, List<Map<String,String>>>();
	
	Map<String, List<Map<String, String>>> hmTravelAdvance = (Map<String, List<Map<String, String>>>)request.getAttribute("hmTravelAdvance");
	if(hmTravelAdvance == null) hmTravelAdvance = new HashMap<String, List<Map<String,String>>>();
	
	Map<String, List<Map<String, String>>> hmClaim = (Map<String, List<Map<String, String>>>)request.getAttribute("hmClaim");
	if(hmClaim == null) hmClaim = new HashMap<String, List<Map<String,String>>>();
	
	Map<String, List<Map<String, String>>> hmReimbursementCTC = (Map<String, List<Map<String, String>>>)request.getAttribute("hmReimbursementCTC");
	if(hmReimbursementCTC == null) hmReimbursementCTC = new HashMap<String, List<Map<String,String>>>();
	
	Map<String, List<Map<String, String>>> hmReimbursementCTCHead = (Map<String, List<Map<String, String>>>)request.getAttribute("hmReimbursementCTCHead");
	if(hmReimbursementCTCHead == null) hmReimbursementCTCHead = new HashMap<String, List<Map<String,String>>>();
	
	Map<String, List<Map<String, String>>> hmReimbursementHeadAmt = (Map<String, List<Map<String, String>>>)request.getAttribute("hmReimbursementHeadAmt");
	if(hmReimbursementHeadAmt == null) hmReimbursementHeadAmt = new HashMap<String, List<Map<String,String>>>();
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script> 
<script type="text/javascript" charset="utf-8">
function addMobileBill(org_id,level_id, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Mobile Bill Reimbursement');
	$.ajax({
		url : 'AddMobileBillReimbursement.action?operation=A&strOrg='+org_id+'&strLevel='+level_id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editMobileBill(org_id,level_id,reimbId, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Mobile Bill Reimbursement');
	$.ajax({
		url : 'AddMobileBillReimbursement.action?operation=E&strOrg='+org_id+'&strLevel='+level_id+'&reimbPolicyId='+reimbId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addLocalReimbursement(org_id,level_id, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Local Reimbursement');
	$.ajax({
		url : 'AddLocalReimbursement.action?operation=A&strOrg='+org_id+'&strLevel='+level_id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editLocalReimbursement(org_id,level_id,reimbId, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Local Reimbursement');
	$.ajax({
		url : 'AddLocalReimbursement.action?operation=E&strOrg='+org_id+'&strLevel='+level_id+'&reimbPolicyId='+reimbId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addTravelAdvanceReimbursement(org_id,level_id, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Travel Advance Reimbursement');
	$.ajax({
		url : 'AddTravelAdvanceReimbursement.action?operation=A&strOrg='+org_id+'&strLevel='+level_id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editTravelAdvanceReimbursement(org_id,level_id,reimbId, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Travel Advance Reimbursement');
	$.ajax({
		url : 'AddTravelAdvanceReimbursement.action?operation=E&strOrg='+org_id+'&strLevel='+level_id+'&reimbPolicyId='+reimbId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addClaimsReimbursement(org_id,level_id, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Claim Reimbursement');
	$.ajax({
		url : 'AddClaimReimbursement.action?operation=A&strOrg='+org_id+'&strLevel='+level_id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editClaimsReimbursement(org_id,level_id,reimbId, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Claim Reimbursement');
	$.ajax({
		url : 'AddClaimReimbursement.action?operation=E&strOrg='+org_id+'&strLevel='+level_id+'&reimbPolicyId='+reimbId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addReimbursementPartOfCTC(org_id,level_id, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Reimbursement Part Of CTC');
	$.ajax({
		url : 'AddReimbursementPartOfCTC.action?operation=A&strOrg='+org_id+'&strLevel='+level_id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editReimbursementPartOfCTC(org_id,level_id,reimbId, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Reimbursement Part Of CTC');
	$.ajax({
		url : 'AddReimbursementPartOfCTC.action?operation=E&strOrg='+org_id+'&strLevel='+level_id+'&reimbCTCId='+reimbId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addReimbursementCTCHead(reimCTCHeadName,reimCTCId, strLevelId, strOrg, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Reimbursement CTC Head');
	$.ajax({
		url : 'AddReimbursementCTCHead.action?reimCTCId='+reimCTCId+'&orgId='+strOrg+'&levelId='+strLevelId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editReimbursementCTCHead(reimCTCHeadId,reimCTCHeadName,reimCTCId, strLevelId, strOrg, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit New Reimbursement CTC Head');
	$.ajax({
		url : 'AddReimbursementCTCHead.action?operation=E&ID='+reimCTCHeadId+'&reimCTCId='+reimCTCId+'&orgId='+strOrg+'&levelId='+strLevelId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function getData(type) {
	var org = document.getElementById("f_org").value;
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	
	window.location='MyDashboard.action?strOrg='+org+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
}

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
</script>

<div class="box-body">
	<div class="box box-default collapsed-box">
		<div class="box-header with-border">
		    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
		    <div class="box-tools pull-right">
		        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
		        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		    </div>
		</div>
		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			<s:form name="frm_ReimbursementPolicy" action="MyDashboard" theme="simple">
				<s:hidden name="userscreen" id="userscreen"/>
				<s:hidden name="navigationId" id="navigationId"/>
				<s:hidden name="toPage" id="toPage"/>
				<div style="float: left; width: 99%; margin-left: 10px;">
					<div style="float: left; margin-right: 5px;">
						<i class="fa fa-filter"></i>
					</div>
					<div style="float: left; width: 75%;">
						<div style="float: left; margin-left: 10px;">
							<p style="padding-left: 5px;">Organisation</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName"
			         			onchange="getData('0');" list="orgList"/>
						</div>
					</div>
				</div>
			</s:form>
		</div>
	</div>
	
	<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
	<% session.setAttribute("MESSAGE", ""); %>
	
	<div class="col-md-12" style="float: left; width: 98%;">
	    <ul class="level_list">
	    	<% for(int i=0;levelList!=null && i<levelList.size();i++){ 
	    		String strLevelId = levelList.get(i).getLevelId();
	    	%>
		    	<li>
					<strong><%=levelList.get(i).getLevelCodeName()%></strong>
					<ul>
						<li>Reimbursement Part of CTC
							<ul>
								<li class="addnew desgn" style="width: 500px;"><a href="javascript:void(0)" onclick="addReimbursementPartOfCTC('<%=request.getAttribute("f_org") %>','<%=strLevelId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Reimbursement Part of CTC</a></li>
								<%
								List<Map<String, String>> alReimbursementCTC = hmReimbursementCTC.get(strLevelId);
								if(alReimbursementCTC == null) alReimbursementCTC = new ArrayList<Map<String,String>>();
								int nAlReimbursementCTCSize = alReimbursementCTC.size();
								for(int j = 0; j< nAlReimbursementCTCSize; j++){
									Map<String, String> hmReimbursementCTCInner = alReimbursementCTC.get(j);
									String strReimburementCTCId = hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_ID");
									String strReimburementCTCName = hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_NAME");
									strReimburementCTCName = strReimburementCTCName != null && !strReimburementCTCName.trim().equals("") && !strReimburementCTCName.trim().equalsIgnoreCase("NULL") ? strReimburementCTCName.replace("'","\'") : "";
								%>
									<li>
										<a href="javascript:void(0)" title="Edit" onclick="editReimbursementPartOfCTC('<%=request.getAttribute("f_org") %>','<%=strLevelId %>','<%=strReimburementCTCId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
										<a title="Delete" href="AddReimbursementPartOfCTC.action?operation=D&reimbCTCId=<%=strReimburementCTCId %>&strOrg=<%=request.getAttribute("f_org") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this Reimbursement Part of CTC?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
										<strong>Reimbursement CTC Name:</strong>&nbsp;<%=hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_NAME") %>  [<%=hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_CODE") %>]
										<ul class="level_list">
											<li class="addnew desgn" style="width: 98%;">
												<a href="javascript:void(0)"  onclick="addReimbursementCTCHead('<%=uF.showData(strReimburementCTCName,"") %>','<%=strReimburementCTCId %>','<%=strLevelId %>','<%=(String)request.getAttribute("f_org") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New Reimbursement CTC Head">+ Add New Reimbursement CTC Head</a>
											</li>
										<%
										List<Map<String, String>> alReimbursementCTCHead = hmReimbursementCTCHead.get(strReimburementCTCId);
										if(alReimbursementCTCHead == null) alReimbursementCTCHead = new ArrayList<Map<String,String>>();
										int nAlReimbursementHeadSize = alReimbursementCTCHead.size();
										if(nAlReimbursementHeadSize > 0){
											for(int k = 0; k< nAlReimbursementHeadSize; k++){
												Map<String, String> hmReimbursementHeadInner = alReimbursementCTCHead.get(k);
												String strReimburementHeadId = hmReimbursementHeadInner.get("REIMBURSEMENT_HEAD_ID");
												String strReimburementHeadName = hmReimbursementHeadInner.get("REIMBURSEMENT_HEAD_NAME");
												strReimburementHeadName = strReimburementHeadName != null && !strReimburementHeadName.trim().equals("") && !strReimburementHeadName.trim().equalsIgnoreCase("NULL") ? strReimburementHeadName.replace("'","\'") : "";
										%>
										<li class="addnew desgn" style="width: 98%;">
											<a href="javascript:void(0)" title="Edit" onclick="editReimbursementCTCHead('<%=strReimburementHeadId %>','<%="" %>','<%=strReimburementCTCId %>','<%=strLevelId %>','<%=(String)request.getAttribute("f_org") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
											<a title="Delete" href="AddReimbursementCTCHead.action?operation=D&ID=<%=strReimburementHeadId %>&strOrg=<%=request.getAttribute("f_org") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this Reimbursement CTC Head?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
											<strong>Head Name:</strong>&nbsp;<%=hmReimbursementHeadInner.get("REIMBURSEMENT_HEAD_NAME") %>  [<%=hmReimbursementHeadInner.get("REIMBURSEMENT_HEAD_CODE") %>],&nbsp;
											<strong>Description:</strong>&nbsp;<%=hmReimbursementHeadInner.get("REIMBURSEMENT_HEAD_DESC") %>&nbsp;
											<%
											List<Map<String, String>> alReimHeadAmt = hmReimbursementHeadAmt.get(strReimburementHeadId);
											if(alReimHeadAmt == null) alReimHeadAmt = new ArrayList<Map<String,String>>();
											if(alReimHeadAmt.size() > 0){
											%>
												<ul>
													<%
														for(int l=0; alReimHeadAmt!=null && l < alReimHeadAmt.size(); l++){
															Map<String, String> hmInner = (Map<String, String>) alReimHeadAmt.get(l);
													%>
														<li>
															<strong><%=(l+1) %>. Financial Year:</strong>&nbsp;<%=hmInner.get("FINANCIAL_YEAR") %>,&nbsp;
															<strong>Amount:</strong>&nbsp;<%=hmInner.get("AMOUNT") %>,&nbsp;
															<strong>Need to apply with documents or receipts:</strong>&nbsp;<%=hmInner.get("ATTACHMENT") %>,&nbsp;
															<strong>Is Optional:</strong>&nbsp;<%=hmInner.get("IS_OPTIMAL") %>&nbsp;
														</li>
													<%} %>
												</ul>													
										<%		}%>
											</li>
											<%} 
										}%>
										</ul>
									</li>
								<%} %>
							</ul>							
						</li>
						<li>Mobile Bill Reimbursement
							<ul>
								<%
								List<Map<String, String>> alMobileBill = hmMobileBill.get(strLevelId);
								if(alMobileBill == null) alMobileBill = new ArrayList<Map<String,String>>();
								if(alMobileBill == null || alMobileBill.isEmpty() || alMobileBill.size() == 0){
								%>
									<li><a href="javascript:void(0)" onclick="addMobileBill('<%=request.getAttribute("f_org") %>','<%=strLevelId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Mobile Bill Reimbursement Policy</a></li>
								<%} %>
								<%
								for(int j = 0; j< alMobileBill.size(); j++){
									Map<String, String> hmMobileInner = alMobileBill.get(j);
								%>
									<li>
										<a title="Edit" href="javascript:void(0)" onclick="editMobileBill('<%=request.getAttribute("f_org") %>','<%=strLevelId %>','<%=hmMobileInner.get("REIMBURSEMENT_POLICY_ID") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
										<a title="Delete" href="AddMobileBillReimbursement.action?operation=D&reimbPolicyId=<%=hmMobileInner.get("REIMBURSEMENT_POLICY_ID") %>&strOrg=<%=request.getAttribute("f_org") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this mobile bill policy?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
										<strong>Default Policy:</strong>&nbsp;<%=uF.showYesNo(hmMobileInner.get("REIMBURSEMENT_IS_MOBILE_POLICY")) %>,&nbsp;
										<strong>Mobile Limit Type:</strong>&nbsp;<%=uF.parseToInt(hmMobileInner.get("REIMBURSEMENT_MOBILE_LIMIT_TYPE")) == 2 ? "Actual" : "No Limit" %>
										<%if(uF.parseToInt(hmMobileInner.get("REIMBURSEMENT_MOBILE_LIMIT_TYPE")) == 2){ %>
											,&nbsp;<strong>Mobile Limit:</strong>&nbsp;<%=uF.showData(hmMobileInner.get("REIMBURSEMENT_MOBILE_LIMIT"),"") %>
										<%} %>
									</li>
								<%} %>
							</ul>							
						</li>
						<li>Local Reimbursement
							<ul>
								<li><a href="javascript:void(0)" onclick="addLocalReimbursement('<%=request.getAttribute("f_org") %>','<%=strLevelId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Local Reimbursement Policy</a></li>
								<%
								List<Map<String, String>> alLocalBill = hmLocalBill.get(strLevelId);
								if(alLocalBill == null) alLocalBill = new ArrayList<Map<String,String>>();
								for(int j = 0; j< alLocalBill.size(); j++){
									Map<String, String> hmLocalInner = alLocalBill.get(j);
								%>
									<li>
										<a title="Edit" href="javascript:void(0)" onclick="editLocalReimbursement('<%=request.getAttribute("f_org") %>','<%=strLevelId %>','<%=hmLocalInner.get("REIMBURSEMENT_POLICY_ID") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
										<a title="Delete" href="AddLocalReimbursement.action?operation=D&reimbPolicyId=<%=hmLocalInner.get("REIMBURSEMENT_POLICY_ID") %>&strOrg=<%=request.getAttribute("f_org") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this local policy?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
										<strong>Default Policy:</strong>&nbsp;<%=uF.showYesNo(hmLocalInner.get("REIMBURSEMENT_IS_LOCAL_POLICY")) %>,&nbsp;
										<strong>Type:</strong>&nbsp;<%=hmLocalInner.get("REIMBURSEMENT_LOCAL_TYPE") %>,&nbsp;
										<%
										String strPer = "pm";
										if(uF.parseToInt(hmLocalInner.get("REIMBURSEMENT_LOCAL_TYPE_ID")) == 1 || uF.parseToInt(hmLocalInner.get("REIMBURSEMENT_LOCAL_TYPE_ID")) == 3){
											strPer = "per km/mile";
										%>
											<strong>Transportation Type:</strong>&nbsp;<%=hmLocalInner.get("REIMBURSEMENT_TRANSPORT_TYPE") %>,&nbsp;
										<%}%>
										<strong>Limit Type:</strong>&nbsp;<%=hmLocalInner.get("REIMBURSEMENT_LOCAL_LIMIT_TYPE") %>,&nbsp;
										<%if(uF.parseToInt(hmLocalInner.get("REIMBURSEMENT_LOCAL_LIMIT_TYPE_ID")) == 2){ %>
											<strong>Local Limit:</strong>&nbsp;<%=uF.showData(hmLocalInner.get("REIMBURSEMENT_LOCAL_LIMIT"),"") %>&nbsp;<%=strPer %>,&nbsp;
										<%}%>
										<strong>Require Approval:</strong>&nbsp;<%=uF.showYesNo(hmLocalInner.get("REIMBURSEMENT_IS_REQUIRE_POLICY")) %>
										<%if(uF.parseToBoolean(hmLocalInner.get("REIMBURSEMENT_IS_REQUIRE_POLICY"))){ %>
											,&nbsp;<strong>Minimum:</strong>&nbsp;<%=uF.showData(hmLocalInner.get("REIMBURSEMENT_MIN_AMOUNT"),"") %>,&nbsp;
											<strong>Maximum:</strong>&nbsp;<%=uF.showData(hmLocalInner.get("REIMBURSEMENT_MAX_AMOUNT"),"") %>
										<%} %>
									</li>
								<%} %>
							</ul>							
						</li>
						<li>Travel Advance
							<ul>
								<li><a href="javascript:void(0)" onclick="addTravelAdvanceReimbursement('<%=request.getAttribute("f_org") %>','<%=strLevelId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Travel Advance Reimbursement Policy</a></li>
								<%
								List<Map<String, String>> alTravelAdvance = hmTravelAdvance.get(strLevelId);
								if(alTravelAdvance == null) alTravelAdvance = new ArrayList<Map<String,String>>();
								for(int j = 0; j< alTravelAdvance.size(); j++){
									Map<String, String> hmTravelAdvanceInner = alTravelAdvance.get(j);
								%>
									<li>
										<a title="Edit" href="javascript:void(0)" onclick="editTravelAdvanceReimbursement('<%=request.getAttribute("f_org") %>','<%=strLevelId %>','<%=hmTravelAdvanceInner.get("REIMBURSEMENT_POLICY_ID") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
										<a title="Delete" href="AddTravelAdvanceReimbursement.action?operation=D&reimbPolicyId=<%=hmTravelAdvanceInner.get("REIMBURSEMENT_POLICY_ID") %>&strOrg=<%=request.getAttribute("f_org") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this travel advance policy?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
										<strong>Country:</strong>&nbsp;<%=uF.showData(hmTravelAdvanceInner.get("REIMBURSEMENT_COUNTRY_NAME"),"") %>,&nbsp;
										<strong>City:</strong>&nbsp;<%=uF.showData(hmTravelAdvanceInner.get("REIMBURSEMENT_CITY"),"") %>,&nbsp;
										<strong>Eligible amount:</strong>&nbsp;<%=uF.showData(hmTravelAdvanceInner.get("REIMBURSEMENT_ELIGIBLE_AMOUNT"),"") %>&nbsp;
										per <%=uF.showData(hmTravelAdvanceInner.get("REIMBURSEMENT_ELIGIBLE_TYPE"),"") %>
									</li>
								<%} %>
							</ul>							
						</li>
						<li>Claims Reimbursement
							<ul>
								<%
								List<Map<String, String>> alClaim = hmClaim.get(strLevelId);
								if(alClaim == null) alClaim = new ArrayList<Map<String,String>>();
								if(alClaim == null || alClaim.isEmpty() || alClaim.size() == 0){
								%>
									<li><a href="javascript:void(0)" onclick="addClaimsReimbursement('<%=request.getAttribute("f_org") %>','<%=strLevelId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Claims Reimbursement Policy</a></li>
								<%} %>
								
								<%for(int j = 0; j< alClaim.size(); j++){
									Map<String, String> hmClaimInner = alClaim.get(j);
								%>
									<li>
										<div>
											<a title="Edit" href="javascript:void(0)" onclick="editClaimsReimbursement('<%=request.getAttribute("f_org") %>','<%=strLevelId %>','<%=hmClaimInner.get("REIMBURSEMENT_POLICY_ID") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
											<a title="Delete" href="AddClaimReimbursement.action?operation=D&reimbPolicyId=<%=hmClaimInner.get("REIMBURSEMENT_POLICY_ID") %>&strOrg=<%=request.getAttribute("f_org") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this claim policy?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
										</div>
										<div style="margin-left: 39px;">
											<span><h4>Travel Policy Eligibility and Limits</h4><hr style="border: solid 1px #ececec;"></span>
											<strong>Transportation Type:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_TRAVEL_TYPE"),"") %>,&nbsp;
											<%if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_TRAVEL_TYPE_ID")) == 1){ %>
												<strong>Train Type:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_TRAIN_TYPE"),"") %>,&nbsp;
											<%} else if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_TRAVEL_TYPE_ID")) == 2){ %>
												<strong>Bus Type:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_BUS_TYPE"),"") %>,&nbsp;
											<%} else if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_TRAVEL_TYPE_ID")) == 3){ %>
												<strong>Flight Type:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_FLIGHT_TYPE"),"") %>,&nbsp;
											<%} else if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_TRAVEL_TYPE_ID")) == 4){ %>
												<strong>Car Type:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_CAR_TYPE"),"") %>,&nbsp;
											<%} %>
											<strong>Limit Type:</strong><%=uF.showData(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT_TYPE"),"") %>,&nbsp;
											<%if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT_TYPE_ID")) == 2){ %>
												<strong>Limit Amount:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT"),"") %> per trip,&nbsp;
											<%} %>
											<strong>Lodging Type:</strong><%=uF.showData(hmClaimInner.get("REIMBURSEMENT_LODGING_TYPE"),"") %>
											<%if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_LODGING_TYPE_ID")) == 9){ %>
												,&nbsp;<strong>Lodging Limit Type:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_LODGING_LIMIT_TYPE"),"") %>
												<%if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_LODGING_LIMIT_TYPE_ID")) == 2){ %>
													,&nbsp;<strong>Limit Amount:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_LODGING_LIMIT"),"") %> per day
												<%} %>
											<%} %>											
										</div>
										<div style="margin-left: 39px;">
											<span><h4>Local Conveyance</h4><hr style="border: solid 1px #ececec;"></span>
											<strong>Transportation Type:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_LOCAL_CONVEYANCE_TRAN_TYPE"),"") %>,&nbsp;
											<strong>Conveyance Limit:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_LOCAL_CONVEYANCE_LIMIT"),"") %> per km
										</div>
										
										<div style="margin-left: 39px;">
											<span><h4>Food & Beverage (Non-Alcoholic)</h4><hr style="border: solid 1px #ececec;"></span>
											<strong>Limit Type:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_FOOD_LIMIT_TYPE"),"") %>
											<%if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_FOOD_LIMIT_TYPE_ID")) == 2){ %>
												,&nbsp;<strong>Limit Amount:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_FOOD_LIMIT"),"") %> per day
											<%} %>
										</div>
										<div style="margin-left: 39px;">
											<span><h4>Laundry</h4><hr style="border: solid 1px #ececec;"></span>
											<strong>Limit Type:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_LAUNDRY_LIMIT_TYPE"),"") %>
											<%if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_LAUNDRY_LIMIT_TYPE_ID")) == 2){ %>
												,&nbsp;<strong>Limit Amount:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_LAUNDRY_LIMIT"),"") %> per day
											<%} %>
										</div>
										<div style="margin-left: 39px;">
											<span><h4>Other Sundry</h4><hr style="border: solid 1px #ececec;"></span>
											<strong>Limit Type:</strong><%=uF.showData(hmClaimInner.get("REIMBURSEMENT_SUNDRY_LIMIT_TYPE"),"") %>
											<%if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_SUNDRY_LIMIT_TYPE_ID")) == 2){ %>
												,&nbsp;<strong>Limit Amount:</strong>&nbsp;<%=uF.showData(hmClaimInner.get("REIMBURSEMENT_SUNDRY_LIMIT"),"") %> per day
											<%} %>
										</div>
									</li>
								<%} %>
							</ul>							
						</li>
					</ul>
				</li>
			<%} %>
		</ul>
	</div>	
</div>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">&nbsp;</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>