<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
var cnt=0;
function addBoking() {
	//alert("dfgsgfdf");
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_booking"+cnt;
	divTag.innerHTML = 	"<div>"+
						"<input type=\"file\" name=\"strBooking\">"+ 
    			    	"<a href=\"javascript:void(0)\" onclick=\"addBoking()\" class=\"add-font\"></a>" +
    			    	"<a href=\"javascript:void(0)\" onclick=\"removeBoking(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a>"+
    			    	"</div>"+
    			    	"<div class=\"clr\"/>"; 
    document.getElementById("div_booking").appendChild(divTag);
     
}

function removeBoking(removeId) {
	var remove_elem = "row_booking"+removeId;
	var row_booking = document.getElementById(remove_elem); 
	document.getElementById("div_booking").removeChild(row_booking);
}


</script>

<div>
	<s:form id="formID" theme="simple" name="frm_TravelBookingAttachment" action="TravelBookingAttachment" enctype="multipart/form-data"  method="post">
		<s:hidden name="travelId"/>
		<s:hidden name="strEmpId"/>
		<table class="table table_no_border form-table">
			<tr>
				<td valign="top">Attach Document:</td>
				<td colspan="3">
					<s:file name="strBooking"/>
					<a href="javascript:void(0)" onclick="addBoking()" class="add-font"></a>
					<div id="div_booking"></div>
				</td>
			</tr>
			<tr>
				<td class="label" valign="top">&nbsp;</td>
				<td colspan="3"><s:submit value="Submit" cssClass="btn btn-primary" name="submit" ></s:submit></td>
			</tr>
		</table>
	</s:form>
</div>