<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="java.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
 
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.min.js"> </script>
<script>
Query(document).ready(function(){
    jQuery("#formId").validationEngine();
});
</script>


<div class="reportWidth">

 
<s:form action="SubmitTicket" name="frm" id="formId" theme="simple">


<table class="tb_style">
	<tr>
		<th align="right">Select Topic<sup>*</sup></th>
		<td>
			<select name="ticketTopic" class="validateRequired">
				<option value="Product Support">Product Support</option>
				<option value="Site Suggestion">Site Suggestion</option>
			</select>
		</td>
	</tr>
	
	<tr>
		<th align="right" valign="top">Enter Your Query<sup>*</sup></th>
		<td><textarea name="strQuery" rows="5" cols="30" class="validateRequired"></textarea></td>
	</tr>
	
	<tr>
		<th align="right">Enter Your Name</th>
		<td><input type="text" name="strName"></td>
	</tr>
	
	<tr>
		<th align="right">Enter Your Contact Number</th>
		<td><input type="text" name="strContactNo"></td>
	</tr>
	
	<tr>
		<td colspan="2"><input type="submit" class="input_button" value="Submit" ></td>
	</tr>
</table>

</s:form>



<div style="float:left;width:100%;color:green;margin:5px;">
Alternatively, you can call us at +91 - 20 - 40096490 Ext 23
</div>

</div>