<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>


<!-- Custom form for adding new records -->

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Advance Report" name="title"/>
</jsp:include> --%>

<script>
	$(function() {
		$('#lt').DataTable({
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
		
		$("#strStartDate").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("#strEndDate").datepicker({
			format : 'dd/mm/yyyy'
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

	function approveAdvance(adv_id) {

		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Approve/Deny Advance');
		 $.ajax({
				url : "ApproveAdvance.action?strAdvId=" + adv_id,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}

	function settleAdvance(adv_id, status) {

		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Advance Settlement');
		$.ajax({
			url : "SettleAdvance.action?strAdvId=" + adv_id + "&status=" + status,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

	
	function getLevelwiseEmployee(value) {
		var orgId = document.getElementById("f_org").value;
		var strWLocation = document.getElementById("f_strWLocation").value;
		var action = 'GetEmployeeList.action?level=' + value + '&f_org=' + orgId + '&location=' + strWLocation + '&strMul=LblAll';
		getContent('myDiv', action);
		//$("#strSelectedEmpId").multiselect().multiselectfilter();
	}
	
	
	function submitForm(type){
		var org = document.getElementById("f_org").value;
		var f_strWLocation = document.getElementById("f_strWLocation").value;
		var f_level = document.getElementById("f_level").value;
		var strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
		var strStartDate = document.getElementById("strStartDate").value;
		var strEndDate = document.getElementById("strEndDate").value;
		var paramValues = "";
		
		if(type=='3')
			{
			paramValues = '&f_strWLocation='+f_strWLocation+'&f_level='+f_level+'&strSelectedEmpId='+strSelectedEmpId
			+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate;
			var action = 'GetEmployeeList.action?level=' + f_level + '&f_org=' + org + '&location=' + f_strWLocation + '&strMul=LblAll';
			getContent('myDiv', action);
		}
		
		
		if(type == '2') {
			paramValues = '&f_strWLocation='+f_strWLocation+'&f_level='+f_level+'&strSelectedEmpId='+strSelectedEmpId
			+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate;							
		}
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'AdvanceReport.action?f_org='+org+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}
	
	
</script>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 400px;">
		<s:form name="frm" action="AdvanceReport" theme="simple" cssStyle="margin-bottom: 20px;">
			<div class="box box-default collapsed-box">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
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
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" onchange="submitForm('2');" list="wLocationList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" onchange="submitForm('3');" list="levelList" key="" required="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Employees</p>
								<div id="myDiv" style="float:left;margin-right:5px">
									<s:select theme="simple" label="All Employees" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeName" headerKey="0" headerValue="All Employee" list="empNamesList" key="" required="true" onchange="submitForm('2');" />
								</div>
							</div>
						</div>
					</div>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">From Date</p>
								<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;" readonly="true"></s:textfield>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">To Date</p>
								<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" readonly="true"></s:textfield>
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
		</s:form>


		<%-- <display:table name="reportList" cellspacing="1" class="table table-bordered table-striped" export="false" pagesize="50" id="lt" requestURI="AdvanceReport.action" width="100%"> --%>
		<display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt">
			<display:column style="text-align:left" nowrap="nowrap" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
			<display:column style="text-align:left" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
			<display:column style="text-align:center" nowrap="nowrap" title="Date of Adv"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
			<display:column style="text-align:right" nowrap="nowrap" title="Adv Amount"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
			<display:column style="text-align:right" nowrap="nowrap" title="Claim Amount"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
			<display:column style="text-align:right" nowrap="nowrap" title="Eligibility"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
			<display:column style="text-align:right" nowrap="nowrap" title="Balance"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
			<display:column style="text-align:right" nowrap="nowrap" title="Settlement"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
			<display:column style="text-align:right" title="Balance Settlement Amount(Written Off)"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>

		</display:table>

		<!-- Legends -->
		<div style="float: left; width: 25%; margin-top: 50px"> <div class="label"><strong>Action Legends</strong></div>
			<div><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/approved.png">Approved --%><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i> Approved </div>
			<div><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/denied.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>Denied </div>
			<div><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"></i>Waiting for approval</div>
		</div> 
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

