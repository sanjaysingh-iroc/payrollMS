<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>
    function prevOtherEarning(emp_id,empname,edtype) {
    	
    	var f_salaryhead=document.getElementById("f_salaryhead").value;
    	
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$(".modal-title").html('Varibales of '+empname); 
    	$.ajax({
   			url : "PrevOtherEarning.action?strEmpId="+emp_id+"&SHID="+f_salaryhead,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
    }
    
    
    function validateField(id){
    	var field = document.getElementById("idStrIncentiveAmount"+id);
    	if(field.value==''){
    		alert('Please enter valid amount');
    		return false;
    	}else{
    		return true;
    	}
    }
    
    function updateEarning(id){
    	var field = document.getElementById("idStrIncentiveAmount"+id);
    	if(field.value==''){
    		alert('Please enter valid amount');
    	}else{
    		<%-- UpdateOtherEarning.action?emp_id='+document.getElementById('idStrEmpId<%=i%>').value+'&salary_id='+document.getElementById('f_salaryhead').value+'&paycycle='+document.getElementById('paycycle').value+'&amt='+document.getElementById('idStrIncentiveAmount<%=i%>').value+'&percent='+document.getElementById('idStrIncentivePercent<%=i%>').value+'&count=<%=i%>' --%>
    		var emp_id=document.getElementById('idStrEmpId'+id).value;
    		var salary_id=document.getElementById('f_salaryhead').value;
    		var paycycle=document.getElementById('paycycle').value;
    		var amt=document.getElementById('idStrIncentiveAmount'+id).value;
    		/* var percent=document.getElementById('idStrIncentivePercent'+id).value;
    		alert("percent "+percent); */
    		var action ='UpdateOtherEarning.action?emp_id='+emp_id+'&salary_id='+salary_id+'&paycycle='+paycycle+'&amt='+amt+'&percent=0&count='+id;
    		getContent('myDiv_'+id, action);		
    				
    	}
    }
    
    function importEmployees(){
    	//e.preventDefault();
    	var data = $("#VariableForm").serialize();
    	$.ajax({
    		type : 'POST',
    		url: 'VariableForm.action',
    		data: data,
    		success: function(result){
            	$("#subDivResult").html(result);
       		}
    	});
    }
    
    /* function getData(type){
    	
    	var org='';
    	var location='';
    	if(type=='2'){
    		org=document.getElementById("f_org").value;
    		location=document.getElementById("f_strWLocation").value;
    	}else{
    		org=document.getElementById("f_org").value;
    		
    	}
    	window.location='VariableForm.action?f_org='+org+"&f_strWLocation="+location;
    } */
    
    function submitForm(type){
    	var org = document.getElementById("f_org").value;
    	var paycycle = document.getElementById("paycycle").value;
    	var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
    	var f_salaryhead = document.getElementById("f_salaryhead").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var level = getSelectedValue("f_level");
    	var strGrade = getSelectedValue("f_grade");
    	var strEmployeType = getSelectedValue("f_employeType");
    	
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&paycycle='+paycycle
    			+'&strPaycycleDuration='+strPaycycleDuration+'&f_salaryhead='+f_salaryhead+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
    	}
    	//alert("service ===>> " + service);
    	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'VariableForm.action?f_org='+org+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
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
    
    function downloadForm() {
    	var org = document.getElementById("f_org").value;
    	var paycycle = document.getElementById("paycycle").value;
    	var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
    	var f_salaryhead = document.getElementById("f_salaryhead").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var level = getSelectedValue("f_level");
    	var strGrade = getSelectedValue("f_grade");
    	var strEmployeType = getSelectedValue("f_employeType");
    	
    	paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&paycycle='+paycycle
		+'&strPaycycleDuration='+strPaycycleDuration+'&f_salaryhead='+f_salaryhead+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&exceldownload=true';
    	
    	/* window.location='VariableForm.action?f_org='+org+paramValues; */
    	window.location='VariableForm.action?f_org='+org+'&exceldownload=true';
    	//alert("service ===>> " + service);
    }
    
    
    function importVariable() { 
    	var org = document.getElementById("f_org").value;
    	var paycycle = document.getElementById("paycycle").value;
    	var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
    	var f_salaryhead = document.getElementById("f_salaryhead").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var level = getSelectedValue("f_level");
    	var strGrade = getSelectedValue("f_grade");
    	var strEmployeType = getSelectedValue("f_employeType");
		var salaryheadname = document.getElementById("salaryheadname").value;
		var paramValues = "";
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&paycycle='+paycycle
		+'&strPaycycleDuration='+strPaycycleDuration+'&f_salaryhead='+f_salaryhead+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&salaryheadname='+salaryheadname;
     	
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Import Variable');
		 $.ajax({
			url : "ImportVariable.action?f_org="+org+paramValues,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
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
		
    /* function submitForm(type){
    	if(type == '1'){
    		document.getElementById("paycycle").selectedIndex = "0";
    		document.getElementById("f_salaryhead").selectedIndex = "0";
    		document.getElementById("strPaycycleDuration").selectedIndex = "0";
    	} else if(type == '3'){
    		document.getElementById("paycycle").selectedIndex = "0";
    		document.getElementById("f_salaryhead").selectedIndex = "0";
    	}
    	
    	document.frm_Otherearning.submit();
    } */
    
</script>


<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Variable Form" name="title"/>
    </jsp:include>  --%>  

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<s:form name="frm_Otherearning" action="VariableForm" theme="simple" method="post">
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
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select id="paycycle" name="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""  onchange="getLevelwiseGrade();" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                                <p style="padding-left: 5px;">Grade</p>
                                <div id="myGrade">
                                	<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"  />
                                </div>
                        		</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Salary Head</p>
								<s:select theme="simple" name="f_salaryhead" id="f_salaryhead" listKey="salaryHeadId" listValue="salaryHeadName"  headerKey="" headerValue="Select Salary Head" onchange="submitForm('2');" list="salaryHeadList" />
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
				<%
					List alEmpReport = (List) request.getAttribute("alEmpReport");
					Map hmSalaryList = (Map) request.getAttribute("hmSalaryList");
					Map hmSalaryHeadsMap = (Map) request.getAttribute("hmSalaryHeadsMap");
					Map hmOtherearning = (Map) request.getAttribute("hmOtherearning");
					Map hmOtherearningId = (Map) request.getAttribute("hmOtherearningId");
					Map hmOtherearningValue = (Map) request.getAttribute("hmOtherearningValue");
					UtilityFunctions uF = new UtilityFunctions();
					
					String sHeadType = (String) request.getAttribute("sHeadType");
					List<String> ckEmpPayList = (List<String>) request.getAttribute("ckEmpPayList");
					if(ckEmpPayList == null) ckEmpPayList = new ArrayList<String>();
					
					String roundOffCondition = (String)request.getAttribute("roundOffCondition");
				%>  
			<div style="clear: both;"></div>
			<!-- <div class="row row_without_margin">
					<input type="hidden" id="exceldownload" name="exceldownload" value ="false">
					<p style="font-size: 14px;" class="pull-right">
						<a href="javascript:void(0)" title="Export to Excel" class="excel pull-right" onclick="downloadForm();"></a>
					</p>
			</div> -->
			<div style="text-align: right;"> 
					<a href="javascript:void(0)" onclick="importVariable();">Import Variable</a>
				</div>
			<div class="row row_without_margin">
				<div class="col-lg-8">
					<table class="table table-bordered">
						<tr>
							<th style="text-align: center;">Employee Name</th>
							<th style="text-align: center;">Variable Amount</th>
							<th style="text-align: center;" colspan="2">Action</th>
						</tr>
						<%
						int i = 0;
						for (; alEmpReport != null && i < alEmpReport.size(); i++) {
							List alEmpReportInner = (List) alEmpReport.get(i);
							String payStatus="0";
							if(ckEmpPayList.contains((String) alEmpReportInner.get(0))){
								payStatus="1";
							}
						%>
						<tr>
							<td><%=(String) alEmpReportInner.get(1)%>
					    		<input type="hidden" id="idStrEmpId<%=i%>" name="strEmpId" value="<%=(String) alEmpReportInner.get(0)%>">
					    	</td>
					    	
					    	<td align="center" style="background-color: #eee;">
					    		<input style="width:75px !important;text-align: right" type="text" id="idStrIncentiveAmount<%=i%>" name="strIncentiveAmount" value="<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmOtherearningValue.get((String) alEmpReportInner.get(0))))%>"/>
					    	</td>
					    	
					    	<td align="center">
					    	<% if (hmOtherearning != null && uF.parseToInt((String) hmOtherearning.get((String) alEmpReportInner.get(0))) == 1) { %>
					    		<div id="myDiv_<%=i%>">
						    		<!-- <img src="images1/icons/approved.png" width="17px" />  -->
						    		<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
						    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOtherEarning.action?requestid=<%=uF.parseToInt((String) hmOtherearningId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
						    	</div>
					    	<% } else if (hmOtherearning != null && uF.parseToInt((String) hmOtherearning.get((String) alEmpReportInner.get(0))) == -1) { %>
					    		<div id="myDiv_<%=i%>">
						    		<!-- <img src="images1/icons/denied.png" width="17px" /> -->
						    		<i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>
						    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOtherEarning.action?requestid=<%=uF.parseToInt((String) hmOtherearningId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
						    	</div>
					    	<% } else if (hmOtherearning != null && uF.parseToInt((String) hmOtherearning.get((String) alEmpReportInner.get(0))) == 2) { %>
					    		<div id="myDiv_<%=i%>">
						    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOtherEarning.action?requestid=<%=uF.parseToInt((String) hmOtherearningId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="17px" src="images1/icons/icons/approve_icon.png"/>
						    		<img   width="16px" src="images1/icons/icons/close_button_icon.png"> --%> 
						    		<i class="fa fa-check-circle checknew" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOtherEarning.action?requestid=<%=uF.parseToInt((String) hmOtherearningId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>"></i>
									<i class="fa fa-times-circle cross" aria-hidden="true"  onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOtherEarning.action?requestid=<%=uF.parseToInt((String) hmOtherearningId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>"></i>
									
					    		</div>
					    	<% } else if (hmOtherearning != null && uF.parseToInt((String) hmOtherearning.get((String) alEmpReportInner.get(0))) == 0) { %>
					    		<div id="myDiv_<%=i%>"><input type="button" class="btn btn-primary" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>updateEarning(<%=i%>);<%} %>" value="Update"></div>
					    	<% } %>
					    	</td>
					    	<td>
					    	<% if (sHeadType != null && sHeadType.equals("E")) { %>
					    		<a href="javascript:void(0)" onclick="prevOtherEarning(<%=(String) alEmpReportInner.get(0)%>,'<%=(String) alEmpReportInner.get(1)%>','E')">Previous Variable</a>
					    	<% } else if (sHeadType != null && sHeadType.equals("D")) { %>
					    		<a href="javascript:void(0)" onclick="prevOtherEarning(<%=(String) alEmpReportInner.get(0)%>,'<%=(String) alEmpReportInner.get(1)%>','D')">Previous Variable</a>
					    	<% } %>
					    	</td>
						</tr>
						<% }
						if (i == 0) {
						%>
						<tr>
    						<td colspan="4">
        						<div style="width: 96%;" class="msg nodata"><span>No employee found for the current selection</span></div>
    						</td>
						</tr>
						<% } %>
 					</table>
				</div>
				
			</div>
		</s:form>
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
    $(function(){
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