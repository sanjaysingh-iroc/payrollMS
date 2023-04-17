<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
 


<style>
.Gray12 {
	font: 12px;
	color: #999999;
}

.Box1 {
	width: 30px;
	height: 70px;
}

.ML {
	margin-left: 120px;
}

.MB {
	margin-bottom: 50px;
}

.W14 {
	font: 14px;
	color: #fff;
}

.greenbutton {
	width: 8px;
	height: 8px;
	background-color: #bcd29d;
	line-height: 8px;
	-moz-border-radius: 15px;
	-webkit-border-radius: 15px;
	border-radius: 15px;
	border: 1px solid #a0a0a0;
	padding: 5px;
}

.topDiv {
	background: #f8f8f8;
	border: 1px #adadad solid;
	height: 25%;
	width: 99.9%;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	-o-border-radius: 5px;
	border-radius: 5px;
	margin-top: 25px
}

.topDiv table tr td {
	vertical-align: middle;
}

.FL {
	float: left;
}

.addgoaltoreview {
	background-color: #ECF3F8;
	border-bottom: 1px solid #DDDDDD;
	box-shadow: 0 17px 21px -6px #CCCCCC;
	display: inline-block;
	padding: 10px 0;
	position: relative;
	text-align: center;
	width: 99.8%;
}

.addgoaltoreview-arrow {
	border-color: #ecf3f8 transparent transparent transparent;
	border-style: solid;
	border-width: 10px;
	height: 0;
	width: 0;
	position: absolute;
	bottom: -19px;
	left: 50%;
}

.addgoaltoreview h3 {
	float: left;
	padding: 10px 20px;
	width: 100%;
	text-align: left;
}

.addgoaltoreview input {
	font-weight: 700;
	float: right;
}

div.reviewbar {
	background: #f8f8f8;
	border: solid 2px #ccc;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	border-radius: 5px;
	clear: both;
	display: inline-block;
	margin: 10px 0;
	width: 99.8%;
	height: 35px;
}

div.reviewbar ul li {
	border-right: 2px solid #ccc;
	float: left;
	height: 35px;
}

div.reviewbar ul li {
	border-bottom: 4px solid #86B600;
}

div.reviewbar ul li.col3 {
	height: 35px;
	padding: 0px;
	width: 140px;
	border-bottom: 4px solid #86B600;
}

div.reviewbar ul li.col5 {
	border-right: 2px solid #ccc;
	border: none;
	float: right;
}

div.reviewbar ul li.col5 span {
	display: inline-block;
	padding: 12px 0;
}

div.reviewbar div.customerreview { /* Safari 4-5, Chrome 1-9 */
	background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#000000),
		to(#757474) );
	/* Safari 5.1, Chrome 10+ */
	background: -webkit-linear-gradient(top, #757474, #000000);
	/* Firefox 3.6+ */
	background: -moz-linear-gradient(top, #757474, #000000);
	/* IE 10 */
	background: -ms-linear-gradient(top, #757474, #000000);
	/* Opera 11.10+ */
	background: -o-linear-gradient(top, #757474, #000000);
	border-radius: 5px 0 0 5px;
	color: #FFFFFF;
	height: 35px;
	line-height: 35px;
	text-align: center;
	width: 150px
}
</style>





<div class="leftbox reportWidth">
	<div style="float: left; width: 100%; font-size: 18px; font-weight: bold; margin-bottom: 9px;">
		<div style="float: left;">HIII</div>
		
	</div>
<br/>

		<table class="tb_style"  width="100%">
			<tr>
				<th width="23%" align="right">Employee Code</th>
				<td>HI</td>
			</tr>
			<tr>
				<th valign="top" align="right">Employee Name</th>
				<td colspan="1">Hi</td>
			</tr>
			
			<tr>	
				<th align="right">Designation Name</th>
				<td>HI</td>
			</tr>
			
			<tr>	
				<th align="right">Department Name</th>
				<td>Hi</td>
			</tr>
			
			<tr>	
				<th align="right">Organisation Name</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Employee Type</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Date of Joining</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Probation Status</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Notice Period</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Total Experience</th>
				<td>HI</td>
			</tr>

		

			<tr>
				<th align="right">Exp with Current Organisation</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Education Qualification</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Skills</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Last Day Date</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">OffBoard Type</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Date of Resignation</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Resignation Accepted by</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Resignation Reason</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">Manager Approval Reason</th>
				<td>HI</td>
			</tr>
			
			<tr>
				<th align="right">HR Manager Approval Reason</th>
				<td>HI</td>
			</tr>
				
		</table>
		
		<br/>
		<div class="addgoaltoreview">
				<h3>Feedback Form</h3>
				<div style="padding: 0px 20px; font-size: 10px; text-align: justify; line-height: 12px;">
							
				</div>
			</div>	
	<br/><br/>
	
	
		
</div>
