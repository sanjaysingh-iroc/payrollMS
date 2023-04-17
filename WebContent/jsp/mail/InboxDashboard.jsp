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

<%
	UtilityFunctions uF=new UtilityFunctions();
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String sbDataV = (String) request.getAttribute("sbDataV");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	//System.out.println("sbDataV==>"+sbDataV);
	
	String emailId = (String)request.getAttribute("emailId");
	List<List<String>> allEmails = (List) request.getAttribute("allEmails");
	if(allEmails == null) allEmails = new ArrayList<List<String>>();
	//String proCount = (String) request.getAttribute("proCount");

%>

	<div class="col-md-12 no-padding" style="margin-bottom: 15px;">
		<div class="col-md-3 no-padding">
			<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
			<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');">
		</div>
				
		<script>
			 $(function(){
			 	$("#strSearchJob").autocomplete({
					source: [ <%=uF.showData(sbDataV,"") %> ]
				});
			 });
					
		</script>
			  	
	</div>
	
	<div class="row row_without_margin">
		<div class="col-md-3" style="padding-left: 0px;">
			<div class="box box-primary">
				<div class="box-body" style="overflow-y: auto; max-height: 450px;">
					<ul class="products-list product-list-in-box">
								<%if(allEmails != null && allEmails.size()>0) { %>
								  	<%for(int i =0;i<allEmails.size();i++) {
								  		List<String> alInner = allEmails.get(i);
								  		if(alInner != null && alInner.size()>0 && !alInner.isEmpty()){
								  	%>
								  		<li class="item">
								  			<span style="float: left; width: 100%;">
								  				<div style="float: left; ">
								  					<%if(uF.parseToInt(emailId) == uF.parseToInt(alInner.get(0))) { %>
														<a href="javascript:void(0);" class="activelink" onclick="getEmailDetails('ReceivedEMailNotification','<%=alInner.get(0)%>')"><%=alInner.get(1)%><br/><%=alInner.get(2)%></a>
													<%} else { %>
														<a href="javascript:void(0);" onclick="getEmailDetails('ReceivedEMailNotification','<%=alInner.get(0)%>')"><%=alInner.get(1)%><br/><%=alInner.get(2)%></a>
													<%} %>
								  				</div>
								  			</span>
								  		</li>
								  <%  }
								  	} %>	
					</ul>
							<%}else { %>
					 				<div class="nodata msg">No Emails.</div>
					 	  	 <% } %>
				</div>
			</div>
		</div>
				
		<div class="col-md-9" style="padding-left: 0px;min-height: 400px;">
			<div class="box box-primary" style="overflow-y: auto; max-height: 450px; id="actionResult">			<!-- style=" min-height: 400px;"  -->
				<div class="box box-none">
			        <div class="active tab-pane" id="subDivVResult" style="min-height: 400px;">
						
			        </div>
			    </div>
			</div>
		</div>
	</div>

<script type="text/javascript">
	$(document).ready(function(){
		getEmailDetails('ReceivedEMailNotification','<%=emailId%>');
	});
	 
	function getEmailDetails(strAction,emailId) {
		 
			$("#subDivVResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
				type : 'GET',
				url: strAction+'.action?nEmailId='+emailId,
				cache: true,
				success: function(result){
					$("#subDivVResult").html(result);
		   		}
			});
	 }  


	 function submitForm(type) {
			var strSearch = document.getElementById("strSearchJob").value;
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'EmailDashboard.action?strSearchJob='+strSearch,
				success: function(result){
		        	/* $("#divResult").html(result); */
					$("#divResult").html(result);
		        }
			});
		}
	
</script>

