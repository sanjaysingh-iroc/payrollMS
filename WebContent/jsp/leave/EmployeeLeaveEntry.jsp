<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%
    String strTitle = ((request.getParameter("E")!=null)?"Edit ":"Apply ")+"Leave";
    UtilityFunctions uF = new UtilityFunctions();
  //===start parvez date: 18-03-2023===	
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
//===end parvez date: 18-03-2023===
    %>
<script>
		function toggleSession(){
			if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked) {
				document.getElementById("idSession").style.display="block";
				document.getElementById("idLeaveTo").style.display="none";
			} else {
				if(document.getElementById("idSession")){
					document.getElementById("idSession").style.display="none";
				}
				
				document.getElementById("idLeaveTo").style.display="table-row";
			
			}
		}
    
	    function getLeaveTypedetails(val,type) {
	    	fadeForm('formID');
	    	/* var strD1=$("#leaveFromTo").val();
	    	var strD2=$("#leaveToDate").val(); */
	    	var strD1=document.frmLeave.leaveFromTo.value;
	    	var strD2=document.frmLeave.leaveToDate.value;
	    	var strSession = "";
	    	
	    	if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked==true){
	    		strD2=document.frmLeave.leaveFromTo.value;
	    /* ===start parvez date: 14-03-2023=== Note added: strSession  */		
	    		var ele = document.getElementsByName("strSession");
				for(var i=0; i < ele.length; i++) {
					if(ele[i].checked) {
						strSession = ele[i].value;
					}
				}
		/* end parvez date: 14-03-2023=== */		
	    	}
	    	
	    	var empid="";
	    	if(type=="1"){
	    		empid=document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value;
	    		var action='GetLeaveStatus.action?EMPID='+document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value+'&LTID='+val+'&D1='+strD1+'&D2='+strD2;
	    	}else if(type=="2"){
	    		var action='GetLeaveStatus.action?LTID='+val+'&D1='+strD1+'&D2='+strD2;
	    	} 
	    	$.ajax({ 
	    		type : 'GET',
	    		url: action,
	    		success: function(result){
	    			$("#myDiv").html(result);
	    			var action1='GetEmployeePolicyDetails.action?leavetype='+ val+'&empid='+empid+'&strD1='+strD1+'&strD2='+strD2+'&strSession='+strSession;
	    			$.ajax({ 
	            		type : 'GET',
	            		url: action1,
	            		success: function(result){
	            			result = $.parseHTML(result.trim());
	            			$( "#policyid" ).nextAll().remove();
	            			$("#policyid").after(result);
	               		}
	            	}).done(function() {
			 			unfadeForm('formID');
			 		});
	       		}
	    	}); 
	    }
    
    
	    function getLeaveDateStatus(type){
	    	fadeForm('formID');
	    	var empid="";
	    	if(type=="1"){
	    		empid=document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value;		
	    	}
	    	var typeOfLeave=$("#typeOfLeave").val();
	    	
	    	var strD1=$("#leaveFromTo").val();
	    	var strD2=$("#leaveToDate").val();
	    	
	    	/* if(strD1 > strD2){
	    		strD2=strD1;
	    	} */
	    	
	    	if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked==true){
	    		strD2=$("#leaveFromTo").val();
	    	}
	    	
	    	var action1='GetEmployeePolicyDetails.action?leavetype='+ typeOfLeave+'&empid='+empid+'&strD1='+strD1+'&strD2='+strD2;
	    	//console.log("action==>"+action1);
	    	$.ajax({ 
	    		type : 'GET',
	    		url: action1,
	    		success: function(result){
	    			result = $.parseHTML(result.trim());
	    			$( "#policyid" ).nextAll().remove();
	    			$("#policyid").after(result);
	       		}
	    	}).done(function() {
	 			unfadeForm('formID');
	 		});		
	    }
    
    
	    function getTypeOFLeave(){
	    	fadeForm('formID');
	    	var strEmpId=document.getElementById("strEmpId").value;
	    	var action="GetTypeOfLeave.action?strEmpID="+strEmpId;
	    	$.ajax({ 
	    		type : 'GET',
	    		url: action,
	    		success: function(result){
	    			$("#tdtypeofleave").html(result);
	    			getLeaveDateStatus("1");
	       		}
	    	}).done(function() {
	 			unfadeForm('formID');
	 		});
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
    
    	 
    	$(function(){
    	    $("body").on("click","#submitButton",function(){
    	    	$(".validateRequired").prop('required',true);
    	    });
    	});
    	
    	toggleSession();

    	
    	function getLeaveValidation(){
    		document.getElementById("submitButton").style.display = 'none';
    		var d1=document.getElementById("leaveFromTo").value;
    		var d2=document.getElementById("leaveToDate").value;
    		if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked==true){
    			d2=document.getElementById("leaveFromTo").value;
        	}
    		var val=document.getElementById("typeOfLeave").options[document.getElementById("typeOfLeave").selectedIndex].value;
    		var emp_id="";
	    	if(document.getElementById("strEmpId")){
	    		var e = document.getElementById("strEmpId");
	    		emp_id = e.options[e.selectedIndex].value;
	    	}
    		
    		var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				url : "LeaveValidation.action?emp_id=" + emp_id+"&LTID="+val+"&D1="+d1+"&D2="+d2,
    				cache : false,
    				success : function(data) {
    					if(parseInt(data.trim().length)>1) {
    						$(".message").html("<div class='msg errorMessage'><span>"+data+"</span></div>");
    						document.getElementById("submitButton").style.display = 'block';
    						unfadeForm('formID');
    					} else {
    						/* var form_data = $("form[name='frmLeave']").serialize(); */
    						var form_data = new FormData($("form[name='frmLeave']")[0]);
    						$.ajax({
    			    			url : 'EmployeeLeaveEntry.action',
    			    			type: 'POST',
    			    			data: form_data,
    			    			contentType: false,
    				 			cache : false,
    				 			processData: false,
    			    			success : function(res) {
    			    				$("#divResult").html(res);
    			    			}, 
    				 			error : function(err) {
    				 				$.ajax({ 
    									url: 'ManagerLeaveApprovalReport.action',
    									cache: true,
    									success: function(result){
    										$("#divResult").html(result);
    							   		}
    								});
    				 			}
    			    		});
    					}
    				}
    			});
    		}
    	}
    	
   
   /* ===start parvez date: 27-09-2022=== */ 	
		$("#formID").submit(function(e){
			fadeForm('formID');
			e.preventDefault();
			
			/* getLeaveValidation(); */
			document.getElementById("submitButton").style.display = 'none';
			var d1=document.getElementById("leaveFromTo").value;
	    	var d2=document.getElementById("leaveToDate").value;
	    	var val=document.getElementById("typeOfLeave").options[document.getElementById("typeOfLeave").selectedIndex].value;
	    	var emp_id="";
	    	if(document.getElementById("strEmpId")) {
	    		var e = document.getElementById("strEmpId");
	    		emp_id = e.options[e.selectedIndex].value;
	    	}
	    	var strUrl = '<%=request.getContextPath() %>'; 
	    	var action = strUrl+'/LeaveValidation.action?EMPID='+ emp_id+'&LTID='+val+'&D1='+d1+'&D2='+d2;
	    	$.ajax({
				url : action,
				cache : false,
				success : function(data) {
					//alert("data ===>> "+data);
					if(parseInt(data.trim().length)>1) {
						if(confirm(data)) {
							/* var form_data = $("form[name='frmLeave']").serialize(); */
							var form_data = new FormData($("form[name='frmLeave']")[0]);
							
				        	$.ajax({
				    			/* url : strUrl+'/EmployeeLeaveEntry.action',
				    			data: form_data,
				    			success : function(res) {
				    				$("#divResult").html(res);
				    			}, */
				    			url : strUrl+'/EmployeeLeaveEntry.action',
				    			type: 'POST',
				    			data: form_data,
				    			contentType: false,
					 			cache : false,
					 			processData: false,
				    			success : function(res) {
				    				$("#divResult").html(res);
				    			},
					 			error : function(err) {
					 				$.ajax({ 
										url: 'ManagerLeaveApprovalReport.action',
										cache: true,
										success: function(result){
											$("#divResult").html(result);
								   		}
									});
					 			}
				    		});
						} else {
							document.getElementById("submitButton").style.display = 'block';
							unfadeForm('formID');
						}
						/* $(".message").html("<div class='msg errorMessage'><span>"+data+"</span></div>");
						document.getElementById("submitButton").style.display = 'block';
						unfadeForm('formID'); */
					} else {
						/* var form_data = $("form[name='frmLeave']").serialize(); */
						var form_data = new FormData($("form[name='frmLeave']")[0]);
			        	$.ajax({
			    			url : strUrl+'/EmployeeLeaveEntry.action',
			    			type: 'POST',
			    			data: form_data,
			    			contentType: false,
				 			cache : false,
				 			processData: false,
			    			success : function(res) {
			    				$("#divResult").html(res);
			    			}, 
				 			error : function(err) {
				 				$.ajax({ 
									url: 'ManagerLeaveApprovalReport.action',
									cache: true,
									success: function(result){
										$("#divResult").html(result);
							   		}
								});
				 			}
			    		});
					}
				}
			});
	    	
		});
   /* ===end parvez date: 27-09-2022=== */
</script>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=strTitle%>" name="title"/>
    </jsp:include> --%>
<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 400px;">
    <%
        String strEmpType = (String) session.getAttribute("USERTYPE");
        String strEmpID = (String) session.getAttribute(IConstants.EMPID);
        String strMessage = (String) session.getAttribute("MESSAGE");
        if (strMessage == null) {
        	strMessage = "";
        }
        String strIsHalfDayLeave = (String)request.getAttribute("strIsHalfDayLeave");
        %>
    <p class="message"><%=strMessage%></p>
    <%session.setAttribute("MESSAGE", ""); %>	
    <s:form id="formID" name="frmLeave" theme="simple" action="EmployeeLeaveEntry" method="POST" enctype="multipart/form-data">
    	<div class="row row_without_margin">
    		<div class="col-lg-8 col-md-8 col-sm-12">
    			<table border="0" class="table table_no_border form-table" id="applyLeaveID">
	                <s:hidden name="leaveId" />
	                <s:hidden name="entrydate" />
	                <s:hidden name="empId"  required="true" />
	                <s:hidden name="isCompensate"></s:hidden>
	                <s:hidden name="strCurrDate" id="strCurrDate"/>
	                <% if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
	                    || strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
	                    %>
	                <tr>
	                    <td class="txtlabel alignRight">Select Emp Name:<sup>*</sup></td>
	                    <td>
	                        <s:select theme="simple" name="strEmpId" id="strEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="Select Employee" list="empList" onchange="getTypeOFLeave();" cssClass="validateRequired"/>
	                    </td>
	                </tr>
	                <% } else { %>
	                <tr>
	                    <td>
	                        <s:hidden name="empId" required="true" />
	                    </td>
	                </tr>
	                <tr style="display:none;">
	                    <td class="txtlabel alignRight">Emp Name:<sup>*</sup></td>
	                    <td>
	                        <s:label name="empName" label="Emp Name"/>
	                    </td>
	                </tr>
	                <% } %>
	                <%if(uF.parseToBoolean(strIsHalfDayLeave)) { %>
	                <tr>
	                    <td class="txtlabel alignRight" valign="top">Half day:</td>
	                    <td height=50 valign="top">
	                        <s:checkbox name="isHalfDay" id="isHalfDay" onclick="toggleSession()" cssStyle="float:left"/>
	                        <div id="idSession">
	                            <s:radio name="strSession" list="strWorkingSession" listKey="strHaldDayId" listValue="strHaldDayName"/>
	                        </div>
	                    </td>
	                </tr>
	                <% } %>
	                <%-- <tr><td class="txtlabel alignRight">Leave From Date:<sup>*</sup></td><td><s:textfield cssClass="validateRequired" id="leaveFromTo" name="leaveFromTo" required="true" onblur="getLeaveDateStatus();"></s:textfield><span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span></td></tr>
	                    <tr id="idLeaveTo"><td class="txtlabel alignRight">Leave To Date:<sup>*</sup></td><td><s:textfield cssClass="validateRequired" id="leaveToDate" name="leaveToDate"  required="true" onblur="getLeaveDateStatus();"></s:textfield><span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
	                <% if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
	                    || strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
	                    %> 
	                <tr>
	                    <td class="txtlabel alignRight">Leave From Date:<sup>*</sup></td>
	                    <td>
	                        <s:textfield cssClass="validateRequired" id="leaveFromTo" name="leaveFromTo" required="true"></s:textfield>
	                        <span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span>
	                    </td>
	                </tr>
	                <tr id="idLeaveTo">
	                    <td class="txtlabel alignRight">Leave To Date:<sup>*</sup></td>
	                    <td>
	                        <s:textfield cssClass="validateRequired  " id="leaveToDate" name="leaveToDate"  required="true"></s:textfield>
	                        <span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span>
	                    </td>
	                </tr>
	                <%-- <tr id = "idLeaveFromtime">
						<td class="txtlabel alignRight">From Time:<sup>*</sup></td>
						<td>
							 <input type="time" id="leaveFromTime" name="leaveFromTime" required="true" >
							<p class="hint">On Leave From Time.<span class="hint-pointer">&nbsp;</span></p>
						</td>
					</tr>
					<tr id="idLeaveTotime">
						<td class="txtlabel alignRight">To Time:<sup>*</sup></td>
						<td>
							  <input type="time" id="leaveToTime" name="leaveToTime" required="true" >
							<p class="hint">Leave End Time.<span class="hint-pointer">&nbsp;</span></p>
						</td>
					</tr> --%>
	                <%-- <tr><td class="txtlabel alignRight">Leave type:<sup>*</sup></td><td><s:select cssClass="validateRequired" name="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getContent('myDiv', 'GetLeaveStatus.action?EMPID='+document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value+'&LTID='+this.options[this.selectedIndex].value+'&D1='+document.frmLeave.leaveFromTo.value+'&D2='+document.frmLeave.leaveToDate.value);"/><span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
	                <tr>
	                    <td class="txtlabel alignRight">Leave type:<sup>*</sup></td>
	                    <td id="tdtypeofleave">
	                        <s:select theme="simple" cssClass="validateRequired " name="typeOfLeave" id="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key="" required="true"  onchange="getLeaveTypedetails(this.value,'1');"/> <!-- headerKey="" headerValue="Select Leave Type"  -->
	                        <span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span>
	                    </td>
	                    <td id="tdpolicyid"></td>
	                </tr>
	                <% } else { %>
	                <tr>
	                    <td class="txtlabel alignRight">Leave From Date:<sup>*</sup></td>
	                    <td>
	                        <s:textfield cssClass="validateRequired" id="leaveFromTo" name="leaveFromTo" required="true"></s:textfield>
	                        <span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span>
	                    </td>
	                </tr>
	                <tr id="idLeaveTo">
	                    <td class="txtlabel alignRight">Leave To Date:<sup>*</sup></td>
	                    <td>
	                        <s:textfield cssClass="validateRequired" id="leaveToDate" name="leaveToDate" required="true"></s:textfield>
	                        <span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span>
	                    </td>
	                </tr>
	                <%-- <tr><td class="txtlabel alignRight">Leave type:<sup>*</sup></td><td><s:select cssClass="validateRequired" name="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getContent('myDiv', 'GetLeaveStatus.action?LTID='+this.options[this.selectedIndex].value+'&D1='+document.frmLeave.leaveFromTo.value+'&D2='+document.frmLeave.leaveToDate.value);"/><span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
	                <tr>
	                    <td class="txtlabel alignRight">Leave type:<sup>*</sup></td>
	                    <td>
	                        <s:select theme="simple" cssClass="validateRequired" name="typeOfLeave" id="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key="" required="true"  onchange="getLeaveTypedetails(this.value,'2');"/>
	                        <span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span>
	                    </td>
	                    <td id="tdpolicyid"></td>
	                </tr>
	                <% } %> 
	                <tr>
	                    <td id="idDocumentRequired"></td>
	                </tr>
	                
	           <!-- start parvez date: 18-03-2023=== -->	
					<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BACKUP_EMPLOYEE_FOR_LEAVE))){ %>
						<tr>
							<td class="txtlabel alignRight">Back-up:<sup>*</sup></td>
							<td>
								<s:textfield cssClass="validateRequired" id="backupEmp" name="backupEmp" required="true"></s:textfield>
							 	<p class="hint"> Back-up<span class="hint-pointer">&nbsp;</span></p>
						 	</td>
						</tr>
					<%} %>
				<!-- end parvez date: 18-03-2023=== -->
	                
	                <tr id="policyid">
	                    <td class="txtlabel alignRight" valign="top">Leave Reason:<sup>*</sup></td>
	                    <td>
	                        <s:textarea cssClass="validateRequired " cols="50" rows="05" name="reason" label="Leave Reason" required="true" />
	                    </td>
	                </tr>
	                <tr>
	                	<td></td>
	                	<td>
				            <input class="btn btn-primary" id="submitButton" type="button" value="Apply Leave"/>
	                	</td>
	                </tr> 
	            </table>
    		</div>
    		<div class="col-lg-4 col-md-4 col-sm-12">
    			<div style="text-align: center;">
		            <p style="display: inline;padding: 0px 10px;"><strong>Leave Balance</strong></p>
		            <br/>
		            <p style="display: inline;padding: 0px 10px;">On Selection of Leave Type get more details</p>
		            <div id="myDiv"></div>
		        </div>
    		</div>
    	</div>
    </s:form>
</div>
<script>
$(function(){
	var strMinDate = document.getElementById("strCurrDate").value;
	$("#leaveFromTo").datepicker({
	    format: 'dd/mm/yyyy',
	    autoclose: true
	}).on('changeDate', function (selected) {
	    var minDate = new Date(selected.date.valueOf());
	    $('#leaveToDate').datepicker('setStartDate', minDate);
	    $('#leaveToDate').datepicker('setDate', minDate);
	    <%
		if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
			|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
		%> 
			getLeaveDateStatus('1');
		<% }else{%>
		getLeaveDateStatus('2');
		<%}%>
	});
	
	$("#leaveToDate").datepicker({
		format: 'dd/mm/yyyy',
		startDate : new Date(strMinDate),
		autoclose: true
	}).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#leaveFromTo').datepicker('setEndDate', minDate);
        <%
		if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
			|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
		%> 
			getLeaveDateStatus('1');
		<% }else{%>
		getLeaveDateStatus('2');
		<%}%>
	});
});

/* startDate : new Date('08/01/2018'),
endDate : new Date('28/01/2018'), */

</script>