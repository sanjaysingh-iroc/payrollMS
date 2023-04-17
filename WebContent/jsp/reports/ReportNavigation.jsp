<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<link rel="stylesheet" type="text/css" media="screen" href="<%= request.getContextPath()%>/css/jquery_treeview.css">
<script src="<%= request.getContextPath()%>/js/jquery.treeview.js" type="text/javascript"></script>


<script type="text/javascript">
		$(function() {
			$("#tree").treeview({
				collapsed: false, 
				animated: "fast",
				control:"#sidetreecontrol",
				prerendered: true,
				persist: "location"
			});
			
			 $(".report_trigger").click(function(){
						   $(".report_panel").toggle("fast");
							  //  $(this).toggleClass("active");
								return false;
							});
			
			
		})
		
	</script>
	
<style>
     .report_panel { 
                position:fixed ; left:0; top:55%; background:#ccc; padding:15px 20px 15px 15px ; display:none ;
                background: #fff;
	            border:1px solid #ccc;
				width: auto;
				height: auto;
             } 

.report_trigger { 
	position:fixed;
	left:0;  
	top:50%; 
	background:#EC8E00; 
	padding:5px ; 
	color:#FFFFFF; 
	text-decoration:none; 
	font-weight:bold
}



</style>



<%
UtilityFunctions uF = new UtilityFunctions();
/* List alParentNavL = (List) session.getAttribute("alParentNavL");
Map hmChildNavL = (Map) session.getAttribute("hmChildNavL");

List alParentNavR = (List) session.getAttribute("alParentNavR");
Map hmChildNavR = (Map) session.getAttribute("hmChildNavR");
Map hmNavigation = (Map) session.getAttribute("hmNavigation");
Map hmNavigationParent = (Map) session.getAttribute("hmNavigationParent"); 
 */
List alParentNavR = (List) session.getAttribute("alParentNavL");
Map hmChildNavR = (Map) session.getAttribute("hmChildNavL");
Map hmNavigation = (Map) session.getAttribute("hmNavigation");
Map hmNavigationParent = (Map) session.getAttribute("hmNavigationParent"); 

%> 




           <!-- <ul class="treeview" id="tree">

	<li class="expandable"><div class="hitarea expandable-hitarea"></div><a href="?/index.cfm">Home</a>
	<ul style="display: none;">
		<li><a href="?/enewsletters/index.cfm">Airdrie eNewsletters </a></li>
		<li><a href="?/index.cfm">Airdrie Directories</a></li>
		<li><a href="?/economic_development/video/index.cfm">Airdrie Life Video</a></li>

		<li><a href="?/index.cfm">Airdrie News</a></li>

		<li><a href="?/index.cfm">Airdrie Quick Links</a></li>
		
	</ul>
	</li>
	<li class="expandable"><div class="hitarea expandable-hitarea"></div><span>City Services</span>
	<ul style="display: none;">
		<li class="expandable"><div class="hitarea expandable-hitarea"></div><a href="?/assessment/index.cfm">Assessment</a>
		<ul style="display: none;">
			<li><a href="?/assessment/assessment_faqs.cfm">Assessment FAQs</a></li>

			<li><a href="?/assessment/property_assessment_notices.cfm">2007 Property Assessment Notices</a></li>
			<li><a href="?http://www.creb.com/" target="_blank">CREB</a></li>
			<li><a href="?/assessment/non_residential_assessment_tax_comparisons.cfm">Non-Residential Assessment / Tax Comparisons</a></li>
			<li><a href="?/assessment/how_to_file_a_complaint.cfm">How to File a Complaint</a></li>
			<li class="last"><a href="?/assessment/supplementary_assessment_tax.cfm">Supplementary Assessment and Tax</a></li>
		</ul>

		</li>
		<li class="expandable"><div class="hitarea expandable-hitarea"></div><a href="?/building_development/index.cfm">Building &amp; Development </a>
		<ul style="display: none;">
			<li ><a href="?/building_inspections/index.cfm">Building Inspections</a></li>
			<li><a title="City Infrastructure" href="?/building_development/city_infrastructure/index.cfm">City Infrastructure</a></li>
	       	<li><a title="Commercial/Industrial Development" href="?/building_development/commercial_industrial_development/index.cfm">Commercial / Industrial / Multi-Family Development</a></li>
			<li><a title="Residential Development" href="?/building_development/residential_development/index.cfm">Residential Development</a></li>
		</ul>
		</li>
		<li class="expandable"><div class="hitarea expandable-hitarea"></div><a href="?/community_safety/index.cfm">Community Safety</a>
		<ul style="display: none;">
			<li><a href="?/disaster_services/index.cfm">Disaster Services</a></li>
			<li><a href="?/emergency_services/index.cfm">Emergency Services</a></li>
			<li><a href="?/municipal_enforcement/index.cfm">Municipal Enforcement</a></li>
			<li class="expandable lastExpandable"><div class="hitarea expandable-hitarea lastExpandable-hitarea"></div><a href="?/rcmp/index.cfm">Royal Canadian Mounted Police</a>
			</li>
		</ul>
		</li>
		
		<li class="expandable"><div class="hitarea expandable-hitarea"></div><a href="?/community_services/index.cfm">Community Services</a>
		</li>
		<li><a href="?/engineering/index.cfm">Engineering Services </a></li>
		<li class="expandable"><div class="hitarea expandable-hitarea"></div><a href="?/recycling_waste/index.cfm">Recycling, Waste &amp; Composting</a>
		<ul style="display: none;">
			<li class="last"><a href="?/environmental_services/index.cfm">Environmental Services </a></li>

		</ul>
		</li>
	
		<li class="last"><a href="?/utilities/index.cfm">Water &amp; Sewer (Utilities)</a></li>

	</ul>
	</li>

	<li class="expandable"><div class="hitarea expandable-hitarea"></div><span>News</span>
	<ul style="display: none;">
		<li class="expandable"><div class="hitarea expandable-hitarea"></div><a href="?/enewsletters/index.cfm">Airdrie eNewsletters</a>
		</li>
		<li><a href="?/calendars/index.cfm">Community Calendar</a></li>
		<li><a href="?/community_news/index.cfm">Community News</a></li>

		<li><a href="?/news_release/index.cfm">News Releases</a>
		</li>
		<li><a href="?/building_development/planning/notice_of_development/notice_of_development.cfm">Notice of Development </a></li>
		<li><a href="?/photogallery/index.cfm">Photo Gallery</a></li>
		<li class="expandable"><div class="hitarea expandable-hitarea"></div><a href="?/public_meetings/index.cfm">Public Meetings</a>

		<ul style="display: none;">
			<li><a href="?/public_meetings/appeals/index.cfm">Appeals</a></li>

			<li><a href="?/public_meetings/open_houses/index.cfm">Open Houses</a></li>
			<li class="last"><a href="?/public_meetings/public_hearings/index.cfm">Public Hearings</a></li>
		</ul>
		</li>
		<li class="expandable lastExpandable"><div class="hitarea expandable-hitarea lastExpandable-hitarea"></div><a href="?/publications/index.cfm">Publications</a>

		<ul style="display: none;">
			<li><a href="?/publications/pdf/AirdrieLIFE_fall2006.pdf">Airdrie Life Magazine</a> (16MB, .PDF)</li>

			<li><a href="?/publications/pdf/report_for_2005.pdf">Annual Economic Report</a> (5 MB, .PDF)</li>
			<li class="last"><a href="?/publications/pdf/Airdrie%20community%20report%20for%202006_sm.pdf">Annual Community Report</a></li>
		</ul>

		</li>
	</ul>
	</li>
		
	<li class="last"><a href="?https://vch.airdrie.ca/index.cfm">Online Services</a></li>

</ul> -->




<ul class="treeview" id="tree">

<%
 List alList = (List) hmChildNavR.get("55");
 if(alList==null)alList=new ArrayList();
 for(int i=0; i<alList.size(); i++){
 	Navigation nav = (Navigation)alList.get(i);
 	%>
 	
	
	<%if(uF.parseToInt(nav.getStrChild())==0 && uF.parseToInt(nav.getStrParent())==0){ %>
		<li class="last"><a href="<%=nav.getStrAction()%>"><%=nav.getStrLabel() %></a></li>
	<%}else if(uF.parseToInt(nav.getStrChild())==0 && uF.parseToInt(nav.getStrParent())>0){ %>
		<li class="last"><a href="<%=nav.getStrAction()%>"><%=nav.getStrLabel() %></a></li>
	<%}else if(uF.parseToInt(nav.getStrChild())==1){ %>
		<li class="expandable"><div class="hitarea expandable-hitarea"></div><a href="<%=nav.getStrAction() %>"><%=nav.getStrLabel() %></a>
		<!-- <ul style="display: none;"> -->
	<ul>
			<%
			 	List alListR = (List) hmChildNavR.get(nav.getStrNavId());
			 	if(alListR==null)alListR=new ArrayList();
			 	for(int ic=0; ic<alListR.size(); ic++){ 		
			 		Navigation navC = (Navigation)alListR.get(ic);
			 		%>
			 		<li> <a href="<%=navC.getStrAction() %>"><%=navC.getStrLabel() %></a></li>
			 		<%
			 	}
			 	%>
			
		</ul>
		</li>
		
	<%} %>
 	<%
 }
 %>
 </ul>