<%=(String)request.getAttribute("lastEventId") %>::::<%=(String)request.getAttribute("remainEvents") %>::::
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.itextpdf.text.Utilities"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ page buffer="16kb"%>
<%@ page import="java.util.*"%>
<script src="js/jquery.expander.js"></script>
<script>
    /*$(document).ready(function() {	
      $('div.eventExpandDiv').expander({
        slicePoint: 100, //It is the number of characters at which the contents will be sliced into two parts.
        widow: 2,
        expandSpeed: 0, // It is the time in second to show and hide the content.
        userCollapseText: 'Read Less (-)' // Specify your desired word default is Less.
      });
      $('div.eventExpandDiv').expander();
      
    });*/
</script>
<style>
    .tb_style tr td {
    padding: 5px;
    border: solid 1px #c5c5c5;
    }
    .tb_style tr th {
    padding: 5px;
    border: solid 1px #c5c5c5;
    background: #efefef;
    }
    ul li.desgn { padding:0px ; border:solid 1px #ccc}
</style>
<%
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    String strOrgId = (String)session.getAttribute(IConstants.ORGID);
    String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
    
    UtilityFunctions uF = new UtilityFunctions();
    
    Map<String, List<String>> hmEvents = (Map<String, List<String>>) request.getAttribute("hmEvents");
    Map<String, String> hmEventIds = (Map<String, String>)request.getAttribute("hmEventIds");
    List<String> availableExt = (List<String>)request.getAttribute("availableExt");
    if(availableExt == null) availableExt = new ArrayList<String>();
    if(hmEvents==null){
    		hmEvents = new LinkedHashMap<String, List<String>>();
    }
    if(hmEventIds==null){
    		hmEventIds = new LinkedHashMap<String, String>();
    }
    Set<String> setEvents = hmEventIds.keySet();
    Iterator<String> it = setEvents.iterator();
    
    while(it.hasNext()) {
    	String strEventId = it.next();
    	List<String> event = hmEvents.get(strEventId);
    	if(event == null) event = new ArrayList<String>();
    	if(event != null && !event.isEmpty() && !event.equals("")) {
    		boolean flag = false;
    		if(availableExt.contains(event.get(11))) {
    			flag = true;
    		}
    		
    %>
<div class="box box-widget" id="mainEventDiv_<%=event.get(0) %>">
    <div class="box-header with-border" style="padding: 5px;">
        <div class="user-block">
            <img class="img-circle" src="userImages/avatar_photo.png" alt="User Image">
           
            <span class="username"><%=event.get(6) %> has posted an Event.</span>
            <span class="description"><%=event.get(3)%></span>
        </div>
        <!-- /.user-block -->
        <% if(((uF.parseToInt(strEmpId) == uF.parseToInt(event.get(10))) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
        <div class="box-tools">
            <a href="javascript:void(0);" onclick="editEventPopup('<%=event.get(0) %>', 'E_E');">
            <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
            </a>  
            <a href="javascript:void(0);" onclick="editYourEvent('<%=event.get(0) %>', 'E_D');">
            <i class="fa fa-trash" aria-hidden="true"></i>
            </a>
        </div>
        <%} %>
        <!-- /.box-tools -->
    </div>
    <!-- /.box-header -->
    <div class="box-body" id="eventDataDiv_<%=event.get(0) %>">
        <!-- post text -->
        <p><%=event.get(5) %></p>
        <!-- Attachment -->
        <div class="attachment-block clearfix">
            	
			<% if(event.get(9) != null && !event.get(9).equals("")) {
				if(event.get(11)!=null && (event.get(11).equalsIgnoreCase("jpg") || event.get(11).equalsIgnoreCase("jpeg") || event.get(11).equalsIgnoreCase("png") || event.get(11).equalsIgnoreCase("bmp") || event.get(11).equalsIgnoreCase("gif"))){
			%>	
					<%=event.get(12)%>
			<% } else {
				if(flag && event.get(17)!=null && !event.get(17).equals("") ){
				%>
					<div id="tblDiv" style="float: left; width: 100%;">
					<a href="javascript:void(0);" onclick="viewEventFilePopup('<%=strEventId%>');event.preventDefault();"  style="color:gray;">&nbsp;<%=event.get(9)%></a>
					</div>
				<%}else{ %>
					<div style="float:left; padding: 60px 40px; font-size: 14px; border: 1px solid #CCCCCC;">Image Preview not available.</div>			
				<%}
			}
		}		
							%>
            <div class="attachment-pushed">
                <h4 class="attachment-heading"><%=event.get(4)%></h4>
                <%=event.get(18)%>
                <div class="attachment-text">
                    Organised at <%=event.get(7) %><br>
                    From <b><%=event.get(1) %> </b> to <b><%=event.get(2) %></b><br>
                    Timing: <b><%=event.get(14)%></b> To <b><%=event.get(15)%></b><br>
                    
                </div>
                <!-- /.attachment-text -->
            </div>
            <!-- /.attachment-pushed -->
        </div>
        <!-- /.attachment-block -->
    </div>
    <!-- /.box-body -->
</div>
<% } %>
<% } %>
<%-- <script>
    //$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    $("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
    
    $(window).bind("load", function() {
        var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
    });
</script> --%>