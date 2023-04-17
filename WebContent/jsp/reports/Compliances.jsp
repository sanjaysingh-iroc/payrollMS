<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
String ParentLabel=(String)request.getAttribute("ParentLabel");
Map<String,List<String>>hmChildActionLabel=(Map<String,List<String>>)request.getAttribute("hmChildActionLabel");
%>
<section style="margin-top: 20px;">
<div class="col-lg-12">
	<div  class="col-lg-4 col-md-4 col-sm-12 paddingright5">
		<div class="box box-body"><%= ParentLabel%></div>
	</div>
</div>
<br>	
 <%if(hmChildActionLabel!=null && hmChildActionLabel.size()>0){
	Iterator<String> it=hmChildActionLabel.keySet().iterator();
	while(it.hasNext()){
		String navigationId=it.next();
		List<String>alActionLabel=hmChildActionLabel.get(navigationId);
	%>

	<div  class="col-lg-4 col-md-4 col-sm-12 paddingright5">
		<div class="box box-body;box box-info"  style="min-height: 50px !important; max-height: 50px !important; overflow-y: auto;">
   		 		<ul class="products-list product-list-in-box">
				<li class="item"> 
					<span style="padding-left:8px"">
						<a   href="<%= "MenuNavigationInner.action?NN=1163&strNavigationId="+navigationId %>" ><%=alActionLabel.get(0)%></a> 
			 		</span>
			 	</li>
			 </ul>
   	 	</div>
	</div>

<%}}%>	
</section>