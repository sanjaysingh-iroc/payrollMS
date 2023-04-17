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
    String callFrom = (String) request.getAttribute("callFrom");
 	
    %>
  
<section class="content">
   <div class="row jscroll">
       <section class="col-lg-12 connectedSortable">
       <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
			<% session.removeAttribute(IConstants.MESSAGE); %>
			<s:form name="frm_GoalKRA" action="GoalKRATargetDashboardData_1" id="frm_GoalKRA" theme="simple" cssStyle="margin-top: 10px;">
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
				<div>
                   <ul class="nav nav-tabs" style="boder-bottom :none !important; border : none !important ">
			         	<li <%if(callFrom==null || callFrom.equals("") || callFrom.equals("COMPSC")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getGoalKRACompany('GoalKRATargetCompany','X','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>');" data-toggle="tab">Company Scorecard</a></li>
			      		<li <%if(callFrom!=null && callFrom.equals("EMPSC")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getGoalKRAEmpList('GoalKRAEmpList_1','L','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>');" data-toggle="tab">Employee Scorecard</a></li>
			      		<li><a href="javascript:void(0)" onclick="getBSCView('BscView','BSC','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>');" data-toggle="tab">BSC's</a></li>
			        </ul>
				</div>
				<div style="padding-top:30px;">
	                <div style="margin: 0px 0px 0px 0px; float: left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
						<div style="float: left">
	                         <input type="text" id="strSearchJob" class="form-control" name="strSearchJob" style="margin-left: 0px; width: 250px; box-shadow: 0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>" />
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
	             <% if(strUserType!=null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN) || (currUserType != null && currUserType.equals(strBaseUserType)))) { %>
	              <div id="divAddBusinessGoal" style="display:none; float: right;">
	              		<a style="height: 1px; width: 2px;" href="javascript:void(0)" onclick="myKRA('<%=currUserType %>');" title="Add New Business Goal"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Business Goal</a>
	              </div>
	               <div id="divAddBSC" style="display:none; float: right; margin-right: 10px;">
	                  	<a style="height: 1px; width: 2px;" href="javascript:void(0)" onclick="myBsc('<%=currUserType %>');" title="Add New BSC"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New BSC</a>
	              </div>
	              <%-- <div style="float: right; margin-right: 10px;">
	                  <a style="height: 1px; width: 2px;" href="javascript:void(0)" onclick="myTarget('<%=currUserType %>');" title="Add New Traget"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Target</a>
	              </div> --%>
	             <% } %>
	         </div>
	         
	   
	      </s:form>
		  
		  <div class="row row_without_margin">
		 
				<div class="col-lg-2 col-md-2 col-sm-12" style="padding-left: 0px;">
					<div class="box box-thin">
						      <div class="nav-tabs-custom">
						   
				             <div class="tab-content" >
				                 <div class="active tab-pane" id="goalKraResult" style="min-height: 600px;">
										
				                 </div>
				             </div>
				        </div>
					</div>
				</div>    
	   
				<div class="col-lg-10 col-md-10 col-sm-12" style="padding-left: 0px;min-height: 600px;">
					<div class="box box-thin" style="padding: 5px;overflow-y: auto; min-height: 600px;" id="actionResult">
						<div class="nav-tabs-custom">
				               <div class="tab-content" >
				                 <div class="active tab-pane" id="goalKraDetails" style="min-height: 600px;">
										
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
		<%if(callFrom!=null && callFrom.equals("EMPSC")) { %>
			getGoalKRAEmpList('GoalKRAEmpList_1','<%=dataType%>','<%=currUserType%>','<%=orgId%>','<%=location%>','<%=department%>','<%=level%>','<%=strSearch %>');
		<% } else { %>
			getGoalKRACompany('GoalKRATargetCompany','<%=dataType%>','<%=currUserType%>','<%=orgId%>','<%=location%>','<%=department%>','<%=level%>','<%=strSearch %>');
			<% } %>
	});
	
	function getGoalKRAEmpList(strAction,dataType,currUserType,orgId,location,dept,level,strSearch){
		//alert("getGoalKRANamesList jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		if(document.getElementById("divAddBusinessGoal")) {
			document.getElementById("divAddBusinessGoal").style.display = 'block';
		}
		if(document.getElementById("divAddBSC")) {
			document.getElementById("divAddBSC").style.display = 'none';
		}
		
		var paramValues = 'dataType='+dataType+'&currUserType='+currUserType+'&f_org='+orgId+'&strLocation='+location+'&strDepartment='+dept+'&strLevel='+level;
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
	
	function getGoalKRACompany(strAction,dataType,currUserType,orgId,location,dept,level,strSearch){
		if(document.getElementById("divAddBusinessGoal")) {
			document.getElementById("divAddBusinessGoal").style.display = 'none';
		}
		if(document.getElementById("divAddBSC")) {
			document.getElementById("divAddBSC").style.display = 'none';
		}
		
		$("#goalKraResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		var form_data = $("#"+this.id).serialize();
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?tabType='+dataType,
			data: form_data,
			cache: true,
			success: function(result){
				//alert("result2==>"+result);
				$("#goalKraResult").html(result);
	   		}
		});
	}

	function getBSCView(strAction,dataType,currUserType,orgId,location,dept,level,strSearch) {
		if(document.getElementById("divAddBusinessGoal")) {
			document.getElementById("divAddBusinessGoal").style.display = 'none';
		}
		if(document.getElementById("divAddBSC")) {
			document.getElementById("divAddBSC").style.display = 'block';
		}

		$("#goalKraResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?tabType='+dataType,
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
		//alert("currUserType ===>> " + currUserType);
		var paramValues = "";
    	if(type == "2") {
    		value = "Submit";
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&strSearchJob='+strSearch;
		}
    	var action = 'GoalKRATargetDashboardData_1.action?f_org='+org+'&currUserType='+currUserType+paramValues;
    	//alert("action ===>> " + action);
    	$("#goalKraTargetData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
            	$("#goalKraTargetData").html(result);
       		}
    	});
    }

</script>          
    