<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page
	import="com.konnect.jpms.export.DataStyle,com.itextpdf.text.BaseColor,com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<style>
.desgn {
	padding: 0px;
	border: 1px solid #ccc;
}
</style>

<%
	UtilityFunctions uF = new UtilityFunctions();

	Map hmEmpNameMap = (Map) request.getAttribute("hmEmpNameMap");
	Map hmEmpCodeMap = (Map) request.getAttribute("hmEmpCodeMap");
	Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");
	Map hmEmpSalary = (Map) request.getAttribute("hmEmpSalary");
	LinkedHashMap hmTotalSalary = (LinkedHashMap) request.getAttribute("hmTotalSalary");
	Map hmEmpPaymentMode = (Map) request.getAttribute("hmEmpPaymentMode");
	Map hmPaymentModeMap = (Map) request.getAttribute("hmPaymentModeMap");
	Map hmLoanPoliciesMap = (Map) request.getAttribute("hmLoanPoliciesMap");
	
	Map hmPresentDays = (Map) request.getAttribute("hmPresentDays");
	Map hmLeaveDays = (Map) request.getAttribute("hmLeaveDays");
	Map hmLeaveTypeDays = (Map) request.getAttribute("hmLeaveTypeDays");
	
	Map<String, String> hmPaidDays = (Map<String, String>) request.getAttribute("hmPaidDays");
	if(hmPaidDays == null) hmPaidDays = new HashMap<String, String>();
	
	//String strTotalDays = (String) request.getAttribute("strTotalDays");
	Map<String, String> hmTotalDays = (Map<String, String>)request.getAttribute("hmTotalDays");   
	if(hmTotalDays==null) hmTotalDays = new HashMap<String, String>();
	
	List alEmp = (List) request.getAttribute("alEmp");
	List alEmpSalaryDetailsEarning = (List) request.getAttribute("alEmpSalaryDetailsEarning");
	List alEmpSalaryDetailsDeduction = (List) request.getAttribute("alEmpSalaryDetailsDeduction");
	List alEmpIdPayrollG = (List) request.getAttribute("alEmpIdPayrollG");
	Map hmEmpLoan = (Map) request.getAttribute("hmEmpLoan");
	
	List alLoans = (List) request.getAttribute("alLoans");
	
	Map<String, String> hmWoHLeaves = (Map<String, String>) request.getAttribute("hmWoHLeaves");
	if(hmWoHLeaves == null) hmWoHLeaves = new HashMap<String, String>();
	
	if (alEmpSalaryDetailsEarning == null)
		alEmpSalaryDetailsEarning = new ArrayList();
	if (alEmpSalaryDetailsDeduction == null)
		alEmpSalaryDetailsDeduction = new ArrayList();
	if (alEmpIdPayrollG == null)
		alEmpIdPayrollG = new ArrayList();
	if (alLoans == null)
		alLoans = new ArrayList();
	
	String strR = (String) request.getParameter("R");
	
	List<String> alEmpJoinDate = (List<String>)request.getAttribute("alEmpJoinDate");
	if(alEmpJoinDate == null) alEmpJoinDate = new ArrayList<String>();
	Map<String, String> hmEmpJoiningMap = (Map<String, String>)request.getAttribute("hmEmpJoiningMap");   
	if(hmEmpJoiningMap==null) hmEmpJoiningMap = new HashMap<String, String>();
	
	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
	
	//out.println("<br/>hmPresentDays===>"+hmPresentDays);
	//	out.println("<br/>alEmpSalaryDetailsEarning===>"+alEmpSalaryDetailsEarning);
	//	out.println("<br/>hmSalaryDetails===>"+hmSalaryDetails);
	
	//	out.println("<br/>alEmpSalaryDetailsDeduction===>"+alEmpSalaryDetailsDeduction);
	//	out.println("<br/>alEmpIdPayroll===>"+alEmpIdPayrollG);
	//	out.println("<br/>hmSalaryDetails===>"+hmSalaryDetails); 
	
	List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
	List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(function(){
	$("#wLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#level").multiselect().multiselectfilter();
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

			$(document).ready( function () {
				<%-- $('#lt').dataTable({ bJQueryUI: true, 
					"sPaginationType": "full_numbers",
					"iDisplayLength": 1000,
					"aLengthMenu": [
					                [1, 2, -1],
					                [1, 2, "All"]
					            ],
					"aaSorting": [[0, 'asc']],
					/* "sDom": '<"H"lTf>rt<"F"ip>', */
					"sDom": '<"H"f>rt<"F"ip>',
					oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
						aButtons: [
							"csv", "xls", {
								sExtends: "pdf",
								sPdfOrientation: "landscape"
								//sPdfMessage: "Your custom message would go here."
								}, "print" 
						]
					}
					}); --%>
					$('#lt').DataTable();
				});

function selectall(x,strEmpId){
	
	 	var  status=x.checked;
		var  arr= document.getElementsByName(strEmpId);
			for(i=0;i<arr.length;i++)
		 	{
		  		arr[i].checked=status;
		 	}
 
}
function exportpdf(){
  window.location="ExportExcelReport.action";
}

function viewExceptions(empName,absent,empId,paycycle) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Exceptions of '+empName);
	$.ajax({
		url : "ViewException.action?absent="+absent+"&strEmpId="+empId+"&paycycle="+paycycle,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function submitForm(type){
	if(type == '1'){
		document.getElementById("paycycle").selectedIndex = "0";
	} else if(type == '3'){
		document.getElementById("paycycle").selectedIndex = "0";
	}
	document.frm_approve_payroll.submit();
}

</script>




<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Approve Compensation" name="title"/>
</jsp:include> --%>

<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<div id="printDiv" class="leftbox reportWidth">

				<div class="">
					<div class="box box-primary collapsed-box"
						style="border-top-color: #EEEEEE; margin-top: 10px;">

						<div class="box-header with-border">
							<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-plus"></i>
								</button>
								<button class="btn btn-box-tool" data-widget="remove">
									<i class="fa fa-times"></i>
								</button>
							</div>
						</div>
						<!-- /.box-header -->
						<div class="box-body"
							style="padding: 5px; overflow-y: auto; display: none;">
							<div class="content1">

								<!-- <div class="filter_div"> -->
								<!-- <div class="filter_caption">Filter</div> -->
								<s:form theme="simple" method="post" name="frm_approve_payroll"
									id="frm_approve_payroll">
									<div style="width: 100%; float: left;">
										<div
											style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">Duration</p>
											<s:select theme="simple" name="strPaycycleDuration"
												listKey="paycycleDurationId"
												listValue="paycycleDurationName" cssStyle="width:80px"
												onchange="submitForm('3');" list="paycycleDurationList"
												key="" cssClass="form-control autoWidth" />
										</div>
										<div
											style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">Organisation</p>
											<s:select theme="simple" name="f_org" id="f_org"
												listKey="orgId" listValue="orgName" cssStyle="width:140px"
												onchange="submitForm('1');" list="organisationList" key=""
												 cssClass="form-control autoWidth" />
										</div>
										<div
											style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">Paycycle</p>
											<s:select label="Select PayCycle" name="paycycle"
												id="paycycle" listKey="paycycleId" listValue="paycycleName"
												headerKey="" headerValue="Select Paycycle"
												onchange="submitForm('2');" list="paycycleList" key=""
												cssStyle="width: 120px;" cssClass="form-control autoWidth"/>
										</div>

										<div
											style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">Payment Mode</p>
											<s:select theme="simple" name="f_paymentMode"
												listKey="payModeId" listValue="payModeName" headerKey="-1"
												headerValue="All Modes" onchange="submitForm('2');"
												list="paymentModeList" key="" cssStyle="width:100px"
												cssClass="form-control autoWidth" />
										</div>
									</div>
									<div>
										<div
											style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">Location</p>
											<s:select theme="simple" name="wLocation" id="wLocation"
												listKey="wLocationId" cssStyle="width:140px"
												listValue="wLocationName" multiple="true"
												list="wLocationList" key="" />
										</div>
										<div
											style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">Department</p>
											<s:select name="f_department" id="f_department"
												list="departmentList" listKey="deptId"
												cssStyle="width:140px" listValue="deptName" multiple="true"></s:select>
										</div>
										<div
											style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">Service</p>
											<s:select name="f_service" id="f_service" list="serviceList"
												listKey="serviceId" listValue="serviceName" multiple="true"
												cssStyle="width:100px"></s:select>
										</div>
										<div
											style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">Level</p>
											<s:select theme="simple" name="level" id="level"
												listKey="levelId" listValue="levelCodeName" multiple="true"
												list="levelList" key="" cssStyle="width:100px" />
										</div>

										<div
											style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">&nbsp;</p>
											<input type="hidden" name="filterType" value="filter" />
											<%-- <s:submit value="Submit" cssClass="btn btn-primary"cssStyle="margin:0px" /> --%>
											<input type="button" name="Submit" value="Submit"
												class="btn btn-primary" style="margin: 0px"
												onclick="submitForm('2');" />
										</div>

										<div
											style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
											<p style="padding-left: 5px;">&nbsp;</p>
											<input type="button" value="Reset" class="btn btn-info"
												style="margin: 0px"
												onclick="JavaScript:window.location.href = window.location.href;" />
										</div>
										<!-- <a href="javascript:void(0)"
						style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right; width: 30px; position: static;"
						title="Export to Excel" class="excel" onclick="exportpdf();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
									</div>
								</s:form>
								<!-- </div> -->
							</div>
						</div>
						<!-- /.box-body -->
					</div>

				</div>
				 <!-- <a href="javascript:void(0)"
					style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right; width: 30px; position: static;"
					title="Export to Excel" class="excel" onclick="exportpdf();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
					
					<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
					

				<%-- <p style="padding-left: 5px;"><%=(String)request.getAttribute("selectedFilter") %></p> --%>

				<%
					String strDiv = "width:100%;float:left;";
					if (hmSalaryDetails != null && hmSalaryDetails.size() > 0) {
						strDiv = "width:100%; overflow:scroll;";
					}
				%>

				<div style="<%=strDiv%>">

					<s:form action="ApprovePayroll" method="post">
						<input type="hidden" name="f_paymentMode"
							value="<%=request.getAttribute("f_paymentMode")%>" />

						<%
							if (strR == null) {
						%>


						<input type="hidden" name="approve" value="approve" onclick="return confirm('Are you sure you wish to approve payroll for selected employees?')" />
						<input type="hidden" name="f_org" value="<%=request.getParameter("f_org")%>" />
						<input type="hidden" name="approvePC" value="<%=request.getParameter("paycycle")%>" />
						<input type="hidden" name="strPaycycleDuration" value="<%=request.getParameter("strPaycycleDuration")%>" />
						<input type="hidden" name="wLocation" value="<%=request.getParameter("wLocation")%>" />
						<input type="hidden" name="level" value="<%=request.getParameter("level")%>" />
						<input type="hidden" name="f_service" value="<%=request.getParameter("f_service")%>" />
						<input type="hidden" name="f_department" value="<%=request.getParameter("f_department")%>" />	

						<%
							}
						%>


						<%
							if (hmSalaryDetails != null && hmSalaryDetails.size() > 0) {
						%>
						<input type="submit" value="Approve" class="btn btn-primary"
							style="margin-bottom: 10px"
							onclick="return confirm('Are you sure you wish to approve payroll for selected employees?')" />
						<%
							}
						%>

						<table id="lt" cellpadding="2" cellspacing="2" border="0"
							class="table table-bordered" width="100%">
							<%
								if (hmSalaryDetails != null && hmSalaryDetails.size() > 0
											&& alEmpSalaryDetailsEarning.size() > 0) {
							%>
							<thead>
								<tr>
									<th class="alignCenter" nowrap>Employee Code</th>
					<th class="alignCenter" nowrap>Employee Name</th>
					<th class="alignCenter" nowrap>Approve<br/><input type="checkbox" onclick="selectall(this,'chbox')" checked="checked"/></th>
					<th class="alignLeft" nowrap>Payment Mode</th>
					<th class="alignCenter" nowrap>Total Days</th>
					<th class="alignCenter" nowrap>Paid Days</th>
					<th class="alignCenter" nowrap>Present</th>
					<th class="alignCenter" nowrap>Leaves</th>
					<th class="alignCenter" nowrap>Absent/Unpaid</th>
					
					<th class="alignCenter" nowrap>Net Pay</th>
					<th class="alignCenter" nowrap>Gross Pay</th>
					  
					<!-- generation of list for pdf -->
					<%
						alInnerExport.add(new DataStyle("Payroll for paycycle " + request.getAttribute("strD1") + " - " + request.getAttribute("strD2"),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Approve", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Payment Mode", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Total Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Paid Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Present", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Leaves", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Absent/Unpaid", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Net Pay", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Gross Pay", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					%>
					
					<%
						for (int i = 0; i < alEmpSalaryDetailsEarning.size(); i++) {
					%>
						<th class="alignCenter" nowrap>
							<%=(String) hmSalaryDetails.get((String) alEmpSalaryDetailsEarning.get(i))%>
							<br/>(+)
						</th>
						<%
							alInnerExport.add(new DataStyle(((String) hmSalaryDetails.get((String) alEmpSalaryDetailsEarning.get(i))) + "(+)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						%>
					<%
						}
					%>
					
					
					<%
																for (int i = 0; i < alEmpSalaryDetailsDeduction.size(); i++) {
															%>
						
						
						<%
																			if (uF.parseToInt((String) alEmpSalaryDetailsDeduction.get(i)) == IConstants.LOAN && hmEmpLoan != null) {
																		%>
							<%
								for (int l = 0; l < alLoans.size(); l++) {
							%>
							<th class="alignCenter" nowrap>
								<%=hmLoanPoliciesMap.get((String) alLoans.get(l))%>
								<br/>(-)
						</th>
						<%
							alInnerExport.add(new DataStyle((hmLoanPoliciesMap.get((String) alLoans.get(l))) + "(-)", Element.ALIGN_CENTER, "NEW_ROMAN", 6,
														"0", "0", BaseColor.LIGHT_GRAY));
						%>
							<%
								}
							%>
						<%
							} else {
						%>
						<th class="alignCenter" nowrap>
							<%=(String) hmSalaryDetails.get((String) alEmpSalaryDetailsDeduction.get(i))%>
							<br/>(-)
						</th>
						<%
							alInnerExport.add(new DataStyle(((String) hmSalaryDetails.get((String) alEmpSalaryDetailsDeduction.get(i))) + "(-)",
													Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						%>
						<%
							}
						%>
						
						
					<%
							}
							%>
							</tr>

						</thead>
							<%
								reportListExport.add(alInnerExport);
							%>
							<%
								}
							%>


							<tbody>
								<%
			  						double dblNet = 0;
									double dblGross = 0;
									Map<String, String> totalSalaryHead = new HashMap<String, String>();
			
									/* Set set0 = hmTotalSalary.keySet();
									Iterator it0 = set0.iterator();
									while(it0.hasNext()){
										String strEmpId = (String)it0.next();
										Map hmInner = (Map)hmTotalSalary.get(strEmpId); */
			
									for (int eid = 0; eid < alEmp.size() && alEmpSalaryDetailsEarning.size() > 0; eid++) {
										String strEmpId = (String) alEmp.get(eid);
										Map hmInner = (Map) hmTotalSalary.get(strEmpId);
										if (hmInner == null)
											hmInner = new HashMap();
			
										Map hmLeaves = (Map) hmLeaveDays.get(strEmpId);
										if (hmLeaves == null)
											hmLeaves = new HashMap();
			
										Map hmLeavesType = (Map) hmLeaveTypeDays.get(strEmpId);
										if (hmLeavesType == null)
											hmLeavesType = new HashMap();
			  					%>
								<%
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle((String) hmEmpCodeMap.get(strEmpId), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle((String) hmEmpNameMap.get(strEmpId), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								%>
								<%
									String strTotalDays = hmTotalDays.get(strEmpId); 
									if(alEmpJoinDate.contains(strEmpId) && uF.parseToInt(uF.getDateFormat(hmEmpJoiningMap.get(strEmpId),IConstants.DATE_FORMAT,"dd")) > 1){
										 strTotalDays = hmPaidDays.get(strEmpId);
									}
									double nAbsent = uF.parseToDouble(strTotalDays) - uF.parseToDouble((String) hmPresentDays.get(strEmpId)) - uF.parseToDouble((String) hmLeavesType.get("COUNT"));
								%>
								<tr>
									<td class=" alignLeft" nowrap><%=(String) hmEmpCodeMap.get(strEmpId)%></td>
									<td class=" alignLeft" nowrap>
										<%=(String) hmEmpNameMap.get(strEmpId)%>
										<%if(nAbsent > 0){ %>
											<div style="background-position:top;float:right;width:10px;">
												<a href="javascript:void(0)" onclick="viewExceptions('<%=(String) hmEmpNameMap.get(strEmpId)%>',<%=nAbsent %>,<%=strEmpId %>,'<%=request.getAttribute("paycycle")%>')">
													<img style="width: 9px; margin-left: 3px;" src="images1/icons/popup_arrow.gif">
												</a>
											</div>
										<%} %>
									</td>
									
									<td class="alignCenter">
									<%
										if (!alEmpIdPayrollG.contains(strEmpId)) {
											if (strR != null) {
									%>
												Pending
									<%
											} else {
									%>
												<input type="checkbox" name="chbox" style="width:10px; height:10px" value="<%=strEmpId%>" checked="checked" />
										<%
											}
										%>
									<%
											alInnerExport.add(new DataStyle("Pending", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									%>
									<%
										} else {
									%>		Approved
									<%
										alInnerExport.add(new DataStyle("Approved", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									%>
									<%
										}
									%>
									</td>
									<%
										alInnerExport.add(new DataStyle((String) hmPaymentModeMap.get((String) hmEmpPaymentMode.get(strEmpId)), Element.ALIGN_CENTER, "NEW_ROMAN",6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(strTotalDays)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmPaidDays.get(strEmpId))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble((String) hmPresentDays.get(strEmpId))), Element.ALIGN_CENTER, "NEW_ROMAN",6, "0", "0", BaseColor.LIGHT_GRAY));
										String strWoHLeaves = uF.parseToDouble(hmWoHLeaves.get(strEmpId)) > 0.0d && uF.parseToDouble(hmWoHLeaves.get(strEmpId)) > uF.parseToDouble((String) hmLeavesType.get("COUNT")) ? "("+uF.parseToDouble(hmWoHLeaves.get(strEmpId))+")" : ""; 
										alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble((String) hmLeavesType.get("COUNT")))+" "+strWoHLeaves, Element.ALIGN_CENTER, "NEW_ROMAN", 6,"0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle((((nAbsent < 0) ? 0 : nAbsent)) + "", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmInner.get("NET"))), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmInner.get("GROSS"))), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			
										dblNet += uF.parseToDouble((String) hmInner.get("NET"));
										dblGross += uF.parseToDouble((String) hmInner.get("GROSS"));
									%>
									
									
									<td class="alignCenter"><input type="hidden" name="paymentMode" value="<%=uF.parseToInt((String) hmEmpPaymentMode.get(strEmpId))%>"><%=uF.showData((String) hmPaymentModeMap.get((String) hmEmpPaymentMode.get(strEmpId)), "N/a")%></td>
									<td class="alignCenter"><%=uF.parseToDouble(strTotalDays)%></td>
									<td class="alignCenter"><%=uF.parseToDouble(hmPaidDays.get(strEmpId))%></td>
									<td class="alignCenter"><%=uF.parseToDouble((String) hmPresentDays.get(strEmpId))%></td>
									<td class="alignCenter" nowrap="nowrap"><%=uF.parseToDouble((String) hmLeavesType.get("COUNT")) +" "+strWoHLeaves%></td>
									<td class="alignCenter"><%=((nAbsent < 0) ? 0 : nAbsent)%></td>
									<td class="alignRight" nowrap="nowrap"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition), uF.parseToDouble((String)hmInner.get("NET")))%></td>
									<td class="alignRight"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition), uF.parseToDouble((String) hmInner.get("GROSS")))%></td>
									
									
								
								<%
									for (int i = 0; i < alEmpSalaryDetailsEarning.size(); i++) {
										String strAmount = (String) hmInner.get((String) alEmpSalaryDetailsEarning.get(i));
			
										double earningHead = uF.parseToDouble("" + hmInner.get(alEmpSalaryDetailsEarning.get(i))) + uF.parseToDouble(totalSalaryHead.get(alEmpSalaryDetailsEarning.get(i)));
										totalSalaryHead.put("" + alEmpSalaryDetailsEarning.get(i), earningHead + "");
								%>
								<%
									alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount)), Element.ALIGN_CENTER, "NEW_ROMAN", 6,"0", "0", BaseColor.LIGHT_GRAY));
								%>
									<td class="alignRight">
										<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount))%>
									</td>
								<%
									}
								%>
								
								<%
									for (int i = 0; i < alEmpSalaryDetailsDeduction.size(); i++) {
										String strAmount = (String) hmInner.get((String) alEmpSalaryDetailsDeduction.get(i));
										
										if (uF.parseToInt((String) alEmpSalaryDetailsDeduction.get(i)) == IConstants.LOAN && hmEmpLoan != null) {
											Map hmEmpLoanInner = (Map) hmEmpLoan.get(strEmpId);
											if (hmEmpLoanInner == null)hmEmpLoanInner = new HashMap();
										%>
										<%
											for (int l = 0; l < alLoans.size(); l++) {
										%>
										<td class="alignRight">
											<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition), uF.parseToDouble((String) hmEmpLoanInner.get((String) alLoans.get(l))))%>
											<%
												double deductionHead = uF.parseToDouble((String) hmEmpLoanInner.get((String) alEmpSalaryDetailsDeduction.get(i) + "_"
																			+ (String) alLoans.get(l)))
																			+ uF.parseToDouble(totalSalaryHead.get(alEmpSalaryDetailsDeduction.get(i)));
												totalSalaryHead.put((String) alEmpSalaryDetailsDeduction.get(i) + "_" + (String) alLoans.get(l), deductionHead + "");
			
												alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmEmpLoanInner.get((String) alLoans.get(l)))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
											%>
											</td>
										<%
											}
										%>
									<%
										} else {
									%>
									<td class="alignRight">
										<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount))%>
										<%
											double deductionHead = uF.parseToDouble(strAmount) + uF.parseToDouble(totalSalaryHead.get(alEmpSalaryDetailsDeduction.get(i)));
											totalSalaryHead.put((String) alEmpSalaryDetailsDeduction.get(i), deductionHead + "");
			
											alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount)), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										%>
										</td>
									<%
										}
									%>
										
									
								<%
																						}
																					%>
								</tr>
								<%
									reportListExport.add(alInnerExport);
								}
			
										alInnerExport = new ArrayList<DataStyle>();
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("Total", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition), Math.round(dblNet)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition), Math.round(dblGross)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			
										for (int i = 0; i < alEmpSalaryDetailsEarning.size(); i++) {
											alInnerExport.add(new DataStyle(""+ uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(uF.showData(totalSalaryHead.get(alEmpSalaryDetailsEarning.get(i)), "0"))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										}
			
										for (int i = 0; i < alEmpSalaryDetailsDeduction.size(); i++) {
			
											if (uF.parseToInt((String) alEmpSalaryDetailsDeduction.get(i)) == IConstants.LOAN && hmEmpLoan != null) {
												for (int l = 0; l < alLoans.size(); l++) {
			
													alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(uF.showData(totalSalaryHead.get((String) alEmpSalaryDetailsDeduction.get(i) + "_" + (String) alLoans.get(l)), "0"))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
												}
											} else {
												alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(uF.showData(totalSalaryHead.get((String) alEmpSalaryDetailsDeduction.get(i)), "0"))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0","0", BaseColor.LIGHT_GRAY));
											}
										}
										reportListExport.add(alInnerExport);
								%>
								
								
								<%-- <%if(alEmpSalaryDetailsEarning.size()==0 && alEmpSalaryDetailsDeduction.size()==0){ %> --%>
								<%
									if (alEmp.size() == 0 && alEmp.size() == 0) {
								%>
								<tr><td colspan="10" class="msg nodata"><span>No employee found for payroll</span></td></tr>
								<%
									}
								%>


							</tbody>
						</table>


						<%
							if (hmSalaryDetails != null && hmSalaryDetails.size() == 0) {
						%>
						<div class="nodata msg" style="width: 97%">
							<span>No employees found.</span>
						</div>
						<%
							}
						%>


					</s:form>
					<%
						session.setAttribute("reportListExport", reportListExport);
					%>
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