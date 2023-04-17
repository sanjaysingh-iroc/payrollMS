<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script src="<%= request.getContextPath()%>/js/jquery-1.7.2.min.js" type="text/javascript"></script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery-ui-1.8.6.custom.min.js"> </script>

<style>
.compIndustry {
	color: #333333;
    font-size: 12px;
    font-weight: normal;
    line-height: 15px;
}

.compAdd { 
	color: #BBBBBB;
    font-size: 11px;
    font-weight: normal;
    line-height: 15px;
}

.compContact {
	color: #68AC3B;
    font-size: 12px;
    font-weight: normal;
    line-height: 15px;
}

.anaAttrib1 {
	font-size: 12px;
	font-family: digital;
	color: #3F82BF;
	font-weight: bold;
	text-align: center;
	height: 22px;
}
</style>

<%-- 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Customer" name="title"/> 
</jsp:include>
 --%>
 
<script>
	
	function showAllPeople(value) {
		//alert(value);
		var status = document.getElementById("hidePeopleDivStatus"+value).value;
		if(status == '0') {
			document.getElementById("hidePeopleDivStatus"+value).value = "1";
			document.getElementById("peopleDiv"+value).style.display = "block";
			document.getElementById("PuparrowSpan"+value).style.display = "block";
			document.getElementById("PdownarrowSpan"+value).style.display = "none";
		} else {
			document.getElementById("hidePeopleDivStatus"+value).value = "0";
			document.getElementById("peopleDiv"+value).style.display = "none";
			document.getElementById("PuparrowSpan"+value).style.display = "none";
			document.getElementById("PdownarrowSpan"+value).style.display = "block";
		}
	}
	
	
	function executeActions(value, custId, clientId) {
		if(value == '1') {
			addUpdateClientOrSPOC(clientId, custId, 'SPOC', 'E');
			//editCustomer(custId, clientId);
		} else if(value == '2') {
			if(confirm('Are you sure, you want to delete this customer?')) {
				deleteCustomer(custId);
			}
			
		} else if(value == '3') {
			if(confirm('Are you sure, you want to resend access to this customer?')) {
				resendCustomerAccess(custId);
			}
			
		} else if(value == '4') {
			if(confirm('Are you sure, you want to disable access for this customer?')) {
				disableCustomerAccess(custId);
			}
			
		} else if(value == '5') {
			viewCustLoginDetails(clientId, custId);
		}
		if(value != '5') {
			document.getElementById("custActions"+custId).selectedIndex = '0';
			document.getElementById("loginDataDiv"+clientId+"_"+custId).style.display = "none";
			document.getElementById("status"+clientId+"_"+custId).value = "0";
		}
	}
	
	
	function deleteCustomer(custId) {
		var action = 'AddNewCustomer.action?operation=D&ID='+custId;
		window.location = action;
	}
	
	function resendCustomerAccess(custId) {
		var action = 'AddNewCustomer.action?operation=RA&ID='+custId;
		window.location = action;
	}
	
	function disableCustomerAccess(custId) {
		var action = 'AddNewCustomer.action?operation=DA&ID='+custId;
		window.location = action;
	}
	
	
	
	function viewCustLoginDetails(clientId, custId) {
		var status = document.getElementById("status"+clientId+"_"+custId).value;
		if(status == '0') {
			document.getElementById("loginDataDiv"+clientId+"_"+custId).style.display = "block";
			document.getElementById("status"+clientId+"_"+custId).value = "1";
		} else {
			document.getElementById("loginDataDiv"+clientId+"_"+custId).style.display = "none";
			document.getElementById("status"+clientId+"_"+custId).value = "0";
		}
	}
	
	
	function resetAndSendPassword(custId, type) {
		var action = 'Customer.action?type='+type+'&custId='+custId;
		window.location = action;
	}
	
	function loadMore(proPage, minLimit) {
		var action = 'Customer.action?proPage='+proPage+'&minLimit='+minLimit;
		window.location = action;
	}
	
	
	function addUpdateClientBrand(clientId, clientBrandId, operation) {
		
		if(clientId != null && clientId != 'null') {
			if(document.getElementById("hidePeopleDivStatus"+clientId)) {
				document.getElementById("hidePeopleDivStatus"+clientId).value = "0";
			}
			if(document.getElementById("peopleDiv"+clientId)){
				document.getElementById("peopleDiv"+clientId).style.display = "none";
			}
			if(document.getElementById("PuparrowSpan"+clientId)){
				document.getElementById("PuparrowSpan"+clientId).style.display = "none";
			}
			if(document.getElementById("PdownarrowSpan"+clientId)) {
				document.getElementById("PdownarrowSpan"+clientId).style.display = "block";
			}
		}
		document.getElementById("addClientOrSPOCDiv").style.display = "block";
		$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#addClientOrSPOCDiv");
			if(operation == 'E') {
				getContent('addClientOrSPOCDiv', 'AddClientBrand.action?operation='+operation+'&clientId='+clientId+'&clientBrandId='+clientBrandId);
			} else {
				getContent('addClientOrSPOCDiv', 'AddClientBrand.action?operation='+operation+'&clientId='+clientId);
			}
	}
	
	
	function addUpdateClientOrSPOC(clientId, spocId, type, operation) {
		
		if(clientId != null && clientId != 'null') {
			if(document.getElementById("hidePeopleDivStatus"+clientId)) {
				document.getElementById("hidePeopleDivStatus"+clientId).value = "0";
			}
			if(document.getElementById("peopleDiv"+clientId)){
				document.getElementById("peopleDiv"+clientId).style.display = "none";
			}
			if(document.getElementById("PuparrowSpan"+clientId)){
				document.getElementById("PuparrowSpan"+clientId).style.display = "none";
			}
			if(document.getElementById("PdownarrowSpan"+clientId)) {
				document.getElementById("PdownarrowSpan"+clientId).style.display = "block";
			}
		}
		document.getElementById("addClientOrSPOCDiv").style.display = "block";
		$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#addClientOrSPOCDiv");
		if(type=='C') {
			if(operation == 'E') {
				getContent('addClientOrSPOCDiv', 'AddClient.action?operation='+operation+'&ID='+clientId);
			} else {
				getContent('addClientOrSPOCDiv', 'AddClient.action');
			}
		} else if(type == 'SPOC') {
			if(operation == 'E') {
				getContent('addClientOrSPOCDiv', 'AddNewCustomer.action?operation='+operation+'&clientId='+clientId+'&ID='+spocId);
			} else {
				getContent('addClientOrSPOCDiv', 'AddNewCustomer.action?operation='+operation+'&clientId='+clientId);
			}
		}
	}
	
	function closeForm(pageType) {
		document.getElementById("addClientOrSPOCDiv").innerHTML = "";
		document.getElementById("addClientOrSPOCDiv").style.display = "none";
		//window.location = "ClientReport.action";
	}
	
	
	
	//var cnt=0;
	
	
</script>

        
		<%
			UtilityFunctions uF = new UtilityFunctions();
			String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
			String strEmpId = (String)session.getAttribute(IConstants.EMPID);
		
			Map<String, List<String>> hmClientReport = (Map<String, List<String>>)request.getAttribute("hmClientReport"); 
			Map<String, String> hmClientsProjects = (Map<String, String>)request.getAttribute("hmClientsProjects");
			if(hmClientsProjects == null) hmClientsProjects = new HashMap<String, String>();
			
			Map<String, List<List<String>>> hmClientContactDetailsReport = (Map<String, List<List<String>>>)request.getAttribute("hmClientContactDetailsReport");
			Map<String, List<String>> hmClientAddressDetailsReport = (Map<String, List<String>>)request.getAttribute("hmClientAddressDetailsReport");
			
			Map<String, String> hmClientDeliveredProCnt = (Map<String, String>)request.getAttribute("hmClientDeliveredProCnt");
			Map<String, String> hmCustDeliveredProCnt = (Map<String, String>)request.getAttribute("hmCustDeliveredProCnt");
			
			Map<String, String> hmClientWorkingProCnt = (Map<String, String>)request.getAttribute("hmClientWorkingProCnt");
			Map<String, String> hmCustWorkingProCnt = (Map<String, String>)request.getAttribute("hmCustWorkingProCnt");
			
			Map<String, String> hmClientCreatedProCnt = (Map<String, String>)request.getAttribute("hmClientCreatedProCnt");
			Map<String, String> hmCustCreatedProCnt = (Map<String, String>)request.getAttribute("hmCustCreatedProCnt");
			
			Map<String, String> hmClientWoringTeamSize = (Map<String, String>)request.getAttribute("hmClientWoringTeamSize");
			
			Map<String, String> hmClientRaisedProCnt = (Map<String, String>)request.getAttribute("hmClientRaisedProCnt");
			Map<String, String> hmClientwiseBillProCnt = (Map<String, String>)request.getAttribute("hmClientwiseBillProCnt");
			
			Map<String, String> hmClientPaidCnt = (Map<String, String>)request.getAttribute("hmClientPaidCnt");
			Map<String, String> hmCustomerDocSize = (Map<String, String>)request.getAttribute("hmCustomerDocSize");
			
			String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
			
			String proCount = (String)request.getAttribute("proCount");
			
			String sbData = (String) request.getAttribute("sbData");
			String strSearch = (String) request.getAttribute("strSearch");
			
			String proPage = (String)request.getAttribute("proPage");
			String minLimit1 = (String)request.getAttribute("minLimit");
			
			String customerId = (String)request.getAttribute("customerId");
			String customerName = (String)request.getAttribute("customerName");
			
		%>
		
        <!-- Main content -->
        <section class="content">
          <!-- title row -->
          
			<div><%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
				<% session.removeAttribute("MESSAGE"); %>
			</div>
        
        <div class="col-lg-12 col-md-12 no-padding box" style="margin-bottom: 7px; padding: 5px !important;">
        	<div class="col-lg-4 col-md-4 no-padding">
	        	
				<!-- <input type="button" value="Search" class="btn btn-primary" onclick="getSearchProjectNameList();"> -->
				<s:form name="frmSearch" id="frmSearch" action="Customer" theme="simple">
	            	<input type="text" id="strSearch" class="form-control" name="strSearch" placeholder="Search Customers" value="<%=uF.showData(strSearch, "") %>"/> 
	            	<input type="submit" value="Search" class="btn btn-primary"/> <!-- onclick="submitForm();" -->
				    <script type="text/javascript">
						$( "#strSearch" ).autocomplete({
							source: [ <%=uF.showData(sbData,"") %> ]
						});
					</script>
					
				</s:form>
			</div>
        </div>
        
			<div class="row">
				<div class="col-md-4">
				<div class="nav-tabs-custom">
	                <div class="tab-content box-body" style="min-height: 600px;">
	                <div class="active tab-pane" id="#projects">
	                <ul class="products-list product-list-in-box" style="min-height: 80px;">
	                  
	                <%
	                	Set setClientMap = hmClientReport.keySet();
		      			Iterator it = setClientMap.iterator();

		      			while(it.hasNext()) {
		      				String strClientId = (String)it.next();
		      				List alClients = (List)hmClientReport.get(strClientId);
		      				if(alClients==null)alClients=new ArrayList();
	                %>

		                    <li class="item">
							<div class="product-img">
								<span style="float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px;" id="proLogoDiv">
				                    <% String clntLogo = "";
				                    	if(alClients.get(1) != null && alClients.get(1).toString().length()>0) {
				                    		clntLogo = alClients.get(1).toString().substring(0, 1);
					                    }
				                   	%>
				                   	<span><%=clntLogo %></span>
								</span>
							</div>
							
		                    <div class="product-info" style="margin-left: 45px;">
			                    	
		                    	<div style="float: left; margin-right: 10px; padding-left:5px; width: 100%;">
				                    <div style="float: left; width: 100%;">
				                    	<span style="float: left; width: 100%;">
											<a <% if(uF.parseToInt(customerId) == uF.parseToInt(strClientId)) { %> class="activelink" <% } %> href="Customer.action?customerId=<%=strClientId %>&proPage=<%=proPage %>&minLimit=<%=minLimit1 %>"><%=alClients.get(1) %></a>
				                    	</span>
										<span style="float: left; width: 100%;"><%=alClients.get(3) %> </span>
										<%
											List alAddress = (List)hmClientAddressDetailsReport.get(strClientId);
											if(alAddress == null)alAddress = new ArrayList();
											StringBuilder sbAddress = null;		
											for(int i=0; i<alAddress.size(); i++) {
												if(sbAddress == null) {
													sbAddress = new StringBuilder();
													sbAddress.append(alAddress.get(i+0));
												} else {
													sbAddress.append(", "+alAddress.get(i+0));
												}
											}
											if(sbAddress == null) {
												sbAddress = new StringBuilder();
											}	
										%>
										<span style="float: left; width: 100%;"><%=sbAddress.toString() %> </span>
									</div>
			                    </div>
							</div>
		                      
	                    </li><!-- /.item -->
                    <% } %>
                    
                    <%if(hmClientReport == null || hmClientReport.size() == 0) { %>
                    	<li class="item" style="padding: 2px 0px;">
							<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No clients available.</div>
						</li>
					<% } %>
	                  </ul>
	                  
	                  <div class="box-footer text-center">
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
	                  
	                </div>
	                </div>
				</div>
				</div>
            	</div>
				
				
				<% 
					String strProId = (String) request.getAttribute("proId");
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
				%>
				
				<div class="col-md-8"> <div style="float: right; padding: 5px;"><a href="javascript:void(0);" onclick="addUpdateClientOrSPOC('<%=customerId %>', '', 'C', 'A');">+ Add New Client</a></div></div>
				<div class="col-md-8">
				<div class="box box-info">
	                <div class="box-header with-border">
	                  <h3 class="box-title"><%=uF.showData(customerName, "") %></h3>
	                  <div class="box-tools pull-right">
	                    <button data-widget="collapse" class="btn btn-box-tool"><i class="fa fa-minus"></i></button>
	                    <button data-widget="remove" class="btn btn-box-tool"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                <div class="box-body">
	                <% if(uF.parseToInt(customerId) > 0) { %>
		           	<div style="float: left; width: 100%;">
						<div style="float: left; width: 10%; text-align: center;">
							<div>Working</div>
							<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(hmClientWorkingProCnt.get(customerId) ,"0") %> </span></div>
						</div>
						
						<div style="float: left; width: 10%; text-align: center;">
							<div>Delivered</div>
							<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(hmClientDeliveredProCnt.get(customerId) ,"0") %> </span></div>
						</div>
						
						<div style="float: left; width: 10%; text-align: center; border-right: 1px solid #CCCCCC;">
							<div>Created</div>
							<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(hmClientCreatedProCnt.get(customerId) ,"0") %> </span></div>
						</div> 
						
						<div style="float: left; width: 9%; text-align: center;">
							<div>Paid</div>
							<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(hmClientPaidCnt.get(customerId) ,"0") %> </span></div>
						</div>
						
						<div style="float: left; width: 9%; text-align: center;">
							<div>Raised</div>
							<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(hmClientRaisedProCnt.get(customerId) ,"0") %> </span></div>
						</div>
						
						<div style="float: left; width: 9%; text-align: center;">
							<div>Pending</div>
							<%
								String strBillRaisedCnt = hmClientRaisedProCnt.get(customerId);
								String strBillAllCnt = hmClientwiseBillProCnt.get(customerId);
								int intPending = uF.parseToInt(strBillAllCnt) - uF.parseToInt(strBillRaisedCnt);
							%>
							<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(intPending+"" ,"0") %> </span></div>
						</div>
						
						<div style="float: left; width: 18%; text-align: center; padding-left: 5px; border-left: 1px solid #CCCCCC;">
							<div>Team Size Working</div>
							<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(hmClientWoringTeamSize.get(customerId) ,"0") %> </span></div>
						</div>
						
						<div style="float: right; width: 9%;">
							<a href="javascript:void(0)" class="fa fa-edit" onclick="addUpdateClientOrSPOC('<%=customerId %>', '', 'C', 'E');" title="Edit Client">&nbsp;</a> <!-- class=\"fa fa-fw fa-remove\"  -->
							<% if(hmClientsProjects.get(customerId) != null) { %>
								<a href="javascript:void(0)" class="fa fa-trash-o" style="color: red;" onclick="alert('You can not delete this client, this client has projects.')">&nbsp;</a>
							<% } else { %>
								<a href="AddClient.action?operation=D&ID=<%=customerId %>" class="fa fa-trash-o" style="color: red;" onclick="return confirm('Are you sure you wish to delete this client?')" title="Delete Client">&nbsp;</a>
							<% } %>
						</div>
					</div>
					
					<div style="float: left; width: 100%;">
						<% 
						List<List<String>> clientPocList = hmClientContactDetailsReport.get(customerId);
						//System.out.println("clientPocList ===>> " + clientPocList);
						if(clientPocList != null && clientPocList.size() > 0) {
						%>
						<div style="float: left; width: 100%;"> <!-- class="compContact" -->
							<input type="hidden" name="hidePeopleDivStatus" id="hidePeopleDivStatus<%=customerId %>" value = "0"/>
							<a href="javascript: void(0);" style="font-weight: normal;" onclick="showAllPeople('<%=customerId %>');">
								<span id="PdownarrowSpan<%=customerId %>" style="float: left; margin-right: 3px;"><i class="fa fa-angle-down" aria-hidden="true" style="width: 12px;"></i></span>
								<span id="PuparrowSpan<%=customerId %>" style="display: none; float: left; margin-right: 3px;"><i class="fa fa-angle-up" aria-hidden="true" style="width: 12px;"></i></span>
								<strong><%=clientPocList.size()%></strong> people in this company.
							</a>
						</div>
						<% } else { %>
							<div style="float: left; width: 100%;">No people in this company. Please click here to <a href="javascript:void(0);" onclick="addUpdateClientOrSPOC('<%=customerId %>', '', 'SPOC', 'A');">+ Add new SPOC</a>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="addUpdateClientBrand('<%=customerId %>', '', 'A');">+ Add another Subsidiary/ Brand</a>
							</div> <!-- class="compContact" -->
						<% } %>
						
							<div id="peopleDiv<%=customerId %>" style="display: none; float:left; width: 100%; border-top: 1px solid #CCCCCC; padding-top: 5px;">
								<% for(int i=0; clientPocList != null && i<clientPocList.size(); i++) {
									List<String> innerList = clientPocList.get(i);
								%>
								<div style="float: left; width: 100%; min-height: 35px;">
								<%if(docRetriveLocation==null) { %>
									<img height="30" width="30" class="lazy" style="float: left; margin-right: 10px; border: 1px lightgray solid;" src="userImages/avatar_photo.png" data-original="<%=IConstants.IMAGE_LOCATION + ((innerList.get(6)!=null && !innerList.get(6).toString().equals("")) ? innerList.get(6):"avatar_photo.png") %>" />
								<% } else { 
									String contactPersonImage = IConstants.IMAGE_LOCATION + ((innerList.get(7)!=null && !innerList.get(7).toString().equals("")) ? innerList.get(7):"avatar_photo.png");
									if(innerList.get(7)!=null && !innerList.get(7).toString().equals("")) {
										contactPersonImage = docRetriveLocation +IConstants.I_CUSTOMER+"/"+customerId+"/"+IConstants.I_IMAGE+"/"+IConstants.I_CUSTOMER_SPOC+"/"+IConstants.I_22x22+"/"+((innerList.get(7)!=null && !innerList.get(6).toString().equals("")) ? innerList.get(7):"avatar_photo.png");
									}
								%>
			                     	<img height="30" width="30" class="lazy" style="float: left; margin-right: 10px; border: 1px lightgray solid;" src="<%=contactPersonImage %>" data-original="<%=contactPersonImage %>" />
			                    <% } %>
									<div style="float: left; width: 25%;">
										<div><%=innerList.get(1) %></div>
										<% if(innerList.get(11) !=null && !innerList.get(11).equals("")) { %>
										<a href="javascript:void(0);" onclick="addUpdateClientBrand('<%=customerId %>', '<%=innerList.get(12) %>', 'E');"><div class="compAdd" style="font-style: italic; font-weight: bold;"><%=innerList.get(11) %></div></a>
										<% } %>
										<div class="compAdd"><%=innerList.get(4) %></div>
									</div>
									<div style="float: left; width: 9%; text-align: center;">
										<div>Working</div>
										<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(hmCustWorkingProCnt.get(innerList.get(0)) ,"0") %> </span></div>
									</div>
									
									<div style="float: left; width: 9%; text-align: center;">
										<div>Delivered</div>
										<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(hmCustDeliveredProCnt.get(innerList.get(0)) ,"0") %> </span></div>
									</div>
									
									<div style="float: left; width: 9%; text-align: center;">
										<div>Created</div>
										<div><span class="anaAttrib1" style="font-size: 22px;"><%=uF.showData(hmCustCreatedProCnt.get(innerList.get(0)) ,"0") %> </span></div>
									</div> 
									
									<div style="float: left; width: 16%; text-align: center;">
										<div>Docs</div>
										<div style="font-weight: bold;"><%=uF.showData(hmCustomerDocSize.get(innerList.get(0)) ,"0 bytes") %> | 8 GB</div>
									</div> 
									<div style="float: left;">
										<select name="custActions" id="custActions<%=innerList.get(0) %>" style="width: 100px !important;" onchange="executeActions(this.value, '<%=innerList.get(0) %>', '<%=customerId %>')">
										<option value="">Actions</option>
										<option value="1">Edit</option>
										<option value="2">Delete</option>
										<%-- <% if(innerList.get(8) != null && innerList.get(8).equals("INACTIVE")) { %>
											<option value="3">Resend Access</option>
										<% } %>
										<% if(innerList.get(8) != null && innerList.get(8).equals("ACTIVE")) { %>
											<option value="4">Disable Access</option>
										<% } %>
										<option value="5">Login Details</option> --%>
										</select>
									</div>
									
								</div>
								
								<div id="loginDataDiv<%=customerId %>_<%=innerList.get(0) %>" style="display: none; float: right; width: 35%; min-height: 35px;">
									<input type="hidden" name="status<%=customerId %>_<%=innerList.get(0) %>" id="status<%=customerId %>_<%=innerList.get(0) %>" value="0">
									<div style="width: 90%; float: left; margin: 3px 0px 3px 25px; padding: 3px; border: 1px solid #CCCCCC;">
										<div style="float: left; width: 100%;"><div style="float: left; font-weight: bold; width: 30%;">Username: </div> <div style="float: left;"><%=innerList.get(9) %></div></div>
										<div style="float: left; width: 100%;"><div style="float: left; font-weight: bold; width: 30%;">Password: </div> <div style="float: left;"><%=innerList.get(10) %></div></div>
										<div style="float: left; width: 100%;">
											<a href="javascript:void(0)" onclick="resetAndSendPassword('<%=innerList.get(0) %>', 'RSC');" title="Click here for reset password">Reset</a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<a href="javascript:void(0)" onclick="resetAndSendPassword('<%=innerList.get(0) %>', 'SC');" title="Click here for send login details to Customer">Send mail to Customer</a>
										</div>
									</div>
								</div>
								<% } %>
								
								<div style="width: 100%; float: left;"><a href="javascript:void(0);" onclick="addUpdateClientOrSPOC('<%=customerId %>', '', 'SPOC', 'A');">+ Add another SPOC</a>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="addUpdateClientBrand('<%=customerId %>', '', 'A');">+ Add another Subsidiary/ Brand</a>
								</div>
							</div>
						
						</div>
						<% } %>
						 <!-- class="compContact"  -->
						
					</div>
					
	          		</div>
	           		
	           	</div>
				
				<div id="addClientOrSPOCDiv" class="col-md-8" style="display: none;"></div>
				
			</div><!-- /.row -->
				
        </section><!-- /.content -->
        
