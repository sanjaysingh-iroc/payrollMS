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

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Employee Organogram View" name="title"/>
</jsp:include>
  
<g:compress>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/jquery.jOrgChart.css" />
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/prettify.css" />
	<script src="<%= request.getContextPath()%>/scripts/organisational/prettify.js" type="text/javascript"></script>
	<script src="<%= request.getContextPath()%>/scripts/organisational/jquery.jOrgChart.js" type="text/javascript"></script>
</g:compress>
    


 <div class="leftbox reportWidth" style="overflow:auto">
  
<%

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
Map hmEmpProfileImage = (Map)request.getAttribute("hmEmpProfileImage");



String strContextPath = request.getContextPath();


%>

<g:compress>
<script>
    jQuery(document).ready(function() {
        
        
	        $("#org").jOrgChart({
				chartElement : '#chart',
				dragAndDrop  : false
			});
        prettyPrint();
    });
    function viewEmployee(empId) {
//    	alert("panel id"+panelid+"candidateid"+candidateid);

    			
    			var dialogEdit = '#viewEmployeeDiv';
    			
    			$(dialogEdit).dialog({
    				autoOpen : false,
    				bgiframe : true,
    				resizable : false,
    				height : 600,
    				width : 800,
    				modal : true,
    				title : 'View Employee Details',
    				open : function() {
    					var xhr = $.ajax({
    						url : "ViewEmployeeDetails.action?empId="+empId ,
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
 </g:compress>   




 


<div id="myDiv" style="background-color: #999999;color: white;font-weight: bold;margin: 5px;text-align: center;width: 100%;"></div>



<ul id="org" style="display:none">
 
	<%!
	
	
	StringBuilder sb = new StringBuilder();
	UtilityFunctions uF = new UtilityFunctions();
	
	public String func(Map hmHireracyLevels, String strEmpId, Map hmEmpMap,Map hmEmpDesigMap, Map hmEmpProfileImage, StringBuilder sb, String strContextPath, String strSessionEmpId, String strUserType, List alChain, CommonFunctions CF){
			
		
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
				sb.append("<img  height=\"60\"  src=\""+(String)hmEmpProfileImage.get(strEmpId1)+"\" />");
			}
			
			sb.append("<div class=\"emp\" id=\""+strEmpId1+"\"><a href=\"javascript:void(0)\" onclick=\"viewEmployee('"+strEmpId1+"')\">"+hmEmpMap.get(strEmpId1)+"</a></div>");
			
			
			
			sb.append("<span class=\"desg_tree\">"+uF.showData((String)hmEmpDesigMap.get(strEmpId1),"-") +"</span>");
			
			
			
			
			
			List al = (List)hmHireracyLevels.get(strEmpId1);
			if(al!=null && al.size()>0){
				func(hmHireracyLevels, strEmpId1, hmEmpMap,hmEmpDesigMap, hmEmpProfileImage, sb, strContextPath, strSessionEmpId, strUserType, alChain, CF);
			}
			sb.append("</li>");
			
			if(ii==alInner1.size()-1){
				sb.append("</ul>");		
			}
		}
		return sb.toString();
	}
	
	%>	


	<li> <img  src="<%=CF.getStrOrgLogo() %>" height="60px"/>
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
						<img  height="60" style="border:1px solid #CCCCCC;"  src="<%=(String)hmEmpProfileImage.get(strEmpId) %>" />
						<!-- <div class="clr"></div> -->	
					<%}%>
					
					<div class="emp" style="margin-top: 5px;" id="<%=strEmpId%>"><a href="javascript:void(0)" onclick="viewEmployee('<%=strEmpId%>')"><%=(String)hmEmpMap.get(strEmpId) %></a></div>
					<!-- <div class="clr"></div> -->
					<span class="desg_tree"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId),"-")%></span>
					
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
						<img  height="60"  style="border:1px solid #CCCCCC;" src="<%=strContextPath %>/userImages/<%=(String)hmEmpProfileImage.get(strEmpId1) %>" />
						<!-- <div class="clr"></div> -->	
					<%}%>
					
					<div class="emp" style="margin-top: 5px;" id="<%=strEmpId1%>"><a href="javascript:void(0)" onclick="viewEmployee('<%=strEmpId1%>')"><%=(String)hmEmpMap.get(strEmpId1)%></a></div>
					<!-- <div class="clr"></div> -->
					<span class="desg_tree"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId1),"-") %></span>
					
					
				<%
				List al = (List)hmHireracyLevels.get(strEmpId1);
				if(al!=null){
					sb = new StringBuilder();
					out.println(func(hmHireracyLevels, strEmpId1, hmEmpMap,hmEmpDesigMap, hmEmpProfileImage, sb, strContextPath, strSessionEmpId, strUserType, alChain, CF));
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
<div id="viewEmployeeDiv"></div>



