<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<% 


List reportList = (List)request.getAttribute("reportList");
List alDates = (List)request.getAttribute("alDates");

String strType= (String)request.getParameter("T");
String strPC= (String)request.getParameter("PC");
String strD1= (String)request.getParameter("D1");
String strD2= (String)request.getParameter("D2");


String strTypeText= "";
String strText= "";
String strTitle= "";

if(strType!=null && strType.equalsIgnoreCase("T")){
	strTitle = "Timesheet";
	strTypeText = "Timesheet Pay Cycles";
	strText="Please select any Employee to view employee timesheet, or <a href=\"PayCycleList.action?T=T\">go back to the paycycles</a>";
}else if(strType!=null && strType.equalsIgnoreCase("O")){
	strTitle = "Exceptions";
	strTypeText = "Exceptions Pay Cycles";
	strText="Please select any Employee or date to view employee exceptions, or <a href=\"PayCycleList.action?T=O\">go back to the paycycles</a>";
}else if(strType!=null && strType.equalsIgnoreCase("C")){
	strTitle = "Clock Entries";
	strTypeText = "Clock Entries";
	strText="Please select any Employee to view employee clock entries, or <a href=\"PayCycleList.action?T=C\">go back to the paycycles</a>";
}else if(strType!=null && strType.equalsIgnoreCase("EA")){
	strTitle = "Employee List";
	strTypeText = "";
	strText="";
}

%>





                        
    <div class="pagetitle">    
      <span><%=strTitle %></span>      
    </div>


    <div id="printDiv" class="leftbox reportWidth">
    <h5><%=strText %></h5>

    
   
<%
String strReqAlphaValue = (String)request.getParameter("alphaValue");
if(strReqAlphaValue==null){
	strReqAlphaValue="";
}

%>


<%
if(alDates.size()!=0)
{
%>
<div class="alphaValue">

<ul>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("A"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=A&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">A</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("B"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=B&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">B</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("C"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=C&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">C</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("D"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=D&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">D</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("E"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=E&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">E</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("F"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=F&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">F</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("G"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=G&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">G</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("H"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=H&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">H</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("I"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=I&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">I</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("J"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=J&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">J</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("K"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=K&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">K</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("L"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=L&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">L</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("M"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=M&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">M</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("N"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=N&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">N</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("O"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=O&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">O</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("P"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=P&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">P</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("Q"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=Q&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">Q</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("R"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=R&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">R</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("S"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=S&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">S</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("T"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=T&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">T</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("U"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=U&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">U</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("V"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=V&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">V</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("W"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=W&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">W</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("X"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=X&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">X</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("Y"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=Y&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">Y</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase("Z"))?"class=\"alpha_selected\"":"" %> href="?alphaValue=Z&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">Z</a></li>
<li><a <%=(strReqAlphaValue.equalsIgnoreCase(""))?"class=\"alpha_selected\"":"" %> href="?alphaValue=&T=<%=strType%>&PC=<%=strPC%>&D1=<%=strD1%>&D2=<%=strD2%>">All</a></li>
</ul>

</div>

<%
}
%>

<div style="width:300px;float:left;">

<table>

<%
for(int i=0; i < reportList.size(); i++){
	 %> 
	
	 <tr>
	 <td class="reportLabel"><%= (String)reportList.get(i)%></td>
	 </tr>
	 <% 
}
%>
</table>
</div>



<%if(strType!=null && strType.equalsIgnoreCase("O")) {%>

<div style="width:300px;float:left;">
<table>

<%
for(int i=0; i < alDates.size(); i++){
	 %> 
	 
	 <tr>
	 <td class="reportLabel"><%= (String)alDates.get(i)%></td>
	 </tr>
	 <%
}
%>

</table>

</div>

<%}%>

	</div>


