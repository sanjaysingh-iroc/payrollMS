<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Bell's Chart" name="title"/>
</jsp:include>

<div id="printDiv" class="leftbox reportWidth">  
<div style="width:100%; height: 200px">
        <jsp:include page="/jsp/chart/AppraisalAreaSplineChart.jsp" />
</div> 
</div>
  