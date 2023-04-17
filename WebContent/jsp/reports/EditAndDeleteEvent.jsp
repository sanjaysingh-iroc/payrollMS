<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">
    $(function() {
    	var date_yest = new Date();
        var date_tom = new Date();
        date_yest.setHours(0,0,0);
        date_tom.setHours(23,59,59); 
    	$('input[name=startTime]').datetimepicker({
    		format: 'HH:mm',
    		useCurrent: false,
    		minDate: date_yest
        }).on('dp.change', function(e){ 
        	$('input[name=endTime]').data("DateTimePicker").minDate(e.date);
        });
    	
    	$('input[name=endTime]').datetimepicker({
    		format: 'HH:mm',
    		maxDate: date_tom,
    		useCurrent: false
        }).on('dp.change', function(e){ 
        	$('input[name=startTime]').data("DateTimePicker").maxDate(e.date);
        });
    	
    	$(".startDate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $(".endDate").datepicker('setStartDate', minDate);
        });
        
        $(".endDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $(".startDate").datepicker('setEndDate', minDate);
        });
        
    	$("select[name='strSharing']").multiselect().multiselectfilter();
    });
    
    $(function () {
    	$("input[type='submit']").click(function(){
    		$("#frm_editDeleteEvent").find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#frm_editDeleteEvent").find('.validateRequired').filter(':visible').prop('required',true);
    	});
    });
    
    	
   	function readImageURL(input, targetDiv) {
   		//alert("notice targetDiv==>"+targetDiv);
   	    if (input.files && input.files[0]) {
   	        var reader = new FileReader();
   	        reader.onload = function (e) {
   	            $('#'+targetDiv).attr('src', e.target.result).width(60).height(60);
   	        };
   	        reader.readAsDataURL(input.files[0]);
   	    }
   	}
    
</script>
<%
    String operation = (String) request.getParameter("operation");
    String eventId = (String) request.getParameter("eventId");
    UtilityFunctions uF = new UtilityFunctions();
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
    
    String eventImgPath = (String) request.getAttribute("eventImgPath");
    String eImage = (String) request.getAttribute("eImage");
    String eventImage = (String) request.getAttribute("eventImage");
    String extenstion = (String) request.getAttribute("extenstion");
    List<String> availableExt = (List<String>)request.getAttribute("availableExt");
    if(availableExt == null) availableExt = new ArrayList<String>();
    boolean flag = false;
    if(availableExt.contains(extenstion)) {
    	flag = true;
    }
    if(operation != null && operation.equals("E_E")) {
    %>
<div style="padding-bottom: 15px;">
    <s:form name="frm_editDeleteEvent"  id="frm_editDeleteEvent" action="EditAndDeleteEvent" theme="simple" enctype="multipart/form-data">
        <s:hidden name="eventId" id="eventId"/>
        <table class="table table_no_border form-table">
            <tr>
                <td>Title:<sup>*</sup></td>
                <td colspan="2"><input type="text"  name="strEventName" id="strEventName_<%=eventId %>" class="validateRequired" value="<%=(String)request.getAttribute("strEventName") %>" /></td>
            </tr>
            <tr>
                <td>Event:<sup>*</sup></td>
                <td colspan="2"><textarea rows="3" name="strEventdesc" id="strEventdesc_<%=eventId%>" class="validateRequired" ><%=uF.showData((String)request.getAttribute("strEventdesc"), "") %></textarea></td>
            </tr>
            <tr>
                <td>Share with:<sup>*</sup></td>
                <td colspan="2"><select name="strSharing" id="strSharing_<%=eventId%>" style="width:50%;" multiple="multiple" class="validateRequired" >
                    <%=(String)request.getAttribute("sbSharing") %>
                    </select>
                </td>
            </tr>
            <tr>
                <td></td>
                <td>Start Date:<sup>*</sup><br/><input type="text" style="width:85px;" name="strStartDate" id="strStartDate_<%=eventId%>" class="validateRequired" value="<%=(String)request.getAttribute("strStartDate") %>"/></td>
                <td>
                    End Date:<sup>*</sup><br/>
                    <s:textfield name="strEndDate" cssStyle="width:85px;" id="strEndDate"  cssClass="validateRequired endDate"></s:textfield>
                </td>
            </tr>
            <%
                String time = uF.timeNow();
                   String newTime = time.substring(0,time.lastIndexOf(":"));
                %>
            <tr>
                <td></td>
                <td>Start Time:<sup>*</sup><br><input type="text" id="startTime" style="width:60px;" name="startTime" style="width:60px;" class="validateRequired" value="<%=newTime%>" /></td>
                <td>End Time:<sup>*</sup><br><input type="text" id="endTime_<%=eventId%>" name="endTime" style="width:60px;" class="validateRequired" value="<%=(String)request.getAttribute("endTime")%>" /></td>
            </tr>
            <tr>
                <td>Location:<sup>*</sup></td>
                <td colspan="2"><input type="text" name="strLocation" id="strLocation_<%=eventId%>" class="validateRequired" value="<%=(String)request.getAttribute("strLocation") %>"/></td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
           
                	<%if(eImage!=null && !eImage.equals("")){
							if(extenstion!=null && (extenstion.equalsIgnoreCase("jpg") || extenstion.equalsIgnoreCase("jpeg") || extenstion.equalsIgnoreCase("png") || extenstion.equalsIgnoreCase("bmp") || extenstion.equalsIgnoreCase("gif"))){ 
						%>	
								<%=eventImage%>
						<% } else { 
									if(flag && eventImgPath!=null && !eventImgPath.equals("")){
							%>
										<div id="tblDiv" style="float: left; width: 100%;">
											<p style="color:gray;">&nbsp;<%=eImage%></p>
										</div>
										<img height="62" width="70" class="lazy" id="eventImage" style="border: 1px solid #CCCCCC;" src="userImages/event_icon.png" data-original="/" /> 
								<%			
									}else{
								%>
										<div style="float:left; padding: 60px 40px; font-size: 14px; border: 1px solid #CCCCCC;">Image Preview not available.</div>		
								<% }
							  }
			
						} %>
					</div>
                    <input type="file" id="strEventImage_<%=eventId%>" name="strEventImage" size="5" style="height: 22px; margin-top: 10px; vertical-align: top; font-size: 11px;" onchange="readImageURL(this, 'eventImage');"></span>
                   
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
                    <s:submit name="submit" cssClass="btn btn-primary" cssStyle="margin-top:10px;" value="Post" />
                </td>
            </tr>
        </table>
    </s:form>
    
</div>
<% } else if(operation != null && (operation.equals("U") || operation.equals("C"))) { %>
<% 
    List<String> event = (List<String>)request.getAttribute("alInner");
    if(event == null) event = new ArrayList<String>();
    if(event != null && !event.isEmpty()) {
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
           <%if(eImage!=null && !eImage.equals("")){
				if(extenstion!=null && (extenstion.equalsIgnoreCase("jpg") || extenstion.equalsIgnoreCase("jpeg") || extenstion.equalsIgnoreCase("png") || extenstion.equalsIgnoreCase("bmp") || extenstion.equalsIgnoreCase("gif"))){ 
			%>	
					<%=eventImage%>
			<% } else { 
					if(flag && eventImgPath!=null && !eventImgPath.equals("")){
				%>
						<div id="tblDiv" style="float: left; width: 100%;">
						<a href="javascript:void(0);" onclick="viewEventFilePopup('<%=eventId%>');event.preventDefault();" style="color:gray;">&nbsp;<%=eImage%></a>
						</div>
				<%	}else{%>
						 <div style="float:left; padding: 60px 40px; font-size: 14px; border: 1px solid #CCCCCC;">Preview not available.</div>		
				 <% }
				}
			} %>
            <div class="attachment-pushed">
                <h4 class="attachment-heading"><%=event.get(4)%></h4>
                <%=event.get(17)%>
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
<% 			}
    }			
    %>