<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<sx:head/>

<script>
function show_details() {
dojo.event.topic.publish("show_detail");


}
</script>

<s:form  id="frm_demo" theme="ajax">
<table border="0">
<tr>
<td><s:select list="lstList1" name="lst"
onchange="javascript:show_details();return false;" ></s:select>
</td>
<td><s:url id="d_url" action="DetailAction" /> <sx:div id="details" href="%{d_url}" listenTopics="show_detail" formId="frm_demo" showLoadingText=""></sx:div>

</tr>
<s:file />


</table>






</s:form>

