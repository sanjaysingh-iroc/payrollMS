<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@page import="java.util.Iterator"%> 
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@taglib uri="/struts-tags" prefix="s"%>
<style> 
.ann-stat-img img{
 width: 14px !important; 
 height: 14px !important;
 }
 
.listMenu1 .icon .fa{
font-size: 55px;
vertical-align: top;
margin-top: 20px;
}
.box-comments .text-muted {
font-weight: 400;
font-size: 14px;
}

::-webkit-scrollbar{
	width:0.25em;
}

::-webkit-scrollbar-button{
	background : #ccc
}

::-webkit-scrollbar-track-piece{
	background : #888
}

::-webkit-scrollbar-thumb{
	background: #eee
}

.eventCard {
	box-shadow: 0 0 0 1px rgba(0,0,0,0.1), 0 2px 3px rgba(0,0,0,0.1);
    transition: 0.3s;
    border-radius: 10px;
    margin: 2%;
    background-color: white;
}

.imageBorder {
	border: outset;
    border-color: #cac8c8 !important;
    border-radius: 2rem !important;
    padding-right: 0px !important;
    border-width: thin;

}

                .dropbtn {
                   color: #545151;;
    			   font-size: 16px;
    			   border: none;
    			   cursor: pointer;
                }

                .dropbtn:hover, .dropbtn:focus {
                    background-color: #rgba(0, 0, 0, 0.1);
                }

                .customDropdown {
                    position: absolute;
    				display: inline-block;
    				right: 0.4em;
    				margin-right: 1%;
    				margin-top: -5%;
                }

                .customDropdown-content {
                    display: none;
                    position: relative;
                    margin-top: 60px;
                    background-color: #f9f9f9;
                    min-width: 160px;
                    overflow: auto;
                    box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
                    z-index: 1;
                }

                .customDropdown-content a {
                    color: black;
                    padding: 5px 16px;
                    text-decoration: none;
                    display: block;
                }

                .customDropdown a:hover {background-color: #f1f1f1}

                .show {display:block;}

				.customeDropdown ul{
					padding-left: 0px;
				}
				

</style>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> 
<script src="<%= request.getContextPath()%>/js/jquery-ui-1.8.16.custom.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath() %>/scripts/EasyTree/jquery.easytree.js"></script> 
<script src="<%= request.getContextPath()%>/scripts/EasyTree/jquery.easytree.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
<link href="<%=request.getContextPath() %>/scripts/EasyTree/skin-win8/ui.easytree.css" rel="stylesheet"/> --%>

<script type="text/javascript">

/* $(document).ready(function(){
	$.fn.gdocsViewer = function(options) {
		
		var settings = {
			width  : '98%',
			height : '742'
		};
		
		if (options) { 
			$.extend(settings, options);
		}
		
		return this.each(function() {
			var file = $(this).attr('href');
            // int SCHEMA = 2, DOMAIN = 3, PORT = 5, PATH = 6, FILE = 8, QUERYSTRING = 9, HASH = 12
            var ext=file.substring(file.lastIndexOf(".")+1);
            
          //  console.log("Extension : "+ext);
          if(ext === "docx" || ext === "pptx") {
        	   alert("Unsupported file format!");
           }else {
			if (/^(tiff|pdf|ppt|pptx|pps|doc|txt|xls|xlsx)$/.test(ext)) {
				$(this).after(function () {
					var id = $(this).attr('id');
					var gdvId = (typeof id !== 'undefined' && id !== false) ? id + '-gdocsviewer' : '';
					return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 742px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 742px; border: none;margin : 0 auto; display : block;"></iframe></div>';
				})
			}
           }
		});
	};
	
	$('a.embed').gdocsViewer();
}); */ 



</script>
<script>
    /* $(document).ready(function(){
    	
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
    	});

    	$("#strStartDate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strEndDate').datepicker('setStartDate', minDate);
        });
        
        $("#strEndDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#strStartDate').datepicker('setEndDate', minDate);
        });
        
        $("#displayStartDate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#displayEndDate').datepicker('setStartDate', minDate);
        });
        
        $("#displayEndDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#displayStartDate').datepicker('setEndDate', minDate);
        });

    	$("#strLevel").multiselect().multiselectfilter();
    });  
   

    $(function(){
    	$('input[type="submit"]').click(function(){
    		$("#"+this.form.id).find('.validateRequired').filter(':hidden').prop('required',false);
            $("#"+this.form.id).find('.validateRequired').filter(':visible').prop('required',true);
    	});
    }); */
    
    function viewManual(manualId, strManualIds) {
		
    	var strIds = strManualIds.split(",");
		$.ajax({
			url : 'AddUpdateViewHubContent.action?type=M&manualId='+manualId,
	        cache : false,
	        success : function(data) {
	           	$("#divResult").html(data);
	        },
	        error : function(data) {
	           	$("#divResult").html(data);
	        }
	   });
    }
		
    

</script>

<% String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));

    UtilityFunctions uF = new UtilityFunctions();
    
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    String strOrgId = (String)session.getAttribute(IConstants.ORGID);
    
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    String []arrEnabledModules = CF.getArrEnabledModules();
    String type = (String) request.getAttribute("type");
    String operation = (String) request.getAttribute("operation");
    
    String DOC_RETRIVE_LOCATION = (String)request.getAttribute("DOC_RETRIVE_LOCATION");
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
      	if (hmEmpProfile == null) {
      		hmEmpProfile = new HashMap<String, String>();
      	}
      	String strEmpID = (String) session.getAttribute(IConstants.EMPID);
    %>

<% if(type!=null && (operation == null || operation.equalsIgnoreCase("null") || operation.equalsIgnoreCase(""))) { %>
<section class="content">
    <div class="row jscroll">
    <section class="col-lg-12 connectedSortable">
        <div class="box box-primary">
            <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div class="rightMenu" style="margin-top: 2px;">
                        <div style="float: left; width: 100%;">
                            <%
                                if(type != null && type.equals("E")) {
                                
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
                                %>	
                                <% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
                                    <p><a href="javascript:void(0);" onclick="addEvent();"><i class="fa fa-plus-circle"></i>Add Event</a></p>
								<% } %>
								
	                                <% if(operation == null || operation.equalsIgnoreCase("null") || operation.equals("")) { %>    
                                        <div class="clr"></div>
                                        <div id="allEventsDiv">
		                                	<input type="hidden" name="eventhideOffsetCnt" id="eventhideOffsetCnt" value="10" />
		                                	<s:hidden name="lastEventId" id="lastEventId"/>
		                                <%
                                            int eventCount = 0;
                                            while(it.hasNext()) {
                                            	String strEventId = (String)it.next();
                                            	List<String> eventList = hmEvents.get(strEventId);
                                            	
                                            	if(eventList == null) eventList = new ArrayList<String>();
                                            	if(eventList != null && eventList.size()>0 && !eventList.equals("")) {
                                            		List<String> event = eventList;
                                            		boolean flag = false;
                                            		if(availableExt.contains(event.get(11))) {
                                            			flag = true;
                                            		}
                                            %>
                                            
				                            <div class="eventCard" id="mainEventDiv_<%=event.get(0) %>">
									            <div style="padding: 5px;font-family: -apple-system,system-ui,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica Neue,Fira Sans,Ubuntu,Oxygen,Oxygen Sans,Cantarell,Droid Sans,Apple Color Emoji,Segoe UI Emoji,Segoe UI Symbol,Lucida Grande,Helvetica,Arial,sans-serif;">
									              <div class="user-block">
									                <img class="img-circle" src="userImages/avatar_photo.png" alt="User Image">
									                <span class="username"><%=event.get(6) %> has posted an Event.</span>
									                <span class="description"><%=event.get(3)%></span>
									              </div>
									              <!-- /.user-block -->
									              <% if(((uF.parseToInt(strEmpId) == uF.parseToInt(event.get(10))) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
									              
									              <div style="position: relative;margin-right: 0%;" onclick="showCustomDropdown(<%=event.get(0) %>)">
							              			<div class="customDropdown" >
                    										<!-- three dots -->
					                    			<ul class="dropbtn icons btn-right showLeft" style="padding-bottom: 0%;padding-left: 140px;">
					                    				<i class="fa fa-ellipsis-h" aria-hidden="true"></i>
                    					    		</ul>
                    								
							              			<div id="listCustomDropdown_<%=event.get(0) %>" class="customDropdown-content" style="margin-top: 14%;">		
							              				<a href="javascript:void(0);" onclick="editEventPopup('<%=event.get(0) %>', 'E_E');"><i class="fa fa-pencil-square-o"></i>Edit post</a>
									              		<a href="javascript:void(0);" onclick="editYourEvent('<%=event.get(0) %>', 'E_D');"><i class="fa fa-trash"></i>Delete post</a>
							              			</div>
							              			</div> 
							              			</div>
									              
									              <%} %>
									              <!-- /.box-tools -->
									            </div>
									            <!-- /.box-header -->
									            <div class="box-body" id="eventDataDiv_<%=event.get(0) %>">
									              <!-- post text -->
									              <p style="padding-left: 7%;font-family: -apple-system,system-ui,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica Neue,Fira Sans,Ubuntu,Oxygen,Oxygen Sans,Cantarell,Droid Sans,Apple Color Emoji,Segoe UI Emoji,Segoe UI Symbol,Lucida Grande,Helvetica,Arial,sans-serif;">
									              <%=event.get(5) %></p>
									
									              <!-- Attachment -->
									              <div class="attachment-block clearfix" style="border: unset;background: #fff;">
									                <% if(event.get(9) != null && !event.get(9).equals("")) {
														if(event.get(11)!=null && (event.get(11).equalsIgnoreCase("jpg") || event.get(11).equalsIgnoreCase("jpeg") || event.get(11).equalsIgnoreCase("png") || event.get(11).equalsIgnoreCase("bmp") || event.get(11).equalsIgnoreCase("gif"))){ 
													%>	
													
														<%=event.get(12)%> 
													
													  <% } else { 
															if(flag && event.get(17)!=null && !(event.get(17)).equals("")){
													  %>
												  			<div id="tblDiv">
																<a href="javascript:void(0);" onclick="viewEventFilePopup('<%=event.get(0)%>');event.preventDefault();"  style="color:gray;">&nbsp;<%=event.get(9)%></a>
															</div>
														<% } else { %>
															<div style="float:left; padding: 60px 40px; font-size: 14px; border: 1px solid #CCCCCC;">Image Preview not available.</div>		
														<% 	}
													  	}
													} else {	
													%>	
														<img class="attachment-img imageBorder" alt="Attachment Image" src="https://myworkrig.com/Workrig/TestingNewUI/Events/1/-19566191091134110878workrig.png" 
														data-original="https://myworkrig.com/Workrig/TestingNewUI/Events/1/-19566191091134110878workrig.png">					
									               <% } %>
									                <div class="attachment-pushed" style="margin-left: 45% !important;">
									                  <h4 class="attachment-heading" style="font-weight: bolder;padding: 2%;color: #676767;text-transform: capitalize;"><%=event.get(4)%></h4>
													  <%=event.get(18) %>
									                  <div class="attachment-text" style="line-height: 25px;padding-top: 1%;">
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
                                        <%	eventCount ++;
                                            }
                                        %>
                                        <% } %>
                                        <% if(eventCount == 0) { %>
                                        	<div class="nodata msg" style="margin-left: 25px; width: 95%;"><span>No events available.</span></div>
                                        <% } %>
                                    <!-- </div> -->
                                    <% if(eventCount==10) { %>
	                                    <div id="loadingEventsDiv" style="display: none; float: left; width: 100%; text-align: center;"> <img src="images1/new_loading.gif"> </div>
	                                    <div id="loadMoreEventsDiv" style="float: left; width: 100%; text-align: center;display:none;"> <a href="javascript:void(0)" onclick="loadMoreEvents()">load more ...</a> </div>
                                    <% } %>
                                </div>
                                <% } %>
                                
                                <% } else if(type != null && type.equals("FAQ")) {
                                	Map<String, List<List<String>>> hmFaqs =( Map<String, List<List<String>>>)request.getAttribute("hmFaqs");
                                	if(hmFaqs==null) hmFaqs = new HashMap<String, List<List<String>>>();
                                	
                                    Map<String,String> hmFaqSection =(Map<String,String>)request.getAttribute("hmFaqSection");
                                   	Set<String> setfaq = hmFaqs.keySet();
                                  	Iterator<String> faqs = setfaq.iterator();
                                 %> 
                                    
                                <div id = "FAQ" style="float:left; width:99%; margin:10px 2px 1px 7px;">
								<%if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.RECRUITER))) { %>
								 	<p style="padding-right: 25px; text-align: right;"><a href="javascript:void(0)" onclick="addFAQ();"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add FAQ</a></p>
								<%} %>
								
								 <% 
								    while(faqs.hasNext()) {
								    	String strSectionId = faqs.next();
								    	List<List<String>> faqList = hmFaqs.get(strSectionId);
								    	%>
									<div id="allSection">
								   	 	<h5 class="box-title" style="text-align:center;color:#337ab7;"><%=hmFaqSection.get(strSectionId) %></h5>
										<div id="allfaq">
								    	<% 
								    	if(faqList != null  && faqList.size()>0 && !faqList.equals("")) {
                         					for(int i = 0;i<faqList.size();i++) {
								    		List<String> faq = faqList.get(i);
                         				%>
									   <div id="mainFaqDiv_" style="border-top: 1px solid #ECECEC;background: #FDFDFD;">
										  <!--  <div class="box-footer box-comments" id="mainFaqDiv_" style="border-top: 1px solid #ECECEC;background: #FDFDFD;">-->
										   	<div class="box-comment" id="faqDataDiv_<%=faq.get(0) %>">
										   		<div>
										   			<div class="box box-default collapsed-box">
														<div class="box-header with-border">
															<h3 class="box-title"><%=faq.get(1) %></h3>
															<div class="box-tools pull-right">
															<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
															<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
															<%if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.RECRUITER))) { %>
																<a href="javascript:void(0)" onclick="AddOrEditFaq('<%=faq.get(0) %>', 'D');" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
																<a href="javascript:void(0)" onclick="AddOrEditFaq('<%=faq.get(0) %>', 'E');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
															<% } %>	
														</div>
														</div>
														<div class="box-body" style="overflow-y: auto;">
															<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
																<p style="padding-left: 5px;"><%=faq.get(2) %></p>
															</div>
														</div>
											   		</div>
												</div>
											</div>
										</div>
										<% } %>
									<% } %>
									</div>
								<% } %>
								 <% if(hmFaqs==null || hmFaqs.size()==0) { %>
								 	<div class="nodata msg" style="margin-left: 25px; width: 95%;"><span>No FAQs available.</span></div>
								 <% } %>
							</div>
                                
                              <% } else if(type != null && type.equals("Q")) {
                                    //List<List<String>> quotesList = (List<List<String>>)request.getAttribute("quoteList");
                                    Map<String, List<String>> hmQuotes = (Map<String, List<String>>) request.getAttribute("hmQuotes");
                                    Map<String, String> hmQuoteIds = (Map<String, String>)request.getAttribute("hmQuoteIds");
                                    if(hmQuotes==null){
                                    	hmQuotes = new LinkedHashMap<String, List<String>>();
                                    }
                                    if(hmQuoteIds==null){
                                    	hmQuoteIds = new LinkedHashMap<String, String>();
                                    }
                                    Set<String> setQuotes = hmQuoteIds.keySet();
                                    Iterator<String> lit = setQuotes.iterator();
                                    
                                    %>	
                                  
                                <div id = "quotes" style="float:left; width:100%; margin:2px 2px 1px 1px;">
                                    <% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
                                    	<p><a href="javascript:void(0);" onclick="addQuote();"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Quote</a></p>
                                    <% } %>	
                                        
                                     <div id="allQuotesDiv" style="height: 1000px;overflow-y: scroll;">
                                        <input type="hidden" name="quotehideOffsetCnt" id="quotehideOffsetCnt" value="10" />
                                        <s:hidden name="lastQuoteId" id="lastQuoteId"/>
                                        <%
                                            int quoteCount = 0;
                                            while(lit.hasNext()) {
                                            	String strQuoteId = lit.next();
                                            	List<String> quotesList = hmQuotes.get(strQuoteId);
                                            	if(quotesList == null) quotesList = new ArrayList<String>();
                                            	if(quotesList != null && quotesList.size()>0 && !quotesList.equals("")){
                                            		List<String> quote = quotesList ;
                                            %>
                                            <div class="box-footer box-comments" id="mainQuoteDiv_<%=quote.get(0) %>" style="border-top: 1px solid #ECECEC;background: #FDFDFD;">
								              <div class="box-comment" id="quoteDataDiv_<%=quote.get(0) %>">
								                <!-- User image -->
								                <!-- <img class="img-circle img-sm" src="userImages/avatar_photo.png" alt="User Image" style="width: 40px !important;height: 40px !important;"> -->
												<%=quote.get(7) %><!--  Please make this return image path to put in above src -->
								                <div class="comment-text" style="margin-left: 55px;">
								                    <span class="username" style="font-size: 14px;color: #0089B4;">
								                        <%=quote.get(4) %> <span style="font-weight:400;">has posted a quote by </span><%=quote.get(2) %>
								                        <span class="text-muted pull-right"><%=quote.get(5) %><% if(((uF.parseToInt(strEmpId) == uF.parseToInt(quote.get(6))) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
                                                             <div style="float: right;">
                                                                 <a href="javascript:void(0);" onclick="editQuotePopup('<%=quote.get(0) %>', 'Q_E');">
                                                                 <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
                                                                 </a>
                                                                 <a href="javascript:void(0);" onclick="deleteYourQuotes('<%=quote.get(0) %>', 'Q_D');">
                                                                 <i class="fa fa-trash" aria-hidden="true"></i>
                                                                 </a>
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

                                        <%	quoteCount++;
                                            }
                                            %>
                                        <% } %>
                                        <% if(quoteCount == 0) { %>
                                        <div class="nodata msg" style="margin-left: 25px; width: 95%;"><span>No quotes available.</span></div>
                                        <% } %>
                                    </div>
                                    <% if(quoteCount==10) { %>
                                    <div id="loadingQuotesDiv" style="display: none; float: left; width: 100%; text-align: center;"> <img src="images1/new_loading.gif"> </div>
                                    <div id="loadMoreQuotesDiv" style="float: left; width: 100%; text-align: center; display:none"> <a href="javascript:void(0)" onclick="loadMoreQuotes()">load more ...</a> </div>
                                    <% } %>
                                </div>
                                <% } else if(type!=null && type.equals("M")) {
                                	
                                		String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
                                		String sessionEmpId = (String)session.getAttribute(IConstants.EMPID);
										String strManualId = (String)request.getAttribute("MANUAL_ID");
										String strTitle = (String)request.getAttribute("TITLE");
                           		   		String strBody = (String)request.getAttribute("BODY");
                           		   		String strDate = (String)request.getAttribute("DATE");
                           		   		List<String> availableExt = (List<String>)request.getAttribute("availableExt");
                           		   		String manualDocPath = (String)request.getAttribute("manualDocPath");
                           		   		String extention = (String)request.getAttribute("extention");

                           		   		if(availableExt == null) availableExt = new ArrayList<String>(); 
                           		   		boolean flag = true;
                           		   		if(!availableExt.contains(extention)) {
                           		   			flag = false;
                           		   		}
                                    	//System.out.println("strManualId ===>> " + strManualId + " -- manualDocPath ===>> " + manualDocPath +" -- strBody ===>> " + strBody);
                                    %>
                                	<div id = "manual" style="float:left; width:99%; margin:10px 2px 1px 7px;">
                                    <% if(strUserType!=null && (strUserType.equals(IConstants.EMPLOYEE) || strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) {
                                        String strDisplay ="block";
                                        String editDisplay ="none";
                                        if(operation != null && operation.equals("E")){
                                        	strDisplay ="none";
                                        	editDisplay ="block";
                                        }
                                        %>
                                    <% String message =  (String)session.getAttribute("MESSAGE");%>
	                                    <div style = "width:100%;float:left;">
	                                        <%if(message!=null && !message.equals("")){ %>
	                                        <%=message %>
	                                        <% }
	                                          session.setAttribute("MESSAGE","");
	                                        %>
	                                    </div>
                                    <% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
	                                    <%-- <div id = "addLink" style="float:left;margin-right:45px;padding:9px 5px;display:<%=strDisplay%>;">
	                                        <a href="#" onclick="addManual();"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Manual</a>
	                                    </div> --%>
                                    <% } %>
                                      <%-- <div id="editLink" style="float:left;font-weight:bold;font-size:14px;margin:15px 0px 0px 5px;padding:9px 5px;color:#68AC3B;display:<%=editDisplay%>;">Edit Manual :</div> --%>
                                      <div id="addManual" style="float: left; width: 97%; margin: 5px 0px; margin-left:5px; padding: 5px;display: <%=(operation != null && operation.equals("E")) ? "block" : "none" %>;">
                                        <div style="float: left; width: 100%; margin: 5px 0px;">
                                            <%
											String manualId = (String)request.getAttribute("manualId");
                                           	if(operation != null && operation.equals("E") && manualId!=null && uF.parseToInt(manualId)>0) {
											%>
	                                            <s:action name="AddCompanyManual" executeResult="true">
	                                                <s:param name="pageFrom">MyHub</s:param>
	                                                <s:param name="E"><%=manualId %></s:param>
	                                                <s:param name="orgId"><%=(String)request.getAttribute("strOrg")%></s:param>
	                                            </s:action>
                                            <% } else { %>
	                                            <s:action name="AddCompanyManual" executeResult="true">
	                                                <s:param name="pageFrom">MyHub</s:param>
	                                                <s:param name="orgId"><%=(String)request.getAttribute("strOrg")%></s:param>
	                                            </s:action>
                                            <% } %>
                                        </div>
                                    </div> 
                                   
                                    <div id="list_Manual" style="float: left; width: 100%; margin: 5px 0px; ">
                                       <%
										  List<List<String>> reportList = (List<List<String>>)request.getAttribute("reportList");
										  String strManualIds = (String)request.getAttribute("strManualIds"); 
					
										  if(strManualIds == null ) strManualIds = new String();
										  if(reportList == null) reportList = new ArrayList<List<String>>();
										%>
										   <div>
										 <% 
					       					 if(reportList!=null && reportList.size()>0) {
												for(int i=0; i<reportList.size(); i++) {
													List<String> alInner = reportList.get(i);
													if(alInner==null) alInner = new ArrayList<String>();
													if(alInner!=null && alInner.size()>0) {
														String strColor = "#fff";
														if(operation!=null && operation.equals("E") && manualId!=null && uF.parseToInt(manualId)>0) {
															if(alInner.get(3)!=null && String.valueOf(alInner.get(3)).length()>0 && alInner.get(3).equals(manualId)) {
																strColor = "#efefef";
															}
														} else if((operation == null || !operation.equals("E")) && alInner.get(3)!=null && String.valueOf(alInner.get(3)).length()>0 && strManualId != null && alInner.get(3).equals(strManualId)) {
															strColor = "#efefef";
														}
													%>
														
												<% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
													<div id ="manualDiv_<%=alInner.get(3) %>" style="float:left; width:98%; margin:5px 7px 5px 8px; border-bottom:1px solid #efefef; background:<%=strColor%>">
														<div style="float:left;margin-left:3px;width:100%;">
														   <%-- <div style="float: left;">
																<a href="AddCompanyManual.action?orgId=<%=request.getAttribute("strOrg") %>&D=<%=alInner.get(3)%>&pageFrom=MyHub" class="del" onclick="return confirm('Are you sure you wish to delete this manual?')"> <i class="fa fa-trash" aria-hidden="true"></i> </a> 
																<a href="Hub.action?type=M&operation=E&manualId=<%=alInner.get(3)%>" onclick="editManual('<%=alInner.get(3) %>','<%=strManualIds %>');"class="edit_lvl"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
															</div> --%>
															<div style="float:left; margin-top:4px;"><%=alInner.get(5)%></div>
															<div style="float:left; margin-left:5px;">
															 	Title: <strong><%=alInner.get(1)%></strong>&nbsp;&nbsp;&nbsp;Organisation : <strong><%=alInner.get(4)%></strong>
															</div>
														</div> 
														
														<div style="float:left; margin-left:63px; width:92%;">
															<div style="float:left;">Status: <strong><span id="myDiv<%=i %>"><%=alInner.get(2)%></span></strong></div>
															<div style="float:left; margin-left:10px;">Last Updated :<strong><%=alInner.get(0)%></strong> </div>
															<div style="float:right; margin-right:5px;">
																<%if(alInner.get(6) != null && !alInner.get(6).equals("")) { %>
																	<a href="javascript:void(0)" onclick="viewManual('<%=alInner.get(3) %>','<%=strManualIds %>');event.preventDefault();" style="float:right">Preview Manual</a>
																<% } else if(alInner.get(7) != null && !alInner.get(7).equals("")){ %>
																	<% 
																	if(docRetriveLocation==null) {
																		manualDocPath =  IConstants.DOCUMENT_LOCATION+"/"+alInner.get(3)+"/"+alInner.get(7); // +"/"+sessionEmpId
																	} else {
																		manualDocPath = docRetriveLocation+IConstants.I_COMPANY_MANUAL+"/"+alInner.get(3)+"/"+alInner.get(7); //+"/"+sessionEmpId
																	}
																	%>
																	<%-- <a href="<%=manualDocPath %>" class="embed1" id="test">&nbsp;</a> --%>
																	<a href="<%=manualDocPath %>" target="_blank" style="float:right">View Manual</a>
																<% } %>
															</div>
														</div>
													</div>
											 	<% } else { %>
											 			<div id ="manualDiv_<%=alInner.get(3) %>" style="float:left; width:98%; margin:5px 7px 5px 7px; border-bottom:1px solid #efefef; background:<%=strColor%>">
															<div style="float:left;margin-left:3px;width:99%;">
																<div style="float:left; margin-left:5px;"><strong><%=alInner.get(1)%></strong></div>
															</div> 
															<div style="float:left; margin-left:3px; width:99%;">
																<div style="float:left; margin-left:5px;">Last Updated :<strong><%=alInner.get(0)%></strong> </div>
																<div style="float:right; margin-right:5px;">
																<%if(alInner.get(6) != null && !alInner.get(6).equals("")) { %>
																	<a href="javascript:void(0)" onclick="viewManual('<%=alInner.get(3) %>','<%=strManualIds %>');event.preventDefault();" style="float:right">Preview Manual</a>
																<% } else if(alInner.get(7) != null && !alInner.get(7).equals("")){ %>
																	<% 
																	if(docRetriveLocation==null) {
																		manualDocPath = IConstants.DOCUMENT_LOCATION +"/"+alInner.get(3)+"/"+alInner.get(7); //+"/"+sessionEmpId
																	} else {
																		manualDocPath = docRetriveLocation+IConstants.I_COMPANY_MANUAL+"/"+alInner.get(3)+"/"+alInner.get(7); //+"/"+sessionEmpId
																	}
																	%>
																	<a href="<%=manualDocPath %>" target="_blank" style="float:right">View Manual</a>
																	<%-- <a href="javascript:void(0);" onclick="viewManual('<%=alInner.get(3)%>','')" style="float:right">Preview Manual</a> --%>
																<% } %>
																</div>
															</div>
														</div>
											 		<% } %>
											<%		}
												}
					       				 	}
									  	  %>
		 						   	  </div>
                        		   </div>
                                    <% } %>
                                     	<div id="show_Manual" style="float: left; width:99%; display: <%=(operation != null && operation.equals("E")) ? "none" : "block" %>">
									        <% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
										        <div style="float:left;width:98%; margin:7px 0px 0px 7px;padding:15px 15px; background:#efefef; border-top:solid 1px #fefefe;border-bottom:solid 5px #ccc;">
										           <div class="manual_title">
										           	<%if(strTitle!=null && !strTitle.equals("")) { %>
													  <%=strTitle %>
													  <% } else { %>
													    Not Available...!
													    <% } %>
										            </div>
										             <div class="clr"></div>
										             <%
										             	String date = "Not Available.";
										             	if(strDate!=null && !strDate.equals("")) {
										             		date= strDate;
										             	}
										             %>
										            <div style="float:right; padding:0px 20px"><span style="font-style:italic; color:#666666">Last Updated on:</span><%=date%></div>
										        </div>
											<% } %>
											
									        <div class="clr"></div>
									        <div class="manual_body">
									          <%if(strBody != null && !strBody.equals("") && (manualDocPath == null || manualDocPath.equals(""))) { %>
									          		<%=strBody%>
									          <%} else if(manualDocPath != null && !manualDocPath.equals("")) {
										        	  if(flag) {%>
									          			<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;">
															<a href="<%=manualDocPath %>" class="embed" id="test">&nbsp;</a>
														</div>
									  				<% } else {	%>
														<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;height: 500px; background-color: #CCCCCC;">
															<div style="text-align: center; font-size: 24px; padding: 150px;">Preview not available</div>
														</div>
												    <% }%>
											    <% } %>
									        </div>
								        	<div class="clr"></div> 
	        							</div>
                                	</div>
                                
                                <% } else if(type!=null && type.equals("A")) { 
                                    //List<List<String>> noticeList = (List<List<String>>)request.getAttribute("noticeList");
                                    Map<String, List<String>> hmNotices = (Map<String, List<String>>) request.getAttribute("hmNotices");
                                    Map<String, String> hmNoticeIds = (Map<String, String>)request.getAttribute("hmNoticeIds");
                                    if(hmNotices==null){
                                    	hmNotices = new LinkedHashMap<String, List<String>>();
                                    }
                                    if(hmNoticeIds==null){
                                    	 hmNoticeIds = new LinkedHashMap<String, String>();
                                    }
                                    
                                    Set<String> setNotices = hmNoticeIds.keySet();
                                    Iterator<String> nit = setNotices.iterator();
                                    
                                    %>
                                <div id = "notices" style="float:left;width:100%;margin:2px 2px 1px 1px;" >
                                    <% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
	                                    <p><a href="javascript:void(0)" onclick="addAnnouncement();"><i class="fa fa-plus-circle"></i>Add Announcement</a></p>
                                    <% } %>
                                    <div id="allNoticesDiv" style="float: left; width: 100%; margin-top:-4px;">
                                        <input type="hidden" name="noticethideOffsetCnt" id="noticethideOffsetCnt" value="10" />
                                        <s:hidden name="lastNoticeId" id="lastNoticeId"/>
                                        <%	
                                            int noticeCount = 0;
                                            while(nit.hasNext()) {
                                            	String strNoticeId = nit.next();
                                            	List<String> noticeList = hmNotices.get(strNoticeId);
                                            	
                                            	if(noticeList == null) noticeList = new ArrayList<String>();
                                            	if(noticeList != null  && noticeList.size()>0 && !noticeList.equals("")){
                                            					
                                            		List<String> notice = noticeList;
                                            %>
                                        <div class="box-footer box-comments" id="mainNoticeDiv_<%=notice.get(0) %>" style="border-top: 1px solid #ECECEC;background: #FDFDFD;padding: 5px;">
								              <div class="box-comment" id="noticeDataDiv_<%=notice.get(0) %>">
								                <!-- User image -->
								                <img class="img-circle img-sm" src="userImages/avatar_photo.png" alt="User Image" style="width: 40px !important;height: 40px !important;">
								                <div class="comment-text" style="margin-left: 55px;">
								                      <span class="username" style="font-size: 14px;color: #0089B4;">
								                        <%=notice.get(8) %> <span style="font-weight:400;">has posted an Announcement of </span><%=notice.get(3) %>
								                        <span class="text-muted pull-right">
								                        	<% if((uF.parseToInt(strEmpId) == uF.parseToInt(notice.get(6)) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
                                                                <div style="float: left;">
                                                                    <a href="javascript:void(0);" onclick="editAndDeleteAnnouncement('<%=notice.get(0) %>', 'E');">
                                                                    <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
                                                                    </a>
                                                                    <a href="javascript:void(0);" onclick="editAndDeleteAnnouncement('<%=notice.get(0) %>', 'D');">
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
                                        
                                        <%	noticeCount++;
                                            }
                                            %>
                                        <% } %>
                                        <% if(noticeCount == 0) { %>
                                        	<div class="nodata msg" style="margin-left: 25px; width: 95%;"><span>No announcements available.</span></div>
                                        <% } %>
                                    </div>
                                    <% if(noticeCount==10 ) { %>
	                                    <div id="loadingNoticesDiv" style="display: none; float: left; width: 100%; text-align: center;"> <img src="images1/new_loading.gif"> </div>
	                                    <div id="loadMoreNoticesDiv" style="float: left; width: 100%; text-align: center;"> <a href="javascript:void(0)" onclick="loadMoreNotices()">load more ...</a> </div>
                                    <% } %>
                                </div>
                                <% } %>
                            </div>
                        </div>
					</div>
				</div>
            
            </section>
	    </div>
	</section>
	<% } %>
            
		<% if(type!=null && type.equals("E") && operation != null && operation.equals("A")) { %>
          	<div style="float: left; width: 100%; padding-bottom: 5px;">
                <s:form name="frm_event" id="frm_event" action="AddUpdateViewHubContent" theme="simple" method="Post" cssClass="formcss" enctype="multipart/form-data">
                    <s:hidden id="type" name="type"></s:hidden>
                    <table class="table table_no_border form-table">
                    	<tr>
                    		<td>Title:<sup>*</sup></td>
                    		<td colspan="2"><s:textfield  name="strEventName" id="strEventName" cssStyle="font-size: 11px;" cssClass="validateRequired" ></s:textfield></td>
                    	</tr>
                    	<tr>
                    		<td>Event:<sup>*</sup></td>
                    		<td colspan="2"><s:textarea rows="3" name="strEventdesc" id="strEventdesc" cssClass="validateRequired" cssStyle="font-size: 11px; width: 78%;" ></s:textarea></td>
                    	</tr>
                    	<tr>
                    		<td>Share with:<sup>*</sup></td>
                    		<td colspan="2"><s:select name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName" required="true"  multiple="true" value="levelvalue" cssClass="validateRequired"> </s:select></td>
                    	</tr>
                    	<tr>
                    		<td></td>
                    		<td>Start Date:<sup>*</sup><br/><s:textfield name="strStartDate" id="strStartDate" cssClass="validateRequired" cssStyle="width:85px;" ></s:textfield></td>
                    		<td>End Date:<sup>*</sup><br/><s:textfield name="strEndDate" id="strEndDate"  cssClass="validateRequired" cssStyle="width:85px"></s:textfield></td>
                    	</tr>
                    	<tr>
                    		<td></td>
                    		<td>Start Time:<sup>*</sup><br><input type="text" id="startTime" name="startTime" style="width:60px;" class="validateRequired startTime"/></td>
                    		<td>End Time:<sup>*</sup><br><input type="text" id="endTime" name="endTime" style="width:60px;" class="validateRequired endTime"/></td>
                    	</tr>
                    	<tr>
                    		<td>Location:<sup>*</sup></td>
                    		<td colspan="2"><s:textfield name="strLocation" id="strLocation" cssClass="validateRequired"></s:textfield></td>
                    	</tr>
                    	<tr>
                    		<td></td>
                    		<td colspan="2">
                    			<img height="62" width="70" class="lazy" id="eventImage" style="border: 1px solid #CCCCCC;" src="userImages/event_icon.png" data-original="/" /> <!-- userImages/avatar_photo.png -->
                                <input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz" id="strEventImage" name="strEventImage"  size="5" style="font-size: 10px; height: 22px;margin-top:10px; vertical-align: top;" onchange="readImageURL(this, 'eventImage');">
                    		</td>
                    	</tr>
                    	<tr>
                    		<td></td>
                    		<td colspan="2"><s:submit name="eventPost" cssClass="btn btn-primary" cssStyle="margin-top:10px;" value="Post" /></td>
                    	</tr>
                    </table>
                    <script>
                    $(function () {
                    	$("input[name='eventPost']").click(function(){
                    		$(".validateRequired").prop('required',true);
                    	});
                    	
                    	var date_yest = new Date();
                        var date_tom = new Date();
                        date_yest.setHours(0,0,0);
                        date_tom.setHours(23,59,59); 
                       
                    	$('.startTime').datetimepicker({ 
                    		format: 'HH:mm',
                    		minDate: date_yest,
                    		defaultDate: date_yest
                        }).on('dp.change', function(e){ 
                        	$('.endTime').data("DateTimePicker").minDate(e.date);
                        });
                    	
                    	$('.endTime').datetimepicker({
                    		format: 'HH:mm',
                    		maxDate: date_tom,
                    		defaultDate: date_tom
                        }).on('dp.change', function(e){ 
                        	$('.startTime').data("DateTimePicker").maxDate(e.date);
                        });
                    	
                    	$("#strLevel").multiselect().multiselectfilter();
                    });
                    </script>
                </s:form>
            </div>
		<% } else if(type!=null && type.equals("Q") && operation != null && operation.equals("A")) { %>            
			<s:form name="frm_quote" id="frm_quote" action="AddUpdateViewHubContent" theme="simple" method="Post" cssClass="formcss">
				<s:hidden id ="type" name="type"></s:hidden>
				<table class="table table_no_border form-table">
					<tr>
						<td>Quote By:<sup>*</sup></td>
						<td><s:textfield  name="strQuoteBy" id="strQuoteBy" cssClass="validateRequired" ></s:textfield></td>
					</tr>
					<tr>
						<td>Quote:<sup>*</sup></td>
						<td><s:textarea rows="3" name="strQuotedesc" id="strQuotedesc" cssClass="validateRequired" ></s:textarea></td>
					</tr>
					<tr>
						<td></td>
						<td><s:submit name="quotePost" cssClass="btn btn-primary" value="Post" /></td>
					</tr>
				</table>
			</s:form>
    	<% } else if(type!=null && type.equals("A") && operation != null && operation.equals("A")) { %>
			<s:form name="frm_notice" id="frm_notice" action="AddUpdateViewHubContent" theme="simple" method="Post" cssClass="formcss" enctype="multipart/form-data">
				<s:hidden id ="type" name="type"></s:hidden>
				<table class="table table_no_border form-table">
					<tr>
						<td>Title:<sup>*</sup></td>
						<td colspan="2"><s:textfield  name="heading" id="heading" cssClass="validateRequired" ></s:textfield></td>
					</tr>
					<tr>
						<td>Notice:<sup>*</sup></td>
						<td colspan="2"><s:textarea rows="3" name="content" id="content"  cssClass="validateRequired"></s:textarea></td>
					</tr>
					<tr>
						<td></td>
						<td>Start Date:<sup>*</sup><br><s:textfield name="displayStartDate" id="displayStartDate"  cssClass="validateRequired" /></td>
						<td>End Date:<sup>*</sup><br><s:textfield name="displayEndDate" id="displayEndDate"  cssClass="validateRequired" /></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><s:radio label="ispublish" name="ispublish" list="#{'1':'Publish','2':'Unpublish'}" value="2" /></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><s:submit name="noticePost" cssClass="btn btn-primary" value="Post" /></td>
					</tr>
				</table>
			</s:form>
			<script>
				$(document).ready( function () {
					$("input[name='noticePost']").click(function(){
						$(".validateRequired").prop('required',true);
					});
				});
			</script>
		<% } else if(type!=null && type.equals("FAQ") && operation != null && operation.equals("A")) { %>
		<script type="text/javascript">
			function selectElements(strSectionId) {
				if( strSectionId == 0) {
					document.getElementById("otherSectionTR").style.display = "table-row";
				} else {
					document.getElementById("otherSectionTR").style.display = "none";
				}
			}
		</script>
			<s:form name="frm_FAQ" id="frm_FAQ" action="AddUpdateViewHubContent" theme="simple" method="Post" cssClass="formcss" enctype="multipart/form-data">
	    		<s:hidden id ="type" name="type"></s:hidden>
                <table class="table form-table table_no_border">
                  	<tr>
                  		 <td>Section:<sup>*</sup></td>
                  		 <td>
                             <s:select name="strfaqSection" id="strfaqSection" listKey="sectionId" theme="simple" cssClass="validateRequired" listValue="sectionName" headerKey="" 
                               headerValue="Select Section" list="faqSectionList" key="" onchange="selectElements(this.value)" />
                               <span class="hint">Select section from the list.<span class="hint-pointer">&nbsp;</span></span>
                        </td>
                  	
                  	</tr>
                  	 <tr id ="otherSectionTR" style="display: none">
                        <td>Section Name:<sup>*</sup></td>
                          <td><input type ="text" name="strSection" id="strSection" class="validateRequired" value = ""/></td>
                    </tr>
                    <tr>
                        <td>Question:<sup>*</sup></td>
                          <td><input type ="text" name="strQuestion" id="strQuestion" class="validateRequired" value = "<%=uF.showData((String)request.getAttribute("faq_question"), "")%>"/></td>
                    </tr>
                    <tr>
                        <td>Answer:<sup>*</sup></td>
	                   <td><textarea rows="3" name="strAnswer" id="strAnswer" class="validateRequired" ><%=uF.showData((String)request.getAttribute("faq_answer"), "")%></textarea></td>
                   </tr>
                   <tr>
                        <td></td>
                        <td colspan="2" align="center"><s:submit name="faqPost" id="faqPost" cssClass="btn btn-primary" value="Submit"></s:submit></td>
                    </tr>
                </table>
			</s:form>
    	<% } %>


<script type="text/javascript">

$("#frm_event").submit(function(e) {
	e.preventDefault();
	var type = document.getElementById("type").value;
	var form_data = $("form[name='frm_event']").serialize();
 	$.ajax({
 		type: 'POST',
		url : "AddUpdateViewHubContent.action?eventPost=Post",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		}, 
		error : function(err) {
			$.ajax({ 
				url: 'AddUpdateViewHubContent.action?type='+type,
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
	});
 	$("#modalInfo").hide();
});


$("#frm_quote").submit(function(e) {
	e.preventDefault();
	var type = document.getElementById("type").value;
	var form_data = $("form[name='frm_quote']").serialize();
 	$.ajax({
 		type: 'POST',
		url : "AddUpdateViewHubContent.action?quotePost=Post",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		}, 
		error : function(err) {
			$.ajax({ 
				url: 'AddUpdateViewHubContent.action?type='+type,
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
	});
 	$("#modalInfo").hide();
});


$("#frm_notice").submit(function(e) {
	e.preventDefault();
	var type = document.getElementById("type").value;
	var form_data = $("form[name='frm_notice']").serialize();
 	$.ajax({
 		type: 'POST',
		url : "AddUpdateViewHubContent.action?noticePost=Post",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		}, 
		error : function(err) {
			$.ajax({ 
				url: 'AddUpdateViewHubContent.action?type='+type,
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
	});
 	$("#modalInfo").hide();
});


$("#frm_FAQ").submit(function(e) {
	e.preventDefault();
	var type = document.getElementById("type").value;
	var form_data = $("form[name='frm_FAQ']").serialize();
 	$.ajax({
 		type: 'POST',
		url : "AddUpdateViewHubContent.action?faqPost=Post",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		}, 
		error : function(err) {
			$.ajax({ 
				url: 'AddUpdateViewHubContent.action?type='+type,
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
	});
 	$("#modalInfo").hide();
});

$("#allQuotesDiv").scroll(function(){
	   debugger;
	   
	   var elem = $("#allQuotesDiv");
	    if (elem[0].scrollHeight - elem.scrollTop() == elem.outerHeight())
	    {
	    	loadMoreQuotes();
	    
	    }
});

function showCustomDropdown(id) {
	   debugger;
	var drpDwnId = "listCustomDropdown_"+id;
    document.getElementById(drpDwnId).classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
    if (!event.target.matches('.fa.fa-ellipsis-h')) {
        var dropdowns = document.getElementsByClassName("customDropdown-content");
        var i;
        for (i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
}

$(window).bind('mousewheel DOMMouseScroll', function(event){
    
    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
        // scroll up
        if($(window).scrollTop() == 0 && $("#allQuotesDiv").scrollTop() != 0){
        	$("#allQuotesDiv").scrollTop($("#allQuotesDiv").scrollTop() - 30);
        }
    }
    else {
        // scroll down
        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#allQuotesDiv").scrollTop($("#allQuotesDiv").scrollTop() + 30);
   		}
    }
});

$(window).keydown(function(event){
   
		if(event.which == 40 || event.which == 34)
		{
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#allQuotesDiv").scrollTop($("#allQuotesDiv").scrollTop() + 50);
			}
		}
		else if(event.which == 38 || event.which == 33)
		{
	   if($(window).scrollTop() == 0 && $("#allQuotesDiv").scrollTop() != 0){
    		$("#allQuotesDiv").scrollTop($("#allQuotesDiv").scrollTop() - 50);
    	}
		}
});


</script>