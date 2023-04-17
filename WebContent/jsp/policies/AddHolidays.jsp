<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@page import="com.konnect.jpms.select.FillDepartment"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="com.konnect.jpms.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%-- <sx:head/> --%>
<script src="<%= request.getContextPath()%>/scripts/color.js" type="text/javascript"></script>
<script type="text/JavaScript">
	var cp = new ColorPicker('window'); 
	var cp2 = new ColorPicker('window');

$(function () { 
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	$( "#holidayDate" ).datepicker({format: 'dd/mm/yyyy'});
	$("#wLocationName").multiselect({
		noneSelectedText: 'Select Location (required)'
	}).multiselectfilter();
	 $(".readonly").keydown(function(e){
	        e.preventDefault();
	    });
});	

function show_validation() {	
	dojo.event.topic.publish("show_validation");
}
	

function checkHoliday() {
	var orgId = document.getElementById("orgId").value;
	var strWlocation = document.getElementById("strWlocation").value;
	var holidayDate = document.getElementById("holidayDate").value;
	
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return false;
	} else {
  
		var xhr = $.ajax({
			url : "ValidateHoliday.action?holidayDate=" + holidayDate+"&orgId="+orgId+"&strWlocation="+strWlocation,
			cache : false,
			success : function(data) {
				if(data.length>1){
					alert(data);
					return false;
				}else{
					//document.getElementById('formID').submit();
					return true;
				}
				
			}
		});
	}
}

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

	var cp = new ColorPicker('window'); 
	var cp2 = new ColorPicker('window');
	 
	
</script> 
<div class="aboveform">
<%	Map<String,String> hmHoliday =(Map<String,String>)request.getAttribute("hmHoliday");
	if(hmHoliday==null) hmHoliday=new HashMap<String, String>();
	UtilityFunctions uF = new UtilityFunctions();
	String operation = (String) request.getAttribute("operation");
	String strType = (String) request.getAttribute("type");
%>
<!-- onsubmit="return checkHoliday();" -->
<s:form theme="simple" id="formID" action="AddHolidays" method="POST" cssClass="formcss" name="frm" >

	<s:hidden name="holidayId"/>
	<s:hidden name="orgId" id="orgId"/>
	<s:hidden name="calendarYear" id="calendarYear"/>
	<s:hidden name="strWlocation" id="strWlocation"></s:hidden>
	<s:hidden name="operation"></s:hidden>
	<s:hidden name="type"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
	<table border="0" class="table table_no_border">
		<tr><td colspan=2><s:fielderror/></td></tr>
		<tr>
			<td class="txtlabel alignRight">Date:<sup>*</sup></td>
			<td>
				<%if(operation!=null && operation.trim().equalsIgnoreCase("U")){ %>
					<input type="hidden" name="holidayDate" id="holidayDate" value="<%=uF.showData(hmHoliday.get("HOLIDAY_DATE"),"")%>"/>
					<%=uF.showData(hmHoliday.get("HOLIDAY_DATE"),"")%>
				<%} else { %>
					<input type="text" name="holidayDate" id="holidayDate" rel="0" class="validateRequired" value="<%=uF.showData(hmHoliday.get("HOLIDAY_DATE"),"")%>"/>
					<span class="hint">Select a date 	from the calendar.<span class="hint-pointer">&nbsp;</span></span>
				<%} %>
			</td>
		</tr>
		 
		<tr>
			<td class="txtlabel alignRight">Description:<sup>*</sup></td>
			<td>
				<input type="text" value="<%=uF.showData(hmHoliday.get("HOLIDAY_DESCRIPTION"),"")%>" name="holidayDesc" id="holidayDesc" rel="2" class="validateRequired" onmouseup="javascript:show_validation();return false;" />
				<span class="hint">Add a description for this holiday.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>		
		<%if((String)request.getAttribute("holidayId")==null){ %>
		<tr>
			<td class="txtlabel alignRight" valign="top">Select Location:<sup>*</sup></td>
			<td>
				<select rel="3" name="wLocationName" id="wLocationName" size="4" class="validateRequired" multiple="multiple">
							<% java.util.List  wLocationList = (java.util.List) request.getAttribute("wLocationList");%>
							<% for (int i=0; i<wLocationList.size(); i++) { %>
							<option value=<%= ((FillWLocation)wLocationList.get(i)).getwLocationId() %>><%= ((FillWLocation)wLocationList.get(i)).getwLocationName() %></option>
							<% } %>
				</select>
				<br>
                <span style="font-size: 11px;">Press Ctrl+ to select more options</span>
			</td>
		</tr>	
		<%} %>
		
		<tr>
			<td class="txtlabel alignRight">Choose Color:<sup>*</sup></td>
			<td>
				<%
				String strColorStyle = "";
				if(hmHoliday.get("COLOUR_CODE")!=null && !hmHoliday.get("COLOUR_CODE").trim().equals("")){
					strColorStyle = "style=\"background-color: "+hmHoliday.get("COLOUR_CODE")+";\"";
				}
				%>
				<input type="text" name="colourCode" id="colourCode" rel="4" class="validateRequired readonly" value="<%=uF.showData(hmHoliday.get("COLOUR_CODE"),"")%>" <%=strColorStyle %>/>
				<img align="left" style="cursor: pointer;position:absolute; padding:5px 0 0 5px" src="images1/color_palate.png" 
				id="pick1" onclick= "cp2.select(document.getElementById('formID').colourCode,'pick1'); return false;"/>
				<%-- <script type="text/JavaScript">cp.writeDiv();</script> --%>
				<p class="hint ml_25">Choose a colour for this holiday. This colour will be marked in timesheets and clock entries.
					<span class="hint-pointer">&nbsp;</span>
				</p> 
			</td>
		</tr>	
		<%
			if(strType!=null && strType.equals("O")){
				String defaultHolidayType = (String) request.getAttribute("defaultHolidayType");
		%>
			<tr>
				<td colspan="2">
					<input type="hidden" name="holidayType" id="holidayType" value="<%=defaultHolidayType %>"/>
				</td>
			</tr>
				
		<%} else {%>
		<tr>
			<td class="txtlabel alignRight">Holiday Type:<sup>*</sup></td> 
			<td>
				<s:radio name="holidayType" id="holidayType"  list="#{'FD':'Full Day','HD':'Half Day'}" value="defaultHolidayType" cssClass="validateRequired" />
			</td>
		</tr>	
		<%} %>
		<tr>
			<td>&nbsp;</td>
			<td>
				 <s:submit name="submit" cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/> 
				<!-- <input class="input_button" type="button" onclick="checkHoliday()" value="Save"> -->
			</td>
		</tr>

 	</table>
</s:form>

</div>

 

<%-- <s:action name="HolidayReportInner" executeResult="true"></s:action> --%>