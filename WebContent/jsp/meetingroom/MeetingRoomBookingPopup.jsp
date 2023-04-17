<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="com.konnect.jpms.select.FillUserType"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    String operation = (String) request.getAttribute("operation");
    String bookingId = (String) request.getAttribute("bookingId");
    List<String> guestList = (List<String>) request.getAttribute("guestList");
    String sbParticipants = (String) request.getAttribute("sbParticipants");
    String sbDishes = (String) request.getAttribute("sbDishes");
    String meetingRoomId = (String) request.getAttribute("meetingRoomId");
    String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),IConstants.DBDATE,IConstants.DATE_FORMAT);
    %>

<%-- <script type="text/javascript" src="scripts/jquery-ui.min.js"></script> --%>
<script type="text/javascript" charset="utf-8">
    $('body').on('click','#submitButton',function(){
    	$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
    	$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
    });
    
    $(function() {
    	$("#strParticipants").multiselect().multiselectfilter();
    	var isFood = document.getElementById("isFoodServiceRequired").value;
    	showFoodReqDetails(isFood);
    	
    	var meetingRoomId1 = document.getElementById("meetingRoomId").value;
    	//alert("meetingRoomId1==>"+meetingRoomId1.length);
    	if(meetingRoomId1!= null && meetingRoomId1.trim() != '' && meetingRoomId1 != "null") {
    		getMeetingRoomDetails(meetingRoomId1);
    	}
    	
    	var date_yest = new Date();
        var date_tom = new Date();
        date_yest.setHours(0,0,0);
        date_tom.setHours(23,59,59); 
       
    	$('#strFrom_time').datetimepicker({
    		format: 'HH:mm',
    		minDate: date_yest,
    		defaultDate: date_yest
        }).on('dp.change', function(e){ 
        	$('#strTo_time').data("DateTimePicker").minDate(e.date);
        });
    	
    	$('#strTo_time').datetimepicker({
    		format: 'HH:mm',
    		maxDate: date_tom,
    		defaultDate: date_tom
        }).on('dp.change', function(e){ 
        	$('#strFrom_time').data("DateTimePicker").maxDate(e.date);
        }); 
    	
    	$("#strBooking_from").datepicker({
            format: 'dd/mm/yyyy',  
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strBooking_to').datepicker('setStartDate', minDate);
        });
        
        $("#strBooking_to").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strBooking_from').datepicker('setEndDate', minDate);
        });
    	
    });	
    	
    var cxtpath='<%=request.getContextPath()%>';
    
    function isNumberKey(evt) {
    	   var charCode = (evt.which) ? evt.which : event.keyCode;
    	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
    	      return false;
    	
    	   return true;
    }
    
    
    function showFoodReqDetails(value) {
    	
    	if(value == 1) {
    		 document.getElementById("tr_noOfPeople").style.display = "table-row";  
    		 document.getElementById("tr_foodServiceDetails").style.display = "table-row";
    		 document.getElementById("tr_foodType").style.display = "table-row";
    	} else {
    		document.getElementById("tr_noOfPeople").style.display = "none";
    		document.getElementById("tr_foodServiceDetails").style.display = "none";
    		document.getElementById("tr_foodType").style.display = "none";
    		
    	}
    }
    
    function getMeetingRoomDetails(meetingRoomId) {
    	
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
        	
        	var frmDate = document.getElementById("strBooking_from").value;
        	var toDate = document.getElementById("strBooking_to").value;
        	var frmTime = document.getElementById("strTo_time").value;
        	var toTime = document.getElementById("strTo_time").value;
        	
        	if(frmDate == "" || toDate == null || frmTime == null || toTime == null ) {
        		alert("Enter required(*) field data!");
        	} else {
    	    	var xhr = $.ajax({
    	            url : 'GetMeetingRoomDetails.action?meetingRoomId='+meetingRoomId+'&fromDate='+frmDate+'&toDate='+toDate+'&fromTime='+frmTime+'&toTime='+toTime,
    	          cache : false,
    	        success : function(data) {
    	        	 
    	            	document.getElementById("meetingRoomDetails_div").style.display = "block";
    	            	document.getElementById("meetingRoomDetails_div").innerHTML = data;
    	            	
    	            }
    	        });
    	    	
    	    	
        	}
        	
        }
    }
    
    function addGuest() {
    	
    	var seatingCapacity = "";
    	if(document.getElementById("strCapacity")) {
    		seatingCapacity = document.getElementById("strCapacity").value;
    	}
    	
    	if(seatingCapacity != '') {
    		var count = 0;
    		var strEmpIds = getSelectedValue("strParticipants");
    		var strEmps = strEmpIds.split(",");
    		for(var i = 0; i<strEmps.length;i++){
    			if(strEmps[i] != ''){
    				count++;
    			}
    		}
    		
    		var guestcount = document.getElementById("guestCount").value;
    		
    		for(var i =0; i<=guestcount;i++) {
    			   if(document.getElementById("strGuest_"+i)) {
    					 var guests = document.getElementById("strGuest_"+i).value;
    					 if(guests.trim() != '') {
    						count++;
    					 }
    			   }
    		}
    		
    		//alert("count==>"+count+"==seatingCapacity==>"+seatingCapacity);
    		if(count < parseInt(seatingCapacity)) {
    			
    			guestcount++;
    			var divid = "GuestSubDiv_"+guestcount;
    			var divtag = document.createElement('div');
    			divtag.setAttribute("style", "margin-top: 7px;");
    			divtag.id = divid;
    			
    			var data = "<input type=\"text\" name=\"strGuest\" id=\"strGuest_"+guestcount+"\" onkeypress=\"showNoOfPeople();\" /> "
    					+"<a href=\"javascript:void(0)\" class=\"add-font\" onclick=\"addGuest();\"></a>&nbsp;"
    					+"<a href=\"javascript:void(0)\" class=\"remove-font\"  onclick=\"removeGuest('" + divid + "');\"></a>";
    					
    			divtag.innerHTML = data;
    			document.getElementById("Guestdiv_0").appendChild(divtag);
    			document.getElementById("guestCount").value = guestcount;
    			showNoOfPeople();
    		    
    		} else if(parseInt(seatingCapacity) > 0){
    			alert("Seating Capacity :"+ seatingCapacity);
    		}
       } else {
    	   alert("Select Meeting room");
       }
    }
    
    
    function removeGuest(id) {
    	/* var guestcount = document.getElementById("guestCount").value;
    	guestcount--;
    	document.getElementById("guestCount").value = guestcount; */
    	
    	var row_skill = document.getElementById(id);
    	if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
    		row_skill.parentNode.removeChild(row_skill);
    	}
    	
    	showNoOfPeople();
    }
    
    function checkFields() {
       var guestCnt = document.getElementById("guestCount").value;
       for(var i =0; i<=guestCnt;i++) {
    	   if(document.getElementById("strGuest_"+i)) {
    			 var guests = document.getElementById("strGuest_"+i).value;
    			 if(guests == '') {
    				alert("Please,Enter guest name!");
    				return false;
    			 }
    	   }
       }
       
         var noOfPeople = document.getElementById("strNoOfParticipants").value;
    	 var seatingCapacity = "";
    	 if(document.getElementById("strCapacity")) {
    		seatingCapacity = document.getElementById("strCapacity").value;
    	 }
    	 
    	// alert("noOfPeople==>"+noOfPeople+"==seatingCapacity==>"+seatingCapacity);
    	 if(parseInt(noOfPeople) > parseInt(seatingCapacity)) {
    		 alert("Seating capacity:"+seatingCapacity);
    		 return false;
    	 } 
       
       return true;
    }
    
    function getSelectedValue(selectId) {
    	var choice = document.getElementById(selectId);
    	var exportchoice = "";
    	for ( var i = 0, j = 0; i < choice.options.length; i++) {
    		var value = choice.options[i].value;
    		if(choice.options[i].selected == true && value != "") {
    			
    			if (j == 0) {
    				exportchoice = "," + choice.options[i].value + ",";
    				j++;
    			} else {
    				exportchoice += choice.options[i].value + ",";
    				j++;
    			}
    		}else if(choice.options[i].selected == true && value == ""){
    			exportchoice = "";
    			break;
    		}
    		
    	}
    	//alert("exportchoice==>"+exportchoice);
    	return exportchoice;
    }
    
    
    /* function submitFrm() {
    	var strEmpIds = "";
    	if(document.getElementById("strParticipants")) {
    		strEmpIds = getSelectedValue("strParticipants"); 
        }
    		
    	document.getElementById("strEmpIds").value = strEmpIds;
    	document.getElementById("operation").value = "U";
    	document.getElementById("frmMeetingRoomBooking").submit();
    } */
    
    function showNoOfPeople() {
    	checkCapacity();
    	var count = 0;
    	var strEmpIds = getSelectedValue("strParticipants");
    	var strEmps = strEmpIds.split(",");
    	for(var i = 0; i<strEmps.length;i++){
    		if(strEmps[i] != ''){
    			count++;
    		}
    	}
    	
    	var guestCount = document.getElementById("guestCount").value;
    	
    	for(var i =0; i<=guestCount;i++) {
    		   if(document.getElementById("strGuest_"+i)) {
    				 var guests = document.getElementById("strGuest_"+i).value;
    				 if(guests.trim() != '') {
    					count++;
    				 }
    		   }
    	}
    	
    	document.getElementById("strNoOfParticipants").value = count;
    
    }
    
    
     function getDishes(mealType) {
        var frmDate = document.getElementById("strBooking_from").value;
        var toDate = document.getElementById("strBooking_to").value; 	
       
        var action='GetDishes.action?mealType='+mealType+'&startDate='+frmDate+'&endDate='+toDate ;
        	getContent('dishes_div', action);
       	
       
     } 
    
     function checkCapacity() {
    	 
    	var seatingCapacity = "";
    	if(document.getElementById("strCapacity")) {
    		seatingCapacity = document.getElementById("strCapacity").value;
    	}
    	
    	if(seatingCapacity != '') {
    		var count = 0;
    		var guestcount = document.getElementById("guestCount").value;
    		for(var i =0; i<=guestcount;i++) {
    			   if(document.getElementById("strGuest_"+i)) {
    					 var guests = document.getElementById("strGuest_"+i).value;
    					 if(guests.trim() != '') {
    						count++;
    					 }
    			   }
    		}
    		
    		//alert("guestCount==>"+count);
    		
    		var choice = document.getElementById("strParticipants");
    	    for ( var i = 0;i < choice.options.length; i++) {
    			var value = choice.options[i].value;
    			if(choice.options[i].selected == true && value != "") {
    				count++;
    			}
    			
    			if( count > parseInt(seatingCapacity)) {
    				//alert("total==>"+count+"==Seating=="+seatingCapacity);
    				choice.options[i].selected = false;
    				break;
    			}
    		}
    	} else {
    		alert("Select Meeting room");
    	}
     }
     
     function closePopup() {
    	 
    	 document.getElementById("operation").value = 'C'; 
    	 document.getElementById("frmMeetingRoomBooking").submit(); 
     }
     
     
</script>
<div id="printDiv" class="leftbox reportWidth">
    <s:form id="frmMeetingRoomBooking" name="frmMeetingRoomBooking" action="AddMeetingRoomBookingPopup" method="POST" theme="simple" cssClass="formcss" onsubmit="return checkFields();" >
        <input type="hidden" name="bookingId" value="<%=bookingId%>"/>
        <input type="hidden" id="operation" name="operation" value="<%=operation%>"/>
        <input type="hidden" id="currDate" name="currDate" value="<%=currDate%>"/>
        <s:hidden name="strEmpIds" id="strEmpIds" />
        <s:hidden name="isFoodServiceRequired" id="isFoodServiceRequired" />
        <input type="hidden" id="meetingRoomId" name="meetingRoomId" value="<%=meetingRoomId%>"/> 
        <div style="float:left;width:98%;">
            <table border="0" class="table table_no_border form-table" cellpadding="3" cellspacing="2">
                <tr>
                    <th class="txtlabel alignRight">From Date:<sup>*</sup></th>
                    <td><input type="text" name="strBooking_from" id="strBooking_from" class="validateRequired" value="<%=(String)request.getAttribute("strBooking_from") %>" onchange="getDishes(this.value)" required/></td>
                </tr>
                <tr>
                    <th class="txtlabel alignRight">To Date:<sup>*</sup></th>
                    <td><input type="text" name="strBooking_to" id="strBooking_to" class="validateRequired" value="<%=(String)request.getAttribute("strBooking_to") %>" onchange="getDishes(this.value)" required/></td>
                </tr>
                <tr>
                    <th class="txtlabel alignRight">Between(Time):<sup>*</sup></th>
                    <td>
                        <div class="input-box">
                        	
                            <input type="text" id="strFrom_time" name="strFrom_time" class="validateRequired" value="<%=uF.showData((String)request.getAttribute("strTo_time"),"") %>"  required/>
                            <span class="unit">From:</span>
                        </div>
                        <div class="input-box" style="margin-top: 10px;">
                            <input type="text" id="strTo_time" name="strTo_time" class="validateRequired" value="<%=uF.showData((String)request.getAttribute("strFrom_time"),"")%>"  required/>						  	
                            <span class="unit">To:</span>
                        </div>
                    </td>
                </tr>
                <tr>
                    <th class="txtlabel alignRight">Select Meeting Room:<sup>*</sup></th>
                    <td>
                        <s:select theme="simple" name="strMeetingRoomId" id="strMeetingRoomId" listKey="meetingRoomId" listValue="meetingRoomName"  headerKey="" headerValue="Select Meeting Room"
                            onchange="getMeetingRoomDetails(this.value);" cssClass="validateRequired selectRoom" list="meetingRoomsList" />
                    </td>
                </tr>
                <tr>
                <tr>
                    <td colspan=2 style="border: 0;">
                        <div id="meetingRoomDetails_div" style="display:none;"></div>
                    </td>
                </tr>
                <th class="txtlabel alignRight">Purpose of the Booking:</th>
                <td>
                    <s:textarea rows="3" name="strBookingComment" id="strBookingComment"/>
                </td>
                </tr>
                <%if(operation != null && operation.equals("E")){ %>
                <tr>
                    <th class="txtlabel alignRight">Select Participants:<sup>*</sup></th>
                    <td>
                        <select name="strParticipants" id="strParticipants" class="validateRequired" multiple="multiple" onchange="showNoOfPeople()">
                            <option>Select Participants</option>
                            <%=sbParticipants%>
                        </select>
                    </td>
                </tr>
                <% } else { %>
                <tr>
                    <th class="txtlabel alignRight">Select Participants:<sup>*</sup></th>
                    <td>
                        <s:select name="strParticipants" id="strParticipants" listKey="employeeId" listValue="employeeName" headerKey=""
                            list="participantsList" key="" required="true" cssClass="validateRequired" multiple="true" onchange="showNoOfPeople()"/>
                    </td>
                </tr>
                <% } %>
                <tr>
                    <td></td>
                    <td>
                        <%if(operation != null && operation.equals("E")) { 
                            if(guestList != null && !guestList.isEmpty() && guestList.size()>0) {
                            %>
                        <div>
                            <input type="hidden" name="guestCount" id="guestCount" value="<%=guestList.size() %>">
                            <span><a href="javascript:void(0)" onclick="addGuest();"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Guest</a></span>
                        </div>
                        <div id="Guestdiv_0" class="clr">
                            <%
                                int i = 0;
                                Iterator<String> it = guestList.iterator();
                                while(it.hasNext()) {
                                	String guest = it.next();
                                %>
                            <div id="GuestSubDiv_<%=i %>" style="margin-top: 7px;">
                                <input type="text" id="strGuest_<%=i%>" name="strGuest" value="<%=guest %>" class="validateRequired" onkeypress="showNoOfPeople()"><a onclick="addGuest();" href="javascript:void(0)"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>&nbsp;
                                <a onclick="removeGuest('GuestSubDiv_<%=i %>');" href="javascript:void(0)">
                                 <!-- <img border="0" src="images1/icons/icons/close_button_icon.png" style="height: 15px; width: 16px;"> -->
                                 <i class="fa fa-times-circle cross" aria-hidden="true"></i>
                                 &nbsp;</a>
                            </div>
                            <% i++;
                                } %>
                        </div>
                        <%
                            }else{
                                     %>
                        <div>
                            <input type="hidden" name="guestCount" id="guestCount" value="0">
                            <span style="margin-left: 7px;"><a href="javascript:void(0)" onclick="addGuest();"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Guest</a></span>
                        </div>
                        <div id="Guestdiv_0" style="margin-top: 1px;"></div>
                        <%			 			  
                            }
                            
                            } else { %>
                        <div>
                            <input type="hidden" name="guestCount" id="guestCount" value="0">
                            <span style="margin-left: 7px;"><a href="javascript:void(0)" onclick="addGuest();"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Guest</a></span>
                        </div>
                        <div id="Guestdiv_0"></div>
                        <% } %>
                    </td>
                </tr>
                <tr>
                    <th class="txtlabel alignRight">Do you need F&B Service?</th>
                    <% if(operation != null && operation.equals("E")) { %>
                    <td >
                        <s:radio label="isFoodRequired" name="isFoodRequired" cssStyle="font-size:12px;" list="#{'1':'Yes','2':'No'}" value="{isFoodServiceRequired}" onchange="showFoodReqDetails(this.value)" />
                    </td>
                    <% } else { %>
                    <td >
                        <s:radio label="isFoodRequired" name="isFoodRequired" cssStyle="font-size:12px;" list="#{'1':'Yes','2':'No'}" value="2"  onchange="showFoodReqDetails(this.value)" />
                    </td>
                    <% } %>
                </tr>
                <tr id="tr_noOfPeople" style="display:none;" >
                    <th  class="txtlabel alignRight">No. of People:</th>
                    <td>
                        <s:textfield name="strNoOfParticipants" id="strNoOfParticipants" cssClass="validateRequired"  readonly="readonly"  onkeypress="return isNumberKey(event)" />
                    </td>
                </tr>
                <tr id="tr_foodType" style="display:none;">
                    <th class="txtlabel alignRight">F&B Type:<sup>*</sup></th>
                    <td>
                        <div style="float:left;">
                            <s:select name="mealType" listKey="mealTypeId" listValue="mealTypeName" theme="simple" headerKey="" 
                                headerValue="Select Food Type" list="mealTypeList" key="" cssClass="validateRequired" onchange="getDishes(this.value)" />
                        </div>
                    </td>
                </tr>
                <%if(operation != null && operation.equals("E")){ %>
                <tr id="tr_foodServiceDetails" style="display:none;" >
                    <th class="txtlabel alignRight">F&B Details:<sup>*</sup></th>
                    <td>
                        <div id ="dishes_div" style="float:left;">
                            <select name="strDishIds" id="strDishIds" class="validateRequired" >
                                <option>Select Dish</option>
                                <%=sbDishes%>
                            </select>
                        </div>
                    </td>
                </tr>
                <% } else { %>
                <tr id="tr_foodServiceDetails" style="display:none;">
                    <th class="txtlabel alignRight">F&B Details:<sup>*</sup></th>
                    <td>
                        <div id ="dishes_div" style="float:left;">
                            <s:select name="strDishIds" id="strDishIds" listKey="dishId" listValue="dishName" headerKey="" 
                                headerValue="Select Dish" list="dishList" key="" cssClass="validateRequired" theme="simple"/>
                        </div>
                    </td>
                </tr>
                <% } %>
                <tr>
                    <td></td>
                    <td>
                        <%if(operation != null && operation.equals("E")){ %>
                        <input type="submit" id="submitButton" class="btn btn-primary"  name="strUpdate"  value="Update"  >
                        <%} else { %>
                        <input type="submit" id="submitButton" class="btn btn-primary"  name="strSubmit" value="Book Here!">
                        <% } %>
                       <!--  <input type="button" class="btn btn-danger" name="strCancel" value="Cancel" onclick= "closePopup()" style="margin-left:5px;"> -->
                    </td>
                </tr>
            </table>
        </div>
    </s:form>
</div>
