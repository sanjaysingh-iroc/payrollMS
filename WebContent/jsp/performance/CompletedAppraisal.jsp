<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>


<script type="text/javascript">
	

	function seeSattlement(id, empId,appFreqId) {

		var dialogEdit = '#comment'; 
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : true,
					height : 600,
					width : 850,
					modal : true,
					title : 'Score Card',
					open : function() {
						var xhr = $.ajax({
							url : "ScoreCard.action?id=" + id+ "&empId=" + empId+"&appFreqId="+appFreqId,
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
	
	function giveRemark(id, empId,appFreqId) {

		var dialogEdit = '#comment';
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : true,
					height : 350,
					width : 650,
					modal : true,
					title : 'Remark',
					open : function() {
						var xhr = $.ajax({
							url : "AppraisalRemark.action?id=" + id+ "&empid=" + empId+"&appFreqId="+appFreqId,
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
	<jsp:param value="Completed Appraisal" name="title" />
</jsp:include>



<%
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);

	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
	Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");
	if (orientationMemberMp == null)
		orientationMemberMp = new HashMap<String, String>();

	Map<String, Map<String, String>> appraisalDetails = (Map<String, Map<String, String>>) request.getAttribute("appraisalDetails");
	//List<List<String>> outerList = (List<List<String>>) request.getAttribute("completedList");
	Map hmCompletedAppraisals = (Map) request.getAttribute("hmCompletedAppraisals");
	boolean flag=(Boolean)request.getAttribute("flag");
%>
<div class="leftbox reportWidth">
<div>
<s:form action="" theme="simple" method="POST">
<table >
<tr>
<td>
<s:select name="strWlocation" list="workList" id="wlocation"
                                        listKey="wLocationId" listValue="wLocationName" headerKey=""
                                        headerValue="All WorkLocation" required="true"
                                        onchange="this.form.submit();" ></s:select>
</td>


                            <td>
                                   
                                    <s:select name="strDepart" list="departmentList" id="depart"
                                        listKey="deptId" listValue="deptName" headerKey=""
                                        headerValue="All Department" required="true" onchange="this.form.submit();" ></s:select>
                           
                            </td>
                            
                            <td>
                                   <s:select name="empGrade" list="gradeList" listKey="gradeId"
                                            listValue="gradeCode" headerKey="" id="gradeIdV"
                                            headerValue="All Grade"
                                            onchange="this.form.submit();" ></s:select>
                            </td>
                            
</tr>
</table>
</s:form>
</div>
<%if(flag==true){ %>
	<table class="tb_style" width="100%">
		<tr>
			<th>Appraisal</th>
			<th>Orientation</th>
			<th>Frequency</th>
			<th>Employee Name</th>
			<th>Designation</th>
			<th>Score Card</th>
			<th>Remark</th>
		</tr>
		<%
		
			Set set = hmCompletedAppraisals.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				String str = (String)it.next();
				List alOuterList = (List)hmCompletedAppraisals.get(str);
		System.out.println("in list "+alOuterList);
				
			for (int i = 0; alOuterList != null && i < alOuterList.size(); i++) {
				List<String> innerList = (List)alOuterList.get(i);
		%>
		<tr>
			<td><%=innerList.get(1)%></td>
			<td><%=innerList.get(2)%>&deg;</td>
			<td><%=innerList.get(3)%></td>
			<td><%=hmEmpName.get(innerList.get(4))%></td>
			<td><%=hmEmpCodeDesig.get(innerList.get(4))%></td>
			<td>
				<%
				if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {
				%> 
				<a href="javascript:void(0)" onclick="seeSattlement('<%=innerList.get(0)%>','<%=innerList.get(4)%>','<%=innerList.get(5)%>')" class="edit_lvl" > Remark </a>  
				<%
  					}
  				%>
				
				<a href="AppraisalSummary.action?id=<%=innerList.get(0)%>&empId=<%=innerList.get(4)%>&appFreqId=<%=innerList.get(5)%>">Score Card</a>
				
				
				
			</td>
<td>
				<%
				if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {
				%> <a href="javascript:void(0)" onclick="giveRemark('<%=innerList.get(0)%>','<%=innerList.get(4)%>','<%=innerList.get(5)%>')" class="edit_lvl" > Report card </a> 
				<%
 					}
 				%>
			</td>

		</tr>
		<%
			}
			}
		%>

	</table>
<%
			} else {
	%>
	<div class="nodata msg">No Completed Appraisal</div>
	<%
		}
	%>
</div>

<div id="comment"></div>