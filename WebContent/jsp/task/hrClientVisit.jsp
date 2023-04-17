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
	List<List<String>> alClientVisits =  (List<List<String>>) request.getAttribute("alClientVisits");
	
%>
<script>
function addNewClientVisit() {
	 var xmlhttp;
		if (window.XMLHttpRequest) {
	        // code for IE7+, Firefox, Chrome, Opera, Safari
	        xmlhttp = new XMLHttpRequest();
		}
	    if (window.ActiveXObject) {
	        // code for IE6, IE5
	    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    if (xmlhttp == null) {
	            alert("Browser does not support HTTP Request");
	            return;
	    } else {
	    	var dialogEdit = '.modal-body';
	    	$(dialogEdit).empty();
	    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$('.modal-title').html('Add New Client visit');
	    	$("#modalInfo").show();
	    	$.ajax({
             url : 'addNewClientVisit.action',
             cache : false,
             success : function(data) {
             	     $(dialogEdit).html(data);
             }
         });
	    }
	}
	
	function editDeletevisit(operation,visitId)
	{
		if(operation == 1)
		{
			var dialogEdit = '.modal-body';
	    	$(dialogEdit).empty();
	    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$('.modal-title').html('Edit Client Visit');
	    	$("#modalInfo").show();
	    	$.ajax({
                url : 'addNewClientVisit.action?visitId='+visitId+'&operation=E',
                cache : false,
                success : function(data) {
                //	alert("data==>"+data);
                	      $(dialogEdit).html(data);
                }
            });
		}
		if(operation == 2)
		{
			 if(confirm('Are you sure, you want to delete this visit?')) {
					 var xhr = $.ajax({
			             url : 'addNewClientVisit.action?visitId='+visitId+'&operation=D',
			             cache : false,
			             success : function(data) {
			            	 document.getElementById("trvisit_"+visitId).style.display="none"; 
			            	 
			             }
			         });
				}
		}
	}
</script>
<section class="content">
	<div class="row jscroll">
		<section class="col-lg-12 connectedSortable">
			<div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
						<% if(strUserType != null && strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN)) { %>
							<div class="row row_without_margin">
	                        	<div class="col-lg-12" >
	                        		<a href="javascript:{}" style = "float:right;margin-top:1%;" onclick="addNewClientVisit()"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>Add New Client Visit</a>
	                        	</div>
	                        </div>
						 <%} %>
						<div style="float: left; width:100%; margin-top:2%">
						
							<table id="lt" class="table table-bordered">
							      <thead>
										<tr>
											<th style="text-align: left;">HR Name</th>
											<th style="text-align: left;">Client Name</th>
											<th style="text-align: left;"> Description</th>
											<th style="text-align: left;"> Date</th>
											<th style="text-align: left;"> Time</th>
										</tr>
									</thead>
									<tbody>
									
									<% if(alClientVisits !=null){
										for(int i =0; i < alClientVisits.size(); i++ )
										{
										  List<String> alInner = alClientVisits.get(i);
										   if(alInner!= null && alInner.size() > 0 )
										   {
											   System.out.println("Date==."+alInner.get(4)+"Time-"+alInner.get(5));
									%>
								 	<tr id="trvisit_<%=alInner.get(0)%>">
								 		<td><%=alInner.get(1)%></td>
								 		<td><%=alInner.get(2)%></td>
								 		<td><%=alInner.get(3)%></td>	
								 		<td><%=alInner.get(4)%></td>	
								 		<td><%=alInner.get(5)%></td>	
								 		<td>
											<a href="javascript:{}" onclick="editDeletevisit('1','<%=alInner.get(0)%>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
											<a href="javascript:{}" onclick="editDeletevisit('2','<%=alInner.get(0)%>');"><i class="fa fa-trash" aria-hidden="true"></i></a>
										</td>	
									</tr>
										<%}
										}
									}
									%>
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
                <h4 class="modal-title">Client Visit</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>