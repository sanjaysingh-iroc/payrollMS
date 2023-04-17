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

/* 	$(function() {
		$("#fdate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		$("#tdate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		//fdate tdate
	}); */

	
	function viewProfileReport(recruitID) {
		
	  	var id=document.getElementById("popupAjaxLoad");
	    if(id){
	    	id.parentNode.removeChild(id);
	    } 
			
		var dialogEdit = '#viewProfileReport';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 650,  
			width : 800,
			modal : true,
			title : 'Job Profile',
			open : function() {
				var xhr = $.ajax({
						url : "ReportJobProfilePopUp.action?view=openjobreport&recruitID="+recruitID ,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
	
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
	
		$(dialogEdit).dialog('open');
	}
	
	
	
	function updateProfile(recruitId) {

		var id=document.getElementById("updateJobProfileDIV");
		if(id){ 
			id.parentNode.removeChild(id);
		}
		
		var dialogEdit = '#UpdateJobPorfile';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 650,
			width : 800,
			modal : true,
			title : 'Job Profile',
			open : function() {
				var xhr = $.ajax({
					//url : "ApplyLeavePopUp.action", 
					url : "UpdateJobProfilePopUp.action?recruitID="+recruitId,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;

			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open');
	}
</script>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Job Profiles" name="title" />
</jsp:include>

<%
UtilityFunctions uF=new UtilityFunctions();
%>

<div class="leftbox reportWidth">

<s:form name="frm_JobProfiles" action="JobProfiles"
		theme="simple">

		<div class="filter_div">
			<div class="filter_caption">Filter</div>
			
			<s:hidden name="f_org"></s:hidden>	
			<s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;"
                         listValue="orgName" headerKey="" headerValue="All Organisations"
                         list="organisationList" key="" onchange="frm_JobProfiles.submit()"/>

			<s:select theme="simple" name="location" listKey="wLocationId"
				listValue="wLocationName" headerKey="" headerValue="All Locations"
				list="workLocationList" key="0" />

			<s:select theme="simple" name="empGrade" list="gradeList"
				listKey="gradeId" listValue="gradeCode" headerKey="0"
				headerValue="All Grade" />

			<s:select theme="simple" name="designation" listKey="desigId"
				listValue="desigCodeName" headerKey="0"
				headerValue="All Designations" list="desigList" key=""/>

			<s:select theme="simple" name="services" listKey="serviceId"
				listValue="serviceName" headerKey="0" headerValue="All Services"
				list="serviceslist" key="" />

			<%-- <s:select theme="simple" name="checkStatus" headerKey="-2"
				headerValue="All" 
				list="#{'1':'Approved', '-1':'Denied', '0':'Pending'}" cssStyle="width:110px"/> --%>

			<s:textfield name="fdate" id="fdate" cssStyle="width:70px"></s:textfield>
			<s:textfield name="tdate" id="tdate" cssStyle="width:70px"></s:textfield>
			<s:submit cssClass="input_button" value="Search" align="center" />

		</div>
	</s:form>


	<table class="display" id="lt">
	<thead>
		<tr>
			<th style="text-align: left;">Job Code (Designation)</th>
 			<!-- <th style="text-align: left;">Level</th>           
			<th style="text-align: left;">Designation</th>  --> 
			<th style="text-align: left;">Grade</th>
			<!-- <th style="text-align: left;">Skills</th> -->
			<th style="text-align: left;">Department</th>
			<th style="text-align: left;">SBU</th>
			<th style="text-align: left;">Work Location</th>
			<!-- <th style="text-align: left;">Manager</th> -->
			<th style="text-align: left;">No. Of Position(s)</th>
			<th style="text-align: left;">Existing</th>
			<th style="text-align: left;">Planned</th>
			
			<th style="text-align: left;">Effective Date</th>
			<th style="text-align: left;">Profile Status</th>
			
			<th style="text-align: left;">Last updated</th>
			<th style="text-align: left;">Updated by</th>
		</tr>
	</thead>
	<tbody>
	<% List<List<String>> requestList = (List<List<String>>)request.getAttribute("requestList"); 
	for (int i=0; requestList!=null && i<requestList.size(); i++) {
	 List<String> InnerList = (List<String>)requestList.get(i);
	 %>
		<tr id =<%=InnerList.get(0) %> >
			<td nowrap> <b><a href="javascript:void(0)"
						onclick="viewProfileReport(<%=InnerList.get(9)%>)"> <%=InnerList.get(0) %> </a></b> (<%=InnerList.get(1) %>)</td> 
<%-- 			<td><%=InnerList.get(1) %></td> --%>
			<td><%=InnerList.get(2) %></td>
			<td><%=InnerList.get(3) %></td>
			<td nowrap><%=InnerList.get(4) %></td>
			<td><%=InnerList.get(5) %></td>
			<td style="text-align: right; font-weight:bold;"><%=InnerList.get(6) %></td>
			<td style="text-align: right; font-weight:bold;"><%=InnerList.get(12) %></td>
			<td style="text-align: right; font-weight:bold;"><%=InnerList.get(13) %></td>
			<td><%=InnerList.get(7) %></td>
			<td nowrap><span style="align: left;"> 
			<%if(uF.parseToInt((String)(InnerList.get(8)))==1){ %>
			<font color="green">Approved</font> 
			<%}else{ %>
			<a href="javascript:void(0)"
				onclick="updateProfile(<%=InnerList.get(9)%>)">
				<%if(InnerList.get(10).equalsIgnoreCase("-")){ %>
			Add	Profile<%}else{ %>
			Update Profile
			<%} %>
			</a>
				<%} %> 
			</span></td>
			<td><%=InnerList.get(10) %></td>
			<td nowrap><%=InnerList.get(11) %></td>
		</tr>
		<% } %>
	</tbody>
</table>

</div>
