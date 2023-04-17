<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
 
<%-- <script type="text/javascript" charset="utf-8">
			$(document).ready( function () {
						var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
						if (usertype == '<%=IConstants.ADMIN%>' 
							|| usertype == '<%=IConstants.CEO%>' || usertype == '<%=IConstants.CFO%>'
								|| usertype == '<%=IConstants.ACCOUNTANT%>' || usertype == '<%=IConstants.HRMANAGER%>'
									|| usertype == '<%=IConstants.MANAGER%>') {
							$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers",
								"aaSorting": [],
								"sDom": '<"H"Tf>rt<"F"ip>',
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
			                           			sAddURL: "AddBank.action?operation=A",
												sDeleteURL: "AddBank.action?operation=D",
												"aoColumns": [
		                     									{
		                     										cssclass:"required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit',
		                                                         	submit:'save',
	                                                         		sUpdateURL: "AddBank.action?operation=U"
		                    									},
		                    									{
		                     										cssclass:"required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit',
		                                                         	submit:'save',
	                                                         		sUpdateURL: "AddBank.action?operation=U"
		                    									},
		                    									{
		                     										cssclass:"required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit',
		                                                         	submit:'save',
	                                                         		sUpdateURL: "AddBank.action?operation=U"
		                    									},
															],
												oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}}, 
												oAddNewRowButtonOptions: {label: "Add...", icons: {primary:'ui-icon-plus'}},
												sAddDeleteToolbarSelector: ".dataTables_length" ,		
												oAddNewRowFormOptions: {	
	                                                    title: 'Add a new Bank',
														show: "blind",
														width: '700px',
														modal: true,
														hide: "explode" 
														}
											}); 
						}else {
							$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" })
						} 
			});
			
</script>

<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<!-- Custom form for adding new records -->
<%
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.CFO))){ 
%>
<jsp:include page="../../master/AddBank.jsp" flush="true" />
<%} %>

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Bank List" name="title"/>
</jsp:include>


    <div id="printDiv" class="leftbox reportWidth">
        <table class="display" id="lt">
			<thead>
				<tr>
					<th>Bank Code</th>
					<th>Bank Name</th>
					<th>Bank Description</th>
					<th>Bank Address</th>
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
				</tr>
				<% } %>
			</tbody>
		</table> 

    </div>
   
    --%>
   

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Banks" name="title"/>
</jsp:include>

<div id="printDiv" class="leftbox reportWidth">

		
<%
UtilityFunctions uF = new UtilityFunctions();
Map hmBankReport = (Map)request.getAttribute("hmBankReport");
Map hmBankReport1 = (Map)request.getAttribute("hmBankReport1");

%>
		
		
		
		
		
	<%-- 	
		
<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>		
<div style="float:left; margin:0px 0px 10px 0px"> <a href="AddBank.action" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"> + Add New Bank</a></div>
<%} %>
<div class="clr"></div>

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
<div>
         <ul class="level_list">

		
		<% 
			Set setBankMap = hmBankReport.keySet();
			Iterator it = setBankMap.iterator();
			
			while(it.hasNext()){
				String strBankId = (String)it.next();
				
					
					List alBank = (List)hmBankReport.get(strBankId);
					if(alBank==null)alBank=new ArrayList();
					%>
					
					<li>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %>
					<a href="AddBank.action?operation=D&ID=<%=strBankId%>" class="del" onclick="return confirm('Are you sure you wish to delete this cost-center?')"> - </a>
					<%} %>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %> 
					<a href="AddBank.action?operation=E&ID=<%=strBankId%>" class="edit_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })">Edit</a>
					<%} %> 
					
					<strong><%=alBank.get(1)%></strong> [<strong><%=alBank.get(2)%></strong>]&nbsp;&nbsp;&nbsp;
					<%=alBank.get(3)%> <%=alBank.get(4)%> <%=alBank.get(5)%>, <%=alBank.get(6)%> (<strong><%=alBank.get(7)%></strong>)
					
					</li> 
		<%
			}
		%>
		 
		 </ul>
         
     </div>	
		
	 --%>
   


<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>		
<div style="float:left; margin:0px 0px 10px 0px"> <a href="AddBank.action" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"> + Add New Bank</a></div>
<%} %>  
<div class="clr"></div>

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
<div>
         <ul class="level_list">

		
		<% 
			Set setLevelMap = hmBankReport.keySet();
			Iterator it1 = setLevelMap.iterator();
			
			while(it1.hasNext()){
				String strOfficeTypeId = (String)it1.next();
				List alOfficeType = (List)hmBankReport.get(strOfficeTypeId);
				if(alOfficeType==null)alOfficeType=new ArrayList();
				
					
					List alOfficeLocation = (List)hmBankReport1.get(strOfficeTypeId);
					if(alOfficeLocation==null)alOfficeLocation=new ArrayList();
					%>
					
					<li>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %>
					<a href="AddBank.action?operation=D&ID=<%=strOfficeTypeId%>" class="del" onclick="return confirm('Are you sure you wish to delete this branch?')"> - </a>
					<%} %>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %>
					 <a href="AddBank.action?operation=E&ID=<%=strOfficeTypeId%>" class="edit_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax' })">Edit</a> 
					 <%} %>
					 <strong><%=alOfficeType.get(2)%> [<%=alOfficeType.get(1)%>]</strong>
					<ul>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>		
					<li class="addnew desgn"><a href="AddBank.action?param=<%=strOfficeTypeId %>" onclick="return hs.htmlExpand(this, { objectType: 'ajax', width:700 })"> + Add New Branch</a></li>
					<%} %>
					
					<%
						for(int d=0; d<alOfficeLocation.size(); d+=20){
						String strOfficeId = (String)alOfficeLocation.get(d);
						
					%>
					
					<li> 
					<%if(!uF.parseToBoolean((String)alOfficeLocation.get(7))) { %>
						<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %>
						  	<a href="AddBank.action?operation=D&ID=<%=strOfficeId%>" class="del" onclick="return confirm('Are you sure you wish to delete this location?')"> - </a>
	                    <%} %>
	                    <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %> 
	                    <a href="javascript:void(0)" class="edit_lvl" onclick="editWLocation(<%=strOfficeTypeId%>, <%=strOfficeId%>)">Edit</a>
	                    <%} %>
                    <% } %>
                     <strong><%=alOfficeLocation.get(d+1)%> <%=alOfficeLocation.get(d+2)%> <%=alOfficeLocation.get(d+3)%> <%=alOfficeLocation.get(d+4)%> <%=alOfficeLocation.get(d+5)%></strong>  
                      
                      
                    </li>
						
				<%
					}
				%>		
					
                 
                 </ul>
                 </li> 
		<%
			}
		%>
		 
		 </ul>
         
     </div>	

	</div>
   