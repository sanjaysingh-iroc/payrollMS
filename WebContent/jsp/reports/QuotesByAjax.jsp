<%=(String)request.getAttribute("lastQuoteId") %>::::<%=(String)request.getAttribute("remainQuotes") %>::::
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
	  $('div.quoteExpandDiv').expander({
		    slicePoint: 100, //It is the number of characters at which the contents will be sliced into two parts.
		    widow: 2,
		    expandSpeed: 0, // It is the time in second to show and hide the content.
		    userCollapseText: 'Read Less (-)' // Specify your desired word default is Less.
	  });
	  $('div.quoteExpandDiv').expander();
	  
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

	Map<String, List<String>> hmQuotes = (Map<String, List<String>>) request.getAttribute("hmQuotes");
	Map<String, String> hmQuoteIds = (Map<String, String>)request.getAttribute("hmQuoteIds");
	if(hmQuotes==null){
			hmQuotes = new LinkedHashMap<String, List<String>>();
	}
	if(hmQuoteIds==null){
			hmQuoteIds = new LinkedHashMap<String, String>();
	}	
	Set<String> setQuotes = hmQuoteIds.keySet();
	Iterator<String> it = setQuotes.iterator();
	
	while(it.hasNext()) {
		String strQuoteId = it.next();
		List<String> quote = hmQuotes.get(strQuoteId);
		if(quote == null) quote = new ArrayList<String>();
		if(quote != null && !quote.isEmpty() && !quote.equals("")) {
	%>
			<div class="box-footer box-comments" id="mainQuoteDiv_<%=quote.get(0) %>" style="border-top: 1px solid #ECECEC;background: #FDFDFD;">
	             <div class="box-comment" id="quoteDataDiv_<%=quote.get(0) %>">
	                <!-- User image -->
	                <%=quote.get(7) %> 
	                <div class="comment-text" style="margin-left: 55px;">
	                    <span class="username" style="font-size: 14px;color: #0089B4;">
	                        <%=quote.get(4) %> <span style="font-weight:400;">has posted a quote by </span><%=quote.get(2) %>
	                        <span class="text-muted pull-right"><%=quote.get(5) %>
	                           <% if(((uF.parseToInt(strEmpId) == uF.parseToInt(quote.get(6))) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
	                              <div style="float: right;">
	                                   <a href="javascript:void(0);" onclick="editQuotePopup('<%=quote.get(0) %>', 'Q_E');">
	                                    <i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
	                                    <a href="javascript:void(0);" onclick="deleteYourQuotes('<%=quote.get(0) %>', 'Q_D');">
	                                    <i class="fa fa-trash" aria-hidden="true"></i></a>
	                              </div>
	                         <% } %>
	                       </span>
	                   </span><!-- /.username -->
	                  <p style="font-size: 14px;width: 60%;font-style:italic;color: rgb(156, 156, 156);"><sup style="color: rgb(109, 109, 109) !important"><i class="fa fa-quote-left" aria-hidden="true"></i></sup><%=quote.get(3) %><sup style="color: rgb(109, 109, 109) !important;"><i class="fa fa-quote-right" aria-hidden="true"></i></sup></p>
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
