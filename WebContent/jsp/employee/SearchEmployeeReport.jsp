<%@page import="java.io.File"%>
<%@page import="com.konnect.jpms.util.EncryptionUtility"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<style>
.factsheet-link a{
color: #fff;
top: 10px;
}
.nav-stacked{

height: 230px;
padding:5px;
overflow :auto;
}
.nav-stacked li{
padding: 5px;
}

.nav-stacked li a {
    padding: 0px 0px;
}
</style>
<script>
function submitForm() {
	var strSearch = document.getElementById("strSearchJob").value;
	//alert("strSearch ===>> " + strSearch);
	var divResult = "changeViewDiv";
	var fromPage = '<%=(String)request.getAttribute("fromPage") %>';
	if(fromPage != null && (fromPage == 'TS' || fromPage == 'COMMUNICATION')) {
	 divResult = "divResult";
	}
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type: 'GET',
		url: 'SearchEmployee.action?strSearchJob=' + strSearch + '&fromPage=' + fromPage,
		cache: true,
		success: function(result) {
			$("#"+divResult).html(result);
	  	}
	});
  }
</script>
<%
UtilityFunctions uF = new UtilityFunctions();
/* EncryptionUtility eU = new EncryptionUtility(); */

String sbData = (String) request.getAttribute("sbData");
String strSearchJob = (String) request.getAttribute("strSearchJob"); 

String fromPage = (String)request.getAttribute("fromPage");
String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
//System.out.println("strSearchJob=>"+strSearchJob);

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
List alReport = (List)request.getAttribute("SEARCH_EMP");
if(alReport==null)  alReport=new ArrayList();
%>

<% if(fromPage !=null && fromPage.equalsIgnoreCase("COMMUNICATION")) { %>
	<div class="leftbox reportWidth">
        <div class="col-lg-12 col-md-12 col-sm-12" style="bottom: 20px; text-align: right;">
             <div style="float: left;line-height: 22px; width: 514px; margin-left: 50px;">
                 <div style="margin: 0px 0px 0px 16px; float: left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
                     <div style="float: left">
                         <input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search People" style="margin-left: 0px; width: 350px !important; max-width: 350px !important; box-shadow: 0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>" />
                     </div>
                     <div style="float: right">
                         <input type="button" value="Search" class="btn btn-primary" name="submit" onclick="submitForm();" style="margin-left: 10px;"/>
                     </div>
                 </div>
             </div>
             <script>
                 $( "#strSearchJob" ).autocomplete({
                 	source: [ <%=uF.showData(sbData,"") %> ]
                 });
             </script>
        </div>
        <!-- <div class="row row_without_margin clr" style=" overflow-y: auto; height: 600px;"> -->
      <%
		int i=0;
      	for(i=0; i<alReport.size(); i++) {
      		List alInner = (List)alReport.get(i);
   		%>
      			<div class="col-lg-6 col-md-6 col-sm-12">
      				 <div class="box box-widget widget-user-2">
			            <div class="widget-user-header" style="max-height: 110px; min-height: 105px;">
			            	<div class="factsheet-link pull-right" style="margin-right: 15px;"><%=uF.showData((String)alInner.get(9),"") %></div>
			              <div class="widget-user-image" style="left: 0%;margin-left: 10px;top: 10px !important;">
			              	<%if(CF.getStrDocRetriveLocation() == null) { %>
			              	<%-- <img class="lazy img-circle" style="height: 65px !important;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String)alInner.get(0) %>"> --%>
			              	<%
								File file = new File(IConstants.DOCUMENT_LOCATION + (String)alInner.get(0));
								boolean existFile = false;
								if(file.exists()){
									existFile = true;
								}
							%>
								<%if(existFile){ %>
									<img class="lazy img-circle" style="height: 65px !important;" src="<%=IConstants.DOCUMENT_LOCATION + (String)alInner.get(0) %>" data-original="<%=IConstants.DOCUMENT_LOCATION + (String)alInner.get(0) %>">
								<%} else{ %>
									<img class="lazy img-circle" style="height: 65px !important;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String)alInner.get(0) %>">
								<%} %>
			              	<% } else { %>
			              	<%-- <img class="lazy img-circle" style="height: 65px !important;" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0) %>" > --%>
			              	<%
								File file = new File(CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0));
								boolean existFile = false;
								if(file.exists()){
									existFile = true;
								}
							%>
								<%if(existFile){ %>
									<img class="lazy img-circle" style="height: 65px !important;" src="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0) %>" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0) %>" >
								<%} else{ %>
									<img class="lazy img-circle" style="height: 65px !important;" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0) %>" >
								<% } %>
			              	<% } %>
			              </div>
			              <h3 class="widget-user-username" title="<%=(String)alInner.get(1) %> <%=(String)alInner.get(2) %> <%=(String)alInner.get(3) %>"><%=(String)alInner.get(1) %> <%=(String)alInner.get(2) %> <%=(String)alInner.get(3) %></h3>
			              <h3 class="widget-user-username" title="<%=alInner.get(17)%>"><%=alInner.get(17)%></h3>
			              <h5 class="widget-user-desc" title="<%=uF.showData((String)alInner.get(4),"") %>"><%=uF.showData((String)alInner.get(4),"") %></h5>
			              <h5 class="widget-user-desc" title="<%=uF.showData((String)alInner.get(6),"") %> [<%=uF.showData((String)alInner.get(13),"") %>]"><%=uF.showData((String)alInner.get(6),"") %>[<%=uF.showData((String)alInner.get(13),"") %>]</h5>
			            </div>
			            
			            <div class="box-footer no-padding">
			              <ul class="nav nav-stacked" > 
			                 <li style="float: left; width: 100%;"><a href="javascript:void(0)">Date of Joining: <span class="pull-right badge bg-blue"><%=alInner.get(15)%></span></a></li>
						     <li style="float: left; width: 100%;"><a href="javascript:void(0)">Reporting Manager: <span class="pull-right"><%=alInner.get(14)%></span></a></li>
			              	 <li style="float: left; width: 100%;"><a href="javascript:void(0)">Status: <span class="pull-right"><%=uF.showData((String)alInner.get(12),"") %></span></a></li>
			              	 <li style="float: left; width: 100%;"><a href="javascript:void(0)">Employment Type: <span class="pull-right"><%=uF.showData((String)alInner.get(16),"-") %></span></a></li>
			                 <li style="float: left; width: 100%;"><a href="javascript:void(0)">Contact no.: <span class="pull-right"><%=uF.showData((String)alInner.get(5),"-") %></span></a></li>
			                 <li style="float: left; width: 100%;"><a href="javascript:void(0)"><%=uF.showData((String)alInner.get(8),"-") %></a></li>
			                 <% if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.ADMIN)) { %>
			                 	<li style="float: left; width: 100%;" class="alignCenter"><a href="EmployeeActivity.action?strEmpId=<%=(String)alInner.get(10) %>" style="color: #3c8dbc;"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Activity</a></li>
			                 <% } %>
			              </ul>
			              </div>
		            </div> 
		           </div>
      		<% }
      		if(i==0) {
    	  %>
    	  <div class="nodata msg"><span>Sorry, No matching employee found.</span></div>
    	  <% } %>
	 </div>
<% } else { %>
    <div class="leftbox reportWidth">
        <div class="col-lg-12 col-md-12 col-sm-12" style="bottom: 20px; margin-top: -30px; text-align: right;">
             <div style="float: left;line-height: 22px; width: 514px; margin-left: 350px;">
                 <span style="float: left; display: block; width: 78px;">Search:</span>
                 <div style="margin: 0px 0px 0px 16px; float: left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
                     <div style="float: left">
                         <input type="text" id="strSearchJob" class="form-control" name="strSearchJob" style="margin-left: 0px; width: 250px; box-shadow: 0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>" />
                     </div>
                     <div style="float: right">
                         <input type="button" value="Search" class="btn btn-primary" name="submit" onclick="submitForm();" style="margin-left: 10px;"/>
                     </div>
                 </div>
             </div>
             <script>
                 $( "#strSearchJob" ).autocomplete({
                 	source: [ <%=uF.showData(sbData,"") %> ]
                 });
             </script>
        </div>
        <div class="row row_without_margin clr" style=" overflow-y: auto; height: 600px;">
      <%
      int i=0;
      	for(i=0; i<alReport.size(); i++) {
      		List alInner = (List)alReport.get(i);
      		
      		%>
      			<div class="col-lg-3 col-md-6 col-sm-12">
      				 <div class="box box-widget widget-user-2">
			            <div class="widget-user-header" style="max-height: 110px; min-height: 105px;">
			              <div class="widget-user-image">
			              	<%if(CF.getStrDocRetriveLocation() == null) { %>
			              	<%-- <img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String)alInner.get(0) %>"> --%>
			              		<%
			              		File file = new File(IConstants.DOCUMENT_LOCATION + (String)alInner.get(0));
									boolean existFile = false;
									if(file.exists()){
										existFile = true;
									}
								%>
								<%if(existFile){ %>
									<img class="lazy img-circle" src="<%=IConstants.DOCUMENT_LOCATION + (String)alInner.get(0) %>" data-original="<%=IConstants.DOCUMENT_LOCATION + (String)alInner.get(0) %>">
								<%} else{ %>
									<img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + (String)alInner.get(0) %>">
								<%} %>
			              	
			              	<% } else { %>
			              	<%-- <img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0) %>" > --%>
			              		<%
									File file = new File(CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0));
									boolean existFile = false;
									if(file.exists()){
										existFile = true;
									}
								%>
								<%if(existFile){ %>
									<img class="lazy img-circle" src="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0) %>" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0) %>" >
								<%} else{ %>
									<img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String)alInner.get(10)+"/"+IConstants.I_60x60+"/"+(String)alInner.get(0) %>" >
								<%} %>
			              	
			              	<% } %>
			              </div>
			              <h3 class="widget-user-username" title="<%=(String)alInner.get(1) %> <%=(String)alInner.get(2) %> <%=(String)alInner.get(3) %>"><%=(String)alInner.get(1) %> <%=(String)alInner.get(2) %> <%=(String)alInner.get(3) %></h3>
			              <h3 class="widget-user-username" title="<%=alInner.get(17)%>"><%=alInner.get(17)%></h3>
			              <h5 class="widget-user-desc" title="<%=uF.showData((String)alInner.get(4),"") %>"><%=uF.showData((String)alInner.get(4),"") %></h5>
			              <h5 class="widget-user-desc" title="<%=uF.showData((String)alInner.get(6),"") %> [<%=uF.showData((String)alInner.get(13),"") %>]"><%=uF.showData((String)alInner.get(6),"") %>[<%=uF.showData((String)alInner.get(13),"") %>]</h5>
			              <div class="factsheet-link pull-right" style="margin-right: 20px;"><%=uF.showData((String)alInner.get(9),"") %></div>
			            </div>
			            <div class="box-footer no-padding">
			              <ul class="nav nav-stacked" > 
			                 <li style="float: left; width: 100%;"><a href="javascript:void(0)">Date of Joining: <span class="pull-right badge bg-blue"><%=alInner.get(15)%></span></a></li>
						     <li style="float: left; width: 100%;"><a href="javascript:void(0)">Reporting Manager: <span class="pull-right"><%=alInner.get(14)%></span></a></li>
			              	 <li style="float: left; width: 100%;"><a href="javascript:void(0)">Status: <span class="pull-right"><%=uF.showData((String)alInner.get(12),"") %></span></a></li>
			              	 <li style="float: left; width: 100%;"><a href="javascript:void(0)">Employment Type: <span class="pull-right"><%=uF.showData((String)alInner.get(16),"-") %></span></a></li>
			                 <li style="float: left; width: 100%;"><a href="javascript:void(0)">Contact no.: <span class="pull-right"><%=uF.showData((String)alInner.get(5),"-") %></span></a></li>
			                 <li style="float: left; width: 100%;"><a href="javascript:void(0)"><%=uF.showData((String)alInner.get(8),"-") %></a></li>
			                 <% if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.ADMIN)) { %>
			                 	<li style="float: left; width: 100%;" class="alignCenter"><a href="EmployeeActivity.action?strEmpId=<%=(String)alInner.get(10) %>" style="color: #3c8dbc;"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Activity</a></li>
			                 <% } %>
			              </ul>
			              </div>
		            </div> 
		           </div>
      		<% }
      		if(i==0) {
    	  %>
    	  <div class="nodata msg"><span>Sorry, No matching employee found.</span></div>
    	  <% } %>
      </div>
	 </div>
	 <% } %>
<script>
$("img.lazy").lazyload({event : "sporty", threshold : 200, effect : "fadeIn", failure_limit : 10});
$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
}); 
</script>