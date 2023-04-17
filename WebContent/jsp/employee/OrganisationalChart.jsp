<%@page import="com.konnect.jpms.util.IMessages"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %> 
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=IMessages.THierarchicalChart%>" name="title"/>
    </jsp:include> --%>
<% 
String fromPage = (String)request.getAttribute("fromPage");

if(fromPage == null) { %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<% } %>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/jquery.jOrgChart.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/prettify.css" />
<script src="<%= request.getContextPath()%>/scripts/organisational/prettify.js" type="text/javascript"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/1.5.3/jspdf.min.js"></script>
<script type="text/javascript" src="https://html2canvas.hertzen.com/dist/html2canvas.js"></script>
<%

	//System.out.println("ORG CHART jsp ");
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    if(CF==null)return;
    
    String strSessionEmpId = (String)session.getAttribute(IConstants.EMPID);
    String productType = (String)session.getAttribute(IConstants.PRODUCT_TYPE);
    String strUserType = null;
    
    	//System.out.println("productType ===>> " + productType);
    	if(productType != null && productType.equals("3")) {
    		strUserType = IConstants.MANAGER;
    	} else {
    		strUserType = (String)session.getAttribute(IConstants.USERTYPE);
    	}
    	//System.out.println("strUserType ===>> " + strUserType);
    	
    Map hmHireracyLevels = (Map)request.getAttribute("hmHireracyLevels");
    //List alHireracyLevels = (List)request.getAttribute("alHireracyLevels");
    
    List alChain = (List)request.getAttribute("alChain");
    Map hmEmpDesigMap = (Map)request.getAttribute("hmEmpDesigMap");
    Map hmEmpMap = (Map)request.getAttribute("hmEmpMap");
    Map hmEmpProfileImage = (Map)request.getAttribute("hmEmpProfileImage");
    
    Map<String, String> hmChieldEmpCnt = (Map<String, String>) request.getAttribute("hmChieldEmpCnt");
    String strContextPath = request.getContextPath();

    %>
<g:compress>
    <script>
		jQuery(document).ready(function() {
		<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>
		$("#org").jOrgChart({
			chartElement : '#chart',
			dragAndDrop  : false // true
		});
		<%}else{%>
		$("#org").jOrgChart({
			chartElement : '#chart',
			dragAndDrop  : false
		});
		<%}%>
        prettyPrint();
        });
		
        function ajaxCall(a, b){
        	if(confirm('Are you sure you want to update the structure?')){
        		var sourceid = a.find("div").attr('id');
           	  	var targetid = b.find("div").attr('id');
           	  	getContent('myDiv','UpdateOrganisation.action?empid='+sourceid+'&superid='+targetid);	
        	}
        }
        
        function submitForm() {
    		var org = document.getElementById("f_org").value;
    		var fromPage = document.getElementById("fromPage").value;
    		var divResult = document.getElementById("divResult1").value;
    		$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$.ajax({
    			type : 'POST',
    			url : 'OrganisationalChart.action?orgId='+org+'&fromPage='+fromPage+'&divResult='+divResult,
    			data : $("#" + this.id).serialize(),
    			success : function(result) {
    				$("#"+divResult).html(result);
    			}
    		});
    	}
        
    </script>
</g:compress>

<section class="content">
    <div class="row jscroll">
    	<% if(fromPage == null) { %>
       		<section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
       	<% } %>
                <div class="box-body" style="padding: 0px 5px; overflow-y: auto; min-height: 600px;">
                    <% if(fromPage == null || !fromPage.equals("MP")) { %>
	                    <%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
	                    <div class="box box-default">  <!--  style="margin-top: 10px;" collapsed-box -->
			                <%-- <div class="box-header with-border">
			                    <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div> --%>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto;">  <!-- display:none; -->
			                    <s:form name="frm_OrganisationalChart" action="OrganisationalChart" theme="simple">
			                    	<s:hidden name="fromPage" id="fromPage" />
			                    	<s:hidden name="divResult" id="divResult1" />
			                        <div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Organization</p>
												<s:select theme="simple" name="orgId" id="f_org" listKey="orgId" listValue="orgName" list="organisationList" key="" onchange="submitForm()"/>
											</div>
										</div>
									</div>
			                    </s:form>
			                </div>
			            </div>
	                    <%} %>
                    <%} %>
                    <div id="myDiv" style="background-color: #999999;color: white;font-weight: bold;margin: 5px;text-align: center;width: 100%;"></div>
                    <ul id="ul-data" style="display:none">
                        <%=request.getAttribute("sbPosition")%>
                    </ul>
                    <% if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
                    <ul id="org" style="display:none">
                        <%=request.getAttribute("sbPosition")%>
                    </ul>
                    <% } else { %>
                    <ul id="org" style="display:none">
                        <% //System.out.println("hmHireracyLevels ===>> " + hmHireracyLevels); %>
                        <%!
                            StringBuilder sb = new StringBuilder();
                            UtilityFunctions uF = new UtilityFunctions();
                            
                            public String func(Map hmHireracyLevels, String strEmpId, Map hmEmpMap,Map hmEmpDesigMap, Map hmEmpProfileImage, StringBuilder sb, String strContextPath, String strSessionEmpId, String strUserType, List alChain, CommonFunctions CF, Map<String, String> hmChieldEmpCnt) {
                            	
                            	//System.out.println("strEmpId ===>> " + strEmpId + " hmHireracyLevels in func ===>> " + hmHireracyLevels);
                            	List alInner1 = (List)hmHireracyLevels.get(strEmpId);
                            	//System.out.println("alInner1 in func ===>> " + alInner1);
                            	if(alInner1==null)alInner1 = new ArrayList();
                            	String strEmpId1 = null;
                            	for(int ii=0; ii<alInner1.size(); ii++) {
                            		strEmpId1 = (String)alInner1.get(ii); 
                            		
                            		if(strUserType!=null && alChain!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.MANAGER)) && !alChain.contains(strEmpId1)) {
                            			continue;
                            		}
                            		
                            		String empImg = ""; 
                            		if((String)hmEmpProfileImage.get(strEmpId1)!=null && !((String)hmEmpProfileImage.get(strEmpId1)).equalsIgnoreCase("avatar_photo.png")) {
                            			if(CF.getStrDocSaveLocation()==null) {
                            				empImg = IConstants.DOCUMENT_LOCATION + ((String)hmEmpProfileImage.get(strEmpId1));
                            			} else {
                            				empImg = CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strEmpId1+"/"+IConstants.I_60x60+"/"+((String)hmEmpProfileImage.get(strEmpId1));
                            			}	
                            		} else {
                            			empImg = "userImages/avatar_photo.png";
                            		}
                            		
                            		/* String empImg = CF.getStrDocRetriveLocation()+(String)hmEmpProfileImage.get(strEmpId1);
                            		String empImg1 = (String)hmEmpProfileImage.get(strEmpId1);
                            		if(empImg1 == null || empImg1.equals("") || empImg1.equals("avatar_photo.png")) {
                            			empImg = "userImages/avatar_photo.png";
                            		} */
                            		
                            		List al = (List)hmHireracyLevels.get(strEmpId1);
                            		String totChieldEmpCnt = hmChieldEmpCnt.get(strEmpId1);
                            		if(ii==0) {
                            			sb.append("<ul>");		
                            		}
                            		
                            		sb.append("<li>");
                            		
                            		//if(strSessionEmpId!=null && strSessionEmpId.equalsIgnoreCase(strEmpId1)) {
                            			sb.append("<img width=\"40\" src=\"" + empImg + "\" />");
                            		//}
                            		
                            		sb.append("<div class=\"emp\" style=\"margin-top: 5px;\" id=\""+strEmpId1+"\">"+hmEmpMap.get(strEmpId1)+"</div>");
                            		
                            		sb.append("<span class=\"desg_tree\">"+uF.showData((String)hmEmpDesigMap.get(strEmpId1),"-") +"</span>");
                            		sb.append("<div class=\"desg_tree\" style=\"font-size: 9.5px; min-width: 85px; border-top: 1px solid #CCC;\"><span style=\"float: left\">Direct(");
                            		sb.append(al != null ? al.size() : "0");
                            		sb.append(") </span> <span style=\"float: right\">Total(" + uF.showData(totChieldEmpCnt, "0") +")</span></div>");
                            		
                            		if(al!=null && al.size()>0) {
                            			func(hmHireracyLevels, strEmpId1, hmEmpMap,hmEmpDesigMap, hmEmpProfileImage, sb, strContextPath, strSessionEmpId, strUserType, alChain, CF, hmChieldEmpCnt);
                            		}
                            		sb.append("</li>");
                            		
                            		if(ii==alInner1.size()-1) {
                            			sb.append("</ul>");		
                            		}
                            	}
                            	return sb.toString();
                            }
                            
                            %>	
                        <%
                            Map<String, String> hmOrgData = (Map<String, String>) request.getAttribute("hmOrgData");
                           //System.out.println("hmOrgData=="+hmOrgData);
                        List alInner = (List)hmHireracyLevels.get("0");
                            String totChieldEmpCnt0 = hmChieldEmpCnt.get("0");
                            //String orgLogo = CF.getStrDocRetriveLocation()+hmOrgData.get("ORG_LOGO");
                            //if(hmOrgData.get("ORG_LOGO") == null) {
                            	//orgLogo = "userImages/company_avatar_photo.png";
                            //}
                            
                            String orgLogo = ""; 
                            if(hmOrgData.get("ORG_LOGO")!=null && !hmOrgData.get("ORG_LOGO").equalsIgnoreCase("company_avatar_photo.png")){
                            	if(CF.getStrDocSaveLocation()==null) {
                            		orgLogo = IConstants.DOCUMENT_LOCATION + hmOrgData.get("ORG_LOGO");
                            	} else {
                            		orgLogo = CF.getStrDocRetriveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+IConstants.I_60x60+"/"+hmOrgData.get("ORG_LOGO");
                            	}	
                            } else {
                            	orgLogo = "userImages/company_avatar_photo.png";
                            }
                            	%>
                        <% if(strUserType != null && !strUserType.equals(IConstants.MANAGER)) { %>
                        <li>
                            <img src="<%=orgLogo %>" height="60px"/>
                            <div class="emp" style="margin-top: 5px;" id="0"><%=uF.showData(hmOrgData.get("ORG_NAME"), "-") %></div>
                            <div class="desg_tree" style="font-size: 10px; width: 100%; border-top: 1px solid #CCC;">
                                <span style="float: left">Direct(<%=alInner != null ? alInner.size() : "0" %>)</span> <span style="float: right">Total(<%=uF.showData(totChieldEmpCnt0, "0") %>)</span>
                            </div>
                            <ul>
                                <% } %>
                                <%
                                    //System.out.println("hmHireracyLevels after ===>> " + hmHireracyLevels);
                                    for(int i=0; alInner != null && i<alInner.size(); i++){
                                    	String strEmpId = (String)alInner.get(i);
                                    	
                                    	if(strUserType!=null && alChain!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.MANAGER)) && !alChain.contains(strEmpId)) {
                                    		continue;
                                    	}
                                    	//String empImg = CF.getStrDocRetriveLocation()+(String)hmEmpProfileImage.get(strEmpId);
                                    	//String empImg1 = (String)hmEmpProfileImage.get(strEmpId);
                                    	//if(empImg1 == null || empImg1.equals("") || empImg1.equals("avatar_photo.png")) {
                                    		//empImg = "userImages/avatar_photo.png";
                                    	//}
                                    	
                                    	String empImg = ""; 
                                    	if((String)hmEmpProfileImage.get(strEmpId)!=null && !((String)hmEmpProfileImage.get(strEmpId)).equalsIgnoreCase("avatar_photo.png")) {
                                    		if(CF.getStrDocSaveLocation()==null) {
                                    			empImg = IConstants.DOCUMENT_LOCATION + ((String)hmEmpProfileImage.get(strEmpId));
                                    		} else {
                                    			empImg = CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strEmpId+"/"+IConstants.I_60x60+"/"+((String)hmEmpProfileImage.get(strEmpId));
                                    		}	
                                    	} else {
                                    		empImg = "userImages/avatar_photo.png";
                                    	}
                                    	
                                    	List alInner1 = (List)hmHireracyLevels.get(strEmpId);
                                    	String totChieldEmpCnt = hmChieldEmpCnt.get(strEmpId);
                                    	%>
                                <li>
                                    <%-- <%if(strSessionEmpId!=null && strSessionEmpId.equalsIgnoreCase(strEmpId)){%> --%>
                                    <img width="40"  src="<%=empImg %>" />
                                    <%-- <img height="40" width="40" border="0" data-original="<%=CF.getStrDocRetriveLocation()+(String)hmEmpProfileImage.get(strEmpId) %>" src="userImages/avatar_photo.png" class="lazy"> --%> <!-- style="float:left;margin-right:10px; border:1px solid #000;" -->
                                    <!-- <div class="clr"></div> -->	
                                    <%-- <%}%> --%>
                                    <div class="emp" style="margin-top: 5px;" id="<%=strEmpId%>"><%=(String)hmEmpMap.get(strEmpId) %></div>
                                    <!-- <div class="clr"></div> -->
                                    <span class="desg_tree"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId),"-")%></span>
                                    <div class="desg_tree" style="font-size: 9.5px; min-width: 85px; border-top: 1px solid #CCC;">
                                        <span style="float: left">Direct(<%=alInner1 != null ? alInner1.size() : "0" %>)</span> <span style="float: right">Total(<%=uF.showData(totChieldEmpCnt, "0") %>)</span>
                                    </div>
                                    <ul>
                                        <%
                                            //System.out.println("hmHireracyLevels after 1 ===>> " + hmHireracyLevels);
                                            //System.out.println("alInner1 after 1 ===>> " + alInner1);
                                            if(alInner1==null)alInner1 = new ArrayList();
                                            String strEmpId1 = null;
                                            for(int ii=0; ii<alInner1.size(); ii++) {
                                            	strEmpId1 = (String)alInner1.get(ii); 
                                            	//System.out.println("strEmpId1 after 1 ===>> " + strEmpId1);
                                            	if(strUserType!=null && alChain!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.MANAGER)) && !alChain.contains(strEmpId1)){
                                            		continue;
                                            	}
                                            	/* String empImgg = CF.getStrDocRetriveLocation()+(String)hmEmpProfileImage.get(strEmpId1);
                                            	String empImgg1 = (String)hmEmpProfileImage.get(strEmpId1);
                                            	if(empImgg1 == null || empImgg1.equals("") || empImgg1.equals("avatar_photo.png")) {
                                            		empImgg = "userImages/avatar_photo.png";
                                            	} */
                                            	
                                            	String empImgg = ""; 
                                            	if((String)hmEmpProfileImage.get(strEmpId1)!=null && !((String)hmEmpProfileImage.get(strEmpId1)).equalsIgnoreCase("avatar_photo.png")){
                                            		if(CF.getStrDocSaveLocation()==null) {
                                            			empImgg = IConstants.DOCUMENT_LOCATION + ((String)hmEmpProfileImage.get(strEmpId1));
                                            		} else {
                                            			empImgg = CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strEmpId1+"/"+IConstants.I_60x60+"/"+((String)hmEmpProfileImage.get(strEmpId1));
                                            		}	
                                            	} else {
                                            		empImgg = "userImages/avatar_photo.png";
                                            	}
                                            	
                                            	List al = (List)hmHireracyLevels.get(strEmpId1);
                                            	String totChieldEmpCnt1 = hmChieldEmpCnt.get(strEmpId1);
                                            	%>
                                        <li>
                                            <%-- <%if(strSessionEmpId!=null && strSessionEmpId.equalsIgnoreCase(strEmpId1)){%> --%>
                                            <img width="40"  src="<%=empImgg %>" />
                                            <!-- <div class="clr"></div> -->	
                                            <%-- <%}%> --%>
                                            <div class="emp" style="margin-top: 5px;" id="<%=strEmpId1 %>"><%=(String)hmEmpMap.get(strEmpId1) %></div>
                                            <!-- <div class="clr"></div> -->
                                            <span class="desg_tree"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId1),"-") %></span>
                                            <div class="desg_tree" style="font-size: 9.5px; min-width: 75px; border-top: 1px solid #CCC;">
                                                <span style="float: left">Direct(<%=al != null ? al.size() : "0" %>)</span> <span style="float: right">Total(<%=uF.showData(totChieldEmpCnt1, "0") %>)</span>
                                            </div>
                                            <%
                                                if(al!=null){
                                                	sb = new StringBuilder();
                                                	out.println(func(hmHireracyLevels, strEmpId1, hmEmpMap,hmEmpDesigMap, hmEmpProfileImage, sb, strContextPath, strSessionEmpId, strUserType, alChain, CF, hmChieldEmpCnt));
                                                }
                                                }
                                                
                                                %>
                                        </li>
                                    </ul>
                                </li>
                                <% } %>
                                <% if(strUserType != null && !strUserType.equals(IConstants.MANAGER)) { %>
                            </ul>
                        </li>
                        <% } %>
                    </ul>
                    <% } %>
                    <script>
                        jQuery(document).ready(function() {
                        
                        /* Custom jQuery for the example */
                        $("#show-list").click(function(e){
                        e.preventDefault();
                        
                        $('#list-html').toggle('fast', function(){
                        if($(this).is(':visible')){
                        	$('#show-list').text('Hide underlying list.');
                        	$(".topbar").fadeTo('fast',0.9);
                        }else{
                        	$('#show-list').text('Show underlying list.');
                        	$(".topbar").fadeTo('fast',1);
                        }
                        });
                        });
                        
                            $('#list-html').text($('#org').html());
                            	$("#org").bind("DOMSubtreeModified", function() {
                                    $('#list-html').text('');
                        
                                    $('#list-html').text($('#org').html());
                        
                                    prettyPrint();
                                });
                            	
                        });
                    </script>
                    <div style="width: 100%; overflow-x: hidden; overflow-y: hidden;" class="sc">
                        <div style="padding:0 10px; height:2px;" class="sc1"> </div>
                    </div>
                    <div class="imp" style="width:100%; overflow:auto;">
                    	<div id="chart" class="orgChart" style="float:left; width:100%; margin-top: 7px; text-align: center; max-height: 550px;"></div>
                    </div>
                </div>
                <!-- /.box-body -->
       <% if(fromPage == null) { %>
       		</div>
        </section>
       	<% } %>
    </div>
</section>
<script type="text/javascript">
    $(function(){
        $(".sc").scroll(function(){
            $(".imp")
                .scrollLeft($(".sc").scrollLeft());
        });
        $(".imp").scroll(function(){
            $(".sc")
                .scrollLeft($(".imp").scrollLeft());
        });
        
        var divWidth = $('#chart .jOrgChart > table').width();
        $('.sc1').css("width",divWidth);
        
        /* $("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
        
        $(window).bind("load", function() {
            var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
        });  */
         
    });

   	
</script>