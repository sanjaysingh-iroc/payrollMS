<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>


<%
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);

	UtilityFunctions uF = new UtilityFunctions();
	List reportListPrint = (List) request.getAttribute("reportListPrint");
	Map hmEmpDetails = (Map) request.getAttribute("hmEmpDetails");
	List alPrevLeaveDetails = (List) request.getAttribute("alPrevLeaveDetails");
	List alLeaveDetails = (List) request.getAttribute("alLeaveDetails");
	List alLeaveEntitlementDetails = (List) request.getAttribute("alLeaveEntitlementDetails");
	
//	int nRowSpan = (alLeaveDetails.size()/4) + 2;
	int nRowSpan = (alLeaveEntitlementDetails.size()/2) + 2;
	
%>
 

<!-- Custom form for adding new records -->

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TLeaveCard %>" name="title" />
</jsp:include>



<div class="leftbox reportWidth">





	<div class="filter_div">
		<div class="filter_caption">Filter</div>

		<s:form theme="simple" id="selectLevel" action="LeaveCard"
			method="post" name="frm" cssClass="formcss"
			enctype="multipart/form-data">

			<s:hidden name="empId"></s:hidden>
			<s:select theme="simple" name="strYear" listKey="yearsID"
				cssStyle="width: 65px;" listValue="yearsName" headerKey="0"
				list="yearList" key="" onchange="document.frm.submit();" />

		</s:form>

	</div>





	<table class="tb_style" width="100%">
		<tr>
			<th align="left">Name:</th>
			<td colspan="5"><%=hmEmpDetails.get("EMP_NAME")%></td>

			<td colspan="3" rowspan="<%=nRowSpan%>" valign="top" style="padding:0">

				<table class="tb_style" width="100%">
					<tr>
						<th colspan="5">Current year leave status</th>
					</tr>
					
					<tr>
						<th>Leave Type</th>
						<th>Balance</th>
						<th>Accrued</th>
						<th>Paid</th>
						<th>Unpaid</th>
					</tr>

					<%
						int x0 = 0;
						for (x0 = 0; alLeaveDetails != null && x0 < alLeaveDetails.size(); x0++) {
					%>
					<tr>
						<th align="right"><%=alLeaveDetails.get(x0++)%></th>
						<td><%=alLeaveDetails.get(x0++)%> days</td>
						<td><%=alLeaveDetails.get(x0++)%> days</td>
						<td><%=alLeaveDetails.get(x0++)%> days</td>
						<td><%=alLeaveDetails.get(x0)%> days</td>
					</tr>
					<%
						}
					%>


				</table>

			</td>

		</tr>
		<tr>
			<th align="left">Department:</th>
			<td colspan="5"><%=hmEmpDetails.get("EMP_DEPT")%></td>
		</tr>

		<tr>
			<th colspan="6" align="left">Leave Balance for previous Year: <s:property
					value="strPrevYear" /></th>

		</tr>


		<%
			int x = 0;
			for (x = 0; alPrevLeaveDetails != null && x < alPrevLeaveDetails.size(); x++) {
		%>
		<tr>
			<th colspan="4" align="right"><%=alPrevLeaveDetails.get(x++)%></th>
			<td colspan="2"><%=alPrevLeaveDetails.get(x)%> days</td>
		</tr>
		<%
			}
			if (x == 0) {
		%>



		<tr>
			<td colspan="5" align="center">No Leave assigned for the year: <s:property
					value="strPrevYear" />
			</td>
			<td>&nbsp;</td>
		</tr>

		<%
			}
		%>


		<tr>
			<th colspan="6" align="left">Leave Entitlement for Year: <s:property
					value="strYear" /></th>

		</tr>

		<%
			int y = 0;
			for (y = 0; alLeaveEntitlementDetails != null && y < alLeaveEntitlementDetails.size(); y++) {
		%>
		<tr>
			<th colspan="4" align="right"><%=alLeaveEntitlementDetails.get(y++)%></th>
			<td colspan="2"><%=alLeaveEntitlementDetails.get(y)%> days</td>
		</tr>
		<%
			}
			if (y == 0) {
		%>


		<tr>
			<td colspan="6" align="center">No Leave assigned for the year: <s:property
					value="strPrevYear" />
			</td>
			
		</tr>
		<%
			}
		%>




		<tr>
			<th width="10%">Applied on</th>
			<th width="10%">Start Date</th>
			<th width="10%">End Date</th>
			<th width="10%">Half Day?</th>
			<th width="10%">Leave Type</th>
			<th width="20%">Leave Applied for</th>
			<th width="5%">No. of days</th>
			<th width="5%">Balance</th>
			<th width="20%">Comments</th>
		</tr>


		<%
		int i = 0;
			for (i = 0; reportListPrint!=null && i < reportListPrint.size(); i++) {
				List alInner = (List) reportListPrint.get(i);
		%>

		<tr>
			<td><%=alInner.get(0)%></td>
			<td><%=alInner.get(1)%></td>
			<td><%=alInner.get(2)%></td>
			<td><%=alInner.get(3)%></td>
			<td><%=alInner.get(4)%></td>
			<td><%=alInner.get(5)%></td>
			<td><%=alInner.get(6)%></td>
			<td><%=alInner.get(7)%></td>
			<td><%=alInner.get(8)%></td>
		</tr>

		<%
			}if(i==0){
		%>

		<tr>
			<td colspan="9"><div class="msg nodata"><span>No leave details available</span></div></td>
		</tr>
		
		<%
			}
		%>



	</table>



</div>

