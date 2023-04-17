<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 
<style>
.divvalign {
     vertical-align: top; /* here */
}
</style>
<%
String ansType = (String)request.getAttribute("ansType");
//System.out.println("ansType :::::: "+ansType);
%>
	<!-- <div id="anstypediv"> -->
				<% if(ansType.equals("1")) { %>
						<div id="anstype1">
							a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"/><br />
							c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"/><br />
							<textarea rows="2" cols="50" name="textara" style="width:200px;" disabled="disabled"></textarea>
						</div>
						<%}else if(ansType.equals("2")) { %>
						<div id="anstype2">
							a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"/><br />
							c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"/><br />
						</div>	
						<%}else if(ansType.equals("3")) { %>
						<div id="anstype3">
							<img border="0" style="padding: 5px 2px 0px; height: 30px; width: 31%;" src="<%=request.getContextPath()%>/images1/scorebar_img.png"/>
							<div id="marksscore" style="width:31%;">0 <span style="float:right;">100</span></div>
						</div>	
						<%}else if(ansType.equals("4")) { %>
						<div id="anstype4">
							<input type="radio" name="excellent" value="Excellent" disabled="disabled"/>Excellent 
							<input type="radio" name="verygood" value="Very Good" disabled="disabled"/>Very Good 
							<input type="radio" name="average" value="Average" disabled="disabled"/>Average 
							<input type="radio" name="good" value="Good" disabled="disabled"/>Good 
							<input type="radio" name="poor" value="Poor" disabled="disabled"/>Poor 
							<!-- <input type="radio" name="bad" value="Bad" disabled="disabled"/>Bad  -->
						</div>
						<%}else if(ansType.equals("5")) { %>
						<div id="anstype5">
							<input type="radio" name="yes" value="Yes" disabled="disabled"/>Yes &nbsp;
							<input type="radio" name="no" value="No" disabled="disabled"/>No &nbsp;
						</div>
						<%}else if(ansType.equals("6")) { %>
						<div id="anstype6">
							<input type="radio" name="true" value="True" disabled="disabled"/>True &nbsp;
							<input type="radio" name="false" value="False" disabled="disabled"/>False &nbsp;
						</div>
						<%}else if(ansType.equals("7")) { %>
						<div id="anstype7">
							<div class="divvalign">
							<textarea rows="2" cols="50" name="singleopentext" style="width:200px" disabled="disabled"></textarea>
							</div>
							<img border="0" style="padding: 5px 2px 0px; height: 30px; width: 21%;" src="<%=request.getContextPath()%>/images1/scorebar_img.png"/>
							<div id="markssingleopen" style="width:21%;">0 <span style="float:right;">100</span></div>
					</div>
					<%}else if(ansType.equals("8")) { %>
					<div id="anstype8">
						a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/><br />
						c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/><br />
					</div> 
					<%}else if(ansType.equals("9")) { %>
					<div id="anstype9">
						a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"/><br />
						c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"/><br />
					</div>
					<%}else if(ansType.equals("10")) { %>
					<div id="anstype10">
						<div class="divvalign" style="vertical-align: text-top; float: left; width: 100%; margin-bottom: 10px;">
						<span style="float: left; vertical-align: text-top; margin-right: 7px;">a)</span><span style="float: left; vertical-align: text-top; margin-right: 20px;"><textarea rows="2" cols="50" name="amultiopen" style="width:170px;" disabled="disabled"></textarea></span>
						<span style="float: left; vertical-align: text-top; margin-right: 7px;">b)</span><span style="vertical-align: text-top; float: left"><textarea rows="2" cols="50" name="bmultiopen" style="width:170px;" disabled="disabled"></textarea></span>
						</div>
						<div class="divvalign" style="vertical-align: text-top; float: left; width: 100%;">
						<span style="float: left; vertical-align: text-top; margin-right: 7px;">c)</span><span style="float: left; vertical-align: text-top; margin-right: 20px;"><textarea rows="2" cols="50" name="amultiopen" style="width:170px;" disabled="disabled"></textarea></span>
						<span style="float: left; vertical-align: text-top; margin-right: 7px;">d)</span><span style="vertical-align: text-top; float: left"><textarea rows="2" cols="50" name="bmultiopen" style="width:170px;" disabled="disabled"></textarea></span>
						</div>
						<!-- <div class="divvalign" style="vertical-align: text-top;">
						c)&nbsp;<textarea rows="2" cols="50" name="cmultiopen" style="width:200px;" disabled="disabled"></textarea>&nbsp;&nbsp;
						d)&nbsp;<textarea rows="2" cols="50" name="dmultiopen" style="width:200px;" disabled="disabled"></textarea>
						</div> -->
							<img border="0" style="padding: 5px 2px 0pt; height: 30px; width: 31%;" src="<%=request.getContextPath()%>/images1/scorebar_img.png"/>
							<div id="marksmultipleopen" style="width:31%;">0 <span style="float:right;">100</span></div>
				</div>
				<% } else if(ansType.equals("11")) { %>						
				<div id="anstype11">
					<img border="0" style="padding: 5px 2px 0px; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
					<img border="0" style="padding: 5px 2px 0px; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
					<img border="0" style="padding: 5px 2px 0px; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
					<img border="0" style="padding: 5px 2px 0px; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
					<img border="0" style="padding: 5px 2px 0px; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
				<!-- ===start parvez date: 28-02-2023=== -->	
					<br />
					Calculation basis on(actual or 100%): <input type="checkbox" name="chkCaculationBasis" id="chkCaculationBasis" />
				<!-- ===end parvez date: 28-02-2023=== -->	
				</div>
				<% } else if(ansType.equals("12")) { %>		
				<div id="anstype12">
					<textarea rows="2" cols="50" name="singleopenwithoutmarks" style="width:200px;" disabled="disabled"></textarea>
				</div>
				<% } else if(ansType.equals("13")) { %>
					<div id="anstype13">
						a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> Rate <input type="text" value="5" style="width: 20px !important; height: 22px !important;" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/> Rate <input type="text" value="3" style="width: 20px !important; height: 22px !important;" disabled="disabled"/> <br />
						c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> Rate <input type="text" value="4" style="width: 20px !important; height: 22px !important;" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/> Rate <input type="text" value="2" style="width: 20px !important; height: 22px !important;" disabled="disabled"/> <br />
						e) Option5&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/> Rate <input type="text" value="1" style="width: 20px !important; height: 22px !important;" disabled="disabled"/><br />
					</div> 
				<% } else if(ansType.equals("14")) { %>		
					<div id="anstype14">
						Matrix Heading: <input value="A B C D/ I II III VI" style="height: 22px !important;" disabled="disabled" type="text"><br />
						a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/><br />
						c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/><br />
					</div>
				<%} %>
<!-- </div> -->						