<%@page import="java.util.Iterator"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%
	UtilityFunctions uF = new UtilityFunctions(); 
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	Map<String, String> hmSalaryDetails = (Map<String, String>)request.getAttribute("hmSalaryDetails");
	if(hmSalaryDetails == null) hmSalaryDetails = new HashMap<String, String>();
	List<String> alEmpSalaryDetailsEarning = (List<String>)request.getAttribute("alEmpSalaryDetailsEarning");
	if(alEmpSalaryDetailsEarning == null) alEmpSalaryDetailsEarning = new ArrayList<String>();
	List<String> alEmpSalaryDetailsDeduction = (List<String>)request.getAttribute("alEmpSalaryDetailsDeduction");
	if(alEmpSalaryDetailsDeduction == null) alEmpSalaryDetailsDeduction = new ArrayList<String>();
	
	String pageFrom = (String) request.getAttribute("pageFrom");
	
	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
	
	Map<String, Map<String, String>> hmPaycyclewiseArrearAmt = (Map<String, Map<String, String>>) request.getAttribute("hmPaycyclewiseArrearAmt");
	if(hmPaycyclewiseArrearAmt == null) hmPaycyclewiseArrearAmt = new HashMap<String, Map<String, String>>();
	Map<String, Map<String, String>> hmPaycyclewiseAttendanceData = (Map<String, Map<String, String>>) request.getAttribute("hmPaycyclewiseAttendanceData");
	if(hmPaycyclewiseAttendanceData == null) hmPaycyclewiseAttendanceData = new HashMap<String, Map<String, String>>();
	
%>

<script type="text/javascript">

$(document).ready(function() {
	$('#lt').DataTable({
		aLengthMenu: [
			[25, 50, 100, 200, -1],
			[25, 50, 100, 200, "All"]
		],
		iDisplayLength: -1,
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ],
        order: [],
		columnDefs: [ {
	      "targets"  : 'no-sort',
	      "orderable": false
	    }]
	});
});


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
	
</script>

	 <div class="box-body" style="width:100%; padding:5px; overflow-y:auto; min-height:600px;">
		<%
		String strDiv = "width:100%; float:left;";
		if (hmSalaryDetails != null && hmSalaryDetails.size() > 0) {
			strDiv = "width:100%; overflow:auto; height:400px;";
		}
		%>
		<div style="width:100%; padding: 20px; font-size: 16px; font-style: italic;"><%=uF.showData((String)request.getAttribute("STRUTURE_CHANGED_MSG"), "") %> </div>
		
		<div class="clr margintop20"></div>
		<div style="<%=strDiv %>">
			<input type="hidden" name="approvePC" id="approvePC" value="<%=request.getParameter("paycycle")%>" />
			<table class="table table-bordered" id ="lt" ><!-- id ="lt" -->
				<thead>
					<tr>
						<th class="alignCenter" nowrap>Paycycle</th>
						<th class="alignCenter" nowrap>Month</th>
						<th class="alignLeft" nowrap>Payment Mode</th>
						<th class="alignCenter" nowrap>Total Days</th>
						<th class="alignCenter" nowrap>Paid Days</th>
						<th class="alignCenter" nowrap>Present</th>
						<th class="alignCenter" nowrap>Leaves</th>
						<th class="alignCenter" nowrap>Absent/Unpaid</th>
						<!-- <th class="alignCenter" nowrap>Net Pay</th>
						<th class="alignCenter" nowrap>Gross Pay</th> -->
						<% for (int i = 0; i < alEmpSalaryDetailsEarning.size(); i++) { %>
							<th class="alignCenter" nowrap>
								<%=(String) hmSalaryDetails.get(alEmpSalaryDetailsEarning.get(i))%>
								<br/>(+)
							</th>
						<%
						}
						for (int i = 0; i < alEmpSalaryDetailsDeduction.size(); i++) {
						%>
							<th class="alignCenter" nowrap>
								<%=hmSalaryDetails.get(alEmpSalaryDetailsDeduction.get(i))%>
								<br/>(-)
							</th>
						<% } %>
					</tr>
				</thead>
				<tbody>
				<%
						/* double dblNet = 0;
						double dblGross = 0; */
						Map<String, String> totalSalaryHead = new HashMap<String, String>();
					
						/* int nEmpSize = alEmp.size();
						for (int i = 0; i < nEmpSize; i++) { */
						Iterator<String> it = hmPaycyclewiseArrearAmt.keySet().iterator();
						while(it.hasNext()) {
							String strPaycycle = it.next();
							
							Map<String, String> hmEmpPay = hmPaycyclewiseAttendanceData.get(strPaycycle);
							if(hmEmpPay == null) hmEmpPay = new HashMap<String, String>();
							String strEmpId = hmEmpPay.get("EMP_ID");
							
							Map<String, String> hmInner = hmPaycyclewiseArrearAmt.get(strPaycycle);
							if (hmInner == null) hmInner = new HashMap<String, String>();
							
							/* dblNet += uF.parseToDouble((String) hmInner.get("NET"));
							dblGross += uF.parseToDouble((String) hmInner.get("GROSS")); */
							
					%>
							<tr>
								<td class="alignLeft" nowrap><%=hmEmpPay.get("EMP_PAYCYCLE")%></td>
								<td class="alignLeft" nowrap><%=hmEmpPay.get("EMP_PAYCYCLE_MONTH")%></td>
								<td class="alignLeft"><input type="hidden" name="paymentMode" value="<%=uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID"))%>"/><%=hmEmpPay.get("EMP_PAYMENT_MODE") %></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"))%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"))%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"))%></td>
								<td class="alignCenter" nowrap="nowrap"><%=uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"))%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_ABSENT_DAYS"))%></td>
								<%-- <td class="alignRight" nowrap="nowrap"><%= uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(hmInner.get("NET")))))%></td>
								<td class="alignRight" nowrap="nowrap"><%=  uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(hmInner.get("GROSS")))))%></td> --%>
								<%
									for (int j = 0; j < alEmpSalaryDetailsEarning.size(); j++) {
										String strAmount = hmInner.get(alEmpSalaryDetailsEarning.get(j));
										double earningHead = uF.parseToDouble(hmInner.get(alEmpSalaryDetailsEarning.get(j))) + uF.parseToDouble(totalSalaryHead.get(alEmpSalaryDetailsEarning.get(j)));
										totalSalaryHead.put(alEmpSalaryDetailsEarning.get(j), earningHead + "");
								%>
										<td class="alignRight"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount))%></td>
								<%
									}
									for (int k = 0; k < alEmpSalaryDetailsDeduction.size(); k++) {
										String strAmount = (String) hmInner.get(alEmpSalaryDetailsDeduction.get(k));
										double deductionHead = uF.parseToDouble(strAmount) + uF.parseToDouble(totalSalaryHead.get(alEmpSalaryDetailsDeduction.get(k)));
										totalSalaryHead.put(alEmpSalaryDetailsDeduction.get(k), deductionHead + "");
											
								%>
										<td class="alignRight"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount))%></td>
								<% } %>
							</tr>							
						<% } %>
				</tbody>
			</table>
		</div>
	</div>

<script type="text/javascript">
checkRevokeAll();
checkAll();
</script>