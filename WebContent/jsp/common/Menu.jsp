<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.itextpdf.text.pdf.CFFFont"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="java.util.*"%>

<% 
UtilityFunctions uF = new UtilityFunctions(); 
List alParentNavL = (List) session.getAttribute("alParentNavL");
Map hmChildNavL = (Map) session.getAttribute("hmChildNavL");

List alParentNavR = (List) session.getAttribute("alParentNavR");
Map hmChildNavR = (Map) session.getAttribute("hmChildNavR");
Map hmNavigation = (Map) session.getAttribute("hmNavigation"); 
Map hmNavigationParent = (Map) session.getAttribute("hmNavigationParent");
Map hmNavigationAction = (Map) session.getAttribute("hmNavigationAction");
 

if(hmNavigation==null)hmNavigation=new HashMap(); 
if(hmNavigationParent==null)hmNavigationParent=new HashMap();
if(hmNavigationAction==null)hmNavigationAction=new HashMap();

String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
String strQuery = (String)request.getAttribute("javax.servlet.forward.query_string");

//System.out.println("strAction --->> " + strAction);
//System.out.println("strQuery --->> " + strQuery);

if(strAction!=null){
	strAction = strAction.replace(request.getContextPath()+"/","");
	//System.out.println("strAction 1 --->> " + strAction);
	if(strQuery!=null && strQuery.indexOf("NN")>=0){
		strAction = strAction+"?"+strQuery;
	}
	//System.out.println("strAction 2 --->> " + strAction);
}

String strNavId = (String)hmNavigation.get(strAction);
//System.out.println("strNavId --->> " + strNavId);
String strNavParentId = (String)hmNavigationParent.get(strAction);
String strAction1 = (String)hmNavigationAction.get(strNavParentId);
String strNavParentId1 = (String)hmNavigationParent.get(strAction1);

	if(strNavId == null) strNavId = "";
	StringBuilder sb = new StringBuilder();
	
	String PRODUCTTYPE = (String) session.getAttribute(IConstants.PRODUCT_TYPE);
	
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
%>

   <!-- Navigation div starts here -->
                <!-- <div class="nav">
                        <div id="leftnav" class="table"> -->
                              
                              
                              <%
                              boolean isSubNav = false;
                              if(alParentNavL!=null) {
                              
                              for(int i=0; alParentNavL!=null && i<alParentNavL.size(); i++) {
                            		Navigation n = (Navigation)alParentNavL.get(i);
                            		List alChild  = (List)hmChildNavL.get(n.getStrNavId());
                            		if(alChild==null) {
                            			alChild = new ArrayList();
                            		}
                            		if(uF.parseToInt(n.getStrChild())==0) {
                            			%>
                                        <% if(n.getStrAction() != null && n.getStrAction().equals("Hub.action")) { %> 
                                        	<hr style="margin: 0px 7px;border-top: 1px solid #4D4D4D;">
                                        <% } %>
                                        <li class="treeview" >
                                        <a class="<%=((n.getStrNavId().equalsIgnoreCase(strNavId))?"selectedL":"") %>" href="<%=n.getStrAction() %>">
                                        <%=((n.getStrNavId().equalsIgnoreCase(strNavId))?n.getStrLabelSelected():n.getStrLabelUnSelected()) %></a></li>
                            			<%
                            			
                            			if(n.getStrPosition() != null && !n.getStrPosition().equals("C")) {
	                            			if(n.getStrNavId().equalsIgnoreCase(strNavId)) {
	                            				sb.append("<li><a href=\""+n.getStrAction()+"\" style=\"color: #3C8DBC\"><i class=\"fa fa-dashboard\"></i> "+n.getStrLabel()+"</a></li>");
	                            			}
                            			} else if(n.getStrPosition() != null && n.getStrPosition().equals("C")) {
                            				if(n.getStrNavId().equalsIgnoreCase(strNavId)) {
                            					sb.append("<li><a href=\"MyDashboard.action?userscreen="+IConstants.ADMIN+"&navigationId=102&toPage=CS\" style=\"color: #3C8DBC\"><i class=\"fa fa-dashboard\"></i> Control Panel</a></li>");
                            				}
                            			}
                            			
                            		} else {
                            		%>
                            			<% if(n.getStrAction() != null && n.getStrAction().equals("Hub.action")) { %> 
                                        	<hr style="margin: 0px 7px;border-top: 1px solid #4D4D4D;">
                                        <% } %>
                                        <li class="treeview">
                                       <% if(n.getStrPosition() != null && !n.getStrPosition().equals("C")) {  %>
                                        <a class="<%=((n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId) || n.getStrNavId().equalsIgnoreCase(strNavParentId1) )?"selectedL":"") %>" href="<%= n.getStrAction()%>">
                                        <%=((n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId) || n.getStrNavId().equalsIgnoreCase(strNavParentId1))?n.getStrLabelSelected():n.getStrLabelUnSelected()) %>
                                        <% if(alChild.size() != 0){ %>
                                        <i class="fa fa-angle-left pull-right"></i>
                                        <% } %>
                                        </a>
                                        <% } %>
                                        <% if(alChild.size() != 0) { %>	
		                                 <ul class="treeview-menu">
	                            			<%
	                            			
	                            			/* if(n.getStrPosition() != null && !n.getStrPosition().equals("C")) {
		                            			if(n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId)) {
		                            				sb.append("<li><a href=\""+n.getStrAction()+"\" style=\"color: #3C8DBC\"><i class=\"fa fa-dashboard\"></i> "+n.getStrLabel()+"</a></li>");
		                            			}
	                            			} else if(n.getStrPosition() != null && n.getStrPosition().equals("C")) {
		                            			if(n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId)) {
		                            				sb.append("<li><a href=\"MyDashboard.action?userscreen="+IConstants.ADMIN+"&navigationId=102&toPage=CS\" style=\"color: #3C8DBC\"><i class=\"fa fa-dashboard\"></i> Control Panel</a></li>");
		                            			}
	                            			} */
	                            			
	                            			for(int k=0; k<alChild.size(); k++){
	                                			Navigation nc = (Navigation)alChild.get(k);
	                                			List alChildC = (List)hmChildNavL.get(nc.getStrNavId());
	                                			boolean isSelected = false;  
	                                			for(int kc=0; alChildC!=null && kc<alChildC.size(); kc++) {
	                                				Navigation ncc = (Navigation)alChildC.get(kc);
	                                				if(strNavId.equals(ncc.getStrNavId())){
	                                					sb.append("<li><a href=\""+n.getStrAction()+"\" style=\"color: #3C8DBC\">"+n.getStrLabel()+"</a></li>");
	                                					if(uF.parseToBoolean(nc.getStrVisibility())){
	                                						sb.append("<li>"+"<a href=\""+nc.getStrAction()+"\" style=\"color: #3C8DBC\">"+nc.getStrLabel()+"</a></li>");
	                                					}
	                                					if(uF.parseToBoolean(ncc.getStrVisibility())){
	                                						sb.append("<li>"+"<a href=\""+ncc.getStrAction()+"\" style=\"color: #3C8DBC\">"+ncc.getStrLabel()+"</a></li>");
	                                					}
	                                				}   
	                                			}
	                                			%>
	                                			
	                                			<%if(uF.parseToBoolean(nc.getStrVisibility())) { %>
													<li><a class="<%= ((strNavId.equals(nc.getStrNavId()) || ( strNavParentId!=null && strNavParentId.equals(nc.getStrNavId())))?"active":"")%>" href="<%= nc.getStrAction()%>"><%= nc.getStrLabel()%></a></li>
			                                    <%} %>
			                                       
		                                		<% if(strNavId.equals(nc.getStrNavId()) && uF.parseToBoolean(nc.getStrVisibility())){
		                            					sb.append("<li>"+"<a href=\""+nc.getStrAction()+"\" style=\"color: #3C8DBC\">"+nc.getStrLabel()+"</a></li>");
		                            				}
		                                		} %>
	                            			</ul>
	                            			<% } %>
                            			</li>
                            		<%
                            		 }
                            	} %>
                            	
                            	<% if(CF.isTaskRig() && CF.isWorkRig()) { %>
	                            	<% if(PRODUCTTYPE == null || PRODUCTTYPE.equals("3")) { %>
	                            	<hr style="margin: 11px 7px;border-top: 1px solid #4D4D4D;">
	                            		<!-- <li class="treeview verti special1"><a class="" id="chg" href="Login.action?role=3&product=2">
										<i class="" style="font-size: 18px;">
											<b id="rotation" style="width: 108px; padding: 0px 8px 5px;border-radius: 5px;font-weight: normal;font-style: normal;">Workrig</b>
										</i></a>
										<span style="font-size: 18px;"><b>Workrig</b></span></a>
										</li> 
										
										<li class="treeview"><a class="" id="chg" href="Login.action?role=3&product=2">
										</a>
										 <span style="font-size: 18px;"><b>Workrig</b></span></a>
										</li> -->
										
										
										
										
										<div class="main-header" style="height: 108px;" >
										<!-- <hr style="margin: 11px 0px;border-top: 1px solid #4c8dbc"> -->
                <!-- Logo -->
                               
                <a class="logo logo1" href="Login.action?role=3&product=2" style= "height: auto; width:auto;">
                    <!-- mini logo for sidebar mini 50x50 pixels -->
                    <!-- <span class="logo-mini vertic" style="padding-top: 14px; padding-top: 14px; margin-left: -30px;" > -->
                    <span class="logo-mini" style="padding-top: 4px;">
                    	<img src="images1/icons/icons/mini-workrig.png" style="width: 30px;">
	                    <!-- Workrig -->
	                   
                    </span>
                    <!-- logo for regular state and mobile devices -->
                    <span class="logo-lg horiz" style="line-height: 30px;/* margin-top: 30px; */" >
                    	<img src="images1/icons/icons/workrig_white.png" style="width: 90px;">
	                    <!-- Workrig -->
	                   
                    </span> 
                </a>    
				              
            </div>
										
										
									<% } else if(PRODUCTTYPE == null || PRODUCTTYPE.equals("2")) { %>
									<hr style="margin: 11px 7px;border-top: 1px solid #4D4D4D;">
										<!-- <hr style="margin: 0px 7px;border-top: 1px solid #4D4D4D;">
										
										<li class="treeview verti special1"><a class="" id="chg" href="Login.action?role=3&product=3">
										<i class="" style="font-size: 18px;">
											<b id="rotation" style="width: 108px; padding: 0px 8px 5px;border-radius: 5px;font-weight: normal;font-style: normal">Taskrig</b>
										</i></a>
										<span style="font-size: 18px;"><b>2Taskrig</b></span></a>
										</li> -->
										 
										
										 
										
				<div class="main-header" style="height: 108px;" >
                <!-- Logo -->
                     <!-- <hr style="margin: 11px 0px;border-top: 1px solid #4c8dbc">  -->         
                <a class="logo logo1" title="Project Management" href="Login.action?role=3&product=3" style= "height: auto; width:auto;" >
                    <!-- mini logo for sidebar mini 50x50 pixels -->
                    <!-- <span class="logo-mini vertic" style="padding-top: 14px; padding-top: 14px; margin-left: -30px;" > -->
                    <span class="logo-mini" style="padding-top: 4px;">
	                    <!-- Taskrig -->
	                   <img src="images1/icons/icons/mini-taskrig.png" style="width: 30px;" >
                    </span>
                    <!-- logo for regular state and mobile devices -->
                    <span class="logo-lg horiz" style="line-height: 30px;/* margin-top: 30px; */" >
	                    <!-- Taskrig -->
	                   <img src="images1/icons/icons/taskrig_white.png" style="width: 90px;">
                    </span> 
                </a>    
				
               
            </div>
									<% } %>
								<% } %>	
                             <% } %>
                                   
                        <!-- </div>
                        
      			 </div> -->
            <!-- Navigation div ends here -->

            
            
<%request.setAttribute("NAV_TRAIL", sb.toString());%>
