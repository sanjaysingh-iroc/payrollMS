<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#lt').dataTable({
			bJQueryUI : true,
			"sPaginationType" : "full_numbers",
			"aaSorting" : []
		})
	});
	$(function() {
		$("#fdate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		$("#tdate").datepicker({
			dateFormat : 'dd/mm/yy'
		});

	});

	function downloadXML(strID) {
		window.location = "DownloadXML.action?id=" + strID;
	}
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Publish Job" name="title" />
</jsp:include>

<div class="leftbox reportWidth">

	<s:form name="frm_PublishJob" action="PublishJob" theme="simple">

		<div class="filter_div">
			<div class="filter_caption">Filter</div>

			<s:select theme="simple" name="location" listKey="wLocationId"
				listValue="wLocationName" headerKey="" headerValue="All Locations"
				list="workLocationList" key="0" />

			<s:select theme="simple" name="empGrade" list="gradeList"
				listKey="gradeId" listValue="gradeCode" headerKey="0"
				headerValue="All Grade" />

			<s:select theme="simple" name="designation" listKey="desigId"
				listValue="desigCodeName" headerKey="0"
				headerValue="All Designations" list="desigList" key="" />

			<s:select theme="simple" name="services" listKey="serviceId"
				listValue="serviceName" headerKey="0" headerValue="All Services"
				list="serviceslist" key="" />

			<%-- <s:select theme="simple" name="checkStatus" headerKey="-2"
				headerValue="All"
				list="#{'1':'Approved', '-1':'Denied', '0':'Pending'}"
				cssStyle="width:110px" /> --%>

			<s:textfield name="fdate" id="fdate" cssStyle="width:70px"></s:textfield>
			<s:textfield name="tdate" id="tdate" cssStyle="width:70px"></s:textfield>
			<s:submit cssClass="input_button" value="Search" align="center" />
			<%
				String sb = (String) request.getAttribute("sb");
					if (sb != null && !sb.equals("")) {
			%>
			<a href="javascript:void(0)"
				onclick="if(confirm('Do you want to download xml file?'))downloadXML('<%=(String) request.getAttribute("sb")%>');"
				style="float: right;"><img src="images1/file-xml.png"
				title="Download XML" /> </a>
			<%
				}
			%>

		</div>
	</s:form>

	<table class="display" id="lt">
		<thead>
			<tr>
				<th style="text-align: left;">Job ID</th>
				<th style="text-align: left;">Level</th>
				<th style="text-align: left;">Designation</th>
				<th style="text-align: left;">Grade</th>
				<!-- <th style="text-align: left;">Skills</th> -->
				<th style="text-align: left;">Department</th>
				<th style="text-align: left;">SBU</th>
				<th style="text-align: left;" nowrap="nowrap">Work Location</th>
				<th style="text-align: left;">Manager</th>
				<th style="text-align: left;">Position</th>
				<th style="text-align: left;" nowrap="nowrap">Effective Date</th>
				<th style="text-align: left;">Download</th>
			</tr>
		</thead>
		<tbody>
			<%
				java.util.List publishJobList = (java.util.List) request
						.getAttribute("publishJobList");

				for (int i = 0; publishJobList != null && i < publishJobList.size(); i++) {
					java.util.List cinnerlist = (java.util.List) publishJobList
							.get(i);
			%>
			<tr id=<%=cinnerlist.get(0)%>>
				<td><%=cinnerlist.get(6)%></td>
				<td><%=cinnerlist.get(11)%></td>
				<td><%=cinnerlist.get(1)%></td>
				<td><%=cinnerlist.get(2)%></td>
				<td><%=cinnerlist.get(9)%></td>
				<td><%=cinnerlist.get(8)%></td>
				<td><%=cinnerlist.get(3)%></td>
				<td><%=cinnerlist.get(10)%></td>
				<td><span style="align: center; font-weight: bold;"><%=cinnerlist.get(4)%></span>
				</td>
				<td><%=cinnerlist.get(5)%></td>
				<td><%=cinnerlist.get(7)%></td>
			</tr>
			<%
				}
			%>
		</tbody>
	</table>


</div>
