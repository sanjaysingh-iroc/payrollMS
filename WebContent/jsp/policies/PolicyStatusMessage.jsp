<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
UtilityFunctions uF=new UtilityFunctions();

String status=(String)request.getAttribute("status");
String count=(String)request.getAttribute("operation");
String pcount=(String)request.getAttribute("pcount");
System.out.println("jsdhgjkdhfgsdf");
if(uF.parseToInt(status)==-1){ %>
						<a href="javascript:void(0)" onclick="getContent('myDiv<%=count%>', 'WorkFlowPolicy.action?status=1&operation=S&pcount=<%=pcount%>&count=<%=count %>')"><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25" title="Disabled. Clcik to enable this policy"></i> <!-- <img src="images1/icons/denied.png" title="Disabled. Clcik to enable this policy" /> --></a>
					<%}else if(uF.parseToInt(status)==1){ %>
						<a href="javascript:void(0)" onclick="getContent('myDiv<%=count%>', 'WorkFlowPolicy.action?status=-1&operation=S&pcount=<%=pcount%>&count=<%=count %>')"> <i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d" title="Enabled. Clcik to disable this policy"></i><!-- <img src="images1/icons/approved.png" title="Enabled. Clcik to disable this policy" /> --></a>
					<%} %>