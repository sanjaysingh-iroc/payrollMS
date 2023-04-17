

<%@page import="org.apache.struts2.components.Param"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script type="text/javascript">
	function submitComment() {

		
		var comment = document.getElementById("trainerComment1").value;
		var completed = document.getElementById("isCompleted").value;

		var empID = document.getElementById("empID").value;
		var planID = document.getElementById("planID").value;

		var action = "InsertTrainerCommentAjax.action?trainerCommentHidden="+comment+"&empID="+empID+"&planID="+planID+"&isCompleted="+completed;

		$(this).dialog('close')  ;
	
		getContent('commentID', action);

		
		/* $find('#trainerCommentPOPUP').hide(); 
       
		$('#trainerCommentPOPUP').dialog('destroy').remove()
		
		$("#submit").click(function () {
            $(this).dialog('close');
        });*/
		
		 
        
	}
</script>


	<s:hidden name="planID" id="planID"></s:hidden>
	<s:hidden name="empID" id="empID"></s:hidden>
	
	<table>

		<tr>
			<td class="tdLabel" style="valign: top">Comments :</td>
			
			<td><s:textarea name="trainerComment" id="trainerComment1" theme="simple"
					rows="3" cols="40">
				</s:textarea></td>
		</tr>

		<tr>
			<td class="tdLabel" style="valign: top">Completed :</td>
		<td><s:checkbox name="isCompleted" id="isCompleted"  theme="simple" ></s:checkbox> 
			 </td>
		</tr>


		<tr>
			<td>&nbsp;</td>
			<td align="right"><input type="button" class="input_button" id="#submit"
				onclick="submitComment();" value="Submit"></td>
		</tr>

	</table>

	<div id="trainerCommentDIV"></div>


