
<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div id="printDiv" class="leftbox reportWidth">
	<%Map<String,Map<String,List<List<String>>>> alData=(Map<String,Map<String,List<List<String>>>> )request.getAttribute("alData");
    Map<String,List<String>> questionMp=(Map<String,List<String>> )request.getAttribute("questionMp");
    List<String> userList=(List<String> )request.getAttribute("userList");
    Map<String, String> hmEmpName =(Map<String, String> )request.getAttribute("hmEmpName");
    UtilityFunctions uF=new UtilityFunctions();
    System.out.println(" userList.size() "+userList.size());
    
    for(int i=0;i<userList.size();i++){
    	if(userList.get(i).equals("2")){ %>
	<h3>Manager block</h3>
	<%}else if(userList.get(i).equals("7")){%>
	<h3>HR block</h3>
	<%}else if(userList.get(i).equals("3")){%>
	<h3>Peer block</h3>
	<%}
    	Map<String,List<List<String>>> innerMap=alData.get(userList.get(i));
    	if(innerMap ==null){
    		System.out.println(" innerMap "+innerMap);
    		continue;
    	}
    	
    	Set<String> keys=innerMap.keySet();
        Iterator<String> it=keys.iterator();
    	
        while(it.hasNext()){
        	String key=it.next();  %>
	<h3><%=hmEmpName.get(key) %></h3>
	<%List<List<String>> outerList=innerMap.get(key); 
	if(outerList==null){
		continue;
	}
	%>

	<table>
		<%for(int j=0;j<outerList.size();j++){
        		List<String> innerList=outerList.get(j);
        		if(innerList==null){
        			continue;
        		}
        			
        		List<String> questionList=questionMp.get(innerList.get(2));
        		if(questionList==null){
        			continue;
        		}
        		%>
		
		

		<tr><td><%=questionList.get(0) %></td></tr>
		<tr>
		<%if(uF.parseToInt(questionList.get(6))==1){ %>
			<td><input type="radio" name="answer<%=j%>" disabled="disabled"
				<% if(innerList.get(1)!=null && innerList.get(1).contains("a") ){ %>
				checked <%} %> value="a" /><%=questionList.get(1)%></td>
			<td><input type="radio" name="answer<%=j%>" disabled="disabled"
				<% if(innerList.get(1)!=null && innerList.get(1).contains("b") ){ %>
				checked <%} %> value="b" /><%=questionList.get(2)%></td>
			<td><input type="radio" name="answer<%=j%>" disabled="disabled"
				<% if(innerList.get(1)!=null && innerList.get(1).contains("c") ){ %>
				checked <%} %> value="c" /><%=questionList.get(3)%></td>
			<td><input type="radio" name="answer<%=j%>" disabled="disabled"
				<% if(innerList.get(1)!=null && innerList.get(1).contains("d") ){ %>
				checked <%} %> value="d" /><%=questionList.get(4)%></td>
		
		<%}else if(uF.parseToInt(questionList.get(6))==2){ %>
		
			<td><input type="checkbox" name="answer<%=j%>" disabled="disabled"
				<% if(innerList.get(1)!=null && innerList.get(1).contains("a") ){ %>
				checked <%} %> value="a" /><%=questionList.get(1)%></td>
			<td><input type="checkbox" name="answer<%=j%>" disabled="disabled"
				<% if(innerList.get(1)!=null && innerList.get(1).contains("b") ){ %>
				checked <%} %> value="b" /><%=questionList.get(2)%></td>
			<td><input type="checkbox" name="answer<%=j%>" disabled="disabled"
				<% if(innerList.get(1)!=null && innerList.get(1).contains("c") ){ %>
				checked <%} %> value="c" /><%=questionList.get(3)%></td>
			<td><input type="checkbox" name="answer<%=j%>" disabled="disabled"
				<% if(innerList.get(1)!=null && innerList.get(1).contains("d") ){ %>
				checked <%} %> value="d" /><%=questionList.get(4)%></td>
		


		<%}else if(uF.parseToInt(questionList.get(6))==3){ %>
					<td><textarea rows="4" cols="20" disabled="disabled" name="answer<%=j%>"><% if(innerList.get(1)!=null){ %><%=innerList.get(1)%><%} %></textarea>
					</td>
				

		<%} %>
</tr>
		<%}%>
	</table>
	<%} }%>
	
	<s:form  name="frmAppraisalFinalSattlement" action="AppraisalFinalSattlement" method="POST" theme="simple">
	<s:hidden value="%{id}" name="id"/>
	<s:hidden value="%{empId}" name="empId"/>
	<s:hidden value="%{appFreqId}" name="appFreqId" id="appFreqId"/>
	<table>
	<tr>
					<td><h3>Comment</h3>
					</td>
				</tr>
	<tr>
					<td><textarea rows="4" cols="20"  name="comment"></textarea>
					</td>
				</tr>
				<tr>
				<td><input type="hidden" name="status" id="status"/>  </td> 
				</tr>

<tr>
					<td><input type="button" class="input_button" value="Approve" onclick="submitForm('true');"/>
					</td>
				</tr>

	</table>
	</s:form>

</div>
<script type="text/javascript">
 
function submitForm(status){
	document.getElementById("status").value=status;
	document.frmAppraisalFinalSattlement.submit();
}
</script>
