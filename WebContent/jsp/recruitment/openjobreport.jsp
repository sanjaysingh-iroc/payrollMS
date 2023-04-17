


<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

	<%
		String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	%>

<style>
.tb_style1 {
	border-collapse: collapse;
}

.tb_style1 tr td {
	padding: 5px;
	border: solid 1px #c5c5c5;
}

.tb_style1 tr th {
	padding: 5px;
	border: solid 1px #c5c5c5;
	background: #efefef
}
</style>

<script type="text/javascript" >

		$(document).ready(function() {
			$('#lt').dataTable({
				bJQueryUI : true,
				"sPaginationType" : "full_numbers",
				"aaSorting" : []
			})
		});
	
/* 		  
		function addpaneledit(recruitID) {
		
		var id=document.getElementById("the_div");
		if(id){ 
			alert('removing div content'+id);
			id.innerHTML='';
		}

		var dialogEdit = '#UpdateJobProfile';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 650,  
			width : 900,
			modal : true,
			title : 'ADD  Panel',
			open : function() {
				var xhr = $.ajax({
					//url : "ApplyLeavePopUp.action", 
					url :"AddCriteriaPanel.action?type=editpanel&recruitID="+recruitID ,
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
	 */
	
	function addpanel(recruitID) {
		
		var id=document.getElementById("panelDiv");
		if(id){
			id.parentNode.removeChild(id);
				}
	
		var dialogEdit = '#ADDPanel';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 500,  
			width : 900,
			modal : true,
			title : 'Add  Panel',
			open : function() {
				var xhr = $.ajax({
					//url : "ApplyLeavePopUp.action", 
					url :"AddCriteriaPanel.action?type=popup&recruitID="+recruitID ,
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


	function viewProfileReport(recruitID) {
		
	  	var id=document.getElementById("popupAjaxLoad")
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
					//url : "ApplyLeavePopUp.action", 
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
	
	 function viewcandidatereport(recruitID){
			 window.location="CandidateReport.action?recruitId="+recruitID;
	 }
	 
	 function viewcandidatereport1(recruitID){
		 window.location="CandidateReport1.action?recruitId="+recruitID;
	}
	 
 
	</script>
	
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Open Jobs" name="title" />
	</jsp:include>
	
	
	<div class="leftbox reportWidth">
	
	
	<s:form name="frm_Openjob" action="OpenJobReport" theme="simple">
		
		<div class="filter_div">
			<div class="filter_caption">Filter</div>
			
			<%-- <s:hidden name="f_org"></s:hidden> --%>
			
			<s:select theme="simple" name="f_org" listKey="orgId"
                      listValue="orgName" list="organisationList" key="" onchange="frm_JobProfilesApproval.submit()"/>

			<s:select theme="simple" name="location" listKey="wLocationId"
				listValue="wLocationName" headerKey="0" headerValue="All Locations"
				list="workLocationList" key="" />
<%-- 			<s:select theme="simple" name="empGrade" list="gradeList"
				listKey="gradeId" listValue="gradeCode" headerKey="0"
				headerValue="All Grade" /> --%>
			<s:select theme="simple" name="designation" listKey="desigId"
				listValue="desigCodeName" headerKey="0"
				headerValue="All Designations" list="desigList" key="" />

			<s:select theme="simple" name="services" listKey="serviceId"
				listValue="serviceName" headerKey="0" headerValue="All Services"
				list="serviceslist" key="" />

			<s:select theme="simple" name="reportStatus" headerKey="1"
				headerValue="Open" list="#{'-1':'Closed', '0':'All'}"
				cssStyle="width:110px" />

			<s:textfield name="fdate" id="fdate" cssStyle="width:70px"></s:textfield>
			<s:textfield name="tdate" id="tdate" cssStyle="width:70px"></s:textfield>
			<s:submit cssClass="input_button" value="Search" align="center" />

		</div> 
		 </s:form> 
		 
		 
	

	<%-- <s:form name="frm_Openjob" action="OpenJobReport" theme="simple">

		<div class="filter_div">
			<div class="filter_caption">Filter</div>


			<s:select theme="simple" name="services" listKey="serviceId"
				listValue="serviceName" headerKey="0" headerValue="All Services"
				list="serviceslist" key="" />



			<s:select theme="simple" name="reportStatus" headerKey="1"
				headerValue="Open" list="#{'-1':'Closed', '0':'All'}"
				cssStyle="width:110px" />


			<s:submit cssClass="input_button" value="Search" align="center" />

		</div>
	</s:form> --%>



	 <table class="display tb_style1" width="100%">
	<!-- <table> -->
		<thead>
			<tr>

				<th style="text-align: center; width: 15%;">Job Code (Designation)</th>
				<th style="text-align: center; width: 18%;">Panel List</th>
				<th style="text-align: center; width: 7%;">Requested</th>
				<th style="text-align: center; width: 7%;">Applications</th>
				<th style="text-align: center; width: 7%;">Accepted</th>
				<th style="text-align: center; width: 7%;">Finalisation</th>
				<th style="text-align: center; width: 7%;">Shortlisted</th>
				<th style="text-align: center; width: 7%;">Rejected</th>
				<th style="text-align: center; width: 25%;">Action</th>


			</tr>
		</thead>

		<tbody>

			<%
				List<List<String>> alopenjobreport = (List<List<String>>)request.getAttribute("alopenjobreport");
			UtilityFunctions uF=new UtilityFunctions();
			for (int i = 0; alopenjobreport != null && i < alopenjobreport.size(); i++) {
					List<String> alinner = (List<String>) alopenjobreport.get(i);
				
			%>
			<tr>
				<td>
				
			 <%if(uF.parseToInt(alinner.get(1).toString())==1){ %>
				<img src="images1/icons/exclamation_mark_icon.png" width="10" height="16"/>
				<% }
		 		%>
				<b> <a href="javascript:void(0)"
						onclick="viewProfileReport(<%=alinner.get(0)%>)">
			 	 <%=alinner.get(2)%></a>
				</b>(<%=alinner.get(3)%>)</td>
				<td>
				<%
					if (alinner.get(4).equals("")) {
				%>
				<a href="javascript:void(0)" class="add_lvl" title="Add Panel" onclick="addpanel(<%=alinner.get(0)%>)"></a>
				<%
					} else {
				%>
				<%=alinner.get(4)%>,&nbsp;&nbsp;
				<a href="javascript:void(0)" class="add_lvl" title="Modify Panel" onclick="addpanel(<%=alinner.get(0)%>)"></a>
			
				<%
					}
				%>
				</td>
				<td>
				<div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff; text-align: center;">
				<%=alinner.get(5)%></div>
				</td>
				<td style="text-align: right;"><%=alinner.get(6)%></td>

				<td style="text-align: right;"><%=alinner.get(7)%></td>
				<td style="text-align: right;"><%=alinner.get(8)%></td>
				<td style="text-align: right;"><%=alinner.get(9)%></td>
				<td style="text-align: right;"><%=alinner.get(10)%></td>

				<td nowrap>

				 	
				 	<a href="javascript:void(0)" name="editclick"
					onclick="viewcandidatereport(<%=alinner.get(0)%>);">View Candidate(<%=alinner.get(6)%>)</a> 
				 	
				 	&nbsp;&nbsp;&nbsp; <a href="AddCandidateMode.action?recruitId=<%=alinner.get(0)%>" target="_blank">Add New Canditate</a>
				 	
					&nbsp;&nbsp;&nbsp; <a href="SearchCandidate.action?recruitId=<%=alinner.get(0)%>" target="_blank">Search Candidate</a>
					
					&nbsp;&nbsp;&nbsp;
						<a onclick="if(confirm('Are you sure, you want to close the job?')) window.location='CloseJobPublish.action?S=close&RID=<%=alinner.get(0)%>';"
						href="javascript:void(0)">Close Job</a>
				</td>
			</tr>
			<%
			}
			%>

			<%
			if (alopenjobreport ==null || alopenjobreport.size() == 0) {
			%>

			<tr>
				<td colspan="9"><div class="nodata msg">
						<span> No Data Available</span>
					</div></td>
			</tr>
			<%
			}
			%>

		</tbody>
	</table>


		
		
		
</div>

