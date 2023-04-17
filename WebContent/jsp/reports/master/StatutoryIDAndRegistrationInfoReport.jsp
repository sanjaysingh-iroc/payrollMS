<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>


<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TReportStatutaryIdAndRegInfo %>" name="title"/>
</jsp:include> --%>

<div id="printDiv" class="leftbox reportWidth">
 
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript">
$(function() {
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
});
 
$(document).ready(function() {

    $('a.poplight[href^=#]').click(function() {
        var popID = $(this).attr('rel'); //Get Popup Name
        var popURL = $(this).attr('href'); //Get Popup href to define size

        //Pull Query & Variables from href URL 
        var query= popURL.split('?');
        var dim= query[1].split('&'); 
        var popWidth = dim[0].split('=')[1]; //Gets the first query string value
  
        //Fade in the Popup and add close button
        $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

        //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
        var popMargTop = ($('#' + popID).height() + 80) / 2;
        var popMargLeft = ($('#' + popID).width() + 80) / 2;

        //Apply Margin to Popup
        $('#' + popID).css({
            'margin-top' : -popMargTop,
            'margin-left' : -popMargLeft
        });

        //Fade in Background
        $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
        $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies

        return false;
    });

    //Close Popups and Fade Layer
    $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
        $('#fade , .popup_block').fadeOut(function() {
            $('#fade, a.close').remove();  //fade them both out
        });
        return false;
    });

});


function editStatutoryIdAndRegInfo(strOrgId, userscreen, navigationId, toPage) {
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Update Statutory ID & Registration Information');
	$.ajax({
		url : 'UpdateStatutoryIdAndRegInfo.action?operation=E&ID='+strOrgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editStatutoryIdAndRegInfoLocation(strLocationId, userscreen, navigationId, toPage) {
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Update Statutory ID & Registration Information of location');
	$.ajax({
		url : 'UpdateStatutoryIdLocation.action?operation=E&ID='+strLocationId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


</script>
		
<%
UtilityFunctions uF = new UtilityFunctions();

Map hmOfficeTypeMap = (Map)request.getAttribute("hmOfficeTypeMap"); 
Map hmOfficeLocationMap = (Map)request.getAttribute("hmOfficeLocationMap");
Map<String, String> hmEmpCount = (Map<String, String>)request.getAttribute("hmEmpCount");
Map<String, String> hmOrgEmpCount = (Map<String, String>) request.getAttribute("hmOrgEmpCount");
Map hmOrganistaionMap = (Map)request.getAttribute("hmOrganistaionMap");

String userscreen = (String)request.getAttribute("userscreen");
String navigationId = (String)request.getAttribute("navigationId");
String toPage = (String)request.getAttribute("toPage");

%>


<div class="clr"></div>

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
<div>
         <ul class="level_list">
		<% 
			Set setOrganisationMap = hmOrganistaionMap.keySet();
			Iterator itOrg = setOrganisationMap.iterator();
			
			while(itOrg.hasNext()){
				String strOrgId = (String)itOrg.next();
				List alOrg = (List)hmOrganistaionMap.get(strOrgId);
				if(alOrg==null)alOrg=new ArrayList();
					
				List alOfficeType = (List)hmOfficeTypeMap.get(strOrgId);
				if(alOfficeType==null)alOfficeType=new ArrayList();
				%>
					
					<li>
						 <a href="javascript:void(0)" onclick="editStatutoryIdAndRegInfo('<%=strOrgId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						 <strong><%=alOrg.get(2)%> [<%=alOrg.get(1)%>]</strong>
						<ul>
					<%
						for(int d=0; d<alOfficeType.size(); d+=4){
						String strOfficeTypeId = (String)alOfficeType.get(d);

						List alOfficeLocation = (List)hmOfficeLocationMap.get(strOfficeTypeId);
						if(alOfficeLocation==null)alOfficeLocation=new ArrayList();
						
					%>
					
						<% for(int g=0; g<alOfficeLocation.size(); g+=20) { %>
							
							<li>
								<a href="javascript:void(0)" onclick="editStatutoryIdAndRegInfoLocation('<%=alOfficeLocation.get(g)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
	                            <strong><%=alOfficeLocation.get(g+1)%>, <%=alOfficeLocation.get(g+7)%>, <%=alOfficeLocation.get(g+2)%></strong>
	                             <span class="user_no"> : <%=uF.showData(hmEmpCount.get(alOfficeLocation.get(g)), "0") %> </span>
                            </li>	
						<% } %>
				<% } %>		
                 	</ul>
                 </li>
                 
                 
                 		
<%-- <div id="popup_name<%=strOrgId %>" class="popup_block" >						
	<s:form theme="simple" name="uploadImage" action="UploadImage" enctype="multipart/form-data" method="post">
	  <table width="450px">
	  <tr>
	  <td>
	    <s:hidden  name="imageType" value="ORG_LOGO"></s:hidden>
	    <input type="hidden"  name="orgId" value="<%=strOrgId %>" />
		<s:file name="empImage"></s:file>
		
		</td>
		</tr>
		<tr><td >
		<span style="color:#efefef">Image size must be smaller than or equal to 250px by 60px</span>
		     </td>
		</tr>
		
			<tr>
			
		 	<td align="right">
		        <s:submit value="Upload" cssClass="input_button"></s:submit>
		    </td>
		</tr>
		</table>
	</s:form>
	
</div> --%>
                 
		<% } %>
		
		 </ul>
     </div>	
		
</div>
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
<div id="editStatIdRegInfoDIV"></div>
<div id="editStatIdRegInfoLocationDIV"></div>
