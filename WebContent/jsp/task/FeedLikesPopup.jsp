<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% String likeIds = (String) request.getParameter("likeIds"); 
   
%>
<input type="hidden" name = "likeIds" value="<%=likeIds %>"/>
<%
	
	List<String> likesBy = (List<String>) request.getAttribute("likesList");
    if(likesBy == null) likesBy = new ArrayList<String>();
    if(likesBy!=null && likesBy.size()>0){
    	Iterator<String> it = likesBy.iterator();
    	while(it.hasNext()){
    	  String likeByName = 	it.next();
%>
			<div style="float:left;margin:10px 0px 0px 5px;"><img width="15" height="15" src="images1/icons/thumbs_up_blue.png"></div>	
			<div style="float:left;margin:10px 0px 0px 5px;width:82%;"><%=likeByName%></div>	
			<div class="clr"></div>
<%		 } 
	}
%>

