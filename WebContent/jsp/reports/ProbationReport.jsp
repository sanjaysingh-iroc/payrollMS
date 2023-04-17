<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.export.DataStyle,com.itextpdf.text.BaseColor,com.itextpdf.text.Element" %>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%> 
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<script type="text/javascript" charset="utf-8">

$(function() {
    $( "#effectiveDate" ).datepicker({dateFormat: 'dd/mm/yy'});
});
			$(document).ready( function () {
				$('#lt').dataTable({ bJQueryUI: true, 
					"sPaginationType": "full_numbers",
					"iDisplayLength": 1000,
					"aLengthMenu": [
					                [1, 2, -1],
					                [1, 2, "All"]
					            ],
					"aaSorting": [[0, 'asc']],
					/* "sDom": '<"H"lTf>rt<"F"ip>', */
					"sDom": '<"H"f>rt<"F"ip>',
					oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
						aButtons: [
							"csv", "xls", {
								sExtends: "pdf",
								sPdfOrientation: "landscape"
								//sPdfMessage: "Your custom message would go here."
								}, "print" 
						]
					}
					});
					/* $('#lt').dataTable({
						bJQueryUI : true,
						"sPaginationType" : "full_numbers",
						"aaSorting" : []
					})  */
				});
			
			


	function checkUncheckValue() {
		var allEmp=document.getElementById("allEmp");		
		var strEmpId = document.getElementsByName('empIds');

		if(allEmp.checked==true){
			 for(var i=0;i<strEmpId.length;i++){
				 strEmpId[i].checked = true;
				  
			 }
		}else{		
			 for(var i=0;i<strEmpId.length;i++){
				 strEmpId[i].checked = false;			 
			 }		 
		}
		 
	}

	var dialogEdit = '#extendsDaysid';
	function extendProbationDays(empid,empname,divid) { 
		
		
		document.getElementById("extendsDaysid").innerHTML = '';
		//paycycle f_org f_strWLocation f_department f_service 
		var paycycle=document.getElementById("paycycle").value;
		var f_org=document.getElementById("f_org").value;
		var f_strWLocation=document.getElementById("f_strWLocation").value;
		var f_department=document.getElementById("f_department").value;
		var f_service=document.getElementById("f_service").value;
		
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 350,
					width : 400,
					modal : true,
					title : 'Extends days for '+empname,
					open : function() {
						var xhr = $.ajax({  
							url : 'ExtendProbationDays.action?empid='+empid+'&empname='+empname+'&divid='+divid+'&paycycle='+paycycle+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_service='+f_service,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;

					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});

		$(dialogEdit).dialog('open');
	}
	
	function validateReason(strReason,empid,empname,paycycle,f_org,f_strWLocation,f_department,f_service,divid,extendDays){
		if(strReason==""){
			alert('Please enter the valid reason');
		}else{
			var action='ExtendProbationDays.action?operation=U&empid='+empid+'&empname='+empname+'&divid='+divid+'&paycycle='+paycycle+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_service='+f_service+"&strReason="+strReason+"&extendDays="+extendDays;
			getContent(divid,action);
		}
		$(dialogEdit).dialog('close');
	}
	

</script>



<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Probations" name="title"/>
</jsp:include>
<%
CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 
UtilityFunctions uF = new UtilityFunctions();

List<List<String>> outerList=(List<List<String>>)request.getAttribute("outerList");
%>

<div id="printDiv" class="leftbox reportWidth">

<div class="filter_div" style="width:98%;">
<div class="filter_caption">Filter</div>
		<s:form theme="simple" method="post" name="frm_Form" action="ProbationReport">
				
			<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId"
					listValue="paycycleName" headerKey="0" cssStyle="width:200px"
					onchange="document.frm_Form.submit();"
					list="paycycleList" key="" />
					
			<s:select theme="simple" name="f_org" listKey="orgId"
                         listValue="orgName"
                         onchange="document.frm_Form.submit();" 
                         list="orgList" key=""   cssStyle="width:200px;"/>			
						
			<s:select theme="simple" name="f_strWLocation" listKey="wLocationId" 
						listValue="wLocationName" 
						onchange="document.frm_Form.submit();"
						list="wLocationList" key=""  cssStyle="width:200px;"/>
						
			<s:select theme="simple" name="f_department" listKey="deptId"
						listValue="deptName"
						onchange="document.frm_Form.submit();"
						list="departmentList" key=""  cssStyle="width:200px;"/>
						
						
			<%-- <s:select theme="simple" name="f_level" listKey="levelId"
						listValue="levelCodeName"
						onchange="document.frm_Form.submit();"
						list="levelList" key=""  cssStyle="width:200px;"/>	 --%>
			<s:select name="f_service" list="serviceList" listKey="serviceId"  
    			listValue="serviceName"
    			onchange="document.frm_Form.submit();"
    			cssStyle="width:200px;" key=""/> 
 	             
				
		</s:form>
</div>

<s:form theme="simple" method="post" name="frm_FormReport" action="ProbationReport">

	<s:hidden name="paycycle" id="paycycle"></s:hidden>
	<s:hidden name="f_org" id="f_org"></s:hidden>
	<s:hidden name="f_strWLocation" id="f_strWLocation"></s:hidden>
	<s:hidden name="f_department" id="f_department"></s:hidden>
	<s:hidden name="f_service" id="f_service"></s:hidden> 
	
	
<div  style="width:100%;">

<%=request.getAttribute(IConstants.MESSAGE)!=null ? request.getAttribute(IConstants.MESSAGE) : "" %>
	
	<a href="#?w=600" rel="popup_name" class="poplight" id="divid" >
			<input type="button" value="Approve" class="input_button" style="margin-bottom: 10px"/>
	</a>
	
	<%
			if (request.getAttribute("approvePopUp") != null) {
				out.println(request.getAttribute("approvePopUp"));
			}
		%>
	
	
	<table id="lt" cellpadding="2" cellspacing="2" border="0"
		class="tb_style" width="100%">

		<thead>
			<tr>
				<th class="alignCenter" nowrap>Emp Code<br/><input onclick="checkUncheckValue();"
								type="checkbox" name="allEmp" id="allEmp"></th>
				<th class="alignCenter" nowrap>Employee Name</th>
				<th class="alignCenter" nowrap>Designation</th>
				<th class="alignCenter" nowrap>Work Location</th>
				<th class="alignCenter" nowrap>Department</th>
				<th class="alignCenter" nowrap>Joining Date</th>
				<th class="alignCenter" nowrap>Probation End Date</th>
				<th class="alignCenter" nowrap>Extend Probation</th>				
			</tr>
		</thead>
		<tbody>		
			<%for(int i=0;outerList!=null && !outerList.isEmpty() && i<outerList.size();i++){
				List<String> innerList=outerList.get(i);
			%>				
				<tr> 
					<td class="alignLeft" nowrap><input type="checkbox" name="empIds" value="<%=innerList.get(0) %>"/>&nbsp;<%=innerList.get(1) %></td>
					<td class="alignLeft" nowrap><%=innerList.get(2) %></td>
					<td class="alignLeft" nowrap><%=innerList.get(3) %></td>
					<td class="alignLeft" nowrap><%=innerList.get(4) %></td>
					<td class="alignLeft" nowrap><%=innerList.get(5) %></td>
					<td class="alignLeft" nowrap><%=innerList.get(6) %></td>
					<td class="alignLeft" nowrap><%=innerList.get(7) %></td>
					<td class="alignCenter" nowrap>
					
					 <a href="javascript:void(0)" onclick="extendProbationDays('<%=innerList.get(0) %>','<%=innerList.get(2) %>','myDiv_<%=i %>');">Extend</a>
					 <div id="myDiv_<%=i %>">
					 </div>
					</td>
				</tr>
			<%} %>
		</tbody>
	</table>
	</div>
	</s:form>
</div>


<script>

$(document).ready(function() {

    $('a.poplight[href^=#]').click(function() {
        var popID = $(this).attr('rel'); //Get Popup Name
        var popURL = $(this).attr('href'); //Get Popup href to define size

        //Pull Query & Variables from href URL
        var query= popURL.split('?');
        var dim= query[1].split('&');
        var popWidth = dim[0].split('=')[1]; //Gets the first query string value

        //Fade in the Popup and add close button
        $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

        //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
        var popMargTop = ($('#' + popID).height() + 80) / 2;
        var popMargLeft = ($('#' + popID).width() + 80) / 2;

        //Apply Margin to Popup
        $('#' + popID).css({
            'margin-top' : -popMargTop,
            'margin-left' : -popMargLeft
        });

        //Fade in Background
        $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
        $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies

        return false;
    });

    //Close Popups and Fade Layer
    $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
        $('#fade , .popup_block').fadeOut(function() {
            $('#fade, a.close').remove();  //fade them both out
        });
        return false;
    });

});

</script>
 
 <div id="extendsDaysid"></div>
