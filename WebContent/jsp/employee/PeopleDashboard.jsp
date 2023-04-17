<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<style>
    .sk_value{
    margin-bottom: 0px;
    }
    
    .skill_div {
    min-width: 80px;
    } 
    
    .imgA1 { top: 0px; left: 0px; z-index: 1; max-height: 100px; overflow: hidden; } 
	.imgB1 { position:absolute; top: 0px; left: 0px; z-index: 3; max-height: 100px; overflow: hidden; }
	
	 .gender-divs{
	 display: inline-block;
	 position: relative;
	 border-right: 4px solid rgb(231, 231, 231);
	 }
	 
	 .gender-divs i{
	 font-size: 100px;
	 }
	 
	 .imgA1 i { 
	 color: rgb(29, 132, 180)
	 }
	 
	 .gender-info{
	 display: inline-block;vertical-align: top;font-size: 16px;padding-left: 5px;
	 }
	 
	 .gender-perc{
		margin-left: 30px;
		margin-top: 10px;
		font-size: 20px;
		color: rgb(27, 102, 162);
	 }
</style>
<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />
<%-- <script src='scripts/charts/jquery.min.js'></script> --%>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>
<script type="text/javascript">
var cities = [];
var departments = [];
</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="People's Dashboard" name="title" />
    </jsp:include> --%>
    
    	<%
            UtilityFunctions uF = new UtilityFunctions();
            List<List<String>> allWorkingPeopleReport = (List<List<String>>) request.getAttribute("allWorkingPeopleReport");
            List<List<String>> allEmploymentTypeReport = (List<List<String>>) request.getAttribute("allEmploymentTypeReport");
            List<List<String>> allGenderPeopleReport = (List<List<String>>) request.getAttribute("allGenderPeopleReport");
            String unassignCandiCnt = (String) request.getAttribute("unassignCandiCnt");
            Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
            
            /* int totPendingEmp1 = 0;
            int totworkingEmp1 = 0; */
            int totPendingEmp = 0;
            int totProbationEmp = 0;
            int totPermanentEmp = 0;
            int totTemporaryEmp = 0;
            int totResignedEmp = 0;
            int totExEmp = 0;
            int totUsers = 0;
            
            /* Employment Typewise Total */
            int totFullTimeEmp = 0;
            int totPartTimeEmp = 0;
            int totConsultantEmp = 0;
            int totContractualEmp = 0;
            int totInternEmp = 0;
            
            int totRegularEmp = 0;
            int totContractEmp = 0;
            int totProfessionalEmp = 0;
            int totStipendEmp = 0;
            int totScholarshipEmp = 0;
            
            int totTempEmp = 0;
            int totArticleEmp = 0;
            int totPartnerEmp = 0;
            
            int totMaleEmp = 0;
            int totFemaleEmp = 0;
            int totOtherEmp = 0;
            //int totCandidates = 0;
            %>
        <%
            for (int i = 0; i < allWorkingPeopleReport.size(); i++) {
            	List<String> alinner = (List<String>) allWorkingPeopleReport.get(i);
            
            	totPendingEmp += uF.parseToInt(alinner.get(2));
            	totProbationEmp += uF.parseToInt(alinner.get(3));
            	totPermanentEmp += uF.parseToInt(alinner.get(4));
            	totTemporaryEmp += uF.parseToInt(alinner.get(5));
            	totResignedEmp += uF.parseToInt(alinner.get(6));
            	//totworkingEmp1  += uF.parseToInt(alinner.get(2));
            	totExEmp  += uF.parseToInt(alinner.get(7));
            	totUsers += uF.parseToInt(alinner.get(8));
            	//totCandidates += uF.parseToInt(alinner.get(6));
            	//totalAprFinalised1 += uF.parseToInt(alinner.get(10)); 
            }
            
            StringBuilder sbEmpStatusPie = new StringBuilder();
            sbEmpStatusPie.append("{'Status':'New Joinee', 'cnt': "+totPendingEmp+"},");
            sbEmpStatusPie.append("{'Status':'Probation', 'cnt': "+totProbationEmp+"},");
            sbEmpStatusPie.append("{'Status':'Permanent', 'cnt': "+totPermanentEmp+"},");
            sbEmpStatusPie.append("{'Status':'Temporary', 'cnt': "+totTemporaryEmp+"},");
            sbEmpStatusPie.append("{'Status':'Resigned', 'cnt': "+totResignedEmp+"},");
            
			if(sbEmpStatusPie.length()>1) {
            	sbEmpStatusPie.replace(0, sbEmpStatusPie.length(), sbEmpStatusPie.substring(0, sbEmpStatusPie.length()-1));
			}
            
            %>
        <%			
            for (int i = 0; i < allEmploymentTypeReport.size(); i++) {
            	List<String> innerList = (List<String>) allEmploymentTypeReport.get(i);
            	if(hmFeatureStatus == null || (hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL)))) {
            		totFullTimeEmp += uF.parseToInt(innerList.get(2));
                 	totPartTimeEmp += uF.parseToInt(innerList.get(3));
                 	totConsultantEmp += uF.parseToInt(innerList.get(4));
                 	totContractualEmp += uF.parseToInt(innerList.get(5));
                 	totInternEmp += uF.parseToInt(innerList.get(6));
                 	
                } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE))) {
                	totRegularEmp += uF.parseToInt(innerList.get(2));
                 	totContractEmp += uF.parseToInt(innerList.get(3));
                 	totProfessionalEmp += uF.parseToInt(innerList.get(4));
                 	totStipendEmp += uF.parseToInt(innerList.get(5));
                 	totScholarshipEmp += uF.parseToInt(innerList.get(6));
                 	
                } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE))) {
                	totFullTimeEmp += uF.parseToInt(innerList.get(2));
                 	totPartTimeEmp += uF.parseToInt(innerList.get(3));
                 	totConsultantEmp += uF.parseToInt(innerList.get(4));
                 	totTempEmp += uF.parseToInt(innerList.get(5));
                 	totArticleEmp += uF.parseToInt(innerList.get(6));
                 	totPartnerEmp += uF.parseToInt(innerList.get(7));
                }
            	
            }
            
            StringBuilder sbEmpTypePie = new StringBuilder();
            if(hmFeatureStatus == null || (hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL)))) {
	            sbEmpTypePie.append("{'Status':'Full Time', 'cnt': "+totFullTimeEmp+"},");
	            sbEmpTypePie.append("{'Status':'Part Time', 'cnt': "+totPartTimeEmp+"},");
	            sbEmpTypePie.append("{'Status':'Consultant', 'cnt': "+totConsultantEmp+"},");
	            sbEmpTypePie.append("{'Status':'Contractual', 'cnt': "+totContractualEmp+"},");
	            sbEmpTypePie.append("{'Status':'Intern', 'cnt': "+totInternEmp+"},");
            } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE))) {
            	sbEmpTypePie.append("{'Status':'Regular', 'cnt': "+totRegularEmp+"},");
	            sbEmpTypePie.append("{'Status':'Contract', 'cnt': "+totContractEmp+"},");
	            sbEmpTypePie.append("{'Status':'Professional', 'cnt': "+totProfessionalEmp+"},");
	            sbEmpTypePie.append("{'Status':'Stipend', 'cnt': "+totStipendEmp+"},");
	            sbEmpTypePie.append("{'Status':'Scholarship', 'cnt': "+totScholarshipEmp+"},");
            } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE))) {
            	sbEmpTypePie.append("{'Status':'Full Time', 'cnt': "+totFullTimeEmp+"},");
	            sbEmpTypePie.append("{'Status':'Part Time', 'cnt': "+totPartTimeEmp+"},");
	            sbEmpTypePie.append("{'Status':'Consultant', 'cnt': "+totConsultantEmp+"},");
	            sbEmpTypePie.append("{'Status':'Temporary', 'cnt': "+totTempEmp+"},");
	            sbEmpTypePie.append("{'Status':'Article', 'cnt': "+totArticleEmp+"},");
	            sbEmpTypePie.append("{'Status':'Partner', 'cnt': "+totPartnerEmp+"},");
            }
            
            if(sbEmpTypePie.length()>1) {
            	sbEmpTypePie.replace(0, sbEmpTypePie.length(), sbEmpTypePie.substring(0, sbEmpTypePie.length()-1));
                  }
            %>
            
        <%			
            for (int i = 0; i < allGenderPeopleReport.size(); i++) {
            	List<String> innerList = (List<String>) allGenderPeopleReport.get(i);
            
            	totMaleEmp += uF.parseToInt(innerList.get(2));
            	totFemaleEmp += uF.parseToInt(innerList.get(3));
            	totOtherEmp += uF.parseToInt(innerList.get(4));
            }
            
            /* StringBuilder sbEmpGenderPie = new StringBuilder();
            sbEmpGenderPie.append("{'Status':'Male', 'cnt': "+totMaleEmp+"},");
            sbEmpGenderPie.append("{'Status':'Female', 'cnt': "+totFemaleEmp+"},");
            sbEmpGenderPie.append("{'Status':'Other', 'cnt': "+totOtherEmp+"},");
            
            if(sbEmpGenderPie.length()>1) {
            	sbEmpGenderPie.replace(0, sbEmpGenderPie.length(), sbEmpGenderPie.substring(0, sbEmpGenderPie.length()-1));
                  } */
            %>
    	<div class="row jscroll">
        	<section class="col-lg-7 connectedSortable" style="padding-right:0px;">
        		<div class="box box-primary">
	        		<%String wLocationCount = (String) request.getAttribute("wLocationCount"); %>
	                <div class="box-header with-border">
	                    <h3 class="box-title">Geographical Spread</h3>
	                    <div class="box-tools pull-right">
	                    	<span class="badge bg-blue"><%=wLocationCount %></span>
	                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                    </div>
	                </div>    
	                
	                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto;">
	                    <div class="pad">
		                    <!-- Map will be created here --> 
		                    <div id="world-map" style="height: 268px;"></div>
		                  </div>
	                </div>
	                
	                <div class="box-footer">
		              <div class="row">
		              	<div class="col-sm-12 col-xs-12 col-md-12 col-lg-12">
		              		<div style="overflow-x: auto;">
			              		<table class="table table_no_border" style="margin-bottom: 0px;">
				              		<tr>
						              	<script src="js/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
										<%-- <script src="js/jvectormap/jquery-jvectormap-in-mill.js"></script> --%>
										<script src="js/jvectormap/jquery-jvectormap-world-mill-en.js"></script>		
						              	<% 
							               Map<String, String> hmWLocOrgName = (Map<String, String>) request.getAttribute("hmWLocOrgName");
							               Map hmWLocationEmployeeCount = (Map)request.getAttribute("hmWLocationEmployeeCount");
							               if(hmWLocationEmployeeCount==null)hmWLocationEmployeeCount=new HashMap<String, Map<String, String>>();
							               Map hmWorkLocationMap = (Map)request.getAttribute("hmWorkLocationMap");
							               
											Set setWLocationEmployeeCount = hmWLocationEmployeeCount.keySet();
											Iterator itWLocationEmployeeCount = setWLocationEmployeeCount.iterator();
											while(itWLocationEmployeeCount.hasNext()){
												String strWLocationId = (String)itWLocationEmployeeCount.next();
												
												Map hmWLocation = (Map)hmWorkLocationMap.get(strWLocationId);
												if(hmWLocation==null)hmWLocation=new HashMap();
												if(hmWLocation != null && !hmWLocation.isEmpty()) {
										%>	
										
										<td>
					              			<div class="description-block border-right">
							                    <span class="description-percentage text-green"><b><%=(String)hmWLocationEmployeeCount.get(strWLocationId) %></b></span>
							                    <h5 class="description-header"><%=(String)hmWLocation.get("WL_CITY")+", "+(String)hmWLocation.get("WL_COUNTRY") %></h5>
							                    <span class="description-text"><%=(String)hmWLocOrgName.get(strWLocationId) %></span>
							                  </div>	
					              		</td>
						               <script>
						               $.ajax({
						            	   url:"https://maps.googleapis.com/maps/api/geocode/json?address="+encodeURIComponent('<%=(String)hmWLocation.get("WL_CITY")%>')+"&key=AIzaSyBIl1nxilzv8l1GDupibf73zUxnnNBjnic",
						            	   dataType: 'json',	
						            	   async: false,
						            	   success: function(val) {
						            		   if(val.results.length) {
								            	      var location = val.results[0].geometry.location;
								            	      cities.push({latLng:[location.lat,location.lng], name: '<%=(String)hmWLocation.get("WL_CITY")%>'});
								            	    }
						            	   }
						            	 });
						               		    
						               </script> 
										<%   }
											}
										%> 
										<script>
											$('#world-map').vectorMap({
											    map: 'world_mill_en',
											    backgroundColor: "transparent",
											    regionStyle: {
											    	initial: {
												        fill: '#e4e4e4',
												        "fill-opacity": 1,
												        "stroke-width": 1,
												        "stroke-opacity": 1
												      }
											    },
											    normalizeFunction: 'polynomial',  
											    markerStyle: {
											        initial: {
											            fill: '#F8E23B',
											            stroke: '#383f47'
											        }
											    },
											    markers:cities.map(function(h) {
											        return {
											           	latLng: h.latLng,
											            name: h.name
											        }
											    })
											  });
										</script>
									</tr>
			              		</table>
			              	</div>	
		              	</div>
		              </div>
		              <!-- /.row -->
		            </div>
	                <!-- /.box-body -->
	            </div>
        	</section>
        	
        	<section class="col-lg-5 connectedSortable"> 
        		<div class="box box-primary">
		            <div class="box-header with-border">
		                <h3 class="box-title">Gender wise</h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
		            <!-- /.box-header -->
		            <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 200px; min-height: 400px;">
		                <%-- <div class="row row_without_margin">
		                 	<div class="col-lg-12 col-md-12 col-sm-12 col_no_padding">
		                 		<ul class="site-stats-new">
									<li class="bg_lh"><i class="fa fa-female" aria-hidden="true"></i><strong><%=totFemaleEmp %></strong> <small>Female</small></li>
									<li class="bg_lh"><i class="fa fa-male" aria-hidden="true"></i><strong><%=totMaleEmp %></strong> <small>Male</small></li>
									<li class="bg_lh"><i class="fa fa-exclamation" aria-hidden="true"></i><strong><%=totOtherEmp %></strong> <small>Other</small></li>
								</ul>
		                 	</div>
		                 </div> --%> 
		                <div class="attendance row row_without_margin" style="clear: both;padding-top: 20px;">
		                    <div class="col-lg-12 col-md-12 col-sm-12 col_no_padding">
		                    	<div class="col-lg-4 col-md-4 col-sm-4 col_no_padding">
			                        <div class="gender-divs">
									    <div class="imgA1">
									    	<i class="fa fa-female" aria-hidden="true" style="color:rgb(255, 116, 238);"></i>
									    </div>
									    <div id="femaleGreyImg" class="imgB1">
									    	<i class="fa fa-female" aria-hidden="true"></i>
									    </div>
									</div>
									<div class="gender-info"><strong><%=totFemaleEmp %></strong><p>Females</p></div>
									<div class="gender-perc" id="female-perc"></div>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col_no_padding">
									<div class="gender-divs">
									    <div class="imgA1">
									    	<i class="fa fa-male" aria-hidden="true"></i>
									    </div>
									    <div id="maleGreyImg" class="imgB1">
									    	<i class="fa fa-male" aria-hidden="true"></i>
									    </div>
									</div>
									<div class="gender-info"><strong><%=totMaleEmp %></strong> <p>Males</p></div>
									<div class="gender-perc" id="male-perc"></div>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col_no_padding">
									<div class="gender-divs">
									    <div class="imgA1">
									    	<i class="fa fa-exclamation" aria-hidden="true" style="color: rgb(213, 213, 213);"></i>
									    </div>
									    <div id="otherGreyImg" class="imgB1">
									    	<i class="fa fa-exclamation" aria-hidden="true"></i>
									    </div>
									</div>
									<div class="gender-info"><strong><%=totOtherEmp %></strong> <p>Other</p></div>
									<div class="gender-perc" id="other-perc"></div>
								</div>
								<script>
								var female_cnt = parseFloat('<%=totFemaleEmp%>');
								var male_cnt = parseFloat('<%=totMaleEmp%>');
								var other_cnt = parseFloat('<%=totOtherEmp%>');
								
								var sum_of_all = female_cnt + male_cnt + other_cnt;
								var female_perc = ((female_cnt/sum_of_all)*100).toFixed(2);
								var male_perc = ((male_cnt/sum_of_all)*100).toFixed(2);
								var other_perc = ((other_cnt/sum_of_all)*100).toFixed(2);
								
								document.getElementById('male-perc').innerHTML = +male_perc+"%" ;
								document.getElementById('female-perc').innerHTML = female_perc+"%" ;
								document.getElementById('other-perc').innerHTML = other_perc+"%" ;
								document.getElementById('femaleGreyImg').setAttribute('style', 'max-height: ' + (100-female_perc) + 'px; overflow: hidden;');
								document.getElementById('maleGreyImg').setAttribute('style', 'max-height: ' + (100-male_perc) + 'px; overflow: hidden;');
								document.getElementById('otherGreyImg').setAttribute('style', 'max-height: ' + (100-other_perc) + 'px; overflow: hidden;');
								</script>
		                    </div>
		                </div>
		            </div>
		            <!-- /.box-body -->
		        </div>	
        	</section>
        </div>
        <div class="row jscroll">
        	<section class="col-lg-6 connectedSortable" style="padding-right:0px;">
        		<div class="box box-primary">
		            <div class="box-header with-border">
		                <h3 class="box-title">Employee Status wise</h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
		            <!-- /.box-header -->
		            <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                <%-- <div class="row row_without_margin">
		                    <div class="col-lg-12 col-md-12 col-sm-12 col_no_padding">
		                        <ul class="site-stats-new">
		                            <li class="bg_lh"><strong><%=totPendingEmp %></strong> <small>Joining</small></li>
		                            <li class="bg_lh"><strong><%=totProbationEmp %></strong> <small>Probation</small></li>
		                            <li class="bg_lh"><strong><%=totPermanentEmp %></strong> <small>Permanent</small></li>
		                            <li class="bg_lh"><strong><%=totTemporaryEmp %></strong> <small>Temporary</small></li>
		                            <li class="bg_lh"><strong><%=totResignedEmp %></strong> <small>Resigned</small></li>
		                        </ul>
		                    </div>
		                </div> --%>
		                <div class="attendance row row_without_margin clr margintop20">
		                    <div class="col-lg-12 col-md-12 col-sm-12 col_no_padding">
		                        <div id="chartEmpStatusdiv" style="height:300px;"></div>
		                        <script>
		                            var chart2;
		                            var chartData2 = [<%=sbEmpStatusPie %>];
		                            var legend2;
		                            var chart = AmCharts.makeChart( "chartEmpStatusdiv", {
		                              "type": "pie",
		                              "theme": "light",
		                              "dataProvider": chartData2,
		                              "valueField": "cnt",
		                              "titleField": "Status",
		                              "outlineAlpha": 0.4,
		                              "depth3D": 15,
		                              "balloonText": "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>",
		                              "angle": 30,
		                              "export": {
		                                "enabled": true
		                              },
		                              "legend":{
		                            	  "position":"right",
				                            "marginRight":100,
				                            "autoMargins":false
				                            },
				                     "labelsEnabled" : false
		                            } );
		                               
		                        </script>
		                    </div>
		                </div>
		            </div>
		            <!-- /.box-body -->
		        </div>
        	</section>
        	<section class="col-lg-6 connectedSortable">
        		<div class="box box-primary">
		            <div class="box-header with-border">
		                <h3 class="box-title">Employee Type wise</h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
		            <!-- /.box-header -->
		            <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                <%-- <div class="row row_without_margin">
		                    <div class="col-lg-12 col-md-12 col-sm-12 col_no_padding">
		                        <ul class="site-stats-new">
		                        <% if(hmFeatureStatus == null || (hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL)))) { %>
		                            <li class="bg_lh"><strong><%=totFullTimeEmp %></strong> <small>Full Time</small></li>
		                            <li class="bg_lh"><strong><%=totPartTimeEmp %></strong> <small>Part Time</small></li>
		                            <li class="bg_lh"><strong><%=totConsultantEmp %></strong> <small>Consultant</small></li>
		                            <li class="bg_lh"><strong><%=totContractualEmp %></strong> <small>Contractual</small></li>
		                            <li class="bg_lh"><strong><%=totInternEmp %></strong> <small>Intern</small></li>
		                        <% } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE))) { %>
		                        	<li class="bg_lh"><strong><%=totRegularEmp %></strong> <small>Regular</small></li>
		                            <li class="bg_lh"><strong><%=totContractEmp %></strong> <small>Contract</small></li>
		                            <li class="bg_lh"><strong><%=totProfessionalEmp %></strong> <small>Professional</small></li>
		                            <li class="bg_lh"><strong><%=totStipendEmp %></strong> <small>Stipend</small></li>
		                            <li class="bg_lh"><strong><%=totScholarshipEmp %></strong> <small>Scholarship</small></li>
		                        <% } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE))) { %>
		                        	<li class="bg_lh"><strong><%=totFullTimeEmp %></strong> <small>Full Time</small></li>
		                            <li class="bg_lh"><strong><%=totPartTimeEmp %></strong> <small>Part Time</small></li>
		                            <li class="bg_lh"><strong><%=totConsultantEmp %></strong> <small>Consultant</small></li>
		                            <li class="bg_lh"><strong><%=totTempEmp %></strong> <small>Temporary</small></li>
		                            <li class="bg_lh"><strong><%=totArticleEmp %></strong> <small>Article</small></li>
		                            <li class="bg_lh"><strong><%=totPartnerEmp %></strong> <small>Partner</small></li>
		                        <% } %>    
		                        </ul>
		                    </div>
		                </div> --%>
		                <div class="attendance row row_without_margin clr margintop20">
		                    <div class="col-lg-12 col-md-12 col-sm-12 col_no_padding">
		                        <div id="chartEmpTypediv" style="width:100%; height:300px;"></div>
		                        <script>
		                            var chart3;
		                            var chartData3 = [<%=sbEmpTypePie %>];
		                            var legend3;
		                            var chart = AmCharts.makeChart( "chartEmpTypediv", {
		                             "type": "pie",
		                             "theme": "light",
		                             "dataProvider": chartData3,
		                             "valueField": "cnt",
		                             "titleField": "Status",
		                             "outlineAlpha": 0.4,
		                             "depth3D": 15,
		                             "balloonText": "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>",
		                             "angle": 30,
		                             "export": {
		                               "enabled": true
		                             },
		                            "legend":{
		                            "position":"right",
		                            "marginRight":100,
		                            "autoMargins":false
		                            },
		                            "labelsEnabled" : false
		                            });
		                        </script>
		                    </div>
		                </div>
		            </div>
		            <!-- /.box-body -->
		        </div>
        	</section>
        </div>
       <div class="row jscroll">
	        <section class="col-lg-6 connectedSortable" style="padding-right: 0px;">
	        	<div class="box box-default">
               		<% String departCount = (String) request.getAttribute("departCount"); %>
	                <div class="box-header with-border">
	                    <h3 class="box-title">Departmental Spread</h3>
	                    <div class="box-tools pull-right">
	                    	<span class="badge bg-gray"><%=departCount %></span>
	                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                    </div>
	                </div>
	                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto;">
	                	<div id="chartdiv" style="width:100%; height:300px;"></div>
			               <%
				               Map<String, String> hmDepartOrgName = (Map<String, String>) request.getAttribute("hmDepartOrgName");
				               Map hmDepartmentEmployeeCount = (Map)request.getAttribute("hmDepartmentEmployeeCount");
				               if(hmDepartmentEmployeeCount==null)hmDepartmentEmployeeCount=new HashMap<String, Map<String, String>>();
				               Map hmDepartmentMap = (Map)request.getAttribute("hmDepartmentMap");
			               
							Set setDepartmentEmployeeCount = hmDepartmentEmployeeCount.keySet();
							Iterator itDepartmentEmployeeCount = setDepartmentEmployeeCount.iterator();
							while(itDepartmentEmployeeCount.hasNext()){
								String strDepartmentId = (String)itDepartmentEmployeeCount.next();
								if(hmDepartmentMap.get(strDepartmentId) != null) {
							%>
			               <script>
			               	   departments.push({"name": '<%=(String)hmDepartmentMap.get(strDepartmentId) %>',"count": '<%=(String)hmDepartmentEmployeeCount.get(strDepartmentId) %>'});
			               </script> 
							<% }
							}   
							%>
							<script>
							var chart = AmCharts.makeChart( "chartdiv", {
	                             "type": "pie",
	                             "theme": "light",
	                             "dataProvider": departments,
	                             "valueField": "count",
	                             "titleField": "name",
	                             "outlineAlpha": 0.4,
	                             "depth3D": 15,
	                             "balloonText": "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>",
	                             "angle": 30,
	                             "export": {
	                               "enabled": true
	                             },
	                            "legend":{
	                            "position":"right",
	                            "marginRight":100,
	                            "autoMargins":false
	                            },
	                            "labelsEnabled" : false
	                            });
							</script>
	                </div>
	                <!-- /.box-body -->
	            </div>
	        </section>
	        <section class="col-lg-6 connectedSortable">
	        	<div class="box box-primary">
               	<% String skillCount = (String) request.getAttribute("skillCount"); %>
	                <div class="box-header with-border">
	                    <h3 class="box-title">Skills Spread</h3>
	                    <%-- <span class="badge bg-gray"><%=skillCount%></span> --%>
	                    <div class="box-tools pull-right">
	                         <span class="badge bg-gray"><%=skillCount%></span>
	                         <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                    </div>
	                </div>
	                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto;">
	                    <div class="content1" style="padding: 5px;">
							<div id="chartSkillPiediv" style="width:100%; height:336px;"></div>
							<script>
					            var chart4;
					            var chartData4 = [<%=request.getAttribute("sbSkillwisePie") %>];
					            var legend4;
					            var chart = AmCharts.makeChart( "chartSkillPiediv", {
		                             "type": "pie",
		                             "theme": "light",
		                             "dataProvider": chartData4,
		                             "valueField": "cnt",
		                             "titleField": "Skill",
		                             "outlineAlpha": 0.4,
		                             "depth3D": 15,
		                             "balloonText": "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>",
		                             "angle": 30,
		                             "export": {
		                               "enabled": true
		                             },
		                            "legend":{
		                            "position":"right",
		                            "marginRight":100,
		                            "autoMargins":false
		                            },
		                            "labelsEnabled" : false
		                            });
					        </script>
						</div>
	                </div>
	                <!-- /.box-body -->
	            </div>
	        </section>
	    </div> 
