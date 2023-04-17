<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@page import="com.konnect.jpms.select.FillDepartment"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="com.konnect.jpms.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%-- <sx:head/> --%>

<script type="text/javascript">
$(function() {
    $( "#holidayDate" ).datepicker({dateFormat: 'dd/mm/yy'});
});
 
function show_validation() {	
	dojo.event.topic.publish("show_validation");
}
	addLoadEvent(prepareInputsForHints);
</script>

<script src="<%= request.getContextPath()%>/scripts/color.js" type="text/javascript"></script>
<script type="text/JavaScript">
	var cp = new ColorPicker('window'); 
	var cp2 = new ColorPicker('window');
	
	
</script> 

<div class="aboveform">
<%-- <h4><%=(request.getParameter("E")!=null)?"Edit":"Add" %> Public Holidays</h4>
<%
	String strEmpType = (String) session.getAttribute("USERTYPE");
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%> 

<p class="message"><%=strMessage%></p> 

<%-- onsubmit="javascript:generateCode();" --%>

<s:form theme="simple" id="formAddNewRow" action="AddHolidays1" method="POST" cssClass="formcss" name="frm" cssStyle="display: none;">

	<s:hidden name="holidayId" />
	

	<table border="0" class="formcss" style="width:675px">

		<tr><td colspan=2><s:fielderror/></td></tr>

		<tr>
			<td class="txtlabel alignRight">Date<sup>*</sup>:</td>
			<td><input type="text" name="holidayDate" id="holidayDate" rel="0" class="required"/>
			<span class="hint">Select a date from the calendar.<span class="hint-pointer">&nbsp;</span></span>
			<s:url id="holiday_url" action="ValidateHoliday" /> 
			<sx:div href="%{holiday_url}" listenTopics="show_validation" formId="formAddNewRow" showLoadingText=""></sx:div> 
		</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Description<sup>*</sup>:</td>
			<td><input type="text" name="holidayDesc" id="holidayDesc" rel="2" class="required" onmouseup="javascript:show_validation();return false;" />
			<span class="hint">Add a description for this holiday.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>		
		
		<tr>
			<td class="txtlabel alignRight">Select Location<sup>*</sup>:</td>
			<td>
				<select rel="3" name="wLocationName" id="wLocationName" size="4" class="required" multiple="multiple">
							<% java.util.List  wLocationList = (java.util.List) request.getAttribute("wLocationList");%>
							<% for (int i=0; i<wLocationList.size(); i++) { %>
							<option value=<%= ((FillWLocation)wLocationList.get(i)).getwLocationId() %>><%= ((FillWLocation)wLocationList.get(i)).getwLocationName() %></option>
							<% } %>
				</select>
			</td>
			
			
		</tr>	
		
		<tr>
			<td class="txtlabel alignRight">Choose Color<sup>*</sup>:</td>
			<td><input type="text" name="colourCode" id="colourCode" rel="4" class="required"/>
			<img align="left" style="cursor: pointer;position:absolute; padding:5px 0 0 5px" src="images1/color_palate.png" 
			id="pick1" onclick= "cp2.select(document.getElementById('formAddNewRow').colourCode,'pick1'); return false;"/>
			<%-- <script type="text/JavaScript">cp.writeDiv();</script> --%>
			<span class="hint ml_25">Choose a colour for this holiday. This colour will be marked in timesheets and clock entries.
				<span class="hint-pointer">&nbsp;</span>
			</span> </td>
		</tr>	
		<tr>
					<td></td>
					<td><s:submit cssClass="input_button" value="Cancel" id="btnAddNewRowCancel"/>
					<s:submit  cssClass="input_button" value="Ok" id="btnAddNewRowOk"/></td>
		</tr>

<%-- <tr>
	<td class="txtlabel alignRight">Date<sup>*</sup>:</td>
	<td><s:textfield name="holidayDate" id="idDate" label="Date" required="true" />
	<span class="hint">Select a date from the calendar.<span class="hint-pointer">&nbsp;</span></span>
	<s:url id="holiday_url" action="ValidateHoliday" />
	<sx:div href="%{holiday_url}" listenTopics="show_validation" formId="frm_holiday" showLoadingText=""></sx:div>
</td>
</tr>

<tr>
	<td class="txtlabel alignRight">Description<sup>*</sup>:</td>
	<td><s:textfield name="holidayDesc" label="Description" required="true" onmouseup="javascript:show_validation();return false;" /><span class="hint">Add a description for this holiday.<span class="hint-pointer">&nbsp;</span></span></td>
</tr>		

<tr>
	<td class="txtlabel alignRight">Choose Color<sup>*</sup>:</td>
	<td><s:textfield name="colourCode"  label="Select Color" id="colourCode" required="true"/><img align="left" style="cursor: pointer;position:absolute; padding:5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick= "cp2.select(document.getElementById('frm_holiday').colourCode,'pick1'); return false;"/><script type="text/JavaScript">cp.writeDiv();</script><span class="hint ml_25">Choose a colour for this holiday. This colour will be marked in timesheets and clock entries.<span class="hint-pointer">&nbsp;</span></span></td>
</tr>	
		
	<%
		if (request.getParameter("E") != null) {
	%>
		<tr><td colspan="2" align="center">
	<s:submit cssClass="input_button" value="Update Holiday" align="center" />
	</td></tr>
	<%
		} else {
	%>
		<tr><td colspan="2" align="center">
	<s:submit cssClass="input_button" value="Add Holiday" align="center" />
	</td></tr>
	<%
		}
	%>
 --%>
 	</table>
</s:form>

</div>

<%-- <s:action name="HolidayReportInner" executeResult="true"></s:action> --%>