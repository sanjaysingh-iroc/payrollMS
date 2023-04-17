<%=(String)request.getAttribute("lastNoticeId") %>::::<%=(String)request.getAttribute("remainNotices") %>::::
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
	
		  $('div.expandDiv').expander({
			  
			    slicePoint: 100, //It is the number of characters at which the contents will be sliced into two parts.
			    widow: 2,
			    expandSpeed: 0, // It is the time in second to show and hide the content.
			    userCollapseText: 'Read Less (-)' // Specify your desired word default is Less.
		  });
	
		  $('div.expandDiv').expander();
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

	Map<String, List<String>> hmNotices = (Map<String, List<String>>) request.getAttribute("hmNotices");
	Map<String, String> hmNoticeIds = (Map<String, String>)request.getAttribute("hmNoticeIds");
	if(hmNotices==null){
			hmNotices = new LinkedHashMap<String, List<String>>();
	}
	if(hmNoticeIds==null){
			 hmNoticeIds = new LinkedHashMap<String, String>();
	}
	Set<String> setNotices = hmNoticeIds.keySet();
	Iterator<String> it = setNotices.iterator();
	while(it.hasNext()) {
		String strNoticeId = it.next();
		List<String> notice = hmNotices.get(strNoticeId);
		if(notice == null) notice = new ArrayList<String>();
		if(notice != null && !notice.isEmpty() && !notice.equals("")) {
	%>
			<div class="box-footer box-comments" id="mainNoticeDiv_<%=notice.get(0) %>" style="border-top: 1px solid #ECECEC;background: #FDFDFD;padding: 5px;">
								              <div class="box-comment" id="noticeDataDiv_<%=notice.get(0) %>">
								                <!-- User image -->
								                <img class="img-circle img-sm" src="userImages/avatar_photo.png" alt="User Image" style="width: 40px !important;height: 40px !important;">
												<%-- <%=notice.get(12) %> Please make this return image path to put in above src--%> 
								                <div class="comment-text" style="margin-left: 55px;">
								                      <span class="username" style="font-size: 14px;color: #0089B4;">
								                        <%=notice.get(8) %> <span style="font-weight:400;">has posted an Announcement of </span><%=notice.get(3) %>
								                        <span class="text-muted pull-right">
								                        	<% if((uF.parseToInt(strEmpId) == uF.parseToInt(notice.get(6)) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
                                                                <div style="float: left;">
                                                                    <a href="javascript:void(0);" onclick="editNoticePopup('<%=notice.get(0) %>', 'E');">
                                                                    <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
                                                                    </a>
                                                                    <a href="javascript:void(0);" onclick="editYourNotice('<%=notice.get(0) %>', 'D');">
                                                                    <i class="fa fa-trash" aria-hidden="true"></i>
                                                                    </a>
                                                                </div>
                                                                <% } %>
                                                                <%=notice.get(7) %><%=notice.get(11)%></span>
								                      </span><!-- /.username -->
								                  <% if((uF.parseToInt(strEmpId) == uF.parseToInt(notice.get(6)) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
								                  <p style="font-size: 14px;width: 60%;font-style:italic;color: rgb(156, 156, 156);"><%=notice.get(4) %></p>
								                  <p><span class="label label-warning">Start Date: <%=notice.get(1) %></span>&nbsp&nbsp<span class="label label-danger"> End Date: <%=notice.get(2) %></span></p>
								                  <% } else { %>
								                  <p style="font-size: 14px;width: 60%;font-style:italic;color: rgb(156, 156, 156);"><%=notice.get(4) %></p>
								                  <% } %>
								                </div>
								                <!-- /.comment-text -->
								              </div>
								              <!-- /.box-comment -->
								            </div>
			<% } %>
		<% } %>
	
			
	
<script>
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});
</script>
