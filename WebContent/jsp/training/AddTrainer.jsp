<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.recruitment.FillEducational"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.select.FillYears"%>
<%@page import="com.konnect.jpms.select.FillDegreeDuration"%>
<%@page import="java.io.File"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.*" %>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %> 
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%-- <jsp:include page="../employee/CustomAjaxForAddEmployee.jsp"></jsp:include>  --%>
<style> 
    <g:compress>
        .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td {
        border-top: 1px solid #FFFFFF;
        }
  
        #div_language {
        height: 300px;
        border: solid 2px #ccc;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding: 10px;
        }
        #div_education {
        border: solid 2px #ccc;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding: 10px;
        height: 300px;
        }
        #div_skills {
        border: solid 2px #ccc;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding: 10px;
        height: 300px;
        }
        #div_hobbies {
        border: solid 2px #ccc;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding: 10px;
        height: 300px;
        }
        .wizard {
        margin: 30px auto;
        background: #fff;
        }
        .wizard .nav-tabs {
        position: relative;
        /* margin: 40px auto; */
        margin-bottom: 0;
        border-bottom-color: #e0e0e0;
        }
        .wizard > div.wizard-inner {
        position: relative;
        }
        .connecting-line {
        height: 2px;
        background: #e0e0e0;
        position: absolute;
        width: 62%;
        /* margin: 0 auto; */
        margin-left: 40px;
        left: 0;
        right: 0;
        top: 52%;
        z-index: 1;
        }
        .wizard .nav-tabs > li.active > a, .wizard .nav-tabs > li.active > a:hover, .wizard .nav-tabs > li.active > a:focus {
        color: #555555;
        cursor: default;
        border: 0;
        border-bottom-color: transparent;
        }
        span.round-tab {
        width: 50px;
        height: 50px;
        line-height: 50px;
        display: inline-block;
        border-radius: 100px;
        background: #fff;
        border: 2px solid #e0e0e0;
        z-index: 2;
        position: absolute;
        left: 0;
        text-align: center;
        font-size: 20px;
        }
        span.round-tab i{
        color:#555555;
        }
        .wizard li.active span.round-tab {
        background: #fff;
        border: 2px solid #5bc0de;
        }
        .wizard li.active span.round-tab i{
        color: #5bc0de;
        }
        span.round-tab:hover {
        color: #333;
        border: 2px solid #333;
        }
        .wizard .nav-tabs > li {
        width: 10%;
        }
        .wizard li:after {
        content: " ";
        position: absolute;
        left: 46%;
        opacity: 0;
        margin: 0 auto;
        bottom: 0px;
        border: 5px solid transparent;
        border-bottom-color: #5bc0de;
        transition: 0.1s ease-in-out;
        }
        .wizard li.active:after {
        content: " ";
        position: absolute;
        left: 42%;
        opacity: 1;
        margin: 0 auto;
        bottom: 0px;
        border: 10px solid transparent;
        border-bottom-color: #5bc0de;
        }
        .wizard .nav-tabs > li a {
        width: 50px;
        height: 50px;
        margin: 20px auto;
        border-radius: 100%;
        padding: 0;
        }
        .wizard .nav-tabs > li a:hover {
        background: transparent;
        }
        .wizard .tab-pane {
        position: relative;
        padding-top: 50px;
        }
        .wizard h3 {
        margin-top: 0;
        }
        @media( max-width : 585px ) {
        .wizard {
        width: 90%;
        height: auto !important;
        }
        span.round-tab {
        font-size: 16px;
        width: 50px;
        height: 50px;
        line-height: 50px;
        }
        .wizard .nav-tabs > li a {
        width: 50px;
        height: 50px;
        line-height: 50px;
        }
        .wizard li.active:after {
        content: " ";
        position: absolute;
        left: 35%;
        }
        }
        .table-head-highlight th{
        background-color: #f9f9f9;
        }
        .table-head-highlight th, .table-head-highlight td{
        border-right: 1px solid #f9f9f9;
        }
        .table-head-highlight th:last-child, .table-head-highlight td:last-child{
        border-right: none;
        }
        .table-head-highlight tr{
        border-bottom: 1px solid #f9f9f9;
        }
        .table-head-highlight tr:last-child{
        border-bottom: none;
        }
        .tooltip{
        top: 75px !important;
        }
        .tooltip-arrow{
        display: none;
        }
        .wizard .tab-pane {
        padding-top: 20px;
        }
        .wizard .nav-tabs {
        margin: 0px;
        }
        .wizard {
        margin: 0px;
        }
        .tdLabelheadingBg {
		font-size: 14px;
		font-weight: 600;
		}
		
		a.close-font:before{
	 	font-size: 24px;
   	    }
    </g:compress>
</style>
<%
    String struserType = (String)session.getAttribute(IConstants.USERTYPE);
    ArrayList educationalList = (ArrayList) request.getAttribute("educationalList"); 
    ArrayList alSkills = (ArrayList) request.getAttribute("alSkills"); 
    ArrayList alHobbies = (ArrayList) request.getAttribute("alHobbies");
    ArrayList alLanguages = (ArrayList) request.getAttribute("alLanguages");
    ArrayList alEducation = (ArrayList) request.getAttribute("alEducation");
    ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
    ArrayList alSiblings = (ArrayList) request.getAttribute("alSiblings");
    ArrayList alPrevEmployment = (ArrayList) request.getAttribute("alPrevEmployment");
    List degreeDurationList = (List) request.getAttribute("degreeDurationList");
    List yearsList = (List) request.getAttribute("yearsList");
    List empGenderList = (List) request.getAttribute("empGenderList");
    List skillsList = (List) request.getAttribute("skillsList");
    String strImage = (String) request.getAttribute("strImage");
    
    HashMap empServicesMap = (HashMap) request.getAttribute("empServicesMap");
    
    UtilityFunctions uF = new UtilityFunctions();
    String currentYear = (String)request.getAttribute("currentYear");
   
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    int nEmpAlphaCodeLength = 2;
    if(CF!=null && CF.getStrOEmpCodeAlpha()!=null){
    	nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();
    }
    
    String strUserType=(String)session.getAttribute(IConstants.USERTYPE);
  
    %>


<script type="text/javascript">
    $(function() {
    		$("input[name='stepSubmit']").click(function(){
    			$(".validateRequired").prop('required',true);
    			$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');
    			$(".validateEmail").prop('type','email');     
    		
    		});
    		$("input[name='stepSave']").click(function(){
    			 $(".validateRequired").prop('required',true);
    			$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');
    			$(".validateEmail").prop('type','email');    
    		
    		});
    	  $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy'});
   	      $( "#empDateOfMarriage" ).datepicker({format: 'dd/mm/yyyy'});
   	      $("input[name=prevCompanyFromDate]").datepicker({format: 'dd/mm/yyyy'});
   	      $("input[name=prevCompanyToDate]").datepicker({format: 'dd/mm/yyyy'});
   	      $("input[name=fatherDob]").datepicker({format: 'dd/mm/yyyy'});
   	      $("input[name=motherDob]").datepicker({format: 'dd/mm/yyyy'});
   	      $("input[name=spouseDob]").datepicker({format: 'dd/mm/yyyy'});
   	      $("input[name=memberDob]").datepicker({format: 'dd/mm/yyyy'});
   	      $("input[name=empPassportExpiryDate]").datepicker({format: 'dd/mm/yyyy'});
    });
</script>
<g:compress>
    <script>  
    function callDatePicker() {
    	//alert("callDatePicker ");
        //$( "#" ).datepicker({format: 'dd/mm/yyyy'});
        $( "#empDateOfMarriage" ).datepicker({format: 'dd/mm/yyyy'});
        $("input[name=prevCompanyFromDate]").datepicker({format: 'dd/mm/yyyy'});
        $("input[name=prevCompanyToDate]").datepicker({format: 'dd/mm/yyyy'});
        $("input[name=fatherDob]").datepicker({format: 'dd/mm/yyyy'});
        $("input[name=motherDob]").datepicker({format: 'dd/mm/yyyy'});
        $("input[name=spouseDob]").datepicker({daformat: 'dd/mm/yyyy'});
        $("input[name=memberDob]").datepicker({format: 'dd/mm/yyyy'});
        $("input[name=empPassportExpiryDate]").datepicker({format: 'dd/mm/yyyy'});
    }
  
      
        function fillFileStatus(ids){
        	
        	document.getElementById(ids).value=1;
        }
        
        function showMarriageDate(){
        	if( document.getElementById("empMaritalStatus")){
        		if(document.frmPersonalInfo.empMaritalStatus.options[document.frmPersonalInfo.empMaritalStatus.options.selectedIndex].value=='M'){
            		document.getElementById("trMarriageDate").style.display = 'table-row';
            	}else{
            		document.getElementById("trMarriageDate").style.display = 'none';
            	}
        	}
        	
        }
        
        <% if (alSkills!=null) {%>
        	var cnt=<%=alSkills.size()%>;
        <%}else{%>
        	var cnt =0;
        <%}%>
        
        function addSkills() {
        	            
            cnt++;
    		var trTag = document.createElement("tr");
    	    trTag.id = "row_skill"+cnt;
    	    trTag.setAttribute("class", "row_skill");
    	    trTag.innerHTML = 	"<td></td>"+"<%=request.getAttribute("sbSkills")%>" +
    	    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addSkills()\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeSkills(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a></td>"; 
    	    document.getElementById("table-skills").appendChild(trTag);
        }
       
        function removeSkills(removeId) {
    		
    		var remove_elem = "row_skill"+removeId;
    		var row_skill = document.getElementById(remove_elem); 
    		document.getElementById("table-skills").removeChild(row_skill);
    		
    	}
        
        <% if (alHobbies!=null) { %>
       		 var cnt=<%=alHobbies.size()%>;
        <%}else{%>
       		 var cnt =0;
        <%}%>
        
        function addHobbies() {
        	
        	cnt++;
        	           
    		var trTag = document.createElement("tr");
    	    trTag.id = "row_hobby"+cnt;
    	    trTag.setAttribute("class", "row_hobby");
    		trTag.innerHTML = 	"<td><input type=\"text\" style=\"width: 180px; \" name=\"hobbyName\"></input></td>" +   			    	
    	    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addHobbies()\" class=\"add-font\"></a>" +
    	 						"<a href=\"javascript:void(0)\" onclick=\"removeHobbies(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a></td></tr>";
    		                    
    	    document.getElementById("table-hobbies").appendChild(trTag);
          }
        
        function removeHobbies(removeId) {
    		
    		var remove_elem = "row_hobby"+removeId;
    		var row_hobby = document.getElementById(remove_elem); 
    		document.getElementById("table-hobbies").removeChild(row_hobby);
    		
    	}
         
        function getState(country){
        	var action= 'GetStateDetails.action?type=candidate&country_id=' + country;
        	getContent('statetdid', action); 
        }
        
        function getState1(country){
        	var action= 'GetStateDetails.action?type=candidate1&country_id=' + country;
        	getContent('statetdid1', action); 
        }
        
        <% if (alLanguages!=null) {%>
                var cnt=<%=alLanguages.size()%>;
       <% }else {%>
                var cnt =0;
       <%}%>
        
        function addLanguages() {
        	
        	cnt++;
            		
            var trTag = document.createElement("tr");
    	    trTag.id = "row_language"+cnt;
    	    trTag.setAttribute("class", "row_language");
    		trTag.innerHTML = 	"<td><input type=\"text\" style=\"width:180px;\" name=\"languageName\" ></input></td>" + 
    	 						"<td align=\"center\"><input type=\"checkbox\" name=\"isReadcheckbox\" value=\"1\"  id=\"isRead_"+cnt+"\" onchange=\"showHideHiddenField(this.id)\" /></td>" +
    							"<input type=\"hidden\" value=\"0\" name=\"isRead\" id=\"hidden_isRead_"+cnt+"\" />" +
    							"<td  align=\"center\"><input type=\"checkbox\" name=\"isWritecheckbox\" value=\"1\" id=\"isWrite_"+cnt+"\" onchange=\"showHideHiddenField(this.id)\" /></td>"+
    							"<input type=\"hidden\" value=\"0\" name=\"isWrite\" id=\"hidden_isWrite_"+cnt+"\" />" +
    							"<td  align=\"center\"><input type=\"checkbox\" name=\"isSpeakcheckbox\" value=\"1\" id=\"isSpeak_"+cnt+"\" onchange=\"showHideHiddenField(this.id)\" /></td>"+
    							"<input type=\"hidden\" value=\"0\" name=\"isSpeak\" id=\"hidden_isSpeak_"+cnt+"\" />" +
    							"<td  align=\"center\"><a href=\"javascript:void(0)\" onclick=\"addLanguages()\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeLanguages(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a></td>"; 

    	    document.getElementById("table-language").appendChild(trTag);
        }
        
    
        function removeLanguages(removeId) {
    		
    		var remove_elem = "row_language"+removeId;
    		var row_language = document.getElementById(remove_elem); 
    		document.getElementById("table-language").removeChild(row_language);
    		
    	}
       
        <% if (alEducation!=null) {%>
        	var cnt=<%=alEducation.size()%>;
        <%}else{%>
        	var cnt =0;
        <%}%>
        
        function addEducation() {
        	
        	cnt++;
      
    		var trTag = document.createElement("tr");
    		trTag.id = "row_education"+cnt;
    		trTag.setAttribute("class", "row_education");
    		trTag.innerHTML = "<td><select class=\"form-control\" name=\"degreeName\" onchange=\"checkEducation(this.value,"+cnt+")\"> "+
    						 "<%=request.getAttribute("sbdegreeDuration")%>" +
    				"<a href=\"javascript:void(0)\" onclick=\"removeEducation(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a></td></tr>";
    	 			; 
    		var trTag1 = document.createElement("tr");
    		trTag1.id = "degreeNameOtherTR"+cnt;
    		trTag1.setAttribute("class", "hide-tr");	
    		trTag1.innerHTML = "<td colspan=\"1\" style=\"text-align: right;\">Enter Academics Degree :</td><td colspan=\"5\"> " +
    			"<input  type=\"text\" name=\"degreeNameOther\" class=\"form control\" style=\"height: 25px;\"></td>"; 
    		
    		
    		console.log(trTag);
    	    document.getElementById("table-education").appendChild(trTag);
    	    document.getElementById("table-education").appendChild(trTag1);
        }
        
        
        function checkEducation(value,count){
       		if(value=="other"){
       			document.getElementById("degreeNameOtherTR"+count).style.display="table-row";
       		}
       	}
        
        function removeEducation(removeId) {
    		
    		var remove_elem = "row_education"+removeId;
    		var row_education = document.getElementById(remove_elem); 
    		document.getElementById("table-education").removeChild(row_education);
    		
    	} 
        
        var siblingcnt = 0;
        
        function addSibling() {
        	
        	siblingcnt = parseInt(siblingcnt)+1;
        	
        	var divTag = document.createElement("div");
        	divTag.setAttribute("style","float: left; width: 30%;");
        	divTag.id = "col_family_siblings"+siblingcnt;
           
            divTag.innerHTML = 	"<%=request.getAttribute("sbSibling")%>" +
            		"<tr><td colspan=\"2\" ><span style=\"float: right;\">"
            		+"<a href=\"javascript:void(0)\" onclick=\"removeSibling(this.id)\" id=\""+siblingcnt+"\" class=\"remove-font\" ></a>"
            		+"<a href=javascript:void(0) onclick=addSibling() class=add-font></a></span></td></tr>" +
                    "</table>"; 
        
                 
        	document.getElementById("div_id_family").appendChild(divTag);
        	callDatePicker();
        	
        }
        
        function removeSibling(removeId) {
        	
        	var remove_elem = "col_family_siblings"+removeId;
        	var row_skill = document.getElementById(remove_elem); 
        	document.getElementById("div_id_family").removeChild(row_skill);
        	
        }
        
        
        <% if (alDocuments!=null) {%>
      		 var documentcnt=<%=alDocuments.size()%>;
 		<%}else{%>
      		 var documentcnt = 0;
        <%}%>
        
        function addDocuments() {
        	documentcnt++;
                var table = document.getElementById('row_document_table');
        
                var rowCount = table.rows.length;
               
                var row = table.insertRow(rowCount);
                row.id = "row_document"+rowCount;
                var cell1 = row.insertCell(0);
                cell1.setAttribute("class", "txtlabel alignRight");
                cell1.setAttribute("style", "text-align: -moz-center");
        
               cell1.innerHTML = "<input type=\"hidden\" name=\"idDocType\" value=\"<%=IConstants.DOCUMENT_OTHER %>\"></input><input type=\"text\" class=\"validateRequired form-control \" style=\"width: 180px; \" name=\"idDocName\"></input>";
               
               var cell2 = row.insertCell(1);
               cell2.setAttribute("class", "txtlabel alignRight");
               cell2.setAttribute("style", "text-align: -moz-center");
               cell2.innerHTML = "<input type=\"file\" name=\"idDoc\" onchange=\"fillFileStatus('idDoc"+documentcnt+"Status')\" required /><input type=\"hidden\" name=\"idDocStatus\" id=\"idDoc"+documentcnt+"Status\" value=\"0\"></input>";
               
               var cell3 = row.insertCell(2);
               cell3.setAttribute("class", "txtlabel alignRight");
               cell3.setAttribute("style", "text-align: -moz-center");
               cell3.innerHTML = "<a href=\"javascript:void(0)\" onclick=\"addDocuments()\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeDocuments('row_document"+rowCount+"')\" id=\""+documentcnt+"\" class=\"remove-font\"></a>";
             
        }
        
        function removeDocuments(rowid)  
        {   
            var table =  document.getElementById('row_document_table');
            var row = document.getElementById(rowid);
            table.deleteRow(row.rowIndex);
            
        }  
        
        function isOnlyNumberKey(evt) {
     	   var charCode = (evt.which) ? evt.which : event.keyCode;
     	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
     	      return true; 
     	   }
     	   return false;
     	}
   
        <% if (alPrevEmployment!=null) {%>
     			var cnt=<%=alPrevEmployment.size()%>;
  		<%}else {%>
 		    	var cnt =0;
 		<%}%>
        
        function addPrevEmployment() {
        
        	cnt++;
            var divTag = document.createElement("div");
        	divTag.setAttribute("style","float: left;");
        	divTag.id = "col_prev_employer"+cnt;
        	divTag.innerHTML = "<%=request.getAttribute("sbPrevEmployment")%>" + 
        					"<tr><td colspan=\"2\" ><span style=\"float: right;\">"
            		+"<a href=\"javascript:void(0)\" onclick=\"removePrevEmployment(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a>"
            		+"<a href=javascript:void(0) onclick=addPrevEmployment() class=add-font></a></span></td></tr>" +
            		/* "<td class=\"txtlabel alignRight\"><a href=\"javascript:void(0)\" onclick=\"removePrevEmployment(this.id)\" id=\""+cnt+"\" class=\"remove-font\" >&nbsp;</td>";
        					"</tr>" + */ 
        				     "</table>";
        	document.getElementById("div_prev_employment").appendChild(divTag);
        	callDatePicker();
        }
        
        function removePrevEmployment(removeId) {
        
        	var remove_elem = "col_prev_employer" + removeId;
        	var row_document = document.getElementById(remove_elem); 
        	document.getElementById("div_prev_employment").removeChild(row_document);
        
        }
       
        function showHideHiddenField(fieldId) {
        	
        	if(document.getElementById(fieldId).checked) {
        		document.getElementById( "hidden_"+fieldId).value="1";
        	}else {
        		document.getElementById( "hidden_"+fieldId).value="0";
        	}
        }
        
        function checkRadio(obj,val){
        	document.getElementById(val).disabled=true;
        	if(obj.value=='false'){
        		document.getElementById(val).disabled=true;
        		document.getElementById(val+'File').disabled=true;
        
        	}else{
        		document.getElementById(val).disabled=false;
        		document.getElementById(val+'File').disabled=false;
        	}
        }
        
        function copyAddress(obj){
        	if(obj.checked){
        		
        		var sel=document.getElementById("countryTmp");
        		for(var i = 0, j = sel.options.length; i < j; ++i) {
        	        if(sel.options[i].innerHTML === document.getElementById("country").options[document.getElementById("country").selectedIndex].text) {
        	           sel.selectedIndex = i;
        	           break;
        	        }
        	    }
        		
        		sel=document.getElementById("stateTmp");
        		for(var i = 0, j = sel.options.length; i < j; ++i) {
        	        if(sel.options[i].innerHTML === document.getElementById("state").options[document.getElementById("state").selectedIndex].text) {
        	           sel.selectedIndex = i;
        	           break;
        	        }
        	    }
        		
        		document.getElementById("frmPersonalInfo_empAddress1Tmp").value = document.getElementById("frmPersonalInfo_empAddress1").value;
        		document.getElementById("frmPersonalInfo_empAddress2Tmp").value = document.getElementById("frmPersonalInfo_empAddress2").value;
        		document.getElementById("frmPersonalInfo_cityTmp").value = document.getElementById("frmPersonalInfo_city").value;
        		document.getElementById("frmPersonalInfo_empPincodeTmp").value = document.getElementById("frmPersonalInfo_empPincode").value;
        	
        	}else{
        		document.getElementById("frmPersonalInfo_empAddress1Tmp").value = '';
        		document.getElementById("frmPersonalInfo_empAddress2Tmp").value = '';
        		document.getElementById("frmPersonalInfo_cityTmp").value = '';
        		document.getElementById("frmPersonalInfo_empPincodeTmp").value = '';
        		
        		var sel=document.getElementById("countryTmp");
        		for(var i = 0, j = sel.options.length; i < j; ++i) {
        	        if(sel.options[i].innerHTML === document.getElementById("country").options[0].text) {
        	           sel.selectedIndex = i;
        	           break;
        	        }
        	    }
        		
        		sel=document.getElementById("stateTmp");
        		for(var i = 0, j = sel.options.length; i < j; ++i) {
        	        if(sel.options[i].innerHTML === document.getElementById("state").options[0].text) {
        	           sel.selectedIndex = i;
        	           break;
        	        }
        	    }
        	}
        	
        }
        
        function submitForm(){
        	
        	document.getElementById('frmAvailibility').submit();
        	
        	window.setTimeout(function() {  
        		parent.window.location="Applications.action";
        		}, 500); 
        }
        
        function closeForm() {
        	$("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
        	$.ajax({
        		url:'TrainerInfo.action',
        		cache:false,
        		success:function(data){
        			$("#divResult").html(data);
        		}
        	});
        	
        }
        function checkMailID(value) {
        	//alert("value ===> "+value);
             xmlhttp = GetXmlHttpObject();
             if (xmlhttp == null) {
                     alert("Browser does not support HTTP Request");
                     return;
             } else {
                     var xhr = $.ajax({
                       url : "EmailValidation.action?trainerEmail=" + value,
                       cache : false,
                       success : function(data) {
                       	//alert("data.length ===> "+data.length + "  data ===> "+data);
                       	if(data.length > 1){
                       		document.getElementById("empEmail").value = "";
                            document.getElementById("emailValidatorMessege").innerHTML = data;
                       	}else{
                       		document.getElementById("emailValidatorMessege").innerHTML = data;
                       	}
                       }
                     });
             	}
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
        
        function checkImageSize(){
        	 if (window.File && window.FileReader && window.FileList && window.Blob){
        		 if(document.getElementById("empImage")){
        			 var fsize = $('#empImage')[0].files[0].size;
                     var ftype = $('#empImage')[0].files[0].type;
                     var fname = $('#empImage')[0].files[0].name;
                     var flag = true;
                     switch(ftype){
                         case 'image/png':
                         case 'image/gif':
                         case 'image/jpeg':
                         case 'image/pjpeg':
                             if(fsize>500000){ //do something if file size more than 1 mb (1048576)
                                 alert("You are trying to upload a larger file than 500kb.");
                                 flag = false;
                             }else{
                                 //alert(fsize +" bites\nYou are good to go!");
                                 flag = true;
                             }
                             break;
                         default:
                             alert('Unsupported File!');
                         	flag = false;
                     }
                     if(flag){
                     	return true;
                     } else {
                     	return false;
                     }
        		 } 
               
               
           }else{
               alert("Please upgrade your browser, because your current browser lacks some new features we need!");
               return false;
           }
        }
                
    </script>
</g:compress>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <h4 class="box-title">
                        <%if(session.getAttribute(IConstants.USERID)!=null){ 
                            %>
                        <%=(request.getParameter("operation")!=null && !request.getParameter("operation").equals(""))?"Edit":"Add" %> Trainer Detail
                        <%}else{ %>
                        Add Your Details
                        <%}%>
                    </h4>
                    <div class="pull-right">
						<a href="javascript:void(0);" onclick="closeForm()" class="close-font" style="margin-right:20px;"/>
					</div>
                </div>
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div class="leftbox reportWidth" >
                        <div class="wizard">
                            <%
                                String strEmpType = (String) session.getAttribute("USERTYPE");
                                String strMessage = (String) request.getAttribute("MESSAGE");
                                if (strMessage == null) {
                                	strMessage = "";
                                }
                                
                                String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
                                String operation = (String) request.getAttribute("operation");
                                %>
                            
                            <% if (operation == null || operation.equalsIgnoreCase("") || operation.equalsIgnoreCase("E")) {%>
                            
<%-- 							  <div class="steps">
									<s:if test="step==1">
									  <span class="current"> Personal Information :</span>
									  <span class="next"> Background Information :</span>
									  <span class="next"> Family Information :</span>
									  <span class="next"> Previous Employment :</span>
									   <span class="next"> References :</span>
									   <span class="next"> Medical Information :</span>
									   <span class="next"> Documentation :</span>
									
									</s:if>
									<s:if test="step==2">
									<span class="prev"><a href="AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
									  	<span class="current">Background Information : </span>
									    <span class="next">Family Information : </span>
									    <span class="next">Previous Employment: </span>
									    <span class="next">References : </span>
									    <span class="next">Medical Information : </span>
									    <span class="next">Documentation : </span>
									
									</s:if>
									<s:if test="step==3">
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>"
										style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
									  <span class="next"><a href="AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>"
											style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
									  <span class="current">Family Information : </span>
									  <span class="next">Previous Employment: </span>
									   <span class="next">References : </span>
									   <span class="next">Medical Information : </span>
									   <span class="next">Documentation : </span>
									
									</s:if>
									<s:if test="step==4">
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
									  <span class="prev"><a href="AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>"
										style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
									  <span class="next"> <a href="AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>"
										style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
									  <span class="current">Previous Employment: </span>
									   <span class="next">References : </span>
									   <span class="next">Medical Information : </span>
									   <span class="next">Documentation : </span>
									
									</s:if>
									<s:if test="step==5">
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
									  <span class="prev"><a href="AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
									  <span class="next"> <a href="AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
									  <span class="prev"><a href="AddTrainer.action?operation=E&step=3&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment :</a></span>
									   <span class="current">References : </span>
									   <span class="next">Medical Information : </span>
									   <span class="next">Documentation : </span>
									
									</s:if>
									<s:if test="step==6">
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
									   <span class="next"> <a href="AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=3&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment :</a></span>
									   	<span class="prev"><a href="AddTrainer.action?operation=E&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
									   <span class="current">Medical Information : </span>
									   <span class="next">Documentation : </span>
									
									</s:if>
									<s:if test="step==7">
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
									   <span class="next"> <a href="AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=3&empId=<%=request.getAttribute("empId")%>w"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment :</a></span>
									   <span class="prev"><a href="AddTrainer.action?operation=E&step=4&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
										<span class="prev"><a href="AddTrainer.action?operation=E&step=5&empId=<%=request.getAttribute("empId")%>"
													style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
									   <span class="current">Documentation : </span>
									
									</s:if>
									
									</div> --%>
	                            <div class="wizard-inner 1st">
	                                <div class="connecting-line"></div>
	                                <ul class="nav nav-tabs" role="tablist">
	                                    <s:if test="step==1">
	                                        <li role="presentation" class="active">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="disabled">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="disabled">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="disabled">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    	                                    
	                                    <s:if test="step==2">
	                                        <li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="active">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="disabled">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="disabled">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    	                                    
	                                    <s:if test="step==3">
	                                        <li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="active">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="disabled">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    	                                    
	                                    <s:if test="step==4">
	                                        <li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="active">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    	                                    
	                                    <s:if test="step==5">
	                                        <li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=3&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="active">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    	                                    
	                                    <s:if test="step==6">
	                                        <li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=3&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=4&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="active">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="disabled">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    	                                    
	                                    <s:if test="step==7">
	                                        <li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=3&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=4&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddTrainer.action?operation=E&step=5&empId=<%=request.getAttribute("empId")%>')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="active">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                </ul>
	                            </div>
                            <%
                                } else if (operation != null && operation.equalsIgnoreCase("U")) {
                                %>
	                            <div class="wizard-inner 2nd">
	                                <div class="connecting-line"></div>
	                                <ul class="nav nav-tabs" role="tablist">
	                                    <s:if test="step==1">
	                                    	<li role="presentation" class="active">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=0&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=1&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=2&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=3&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=5&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=6&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    
	                                    <s:if test="step==2">
	                                    	<li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=0&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="active">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=1&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=2&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=3&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=5&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=6&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    
	                                    <s:if test="step==3">
	                                    	<li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=0&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=1&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="active">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=2&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=3&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=5&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=6&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    
	                                    <s:if test="step==4">
	                                    	<li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=0&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=1&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=2&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="active">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=3&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=5&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=6&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    
	                                    <s:if test="step==5">
	                                    	<li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=0&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=1&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=2&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=3&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="active">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=5&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=6&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    
	                                    <s:if test="step==6">
	                                    	<li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=0&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=1&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=2&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=3&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="active">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=5&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=6&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    
	                                    <s:if test="step==7">
	                                    	<li role="presentation" class="">
		                                        <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=0&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=1&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=2&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
		                                    </li>
		                                    <li role="presentation" class="">
	                                        	<a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=3&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="">
	                                        	<a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=5&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	                                    	</li>
	                                    	<li role="presentation" class="active">
	                                        	<a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation" onclick="loadStepOnClick('AddTrainer.action?operation=U&step=6&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view')"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	                                    	</li>
	                                    </s:if>
	                                    
	                                </ul>
	                            </div>
                            <%
                                }
                                %>
                            <div class="tab-content">
                                <s:if test="step==1">
                                    <div class="tab-pane active" role="tabpanel" id="pi">
                                        <div>
                                            <s:form theme="simple" action="AddTrainer" name="frmPersonalInfo" id="frmPersonalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkImageSize();">
                                                <s:hidden name="operation" />
                                                <%-- <s:hidden name="recruitId" /> --%>
                                                <s:hidden name="empId" />
                                                <s:hidden name="mode" />
                                                <s:hidden name="step" />
                                                <%-- <s:hidden name="jobcode" /> --%>
                                                <div>
                                                    <table border="0" class="table">
                                                        <tr>
                                                            <td class="" colspan="2">
                                                                <span style="color:#68AC3B; font-size:22px">Step 1 : </span> <span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;"> Enter Trainer Personal Information</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td height="10px">&nbsp;</td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2>
                                                                <s:fielderror >
                                                                    <s:param>empFname</s:param>
                                                                </s:fielderror>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">First Name:<sup>*</sup></td>
                                                            <td>
                                                                <%if(session.getAttribute("isApproved")==null) {%>
                                                                <s:textfield name="empFname" cssClass="validateRequired form-control " required="true"/>
                                                                <%}else{%>
                                                                <s:textfield name="empFname" required="true" disabled="true" cssClass=" form-control "/>
                                                                <s:hidden name="empFname" />
                                                                <%}%>
                                                                <span class="hint">Trainer's first name.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2>
                                                                <s:fielderror >
                                                                    <s:param>empLname</s:param>
                                                                </s:fielderror>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Last Name:<sup>*</sup></td>
                                                            <td>
                                                                <%if(session.getAttribute("isApproved")==null) {%>
                                                                <s:textfield name="empLname" cssClass="validateRequired form-control " required="true"/>
                                                                <%}else{%>
                                                                <s:textfield name="empLname" required="true" disabled="true" cssClass=" form-control "/>
                                                                <s:hidden name="empLname" />
                                                                <%}%>
                                                                <span class="hint">Trainer's last name.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2>
                                                                <s:fielderror >
                                                                    <s:param>empEmail</s:param>
                                                                </s:fielderror>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight" >Personal Email Id:<sup>*</sup></td>
                                                            <td>
                                                                <%if(session.getAttribute("isApproved")==null) {%>
                                                                <s:textfield name="empEmail" id="empEmail" cssClass="validateRequired form-control  validateEmail" required="true" onchange="checkMailID(this.value);"/>
                                                                <!-- getContent('emailValidatorMessege','EmailValidation.action?candiEmail='+this.value) -->
                                                                <%}else{%>
                                                                <s:textfield name="empEmail" id="empEmail" required="true" disabled="true" cssClass=" form-control "/>
                                                                <s:hidden name="empEmail" id="empEmail"/>
                                                                <%}%>
                                                                <span class="hint">Email id is required as the user will received all information on this id.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan="2">
                                                                <div id="emailValidatorMessege" style="font-size: 12px; float: right;"></div>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2 style="font-size: 14px;">
                                                                Permanent Address:
                                                                <hr style="background-color:#346897;height:1px">
                                                                &nbsp;
                                                                <s:fielderror >
                                                                    <s:param>empAddress1</s:param>
                                                                </s:fielderror>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Address1:<sup>*</sup></td>
                                                            <td>
                                                                <s:textfield name="empAddress1" cssClass="validateRequired form-control " required="true"/>
                                                                <span class="hint">Trainer current address.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Address2:</td>
                                                            <td>
                                                                <s:textfield name="empAddress2" cssClass=" form-control "/>
                                                                <span class="hint">Trainer current address. (optional)<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2>
                                                                <s:fielderror >
                                                                    <s:param>city</s:param>
                                                                </s:fielderror>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Suburb:<sup>*</sup></td>
                                                            <td>
                                                                <s:textfield name="city" cssClass="validateRequired form-control " required="true"/>
                                                                <span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2>
                                                                <s:fielderror >
                                                                    <s:param>country</s:param>
                                                                </s:fielderror>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Select Country:<sup>*</sup></td>
                                                            <td>
                                                                <s:select id="country" cssClass="validateRequired form-control "
                                                                    name="country" listKey="countryId" 	listValue="countryName" headerKey="" headerValue="Select Country"
                                                                    onchange="getState(this.value);"
                                                                    list="countryList" key="" required="true" />
                                                                <span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2>
                                                                <s:fielderror >
                                                                    <s:param>state</s:param>
                                                                </s:fielderror>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Select State:<sup>*</sup></td>
                                                            <td id="statetdid">
                                                                <s:select theme="simple" title="state" cssClass="validateRequired form-control "
                                                                    id="state" name="state" listKey="stateId" listValue="stateName" headerKey="" headerValue="Select State"		
                                                                    list="stateList" key="" required="true" />
                                                                <span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                        <!-- ===start parvez date: 30-07-2022=== -->
                                                            <!-- <td class="txtlabel alignRight">Postcode:</td> -->
                                                            <td class="txtlabel alignRight">Pincode:</td> 
                                                        <!-- ===end parvez date: 30-07-2022=== -->    
                                                            <td>
                                                                <s:textfield name="empPincode" onkeypress="return isOnlyNumberKey(event)" label="Trainer Pincode" cssClass=" form-control"/>
                                                                <span class="hint">Trainer's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="border-bottom:1px solid #346897; font-size: 14px;">
                                                                Temporary Address:
                                                            </td>
                                                            <td style="border-bottom:1px solid #346897;">
                                                            	<div>
                                                                    <input type="checkbox" onclick="copyAddress(this);" />Same as above
                                                                </div>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Address1:<sup>*</sup></td>
                                                            <td>
                                                                <s:textfield name="empAddress1Tmp" cssClass="validateRequired text-input form-control "  required="true"/>
                                                                <span class="hint">Trainer current address.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Address2:</td>
                                                            <td>
                                                                <s:textfield name="empAddress2Tmp"  cssClass=" form-control "/>
                                                                <span class="hint">Trainer current address. (optional)<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Suburb:<sup>*</sup></td>
                                                            <td>
                                                                <s:textfield name="cityTmp" cssClass="validateRequired text-input form-control " required="true"/>
                                                                <span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Select Country:<sup>*</sup></td>
                                                            <td>
                                                                <s:select id="countryTmp" cssClass="validateRequired form-control "
                                                                    name="countryTmp" listKey="countryId" 	listValue="countryName" headerKey="" headerValue="Select Country"
                                                                    onchange="getState1(this.value);"
                                                                    list="countryList" key="" required="true" />
                                                                <span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Select State:<sup>*</sup></td>
                                                            <td id="statetdid1">
                                                                <s:select theme="simple" title="state" cssClass="validateRequired form-control "
                                                                    id="stateTmp" name="stateTmp" listKey="stateId" listValue="stateName" headerKey="" headerValue="Select State"		
                                                                    list="stateList" key="" required="true" />
                                                                <span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                      <!-- ===start parvez date: 30-07-2022=== -->      
                                                            <!-- <td class="txtlabel alignRight">Postcode:</td> -->
                                                            <td class="txtlabel alignRight">Pincode:</td>
                                                      <!-- ===end parvez date: 30-07-2022=== -->      
                                                            <td>
                                                                <s:textfield name="empPincodeTmp" label="Trainer Pincode" onkeypress="return isOnlyNumberKey(event)" cssClass=" form-control  validateNumber"/>
                                                                <span class="hint">Trainer's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <%-- <tr><td class="txtlabel alignRight">Trainer Work Location:<sup>*</sup></td><td>	<s:select theme="simple" name="strwLocation" listKey="wLocationId"
                                                            listValue="wLocationName" headerKey="" headerValue="All Locations" list="wLocationList" key="0" cssClass="validateRequired text-input"/></td></tr> --%>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Landline Number:<sup>*</sup></td>
                                                            <td>
                                                                <s:textfield name="empContactno" onkeypress="return isOnlyNumberKey(event)" cssClass="validateRequired text-input form-control  validateNumber" />
                                                                <span class="hint">Trainer's contact no. (optional but recommended)<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Mobile Number:<sup>*</sup></td>
                                                            <td>
                                                                <s:textfield name="empMobileNo" onkeypress="return isOnlyNumberKey(event)" cssClass="validateRequired text-input form-control  validateNumber" />
                                                                <span class="hint">Trainer's Mobile No<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Passport Number:</td>
                                                            <td>
                                                                <s:textfield name="empPassportNo" cssClass=" form-control "/>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Passport Expiry Date:</td>
                                                            <td>
                                                                <s:textfield name="empPassportExpiryDate" cssClass=" form-control " />
                                                        <tr>
                                                            <td class="txtlabel alignRight">Blood Group:</td>
                                                            <td>
                                                                <s:select theme="simple" cssClass=" form-control " name="empBloodGroup" listKey="bloodGroupId"
                                                                    listValue="bloodGroupName" headerKey="0" headerValue="Select Blood Group"		
                                                                    list="bloodGroupList" key="" required="true" />
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2>
                                                                <s:fielderror >
                                                                    <s:param>empDateOfBirth</s:param>
                                                                </s:fielderror>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Date Of Birth:<sup>*</sup></td>
                                                            <td>
                                                                <s:textfield name="empDateOfBirth" id="empDateOfBirth" cssClass="validateRequired text-input form-control " required="true"></s:textfield>
                                                                <span class="hint">Trainer's Date Of Birth.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2>
                                                                <s:fielderror >
                                                                    <s:param>empGender</s:param>
                                                                </s:fielderror>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Gender:<sup>*</sup></td>
                                                            <td>
                                                                <s:select theme="simple" cssClass="validateRequired form-control " label="Select Gender" name="empGender" listKey="genderId"
                                                                    listValue="genderName" headerKey="" headerValue="Select Gender"		
                                                                    list="empGenderList" key="" required="true" />
                                                                <span class="hint">Select Gender.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="txtlabel alignRight">Marital Status:</td>
                                                            <td>
                                                                <s:select theme="simple" name="empMaritalStatus" id="empMaritalStatus" listKey="maritalStatusId"
                                                                    listValue="maritalStatusName" headerKey="0" headerValue="Select Marital Status"  cssClass=" form-control "		
                                                                    list="maritalStatusList" key="" required="true" onchange="showMarriageDate();"/>
                                                            </td>
                                                        </tr>
                                                        <tr id="trMarriageDate">
                                                            <td class="txtlabel alignRight">Date Of Marriage<sup></sup>:</td>
                                                            <td>
                                                                <s:textfield name="empDateOfMarriage" id="empDateOfMarriage"  cssClass=" form-control "></s:textfield>
                                                                <span class="hint">Trainer's Date Of Marriage.<span class="hint-pointer">&nbsp;</span></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan=2 style="font-size: 14px;">
                                                                Update Trainer image
                                                                <hr style="background-color:#346897;height:1px">
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                        	<td colspan="2">
                                                        		<table class="table">
			                                                        <tr>
			                                                            <td>
			                                                                <%-- <img height="100" width="100" id="profilecontainerimg" src="userImages/<%=strImage!=null? strImage:"avatar_photo.png"%>" /> --%>
			                                                                <%if(docRetriveLocation == null) { %>
			                                                                <img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + strImage %>" />
			                                                                <%} else { %>
			                                                                <img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_TRAINER+"/"+IConstants.I_IMAGE+"/"+(String)request.getAttribute("empId")+"/"+strImage %>" />
			                                                                <%} %>
			                                                            </td>
			                                                        </tr>
			                                                        <tr>
			                                                            <td>
			                                                                <%-- <s:file name="empImage" ></s:file> --%>
			                                                                <s:file name="empImage" id="empImage" onchange="readImageURL(this, 'profilecontainerimg');"></s:file>
			                                                                <span style="color:#4286f4">Image size must be smaller than or equal to 500kb.</span>
			                                                            </td>
			                                                        </tr>
			                                                    </table>
                                                        	</td>
                                                        	<td></td>
                                                        </tr>
                                                    </table>
                                                </div>
                                                <div class="clr"></div>
                                                <div>
                                                    <table class="table">
                                                        <s:if test="mode==null">
                                                            <tr>
                                                                <td colspan="2" align="left">
                                                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px;" value="Submit & Proceed" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:if>
                                                        <s:else>
                                                            <tr>
                                                                <td colspan="2" align="left">
                                                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:else>
                                                    </table>
                                                </div>
                                            </s:form>
                                        </div>
                                    </div>
                                </s:if>
                                <s:if test="step==2">
                                    <div class="tab-pane active" role="tabpanel" id="bi">
                                        <div>
                                            <s:form theme="simple" action="AddTrainer" id="frmBackgroundInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
                                                <s:hidden name="operation" />
                                                <%-- <s:hidden name="recruitId" /> --%>
                                                <s:hidden name="empId" />
                                                <s:hidden name="mode" />
                                                <s:hidden name="step" />
                                                <div><span style="color:#68AC3B; font-size:22px">Step 2 : </span><span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;">Enter Trainers background information</span> </div>
                                                <div class="row row_without_margin" style="margin-top: 20px;">
                                                    <div id="div_skills" class=" col-lg-5 col-md-5 col-sm-12" style="padding-left: 5px;padding-right: 5px;border: solid 2px #F5F5F5;height: 300px;overflow-y: auto;margin-left: 10px; margin-right: 10px;">
                                                            <h4 style="margin:5px 0px 10px 0px;background: aliceblue;padding: 5px;">Enter trainer skills and their values</h4>
                                                            <table class="table table-head-highlight">
                                                            	<tbody id="table-skills">
                                                            		<tr>
	                                                            		<th></th>
																   	  	<th>Skill Name</th>
																   	  	<th>Skill Rating</th>
																   	  	<th style="width:10%"></th>
															   	  	</tr>
															   	  	 <% 	
	                                                                if(alSkills!=null && alSkills.size()!=0){
	                                                                	String empId = (String)((ArrayList)alSkills.get(0)).get(3);
	                                                                    
	                                                                	for(int i=0; i<alSkills.size(); i++) {
	                                                                %>
	                                                             	<tr id="row_skill<%=i%>" class="row_skill">
	                                                             		<td><%if(i==0){ %>
                                                                           [PRI]
                                                                            <%}%>
                                                                        </td>
                                                                        <td>
                                                                        	<select name="skillName" class="form-control ">
                                                                                <%for(int k=0; k< skillsList.size(); k++) { 
                                                                                
                                                                                    if( (((FillSkills)skillsList.get(k)).getSkillsId()+"").equals( (String)((ArrayList)alSkills.get(i)).get(4) )) {
                                                                                    %>
	                                                                                <option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>" selected="selected">
	                                                                                    <%=((FillSkills)skillsList.get(k)).getSkillsName() %>
	                                                                                </option>
	                                                                                <%}else { %>
		                                                                                <option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>">
		                                                                                    <%=((FillSkills)skillsList.get(k)).getSkillsName() %>
		                                                                                </option>
	                                                                                <% }
                                                                                   }%>
                                                                            </select>
                                                                        </td>
                                                                        <td>
                                                                        	<select name="skillValue" style="width: 105px;" class="form-control ">
                                                                                <%for(int k=1; k< 11; k++) { 
                                                                                    if( (k+"").equals(((String)((ArrayList)alSkills.get(i)).get(2)))) {
                                                                                    %>
		                                                                                <option value="<%=k%>" selected="selected">
		                                                                                    <%=k%>
		                                                                                </option>
                                                                                	<%}else {%>
		                                                                                <option value="<%=k%>">
		                                                                                    <%=k%>
		                                                                                </option>
                                                                                	<% }
                                                                                  }%>
                                                                            </select>
                                                                        </td>
                                                                        <td>
                                                                        	<a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a>
                                                                        	<%if(i>0){ %>
	                                                                        <a href="javascript:void(0)" onclick="removeSkills(this.id)" id=<%=i%> class="remove-font" ></a>
	                                                                        <% } %>
                                                                        </td>
	                                                             	</tr>   
	                                                                <% }
	                                                                	}else{ %>
	                                                                <tr id="row_skill" class="row_skill">
	                                                                	<td>
	                                                                		[Pri]
	                                                                	</td>
	                                                                	<td>
	                                                                		<select name="skillName" class="form-control ">
                                                                                <option value="">Select Skill Name</option>
                                                                                <%for(int k=0; k< skillsList.size(); k++) {%> 
                                                                                <option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>">
                                                                                    <%=((FillSkills)skillsList.get(k)).getSkillsName() %>
                                                                                </option>
                                                                                <%}%>
                                                                            </select>
	                                                                	</td>
	                                                                	<td>
	                                                                		<select name="skillValue" style="width: 105px;" class="form-control ">
                                                                                <option value="">Skill Rating</option>
                                                                                <%for(int k=1; k< 11; k++) {%>
                                                                                <option value="<%=k%>">
                                                                                    <%=k%>
                                                                                </option>
                                                                                <%}%>
                                                                            </select>
	                                                                	</td>
	                                                                	<td>
	                                                                		<a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a>
	                                                                	</td>
	                                                                </tr>	
	                                                                <%} %>
                                                            	</tbody>
                                                            </table>
                                                    </div>
                                                    <div id="div_education" class=" col-lg-5 col-md-5 col-sm-12" style="padding-left: 5px;padding-right: 5px;border: solid 2px #F5F5F5;height: 300px;overflow-y: auto;margin-left: 10px; margin-right: 10px;">
                                                        <div>
                                                            <h4 style="margin:5px 0px 10px 0px;background: aliceblue;padding: 5px;">Enter trainer educational details</h4>
                                                            <table class="table table-head-highlight">
                                                            	<tbody id="table-education">
                                                            		<tr>
                                                                        <th>Degree Name</th>
                                                                        <th>Duration</th>
                                                                        <th>Completion Year</th>
                                                                        <th>Grade</th>
                                                                        <th></th>
                                                                    </tr>
                                                                    <% 	
	                                                                if(alEducation!=null && alEducation.size()!=0){
	                                                                	for(int i=0; i<alEducation.size(); i++) {
	                                                                %>
	                                                                <tr id="row_education<%=i%>" class="">
	                                                                	<td>
                                                                            <select name="degreeName" style="width: 120px;" class="form-control ">
                                                                               
                                                                                <%for(int k=0; k< educationalList.size(); k++) { 
                                                                                	 
                                                                                    if( (((FillEducational)educationalList.get(k)).getEduId()+"").equals( (String)((ArrayList)alEducation.get(i)).get(5) )) {
                                                                                    %>
                                                                                <option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" selected="selected">
                                                                                    <%=((FillEducational)educationalList.get(k)).getEduName() %>
                                                                                </option>
                                                                                <%}else { %>
                                                                                <option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" >
                                                                                    <%=((FillEducational)educationalList.get(k)).getEduName() %>
                                                                                </option>
                                                                                <% }
                                                                                    }%>
                                                                            </select>
                                                                        </td>
                                                                        <td>
                                                                            <select name="degreeDuration" style="width: 90px;" class="form-control ">
                                                                                <%for(int k=0; k< degreeDurationList.size(); k++) { 
                                                                                    if( (((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationID()+"").equals( (String)((ArrayList)alEducation.get(i)).get(2) )) {
                                                                                    %>
                                                                                <option value="<%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationID() %>" selected="selected">
                                                                                    <%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationName() %>
                                                                                </option>
                                                                                <%}else { %>
                                                                                <option value="<%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationID() %>">
                                                                                    <%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationName() %>
                                                                                </option>
                                                                                <% }
                                                                                    }%>
                                                                            </select>
                                                                        </td>
                                                                        <td>
                                                                            <select name="completionYear" style="width: 110px;" class="form-control ">
                                                                                <%for(int k=0; k< yearsList.size(); k++) { 
                                                                                    if( (((FillYears)yearsList.get(k)).getYearsID()+"").equals( (String)((ArrayList)alEducation.get(i)).get(3) )) {
                                                                                    %>
                                                                                <option value="<%=((FillYears)yearsList.get(k)).getYearsID() %>" selected="selected">
                                                                                    <%=((FillYears)yearsList.get(k)).getYearsName() %>
                                                                                </option>
                                                                                <%}else { %>
                                                                                <option value="<%=((FillYears)yearsList.get(k)).getYearsID() %>">
                                                                                    <%=((FillYears)yearsList.get(k)).getYearsName() %>
                                                                                </option>
                                                                                <% }
                                                                                    }%>
                                                                            </select>
                                                                        </td>
                                                                        <td><input type="text" style="width: 88px;" name="grade" value="<%=((ArrayList)alEducation.get(i)).get(4)%>" class=" form-control "></input></td>
                                                                        <td><a href="javascript:void(0)" onclick="addEducation()" class="add-font"></a>
                                                                        <%if(i>0){ %>
                                                                        <a href="javascript:void(0)" onclick="removeEducation(this.id)" id=<%=i%> class="remove-font" ></a>
                                                                        <% } %>
                                                                        </td>
	                                                                </tr>
	                                                                <%}
                                                                	}else { %>
                                                                		<tr id="row_education" class="row_education">
                                                                			<td>
	                                                                            <select name="degreeName"	style="width:110px;"  onchange="checkEducation(this.value,0);"  class="form-control ">
	                                                                                <option value="">Degree</option>
	                                                                                <%for(int k=0; k< educationalList.size(); k++) {%> 
	                                                                                <option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" >
	                                                                                    <%=((FillEducational)educationalList.get(k)).getEduName() %>
	                                                                                </option>
	                                                                                <%} %>
	                                                                                <option value="other">Other</option>
	                                                                            </select>
	                                                                        </td>
	                                                                        <td>
	                                                                            <s:select name="degreeDuration"	cssStyle="width: 90px;" listKey="degreeDurationID" listValue="degreeDurationName" headerKey=""
	                                                                                headerValue="Duration" list="degreeDurationList" key=""
	                                                                                required="true"  cssClass=" form-control "/>
	                                                                        </td>
	                                                                        <td>
	                                                                            <s:select name="completionYear"	cssStyle="width: 110px;" listKey="yearsID" listValue="yearsName" headerKey=""
	                                                                                headerValue="Completion Year" list="yearsList" key=""
	                                                                                required="true"  cssClass=" form-control "/>
	                                                                        </td>
	                                                                        <td><input type="text" style="width: 88px;" name="grade" class=" form-control "></input></td>
	                                                                        <td><a href="javascript:void(0)" onclick="addEducation()" class="add-font"></a></td>
	                                                                		</tr>
	                                                                		<tr id="degreeNameOtherTR0" style="display:none;">
		                                                                        <td style="text-align:right;">Enter Education :</td>
		                                                                        <td colspan="3"> 
		                                                                            <input type="text" name="degreeNameOther" class=" form-control ">
		                                                                        </td>
		                                                                    </tr>
                                                                	<%} %>
                                                            	</tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="row row_without_margin" style="margin-top: 20px;">
                                                    <div id="div_languages" class=" col-lg-5 col-md-5 col-sm-12" style="padding-left: 5px;padding-right: 5px;border: solid 2px #F5F5F5;height: 300px;overflow-y: auto;margin-left: 10px; margin-right: 10px;">
                                                    	<h4 style="margin:5px 0px 10px 0px;background: aliceblue;padding: 5px;">Enter trainer languages</h4>
                                                            <table class="table table-head-highlight">
                                                            	<tbody id="table-language">
                                                            		<tr>
                                                            			<th>Language Name</th>
                                                            			<th>Read</th>
                                                            			<th>Write</th>
                                                            			<th>Speak</th>
                                                            			<th></th>
                                                            		</tr>
                                                            		<% 	
	                                                                if(alLanguages!=null && alLanguages.size()!=0){
	                                                                	for(int i=0; i<alLanguages.size(); i++) {
	                                                                %>
		                                                                <tr>
		                                                                	<td><input type="text" style="width: 180px;" name="languageName" class=" form-control " value="<%=((ArrayList)alLanguages.get(i)).get(1)%>" ></input></td>
	                                                                        <td width="50px" align="center" >
	                                                                            <% 
	                                                                                if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(2)) ) { 
	                                                                                %>
	                                                                            <input type="checkbox" name="isReadcheckbox" value="1" id="isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                                                                                checked="checked" onchange="showHideHiddenField(this.id)" />
	                                                                            <input type="hidden" name="isRead" value="1" id="hidden_isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                                                                            <%}else { %>
	                                                                            <input type="checkbox" name="isReadcheckbox" value="0" id="isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                                                                                onchange="showHideHiddenField(this.id)" />
	                                                                            <input type="hidden" name="isRead" value="0" id="hidden_isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                                                                            <%} %>
	                                                                        </td>
	                                                                        <td width="50px" align="center" id="td_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>">
	                                                                            <% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(3)) ) { %>
	                                                                            <input type="checkbox" name="isWritecheckbox" value="1" checked="checked" id="isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>"  
	                                                                                onchange="showHideHiddenField(this.id)"	/>
	                                                                            <input type="hidden" name="isWrite" value="1" id="hidden_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                                                                            <%}else { %>
	                                                                            <input type="checkbox" name="isWritecheckbox" value="0" id="isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                                                                                onchange="showHideHiddenField(this.id)"	/>
	                                                                            <input type="hidden" name="isWrite" value="0" id="hidden_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                                                                            <%} %>
	                                                                        </td>
	                                                                        <td width="50px" align="center" id="td_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>">
	                                                                            <% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(4)) ) { %>
	                                                                            <input type="checkbox" name="isSpeakcheckbox" value="1" checked="checked" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                                                                                onchange="showHideHiddenField(this.id)"	/>
	                                                                            <input type="hidden" name="isSpeak" value="1" id="hidden_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                                                                            <%}else { %>
	                                                                            <input type="checkbox" name="isSpeakcheckbox" value="0" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>"
	                                                                                onchange="showHideHiddenField(this.id)"	/>
	                                                                            <input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                                                                            <%} %>
	                                                                        </td>
	                                                                        <td><a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a>
	                                                                        <%if(i>0){ %>
	                                                                        <a href="javascript:void(0)" onclick="removeLanguages(this.id)" id=<%=i%> class="remove-font" ></a>
	                                                                        <% } %>
	                                                                        </td>
		                                                                </tr>
	                                                                <%}
	                                                                }else{%>
	                                                                	<tr id="row_language" class="row_language">
	                                                                		<td><input type="text" style="width: 180px;" name="languageName" class=" form-control "></input></td>
	                                                                        <td width="50px" align="center"><input type="checkbox" id="isRead_0" name="isReadcheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
	                                                                            <input type="hidden" name="isRead" value="0" id="hidden_isRead_0" />
	                                                                        <td width="50px" align="center"><input type="checkbox" id="isWrite_0" name="isWritecheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
	                                                                            <input type="hidden" name="isWrite" value="0" id="hidden_isWrite_0" />
	                                                                        <td width="50px" align="center"><input type="checkbox" id="isSpeak_0" name="isSpeakcheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
	                                                                            <input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_0" />
	                                                                        <td ><a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a></td>
	                                                                	</tr>
	                                                                <%} %>
                                                            	</tbody>
                                                           	</table>
                                                    </div>
                                                    <div id="div_languages" class=" col-lg-5 col-md-5 col-sm-12" style="padding-left: 5px;padding-right: 5px;border: solid 2px #F5F5F5;height: 300px;overflow-y: auto;margin-left: 10px; margin-right: 10px;">
                                                    	<h4 style="margin:5px 0px 10px 0px;background: aliceblue;padding: 5px;">Enter trainer hobbies</h4>
                                                            <table class="table table-head-highlight">
                                                            	<tbody id="table-hobbies">
                                                            		<tr id="row_hobby" class="row_hobby">
                                                            			<th>Hobby Name</th>
                                                            			<th>Read</th>
                                                            		</tr>
                                                            		<% 	
	                                                                if(alHobbies!=null && alHobbies.size()!=0){
	                                                                	String empId = (String)((ArrayList)alHobbies.get(0)).get(2);
	                                                                
	                                                                for(int i=0; i<alHobbies.size(); i++) {
	                                                                %>
	                                                                <tr id="row_hobby<%=((ArrayList)alHobbies.get(i)).get(0)%>" class="row_hobby">
	                                                                	<td><input type="text" style="width: 180px;" name="hobbyName" class=" form-control " value="<%=((ArrayList)alHobbies.get(i)).get(1)%>" ></input></td>
                                                                        <td><a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a>
                                                                        <%if(i>0){ %>
                                                                        <a href="javascript:void(0)" onclick="removeHobbies(this.id)" id=<%=((ArrayList)alHobbies.get(i)).get(0)%> class="remove-font" ></a>
                                                                        <% } %></td>
	                                                                </tr>
	                                                                <%}
	                                                                }else {
	                                                                %>
	                                                                <tr id="row_hobby" class="row_hobby">
                                                                        <td><input type="text" style="width: 180px;"name="hobbyName" class=" form-control "></input></td>
                                                                        <td><a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a></td>
                                                                    </tr>
	                                                                <%} %>
                                                            	</tbody>
                                                            </table>
                                                    </div>
                                                </div>
                                                    
                                                <div class="clr"></div>
                                                <div>
                                                    <table class="table">
                                                        <s:if test="mode==null">
                                                            <tr>
                                                                <td colspan="2" align="right">
                                                                	<%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                    <s:submit cssClass="btn btn-primary next-step" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:if>
                                                        <s:else>
                                                            <tr>
                                                                <td colspan="2" align="right">
                                                                	<%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=0&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:else>
                                                    </table>
                                                </div>
                                            </s:form>
                                        </div>
                                    </div>
                                </s:if>
                                <s:if test="step==3">
                                    <div class="tab-pane active" role="tabpanel" id="fi">
                                        <div>
                                            <!-- Family Information -->
                                            <s:form theme="simple" action="AddTrainer" id="frmFamilyInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
                                                <div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_id_family">
                                                    <s:hidden name="operation" />
                                                    <%-- <s:hidden name="recruitId" /> --%>
                                                    <s:hidden name="empId"/>
                                                    <s:hidden name="mode" />
                                                    <s:hidden name="step" />
                                                    <table border="0" class="table">
                                                        <tr>
                                                            <td class="tdLabelheadingBg alignCenter" colspan="2">
                                                            	<div><span style="color:#68AC3B; font-size:22px">Step 3 : </span><span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;">Enter Trainers Family Information</span> </div>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                    <table class="table">
                                                        <tr>
                                                            <td>
                                                                <table class="table">
                                                                    <tr>
                                                                        <td  style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Father's Information </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Name:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="fatherName"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Date of birth:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="fatherDob"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Education:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="fatherEducation"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Occupation:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle=" width: 180px;" name="fatherOccupation"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Contact Number:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="fatherContactNumber"  onkeypress="return isOnlyNumberKey(event)"  cssClass=" form-control  validateNumber"></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Email Id:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="fatherEmailId" cssClass=" form-control  validateEmail"></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                            <td>&nbsp;</td>
                                                            <td>
                                                                <table class="table">
                                                                    <tr>
                                                                        <td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Mother's Information </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Name:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="motherName"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Date of birth:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="motherDob"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Education:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="motherEducation"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Occupation:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="motherOccupation"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Contact Number:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="motherContactNumber"  onkeypress="return isOnlyNumberKey(event)"  cssClass=" form-control  validateNumber"></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Email Id:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="motherEmailId"  cssClass=" form-control  validateEmail"></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                            <td>&nbsp;</td>
                                                            <td>
                                                                <table class="table">
                                                                    <tr>
                                                                        <td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Spouse's Information </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Name:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="spouseName"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Date of birth:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="spouseDob"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Education:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="spouseEducation"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Occupation:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="spouseOccupation"  cssClass=" form-control "></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Contact Number:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="spouseContactNumber"  onkeypress="return isOnlyNumberKey(event)"  cssClass=" form-control  validateNumber"></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Email Id:</td>
                                                                        <td>
                                                                            <s:textfield cssStyle="width: 180px;" name="spouseEmailId"  cssClass=" form-control  validateEmail"></s:textfield>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Gender:</td>
                                                                        <td>
                                                                            <s:select theme="simple" label="Select Gender" name="spouseGender" listKey="genderId" cssStyle="width: 180px;"
                                                                                listValue="genderName" headerKey="0" headerValue="Select Gender"		
                                                                                list="empGenderList" key="" required="true" cssClass=" form-control " />
                                                                        </td>
                                                                </table>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                    <%	if(alSiblings!=null && alSiblings.size()!=0) {
                                                        for(int i=0; i<alSiblings.size(); i++) { %>
                                                    <div id="col_family_siblings<%=i%>" style="float:left; border:solid 0px #ccc" >
                                                        <table class="table">
                                                            <tr>
                                                                <td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Name:</td>
                                                                <td><input type="text" class=" form-control " style="width: 180px;" name="memberName" value="<%=((ArrayList)alSiblings.get(i)).get(1)%>" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Date of birth:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="memberDob" value="<%=((ArrayList)alSiblings.get(i)).get(2)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Education: </td>
                                                                <td><input type="text" class=" form-control " style="width: 180px;" name="memberEducation" value="<%=((ArrayList)alSiblings.get(i)).get(3)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Occupation:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="memberOccupation" value="<%=((ArrayList)alSiblings.get(i)).get(4)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Contact Number:</td>
                                                                <td> <input type="text" class=" form-control  validateNumber" onkeypress="return isOnlyNumberKey(event)"  style="width: 180px;" name="memberContactNumber" value="<%=((ArrayList)alSiblings.get(i)).get(5)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Email Id:</td>
                                                                <td> <input type="text" class=" form-control  validateEmail" style="width: 180px;" name="memberEmailId" value="<%=((ArrayList)alSiblings.get(i)).get(6)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Gender:</td>
                                                                <%-- <td><input type="text" style="width: 180px;" name="memberGender" value="<%=((ArrayList)alSiblings.get(i)).get(7)%>"></input></td> --%>
                                                                <td>
                                                                    <select name="memberGender" style="width: 180px;" class="form-control ">
                                                                        <option value="">Select Gender</option>
                                                                        <%for(int k=0; k< empGenderList.size(); k++) { 
                                                                            if( (((FillGender)empGenderList.get(k)).getGenderId()+"").equals( (String)((ArrayList)alSiblings.get(i)).get(7) )) {
                                                                            %>
                                                                        <option value="<%=((FillGender)empGenderList.get(k)).getGenderId() %>" selected="selected">
                                                                            <%=((FillGender)empGenderList.get(k)).getGenderName() %>
                                                                        </option>
                                                                        <%}else { %>
                                                                        <option value="<%=((FillGender)empGenderList.get(k)).getGenderId() %>">
                                                                            <%=((FillGender)empGenderList.get(k)).getGenderName() %>
                                                                        </option>
                                                                        <% }
                                                                            }%>
                                                                    </select>
                                                                </td>
                                                            </tr>
                                                            <!-- <tr><td class="txtlabel alignRight"><a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add-font">&nbsp;</a></td> -->   
                                                            <tr>
                                                                <td class="txtlabel alignRight" colspan="2">
                                                                    <a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add-font"></a>
                                                                    <% if(i>0){ %>
                                                                    <a href="javascript:void(0)" onclick="removeSibling(this.id)" id=<%=i%> class="remove-font" ></a>
                                                                    <%} %>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </div>
                                                    <%}
                                                        }else {%>
                                                    <div id="col_family_siblings" style="float: left;border:solid 0px #f00" >
                                                        <table class="table">
                                                            <tr>
                                                                <td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Name:</td>
                                                                <td><input type="text" style="width: 180px;" name="memberName"  class=" form-control "></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Date of birth:</td>
                                                                <td> <input type="text" style="width: 180px;" name="memberDob"  class=" form-control "></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Education:</td>
                                                                <td> <input type="text" style="width: 180px;" name="memberEducation"  class=" form-control "></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Occupation:</td>
                                                                <td><input type="text" style="width: 180px;" name="memberOccupation"  class=" form-control "></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Contact Number:</td>
                                                                <td> <input type="text" style="width: 180px;" name="memberContactNumber"  onkeypress="return isOnlyNumberKey(event)" class=" form-control "></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Email Id:</td>
                                                                <td> <input type="text" style="width: 180px;" name="memberEmailId" class=" form-control validateEmail" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Gender:</td>
                                                                <td>
                                                                    <s:select theme="simple" label="Select Gender" name="memberGender" listKey="genderId" cssStyle="width: 180px;"
                                                                        listValue="genderName" headerKey="0" headerValue="Select Gender"		
                                                                        list="empGenderList" key="" required="true" cssClass=" form-control " />
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight" colspan="2"><a href="javascript:void(0)" onclick="addSibling()" class="add-font" style="float:right"></a></td>
                                                            </tr>
                                                        </table>
                                                    </div>
                                                    <%}%>
                                                </div>
                                                <div class="clr"></div>
                                                <div>
                                                    <table class="table">
                                                        <s:if test="mode==null">
                                                            <tr>
                                                                <td colspan="2" align="right">
                                                                	<%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:if>
                                                        <s:else>
                                                            <tr>
                                                                <td colspan="2" align="right">
                                                                <%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=1&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:else>
                                                    </table>
                                                </div>
                                            </s:form>
                                        </div>
                                    </div>
                                </s:if>
                                <s:if test="step==4">
                                    <div class="tab-pane active" role="tabpanel" id="pe">
                                        <div>
                                            <!-- Previous Employment -->
                                            <s:form theme="simple" action="AddTrainer" id="frmPrevEmployment" method="POST" cssClass="formcss" enctype="multipart/form-data">
                                                <div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
                                                    <s:hidden name="operation" />
                                                    <%-- <s:hidden name="recruitId" /> --%>
                                                    <s:hidden name="empId" />
                                                    <s:hidden name="mode" />
                                                    <s:hidden name="step" />
                                                    <table border="0" class="table">
                                                     
                                                        <tr>
                                                            <td class="tdLabelheadingBg alignCenter" colspan="2">
                                                            <div><span style="color:#68AC3B; font-size:22px">Step 4 : </span><span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;">Enter Trainers Previous Employment </span> </div>
                                                        </tr>
                                                    </table>
                                                    <%	if(alPrevEmployment!=null && alPrevEmployment.size()!=0) {
                                                        for(int i=0; i<alPrevEmployment.size(); i++) {%>
                                                    <div id="col_prev_employer<%=i%>" style="float: left;">
                                                        <table class="table">
                                                            <tr>
                                                                <td class="txtlabel alignRight">Company Name:</td>
                                                                <td><input type="text" class=" form-control " name="prevCompanyName" style="width: 180px;" name="prevCompanyLocation" value="<%=((ArrayList)alPrevEmployment.get(i)).get(1)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Location:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyLocation" value="<%=((ArrayList)alPrevEmployment.get(i)).get(2)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">City:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyCity" value="<%=((ArrayList)alPrevEmployment.get(i)).get(3)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">State:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyState" value="<%=((ArrayList)alPrevEmployment.get(i)).get(4)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Country:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyCountry" value="<%=((ArrayList)alPrevEmployment.get(i)).get(5)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Contact Number:</td>
                                                                <td> <input type="text" class=" form-control  validateNumber" onkeypress="return isOnlyNumberKey(event)"  style="width: 180px;" name="prevCompanyContactNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(6)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Reporting To:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyReportingTo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(7)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">From: </td>
                                                                <td><input type="text" class=" form-control " style="width: 180px;" name="prevCompanyFromDate" value="<%=((ArrayList)alPrevEmployment.get(i)).get(8)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">To:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyToDate" value="<%=((ArrayList)alPrevEmployment.get(i)).get(9)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Designation:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyDesination" value="<%=((ArrayList)alPrevEmployment.get(i)).get(10)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Responsibility:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyResponsibilities" value="<%=((ArrayList)alPrevEmployment.get(i)).get(11)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight">Skills:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanySkills" value="<%=((ArrayList)alPrevEmployment.get(i)).get(12)%>"></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td colspan="2" style="margin:0px 5px 0px 0px"><span style="float: right;"> 
                                                                    <%if(i>0){ %>
                                                                    <a href="javascript:void(0)" onclick="removePrevEmployment(this.id)" id=<%=i%> class="remove-font" ></a>
                                                                    <%} %>
                                                                    <a href=javascript:void(0) onclick="addPrevEmployment()" class=add-font></a></span>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </div>
                                                    <%}
                                                        }else { %>
                                                    <div id="col_prev_employer" style="float: left;">
                                                        <table class="table">
                                                            <tr>
                                                                <td class="txtlabel alignRight"> Company Name:</td>
                                                                <td><input type="text" class=" form-control " name="prevCompanyName" style="width: 180px;" name="prevCompanyLocation" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> Location:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyLocation" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> City: </td>
                                                                <td><input type="text" class=" form-control " style="width: 180px;" name="prevCompanyCity" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> State:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyState" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> Country:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyCountry" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> Contact Number:</td>
                                                                <td> <input type="text" class=" form-control  validateNumber" onkeypress="return isOnlyNumberKey(event)" style="width: 180px;" name="prevCompanyContactNo" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> Reporting To:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyReportingTo" ></input></td>
                                                            </tr>
                                                            <tr> 
                                                                <td class="txtlabel alignRight"> From:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyFromDate" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> To:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyToDate" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> Designation:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyDesination" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> Responsibility:</td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanyResponsibilities" ></input>  </td>
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"> Skills: </td>
                                                                <td> <input type="text" class=" form-control " style="width: 180px;" name="prevCompanySkills" ></input></td>
                                                            </tr>
                                                            <tr>
                                                                <td colspan="2"><span style="float:right;"><a href="javascript:void(0)" onclick="addPrevEmployment()" class="add-font"></a></span></td>
                                                            </tr>
                                                        </table>
                                                    </div>
                                                    <%}%>
                                                </div>
                                                <div class="clr"></div>
                                                <div>
                                                    <table class="table">
                                                        <s:if test="mode==null">
                                                            <tr>
                                                                <td colspan="2" align="right">
                                                                	<%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:if>
                                                        <s:else>
                                                            <tr>
                                                                <td colspan="2" align="right">
                                                                <%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=2&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:else>
                                                    </table>
                                                </div>
                                            </s:form>
                                        </div>
                                    </div>
                                </s:if>
                                <s:if test="step==5">
                                    <div class="tab-pane active" role="tabpanel" id="rf">
                                        <div>
                                            <s:form theme="simple" action="AddTrainer" id="frmReferences" method="POST" cssClass="formcss" enctype="multipart/form-data">
                                                <s:hidden name="operation" />
                                                <%-- <s:hidden name="recruitId" /> --%>
                                                <s:hidden name="empId"/>
                                                <s:hidden name="mode"/>
                                                <s:hidden name="step"/>
                                                <table border="0" class="table">
                                                    <tr>
                                                        <td>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="tdLabelheadingBg alignCenter" colspan="2">
                                                        	<div><span style="color:#68AC3B; font-size:22px">Step 5 : </span><span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;">Enter Trainer References 1:</span> </div>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Name:</td>
                                                        <td>
                                                            <s:textfield name="ref1Name" cssClass=" form-control "/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Company:</td>
                                                        <td>
                                                            <s:textfield name="ref1Company" cssClass=" form-control "/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Designation:</td>
                                                        <td>
                                                            <s:textfield name="ref1Designation" cssClass=" form-control "/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Contact No:</td>
                                                        <td>
                                                            <s:textfield name="ref1ContactNo" cssClass=" form-control  validateNumber" onkeypress="return isOnlyNumberKey(event)"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Email Id:</td>
                                                        <td>
                                                            <s:textfield name="ref1Email" cssClass=" form-control  validateEmail"/>
                                                        </td>
                                                    </tr>
                                                </table>
                                                <table border="0" class="table">
                                                    <tr>
                                                        <td>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="tdLabelheadingBg alignCenter" colspan="2" >
																<div style="margin-left:70px;">
																	<span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;">Enter Trainer References 2:</span> 
															   </div>
														</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Name:</td>
                                                        <td>
                                                            <s:textfield name="ref2Name"  cssClass=" form-control "/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Company:</td>
                                                        <td>
                                                            <s:textfield name="ref2Company"  cssClass=" form-control "/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Designation:</td>
                                                        <td>
                                                            <s:textfield name="ref2Designation" cssClass=" form-control " />
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Contact No:</td>
                                                        <td>
                                                            <s:textfield name="ref2ContactNo"  cssClass=" form-control" onkeypress="return isOnlyNumberKey(event)"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="txtlabel alignRight">Email Id:</td>
                                                        <td>
                                                            <s:textfield name="ref2Email" cssClass=" form-control validateEmail" />
                                                        </td>
                                                    </tr>
                                                </table>
                                                <div class="clr"></div>
                                                <div>
                                                    <table class="table">
                                                        <s:if test="mode==null">
                                                            <tr>
                                                                <td colspan="2" align="right">
                                                                	<%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=3&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:if>
                                                        <s:else>
                                                            <tr>
                                                                <td colspan="2" align="right">
                                                                <%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=3&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
                                                                </td>
                                                            </tr>
                                                        </s:else>
                                                    </table>
                                                </div>
                                            </s:form>
                                        </div>
                                    </div>
                                </s:if>
                                <s:if test="step==6">
                                    <div class="tab-pane active" role="tabpanel" id="mi">
                                        <div>
                                            <!-- Medical Information -->
                                            <div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
                                                <s:form theme="simple" action="AddTrainer" id="frmMedicalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
                                                    <s:hidden name="operation" />
                                                    <%-- <s:hidden name="recruitId" /> --%>
                                                    <s:hidden name="empId" />
                                                    <s:hidden name="mode" />
                                                    <s:hidden name="step" />
                                                    <table border="0" class="table">
                                                        <tr>
                                                            <td  class="tdLabelheadingBg alignCenter" colspan="2">
                                                            <div><span style="color:#68AC3B; font-size:22px">Step 6 : </span><span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;">Medical Information :-</span> </div>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                    <table border="0" class="table">
                                                        <tr>
                                                            <td class="tdLabelheadingBg alignCenter" colspan="2">Enter Trainer's Medical Information </td>
                                                            
                                                        </tr>
                                                        <tr>
                                                            <td>
                                                                <table style="font-size: 12px" class="table">
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">
                                                                        	Are you now receiving medical attention:
                                                                        	</td>
                                                                        <td>
                                                                            <s:radio list="#{'true':'Yes','false':'No'}" name="checkQue1" onclick="checkRadio(this,'text1');"></s:radio>
                                                                            <s:hidden name="empMedicalId1" />
                                                                            <s:hidden name="que1Id" value="1"></s:hidden>
                                                                            <s:hidden name="que1IdFileStatus" id="que1IdFileStatus" value="0"></s:hidden>
                                                                        </td>
                                                                        <s:if test="checkQue1==true">
                                                                            <td>
                                                                                <s:textarea  rows="7" cols="63" id="text1" name="que1Desc"   cssClass=" form-control "></s:textarea>
                                                                            </td>
                                                                            <td>
                                                                                <s:file name="que1DescFile" id="text1File" onchange="fillFileStatus('que1IdFileStatus')" />
                                                                            </td>
                                                                        </s:if>
                                                                        <s:else>
                                                                            <td>
                                                                                <s:textarea  rows="7" cols="63" id="text1" name="que1Desc" disabled="true"  cssClass=" form-control "></s:textarea>
                                                                            </td>
                                                                            <td>
                                                                                <s:file name="que1DescFile" id="text1File" disabled="true" onchange="fillFileStatus('que1IdFileStatus')"/>
                                                                            </td>
                                                                        </s:else>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Have you had any form of serious illness or operation:</td>
                                                                        <td>
                                                                            <s:radio list="#{'true':'Yes','false':'No'}" name="checkQue2" onclick="checkRadio(this,'text2');"></s:radio>
                                                                            <s:hidden name="empMedicalId2" />
                                                                            <s:hidden name="que2Id" value="2"></s:hidden>
                                                                            <s:hidden name="que1IdFileStatus" id="que2IdFileStatus" value="0"></s:hidden>
                                                                        </td>
                                                                        <s:if test="checkQue2==true">
                                                                            <td>
                                                                                <s:textarea  rows="7" cols="63" id="text2" name="que2Desc"   cssClass=" form-control "></s:textarea>
                                                                            </td>
                                                                            <td>
                                                                                <s:file name="que1DescFile" id="text2File" onchange="fillFileStatus('que2IdFileStatus')"/>
                                                                            </td>
                                                                        </s:if>
                                                                        <s:else>
                                                                            <td>
                                                                                <s:textarea  rows="7" cols="63" id="text2" name="que2Desc" disabled="true"  cssClass=" form-control "></s:textarea>
                                                                            </td>
                                                                            <td>
                                                                                <s:file name="que1DescFile" id="text2File" disabled="true" onchange="fillFileStatus('que2IdFileStatus')"/>
                                                                            </td>
                                                                        </s:else>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="txtlabel alignRight">Have you had any illness in the last two years? YES/NO If YES, 
                                                                            please give the details about the same and any absences from work: 
                                                                        </td>
                                                                        <td>
                                                                            <s:radio list="#{'true':'Yes','false':'No'}" name="checkQue3" onclick="checkRadio(this,'text3');"></s:radio>
                                                                            <s:hidden name="empMedicalId3" />
                                                                            <s:hidden name="que3Id" value="3"></s:hidden>
                                                                            <s:hidden name="que1IdFileStatus" id="que3IdFileStatus" value="0"></s:hidden>
                                                                        </td>
                                                                        <s:if test="checkQue3==true">
                                                                            <td>
                                                                                <s:textarea  rows="7" cols="63" id="text3" name="que3Desc"   cssClass=" form-control "></s:textarea>
                                                                            </td>
                                                                            <td>
                                                                                <s:file name="que1DescFile" id="text3File" onchange="fillFileStatus('que3IdFileStatus')"/>
                                                                            </td>
                                                                        </s:if>
                                                                        <s:else>
                                                                            <td>
                                                                                <s:textarea  rows="7" cols="63" id="text3" name="que3Desc" disabled="true"  cssClass=" form-control "></s:textarea>
                                                                            </td>
                                                                            <td>
                                                                                <s:file name="que1DescFile" id="text3File" disabled="true" onchange="fillFileStatus('que3IdFileStatus')" />
                                                                            </td>
                                                                        </s:else>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                    <div class="clr"></div>
                                                    <div>
                                                        <table class="table">
                                                            <s:if test="mode==null">
                                                                <tr>
                                                                    <td colspan="2" align="right">
                                                                    	<%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view">Previous</a></button> --%>
                                                                        <s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
                                                                    </td>
                                                                </tr>
                                                            </s:if>
                                                            <s:else>
                                                                <tr>
                                                                    <td colspan="2" align="right">
                                                                    <%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=4&empId=<%=request.getAttribute("empId")%>&mode=profile&type=view">Previous</a></button> --%>
                                                                        <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
                                                                    </td>
                                                                </tr>
                                                            </s:else>
                                                        </table>
                                                    </div>
                                                </s:form>
                                            </div>
                                        </div>
                                    </div>
                                </s:if>
                                <s:if test="step==7">
                                    <div class="tab-pane active" role="tabpanel" id="dc">
                                        <div>
                                            <div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_id_docs">
                                                <s:form theme="simple" action="AddTrainer" id="frmDocumentation" name="frmDocumentation" method="POST" cssClass="formcss" enctype="multipart/form-data">
                                                   
                                                    <s:hidden name="operation" />
                                                    <%-- <s:hidden name="recruitId" /> --%>
                                                    <s:hidden name="empId" />
                                                    <s:hidden name="mode" />
                                                    <s:hidden name="step" />
                                                    <table border="0" class="table">
                                                        <tr>
                                                            <td  class="tdLabelheadingBg alignCenter" colspan="2">
                                                            	<div><span style="color:#68AC3B; font-size:22px">Step 7 : </span><span class="tdLabelheadingBg" style="font-size: 18px;font-weight: 600;">Attach Documents </span> </div>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                    <% 	
                                                        if(alDocuments!=null && alDocuments.size()!=0) {
                                                        	//String empId = (String)((ArrayList)alDocuments.get(0)).get(3);
                                                        %>
		                                                    <table style="width:70%" id="row_document_table" class="table table-bordered autoWidth">
		                                                        <tr>
		                                                            <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Document Name</b></label></td>
		                                                            <td class="txtlabel alignRight" style="text-align: -moz-center" ><label><b>Attached Document</b></label></td>
		                                                            <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Added By</b></label></td>
		                                                            <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Entry Date</b></label></td>
		                                                        </tr>
		                                                        <% 
		                                                            for(int i=0; i<alDocuments.size(); i++) {
		                                                            %>
		                                                        <tr>
		                                                            <td class="txtlabel alignRight" style="text-align: -moz-center"><input type="hidden" name="idDocType" value="<%=((ArrayList)alDocuments.get(i)).get(2)%>"></input>
		                                                                <input type="hidden" name="docId" value="<%=((ArrayList)alDocuments.get(i)).get(0)%>"></input>
		                                                                <%=((ArrayList)alDocuments.get(i)).get(1)%>
		                                                            </td>
		                                                            <td class="txtlabel alignRight" style="text-align: -moz-center">
		                                                                <a href="<%=request.getContextPath()+"/userDocuments/"+((ArrayList)alDocuments.get(i)).get(4)%>" ><i class="fa fa-file-o" aria-hidden="true" title="click to download"></i></a>
		                                                            </td>
		                                                            <td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(6)%></td>
		                                                            <td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(5)%></td>
		                                                            <td class="txtlabel alignRight" style="text-align: -moz-center;width:125px;">
		                                                                <%if (struserType != null && (struserType.equalsIgnoreCase(IConstants.HRMANAGER))){ %>
		                                                                <div id="removeDivDocument_<%=((ArrayList)alDocuments.get(i)).get(0)%>"><a href="javascript:void(0)" onclick="deleteDocuments('<%=((ArrayList)alDocuments.get(i)).get(0)%>')" class="remove-font"></a></div>
		                                                                <%} %>
		                                                                <%if(i==alDocuments.size()-1){ %>
		                                                                <a href="javascript:void(0)" onclick="addDocuments()" class="add-font"></a>
		                                                                <%} %>
		                                                            </td>
		                                                        </tr>
		                                                        <%}%>
		                                                    </table>
                                                    <%}else {
                                                        %>
                                                    <div id="row_document">
                                                        <table class="table table-bordered autoWidth">
                                                            <tr>
                                                                <td class="txtlabel alignRight"><label><b>Document Name</b></label></td>
                                                                <td class=""><label><b>Attached Document</b></label></td>
                                                                <!--<td><a href="javascript:void(0)" onclick="addDocuments()" class="add-font"><b>Add New Document..</b></a></td>-->
                                                            </tr>
                                                            <tr>
                                                                <td class="txtlabel alignRight"><%=IConstants.DOCUMENT_RESUME%>:<sup>*</sup>
                                                                    <input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_RESUME%>"></input>
                                                                    <input type="hidden" style="width: 180px;" value="<%=IConstants.DOCUMENT_RESUME%>" name="idDocName" ></input>
                                                                    <input type="hidden" name="idDocStatus" id="idDoc1Status" value="0"></input>
                                                                </td>
                                                                <td class="txtlabel alignRight"><input type="file" name="idDoc" onchange="fillFileStatus('idDoc1Status')" required/></td>
                                                            <tr>
                                                                <td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ID_PROOF%>:<sup>*</sup>
                                                                    <input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_ID_PROOF%>"></input>
                                                                    <input type="hidden" value="<%=IConstants.DOCUMENT_ID_PROOF%>" style="width: 180px;" name="idDocName" ></input>
                                                                    <input type="hidden" name="idDocStatus" id="idDoc2Status" value="0"></input>
                                                                </td>
                                                                <td class="txtlabel alignRight"><input type="file" name="idDoc" onchange="fillFileStatus('idDoc2Status')"required/></td>
                                                            <tr>
                                                                <td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ADDRESS_PROOF%>:<sup>*</sup>
                                                                    <input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>"></input>
                                                                    <input type="hidden" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>" style="width: 180px;" name="idDocName" ></input>
                                                                    <input type="hidden" name="idDocStatus" id="idDoc3Status" value="0"></input>
                                                                </td>
                                                                <td class="txtlabel alignRight"><input type="file" name="idDoc" onchange="fillFileStatus('idDoc3Status')"required/></td>
                                                            </tr>
                                                        </table>
                                                    </div>
                                                    <%} %>
                                                    <div class="clr"></div>
                                                    <div>
                                                        <table class="table">
                                                            <s:if test="mode==null">
                                                                <tr>
                                                                    <td colspan="2" align="right">
                                                                    	<%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=5&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                        <s:submit cssClass="btn btn-primary" name="stepSubmit" id="stepSubmit" cssStyle="width:200px; float:right;" value="Submit" align="center" />
                                                                    </td>
                                                                </tr>
                                                            </s:if>
                                                            <s:else>
                                                                <tr>
                                                                    <td colspan="2" align="right">
                                                                    	<%-- <button type="button" class="btn btn-default prev-step"><a href="AddTrainer.action?operation=E&step=5&empId=<%=request.getAttribute("empId")%>">Previous</a></button> --%>
                                                                        <s:submit cssClass="btn btn-primary" name="stepSubmit" id="stepSubmit" value="Submit & Proceed" align="center" />
                                                                    </td>
                                                                </tr>
                                                            </s:else>
                                                        </table>
                                                    </div>
                                                </s:form>
                                            </div>
                                        </div>
                                    </div>
                                </s:if>
                            </div>
                        </div>
                        <script>
                            showMarriageDate();
                            //validateMandatory(document.frmOfficial.empType.options[document.frmOfficial.empType.options.selectedIndex].value);
                        </script>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>
<script>

  $("form").bind('submit',function(event) {
	 $("input[type='submit']").val('Submitting..');
	  event.preventDefault();
   	  var form_data = $("#"+this.id).serialize();
   	  var op = '<%=operation%>';
   	 $("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
   	  $("input[type='submit']").val('Submitting..');
   	  $.ajax({
     		url: "AddTrainer.action",
     		type: 'POST',
     		data: form_data,
     		success: function(result){
     			$("#divResult").html(result);
     			
     	    }
      });
     
	});
  
  function loadStepOnClick(action){
	  var op = '<%=operation%>';
	  $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  	  $.ajax({
    		url: action,
    		type: 'GET',
    		success: function(result){
    			$("#divResult").html(result);
    			
    	    }
      });
  }
  
$(function(){
	$(".prev-step").click(function (e) {
        var $active = $('.wizard .nav-tabs li.active');
        prevTab($active);
    });
    
    $('.nav-tabs > li a[title]').tooltip();
    $('a[data-toggle="tab"]').on('show.bs.tab', function (e) {
        var $target = $(e.target);
        if ($target.parent().hasClass('disabled')) {
            return false;
        }
    });
    $(".next-step").click(function (e) {
		var form_id = this.form.id;
		if(document.getElementById("#"+form_id).submit()){
			var $active = $('.wizard .nav-tabs li.active');
	        nextTab($active);
		}
	});
    $(".prev-step").click(function (e) {
        var $active = $('.wizard .nav-tabs li.active');
        prevTab($active);
    });
});
function nextTab(elem) {
    $(elem).next().find('a[data-toggle="tab"]').click();
}
function prevTab(elem) {
    $(elem).prev().find('a[data-toggle="tab"]').click();
}
</script>