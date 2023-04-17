<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">
  
    $(document).ready(function() {

        $('a.poplight[href^=#]').click(function() {
            var popID = $(this).attr('rel'); //Get Popup Name
            var popURL = $(this).attr('href'); //Get Popup href to define size

            //Pull Query & Variables from href URL
            var query= popURL.split('?');
            var dim= query[1].split('&');
            var popWidth = dim[0].split('=')[1]; //Gets the first query string value

            //Fade in the Popup and add close button
            $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

            //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
            var popMargTop = ($('#' + popID).height() + 80) / 2;
            var popMargLeft = ($('#' + popID).width() + 80) / 2;

            //Apply Margin to Popup
            $('#' + popID).css({
                'margin-top' : -popMargTop,
                'margin-left' : -popMargLeft
            });

            //Fade in Background
            $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
            $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies

            return false;
        });

        //Close Popups and Fade Layer
        $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
            $('#fade , .popup_block').fadeOut(function() {
                $('#fade, a.close').remove();  //fade them both out
            });
            return false;
        });

    });

function show_empList() {
	dojo.event.topic.publish("show_empList");
}

</script>

<script type="text/javascript" charset="utf-8">

			$(document).ready( function () {
				var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
				<%-- var sbUserTypeList = '<%= ((String)request.getAttribute("sbUserTypeList")) %>'; --%>
				var sbLevelTypeList = '<%= ((String)request.getAttribute("sbLevelTypeList")) %>';
				var sbEmpLeaveTypeList = '<%= ((String)request.getAttribute("sbEmpLeaveTypeList")) %>';
				var sbApprovalList = '<%= ((String)request.getAttribute("sbApprovalList")) %>';
				if (usertype == '<%=IConstants.ADMIN%>' 
					|| usertype == '<%=IConstants.CEO%>' || usertype == '<%=IConstants.CFO%>'
						|| usertype == '<%=IConstants.ACCOUNTANT%>' || usertype == '<%=IConstants.HRMANAGER%>'
							|| usertype == '<%=IConstants.MANAGER%>') {
					
							$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers",
								
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
								
							}).makeEditable({
			                           			sAddURL: "EmployeeIssueLeave.action?operation=A",
												sDeleteURL: "EmployeeIssueLeave.action?operation=D",
												"aoColumns": [
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                    										data: sbEmpLeaveTypeList,
		                                                         	submit:'save',
	                                                         		sUpdateURL: "EmployeeIssueLeave.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                    										data: sbLevelTypeList,
		                                                         	submit:'save',
	                                                         		sUpdateURL: "EmployeeIssueLeave.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'save',
	                                                         		sUpdateURL: "EmployeeIssueLeave.action?operation=U"
		                    									},
		                    									/* {
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'save',
		                                                         	type: 'select',
		                                                         	data: sbApprovalList,
	                                                         		sUpdateURL: "EmployeeIssueLeave.action?operation=U"
		                    									}, */
		                    									null,
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'save',
		                                                         	type: 'select',
		                                                         	data:"{'True':'Yes','False':'No'}",
	                                                         		sUpdateURL: "EmployeeIssueLeave.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'save',
		                                                         	type: 'select',
		                                                         	data:"{'True':'Yes','False':'No'}",
	                                                         		sUpdateURL: "EmployeeIssueLeave.action?operation=U"
		                    									},
		                    									null,
															],
												oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}}, 
												oAddNewRowButtonOptions: {label: "Add...", icons: {primary:'ui-icon-plus'}},
												sAddDeleteToolbarSelector: ".dataTables_length" ,		
												oAddNewRowFormOptions: {
	                                                    title: 'Add a new leave policy',
	                                                    show: "blind", 
														width: '700px',
														modal: true,
														hide: "explode"
												},
												fnOnAdded: function(status)
												{
													window.location.reload( true );
												},
												fnOnEdited: function(status)
												{
													window.location.reload( true );
												}
											});
									}else {
										$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" })
									} 
			});
			
</script>

<!-- Custom form for adding new records -->
<%
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO)
				|| strUserType.equalsIgnoreCase(IConstants.CFO)  || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){
%>
<jsp:include page="../leave/EmployeeIssueLeave.jsp" flush="true" />
<%} %>

 <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Employee Issue Leave List" name="title"/>
</jsp:include>

	
	<div id="printDiv" class="leftbox reportWidth">
		<table class="display" id="lt">
			<thead>
				<tr>
					<th style="text-align: left;">Leave Type</th>
					<th style="text-align: left;">Level Type</th>
					<th style="text-align: left;">No of Leaves</th>
					<th style="text-align: left;">Leave Calculation</th>
					<th style="text-align: left;">Paid/Unpaid</th>
					<th style="text-align: left;">Carry Forward?</th>
					<th style="text-align: left;">Entry Date</th>
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
				</tr>
				<% } %>
			</tbody>
		</table> 
	
		<%
	 		System.out.println(request.getAttribute("empLeaveTypes"));
		
			if (request.getAttribute("empLeaveTypes") != null) {
				out.println(request.getAttribute("empLeaveTypes"));
	
			}
		%>
		</div>
		
