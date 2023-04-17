<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.itextpdf.text.Utilities"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ page buffer="16kb"%>
<%@ page import="java.util.*"%>
<%int proid=(Integer) request.getAttribute("pro_id"); %>

<script type="text/javascript">

hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';

</script>
 

<script language="javascript" type="text/javascript">   
         
	function start123(id) {
		var proid=document.getElementsByName("pro_id"); 
		
	var url='TaskUpdateTime.action?type=start&id=' + id;
	url+='&pro_id='+<%=proid%>;
		window.location = url;
		
	}
	function SendProId(id)
	{ 
		if(id!=null)
			{
		window.location ='EmpCompletedViewProject.action?pro_id='+id ;
			}
	}
	
	function viewDocuments(id) {

		var dialogEdit = '#viewdocuments';
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 500,
			width : 500,
			modal : true,
			title : 'Project Documents',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectDocumentView.action?pro_id="+id,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;

			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open');
	}
	function viewSummary(id) {

		var dialogEdit = '#viewsummary';
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 1120,
			width : 1200,
			modal : true,
			title : 'Project Summary',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectSummaryView.action?pro_id="+id,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;

			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open');
	}
	
	jQuery(document).ready(function() {
		
		//	jQuery("#frmClockEntries").validationEngine();
			
		  jQuery(".content1").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".heading_dash").click(function()
		  {
		    jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("close_div"); 
		  });
		});
		
</script>
<style>
.tb_style tr td {
	padding: 5px;
	border: solid 1px #c5c5c5;
}

.tb_style tr th {
	padding: 5px;
	border: solid 1px #c5c5c5;
	background: #efefef
}


ul li.desgn { padding:0px ; border:solid 1px #ccc}

.close_div
{
  cursor:pointer;
	text-shadow: 0 1px 0 #FFFFFF;
	background-image:url(images1/minus_sign.png);
	background-repeat:no-repeat;
	background-position:10px 6px;
	background-color:#efefef;
}

</style>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="My Project History" name="title" />
</jsp:include>

<%
	/*  List<String> projectlist = (List<String>) request.getAttribute("projectlist");
	List<Integer> projectidlist = (List<Integer>) request.getAttribute("projectidlist");
	List<Integer> index = (List<Integer>) request.getAttribute("index");
	Map<Integer, List<String>> al = (Map<Integer, List<String>>) request.getAttribute("al");
	Map<Integer, List<List<String>>> activityDetailMap = (Map<Integer, List<List<String>>>) request.getAttribute("activityDetailMap"); */
	UtilityFunctions uF = new UtilityFunctions();	
%>
<div class="leftbox reportWidth">
	<div style="width: 100%;text-align:center"> <%=uF.showData((String)session.getAttribute("MESSAGE"), "") %></div>

<%

Map hmTasks = (java.util.Map)request.getAttribute("hmTasks");
Map hmProject = (java.util.Map)request.getAttribute("hmProject");
Map hmActivities = (java.util.Map)request.getAttribute("hmActivities");

%>


<!-- <div style="float:left; margin:10px 0px 0px 0px"> <a href="AddLevel.action"  class="add_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax' })">Add Project</a></div> -->



	<s:form action="EmpCompletedViewProject.action" name="frm_empproject_view" method="post" theme="simple">

		<div class="filter_div">
		<div class="filter_caption">Filter</div>
			<s:select name="pro_id"
							listKey="projectID" headerKey="" headerValue="Select Project"
							listValue="projectName" list="projectdetailslist" key="" onchange="SendProId(this.value);"/>
		</div>
		
<div style="float: right; width: auto;"> <%=uF.showData((String)request.getAttribute("MESSAGE"), "") %></div>
		
		
		<%if(hmTasks!=null && hmTasks.size()>0){ %>
         <div style="margin-left:30px;float:left">
			<!-- <a onclick="viewSummary(0)" href="javascript:void(0)">View Task Summary</a> -->
		</div>
         <%} %>
         


		
  
<div class="clr"></div>
<div style="margin:10px 0px 0px 0px ;float:left; width:100%">
         <ul class="level_list">

		
		<% 
		Set setTaskMap = hmTasks.keySet();
		Iterator itTask = setTaskMap.iterator();
			
			while(itTask.hasNext()){
				String strProjectId = (String)itTask.next();
				List alTasks = (List)hmTasks.get(strProjectId);
				if(alTasks==null)alTasks=new ArrayList();				
				
				for(int d=0; d<alTasks.size(); d+=10){
					String strTaskId = (String)alTasks.get(d);
							
					%>
					
					<li>
					
					<%
						String strColour = null;
						if(uF.parseToInt((String)alTasks.get(d+4))==2){
							strColour = "red";
						}else if(uF.parseToInt((String)alTasks.get(d+4))==1){
							strColour = "yellow";
						}else if(uF.parseToInt((String)alTasks.get(d+4))==0){
							strColour = "green";
						}else{
							strColour = "";
						}
					
					%>
				
					<div style="float:left">
					<p>
                   <%=alTasks.get(d+1)%>
                    <%-- <%=alTasks.get(d+2)%> --%>
                    Task Name: <strong><%=alTasks.get(d+3)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <span style="padding:3px;background-color:<%=strColour%>">Priority: <strong><%=alTasks.get(d+4)%></strong></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    Assigned To: <strong><%=alTasks.get(d+5)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    Worked: <strong><%=alTasks.get(d+8)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		             </p>       
		             <p style="margin:0px 0px 0px 50px">
                    Deadline: <strong><%=alTasks.get(d+6)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    Estimated Time: <strong><%=alTasks.get(d+7)%> hrs</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    
                    Completion Status: <strong><%=alTasks.get(d+9)%> %</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              		</p>      
              		</div>
              		
              		<div style="float:right">
                    	<a class="viewdocticon" href="javascript:void(0)" onclick="viewDocuments(<%=strProjectId%>)" title="View Project Documents">View Project Documents</a>
                    </div>
                    <div class="clr"></div>
                                  <%String per=(String)alTasks.get(d+9); 
                                  double percent=uF.parseToDouble(per);
                                  %>
                    
					
						<ul>
					
					
							<li class="desgn">
							<p class="past heading_dash" style="text-align:left;padding-left:35px;">Task List ( click to expand )</p>
					   		<div class="content1">
					   
								<ul>   
								
								<%
								
								List alActivities = (List)hmActivities.get(strTaskId);
								if(alActivities==null)alActivities = new ArrayList();
								boolean isEnd = false;
								for(int i=0; i<alActivities.size(); i+=5){ %>
									
									<%-- <a href="AddDesig.action?operation=D&ID=<%=alActivities.get(i)%>" class="del" title="Delete Task" onclick="return confirm('Are you sure you wish to delete this Task?')"> - </a>
									<a href="AddDesig.action?operation=E&ID=<%=alActivities.get(i)%>" class="edit_lvl"  title="Edit Task" 
				                    onclick="return hs.htmlExpand(this, { objectType: 'ajax' })">Edit</a> --%> 
				                
				                <li>
				                
				                
				                
				                    Date: <strong><%=alActivities.get(i+1)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				                    Start Time: <strong><%=alActivities.get(i+2)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				                    End Time: <strong><%=uF.showData((String)alActivities.get(i+3), " working ")%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				                    Total Time: <strong><%=alActivities.get(i+4)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				                    
				                    
				                    <%
				                   if(percent<100){
				                    if(alActivities.get(i+3)==null){ isEnd = true;%>
				                    	<a onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })" href="EndTaskPopup.action?id=<%=strTaskId%>&pro_id=<%=strProjectId%>">End this activity</a>
				                    <%}%>
                    
                    			</li>
                    			
                    			
								<%} 
								}%>
								
									<%
									 if(percent<100){
										if(!isEnd){ %>
	                    					<li><a href="javascript:void(0)" onclick="start123(<%=strTaskId%>);">Start this task</a></li>
									   	<%}
									} 
								   	
								   	%>
                                </ul>
                                </div>
                                
                            </li>
                            </ul>  
                                       
					</li>
				<%
					
				}
				
			}
				%>	
							</ul>
		
         
         
         <%if(hmTasks!=null && hmTasks.size()==0){ %>
         <div style="width: 100%;text-align:center"><div class="msg nodata"><span>No task has been completed for the current selection.</span></div></div>
         <%} %>
         
     </div>	

		<!-- <center>
			<input type="submit" value="Submit" class="input_button">
		</center>  -->
	
	</s:form> 
	
	
	
	
	
	
</div>


<div id="viewsummary"></div>
<div id="viewdocuments"></div>
<%session.setAttribute("MESSAGE", null);%>