<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String) session
			.getAttribute(IConstants.USERTYPE);
%>
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
		$("#strStartDate").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("#strEndDate").datepicker({
			format : 'dd/mm/yyyy'
		});
	});

	$(document)
			.ready(
					function() {
						<%-- $('#lt')
								.dataTable(
										{
											bJQueryUI : true,
											"sPaginationType" : "full_numbers",
											"aaSorting" : [],
											"sDom" : '<"H"lTf>rt<"F"ip>',
											oTableTools : {
												"sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
												aButtons : [
														"csv",
														"xls",
														{
															sExtends : "pdf",
															sPdfOrientation : "landscape"
														//sPdfMessage: "Your custom message would go here."
														}, "print" ]
											}
										}) --%>
						$('#lt').DataTable();
					});

	hs.graphicsDir= '<%=request.getContextPath()%>/images1/highslide/graphics/';
	hs.outlineType = 'rounded-white';
	hs.wrapperClassName = 'draggable-header';

	function getApprovalStatus(strId, empname) {

		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Approval Status of ' + empname);
		 $.ajax({
				url : "GetLeaveApprovalStatus.action?effectiveid="
						+ strId + "&type=3",
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});

	}

	function approveDeny(apStatus, strRequiId, requiStatus, strStartDate,
			strEndDate, userType) {
		var status = '';
		if (apStatus == '1') {
			status = 'approve';
		} else if (apStatus == '-1') {
			status = 'deny';
		}
		if (confirm('Are you sure, do you want to ' + status + ' this request?')) {
			var reason = window.prompt("Please enter your " + status
					+ " reason.");
			if (reason != null) {
				var action = 'RequisitionApprovalReport.action?operation=E&approveStatus=' + apStatus + '&strRequiId=' + strRequiId
						+ '&requiStatus=' + requiStatus + '&strStartDate=' + strStartDate + '&strEndDate=' + strEndDate + '&mReason=' + reason
						+ '&userType=' + userType;
				window.location = action;
			}
		}
}

</script>

<%-- 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Requisition Approval" name="title"/>
</jsp:include> --%>

	<%
		String currUserType = (String) request.getAttribute("currUserType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>

<section class="content">
	<% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		<div style="width: 100%;">
			<ul class="nav nav-pills">
				<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="RequisitionApprovalReport.action?currUserType=MYTEAM" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;">My Team</a>
				</li>
				<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"  style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="RequisitionApprovalReport.action?currUserType=<%=strBaseUserType %>" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
				</li>
			</ul>
		</div>
	<% } %>
	
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
				<s:form name="frm" action="RequisitionApprovalReport" theme="simple">
					<div class="box box-default collapsed-box">
						<div class="box-header with-border">
						    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
						    <div class="box-tools pull-right">
						        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						    </div>
						</div>
						<div class="box-body" style="padding: 5px; overflow-y: auto;">
							<div class="row row_without_margin" style="padding: 10px 0px;">
								<div class="col-lg-1 col-md-1 autoWidth" style="padding-right: 0px;">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-1 col-md-2 autoWidth" style="padding-right: 0px;">
									<p style="padding-left: 5px;">Status</p>
									<s:select theme="simple" cssClass="form-control autoWidth inline" name="checkStatus" id="checkStatus" headerKey="-2" headerValue="All Status" list="#{'1':'Approved', '-1':'Denied', '0':'Pending'}" cssStyle="width:110px" />
								</div>
								<% 
									String strStartDate = (String)request.getAttribute("strStartDate");
									String strEndDate = (String)request.getAttribute("strEndDate");
									if(strStartDate == null || strStartDate.equals("null") || strStartDate.equals("")) {
										strStartDate = "From Date";
									}
									if(strEndDate == null || strEndDate.equals("null") || strEndDate.equals("")) {
										strEndDate = "To Date";
									}
								%>
								<div class="col-lg-1 col-md-2 autoWidth" style="padding: 0px 9px;">
									<p style="padding-left: 5px;">From Date</p>
									<input type="text" name="strStartDate" id="strStartDate" value="<%=strStartDate %>" onblur="fillField(this.id, 3);" onclick="clearField(this.id);" style="width:90px" />
								</div>
								<div class="col-lg-1 col-md-2 autoWidth" style="padding: 0px 9px;">
									<p style="padding-left: 5px;">To Date</p>
									<input type="text" name="strEndDate" id="strEndDate" value="<%=strEndDate %>" onblur="fillField(this.id, 4);" onclick="clearField(this.id);" style="width:90px" />
								</div>
								<div class="col-lg-1 col-md-2 autoWidth" style="padding: 0px 9px;">
									<p style="padding-left: 5px;">&nbsp;</p>
									<s:submit value="Search" cssClass="btn btn-primary" cssStyle="margin:0px" />
								</div>
							</div>
						</div>
					</div>
				</s:form>

			<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
			<%session.setAttribute(IConstants.MESSAGE, ""); %>
				<!-- Place holder where add and delete buttons will be generated -->
				<br/>
				<table class="table" id="lt">
					<thead>
						<tr>
							<th style="text-align: center;">Employee Name</th>
							<th style="text-align: center;">Requisition Type</th>
							<th style="text-align: center;">Document Type</th>
							<th style="text-align: center;">Infrastructure Type</th>
							<th style="text-align: center;">Purpose</th>
							<th style="text-align: center;">Requisition Date</th>
							<th style="text-align: center;">From Date</th>
							<th style="text-align: center;">To Date</th>
							<th style="text-align: center;">Action</th>
							<th style="text-align: center;">Approving Profile</th>
							<th style="text-align: center;">Work Flow</th>
						</tr>
					</thead>
					<tbody>
						<%
							List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
							if(reportList.size() != 0){	
						for (int i = 0; reportList != null && i < reportList.size(); i++) {
								List<String> innerList = (List<String>) reportList.get(i);
						%>
						<tr>
							<td class="alignLeft"><%=innerList.get(2)%></td>
							<td class="alignLeft"><%=innerList.get(3)%></td>
							<td class="alignLeft"><%=innerList.get(4)%></td>
							<td class="alignLeft"><%=innerList.get(5)%></td>
							<td class="alignLeft"><%=innerList.get(6)%></td>
							<td class="alignCenter"><%=innerList.get(7)%></td>
							<td class="alignCenter"><%=innerList.get(8)%></td>
							<td class="alignCenter"><%=innerList.get(9)%></td>
							<td class="alignCenter"><%=innerList.get(10)%></td>
							<td class="alignCenter"><%=innerList.get(12)%></td>
							<td class="alignCenter"><%=innerList.get(11)%></td>
						</tr>
						<%
							}
							}else{
						%>
						<tr>
							<td colspan=11>
								No data available
							</td>
						</tr>
						<% } %>
					</tbody>
				</table>

				<%-- <div style="float:left;width:90%">
	<div><img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png">Waiting for approval</div>
	<div><img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/approved.png">Approved</div>
	<div><img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/denied.png">Denied</div>
	<div><img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pullout.png">Pull Out</div>
	<div><img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/act_now.png">Received</div>
</div> --%>
				<br/><br/>
				<div style="float: left; width: 50%">
					<div class="fieldset">
						<fieldset>
							<legend>Status</legend>
							<div>
								<%-- <img style="padding: 5px 5px 0 5px;" border="0"
									src="<%=request.getContextPath()%>/images1/icons/re_submit.png">Waiting
								for workflow --%>
								<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5; padding: 5px 5px 0 5px;"> </i>Waiting
								for workflow
							</div>
							<div>
								<%-- <img style="padding: 5px 5px 0 5px;" border="0"
									src="<%=request.getContextPath()%>/images1/icons/pending.png"> --%>
									<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5; padding: 5px 5px 0 5px;"> </i>
									Waiting	for approval
							</div>
							<div>
								 <%-- <img style="padding: 5px 5px 0 5px;" border="0"
									src="<%=request.getContextPath()%>/images1/icons/approved.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>Approved
							</div>
							<div>
								<%-- <img style="padding: 5px 5px 0 5px;" border="0"
									src="<%=request.getContextPath()%>/images1/icons/denied.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>Denied
							</div>
							<div style="border-bottom: 1px solid #CCCCCC;">
								<%-- <img style="padding: 5px 5px 0 5px;" border="0"
									src="<%=request.getContextPath()%>/images1/icons/pullout.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>Pull
								Out
							</div>
							<div>
								 <%-- <img style="padding: 5px 5px 0 5px;" border="0"
									src="<%=request.getContextPath()%>/images1/icons/icons/approve_icon.png"> --%>
									<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900;padding: 5px 5px 0px 5px;" ></i>Approve
							</div>
							<div>
								 <%-- <img style="padding: 5px 5px 0 5px; width: 16px;" border="0"
									src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"> --%>
									<i class="fa fa-times-circle cross" aria-hidden="true" style="padding: 5px 5px 0 5px;"></i>Deny
							</div>
						</fieldset>
					</div>
				</div>
		</div>
		<!-- /.box-body -->
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
