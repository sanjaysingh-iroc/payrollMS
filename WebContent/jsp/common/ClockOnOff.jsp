<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 

<%-- <sx:head extraLocales="en-us"/> --%>

<script type="text/javascript">
$(document).ready(function() {
	getContent('clockLabel', 'GetClockLabel.action');
	getContent('clockMsg', 'GetClockEntryMessage.action');
}
</script>

<div id="involmentcontainer">
	<s:form id="frmClockEntries" name="frmClockEntries" theme="simple" action="ClockOnOffEntry">


		<h2>
			<%-- <s:url id="clockLabelUrl" action="GetClockLabel" />
			<sx:div onclick="return confirmMsg(this.innerHTML);" href="%{clockLabelUrl}" listenTopics="showClockLabel" formId="frmClockEntries" showLoadingText=""></sx:div> --%>
			<div id="clockLabel"></div>
		</h2>

		<s:hidden name="strClock"></s:hidden>

		<div class="clr"></div>
		<div class="clockon_content">

			<div class="clockon_content">
				It is <%=request.getAttribute("CURRENT_DATE")%></div>
			<div class="clockon_content_time"><%-- <%=request.getAttribute("CURRENT_TIME")%> --%><div id="myTime" style="text-align: left; margin-left: 15%"></div>
			</div>

			<div class="clockon_content"><%=(request.getAttribute("ROSTER_TIME") != null) ? request.getAttribute("ROSTER_TIME") : ""%></div>
			<%-- <s:url id="clockMessageUrl" action="GetClockEntryMessage" />
			<sx:div href="%{clockMessageUrl}" listenTopics="showClockMessage" formId="frmClockEntries" showLoadingText=""></sx:div> --%>
			<div id="clockMsg"></div>
		</div>
	</s:form>
</div>
