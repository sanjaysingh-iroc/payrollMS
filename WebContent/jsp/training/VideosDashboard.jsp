<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<style>
#lt_wrapper .row{
margin-left: 0px;
margin-right: 0px;
}
</style>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>  --%>
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.css' />
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.print.css' media='print' />
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.min.js"> </script>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("body").on('click','#closeButton',function(){
			$(".modal-body").height(400);
			$(".modal-dialog").removeAttr('style');
			$("#modalInfo").hide();
	    });
		$("body").on('click','.close',function(){
			$(".modal-body").height(400);
			$(".modal-dialog").removeAttr('style');
			$("#modalInfo").hide();
		});
	
		
	});

	
</script>

<%
	UtilityFunctions uF=new UtilityFunctions();
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String sbDataV = (String) request.getAttribute("sbDataV");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	//System.out.println("sbDataV==>"+sbDataV);
	
	String strVideoId = (String)request.getAttribute("strVideoId");
	String fromPage = (String)request.getAttribute("fromPage");
	List<List<String>> alVideoDetails = (List) request.getAttribute("alVideoDetails");
	if(alVideoDetails == null) alVideoDetails = new ArrayList<List<String>>();
	//String proCount = (String) request.getAttribute("proCount");

%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable"> 
        	<div class="col-md-12 no-padding" style="margin-bottom: 15px;">
				<div class="col-md-3 no-padding">
					<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
					<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');">
				</div>
	       
				<script>
			       $(function(){
			    	   $("#strSearchJob" ).autocomplete({
							source: [ <%=uF.showData(sbDataV,"") %> ]
						});
			       });
					
			  	</script>
			  	<div class="col-md-9 no-padding">
					 <% if (strUserType != null &&  (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) {%>
							<a href="javascript:void(0)" style="float:right;" onclick="addVideo()">
						    <input type="button" class="btn btn-primary pull-right" value="Add New Video"></a> 					
					<%} %>
				</div>
			</div>	
         
			<div class="row row_without_margin">
				<div class="col-md-3" style="padding-left: 0px;">
					<div class="box box-primary">
					<!-- ===start parvez date: 11-11-2021 Note added style for max height=== -->
						<div class="box-body" style="overflow-y: auto; max-height: 450px;">
					<!-- ===end parvez date: 11-11-2021=== -->	
						
						<!-- <div  id="divVNamesResult" style="min-height: 600px;">
							
				        </div> -->
				        
					        <ul class="products-list product-list-in-box">
								<%if(alVideoDetails != null && alVideoDetails.size()>0) { %>
								  	<%for(int i =0;i<alVideoDetails.size();i++) {
								  		List<String> alInner = alVideoDetails.get(i);
								  		if(alInner != null && alInner.size()>0 && !alInner.isEmpty()){
								  	%>
								  			<li class="item">
												<span style="float: left; width: 100%;">
												<%-- <% if(alInner.get(3) != null){ %>
													<%=alInner.get(3) %>
												<% } %> --%>
												 	<div style="float:left;">
												 		<%-- <% if(alInner.get(3) != null){ %>
															<%=alInner.get(3) %>
														<% } %> --%>
														<%if(uF.parseToInt(strVideoId) == uF.parseToInt(alInner.get(0))) { %>
																<a href="javascript:void(0);" class="activelink" onclick="getLearningVideoDetails('LearningVideoDetails','<%=alInner.get(0)%>','<%=fromPage%>')"><%=alInner.get(1)%></a>
																<%-- <br/><%=alInner.get(2)%> --%>
														<%} else { %>
																<a href="javascript:void(0);" onclick="getLearningVideoDetails('LearningVideoDetails','<%=alInner.get(0)%>','<%=fromPage%>')"><%=alInner.get(1)%></a>
																<%-- <br/><%=alInner.get(2)%> --%>
														<%} %>
													</div>
												</span>
											</li>
								  	<%  }
								  	}%>	
									  	
								  	<%-- <%if(alVideoDetails.size() > 0){ %>
								  		<div style="text-align: center;clear: both;width: 100%;">
										<%
											int intproCnt = uF.parseToInt(proCount);
												int pageCnt = 0;
												int minLimit = 0;
					
												for (int i = 1; i <= intproCnt; i++) {
													minLimit = pageCnt * 15;
													pageCnt++;
										%>
												<%
													if (i == 1) {
														String strPgCnt = (String) request.getAttribute("proPage");
														String strMinLimit = (String) request.getAttribute("minLimit");
														if (uF.parseToInt(strPgCnt) > 1) {
															strPgCnt = (uF.parseToInt(strPgCnt) - 1) + "";
															strMinLimit = (uF.parseToInt(strMinLimit) - 15)
																	+ "";
														}
														if (strMinLimit == null) {
															strMinLimit = "0";
														}
														if (strPgCnt == null) {
															strPgCnt = "1";
														}
												%>
														<span style="color: lightgray;"> <%
							 								if (uF.parseToInt((String) request.getAttribute("proPage")) > 1) {
												 %>
																<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>');"> <%="< Prev"%></a>
															<%
															} else {%>
																 <b><%="< Prev"%></b>
														 <% }%> 
													  </span> 
													 <span>
														 <a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>');"
														<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
															style="color: black;" <%}%>><%=pageCnt%></a>
													</span>
					
													<% 	if ((uF.parseToInt((String) request.getAttribute("proPage")) - 3) > 1) {%>
															<b>...</b>
													<%	} %>
												<%	} %>
					
												<%if (i > 1 && i < intproCnt) { %>
													<% if (pageCnt >= (uF.parseToInt((String) request.getAttribute("proPage")) - 2) && pageCnt <= (uF.parseToInt((String) request.getAttribute("proPage")) + 2)) {%>
														<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>');"
															<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
															style="color: black;" <%}%>><%=pageCnt%></a>
														</span>
													<% 	} %>
												<% } %>
					
												<%if (i == intproCnt && intproCnt > 1) {
														String strPgCnt = (String) request.getAttribute("proPage");
														String strMinLimit = (String) request.getAttribute("minLimit");
														strPgCnt = (uF.parseToInt(strPgCnt) + 1) + "";
														strMinLimit = (uF.parseToInt(strMinLimit) + 15)
																+ "";
														if (strMinLimit == null) {
															strMinLimit = "0";
														}
														if (strPgCnt == null) {
															strPgCnt = "1";
														}
												%>
												<%if ((uF.parseToInt((String) request.getAttribute("proPage")) + 3) < intproCnt) {%>
													<b>...</b>
												<% } %>
												<span>
													<a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>');"
													<%if (uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
															style="color: black;" <%}%>><%=pageCnt%></a>
												</span> 
												<span style="color: lightgray;"> 
													<%if (uF.parseToInt((String) request.getAttribute("proPage")) < pageCnt) {%>
														<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>');"><%="Next >"%></a>
													<%} else { %> 
														<b><%="Next >"%></b> 
													<%}%> 
												</span>
											<%}%>
										<%}%>
									</div>
							  	  	  <%} %> --%>						  	
								<%}else { %>
					 				<div class="nodata msg">No Videos.</div>
					 	  	   <% } %> 
					 	  	   
					 	  	   
						 	 </ul>
				        
						</div>
						
				 	</div>
			  	</div>    
		   
				<div class="col-md-9" style="padding-left: 0px;min-height: 400px;">
			<!-- ===start parvez date: 11-11-2021=== -->
					<div class="box box-primary" style="overflow-y: auto; max-height: 450px; id="actionResult">			<!-- style=" min-height: 400px;"  -->
			<!-- ===end parvez date: 11-11-2021=== -->
						<div class="box box-none">
			                <div class="active tab-pane" id="subDivVResult" style="min-height: 400px;">
						
			                </div>
			            </div> 
					</div>
				</div>
			</div>
	    </section>
    </div>
    <!-- <div class="custom-legends">
		<div class="custom-legend approved"><div class="legend-info">&nbsp;&nbsp;Live</div></div>
		<div class="custom-legend pullout"><div class="legend-info">&nbsp;&nbsp;Waiting for live</div></div>
	</div> -->
</section>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript">
	$(document).ready(function(){
		getLearningVideoDetails('LearningVideoDetails','<%=strVideoId%>','LD');
	});

	 function addVideo() {
			/* $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>'); */
			$("#divCDResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
				type : 'POST',
				url: 'AddNewVideo.action',
				cache: true,
				success: function(result){
					//alert("addTrainingplan result1==>"+result);
					/* $("#divResult").html(result); */
					$("#divCDResult").html(result);
		   		}
			});
	 }
	 
	function getLearningVideoDetails(strAction,videoId,fromPage) {
		 
			$("#subDivVResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
				type : 'GET',
				url: strAction+'.action?learningVideoId='+videoId+'&fromPage='+fromPage,
				cache: true,
				success: function(result){
					$("#subDivVResult").html(result);
		   		}
			});
	 }  


	 function submitForm(type) {
			var strSearch = document.getElementById("strSearchJob").value;
			//alert("strSearch==>"+strSearch);
			/* $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>'); */
			$("#divCDResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'VideosDashboard.action?strSearchJob='+strSearch,
				success: function(result){
		        	/* $("#divResult").html(result); */
					$("#divCDResult").html(result);
		        }
			});
			
		}
	
</script>
