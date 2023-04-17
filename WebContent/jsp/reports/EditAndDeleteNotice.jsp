<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<script>
    $(document).ready( function () {
    	$("input[name='btnUpdate']").click(function(){
    		$(".validateRequired").prop('required',true);
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
    });
    
    
    
    $("#frm_EditAndDeleteNotice").submit(function(e) {
    	e.preventDefault();
    	var form_data = $("form[name='frm_EditAndDeleteNotice']").serialize();
     	$.ajax({
     		type: 'POST',
    		url : "EditAndDeleteNotice.action",
    		data: form_data,
    		cache : false,
    		success : function(res) {
    			$("#divResult").html(res);
    		}, 
    		error : function(err) {
    			$.ajax({ 
    				url: 'AddUpdateViewHubContent.action?type=A',
    				cache: true,
    				success: function(result){
    					$("#divResult").html(result);
    		   		}
    			});
    		}
    	});
     	$("#modalInfo").hide();
    });
    
</script>
<%
    String operation = (String) request.getParameter("operation");
    String noticeId = (String) request.getParameter("noticeId");
    UtilityFunctions uF = new UtilityFunctions();
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
    //if(operation != null && operation.equals("E")) {
    %>
<div style="float: left; width: 100%; padding-bottom: 15px;">
    <form name="frm_EditAndDeleteNotice" id="frm_EditAndDeleteNotice" action="EditAndDeleteNotice">
        <div style="float: left; width: 100%;">
        <s:hidden name="operation" id="operation" />
	    <s:hidden name="noticeId" id="noticeId" />
               <table class="table form-table table_no_border">
                   <tr>
                       <td>Title:<sup>*</sup></td>
                       <td colspan="2"><input type="text"  name="heading" id="heading_<%=noticeId%>" class="validateRequired" value="<%=(String)request.getAttribute("heading")%>" /></td>
                   </tr>
                   <tr>
                       <td>Notice:<sup>*</sup></td>
                       <td colspan="2"><textarea rows="3" name="content" id="content_<%=noticeId%>" class="validateRequired"><%=uF.showData((String)request.getAttribute("content"), "")%></textarea></td>
                   </tr>
                   <tr>
                       <td></td>
                       <td>Start Date:<sup>*</sup><br><input type="text" name="displayStartDate" id="displayStartDate_<%=noticeId%>"  class="validateRequired startDate" value="<%=(String)request.getAttribute("displayStartDate")%>"/></td>
                       <td>End Date:<sup>*</sup><br><input type="text" name="displayEndDate" id="displayEndDate_<%=noticeId%>" class="validateRequired endDate" value="<%=(String)request.getAttribute("displayEndDate")%>" /></td>
                   </tr>
                   <tr>
                       <%
                           String isPublish = (String) request.getAttribute("ispublish");
                           String isPublish1Checked ="";
                           String isPublish2Checked ="";
                           if(uF.parseToBoolean(isPublish)){
                           	isPublish1Checked ="checked";
                           	isPublish2Checked ="";
                           } else if(!uF.parseToBoolean(isPublish)){
                           	isPublish1Checked ="";
                           	isPublish2Checked ="checked";
                           }	
                           %>
                       <td></td>
                       <td colspan="2"><input type="radio" name="ispublish" id="ispublish1_<%=noticeId%>"  <%=isPublish1Checked %> >Publish
                           <input type="radio" name="ispublish" id="ispublish2_<%=noticeId%>"  <%=isPublish2Checked %> >Unpublish
                       </td>
                   </tr>
                   <tr>
                       <td></td>
                       <td colspan="2"><s:submit name="btnUpdate" id="btnUpdate" cssClass="btn btn-primary" value="Submit"></s:submit>
                       <%-- <input type="button" name="btnUpdate" class="btn btn-primary" value="Update" onclick="updateCancelNotice('<%=noticeId %>', 'U');" /> --%>
                     </td>
                   </tr>
               </table>
        </div>
    </form>
</div>


<%-- <% } else if(operation != null && (operation.equals("U") || operation.equals("C"))) { %>
<% 
    List<String> notice = (List<String>)request.getAttribute("alInner");
    if(notice == null) notice = new ArrayList<String>();
    if(notice != null && !notice.isEmpty()) {
    %>
<div class="box-footer box-comments" id="mainNoticeDiv_<%=notice.get(0) %>" style="padding: 5px;">
    <div class="box-comment" id="noticeDataDiv_<%=notice.get(0) %>">
        <img class="img-circle img-sm" src="userImages/avatar_photo.png" alt="User Image" style="width: 40px !important;height: 40px !important;">
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
                    <%=notice.get(7) %><%=notice.get(11)%>
                </span>
            </span>
            <% if((uF.parseToInt(strEmpId) == uF.parseToInt(notice.get(6)) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
            <p style="font-size: 14px;width: 60%;font-style:italic;color: rgb(156, 156, 156);"><%=notice.get(4) %></p>
            <p><span class="label label-warning">Start Date: <%=notice.get(1) %></span>&nbsp&nbsp<span class="label label-danger"> End Date: <%=notice.get(2) %></span></p>
            <% } else { %>
            <p style="font-size: 14px;width: 60%;font-style:italic;color: rgb(156, 156, 156);"><%=notice.get(4) %></p>
            <% } %>
        </div>
    </div>
</div>
<%}
    }			
    %> --%>