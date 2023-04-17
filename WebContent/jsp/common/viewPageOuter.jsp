<!DOCTYPE html>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>

<%
	String strPage = (String) request.getAttribute("PAGE");
	String strTitle = (String) request.getAttribute("TITLE");
	
	if (strPage == null) {
		strPage = "Login.jsp";
	}
	
	if (strTitle == null) {
		strTitle = "Workrig | Human Capital Management"; 
	}
	
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    
    if (CF == null){
		CF = new CommonFunctions();
		CF.setRequest(request);
	}
	String strLogo = (String) session.getAttribute("ORG_LOGO");
	//System.out.println("strLogo ===> " + strLogo);
	if (strLogo == null) {
		strLogo = CF.getOrgLogo(request, CF); 
	}
	String strLogoSmall = (String) session.getAttribute("ORG_LOGO_SMALL");
	
	if (strLogoSmall == null) {
		strLogoSmall = CF.getOrgLogoSmall(request, CF); 
	} 
	
	String strUITheme = CF.getStrUI_Theme();
	
    %>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1"> 
         
        <title><%=strTitle%></title>
        <link rel="icon" href="images1/icons/icons/w_green.png">
         <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
		<% if(strUITheme != null && strUITheme.equals("2")) { %>
			<link rel="stylesheet" href="dist/css/AdminLTENew_two.min.css">
	        <link rel="stylesheet" href="dist/css/skins/_all-skinsNew_two.min.css">
	         <link rel="stylesheet" href="bootstrap/css/bootstrapNew_two.css">
	         <link rel="stylesheet" href="dist/css/skins/close-popupNew_two.css">
	         
		<% } else if(strUITheme != null && strUITheme.equals("1")) { %>
			<link rel="stylesheet" href="dist/css/AdminLTENew.min.css">
	        <link rel="stylesheet" href="dist/css/skins/_all-skinsNew.min.css">
	         <link rel="stylesheet" href="bootstrap/css/bootstrapNew.css">
	         <link rel="stylesheet" href="dist/css/skins/close-popupNew.css">
	         
		<% } else { %>
	        <link rel="stylesheet" href="dist/css/AdminLTE.min.css">
	        <link rel="stylesheet" href="dist/css/skins/_all-skins.min.css">
	         <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
	        <link rel="stylesheet" href="dist/css/skins/close-popup.css"> 
	        
        <% } %>
        
        <link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
		<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
        <link href="js_bootstrap/timepicker/bootstrap-timepicker.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
        <link rel="stylesheet" type="text/css" href="js/datatables_new/buttons.dataTables.min.css" />
        <link rel="stylesheet" type="text/css" href="js/datatables_new/dataTables.bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="js/datatables_new/responsive.bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="css/jquery.poplight.css"/>
		<link rel="stylesheet" type="text/css" href="js_bootstrap/bootstrap-datetimepicker/bootstrap-datetimepicker.min.css"/>
		<link rel="stylesheet" href="css/font-awesome-4.7.0/css/font-awesome.min.css">
		<link rel="stylesheet" href="css/font-awesome-4.7.0/css/font-awesome.css">
        <link rel="stylesheet" href="css/font-awesome/font-awesome-animation.min.css">
        <link rel="stylesheet" href="bootstrap/css/bootstrap-glyphicons.css"><!-- Created by Dattatray Date:25-June-2021 -->
		
        
        <% if(strUITheme != null && strUITheme.equals("2")) { %>
       		<link rel="stylesheet" href="css/new_customNew_two.css">
       		<link rel="stylesheet" href="css/login-form-elementsNew_two.css">
       		<link rel="stylesheet" href="bootstrap/css/New_two.css">
       	<% } else if(strUITheme != null && strUITheme.equals("1")) { %>
    		<link rel="stylesheet" href="css/new_customNew.css">
    		<link rel="stylesheet" href="css/login-form-elementsNew.css">
    		<link rel="stylesheet" href="bootstrap/css/New.css">
    	<% } else { %>
       		<link rel="stylesheet" href="css/new_custom.css">
       		<link rel="stylesheet" href="css/login-form-elements.css">
       	<% } %>
       	
       	<!-- <link rel="stylesheet" href="css/new_custom.css"> -->
    </head>
    <body class="hold-transition skin-blue sidebar-mini sidebar-collapse">
        <div class="wrapper">
            <header class="main-header">
                <!-- Logo -->
                
                <%-- <% if(PRODUCTTYPE == null || PRODUCTTYPE.equals("2")) { %> --%>
                <a class="logo" href="javascript:void(0);"> 
                    <span class="logo-mini">
	                    <!-- <img src="images1/icons/beta-ribbon.png" style="position: absolute; margin-left: -5px; width: 35px;">
	                    <img src="images1/icons/icons/mini-workrig.png" style="width: 40px;"> -->
	                   <!--  <img src="images1/icons/icons/mini-workrig.png" style="width: 50px;"> --> 
                    </span>
                    <!-- logo for regular state and mobile devices -->
                    <span class="logo-lg">
	                    <!-- <img src="images1/icons/beta-ribbon.png" style="width: 50px; margin-top: -8px; margin-left: -81px;">
	                    <img src="images1/icons/icons/workrig_white.png" style="width: 90px;"> -->
	                    <!-- <img src="images1/icons/icons/workrig_white.png" style="width: 90px;"> -->
                    </span> 
                </a>    
				<%-- <% } %> --%>
                
                <!-- Header Navbar: style can be found in header.less -->
                <nav class="navbar navbar-static-top" role="navigation">
                    
                </nav>
            </header>
            
            <!-- Content Wrapper. Contains page content -->
            	<div class="content-wrapper" style="min-height: 700px; margin-left: 0px !important;">
            	<section class="content-header paddigtop2" id="pageTitleAndNaviTrail">
			      <% String PAGETITLE_NAVITRAIL = (String) request.getAttribute("PAGETITLE_NAVITRAIL"); %>
			      <% if(PAGETITLE_NAVITRAIL != null && !PAGETITLE_NAVITRAIL.trim().equals("")) { %>
			      	<ol class="breadcrumb" style="top: 1px; position: relative; float: left; width: 100%;">
			      		<%=PAGETITLE_NAVITRAIL %>
			      	</ol>
			      <% } else { %>
			    <% } %>
			    </section>
            
                <!-- Main content -->
                <section class="content paddigtop0" style="margin-top: -5px !important;">
                    <div class="row">
                      	<jsp:include page="<%=strPage%>"/>
                    </div>
                    <!-- /.row -->
                </section>
                <!-- /.content -->
            </div>
            
           	<!-- <footer class="main-footer" style="margin-left: 0px !important;">
            	<div class="pull-right hidden-xs">
                    <b>Version</b> 1.8
                </div>
                <strong>Copyright &copy; 2021-2022 <a href="http://www.workrig.com">Workrig</a>.</strong> All rights reserved.
            </footer> -->
        </div>
        
        	
        	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
       
			<!-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> -->
        	<script type="text/javascript" src="js/jquery.sparkline.js"></script>
	        <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>	
	        <script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
	        <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
	        <script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
			<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
	        <script type="text/javascript" src="scripts/waypoints.js"></script> 
	        <script type="text/javascript" src="js/datatables_new/jquery.dataTables.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/pdfmake.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/buttons.print.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/jszip.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/dataTables.bootstrap.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/dataTables.buttons.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/buttons.flash.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/vfs_fonts.js"></script>
	        <script type="text/javascript" src="js/datatables_new/buttons.html5.min.js"></script>
			<script type="text/javascript" src="js/datatables_new/dataTables.responsive.min.js"></script>
			<script type="text/javascript" src="js/datatables_new/buttons.bootstrap.min.js"></script>
			<script type="text/javascript" src="js/datatables_new/responsive.bootstrap.min.js"></script>
			<script type="text/javascript" src="scripts/jquery.lazyload.js"></script>
	        <script type="text/javascript" src="dist/js/app1.min.js"></script>
			<script type="text/javascript" src="scripts/_rating/js/jquery.raty.min.js"> </script>
	        <script type="text/javascript" src="scripts/organisational/jquery.jOrgChart.js"></script>
	        <script type="text/javascript" src="scripts/customAjax.js" ></script>
	        <script type="text/javascript" src="js/jquery.rateyo.min.js"></script>
	        <script type="text/javascript" src="js/moment.js"></script>
	        <script type="text/javascript" src="js_bootstrap/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js"></script>
	        <script type="text/javascript" src="js_bootstrap/Jcrop/jquery.Jcrop.min.js"></script>
	        <script type="text/javascript">
	        $(".selectedL").parent().addClass("active");
	        $(".selectedL").next('.treeview-menu').css('display','block');
	        $(".selectedL").next('.treeview-menu').addClass("menu-open");
	        

	        $(document).on('mouseover', 'input, select, textarea',function(){
	        	$(this).next('.hint').css("visibility", "visible");
	        });
	        $(document).on('mouseout', 'input, select, textarea',function(){
	        	$(this).next('.hint').css("visibility", "hidden");
	        });

	        $(document).on('click', '.products-list a',function(){
	        	$('a').removeClass("activelink");
	        	$(this).addClass("activelink");
	        });

	        $(window).on('load',function(){
	        	$("input[type='number']").prop('step','any');
	        	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	        });

	        $('body').on('onkeypress','input[type="number"]',function(evt){
	        	var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	               return false;
	            }
	            return true;
	        });


	        var nowTemp = new Date();
	        var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

	        jQuery.browser = {};
	        (function () {
	            jQuery.browser.msie = false;
	            jQuery.browser.version = 0;
	            if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
	                jQuery.browser.msie = true;
	                jQuery.browser.version = RegExp.$1;
	            }
	        })();

	        $(function(){
	        	
	        	loadLazyImages();
	        	
	        	$("body").on("click",".fc-button",function(){
	        		if($(this).hasClass("fc-month-button")){
	        			setTimeout(function(){ $(".fc-time").hide();}, 500);
		        	}else{
		        		setTimeout(function(){ $(".fc-time").show();}, 500);
		        	}
	        	});
	        	
	        	$("input[type='number']").keydown(function(e) {
	        		var n = (window.Event) ? e.which : e.keyCode;
	        		if(n==38 || n==40) return false;
	        	});
	        	
	        	$("input[type='number']").attr("onmousewheel", "return false;");

	        	if($(".fc-month-button").hasClass("fc-state-active")){
	        		$(".fc-time").hide();
	        	}
	        	
	        	$("body").on("click",".external-event input[type='checkbox']",function(){
	        		$(".fc-time").hide();
	        	});
	        	
	        	$("body").on('click','#closeButton',function(){
	        		$(".modal-dialog").removeAttr('style');
	        		$(".modal-bodyCP").height(400);
	        		$("#modalInfoCP").hide();
	        	});
	        	
	        	$("body").on('click','.close',function(){
	        		$(".modal-dialog").removeAttr('style');
	        		$(".modal-bodyCP").height(400);
	        		$("#modalInfoCP").hide();
	        	});
	        	
	        	var active = $(".active-workrig-user").html();
	        	$(".workrig-users-button>.btn-sm[for='"+active+"']").addClass("active");
	        	
	        	if($('body').hasClass('sidebar-collapse')){
	        		$(".treeview").removeClass("arrow_box");
	        		$(".treeview.active").addClass("arrow_box");
	        	}
	        	
	        	$('body').on('click','.sidebar-toggle',function(){
	        		if($('body').hasClass('sidebar-collapse')){
	        			$(".treeview").removeClass("arrow_box");	
	        			
	        		}else{
	        			$(".treeview").removeClass("arrow_box");
	        			$(".treeview.active").addClass("arrow_box");
	        			//$(".treeview.active").addClass("arrow_box");
	        		}
	        	});
				
	        	$('body').on('click','.sidebar-toggle,#open-dropdown',function(){
	        		loadLazyImages();
	        	});
	        		        	
	        	$(document).on("click",".nav-tabs li",function(e){
	        		//console.log(e);
	        		$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
	        			//console.log(options);
	        			//console.log(originalOptions);
	        			//console.log(jqXHR);
	        			//console.log("----------------");
	        			options.async = false;
	        		});
	        	});
	        	
	        	/* var currentRequests = {};
	        	$(document).on("click",".nav-tabs li",function(e){
	        		console.log(currentRequests);
	        		console.log("----------------");
	        		currentRequests.forEach(function(url){
	        			url.abort();
	        		});
	        		$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
	        			currentRequests[ options.url ] = jqXHR;
		        	});
	        	}); */
	        	$(document).find(".box-tools").parent().attr("data-widget","collapse-full");
	        
	        	$( document ).on('ajaxComplete',function() {
	        		$(document).find(".box-tools").parent().attr("data-widget","collapse-full");
	        		setTimeout(function(){ $(".fc-time").hide();}, 500);
	        		$("input[type='number']").keydown(function(e) {
		        		var n = (window.Event) ? e.which : e.keyCode;
		        		if(n==38 || n==40) return false;
		        	});
		        	
		        	$("input[type='number']").attr("onmousewheel", "return false;");
		        	
		        	//lazy load
		        	loadLazyImages();
	        	});
	        		        	
	        	$('body').on('click','.box-header', function(e) { 
	        	  var e_target = e.target;
	        	  if (e_target !== this){
	        		  if($(e_target).hasClass('box-title') || $(e_target).parent().hasClass('box-title') || $(e_target).attr('data-widget') === "collapse" || 
	        				  $(e_target).hasClass('fa-minus') || $(e_target).hasClass('fa-plus')){
	        			  
	        		  }else{
	        			  e.stopPropagation();
	        		  }
	        	  }
	        	});
	        	$('body').on('keydown','.readonly',function(e) {
        		    e.preventDefault();
        		});
	        	
	        	$('body').on('keydown','.no-press-enter',function(e) {
	        	    if(e.which == 13) {
	        	       return false;
	        	    }
	        	});
	        });
	        
	        function loadLazyImages(){
	        	$("img.lazy").each(function(i,element) {
	        	    if($(element).attr("src") !== $(element).attr("data-original")){
	        	    	$(element).lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	        	    }
	        	});
	        }
	        
	        function fadeForm(form_id){
	        	if($('#'+form_id).find('.there').length === 0){
	        		$('#'+form_id).prepend('<div class="there"><div id="ajaxLoadImage"></div></div>');
	        	}
	        } 

	        function unfadeForm(form_id){
	        	$("#"+form_id).find('.there').remove();
	        }

	        function isNumberKey(evt){
	            var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	               return false;
	            }
	            return true;
	         } 


	        function isOnlyNumberKey(evt) {
	            var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	         		return true;
	            }
	            return false;
	         }

	        function clearField(elementId){
	        	document.getElementById(elementId).value = '';
	        }
	        </script>
    </body>
</html>


