<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
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

	$('#lt').dataTable({
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

function submitForm(){
 	document.frm_TravelDesk.submit();
}

function addTravelBooking(travelId, empId) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Booking');
	 $.ajax({
			url : "TravelBookingAttachment.action?travelId="+travelId+"&strEmpId="+empId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}

</script>


<%
UtilityFunctions uF = new UtilityFunctions();
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String strTitle = (String)request.getAttribute(IConstants.TITLE);

List<Map<String, String>> reportList = (List<Map<String, String>>)request.getAttribute("reportList");
Map<String, List<Map<String, String>>> hmBooking = (Map<String, List<Map<String, String>>>) request.getAttribute("hmBooking");
if(hmBooking == null) hmBooking = new HashMap<String, List<Map<String, String>>>();

%>
<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <s:form name="frm_TravelDesk" id="frm_TravelDesk" action="TravelDesk" theme="simple">
                    	<div class="box box-default collapsed-box" style="margin-top: 10px;">
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
										<i class="fa fa-filter" aria-hidden="true"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Organization</p>
											<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
											listValue="orgName"
											onchange="submitForm();" list="organisationList" key="" />
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
											<p style="padding-left: 5px;">Service</p>
											<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId"
											listValue="serviceName" multiple="true"></s:select>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Level</p>
											<s:select theme="simple" name="f_level" id="f_level" listKey="levelId"
											listValue="levelCodeName" list="levelList" key="" multiple="true"/>
										</div>
									</div>
								</div><br>
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
										<i class="fa fa-calendar"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Start Date</p>
											<s:textfield name="strStartDate" id="strStartDate" readonly="true"></s:textfield>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">End Date</p>
											<s:textfield name="strEndDate" id="strEndDate" readonly="true"></s:textfield>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">&nbsp;</p>
											<s:submit value="Submit" cssClass="btn btn-primary"/>
										</div>
									</div>
								</div>
			                </div>
			            </div>
					</s:form>
					
					<div class="clr margintop20">
						<table class="table table-bordered" id="lt">
								<thead>
									<tr>
										<th>Employee Name</th>
										<th>Apply date</th>
										<th>From</th>
										<th>To</th>
										<th>No of days</th>
										<th>Employee Reason</th>
										<th>Is Concierge</th>
										<th>Travel Mode</th>
										<th>Is Booking</th>
										<th>Booking Info</th>
										<th>Is Accommodation</th>
										<th>Accommodation Info</th>
										<th class="no-sort">Action</th>
									</tr>
								</thead>
								<tbody>
								<% 					
								   for(int i=0; reportList!=null && i<reportList.size(); i++) { 
									 Map<String, String> hmInner = (Map<String, String>)reportList.get(i); 
								%>
										<tr id="<%=hmInner.get("TRAVEL_ID") %>">
											<td><%=uF.showData(hmInner.get("EMP_NAME"), "") %></td>
											<td><%=uF.showData(hmInner.get("ENTRY_DATE"), "") %></td>
											<td><%=uF.showData(hmInner.get("FROM_DATE"), "") %></td>
											<td><%=uF.showData(hmInner.get("TO_DATE"), "") %></td>
											<td><%=uF.showData(hmInner.get("NO_DAYS"), "") %></td>
											<td><%=uF.showData(hmInner.get("EMP_REASON"), "") %></td>
											<td><%=uF.showData(hmInner.get("IS_CONCIERGE"), "") %></td>
											<td><%=uF.showData(hmInner.get("TRAVEL_MODE"), "") %></td>
											<td><%=uF.showData(hmInner.get("IS_BOOKING"), "") %></td>
											<td><%=uF.showData(hmInner.get("BOOKING_INFO"), "") %></td>
											<td><%=uF.showData(hmInner.get("IS_ACCOMMODATION"), "") %></td>
											<td><%=uF.showData(hmInner.get("ACCOMMODATION_INFO"), "") %></td>
											<td>
												<%if(hmBooking.containsKey(hmInner.get("TRAVEL_ID"))){%>
													<span style="float:left;"><a href="javascript:void(0);" onclick="addTravelBooking('<%=hmInner.get("TRAVEL_ID") %>','<%=hmInner.get("EMP_ID") %>');">Booked</a></span>
													<span style="float:left;">
														<%
															List<Map<String, String>> alData = (List<Map<String, String>>)hmBooking.get(hmInner.get("TRAVEL_ID"));
															for(int j = 0; alData!=null && j<alData.size(); j++){
															Map<String, String> hmAttach = (Map<String, String>) alData.get(j);
															if(hmAttach == null) hmAttach = new HashMap<String, String>();
															
															if(hmAttach.get("FILE_PATH")!=null && !hmAttach.get("FILE_PATH").trim().equals("") && !hmAttach.get("FILE_PATH").trim().equalsIgnoreCase("NULL")){
														%>
																<%=hmAttach.get("FILE_PATH") %>
														<%} 
														}%>
													</span>
												<%} else { %>
													<a href="javascript:void(0);" onclick="addTravelBooking('<%=hmInner.get("TRAVEL_ID") %>','<%=hmInner.get("EMP_ID") %>');">Yet to be Booked</a>	
												<%} %>
											</td>
										</tr>
									<% } %>
								</tbody>
							</table>
					</div>
                </div>
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