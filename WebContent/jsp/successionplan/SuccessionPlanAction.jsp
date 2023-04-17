<%@page import="java.util.Arrays"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%
UtilityFunctions uF = new UtilityFunctions();
String fromPage = (String)request.getParameter("fromPage");
java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); 
if(couterlist == null) couterlist = new java.util.ArrayList();

Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	
List<List<String>> leaveTypeListWithBalance = (List<List<String>>) request.getAttribute("leaveTypeListWithBalance");
%>

<script type="text/javascript">
     $(function(){
   		$("input[type='submit']").click(function(){
   			$("#frmSuccessionPlanAction").find('.validateRequired').filter(':hidden').prop('required',false);
   			$("#frmSuccessionPlanAction").find('.validateRequired').filter(':visible').prop('required',true);
   		});
   		
   		$("#learningIds").multiselect().multiselectfilter();
   		$("#reviewIds").multiselect().multiselectfilter();
   	});
      
      	
	function checkGapStatus(id) {
		if(document.getElementById("sendtoGapStatus").checked) {
			document.getElementById("learningPlanSpan").style.display = 'block';
		} else {
			document.getElementById("learningPlanSpan").style.display = 'none';
		}
	}
	</script>

<%
	String hrremark = (String) request.getAttribute("hrremark");
	String areasOfStrength = (String) request.getAttribute("areasOfStrength");
	String areasOfDevelopment = (String) request.getAttribute("areasOfDevelopment");
	List<String> alLearningPlans = (List<String>) request.getAttribute("alLearningPlans");
	
	boolean flag = (Boolean) request.getAttribute("flag");
	String strApprovedBy = (String)request.getAttribute("strApprovedBy");
	Map<String, String> hmActivityMap = (Map<String, String>) request.getAttribute("hmActivityMap"); 
	
	String appraiseeName = (String)request.getAttribute("appraiseeName");
	
	%>
<div class="leftbox reportWidth" id="formBody">
	<s:form method="POST" action="SuccessionPlanAction" theme="simple" id="frmSuccessionPlanAction">
		<s:hidden name="strIncumbentEmpId" />
		<s:hidden name="empid" />
		<s:hidden name="strDesignation" />
		<s:hidden name="fromPage" />

		<div style="float: left; width: 100%; margin-top: 10px;"><b><%=appraiseeName %></b>'s Summary</div>
		<div style="float: left; width: 100%; margin-top: 20px;">  <!-- border: 1px solid #dbdbdb; -->
			<table class="table table-bordered" <% if(flag) { %> style="margin: 0px; border: 0px none !important;" <% } else { %> style="border: 0px none !important;"<% } %>>
			<% if (!flag) { %>
			
				<tr>
					<td style="text-align: right;" class="txtlabel" valign="top"><b>Remark:<sup>*</sup></b>&nbsp;</td>
					<td><s:textarea name="remark" cssClass="validateRequired" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
				</tr>
				<tr>
					<td style="text-align: right;" class="txtlabel" valign="top"><b>Areas of Strength:</b>&nbsp;</td>
					<td><s:textarea name="areasOfStrength" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
				</tr>
				<tr>
					<td style="text-align: right;" class="txtlabel" valign="top"><b>Areas of Development:</b>&nbsp;</td>
					<td><s:textarea name="areasOfDevelopment" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
				</tr>
				<% } else { %>
				<tr>
					<td colspan="2" class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Remark:</b>
						<div style="margin-left: 30px;"><%=hrremark != null ? hrremark : ""%></div>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Areas of Strength:</b>
						<div style="margin-left: 30px;"><%=areasOfStrength != null ? areasOfStrength : ""%></div>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="txtlabel" valign="top" style="width:50%; padding-left: 10px !important;"><b>Areas of Development:</b>&nbsp;
						<div style="margin-left: 30px;"><%=areasOfDevelopment != null ? areasOfDevelopment : ""%></div>
					</td>
				</tr>
				<% } %>
			</table>
		</div>
		
		<div style="float: left; width: 100%;">
			<table class="table table_no_border">
			<% if (flag) { %>
				<tr><td colspan="2" style="padding-right: 20px;" align="right"><strong><i>Approved by - <%=strApprovedBy %></i></strong></td></tr>
			<% } %>
			</table>
		</div>
		
		
			<%
				if (!flag) {
			%>
			<div id="link_div" class="box box-none nav-tabs-custom clr" style="float: left; margin-top: 15px;">
				<ul class="nav nav-tabs">
					<li class="active"><a href="javascript:void(0)" onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
					</li>
				</ul>
	
				<div id="activity_div" style="width:100%;" class="tab-content">
					<table class="table table_no_border autoWidth">
						<tr>
							<td class="txtlabel alignRight">
								Send to Learning Gap <s:checkbox name="sendtoGapStatus" id="sendtoGapStatus" onclick="checkGapStatus(this)"/>
							</td>
							<td>
								<span id="learningPlanSpan" style="display: none; float: left; margin-left: 20px; margin-top: 5px;"> 
									<select name="learningIds" id="learningIds" multiple="multiple"><%=(String)request.getAttribute("sbLearningOptions") %></select>
								</span>
							</td>
						</tr>
						<tr>
							<td class="txtlabel alignRight">Reviews:<sup>*</sup></td>
							<td >
								<select name="reviewIds" id="reviewIds" style="margin-left: 20px;" multiple="multiple"><%=(String)request.getAttribute("sbReviewOptions") %></select>
							</td>
						</tr>
						<tr>
							<td class="txtlabel alignRight" valign="top" id="reasonLTD">Reason:<sup>*</sup></td>
							<td><s:textarea name="strReason" cssClass="validateRequired" cssStyle="margin-left: 20px; width: 500px !important; height: 70px;"></s:textarea>
							<span class="hint">Add the reason for this activity for reference.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
					</table>
				</div>
			</div>
				<div>
					<s:submit name="submit" cssClass="btn btn-primary" value="Finalise" />
				</div>
			<% } else { %>
				<table class="table table_no_border autoWidth">
						<tr>
							<th class="txtlabel alignRight"> Assigned Learnings:</th>
							<td><%=(String)request.getAttribute("strLearningPlans") %> </td>
						</tr>
						<tr>
							<th class="txtlabel alignRight">Aligned Reviews:</th>
							<td><%=(String)request.getAttribute("strAlignedReview") %> </td>
						</tr>
						<tr>
							<th class="txtlabel alignRight" valign="top">Reason:</th>
							<td><%=(String)request.getAttribute("strReson") %> </td>
						</tr>
					</table>
			<% } %>	
		</s:form>
	</div>


<script>


<%-- $("#frmSuccessionPlanAction").submit(function(e){
	e.preventDefault();
	var fromPage = '<%=fromPage %>';
	//	alert("updateMyReview jsp ==appId==>"+appId+"==>appFreqId==>"+appFreqId);
	var form_data = $("#frmSuccessionPlanAction").serialize();
	$.ajax({ 
		type : 'POST',
	//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
		url: "SuccessionPlanAction.action",
		data: form_data+"&submit=Finalise",
		cache: true,
		success: function(result){
			getReviewStatus('AppraisalStatus',fromPage);
   		},
		error: function(result){
			getReviewStatus('AppraisalStatus',fromPage);
		}
	});
}); --%>



</script>
