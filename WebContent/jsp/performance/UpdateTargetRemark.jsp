
<%-- <%@ taglib prefix="s" uri="/struts-tags"%>

<div id="targetRemarkDiv">

	<title>Target Remark</title>

<form id="frm_targetremark" name="frm_targetremark" action="UpdateTargetRemark.action" method="post">
	<s:hidden name="targetID" />
	<s:hidden name="form" />
	<s:hidden name="updateRemark" value="Update"/>
	<div style="float:left; margin:10px;">
		<span style="float: left; margin: 5px; vertical-align: text-top;">Remark : </span> <sup>*</sup>
		<s:textarea id="targetRemark" name="targetRemark" cssClass="validate[required]" rows="2" cols="45"> </s:textarea>
	</div>
	<div style="float:left; margin:10px;">
	<input type="submit" class="input_button" name="update" value="Save"/>
	</div>
</form>
</div> --%>


<%String targetRemark = (String)request.getAttribute("LastRemark"); %>
<label><%=targetRemark %></label>
<br/>
<h5>Remark added successfully.</h5>