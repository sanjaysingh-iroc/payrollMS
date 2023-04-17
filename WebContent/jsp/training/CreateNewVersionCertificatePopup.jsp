<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<% String certiId = (String) request.getAttribute("certiId"); %>
<div>
	<s:form id="formID" name="frmCreateNewVersionCertificatePopup" theme="simple" action="CreateNewVersionCertificatePopup" method="POST" cssClass="formcss">

		<table style="width: 100%"> <!-- class="tb_style" -->
			<tr>
				<td>This Certificate is already assigned. If you proceed to edit a new version will be created.
				<s:hidden name="certiId" id="certiId"></s:hidden>
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
			<input type="button" class="btn btn-primary" style="float:right; margin-right: 5px;" name="no"  value="No" onclick="addCertificate('E', '<%=certiId %>', 'No');" />
			<input type="button" class="btn btn-primary" style="float:right; margin-right: 5px;" name="yes"  value="Yes" onclick="addCertificate('E', '<%=certiId %>', 'Yes');" />
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
       
       	$(".modal-body").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   		$.ajax({
		   		type :'POST',
		   		url:'CreateNewVersionCertificatePopup.action',
		   		data :form_data+"&",
		   		success:function(result){
		   			$(".modal-body").html(result);
		   		}
   			});
	   		
	});
	
	submitButtons.click(function(event) {
	    submitActor = this;
	});

</script>




