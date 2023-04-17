<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="javax.swing.Icon"%>
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
String strTitle = request.getParameter("title");

String strSubNav = (String)request.getAttribute("NAV_TRAIL");

List alNotice = (List)session.getAttribute("NOTICE");
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

%>

<script type="text/javascript">

function submitTicket() {

	var dialogEdit = '#help';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true, 
		resizable : true,
		height : 600,
		width : 670,
		modal : true,
		//title : 'Submit your ticket',
		title : 'User Guide',
		open : function() {
			var xhr = $.ajax({
				//url : "SubmitTicket.action",
				url : "UserGuide.action",
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
			xhr = null;

		},
		overlay : {
			backgroundColor : '#000',
			opacity : 0.5
		}
	});

	$(dialogEdit).dialog('open');
}


hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';

<%if(CF!=null && CF.isTermsCondition() && strUserType!=null && !CF.isForcePassword() && alNotice!=null && alNotice.size()>0){ %>
$(function () {
    $('#js-news').ticker(); 
}); 
<%}%>

function clearField(elementId){
	document.getElementById(elementId).value = '';
}
function fillField(elementId, num){
	if(document.getElementById(elementId).value=='' && num==1){
		document.getElementById(elementId).value="First Name";
	}
	if(document.getElementById(elementId).value=='' && num==2){
		document.getElementById(elementId).value="Last Name";
	} 
	if(document.getElementById(elementId).value=='' && num==3){
		document.getElementById(elementId).value="From Date";
	}
	if(document.getElementById(elementId).value=='' && num==4){
		document.getElementById(elementId).value="To Date";
	}
}
</script>
 
   
<div class="pagetitle">
<div style="float:left;"><span><%=strTitle%> </span>
<p style="font-style: normal; font-size: 10px;"><%=(strSubNav!=null)?strSubNav:""%></p>
</div>

<%if(CF!=null && CF.isTermsCondition() && strUserType!=null && !CF.isForcePassword()){ %>

 <%-- <div class="" style="float:left; margin:0 0 0 2%">        
              <s:form name="SearchEmployee" action="SearchEmployee" theme="simple">
            <div style="float:left; font-size:12px; line-height:22px; width:380px">
                <span style="float:left;display:block; width:110px">Search Buddy :</span>
                <div style="border:solid 1px #68AC3B; margin:0px 0px 0px 5px; float:right; -moz-border-radius: 3px;	-webkit-border-radius: 3px;	border-radius: 3px;">
	                <div style="float:left">
	                	<input type="text" style="margin-left: 0px; border:0px solid #ccc; width:170px; box-shadow:0px 0px 0px #ccc" 
                        id="strFirstName" name="strFirstName" onclick="clearField(this.id);" onblur="fillField(this.id, 1);" value="First Name"> 
	              		<!--  <input type="text" style="margin-left: 5px;" id="strLastName" name="strLastName" onclick="clearField(this.id);" onblur="fillField(this.id, 2);" value="Last Name">-->
	              	</div>
	             	 <div style="float:right">
	                	<input type="submit" value="Search"  class="input_search" >
	                </div>
            	</div>
            </div>
            </s:form>
    </div> --%>

	<!-- <div style="float:right"><a href="javascript:void(0)" onclick="submitTicket();" class="help">help</a></div> -->
	<div style="float:right"><a href="javascript:void(0)" class="help">help</a></div>

	<!-- <div style="float:right"><a href="javascript:void(0)" class="print" onclick="printData('printDiv');">print</a></div> -->
	<!-- <div style="float:right"><a href="javascript:void(0)" class="print">print</a></div> -->

        <div id="announcement" style="float:right; width:520px">
        <ul id="js-news" class="js-hidden">
        
        <%
        int i=0;
        for(i=0;alNotice!=null && i<alNotice.size(); i++){
            Map hm = (Map)alNotice.get(i);
            if(hm==null)hm=new HashMap();
        %>
        
        <li class="news-item"><a href="javascript:void(0)" onclick="return hs.htmlExpand(this)"><%=(String)hm.get("HEADLINE_LIMITED") %> [<%=(String)hm.get("DISPLAY_DATE") %>]</a>
            <div class="highslide-maincontent">
                <h3><%=(String)hm.get("HEADLINE") %></h3>
                <%=(String)hm.get("CONTENT") %>
            </div>
        </li>
        
        <%} %>
        </ul>
        </div>
<%} %>

   
</div>


<div id="help"></div>