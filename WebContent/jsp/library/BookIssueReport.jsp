<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_wlocation").multiselect().multiselectfilter();
});    
</script>

<script>

	function approveOrDenyBookIssueRequest(value,bookId,bookName,bookAuthor,availQuantity,bookIssuedId,reqQuantity,from,to,empName,reqDate) {
		
		
		if(value == 1){
			removeLoadingDiv('the_div');
			var dialogEdit = '#ApproveOrDenyBookIssuePopup';
			dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog({
				autoOpen : false,
				bgiframe : true,
				resizable : false, 
				height : 400,  
				width : 550, 
				modal : true,
				position: {
							 my: "center",
							 at: "center",
							 of: window,
							 collision: "fit"
					 	  },
				title : 'Issue Book ',
				open : function() {
						    		
			        var xhr = $.ajax({
			                url : 'ApproveOrDenyBookIssueRequest.action?bookId='+bookId+'&bookIssuedId='+bookIssuedId+'&bookName='+bookName+
			                		'&bookAuthor='+bookAuthor+'&availQuantity='+availQuantity+'&reqQuantity='+reqQuantity+'&fromDate='+from
			                		+'&toDate='+to+'&empName='+empName+'&reqDate='+reqDate,
			                cache : false,
			                success : function(data) {
			                	$(dialogEdit).html(data);
			                }
			            });
		           	
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});
			$(dialogEdit).dialog('open');
		}
  }
		
  function returnIssuedBook(value,bookId,bookName,bookAuthor,bookIssuedId,issuedQuantity,toDate,empName,issuedDate) {
		
		if(value == 1){
			removeLoadingDiv('the_div');
			var dialogEdit = '#ReturnIssuedBookPopup';
			dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog({
				autoOpen : false,
				bgiframe : true,
				resizable : false, 
				height : 400,  
				width : 550, 
				modal : true,
				position: {
							 my: "center",
							 at: "center",
							 of: window,
							 collision: "fit"
					 	  },
				title : 'Return Issued Book ',
				open : function() {
						    		
			        var xhr = $.ajax({
			                url : 'ReturnIssuedBook.action?bookId='+bookId+'&bookIssuedId='+bookIssuedId+'&bookName='+bookName+
			                		'&bookAuthor='+bookAuthor+'&issuedQuantity='+issuedQuantity+'&toDate='+toDate
			                		 +'&empName='+empName+'&issuedDate='+issuedDate,
			                cache : false,
			                success : function(data) {
			                	//alert("data==>"+data);
			                	$(dialogEdit).html(data);
			                }
			            });
		           	
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});
			$(dialogEdit).dialog('open');
		}
}	
</script>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function () {
		
			$('#lt').dataTable({ bJQueryUI: true, 
				  								
				"sPaginationType": "full_numbers",
				"aaSorting": [],
				"sDom": '<"H"lTf>rt<"F"ip>',
				oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
				aButtons: [
						"csv", "xls", {
							sExtends: "pdf",
							sPdfOrientation: "landscape"
							//sPdfMessage: "Your custom message would go here."
							}, "print" 
					]
				}
			});
	});
</script>

<script>
function selectall(x,strEmpId){
	var  status=x.checked; 
	var  arr= document.getElementsByName(strEmpId);
	for(i=0;i<arr.length;i++){ 
  		arr[i].checked=status;
 	}
}
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Book Report" name="title"/>
</jsp:include>

<%
	String dataType = (String)request.getAttribute("dataType");
    UtilityFunctions uF = new UtilityFunctions();
	String sbData = (String) request.getAttribute("sbData");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	Map<String,List<String>> hmBookIssuesDetails = (Map<String,List<String>>)request.getAttribute("hmBookIssuesDetails");
	Map<String,List<String>> hmReturnBookDetails = (Map<String,List<String>>)request.getAttribute("hmReturnBookDetails");
	
	if(hmBookIssuesDetails == null) hmBookIssuesDetails = new HashMap<String,List<String>>();
	if(hmReturnBookDetails == null ) hmReturnBookDetails = new HashMap<String,List<String>>(); 
%>

<div id="printDiv" class="leftbox reportWidth">

	 <div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
		<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
			<%=(String)request.getAttribute("selectedFilter") %>
		</p>
	    <div class="content1" style="height: 65px;">
			<s:form name="frmBookIssueReport" id = "frmBookIssueReport" action="BookIssueReport" theme="simple">
				<div style="float: left; width: 100%; margin-top: -5px;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-filter"></i>
					</div>
					<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Organisation</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left; margin-right: 10px;" listValue="orgName" 
							onchange="document.frmBookIssueReport.submit();" list="organisationList" />
					</div>
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Location</p>
							<s:select theme="simple" name="f_wlocation" id="f_wlocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
							listValue="wLocationName" list="wLocationList" multiple="true"/>
					</div>
					<div style="float:left; margin-top: 10px;margin-left: 40px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<s:submit value="Submit" cssClass="input_button" cssStyle="margin:0px" />
					</div>
				</div>
			</s:form>
		</div>
	 </div>
		 
<div style="float:left;width:100%;margin-top:10px;">	
	<s:form theme="simple" name="frmBookIssueReturns" id = "frmBookIssueReturns" action="BookIssueReport" method="POST">
		<s:hidden name="dataType"/>
		<div style="float:left; font-size:12px; line-height:22px; width:514px; margin:12px 0px 0px 224px;">
	           <span style="float:left; margin-right:7px;">Search:</span>
	           <div style="border:solid 1px #68AC3B;float:left; -moz-border-radius: 3px;	-webkit-border-radius: 3px;	border-radius: 3px;">
		            <div style="float:left;">
		            	<input type="text" id="strSearchJob" name="strSearchJob" style="margin-left: 0px; border:0px solid #ccc; width:282px; box-shadow:0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>"/> 
		          	</div>
		         	 <div style="float:right">
		            	<input type="submit" value="Search"  class="input_search" >
		            </div>
	       		</div>
	       </div>
	       
	      <script>
			$( "#strSearchJob" ).autocomplete({
				source: [ <%=uF.showData(sbData,"") %> ]
			});
		</script>
		
		<div style="float:left;width:100%;margin-top:25px;">
		<%
			String strLabel = "";
			if(dataType == null || dataType.equals("I")) { 
				strLabel ="Issue Book Requests";
		%>
					<a href="BookIssueReport.action?dataType=I" class="all" style="width: 150px;">Issue Book Requests</a>
					<a href="BookIssueReport.action?dataType=R"  class="live_dull" style="width: 150px;">Return Book Details</a>
		<% } else if(dataType != null && dataType.equals("R")) { 
				strLabel ="Return Book Details"; 
		%>
				<a href="BookIssueReport.action?dataType=I" class="all_dull" style="width: 150px;">Issue Book Requests</a>
				<a href="BookIssueReport.action?dataType=R" class="live" style="width: 150px;">Return Book Details</a>
		<% } %>	
	</div>
		<div style="float:left;width:100%;margin-top:-15px;">
		<% if(dataType != null && dataType.equals("I")) { %>
			<table id="lt" class="display" style="width:100%">
				<thead>
					<tr>
						<th style="text-align: left;"></th>
						<th style="text-align: left;">Title</th>
						<th style="text-align: left;">Author</th>
						<th style="text-align: left;">Category</th>
						<th style="text-align: left;">Available Quantity</th>
						<th style="text-align: left;">Requested by</th>
						<th style="text-align: left;">Request Quantity</th>
						<th style="text-align: left;">From</th>
						<th style="text-align: left;">To</th>
						<th style="text-align: left;">Action</th>
					</tr>
				</thead>
				<tbody>
					<%
					if(hmBookIssuesDetails != null  && hmBookIssuesDetails.size()>0){
						Set bookSet = hmBookIssuesDetails.keySet();	
						Iterator<String> it = bookSet.iterator();
						while(it.hasNext()){
					 	  String bookIssuedId = it.next();
					 	  List<String> bookDetailsList = (ArrayList<String>)hmBookIssuesDetails.get(bookIssuedId);
					 	  if(bookDetailsList == null) bookDetailsList = new ArrayList<String>();
					 	  if(bookDetailsList != null && bookDetailsList.size()>0 ){
					 %>
	
							<tr id = "trBook_<%=bookDetailsList.get(5)%>">
							    
							    <td style="text-align: left;width:75px;"><%=bookDetailsList.get(12)%></td>
								<td><%=bookDetailsList.get(1)%></td>
								<td><%=bookDetailsList.get(2)%></td>
								<td><%=bookDetailsList.get(3)%></td>
								<td><%=bookDetailsList.get(4)%></td>
								<td><%=bookDetailsList.get(10)%></td>
								<td><%=bookDetailsList.get(7)%></td>
								<td><%=bookDetailsList.get(8)%></td>
								<td><%=bookDetailsList.get(9)%></td>
								<td>
									<select style="width:55px;" onchange="approveOrDenyBookIssueRequest(this.value,'<%=bookDetailsList.get(0)%>','<%=bookDetailsList.get(1)%>','<%=bookDetailsList.get(2)%>', '<%=bookDetailsList.get(4)%>','<%=bookDetailsList.get(5)%>', '<%=bookDetailsList.get(7)%>','<%=bookDetailsList.get(8)%>','<%=bookDetailsList.get(9)%>','<%=bookDetailsList.get(10)%>','<%=bookDetailsList.get(13)%>');">
										<option value="">Select</option>
										<option value="1">Approve/Deny</option>
									</select>
								</td>
								
							</tr>
					<%
					 	    }
						}
					}
						%>
				</tbody>
			</table>
			<% } else if(dataType != null && dataType.equals("R")) { %>
					<table id="lt" class="display" style="width:100%">
				<thead>
					<tr>
						
						<th style="text-align: left;"></th>
						<th style="text-align: left;">Title</th>
						<th style="text-align: left;">Author</th>
						<th style="text-align: left;">Category</th>
						<th style="text-align: left;">Issued Quantity</th>
						<th style="text-align: left;">Issued to</th>
						<th style="text-align: left;">Issued Date</th>
						<th style="text-align: left;">Return Date</th>
						<th style="text-align: left;">Comment</th>
						<th style="text-align: left;">Action</th>
					</tr>
				</thead>
				<tbody>
					<%
					if(hmReturnBookDetails != null  && hmReturnBookDetails.size()>0){
						Set bookReturnSet = hmReturnBookDetails.keySet();	
						Iterator<String> it = bookReturnSet.iterator();
						while(it.hasNext()){
					 	  String bookReturnId = it.next();
					 	  List<String> bookReturnDetailsList = (ArrayList<String>)hmReturnBookDetails.get(bookReturnId);
					 	  if(bookReturnDetailsList == null) bookReturnDetailsList = new ArrayList<String>();
					 	  if(bookReturnDetailsList != null && bookReturnDetailsList.size()>0 ){
					 %>
	
							<tr id = "trBook_<%=bookReturnDetailsList.get(5)%>">
							    
							    <td style="text-align: left;width:75px;"><%=bookReturnDetailsList.get(12)%></td>
								<td><%=bookReturnDetailsList.get(1)%></td>
								<td><%=bookReturnDetailsList.get(2)%></td>
								<td><%=bookReturnDetailsList.get(3)%></td>
								<td><%=bookReturnDetailsList.get(6)%></td>
								<td><%=bookReturnDetailsList.get(8)%></td>
								<td><%=bookReturnDetailsList.get(9)%></td>
								<td><%=bookReturnDetailsList.get(7)%></td>
								<td><%=bookReturnDetailsList.get(10)%></td>
								<td>
									<select style="width:55px;" onchange="returnIssuedBook(this.value,'<%=bookReturnDetailsList.get(0)%>','<%=bookReturnDetailsList.get(1)%>','<%=bookReturnDetailsList.get(2)%>','<%=bookReturnDetailsList.get(4)%>','<%=bookReturnDetailsList.get(6)%>','<%=bookReturnDetailsList.get(7)%>','<%=bookReturnDetailsList.get(8)%>','<%=bookReturnDetailsList.get(9)%>');">
										<option value="">Select</option>
										<option value="1">Return a Book</option>
									</select>
								</td>
								
							</tr>
					<%
					 	    }
						}
					}
						%>
				</tbody>
			</table>
			
			<% } %>
			</div>
		</s:form>
	</div>
</div>
<div id="issueOrReturnBookPopup"></div>
<div id="ApproveOrDenyBookIssuePopup"></div>
<div id = "ReturnIssuedBookPopup"></div>
<script>

$("img.lazy2").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy2").trigger("sporty") }, 1000);
});
</script>