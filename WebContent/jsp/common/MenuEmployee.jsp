
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="java.util.*"%>

<% 
UtilityFunctions uF = new UtilityFunctions();
List alParentNavL = (List) session.getAttribute("alParentNavL");
Map hmChildNavL = (Map) session.getAttribute("hmChildNavL");

List alParentNavR = (List) session.getAttribute("alParentNavR");
Map hmChildNavR = (Map) session.getAttribute("hmChildNavR");
%>


 <!-- Navigation div starts here --> 
                <div class="nav">
                
                        <div class="table">
                              
                              
                              <%
                              
                              for(int i=0; i<alParentNavL.size(); i++){
                            		Navigation n = (Navigation)alParentNavL.get(i);
                            		List alChild  = (List)hmChildNavL.get(n.getStrNavId());
                            		if(alChild==null){
                            			alChild = new ArrayList();
                            		}
                            		
                            		if(uF.parseToInt(n.getStrChild())==0){
                            			%>
                            			
                            			<ul class="select"><li><a href="<%= n.getStrAction()%>"><b><%= n.getStrLabel()%></b></a></li></ul>
                            			<%
                            			
                            		}else{
                            			%>
                            			<ul class="select"><li><a href="<%= n.getStrAction()%>"><b><%= n.getStrLabel()%></b><!--[if IE 7]><!--></a><!--<![endif]-->
                            			<!--[if lte IE 6]><table><tr><td><![endif]-->
                            			 <div class="select_sub">
		                                        <ul class="sub">
                            			<%
                            			for(int k=0; k<alChild.size(); k++){
                                			Navigation nc = (Navigation)alChild.get(k);
                                			%>
		                                   
		                                            <li><a href="<%= nc.getStrAction()%>"><%= nc.getStrLabel()%></a></li>
		                                        
                                			<%
                                		}
                            			%>
                            			</ul>
		                                    </div>
                            			<!--[if lte IE 6]></td></tr></table></a><![endif]-->
                            			</li></ul>
                            			<%
                            		}
                            	}
                              
                              %>
                                   
                        </div>
                        
                        
                         <div class="table_report table">
                                    <%
                              for(int i=0; i<alParentNavR.size(); i++){
                            		Navigation n = (Navigation)alParentNavR.get(i);
                            		List alChild  = (List)hmChildNavR.get(n.getStrNavId());
                            		if(alChild==null){
                            			alChild = new ArrayList();
                            		}
                            		
                            		if(uF.parseToInt(n.getStrChild())==0){
                            			%>
                            			
                            			<ul class="select"><li><a href="<%= n.getStrAction()%>"><b><%= n.getStrLabel()%></b></a></li></ul>
                            			<%
                            			
                            		}else{
                            			%>
                            			<ul class="select"><li><a href="<%= n.getStrAction()%>"><b><%= n.getStrLabel()%></b><!--[if IE 7]><!--></a><!--<![endif]-->
                            			<!--[if lte IE 6]><table><tr><td><![endif]-->
                            			 <div class="select_sub">
		                                        <ul class="sub">
                            			<%
                            			for(int k=0; k<alChild.size(); k++){
                                			Navigation nc = (Navigation)alChild.get(k);
                                			%>
		                                   
		                                            <li><a href="<%= nc.getStrAction()%>"><%= nc.getStrLabel()%></a></li>
		                                        
                                			<%
                                		}
                            			%>
                            			</ul>
		                                    </div>
                            			<!--[if lte IE 6]></td></tr></table></a><![endif]-->
                            			</li></ul>
                            			<%
                            		}
                            	}
                              
                              %>                          
                               </div>
                               
      			 </div>
            <!-- Navigation div ends here -->

