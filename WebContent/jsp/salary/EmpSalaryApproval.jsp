<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/customAjax.js"></script>
<% 
    UtilityFunctions uF = new UtilityFunctions();
    String leaveStatus = (String) request.getAttribute("leaveStatus");
%>
<script>
    function importSalaryStructure(){    	 
     	var f_org=document.getElementById("f_org").value;
     	var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Import Salary Structure');
		 $.ajax({
			url : "ImportSalaryStructure.action?strOrg="+f_org,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }	
    	
    function selectall(x,strEmpId){
    	var  status=x.checked; 
    	var  arr= document.getElementsByName(strEmpId);
    	for(i=0;i<arr.length;i++){ 
      		arr[i].checked=status;
     	}
    	if(x.checked == true){
    		document.getElementById("unSendSpan").style.display = 'none';
    		document.getElementById("sendSpan").style.display = 'block';
    	} else {
    		document.getElementById("unSendSpan").style.display = 'block';
    		document.getElementById("sendSpan").style.display = 'none';
    	}
    }
    
    function checkAll(){
    	var sendAll = document.getElementById("sendAll");		
    	var strSendLogin = document.getElementsByName('strSalaryStruture');
    	var cnt = 0;
    	var chkCnt = 0;
    	for(var i=0;i<strSendLogin.length;i++) {
    		cnt++;
    		 if(strSendLogin[i].checked) {
    			 chkCnt++;
    		 }
    	 }
    	if(parseFloat(chkCnt) > 0) {
    		document.getElementById("unSendSpan").style.display = 'none';
    		document.getElementById("sendSpan").style.display = 'block';
    	} else {
    		document.getElementById("unSendSpan").style.display = 'block';
    		document.getElementById("sendSpan").style.display = 'none';
    	}
    	
    	if(cnt == chkCnt) {
    		sendAll.checked = true;
    	} else {
    		sendAll.checked = false;
    	}
    }
    
    function submitForm(type){
    	if(type == '1'){
    		document.getElementById("strSelectedEmpId").selectedIndex = "0";
    	}
    	document.frmEmpSalaryApproval.submit();
    }
    							
  
</script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script> 
<script>
    $(function() {
    	$("#strStartDate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strEndDate').datepicker('setStartDate', minDate);
        });
        
        $("#strEndDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#strStartDate').datepicker('setEndDate', minDate);
        });
        
        $("select[multiple='multiple']").multiselect().multiselectfilter();
        
        $('#lt1').DataTable({
    		"order": [],
    		"columnDefs": [ {
    		      "targets"  : 'no-sort',
    		      "orderable": false
    		    }],
    		'dom': 'lBfrtip',
            'buttons': [
    			'copy', 'csv', 'excel', 'pdf', 'print'
            ]
      	});
        
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
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Salary Approval" name="title"/>
    </jsp:include> --%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-none nav-tabs-custom">
            	<ul class="nav nav-tabs">
					<li class="active"><a href="javascript:void(0)" onclick="window.location='EmpSalaryApproval.action'" data-toggle="tab">Salary Approval</a></li>
					<%if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE){ %>
						<li><a href="javascript:void(0)" onclick="window.location='ApproveBasicFitment.action'" data-toggle="tab">Basic Fitment Approval</a></li>
					<% } %>
				</ul>
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                        <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
                        <%session.setAttribute(IConstants.MESSAGE,""); %>
                        <s:form name="frmEmpSalaryApproval" id="frmEmpSalaryApproval" action="EmpSalaryApproval" theme="simple">
                        	<div class="box box-default"> <!--  collapsed-box -->
								<%-- <div class="box-header with-border">
								    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
								    <div class="box-tools pull-right">
								        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
								        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
								    </div>
								</div> --%>
								<div class="box-body" style="padding: 5px; overflow-y: auto;">
									<div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Status</p>
												<s:select theme="simple" name="leaveStatus" list="#{'0':'All','1':'Approved', '2':'Pending'}" onchange="submitForm('2');"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Organisation</p>
												<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1')" list="orgList" key=""/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Employee</p>
												<s:select name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" headerValue="Select Employee" listValue="employeeCode" headerKey="" onchange="submitForm('2')" list="empList" key=""/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Location</p>
												<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Department</p>
												<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">SBU</p>
												<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Level</p>
												<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
											</div>
										</div>
									</div><br>
									<div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-calendar"></i>
										</div>
										<% 
												String strStartDate = (String)request.getAttribute("strStartDate");
												String strEndDate = (String)request.getAttribute("strEndDate");
												if(strStartDate == null || strStartDate.equals("null") || strStartDate.equals("")) {
													strStartDate = "From Date";
												}
												if(strEndDate == null || strEndDate.equals("null") || strEndDate.equals("")) {
													strEndDate = "To Date";
												}
											%>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">From Date</p>
												<input type="text" name="strStartDate" id="strStartDate" value="<%=strStartDate %>" onblur="fillField(this.id, 3);" onclick="clearField(this.id);"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">To Date</p>
												<input type="text" name="strEndDate" id="strEndDate" value="<%=strEndDate %>" onblur="fillField(this.id, 4);" onclick="clearField(this.id);"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">&nbsp;</p>
												<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2')"/>
											</div>
										</div>
									</div>										
								</div>
							</div>
                        	
                            
                            <% if(uF.parseToInt(leaveStatus)==0 || uF.parseToInt(leaveStatus)==2) { %>
                            <div style="margin-bottom: 10px;margin-top: 10px;">
                                <span id="unSendSpan" style="display: none;">
                                	<input type="button" name="unSend" class="btn btn-primary" value="Approve" />
                                </span>
                                <span id="sendSpan">
                                	<input type="submit" value="Approve" name="approveSubmit" class="btn btn-primary" onclick="return confirm('Are you sure, you want to approve salary structure of selected employee?')"/>
                                </span>
                            </div>
                            <% } %>
                            
                            <div style="text-align: right;"> 
								<a href="javascript:void(0)" onclick="importSalaryStructure();"><i class="fa fa-upload" aria-hidden="true"></i> Import Salary Structure</a>
							</div>
							
                            <table id="lt1" class="table table-bordered">
                                <thead>
                                    <tr>
                                        <% if(uF.parseToInt(leaveStatus)==0 || uF.parseToInt(leaveStatus)==2) { %>
                                        <th align="left" class="no-sort"><input type="checkbox" name="sendAll" id="sendAll" onclick="selectall(this,'strSalaryStruture')"/></th>
                                        <% } else { %>
                                        <% } %>
                                        <th>Employee Code</th>
                                        <th>Employee Name</th>
                                        <th>Designation</th>
                                        <th>Effective Date</th>
                                        <th>Gross Amount</th>
                                        <th>Approved Date</th>
                                        <th>Approved By</th>
                                        <th class="no-sort">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        List alReport = (List)request.getAttribute("alReport");
                                        for(int i=0; alReport!=null && i<alReport.size(); i++){
                                        	List alInner = (List)alReport.get(i);
                                    %>
                                    <tr>
                                        <%
                                            if(uF.parseToInt(leaveStatus)==0 || uF.parseToInt(leaveStatus)==2){
                                            	if(alInner.get(8).equals("Pending")){ %>
                                        <td><input type="checkbox" name="strSalaryStruture" id="strSalaryStruture" value="<%=alInner.get(9)+"::::"+alInner.get(10)+"::::"+alInner.get(11)%>" onclick="checkAll();"/></td>
                                        <% } else { %>
                                        <td></td>
                                        <%}
                                            }
                                            %>
                                        <td><%=alInner.get(0)%></td>
                                        <td><%=alInner.get(1)%></td>
                                        <td><%=alInner.get(2)%></td>
                                        <td><%=alInner.get(3)%></td>
                                        <td><%=alInner.get(4)%></td>
                                        <td><%=alInner.get(5)%></td>
                                        <td><%=uF.showData((String)alInner.get(6),"-")%></td>
                                        <td><%=alInner.get(7)%></td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </s:form>
                        <div class="custom-legends">
						  <div class="custom-legend pullout">
						    <div class="legend-info">Pull out</div>
						  </div>
						  <div class="custom-legend pending">
						    <div class="legend-info">Waiting for approval</div>
						  </div>
						  <div class="custom-legend approved">
						    <div class="legend-info">Approved</div>
						  </div>
						</div>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>


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

