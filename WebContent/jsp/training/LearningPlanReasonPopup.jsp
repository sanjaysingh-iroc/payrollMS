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

<div class="leftbox reportWidth2">

<%=(String)request.getAttribute("lPlanReason") %>
</div>