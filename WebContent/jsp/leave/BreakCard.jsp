<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<% 
    List reportListPrint = (List)request.getAttribute("reportListPrint");
    %>
<!-- Custom form for adding new records -->
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=IMessages.TBreakCard %>" name="title" />
    </jsp:include> --%>
<script>
    $(document).ready( function () {
    	<%-- $('#lt').dataTable({ bJQueryUI: true, 
        "sPaginationType": "full_numbers",
        "iDisplayLength": 1000,
        "aLengthMenu": [
                        [1, 2, -1],
                        [1, 2, "All"]
                    ],
        "aaSorting": [[0, 'asc']],
        /* "sDom": '<"H"lTf>rt<"F"ip>', */
        "sDom": '<"H"f>rt<"F"ip>',
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
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div class="leftbox reportWidth">
                        <div class="filter_div">
                            <div class="filter_caption">Filter</div>
                            <s:form theme="simple" id="selectLevel" action="BreakCard"
                                method="post" name="frm" cssClass="formcss"
                                enctype="multipart/form-data">
                                <s:select label="Select PayCycle" name="paycycle" listKey="paycycleId"
                                    listValue="paycycleName"  headerValue="Select Paycycle"
                                    onchange="document.frm.submit();"
                                    list="payCycleList" key="" />
                                <s:select theme="simple" name="f_org" listKey="orgId" 
                                    listValue="orgName" headerKey="-1" headerValue="All Organisations" 
                                    onchange="document.frm.submit();"
                                    list="orgList" key="" />
                                <s:select theme="simple" name="wLocation" listKey="wLocationId" 
                                    listValue="wLocationName" headerKey="-1" headerValue="All Locations" 
                                    onchange="document.frm.submit();"
                                    list="wLocationList" key="" />
                                <s:select name="f_department" list="departmentList" listKey="deptId"  
                                    listValue="deptName" headerKey="0" headerValue="All Departments"
                                    onchange="document.frm.submit();" 
                                    ></s:select>
                            </s:form>
                        </div>
                        <table class="table" width="100%" id="lt">
                            <thead>
                                <tr>
                                    <th width="10%">Employee Code</th>
                                    <th width="20%">Employee Name</th>
                                    <th width="20%">Break Type</th>
                                    <th width="10%">Date</th>
                                    <th width="10%">Taken Paid</th>
                                    <th width="10%">Taken Unpaid</th>
                                    <th width="10%">Op. Balance</th>
                                    <th width="10%">Cl. Balance</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    int i = 0;
                                    	for (i = 0; reportListPrint!=null && i < reportListPrint.size(); i++) {
                                    		List alInner = (List) reportListPrint.get(i);
                                    %>
                                <tr>
                                    <td><%=alInner.get(0)%></td>
                                    <td><%=alInner.get(1)%></td>
                                    <td>
                                        <div style="padding:0 5px;background-color: <%=alInner.get(2)%>"><%=alInner.get(3)%></div>
                                    </td>
                                    <td class="alignRight padRight20"><%=alInner.get(4)%></td>
                                    <td class="alignRight padRight20"><%=alInner.get(5)%></td>
                                    <td class="alignRight padRight20"><%=alInner.get(6)%></td>
                                    <td class="alignRight padRight20"><%=alInner.get(7)%></td>
                                    <td class="alignRight padRight20"><%=alInner.get(8)%></td>
                                </tr>
                                <%
                                    }if(i==0){
                                    %>
                                <tr>
                                    <td colspan="9">
                                        <div class="msg nodata"><span>No leave details available</span></div>
                                    </td>
                                </tr>
                                <%
                                    }
                                    %>
                            </tbody>
                        </table>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>