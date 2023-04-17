<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script>
	$( function () {
		/* $("input[name='submit']").click(function(){
			$(".validateRequired").prop('required',true);
		}); */
		
		$("body").on("click","#submit",function(){
	    	$(".validateRequired").prop('required',true);
	    }); 
		
	    $("#effectiveDate").datepicker({format: 'dd/mm/yyyy'});
	    
	});
       
    function checkAmt() {
	    var loanAmount = document.getElementById("loanAmount").value;
	    if(parseFloat(loanAmount) <= 0) {
		    document.getElementById("loanAmount").value = '';
		    return false;
	    }
	    return true;
    }
    
    function getLocationOrg(orgid) {
		var action='GetLocationOrg.action?strOrg='+orgid+'&fromPage=LAFORM';
		getContent('locationdivid', action);
		getDepartByOrg(orgid);
	}
    
    
    function getDepartByOrg(orgid) {
		var action='GetOrgDepartmentList.action?OID='+orgid+'&type=LAFORM';
		getContent('departdivid', action);
		getLevelByOrg(orgid);
	}
    
    
    function getLevelByOrg(orgid) {
		var action='GetLevelByOrg.action?strOrg='+orgid+'&fromPage=LAFORM';
		getContent('leveldivid', action);
		getEmployeeList();
	}
    
    
    function getEmployeeList() {
    	var orgId = $(".modal-body").find("#f_org")[0].value;
    	var wLocId = $(".modal-body").find("#f_strWLocation")[0].value;
    	var departId = $(".modal-body").find("#f_department")[0].value;
    	var levelId = $(".modal-body").find("#f_level")[0].value;
    //	getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.frmEmployeeActivity.f_level.options[document.frmEmployeeActivity.f_level.selectedIndex].value+'&f_department='+document.frmEmployeeActivity.f_department.options[document.frmEmployeeActivity.f_department.selectedIndex].value+'&f_strWLocation='+document.frmEmployeeActivity.f_strWLocation.options[document.frmEmployeeActivity.f_strWLocation.selectedIndex].value)
    	var action='GetLiveEmployeeList.action?fromPage=LAFORM&strOrg='+orgId+'&f_strWLocation='+wLocId+'&f_department='+departId+'&f_level='+levelId;
		getContent('empDiv', action);
    }
    
    
    function getLoanTypeByEmp(strEmpId) {
    	document.getElementById("strEmpIdHide").value = strEmpId;
	   	var xmlhttp = GetXmlHttpObject();
  		if (xmlhttp == null) {
  			alert("Browser does not support HTTP Request");
  			return;
  		} else {
  			var xhr = $.ajax({
  				url : 'GetLoanTypeByEmployee.action?strEmpId='+strEmpId,
  				cache : false,
  				success : function(data) {
  					//alert("data ===>>"+ data);
  					if(data.trim() != "") {
	  					var allData = data.split("::::");
	  					//console.log("allData[0]==>"+allData[0]);console.log("allData[1]==>"+allData[1]);console.log("allData[2]==>"+allData[2]);
	  					document.getElementById("leaveTypedivid").innerHTML = allData[0];
	  					document.getElementById("workFlowDiv").innerHTML = allData[1];
	  					document.getElementById("policy_id").value = allData[2].trim();
  					}
  				}
  			});
  		}
     }
	 
	$("#frmLoanApplication1").submit(function(e){
		e.preventDefault();
		var loan_limit = parseInt($("#loanAmountLimit").val().replace(/\,/g,''));
		var desired_loan = parseInt($("#loanAmount").val().replace(/\,/g,''));
		if(loan_limit < desired_loan){
			$("#loanAmtErr").html("You can not apply for more than specified amount ("+$("#loanAmountLimit").val()+")");
			$("#loanAmtErr").css("color","red");
		} else {
			var divResult = "divResult";
			var currUserType = '<%=(String)request.getAttribute("currUserType")%>';
			var strCEO = '<%=IConstants.CEO %>';
			var strHOD = '<%=IConstants.HOD %>';
			if(currUserType == strCEO || currUserType == strHOD) {
				divResult = 'subDivResult';
			}
			var form_data = $("form[name='frmLoanApplication1']").serialize();
	     	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	     	$.ajax({
	 			url : "LoanApplication.action",
	 			data: form_data, 
	 			cache : false,
	 			type : "POST",
	 			success : function(res) {
	 				$("#"+divResult).html(res);
	 			},
	 			error: function(res) {
	 				$.ajax({
	 					url: 'LoanApplicationReport.action?currUserType='+currUserType,
	 					cache: true,
	 					success: function(result){
	 						$("#"+divResult).html(result);
	 			   		}
	 				});	 				
	 			}
	 		});
		}
	});

	
	function getLoanTypeRelatedData(value,empId){
		document.getElementById("trSubmit").style.display="table-row";
		
		$.ajax({
 			url : 'ViewLoanInfo.action?loanId='+value+'&strEmpId='+empId,
 			cache : false,
 			success : function(data) {
 				//$("#myDiv").html(res);
 				if(data != "" && data.trim().length > 0){
            		var allData = data.split("::::");
                    $("#myDiv").html(allData[0]);
                    if(allData[1].trim()=='true'){
                    	document.getElementById("trSubmit").style.display="none";
                    	alert('You have already applied loan or not completed previous loan.');
                    }
            	}
 			}
 		});
	}
	
	function checkValue(value) {
		var val = document.getElementById("loanAmount").value;
		//console.log("val ==>"+val+"==>value==>"+value);
		if(parseInt(val) === 0) {
			alert("Invalid amount!");
			document.getElementById("loanAmount").value = "";
		}
	}
	
	
	function GetXmlHttpObject() {
		if (window.XMLHttpRequest) {
			return new XMLHttpRequest();
		}
		if (window.ActiveXObject) {
			return new ActiveXObject("Microsoft.XMLHTTP");
		}
		return null;
	}
    
</script>
<% 
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
    String strMessage = (String)request.getAttribute(IConstants.MESSAGE);
    if(strMessage==null) {
    	strMessage = "";
    }
    UtilityFunctions uF = new UtilityFunctions();
    boolean isCurrentLoan = uF.parseToBoolean((String)request.getAttribute("isCurrentLoan"));
    
    Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
    if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
    String policy_id = (String) request.getAttribute("policy_id");
    String currUserType = (String) request.getAttribute("currUserType");
    
    String strDomain = request.getServerName().split("\\.")[0];
    
    boolean isEmpLoanAutoApprove = uF.parseToBoolean((String)request.getAttribute("isEmpLoanAutoApprove"));
    
%>

    <s:form name="frmLoanApplication" id="frmLoanApplication" theme="simple" action="LoanApplication" method="POST">
        <%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
        <div class="filter_div" style="border: 2px solid rgb(241, 241, 241);padding: 10px;">
	            <div class="row row_without_margin">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-filter"></i>
					</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && ((strUserType.equalsIgnoreCase(IConstants.MANAGER) && currUserType != null && !currUserType.equals("MYTEAM")) || !strUserType.equalsIgnoreCase(IConstants.MANAGER))) { %>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="getLocationOrg(this.value);" 
		                	list="orgList" key=""/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Location</p>
							<div id="locationdivid">
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" 
			            		headerValue="All Locations" onchange="getEmployeeList();" list="wLocationList" key=""/>
		            		</div>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Department</p>
							<div id="departdivid">
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" 
		            			headerValue="All Departments" onchange="getEmployeeList();"/>
	            			</div>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Level</p>
							<div id="leveldivid">
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" 
		                		onchange="getEmployeeList();" list="levelList" key="" required="true"/>
	                		</div>
						</div>
						<% } %>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Employee</p>
							<div id="empDiv">
								<s:select name="strEmpId" id="strEmpId" listKey="employeeId" listValue="employeeCode" 
								headerKey="" headerValue="Select Employee" list="empList" key="" required="true" onchange="getLoanTypeByEmp(this.value);"/>
							</div>
						</div>
					</div>
				</div>
       		</div>
        <%} %>
    </s:form>

	
	<br>
	<s:form name="frmLoanApplication1" id="frmLoanApplication1" theme="simple" action="LoanApplication" method="POST" onsubmit="return checkAmt();">
	    <s:hidden name="strEmpId" id="strEmpIdHide" />
	    <s:hidden name="currUserType" id="currUserType" />
	    <input type="hidden" name="policy_id" id="policy_id" value="<%=(String)request.getAttribute("policy_id") %>"/>
	    <s:fielderror />
	    <div class="row row_without_margin">
	    	<div class="col-lg-6">
	    		<table class="table table_no_border form-table">
			        <%-- <%if(!isCurrentLoan){ %> --%>
			        <tr>
			            <td>Select Loan Type:<sup>*</sup></td>
			            <td><div id="leaveTypedivid">
				                <s:select theme="simple" name="strLoanCode" id="strLoanCode" listKey="loanId" cssClass="validateRequired"
									list="loanList" key="" listValue="loanCode" headerKey="" headerValue="Select Loan"
									onchange="getLoanTypeRelatedData(this.value,document.frmLoanApplication1.strEmpId.value);" />
							</div>
			                <!-- onchange="getContent('myDiv', 'ViewLoanInfo.action?loanId='+this.value+'&strEmpId='+document.frmLoanApplication1.strEmpId.options[document.frmLoanApplication1.strEmpId.selectedIndex].value)" -->
			            </td>
			        </tr> 
			        <tr>
			            <td>Enter Loan Amount:<sup>*</sup></td>
			            <td>
			                <s:textfield name="loanAmount" id="loanAmount" cssClass="validateRequired" onkeypress="return isNumberKey(event);" onkeyup="checkValue(this.value)"></s:textfield>
			            	<p id="loanAmtErr"></p>
			            </td>
			        </tr>
			        <tr>
			            <td>Enter Duration (in months):<sup>*</sup></td>
			            <td>
			                <s:select name="loanDuration" cssClass="validateRequired" list="durationList" listKey="numberId" listValue="numberName"></s:select>
			            </td>
			        </tr>
			        <tr>
			            <td>Effective Date:<sup>*</sup></td>
			            <td><s:textfield name="effectiveDate" id="effectiveDate" cssClass="validateRequired"/></td>
			        </tr>
			        <tr>
			            <td valign="top">Enter Reason:<sup>*</sup></td>
			            <td>
			                <s:textarea name="loanDesc" cols="40" rows="5"  cssClass="validateRequired"> </s:textarea>
			            </td>
			        </tr>
			        
			        <%if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
			            if(isEmpLoanAutoApprove){ %>
			            	<tr id="trSubmit">
					            <td>&nbsp;</td>
					            <td><input type="submit" name="submit" id="submit" value="Apply For Loan" class="btn btn-primary"/></td>
					        </tr>
			        <%    } else {
				        	if(uF.parseToBoolean(CF.getIsWorkFlow())) {
				               	if(hmMemberOption != null && !hmMemberOption.isEmpty()) {
				            		Iterator<String> it1 = hmMemberOption.keySet().iterator();
				            		while(it1.hasNext()) {
				            			String memPosition = it1.next();
				            			String optiontr = hmMemberOption.get(memPosition);					
				            			out.println(optiontr); 
				            		}
				            %>
							        <tr id="trSubmit">
							            <td>&nbsp;</td>
							            <td><input type="submit" name="submit" id="submit" value="Apply For Loan" class="btn btn-primary"/></td>
							        </tr>
							   <% } else { %>
							        <tr>
							            <td colspan="2">Your work flow is not defined. Please, speak to your hr for your work flow.</td>
							        </tr>
							   <% } %>
					        <% } else { %>
							        <tr id="trSubmit">
							            <td>&nbsp;</td>
							            <td><input type="submit" name="submit" id="submit" value="Apply For Loan" class="btn btn-primary"/></td>
							        </tr>
					        <% }
			        	}%>
			        <% } else { %>
			        		<tr><td colspan="2" id="workFlowDiv" class="normal-font"></td></tr>
			        <% } %>
			    </table>
			    <%if(isCurrentLoan) { %>
			        <td colspan="2">
			            <div class="msg_error"><span>You can not apply for new loan as you already have one loan approved and yet to be finished.</span></div>
			        </td>
			    <%}%>
	    	</div>
	    	<div class="col-lg-6">
	    		<div id="myDiv"></div>
	    	</div>
	    </div>    
</s:form>
                            