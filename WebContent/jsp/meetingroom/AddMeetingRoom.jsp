<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="com.konnect.jpms.select.FillUserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script src="<%= request.getContextPath()%>/scripts/color.js" type="text/javascript"></script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
	$(document).ready( function () {
		$("input[name='strSubmit']").click(function(){
			$(".validateRequired").prop('required',true);
		});
		$("input[name='strUpdate']").click(function(){
			$(".validateRequired").prop('required',true);
		});
	});
	var cp = new ColorPicker('window'); 
	var cp2 = new ColorPicker('window');

	
	$("#strSeating_Capacity").blur(function (e) {
	    var strCapacity = jQuery('#strSeating_Capacity').val();
	    if(strCapacity <= 0 )
	    {
	        alert('Seating Capacity should be greater than 0');

	    }   
	});
	

function isOnlyNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	      return true;
	   }
	   return false;
	}

function getLocationOrg(orgid){
	var action='GetLocationOrg.action?strOrg='+orgid ;
	getContent('locationdivid', action);
}

function checkCapacity() {
	 var seatingCapacity = document.getElementById("strSeating_Capacity").value; 
	 if(parseInt(seatingCapacity) <= 0 ) {
		alert("Seating capacity should be greater than 0.");
		return false;
	 }
	
	return true;
}

</script>

<%

String meetingRoomId = (String)request.getAttribute("meetingRoomId");
String operation = (String)request.getAttribute("operation");
%>
 

<div id="printDiv" class="leftbox reportWidth">
		<s:form id="frmAddMeetingRoom" name ="frmAddMeetingRoom" action="AddMeetingRoom.action" method="POST" theme="simple" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkCapacity();">
			<s:token>
			<s:hidden name="meetingRoomId" />
			<s:hidden name="operation" />
				<div style="float: left;" >
					<table class="table table_no_border form-table">
					    <tr><td height="10px">&nbsp;</td></tr>
					    <tr>
							<td class="txtlabel alignRight">Select Organization:<sup>*</sup></td>
							<td>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssClass="validateRequired" listValue="orgName" 
									onchange="getLocationOrg(this.value);" list="orgList" />
								
							</td>
						</tr>	
						<tr>
							<td class="txtlabel alignRight">Select Location:<sup>*</sup></td>
							<td>
							    <div id="locationdivid">
									<s:select theme="simple" name="location" id="location" listKey="wLocationId" theme="simple" cssClass="validateRequired"
									listValue="wLocationName" list="wLocationList" />
								</div>
							</td>
						</tr>	
					    <tr>
							<td class="txtlabel alignRight">Meeting Room Name:<sup>*</sup></td>
							<td>
								<s:textfield  name="strMeetingRoomName" id="strMeetingRoomName" cssClass="validateRequired" ></s:textfield>
							</td>
						</tr>	
						
						 <tr>
							<td class="txtlabel alignRight">Length:</td>
							<td>
								<s:textfield  name="strRoom_Length" id="strRoom_Length" onkeypress="return isNumberKey(event)"></s:textfield>
							</td>
						</tr>	
						
						<tr>
							<td class="txtlabel alignRight">Width:</td>
							<td>
								<s:textfield  name="strRoom_Width" id="strRoom_Width" onkeypress="return isNumberKey(event)"></s:textfield>
							</td>
						</tr>	
						 
						<tr>
							<td class="txtlabel alignRight">Seating Capacity:<sup>*</sup></td>
							<td>
								<s:textfield  name="strSeating_Capacity" id="strSeating_Capacity" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)"></s:textfield>
							</td>
						</tr>
						<tr>
							<td class="txtlabel alignRight">Room Color:<sup>*</sup></td>
							<td valign="top">
							<%if(operation !=null && operation.equals("E")) { %>
							<input type="text" name="strRoom_color_code" value="<%=request.getAttribute("strRoom_color_code") %>" id="colourCode" class="validateRequired" style="width: 67px !important;; background:<%=request.getAttribute("strRoom_color_code") %>; height: 20px !important;"  readonly="readonly"/>
							<% } else { %>
							<input type="text" name="strRoom_color_code" value="#efefef" id="colourCode" class="validateRequired" style="width: 67px !important;; background:#efefef;height:20px !important;;" class="validateRequired" readonly="readonly"/>
							<% } %> 
							<img align="left" src="images1/color_palate.png" id="pick1" onclick="cp2.select(document.getElementById('frmAddMeetingRoom').colourCode,'pick1'); return false;" />
						</td>
						</tr>
						<%if(operation !=null && operation.equals("E")) { %>
						 <tr>
						    <td></td>
						 	<td colspan="2" align="center"><s:submit name="strUpdate" cssClass="btn btn-primary" value="Update" align="center"/></td>
						 </tr>
						<% } else { %>
						 <tr>
						   <td></td>
						 	<td colspan="2" align="center"><s:submit name="strSubmit" cssClass="btn btn-primary" value="Submit" align="center"/></td>
						 </tr>
						 <% } %>
					
					  </table>
				</div>
			</s:token>	
		</s:form>
</div>
