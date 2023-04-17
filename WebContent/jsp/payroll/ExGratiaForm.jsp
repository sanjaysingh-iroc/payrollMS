<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<script> 
	function prevExGratia(emp_id,empname) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Ex-Gratia of '+empname); 
		 $.ajax({
			url : "PrevExGratiaForm.action?strEmpId="+emp_id,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

function validateField(id){
	var field = document.getElementById("idStrExGratiaAmount"+id);
	if(field.value==''){
		alert('Please enter valid amount');
		return false;
	}else{
		return true;
	}
}

function updateExGratia(id){
	var field = document.getElementById("idStrExGratiaAmount"+id);
	if(field.value=='' || field.value=='0'){
		alert('Please enter valid amount');
	}else{
		var emp_id=document.getElementById('idStrEmpId'+id).value;
		var paycycle=document.getElementById('paycycle').value;
		var amt=document.getElementById('idStrExGratiaAmount'+id).value;
		
		var action ='UpdateExGratiaForm.action?emp_id='+emp_id+'&paycycle='+paycycle+'&amt='+amt+'&count='+id;
		getContent('myDiv_'+id, action);		
	}
}

function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

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
		url: 'ExGratiaForm.action?f_org='+org+paramValues,
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



<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Ex-Gratia Form" name="title"/>
</jsp:include>  --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<s:form name="frm_ExGratiaForm" action="ExGratiaForm" theme="simple" method="post">
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
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
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
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true" />
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
			List<List<String>> alEmpReport = (List<List<String>>) request.getAttribute("alEmpReport");
			Map<String, String> hmExGratia = (Map<String, String>) request.getAttribute("hmExGratia");
			if(hmExGratia == null) hmExGratia = new HashMap<String, String>();
			Map<String, String> hmExGratiaId = (Map<String, String>) request.getAttribute("hmExGratiaId");
			if(hmExGratiaId == null) hmExGratiaId = new HashMap<String, String>();
			Map<String, String> hmExGratiaValue = (Map<String, String>) request.getAttribute("hmExGratiaValue");
			if(hmExGratiaValue == null) hmExGratiaValue = new HashMap<String, String>();			
			Map<String, String> hmExGratiaCalAmt = (Map<String, String>) request.getAttribute("hmExGratiaCalAmt");
			if(hmExGratiaCalAmt == null) hmExGratiaCalAmt = new HashMap<String, String>();
			
			List<String> ckEmpPayList = (List<String>) request.getAttribute("ckEmpPayList");
			if(ckEmpPayList == null) ckEmpPayList = new ArrayList<String>();
			
			String roundOffCondition = (String)request.getAttribute("roundOffCondition");
			%>

			<div class="clr margintop20"></div>
			<table class="table table-bordered table-striped">
				<tr>
					<th align="center">Employee Name</th>
					<th align="center">Ex-Gratia Calculated Amount</th>
					<th align="center">Ex-Gratia Amount</th>
					<th align="center" colspan="2">Action</th>
				</tr>
				<%
			  	int i = 0;
         		for (; alEmpReport != null && i < alEmpReport.size(); i++) {
         			List<String> alEmpReportInner = (List<String>) alEmpReport.get(i);
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
			    		<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmExGratiaCalAmt.get((String) alEmpReportInner.get(0))))%>
			    	</td>
			    	
			    	<td align="center" style="background-color: #eee;">
			    		<%
			    			String exGratiaAmt = "";
			    			if((String) hmExGratiaValue.get((String) alEmpReportInner.get(0))!=null){
			    				exGratiaAmt = (String) hmExGratiaValue.get((String) alEmpReportInner.get(0));
			    			} else {
			    				exGratiaAmt = (String) hmExGratiaCalAmt.get((String) alEmpReportInner.get(0));
			    			}
			    		%>
			    		<input style="width:75px !important;text-align: right" type="text" id="idStrExGratiaAmount<%=i%>" name="strExGratiaAmount" value="<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(exGratiaAmt)) %>"  onkeypress="return isNumberKey(event)"/>
			    	</td>
			    	<td align="center">
			    	<%
			    		if (hmExGratia != null && uF.parseToInt((String) hmExGratia.get((String) alEmpReportInner.get(0))) == 1) {
			    	%>
			    		<div id="myDiv_<%=i%>">
				    		<!-- <img src="images1/icons/approved.png" width="17px" /> -->
				    		<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
				    		
				    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateExGratiaForm.action?requestid=<%=uF.parseToInt((String) hmExGratiaId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
				    	</div>
			    	<%
			    		} else if (hmExGratia != null && uF.parseToInt((String) hmExGratia.get((String) alEmpReportInner.get(0))) == -1) {
			    	%>
			    		<div id="myDiv_<%=i%>">
				    		<!-- <img src="images1/icons/denied.png" width="17px" /> -->
				    		<i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>
				    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateExGratiaForm.action?requestid=<%=uF.parseToInt((String) hmExGratiaId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
				    	</div>
			    	<%
			    		} else if (hmExGratia != null && uF.parseToInt((String) hmExGratia.get((String) alEmpReportInner.get(0))) == 2) {
			    	%>
			    	<div id="myDiv_<%=i%>">
				    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateExGratiaForm.action?requestid=<%=uF.parseToInt((String) hmExGratiaId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="17px" src="images1/icons/icons/approve_icon.png">
				    		<img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateExGratiaForm.action?requestid=<%=uF.parseToInt((String) hmExGratiaId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="16px" src="images1/icons/icons/close_button_icon.png"> --%> 
				    		
				    		<i class="fa fa-times-circle cross" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateExGratiaForm.action?requestid=<%=uF.parseToInt((String) hmExGratiaId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>"  ></i>
                            <i class="fa fa-check-circle checknew" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateExGratiaForm.action?requestid=<%=uF.parseToInt((String) hmExGratiaId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" ></i>
			    		</div>
			    	<%
			    		} else if (hmExGratia != null && uF.parseToInt((String) hmExGratia.get((String) alEmpReportInner.get(0))) == 0) {
			    	%>
			    		<div id="myDiv_<%=i%>"><input type="button" class="btn btn-primary" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>updateExGratia(<%=i%>);<%} %>" value="Update"></div>
			    	<%
			    		}
			    	%>
			    	</td>
			    	<td>
			    		<a href="javascript:void(0)" onclick="prevExGratia('<%=(String) alEmpReportInner.get(0)%>','<%=(String) alEmpReportInner.get(1)%>')">Previous Ex-Gratia</a>
			    	</td>
			    	
			    	
			    </tr>
			    <%
			    	}
			    	if (i == 0) {
			    %>
			    		<tr><td colspan="5" class="msg nodata"><span>No employee found for the current selection</span></td></tr>
			    <%
			    	}
			    %>
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
					<h4 class="modal-title">Candidate Information</h4>
				</div>
				<div class="modal-body"
					style="height: 400px; overflow-y: auto; padding-left: 25px;">
				</div>
				<div class="modal-footer">
					<button type="button" id="closeButton" class="btn btn-default"
						data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>

<script type="text/javascript">
$(document).ready(function(){
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

$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
});

</script>