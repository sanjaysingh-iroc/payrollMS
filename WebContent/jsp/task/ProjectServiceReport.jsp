<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<style>
.level_list>li{
padding-top: 5px;
padding-bottom: 5px;
border-bottom: 1px solid rgb(240, 240, 240);
}
</style>
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
});

hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';

function addService(userscreen, navigationId, toPage) { 
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Service');
	$.ajax({
		url : 'AddTaskService.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editService(strServiceId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Service');
	$.ajax({ 
		url : 'AddTaskService.action?operation=E&ID='+strServiceId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addSkill(strServiceId, userscreen, navigationId, toPage) { 
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Skill');
	$.ajax({
		url : 'AddTaskSkill.action?service_porject_id='+strServiceId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editSkill(strServiceSkillId, strSkillId, strServiceId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Skill');
	$.ajax({ 
		url : 'AddTaskSkill.action?operation=E&ID='+strServiceSkillId+'&strSkillId='+strSkillId+'&strServiceId='+strServiceId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addPredefinedTask(strServiceId, userscreen, navigationId, toPage) { 
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Task');
	$.ajax({
		url : 'AddPredefinedTaskForService.action?strServiceId='+strServiceId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editPredefinedTask(strServiceTaskId, strServiceId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Task');
	$.ajax({
		url : 'AddPredefinedTaskForService.action?operation=E&strServiceTaskId='+strServiceTaskId+'&strServiceId='+strServiceId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

		
function addRate(strServiceId, strSkillId, userscreen, navigationId, toPage) { 
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Rate');
	$.ajax({
		url : 'AddProjectRate.action?service_porject_id='+strServiceId +'&skillId='+strSkillId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editRate(strRateId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Rate');
	$.ajax({ 
		url : 'AddProjectRate.action?operation=E&ID='+strRateId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


</script>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	Map hmProjectServiceMap = (java.util.Map)request.getAttribute("hmProjectServiceMap");
	Map<String, List<List<String>>> hmProjectSkillMap = (Map<String, List<List<String>>>)request.getAttribute("hmProjectSkillMap");
	if(hmProjectSkillMap==null) hmProjectSkillMap = new HashMap<String, List<List<String>>>();
	
	Map<String, List<List<String>>> hmServiceTasksMap = (Map<String, List<List<String>>>)request.getAttribute("hmServiceTasksMap");
	if(hmServiceTasksMap==null) hmServiceTasksMap = new HashMap<String, List<List<String>>>();
	
	Map<String, List<List<String>>> hProjectRateMap = (Map<String, List<List<String>>>)request.getAttribute("hProjectRateMap");
	
	//out.println("hmLevelMap==>"+hmLevelMap);
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
%>

 
<div class="box-body">

<%=uF.showData((String) session.getAttribute("MESSAGE"), "") %>

	<div class="col-md-12">
		<a href="javascript:void(0)" onclick="addService('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Service</a>
	</div>

	<div class="col-md-12">
         <ul class="level_list">
		<% 
			Set<String> setLevelMap = hmProjectServiceMap.keySet();
			Iterator<String> it = setLevelMap.iterator();
			
			while(it.hasNext()) {
				String strServiceId = (String)it.next();
				List<String> alLevel = (List<String>)hmProjectServiceMap.get(strServiceId);
				if(alLevel==null)alLevel = new ArrayList<String>();
					
				List<List<String>> preTaskList = hmServiceTasksMap.get(strServiceId);
				if(preTaskList==null)preTaskList = new ArrayList<List<String>>();
				
					List<List<String>> skillList = (List<List<String>>)hmProjectSkillMap.get(strServiceId);
					if(skillList==null)skillList = new ArrayList<List<String>>();
			%>
					
					<li>
                    <a href="AddTaskService.action?operation=D&ID=<%=strServiceId%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" title="Delete Service" onclick="return confirm('Are you sure you wish to delete this service?')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
                    <a href="javascript:void(0)" class="edit_lvl" onclick="editService('<%=strServiceId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                    <strong><%=alLevel.get(1)%> [<%=alLevel.get(2)%>]</strong> 
					<ul>
					<li class="addnew desgn">
						<a href="javascript:void(0)" onclick="addPredefinedTask('<%=strServiceId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Task</a>
					</li>
					<%
						for(int d=0; preTaskList!= null && d<preTaskList.size(); d++){
						List<String> innerList = preTaskList.get(d);
						//System.out.println("skillInnerList.get(1)) --------===>> " + skillInnerList.get(1));
					%>
                    <li class="desgn">
	                    <a href="AddPredefinedTaskForService.action?operation=D&strServiceTaskId=<%=innerList.get(0)%>&strServiceId=<%=strServiceId %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" title="Delete Skill" onclick="return confirm('Are you sure, you wish to delete this task?')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
						<a href="javascript:void(0)" onclick="editPredefinedTask('<%=innerList.get(0)%>','<%=strServiceId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>  
	                    <strong>Task Name:</strong> <%=innerList.get(1)%> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <strong>Task Description:</strong> <%=innerList.get(2)%>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;                 
					</li>
					<% } %>
						
						
					
					
					<li class="addnew desgn">
						<a href="javascript:void(0)" onclick="addSkill('<%=strServiceId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Skill</a>
					</li>
					<li class="desgn">
					<%
						for(int d=0; skillList!= null && d<skillList.size(); d++){
						List<String> skillInnerList = skillList.get(d);
						
						//System.out.println("skillInnerList.get(1)) --------===>> " + skillInnerList.get(1));
						List<List<String>> skillRateList = (List<List<String>>)hProjectRateMap.get(strServiceId+"_"+skillInnerList.get(1));
						if(skillRateList == null)skillRateList = new ArrayList<List<String>>();
						//System.out.println("skillRateList ------===>> " + skillRateList);
					%>
                    
                    <a href="AddTaskSkill.action?operation=D&ID=<%=skillInnerList.get(0)%>&strSkillId=<%=skillInnerList.get(1)%>&strServiceId=<%=strServiceId %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" title="Delete Skill" onclick="return confirm('Are you sure you wish to delete this skill?')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
					<a href="javascript:void(0)" onclick="editSkill('<%=skillInnerList.get(0)%>','<%=skillInnerList.get(1)%>','<%=strServiceId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>  
                    <strong>Skill Name:</strong> <%=skillInnerList.get(2)%> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <strong>Skill Description:</strong> <%=skillInnerList.get(3)%>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;                 
					<ul>
					<li class="addnew desgn"> 
						<a href="javascript:void(0)" onclick="addRate('<%=strServiceId %>','<%=skillInnerList.get(1) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add Skill Rate</a>
					</li>
						<%
						for(int g=0; skillRateList != null && g<skillRateList.size(); g++) {
							List<String> innerList = skillRateList.get(g);
							%>
							
							<li>
                            <a href="AddProjectRate.action?operation=D&ID=<%=innerList.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" title="Delete Skill Rate" onclick="return confirm('Are you sure you wish to delete this rate?')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
                            <a href="javascript:void(0)" class="edit_lvl" onclick="editRate('<%=innerList.get(0) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> 
                            
                            <strong>Level:</strong> <%=innerList.get(2)%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
                            <strong>Location:</strong> <%=innerList.get(1)%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <strong>Rate/Month:</strong> <%=innerList.get(6)%> <%=innerList.get(5)%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <strong>Rate/Day:</strong> <%=innerList.get(6)%> <%=innerList.get(3)%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <strong>Rate/Hour:</strong> <%=innerList.get(6)%> <%=innerList.get(4)%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
                             
                            </li>	
						<% } %>
						</ul>
					
					<% } %>
						</li>
					</ul>
                 </li> 
			<% } %>
		 </ul>
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

