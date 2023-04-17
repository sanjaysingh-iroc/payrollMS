<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib prefix="s" uri="/struts-tags" %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.min.js"> </script>
<style> 
    .imageContent { position: relative; width:100%; height:180px; }
    .img1 {width: 100% !important;height: 100% !important;margin-top: 0px !important; margin-left: 0px !important; }
    
    .list .nav-pills li a {
    border-radius: 10px 10px 0px 0px !important;
    }
    .list .nav>li>a {
    position: relative;
    display: block;
    padding: 5px 15px !important;
    }
    
</style>
<script type="text/javascript">
    $(document).ready(function(){
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    	});
    	$("#f_wlocation").multiselect().multiselectfilter();
    });
   

    function approveOrDenyBookIssueRequest(value, bookIssuedId, bookId, bookName) {
    
    	var titl = "Approve";
    	if(value == 'D') {
    		titl = 'Deny';
    	}
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Book Issue Request '+titl);
    	 $.ajax({
                url : 'ApproveOrDenyBookIssueRequest.action?bookId='+bookId+'&bookIssuedId='+bookIssuedId+'&operation='+value,
                cache : false,
                success : function(data) {
                	$(dialogEdit).html(data);
                }
            });
    }
    
    
    var dialogEditReturn = '#ReturnIssuedBookPopup';
    function returnIssuedBook(value, bookIssuedId, bookId, bookName) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Return Issued Book');
    	 $.ajax({
                url : 'ReturnIssuedBook.action?bookId='+bookId+'&bookIssuedId='+bookIssuedId+'&operation='+value,
                cache : false,
                success : function(data) {
                	//alert("data==>"+data);
                	$(dialogEdit).html(data);
                }
            });
    }
    
    
    function bookPurchase(bookId, quantity, bookName) {
    //alert("value==>"+value+"==>bookId==>"+bookId+"==>quantity==>"+quantity);
    	if(quantity == 0) {
    		alert("Out of stock.");
    	} else {
    		var dialogEdit = '.modal-body';
    		 $(dialogEdit).empty();
    		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		 $("#modalInfo").show();
    		 $(".modal-title").html('Want to Purchase Book');
    		 $.ajax ({
                    url : 'PurchaseBook.action?bookId='+bookId,
                    cache : false,
                    success : function(data) {
                	      $(dialogEdit).html(data);
                    }
                });
    	}
    }
    
    var dialogEditIssue = '#IssueBookPopup';
    function bookOnRent(bookId, quantity, bookName) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Want to Rent Book');
    	 $.ajax({
                url : 'IssueBook.action?bookId='+bookId+'&availQuantity='+quantity+'&bookName='+bookName,
                cache : false,
                success : function(data) {
                	$(dialogEdit).html(data);
                }
            });
     }
    
    
    function reviewOnBook(bookId, quantity, bookName) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Rate Book ');
    	 $.ajax({
                url : 'RateBook.action?bookId='+bookId+'&from=BR',
                cache : false,
                success : function(data) {
            	      $(dialogEdit).html(data);
                }
            });
    }
    
    
    var dialogEditAddBook = '#AddBookPopup';
    function addNewBook() {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Add New Book ');
    	 $.ajax({
                url : 'AddBook.action',
                cache : false,
                success : function(data) {
                	//alert("data==>"+data);
                    $(dialogEdit).html(data);
             }
            });
    }
    
    var dialogEditEditBook = '#EditBookPopup';
    function editBook(bookId, bookName) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Edit Book');
    	 $.ajax({
                url : 'AddBook.action?bookId='+bookId+'&operation=E',
                cache : false,
                success : function(data) {
                //	alert("data==>"+data);
                	$(dialogEdit).html(data);
                }
            });
    }
    
    
    function deleteBook(bookId, bookName,divId) {
     /* if(confirm("Are you sure, you wish to delete this Book?")) {
    	 
    	var action='Library.action?bookId='+bookId+'&operation=D';
    	
 //   	alert("bookId==>"+bookId+"==>divId==>"+divId+"==>action==>"+action);
    	getContent(divId, action);
    	window.location = 'Library.action';
     } */
     

		var xmlhttp;
		if (window.XMLHttpRequest) {
	         // code for IE7+, Firefox, Chrome, Opera, Safari
	         xmlhttp = new XMLHttpRequest();
 		}
		
	    if (window.ActiveXObject) {
	        // code for IE6, IE5
	    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    
	    if (xmlhttp == null) {
	            alert("Browser does not support HTTP Request");
	            return;
	    } else {
	    		var currPage = document.getElementById("currPage").value;
	    		var minLimit = document.getElementById("minLimit").value;
	    				if(confirm("Are you sure, you wish to delete this Book? ")) {
        			var xhr = $.ajax({
 	                url : 'Library.action?bookId='+bookId+'&operation=D',
 	                cache : false,
 	                success : function(data) {
 	                	//alert("data ===>> " + data);
 	                	//document.getElementById(divId).innerHTML = '';
 	                	window.location = 'Library.action?currPage='+currPage+'&minLimit='+minLimit;
					  }
					});
				}
			}
	}
    
    
    /* } else if(value == 2) {
    var xhr = $.ajax({
       url : 'AddBook.action?bookId='+bookId+'&operation=D',
       cache : false,
       success : function(data) {
       	   document.getElementById("trBook_"+bookId).style.display="none";        	
       }
    });
    } */
    
    
    function approveOrDenyBookPurchase(value, bookId, purchaseId, bookName) {
    	
    	var titl = "Approve";
    	if(value == 'D') {
    		titl = 'Deny';
    	}
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Book Purchase Request '+titl);
    	 $.ajax({
                url : 'ApproveOrDenyBookPurchase.action?bookId='+bookId+'&bookPurchaseId='+purchaseId+'&operation='+value,
                cache : false,
                success : function(data) {
                	$(dialogEdit).html(data);
    			}
    		});
    }
    
    
    function viewReviews(bookId) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Reviews');
    	 $.ajax({
                url : 'RateBook.action?bookId='+bookId+'&operation=VIEW',
                cache : false,
                success : function(data) {
                	$(dialogEdit).html(data);
                }
            });
    }
    
    
    function loadMoreBooks(currPage, minLimit, dataType) {
    	document.frmLibrary.currPage.value = currPage;
    	document.frmLibrary.minLimit.value = minLimit;
    	document.frmLibrary.submit();
    }
    
    function submitForm(type) {
		if(type == '1') {
			var f_org = document.getElementById("f_org").value;
			window.location = 'Library.action?f_org='+f_org;	
			
		} else {
			document.frmLibrary.submit();
		}
	}
    
</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Library" name="title"/>
    </jsp:include> --%>
<%
    String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
    String dataType = (String)request.getAttribute("dataType");
    String bookPageCount = (String)request.getAttribute("bookPageCount");
    
       UtilityFunctions uF = new UtilityFunctions();
    String sbData = (String) request.getAttribute("sbData");
    String strSearchJob = (String) request.getAttribute("strSearchJob");
    Map<String,List<String>> hmPurchaseBookDetails = (Map<String,List<String>>)request.getAttribute("hmPurchaseBookDetails");
    Map<String,List<String>> hmBookDetails = (Map<String,List<String>>)request.getAttribute("hmBookDetails");
    
    if(hmBookDetails == null) hmBookDetails = new HashMap<String,List<String>>();
    if(hmPurchaseBookDetails == null ) hmPurchaseBookDetails = new HashMap<String,List<String>>();
    
    Map<String,List<String>> hmBookIssuesDetails = (Map<String,List<String>>)request.getAttribute("hmBookIssuesDetails");
    Map<String,List<String>> hmReturnBookDetails = (Map<String,List<String>>)request.getAttribute("hmReturnBookDetails");
    
    if(hmBookIssuesDetails == null) hmBookIssuesDetails = new HashMap<String,List<String>>();
    if(hmReturnBookDetails == null ) hmReturnBookDetails = new HashMap<String,List<String>>(); 
    
    Map<String,List<String>> hmEmpPurchasedBookDetails = (Map<String,List<String>>)request.getAttribute("hmEmpPurchasedBookDetails");
    if(hmEmpPurchasedBookDetails == null ) hmEmpPurchasedBookDetails = new HashMap<String,List<String>>();
    
    Map<String,List<String>> hmEmpIssuedBookDetails = (Map<String,List<String>>)request.getAttribute("hmEmpIssuedBookDetails");
    if(hmEmpIssuedBookDetails == null ) hmEmpIssuedBookDetails = new HashMap<String,List<String>>();
    %>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                        <s:form name="frmLibrary" id="frmLibrary" action="Library" theme="simple">
                            <s:hidden name="dataType" id="dataType"/>
                            <s:hidden name="currPage" id="currPage"/>
                            <s:hidden name="minLimit" id="minLimit"/>
                            <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                            <div class="box box-default collapsed-box" style="margin-top: 10px;">
				                <div class="box-header with-border">
				                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
				                    <div class="box-tools pull-right">
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				                	<div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter" aria-hidden="true"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Organization</p>
												<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" 
                                                onchange="submitForm('1');" list="organisationList" />
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Location</p>
												<s:select theme="simple" name="f_wlocation" id="f_wlocation" listKey="wLocationId"
                                                listValue="wLocationName" list="wLocationList" multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">&nbsp;</p>
												<s:submit value="Submit" cssClass="btn btn-primary"/>
											</div>
										</div>
									</div>
				                </div>
				                <!-- /.box-body -->
				            </div>
                            <% } %>	
                            <div class="clr alignCenter">
                                    <span>Search:</span>
                                    <input type="text" class="form-control" id="strSearchJob" name="strSearchJob" value="<%=uF.showData(strSearchJob,"") %>"/>
                                    <input type="submit" value="Search"  class="btn btn-primary" >
                                <script>
                                    $( "#strSearchJob" ).autocomplete({
                                    	source: [ <%=uF.showData(sbData,"") %> ]
                                    });
                                </script>
                            </div>
                        </s:form>
                        <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                        <div class="row row_without_margin">
                        	<div class="col-lg-12">
                            	<input type="button" onclick="addNewBook()" value="Add New Book" class="btn btn-primary pull-right">
                        	</div>
                        </div>
                        <%}%>
                        <div class="clr margintop20">
                            <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { 
                                String issuedCnt = (String)request.getAttribute("issuedCnt");
                                String issueReqCnt = (String)request.getAttribute("issueReqCnt");
                                String purchaseReqCnt = (String)request.getAttribute("purchaseReqCnt");
                                %>
                            <div class="nav-tabs-custom">
                                <% if(dataType == null || dataType.equals("B")) { %>
                                <ul class="nav nav-tabs">
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=B'" data-toggle="tab">Books</a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=PR'" data-toggle="tab">Purchase Requests<span class="label label-primary tab-count"><%=purchaseReqCnt %></span></a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=IR'" data-toggle="tab">Issue Requests<span class="label label-primary tab-count"><%=issueReqCnt %></span></a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=IB'" data-toggle="tab">Issued Books<span class="label label-primary tab-count"><%=issuedCnt %></span></a></li>
								</ul>
                                <% } else if(dataType != null && dataType.equals("PR")) { %>
                                <ul class="nav nav-tabs">
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=B'" data-toggle="tab">Books</a></li>
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=PR'" data-toggle="tab">Purchase Requests<span class="label label-primary tab-count"><%=purchaseReqCnt %></span></a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=IR'" data-toggle="tab">Issue Requests<span class="label label-primary tab-count"><%=issueReqCnt %></span></a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=IB'" data-toggle="tab">Issued Books<span class="label label-primary tab-count"><%=issuedCnt %></span></a></li>
								</ul>
                                <% } else if(dataType != null && dataType.equals("IR")) { %>
                                <ul class="nav nav-tabs">
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=B'" data-toggle="tab">Books</a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=PR'" data-toggle="tab">Purchase Requests<span class="label label-primary tab-count"><%=purchaseReqCnt %></span></a></li>
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=IR'" data-toggle="tab">Issue Requests<span class="label label-primary tab-count"><%=issueReqCnt %></span></a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=IB'" data-toggle="tab">Issued Books<span class="label label-primary tab-count"><%=issuedCnt %></span></a></li>
								</ul>
                                <% } else if(dataType != null && dataType.equals("IB")) { %>
                                <ul class="nav nav-tabs">
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=B'" data-toggle="tab">Books</a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=PR'" data-toggle="tab">Purchase Requests<span class="label label-primary tab-count"><%=purchaseReqCnt %></span></a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=IR'" data-toggle="tab">Issue Requests<span class="label label-primary tab-count"><%=issueReqCnt %></span></a></li>
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=IB'" data-toggle="tab">Issued Books<span class="label label-primary tab-count"><%=issuedCnt %></span></a></li>
								</ul>
                                <% } %>	
                            </div>
                            
                            <% } else if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) { 
                                String empIssuedCnt = (String)request.getAttribute("empIssuedCnt");
                                String empPurchasedCnt = (String)request.getAttribute("empPurchasedCnt");
                                %>
                            <div class="nav-tabs-custom">
                                <% if(dataType == null || dataType.equals("B")) { %>
                                <ul class="nav nav-tabs">
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=B'" data-toggle="tab">Books</a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=EMPPB'" data-toggle="tab">Purchased Books<span class="label label-primary tab-count"><%=empPurchasedCnt %></span></a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=EMPIB'" data-toggle="tab">Issued Books<span class="label label-primary tab-count"><%=empIssuedCnt %></span></a></li>
								</ul>
                                <% } else if(dataType != null && dataType.equals("EMPPB")) { %>
                                <ul class="nav nav-tabs">
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=B'" data-toggle="tab">Books</a></li>
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=EMPPB'" data-toggle="tab">Purchased Books<span class="label label-primary tab-count"><%=empPurchasedCnt %></span></a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=EMPIB'" data-toggle="tab">Issued Books<span class="label label-primary tab-count"><%=empIssuedCnt %></span></a></li>
								</ul>
                                <% } else if(dataType != null && dataType.equals("EMPIB")) { %>
                                <ul class="nav nav-tabs">
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=B'" data-toggle="tab">Books</a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=EMPPB'" data-toggle="tab">Purchased Books<span class="label label-primary tab-count"><%=empPurchasedCnt %></span></a></li>
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Library.action?dataType=EMPIB'" data-toggle="tab">Issued Books<span class="label label-primary tab-count"><%=empIssuedCnt %></span></a></li>
								</ul>
                                <% } %>	
                            </div>
                            <% } %>
                        </div>
                        <div style="float:left;width:100%;">
                            <% if(dataType != null && dataType.equals("B")) { %>
                            <%
                                if(hmBookDetails != null  && hmBookDetails.size()>0) {
                                	Set bookSet = hmBookDetails.keySet();	
                                	Iterator<String> it = bookSet.iterator();
                                	while(it.hasNext()) {
                                 	  String bookId = it.next();
                                 	  List<String> bookDetailsList = (ArrayList<String>)hmBookDetails.get(bookId);
                                 	  if(bookDetailsList == null) bookDetailsList = new ArrayList<String>();
                                 	  if(bookDetailsList != null && bookDetailsList.size()>0) {
                                 		  String strOpacity = "";
                                 		 if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) {
                                 		  if(bookDetailsList.get(20) != null && uF.parseToInt(bookDetailsList.get(20)) <= 0) {
                                 			 strOpacity = "0.5";
                                 		  }
                                 	     }
                                 %>
                            <div id="Book_<%=bookId %>" style="float:left; opacity:<%=strOpacity%>;width:98%; min-height:250px; padding:10px 10px 10px 10px;">
                                <div style="float:left; width:20%;">
                                    <div style ="float:left; width:100%; height:180px;">
                                        <div class="imageContent"><%=bookDetailsList.get(16)%></div>
                                    </div>
                                </div>
                                <div style="float:left; min-height: 180px; width:77%; margin-left:10px; color:gray; padding: 10px; border: 1px solid #EBEBEB;">
                                    <div style="float:left;width:100%; font-weight: bold; font-size: 14px; margin: 2px;">
                                        <%=bookDetailsList.get(1) %>
                                        <div id="bookRatingDiv_<%=bookId %>" style="float: right;">
                                            <span id="starPrimaryS_<%=bookId %>" style="float: left; margin-left: 5px; line-height: 12px;"></span>
                                            <script type="text/javascript">
                                                $('#starPrimaryS_<%=bookId%>').raty({
                                                                readOnly: true,
                                                                start:	<%=bookDetailsList.get(18) %>,
                                                                half: true,
                                                                targetType: 'number'
                                                });
                                            </script>
                                            <span style="float: left;"><% if(uF.parseToInt(bookDetailsList.get(19))>0) { %><%=bookDetailsList.get(18) %><% } %>
                                            <% if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE) && bookDetailsList.get(17) != null && bookDetailsList.get(17).equals("T")) { %>
                                            <a onclick="reviewOnBook('<%=bookId %>')" href="javascript:void(0)"><img src="images1/icons/popup_arrow.gif" style="width: 9px; margin-left: 3px;"></a>
                                            <% } %>	
                                            <% if(uF.parseToInt(bookDetailsList.get(19))>0) { %>
                                            (<a href="javascript:void(0);" style="font-weight: normal;" onclick="viewReviews('<%=bookId %>')"><%=bookDetailsList.get(19) %> Reviews</a>)
                                            <% } %>
                                            </span>
                                            <% 
	                                            double dblRemainQntyPercent = 0;
	            								if(bookDetailsList.get(20) != null && bookDetailsList.get(0)!=null && uF.parseToInt(bookDetailsList.get(0))>0) {
	            									dblRemainQntyPercent = (uF.parseToInt(bookDetailsList.get(20))/uF.parseToInt(bookDetailsList.get(0))) * 100;
	            								}
                                                String bgColor = "";
                                                if(dblRemainQntyPercent <= 33.33) {
                                                	bgColor = "#F35B5B";
                                                } else if(dblRemainQntyPercent > 33.33 && dblRemainQntyPercent <= 66.66) {
                                                	bgColor = "#EEEE27";
                                                } else if(dblRemainQntyPercent > 66.66) {
                                                	bgColor = "#4CC54C";
                                                }
                                                %>
                                            <span style="float: left; margin-left: 25px; background-color: <%=bgColor %>; background-color: #db5c50;color: white; padding: 0px 3px;" title="Available Quantity"><%=uF.parseToInt(bookDetailsList.get(20)) %>/<%=uF.parseToInt(bookDetailsList.get(0)) %></span>
                                        </div>
                                    </div>
                                    <%-- <div style="float:left;width:100%; margin: 2px; font-family: Effra;">By <%=bookDetailsList.get(2) %></div>
                                        <div style="float:left;width:100%; margin: 2px; font-family: Libre Baskerville Regular;"><%=bookDetailsList.get(6) %></div>
                                        <div style="float:left;width:100%; margin: 2px; font-family: Libre Baskerville Italic;">About the Book: <%=bookDetailsList.get(7) %></div>
                                        <div style="float:left;width:100%; margin: 2px; font-family: Libre Baskerville Bold;">Published by '<%=bookDetailsList.get(3)%>' published on '<%=bookDetailsList.get(4) %>'</div> --%>
                                    <div style="float:left;width:100%; margin: 2px;">By <%=bookDetailsList.get(2) %></div>
                                    <div style="float:left;width:100%; margin: 2px;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=bookDetailsList.get(6) %></span></div>
                                    <div style="float:left;width:100%; margin: 2px;">About the Book: <%=bookDetailsList.get(7) %></div>
                                    <div style="float:left;width:100%; margin: 2px;">Published by '<%=bookDetailsList.get(3)%>' published on '<%=bookDetailsList.get(4) %>'</div>
                                    <div style="float:left;width:100%; margin-top: 16px;">
                                        <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                                        <input type="button" class="btn btn-primary" style="margin:0px 0px 0px 5px; float: left;" value="Edit" onclick="editBook('<%=bookId %>', '<%=bookDetailsList.get(1) %>');"/>
                                        <% if(bookDetailsList.get(21) != null && bookDetailsList.get(21).equals("T")) { %>
                                        <input type="button" class="btn btn-danger" style="margin:0px 0px 0px 25px; float: left;" value="Delete" onclick="alert('You can not delete this book, since it is in use!');"/>
                                        <% } else { %>
                                        <input type="button" class="btn btn-danger" style="margin:0px 0px 0px 25px; float: left;" value="Delete" onclick="deleteBook('<%=bookId %>', '<%=bookDetailsList.get(1) %>','Book_<%=bookId%>');"/>
                                        <% } %>
                                        <% } else if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) {
                                        	   
                                        		if(bookDetailsList.get(20) != null && uF.parseToInt(bookDetailsList.get(20)) > 0){
                                        	%>
                                        			<input type="button" class="btn btn-primary" style="margin:0px 0px 0px 5px;float: left;" value="Want to Rent" onclick="bookOnRent('<%=bookId %>', '<%=bookDetailsList.get(0) %>', '<%=bookDetailsList.get(1) %>');"/>
                                       				<input type="button" class="btn btn-primary" style="margin:0px 0px 0px 25px;float: left;" value="Want to Purchase" onclick="bookPurchase('<%=bookId %>', '<%=bookDetailsList.get(0) %>', '<%=bookDetailsList.get(1) %>');"/>
                                      		 <% } else { %>
                                      				<span style="margin:0px 0px 0px 5px;float:left;color:red;">Out of stock.</span>
                                      	<% }
                                        }%>
                                    </div>
                                </div>
                            </div>
                            <script type="text/javascript">
                                var srcImg = $(".img1");
                                     var newImage = new Image();
                                     var container = $(".imageContent");
                                    // alert("container==>"+container)
                                     newImage.src = srcImg.attr("src");
                                     var imageWidth = newImage.width;
                                     var imageHeight = newImage.height;
                                     //alert("imageWidth ===>> " + imageWidth + "container.width() ===>> " + container.width());
                                     if(imageWidth > container.width()) {
                                     	imageWidth = container.width();
                                     }
                                     if(imageWidth == 0) {
                                     	imageWidth = container.width();
                                     }
                                     //alert("imageHeight ===>> " + imageHeight + "container.height() ===>> " + container.height());
                                     if(imageHeight > container.height()) {
                                     	imageHeight = container.height();
                                     }
                                     if(imageHeight == 0) {
                                     	imageHeight = container.height();
                                     }
                                     var marginTop = -Math.abs(imageHeight / 2);
                                     var marginLeft = -Math.abs(imageWidth / 2);
                                     //alert("marginTop ===>> " + marginTop + "marginLeft ===>> " + marginLeft);
                                     
                                     $(".img1").css( {"margin-top": marginTop + "px", "margin-left": marginLeft + "px" });
                                     $(".img1").css( {"width": imageWidth + "px", "height": imageHeight +"px" });
                                     //alert("After");
                            </script>
                            <% } %>
                            <% } %>
                            <% } %>
                            <% if(hmBookDetails == null || hmBookDetails.size()==0) { %>
                            <div class="msg nodata"> 
                                <span>No books available.</span>  
                            </div>
                            <% } %>
                            <% } else if(dataType!=null && dataType.equals("PR")) { %>
                            <%
                                if(hmPurchaseBookDetails != null  && hmPurchaseBookDetails.size()>0) {
                                	int count = 1;
                                	Iterator<String> pit = hmPurchaseBookDetails.keySet().iterator();
                                	while(pit.hasNext()) {
                                 	  String bookPurchaseId = pit.next();
                                 	  List<String> purchaseBookList = (ArrayList<String>)hmPurchaseBookDetails.get(bookPurchaseId);
                                 	  if(purchaseBookList == null) purchaseBookList = new ArrayList<String>();
                                 	  if(purchaseBookList != null && purchaseBookList.size()>0 ) {
                                 		 String availQuantity = (String) purchaseBookList.get(18);
                                 		 String strOpacity="";
                                 		 if(availQuantity !=null  && (uF.parseToInt(availQuantity)) <= 0) {
                                 			strOpacity = "0.5";
                                 		 }
                                 %>
                            <div id="PBook_<%=bookPurchaseId %>" style="float:left; width:98%; opacity:<%=strOpacity%>;min-height:250px; padding:10px 10px 10px 10px;">
                                <div style="float:left; width:20%;">
                                    <div style ="float:left; width:100%; height:180px;">
                                        <div class="imageContent"><%=purchaseBookList.get(14)%></div>
                                    </div>
                                </div>
                                <div style="float:left; min-height: 180px; width:77%; margin-left:10px; color:gray; padding: 10px; border: 1px solid #EBEBEB;">
                                    <div style="float:left;width:100%; font-weight: bold; font-size: 14px; margin: 2px;">
                                        <%=purchaseBookList.get(1) %>
                                        <div id="bookRatingDiv_<%=bookPurchaseId %>" style="float: right; padding-right: 15px;">
                                            <span id="starPrimaryS<%=bookPurchaseId %>" style="float: left; margin-left: 5px; line-height: 12px;"></span>
                                            <script type="text/javascript">
                                                $('#starPrimaryS'+'<%=bookPurchaseId %>').raty({
                                                                readOnly: true,
                                                                start:	<%=purchaseBookList.get(16) %> ,
                                                                half: true,
                                                                targetType: 'number'
                                                });
                                            </script>
                                            <span style="float: left;"><% if(uF.parseToInt(purchaseBookList.get(17))>0) { %><%=purchaseBookList.get(16) %><% } %>
                                            <% if(uF.parseToInt(purchaseBookList.get(17))>0) { %>
                                            (<a href="javascript:void(0);" style="font-weight: normal;" onclick="viewReviews('<%=purchaseBookList.get(0) %>')"><%=purchaseBookList.get(17) %> Reviews</a>)
                                            <% } %>
                                            </span>	
                                        </div>
                                    </div>
                                    <div style="float:left;width:100%; margin: 2px;">By <%=purchaseBookList.get(2) %></div>
                                    <div style="float:left;width:100%; margin: 2px;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=purchaseBookList.get(5) %></span></div>
                                    <div style="float:left;width:100%; margin: 2px;">About the Book: <%=purchaseBookList.get(6) %></div>
                                    <div style="float:left;width:100%; margin: 2px;">Published by '<%=purchaseBookList.get(3)%>' published on '<%=purchaseBookList.get(4) %>'</div>
                                    <div style="float:left;width:100%; margin: 2px;">Requested by '<%=purchaseBookList.get(9)%>' requested on '<%=purchaseBookList.get(11) %>' quantity '<%=purchaseBookList.get(10) %>'</div>
                                    <div style="float:left;width:100%; margin-top: 16px;">
                                        <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                                        	 <%if(availQuantity !=null  && (uF.parseToInt(availQuantity)) > 0) { %>
                                        	 	 <input type="button" class="btn btn-primary" style="margin:0px 0px 0px 5px; float: left;" value="Approve" onclick="approveOrDenyBookPurchase('A', '<%=purchaseBookList.get(0) %>', '<%=purchaseBookList.get(12) %>', '<%=purchaseBookList.get(1) %>');"/>
                                        		 <input type="button" class="btn btn-danger" style="margin:0px 0px 0px 25px; float: left;" value="Deny" onclick="approveOrDenyBookPurchase('D', '<%=purchaseBookList.get(0) %>', '<%=purchaseBookList.get(12) %>', '<%=purchaseBookList.get(1) %>');"/>
                                        	 <% } else { %>
                                       			  <span style="margin:0px 0px 0px 5px;float:left;color:red;">Out of stock.</span>
                                       		 <% } 
                                       	 }%>
                                    </div>
                                </div>
                            </div>
                            <script type="text/javascript">
                                var srcImg = $(".img1");
                                     var newImage = new Image();
                                     var container = $(".imageContent");
                                    // alert("container==>"+container)
                                     newImage.src = srcImg.attr("src");
                                     var imageWidth = newImage.width;
                                     var imageHeight = newImage.height;
                                     //alert("imageWidth ===>> " + imageWidth + "container.width() ===>> " + container.width());
                                     if(imageWidth > container.width()) {
                                     	imageWidth = container.width();
                                     }
                                     if(imageWidth == 0) {
                                     	imageWidth = container.width();
                                     }
                                     //alert("imageHeight ===>> " + imageHeight + "container.height() ===>> " + container.height());
                                     if(imageHeight > container.height()) {
                                     	imageHeight = container.height();
                                     }
                                     if(imageHeight == 0) {
                                     	imageHeight = container.height();
                                     }
                                     var marginTop = -Math.abs(imageHeight / 2);
                                     var marginLeft = -Math.abs(imageWidth / 2);
                                     //alert("marginTop ===>> " + marginTop + "marginLeft ===>> " + marginLeft);
                                     
                                     $(".img1").css( {"margin-top": marginTop + "px", "margin-left": marginLeft + "px" });
                                     $(".img1").css( {"width": imageWidth + "px", "height": imageHeight +"px" });
                                     //alert("After");
                            </script>
                            <% } %>
                            <% } %>
                            <% } %>
                            <% if(hmPurchaseBookDetails == null || hmPurchaseBookDetails.size()==0) { %>
                            <div class="msg nodata"> 
                                <span>No book purchase request.</span>  
                            </div>
                            <% } %>
                            <% } else if(dataType != null && dataType.equals("IR")) { %>
                            <%
                                if(hmBookIssuesDetails != null  && hmBookIssuesDetails.size()>0) {
                                	int count = 1;
                                	Iterator<String> pit = hmBookIssuesDetails.keySet().iterator();
                                	while(pit.hasNext()) {
                                 	  String bookIssueId = pit.next();
                                 	  List<String> issueBookList = (ArrayList<String>)hmBookIssuesDetails.get(bookIssueId);
                                 	  if(issueBookList == null) issueBookList = new ArrayList<String>();
                                 	  if(issueBookList != null && issueBookList.size()>0 ) {
                                 		 String availQuantity = (String) issueBookList.get(20);
                                 		 String strOpacity="";
                                 		 if(availQuantity !=null  && (uF.parseToInt(availQuantity)) <= 0) {
                                 			strOpacity = "0.5";
                                 		 }
                                 %>
                            <div id="IBook_<%=bookIssueId %>" style="float:left;opacity:<%=strOpacity%>;width:98%; min-height:250px; padding:10px 10px 10px 10px;">
                                <div style="float:left; width:20%;">
                                    <div style ="float:left; width:100%; height:180px;">
                                        <div class="imageContent"><%=issueBookList.get(17) %></div>
                                    </div>
                                </div>
                                <div style="float:left; min-height: 180px; width:77%; margin-left:10px; color:gray; padding: 10px; border: 1px solid #EBEBEB;">
                                    <div style="float:left;width:100%; font-weight: bold; font-size: 14px; margin: 2px;">
                                        <%=issueBookList.get(2) %>
                                        <div id="bookRatingDiv_<%=bookIssueId %>" style="float: right; padding-right: 15px;">
                                            <span id="starPrimaryS<%=bookIssueId %>" style="float: left; margin-left: 5px; line-height: 12px;"></span>
                                            <script type="text/javascript">
                                                $('#starPrimaryS'+'<%=bookIssueId %>').raty({
                                                                readOnly: true,
                                                                start:	<%=issueBookList.get(18) %> ,
                                                                half: true,
                                                                targetType: 'number'
                                                });
                                            </script>
                                            <span style="float: left;"><% if(uF.parseToInt(issueBookList.get(19))>0) { %><%=issueBookList.get(18) %><% } %>
                                            <% if(uF.parseToInt(issueBookList.get(19))>0) { %>
                                            (<a href="javascript:void(0);" style="font-weight: normal;" onclick="viewReviews('<%=issueBookList.get(1) %>')"><%=issueBookList.get(19) %> Reviews</a>)
                                            <% } %>
                                            </span>	
                                        </div>
                                    </div>
                                    <div style="float:left;width:100%; margin: 2px;">By <%=issueBookList.get(3) %></div>
                                    <div style="float:left;width:100%; margin: 2px;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=issueBookList.get(6) %></span></div>
                                    <div style="float:left;width:100%; margin: 2px;">About the Book: <%=issueBookList.get(7) %></div>
                                    <div style="float:left;width:100%; margin: 2px;">Published by '<%=issueBookList.get(4)%>' published on '<%=issueBookList.get(5) %>'</div>
                                    <div style="float:left;width:100%; margin: 2px;">Requested by '<%=issueBookList.get(11)%>' requested on '<%=issueBookList.get(12) %>' quantity '<%=issueBookList.get(13) %>'</div>
                                    <div style="float:left;width:100%; margin: 2px;">From '<%=issueBookList.get(14)%>' To '<%=issueBookList.get(15) %>' </div>
                                    <div style="float:left;width:100%; margin-top: 16px;">
                                        <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                                       		 <%if(availQuantity !=null  && (uF.parseToInt(availQuantity)) > 0) { %>
                                       			 <input type="button" class="btn btn-primary" style="margin:0px 0px 0px 5px; float: left;" value="Approve" onclick="approveOrDenyBookIssueRequest('A', '<%=issueBookList.get(0) %>', '<%=issueBookList.get(1) %>', '<%=issueBookList.get(2) %>');"/>
                                        		<input type="button" class="btn btn-danger" style="margin:0px 0px 0px 25px; float: left;" value="Deny" onclick="approveOrDenyBookIssueRequest('D', '<%=issueBookList.get(0) %>', '<%=issueBookList.get(1) %>', '<%=issueBookList.get(2) %>');"/>
                                       		<% }else { %>
                                 			  <span style="margin:0px 0px 0px 5px;float:left;color:red;">Out of stock.</span>
                                        	<% } 
                                       	}%>
                                    </div>
                                </div>
                            </div>
                            <script type="text/javascript">
                                var srcImg = $(".img1");
                                     var newImage = new Image();
                                     var container = $(".imageContent");
                                    // alert("container==>"+container)
                                     newImage.src = srcImg.attr("src");
                                     var imageWidth = newImage.width;
                                     var imageHeight = newImage.height;
                                     //alert("imageWidth ===>> " + imageWidth + "container.width() ===>> " + container.width());
                                     if(imageWidth > container.width()) {
                                     	imageWidth = container.width();
                                     }
                                     if(imageWidth == 0) {
                                     	imageWidth = container.width();
                                     }
                                     //alert("imageHeight ===>> " + imageHeight + "container.height() ===>> " + container.height());
                                     if(imageHeight > container.height()) {
                                     	imageHeight = container.height();
                                     }
                                     if(imageHeight == 0) {
                                     	imageHeight = container.height();
                                     }
                                     var marginTop = -Math.abs(imageHeight / 2);
                                     var marginLeft = -Math.abs(imageWidth / 2);
                                     //alert("marginTop ===>> " + marginTop + "marginLeft ===>> " + marginLeft);
                                     
                                     $(".img1").css( {"margin-top": marginTop + "px", "margin-left": marginLeft + "px" });
                                     $(".img1").css( {"width": imageWidth + "px", "height": imageHeight +"px" });
                                     //alert("After");
                            </script>
                            <% } %>
                            <% } %>
                            <% } %>
                            <% if(hmBookIssuesDetails == null || hmBookIssuesDetails.size()==0) { %>
                            <div class="msg nodata"> 
                                <span>No book issue request.</span>  
                            </div>
                            <% } %>
                            <% } else if(dataType != null && dataType.equals("IB")) { %>
                            <%
                                if(hmReturnBookDetails != null  && hmReturnBookDetails.size()>0) {
                                	//Set bookReturnSet = hmReturnBookDetails.keySet();	
                                	Iterator<String> it = hmReturnBookDetails.keySet().iterator();
                                	while(it.hasNext()) {
                                 	  String bookReturnId = it.next();
                                 	  List<String> bookReturnList = (ArrayList<String>)hmReturnBookDetails.get(bookReturnId);
                                 	  if(bookReturnList == null) bookReturnList = new ArrayList<String>();
                                 	  if(bookReturnList != null && bookReturnList.size()>0 ) {
                                %>
                            <div id="RBook_<%=bookReturnId %>" style="float:left; width:98%; min-height:250px; padding:10px 10px 10px 10px;">
                                <div style="float:left; width:20%;">
                                    <div style ="float:left; width:100%; height:180px;">
                                        <div class="imageContent"><%=bookReturnList.get(17) %></div>
                                    </div>
                                </div>
                                <div style="float:left; min-height: 180px; width:77%; margin-left:10px; color:gray; padding: 10px; border: 1px solid #EBEBEB;">
                                    <div style="float:left;width:100%; font-weight: bold; font-size: 14px; margin: 2px;">
                                        <%=bookReturnList.get(2) %>
                                        <div id="bookRatingDiv_<%=bookReturnId %>" style="float: right; padding-right: 15px;">
                                            <span id="starPrimaryS<%=bookReturnId %>" style="float: left; margin-left: 5px; line-height: 12px;"></span>
                                            <script type="text/javascript">
                                                $('#starPrimaryS'+'<%=bookReturnId %>').raty({
                                                                readOnly: true,
                                                                start:	<%=bookReturnList.get(18) %> ,
                                                                half: true,
                                                                targetType: 'number'
                                                });
                                            </script>
                                            <span style="float: left;"><% if(uF.parseToInt(bookReturnList.get(19))>0) { %><%=bookReturnList.get(18) %><% } %>
                                            <% if(uF.parseToInt(bookReturnList.get(19))>0) { %>
                                            (<a href="javascript:void(0);" style="font-weight: normal;" onclick="viewReviews('<%=bookReturnList.get(1) %>')"><%=bookReturnList.get(19) %> Reviews</a>)
                                            <% } %>
                                            </span>	
                                        </div>
                                    </div>
                                    <div style="float:left;width:100%; margin: 2px;">By <%=bookReturnList.get(3) %></div>
                                    <div style="float:left;width:100%; margin: 2px;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=bookReturnList.get(6) %></span></div>
                                    <div style="float:left;width:100%; margin: 2px;">About the Book: <%=bookReturnList.get(7) %></div>
                                    <div style="float:left;width:100%; margin: 2px;">Published by '<%=bookReturnList.get(4)%>' published on '<%=bookReturnList.get(5) %>'</div>
                                    <div style="float:left;width:100%; margin: 2px;">Requested by '<%=bookReturnList.get(11)%>' issued on '<%=bookReturnList.get(12) %>' quantity '<%=bookReturnList.get(13) %>'</div>
                                    <div style="float:left;width:100%; margin: 2px;">From '<%=bookReturnList.get(14)%>' To '<%=bookReturnList.get(15) %>' </div>
                                    <div style="float:left;width:100%; margin-top: 16px;">
                                        <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                                        <input type="button" class="btn btn-primary" style="margin:0px 0px 0px 5px; float: left;" value="Book Return" onclick="returnIssuedBook('R', '<%=bookReturnList.get(0) %>', '<%=bookReturnList.get(1) %>', '<%=bookReturnList.get(2) %>');"/>
                                        <%-- <input type="button" class="btn btn-danger" style="margin:0px 0px 0px 25px; float: left;" value="Deny" onclick="returnIssuedBook('D', '<%=bookReturnList.get(0) %>', '<%=bookReturnList.get(1) %>', '<%=bookReturnList.get(2) %>');"/> --%>
                                        <% } %>
                                    </div>
                                </div>
                            </div>
                            <script type="text/javascript">
                                var srcImg = $(".img1");
                                     var newImage = new Image();
                                     var container = $(".imageContent");
                                    // alert("container==>"+container)
                                     newImage.src = srcImg.attr("src");
                                     var imageWidth = newImage.width;
                                     var imageHeight = newImage.height;
                                     //alert("imageWidth ===>> " + imageWidth + "container.width() ===>> " + container.width());
                                     if(imageWidth > container.width()) {
                                     	imageWidth = container.width();
                                     }
                                     if(imageWidth == 0) {
                                     	imageWidth = container.width();
                                     }
                                     //alert("imageHeight ===>> " + imageHeight + "container.height() ===>> " + container.height());
                                     if(imageHeight > container.height()) {
                                     	imageHeight = container.height();
                                     }
                                     if(imageHeight == 0) {
                                     	imageHeight = container.height();
                                     }
                                     var marginTop = -Math.abs(imageHeight / 2);
                                     var marginLeft = -Math.abs(imageWidth / 2);
                                     //alert("marginTop ===>> " + marginTop + "marginLeft ===>> " + marginLeft);
                                     
                                     $(".img1").css( {"margin-top": marginTop + "px", "margin-left": marginLeft + "px" });
                                     $(".img1").css( {"width": imageWidth + "px", "height": imageHeight +"px" });
                                     //alert("After");
                            </script>
                            <% } %>
                            <% } %>
                            <% } %>
                            <% if(hmReturnBookDetails == null || hmReturnBookDetails.size()==0) { %>
                            <div class="msg nodata"> 
                                <span>No book issued.</span>  
                            </div>
                            <% } %>
                            <% } else if(dataType != null && dataType.equals("EMPPB")) { %>
                            <%
                                if(hmEmpPurchasedBookDetails != null  && hmEmpPurchasedBookDetails.size()>0) {
                                	int count = 1;
                                	Iterator<String> pit = hmEmpPurchasedBookDetails.keySet().iterator();
                                	while(pit.hasNext()) {
                                 	  String bookPurchaseId = pit.next();
                                 	  List<String> purchaseBookList = (ArrayList<String>)hmEmpPurchasedBookDetails.get(bookPurchaseId);
                                 	  if(purchaseBookList == null) purchaseBookList = new ArrayList<String>();
                                 	  if(purchaseBookList != null && purchaseBookList.size()>0 ) {
                                 %>
                            <div id="PBook_<%=bookPurchaseId %>" style="float:left; width:98%; min-height:250px; padding:10px 10px 10px 10px;">
                                <div style="float:left; width:20%;">
                                    <div style ="float:left; width:100%; height:180px;">
                                        <div class="imageContent"><%=purchaseBookList.get(14)%></div>
                                    </div>
                                </div>
                                <div style="float:left; min-height: 180px; width:77%; margin-left:10px; color:gray; padding: 10px; border: 1px solid #EBEBEB;">
                                    <div style="float:left;width:100%; font-weight: bold; font-size: 14px; margin: 2px;">
                                        <%=purchaseBookList.get(1) %>
                                        <div id="bookRatingDiv_<%=bookPurchaseId %>" style="float: right; padding-right: 15px;">
                                            <span id="starPrimaryS<%=bookPurchaseId %>" style="float: left; margin-left: 5px; line-height: 12px;"></span>
                                            <script type="text/javascript">
                                                $('#starPrimaryS'+'<%=bookPurchaseId %>').raty({
                                                                readOnly: true,
                                                                start: <%=purchaseBookList.get(16) %>,
                                                                half: true,
                                                                targetType: 'number'
                                                });
                                            </script>
                                            <span style="float: left;"><% if(uF.parseToInt(purchaseBookList.get(17))>0) { %><%=purchaseBookList.get(16) %><% } %>
                                            <% if(uF.parseToInt(purchaseBookList.get(17))>0) { %>
                                            (<a href="javascript:void(0);" style="font-weight: normal;" onclick="viewReviews('<%=purchaseBookList.get(0) %>')"><%=purchaseBookList.get(17) %> Reviews</a>)
                                            <% } %>
                                            </span>	
                                        </div>
                                    </div>
                                    <div style="float:left;width:100%; margin: 2px;">By <%=purchaseBookList.get(2) %></div>
                                    <div style="float:left;width:100%; margin: 2px;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=purchaseBookList.get(5) %></span></div>
                                    <div style="float:left;width:100%; margin: 2px;">About the Book: <%=purchaseBookList.get(6) %></div>
                                    <div style="float:left;width:100%; margin: 2px;">Published by '<%=purchaseBookList.get(3)%>' published on '<%=purchaseBookList.get(4) %>'</div>
                                    <div style="float:left;width:100%; margin: 2px;">Approved by '<%=purchaseBookList.get(9)%>' approved on '<%=purchaseBookList.get(11) %>' quantity '<%=purchaseBookList.get(10) %>'</div>
                                    <div style="float:left;width:100%; margin: 2px;">Purchased Amount 'Rs. <%=purchaseBookList.get(18)%>'</div>
                                </div>
                            </div>
                            <script type="text/javascript">
                                var srcImg = $(".img1");
                                     var newImage = new Image();
                                     var container = $(".imageContent");
                                    // alert("container==>"+container)
                                     newImage.src = srcImg.attr("src");
                                     var imageWidth = newImage.width;
                                     var imageHeight = newImage.height;
                                     //alert("imageWidth ===>> " + imageWidth + "container.width() ===>> " + container.width());
                                     if(imageWidth > container.width()) {
                                     	imageWidth = container.width();
                                     }
                                     if(imageWidth == 0) {
                                     	imageWidth = container.width();
                                     }
                                     //alert("imageHeight ===>> " + imageHeight + "container.height() ===>> " + container.height());
                                     if(imageHeight > container.height()) {
                                     	imageHeight = container.height();
                                     }
                                     if(imageHeight == 0) {
                                     	imageHeight = container.height();
                                     }
                                     var marginTop = -Math.abs(imageHeight / 2);
                                     var marginLeft = -Math.abs(imageWidth / 2);
                                     //alert("marginTop ===>> " + marginTop + "marginLeft ===>> " + marginLeft);
                                     
                                     $(".img1").css( {"margin-top": marginTop + "px", "margin-left": marginLeft + "px" });
                                     $(".img1").css( {"width": imageWidth + "px", "height": imageHeight +"px" });
                                     //alert("After");
                            </script>
                            <% } %>
                            <% } %>
                            <% } %>
                            <% if(hmEmpPurchasedBookDetails == null || hmEmpPurchasedBookDetails.size()==0) { %>
                            <div class="msg nodata"> 
                                <span>No book purchased.</span>  
                            </div>
                            <% } %>
                            <% } else if(dataType != null && dataType.equals("EMPIB")) { %>
                            <%
                                if(hmEmpIssuedBookDetails != null  && hmEmpIssuedBookDetails.size()>0) {
                                	int count = 1;
                                	Iterator<String> pit = hmEmpIssuedBookDetails.keySet().iterator();
                                	while(pit.hasNext()) {
                                 	  String bookIssueId = pit.next();
                                 	  List<String> issueBookList = (ArrayList<String>)hmEmpIssuedBookDetails.get(bookIssueId);
                                 	  if(issueBookList == null) issueBookList = new ArrayList<String>();
                                 	  if(issueBookList != null && issueBookList.size()>0 ) {
                                 %>
                            <div id="IBook_<%=bookIssueId %>" style="float:left; width:98%; min-height:250px; padding:10px 10px 10px 10px;">
                                <div style="float:left; width:20%;">
                                    <div style ="float:left; width:100%; height:180px;">
                                        <div class="imageContent"><%=issueBookList.get(17) %></div>
                                    </div>
                                </div>
                                <div style="float:left; min-height: 180px; width:77%; margin-left:10px; color:gray; padding: 10px; border: 1px solid #EBEBEB;">
                                    <div style="float:left;width:100%; font-weight: bold; font-size: 14px; margin: 2px;">
                                        <%=issueBookList.get(2) %>
                                        <div id="bookRatingDiv_<%=bookIssueId %>" style="float: right; padding-right: 15px;">
                                            <span id="starPrimaryS<%=bookIssueId %>" style="float: left; margin-left: 5px; line-height: 12px;"></span>
                                            <script type="text/javascript">
                                                $('#starPrimaryS'+'<%=bookIssueId %>').raty({
                                                                readOnly: true,
                                                                start:	<%=issueBookList.get(18) %> ,
                                                                half: true,
                                                                targetType: 'number'
                                                });
                                            </script>
                                            <span style="float: left;"><% if(uF.parseToInt(issueBookList.get(19))>0) { %><%=issueBookList.get(18) %><% } %>
                                            <% if(uF.parseToInt(issueBookList.get(19))>0) { %>
                                            (<a href="javascript:void(0);" style="font-weight: normal;" onclick="viewReviews('<%=issueBookList.get(1) %>')"><%=issueBookList.get(19) %> Reviews</a>)
                                            <% } %>
                                            </span>	
                                        </div>
                                    </div>
                                    <div style="float:left;width:100%; margin: 2px;">By <%=issueBookList.get(3) %></div>
                                    <div style="float:left;width:100%; margin: 2px;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=issueBookList.get(6) %></span></div>
                                    <div style="float:left;width:100%; margin: 2px;">About the Book: <%=issueBookList.get(7) %></div>
                                    <div style="float:left;width:100%; margin: 2px;">Published by '<%=issueBookList.get(4)%>' published on '<%=issueBookList.get(5) %>'</div>
                                    <div style="float:left;width:100%; margin: 2px;">Approved by '<%=issueBookList.get(11)%>' approved on '<%=issueBookList.get(12) %>' quantity '<%=issueBookList.get(13) %>'</div>
                                    <div style="float:left;width:100%; margin: 2px;">From '<%=issueBookList.get(14)%>' To '<%=issueBookList.get(15) %>' </div>
                                </div>
                            </div>
                            <script type="text/javascript">
                                var srcImg = $(".img1");
                                     var newImage = new Image();
                                     var container = $(".imageContent");
                                    // alert("container==>"+container)
                                     newImage.src = srcImg.attr("src");
                                     var imageWidth = newImage.width;
                                     var imageHeight = newImage.height;
                                     //alert("imageWidth ===>> " + imageWidth + "container.width() ===>> " + container.width());
                                     if(imageWidth > container.width()) {
                                     	imageWidth = container.width();
                                     }
                                     if(imageWidth == 0) {
                                     	imageWidth = container.width();
                                     }
                                     //alert("imageHeight ===>> " + imageHeight + "container.height() ===>> " + container.height());
                                     if(imageHeight > container.height()) {
                                     	imageHeight = container.height();
                                     }
                                     if(imageHeight == 0) {
                                     	imageHeight = container.height();
                                     }
                                     var marginTop = -Math.abs(imageHeight / 2);
                                     var marginLeft = -Math.abs(imageWidth / 2);
                                     //alert("marginTop ===>> " + marginTop + "marginLeft ===>> " + marginLeft);
                                     
                                     $(".img1").css( {"margin-top": marginTop + "px", "margin-left": marginLeft + "px" });
                                     $(".img1").css( {"width": imageWidth + "px", "height": imageHeight +"px" });
                                     //alert("After");
                            </script>
                            <% } %>
                            <% } %>
                            <% } %>
                            <% if(hmEmpIssuedBookDetails == null || hmEmpIssuedBookDetails.size()==0) { %>
                            <div class="msg nodata"> <span>No book issued.</span> </div>
                            <% } %>
                            <% } %>
                            <div style="text-align: center; float: left; width: 100%;">
                                <% int intPageCnt = uF.parseToInt(bookPageCount);
                                    int pageCnt = 0;
                                    int minLimit = 0;
                                    
                                    for(int i=1; i<=intPageCnt; i++) {
                                    		minLimit = pageCnt * 10;
                                    		pageCnt++;
                                    %>
                                <% if(i ==1) {
                                    String strPgCnt = (String)request.getAttribute("currPage");
                                    String strMinLimit = (String)request.getAttribute("minLimit");
                                    if(uF.parseToInt(strPgCnt) > 1) {
                                    	 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
                                    	 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
                                    }
                                    if(strMinLimit == null) {
                                    	strMinLimit = "0";
                                    }
                                    if(strPgCnt == null) {
                                    	strPgCnt = "1";
                                    }
                                    %>
                                <span style="color: lightgray;">
                                <% if(uF.parseToInt((String)request.getAttribute("currPage")) > 1) { %>
                                <a href="javascript:void(0);" onclick="loadMoreBooks('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "B") %>');">
                                <%="< Prev" %></a>
                                <% } else { %>
                                <b><%="< Prev" %></b>
                                <% } %>
                                </span>
                                <span><a href="javascript:void(0);" onclick="loadMoreBooks('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "B") %>');"
                                    <% if(((String)request.getAttribute("currPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("currPage")) == pageCnt) { %>
                                    style="color: black;"
                                    <% } %>
                                    ><%=pageCnt %></a></span>
                                <% if((uF.parseToInt((String)request.getAttribute("currPage"))-3) > 1) { %>
                                <b>...</b>
                                <% } %>
                                <% } %>
                                <% if(i > 1 && i < intPageCnt) { %>
                                <% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("currPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("currPage"))+2)) { %>
                                <span><a href="javascript:void(0);" onclick="loadMoreBooks('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "B") %>');"
                                    <% if(((String)request.getAttribute("currPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("currPage")) == pageCnt) { %>
                                    style="color: black;"
                                    <% } %>
                                    ><%=pageCnt %></a></span>
                                <% } %>
                                <% } %>
                                <% if(i == intPageCnt && intPageCnt > 1) {
                                    String strPgCnt = (String)request.getAttribute("currPage");
                                    String strMinLimit = (String)request.getAttribute("minLimit");
                                     strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
                                     strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
                                     if(strMinLimit == null) {
                                    	strMinLimit = "0";
                                    }
                                    if(strPgCnt == null) {
                                    	strPgCnt = "1";
                                    }
                                    %>
                                <% if((uF.parseToInt((String)request.getAttribute("currPage"))+3) < intPageCnt) { %>
                                <b>...</b>
                                <% } %>
                                <span><a href="javascript:void(0);" onclick="loadMoreBooks('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "B") %>');"
                                    <%-- <a href="ViewAllProjects.action?proPage=<%=pageCnt %>&minLimit=<%=minLimit %>" --%>
                                    <% if(uF.parseToInt((String)request.getAttribute("currPage")) == pageCnt) { %>
                                    style="color: black;"
                                    <% } %>
                                    ><%=pageCnt %></a></span>
                                <span style="color: lightgray;">
                                <% if(uF.parseToInt((String)request.getAttribute("currPage")) < pageCnt) { %>
                                <a href="javascript:void(0);" onclick="loadMoreBooks('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "B") %>');"><%="Next >" %></a>
                                <% } else { %>
                                <b><%="Next >" %></b>
                                <% } %>
                                </span>
                                <% } %>
                                <%} %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</section>
<div id="AddBookPopup"></div>
<div id="EditBookPopup"></div>
<div id="RateBookPopup"></div>
<div id="PurchaseBookPopup"></div>
<div id="ApproveOrDenyPurchasePopup"></div>
<div id="IssueBookPopup"></div>
<div id="bookReviewDiv"></div>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
