

<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>



			<link href="assessment/css/main.css" rel="stylesheet" type="text/css" />
            



	<!-- 		<title>Likelythis</title> -->
      <!--<link rel="stylesheet" type="text/css" href="../css/form.css" />
<script src="../js/custom-form-elements.js" type="text/javascript" />-->
<link rel="shortcut icon" href="images1/icons/icons/w_green.png" type="image/x-icon">
<link rel="icon" href="images1/icons/icons/w_green.png" type="image/x-icon">
    
    <!--<link rel="stylesheet" type="text/css" href="css/admin_home.css" />-->
<script type="text/javascript">
function valid()
{

	var user=document.getElementById("uname").value;
	var pass=document.getElementById("pwd").value;
	var userlen = user.length;
	var passlen = pass.length;
	
	if(user=="")
	{
		document.getElementById('eruname').innerHTML ='Please enter username';
		document.getElementById("uname").focus();
		return false;
	}
	else
    {
    	document.getElementById('eruname').innerHTML ='';
    }
	

	if(pass=="")
	{
		document.getElementById('erpass').innerHTML ='Please enter password';
		document.getElementById("pwd").focus();
		return false;
	}
	else
	{
    	document.getElementById('erpass').innerHTML ='';
	}
}
</script>


<script type="text/javascript">
var xmlhttp="";
var xhttp ="";

	function GetXmlHttpObject()
	{
		
		if (window.XMLHttpRequest)
		{
		// code for IE7+, Firefox, Chrome, Opera, Safari
			return new XMLHttpRequest();
		}
		if (window.ActiveXObject)
		{
		// code for IE6, IE5
			return new ActiveXObject("Microsoft.XMLHTTP");
		}
		return null;
	}
	
	function getusername()
	{
		var user = document.getElementById("uname").value;
		if(user=="")
		{
			document.getElementById("eruname").innerHTML="Please enter user name";
			document.getElementById("uname").focus();
			return false;
		}
		else
		{
			//document.getElementById("preload_email").style.display="none";
			xmlhttp=GetXmlHttpObject();
			
			if (xmlhttp==null)
			{
				alert ("Browser does not support HTTP Request");
				return;
			}
			else
			{

				var url="ajax_user_check.php";
				url +="?user=";
				url +=user;
				xmlhttp.onreadystatechange=stateChanged;
				xmlhttp.open("GET",url,true);
				xmlhttp.send(null);	
			}
		}
	}
	function stateChanged()
	{
		if (xmlhttp.readyState==4)
		{
			var res = xmlhttp.responseText;
			if(res!=0)
			{
				document.getElementById("eruname").innerHTML="The username is already exist";
				document.getElementById("uname").focus();
			}
			else
			{
				document.getElementById("eruname").innerHTML="";
			}
		}
	}
</script>

<style>
.signin_box {
	background: url("images/bkgd_mono_noblack_312.png") repeat-x scroll 0 0 transparent;
    margin: 0;
    padding: 0;
    width: 100%;
	clear:both;
	float:left;
	height:600px;;
}
</style>


				<div id="header">
				<div class="align_center corner_5 block">
					
						<div class="logo">
                       
				<a href="index.php"><img src="admin/upload/project_logo/newlogo_likely_upload.png"/></a>
							
						</div>
                   <div class="links">
								<div class="inner-link">
								<!-- 	<a href="user_login.html">User Login</a> -->
									<a href="#">User Login</a>
								</div>  
								<div class="inner-link">  
							<!-- 		<a href="company_login.html">Company Login</a>  -->
							<a href="#">Company Login</a>
								</div>               
							</div>
                                                        						   
				</div> 
			</div>
			  <div class="min-height">    
   <div class="clr"></div>
	<div id="content">
		<div class="align_center corner_5 block">
			<div class="hr_strip">
				<p>
					
					<div class="signin">
                    
                     
                                  <div class="userlogin"><h2>Corporate Login</h2></div>
                                  <div class="clr"></div>
                                  
						<form name="frm" id="frm" method="post" action="corporate_checklog.php">
							<!--logoin_background_img start here-->
							<div class="logoin_background_img">
								<!--login_text_box start here-->
								<div class="login_text_box">
                                
                                
									<h1>Please provide your login information</h1>
                                    <div class="clr"></div>
																		<!--login_textfild_box_top start here-->
									<div class="login_textfild_box_top">
										<div class="textfild_left">Username :</div>
										<div class="textfild_rigth">
											<div class="txt_bx">
												<input type="text" name="uname" id="uname" value="" class="field" title="Enter User Name"/>
											</div><!--txt_bx end here-->
											<div id="eruname" style="color:red;"></div>
										</div><!--textfild_rigth end here-->
									</div>
									<!--login_textfild_box_top end here-->
									<div class="login_textfild_box_middle">
										<div class="textfild_left"> Password :</div>
										<div class="textfild_rigth">
											<div class="txt_bx">
												<!--<input name="pwd" id="pwd" type="password" size="30" maxlength="8" />-->
												<input type="password" name="pwd" id="pwd" type="password" value="" class="field" title="Enter Password" />
												
											</div><!--txt_bx end here-->
											<div id="erpass" style="color:red; font-size:10px"></div>
										</div><!--textfild_rigth end here-->
									</div>
									<!--login_textfild_box_middle end here-->
									<div >
										<div class="textfild_left">&nbsp;</div>
										
										<div class="textfild_rigth_button">
										<input type="checkbox" class="styled" name="remember"><font size="2">Remember me next time
											<div class="submit">
												<label>
								
								<input type="submit" name="button" value="Sign In" class="admin_button" onclick="return valid();" />
								
													<div class="clr"></div>
												<div style="margin:10px 0px 0px 5px"><a href="forgotpassword.php">Forgot Password</a></div>
												</label>
											</div><!--submit end here-->
										</div><!-- textfild_rigth end here-->
									</div>
									<!--login_text_box end here-->
								</div>
								<!--logoin_background_img end here-->
							</div>
						</form>
					</div>
                    
                       <div class="signin_banner">
						<!--<p>The Best Decisions Start Here</p>-->
						<div class="clr"></div>
						
						<!--<h4>Self Assessment helps you take better decisions</h4>-->
						<div class="signin_images">
							<!--<img src="images/signin_banner1.jpg" />
							<img src="images/signin_banner2.jpg" />
							<img src="images/signin_banner3.jpg" />-->
                            <div class="loginpagetext">
                           
                            Login to the account to access innumerous features,<br/> create your own <b> Assessment</b> and target groups.                            
                            <br/><br/>
                            
                            
                            <div class="comp_regi"><div style="padding:25px 10px 20px 46px; line-height:25px;">&nbsp;&nbsp;&nbsp;&nbsp; If you dont have an account, then <br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="plans_pricing.php" style="color:#e58900;font-size:16px"><strong>Register here </strong></a></div> </div>
                                                             
                           
                            </div>
                            
						</div>
						
					</div>
                    
                    
                    
				</p>
			</div>
		</div>
	</div>
   </div> 
	            
   
</div>
            
            

