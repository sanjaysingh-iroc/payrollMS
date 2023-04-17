<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script>
    function addcandidatebymyself(varjobid){
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Add Candidate');
		$("#modalInfo").show();
		$.ajax({
			//url : "ApplyLeavePopUp.action", 
			url :"AddCandidate.action?jobid="+varjobid,
					
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
		
    	//window.location="AddCandidate.action?jobid="+varjobid;
    }
    
    function checkMailID(value) {
    //alert("sdfasf ");
    /* var mailID = document.getElementById("email").value; 
    var fname = document.getElementById("fname").value;
    var lname = document.getElementById("lname").value;
    if(mailID == "" || fname == "" || lname == ""){
    alert("Please fill all fields");
    }else{ */
      xmlhttp = GetXmlHttpObject();
      if (xmlhttp == null) {
              alert("Browser does not support HTTP Request");
              return;
      } else {
              var xhr = $.ajax({
              	 
                      url : "EmailValidation.action?candiEmail=" + value,
                      cache : false,
                      success : function(data) {
                      	//alert("data.length ===> "+data.length + "  data ===> "+data);
                      	if(data.length > 1){
                      		//document.getElementById('signUpForm').submit();
                      		document.getElementById("email").value = "";
                              document.getElementById("msgDiv").innerHTML = data;
                      	}else{
                              document.getElementById("msgDiv").innerHTML = data;
                      	}
                      }
              });
      	}
    /* } */
    }
    
    function GetXmlHttpObject() {
      if (window.XMLHttpRequest) {
              // code for IE7+, Firefox, Chrome, Opera, Safari
              return new XMLHttpRequest();
      }
      if (window.ActiveXObject) {
              // code for IE6, IE5
              return new ActiveXObject("Microsoft.XMLHTTP");
      }
      return null;
    }      
    
    $(function(){
    	$("#signUpForm_emailEmployee").click(function(){
    		$(".validateRequired").prop('required',true);
    		$("#email").prop('type','email');
    	});
    });
    
    
    $("#signUpForm").submit(function(event){
  		event.preventDefault();
		var fromPage = document.getElementById("fromPage").value;
		//alert(fromPage);
		if(fromPage == null || fromPage == '' || fromPage == 'null' || fromPage != 'CR') {
			var recruitId = document.getElementById("recruitId").value;
			var form_data = $("#signUpForm").serialize();
			//alert("form_data ===>> " + form_data);
			$.ajax({
				type :'POST',
				url  :'AddCandidateModePopup.action',
				data :form_data,
				cache:true/* ,
				success : function(result) {
					$("#subSubDivResult").html(result);
				} */
			});
			
			$.ajax({
				url: 'Applications.action?recruitId='+recruitId,
				cache: true,
				success: function(result){
					$("#subSubDivResult").html(result);
		   		}
			});
			
		} else {
			var recruitId = document.getElementById("recruitId").value;
			var fname = document.getElementById("fname").value;
			var lname = document.getElementById("lname").value;
			var email = document.getElementById("email").value;
			var notification = document.getElementById("notification").value;
			
			var action = "AddCandidateModePopup.action?recruitId="+recruitId+"&fname="+fname+"&lname="+lname+"&email="+email
				+"&fromPage="+fromPage+"&notification="+notification;
			window.location = action;
		}
  	});
    
</script>
<%
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    UtilityFunctions uF = new UtilityFunctions();
    
    Map<String, String> hmEducationDetails = (Map)request.getAttribute("hmEducationDetails");
    /* 	Map<String, String[]> hmExperienceDetails = (Map)request.getAttribute("hmExperienceDetails"); */
    Map<String, String> hmExperienceDetails = (Map)request.getAttribute("hmExperienceDetails");
    Map<String, String> hmSkillDetails = (Map)request.getAttribute("hmSkillDetails");
    
    List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
    
    %>
<div class="leftbox reportWidth">
    <div class="row">
        <div class="col-lg-5 col-md-5 col-sm-12">
            <div>
                <div style="width:100%;float:left;border: solid 1px #ccc; ">
                    <p class="past">To add new Candidate by yourself click on the link below </p>
                    <div style="text-align:center; padding:10px;">
                        <a href="javascript:void(0)" onclick="addNewCandidate('<%=request.getAttribute("recruitId") %>','<%=request.getAttribute("fromPage") %>')"><input type="button" class="btn btn-primary" value="Add Candidate By Myself"> </a>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-2 col-md-2 hidden-sm hidden-xs autoWidth">
            <img src="images1/or_bg.png" width="25px" height="100px"/>
            <div style="background:#fff;text-align:center;font-weight: 600;font-size: 20px;color: #999999;">OR</div>
            <img src="images1/or_bg.png" width="25px" height="100px"/>
        </div>
        <div class="visible-sm-block visible-xs-block hidden-md hidden-lg autoWidth">
            <img src="images1/or_horizontal.png" width="45%" height="25px"/>
            <div style="background:#fff;display: inline;text-align:center;font-weight: 600;font-size: 20px;color: #999999;">OR</div>
            <img src="images1/or_horizontal.png" width="45%" height="25px"/>
        </div>
        <div class="col-lg-5 col-md-5 col-sm-12">
            <div style="border: solid 1px #ccc; width: 381px;">
                <p class="past">Let Candidate fill the information for you</p>
                <div style=" padding:10px;">
                    <s:form theme="simple" action="AddCandidateModePopup" name="signUpForm" method="post" id="signUpForm" cssClass="formcss">
                        <s:hidden name="recruitId" id="recruitId" />
                        <s:hidden name="fromPage" id="fromPage" />
                        <s:token name="token"></s:token>
                        <table class="table table_no_border" style="table-layout: fixed;">
                            <tr>
                                <td style="width: 155px;">Candidate First Name:<sup>*</sup></td>
                                <td>
                                    <s:textfield name="fname" id="fname" cssClass="validateRequired"></s:textfield>
                                </td>
                            </tr>
                            <tr>
                                <td>Candidate Last Name:<sup>*</sup></td>
                                <td>
                                    <s:textfield name="lname" id="lname" cssClass="validateRequired"></s:textfield>
                                </td>
                            </tr>
                            <tr>
                                <td>Candidate Email Id:<sup>*</sup></td>
                                <td>
                                    <s:textfield name="email" id="email" cssClass="validateRequired" onblur="checkMailID(this.value);" onchange="checkMailID(this.value);"></s:textfield>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
	                                <div id="msgDiv"></div>
	                                <s:hidden name="notification" id="notification" value="signup" />
                                </td>
                            </tr>
                            
                            <tr>
                                <td colspan=2 align="center">
                                    <s:submit cssClass="btn btn-primary" name="emailEmployee" value="Let Candidate Enter the Info"></s:submit>
                                </td>
                            </tr>
                        </table>
                    </s:form>
                </div>
            </div>
        </div>
    </div>
    
    <div style="float: left;width:100%;margin-top: 10px;">
        <%
            if(request.getAttribute("sbMessage")!=null) {
            	out.println(request.getAttribute("sbMessage"));	
            }
            %>
    </div>
    <% if(uF.parseToInt(request.getAttribute("recruitId").toString()) > 0) { %>
    <div id="showallcand"></div>
    <script>
    	$("#showallcand").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $.ajax({
			url :'ShowAllCandidate.action?recruitId=<%=request.getAttribute("recruitId") %>',
		    type : 'GET',
			success : function(data) {
				$("#showallcand").html(data);
			}
		});
    </script>
    <% } %>
</div>
