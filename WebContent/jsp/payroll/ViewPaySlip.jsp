<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">

<% String strUserType = (String) session.getAttribute(IConstants.USERTYPE); %>

<%-- <script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/pdfmake.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/vfs_fonts.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.print.min.js"></script>  --%>

<script type="text/javascript" src="DataTableJs/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="DataTableJs/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="DataTableJs/jszip.min.js"></script>
 <script type="text/javascript" src="DataTableJs/pdfmake.min.js"></script>
 <script type="text/javascript" src="DataTableJs/vfs_fonts.js"></script>
 <script type="text/javascript" src="DataTableJs/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="DataTableJs/buttons.print.min.js"></script> 
 <script type="text/javascript" src="js_bootstrap/datatables/dataTables.bootstrap.js"></script> 

<script type="text/javascript">
	$(document).ready(function() {
		$('#lt1').DataTable({
			"order": [],
			"columnDefs": [ {
			      "targets"  : 'no-sort',
			      "orderable": false
			    }],
			'dom': 'lBfrtip',
	       /*  'buttons': [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ] */
	        buttons: [
	                  'copy',
	                  {
	                      extend: 'csv',
	                      title: 'Consolidated Payslips'
	                  },
	                  {
	                      extend: 'excel',
	                      title: 'Consolidated Payslips'
	                  },
	                  {
	                      extend: 'pdf',
	                      title: 'Consolidated Payslips'
	                  },
	                  {
	                      extend: 'print',
	                      title: 'Consolidated Payslips'
	                  }
	              ]
	  	});
	});
	 
	function submitForm(type){
		var strMonth = "";
		if(document.getElementById("strMonth")) {
			strMonth = document.getElementById("strMonth").value;
		}
		var financialYear = document.getElementById("financialYear").value;
		
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ViewPaySlips.action?financialYear='+financialYear+'&strMonth='+strMonth,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	//console.log(result);
	        	$("#divResult").html(result);
	   		}
		});
	}
	
</script>

<% String strTitle = ((session.getAttribute(IConstants.USERTYPE) != null && !((String) session.getAttribute(IConstants.USERTYPE)).equalsIgnoreCase(IConstants.EMPLOYEE)) ? "Staff Compensation" : "My Compensation"); %>
<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
				<s:form name="frm_ViewPaySlips" action="ViewPaySlips" theme="simple">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
							<p style="padding-left: 5px;">Financial Year</p>
							<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" onchange="submitForm('2');" list="financialYearList" key="" cssStyle="width:200px;"/>
						</div>
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
							<p style="padding-left: 5px;">Month</p>
							<s:select label="Select a month" name="strMonth" id="strMonth" list="#{'1':'January', '2':'February', '3':'March', '4':'April', '5':'May', '6':'June', '7':'July', '8':'August', '9':'September', '10':'October', '11':'November', '12':'December'}" cssClass="inline" cssStyle="margin-left: 5px;"/>
						</div>
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5 ">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
						</div>
					</div>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>
		
		<display:table name="reportList" class="table table-hover table-bordered" id="lt1">
			<%-- <display:setProperty name="export.excel.filename" value="ViewPaySlips.xls" />
			<display:setProperty name="export.xml.filename" value="ViewPaySlips.xml" />
			<display:setProperty name="export.csv.filename" value="ViewPaySlips.csv" /> --%>
			<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
			<display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
			<display:column style="text-align:center;" valign="top" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			<display:column style="text-align:center;" valign="top" title="Pan No"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Organization"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
			<% } %>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Pay Date"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
			<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Amount Paid"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
			<%-- <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Action"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column> --%>
		</display:table>
	</div>
	<!-- /.box-body -->
</div>

