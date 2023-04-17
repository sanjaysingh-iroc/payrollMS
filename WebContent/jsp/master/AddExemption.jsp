<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUnderSection"%>
<%@page import="com.konnect.jpms.select.FillSalaryHeads"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<% UtilityFunctions uF = new UtilityFunctions(); 
   String fromPage = (String) request.getAttribute("fromPage");
  
%>
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%} %>
<script>
$(function() {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
   
});


function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
      return false;

   return true;
}

</script>



	<s:form theme="simple" id="formAddNewRow" action="AddExemption" method="POST" name="formAddNewRow">

	<input type="hidden" name="strFinancialYearStart" value="<%=request.getAttribute("strFinancialYearStart")%>">
	<input type="hidden" name="strFinancialYearEnd" value="<%=request.getAttribute("strFinancialYearEnd")%>">
	<s:hidden name="exemptionId" />
	<s:hidden name="operation" />
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	<s:hidden name="financialYear" id="financialYear"/>
	<input type="hidden" name="fromPage"id="fromPage"  value="<%=request.getAttribute("fromPage")%>"/>
	
	<table class="table table_no_border">
		<tr>
			<td class="txtlabel alignRight">Exemption Name:<sup>*</sup></td>
			<td><s:textfield name="exemptionCode" id="exemptionCode" cssClass="validateRequired"/>
				<!-- <input type="text" name="exemptionCode" id="exemptionCode" rel="0" class="validate[required]" /> --> 
				<span class="hint">Exemption Name<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight">Exemption Salary Head:</td>
			<td>
				<select name="exemptionName" id="exemptionName">
					<%-- <% java.util.List  alSalaryHeads = (java.util.List) request.getAttribute("alSalaryHeads"); %>
					<% for (int i=0; i<alSalaryHeads.size(); i++) { %>
					<option value="<%=(String)alSalaryHeads.get(i)%>"> <%=(String)alSalaryHeads.get(i) %></option>
					<% } %> --%>
					<% List<FillSalaryHeads> salaryHeadList =(List<FillSalaryHeads>) request.getAttribute("salaryHeadList"); %>
					<% for (int i=0; i<salaryHeadList.size(); i++) {
							//System.out.println("head id==>"+uF.parseToInt((String)salaryHeadList.get(i).getSalaryHeadId())+"check==>"+(uF.parseToInt((String)salaryHeadList.get(i).getSalaryHeadId()) == uF.parseToInt((String)request.getAttribute("exemptionName"))));
					%>
					<option value="<%=(String)salaryHeadList.get(i).getSalaryHeadId()%>"
					<% if(uF.parseToInt((String)salaryHeadList.get(i).getSalaryHeadId()) == uF.parseToInt((String)request.getAttribute("SALARY_HEAD_ID"))) { %>
						selected
					<% } %>> <%=(String)salaryHeadList.get(i).getSalaryHeadName() %></option>
					<% } %>
				</select>
				<span class="hint">Exemption Salary Head<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Exemption Description:</td>
			<td><s:textarea name="exemptionDesc" id="exemptionDesc" />
				<!-- <input type="text" name="exemptionDesc" id="exemptionDesc" rel="2" /> --> 
				<span class="hint">Exemption Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<%-- <tr>
			<td class="txtlabel alignRight">Exemption From:</td>
			<td>
				<input type="text" name="exemptionFromDate" id="idExemptionFromDate" rel="3"/> 
				<span class="hint">Exemption From<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Exemption To:</td>
			<td>
				<input type="text" name="exemptionToDate" id="idExemptionToDate" rel="4"/> 
				<span class="hint">Exemption To<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr> --%>

		<tr>
			<td class="txtlabel alignRight">Exemption Limit:</td>
			<td><s:textfield name="exemptionLimit" id="exemptionLimit" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="exemptionLimit" id="exemptionLimit" rel="3" onkeypress="return isNumberKey(event)"/> --> 
				<span class="hint">Exemption Limit<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Under Section:</td>
			<td>
				<select name="strUnderSection" id="strUnderSection" >
					<% java.util.List  underSection10and16List = (java.util.List) request.getAttribute("underSection10and16List"); %>
					<% for (int i=0; i<underSection10and16List.size(); i++) { %>
					<option value=<%= ((FillUnderSection)underSection10and16List.get(i)).getUnderSectionId() %>
					<% if(uF.parseToInt(((FillUnderSection)underSection10and16List.get(i)).getUnderSectionId()) == uF.parseToInt((String)request.getAttribute("strUnderSection"))) { %>
						selected
					<% } %>><%= ((FillUnderSection)underSection10and16List.get(i)).getUnderSectionName() %></option>
					<% } %>
				</select>
				
				<span class="hint">Under Section<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Is In Investment Form:</td>
			<td>
				<select name="strIsInvestmentForm" id="strIsInvestmentForm">
					<option value="False" <%if(!uF.parseToBoolean((String)request.getAttribute("strIsInvestmentForm"))) { %> selected<% } %>>False</option>
					<option value="True" <%if(uF.parseToBoolean((String)request.getAttribute("strIsInvestmentForm"))) { %> selected<% } %>>True</option>
				</select>
				
				<span class="hint">Is In Investment Form<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td class="alignRight">Select Slab Type:<sup>*</sup></td>
			<td>
				<select name="slabType" id="slabType" class="validateRequired">
					<option value="0" <%=(request.getAttribute("slabType") != null && ((String)request.getAttribute("slabType")).equals("0")) ? "selected" : "" %>>Standard</option>
					<option value="1" <%=(request.getAttribute("slabType") != null && ((String)request.getAttribute("slabType")).equals("1")) ? "selected" : "" %>>New</option>
				</select>
				<span class="hint">Select the slab type.<br>Standard Slab<br>New Slab<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td></td>
			<td>
				<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk" name="btnAddNewRowOk"/> 
			</td>
		</tr>

	</table>
	
</s:form>
<script>
<%if(fromPage != null && fromPage.equals("ER")) {%>
	 $("#formAddNewRow").submit(function(event){
		event.preventDefault();
		var financialYear = document.getElementById("financialYear").value;
		var form_data = $("#formAddNewRow").serialize();
		//$("#actionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url  : 'AddExemption.action',
			data : form_data,
			success:function(result){
				$("#actionResult").html(result);
			},
			error: function(result){
				$.ajax({
					url: 'ExemptionReport.action?financialYear='+financialYear,
					cache: true,
					success: function(result){
						$("#actionResult").html(result);
			   		}
				});
			}
		});
		
	});
 <%}%>   
 
 
</script>
