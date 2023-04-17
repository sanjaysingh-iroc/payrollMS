<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
 <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
 <%	UtilityFunctions uF = new UtilityFunctions();
    String callFrom = (String) request.getAttribute("callFrom");
    String pType = (String) request.getAttribute("pType");
    String alertID = (String) request.getAttribute("alertID");
    
    String strClass1 = "class=\"active\"";
    String strClass2 = "";
    
    if(callFrom != null && callFrom.equals("LPDash")) {
    	 strClass1 = "";
         strClass2 = "class=\"active\"";
    }
 %> 
  
<section class="content">
   <div class="row jscroll">
       <section class="col-lg-12 connectedSortable col_no_padding">
          <div class="col-md-12">
				 <div class="nav-tabs-custom">
				      <ul class="nav nav-tabs">
				          <%-- <li <%=strClass1 %>><a href="javascript:void(0)" onclick="getMyHRData('KRATarget','L','<%=pType%>','<%=alertID%>');" data-toggle="tab">Goals, KRAs, Targets</a></li> --%>
				          <li <%=strClass1 %>><a href="javascript:void(0)" onclick="getMyHRData('KRATarget_1','L','<%=pType%>','<%=alertID%>');" data-toggle="tab">Goals</a></li>
				          <li <%=strClass2 %>><a href="javascript:void(0)" onclick="getMyHRData('MyLearningPlan','','<%=pType%>','<%=alertID%>');" data-toggle="tab">Learnings</a></li>
				       </ul>
				      <div class="tab-content" >
				           <div class="active tab-pane" id="divMyHRData" style="min-height: 600px;">
				     		</div>
				      </div>
				  </div>
			    </div>
	   </section>
    </div>
</section> 

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		
		<% if(callFrom != null && callFrom.equals("LPDash")) { %>
			getMyHRData('MyLearningPlan','','<%=pType%>','<%=alertID%>');
		<%} else { %>
			getMyHRData('KRATarget_1','L','<%=pType%>','<%=alertID%>');
		<% } %>
	});
		function getMyHRData(strAction,dataType,pType,alertID){
	 // alert("getMyHRData jsp action==>" + strAction+"==>dataType==>"+dataType);
	  strAction = strAction+".action?fromPage=MyHR&pType="+pType+"&alertID="+alertID;
	  if(dataType != "") {
		  strAction+= "&dataType="+dataType;
	  }
     
	  $("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	  $.ajax({ 
		type : 'POST',
		url: strAction,
		cache: true,
		success: function(result){
			//alert("result2==>"+result);
			$("#divMyHRData").html(result);
   		}
	  });
	}
	

</script>          
    