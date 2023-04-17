<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
  

<script type="text/javascript">
    $(function(){
    	$("#f_strWLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_level").multiselect().multiselectfilter();
    });    
</script>
 <%	
 	UtilityFunctions uF = new UtilityFunctions();
	String dataType = (String) request.getAttribute("dataType"); 
	String currUserType = (String) request.getAttribute("currUserType");
	String strEmpId = (String) request.getAttribute("strEmpId");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
    String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
    String sbData = (String) request.getAttribute("sbData");
    String strSearchJob = (String) request.getAttribute("strSearchJob");
    String orgId = (String) request.getAttribute("f_org");
    String location = (String) request.getAttribute("strLocation");
    String department = (String) request.getAttribute("strDepartment");
    String level = (String) request.getAttribute("strLevel");
    String strSearch = (String) request.getAttribute("strSearchJob");
    
%> 
  
<section class="content">
   <div class="row jscroll">
       <section class="col-lg-12 connectedSortable">
       <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
			<% session.removeAttribute(IConstants.MESSAGE); %>
			<s:form name="frm_GoalKRA" action="GoalKRATargetDashboardData" id="frm_GoalKRA" theme="simple" cssStyle="margin-top: 10px;">
				<s:hidden name="dataType"></s:hidden>
				<s:hidden name="proPage" id="proPage" />
				<s:hidden name="minLimit" id="minLimit" />
				<s:hidden name="currUserType" id="currUserType"/>
				<% if((!strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
				<div style="margin-bottom: 5px; color: #232323;">
					<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
						<div class="box-header with-border">
							<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div>
	                  <!-- /.box-header -->
						<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="submitDIV">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');" />
									</div>
								</div>
							</div><br>
						</div>
	                    <!-- /.box-body -->
					</div>
				</div>
	         <% } %>
	         
	         <div class="col-md-12" style="margin: 0px 0px 10px 0px; text-align: right;">
	             <div style="float: left;line-height: 22px; width: 514px; margin-left: 350px;">
	                 <span style="float: left; display: block; width: 78px;">Search:</span>
	                 <div style="margin: 0px 0px 0px 16px; float: left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
	                     <div style="float: left">
	                         <input type="text" id="strSearchJob" class="form-control" name="strSearchJob"
	                             style="margin-left: 0px; width: 250px; box-shadow: 0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>" />
	                     </div>
	                     <div style="float: right">
	                         <input type="button" value="Search" class="btn btn-primary" name="submit" onclick="submitForm('2');" style="margin-left: 10px;"/>
	                     </div>
	                 </div>
	             </div>
	             <script>
	                 $( "#strSearchJob" ).autocomplete({
	                 	source: [ <%=uF.showData(sbData,"") %> ]
	                 });
	             </script>
	             <% if(strUserType!=null && (strUserType.equals(IConstants.MANAGER) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN) || (currUserType != null && currUserType.equals(strBaseUserType)))) { %>
	              <div style="float: right;">
	                  <a style="height: 1px; width: 2px;" href="javascript:void(0)" onclick="myKRA('<%=currUserType %>');" title="Add New Individual KRA"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Individual KRA</a>
	              </div>
	              <%-- <div style="float: right; margin-right: 10px;">
	                  <a style="height: 1px; width: 2px;" href="javascript:void(0)" onclick="myTarget('<%=currUserType %>');" title="Add New Traget"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Target</a>
	              </div> --%>
	             <% } %>
	         </div>
	      </s:form>
		  
		  <div class="row row_without_margin">
			<!-- ===start parvez date: 23-02-2023=== -->	
				<div class="col-md-3" style="padding-left: 0px;max-height: 450px;overflow-y:hidden;" id="leftSectionKRA"><!-- @uthor Dattatray  -->
			<!-- ===end parvez date: 23-02-2023=== -->		
					<div class="box box-thin">
						<div class="nav-tabs-custom">
				             <ul class="nav nav-tabs">
				                 <li class="active"><a href="javascript:void(0)" onclick="getGoalKRAEmpList('GoalKRAEmpList','L','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>', '');" data-toggle="tab">Live</a></li>
				                 <li><a href="javascript:void(0)" onclick="getGoalKRAEmpList('GoalKRAEmpList','C','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>', '');" data-toggle="tab">Closed</a></li>
				             </ul>
				             <div class="tab-content" >
				                 <div class="active tab-pane" id="goalKraResult">
							
				                 </div>
				             </div>
				        </div>
					</div>
				</div>    
	   
	  		<!-- ===end parvez date: 23-02-2023=== --> 
				<div class="col-md-9" style="padding-left: 0px; max-height: 450px; overflow-y:hidden;" id="rightSectionKRA"><!-- @author Dattatray  -->
			<!-- ===end parvez date: 23-02-2023=== -->		
					<div class="box box-thin" style="padding: 5px;" id="actionResult"><!-- @author Dattatray Note: removed overflow-y from style -->
						<div class="nav-tabs-custom">
				               <div class="tab-content" >
				                 <div class="active tab-pane" id="goalKraDetails"	>
							
				                 </div>
				             </div>
				        </div>
					</div>
			   </div>
			</div>
       </section>
    </div>
</section> 

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		getGoalKRAEmpList('GoalKRAEmpList','<%=dataType%>','<%=currUserType%>','<%=orgId%>','<%=location%>','<%=department%>','<%=level%>','<%=strSearch %>','<%=strEmpId %>');
	});
	
	function getGoalKRAEmpList(strAction,dataType,currUserType,orgId,location,dept,level,strSearch,strEmpId){
		//alert("getGoalKRANamesList jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		var paramValues = 'dataType='+dataType+'&currUserType='+currUserType+'&f_org='+orgId+'&strLocation='+location+'&strDepartment='+dept
		+'&strLevel='+level+'&strEmpId='+strEmpId;
		$("#goalKraResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?'+paramValues+'&strSearchJob='+strSearch,
			cache: true,
			success: function(result){
				//alert("result2==>"+result);
				$("#goalKraResult").html(result);
	   		}
		});
	}
	
	function submitForm(type) {
		
		var value = "Search";
		var org = "";
		var location = "";
		var department = "";
		var level = "";
		if(document.getElementById("f_org")){
			org = document.getElementById("f_org").value;
		} 
		
		if(document.getElementById("f_strWLocation")){
			location = getSelectedValue("f_strWLocation");
		}
		
		if(document.getElementById("f_department")){
			 department = getSelectedValue("f_department");
		}
		
		if(document.getElementById("f_level")){
			 level = getSelectedValue("f_level");
		}
		 
		var strSearch = document.getElementById("strSearchJob").value;
		var currUserType = document.getElementById("currUserType").value;
		var paramValues = "";
    	if(type == "2") {
    		value = "Submit";
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&strSearchJob='+strSearch;
		}
        
    	var action = 'GoalKRATargetDashboardData.action?f_org='+org+'&currUserType='+currUserType+paramValues;
    	
    	$("#goalKraTargetData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
            	$("#goalKraTargetData").html(result);
       		}
    	});
    	
    }
	
	/* @uthor : Dattatray */
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#leftSectionKRA").scrollTop() != 0) {
	        	$("#leftSectionKRA").scrollTop($("#leftSectionKRA").scrollTop() - 30);
	        }
	        if($(window).scrollTop() == 0 && $("#rightSectionKRA").scrollTop() != 0) {
	        	$("#rightSectionKRA").scrollTop($("#rightSectionKRA").scrollTop() - 30);
	        }
	    } else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#leftSectionKRA").scrollTop($("#leftSectionKRA").scrollTop() + 30);
	   		}
	         if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	 		   $("#rightSectionKRA").scrollTop($("#rightSectionKRA").scrollTop() + 30);
			}
	    }
	});

	/* @uthor : Dattatray */
	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#leftSectionKRA").scrollTop($("#leftSectionKRA").scrollTop() + 50);
	   		}
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#rightSectionKRA").scrollTop($("#rightSectionKRA").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
			if($(window).scrollTop() == 0 && $("#leftSectionKRA").scrollTop() != 0) {
		    	$("#leftSectionKRA").scrollTop($("#leftSectionKRA").scrollTop() - 50);
		    }
			if($(window).scrollTop() == 0 && $("#rightSectionKRA").scrollTop() != 0) {
		    	$("#rightSectionKRA").scrollTop($("#rightSectionKRA").scrollTop() - 50);
		    }
		}
	});

</script>          
    