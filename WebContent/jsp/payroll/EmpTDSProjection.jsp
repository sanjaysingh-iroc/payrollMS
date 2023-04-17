<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, Map<String, String>> hmEmployeeMap = (Map<String, Map<String, String>>) request.getAttribute("hmEmployeeMap");
	if (hmEmployeeMap == null) hmEmployeeMap = new LinkedHashMap<String, Map<String, String>>();

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
	String pageCount = (String)request.getAttribute("pageCount");
	
	String sbData = (String) request.getAttribute("sbData");
	String strSearch = (String) request.getAttribute("strSearch");
	
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
%>
<script type="text/javascript">

	jQuery(document).ready(function() {
	
		jQuery(".content1").hide();
		//toggle the componenet with class msg_body
		jQuery(".heading_dash").click(function() {
			jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("filter_close");
		});
	});	
	
	function submitForm(){
		document.frm_EmpTDSProjection.proPage.value = '';
		document.frm_EmpTDSProjection.minLimit.value = '';
		document.frm_EmpTDSProjection.submit();
	}
	
	function loadMore(proPage, minLimit) {
		document.frm_EmpTDSProjection.proPage.value = proPage;
		document.frm_EmpTDSProjection.minLimit.value = minLimit;
		document.frm_EmpTDSProjection.submit();
	}
	
	function viewEmpTDSProjection(empName, empId,strFinancialYearStart,strFinancialYearEnd) {

		var dialogEdit = '#divViewEmpTDSProjection';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : true,
			height : 450,
			width : 650,
			modal : true,
			title : 'TDS Projection of '+empName+' for financial year '+strFinancialYearStart+' to '+strFinancialYearEnd,
			open : function() {
				var xhr = $.ajax({
					url : "ViewEmpTDSProjection.action?strEmpId="+empId+"&strFinancialYearStart="+strFinancialYearStart+"&strFinancialYearEnd="+strFinancialYearEnd,
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
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
});    
</script>


<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<!-- Custom form for adding new records -->


    <div class="pagetitle">
      <span>TDS Projection for financial year <%=uF.showData(strFinancialYearStart, "") %> to <%=uF.showData(strFinancialYearEnd, "") %></span>
    </div>
    

<div id="printDiv" class="leftbox reportWidth">
	<div style="float: left; width: 98%;">
		<s:form name="frm_EmpTDSProjection" id="frm_EmpTDSProjection" action="EmpTDSProjection" theme="simple" method="post">
			<s:hidden name="proPage" id="proPage"/>
			<s:hidden name="minLimit" id="minLimit"/>
			<div class="desgn" style="margin-bottom: 5px; background: #f5f5f5; color: #232323;">
				<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
					<%=(String) request.getAttribute("selectedFilter")%>
				</p>
				<div class="content1" style="height: 170px;">
					<div style="float: left; width: 100%;">
						<div style="float: left; margin-top: 10px;">
							<img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/filter_icon.png">
						</div>
	
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
							<p style="padding-left: 5px;">Organization</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;"
							 listValue="orgName" onchange="submitForm();" list="orgList" key="" />
						</div>
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
							<p style="padding-left: 5px;">Location</p>
							<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" 
								cssStyle="float:left;margin-right: 10px;" listValue="wLocationName" multiple="true" list="wLocationList"key="" />
						</div>
	
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
							<p style="padding-left: 5px;">Department</p>
							<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId"
								cssStyle="float:left;margin-right: 10px;" listValue="deptName" multiple="true"></s:select>
						</div>
	
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
							<p style="padding-left: 5px;">Service</p>
							<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
								listValue="serviceName" multiple="true"></s:select>
						</div>
	
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
							<p style="padding-left: 5px;">Level</p>
							<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" cssStyle="float:left;margin-right: 10px;width:200px;"
								listValue="levelCodeName" multiple="true" list="levelList" key="" />
						</div>
					</div>
					<div style="float: left; width: 100%;">
						<div style="float: left; margin-top: 10px;">
							<img style="padding: 2px 1px 0 1px; width: 24px;" border="0" src="<%=request.getContextPath()%>/images1/icons/cal_period_icon.png">
						</div>
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
							<p style="padding-left: 5px;">Financial Year</p>
							<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId"
								listValue="financialYearName" headerKey="0" onchange="submitForm();" list="financialYearList" key=""
								cssStyle="width:200px;" />
						</div>
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;"> 
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" value="Submit"  class="input_button" onclick="submitForm();"/>
						</div>
					</div>
				</div>
			</div>
			<div style="float:left; font-size:12px; line-height:22px; width:100%; margin-left: 350px;margin-bottom: 10px;">
		        <span style="float:left; margin-right:7px;">Search:</span>
		        <div style="border:solid 1px #68AC3B; float:left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
		        	<div style="float:left">
		          		<input type="text" id="strSearch" name="strSearch" style="margin-left: 0px; border:0px solid #ccc; width:282px; box-shadow:0px 0px 0px #ccc" value="<%=uF.showData(strSearch,"") %>"/> 
		        	</div>
			       	<div style="float:right">
			        	<input type="button" value="Search" class="input_search" onclick="submitForm();"/>
			        </div>
		    	</div>
		    </div>
		    <script type="text/javascript">
				$("#strSearch").autocomplete({
					source: [ <%=uF.showData(sbData,"") %> ]
				});
			</script>
		</s:form>
		<div style="float:left; width: 100%;">
			<table width="60%" class="tb_style">
				<tr>
					<td class="reportHeading">Employee Code</td>
					<td class="reportHeading">Employee Name</td>
					<td class="reportHeading">Financial Year</td>
					<td class="reportHeading">View</td>
				</tr> 
			<%
	 			Iterator<String> it = hmEmployeeMap.keySet().iterator();
	 			while (it.hasNext()) {
	 				String strEmpId = (String) it.next();
	 				Map<String, String> hmEmpInner = hmEmployeeMap.get(strEmpId);
	 		%>
					<tr>
						<td class="reportLabel" align="center"><%=hmEmpInner.get("EMP_CODE")%></td>
						<td class="reportLabel"><%=hmEmpInner.get("EMP_NAME")%></td>
						<td class="reportLabel" align="center"><%=uF.showData(strFinancialYearStart, "") %> - <%=uF.showData(strFinancialYearEnd, "") %></td>
						<td class="reportLabel"><a href="javascript:void(0);" onclick="viewEmpTDSProjection('<%=hmEmpInner.get("EMP_NAME")%>','<%=strEmpId %>','<%=uF.showData(strFinancialYearStart, "") %>', '<%=uF.showData(strFinancialYearEnd, "") %>')">View</a></td>
						
					</tr>
			<%}%>
			<%if (hmEmployeeMap.size() == 0) {%>
				<tr><td class="reportLabel alignCenter" colspan="4">No employee found in this financial year</td></tr>
			<%}%>        
			</table>
	    </div>
	    
	    <div style="text-align: center; float: left; width: 100%;">
			<%
			int intPageCnt = uF.parseToInt(pageCount);
				int pageCnt = 0;
				int minLimit = 0;
				
				for(int i=1; i<=intPageCnt; i++) { 
					minLimit = pageCnt * 10;
					pageCnt++;
			%>
			<% if(i ==1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				if(uF.parseToInt(strPgCnt) > 1) {
					 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
				}
				if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) {
					strPgCnt = "1";
				}
			%>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
					<%="< Prev" %></a>
				<% } else { %>
					<b><%="< Prev" %></b>
				<% } %>
				</span>
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
				<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"<% } %>><%=pageCnt %></a></span>
				
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
					<b>...</b>
				<% } %>
			
			<% } %>
			
			<% if(i > 1 && i < intPageCnt) { %>
				<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"<% } %>><%=pageCnt %></a></span>
				<% } %>
			<% } %>
			
			<% if(i == intPageCnt && intPageCnt > 1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
				 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
				 if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) {
					strPgCnt = "1";
				}
				%>
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intPageCnt) { %>
					<b>...</b>
				<% } %>
			
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"<% } %>><%=pageCnt %></a></span>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
				<% } else { %>
					<b><%="Next >" %></b>
				<% } %>
				</span>
			<% } %>
			<%} %>
		</div>
	</div>
 </div>
 <div id="divViewEmpTDSProjection"></div>