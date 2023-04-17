<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>



<script>
    $(function() {
        $( "#strFromDate" ).datepicker({format: 'dd/mm/yyyy',
        	onClose: function(selectedDate){
				$("#strToDate").datepicker("option", "minDate", selectedDate);
			}	
        });
        $( "#strToDate" ).datepicker({format: 'dd/mm/yyyy',
        	onClose: function(selectedDate){
				$("#strFromDate").datepicker("option", "maxDate", selectedDate);
			}	
        });
    });

    jQuery(document).ready(function(){
        // binds form submission and fields to the validation engine
    	$("input[name='submit']").click(function(){
    		$(".validateRequired").prop('required',true);
    		$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');	 		
    	});
    }); 
    
function showType(strRequisitionType){
	//trDocId trFromDateId trToDateId trTypeId 
	 document.getElementById('strDoc').selectedIndex = 0;
   	 document.getElementById('strInfraType').selectedIndex = 0;
   	 
   	 document.getElementById("strFromDate").value = "";
   	 document.getElementById("strToDate").value = "";
   	  	 
	if(parseInt(strRequisitionType) == 1){
		 document.getElementById("trDocId").style.display = "table-row";
		 $("#strDoc").prop('required',true);
		 $("#strFromDate").prop('required',false);
		 document.getElementById("trFromDateId").style.display = "none";
		 document.getElementById("trToDateId").style.display = "none";
		 $("#strInfraType").prop('required',false);
		 document.getElementById("trTypeId").style.display = "none";
	} else if(parseInt(strRequisitionType) == 2){
		$("#strDoc").prop('required',false);
		document.getElementById("trDocId").style.display = "none";
		document.getElementById("trFromDateId").style.display = "table-row";
		$("#strFromDate").prop('required',true);
		document.getElementById("trToDateId").style.display = "table-row";
		document.getElementById("trTypeId").style.display = "table-row";
		$("#strInfraType").prop('required',true);
	} else if(parseInt(strRequisitionType) == 3){ 
		$("#strDoc").prop('required',false);
		document.getElementById("trDocId").style.display = "none";
		$("#strFromDate").prop('required',false);
		document.getElementById("trFromDateId").style.display = "none";
		document.getElementById("trToDateId").style.display = "none";
		$("#strInfraType").prop('required',false);
		document.getElementById("trTypeId").style.display = "none";
		
	} else {
		$("#strDoc").prop('required',false);
		document.getElementById("trDocId").style.display = "none";
		$("#strFromDate").prop('required',false);
		document.getElementById("trFromDateId").style.display = "none";
		document.getElementById("trToDateId").style.display = "none";
		$("#strInfraType").prop('required',false);
		document.getElementById("trTypeId").style.display = "none";
		
	}
	
}
    
</script>
<div class="leftbox reportWidth">
<%UtilityFunctions uF = new UtilityFunctions(); %>
	<s:form id="formID" name="frmRequisition" theme="simple" action="AddNewRequisition" method="POST" cssClass="formcss">
		<s:hidden name="policy_id" id="policy_id"></s:hidden>
		<div style="width: 98%; float: left;">
			<table border="0" class="table">
				<tr>
					<td class="txtlabel alignRight">Requisition Type:<sup>*</sup></td>
					<td>
					<s:select name="strRequisitionType" id="strRequisitionType" theme="simple" cssClass="validateRequired" listKey="requiTypeId" listValue="requiTypeName" 
						headerKey="" headerValue="Select Requisition Type" list="requisitionTypeList" key="" onchange="showType(this.value);"/>
					</td>					
				</tr>
				<tr id="trDocId" style="display: none;">
					<td class="txtlabel alignRight">Select Document:<sup>*</sup></td>
					<td>
					<s:select name="strDoc" id="strDoc" theme="simple" listKey="activityId" listValue="activityName" 
						headerKey="" headerValue="Select Document" list="docList" key="" />
					</td>
				</tr> 
				<tr id="trFromDateId" style="display: none;">
					<td class="txtlabel alignRight" valign="top">From:<sup>*</sup></td>
					<td><s:textfield name="strFromDate" id="strFromDate"/></td>
				</tr>
				
				<tr id="trToDateId" style="display: none;">
					<td class="txtlabel alignRight" valign="top">To:</td>
					<td><s:textfield name="strToDate" id="strToDate"/></td>
				</tr>
				
						
				<tr id="trTypeId" style="display: none;">
					<td class="txtlabel alignRight" valign="top">Type:<sup>*</sup></td>
					<td colspan="3">
						<s:select name="strInfraType" id="strInfraType" id="strInfraType" listKey="strInfraTypeId" listValue="strInfraTypeName" headerKey=""
							headerValue="Select Type" list="infraTypeList"/>
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight" valign="top">Purpose:<sup>*</sup></td>
					<td><s:textarea name="strPurpose" rows="5" cols="22" cssClass="validateRequired"></s:textarea></td>
				</tr>
				<%=uF.showData((String)request.getAttribute("requisitionD"),"") %>
				
				<%-- <tr>
					<td class="txtlabel alignRight">&nbsp;</td>
					<td><s:submit value="Submit" cssClass="input_button"></s:submit></td>
				</tr> --%>
			</table>
		</div>
	</s:form>
 </div>
