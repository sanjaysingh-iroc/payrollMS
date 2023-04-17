<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<style>
    .sk_value{
    margin-bottom: 0px;
    }
    
    .skill_div{
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
var inoutDetails = [];
</script>

 		  <section class="col-lg-12 connectedSortable">
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
		                    <div id="world-map" style="height: 312px;"></div>
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
						                   String longitude  = null;
									       String lattitude =  null;
									       String punch_mode = null;
									       Map<String, String> hmWLocOrgName = (Map<String, String>) request.getAttribute("hmWLocOrgName");
							               Map hmWLocationEmployeeCount = (Map)request.getAttribute("hmWLocationEmployeeCount");
							               if(hmWLocationEmployeeCount==null)hmWLocationEmployeeCount=new HashMap<String, Map<String, String>>();
							               Map hmWorkLocationMap = (Map)request.getAttribute("hmWorkLocationMap");
							               Map<String, List<String>> hmLongLatDetails = (Map)request.getAttribute("hmLongLatDetails");
							               if(hmLongLatDetails==null)hmLongLatDetails=new HashMap<String, List<String>>();
							               Iterator<String> itLongLatDetails = hmLongLatDetails.keySet().iterator();
									    	while(itLongLatDetails.hasNext()) {
							           			String longLatId = itLongLatDetails.next();
                                               	List<String> listInOutDetails = hmLongLatDetails.get(longLatId);
							           			if(longitude!=null && lattitude!=null )
							           			{
							           				if(longitude.equals(listInOutDetails.get(0)) && lattitude.equals(listInOutDetails.get(1)))
							           				{
							           					punch_mode = "In_Out";
							           				}
							           				else
							           				punch_mode = listInOutDetails.get(3);
							           			}
							           			else
							           			punch_mode = listInOutDetails.get(3);
							           			longitude=listInOutDetails.get(0);
							           			lattitude = listInOutDetails.get(1);
							           			String name =listInOutDetails.get(2)+","+punch_mode;
							           		%>
							           		<script>
							           		inoutDetails.push({latLng:[<%=lattitude%>,<%=longitude%>],name:'<%=name%>'});
							           		</script>
							           		<% 
											}%>
							           		<% 
							           		// For getting lattitude & Longitude of city from google api
							           		
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
						            	/* $.ajax({
						            	   url:"https://maps.googleapis.com/maps/api/geocode/json?address="+encodeURIComponent('')+"&key=AIzaSyBIl1nxilzv8l1GDupibf73zUxnnNBjnic",
						            		dataType: 'json',
						            	   async: false,
						            	   success: function(val) {
						            		if(val.results.length)
						            			   {
								            	      var location = val.results[0].geometry.location;
								      	      cities.push({latLng:[location.lat,location.lng],name: ''});
						            			   }
						            	   }
							            	   });
						            	*/
						            	
																			             
						               </script>
										<%   }
											}
										%> 
										<script>
										
												 function initMap() {
												        var myLatLng = {lat: -25.363, lng: 131.044};
													  	//var map = new google.maps.Map(document.getElementById('world-map'), {
												         // zoom: 4,
												         // center: myLatLng
												        //});
													
															$('#world-map').vectorMap({
																  map: 'world_mill_en',
																    backgroundColor: "transparent"
																
															})
												        var marker = new google.maps.Marker({
												          position: myLatLng,
												          map: map,
												          title: 'Hello World!'
												        });
												      }
									</script>
									 <script async defer
   											 src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBIl1nxilzv8l1GDupibf73zUxnnNBjnic&callback=initMap">
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

