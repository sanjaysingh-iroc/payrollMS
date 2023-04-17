<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript">

$(function() {
	$("#paidDate").datepicker({format : 'dd/mm/yyyy'});
	
	$("#strSubmit").click(function(){
		$(".validateRequired").prop('required', true);
	});
});


$("#formEPFUpdateChallanData").submit(function(e){
	e.preventDefault();
	var form_data = $("form[name='formEPFUpdateChallanData']").serialize();
   	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	$.ajax({
		url : "EPFUpdateChallanData.action",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		},
		error: function(result){
			$.ajax({
				url: 'EPFChallan.action',
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
	});
});

</script>

<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();
String payAmount=(String)request.getAttribute("payAmount") ;
String challanDate=(String)request.getAttribute("challanDate");
String f_org = (String) request.getAttribute("f_org");
String strCurrency = (String) request.getAttribute("strCurrency"); 

String sbEmp=(String) request.getAttribute("sbEmp"); 
//System.out.println(" sbEmp in PayEPFPrintedChallan.jsp==>"+sbEmp);
%>

	<s:form id="formEPFUpdateChallanData" action="EPFUpdateChallanData" method="post" name="formEPFUpdateChallanData" enctype="multipart/form-data" theme="simple">		
		<div style="float: center" id="tblDiv">
		<s:hidden name="challanDate"></s:hidden>
		<s:hidden name="financialYear"></s:hidden>
		<s:hidden name="operation" value="update"></s:hidden>
		<input type="hidden" name="f_org" value="<%=f_org%>"/>
		<input type="hidden" name="sbEmp" value="<%=sbEmp%>"/>
		
			<table border="0" class="table table-bordered">
				<tr>
					<td><b>Challan printed on  <%=uF.getDateFormat(challanDate, IConstants.DBDATE, CF.getStrReportDateFormat())%>  for <%=uF.showData(strCurrency,"")%> <%=payAmount%>.</b></td>
				</tr>
			</table>
			
			<table border="0" class="table table-bordered">
				<tr>
					<td  class="txtlabel alignLeft">Challan No.:<sup>*</sup></td>
					<td  class="txtlabel alignLeft"><s:textfield name="challanNum" cssClass="validateRequired"></s:textfield></td>
				</tr>
				<tr>
					<td class="txtlabel alignLeft">Paid Date.:<sup>*</sup></td>
					<td class="txtlabel alignLeft"><s:textfield id="paidDate" name="paidDate" cssClass="validateRequired"></s:textfield> </td>
				</tr>
				<tr>
					<td class="txtlabel alignLeft">Cheque No.:</td>
					<td class="txtlabel alignLeft"><s:textfield id="cheque_no" name="cheque_no"></s:textfield> </td>
				</tr>
			</table>
		</div>		
		<div class="clr"></div>
		<div style="margin: 0px 0px 0px 210px">
			<table border="0" class="table table-bordered">
				<tr>
					<td class="txtlabel alignLeft"><s:submit value="Submit" cssClass="btn btn-primary" name="strSubmit" id="strSubmit" />
					</td>
					<td></td>
				</tr>
			</table>
		</div>
	</s:form>
