
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%-- <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script> --%>
<script src="js/jquery.expander.js"></script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"> </script>

<style>


</style>

<script>

$(function(){
	 
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
	
});


$(document).ready(function() {
  $('div.expandDiv').expander({
    slicePoint: 100, //It is the number of characters at which the contents will be sliced into two parts.
    widow: 2,
    expandSpeed: 0, // It is the time in second to show and hide the content.
    userCollapseText: 'Read Less (-)' // Specify your desired word default is Less.
  });
  $('div.expandDiv').expander();
});


function addCandidateShortFormPopup(recruitID, desigName, jobCode, refEmpId) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Apply for the position of '+desigName+' ('+jobCode+')');
	$.ajax({
		url : 'AddCandidateInOneStep.action?recruitId='+recruitID+'&fromPage=JO&refEmpId='+refEmpId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
	

function addCandidateShortFormPopupWithOutJob(refEmpId) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Apply without job profile');
	 $.ajax({
		url : 'AddCandidateInOneStep.action?applyType=withoutjob&fromPage=JO&refEmpId='+refEmpId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
 

function loadMore(proPage, minLimit) {
	document.frm_JobOpportunity.proPage.value = proPage;
	document.frm_JobOpportunity.minLimit.value = minLimit;
	document.frm_JobOpportunity.submit();
}
	
</script>

<%
		String strUserType = (String) session.getAttribute("USERTYPE");
		String strTitle = (String) request.getAttribute(IConstants.TITLE);
		
		UtilityFunctions uF = new UtilityFunctions();
		List<String> recruitmentIDList = (List<String>) request.getAttribute("recruitmentIDList");
		Map<String, List<String>> hmJobReport = (Map<String, List<String>>) request.getAttribute("hmJobReport");
		Map<String, List<String>> hmSingleJobReport = (Map<String, List<String>>) request.getAttribute("hmSingleJobReport");
		
		String proCount = (String)request.getAttribute("proCount");
		String sbData = (String) request.getAttribute("sbData");
		String strSearchJob = (String) request.getAttribute("strSearchJob");
		
		String strRecruitId = (String) request.getAttribute("strRecruitId");
		String refEmpId = (String) request.getAttribute("refEmpId");
			
%>

	
<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">

	<div style="float: left; margin-left: 10px; width: 98%; margin-bottom: 15px; padding: 5px; border-bottom: 1px solid lightgray;">
	<!-- <div style="float:left; margin-left: 10px;"><a href="javascript:void(0);" onclick="window.location='JobOpportunities.action'" style="font-weight: normal;">Reset to default</a></div> -->
		<% if(uF.parseToInt(strRecruitId) == 0) { %>
			<s:form name="frm_JobOpportunity" id="frm_JobOpportunity" action="JobOpportunities" theme="simple">
				<s:hidden name="proPage" id="proPage" />
				<s:hidden name="minLimit" id="minLimit" />
				<div class="col-lg-6 col-md-6 col-sm-12 no-padding">
					<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
					<!-- <input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');"> -->
					<input type="submit" name="btnSubmit" value="Search" class="btn btn-primary" />
					<input type="submit" name="btnReset" value="Reset" class="btn btn-warning" />
				</div>
				
		       	<div style="line-height: 22px; float: right; font-size: 12px; margin-right: 45px;">
		       		<input type="button" name="applyWithoutJob" id="applyWithoutJob" class="btn btn-primary" onclick="addCandidateShortFormPopupWithOutJob('<%=uF.parseToInt(refEmpId) %>')" value="Apply without job profile"/>
		       	</div>
		    </s:form>
		<% } %>
		
		<script>
		$(function(){
	    	   $("#strSearchJob" ).autocomplete({
					source: [ <%=uF.showData(sbData,"") %> ]
				});
	       });
		</script>
	</div>	

	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% if(uF.parseToInt(strRecruitId) > 0) { %>
		<div class="col-lg-7">
		<%
			List<String> alinner = (List<String>) hmSingleJobReport.get(strRecruitId);

			%>
			<div style="float: left; padding: 25px !important; min-height: 65px; border: 1px darkgray solid; margin-bottom: 10px;">
				<div style="float: left; width: 92%; margin-bottom: 7px;">
					<div style="float: left; width: 100%;">
						<div style="float: left; width: 100%; font-size: 16px;"><strong><%=alinner.get(8)%></strong>&nbsp;(<%=alinner.get(1)%>)</div> <%-- <%=alinner.get(4)%> --%>
						<div style="float: left; width: 100%;"> <div class="expandDiv">Job Description:&nbsp;<span style="color: gray;"><%=alinner.get(9)%></span></div></div>
						<div style="float: left; width: 100%;">Essential Skills:&nbsp;<span style="color: gray;"><%=alinner.get(10)%></span></div>
						<div style="float: left; width: 100%; font-size: 11px;">
							<span style="float:left;">Minimum Experience:&nbsp;<span style="color: gray;"><%=alinner.get(11)%>&nbsp;yrs</span></span>
			                <span style="float:left; margin-left: 32px;">Maximum Experience:&nbsp;<span style="color: gray;"><%=alinner.get(12)%>&nbsp;yrs</span></span>
		                </div>
		                <div style="float: left; width: 100%; font-size: 11px;">
							<div style="float: left; width: 100%; font-size: 11px;">Location:&nbsp;<span style="color: gray;"><%=alinner.get(13)%></span></div>
			                <div style="float: left; width: 100%; font-size: 11px;"> Organization:&nbsp;<span style="color: gray;"><%=alinner.get(14)%></span></div>
			                <div style="float: right; width: 100%; margin-top: 15px;">
			                	<input type="button" name="apply" id="apply" class="btn btn-primary" onclick="addCandidateShortFormPopup('<%=alinner.get(0) %>','<%=alinner.get(4) %>','<%=alinner.get(1) %>', '<%=uF.parseToInt(refEmpId) %>')" value="Apply Now"/>
			                </div>
		                </div>
					</div>
				</div>		
			</div>
		</div>
		
		
		<div class="col-lg-5">
			<%
			String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+ "0123456789" + "abcdefghijklmnopqrstuvxyz";
			int n=25;
				for (int i = 0; recruitmentIDList != null && i < recruitmentIDList.size(); i++) {
					alinner = (List<String>) hmJobReport.get(recruitmentIDList.get(i));

					StringBuilder sb = new StringBuilder(n); 
			        for (int a=0; a<n; a++) {
			            int index = (int)(AlphaNumericString.length() * Math.random()); 
			            sb.append(AlphaNumericString.charAt(index)); 
			        } 
			         
			%>
				<div style="float: left; width: 96%; margin-left: 25px; min-height: 65px; border-bottom: 1px lightgray solid; margin-bottom: 10px; padding: 5px;">
					<div style="float: left; width: 92%; margin-bottom: 7px;">
						<div style="float: left; width: 100%;">
							<div style="float: left; width: 100%;"><a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>"><strong><%=alinner.get(8)%></strong>&nbsp;(<%=alinner.get(1)%>)</a></div> <%-- <%=alinner.get(4)%> --%>
							<div style="float: left; width: 100%;"> <div class="expandDiv">Job Description:&nbsp;<span style="color: gray;"><%=alinner.get(9)%></span></div></div>
							<div style="float: left; width: 100%;">Essential Skills:&nbsp;<span style="color: gray;"><%=alinner.get(10)%></span></div>
							<div style="float: left; width: 100%; font-size: 11px;">
								<span style="float:left;">Minimum Experience:&nbsp;<span style="color: gray;"><%=alinner.get(11)%>&nbsp;yrs</span></span>
				                <span style="float:left; margin-left: 32px;">Maximum Experience:&nbsp;<span style="color: gray;"><%=alinner.get(12)%>&nbsp;yrs</span></span>
			                </div>
			                <div style="float: left; width: 100%; font-size: 11px;">
								<span style="float:left;">Location:&nbsp;<span style="color: gray;"><%=alinner.get(13)%></span></span>
				                <span style="float:left; margin-left: 32px;"> Organization:&nbsp;<span style="color: gray;"><%=alinner.get(14)%></span></span>
				                <%-- <span style="float:right;">
				                	<input type="button" name="apply" id="apply" class="btn btn-primary" onclick="addCandidateShortFormPopup('<%=alinner.get(0) %>','<%=alinner.get(4) %>','<%=alinner.get(1) %>')" value="Apply Now"/>
				                </span> --%>
			                </div>
						</div>
					</div>		
				</div>
			<% } %>
		</div>
	<% } %>
	
	<% if(uF.parseToInt(strRecruitId) == 0) { %>
	
		<div class="attendance">
			<%
			String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+ "0123456789" + "abcdefghijklmnopqrstuvxyz";
			int n=25;
				for (int i = 0; recruitmentIDList != null && i < recruitmentIDList.size(); i++) {
					List<String> alinner = (List<String>) hmJobReport.get(recruitmentIDList.get(i));

					StringBuilder sb = new StringBuilder(n); 
			        for (int a=0; a<n; a++) {
			            int index = (int)(AlphaNumericString.length() * Math.random()); 
			            sb.append(AlphaNumericString.charAt(index)); 
			        } 
			         
			%>
				<div style="float: left; width: 96%; margin-left: 25px; min-height: 65px; border-bottom: 1px lightgray solid; margin-bottom: 10px; padding: 5px;">
					<div style="float: left; width: 92%; margin-bottom: 7px;">
						<div style="float: left; width: 100%;">
							<% if(uF.parseToInt(strRecruitId) == 0) { %>
								<div style="float: left; width: 100%;"><a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>"><strong><%=alinner.get(4)%></strong>&nbsp;(<%=alinner.get(1)%>)</a></div>
							<% } else { %>
								<div style="float: left; width: 100%;"><strong><%=alinner.get(8)%></strong>&nbsp;(<%=alinner.get(1)%>)</div> <%-- <%=alinner.get(4)%> --%>
							<% } %>
							<div style="float: left; width: 100%;"> <div class="expandDiv">Job Description:&nbsp;<span style="color: gray;"><%=alinner.get(9)%></span></div></div>
							<div style="float: left; width: 100%;">Essential Skills:&nbsp;<span style="color: gray;"><%=alinner.get(10)%></span></div>
							<div style="float: left; width: 100%; font-size: 11px;">
								<span style="float:left;">Minimum Experience:&nbsp;<span style="color: gray;"><%=alinner.get(11)%>&nbsp;yrs</span></span>
				                <span style="float:left;margin-left: 32px;">Maximum Experience:&nbsp;<span style="color: gray;"><%=alinner.get(12)%>&nbsp;yrs</span></span>
			                </div>
			                <div style="float: left; width: 100%; font-size: 11px;">
								<span style="float:left;">Location:&nbsp;<span style="color: gray;"><%=alinner.get(13)%></span></span>
				                <span style="float:left;margin-left: 32px;"> Organization:&nbsp;<span style="color: gray;"><%=alinner.get(14)%></span></span>
				                <span style="float:right;">
				                	<input type="button" name="apply" id="apply" class="btn btn-primary" onclick="addCandidateShortFormPopup('<%=alinner.get(0) %>','<%=alinner.get(4) %>','<%=alinner.get(1) %>')" value="Apply Now"/>
				                </span>
			                </div>
						</div>
					</div>		
				</div>
			<% } %>
			
			<% if(recruitmentIDList==null || recruitmentIDList.size()==0) { %>
				<div class="nodata msg" style="float: left;">Currently we don't have any open positions.</div>
			<% } %>
		</div>
	
		<%  if(recruitmentIDList!=null && recruitmentIDList.size()>0) { %>
			<div style="text-align: center; float: left; width: 100%;">
				<% int intproCnt = uF.parseToInt(proCount);
					int pageCnt = 0;
					int minLimit = 0;
					for(int i=1; i<=intproCnt; i++) { 
						minLimit = pageCnt * 10;
						pageCnt++;
				%>
				<% if(i ==1) {
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
					if(uF.parseToInt(strPgCnt) > 1) {
						 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
						 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
					}
					if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
				%>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
						<%="< Prev" %></a>
					<% } else { %>
						<b><%="< Prev" %></b>
					<% } %>
					</span>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					
					<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
						<b>...</b>
					<% } %>
				
				<% } %>
				
				<% if(i > 1 && i < intproCnt) { %>
				<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
				<% } %>
				<% } %>
				
				<% if(i == intproCnt && intproCnt > 1) {
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
					 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
					 if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
					%>
					<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
						<b>...</b>
					<% } %>
				
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
					<% } else { %>
						<b><%="Next >" %></b>
					<% } %>
					</span>
				<% } %>
				<%} %>
			</div>
		<%} %>
		
	<%} %>
	
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
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px; overflow-y:auto; padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

