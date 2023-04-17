<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillRimbursementType"%>
<%@page import="com.konnect.jpms.select.FillPayCycles"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    UtilityFunctions uF = new UtilityFunctions();
    String strUserTYpe = (String)session.getAttribute(IConstants.USERTYPE);
    String strE = (String)request.getParameter("E"); 
    String reimType = (String) request.getAttribute("reimType");
    Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
    if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
    String policy_id = (String) request.getAttribute("policy_id");
    %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript"> 
    $(document).ready( function () {
    	$("#submitButton").click(function(){
        	$("#frm_MyReimbursements").find('.validateRequired').filter(':hidden').prop('required',false);
        	$("#frm_MyReimbursements").find('.validateRequired').filter(':visible').prop('required',true);
        	
        });
        
    	$("#strFromDate1").datepicker({format: 'dd/mm/yyyy'});
    	
    	var value='<%=reimType %>';
    	showType(value,1);
    });
    
    var cnt=0;
    function addDoc(count) {
    	//alert("dfgsgfdf");
    	cnt++;
    	var divTag = document.createElement("div");
        divTag.id = "row_kra"+cnt;
    	divTag.innerHTML = 	"<div class=\"row row_without_margin\">"+
		"<div class=\"col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding\">"+
		"<input type=\"file\" name=\"strDocument"+count+"\"></div>"+
		"<div class=\"col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding\">"+
		"<a href=\"javascript:void(0)\" onclick=\"addDoc("+count+")\" class=\"add-font\"></a>"+
		"<a href=\"javascript:void(0)\" onclick=\"removeDoc(this.id,"+count+")\" id=\""+cnt+"\" class=\"remove-font\"></a>"+
		"</div></div>"; 
        document.getElementById("div_documents"+count).appendChild(divTag);
        
        var fileCount = document.getElementById("fileCount"+count).value;
        fileCount = parseInt(fileCount) + 1;
        document.getElementById("fileCount"+count).value=fileCount;
         
    }
    
    function removeDoc(removeId,cnt) {
    	var remove_elem = "row_kra"+removeId;
    	var row_kra = document.getElementById(remove_elem);
    	document.getElementById("div_documents"+cnt).removeChild(row_kra);
    	
    	var fileCount = document.getElementById("fileCount"+cnt).value;
        fileCount = parseInt(fileCount) - 1;
        document.getElementById("fileCount"+cnt).value=fileCount;	
    }

    var cnt1=0;
    function addReceiptNo(count) {
    	cnt1++;
    	var divTag = document.createElement("div");
        divTag.id = "row_receipt"+cnt1;
    	divTag.innerHTML = 	"<div style=\"float:left\">"+
    		"<input type=\"text\" name=\"strReceiptNo"+count+"\">"+
    		"<a href=\"javascript:void(0)\" onclick=\"addReceiptNo("+count+")\" class=\"add-font\"></a>"+
    		"<a href=\"javascript:void(0)\" onclick=\"removeReceiptNo("+cnt1+","+count+")\" id=\"removeReceiptId"+cnt1+"\" class=\"remove-font\"></a>"+
    		"</div>";
        document.getElementById("divReceiptNo"+count).appendChild(divTag);
        
        var receiptNoCount = document.getElementById("receiptNoCount"+count).value;
        receiptNoCount = parseInt(receiptNoCount) + 1;
        document.getElementById("receiptNoCount"+count).value=fileCount;
         
    }

    function removeReceiptNo(removeId,cnt) {
    	
    	var remove_elem = "row_receipt"+removeId;
    	var row_receipt = document.getElementById(remove_elem); 
    	document.getElementById("divReceiptNo"+cnt).removeChild(row_receipt);
    	
    	var receiptNoCount = document.getElementById("receiptNoCount"+cnt).value;
    	receiptNoCount = parseInt(receiptNoCount) - 1;
        document.getElementById("receiptNoCount"+cnt).value=receiptNoCount;	
    }
    
    function showType(value,cnt){
    	// client1 pro1 trav1 othr1 isbillfirst1 typeC1 typeP1 typeT1 typeO1 isbillsecond1 
    	//alert('showType value=>'+value+'--cnt==>'+cnt);
    	if(value=='P'){
    		document.getElementById("typeC"+cnt).style.display = "table-cell";
    		document.getElementById("client1").style.display="table-cell";
    		document.getElementById("typeP"+cnt).style.display = "table-cell";
    		document.getElementById("typeT"+cnt).style.display = "none";
    		document.getElementById("typeO"+cnt).style.display = "table-cell";
    		document.getElementById("pro1").style.display="table-cell";
    		document.getElementById("othr1").style.display="table-cell";
    		document.getElementById("trav1").style.display="none";
    		document.getElementById("isbillfirst1").style.display="table-cell";
    		document.getElementById("isbillsecond"+cnt).style.display="table-cell";
    		
    		if(document.getElementById("transportType")){
    			document.getElementById("transportType").selectedIndex=0;
    		}
    		document.getElementById("trTransType"+cnt).style.display = "none";
    		document.getElementById("trTrain"+cnt).style.display="none";
    		document.getElementById("trBus"+cnt).style.display = "none";
    		document.getElementById("trFlight"+cnt).style.display = "none";
    		document.getElementById("trCar"+cnt).style.display = "none";
    		
    		if(document.getElementById("strTransAmount"+cnt)){
    			document.getElementById("strTransAmount"+cnt).value='';
    		}
    		document.getElementById("trTransAmount"+cnt).style.display = "none";
    		
    		document.getElementById("trLodgingType"+cnt).style.display = "none";
    		if(document.getElementById("strLodgingAmount"+cnt)){
    			document.getElementById("strLodgingAmount"+cnt).value='';
    		}
    		document.getElementById("trLodgingAmount"+cnt).style.display = "none";
    		
    		document.getElementById("trLocalConveyanceType"+cnt).style.display = "none";
    		document.getElementById("trLocalConveyanceRate"+cnt).style.display = "none";
    		if(document.getElementById("localConveyanceKM"+cnt)){
    			document.getElementById("localConveyanceKM"+cnt).value='';
    		}
    		if(document.getElementById("localConveyanceRate"+cnt)){
    			document.getElementById("localConveyanceRate"+cnt).value='';
    		}
    		if(document.getElementById("strLocalConveyanceAmount"+cnt)){
    			document.getElementById("strLocalConveyanceAmount"+cnt).value='';
    		}
    		document.getElementById("trLocalConveyanceAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strFoodBeverageAmount"+cnt)){
    			document.getElementById("strFoodBeverageAmount"+cnt).value='';
    		}
    		document.getElementById("trFoodBeverageAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strLaundryAmount"+cnt)){
    			document.getElementById("strLaundryAmount"+cnt).value='';
    		}
    		document.getElementById("trLaundryAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strSundryAmount"+cnt)){
    			document.getElementById("strSundryAmount"+cnt).value='';
    		}
    		document.getElementById("trSundryAmount"+cnt).style.display = "none";
    		
    		document.getElementById("tdAmt"+cnt).style.display="none";
    		
    	}else if(value=='T'){
    		document.getElementById("typeC"+cnt).style.display = "none";
    		document.getElementById("client1").style.display="none";
    		document.getElementById("typeP"+cnt).style.display = "none";
    		document.getElementById("typeT"+cnt).style.display = "table-cell";
    		document.getElementById("typeO"+cnt).style.display = "none";
    		document.getElementById("pro1").style.display="none";
    		document.getElementById("othr1").style.display="none";
    		document.getElementById("trav1").style.display="table-cell";
    		//document.getElementById("isbillableTr"+cnt).style.display="none";
    		document.getElementById("isbillfirst1").style.display="none";
    		document.getElementById("isbillsecond"+cnt).style.display="none";
    		
    		document.getElementById("trTransType"+cnt).style.display = "table-row";
    		document.getElementById("trTrain"+cnt).style.display="none";
    		document.getElementById("trBus"+cnt).style.display = "none";
    		document.getElementById("trFlight"+cnt).style.display = "none";
    		document.getElementById("trCar"+cnt).style.display = "none";
    		document.getElementById("trTransAmount"+cnt).style.display = "table-row";
    		
    		document.getElementById("trLodgingType"+cnt).style.display = "table-row";
    		document.getElementById("trLodgingAmount"+cnt).style.display = "table-row";
    		
    		document.getElementById("trLocalConveyanceType"+cnt).style.display = "table-row";
    		document.getElementById("trLocalConveyanceRate"+cnt).style.display = "table-row";
    		document.getElementById("trLocalConveyanceAmount"+cnt).style.display = "table-row";
    		
    		document.getElementById("trFoodBeverageAmount"+cnt).style.display = "table-row";
    		
    		document.getElementById("trLaundryAmount"+cnt).style.display = "table-row";
    		
    		document.getElementById("trSundryAmount"+cnt).style.display = "table-row";
    		
    		document.getElementById("tdAmt"+cnt).style.display="table-cell";
    		
    	}else if(value=='M'){
    		document.getElementById("typeC"+cnt).style.display = "none";
    		document.getElementById("client1").style.display="none";
    		document.getElementById("typeP"+cnt).style.display = "none";
    		document.getElementById("typeT"+cnt).style.display = "none";
    		document.getElementById("typeO"+cnt).style.display = "none";
    		document.getElementById("pro1").style.display="none";
    		document.getElementById("othr1").style.display="none";
    		document.getElementById("trav1").style.display="none";
    		//document.getElementById("isbillableTr"+cnt).style.display="none";
    		document.getElementById("isbillfirst1").style.display="none";
    		document.getElementById("isbillsecond"+cnt).style.display="none";
    		
    		if(document.getElementById("transportType")){
    			document.getElementById("transportType").selectedIndex=0;
    		}
    		document.getElementById("trTransType"+cnt).style.display = "none";
    		document.getElementById("trTrain"+cnt).style.display="none";
    		document.getElementById("trBus"+cnt).style.display = "none";
    		document.getElementById("trFlight"+cnt).style.display = "none";
    		document.getElementById("trCar"+cnt).style.display = "none";
    		if(document.getElementById("strTransAmount"+cnt)){
    			document.getElementById("strTransAmount"+cnt).value='';
    		}
    		document.getElementById("trTransAmount"+cnt).style.display = "none";
    		
    		document.getElementById("trLodgingType"+cnt).style.display = "none";
    		if(document.getElementById("strLodgingAmount"+cnt)){
    			document.getElementById("strLodgingAmount"+cnt).value='';
    		}
    		document.getElementById("trLodgingAmount"+cnt).style.display = "none";
    		
    		document.getElementById("trLocalConveyanceType"+cnt).style.display = "none";
    		document.getElementById("trLocalConveyanceRate"+cnt).style.display = "none";
    		if(document.getElementById("localConveyanceKM"+cnt)){
    			document.getElementById("localConveyanceKM"+cnt).value='';
    		}
    		if(document.getElementById("localConveyanceRate"+cnt)){
    			document.getElementById("localConveyanceRate"+cnt).value='';
    		}
    		if(document.getElementById("strLocalConveyanceAmount"+cnt)){
    			document.getElementById("strLocalConveyanceAmount"+cnt).value='';
    		}
    		document.getElementById("trLocalConveyanceAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strFoodBeverageAmount"+cnt)){
    			document.getElementById("strFoodBeverageAmount"+cnt).value='';
    		}
    		document.getElementById("trFoodBeverageAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strLaundryAmount"+cnt)){
    			document.getElementById("strLaundryAmount"+cnt).value='';
    		}
    		document.getElementById("trLaundryAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strSundryAmount"+cnt)){
    			document.getElementById("strSundryAmount"+cnt).value='';
    		}
    		document.getElementById("trSundryAmount"+cnt).style.display = "none";
    		
    		document.getElementById("tdAmt"+cnt).style.display="none";
    		
    	}else if(value=='L'){
    		document.getElementById("typeC"+cnt).style.display = "none";
    		document.getElementById("client1").style.display="none";
    		document.getElementById("typeP"+cnt).style.display = "none";
    		document.getElementById("typeT"+cnt).style.display = "none";
    		document.getElementById("typeO"+cnt).style.display = "table-cell";
    		document.getElementById("pro1").style.display="none";
    		document.getElementById("othr1").style.display="table-cell";
    		document.getElementById("trav1").style.display="none";
    		document.getElementById("isbillfirst1").style.display="none";
    		document.getElementById("isbillsecond"+cnt).style.display="none";
    		
    		if(document.getElementById("transportType"+cnt)){
    			document.getElementById("transportType"+cnt).selectedIndex=0;
    		}
    		document.getElementById("trTransType"+cnt).style.display = "none";
    		document.getElementById("trTrain"+cnt).style.display="none";
    		document.getElementById("trBus"+cnt).style.display = "none";
    		document.getElementById("trFlight"+cnt).style.display = "none";
    		document.getElementById("trCar"+cnt).style.display = "none";
    		if(document.getElementById("strTransAmount"+cnt)){
    			document.getElementById("strTransAmount"+cnt).value='';
    		}
    		document.getElementById("trTransAmount"+cnt).style.display = "none";
    		
    		document.getElementById("trLodgingType"+cnt).style.display = "none";
    		if(document.getElementById("strLodgingAmount"+cnt)){
    			document.getElementById("strLodgingAmount"+cnt).value='';
    		}
    		document.getElementById("trLodgingAmount"+cnt).style.display = "none";
    		
    		document.getElementById("trLocalConveyanceType"+cnt).style.display = "none";
    		document.getElementById("trLocalConveyanceRate"+cnt).style.display = "none";
    		if(document.getElementById("localConveyanceKM"+cnt)){
    			document.getElementById("localConveyanceKM"+cnt).value='';
    		}
    		if(document.getElementById("localConveyanceRate"+cnt)){
    			document.getElementById("localConveyanceRate"+cnt).value='';
    		}
    		if(document.getElementById("strLocalConveyanceAmount"+cnt)){
    			document.getElementById("strLocalConveyanceAmount"+cnt).value='';
    		}
    		document.getElementById("trLocalConveyanceAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strFoodBeverageAmount"+cnt)){
    			document.getElementById("strFoodBeverageAmount"+cnt).value='';
    		}
    		document.getElementById("trFoodBeverageAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strLaundryAmount"+cnt)){
    			document.getElementById("strLaundryAmount"+cnt).value='';
    		}
    		document.getElementById("trLaundryAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strSundryAmount"+cnt)){
    			document.getElementById("strSundryAmount"+cnt).value='';
    		}
    		document.getElementById("trSundryAmount"+cnt).style.display = "none";
    		
    		document.getElementById("tdAmt"+cnt).style.display="none";
    	}else{
    		document.getElementById("typeC"+cnt).style.display = "none";
    		document.getElementById("client1").style.display="none";
    		document.getElementById("typeP"+cnt).style.display = "none";
    		document.getElementById("typeT"+cnt).style.display = "none";
    		document.getElementById("typeO"+cnt).style.display = "table-cell";
    		document.getElementById("pro1").style.display="none";
    		document.getElementById("othr1").style.display="table-cell";
    		document.getElementById("trav1").style.display="none";
    		//document.getElementById("isbillableTr"+cnt).style.display="none";
    		document.getElementById("isbillfirst1").style.display="none";
    		document.getElementById("isbillsecond"+cnt).style.display="none";		
    		
    		if(document.getElementById("transportType"+cnt)){
    			document.getElementById("transportType"+cnt).selectedIndex=0;
    		}
    		document.getElementById("trTransType"+cnt).style.display = "none";
    		document.getElementById("trTrain"+cnt).style.display="none";
    		document.getElementById("trBus"+cnt).style.display = "none";
    		document.getElementById("trFlight"+cnt).style.display = "none";
    		document.getElementById("trCar"+cnt).style.display = "none";
    		if(document.getElementById("strTransAmount"+cnt)){
    			document.getElementById("strTransAmount"+cnt).value='';
    		}
    		document.getElementById("trTransAmount"+cnt).style.display = "none";
    		
    		document.getElementById("trLodgingType"+cnt).style.display = "none";
    		if(document.getElementById("strLodgingAmount"+cnt)){
    			document.getElementById("strLodgingAmount"+cnt).value='';
    		}
    		document.getElementById("trLodgingAmount"+cnt).style.display = "none";
    		
    		document.getElementById("trLocalConveyanceType"+cnt).style.display = "none";
    		document.getElementById("trLocalConveyanceRate"+cnt).style.display = "none";
    		if(document.getElementById("localConveyanceKM"+cnt)){
    			document.getElementById("localConveyanceKM"+cnt).value='';
    		}
    		if(document.getElementById("localConveyanceRate"+cnt)){
    			document.getElementById("localConveyanceRate"+cnt).value='';
    		}
    		if(document.getElementById("strLocalConveyanceAmount"+cnt)){
    			document.getElementById("strLocalConveyanceAmount"+cnt).value='';
    		}
    		document.getElementById("trLocalConveyanceAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strFoodBeverageAmount"+cnt)){
    			document.getElementById("strFoodBeverageAmount"+cnt).value='';
    		}
    		document.getElementById("trFoodBeverageAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strLaundryAmount"+cnt)){
    			document.getElementById("strLaundryAmount"+cnt).value='';
    		}
    		document.getElementById("trLaundryAmount"+cnt).style.display = "none";
    		
    		if(document.getElementById("strSundryAmount"+cnt)){
    			document.getElementById("strSundryAmount"+cnt).value='';
    		}
    		document.getElementById("trSundryAmount"+cnt).style.display = "none";
    		
    		document.getElementById("tdAmt"+cnt).style.display="none";
    	}
    }
    
    function othervalue(value,cnt) {
    	//trModeTravel trPlaceFrom trPlaceTo trNodays trTotalKM trRateKM tdAmt 
    	//alert('othervalue value=>'+value+'--cnt==>'+cnt);
    	if(value == "Travel" || value == "Conveyance Bill"){	
    		document.getElementById("trModeTravel"+cnt).style.display="table-row";
    		document.getElementById("trPlaceFrom"+cnt).style.display="table-row";
    		document.getElementById("trPlaceTo"+cnt).style.display="table-row";
    		document.getElementById("trNodays"+cnt).style.display="table-row";
    		document.getElementById("trTotalKM"+cnt).style.display="table-row";
    		document.getElementById("trRateKM"+cnt).style.display="table-row";
    		
    		document.getElementById("tdAmt"+cnt).style.display="table-cell";
    	}else{ 
    		document.getElementById("trModeTravel"+cnt).style.display="none";
    		document.getElementById("trPlaceFrom"+cnt).style.display="none";
    		document.getElementById("trPlaceTo"+cnt).style.display="none";
    		document.getElementById("trNodays"+cnt).style.display="none";
    		document.getElementById("trTotalKM"+cnt).style.display="none";
    		document.getElementById("trRateKM"+cnt).style.display="none";
    		
    		document.getElementById("tdAmt"+cnt).style.display="none";		
    	}
    	checkTransportType(cnt);
    }
    
    function setamount(cnt){
     	/* var a=document.getElementById("kmpd"+cnt).value;
       	var b=document.getElementById("ratepkm"+cnt).value ;
       	var d=document.getElementById("noofdays"+cnt).value ;
    	var c=a*b*d;  
    	document.getElementById("strAmount"+cnt).value=c; */
    	
    	var reimType = $("input[name='reimType']:checked").val();
        if(reimType == 'L'){
        	var strType = document.getElementById("strType"+cnt).value;
        	var modeoftravel = document.getElementById("modeoftravel"+cnt).value;
        	if(strType == 'Conveyance Bill'){
        		if(document.getElementById("local_1_"+modeoftravel)){
    	    		var limit = document.getElementById("local_1_"+modeoftravel).value;
    				var kmpd=document.getElementById("kmpd"+cnt).value;
    			    var ratepkm=document.getElementById("ratepkm"+cnt).value;
    			    var noofdays=document.getElementById("noofdays"+cnt).value;
    				if(parseFloat(ratepkm) <= parseFloat(limit)){
    					/* var totalAmt=kmpd*ratepkm*noofdays;
    	    			document.getElementById("strAmount"+cnt).value=totalAmt; */
    					if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
    						var totalAmt=kmpd * ratepkm * noofdays;
    						document.getElementById("strAmount"+cnt).value=totalAmt;
    				    }
    	    		} else {
    	    			alert('Enter proper rate/km upto '+limit);
    	    			document.getElementById("kmpd"+cnt).value = '';
    	    		    document.getElementById("ratepkm"+cnt).value = '';
    	    		    document.getElementById("noofdays"+cnt).value = '';
    	    			document.getElementById("strAmount"+cnt).value='';
    	    		}	
        		} else {
            		var kmpd=document.getElementById("kmpd"+cnt).value;
        		    var ratepkm=document.getElementById("ratepkm"+cnt).value;
        		    var noofdays=document.getElementById("noofdays"+cnt).value;
        			/* var totalAmt=kmpd * ratepkm * noofdays;
        			document.getElementById("strAmount"+cnt).value=totalAmt; */
        		    if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
        				var totalAmt=kmpd * ratepkm * noofdays;
        				document.getElementById("strAmount"+cnt).value=totalAmt;
        		    }
            	}
        	} else if(strType =='Food Expenses'){ 
        		if(document.getElementById("local_2")){
    	    		var limit = document.getElementById("local_2").value;
    	    		var strAmount=document.getElementById("strAmount"+cnt).value;
    	    		if(parseFloat(strAmount) <= parseFloat(limit)){
    	    			document.getElementById("strAmount"+cnt).value=strAmount;
    	    		} else {
    	    			alert('Enter proper amount upto '+limit);
    	    			document.getElementById("kmpd"+cnt).value = '';
    	    		    document.getElementById("ratepkm"+cnt).value = '';
    	    		    document.getElementById("noofdays"+cnt).value = '';
    	    			document.getElementById("strAmount"+cnt).value='';
    	    		}
        		} /* else {
            		var kmpd=document.getElementById("kmpd"+cnt).value;
        		    var ratepkm=document.getElementById("ratepkm"+cnt).value ;
        		    var noofdays=document.getElementById("noofdays"+cnt).value ;
        			var totalAmt=kmpd * ratepkm * noofdays;
        			document.getElementById("strAmount"+cnt).value=totalAmt;
            	} */
        	} else if(strType == 'Travel'){
        		if(document.getElementById("local_3_"+modeoftravel)){
    	    		var limit = document.getElementById("local_3_"+modeoftravel).value;
    				var kmpd=document.getElementById("kmpd"+cnt).value;
    			    var ratepkm=document.getElementById("ratepkm"+cnt).value;
    			    var noofdays=document.getElementById("noofdays"+cnt).value;
    				
    				if(parseFloat(ratepkm) <= parseFloat(limit)){
    					/* var totalAmt=kmpd*ratepkm*noofdays;
    	    			document.getElementById("strAmount"+cnt).value=totalAmt; */
    					if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
    						var totalAmt=kmpd * ratepkm * noofdays;
    						document.getElementById("strAmount"+cnt).value=totalAmt;
    				    }
    	    		} else {
    	    			alert('Enter proper rate/km upto '+limit);
    	    			document.getElementById("kmpd"+cnt).value = '';
    	    		    document.getElementById("ratepkm"+cnt).value = '';
    	    		    document.getElementById("noofdays"+cnt).value = '';
    	    			document.getElementById("strAmount"+cnt).value='';
    	    		}	
        		} else {
            		var kmpd=document.getElementById("kmpd"+cnt).value;
        		    var ratepkm=document.getElementById("ratepkm"+cnt).value;
        		    var noofdays=document.getElementById("noofdays"+cnt).value;
        			/* var totalAmt=kmpd * ratepkm * noofdays;
        			document.getElementById("strAmount"+cnt).value=totalAmt; */
        		    if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
        				var totalAmt=kmpd * ratepkm * noofdays;
        				document.getElementById("strAmount"+cnt).value=totalAmt;
        		    }
            	}
        	} else {
        		var kmpd=document.getElementById("kmpd"+cnt).value;
    		    var ratepkm=document.getElementById("ratepkm"+cnt).value ;
    		    var noofdays=document.getElementById("noofdays"+cnt).value ;
    			/* var totalAmt=kmpd * ratepkm * noofdays;
    			document.getElementById("strAmount"+cnt).value=totalAmt; */
    		    if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
    				var totalAmt=kmpd * ratepkm * noofdays;
    				document.getElementById("strAmount"+cnt).value=totalAmt;
    		    }
        	}
        } else if(reimType == 'M'){
        	if(document.getElementById("mobile")){
        		var limit = document.getElementById("mobile").value;
    			var totalAmt=document.getElementById("strAmount"+cnt).value;
    			if(parseFloat(totalAmt) <= parseFloat(limit)){
        			document.getElementById("strAmount"+cnt).value=totalAmt;
        		} else {
        			alert('Enter proper amount upto '+limit);
        			document.getElementById("strAmount"+cnt).value='';
        		}	
    		}
        } else {
        	var kmpd=document.getElementById("kmpd"+cnt).value;
    	    var ratepkm=document.getElementById("ratepkm"+cnt).value;
    	    var noofdays=document.getElementById("noofdays"+cnt).value;
    	    
    	    if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))){
    			var totalAmt=kmpd * ratepkm * noofdays;
    			document.getElementById("strAmount"+cnt).value=totalAmt;
    	    }		
        }
    }
    
    function clearAmt(cnt){
    	document.getElementById("kmpd"+cnt).value = '';
        document.getElementById("ratepkm"+cnt).value = '';
        document.getElementById("noofdays"+cnt).value = '';
    	document.getElementById("strAmount"+cnt).value='';
    }
    
    var prevcnt=1;
    function addRow() {
    	prevcnt++;
    
    	var sbPaycycleList = '<%=(String) request.getAttribute("sbPaycycleList") %>';
    	var reimType = '<%=(String) request.getAttribute("reimType") %>';
    	
    	var sbClientList = '<%=(String) request.getAttribute("sbClientList") %>';
    	var chkP = '<%=(!"P".equalsIgnoreCase(reimType))?"display:none":"" %>';
    	var sbProjectList = '<%=(String) request.getAttribute("sbProjectList") %>';
    	var chkT = '<%=(!"T".equalsIgnoreCase(reimType))?"display:none":"" %>';
    	var sbTravelPlanList = '<%=(String) request.getAttribute("sbTravelPlanList") %>';
    	var sbTypeList = '<%=(String) request.getAttribute("sbTypeList") %>';
    	var chkO = '<%=(!"L".equalsIgnoreCase(reimType))?"display:none":"" %>';
    	var chkM = '<%=(!"M".equalsIgnoreCase(reimType))?"display:none":"" %>';
    	var sbModeoftravelList = '<%=(String) request.getAttribute("sbModeoftravelList") %>';
    	var sbLodgingTypeList = '<%=(String) request.getAttribute("sbLodgingTypeList") %>';
    	var sbLocalConveyanceTranTypeList = '<%=(String) request.getAttribute("sbLocalConveyanceTranTypeList") %>';
       
    	
        var totalCount = document.getElementById("count").value;
        //alert("totalCount====>"+totalCount);
        var val=(parseInt(totalCount));
        //alert("val====>"+val)
        var table = document.getElementById("reimTableId");
        var rowCount = table.rows.length; 
        //alert("rowCount  "+rowCount);
        var rowid=(parseInt(rowCount)+1);
        //alert("rowid====>"+rowid);
        //alert("val====>"+val);
        var row = table.insertRow(rowCount);
        //alert("val 111====>"+val);
        row.id=""+rowid;
       	//alert("rowCount====>"+rowCount);
        //alert("new row  "+table.rows.length);
        row.className ="bb";
    	
        var cell0 = row.insertCell(0);
        var cell1 = row.insertCell(1);
        var cell2 = row.insertCell(2);
        var cell3 = row.insertCell(3);	 
    	var cell4 = row.insertCell(4);
    	var cell5 = row.insertCell(5);
    	var cell6 = row.insertCell(6);
    	var cell7 = row.insertCell(7);
    	var cell8 = row.insertCell(8);
    	var cell9 = row.insertCell(9);
    	var cell10 = row.insertCell(10);
    	var cell11 = row.insertCell(11);
    	var cell12 = row.insertCell(12);
    	
        var typeC='typeC'+prevcnt;
        cell2.setAttribute('id',typeC);
        cell2.setAttribute('style',chkP);
       
    	var typeP='typeP'+prevcnt;
    	cell3.setAttribute('id',typeP);
    	cell3.setAttribute('style',chkP);
    	
    	var typeT='typeT'+prevcnt;
    	cell4.setAttribute('id',typeT);
    	cell4.setAttribute('style',chkT);
    	
    	var typeO='typeO'+prevcnt;
    	cell5.setAttribute('id',typeO);
    	cell5.setAttribute('style',chkO);
    	
    	var isbillsecond='isbillsecond'+prevcnt;
    	cell11.setAttribute('id',isbillsecond);
    	cell11.setAttribute('style',chkP);
    	
    	cell0.vAlign = 'top';
    	cell1.vAlign = 'top';
    	cell2.vAlign = 'top';
    	cell3.vAlign = 'top';
    	cell4.vAlign = 'top';
    	cell5.vAlign = 'top';
    	cell6.vAlign = 'top';
    	cell7.vAlign = 'top';
    	cell8.vAlign = 'top';
    	cell9.vAlign = 'top';
    	cell10.vAlign = 'top';
    	cell11.vAlign = 'top';
    	cell12.vAlign = 'top';
    	
    	cell0.setAttribute('nowrap','nowrap');
    	cell1.setAttribute('nowrap','nowrap');
    	cell2.setAttribute('nowrap','nowrap');
    	cell3.setAttribute('nowrap','nowrap');
    	cell4.setAttribute('nowrap','nowrap');
    	cell5.setAttribute('nowrap','nowrap');
    	cell6.setAttribute('nowrap','nowrap');
    	cell7.setAttribute('nowrap','nowrap');
    	cell8.setAttribute('nowrap','nowrap');
    	cell9.setAttribute('nowrap','nowrap');
    	cell10.setAttribute('nowrap','nowrap');
    	cell11.setAttribute('nowrap','nowrap');
    	cell12.setAttribute('nowrap','nowrap');
    	
    	cell0.innerHTML = "<a href='javascript:void(0)' onclick='deleteRow(this)' class='remove-font'></a>"+
        		"<a href='javascript:void(0)' onclick='addRow();' class='add-font'></a>";
        cell1.innerHTML = "<input type='text' name='strFromDate"+prevcnt+"' id='strFromDate"+prevcnt+"' style=\"width: 100px !important;\" class=\"validateRequired form-control\"/>";
        cell2.innerHTML = "<select name='strClient"+prevcnt+"' id='strClient"+prevcnt+"' onchange=\"getContent('typeP"+prevcnt+"', 'GetProjectClientTask.action?client_id='+this.value+'&type=R&cnt='+prevcnt);\" class='validateRequired form-control'>"+
    			"		<option value=''>Select Client</option>"+
    			""+sbClientList+
    			"	</select>";
        cell3.innerHTML = "<select theme='simple' name='strProject"+prevcnt+"' id='strProject"+prevcnt+"' class='validateRequired form-control'>"+
    			"		<option value=''>Select Project</option>"+
    			""+sbProjectList+
    			"	</select>";
        cell4.innerHTML = "<select name='strTravelPlan"+prevcnt+"' id='strTravelPlan"+prevcnt+"' class='validateRequired form-control' onchange=\"getTravelPlanDetails(this.value,"+prevcnt+");\">"+
    			"		<option value=''>Select Travel Plan</option>"+
    			""+sbTravelPlanList+
    			"	</select> <input type='hidden' name='strTravelPlanDays"+prevcnt+"' id='strTravelPlanDays"+prevcnt+"' value='0'/>";
        cell5.innerHTML = "<select name='strType"+prevcnt+"' id='strType"+prevcnt+"' class='validateRequired form-control' onchange='othervalue(this.value,"+prevcnt+");'>"+
    			"		<option value=''>Select Type</option>"+
    			""+sbTypeList+
    			"	</select>";
        cell6.innerHTML = "<input type='text' name='noofperson"+prevcnt+"' id='noofperson"+prevcnt+"' class='validateRequired form-control' />";
        cell7.innerHTML = "<input type='text' name='strVendor"+prevcnt+"' id='strVendor"+prevcnt+"'/>";
   	 	cell8.innerHTML = "<input type='hidden' name='receiptNoCount"+prevcnt+"' id='receiptNoCount"+prevcnt+"' value='1'/>"+
    			"<input type='text' name='strReceiptNo"+prevcnt+"' id='strReceiptNo"+prevcnt+"'/>"+
    			"<a href='javascript:void(0)' onclick='addReceiptNo("+prevcnt+")' class='add-font'></a>"+
	 			"<div id='divReceiptNo"+prevcnt+"'></div>";		
        cell9.innerHTML = "<table class=\"table\" id='innerTable"+prevcnt+"' cellpadding=\"0\" cellspacing=\"0\">"+
        		"<tr id='trModeTravel"+prevcnt+"' style=\"display: none;\">"+
        		"<td class=\"alignRight\" style=\"border: 0px none;\">Mode of travel:<sup>*</sup></td>"+
        		"<td style=\"border: 0px none;\">"+
        		"<select name='modeoftravel"+prevcnt+"' id='modeoftravel"+prevcnt+"' class='validateRequired form-control' onchange=\"clearAmt("+prevcnt+");\">"+
        		"		<option value=''>Select Mode</option>"+
        		""+sbModeoftravelList+
        		"	</select>"+
        		"</td>"+
        		"</tr>"+
        		"<tr id='trPlaceFrom"+prevcnt+"' style=\"display: none;\">"+
        		"<td class=\"alignRight\" style=\"border: 0px none;\">Place From:<sup>*</sup></td>"+
        		"<td style=\"border: 0px none;\">"+
        		"<input type='text' name='placefrom"+prevcnt+"' id='placefrom"+prevcnt+"' class=\"validateRequired form-control\" />"+
        		"</td></tr>"+
        		"<tr id='trPlaceTo"+prevcnt+"' style=\"display: none;\">"+
        		"<td class=\"alignRight\" style=\"border: 0px none;\">Place To:<sup>*</sup></td>"+
        		"<td style=\"border: 0px none;\"><input type='text' name='placeto"+prevcnt+"' id='placeto"+prevcnt+"' class=\"validateRequired form-control\" /></td>"+
        		"</tr>"+
        		"<tr id='trNodays"+prevcnt+"' style=\"display: none;\">"+
        		"<td class=\"alignRight\" style=\"border: 0px none;\">No of Days:<sup>*</sup></td>"+
        		"<td style=\"border: 0px none;\">"+
        		"<input type='text' name='noofdays"+prevcnt+"' id='noofdays"+prevcnt+"' class=\"validateRequired form-control\" onkeyup=\"setamount("+prevcnt+");\" onkeypress=\"return isNumberKey(event)\"/></td>"+
        		"</tr>"+
        		"<tr id='trTotalKM"+prevcnt+"' style=\"display: none;\">"+
        		"<td class=\"alignRight\" style=\"border: 0px none;\">Total KM:</td>"+
        		"<td style=\"border: 0px none;\">"+
        		"<input type='text' name='kmpd"+prevcnt+"' id='kmpd"+prevcnt+"' style=\"width:70px !important;\" onkeyup=\"setamount("+prevcnt+");\" onkeypress=\"return isNumberKey(event)\"/>"+
        		"</td></tr>"+
        		"<tr id='trRateKM"+prevcnt+"' style=\"display: none;\">"+
        		"<td class=\"alignRight\" style=\"border: 0px none;\">Rate/KM:</td>"+
        		"<td style=\"border: 0px none;\"><input type='text' name='ratepkm"+prevcnt+"' id='ratepkm"+prevcnt+"' style=\"width:50px !important;\" onkeyup=\"setamount("+prevcnt+");\" onkeypress=\"return isNumberKey(event)\"/>"+
        		"</td></tr>"+
        		
        		"<tr id=\"trTransType"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Transportation Type:<sup>*</sup></td>"+
    			"<td colspan=\"3\" style=\"border: 0px none;\">"+
    			"	<select name='transportType"+prevcnt+"' id='transportType"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportType("+prevcnt+");\">"+
        		"		<option value=''>Select Transport Type</option>"+
        		"		<option value='1'>Train</option>"+
        		"		<option value='2'>Bus</option>"+
        		"		<option value='3'>Flight</option>"+
        		"		<option value='4'>Car</option>"+
        		"	</select>"+	
    			"</td></tr>"+ 
    			
    			"<tr id=\"trTrain"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Train:<sup>*</sup></td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<select name='trainType"+prevcnt+"' id='trainType"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportAmount("+prevcnt+");\">"+
        		"		<option value='1'>3 Tier</option>"+
        		"		<option value='2'>Chair Car</option>"+
        		"		<option value='3'>AC 3 Tier</option>"+
        		"		<option value='4'>AC 2 Tier</option>"+
        		"		<option value='5'>AC 1st Class</option>"+
        		"	</select>"+		 
    			"</td></tr>"+
    			
    			"<tr id=\"trBus"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Bus:<sup>*</sup></td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<select name='busType"+prevcnt+"' id='busType"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportAmount("+prevcnt+");\">"+
        		"		<option value='1'>A/c Bus</option>"+
        		"		<option value='2'>Non- A/c Bus</option>"+
        		"	</select>"+		
    			"</td></tr>"+
    			
    			"<tr id=\"trFlight"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Flight:<sup>*</sup></td>"+ 
    			"<td style=\"border: 0px none;\">"+
    			"	<select name='flightType"+prevcnt+"' id='flightType"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportAmount("+prevcnt+");\">"+
        		"		<option value='1'>Economy Class</option>"+
        		"		<option value='2'>Business Class</option>"+
        		"	</select>"+		
    			"</td></tr>"+
    			
    			"<tr id=\"trCar"+prevcnt+"\" style=\"display: none;\">"+ 
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Car:<sup>*</sup></td>"+
    			"<td colspan=\"3\" style=\"border: 0px none;\">"+
    			"	<select name='carType"+prevcnt+"' id='carType"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportAmount("+prevcnt+");\">"+
        		"		<option value='1'>Cab</option>"+
        		"		<option value='2'>Self Owned</option>"+
        		"	</select>"+		
    			"</td></tr>"+
    			
    			"<tr id=\"trTransAmount"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Transport Amount:<sup>*</sup></td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<input type='text' name=\"strTransAmount"+prevcnt+"\" id=\"strTransAmount"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" class=\"validateRequired form-control\" onkeyup=\"checkTransportAmount("+prevcnt+");\" onkeypress=\"return isNumberKey(event);\"/>"+
    			"</td></tr>"+
    		
    			"<tr id=\"trLodgingType"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Lodging Type:</td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<select name=\"lodgingType"+prevcnt+"\" id=\"lodgingType"+prevcnt+"\" onchange=\"checkLodgingAmount("+prevcnt+");\">"+
    			"		<option value=''>Select Lodging Type</option>"+
    			""+sbLodgingTypeList+
        		"	</select>"+			
    			"</td></tr>"+
    		
    			"<tr id=\"trLodgingAmount"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Lodging Amount:</td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<input type='text' name=\"strLodgingAmount"+prevcnt+"\" id=\"strLodgingAmount"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onkeyup=\"checkLodgingAmount("+prevcnt+");\" onkeypress=\"return isNumberKey(event);\"/>"+
    			"</td></tr>"+
    		
    			"<tr id=\"trLocalConveyanceType"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Local Conveyance Type:<sup>*</sup></td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<select name=\"localConveyanceTranType"+prevcnt+"\" id=\"localConveyanceTranType"+prevcnt+"\" onchange=\"checkLocalConveyanceAmount("+prevcnt+");\" class=\"validateRequired form-control\">"+
    			"		<option value=''>Select Mode</option>"+
    			""+sbLocalConveyanceTranTypeList+
        		"	</select>"+		
    			"</td></tr>"+
    		
    			"<tr id=\"trLocalConveyanceRate"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Total KM:</td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<input type='text' name=\"localConveyanceKM"+prevcnt+"\" id=\"localConveyanceKM"+prevcnt+"\" style=\"width:70px !important; text-align: right;\" onkeyup=\"checkLocalConveyanceAmount("+prevcnt+");\" onkeypress=\"return isNumberKey(event)\"/>&nbsp;"+	
    			"	Rate/KM:&nbsp;<input type='text' name=\"localConveyanceRate"+prevcnt+"\" id=\"localConveyanceRate"+prevcnt+"\" style=\"width:50px !important; text-align: right;\" onkeyup=\"checkLocalConveyanceAmount("+prevcnt+");\" onkeypress=\"return isNumberKey(event)\"/>"+
    			"</td></tr>"+
    		
    			"<tr id=\"trLocalConveyanceAmount"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Local Conveyance Amount:</td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<input type='text' name=\"strLocalConveyanceAmount"+prevcnt+"\" id=\"strLocalConveyanceAmount"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onkeypress=\"return isNumberKey(event);\" onkeyup=\"sumTotalTravelAmt("+prevcnt+");\"/>"+
    			"</td></tr>"+
    		
    			"<tr id=\"trFoodBeverageAmount"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Food &amp; Beverage:</td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<input type='text' name=\"strFoodBeverageAmount"+prevcnt+"\" id=\"strFoodBeverageAmount"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onkeyup=\"checkFoodBeverageAmount("+prevcnt+");\" onkeypress=\"return isNumberKey(event);\"/>"+
    			"</td></tr>"+
    		
    			"<tr id=\"trLaundryAmount"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Laundry:</td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<input type='text' name=\"strLaundryAmount"+prevcnt+"\" id=\"strLaundryAmount"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onkeyup=\"checkLaundryAmount("+prevcnt+");\" onkeypress=\"return isNumberKey(event);\"/>"+
    			"</td></tr>"+
    		
    			"<tr id=\"trSundryAmount"+prevcnt+"\" style=\"display: none;\">"+
    			"<td class=\"alignRight\" style=\"border: 0px none;\">Sundry:</td>"+
    			"<td style=\"border: 0px none;\">"+
    			"	<input type='text' name=\"strSundryAmount"+prevcnt+"\" id=\"strSundryAmount"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onkeyup=\"checkSundryAmount("+prevcnt+");\" onkeypress=\"return isNumberKey(event);\"/>"+
    			"</td></tr>"+
        		
        		"<tr><td class=\"alignRight\" style=\"border: 0px none; display: none;\" id='tdAmt"+prevcnt+"'>Amount:<sup>*</sup></td>"+
        		"<td style=\"border: 0px none;\"><input type='text' name='strAmount"+prevcnt+"' id='strAmount"+prevcnt+"'  onchange=\"setamount("+prevcnt+");\" class=\"validateRequired form-control\" style=\"width: 81px !important; text-align: right;\"/></td>"+
        		"</tr>"+
        		"</table>";
        cell10.innerHTML = "<textarea rows='2' cols='25' name='strPurpose"+prevcnt+"' id='strPurpose"+prevcnt+"'></textarea>";
        cell11.innerHTML = "<input type='checkbox' name='isbillable"+prevcnt+"' id='isbillable"+prevcnt+"' value='true' checked>";
        cell12.innerHTML = "<input type='hidden' name='fileCount"+prevcnt+"' id='fileCount"+prevcnt+"' value='1'/>"+
    	 "		<input type='file' name='strDocument"+prevcnt+"' id='strDocument"+prevcnt+"'/>"+
    	 "		<a href='javascript:void(0)' onclick='addDoc("+prevcnt+")' class='add-font'></a>"+
    	 "		<div id='div_documents"+prevcnt+"'></div>";
        
    	document.getElementById("count").value=rowid;
    	
    	$("#strFromDate"+prevcnt).datepicker({format: 'dd/mm/yyyy'});
    	
    	//othervalue('',prevcnt);
    	showType(reimType,prevcnt);
    	
    	document.getElementById(""+rowid).style.borderBottom = "1px solid black;"; 
        
    }
    
    function deleteRow(trIndex) {
    	var row = trIndex.parentNode.parentNode;
    	row.parentNode.removeChild(row);
    }
    
    function checkTransportType(cnt){
    	//alert('checkTransportType cnt==>'+cnt);
    	var transportType = document.getElementById("transportType"+cnt).value;
    	//alert('transportType==>'+transportType);
    	if(parseInt(transportType) == 1){
    		//alert('checkTransportType 1');
    		document.getElementById("trTrain"+cnt).style.display = 'table-row';
    		document.getElementById("trBus"+cnt).style.display = 'none';
    		document.getElementById("trFlight"+cnt).style.display = 'none';
    		document.getElementById("trCar"+cnt).style.display = 'none';
    	} else if(parseInt(transportType) == 2){
    		//alert('checkTransportType 2');
    		document.getElementById("trTrain"+cnt).style.display = 'none';
    		document.getElementById("trBus"+cnt).style.display = 'table-row';
    		document.getElementById("trFlight"+cnt).style.display = 'none';
    		document.getElementById("trCar"+cnt).style.display = 'none';
    	} else if(parseInt(transportType) == 3){
    		//alert('checkTransportType 3');
    		document.getElementById("trTrain"+cnt).style.display = 'none';
    		document.getElementById("trBus"+cnt).style.display = 'none';
    		document.getElementById("trFlight"+cnt).style.display = 'table-row';
    		document.getElementById("trCar"+cnt).style.display = 'none';
    	} else if(parseInt(transportType) == 4){
    		//alert('checkTransportType 4');
    		document.getElementById("trTrain"+cnt).style.display = 'none';
    		document.getElementById("trBus"+cnt).style.display = 'none';
    		document.getElementById("trFlight"+cnt).style.display = 'none';
    		document.getElementById("trCar"+cnt).style.display = 'table-row';
    	} else {
    		//alert('checkTransportType 5');
    		document.getElementById("trTrain"+cnt).style.display = 'none';
    		document.getElementById("trBus"+cnt).style.display = 'none';
    		document.getElementById("trFlight"+cnt).style.display = 'none';
    		document.getElementById("trCar"+cnt).style.display = 'none';
    	}
    	
    	checkTransportAmount(cnt);
    }
    
    function checkTransportAmount(cnt){
    	//alert('checkTransportAmount cnt==>'+cnt);
    	var strTransAmount = document.getElementById("strTransAmount"+cnt).value;
    	//alert('strTransAmount==>'+strTransAmount);
    	var hiddenTravelType = document.getElementById("hiddenTravelType").value;
    	//alert('hiddenTravelType==>'+hiddenTravelType);
    	var transportType = document.getElementById("transportType"+cnt).value;
    	//alert('transportType==>'+transportType);
    	if(parseInt(transportType) == 1 && document.getElementById("hiddenTrainType")){
    		var hiddenTrainType = document.getElementById("hiddenTrainType").value;
    		var trainType = document.getElementById("trainType"+cnt).value;
    		if(parseInt(hiddenTrainType) == parseInt(trainType)){
    			var limit = document.getElementById("train_"+hiddenTravelType+"_"+hiddenTrainType).value;
    			if(parseFloat(strTransAmount) <= parseFloat(limit)){
    				document.getElementById("strTransAmount"+cnt).value=strTransAmount;
    			} else {
    				alert('Enter proper amount upto '+limit);
    				document.getElementById("strTransAmount"+cnt).value='';    			
    			}	
    		}
    		sumTotalTravelAmt(cnt);
    	} else if(parseInt(transportType) == 2 && document.getElementById("hiddenBusType")){
    		var hiddenBusType = document.getElementById("hiddenBusType").value;
    		var busType = document.getElementById("busType"+cnt).value;
    		var limit = document.getElementById("bus_"+hiddenTravelType+"_"+hiddenBusType).value;
    		if(parseInt(hiddenBusType) == parseInt(busType)){
    			if(parseFloat(strTransAmount) <= parseFloat(limit)){
    				document.getElementById("strTransAmount"+cnt).value=strTransAmount;
    			} else {
    				alert('Enter proper amount upto '+limit);
    				document.getElementById("strTransAmount"+cnt).value='';    			
    			}	
    		}
    		sumTotalTravelAmt(cnt);
    	} else if(parseInt(transportType) == 3 && document.getElementById("hiddenFlightType")){
    		var hiddenFlightType = document.getElementById("hiddenFlightType").value;
    		var flightType = document.getElementById("flightType"+cnt).value;
    		var limit = document.getElementById("flight_"+hiddenTravelType+"_"+hiddenFlightType).value;
    		if(parseInt(hiddenFlightType) == parseInt(flightType)){
    			if(parseFloat(strTransAmount) <= parseFloat(limit)){
    				document.getElementById("strTransAmount"+cnt).value=strTransAmount;
    			} else {
    				alert('Enter proper amount upto '+limit);
    				document.getElementById("strTransAmount"+cnt).value='';    			
    			}	
    		}
    		sumTotalTravelAmt(cnt);
    	} else if(parseInt(transportType) == 4 && document.getElementById("hiddenCarType")){
    		var hiddenCarType = document.getElementById("hiddenCarType").value;
    		var carType = document.getElementById("carType"+cnt).value;
    		var limit = document.getElementById("car_"+hiddenTravelType+"_"+hiddenCarType).value;
    		if(parseInt(hiddenCarType) == parseInt(carType)){
    			if(parseFloat(strTransAmount) <= parseFloat(limit)){
    				document.getElementById("strTransAmount"+cnt).value=strTransAmount;
    			} else {
    				alert('Enter proper amount upto '+limit);
    				document.getElementById("strTransAmount"+cnt).value='';    			
    			}	
    		}
    		sumTotalTravelAmt(cnt);
    	} 	
    }
    
    function checkLodgingAmount(cnt){
    	var lodgingType = document.getElementById("lodgingType"+cnt).value;
    	if(parseInt(lodgingType) == 9){
    		var lodgingLimit = document.getElementById("lodgingLimit").value;
    		var strLodgingAmount = document.getElementById("strLodgingAmount"+cnt).value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays"+cnt).value;
    		
    		var totalLodingLimit = parseFloat(lodgingLimit) * parseFloat(strTravelPlanDays);
    		if(parseFloat(strLodgingAmount) <= parseFloat(totalLodingLimit)){
    			document.getElementById("strLodgingAmount"+cnt).value=strLodgingAmount;
    		} else {
    			alert('Enter proper amount upto '+lodgingLimit);
    			document.getElementById("strLodgingAmount"+cnt).value='';    			
    		}	 
    	}	
    	sumTotalTravelAmt(cnt);
    }
    
    function checkLocalConveyanceAmount(cnt){
    	var localConveyanceTranType = document.getElementById("localConveyanceTranType"+cnt).value;
    	if(document.getElementById("localConveyanceLimit_"+localConveyanceTranType)){
    		var localConveyanceLimit = document.getElementById("localConveyanceLimit_"+localConveyanceTranType).value;
    		var localConveyanceRate = document.getElementById("localConveyanceRate"+cnt).value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays"+cnt).value;
    		
    		var totalLocalConveyanceLimit = parseFloat(localConveyanceLimit);
    		if(parseFloat(localConveyanceRate) <= parseFloat(totalLocalConveyanceLimit)){
    			document.getElementById("localConveyanceRate"+cnt).value=localConveyanceRate;
    		} else {
    			alert('Enter proper km upto '+totalLocalConveyanceLimit);
    			document.getElementById("localConveyanceRate"+cnt).value='';    			
    		}	 
    	}
    	calLocalConveyanceAmount(cnt);
    }
    
    function calLocalConveyanceAmount(cnt){
    	var localConveyanceKM = document.getElementById("localConveyanceKM"+cnt).value;
        var localConveyanceRate=document.getElementById("localConveyanceRate"+cnt).value ;
    	var totalAmt=localConveyanceKM * localConveyanceRate;
    	document.getElementById("strLocalConveyanceAmount"+cnt).value=totalAmt;
    	
    	sumTotalTravelAmt(cnt);
    }
    
    function checkFoodBeverageAmount(cnt){
    	if(document.getElementById("foodBeverageLimit")){
    		var foodBeverageLimit = document.getElementById("foodBeverageLimit").value;
    		var strFoodBeverageAmount = document.getElementById("strFoodBeverageAmount"+cnt).value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays"+cnt).value;
    		
    		var totalFoodBeverageLimit = parseFloat(foodBeverageLimit) * parseFloat(strTravelPlanDays);
    		if(parseFloat(strFoodBeverageAmount) <= parseFloat(totalFoodBeverageLimit)){
    			document.getElementById("strFoodBeverageAmount"+cnt).value=strFoodBeverageAmount;
    		} else {
    			alert('Enter proper amount upto '+totalFoodBeverageLimit);
    			document.getElementById("strFoodBeverageAmount"+cnt).value='';    			
    		}
    	}
    	sumTotalTravelAmt(cnt);
    }
    
    function checkLaundryAmount(cnt){
    	if(document.getElementById("laundryLimit")){
    		var laundryLimit = document.getElementById("laundryLimit").value;
    		var strLaundryAmount = document.getElementById("strLaundryAmount"+cnt).value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays"+cnt).value;
    		
    		var totalLaundryLimit= parseFloat(laundryLimit) * parseFloat(strTravelPlanDays);
    		if(parseFloat(strLaundryAmount) <= parseFloat(totalLaundryLimit)){
    			document.getElementById("strLaundryAmount"+cnt).value=strLaundryAmount;
    		} else {
    			alert('Enter proper amount upto '+totalLaundryLimit);
    			document.getElementById("strLaundryAmount"+cnt).value='';    			
    		}
    	}
    	sumTotalTravelAmt(cnt);
    }
    
    function checkSundryAmount(cnt){
    	if(document.getElementById("sundryLimit")){
    		var sundryLimit = document.getElementById("sundryLimit").value;
    		var strSundryAmount = document.getElementById("strSundryAmount"+cnt).value;
    		var strTravelPlanDays = document.getElementById("strTravelPlanDays"+cnt).value;
    		
    		var totalSundryLimit= parseFloat(sundryLimit) * parseFloat(strTravelPlanDays);
    		if(parseFloat(strSundryAmount) <= parseFloat(totalSundryLimit)){
    			document.getElementById("strSundryAmount"+cnt).value=strSundryAmount;
    		} else {
    			alert('Enter proper amount upto '+totalSundryLimit);
    			document.getElementById("strSundryAmount"+cnt).value='';    			
    		}
    	}
    	sumTotalTravelAmt(cnt);
    }
    
    function sumTotalTravelAmt(cnt){
    	var strTransAmount = document.getElementById("strTransAmount"+cnt).value;
    	var strLodgingAmount = document.getElementById("strLodgingAmount"+cnt).value;
    	var strLocalConveyanceAmount = document.getElementById("strLocalConveyanceAmount"+cnt).value;
    	var strFoodBeverageAmount = document.getElementById("strFoodBeverageAmount"+cnt).value;
    	var strLaundryAmount = document.getElementById("strLaundryAmount"+cnt).value;
    	var strSundryAmount = document.getElementById("strSundryAmount"+cnt).value;
    	var totalAmt = 0;
    	if(isInt(strTransAmount) || isFloat(strTransAmount)){
    		totalAmt += parseFloat(strTransAmount);
    	}
    	if(isInt(strLodgingAmount) || isFloat(strLodgingAmount)){
    		totalAmt += parseFloat(strLodgingAmount);
    	}
    	if(isInt(strLocalConveyanceAmount) || isFloat(strLocalConveyanceAmount)){
    		totalAmt += parseFloat(strLocalConveyanceAmount);
    	}
    	if(isInt(strFoodBeverageAmount) || isFloat(strFoodBeverageAmount)){
    		totalAmt += parseFloat(strFoodBeverageAmount);
    	}
    	if(isInt(strLaundryAmount) || isFloat(strLaundryAmount)){
    		totalAmt += parseFloat(strLaundryAmount);
    	}
    	if(isInt(strSundryAmount) || isFloat(strSundryAmount)){
    		totalAmt += parseFloat(strSundryAmount);
    	}
    	document.getElementById("strAmount"+cnt).value = parseFloat(totalAmt); 
    }
    
    function isInt(n){
        return n != "" && !isNaN(n) && Math.round(n) == n;
    }
    function isFloat(n){
        return n != "" && !isNaN(n) && Math.round(n) != n;
    }
    
    function getTravelPlanDetails(val,cnt){
    	document.getElementById("strTravelPlanDays"+cnt).value='';
    	
    	var xmlhttp = GetXmlHttpObject();
    	if (xmlhttp == null) {
    		alert("Browser does not support HTTP Request");
    		return;
    	} else {
    		var xhr = $.ajax({
    			url : 'GetTravelPlanDetails.action?empId='+document.getElementById('strSessionEmpId').value+'&travelId='+val,
    			cache : false,
    			success : function(data) {
    				document.getElementById("strTravelPlanDays"+cnt).value=data;
    			}
    		});
    
    	}
    }
</script>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
                    <div class="row row_without_margin">
                        <div class="col-lg-2 col-md-2 col-sm-6">
                            <% 
                                List<FillRimbursementType> reimTypeList = (List<FillRimbursementType>) request.getAttribute("reimbursementTypeList");
                                
                                for(int i=0; reimTypeList!=null && i<reimTypeList.size(); i++){
                                	FillRimbursementType rimbursementType = reimTypeList.get(i);
                                	String strChecked = (reimType!=null && reimType.equals(""+rimbursementType.getTypeId())) ? "checked" : "";
                                %>
                            <div>
                                <input type="radio" name="reimType" value="<%=rimbursementType.getTypeId()%>" onclick="window.location='BulkReimbursements.action?reimType=<%=""+rimbursementType.getTypeId() %>&paycycle=<%=(String) request.getAttribute("paycycle") %>';" <%=strChecked %>/><label><%=rimbursementType.getTypeName() %></label> &nbsp;
                            </div>
                            <% } %>
                        </div>
                        <div class="col-lg-10 col-md-10 col-sm-6" style="padding-left: 5px;padding-right: 5px;border: 2px solid rgb(238, 238, 238);">
                            <s:form id="formReimID" theme="simple" name="formReimID" action="BulkReimbursements" method="post">
                                <%if(reimType.equals("P")){ %>
                                <p style="margin-top: 10px;margin-bottom: 10px;">
                                    Select Paycycle: 
                                    <s:select name="paycycle" id="paycycle"listKey="paycycleId" listValue="paycycleName" list="paycycleList" 
                                        onchange="document.formReimID.submit();"/>
                                </p>
                                <%} %>
                                <s:hidden name="reimType"></s:hidden>
                            </s:form>
                            <s:form id="frm_MyReimbursements" theme="simple" name="frm_MyReimbursements" action="BulkReimbursements" enctype="multipart/form-data" method="post">
                                <s:hidden name="strId"/>
                                <input type="hidden" name="strSessionEmpId" id="strSessionEmpId" value="<%=(String)session.getAttribute(IConstants.EMPID) %>"/>
                                <input type="hidden" name="paycycle" id="paycycle" value="<%=(String)request.getAttribute("paycycle") %>"/>
                                <input type="hidden" name="reimbursementType" id="reimbursementType" value="<%=(String)request.getAttribute("reimType") %>"/>
                                <input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id %>"/>
                                <%
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
                                <div style="width: 100%; overflow-x: auto;">
                                    <%
                                        boolean isProject = (Boolean) request.getAttribute("isProject");
                                        if ((reimType.trim().equalsIgnoreCase("P") && isProject) || reimType.trim().equalsIgnoreCase("T") || reimType.trim().equalsIgnoreCase("L") || reimType.trim().equalsIgnoreCase("M")){ 
                                        %>
                                    <table id="reimTableId" class="table table-bordered">
                                        <tr id="1" class="bb">
                                            <th nowrap="nowrap" class="alignCenter">Add</th>
                                            <th nowrap="nowrap" class="alignRight" valign="top">Reimbursement Date</th>
                                            <th nowrap="nowrap" class="alignCenter" id="client1" <%=(!"P".equalsIgnoreCase(reimType))?"style=\"display:none\"":"" %>>Client<sup>*</sup></th>
                                            <th nowrap="nowrap" class="alignCenter" id="pro1" <%=(!"P".equalsIgnoreCase(reimType))?"style=\"display:none\"":"" %>>Project<sup>*</sup></th>
                                            <th nowrap="nowrap" class="alignCenter" id="trav1" <%=(!"T".equalsIgnoreCase(reimType))?"style=\"display:none\"":"" %>>Travel Plan<sup>*</sup></th>
                                            <th nowrap="nowrap" class="alignCenter" id="othr1" <%=(!"L".equalsIgnoreCase(reimType))?"style=\"display:none\"":"" %>>Type<sup>*</sup></th>
                                            <th nowrap="nowrap" class="alignRight" valign="top">Number of persons<sup>*</sup></th>
                                            <th nowrap="nowrap" class="alignRight" valign="top">Vendor</th>
                                            <th nowrap="nowrap" class="alignRight" valign="top">Receipt No</th>
                                            <th nowrap="nowrap" class="alignRight" valign="top">Amount<sup>*</sup></th>
                                            <th nowrap="nowrap" class="alignRight" valign="top">Purpose:</th>
                                            <th nowrap="nowrap" id="isbillfirst1" class="alignRight" valign="top">Chargeable to client</th>
                                            <th nowrap="nowrap" class="alignRight" valign="top" valign="top">Attach Document</th>
                                        </tr>
                                        <tr id="2">
                                            <td nowrap="nowrap" class="alignRight" valign="top"><a href="javascript:void(0)" onclick="addRow();" class="add-font"></a></td>
                                            <td nowrap="nowrap" valign="top">
                                                <s:textfield name="strFromDate1" id="strFromDate1" cssStyle="width: 100px !important;" cssClass="validateRequired form-control"/>
                                            </td>
                                            <td nowrap="nowrap" id="typeC1" valign="top" <%=(!"P".equalsIgnoreCase(reimType))?"style=\"display:none\"":"" %>>
                                                <s:select theme="simple" name="strClient1" id="strClient1" listKey="clientId" cssClass="validateRequired form-control" listValue="clientName" headerKey="" headerValue="Select Client"		
                                                    list="clientList" key="" required="true" onchange="getContent('typeP1', 'GetProjectClientTask.action?client_id='+this.value+'&type=R&cnt=1')"/>
                                            </td>
                                            <td nowrap="nowrap" id="typeP1" valign="top" <%=(!"P".equalsIgnoreCase(reimType))?"style=\"display:none\"":"" %>>
                                                <s:select theme="simple" name="strProject1" id="strProject1" listKey="projectID" cssClass="validateRequired form-control" listValue="projectName" headerKey="" headerValue="Select Project"		
                                                    list="projectList" key="" required="true" />
                                            </td>
                                            <td nowrap="nowrap" id="typeT1" valign="top" <%=(!"T".equalsIgnoreCase(reimType))?"style=\"display:none\"":"" %>>
                                                <s:select theme="simple" name="strTravelPlan1" id="strTravelPlan1" listKey="leaveId" cssClass="validateRequired form-control" listValue="planName" headerKey="" headerValue="Select Travel Plan"		
                                                    list="travelPlanList" key="" required="true" onchange="getTravelPlanDetails(this.value,1);"/>
                                                <s:hidden name="strTravelPlanDays1" id="strTravelPlanDays1"/>
                                            </td>
                                            <td nowrap="nowrap" id="typeO1" valign="top" valign="top" <%=(!"L".equalsIgnoreCase(reimType))?"style=\"display:none\"":"" %>>
                                                <s:select theme="simple" name="strType1" id="strType1" listKey="typeId" cssClass="validateRequired form-control" listValue="typeName" headerKey="" headerValue="Select Type"		
                                                    list="typeList" key="" required="true" onchange="othervalue(this.value,1);" />
                                            </td>
                                            <td nowrap="nowrap" valign="top">
                                                <s:textfield name="noofperson1" id="noofperson1" cssClass="validateRequired form-control" onkeypress="return isNumberKey(event);"/>
                                            </td>
                                            <td nowrap="nowrap" valign="top">
                                                <s:textfield name="strVendor1" id="strVendor1"/>
                                            </td>
                                            <td nowrap="nowrap" valign="top">
                                            	<input type="hidden" name="receiptNoCount1" id="receiptNoCount1" value="1"/>
				                                <s:textfield name="strReceiptNo1" id="strReceiptNo1"/>
				                                <a href="javascript:void(0)" onclick="addReceiptNo(1)" class="add-font"></a>
				                                <div id="divReceiptNo1"></div>
                                            </td>
                                            <td nowrap="nowrap" valign="top">
                                                <table class="table" cellpadding="0" cellspacing="0">
                                                    <tr id="trModeTravel1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Mode of travel:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:select theme="simple" name="modeoftravel1" id="modeoftravel1" listKey="typeId" cssClass="validateRequired form-control" listValue="typeName" headerKey="" headerValue="Select Mode"		
                                                                list="modeoftravelList" key="" required="true" onchange="clearAmt(1);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trPlaceFrom1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Place From:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="placefrom1" id="placefrom1" cssClass="validateRequired form-control" />
                                                        </td>
                                                    </tr>
                                                    <tr id="trPlaceTo1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Place To:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="placeto1" id="placeto1" cssClass="validateRequired form-control" />
                                                        </td>
                                                    </tr>
                                                    <tr id="trNodays1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">No of Days:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="noofdays1" id="noofdays1" cssClass="validateRequired form-control" onkeyup="setamount(1);" onkeypress="return isNumberKey(event)"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trTotalKM1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Total KM:</td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="kmpd1" id="kmpd1" cssStyle="width:70px !important;" onkeyup="setamount(1);" onkeypress="return isNumberKey(event)"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trRateKM1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Rate/KM:</td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="ratepkm1" id="ratepkm1" cssStyle="width:50px !important;" onkeyup="setamount(1);" onkeypress="return isNumberKey(event)"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trTransType1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Transportation Type:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:select theme="simple" name="transportType1" id="transportType1" list="#{'1':'Train','2':'Bus','3':'Flight','4':'Car'}" 
                                                                cssClass="validateRequired form-control" headerKey="" headerValue="Select Transport Type" onchange="checkTransportType(1);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trTrain1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Train:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:select theme="simple" name="trainType1" id="trainType1" list="#{'1':'3 Tier','2':'Chair Car','3':'AC 3 Tier','4':'AC 2 Tier','5':'AC 1st Class'}" 
                                                                onchange="checkTransportAmount();" cssClass="validateRequired form-control"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trBus1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Bus:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:select theme="simple" name="busType1" id="busType1" list="#{'1':'A/c Bus','2':'Non- A/c Bus'}" cssClass="validateRequired form-control" onchange="checkTransportAmount(1);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trFlight1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Flight:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:select theme="simple" name="flightType1" id="flightType1" list="#{'1':'Economy Class','2':'Business Class'}" cssClass="validateRequired form-control" onchange="checkTransportAmount(1);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trCar1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Car:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:select theme="simple" name="carType1" id="carType1" list="#{'1':'Cab','2':'Self Owned'}" cssClass="validateRequired form-control" onchange="checkTransportAmount(1);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trTransAmount1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Transport Amount:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="strTransAmount1" id="strTransAmount1" cssStyle="width: 81px !important; text-align: right;" cssClass="validateRequired form-control" onkeyup="checkTransportAmount(1);" onkeypress="return isNumberKey(event);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trLodgingType1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Lodging Type:</td>
                                                        <td style="border: 0px none;">
                                                            <s:select theme="simple" name="lodgingType1" id="lodgingType1" listKey="lodgingTypeId" 
                                                                listValue="lodgingTypeName" headerKey="" headerValue="Select Lodging Type" list="lodgingTypeList" key="" onchange="checkLodgingAmount(1);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trLodgingAmount1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Lodging Amount:</td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="strLodgingAmount1" id="strLodgingAmount1" cssStyle="width: 81px !important; text-align: right;" onkeyup="checkLodgingAmount(1);" onkeypress="return isNumberKey(event);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trLocalConveyanceType1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Local Conveyance Type:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:select theme="simple" name="localConveyanceTranType1" id="localConveyanceTranType1" listKey="typeId" listValue="typeName" cssClass="validateRequired" 
                                                                headerKey="" headerValue="Select Mode" list="localConveyanceTranTypeList" key="" required="true" onchange="checkLocalConveyanceAmount(1);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trLocalConveyanceRate1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Total KM:</td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="localConveyanceKM1" id="localConveyanceKM1" cssStyle="width:70px !important; text-align: right;" onkeyup="checkLocalConveyanceAmount(1);" onkeypress="return isNumberKey(event)"/>
                                                            &nbsp;
                                                            Rate/KM:&nbsp;
                                                            <s:textfield name="localConveyanceRate1" id="localConveyanceRate1" cssStyle="width:50px !important; text-align: right;" onkeyup="checkLocalConveyanceAmount(1);" onkeypress="return isNumberKey(event)"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trLocalConveyanceAmount1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Local Conveyance Amount:</td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="strLocalConveyanceAmount1" id="strLocalConveyanceAmount1" cssStyle="width: 81px !important; text-align: right;" onkeypress="return isNumberKey(event);" onkeyup="sumTotalTravelAmt(1);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trFoodBeverageAmount1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Food &amp; Beverage:</td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="strFoodBeverageAmount1" id="strFoodBeverageAmount1" cssStyle="width: 81px !important; text-align: right;" onkeyup="checkFoodBeverageAmount(1);" onkeypress="return isNumberKey(event);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trLaundryAmount1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Laundry:</td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="strLaundryAmount1" id="strLaundryAmount1" cssStyle="width: 81px !important; text-align: right;" onkeyup="checkLaundryAmount(1);" onkeypress="return isNumberKey(event);"/>
                                                        </td>
                                                    </tr>
                                                    <tr id="trSundryAmount1" style="display: none;">
                                                        <td class="alignRight" style="border: 0px none;">Sundry:</td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="strSundryAmount1" id="strSundryAmount1" cssStyle="width: 81px !important; text-align: right;" onkeyup="checkSundryAmount(1);" onkeypress="return isNumberKey(event);"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight" style="border: 0px none; display: none;" id="tdAmt1">Amount:<sup>*</sup></td>
                                                        <td style="border: 0px none;">
                                                            <s:textfield name="strAmount1" id="strAmount1" cssClass="validateRequired form-control" cssStyle="width: 81px !important; text-align: right;"  onchange="setamount(1);" onkeypress="return isNumberKey(event);"/>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                            <td nowrap="nowrap" valign="top">
                                                <s:textarea rows="2" cols="25" name="strPurpose1" id="strPurpose1"></s:textarea>
                                            </td>
                                            <td nowrap="nowrap" id="isbillsecond1" valign="top">
                                                <s:checkbox name="isbillable1" id="isbillable1" fieldValue="true"></s:checkbox>
                                            </td>
                                            <td nowrap="nowrap" valign="top">
                                                <input type="hidden" name="fileCount1" id="fileCount1" value="1"/>
                                                <div class="row row_without_margin">
													<div class="col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding">
														<s:file name="strDocument1" id="strDocument1"/>
													</div>
													<div class="col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding">
														<a href="javascript:void(0)" onclick="addDoc(1)" class="add-font"></a>
													</div>
												</div>
                                                <div id="div_documents1"></div>
                                            </td>
                                        </tr>
                                    </table>
                                    <%} else { %>
                                    <div class="nodata msg"><span>No Project assigned this paycyle</span></div>
                                    <%} %>
                                </div>
                                <div id="submitdivid" style="float:right;">
                                    <input type="hidden" name="count" id="count" value="1"/>
                                    <table class="table table_no_border">
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
                                                <s:submit value="Submit" cssClass="btn btn-primary" name="submit" theme="simple" id="submitButton"/>
                                            </td>
                                        </tr>
                                        <% }else{%>
                                        <tr>
                                            <td colspan="4">Your work flow is not defined. Please, speak to your hr for your workflow.</td>
                                        </tr>
                                        <%}%>
                                        <%}else{%>
                                        <tr>
                                            <td>&nbsp;</td>
                                            <td colspan="3">
                                                <s:submit value="Submit" cssClass="btn btn-primary" name="submit" theme="simple" id="submitButton"/>
                                            </td>
                                        </tr>
                                        <%}
                                            %>
                                    </table>
                                </div>
                                <s:token/>
                            </s:form>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</section>
<script>
    othervalue(document.frm_MyReimbursements.strType1.options[document.frm_MyReimbursements.strType1.selectedIndex].value,1);
    showType(document.frm_MyReimbursements.reimbursementType.value,1);

    function getReimbursementType(paycycle,cnt){
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
    				document.getElementById("reimbDivId"+cnt).innerHTML=res[0];
    				showType(res[1].trim(),cnt);
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