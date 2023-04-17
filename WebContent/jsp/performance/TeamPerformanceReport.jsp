
<%@page import="java.util.*"%>
<style>
.ORDig{
color: #FF9900;
font-family: digital;
font-size: 14px;
}
</style>


	
<%
			List<String> list1 = (List<String>) request.getAttribute("list1");
			List<String> list2 = (List<String>) request.getAttribute("list2");
			List<String> list3 = (List<String>) request.getAttribute("list3");
			List<String> list4 = (List<String>) request.getAttribute("list4");
			List<String> list5 = (List<String>) request.getAttribute("list5");
			List<String> list6 = (List<String>) request.getAttribute("list6");
			List<String> list7 = (List<String>) request.getAttribute("list7");
			List<String> list8 = (List<String>) request.getAttribute("list8");
			List<String> list9 = (List<String>) request.getAttribute("list9");
			Map<String, String> hmEmpList =(Map<String, String>)request.getAttribute("hmEmpList");
			if(hmEmpList==null) hmEmpList=new HashMap<String, String>();
		%>
		
		<div style="margin-top: 15px;">
			<div class="process">
				<div class="OR rotate">
					<strong>Performance</strong>
				</div>
			</div>
			<div class="verticalLine PL40 PB40">
				<div class="section group">
					<div class="rotate fl ro-Div">
						<strong>Exceeds</strong>
					</div>
					<div class="col span_1_of_4" style="height: 3.2cm; background-color: #fdd7b6; border: 1px solid white;">
						<div class="Box">
							<div style="max-height: 17px;">
								<p> Worker <span class="ORDig" ><strong><%=list1 != null ? list1.size() : "0"%></strong></span></p>
							</div>
							<div style="float: left; width: 99%; max-height: 70%;">
								<ul style="float: left;">
									<% for (int i = 0; list1 != null && i < list1.size(); i++) { %>
									<li class="ImageDiv printli" style="font-size: 20px;"><%=list1.get(i)%></li>
									<% if(i > 13){break;}
									} %>
								</ul>
							</div>
							 <%if(list1 != null && list1.size() > 14){ %> 
							<div style="float: right">
								<a href="javascript:void(0);" onclick="seeEmpList('<%=hmEmpList.get("EMPLIST1") %>', 'Worker');" class="OR testa">more..</a>
							</div>
							 <%} %> 
						</div>
					</div>
					<div class="col span_1_of_4" style="height: 3.2cm; background-color: #ffbe85; border: 1px solid white;">
						<div class="Box">
							<div style="padding: 2px 2px 2px 10px">
								<p>
									Contributor <span class="ORDig"><strong><%=list2 != null ? list2.size() : "0"%></strong>
									</span>
								</p>
								<ul style="float: left;">
									<% for (int i = 0; list2 != null && i < list2.size(); i++) { %>
									<li class="ImageDiv printli" style="font-size: 20px;"><%=list2.get(i)%></li>
									<% if(i > 13){ break;}
									} %>
								</ul>
								<%if(list2 != null && list2.size() > 14){ %>
								<p class="fr">
									<a href="javascript:void(0);" onclick="seeEmpList('<%=hmEmpList.get("EMPLIST2") %>', 'Contributor');" class="OR testa">more..</a>
								</p>
								<%} %>
							</div>
						</div>
					</div>
					<div class="col span_1_of_4" style="height: 3.2cm; background-color: #fca150; border: 1px solid white;">
						<div class="Box">
							<div style="padding: 2px 2px 2px 10px">
								<p>
									Stars <span class="ORDig" style="color: black;"><strong><%=list3 != null ? list3.size() : "0"%></strong>
									</span>
								</p>
								<ul style="float: left;">
									<% for (int i = 0; list3 != null && i < list3.size(); i++) { %>
									<li class="ImageDiv printli" style="font-size: 20px;"><%=list3.get(i)%></li>
									<% if(i > 13){ break;}
									} %>
								</ul>
								<%if(list3 != null && list3.size() > 14){ %>
								<p class="fr">
									<a href="javascript:void(0);" onclick="seeEmpList('<%=hmEmpList.get("EMPLIST3") %>', 'Stars');" class="OR testa">more..</a>
								</p>
								<%} %>
							</div>
						</div>
					</div>
				</div>
				<div class="section group">
					<div class="rotate fl ro-Div">
						<strong>Meets</strong>
					</div>
					<div class="col span_1_of_4" style="height: 3.2cm; background-color: #fde1c7; border: 1px solid white;">
						<div class="Box">
							<div style="padding: 2px 2px 2px 10px">
								<p>
									Blockers <span class="ORDig"><strong><%=list4 != null ? list4.size() : "0"%></strong>
									</span>
								</p>
								<ul style="float: left;">
									<% for (int i = 0; list4 != null && i < list4.size(); i++) { %>
									<li class="ImageDiv printli" style="font-size: 20px;"><%=list4.get(i)%></li>
									<% if(i > 13){ break;}
									} %>
								</ul>
								<%if(list4 != null && list4.size() > 14){ %>
								<p class="fr">
									<a href="javascript:void(0);" onclick="seeEmpList('<%=hmEmpList.get("EMPLIST4") %>', 'Blockers');" class="OR testa">more..</a>
								</p>
								<%} %>
							</div>
						</div>
					</div>
					<div class="col span_1_of_4" style="height: 3.2cm; background-color: #fecb9e; border: 1px solid white;">
						<div class="Box">
							<div style="padding: 2px 2px 2px 10px">
								<p>
									Transitionals <span class="ORDig"><strong><%=list5 != null ? list5.size() : "0"%></strong>
									</span>
								</p>
								<ul style="height: 60px">
									<% for (int i = 0; list5 != null && i < list5.size(); i++) { %>
									<li class="ImageDiv printli" style="font-size: 20px;"><%=list5.get(i)%></li>
									<% if(i > 13){ break;}
									} %>
								</ul>
								<%if(list5 != null && list5.size() > 14){ %>
								<p class="fr">
									<a href="javascript:void(0);" onclick="seeEmpList('<%=hmEmpList.get("EMPLIST5") %>', 'Transitionals');" class="OR testa">more..</a>
								</p>
								<%} %>
							</div>
						</div>
					</div>
					<div class="col span_1_of_4" style="height: 3.2cm; background-color: #ffbe85; border: 1px solid white;">
						<div class="Box">
							<div style="padding: 2px 2px 2px 10px">
								<p>
									Emergers <span class="ORDig"><strong><%=list6 != null ? list6.size() : "0"%></strong>
									</span>
								</p>
								<ul style="float: left;">
									<% for (int i = 0; list6 != null && i < list6.size(); i++) { %>
									<li class="ImageDiv printli" style="font-size: 20px;"><%=list6.get(i)%></li>
									<% if(i > 13){ break;}
									} %>
								</ul><%if(list6 != null && list6.size() > 14){ %>
								<p class="fr">
									<a href="javascript:void(0);" onclick="seeEmpList('<%=hmEmpList.get("EMPLIST6") %>', 'Emergers');" class="OR testa">more..</a>
								</p>
								<%} %>
							</div>
						</div>
					</div>
				</div>
				<div class="section group">
					<div class="rotate fl ro-Div">
						<strong>Below</strong>
					</div>
					<div class="col span_1_of_4" style="height: 3.2cm; background-color: #ffecdc; border: 1px solid white;">
						<div class="Box">
							<div style="padding: 2px 2px 2px 10px">
								<p>
									Detactors <span class="ORDig"><strong><%=list7 != null ? list7.size() : "0"%></strong>
									</span>
								</p>
								<ul style="float: left;">
									<% for (int i = 0; list7 != null && i < list7.size(); i++) { %>
									<li class="ImageDiv printli" style="font-size: 20px;"><%=list7.get(i)%></li>
									<% if(i > 13){ break;}
									} %>
								</ul>
								<%if(list7 != null && list7.size() > 14){ %>
								<p class="fr">
									<a href="javascript:void(0);" onclick="seeEmpList('<%=hmEmpList.get("EMPLIST7") %>', 'Detactors');" class="OR testa">more..</a>
								</p>
								<%} %>
							</div>
						</div>
					</div>
					<div class="col span_1_of_4" style="height: 3.2cm; background-color: #fde1c7; border: 1px solid white;">
						<div class="Box">
							<div style="padding: 2px 2px 2px 10px">
								<p>
									Placeholders <span class="ORDig"><strong><%=list8 != null ? list8.size() : "0"%></strong>
									</span>
								</p>
								<ul style="float: left;">
									<% for (int i = 0; list8 != null && i < list8.size(); i++) { %>
									<li class="ImageDiv printli" style="font-size: 20px;"><%=list8.get(i)%></li>
									<% if(i > 13){ break;}
									} %>
								</ul>
								<%if(list8 != null && list8.size() > 14){ %>
								<p class="fr">
									<a href="javascript:void(0);" onclick="seeEmpList('<%=hmEmpList.get("EMPLIST8") %>', 'Placeholders');" class="OR testa">more..</a>
								</p>
								<%} %>
							</div>
						</div>
					</div>
					<div class="col span_1_of_4" style="height: 3.2cm; background-color: #fdd7b6; border: 1px solid white;">
						<div class="Box">
							<div style="padding: 2px 2px 2px 10px">
								<p>Latents <span class="ORDig"><strong><%=list9 != null ? list9.size() : "0"%></strong></span></p>
								<ul style="float: left;">
									<% for (int i = 0; list9 != null && i < list9.size(); i++) { %>
									<li class="ImageDiv printli" style="font-size: 20px;"><%=list9.get(i)%></li>
									<% if(i > 13){ break;}
									} %>
								</ul>
								<%if(list9 != null && list9.size() > 14){ %>
								<p class="fr">
									<a href="javascript:void(0);" onclick="seeEmpList('<%=hmEmpList.get("EMPLIST9") %>', 'Latents');" class="OR testa">more..</a>
								</p>
								<%} %>
							</div>
						</div>
					</div>
				</div>
				<div>
					<ul>
						<!-- <li class="printli" style="margin-left: 35px;"><strong>Low</strong></li>
						<li class="PLL printli" style="margin-left: 20px;"><strong>Medium</strong></li>
						<li class="PL130 printli"><strong>High</strong></li> -->
						<li class="span_1_of_4" style="float:left; border:0px; text-align:center; margin-left:5%;"><strong>Low</strong></li>
						<li class="span_1_of_4" style="float:left; border:0px; text-align:center;"><strong>Medium</strong></li>
						<li class="span_1_of_4" style="float:left; border:0px; text-align:center;"><strong>High</strong></li>
					</ul>
				</div>
			</div>
			<div class="OR  PR45 PT10" align="center">
				<strong> Potential</strong>
			</div>
		</div>
		
<script>  
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>