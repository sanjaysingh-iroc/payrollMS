<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<sx:head/>

<script>
function show_states() {
	dojo.event.topic.publish("show_states");
}

	addLoadEvent(prepareInputsForHints);
</script>


<div class="aboveform">
<h4><%=(request.getParameter("E")!=null)?"Edit":"Add" %> Suburb</h4>
<%
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>
<p class="message"><%=strMessage%></p>

<s:form theme="simple" action="AddCity" method="POST" cssClass="formcss">

	<s:hidden name="cityId" />
	
	<table border="0" class="formcss" style="width:675px">
	<tr><td colspan=2><s:fielderror/></td></tr>
	<tr>
	<td class="txtlabel alignRight">Suburb<sup>*</sup>:</td>
	<td><s:textfield name="cityName" label="City" required="true"/><span class="hint">Add new suburb here.<span class="hint-pointer">&nbsp;</span></span></td>
	</tr>
	
	<tr>
	<td class="txtlabel alignRight">Select Country<sup>*</sup>:</td>
	<td>
	<s:select label="Select Country" name="country" listKey="countryId"
		listValue="countryName" headerKey="0" headerValue="Select Country"
		onchange="javascript:show_states();return false;"
		list="countryList" key="" /><span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span>
	</td>
	</tr>
	

	
	<tr><td class="txtlabel alignRight">Select State<sup>*</sup>:</td><td><s:url id="states_url" action="GetStates" /> <sx:div href="%{states_url}" listenTopics="show_states" formId="frm_emp" showLoadingText=""></sx:div></td></tr>

	
	<%
		if (request.getParameter("E") != null) {
	%>
	<tr><td colspan="2" align="center">
	<s:submit cssClass="input_button" value="Update City" align="center" />
	</td></tr>
	<%
		} else {
	%>
	<tr><td colspan="2" align="center">
	<s:submit cssClass="input_button" value="Add City" align="center" />
	</td></tr>
	<%
		}
	%>
	</table>
</s:form>

</div>

