<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TViewWLocation %>" name="title"/>
</jsp:include> --%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
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
	$("body").on('click','#closeButton1',function(){
		$(".proDialog").removeAttr('style');
		$("#proBody").height(400);
		$("#profileInfo").hide();
    });
	$("body").on('click','.close',function(){
		$(".proDialog").removeAttr('style');
		$("#proBody").height(400);
		$("#profileInfo").hide();
    });
});

<%-- $(document).ready(function() { 

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

}); --%>

function addWLocation(strOrgId, strOfficeTypeId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Work Location');
	if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	$.ajax({
		url : 'AddWLocation.action?strOrg='+strOrgId+'&param='+strOfficeTypeId+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function editWLocation(strOfficeTypeId, strOfficeId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Work Location');
	if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	$.ajax({
		url : "AddWLocation.action?param="+strOfficeTypeId+"&operation=E&ID="+strOfficeId+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 


function addOrganisation(userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Organisation');
	if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	$.ajax({
		url : "AddOrganisation.action?userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function editOrganisation(strOrgId, userscreen, navigationId, toPage) {
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Organisation');
	if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	$.ajax({
		url : "AddOrganisation.action?operation=E&ID="+strOrgId+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 


function addWLocationType(strOrgId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Office Type');
	$.ajax({
		url : "AddWlocationType.action?param="+strOrgId+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 


function editWLocationType(strOrgId, strOfficeId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Office Type');
	$.ajax({
		url : "AddWlocationType.action?param="+strOrgId+"&operation=E&ID="+strOfficeId+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function checkImageSize(id){
	 if (window.File && window.FileReader && window.FileList && window.Blob){
       var fsize = $('#'+id)[0].files[0].size;
       var ftype = $('#'+id)[0].files[0].type;
       var fname = $('#'+id)[0].files[0].name;
       var flag = true;
       switch(ftype){
           case 'image/png':
           case 'image/gif':
           case 'image/jpeg':
           case 'image/pjpeg':
               if(fsize>500000){ //do something if file size more than 1 mb (1048576)
                   alert("You are trying to upload a larger file than 500kb.");
                   flag = false;
               }else{
                   //alert(fsize +" bites\nYou are good to go!");
                   flag = true;
               }
               break;
           default:
               alert('Unsupported File!');
           	flag = false;
       }
       if(flag){
       	return true;
       } else {
       	return false;
       }
       
   }else{
       alert("Please upgrade your browser, because your current browser lacks some new features we need!");
       return false;
   }
}


function editLegalEntityLogo(orgId,type) {
	$("#profileInfo").show();
	document.getElementById('orgId').value = orgId;
	document.getElementById('imageType').value = type;
}

</script>
<div id="printDiv" class="leftbox reportWidth">		
<%
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	UtilityFunctions uF = new UtilityFunctions();
	Map hmOfficeTypeMap = (Map)request.getAttribute("hmOfficeTypeMap"); 
	Map hmOfficeLocationMap = (Map)request.getAttribute("hmOfficeLocationMap");
	Map<String, String> hmEmpCount = (Map<String, String>)request.getAttribute("hmEmpCount");
	Map<String, String> hmOrgEmpCount = (Map<String, String>) request.getAttribute("hmOrgEmpCount");
	Map hmOrganistaionMap = (Map)request.getAttribute("hmOrganistaionMap");
	//out.println(hmOfficeTypeMap);
	String isOrgLimit = (String)request.getAttribute("isOrgLimit");
	String isLocLimit = (String)request.getAttribute("isLocLimit");
	
	Map<String, String> hmOfficeTypeWlocCount = (Map<String, String>) request.getAttribute("hmOfficeTypeWlocCount");
	if(hmOfficeTypeWlocCount == null) hmOfficeTypeWlocCount = new HashMap<String, String>();

	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
	//System.out.println("hmFeatureUserTypeId ===>> " + hmFeatureUserTypeId);
	//System.out.println("strUsertypeId ===>> " + strUsertypeId);
%>
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE, ""); %>	
<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))) { %> --%>
	<%
	if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ADD_LEGAL_ENTITY)) && hmFeatureUserTypeId.get(IConstants.F_ADD_LEGAL_ENTITY).contains(strUsertypeId)) { %>
		<!-- <div style="float:left; margin:0px 0px 10px 0px"> <a href="AddOrganisation.action" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"> + Add New Organisation</a></div> -->
		<%if(uF.parseToBoolean(isOrgLimit)){ %>
			<div style="float:left; margin:0px 0px 10px 0px"> <a href="javascript:void(0)" onclick="alert('You have reached maximum organisation limit. Please contact the support team to increase your organisations limit.');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Legal Entity</a></div>
		<%} else { %>
			<div style="float:left; margin:0px 0px 10px 0px"> <a href="javascript:void(0)" onclick="addOrganisation('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Legal Entity</a></div>
		<%} %>
	<% } %>
<%-- <% } %> --%>  
<div class="clr"></div>

<div>
         <ul class="level_list">
		<% 
		if(hmOrganistaionMap != null && hmOrganistaionMap.size()>0) {
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
					<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DELETE_LEGAL_ENTITY)) && hmFeatureUserTypeId.get(IConstants.F_DELETE_LEGAL_ENTITY).contains(strUsertypeId)) { %>
						<%if(uF.parseToInt(hmOrgEmpCount.get(strOrgId)) > 0) {
							String strMsg = "Sorry! You have " + uF.parseToInt(hmOrgEmpCount.get(strOrgId)) + " employees added with this Legal Entity, therefore we cannot delete the Legal Entity. To consider this option, please ensure that you have ZERO Employees added.";
						%>
							<a href="javascript:void(0);" class="del" style="color: rgb(233, 0, 0);" onclick="alert('<%=strMsg %>')"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
						<% } else { %>
							<a href="AddOrganisation.action?operation=D&ID=<%=strOrgId%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" style="color: rgb(233, 0, 0);" class="del" onclick="return confirm('Are you sure you wish to delete this organisation?')"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
						<% } %>
					<% } %>
					 <a href="javascript:void(0)" class="edit_lvl" onclick="editOrganisation('<%=strOrgId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                    
					 <strong><%=alOrg.get(2)%> [<%=alOrg.get(1)%>]</strong>
						 
						 <div>
						   <div style="float:left;">
							 <a href="javascript:void(0);" class="fa fa-edit" onclick="editLegalEntityLogo('<%=strOrgId %>','ORG_LOGO');" title="Edit Logo">&nbsp;</a>
	                         <img class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=alOrg.get(3)%>" height="60" />
						   </div>
						   
						     <div style="float:left;margin: 15px 0px 0px 30px;">
						 	  <a href="javascript:void(0);" class="fa fa-edit" onclick="editLegalEntityLogo('<%=strOrgId %>','ORG_LOGO_SMALL');" title="Edit Small Logo">&nbsp;</a>
                              <img class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=alOrg.get(5)%>" height="30" />
						   </div> 
						</div> 
					<ul style="clear: both;">
							
					<li class="addnew desgn" style="padding-top: 5px;padding-bottom: 5px;">
					
					<%-- <a href="AddWlocationType.action?param=<%=strOrgId %>"" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"> + Add New Office Type</a> --%>
					<a href="javascript:void(0)" onclick="addWLocationType('<%=strOrgId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Office Type</a>
					
					</li>
					
					<li class="desgn">
					<%
						for(int d=0; d<alOfficeType.size(); d+=4){
							String strOfficeTypeId = (String)alOfficeType.get(d);
	
							List alOfficeLocation = (List)hmOfficeLocationMap.get(strOfficeTypeId);
							if(alOfficeLocation==null)alOfficeLocation=new ArrayList();
						
							int nOfficeLocCount = uF.parseToInt(hmOfficeTypeWlocCount.get(strOfficeTypeId));
					%>
					
					<li> 
					<%
						if(nOfficeLocCount > 0){
							String strMsg = "Sorry! You have " + nOfficeLocCount + " work location added with this office type, therefore we cannot delete the Office Type. To consider this option, please ensure that you have ZERO Work location added.";
					%>
                    	<a href="javascript:void(0)" class="del" style="color: rgb(233, 0, 0);" onclick="alert('<%=strMsg %>')">  <i class="fa fa-trash" aria-hidden="true"></i>  </a>
                    <%} else { %>
						<a href="AddWlocationType.action?operation=D&ID=<%=strOfficeTypeId %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" style="color: rgb(233, 0, 0);" class="del" onclick="return confirm('Are you sure you wish to delete this office type?')">  <i class="fa fa-trash" aria-hidden="true"></i> </a>
					<%} %>
                     
                    <a href="javascript:void(0)" class="edit_lvl" onclick="editWLocationType('<%=strOrgId %>', '<%=strOfficeTypeId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                    
                     <strong><%=alOfficeType.get(d+2)%> [<%=alOfficeType.get(d+1)%>]</strong>  
                      
                    <ul>
					
					<li class="addnew desgn" style="padding-top: 5px;padding-bottom: 5px;"> 
						<%if(uF.parseToBoolean(isLocLimit)){ %>
							<a href="javascript:void(0)" onclick="alert('You have reached maximum work location limit. Please contact the support team to increase your work location limit.');"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Work Location</a>
						<%} else { %>
							<a href="javascript:void(0)" onclick="addWLocation('<%=strOrgId%>', '<%=strOfficeTypeId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Work Location</a>
						<%} %>
					</li>	
						<%
						for(int g=0; g<alOfficeLocation.size(); g+=20){
							
							%>
							
							<li >
                            <%if(uF.parseToInt(hmEmpCount.get(alOfficeLocation.get(g))) > 0) { 
							String strMsg = "Sorry! You have " + uF.parseToInt(hmEmpCount.get(alOfficeLocation.get(g))) + " employees added with this Work Location, therefore we cannot delete the Work Location. To consider this option, please ensure that you have ZERO Employees added.";
							%>
								<a href="javascript:void(0);" class="del" onclick="alert('<%=strMsg %>')" style="color: rgb(233, 0, 0);">  <i class="fa fa-trash" aria-hidden="true"></i>  </a>
							<% } else { %>
                            <a href="AddWLocation.action?operation=D&ID=<%=alOfficeLocation.get(g)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" style="color: rgb(233, 0, 0);" class="del" title="Delete Location" onclick="return confirm('Are you sure you wish to delete this work location?')">  <i class="fa fa-trash" aria-hidden="true"></i>  </a>
                            <% } %>
                             
                            <a href="javascript:void(0);" class="edit_lvl" title="Edit Location" 
                            onclick="editWLocation('<%=alOfficeType.get(d)%>', '<%=alOfficeLocation.get(g)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> 
                            
                            <strong><%=alOfficeLocation.get(g+1)%>, <%=alOfficeLocation.get(g+7)%>, <%=alOfficeLocation.get(g+2)%></strong>
                             <span class="user_no"><i class="fa fa-users"></i> : <%=uF.showData(hmEmpCount.get(alOfficeLocation.get(g)), "0") %> </span>
                            </li>	
						<% } %>
						</ul>
						
					<% } %>		
					
                 		</li>
                 	</ul>
                 </li>                  
		<% } %>
		<%} %> 
		 </ul>
         
     </div>	
		
	</div>
	
		<%-- <div id="myModal" class="modal fade">
		    <div class="modal-dialog" style="width: 350px;">
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title">Update Legal Entity Logo</h4>
		            </div>
		            <div class="modal-body">
						<s:form theme="simple" name="uploadImage" action="UploadImage" enctype="multipart/form-data" method="post" onsubmit="return checkImageSize();">
						<table class="table table-hover">
							<tr>
								<td>
									<s:hidden name="imageType" value="ORG_LOGO"></s:hidden>
									<s:hidden name="orgId" id="orgId"></s:hidden>
									<s:hidden name="userscreen"></s:hidden>
									<s:hidden name="navigationId"></s:hidden>
									<s:hidden name="toPage"></s:hidden>
									<s:file name="empImage" id="empImage" size="15"></s:file>
								</td>
							</tr>
							<tr>
								<td><span style="color:#999999; float:left; width:100%;">Image size must be smaller than or equal to 250px by 60px</span>
									<span style="color:#999999; float:left; width:100%;">Image size must be smaller than or equal to 500kb.</span>
								</td>
							</tr>
							<tr>
								<td align="center">
							        <s:submit value="Upload" cssClass="btb btn-success" cssStyle="padding: 3px;"></s:submit>
							    </td>
							</tr>
							</table>
						</s:form>
		            </div>
		        </div>
		    </div>
		</div> --%>
			
<div id="editWLocationid"></div>
<div id="addWLocationid"></div>
<div id="addOrgid"></div>
<div id="editOrgDivId"></div>
<div id="addWLocationTypeId"></div>
<div id="editWLocationTypeId"></div>
<div class="modal" id="profileInfo" role="dialog">
    <div class="modal-dialog proDialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
            	<button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title1">Update Legal Entity Logo</h4>
            </div>
            <div class="modal-body1" id="proBody" style="height:400px;overflow-y:auto;padding-left: 25px;">
            	<s:form theme="simple" name="uploadImage" action="UploadImage" enctype="multipart/form-data" method="post" onsubmit="return checkImageSize();">
						<table class="table table_no_border">
							<tr>
								<td>
									<s:hidden name="imageType" id="imageType"></s:hidden>
									<s:hidden name="orgId" id="orgId"></s:hidden>
									<s:hidden name="userscreen"></s:hidden>
									<s:hidden name="navigationId"></s:hidden>
									<s:hidden name="toPage"></s:hidden>
									<s:file name="empImage" id="empImage" size="15"></s:file>
								</td>
							</tr>
							<tr>
								<td><span style="color:#999999; float:left; width:100%;">Image size must be smaller than or equal to 250px by 60px</span>
									<span style="color:#999999; float:left; width:100%;">Image size must be smaller than or equal to 500kb.</span>
								</td>
							</tr>
							<tr>
								<td align="center">
							        <s:submit value="Upload" cssClass="btb btn-success" cssStyle="padding: 3px;"></s:submit>
							    </td>
							</tr>
							</table>
						</s:form>
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
