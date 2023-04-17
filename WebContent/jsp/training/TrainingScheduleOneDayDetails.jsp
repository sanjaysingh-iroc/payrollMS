<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<style>

.reportWidth2 {
	background: none repeat scroll 0 0 #FFFFFF;
    border: 1px solid #A1A1A1;
    border-radius: 4px 4px 4px 4px;
    box-shadow: 0 0 0 #CCCCCC;
    float: left;
    margin: 10px 5px;
    min-height: 70%;
    padding: 10px 10px 20px; 
    width: 90%;
}

</style>

<div class="leftbox reportWidth">

<div style="float: left; width: 100%; font-weight: bold;"><div style="float: left; margin: 10px;"> Day <%=(String)request.getAttribute("dayCount") %>:</div> <div style="float: left; margin: 10px;"> <%=(String)request.getAttribute("strDate") %></div></div>

<div style="float: left; width: 100%; margin: 0px 10px;"><%=(String)request.getAttribute("dayDescription") %></div>
</div>