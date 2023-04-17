<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@page import="java.util.*"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script> 
<%	UtilityFunctions uF = new UtilityFunctions();
				List alLinks = (List)request.getAttribute("alLinks");
				if(alLinks==null)alLinks  = new ArrayList();
				String strTitle = (String)request.getAttribute("strTitle");
				strTitle = uF.showData(strTitle, "");
				String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
				String strNN = (String)request.getParameter("NN");
				//System.out.println("strNN ===>> " + strNN);
				String toPage = (String) request.getAttribute("toPage");
				String toTab = (String) request.getAttribute("toTab");
				//System.out.println("toPage ===>> " + toPage);
				
				String strOrg = (String) request.getAttribute("strOrg");
				String strLocation = (String) request.getAttribute("strLocation");
				String strDepartment = (String) request.getAttribute("strDepartment");
				String strSbu = (String) request.getAttribute("strSbu");
				String strLevel = (String) request.getAttribute("strLevel");
				String strCFYear = (String) request.getAttribute("strCFYear");
				String strMonth = (String) request.getAttribute("strMonth");
				String strYear = (String) request.getAttribute("strYear");
				String strFromDate = (String) request.getAttribute("strFromDate");
				String strToDate = (String) request.getAttribute("strToDate");
				String callFrom = (String) request.getAttribute("callFrom");
				String strNavigationId = (String) request.getAttribute("strNavigationId");
				//System.out.println("callFrom --->> " + callFrom);
				
				
			%>
<script type="text/javascript">
//Created By Dattatray Date:28-09-21
$(document).ready(function() {
	<% 
		int count2=0;
		for(int i=0; i<alLinks.size(); i++) {
		Map<String, String> hm = (Map) alLinks.get(i);
		if(hm==null)hm = new HashMap<String, String>();
	%>
		var index = <%=count2%>;
		if(selectedIndex == undefined){
			selectedIndex = 0;
		}
		if(index != selectedIndex){
			$("#reports"+index).addClass("disabled-pointer");
		}
	<%
	count2++;
	%>
<%}%>
	
});
function getPageOnAction(strAction, parentId, parentLbl, chieldLbl,selectedIndex) {
	//alert("strAction==>"+strAction);
			// Started By Dattatray Date:28-09-21 
				<% 
					int count=0;
	        		for(int i=0; i<alLinks.size(); i++) {
	        		Map<String, String> hm = (Map) alLinks.get(i);
	        		if(hm==null)hm = new HashMap<String, String>();
        	%>
	        		var index = <%=count%>;
	        		if(selectedIndex == undefined){
	        			selectedIndex = 0;
	            	}
	        		if(index != selectedIndex){
	        			$("#reports"+index).addClass("disabled-pointer");
	            	}
        	<%
        		count++;
        	%>
        	<%}%>
        	// Ended By Dattatray Date:28-09-21 
	 $("#actionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 var activeElement = $(this).parent("li");
	$.ajax({
		type : 'POST',
		url: ''+strAction,
		data: $("#"+this.id).serialize(),
		success: function(result){
			// Started By Dattatray Date:28-09-21 
			<% 
				int count1=0;
	        	for(int i=0; i<alLinks.size(); i++) {
	        		Map<String, String> hm = (Map) alLinks.get(i);
	        		if(hm==null)hm = new HashMap<String, String>();
        		%>
	        		var index = <%=count1%>;
	        		$("#reports"+index).removeClass("disabled-pointer");
            	<%
        		count1++;
        		%>
        	<%}%>
        	// Ended By Dattatray Date:28-09-21 
			$("#actionResult").html(result);
   		} 
	});
	
	if(chieldLbl !== "") {
		chieldLbl = chieldLbl.replace("::","'");
	}
		
	if(parentLbl !== "") {
		parentLbl = parentLbl.replace("::","'");
	}
	
	/* var sbpageTitleNaviTrail = "<ol class=\"breadcrumb\" style=\"top: 1px; position: relative; float: left; width: 100%;\"> "
	+"<li><i class=\"fa fa-th-large\"></i><a href=\"MenuNavigationInner.action?NN=1109\" style=\"color: #3c8dbc;\"> Reports</a></li>"
	+"<li><a href=\"MenuNavigationInner.action?NN="+parentId+"\" style=\"color: #3c8dbc;\">"+parentLbl+"</a></li>"
	+"<li class=\"active\">"+chieldLbl+"</li></ol>";
	
	//alert("sbpageTitleNaviTrail"+sbpageTitleNaviTrail);
	document.getElementById("pageTitleAndNaviTrail").innerHTML = sbpageTitleNaviTrail; */
}

</script>

<style>
li > span {
position: relative;
top: 0px;
}
.level_list>li {
padding-top: 5px;
padding-bottom: 5px;
border-bottom: 1px solid rgb(240, 240, 240);
}
.disabled-pointer {
    pointer-events:none;
    opacity:0.6;     
}
</style>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>  --%>
<%String []arrModules = (String[])request.getAttribute("arrModules"); %>

      <!-- Main content -->
	<section class="content">
         <!-- title row -->
		<div class="row">
			
			
			<%if(strNN!=null && !strNN.equalsIgnoreCase("1")) { %>
				<div class="col-md-3">
					<div class="box box-body" style="min-height: 600px !important; max-height: 630px !important; overflow-y: auto;">
						<ul class="products-list product-list-in-box">
							<% 
								for(int i=0; i<alLinks.size(); i++) {
									Map<String, String> hm = (Map) alLinks.get(i);
									if(hm==null)hm = new HashMap<String, String>();
							%>
						 		<li class="item">
						 			<span style="float: left; width: 100%;">
						 			<%-- <img src="<%=request.getContextPath() %>/images1/icons/icons/w_green.png" />&nbsp; --%>
						 			<!-- Created By Dattatray Date:28-09-21  Note:Id set to a tag-->
									<a href="javascript:void(0);"   id="reports<%=i%>"
									<% if(uF.parseToInt(strNavigationId)>0  && uF.parseToInt(strNavigationId) == uF.parseToInt(hm.get("NAVI_ID"))) { %> 
										class="activelink"
									<% } else if(uF.parseToInt(strNavigationId)==0 && i==0 && (callFrom == null || callFrom.equalsIgnoreCase("null") || callFrom.equals(""))) { %> 
										class="activelink"
									<% } else if(callFrom != null && callFrom.equalsIgnoreCase("FactQuickLinkViewSalarySlip") && (uF.parseToInt(hm.get("NAVI_ID")) == 731 || uF.parseToInt(hm.get("NAVI_ID")) == 1131)) { %> 
										class="activelink"
									<% } %>
									onclick="<%=hm.get("ACTION") %>" ><%=hm.get("LABEL") %></a></span>
									<span style="float: left; width: 100%;"><%=uF.showData(hm.get("DESC"), "") %></span>
								</li>
							<% } %>
						</ul>
				 	</div>
				</div>
			<% } %>
			
			
			<%if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && strNN!=null && strNN.equalsIgnoreCase("1")){ %>
				<div class="col-md-3">
					<div class="box box-body" style="min-height: 600px;">
						<ul class="products-list product-list-in-box">
						 	<%
								List alParentNavL = (List) session.getAttribute("alParentNavL");
								Map hmChildNavL = (Map) session.getAttribute("hmChildNavL");
								
								List alParentNavR = (List) session.getAttribute("alParentNavR");
								Map hmChildNavR = (Map) session.getAttribute("hmChildNavR");
								Map hmNavigation = (Map) session.getAttribute("hmNavigation");
								Map hmNavigationParent = (Map) session.getAttribute("hmNavigationParent");
								
								CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
								String []arrEnabledModules = CF.getArrEnabledModules();
								String []arrAllModules = CF.getArrAllModules();
								 
								 List alList = (List) hmChildNavL.get("1");
								 if(alList==null)alList=new ArrayList();
								 for(int i=0; i<alList.size(); i++) {
								 	Navigation nav = (Navigation)alList.get(i);
							%>
							
						 		<li class="item">
						 			<div style="float: left; width: 100%;">
						 				<h2 class="heading">- <%=nav.getStrLabel() %> <p style="font-size:10px; font-weight:normal"><%=uF.showData(nav.getStrDescription(), "-") %></p></h2>
						 			</div>
						 			<%-- <span style="float: left; width: 100%;"><img src="<%=request.getContextPath() %>/images1/dailyhrs_24x24.png" />
									<a href="<%= hm.get("ACTION")%>"><%= hm.get("LABEL")%></a></span>
							       <span style="float: left; width: 100%;"><%= uF.showData(hm.get("DESC"),"")%></span> --%>
							    </li>
						 	<% } %>
					 	</ul>
				 	</div>
				</div>
			<%} %>
		
			<div class="col-md-9" style="padding-left: 0px;min-height: 600px;">
				<div class="box box-body" style="padding: 5px; overflow-y: auto;  min-height: 600px; max-height:700px;" id="actionResult">
					
				</div>
			</div>
		</div>
	</section>


<script type="text/javascript">
			function pop(div) {
				document.getElementById(div).style.display = 'block';
			}
			function hide(div) {
				document.getElementById(div).style.display = 'none';
			}
			//To detect escape button
			document.onkeydown = function(evt) {
				evt = evt || window.event;
				if (evt.keyCode == 27) {
					hide('popDiv');
				}
			};
		</script>
		
	

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		var firstNaviLbl = '<%=(String)request.getAttribute("firstNaviLbl") %>';
		var firstNaviAction = '<%=(String)request.getAttribute("firstNaviAction") %>';
		var firstNaviParentId = '<%=(String)request.getAttribute("firstNaviParentId") %>';
		var strTitle = '<%=(String)request.getAttribute("strTitle") %>';
		//alert("firstNaviLbl=="+firstNaviLbl+"firstNaviAction=="+firstNaviAction+"firstNaviParentId=="+firstNaviParentId+"strTitle=="+strTitle);
		getPageOnAction(firstNaviAction, firstNaviParentId, strTitle, firstNaviLbl);
		
	});

</script>