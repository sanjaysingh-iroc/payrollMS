<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<% String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
   String fromPage = (String) request.getAttribute("fromPage"); 
   String callFrom = (String) request.getAttribute("callFrom");
   String alertID = (String) request.getAttribute("alertID");
   String strSearchJob = (String) request.getAttribute("strSearchJob");
   UtilityFunctions uF=new UtilityFunctions();
	String currUserType = (String) request.getAttribute("currUserType");

%>

<section class="content">
    <div class="row jscroll"  id = "divReviewsResult">
        <section class="col-lg-12 connectedSortable"> 
        <div class="col-lg-12 col-md-12" style="margin: 0px 0px 10px 0px; text-align: right";>
				<div class="col-lg-3 col-md-3 no-padding">
					<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
					<input type="button" value="Search" class="btn btn-primary" onclick="submitForm();">
				</div>
				  <%if (strSessionUserType != null && (strSessionUserType .equalsIgnoreCase(IConstants.MANAGER) || strSessionUserType .equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType .equalsIgnoreCase(IConstants.ADMIN) || strSessionUserType .equalsIgnoreCase(IConstants.RECRUITER))) { %>
					    <div class="col-lg-9 col-md-9 pull-right ">
							<a href="javascript:void(0)" onclick="createOneToOne()" class="pull-right">
								<input type="button" class="btn btn-primary" value="Add New 1-1">
							</a>
					    </div>
				  <% } %>
       	</div>
       	<div class="col-md-12">
				<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
				<% session.removeAttribute(IConstants.MESSAGE); %>
		</div>
		<div class="row row_without_margin">
				<div class="col-md-3">
					<div class="box box-none">
						<div class="nav-tabs-custom">
				             <ul class="nav nav-tabs">
					             <% if(callFrom == null || callFrom.equals("") || callFrom.equalsIgnoreCase("null")) { %>
					                 <li class="active"><a href="javascript:void(0)" onclick="getOneToOneList('OneToOneNamesList','L','<%=currUserType %>','<%=strSearchJob%>');" data-toggle="tab">Live</a></li>
					                 <li><a href="javascript:void(0)" onclick="getOneToOneList('OneToOneNamesList','C','<%=currUserType %>','<%=strSearchJob%>');" data-toggle="tab">Closed</a></li>
				                 <% } else { %>
				                 	 <li class="active"><a href="javascript:void(0)" onclick="getOneToOneList('OneToOneNamesList','SRR','<%=currUserType %>','<%=strSearchJob%>');" data-toggle="tab">Self Review Request</a></li>
				                 <% } %>
				             </ul>
				             
				             <div class="tab-content" >
				                 <div class="active tab-pane" id="oneToOneNameResult" style="min-height: 600px;">
							
				                 </div>
				             </div>
				        </div>
					</div>
				</div>
				<div class="col-md-9" style="padding-left: 0px; min-height: 600px;">
					<div class="box box-none" style="overflow-y: auto; min-height: 600px;" id="actionResult">
		                 <div class="active tab-pane" id="oneToOneDetails" style="min-height: 600px;">
					
						</div>
					</div>
				</div>
		</div> 
		
		
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
		
						 
        </section>
    </div>
    
    
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	getOneToOneList('OneToOneNamesList','L','<%=currUserType %>','<%=strSearchJob%>',' ');
	
});

function getOneToOneList(strAction, dataType, currUserType, strSearch, onetooneId) {
	var callFrom = '<%=callFrom%>';
	var alertID = '<%=alertID%>';
	$("#oneToOneNameResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?dataType='+dataType+'&currUserType='+currUserType+'&strSearchJob ='+strSearch+'&callFrom='+callFrom
				+'&alertID='+alertID+'&strOneToOneId='+onetooneId,
		cache: true,
		success: function(result){
			$("#oneToOneNameResult").html(result);
   		}
	});
}

function createOneToOne(){
	//alert("create");
	$("#divReviewsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'CreateOneToOne.action',
		cache: true,
		success: function(result){
			//alert("result1==>"+result);
			$("#divReviewsResult").html(result);
   		}
	});
}

</script>
</section> 