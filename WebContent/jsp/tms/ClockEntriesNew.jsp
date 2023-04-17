<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<script>

function updateClockEntries(DATE,EID,paycycle,org,location,level) { 
	
	
	
	var dialogEdit = '#updateSettingDiv';

	
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 250,
				width : 300,
				modal : true,
				title : 'Update Clock Entries',
				open : function() {
					var xhr = $.ajax({  
						url : 'AddClockEntriesNew.action?type=V&strDate='+DATE+'&strEmpId='+EID+'&paycycle='+paycycle+'&org='+org+'&location='+location+'&level='+level,
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

<%
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
String empId = (String) request.getAttribute("empId");

UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 

String strPaycycleStartDate=(String)request.getAttribute("strPaycycleStartDate");
String strPaycycleEndDate=(String)request.getAttribute("strPaycycleEndDate");

String strEmpName=(String)request.getAttribute("empName");
Map<String,String> hmAttendanceIN =(Map<String,String> )request.getAttribute("hmAttendanceIN");
Map<String,String> hmAttendanceOUT =(Map<String,String> )request.getAttribute("hmAttendanceOUT");
Map<String,String> hmhoursWorked =(Map<String,String> )request.getAttribute("hmhoursWorked");
Map<String,Map<String,String>> rosterMap=(Map<String,Map<String,String>> )request.getAttribute("rosterMap");
Set<String> hmHalfWeekEnds=(Set<String>)request.getAttribute("hmHalfWeekEnds");
Set<String> hmWeekEnds =(Set<String>)request.getAttribute("hmWeekEnds");
Map<String,Map<String,String>> leaveTypeMap=(Map<String,Map<String,String>>)request.getAttribute("leaveTypeMap");
List<String> dateList=(List<String> )request.getAttribute("dateList");
Map<String,String> dateDisplayList=(Map<String,String>)request.getAttribute("dateDisplayList");
Map<String,Map<String,String>> holidayList=(Map<String,Map<String,String>> )request.getAttribute("holidayList");
String strTitle =  ((strUserType != null && strUserType.equalsIgnoreCase("EMPLOYEE") ? "My Clock Entries"  : "Clock Entries"));
%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title"/>
</jsp:include>



<div id="printDiv" class="leftbox reportWidth">

<div class="pagetitle" style="margin:0px;">
      <span>
      
      <% if(strUserType != null && strUserType.equalsIgnoreCase("EMPLOYEE")){
		%>    	
    	  
    	 <%="Pay cycle "+uF.getDateFormat(strPaycycleStartDate, IConstants.DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(strPaycycleEndDate, IConstants.DBDATE, CF.getStrReportDateFormat()) %> 
    	  
  	<%   	  
      
      }else if(strUserType != null && strEmpName!=null && strEmpName.length()>0){
  	  %>
    	  <%= strEmpName + " for pay cycle "+uF.getDateFormat(strPaycycleStartDate, IConstants.DBDATE, CF.getStrReportDateFormat())+"-"+uF.getDateFormat(strPaycycleEndDate, IConstants.DBDATE, CF.getStrReportDateFormat())%>
   	  <%
      }
      %>
      
      </span>
    </div> 
    
    
    
    <s:form theme="simple"  action="ClockEntriesNew" cssClass="formcss" >

<div class="filter_div">
    <div class="filter_caption">Filter</div>
    	   
              <%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
              <div style="float:left;margin-right:5px">
	              
	                            
	                <s:select theme="simple" name="f_org" listKey="orgId" listValue="orgName" headerValue="Select Organization" headerKey="0"
                       onchange="window.location='ClockEntriesNew.action?f_org='+this.value" list="orgList" />
                         
                   <s:select theme="simple" name="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" list="payCycleList" />
                         
	          </div> 
	          
	          <div  style="float:left;margin-right:5px" id="locationdivid">
					<s:select name="location"  theme="simple"
						listKey="wLocationId" listValue="wLocationName" headerKey="0"
						onchange="getContent('myDiv', 'GetEmployeeList.action?strMul=N&location='+this.value);"
						
						headerValue="Select Location" list="workLocationList" />
				</div>
	          
              <div style="float:left;margin-right:5px">
	                <s:select theme="simple" name="level" listKey="levelId" headerValue="All Levels"
	                            listValue="levelCodeName" headerKey="0" 
	                            
	                            onchange="getContent('myDiv', 'GetEmployeeList.action?strMul=N&level='+this.value);"
	                            list="levelList"/>
	                            
              </div>
              <div id="myDiv" style="float:left;margin-right:5px">
	            
	           <s:select theme="simple" label="Select Single Employee" name="strSelectedEmpId" listKey="employeeId" 
				listValue="employeeName" headerKey="0"  headerValue="Select Employee" list="empNamesList" key="" onchange="formSubmit();"  />
			</div>
			 <div style="float:left;margin-left:10px; width:auto;">
						
						
						<s:submit value="Submit" cssClass="input_button"cssStyle="margin:0px" />
					</div>
               <%}else {%> 
               <div style="float:left;margin-right:5px">
		                <s:select theme="simple"  name="paycycle" listKey="paycycleId"
		                            listValue="paycycleName" headerKey="0" 		
		                            list="payCycleList" key="" required="true" onchange="formSubmit();" />
               </div>
               <input type="hidden" name="strSelectedEmpId" value="<%=(String) session.getAttribute(IConstants.EMPID) %>" />
               <%}%>
	          
</div>	       
       
	</s:form>
	
	
	<table cellpadding="2" cellspacing="1" align="left" width="100%" class="tb_style">
	<tbody>
	<tr>
						<th nowrap="nowrap" class="reportHeading alignCenter">Date</th>
						
						<th nowrap="nowrap" class="reportHeading alignCenter">Roster/Standard Start Time<br/>(HH:mm)</th>
						<th nowrap="nowrap" class="reportHeading alignCenter">Approved [Actual] Start Time<br/>(HH:mm)</th>
						<th nowrap="nowrap" class="reportHeading alignCenter">Roster/Standard End Time<br/>(HH:mm)</th>
						<th nowrap="nowrap" class="reportHeading alignCenter">Approved [Actual] End Time<br/>(HH:mm)</th>
						<th nowrap="nowrap" class="reportHeading alignCenter">Roster Summary<br/>(hrs)</th> 
						<th nowrap="nowrap" class="reportHeading alignCenter">Day Summary<br/>(hrs)</th>
						<th nowrap="nowrap" class="reportHeading alignCenter">Variance<br/>(hrs)</th>
					</tr>
					<%
					double dblTotalRosterHrs=0.0;
					double dblTotalActualHrs=0.0;

					double dblTotalVarianceHrs=0.0;
					double dblTotalOTHrs=0.0;

					if(dateList!=null){
						Map<String,String> tempMap=new HashMap<String,String>();

						for(String date:dateList){
							Map<String,String> list=rosterMap.get(date);
							if(list==null)list=tempMap;
							double variance=uF.parseToDouble(hmhoursWorked.get(date))-uF.parseToDouble(list.get("ACTUAL_HOURS"));
							Map<String,String> holiday=holidayList.get(date);
							Map<String,String> leaveMp=leaveTypeMap.get(date);
							String color="";
							if(leaveMp!=null){
								color="style=\"background-color:"+leaveMp.get("COLOR_CODE")+"\"";
							}else if(holiday!=null){
								color="style=\"background-color:"+holiday.get("COLOR_CODE")+"\"";
							}else if(hmWeekEnds.contains(date)){
								color="style=\"background-color:#a9cfff\"";
							}else if(hmHalfWeekEnds.contains(date)){
								color="style=\"background-color:#dfffff\"";
							}
							dblTotalRosterHrs+=uF.parseToDouble(list.get("ACTUAL_HOURS"));
							dblTotalActualHrs+=uF.parseToDouble(hmhoursWorked.get(date));
							dblTotalVarianceHrs+=variance;

						%>
							
					<tr>
					<%if(leaveMp!=null){
						double leaveNo=uF.parseToDouble(leaveMp.get("LEAVE_NO"));
						%>
					<td class="alignLeft" <%=color %>><%=dateDisplayList.get(date)  %></td>
						<td class="alignCenter" <%=color %>><%=(leaveNo==.5)?"HALF/ ":""%> <%=leaveMp.get("LEAVE_CODE") %></td>
						<td class="alignCenter" <%=color %>><%=(leaveNo==.5)?"HALF/ ":""%> <%=leaveMp.get("LEAVE_CODE") %></td>
						<td class="alignCenter" <%=color %>><%=(leaveNo==.5)?"HALF/ ":""%> <%=leaveMp.get("LEAVE_CODE")  %></td>
						<td class="alignCenter" <%=color %>><%=(leaveNo==.5)?"HALF/ ":""%> <%=leaveMp.get("LEAVE_CODE") %></td>
						<%}else{%>
						
						<td class="alignLeft" <%=color %>><%=dateDisplayList.get(date) %></td>
						<td class="alignCenter" <%=color %>><%=list.get("IN")!=null?list.get("IN"):"-" %></td>
						<td class="alignCenter" <%=color %>><%=hmAttendanceIN.get(date)!=null?hmAttendanceIN.get(date):"-" %></td>
						<td class="alignCenter" <%=color %>><%=list.get("OUT")!=null?list.get("OUT"):"-"  %></td>
						<td class="alignCenter" <%=color %>><%=hmAttendanceOUT.get(date)!=null?hmAttendanceOUT.get(date):"-" %></td>
						<%} %>
					
						
						<td class="alignRight" <%=color %>><%=list.get("ACTUAL_HOURS")!=null?list.get("ACTUAL_HOURS"):"0.0" %></td>
						<td class="alignRight" <%=color %>><%=hmhoursWorked.get(date)!=null?hmhoursWorked.get(date):"0.0" %>
						
						<div id="myDiv_<%=date%>" style="float:right">
					<a href="javascript:void(0)" class="del" onclick="(confirm('Are you sure you want to delete this attendance?')?getContent('myDiv_<%=date%>','AddClockEntriesNew.action?type=D&strDate=<%=date %>&strEmpId=<%=empId%>'):'')">
						
					</a>
										 <a href="javascript:void(0)"  class="time_edit_setting" onclick="updateClockEntries('<%=date%>','<%=empId%>','<%=(String)request.getAttribute("paycycle") %>','<%=(String)request.getAttribute("f_org") %>','<%=(String)request.getAttribute("location") %>','<%=(String)request.getAttribute("level") %>');"> 
					</a>
					</div>
						</td>
						<td class="alignRight" <%=color %>><%=uF.formatIntoTwoDecimalWithOutComma(variance) %></td>
					</tr>
							
						<%}
						%>
						
					<%} %>
					
					<tr>
		<td colspan="5" class="alignRight padRight20"><strong>Total</strong></td>
		<td class="alignRight padRight20"><strong><%=uF.formatIntoOneDecimal(dblTotalRosterHrs)%></strong></td>
		<td class="alignRight padRight20"><strong><%=uF.roundOffInTimeInHoursMins(dblTotalActualHrs)%></strong></td>
		<td class="alignRight padRight20"><strong><%=uF.roundOffInTimeInHoursMins(dblTotalVarianceHrs)%></strong></td>
	</tr>
					</tbody>
					
	</table>
	
<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:#a9cfff;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div>Weekly Off</div>
<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:#dfffff;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div>HalfWeekly Off</div>
<%if(leaveTypeMap!=null){
	Set<String> set=leaveTypeMap.keySet();

	Iterator it=set.iterator();
	List<String> a=new ArrayList<String>();
	while(it.hasNext()){
		
		Map<String,String> list=leaveTypeMap.get(it.next());
		if(!a.contains(list.get("LEVE_TYPE_ID"))){
			a.add(list.get("LEVE_TYPE_ID"));
		%>
		<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=list.get("COLOR_CODE") %>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div><%=list.get("LEAVE_CODE") %></div>
		
	<%}}}%>
	
	<%if(holidayList!=null){
	Set<String> set1=holidayList.keySet();
	Iterator it1=set1.iterator();
	while(it1.hasNext()){
		Map<String,String> list=holidayList.get(it1.next());%>
		<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=list.get("COLOR_CODE") %>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div><%=list.get("HOLIDAY") %></div>
		
	<%}}%>
</div>
<div id="updateSettingDiv"></div>
