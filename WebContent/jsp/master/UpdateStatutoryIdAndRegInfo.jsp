<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
$(document).ready( function () {
	jQuery("#formAddNewRow").validationEngine();
});

</script> 


<% 
UtilityFunctions uF = new UtilityFunctions();
Map<String, String> hmStatutoryIds = (Map<String, String>) request.getAttribute("hmStatutoryIds");
if(hmStatutoryIds == null) hmStatutoryIds = new HashMap<String, String>();
String fromPage=(String)request.getAttribute("fromPage");
//System.out.println("fromPage in jsp==>"+fromPage);
%>

<script>
$(document).ready(function(){
	
	var fromPage="<%=(String)request.getAttribute("fromPage")%>";
	//alert("fromPage=="+fromPage);
	if(fromPage == "CD")
		{
			$("#tblv").show();
			$("#tbl").hide();
		}else {
			$("#tblv").hide();
			$("#tbl").show();
		}
});
</script>

	<s:form theme="simple" id="formAddNewRow" action="UpdateStatutoryIdAndRegInfo" method="POST" cssClass="formcss">
		<s:hidden name="wlocationTypeId" />
		<s:hidden name="orgId"/>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table id=tbl hidden class="table table_no_border">
			<tr>
				<td class="txtlabel alignRight">Organisation Code:</td>
				<td> <%-- <s:textfield name="orgCode" id="idOrgCode" readonly="true"/> --%>
				<%=(String)request.getAttribute("orgCode") %>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Organisation Name:</td>
				<td> <%-- <s:textfield name="orgName" id="idOrgName" readonly="true"/> --%>
				<s:hidden name="orgName"/>
				<%=(String)request.getAttribute("orgName") %>
				</td>
			</tr>
	
			<tr> 
				<td class="txtlabel alignRight">MCA Registration No.:</td>
				<td>
					<%-- <s:textfield name="orgMCARegNo"/> --%>
					<input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_REG_NO"),"") %>"/>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">PAN No.:</td>
				<td><%-- <s:textfield name="orgStatutoryIds" id="orgPanNo"/> --%>
					<input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_PAN_NO"),"") %>"/>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">TAN No.:</td>
				<td> <%-- <s:textfield name="orgStatutoryIds" id="orgTanNo"/> --%> 
				<input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_TAN_NO"),"") %>"/>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">AIN Code:</td>
				<td> <%-- <s:textfield name="orgStatutoryIds" id="orgAINCode"/> --%>
					<input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_AIN_CODE"),"") %>"/>
				 </td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">ESIC No. (Employer):</td>
				<td><input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_ESIC_NO"),"") %>"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">TRRN (EPF):</td>
				<td><input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_TRRN_EPF"),"") %>"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">EPF Account No.:</td>
				<td><input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_EPF_ACC_NO"),"") %>"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Establishment Code No.:</td>
				<td><input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_ESTABLISH_CODE_NO"),"") %>"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">TDS Payment Code:</td>
				<td><input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_TDS_PAYMENT_CODE"),"") %>"/></td>
			</tr>
			
			<tr> 
				<td class="txtlabel alignRight">Service Tax Registration No.:</td>
				<td>
					<%-- <s:textfield name="orgSTRegNo"/> --%>
					<input type="text" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_ST_REG_CODE"),"") %>"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="center">
					<s:submit cssClass="btn btn-primary" value="Update" id="btnUpdate" /> 
				</td>
			</tr>
	
		</table>
		
	<!-- ****************************viewtable only*********************** -->	
		
		
	</s:form>

<table id=tblv hidden class="table table_no_border">
			<tr>
				<td class="txtlabel alignRight">Organisation Code:</td>
				<td> <%-- <s:textfield name="orgCode" id="idOrgCode" readonly="true"/> --%>
				<%=(String)request.getAttribute("orgCode") %>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Organisation Name:</td>
				<td> <%-- <s:textfield name="orgName" id="idOrgName" readonly="true"/> --%>
				<s:hidden name="orgName"/>
				<%=(String)request.getAttribute("orgName") %>
				</td>
			</tr>
	
			<tr> 
				<td class="txtlabel alignRight">MCA Registration No.:</td>
				<td>
					<%-- <s:textfield name="orgMCARegNo"/> --%>
					<input type="text" readonly="true" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_REG_NO"),"") %>"/>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">PAN No.:</td>
				<td><%-- <s:textfield name="orgStatutoryIds" id="orgPanNo"/> --%>
					<input type="text" readonly="true" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_PAN_NO"),"") %>"/>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">TAN No.:</td>
				<td> <%-- <s:textfield name="orgStatutoryIds" id="orgTanNo"/> --%> 
				<input type="text" readonly="true" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_TAN_NO"),"") %>"/>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">AIN Code:</td>
				<td> <%-- <s:textfield name="orgStatutoryIds" id="orgAINCode"/> --%>
					<input type="text" readonly="true" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_AIN_CODE"),"") %>"/>
				 </td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">ESIC No. (Employer):</td>
				<td><input type="text"readonly="true"  name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_ESIC_NO"),"") %>"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">TRRN (EPF):</td>
				<td><input type="text" readonly="true" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_TRRN_EPF"),"") %>"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">EPF Account No.:</td>
				<td><input type="text" readonly="true" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_EPF_ACC_NO"),"") %>"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Establishment Code No.:</td>
				<td><input type="text" readonly="true"  name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_ESTABLISH_CODE_NO"),"") %>"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">TDS Payment Code:</td>
				<td><input type="text" readonly="true" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_TDS_PAYMENT_CODE"),"") %>"/></td>
			</tr>
			
			<tr> 
				<td class="txtlabel alignRight">Service Tax Registration No.:</td>
				<td>
					<%-- <s:textfield name="orgSTRegNo"/> --%>
					<input type="text" readonly="true" name="orgStatutoryIds" value="<%=uF.showData(hmStatutoryIds.get("ORG_ST_REG_CODE"),"") %>"/>
				</td>
			</tr>
			
			<%-- <tr>
				<td colspan="2" align="center">
					<s:submit cssClass="btn btn-primary" value="Update" id="btnUpdate" /> 
				</td>
			</tr>
	 --%>
		</table>