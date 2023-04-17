<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>


<script>
      jQuery(document).ready(function(){
          // binds form submission and fields to the validation engine
          jQuery("#signUpForm").validationEngine();
      });
      
      function addcandidatebymyself(varjobid){
    	  window.location="AddCandidate.action?jobid="+varjobid;
      }
</script>
 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Add New Candidate" name="title"/>
</jsp:include>

<div class="leftbox reportWidth">

<div style="width:45%;float:left;">
	<div style="width:100%;float:left;border: solid 1px #ccc; ">
	<p class="past">To add new Candidate by yourself click on the link below</p> 
	<div style="text-align:center; padding:10px;">
	<%-- <a href="javascript:void(0)" onclick="addNewCandidate('<%=request.getAttribute("recruitID") %>')"><input type="button" class="input_button" value="Add Candidate By Myself"> </a> --%>
		<s:form theme="simple" action="AddCandidate" method="post" cssClass="formcss">
		<input type="hidden" name="recruitId" value=<%=request.getAttribute("recruitID") %> >
			<s:submit cssClass="input_button" name="myself" value="Add Candidate By Myself" ></s:submit>
		</s:form>
	</div>
	</div>
	<!-- <br/><br/><br/><br/><br/><br/>
<a href="OpenJobReport.action">Go To Previous Page</a>
 -->
</div>



<div class="or_div" style=""><div style="background:#fff; padding:10px 0px; margin:30px 0px">OR</div></div>

<div style="width:45%;float:right;border: solid 0px #ccc;text-align: center; margin: 0px 0px;">
<p style="color: green"><s:property value="message"/></p>
</div>

<div style="width:45%;float:right;border: solid 1px #ccc; ">
<p class="past">Let Candidate fill the information for you</p>
        <div style=" padding:10px;">
       	        
            <s:form theme="simple" method="post" id="signUpForm" cssClass="formcss" enctype="multipart/form-data">
            <s:hidden name="recruitId" />
                <s:token name="token"></s:token>
                <table>
                <tr><td>Candidate First Name<sup>*</sup>:</td><td><s:textfield name="fname" cssClass="validateRequired text-input"></s:textfield></td></tr>
                <tr><td>Candidate Last Name<sup>*</sup>:</td><td><s:textfield name="lname" cssClass="validateRequired text-input"></s:textfield></td></tr>
                <tr><td>Candidate Email Id<sup>*</sup>:</td><td><s:textfield name="email" cssClass="validateRequired"></s:textfield></td></tr>
                <s:hidden name="notification" value="signup" />
                <tr><td>&nbsp;</td><td><s:submit cssClass="input_button" name="emailEmployee" value="Let Candidate Enter the Info"></s:submit></td></tr>
                </table>
            </s:form>
        </div>
</div>


<div style="float: left;width:100%;margin-top: 10px;">
<%
if(request.getAttribute("sbMessage")!=null){
	out.println(request.getAttribute("sbMessage"));	
} 
%>
</div>

</div>
 

<script>
$(document).ready(function(){
	$("#signUpForm_emailEmployee").click(function(){
		$(".validateRequired").prop('required',true);
		$("#signUpForm_email").prop('type','email');
	});
});
</script>