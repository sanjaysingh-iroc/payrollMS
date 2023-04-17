<%@ taglib uri="/struts-tags" prefix="s"%>


<%
String strServiceId=request.getParameter("SID");
String strEmpId=request.getParameter("EID");
String strDate=request.getParameter("DATE");
String strInOutStatus=request.getParameter("inOutStatus");// Started By Dattatray Date:06-10-21

//System.out.println("strInOutStatus : "+strInOutStatus);

%>
<!-- Started By Dattatray Date:06-10-21-->
<style>
.bootstrap-datetimepicker-widget.dropdown-menu {
        width: auto;
    }

    .timepicker-picker table td a span,
    .timepicker-picker table td,
    .timepicker-picker table td span {
        height: 25px !important;
        line-height: 25px !important;
        vertical-align: middle;
        width: 25px !important;
        padding: 0px !important;
    }
</style>
<!-- Ended By Dattatray Date:06-10-21-->
<script>

$(function(){
	$("body").on("click","#reasonSubmit",function(){
		$(".validateRequired").prop('required',true);
	});
	 $("input[name='strStartTime']").datetimepicker({format: 'HH:mm'});
	 $("input[name='strEndTime']").datetimepicker({format: 'HH:mm'});
});

function validateReason(){
	/* Started BY Dattatray Date:07-10-21 */
	var strEndTime = "";
	var strOutTimeFlag = "0";
	var strStartTime = "";
	var strInTimeFlag = "0";
	if(document.getElementById("strStartTime")){
		strStartTime = document.getElementById("strStartTime").value;
		strInTimeFlag = "1";
	}
	if(document.getElementById("strEndTime")){
		strEndTime = document.getElementById("strEndTime").value;
		strOutTimeFlag = "1";
	}
	var strReason = document.getElementById("strReason").value;
	if(strInTimeFlag == "1" && strStartTime=="") {
		alert('Please enter the valid In Time');
		return false;
	}else if(strOutTimeFlag == "1" && strEndTime=="") {
		alert('Please enter the valid Out Time');
		return false;
	} /* Ended BY Dattatray Date:07-10-21 */
	 if(strReason=="") {
		alert('Please enter the valid reason');
		return false;
	} 
	return true;
}

$("#frmAddReason").submit(function(e){
	e.preventDefault();
	if(validateReason()){
		var paycycle = document.getElementById("paycycle").value;
		var form_data = $("form[name='frmAddReason']").serialize();
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
			url : "AddReason.action",
			data: form_data,
			cache : false,
			success : function(res) {
				$("#divResult").html(res);
			},
			error : function(res) {
				$.ajax({
					url: 'UpdateClockEntries.action?paycycle='+paycycle,
					cache: true,
					success: function(result){
						$("#divResult").html(result);
			   		}
				});
			}
		});
    	
    	
	}
});

</script>


<s:form action="AddReason" name="frmAddReason" id="frmAddReason" theme="simple" method="POST">

	<s:hidden name="strServiceId"></s:hidden>
	<s:hidden name="strEmpId"></s:hidden>
	<s:hidden name="strDate"></s:hidden>
	<s:hidden name="inOutStatus"></s:hidden>
	<s:hidden name="paycycle" id="paycycle"></s:hidden>

<!-- Started By Dattatray Date:06-10-21-->
<table class="table table_no_border autoWidth">
<%
	if(strInOutStatus.equalsIgnoreCase("IN_OUT")){
%>
	<tr>
		<td>In Time:</td>
		<td nowrap="nowrap"><s:textfield name="strStartTime" id="strStartTime" cssStyle="width:65px !important;"/></td>
	</tr>
	<tr>
		<td nowrap="nowrap">Out Time:</td>
		<td><s:textfield name="strEndTime" id="strEndTime" cssStyle="width:65px !important;"/></td>
	</tr>
<%} else if(strInOutStatus.equalsIgnoreCase("OUT")){ %>
	<tr>
		<td nowrap="nowrap">Out Time:</td>
		<td><s:textfield name="strEndTime" cssStyle="width:65px !important;"/></td>
	</tr>
<%} else if(strInOutStatus.equalsIgnoreCase("IN")){ %>
	<tr>
		<td nowrap="nowrap">In Time:</td>
		<td><s:textfield name="strStartTime" id="strStartTime" cssStyle="width:65px !important;"/></td>
	</tr>
<%} %>
<!-- Ended By Dattatray Date:06-10-21-->
	</table>
	<s:textarea cols="35" rows="2" name="strReason" id="strReason"></s:textarea>
	<br/>
	<s:submit cssClass="btn btn-primary" name="reasonSubmit" id="reasonSubmit" value="Enter Reason"></s:submit>
</s:form>
