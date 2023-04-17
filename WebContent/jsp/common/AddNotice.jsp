<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<script>
$(document).ready( function () {
	jQuery("#formAddNewRow").validationEngine();
});	
    $(function() {
        $( "#displayStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
        $( "#displayEndDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    });

	//addLoadEvent(prepareInputsForHints);     
</script>


<s:form theme="simple" action="AddNotice" method="POST" cssClass="formcss" id="formAddNewRow">

	<s:hidden name="noticeId"/>
	<s:hidden name="operation"/>
	
	<input type="hidden" rel="3"/>
	<table border="0" class="formcss" style="width:675px">

	<tr><td colspan=2><s:fielderror/></td></tr>
 
	<tr>
		<td class="txtlabel alignRight">Display Start Date:<sup>*</sup></td>
		<td><input type="text" name="displayStartDate" id="displayStartDate" rel="0" class="required"/>
		<span class="hint">Select a date from the calendar. This news will be published on this day onwards.<span class="hint-pointer t55">&nbsp;</span></span></td>
	</tr>
	
	<tr>
		<td class="txtlabel alignRight">Display End Date:<sup>*</sup></td>
		<td><input type="text" name="displayEndDate" id="displayEndDate" rel="3" class="required"/></td>
	</tr>
	
	<tr>
		<td class="txtlabel alignRight">Short Description:<sup>*</sup></td>
		<td><input type="text" name="heading" id="heading" rel="1" class="required"/>
		<span class="hint">Add a heading to the news.<span class="hint-pointer">&nbsp;</span></span></td>
	</tr>
	
	<tr>
		<td valign="top" class="txtlabel alignRight">Long Description:</td>
		<td><textarea cols="24" rows="5" name="content" id="content" rel="2" class="required" ></textarea>
		<span class="hint">Add news content here.<span class="hint-pointer">&nbsp;</span></span></td>
	</tr>
	
	<tr>
		<td colspan="2" align="center">
			<s:submit value="Publish" name="publish" cssClass="input_button"></s:submit> 
			<s:submit value="Unpublish" name="unpublish" cssClass="input_button"></s:submit>
		</td>
	</tr>
	
</table>
</s:form>

