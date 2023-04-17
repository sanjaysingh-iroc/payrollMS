
 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>

<!-- 
<s:head theme="" ></s:head>
 -->

<title>Access Denied!!!</title>
<script type="text/javascript">

onload=function(){
   
	document.getElementById('the_div').className="nothere";
   	document.getElementById("ajaxLoadImage").style.visibility="hidden";
   	document.getElementById('ui-datepicker-div').style.display = 'none';
	
};


</script>


<jsp:include page="../common/Links.jsp" flush="true" />

</head>

<body>

<div id="wrapper">
         <div class="container">
       
	       <div id="the_div" class="there">
			   <div id="ajaxLoadImage"></div>
			</div>
  		
         	<jsp:include page="../common/Header.jsp" flush="true" />
  		 
        
		 
			<div id="shadow">
         		
         		
         		
         		<div class="leftbox reportWidth">
         			You do not have access to this software. Please contact administrator of this software with the following information.
         			<br />
         			Machine Id : <%=request.getAttribute("MAC_ID") %><br/>
         			Created On : <%=request.getAttribute("CREATED_ON") %><br/>
         			
         		</div>
			</div>
        	<jsp:include page="../common/Footer.jsp" flush="true" />
         
      </div> 
       </div>      
         
</body>

</html>