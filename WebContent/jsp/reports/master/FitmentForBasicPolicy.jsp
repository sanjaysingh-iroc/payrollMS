<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<script type="text/javascript">
function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
      return false;

   return true;
}

function calAmt(desigId){
	var allGradeIds = document.getElementById("strDesigGrade_"+desigId).value;
	var strBaseAmount = document.getElementById("strBaseAmount_"+desigId).value;
	var strIncrementAmount = document.getElementById("strIncrementAmount_"+desigId).value;
	
	if(allGradeIds!='' && allGradeIds.length>0){
		var gradeIds = allGradeIds.split(",");
		var totalAmt = 0;
		for (var i = 0; i < gradeIds.length; i++) {
			if(i==0){
				totalAmt = parseFloat(strBaseAmount);
				document.getElementById("strAmount_"+gradeIds[i]).value = parseFloat(totalAmt); 
			} else {
				totalAmt += parseFloat(strIncrementAmount);
				document.getElementById("strAmount_"+gradeIds[i]).value = parseFloat(totalAmt);
			}
		}
	}
}

function submitForm(type){
	if(type == '1'){
		var strOrg = document.getElementById("strOrg").value;
		window.location = 'FitmentForBasicPolicy.action?strOrg='+strOrg;
	} else {
		var choice = document.getElementById("strLevel");
		var exportchoice = "";
		var j = 0;
		for (var i = 0; i < choice.options.length; i++) {
			if (choice.options[i].selected == true) {
				j++;
			}
		}
		
		if(j <= 10){
			document.frm.submit();
		} else {
			alert('Please select level upto 10.');
		}
	}
}

</script>

<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>
<script type="text/javascript">
$(function(){
	$("#strLevel").multiselect().multiselectfilter();
}); 
</script>

<%
	String empId = (String) request.getParameter("empId");
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	Map<String, String> hmLevelMap = (Map<String, String>) request.getAttribute("hmLevelMap");
	if (hmLevelMap == null)hmLevelMap = new LinkedHashMap<String, String>();
	Map<String, Map<String, String>> hmDesigMap = (Map<String, Map<String, String>>) request.getAttribute("hmDesigMap");
	if (hmDesigMap == null)hmDesigMap = new LinkedHashMap<String, Map<String, String>>();
	Map<String, Map<String, String>> hmGradeMap = (Map<String, Map<String, String>>) request.getAttribute("hmGradeMap");
	if (hmGradeMap == null)hmGradeMap = new LinkedHashMap<String, Map<String, String>>();
	Map<String, String> hmFitment = (Map<String, String>) request.getAttribute("hmFitment");
	if (hmFitment == null)hmFitment = new HashMap<String, String>();
	Map<String, String> hmDesigGrade = (Map<String, String>) request.getAttribute("hmDesigGrade");
	if (hmDesigGrade == null) hmDesigGrade = new HashMap<String, String>();
%>

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Fitment for Basic" name="title" />
</jsp:include> --%>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-none nav-tabs-custom">
            	
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                        <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
                        <%session.removeAttribute(IConstants.MESSAGE); %>
						<div class="box box-default collapsed-box">
							<div class="box-header with-border">
							    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
							    <div class="box-tools pull-right">
							        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							    </div>
							</div>
							<div class="box-body" style="padding: 5px; overflow-y: auto;">
								<s:form name="frm" action="FitmentForBasicPolicy" theme="simple">
									<div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Organisation</p>
												<s:select list="orgList" name="strOrg" listKey="orgId"
													listValue="orgName" onchange="document.frm.submit();"></s:select>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Level</p>
												<s:select theme="simple" name="strLevel" id="strLevel" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">&nbsp;</p>
												<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
											</div>
										</div>
									</div>
								</s:form>
							</div>
						</div>
						
						<div>
							<s:form name="frm1" action="FitmentForBasicPolicy" theme="simple" method="post">
								<s:hidden name="strOrg"></s:hidden>
								<s:hidden name="strStatus" value="Update"></s:hidden>
								<input type="hidden" name="strLevelIds" id="strLevelIds" value="<%=(String)request.getAttribute("strLevelIds")%>"/>
								<%if(hmLevelMap!=null && hmLevelMap.size()>0) {%>
									<div><input type="submit" value="Update" class="btn btn-primary" style="margin-bottom: 10px;" onclick="return confirm('Are you sure you wish to update fitment for basic?')" /></div>
								<%} %>
								
								<div class="clr"></div>
					
								<%
								String strLevelIds = (String)request.getAttribute("strLevelIds");
								if (uF.parseToInt((String) request.getAttribute("strOrg")) > 0 && strLevelIds != null && !strLevelIds.trim().equals("") && !strLevelIds.trim().equalsIgnoreCase("NULL") && strLevelIds.length() > 0) { %>
					
								<ul class="level_list">
									<%
										Iterator<String> it = hmLevelMap.keySet().iterator();
										while (it.hasNext()) {
											String strLevelId = (String) it.next();
											String strLevelName = hmLevelMap.get(strLevelId);
									%>
									<li><strong><%=strLevelName%></strong></li>
									<li>
										<ul>
											<%
												Map<String, String> hmDesig = hmDesigMap.get(strLevelId);
												if (hmDesig == null)hmDesig = new LinkedHashMap<String, String>();
												Iterator<String> it1 = hmDesig.keySet().iterator();
												while (it1.hasNext()) {
													String strDesigId = (String) it1.next();
													String strDesigName = hmDesig.get(strDesigId);
											%>
											<li><strong><%=strDesigName%></strong></li>
											<li>
												<ul>
													<%if(hmGradeMap!=null && hmGradeMap.size()>0) {%>
														<li>
															<span>Base Amount:&nbsp;
															<input type="hidden" name="strDesigGrade_<%=strDesigId%>" id="strDesigGrade_<%=strDesigId%>" value="<%=hmDesigGrade.get(strDesigId)%>"/>
															<input type="text" name="strBaseAmount_<%=strDesigId%>" id="strBaseAmount_<%=strDesigId%>" style="width: 50px !important;text-align: right;" value="<%=hmFitment != null && hmFitment.get("BASE_AMOUNT_"+strDesigId) != null ? hmFitment.get("BASE_AMOUNT_"+strDesigId) : "0"%>" onkeyup="calAmt('<%=strDesigId %>');" onkeypress="return isNumberKey(event)"/>
															</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
															<span>Increment Amount:&nbsp;<input type="text" name="strIncrementAmount_<%=strDesigId%>" id="strIncrementAmount_<%=strDesigId%>" style="width: 50px !important;text-align: right;" value="<%=hmFitment != null && hmFitment.get("INCREMENT_AMOUNT_"+strDesigId) != null ? hmFitment.get("INCREMENT_AMOUNT_"+strDesigId) : "0"%>" onkeyup="calAmt('<%=strDesigId %>');" onkeypress="return isNumberKey(event)"/></span>
														</li>
													<%} %>
													<li>
														<div style="width: 100%; overflow: auto;">
															<table cellpadding="2" cellspacing="2" border="0" class="tb_style">
																<tr>
																	<th class="alignCenter" nowrap="nowrap">Grade</th>
																	<%
																		Map<String, String> hmGrade = hmGradeMap.get(strDesigId);
																		if (hmGrade == null)hmGrade = new LinkedHashMap<String, String>();
																		Iterator<String> it2 = hmGrade.keySet().iterator();
																		while (it2.hasNext()) {
																			String strGradeId = (String) it2.next();
																			String strGradeName = hmGrade.get(strGradeId);
																	%>
																	<th class="alignCenter"><%=strGradeName%></th>
																	<%
																		}
																	%>
																</tr>
																<tr>
																	<td class="alignLeft" nowrap="nowrap">Amount (Rs)</td>
																	<%
																		Iterator<String> it13 = hmGrade.keySet().iterator();
																		while (it13.hasNext()) {
																			String strGradeId = (String) it13.next();
																	%>
																	<td class="alignCenter">
																		<input type="text" name="strAmount_<%=strGradeId%>" id="strAmount_<%=strGradeId%>" style="width: 50px !important;text-align: right;" value="<%=hmFitment != null && hmFitment.get("AMOUNT_" + strGradeId) != null ? hmFitment.get("AMOUNT_" + strGradeId) : "0"%>" onkeypress="return isNumberKey(event)"/>
																	</td>
																	<%
																		}
																	%>
																</tr>
																
															</table>
														</div>
													</li>
												</ul>
											</li>
											<%
												}
											%>
										</ul>
									</li>
									<%
										}
									%>
								</ul>
					
					
								<%
									} else {
								%>
									<div class="filter">
										<div class="msg nodata">
											<span>Please choose the Organisation and levels</span>
										</div>
									</div>
								<%
									}
								%>
							</s:form>
						</div>
					</div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>