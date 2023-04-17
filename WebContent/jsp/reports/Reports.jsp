<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
	Map<String,String>hmParentNavigation =(Map<String,String>)request.getAttribute("hmParentNavigation");
	Map<String,String>hmParentLable=(Map<String,String>)request.getAttribute("hmParentLable");
	Map<String,Map<String,List<String>>>hmParentChild=(Map<String,Map<String,List<String>>>)request.getAttribute("hmParentChild");
	
%>

<section style="margin-top: 20px;">

<% if(hmParentLable!=null && hmParentLable.size()>0 && hmParentNavigation!=null && hmParentNavigation.size()>0){
	Iterator<String> it=hmParentLable.keySet().iterator();
	while(it.hasNext()){
		String parent=it.next();
		 %>
	
	<div class="col-lg-4 col-md-4 col-sm-12 paddingright5">	
  	 	<div class="box box-body;box box-info"  style="min-height: 500px !important; max-height: 500px !important; overflow-y: auto;">
   		 	<div class="box-header with-border" data-widget="collapse-full"><p><b><%=hmParentLable.get(parent)%></b></p></div>
   		 		<%
   		 		if(hmParentChild!=null && hmParentChild.size()>0){
   				Map<String,List<String>>hmChild=hmParentChild.get(parent);
   					if(hmChild!=null && hmChild.size()>0){
   						Iterator<String> it1=hmChild.keySet().iterator();
   						while(it1.hasNext()) {
						String navigationID=it1.next();
						List<String>alLabelAction=hmChild.get(navigationID);%> 
			<ul class="products-list product-list-in-box">
				<li class="item"> 
				
					<span style="padding-left:8px"">
						<a href="<%=hmParentNavigation.get(parent)+"&strNavigationId="+navigationID%>"><%=alLabelAction.get(1)%></a> 
			 		</span>
			 	
			 	</li>
			 </ul>
			 <%} } } %>
			
   	 	</div>
    </div>
	
<%}}%>
	
 </section>  
    