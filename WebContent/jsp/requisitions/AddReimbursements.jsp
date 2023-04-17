<%@page import="java.util.LinkedHashMap"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF =  new UtilityFunctions();
	
	Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
	if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
	String policy_id = (String) request.getAttribute("policy_id");
	String strUserTYpe = (String)session.getAttribute(IConstants.USERTYPE);
	String strE = (String)request.getParameter("E");
	List<String> alReceiptNo = (List<String>)request.getAttribute("alReceiptNo");
	if(alReceiptNo == null) alReceiptNo = new ArrayList<String>();
%>
 
<script type="text/javascript">
	 
	$(document).ready( function () {
		
		/* $("#strFromDate").datepicker({
	        format: 'dd/mm/yyyy',
	        autoclose: true
	    }).on('changeDate', function (selected) {
	        var minDate = new Date(selected.date.valueOf()); 
	        $('#strFromDate').datepicker('strFromDate', minDate);
	    });
	    
	    $("#strToDate").datepicker({
	    	format: 'dd/mm/yyyy',
	    	autoclose: true
	    }).on('changeDate', function (selected) {
	            var minDate = new Date(selected.date.valueOf());
	            $('#strToDate').datepicker('strToDate', minDate);
	    });  */
	    
	    $("#strFromDate").datepicker({format: 'dd/mm/yyyy'});
	    
		
		var value='<%=(String)request.getAttribute("reimbursementType") %>';
		showType(value);
	});
	
	var cnt=0;
	function addDocument() {
		//alert("dfgsgfdf");
		cnt++;
		var divTag = document.createElement("div");
	    divTag.id = "row_document"+cnt;
	    divTag.innerHTML = 	"<div class=\"row row_without_margin\">"+
			"<div class=\"col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding\" style=\"margin-top: 5px;\">"+
			"<span id=\"file"+cnt+"\"></span><input type=\"file\" name=\"strDocument\"  onchange=\"readFileURL(this, 'file"+cnt+"');\">"+
			"</div><div class=\"col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding\">"+
			"<a href=\"javascript:void(0)\" onclick=\"addDocument()\" class=\"add-font\"></a>"+
			"<a href=\"javascript:void(0)\" onclick=\"removeDocument("+cnt+")\" id=\"removeDocId"+cnt+"\" class=\"remove-font\"></a>"+
			"</div></div>";
	    document.getElementById("div_documents").appendChild(divTag);
	     
	}

	function removeDocument(removeId) {
		var remove_elem = "row_document"+removeId;
		var row_document = document.getElementById(remove_elem); 
		document.getElementById("div_documents").removeChild(row_document);
		
	}

	function addReceiptNo() {
		var cnt1 = document.getElementById("cnt1").value;
		cnt1++;
		var divTag = document.createElement("div");
	    divTag.id = "row_receipt"+cnt1;
	    divTag.innerHTML = 	"<div class=\"row row_without_margin\">"+
			"<div class=\"col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding\" style=\"margin-top: 5px;\">"+
			"<input type=\"text\" name=\"strReceiptNo\">"+
			"</div><div class=\"col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding\">"+
			"<a href=\"javascript:void(0)\" onclick=\"addReceiptNo()\" class=\"add-font\"></a>"+
			"<a href=\"javascript:void(0)\" onclick=\"removeReceiptNo("+cnt1+")\" id=\"removeReceiptId"+cnt1+"\" class=\"remove-font\"></a>"+
			"</div></div>";
	    document.getElementById("divReceiptNo").appendChild(divTag);
	    document.getElementById("cnt1").value=cnt1; 
	}

	function removeReceiptNo(removeId) {
		var remove_elem = "row_receipt"+removeId;
		var row_receipt = document.getElementById(remove_elem); 
		document.getElementById("divReceiptNo").removeChild(row_receipt);
		
	}
	
	function callId(id){
    	document.getElementById("divid").setAttribute("rel","popup_name"+id);
    	document.getElementById("divid").setAttribute("class","poplight") ;
    } 

	function setamount(){
	    var reimbursementType = $("input[name='reimbursementType']:checked").val();
	    if(reimbursementType == 'L'){
	    	var selecttype = document.getElementById("selecttype").value;
	    	var modeoftravel = document.getElementById("modeoftravel").value;
	    	if(selecttype == 'Conveyance Bill'){
	    		if(document.getElementById("local_1_"+modeoftravel)){
		    		var limit = document.getElementById("local_1_"+modeoftravel).value;
					var kmpd=document.getElementById("kmpd").value;
				    var ratepkm=document.getElementById("ratepkm").value ;
				    var noofdays=document.getElementById("noofdays").value ;
					if(parseFloat(ratepkm) <= parseFloat(limit)){
						/* var totalAmt=kmpd*ratepkm*noofdays;
		    			document.getElementById("strAmount").value=totalAmt; */
						if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
							var totalAmt=kmpd * ratepkm * noofdays;
							document.getElementById("strAmount").value=totalAmt;
					    }
		    		} else {
		    			alert('Enter proper rate/km upto '+limit);
		    			document.getElementById("kmpd").value = '';
		    		    document.getElementById("ratepkm").value = '';
		    		    document.getElementById("noofdays").value = '';
		    			document.getElementById("strAmount").value='';
		    		}	
	    		} else {
	        		var kmpd=document.getElementById("kmpd").value;
	    		    var ratepkm=document.getElementById("ratepkm").value ;
	    		    var noofdays=document.getElementById("noofdays").value ;
	    			/* var totalAmt=kmpd * ratepkm * noofdays;
	    			document.getElementById("strAmount").value=totalAmt; */
	    		    if(selecttype == 'Conveyance Bill' && (isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
	    				var totalAmt=kmpd * ratepkm * noofdays;
	    				document.getElementById("strAmount").value=totalAmt;
	    		    }
	        	}
	    	} else if(selecttype =='Food Expenses'){ 
	    		if(document.getElementById("local_2")){
		    		var limit = document.getElementById("local_2").value;
		    		var strAmount=document.getElementById("strAmount").value;
		    		
		    		if(parseFloat(strAmount) <= parseFloat(limit)){
		    			document.getElementById("strAmount").value=strAmount;
		    		} else {
		    			alert('Enter proper amount upto '+limit);
		    			document.getElementById("kmpd").value = '';
		    		    document.getElementById("ratepkm").value = '';
		    		    document.getElementById("noofdays").value = '';
		    			document.getElementById("strAmount").value='';
		    		}
	    		} /* else {
	        		var kmpd=document.getElementById("kmpd").value;
	    		    var ratepkm=document.getElementById("ratepkm").value ;
	    		    var noofdays=document.getElementById("noofdays").value ;
	    			var totalAmt=kmpd * ratepkm * noofdays;
	    			document.getElementById("strAmount").value=totalAmt;
	        	} */ 
	    	} else if(selecttype == 'Travel'){
	    		if(document.getElementById("local_3_"+modeoftravel)){
		    		var limit = document.getElementById("local_3_"+modeoftravel).value;
					var kmpd=document.getElementById("kmpd").value;
				    var ratepkm=document.getElementById("ratepkm").value ;
				    var noofdays=document.getElementById("noofdays").value ;
					
					if(parseFloat(ratepkm) <= parseFloat(limit)){
						/* var totalAmt=kmpd*ratepkm*noofdays;
		    			document.getElementById("strAmount").value=totalAmt; */
						if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
							var totalAmt=kmpd * ratepkm * noofdays;
							document.getElementById("strAmount").value=totalAmt;
					    }
		    		} else {
		    			alert('Enter proper rate/km upto '+limit);
		    			document.getElementById("kmpd").value = '';
		    		    document.getElementById("ratepkm").value = '';
		    		    document.getElementById("noofdays").value = '';
		    			document.getElementById("strAmount").value='';
		    		}	
	    		} else {
	        		var kmpd=document.getElementById("kmpd").value;
	    		    var ratepkm=document.getElementById("ratepkm").value ;
	    		    var noofdays=document.getElementById("noofdays").value ;
	    			/* var totalAmt=kmpd * ratepkm * noofdays;
	    			document.getElementById("strAmount").value=totalAmt; */
	    		    if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
	    				var totalAmt=kmpd * ratepkm * noofdays;
	    				document.getElementById("strAmount").value=totalAmt;
	    		    }
	        	}
	    	} else {
	    		var kmpd=document.getElementById("kmpd").value;
			    var ratepkm=document.getElementById("ratepkm").value ;
			    var noofdays=document.getElementById("noofdays").value ;
				//var totalAmt=kmpd * ratepkm * noofdays;
				//document.getElementById("strAmount").value=totalAmt;
			    if(selecttype == 'Conveyance Bill' && (isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
					var totalAmt=kmpd * ratepkm * noofdays;
					document.getElementById("strAmount").value=totalAmt;
			    }
	    	}
	    } else if(reimbursementType == 'M'){
	    	if(document.getElementById("mobile")){
	    		var limit = document.getElementById("mobile").value;
				var totalAmt=document.getElementById("strAmount").value;
				if(parseFloat(totalAmt) <= parseFloat(limit)){
	    			document.getElementById("strAmount").value=totalAmt;
	    		} else {
	    			alert('Enter proper amount upto '+limit);
	    			document.getElementById("strAmount").value='';
	    		}	
			}
	    } else {
	    	var kmpd=document.getElementById("kmpd").value;
		    var ratepkm=document.getElementById("ratepkm").value;
		    var noofdays=document.getElementById("noofdays").value;
		    var selecttype = document.getElementById("selecttype").value;
		    	    
		    if(selecttype == 'Conveyance Bill' && (isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
				var totalAmt=kmpd * ratepkm * noofdays;
				document.getElementById("strAmount").value=totalAmt;
		    }
	    }
	}

	function clearAmt(){
		document.getElementById("kmpd").value = '';
	    document.getElementById("ratepkm").value = '';
	    document.getElementById("noofdays").value = '';
		document.getElementById("strAmount").value='';
	}

	function isNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	      return false;
	   }
	   return true;
	}

	function othervalue(value) {
		if(value == "Travel" || value == "Conveyance Bill"){	
			document.getElementById("1").style.display="table-row";
			document.getElementById("3").style.display="table-row";
			document.getElementById("4").style.display="table-row";
			document.getElementById("5").style.display="table-row";
			document.getElementById("6").style.display="table-row"; 
		}else{ 
			document.getElementById("1").style.display="none";
			document.getElementById("3").style.display="none";
			document.getElementById("4").style.display="none";
			document.getElementById("5").style.display="none";
			document.getElementById("6").style.display="none";
		}
		//alert('othervalue editType==>'+document.getElementById("editType").value);
		if(parseInt(document.getElementById("strId").value) > 0 && parseInt(document.getElementById("editType").value) == 1 && (isInt(document.getElementById("strAmount").value) || isFloat(document.getElementById("strAmount").value))){
			
    	} else {
    		checkTransportType();
    	}
	}

    function showType(value){
    	//alert('value==>'+value);
    	if(value=='P'){
    		document.getElementById("typeC").style.display = "table-cell";
    		document.getElementById("client").style.display="table-cell";
    		document.getElementById("typeP").style.display = "table-cell";
    		document.getElementById("typeT").style.display = "none";
    		document.getElementById("typeO").style.display = "table-cell";
    		document.getElementById("pro").style.display="table-cell";
    		document.getElementById("othr").style.display="table-cell";
    		document.getElementById("trav").style.display="none";
    		document.getElementById("isbillable").style.display="table-row";
    		
    		if(document.getElementById("transportType")){
    			document.getElementById("transportType").selectedIndex=0;
    		}
    		document.getElementById("trTransType").style.display = "none";
    		document.getElementById("trTrain").style.display="none";
    		document.getElementById("trBus").style.display = "none";
    		document.getElementById("trFlight").style.display = "none";
    		document.getElementById("trCar").style.display = "none";
    		document.getElementById("trReimbDate").style.display = "table-row";
    		if(document.getElementById("strTransAmount")){
    			document.getElementById("strTransAmount").value='';
    		}
    		document.getElementById("trTransAmount").style.display = "none";
    		
    		document.getElementById("trLodgingType").style.display = "none";
    		if(document.getElementById("strLodgingAmount")){
    			document.getElementById("strLodgingAmount").value='';
    		}
    		document.getElementById("trLodgingAmount").style.display = "none";
    		
    		document.getElementById("trLocalConveyanceType").style.display = "none";
    		document.getElementById("trLocalConveyanceRate").style.display = "none";
    		
    		if(document.getElementById("localConveyanceKM")){
    			document.getElementById("localConveyanceKM").value='';
    		}
    		if(document.getElementById("localConveyanceRate")){
    			document.getElementById("localConveyanceRate").value='';
    		}
    		if(document.getElementById("strLocalConveyanceAmount")){
    			document.getElementById("strLocalConveyanceAmount").value='';
    		}
    		document.getElementById("trLocalConveyanceAmount").style.display = "none";
    		
    		if(document.getElementById("strFoodBeverageAmount")){
    			document.getElementById("strFoodBeverageAmount").value='';
    		}
    		document.getElementById("trFoodBeverageAmount").style.display = "none";
    		
    		if(document.getElementById("strLaundryAmount")){
    			document.getElementById("strLaundryAmount").value='';
    		}
    		document.getElementById("trLaundryAmount").style.display = "none";
    		
    		if(document.getElementById("strSundryAmount")){
    			document.getElementById("strSundryAmount").value='';
    		}
    		document.getElementById("trSundryAmount").style.display = "none";
    		
    		othervalue(document.frm_MyReimbursements.strType.options[document.frm_MyReimbursements.strType.selectedIndex].value);
    	//	setamount();
    	}else if(value=='T'){
    		document.getElementById("typeC").style.display = "none";
    		document.getElementById("client").style.display="none";
    		document.getElementById("typeP").style.display = "none";
    		document.getElementById("typeT").style.display = "table-cell";
    		document.getElementById("typeO").style.display = "none";
    		document.getElementById("pro").style.display="none";
    		document.getElementById("othr").style.display="none";
    		document.getElementById("trav").style.display="table-cell";
    		document.getElementById("isbillable").style.display="none";
    		
    		document.getElementById("trTransType").style.display = "table-row";
    		document.getElementById("trTrain").style.display="none";
    		document.getElementById("trBus").style.display = "none";
    		document.getElementById("trFlight").style.display = "none";
    		document.getElementById("trCar").style.display = "none";
    		document.getElementById("trTransAmount").style.display = "table-row";
    		document.getElementById("trReimbDate").style.display = "table-row";
    		document.getElementById("trLodgingType").style.display = "table-row";
    		document.getElementById("trLodgingAmount").style.display = "table-row";
    		
    		document.getElementById("trLocalConveyanceType").style.display = "table-row";
    		document.getElementById("trLocalConveyanceRate").style.display = "table-row";
    		document.getElementById("trLocalConveyanceAmount").style.display = "table-row";
    		
    		document.getElementById("trFoodBeverageAmount").style.display = "table-row";
    		
    		document.getElementById("trLaundryAmount").style.display = "table-row";
    		
    		document.getElementById("trSundryAmount").style.display = "table-row";
    		
    		othervalue('');
    	}else if(value=='M'){
    		document.getElementById("typeC").style.display = "none";
    		document.getElementById("client").style.display="none";
    		document.getElementById("typeP").style.display = "none";
    		document.getElementById("typeT").style.display = "none";
    		document.getElementById("typeO").style.display = "none";
    		document.getElementById("pro").style.display="none";
    		document.getElementById("othr").style.display="none";
    		document.getElementById("trav").style.display="none";
    		document.getElementById("isbillable").style.display="none";
    		document.getElementById("trReimbDate").style.display = "none";
    		if(document.getElementById("transportType")){
    			document.getElementById("transportType").selectedIndex=0;
    		}
    		document.getElementById("trTransType").style.display = "none";
    		document.getElementById("trTrain").style.display="none";
    		document.getElementById("trBus").style.display = "none";
    		document.getElementById("trFlight").style.display = "none";
    		document.getElementById("trCar").style.display = "none";
    		if(document.getElementById("strTransAmount")){
    			document.getElementById("strTransAmount").value='';
    		}
    		document.getElementById("trTransAmount").style.display = "none";
    		
    		document.getElementById("trLodgingType").style.display = "none";
    		if(document.getElementById("strLodgingAmount")){
    			document.getElementById("strLodgingAmount").value='';
    		}
    		document.getElementById("trLodgingAmount").style.display = "none";
    		
    		document.getElementById("trLocalConveyanceType").style.display = "none";
    		document.getElementById("trLocalConveyanceRate").style.display = "none";
    		if(document.getElementById("localConveyanceKM")){
    			document.getElementById("localConveyanceKM").value='';
    		}
    		if(document.getElementById("localConveyanceRate")){
    			document.getElementById("localConveyanceRate").value='';
    		}
    		if(document.getElementById("strLocalConveyanceAmount")){
    			document.getElementById("strLocalConveyanceAmount").value='';
    		}
    		document.getElementById("trLocalConveyanceAmount").style.display = "none";
    		
    		if(document.getElementById("strFoodBeverageAmount")){
    			document.getElementById("strFoodBeverageAmount").value='';
    		}
    		document.getElementById("trFoodBeverageAmount").style.display = "none";
    		
    		if(document.getElementById("strLaundryAmount")){
    			document.getElementById("strLaundryAmount").value='';
    		}
    		document.getElementById("trLaundryAmount").style.display = "none";
    		
    		if(document.getElementById("strSundryAmount")){
    			document.getElementById("strSundryAmount").value='';
    		}
    		document.getElementById("trSundryAmount").style.display = "none";
    		
    		othervalue(''); 
    	}else if(value=='L'){
    		document.getElementById("typeC").style.display = "none";
    		document.getElementById("client").style.display="none";
    		document.getElementById("typeP").style.display = "none";
    		document.getElementById("typeT").style.display = "none";
    		document.getElementById("typeO").style.display = "table-cell";
    		document.getElementById("pro").style.display="none";
    		document.getElementById("othr").style.display="table-cell";
    		document.getElementById("trav").style.display="none";
    		document.getElementById("isbillable").style.display="none";
    		
    		if(document.getElementById("transportType")){
    			document.getElementById("transportType").selectedIndex=0;
    		}
    		document.getElementById("trReimbDate").style.display = "table-row";
    		document.getElementById("trTransType").style.display = "none";
    		document.getElementById("trTrain").style.display="none";
    		document.getElementById("trBus").style.display = "none";
    		document.getElementById("trFlight").style.display = "none";
    		document.getElementById("trCar").style.display = "none";
    		if(document.getElementById("strTransAmount")){
    			document.getElementById("strTransAmount").value='';
    		}
    		document.getElementById("trTransAmount").style.display = "none";
    		
    		document.getElementById("trLodgingType").style.display = "none";
    		if(document.getElementById("strLodgingAmount")){
    			document.getElementById("strLodgingAmount").value='';
    		}
    		document.getElementById("trLodgingAmount").style.display = "none";
    		
    		document.getElementById("trLocalConveyanceType").style.display = "none";
    		document.getElementById("trLocalConveyanceRate").style.display = "none";
    		if(document.getElementById("localConveyanceKM")){
    			document.getElementById("localConveyanceKM").value='';
    		}
    		if(document.getElementById("localConveyanceRate")){
    			document.getElementById("localConveyanceRate").value='';
    		}
    		if(document.getElementById("strLocalConveyanceAmount")){
    			document.getElementById("strLocalConveyanceAmount").value='';
    		}
    		document.getElementById("trLocalConveyanceAmount").style.display = "none";
    		
    		if(document.getElementById("strFoodBeverageAmount")){
    			document.getElementById("strFoodBeverageAmount").value='';
    		}
    		document.getElementById("trFoodBeverageAmount").style.display = "none";
    		
    		if(document.getElementById("strLaundryAmount")){
    			document.getElementById("strLaundryAmount").value='';
    		}
    		document.getElementById("trLaundryAmount").style.display = "none";
    		
    		if(document.getElementById("strSundryAmount")){
    			document.getElementById("strSundryAmount").value='';
    		}
    		document.getElementById("trSundryAmount").style.display = "none";
    		
    		othervalue(document.frm_MyReimbursements.strType.options[document.frm_MyReimbursements.strType.selectedIndex].value);
    		//setamount();
    	}else{
    		document.getElementById("typeC").style.display = "none";
    		document.getElementById("client").style.display="none";
    		document.getElementById("typeP").style.display = "none";
    		document.getElementById("typeT").style.display = "none";
    		document.getElementById("typeO").style.display = "table-cell";
    		document.getElementById("pro").style.display="none";
    		document.getElementById("othr").style.display="table-cell";
    		document.getElementById("trav").style.display="none";
    		document.getElementById("isbillable").style.display="none";
    		document.getElementById("trReimbDate").style.display = "table-row";
    		if(document.getElementById("transportType")){
    			document.getElementById("transportType").selectedIndex=0;
    		}
    		document.getElementById("trTransType").style.display = "none";
    		document.getElementById("trTrain").style.display="none";
    		document.getElementById("trBus").style.display = "none";
    		document.getElementById("trFlight").style.display = "none";
    		document.getElementById("trCar").style.display = "none";
    		if(document.getElementById("strTransAmount")){
    			document.getElementById("strTransAmount").value='';
    		}
    		document.getElementById("trTransAmount").style.display = "none";
    		
    		document.getElementById("trLodgingType").style.display = "none";
    		if(document.getElementById("strLodgingAmount")){
    			document.getElementById("strLodgingAmount").value='';
    		}
    		document.getElementById("trLodgingAmount").style.display = "none";
    		
    		document.getElementById("trLocalConveyanceType").style.display = "none";
    		document.getElementById("trLocalConveyanceRate").style.display = "none";
    		if(document.getElementById("localConveyanceKM")){
    			document.getElementById("localConveyanceKM").value='';
    		}
    		if(document.getElementById("localConveyanceRate")){
    			document.getElementById("localConveyanceRate").value='';
    		}
    		if(document.getElementById("strLocalConveyanceAmount")){
    			document.getElementById("strLocalConveyanceAmount").value='';
    		}
    		document.getElementById("trLocalConveyanceAmount").style.display = "none";
    		
    		if(document.getElementById("strFoodBeverageAmount")){
    			document.getElementById("strFoodBeverageAmount").value='';
    		}
    		document.getElementById("trFoodBeverageAmount").style.display = "none";
    		
    		if(document.getElementById("strLaundryAmount")){
    			document.getElementById("strLaundryAmount").value='';
    		}
    		document.getElementById("trLaundryAmount").style.display = "none";
    		
    		if(document.getElementById("strSundryAmount")){
    			document.getElementById("strSundryAmount").value='';
    		}
    		document.getElementById("trSundryAmount").style.display = "none";
    		
    		othervalue('');
    	}
    }
    
    function checkTransportType(){
    	var transportType = document.getElementById("transportType").value;
    	if(parseInt(transportType) == 1){
    		document.getElementById("trTrain").style.display = 'table-row';
    		document.getElementById("trBus").style.display = 'none';
    		document.getElementById("trFlight").style.display = 'none';
    		document.getElementById("trCar").style.display = 'none';
    	} else if(parseInt(transportType) == 2){
    		document.getElementById("trTrain").style.display = 'none';
    		document.getElementById("trBus").style.display = 'table-row';
    		document.getElementById("trFlight").style.display = 'none';
    		document.getElementById("trCar").style.display = 'none';
    	} else if(parseInt(transportType) == 3){
    		document.getElementById("trTrain").style.display = 'none';
    		document.getElementById("trBus").style.display = 'none';
    		document.getElementById("trFlight").style.display = 'table-row';
    		document.getElementById("trCar").style.display = 'none';
    	} else if(parseInt(transportType) == 4){
    		document.getElementById("trTrain").style.display = 'none';
    		document.getElementById("trBus").style.display = 'none';
    		document.getElementById("trFlight").style.display = 'none';
    		document.getElementById("trCar").style.display = 'table-row';
    	} else {
    		document.getElementById("trTrain").style.display = 'none';
    		document.getElementById("trBus").style.display = 'none';
    		document.getElementById("trFlight").style.display = 'none';
    		document.getElementById("trCar").style.display = 'none';
    	}
    	
    	checkTransportAmount();
    }

    function checkTransportAmount(){
    	var strTransAmount = document.getElementById("strTransAmount").value;
    	var hiddenTravelType = "";
    	if(document.getElementById("hiddenTravelType")){
    		hiddenTravelType = document.getElementById("hiddenTravelType").value;
    	}
    	
    	var transportType = document.getElementById("transportType").value;
    	if(parseInt(transportType) == 1 && document.getElementById("hiddenTrainType")){
    		var hiddenTrainType = document.getElementById("hiddenTrainType").value;
    		var trainType = document.getElementById("trainType").value;
    		if(parseInt(hiddenTrainType) == parseInt(trainType)){
    			var limit = document.getElementById("train_"+hiddenTravelType+"_"+hiddenTrainType).value;
    			if(parseFloat(strTransAmount) <= parseFloat(limit)){
    				document.getElementById("strTransAmount").value=strTransAmount;
    			} else {
    				alert('Enter proper amount upto '+limit);
    				document.getElementById("strTransAmount").value='';    			
    			}	
    		}
    	} else if(parseInt(transportType) == 2 && document.getElementById("hiddenBusType")){
    		var hiddenBusType = document.getElementById("hiddenBusType").value;
    		var busType = document.getElementById("busType").value;
    		var limit = document.getElementById("bus_"+hiddenTravelType+"_"+hiddenBusType).value;
    		if(parseInt(hiddenBusType) == parseInt(busType)){
    			if(parseFloat(strTransAmount) <= parseFloat(limit)){
    				document.getElementById("strTransAmount").value=strTransAmount;
    			} else {
    				alert('Enter proper amount upto '+limit);
    				document.getElementById("strTransAmount").value='';    			
    			}	
    		}
    	} else if(parseInt(transportType) == 3 && document.getElementById("hiddenFlightType")){
    		var hiddenFlightType = document.getElementById("hiddenFlightType").value;
    		var flightType = document.getElementById("flightType").value;
    		var limit = document.getElementById("flight_"+hiddenTravelType+"_"+hiddenFlightType).value;
    		if(parseInt(hiddenFlightType) == parseInt(flightType)){
    			if(parseFloat(strTransAmount) <= parseFloat(limit)){
    				document.getElementById("strTransAmount").value=strTransAmount;
    			} else {
    				alert('Enter proper amount upto '+limit);
    				document.getElementById("strTransAmount").value='';    			
    			}	
    		}
    	} else if(parseInt(transportType) == 4 && document.getElementById("hiddenCarType")){
    		var hiddenCarType = document.getElementById("hiddenCarType").value;
    		var carType = document.getElementById("carType").value;
    		var limit = document.getElementById("car_"+hiddenTravelType+"_"+hiddenCarType).value;
    		if(parseInt(hiddenCarType) == parseInt(carType)){
    			if(parseFloat(strTransAmount) <= parseFloat(limit)){
    				document.getElementById("strTransAmount").value=strTransAmount;
    			} else {
    				alert('Enter proper amount upto '+limit);
    				document.getElementById("strTransAmount").value='';    			
    			}	
    		}
    	} 	
    	sumTotalTravelAmt();
    }

    function checkLodgingAmount(){
    	var lodgingType = document.getElementById("lodgingType").value;
    	if(parseInt(lodgingType) == 9){
    		var lodgingLimit = document.getElementById("lodgingLimit").value;
    		var strLodgingAmount = document.getElementById("strLodgingAmount").value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays").value;
    		
    		var totalLodingLimit = parseFloat(lodgingLimit) * parseFloat(strTravelPlanDays);
    		if(parseFloat(strLodgingAmount) <= parseFloat(totalLodingLimit)){
    			document.getElementById("strLodgingAmount").value=strLodgingAmount;
    		} else {
    			alert('Enter proper amount upto '+lodgingLimit);
    			document.getElementById("strLodgingAmount").value='';    			
    		}	 
    	}	
    	sumTotalTravelAmt();
    }

    function checkLocalConveyanceAmount(){
    	var localConveyanceTranType = document.getElementById("localConveyanceTranType").value;
    	if(document.getElementById("localConveyanceLimit_"+localConveyanceTranType)){
    		var localConveyanceLimit = document.getElementById("localConveyanceLimit_"+localConveyanceTranType).value;
    		var localConveyanceRate = document.getElementById("localConveyanceRate").value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays").value;
    		
    		var totalLocalConveyanceLimit = parseFloat(localConveyanceLimit);
    		if(parseFloat(localConveyanceRate) <= parseFloat(totalLocalConveyanceLimit)){
    			document.getElementById("localConveyanceRate").value=localConveyanceRate;
    		} else {
    			alert('Enter proper km upto '+totalLocalConveyanceLimit);
    			document.getElementById("localConveyanceRate").value='';    			
    		}	 
    	}
    	calLocalConveyanceAmount();
    }

    function calLocalConveyanceAmount(){
    	var localConveyanceKM = document.getElementById("localConveyanceKM").value;
        var localConveyanceRate=document.getElementById("localConveyanceRate").value ;
    	var totalAmt=localConveyanceKM * localConveyanceRate;
    	document.getElementById("strLocalConveyanceAmount").value=totalAmt;
    	
    	sumTotalTravelAmt();
    }

    function checkFoodBeverageAmount(){
    	if(document.getElementById("foodBeverageLimit")){
    		var foodBeverageLimit = document.getElementById("foodBeverageLimit").value;
    		var strFoodBeverageAmount = document.getElementById("strFoodBeverageAmount").value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays").value;
    		
    		var totalFoodBeverageLimit = parseFloat(foodBeverageLimit) * parseFloat(strTravelPlanDays);
    		if(parseFloat(strFoodBeverageAmount) <= parseFloat(totalFoodBeverageLimit)){
    			document.getElementById("strFoodBeverageAmount").value=strFoodBeverageAmount;
    		} else {
    			alert('Enter proper amount upto '+totalFoodBeverageLimit);
    			document.getElementById("strFoodBeverageAmount").value='';    			
    		}
    	}
    	sumTotalTravelAmt();
    }

    function checkLaundryAmount(){
    	if(document.getElementById("laundryLimit")){
    		var laundryLimit = document.getElementById("laundryLimit").value;
    		var strLaundryAmount = document.getElementById("strLaundryAmount").value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays").value;
    		
    		var totalLaundryLimit= parseFloat(laundryLimit) * parseFloat(strTravelPlanDays);
    		if(parseFloat(strLaundryAmount) <= parseFloat(totalLaundryLimit)){
    			document.getElementById("strLaundryAmount").value=strLaundryAmount;
    		} else {
    			alert('Enter proper amount upto '+totalLaundryLimit);
    			document.getElementById("strLaundryAmount").value='';    			
    		}
    	}
    	sumTotalTravelAmt();
    }

    function checkSundryAmount(){
    	if(document.getElementById("sundryLimit")){
    		var sundryLimit = document.getElementById("sundryLimit").value;
    		var strSundryAmount = document.getElementById("strSundryAmount").value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays").value;
    		
    		var totalSundryLimit= parseFloat(sundryLimit) * parseFloat(strTravelPlanDays);
    		if(parseFloat(strSundryAmount) <= parseFloat(totalSundryLimit)){
    			document.getElementById("strSundryAmount").value=strSundryAmount;
    		} else {
    			alert('Enter proper amount upto '+totalSundryLimit);
    			document.getElementById("strSundryAmount").value='';    			
    		}
    	}
    	sumTotalTravelAmt();
    }

    function sumTotalTravelAmt(){
    	var strTransAmount = document.getElementById("strTransAmount").value;
    	var strLodgingAmount = document.getElementById("strLodgingAmount").value;
    	var strLocalConveyanceAmount = document.getElementById("strLocalConveyanceAmount").value;
    	var strFoodBeverageAmount = document.getElementById("strFoodBeverageAmount").value;
    	var strLaundryAmount = document.getElementById("strLaundryAmount").value;
    	var strSundryAmount = document.getElementById("strSundryAmount").value;
    	
    	var totalAmt = 0; 
    	var selecttype = document.getElementById("selecttype").value;
    	//alert('editType==>'+document.getElementById("editType").value);
		if(selecttype != 'Conveyance Bill' && parseInt(document.getElementById("strId").value) > 0 && parseInt(document.getElementById("editType").value) == 1 && (isInt(document.getElementById("strAmount").value) || isFloat(document.getElementById("strAmount").value))){
    		totalAmt += parseFloat(document.getElementById("strAmount").value);
    		//alert('1 totalAmt==>'+totalAmt);
    		document.getElementById("editType").value = "0";
    	}
    	
    	if(isInt(strTransAmount) || isFloat(strTransAmount)){
    		totalAmt += parseFloat(strTransAmount);
    		//alert('2 totalAmt==>'+totalAmt);
    	}
    	if(isInt(strLodgingAmount) || isFloat(strLodgingAmount)){
    		totalAmt += parseFloat(strLodgingAmount);
    		//alert('3 totalAmt==>'+totalAmt);
    	}
    	if(isInt(strLocalConveyanceAmount) || isFloat(strLocalConveyanceAmount)){
    		totalAmt += parseFloat(strLocalConveyanceAmount);
    		//alert('4 totalAmt==>'+totalAmt);
    	}
    	if(isInt(strFoodBeverageAmount) || isFloat(strFoodBeverageAmount)){
    		totalAmt += parseFloat(strFoodBeverageAmount);
    		//alert('5 totalAmt==>'+totalAmt);
    	}
    	if(isInt(strLaundryAmount) || isFloat(strLaundryAmount)){
    		totalAmt += parseFloat(strLaundryAmount); 
    		//alert('6 totalAmt==>'+totalAmt);
    	}
    	if(isInt(strSundryAmount) || isFloat(strSundryAmount)){
    		totalAmt += parseFloat(strSundryAmount);
    		//alert('7 totalAmt==>'+totalAmt);
    	}
    	//alert('fnal totalAmt==>'+totalAmt);
    	document.getElementById("strAmount").value = parseFloat(totalAmt);
    }

    function isInt(n){
        return n != "" && !isNaN(n) && Math.round(n) == n;
    }
    function isFloat(n){
        return n != "" && !isNaN(n) && Math.round(n) != n;
    }
    
    function getTravelPlanDetails(val){
    	document.getElementById("strTravelPlanDays").value='';
    	
    	var xmlhttp = GetXmlHttpObject();
    	if (xmlhttp == null) {
    		alert("Browser does not support HTTP Request");
    		return;
    	} else {
    		var xhr = $.ajax({
    			url : 'GetTravelPlanDetails.action?empId='+document.getElementById('strSelectedEmpId1').value+'&travelId='+val,
    			cache : false,
    			success : function(data) {
    				document.getElementById("strTravelPlanDays").value=data;
    			}
    		});

    	}
    }
	
    function readFileURL(input, targetDiv) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#'+targetDiv).attr('path', e.target.result);
            };
            reader.readAsDataURL(input.files[0]);
        }
    }
    
    $("#frm_MyReimbursements").submit(function(e){
		//alert("check ........");
		e.preventDefault();
		var paycycle = document.getElementById("paycycle").value;
		if($("#file").attr('path') !== undefined){
  		  var form_data = new FormData($(this)[0]);
  		  form_data.append("strDocument", $("#file").attr('path'));
  		  //alert("form_data ===>> " + form_data);
  		  $("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  		  $.ajax({
	      		url: "AddReimbursements.action",
	      		type: 'POST',
	      		data: form_data,
	      		contentType: false,
	            cache: false,
	      		processData: false
	      		/* success: function(result){
	      			$("#divResult").html(result);
	      	    } */
	      	 });
	   	  } else {
	   		var form_data = $("form[name='frm_MyReimbursements']").serialize();
	   		//alert("form_data ===>> " + form_data);
	     	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	     	$.ajax({
	 			url : "AddReimbursements.action",
	 			data: form_data,
	 			cache : false/* ,
	 			success : function(res) {
	 				$("#subDivResult").html(res);
	 			} */
	 		});
	   	  }
		
		$.ajax({
			url: 'Reimbursements.action?paycycle='+paycycle,
			cache: true,
			success: function(result){
				
				$("#subDivResult").html(result);
			}
		});
		
	});
    
    jQuery(document).ready(function(){
    	$("#submitButton").click(function(){
    		$("#frm_MyReimbursements").find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#frm_MyReimbursements").find('.validateRequired').filter(':visible').prop('required',true);
        });
    }); 
    
    function checkValue() {
    	var noOfPerson = document.getElementById("noofperson").value;
    	if(parseInt(noOfPerson) == 0) {
    		alert("Invalid no of person!");
    		document.getElementById("noofperson").value ="";
    	}
    }
    
    function removeDocument(spanId,rid,strDoc,docFilePath){
    	if(confirm('Are you sure, do you want to remove \''+strDoc+'\' this document?')){
	    	var action = 'AddReimbursements.action?removeDoc=removeDoc&rid='+rid+'&strDoc='+encodeURIComponent(strDoc)+'&docFilePath='+encodeURIComponent(docFilePath);
	    	getContent(spanId,action);
    	}
    }
    
</script>

<div class="box-body">
	<s:form id="frm_MyReimbursements" theme="simple" name="frm_MyReimbursements" action="AddReimbursements" enctype="multipart/form-data" method="post">
			<s:hidden name="strId" id="strId"/>
			<s:hidden name="strPaycycle" id="strPaycycle"/>
			<s:hidden name="pageType"/>
			<s:hidden name="changeEmpType"></s:hidden>
			<s:hidden name="editType" id="editType" value="1"></s:hidden>
			<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id %>"/>
			<%
			int cnt1=0;
			List<Map<String, String>> alLocal = (List<Map<String, String>>)request.getAttribute("alLocal");
			if(alLocal == null) alLocal = new ArrayList<Map<String,String>>();
			int nAlLocal = alLocal.size();
			for(int i=0; i < nAlLocal; i++){
				Map<String, String> hmLocal = alLocal.get(i);
				String strLocalType = "";
				if(uF.parseToInt(hmLocal.get("REIMBURSEMENT_LOCAL_TYPE_ID")) == 1){
					strLocalType = hmLocal.get("REIMBURSEMENT_LOCAL_TYPE_ID")+"_"+hmLocal.get("REIMBURSEMENT_TRANSPORT_TYPE");
				} else if(uF.parseToInt(hmLocal.get("REIMBURSEMENT_LOCAL_TYPE_ID")) == 2){
					strLocalType = hmLocal.get("REIMBURSEMENT_LOCAL_TYPE_ID");
				} else if(uF.parseToInt(hmLocal.get("REIMBURSEMENT_LOCAL_TYPE_ID")) == 3){
					strLocalType = hmLocal.get("REIMBURSEMENT_LOCAL_TYPE_ID")+"_"+hmLocal.get("REIMBURSEMENT_TRANSPORT_TYPE");
				}
			%>
				<input type="hidden" name="local_<%=strLocalType %>" id="local_<%=strLocalType %>" value="<%=uF.parseToDouble(hmLocal.get("REIMBURSEMENT_LOCAL_LIMIT")) %>"/>
			<%}%>
			
			<%
			List<Map<String, String>> alMobileBill = (List<Map<String, String>>)request.getAttribute("alMobileBill");
			if(alMobileBill == null) alMobileBill = new ArrayList<Map<String,String>>();
			int nMobileBill = alMobileBill.size();
			for(int i=0; i < nMobileBill; i++){
				Map<String, String> hmMobileInner = alMobileBill.get(i);
			%>
				<input type="hidden" name="mobile" id="mobile" value="<%=uF.parseToDouble(hmMobileInner.get("REIMBURSEMENT_MOBILE_LIMIT")) %>"/>
			<%}%>
			
			<%
			List<Map<String, String>> alClaim = (List<Map<String, String>>)request.getAttribute("alClaim");
			if(alClaim == null) alClaim = new ArrayList<Map<String,String>>();
			int nAlClaim = alClaim.size();
			for(int i=0; i < nAlClaim; i++){
				Map<String, String> hmClaimInner = alClaim.get(i);
				String strTravelTypeId = hmClaimInner.get("REIMBURSEMENT_TRAVEL_TYPE_ID");
			%>
				<input type="hidden" name="hiddenTravelType" id="hiddenTravelType" value="<%=strTravelTypeId %>"/>
			<%	if(uF.parseToInt(strTravelTypeId) == 1){
					String strTrainTypeId = hmClaimInner.get("REIMBURSEMENT_TRAIN_TYPE_ID");
					if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT_TYPE_ID")) == 2){ 
			%>
						<input type="hidden" name="hiddenTrainType" id="hiddenTrainType" value="<%=strTrainTypeId %>"/>
						<input type="hidden" name="train_<%=strTravelTypeId %>_<%=strTrainTypeId %>" id="train_<%=strTravelTypeId %>_<%=strTrainTypeId %>" value="<%=uF.parseToDouble(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT")) %>"/>
			<%		}
				} else if(uF.parseToInt(strTravelTypeId) == 2){
					String strBusTypeId = hmClaimInner.get("REIMBURSEMENT_BUS_TYPE_ID");
					if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT_TYPE_ID")) == 2){ 
			%>
						<input type="hidden" name="hiddenBusType" id="hiddenBusType" value="<%=strBusTypeId %>"/>
						<input type="hidden" name="bus_<%=strTravelTypeId %>_<%=strBusTypeId %>" id="bus_<%=strTravelTypeId %>_<%=strBusTypeId %>" value="<%=uF.parseToDouble(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT")) %>"/>
			<%		} 
				} else if(uF.parseToInt(strTravelTypeId) == 3){
					String strFlightTypeId = hmClaimInner.get("REIMBURSEMENT_FLIGHT_TYPE_ID");
					if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT_TYPE_ID")) == 2){ 
			%>
						<input type="hidden" name="hiddenFlightType" id="hiddenFlightType" value="<%=strFlightTypeId %>"/>
						<input type="hidden" name="flight_<%=strTravelTypeId %>_<%=strFlightTypeId %>" id="flight_<%=strTravelTypeId %>_<%=strFlightTypeId %>" value="<%=uF.parseToDouble(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT")) %>"/>
			<%		}
				} else if(uF.parseToInt(strTravelTypeId) == 4){
					String strCarTypeId = hmClaimInner.get("REIMBURSEMENT_CAR_TYPE_ID");
					if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT_TYPE_ID")) == 2){ 
			%>
						<input type="hidden" name="hiddenCarType" id="hiddenCarType" value="<%=strCarTypeId %>"/>
						<input type="hidden" name="car_<%=strTravelTypeId %>_<%=strCarTypeId %>" id="car_<%=strTravelTypeId %>_<%=strCarTypeId %>" value="<%=uF.parseToDouble(hmClaimInner.get("REIMBURSEMENT_TRAVEL_LIMIT")) %>"/>
			<%		}
				}
				
				if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_LODGING_TYPE_ID")) == 9){
					if(uF.parseToInt(hmClaimInner.get("REIMBURSEMENT_LODGING_LIMIT_TYPE_ID")) == 2){
			%>
						<input type="hidden" name="lodgingLimit" id="lodgingLimit" value="<%=uF.parseToDouble(hmClaimInner.get("REIMBURSEMENT_LODGING_LIMIT")) %>"/>
			<%  	}
				}
			%>
				<input type="hidden" name="localConveyanceLimit_<%=hmClaimInner.get("REIMBURSEMENT_LOCAL_CONVEYANCE_TRAN_ID") %>" id="localConveyanceLimit_<%=hmClaimInner.get("REIMBURSEMENT_LOCAL_CONVEYANCE_TRAN_ID") %>" value="<%=uF.parseToDouble(hmClaimInner.get("REIMBURSEMENT_LOCAL_CONVEYANCE_LIMIT")) %>"/>
				<input type="hidden" name="foodBeverageLimit" id="foodBeverageLimit" value="<%=uF.parseToDouble(hmClaimInner.get("REIMBURSEMENT_FOOD_LIMIT")) %>"/>
				<input type="hidden" name="laundryLimit" id="laundryLimit" value="<%=uF.parseToDouble(hmClaimInner.get("REIMBURSEMENT_LAUNDRY_LIMIT")) %>"/>
				<input type="hidden" name="sundryLimit" id="sundryLimit" value="<%=uF.parseToDouble(hmClaimInner.get("REIMBURSEMENT_SUNDRY_LIMIT")) %>"/>
			<%}%>
			
			<table class="table table_no_border form-table">
			<% if(strUserTYpe!=null && !strUserTYpe.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserTYpe.equalsIgnoreCase(IConstants.ARTICLE) && !strUserTYpe.equalsIgnoreCase(IConstants.CONSULTANT)){ %>
				<tr>
				 	<td>&nbsp;</td>
				 	<td>
					 	<s:select name="strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="0" headerValue="Select Location" list="wLocationList" key="" 
							onchange="getContent('myDivL', 'GetEmployeeList.action?location='+document.frm_MyReimbursements.strWLocation.options[document.frm_MyReimbursements.strWLocation.selectedIndex].value+'&strMul=N&strPro=P')"/>
				 	</td>
				 	<td colspan="2" id="myDivL">
				 		<%-- <s:select name="strSelectedEmpId1" listKey="employeeId" id="strSelectedEmpId1" listValue="employeeName" headerKey="0"  headerValue="Select Employee" list="empNamesList1" key="" cssClass="validateRequired"
							onchange="getContent('typeC', 'GetEmpClientList.action?empId='+document.frm_MyReimbursements.strSelectedEmpId1.options[document.frm_MyReimbursements.strSelectedEmpId1.selectedIndex].value)"/> --%>
							<s:select name="strSelectedEmpId1" listKey="employeeId" id="strSelectedEmpId1" listValue="employeeName" headerKey="0"  headerValue="Select Employee" list="empNamesList1" key="" cssClass="validateRequired"
								onchange="submitSelectEmpForm();"/> 
				 	</td>
				 </tr>
			
			<%} else {%>
				<tr>
				 	<td colspan="4"><input type="hidden" name="strSelectedEmpId1" id="strSelectedEmpId1" value="<%=uF.showData((String)request.getAttribute("strEmpID11"),"0") %>"/></td>
				</tr>			
			<%} %>
				<tr>
					<td class="alignRight">PayCycle:<sup>*</sup></td>
					<td colspan="3">
						<s:select name="paycycle"  id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" 
						onchange="getReimbursementType(this.value);" cssClass="validateRequired"/>
						<!-- onchange="getContent('reimbDivId', 'GetReimbursementType.action?empId='+document.getElementById('strSelectedEmpId1').value+'&paycycle='+this.value)" -->
					</td> 
				</tr>
				 
				<tr>
					<td>&nbsp;</td>
					<td colspan="3">
						<div id="reimbDivId">
						<s:radio name="reimbursementType" id="reimbursementType" listKey="typeId" listValue="typeName"  list="reimbursementTypeList" onchange="showType(this.value);"></s:radio>
						</div> 
					</td>
				</tr>
	
				<tr id="trReimbDate" style="display: none;">
					<!-- <td class="alignRight" >Reimbursement Date</td> -->
					<td class="alignRight" id="reimbDate"><span>Reimbursement Date:<sup>*</sup></span></td>
					<td colspan="3" id="reimbDate"> 
					<s:textfield name="strFromDate" id="strFromDate" cssStyle="width: 100px !important;" cssClass="validateRequired" readonly="true"/>
					<%-- <s:textfield name="strToDate" id="strToDate" cssStyle="width: 100px !important;"  /> --%> 	
					</td>
				</tr>
				<tr>
					<td class="alignRight" id="client" style="display:none"><span>Select Client:<sup>*</sup> </span></td>
					<td colspan="3" id="typeC" <%=(!"P".equalsIgnoreCase((String)request.getAttribute("reimbursementType")))?"style=\"display:none\"":"" %>>
			    		<s:select theme="simple" name="strClient" listKey="clientId" cssClass="validateRequired" listValue="clientName" headerKey="" headerValue="Select Client"		
							list="clientList" key="" required="true" onchange="getContent('typeP', 'GetProjectClientTask.action?client_id='+this.value+'&type=R')"/>
					</td>
				</tr>
				  
				<tr>
					<td class="alignRight" id="pro" style="display:none"><span>Select Project:<sup>*</sup> </span></td>
					<td colspan="3" id="typeP" <%=(!"P".equalsIgnoreCase((String)request.getAttribute("reimbursementType")))?"style=\"display:none\"":"" %>>
			    		<s:select theme="simple" name="strProject" listKey="projectID" cssClass="validateRequired" listValue="projectName" headerKey="" headerValue="Select Project"		
							list="projectList" key="" required="true" />
					</td>
				</tr>
					
				<tr>
					<td class="alignRight" id="trav" style="display:none"><span >Travel plan:<sup>*</sup> </span></td>
					<td colspan="3" id="typeT" <%=(!"T".equalsIgnoreCase((String)request.getAttribute("reimbursementType")))?"style=\"display:none\"":"" %>>
						<s:select theme="simple" name="strTravelPlan" id="strTravelPlan" listKey="leaveId" cssClass="validateRequired" listValue="planName" headerKey="" headerValue="Select Travel Plan"		
							list="travelPlanList" key="" required="true" onchange="getTravelPlanDetails(this.value);"/>
						<s:hidden name="strTravelPlanDays" id="strTravelPlanDays"/>	
					</td>
				</tr>
					
				<tr>
					<td class="alignRight" id="othr">Type :<sup>*</sup></td>
					<td colspan="3" id="typeO" <%=(!"L".equalsIgnoreCase((String)request.getAttribute("reimbursementType")))?"style=\"display:none\"":"" %>>
						<s:select id="selecttype" theme="simple" name="strType" listKey="typeId" cssClass="validateRequired" listValue="typeName" headerKey="" headerValue="Select Type"		
							list="typeList" key="" required="true" onchange="othervalue(this.value);setamount();" />
					</td>
				</tr>
				
				<tr id="1">
					<td class="alignRight">Mode of travel:<sup>*</sup></td>
					<td colspan="3">
						<s:select theme="simple" name="modeoftravel" id="modeoftravel" listKey="typeId" cssClass="validateRequired" listValue="typeName" headerKey="" headerValue="Select Mode"		
							list="modeoftravelList" key="" required="true" onchange="clearAmt();"/>
					</td>
				</tr>
				
				<tr>
					<td class="alignRight">Number of persons:<sup>*</sup></td>
					<td colspan="3">
						<s:textfield name="noofperson" id="noofperson" cssStyle="width: 81px !important; text-align: right;" cssClass="validateRequired" onkeyup="checkValue();"   onkeypress="return isNumberKey(event)"/>
					</td>
				</tr>
				
				<tr id="trTransType" style="display: none;">
					<td class="alignRight">Transportation Type:<sup>*</sup></td>
					<td colspan="3">
						<s:select theme="simple" name="transportType" id="transportType" list="#{'1':'Train','2':'Bus','3':'Flight','4':'Car'}" 
							cssClass="validateRequired" headerKey="" headerValue="Select Transport Type" onchange="checkTransportType();"/>
					</td>
				</tr> 
				
				<tr id="trTrain" style="display: none;">
					<td class="alignRight">Train:<sup>*</sup></td>
					<td colspan="3">
						<s:select theme="simple" name="trainType" id="trainType" list="#{'1':'3 Tier','2':'Chair Car','3':'AC 3 Tier','4':'AC 2 Tier','5':'AC 1st Class'}" 
							 onchange="checkTransportAmount();" cssClass="validateRequired"/>
					</td>
				</tr>
				
				<tr id="trBus" style="display: none;">
					<td class="alignRight">Bus:<sup>*</sup></td>
					<td colspan="3">
						<s:select theme="simple" name="busType" id="busType" list="#{'1':'A/c Bus','2':'Non- A/c Bus'}" cssClass="validateRequired" onchange="checkTransportAmount();"/>
					</td>
				</tr>
				
				<tr id="trFlight" style="display: none;">
					<td class="alignRight">Flight:<sup>*</sup></td> 
					<td colspan="3">
						<s:select theme="simple" name="flightType" id="flightType" list="#{'1':'Economy Class','2':'Business Class'}" cssClass="validateRequired" onchange="checkTransportAmount();"/>
					</td>
				</tr>
				
				<tr id="trCar" style="display: none;"> 
					<td class="alignRight">Car:<sup>*</sup></td>
					<td colspan="3">
						<s:select theme="simple" name="carType" id="carType" list="#{'1':'Cab','2':'Self Owned'}" cssClass="validateRequired" onchange="checkTransportAmount();"/>
					</td>
				</tr>
				
				<tr id="trTransAmount" style="display: none;">
					<td class="alignRight">Transport Amount:<sup>*</sup></td>
					<td colspan="3">
						<s:textfield name="strTransAmount" id="strTransAmount" cssStyle="width: 81px !important; text-align: right;" cssClass="validateRequired" onkeyup="checkTransportAmount();" onkeypress="return isNumberKey(event);"/>
					</td>
				</tr>
				
				<tr id="trLodgingType" style="display: none;">
					<td class="alignRight">Lodging Type:</td>
					<td colspan="3">
						<s:select theme="simple" name="lodgingType" id="lodgingType" listKey="lodgingTypeId" 
							listValue="lodgingTypeName" headerKey="" headerValue="Select Lodging Type" list="lodgingTypeList" key="" onchange="checkLodgingAmount();"/>					
					</td>
				</tr> 
				
				<tr id="trLodgingAmount" style="display: none;">
					<td class="alignRight">Lodging Amount:</td>
					<td colspan="3">
						<s:textfield name="strLodgingAmount" id="strLodgingAmount" cssStyle="width: 81px !important; text-align: right;" onkeyup="checkLodgingAmount();" onkeypress="return isNumberKey(event);"/>
					</td>
				</tr>
				
				<tr id="trLocalConveyanceType" style="display: none;">
					<td class="alignRight">Local Conveyance Type:<sup>*</sup></td>
					<td>
						<s:select theme="simple" name="localConveyanceTranType" id="localConveyanceTranType" listKey="typeId" listValue="typeName" cssClass="validateRequired"
							headerKey="" headerValue="Select Mode" list="localConveyanceTranTypeList" key="" required="true" onchange="checkLocalConveyanceAmount();"/>						
					</td>
				</tr>
				
				<tr id="trLocalConveyanceRate" style="display: none;">
					<td class="alignRight">Total KM:</td>
					<td colspan="3">
						<s:textfield name="localConveyanceKM" id="localConveyanceKM" cssStyle="width:70px !important; text-align: right;" onkeyup="checkLocalConveyanceAmount();" onkeypress="return isNumberKey(event)"/>&nbsp;
						Rate/KM:&nbsp;<s:textfield name="localConveyanceRate" id="localConveyanceRate" cssStyle="width:50px !important; text-align: right;" onkeyup="checkLocalConveyanceAmount();" onkeypress="return isNumberKey(event)"/>
					</td> 
				</tr> 
				
				<tr id="trLocalConveyanceAmount" style="display: none;">
					<td class="alignRight">Local Conveyance Amount:</td>
					<td colspan="3">
						<s:textfield name="strLocalConveyanceAmount" id="strLocalConveyanceAmount" cssStyle="width: 81px !important; text-align: right;" onkeypress="return isNumberKey(event);" onkeyup="sumTotalTravelAmt();"/>
					</td>
				</tr>
				
				<tr id="trFoodBeverageAmount" style="display: none;">
					<td class="alignRight">Food &amp; Beverage:</td>
					<td colspan="3">
						<s:textfield name="strFoodBeverageAmount" id="strFoodBeverageAmount" cssStyle="width: 81px !important; text-align: right;" onkeyup="checkFoodBeverageAmount();" onkeypress="return isNumberKey(event);"/>
					</td>
				</tr> 
				
				<tr id="trLaundryAmount" style="display: none;">
					<td class="alignRight">Laundry:</td>
					<td colspan="3">
						<s:textfield name="strLaundryAmount" id="strLaundryAmount" cssStyle="width: 81px !important; text-align: right;" onkeyup="checkLaundryAmount();" onkeypress="return isNumberKey(event);"/>
					</td>
				</tr>
				
				<tr id="trSundryAmount" style="display: none;">
					<td class="alignRight">Sundry:</td>
					<td colspan="3">
						<s:textfield name="strSundryAmount" id="strSundryAmount" cssStyle="width: 81px !important; text-align: right;" onkeyup="checkSundryAmount();" onkeypress="return isNumberKey(event);"/>
					</td>
				</tr> 
				
				<tr>
					<td class="alignRight">Vendor:</td>
					<td colspan="3">
						<s:textfield name="strVendor" id="strVendor"/>
					</td>
				</tr>
				
				<%if(strE!=null){ %>				
					<tr>
						<td class="alignRight" valign="top">Receipt No:</td>
						<td colspan="3">
							<div id="divReceiptNo">
								<%
								int nAlReceiptNo = alReceiptNo.size();
								for(int i = 0; i < nAlReceiptNo; i++){
									cnt1++;
								%>
									<div id="row_receipt<%=cnt1 %>" style="float:left;">
								 		<input type="text" name="strReceiptNo" id="strReceiptNo" style="float:left;" value="<%=alReceiptNo.get(i) %>"/>
								 		<a href="javascript:void(0)" onclick="addReceiptNo()" class="add-font" style="float:right;margin:0px;"></a>
								 		<%if(i > 0){ %>
								     		<a href="javascript:void(0)" onclick="removeReceiptNo(<%=cnt1 %>)" id="removeReceiptId<%=cnt1 %>" class="remove-font" style="float:left; margin:0px;"></a>
								     	<%} %>
								 	</div>
									
								<%}
								if(nAlReceiptNo == 0){
									cnt1++;
								%>
									<s:textfield name="strReceiptNo" id="strReceiptNo" cssStyle="float:left"/>
									<a href="javascript:void(0)" onclick="addReceiptNo()" style="float:left;" class="add-font"></a>
								<%} %>
							</div>
						</td>
					</tr>
				<%}else{
					cnt1++;
				%>
					<tr>
						<td class="alignRight" valign="top">Receipt No:</td>
						<td colspan="3">
							<s:textfield name="strReceiptNo" id="strReceiptNo" cssStyle="float:left"/>
							<a href="javascript:void(0)" onclick="addReceiptNo()" style="float:left;" class="add-font"></a>
							<div id="divReceiptNo"></div>
						</td>
					</tr>
				<%} %>
				
				<tr id="3">
					<td class="alignRight">Place From:<sup>*</sup></td>
					<td colspan="3">
						<s:textfield name="placefrom" id="placefrom" cssClass="validateRequired"/>
					</td>
				</tr>
					
				<tr id="4">
					<td class="alignRight">Place To:<sup>*</sup></td>
					<td colspan="3">
						<s:textfield name="placeto" id="placeto" cssClass="validateRequired"/>
					</td>
				</tr>
					
				<tr id="5">
					<td class="alignRight">No of Days:<sup>*</sup></td>
					<td colspan="3">
						<s:textfield name="noofdays" id="noofdays" cssStyle="width: 81px !important; text-align: right;" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
					</td>
				</tr>
					
				<tr id="6">
					<td class="alignRight">Total KM:</td>
					<td colspan="3">
						<s:textfield name="kmpd" id="kmpd" cssStyle="width:70px !important; text-align: right;" onkeyup="setamount();" onkeypress="return isNumberKey(event)"/>&nbsp;
						Rate/KM:&nbsp;<s:textfield name="ratepkm" id="ratepkm" cssStyle="width:50px !important; text-align: right;" onkeyup="setamount();" onkeypress="return isNumberKey(event)"/>
					</td>
				</tr>
					
				<tr>
					<td class="alignRight" style="border: 0px none; display: table-cell;" id="tdReimbCurrency">Currency:<sup>*</sup></td>
					<td style="border: 0px none;">
					<s:select theme="simple" name="reimbCurrency" id="reimbCurrency" listKey="currencyId" listValue="currencyName" 
						cssClass="validateRequired" list="currencyList" key="" required="true"/> <!-- headerKey="" headerValue="Select Mode" -->
					</td>
				</tr>
				
				<tr>
					<td class="alignRight">Amount:<sup>*</sup></td>
					<td colspan="3">
					<%--System.out.println("strAmount==>"+request.getAttribute("strAmount")); --%> 
						<input type="text" name="strAmount" id="strAmount" value="<%=uF.showData((String)request.getAttribute("strAmount"),"0")%>" style="width: 81px !important; text-align: right;" class="validateRequired" onkeyup="setamount();" onkeypress="return isNumberKey(event);"/>
					</td>				
				</tr>
				
				<tr>
					<td class="alignRight" style="border: 0px none; display: table-cell;" id="tdReimbPaymentMode">Payment Mode:</td>
					<td style="border: 0px none;">
						<s:select theme="simple" name="reimbPaymentMode" id="reimbPaymentMode" listKey="payModeId" listValue="payModeName" 
							list="paymentModeList" key=""/>
					</td>
				</tr>
				
				<tr>
					<td class="alignRight" valign="top">Purpose:</td>
					<td colspan="3">
						<s:textarea rows="5" cols="50" name="strPurpose"></s:textarea>
					</td>				
				</tr>
				
				<tr id="isbillable">
					<td class="alignRight" valign="top">Chargeable to client:</td>
					<td colspan="3"><s:checkbox name="isbillable" fieldValue="true"></s:checkbox></td>
				</tr>
				<%if(CF.getIsReceipt()){ %>
					<tr>
						<td colspan="4" style="text-align: center;">Please ensure that you attach receipts, else this will be taxable.</td>				
					</tr>
				<%} %>
				<%if(strE!=null){ %>
					<tr>
						<td>&nbsp;</td>
						<td colspan="3">
							<%=uF.showData((String)request.getAttribute("strViewDocument"),"") %>
						</td>
					</tr>
					<tr>
						<td class="alignRight" valign="top">Attach Document:</td>
						<td colspan="3">
							<span id="file"></span>
							<s:file accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="strDocument"   cssStyle="float:left" onchange="readFileURL(this, 'file');"/>
							<a href="javascript:void(0)" onclick="addDocument()" style="float:left;" class="add-font"></a>
							<div id="div_documents"></div>
						</td>
					</tr>
				<%}else{%>		
					<tr>
						<td class="alignRight" valign="top">Attach Document:</td>
						<td colspan="3">
							<span id="file"></span>
							<s:file accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="strDocument" cssStyle="float:left;" onchange="readFileURL(this, 'file');"/>
							<a href="javascript:void(0)" onclick="addDocument()" style="float:left;" class="add-font"></a>
							<div id="div_documents"></div>
						</td>
						
					</tr>
				<%}%>
				
				<%
					if(uF.parseToBoolean(CF.getIsWorkFlow())){		
						 if(hmMemberOption != null && !hmMemberOption.isEmpty() ){
							Iterator<String> it1 = hmMemberOption.keySet().iterator();
							while(it1.hasNext()){
								String memPosition=it1.next();
								String optiontr=hmMemberOption.get(memPosition);					
								out.println(optiontr); 
							}
							%>
							<tr>
								<td>&nbsp;</td>
								<td colspan="3">
								<input type="submit" name="submit" id="submitButton" value="Submit" class="btn btn-primary"/>
								</td>
							</tr>
						<% }else{%>
							 <tr><td colspan="4">Your work flow is not defined. Please, speak to your hr for your workflow.</td></tr>
						 <%}%>
					<%}else{%>
						<tr>
							<td>&nbsp;</td>
							<td colspan="3"><input type="submit" name="submit" id="submitButton" value="Submit" class="btn btn-primary"/></td>
						</tr>
					<%}
					%>
					
			</table>
			<input type="hidden" name="cnt1" id="cnt1" value="<%=cnt1 %>"/>
			<%-- <div id="submitdivid">
				<span style="margin-left: 120px;"><input type="hidden" name="policy_id" id="policy_id" value="<%=request.getAttribute("policy_id") %>"/>
				<%if(request.getAttribute("divpopup")!=null){
					if(uF.parseToBoolean(CF.getIsWorkFlow())){
					%>
						<a href="#?w=600" rel="popup_name<%=request.getAttribute("strEmpID") %>" class="poplight" id="divid" >
						  	<%=request.getAttribute("divpopup") %>
						 </a>
				<%}else{%>
					<%=request.getAttribute("divpopup") %>
				<%}
				}%>
				</span>
				<%
					if (request.getAttribute("reimbursementsD") != null) {
						out.println(request.getAttribute("reimbursementsD"));
					}
				%>
			</div> --%>
		<s:token />
	</s:form>
</div>

<script type="text/javascript">

othervalue(document.frm_MyReimbursements.strType.options[document.frm_MyReimbursements.strType.selectedIndex].value);
//showType(document.frm_MyReimbursements.reimbursementType.value);

function getReimbursementType(paycycle){
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var xhr = $.ajax({
			url : 'GetReimbursementType.action?empId='+document.getElementById('strSelectedEmpId1').value+'&paycycle='+paycycle,
			cache : false,

			success : function(data) {
				var res = data.split("::::");
				document.getElementById("reimbDivId").innerHTML=res[0];
				showType(res[1].trim());
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
	


	

</script>