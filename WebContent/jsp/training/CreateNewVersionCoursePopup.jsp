<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div>
	<s:form id="formID" name="frmCreateNewVersionAssessmentPopup" theme="simple" action="CreateNewVersionCoursePopup"
		method="POST" cssClass="formcss">

		<table style="width: 100%"> <!-- class="tb_style" -->
			<tr>
				<!-- <th width="30%" align="right">Select Question</th> -->
				<td>This Course is already assigned. If you proceed to edit a new version will be created.
				<s:hidden name="courseId" id="courseId"></s:hidden>
				<%-- <select name="questionSelect" id="questionSelect" style="width: 80%;"><option value="">Select Question</option><%=opt %></select> --%>
				</td>
			</tr>
			 <tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<!-- <th align="right">&nbsp;</th> -->
				<td>Do you want to assign this version to existing assignees ?</td>
			</tr>

		</table>
		
		<div align="center">
			
			<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="no" value="No"/>
			<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="yes" value="Yes"/>
		</div>
	</s:form>
</div>
<script>

var submitActor = null;
var submitButtons = $('form').find('input[type=submit]').filter(':visible');
$("form").bind('submit',function(event) {
	  event.preventDefault();
	  if (null === submitActor) {
       // If no actor is explicitly clicked, the browser will
       // automatically choose the first in source-order
       // so we do the same here
       submitActor = submitButtons[0];
   }
	  var form_data = $("#"+this.id).serialize();
   	  var yesSubmit=$('input[name = yes ]').val();
   	  var noSubmit=$('input[name = no ]').val();
	  var submit = submitActor.name;
     
   	  if(submit != null && submit == "yes") {
 	    form_data = form_data +"&yes="+yesSubmit;
      } else if(submit != null && submit == "no"){
 	     form_data = form_data +"&no="+noSubmit;
      }
   
       $("#divCDResult").html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
	   $.ajax({
	   		type :'POST',
	   		url:'CreateNewVersionCoursePopup.action',
	   		data :form_data,
	   		success:function(result){
	   			$("#divCDResult").html(result);
	   		}
	   	});
   		
   		$(".modal").hide();
     
});

submitButtons.click(function(event) {
    submitActor = this;
});


</script>

