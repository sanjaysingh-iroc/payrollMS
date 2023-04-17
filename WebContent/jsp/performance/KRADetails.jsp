<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">

function addKRA(){
	removeLoadingDiv('the_div');
	  var dialogEdit = '#addkraid';
	  dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 450,
			width :  700,
						
			modal : true,
			title : 'Add KRA',
			open : function() {
				var xhr = $.ajax({
					url : "AddKRADetails.action?operation=A",
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

function editKRA(kraid){
	removeLoadingDiv('the_div');
	  var dialogEdit = '#editkraid';
	  dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 450,
			width :  700,
						
			modal : true,
			title : 'Edit KRA',
			open : function() {
				var xhr = $.ajax({
					url : "AddKRADetails.action?operation=E&kraid="+kraid+"&type=",
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
	<jsp:param value="Key Responsibility Area" name="title"/>
</jsp:include>

<% 
UtilityFunctions uF=new UtilityFunctions();
List<List<String>> levelOuterList=(List<List<String>>)request.getAttribute("levelOuterList");
Map<String, List<List<String>>> hmLevelWiseKRA=(Map<String, List<List<String>>>)request.getAttribute("hmLevelWiseKRA");
Map<String, String> hmAttribute=(Map<String, String>)request.getAttribute("hmAttribute");
%> 
 


<div id="printDiv" class="leftbox reportWidth">

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

<div style="float: right;"><a	href="javascript:void(0)" onclick="window.location='KRADetails.action?type=attribute'">view Attribute wise</a></div>
		
<div style="float:left; margin:0px 0px 10px 0px"> <a href="javascript:void(0)" onclick="addKRA()"> + Add New KRA</a></div>
<div class="clr"></div>

	<div>
         <ul class="level_list">
			<%
				for(int i=0;levelOuterList!=null && !levelOuterList.isEmpty() && i<levelOuterList.size(); i++){
					List<String> levelList=levelOuterList.get(i);
			%>
				<li>
				<strong><%="["+levelList.get(1)+"] "+levelList.get(2) %></strong>
				<ul>
				<%
					List<List<String>> outerList=hmLevelWiseKRA.get(levelList.get(0));
					if(outerList!=null && !outerList.isEmpty()){
						for(int j=0; j<outerList.size();j++){
							List<String> innerList=outerList.get(j);
				%>
					<li>
						<strong><%=hmAttribute.get(innerList.get(3)) %></strong>
						<ul>
							<li>
								<p style="font-weight: normal; font-size: 10px;margin-left: 40px; line-height: 14px; width: 81%;">
								<a href="AddKRADetails.action?operation=D&kraid=<%=innerList.get(0) %>&type=" onclick="return confirm('Are you sure you wish to delete this?')" class="del"> Delete Level</a>
								<a href="javascript:void(0)" onclick="editKRA('<%=innerList.get(0)%>');" class="edit_lvl">Edit Level</a>
									<b><%=innerList.get(1) %></b>
								</p>
								<p style="font-weight: normal; font-size: 10px;margin-left: 40px; line-height: 14px; width: 81%;"><%=innerList.get(2) %></p>
							</li>
						</ul>
						
						
					</li> 
					<%}
					}%>
				</ul>
				
				</li>
			<%} %>
		 
		 </ul>
         
     </div>	

		
	</div> 
<div id="addkraid"></div>
<div id="editkraid"></div>
