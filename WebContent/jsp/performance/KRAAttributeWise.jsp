<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">

function addKRA(){
	  
	  var dialogEdit = '#addkraid';
		
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 400,
			width :  600,
						
			modal : true,
			title : 'Add KRA',
			open : function() {
				var xhr = $.ajax({
					url : "AddKRADetails.action?operation=A&type=attribute",
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
	  
	  var dialogEdit = '#editkraid';
		
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 400,
			width :  600,
						
			modal : true,
			title : 'Edit KRA',
			open : function() {
				var xhr = $.ajax({
					url : "AddKRADetails.action?operation=E&kraid="+kraid+"&type=attribute",
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
List<List<String>> attributeOuterList=(List<List<String>>)request.getAttribute("attributeOuterList");
Map<String, List<List<String>>> hmAttributeWiseKRA=(Map<String, List<List<String>>>)request.getAttribute("hmAttributeWiseKRA");
Map<String, String> hmLevel=(Map<String, String>)request.getAttribute("hmLevel");
%> 
 


<div id="printDiv" class="leftbox reportWidth">

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

<div style="float: right;"><a href="javascript:void(0)" onclick="window.location='KRADetails.action?type='">view level wise</a></div>
		
<div style="float:left; margin:0px 0px 10px 0px"> <a href="javascript:void(0)" onclick="addKRA()"> + Add New KRA</a></div>
<div class="clr"></div>

	<div>
         <ul class="level_list">
			<%
				for(int i=0;attributeOuterList!=null && !attributeOuterList.isEmpty() && i<attributeOuterList.size(); i++){
					List<String> attributeList=attributeOuterList.get(i);
			%>
				<li>
				<strong><%=attributeList.get(1) %></strong>
				<ul>
				<%
					List<List<String>> outerList=hmAttributeWiseKRA.get(attributeList.get(0));
					if(outerList!=null && !outerList.isEmpty()){
						
						for(int j=0; j<outerList.size();j++){
							List<String> innerList=outerList.get(j);
					
				%>
					<li>
						<strong><%=hmLevel.get(innerList.get(4)) %></strong>
						<ul>
							<li>
								<p style="font-weight: normal; font-size: 10px;margin-left: 40px; line-height: 14px; width: 81%;">
								<a href="AddKRADetails.action?operation=D&kraid=<%=innerList.get(0) %>&type=attribute" onclick="return confirm('Are you sure you wish to delete this?')" class="del"> Delete Level</a>
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
