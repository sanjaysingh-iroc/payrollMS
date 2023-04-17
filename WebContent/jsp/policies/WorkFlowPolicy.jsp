


<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready( function () {
	$("#submitButton").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	
/* $(function() {
    //$( "#effectiveDate" ).datepicker({dateFormat: 'dd/mm/yy'}); 
	$("input[name=effectiveDate]").datepicker({dateFormat: 'dd/mm/yy'});
}); */


function getSelectedEmp(checked,emp){
	var empselect=document.getElementById("empselected").value;
	var oldempids=document.getElementById("oldempids").value;
	var alrtMsg = "";
	if(checked == true) {
		alrtMsg = "Are you sure, you want to add this employee?";
	} else {
		alrtMsg = "Are you sure, you want to remove this employee?";
	}
	if(confirm(alrtMsg)) {	
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetAnyOneEmployeeAjax.action?chboxStatus="+checked+"&selectedEmp="+emp+"&existemp="+empselect+
						"&oldempids="+oldempids,		
				cache : false,
				success : function(data) {
					//alert("data == "+data);
                	if(data == "") {
                	} else {
                		/* var allData = data.split("::::");
                        document.getElementById("idEmployeeInfo").innerHTML = allData[0];
                        parent.document.getElementById("selectedEmpIdSpan").innerHTML = allData[1];
                        if(checked == 'false') {
                        	document.getElementById("strEmpId_"+emp).checked = false;
                        } */
                        document.getElementById("idEmployeeInfo").innerHTML = data;
                        if(checked == 'false') {
                        	document.getElementById("strEmpId_"+emp).checked = false;
                        }
                	}
                }
			});
		}
	} else {
		document.getElementById("strEmpId_"+emp).checked = false;
	}
} 

function GetXmlHttpObject() {
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		return new XMLHttpRequest();
	}
	if (window.ActiveXObject) {
		// code for IE6, IE5
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
	return null;
}

</script>

<%
	UtilityFunctions uf=new UtilityFunctions();
	List<List<String>> outerList = (List<List<String>>) request.getAttribute("outerList");
	int mem_count =(Integer) request.getAttribute("mem_count");
	Map<String, String> hmSelected = (Map<String, String>) request.getAttribute("hmSelected");
	Map<String, String> hmSelectedSecond = (Map<String, String>) request.getAttribute("hmSelectedSecond");

	String anyOne = (String) request.getAttribute("anyOne");
	String anyOneType=(String) request.getAttribute("anyOneType");
	
	List<Map<String, String>> alEmpList = (List<Map<String, String>>) request.getAttribute("alEmpList");
	if(alEmpList == null) alEmpList = new ArrayList<Map<String,String>>();
	List<String> alSelectedEmp = (List<String>)request.getAttribute("alSelectedEmp");
	if(alSelectedEmp == null) alSelectedEmp = new ArrayList<String>();
	List<Map<String, String>> alSelectedEmpMap = (List<Map<String, String>>) request.getAttribute("alSelectedEmpMap");
	if(alSelectedEmpMap == null) alSelectedEmpMap = new ArrayList<Map<String,String>>();
	String sbEmp = (String) request.getAttribute("sbEmp");
%>

	<div class="aboveform">
	<s:form theme="simple" id="formAddNewRow" action="WorkFlowPolicy" method="POST" cssClass="formcss">
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="pcount"></s:hidden>
		<s:hidden name="group_id"></s:hidden>
		<input type="hidden" name="anyOne" id="anyOne" value="<%=anyOne %>">
		<input type="hidden" name="anyOneType" id="anyOneType" value="<%=anyOneType %>">
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
	
		<div style="float: left; width: 100%;">
			<div style="float: left; width: 100%;">
			<s:hidden name="organization"></s:hidden>
			<s:hidden name="location"></s:hidden>
				<table border="0" class="table table_no_border" style="width: 100%;">	
					<tr>
						<td nowrap="nowrap" class="txtlabel" align="right">Work Flow Policy Name :<sup>*</sup></td>
						<td>
							<input type="text" name="policyName" id="policyName" class="validateRequired text-input" value="<%=(String) (request.getAttribute("pname") != null ? request.getAttribute("pname") : "")%>"/>
						</td>	
					</tr>
					<%
						if (uf.parseToInt(anyOneType)<3) {
					%>
					<tr>
						<td nowrap="nowrap" class="txtlabel" valign="top" align="right">Workflow: </td>
						<td>
							<table border="0" class="table table_no_border" align="left">
									<tr>
										<th>&nbsp;</th>
										<%
											for (int i = 0; outerList != null && i < outerList.size(); i++) {
														List<String> memList = outerList.get(i);
										%>
										<th class="alignCenter"><%=memList.get(1)%></th>
										<%
											}
										%>
	
									</tr>
									<%
										if (outerList != null && mem_count > 0) {
	
											for (int j = 0; j < mem_count; j++) {
									%>
									<tr>
										<td align="center">Step - <%=(j+1) %></td>
										<%
											for (int i = 0; outerList != null && i < outerList.size(); i++) {
												List<String> memList = outerList.get(i);
										%>
										<%-- value="<%=memList.get(3)+"." + (j+1) %>" --%>
										
										<td align="center"><input type="radio" value="<%=(j + 1)%>"
											id="<%=memList.get(0)%>" name="<%=memList.get(0)%>"
											<%if (hmSelected != null && hmSelected.get(memList.get(0).trim())!=null && hmSelected.get(memList.get(0).trim()).equals("" + (j + 1))) {%>
											checked="checked" <%}%>></td>
										<%
											}
										%>
									</tr>
									<%
										}
												}
									%>
								</table> 
							</td>
						</tr>
						<%}else if (uf.parseToInt(anyOneType)==3) {%>
							<tr>
								<td nowrap="nowrap" class="txtlabel" valign="top" align="right">Workflow: </td>
								<td>
									<%="Any One" %>
									<input type="hidden" name="anyOneRegular" id="anyOneRegular" value="<%=anyOne %>">
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<div style="overflow-y: auto; height: 200px; margin-top: 35px;border: 2px solid #ccc; width: 46%;float:left;">
										<table class="table" width="100%">
											<tr>
												<th width="10%"><input onclick="checkUncheckValueInd();" type="checkbox" name="allEmpInd" id="allEmpInd"></th>
												<th align="center">Employee Code</th>
												<th align="center">Employee</th>
											</tr>
											<%
											for(int i=0; alEmpList!=null && i<alEmpList.size(); i++){
												Map<String, String> hmEmp = (Map<String, String>) alEmpList.get(i);
											%>
												<tr>
													<td>
														<input type="checkbox" name="strEmpId" id="strEmpId_<%=hmEmp.get("EMP_ID")%>" 
														value="<%=hmEmp.get("EMP_ID")%>" onclick="getSelectedEmp(this.checked,this.value);" 
														<%if (alSelectedEmp != null && alSelectedEmp.contains(hmEmp.get("EMP_ID"))) {%>  checked="checked" <%}%>/>
													</td>
													<td><%=hmEmp.get("EMP_CODE")%></td>
													<td><%=hmEmp.get("EMP_NAME")%></td>
									
												</tr>
											<%} %>
										</table>
									</div>
									
									<div id="idEmployeeInfo" style="border: 2px solid rgb(204, 204, 204); float: left; width: 46%; overflow-y: auto; padding: 4px; margin-top: 35px; height: 190px; margin-left: 25px;">
										<div align="center" style="border: 1px solid rgb(204, 204, 204);"><b>Approval Employee</b></div>
										<%if(alSelectedEmpMap!=null && !alSelectedEmpMap.isEmpty() && alSelectedEmpMap.size() >0){
											for(int i=0;i<alSelectedEmpMap.size();i++){
												Map<String, String> hmEmp = (Map<String, String>) alSelectedEmpMap.get(i);
										%>
												<div style="float: left; width: 100%; margin: 5px;"><strong><%=(i+1)%>.</strong>&nbsp;&nbsp;<%=hmEmp.get("EMP_NAME")%>&nbsp;&nbsp;
													<a href="javascript: void(0)" onclick="getSelectedEmp('false','<%=hmEmp.get("EMP_ID")%>');">
														<img border="0" style="width: 12px; height: 12px;" src="images1/arrow_reset1.png"/> 
													</a>
												</div>
										<%	}
										  } else { %>
											<div class="nodata msg" style="width:85%;"><span>No Employee selected</span></div>
										<%} %>
										<input type="hidden" name="oldempids" id="oldempids" value="<%=sbEmp!=null && !sbEmp.equals("") ? sbEmp :"0" %>"/>
										<input type="hidden" name="empselected" id="empselected" value="<%=sbEmp!=null && !sbEmp.equals("") ? sbEmp :"0" %>"/>	
									</div>								
								</td>
							</tr>
						<%}%>				
				</table>
				<%-- <span id="selectedEmpIdSpan">
					<input type="hidden" name="oldempids" id="oldempids1" value="0"/>
					<input type="hidden" name="empselected" id="empselected1" value="0"/>
				</span>	 --%>
			</div>
			
		</div>

		<div style="float: left; margin-left: 220px; ">
			<s:submit cssClass="btn btn-primary" value="Save" name="submit" id="submitButton"></s:submit>
		</div>
	</s:form>
</div>
