<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script> 
    function prevBonus(emp_id) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Bonus Details');
    	 $.ajax({
    			url : "PrevBonus.action?strEmpId="+emp_id,
    			cache : false,
    			success : function(data) { 
    				$(dialogEdit).html(data);
    			}
    		});
    }
     
    function validateField(id,payStatus){
    	var field = document.getElementById("idStrBonusPercent"+id);
    	var field1 = document.getElementById("idStrBonusAmount"+id);
    	var field2 = document.getElementById("idStrBonusCalAmount"+id);
    	if(field.value =='' && field1.value =='' && field2.value ==''){
    		alert('Please enter valid amount');
    	} else{
    		var emp_id=document.getElementById("idStrEmpId"+id).value;
    		var salary_id=document.getElementById("salaryId_"+id).value;
    		var paycycle=document.getElementById('idPaycycleId').value;
    		var amt=document.getElementById("idStrBonusAmount"+id).value;
    		var bonusCalAmt=document.getElementById("idStrBonusCalAmount"+id).value;
    		var percent=document.getElementById("idStrBonusPercent"+id).value;
    		
    		var action = 'UpdateBonus.action?emp_id='+emp_id+'&salary_id='+salary_id+'&paycycle='+paycycle;
    		action +='&amt='+amt+'&bonusCalAmt='+bonusCalAmt+'&percent='+percent+'&count='+id+'&payStatus'+payStatus;
    
    		getContent('myDiv_'+id, action);
    	}
    }
    
    function isNumberKey(evt)
    {
       var charCode = (evt.which) ? evt.which : event.keyCode;
       if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
          return false;
       }
       return true;
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
    
    function submitForm(type){
    	var org = document.getElementById("f_org").value;
    	var paycycle = document.getElementById("idPaycycleId").value;
    	
    	var f_level = document.getElementById("f_level").value;
    	var location = getSelectedValue("f_wLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	
    	var paramValues = '&strLocation='+location+'&strDepartment='+department+'&strService='+service+'&strLevel='+f_level+'&paycycle='+paycycle; 
    	var action = 'BonusForm.action?f_org='+org+paramValues; 
    	
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	$("#divResult").html(result);
       		}
    	});
    }
</script>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript">
    $(function(){
    	$("#f_wLocation").multiselect().multiselectfilter();
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
</script>

<%-- <section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
                    <div class="box box-primary"> --%>
                    
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                        <s:form name="frm_Bonus" action="BonusForm" theme="simple" method="post">
                        	<div class="box box-default collapsed-box" style="margin-top: 10px;">
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
											<i class="fa fa-filter" aria-hidden="true"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Organization</p>
												<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
		                                            listValue="orgName" onchange="submitForm('1');"
		                                            list="organisationList" key=""/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Paycycle</p>
												<s:select id="idPaycycleId" name="paycycle" listKey="paycycleId"
		                                            listValue="paycycleName" list="paycycleList" key=""
		                                            onchange="submitForm('1');"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Location</p>
												<s:select theme="simple" name="f_wLocation" id="f_wLocation" listKey="wLocationId"
		                                            listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Department</p>
												<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId"
		                                            listValue="deptName" multiple="true"></s:select>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">SBU</p>
												<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId"
		                                            listValue="serviceName" multiple="true"></s:select>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Level</p>
												<s:select theme="simple" name="f_level" id="f_level" listKey="levelId"
		                                            listValue="levelCodeName" list="levelList" key="" multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">&nbsp;</p>
												<input type="button" value="Submit" class="btn btn-primary" onclick="submitForm('1');"/>
											</div>
										</div>
									</div>
				                </div>
				            </div>
                        	<div class="clr margintop20"></div>
                            <%
                                	List alEmpReport = (List) request.getAttribute("alEmpReport");
                                	Map hmSalaryList = (Map) request.getAttribute("hmSalaryList");
                                	Map hmSalaryHeadsMap = (Map) request.getAttribute("hmSalaryHeadsMap");
                                	Map hmBonus = (Map) request.getAttribute("hmBonus");
                                	Map hmBonusId = (Map) request.getAttribute("hmBonusId");
                                	Map hmBonusValue = (Map) request.getAttribute("hmBonusValue");
                                	Map<String,String> hmEmpCalBonus = (Map<String,String>) request.getAttribute("hmEmpCalBonus");
                                	if(hmEmpCalBonus == null) hmEmpCalBonus = new HashMap<String, String>();
                                	Map<String,String> hmBonusPercent = (Map<String,String>) request.getAttribute("hmBonusPercent");
                                	if(hmBonusPercent == null) hmBonusPercent = new HashMap<String, String>();
                                	Map<String,String> hmBonusSalId = (Map<String,String>) request.getAttribute("hmBonusSalId");
                                	if(hmBonusSalId == null) hmBonusSalId = new HashMap<String, String>();
                                	
                                	List<String> ckEmpPayList = (List<String>) request.getAttribute("ckEmpPayList");
                                	if(ckEmpPayList == null) ckEmpPayList = new ArrayList<String>();
                                	
                                	UtilityFunctions uF = new UtilityFunctions();
                                	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
                                %>  
                            <table class="table table-bordered">
                                <tr>
                                    <th align="center">Employee Name</th>
                                    <th align="center">Percent (%) of Salary Heads</th>
                                    <th align="center"></th>
                                    <th align="center">Fixed Amount</th>
                                    <th align="center"></th>
                                    <th align="center">Bonus Calculated Amount</th>
                                    <th align="center" colspan="2">Action</th>
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
							    		<input style="width: 85px !important; text-align: right" type="text" id="idStrBonusPercent<%=i%>" name="strBonusPercent" onkeypress="return isNumberKey(event)" value="<%=uF.showData(hmBonusPercent.get((String) alEmpReportInner.get(0)),"")%>"/> of 
							    		<select style="width: 150px !important;" id="salaryId_<%=i%>">
							    		<%
							    			List alSalaryDetails = (List) hmSalaryList.get((String) alEmpReportInner.get(0));
							    			String salaryHeadId = uF.showData(hmBonusSalId.get((String) alEmpReportInner.get(0)),"");
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
							    	<td align="center" style="background-color: #eee;"><input style="width: 85px !important; text-align: right" type="text" id="idStrBonusAmount<%=i%>" name="strBonusAmount" value="<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String)hmBonusValue.get((String) alEmpReportInner.get(0))))%>" onkeypress="return isNumberKey(event)"></td>
							    	<td align="center">
							    		- OR -
							    	</td>
							    	<td align="center" style="background-color: #eee;">
							    		<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmEmpCalBonus.get((String) alEmpReportInner.get(0))))%>
							    		<input type="hidden" id="idStrBonusCalAmount<%=i%>" name="strBonusCalAmount" value="<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmEmpCalBonus.get((String) alEmpReportInner.get(0))))%>" />
							    	</td>
							    	<td align="center">
							    	<%
							    		if (hmBonus != null && uF.parseToInt((String) hmBonus.get((String) alEmpReportInner.get(0))) == 1) {
							    	%>
							    	<div id="myDiv_<%=i%>">
							    		<!-- <img src="images1/icons/approved.png" width="17px" />  -->
							    		<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
							    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateBonus.action?requestid=<%=uF.parseToInt((String) hmBonusId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
							    	</div>
							    	<%
							    		} else if (hmBonus != null && uF.parseToInt((String) hmBonus.get((String) alEmpReportInner.get(0))) == -1) {
							    	%>
							    	<div id="myDiv_<%=i%>">
							    		<!-- <img src="images1/icons/denied.png" width="17px" /> -->
							    		<i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>
							    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateBonus.action?requestid=<%=uF.parseToInt((String) hmBonusId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
							    	</div>
							    	<%
							    		} else if (hmBonus != null && uF.parseToInt((String) hmBonus.get((String) alEmpReportInner.get(0))) == 2) {
							    	%>
							    		<div id="myDiv_<%=i%>">
								    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateBonus.action?requestid=<%=uF.parseToInt((String) hmBonusId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="17px" src="images1/icons/icons/approve_icon.png"/> --%>
								    		<i class="fa fa-check-circle checknew" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateBonus.action?requestid=<%=uF.parseToInt((String) hmBonusId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" ></i>
								    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateBonus.action?requestid=<%=uF.parseToInt((String) hmBonusId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="16px" src="images1/icons/icons/close_button_icon.png"/> --%> 
								    		<i class="fa fa-times-circle cross" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateBonus.action?requestid=<%=uF.parseToInt((String) hmBonusId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>"></i>
							    		</div>
							    	<%
							    		} else if (hmBonus != null && uF.parseToInt((String) hmBonus.get((String) alEmpReportInner.get(0))) == 0) {
							    	%>
							   		<div id="myDiv_<%=i%>"><input type="button" class="btn btn-primary" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>validateField(<%=i%>,<%=payStatus%>);<%} %>" value="Update"></div>
							    	<%
							    		}
							    	%>
							    	</td>
							    	<td><a href="javascript:void(0)" onclick="prevBonus(<%=(String) alEmpReportInner.get(0)%>)">Previous Bonus</a></td>
                                </tr>
                                <%
                                    }
                                    	if (i == 0) {
                                    %>
                                <tr>
                                    <td colspan="8">
                                        <div style="width: 96%;" class="msg nodata"><span>No employee found for the current selection</span></div>
                                    </td>
                                </tr>
                                <%
                                    }
                                    %>
                            </table>
                        </s:form>
                    </div>
                </div>
                <!-- /.box-body -->
           <%--  </div>
        </section>
     </div>
</section> --%>

 <div class="modal" id="modalInfo" role="dialog">
            <div class="modal-dialog">
                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Candidate Information</h4>
                    </div>
                    <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>