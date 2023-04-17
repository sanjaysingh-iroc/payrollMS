<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*,ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div id="divResult">
<% 
	Map<String,List<String>> hmClientVisits =  (Map<String,List<String>>)request.getAttribute("hmClientVisits");

%>


<script type="text/javascript" charset="utf-8">

$(document).ready(function(){
		
	$("body").on('click','#closeButton',function(){
    $(".modal-dialog").removeAttr('style');
    $("#modalInfo").hide();
  });
    $("body").on('click','.close',function(){
    $(".modal-dialog").removeAttr('style');
    $("#modalInfo").hide();
   });
    
    $("#strVisitDate").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#strEndDate').datepicker('setStartDate', minDate);
    });
    
    $("#strHrName").multiselect().multiselectfilter();
	$("#strClientName").multiselect().multiselectfilter();
 
});
</script>

	<div>
 	<div id="printDiv" class="leftbox reportWidth">
 	
		<s:form id="frmAddClientVisit" name ="frmAddClientVisit" action ="addNewClientVisit"  method="POST" theme="simple" cssClass="formcss" enctype="multipart/form-data" >
			<div style="float: left;" >
			<%if( request.getAttribute("visitId")!=null){ %>
				<input type="hidden" name = "visitId" value = "<%=request.getAttribute("visitId")%>"/>
			<%}%>
			<table class="table table_no_border form-table">
				
				<tr><td height="10px">&nbsp;</td></tr>
				<tr>
				<td>Select HR</td>
				<%if( request.getAttribute("sbHrNames")!=null){ %>
					<td>
						<select name="strHrName" id="strHrName"  multiple="multiple">
						<%=(String)request.getAttribute("sbHrNames") %></select>
                    </td>
				<%}else{ %>
					<td colspan="3">
						<s:select name="strHrName" id ="strHrName" listKey="employeeId"
							listValue="employeeCode" headerKey="" headerValue="Select HR"
							list="HRList" key="" cssClass="validateRequired" required="true" multiple="true">
						 	
						</s:select>
					</td>
					<%} %>
				</tr>
				<tr>
				<td>Select Client</td>
				<%if( request.getAttribute("sbClient")!=null){%>
					<td>
						<select name="strClientName" id="strClientName"  multiple="multiple">
						<%=(String)request.getAttribute("sbClient") %></select>
                    </td>
                    
				<%}else{ %>
					<td colspan="3">
						<s:select name="strClientName" id ="strClientName" listKey="clientId"
						listValue="clientName" headerKey="" headerValue="Select Client"
						list="clientlist" key="" cssClass="validateRequired" multiple="true">
						 </s:select>
					</td>
				<%} %>
				</tr>
				<tr>
					 <td class="txtlabel alignRight">Date:<sup>*</sup></td>
						  <td>
							  <s:textfield name="strVisitDate" id="strVisitDate" cssClass="validateRequired" cssStyle="width:85px;"  ></s:textfield>
								<span class="hint">Enter Visiting Date.<span class="hint-pointer">&nbsp;</span>
						</td>
						
				</tr>
				<tr>
					 <td class="txtlabel alignRight">Time:<sup>*</sup></td>
						<td>
							<input type="text" id="startTime" name="startTime" style="width:60px;" class="validateRequired startTime" value="<%=(String)request.getAttribute("startTime")%>"/>
							<span class="hint">Enter Visiting Time.<span class="hint-pointer">&nbsp;</span>
						</td>
				</tr>
				<tr>
				     	<td>Description:<sup>*</sup></td>
				         <td colspan="2"><s:textarea rows="3" name="strVisitdesc" id="strVisitdesc" cssClass="validateRequired" cssStyle="font-size: 11px; width: 78%;" ></s:textarea></td>
				</tr>
				<%if( request.getAttribute("operationName")!=null){ %>
				<tr>
				     <td></td>
				     	<td colspan="2"><s:submit name="btnSubmit" cssClass="btn btn-primary" cssStyle="margin-top:10px;" value="Update" />
				     	</td>
				 </tr>
				 <%}else{ %>
				
				<tr>
				     <td></td>
				     	<td colspan="2"><s:submit name="btnSubmit" cssClass="btn btn-primary" cssStyle="margin-top:10px;" value="Submit" />
				     	</td>
				 </tr>
				 <%} %>
				</table>
			   <script>
				  $(function () {
				      $("input[name='eventPost']").click(function(){
				      $(".validateRequired").prop('required',true);
				   });
				  var date_yest = new Date();
				   var date_tom = new Date();
				   date_yest.setHours(0,0,0);
				   date_tom.setHours(23,59,59); 
				   
				   $('.startTime').datetimepicker({ 
				      format: 'HH:mm',
				      minDate: date_yest,
				      defaultDate: date_yest
				   })
				 });
		  </script>
	</div>
		</s:form>
	</div>
</div>
</div>