<div id="divResult"> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page import="java.util.Iterator"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String org = (String) request.getAttribute("f_org");
	String strLocation = (String) request.getAttribute("strLocation");
	Map<String, List<String>> hmEmployeeEPF = (Map<String, List<String>>) request.getAttribute("employeeEPF");
	
	List<String> alEmp = (List<String>) request.getAttribute("alEmp");
	if(alEmp == null) alEmp = new ArrayList<String>();
	 System.out.println(alEmp);
	System.out.println("String, List<String> "+hmEmployeeEPF);
%>

<script>


$(function(){
	
	$(document).ready(function(){
		$('#lt').DataTable({
			aLengthMenu: [
				[25, 50, 100, 200, -1],
				[25, 50, 100, 200, "All"]
			],
			iDisplayLength: -1,
			dom: 'lBfrtip',
	        buttons: [],
	        order: [],
			columnDefs: [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }]
		});
		
	});
}); 

$(function(){
	 	$("select[multiple='multiple']").multiselect().multiselectfilter();
		/* $("#location").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter(); */

}); 

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

function GetXmlHttpObject() {
    if (window.XMLHttpRequest) {
        // code for IE7+, Firefox, Chrome, Opera, Safari
        return new XMLHttpRequest();
    }
    if (window.ActiveXObject) {
    	// code for IE6, IE5
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
    return null;
}


</script>
<div >
<section class="content">
    <div class="row jscroll">
        <section class=" col-lg-12 connectedSortable">
 	<div class="box box-primary">
        <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
   <div class="box box-default" style="border-top-color: #EBEBEB;">
       <div class="box-header with-border">
             <div class="box-tools pull-right">
                     <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                     <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
              </div>
             </div>
          <div class="box-body" style="padding: 5px; overflow-y: auto;">
            <s:form name="frm_UpdateEPF" id="frm_UpdateEPF" action="UpdateEPF" theme="simple">
    		<div class="row row_without_margin">
				<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
					<i class="fa fa-filter"></i>
				</div>
				<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
						<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="organisationList" key=""  
						onchange="submitForm();"/>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Location</p>
						<s:select theme="simple" name="location" id="location" listKey="wLocationId" listValue="wLocationName" 
					list="wLocationList" key=""  multiple="true"/>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Level</p>
						<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Paycyle</p>
						<s:select name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerValue="Select Paycycle" list="paycycleList"/>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">&nbsp;</p>
						<input type="button" name="submit" value="Submit" class="btn btn-primary" onclick="submitForm();"/>
					</div>
				</div>
			</div>
	    </s:form>
	 </div>
	</div>
	
	<div class="box box-primary">
	<s:form name="frm_UpdateEPF1" id="frm_UpdateEPF1" action="UpdateEPF" theme="simple">
	 <table class="table table-bordered" id="lt" style="width:100%;">
         <thead>
	           <tr>
	               <th style="text-align: left;">Employee Name</th>
	               <th style="text-align: left;">PF Amount</th>
	               <th style="text-align: left;" class="no-sort">Action</th>                                 
	          	</tr>
          </thead>
          <tbody>
          <%
         
          if(hmEmployeeEPF!=null && !hmEmployeeEPF.isEmpty()){
          	Iterator<String> itr = hmEmployeeEPF.keySet().iterator();
          	int i=0;
          while(itr.hasNext()){
        	  String empId = itr.next();
        	  List alEmp1 = hmEmployeeEPF.get(empId);
        	  if(alEmp1 == null) alEmp1=new ArrayList();
        	  if(alEmp1!=null && !alEmp1.isEmpty()){
          
          %>
	          <tr id="mydiv_<%=i%>">
	          	<td id="empId_<%=i%>"><%=alEmp1.get(0) %></td>
	          	<td id="amount_<%=i%>">
	          		<input type="text" name="pfAmount" id="pfAmount_<%=i%>" value="<%=alEmp1.get(1)%>"/>
	          	</td>
	          	<td>
	          		          	<%-- <a href="javascript:void(0)" style="float: left;" onclick="updatePF('<%=empId%>','<%=i %>');"> <img src="images1/setting.png" title="update" /></a> </td> --%>
	          	
	          <div id="myDiv_<%=i%>">
	          <input type="button" class="btn btn-primary" onclick="updatePF('<%=empId%>','<%=i %>');" value="Update">
	          </div>
	          </td>
	          	
	          </tr>
          <% }%>
         <%
         	i++;
         %>
          <%} %>
         <%} %>
          </tbody>
      </table>
      </s:form>
	
	</div>
	</div>
	</div>
 </section>
    </div>
</section>
</div> 
<script type="text/javascript" charset="utf-8">

function submitForm() {
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("location");
	var level = getSelectedValue("f_level");

	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'UpdateEPF.action?f_org='+org+'&strLocation='+location+'&strLevel='+level+'&paycycle='+paycycle,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

	function updatePF(empId, id) {
		  event.preventDefault();
		  var salaryId = document.getElementById("pfAmount_"+id).value;
		  var paycycle = document.getElementById("paycycle").value;
		  if(confirm("Are you sure, you want to update employee PF?")){
			var form_data = $("#frm_UpdateEPF1").serialize();
			$.ajax({
				type :'POST',
				url  :'UpdateEPF.action',
				data :form_data+'&empId='+empId+'&amount='+salaryId+'&type=PFUpdate&paycycle='+paycycle,
				cache:true,
				success: function(result){
					/* window.location="UpdateEPF.action"; */
					$("#myDiv_"+id).html("Updated");
		   		},
		   		error: function(result){
					/* window.location="UpdateEPF.action"; */
		   			$("#myDiv_"+id).html("Update failed");
		   		}
			});
		  }			
	}  
</script>

</div>