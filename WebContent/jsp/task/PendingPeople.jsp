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
		url: 'PendingPeople.action?f_org='+org+paramValues,
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
	$("#resourceType").multiselect();
});

</script>   

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF =  new UtilityFunctions();
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	
	List<Map<String, String>> alPeopleList = (List<Map<String, String>>) request.getAttribute("alPeopleList");
	if(alPeopleList == null) alPeopleList = new ArrayList<Map<String,String>>();
	
	Map<String, List<List<String>>> hmEmpSkills = (Map<String, List<List<String>>>) request.getAttribute("hmEmpSkills");
	if(hmEmpSkills == null) hmEmpSkills = new HashMap<String, List<List<String>>>();
	
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>

<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
	
	<div style="width:100%; font-size: 14px; text-align: right; margin-top: -10px;">
		<span style="font-weight: bolder; font-family: digital; color: green; font-size: 26px;"><%=alPeopleList.size() %></span>&nbsp;Pending Resources 
	</div>
	
	<s:form name="frm_People" action="PendingPeople" theme="simple">
	
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
    
    
	<table id="lt" class="table table-bordered" style="width:100%; margin-top: 30px;clear:both;">
		<thead>
			<tr>
				<th style="text-align: left;">Resource Name</th>
				<th style="text-align: left;">Skills</th>
				<th style="text-align: left;">Email Id</th>
				<th style="text-align: left;">Mobile No.</th>
				<th style="text-align: left;">Profile</th>
			</tr>
		</thead>

		<tbody>
			<%
			if(alPeopleList!=null && !alPeopleList.isEmpty() && alPeopleList.size() > 0){
				int cnt = 0;
				for(int i = 0; i < alPeopleList.size(); i++){
					Map<String, String> hmPeople = (Map<String, String>) alPeopleList.get(i);
			%>
				<tr>
					<td>
						<%if(CF.getStrDocRetriveLocation() == null) { %>
							<img height="20" width="20" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmPeople.get("EMP_IMAGE") %>" />
						<%} else { %>
		                 	<img height="20" width="20" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmPeople.get("EMP_ID")+"/"+IConstants.I_22x22+"/"+hmPeople.get("EMP_IMAGE")%>" />
		                <%} %> 
						<%=hmPeople.get("EMP_NAME") %> 
					</td>
					<td>
						<% 
							List<List<String>> alSkills = hmEmpSkills.get(hmPeople.get("EMP_ID"));
							if(alSkills != null && alSkills.size() > 0){
								for (int j = 0; j < alSkills.size(); j++) {
		                  			List<String> alInner = alSkills.get(j);
		                  			cnt++;
		                  			String strSkills = "";
		                  			if(j == 0){
		                  				strSkills = "<strong>"+alInner.get(1)+"</strong>";
									} else { 
										strSkills = ", "+alInner.get(1);
									%>
									<%} %>
									<%=strSkills %>	
						<%		}
							} else { %>
							&nbsp;
						<%	} %>
					</td>
					<td><%=uF.showData(hmPeople.get("EMP_EMAIL"),"") %></td>
					<td><%=uF.showData(hmPeople.get("EMP_MOBILE_NO"),"") %></td>
					<td><%=uF.showData(hmPeople.get("FACTSHEET"),"") %></td>
					
				</tr>
			<%	}
			} else { %>
				
				<tr>
					<td colspan="5">
						<div class="nodata msg"><span>No pending people found.</span></div>
					</td>
				</tr>
			<%} %>
		</tbody>
	</table>
</div>


<script>
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});  
</script>