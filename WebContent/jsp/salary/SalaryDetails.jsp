<%@page import="com.konnect.jpms.select.FillSalaryBand"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 
<% String empId = (String)request.getParameter("empId");
    UtilityFunctions uF = new UtilityFunctions();
    String currency = (String)request.getAttribute("currency");
    //System.out.println("SD.jsp");
	
    List<String> alAssignToPercentageHead = (List<String>)request.getAttribute("alAssignToPercentageHead");
	if(alAssignToPercentageHead == null) alAssignToPercentageHead = new ArrayList<String>();
%>  
<style>
    .heading1 {
    	text-align: center;
    	background-color: aliceblue;
    	padding: 1px;
    	font-weight: 900;
    }
    .credit>div, .deduction>div{
    	padding-left: 15px;
    	padding-right: 15px;
    }
    .heading1>h4{
    	font-weight: 600;
    }
    .col2 {
    	display: inline;
    }
    .col1.tdDashLabel {
    	display: inline;
    }
    .details_lables .row{
   	 	border-bottom: 1px solid rgb(243, 243, 243);
    }
    span.tdDashLabel {
   	 	padding-right: 5px;
    }
    .multipleCalType select{
	    margin-top: 5px;
	    margin-bottom: 5px;
    }
    .close{
    position: fixed;
    }
    img.btn_close {
	margin: -45px -55px 0 0;
	}
	.multipleCalType:nth-child(2):nth-child(even) {
    width:50px !important;
	}
	.multipleCalType:nth-child(2):nth-child(odd) {
    width:150px !important;
	}
	.multipleCalType:nth-child(2){
    display:none;
	}
</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
   
    $(document).ready(function(){
    	$("body").on('click','.close',function(){
    		$(".popup_block").hide();
    		$("#fade").hide();
    	});
    });
    
    var checkSalaryHead = '<%=request.getAttribute("checkSalaryHead")%>';
    
        $(function() {
        	
        	//deductProfTax();
        	//changeLabelValue();
        	//changeDeductionLabelValue();
     
        	<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); 
        	for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
        		java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
    	 		show_sub_salary_head1("newSel_"+<%=cinnerlist.get(0)%>);
    	 	<%}%>
    		
    	 	show_sub_salary_head("newSelE");
     		show_sub_salary_head("newSelIncentiveE");
     		show_sub_salary_head("newSelAllowanceE");
     		show_sub_salary_head("newSelD"); 
        	
            $('a.poplight').click(function() {
                var popID = $(this).attr('rel'); //Get Popup Name
                var popURL = $(this).attr('href'); //Get Popup href to define size
    
                //Pull Query & Variables from href URL
                var query= popURL.split('?');
                var dim= query[1].split('&');
                var popWidth = dim[0].split('=')[1]; //Gets the first query string value
    
                //Fade in the Popup and add close button
                $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');
    
                //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
                var popMargTop = ($('#' + popID).height() + 80) / 2;
                var popMargLeft = ($('#' + popID).width() + 80) / 2;
    
                //Apply Margin to Popup
                $('#' + popID).css({
                    'margin-top' : -popMargTop,
                    'margin-left' : -popMargLeft
                });
                
                //Fade in Background
                $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
                $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies
    
                return false;
            });
    
            //Close Popups and Fade Layer
            $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
                $('#fade , .popup_block').fadeOut(function() {
                    $('#fade, a.close').remove();  //fade them both out
                });
                return false;
            });
        });
        
        function showCreditDetails() {	
    		dojo.event.topic.publish("showCreditDetails");
    	} 
        
        function isNumberKey(evt){
           var charCode = (evt.which) ? evt.which : event.keyCode;
           if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
              return false;
           }
           return true;
        }
        
        
        function show_sub_salary_head(selId) {
        	
        	if(document.getElementById(selId).value == 'A') {
        		if(document.getElementById("trMultipleCalType_"+selId.split("newSel_")[1])) {
    		  		document.getElementById("trMultipleCalType_"+selId.split("newSel_")[1]).style.display = "none";
    			}
        		if(document.getElementById("id_headMaxCapAmount_"+selId.split("newSel_")[1])) {
                	document.getElementById("id_headMaxCapAmount_"+selId.split("newSel_")[1]).style.display = "none";
                }
        		if(document.getElementById("trMultiplePercentageCalType_"+selId.split("newSel_")[1])) {
    		  		document.getElementById("trMultiplePercentageCalType_"+selId.split("newSel_")[1]).style.display = "none";
    			}
        		if(document.getElementById("trMultipleCalTypeE")) {
                	document.getElementById("trMultipleCalTypeE").style.display = "none";
                }
        		if(document.getElementById("id_headMaxCapAmountE")) {
                 	document.getElementById("id_headMaxCapAmountE").style.display = "none";
                }
        		if(document.getElementById("trMultiplePercentageCalTypeE")) {
                	document.getElementById("trMultiplePercentageCalTypeE").style.display = "none";
                }
        		if(document.getElementById("trMultiplePercentageCalTypeIncentiveE")) {
                	document.getElementById("trMultiplePercentageCalTypeIncentiveE").style.display = "none";
                }
        		if(document.getElementById("trMultiplePercentageCalTypeD")) {
                	document.getElementById("trMultiplePercentageCalTypeD").style.display = "none";
                }
        		if(document.getElementById("id_headAmountE")) {
                	document.getElementById("id_headAmountE").style.display = "table-row";
                }
        		if(document.getElementById('salaryHead1E')){
    				document.getElementById("salaryHead1E").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead2E')){
    				document.getElementById("salaryHead2E").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead3E')){
    				document.getElementById("salaryHead3E").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead4E')){
    				document.getElementById("salaryHead4E").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead5E')){
    				document.getElementById("salaryHead5E").selectedIndex = "0";
    			}
        		
        		if(document.getElementById("trMultipleCalTypeIncentiveE")) {
                	document.getElementById("trMultipleCalTypeIncentiveE").style.display = "none";
                }
        		if(document.getElementById("id_headMaxCapAmountIncentiveE")) {
                 	document.getElementById("id_headMaxCapAmountIncentiveE").style.display = "none";
                }
        		if(document.getElementById("id_headAmountIncentiveE")) {
                	document.getElementById("id_headAmountIncentiveE").style.display = "table-row";
                }
        		if(document.getElementById('salaryHead1IncentiveE')){
    				document.getElementById("salaryHead1IncentiveE").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead2IncentiveE')){
    				document.getElementById("salaryHead2IncentiveE").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead3IncentiveE')){
    				document.getElementById("salaryHead3IncentiveE").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead4IncentiveE')){
    				document.getElementById("salaryHead4IncentiveE").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead5IncentiveE')){
    				document.getElementById("salaryHead5IncentiveE").selectedIndex = "0";
    			}
        		
        		if(document.getElementById("trMultipleCalTypeD")) {
                	document.getElementById("trMultipleCalTypeD").style.display = "none";
                }
        		if(document.getElementById("id_headMaxCapAmountD")) {
                 	document.getElementById("id_headMaxCapAmountD").style.display = "none";
                }
        		if(document.getElementById("id_headAmountD")) {
                	document.getElementById("id_headAmountD").style.display = "table-row";
                }
        		if(document.getElementById('salaryHead1D')){
    				document.getElementById("salaryHead1D").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead2D')){
    				document.getElementById("salaryHead2D").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead3D')){
    				document.getElementById("salaryHead3D").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead4D')){
    				document.getElementById("salaryHead4D").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead5D')){
    				document.getElementById("salaryHead5D").selectedIndex = "0";
    			}
        		
    			if(document.getElementById("id_salaryHead_"+selId.split("newSel_")[1])) {
            		document.getElementById("id_salaryHead_"+selId.split("newSel_")[1]).style.display = "none";
    			}
    			if(document.getElementById("id_headAmountType_"+selId.split("newSel_")[1])) {
    				document.getElementById("id_headAmountType_"+selId.split("newSel_")[1]).innerHTML = "Amount (<%=uF.showData(currency, "") %>):";
    			}
    			if(document.getElementById("id_headAmount_"+selId.split("newSel_")[1])) {
               		document.getElementById("id_headAmount_"+selId.split("newSel_")[1]).style.display = "table-row";
           		}
            	if(document.getElementById("id_salaryHeadE")) {
            		document.getElementById("id_salaryHeadE").style.display = "none";
            	}
            	if(document.getElementById("id_salaryHeadIncentiveE")) {
            		document.getElementById("id_salaryHeadIncentiveE").style.display = "none"; 
            	} 
            	if(document.getElementById("id_salaryHeadAllowanceE")) {
            		document.getElementById("id_salaryHeadAllowanceE").style.display = "none";
            	} 
    		//	if(document.getElementById("id_headAmountE")) {
    		//		document.getElementById("id_headAmountE").style.display = "none";
            //	}
    			if(document.getElementById("id_salaryHeadD")) {
            		document.getElementById("id_salaryHeadD").style.display = "none";
            	}
    		
    			if(document.getElementById("id_headAmountTypeE")) {
            		document.getElementById("id_headAmountTypeE").innerHTML = "Amount (<%=uF.showData(currency, "") %>):";
            	}
    			if(document.getElementById("id_headAmountIncentiveTypeE")) {
            		document.getElementById("id_headAmountIncentiveTypeE").innerHTML = "Amount (<%=uF.showData(currency, "") %>):";
            	}
    			if(document.getElementById("id_headAmountAllowanceTypeE")) {
            		document.getElementById("id_headAmountAllowanceTypeE").innerHTML = "Amount:";
            	}
    			if(document.getElementById("id_headAmountTypeD")) {
            		document.getElementById("id_headAmountTypeD").innerHTML = "Amount (<%=uF.showData(currency, "") %>):";
            	}
    			if(document.getElementById('headAmountE')){
    				document.getElementById('headAmountE').value = '';
    			}
    			if(document.getElementById('headMaxCapAmountE')){
    				document.getElementById('headMaxCapAmountE').value = '';
    			}
    			if(document.getElementById('headAmountIncentiveE')){
    				document.getElementById('headAmountIncentiveE').value = '';
    			}
    			if(document.getElementById('headMaxCapAmountIncentiveE')){
    				document.getElementById('headMaxCapAmountIncentiveE').value = '';
    			}
    			if(document.getElementById('headAmountAllowanceE')){
    				document.getElementById('headAmountAllowanceE').value = '';
    			}
    			if(document.getElementById('headAmountD')){
    				document.getElementById('headAmountD').value = '';
    			}
    			if(document.getElementById('headMaxCapAmountD')){
    				document.getElementById('headMaxCapAmountD').value = '';
    			}
    			if(document.getElementById('salarySubHeadE')){
    				document.getElementById("salarySubHeadE").selectedIndex = "0";
    			}
    			if(document.getElementById('salarySubHeadIncentiveE')){
    				document.getElementById("salarySubHeadIncentiveE").selectedIndex = "0";
    			}
    			if(document.getElementById('salarySubHeadAllowanceE')){
    				document.getElementById("salarySubHeadAllowanceE").selectedIndex = "0";
    			}
    			if(document.getElementById('id_salaryHead')){
    				document.getElementById("id_salaryHead").selectedIndex = "0";
    			}
    			if(document.getElementById("salarySubHead_"+selId.split("newSel_")[1])) {
            		document.getElementById("salarySubHead_"+selId.split("newSel_")[1]).selectedIndex = "0";
    			}
    			if(document.getElementById("headAmount_"+selId.split("newSel_")[1])) {
            		document.getElementById("headAmount_"+selId.split("newSel_")[1]).value = '';
    			}
    			if(document.getElementById("headMaxCapAmount_"+selId.split("newSel_")[1])) {
            		document.getElementById("headMaxCapAmount_"+selId.split("newSel_")[1]).value = '';
    			}
    			
            } else if(document.getElementById(selId).value == 'P') {
            	if(document.getElementById("trMultipleCalType_"+selId.split("newSel_")[1])) {
    		  		document.getElementById("trMultipleCalType_"+selId.split("newSel_")[1]).style.display = "none";
    			}
            	if(document.getElementById("id_headMaxCapAmount_"+selId.split("newSel_")[1])) {
                	document.getElementById("id_headMaxCapAmount_"+selId.split("newSel_")[1]).style.display = "table-row";
                }
            	if(document.getElementById("trMultiplePercentageCalType_"+selId.split("newSel_")[1])) {
    		  		document.getElementById("trMultiplePercentageCalType_"+selId.split("newSel_")[1]).style.display = "table-row";
    			}
            	if(document.getElementById("id_salaryHead_"+selId.split("newSel_")[1])) {
            		document.getElementById("id_salaryHead_"+selId.split("newSel_")[1]).style.display = "none";
    			}
    			if(document.getElementById("id_headAmountType_"+selId.split("newSel_")[1])) {
    				document.getElementById("id_headAmountType_"+selId.split("newSel_")[1]).innerHTML = 'Percentage';
    			}
    			if(document.getElementById("id_headAmount_"+selId.split("newSel_")[1])) {
               		document.getElementById("id_headAmount_"+selId.split("newSel_")[1]).style.display = "table-row";
           		}
    			
            	if(document.getElementById("id_salaryHeadE")) {
            		document.getElementById("id_salaryHeadE").style.display = "none";
            	}
            	if(document.getElementById("id_salaryHeadIncentiveE")) {
            		document.getElementById("id_salaryHeadIncentiveE").style.display = "none";
            	} 
            	if(document.getElementById("id_salaryHeadAllowanceE")) {
            		document.getElementById("id_salaryHeadAllowanceE").style.display = "none";
            	} 
    		//	if(document.getElementById("id_headAmountE")) {
    		//		document.getElementById("id_headAmountE").style.display = "none";
            //	}
    			if(document.getElementById("id_salaryHeadD")) {
            		document.getElementById("id_salaryHeadD").style.display = "none";
            	}
    		
    			if(document.getElementById("id_headAmountTypeE")) {
            		document.getElementById("id_headAmountTypeE").innerHTML = 'Percentage';
            	}
    			if(document.getElementById("id_headAmountIncentiveTypeE")) {
            		document.getElementById("id_headAmountIncentiveTypeE").innerHTML = 'Percentage';
            	}
    			/* if(document.getElementById("id_headAmountAllowanceTypeE")) {
            		document.getElementById("id_headAmountAllowanceTypeE").innerHTML = "Amount:";
            	} */
    			if(document.getElementById("id_headAmountTypeD")) {
            		document.getElementById("id_headAmountTypeD").innerHTML = 'Percentage';
            	}
    			if(document.getElementById('headAmountE')){
    				document.getElementById('headAmountE').value = '';
    			}
    			if(document.getElementById('headMaxCapAmountE')){
    				document.getElementById('headMaxCapAmountE').value = '';
    			}
    			if(document.getElementById('headAmountIncentiveE')){
    				document.getElementById('headAmountIncentiveE').value = '';
    			}
    			if(document.getElementById('headMaxCapAmountIncentiveE')){
    				document.getElementById('headMaxCapAmountIncentiveE').value = '';
    			}
    			if(document.getElementById('headAmountAllowanceE')){
    				document.getElementById('headAmountAllowanceE').value = '';
    			}
    			if(document.getElementById('headAmountD')){
    				document.getElementById('headAmountD').value = '';
    			}
    			if(document.getElementById('headMaxCapAmountD')){
    				document.getElementById('headMaxCapAmountD').value = '';
    			}
    			if(document.getElementById('salarySubHeadE')){
    				document.getElementById("salarySubHeadE").selectedIndex = "0";
    			}
    			if(document.getElementById('salarySubHeadIncentiveE')){
    				document.getElementById("salarySubHeadIncentiveE").selectedIndex = "0";
    			}
    			if(document.getElementById('salarySubHeadAllowanceE')){
    				document.getElementById("salarySubHeadAllowanceE").selectedIndex = "0";
    			}
    			if(document.getElementById('id_salaryHead')){
    				document.getElementById("id_salaryHead").selectedIndex = "0";
    			}
    			if(document.getElementById("salarySubHead_"+selId.split("newSel_")[1])) {
            		document.getElementById("salarySubHead_"+selId.split("newSel_")[1]).selectedIndex = "0";
    			}
    			/* if(document.getElementById("headAmount_"+selId.split("newSel_")[1])) {
            		document.getElementById("headAmount_"+selId.split("newSel_")[1]).value = '';
    			} */
            	
           		if(document.getElementById("trMultipleCalTypeE")) {
                	document.getElementById("trMultipleCalTypeE").style.display = "none";
                }
           		if(document.getElementById("trMultiplePercentageCalTypeE")) {
                	document.getElementById("trMultiplePercentageCalTypeE").style.display = "table-row";
                }
           		if(document.getElementById("trMultiplePercentageCalTypeIncentiveE")) {
                	document.getElementById("trMultiplePercentageCalTypeIncentiveE").style.display = "table-row";
                }
           		if(document.getElementById("trMultiplePercentageCalTypeD")) {
                	document.getElementById("trMultiplePercentageCalTypeD").style.display = "table-row";
                }
        		if(document.getElementById("id_headAmountE")) {
                	document.getElementById("id_headAmountE").style.display = "table-row";
                }
        		if(document.getElementById("id_headMaxCapAmountE")) {
                 	document.getElementById("id_headMaxCapAmountE").style.display = "table-row";
                }
        		if(document.getElementById('salaryHead1E')){
    				document.getElementById("salaryHead1E").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead2E')){
    				document.getElementById("salaryHead2E").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead3E')){
    				document.getElementById("salaryHead3E").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead4E')){
    				document.getElementById("salaryHead4E").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead5E')){
    				document.getElementById("salaryHead5E").selectedIndex = "0";
    			}
        		
        		if(document.getElementById("trMultipleCalTypeIncentiveE")) {
                	document.getElementById("trMultipleCalTypeIncentiveE").style.display = "none";
                }
        		if(document.getElementById("id_headAmountIncentiveE")) {
                	document.getElementById("id_headAmountIncentiveE").style.display = "table-row";
                }
        		if(document.getElementById("id_headMaxCapAmountIncentiveE")) {
                 	document.getElementById("id_headMaxCapAmountIncentiveE").style.display = "table-row";
                }
        		if(document.getElementById('salaryHead1IncentiveE')){
    				document.getElementById("salaryHead1IncentiveE").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead2IncentiveE')){
    				document.getElementById("salaryHead2IncentiveE").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead3IncentiveE')){
    				document.getElementById("salaryHead3IncentiveE").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead4IncentiveE')){
    				document.getElementById("salaryHead4IncentiveE").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead5IncentiveE')){
    				document.getElementById("salaryHead5IncentiveE").selectedIndex = "0";
    			}
        		
        		if(document.getElementById("trMultipleCalTypeD")) {
                	document.getElementById("trMultipleCalTypeD").style.display = "none";
                }
        		if(document.getElementById("id_headMaxCapAmountD")) {
                 	document.getElementById("id_headMaxCapAmountD").style.display = "table-row";
                }
        		if(document.getElementById("id_headAmountD")) {
                	document.getElementById("id_headAmountD").style.display = "table-row";
                }
        		if(document.getElementById('salaryHead1D')){
    				document.getElementById("salaryHead1D").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead2D')){
    				document.getElementById("salaryHead2D").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead3D')){
    				document.getElementById("salaryHead3D").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead4D')){
    				document.getElementById("salaryHead4D").selectedIndex = "0";
    			}
        		if(document.getElementById('salaryHead5D')){
    				document.getElementById("salaryHead5D").selectedIndex = "0";
    			}
        		
        		if(document.getElementById("headAmount_"+selId.split("newSel_")[1])) {
            		document.getElementById("headAmount_"+selId.split("newSel_")[1]).value = '';
    			}
    			if(document.getElementById("headMaxCapAmount_"+selId.split("newSel_")[1])) {
            		document.getElementById("headMaxCapAmount_"+selId.split("newSel_")[1]).value = '';
    			}
           	}
        }
        
     function show_sub_salary_head1(selId) {
     	
     	if(document.getElementById(selId).value == 'A') {
     		if(document.getElementById("id_headAmount_"+selId.split("newSel_")[1])) {
           		document.getElementById("id_headAmount_"+selId.split("newSel_")[1]).style.display = "table-row";
       		}
     		/* if(document.getElementById("id_headMaxCapAmount_"+selId.split("newSel_")[1])) {
            	document.getElementById("id_headMaxCapAmount_"+selId.split("newSel_")[1]).style.display = "none";
            } */
     		if(document.getElementById("trMultipleCalType_"+selId.split("newSel_")[1])) {
 		  		document.getElementById("trMultipleCalType_"+selId.split("newSel_")[1]).style.display = "none";
 			}
     		if(document.getElementById("trMultiplePercentageCalType_"+selId.split("newSel_")[1])) {
 		  		document.getElementById("trMultiplePercentageCalType_"+selId.split("newSel_")[1]).style.display = "none";
 			}
     		
     		if(document.getElementById("trMultipleCalTypeE")) {
             	document.getElementById("trMultipleCalTypeE").style.display = "none";
            }
     		if(document.getElementById("trMultiplePercentageCalTypeE")) {
             	document.getElementById("trMultiplePercentageCalTypeE").style.display = "none";
            }
     		if(document.getElementById("trMultiplePercentageCalTypeIncentiveE")) {
             	document.getElementById("trMultiplePercentageCalTypeIncentiveE").style.display = "none";
             }
     		if(document.getElementById("trMultiplePercentageCalTypeD")) {
             	document.getElementById("trMultiplePercentageCalTypeD").style.display = "none";
             }
     		if(document.getElementById("id_headAmountE")) {
             	document.getElementById("id_headAmountE").style.display = "table-row";
             }
     		if(document.getElementById('salaryHead1E')){
 				document.getElementById("salaryHead1E").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead2E')){
 				document.getElementById("salaryHead2E").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead3E')){
 				document.getElementById("salaryHead3E").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead4E')){
 				document.getElementById("salaryHead4E").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead5E')){
 				document.getElementById("salaryHead5E").selectedIndex = "0";
 			}
     		
     		if(document.getElementById("trMultipleCalTypeIncentiveE")) {
             	document.getElementById("trMultipleCalTypeIncentiveE").style.display = "none";
            }
     		if(document.getElementById("id_headAmountIncentiveE")) {
             	document.getElementById("id_headAmountIncentiveE").style.display = "table-row";
            }
     		if(document.getElementById('salaryHead1IncentiveE')){
 				document.getElementById("salaryHead1IncentiveE").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead2IncentiveE')){
 				document.getElementById("salaryHead2IncentiveE").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead3IncentiveE')){
 				document.getElementById("salaryHead3IncentiveE").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead4IncentiveE')){
 				document.getElementById("salaryHead4IncentiveE").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead5IncentiveE')){
 				document.getElementById("salaryHead5IncentiveE").selectedIndex = "0";
 			}
     		
     		if(document.getElementById("trMultipleCalTypeD")) {
             	document.getElementById("trMultipleCalTypeD").style.display = "none";
            }
     		if(document.getElementById("id_headAmountD")) {
             	document.getElementById("id_headAmountD").style.display = "table-row";
             }
     		if(document.getElementById('salaryHead1D')){
 				document.getElementById("salaryHead1D").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead2D')){
 				document.getElementById("salaryHead2D").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead3D')){
 				document.getElementById("salaryHead3D").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead4D')){
 				document.getElementById("salaryHead4D").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead5D')){
 				document.getElementById("salaryHead5D").selectedIndex = "0";
 			}
     		
 			if(document.getElementById("id_salaryHead_"+selId.split("newSel_")[1])) {
         		document.getElementById("id_salaryHead_"+selId.split("newSel_")[1]).style.display = "none";
 			}
 			if(document.getElementById("id_headAmountType_"+selId.split("newSel_")[1])) {
 				document.getElementById("id_headAmountType_"+selId.split("newSel_")[1]).innerHTML = "Amount (<%=uF.showData(currency, "") %>):";
 			}
         	if(document.getElementById("id_salaryHeadE")) {
         		document.getElementById("id_salaryHeadE").style.display = "none";
         	}
         	if(document.getElementById("id_salaryHeadIncentiveE")) {
         		document.getElementById("id_salaryHeadIncentiveE").style.display = "none";
         	}
         	if(document.getElementById("id_salaryHeadAllowanceE")) {
         		document.getElementById("id_salaryHeadAllowanceE").style.display = "none";
         	}
 		//	if(document.getElementById("id_headAmountE")) {
 		//		document.getElementById("id_headAmountE").style.display = "none";
         //	}
 			if(document.getElementById("id_salaryHeadD")) {
         		document.getElementById("id_salaryHeadD").style.display = "none";
         	}
 		
 			if(document.getElementById("id_headAmountTypeE")) {
         		document.getElementById("id_headAmountTypeE").innerHTML = "Amount (<%=uF.showData(currency, "") %>):";
         	}
 			if(document.getElementById("id_headAmountIncentiveTypeE")) {
         		document.getElementById("id_headAmountIncentiveTypeE").innerHTML = "Amount (<%=uF.showData(currency, "") %>):";
         	}
 			/* if(document.getElementById("id_headAmountAllowanceTypeE")) {
         		document.getElementById("id_headAmountAllowanceTypeE").innerHTML = "Amount:";
         	} */
 			if(document.getElementById("id_headAmountTypeD")) {
         		document.getElementById("id_headAmountTypeD").innerHTML = "Amount (<%=uF.showData(currency, "") %>):";
         	}
 			if(document.getElementById('headAmountE')){
 				document.getElementById('headAmountE').value = '';
 			}
 			if(document.getElementById('headAmountIncentiveE')){
 				document.getElementById('headAmountIncentiveE').value = '';
 			}
 			if(document.getElementById('headAmountAllowanceE')){
 				document.getElementById('headAmountAllowanceE').value = '';
 			}
 			if(document.getElementById('headAmountD')){
 				document.getElementById('headAmountD').value = '';
 			}
 			if(document.getElementById('salarySubHeadE')){
 				document.getElementById("salarySubHeadE").selectedIndex = "0";
 			}
 			if(document.getElementById('salarySubHeadIncentiveE')){
 				document.getElementById("salarySubHeadIncentiveE").selectedIndex = "0";
 			}
 			if(document.getElementById('salarySubHeadAllowanceE')){
 				document.getElementById("salarySubHeadAllowanceE").selectedIndex = "0";
 			}
 			if(document.getElementById('id_salaryHead')){
 				document.getElementById("id_salaryHead").selectedIndex = "0";
 			}
 			if(document.getElementById("salarySubHead_"+selId.split("newSel_")[1])) {
         		document.getElementById("salarySubHead_"+selId.split("newSel_")[1]).selectedIndex = "0";
 			}
 						
         } else if(document.getElementById(selId).value == 'P') {
 			if(document.getElementById("id_headAmount_"+selId.split("newSel_")[1])) {
 		    	document.getElementById("id_headAmount_"+selId.split("newSel_")[1]).style.display = "table-row";
 		 	}
 			/* if(document.getElementById("id_headMaxCapAmount_"+selId.split("newSel_")[1])) {
            	document.getElementById("id_headMaxCapAmount_"+selId.split("newSel_")[1]).style.display = "table-row";
            } */
 		 	if(document.getElementById("trMultipleCalType_"+selId.split("newSel_")[1])) {
 		  		document.getElementById("trMultipleCalType_"+selId.split("newSel_")[1]).style.display = "none";
 			}	  	
 		 	if(document.getElementById("trMultiplePercentageCalType_"+selId.split("newSel_")[1])) {
 		  		document.getElementById("trMultiplePercentageCalType_"+selId.split("newSel_")[1]).style.display = "table-row";
 			}
         	if(document.getElementById("id_salaryHead_"+selId.split("newSel_")[1])) {
         		document.getElementById("id_salaryHead_"+selId.split("newSel_")[1]).style.display = "none";
 			}
 			if(document.getElementById("id_headAmountType_"+selId.split("newSel_")[1])) {
 				document.getElementById("id_headAmountType_"+selId.split("newSel_")[1]).innerHTML = 'Percentage';
 			}
         	if(document.getElementById("id_salaryHeadE")) {
         		document.getElementById("id_salaryHeadE").style.display = "none";
         	}
         	if(document.getElementById("id_salaryHeadIncentiveE")) {
         		document.getElementById("id_salaryHeadIncentiveE").style.display = "none";
         	} 
         	if(document.getElementById("id_salaryHeadAllowanceE")) {
         		document.getElementById("id_salaryHeadAllowanceE").style.display = "none";
         	} 
 		//	if(document.getElementById("id_headAmountE")) {
 		//		document.getElementById("id_headAmountE").style.display = "none";
         //	}
 			
 			if(document.getElementById("id_salaryHeadD")) {
         		document.getElementById("id_salaryHeadD").style.display = "none";
         	}
 		
 			if(document.getElementById("id_headAmountTypeE")) {
         		document.getElementById("id_headAmountTypeE").innerHTML = 'Percentage';
         	}
 			if(document.getElementById("id_headAmountIncentiveTypeE")) {
         		document.getElementById("id_headAmountIncentiveTypeE").innerHTML = 'Percentage';
         	}
 			/* if(document.getElementById("id_headAmountAllowanceTypeE")) {
         		document.getElementById("id_headAmountAllowanceTypeE").innerHTML = "Amount:";
         	} */
 			if(document.getElementById("id_headAmountTypeD")) {
         		document.getElementById("id_headAmountTypeD").innerHTML = 'Percentage';
         	}
 			if(document.getElementById('headAmountE')){
 				document.getElementById('headAmountE').value = '';
 			}
 			if(document.getElementById('headAmountIncentiveE')){
 				document.getElementById('headAmountIncentiveE').value = '';
 			}
 			if(document.getElementById('headAmountAllowanceE')){
 				document.getElementById('headAmountAllowanceE').value = '';
 			}
 			if(document.getElementById('headAmountD')){
 				document.getElementById('headAmountD').value = '';
 			}
 			if(document.getElementById('salarySubHeadE')){
 				document.getElementById("salarySubHeadE").selectedIndex = "0";
 			}
 			if(document.getElementById('salarySubHeadIncentiveE')){
 				document.getElementById("salarySubHeadIncentiveE").selectedIndex = "0";
 			}
 			if(document.getElementById('salarySubHeadAllowanceE')){
 				document.getElementById("salarySubHeadAllowanceE").selectedIndex = "0";
 			}
 			if(document.getElementById('id_salaryHead')){
 				document.getElementById("id_salaryHead").selectedIndex = "0";
 			}
 			if(document.getElementById("salarySubHead_"+selId.split("newSel_")[1])) {
         		document.getElementById("salarySubHead_"+selId.split("newSel_")[1]).selectedIndex = "0";
 			}
 			/* if(document.getElementById("headAmount_"+selId.split("newSel_")[1])) {
         		document.getElementById("headAmount_"+selId.split("newSel_")[1]).value = '';
 			} */
         	
        	if(document.getElementById("trMultipleCalTypeE")) {
             	document.getElementById("trMultipleCalTypeE").style.display = "table-row";
            }
        	if(document.getElementById("trMultiplePercentageCalTypeE")) {
             	document.getElementById("trMultiplePercentageCalTypeE").style.display = "table-row";
             }
        		if(document.getElementById("trMultiplePercentageCalTypeIncentiveE")) {
             	document.getElementById("trMultiplePercentageCalTypeIncentiveE").style.display = "table-row";
             }
        		if(document.getElementById("trMultiplePercentageCalTypeD")) {
             	document.getElementById("trMultiplePercentageCalTypeD").style.display = "table-row";
             }
     		if(document.getElementById("id_headAmountE")) {
             	document.getElementById("id_headAmountE").style.display = "table-row";
             }
     		if(document.getElementById('salaryHead1E')){
 				document.getElementById("salaryHead1E").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead2E')){
 				document.getElementById("salaryHead2E").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead3E')){
 				document.getElementById("salaryHead3E").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead4E')){
 				document.getElementById("salaryHead4E").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead5E')){
 				document.getElementById("salaryHead5E").selectedIndex = "0";
 			}
     		
     		if(document.getElementById("trMultipleCalTypeIncentiveE")) {
             	document.getElementById("trMultipleCalTypeIncentiveE").style.display = "table-row";
            }
     		if(document.getElementById("id_headAmountIncentiveE")) {
             	document.getElementById("id_headAmountIncentiveE").style.display = "table-row";
            }
     		
     		if(document.getElementById('salaryHead1IncentiveE')){
 				document.getElementById("salaryHead1IncentiveE").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead2IncentiveE')){
 				document.getElementById("salaryHead2IncentiveE").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead3IncentiveE')){
 				document.getElementById("salaryHead3IncentiveE").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead4IncentiveE')){
 				document.getElementById("salaryHead4IncentiveE").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead5IncentiveE')){
 				document.getElementById("salaryHead5IncentiveE").selectedIndex = "0";
 			}
     		
     		if(document.getElementById("trMultipleCalTypeD")) {
             	document.getElementById("trMultipleCalTypeD").style.display = "table-row";
            }
     		if(document.getElementById("id_headAmountD")) {
             	document.getElementById("id_headAmountD").style.display = "table-row";
            }
     		if(document.getElementById('salaryHead1D')){
 				document.getElementById("salaryHead1D").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead2D')){
 				document.getElementById("salaryHead2D").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead3D')){
 				document.getElementById("salaryHead3D").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead4D')){
 				document.getElementById("salaryHead4D").selectedIndex = "0";
 			}
     		if(document.getElementById('salaryHead5D')){
 				document.getElementById("salaryHead5D").selectedIndex = "0";
 			}
        	}
     }
     
     function resetMulPerCal(strMulPerCalFormula,spanMulCalPercentage){ 
    		document.getElementById(spanMulCalPercentage).innerHTML = '';
    		document.getElementById(strMulPerCalFormula).value = '';
    	 } 
    	 
    	 function addMulPerCal(type,bracketMulP,salaryHeadMulP,strMulPerCalFormula,spanMulCalPercentage){
    		 var mulPerFormula = document.getElementById(strMulPerCalFormula).value;
    		 var spanMulPerFormula = document.getElementById(spanMulCalPercentage).innerHTML;
    		 if (type == '1'){
    			var bracketMulP = document.getElementById(bracketMulP);
    			var bracketMulPIndex = bracketMulP.selectedIndex;
    			var bracketMulPVal = bracketMulP.options[bracketMulPIndex].value;
    			var bracketMulPText = bracketMulP.options[bracketMulPIndex].text;
    			if(bracketMulPVal.trim() !=''){
    				if(mulPerFormula.trim()==''){
    					mulPerFormula = ','+bracketMulPVal.trim()+',';
    					spanMulPerFormula = bracketMulPText.trim();
    				} else {
    					mulPerFormula += bracketMulPVal.trim()+',';
    					spanMulPerFormula += bracketMulPText.trim();
    				}
    				document.getElementById(strMulPerCalFormula).value = mulPerFormula;
    				document.getElementById(spanMulCalPercentage).innerHTML = spanMulPerFormula;
    			}
    		 } else if (type == '2'){
    			var salaryHeadMulP = document.getElementById(salaryHeadMulP);
    			var salaryHeadMulPIndex = salaryHeadMulP.selectedIndex;
    			var salaryHeadMulPVal = salaryHeadMulP.options[salaryHeadMulPIndex].value;
    			var salaryHeadMulPText = salaryHeadMulP.options[salaryHeadMulPIndex].text;
    			if(isInt(salaryHeadMulPVal)){
    				if(mulPerFormula.trim()==''){
    					mulPerFormula = ','+salaryHeadMulPVal.trim()+',';
    					spanMulPerFormula = salaryHeadMulPText.trim();
    				} else {
    					mulPerFormula += salaryHeadMulPVal.trim()+',';
    					spanMulPerFormula += salaryHeadMulPText.trim();
    				}
    				document.getElementById(strMulPerCalFormula).value = mulPerFormula;
    				document.getElementById(spanMulCalPercentage).innerHTML = spanMulPerFormula;
    			}
    		 }
    	 }
    	 
    	function isInt(n){
    		return n != "" && !isNaN(n) && Math.round(n) == n;
    	}
    	function isFloat(n){
    		return n != "" && !isNaN(n) && Math.round(n) != n;
    	}

    
    	function removeStatutoryField(x,removeId) {
    		if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
    			var userscreen = document.getElementById("userscreen").value;
            	var navigationId = document.getElementById("navigationId").value;
            	var toPage = document.getElementById("toPage").value;
            	
    			window.location="SalaryDetails.action?removeId=" +removeId+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage
					+"&strOrg=<%=request.getAttribute("strOrg")%>&level=<%=request.getAttribute("level")%>&salaryBand=<%=request.getAttribute("salaryBand")%>"; 
      			return true;
      		} else {
    			x.checked = true;
      			return false;
      		}
    	}
    	
    	function removeStatutoryFieldByGrade(x,removeId) {
    		if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
    			var userscreen = document.getElementById("userscreen").value;
            	var navigationId = document.getElementById("navigationId").value;
            	var toPage = document.getElementById("toPage").value;
    			
    			window.location="SalaryDetails.action?removeId=" +removeId+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage+"&strOrg=<%=request.getAttribute("strOrg")%>&level=<%=request.getAttribute("level")%>&strGrade=<%=request.getAttribute("strGrade")%>"; 
      			return true;
      		} else {
    			x.checked = true;
      			return false;
      		}
    	}
    	
    	function removeField(removeId) {
    		if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
    			var userscreen = document.getElementById("userscreen").value;
            	var navigationId = document.getElementById("navigationId").value;
            	var toPage = document.getElementById("toPage").value;
            	
    			window.location="SalaryDetails.action?removeId=" +removeId+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage
   					+"&strOrg=<%=request.getAttribute("strOrg")%>&level=<%=request.getAttribute("level")%>&salaryBand=<%=request.getAttribute("salaryBand")%>"; 
      			return true;
      		} else {
      			return false;
      		}
    	}
    	
    	function removeFieldByGrade(removeId) {
    		if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
    			var userscreen = document.getElementById("userscreen").value;
            	var navigationId = document.getElementById("navigationId").value;
            	var toPage = document.getElementById("toPage").value;
            	
    			window.location="SalaryDetails.action?removeId="+removeId+"&userscreen="+userscreen+"&navigationId="+navigationId+"&toPage="+toPage+"&strOrg=<%=request.getAttribute("strOrg")%>&level=<%=request.getAttribute("level")%>&strGrade=<%=request.getAttribute("strGrade")%>"; 
      			return true;
      		} else {
      			return false;
      		}
    	}
    
    
    function showTaxExemption(id){
		//alert("id=====>"+document.getElementById(id).checked);  
		if(document.getElementById(id).checked==true){
			if(document.getElementById("id_isTaxExemptE")) {
				document.getElementById("id_isTaxExemptE").style.display = "table-row";
        	}
			if(document.getElementById("id_isTaxExemptIncentiveE")) {
				document.getElementById("id_isTaxExemptIncentiveE").style.display = "table-row";
        	}
			if(document.getElementById("id_isTaxExemptAllowanceE")) {
				document.getElementById("id_isTaxExemptAllowanceE").style.display = "table-row";
        	}
			if(document.getElementById("id_isTaxExemptD")) {
				document.getElementById("id_isTaxExemptD").style.display = "table-row";
        	}
			if(document.getElementById("id_isTaxExempt_"+id.split("isCTCVariable_")[1])) {
           		document.getElementById("id_isTaxExempt_"+id.split("isCTCVariable_")[1]).style.display = "table-row";
       		}
       		
			
		} else {
			if(document.getElementById("id_isTaxExemptE")) {
				document.getElementById("id_isTaxExemptE").style.display = "none";
        	}
			if(document.getElementById("id_isTaxExemptIncentiveE")) {
				document.getElementById("id_isTaxExemptIncentiveE").style.display = "none";
        	}
			if(document.getElementById("id_isTaxExemptAllowanceE")) {
				document.getElementById("id_isTaxExemptAllowanceE").style.display = "none";
        	}
			if(document.getElementById("id_isTaxExemptD")) {
				document.getElementById("id_isTaxExemptD").style.display = "none";
        	}
			if(document.getElementById("id_isTaxExempt_"+id.split("isCTCVariable_")[1])) {
           		document.getElementById("id_isTaxExempt_"+id.split("isCTCVariable_")[1]).style.display = "none";
       		}
		}
	}
    
	function showDefaultAmount(id){ 
		
		if(document.getElementById(id).checked==true){
			if(document.getElementById("id_headAmountAllowanceE")) {
				document.getElementById("id_headAmountAllowanceE").style.display = "table-row";
        	}
			
			if(document.getElementById("id_headAmountAllowance_"+id.split("isDefaultCalculateAllowance_")[1])) {
           		document.getElementById("id_headAmountAllowance_"+id.split("isDefaultCalculateAllowance_")[1]).style.display = "table-row";
       		}
			
		} else {
			if(document.getElementById("id_headAmountAllowanceE")) {
				document.getElementById("id_headAmountAllowanceE").style.display = "none";
        	}
			
			if(document.getElementById("id_headAmountAllowance_"+id.split("isDefaultCalculateAllowance_")[1])) {
           		document.getElementById("id_headAmountAllowance_"+id.split("isDefaultCalculateAllowance_")[1]).style.display = "none";
       		}
		}
	}
    
    function submitForm(type){
    	if(type == '1'){
    		document.getElementById("level").selectedIndex = "0";
    	} 
    	//document.frm.submit(); 
    	var userscreen = document.getElementById("userscreen").value;
    	var navigationId = document.getElementById("navigationId").value;
    	var toPage = document.getElementById("toPage").value;
    	var org = document.getElementById("strOrg").value;
    	var level = document.getElementById("level").value;
    	var salaryBand = '';
    	if(document.getElementById("salaryBand")) {
    		salaryBand = document.getElementById("salaryBand").value;
    	}
    	
    	window.location='MyDashboard.action?strOrg='+org+'&strLevel='+level+'&salaryBand='+salaryBand+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
    }
    
    function submitFormBygrade(type){
    	if(type == '1'){
    		document.getElementById("level").selectedIndex = "0";
    		document.getElementById("strGrade").selectedIndex = "0";
    	} else if(type == '2'){
    		document.getElementById("strGrade").selectedIndex = "0";
    	} 
    	//document.frm.submit();
    	var userscreen = document.getElementById("userscreen").value;
    	var navigationId = document.getElementById("navigationId").value;
    	var toPage = document.getElementById("toPage").value;
    	var org = document.getElementById("strOrg").value;
    	var level = document.getElementById("level").value;
    	var strGrade = document.getElementById("strGrade").value;
    	
    	window.location='MyDashboard.action?strOrg='+org+'&strLevel='+level+'&strGrade='+strGrade+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
    }
    
    function changeSalaryBasis(id,salId, orgId, levelId, salaryBand) {
    	if(parseInt(salId) == <%=IConstants.CTC%>){
    		document.getElementById("strBasicAmount").value='';
    		if(id == 1){
    			alert("Already added ctc as basis of salary structure.");
    			document.getElementById("strCtcAmount").value='';
    		} else {
    			var ctcAmt = document.getElementById("strCtcAmount").value;
    			if(parseFloat(ctcAmt) > 0){
    				if(confirm('Are you sure, you want to add ctc as basis of salary structure?')){ 
    					var userscreen = document.getElementById("userscreen").value;
    			    	var navigationId = document.getElementById("navigationId").value;
    			    	var toPage = document.getElementById("toPage").value;
    			    	
    					window.location='SalaryDetails.action?operation=SALBASIS&SALID='+salId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&salaryBand='+salaryBand+'&strAmt='+ctcAmt;
    				} else {
    					if(id == 1){
    						document.getElementById("ctc").checked = true;
    					} else if(id == 2){
    						document.getElementById("basic").checked = true;
    					} else {
    						document.getElementById("other").checked = true;
    					}
    					document.getElementById("strCtcAmount").value = '';
    				}
    			} else {
    				alert("Please, Enter the amount.");
    				if(id == 1){
    					document.getElementById("ctc").checked = true;
    				} else if(id == 2){
    					document.getElementById("basic").checked = true;
    				} else {
    					document.getElementById("other").checked = true;
    				}
    				document.getElementById("strCtcAmount").value = '';
    			}
    		}
    	} else if(parseInt(salId) == <%=IConstants.BASIC%>){
    		document.getElementById("strCtcAmount").value = '';
    		if(id == 2){
    			alert("Already added basic as basis of salary structure.");
    			document.getElementById("strBasicAmount").value='';
    		} else {
    			var basicAmt = document.getElementById("strBasicAmount").value;
    			if(parseFloat(basicAmt) > 0){
    				if(confirm('Are you sure, you want to add basic as basis of salary structure?')){ 
    					var userscreen = document.getElementById("userscreen").value;
    			    	var navigationId = document.getElementById("navigationId").value;
    			    	var toPage = document.getElementById("toPage").value;
    			    	
    					window.location='SalaryDetails.action?operation=SALBASIS&SALID='+salId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&salaryBand='+salaryBand+'&strAmt='+basicAmt;
    				} else {
    					if(id == 1){
    						document.getElementById("ctc").checked = true;
    					} else if(id == 2){
    						document.getElementById("basic").checked = true;
    					} else {
    						document.getElementById("other").checked = true;
    					}
    					document.getElementById("strBasicAmount").value='';
    				}
    			} else {
    				alert("Please, Enter the amount.");
    				if(id == 1){
    					document.getElementById("ctc").checked = true;
    				} else if(id == 2){
    					document.getElementById("basic").checked = true;
    				} else {
    					document.getElementById("other").checked = true;
    				}
    				document.getElementById("strBasicAmount").value='';
    			}
    		}
    	} else if(parseInt(salId) == -1){
    		document.getElementById("strCtcAmount").value='';
    		document.getElementById("strBasicAmount").value='';
    		
    		if(id == 3){
    			alert("Already added other as basis of salary structure.");
    		} else {
    			if(confirm('Are you sure, you want to add other as basis of salary structure?')){ 
    				var userscreen = document.getElementById("userscreen").value;
    		    	var navigationId = document.getElementById("navigationId").value;
    		    	var toPage = document.getElementById("toPage").value;
    		    	
    				window.location='SalaryDetails.action?operation=SALBASIS&SALID=-1&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&salaryBand='+salaryBand;
    			} else {
    				if(id == 1){
    					document.getElementById("ctc").checked = true;
    				} else if(id == 2){
    					document.getElementById("basic").checked = true;
    				} else {
    					document.getElementById("other").checked = true;
    				}
    			}
    		}
    	}
    }
    
    function changeSalaryBasisByGrade(id,salId, orgId, levelId, gradeId){
    	if(parseInt(salId) == <%=IConstants.CTC%>){
    		document.getElementById("strBasicAmount").value='';
    		if(id == 1){
    			alert("Already added ctc as basis of salary structure.");
    			document.getElementById("strCtcAmount").value='';
    		} else {
    			var ctcAmt = document.getElementById("strCtcAmount").value;
    			if(parseFloat(ctcAmt) > 0){
    				if(confirm('Are you sure, you want to add ctc as basis of salary structure?')){ 
    					var userscreen = document.getElementById("userscreen").value;
    			    	var navigationId = document.getElementById("navigationId").value;
    			    	var toPage = document.getElementById("toPage").value;
    			    	
    					window.location='SalaryDetails.action?operation=SALBASIS&SALID='+salId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&strAmt='+ctcAmt+'&strGrade='+gradeId;
    				} else {
    					if(id == 1){
    						document.getElementById("ctc").checked = true;
    					} else if(id == 2){
    						document.getElementById("basic").checked = true;
    					} else {
    						document.getElementById("other").checked = true;
    					}
    					document.getElementById("strCtcAmount").value = '';
    				}
    			} else {
    				alert("Please, Enter the amount.");
    				if(id == 1){
    					document.getElementById("ctc").checked = true;
    				} else if(id == 2){
    					document.getElementById("basic").checked = true;
    				} else {
    					document.getElementById("other").checked = true;
    				}
    				document.getElementById("strCtcAmount").value = '';
    			}
    		}
    	} else if(parseInt(salId) == <%=IConstants.BASIC%>){
    		document.getElementById("strCtcAmount").value = '';
    		if(id == 2){
    			alert("Already added basic as basis of salary structure.");
    			document.getElementById("strBasicAmount").value='';
    		} else {
    			var basicAmt = document.getElementById("strBasicAmount").value;
    			if(parseFloat(basicAmt) > 0){
    				if(confirm('Are you sure, you want to add basic as basis of salary structure?')){ 
    					var userscreen = document.getElementById("userscreen").value;
    			    	var navigationId = document.getElementById("navigationId").value;
    			    	var toPage = document.getElementById("toPage").value;
    			    	
    					window.location='SalaryDetails.action?operation=SALBASIS&SALID='+salId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&strAmt='+basicAmt+'&strGrade='+gradeId;
    				} else {
    					if(id == 1){
    						document.getElementById("ctc").checked = true;
    					} else if(id == 2){
    						document.getElementById("basic").checked = true;
    					} else {
    						document.getElementById("other").checked = true;
    					}
    					document.getElementById("strBasicAmount").value='';
    				}
    			} else {
    				alert("Please, Enter the amount.");
    				if(id == 1){
    					document.getElementById("ctc").checked = true;
    				} else if(id == 2){
    					document.getElementById("basic").checked = true;
    				} else {
    					document.getElementById("other").checked = true;
    				}
    				document.getElementById("strBasicAmount").value='';
    			}
    		}
    	} else if(parseInt(salId) == -1){
    		document.getElementById("strCtcAmount").value='';
    		document.getElementById("strBasicAmount").value='';
    		
    		if(id == 3){
    			alert("Already added other as basis of salary structure.");
    		} else {
    			if(confirm('Are you sure, you want to add other as basis of salary structure?')){ 
    				var userscreen = document.getElementById("userscreen").value;
    		    	var navigationId = document.getElementById("navigationId").value;
    		    	var toPage = document.getElementById("toPage").value;
    		    	
    				window.location='SalaryDetails.action?operation=SALBASIS&SALID=-1&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&strGrade='+gradeId;
    			} else {
    				if(id == 1){
    					document.getElementById("ctc").checked = true;
    				} else if(id == 2){
    					document.getElementById("basic").checked = true;
    				} else {
    					document.getElementById("other").checked = true;
    				}
    			}
    		}
    	}
    }
    
    function addNewSalaryBand(orgId,levelId) {
    	
		if(confirm('Are you sure, you want to add new salary band?')) {
			var userscreen = document.getElementById("userscreen").value;
			var navigationId = document.getElementById("navigationId").value;
			var toPage = document.getElementById("toPage").value;
			
			var saveBand = document.getElementById("saveBand").value;
			var salaryBand = '';
	    	if(document.getElementById("salaryBand")) {
	    		salaryBand = document.getElementById("salaryBand").value;
	    	}
			if(saveBand=='Save') {
				salaryBand='';
			}
			var replicateSalaryBand = document.getElementById("replicateSalaryBand").value;
			var salaryBandName = document.getElementById("salaryBandName").value;
			var salaryBandMinAmt = document.getElementById("salaryBandMinAmt").value;
			var salaryBandMaxAmt = document.getElementById("salaryBandMaxAmt").value;
			window.location='SalaryDetails.action?operation=SALBAND&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId
				+'&replicateSalaryBand='+replicateSalaryBand+'&salaryBand='+salaryBand+'&salaryBandName='+salaryBandName+'&salaryBandMinAmt='+salaryBandMinAmt+'&salaryBandMaxAmt='+salaryBandMaxAmt;
		}
	}
    
    
	function deleteSalaryBand(orgId,levelId) {
    	
		if(confirm('Are you sure, you want to delete this salary band?')) {
			var userscreen = document.getElementById("userscreen").value;
			var navigationId = document.getElementById("navigationId").value;
			var toPage = document.getElementById("toPage").value;
			
			var salaryBand = document.getElementById("salaryBand").value;
			window.location='SalaryDetails.action?operation=SALBANDDELETE&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage
				+'&strOrg='+orgId+'&level='+levelId+'&salaryBand='+salaryBand;
		}
    }
    
    function addStatutoryHeads(x,id,type,orgId,levelId,salaryBand){
    	if(type=='PT'){
    		if(confirm('Are you sure, you want to add Professional Tax to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&salaryBand='+salaryBand; 
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	} else if(type=='PF'){
    		if(confirm('Are you sure, you want to add Employee PF to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&salaryBand='+salaryBand; 
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	} else if(type=='ESI'){
    		if(confirm('Are you sure, you want to add Employee ESI to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&salaryBand='+salaryBand; 
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	} else if(type=='LWF'){
    		if(confirm('Are you sure, you want to add Employee LWF to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&salaryBand='+salaryBand;
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	} else if(type=='TDS'){
    		if(confirm('Are you sure, you want to add TDS (Income Tax) to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&salaryBand='+salaryBand; 
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	}
    	
    }
    
    function addStatutoryHeadsByGrade(x,id,type,orgId,levelId,gradeId){
    	if(type=='PT'){
    		if(confirm('Are you sure, you want to add Professional Tax to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&strGrade='+gradeId; 
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	} else if(type=='PF'){
    		if(confirm('Are you sure, you want to add Employee PF to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&strGrade='+gradeId; 
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	} else if(type=='ESI'){
    		if(confirm('Are you sure, you want to add Employee ESI to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&strGrade='+gradeId;
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	} else if(type=='LWF'){
    		if(confirm('Are you sure, you want to add Employee LWF to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&strGrade='+gradeId;
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	} else if(type=='TDS'){
    		if(confirm('Are you sure, you want to add TDS (Income Tax) to statutory compliance heads?')){
    			var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
    			window.location='SalaryDetails.action?operation=ADD&SALID='+id+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&strOrg='+orgId+'&level='+levelId+'&strGrade='+gradeId;
    		} else {
    			if(x.checked == true){
    				x.checked = false;
    			} else {
    				x.checked = true;
    			}
    		}
    	}
    }
    
    function checkAssignTemplate(){
		var obj = document.getElementById('assignTemplate');
		if(obj.checked){
			document.getElementById('disAssignTemplate').style.display = 'table-row';	
		}else{
			document.getElementById('disAssignTemplate').style.display = 'none';	
		}
	}
	
	function assignTemplateToGrades(orgId,levelId,gradeId){
		var x = document.getElementById('assignTemplate');
		if(x.checked == true){
			if(confirm('Are you sure, Assign this salary structure to all grades under the selected level?')){
				var userscreen = document.getElementById("userscreen").value;
		    	var navigationId = document.getElementById("navigationId").value;
		    	var toPage = document.getElementById("toPage").value;
		    	
				window.location='SalaryDetails.action?operation=assignTemplate&strOrg='+orgId+'&level='+levelId+'&strGrade='+gradeId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage; 
			}
		}
	}
	
	
	function createNewBand(id, salaryBand) {
		if(id.checked == true) {
			//x.checked = false;
			document.getElementById("newBandDiv").style.display = 'block';
			document.getElementById("divAllSalHeads").style.display = 'none';
			document.getElementById("salaryBandName").value='';
			document.getElementById("salaryBandMinAmt").value='';
			document.getElementById("salaryBandMaxAmt").value='';
			document.getElementById("saveBand").value = 'Save';
			document.getElementById("deleteBand").style.display = 'none';
		} else {
			if(parseInt(salaryBand) == 0) {
				document.getElementById("newBandDiv").style.display = 'none';
			}
			document.getElementById("divAllSalHeads").style.display = 'block';
			document.getElementById("salaryBandName").value = document.getElementById("hiddenSalaryBandName").value;
			document.getElementById("salaryBandMinAmt").value = document.getElementById("hiddenSalaryBandMinAmt").value;
			document.getElementById("salaryBandMaxAmt").value = document.getElementById("hiddenSalaryBandMaxAmt").value;
			if(parseInt(salaryBand)> 0) {
				document.getElementById("saveBand").value = 'Update';
				document.getElementById("deleteBand").style.display = 'block';
			}
		}
	}
	
</script>
<%--                          
    <jsp:include page="../common/SubHeader.jsp">
    	<jsp:param value="Salary Structure" name="title"/>
    </jsp:include> --%>
<div class="leftbox reportWidth">
    <%if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE) { %>
    <div class="box box-default collapsed-box">
		<div class="box-header with-border">
		    <h4 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h4>
		    <div class="box-tools pull-right">
		        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
		        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		    </div>
		</div>
		<div class="box-body" style="padding: 15px; overflow-y: auto;">
			<s:form name="frm" action="MyDashboard" theme="simple" method="post">
				<s:hidden name="userscreen" id="userscreen"/>
				<s:hidden name="navigationId" id="navigationId"/>
				<s:hidden name="toPage" id="toPage"/>
	            
	            <div class="row row_without_margin">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-filter"></i>
					</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Organisation</p>
							<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" cssStyle="width: 150px !important;" onchange="submitFormBygrade('1');"></s:select>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Level</p>
							<s:select theme="simple" name="level" id="level" listKey="levelId" cssStyle="margin-left:10px; width: 150px !important;"
				                listValue="levelCodeName" headerKey="" headerValue="Choose Level" onchange="submitFormBygrade('2');"
				                list="levelList" key="" required="true" />
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Grade</p>
							<s:select theme="simple" name="strGrade" id="strGrade" listKey="gradeId" cssStyle="margin-left:10px; width: 150px !important;"
				                listValue="gradeCode" headerKey="" headerValue="Choose Grade" onchange="submitFormBygrade('3');"
				                list="gradeList" key="" required="true" />
						</div>
					</div>
				</div>    
	        </s:form>
		</div>
	</div>
    <div class="col-md-12 col_no_padding">
		<p style="font-size: 12px; padding-right: 10px; font-style: italic;float:right">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
	</div>
    <%if(uF.parseToInt((String)request.getAttribute("strGrade"))>0){ 
    	String strOrgId = (String) request.getAttribute("strOrg");
		String strLevelId = (String) request.getAttribute("level");
		String strGradeId = (String) request.getAttribute("strGrade");
    %>    
    	<div class="col-md-12 col_no_padding">
			<a href="FitmentForBasicPolicy.action?strOrg=<%=request.getAttribute("strOrg")%>" style="float: right;">Fitment for Basic</a>
		</div>         
    
	    <div class="col-md-12 col_no_padding">
	    	<%if(couterlist != null && couterlist.size() > 1 && uF.parseToInt((String)request.getAttribute("strGrade")) > 0){ %>
				<div style="float:left; width:98%;">
					<p>
						<input type="checkbox" name="assignTemplate" id="assignTemplate" onclick="checkAssignTemplate();">
						Assign this salary structure to all grades under the selected level
					</p>
					
					<span id="disAssignTemplate" style="display:none;">
						<input type="button" name="Assign" value="Assign" class="btn btn-primary" style="margin:0px" onclick="assignTemplateToGrades('<%=strOrgId %>','<%=strLevelId%>','<%=strGradeId%>')"/>
					</span>
				</div>
			<%} %>	
	        <%
		        List<String> alStatutoryIds = (List<String>) request.getAttribute("alStatutoryIds");
				if(alStatutoryIds == null) alStatutoryIds = new ArrayList<String>(); 
				Map<String, String> hmStatutoryIds = (Map<String, String>)request.getAttribute("hmStatutoryIds");
				if(hmStatutoryIds == null) hmStatutoryIds = new HashMap<String, String>();
				
				String checkedPT = "";
				if(alStatutoryIds.contains(""+IConstants.PROFESSIONAL_TAX)){ 
					checkedPT="checked";
				} 
				String strPT = "addStatutoryHeadsByGrade(this,'"+IConstants.PROFESSIONAL_TAX+"','PT','"+strOrgId+"','"+strLevelId+"','"+strGradeId+"')";
				if(alStatutoryIds.contains(""+IConstants.PROFESSIONAL_TAX)){
					strPT = "removeStatutoryFieldByGrade(this,'remove_"+hmStatutoryIds.get(""+IConstants.PROFESSIONAL_TAX)+"')";
				}
				
				String checkedPF = "";
				if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_EPF)){ 
					checkedPF="checked";
				} 
				String strPF = "addStatutoryHeadsByGrade(this,'"+IConstants.EMPLOYEE_EPF+"','PF','"+strOrgId+"','"+strLevelId+"','"+strGradeId+"')";
				if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_EPF)){
					strPF = "removeStatutoryFieldByGrade(this,'remove_"+hmStatutoryIds.get(""+IConstants.EMPLOYEE_EPF)+"')";
				}
				
				String checkedESI = "";
				if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_ESI)){ 
					checkedESI="checked";
				} 
				String strESI = "addStatutoryHeadsByGrade(this,'"+IConstants.EMPLOYEE_ESI+"','ESI','"+strOrgId+"','"+strLevelId+"','"+strGradeId+"')";
				if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_ESI)){
					strESI = "removeStatutoryFieldByGrade(this,'remove_"+hmStatutoryIds.get(""+IConstants.EMPLOYEE_ESI)+"')";
				}
				
				String checkedLWF = "";
				if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_LWF)){ 
					checkedLWF="checked";
				} 
				String strLWF = "addStatutoryHeadsByGrade(this,'"+IConstants.EMPLOYEE_LWF+"','LWF','"+strOrgId+"','"+strLevelId+"','"+strGradeId+"')";
				if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_LWF)){
					strLWF = "removeStatutoryFieldByGrade(this,'remove_"+hmStatutoryIds.get(""+IConstants.EMPLOYEE_LWF)+"')";
				}
				
				String checkedTDS = "";
				if(alStatutoryIds.contains(""+IConstants.TDS)){ 
					checkedTDS="checked";
				} 
				String strTDS = "addStatutoryHeadsByGrade(this,'"+IConstants.TDS+"','TDS','"+strOrgId+"','"+strLevelId+"','"+strGradeId+"')";
				if(alStatutoryIds.contains(""+IConstants.TDS)){
					strTDS = "removeStatutoryFieldByGrade(this,'remove_"+hmStatutoryIds.get(""+IConstants.TDS)+"')";
				}
	            %>
	        <div style="float:left; width:55%;">
	            <h5><strong>Statutory Compliance Heads</strong></h5>
	            <input type="checkbox" onclick="<%=strPT %>" value="<%=IConstants.PROFESSIONAL_TAX %>" name="PT" id="PT" <%=checkedPT %>><span class="tdDashLabel"> Professional Tax</span>
	            <input type="checkbox" onclick="<%=strPF %>" value="<%=IConstants.EMPLOYEE_EPF %>" name="PF" id="PF" <%=checkedPF %>><span class="tdDashLabel"> Employee PF</span>
	            <input type="checkbox" onclick="<%=strESI %>" value="<%=IConstants.EMPLOYEE_ESI %>" name="ESI" id="ESI" <%=checkedESI %>><span class="tdDashLabel"> Employee ESI</span>
	            <input type="checkbox" onclick="<%=strLWF %>" value="<%=IConstants.EMPLOYEE_LWF %>" name="LWF" id="LWF" <%=checkedLWF %>><span class="tdDashLabel"> Employee LWF</span>
	            <input type="checkbox" onclick="<%=strTDS %>" value="<%=IConstants.TDS %>" name="TDS" id="TDS" <%=checkedTDS %>><span class="tdDashLabel"> TDS (Income Tax)</span>
	        </div>
	        
	        <%
		        List<String> alBasisSalIds = (List<String>) request.getAttribute("alBasisSalIds");
				if(alBasisSalIds == null) alBasisSalIds = new ArrayList<String>(); 
				
				String strCheckedCTC="";
				String strCheckedBasic="";
				String strCheckedOther="";
				String strDisabled="";
				int nId=0;
				if(alBasisSalIds.contains(""+IConstants.CTC)){
					strCheckedCTC="checked=\"checked\""; 
					//strDisabled="disabled=\"disabled\"";
					nId=1;
				} else if(alBasisSalIds.contains(""+IConstants.BASIC)){
					strCheckedBasic="checked=\"checked\"";
					//strDisabled="disabled=\"disabled\"";
					nId=2;
				} else {
					strCheckedOther="checked=\"checked\"";
					nId=3;
				}
	            
	            %>
	        <div style="float:left; width:98%;">
	            <h5><strong>Basis of salary structure</strong></h5>
	            <s:textfield name="strCtcAmount" id="strCtcAmount" cssStyle="width: 82px !important;" onkeypress="return isNumberKey(event)"/>
	            &nbsp;
	            <input type="radio" name="salaryBasis" id="ctc" value="<%=IConstants.CTC %>" <%=strCheckedCTC %> <%=strDisabled %> onclick="changeSalaryBasisByGrade(<%=nId %>,'<%=IConstants.CTC %>', '<%=(String) request.getAttribute("strOrg") %>', '<%=(String) request.getAttribute("level") %>', '<%=strGradeId %>');" /><label class="tdDashLabel">CTC</label> &nbsp;
	            <s:textfield name="strBasicAmount" id="strBasicAmount" cssStyle="width: 82px !important;" onkeypress="return isNumberKey(event)"/>
	            &nbsp;
	            <input type="radio" name="salaryBasis" id="basic" value="<%=IConstants.BASIC %>" <%=strCheckedBasic %> <%=strDisabled %> onclick="changeSalaryBasisByGrade(<%=nId %>,'<%=IConstants.BASIC %>', '<%=(String) request.getAttribute("strOrg") %>', '<%=(String) request.getAttribute("level") %>', '<%=strGradeId %>');" /><label class="tdDashLabel">Basic</label> &nbsp;
	            <input type="radio" name="salaryBasis" id="other" value="-1" <%=strCheckedOther %> <%=strDisabled %> onclick="changeSalaryBasisByGrade(<%=nId %>,'-1', '<%=(String) request.getAttribute("strOrg") %>', '<%=(String) request.getAttribute("level") %>', '<%=strGradeId %>');"/><label class="tdDashLabel">Other</label> &nbsp;
	        </div>
	        <s:hidden name="curr_short" id="curr_short"></s:hidden>
	        <div class="clr"></div>
	        <div class="row row_without_margin" style="border: 2px solid rgb(231, 231, 231);margin-top: 20px;padding-bottom: 20px;">
	        	<div class="credit col-lg-6 col-md-6 col_no_padding" id="div_gross" style="border-right: 2px solid rgb(236, 236, 236);">
	            <div class="heading1">
	                <h4>EARNING DETAILS</h4>
	            </div>
	            <div class="details_lables" >
	                <%boolean isBenefit = false;
	                    for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
	                <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
	                    %>
	                <% 
	                    if(cinnerlist.get(2).equals("E") && !uF.parseToBoolean(""+cinnerlist.get(11)) && !uF.parseToBoolean(""+cinnerlist.get(12))) { %>	
	                <input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
	                <input type="hidden" name="salary_head_name" value='<%= cinnerlist.get(1) %>'></input>
	                <input type="hidden" name="salary_head_byte" value='<%= cinnerlist.get(2) %>'></input>
	                <%if(uF.parseToInt((String)cinnerlist.get(9))>990 && !isBenefit){ isBenefit=true;%>
	                Benefits
	                <hr/>
	                <%} %>
	                <div class="row row_without_margin">
	                    <div class="col1 tdDashLabel">
	                        <label id="lbl"><%=cinnerlist.get(1) %>:</label>	
	                    </div>
	                    <div class="col2" style="width: 375px;">
	                        <%
	      	 				if(cinnerlist.get(3).equals("P")) { 
	      	 				%>
	      	 				
	      	 					<label id="lbl_head_value"><%= cinnerlist.get(10) %></label>
	      	 					[<label id="lbl_head_value"><%= cinnerlist.get(5) %></label>
		      	 					% of <label id="lbl_sub_head"><%=cinnerlist.get(13)!=null && cinnerlist.get(13).equals("") ? "": cinnerlist.get(13) %></label>]
					      	<%}else{ %>
						     	<%=cinnerlist.get(5) %>
						    <%}
				      	 	if(uF.parseToBoolean((String)cinnerlist.get(6))) {
					      	%>
		       					<a style="color:red" href="javascript:void(0)" onclick="removeFieldByGrade(this.id)" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
		       				<%} 
				      	 	if(uF.parseToBoolean((String)cinnerlist.get(7))) {
		       				%>
			       				<a href="#?w=400" rel="popup_name_edit_<%=cinnerlist.get(8)%>" class="poplight">Edit..</a>
		       				<%}%>
	                    </div>
	                </div>
	                <div class="clr"></div>
	                <%} %>
	                <%}%>
	            </div>
	            <div class="buttons">
	                <a href="#?w=400" rel="popup_name" class="poplight">Add New Field..</a>
	            </div>
	            <div class="clr"></div>
	            <%if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_EPF) || alStatutoryIds.contains(""+IConstants.EMPLOYEE_ESI) || alStatutoryIds.contains(""+IConstants.EMPLOYEE_LWF)){ %>
	            <div>
	                <h4>CONTRIBUTION DETAILS</h4>
	                <hr style="border:1px solid gray;"/>
	            </div>
	            <div class="clr"></div>
	            <div class="details_lables" >
	                <%if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_EPF)){ %>
	                <div class="row row_without_margin">
	                    <div class="col1 tdDashLabel">
	                        <label id="lbl">Employer PF:</label>	
	                    </div>
	                    <div class="col2">
	                        based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=EPFS">Statutory Compliance</a>
	                    </div>
	                </div>
	                <div class="clr"></div>
	                <%} %>
	                <%if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_ESI)){ %>
	                <div class="row row_without_margin">
	                    <div class="col1 tdDashLabel">
	                        <label id="lbl">Employer ESI:</label>	
	                    </div>
	                    <div class="col2">
	                        based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=ESICS">Statutory Compliance</a>
	                    </div>
	                </div>
	                <div class="clr"></div>
	                <%} %>
	                <%if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_LWF)){ %>
	                <div class="row row_without_margin">
	                    <div class="col1 tdDashLabel">
	                        <label id="lbl">Employer LWF:</label>	
	                    </div>
	                    <div class="col2">
	                        based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=LWFS">Statutory Compliance</a>
	                    </div>
	                </div>
	                <div class="clr"></div>
	                <%} %>
	            </div>
	            <div class="clr"></div>
	            <%} %>
	            <div>
	                <h4>INCENTIVES (PART OF SALARY)</h4>
	                <hr style="border:1px solid gray;"/>
	            </div>
	            <div class="clr"></div>
	            <div class="details_lables" >
	                <%isBenefit = false;
	                    for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
	                <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
	                    %>
	                <% 
	                    if(cinnerlist.get(2).equals("E") && uF.parseToBoolean(""+cinnerlist.get(11))) { %>	
	                <input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
	                <input type="hidden" name="salary_head_name" value='<%= cinnerlist.get(1) %>'></input>
	                <input type="hidden" name="salary_head_byte" value='<%= cinnerlist.get(2) %>'></input>
	                <%if(uF.parseToInt((String)cinnerlist.get(9))>990 && !isBenefit){ isBenefit=true;%>
	                Benefits
	                <hr/>
	                <%} %>
	                <div class="row row_without_margin">
	                    <div class="col1 tdDashLabel">
	                        <label id="lbl"><%= cinnerlist.get(1) %>:</label>	
	                    </div>
	                    <div class="col2" style="width: 375px;">
	                        <%
	    	 					if( cinnerlist.get(3).equals("P")) { 
	    	 				%>
	    	 					<label id="lbl_head_value"><%= cinnerlist.get(10) %></label>
	    	 					[<label id="lbl_head_value"><%= cinnerlist.get(5) %></label>
	     	 					% of <label id="lbl_sub_head"><%=cinnerlist.get(13)!=null && cinnerlist.get(13).equals("") ? "": cinnerlist.get(13) %></label>]
					      	 <%}else{ %>
						      	 	<!-- Amount -->
						      	 	<%=cinnerlist.get(5) %>
						     <%}
					      	 if(uF.parseToBoolean((String)cinnerlist.get(6))) {
					      	%>
			       				<a style="color:red" href="javascript:void(0)" onclick="removeFieldByGrade(this.id)" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
			       			<%}
				      	 	if(uF.parseToBoolean((String)cinnerlist.get(7))) {
		       				%>
		       					<a href="#?w=400" rel="popup_name_edit_<%=cinnerlist.get(8)%>" class="poplight">Edit..</a>
		       				<%}%>
	                    </div>
	                </div>
	                <div class="clr"></div>
	                <%} %>
	                <%}%>
	            </div>
	            <div class="buttons">
	                <a href="#?w=400" rel="popup_incentive" class="poplight" ">Add New Field..</a>
	            </div>
	            <div class="clr"></div>
	        </div>
	        
	        <div class="deduction col-lg-6 col-md-6 col_no_padding" id="frm_deduction" >
	            <div class="heading1">
	                <h4>DEDUCTION DETAILS</h4>
	            </div>
	            <!-- <form id="frm_deduction" name="frm_deduction" action="SalaryDetails.action.action" onsubmit="return saveDeductionAll()" > -->
	            <div class="details_lables" >
	                <% 
	                    for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
	                <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
	                <% if(cinnerlist.get(2).equals("D")) { %>	
	                <input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
	                <input type="hidden" name="salary_head_name" value='<%= cinnerlist.get(1) %>'></input>
	                <input type="hidden" name="salary_head_byte" value='<%= cinnerlist.get(2) %>'></input>
	                <div class="row row_without_margin">
	                    <div class="col1 tdDashLabel">
	                        <label id="lbl" ><%= cinnerlist.get(1) %>:</label>
	                    </div>
	                    <div class="col2" style="width: 375px;">
	                        <%
	      	 					if(cinnerlist.get(3).equals("P")) { 
	      	 					%>
		      	 					<label id="lbl_head_value"><%= cinnerlist.get(10) %></label>
		      	 					[<label id="lbl_head_value"><%= cinnerlist.get(5) %></label>
			      	 					% of <label id="lbl_sub_head"><%=cinnerlist.get(13)!=null && cinnerlist.get(13).equals("") ? "": cinnerlist.get(13) %></label>]
				      	 		<%}else{ %>										      	 	
					      	 	<!-- Amount -->
						      	 	<%
						      	 	if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.PROFESSIONAL_TAX) {%>
						      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=PTS">Statutory Compliance</a>
						      	 	<%} else if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.EMPLOYEE_EPF) {%>
						      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=EPFS">Statutory Compliance</a>
						      	 	<%} else if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.EMPLOYEE_ESI) {%>
						      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=ESICS">Statutory Compliance</a>
						      	 	<%} else if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.EMPLOYEE_LWF) {%>
						      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=LWFS">Statutory Compliance</a>
						      	 	<%} else if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.TDS) {%>
						      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=ITS">Statutory Compliance</a>
						      	 	<%} else {%>
						      	 		<%=cinnerlist.get(5) %>
					      	 		<%} %>
					      	 	<% } %>
					      	 	
					      	 	<% if(uF.parseToBoolean((String)cinnerlist.get(6))) { %>
					      	 	
			       						<a style="color:red" href="javascript:void(0)" onclick="removeFieldByGrade(this.id)" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
			       				
				       				<%}
			       				if(uF.parseToBoolean((String)cinnerlist.get(7)) && uF.parseToInt((String)cinnerlist.get(0))!=IConstants.PROFESSIONAL_TAX 
			       						&& uF.parseToInt((String)cinnerlist.get(0))!=IConstants.EMPLOYEE_EPF 
			       						&& uF.parseToInt((String)cinnerlist.get(0))!=IConstants.EMPLOYEE_ESI 
			       						&& uF.parseToInt((String)cinnerlist.get(0))!=IConstants.EMPLOYEE_LWF 
			       						&& uF.parseToInt((String)cinnerlist.get(0))!=IConstants.TDS) {
						      	
				       				%>
				       					<a href="#?w=400" rel="popup_name_edit_<%=cinnerlist.get(8)%>" class="poplight">Edit..</a>
			       				<% } %>
	                    </div>
	                </div>
	                <div class="clr"></div>
	                <% } %>
	                <% } %>
	                <%-- <% if(empId!=null) { %>
	                    <div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
	                    	<label name="total_deduction" id="lbl">Total Deduction: </label>
	                    	<label name="total_deduction_value" id= "total_deduction_value" style="color: green;"></label>
	                    </div>
	                    <% } %> --%>
	            </div>
	            <div class="buttons">
	                <a href="#?w=400" rel="popup_deduction" class="poplight" ">Add New Field..</a>
	            </div>
	            <div class="clr"></div>
	        </div>
	    </div>
	    <% } else { %>
	    <div class="filter">
	        <div class="msg nodata"><span>Please choose the grade</span></div>
	    </div>
	    <% } %>
    <%} else { %>
    <%-- <div class="filter_div">
        <div class="filter_caption">Select</div>
        <s:form name="frm" action="SalaryDetails" theme="simple">
            <s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm('1');"></s:select>
            <s:select theme="simple" name="level" id="level" listKey="levelId" cssStyle="margin-left:10px"
                listValue="levelCodeName" headerKey="" headerValue="Choose Level" onchange="submitForm('2');"
                list="levelList" key="" required="true" />
        </s:form>
    </div> --%>
    <div class="box box-default collapsed-box">
		<div class="box-header with-border">
		    <h4 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h4>
		    <div class="box-tools pull-right">
		        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
		        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		    </div>
		</div>
		<% 
			String strOrgId = (String) request.getAttribute("strOrg");
			String strLevelId = (String) request.getAttribute("level");
			String salaryBand = (String) request.getAttribute("salaryBand");
		 %>
		<div class="box-body" style="padding: 15px; overflow-y: auto;">
			<s:form name="frm" action="MyDashboard" theme="simple" method="post">
				<s:hidden name="userscreen" id="userscreen"/>
				<s:hidden name="navigationId" id="navigationId"/>
				<s:hidden name="toPage" id="toPage"/>

	            <div class="row row_without_margin">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-filter"></i>
					</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Organisation</p>
							<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm('1');"></s:select>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Level</p>
							<s:select theme="simple" name="level" id="level" listKey="levelId" cssStyle="margin-left:10px"
				                listValue="levelCodeName" headerKey="" headerValue="Choose Level" onchange="submitForm('2');"
				                list="levelList" key="" required="true" />
						</div>
						<% List<FillSalaryBand> salBandList = (List<FillSalaryBand>) request.getAttribute("salBandList"); 
							if(salBandList!=null && salBandList.size()>0) {
						%>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Salary Band</p>
							<s:select theme="simple" name="salaryBand" id="salaryBand" listKey="salaryBandId" cssStyle="width: 160px !important;"
				                listValue="salaryBandName" headerKey="" headerValue="Choose Band" onchange="submitForm('2');" list="salBandList" key="" required="true" />
						</div>
						<% } %>
					</div>
				</div>    
	        </s:form>
		</div>
	</div>
    <div class="col-md-12 col_no_padding">
    	<p style="float:left; margin-top: -10px;"><input type="checkbox" onclick="createNewBand(this, '<%=salaryBand %>');" name="createBand" id="createBand"><span class="tdDashLabel"> Create Band</span></p>
		<p style="font-size: 12px; padding-right: 10px; font-style: italic;float:right">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
	</div>
	
	<% String strDisplay = "none";
		String strButton = "Save";
		if(uF.parseToInt(salaryBand)>0) {
			strDisplay = "block";
			strButton = "Update";
		}
		
	%>
	<div id="newBandDiv" class="col-lg-12 col-md-12 col-sm-12 col_no_padding" style="display: <%=strDisplay %>;">
		<div class="col-lg-4 col-md-6 col-sm-12">Band Name</div>
		<div class="col-lg-3 col-md-6 col-sm-12">Minimum Amount</div>
		<div class="col-lg-3 col-md-6 col-sm-12">Maximum Amount</div>
		<div class="col-lg-2 col-md-6 col-sm-12">&nbsp;</div>
		
		<div class="col-lg-4 col-md-6 col-sm-12">
			<s:textfield name="salaryBandName" id="salaryBandName" />
			<input type="hidden" id="hiddenSalaryBandName" value="<%=uF.showData((String)request.getAttribute("salaryBandName"), "") %>" />
			<input type="hidden" id="hiddenSalaryBandMinAmt" value="<%=uF.showData((String)request.getAttribute("salaryBandMinAmt"), "0") %>" />
			<input type="hidden" id="hiddenSalaryBandMaxAmt" value="<%=uF.showData((String)request.getAttribute("salaryBandMaxAmt"), "0") %>" />
		</div>
		<div class="col-lg-3 col-md-6 col-sm-12"><s:textfield name="salaryBandMinAmt" id="salaryBandMinAmt" cssStyle="width: 80px !important;" onkeypress="return isNumberKey(event)"/></div>
		<div class="col-lg-3 col-md-6 col-sm-12"><s:textfield name="salaryBandMaxAmt" id="salaryBandMaxAmt" cssStyle="width: 80px !important;" onkeypress="return isNumberKey(event)"/></div>
		<%-- <div class="col-lg-2 col-md-6 col-sm-12"><input type="button" class="btn btn-primary" name="saveBand" id="saveBand" style="float: center;" value="<%=strButton %>" onclick="addNewSalaryBand('<%=strOrgId %>','<%=strLevelId %>')"/></div> --%>
		<div class="col-lg-12 col-md-12 col-sm-12" style="padding-top: 7px;">
			<div class="col-lg-7 col-md-12 col-sm-12" style="padding-left: 0px;">Replicate from: 
				<s:select theme="simple" name="replicateSalaryBand" id="replicateSalaryBand" listKey="salaryBandId" cssStyle="width: 160px !important;"
					listValue="salaryBandName" headerKey="" headerValue="Select Band" list="salBandList" key="" />
			</div>
			<div class="col-lg-5 col-md-12 col-sm-12">
				<input type="button" class="btn btn-primary" name="saveBand" id="saveBand" style="float: center;" value="<%=strButton %>" onclick="addNewSalaryBand('<%=strOrgId %>','<%=strLevelId %>')"/>
				<% if(uF.parseToInt(salaryBand)>0) { %>
					<input type="button" class="btn btn-primary" name="deleteBand" id="deleteBand" style="float: center;" value="Delete" onclick="deleteSalaryBand('<%=strOrgId %>','<%=strLevelId %>')"/>
				<% } %>
			</div>
		</div>
	</div>
	
    <%if(uF.parseToInt((String)request.getAttribute("level"))>0){ %>
    <div id="divAllSalHeads" class="crdb_details" style="width: 97%;" >
        <%
	        List<String> alStatutoryIds = (List<String>) request.getAttribute("alStatutoryIds");
			if(alStatutoryIds == null) alStatutoryIds = new ArrayList<String>(); 
			Map<String, String> hmStatutoryIds = (Map<String, String>)request.getAttribute("hmStatutoryIds");
			if(hmStatutoryIds == null) hmStatutoryIds = new HashMap<String, String>();
			
			
			String checkedPT = "";
			if(alStatutoryIds.contains(""+IConstants.PROFESSIONAL_TAX)){ 
				checkedPT="checked";
			} 
			String strPT = "addStatutoryHeads(this,'"+IConstants.PROFESSIONAL_TAX+"','PT','"+strOrgId+"','"+strLevelId+"','"+salaryBand+"')";
			if(alStatutoryIds.contains(""+IConstants.PROFESSIONAL_TAX)){
				strPT = "removeStatutoryField(this,'remove_"+hmStatutoryIds.get(""+IConstants.PROFESSIONAL_TAX)+"')";
			}
			
			String checkedPF = "";
			if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_EPF)){ 
				checkedPF="checked";
			} 
			String strPF = "addStatutoryHeads(this,'"+IConstants.EMPLOYEE_EPF+"','PF','"+strOrgId+"','"+strLevelId+"','"+salaryBand+"')";
			if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_EPF)){
				strPF = "removeStatutoryField(this,'remove_"+hmStatutoryIds.get(""+IConstants.EMPLOYEE_EPF)+"')";
			}
			
			String checkedESI = "";
			if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_ESI)){ 
				checkedESI="checked";
			} 
			String strESI = "addStatutoryHeads(this,'"+IConstants.EMPLOYEE_ESI+"','ESI','"+strOrgId+"','"+strLevelId+"','"+salaryBand+"')";
			if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_ESI)){
				strESI = "removeStatutoryField(this,'remove_"+hmStatutoryIds.get(""+IConstants.EMPLOYEE_ESI)+"')";
			}
			
			String checkedLWF = "";
			if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_LWF)){ 
				checkedLWF="checked";
			}
			String strLWF = "addStatutoryHeads(this,'"+IConstants.EMPLOYEE_LWF+"','LWF','"+strOrgId+"','"+strLevelId+"','"+salaryBand+"')";
			if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_LWF)){
				strLWF = "removeStatutoryField(this,'remove_"+hmStatutoryIds.get(""+IConstants.EMPLOYEE_LWF)+"')";
			}
			
			String checkedTDS = "";
			if(alStatutoryIds.contains(""+IConstants.TDS)){ 
				checkedTDS="checked";
			} 
			String strTDS = "addStatutoryHeads(this,'"+IConstants.TDS+"','TDS','"+strOrgId+"','"+strLevelId+"','"+salaryBand+"')";
			if(alStatutoryIds.contains(""+IConstants.TDS)){
				strTDS = "removeStatutoryField(this,'remove_"+hmStatutoryIds.get(""+IConstants.TDS)+"')";
			}
            %>
        <div style="float:left; width:98%;">
            <h5><strong>Statutory Compliance Heads</strong></h5>
            <input type="checkbox" onclick="<%=strPT %>" value="<%=IConstants.PROFESSIONAL_TAX %>" name="PT" id="PT" <%=checkedPT %>><span class="tdDashLabel"> Professional Tax</span>
            <input type="checkbox" onclick="<%=strPF %>" value="<%=IConstants.EMPLOYEE_EPF %>" name="PF" id="PF" <%=checkedPF %>><span class="tdDashLabel"> Employee PF</span>
            <input type="checkbox" onclick="<%=strESI %>" value="<%=IConstants.EMPLOYEE_ESI %>" name="ESI" id="ESI" <%=checkedESI %>><span class="tdDashLabel"> Employee ESI</span>
            <input type="checkbox" onclick="<%=strLWF %>" value="<%=IConstants.EMPLOYEE_LWF %>" name="LWF" id="LWF" <%=checkedLWF %>><span class="tdDashLabel"> Employee LWF</span>
            <input type="checkbox" onclick="<%=strTDS %>" value="<%=IConstants.TDS %>" name="TDS" id="TDS" <%=checkedTDS %>><span class="tdDashLabel"> TDS (Income Tax)</span>
        </div>
        <%
	        List<String> alBasisSalIds = (List<String>) request.getAttribute("alBasisSalIds");
			if(alBasisSalIds == null) alBasisSalIds = new ArrayList<String>(); 
			
			String strCheckedCTC="";
			String strCheckedBasic="";
			String strCheckedOther="";
			String strDisabled="";
			int nId=0;
			if(alBasisSalIds.contains(""+IConstants.CTC)){
				strCheckedCTC="checked=\"checked\""; 
				//strDisabled="disabled=\"disabled\"";
				nId=1;
			} else if(alBasisSalIds.contains(""+IConstants.BASIC)){
				strCheckedBasic="checked=\"checked\"";
				//strDisabled="disabled=\"disabled\"";
				nId=2;
			} else {
				strCheckedOther="checked=\"checked\"";
				nId=3;
			}
            
            %>
        <div style="float:left; width:98%;">
            <h5><strong>Basis of salary structure</strong></h5>
            <s:textfield name="strCtcAmount" id="strCtcAmount" cssStyle="width: 82px;" onkeypress="return isNumberKey(event)"/>
            &nbsp;
            <input type="radio" name="salaryBasis" id="ctc" value="<%=IConstants.CTC %>" <%=strCheckedCTC %> <%=strDisabled %> onclick="changeSalaryBasis(<%=nId %>,'<%=IConstants.CTC %>', '<%=(String) request.getAttribute("strOrg") %>', '<%=(String) request.getAttribute("level") %>','<%=salaryBand %>');" /><label class="tdDashLabel">CTC</label> &nbsp;
            <s:textfield name="strBasicAmount" id="strBasicAmount" cssStyle="width: 82px;" onkeypress="return isNumberKey(event)"/>
            &nbsp;
            <input type="radio" name="salaryBasis" id="basic" value="<%=IConstants.BASIC %>" <%=strCheckedBasic %> <%=strDisabled %> onclick="changeSalaryBasis(<%=nId %>,'<%=IConstants.BASIC %>', '<%=(String) request.getAttribute("strOrg") %>', '<%=(String) request.getAttribute("level") %>','<%=salaryBand %>');" /><label class="tdDashLabel">Basic</label> &nbsp;
            <input type="radio" name="salaryBasis" id="other" value="-1" <%=strCheckedOther %> <%=strDisabled %> onclick="changeSalaryBasis(<%=nId %>,'-1', '<%=(String) request.getAttribute("strOrg") %>', '<%=(String) request.getAttribute("level") %>','<%=salaryBand %>');"/><label class="tdDashLabel">Other</label> &nbsp;
        </div>
        <!-- <form action="generateHourlySalarySlip.action"> 
            <div id="div_genHourly" style="float:right; margin:5px; width:170px">
            	<input type="submit" class="btn btn-primary" name="HourlySalarySlip" id = "HourlySalarySlip" value="Generate Hourly Salary Slip" onclick="return generateSalarySlip()" />
            </div>
            </form> -->
        <s:hidden name="curr_short" id="curr_short"></s:hidden>
        <!-- <form id="frm_global" name="frm_global" action="GetCreditDetails.action"  > -->
        <!-- <div id="div_gen" style="float:right; margin:5px; width:170px">
            <input type="submit" class="btn btn-primary" name="salarySlip" id = "salarySlip" value="Generate Salary Slip" onclick="return generateSalarySlip()" />
            </div> -->
        <!-- <div id="div_save" style="float:right; margin:5px;">
            <input type="submit" class="btn btn-primary" name="save" value="Save" onclick="return saveAll()"/>
            </div> -->
        <div class="clr"></div>
        <div class="row row_without_margin" style="border: 2px solid rgb(231, 231, 231);margin-top: 20px;padding-bottom: 20px;">
        <div class="credit col-lg-6 col-md-6 col_no_padding" id="div_gross" style="border-right: 2px solid rgb(236, 236, 236);">
            <div class="heading1">
                <h4>EARNING DETAILS</h4>
            </div>
            <div class="details_lables" >
                <%	boolean isBenefit = false;
                    for (int i=0; couterlist!=null && i<couterlist.size(); i++) { 
                    	java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
				%>
                <% 
                    if(cinnerlist.get(2).equals("E") && !uF.parseToBoolean(""+cinnerlist.get(11)) && !uF.parseToBoolean(""+cinnerlist.get(12))) { %>	
                <input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
                <input type="hidden" name="salary_head_name" value='<%= cinnerlist.get(1) %>'></input>
                <input type="hidden" name="salary_head_byte" value='<%= cinnerlist.get(2) %>'></input>
                <%if(uF.parseToInt((String)cinnerlist.get(9))>990 && !isBenefit) { isBenefit=true; %>
                Benefits
                <hr/>
                <% } %>
                <div class="row row_without_margin">
                    <div class="col1 tdDashLabel">
                        <label id="lbl"><%=cinnerlist.get(1) %>:</label>	
                    </div>
                    <div class="col2" style="width: 375px;">
                       <%if(cinnerlist.get(3).equals("P")){ %>
				      	 	<label id="lbl_head_value"><%= cinnerlist.get(10) %></label>
      	 					[<label id="lbl_head_value"><%= cinnerlist.get(5) %></label>
	      	 					% of <label id="lbl_sub_head"><%=cinnerlist.get(13)!=null && cinnerlist.get(13).equals("") ? "": cinnerlist.get(13) %></label>]	
				      	 <%}else{ %>
					      	 <%=cinnerlist.get(5) %>
				      	<%}
			      	 	if(uF.parseToBoolean((String)cinnerlist.get(6))) {
			      	 		if(alAssignToPercentageHead.contains((String)cinnerlist.get(0))){
				      	%>
				      		 <a style="color:red" href="javascript:void(0)" onclick="alert('This head is align with other head.');" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
				      	<%} else { %>	
	       					<a style="color:red" href="javascript:void(0)" onclick="removeField(this.id)" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
	       				<%}
			      	 	}	
					      	
				      	 if(uF.parseToBoolean((String)cinnerlist.get(7))) {
		       				%>
			       				<a href="#?w=400" rel="popup_name_edit_<%=cinnerlist.get(8)%>" class="poplight">Edit..</a>
			       		<%}%>
                    </div>
                </div>
                <div class="clr"></div>
                <% } %>
                <% } %>
            </div>
            <div class="buttons">
                <a href="#?w=400" rel="popup_name" class="poplight">Add New Field..</a>
            </div>
            <div class="clr"></div>
            <%if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_EPF) || alStatutoryIds.contains(""+IConstants.EMPLOYEE_ESI) || alStatutoryIds.contains(""+IConstants.EMPLOYEE_LWF)){ %>
            <div>
                <h4>CONTRIBUTION DETAILS</h4>
                <hr style="border:1px solid gray;"/>
            </div>
            <div class="clr"></div>
            <div class="details_lables" >
                <%if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_EPF)){ %>
                <div class="row row_without_margin">
                    <div class="col1 tdDashLabel">
                        <label id="lbl">Employer PF:</label>	
                    </div>
                    <div class="col2">
                        based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=EPFS">Statutory Compliance</a>
                    </div>
                </div>
                <div class="clr"></div>
                <%} %>
                <%if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_ESI)){ %>
                <div class="row row_without_margin">
                    <div class="col1 tdDashLabel">
                        <label id="lbl">Employer ESI:</label>	
                    </div>
                    <div class="col2">
                        based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=ESICS">Statutory Compliance</a>
                    </div>
                </div>
                <div class="clr"></div>
                <%} %>
                <%if(alStatutoryIds.contains(""+IConstants.EMPLOYEE_LWF)){ %>
                <div class="row row_without_margin">
                    <div class="col1 tdDashLabel">
                        <label id="lbl">Employer LWF:</label>	
                    </div>
                    <div class="col2">
                        based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=LWFS">Statutory Compliance</a>
                    </div>
                </div>
                <div class="clr"></div>
                <%} %>
            </div>
            <div class="clr"></div>
            <%} %>
            <div>
                <h4>INCENTIVES (PART OF SALARY)</h4>
                <hr style="border:1px solid gray;"/>
            </div>
            <div class="clr"></div>
            <div class="details_lables" >
                <%isBenefit = false;
                    for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
                <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
                    %>
                <% 
                    if(cinnerlist.get(2).equals("E") && uF.parseToBoolean(""+cinnerlist.get(11))) { %>	
                <input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
                <input type="hidden" name="salary_head_name" value='<%= cinnerlist.get(1) %>'></input>
                <input type="hidden" name="salary_head_byte" value='<%= cinnerlist.get(2) %>'></input>
                <%if(uF.parseToInt((String)cinnerlist.get(9))>990 && !isBenefit){ isBenefit=true;%>
                Benefits
                <hr/>
                <%} %>
                <div class="row row_without_margin">
                    <div class="col1 tdDashLabel">
                        <label id="lbl"><%= cinnerlist.get(1) %>:</label>	
                    </div>
                    <div class="col2">
                       <%if(cinnerlist.get(3).equals("P")){ %>
			      	 		<label id="lbl_head_value"><%= cinnerlist.get(10) %></label>
	    	 						[<label id="lbl_head_value"><%= cinnerlist.get(5) %></label>
	     	 						% of <label id="lbl_sub_head"><%= cinnerlist.get(13)!=null && cinnerlist.get(13).equals("") ? "": cinnerlist.get(13) %></label>]	
			      	 	<%}else{ %>
				      	 	<!-- Amount -->
				      	 	<%=cinnerlist.get(5) %>
				      	<%}
			      	 	
			      	 	if(uF.parseToBoolean((String)cinnerlist.get(6))) {
				      	if(alAssignToPercentageHead.contains((String)cinnerlist.get(0))){
				      	%>
				      		 <a style="color:red" href="javascript:void(0)" onclick="alert('This head is align with other head.');" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
				      	<%} else { %>	
	       					<a style="color:red" href="javascript:void(0)" onclick="removeField(this.id)" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
	       				<%}
				      	}
				      	
			      	 	if(uF.parseToBoolean((String)cinnerlist.get(7))) {
				      		%>
		       					<a href="#?w=400" rel="popup_name_edit_<%=cinnerlist.get(8)%>" class="poplight">Edit..</a>
	       				<%}%>
                    </div>
                </div>
                <div class="clr"></div>
                <%} %>
                <%}%>
            </div>
            <div class="buttons">
                <a href="#?w=400" rel="popup_incentive" class="poplight" ">Add New Field..</a>
            </div>
            <div class="clr"></div>
            <div>
                <h4>ALLOWANCE (PART OF SALARY)</h4>
                <p>Please add a field and then go to <a target="_blank" href="MyDashboard.action?strOrg=<%=request.getAttribute("strOrg")%>&strLevel=<%=request.getAttribute("level")%>&userscreen=<%=IConstants.ADMIN %>&navigationId=125&toPage=AP">allowance section</a> to update policy</p>
                <hr style="border:1px solid gray;"/>
            </div>
            <div class="clr"></div>
            <div class="details_lables" >
                <%isBenefit = false;
                    for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
                <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
                    %>
                <% 
                    if(cinnerlist.get(2).equals("E") && uF.parseToBoolean(""+cinnerlist.get(12))) { %>	
                <input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
                <input type="hidden" name="salary_head_name" value='<%= cinnerlist.get(1) %>'></input>
                <input type="hidden" name="salary_head_byte" value='<%= cinnerlist.get(2) %>'></input>
                <%if(uF.parseToInt((String)cinnerlist.get(9))>990 && !isBenefit){ isBenefit=true;%>
                Benefits
                <hr/>
                <%} %>
                <div class="row row_without_margin">
                    <div class="col1 tdDashLabel">
                        <label id="lbl"><%= cinnerlist.get(1) %>:</label>	
                    </div>
                    <div class="col2">
                       <%if(cinnerlist.get(3).equals("P")){ %>
			      	 		<label id="lbl_head_value"><%= cinnerlist.get(10) %></label>
     	 						[<label id="lbl_head_value"><%= cinnerlist.get(5) %></label>
      	 						% of <label id="lbl_sub_head"><%= cinnerlist.get(13)!=null && cinnerlist.get(13).equals("") ? "": cinnerlist.get(13) %></label>]
			      	 	<%}else{ %>
				      	 	<!-- Amount -->
				      	 	<%=cinnerlist.get(5) %>
				      	<%}
			      	 	
			      	 	if(uF.parseToBoolean((String)cinnerlist.get(6))) {
					      	if(alAssignToPercentageHead.contains((String)cinnerlist.get(0))){
				      	%>
				      		 <a style="color:red" href="javascript:void(0)" onclick="alert('This head is align with other head.');" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
				      	<%} else { %>	
	       					<a style="color:red" href="javascript:void(0)" onclick="removeField(this.id)" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
	       				<%}
				      	}
				      	
			      	 	if(uF.parseToBoolean((String)cinnerlist.get(7))) {
				      		%>
		       					<a href="#?w=400" rel="popup_name_edit_<%=cinnerlist.get(8)%>" class="poplight">Edit..</a>
	       				<%}%>
                    </div>
                </div>
                <div class="clr"></div>
                <%} %>
                <%}%>
            </div>
            <div class="buttons">
                <a href="#?w=400" rel="popup_allowance" class="poplight" ">Add New Field..</a>
            </div>
            <div class="clr"></div>
        </div>
        
        <div class="deduction col-lg-6 col-md-6 col_no_padding" id="frm_deduction">
            <div class="heading1">
                <h4>DEDUCTION DETAILS</h4>
            </div>
            <!-- <form id="frm_deduction" name="frm_deduction" action="SalaryDetails.action.action" onsubmit="return saveDeductionAll()" > -->
            <div class="details_lables" >
                <% 
                    for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
                <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
                <% if(cinnerlist.get(2).equals("D")) { %>	
                <input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
                <input type="hidden" name="salary_head_name" value='<%= cinnerlist.get(1) %>'></input>
                <input type="hidden" name="salary_head_byte" value='<%= cinnerlist.get(2) %>'></input>
                <div class="row row_without_margin">
                    <div class="col1 tdDashLabel">
                        <label id="lbl" ><%= cinnerlist.get(1) %>:</label>
                    </div>
                    <div class="col2">
                        <%if(cinnerlist.get(3).equals("P")){ %>
			      	 		<label id="lbl_head_value"><%= cinnerlist.get(10) %></label>
     	 						[<label id="lbl_head_value"><%= cinnerlist.get(5) %></label>
     	 							% of <label id="lbl_sub_head"><%= cinnerlist.get(13)!=null && cinnerlist.get(13).equals("") ? "": cinnerlist.get(13) %></label>]	
		      	 		<%}else{ %>
			      	 	
			      	 	<!-- Amount -->
				      	 	<%
				      	 	if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.PROFESSIONAL_TAX) {%>
				      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=PTS">Statutory Compliance</a>
				      	 	<%} else if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.EMPLOYEE_EPF) {%>
				      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=EPFS">Statutory Compliance</a>
				      	 	<%} else if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.EMPLOYEE_ESI) {%>
				      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=ESICS">Statutory Compliance</a>
				      	 	<%} else if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.EMPLOYEE_LWF) {%>
				      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=LWFS">Statutory Compliance</a>
				      	 	<%} else if(uF.parseToInt((String)cinnerlist.get(0))==IConstants.TDS) {%>
				      	 		based on <a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=131&toPage=ITS">Statutory Compliance</a>
				      	 	<%} else {%>
				      	 		<%=cinnerlist.get(5) %>
			      	 		<%} %>
			      	 	<% } %>
			      	 	
			      	 	<% if(uF.parseToBoolean((String)cinnerlist.get(6))) {
			      	 		if(alAssignToPercentageHead.contains((String)cinnerlist.get(0))){
				      	%>
				      		 <a style="color:red" href="javascript:void(0)" onclick="alert('This head is align with other head.');" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
				      	<%  } else { %>	
	       					<a style="color:red" href="javascript:void(0)" onclick="removeField(this.id)" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
	       				<%  }
			      	 	}
	       				if(uF.parseToBoolean((String)cinnerlist.get(7)) && uF.parseToInt((String)cinnerlist.get(0))!=IConstants.PROFESSIONAL_TAX 
	       						&& uF.parseToInt((String)cinnerlist.get(0))!=IConstants.EMPLOYEE_EPF 
	       						&& uF.parseToInt((String)cinnerlist.get(0))!=IConstants.EMPLOYEE_ESI 
	       						&& uF.parseToInt((String)cinnerlist.get(0))!=IConstants.EMPLOYEE_LWF 
	       						&& uF.parseToInt((String)cinnerlist.get(0))!=IConstants.TDS) {
				      	
		       				%>
		       					<a href="#?w=400" rel="popup_name_edit_<%=cinnerlist.get(8)%>" class="poplight">Edit..</a>
	       				<% } %>
                    </div>
                </div>
                <div class="clr"></div>
                <% } %>
                <% } %>
                <%-- <% if(empId!=null) { %>
                    <div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
                    	<label name="total_deduction" id="lbl">Total Deduction: </label>
                    	<label name="total_deduction_value" id= "total_deduction_value" style="color: green;"></label>
                    </div>
                    <% } %> --%>
            </div>
            <div class="buttons">
                <a href="#?w=400" rel="popup_deduction" class="poplight" ">Add New Field..</a>
            </div>
            <div class="clr"></div>
        </div>
    </div>
    </div>
    <% } else { %>
    <div class="filter">
        <div class="msg nodata"><span>Please choose the level</span></div>
    </div>
    <% } %>
    <%} %>
    <!-- div for pop - up windows -->
    <script>
    function showhideOthers(id){
		//Allowance // daSpan
		
    	if(parseInt(id)==-1){
			document.getElementById('idOtherE').style.display = "table-row";
			document.getElementById('idOtherIncentiveE').style.display = "table-row";
			document.getElementById('idOtherAllowanceE').style.display = "table-row";
			document.getElementById('idOtherD').style.display = "table-row";
			
			if(document.getElementById('headNameOtherE')){
				document.getElementById('headNameOtherE').value = '';
			}
			if(document.getElementById('headNameOtherIncentiveE')){
				document.getElementById('headNameOtherIncentiveE').value = '';
			}
			if(document.getElementById('headNameOtherAllowanceE')){
				document.getElementById('headNameOtherAllowanceE').value = '';
			}
			if(document.getElementById('headNameOtherD')){
				document.getElementById('headNameOtherD').value = '';
			}
		}else{
			if(document.getElementById('headNameOtherE')){
				document.getElementById('headNameOtherE').value = '';
			}
			if(document.getElementById('headNameOtherIncentiveE')){
				document.getElementById('headNameOtherIncentiveE').value = '';
			}
			if(document.getElementById('headNameOtherAllowanceE')){
				document.getElementById('headNameOtherAllowanceE').value = '';
			}
			if(document.getElementById('headNameOtherD')){
				document.getElementById('headNameOtherD').value = '';
			}
			
			document.getElementById('idOtherE').style.display = 'none';
			document.getElementById('idOtherIncentiveE').style.display = "none";
			document.getElementById('idOtherAllowanceE').style.display = "none";
			document.getElementById('idOtherD').style.display = 'none';
		}
		
		var sHeadArray=checkSalaryHead.split(",");
		if(parseInt(id)==0){
			if(document.getElementById('id_isvariableE')){
				document.getElementById('id_isvariableE').style.display = "none";
			}
			if(document.getElementById('id_isAnnualVariableE')){
				document.getElementById('id_isAnnualVariableE').style.display = "none";
			}
			if(document.getElementById('id_isvariableIncentiveE')){
				document.getElementById('id_isvariableIncentiveE').style.display = "none";
			}
			if(document.getElementById('id_isAnnualVariableIncentiveE')){
				document.getElementById('id_isAnnualVariableIncentiveE').style.display = "none";
			}
			if(document.getElementById('id_isvariableAllowanceE')){
				document.getElementById('id_isvariableAllowanceE').style.display = "none";
			}
			if(document.getElementById('id_isvariableD')){
				document.getElementById('id_isvariableD').style.display = "none";
			}
			if(document.getElementById('id_isAnnualVariableD')){
				document.getElementById('id_isAnnualVariableD').style.display = "none";
			}
			if(document.getElementById('id_isAlignPerkE')){
				document.getElementById('id_isAlignPerkE').style.display = "none";
			}
			if(document.getElementById('id_isAlignPerkIncentiveE')){
				document.getElementById('id_isAlignPerkIncentiveE').style.display = "none";
			}
			if(document.getElementById('id_isAlignPerkD')){
				document.getElementById('id_isAlignPerkD').style.display = "none";
			}
		}else if(!contains(sHeadArray, id)){
			if(document.getElementById('id_isvariableE')){
				document.getElementById('id_isvariableE').style.display = "table-row";
			}
			if(document.getElementById('id_isAnnualVariableE')){
				document.getElementById('id_isAnnualVariableE').style.display = "table-row";
			}
			if(document.getElementById('id_isvariableIncentiveE')){
				document.getElementById('id_isvariableIncentiveE').style.display = "table-row";
			}
			if(document.getElementById('id_isAnnualVariableIncentiveE')){
				document.getElementById('id_isAnnualVariableIncentiveE').style.display = "table-row";
			}
			if(document.getElementById('id_isvariableAllowanceE')){
				document.getElementById('id_isvariableAllowanceE').style.display = "table-row";
			}
			if(document.getElementById('id_isvariableD')){
				document.getElementById('id_isvariableD').style.display = "table-row";
			}
			if(document.getElementById('id_isAnnualVariableD')){
				document.getElementById('id_isAnnualVariableD').style.display = "table-row";
			}
			if(document.getElementById('id_isAlignPerkE')){
				document.getElementById('id_isAlignPerkE').style.display = "table-row";
			}
			if(document.getElementById('id_isAlignPerkIncentiveE')){
				document.getElementById('id_isAlignPerkIncentiveE').style.display = "table-row";
			}
			if(document.getElementById('id_isAlignPerkD')){
				document.getElementById('id_isAlignPerkD').style.display = "table-row";
			}
		}else{
			if(document.getElementById('id_isvariableE')){
				document.getElementById('id_isvariableE').style.display = "none";
			}
			if(document.getElementById('id_isAnnualVariableE')){
				document.getElementById('id_isAnnualVariableE').style.display = "none";
			}
			if(document.getElementById('id_isvariableIncentiveE')){
				document.getElementById('id_isvariableIncentiveE').style.display = "none";
			}
			if(document.getElementById('id_isAnnualVariableIncentiveE')){
				document.getElementById('id_isAnnualVariableIncentiveE').style.display = "none";
			}
			if(document.getElementById('id_isvariableAllowanceE')){
				document.getElementById('id_isvariableAllowanceE').style.display = "none";
			}
			if(document.getElementById('id_isvariableD')){
				document.getElementById('id_isvariableD').style.display = "none";
			}
			if(document.getElementById('id_isAnnualVariableD')){
				document.getElementById('id_isAnnualVariableD').style.display = "none";
			}
			if(document.getElementById('id_isAlignPerkE')){
				document.getElementById('id_isAlignPerkE').style.display = "none";
			}
			if(document.getElementById('id_isAlignPerkIncentiveE')){
				document.getElementById('id_isAlignPerkIncentiveE').style.display = "none";
			}
			if(document.getElementById('id_isAlignPerkD')){
				document.getElementById('id_isAlignPerkD').style.display = "none";
			}
		}
		
		if(document.getElementById('isCTCVariableE')){
			document.getElementById('isCTCVariableE').checked = false;
		}
		if(document.getElementById('isCTCVariableIncentiveE')){
			document.getElementById('isCTCVariableIncentiveE').checked = false;
		}
		if(document.getElementById('isCTCVariableAllowanceE')){
			document.getElementById('isCTCVariableAllowanceE').checked = false;
		}
		if(document.getElementById('isCTCVariableD')){
			document.getElementById('isCTCVariableD').checked = false;
		}
		
		if(document.getElementById('isVariableE')){
			document.getElementById('isVariableE').checked = false;
		}
		if(document.getElementById('isVariableIncentiveE')){
			document.getElementById('isVariableIncentiveE').checked = false;
		}
		if(document.getElementById('isVariableAllowanceE')){
			document.getElementById('isVariableAllowanceE').checked = false;
		}
		if(document.getElementById('isVariableD')){
			document.getElementById('isVariableD').checked = false;
		}
		
		if(document.getElementById('isTaxExemptionE')){
			document.getElementById('isTaxExemptionE').checked = false;
		}
		if(document.getElementById('isTaxExemptionIncentiveE')){
			document.getElementById('isTaxExemptionIncentiveE').checked = false;
		}
		if(document.getElementById('isTaxExemptionAllowanceE')){
			document.getElementById('isTaxExemptionAllowanceE').checked = false;
		}
		if(document.getElementById('isTaxExemptionD')){
			document.getElementById('isTaxExemptionD').checked = false;
		}
		
		if(document.getElementById("id_isTaxExemptE")) {
			document.getElementById("id_isTaxExemptE").style.display = "none";
    	}
		if(document.getElementById("id_isTaxExemptIncentiveE")) {
			document.getElementById("id_isTaxExemptIncentiveE").style.display = "none";
    	}
		if(document.getElementById("id_isTaxExemptAllowanceE")) {
			document.getElementById("id_isTaxExemptAllowanceE").style.display = "none";
    	}
		if(document.getElementById("id_isTaxExemptD")) {
			document.getElementById("id_isTaxExemptD").style.display = "none";
    	}
		
		if(document.getElementById('salarySubHeadE')){
			document.getElementById("salarySubHeadE").selectedIndex = "0";
		}
		if(document.getElementById('salarySubHeadIncentiveE')){
			document.getElementById("salarySubHeadIncentiveE").selectedIndex = "0";
		}
		if(document.getElementById('salarySubHeadAllowanceE')){
			document.getElementById("salarySubHeadAllowanceE").selectedIndex = "0";
		}
		if(document.getElementById('id_salaryHead')){
			document.getElementById("id_salaryHead").selectedIndex = "0";
		}
		
		if(document.getElementById('headAmountE')){
			document.getElementById('headAmountE').value = '';
		}
		if(document.getElementById('headAmountIncentiveE')){
			document.getElementById('headAmountIncentiveE').value = '';
		}
		if(document.getElementById('headAmountAllowanceE')){
			document.getElementById('headAmountAllowanceE').value = '';
		}
		if(document.getElementById('headAmountD')){
			document.getElementById('headAmountD').value = '';
		}
		
		if(document.getElementById('isAlignPerkE')){
			document.getElementById('isAlignPerkE').checked = false;
		}
		if(document.getElementById('isAlignPerkIncentiveE')){
			document.getElementById('isAlignPerkIncentiveE').checked = false;
		}
		if(document.getElementById('isAlignPerkD')){
			document.getElementById('isAlignPerkD').checked = false;
		}
		
		if(parseInt(id)==2){
			document.getElementById('daSpan').innerHTML = "This DA will be added with basic by default.";
		} else {
			document.getElementById('daSpan').innerHTML = "";
		}
	}
        
        function contains(arr, value) {
            var i = arr.length;
            while (i--) {
                if (arr[i] === value) return true;
            }
            return false;
        }
        
    </script>
    <div id="popup_name" class="popup_block">
        <h5 class="textcolorWhite">Add New Earning Head</h5>
        <s:form id="frmCreditDetails" action="SalaryDetails" method="post" theme="simple">
            <s:hidden name="operation" value="A"></s:hidden>
            <s:hidden name="earningOrDeduction" id="headByte" value="E"></s:hidden>
            <s:hidden name="level"></s:hidden>
            <s:hidden name="strOrg"></s:hidden>
            <s:hidden name="strGrade"></s:hidden>
            <s:hidden name="salaryBand"></s:hidden>
            <s:hidden name="userscreen"/>
			<s:hidden name="navigationId"/>
			<s:hidden name="toPage"/>
            <table class="table table_no_border">
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;" nowrap="nowrap" valign="top">Salary Head Name:</td>
                    <td>
                        <s:select theme="simple" label="Select Salary Head" name="headName" listKey="salaryHeadId"
                            listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
                            list="salaryHeadListEarning" key="" required="true" onchange="showhideOthers(this.value)"/>
                        <br/>
                        <span id="daSpan" style="color: rgb(255, 255, 255); font-style: italic; font-size: 10px;width:100px;"></span>		
                    </td>
                </tr>
                <tr id="idOtherE" style="display:none">
                    <td>&nbsp;</td>
                    <td>
                        <s:textfield name="headNameOther" id="headNameOtherE"></s:textfield>
                    </td>
                </tr>
                <tr id="id_isctcvariableE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is CTC Variable:</td>
                    <td><input type="checkbox" name="isCTCVariable" id="isCTCVariableE" onclick="showTaxExemption(this.id);"/></td>
                </tr>
                <tr id="id_isvariableE" style="display:none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is variable:</td>
                    <td><input type="checkbox" name="isVariable" id="isVariableE"/></td>
                </tr>
                <tr id="id_isAnnualVariableE" style="display:none;">
					<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Annual Variable:</td>
					<td><input type="checkbox" name="isAnnualVariable" id="isAnnualVariableE"/></td>
				</tr>
                <tr id="id_isTaxExemptE" style="display:none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Tax Exemption:</td>
                    <td><input type="checkbox" name="isTaxExemption" id="isTaxExemptionE"/></td>
                </tr>
                <tr id="id_isAlignPerkE" style="display:none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;" nowrap="nowrap">Is Aligned with Perk:</td>
                    <td><input type="checkbox" name="isAlignPerk" id="isAlignPerkE"/></td>
                </tr>
                <tr id="id_isContributionE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Contribution:</td>
                    <td><input type="checkbox" name="isContribution" id="isContributionE"/></td>
                </tr>
                <tr id="trTypeOfHeadE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Type of Head:</td>
                    <td>
                        <select id="newSelE" name="headAmountType" style="width:91px" onchange="javascript:show_sub_salary_head(this.id);return false;" >
                           <option value="A">A (Amount)</option>
		   	 				<option value="P">% (Percentage)</option>
		   	 				<!-- <option value="MP">% (Multiple)</option>
		   	 				<option value="M">A (Multiple)</option> -->
                        </select>
                    </td>
                </tr>
                <%-- <tr id="id_salaryHeadE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">% Of:</td>
                    <td>
                        <s:select theme="simple" label="Select Salary Head" name="salarySubHead" id="salarySubHeadE" listKey="salaryHeadId"
                            listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
                            list="salaryHeadList" key="" required="true"/>
                    </td>
                </tr> --%>
                <tr id="id_headAmountE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"><span id="id_headAmountTypeE">Percentage:</span></td>
                    <td><input type="text" name="headAmount" id="headAmountE" onkeypress="return isNumberKey(event)"/></td>
                </tr>
                
                <tr id="id_headMaxCapAmountE" style="display: none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"><span>Max Cap Amount:</span></td>
                    <td><input type="text" name="headMaxCapAmount" id="headMaxCapAmountE" onkeypress="return isNumberKey(event)"/></td>
                </tr>

                <tr id="trMultiplePercentageCalTypeE" style="display: none;">
					<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">&nbsp;</td>
					<td> 
						<select id="bracketMulPE" name="bracketMulP" style="width:50px !important;">
			   	 				<option value="">Select</option>
			   	 				<option value="+">+</option>
			   	 				<option value="-">-</option>
			   	 				<option value="*">*</option>
			   	 				<option value="/">/</option>
			   	 				<option value="(">(</option>
			   	 				<option value=")">)</option>
			      	 	</select> 
			      	 	<a href="javascript:void(0)" class="add_lvl" onclick="addMulPerCal('1','bracketMulPE','salaryHeadMulPE','strMulPerCalFormulaE','spanMulCalPercentageE');" title="Add Sign"></a>
						<s:select theme="simple" name="salaryHeadMulP" id="salaryHeadMulPE" cssStyle="width:91px !important;" listKey="salaryHeadId"
								listValue="salaryHeadName" headerKey="" headerValue="Select Salary Head" list="salaryHeadList" key="" required="true"/>
						<a href="javascript:void(0)" class="add_lvl" onclick="addMulPerCal('2','bracketMulPE','salaryHeadMulPE','strMulPerCalFormulaE','spanMulCalPercentageE');" title="Add Head"></a>
						<img src="images1/icons/hd_cross_16x16.png" style="vertical-align: top;" onclick="resetMulPerCal('strMulPerCalFormulaE','spanMulCalPercentageE');">
						<br/>
						<input type="hidden" name="strMulPerCalFormula" id="strMulPerCalFormulaE" value=""/>
						<span id="spanMulCalPercentageE" style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"></span> 
			      	 </td>
				</tr>
                
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Salary Type:</td>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">
                        <input type="radio" name="salary_type" value="M" checked/> Monthly
                        <input type="radio" name="salary_type" value="D"/> Daily
                        <input type="radio" name="salary_type" value="F"/> Fixed
                    </td>
                </tr>
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">&nbsp;</td>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">
                    	<input type="checkbox" name="autoUpdate" id="autoUpdateE"/>&nbsp;Auto update salary structure to all employee
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <s:submit cssClass="btn btn-primary" value="Add Earning Head" align="center"></s:submit>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
    <div id="popup_incentive" class="popup_block">
        <h5 class="textcolorWhite">Add New Earning Incentive Head</h5>
        <s:form id="frmCreditDetails" action="SalaryDetails" method="post" theme="simple">
            <s:hidden name="operation" value="A"></s:hidden>
            <s:hidden name="earningOrDeduction" id="headByte" value="E"></s:hidden>
            <s:hidden name="level"></s:hidden>
            <s:hidden name="strOrg"></s:hidden>
            <s:hidden name="strIsIncentive" value="true"></s:hidden>
            <s:hidden name="strGrade"></s:hidden>
            <s:hidden name="salaryBand"></s:hidden>
            <s:hidden name="userscreen"/>
			<s:hidden name="navigationId"/>
			<s:hidden name="toPage"/>
			
            <table class="table table_no_border">
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;" nowrap="nowrap">Salary Head Name:</td>
                    <td>
                        <s:select theme="simple" label="Select Salary Head" name="headName" listKey="salaryHeadId"
                            listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
                            list="salaryHeadListIncentiveEarning" key="" required="true" onchange="showhideOthers(this.value)"/>
                    </td>
                </tr>
                <tr id="idOtherIncentiveE" style="display:none">
                    <td>&nbsp;</td>
                    <td>
                        <s:textfield name="headNameOther" id="headNameOtherIncentiveE"></s:textfield>
                    </td>
                </tr>
                <tr id="id_isctcvariableIncentiveE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is CTC Variable:</td>
                    <td><input type="checkbox" name="isCTCVariable" id="isCTCVariableIncentiveE" onclick="showTaxExemption(this.id);"/></td>
                </tr>
                <tr id="id_isvariableIncentiveE" style="display:none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is variable:</td>
                    <td><input type="checkbox" name="isVariable" id="isVariableIncentiveE"/></td>
                </tr>
                <tr id="id_isAnnualVariableIncentiveE" style="display:none;">
					<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Annual Variable:</td>
					<td><input type="checkbox" name="isAnnualVariable" id="isAnnualVariableIncentiveE"/></td>
				</tr>
                <tr id="id_isTaxExemptIncentiveE" style="display:none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Tax Exemption:</td>
                    <td><input type="checkbox" name="isTaxExemption" id="isTaxExemptionIncentiveE"/></td>
                </tr>
                <tr id="id_isAlignPerkIncentiveE" style="display:none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;" nowrap="nowrap">Is Aligned with Perk:</td>
                    <td><input type="checkbox" name="isAlignPerk" id="isAlignPerkIncentiveE"/></td>
                </tr>
                <tr id="id_isContributionIncentiveE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Contribution:</td>
                    <td><input type="checkbox" name="isContribution" id="isContributionIncentiveE"/></td>
                </tr>
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Type of Head:</td>
                    <td>
                        <select id="newSelIncentiveE" name="headAmountType" style="width:91px" onchange="javascript:show_sub_salary_head(this.id);return false;" >
                            <option value="A">A (Amount)</option>
			   	 			<option value="P">% (Percentage)</option>
			   	 			<!-- <option value="MP">% (Multiple)</option>
			   	 			<option value="M">A (Multiple)</option> -->
                        </select>
                    </td>
                </tr>
                <%-- <tr id="id_salaryHeadIncentiveE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">% Of:</td>
                    <td>
                        <s:select theme="simple" label="Select Salary Head" name="salarySubHead" id="salarySubHeadIncentiveE" listKey="salaryHeadId"
                            listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
                            list="salaryHeadList" key="" required="true"/>
                    </td>
                </tr> --%>
                <tr id="id_headAmountIncentiveE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"><span id="id_headAmountIncentiveTypeE">Percentage:</span></td>
                    <td><input type="text" name="headAmount" id="headAmountIncentiveE" onkeypress="return isNumberKey(event)"/></td>
                </tr>
                
                <tr id="id_headMaxCapAmountIncentiveE" style="display: none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"><span>Max Cap Amount:</span></td>
                    <td><input type="text" name="headMaxCapAmount" id="headMaxCapAmountIncentiveE" onkeypress="return isNumberKey(event)"/></td>
                </tr>

                <tr id="trMultiplePercentageCalTypeIncentiveE" style="display: none;">
					<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">&nbsp;</td>
					<td> 
						<select id="bracketMulPIncentiveE" name="bracketMulP" style="width:50px !important;">
			   	 				<option value="">Select</option>
			   	 				<option value="+">+</option>
			   	 				<option value="-">-</option>
			   	 				<option value="*">*</option>
			   	 				<option value="/">/</option>
			   	 				<option value="(">(</option>
			   	 				<option value=")">)</option>
			      	 	</select> 
			      	 	<a href="javascript:void(0)" class="add_lvl" onclick="addMulPerCal('1','bracketMulPIncentiveE','salaryHeadMulPIncentiveE','strMulPerCalFormulaIncentiveE','spanMulCalPercentageIncentiveE');" title="Add Sign"></a>
						<s:select theme="simple" name="salaryHeadMulP" id="salaryHeadMulPIncentiveE" cssStyle="width:91px !important;" listKey="salaryHeadId"
								listValue="salaryHeadName" headerKey="" headerValue="Select Salary Head" list="salaryHeadList" key="" required="true"/>
						<a href="javascript:void(0)" class="add_lvl" onclick="addMulPerCal('2','bracketMulPIncentiveE','salaryHeadMulPIncentiveE','strMulPerCalFormulaIncentiveE','spanMulCalPercentageIncentiveE');" title="Add Head"></a>
						<img src="images1/icons/hd_cross_16x16.png" style="vertical-align: top;" onclick="resetMulPerCal('strMulPerCalFormulaIncentiveE','spanMulCalPercentageIncentiveE');">
						<br/>
						<input type="hidden" name="strMulPerCalFormula" id="strMulPerCalFormulaIncentiveE" value=""/>
						<span id="spanMulCalPercentageIncentiveE" style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"></span> 
			      	 </td>
				</tr>
                
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Salary Type:</td>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">
                        <input type="radio" name="salary_type" value="M" checked/> Monthly
                        <input type="radio" name="salary_type" value="D"/> Daily
                        <input type="radio" name="salary_type" value="F"/> Fixed
                    </td>
                </tr>
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">&nbsp;</td>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">
                    	<input type="checkbox" name="autoUpdate" id="autoUpdateIncentiveE"/>&nbsp;Auto update salary structure to all employee
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <s:submit cssClass="btn btn-primary" value="Add Earning Head" align="center"></s:submit>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
    
    <div id="popup_allowance" class="popup_block">
        <h5 class="textcolorWhite">Add New Earning Allowance Head</h5>
        <s:form id="frmCreditDetails" action="SalaryDetails" method="post" theme="simple">
            <s:hidden name="operation" value="A"></s:hidden>
            <s:hidden name="earningOrDeduction" id="headByte" value="E"></s:hidden>
            <s:hidden name="level"></s:hidden>
            <s:hidden name="strOrg"></s:hidden>
            <s:hidden name="strIsAllowance" value="true"></s:hidden>
            <s:hidden id="newSelAllowanceE" name="headAmountType" value="A"></s:hidden>
            <!-- <input type="hidden" name="headAmount" id="headAmountAllowanceE" value="0"/> -->
            <s:hidden name="strGrade"></s:hidden>
            <s:hidden name="salaryBand"></s:hidden>
            <s:hidden name="userscreen"/>
			<s:hidden name="navigationId"/>
			<s:hidden name="toPage"/>
            <table class="table table_no_border">
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;" nowrap="nowrap">Salary Head Name:</td>
                    <td>
                        <s:select theme="simple" label="Select Salary Head" name="headName" listKey="salaryHeadId"
                            listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
                            list="salaryHeadListAllowanceEarning" key="" required="true" onchange="showhideOthers(this.value)"/>
                    </td>
                </tr>
                <tr id="idOtherAllowanceE" style="display:none">
                    <td>&nbsp;</td>
                    <td>
                        <s:textfield name="headNameOther" id="headNameOtherAllowanceE"></s:textfield>
                    </td>
                </tr>
                <%-- <tr id="id_isctcvariableAllowanceE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is CTC Variable:</td>
                    <td><input type="checkbox" name="isCTCVariable" id="isCTCVariableAllowanceE" onclick="showTaxExemption(this.id);"/></td>
                    </tr>
                    
                    <tr id="id_isvariableAllowanceE" style="display:none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is variable:</td>
                    <td><input type="checkbox" name="isVariable" id="isVariableAllowanceE"/></td>
                    </tr>
                    
                    <tr id="id_isTaxExemptAllowanceE" style="display:none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Tax Exemption:</td>
                    <td><input type="checkbox" name="isTaxExemption" id="isTaxExemptionAllowanceE"/></td>
                    </tr>
                    
                    <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Type of Head:</td>
                    <td>
                    	<select id="newSelAllowanceE" name="headAmountType" style="width:50px !important" onchange="javascript:show_sub_salary_head(this.id);return false;" >
                     	 				<option value="P">%</option>
                     	 				<option value="A">A</option>
                        	 	</select>
                    </td>
                    </tr> 
                    
                    <tr id="id_salaryHeadAllowanceE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">% Of:</td>
                    <td>
                    	<s:select theme="simple" label="Select Salary Head" name="salarySubHead" id="salarySubHeadAllowanceE" listKey="salaryHeadId"
                    			listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
                    			list="salaryHeadList" key="" required="true"/>
                    </td>
                    </tr>--%>
                <%-- <tr id="id_headAmountAllowanceE">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"><span id="id_headAmountAllowanceTypeE">Amount (<%=uF.showData(currency, "") %>):</span></td>
                    <td><input type="text" name="headAmount" id="headAmountAllowanceE" onkeypress="return isNumberKey(event)"/></td>
                    </tr> --%> 
                    
                <tr id="id_isDefaultCalculateAllowanceE">
					<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Calculate with default figure:</td>
					<td><input type="checkbox" name="isDefaultCalculateAllowance" id="isDefaultCalculateAllowanceE" onclick="showDefaultAmount(this.id);"/></td>
				</tr>
				
				<tr id="id_headAmountAllowanceE" style="display:none;">  
					<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Default Amount (<%=uF.showData(currency, "") %>):</td>
					<td><input type="text" name="headAmount" id="headAmountAllowanceE" onkeypress="return isNumberKey(event)"/></td>
				</tr>  
				  
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Salary Type:</td>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">
                        <!-- <input type="radio" name="salary_type" value="M" checked/> Monthly
							<input type="radio" name="salary_type" value="D"/> Daily
							<input type="radio" name="salary_type" value="F"/> Fixed --> 
							<input type="hidden" name="salary_type" value="F"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <s:submit cssClass="btn btn-primary" value="Add Earning Head" align="center"></s:submit>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
    <div id="popup_deduction" class="popup_block">
        <h5 class="textcolorWhite">Add New Deduction Head</h5>
        <s:form id="frmdeductionDetails"  action="SalaryDetails" method="post" theme="simple">
            <s:hidden name="operation" value="A"></s:hidden>
            <s:hidden name="earningOrDeduction" id="headByte" value="D"></s:hidden>
            <s:hidden name="level"></s:hidden>
            <s:hidden name="strOrg"></s:hidden>
            <s:hidden name="strGrade"></s:hidden>
            <s:hidden name="salaryBand"></s:hidden>
            <s:hidden name="userscreen"/>
			<s:hidden name="navigationId"/>
			<s:hidden name="toPage"/>
            <table class="table table_no_border">
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;" nowrap="nowrap">Salary Head Name:</td>
                    <td>
                        <s:select theme="simple" label="Select Salary Head" name="headName" listKey="salaryHeadId"
                            listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
                            list="salaryHeadListDeduction" key="" required="true" onchange="showhideOthers(this.value)"/>
                    </td>
                </tr>
                <tr id="idOtherD" style="display:none">
                    <td>&nbsp;</td>
                    <td>
                        <s:textfield name="headNameOther" id="headNameOtherD"></s:textfield>
                    </td>
                </tr>
                <tr id="id_isctcvariableD">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is CTC Variable:</td>
                    <td><input type="checkbox" name="isCTCVariable" id="isCTCVariableD" onclick="showTaxExemption(this.id);"/></td>
                </tr>
                <tr id="id_isvariableD" style="display:none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is variable:</td>
                    <td><input type="checkbox" name="isVariable" id="isVariableD"/></td>
                </tr>
                <!-- <tr id="id_isAnnualVariableD" style="display:none;">
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Annual Variable:</td>
						<td><input type="checkbox" name="isAnnualVariable" id="isAnnualVariableD"/></td>
					</tr>  -->
                <tr id="id_isTaxExemptD" style="display: none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Tax Exemption:</td>
                    <td><input type="checkbox" name="isTaxExemption" id="isTaxExemptionD"/></td>
                </tr>
                <!-- <tr id="id_isAlignPerkD" style="display:none;">  
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;" nowrap="nowrap">Is Aligned with Perk:</td>
                    <td><input type="checkbox" name="isAlignPerk" id="isAlignPerkD"/></td>
                    </tr> --> 
                <tr id="id_isContributionD">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is Contribution:</td>
                    <td><input type="checkbox" name="isContribution" id="isContributionD"/></td>
                </tr>
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Type of Head:</td>
                    <td>
                        <select id="newSelD" name="headAmountType" style="width:91px" onchange="javascript:show_sub_salary_head(this.id);return false;" >
                            <option value="A">A (Amount)</option>
			   	 			<option value="P">% (Percentage)</option>
			   	 			<!-- <option value="MP">% (Multiple)</option>
			   	 			<option value="M">A (Multiple)</option> -->
                        </select>
                    </td>
                </tr>
                <%-- <tr id="id_salaryHeadD">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">% Of:</td>
                    <td>
                        <s:select theme="simple" label="Select Salary Head" name="salarySubHead" id="id_salaryHead" listKey="salaryHeadId"
                            listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head"
                            list="salaryHeadList" key="" required="true"/>
                    </td>
                </tr> --%> 
                <tr id="id_headAmountD">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"><span id="id_headAmountTypeD">Percentage:</span></td>
                    <td><input type="text" name="headAmount" id="headAmountD" onkeypress="return isNumberKey(event)"/></td>
                </tr>
                
                <tr id="id_headMaxCapAmountD" style="display: none;">
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"><span>Max Cap Amount:</span></td>
                    <td><input type="text" name="headMaxCapAmount" id="headMaxCapAmountD" onkeypress="return isNumberKey(event)"/></td>
                </tr>
                
                <tr id="trMultiplePercentageCalTypeD" style="display: none;">
					<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">&nbsp;</td>
					<td> 
						<select id="bracketMulPD" name="bracketMulP" style="width:50px !important;">
			   	 				<option value="">Select</option>
			   	 				<option value="+">+</option>
			   	 				<option value="-">-</option>
			   	 				<option value="*">*</option>
			   	 				<option value="/">/</option>
			   	 				<option value="(">(</option>
			   	 				<option value=")">)</option>
			      	 	</select> 
			      	 	<a href="javascript:void(0)" class="add_lvl" onclick="addMulPerCal('1','bracketMulPD','salaryHeadMulPD','strMulPerCalFormulaD','spanMulCalPercentageD');" title="Add Sign"></a>
						<s:select theme="simple" name="salaryHeadMulP" id="salaryHeadMulPD" cssStyle="width:91px !important;" listKey="salaryHeadId"
								listValue="salaryHeadName" headerKey="" headerValue="Select Salary Head" list="salaryHeadList" key="" required="true"/>
						<a href="javascript:void(0)" class="add_lvl" onclick="addMulPerCal('2','bracketMulPD','salaryHeadMulPD','strMulPerCalFormulaD','spanMulCalPercentageD');" title="Add Head"></a>
						<img src="images1/icons/hd_cross_16x16.png" style="vertical-align: top;" onclick="resetMulPerCal('strMulPerCalFormulaD','spanMulCalPercentageD');">
						<br/>
						<input type="hidden" name="strMulPerCalFormula" id="strMulPerCalFormulaD" value=""/>
						<span id="spanMulCalPercentageD" style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;"></span> 
			      	 </td>
				</tr>
                
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Salary Type:</td>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">
                        <input type="radio" name="salary_type" value="M" checked/> Monthly
                        <input type="radio" name="salary_type" value="D"/> Daily
                        <input type="radio" name="salary_type" value="F"/> Fixed
                    </td>
                </tr>
                <tr>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">&nbsp;</td>
                    <td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">
                    	<input type="checkbox" name="autoUpdate" id="autoUpdateD"/>&nbsp;Auto update salary structure to all employee
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <s:submit cssClass="btn btn-primary" value="Add Deduction Head" align="center"></s:submit>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
    <%if(request.getAttribute("sb")!=null) { 
        out.println(request.getAttribute("sb"));
        }%>
</div>