<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%	UtilityFunctions uF = new UtilityFunctions(); 
    /* Map<String, String> hm=(Map<String, String> )request.getAttribute("myProfile");
    List<List<String>> alSkills=(List<List<String>> )request.getAttribute("alSkills");
    List<List<String>> empScoreList=(List<List<String>> )request.getAttribute("empScoreList");
    List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
    
    Map<String,String> empImageMap=(Map<String,String>)request.getAttribute("empImageMap"); */
    
    List alSkills = (List) request.getAttribute("alSkills");
    String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
    if (hmEmpProfile == null) {
    	hmEmpProfile = new HashMap<String, String>();
    }
    
    List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
	Map<String, String> hmAnalysisSummaryMap = (Map<String, String>) request.getAttribute("hmAnalysisSummaryMap");
	String totAverage = (String)request.getAttribute("totAverage");
	
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String strUITheme = CF.getStrUI_Theme();
	//EncryptionUtils EU = new EncryptionUtils();//Created by Dattatray Date : 21-07-21 Note: Encryption
	
    /* Map<String, List<List<String>>> hmElementAttribute = (Map<String, List<List<String>>>) request.getAttribute("hmElementAttribute");
    Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap"); */
 /* ====start parvez on 27-10-2022===== */    
    Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
    Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
/*  ====end parvez on 27-10-2022===== */    
     %>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script> 

<div class="box box-widget widget-user widget-user1" <% if(strUITheme != null && strUITheme.equals("2")) { %> style="padding-top: 14%;" <% } %>> 
    <!-- <div class="widget-user-header bg-aqua-active" style="padding:0px;"> -->
    <!-- ====start parvez on 27-10-2022===== -->   
        <%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'> --%>
        <%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
			List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
			//System.out.println("alPhotoInner=="+alPhotoInner+"--size=="+alPhotoInner.size());
		%>
		<div class="widget-user-header bg-aqua-active" style="padding:0px; height: 190px !important;">
			<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>' style="padding:0px; height: auto !important;">
		<% } else{ %>
		<div class="widget-user-header bg-aqua-active" style="padding:0px;">
			<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
		<% } %>
	<!-- ====end parvez on 27-10-2022===== -->		
        <h3 class="widget-user-username"><span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span>
        <!-- Created by Dattatray Date 21-07-21 Note: empId encryption -->
        <span style="float: right;"><a href="MyProfile.action?empId=<%=hmEmpProfile.get("EMP_ID") %>" title="Go to FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
        </h3>
        <h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
    </div>
    <div class="widget-user-image">
    	<%if(docRetriveLocation==null) { %>
    		<img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
    	<% } else { %>
    		<img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmEmpProfile.get("EMP_ID")+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
    	 <% } %>
    </div>
    <div class="box-footer" style="padding-top: 4%;">
        <div class="row">
            <div class="col-sm-12">
                <div class="description-block">
                    <h5 class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> </h5>
                    <span class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></span><br/>
                    <span class="description-text">Date of Joining: <strong><%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></strong></span>
                    <p class="description-text">
	                    <%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
	                   	  You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong>
	                  	<% } else { %>
	                  	  You don't have a reporting manager.
	                  	<% } %>
                    </p>
                     <% if(alSkills!=null && alSkills.size()!=0) { %>
	                        <% for(int i=0; i<alSkills.size(); i++) { 
	                        if(i%5 == 0){%>
	                        <span class="label label-info">
	                        <% }else if(i%5 == 1){ %> 
	                        <span class="label label-success">
	                        <% }else if(i%5 == 2){ %> 
	                        <span class="label label-primary">
	                        <% }else if(i%5 == 3){ %> 
	                        <span class="label label-warning">
	                        <% }else{ %>
	                        <span class="label label-danger">
	                        <% } %>
	                        <strong><%=(i<alSkills.size()-1) ? ((List)alSkills.get(i)).get(1) + "" : ((List)alSkills.get(i)).get(1)%></strong></span>&nbsp;
	                        <% } %>
	                <% } %> 
	                <br>
	                <% if(alSkills!=null && alSkills.size()!=0) { %>    
	                   <span id="skillPrimary"></span><br>
	                <% } %>
	                <%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %> 
	                <span class="description-text">Overall:</span>
                    <span id="skillPrimaryAttrib"></span>
	                <% for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
                    List<String> innerList = elementouterList.get(i);
                    double 	dblElementAggregate = 0.0d;
                   		dblElementAggregate = uF.parseToDouble(hmAnalysisSummaryMap.get(innerList.get(0).trim()));
                    %>	
                    	<br>
                    	<span class="description-text"><%=innerList.get(1)%>:</span>
                        <span id="starPrimary<%=i%>"></span>
                        <script type="text/javascript">
		                    	$('#starPrimary<%=i%>').raty({
		                    		readOnly: true,
		                    		start: <%=dblElementAggregate > 0 ? (dblElementAggregate / 20) + "" : "0"%>,
		                    		half: true,
		                    		targetType: 'number'
		                    	});
		                </script>
                    
                    <% } %>
                	<% } %>
                    <%-- <span class="description-text">You report to <strong>Anuja   Deodhar</strong> </span> --%>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $('#default').raty();
     <%double dblPrimary = 0;	
        if(alSkills!=null && alSkills.size()!=0) { 
                    	for(int i=0; i<alSkills.size(); i++) {
                    		List alInner = (List)alSkills.get(i); %>
    			<%-- $('#star<%=i%>').raty({
        readOnly: true,
        start: <%=uF.parseToDouble((String)alInner.get(2))/2%>,
        half: true
        }); --%>
    		<%
        if(i==0){dblPrimary = uF.parseToDouble((String)alInner.get(2))/2;}
        }
        } %>
    $('#skillPrimary').raty({
    	  readOnly: true,
    	  start:    <%=dblPrimary%>,
    	  half: true
    	});
    	
    <%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
        double dblScorePrimary = 0;
        //, aggregeteMarks = 0, totAllAttribMarks = 0;
        /* int count = 0;
        
        for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
        	List<String> innerList = elementouterList.get(i);
        	List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
        	for (int j = 0; attributeouterList1 != null && j < attributeouterList1.size(); j++) {
        		List<String> attributeList1 = attributeouterList1.get(j);
        		totAllAttribMarks += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
        		count++;
        	}
        } */
        //aggregeteMarks = totAllAttribMarks / count;
        
        dblScorePrimary = uF.parseToDouble(totAverage) / 20; 
        %>
    $('#skillPrimaryAttrib').raty({
    	  readOnly: true,
    	  start:    <%=dblScorePrimary%>,
    	  half: true
    	});
    
    
    <% } %>
</script>