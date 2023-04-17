<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<link rel="stylesheet" href="<%= request.getContextPath()%>/css/style-iphone.css" type="text/css" media="screen" charset="utf-8" />

<script src="<%= request.getContextPath()%>/scripts/jquery.1.9.0.min.js" type="text/javascript"></script>
<script src="<%= request.getContextPath()%>/scripts/iphone-style-checkboxes.js" type="text/javascript" charset="utf-8"></script>

<script type="text/javascript" charset="utf-8">
			$(document).ready( function () {
				
				
				
					var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
					var sbUserTypeList = '<%= ((String)request.getAttribute("sbUserTypeList")) %>';
					var sbEmpCodeList = '<%= ((String)request.getAttribute("sbEmpCodeList")) %>';
					var sbUserStatusList = '<%= ((String)request.getAttribute("sbUserStatusList")) %>';
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
			                           			sAddURL: "AddUser.action?operation=A",
												sDeleteURL: "AddUser.action?operation=D",
												"aoColumns": [
		                    									null,
		                    									null,
		                    									/* {
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddUser.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',		                                                         	
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddUser.action?operation=U"
		                    									} */
		                    									null,
		                    									null,
		                    									,
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                                                         	data: sbUserTypeList,
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddUser.action?operation=U"
		                    									},
		                    									
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
																	data: sbUserStatusList,
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddUser.action?operation=U"
		                    									},
		                    									null,
		                    									null,
		                    									/* {
		                    										tooltip: 'Click to View/Edit ACL'
		                    									} */
		                    									null
															],
												oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}}, 
												oAddNewRowButtonOptions: {label: "Add...", icons: {primary:'ui-icon-plus'}},
												sAddDeleteToolbarSelector: ".dataTables_length" ,		
												oAddNewRowFormOptions: { 	
	                                                    title: 'Add a new User',
														show: "blind", 
														width: '700px',
														modal: true,
														hide: "explode"
												}
												}); 
							}
							
							else {
									$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" })
								} 
					
					
					
							});
			
			
			function changeStatus(val){
				
				getContent('biometric'+val,'UserAccessFileCreation.action?value='+val);
			}
		</script>
		
		
  <script type="text/javascript" charset="utf-8">
    $(window).load(function() {
    	 var onchange_checkbox = ($('.on_off :checkbox')).iphoneStyle({
    	        onChange: function(elem, value) { 
    	         
    	          getContent('msg_status','UserAccessFileCreation.action?value='+elem.val()+'&flag='+value);
    	        }
    	      });
      
    });

  </script>
	</head>

 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="User Access" name="title"/>
</jsp:include>
 

    <div id="printDiv" class="leftbox reportWidth">
         
         
         
         
<s:form name="frmUsers" action="UserAccess" theme="simple">
<div class="filter_div" style="float:none">

<div class="filter_caption">Filter</div>

<s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;"
                         listValue="orgName" 
                         onchange="document.frmUsers.submit();"
                         list="organisationList" key=""  />

    <s:select theme="simple" name="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         list="wLocationList" key=""  />
                    
    <s:select name="f_department" list="departmentList" listKey="deptId" cssStyle="float:left;margin-right: 10px;"
    			listValue="deptName" headerKey="0" headerValue="All Departments" 
    			></s:select>
    
    <s:select name="f_service" list="serviceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
    			listValue="serviceName" headerKey="0" headerValue="All Services" 
    			></s:select>
    			
    <s:select theme="simple" name="f_level" listKey="levelId" headerValue="All Levels"  cssStyle="float:left;margin-right: 10px;"
	                            listValue="levelCodeName" headerKey="0" 
	                            list="levelList" key="" required="true" />
     
       
    
    <s:submit value="Submit" cssClass="input_button"  cssStyle="margin:0px"/>

    </div>
</s:form>
       
<!-- Place holder where add and delete buttons will be generated -->
<div id="msg_status" style="text-align:center;width:700px"></div>
<div class="add_delete_toolbar"></div>
<% java.util.List<java.util.List<String>> couterlist = (java.util.List<java.util.List<String>>)request.getAttribute("reportList");
	Map<String, Map<String, String>> hmWorkLocation =(Map<String, Map<String, String>> )request.getAttribute("hmWorkLocation");
	UtilityFunctions uF = new UtilityFunctions();
	%>
<table class="display tb_style" id="lt" >
	<thead>
		<tr>
			
			<th style="text-align: left;">Emp Code</th>
			<th style="text-align: left;">Emp Name</th>
			<th style="text-align: left;">Work Location</th>
			<th style="text-align: left;">BioMetric Access</th>
			<%Set<String> keys=hmWorkLocation.keySet();
				Iterator<String> it=keys.iterator();
				while(it.hasNext()){
					String key=it.next();
					Map<String, String> innerWLocationMp=hmWorkLocation.get(key);%>
					
					<th style="text-align: left;"><%=innerWLocationMp.get("WL_NAME") %></th>
				<%}
			%>
		</tr>
	</thead>
	<tbody>
	
	 <% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
	 <% java.util.List<String> cinnerlist = couterlist.get(i);
	 	String biometrix_access=cinnerlist.get(7);
	 %>
		<tr id = <%= cinnerlist.get(0) %> >
		
			<td><%=  cinnerlist.get(2) %></td>
			<td><%=  cinnerlist.get(3) %></td>
			<td><%=  cinnerlist.get(4) %></td>
			<td><div id="biometric<%=cinnerlist.get(8) %>"><%=  cinnerlist.get(5) %></div></td>
			<%Iterator<String> it1=keys.iterator();
			
				while(it1.hasNext()){
					String key1=it1.next();
					Map<String, String> innerWLocationMp=hmWorkLocation.get(key1);
					String boi=innerWLocationMp.get("WL_BOIMETRIC_INFO"); %>
					<td >
					<%if(boi!=null){
						String[] bioArr=boi.split(",");
						for(String ele:bioArr){
							String[] eleArr=ele.split("::");
				%>
					
					<%if(uF.parseToBoolean(cinnerlist.get(6))){ %>
					<div class="on_off" style="float:left;margin:8px;">
					<div><%=eleArr[1] %></div>
					<%if(biometrix_access!=null){
						String[] aa=biometrix_access.split(",");
						boolean f=false;
						for(String a:aa){
							String[] bb=a.split("=");
							if(uF.parseToInt(bb[0])==uF.parseToInt(eleArr[0])){
								
								if(uF.parseToBoolean(bb[1]))
								f=true;
							}
							
						}
						
						if(f){%>
							 <div><input type="checkbox" id="on_off<%=eleArr[0]+"_"+cinnerlist.get(1) %>" checked="checked" value="<%=eleArr[0]+"_"+cinnerlist.get(1) %>" /></div>
						<%}else{%>
							 <div><input type="checkbox" id="on_off<%=eleArr[0]+"_"+cinnerlist.get(1) %>" value="<%=eleArr[0]+"_"+cinnerlist.get(1) %>" /></div>
						<%}
						%>
						
					
					<%}else{ %>
					
					
       <div> <input type="checkbox" id="on_off<%=eleArr[0]+"_"+cinnerlist.get(1) %>" value="<%=eleArr[0]+"_"+cinnerlist.get(1) %>" /></div>
        <%} %>
        
      </div>

<%} %>


				<%}} %>
				</td>
			<%	}
			%>
			
			
				
		</tr>
		<% } %>
	</tbody>
</table>

</div>
