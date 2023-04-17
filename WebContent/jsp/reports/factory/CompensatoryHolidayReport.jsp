<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%String strUserType = (String)session.getAttribute(IConstants.USERTYPE); %>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<script type="text/javascript" charset="utf-8">
    $(document).ready(function () {
    	
    		<%-- $('#lt').dataTable({ bJQueryUI: true, 
        "sPaginationType": "full_numbers",
        "aaSorting": [],
        "sDom": '<"H"lf>rt<"F"ip>',
        oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
        aButtons: [
        		"csv", "xls", {
        			sExtends: "pdf",
        			sPdfOrientation: "landscape"
        			//sPdfMessage: "Your custom message would go here."
        			}, "print" 
        	]
        }
        }); --%>
    		$('#lt').DataTable();
    });
    
</script>

<%-- 	<jsp:include page="../../common/SubHeader.jsp">
    <jsp:param value="<%=IMessages.TCompensatoryHoliday %>" name="title"/> 
    </jsp:include> --%>

                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <!-- <a href="CompensatoryHolidayReport.action?pdfGeneration=true" style="background-image: url('images1/file-pdf.png');background-repeat: no-repeat;float: right;padding-right:20px;height:25px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
                    
                    <a href="CompensatoryHolidayReport.action?pdfGeneration=true"><i class="fa fa-file-pdf-o" aria-hidden="true" style="right;padding-right:20px;height:25px;"></i></a>
                    
                    
                    <div class="clr"></div>
                    <table class="table table-bordered" id="lt">
                        <thead>
                            <tr>
                                <th style="text-align: left;" rowspan="2"class="no-sort">Sr.No</th>
                                <th style="text-align: left;" rowspan="2">No. in the register of workers</th>
                                <th style="text-align: left;" rowspan="2">Name</th>
                                <th style="text-align: left;" rowspan="2">Group of Relay No</th>
                                <th style="text-align: left;" rowspan="2">No. and date of exempting order</th>
                                <th style="text-align: left;" rowspan="2">Year</th>
                                <th style="text-align: left;" colspan="4">Weekly rest day lost due to the exempting order in</th>
                                <th style="text-align: left;" colspan="4">Date of compensatory holidays given to</th>
                                <th style="text-align: left;" rowspan="2">Lost days</th>
                                <th style="text-align: left;" rowspan="2"class="no-sort">Remarks</th>
                            </tr>
                            <tr>
                                <th style="text-align: left;">Jan-Mar</th>
                                <th style="text-align: left;">Apr-Jun</th>
                                <th style="text-align: left;">Jul-Sep</th>
                                <th style="text-align: left;">Oct-Dec</th>
                                <th style="text-align: left;">Jan-Mar</th>
                                <th style="text-align: left;">Apr-Jun</th>
                                <th style="text-align: left;">Jul-Sep</th>
                                <th style="text-align: left;">Oct-Dec</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
                            <% for (int i=0; i<couterlist.size(); i++) { %>
                            <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
                            <tr id = <%= cinnerlist.get(0) %> >
                                <td><%= cinnerlist.get(1) %></td>
                                <td><%= cinnerlist.get(2) %></td>
                                <td><%= cinnerlist.get(3) %></td>
                                <td><%= cinnerlist.get(4) %></td>
                                <td><%= cinnerlist.get(5) %></td>
                                <td><%= cinnerlist.get(6) %></td>
                                <td><%= cinnerlist.get(7) %></td>
                                <td><%= cinnerlist.get(8) %></td>
                                <td><%= cinnerlist.get(9) %></td>
                                <td><%= cinnerlist.get(10) %></td>
                                <td><%= cinnerlist.get(11) %></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <!-- /.box-body -->
