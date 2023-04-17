<%@page import="com.konnect.jpms.util.IMessages"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TDepartmentChart%>" name="title"/>
</jsp:include>
 
<g:compress>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/jquery.jOrgChart.css" />
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/prettify.css" />
	<script src="<%= request.getContextPath()%>/scripts/organisational/prettify.js" type="text/javascript"></script>
	<script src="<%= request.getContextPath()%>/scripts/organisational/jquery.jOrgChart.js" type="text/javascript"></script>
</g:compress>
    


 <div class="leftbox reportWidth" style="overflow:auto">
  
<%
System.out.println("ORG CHART 2 jsp ");
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
if(CF==null)return;

String strSessionEmpId = (String)session.getAttribute(IConstants.EMPID);
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);

Map hmHireracyLevels = (Map)request.getAttribute("hmHireracyLevels");
List alHireracyLevels = (List)request.getAttribute("alHireracyLevels");
List alChain = (List)request.getAttribute("alChain");
 Map hmEmpDesigMap = (Map)request.getAttribute("hmEmpDesigMap"); 
Map hmEmpMap = (Map)request.getAttribute("hmEmpMap");
Map hmStyleClassMap = (Map)request.getAttribute("hmStyleClassMap");
Map hmStyleColorMap = (Map)request.getAttribute("hmStyleColorMap");
Map hmLocationColor = (Map)request.getAttribute("hmLocationColor");

 Map hmEmpProfileImage = (Map)request.getAttribute("hmEmpProfileImage"); 
 Map hmCount = (Map)request.getAttribute("hmCount");


String strContextPath = request.getContextPath();


%>

<g:compress>
<script>
    jQuery(document).ready(function() {
        
        <%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>
	        $("#org").jOrgChart({
				chartElement : '#chart',
				dragAndDrop  : false
			});
        <%}else{%>
	        $("#org").jOrgChart({
				chartElement : '#chart',
				dragAndDrop  : false
			});
        <%}%>
		prettyPrint();
    });
    function ajaxCall(a, b){
    	if(confirm('Are you sure you want to update the structure?')){
    		var sourceid = a.find("div").attr('id');
       	  	var targetid = b.find("div").attr('id');
       	  	getContent('myDiv','UpdateOrganisation.action?empid='+sourceid+'&superid='+targetid);	
    	}
    }
    </script>
 </g:compress>   

<style>
.sbu{
	-moz-border-radius: 6px 6px 6px 6px;
    background-color: lightcyan;
    margin: -5px -10px;
    padding: 5px;
    }
.wlocation{
	-moz-border-radius: 6px 6px 6px 6px;
    background-color: lightblue;
    margin: -5px -10px;
    padding: 5px;
    }
.org{
	-moz-border-radius: 6px 6px 6px 6px;
    background-color: lightgreen;
    margin: -5px -10px;
    padding: 5px;
    }    
    
.dept{
	-moz-border-radius: 6px 6px 6px 6px;
    background-color: orange;
    margin: -5px -10px;
    padding: 5px;
    }    
</style>


<%-- 
 <%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>
	  <div><div class="move"></div> Please drag n drop the employees to update the organisation tree.</div>
 <%} %>
 --%>


<div style="width: 100%;">

<div style="float:left">
<div class="org" style="width: 20px; height: 10px; float: left; margin: 5px; padding: 0px;"></div><div style="float: left;"> Organisation</div>
</div>
<!-- 
<div style="float:left">
<div class="wlocation" style="width: 20px; height: 10px; float: left; margin: 5px; padding: 0px;"></div><div style="float: left;"> Location</div>
</div>
 -->
<div style="float:left">
<div class="sbu" style="width: 20px; height: 10px; float: left; margin: 5px; padding: 0px;"></div><div style="float: left;"> SBU</div>
</div>

<!-- <div style="float:left">
<div class="dept" style="width: 20px; height: 10px; float: left; margin: 5px; padding: 0px;"></div><div style="float: left;"> Department</div>
</div> -->

<div style="float:left">
<div class="dept" style="width: 20px; height: 10px; float: left; margin: 5px; padding: 0px;"></div><div style="float: left;"> Department</div>
</div>

<%-- <%
Set set = hmLocationColor.keySet();
Iterator it = set.iterator();
while(it.hasNext()){
	String str = (String)it.next();
	
%>
<div style="float:left">
<div class="dept" style="width: 20px; height: 10px; float: left; margin: 5px; padding: 0px;background-color:<%=hmLocationColor.get(str)%>"></div><div style="float: left;"> <%=hmEmpMap.get(str+"_WL") %></div>
</div>


<%	
	
}


%> --%>







</div>


<div id="myDiv" style="background-color: #999999;color: white;font-weight: bold;margin: 5px;text-align: center;width: 100%;"></div>



<ul id="org" style="display:none">
 
	<%!
	
	
	StringBuilder sb = new StringBuilder();
	UtilityFunctions uF = new UtilityFunctions();
	
	public String func(Map hmHireracyLevels, String strEmpId, Map hmEmpMap,Map hmEmpDesigMap, Map hmEmpProfileImage, StringBuilder sb, String strContextPath, String strSessionEmpId, String strUserType, List alChain, Map hmStyleClassMap, Map hmStyleColorMap, Map hmCount, CommonFunctions CF){
			
		
		List alInner1 = (List)hmHireracyLevels.get(strEmpId);
		if(alInner1==null)alInner1 = new ArrayList();
		String strEmpId1 = null;
		for(int ii=0; ii<alInner1.size(); ii++){
			strEmpId1 = (String)alInner1.get(ii); 
			
			if(strUserType!=null && alChain!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.MANAGER)) && !alChain.contains(strEmpId1)){
				continue;
			}
			
			
			if(ii==0){
				sb.append("<ul>");		
			}
			
			sb.append("<li>");
			
			if(strSessionEmpId!=null && strSessionEmpId.equalsIgnoreCase(strEmpId1)){
				sb.append("<img  height=\"60\"  src=\""+CF.getStrDocRetriveLocation()+(String)hmEmpProfileImage.get(strEmpId1)+"\" />");
			}
			
			sb.append("<div style=\"background-color:"+hmStyleColorMap.get(strEmpId1)+"\" class=\""+hmStyleClassMap.get(strEmpId1)+"\" id=\""+strEmpId1+"\">"+
			hmEmpMap.get(strEmpId1)+(((String)hmCount.get(strEmpId1)!=null)?"<br/>["+(String)hmCount.get(strEmpId1)+"]":"")+"</div>");
			
			
			/* sb.append("<span class=\"desg_tree\">"+uF.showData((String)hmEmpDesigMap.get(strEmpId1),"-") +"</span>"); */
			
			
			List al = (List)hmHireracyLevels.get(strEmpId1);
			if(al!=null && al.size()>0){
				func(hmHireracyLevels, strEmpId1, hmEmpMap,hmEmpDesigMap, hmEmpProfileImage, sb, strContextPath, strSessionEmpId, strUserType, alChain, hmStyleClassMap, hmStyleColorMap, hmCount, CF);
			}
			sb.append("</li>");
			
			if(ii==alInner1.size()-1){
				sb.append("</ul>");		
			}
		}
		return sb.toString();
	}
	
	%>	


	<li> <img  src="<%=request.getContextPath() %>/userImages/<%=CF.getStrOrgLogo() %>" />
	<ul> 
<%
	List alInner = (List)hmHireracyLevels.get("0");
		

		for(int i=0; i<alInner.size(); i++){
			String strEmpId = (String)alInner.get(i);
			

			if(strUserType!=null && alChain!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.MANAGER)) && !alChain.contains(strEmpId)){
				continue;
			}
			
			%>
				
					<li>
					<%if(strSessionEmpId!=null && strSessionEmpId.equalsIgnoreCase(strEmpId)){%>
						<%-- <img  height="60"  src="<%=strContextPath %>/userImages/<%=(String)hmEmpProfileImage.get(strEmpId) %>" /> --%>
						<!-- <div class="clr"></div> -->	
					<%}%>
					
					<div style="background-color:<%=hmStyleColorMap.get(strEmpId)%>" class="<%=(String)hmStyleClassMap.get(strEmpId)%>" id="<%=strEmpId%>">
					<%=(String)hmEmpMap.get(strEmpId) %>					
					</div>
					<!-- <div class="clr"></div> -->
					<%-- <span class="desg_tree"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId),"-")%></span> --%>
					
					<ul>
			
			<%
				
			List alInner1 = (List)hmHireracyLevels.get(strEmpId);
			if(alInner1==null)alInner1 = new ArrayList();
			String strEmpId1 = null;
			for(int ii=0; ii<alInner1.size(); ii++){
				strEmpId1 = (String)alInner1.get(ii); 
				
				if(strUserType!=null && alChain!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.MANAGER)) && !alChain.contains(strEmpId1)){
					continue;
				}
				
				%>
					<li>
					
					<%if(strSessionEmpId!=null && strSessionEmpId.equalsIgnoreCase(strEmpId1)){%>
						<%-- <img  height="60"  src="<%=strContextPath %>/userImages/<%=(String)hmEmpProfileImage.get(strEmpId1) %>" /> --%>
						<!-- <div class="clr"></div> -->	
					<%}%>
					
					<div style="background-color:<%=hmStyleColorMap.get(strEmpId)%>" class="<%=(String)hmStyleClassMap.get(strEmpId1)%>" id="<%=strEmpId1%>">
					<%=(String)hmEmpMap.get(strEmpId1)%>					
					<%=(((String)hmCount.get(strEmpId1)!=null)?"<br/>["+(String)hmCount.get(strEmpId1)+"]":"") %>
					</div>
					<!-- <div class="clr"></div> -->
					<%-- <span class="desg_tree"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId1),"-") %></span> --%>
					
					
				<%
				List al = (List)hmHireracyLevels.get(strEmpId1);
				if(al!=null){
					sb = new StringBuilder();
					out.println(func(hmHireracyLevels, strEmpId1, hmEmpMap,hmEmpDesigMap, hmEmpProfileImage, sb, strContextPath, strSessionEmpId, strUserType, alChain, hmStyleClassMap, hmStyleColorMap, hmCount, CF));
				}
			}
			
			%>
				
				 </li>
				</ul>
			</li>
			<%
			
		}
		
		%>
		</ul>
	</li>	
</ul>





    
        <script>
        jQuery(document).ready(function() {

    		/* Custom jQuery for the example */
    		$("#show-list").click(function(e){
    			e.preventDefault();

    			$('#list-html').toggle('fast', function(){
    				if($(this).is(':visible')){
    					$('#show-list').text('Hide underlying list.');
    					$(".topbar").fadeTo('fast',0.9);
    				}else{
    					$('#show-list').text('Show underlying list.');
    					$(".topbar").fadeTo('fast',1);
    				}
    			});
    		});

            $('#list-html').text($('#org').html());
            	$("#org").bind("DOMSubtreeModified", function() {
                    $('#list-html').text('');

                    $('#list-html').text($('#org').html());

                    prettyPrint();
                });
        });
    </script>
    
     	<div id="chart" class="orgChart" style="float:left;width:100%;text-align: center;"></div>



</div>


<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
}); 
</script>

