<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script src="js/jquery.expander.js"></script>

<script>
    $(function () {
    	$("input[name='btnUpdate']").click(function(){
    		$("#frm_editDeleteQuotes").find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#frm_editDeleteQuotes").find('.validateRequired').filter(':visible').prop('required',true);
    	});
    });
   
    
    function updateCancelQuotes(thoughtId, type) {
		//alert("inside update");
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
	    	//alert("Are you sure, you want to update this thought? for " +thoughtId+"==opeartion==>"+type);
	    	var strQuoteBy1 = "";
	    	var strQuotedesc1 = "";
	    	if( document.getElementById("strQuoteBy_"+thoughtId)){
	    		var str = document.getElementById("strQuoteBy_"+thoughtId).value;
	    		strQuoteBy1 = str.replace("&", "::");
	    	}
	    	if(document.getElementById("strQuotedesc_"+thoughtId)){
	    		var str = document.getElementById("strQuotedesc_"+thoughtId).value;
	    		strQuotedesc1 = str.replace("&", "::");
	    	}
	    	if(type == 'U'){
	            var xhr = $.ajax({
	                url : 'EditAndDeleteQuotes.action?thoughtId='+thoughtId+'&operation='+type+'&strQuoteBy1='+strQuoteBy1+'&strQuotedesc1='+strQuotedesc1,
	                cache : false,
	                success : function(data) {
	                	document.getElementById("quoteDataDiv_"+thoughtId).innerHTML = data;
	                }
	            });
	    	}
		}
	    $('#modalInfo').hide();
	}
    
</script>
<%
    String operation = (String) request.getParameter("operation");
    String thoughtId = (String) request.getParameter("thoughtId");
    UtilityFunctions uF = new UtilityFunctions();
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
   if(operation != null && operation.equals("Q_E")) {
    %>
<div>
    <s:form name="frm_editDeleteQuotes" id = "frm_editDeleteQuotes" action="EditAndDeleteQuotes" theme="simple">
        <s:hidden name="operation" id ="operation"/>
        <s:hidden name="thoughtId" id ="thoughtId"/>
        <table class="table table_no_border form-table">
            <tr>
                <td>Quote By:<sup>*</sup></td>
                <td><input type="text" name="strQuoteBy" id="strQuoteBy_<%=thoughtId%>" style="" class="validateRequired" value="<%=(String)request.getAttribute("strQuoteBy") %>" /></td>
            </tr>
            <tr>
                <td>Quote:<sup>*</sup></td>
                <td><textarea rows="3" name="strQuotedesc" id="strQuotedesc_<%=thoughtId%>" style="font-size: 14px; width: 81%;" class="validateRequired" ><%=uF.showData((String)request.getAttribute("strQuotedesc"), "") %></textarea></td>
            </tr>
            <tr>
                <td></td>
                <td><input type="button" name="btnUpdate" class="btn btn-primary" style="margin-top: 5px;" value="Update" onclick="updateCancelQuotes('<%=thoughtId%>', 'U');" /> 
                </td>
            </tr>
        </table>
    </s:form>
</div>
 <% } else if(operation != null && (operation.equals("U") || operation.equals("C"))) { %>
<% 
    List<String> quote = (List<String>)request.getAttribute("alInner");
    if(quote == null) quote = new ArrayList<String>();
    if(quote != null && !quote.isEmpty()) {
    %>
<div style="float: left; width: 100%; margin-bottom: 5px;">
    <!--  <img class="img-circle img-sm" src="userImages/avatar_photo.png" alt="User Image" style="width: 40px !important;height: 40px !important;"> -->
    <%=quote.get(7) %> <!-- Please make this return image path to put in above src  -->
    <div class="comment-text" style="margin-left: 55px;">
        <span class="username" style="font-size: 14px;color: #0089B4;">
            <%=quote.get(4) %> <span style="font-weight:400;">has posted a quote by </span><%=quote.get(2) %>
            <span class="text-muted pull-right">
                <%=quote.get(5) %><% if(((uF.parseToInt(strEmpId) == uF.parseToInt(quote.get(6))) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
                <div style="float: right;">
                    <a href="javascript:void(0);" onclick="editQuotePopup('<%=quote.get(0) %>', 'Q_E');">
                    <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
                    </a>
                    <a href="javascript:void(0);" onclick="deleteYourQuotes('<%=quote.get(0) %>', 'Q_D');">
                    <i class="fa fa-trash" aria-hidden="true"></i>
                    </a>
                </div>
                <%
                    }
                    %>
            </span>
        </span>
        <!-- /.username -->
		<p style="font-size: 14px;width: 60%;font-style:italic;color: rgb(156, 156, 156);"><sup style="color: rgb(109, 109, 109) !important"><i class="fa fa-quote-left" aria-hidden="true"></i></sup><%=quote.get(3) %><sup style="color: rgb(109, 109, 109) !important;"><i class="fa fa-quote-right" aria-hidden="true"></i></sup></p>
    </div>
</div>
<% 			}
    }			
    %> 