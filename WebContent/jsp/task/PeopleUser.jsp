<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script type="text/javascript">
$(document).ready( function () {
    $('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});
});


function changeUserName(empid,userid,empname) {
	//alert("111 ------->> ");
	document.getElementById("modalDialog").style.width = "400px";
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Change Username for '+empname);				
	 $.ajax({
		url : 'ChangeUserName.action?empid='+empid+'&userid='+userid+'&fromPage=people',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data); 
		}
	});
}


function changeUserType(empid,userid,empname) {
	document.getElementById("modalDialog").style.width = "550px";
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Change User type for '+empname);				
	 $.ajax({  
		url : 'ChangeUserType.action?empid='+empid+'&userid='+userid+'&fromPage=people',
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

	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strSkill='+strSkill+'&strEdu='+strEdu+'&strExp='+strExp;
	}
	//alert("paramValues ===>> " + paramValues);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'PeopleUser.action?f_org='+org+paramValues,
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
	$("#f_strWLocation").multiselect();
	$("#strSkills").multiselect();
	$("#strEducation").multiselect();
	$("#strExperience").multiselect();
});

</script>   

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF =  new UtilityFunctions();
	String strUserType = (String) session.getAttribute(IConstants.BASEUSERTYPE);
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	
	List<Map<String, String>> alPeopleList = (List<Map<String, String>>) request.getAttribute("alPeopleList");
	if(alPeopleList == null) alPeopleList = new ArrayList<Map<String,String>>();
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
	
		<div style="width:100%; font-size: 14px; text-align: right; margin-top: -10px;">
			<span style="font-weight: bolder; font-family: digital; color: green; font-size: 26px;"><%=alPeopleList.size() %></span>&nbsp;Users 
		</div>
		
		<s:form name="frm_PeopleUser" action="PeopleUser" theme="simple">
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
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
						</div>
					</div>
				</div>
			</div>
						
		</s:form>

	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE,""); %>
	
	<table id="lt" class="table table-bordered" style="width:100%; margin-top: 30px;clear:both;">
		<thead>
			<tr>
				<!-- <th style="text-align: left;"><input type="checkbox" name="allCandidates" id="allCandidates" onclick="checkUncheckAllPeople();" /></th> -->
				<th style="text-align: left;">Person Name</th>
				<th style="text-align: left;">UserName</th>
				<th style="text-align: left;">Password</th>
				<th style="text-align: left;">Created On</th>
				<th style="text-align: left;">User Type</th>
				<th style="text-align: left;">Reset</th>
				<th style="text-align: left;">Last Reset On</th>
				<th style="text-align: left;">Action</th>
			</tr>
		</thead>

		<tbody>
			<%
			if(alPeopleList!=null && !alPeopleList.isEmpty() && alPeopleList.size() > 0){
				for(int i = 0 ; i < alPeopleList.size(); i++){
					Map<String, String> hmPeople = (Map<String, String>) alPeopleList.get(i);
			%>
				<tr id="<%=hmPeople.get("EMP_ID") %>">
					<%-- <td><input type="checkbox" value="<%=hmPeople.get("EMP_ID") %>" name="alEmp" id="alEmp"/></td> --%>
					<td>
						<%if(CF.getStrDocRetriveLocation() == null) { %>
							<img height="20" width="20" border="0" class="lazy img-circle" style="float:left; margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmPeople.get("EMP_IMAGE") %>" />
						<%} else { %>
		                 	<img height="20" width="20" border="0" class="lazy img-circle" style="float:left; margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmPeople.get("EMP_ID")+"/"+IConstants.I_22x22+"/"+hmPeople.get("EMP_IMAGE")%>" />
		                <%} %> 
						<%=hmPeople.get("EMP_NAME") %> 
					</td>
					<td><%=hmPeople.get("USER_NAME") %></td>
					<td><%=hmPeople.get("PASSWORD") %></td>
					<td><%=hmPeople.get("CREATED_ON") %></td>
					<td><%=hmPeople.get("USER_TYPE") %></td>
					<td><%=hmPeople.get("RESET_PASSWORD") %></td>
					<td><%=hmPeople.get("RESET_TIMESTAMP") %></td>
					<td><%=hmPeople.get("CHANGE_USERTYPE") %></td>
					
				</tr>
			<%	}
			} else { %>
				<tr>
					<td colspan="14">
						<div class="nodata msg"><span>No people found.</span></div>
					</td>
				</tr>
			<%} %>
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
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});  
</script>