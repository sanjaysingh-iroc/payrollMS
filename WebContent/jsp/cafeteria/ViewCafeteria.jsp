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
<style>
    .imageContent { position: relative; width:100%; height:150px; }
    .img1 { width: 100% !important;height: 100% !important;margin-top: 0px !important; margin-left: 0px !important; }
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
    
    var dialogEditEditDish = '#editDishPopup';
    function editDeleteYourDish(dishId, type, section) {
    	if(type == 'E') {
    		var dialogEdit = '.modal-body';
    		 $(dialogEdit).empty();
    		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		 $("#modalInfo").show();
    		 $(".modal-title").html('Edit Dish ');
    		 $.ajax({
                    url : 'AddDish.action?dishId='+dishId+'&operation='+type,
                    cache : false,
                    success : function(data) {
                    	//alert("data ==> " + data);
                        $(dialogEdit).html(data);
                 	}
                });
       } else {
    	   if(confirm("Are you sure, you wish to delete this Dish?")) {
    	   var xhr = $.ajax({
                 url : 'AddDish.action?dishId='+dishId+'&operation='+type,
                 cache : false,
                 success : function(data) {
                 	document.getElementById(section+"_"+dishId).style.display="none";        	
                 }
             });
       		}
    	}
    }
    
    
    function closeEditDishPopup() {
    	$(dialogEditEditDish).dialog('close');
    }
    
    
    function viewOrdersPopup(dishId, dishName) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('View Orders for '+ dishName);
    	 
    	 if($(window).width() >= 900){
    		 $(".modal-dialog").width(900);
    	 }
    	 $.ajax({
    	        url : 'ViewCafeteriaOrders.action?dishId='+dishId,
    	      cache : false,
    	    success : function(data) {
    	         $(dialogEdit).html(data);
    	      }
           });
    	}
    
    
    var dialogEditAddDish = '#AddDishPopup';
    function addNewDish() {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Add New Dish ');
    	 $.ajax({
                url : 'AddDish.action',
                cache : false,
                success : function(data) {
                	//alert("data==>"+data);
                    $(dialogEdit).html(data);
             }
            });
    }
    
     
    function closeAddDishPopup() {
    	$(dialogEditAddDish).dialog('close');
    }
    
    
     var dialogEditDishOrder = '#PlaceOrderPopup';
     function placeOrder(dishId, orderCount) {
    	 
    	if(confirm('Are you sure about the Dish?')) { 
    		var dialogEdit = '.modal-body';
    		 $(dialogEdit).empty();
    		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		 $("#modalInfo").show();
    		 $(".modal-title").html('Will take this dish ');
    		 $.ajax({
    		        url : 'PlaceOrderForDish.action?dishId='+dishId+'&orderCount='+orderCount,
    		      cache : false,
    		    success : function(data) {
    		               $(dialogEdit).html(data);
    		        }
    	       });
    	}
     }
     
     
     function closeDishOrderPopup() {
    	 $(dialogEditDishOrder).dialog('close');
     }
     
    
    var dialogEditAddEmpOrder = '#AddEmployeeOrder';
    function addEmpFoodRequests(dishId) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Add Food Order');
    	 $.ajax({
    		    url : 'AddEmpFoodRequests.action?dishId='+dishId,
    		    cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    }
    
    
     function closeAddEmpOrderPopup() {
    	 $(dialogEditAddEmpOrder).dialog('close');
     }
    
    
    var dialogEditAddGuest = '#AddGuests';
    function addGuestFoodRequests(dishId) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Add Guests');
    	 $.ajax({
    		    url : 'AddGuests.action?dishId='+dishId,
    		    cache : false,
    		    success : function(data) {
    				$(dialogEdit).html(data);
    			}
           });
    }
    	
    function closeAddGuestPopup() {
    	 $(dialogEditAddGuest).dialog('close');
     }
    
    var dialogEditAddGuest = '#ViewConfirmedOrders';
    function viewConfirmedOrders(dishId) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 if($(window).width() >= 700){
    		 $(".modal-dialog").width(700);
    	 }
    	 $(".modal-title").html('View Confirmed Orders');
    	 $.ajax({
    		    url : 'ViewConfirmedOrders.action?dishId='+dishId,
    		    cache : false,
    		    success : function(data) {
    				$(dialogEdit).html(data);
    			}
           });
    }
    
    function submitForm(type) {
    	
    	if(type == '1') {
    		var f_org = document.getElementById("f_org").value;
    		window.location = 'Cafeteria.action?f_org='+f_org;	
    		
    	} else {
    		document.frmCafeteriaReport.submit();
    	}
    }
</script>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Cafeteria" name="title"/>
    </jsp:include> --%>
<%
    String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
    String empId = (String)session.getAttribute(IConstants.EMPID);
    String dataType = (String)request.getAttribute("dataType");
    UtilityFunctions uF = new UtilityFunctions();
    String sbData = (String) request.getAttribute("sbData");
    String strSearchJob = (String) request.getAttribute("strSearchJob");
    Map<String,List<String>> hmBreakFastMenu = (Map<String,List<String>>) request.getAttribute("hmBreakFastMenu");
    Map<String,List<String>> hmLunchMenu = (Map<String,List<String>>) request.getAttribute("hmLunchMenu");
    Map<String,List<String>> hmDinnerMenu = (Map<String,List<String>>) request.getAttribute("hmDinnerMenu");
    Map<String,List<String>> hmOtherMenu = (Map<String,List<String>>) request.getAttribute("hmOtherMenu");
    
    if(hmBreakFastMenu == null) hmBreakFastMenu = new HashMap<String,List<String>>();
    if(hmLunchMenu == null) hmLunchMenu = new HashMap<String,List<String>>();
    if(hmDinnerMenu == null) hmDinnerMenu = new HashMap<String,List<String>>();
    if(hmOtherMenu == null) hmOtherMenu = new HashMap<String,List<String>>();
    %>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                        <s:form name="frmCafeteriaReport" id="frmCafeteriaReport" action="Cafeteria" theme="simple">
                            <%if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                            <div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
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
                                </div>
                            </div>
                            <% } %>
                            <div class="clr alignCenter">
	                            <s:hidden name="dataType"/>
	                            <span>Search:</span>
	                            <input type="text" id="strSearchJob" name="strSearchJob" value="<%=uF.showData(strSearchJob,"") %>"/>
	                            <input type="submit" value="Search" class="btn btn-primary" > 
	                            <script>
	                                $( "#strSearchJob" ).autocomplete({
	                                	source: [ <%=uF.showData(sbData,"") %> ]
	                                });
	                            </script>
                            </div>
                        </s:form>
                        <%if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                        <div class="row row_without_margin">
                        	<div class="col-lg-12">
                                <input type="button" onclick="addNewDish()" value="Add New Dish" class="btn btn-primary pull-right">
                        	</div>
                        </div>
                        <% } %>
                        <div class="clr margintop20">
                            <% if(dataType == null) {
                                dataType = "T"; 
                                } %>
                            <div class="nav-tabs-custom">
                                <% if(dataType == null || dataType.equals("T")) { %>
                                <ul class="nav nav-tabs">
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Cafeteria.action?dataType=T'" data-toggle="tab">Today</a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Cafeteria.action?dataType=TM'" data-toggle="tab">Tomorrow</a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Cafeteria.action?dataType=DAT'" data-toggle="tab">Day After Tomorrow</a></li>
								</ul>
                                <%} else if(dataType == null || dataType.equals("TM")) { %>
                                <ul class="nav nav-tabs">
									<li><a href="javascript:void(0)" onclick="window.location='Cafeteria.action?dataType=T'" data-toggle="tab">Today</a></li>
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Cafeteria.action?dataType=TM'" data-toggle="tab">Tomorrow</a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Cafeteria.action?dataType=DAT'" data-toggle="tab">Day After Tomorrow</a></li>
								</ul>
                                <% } else if(dataType == null || dataType.equals("DAT")) { %>
                                <ul class="nav nav-tabs">
									<li><a href="javascript:void(0)" onclick="window.location='Cafeteria.action?dataType=T'" data-toggle="tab">Today</a></li>
									<li><a href="javascript:void(0)" onclick="window.location='Cafeteria.action?dataType=TM'" data-toggle="tab">Tomorrow</a></li>
									<li class="active"><a href="javascript:void(0)" onclick="window.location='Cafeteria.action?dataType=DAT'" data-toggle="tab">Day After Tomorrow</a></li>
								</ul>
                                <% } %>
                            </div>
                            <div style="float:left; width:100%;">
                                <div style="margin: 10px 0px 0px 0px; float: left; width: 100%;">
                                    <p style="padding-left: 15px;border-bottom:1px solid #EFEFEF;font-size:12px;font-family:arial"><span style="width: 100%;"><b>Breakfast</b></span></p>
                                    <% if(hmBreakFastMenu != null && !hmBreakFastMenu.isEmpty() && hmBreakFastMenu.size()>0) { %>
                                    <div style=" width: 97%; margin:10px -9px 2px 26px; overflow-x:auto;">
                                        <table cellspacing="0" cellpadding="0" border="0">
                                            <tbody>
                                                <tr>
                                                    <%
                                                        Iterator<String> it = hmBreakFastMenu.keySet().iterator();
                                                        while(it.hasNext()) {
                                                        	String dishId = it.next();
                                                        	List<String> dishList = hmBreakFastMenu.get(dishId);
                                                        	if(dishList == null) dishList = new ArrayList<String>();
                                                        	if(dishList!=null && dishList.size()>0 && !dishList.isEmpty()) {
                                                        		
                                                        %>
                                                    <td>
                                                        <div id = "Tb_<%=dishList.get(0)%>" style="float:left;width:290px;padding: 5px;margin:10px 10px 10px 10px;border:1px solid #efefef;display:inline;">
                                                            <div style ="float:left;width:100%;">
                                                                <div style="float:left; width:100%;">
                                                                    <div style ="float:left; width:100%; height:150px;">
                                                                        <div class="imageContent"><%=dishList.get(16) %></div>
                                                                    </div>
                                                                </div>
                                                                <%-- <div style ="float:left;width:94%;height:125px;margin:7px 7px 2px 7px;"><%=dishList.get(16) %></div> --%>
                                                                <div style="float:left; width:100%; margin: 3px 0px 0px 5px; color:gray;">
                                                                    <div style ="float:left; width:100%; font-weight:bold; font-size:14px;"><%=dishList.get(1)%></div>
                                                                    <div style ="float:left; width:100%;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=dishList.get(18)%></span></div>
                                                                    <div style ="float:left; width:100%;"><%=dishList.get(19)%></div>
                                                                    <div style ="float:left; width:100%; font-style:italic;">Served between <%=dishList.get(13)%> to <%=dishList.get(14)%>
                                                                    </div>
                                                                </div>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {  
                                                                    if(uF.parseToInt(dishList.get(17)) > 0) {
                                                                    %>
                                                                <div style="width:30%; float:left; margin-left:10px;padding: 5px 0px;">
                                                                    <input type="button" name="strViewOrders" class="btn btn-primary" style="float:left; margin-left:15px; font-size:11px;" onclick="viewOrdersPopup('<%=dishList.get(0) %>', '<%=dishList.get(1)%>');" value="<%=dishList.get(17)%> people"/>
                                                                </div>
                                                                <% } } else { %>
                                                                <div style="float:left;width:100%;">
                                                                    <input type="button" class="btn btn-primary" style="font-size:11px; margin:10px 0px 0px 5px; float:left;" value="Will take this dish!" onclick="placeOrder('<%=dishList.get(0)%>','<%=dishList.get(17)%>' );"/>
                                                                </div>
                                                                <% } %>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                                                                <div style="width:60%; float:left; margin-left:10px;padding: 5px 0px;">
                                                                    <input type="button" name="strEdit" class="btn btn-primary" value="Edit" style="float:left; font-size:11px;" onclick="editDeleteYourDish('<%=dishList.get(0) %>', 'E','Tb');"/>
                                                                    <%
                                                                        if(dishList.get(20) != null && uF.parseToInt(dishList.get(20)) > 0) { %>
                                                                    <input type="button" name="strDelete" class="btn btn-danger" value="Remove" style="float:left; margin-left:10px; font-size:11px;" onclick="alert('You can not delete this dish,since order is confirmed!');"/>
                                                                    <% } else { %>
                                                                    <input type="button" name="strDelete" class="btn btn-danger" value="Remove" style="float:left; margin-left:10px; font-size:11px;" onclick="editDeleteYourDish('<%=dishList.get(0) %>', 'D','Tb');"/>
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                                                                <div style="padding-top: 10px;clear: both;">
                                                                    <a href="javascript:void(0);" style="font-size: 11px;" onclick="addEmpFoodRequests('<%=dishList.get(0) %>');" ><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Employees</a>
                                                                    <% if(uF.parseToInt(dishList.get(20))>0) { %>
                                                                    <a href="javascript:void(0);" style="font-size: 11px;" onclick="addGuestFoodRequests('<%=dishList.get(0) %>');" ><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Guests</a>
                                                                    <a href="javascript:void(0);" style="font-size: 11px;" onclick="viewConfirmedOrders('<%=dishList.get(0) %>');" class="badge bg-light-blue"><%=dishList.get(20)%> Eating People</a>
                                                                    <%} else {%>
                                                                    <a href="javascript:void(0);" style="font-size: 11px;" onclick="addGuestFoodRequests('<%=dishList.get(0) %>');" ><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Guests</a>
                                                                    <%} %>
                                                                </div>
                                                                <% } %>
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
                                                    </td>
                                                    <%	} } %>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <% } else { %>
                                    <div class="nodata msg"><span>No menu for breakfast</span></div>
                                    <% } %>
                                </div>
                                <div style="margin: 20px 0px 0px 0px; float: left; width: 100%;">
                                    <p style="padding-left: 15px;border-bottom:1px solid #EFEFEF;font-size:12px;font-family:arial"><span style="width: 100%;"><b>Lunch</b></span></p>
                                    <% if(hmLunchMenu != null && !hmLunchMenu.isEmpty() && hmLunchMenu.size()>0) { %>
                                    <div style="width: 97%; margin:10px -9px 2px 26px; overflow-x:auto;">
                                        <table cellspacing="0" cellpadding="0" border="0">
                                            <tbody>
                                                <tr>
                                                    <%
                                                        Iterator<String> it = hmLunchMenu.keySet().iterator();
                                                        while(it.hasNext()){
                                                        	String dishId = it.next();
                                                        	List<String> lunchDishList = hmLunchMenu.get(dishId);
                                                        	if(lunchDishList == null) lunchDishList = new ArrayList<String>();
                                                        	if(lunchDishList!=null && lunchDishList.size()>0 && !lunchDishList.isEmpty()){
                                                        %>
                                                    <td>
                                                        <div id = "Tl_<%=lunchDishList.get(0)%>" style = "float:left;width:290px;height:300px;margin:10px 10px 10px 10px;border:1px solid #efefef;display:inline;">
                                                            <div style ="float:left;width:100%;height:30px;">
                                                                <div style ="float:left; width:100%; height:150px;">
                                                                    <div class="imageContent"><%=lunchDishList.get(16)%></div>
                                                                </div>
                                                                <%-- <div style ="float:left;width:94%;height:125px;margin:7px 7px 2px 7px;"><%=lunchDishList.get(16) %></div> --%>
                                                                <div style="float:left; width:100%; margin: 3px 0px 0px 5px; color:gray;">
                                                                    <div style ="float:left; width:100%; font-weight:bold; font-size:14px;"><%=lunchDishList.get(1)%></div>
                                                                    <div style ="float:left; width:100%;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=lunchDishList.get(18)%></span></div>
                                                                    <div style ="float:left; width:100%;"><%=lunchDishList.get(19)%></div>
                                                                    <div style ="float:left; width:100%; font-style:italic;">Served between <%=lunchDishList.get(13)%> to <%=lunchDishList.get(14)%>
                                                                    </div>
                                                                </div>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {  
                                                                    if(uF.parseToInt(lunchDishList.get(17)) > 0) {
                                                                    %>
                                                                <div style="float:left;width:30%;">
                                                                    <input type="button" name="strViewOrders" class="btn btn-primary" style="float:left; margin-left:15px; font-size:11px;" onclick="viewOrdersPopup('<%=lunchDishList.get(0) %>', '<%=lunchDishList.get(1)%>');" value="<%=lunchDishList.get(17)%> people"/>
                                                                </div>
                                                                <% 
                                                                    }
                                                                    } else { %>
                                                                <div style="float:left;width:99%;">
                                                                    <input type="button" class="btn btn-primary" style="font-size:11px; margin:10px 0px 0px 5px; float:left;" value="Will take this dish!" onclick="placeOrder('<%=lunchDishList.get(0)%>','<%=lunchDishList.get(17)%>' );"/>
                                                                </div>
                                                                <% } %>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                                                                <div style="width:60%;float:left;margin-left:10px;">
                                                                    <input type="button" name="strEdit" class="btn btn-primary" value="Edit" style="float:left; font-size:11px;" onclick="editDeleteYourDish('<%=lunchDishList.get(0) %>', 'E','Tl');"/>
                                                                    <%if(lunchDishList.get(20) != null && uF.parseToInt(lunchDishList.get(20)) > 0) { %>
                                                                    <input type="button" name="strDelete" class="btn btn-danger" value="Remove" style="float:left; margin-left:10px; font-size:11px;" onclick="alert('You can not delete this dish,since order is confirmed!');"/>
                                                                    <% } else { %>
                                                                    <input type="button" name="strDelete" class="btn btn-danger" value="Remove" style="float:left; margin-left:10px; font-size:11px;" onclick="editDeleteYourDish('<%=lunchDishList.get(0) %>', 'D','Tl');"/>
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {  %>
                                                                <div style="float:left;width:90%;margin:10px 0px 0px 3px;">
                                                                    <a href="javascript:void(0);" style="float:left;margin-left:10px; font-size: 11px;" onclick="addEmpFoodRequests('<%=lunchDishList.get(0) %>');" >+Add Employees</a>
                                                                    <% if(uF.parseToInt(lunchDishList.get(20))>0) { %>
                                                                    <a href="javascript:void(0);" style="float:left;margin-left:10px; font-size: 11px;" onclick="addGuestFoodRequests('<%=lunchDishList.get(0) %>');" >+Add Guests</a>
                                                                    <a href="javascript:void(0);" style="float:left; margin-left:10px; font-size: 11px;" onclick="viewConfirmedOrders('<%=lunchDishList.get(0) %>');" ><%=lunchDishList.get(20)%> Eating People</a>
                                                                    <%} else {%>
                                                                    <a href="javascript:void(0);" style="float:left;margin-left:72px; font-size: 11px;" onclick="addGuestFoodRequests('<%=lunchDishList.get(0) %>');" >+Add Guests</a>
                                                                    <%} %>
                                                                </div>
                                                                <% } %>
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
                                                    </td>
                                                    <% } } %>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <% } else { %>
                                    <div class="nodata msg"><span>No menu for lunch</span></div>
                                    <% }%>
                                </div>
                                <div style="margin: 20px 0px 0px 0px; float: left; width: 100%;">
                                    <p style="padding-left: 15px;border-bottom:1px solid #EFEFEF;font-size:12px;font-family:arial"> <span style="width: 100%;"><b>Dinner</b></span> </p>
                                    <% if(hmDinnerMenu != null && !hmDinnerMenu.isEmpty() && hmDinnerMenu.size()>0) { %>
                                    <div style="width: 97%; margin:10px -9px 2px 26px; overflow-x:auto;">
                                        <table cellspacing="0" cellpadding="0" border="0">
                                            <tbody>
                                                <tr>
                                                    <%	
                                                        Iterator<String> it = hmDinnerMenu.keySet().iterator();
                                                        while(it.hasNext()){
                                                        	String dishId = it.next();
                                                        	List<String> dinnerDishList = hmDinnerMenu.get(dishId);
                                                        	if(dinnerDishList == null) dinnerDishList = new ArrayList<String>();
                                                        	if(dinnerDishList!=null && dinnerDishList.size()>0 && !dinnerDishList.isEmpty()){
                                                        %>
                                                    <td>
                                                        <div id = "Td_<%=dinnerDishList.get(0)%>" style = "float:left;width:290px;height:300px;margin:10px 10px 10px 10px;border:1px solid #efefef;display:inline;">
                                                            <div style="float:left;width:100%;height:30px;">
                                                                <div style ="float:left; width:100%; height:150px;">
                                                                    <div class="imageContent"><%=dinnerDishList.get(16)%></div>
                                                                </div>
                                                                <%-- <div style ="float:left;width:94%;height:125px;margin:7px 7px 2px 7px;"><%=dinnerDishList.get(16) %></div> --%>
                                                                <div style="float:left; width:100%; margin: 3px 0px 0px 5px; color:gray;">
                                                                    <div style ="float:left; width:100%; font-weight:bold; font-size:14px;"><%=dinnerDishList.get(1)%></div>
                                                                    <div style ="float:left; width:100%;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=dinnerDishList.get(18)%></span></div>
                                                                    <div style ="float:left; width:100%;"><%=dinnerDishList.get(19)%></div>
                                                                    <div style ="float:left; width:100%; font-style:italic;">Served between <%=dinnerDishList.get(13)%> to <%=dinnerDishList.get(14)%>
                                                                    </div>
                                                                </div>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {  
                                                                    if(uF.parseToInt(dinnerDishList.get(17)) > 0) {
                                                                    %>
                                                                <div style="float:left;width:30%;">
                                                                    <input type="button" name="strViewOrders" class="btn btn-primary" style="float:left; margin-left:15px; font-size:11px;" onclick="viewOrdersPopup('<%=dinnerDishList.get(0) %>', '<%=dinnerDishList.get(1)%>');" value="<%=dinnerDishList.get(17)%> people"/>
                                                                </div>
                                                                <% } } else { %>
                                                                <div style="float:left;width:99%;">
                                                                    <input type="button" class="btn btn-primary" style="font-size:11px; margin:10px 0px 0px 5px; float:left;" value="Will take this dish!" onclick="placeOrder('<%=dinnerDishList.get(0)%>','<%=dinnerDishList.get(17)%>' );"/>
                                                                </div>
                                                                <% } %>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                                                                <div style="width:60%; float:left; margin-left:10px;">
                                                                    <input type="button" name="strEdit" class="btn btn-primary" value="Edit" style="float:left; font-size:11px;" onclick="editDeleteYourDish('<%=dinnerDishList.get(0) %>', 'E','Td');"/>
                                                                    <%if(dinnerDishList.get(20) != null && uF.parseToInt(dinnerDishList.get(20)) > 0) { %>
                                                                    <input type="button" name="strDelete" class="btn btn-danger" value="Remove" style="float:left; margin-left:10px; font-size:11px;" onclick="alert('You can not delete this dish,since order is confirmed!');"/>
                                                                    <% } else { %>
                                                                    <input type="button" name="strDelete" class="btn btn-danger" value="Remove" style="float:left; margin-left:10px; font-size:11px;" onclick="editDeleteYourDish('<%=dinnerDishList.get(0) %>', 'D','Td');"/>
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {  %>
                                                                <div style="float:left;width:90%;margin:10px 0px 0px 3px;">
                                                                    <a href="javascript:void(0);" style="float:left;margin-left:10px; font-size: 11px;" onclick="addEmpFoodRequests('<%=dinnerDishList.get(0) %>');" >+Add Employees</a>
                                                                    <% if(uF.parseToInt(dinnerDishList.get(20))>0) { %>
                                                                    <a href="javascript:void(0);" style="float:left;margin-left:10px; font-size: 11px;" onclick="addGuestFoodRequests('<%=dinnerDishList.get(0) %>');" >+Add Guests</a>
                                                                    <a href="javascript:void(0);" style="float:left; margin-left:10px; font-size: 11px;" onclick="viewConfirmedOrders('<%=dinnerDishList.get(0) %>');" ><%=dinnerDishList.get(20)%> Eating People</a>
                                                                    <%} else {%>
                                                                    <a href="javascript:void(0);" style="float:left;margin-left:72px; font-size: 11px;" onclick="addGuestFoodRequests('<%=dinnerDishList.get(0) %>');" >+Add Guests</a>
                                                                    <%} %>
                                                                </div>
                                                                <% } %>
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
                                                    </td>
                                                    <% } } %>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <% } else { %>
                                    <div class="nodata msg"><span>No menu for dinner</span></div>
                                    <% } %>
                                </div>
                                <div style="margin: 20px 0px 0px 0px; float: left; width: 100%;">
                                    <p style="padding-left: 15px;border-bottom:1px solid #EFEFEF; font-size:12px; font-family:arial;"> <span style="width: 100%;"><b>Other</b></span></p>
                                    <%	if(hmOtherMenu != null && !hmOtherMenu.isEmpty() && hmOtherMenu.size()>0) { %>
                                    <div style="width: 97%;margin:10px -9px 2px 26px; overflow-x:auto;">
                                        <table cellspacing="0" cellpadding="0" border="0">
                                            <tbody>
                                                <tr>
                                                    <%		
                                                        Iterator<String> it = hmOtherMenu.keySet().iterator();
                                                        while(it.hasNext()){
                                                        	String dishId = it.next();
                                                        	List<String> otherDishList = hmOtherMenu.get(dishId);
                                                        	if(otherDishList == null) otherDishList = new ArrayList<String>();
                                                        	if(otherDishList!=null && otherDishList.size()>0 && !otherDishList.isEmpty()){
                                                        %>
                                                    <td>
                                                        <div id = "To_<%=otherDishList.get(0)%>" style = "float:left;width:290px;height:300px;margin:10px 10px 10px 10px;border:1px solid #efefef;display:inline;">
                                                            <div style ="float:left;width:100%;height:30px;">
                                                                <div style ="float:left; width:100%; height:150px;">
                                                                    <div class="imageContent"><%=otherDishList.get(16)%></div>
                                                                </div>
                                                                <%-- <div style ="float:left;width:94%;height:125px;margin:7px 7px 2px 7px;"><%=otherDishList.get(16) %></div> --%>
                                                                <div style="float:left; width:100%; margin: 3px 0px 0px 5px; color:gray;">
                                                                    <div style ="float:left; width:100%; font-weight:bold; font-size:14px;"><%=otherDishList.get(1)%></div>
                                                                    <div style ="float:left; width:100%;"><span style="background-color:yellow; border-radius: 3px; padding: 2px 5px;"><%=otherDishList.get(18)%></span></div>
                                                                    <div style ="float:left; width:100%;"><%=otherDishList.get(19)%></div>
                                                                    <div style ="float:left; width:100%; font-style:italic;">Served between <%=otherDishList.get(13)%> to <%=otherDishList.get(14)%>
                                                                    </div>
                                                                </div>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {  
                                                                    if(uF.parseToInt(otherDishList.get(17)) > 0) {
                                                                    %>
                                                                <div style="float:left;width:30%;">
                                                                    <input type="button" name="strViewOrders" class="btn btn-primary" style="float:left; margin-left:15px; font-size:11px;" onclick="viewOrdersPopup('<%=otherDishList.get(0) %>', '<%=otherDishList.get(1)%>');" value="<%=otherDishList.get(17)%> people"/>
                                                                </div>
                                                                <% } } else { %>
                                                                <div style="float:left;width:99%;">
                                                                    <input type="button" class="btn btn-primary" style="font-size:11px; margin:10px 0px 0px 5px; float:left;" value="Will take this dish!" onclick="placeOrder('<%=otherDishList.get(0)%>','<%=otherDishList.get(17)%>' );"/>
                                                                </div>
                                                                <% } %>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
                                                                <div style="width:60%; float:left; margin-left:10px;">
                                                                    <input type="button" name="strEdit" class="btn btn-primary" value="Edit" style="float:left; font-size:11px;" onclick="editDeleteYourDish('<%=otherDishList.get(0) %>', 'E','To');"/>
                                                                    <%if(otherDishList.get(20) != null && uF.parseToInt(otherDishList.get(20)) > 0) { %>
                                                                    <input type="button" name="strDelete" class="btn btn-danger" value="Remove" style="float:left; margin-left:10px; font-size:11px;" onclick="alert('You can not delete this dish,since order is confirmed!');"/>
                                                                    <% } else { %>
                                                                    <input type="button" name="strDelete" class="btn btn-danger" value="Remove" style="float:left; margin-left:10px; font-size:11px;" onclick="editDeleteYourDish('<%=otherDishList.get(0) %>', 'D','To');"/>	
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                                <% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {  %>
                                                                <div style="float:left;width:90%;margin:10px 0px 0px 3px;">
                                                                    <a href="javascript:void(0);" style="float:left;margin-left:10px; font-size: 11px;" onclick="addEmpFoodRequests('<%=otherDishList.get(0) %>');" >+Add Employees</a>
                                                                    <% if(uF.parseToInt(otherDishList.get(20))>0) { %>
                                                                    <a href="javascript:void(0);" style="float:left;margin-left:10px; font-size: 11px;" onclick="addGuestFoodRequests('<%=otherDishList.get(0) %>');" >+Add Guests</a>
                                                                    <a href="javascript:void(0);" style="float:left; margin-left:10px; font-size: 11px;" onclick="viewConfirmedOrders('<%=otherDishList.get(0) %>');" ><%=otherDishList.get(20)%> Eating People</a>
                                                                    <%} else {%>
                                                                    <a href="javascript:void(0);" style="float:left;margin-left:72px; font-size: 11px;" onclick="addGuestFoodRequests('<%=otherDishList.get(0) %>');" >+Add Guests</a>
                                                                    <%} %>
                                                                </div>
                                                                <% } %>
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
                                                    </td>
                                                    <% } } %>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <% } else { %>
                                    <div class="nodata msg"><span>No other menu</span></div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</section>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

