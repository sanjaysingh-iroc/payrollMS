
<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

   
<%
   	UtilityFunctions uF = new UtilityFunctions();
   	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
   	String strEmpID = (String) request.getAttribute("EMPID");
   	String strProID = (String) request.getParameter("PROFILEID");
   	String strSessionEmpID = (String) session.getAttribute("EMPID");

   	ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
   	List<List<String>> alSkills = (List<List<String>>) request.getAttribute("alSkills");
   	List<List<String>> alHobbies = (List<List<String>>) request.getAttribute("alHobbies");
   	List<List<String>> alLanguages = (List<List<String>>) request.getAttribute("alLanguages");
   	List<List<String>> alEducation = (List<List<String>>) request.getAttribute("alEducation");
   	List<List<String>> alFamilyMembers = (List<List<String>>) request.getAttribute("alFamilyMembers");
   	List<List<String>> alPrevEmployment = (List<List<String>>) request.getAttribute("alPrevEmployment");
   	Map<String, String> hmEmpPrevEarnDeduct = (Map<String, String>)request.getAttribute("hmEmpPrevEarnDeduct");
   	
   	List<List<String>> alActivityDetails = (List<List<String>>) request.getAttribute("alActivityDetails");
   	
   	List<List<String>> alKRADetails = (List<List<String>>) request.getAttribute("alKRADetails");
   	List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
   	
   	Map<String, List<List<String>>> hmElementAttribute = (Map<String, List<List<String>>>) request.getAttribute("hmElementAttribute");
   	Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");

   	AngularMeter semiWorkedAbsent = (AngularMeter) request.getAttribute("KPI");
   	String semiWorkedAbsent1URL = semiWorkedAbsent.makeSession(request, "chart3");

   	boolean isFilledStatus = uF.parseToBoolean((String) request.getAttribute("isFilledStatus"));
   	//boolean isOfficialFilledStatus = uF.parseToBoolean((String) request.getAttribute("isOfficialFilledStatus"));
   	String AGGREGATE_SCORE = (String) request.getAttribute("AGGREGATE_SCORE");

   	if (strEmpID != null) {
   		strProID = strEmpID;
   	} else if (strProID != null) {
   	} else if (strSessionEmpID != null) {
   		strProID = strSessionEmpID;
   	}

   	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
   	if (hmEmpProfile == null) {
   		hmEmpProfile = new HashMap<String, String>();
   	}
   	
   	List<List<String>> leaveTypeListWithBalance = (List<List<String>>) request.getAttribute("leaveTypeListWithBalance");
   	//Map<String, String> hmLeaveTypeName = (Map<String, String>) request.getAttribute("hmLeaveTypeName");
	Map<String, String> hmEmpLeaveBalance = (Map<String, String>) request.getAttribute("hmEmpLeaveBalance");
	
   //	String strImage = hmEmpProfile.get("IMAGE");

   	String strTitle = "";
   	if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
   			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) {
   		strTitle = hmEmpProfile.get("NAME") + "'s Profile";
   	} else {
   		strTitle = "My Profile";

   	}

   	String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
   	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
   	String isDeviceIntegration = (String) request.getAttribute("IS_DEVICE_INTEGRATION");
   	
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
   	
   %>

<script>

function getKRA(id) {
	removeLoadingDiv('the_div');
	var dialogEdit = '#kra_profile';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : true,
		height : 600,
		width : 600,
		modal : true,
		title : 'KRA\'s of <%=uF.showData(hmEmpProfile.get("NAME"), "-")%> (<%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-").replace("'", "\\'")%>)',
		open : function() {
			var xhr = $.ajax({
				url : "AddKRA.action?empId="+id,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
			xhr = null;
		},
		overlay : {
			backgroundColor : '#000',
			opacity : 0.5
		}
	});

	$(dialogEdit).dialog('open');
}

</script>

		<script type="text/javascript">
			
		$(function() {
			
			$('#default').raty();
			 <%double dblPrimary = 0;
			if (alSkills != null && alSkills.size() != 0) {
				for (int i = 0; i < alSkills.size(); i++) {
					List<String> alInner = alSkills.get(i);%>
					$('#star<%=i%>').raty({
						  readOnly: true,
						  start:    <%=uF.parseToDouble(alInner.get(2)) / 2%>,
						  half: true
						});
					<%if (i == 0) {
						dblPrimary = uF.parseToDouble(alInner.get(2)) / 2;
					}
				}
			}%>
			$('#skillPrimary').raty({
				  readOnly: true,
				  start:    <%=dblPrimary%>,
				  half: true
				});
			
			<%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
				double dblScorePrimary = 0, aggregeteMarks = 0, totAllAttribMarks = 0;
				int count = 0;

				for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
					List<String> innerList = elementouterList.get(i);
					List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
					for (int j = 0; attributeouterList1 != null && j < attributeouterList1.size(); j++) {
						List<String> attributeList1 = attributeouterList1.get(j);
						totAllAttribMarks += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
						count++;
					}
				}
				aggregeteMarks = totAllAttribMarks / count;

				dblScorePrimary = aggregeteMarks / 20; 
			%>
				$('#skillPrimaryOverall').raty({
					  readOnly: true,
					  start:    <%=dblScorePrimary%>,
					  half: true
					});
				
			
			<% } %>
		});
		
		
		
		function sendMail(emp_id) {
			removeLoadingDiv('the_div');
			var dialogEdit = '#sendMail';
			dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog({
				autoOpen : false,
				bgiframe : true,
				resizable : true,
				height : 450,
				width : 650,
				modal : true,
				title : 'Send Document', 
				open : function() {
					var xhr = $.ajax({
						url : "SendMail.action?emp_id="+emp_id,
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
					xhr = null;

				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});

			$(dialogEdit).dialog('open');
		}
	 	
	function salaryPreview(id) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#salarypreview';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : true,
			height : 600,
			width : 600,
			modal : true,
			title : 'Salary Preview',
			open : function() {
				var xhr = $.ajax({
					url : "SalaryPreview.action?emp_id="+id,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;

			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open');
	}
	
	
	function salaryHistory(id) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#salaryhistory';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 600,
			width : 800,
			modal : true,
			title : 'Salary History',
			open : function() {
				var xhr = $.ajax({
					url : "SalaryHistory.action?emp_id="+id,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;

			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open');
	}
	 
    $(document).ready(function() {
 
        $('a.poplight[href^=#]').click(function() {
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


		  jQuery(".content1").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".heading_dash").click(function()
		  {
			jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("filter_close");
		  });



    });

</script>


 <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include>

<div id="printDiv" class="leftbox reportWidth">


<%
	if (!isFilledStatus) {
%>
	<div style="width:80%;text-align:center;color:red">Profile is still pending and is waiting for approval</div>
<%
	}
%>     
     
 <div class="leftholder" style="width:40%;border:solid 0px #ccc" >
 
	<div class="tableblock"  style="background:#efefef; padding:5px; border:solid 1px #d4d4d4">
    
    <div class="trow" style="background:#fff; margin:0px; width:92%; min-height: 190px;">
        
               <div style="float:left; padding:5px; width:90px;">
                       <div style="height:82px; width:84px; border:1px solid #CCCCCC; float:left; margin:2px 10px 0px 0px">
                            <%-- <img height="100" width="100" class="lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation + hmEmpProfile.get("IMAGE")%>" /> --%>
                            <%if(docRetriveLocation==null) { %>
								<img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>" />
							<%} else { %>
                            	<img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strProID+"/"+hmEmpProfile.get("IMAGE")%>" />
                            <%} %>    
                       </div>
                                <!-- <div style="text-align:center; float:left; width:85px"><a href="#?w=550" rel="popup_name" class="poplight">Edit Photo</a></div> -->
                       
               </div>
               
              <div style="float:left; border:0px #ccc solid; width: 70%;">
                       
                        <table class="table_font" style="width: 100%;">
                        <tr><td class="textblue" style="font-size: 12px; font-weight: bold;"><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%>]</td></tr>
                        <tr><td><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%>]</td></tr>
                        <%-- <tr><td><span style="float: left; margin: 2px 5px 0px 0px;"><img src="images1/mail_icon.png" style="width: 15px; height: 15px;"/></span> <%=uF.showData((String) hmEmpProfile.get("EMP_EMAIL"), "-")%></td></tr>
                        <tr><td><span style="float: left; margin: 1px 5px 0px 0px;"><img src="images1/telephone.png" style="width: 15px; height: 15px;"/></span> <%=uF.showData((String) hmEmpProfile.get("CONTACT_MOB"), "-")%></td></tr> --%>
                        <tr><td>Date of Joining: <%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></td></tr>
                        <tr><td>Reporting Manager: <%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></td></tr>
                        <% if(alSkills!=null && alSkills.size()!=0) { %>
                        <tr><td>
                        	<% for(int i=0; i<alSkills.size(); i++) { %>
                            	<strong><%=(i<alSkills.size()-1) ? ((List)alSkills.get(i)).get(1) + ", " : ((List)alSkills.get(i)).get(1)%></strong>
                        	<% } %>
                        </td></tr>
                        <% } %>    
                        <% if(alSkills!=null && alSkills.size()!=0) { %>    
                        <tr><td><div id="skillPrimary"></div></td></tr>
                        <% } %>
                       
                       <%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %> 
                       <tr><td><div style="width: 75px; float: left; margin-right: 10px; font-weight: bold;">Overall:</div> <div id="skillPrimaryOverall" style="float: left;"></div></td></tr>
	                        <%-- <% for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
								List<String> innerList = elementouterList.get(i);
							%>
							<%
							List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
							double dblTotElementAggregate = 0.0d;
							int attribCount = 0;
							double 	dblElementAggregate = 0.0d;
								for (int j = 0; attributeouterList1 != null && j < attributeouterList1.size(); j++) {
									List<String> attributeList1 = attributeouterList1.get(j);
									dblTotElementAggregate += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
									attribCount++;
								}
								if(attribCount > 0) {
									dblElementAggregate = dblTotElementAggregate / attribCount;
								}
							%>
					      <script type="text/javascript">
						        $(function() {
						        	$('#starPrimary<%=i%>').raty({
						        		readOnly: true,
						        		start: <%=dblElementAggregate > 0 ? (dblElementAggregate / 20) + "" : "0"%>,
						        		half: true,
						        		targetType: 'number'
						        	});
						        	});
				        	</script>
				        	
				        	<tr><td> <div style="width: 75px; float: left; margin-left: 10%;"><%=innerList.get(1)%>:</div> <div id="starPrimary<%=i%>" style="float: left;"></div> </td></tr>
					      	<% } %> --%>
	         		<% } %>
                        </table>
                </div>   
		</div>
		
		<%-- <div class="trow" style="background:#fff; margin:0px; border-right:solid 1px #ccc; width:93%">
        
               <div style="float:left;padding:5px; width:100px">
                       <div style="height:82px;width:84px;border:1px solid #000;float:left;margin:2px 10px 0px 0px">
                                <img class="lazy" height="100" width="100" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation + strImage%>" /></div>
                                <div style="text-align:center; float:left; width:85px"><a href="#?w=550" rel="popup_name" class="poplight">Edit Photo</a></div>
                       
               </div>
               
              <div style="float:left; border:0px #ccc solid">
                        <div>
                        <table>
                        <tr><td class="textblue" valign="bottom" style="font-size: 12px;font-weight: bold">Employee Id:<%=uF.showData(hm.get("EMPLOYEEID"), "-")%></td></tr>
                        <tr><td class="textblue" valign="bottom" style="font-size: 12px;font-weight: bold"><%=uF.showData(hm.get("NAME"), "-")%></td></tr>
                        <tr><td>Emp code: <%=uF.showData(hm.get("EMPCODE"), "-")%></td></tr>
                        <%
                        	if (uF.parseToBoolean(isDeviceIntegration)) {
                        %>
                        	<tr><td>Biometric Id: <%=uF.showData(hm.get("BIO_ID"), "-")%></td></tr>
                        <%
                        	}
                        %>
                        <tr><td>Email: <%=uF.showData(hm.get("EMP_EMAIL"), "-")%></td></tr>
                        </table>
                        
                        </div>
                      <div class="clr"></div>  
                       <div class="">
                           <%
                           	if (alSkills != null && alSkills.size() != 0) {
                           		for (int i = 0; i < alSkills.size(); i++) {
                           %>
                                <strong><%=(i < alSkills.size() - 1) ? (alSkills.get(i)).get(1) + ", " : (alSkills.get(i)).get(1)%></strong>
                            <%
                            	}
                            	}
                            %>
                       </div>
                      <div class="clr"></div> 
                        <div style="border:solid 0px #ccc; float:left; width:30%">
                                <div id="skillPrimary"></div>
                                          
                         </div>
                </div>   
                    
                
		</div> --%>
          
           <div class="right_shrtlnk">
           		<form method="post" action="RosterReport.action">                	
                	<input type="hidden" name="profileEmpId" value="<%=strProID%>"/>
                	<input type="image" class="roster_shrtlnk"  border="0" title="Roster details"/>
                </form>
                
                <form method="post" action="LeaveCard1.action">                	
                	<input type="hidden" name="profileEmpId" value="<%=strProID%>"/>
                	<input type="hidden" name="empId" value="<%=strProID%>"/>
                	<input type="image" class="leaves_shrtlnk" border="0" title="Leave Card"/>
                </form>
                
                <form method="post" action="ClockEntries.action">
                	<%-- <input type="hidden" name="T" value="T"/>                	
                	<input type="hidden" name="profileEmpId" value="<%=strProID%>"/> --%>
                	<input type="image" class="tmsheet_shrtlnk"  border="0" title="Clock Entry"/>
                </form>
                
                <form method="post" action="ViewPaySlips.action">                	
                	<input type="hidden" name="profileEmpId" value="<%=strProID%>"/>
                	<input type="image" class="payslip_shrtlnk"  border="0" title="Compensation details"/>
                </form>
               
              </div>   
                   
	    </div>
        
        <div class="clr"></div>
       
			<%
       				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
       			%>
			 <div style="float:left; min-width:200px; margin:10px 0px 0px 0px" class="trow">
			  <div class="fieldset" style="margin-top:0px">
				  <fieldset>
				   <legend>Attributes</legend>
				<%
						for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
							List<String> innerList = elementouterList.get(i);
				%>
						<div style="float:left;width:100%"><strong><%=innerList.get(1)%></strong></div>
						<%
							List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
									for (int j = 0; attributeouterList1 != null && j < attributeouterList1.size(); j++) {
										List<String> attributeList1 = attributeouterList1.get(j);
						%>
							<div style="float:left;width:100%"><div style="float:left"><%=attributeList1.get(1)%>&nbsp;: </div>
							<div id="starPrimary<%=i%><%=j%>" style="float:left; margin-left: 5px;"></div> 
				   			<input type="hidden" id="gradewithrating<%=i%><%=j%>" value="<%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) / 20 + "" : "0"%>"; name="gradewithrating<%=i%><%=j%>" />
								<script type="text/javascript">
							        $(function() {
							        	$('#starPrimary<%=i%><%=j%>').raty({
							        		readOnly: true,
							        		start: <%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) / 20 + "" : "0"%>,
							        		half: true,
							        		targetType: 'number',
							        		click: function(score, evt) {
							        			$('#gradewithrating<%=i%><%=j%>').val(score);
							        			}
							        	});
							        	});
					        	</script>
							
							</div>
					      <%
					      	}
					      		}
					      %>
						<%
							if (hmElementAttribute == null || hmElementAttribute.isEmpty()) {
						%>
					<div class="nodata msg" style="width: 93%">
					<span>No attribute aligned with this level</span>
					</div>
					<%
						}
					%>
	              </fieldset>
			  </div>	
			</div> 
	         <%
 	         	}
 	         %>
        
        
	<div class="clr"></div>
	  <div class="trow">
          <div>
		<%
			List alKRA = (List) request.getAttribute("alKRA");
			Map hmKRA = (Map) request.getAttribute("hmKRA");
		%>
          <div class="fieldset">
          <fieldset>
                  <legend>Key Responsibility Areas</legend>
             <div class="clr"></div>
              <div>			
              <%-- <%
	              	if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
	              			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) {
	              %>
                <div class="edit_profile">
	                      <a class="edit" href="javascript:void(0)" onclick="getKRA(<%=strProID%>);">Edit</a>
	              </div>  
	              <div class="clr"></div>
          		<%
          			}
          		%> --%>
	             <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
 				  			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
 				  %>
			  	<div class="edit_profile">
			        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=8" rel="popup_name" class="edit poplight">Edit</a>
        		</div>
        		<% } %>
		        		
                   <div>
                  <%
                  String effectiveDate = "";
                  if(alKRADetails != null && !alKRADetails.isEmpty()) {
                	  effectiveDate = alKRADetails.get(alKRADetails.size()-1).get(0);
                  }
                  %>
                  <div>
                  	 <%=((effectiveDate != null && !effectiveDate.equals("")) ? "Since: " + effectiveDate : "<div class=\"nodata msg\" style=\"width:95%\">No KRAs defined yet.</span></div>")%>
                  	<table>  
                	  <%
                	  for(int i=0; alKRADetails != null && !alKRADetails.isEmpty() && i<alKRADetails.size(); i++) {
						List<String> innerList = alKRADetails.get(i);                		  
                  	  %>
                      	<tr><td class="kra"><%=innerList.get(1)%></td></tr>
	                   <% } %>
                	</table>
                  </div>
                  </div>
               </div>
          </fieldset>
          </div>
          </div>
       </div>
    
    
       
		<div class="clr"></div>
			<div style="float:left; min-width:200px; margin:10px 0px 0px 0px" class="trow">
			  <div class="fieldset" style="margin-top:0px">
				  <fieldset>
				   <legend>Current Job</legend>
					  <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
   					  			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
   					  %>
					  	<div class="edit_profile">
					        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=8" rel="popup_name" class="edit poplight">Edit</a>
		        		</div>
		        		<% } %>
					      <div style="float:left">  
						    <table>
							    <tr><td class="alignRight">Employee Type:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_TYPE"), "-")%></td></tr>
							    <tr><td class="alignRight">Level:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("LEVEL_NAME"), "-")%></td></tr>
							    <tr><td class="alignRight">Designation:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-")%></td></tr>
							    <tr><td class="alignRight">Grade:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("GRADE_NAME"), "-")%></td></tr>
								
								<tr><td class="alignRight">SBU:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("SBU_NAME"), "-")%></td></tr>
								<tr><td class="alignRight">Department: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("DEPARTMENT_NAME"), "-")%></td></tr>
								<tr><td class="alignRight">Location: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("WLOCATION_NAME"), "-")%></td></tr>
								<tr><td class="alignRight">Organization: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ORG_NAME"), "-")%></td></tr>
							</table>
					   
						</div>
						<%if(!uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS"))) {%>
							<div style="float:left;margin-top:60px;margin-left:60px "><img src="images1/warning.png" /> </div>
						<% } %>
					</fieldset>
			  </div>	
			</div>
	
	
	
	<div class="clr"></div>
					
		<div style="float:left; min-width:200px; margin:10px 0px 0px 0px" class="trow">
		  <div class="fieldset" style="margin-top:0px">
			  <fieldset>
			   <legend>Reporting Structure</legend>
				  <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
  					  			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
  					  %>
				  	<div class="edit_profile">
				        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=8" rel="popup_name" class="edit poplight">Edit</a>
	        		</div>
	        		<% } %>
				      <div style="float:left">  
					    <table>
						    <tr><td class="alignRight">H.O.D.: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("HOD_NAME"), "-")%> </td></tr>
							<tr><td class="alignRight">HR: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("HR_NAME"), "-")%> </td></tr>
							<tr><td class="alignRight">Manager: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("SUPERVISOR_NAME"), "-")%> </td></tr>
						</table>
					</div>
				</fieldset>
		  </div>	
		</div>
	
	
		
	 <div class="clr"></div>
        <div class="trow">
          <div class="fieldset">
          <fieldset>
                  <legend>Teacher History</legend>
          	<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
              			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
              %>
               <div class="edit_profile">
                      <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=8" rel="popup_name" class="edit poplight">Edit</a>
               </div>  
          	<% } %>
             <div>
                  <table>
                  <tr><td class="alignRight">Joining Date:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "-")%></td></tr>
                  <tr><td class="alignRight">Last Promotion:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PREV_PROMOTION"), "-")%></td></tr>
                  <tr><td class="alignRight">Previous position:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PREV_DESIGNATION"), "-")%></td></tr>
                  </table>
             </div>
             </fieldset>
          </div>
      </div>
      
    
	
	<div class="clr"></div>
		<div style="float:left; min-width:200px; margin:10px 0px 0px 0px" class="trow">
		  <div class="fieldset" style="margin-top:0px">
			  <fieldset>
			   <legend>Other Official Information</legend>
				  <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
  					  			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
  					  %>
				  	<div class="edit_profile">
				        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=8" rel="popup_name" class="edit poplight">Edit</a>
	        		</div>
	        		<% } %>
				      <div style="float:left">  
					    <table>
						    <tr><td class="alignRight">Employment Type:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMPLOYMENT_TYPE"), "-")%></td></tr>
						    <tr><td class="alignRight">Roster Policy:</td><td class="textblue" valign="bottom"> <%=uF.showData("", "-")%></td></tr>
						    <tr><td class="alignRight">Probation Period:</td><td class="textblue" valign="bottom"> <%=uF.showData((String)request.getAttribute("PROBATION_PERIOD"), "-")%></td></tr>
						    <tr><td class="alignRight">Notice Period:</td><td class="textblue" valign="bottom"> <%=uF.showData((String)request.getAttribute("NOTICE_PERIOD"), "-")%> days</td></tr>
							<tr><td class="alignRight">Paycycle Duration: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PAYCYCLE_DURATION"), "-")%> </td></tr>
							<tr><td class="alignRight">Roster dependency?: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ROSTER_DEPENDENCY"), "-")%> </td></tr>
							<tr><td class="alignRight">Attendance dependency?: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ATTENDANCE_DEPENDENCY"), "-")%> </td></tr>
							
							<tr><td class="alignRight">Eligible for allowance:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("ALLOWANCE"), "-")%></td></tr>
							<tr><td class="alignRight">Biometric Machine Id (if integrated): </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("BIOMATRIC_MACHINE_ID"), "-")%></td></tr>
						</table>
					</div>
				</fieldset>
		  </div>	
		</div>
	
	
	
	<div class="clr"></div>
			<div style="float:left; min-width:200px; margin:10px 0px 0px 0px" class="trow">
			  <div class="fieldset" style="margin-top:0px">
				  <fieldset>
				   <legend>Leave Snapshot</legend>
					  <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
   					  			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
   					  %>
					  	<div class="edit_profile">
					        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=8" rel="popup_name" class="edit poplight">Edit</a>
		        		</div>
		        		<% } %>
					      <div style="float:left">  
						    <table>
								<tr><td class="alignRight"><b>Leave Balance </b></td><td class="textblue" valign="bottom"></td></tr>
								
								<% int cnt = 0;
								for(int i=0; leaveTypeListWithBalance != null && !leaveTypeListWithBalance.isEmpty() && i<leaveTypeListWithBalance.size(); i++) {
									List<String> innerList = leaveTypeListWithBalance.get(i);
								%>
									<tr>
										<td class="alignRight"><%=innerList.get(1) %>:</td>
										<td class="textblue" valign="bottom">
										<% if(hmEmpLeaveBalance != null && hmEmpLeaveBalance.get(innerList.get(0)) != null) { 
											cnt++;
										%>
											<%=hmEmpLeaveBalance.get(innerList.get(0)) %>
										<% } %>
										</td>
									</tr>
								<% } %>		
								
								<% if(cnt == 0) { %>
									<tr><td colspan="2"><div class="nodata msg"><span>No Leave Data Available.</span></div></td></tr>
								<% } %>
								
								<%-- <% Iterator<String> it = hmEmpLeaveBalance.keySet().iterator();
									while(it.hasNext()) {
										String key = it.next(); 
								%>
									<tr><td class="alignRight"><%=hmLeaveTypeName.get(key) %>: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpLeaveBalance.get(key), "-")%></td></tr>
								<% } 
									if(hmEmpLeaveBalance == null || hmEmpLeaveBalance.isEmpty()) {
								%>
									<tr><td colspan="2" align="center" class="nodata msg"><span>No Leave Data Available.</span></td></tr>
								<% } %> --%>
								
							</table>
						</div>
					</fieldset>
			  </div>	
			</div>
	
	
	
	<div class="clr"></div>
		<div class="trow">
			<div class="fieldset">
				<fieldset>
			   	<legend>Compensation Structure</legend>
			   	<div style="float: left;"> 
			   		<table>                  
	                  <tr><td class="alignRight">Payout Type:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PAYOUT_TYPE"), "-")%></td></tr>
	                  <%
	                  	if (hmEmpProfile.get("EMP_ACT_NO") != null && !hmEmpProfile.get("EMP_ACT_NO").equals("")) {
	                  %>
	                  	<tr><td class="alignRight">Bank Account No.:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_ACT_NO"), "-")%></td></tr>
	                  <%
	                  	}
	                  %>
	                </table>
                  </div>
			   	<div style="float:right"><a title="Preview" class="preview"  href="javascript:void(0)" onclick="salaryPreview(<%=strProID%>)">Preview</a></div>
			   	<div style="float:right"><a title="Historical Salary" class="sal_history" href="javascript:void(0)" onclick="salaryHistory(<%=strProID%>)">History</a></div>
            	<%
	            	List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
	            	Map<String, Double> hmSalaryTotal = (Map<String, Double>) request.getAttribute("hmSalaryTotal");
	            %>
                 
  				<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) { %>
  
		  			<%if(uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS"))) {%>
	                 				<div class="edit_profile">
				        				<a style="padding:5px;" href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&serviceId=0&step=9" rel="popup_name" class="edit poplight">Edit</a>
				        			</div>
	      			<%
	      				} else if (!uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS")) && !isFilledStatus) {
	      			%>
	            		<div class="edit_profile">
		      				<a href="javascript:void(0)" onclick="return hs.htmlExpand(this);" class="edit poplight">Edit</a>
		      				<div class="highslide-maincontent">
								<h3>Please fill up the current job section first.</h3>
							</div>
		      			</div>
	            	<% } %>
								  
		        <% } %>
                 
                 <div style="float:left; width:100%; margin-top :10px; margin-bottom: 10px;">		
                 	
	           		<table style="border: 0px; width: 100%;">
	                 <tr>
	                 <td valign="top">
						<table cellspacing="1" cellpadding="2" class="tb_style" style="float: left;">
							<tr>
								<td colspan="3" nowrap="nowrap" align="center"><h5>EARNING DETAILS</h5></td>
							</tr>
							
							<tr>
								<td class="alignRight">Salary Head</td>
								<td width="30%" class="alignRight">Monthly</td>
								<td width="30%" class="alignRight">Annual</td>
							</tr>
							
							<%
							double grossAmount = 0.0d;
							double grossYearAmount = 0.0d;
							for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
								List<String> innerList = salaryHeadDetailsList.get(i);
									if(innerList.get(1).equals("E")) {
										grossAmount +=uF.parseToDouble(innerList.get(2));
										grossYearAmount +=uF.parseToDouble(innerList.get(3));
							%>
									<tr>
										<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
										<td align="right" class="textblue" valign="bottom"><%=uF.showData(innerList.get(2), "0")%></td>
										<td align="right" class="textblue" valign="bottom"><%=uF.showData(innerList.get(3), "0")%></td>
									</tr>
								<% } %>
									
								<% } %>
								
								<tr>
									<td class="alignRight"><strong>Gross Salary</strong></td>
									<%-- <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(hmSalaryTotal.get("GROSS_AMOUNT"))%></strong></td>
									<td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(hmSalaryTotal.get("GROSS_YEAR_AMOUNT"))%></strong></td> --%>
									<td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(grossAmount)%></strong></td>
									<td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(grossYearAmount)%></strong></td>
									
								</tr>
						
						</table>
					</td>
						
					<td valign="top">
						<table cellspacing="1" cellpadding="2" class="tb_style" style="float: left; width:50%">
							
							<tr>
								<td colspan="3" nowrap="nowrap" align="center"><h5>DEDUCTION DETAILS</h5></td>
							</tr>
							
							<tr>
								<td class="alignRight">Salary Head</td>
								<td width="30%" class="alignRight">Monthly</td>
								<td width="30%" class="alignRight">Annual</td>
							</tr>
							
							<% 

								double deductAmount = 0.0d;
								double deductYearAmount = 0.0d;
							
								for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
								List<String> innerList = salaryHeadDetailsList.get(i);
									if(innerList.get(1).equals("D")) {
										deductAmount +=uF.parseToDouble(innerList.get(2));
										deductYearAmount +=uF.parseToDouble(innerList.get(3));
							%>
									<tr>
										<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
										<td align="right" class="textblue" valign="bottom"><%=uF.showData(innerList.get(2), "0")%></td>
								 		<td align="right" class="textblue" valign="bottom"><%=uF.showData(innerList.get(3), "0")%></td>
										
									</tr>
								<% } %>
									
								<% } %>
								
							<tr>
									<td class="alignRight"><strong>Deduction</strong></td>
									<%-- <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(hmSalaryTotal.get("DEDUCT_AMOUNT"))%></strong></td>
									<td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(hmSalaryTotal.get("DEDUCT_YEAR_AMOUNT"))%></strong></td> --%>
									<td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(deductAmount)%></strong></td>
									<td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(deductYearAmount)%></strong></td>
									
							</tr>
						
						</table>
					</td>
					</tr>
				</table>
					</div>
				
					<%
						if (!isFilledStatus) {
					%>
						<div style="float:left;margin-top:60px;margin-left:60px "><img src="images1/warning.png" /> </div>
					<% } %>
            
			</fieldset>
			
			</div>
			
		</div>
		
		
	<div class="clr"></div>
		<div class="trow">
			<div>
			  <div class="fieldset">
	  			<fieldset>
				   	<legend>Statutory Compliance Applied</legend>
			  
			  	<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) 
			  			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
				  	<div class="edit_profile">
				        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=8" rel="popup_name" class="edit poplight">Edit</a>
			        </div>
		  		<%
		  			}
		  		%>
				   <table>
				   <% 
				   StringBuilder TdsForm16Or16A = null;
				   if(uF.parseToBoolean(hmEmpProfile.get("IS_FORM16"))) {
					   if(TdsForm16Or16A == null) TdsForm16Or16A = new StringBuilder();
					   TdsForm16Or16A.append("Form 16");
				   }
				   if(uF.parseToBoolean(hmEmpProfile.get("IS_FORM16_A"))) {
					   if(TdsForm16Or16A == null) TdsForm16Or16A = new StringBuilder();
					   TdsForm16Or16A.append("Form 16 A");
				   }
				   if(TdsForm16Or16A == null) TdsForm16Or16A = new StringBuilder("-");
				   %>
				   		<tr><td class="alignRight">TDS: </td><td class="textblue" valign="bottom"><%=uF.showData(TdsForm16Or16A.toString(), "-")%></td></tr>
				   </table>
	   		 </fieldset>     
			  </div>
			</div>
		</div>


		

	<div class="clr"></div>
      <div class="trow">
          <div>
          <div class="fieldset">
          <fieldset>
            <legend>Corporate Information & Personal Information</legend>
             <div class="clr"></div>
              <div>			
              <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) { %>
                <div class="edit_profile">
	                      <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=8" rel="popup_name" class="edit poplight">Edit</a>
	              </div>  
          		<% } %>
          
                   <p class="past heading_dash" style="margin:0px 0px 10px 0px"> Corporate Information</p>
                   <div class="content1">
	                  <table>
	                  	<tr><td class="alignRight">Corporate Mobile:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("CORPORATE_MOBILE"), "-")%></td></tr>
	                 	<tr><td class="alignRight">Corporate Desk:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("CORPORATE_DESK"), "-")%></td></tr>
	                 	<tr><td class="alignRight">Corporate id:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMAIL_SEC"), "-")%></td></tr>
	                 	<tr><td class="alignRight">Skype id:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("SKYPE_ID"), "-")%></td></tr>
	                  </table>
                 	</div>
               </div>
               
               <div>   
	               <div class="edit_profile">
	                      <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=1" rel="popup_name" class="edit poplight">Edit</a>
	              </div>
                  <p class="past heading_dash" style="margin:0px 0px 10px 0px">Personal Information</p>
                  <div class="content1">
                  <table>
                  <tr><td class="alignRight">Current Address:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("CURRENT_ADDRESS"), "") + ", " + uF.showData(hmEmpProfile.get("CURRENT_CITY"), "") + ", " + uF.showData(hmEmpProfile.get("CURRENT_STATE"), "") + ", "
					+ uF.showData(hmEmpProfile.get("CURRENT_COUNTRY"), "")%></td></tr>
                  <tr><td class="alignRight">Permanent Address:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("ADDRESS"), "") + ", " + uF.showData(hmEmpProfile.get("CITY"), "") + ", " + uF.showData(hmEmpProfile.get("STATE"), "") + ", "
					+ uF.showData(hmEmpProfile.get("COUNTRY"), "")%></td></tr>
                  <tr><td class="alignRight">Landline: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CONTACT"), "-")%></td></tr>
                  <tr><td class="alignRight">Mobile: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CONTACT_MOB"), "-")%></td></tr>
                  <tr><td class="alignRight">Email id:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMAIL"), "-")%></td></tr>
                  <tr><td class="alignRight">Date of Birth:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DOB"), "-")%></td></tr>
                  <tr><td class="alignRight">Gender:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("GENDER"), "-")%></td></tr>
                  <tr><td class="alignRight">Blood Group:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_BLOOD_GROUP"), "-")%></td></tr>
                  <tr><td class="alignRight">Marital Status:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("MARITAL_STATUS"), "-")%></td></tr>
                  
                  <% if (hmEmpProfile.get("MARITAL_STATUS") != null && hmEmpProfile.get("MARITAL_STATUS").equals("Married")) { %>
                  	<tr><td class="alignRight">Date Of Marriage:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("MARRAIGE_DATE"), "-")%></td></tr>
                  <% } %>
                  </table>
                  </div>
              </div>
              
              
              <div>   
	               <div class="edit_profile">
	                      <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=1" rel="popup_name" class="edit poplight">Edit</a>
	              </div>
                  <p class="past heading_dash" style="margin:0px 0px 10px 0px">Other Personal Information</p>
                  <div class="content1">
                  <table>
                  <tr><td class="alignRight">Pan No:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PAN_NO"), "-")%></td></tr>
                  <tr><td class="alignRight">Passport No:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PASSPORT_NO"), "-")%></td></tr>
                  <tr><td class="alignRight">Passport expires on:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PASSPORT_EXPIRY"), "-")%></td></tr>
                  </table>
                  </div>
              </div>
              
              
              <div>			
                <div class="edit_profile">
	                      <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=1" rel="popup_name" class="edit poplight">Edit</a>
	              </div>  
                   <p class="past heading_dash" style="margin:0px 0px 10px 0px"> Emergency Information</p>
                   <div class="content1">
                  <table>
                  <tr><td class="alignRight">Contact Name:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMERGENCY_NAME"), "-")%></td></tr>
                  <tr><td class="alignRight">Contact Number:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMERGENCY_NO"), "-")%></td></tr>
                  <tr><td class="alignRight">Contact Relation:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMERGENCY_RELATION"), "-")%></td></tr>
                  <tr><td class="alignRight">Doctor's Name:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DOCTOR_NAME"), "-")%></td></tr>
                  <tr><td class="alignRight">Doctor's Contact Number:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DOCTOR_NO"), "-")%></td></tr>
                  </table>
                  </div>
               </div>
               
               
               <div>   
	               <div class="edit_profile">
	                      <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=1" rel="popup_name" class="edit poplight">Edit</a>
	              </div>
                  <p class="past heading_dash" style="margin:0px 0px 10px 0px">Personal Statutory Information</p>
                  <div class="content1">
                  <table>
                  <tr><td class="alignRight">Provident Fund No:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PF_NO"), "-")%></td></tr>
                  <tr><td class="alignRight">GPF Acc No:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("GPF_ACC_NO"), "-")%></td></tr>
                  <tr><td class="alignRight">ESIC No:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("ESIC_NO"), "-")%></td></tr>
                  <tr><td class="alignRight">UAN No:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("UAN_NO"), "-")%></td></tr>
                  <tr><td class="alignRight">UID No:</td><td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("UID_NO"), "-")%></td></tr>
                  </table>
                  </div>
              </div>
              
          </fieldset>
          </div>
          </div>
       </div>
       
		<%-- <div class="clr"></div>
		
		<div class="trow">
			<div>
			  <div class="fieldset">
	  			<fieldset>
				   	<legend>Other Information</legend>
			  
			  	<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) { %>
				  	<div class="edit_profile">
				        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=8" rel="popup_name" class="edit poplight">Edit</a>
			        </div>
		  		<%
		  			}
		  		%>
				   <table>
				   		<tr><td class="alignRight">Can work for: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("SBU_NAME"), "-")%></td></tr>
				     	<tr><td class="alignRight">Employment type: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_TYPE"), "-")%></td></tr>
				     	<tr><td class="alignRight">Roster dependency: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ROSTER_DEPENDENCY"), "-")%></td></tr>
				     	<tr><td class="alignRight">Eligible for allowance: </td><td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ALLOWANCE"), "-")%></td></tr>
				     
				   </table>
	   		 </fieldset>     
			  </div>
			</div>
		</div>
		 --%>
		
		
		
		 <div class="clr"></div>
           <div class="trow">
   			<div>
               <div class="fieldset">
               <fieldset>
                  <legend>Skills</legend>
				<%-- <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) { %> --%>
                   <div class="edit_profile">
                           <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=2" rel="popup_name" class="edit poplight">Edit</a>
                   </div>
                   <div class="clr"></div>
              	<%-- <% } %> --%>    
                   <table style="width:98%">	
                  <%
                  	if (alSkills != null && alSkills.size() != 0) {
                  		for (int i = 0; i < alSkills.size(); i++) {
                  			List<String> alInner = alSkills.get(i);
                  %>
                        <tr>
                            <td><strong><%=alInner.get(1)%>:</strong></td>
                            <td><div id="star<%=i%>"></div></td>
                        </tr>
                    <% } } else { %>
						<tr><td class="nodata msg"><span>No skill sets added</span></td></tr>
                    <% }  %>
                   </table>
               </fieldset>
               </div>
             </div>
           </div>
           
         
           
	<div class="clr"></div>
		<div class="trow">
			<div>
			  <div class="fieldset">
			  <fieldset>
				   	<legend>Education</legend>
				<%
					//if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)){
				%>
				  	<div class="edit_profile">
				        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=2" rel="popup_name" class="edit poplight">Edit</a>
			        </div>
			         <div class="clr"></div>
			    <%
			    	//}
			    %>
				   <%
  				   	if (alEducation != null && alEducation.size() != 0) {
  				   		for (int i = 0; i < alEducation.size(); i++) {
  				   			List<String> innerList = alEducation.get(i);
  				   %>
			     	<p class="past heading_dash" style="margin:0px 0px 10px 0px" ><%=innerList.get(1)%></p>
			     	<div class="content1" style="margin:0px 0px 10px 0px">
				     	<table>
						     <tr>
						     	<td class="alignRight">Duration: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(2) + " Years"%></td>
						     </tr>
						     <tr>
						     	<td class="alignRight">Completion Year: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(3)%></td>
						     </tr>
						     <tr>
						     	<td class="alignRight">Grade: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(4)%></td>
						     </tr>
					   	</table>
					 </div>
					 <% } %>
					<% } else { %>
					<table style="width:98%">
						<tr><td class="nodata msg"><span>No Education information added</span></td></tr>
					</table>
					<% } %>
			   		</fieldset>      
			  </div>
			</div>
		</div>
		
		
		
	<div class="clr"></div>
	    <div class="trow">
	       <div>
	        <div class="fieldset">
	        <fieldset>
	           <legend>Languages</legend>
	                <%-- <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) { %> --%>
	            <div class="edit_profile">
	                 <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=2" rel="popup_name" class="edit poplight">Edit</a>
	            </div>
	            <div class="clr"></div>
	            <%-- <% } %> --%>
	            <table style="width:98%">			
	                <% if (alLanguages != null && alLanguages.size() != 0) { %>
	                <tr class="center">
	                	<td width="150px"><strong>Language</strong></td>
	                	<td width="150px"><strong>Read</strong></td>
	                	<td width="150px"><strong>Write</strong></td>
	                	<td width="150px"><strong>Speak</strong></td>
	                </tr>
	               	<%
	               		for (int i = 0; i < alLanguages.size(); i++) {
	      					List<String> alInner = alLanguages.get(i);
	               	%>
	                 <tr>
	                     <td class="textblue" valign="bottom"><strong><%=alInner.get(1)%></strong></td>
	                     <% if ((alInner.get(2)).equals("1")) { %>
	                     	<td class="textblue yes"></td>
	                     <% } else { %>
	                     	<td class="textblue no"></td>
	                     <% } %>
	                     
	                     <% if ((alInner.get(3)).equals("1")) { %>
	                     	<td class="textblue yes"></td>
	                     <% } else { %>
	                     	<td class="textblue no"></td>
	                     <% } %>
	                     
	                     <% if ((alInner.get(4)).equals("1")) { %>
	                     	<td class="textblue yes"></td>
	                     <% } else { %>
	                     	<td class="textblue no"></td>
	                     <% } %>
	                 </tr>
	                <% } } else { %>
					<tr><td class="nodata msg"><span>No languages added</span></td></tr>
	                <% } %>
	            </table>
	        </fieldset>
	        </div>
	        </div>
	    </div>
        
        
     <div class="clr"></div>      	
		<div class="trow">
             <div>
            	<div class="fieldset">
            		<fieldset>
                     <legend>Hobbies</legend>
                     <%-- <%if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) { %> --%>
                 <div class="edit_profile">
                         <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=2" rel="popup_name" class="edit poplight">Edit</a>
                 </div>
                 <div class="clr"></div>
                 <%-- <% } %> --%>
                 <table style="width:98%">	
                     <% if (alHobbies != null && alHobbies.size() != 0) { %>
                     <tr>
                  <% for (int i = 0; i < alHobbies.size(); i++) {
                   		List<String> alInner = alHobbies.get(i);
                    %>
                      <td class="textblue" valign="bottom"><strong><%=i < alHobbies.size() - 1 ? alInner.get(1) + " ," : alInner.get(1)%></strong></td>
                  <% } %>
                    </tr>  
                     <% } else { %>
                     <tr><td class="nodata msg"><span>No hobbies added</span></td></tr>
                     <% } %>
                 </table>
             </fieldset>
            </div>
            </div>
         </div>
               
      
      
        <div class="clr"></div>
          <div style="float:left; min-width:200px; margin:10px 0px 0px 0px" class="trow">
		  <div class="fieldset" style="margin-top:0px">
			  <fieldset>
			   <legend>Previous Employment</legend>
				  <%
					  	if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
					  		&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
				  %>
				  	<div class="edit_profile">
				        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=3" rel="popup_name" class="edit poplight">Edit</a>
	        		</div><br/><br/>
	        		<% } %>
	        		
				 <% for(int i=0; alPrevEmployment != null && !alPrevEmployment.isEmpty() && i<alPrevEmployment.size(); i++) { 
				 	List<String> innerList = alPrevEmployment.get(i); 
				 %>
				      <p class="past heading_dash" style="margin:0px 0px 10px 0px"> <%=uF.showData(innerList.get(1), "-") %></p>
	                   <div class="content1">
		                  <table>
		                  	<tr><td class="alignRight">Location:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(2), "-")%></td></tr>
		                 	<tr><td class="alignRight">City:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(3), "-")%></td></tr>
		                 	<tr><td class="alignRight">State:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(4), "-")%></td></tr>
		                 	<tr><td class="alignRight">Country:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(5), "-")%></td></tr>
		                 	<tr><td class="alignRight">Phone Number:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(6), "-")%></td></tr>
		                 	<tr><td class="alignRight">Reporting To:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(7), "-")%></td></tr>
		                 	<tr><td class="alignRight">Reporting Manager Ph. No.:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(13), "-")%></td></tr>
		                 	<tr><td class="alignRight">HR Manager:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(14), "-")%></td></tr>
		                 	<tr><td class="alignRight">HR Manager Ph. No.:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(15), "-")%></td></tr>
		                 	<tr><td class="alignRight">From:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(8), "-")%></td></tr>
		                 	<tr><td class="alignRight">To:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(9), "-")%></td></tr>
		                 	<tr><td class="alignRight">Designation:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(10), "-")%></td></tr>
		                 	<tr><td class="alignRight">Responsibility:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(11), "-")%></td></tr>
		                 	<tr><td class="alignRight">Skills:</td><td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(12), "-")%></td></tr>
		                 	<tr><td class="alignRight">TDS information:</td><td class="textblue" valign="bottom">&nbsp;</td></tr>
		                 	<tr><td class="alignRight">&nbsp;</td><td class="textblue" valign="bottom">Financial Year: <%=uF.showData(hmEmpPrevEarnDeduct.get("PREV_COMP_FINANCIAL_YEAR"), "-") %></td></tr>
		                 	<tr><td class="alignRight">&nbsp;</td><td class="textblue" valign="bottom">Gross Amount: <%=uF.showData(hmEmpPrevEarnDeduct.get("PREV_COMP_GROSS_AMOUNT"), "-") %></td></tr>
		                 	<tr><td class="alignRight">&nbsp;</td><td class="textblue" valign="bottom">Tds Amount: <%=uF.showData(hmEmpPrevEarnDeduct.get("PREV_COMP_TDS_AMOUNT"), "-") %></td></tr>
		                 	<tr><td class="alignRight">&nbsp;</td><td class="textblue" valign="bottom">Please upload relevant document (form 16): 
		                 	<% if(hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME") != null && !hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME").equals("")) { %>
		                 	<a href="<%=request.getContextPath()+"/userDocuments/"+hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME")%>" >Download</a>
		                 	<% } else { %>
		                 	-
		                 	<% } %>
		                 	</td></tr>
		                  </table>
	                 	</div>
				     <% } %>
				     
			     <% if (alPrevEmployment == null || alPrevEmployment.isEmpty()) { %>
					<div class="nodata msg" style="width: 96%"> <span>No previous employment</span> </div>
				<% } %>
				</fieldset>
		  </div>	
		</div>
	
	          
        <%
			List<Map<String, String>> empRefList = (List<Map<String, String>>) request.getAttribute("empRefList");
			if (empRefList != null && empRefList.size()>0) {
		%>
		<div class="trow">
			<div>
				<div class="fieldset">
					<fieldset>
						<legend>Teacher References</legend>
						<%
							if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
								&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
						%>
						<div class="edit_profile">
							<a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=4" rel="popup_name" class="edit poplight">Edit</a>
						</div>
						<div class="clr"></div>
						<% } %>
						<div>
							<%
								for (int i = 0; empRefList != null && i < empRefList.size(); i++) {
										Map<String, String> hmInner = (Map<String, String>) empRefList.get(i);
										if (hmInner == null) hmInner = new HashMap<String, String>();
							%>
							<ul>
								<li><strong>Reference <%=(i + 1)%></strong></li>
								<li>
									<table>
										<tr>
											<td class="alignRight">Reference Name:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_NAME"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">School:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_COMPANY"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Designation:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_DESIGNATION"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Contact No.:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_CONTACT_NO"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Email:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_EMAIL"), "-")%></td>
										</tr>
									</table>
								</li>
							</ul>
							<% } %>
						</div>
					</fieldset>
				</div>
			</div>
		</div>
		<% } %>
		
		
<!-- ===start parvez date: 06-09-2022=== -->
<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_EMPLOYEE_FAMILY_INFORMATION))){ %>		        
	<div class="clr"></div>
		<div class="trow">
			<div>
			  <div class="fieldset">
			  <fieldset>
				   	<legend>Family Information</legend>
				<%
					//if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)){
				%>
			  	<div class="edit_profile">
			        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=5" rel="popup_name" class="edit poplight">Edit</a>
		        </div>
		        <div class="clr"></div>
		        <%
		        	//}
		        %>
			      
			   <div style="float:left; width:100%">
			   
			   <%
  			   	if (alFamilyMembers != null && alFamilyMembers.size() != 0) {

  			   		for (int i = 0; i < alFamilyMembers.size(); i++) {
  			   			List<String> innerList = alFamilyMembers.get(i);

  			   			if (innerList.get(1).length() != 0) {
  			   				if (innerList.get(8).equals("FATHER")) {
  			   %>
				   			<p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Father's Info</Strong></p>
					   	<% } else if (innerList.get(8).equals("MOTHER")) { %>
					   	
					   		<p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Mother's Info</Strong></p>
					   	<% } else if (innerList.get(8).equals("SPOUSE")) { %>
					   	
					   		<p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Spouse's Info</Strong></p>
					   	<% } else if (innerList.get(8).equals("SIBLING")) { %>
					   	
					   		<p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Sibling's Info</Strong></p>
					   	<% } else if (innerList.get(8).equals("CHILD")) { %>
					   	
					   		<p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Children's Info</Strong></p>
					   	<% } %>
					   	
                        <div class="content1" style="margin:0px 0px 10px 0px">
	                        <table>
						     <tr>
						     	<td class="alignRight">Name: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(1)%></td>
						     </tr>
						     <tr>
						     	<td class="alignRight">Date Of Birth: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(2)%></td>
						     </tr>
						     <tr>
						     	<td class="alignRight">Education: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(3)%></td>
						     </tr>
						     <tr>
						     	<td class="alignRight">Occupation: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(4)%></td>
						     </tr>
						     <tr>
						     	<td class="alignRight">Contact No: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(5)%></td>
						     </tr>
						     <tr>
						     	<td class="alignRight">Email Id: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(6)%></td>
						     </tr>
						     <% if (innerList.get(8).equals("SPOUSE") || innerList.get(8).equals("CHILD") || innerList.get(8).equals("SIBLING")) { %>
						     <tr>
						     	<td class="alignRight">Gender: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(7)%></td>
						     </tr>
	                         <% } %>
	                         <% if (innerList.get(8).equals("CHILD") || innerList.get(8).equals("SIBLING")) { %>
						     <tr>
						     	<td class="alignRight">Marital Status: </td>
						     	<td class="textblue" valign="bottom"><%=innerList.get(9)%></td>
						     </tr>
	                         <% } %>
	                         </table>
						</div>
				    	<% } }
				    		} else {
				    	%>
				    <table style="width:98%">
						<tr><td class="nodata msg"><span>No Family members added</span></td></tr>
					</table>
				    <% } %>
			   </div>
			   		</fieldset>      
			  </div>
			</div>
		</div>
	<% } %>	
			
            
	<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_MEDICAL_DETAILS))){ %>
	<div class="clr"></div>
            <div class="trow">
                <div>
                <%
                	List<List<String>> alMedicalDetails = (List<List<String>>) request.getAttribute("alMedicalDetails");
                	Map<String, String> medicalQuest = (Map<String, String>) request.getAttribute("medicalQuest");
                %>
                <div class="fieldset">
               	 <fieldset>
                        <legend>Medical Details</legend>
                        <%
                        	if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
                        			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
                        %>
                    <div class="edit_profile">
                            <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=6" rel="popup_name" class="edit poplight">Edit</a>
                    </div>
                    <div class="clr"></div>
                    <% } %>
                    <table style="width:98%">	
                        <% if (alMedicalDetails != null && alMedicalDetails.size() != 0) { %>
                         <% for (int i = 0; i < alMedicalDetails.size(); i++) {
                        		List<String> alInner = alMedicalDetails.get(i);
                        		if(medicalQuest.get(alInner.get(0)) != null && !medicalQuest.get(alInner.get(0)).equals("")) {	
                         %>
                         <tr>
                         <td style="width:70%"><div style="float:left;width:10px;font-weight:bold;padding-right:10px;"><%=i + 1%>.&nbsp;&nbsp;&nbsp;</div><div style="float:left;width:90%;"><%=medicalQuest.get(alInner.get(0)) %></div></td>
                         <% if (uF.parseToBoolean(alInner.get(1))) { %>
                         	<td class="textblue yes" style="width:10%"></td>
                         <% } else { %>
                         	<td class="textblue no" style="width:10%"></td>
                         <% } %>
                         
                         <% if (alInner.get(3) != null) { %>
                         	 <td style="width:20%" class="alignRight"><a href="<%=request.getContextPath()+"/userDocuments/"+alInner.get(3)%>" >Download</a> 
                         	 <%-- <a href="<%=docRetriveLocation + alInner.get(3)%>">Download</a>  --%></td>
                         <% } %>
                         </tr>
                         <% if (uF.parseToBoolean(alInner.get(1))) { %>
                         	<tr><td class="textblue" valign="bottom"><strong><%=alInner.get(2)%></strong></td></tr>
                         <% } %>
                         
                         <% } } %>
                         
                        <% } else { %>
                        <tr><td class="nodata msg"><span>No Medical Details added</span></td></tr>
                        <% } %>
                    </table>
               	 </fieldset>
                </div>
                </div>
            </div>
        <% } %>    
      <!-- ===end parvez date: 06-09-2022=== -->      
              
              
              
   <div class="clr"></div>
           <div class="trow">
			<div>
			  <div class="fieldset">
			  <fieldset>
				   	<legend>Supporting Documents</legend>
				   	
				<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) 
						&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
				  	<div class="edit_profile">
				        <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=7" rel="popup_name" class="edit poplight">Edit</a>
			        </div>
			        <div class="clr"></div>
			    <% } %>
				   <table style="width:98%;">
				   <%
	   			   	if (alDocuments != null && alDocuments.size() != 0) {
	   			   		for (int i = 0; i < alDocuments.size(); i++) {
	   			   %>
				     <tr><td class="alignRight"> <%=((ArrayList) alDocuments.get(i)).get(1)%> </td><td class="textblue" valign="bottom">-</td>
				     <td class="alignRight"> <a href="<%=request.getContextPath()+"/userDocuments/"+((ArrayList)alDocuments.get(i)).get(4)%>" >Download</a>
				     <%-- <a href="<%=docRetriveLocation + ((ArrayList) alDocuments.get(i)).get(4)%>"> --%>
				     </td><td class="textblue" valign="bottom">-</td></tr>
				    <% } } else { %>
				    <tr><td colspan="4" class="nodata msg"><span>No Documents attached</span></td></tr>
				    <% } %>
				   </table>
		   		</fieldset>      
			  </div>
			</div>
		</div>

		
	</div>
    
    
    
     <div class="rightholder" style="width:57%; border: solid 0px #ccc">
                     
              <div class="tableblock myprof_kpi">        
              
                      <div class="trow" style="background:#fff">
                            
                         <div style="float:left;width:44%; border:#CCCCCC solid 1px; min-height:120px">
                           <img src='<%=response.encodeURL(request.getContextPath() + "/jsp/chart/getChart.jsp?" + semiWorkedAbsent1URL)%>' border="0">
                         </div>
                          <div class="holder exp">
                          <% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))) { %>
                          <div style="float: right;"><a href="javascript:void(0);" onclick="sendMail(<%=strProID%>);" title="Send Mail"><img src="images1/mail_enbl.png"></a></div>
                          <% } %>
                            <%-- <div class="time_spent_duration"><%= uF.showData(request.getAttribute("TIME_DURATION"), "") %></div>
                            <div class="time_spent_duration"><span><%=request.getAttribute("HRS_WORKED") %></span> hrs</div>
                            <div class="time_since">Since <span><%=uF.showData( hm.get("JOINING_DATE"), "NA")%></span></div>
                             --%>
                            <div class="time_spent_duration" style="text-align:left;line-height:30px">
                            <%
                            	if (request.getAttribute("TIME_DURATION") != null) {
                            %>
							Since <span><%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "NA")%>, </span>
							you have worked <%=request.getAttribute("TIME_DURATION")%>
							for <span><%=request.getAttribute("HRS_WORKED")%></span> hrs
							<%
                            	} else {
                            %>
							Your working hours have not been calcualated, yet.
							<%
                            	}
                            %>
							
							<div style="float:right">
							<%
								if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || !strUserType.equalsIgnoreCase(IConstants.ARTICLE) 
										|| !strUserType.equalsIgnoreCase(IConstants.CONSULTANT))) {
									String empStatus = (String)request.getAttribute("RESIGNATION_STATUS");
									
									if(empStatus!=null && empStatus.equalsIgnoreCase("TERMINATED") ){
							%>
								<span style="float: right; font-family:times new roman; font-size:17px;color:red;">
								<a style="float:right;" href="ResignationEntry1.action?emp_id=<%=strProID%>"><%=empStatus %></a></span>
								
							<%}else{ %>	
								<a style="float:right;" href="ResignationEntry1.action?emp_id=<%=strProID%>"><%=((request.getAttribute("RESIGNATION_STATUS") != null) ? request.getAttribute("RESIGNATION_STATUS") : "Leave Organisation")%></a>
							<%
							  }
							} %>

							<% if (uF.parseToInt((String) request.getAttribute("RESIG_STATUS")) == 1) { %>
								<div style="float:right;color: red;text-align: right;width:100%"><%=uF.showData((String) request.getAttribute("RESIGNATION_REMAINING"), "0")%> </div>	
							<% } %>
							
							<% if (uF.parseToInt((String) request.getAttribute("PROBATION_REMAINING")) > 0 && uF.parseToInt((String) request.getAttribute("RESIG_STATUS")) == 0) { %>
								<div style="float:right;text-align: right;width:100%">Your probation period will end in <%=uF.showData((String) request.getAttribute("PROBATION_REMAINING"), "0")%> days</div>	
							<% } %>
								
							</div>
							</div>
                          </div>
                      </div>
                    <div class="clr"></div>                                 
              </div>
              
              
              
                <div class="tmln_holder">
                 
                    <div style="float:left; border:solid 0px #ccc; width:93%; margin:0px 0px 0px 10px"> 
             			<%
              				if (alActivityDetails.size() != 0) {
              			%>
             			<div class="lholder">
             			
	             			<%
	             				for (int i = 0; i < alActivityDetails.size(); i++) {
             						List<String> innerList = alActivityDetails.get(i);
             						int nDoc = uF.parseToInt(innerList.get(11));
             						if (i % 2 == 0) {
	             			%>
                             
	                              <div class="lblock">
	                              
	                                     <div class="lp"></div>
	                                     <div class="tm_container">
	                                     <% if (i == 0) { %>
	                                     <%
											if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) 
													|| strUserType.equalsIgnoreCase(IConstants.CEO))) {
										%>
	                                     <div style="float:right">
											<form action="EmployeeActivity.action" method="post">
												<input type="hidden" name="strEmpId" value="<%=strProID%>"/>
												<input type="image" src="images1/actvty.png">
											</form>
										</div>
											<% } %>
											<% } %>		
													
	                                 <p class="act_title" style="background:<%=(nDoc == 0) ? "" : "orange"%>"><strong><%=innerList.get(5)%> <%=(nDoc == 0) ? "" : "<a href=\"DownloadDocument.action?doc_id=" + nDoc+ "\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" style=\"margin-left:5px;height:16px;float:right;\" ></i></a>"%></strong></p>
		                                 <p><%=innerList.get(5)%> on <%=innerList.get(7)%> 
		                                 	<%=(innerList.get(6) != null && innerList.get(6).length() != 0) ? "for " + innerList.get(6) : ""%>
		                                 </p>
		                              </div> 
                              </div>
                               
                               	  <div class="clr"></div>
       					<% } } %>
             		 </div>     

	             		 <div class="rholder">
	             		<%
	             			for (int i = 0; i < alActivityDetails.size(); i++) {
             					List<String> innerList = alActivityDetails.get(i);
             					int nDoc = uF.parseToInt(innerList.get(11));
             					if (i % 2 == 1) {
	             		%>
		                             
                                <div class="rblock">
	                                <div class="lp_r"></div>
                                 	   <div class="tm_container_r">
                                  		 <p class="act_title" style="background:<%=(nDoc == 0) ? "" : "orange"%>"><strong><%=innerList.get(5)%> <%=(nDoc == 0) ? "" : "<a href=\"DownloadDocument.action?doc_id=" + nDoc+ "\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" style=\"margin-left:5px;height:16px;float:right;\"  ></i></a>"%></strong></p>
		                                     <p><%=innerList.get(5)%> on <%=innerList.get(7)%>
	                                     	 <%=(innerList.get(6) != null && innerList.get(6).length() != 0) ? "for " + innerList.get(6) : ""%>
			                                </p> 
                                  		</div> 
                                </div>  
		                                
                            <div class="clr"></div>
				               <% } } %>
	             		</div>
	             		<% } else { %>

	             		<div class="lholder">
		             		<div class="lblock">
	                               <div class="lp"></div>
	                               <div class="tm_container">
	                                   <p class="act_title"><strong>Pending For Approval</strong></p>
										<p>Please fill up the remaining information to get it approved.</p>
	                               </div> 
		                     </div>
                        </div> 
                        <div class="clr"></div>
	             		<% } %>  
	             		</div> 
		            </div> 	     
             
              		<div class="curnt_job">	<!-- Current_job -->
	            		
							    <div>
		                               <div class="lblock1">
		                                    
		                                     <div class="tm_container1">
                                                <p class="past"><strong>Current employment details </strong></p> 
                                                <%-- <p class="textblue" valign="bottom">Employee Type: <%=uF.showData( hm.get("EMP_TYPE"), "-")%></p>
                                                <p class="textblue" valign="bottom">Grade: <%=uF.showData( hm.get("GRADE"), "-")%></p>
                                                <p class="textblue" valign="bottom">Manager: <%=uF.showData( hm.get("SUPER_CODE"), "-")%></p> --%>
                                   				<p class="textblue" valign="bottom">Joining Date: <%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "-")%></p>
                                   				<p class="textblue" valign="bottom">Designation: <%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-")%></p>
										
                                  		</div> 
                                </div>  
                                
                                <div class="clr"></div>
				              
	             		</div>
	             		
              		</div> 
              
	              <div class="tmln_holder">	<!-- Previous Employment -->
	             		
                         <div style="float:left; border:solid 0px #ccc; width:93%; margin:0px 0px 0px 10px"> 
                         <% if (alPrevEmployment.size() != 0) { %>   
	             			<div class="lholder prev_empl">
		             			<%
		             				for (int i = 0; i < alPrevEmployment.size(); i++) {
	             						List<String> innerList = alPrevEmployment.get(i);
	             						if (i % 2 == 0) {
		             			%>
		                              <div class="lblock">
		                                     <div class="lp"></div>
		                                     <div class="tm_container">
		                                         <p class="act_title"><strong><%=innerList.get(1)%></strong></p>
			                                         <p>From <%=innerList.get(8)%></p>
                                                     <p> To <%=innerList.get(9)%></p>
			                                         <p>	 <%=innerList.get(6) != null && (innerList.get(6)).length() != 0 ? "with " + innerList.get(6) + " designation" : ""%> 
			                                         </p>
			                                         <p><%=innerList.get(12) != null && (innerList.get(12)).length() != 0 ? "Skills: " + innerList.get(12) : ""%></p>
		                                     </div> 
		                              </div>
	                               	  <div class="clr"></div>
	       					<% } } %>
		             		
		             		 </div>     
		             		 
		             		 <div class="rholder">
		             		<%
		             			for (int i = 0; i < alPrevEmployment.size(); i++) {
		             					List<String> innerList = alPrevEmployment.get(i);
		             					if (i % 2 == 1) {
		             		%>
			                             
	                               <div class="rblock">
	                                <div class="lp_r"></div>
	                                	   <div class="tm_container_r">
	                                	   		 <p class="act_title"><strong><%=innerList.get(1)%></strong></p>
	                                         <p>From <%=innerList.get(8)%> To <%=innerList.get(9)%>
	                                         	 <%=innerList.get(6) != null && (innerList.get(6)).length() != 0 ? "with" + innerList.get(6) + "designation" : ""%> 
	                                         </p>
	                                         <p><%=innerList.get(12) != null && (innerList.get(12)).length() != 0 ? "Skills: " + innerList.get(12) : ""%></p>
	                                 		</div> 
	                               </div>  
	                               <div class="clr"></div>
			               <% } } %>
		             		</div>
		             		 <% } else { %>
                        <div class="lholder prev_empl">
                        
                        <div class="lblock">
		                              
                            <div class="lp"></div>
                            <div class="tm_container">
                                <p class="act_title"><strong>No Previous Employment</strong></p>
								<p>Click on the edit icon on the right side to add Previous Employment</p>
                            </div> 
		                </div>
	                    <div class="clr"></div>
                        <% } %>   
		              </div>

                          <div class="title_prevemp">
	                        <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
	                        			&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) {
	                        %>
					  			<div class="edit_profile">
					        		<%-- <a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=4" rel="popup_name" class="edit poplight" title="edit previous employment">Edit</a> --%>
					        		<a href="AddEmployee.action?operation=U&mode=profile&empId=<%=strProID%>&step=3" rel="popup_name" class="edit poplight" title="edit previous employment">Edit</a>
				        		</div>
							<% } %>
                        </div>
	              </div> 
              
              <div class="clr"></div>         
    </div>
    		
</div>

<div id="popup_name" class="popup_block">
	
	<s:form name="uploadImage" action="UploadImage" enctype="multipart/form-data" method="post">
		<s:hidden name="imageType" value="EMPLOYEE_IMAGE"></s:hidden>
		<input type="hidden" name="empId" value="<%=strProID%>" />
		<s:file name="empImage"></s:file>
		<s:submit value="Upload" cssClass="input_button"></s:submit>
	</s:form>
	
</div>
</div>

<div id="salarypreview"></div>
<div id="salaryhistory"></div>
<div id="sendMail"></div>
<div id="kra_profile"></div>


<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});
</script>