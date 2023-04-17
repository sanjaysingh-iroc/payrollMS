<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%
    UtilityFunctions uF = new UtilityFunctions(); 
    String strUserType = (String)session.getAttribute(IConstants.USERTYPE); 
    %>
    
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8">
    $(function() {
        $( "#strStartDate" ).datepicker({format: 'dd/mm/yyyy'});
        $( "#strEndDate" ).datepicker({format: 'dd/mm/yyyy'});
    });
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
    $(document).ready( function () {
    	<%-- $('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers",  
        "aaSorting": [],		
        "sDom": '<"H"lTf>rt<"F"ip>',
        oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
        	aButtons: [
        		"csv", "xls", {
        			sExtends: "pdf",
        			sPdfOrientation: "landscape"
        			//sPdfMessage: "Your custom message would go here."
        			}, "print" 
        	]
        }	
        })  --%>
    	/* $('#lt').dataTable({
    		bJQueryUI : true,
    		"sPaginationType" : "full_numbers",
    		"bSort": false
    	}); */  
    	$('#lt').DataTable();
    });
    
    
    hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
    hs.outlineType = 'rounded-white';
    hs.wrapperClassName = 'draggable-header';
    
    
    function addNewRequest(){
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('New Request');
		$("#modalInfo").show();
		$.ajax({
			url : "AddNewRequisition.action",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
</script>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="My Requisitions" name="title"/>
    </jsp:include> --%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div class="leftbox reportWidth" >
                        <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
                        <s:form name="frm" action="MyRequests" theme="simple">
                            <div class="filter_div">
                                <div class="filter_caption">Filter</div>
                                <s:textfield name="strStartDate" id="strStartDate" cssClass="form-control autoWidth inline"></s:textfield>
                                <s:textfield name="strEndDate"  id="strEndDate" cssClass="form-control autoWidth inline"></s:textfield>
                                <s:submit value="Submit" cssClass="btn btn-primary"  cssStyle="margin-top: -3px;"/>
                            </div>
                        </s:form>
                        <div style="float:right; margin:0px 0px 10px 0px">
                            <input type="button" class="btn btn-primary" value="Add New Request" onclick="addNewRequest();" style="float:right;margin-bottom:10px;"/>
                        </div>
                        <!-- Place holder where add and delete buttons will be generated -->
                        <div style="clear: both;"></div>
                        <div class="add_delete_toolbar"></div>
                        <table class="table table-bordered" id="lt">
                            <thead>
                                <tr>
                                    <th style="text-align: center;">Status</th>
                                    <th style="text-align: center;">Requisition Type</th>
                                    <th style="text-align: center;">Document Type</th>
                                    <th style="text-align: center;">Infrastructure Type</th>
                                    <th style="text-align: center;">Purpose</th>
                                    <th style="text-align: center;">Requisition Date</th>
                                    <th style="text-align: center;">From Date</th>
                                    <th style="text-align: center;">To Date</th>
                                    <th style="text-align: center;">Download</th>
                                    <th style="text-align: center;">Receive</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    List<List<String>> reportList = (List<List<String>>)request.getAttribute("reportList");
                                    for(int i=0; reportList!=null && i < reportList.size(); i++){
                                    	List<String> innerList = (List<String>) reportList.get(i);
                                    %>
                                <tr>
                                    <td class="alignCenter" id="myDiv_<%=i%>"><%=innerList.get(2) %></td>
                                    <td class="alignLeft"><%=innerList.get(3) %></td>
                                    <td class="alignLeft"><%=innerList.get(4) %></td>
                                    <td class="alignLeft"><%=innerList.get(5) %></td>
                                    <td class="alignLeft"><%=innerList.get(6) %></td>
                                    <td class="alignCenter"><%=innerList.get(7) %></td>
                                    <td class="alignCenter"><%=innerList.get(8) %></td>
                                    <td class="alignCenter"><%=innerList.get(9) %></td>
                                    <td class="alignCenter"><%=innerList.get(10) %></td>
                                    <td class="alignCenter"><%=innerList.get(11) %></td>
                                </tr>
                                <%
                                    }
                                    %>
                            </tbody>
                        </table>
                        <div style="margin-top: 20px;">
                            <div><%-- img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"></i>Waiting for approval</div>
                            <div><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/approved.png">  --%><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>Approved</div>
                            <div><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/denied.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>Denied</div>
                            <div><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pullout.png">  --%><i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i> Pull Out</div>
                            <div><img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/act_now.png">Received</div>
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
<div id="addNewRequestDiv"></div>