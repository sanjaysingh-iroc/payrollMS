<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<style>
	.list{
		padding-top: 10px;
		border-bottom: 1px solid #F0F0F0;
		padding-bottom: 10px;
	}
</style>

<script src="scripts/ckeditor/ckeditor.js"></script>
<%
	UtilityFunctions uF=new UtilityFunctions();
	String strUserType = (String) session.getAttribute("USERTYPE");
	String strCertiId = (String) request.getAttribute("strCertiId");
%>

<script type="text/javascript" charset="utf-8">

	 CKEDITOR.config.height='500px';
	$(function() {
		$("#fdate").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("#tdate").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    	});
		//fdate tdate
	});

	function addCertificate(operation, certiId, assignToExist) {
		var strTitle = '';
		if (certiId == '') {
			strTitle = 'Add New Certificate';
		} else {
			strTitle = 'Edit Certificate';
		}

		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html(strTitle);
		 if($(window).width() >= 900){
			 $(".modal-dialog").width(900);
		 }
		 $.ajax({
				url : "AddCertificate.action?operation="+ operation + "&certiId=" + certiId + "&assignToExist=" +assignToExist,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		
		if (assignToExist == 'No' || assignToExist == 'Yes') {
			closeCertificatePopup();
		}
	}

	function viewCertificate(id,certiName) { 
		
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html(''+ certiName);
		 $.ajax({
				url : "ViewCertificate.action?ID="+id+"&fromPage=LD",
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	
	var dialogEdit3 = '#newVersionCertiDiv';
	function createNewVersionOfCerti(certiId) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Create New Version Of Certificate');
		 $.ajax({
				url : "CreateNewVersionCertificatePopup.action?certiId="+certiId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		}

	
	function closeCertificatePopup(){
		$(dialogEdit3).dialog('close');
	}
	
	
	function showAllCertificates(value) {
		//alert(value);
		var status = document.getElementById("hideCertiDivStatus"+value).value;
		if(status == '0'){
			document.getElementById("hideCertiDivStatus"+value).value = "1";
			document.getElementById("oldCertiDiv"+value).style.display = "block";
			document.getElementById("CuparrowSpan"+value).style.display = "block";
			document.getElementById("CdownarrowSpan"+value).style.display = "none";
		} else {
			document.getElementById("hideCertiDivStatus"+value).value = "0";
			document.getElementById("oldCertiDiv"+value).style.display = "none";
			document.getElementById("CuparrowSpan"+value).style.display = "none";
			document.getElementById("CdownarrowSpan"+value).style.display = "block";
		}
	}
	
</script>
<%
	List<String> certiIDList = (List<String>) request.getAttribute("certiIDList");
	if(certiIDList == null) certiIDList= new ArrayList<String>();
	
	Map<String, String> hmCertiData = (Map<String, String>) request.getAttribute("hmCertiData");
	if(hmCertiData == null ) hmCertiData = new LinkedHashMap<String,String>();
	
	Map<String, List<String>> hmAllCertiData = (Map<String, List<String>>) request.getAttribute("hmAllCertiData");
	if(hmAllCertiData == null ) hmAllCertiData = new LinkedHashMap<String,List<String>>();
	
	Map<String, List<String>> hmCertificateDetails = (Map<String, List<String>>) request.getAttribute("hmCertificateDetails");
	if(hmCertificateDetails == null ) hmCertificateDetails = new LinkedHashMap<String,List<String>>();
	String sbData = (String) request.getAttribute("sbData");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	//System.out.println("sbData ==>"+sbData);
	//System.out.println("strSearchJob ==>"+strSearchJob);
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
							source: [ <%=uF.showData(sbData,"") %> ]
						});
			       });
					
			  	</script>
			  	<div class="col-md-9 no-padding">
					 <% if (strUserType != null &&  (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) {%>
							<a href="javascript:void(0)" style="float:right;" onclick="addCertificate('A','','')">
						<input type="button" class="btn btn-primary pull-right" value="Add New Certificate"></a>			
					<%} %>
				</div>
			</div> 
            
			<div class="row row_without_margin">
				<div class="col-md-3" style="padding-left: 0px;">
					<div class="box box-primary">
						<div class="box-body">
						<ul class="products-list product-list-in-box">
						<%	for(int i=0; certiIDList != null && i<certiIDList.size(); i++){
								String latestCertiId = hmCertiData.get(certiIDList.get(i));
								if(latestCertiId != null && !latestCertiId.equals("")) {
								List<String> alinner1 = hmCertificateDetails.get(latestCertiId);
									%>
								<li class="item">
									<span style="float: left; width: 100%;">
									     <%=alinner1.get(3)%>
									     <div style="float:left;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;width: 75%;">
										<%if(uF.parseToInt(latestCertiId) == uF.parseToInt(strCertiId)) { %>
											<a href="javascript:void(0);" class="activelink" onclick="getCertificateSummary('ViewCertificate','<%=alinner1.get(0)%>')" title="<%=alinner1.get(1)%>"><%=alinner1.get(1)%></a>
											<br/><span title="<%=alinner1.get(2)%>"><%=alinner1.get(2)%></span>
										<%} else { %>
											<a href="javascript:void(0);" onclick="getCertificateSummary('ViewCertificate','<%=alinner1.get(0)%>')" title="<%=alinner1.get(1)%>"><%=alinner1.get(1)%></a>
											<br/><span title="<%=alinner1.get(2)%>"><%=alinner1.get(2)%></span>
										<%} %>
										</div>
										<input type="hidden" name="hideCertiDivStatus" id="hideCertiDivStatus<%=i %>" value = "0"/>
										<a href="javascript: void(0);" onclick="showAllCertificates('<%=i %>');">
											<span id="CdownarrowSpan<%=i %>"><i class="fa fa-angle-down" aria-hidden="true"></i>
												<!-- <img src="images1/icons/icons/downarrow.png" style="width: 14px;"/> --> 
											</span>
											<span id="CuparrowSpan<%=i %>" style="display: none;"><i class="fa fa-angle-up" aria-hidden="true"></i></span>
										</a>
									</span>
									
									<div id="oldCertiDiv<%=i %>" style="display: none; float: left; width: 95%;">
										<ul style="float: left; width:101%; margin: 0px 0px 0px 0px;">
										<%
											List<String> certiIdList = hmAllCertiData.get(certiIDList.get(i));
											for(int j=0; certiIdList != null && j<certiIdList.size(); j++) {
												if(!latestCertiId.equals(certiIdList.get(j))) {
													List<String> alinner = hmCertificateDetails.get(certiIdList.get(j));
												%>
													<li class="item">
														<span style="float: left; width: 100%;">
														   <%=alinner1.get(3)%>
														   <div style="float:left;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;width: 85%;">
															<a href="javascript:void(0);" onclick="getCertificateSummary('ViewCertificate','<%=alinner.get(0)%>')" title="<%=alinner1.get(1)%>"><%=alinner1.get(1)%></a>
															<br/><span title="<%=alinner1.get(2)%>"><%=alinner1.get(2)%></span>
															</div>
														</span>
													</li>
											 <% } 
											} %>
											<li class="item">
												<% List<String> alinner = hmCertificateDetails.get(certiIDList.get(i).trim());%>
												<span style="float: left; width: 100%;">
												   <%=alinner1.get(3)%>
												   <div style="float:left;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;width: 85%;">
													<a href="javascript:void(0);" onclick="getCertificateSummary('ViewCertificate','<%=alinner.get(0)%>')" title="<%=alinner1.get(1)%>"><%=alinner.get(1)%></a>
													<br/><span title="<%=alinner1.get(2)%>"><%=alinner1.get(2)%></span>
													</div>
												</span>
											</li>
										</ul>
									</div>
								</li>
							<%} else {
								    List<String> alinner1 = hmCertificateDetails.get(certiIDList.get(i).trim());
							%>
									<li class="item"">
										<span style="float: left; width: 100%;">
										  <%=alinner1.get(3)%>
										  <div style="float:left;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;width: 85%;">
											<%if(uF.parseToInt(certiIDList.get(i)) == uF.parseToInt(strCertiId)) { %>
												<a href="javascript:void(0);" class="activelink" onclick="getCertificateSummary('ViewCertificate','<%=alinner1.get(0)%>')" title="<%=alinner1.get(1)%>"><%=alinner1.get(1)%></a>
												<br/><span title="<%=alinner1.get(2)%>"><%=alinner1.get(2)%></span>
											<%} else { %>
												<a href="javascript:void(0);" onclick="getCertificateSummary('ViewCertificate','<%=alinner1.get(0)%>')" title="<%=alinner1.get(1)%>"><%=alinner1.get(1)%></a>
												<br/><span title="<%=alinner1.get(2)%>"><%=alinner1.get(2)%></span>
											<%} %>
										  </div>
										</span>
									</li>
						<%  }
					   } if(certiIDList==null || (certiIDList != null && certiIDList.size() == 0)) { %>
						<li class="nodata msg"> No certificate added yet. </li>
				   <%  } %>
			      </ul>
			     </div>
			     
			      <div class="custom-legends">
					<div class="custom-legend approved"><div class="legend-info">&nbsp;&nbsp;Live</div></div>
					<div class="custom-legend pullout"><div class="legend-info">&nbsp;&nbsp; Waiting for live</div></div>
 				 </div>
			 </div>
		  </div>    
	   
				<div class="col-md-9" style="padding-left: 0px;min-height: 600px;">
					<div class="box box-primary" style="overflow-y: auto; min-height: 600px;" id="actionResult">
					  <div class="box-body">
		                 <div class="active tab-pane" id="divCertiResult" style="min-height: 600px;">
					
		                 </div>
		             </div> 
				  </div>
			   </div>
			</div>
	    </section>
    </div>
</section>

 <div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
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
		getCertificateSummary('ViewCertificate','<%=strCertiId%>','<%=strSearchJob%>');
	});
	
	function getCertificateSummary(strAction,strCertiId,strSearch) {
		//alert("strCertiId==>"+strCertiId);
		$('#divCertiResult').html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
		var form_data = $('#'+this.id).serialize();
		$.ajax({
			type : 'POST',
			url : strAction+'.action?ID='+strCertiId+'&fromPage=LD&strSearchJob='+strSearch,
			data : form_data,
			success : function(result){
				//alert("result==>"+result);
				$('#divCertiResult').html(result);
			}
			
		});
	}

	function submitForm(type) {
		var strSearch = document.getElementById("strSearchJob").value;
		//alert("strSearch==>"+strSearch);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'CertificateInfo.action?strSearchJob='+strSearch,
			success: function(result){
	        	$("#divResult").html(result);
	        }
		});
		
	}
</script>