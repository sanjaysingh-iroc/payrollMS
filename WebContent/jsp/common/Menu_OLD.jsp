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



if(strNavId==null)strNavId="";

/* out.println("<br/>strAction="+strAction);
out.println("<br/>strQuery="+strQuery);
out.println("<br/>strNavId="+strNavId);
out.println("<br/>strAction="+strAction);
out.println("<br/>hmNavigation="+hmNavigation);
out.println("<br/>alParentNavL="+alParentNavL);
out.println("<br/>hmChildNavL="+hmChildNavL);
 */

 StringBuilder sb = new StringBuilder();
%>


   <!-- Navigation div starts here --> 
                <div class="nav">
                        <div id="leftnav" class="table">
                              
                              
                              <%
                              
                              //System.out.println("alParentNavL.size() --->> " + alParentNavL.size());
                              
                              boolean isSubNav = false;
                              if(alParentNavL!=null) {
                              
                              for(int i=0; alParentNavL!=null && i<alParentNavL.size(); i++){ 
                            		Navigation n = (Navigation)alParentNavL.get(i);
                            		List alChild  = (List)hmChildNavL.get(n.getStrNavId());
                            		if(alChild==null){
                            			alChild = new ArrayList();
                            		}
                            		//System.out.println("n.getStrNavId() --->> " + n.getStrNavId());
                            		//System.out.println("strNavId --->> " + strNavId);
                            		
                            			
                            		if(uF.parseToInt(n.getStrChild())==0) {
                            			%>
                            			
                            			<ul class="select <%=((n.getStrNavId().equalsIgnoreCase(strNavId))?"current":"") %>">
                                        <li ><a class="<%=((n.getStrNavId().equalsIgnoreCase(strNavId))?"selectedL":"") %>" href="<%= n.getStrAction()%>">
                                        <b><%=((n.getStrNavId().equalsIgnoreCase(strNavId))?n.getStrLabelSelected():n.getStrLabelUnSelected()) %> </b></a></li>
                                        </ul>
                            			<%
                            			if(n.getStrPosition() != null && !n.getStrPosition().equals("C")) {
	                            			if(n.getStrNavId().equalsIgnoreCase(strNavId)) {
	                            				sb.append("<a href=\""+n.getStrAction()+"\" style=\"font-style: normal;text-decoration: underline; font-size: 10px;\">"+n.getStrLabel()+"</a>");
	                            			}
                            			} else if(n.getStrPosition() != null && n.getStrPosition().equals("C")) {
                            				if(n.getStrNavId().equalsIgnoreCase(strNavId)) {
                            					sb.append("<a href=\"MyDashboard.action?userscreen=Global HR\" style=\"font-style: normal;text-decoration: underline; font-size: 10px;\">Control Panel</a>");
                            				}
                            			}
                            			
                            		} else {
                            			%>
                       		             <ul class="select <%=((n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId) || n.getStrNavId().equalsIgnoreCase(strNavParentId1))?"current":"") %>">
                                        <li>
                                       <% if(n.getStrPosition() != null && !n.getStrPosition().equals("C")) {  %>
                                        <a class="<%=((n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId) || n.getStrNavId().equalsIgnoreCase(strNavParentId1) )?"selectedL":"") %>" href="<%= n.getStrAction()%>">
                                        <b><%=((n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId) || n.getStrNavId().equalsIgnoreCase(strNavParentId1))?n.getStrLabelSelected():n.getStrLabelUnSelected()) %>    </b><!--[if IE 7]><!--></a><!--<![endif]-->
                                        <% } %>
                            			<!--[if lte IE 6]><table><tr><td><![endif]-->
                            			 <div class="select_sub show">
		                                        <ul class="sub">
                            			<%
                            			
                            			if(n.getStrPosition() != null && !n.getStrPosition().equals("C")) {
	                            			if(n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId)) {
	                            				sb.append("<a href=\""+n.getStrAction()+"\" style=\"font-style: normal; font-size: 10px;\">"+n.getStrLabel()+"</a>");
	                            			}
                            			} else if(n.getStrPosition() != null && n.getStrPosition().equals("C")) {
	                            			if(n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId)) {
	                            				sb.append("<a href=\"MyDashboard.action?userscreen=Global HR\" style=\"font-style: normal; font-size: 10px;\">Control Panel</a>");
	                            			}
                            			}
                            			
                            			for(int k=0; k<alChild.size(); k++){
                                			Navigation nc = (Navigation)alChild.get(k);
                                			List alChildC = (List)hmChildNavL.get(nc.getStrNavId());
                                			boolean isSelected = false;  
                                			for(int kc=0; alChildC!=null && kc<alChildC.size(); kc++) {
                                				Navigation ncc = (Navigation)alChildC.get(kc);
                                				if(strNavId.equals(ncc.getStrNavId())){
                                					sb.append("<a href=\""+n.getStrAction()+"\" style=\"font-style: normal; font-size: 10px;\">"+n.getStrLabel()+"</a>");
                                					if(uF.parseToBoolean(nc.getStrVisibility())){
                                						sb.append(" >> "+"<a href=\""+nc.getStrAction()+"\" style=\"font-style: normal; font-size: 10px;\">"+nc.getStrLabel()+"</a>");
                                					}
                                					if(uF.parseToBoolean(ncc.getStrVisibility())){
                                						sb.append(" >> "+"<a href=\""+ncc.getStrAction()+"\" style=\"font-style: normal;text-decoration: underline; font-size: 10px;\">"+ncc.getStrLabel()+"</a>");
                                					}
                                				}   
                                			}
                                			
                                			
                                			%>
                                			
                                			
                                			
                                			<%if(uF.parseToBoolean(nc.getStrVisibility())){ %>
		                                            <li><a class="<%= ((strNavId.equals(nc.getStrNavId()) || ( strNavParentId!=null && strNavParentId.equals(nc.getStrNavId())))?"selectedL_sub":"")%>" href="<%= nc.getStrAction()%>"><%= nc.getStrLabel()%></a></li>
		                                    <%} %>    
                                			<%
                                			
                                			if(strNavId.equals(nc.getStrNavId()) && uF.parseToBoolean(nc.getStrVisibility())){
                                				sb.append(" >> "+"<a href=\""+nc.getStrAction()+"\" style=\"font-style: normal;text-decoration: underline; font-size: 10px;\">"+nc.getStrLabel()+"</a>");
                                			}
                                		}
                            			
                            			
                            			%>
                            			</ul>
		                                    </div>
                            			<!--[if lte IE 6]></td></tr></table></a><![endif]-->
                            			</li></ul>
                            			<%
                            		}
                            		
                              
                            	}
                              }
                              %>
                                   
                        </div>
                        
                          <div class="table_report table">
                          <%
                             if(alParentNavR!=null) {
                              for(int i=0; alParentNavR!=null && i<alParentNavR.size(); i++){
                            		Navigation n = (Navigation)alParentNavR.get(i);
                            		List alChild  = (List)hmChildNavR.get(n.getStrNavId());
                            		if(alChild==null){
                            			alChild = new ArrayList();
                            		}
                            		
                            		if(uF.parseToInt(n.getStrChild())==0){                            			
                            			%>
                            			
                            			<ul class="select <%=((n.getStrNavId().equalsIgnoreCase(strNavId))?"current":"")%>">
                                        <li><a class="<%=((n.getStrNavId().equalsIgnoreCase(strNavId))?"selectedR":"") %>" href="<%= n.getStrAction()%>">
										<b><%=((n.getStrNavId().equalsIgnoreCase(strNavId))?n.getStrLabelSelected():n.getStrLabelUnSelected()) %></b></a></li>
                                        </ul>
                            			                           			 
                            			<%
                            			if(n.getStrNavId().equalsIgnoreCase(strNavId)){
                            				sb.append("<a href=\""+n.getStrAction()+"\" style=\"font-style: normal;text-decoration: underline; font-size: 10px;\">"+n.getStrLabel()+"</a>");
                            			}
                            		}else{
                            			%>
               						<ul class="select <%=((n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId) || n.getStrNavId().equalsIgnoreCase(strNavParentId1))?"current":"") %>">
               						<li>
                                    <a class="<%=((n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId))?"selectedR":"") %>" href="<%= n.getStrAction()%>">  
                                    <b><%=((n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId))?n.getStrLabelSelected():n.getStrLabelUnSelected()) %></b>
                                    <!--[if IE 7]><!--></a><!--<![endif]-->
                                    <!--[if lte IE 6]><table><tr><td><![endif]-->
                                    	 <div class="select_sub show">
		                                        <ul class="sub right_nav">
                            			<%
                            			/* for(int k=0; k<alChild.size(); k++){
                                			Navigation nc = (Navigation)alChild.get(k);
                                			
                                			 */
                                			
                                			if(n.getStrNavId().equalsIgnoreCase(strNavId) || n.getStrNavId().equalsIgnoreCase(strNavParentId)){
                                				sb.append("<a href=\""+n.getStrAction()+"\" style=\"font-style: normal; font-size: 10px;\">"+n.getStrLabel()+"</a>");
                                			}
                                			
                                			for(int k=0; k<alChild.size(); k++){
                                    			Navigation nc = (Navigation)alChild.get(k);
                                    			List alChildC = (List)hmChildNavR.get(nc.getStrNavId());
                                    			for(int kc=0; alChildC!=null && kc<alChildC.size(); kc++){
                                    				Navigation ncc = (Navigation)alChildC.get(kc);
                                    				if(strNavId.equals(ncc.getStrNavId())){
                                    					sb.append("<a href=\""+n.getStrAction()+"\" style=\"font-style: normal; font-size: 10px;\">"+n.getStrLabel()+"</a>");
                                    					sb.append(" >> "+"<a href=\""+nc.getStrAction()+"\" style=\"font-style: normal; font-size: 10px;\">"+nc.getStrLabel()+"</a>");
                                    					sb.append(" >> "+"<a href=\""+ncc.getStrAction()+"\" style=\"font-style: normal;text-decoration: underline; font-size: 10px;\">"+ncc.getStrLabel()+"</a>");
                                    				}
                                    			}
                                    			
                                			%>
		                                   
		                                   
		                           <li><a class="<%= ((strNavId.equals(nc.getStrNavId()) || ( strNavParentId!=null && strNavParentId.equals(nc.getStrNavId())))?"selectedR_sub":"")%>" href="<%= nc.getStrAction()%>"><%= nc.getStrLabel()%>
                                   </a></li>
		                                   
                                			<%
                                			if(strNavId.equals(nc.getStrNavId()) && uF.parseToBoolean(nc.getStrVisibility())){
                                				sb.append(" >> "+"<a href=\""+nc.getStrAction()+"\" style=\"font-style: normal;text-decoration: underline; font-size: 10px;\">"+nc.getStrLabel()+"</a>");
                                			}
                                		}
                            			%>
                            			</ul>
		                                    </div>
                            			<!--[if lte IE 6]></td></tr></table></a><![endif]-->
                            			</li></ul>
                            			<%
                            			
                            		}
                            	}
                                    }
                              
                              %>                          
                               </div>
                               
      			 </div>
            <!-- Navigation div ends here -->

            
            
<%request.setAttribute("NAV_TRAIL", sb.toString());%>