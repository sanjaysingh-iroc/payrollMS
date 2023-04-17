<%@page import="javax.swing.Icon"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="KRA" name="title" />
</jsp:include>
<%
Map<String, String> hmEmpList = (Map<String, String>) request.getAttribute("hmEmpList");
Map<String, String> hmMesures = (Map<String, String>) request.getAttribute("hmMesures");
Map<String, String> hmMesuresType = (Map<String, String>) request.getAttribute("hmMesuresType");

Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");

Map<String, List<List<String>>> hmGoalTitle = (Map<String, List<List<String>>>) request.getAttribute("hmGoalTitle");

Map<String, List<List<String>>> hmKRA =(Map<String, List<List<String>>>)request.getAttribute("hmKRA");

List<String> memberList = (List<String>) request.getAttribute("memberList");
Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");

String strUserTypeId = (String) session.getAttribute(IConstants.USERTYPEID);
//System.out.println("strUserTypeId"+ strUserTypeId);

String id=request.getParameter("id");
String empid=request.getParameter("empid");

Map<String, String> hmKRARating =(Map<String, String>)request.getAttribute("hmKRARating");

String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);

UtilityFunctions uf = new UtilityFunctions();
%>

<div class="leftbox reportWidth">
<%-- <s:hidden name="id"></s:hidden>
<s:hidden name="empid"></s:hidden> --%>
<%
			if(!hmKRA.isEmpty()){
			%>
	<s:form action="KRA" id="formID" method="POST" theme="simple">
	<s:hidden name="id"></s:hidden>
	<s:hidden name="empid"></s:hidden>
<%-- <input type="hidden" name="id" value="<%=id%>" />  
				<input type="hidden" name="empid" value="<%=empid%>" />  --%>
		<table class="tb_style" style="float: left; width: 100%;">
			<tr>
				<th style="width:60%;">KRA</th>
				<%
					for (int i = 0; i < memberList.size(); i++) {
				%>
				<th><%=orientationMemberMp.get(memberList.get(i))%></th>
				<%
					}
				%>
			</tr>
			<%
			Iterator<String> it = hmKRA.keySet().iterator();
			int count=0;
			while(it.hasNext()){
				String key = it.next();
				List<List<String>> outerList = hmKRA.get(key);
				for(int i=0;outerList!=null && i<outerList.size();i++){
					List<String> innerList=outerList.get(i);
			%>
			<tr>
				<td>
				<input type="hidden" name="goalid" value="<%=innerList.get(1)%>" />  
				<input type="hidden" name="goalkraid" value="<%=innerList.get(0)%>" /> 
				<input type="hidden" name="weightage" value="<%=innerList.get(9)%>" /> 
				<span style="font-size: 12px;"><%=innerList.get(7)%></span>&nbsp;
				</td>
				<%
					for (int j = 0; j < memberList.size(); j++) {
						boolean flag=false;
						String usertypeid=null;
						/* System.out.println("memberList.get(j) "+ memberList.get(j)); */
						if (strUserTypeId != null && strUserTypeId.equals(memberList.get(j))) {
							flag=true;
							usertypeid=strUserTypeId;
						}
						//System.out.println("flag====>"+flag);
						
						String KraRating=hmKRARating.get(innerList.get(1)+innerList.get(0)+empid+id+strSessionEmpId+memberList.get(j));
						//System.out.println("selfKraData===>"+selfKraData);
						String kra_rating_id=null;
						String krarating=null;
						if(KraRating!=null && !KraRating.equals("")){
							String[] tempselfKraData=KraRating.split(":_:");
							kra_rating_id=tempselfKraData[0];
							krarating=tempselfKraData[1];
						}
						
				%>				
				<td align="right">
				<%if(flag){ %>
				<input type="hidden" name="usertype" value="<%=usertypeid%>" />
				<input type="hidden" name="kra_rating_id" value="<%=kra_rating_id%>" />
				<%} %>
				
				<div <%if(!flag){ %> style="opacity: 0.4; filter: alpha(opacity = 40);" <%} %>>
				<%-- <input type="hidden" name="<%=orientationMemberMp.get(memberList.get(j))%>" value="<%=memberList.get(j)%>" /> --%>
				<div id="starPrimary<%=i+"_"+j %>"></div> 
				<input type="hidden" id="gradewithrating<%=i+"_"+j%>" name="gradewithrating<%=memberList.get(j)+"_"+innerList.get(0)%>" /> 
				<script type="text/javascript">
											        $(function() {
											        	$('#starPrimary<%=i+"_"+j%>').raty({
											        		readOnly: <%if(flag){ %>false<%}else{%>true<%}%>,
											        		start: <%=krarating != null ? uf.parseToDouble(krarating) / 20 + "" : "0"%>,
											        		half: true,
											        		targetType: 'number',
											        		click: function(score, evt) {
											        			
											        			$('#gradewithrating<%=i+"_"+j%>').val(score);
											        			}
											        	});
											        	});
											        </script>
											        </div>
				
				</td>
				<%
				count++;} %>
			</tr>
			<%
			}
				}%>
		</table>

		<div>
			<s:submit value="Submit" cssClass="input_button" name="submit"></s:submit>

		</div>		
	</s:form>
	<%
			}else{
		%>
		<div class="nodata msg">No KRA assigned to you</div>
		<%
			}
		%>
</div>