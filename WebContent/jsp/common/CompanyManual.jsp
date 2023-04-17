
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>


<style>


.level_list li
  {
    list-style-type:none;
	font-size:12px;
	margin:0px 0px 1px 0px;
	line-height:25px; 
	/* background: none repeat scroll 0 0 #f4f4f4;  */
    border-bottom: 1px solid #CCCCCC;
	padding:5px 10px;
	  }

.level_list ul li
{
  list-style-type:none;
  font-weight:normal;
  margin:5px 0px 0px 35px; 
  padding:0px 0px 0px 20px;
  border:0px #ccc solid;
}

.level_list ul li ul li
{
  padding:0px 0px 0px 0px;
}
.level_list ul li ul li 
{
     padding:0px 0px 0px 25px;
}

.addnew
{
  padding:4px 5px;
  
  color:#666666;
  font-size:12px;
  display:block;
  width:245px;
}

.addnew a
 {
   color:#666666;
   font-weight:normal;
 }

.del
{
  
  text-decoration:none;
  width:25px;
  
}

.level_list ul li .desgn
{
  margin:3px 0px 3px 0px;
  
}

</style>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script>
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
});
function addNewManual(orgId,userscreen,navigationId,toPage) {

	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Add New Manual');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url : "AddCompanyManual.action?orgId=" + orgId+ "&userscreen=" +userscreen+ "&navigationId=" + navigationId + "&toPage=" + toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}
function previewManual(E) {

	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Add New Manual');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url : "ViewCompanyManual.action?E=" +E,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}

function submitForm() {
	var form_data = $("#frmCompanyManual").serialize();
	//console.log("form_data==>"+form_data);
	$("#actionResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
   	
    $.ajax({
   		url: "CompanyManual.action",
   		type: 'POST',
   		data: form_data,
   	    success: function(result){
   	    	$("#actionResult").html(result);
   		 }
     });
}

function deleteManual(strAction, orgId, userscreen, navigationId, toPage) {
	//alert("strAction ===>> " + strAction);
	if(confirm('Are you sure you wish to delete this manual?')) {
		$("#actionResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
	   	$.ajax({
	   		url: strAction,
	   		type: 'POST',
	   		cache: false,
	   	    success: function(result){
	   	    	//console.log("result==>"+result);
	   			$("#actionResult").html(result);
	   	    }, error: function(result){
	   	    	$.ajax({
	   		   		url: 'CompanyManual.action?strOrg='+orgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
	   		   		type: 'POST',
	   		   		cache: false,
	   		   	    success: function(result){
	   		   	    	//console.log("result==>"+result);
	   		   			$("#actionResult").html(result);
	   		   	    }
	   		     });
	   	    }
	     });
	}
}

function editManual(strAction) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Add New Manual');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	
   	$.ajax({
   		url: strAction,
   		type: 'POST',
   		cache: false,
   	    success: function(data){
   	    	//console.log("result==>"+data);
   	    	$(dialogEdit).html(data);
   			
   	    }
     });	
}
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	List reportList = (List)request.getAttribute("reportList");
	String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
	String strOrg =  (String)request.getAttribute("strOrg");
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");

	/* System.out.println("userscreen CM.jsp ===>> " + userscreen);
	System.out.println("navigationId CM.jsp ===>> " + navigationId);
	System.out.println("toPage CM.jsp ===>> " + toPage); */
	
	String pageFrom = (String)request.getAttribute("pageFrom");
    
%>
  

<% if(pageFrom == null || pageFrom.trim().equals("") || !pageFrom.trim().equalsIgnoreCase("MyHub")){ %>

	<div class="box-body">
			
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frmCompanyManual" id="frmCompanyManual" action="CompanyManual" theme="simple">
					<s:hidden name="userscreen" id="userscreen" />
					<s:hidden name="navigationId" id="navigationId" />
					<s:hidden name="toPage" id="toPage" />
					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm();"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm();"></s:select>
								<% } %>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
		
	<% } %>	
	
	<% if(pageFrom == null || pageFrom.trim().equals("") || !pageFrom.trim().equalsIgnoreCase("MyHub")){ %>		
		<div class="col-md-12">
			<a href="javascript:void(0);" onclick="addNewManual(<%=request.getAttribute("strOrg") %>,'<%=userscreen %>','<%=navigationId %>','<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Manual</a> 
		</div>
	<%} %>
	
	<div class="col-md-12">
		<s:hidden name="pageFrom" id="pageFrom"></s:hidden>
		<s:hidden name="strOrg" id="strOrg"></s:hidden>
		 <%
		 for(int i=0; i<reportList.size(); i++){
				List alInner = (List)reportList.get(i);
				if(alInner==null)alInner = new ArrayList();
		 %>
			 <div id ="manualDiv_<%=alInner.get(3) %>" style="float:left;width:99%;margin: 10px 7px 10px 0px;padding-bottom: 10px;padding-top: 10px;border-bottom:1px solid #efefef;">
				<div style="float:left;margin-left:3px;width:100%;">
					<div style="float;left;">
					     <%--System.out.println("strUserType==>"+strUserType); --%>
						<%--if(strUserType != null  && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {--%>
							<a href="javascript:void(0);" onclick="deleteManual('AddCompanyManual.action?orgId=<%=request.getAttribute("strOrg") %>&D=<%=alInner.get(3)%>&pageFrom=<%=pageFrom%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>', '<%=request.getAttribute("strOrg") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" style="color:rgb(233,0,0)"> <i class="fa fa-trash" aria-hidden="true"></i> </a> 
							<a href="javascript:void(0);" onclick="editManual('AddCompanyManual.action?E=<%=alInner.get(3)%>&pageFrom=<%=pageFrom%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>')" style="margin-left:-3px;"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						<%--} --%>
						
					</div>
					<div style="float:left;"><%=alInner.get(5)%></div>
					<div style="float:left;margin-left:5px;">
						Title: <strong><%=alInner.get(1)%></strong>&nbsp;&nbsp;&nbsp;Organisation: <strong><%=alInner.get(4)%></strong>
					</div>
				</div> 
				<div style="float:left;margin-left:66px;width:92%;">
				    <div style="float:left;">Status: <strong><span id="myDiv<%=i %>"><%=alInner.get(2)%></span></strong></div>
					<div style="float:left;margin-left:10px;">Last Updated:<strong><%=alInner.get(0)%></strong> </div>
					<div style="float:right;margin-right:5px;"> <a href="javascript:void(0);" onclick="previewManual('<%=alInner.get(3)%>')" style="float:right">Preview Manual</a> </div>
				</div>   
			</div>
		<% } %>
	</div>	
<% if(pageFrom == null || pageFrom.trim().equals("") || !pageFrom.trim().equalsIgnoreCase("MyHub")){ %>
</div>
 <% } %>
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