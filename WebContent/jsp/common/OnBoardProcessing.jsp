<%@page import="java.util.Arrays"%>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<style>
    li > span {
    top: 0px;	
    }
    
    hr {
	margin-top: 10px;
	margin-bottom: 10px;
	}
.about-header > a {
float: right;

}

 a.close-font:before {
	font-size: 25px;
 }
 
 .profile-user-img{
 padding: 0px;
 }
 
 .manual_title{
font-size: 18px;
font-weight: bold;
width: 100%;
color: #346897;
padding: 5px;
}
.manual_body {
line-height: 24px;
text-align: left;
padding: 15px;
}
</style>

<script type="text/javascript" charset="utf-8">
function GetXmlHttpObject() {
    if (window.XMLHttpRequest) {
            return new XMLHttpRequest();
    }
    if (window.ActiveXObject) {
            return new ActiveXObject("Microsoft.XMLHTTP");
    }
    return null;
}
</script>
<script type="text/javascript">

$("#org").jOrgChart({
	chartElement : '#chart',
	dragAndDrop  : false
});

</script>

<script type="text/javascript">

$(document).ready(function(){
	
/* ===start parvez date: 28-10-2021=== 
	Note: disabled CSS File (login-form-elementsNew_two) which is Overriding	
*/

	$('#login-form-elementsNew_two').prop('disabled', true);
	
/* ===end parvez date: 28-10-2021=== */
	
	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
		
	$("body").on('click','#nextButton1',function(){
		$("#page2").show();
		$("#page1").hide();
		$("#page3").hide();
		$("#page4").hide();
		$("#page5").hide();
		$("#page6").hide();
		$("#page7").hide();
		
	});
	
	$("body").on('click','#nextButton2',function(){
		
		$("#page3").show();
		$("#page1").hide();
		$("#page2").hide();
		$("#page4").hide();
		$("#page5").hide();
		$("#page6").hide();
		$("#page7").hide();
	});
	
	$("body").on('click','#nextButton3',function(){
		
		$("#page4").show();
		$("#page1").hide();
		$("#page2").hide();
		$("#page3").hide();
		$("#page5").hide();
		$("#page6").hide();
		$("#page7").hide();
	});
	
	$("body").on('click','#nextButton4',function(){
		
		$("#page5").show();
		$("#page1").hide();
		$("#page2").hide();
		$("#page3").hide();
		$("#page4").hide();
		$("#page6").hide();
		$("#page7").hide();
	});
	
	$("body").on('click','#nextButton5',function(){
		
		$("#page6").show();
		$("#page1").hide();
		$("#page2").hide();
		$("#page3").hide();
		$("#page4").hide();
		$("#page5").hide();
		$("#page7").hide();
	});
	
	$("body").on('click','#nextButton6',function(){
		
		$("#page7").show();
		$("#page1").hide();
		$("#page2").hide();
		$("#page3").hide();
		$("#page4").hide();
		$("#page5").hide();
		$("#page6").hide();
	});
	
	$("body").on('click','#PreviousButton1',function(){
		$("#page2").hide();
		$("#page3").hide();
		$("#page4").hide();
		$("#page1").show();
		$("#page5").hide();
		$("#page6").hide();
		$("#page7").hide();
	});
	
	$("body").on('click','#PreviousButton2',function(){
		$("#page3").hide();
		$("#page2").show();
		$("#page1").hide();
		$("#page4").hide();
		$("#page5").hide();
		$("#page6").hide();
		$("#page7").hide();
	});
	
	$("body").on('click','#PreviousButton3',function(){
		$("#page2").hide();
		$("#page3").show();
		$("#page4").hide();
		$("#page1").hide();
		$("#page5").hide();
		$("#page6").hide();
		$("#page7").hide();
	});
	
	$("body").on('click','#PreviousButton4',function(){
		$("#page2").hide();
		$("#page4").show();
		$("#page3").hide();
		$("#page1").hide();
		$("#page5").hide();
		$("#page6").hide();
		$("#page7").hide();
	});
	
	$("body").on('click','#PreviousButton5',function(){
		$("#page2").hide();
		$("#page5").show();
		$("#page3").hide();
		$("#page1").hide();
		$("#page4").hide();
		$("#page6").hide();
		$("#page7").hide();
	});
	
	$("body").on('click','#PreviousButton6',function(){
		$("#page2").hide();
		$("#page6").show();
		$("#page3").hide();
		$("#page1").hide();
		$("#page4").hide();
		$("#page5").hide();
		$("#page7").hide();
	});
	
});

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
        
        if(ext === "docx" || ext === "pptx") {
     	   alert("Unsupported file format!");
        }else {
			if (/^(tiff|pdf|ppt|pptx|pps|doc|docx|txt|xls|xlsx)$/.test(ext)) {
				$(this).after(function () {
					var id = $(this).attr('id');
					var gdvId = (typeof id !== 'undefined' && id !== false) ? id + '-gdocsviewer' : '';
					return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 742px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 742px; border: none;margin : 0 auto; display : block;"></iframe></div>';
				})
			}
        }
	});
};

$(function () {
    $('a.embed1').gdocsViewer();
    //$('#embedURL').gdocsViewer();
});


 /* ====start parvez on 03-07-2021===== */ 
function closeForm1() {
	window.location = "MyHome.action?toAction=MyHome";
}
 /* ====end parvez on 03-07-2021===== */

</script>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%   
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	UtilityFunctions uF = new UtilityFunctions();
	/* List alSkills = (List) request.getAttribute("alSkills"); */
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    String strUITheme = CF.getStrUI_Theme();
  
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
    if (hmEmpProfile == null) {
    	hmEmpProfile = new HashMap<String, String>();
    }

    Map<String, String> hmEmpProfileHr = (Map<String, String>) request.getAttribute("hmEmpProfileHr");
    Map<String, String> hmEmpProfileHod = (Map<String, String>) request.getAttribute("hmEmpProfileHod");
    Map<String, String> hmEmpProfileMngr = (Map<String, String>) request.getAttribute("hmEmpProfileMngr");

    String orgFullName=(String)request.getAttribute("orgFullName");
    String orgDescription=(String)request.getAttribute("orgDescription");
    String orgWebsite=(String)request.getAttribute("orgWebsite");
   

	String strTitle = (String)request.getAttribute("TITLE1");
	String strBody = (String)request.getAttribute("BODY");
	String strDate = (String)request.getAttribute("DATE");
	String strE = (String)request.getParameter("E");
	String pageFrom = (String)request.getParameter("pageFrom");
	
	List<String> availableExt = (List<String>)request.getAttribute("availableExt");
	String manualDocPath = (String)request.getAttribute("manualDocPath");
	String extention = (String)request.getAttribute("extention");
	
	if(availableExt == null) availableExt = new ArrayList<String>(); 
	boolean flag = true;
	if(!availableExt.contains(extention)) {
		flag = false;
	}
    
   		String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
		String USERTYPEID = (String) session.getAttribute(IConstants.USERTYPEID);
	    String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
	    
	    Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
		Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	    String strCurr = (String) request.getAttribute("strCurr");
	    List<List<String>> alKRADetails = (List<List<String>>) request.getAttribute("alKRADetails");
 
 %>
<section class="content">
    <div class="row jscroll">
    <section class="col-lg-12">
    <div class="box box-body box-primary">
    
    <!-- ====start parvez on 03-07-2021===== -->
    	<div class="box-tools pull-right" id="closeForm" style="padding-left: 10px;" >
			<a href="javascript:void(0);" onclick="closeForm1()" class="close-font" style="margin-right: 20px;"> </a>
		</div>
<!-- ====end parvez on 03-07-2021===== -->
       <s:form theme="simple" name="frm_OnBoardProcessing" id="frm_OnBoardProcessing" action="OnBoardProcessing" method="post">
    
    <div id="page1">
    
  <!-- style="height:30px; line-height:35px; padding-left:15px; padding-right:15px" -->
    <div style="text-align: right; padding-right: 15px;"><button type="button" id="nextButton1" class="btn btn-submit" data-dismiss="modal" >Next</button></div>
    <div class="box-body">
        <section class="col-lg-4 connectedSortable">
        
        
			<!-- <div class="box box-widget widget-user widget-user1" > -->
			<div class="box-widget widget-user widget-user1" >
                <!-- Add the bg color to the header using any of the bg-* classes -->
           <!-- ====start parvez on 27-10-2022===== -->     
                <!-- <div class="widget-user-header bg-aqua-active"> -->
                
                    <%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'> --%>
                    <%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
						List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
						//System.out.println("alPhotoInner=="+alPhotoInner+"--size=="+alPhotoInner.size());
					%>
						<div class="widget-user-header bg-aqua-active" style="height: 140px !important">
						<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>' style="height:auto;">
					<% } else{ %>
						<div class="widget-user-header bg-aqua-active">
						<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
					<% } %>
              <!-- ====end parvez on 27-10-2022===== -->      
                    
                    <h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: <%=uF.parseToInt(strUITheme)==1 ? "-135px;" : "0px;" %>;"><span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span>
                        <span style="float: right;"><a href="MyProfile.action" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
                    </h3>
                    <h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
                </div>
                <div class="widget-user-image">
                    <%if(docRetriveLocation==null) { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
                    <%} else { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
                    <%} %>
                </div>
                <div class="box-footer">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="description-block">
                                <h5 class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> </h5>
                                [<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>]
                                <span class="description-text"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%> </span> [<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>]
                                <p class="description-text"><%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%> </p>
                                <p class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></p>
                                <%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
									<span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong> </span>
                                <% } else { %>
									You don't have a reporting manager.
                                <% } %>
                            </div>
                            <!-- /.description-block -->
                        </div>
                    </div>
                    <!-- /.row -->
                </div>
            </div>
         
       </section>    
       
       <!--**********about Company************* -->
       
        <section class="col-lg-4 connectedSortable">
        
        <!-- ====start parvez on 27-10-2021===== -->
			<!-- <div class="box box-info"> -->
			<div class="box-info">
		<!-- ====end parvez on 27-10-2021===== -->
				 <div class="box-header with-border">
					 <h3 class="box-title">My Organization</h3>
				 </div>
			 <div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;"><!-- Start Dattatray  -->
				<table class="table table-hover table_no_border">
					<tr> 
						<th style="width:140px">Name of the Organization: </td>
						<td class="textblue" valign="bottom"><%=orgFullName %></td>
					</tr>
					
					<tr>
						<th style="width:140px">Organization Description: </td>
						<%-- <td class="textblue" valign="bottom"><%=orgDescription %></td> --%>
						<td class="textblue" valign="bottom"><%=uF.showData(orgDescription,"-") %></td> <!-- Start Dattatray -->
					</tr>
					
					<tr>
						<th style="width:140px">Website: </td>
						<td class="textblue" valign="bottom"><a href="<%=uF.showData(orgWebsite,"-")%>"><%=uF.showData(orgWebsite,"-")%></a> </td>
					</tr>
				</table>
			</div>
		 </div>
       </section> 
       
       <section class="col-lg-4 connectedSortable">
       
    <!-- ====start parvez on 27-10-2021===== -->
      	 <!-- <div class="box box-info"> -->
      	 <div class="box-info">
    <!-- ====end parvez on 27-10-2021===== -->
				 <div class="box-header with-border">
					 <h3 class="box-title">My Current Job</h3>
				 </div>
			 <div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;"> <!-- Start Dattatray  -->
                   <table class="table table_no_border autoWidth">
                        <tr>
                            <td class="alignRight">Employee Type:</td>
                            <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_TYPE"), "-")%></td>
                        </tr>
                        <tr>
                            <td class="alignRight">Level:</td>
                            <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("LEVEL_NAME"), "-")%></td>
                        </tr>
                        <tr>
                            <td class="alignRight">Designation:</td>
                            <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-")%></td>
                        </tr>
                        <tr>
                            <td class="alignRight">Grade:</td>
                            <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("GRADE_NAME"), "-")%></td>
                        </tr>
                        <tr>
                            <td class="alignRight">SBU:</td>
                            <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("SBU_NAME"), "-")%></td>
                        </tr>
                        <tr>
                            <td class="alignRight">Department: </td>
                            <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("DEPARTMENT_NAME"), "-")%></td>
                        </tr>
                        <tr>
                            <td class="alignRight">Location: </td>
                            <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("WLOCATION_NAME"), "-")%></td>
                        </tr>
                        <tr>
                            <td class="alignRight">Organization: </td>
                            <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ORG_NAME"), "-")%></td>
                        </tr>
                    </table>
				</div>
		 	</div>
       </section>
   <!-- ====start parvez on 27-10-2021===== -->
       </div>
       <!-- <div style="text-align: right; padding-right: 15px;"><button type="button" id="nextButton1" class="btn btn-submit" data-dismiss="modal">Next</button></div> -->
   <!-- ====end parvez on 27-10-2021===== -->
     
      </div> 
    
   	<div id="page2" hidden>
   	
   	<!-- ====start parvez on 27-10-2021===== -->	
   		<div style="text-align: right; padding-right: 15px;">
      	 <!-- Start Dattatray Note : Next and Previous button sequence changed -->
       		<button type="button" id="PreviousButton1" class="btn btn-submit" data-dismiss="modal">Previous</button>
			<button type="button" id="nextButton2" class="btn btn-submit" data-dismiss="modal">Next</button>
			<!-- End Dattatray -->
		</div>
   		<div class="box-body">
   	<!-- ====end parvez on 27-10-2021===== -->
   	
	       <section class="col-lg-12 connectedSortable">
	       
	 <!-- ====start parvez on 27-10-2021===== -->
	      	 <!-- <div class="box box-info"> -->
	      	 <div class="box-info">
	<!-- ====end parvez on 27-10-2021===== -->
				<div class="box-header with-border">
					<h3 class="box-title">My Position</h3>
				 </div>
				 <div class="box-body" style="padding: 0px; overflow-y: auto;">
	            	<div class="holder" style="overflow-x: auto;">
						<s:action name="OrganisationalChart" executeResult="true">
	               			<s:param name="strEmpId"><%=(String)hmEmpProfile.get("EMP_ID")%></s:param>
	               			<s:param name="orgId"><%=(String)hmEmpProfile.get("ORG_ID")%></s:param>
	               			<s:param name="fromPage">MP</s:param>
	               		</s:action>
	               		<ul id="org" style="display:none">	
	               			<%=request.getAttribute("sbPosition")%>
						</ul>
					<div id="chart" class="orgChart" style="float:left;width:99%;text-align: center;"></div>
	                   </div>
	               
	                </div>
	             </div>   
	       </section> 
	         
	      
	      <%--  <section class="col-lg-12 connectedSortable">
	        <div class="box box-danger">
		                <div class="box-header with-border">
		                	<h3 class="box-title">Position</h3>
		                  <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                  </div>
		                </div><!-- /.box-header -->
		                
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
		                	<div class="rosterweek"> 
								<div class="content1">
									
			                    </div>
							</div>
		                </div><!-- /.box-body -->
		              </div>
		           </section>
		       
	        --%>
	        
	 <!-- ====start parvez on 27-10-2021===== -->
	       <!-- <div style="text-align: right; padding-right: 15px;">
	      	 Start Dattatray Note : Next and Previous button sequence changed
	       		<button type="button" id="PreviousButton1" class="btn btn-submit" data-dismiss="modal">Previous</button>
				<button type="button" id="nextButton2" class="btn btn-submit" data-dismiss="modal">Next</button>
				End Dattatray
			</div> -->
		</div>
	<!-- ====end parvez on 27-10-2021===== -->
     </div>
     
    <div id="page3" hidden>
    
    	<div style="text-align: right; padding-right: 15px;">
       		<!-- Start Dattatray Note : Next and Previous button sequence changed -->
       		<button type="button" id="PreviousButton2" class="btn btn-submit" data-dismiss="modal">Previous</button>
        	<button type="button" id="nextButton3" class="btn btn-submit" data-dismiss="modal">Next</button>
        	<!-- End Dattatray -->
		</div>
		<div class="box-body">
	
	<!-- ====start parvez on 30-07-2022===== -->
		<section class="col-lg-4 connectedSortable">
        
      	 <!-- <div class="box box-info"> -->
      	 <div class="box-info">
				 <div class="box-header with-border">
					 <h3 class="box-title">My HOD Profile</h3> <!-- Start Dattatray  -->
				 </div>
			 <div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;"><!-- Start Dattatray Note: height changed  -->
            	<%if(hmEmpProfileHod!=null && !hmEmpProfileHod.equals("")){ %>
            	   	
            	<!-- <div class="box box-widget widget-user widget-user1"> -->
            	<div class="box-widget widget-user widget-user1">
                <!-- Add the bg color to the header using any of the bg-* classes -->
                <!-- <div class="widget-user-header bg-aqua-active"> -->
                    <%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileHod.get("EMP_ID")+"/"+hmEmpProfileHod.get("COVER_IMAGE")%>'> --%>
                    <%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
						List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
					%>
						<div class="widget-user-header bg-aqua-active" style="height: 130px !important">
						<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileHr.get("EMP_ID")+"/"+hmEmpProfileHr.get("COVER_IMAGE")%>' style="height: auto !important">
					<% } else{ %>
						<div class="widget-user-header bg-aqua-active">
						<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileHod.get("EMP_ID")+"/"+hmEmpProfileHod.get("COVER_IMAGE")%>'>
					<% } %>
					
                    <h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: <%=uF.parseToInt(strUITheme)==1 ? "-135px;" : "0px;" %>;"><span><%=uF.showData((String) hmEmpProfileHod.get("NAME"), "-")%></span>
                        <span style="float: right;"><a href="MyProfile.action" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
                    </h3>
                    <h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfileHod.get("EMPCODE"), "-")%></h5>
                </div>
                <div class="widget-user-image">
                    <%if(docRetriveLocation==null) { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfileHod.get("IMAGE")%>">
                    <%} else { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfileHod.get("IMAGE")%>">
                    <%} %>
                </div>
                <div class="box-footer">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="description-block">
                                <h5 class="description-header"><%=uF.showData((String) hmEmpProfileHod.get("DESIGNATION_NAME"), "-")%> </h5>
                                [<%=uF.showData((String) hmEmpProfileHod.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfileHod.get("GRADE_NAME"), "-")%>]
                                <span class="description-text"><%=uF.showData((String) hmEmpProfileHod.get("DEPARTMENT_NAME"), "-")%> </span> [<%=uF.showData((String) hmEmpProfileHod.get("SBU_NAME"), "-")%>]
                                <p class="description-text"><%=uF.showData((String) hmEmpProfileHod.get("WLOCATION_NAME"), "-")%> </p>
                                <p class="description-text"><%=uF.showData((String) hmEmpProfileHod.get("ORG_NAME"), "-")%></p>
                                <%if(((String) hmEmpProfileHod.get("SUPERVISOR_NAME"))!=null) { %>
									<span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfileHod.get("SUPERVISOR_NAME"), "-")%></strong> </span>
                                <% } else { %>
									You don't have a reporting manager.
                                <% } %>
                            </div>
                            <!-- /.description-block -->
                        </div>
                    </div>
                    <!-- /.row -->
                </div>
            </div>
         
            <%}else{ %>	
            <div class="nodata msg">
            	You don't have a reporting HOD.
            </div>
            <%} %>	
             </div>   
      	 </div>
       </section>
 <!-- ====end parvez on 30-07-2022===== -->      
       
       <section class="col-lg-4 connectedSortable">
      	 <!-- <div class="box box-info"> -->
      	 <div class="box-info">
				 <div class="box-header with-border">
					 <h3 class="box-title">My HR Profile</h3><!-- Start Dattatray  -->
				 </div>
			 <div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;"><!-- Start Dattatray note: height changed-->
            	
            	<%if(hmEmpProfileHr!=null && !hmEmpProfileHr.equals("")){ %>
            	
            	<!-- <div class="box box-widget widget-user widget-user1"> -->
            	<div class="box-widget widget-user widget-user1">
                <!-- Add the bg color to the header using any of the bg-* classes -->
                <!-- <div class="widget-user-header bg-aqua-active"> -->
             <!-- ====start parvez on 27-10-2022===== -->       
                    <%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileHr.get("EMP_ID")+"/"+hmEmpProfileHr.get("COVER_IMAGE")%>'> --%>
                    <%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
						List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
					%>
						<div class="widget-user-header bg-aqua-active" style="height: 130px !important">
						<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileHr.get("EMP_ID")+"/"+hmEmpProfileHr.get("COVER_IMAGE")%>' style="height: auto !important">
					<% } else{ %>
						<div class="widget-user-header bg-aqua-active">
						<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileHr.get("EMP_ID")+"/"+hmEmpProfileHr.get("COVER_IMAGE")%>'>
					<% } %>
            <!-- ====end parvez on 27-10-2022===== -->        
                    <h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: <%=uF.parseToInt(strUITheme)==1 ? "-135px;" : "0px;" %>;"><span><%=uF.showData((String) hmEmpProfileHr.get("NAME"), "-")%></span>
                        <span style="float: right;"><a href="MyProfile.action" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
                    </h3>
                    <h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfileHr.get("EMPCODE"), "-")%></h5>
                </div>
                <div class="widget-user-image">
                    <%if(docRetriveLocation==null) { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfileHr.get("IMAGE")%>">
                    <%} else { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfileHr.get("IMAGE")%>">
                    <%} %>
                </div>
                <div class="box-footer">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="description-block">
                                <h5 class="description-header"><%=uF.showData((String) hmEmpProfileHr.get("DESIGNATION_NAME"), "-")%> </h5>
                                [<%=uF.showData((String) hmEmpProfileHr.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfileHr.get("GRADE_NAME"), "-")%>]
                                <span class="description-text"><%=uF.showData((String) hmEmpProfileHr.get("DEPARTMENT_NAME"), "-")%> </span> [<%=uF.showData((String) hmEmpProfileHr.get("SBU_NAME"), "-")%>]
                                <p class="description-text"><%=uF.showData((String) hmEmpProfileHr.get("WLOCATION_NAME"), "-")%> </p>
                                <p class="description-text"><%=uF.showData((String) hmEmpProfileHr.get("ORG_NAME"), "-")%></p>
                                <%if(((String) hmEmpProfileHr.get("SUPERVISOR_NAME"))!=null) { %>
									<span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfileHr.get("SUPERVISOR_NAME"), "-")%></strong> </span>
                                <% } else { %>
									You don't have a reporting manager.
                                <% } %>
                            </div>
                            <!-- /.description-block -->
                        </div>
                    </div>
                    <!-- /.row -->
                </div>
            </div>
         
            <%}else{ %>	
            <div class="nodata msg"> You don't have a reporting HR. </div>
            <%} %>	
             </div>   
      	 </div>
       </section>
       
        <%-- <section class="col-lg-4 connectedSortable">
        
      	 <!-- <div class="box box-info"> -->
      	 <div class="box-info">
				 <div class="box-header with-border">
					 <h3 class="box-title">My HOD Profile</h3> <!-- Start Dattatray  -->
				 </div>
			 <div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;"><!-- Start Dattatray Note: height changed  -->
            	<%if(hmEmpProfileHod!=null && !hmEmpProfileHod.equals("")){ %>
            	  	
            	<!-- <div class="box box-widget widget-user widget-user1"> -->
            	<div class="box-widget widget-user widget-user1">
                <!-- Add the bg color to the header using any of the bg-* classes -->
                <div class="widget-user-header bg-aqua-active">
                    <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileHod.get("EMP_ID")+"/"+hmEmpProfileHod.get("COVER_IMAGE")%>'>
                    <h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: <%=uF.parseToInt(strUITheme)==1 ? "-135px;" : "0px;" %>;"><span><%=uF.showData((String) hmEmpProfileHod.get("NAME"), "-")%></span>
                        <span style="float: right;"><a href="MyProfile.action" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
                    </h3>
                    <h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfileHod.get("EMPCODE"), "-")%></h5>
                </div>
                <div class="widget-user-image">
                    <%if(docRetriveLocation==null) { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfileHod.get("IMAGE")%>">
                    <%} else { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfileHod.get("IMAGE")%>">
                    <%} %>
                </div>
                <div class="box-footer">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="description-block">
                                <h5 class="description-header"><%=uF.showData((String) hmEmpProfileHod.get("DESIGNATION_NAME"), "-")%> </h5>
                                [<%=uF.showData((String) hmEmpProfileHod.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfileHod.get("GRADE_NAME"), "-")%>]
                                <span class="description-text"><%=uF.showData((String) hmEmpProfileHod.get("DEPARTMENT_NAME"), "-")%> </span> [<%=uF.showData((String) hmEmpProfileHod.get("SBU_NAME"), "-")%>]
                                <p class="description-text"><%=uF.showData((String) hmEmpProfileHod.get("WLOCATION_NAME"), "-")%> </p>
                                <p class="description-text"><%=uF.showData((String) hmEmpProfileHod.get("ORG_NAME"), "-")%></p>
                                <%if(((String) hmEmpProfileHod.get("SUPERVISOR_NAME"))!=null) { %>
									<span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfileHod.get("SUPERVISOR_NAME"), "-")%></strong> </span>
                                <% } else { %>
									You don't have a reporting manager.
                                <% } %>
                            </div>
                            <!-- /.description-block -->
                        </div>
                    </div>
                    <!-- /.row -->
                </div>
            </div>
         
            <%}else{ %>	
            <div class="nodata msg">
            	You don't have a reporting HOD.
            </div>
            <%} %>	
             </div>   
      	 </div>
       </section> --%>
       
       <section class="col-lg-4 connectedSortable">
      	 <!-- <div class="box box-info"> -->
      	 <div class="box-info">
				 <div class="box-header with-border">
					 <h3 class="box-title">My Manager Profile</h3><!-- Start Dattatray  -->
				 </div>
			 <div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;"><!-- Start Dattatray Note : Height changed -->
            	<%if(hmEmpProfileMngr!=null && !hmEmpProfileMngr.equals("")){ %>
            	 	
            	<!-- <div class="box box-widget widget-user widget-user1"> -->
            	<div class="box-widget widget-user widget-user1">
                <!-- Add the bg color to the header using any of the bg-* classes -->
                <!-- <div class="widget-user-header bg-aqua-active"> -->
             <!-- ====start parvez on 27-10-2022===== -->       
                    <%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileMngr.get("EMP_ID")+"/"+hmEmpProfileMngr.get("COVER_IMAGE")%>'> --%>
                    <%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
						List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
						//System.out.println("alPhotoInner=="+alPhotoInner+"--size=="+alPhotoInner.size());
					%>
						<div class="widget-user-header bg-aqua-active" style="height: 130px !important;">
						<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileMngr.get("EMP_ID")+"/"+hmEmpProfileMngr.get("COVER_IMAGE")%>' style="height: auto !important;">
					<% } else{ %>
						<div class="widget-user-header bg-aqua-active">
						<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfileMngr.get("EMP_ID")+"/"+hmEmpProfileMngr.get("COVER_IMAGE")%>'>
					<% } %>
             <!-- ====end parvez on 27-10-2022===== -->       
                    <h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: <%=uF.parseToInt(strUITheme)==1 ? "-135px;" : "0px;" %>;"><span><%=uF.showData((String) hmEmpProfileMngr.get("NAME"), "-")%></span>
                        <span style="float: right;"><a href="MyProfile.action" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
                    </h3>
                    <h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfileMngr.get("EMPCODE"), "-")%></h5>
                </div>
                <div class="widget-user-image">
                    <%if(docRetriveLocation==null) { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfileMngr.get("IMAGE")%>">
                    <%} else { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfileMngr.get("IMAGE")%>">
                    <%} %>
                </div>
                <div class="box-footer">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="description-block">
                                <h5 class="description-header"><%=uF.showData((String) hmEmpProfileMngr.get("DESIGNATION_NAME"), "-")%> </h5>
                                [<%=uF.showData((String) hmEmpProfileMngr.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfileMngr.get("GRADE_NAME"), "-")%>]
                                <span class="description-text"><%=uF.showData((String) hmEmpProfileMngr.get("DEPARTMENT_NAME"), "-")%> </span> [<%=uF.showData((String) hmEmpProfileMngr.get("SBU_NAME"), "-")%>]
                                <p class="description-text"><%=uF.showData((String) hmEmpProfileMngr.get("WLOCATION_NAME"), "-")%> </p>
                                <p class="description-text"><%=uF.showData((String) hmEmpProfileMngr.get("ORG_NAME"), "-")%></p>
                                <%if(((String) hmEmpProfileMngr.get("SUPERVISOR_NAME"))!=null) { %>
									<span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfileMngr.get("SUPERVISOR_NAME"), "-")%></strong> </span>
                                <% } else { %>
									You don't have a reporting manager.
                                <% } %>
                            </div>
                            <!-- /.description-block -->
                        </div>
                    </div>
                    <!-- /.row -->
                </div>
            </div>
         
            <%}else{ %>	
            <div class="nodata msg">
            	You don't have a reporting manager.
            </div>
            <%} %>	
             </div>   
      	 </div>
       </section>
   
       	<!-- <div style="text-align: right; padding-right: 15px;">
       		Start Dattatray Note : Next and Previous button sequence changed
       		<button type="button" id="PreviousButton2" class="btn btn-submit" data-dismiss="modal">Previous</button>
        	<button type="button" id="nextButton3" class="btn btn-submit" data-dismiss="modal">Next</button>
        	End Dattatray
		</div> -->
		
		</div>
     </div>
     
    <div id="page4" hidden>
    
    	<div style="text-align: right; padding-right: 15px;">
		<!-- Start Dattatray Note : Next and Previous button sequence changed -->
			<button type="button" id="PreviousButton3" class="btn btn-submit" data-dismiss="modal">Previous</button>
        	<button type="button" id="nextButton4" class="btn btn-submit" data-dismiss="modal">Next</button>
        <!-- End Dattatray -->	
     	</div>
     	<div class="box-body">
       <section class="col-lg-12 connectedSortable">
      	 <!-- <div class="box box-info"> -->
      	 <div class="box-info">
				 <div class="box-header with-border">
					 <h3 class="box-title">My Company Manual</h3> <!-- Start Dattatray  -->
				 </div>
			 <div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;"><!-- Start Dattatray Note : Height changed -->
            	 <%if(strTitle!=null && !strTitle.equals("") ){ %>
            	 <div class="manual_box">
            	 
            	
                     <div class="addgoaltoreview">
                        <div class="manual_title"><%=uF.showData(strTitle,"-") %></div>
                        <div class="clr"></div>
                        <div style="float:right; padding:0px 20px"><span style="font-style:italic; color:#666666">Last Updated on:</span> <%=strDate %></div>
                     </div>
                
                       <div class="clr"></div>
                       
                       <div class="manual_body">
                            <%if(strBody != null && !strBody.equals("") && (manualDocPath == null || manualDocPath.equals(""))) {%>
       						<%=strBody%>
           		 <%} else if(manualDocPath != null && !manualDocPath.equals("")) {
	        		if(flag) {%>
    	
						<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;">
							<a href="<%=manualDocPath %>" class="embed1" id="test">&nbsp;</a>
						</div>
			     <% } else {%>
						<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;height: 500px; background-color: #CCCCCC;">
							<div style="text-align: center; font-size: 24px; padding: 150px;">Preview not available</div>
						</div>
			 	  <% }%>
				 <% } %>
                       </div>
                       <div class="clr"></div>
                   </div>
                   <%}else{ %>
                   <div class="nodata msg">No manual available.</div>
                   <%} %>
             </div>   
      	 </div>
       </section>
       
		<!-- <div style="text-align: right; padding-right: 15px;">
		Start Dattatray Note : Next and Previous button sequence changed
			<button type="button" id="PreviousButton3" class="btn btn-submit" data-dismiss="modal">Previous</button>
        	<button type="button" id="nextButton4" class="btn btn-submit" data-dismiss="modal">Next</button>
        End Dattatray	
     	</div> -->
      </div>
     </div>
     
    <div id="page5" hidden>
    	
    	<div style="text-align: right; padding-right: 15px;">
			<button type="button" id="PreviousButton4" class="btn btn-submit" data-dismiss="modal">Previous</button>
       		<button type="button" id="nextButton5" class="btn btn-submit" data-dismiss="modal">Next</button>
        </div>
    	
    	<div class="box-body">
       <section class="col-lg-6 connectedSortable">
      	 <!-- <div class="box box-info"> -->
      	 <div class="box-info">
				 <div class="box-header with-border">
					 <h3 class="box-title">My KRA</h3><!-- Start Dattatray  -->
				 </div>
			 <div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;"><!-- Start Dattatray Note : Height changed -->
            	
            	<%String effectiveDate = "";
                   if(alKRADetails != null && !alKRADetails.isEmpty()) {
                            effectiveDate = alKRADetails.get(alKRADetails.size()-1).get(0);
                    }%>
                      <div>
                    	<%=((effectiveDate != null && !effectiveDate.equals("")) ? "Since: " + effectiveDate : "<div class=\"nodata msg\" style=\"width:95%\">No KRAs defined yet.</span></div>")%>
                          <table class="table table_no_border autoWidth">
                           <%for(int i=0; alKRADetails != null && !alKRADetails.isEmpty() && i<alKRADetails.size(); i++) {
                               List<String> innerList = alKRADetails.get(i);%>
                               <tr>
                                  <td class="kra"><%=innerList.get(1)%></td>
                                </tr>
                            <% } %>
                          </table>
                     </div>
             	</div>   
      		 </div>
      	</section> 
      	
      	  
      	<section class="col-lg-6 connectedSortable">
      	
      	  <!-- <div class="box box-info"> -->
      	  <div class="box-info">
			 <div class="box-header with-border">
				 <h3 class="box-title">My Live Learnings</h3><!-- Start Dattatray -->
			  </div>
			 <div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;"><!-- Start Dattatray Note : Height changed -->
            	<%List<List<String>> alLiveLearnings=(List<List<String>>)request.getAttribute("alLiveLearnings"); 
            	if(alLiveLearnings!=null && !alLiveLearnings.equals("") && alLiveLearnings.size()>0){ %>
            	<table  class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin:10px 0px 25px 0px;">
                    <tbody>
                       <tr class="darktable">
	                       	<th style="text-align: center; width: 190px;">Plan Name</th>
	                       	<th style="text-align: center; width: 60px;">Start Date</th>
	                        <th style="text-align: center; width: 60px;">End Date</th>
                        </tr>
                       
                       <%for(int i=0;i<alLiveLearnings.size();i++){
                        List<String> innerList=alLiveLearnings.get(i);%>
                        <tr>
                 			 <td class="textblue" valign="bottom"><%=innerList.get(0) %></td>
                 			 <td valign="bottom"><%=innerList.get(1) %></td>
                 			 <td valign="bottom"><%=innerList.get(2) %></td>
                  		</tr>
                       <% }%>
                   </tbody>
               </table>
            	<%}else{%>
            	<div class="nodata msg">
                    No learnings assigned.
                 </div>
                <%} %>
             </div>   
      	 </div>
       </section>
       
   <!-- ====start parvez on 27-10-2021===== -->
		<!-- <div style="text-align: right; padding-right: 15px;">
			<button type="button" id="PreviousButton4" class="btn btn-submit" data-dismiss="modal">Previous</button>
       		<button type="button" id="nextButton5" class="btn btn-submit" data-dismiss="modal">Next</button>
        </div> -->
        </div>	
  <!-- ====end parvez on 27-10-2021===== -->
     </div>
     
    <div id="page6" hidden>
    
    <!-- ====start parvez on 27-10-2021===== -->
    	<div style="text-align: right; padding-right: 15px;">
			<!-- Start Dattatray Note : Next and Previous button sequence changed -->
			<button type="button" id="PreviousButton5" class="btn btn-submit" data-dismiss="modal">Previous</button>
        	<button type="button" id="nextButton6" class="btn btn-submit" data-dismiss="modal">Next</button>
        	<!-- End Dattatray -->
		</div>
		<div class="box-body">
       <section class="col-lg-6 connectedSortable">
      	 <!-- <div class="box box-info"> -->
      	 <div class="box-info">
   <!-- ====end parvez on 27-10-2021===== -->
				 <div class="box-header with-border"><!-- Start Dattatray  Note: Remove style  -->
					 <h3 class="box-title">My Leave Status</h3><!-- Start Dattatray -->
				 </div>
			 <div class="box-body" style="padding: 10px; max-height: 180px; overflow-y: auto;"><!-- start Dattatray Note : change height and padding  -->
          	  <% java.util.List couterlist = (java.util.List)request.getAttribute("leaveList"); 
           			 if(couterlist!=null && !couterlist.equals("") && couterlist.size()>0) {%>
            		<table class="table table-bordered" id="lt">
						<thead>
							<tr>
								<th>Leave Type</th>
								<th>Remaining Leaves<br>(in days)</th>
							</tr>
						</thead>
						<tbody>
							<% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
							<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
							<tr>
								<td><%= cinnerlist.get(0) %></td>
								<td class="alignRight" style="padding-right: 20px"><%= cinnerlist.get(1) %></td>
							</tr>
							<% } %>
						</tbody>
					</table>
					<% }else { %>
						<div class="nodata msg"> No leave data available. </div>
					<% } %>
             </div>   
      	 </div>
      </section>
      
       <section class="col-lg-6 connectedSortable">
      
      <!-- ====start parvez on 27-10-2021===== -->	 
      	  <!-- <div class="box box-info"> -->
      	  <div class="box-info">
     <!-- ====end parvez on 27-10-2021===== -->
				 <div class="box-header with-border"><!-- Start Dattatray  Note: Remove style  -->
					 <h3 class="box-title">My Roster</h3><!-- Start Dattatray-->
				 </div>
			 <div class="box-body" style="padding: 15px; max-height: 180px; overflow-y: auto;"><!-- Start Dattatray  Note: changed height -->
           		
           		<%Map<String, String> hmRoster=(Map<String, String>)request.getAttribute("hmRoster");
           			List<String> alDates=(List<String>)request.getAttribute("alDates");
           			if(hmRoster!=null && !hmRoster.equals("") && hmRoster.size()>0) { %>
           			<table class="table table_no_border" >
						<thead>
							<tr>
								<th>Date</th>
								<th>Start Time</th>
								<th>End Time</th>
							</tr>
						</thead>
						<tbody>
           				<%	for(int i=0;i<alDates.size();i++){
           					
           					String starttime = (String) hmRoster.get((String) alDates.get(i) + "FROM");
           					String endtime = (String) hmRoster.get((String) alDates.get(i) + "TO");
           				%>
           				<tr>
							<td><%= alDates.get(i) %></td>
							<!-- Start Dattatray -->
							<td style="padding-right: 20px"><%= uF.showData ((String) hmRoster.get((String) alDates.get(i) + "FROM"),"-") %></td>
							<td style="padding-right: 20px"><%= uF.showData((String) hmRoster.get((String) alDates.get(i) + "TO"),"-") %></td>
							<!-- End Dattatray -->
						</tr>
           				<%}%>
           			</tbody>
           			</table>
           			<% } else { 
	               		String startTime = (String)request.getAttribute("startTime");
	           			String endTime = (String)request.getAttribute("endTime");
           			%>
           			<table class="table table_no_border">
           				<thead>
           					<tr>
           						<th>Start Time</th>
								<th>End Time</th>
							</tr>
						<thead>
						<tbody>
							<tr>
								<td><%=uF.showData (startTime, "-") %></td>
								<td><%=uF.showData (endTime, "-") %></td>
							</tr>
						</tbody>
           		  </table>
           		<%} %>
             </div>   
      	 </div>
      </section>
      	 
      <section class="col-lg-12 connectedSortable">
    
    <!-- ====start parvez on 27-10-2021===== -->
      	 <!-- <div class="box box-info"> -->
      	 <div class="box-info">
    <!-- ====end parvez on 27-10-2021===== -->
				 <div class="box-header with-border"><!-- Start Dattatray  Note: Remove style  -->
					 <h3 class="box-title" style="padding-top: 20px;">My Salary Summary</h3><!-- Start Dattatray -->
				 </div>
			     <%
                        List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
                        Map<String, Double> hmSalaryTotal = (Map<String, Double>) request.getAttribute("hmSalaryTotal");
                        
                        Map<String, String> hmGratuityPolicy = (Map<String, String>) request.getAttribute("hmGratuityPolicy");
                        double gratuitySalHeadAmt=0.0d;
                        List<String> alGratuitySlaHeadId = new ArrayList<String>();
                        if(hmGratuityPolicy.get("SALARY_HEAD")!=null) {
                      	  alGratuitySlaHeadId = Arrays.asList(hmGratuityPolicy.get("SALARY_HEAD").split(","));
                        }
              
	              %>	 
				 
			 <div class="box-body clr" style="padding: 10px; min-height: 307px; overflow-y: auto;"><!-- Start Dattatray Note : Height changed -->
            	<table class="table table_no_border autoWidth">
                         <tr>
                             <td>Payout Type:</td><!-- Start Dattatray  -->
                             <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PAYOUT_TYPE"), "-")%></td>
                         </tr>
                   <!-- ===start parvez date: 12-08-2022=== -->      
                         <% if(hmEmpProfile.get("EMP_BANK_NAME")!=null && !hmEmpProfile.get("EMP_BANK_NAME").equals("-1") && !hmEmpProfile.get("EMP_BANK_NAME").equals("")){ %>
                        	
                        	<tr><td class="alignRight">Bank Name:</td> <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_STR_BANK_NAME"), "-")%></td></tr>
                        	<% 
                        	 if (hmEmpProfile.get("EMP_ACT_NO") != null && !hmEmpProfile.get("EMP_ACT_NO").equals("")) { %>
                         	<tr><td class="alignRight">Bank Account No.:</td> <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_ACT_NO"), "-")%></td></tr>
                         <% } %>
                         
                         <% if (hmEmpProfile.get("EMP_IFSC_CODE") != null && !hmEmpProfile.get("EMP_IFSC_CODE").equals("")) { %>
             				<tr><td class="alignRight">Bank IFSC Code:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_IFSC_CODE"), "-")%></td></tr>
            		 	
            		 	<% } %>
                         
                         <% if (hmEmpProfile.get("EMP_BANK_BRANCH") != null && !hmEmpProfile.get("EMP_BANK_BRANCH").equals("")) { %>
             				<tr><td class="alignRight">Bank Branch:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_BANK_BRANCH"), "-")%></td></tr>
            		 	
            		 	<% }}
                         
                        else if(hmEmpProfile.get("EMP_BANK_NAME")!=null && hmEmpProfile.get("EMP_BANK_NAME").equals("-1")){ 
                        	if (hmEmpProfile.get("EMP_OTHER_BANK_NAME") != null && !hmEmpProfile.get("EMP_OTHER_BANK_NAME").equals("")) {%>
           	  				
           	  				<tr><td class="alignRight">Bank Name:</td> <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_OTHER_BANK_NAME"), "-")%></td></tr>
           	  				
           	  				<% if (hmEmpProfile.get("EMP_ACT_NO") != null && !hmEmpProfile.get("EMP_ACT_NO").equals("")) { %>
                          	<tr><td class="alignRight">Bank Account No.:</td> <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_ACT_NO"), "-")%></td></tr>
                           <% if (hmEmpProfile.get("EMP_OTHER_BANK_IFSC_CODE") != null && !hmEmpProfile.get("EMP_OTHER_BANK_IFSC_CODE").equals("")) { %>
             				<tr><td class="alignRight">Bank IFSC Code:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_OTHER_BANK_IFSC_CODE"), "-")%></td></tr>
                          <% if (hmEmpProfile.get("EMP_OTHER_BANK_BRANCH") != null && !hmEmpProfile.get("EMP_OTHER_BANK_BRANCH").equals("")) { %>
             				<tr><td class="alignRight">Bank Branch:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_OTHER_BANK_BRANCH"), "-")%></td></tr>
                          <%}}}}} %>
                 <!-- ===end parvez date: 12-08-2022=== -->          
                  </table>
                  <table  class="table table_no_border autoWidth">
                                  <tr>
                                      <td valign="top">
                                          <table cellspacing="1" cellpadding="2"  class="table table-bordered" style="width: 500px">
                                              <tr>
                                                  <td colspan="3" nowrap="nowrap" align="center">
                                                      <h5>EARNING DETAILS</h5>
                                                  </td>
                                              </tr>
                                              <tr>
                                              <!-- Start Dattatray Note class changed alignRight to alignCenter -->
                                                  <td class="alignCenter">Salary Head</td>
                                                  <td width="30%" class="alignCenter">Monthly</td>
                                                  <td width="30%" class="alignCenter">Annual</td>
                                                  <!-- End Dattatray -->
                                              </tr>
                                              <%
                                                  double grossAmount = 0.0d;
                                                  double grossYearAmount = 0.0d;
              									  double netTakeHome = 0.0d;
                                                  for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
                                                  	List<String> innerList = salaryHeadDetailsList.get(i);
                                                  		if(innerList.get(1).equals("E")) {
                                                  			double dblEarnMonth = uF.parseToDouble(innerList.get(2));
            												double dblEarnAnnual = uF.parseToDouble(innerList.get(3));
            												grossAmount += dblEarnMonth;
            												grossYearAmount += dblEarnAnnual;
            												
            												netTakeHome += dblEarnMonth;
            												if(alGratuitySlaHeadId.contains(innerList.get(4))) {
            													gratuitySalHeadAmt += dblEarnMonth;
            												}
            									%>
            											<tr>
            												<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
            															
            												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnMonth)%></td>
            												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnAnnual)%></td>			
            												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnMonth) %></td>
            												<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnAnnual)%></td> --%>
            											</tr>
                                              	<% } %>
                                              <% } %>
                                              	<tr>
													<td class="alignRight"><strong>Gross Salary</strong></td>
													<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossAmount)%></strong></td>
													<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossYearAmount)%></strong></td>
													
													
												<%--  <td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossAmount))%></strong></td>
													<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossYearAmount))%></strong></td> --%> 
												</tr>
                                          </table>
                                      </td>
                                      <td style="padding:20px !important;"></td>
                                      <td valign="top">
                                              
                                          <table cellspacing="1" cellpadding="2" class="table table-bordered" style="width: 500px"> <!--Start Dattatray Note removed autoWidth from class and added width in style  -->
                                              <tr>
                                                  <td colspan="3" nowrap="nowrap" align="center">
                                                      <h5>DEDUCTION DETAILS</h5>
                                                  </td>
                                              </tr>
                                              <tr>
                                              <!-- Start Dattatray Note class changed alignRight to alignCenter -->
                                                  <td class="alignCenter">Salary Head</td>
                                                  <td width="30%" class="alignCenter">Monthly</td>
                                                  <td width="30%" class="alignCenter">Annual</td>
                                                  <!-- End Dattatray -->
                                              </tr>
                                              <% 
                                                  double deductAmount = 0.0d;
                                                  double deductYearAmount = 0.0d;
                                                  
                                                  for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
                                                  List<String> innerList = salaryHeadDetailsList.get(i);
                                                  	if(innerList.get(1).equals("D")) {
                                                  		double dblDeductMonth = uF.parseToDouble(innerList.get(2));
        												double dblDeductAnnual = uF.parseToDouble(innerList.get(3));
        												deductAmount += dblDeductMonth;
        												deductYearAmount += dblDeductAnnual;
        												
        												netTakeHome -= dblDeductMonth;
        												if(alGratuitySlaHeadId.contains(innerList.get(4))) {
        													gratuitySalHeadAmt += dblDeductMonth;
        												}
        									%>
        											<tr>
        												<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
        												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblDeductMonth) %></td>
        										 		<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblDeductAnnual) %></td> --%>       												
        												
        												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblDeductMonth) %></td>
        										 		<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblDeductAnnual) %></td>
        											</tr>
                                              <% } %>
                                              <% } %>
                                              <tr>
													<td class="alignRight"><strong>Deduction</strong></td>
												<%-- 	<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(deductAmount))%></strong></td>
													<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(deductYearAmount))%></strong></td> --%>
													
													<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(deductAmount)%></strong></td>
													<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(deductYearAmount)%></strong></td>
											</tr>
                                          </table>
                                      </td>
                                  </tr>
                                 <%
						double dblCTCMonthly = grossAmount;
						double dblCTCAnnualy = grossYearAmount;
						Map<String, String> hmReimCTC = (Map<String, String>)request.getAttribute("hmReimCTC");
						if(hmReimCTC == null) hmReimCTC = new HashMap<String, String>();
						
						Map<String, String> hmReimCTCHeadAmount = (Map<String, String>)request.getAttribute("hmReimCTCHeadAmount");
						if(hmReimCTCHeadAmount == null) hmReimCTCHeadAmount = new HashMap<String, String>();
						if(hmReimCTC.size() > 0 && hmReimCTCHeadAmount.size() > 0){
						%>		
							<tr>
								<td valign="top">
									<table cellspacing="1" cellpadding="2" class="table table-bordered autoWidth" style="float: left;">
										<tr>
											<td colspan="3" nowrap="nowrap" align="center"><h5>EARNING DETAILS</h5><h6>[Reimbursement part of CTC]</h6></td>
										</tr>
										<tr>
											<td class="alignRight">Salary Head</td>
											<td width="30%" class="alignRight">Monthly</td>
											<td width="30%" class="alignRight">Annual</td>
										</tr>
										<%
										double grossReimbursementAmount = 0.0d;
										double grossReimbursementYearAmount = 0.0d;
										Iterator<String> it = hmReimCTC.keySet().iterator();
										while(it.hasNext()){
											String strReimCTCId = it.next();
											String strReimCTCName = hmReimCTC.get(strReimCTCId);
											
											double dblReimMonth = uF.parseToDouble(hmReimCTCHeadAmount.get(strReimCTCId));
											double dblReimAnnual = uF.parseToDouble(hmReimCTCHeadAmount.get(strReimCTCId+"_ANNUAL"));
											grossReimbursementAmount += dblReimMonth;
											grossReimbursementYearAmount += dblReimAnnual;
											
											netTakeHome += dblReimMonth;
										%>
											<tr>
												<td class="alignRight"><%=uF.showData(strReimCTCName, "-")%></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblReimMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblReimAnnual)%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblReimMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblReimAnnual)%></td> --%>
											</tr>
										<%}
										dblCTCMonthly += grossReimbursementAmount;
										dblCTCAnnualy += grossReimbursementYearAmount;
										%>
											<tr>
												<td class="alignRight"><strong>Total</strong></td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossReimbursementAmount)%></strong></td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossReimbursementYearAmount)%></strong></td>
												<%-- <td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossReimbursementAmount))%></strong></td>
												<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossReimbursementYearAmount))%></strong></td> --%>
												
											</tr>
									</table>
								</td>
								<td>&nbsp;</td>
							</tr>				
						<%} %>
						<%
						List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
						if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
						int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
						if(nAnnualVariSize > 0){
						%>	
							<tr>
				                 <td valign="top">
									<table cellspacing="1" cellpadding="2" class="table table-bordered autoWidth" style="float: left;">
										<tr>
											<td colspan="3" nowrap="nowrap" align="center"><h5>EARNING DETAILS</h5><h6>[Annual Variables]</h6></td>
										</tr>
										<tr>
											<td class="alignRight">Salary Head</td>
											<td width="30%" class="alignRight">Monthly</td>
											<td width="30%" class="alignRight">Annual</td>
										</tr>
									<%	
										double grossAnnualAmount = 0.0d;
										double grossAnnualYearAmount = 0.0d;
										for(int i = 0; i < nAnnualVariSize; i++){
											List<String> innerList = salaryAnnualVariableDetailsList.get(i);
											double dblEarnMonth = uF.parseToDouble(innerList.get(2));
											double dblEarnAnnual = uF.parseToDouble(innerList.get(3));
											grossAnnualAmount += dblEarnMonth;
											grossAnnualYearAmount += dblEarnAnnual;
								%>
											<tr>
												<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnAnnual)%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnAnnual)%></td> --%>
											</tr>
									<%	} 
										dblCTCMonthly += grossAnnualAmount;
										dblCTCAnnualy += grossAnnualYearAmount;
									%>
										<tr>
											<td class="alignRight"><strong>Total</strong></td>
											<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossAnnualAmount)%></strong></td>
											<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossAnnualYearAmount)%></strong></td>
											<%-- <td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossAnnualAmount))%></strong></td>
											<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossAnnualYearAmount))%></strong></td> --%>
											
										</tr>
									</table>
								</td>
								<td>&nbsp;</td>
							</tr>
						<%}%>
						
					<%
						List<List<String>> salaryContributionDetailsList = (List<List<String>>) request.getAttribute("salaryContributionDetailsList");
						
						Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
						if(hmContribution == null) hmContribution = new HashMap<String, String>();
						double dblMonthContri = 0.0d;
						double dblAnnualContri = 0.0d;
						boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
						boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
						boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
						if(isEPF || isESIC || isLWF || (salaryContributionDetailsList!=null && salaryContributionDetailsList.size()>0)) {
					%>
							<tr>
								<td valign="top">
									<table cellspacing="1" cellpadding="2" class="table table-bordered autoWidth" style="float: left;">
										<tr>
											<td colspan="3" nowrap="nowrap" align="center"><h5>CONTRIBUTION DETAILS</h5></td>
										</tr>
										
										<tr>
											<td class="alignRight">Contribution Head</td>
											<td width="30%" class="alignRight">Monthly</td>
											<td width="30%" class="alignRight">Annual</td>
										</tr>
										<%if(isEPF){
											double dblEPFMonth = uF.parseToDouble(hmContribution.get("EPF_MONTHLY"));
											double dblEPFAnnual = uF.parseToDouble(hmContribution.get("EPF_ANNUALY"));
											dblMonthContri += dblEPFMonth;
											dblAnnualContri += dblEPFAnnual;
										%>
											<tr>
												<td class="alignRight">Employer PF</td>
												
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEPFMonth)%></td>
										 		<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEPFAnnual)%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEPFMonth) %></td>
										 		<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEPFAnnual) %></td> --%>
											</tr>
										<%} %>
										<%if(isESIC){
											
											double dblESIMonth = uF.parseToDouble(hmContribution.get("ESI_MONTHLY"));
											double dblESIAnnual = uF.parseToDouble(hmContribution.get("ESI_ANNUALY"));
											dblMonthContri += dblESIMonth;
											dblAnnualContri += dblESIAnnual;
										%>
											<tr>
												<td class="alignRight">Employer ESI</td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblESIMonth)%></td>
										 		<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblESIAnnual)%></td>
										 		
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblESIMonth) %></td>
										 		<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblESIAnnual) %></td> --%>
											</tr>
										<%} %>
										<%if(isLWF){
											double dblLWFMonth = uF.parseToDouble(hmContribution.get("LWF_MONTHLY"));
											double dblLWFAnnual = uF.parseToDouble(hmContribution.get("LWF_ANNUALY"));
											dblMonthContri += dblLWFMonth;
											dblAnnualContri += dblLWFAnnual;
										%>
											<tr>
												<td class="alignRight">Employer LWF</td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblLWFMonth) %></td>
										 		<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblLWFAnnual) %></td>
										 		
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblLWFMonth) %></td>
										 		<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblLWFAnnual) %></td> --%>
											</tr>
										<%} 
										
										/* dblCTCMonthly += dblMonthContri;
										dblCTCAnnualy += dblAnnualContri; */
										%>
										
										<%	
										//System.out.println("gratuitySalHeadAmt ===>> " + gratuitySalHeadAmt);
										double contributionMonthAmount = 0.0d;
										double contributionYearAmount = 0.0d;
										for(int i = 0; salaryContributionDetailsList!=null && i<salaryContributionDetailsList.size(); i++) {
											List<String> innerList = salaryContributionDetailsList.get(i);
											double dblEarnMonth = uF.parseToDouble(innerList.get(2));
											double dblEarnAnnual = uF.parseToDouble(innerList.get(3));
											if(innerList.get(4).equals(IConstants.GRATUITY+"")) {
												dblEarnMonth = (gratuitySalHeadAmt * uF.parseToDouble(hmGratuityPolicy.get("CALCULATE_PERCENT"))) / 100;
												dblEarnAnnual = dblEarnMonth * 12;
											}
											dblMonthContri += dblEarnMonth;
											dblAnnualContri += dblEarnAnnual;
										%>
											<tr>
												<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnAnnual)%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnAnnual)%></td> --%>
											</tr>
											<%	} 
											dblCTCMonthly += dblMonthContri;
											dblCTCAnnualy += dblAnnualContri;
										%>
										<tr>
											<td class="alignRight"><strong>Contribution Total</strong></td>
											
											<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(dblMonthContri)%></strong></td>
											<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(dblAnnualContri)%></strong></td>
											
											<%-- <td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(dblMonthContri))%></strong></td>
											<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(dblAnnualContri))%></strong></td> --%>
											
										</tr>
									</table>
								</td>
								<td>&nbsp;</td>
							</tr>
						<%}%>
                     </table>
                     <table class="table table_no_border autoWidth">
	                         <!-- ====start parvez on 12-08-2022===== -->     
	                              <tr >
					                  <!-- <td style="padding-left: 10px !important;">Net Take Home Per Month:</td> -->
					                  <td style="padding-left: 10px !important;">Net Earning:</td>
					                  <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+ uF.formatIntoOneDecimal(netTakeHome)%></td>
					                  <%-- <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+ uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(netTakeHome))%></td> --%>
					              </tr>                  
				                  <tr>
					                  <!-- <td style="padding-left: 10px !important;">Cost To Company (Monthly):</td> --> <!-- Start Dattatray  -->
					                  <td style="padding-left: 10px !important;">Gross Earning:</td>
					                  <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+ uF.formatIntoOneDecimal(dblCTCMonthly)%></td>
					                  <%-- <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+ uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(dblCTCMonthly))%></td> --%>
					              </tr>
					              <tr>
					                  <!-- <td style="padding-left: 10px !important;">Cost To Company (Annually):</td> --> <!-- Start Dattatray  -->
					                  <td style="padding-left: 10px !important;">Annual Gross Earning:</td>
					                  <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+uF.formatIntoOneDecimal(dblCTCAnnualy)%></td>
					                  <%-- <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(dblCTCAnnualy))%></td> --%>
					              </tr>
					       <!-- ====end parvez on 12-08-2022===== -->       
					              <%if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE){ %>
										<tr>
						              		<td class="alignRight">Salary Scale:</td>
						              		<%-- <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("MINSCALE"),"")+" - "+uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("MAXSCALE"),"")+" Increment Amount: - "+uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("INCREMENTAMOUNT"),"")%></td> --%>
						              		<td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("MINSCALE"),"")+" - "+uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("INCREMENTAMOUNT"),"")+" - "+uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("MAXSCALE"),"")%></td>
						              	</tr>
								<%} %>
                      </table> 
                       <%
						if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.ACCOUNTANT))){ 
							Map<String, String> hmPerkAlign = (Map<String, String>) request.getAttribute("hmPerkAlign");
							if(hmPerkAlign == null) hmPerkAlign = new HashMap<String,String>(); 
							Map<String, List<Map<String, String>>> hmPerkAlignSalary = (Map<String, List<Map<String, String>>>) request.getAttribute("hmPerkAlignSalary");
							if(hmPerkAlignSalary == null) hmPerkAlignSalary = new HashMap<String, List<Map<String, String>>>();
							Map<String, Map<String, String>> hmAssignPerkSalary = (Map<String, Map<String, String>>) request.getAttribute("hmAssignPerkSalary");
							if(hmAssignPerkSalary == null) hmAssignPerkSalary = new HashMap<String, Map<String,String>>();
							List<String> alPerkSalaryAppliedId = (List<String>) request.getAttribute("alPerkSalaryAppliedId");
							if(alPerkSalaryAppliedId == null) alPerkSalaryAppliedId = new ArrayList<String>();
							
							if(hmPerkAlign.size() > 0 && hmPerkAlignSalary.size() > 0){
						%>
						<!-- ====start parvez on 27-10-2021===== -->
								<div style="float: left; width: 100%; border-top: 1px solid #ccc; padding-top: 10px;"><strong>Perk Options</strong></div>
						<!-- ====end parvez on 27-10-2021===== -->
								<table class="table table_no_border">	
							<%	Iterator<String> it = hmPerkAlign.keySet().iterator();
								int x = 0;
								while(it.hasNext()){
									String strSalaryHeadId = it.next();
									String strSalaryHeadName = hmPerkAlign.get(strSalaryHeadId); 
									
									List<Map<String, String>> outerList = hmPerkAlignSalary.get(strSalaryHeadId);
									if (outerList == null) outerList = new ArrayList<Map<String, String>>();
									int nOuterList = outerList.size();
										
									x++;
							%>
									<tr>
										<td colspan="2"><strong><%=x %>. <%=strSalaryHeadName %></strong></td>
									</tr>
						
								<%} %>
						 		</table>
						<%	}
						}
						%>
						
						<%if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.ACCOUNTANT))){
							List<Map<String, String>> alReimbursementCTC = (List<Map<String, String>>)request.getAttribute("alReimbursementCTC");
							if(alReimbursementCTC == null) alReimbursementCTC = new ArrayList<Map<String,String>>();	
							
							Map<String, List<Map<String, String>>> hmReimbursementCTCHead = (Map<String, List<Map<String, String>>>) request.getAttribute("hmReimbursementCTCHead");
							if(hmReimbursementCTCHead == null) hmReimbursementCTCHead = new HashMap<String, List<Map<String,String>>>();
							
							Map<String, Map<String, String>> hmAssignReimHead = (Map<String, Map<String, String>>) request.getAttribute("hmAssignReimHead");
							if(hmAssignReimHead == null) hmAssignReimHead = new HashMap<String, Map<String,String>>();
							
							List<String> alReimbursementCTCAppliedId = (List<String>) request.getAttribute("alReimbursementCTCAppliedId");
							if(alReimbursementCTCAppliedId == null) alReimbursementCTCAppliedId = new ArrayList<String>();
							
							if(alReimbursementCTC.size() > 0){
						%>
						<!-- ====start parvez on 27-10-2021===== -->
								<div style="float: left; width: 100%; border-top: 1px solid #ccc; padding-top: 10px;"><strong>Reimbursement Part of CTC Options</strong></div>
					    <!-- ====end parvez on 27-10-2021===== -->
								<table>	
						<%		int nAlReimbursementCTC = alReimbursementCTC.size();
								int x = 0; 
								for(int i=0; i < nAlReimbursementCTC; i++){
									Map<String, String> hmReimbursementCTCInner = (Map<String, String>) alReimbursementCTC.get(i);
									String strReimCTCId = hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_ID");
									
									List<Map<String, String>> alReimCTCHead = hmReimbursementCTCHead.get(strReimCTCId);
									if(alReimCTCHead == null) alReimCTCHead = new ArrayList<Map<String,String>>();
									
									int nAlReimCTCHead = alReimCTCHead.size();  
									if(nAlReimCTCHead > 0){			
										x++;
						%>	
									<tr>
										<td colspan="2"><strong><%=(x) %>. <%=uF.showData(hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_NAME"),"") %> [<%=uF.showData(hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_CODE"),"") %>]</strong></td>
									</tr>
							<%	}
				            	}%>
								</table>
							<%}
	            		} %>
          		     </div>  
      			 </div>
          </section> 
          
		<!-- <div style="text-align: right; padding-right: 15px;">
			Start Dattatray Note : Next and Previous button sequence changed
			<button type="button" id="PreviousButton5" class="btn btn-submit" data-dismiss="modal">Previous</button>
        	<button type="button" id="nextButton6" class="btn btn-submit" data-dismiss="modal">Next</button>
        	End Dattatray
		</div> -->
		</div>
     </div>
     
    <div id="page7" hidden>
    	
    <!-- ====start parvez on 27-10-2021===== -->	
    	<div style="text-align: right; padding-right: 15px;">
        	<button type="button" id="PreviousButton6" class="btn btn-submit" data-dismiss="modal">Previous</button>
     	</div>
     	<div class="box-body">
    <!-- ====end parvez on 27-10-2021===== -->
	       <section class="col-lg-12 connectedSortable">
	       <!-- <div class="box box-info"> -->
	       <div class="box-info">
	      	 <!-- Start Dattatray -->
	       		<div class="box-body" style="padding: 15px; min-height: 420px; max-height: 500px; overflow-y: auto;display: flex;justify-content: center;align-items: center;" align="center"><!-- Start Dattatray Note : Set style -->
	      			 <h2 align="center">Thank You !!!</h2>
	      		</div>
	      		<!-- End Dattatray -->
	      	</div>
	       </section> 
       	 <br><br><br>
   <!-- ====start parvez on 27-10-2021===== -->
       	<!-- <div style="text-align: right; padding-right: 15px;">
        	<button type="button" id="PreviousButton6" class="btn btn-submit" data-dismiss="modal">Previous</button>
     	</div> --> 
     	</div>
   <!-- ====end parvez on 27-10-2021===== -->
     </div>
     
    </s:form>
  </div>
</section>
</div>     
     <!--*********code for next Button********* -->
     
   </section> 
      