<%@taglib prefix="s" uri="/struts-tags" %>
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script type="text/javascript">

var dataDrop = $('#billingId').attr('listKey');
var dataListToSplit =  $("input[type=checkbox][name=multiselect_billingId]:checked").attr('billdetails');
</script>
<%-- <td valign="top">
	<s:select theme="simple" label="Select BillNo" name="billingId" id="billingId" listKey="billID"
		listValue="billNumber" list="billDetailsList" key="" onchange="addBillAmount();" multiple="true" />
</td> --%>
<td valign="top">
	<select name="billingId" id="billingId" onchange="addBillAmount();" multiple="true" >
	<%=request.getAttribute("sbBillList") %>
	</select>
</td>

