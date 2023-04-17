<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script>
    function addArear(arear_id, empName) {
    	var title = "Apply New Arrear";
    	if(parseInt(arear_id)>0){
    		title = "Edit Arrear of "+empName;
    	}
    	var myDiv = document.getElementById('myDiv');
    	var frmArear = document.getElementById('frmArear');
    	if(myDiv && frmArear) {
    	//	frmArear.removeChild(myDiv); 
    	}
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html(''+title);
    	 if($(window).width() >= 700){
    		 $(".modal-dialog").width(700);
    	 }
    	 $.ajax({
   			url : "AddArrearPopUp.action?arear_id="+arear_id,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
    } 
    
    function selectall(x,strEmpId){
    	
  	 	var  status=x.checked;
  		var  arr= document.getElementsByName(strEmpId);
  			for(i=0;i<arr.length;i++) {
  		  		arr[i].checked=status;
  		 	}
    }

    $(document).ready( function () {
        $('#lt').DataTable({
			"order": [],
			"columnDefs": [ {
			      "targets"  : 'no-sort',
			      "orderable": false
			    }],
		    "dom": 'lBfrtip',
	        "buttons": [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ]
		});
    });

    
    function submitForm(type){
    	var org = document.getElementById("f_org").value;
    	var strYear = document.getElementById("strYear").value;
    	var strMonth = document.getElementById("strMonth").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	var level = getSelectedValue("f_level");
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&strMonth='+strMonth
    		+'&strYear='+strYear;
    	}
    	//alert("service ===>> " + service);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'PayArrears.action?f_org='+org+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	$("#divResult").html(result);
       		}
    	});
    }


    function getSelectedValue(selectId) {
    	var choice = document.getElementById(selectId);
    	var exportchoice = "";
    	for ( var i = 0, j = 0; i < choice.options.length; i++) {
    		if (choice.options[i].selected == true) {
    			if (j == 0) {
    				exportchoice = choice.options[i].value;
    				j++;
    			} else {
    				exportchoice += "," + choice.options[i].value;
    				j++;
    			}
    		}
    	}
    	return exportchoice;
    }
    
    
    function deleteArrear(arrearId) {
    	if(confirm('Are you sure you want to delete this arrear?')) {
	    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$.ajax({
	    		type : 'POST',
	    		url: 'GetArrearEmployees.action?DID='+arrearId
	    				/* ,
	    		success: function(result){
	            	$("#divResult").html(result);
	       		} */
	    	});
	    	
	    	$.ajax({
				url: 'PayArrears.action',
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
    	}
    }
    
    
    function approveDenyArrear(arear_id, empName, actionType) {
    	var strAction = "Approve";
    	if(actionType == 'D'){
    		strAction = "Deny";
    	}
    	var title = strAction+" Arrear of "+empName;
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html(''+title);
    	 $(".modal-dialog").height(300);
    	 /* if($(window).width() >= 700){
    		 $(".modal-dialog").width(700);
    	 } */
    	 $.ajax({
   			url : "ArrearApproveDeny.action?arear_id="+arear_id+"&actionType="+actionType,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
    } 
    
    
    
    
</script>
<%--     
    <jsp:include page="../common/SubHeader.jsp">
    	<jsp:param value="Apply Arrears" name="title"/>
    </jsp:include> --%> 
<%
    List<List<String>> alArearListReq = (List<List<String>>) request.getAttribute("alArearList");
    UtilityFunctions uF = new UtilityFunctions();
    
    %>
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
		<%session.setAttribute(IConstants.MESSAGE, ""); %>
		<s:form name="frm_PayArears" action="PayArrears" theme="simple" method="post">
			<div class="box box-default collapsed-box">
 				<div class="box-header with-border">
    				<h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organisation</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Month</p>
								<s:select theme="simple" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" key="" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Year</p>
								<s:select theme="simple" name="strYear" listKey="yearsID" id="strYear" listValue="yearsName" list="yearList" key="" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Service</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
						</div>
					</div>
				</div>
				<!-- /.box-body -->
			</div>
		</s:form>
		
		<div class="row row_without_margin">
			<a href="javascript:void(0)" onclick="addArear(0, '');" title="Apply New Arrear"><i class="fa fa-plus-circle"></i> Apply New Arrear</a>
		</div>
		
		<div class="margintop20"></div>
			<table class="table table-bordered" id="lt">
				<thead>
					<tr>
						<th>Arrear Code</th>
						<th>Arrear Name</th>
						<th>Arrear Description</th>
						<th>Employee Name</th>
						<th>Arrear Type</th>
						<th>Effective Date</th>
						<!-- <th>Duration</th> --> 
						<th>Basic Amount(PF Only)</th>
						<th>Arrear Amount</th>
						<th>Amount Paid</th>
						<th>Balance Amount</th>
						<!-- <th>Monthly Arrear</th> -->
						<th>Arrear Days</th>
						<th>Arrear Paycycle</th>
						<th class="no-sort">Action</th>
					</tr>
				</thead>
				<tbody>
				<% for (int i=0; alArearListReq!=null && i<alArearListReq.size(); i++) {
					List<String> cinnerlist = (List<String>)alArearListReq.get(i); %>
					<tr>
						<td><%=cinnerlist.get(1) %></td>
						<td><%=cinnerlist.get(2) %></td>
						<td><%=cinnerlist.get(3) %></td>
						<td><%=cinnerlist.get(4) %></td>
						<td><%=cinnerlist.get(13) %></td>
						<td><%=cinnerlist.get(5) %></td>
						<%-- <td><%=cinnerlist.get(6) %></td> --%>
						<td><%=cinnerlist.get(12) %></td>
						<td><%=cinnerlist.get(7) %></td>
						<td><%=cinnerlist.get(8) %></td>
						<td><%=cinnerlist.get(9) %></td>
						<%-- <td><%=cinnerlist.get(10) %></td> --%>
						<td><%=cinnerlist.get(14) %></td>
						<td><%=cinnerlist.get(15) %></td>
						<td><%=cinnerlist.get(11) %></td>
					</tr>
    			<% } %>
				</tbody>
			</table>
	</div>
	<!-- /.box-body -->


    <div class="modal" id="modalInfo" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">-</h4>
                </div>
                <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
                </div>
                <div class="modal-footer">
                    <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    
    <script type="text/javascript">
    $(function(){
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    	});
    	$("#f_strWLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_service").multiselect().multiselectfilter();
    	$("#f_level").multiselect().multiselectfilter();
    });
    </script>