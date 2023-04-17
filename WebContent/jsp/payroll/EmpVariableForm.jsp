<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>
    function prevOtherEarning(salaryHeadId, salaryHeadName) {
    	var employeeId = document.getElementById("employeeId").value;
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$(".modal-title").html('Varibales of '+salaryHeadName); 
    	$.ajax({
   			url : "PrevOtherEarning.action?strEmpId="+employeeId+"&SHID="+salaryHeadId,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
    }
    
    
    function validateField(id) {
    	var field = document.getElementById("idStrIncentiveAmount"+id);
    	if(field.value=='') {
    		alert('Please enter valid amount');
    		return false;
    	} else {
    		return true;
    	}
    }
    
    
    function submitForm(type) {
    	var org = document.getElementById("f_org").value;
    	/* var paycycle = document.getElementById("paycycle").value; */
    	var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
    	/* var f_salaryhead = document.getElementById("f_salaryhead").value; */
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var level = getSelectedValue("f_level");
    	var strGrade = getSelectedValue("f_grade");
    	var strEmployeType = getSelectedValue("f_employeType");
    	var employee = document.getElementById("employee").value;
    	
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&strPaycycleDuration='+strPaycycleDuration
    			+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&employee='+employee;
    	}
    	//alert("service ===>> " + service);
    	/* var data = $("#"+this.id).serialize();
    	var data1 = $("#frm_Otherearning").serialize();
    	alert("data ===>> " + data);
    	alert("data1 ===>> " + data1); */
    	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'EmpVariableForm.action?f_org='+org+paramValues,
    		success: function(result) {
            	$("#subDivResult").html(result);
       		}
    	});
    }


    function getSelectedValue(selectId) {
    	var choice = document.getElementById(selectId);
    	var exportchoice = "";
    	for ( var i = 0, j = 0; i < choice.options.length; i++) {
    		if (choice.options[i].selected == true) {
    			if (j == 0) {
    				exportchoice = choice.options[i].value;
    				j++;
    			} else {
    				exportchoice += "," + choice.options[i].value;
    				j++;
    			}
    		}
    	}
    	return exportchoice;
    }
    
    
	function getLevelwiseGrade() {
		var orgId = document.getElementById("f_org").value;
		var levelIds = getSelectedValue('f_level');
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetGradeList.action?fromPage=filter&orgId='+orgId+'&levelIds='+levelIds,
				cache : false,
				success : function(data) {
					document.getElementById('myGrade').innerHTML = data;
					$("#f_grade").multiselect().multiselectfilter();
					getEmployeeName();
				}
			});
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
		
		
	function getEmployeeName() {
		var org = document.getElementById("f_org").value;
		var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var level = getSelectedValue("f_level");
    	var strGrade = getSelectedValue("f_grade");
    	var strEmployeType = getSelectedValue("f_employeType");
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	       alert("Browser does not support HTTP Request");
	       return;
	    } else {
	    	var action = 'GetEmployeeList.action?fromPage=EMP_VARI_FORM&f_org='+org+'&location='+location+'&strDepart='+department
	    		+'&level='+level+'&strGrade='+strGrade+'&strEmployeementType='+strEmployeType;
	       	var xhr = $.ajax({
	           	url : action,
	           	cache : false,
	           	success : function(data) {
		           	if(data == "") {
		           	} else {
	                  	document.getElementById("myEmployee").innerHTML = data;
		           	}
	           	}
	       	});
	    }
	}
	
	
	function updateVariableForm() {
    	//alert("service ===>> " + service);
    	//$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	if(confirm("Please check previously added variables, then update it.")) {
	    	var data = $("#frmEmpVariableForm").serialize();
	    	//alert("data ===>> " + data);
	    	$.ajax({
	    		type : 'POST',
	    		data: data,
	    		url: 'EmpVariableForm.action?btnUpdate=Update',
	    		success: function(result) {
	            	$("#subDivResult").html(result);
	       		}
	    	});
    	}
    }
	
	
	function revokeVariableForm() {
    	//alert("service ===>> " + service);
    	//$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	if(confirm("Are you sure, you want to revoke all variables, which are added previously?")) {
	    	var data = $("#frmEmpVariableForm").serialize();
	    	$.ajax({
	    		type : 'POST',
	    		data: data,
	    		url: 'EmpVariableForm.action?btnRevoke=Revoke',
	    		success: function(result) {
	            	$("#subDivResult").html(result);
	       		}
	    	});
    	}
    }
	
</script>


<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Variable Form" name="title"/>
    </jsp:include>  --%>  

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<s:form name="frm_Otherearning" id="frm_Otherearning" action="EmpVariableForm" theme="simple" method="post">
			<div class="box box-default collapsed-box">
				<div class="box-header with-border">
				    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
					<div class="box-tools pull-right">
					    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
                <!-- /.box-header -->
                
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<s:hidden name="salaryheadname" id="salaryheadname"></s:hidden>
								<p style="padding-left: 5px;">Duration</p>
								<s:select theme="simple" name="strPaycycleDuration" id="strPaycycleDuration" listKey="paycycleDurationId" listValue="paycycleDurationName" headerKey="" headerValue="Select Duration" onchange="submitForm('2');" list="paycycleDurationList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organisation</p>
								<s:select list="orgList" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" />
							</div>
							<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select id="paycycle" name="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');" />
							</div> --%>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" onchange="getEmployeeName();" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" onchange="getEmployeeName();" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" onchange="getLevelwiseGrade();" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                                <p style="padding-left: 5px;">Grade</p>
                                <div id="myGrade">
                                	<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" onchange="getEmployeeName();" multiple="true"/>
                                </div>
                        		</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key="" onchange="getEmployeeName();" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Select Employee</p>
								<div id="myEmployee">
									<s:select theme="simple" name="employee" list="empList" id="employee" headerKey="" headerValue="Select Employee" listKey="employeeId" listValue="employeeName" required="true" />
								</div>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Search" value="Search" class="btn btn-primary" onclick="submitForm('2');"/> 
							</div>
						</div>
					</div>
				</div>
                <!-- /.box-body -->
			</div>
		</s:form>
		
				<%UtilityFunctions uF = new UtilityFunctions();
					Map<String, List<String>> hmSalaryHeadData = (Map<String, List<String>>) request.getAttribute("hmSalaryHeadData");
					if(hmSalaryHeadData == null) hmSalaryHeadData = new HashMap<String, List<String>>();
					/* Map hmSalaryList = (Map) request.getAttribute("hmSalaryList");
					Map hmSalaryHeadsMap = (Map) request.getAttribute("hmSalaryHeadsMap");
					Map hmOtherearning = (Map) request.getAttribute("hmOtherearning");
					Map hmOtherearningId = (Map) request.getAttribute("hmOtherearningId");
					Map hmOtherearningValue = (Map) request.getAttribute("hmOtherearningValue");
					UtilityFunctions uF = new UtilityFunctions();
					
					String sHeadType = (String) request.getAttribute("sHeadType");
					List<String> ckEmpPayList = (List<String>) request.getAttribute("ckEmpPayList");
					if(ckEmpPayList == null) ckEmpPayList = new ArrayList<String>(); */
					
					String roundOffCondition = (String)request.getAttribute("roundOffCondition");
					String sbPaycycle = (String)request.getAttribute("sbPaycycle");
					
					System.out.println("hmSalaryHeadData --->> " + hmSalaryHeadData);
				%>  
			<div style="clear: both;"></div>
				<!-- <div style="text-align: right;"> 
					<a href="javascript:void(0)" onclick="importVariable();">Import Variable</a>
				</div> -->
				<%=uF.showData((String)request.getAttribute(IConstants.MESSAGE), "") %>
			<div class="row row_without_margin">
				<div class="col-lg-8">
					<s:form name="frmEmpVariableForm" id="frmEmpVariableForm" action="EmpVariableForm" theme="simple" method="post">
						<table class="table table-bordered">
							<tr>
								<th style="text-align: center;">Salary Head
									<s:hidden id="employeeId" name="employee"/>
								</th>
								<th style="text-align: center;">Variable Amount</th>
								<th style="text-align: center;">Paycycles</th>
								<th style="text-align: center;">Action</th>
							</tr>
							<%
							int i = 0;
							Iterator<String> it = hmSalaryHeadData.keySet().iterator();
							while(it.hasNext()) {
								String salaryHeadId = it.next();
								//System.out.println("salaryHeadId =======>> " + salaryHeadId);
								
								List<String> innerList = hmSalaryHeadData.get(salaryHeadId);
								i++;
							%>
							<tr>
								<td><%=innerList.get(1) %>
						    		<input type="hidden" id="salaryHeadId" name="salaryHeadId" value="<%=salaryHeadId %>">
						    		<input type="hidden" id="<%=salaryHeadId %>_earn_deduct" name="<%=salaryHeadId %>_earn_deduct" value="<%=innerList.get(2) %>">
						    	</td>
						    	
						    	<td align="center"> <!-- style="background-color: #eee;" -->
						    		<input style="width:75px !important; text-align:right" type="text" id="<%=salaryHeadId %>_amount" name="<%=salaryHeadId %>_amount"/>
						    	</td>
						    	
						    	<td align="center">
						    		<select id="<%=salaryHeadId %>_paycycle" name="<%=salaryHeadId %>_paycycle" multiple="multiple">
						    			<%=sbPaycycle %>
						    		</select>
						    	</td>
						    	<script type="text/javascript">
						    		$("#<%=salaryHeadId %>_paycycle").multiselect().multiselectfilter();
						    	</script>
						    	<td>
						    		<a href="javascript:void(0)" onclick="prevOtherEarning(<%=salaryHeadId%>,'<%=innerList.get(1)%>')">Previous Variable</a>
						    	</td>
							</tr>
							<% }
								if(i == 0) {
							%>
							<tr>
	    						<td colspan="3">
	        						<div style="width: 96%;" class="msg nodata"><span>No Salary Head found for the current selection</span></div>
	    						</td>
							</tr>
							<% } else { %>
							<tr>
	    						<td align="center" colspan="3">
	        						<input type="button" name="btnUpdate" value="Update" class="btn btn-primary" onclick="updateVariableForm();"/>
	        						<input type="button" name="btnRevoke" value="Revoke" class="btn btn-primary" onclick="revokeVariableForm();"/>
	    						</td>
							</tr>
							<% } %>
	 					</table>
	 				</s:form>
				</div>
			</div>
		<%-- </s:form> --%>
	</div>
	<!-- /.box-body -->
        
        <div class="modal" id="modalInfo" role="dialog">
		    <div class="modal-dialog">
		        <!-- Modal content-->
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal">&times;</button>
		                <h4 class="modal-title">-</h4>
		            </div>
		            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
		            </div>
		            <div class="modal-footer">
		                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
		            </div>
		        </div>
		    </div>
		</div>
	
<script type="text/javascript">
    $(function() {
    	$("#f_strWLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_level").multiselect().multiselectfilter();
    	$("#f_grade").multiselect().multiselectfilter();
    	$("#f_employeType").multiselect().multiselectfilter();
    	
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    	});
    	
    });    
</script>