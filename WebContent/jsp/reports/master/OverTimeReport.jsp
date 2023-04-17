 
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(function() {
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

function addOverTime(org_id, level_id, ot_type, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Overtime');
	$.ajax({
		url : 'AddOverTime.action?operation=A&org_id='+org_id+'&strLevel='+level_id+'&strOverTimeType='+ot_type+'&userscreen='+userscreen
				+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});		
}
							
function editOverTime(overtimeid, org_id, level_id, ot_type, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Overtime');
	$.ajax({
		url : 'AddOverTime.action?operation=U&id='+overtimeid+'&org_id='+org_id+'&strLevel='+level_id+'&strOverTimeType='+ot_type
				+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function getData(type) {
	var org=document.getElementById("f_org").value;
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	
	window.location='MyDashboard.action?strOrg='+org+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
}

</script>

</head>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);

	List<FillLevel> levelList=(List<FillLevel>)request.getAttribute("levelList");
	Map<String, Map<String, String>> hmEmpOverTimeLevelPolicy=(Map<String, Map<String, String>>)request.getAttribute("hmEmpOverTimeLevelPolicy");
	if(hmEmpOverTimeLevelPolicy == null) hmEmpOverTimeLevelPolicy=new HashMap<String, Map<String,String>>();
	String strTitle = (String)request.getAttribute(IConstants.TITLE);
	
	Map<String, List<Map<String,String>>> hmOvertimeMinuteSlab = (Map<String, List<Map<String,String>>>)request.getAttribute("hmOvertimeMinuteSlab");
	if(hmOvertimeMinuteSlab == null) hmOvertimeMinuteSlab = new HashMap<String, List<Map<String,String>>>();
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
%>

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>

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
				<s:form name="frm_OverTimeReport" action="MyDashboard" theme="simple">
					<s:hidden name="userscreen" id="userscreen" />
					<s:hidden name="navigationId" id="navigationId" />
					<s:hidden name="toPage" id="toPage" />
					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select list="orgList" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="getData('0');"></s:select>
								<% } else { %>
									<s:select list="orgList" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="getData('0');"></s:select>
								<% } %>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
		

		<div class="col-md-12">
		    <ul class="level_list">
		    	<% for(int i=0;levelList!=null && i<levelList.size();i++){ %>
    	<li>
			<strong><%=levelList.get(i).getLevelCodeName()%></strong>
				<ul>
					<li>Public Holiday
						<ul>
							<li><a href="javascript:void(0)" onclick="addOverTime('<%=request.getAttribute("f_org") %>','<%=levelList.get(i).getLevelId() %>','PH','<%=userscreen %>','<%=navigationId %>','<%=toPage %>')"> + Add New Overtime Policy</a></li>
						<%
							List<Map<String, String>> alPHInner = (List<Map<String,String>>) hmEmpOverTimeLevelPolicy.get(levelList.get(i).getLevelId()+"_PH");
							if(alPHInner == null) alPHInner = new ArrayList<Map<String,String>>();
							int nPHInner = alPHInner.size();
							for(int j = 0; j < nPHInner; j++){
								Map<String,String> hmEmpOverTimePolicy = alPHInner.get(j); 
								if(hmEmpOverTimePolicy == null) hmEmpOverTimePolicy = new HashMap<String,String>();
								if(hmEmpOverTimePolicy!=null && !hmEmpOverTimePolicy.isEmpty()){
							%>						
								<li>
									<a title="Edit" href="javascript:void(0)" onclick="editOverTime('<%=hmEmpOverTimePolicy.get("OVERTIME_ID")%>','<%=request.getAttribute("f_org") %>','<%=levelList.get(i).getLevelId() %>','PH', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
									<a title="Delete" href="AddOverTime.action?operation=D&id=<%=hmEmpOverTimePolicy.get("OVERTIME_ID") %>&org_id=<%=request.getAttribute("f_org") %>&strLocation=<%=request.getAttribute("strLocation") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this  ?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
									<%=hmEmpOverTimePolicy.get("OVERTIME_CODE") %>,&nbsp;
									<strong>Effective date:</strong><%=hmEmpOverTimePolicy.get("DATE_FROM") %> <strong>to</strong> <%=hmEmpOverTimePolicy.get("DATE_TO") %>,&nbsp;
									<strong>Overtime Type:</strong> <%=hmEmpOverTimePolicy.get("OVERTIME_TYPE") %>,&nbsp;
									<strong>Calculation Basis:</strong> <%=hmEmpOverTimePolicy.get("CAL_BASIS") %>,&nbsp;
									<%
									String strOTPType = hmEmpOverTimePolicy.get("OVERTIME_PAYMENT_TYPE");
									String strCalBasis = hmEmpOverTimePolicy.get("CAL_BASIS");
									if(strCalBasis!=null && !strCalBasis.equalsIgnoreCase("Minute")){ %>
										<strong>Overtime Payment Type:</strong> <%=hmEmpOverTimePolicy.get("OVERTIME_PAYMENT_TYPE") %>,&nbsp;
									<%} %>
									<br/>
									<%
									if(strCalBasis!=null && strCalBasis.equals("Hourly") && (strOTPType !=null && strOTPType.equals("Percent"))){
									%>	
										<strong>Salary Head:</strong> <%=hmEmpOverTimePolicy.get("SALARY_HEAD_ID") %>,&nbsp;
										<strong>%:</strong> <%=hmEmpOverTimePolicy.get("OVERTIME_PAYMENT_AMOUNT") %>,&nbsp;
										<strong>Days Calculation:</strong> <%=hmEmpOverTimePolicy.get("DAY_CALCULATION") %>,&nbsp;
										<strong>Standard Working Hours:</strong> <%=hmEmpOverTimePolicy.get("STANDARD_WKG_HOURS") %>,&nbsp;
										<strong>Buffer After Roster/Standard Time:</strong> <%=hmEmpOverTimePolicy.get("BUFFER_STANDARD_TIME") %>,&nbsp;
										<strong>Min Hrs Working for Overtime:</strong> <%=hmEmpOverTimePolicy.get("MIN_OVER_TIME") %>,&nbsp;
									<% } else if(strCalBasis!=null && strCalBasis.equals("Hourly") && (strOTPType !=null && strOTPType.equals("Amount"))) { %>	
										<strong>Amount:</strong> <%=hmEmpOverTimePolicy.get("OVERTIME_PAYMENT_AMOUNT") %>,&nbsp;
										<strong>Days Calculation:</strong> <%=hmEmpOverTimePolicy.get("DAY_CALCULATION") %>,&nbsp;
										<strong>Standard Working Hours:</strong> <%=hmEmpOverTimePolicy.get("STANDARD_WKG_HOURS") %>,&nbsp;
										<strong>Buffer After Roster/Standard Time:</strong> <%=hmEmpOverTimePolicy.get("BUFFER_STANDARD_TIME") %>,&nbsp;
										<strong>Min Hrs Working for Overtime:</strong> <%=hmEmpOverTimePolicy.get("MIN_OVER_TIME") %>,&nbsp;
										<strong>Round off to nearest(on daily basis):</strong> <%=uF.showData(hmEmpOverTimePolicy.get("ROUND_OFF_OVERTIME"), "") %>&nbsp;
									<% } else if(strCalBasis!=null && strCalBasis.equals("Full Day") && (strOTPType !=null && strOTPType.equals("Percent"))) { %>	
										<strong>Salary Head:</strong> <%=hmEmpOverTimePolicy.get("SALARY_HEAD_ID") %>,&nbsp;
										<strong>%:</strong> <%=hmEmpOverTimePolicy.get("OVERTIME_PAYMENT_AMOUNT") %>,&nbsp;
									<% } else if(strCalBasis!=null && !strCalBasis.equalsIgnoreCase("Minute")){ %>	
										<strong>Amount:</strong> <%=hmEmpOverTimePolicy.get("OVERTIME_PAYMENT_AMOUNT") %>,&nbsp;
									<%}%>	
									<%if(strCalBasis!=null && strCalBasis.equalsIgnoreCase("Minute")){%>
										<ul>
									<%	List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(hmEmpOverTimePolicy.get("OVERTIME_ID"));
										if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
										int nAlOtMinuteSize =  alOtMinute.size();
										for(int x = 0; x < nAlOtMinuteSize; x++){
											Map<String,String> hmOvertimeMinute = alOtMinute.get(x);
									%>
											<li>
												<strong>Minimum:</strong> <%=hmOvertimeMinute.get("OVERTIME_MIN_MINUTE") %> Minute&nbsp;
												<strong>Maximum:</strong> <%=hmOvertimeMinute.get("OVERTIME_MAX_MINUTE") %> Minute&nbsp;
												= <%=uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE")) == 60 ? "1 Hrs." : hmOvertimeMinute.get("ROUNDOFF_MINUTE")+" Minute." %>
											</li>
									<%	}%>
										</ul>
									<%}%>						
								</li>						
							<%}
    						}	%>
						</ul>
					</li>
					<li>Weekend
						<ul>
							<li><a href="javascript:void(0)" onclick="addOverTime('<%=request.getAttribute("f_org") %>','<%=levelList.get(i).getLevelId() %>','BH','<%=userscreen %>','<%=navigationId %>','<%=toPage %>')"> + Add New Overtime Policy</a></li>
						<%
							List<Map<String, String>> alBHInner = (List<Map<String,String>>) hmEmpOverTimeLevelPolicy.get(levelList.get(i).getLevelId()+"_BH");
							if(alBHInner == null) alBHInner = new ArrayList<Map<String,String>>();
							int nBHInner = alBHInner.size();
							for(int j = 0; j < nBHInner; j++){
								Map<String,String> hmEmpOverTimePolicy1 = alBHInner.get(j); 
								if(hmEmpOverTimePolicy1 == null) hmEmpOverTimePolicy1 = new HashMap<String,String>();
								if(hmEmpOverTimePolicy1!=null && !hmEmpOverTimePolicy1.isEmpty()){
							%>
							
								<li>
									<a title="Edit" href="javascript:void(0)" onclick="editOverTime('<%=hmEmpOverTimePolicy1.get("OVERTIME_ID")%>','<%=request.getAttribute("f_org") %>','<%=levelList.get(i).getLevelId() %>','BH', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
									<a title="Delete" href="AddOverTime.action?operation=D&id=<%=hmEmpOverTimePolicy1.get("OVERTIME_ID") %>&org_id=<%=request.getAttribute("f_org") %>&strLocation=<%=request.getAttribute("strLocation") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this  ?')" style="color:rgb(233,0,0)" ><i class="fa fa-trash" aria-hidden="true"></i></a>
									<strong>Overtime Code:</strong><%=uF.showData(hmEmpOverTimePolicy1.get("OVERTIME_CODE"), "") %>,&nbsp;
									<strong>Effective date:</strong><%=uF.showData(hmEmpOverTimePolicy1.get("DATE_FROM"), "") %> <strong>to</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("DATE_TO"), "") %>,&nbsp;
									<strong>Calculation Basis:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("CAL_BASIS"), "") %>,&nbsp;
									<% 
									String strOTPType = hmEmpOverTimePolicy1.get("OVERTIME_PAYMENT_TYPE");
									String strCalBasis = hmEmpOverTimePolicy1.get("CAL_BASIS");
									if(strCalBasis!=null && !strCalBasis.equalsIgnoreCase("Minute")){ %>
										<strong>Overtime Payment Type:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("OVERTIME_PAYMENT_TYPE"), "") %>,&nbsp;
									<%}%>	
									<br/>
									<%
									if(strCalBasis!=null && strCalBasis.equalsIgnoreCase("Hourly") && (strOTPType !=null && strOTPType.equalsIgnoreCase("Percent"))){
									%>	
										<strong>Salary Head:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("SALARY_HEAD_ID"), "") %>,&nbsp;
										<strong>%:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("OVERTIME_PAYMENT_AMOUNT"), "") %>,&nbsp;
										<strong>Days Calculation:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("DAY_CALCULATION"), "") %>,&nbsp;
										<strong>Standard Working Hours:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("STANDARD_WKG_HOURS"), "") %>,&nbsp;
										<strong>Buffer After Roster/Standard Time:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("BUFFER_STANDARD_TIME"), "") %>,&nbsp;
										<strong>Min Hrs Working for Overtime:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("MIN_OVER_TIME"), "") %>,&nbsp;
										<strong>Round off to nearest(on daily basis):</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("ROUND_OFF_OVERTIME"), "") %>&nbsp;
									<%
									} else if(strCalBasis!=null && strCalBasis.equalsIgnoreCase("Hourly") && (strOTPType !=null && strOTPType.equalsIgnoreCase("Amount"))){
										%>	
										<strong>Amount:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("OVERTIME_PAYMENT_AMOUNT"), "") %>,&nbsp;
										<strong>Days Calculation:</strong> <%=hmEmpOverTimePolicy1.get("DAY_CALCULATION") %>,&nbsp;
										<strong>Standard Working Hours:</strong> <%=hmEmpOverTimePolicy1.get("STANDARD_WKG_HOURS") %>,&nbsp;
										<strong>Buffer After Roster/Standard Time:</strong> <%=hmEmpOverTimePolicy1.get("BUFFER_STANDARD_TIME") %>,&nbsp;
										<strong>Min Hrs Working for Overtime:</strong> <%=hmEmpOverTimePolicy1.get("MIN_OVER_TIME") %>,&nbsp;
										<strong>Round off to nearest(on daily basis):</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("ROUND_OFF_OVERTIME"), "") %>&nbsp;
									<%
									} else if(strCalBasis!=null && strCalBasis.equalsIgnoreCase("Full Day") && (strOTPType !=null && strOTPType.equalsIgnoreCase("Percent"))){
									%>	
										<strong>Salary Head:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("SALARY_HEAD_ID"), "") %>,&nbsp;
										<strong>%:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("OVERTIME_PAYMENT_AMOUNT"), "") %>,&nbsp;
									<%
									} else if(strCalBasis!=null && !strCalBasis.equalsIgnoreCase("Minute")){
									%>	
										<strong>Amount:</strong> <%=uF.showData(hmEmpOverTimePolicy1.get("OVERTIME_PAYMENT_AMOUNT"), "") %>,&nbsp;
									<%}%>
									
									<%if(strCalBasis!=null && strCalBasis.equalsIgnoreCase("Minute")){%>
										<ul>
									<%	List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(hmEmpOverTimePolicy1.get("OVERTIME_ID"));
										if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
										int nAlOtMinuteSize =  alOtMinute.size();
										for(int x = 0; x < nAlOtMinuteSize; x++){
											Map<String,String> hmOvertimeMinute = alOtMinute.get(x);
									%>
											<li>
												<strong>Minimum:</strong> <%=hmOvertimeMinute.get("OVERTIME_MIN_MINUTE") %> Minute&nbsp;
												<strong>Maximum:</strong> <%=hmOvertimeMinute.get("OVERTIME_MAX_MINUTE") %> Minute&nbsp;
												= <%=uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE")) == 60 ? "1 Hrs." : hmOvertimeMinute.get("ROUNDOFF_MINUTE")+" Minute." %>
											</li>
									<%	}%>
										</ul>
									<%} %>
								</li>
							
							<%}
							}%>
						</ul>
					</li>
					<li>Extra Hour worked
						<ul>
							<li><a href="javascript:void(0)" onclick="addOverTime('<%=request.getAttribute("f_org") %>','<%=levelList.get(i).getLevelId() %>','EH','<%=userscreen %>','<%=navigationId %>','<%=toPage %>')"> + Add New Overtime Policy</a></li>
						<%
							List<Map<String, String>> alEHInner = (List<Map<String,String>>) hmEmpOverTimeLevelPolicy.get(levelList.get(i).getLevelId()+"_EH");
							if(alEHInner == null) alEHInner = new ArrayList<Map<String,String>>();
							int nEHInner = alEHInner.size();
							for(int j = 0; j < nEHInner; j++){
								Map<String,String> hmEmpOverTimePolicy2 = alEHInner.get(j); 
								if(hmEmpOverTimePolicy2 == null) hmEmpOverTimePolicy2 = new HashMap<String,String>();
								if(hmEmpOverTimePolicy2!=null && !hmEmpOverTimePolicy2.isEmpty()){
							%>						
								<li>
									<a title="Edit" href="javascript:void(0)" onclick="editOverTime('<%=hmEmpOverTimePolicy2.get("OVERTIME_ID")%>','<%=request.getAttribute("f_org") %>','<%=levelList.get(i).getLevelId() %>','EH', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
									<a title="Delete" href="AddOverTime.action?operation=D&id=<%=hmEmpOverTimePolicy2.get("OVERTIME_ID") %>&org_id=<%=request.getAttribute("f_org") %>&strLocation=<%=request.getAttribute("strLocation") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this  ?')"  style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
									<strong>Overtime Code:</strong><%=uF.showData(hmEmpOverTimePolicy2.get("OVERTIME_CODE"), "") %>,&nbsp;
									<strong>Effective date:</strong><%=uF.showData(hmEmpOverTimePolicy2.get("DATE_FROM"), "") %> <strong>to</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("DATE_TO"), "") %>,&nbsp;
									<strong>Calculation Basis:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("CAL_BASIS"), "") %>,&nbsp;
									<% 
									String strOTPType = hmEmpOverTimePolicy2.get("OVERTIME_PAYMENT_TYPE");
									String strCalBasis = hmEmpOverTimePolicy2.get("CAL_BASIS");
									if(strCalBasis!=null && !strCalBasis.equalsIgnoreCase("Minute")){ %>
										<strong>Overtime Payment Type:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("OVERTIME_PAYMENT_TYPE"), "") %>,&nbsp;
									<%}%>
									<br/>
									<%
									if(strCalBasis!=null && strCalBasis.equals("Hourly") && (strOTPType !=null && strOTPType.equals("Percent"))){
									%>	
										<strong>Salary Head:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("SALARY_HEAD_ID"), "") %>,&nbsp;
										<strong>%:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("OVERTIME_PAYMENT_AMOUNT"), "") %>,&nbsp;
										<strong>Days Calculation:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("DAY_CALCULATION"), "") %>,&nbsp;
										<strong>Standard Working Hours:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("STANDARD_WKG_HOURS"), "") %>,&nbsp;
										<strong>Buffer After Roster/Standard Time:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("BUFFER_STANDARD_TIME"), "") %>,&nbsp;
										<strong>Min Hrs Working for Overtime:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("MIN_OVER_TIME"), "") %>,&nbsp;
										<strong>Round off to nearest(on daily basis):</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("ROUND_OFF_OVERTIME"), "") %>&nbsp;
									<%
									} else if(strCalBasis!=null && strCalBasis.equals("Hourly") && (strOTPType !=null && strOTPType.equals("Amount"))){
										%>	
										<strong>Amount:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("OVERTIME_PAYMENT_AMOUNT"), "") %>,&nbsp;
										<strong>Days Calculation:</strong> <%=hmEmpOverTimePolicy2.get("DAY_CALCULATION") %>,&nbsp;
										<strong>Standard Working Hours:</strong> <%=hmEmpOverTimePolicy2.get("STANDARD_WKG_HOURS") %>,&nbsp;
										<strong>Buffer After Roster/Standard Time:</strong> <%=hmEmpOverTimePolicy2.get("BUFFER_STANDARD_TIME") %>,&nbsp;
										<strong>Min Hrs Working for Overtime:</strong> <%=hmEmpOverTimePolicy2.get("MIN_OVER_TIME") %>,&nbsp;
										<strong>Round off to nearest(on daily basis):</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("ROUND_OFF_OVERTIME"), "") %>&nbsp;
									<%
									} else if(strCalBasis!=null && strCalBasis.equals("Full Day") && (strOTPType !=null && strOTPType.equals("Percent"))){
									%>	
										<strong>Salary Head:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("SALARY_HEAD_ID"), "") %>,&nbsp;
										<strong>%:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("OVERTIME_PAYMENT_AMOUNT"), "") %>,&nbsp;
									<%
									} else if(strCalBasis!=null && !strCalBasis.equalsIgnoreCase("Minute")){
									%>	
										<strong>Amount:</strong> <%=uF.showData(hmEmpOverTimePolicy2.get("OVERTIME_PAYMENT_AMOUNT"), "") %>,&nbsp;
									<%}%>
									<%if(strCalBasis!=null && strCalBasis.equalsIgnoreCase("Minute")){%>
										<ul>
									<%	List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(hmEmpOverTimePolicy2.get("OVERTIME_ID"));
										if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
										int nAlOtMinuteSize =  alOtMinute.size();
										for(int x = 0; x < nAlOtMinuteSize; x++){
											Map<String,String> hmOvertimeMinute = alOtMinute.get(x);
									%>
											<li>
												<strong>Minimum:</strong> <%=hmOvertimeMinute.get("OVERTIME_MIN_MINUTE") %> Minute&nbsp;
												<strong>Maximum:</strong> <%=hmOvertimeMinute.get("OVERTIME_MAX_MINUTE") %> Minute&nbsp;
												= <%=uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE")) == 60 ? "1 Hrs." : hmOvertimeMinute.get("ROUNDOFF_MINUTE")+" Minute." %>
											</li>
									<%	}%>
										</ul>
									<%}%>
								</li>  
							
							<%}
							}%>
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

 <div id="editOverTimeID"></div>
 <div id="addOverTimeID"></div>