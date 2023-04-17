<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>



<script src="scripts/ckeditor_cust/ckeditor.js"></script> 
<script type="text/javascript" src="scripts/customAjax.js"></script>
<!-- created by seema -->
<script type="text/javascript" src="scripts/jquery.shorten.1.0.js"></script>

<head>
<script type="text/javascript">
	$(document).ready(function() {
		$(".description").shorten({
			"showChars" : 50,
			"moreText" : "See More",
			"lessText" : "Less"
		});

		$(".instruction").shorten({
			"showChars" : 50,
			"moreText" : "See More",
			"lessText" : "Less"
		});

	});
	
	function generateRevieweePanel(reviewId) {
		//var revieweeId = document.getElementById(revieweeId).value;
		window.location='ImportReviewSectionSubsection.action?exceldownload=true&reviewId='+reviewId;
	}
	
</script>
<style type="text/css">
.zoom:hover {
  -ms-transform: scale(1.5); /* IE 9 */
  -webkit-transform: scale(1.5); /* Safari 3-8 */
  transform: scale(1.5); 
}

/* ===start parvez date: 22-03-2022=== */
	#textlabel{
	white-space:pre-line;
	}
/* ===end parvez date: 22-03-2022=== */	
</style>
</head>
<!-- created by seema -->

<%-- <script src="js/customAjaxForReview3.js"></script> --%>
<jsp:include page="../performance/CustomJsForAppraisalSummary.jsp"></jsp:include>
<jsp:include page="../performance/CustomAjaxForReview.jsp"></jsp:include>

<g:compress>
<style>
	.divvalign {vertical-align: top;}
	.add1 {background-image: url("images1/add-item.png");background-position: right center;
	    background-repeat: no-repeat;display: block;float: left;padding: 0 20px 0 0;
	    text-decoration: none;text-indent: -9999px;width: 10px;
	}

/* 	.ul_class li {margin: 10px 0px 10px 100px;}*/
	.ul_class1 li {margin: 10px 0px 10px 0px;}
	.ul_section li {margin: 0px 0px 0px 5px;}
	.ul_subsection li {margin: 0px 0px 0px 20px;}
</style> 
</g:compress> 

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

	String orientation = (String)request.getAttribute("oreinted");
	String appFreqId = (String)request.getParameter("appFreqId");
	String id = (String)request.getParameter("id");
	String fromPage = (String)request.getParameter("fromPage");
	
	List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
	if(appraisalList == null) appraisalList = new ArrayList<String>();
	
	Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
	Map<String, String> questMp = (Map<String, String>) request.getAttribute("questMp");
	Map<String, Map<String,String>> memberMp =(Map<String, Map<String,String>>) request.getAttribute("memberMp");
	
	boolean flagProcess = (Boolean) request.getAttribute("flagProcess");
	
	Map<String, String> hmSections = (Map<String, String>) request.getAttribute("hmSections");
	Map<String, String> hmSubSections = (Map<String, String>) request.getAttribute("hmSubSections");
	Map<String, String> hmQuestions = (Map<String, String>) request.getAttribute("hmQuestions");
	Map<String,String> hmAnswerType = (Map<String,String>) request.getAttribute("hmAnswerType");
	
	int newlvlno =1;
	double sectionTotWeightage = 0;
	List<List<String>> mainLevelList = (List<List<String>>) request.getAttribute("mainLevelList");
	Map<String, List<List<String>>> hmSystemLevelMp = (Map<String, List<List<String>>>)request.getAttribute("hmSystemLevelMp");
	Map<String, List<Map<String, List<List<String>>>>> levelMp = (Map<String, List<Map<String, List<List<String>>>>>) request.getAttribute("levelMp");
	UtilityFunctions uF = new UtilityFunctions();
	
	if(mainLevelList != null && !mainLevelList.isEmpty()) {
		newlvlno = mainLevelList.size()+1;
		for(int i=0; i<mainLevelList.size(); i++) {
			List<String> mainInnerlist = mainLevelList.get(i);
			sectionTotWeightage += uF.parseToDouble(mainInnerlist.get(5));
		}
	}
	//System.out.println("ApSum.jsp/103--sectionTotWeightage="+sectionTotWeightage);
	
	boolean flagPublish = false;
	boolean flag = false;
	
	if(appraisalList != null && !appraisalList.isEmpty() && appraisalList.size() > 0) {
		if(uF.parseToBoolean(appraisalList.get(25))) {
			flagPublish = true;
		}
	 	if(uF.parseToBoolean(appraisalList.get(26))) {
			flag = true;
		}
	}
	boolean finalFlag = (Boolean) request.getAttribute("finalFlag");
	String tabName = request.getParameter("tabName");
	//System.out.println("tabName ===>> " + tabName);
	
	/* System.out.println("finalFlag ===>> " + finalFlag);
	System.out.println("flag ===>> " + flag);
	System.out.println("flagPublish ===>> " + flagPublish);
	System.out.println("flagProcess ===>> " + flagProcess); */
	
%>
<% if(appraisalList != null && !appraisalList.isEmpty() && appraisalList.size()>0) { %>
<%if(fromPage == null || !fromPage.equals("AD")) { %>
 <section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary"> 
   	<%} %>
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
						<%=uF.showData((String) session.getAttribute("sbMessage"), "") %>
						<% session.setAttribute("sbMessage", ""); %>
						<%if(fromPage == null || !fromPage.equals("AD")) { %>
						 <section class="content">
						    <div class="row jscroll">
						        <section class="col-lg-12 connectedSortable" style="margin-top: 10px;"> 
						        <%} %>
						        <!-- created by seema -->
						        <% if(request.getParameter("tabName")==null || request.getParameter("tabName").equals("") || request.getParameter("tabName").equalsIgnoreCase("undefined") || request.getParameter("tabName").equals("summary")) { %>
						        <!-- created by seema -->
						        
						         <div style=" max-height: 600px !important; overflow-y: hidden;" id="sectionSummary"><!-- Start Dattatray  -->						         
						        	<div class="box box-primary" style="border-top: 3px solid #d2d6de;">
						                <% if(appraisalList != null && !appraisalList.isEmpty()  && appraisalList.size()>0) { %>
						                         <!-- created by seema -->
						                       <div class="box-header with-border">  <!-- style="background-color:#d2d6de;" -->
								                <!-- created by seema -->
								                    <h4 class="box-title" style="width: 100%;">
								                    	<div style="display: inline;"><%=uF.showData(appraisalList.get(1), "")%></div>
														<span style="float:right;">
															
															<s:if test="type =='choose'">
																<a href="javascript:void(0);" onclick="if(confirm('Are you sure, You want to create Review from this template?')) window.location='CreateAppraisalFromTemplate.action?appFreqId=<%=appFreqId%>&existID=<s:property value="id"/>';">Choose this Review</a>
															</s:if>
															<%=uF.showData(appraisalList.get(29), "") %>
									                    	<%if(!flag && !flagPublish && (!finalFlag || !flagProcess)) { %> 
									                    		<a href="javascript: void(0)" onclick="openEditAppraisal('<%=appraisalList.get(0) %>','appraisal','<%=appFreqId%>','<%=fromPage %>')" title="Edit Appraisal"><i class="fa fa-pencil-square-o"></i></a>
																<% if((!finalFlag || !flagProcess)) { %>
																	<%-- <a class="del" title="Delete" style="color:#F02F37;" href="javascript:void();" onclick="deleteAppraisal('<%=id %>','<%=appFreqId %>','<%=fromPage %>')" ><i class="fa fa-trash" aria-hidden="true"></i></a> --%>
																	<%if(fromPage != null && fromPage.equals("AD")) {%>
																		<a title="Delete" href="javascript:void();" onclick="deleteAppraisal('<%=id %>','<%=appFreqId %>','<%=fromPage %>')" ><i class="fa fa-trash" aria-hidden="true"></i></a> <!-- style="color:#F02F37;"  -->
																	<%} else { %>
																		<a title="Delete" href="DeleteAppraisal.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&fromPage=Review" onclick="return confirm('Are you sure you want to delete this appraisal?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>  <!-- style="color:#F02F37;" -->
																	<%} %> 
																<% } %> 
															<% } %>
														</span>
													</h4>
													
									</div>
                                      <!-- created by seema -->
									<div class="box-body" style="padding: 5px; overflow-y: auto;">
										<div class="row row_without_margin">
											<div class="col-lg-12 col-md-12 col-sm-12" style="margin-top:1%;">
												<div style="float: left; width: 100%;">
													<span
														style="float: left; font-size: 12px; line-height: 32px;">
														<span title="Review Type"><%=appraisalList.get(14)%>,</span>
														<span title="Frequency" ><%=appraisalList.get(7)%>,</span>
														<%
												             if (appFreqId != null && !appFreqId.equals("") && !appFreqId.equalsIgnoreCase("")) {
												         %>
												         <span title="Effective Date"><%=appraisalList.get(27)%>,</span>
														 <span title="Due Date"><%=appraisalList.get(28)%>,</span>
														 <%
												              } else {
												         %>
												         <span title="Effective Date"><%=appraisalList.get(17)%>,</span>
														 <span title="Due Date"><%=appraisalList.get(18)%>,</span>
														 <%
												               }
												         %>
												         <span title="Orientation"><%=appraisalList.get(2)%></span>
													</span>
												</div>

												<div style="float: left; width: 100%;">
													<span
														style="float: left; font-size: 12px; line-height: 32px;"><b>Description:&nbsp;&nbsp;
											<!-- ===start parvez date: 22-03-2022=== -->		
													</b><span class="description" id="textlabel" ><%=appraisalList.get(15)%></span></span>
											<!-- ===end parvez date: 22-03-2022=== -->	
												</div>

												<div style="float: left; width: 100%;">
													<span
														style="float: left; font-size: 12px; line-height: 32px;"><b>Instruction:&nbsp;&nbsp;
											<!-- ===start parvez date: 22-03-2022=== -->		
													</b><span class="instruction" id="textlabel"><%=appraisalList.get(16)%></span></span>
											<!-- ===end parvez date: 22-03-2022=== -->	
												</div>

											</div>
										</div>
										
									</div>
									<%
									}
									%>
            							 </div>
            							 
            							 <div class="box box-primary" style="border-top: 3px solid #d2d6de;">
									<%
									if (appraisalList != null && !appraisalList.isEmpty() && appraisalList.size() > 0) {
									%>
									<div class="box-header with-border">  <!-- style="background-color:#d2d6de;" -->
										<!-- created by seema -->
										<h4 class="box-title" style="width: 100%">Reviewers & Reviewee
											<%if(!flag && !flagPublish && (!finalFlag || !flagProcess)) { %>
												<div style="float: right; height:25px; margin-top: -10px; font-size: 12px !important">
													<a href="javascript: void(0);" title="Import Reviewee" onclick="importRevieweePopup('<%=appraisalList.get(0)%>','<%=orientation%>','<%=appFreqId%>');"><i class="fa fa-upload"></i> Reviewee</a> 
													<!-- <a title="Download Import Reviewee Sample File" style="margin-left: 20px;font-size: 12px; line-height: 35px;" href="import/Import_Reviewee.xlsx" target="_blank"><i class="fa fa-download"></i> Sample File</a> -->
													<a title="Download Import Reviewee Sample File" style="margin-left: 20px;font-size: 12px; line-height: 35px;" href="javascript:void(0);" onclick="generateRevieweePanel('<%=appraisalList.get(0)%>')"><i class="fa fa-download"></i> Sample File</a>
												</div>
											<% } %>
										</h4>
									</div>

									<div class="box-body" style="padding: 5px; overflow-y: auto;">
										<div class="row row_without_margin">
											<div class="col-lg-12 col-md-12 col-sm-12">
											    
												<div class="row" style="float: left; width: 100%;">
												<span class="col-lg-2" style="font-size: 12px; line-height: 35px;"><b>Reviewee:</b></span>
													<div class="col-sm-offset-1"><span
														style="float: left; font-size: 12px; line-height: 35px;"><%=appraisalList.get(12)%></span></div>
												</div>
												<div style="float: left; width: 100%;">
													<span style=" font-size: 12px; line-height: 35px;"><b>Appraiser:&nbsp;&nbsp; </b><a href="javascript:void(0);" onclick="getRevieweeAppraisers('<%=appraisalList.get(0)%>', '', '');">Click Here</a></span>
												</div>
												<div style="float: left; width: 100%;">
													<span
														style="float: left; font-size: 12px; line-height: 35px;"><b>Reviewer:&nbsp;&nbsp;&nbsp;&nbsp;</b><%=appraisalList.get(23)%></span>
												</div>
												
											</div>
										</div>
									</div>

									<%
									}
									%>
								</div>
            				</div> <!-- End dattatray -->
            				
            							 <!-- created by seema -->
                                        <%}%>
                                        <!-- created by seema -->
                                        <%if(request.getParameter("tabName") !=null && request.getParameter("tabName").equals("reviewforms")) { %>
                                        <!-- created by seema -->
							            <%if(!flag && !flagPublish && (!finalFlag || !flagProcess)) { %>
							              <!-- created by seema -->
							              <h4>Sections (<%=mainLevelList.size() %>)
							         		<span style="float: right;">
												<a title="Import Section Data" href="javascript: void(0);" onclick="importSectionSubsectionPopup('<%=appraisalList.get(0)%>','<%=orientation%>','<%=appFreqId%>');"><i class="fa fa-upload"></i> Section Data</a>
												<a title="Download Import Section Sample File" style="margin-left: 20px;" href="import/Import_Review_Section.xlsx" target="_blank"><i class="fa fa-download"></i> Sample File</a>
											</span>
											<!-- created by seema -->
										<% } %></h4>
										
										<div style=" max-height: 580px !important; overflow-y: hidden;" id="sectionReviewforms"><!-- Start Dattatray  -->
										<%
										int z=0;int newsysno=1;
										for (int a = 0; mainLevelList != null && a < mainLevelList.size(); a++) {
											double subsectionTotWeightage=0;
											List<String> maininnerList = mainLevelList.get(a);
											List<List<String>> outerList1 = hmSystemLevelMp.get(maininnerList.get(0));
											if(outerList1 != null && !outerList1.isEmpty()){
												newsysno = outerList1.size()+1; 
												for(int i=0;i<outerList1.size();i++){
													List<String> subInnerlist = outerList1.get(i);
													subsectionTotWeightage += uF.parseToDouble(subInnerlist.get(7));
												}
											} 
											%>
		 								            <div class="box box-primary collapsed-box" style="border-top: 3px solid #d2d6de;">
										               <div class="box-header with-border">
										                    <h3 class="box-title"><%=a+1%>)&nbsp;<%=maininnerList.get(1)%> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										                    	<div style="font-size:13px;padding-bottom: 5px;margin-top: 5px;">
											                    	<%-- <span><strong>Answer Type :-&nbsp;&nbsp;</strong> <%=uF.showData(maininnerList.get(1),"0")%>%</span>&nbsp;&nbsp;&nbsp;&nbsp; --%>
																<!-- ===start parvez date: 22-02-2023=== -->	
																	<%-- <span><strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(maininnerList.get(5),"0")%>%</span>&nbsp;&nbsp;&nbsp;&nbsp; --%>
																	<span><strong>Weightage :&nbsp;&nbsp;</strong> <%=uF.showData(maininnerList.get(5),"0")%>%</span>&nbsp;&nbsp;&nbsp;&nbsp;
																<!-- ===end parvez date: 22-02-2023=== -->	
																	<%if(!flag && !flagPublish && (!finalFlag || !flagProcess)){ %>
																	 	<span> 	
																			<%-- <a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=maininnerList.get(0) %>','<%=a+1%>','section','<%=orientation %>','','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage %>')" title="Edit Section"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> --%>
																			<a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=maininnerList.get(0) %>','<%=a+1%>','section','<%=orientation %>','','<%=maininnerList.get(1) %>','<%=sectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage %>')" title="Edit Section"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																			<% if(uF.parseToInt(hmSections.get(maininnerList.get(0))) == 0) { %>
																					<% if(fromPage != null && fromPage.equals("AD")) { %>
																						<a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','','Level','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																					<% } else { %>
																						<a style="color:#F02F37;" title="Delete" href="DeleteAppraisalLevelAndSystem.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&type=Level" onclick="return confirm('Are you sure you want to delete this section?')" ><i class="fa fa-trash" aria-hidden="true"></i></a></span>
																					<% } %>
																			<% } %>
																	<% } %>
																	<span style="margin-right: 10px; font-weight: normal; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(maininnerList.get(6),"")%>&nbsp;<%=uF.showData(maininnerList.get(7),"")%></span>
																</div>
																<span style="width: 70%;font-size: 13px;padding-top: 5px;"><%=uF.showData(maininnerList.get(2), "")%></span>
															</h3>
										                    <div class="box-tools pull-right">
										                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
										                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
										                    </div>
										                </div>
										                <div class="box-body" style="padding: 5px; overflow-y: auto;">
										                    <div style="margin-bottom:10px;">
																<div class="content1">
																	<%for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
																			List<String> innerList1 = outerList1.get(i);
																			if (uF.parseToInt(innerList1.get(3)) == 2) {
																				List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
																				Map<String, List<List<String>>> scoreMp = list.get(0);
																				List<List<String>> queList = scoreMp.get(innerList1.get(0));
																				double totothersysWeightage = 0;
																				//System.out.println("ApSem.jsp/329--totothersysWeightage="+totothersysWeightage);
																				int newquecnt = 0;
																				String otherQueAnstype="";
																				String otherQuetype ="",sectionattribute="";
																	%>
																				<ul id="" class="ul_class1">
																				<li><div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
																					<div style="text-align:left; height: 35px;">
																					  <%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) { %>
																							&nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
																						<!-- ===start parvez date: 22-02-2023 Note removed: - after : === -->	
																							<span style="float: right; margin-right: 333px;"><strong>Weightage :&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"0")%>%</span>
																						<!-- ===end parvez date: 22-02-2023=== -->	
																							<div style="width: 70%">
																								<ul id="" class="ul_subsection">
																									<li>
																										<div style=" width: 100%; float: left;"><%=innerList1.get(4)%></div>
																										<div style="width: 100%; float: left;"><%=innerList1.get(5)%></div>
																									</li>
																								</ul>
																							</div>
																						<% } %>
																						<div style="width: 100%; float: left;">
																							<%if(!flag && !flagPublish && (!finalFlag || !flagProcess)){ %>
																							<span style="float: right;">
																							<a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage %>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																								<% if(uF.parseToInt(hmSubSections.get(innerList1.get(0))) == 0) { %>
																									<% if(fromPage != null && fromPage.equals("AD")) { %>
																											<a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																									<% } else { %>
																										    <a title="Delete" href="DeleteAppraisalLevelAndSystem.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																									<% } %>
																								<% } %>
																							</span>
																							<%} %>
																							<span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
																						</div>
																					</div>
																					<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 99.9%;">
																						<table class="table" style="width: 100%; float: left;">
																							<tr><td><b>Question</b></td>
																								<td><b>Assessment_System</b>
																								<td><b>Weightage</b></td>
																							</tr> 
																							<%
																							totothersysWeightage = 0;	
																							List<List<String>> goalList = scoreMp.get(innerList1.get(0));
																								newquecnt= goalList != null ? goalList.size()+1 : 1 ;
																								for (int k = 0; goalList != null && k < goalList.size(); k++) {
																									List<String> goalinnerList = goalList.get(k);
																									totothersysWeightage += uF.parseToDouble(goalinnerList.get(1));
																								}
																								String anstype="Not Defined";
																								String anstypeid="0";
																								for (int k = 0; goalList != null && k < goalList.size(); k++) {
																									List<String> goalinnerList = goalList.get(k);
																									z++;
																									otherQuetype = goalinnerList.get(3);
																									otherQueAnstype = goalinnerList.get(2);
																									sectionattribute = goalinnerList.get(7);
																									if(uF.parseToInt(goalinnerList.get(2)) > 0) {
																										anstype = hmAnswerType.get(goalinnerList.get(2)) ;
																										anstypeid = goalinnerList.get(2);
																									}
																							%>
																									<tr><td><span style="float: left;"><%=a+1%>.<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) {%>
																										<%=i+1%>.<% } %><%=k+1%>)&nbsp;<%=goalinnerList.get(0)%></span>
																										<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																										<span style="float: left; margin-left: 10px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue1('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=goalinnerList.get(5)%>','quest','OTHEReditquedivOfQ<%=a %>_<%=i %>','<%=goalinnerList.get(3)%>','<%=a+1%>.<%=i+1%>.<%=k+1%>)','<%=a %>_<%=i %>e','<%=goalinnerList.get(2) %>','<%=totothersysWeightage %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>'); " title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																											<% if(k>0 && uF.parseToInt(hmQuestions.get(goalinnerList.get(5))) == 0) { %>
																												<%if(fromPage != null && fromPage.equals("AD")) { %>
																													<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=goalinnerList.get(5) %>','Q','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																												<%} else {%>
																													<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=goalinnerList.get(5) %>&type=Q" onclick="return confirm('Are you sure you want to delete this Question?')" ><i class="fa fa-trash" aria-hidden="true"></i></a></span>
																												<%} %>
																											<%} %>
																										<%} %>
																										</td>	
																										<td><%=anstype%></td>						
																										<td ><%=goalinnerList.get(1)%>%</td> 
																									</tr>
																									<%}%>
																									<tr><td colspan="3">
																									<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																										<span> <a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="openOtherSystemNewQue('<%=a %>_<%=i %>','<%=totothersysWeightage %>');changeNewQuestionTypeOther('<%=anstypeid %>','answerType<%=a %>_<%=i %>','answerType1<%=a %>_<%=i %>','answerType2<%=a %>_<%=i %>','0')" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																									<%} %>
																									</td></tr>
																								</table>
																								
																							</div>
																						</div></li></ul>
																				 <div style="display: none"><s:form action="" id="frmothersystem1" theme="simple"></s:form></div> 	
		 																		<form action="AddQuestionSystemOther" id="frmothersystem<%=a %>_<%=i %>" method="POST" > 
																					<div id="OTHERnewquedivOfQ<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
																						<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																						<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																						<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																						<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																						<input type="hidden" name="UID" id="UID" value="<%=innerList1.get(0)%>" />
																						<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																						<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																						<input type="hidden" name="type1" id="type1" value="quest" />
																						<ul class="ul_class">
																							<li><table class="table table_no_border" width="100%">
																									<tr><th><%=a+1%>.<%=i+1%>.<%=newquecnt %>)</th>
																										<th width="17%">Add Question<sup>*</sup>
																											<input type="hidden" name="othrqueanstype" id="othrqueanstype<%=a %>_<%=i %>" value="<%=otherQueAnstype%>"/>
																											<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>" value="<%=sectionattribute %>"/>
																										</th>
																										<td colspan="3"><span id="newquespan<%=a %>_<%=i %>" style="float: left;">
																												<input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>" value="0"/>
																												<textarea rows="2" name="question" id="question<%=a %>_<%=i %>" class="validateRequired" style="width: 330px;"></textarea>
																											</span>
																											<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																												<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>','hideweightage<%=a %>_<%=i %>')" onkeypress="return isNumberKey(event)"/>
																												<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>" value="100" /></span>
																											<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>','addQue');" > +Q </a></span>
																											<%if(otherQuetype.equals("With Short Description")){ %>
																												<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" onclick="openOtheQueShortD('<%=a %>_<%=i %>')" > D </a></span>
																											<%}else{ %>
																												<span style="float: left; margin-left: 10px;"> D </span>
																											<%} %>
																											<span id="checkboxspan<%=a %>_<%=i %>" style="float: left; margin-left: 10px;">
																												<input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>')"/>
																												<input type="hidden" id="status<%=a %>_<%=i %>" name="status" value="0"/></span>
																												<input type="hidden" name="questiontypename" value="0" />
																									</td>
																							   </tr>
																								<tr id="shortdescTr<%=a %>_<%=i %>" style="display: none;"><th></th><th>Short Description</th>
																									<td colspan="3"><input type="hidden" name="hideotherSD" id="hideotherSD<%=a %>_<%=i %>" value="f"/>
																									<input type="text" name="otherSDescription" id="otherSDescription" style="width: 450px;" /></td></tr>
																						
																								<%if(otherQueAnstype == null || otherQueAnstype.equals("") || otherQueAnstype.equals("0")){ %>
																									<tr><th>&nbsp;</th><th>Select Answer Type</th>
																										<td width="280px"><select name="ansType" id="ansType" onchange="showAnswerTypeDiv(this.value);changeNewQuestionType1(this.value,'answerType<%=a %>_<%=i %>','answerType1<%=a %>_<%=i %>','answerType2<%=a %>_<%=i %>','0')"><%=request.getAttribute("anstype") %></select></td>
																										<td colspan="2"><div id="anstypedivAdd">
																											<div id="anstype9">
																												a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"/>
																												b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"/><br />
																												c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"/> 
																												d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"/><br />
																											</div></div>
																										</td>
																									</tr>
																								<%} %>
																							
																									<tr id="answerType<%=a %>_<%=i %>" style="display: <%if(otherQueAnstype == null || otherQueAnstype.equals("") || otherQueAnstype.equals("0")){ %>table-row; <%}else{%>none; <%}%>">
																										<th>&nbsp;</th><th>&nbsp;</th>
																										<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																										<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" /></td>
																									</tr>
																									<tr id="answerType1<%=a %>_<%=i %>" style="display: <%if(otherQueAnstype == null || otherQueAnstype.equals("") || otherQueAnstype.equals("0")){ %> table-row; <%}else{%> none; <%}%>">
																										<th>&nbsp;</th><th>&nbsp;</th>
																										<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																										<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																									</tr>
																									<tr id="answerType2<%=a %>_<%=i %>" style="display: none;">
																										<th>&nbsp;</th><th>&nbsp;</th>
																										<td>&nbsp;</td>
																										<td colspan="2">&nbsp;</td>
																									</tr>
																							   </table>
																							</li></ul></div> 
																					<div id="OTHERsavebtndivOfQ<%=a %>_<%=i %>" style="display: none" align="center">
																						<%if(fromPage != null && fromPage.equals("AD")) { %> 
																							<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="addQuestionSystemOther('frmothersystem<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																						<% } else { %><input type="submit" value="Save" class="btn btn-primary" name="submit" /><%} %>
																						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('OTHERsavebtndivOfQ<%=a %>_<%=i %>','OTHERnewquedivOfQ<%=a %>_<%=i %>');"/>
																					</div>
																				</form>
																				<div id="OTHEReditquedivOfQ<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none"></div> 
																		<%
																			} else if (uF.parseToInt(innerList1.get(3)) == 1) {
																						if (uF.parseToInt(innerList1.get(2)) == 1) {
																							String CGOMScoreUID="";
																							String CGOMGoalUID="";
																							String CGOMObjectiveUID="";
																							String CGOMMeasureUID="";
																							String CGOMQueUID="";
																							double CGOMtotScoreWeight = 0;
																							String queAnstype="",sectionattribute="";
																							String newscorecnt = null;
																							String newgoalcnt = null;
																							String newobjcnt = null;
																							String newmeasurecnt = null;
																							String newquecnt = null;
																							List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
																							Map<String, List<List<String>>> scoreMp = list.get(0);
																							Map<String, List<List<String>>> measureMp = list.get(1);
																							Map<String, List<List<String>>> questionMp = list.get(2);
																							Map<String, List<List<String>>> GoalMp = list.get(3);
																							Map<String, List<List<String>>> objectiveMp = list.get(4);
																							List<List<String>> scoreList1 = scoreMp.get(innerList1.get(0));
																		%>
																							<ul id="" class="ul_class1">
																							<li><div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
																							<div style="text-align:left; height: 35px;">
																								<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) { %>
																									&nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1) %>
																								<!-- ===start parvez date: 22-02-2023 Note removed: - after :=== -->	
																									<span style="float: right; margin-right: 333px;"><strong>Weightage : &nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"0") %>%</span>
																								<!-- ===end parvez date: 22-02-2023=== -->	
																									<div style="width: 70%">
																										<ul class="ul_subsection">
																											<li><div style=" width: 100%; float: left;"><%=innerList1.get(4)%></div>
																											<div style="width: 100%; float: left;"><%=innerList1.get(5)%></div></li>
																										</ul>
																									</div>
																								<%} %>
																								<div style="width: 100%; float: left;">
																									<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																										<span style="float: right;"><a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage %>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																											<% if(hmSubSections != null && innerList1.get(0) != null && uF.parseToInt(hmSubSections.get(innerList1.get(0))) == 0) { %>
																												<% if(fromPage != null && fromPage.equals("AD")) { %>
																													<a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																												<% } else { %>
																													 <a title="Delete" href="DeleteAppraisalLevelAndSystem.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																												<% } %>
																											<% } %>
																										</span>
																									<%} %>
																									<span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
																								</div>
																							</div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;"><b>Competencies</b></div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.6%;  text-align: center;"><b>Weightage</b></div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;"><b>Goal </b></div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.6%;  text-align: center;"><b>Weightage</b></div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;"><b>Objective </b></div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.6%;  text-align: center;"><b>Weightage</b></div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 10%;  text-align: center;"><b>Measure</b></div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.6%;  text-align: center;"><b>Weightage</b></div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 17%;  text-align: center;"><b>Question</b></div>
																							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 6.5%;  text-align: center;"><b>Weightage</b></div>
																							<%
																							CGOMtotScoreWeight = 0;
																							List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
																							int intscorecnt = scoreList != null ? scoreList.size()+1 : 1 ;
																							newscorecnt = intscorecnt+"";
																							for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
																								List<String> innerList = scoreList.get(j);
																								CGOMtotScoreWeight += uF.parseToDouble(innerList.get(2));
																							}
																								CGOMScoreUID = innerList1.get(0);
																								for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
																									List<String> innerList = scoreList.get(j);
																									z++;
																									queAnstype = innerList.get(4);
																									sectionattribute = innerList.get(5);
																							%>
																									<div style="overflow: hidden; float: left; width: 99.9%;">
																										<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.1%;"><p>
																											<span style="float: left; margin-left: 4px;"><%=innerList.get(1)%></span>
																											<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																												<span style="float: left; margin-left: 5px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=innerList.get(0)%>','score','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>se','<%=innerList.get(4) %>','<%=CGOMtotScoreWeight %>','<%=j+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																												<% if(j>0 && hmQuestions != null && innerList.get(0)!= null && uF.parseToInt(hmQuestions.get(innerList.get(0))) == 0) { %>
																													<%if(fromPage != null && fromPage.equals("AD")) { %>
																														<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=innerList.get(0) %>','C','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																													<%} else {%>
																														<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=innerList.get(0) %>&type=C" onclick="return confirm('Are you sure you want to delete this Compentency?')" ><i class="fa fa-trash" aria-hidden="true"></i></a></span>
																													<%} %>
																												<%} %>
																											 <%} %></p>					
																										</div>
																										<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 6%; text-align: right;"><p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p></div>
																										<div style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 79.6%;">
																											<%
																												double CGOMtotGoalWeight = 0;
																												List<List<String>> goalList = GoalMp.get(innerList.get(0));
																												int intgoalcnt = goalList != null ? goalList.size()+1 : 1 ;
																												newgoalcnt = (j+1)+"."+intgoalcnt+"";
																												for (int k = 0; goalList != null && k < goalList.size(); k++) {
																													List<String> goalinnerList = goalList.get(k);
																													CGOMtotGoalWeight += uF.parseToDouble(goalinnerList.get(2));
																												}
																												    CGOMGoalUID =  innerList.get(0);
																													for (int k = 0; goalList != null && k < goalList.size(); k++) {
																														List<String> goalinnerList = goalList.get(k);
																														z++;
																											%>
																														<div style="overflow: hidden; float: left; width: 100%;">
																															<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 17.8%;"><p>
																																	<span style="float: left; margin-left: 4px;"><%=goalinnerList.get(1)%></span>
																																	<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																	<span style="float: left; margin-left: 5px;">
																																		<a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=goalinnerList.get(0)%>','goal','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>ge','<%=innerList.get(4) %>','<%=CGOMtotGoalWeight %>','<%=j+1 %>.<%=k+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																																			<% if(k>0 && hmQuestions!=null && goalinnerList.get(0) != null &&  uF.parseToInt(hmQuestions.get(goalinnerList.get(0))) == 0) { %>
																																				<%if(fromPage != null && fromPage.equals("AD")) { %>
																																					<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=goalinnerList.get(0) %>','G','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																				<%} else {%>
																																					<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=goalinnerList.get(0)%>&type=G" onclick="return confirm('Are you sure you want to delete this Goal?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																				<%} %>
																																			<% } %>
																																	</span>	
																																	<%} %></p>
																															</div>
																															<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 7.3%; text-align: right;"><p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p></div>
																															<div style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 74.7%;">
																																<%
																																	double CGOMtotObjectiveWeight = 0;
																																	List<List<String>> objectiveList = objectiveMp.get(goalinnerList.get(0));
																																	int intobjcnt = objectiveList != null ? objectiveList.size()+1 : 1 ;
																																	newobjcnt = (j+1)+"."+(k+1)+"."+intobjcnt+"";
																																	for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
																																		List<String> objectivelinnerList = objectiveList.get(l);
																																		CGOMtotObjectiveWeight += uF.parseToDouble(objectivelinnerList.get(2));
																																	}
																																	CGOMObjectiveUID =  goalinnerList.get(0);
																																		for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
																																			List<String> objectivelinnerList = objectiveList.get(l);
																																			z++;
																																%>
																																			<div style="overflow: hidden; float: left; width: 100%;">
																																				<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24.6%;"> <p>
																																						<span style="float: left; margin-left: 4px;"><%=objectivelinnerList.get(1)%></span>
																																						<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																						<span style="float: left; margin-left: 5px;">
																																						<a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=objectivelinnerList.get(0)%>','objective','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>oe','<%=innerList.get(4) %>','<%=CGOMtotObjectiveWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																																							<% if(l>0 && hmQuestions != null && objectivelinnerList.get(0) != null && uF.parseToInt(hmQuestions.get(objectivelinnerList.get(0))) == 0) { %>
																																								<%if(fromPage != null && fromPage.equals("AD")) { %>
																																									<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=objectivelinnerList.get(0) %>','O','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																								<%} else {%>
																																									<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=objectivelinnerList.get(0)%>&type=O" onclick="return confirm('Are you sure you want to delete this Objective?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																								<%} %>
																																							<%} %>	
																																						</span>  
																																						<%} %></p>										
																																				</div>
																																				<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 10.2%; text-align: right;"><p style="margin: 0px 10px 0px 0px;"><%=objectivelinnerList.get(2)%>%</p></div>
																																				<div style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 64.7%;">
																																					<%
																																						double CGOMtotMeasureWeight = 0;
																																						List<List<String>> measureList = measureMp.get(objectivelinnerList.get(0));
																																						int intmeasurecnt = measureList != null ? measureList.size()+1 : 1 ;
																																						newmeasurecnt = (j+1)+"."+(k+1)+"."+(l+1)+"."+intmeasurecnt+"";
																																						for (int m = 0; measureList != null && m < measureList.size(); m++) {
																																							List<String> measureinnerList = measureList.get(m);
																																							CGOMtotMeasureWeight += uF.parseToDouble(measureinnerList.get(2));
																																						}
																																							CGOMMeasureUID =  objectivelinnerList.get(0);
																																							for (int m = 0; measureList != null && m < measureList.size(); m++) {
																																								List<String> measureinnerList = measureList.get(m);
																																								z++;
																																					%>
																																								<div style="overflow: hidden; float: left; width: 100%;">
																																									<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24%;"><p>
																																											<span style="float: left; margin-left: 4px;"><%=measureinnerList.get(1)%></span>
																																											<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																											<span style="float: left; margin-left: 5px;">
																																											<a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=measureinnerList.get(0)%>','measure','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>me','<%=innerList.get(4) %>','<%=CGOMtotMeasureWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>.<%=m+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																																												<% if(m>0 && hmQuestions != null && measureinnerList.get(0) != null && uF.parseToInt(hmQuestions.get(measureinnerList.get(0))) == 0) { %>
																																													<%if(fromPage != null && fromPage.equals("AD")) { %>
																																														<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=measureinnerList.get(0) %>','M','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																													<%} else {%>
																																														<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=measureinnerList.get(0)%>&type=M" onclick="return confirm('Are you sure you want to delete this Measure?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																													<%} %>
																																													
																																												<% } %>
																																											</span>
																																											<%} %></p>
																																									</div>
																																									<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.5%; text-align: right;">
																																										<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p></div>
																																									<div style="overflow: hidden; float: left; width: 60.6%;">
																																										<%
																																											double CGOMtotQueWeight = 0;
																																											List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
																																											int intquecnt = questionList != null ? questionList.size()+1 : 1 ;
																																											newquecnt = (j+1)+"."+(k+1)+"."+(l+1)+"."+(m+1)+"."+intquecnt+"";
																																											for (int n = 0; questionList != null && n < questionList.size(); n++) {
																																												List<String> question1List = questionList.get(n);
																																												CGOMtotQueWeight += uF.parseToDouble(question1List.get(1));
																																											}
																																												CGOMQueUID =  measureinnerList.get(0);
																																												for (int n = 0; questionList != null && n < questionList.size(); n++) {
																																													List<String> question1List = questionList.get(n);
																																													z++;
																																										%>
																																													<div style="overflow: hidden; float: left; width: 100%;">
																																														<div style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 73%;"><p>
																																												 				<span style="float: left; margin-left: 4px;" id="textlabel"><%=question1List.get(0)%></span>
																																												 				<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																												 					<span style="float: left; margin-left: 10px;">
																																												 						<a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=question1List.get(3)%>','quest','CGOMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>qe','<%=innerList.get(4) %>','<%=CGOMtotQueWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>.<%=m+1 %>.<%=n+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																																																		<% if(n>0 && hmQuestions != null && question1List.get(0) != null && uF.parseToInt(hmQuestions.get(question1List.get(0))) == 0) { %>
																																																			<%if(fromPage != null && fromPage.equals("AD")) { %>
																																																				<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=question1List.get(3) %>','Q','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																																			<%} else {%>
																																																				<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=question1List.get(3)%>&type=Q" onclick="return confirm('Are you sure you want to delete this Question?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																																			<%} %>
																																																		<% } %>
																																																	</span>	
																																												 				<%} %></p>
																																														</div>
																																													<div style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 26%; text-align: right;">
																																														<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p></div>
																																											</div>
																																										<% }%>
																																									<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																											<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'quest','<%=CGOMtotQueWeight %>','<%=CGOMQueUID %>','<%=newquecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>q','answerType1<%=a %>_<%=i %>q','answerType2<%=a %>_<%=i %>q','0')" title="Add Question"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																																									  <%} %>
																																								</div>
																																							</div>
																																						<% } %>
																																					<% if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																						<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'measure','<%=CGOMtotMeasureWeight %>','<%=CGOMMeasureUID %>','<%=newmeasurecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>m','answerType1<%=a %>_<%=i %>m','answerType2<%=a %>_<%=i %>m','0')" title="Add Measure"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																																					<%} %>
																																				</div>
																																			</div>
																																		<% } %>
																																		<% if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																			<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'objective','<%=CGOMtotObjectiveWeight %>','<%=CGOMObjectiveUID %>','<%=newobjcnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>o','answerType1<%=a %>_<%=i %>o','answerType2<%=a %>_<%=i %>o','0')" title="Add Objective"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																																		<%} %>
																																	</div>
																																</div>
						                                                                                                    <% } %>
																																<% if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																	<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'goal','<%=CGOMtotGoalWeight %>','<%=CGOMGoalUID %>','<%=newgoalcnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>g','answerType1<%=a %>_<%=i %>g','answerType2<%=a %>_<%=i %>g','0')" title="Add Goal"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																																<%} %>					
																															</div>
																														</div>				
																													<% } %>
																													<% if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %><br>
																														<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCGOMnewquediv('<%=a %>_<%=i %>', 'score','<%=CGOMtotScoreWeight %>','<%=CGOMScoreUID %>','<%=newscorecnt %>');changeNewQuestionTypeOther('<%=queAnstype%>','answerType<%=a %>_<%=i %>s','answerType1<%=a %>_<%=i %>s','answerType2<%=a %>_<%=i %>s','0')" title="Add Competency"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																													<%} %>
																												</div>
																											</li>
																										</ul>
																								<!-- this div is only created for some prblm -->			
																										<div style="display: none"><s:form action="" id="frmCGOMsystemOfS1" theme="simple"></s:form></div>
																										<form action="AddQuestionSystemOther" id="frmCGOMsystemOfS<%=a %>_<%=i %>" method="POST" > 	
																											<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																											<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																											<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																											<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																											<input type="hidden" name="UID20" id="UID20<%=a %>_<%=i %>" value="" />
																											<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																											<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																											<input type="hidden" name="type20" id="type20<%=a %>_<%=i %>" value="" />
																											<div id="CGOMscorenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
																											<ul class="ul_class">
																												<li> <table class="table table_no_border" style="width: 100%;">
																														<tr><th width="15%"><span id="CGOMScoreCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																															<%-- <td>Competency<span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGOMsavebtndivOfS<%=a %>_<%=i %>','CGOMscorenewquediv<%=a %>_<%=i %>')"/></span></td> --%>
																															<td>Competency<span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGOMsavebtndivOfS<%=a %>_<%=i %>','CGOMscorenewquediv<%=a %>_<%=i %>')"></i></span></td>
																															
																														</tr>
																														<tr><th >Section Name<sup>*</sup></th><td><input type="text" name="scoreSectionName" id="scoreSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																														<tr><th>Description</th><td><input type="text" name="scoreCardDescription" style="width: 450px;"/></td></tr>
																														<tr><th>Weightage %<sup>*</sup></th>
																															<td><input type="text" name="scoreCardWeightage" id="scoreCardWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'scoreCardWeightage<%=a %>_<%=i %>s','hidescoreCardWeightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																																<input type="hidden" name="hidescoreCardWeightage" id="hidescoreCardWeightage<%=a %>_<%=i %>s" value="100"/></td>
																														</tr>
																													</table>
																												</li>
																												
																												<li><ul class="ul_class">
																														<li> <table class="table" style="width: 100%;">
																																<tr><th width="15%"><span id="CGOMGoalCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Goals</td></tr>
																																<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																																<tr><th>Description</th><td><input type="text" name="goalDescription" style="width: 450px;"/></td></tr>
																																<tr><th>Weightage %<sup>*</sup></th>
																																	<td><input type="text" name="goalWeightage" id="goalWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'goalWeightage<%=a %>_<%=i %>s','hidegoalWeightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																																	<input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=a %>_<%=i %>s" value="100"/></td></tr>
																															</table>
																														</li>
																														
																														<li><ul class="ul_class">
																																<li><table class="table" style="width: 100%;">
																																		<tr><th width="15%"><span id="CGOMObjCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Objective </td></tr>
																																		<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="objectiveSectionName" id="objectiveSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																																		<tr><th>Description</th><td><input type="text" name="objectiveDescription" style="width: 450px;"/></td></tr>
																																		<tr><th>Weightage %<sup>*</sup></th>
																																			<td><input type="text" name="objectiveWeightage" id="objectiveWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'objectiveWeightage<%=a %>_<%=i %>s','hideobjectiveWeightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																																			<input type="hidden" name="hideobjectiveWeightage" id="hideobjectiveWeightage<%=a %>_<%=i %>s" value="100"/></td></tr>
																																	</table>
																																</li>
																																<li><ul class="ul_class">
																																		<li> <table class="table" style="width: 100%;">
																																				<tr><th width="15%"><span id="CGOMMeasureCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Measures <input type="hidden" name="measureID"/></td></tr>
																																				<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																																				<tr><th>Description</th><td><input type="text" name="measuresDescription" style="width: 450px;"/></td></tr>
																																				<tr><th>Weightage %<sup>*</sup></th><td>
																																					<input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>s','hidemeasureWeightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																																					<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>s" value="100"/></td></tr>
																																			</table></li>
																																		<li><ul><li> <table class="table" width="100%">
																																					<tr><th><span id="CGOMQueCntS<%=a %>_<%=i %>" style="float: left;"></span></th><th width="17%">Add Question<sup>*</sup>
																																						<input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
																																						<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/>
																																					</th>
																																					<td colspan="3"><span id="newquespan<%=a %>_<%=i %>s" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>s" value="0"/>
																																							<textarea rows="2" name="question" id="question<%=a %>_<%=i %>s" class="validateRequired" style="width: 330px;"></textarea></span>
																																						<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																																							<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>s','hideweightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																																							<input type="hidden" style="width:35px !important;" name="hideweightage" id="hideweightage<%=a %>_<%=i %>s" value="100"/></span>
																																						<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>s','addQue');" > +Q </a></span>
																																						<span id="checkboxspan<%=a %>_<%=i %>s" style="float: left; margin-left: 10px;">
																																							<input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>s" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>s')"/>
																																							<input type="hidden" id="status<%=a %>_<%=i %>s" name="status" value="0"/>
																																						</span>
																																						<input type="hidden" name="questiontypename" value="0" /></td>
																																					</tr>
																																					<tr id="answerType<%=a %>_<%=i %>s" style="display: none">
																																						<th>&nbsp;</th><th>&nbsp;</th><td>a)<input type="text" name="optiona" class="validateRequired form-control"/><input type="checkbox" value="a" name="correct0" /></td>
																																						<td colspan="2">b)<input type="text" name="optionb" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="b" /></td>
																																					</tr>
																																					<tr id="answerType1<%=a %>_<%=i %>s" style="display: none"><th>&nbsp;</th><th>&nbsp;</th><td>c)<input type="text" name="optionc" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="c" /></td>
																																						<td colspan="2">d)<input type="text" name="optiond" class="validateRequired form-control" /><input type="checkbox" name="correct0" value="d" /></td>
																																					</tr>
																																					<tr id="answerType2<%=a %>_<%=i %>s" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																																						<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																																					</tr>	
																																				</table> 
																																				</li></ul></li>
																																	</ul></li></ul></li></ul></li></ul></div>
																										   <div id="CGOMsavebtndivOfS<%=a %>_<%=i %>" style="display: none" align="center">
																											<%if(fromPage != null && fromPage.equals("AD")) { %> 
																												<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="addQuestionSystemOther('frmCGOMsystemOfS<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																											<% } else { %><input type="submit" value="Save" class="btn btn-primary" name="submit"/><%} %>
																											<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfS<%=a %>_<%=i %>','CGOMscorenewquediv<%=a %>_<%=i %>')"/>
																									   </div></form>							
																								<form action="AddQuestionSystemOther" id="frmCGOMsystemOfG<%=a %>_<%=i %>" method="POST" > 
																									<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																									<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																									<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																									<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																									<input type="hidden" name="UID21" id="UID21<%=a %>_<%=i %>" value="" />
																									<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																									<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																									<input type="hidden" name="type21" id="type21<%=a %>_<%=i %>" value="" />
												<div id="CGOMgoalnewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
													<ul class="ul_class">
														<li> <table class="table table_no_border" style="width: 100%;">
																<%-- <tr><th width="15%"><span id="CGOMGoalCntG<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Goals<span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;"
																	 src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGOMsavebtndivOfG<%=a %>_<%=i %>','CGOMgoalnewquediv<%=a %>_<%=i %>')"/></span></td>
																</tr> --%>
																<tr><th width="15%"><span id="CGOMGoalCntG<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Goals<span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" style="padding: 5px 5px 0pt;" ></i></span></td>
																</tr>
																<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																<tr><th>Description</th><td><input type="text" name="goalDescription" style="width: 450px;"/></td></tr>
																<tr><th>Weightage %<sup>*</sup></th><td>
																	<input type="text" name="goalWeightage" id="goalWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'goalWeightage<%=a %>_<%=i %>g','hidegoalWeightage<%=a %>_<%=i %>g')" onkeypress="return isNumberKey(event)"/>
																	<input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=a %>_<%=i %>g" value="100"/></td>
																</tr>
															</table>
														</li>
														<li><ul class="ul_class">
																<li> <table class="table" style="width: 100%;">
																		<tr><th width="15%"><span id="CGOMObjCntG<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Objective </td></tr>
																		<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="objectiveSectionName" id="objectiveSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																		<tr><th>Description</th><td><input type="text" name="objectiveDescription" style="width: 450px;"/></td></tr>
																		<tr><th>Weightage %<sup>*</sup></th><td>
																			<input type="text" name="objectiveWeightage" id="objectiveWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'objectiveWeightage<%=a %>_<%=i %>g','hideobjectiveWeightage<%=a %>_<%=i %>g')" onkeypress="return isNumberKey(event)"/>
																			<input type="hidden" name="hideobjectiveWeightage" id="hideobjectiveWeightage<%=a %>_<%=i %>g" value="100"/></td></tr>
																	</table>
																</li>
																<li><ul class="ul_class">
																		<li> <table class="table" style="width: 100%;">
																				<tr><th width="15%"><span id="CGOMMeasureCntG<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Measures <input type="hidden" name="measureID"/></td></tr>
																				<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																				<tr><th>Description</th><td><input type="text" name="measuresDescription" style="width: 450px;"/></td></tr>
																				<tr><th>Weightage %<sup>*</sup></th><td>
																					<input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>g','hidemeasureWeightage<%=a %>_<%=i %>g')" onkeypress="return isNumberKey(event)"/>
																					<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>g" value="100"/></td></tr>
																			</table>
																		</li>
																		<li><ul><li><table class="table" width="100%">
																					<tr><th><span id="CGOMQueCntG<%=a %>_<%=i %>" style="float: left;"></span></th>
																					<th width="17%">Add Question<sup>*</sup><input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>g" value="<%=queAnstype%>"/>
																						<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>g" value="<%=sectionattribute %>"/></th>
																					<td colspan="3"><span id="newquespan<%=a %>_<%=i %>g" style="float: left;">
																							<input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>g" value="0"/><textarea rows="2" name="question" id="question<%=a %>_<%=i %>g" class="validateRequired" style="width: 330px;"></textarea></span>
																						<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																							<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>g','hideweightage<%=a %>_<%=i %>g')" onkeypress="return isNumberKey(event)"/>
																							<input type="hidden" style="width:35px !important;" name="hideweightage" id="hideweightage<%=a %>_<%=i %>g" value="100"/></span>
																						<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>g','addQue');" > +Q </a></span>
																						<span id="checkboxspan<%=a %>_<%=i %>g" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>g" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>g')"/>
																							<input type="hidden" id="status<%=a %>_<%=i %>g" name="status" value="0"/></span>
																						<input type="hidden" name="questiontypename" value="0" /></td>
																					</tr>
																					<tr id="answerType<%=a %>_<%=i %>g" style="display: none"><th>&nbsp;</th><th>&nbsp;</th>
																						<td>a)<input type="text" name="optiona" class="validateRequired form-control"/><input type="checkbox" value="a" name="correct0" /></td><td colspan="2">b)<input type="text" name="optionb" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="b" /></td>
																					</tr>
																					<tr id="answerType1<%=a %>_<%=i %>g" style="display: none"><th>&nbsp;</th><th>&nbsp;</th><td>c)<input type="text" name="optionc" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="c" /></td>
																						<td colspan="2">d)<input type="text" name="optiond" class="validateRequired form-control" /><input type="checkbox" name="correct0" value="d" /></td>
																					</tr>
																					<tr id="answerType2<%=a %>_<%=i %>g" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																						<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																					</tr>
																				</table> 
																			</li></ul></li></ul></li></ul></li></ul></div>
																			<div id="CGOMsavebtndivOfG<%=a %>_<%=i %>" style="display: none" align="center">
																				<%if(fromPage != null && fromPage.equals("AD")) { %> 
																					<input type="button" value="Save" class="btn btn-primary" name="submit"  id="frmCGOMsystemOfG_submit" onclick="addQuestionSystemOther('frmCGOMsystemOfG<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																				<% } else { %>
																					<input type="submit" value="Save" class="btn btn-primary" name="submit" id="frmCGOMsystemOfG_submit"/>
																				<%} %>
																				<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfG<%=a %>_<%=i %>','CGOMgoalnewquediv<%=a %>_<%=i %>')"/>
																			</div>
																	</form>							
										<form action="AddQuestionSystemOther" id="frmCGOMsystemOfO<%=a %>_<%=i %>" method="POST"> 
											<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
											<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
											<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
											<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
											<input type="hidden" name="UID22" id="UID22<%=a %>_<%=i %>" value="" />
											<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
											<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
											<input type="hidden" name="type22" id="type22<%=a %>_<%=i %>" value="" />
											<div id="CGOMobjectivenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
												<ul class="ul_class">
													<li> <table class="table table_no_border" style="width: 100%;">
															<%-- <tr><th width="15%"><span id="CGOMObjCntO<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Objective
																<span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGOMsavebtndivOfO<%=a %>_<%=i %>','CGOMobjectivenewquediv<%=a %>_<%=i %>')"/></span></td>
															</tr> --%>
															<tr><th width="15%"><span id="CGOMObjCntO<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Objective
																<span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGOMsavebtndivOfO<%=a %>_<%=i %>','CGOMobjectivenewquediv<%=a %>_<%=i %>')" ></i></span></td>
															</tr>
															
															<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="objectiveSectionName" id="objectiveSectionName"class="validateRequired" style="width: 450px;"/></td></tr>
															<tr><th>Description</th><td><input type="text" name="objectiveDescription" style="width: 450px;"/></td></tr>
															<tr><th>Weightage %<sup>*</sup></th><td>
																<input type="text" name="objectiveWeightage" id="objectiveWeightage<%=a %>_<%=i %>o" class="validateRequired" value="100" onkeyup="validateScore(this.value,'objectiveWeightage<%=a %>_<%=i %>o','hideobjectiveWeightage<%=a %>_<%=i %>o')" onkeypress="return isNumberKey(event)"/>
																<input type="hidden" name="hideobjectiveWeightage" id="hideobjectiveWeightage<%=a %>_<%=i %>o" value="100"/></td>
															</tr>
														</table>
													</li>
													<li><ul class="ul_class">
															<li> <table class="table" style="width: 100%;">
																	<tr><th width="15%"><span id="CGOMMeasureCntO<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Measures <input type="hidden" name="measureID"/></td></tr>
																	<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																	<tr><th>Description</th><td><input type="text" name="measuresDescription" style="width: 450px;"/></td></tr>
																	<tr><th>Weightage %<sup>*</sup></th><td>
																		<input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>o" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>o','hidemeasureWeightage<%=a %>_<%=i %>o')" onkeypress="return isNumberKey(event)"/>
																		<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>o" value="100"/></td></tr>
																</table>
															</li>
															<li><ul><li>
																	<table class="table" width="100%">
																		<tr><th><span id="CGOMQueCntO<%=a %>_<%=i %>" style="float: left;"></span></th><th width="17%">Add Question<sup>*</sup><input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>o" value="<%=queAnstype%>"/>
																				<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>o" value="<%=sectionattribute %>"/></th>
																			<td colspan="3"><span id="newquespan<%=a %>_<%=i %>o" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>o" value="0"/>
																			<textarea rows="2" name="question" id="question<%=a %>_<%=i %>o" class="validateRequired" style="width: 330px;"></textarea></span>
																			<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																			<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>o" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>o','hideweightage<%=a %>_<%=i %>o')" onkeypress="return isNumberKey(event)"/>
																			<input type="hidden" style="width:35px !important;" name="hideweightage" id="hideweightage<%=a %>_<%=i %>o" value="100"/></span>
																			<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>o','addQue');" > +Q </a></span>
																			<span id="checkboxspan<%=a %>_<%=i %>o" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>o" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>o')"/>
																			<input type="hidden" id="status<%=a %>_<%=i %>o" name="status" value="0"/></span><input type="hidden" name="questiontypename" value="0" /></td>
																		</tr>
																		<tr id="answerType<%=a %>_<%=i %>o" style="display: none"><th>&nbsp;</th><th>&nbsp;</th>
																			<td>a)<input type="text" name="optiona" class="validateRequired form-control"/><input type="checkbox" value="a" name="correct0" /></td>
																			<td colspan="2">b)<input type="text" name="optionb" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="b" /></td>
																		</tr>
																		<tr id="answerType1<%=a %>_<%=i %>o" style="display: none"><th>&nbsp;</th><th>&nbsp;</th><td>c)<input type="text" name="optionc" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="c" /></td>
																			<td colspan="2">d)<input type="text" name="optiond" class="validateRequired form-control"/><input type="checkbox" name="correct0" value="d" /></td>
																		</tr>
																		<tr id="answerType2<%=a %>_<%=i %>o" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																			<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																		</tr>
																	</table> 
																</li></ul></li></ul></li></ul></div>
										<div id="CGOMsavebtndivOfO<%=a %>_<%=i %>" style="display: none" align="center">
											<%if(fromPage != null && fromPage.equals("AD")) { %> 
												<input type="button" value="Save" class="btn btn-primary" name="submit" id="frmCGOMsystemOfO_submit" onclick="addQuestionSystemOther('frmCGOMsystemOfO<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
											<%} else { %><input type="submit" value="Save" class="btn btn-primary" name="submit" id="frmCGOMsystemOfO_submit"/><%} %>
											<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfO<%=a %>_<%=i %>','CGOMobjectivenewquediv<%=a %>_<%=i %>')"/>
										</div>
									</form>							
									<form action="AddQuestionSystemOther" id="frmCGOMsystemOfM<%=a %>_<%=i %>" method="POST" > 
										<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
										<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
										<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
										<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
										<input type="hidden" name="UID23" id="UID23<%=a %>_<%=i %>" value="" />
										<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
										<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
										<input type="hidden" name="type23" id="type23<%=a %>_<%=i %>" value="" />
										<div id="CGOMmeasurenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">				
											<ul class="ul_class">
												<li> <table class="table table_no_border" style="width: 100%;">
														<%-- <tr><th width="15%"><span id="CGOMMeasureCntM<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
															<td>Measures <input type="hidden" name="measureID"/><span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" 
															src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGOMsavebtndivOfM<%=a %>_<%=i %>','CGOMmeasurenewquediv<%=a %>_<%=i %>')"/></span></td>
														</tr> --%>
														<tr><th width="15%"><span id="CGOMMeasureCntM<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
															<td>Measures <input type="hidden" name="measureID"/><span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGOMsavebtndivOfM<%=a %>_<%=i %>','CGOMmeasurenewquediv<%=a %>_<%=i %>')" ></i></span></td>
														</tr>
														
														<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
														<tr><th>Description</th><td><input type="text" name="measuresDescription" style="width: 450px;"/></td></tr>
														<tr><th>Weightage %<sup>*</sup></th><td><input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>m','hidemeasureWeightage<%=a %>_<%=i %>m')" onkeypress="return isNumberKey(event)"/>
															<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>m" value="100"/></td></tr>
													 </table>
												</li>
												<li><ul><li><table class="table" width="100%">
															<tr><th><span id="CGOMQueCntM<%=a %>_<%=i %>" style="float: left;"></span></th>
															<th width="17%">Add Question<sup>*</sup><input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>m" value="<%=queAnstype%>"/>
																<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>m" value="<%=sectionattribute %>"/></th>
																<td colspan="3"><span id="newquespan<%=a %>_<%=i %>m" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>m" value="0"/>
																<textarea rows="2" name="question" id="question<%=a %>_<%=i %>m" class="validateRequired" style="width: 330px;"></textarea></span>
																<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>m','hideweightage<%=a %>_<%=i %>m')" onkeypress="return isNumberKey(event)"/>
																<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>m" value="100" /></span>
																<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>m','addQue');" > +Q </a></span>
																<span id="checkboxspan<%=a %>_<%=i %>m" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>m" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>m')"/>
																<input type="hidden" id="status<%=a %>_<%=i %>m" name="status" value="0"/></span><input type="hidden" name="questiontypename" value="0" /></td>
															</tr>
															<tr id="answerType<%=a %>_<%=i %>m" style="display: none"><th>&nbsp;</th><th>&nbsp;</th>
																<td>a)<input type="text" name="optiona" class="validateRequired form-control"/><input type="checkbox" value="a" name="correct0" /></td>
																<td colspan="2">b)<input type="text" name="optionb" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="b" /></td>
															</tr>
															<tr id="answerType1<%=a %>_<%=i %>m" style="display: none"><th>&nbsp;</th><th>&nbsp;</th>
																<td>c)<input type="text" name="optionc" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="c" /></td>
																<td colspan="2">d)<input type="text" name="optiond" class="validateRequired form-control" /><input type="checkbox" name="correct0" value="d" /></td>
															</tr>
															<tr id="answerType2<%=a %>_<%=i %>m" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
															</tr>
														</table> 
													</li></ul></li></ul></div>
									<div id="CGOMsavebtndivOfM<%=a %>_<%=i %>" style="display: none" align="center">
										<%if(fromPage != null && fromPage.equals("AD")) { %> 
											<input type="button" value="Save" class="btn btn-primary" name="submit" id="frmCGOMsystemOfM_submit" onclick="addQuestionSystemOther('frmCGOMsystemOfM<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
										<%} else { %><input type="submit" value="Save" class="btn btn-primary" name="submit" id="frmCGOMsystemOfM_submit"><%} %>
										<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfM<%=a %>_<%=i %>','CGOMmeasurenewquediv<%=a %>_<%=i %>')"/>
									</div>
							</form>							
										<form action="AddQuestionSystemOther" id="frmCGOMsystemOfQ<%=a %>_<%=i %>" method="POST" > 
											<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
											<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
											<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
											<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
											<input type="hidden" name="UID24" id="UID24<%=a %>_<%=i %>" value="" />
											<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
											<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
											<input type="hidden" name="type24" id="type24<%=a %>_<%=i %>" value="" />
											<div id="CGOMquenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
												<ul class="ul_class">
													<li><table class="table table_no_border" width="100%">
														<tr><th><span id="CGOMQueCntQ<%=a %>_<%=i %>" style="float: left;"></span></th>
														<th width="17%">Add Question<sup>*</sup><input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>q" value="<%=queAnstype%>"/>
															<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>q" value="<%=sectionattribute %>"/></th>
															<td colspan="3"><span id="newquespan<%=a %>_<%=i %>q" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>q" value="0"/>
															<textarea rows="2" name="question" id="question<%=a %>_<%=i %>q" class="validateRequired" style="width: 330px;"></textarea></span>
															<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
															<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>q" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>q','hideweightage<%=a %>_<%=i %>q')" onkeypress="return isNumberKey(event)"/>
															<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>q" value="100" /></span>
															<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>q','addQue');" > +Q </a></span>
															<span id="checkboxspan<%=a %>_<%=i %>q" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>q" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>q')"/>
															<input type="hidden" id="status<%=a %>_<%=i %>q" name="status" value="0"/></span>
														</tr>
														<tr id="answerType<%=a %>_<%=i %>q" style="display: none"><th>&nbsp;</th><th>&nbsp;</th>
															<td>a)<input type="text" name="optiona" class="validateRequired form-control" /><input type="checkbox" value="a" name="correct0" /></td>
															<td colspan="2">b)<input type="text" name="optionb" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="b" /></td>
														</tr>
														<tr id="answerType1<%=a %>_<%=i %>q" style="display: none"><th>&nbsp;</th><th>&nbsp;</th><td>c)<input type="text" name="optionc" class="validateRequired form-control"/> <input type="checkbox" name="correct0" value="c" /></td>
															<td colspan="2">d)<input type="text" name="optiond" class="validateRequired form-control"/><input type="checkbox" name="correct0" value="d" /></td>
														</tr>
														<tr id="answerType2<%=a %>_<%=i %>q" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
															<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
														</tr>
													</table> 
												</li></ul></div>
										<div id="CGOMsavebtndivOfQ<%=a %>_<%=i %>" style="display: none" align="center">
											<%if(fromPage != null && fromPage.equals("AD")) { %> 
												<input type="button" value="Save" class="btn btn-primary" name="submit" id="frmCGOMsystemOfQ_submit" onclick="addQuestionSystemOther('frmCGOMsystemOfQ<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
											<%} else { %><input type="submit" value="Save" class="btn btn-primary" name="submit" id="frmCGOMsystemOfQ_submit"/><%} %>
											<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGOMsavebtndivOfQ<%=a %>_<%=i %>','CGOMquenewquediv<%=a %>_<%=i %>')"/>
										</div>
									</form>
									<div id="CGOMeditquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none"></div>	
							<%
								} else if (uF.parseToInt(innerList1.get(2)) == 2) {
									String CMScoreUID="";
									String CMMeasureUID="";
									String CMQueUID="";
									String newscorecnt=null;
									String newmeasurecnt=null;
									String newquecnt=null;
									List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
									Map<String, List<List<String>>> scoreMp = list.get(0);
									Map<String, List<List<String>>> measureMp = list.get(1);
									Map<String, List<List<String>>> questionMp = list.get(2);
							%>
									<ul id="" class="ul_class1">
										<li><div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
												<div style="text-align:left; height: 35px;">
													<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) { %>
														&nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
													<!-- ===start parvez date: 22-02-2023 Note removed: - after :=== -->	
														<span style="float: right; margin-right: 333px;"><strong>Weightage :&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"0")%>%</span>
													<!-- ===end parvez date: 22-02-2023=== -->	
														<div style="width: 70%">
															<ul id="" class="ul_subsection"><li><div style=" width: 100%; float: left;"><%=innerList1.get(4)%></div><div style="width: 100%; float: left;"><%=innerList1.get(5)%></div></li></ul>
														</div>
													<%} %>
													<div style="width: 100%; float: left;">
														<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
															<span style="float: right;">
																<a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage %>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																<% if(hmSubSections != null && innerList1.get(0) != null && uF.parseToInt(hmSubSections.get(innerList1.get(0))) == 0) { %>
																	<% if(fromPage != null && fromPage.equals("AD")) { %>
																		<a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																	<% } else { %>
																	<a title="Delete" href="DeleteAppraisalLevelAndSystem.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																	<% } %>
																	
																<% } %>
															</span>
														<%} %>
														<span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
													</div>
												</div>
												<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 24.9%;  text-align: center;"><b>Competencies</b></div>
												<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 9%;  text-align: center;"><b>Weightage</b></div>
												<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 18.3%;  text-align: center;"><b>Measure</b></div>
												<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 9.1%;  text-align: center;"><b>Weightage</b></div>
												<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 28.4%;  text-align: center;"><b>Question</b></div>
												<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 9%;  text-align: center;"><b>Weightage</b></div>
												<!-- <div style="overflow: hidden; float: left; border: 1px solid #eee; width: 9%;  text-align: center;"><b>Assessment_System</b></div> -->
												<div style="overflow: hidden; float: left; width: 99.9%;">
													<%
														double CMtotScoreWeight = 0;
														List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
														int intscorecnt = scoreList != null ? scoreList.size()+1 : 1;
														newscorecnt = intscorecnt+"";
														for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
															List<String> innerList = scoreList.get(j);
															CMtotScoreWeight += uF.parseToDouble(innerList.get(2));
														}
															String queAnstype="",sectionattribute="";
															CMScoreUID =innerList1.get(0);
															for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
																List<String> innerList = scoreList.get(j);
																z++;
																queAnstype = innerList.get(4);
																sectionattribute = innerList.get(5);
													%>
																<div style="overflow: hidden; float: left; width: 100%;">
																	<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24.9%;">
																		<p><span style="float: left; margin-left: 4px;"><%=innerList.get(1)%></span>
																			<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																				<span style="float: left; margin-left: 5px;">
																				<a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=innerList.get(0)%>','score','CMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>se','<%=innerList.get(4) %>','<%=CMtotScoreWeight %>','<%=j+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																					<% if(j>0 && hmQuestions != null && innerList.get(0) != null && uF.parseToInt(hmQuestions.get(innerList.get(0))) == 0) { %>
																						<%if(fromPage != null && fromPage.equals("AD")) { %>
																							<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=innerList.get(0) %>','C','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																						<%} else {%>
																								<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=innerList.get(0)%>&type=C" onclick="return confirm('Are you sure you want to delete this Compentency?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																						<%} %>
																					<% } %>
																				</span> 
																			<%} %>
																		</p>
																	</div>
																	<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 9.4%; text-align: right;"><p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p></div>

																	<div style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 64.7%;">
																		<%
																			double CMtotMeasureWeight = 0;
																			List<List<String>> measureList = measureMp.get(innerList.get(0));
																			int intmeasurecnt = measureList != null ? measureList.size()+1 : 1;
																			newmeasurecnt = ""+(j+1)+"."+intmeasurecnt;
																			for (int k = 0; measureList != null && k < measureList.size(); k++) {
																				List<String> measureinnerList = measureList.get(k);
																				CMtotMeasureWeight += uF.parseToDouble(measureinnerList.get(2));
																			}
																			
																			    CMMeasureUID =innerList.get(0);
																				for (int k = 0; measureList != null && k < measureList.size(); k++) {
																					List<String> measureinnerList = measureList.get(k);
																					z++;
																		%>
																					<div style="overflow: hidden; float: left; width: 100%;">	
																						<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 28.3%;">
																							<p><span style="float: left; margin-left: 4px;"><%=measureinnerList.get(1)%></span>
																								<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																									<span style="float: left; margin-left: 5px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=measureinnerList.get(0)%>','measure','CMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>me','<%=innerList.get(4) %>','<%=CMtotMeasureWeight %>','<%=j+1 %>.<%=k+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																										<% if(k>0 && hmQuestions != null && measureinnerList.get(0)!= null &&  uF.parseToInt(hmQuestions.get(measureinnerList.get(0))) == 0) { %>
																											<%if(fromPage != null && fromPage.equals("AD")) { %>
																												<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=measureinnerList.get(0) %>','M','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																											<%} else {%>
																												<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=measureinnerList.get(0)%>&type=M" onclick="return confirm('Are you sure you want to delete this Measure?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																											<%} %>
																										<% } %>
																									</span>
																								<%} %>
																							</p>
																						</div>
																						<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.4%; text-align: right;"><p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p></div>
																						<div style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 57.1%;">
																							<%
																								double CMtotQueWeight = 0;
																								List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
																								int intquecnt = questionList != null ? questionList.size()+1 : 1;
																								newquecnt = ""+(j+1)+"."+(k+1)+"."+intquecnt;
																								for (int l = 0; questionList != null && l < questionList.size(); l++) {
																									List<String> question1List = questionList.get(l);
																									CMtotQueWeight += uF.parseToDouble(question1List.get(1));
																								}
																									CMQueUID =measureinnerList.get(0);
																									for (int l = 0; questionList != null && l < questionList.size(); l++) {
																										List<String> question1List = questionList.get(l);
																										z++;
																							%>
																										<div style="overflow: hidden; float: left; width: 100%;">
																											<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 77%;">
																												<!-- ===start parvez date: 22-02-2023=== -->	
																													<p><span style="float: left; margin-left: 4px;" id="textlabel"><%=question1List.get(0)%></span>
																												<!-- ===end parvez date: 22-02-2023=== -->	
																													<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																													<span style="float: left; margin-left: 10px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=question1List.get(3)%>','quest','CMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>qe','<%=innerList.get(4) %>','<%=CMtotQueWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																														<% if(l>0 && hmQuestions != null && question1List.get(3) != null && uF.parseToInt(hmQuestions.get(question1List.get(3))) == 0) { %>
																															<%if(fromPage != null && fromPage.equals("AD")) { %>
																																<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=question1List.get(3) %>','Q','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																															<%} else {%>
																																<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=question1List.get(3)%>&type=Q" onclick="return confirm('Are you sure you want to delete this Question?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																															<%} %>
																														<% } %>	
																													</span>	
																													<%} %>
																													</p>
																											</div>
																											<div style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 22%; text-align: right;"><p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p></div>
																										</div>
																									<% } %>
																									<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																										<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCMnewquediv('<%=a %>_<%=i %>', 'quest', '<%=CMtotQueWeight %>','<%=CMQueUID %>','<%=newquecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>q','answerType1<%=a %>_<%=i %>q','answerType2<%=a %>_<%=i %>q','0')" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																									<%} %>
																								</div>
																							</div>
																						<% } %>
																						<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																							<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCMnewquediv('<%=a %>_<%=i %>', 'measure', '<%=CMtotMeasureWeight %>','<%=CMMeasureUID %>','<%=newmeasurecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>m','answerType1<%=a %>_<%=i %>m','answerType2<%=a %>_<%=i %>m','0')" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																						<%} %>
																					</div>
																				</div>
																		  <% } %>
																			<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																				<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCMnewquediv('<%=a %>_<%=i %>', 'score', '<%=CMtotScoreWeight %>','<%=CMScoreUID %>','<%=newscorecnt %>');changeNewQuestionTypeOther('<%=queAnstype %>','answerType<%=a %>_<%=i %>s','answerType1<%=a %>_<%=i %>s','answerType2<%=a %>_<%=i %>s','0')" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																			<%} %>
																		</div>
																	</div></li></ul>
														<!-- this div is only created for some prblm -->			
															<div style="display: none"><s:form action="" id="frmCMsystemOfS1" theme="simple"></s:form></div>
															<form action="AddQuestionSystemOther" id="frmCMsystemOfS<%=a %>_<%=i %>" method="POST" > 
																<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																<input type="hidden" name="UID30" id="UID30<%=a %>_<%=i %>" value="" />
																<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																<input type="hidden" name="type30" id="type30<%=a %>_<%=i %>" value="" />
																<div id="CMscorenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">		
																	<ul class="ul_class">
																		<li><table class="table table_no_border" style="width: 100%;">
																		<%-- <tr><th width="15%"><span id="CMScoreCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Competency
																			<span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CMsavebtndivOfS<%=a %>_<%=i %>','CMscorenewquediv<%=a %>_<%=i %>')"/></span></td></tr> --%>
																			
																			<tr><th width="15%"><span id="CMScoreCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Competency
																			<span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CMsavebtndivOfS<%=a %>_<%=i %>','CMscorenewquediv<%=a %>_<%=i %>')"></i></span></td></tr>
																			
																			
																				<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="scoreSectionName" id="scoreSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																				<tr><th>Description</th><td><input type="text" name="scoreCardDescription" style="width: 450px;"/></td></tr>
																				<tr><th>Weightage %<sup>*</sup></th><td>
																					<input type="text" name="scoreCardWeightage" id="scoreCardWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'scoreCardWeightage<%=a %>_<%=i %>s','hidescoreCardWeightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																					<input type="hidden" name="hidescoreCardWeightage" id="hidescoreCardWeightage<%=a %>_<%=i %>s" value="100"/></td>
																				</tr>
																			</table>
																		</li>
																		 <li><ul class="ul_class">
																				<li><table class="table" style="width: 100%;">
																						<tr><th width="15%"><span id="CMMeasureCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th><td>Measures <input type="hidden" name="measureID"/></td></tr>
																						<tr><th>Section Name<sup>*</sup></th><td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/></td></tr>
																						<tr><th>Description</th><td><input type="text" name="measuresDescription" style="width: 450px;"/></td></tr>
																						<tr><th>Weightage %<sup>*</sup></th><td>
																							<input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>s','hidemeasureWeightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																							<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>s" value="100"/></td></tr>
																					</table>
																				</li>
																				
																				<li><ul><li><table class="table" width="100%">
																								<tr><th><span id="CMQueCntS<%=a %>_<%=i %>" style="float: left;"></span></th>
																								<th width="17%">Add Question<sup>*</sup><input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
																									<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/></th>
																									<td colspan="3">
																									<span id="newquespan<%=a %>_<%=i %>s" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>s" value="0"/>
																									<textarea rows="2" name="question" id="question<%=a %>_<%=i %>s" class="validateRequired" style="width: 330px;"></textarea></span>
																									<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																									<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>s','hideweightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																									<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>s" value="100"/></span>
																									<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>s','addQue');" > +Q </a></span>
																									<span id="checkboxspan<%=a %>_<%=i %>s" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>s" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>s')"/>
																									<input type="hidden" id="status<%=a %>_<%=i %>s" name="status" value="0"/></span><input type="hidden" name="questiontypename" value="0" /></td>
																								</tr>
																								<tr id="answerType<%=a %>_<%=i %>s" style="display: none"><th>&nbsp;</th><th>&nbsp;</th>
																									<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																									<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" /></td>
																								</tr>
																								<tr id="answerType1<%=a %>_<%=i %>s" style="display: none"><th>&nbsp;</th><th>&nbsp;</th>
																									<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																									<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																								</tr>
																								<tr id="answerType2<%=a %>_<%=i %>s" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																									<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																								</tr>
																						  </table> 
																					  </li></ul></li></ul></li></ul></div>
																<div id="CMsavebtndivOfS<%=a %>_<%=i %>" style="display: none" align="center">
																	<%if(fromPage != null && fromPage.equals("AD")) { %> 
																		<input type="button" value="Save" class="btn btn-primary" name="submit" id="frmCGOMsystemOfQ_submit" onclick="addQuestionSystemOther('frmCGOMsystemOfQ<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																	<%} else { %><input type="submit" value="Save" class="btn btn-primary" name="submit" id="frmCGOMsystemOfQ_submit"><%} %>
																	<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CMsavebtndivOfS<%=a %>_<%=i %>','CMscorenewquediv<%=a %>_<%=i %>')"/>
																</div>
															</form>
															<form action="AddQuestionSystemOther" id="frmCMsystemOfM<%=a %>_<%=i %>" method="POST" > 
																<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																<input type="hidden" name="UID31" id="UID31<%=a %>_<%=i %>" value="" />
																<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																<input type="hidden" name="type31" id="type31<%=a %>_<%=i %>" value="" />
																<div id="CMmeasurenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">				
																	<ul class="ul_class">
																		<li>
																			<table class="table table_no_border" style="width: 100%;">
																				<%-- <tr><th width="15%"><span id="CMMeasureCntM<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																					<td>Measures <input type="hidden" name="measureID"/>
																					<span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CMsavebtndivOfM<%=a %>_<%=i %>','CMmeasurenewquediv<%=a %>_<%=i %>')"/></span>
																					</td>
																				</tr> --%>
																				<tr><th width="15%"><span id="CMMeasureCntM<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																					<td>Measures <input type="hidden" name="measureID"/>
																					<span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CMsavebtndivOfM<%=a %>_<%=i %>','CMmeasurenewquediv<%=a %>_<%=i %>')"></i></span>
																					</td>
																				</tr>
																				
																				<tr>
																					<th>Section Name<sup>*</sup></th>
																					<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
																					</td>
																				</tr>
																				<tr>
																					<th>Description</th>
																					<td><input type="text" name="measuresDescription" style="width: 450px;"/>
																					</td>
																				</tr>
																				<tr>
																					<th>Weightage %<sup>*</sup></th>
																					<td>
																					<input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>m','hidemeasureWeightage<%=a %>_<%=i %>m')" onkeypress="return isNumberKey(event)"/>
																					<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>m" value="100"/>
																					</td>
																				</tr>
																			</table>
																		</li>
																								
																		<li><ul><li><table class="table" width="100%">
																						<tr><th><span id="CMQueCntM<%=a %>_<%=i %>" style="float: left;"></span></th>
																							<th width="17%">Add Question<sup>*</sup>
																							<input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>m" value="<%=queAnstype%>"/>
																							<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>m" value="<%=sectionattribute %>"/>
																							</th>
																							<td colspan="3">
																							<span id="newquespan<%=a %>_<%=i %>m" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>m" value="0"/>
																							<textarea rows="2" name="question" id="question<%=a %>_<%=i %>m" class="validateRequired" style="width: 330px;"></textarea>
																							</span>
																							<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																							<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>m','hideweightage<%=a %>_<%=i %>m')" onkeypress="return isNumberKey(event)"/>
																							<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>m" value="100"/>
																							</span>
																							<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>m','addQue');" > +Q </a></span>
																							<span id="checkboxspan<%=a %>_<%=i %>m" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>m" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>m')"/>
																							<input type="hidden" id="status<%=a %>_<%=i %>m" name="status" value="0"/></span>
																							<input type="hidden" name="questiontypename" value="0" /></td>
																						</tr>
																						<tr id="answerType<%=a %>_<%=i %>m" style="display: none">
																							<th>&nbsp;</th>
																							<th>&nbsp;</th>
																							<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																							<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
																							</td>
																						</tr>
																						<tr id="answerType1<%=a %>_<%=i %>m" style="display: none">
																							<th>&nbsp;</th><th>&nbsp;</th>
																							<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																							<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																						</tr>
																						<tr id="answerType2<%=a %>_<%=i %>m" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																							<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																						</tr>
																				   </table> 
																			   </li>
																		   </ul>
																	  </li>
																	</ul>
																</div>
																
																<div id="CMsavebtndivOfM<%=a %>_<%=i %>" style="display: none" align="center">
																	<%if(fromPage != null && fromPage.equals("AD")) { %> 
																		<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="addQuestionSystemOther('frmCMsystemOfM<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																	<%} else { %>
																		<input type="submit" value="Save" class="btn btn-primary" name="submit" />
																	<%} %>
																	<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CMsavebtndivOfM<%=a %>_<%=i %>','CMmeasurenewquediv<%=a %>_<%=i %>')"/>
																</div>
															</form>
															
															<form action="AddQuestionSystemOther" id="frmCMsystemOfQ<%=a %>_<%=i %>" method="POST" > 
																<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																<input type="hidden" name="UID32" id="UID32<%=a %>_<%=i %>" value="" />
																<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																<input type="hidden" name="type32" id="type32<%=a %>_<%=i %>" value="" />
																<div id="CMquenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
																		<ul class="ul_class">
																			<li>
																				<table class="table table_no_border" width="100%">
																					<tr><th><span id="CMQueCntQ<%=a %>_<%=i %>" style="float: left;"></span></th>
																						<th width="17%">Add Question<sup>*</sup>
																						<input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>q" value="<%=queAnstype%>"/>
																						<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>q" value="<%=sectionattribute %>"/>
																						</th>
																						<td colspan="3">
																						<span id="newquespan<%=a %>_<%=i %>q" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>q" value="0"/>
																						<textarea rows="2" name="question" id="question<%=a %>_<%=i %>q" class="validateRequired" style="width: 330px;"></textarea>
																						</span>
																						<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																						<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>q" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>q','hideweightage<%=a %>_<%=i %>q')" onkeypress="return isNumberKey(event)"/>
																						<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>q" value="100" />
																						</span>
																						<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>q','addQue');" > +Q </a></span>
																						<span id="checkboxspan<%=a %>_<%=i %>q" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>q" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>q')"/>
																						<input type="hidden" id="status<%=a %>_<%=i %>q" name="status" value="0"/></span>
																						<input type="hidden" name="questiontypename" value="0" /></td>
																					</tr>
																					<tr id="answerType<%=a %>_<%=i %>q" style="display: none">
																						<th>&nbsp;</th><th>&nbsp;</th>
																						<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																						<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
																						</td>
																					</tr>
																					<tr id="answerType1<%=a %>_<%=i %>q" style="display: none">
																						<th>&nbsp;</th><th>&nbsp;</th>
																						<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																						<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																					</tr>
																					<tr id="answerType2<%=a %>_<%=i %>q" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																						<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																					</tr>
																				</table> 
																			</li>
																		</ul>																				
																	</div>
																	<div id="CMsavebtndivOfQ<%=a %>_<%=i %>" style="display: none" align="center">
																		<%if(fromPage != null && fromPage.equals("AD")) { %> 
																			<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="addQuestionSystemOther('frmCMsystemOfQ<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																		<%} else { %>
																			<input type="submit" value="Save" class="btn btn-primary" name="submit" />
																		<%} %>
																		<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CMsavebtndivOfQ<%=a %>_<%=i %>','CMquenewquediv<%=a %>_<%=i %>')"/>
																	</div>
															</form>	
															
															<div id="CMeditquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none"></div>	
		
													<%
														} else {
															String CGMScoreUID="";
															String CGMGoalUID="";
															String CGMMeasureUID="";
															String CGMQueUID="";
															String newscorecnt = null;
															String newgoalcnt = null;
															String newmeasurecnt = null;
															String newquecnt = null;
															List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
															Map<String, List<List<String>>> scoreMp = list.get(0);
															Map<String, List<List<String>>> measureMp = list.get(1);
															Map<String, List<List<String>>> questionMp = list.get(2);
															Map<String, List<List<String>>> GoalMp = list.get(3);
													%>
															<ul id="" class="ul_class1">
																<li>
																	<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
																	<div style="text-align:left; height: 35px;">
																		<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) {%>
																			&nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
																		<!-- ===start parvez date: 22-02-2023 Note removed: - after :=== -->	
																			<span style="float: right; margin-right: 333px;"><strong>Weightage :&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"0")%>%</span>
																		<!-- ===end parvez date=== -->	
																			<div style="width: 70%">
																				<ul id="" class="ul_subsection">
																					<li>
																					<div style=" width: 100%; float: left;"><%=innerList1.get(4)%></div>
																						<div style="width: 100%; float: left;"><%=innerList1.get(5)%></div>
																					</li>
																				</ul>
																			</div>
																		<%} %>
																		<div style="width: 100%; float: left;">
																			<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																				<span style="float: right;">
																				<a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage %>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																					<% if(hmSubSections != null && innerList1.get(0) != null && uF.parseToInt(hmSubSections.get(innerList1.get(0))) == 0) { %>
																						<% if(fromPage != null && fromPage.equals("AD")) { %>
																							<a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																						<% } else { %>
																							 <a title="Delete" href="DeleteAppraisalLevelAndSystem.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																						<% } %>
																						
																					<% } %>
																				</span>
																			<%} %>
																			<span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
																		</div>
																	</div>
																	<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 16%;  text-align: center;"><b>Competencies</b></div>
																	<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;"><b>Weightage</b></div>
																	<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 16.5%;  text-align: center;"><b>Goal </b></div>
																	<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 8%;  text-align: center;"><b>Weightage</b></div>
																	<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 12%;  text-align: center;"><b>Measure</b></div>
																	<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 5.2%;  text-align: center;"><b>Weightage</b></div>
																	<div style="overflow: hidden; float: left;  border: 1px solid #eee; width: 25.8%; text-align: center;"><b>Question</b></div>
																	<div style="overflow: hidden; float: left;  border: 1px solid #eee; width: 8.1%; text-align: center;"><b>Weightage</b></div>
																	<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 99.9%;">
																		<%
																			double CGMtotScoreWeight = 0;
																			List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
																			int intscorecnt = scoreList != null ? scoreList.size()+1 : 1;
																			newscorecnt = intscorecnt+"";
																			String queAnstype="",sectionattribute="";
																			for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
																				List<String> innerList = scoreList.get(j);
																				CGMtotScoreWeight += uF.parseToDouble(innerList.get(2));
																			}
																			
																				CGMScoreUID =innerList1.get(0);
																				for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
																					List<String> innerList = scoreList.get(j);
																					z++;
																					queAnstype = innerList.get(4);
																					sectionattribute = innerList.get(5);
																		%>

																					<div style="overflow: hidden; float: left; width: 100%;">
																						<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 16%;">
																							<p><span style="float: left; margin-left: 4px;"><%=innerList.get(1)%></span>
																								<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																								<span style="float: left; margin-left: 5px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=innerList.get(0)%>','score','CGMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>se','<%=innerList.get(4) %>','<%=CGMtotScoreWeight %>','<%=j+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																									<% if(j>0 && hmQuestions != null && innerList.get(0) != null && uF.parseToInt(hmQuestions.get(innerList.get(0))) == 0) { %>
																										<%if(fromPage != null && fromPage.equals("AD")) { %>
																											<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=innerList.get(0) %>','C','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																										 <%} else {%>
																											<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=innerList.get(0) %>&type=C" onclick="return confirm('Are you sure you want to delete this Compentency?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																										<%} %>
																									<% } %>
																								</span>
																								<%} %>
																							</p>						
																						</div>
																						<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 7.2%; text-align: right;">
																							<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
																						</div>

																						<div style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 76.6%;">
																							<%
																								double CGMtotGoalWeight = 0;
																								List<List<String>> goalList = GoalMp.get(innerList.get(0));
																								int intgoalcnt = goalList != null ? goalList.size()+1 : 1;
																								newgoalcnt = (j+1)+"."+intgoalcnt+"";
																								for (int k = 0; goalList != null && k < goalList.size(); k++) {
																									List<String> goalinnerList = goalList.get(k);
																									CGMtotGoalWeight += uF.parseToDouble(goalinnerList.get(2));
																								}
																								    CGMGoalUID =innerList.get(0);
																									for (int k = 0; goalList != null && k < goalList.size(); k++) {
																										List<String> goalinnerList = goalList.get(k);
																										z++;
																							%>
																										<div style="overflow: hidden; float: left; width: 100%;">
																											<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 22%;">
																												<p><span style="float: left; margin-left: 4px;"><%=goalinnerList.get(1)%></span>
																													<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																														<span style="float: left; margin-left: 5px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=goalinnerList.get(0)%>','goal','CGMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>ge','<%=innerList.get(4) %>','<%=CGMtotGoalWeight %>','<%=j+1 %>.<%=k+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																															<% if(k>0 && hmQuestions != null && goalinnerList.get(0) != null && uF.parseToInt(hmQuestions.get(goalinnerList.get(0))) == 0) { %>
																																<%if(fromPage != null && fromPage.equals("AD")) { %>
																																	<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=goalinnerList.get(0) %>','G','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																 <%} else {%>
																																	<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=goalinnerList.get(0) %>&type=G" onclick="return confirm('Are you sure you want to delete this Goal?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																<%} %>
																															<% } %>
																														</span>	
																													<%} %>
																												</p>
																											</div>
																											<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 10.5%; text-align: right;">
																												<p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
																											</div>
																											
																											<div style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 66.4%;">
																												<%double CGMtotMeasureWeight = 0;
																													List<List<String>> measureList = measureMp.get(goalinnerList.get(0));
																													int intmeasurecnt = measureList != null ? measureList.size()+1 : 1;
																													newmeasurecnt = (j+1)+"."+(k+1)+"."+intmeasurecnt+"";
																													for (int l = 0; measureList != null && l < measureList.size(); l++) {
																														List<String> measureinnerList = measureList.get(l);
																														CGMtotMeasureWeight += uF.parseToDouble(measureinnerList.get(2));
																													}
																													    CGMMeasureUID =goalinnerList.get(0);
																														for (int l = 0; measureList != null && l < measureList.size(); l++) {
																															List<String> measureinnerList = measureList.get(l);
																															z++;
																												%>
																															<div style="overflow: hidden; float: left; width: 100%;">
																																<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24%;">
																																	<p><span style="float: left; margin-left: 4px;"><%=measureinnerList.get(1)%></span>
																																		<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																			<span style="float: left; margin-left: 5px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=measureinnerList.get(0)%>','measure','CGMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>me','<%=innerList.get(4) %>','<%=CGMtotMeasureWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																																				<% if(l>0 && hmQuestions != null && measureinnerList.get(0) != null && uF.parseToInt(hmQuestions.get(measureinnerList.get(0))) == 0) { %>
																																					<%if(fromPage != null && fromPage.equals("AD")) { %>
																																						<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=measureinnerList.get(0) %>','M','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																					 <%} else {%>
																																						<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=measureinnerList.get(0) %>&type=M" onclick="return confirm('Are you sure you want to delete this Measure?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																					<% } %>
																																					
																																				<% } %>
																																			</span>
																																		<%} %>
																																	</p>
																																</div>
																																<div style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 10.2%; text-align: right;">
																																	<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p></div>
																																<div style="overflow: hidden; float: left; width: 65.8%;">
																																	<%
																																		double CGMtotQueWeight = 0;
																																		List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
																																		int intquecnt = questionList != null ? questionList.size()+1 : 1;
																																		newquecnt = (j+1)+"."+(k+1)+"."+(l+1)+"."+intquecnt+"";
																																		for (int m = 0; questionList != null && m < questionList.size(); m++) {
																																			List<String> question1List = questionList.get(m);
																																			CGMtotQueWeight += uF.parseToDouble(question1List.get(1));
																																		}
																																			CGMQueUID =measureinnerList.get(0);
																																			for (int m = 0; questionList != null && m < questionList.size(); m++) {
																																				List<String> question1List = questionList.get(m);
																																				z++;
																																	%>
																																				<div style="overflow: hidden; float: left; width: 100%;">
																																					<div style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 77%;">
																																							<p><span style="float: left; margin-left: 4px;" id="textlabel"><%=question1List.get(0)%></span>
																																							<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																							<span style="float: left; margin-left: 10px;"><a id="editexist<%=z%>" href="javascript:void(0)" onclick="openOtherSystemEditQue('<%=appraisalList.get(0) %>','<%=innerList1.get(3) %>','<%=innerList1.get(2) %>','<%=question1List.get(3)%>','quest','CGMeditquediv<%=a %>_<%=i %>','<%=a %>_<%=i %>qe','<%=innerList.get(4) %>','<%=CGMtotQueWeight %>','<%=j+1 %>.<%=k+1 %>.<%=l+1 %>.<%=m+1 %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','<%=appFreqId %>','<%=fromPage %>');" title="Edit Exist"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																																								<% if(m>0 && hmQuestions != null && question1List.get(3) != null && uF.parseToInt(hmQuestions.get(question1List.get(3))) == 0) { %>
																																									<%if(fromPage != null && fromPage.equals("AD")) { %>
																																										<a  title="Delete" href="javascript:void(0)" onclick="deleteCGOMAndQuestion('<%=id %>','<%=appFreqId %>','<%=question1List.get(3) %>','Q','<%=fromPage %>') " ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																				 					<%} else {%>
																																										<a title="Delete" href="DeleteCGOMAndQuestion.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=question1List.get(3) %>&type=Q" onclick="return confirm('Are you sure you want to delete this Question?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
																																									<% } %>
																																													
																																								<% } %>
																																							</span>
																																							<%} %>
																																							</p>
																																					</div>
																																					<div style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 22%; text-align: right;">
																																						<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
																																					</div>
																																				</div>
																																			<% }%>
																																		<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																			<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'quest', '<%=CGMtotQueWeight %>','<%=CGMQueUID %>','<%=newquecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>q','answerType1<%=a %>_<%=i %>q','answerType2<%=a %>_<%=i %>q','0')" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																																		<%} %>
																																	</div>
																																</div>
																															<% }%>
																															<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																																<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'measure','<%=CGMtotMeasureWeight %>','<%=CGMMeasureUID %>','<%=newmeasurecnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>m','answerType1<%=a %>_<%=i %>m','answerType2<%=a %>_<%=i %>m','0')" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																															<%} %>
																														</div>
																													</div>
																												<% } %>
																											<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																												<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'goal', '<%=CGMtotGoalWeight %>','<%=CGMGoalUID %>','<%=newgoalcnt %>');changeNewQuestionTypeOther('<%=innerList.get(4)%>','answerType<%=a %>_<%=i %>g','answerType1<%=a %>_<%=i %>g','answerType2<%=a %>_<%=i %>g','0')" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																											<%} %>		
																										</div>
																									</div>
																								<% }%>
																								<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
																									<span><a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="OpenCGMnewquediv('<%=a %>_<%=i %>', 'score', '<%=CGMtotScoreWeight %>','<%=CGMScoreUID %>','<%=newscorecnt %>');changeNewQuestionTypeOther('<%=queAnstype%>','answerType<%=a %>_<%=i %>s','answerType1<%=a %>_<%=i %>s','answerType2<%=a %>_<%=i %>s','0')" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
																								<%} %>
																							</div>
																						</div>
																					</li>
																				</ul>
																		<!-- this div is only created for some prblm -->			
																				<div style="display: none"><s:form action="" id="frmCGMsystemOfS1" theme="simple"></s:form></div>
																				<form action="AddQuestionSystemOther" id="frmCGMsystemOfS<%=a %>_<%=i %>" method="POST" > 
																					<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																					<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																					<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																					<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																					<input type="hidden" name="UID40" id="UID40<%=a %>_<%=i %>" value="" />
																					<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																					<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																					<input type="hidden" name="type40" id="type40<%=a %>_<%=i %>" value="" />
																					<div id="CGMscorenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">		
																						<ul class="ul_class">
																							<li>
																								 <table class="table table_no_border" style="width: 100%;">
																									<%-- <tr><th width="15%"><span id="CGMScoreCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																										<td>Competency
																										<span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGMsavebtndivOfS<%=a %>_<%=i %>','CGMscorenewquediv<%=a %>_<%=i %>')"/></span>
																										</td>
																									</tr> --%>
																									
																									<tr><th width="15%"><span id="CGMScoreCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																										<td>Competency
																										<span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGMsavebtndivOfS<%=a %>_<%=i %>','CGMscorenewquediv<%=a %>_<%=i %>')"></i></span>
																										</td>
																									</tr>
																									
																									
																									<tr><th>Section Name<sup>*</sup></th>
																										<td><input type="text" name="scoreSectionName" id="scoreSectionName" class="validateRequired" style="width: 450px;"/></td>
																									</tr>
																									<tr><th>Description</th>
																										<td><input type="text" name="scoreCardDescription" style="width: 450px;"/></td>
																									</tr>
																									<tr><th>Weightage %<sup>*</sup></th>
																										<td>
																										<input type="text" name="scoreCardWeightage" id="scoreCardWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'scoreCardWeightage<%=a %>_<%=i %>s','hidescoreCardWeightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																										<input type="hidden" name="hidescoreCardWeightage" id="hidescoreCardWeightage<%=a %>_<%=i %>s" value="100"/>
																										</td>
																									</tr>
																								</table>
																							</li>
																						
																							<li>
																								<ul class="ul_class">
																									<li>
																										 <table class="table" style="width: 100%;">
																											<tr>
																												<th width="15%"><span id="CGMGoalCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																												<td>Goals </td>
																											</tr>
																											<tr>
																												<th>Section Name<sup>*</sup></th>
																												<td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/></td>
																											</tr>
																											<tr>
																												<th>Description</th>
																												<td><input type="text" name="goalDescription" style="width: 450px;"/></td>
																											</tr>
																											<tr>
																												<th>Weightage %<sup>*</sup></th>
																												<td>
																													<input type="text" name="goalWeightage" id="goalWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'goalWeightage<%=a %>_<%=i %>s','hidegoalWeightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																													<input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=a %>_<%=i %>s" value="100"/>
																												</td>
																											</tr>
																									  </table>
																								  </li>
																																					
																								  <li>
																									<ul class="ul_class">
																										<li>
																											 <table class="table" style="width: 100%;">
																												<tr>
																													<th width="15%">
																													<span id="CGMMeasureCntS<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																													<td>Measures <input type="hidden" name="measureID"/></td>
																												</tr>
																												<tr>
																													<th>Section Name<sup>*</sup></th>
																													<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/></td>
																												</tr>
																												<tr>
																													<th>Description</th>
																													<td><input type="text" name="measuresDescription" style="width: 450px;"/></td>
																												</tr>
																												<tr>
																													<th>Weightage %<sup>*</sup></th>
																													<td>
																														<input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>s','hidemeasureWeightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																														<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>s" value="100"/>
																													</td>
																												</tr>
																											</table>
																										</li>
																											
																										<li>
																											<ul>
																												<li>
																													<table class="table" width="100%">
																														<tr><th><span id="CGMQueCntS<%=a %>_<%=i %>" style="float: left;"></span></th>
																														<th width="17%">Add Question<sup>*</sup>
																															<input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
																															<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/>
																															</th>
																															<td colspan="3">
																															<span id="newquespan<%=a %>_<%=i %>s" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>s" value="0"/>
																															<textarea rows="2" name="question" id="question<%=a %>_<%=i %>s" class="validateRequired" style="width: 330px;"></textarea>
																															</span>
																															<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																															<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>s" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>s','hideweightage<%=a %>_<%=i %>s')" onkeypress="return isNumberKey(event)"/>
																															<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>s" value="100"/>
																															</span>
																															<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>s','addQue');" > +Q </a></span>
																															<span id="checkboxspan<%=a %>_<%=i %>s" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>s" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>s')"/>
																															<input type="hidden" id="status<%=a %>_<%=i %>s" name="status" value="0"/></span>
																															<input type="hidden" name="questiontypename" value="0" /></td>
																														</tr>
																														<tr id="answerType<%=a %>_<%=i %>s" style="display: none">
																															<th>&nbsp;</th><th>&nbsp;</th>
																															<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																															<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
																															</td>
																														</tr>
																														<tr id="answerType1<%=a %>_<%=i %>s" style="display: none">
																															<th>&nbsp;</th><th>&nbsp;</th>
																															<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																															<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																														</tr>
																														<tr id="answerType2<%=a %>_<%=i %>s" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																															<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																														</tr>
																													</table> 
																												</li>
																											</ul>
																										</li>
																									</ul>
																								</li>
																							</ul>
																						 </li>
																					 </ul>
																				 </div>
																				
																				<div id="CGMsavebtndivOfS<%=a %>_<%=i %>" style="display: none" align="center">
																					<%if(fromPage != null && fromPage.equals("AD")) { %> 
																						<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="addQuestionSystemOther('frmCGMsystemOfS<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																					<%} else { %>
																						<input type="submit" value="Save" class="btn btn-primary" name="submit"/ >
																					<%} %>
																					<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGMsavebtndivOfS<%=a %>_<%=i %>','CGMscorenewquediv<%=a %>_<%=i %>')"/>
																				</div>
																			</form>
																									
																			<form action="AddQuestionSystemOther" id="frmCGMsystemOfG<%=a %>_<%=i %>" method="POST" > 
																				<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																				<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																				<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																				<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																				<input type="hidden" name="UID41" id="UID41<%=a %>_<%=i %>" value="" />
																				<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																				<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																				<input type="hidden" name="type41" id="type41<%=a %>_<%=i %>" value="" />
																				<div id="CGMgoalnewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
																					<ul class="ul_class">
																						<li>
																						 	<table class="table table_no_border" style="width: 100%;">
																								<%-- <tr><th width="15%"><span id="CGMGoalCntG<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																									<td>Goals
																									<span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGMsavebtndivOfG<%=a %>_<%=i %>','CGMgoalnewquediv<%=a %>_<%=i %>')"/></span>
																									</td>
																								</tr> --%>
																								<tr><th width="15%"><span id="CGMGoalCntG<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																									<td>Goals
																									<span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGMsavebtndivOfG<%=a %>_<%=i %>','CGMgoalnewquediv<%=a %>_<%=i %>')" ></i></span>
																									</td>
																								</tr>
																								
																								<tr><th>Section Name<sup>*</sup></th>
																									<td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/>
																									</td>
																								</tr>
																								<tr><th>Description</th>
																									<td><input type="text" name="goalDescription" style="width: 450px;"/>
																									</td>
																								</tr>
																								<tr><th>Weightage %<sup>*</sup></th>
																									<td>
																									<input type="text" name="goalWeightage" id="goalWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'goalWeightage<%=a %>_<%=i %>g','hidegoalWeightage<%=a %>_<%=i %>g')" onkeypress="return isNumberKey(event)"/>
																									<input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=a %>_<%=i %>g" value="100"/>
																									</td>
																								</tr>
																							</table>
																						</li>
																						
																						<li>
																							<ul class="ul_class">
																								<li>
																									 <table class="table" style="width: 100%;">
																										<tr><th width="15%"><span id="CGMMeasureCntG<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																											<td>Measures <input type="hidden" name="measureID"/></td>
																										</tr>
																										<tr>
																											<th>Section Name<sup>*</sup></th>
																											<td><input type="text" name="measuresSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"/>
																											</td>
																										</tr>
																										<tr><th>Description</th>
																											<td><input type="text" name="measuresDescription" style="width: 450px;"/>
																											</td>
																										</tr>
																										<tr><th>Weightage %<sup>*</sup></th>
																											<td>
																											<input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>g','hidemeasureWeightage<%=a %>_<%=i %>g')" onkeypress="return isNumberKey(event)"/>
																											<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>g" value="100"/>
																											</td>
																										</tr>
																									</table>
																								</li>
																								
																								<li>
																									<ul>
																										<li>
																											<table class="table" width="100%">
																												<tr><th><span id="CGMQueCntG<%=a %>_<%=i %>" style="float: left;"></span></th>
																												<th width="17%">Add Question<sup>*</sup>
																													<input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>g" value="<%=queAnstype%>"/>
																													<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>g" value="<%=sectionattribute %>"/>
																													</th>
																													<td colspan="3">
																													<span id="newquespan<%=a %>_<%=i %>g" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>g" value="0"/>
																													<textarea rows="2" name="question" id="question<%=a %>_<%=i %>g" class="validateRequired" style="width: 330px;"></textarea>
																													</span>
																													<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																													<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>g" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>g','hideweightage<%=a %>_<%=i %>g')" onkeypress="return isNumberKey(event)"/>
																													<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>g" value="100"/>
																													</span>
																													<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>g','addQue');" > +Q </a></span>
																													<span id="checkboxspan<%=a %>_<%=i %>g" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>g" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>g')"/>
																													<input type="hidden" id="status<%=a %>_<%=i %>g" name="status" value="0"/></span>
																													<input type="hidden" name="questiontypename" value="0" /></td>
																												</tr>
																												<tr id="answerType<%=a %>_<%=i %>g" style="display: none">
																													<th>&nbsp;</th><th>&nbsp;</th>
																													<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																													<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
																													</td>
																												</tr>
																												<tr id="answerType1<%=a %>_<%=i %>g" style="display: none">
																													<th>&nbsp;</th><th>&nbsp;</th>
																													<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																													<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																												</tr>
																												<tr id="answerType2<%=a %>_<%=i %>g" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																													<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																												</tr>
																										   </table>
																										</li>
																									</ul>
																								</li>								
																							</ul>
																						</li>
																					</ul>
																				</div>
																				<div id="CGMsavebtndivOfG<%=a %>_<%=i %>" style="display: none" align="center">
																					<%if(fromPage != null && fromPage.equals("AD")) { %> 
																						<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="addQuestionSystemOther('frmCGMsystemOfG<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																					<%} else { %>
																						<input type="submit" value="Save" class="btn btn-primary" name="submit" />
																					<%} %>
																				<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGMsavebtndivOfG<%=a %>_<%=i %>','CGMgoalnewquediv<%=a %>_<%=i %>')"/>
																			</div>
																		 </form> 
																										
																			<form action="AddQuestionSystemOther" id="frmCGMsystemOfM<%=a %>_<%=i %>" method="POST"> 
																					<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																					<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																					<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																					<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																					<input type="hidden" name="UID42" id="UID42<%=a %>_<%=i %>" value="" />
																					<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																					<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																					<input type="hidden" name="type42" id="type42<%=a %>_<%=i %>" value="" />
																					<div id="CGMmeasurenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">				
																						<ul class="ul_class">
																							<li>
																								 <table class="table table_no_border" style="width: 100%;">
																									<%-- <tr>
																										<th width="15%">
																										<span id="CGMMeasureCntM<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																										<td>Measures <input type="hidden" name="measureID"/>
																										<span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeDiv('CGMsavebtndivOfM<%=a %>_<%=i %>','CGMmeasurenewquediv<%=a %>_<%=i %>')"/></span>
																										</td>
																									</tr> --%>
																									<tr>
																										<th width="15%">
																										<span id="CGMMeasureCntM<%=a %>_<%=i %>" style="float: left;"></span>Level Type</th>
																										<td>Measures <input type="hidden" name="measureID"/>
																										<span style="float: right"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="closeDiv('CGMsavebtndivOfM<%=a %>_<%=i %>','CGMmeasurenewquediv<%=a %>_<%=i %>')" ></i></span>
																										</td>
																									</tr>
																									
																									
																									<tr>
																										<th>Section Name<sup>*</sup></th>
																										<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"/>
																										</td>
																									</tr>
																									<tr>
																										<th>Description</th>
																										<td><input type="text" name="measuresDescription" style="width: 450px;"/>
																										</td>
																									</tr>
																									<tr>
																										<th>Weightage %<sup>*</sup></th>
																										<td>
																										<input type="text" name="measureWeightage" id="measureWeightage<%=a %>_<%=i %>m" class="validateRequired" value="100" onkeyup="validateScore(this.value,'measureWeightage<%=a %>_<%=i %>m','hidemeasureWeightage<%=a %>_<%=i %>m')" onkeypress="return isNumberKey(event)"/>
																										<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=a %>_<%=i %>m" value="100"/>
																										</td>
																									</tr>
																								</table>
																							</li>
																										
																							<li>
																								<ul>
																									<li>
																										<table class="table" width="100%">
																											<tr><th><span id="CGMQueCntM<%=a %>_<%=i %>" style="float: left;"></span></th>
																											<th width="17%">Add Question<sup>*</sup>
																												<input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>m" value="<%=queAnstype%>"/>
																												<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>m" value="<%=sectionattribute %>"/>
																												</th>
																												<td colspan="3">
																												<span id="newquespan<%=a %>_<%=i %>m" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>m" value="0"/>
																												<textarea rows="2" name="question" id="question<%=a %>_<%=i %>m" class="validateRequired" style="width: 330px;"></textarea>
																												</span>
																												<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																												<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>m" class="validateRequired" value="100"  onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>m','hideweightage<%=a %>_<%=i %>m')" onkeypress="return isNumberKey(event)"/>
																												<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>m" value="100"/>
																												</span>
																												<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>m','addQue');" > +Q </a></span>
																												<span id="checkboxspan<%=a %>_<%=i %>m" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>m" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>m')"/>
																												<input type="hidden" id="status<%=a %>_<%=i %>m" name="status" value="0"/></span>
																												<input type="hidden" name="questiontypename" value="0" /></td>
																											</tr>
																											<tr id="answerType<%=a %>_<%=i %>m" style="display: none">
																												<th>&nbsp;</th><th>&nbsp;</th>
																												<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																												<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
																												</td>
																											</tr>
																											<tr id="answerType1<%=a %>_<%=i %>m" style="display: none">
																												<th>&nbsp;</th><th>&nbsp;</th>
																												<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																												<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																											</tr>
																											<tr id="answerType2<%=a %>_<%=i %>m" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																												<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																											</tr>
																										</table> 
																									</li>
																								</ul>
																							</li>
																						</ul>													
																				  </div>
																				
																				<div id="CGMsavebtndivOfM<%=a %>_<%=i %>" style="display: none" align="center">
																					<%if(fromPage != null && fromPage.equals("AD")) { %> 
																						<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="addQuestionSystemOther('frmCGMsystemOfM<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																					<%} else { %>
																						<input type="submit" value="Save" class="btn btn-primary" name="submit" />
																					<%} %>
																					<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGMsavebtndivOfM<%=a %>_<%=i %>','CGMmeasurenewquediv<%=a %>_<%=i %>')"/>
																				</div>
																			</form>
																									
																			<form action="AddQuestionSystemOther" id="frmCGMsystemOfQ<%=a %>_<%=i %>" method="POST"> 
																					<input type="hidden" name="id" id="id" value="<%=appraisalList.get(0)%>" />
																					<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>" />
																					<input type="hidden" name="appsystem" id="appsystem" value="<%=innerList1.get(3)%>" />
																					<input type="hidden" name="scoreType" id="scoreType" value="<%=innerList1.get(2)%>" />
																					<input type="hidden" name="UID43" id="UID43<%=a %>_<%=i %>" value="" />
																					<input type="hidden" name="appLvlID" id="appLvlID" value="<%=innerList1.get(0)%>" />
																					<input type="hidden" name="sectionID" id="sectionID" value="<%=maininnerList.get(0)%>" />
																					<input type="hidden" name="type43" id="type43<%=a %>_<%=i %>" value="" />
																				
																					<div id="CGMquenewquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none">
																						<ul class="ul_class">
																							<li>
																								<table class="table table_no_border">
																										<tr><th><span id="CGMQueCntQ<%=a %>_<%=i %>" style="float: left;"></span></th>
																										<th width="17%">Add Question<sup>*</sup>
																											<input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>q" value="<%=queAnstype%>"/>
																											<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>q" value="<%=sectionattribute %>"/>
																											</th>
																											<td colspan="3">
																											<span id="newquespan<%=a %>_<%=i %>q" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=a %>_<%=i %>q" value="0"/>
																											<textarea rows="2" name="question" id="question<%=a %>_<%=i %>q" class="validateRequired" style="width: 330px;"></textarea>
																											</span>
																											<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
																											<input type="text" style="width:35px !important;" name="weightage" id="weightage<%=a %>_<%=i %>q" class="validateRequired" value="100" onkeyup="validateScore(this.value,'weightage<%=a %>_<%=i %>q','hideweightage<%=a %>_<%=i %>q')" onkeypress="return isNumberKey(event)"/>
																											<input type="hidden" name="hideweightage" id="hideweightage<%=a %>_<%=i %>q" value="100" />
																											</span>
																											<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=a %>_<%=i %>q','addQue');" > +Q </a></span>
																											<span id="checkboxspan<%=a %>_<%=i %>q" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=a %>_<%=i %>q" title="Add to Question Bank" onclick="changeStatus('<%=a %>_<%=i %>q')"/>
																											<input type="hidden" id="status<%=a %>_<%=i %>q" name="status" value="0"/></span>
																											<input type="hidden" name="questiontypename" value="0" /></td>
																										</tr>
																										<tr id="answerType<%=a %>_<%=i %>q" style="display: none">
																											<th>&nbsp;</th>
																											<th>&nbsp;</th>
																											<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																											<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" />
																											</td>
																										</tr>
																										<tr id="answerType1<%=a %>_<%=i %>q" style="display: none">
																											<th>&nbsp;</th>
																											<th>&nbsp;</th>
																											<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																											<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																										</tr>
																										<tr id="answerType2<%=a %>_<%=i %>q" style="display: none;"> <th>&nbsp;</th><th>&nbsp;</th>
																											<td>&nbsp;</td> <td colspan="2">&nbsp;</td>
																										</tr>
																								</table>
																							</li>
																						</ul>
																					</div>
																					<div id="CGMsavebtndivOfQ<%=a %>_<%=i %>" style="display: none" align="center">    
																						<%if(fromPage != null && fromPage.equals("AD")) { %> 
																							<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="addQuestionSystemOther('frmCGMsystemOfQ<%=a %>_<%=i %>','<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
																						<%} else { %>
																							<input type="submit" value="Save" class="btn btn-primary" name="submit" />
																						<%} %>
																							<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeDiv('CGMsavebtndivOfQ<%=a %>_<%=i %>','CGMquenewquediv<%=a %>_<%=i %>')"/>
																					</div>
																			</form>			
																				<div id="CGMeditquediv<%=a %>_<%=i %>" style="float: left; margin: 10px 0px 0px 0px; width: 100%; display: none"></div>	
				<%
				}
			} else if (uF.parseToInt(innerList1.get(3)) == 4) {
				Map<String, List<List<String>>> hmKRAId = (Map<String, List<List<String>>>)request.getAttribute("hmKRAId"); 
				Map<String, List<String>> hmKRADetails = (Map<String, List<String>>)request.getAttribute("hmKRADetails");
			%>
			<ul id="" class="ul_class1">
			<li>
				<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left; height: 35px;">
					<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) { %>
					&nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
				<!-- ===start parvez date: 22-02-2023 Note removed: - after :=== -->	
					<span style="float: right; margin-right: 333px;"><strong>Weightage :&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"")%>%</span>
				<!-- ===end parvez date: 22-02-2023=== -->	
					<div style="width: 70%">
						<ul id="" class="ul_subsection">
							<li>
							<div style=" width: 100%; float: left;"><%=innerList1.get(4)%></div>
								<div style="width: 100%; float: left;"><%=innerList1.get(5)%></div>
							</li>
						</ul>
					</div>
					<%} %>
					<div style="width: 100%; float: left;">
						<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
						<span style="float: right;">
						<a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage %>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
							<% if(hmSubSections != null && innerList1.get(0) != null && uF.parseToInt(hmSubSections.get(innerList1.get(0))) == 0) { %>
								<% if(fromPage != null && fromPage.equals("AD")) { %>
									<a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
								<% } else { %>
									 <a title="Delete" href="DeleteAppraisalLevelAndSystem.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
								<% } %>
							<% } %>
						</span>
						<%} %>
						<span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
					</div>
				</div>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 99.8%;">
					<table class="table" style="width: 100%; float: left;">
						<tr>
							<td><b>KRA</b></td>
							<td><b>Assessment_System</b></td>
							<td><b>Weightage</b></td>
						</tr>
						<%
						String ansType = "Not Defined", attributeId="";
						String ansTypeid = "0";
						if(hmKRAId != null && !hmKRAId.isEmpty()){
							List<List<String>> kraIDList = hmKRAId.get(innerList1.get(0)+"_"+innerList1.get(3));
							//System.out.println("kraIDList ===>> " + kraIDList);
							int count=0;
							for(int aa=0;kraIDList!=null && aa<kraIDList.size();aa++) {
								List<String> innerList = kraIDList.get(aa);
								//System.out.println("innerList.get(1) ===>> " + innerList.get(1));
								if(uF.parseToInt(innerList.get(1)) > 0) {
									ansType = hmAnswerType.get(innerList.get(1));
									ansTypeid = innerList.get(1);
								}
								//System.out.println("ansType ===>> " + ansType);
								//System.out.println("ansTypeid ===>> " + ansTypeid);
								attributeId = innerList.get(3);
									count++;
									%>
									<tr>
										<td><span style="float: left;"><%=count%>)&nbsp;<%=innerList.get(4)%></span>
											<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
												<% if(aa>0 && hmQuestions != null && innerList.get(0) != null && uF.parseToInt(hmQuestions.get(innerList.get(0))) == 0) { %>
													<span style="float: left; margin-left: 10px;"><a title="Delete" href="DeleteGoalTargetKRA.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=innerList.get(0) %>&type=delete" onclick="return confirm('Are you sure you want to delete this KRA?')" ></a></span>
												<% } %>
											<% } %>
										</td>					
										<td style="text-align: right"><%=ansType%></td>		
										<td style="text-align: right"><%=innerList.get(5)%>%</td>
									</tr>
									<%}
								}%>
						<tr><td colspan="3">
						<%if(!flag){ %>
							<span> <a id="addnew<%=z %>"  href="javascript:void(0);" onclick="openAddGoalsTarget('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=innerList1.get(3) %>','<%=ansTypeid %>','<%=attributeId %>','ReviewSummary','<%=a %>_<%=i %>','<%=appFreqId %>');" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
						<%} %>
						</td></tr>
					</table>
			   </div>
			</div>
		</li>
		</ul>
		<div id="goalsTargetDiv<%=a %>_<%=i %>" style="display: none"> </div>
			<%
						} else if (uF.parseToInt(innerList1.get(3)) == 3 || uF.parseToInt(innerList1.get(3)) == 5) {
							String systemtype="",measureOrWeightage="",attributeId=""; 
							Map<String, List<List<String>>> hmGoalTargetId = new HashMap<String, List<List<String>>>();
							Map<String, List<String>> hmGoalTargetDetails = (Map<String, List<String>>)request.getAttribute("hmGoalTargetDetails");
							Map<String, String> hmGoalTargetMesures = (Map<String, String>)request.getAttribute("hmGoalTargetMesures");
							if(uF.parseToInt(innerList1.get(3)) == 3){
								systemtype="Goal";
								measureOrWeightage = "Weightage";
								hmGoalTargetId = (Map<String, List<List<String>>>)request.getAttribute("hmGoalId");
							}else{
								systemtype="Target";
								measureOrWeightage = "Measure";
								hmGoalTargetId = (Map<String, List<List<String>>>)request.getAttribute("hmTargetId");
							}
							%>
			<ul id="" class="ul_class1">
			<li><div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left; height: 35px;">
					<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) { %>
					&nbsp;&nbsp;<strong><%=a+1%>.<%=i+1%>)&nbsp;</strong><%=innerList1.get(1)%>
				<!-- ===start parvez date: 22-02-2023 Note removed: - after :=== -->	
					<span style="float: right; margin-right: 333px;"><strong>Weightage :&nbsp;&nbsp;</strong> <%=uF.showData(innerList1.get(7),"")%>%</span>
				<!-- ===end parvez date: 22-02-2023=== -->	
					<div style="width: 70%">
						<ul id="" class="ul_subsection">
							<li>
							<div style=" width: 100%; float: left;"><%=innerList1.get(4)%></div>
								<div style="width: 100%; float: left;"><%=innerList1.get(5)%></div>
							</li>
						</ul>
					</div>
					<% } %>
					<div style="width: 100%; float: left;">
						<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
						<span style="float: right;">
						<a href="javascript: void(0)" onclick="openEditSection('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=a+1%>.<%=i+1%>','subsection','<%=orientation %>','<%=maininnerList.get(1) %>','<%=maininnerList.get(1) %>','<%=subsectionTotWeightage %>','<%=maininnerList.get(0) %>','<%=appFreqId %>','<%=fromPage %>')" title="Edit Subsection"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
							<% if(hmSubSections != null && innerList1.get(0) != null &&  uF.parseToInt(hmSubSections.get(innerList1.get(0))) == 0) { %>
								<% if(fromPage != null && fromPage.equals("AD")) { %>
									<a title="Delete" href="javascript:void(0)" onclick="deleteReviewLevelAndSystem('<%=appraisalList.get(0) %>','<%=appFreqId %>','<%=maininnerList.get(0) %>','<%=innerList1.get(0) %>','System','<%=fromPage%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
								<% } else { %>
									 <a  title="Delete" href="DeleteAppraisalLevelAndSystem.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&levelID=<%=maininnerList.get(0) %>&lvlID=<%=innerList1.get(0)%>&type=System" onclick="return confirm('Are you sure you want to delete this subsection?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
								<% } %>
							<% } %>
						</span>
						<%} %>
						<span style="float: right; margin-right: 10px; font-size: 11px; font-style: italic;">Last Updated By -&nbsp;<%=uF.showData(innerList1.get(8),"")%>&nbsp;<%=uF.showData(innerList1.get(9),"")%></span>
					</div>
				</div>
							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 99.8%;">
									<table class="table" style="width: 100%; float: left;">
										<tr>
											<td><b><%=systemtype %></b></td>
											<td><b>Assessment_System</b></td>
											<td><b><%=measureOrWeightage %></b></td>
										</tr>
										<%
										String ansType = "Not Defined";
										String ansTypeid = "0";
										if(hmGoalTargetId != null && !hmGoalTargetId.isEmpty()){
										List<List<String>> goalTargetIDList = hmGoalTargetId.get(innerList1.get(0)+"_"+innerList1.get(3));
										int count=0;
										for(int aa=0;goalTargetIDList!=null && aa<goalTargetIDList.size();aa++){
											count++;
											List<String> innerList = goalTargetIDList.get(aa);
											if(uF.parseToInt(innerList.get(1))>0){
												ansType = hmAnswerType.get(innerList.get(1));
												ansTypeid = innerList.get(1);
											}
											attributeId = innerList.get(3);
										%>
										<tr>
											<td><span style="float: left;"><%=count%>)&nbsp;<%=innerList.get(4)%></span>
											<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
												<% if(hmQuestions != null && innerList.get(0) != null && uF.parseToInt(hmQuestions.get(innerList.get(0))) == 0) { %>
													<span style="float: left; margin-left: 10px;"><a title="Delete" href="DeleteGoalTargetKRA.action?id=<%=appraisalList.get(0) %>&appFreqId=<%=appFreqId %>&queID=<%=innerList.get(0) %>&type=delete" onclick="return confirm('Are you sure you want to delete this Goal?')" ><i class="fa fa-trash" aria-hidden="true"></i></a></span>
												<% } %>
											<% } %>
											</td>		
											<td style="text-align: right">
												<%= ansType %>
											</td>					
											<td style="text-align: right">
												<% if(uF.parseToInt(innerList1.get(3)) == 3) { %>
													<%=innerList.get(5) %> %
												<% } else {%>
													<%=uF.showData(hmGoalTargetMesures.get(innerList.get(2)), "-") %>
												<%} %>
											</td>
										</tr>
										<%
											}
										}
										%>
										<tr>
											<td colspan="3">
												<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
												<span> <a id="addnew<%=z %>"  href="javascript:void(0);"  onclick="openAddGoalsTarget('<%=appraisalList.get(0) %>','<%=innerList1.get(0) %>','<%=innerList1.get(3) %>','<%=ansTypeid %>','<%=attributeId %>','ReviewSummary','<%=a %>_<%=i %>','<%=appFreqId %>');" title="Add New"><i class="fa fa-plus-circle" aria-hidden="true"></i></a></span>
												<%} %>
											</td>
									   </tr>
								 </table>
							</div>
						</div>
					</li>
				</ul>
				<div id="goalsTargetDiv<%=a %>_<%=i %>" style="display: none"> </div>
			<%
			}
 		}
	 %>
			<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)){ %>
				<div id="sectionAssessLinkDiv<%=a %>" style="float:left; margin-top: 15px; margin-bottom: 10px; width: 100%;">
					<span id="sectionLinkSpan<%=a %>" style="float: left; margin-left: 100px;"><a href="javascript:void(0)" title="Add Subsections" onclick="openAddNewSystem('<%=appraisalList.get(0) %>','<%=maininnerList.get(0) %>','system','addnewSystemdiv<%=a %>','<%=a+1%>.<%=newsysno %>','<%=subsectionTotWeightage %>','section','sectionAssessLinkDiv<%=a %>','<%=a %>','<%=orientation %>','<%=appFreqId%>','<%=fromPage%>');"> +Subsections </a></span>
					<span id="assessLinkSpan<%=a %>" style="float: left; margin-left: 100px;"><a href="javascript:void(0)" title="Add Assessments" onclick="openAddNewSystem('<%=appraisalList.get(0) %>','<%=maininnerList.get(0) %>','system','addnewSystemdiv<%=a %>','<%=a+1%>.<%=newsysno %>','<%=subsectionTotWeightage %>','assessment','sectionAssessLinkDiv<%=a %>','<%=a %>','<%=orientation %>','<%=appFreqId%>','<%=fromPage%>');"> +Assessments </a></span>
				</div>
			<% } %>
	   			 <div id="addnewSystemdiv<%=a %>" style="clear:both; display: none">
    		</div>
		 </div>
 		</div>
      </div>
   </div>
 <% } %>
 <!-- created by seema -->
 <%} %>
 <!-- created by seema -->
 <%if(fromPage == null || !fromPage.equals("AD")) { %>
       </section></div></section> <%} %>

					

<!-- created by seema -->
	 <%if(request.getParameter("tabName") !=null && request.getParameter("tabName").equals("reviewforms")) { %>
		<%if(!flag && !flagPublish &&  (!finalFlag || !flagProcess)) { %>
			<p style="margin: 0px 0px 0px 15px" class="addnew desgn">
				<a href="javascript:void(0)" onclick="openAddNewLevel('<%=appraisalList.get(0) %>','addnewLeveldiv','<%=newlvlno %>','<%=sectionTotWeightage %>','<%=orientation %>','<%=appFreqId %>','<%=fromPage %>')"; title="Add New Section"><i class="fa fa-plus-circle"></i>Add New Section</a></p> 
		<%} %>
	<% } %> 
	<div id="addnewLeveldiv" style="margin: 10px 0px 0px 0px;clear:both;display: none;"></div>
</div>
</div><!-- End Dattatray -->

     <!-- created by seema -->
<%if(fromPage == null || !fromPage.equals("AD")) { %></div></section></div></section><% } %>
<% } else { %>
	<div class="nodata msg">No review summary.</div>
<% } %>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog modal-dialog1">
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
<div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1"></h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript">
/* @uthor : Dattatray */
$(window).bind('mousewheel DOMMouseScroll', function(event){
    
    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
        // scroll up
        if($(window).scrollTop() == 0 && $("#sectionReviewforms").scrollTop() != 0){
        	$("#sectionReviewforms").scrollTop($("#sectionReviewforms").scrollTop() - 30);
        }
        if($(window).scrollTop() == 0 && $("#sectionSummary").scrollTop() != 0){
        	$("#sectionSummary").scrollTop($("#sectionSummary").scrollTop() - 30);
        }
    }
    else {
        // scroll down
        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#sectionReviewforms").scrollTop($("#sectionReviewforms").scrollTop() + 30);
   		}
        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
 		   $("#sectionSummary").scrollTop($("#sectionSummary").scrollTop() + 30);
		}
    }
});

/* @uthor : Dattatray */
$(window).keydown(function(event){
   
		if(event.which == 40 || event.which == 34)
		{
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#sectionReviewforms").scrollTop($("#sectionReviewforms").scrollTop() + 50);
   			}
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#sectionSummary").scrollTop($("#sectionSummary").scrollTop() + 50);
	   		}
		}
		else if(event.which == 38 || event.which == 33)
		{
	   		if($(window).scrollTop() == 0 && $("#sectionReviewforms").scrollTop() != 0){
    			$("#sectionReviewforms").scrollTop($("#sectionReviewforms").scrollTop() - 50);
    		}
	   		if($(window).scrollTop() == 0 && $("#sectionSummary").scrollTop() != 0){
    			$("#sectionSummary").scrollTop($("#sectionSummary").scrollTop() - 50);
    		}
		}
}); 
</script>