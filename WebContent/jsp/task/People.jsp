<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <script type="text/javascript" src="DataTableJs/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="DataTableJs/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="DataTableJs/jszip.min.js"></script>
 <script type="text/javascript" src="DataTableJs/pdfmake.min.js"></script>
 <script type="text/javascript" src="DataTableJs/vfs_fonts.js"></script>
 <script type="text/javascript" src="DataTableJs/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="DataTableJs/buttons.print.min.js"></script> 
 <script type="text/javascript" src="js_bootstrap/datatables/dataTables.bootstrap.js"></script>  --%>

<script type="text/javascript">
$(function(){
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

$(document).ready( function () {
    $('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                "bBomInc": true
		            },
		            {
		                extend: 'excel',
		                "bBomInc": true
		            },
		            {
		                extend: 'pdf',
		                "bBomInc": true
		            },
		            {
		                extend: 'print',
		                "bBomInc": true
		            }
		        ]
  	});
});


function addPeople() { 
	window.location='AddPeople.action';
}


function letResourceInfo() {
	//alert("111 ------->> ");
	document.getElementById("modalDialog").style.width = "600px";
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Let Resource fill Information');				
	 $.ajax({  
		url : 'AddPeopleMode.action',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data); 
		}
	});
}



function viewWorkAllocation(emp_id,proStartDate,proEndDate) {
	//alert("111 ------->> ");
	document.getElementById("modalDialog").style.width = "800px";
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Work Allocation Summary');				
	 $.ajax({  
		url : 'ProjectWorkAllocation.action?emp_id='+emp_id+'&proStartDate='+proStartDate+'&proEndDate='+proEndDate,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data); 
		}
	});

}


function selectall(x,strEmpId){
	var  status = x.checked; 
	var  arr = document.getElementsByName(strEmpId);
	for(i = 0; i<arr.length; i++){ 
  		arr[i].checked=status;
 	}
}


function addToProject() { 
	var  arr = document.getElementsByName("alEmp");
	var strEmp = '';
	var cnt = 0;
	for(i = 0; i<arr.length; i++){
		if(arr[i].checked){
			var emp = arr[i].value;
			if(cnt == 0){
				strEmp = emp;
			} else {
				strEmp +=","+emp;
			}
			cnt++;
		}
 	}
	if(strEmp == ''){
		alert("Please select resource!");
	} else {
		document.getElementById("modalDialog").style.width = "400px";
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Add to Project');				
		 $.ajax({  
			url : 'AddtoProject.action?strEmpId='+strEmp,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data); 
			}
		});
	}
}


function executePeopleAction(val,strEmp,cnt,strEmpName){
	if(parseInt(val) == 1){
		addEmpToProject(strEmp);
	} else if(parseInt(val) == 2){
		var msg = 'Are you sure, you wish to delete '+strEmpName+'?';
		if(confirm(msg)) {
			var action='Peoples.action?operation=D&strEmpId='+strEmp;
			getContent("actionDiv"+strEmp+"_"+cnt,action);
		}
	} 
}


function addEmpToProject(strEmp) {
	//alert("111 ------->> ");
	document.getElementById("modalDialog").style.width = "300px";
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Add to Project');				
	 $.ajax({  
		url : 'AddtoProject.action?strEmpId='+strEmp,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data); 
		}
	});
}


function submitForm(type) {
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var strSkill = getSelectedValue("strSkills");
	var strEdu = getSelectedValue("strEducation");
	var strExp = getSelectedValue("strExperience");
	var strResType = getSelectedValue("resourceType");

	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strSkill='+strSkill+'&strEdu='+strEdu+'&strExp='+strExp+'&strResType='+strResType;
	}
	//alert("paramValues ===>> " + paramValues);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Peoples.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		if (choice.options[i].selected == true) {
			if (j == 0) {
				exportchoice = choice.options[i].value;
				j++;
			} else {
				exportchoice += "," + choice.options[i].value;
				j++;
			}
		}
	}
	return exportchoice;
}

</script>

<script type="text/javascript">
$(function(){
	
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#strSkills").multiselect().multiselectfilter();
	$("#strEducation").multiselect().multiselectfilter();
	$("#strExperience").multiselect().multiselectfilter();
	$("#resourceType").multiselect().multiselectfilter();
});    

</script>   

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF =  new UtilityFunctions();
	String strUserType = (String) session.getAttribute(IConstants.BASEUSERTYPE);
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	
	List<Map<String, String>> alPeopleList = (List<Map<String, String>>) request.getAttribute("alPeopleList");
	if(alPeopleList == null) alPeopleList = new ArrayList<Map<String,String>>();
	
	Map<String, List<List<String>>> hmEmpSkills = (Map<String, List<List<String>>>) request.getAttribute("hmEmpSkills");
	if(hmEmpSkills == null) hmEmpSkills = new HashMap<String, List<List<String>>>();
	Map<String,Map<String, String>> hmEmpProject = (Map<String,Map<String, String>>) request.getAttribute("hmEmpProject");
	if(hmEmpProject == null) hmEmpProject = new HashMap<String, Map<String,String>>();
	
	Map<String, Map<String, String>> hmLeaves = (Map<String, Map<String, String>>)request.getAttribute("hmLeaves");
	if(hmLeaves == null) hmLeaves = new HashMap<String, Map<String, String>>();
	
	Map<String, String> hmTaskAllocation = (Map<String, String>)request.getAttribute("hmTaskAllocation");
	if(hmTaskAllocation == null) hmTaskAllocation = new HashMap<String, String>();
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>

<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
	
	<div style="width:100%; font-size: 14px; text-align: right; margin-top: -10px;">
		<span style="font-weight: bolder; font-family: digital; color: green; font-size: 26px;"><%=uF.showData((String) request.getAttribute("empCnt"),"0") %></span>&nbsp;Resources 
	</div>
	
		<s:form name="frm_People" action="Peoples" theme="simple">
		<div class="box box-default collapsed-box">
               <div class="box-header with-border">
                   <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
                   <div class="box-tools pull-right">
                       <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                       <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                   </div>
               </div>
               <!-- /.box-header -->
               <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-filter"></i>
					</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Organization</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key="" />
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Location</p>
							<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" />
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Skill</p>
							<s:select name="strSkills" id="strSkills" list="skillsList" theme="simple" listKey="skillsId" listValue="skillsName" multiple="true"/>
						</div>

						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Education</p>
							<s:select  name="strEducation" id="strEducation" list="eduList" theme="simple" listKey="eduId" listValue="eduName" multiple="true"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Experience</p>
							<s:select theme="simple" name="strExperience" id="strExperience" list="#{'1':'0 to 1 Year','2':'1 to 2 Years','3':'2 to 5 Years','4':'5 to 10 Years','5':'10+ Years'}" multiple="true"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Resource Type</p>
							<s:select theme="simple" name="resourceType" id="resourceType" list="resourceList" listKey="resourceTypeId" listValue="resourceTypeName"  multiple="true"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
						</div>
					</div>
               </div>
           </div>
           
		</s:form>

	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE,""); %>
	
	<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || 
		strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.CFO))) {
	%>
	<div class="row row_without_margin">
		<div class="col-lg-6 col-md-6 col-sm-6 autoWidth">
			<a href="javascript:void(0)" onclick="addPeople();"><i class="fa fa-plus-circle"></i> Add People by Yourself</a>
			<a href="javascript:void(0)" onclick="letResourceInfo();"><i class="fa fa-plus-circle"></i> Let Resource fill Information</a>
		</div>
		<div class="col-lg-6 col-md-6 col-sm-6 autoWidth" style="float:right;">
			<p style="float:right;"><input type="button" value="Add to Project" onclick="addToProject();" class="btn btn-primary"/></p>
		</div> 
	</div>
	<% } %>
    
	<table id="lt" class="table table-bordered" style="width:100%; margin-top: 30px;clear:both;">
		<thead>
			<tr>
				<th style="text-align: left;" class="no-sort"><input type="checkbox" name="allPeople" id="allPeople" onclick="selectall(this,'alEmp')" /></th>
				<th style="text-align: left;">Resource Name</th>
				<!-- <th style="text-align: left;">Overall</th>
				<th style="text-align: left;">Skills- Rating</th> -->
				<th style="text-align: left;">Experience</th>
				<th style="text-align: left;">Education</th>
				<th style="text-align: left;">Availability</th>
				<th style="text-align: left;">Workload</th>
				<th style="text-align: center;">Cost<br/>(Monthly)</th>
				<th style="text-align: center;">Rate<br/>(Monthly)</th>
				<th style="text-align: left;">Last Project</th>
				<th style="text-align: center;">Joining Date</th>
				<th style="text-align: left;">Profile</th>
				<th style="text-align: left;">Action</th>
			</tr>
		</thead>

		<tbody>
			<%
			if(alPeopleList!=null && !alPeopleList.isEmpty() && alPeopleList.size() > 0){
				int cnt = 0;
				for(int i = 0; i < alPeopleList.size(); i++){
					Map<String, String> hmPeople = (Map<String, String>) alPeopleList.get(i);
					
					Map<String, String> hmProject = hmEmpProject.get(hmPeople.get("EMP_ID"));
					if(hmProject == null) hmProject = new HashMap<String, String>();
					
					Map<String, String> hmInnerLeave = hmLeaves.get(alPeopleList.get(i));
					if(hmInnerLeave == null) hmInnerLeave = new HashMap<String, String>();
			%>
				<tr>
					<td id="<%=hmPeople.get("EMP_ID") %>"><input type="checkbox" value="<%=hmPeople.get("EMP_ID") %>" name="alEmp" id="alEmp"/></td>
					<td>
						<%if(CF.getStrDocRetriveLocation() == null) { %>
							<img height="20" width="20" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmPeople.get("EMP_IMAGE") %>" />
						<%} else { %>
		                 	<img height="20" width="20" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmPeople.get("EMP_ID")+"/"+IConstants.I_22x22+"/"+hmPeople.get("EMP_IMAGE")%>" />
		                <%} %> 
						<%=hmPeople.get("EMP_NAME") %> 
					</td>
					<%-- <td nowrap="nowrap">
						<% 
							List<List<String>> alSkills = hmEmpSkills.get(hmPeople.get("EMP_ID"));
							if(alSkills != null && alSkills.size() > 0){
								double dblOverall = 0.0d;
								int nOverall = 0;
								for (int j = 0; j < alSkills.size(); j++) {
		                  			List<String> alInner = alSkills.get(j);
		                  			nOverall++;
		                  			dblOverall += uF.parseToDouble(alInner.get(2));
		                  		}
								cnt++;
								double dblOverallRating = 0.0d;
								if(dblOverall > 0.0d){
									dblOverallRating = (dblOverall/nOverall) / 2;
								}
							%>
								<span style="float:left;width:100%">
									<span id="starOverall<%=cnt%>"></span>
									<script type="text/javascript">
									    $(function() {
									    	$('#starOverall<%=cnt%>').raty({
												  readOnly: true,
												  start:    <%=dblOverallRating%>,
												  half: true
											});
								        });
								    </script>
								</span>
						<%	} else { %>
							&nbsp;
						<%	} %>
					</td>
					<td nowrap="nowrap">
						<% 
							if(alSkills != null && alSkills.size() > 0){
								for (int j = 0; j < alSkills.size(); j++) {
		                  			List<String> alInner = alSkills.get(j);
		                  			cnt++;
						%>
								<span style="float:left;width:100%">
									<%=alInner.get(1)%>:
									<span id="star<%=cnt%>"></span>
									<script type="text/javascript">
									    $(function() {
									    	$('#star<%=cnt%>').raty({
												  readOnly: true,
												  start:    <%=uF.parseToDouble(alInner.get(2)) / 2%>,
												  half: true
											});
								        });
								    </script>
								</span>
						<%		}
							} else { %>
							&nbsp;
						<%	} %>
					</td> --%>
					<td><%=uF.showData(hmPeople.get("EMP_EXPERIENCE"),"") %></td>
					<td><%=uF.showData(hmPeople.get("EMP_EDUCATION"),"") %></td>
					
					<%if(hmInnerLeave.size()==0){ %>
						<td nowrap="nowrap" style="background-color: #00CC00">No Leave</td>
					<%}else if(hmInnerLeave.size()<4){ %>
						<td nowrap="nowrap" style="background-color: #99FF00"><%=hmInnerLeave.size() %> leaves</td>
					<%}else if(hmInnerLeave.size()<7){ %>
						<td nowrap="nowrap" style="background-color: #FFFF33"><%=hmInnerLeave.size() %> leaves</td>
					<%}else if(hmInnerLeave.size()<15){ %>
						<td nowrap="nowrap" style="background-color: #FF9900"><%=hmInnerLeave.size() %> leaves</td>
					<%}else if(hmInnerLeave.size()>=15){ %>
						<td nowrap="nowrap" style="background-color: #FF3300"><%=hmInnerLeave.size() %> leaves</td>
					<%} else {%>
						<td nowrap="nowrap" style="background-color: #00CC00">No Leave</td>
					<%} %>
					
					<td align="center"><%=uF.showData(hmTaskAllocation.get(hmPeople.get("EMP_ID")), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\">&nbsp;</div>") %></td>
					
					<td nowrap="nowrap" style="text-align: right;"><%=uF.showData(hmProject.get("COST"),"") %></td>
					<td nowrap="nowrap" style="text-align: right;"><%=uF.showData(hmProject.get("RATE"),"") %></td>
					<td><%=uF.showData(hmProject.get("PRO_NAME"),"") %></td>
					<td style="text-align: center;"><%=uF.showData(hmPeople.get("JOINING_DATE"),"") %></td>
					<td><%=uF.showData(hmPeople.get("FACTSHEET"),"") %></td>
					<td>
						<%-- <a onclick="addEmpToProject('<%=uF.showData(hmPeople.get("EMP_ID"),"") %>')" href="javascript: void(0)"><img title="Add To Project" src="images1/icons/icons/summary_icon_b.png"></a> --%>
						<select name="peopleActions<%=hmPeople.get("EMP_ID") %>_<%=i %>" id="peopleActions<%=hmPeople.get("EMP_ID") %>_<%=i %>" style="width: 100px !important;" onchange="executePeopleAction(this.value, '<%=hmPeople.get("EMP_ID") %>',<%=i%>,'<%=hmPeople.get("EMP_NAME") %>');">
	                    	<option value="">Actions</option>
	                    	<option value="1">Add to Project</option>
                    		<option value="2">Delete</option>
		                </select>
		                <div id="actionDiv<%=hmPeople.get("EMP_ID") %>_<%=i%>"></div>
					</td>
				</tr>
			<%	} } %>
		</tbody>
	</table>
</div>

	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog" id="modalDialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:400px; overflow-y:auto; padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>


<script>
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});  
</script>