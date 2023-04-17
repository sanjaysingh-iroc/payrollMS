<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<script type="text/javascript">
jQuery(document).ready(function() {	
		
	  jQuery(".content1").hide();
	  //toggle the componenet with class msg_body
	  jQuery(".heading_dash").click(function()
	  {
	    jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("close_div"); 
	  });
	});
	
	
function myGoal() { 

	document.getElementById("MyPersonalGoalid").innerHTML = ''; 
	
	
	var dialogEdit = '#MyPersonalGoalid';
	
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 700,
				width : 1100,
				modal : true,
				title : 'My Personal Goal',
				open : function() {
					var xhr = $.ajax({
						url : "MyGoalPopUp.action?operation=A",
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


function editGoal(goalid) { 

	document.getElementById("MyPersonalGoalid").innerHTML = '';
	
	
	var dialogEdit = '#MyPersonalGoalid';
	
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 700,
				width : 1100,
				modal : true,
				title : 'My Personal Goal',
				open : function() {
					var xhr = $.ajax({
						url : "EditMyPersonalGoalPopUp.action?operation=E&goalid="+goalid,
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
	
	
</script>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="My Goal" name="title" />
</jsp:include>
<%
	CommonFunctions CF= (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map<String, List<List<String>>> hmIndividual = (Map<String, List<List<String>>>) request.getAttribute("hmIndividual");
	UtilityFunctions uF=new UtilityFunctions();
	
	Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");
	
	Map<String, String> hmGoalType = (Map<String, String>)request.getAttribute("hmGoalType");
%>

<div class="leftbox reportWidth">
<div style="float: right; margin-bottom: 10px">
		<a href="javascript:void(0)"><input type="button"
			class="input_button" onclick="myGoal();"
			value="Add New Personal Goal"> </a>

</div>
<div class="clr"></div>
<%
if(!hmIndividual.isEmpty()){
Iterator<String> it=hmIndividual.keySet().iterator();
int ccount = 0;
while(it.hasNext()){
	String key = it.next();
	List<List<String>> outerList=hmIndividual.get(key);
	
	for(int i=0;outerList!=null && i<outerList.size();i++){
		List<String> innerList=outerList.get(i);
		ccount++;
%>
<div style="margin:10px 0px 0px 0px ;float:left; width:100%">
<p class="past heading_dash" style="text-align:left;padding-left:35px;">

<strong><%=ccount%>.</strong> <%=innerList.get(3)%> (<%=hmGoalType.get(innerList.get(1)) %>)
<%if(innerList.get(1)!=null && innerList.get(1).equals("5")){ %>
<span id="corporateEditId" style="float: right; margin-right: 1cm;">



				<%-- <a href="javascript:void(0)"
				onclick="goalChart('<%=innerList.get(0)%>')"  style="float:left;" title="Chart"><img src="images1/icons/org_icon.jpeg" height="24"/></a>&nbsp; --%>

				
				<a href="javascript:void(0)" class="edit_lvl"
				onclick="editGoal('<%=innerList.get(0)%>')" title="Edit Level">Edit</a>&nbsp;
				<a
				href="MyPersonalGoal.action?operation=D&goal_id=<%=innerList.get(0)%>"
				class="del" title="Delete Corporate Goal"
				onclick="return confirm('Are you sure you wish to delete this Goal?')">
					- </a> </span>
		<%} %>

</p>


					   <div class="content1">
         <ul class="level_list">
							
					<li>
					<%-- <div style="float: left; background-repeat: no-repeat; background-position: right top;">
					<p style="margin:0px 0px 0px 50px"><strong><%=innerList.get(3) %></strong></p>
					<p style="margin:0px 0px 0px 50px">						
	                    Objective:<strong> <%=innerList.get(4) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Attribute:<strong> <%=innerList.get(6) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  					</p>
  					<p style="margin:0px 0px 0px 50px">Description:<strong><%=innerList.get(5) %></strong></p>
                   	<p style="margin:0px 0px 0px 50px">                
	                    Measure Efforts Days & Hrs:<strong><%=innerList.get(9) %>&nbsp;Days&nbsp;<%=innerList.get(10) %>&nbsp;Hrs</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Due Date:<strong><%=innerList.get(16) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Weightage:<strong><%=innerList.get(19) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    				</p>
    				<p style="margin:0px 0px 0px 50px">                
	                    Assigned To:&nbsp;<strong><%=innerList.get(20) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;	                    
    				</p>      				 
    				             
                    </div> --%>
                    
                    <div style="float: right; width: 30%; padding: 0px;">
						<p style="font-size: 10px; padding-left: 42px; font-style: italic;">Last updated by <%=hmEmpName.get(innerList.get(22))%> on <%=innerList.get(21)%></p>
					</div>
															<div
																style="float: left; background-repeat: no-repeat; background-position: right top; width: 60%;">
																
																<p style="margin: 0px 0px 0px 0px">
																	Title:<strong> <%=innerList.get(3)%></strong>
																</p>
																<p style="margin: 0px 0px 0px 0px">
																	Objective:<strong> <%=innerList.get(4)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																</p>
															 	<p style="margin: 0px 0px 0px 0px">
																	Attribute:<strong> <%=innerList.get(6)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																</p>
																<p style="margin: 0px 0px 0px 0px">
																	Description:<strong> <%=innerList.get(5)%></strong>
																</p>
																<p style="margin: 0px 0px 0px 0px">
																	Priority:<strong> <%=innerList.get(29)%></strong>
																</p>
																<p style="margin: 0px 0px 0px 0px">
																	<%
																		if (innerList.get(7).equals("Effort")) {
																	%>
																	Measure Efforts Days & Hrs:<strong> <%=innerList.get(10)%>&nbsp;Days&nbsp;<%=innerList.get(11)%>&nbsp;Hrs</strong>
																	<%
																		} else if (innerList.get(7).equals("$")) {
																	%>
																	$:<strong> <%=innerList.get(8)%></strong>
																	<%
																		}
																	%>
																	</p>
																<p style="margin: 0px 0px 0px 0px">
																	Due Date:<strong><%=innerList.get(16)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																	Weightage:<strong> <%=innerList.get(19)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																</p>
																<p style="margin: 0px 0px 0px 0px">
																	Assigned To:&nbsp;<%-- <strong> <%=tinnerList.get(20)%></strong> --%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																</p>


															</div>
                    
                    
                    <div class="clr"></div>
                
                 </li>
		 
		 </ul>
         
     </div>
     </div>
     <%}} %>
     <%
		} else {
	%>
	<div class="nodata msg">No Goal assigned to you</div>
	<%
		}
	%>
</div>

<div id="MyPersonalGoalid"></div>
