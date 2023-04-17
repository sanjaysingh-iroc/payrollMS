<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>

$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	
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
    function prevIncentives(emp_id) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Incentive Details');
    	 $.ajax({
    			url : "PrevIncentives.action?strEmpId="+emp_id,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    }
    
    function isNumberKey(evt){
       var charCode = (evt.which) ? evt.which : event.keyCode; 
       if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
          return false;
       }
       return true;
    }
    
    function validateField(id){
    	var field = document.getElementById("idStrIncentiveAmount"+id);
    	var field1 = document.getElementById("idStrIncentivePercent"+id);
    	if(field.value=='' && field1.value ==''){
    		alert('Please enter valid amount');
    		return false;
    	}else{
    		return true;
    	}
    }
    
    function getContentUpdate(divId,count) {
    	var empId = document.getElementById("idStrEmpId"+count).value;
    	var salaryId = document.getElementById("salaryId_"+count).value;
    	var paycycle = document.getElementById("paycycle").value;
    	var amt = document.getElementById("idStrIncentiveAmount"+count).value;
    	var percent = document.getElementById("idStrIncentivePercent"+count).value;
    	
    	var action = "UpdateIncentive.action?emp_id="+empId+"&salary_id="+salaryId+"&paycycle="+paycycle+"&amt="+amt+"&percent="+percent+"&count="+count;
   		$.ajax({
   			type:'GET',
   			url : action,
   			cache:false,
   		    success:function(data) {
   		    	//alert("data==>"+data);
   		    	document.getElementById(divId).innerHTML = data;
   		    }
   		});
    }
</script>

<script type="text/javascript">

    function submitForm(type){
    	var org = document.getElementById("f_org").value;
    	var paycycle = document.getElementById("paycycle").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	var level = getSelectedValue("f_level");
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&paycycle='+paycycle;
    	}
    	//alert("service ===>> " + service);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'IncentiveForm.action?f_org='+org+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	$("#divResult").html(result);
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
    
</script>

<%-- 
    <jsp:include page="../common/SubHeader.jsp">
    	<jsp:param value="Incentives" name="title"/>
    </jsp:include> --%> 
	
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<s:form name="frm_Incentive" action="IncentiveForm" theme="simple" method="post">
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
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select id="paycycle" name="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key="" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Service</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
						</div>
					</div>
				</div>
				<!-- /.box-body -->
			</div>
            <%
	            UtilityFunctions uF = new UtilityFunctions();
				List alEmpReport = (List) request.getAttribute("alEmpReport");
				Map hmSalaryList = (Map) request.getAttribute("hmSalaryList");
				Map hmSalaryHeadsMap = (Map) request.getAttribute("hmSalaryHeadsMap");
				Map hmIncentives = (Map) request.getAttribute("hmIncentives");
				Map hmIncentivesId = (Map) request.getAttribute("hmIncentivesId");
				Map hmIncentivesValue = (Map) request.getAttribute("hmIncentivesValue");
				
				Map<String,String> hmIncentivePercent = (Map<String,String>) request.getAttribute("hmIncentivePercent");
				if(hmIncentivePercent == null) hmIncentivePercent = new HashMap<String, String>();
				Map<String,String> hmIncentiveSalId = (Map<String,String>) request.getAttribute("hmIncentiveSalId");
				if(hmIncentiveSalId == null) hmIncentiveSalId = new HashMap<String, String>();
				
				List<String> ckEmpPayList = (List<String>) request.getAttribute("ckEmpPayList");
				if(ckEmpPayList == null) ckEmpPayList = new ArrayList<String>();
				
				
				String roundOffCondition = (String)request.getAttribute("roundOffCondition");
				
			%>  
			<div style="clear: both;"></div>
			<table class="table table-bordered" style="margin-top: 35px;">
				<tr>
				    <th class="textcenter">Employee Name</th>
				    <th class="textcenter">Percent (%) of Salary Heads</th>
				    <th class="textcenter"></th>
				    <th class="textcenter">Fixed Amount</th>
				    <th class="textcenter" colspan="2">Action</th>
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
			    	
			    	<td align="center" style="background-color: #efe;">
			    		<input style="width:75px !important;text-align: right" type="text" id="idStrIncentivePercent<%=i%>" name="strIncentivePercent" onkeypress="return isNumberKey(event)"  value="<%=uF.showData(hmIncentivePercent.get((String) alEmpReportInner.get(0)),"")%>"/> of 
			    		<select style="width:100px" id="salaryId_<%=i%>">
			    		<%
			    			List alSalaryDetails = (List) hmSalaryList.get((String) alEmpReportInner.get(0));
			    			String salaryHeadId = uF.showData(hmIncentiveSalId.get((String) alEmpReportInner.get(0)),"");
			    					for (int x = 0; alSalaryDetails != null && x < alSalaryDetails.size(); x++) {
			    		%>
							<option value="<%=alSalaryDetails.get(x)%>" <%if(salaryHeadId.equals(alSalaryDetails.get(x))){ %> selected<%} %>><%=(String) hmSalaryHeadsMap.get(alSalaryDetails.get(x))%></option>
							<%
								}
							%>
			    		</select>
			    	
			    	</td>
			    	<td align="center">
			    		- OR -
			    	</td>
			    	<td align="center" style="background-color: #eee;"><input style="width:75px;text-align: right" type="text" id="idStrIncentiveAmount<%=i%>" name="strIncentiveAmount" onkeypress="return isNumberKey(event)" value="<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmIncentivesValue.get((String) alEmpReportInner.get(0))))%>"/></td>
			    	<td align="center">
			    	<%
			    		if (hmIncentives != null && uF.parseToInt((String) hmIncentives.get((String) alEmpReportInner.get(0))) == 1) {
			    	%>
			    		<div id="myDiv_<%=i%>">
				    		<!-- <img src="images1/icons/approved.png" width="17px" />  -->
				    		<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
				    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateIncentive.action?requestid=<%=uF.parseToInt((String) hmIncentivesId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
				    	</div>
			    	<%
			    		} else if (hmIncentives != null && uF.parseToInt((String) hmIncentives.get((String) alEmpReportInner.get(0))) == -1) {
			    	%>
			    		<div id="myDiv_<%=i%>">
				    		<!-- <img src="images1/icons/denied.png" width="17px" /> -->
				    		<i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>
				    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateIncentive.action?requestid=<%=uF.parseToInt((String) hmIncentivesId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
				    	</div>
			    	<%
			    		} else if (hmIncentives != null && uF.parseToInt((String) hmIncentives.get((String) alEmpReportInner.get(0))) == 2) {
			    	%>
			    		<div id="myDiv_<%=i%>">
			    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateIncentive.action?requestid=<%=uF.parseToInt((String) hmIncentivesId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="17px" src="images1/icons/icons/approve_icon.png">
			    		<img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateIncentive.action?requestid=<%=uF.parseToInt((String) hmIncentivesId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="16px" src="images1/icons/icons/close_button_icon.png"> --%> 
			    		
			    		<i class="fa fa-times-circle cross" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateIncentive.action?requestid=<%=uF.parseToInt((String) hmIncentivesId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>"></i>
						<i class="fa fa-check-circle checknew" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateIncentive.action?requestid=<%=uF.parseToInt((String) hmIncentivesId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>"></i>
			    		
			    		
			    		</div>
			    	<%
			    		} else if (hmIncentives != null && uF.parseToInt((String) hmIncentives.get((String) alEmpReportInner.get(0))) == 0) {
			    	%>
			    		<div id="myDiv_<%=i%>"><input type="button" class="btn btn-primary" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>((validateField(<%=i%>))?getContentUpdate('myDiv_<%=i%>','<%=i%>'):'')<%} %>" value="Update"></div>
			    	<%
			    		}
			    	%>
			    	</td>
			    	<td><a href="javascript:void(0)" onclick="prevIncentives(<%=(String) alEmpReportInner.get(0)%>)">Previous Incentives</a></td>
				</tr>
				<%} if (i == 0) { %>
				<tr>
	    			<td colspan="6">
						<div style="width: 96%;" class="msg nodata"><span>No employee found for the current selection</span></div>
	    			</td>
				</tr>
				<%} %>
			</table>
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