<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Requirement Request" name="title" />
    </jsp:include> --%>
<%
    List<String> panelNameList = (List) request.getAttribute("panelNameList");
    %> 
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>  --%>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
<script type="text/javascript">

      	
    function checkAdd(checked, value, recruitId, chkboxID){
    //alert("checked : "+checked +" value : "+value+" recruitId : "+recruitId);
    if(confirm("Are you sure, you want to shortlist this application?")) {
    	getContent('panelCandiDiv', 'GetSelectedCandidateList.action?chboxShortlist='+checked+'&selectedEmp='+value+'&recruitId='+recruitId);
    }else{
    	document.getElementById(chkboxID).checked = false;
    }
    }
    
    function checkAdd1(checked, value, recruitId, chkboxID){
    //alert("checked : "+checked +" value : "+value+" recruitId : "+recruitId);
    if(confirm("Are you sure, you want to reject this application?")) {
    	getContent('panelCandiDiv', 'GetSelectedCandidateList.action?chboxReject='+checked+'&selectedEmp='+value+'&recruitId='+recruitId);
    }else{
    	document.getElementById(chkboxID).checked = false;
    }
    }
    
    /* function checkReset() {	
    alert("checkReset");
    dojo.event.topic.publish("checkReset");
    } */
    
    function checkReset(empId, recruitId){
    //alert(" empId : "+empId+" recruitId : "+recruitId);
    if(confirm("Are you sure, you want to reset this application?")) {
    	getContent('panelCandiDiv', 'GetSelectedCandidateList.action?resetCandi=reset&selectedEmp='+empId+'&recruitId='+recruitId);
    }
    } 
    
    function sendNotification(){
    //alert("rid .....");
    var rid = document.getElementById("recruitId").value;
    //alert("rid "+rid);
    
    $.ajax({
		type :'POST',
		url  :'SendShortlistApplicationNotification.action?recruitId='+rid,
		cache:true/* ,
		success : function(result) {
			$("#subSubDivResult").html(result);
		} */ 
	});
    
	$.ajax({
		url: 'Applications.action?recruitId='+rid,
		cache: true,
		success: function(result){
			$("#subSubDivResult").html(result);
		}
	});
	
    /* window.location = "SendShortlistApplicationNotification.action?recruitId="+rid; */
    
    /* window.setTimeout(function() {  
    parent.window.location="Applications.action";
    }, 500);  */
    }
    
    
    /* function submitForm(formName){
    //var dialogEdit = '#addCandidateDiv';
    alert("formName ");
    document.getElementById(formName).submit();
    $('#addCandidateDiv').dialog('close');
    alert("dialogEdit ");
    
    } */
    
    function copyAddress(obj){
    //alert("obg = "+obj);
    if(obj.checked){
    	
    	var sel=document.getElementById("countryTmp");
    	for(var i = 0, j = sel.options.length; i < j; ++i) {
            if(sel.options[i].innerHTML === document.getElementById("country").options[document.getElementById("country").selectedIndex].text) {
               sel.selectedIndex = i;
               break;
            }
        }
    	
    	sel=document.getElementById("stateTmp");
    	for(var i = 0, j = sel.options.length; i < j; ++i) {
            if(sel.options[i].innerHTML === document.getElementById("state").options[document.getElementById("state").selectedIndex].text) {
               sel.selectedIndex = i;
               break;
            }
        }
    	
    	document.getElementById("frmPersonalInfo_empAddress1Tmp").value = document.getElementById("frmPersonalInfo_empAddress1").value;
    	document.getElementById("frmPersonalInfo_empAddress2Tmp").value = document.getElementById("frmPersonalInfo_empAddress2").value;
    	document.getElementById("frmPersonalInfo_cityTmp").value = document.getElementById("frmPersonalInfo_city").value;
    	document.getElementById("frmPersonalInfo_empPincodeTmp").value = document.getElementById("frmPersonalInfo_empPincode").value;
    	
    	
    	
    }else{
    	document.getElementById("frmPersonalInfo_empAddress1Tmp").value = '';
    	document.getElementById("frmPersonalInfo_empAddress2Tmp").value = '';
    	document.getElementById("frmPersonalInfo_cityTmp").value = '';
    	document.getElementById("frmPersonalInfo_empPincodeTmp").value = '';
    	
    	var sel=document.getElementById("countryTmp");
    	for(var i = 0, j = sel.options.length; i < j; ++i) {
            if(sel.options[i].innerHTML === document.getElementById("country").options[0].text) {
               sel.selectedIndex = i;
               break;
            }
        }
    	
    	sel=document.getElementById("stateTmp");
    	for(var i = 0, j = sel.options.length; i < j; ++i) {
            if(sel.options[i].innerHTML === document.getElementById("state").options[0].text) {
               sel.selectedIndex = i;
               break;
            }
        }
    }
    
    }
</script>

<!-- <iframe  src="GetSelectedCandidateList.action"><p>Your browser does not support iframes.</p></iframe> -->
<div id="panelCandiDiv">
    <s:hidden name="recruitId" id="recruitId" ></s:hidden>
    <%
        List<List<String>> allCandidateList = (List<List<String>>)request.getAttribute("allCandidateList");
        //Map<String, String> sltEmpDtCmprHm = (Map<String, String>) request.getAttribute("sltEmpDtCmprHm");
        Map<String, String> hmCandToEmp = (Map<String, String>) request.getAttribute("hmCandToEmp");
        if(hmCandToEmp == null) hmCandToEmp = new HashMap<String, String>();
        %>
    <%-- <s:form theme="simple" action="GetCandidateEmployeeList" method="POST" cssClass="formcss" enctype="multipart/form-data"> --%>
    <div style="float: left; width: 100%;">
        <div style="width: 20%;">
            <!-- <a href="javascript:void(0)" onclick="addCandidateModePopup('<%=request.getAttribute("recruitId") %>')"><input type="button" class="btn btn-primary" onclick="addCandidateModePopup('<%=request.getAttribute("recruitId") %>')" value="Add New Candidate"> </a> -->
            <input type="button" class="btn btn-primary" onclick="addCandidateModePopup('<%=request.getAttribute("recruitId") %>')" value="Add New Candidate"/>
        </div>
    </div>
    <div style="float: left; width: 52.5%; font-weight: bold;">New Applications</div>
    <div style="float: left; width: 45%; font-weight: bold;">Shortlisted / Rejected Applications</div>
    <div id="empidlist" style="overflow-y: auto; height: 200px; margin-top: 20px;border: 2px solid #F1F1F1; width: 50%;float:left;">
        <table class="table table-bordered" width="100%">
            <tr>
                <th nowrap="nowrap" width="27%">Approve | Reject</th>
                <th width="60%" align="center">Name</th>
                <!-- <th width="18%" align="center">Education</th> -->
                <th width="13%" align="center">Criteria Rating</th>
            </tr>
            <%
                UtilityFunctions uF=new UtilityFunctions();
                //Map<String,String> hmWlocation=(Map<String,String>)request.getAttribute("hmWlocation");	
                	for(int i=0; allCandidateList !=null && i<allCandidateList.size();i++){
                	List<String> innerList = allCandidateList.get(i);
                	 String empID=innerList.get(0);
                       String empName=innerList.get(1);
                       String education=innerList.get(2);
                       String jobCode=innerList.get(3);
                       String skills=innerList.get(4);
                       String candiExp=innerList.get(5);
                       String strStars = innerList.get(6);
                       String yesno="";
                       
                          	 %>
            
            <tr >
                <td nowrap="nowrap" style="vertical-align: text-top;text-align: center;">
                    <input type="checkbox" name="strCandiShortlist" id="strCandiShortlist<%=i%>" 
                        onclick="<%if(hmCandToEmp.containsKey(empID.trim())){%>alert('<%=hmCandToEmp.get(empID.trim()) %>'); this.checked=false;<%} else {%>checkAdd(this.checked,this.value,'<s:property value="recruitId"/>','strCandiShortlist<%=i%>');<%}%>"
                    value="<%=empID %>">
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <input type="checkbox" name="strCandiReject" id="strCandiReject<%=i%>" 
                        onclick="<%if(hmCandToEmp.containsKey(empID.trim())){%>alert('<%=hmCandToEmp.get(empID.trim()) %>'); this.checked=false;<%} else {%>checkAdd1(this.checked,this.value,'<s:property value="recruitId"/>','strCandiReject<%=i%>');<%}%>"
                    value="<%=empID %>">
                </td>
                <td>
                    <a href="javascript: void(0)" onclick="openCandidateProfilePopup('<%=empID%>','','')"><%=empName %></a><br/>
                    <%-- <a href="CandidateMyProfile.action?CandID=<%=empID %>"><%=empName %></a> --%>
                    <div style="width: 100%; font-size: 11px; margin-top: -2px"> Edu:- <%=uF.showData(education,"Not specified")%><br/></div>
                    <div style="width: 100%; font-size: 11px; margin-top: -5px">Skills:- <%=uF.showData(skills,"Not specified")%><br/></div>
                    <div style="width: 100%; font-size: 11px; margin-top: -5px">Exp:- <%=uF.showData(candiExp,"Not specified")%></div>
                </td>
                <!-- <td></td> --> 
                <td style="vertical-align: text-top;">
                    <div style="width: 100%; float: left;"> <%=strStars %>/5</div>
                    <div id="starPrimary<%=empID%>" style="width: 100%;"></div>
                    <script type="text/javascript">
                    		$("#starPrimary<%=empID%>").raty({
  		                      readOnly: true,
  		                      start:	<%=strStars %> ,
  		                      half: true,
  		                      targetType: 'number'
  		                	});
		            </script>
                </td>
            </tr>
            <%} if(allCandidateList==null || allCandidateList.isEmpty() || allCandidateList.equals("")){%>
            <tr>
                <td colspan="4">
                    <div class="nodata msg" style="width: 95%">
                        <span>No Employee within selection</span>
                    </div>
                </td>
            </tr>
            <%} %>
        </table>
    </div>
    <div id="idEmployeeInfo" style="border: 2px solid rgb(204, 204, 204); float: left; left: 70%; top: 46px;
        width: 46%; overflow-y: auto; padding: 4px; margin-top: 20px; height: 190px; margin-left: 20px;">
        <div align="center" style="width:50%;float:left;border-bottom:1px solid black;"><b>Shortlisted </b></div>
        <div align="center" style="width:50%;float:left;border-bottom:1px solid black;"><b>Rejected</b></div>
        <%	List<List<String>> selectCandidateList = (List<List<String>>) request.getAttribute("selectCandidateList");
            List<String> rejectCandidateList = (List<String>) request.getAttribute("rejectCandidateList");
            
            //List<String> selectCandidateIds = (List<String>) request.getAttribute("selectCandidateIds");
            List<String> rejectCandidateIds = (List<String>) request.getAttribute("rejectCandidateIds");
            %>
        <table border="0" width="48%" style="float:left">
            <% if (selectCandidateList != null) {
                for (int i = 0; i < selectCandidateList.size(); i++) {
                	List<String> innerList = selectCandidateList.get(i);
                %>
            <tr>
                <%-- <td nowrap="nowrap" style="font-weight: bold; vertical-align: text-top;"><%=i + 1%>.&nbsp;</td> --%>
                <td nowrap="nowrap" align="left"><strong><%=i + 1%>.</strong>&nbsp;<%=innerList.get(0)%>
                	<% if(uF.parseToInt(innerList.get(2))==0) { %>
	                    <a href="javascript: void(0)" onclick="checkReset('<%=innerList.get(1)%>','<s:property value="recruitId"/>');" title="Remove Shortlisted Candidate">
	                    <img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
                    <% } else { %>
                    	<a href="javascript: void(0)" onclick="alert('This candidate is under interview process, you can not remove this candidate form shortlisted.');" title="Remove Shortlisted Candidate">
	                    <img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
                    <% } %>
                </td>
            </tr>
            <%}
                } else {%>
            <tr>
                <td>
                    <div class="nodata msg" style="width: 90%">
                        <span>No Candidate shortlisted</span>
                    </div>
                </td>
            </tr>
            <%}%>
        </table>
        <table border="0" width="50%" style="float:left">
            <%
                if (rejectCandidateList != null) {
                	for (int i = 0; i < rejectCandidateList.size(); i++) {
                		%>
            <tr>
                <%-- <td nowrap="nowrap" style="font-weight: bold; vertical-align: text-top;"><%=i + 1%>.&nbsp;</td> --%>
                <td nowrap="nowrap" align="left"><strong><%=i + 1%>.</strong>&nbsp;<%=rejectCandidateList.get(i)%>
                    <a href="javascript: void(0)" onclick="checkReset('<%=rejectCandidateIds.get(i)%>','<s:property value="recruitId"/>');" title="Remove Rejected Candidate">
                    <img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
                </td>
            </tr>
            <%}
                } else { %>
            <tr>
                <td>
                    <div class="nodata msg" style="width: 90%">
                        <span>No Candidate rejected</span>
                    </div>
                </td>
            </tr>
            <%}%>
        </table>
    </div>
</div>
<div style="margin-top: 35px; float:left; width: 100%">
    <div align="center">
        <input type="button" value="Send Notification to New Candidate" class="btn btn-primary" onclick="sendNotification();" />
        <%-- <s:hidden name="type" value="elist"></s:hidden> --%>
        <%-- <s: name="submit" cssClass="btn btn-primary" value="Save" align="center" /> --%> 
    </div>
</div>
<div id="addCandidateDiv"></div>
