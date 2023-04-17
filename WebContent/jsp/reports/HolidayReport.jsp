<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page buffer = "16kb" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

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

 /* $(document).ready(function() {
		$('#lt').dataTable({
				bJQueryUI : true,
				"sPaginationType" : "full_numbers",
				"aaSorting" : [] 
			})
}); */
			
function getData(type, userscreen, navigationId, toPage) {
	var org='';
	var calendarYear='';
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	if(type=='2') {
		org = document.getElementById("strOrg").value;
		calendarYear = document.getElementById("calendarYear").value;
	} else {
		org = document.getElementById("strOrg").value;
	}
	window.location='MyDashboard.action?strOrg='+org+'&strCFYear='+calendarYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
}


function addHoliday(calendarYear, org_id, locationid, type, userscreen, navigationId, toPage) {
								
		var strTitle = "Add Holiday";
		if(type == 'O' || type=='o'){
			strTitle = "Add Optional Holiday";
		}
							
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html(strTitle);
		$.ajax({
			url : 'AddHolidays.action?operation=A&calendarYear='+calendarYear+'&orgId='+org_id+'&strWlocation='+locationid+'&type='+type+'&userscreen='+userscreen
					+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});	
}
							
function editHoliday(holidayid, calendarYear, org_id, locationid, type, userscreen, navigationId, toPage) {
	/* document.getElementById("editHolidayID").innerHTML = '';
	document.getElementById("addHolidayID").innerHTML = ''; */
	
	var strTitle = "Edit Holiday";
	if(type == 'O' || type=='o'){
		strTitle = "Edit Optional Holiday";
	}
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html(strTitle);
	$.ajax({
		url : 'AddHolidays.action?operation=U&holidayId='+holidayid+'&calendarYear='+calendarYear+'&orgId='+org_id+'&strWlocation='+locationid+'&type='+type
				+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);

	List<FillWLocation> wLocationList = (List<FillWLocation>) request.getAttribute("wLocationList");
	Map<String,List<Map<String,String>>> hmHolidayList=(Map<String,List<Map<String,String>>>) request.getAttribute("hmHolidayList");
	if(hmHolidayList == null) hmHolidayList = new HashMap<String,List<Map<String,String>>>();
	Map<String,List<Map<String,String>>> hmOptionalHolidayList=(Map<String,List<Map<String,String>>>) request.getAttribute("hmOptionalHolidayList");
	if(hmOptionalHolidayList == null) hmOptionalHolidayList = new HashMap<String,List<Map<String,String>>>();
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
%>


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
				<s:form name="frm_HolidayReport" action="MyDashboard" theme="simple">
					<s:hidden name="userscreen" id="userscreen" />
					<s:hidden name="navigationId" id="navigationId" />
					<s:hidden name="toPage" id="toPage" />
					<div style="float: left; width: 96%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Calendar Year</p>
								<s:select label="Select Calendar Year" name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" 
									headerKey="0" onchange="getData('2');" list="calendarYearList" key="" />
							</div>
							
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select theme="simple" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm_HolidayReport.submit();" list="orgList"/>
								<% } else { %>
									<s:select theme="simple" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm_HolidayReport.submit();" list="orgList"/>
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
	    	<% for(int i=0;wLocationList!=null && i<wLocationList.size();i++){ %>
    		<div style="float: left; width:100%; ">
				<div style="float: left; width:100%;"><strong><%=wLocationList.get(i).getwLocationName()%></strong></div>
				<div class="col-md-6">
					<ul class="level_list">
						<li><a href="javascript:void(0)" onclick="addHoliday('<%=request.getAttribute("calendarYear") %>','<%=request.getAttribute("strOrg") %>','<%=wLocationList.get(i).getwLocationId() %>','', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Holiday</a></li>
						<%
							List<Map<String,String>> outerList =hmHolidayList.get(wLocationList.get(i).getwLocationId());
							for(int j=0;outerList!=null && j<outerList.size();j++){
								Map<String,String> hmInner =outerList.get(j);
						%>
							<li>
								<a title="Delete" href="AddHolidays.action?operation=D&holidayId=<%=hmInner.get("HOLIDAY_ID") %>&calendarYear=<%=request.getAttribute("calendarYear") %>&orgId=<%=request.getAttribute("strOrg") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this?')" style="color:rgb(233,0,0)" ><i class="fa fa-trash" aria-hidden="true"></i></a>
								<a href="javascript:void(0)" onclick="editHoliday('<%=hmInner.get("HOLIDAY_ID") %>','<%=request.getAttribute("calendarYear") %>','<%=request.getAttribute("strOrg") %>','<%=wLocationList.get(i).getwLocationId() %>','', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
								<%=hmInner.get("HOLIDAY_DESCRIPTION") %> <strong>Date:</strong><%=hmInner.get("HOLIDAY_DATE") %> <strong>Holiday Type:</strong> <%=hmInner.get("HOLIDAY_TYPE") %>
							</li>
						<%} %>
					</ul>
				</div>	
				
				<div class="col-md-6">
					<ul class="level_list">
						<li><a href="javascript:void(0)" onclick="addHoliday('<%=request.getAttribute("calendarYear") %>','<%=request.getAttribute("strOrg") %>','<%=wLocationList.get(i).getwLocationId() %>','O', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Optional Holiday</a></li>
						<%
							List<Map<String,String>> outerOptionalList = hmOptionalHolidayList.get(wLocationList.get(i).getwLocationId());
							for(int j=0; outerOptionalList != null && j < outerOptionalList.size(); j++){
								Map<String,String> hmInner = outerOptionalList.get(j);
						%>
							<li>
								<a title="Delete" href="AddHolidays.action?operation=D&holidayId=<%=hmInner.get("HOLIDAY_ID") %>&calendarYear=<%=request.getAttribute("calendarYear") %>&orgId=<%=request.getAttribute("strOrg") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this?')"  style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
								<a href="javascript:void(0)" onclick="editHoliday('<%=hmInner.get("HOLIDAY_ID") %>','<%=request.getAttribute("calendarYear") %>','<%=request.getAttribute("strOrg") %>','<%=wLocationList.get(i).getwLocationId() %>','O', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
								<%=hmInner.get("HOLIDAY_DESCRIPTION") %> <strong>Date:</strong><%=hmInner.get("HOLIDAY_DATE") %> <strong>Holiday Type:</strong> <%=hmInner.get("HOLIDAY_TYPE") %>
							</li>
						<%} %>
					</ul>
				</div>		
			</div>					
		<%} %>
     </div>	
</div>


 <div id="editHolidayID"></div>
 <div id="addHolidayID"></div>
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


