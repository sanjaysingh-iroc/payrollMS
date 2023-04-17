<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmOnetoOneNames = (Map<String, String>)request.getAttribute("hmOnetoOne");
	System.out.println("hmOnetoOneNames:::"+hmOnetoOneNames);
	List<String> onetoOnelist =  (List<String>)request.getAttribute("onetoOnelist");
	String strOneToOneId1 = null;
	if(onetoOnelist != null && onetoOnelist.size() > 0){ 
		 strOneToOneId1 = onetoOnelist.get(0);
	}
	System.out.println("strOneToOneId1:::"+strOneToOneId1);
	%>

	<ul class="products-list product-list-in-box">
	<%if(hmOnetoOneNames != null && hmOnetoOneNames.size()>0) { 
		Iterator<String> it = hmOnetoOneNames.keySet().iterator();
			while(it.hasNext()) {
				String strOneToOneId = it.next();
			
			String strOneToOneName = hmOnetoOneNames.get(strOneToOneId);
	%>
		<li class="item">
			<span style="float: left; width: 100%;">
				<a href="javascript:void(0);" class="activelink" onclick ="getOneToOneDetails('OneToOneDetails','<%=strOneToOneId %>','OneToOneNameList')" >
 				<%= strOneToOneName%></a>
 				<br/>
   			</span>	
 		</li>
		
	<% }
	}else{ %>
		<div class="nodata msg">No One To One requests.</div>
 <% } %>
	
</ul>
 <script type="text/javascript" charset="utf-8">
 $(document).ready(function() {
		getOneToOneDetails('OneToOneDetails','<%=strOneToOneId1%>',OneToOneNameList);
		
	});
 function getOneToOneDetails(strAction,oneTooneId,fromPage){
	 	//alert(oneTooneId);
	 	
		$("#oneToOneDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		var form_data = $("#"+this.id).serialize();
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?oneToOneId='+oneTooneId+'&fromPage='+fromPage,
			data: form_data,
			cache: true,
			success: function(result){
				$("#oneToOneDetails").html(result);
	   		}
		});
	 
 }
</script>