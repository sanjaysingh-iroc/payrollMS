<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<style>
#lt_wrapper .row{
margin-left: 0px;
margin-right: 0px;
}
</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/autocomplete/jquery-ui.min.js"> </script>

<%
	String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
	Map<String,List<List<String>>> hmEmpDetails =(Map<String,List<List<String>>>)request.getAttribute("hmEmpDetails");
	if(hmEmpDetails == null)
		hmEmpDetails = new HashMap<String,List<List<String>>>();
	Map<String,String>hmEmpNames = (Map<String,String>)request.getAttribute("hmEmpNames");
	List<String> liveProEmpIds =(List<String>)request.getAttribute("liveProEmpIds");
	Map<String, String> hmTaskAllocation = (Map<String, String>)request.getAttribute("hmTaskAllocation");
	//Map<String, List<String>> hmEmpTransition  = (Map<String, List<String>>) request.getAttribute("hmEmpTransition");
	Map<String, String> hmEmpTransition  = (Map<String,String>) request.getAttribute("hmEmpTransition");
	Map<String,String> hmonBechEmp =(Map<String,String>)request.getAttribute("hmonBechEmp");
//	System.out.println("hmEmpNames=====>"+hmEmpNames);
//	System.out.println("hmTaskAllocation---->"+hmTaskAllocation);
//	System.out.println("liveProEmpIds---->"+liveProEmpIds);
//	System.out.println("hmEmpTransition---->"+hmEmpTransition);
//	System.out.println("hmonBechEmp---->"+hmonBechEmp);
	
	
%>

<script type="text/javascript" charset="utf-8">
 
$(function(){
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

<script>
function viewWorkAllocation(emp_id,proStartDate,proEndDate) {
	//alert("xcv cxvx");
	//alert(emp_id);
	//alert(proStartDate);
	//alert(proEndDate);
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Task Summary');
	$.ajax({
		url : 'ProjectTaskAllocation.action?emp_id='+emp_id+'&proStartDate='+proStartDate+'&proEndDate='+proEndDate,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
</script>
<section class="content">
	<div class="row jscroll">
		<section class="col-lg-12 connectedSortable">
			<div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
						
						<div style="float: left; width:100%; margin-top:2%">
						
							<table id="lt" class="table table-bordered">
							      <thead>
										<tr>
											<th style="text-align: left;">Employee Name</th>
											<th style="text-align: left;">Tasks</th>
											<th style="text-align: left;">Transition</th>
											<th style="text-align: left;">On Bench</th>
										</tr>
								</thead>
								<tbody>
									<% for(int i = 0; liveProEmpIds != null && i < liveProEmpIds.size();i++){	%>
									<tr>
										<td nowrap="nowrap"><%=hmEmpNames.get(liveProEmpIds.get(i)) %></td>
										<td nowrap="nowrap"><%=hmTaskAllocation.get(liveProEmpIds.get(i)) %></td>
										<td nowrap="nowrap"><%=hmEmpTransition.get(liveProEmpIds.get(i)) %></td>
										<td nowrap="nowrap"><%=hmonBechEmp.get(liveProEmpIds.get(i)) %></td>
									</tr>
									<%} %>
								</tbody>
								
							</table>
						</div>
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
                <h4 class="modal-title">Task Summary</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>