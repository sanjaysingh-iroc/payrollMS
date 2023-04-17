<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<g:compress>
	<style>
		.ui-state-default {
			padding-right: 20px;
			padding-left: 20px;
		}
		.noemp_block {
			margin: 5px;
			padding: 5px;
			width: 90%;
			height: 30px;
			float: left;
			border: 0px solid #ccc;
			text-align: center;
			font-size: 15px;
			font-weight: bold;
			color: #454545;
		}

		.emp_block {
			margin: 5px;
			padding: 5px;
			min-height: 115px;
			border: 1px solid #ccc;
		}
		
		.emp_img {
			float: left;
			border: 1px solid #ccc;
			width: 84px;
			height: 82px;
			margin-right: 5px;
		}
		
		.widget-user-header{
		   background-color: #E5F6FF !important;
		   color: #000 !important;
		}
   </style>

</g:compress>
<%
	UtilityFunctions uF = new UtilityFunctions();
	
	String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
	
	List<String> empIdList = (List<String>) request.getAttribute("empIdList");
	Map<String, List<List<String>>> hmEmpSkills = (Map<String, List<List<String>>>) request.getAttribute("hmEmpSkills");
	Map<String, Map<String, String>> hmEmpProfile1 = (Map<String, Map<String, String>>) request.getAttribute("hmEmpProfile");
	
	Map<String, Map> hmEAttributeData = (Map<String, Map>) request.getAttribute("hmEAttributeData");
	
	Map<String,Map<String, String>> empSkillAvgRating = (Map<String,Map<String, String>>) request.getAttribute("empSkillAvgRating");
%>

<g:compress>
	<link rel="stylesheet" href="<%= request.getContextPath()%>/css/select/prism.css">
	<link rel="stylesheet" href="<%= request.getContextPath()%>/css/select/chosen.css">
	<style type="text/css" media="all">
		/* fix rtl for demo */
		.chosen-rtl .chosen-drop {
			left: -9000px;
		}
	</style>
</g:compress>

<%String fromPage = (String)request.getAttribute("from");
	//System.out.println("fromPage==>"+fromPage);
%>
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%} %>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>

<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-body"style="padding: 5px; overflow-y: auto; min-height: 400px;">
<%} %>
			<div class="leftbox reportWidth">
				<s:form action="SearchEmployeeSkills" name ="formID" id = "formID" theme="simple">
				    <input type="hidden" id="fromPage" name="<%=fromPage%>" value="<%=fromPage %>"/>
					<label style="font-weight: bold">Search Skill: </label>
					<s:select list="skillList" name="strSkills" id="strSkills" listKey="skillsId" listValue="skillsName" cssStyle="width: 350px;" 
						multiple="true" cssClass="chosen-select-no-results" /> 
					<s:submit value="Search" cssClass="btn btn-primary"></s:submit>
					
				</s:form>
				<div style="margin-top: 40px;"></div>
				<%
		      	//int i=0;
		      	for(int a=0; empIdList != null && !empIdList.isEmpty() && a< empIdList.size(); a++) {
		      		Map<String, String> hmEmpProfile = hmEmpProfile1.get(empIdList.get(a));
		      		List<List<String>> hmSkills = hmEmpSkills.get(empIdList.get(a));
		      		Map hmEAttributeData1 = hmEAttributeData.get(empIdList.get(a));
		      		
		      		Map<String, List<List<String>>> hmElementAttribute = (Map<String, List<List<String>>>) hmEAttributeData1.get(empIdList.get(a)+"_EA");
		      		Map<String, String> hmScoreAggregateMap = (Map<String, String>) hmEAttributeData1.get(empIdList.get(a)+"_EASA");
		      		
		      		//System.out.println("hmElementAttribute --->> " +hmElementAttribute);
		      		//System.out.println("hmScoreAggregateMap --->> " +hmScoreAggregateMap);
		      		Map<String, String> skillwiseAvgRating = empSkillAvgRating.get(empIdList.get(a));
		      	%>
				
				
					<div class="col-lg-3 col-md-4 col-sm-6">
						<div class="box box-widget widget-user-2">
			            <!-- Add the bg color to the header using any of the bg-* classes -->
			            <div class="widget-user-header bg-yellow" style="background-color: #006F8A !important;color: #fff !important;">
			              <div class="widget-user-image">
			              	<%if(docRetriveLocation==null) { %>
							<img class="img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>" />
							<% } else { %>
							<img class="img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empIdList.get(a)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>" />
							<% } %>
			              </div>
			              <!-- /.widget-user-image -->
			              <h3 class="widget-user-username" title="<%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%>"><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></h3>
			              <h3 class="widget-user-username" title="<%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%>">[<%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%>]</h3>
			              <h5 class="widget-user-desc" title="<%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%>"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%></h5>
			              <h5 class="widget-user-desc" title="<%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%>">[<%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%>]</h5>
			            </div>
			            <div class="box-footer no-padding">
			              <ul class="nav nav-stacked" style="height: 165px;overflow-y: auto;">
			                <li><a href="javascript:void(0)">Date of Joining: <span class="pull-right badge bg-blue"><%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></span></a></li>
			                <li><a href="javascript:void(0)">Reporting Manager: <span class="pull-right badge bg-aqua"><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></span></a></li>
			                <%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
			                	<li><a href="javascript:void(0)">Overall: <span class="pull-right" id="skillPrimaryAttrib<%=empIdList.get(a)%>"></span></a>
				                <script type="text/javascript">
									$('#default').raty();
									<%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
										double dblScorePrimary = 0, aggregeteMarks = 0, totAllAttribMarks = 0;
										int count = 0;
							
										for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
											List<String> innerList = elementouterList.get(i);
											if(hmElementAttribute != null) {
												List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
												for (int j = 0; attributeouterList1 != null && j < attributeouterList1.size(); j++) {
													List<String> attributeList1 = attributeouterList1.get(j);
													totAllAttribMarks += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
													count++;
												}
											}
										}
										aggregeteMarks = totAllAttribMarks / count;
							
										dblScorePrimary = aggregeteMarks / 20; 
									%>
										$('#skillPrimaryAttrib<%=empIdList.get(a)%>').raty({
											  readOnly: true,
											  start:    <%=dblScorePrimary%>,
											  half: true
											});
									<% } %>
								</script></li>
			                <% } %>
			                <%
      							//System.out.println("hmSkills 1 --->> " +hmSkills);
      							for(int b=0; hmSkills != null && !hmSkills.isEmpty() && b<hmSkills.size(); b++) {
      								List<String> innerList = hmSkills.get(b);
      								//System.out.println("innerList --->> " +innerList);
      								//System.out.println("skillwiseAvgRating --->> " +skillwiseAvgRating);
      								String skillValue = "0";
      								double skillrate = 0.0d;
      								String ratingBy = "(Self Rated)";
      								if(skillwiseAvgRating != null && uF.parseToDouble(skillwiseAvgRating.get(innerList.get(0))) > 0) {
      									skillrate = uF.parseToDouble(skillwiseAvgRating.get(innerList.get(0))) / 20;
      									ratingBy = "";
      								} else {
      									skillrate = uF.parseToDouble(innerList.get(2)) / 2;
      								}
      							%>
							

							<li><a href="javascript:void(0)" class="ratings" title="<%=innerList.get(1) %><%=ratingBy %>"><%=innerList.get(1) %><%=ratingBy %><span class="pull-right" id="skill<%=a+"_"+b%>"></span></a>
							<script type="text/javascript">
								$('#skill<%=a+"_"+b%>').raty({
									readOnly : true,
									start :	<%=skillrate %>,
									half : true
								});
							</script></li>
							<% } %>
			              </ul>
			            </div>
			          </div>
					</div>
				
				<%
	      	} %>
	      	</div>
		<% if(empIdList == null || empIdList.size()==0){
    	  %>
				<div class="nodata msg"> <span>Sorry , No matching employee found</span> </div>
		<% } %>
		
		<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
			</div>
		</div>
	</div>
	</section>
</div>
</section>
<%} %>
<g:compress>
	<script type="text/javascript">
		$(function(){
			$("#strSkills").multiselect().multiselectfilter();
		});
		var config = {
			'.chosen-select' : {},
			'.chosen-select-deselect' : {
				allow_single_deselect : true
			},
			'.chosen-select-no-single' : {
				disable_search_threshold : 10
			},
			'.chosen-select-no-results' : {
				no_results_text : 'Oops, nothing found!'
			},
			'.chosen-select-width' : {
				width : "95%"
			}
		}
		for ( var selector in config) {
			$(selector).chosen(config[selector]);
		}
	</script>

	<script>
		//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
		$("img.lazy").lazyload({
			event : "sporty",
			threshold : 200,
			effect : "fadeIn",
			failure_limit : 10
		});

		$(window).bind("load", function() {
			var timeout = setTimeout(function() {
				$("img.lazy").trigger("sporty")
			}, 1000);
		});
	</script>
	
	<script>
	 <%if(fromPage != null && fromPage.equals("TS")) {%>
		 $("#formID").submit(function(event){
				event.preventDefault();
				var from = '<%=fromPage%>';
				//alert("from==>"+from);
				var skills = getSelectedValue("strSkills");
				var form_data = $("#formID").serialize();
					$.ajax({
						type :'POST',
						url  :'SearchEmployeeSkills.action?skills='+skills+'&fromPage='+from,
						data :form_data,
						cache:true,
						success : function(result) {
							$("#divResult").html(result);
						}
					});
			});
	     
	 <%}%>
	 
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
</g:compress>