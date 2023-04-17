<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	String strtUserType = (String) session.getAttribute(IConstants.USERTYPE);
	boolean isCurrentDeclaration = uF.parseToBoolean((String) request.getAttribute("isCurrentDeclaration"));
	if (CF == null || strtUserType == null) {
		return; 
	}
	
	boolean isApproveRelease = uF.parseToBoolean((String) request.getAttribute("isApproveRelease"));
	
	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
	String strEmpId = (String) request.getAttribute("strEmployeeId");
	/* Map<String, String> hmEmpSlabMap =(Map<String,String>) request.getAttribute("hmEmpSlabMap");
	if(hmEmpSlabMap == null) hmEmpSlabMap = new HashMap<String,String>(); */
	int slabType  = uF.parseToInt((String)request.getAttribute("slabType"));
	//System.out.println("slabType ===>> " + slabType+ " -- strEmpId ===>>>> " + strEmpId);
	String strEmpName = (String) request.getAttribute("strEmpName");
	
	Map<String, String> hmPrevOrgTDSDetails = (Map<String, String>) request.getAttribute("hmPrevOrgTDSDetails");
	if(hmPrevOrgTDSDetails==null) hmPrevOrgTDSDetails = new HashMap<String, String>();
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	//Started By Dattatray Date:21-10-21
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus==null) hmFeatureStatus = new HashMap<String, String>();
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	//Ended By Dattatray Date:21-10-21
	
%>
<style>.add-font,.remove-font{float:right;}</style>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/highslide/highslide-with-html.js"> </script>
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/highslide/highslide.css" />
<script type="text/javascript">
$(function(){
	
	$("#btnSubmit").click(function(){
		$('.validateRequired').filter(':hidden').prop('required',false);
		$('.validateRequired').filter(':visible').prop('required',true);
	});
	
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
	
	
});




hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/'; 
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';


function checkAgree() {
	if(document.getElementById("isAgree").checked)
		return true;
	else{
		alert("Please read the declaration policy and select the checkbox to sumbit the investment details.");
		return false;
	}
}


var cnt = 0;
function addSectionDocument(sectionId){
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_section"+cnt;
	divTag.innerHTML = 	"<div style=\"float:left;width:100%;\">"+
						"<input type=\"hidden\" name=\"sectionId\" value=\""+sectionId+"\">"+
						"<input type=\"file\" name=\"sectionDoc\" id=\"sectionDoc"+cnt+"\"/>"+ 
						"<a href=\"javascript:void(0)\" onclick=\"addSectionDocument("+sectionId+")\" class=\"add-font\"></a>"+
    			    	"<a href=\"javascript:void(0)\" onclick=\"removeSectionDocument(this.id, "+sectionId+")\" id=\""+cnt+"\" class=\"remove-font\"></a>"+
    			    	"</div>"+
    			    	"<div class=\"clr\"/>"; 
    document.getElementById("div_section_"+sectionId).appendChild(divTag);
    
    document.getElementById("sectionDoc"+cnt).accept = '.gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs';
  
}

function removeSectionDocument(removeId, sectionId){
	
	var remove_elem = "row_section"+removeId;
	var row_kra = document.getElementById(remove_elem); 
	document.getElementById("div_section_"+sectionId).removeChild(row_kra);
}


var cntAdd=0;
function addRow(strSectionId,count) {
	cntAdd = document.getElementById("cntAdd").value;
	cntAdd++;
	var val=(parseInt(count)+1);
   // alert(val);
    var totalCount = document.getElementById("count").value;
    var table = document.getElementById("investFTableId");
    var rowCount = table.rows.length; 
   // alert("rowCount  "+rowCount);
    var rowid=(parseInt(totalCount)+1);
    var row = table.insertRow(val);
    row.id=""+rowid;
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);
    var cell4 = row.insertCell(3);
    cell1.className ="reportLabel";
    cell2.className ="reportLabel";
    cell3.className ="reportLabel";
    cell4.className ="reportLabel";
    
    cell1.setAttribute('style','background-color: #7FFFD4;' );
    cell2.setAttribute('style','background-color: #7FFFD4;' );
    cell3.setAttribute('style','background-color: #7FFFD4;' );
    cell4.setAttribute('style','background-color: #7FFFD4;' );
    
    var divSection="div_section_"+strSectionId+"_"+rowid;
    var sectionType=strSectionId+"_"+rowid;
    cell1.innerHTML = "&nbsp;";
    cell2.innerHTML = "<input type='hidden' name='strSubInvestmentId_"+strSectionId+"' value='0' /><input type='hidden' name='strSubSectionNo_"+strSectionId+"' value='0' /><input type='hidden' name='strSubSectionAmount_"+strSectionId+"' value='0' /><input type='hidden' name='strSubSectionLimitType_"+strSectionId+"' value='' />"
    	+"<input style='width: 150px; text-align: right;' type='text' name='strSubSectionId_"+strSectionId+"' id='strSubSectionId_"+strSectionId+"_"+cntAdd+"' onkeyup='calAmt(\""+strSectionId+"\")' onfocus='getSectionInfo(\""+strSectionId+"\")'/><a href='javascript:void(0)' onclick='deleteRow(document.getElementById("+rowid+").rowIndex,\""+strSectionId+"\", 0)' class='remove-font'></a>";
    cell3.innerHTML = "<input style='width: 75px !important; text-align: right;' type='text' name='strAmountPaid_"+strSectionId+"' id='strAmountPaid_"+strSectionId+"_"+cntAdd+"' onkeypress='return isNumberKey(event)' onkeyup='calAmt(\""+strSectionId+"\")' onfocus='getSectionInfo(\""+strSectionId+"\")'/>";
    //cell3.innerHTML = "<div id='div_section_"+strSectionId+"_"+rowid+"'> <a href='javascript:void(0)' onclick='addSectionDocument1(\""+divSection+"\",\""+sectionType+"\",\""+strSectionId+"\")' class='add' style='float:right;height:5px'>Add</a></div>";
    cell4.innerHTML = "&nbsp;";
    
    document.getElementById("count").value=rowid;
    document.getElementById("strAmountPaid_"+strSectionId).style.readOnly='true';
    document.getElementById("cntAdd").value=cntAdd;
    // valign="top" class="reportLabel"  <td valign="top" class="reportLabel">&nbsp;</td>
}

function deleteRow(trIndex,strSectionId, investmentId) {
    document.getElementById("investFTableId").deleteRow(trIndex);
    
    if(parseInt(investmentId) > 0){
    	var removeSubInvestmentId = document.getElementById("removeSubInvestmentId").value;
	    if(removeSubInvestmentId.trim() == ''){
	    	document.getElementById("removeSubInvestmentId").value = ','+investmentId+',';
	    } else {
	    	document.getElementById("removeSubInvestmentId").value = removeSubInvestmentId+investmentId+',';
	    }
    }
    
    calAmt(""+strSectionId);
}

function addSectionDocument1(divSection,sectionType,sectionId){
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_section"+cnt;
	divTag.innerHTML = 	"<div style=\"float:left;width:100%;\">"+
						"<input type=\"hidden\" name=\"sectionId_"+sectionId+"\" value=\""+sectionId+"\">"+
						"<input type=\"file\" name=\"sectionDoc_"+sectionId+"\" id=\"sectionDoc_"+sectionId+cnt+"\" />"+ 
						//"<a href=\"javascript:void(0)\" onclick=\"addSectionDocument1('"+divSection+"','"+sectionType+"','"+sectionId+"')\" class=\"add\" style=\"float:right;height:5px;\">Add</a>"+
    			    	//"<a href=\"javascript:void(0)\" onclick=\"removeSectionDocument1(this.id, '"+divSection+"')\" id=\""+cnt+"\" class=\"remove\" style=\"height:5px;>Remove</a>"+
    			    	"</div>"+
    			    	"<div class=\"clr\"/>";
    			    	
    document.getElementById(divSection).appendChild(divTag);
    document.getElementById("sectionDoc_"+sectionId+cnt).accept = '.gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs';

}

function removeSectionDocument1(removeId, divSection){
	var remove_elem = "row_section"+removeId;
	var row_kra = document.getElementById(remove_elem); 
	document.getElementById(divSection).removeChild(row_kra);
}


function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function calAmt(strSectionId, strCombineSubSec, currCntAdd) {
	var amt = 0;
	// strAmountPaid_"+strSectionId  strSubSectionId 
	//alert("strSectionId===="+strSectionId + " -- strCombineSubSec====>>>" + strCombineSubSec+ " -- currCntAdd====>>>" + currCntAdd);
	if(document.getElementById("strSubSecCnt_"+strSectionId+"_"+strCombineSubSec)) {
		var cntCss = document.getElementById("strSubSecCnt_"+strSectionId+"_"+strCombineSubSec).value;
		//alert("cntCss ===>> " + cntCss);
		var strAmt = document.getElementById("strAmountPaid_"+strSectionId+"_"+currCntAdd).value;
		//alert("cntCss ===>> " + cntCss + " -- strAmt ===>> " + strAmt);
		if(parseInt(strCombineSubSec)>0 && parseFloat(strAmt)>0) {
			document.getElementById("strAmountPaid_"+strSectionId+"_"+cntCss).value = "0";
			document.getElementById("strAmountPaid_"+strSectionId+"_"+cntCss).readOnly = true;
		} else if(parseInt(strCombineSubSec)>0 && parseFloat(strAmt) == 0) {
			document.getElementById("strAmountPaid_"+strSectionId+"_"+cntCss).value = "0";
			document.getElementById("strAmountPaid_"+strSectionId+"_"+cntCss).readOnly = false;
		}
	}
	
	var cntAdd1 = document.getElementById("cntAdd").value;
	//alert("cntAdd1===="+cntAdd1);
	for(var i=0;i<=cntAdd1; i++) {
		var textid = document.getElementById("strSubSectionId_"+strSectionId+"_"+i);
		if(textid && textid.value != '') {
			var id = document.getElementById("strAmountPaid_"+strSectionId+"_"+i);
			if(id) {
				if(id.value != '') {
					//alert(id+" value===="+id.value);
					amt = parseFloat(amt) + parseFloat(id.value);
				}
			}
		}
	}
	//alert("amt===="+amt);
	document.getElementById("strAmountPaid_"+strSectionId).value = ""+parseFloat(amt);
	
}  


//other investment


var cnt1 = 0;
function addOtherSectionDocument(OthersectionId){
	cnt1++;
	var divTag = document.createElement("div");
    divTag.id = "row_other_section"+cnt1;
	divTag.innerHTML = 	"<div style=\"float:left;width:100%;\">"+
						"<input type=\"hidden\" name=\"othersectionId\" value=\""+OthersectionId+"\">"+
						"<input type=\"file\" name=\"othersectionDoc\" id=\"othersectionDoc"+cnt1+"\" />"+ 
						"<a href=\"javascript:void(0)\" onclick=\"addOtherSectionDocument("+OthersectionId+")\" class=\"add-font\"></a>"+
    			    	"<a href=\"javascript:void(0)\" onclick=\"removeOtherSectionDocument(this.id, "+OthersectionId+")\" id=\""+cnt1+"\" class=\"remove-font\"></a>"+
    			    	"</div>"+
    			    	"<div class=\"clr\"/>";
    document.getElementById("div_other_section_"+OthersectionId).appendChild(divTag);
    document.getElementById("othersectionDoc"+cnt1).accept = '.gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs';

}

function removeOtherSectionDocument(removeId, OthersectionId){
	var remove_elem = "row_other_section"+removeId;
	var row_kra = document.getElementById(remove_elem); 
	document.getElementById("div_other_section_"+OthersectionId).removeChild(row_kra);
}


var cntAdd2=0;
function addOtherRow(strOtherSectionId,count) {
	cntAdd2 = document.getElementById("cntAdd1").value;
	cntAdd2++;
	var val=(parseInt(count)+1);
   // alert(val);
    var totalCount = document.getElementById("count").value;
    var table = document.getElementById("investFTableId");
    var rowCount = table.rows.length; 
   // alert("rowCount  "+rowCount);
    var rowid=(parseInt(totalCount)+1);
    var row = table.insertRow(val);
    row.id=""+rowid;
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);
    var cell4 = row.insertCell(3);
    cell1.className ="reportLabel";
    cell2.className ="reportLabel";
    cell3.className ="reportLabel";
    cell4.className ="reportLabel";
    
    cell1.setAttribute('style','background-color: #7FFFD4;' );
    cell2.setAttribute('style','background-color: #7FFFD4;' );
    cell3.setAttribute('style','background-color: #7FFFD4;' );
    cell4.setAttribute('style','background-color: #7FFFD4;' );
    
    var divSection="div_other_section_"+strOtherSectionId+"_"+rowid;
    var sectionType=strOtherSectionId+"_"+rowid;
    cell1.innerHTML = "&nbsp;";
    cell2.innerHTML = "<input type='hidden' name='strSubOtherInvestmentId_"+strOtherSectionId+"' value='0' /><input style='width: 150px; text-align: right;' type='text' name='strSubOtherSectionId_"+strOtherSectionId+"' id='strSubOtherSectionId_"+strOtherSectionId+"_"+cntAdd2+"' onkeyup='calOtherAmt(\""+strOtherSectionId+"\")' onfocus='getSectionInfo(\""+strOtherSectionId+"\")'/><a href='javascript:void(0)' onclick='deleteOtherRow(document.getElementById("+rowid+").rowIndex,\""+strOtherSectionId+"\",0)' class='remove-font'></a>";
    cell3.innerHTML = "<input style='width: 75px !important; text-align: right;' type='text' name='strOtherAmountPaid_"+strOtherSectionId+"' id='strOtherAmountPaid_"+strOtherSectionId+"_"+cntAdd2+"' onkeypress='return isNumberKey(event)' onkeyup='calOtherAmt(\""+strOtherSectionId+"\")' onfocus='getSectionInfo(\""+strOtherSectionId+"\")'/>";
    //cell3.innerHTML = "<div id='div_section_"+strSectionId+"_"+rowid+"'> <a href='javascript:void(0)' onclick='addSectionDocument1(\""+divSection+"\",\""+sectionType+"\",\""+strSectionId+"\")' class='add' style='float:right;height:5px'>Add</a></div>";
    cell4.innerHTML = "&nbsp;";
    
    document.getElementById("count").value=rowid;
   // alert("before cntAdd=====>"+cntAdd2);
    //alert("before strOtherSectionId=====>"+strOtherSectionId);
    document.getElementById("strOtherAmountPaid_"+strOtherSectionId).style.readOnly='true';
    document.getElementById("cntAdd1").value=cntAdd2;

    //alert("document.getElementById(cntAdd).value=====>"+document.getElementById("cntAdd").value);
    // valign="top" class="reportLabel"  <td valign="top" class="reportLabel">&nbsp;</td>
}

function deleteOtherRow(trIndex, strOtherSectionId, investmentId) {
    document.getElementById("investFTableId").deleteRow(trIndex);
    
    if(parseInt(investmentId) > 0){
    	var removeSubInvestmentId = document.getElementById("removeSubOtherInvestmentId").value;
	    if(removeSubInvestmentId.trim() == ''){
	    	document.getElementById("removeSubOtherInvestmentId").value = ','+investmentId+',';
	    } else {
	    	document.getElementById("removeSubOtherInvestmentId").value = removeSubInvestmentId+investmentId+',';
	    }
    }
    
    calOtherAmt(""+strOtherSectionId);
}

function addOtherSectionDocument1(divSection,sectionType,sectionId){
	cnt1++;
	var divTag = document.createElement("div");
    divTag.id = "row_other_section"+cnt1;
	divTag.innerHTML = 	"<div style=\"float:left;width:100%;\">"+
						"<input type=\"hidden\" name=\"OthersectionId_"+sectionId+"\" value=\""+sectionId+"\">"+
						"<input type=\"file\" name=\"OthersectionDoc_"+sectionId+"\" id=\"OthersectionDoc_"+sectionId+cnt1+"\" />"+ 
						//"<a href=\"javascript:void(0)\" onclick=\"addSectionDocument1('"+divSection+"','"+sectionType+"','"+sectionId+"')\" class=\"add\" style=\"float:right;height:5px;\">Add</a>"+
    			    	//"<a href=\"javascript:void(0)\" onclick=\"removeSectionDocument1(this.id, '"+divSection+"')\" id=\""+cnt+"\" class=\"remove\" style=\"height:5px;>Remove</a>"+
    			    	"</div>"+
    			    	"<div class=\"clr\"/>"; 
    			    	
    document.getElementById(divSection).appendChild(divTag);
    document.getElementById("OthersectionDoc_"+sectionId+cnt1).accept = '.gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs';

}

function removeOtherSectionDocument1(removeId, divSection){
	var remove_elem = "row_other_section"+removeId;
	var row_kra = document.getElementById(remove_elem); 
	document.getElementById(divSection).removeChild(row_kra);
}


function isNumberKey(evt)
{
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function calOtherAmt(strSectionId){
	var amt=0;
	// strAmountPaid_"+strSectionId  strSubSectionId 
	//alert("strSectionId===="+strSectionId);
	var cntAdd3=document.getElementById("cntAdd1").value;
	for(var i=0;i<=cntAdd3;i++){
		
		var textid=document.getElementById("strSubOtherSectionId_"+strSectionId+"_"+i);
		if(textid && textid.value != '') {
			var id=document.getElementById("strOtherAmountPaid_"+strSectionId+"_"+i);
			if(id){
				if(id.value != ''){   
					amt=parseFloat(amt) +parseFloat(id.value);
				}
			}
		}
	}
	//alert("amt===="+amt);
	document.getElementById("strOtherAmountPaid_"+strSectionId).value=""+parseFloat(amt);
	
}  

function getSectionInfo(sid){
	document.getElementById("myDiv").innerHTML='';
	
	var financialYear = document.getElementById("f_strFinancialYear").value;
	var action = 'GetSectionDesc.action?SID='+sid+'&financialYear='+financialYear;
	getContent('myDiv',action);
}

function removeDocument(empId,investmentDocId,docFilePath,docFile){
	if(confirm('Are you sure, You want remove document ('+docFile+') ?')){
		var financialYear = document.getElementById("f_strFinancialYear").value;
		var url='InvestmentForm.action?operation=deleteDoc&investmentDocId='+investmentDocId;
		url+='&strEmployeeId='+empId+'&docFilePath='+docFilePath+'&docFile='+docFile+'&f_strFinancialYear='+financialYear;
		
		//window.location = url;
		
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			url: url,
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}	
}


function submitForm(type){
	var f_strFinancialYear = document.getElementById("f_strFinancialYear").value;
	var strEmployeeId = document.getElementById("strEmployeeId").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strEmployeeId='+strEmployeeId;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'InvestmentForm.action?f_strFinancialYear='+f_strFinancialYear+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

/* $("#frmAddInvestment").submit(function(e){
	e.preventDefault();
	if(checkAgree()){
		var strEmployeeId = document.frmAddInvestment.strEmployeeId.value;
		var f_strFinancialYear = document.frmAddInvestment.f_strFinancialYear.value;;
		
		var form_data = $("form[name='frmAddInvestment']").serialize();
     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
 			url : "AddInvestment.action",
 			data: form_data,
 			cache : false,
 			success : function(res) {
 				$("#divResult").html(res);
 			},
 			error : function(res) {
 				$.ajax({
 					url: 'InvestmentForm.action?strEmployeeId='+strEmployeeId+'&f_strFinancialYear='+f_strFinancialYear,
 					cache: true,
 					success: function(result){
 						$("#divResult").html(result);
 					}
 				});
 			}
 		});
	} 
}); */  

function viewEmpTDSProjection(empName, empId,strFinancialYearStart,strFinancialYearEnd) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$('.modal-title').html('TDS Projection of '+empName+' for financial year '+strFinancialYearStart+' to '+strFinancialYearEnd);
	$.ajax({
		url : "ViewEmpTDSProjection.action?strEmpId="+empId+"&strFinancialYearStart="+strFinancialYearStart+"&strFinancialYearEnd="+strFinancialYearEnd,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 


	function selectOne(x, slabType) {
	 	var status = x.checked;
	 	
		if(status == 'false' || status == false) {
			if(slabType=='0') {
				document.getElementById('chboxStandardNew').value = '1';
				document.getElementById('chboxNew').checked = 'checked';
			} else if(slabType=='1') {
				document.getElementById('chboxStandardNew').value = '0';
				document.getElementById('chboxStandard').checked = 'checked';
			}
			
		} else {
			if(slabType=='0') {
				document.getElementById('chboxStandardNew').value = '0';
				document.getElementById('chboxNew').checked = '';
			} else if(slabType=='1') {
				document.getElementById('chboxStandardNew').value = '1';
				document.getElementById('chboxStandard').checked = '';
			}
			
		}
	}
	
/* ===start parvez date: 03-08-2022=== */	
	function submitIncomTaxSlab(dataType,operation){
		var f_strFinancialYear = document.getElementById("f_strFinancialYear").value;
		var strEmployeeId = document.getElementById("strEmployeeId").value;
		var chboxStandardNew = document.getElementById("chboxStandardNew").value;
		
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'InvestmentForm.action?f_strFinancialYear='+f_strFinancialYear+'&strEmployeeId='+strEmployeeId
					+'&dataType='+dataType+'&operation='+operation+'&chboxStandardNew='+chboxStandardNew,
			data: $("#"+this.id).serialize(),
			success: function(result){
				console.log(result);
	        	$("#divResult").html(result);
	   		},error : function(res) {
 				$.ajax({
 					url: 'InvestmentForm.action?strEmployeeId='+strEmployeeId+'&f_strFinancialYear='+f_strFinancialYear,
 					cache: true,
 					success: function(result){
 						$("#divResult").html(result);
 					}
 				});
 			}
		});
	}
/* ===end parvez date: 03-08-2022=== */

</script>

			<!-- Started By Dattatray Date:21-10-21 -->
			
			<%
					Date date= new Date();
					boolean isITOpenMonth;
					if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_IT_DECLRATION_SUBMISSION_OPEN_MONTH))) {
 						if(hmFeatureUserTypeId.get(IConstants.F_IT_DECLRATION_SUBMISSION_OPEN_MONTH+"_USER_IDS").contains(uF.getMonthInt(date)+"")){
 							isITOpenMonth = false;
 						}else{
 							isITOpenMonth = true;
 						}
 					}else{ 
 						isITOpenMonth = false;
 					}
 			%>
 				<!-- Created By Dattatray Date:21-10-21 Note:condition checked-->
 				 <%
                	if(isITOpenMonth){
                %>
                
                	 <section class="col-lg-12 connectedSortable">
				       <h5 align="center">The IT Declarations window is currently closed. Please contact your HR Department.</h5>
				     </section> 
                <% } else{ %>	
        <section class="col-lg-8 connectedSortable">
        	<div class="box box-none">
        		<%-- <ul class="nav nav-tabs">
					<li class="active"><a href="javascript:void(0)" onclick="window.location='InvestmentForm.action'" data-toggle="tab">IT Declarations</a></li>
					<%if (strtUserType != null && strtUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {%>
						<li><a href="javascript:void(0)" onclick="window.location='MyForm16.action'" data-toggle="tab">Form 16</a></li>
					<% } %>
				</ul> --%>
                <%
					Map hmInvestment = (Map) request.getAttribute("hmInvestment");
					if (hmInvestment == null)hmInvestment = new HashMap();
					Map hmSectionMap = (Map) request.getAttribute("hmSectionMap");
					Map hmInvestmentId = (Map) request.getAttribute("hmInvestmentId");
					Map hmInvestmentStatus = (Map) request.getAttribute("hmInvestmentStatus");
					Map hmInvestmentDocuments = (Map) request.getAttribute("hmInvestmentDocuments");
					
					Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) request.getAttribute("hmSubInvestment");
					if (hmSubInvestment == null)hmSubInvestment = new HashMap<String, List<Map<String, String>>>();
					Map<String, String> hmUnderSection =(Map<String, String>)request.getAttribute("hmUnderSection");
					if(hmUnderSection==null) hmUnderSection = new HashMap<String, String>();
					
					Map hmOtherInvestment = (Map) request.getAttribute("hmOtherInvestment");
					if (hmOtherInvestment == null)hmOtherInvestment = new HashMap();
					Map hmOtherSectionMap = (Map) request.getAttribute("hmOtherSectionMap");
					Map hmOtherInvestmentId = (Map) request.getAttribute("hmOtherInvestmentId");
					Map hmOtherInvestmentStatus = (Map) request.getAttribute("hmOtherInvestmentStatus");
					Map hmOtherInvestmentDocuments = (Map) request.getAttribute("hmOtherInvestmentDocuments");
					
					Map<String, List<Map<String, String>>> hmOtherSubInvestment = (Map<String, List<Map<String, String>>>) request.getAttribute("hmOtherSubInvestment");
					if (hmOtherSubInvestment == null)hmOtherSubInvestment = new HashMap<String, List<Map<String, String>>>();
					Map<String, String> hmOtherUnderSection =(Map<String, String>)request.getAttribute("hmOtherUnderSection");
					if(hmOtherUnderSection==null) hmOtherUnderSection = new HashMap<String, String>();
					
					Map<String, List<Map<String, String>>> hmSubSectionData = (Map<String, List<Map<String, String>>>) request.getAttribute("hmSubSectionData");
					if(hmSubSectionData==null) hmSubSectionData = new HashMap<String, List<Map<String, String>>>();
					
					Map<String, Map<String, Map<String, String>>> hmSubSectionAmount = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("hmSubSectionAmount");
					if(hmSubSectionAmount == null) hmSubSectionAmount = new HashMap<String, Map<String, Map<String, String>>>();
					
					Map<String, List<String>> hmAddedSubSections = (Map<String, List<String>>) request.getAttribute("hmAddedSubSections");
					if(hmAddedSubSections==null) hmAddedSubSections = new HashMap<String, List<String>>();
					
					
				%>
				   
				
 					
                <div class="box-body" style="padding: 5px; overflow-y: auto;">
                
               
					<div class="box box-default collapsed-box">
						<div class="box-header with-border">
						    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
						    <div class="box-tools pull-right">
						        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						    </div>
						</div>
						<div class="box-body" style="padding: 5px; overflow-y: auto;">
							<s:form name="frmInvestment1" action="InvestmentForm" theme="simple" method="post">
								<s:hidden name="currUserType"/>
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
										<i class="fa fa-filter"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Financial Year</p>
											<s:select theme="simple" name="f_strFinancialYear" id="f_strFinancialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" onchange="submitForm('1');"/>
										</div>
										<% if (strtUserType != null && !strtUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Employee</p>
												<s:select theme="simple" list="employeeList" name="strEmployeeId" id="strEmployeeId" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="Select Employee" onchange="submitForm('2');"/>
											</div>
										<% } %>
									</div>
								</div>
							</s:form>
						</div>
					</div>
					
					<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
			    	<% session.removeAttribute(IConstants.MESSAGE); %>
			    	
			    	 <%-- <%if(slabType == 0) { %> --%>
			    	
			    	<% if(uF.parseToInt(strEmpId)>0) { %>
						<s:form name="frmAddInvestment" id="frmAddInvestment" action="AddInvestment" method="post" enctype="multipart/form-data">
							<s:hidden name="strEmployeeId" id="strEmployeeId"></s:hidden>
							<s:hidden name="f_strFinancialYear" id="f_strFinancialYear"></s:hidden>
							
							<input type="hidden" name="operation" value="A" />
							<input type="hidden" name="removeSubInvestmentId" id="removeSubInvestmentId" value=""/>
							<input type="hidden" name="removeSubOtherInvestmentId" id="removeSubOtherInvestmentId" value=""/>
				
							<%int count=1;
								int ii=0;
							%> 
							<table class="table table-striped table-bordered" id="investFTableId">
								<tr id="<%=count %>" style="background-color: #DADADA; font-weight:600;">
									<td class="reportHeading" width="20%">Under Section</td>
									<td class="reportHeading" width="35%">Section Code</td>
									<td class="reportHeading" width="15%">Amount Paid</td>
									<td class="reportHeading" width="25%">Documents</td>
								</tr>
				   				<% if(hmPrevOrgTDSDetails!=null && uF.parseToDouble(hmPrevOrgTDSDetails.get(strEmpId+"_GROSS_AMT"))>0) { %>
				   				<tr id="<%=count %>">
									<td valign="top" class="reportLabel"></td>
									<td valign="top" class="reportLabel"> Income from previous Orgnization </td>
									<td valign="top" class="reportLabel"><%=hmPrevOrgTDSDetails.get(strEmpId+"_GROSS_AMT") %></td>
									<td valign="top" class="reportLabel">
									<% if(hmPrevOrgTDSDetails.get(strEmpId+"_FORM16_DOC") != null && !hmPrevOrgTDSDetails.get(strEmpId+"_FORM16_DOC").equals("")) { %>
					                 		<%if(docRetriveLocation == null) { %>
                                           		<a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + hmPrevOrgTDSDetails.get(strEmpId+"_FORM16_DOC")  %>" title="Form 16" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
                                            <% } else { %>
												<a href="<%=docRetriveLocation +hmPrevOrgTDSDetails.get(strEmpId+"_FORM16_DOC") %>" title="Form 16" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
                                            <% } %>
					                 	<% } %>
									</td>
								</tr>
								<% } %>
								<%
									int i = 0;
									int cntAdd=0;
										Set set = hmInvestmentId.keySet();
										Iterator it = set.iterator();
										List<String> alUnderSection = new ArrayList<String>();
										while (it.hasNext()) {
											String strSectionId = (String) it.next();
											String strInvestmentId = (String) hmInvestmentId.get(strSectionId);   
											count++;
											String strUnderSection = "";
											String strStyle = "";
									  		if(ii==0) {
									  			alUnderSection.add(hmUnderSection.get(strSectionId));
												strUnderSection = hmUnderSection.get(strSectionId);
									  		} else if(!alUnderSection.contains(hmUnderSection.get(strSectionId))) {
												alUnderSection.add(hmUnderSection.get(strSectionId));
												strUnderSection = hmUnderSection.get(strSectionId);
												strStyle = "style=\"border-top: 2px solid #CCCCCC;\""; //616161
											}
									  		ii++;
									  		//System.out.println("strSectionId ===>> " + strSectionId);
									  		List<String> alAddedSubSections = hmAddedSubSections.get(""+strSectionId);
											if(alAddedSubSections == null) alAddedSubSections = new ArrayList<String>();
											
											List<Map<String, String>> subInvestList = (List<Map<String, String>>)hmSubInvestment.get(""+strSectionId);
											if(subInvestList == null) subInvestList = new ArrayList<Map<String, String>>();
											
											
											Map<String, Map<String, String>> hmSubSecDetails = hmSubSectionAmount.get(""+strSectionId);
											if(hmSubSecDetails == null) hmSubSecDetails = new HashMap<String, Map<String, String>>();
								%>
								<%-- <% System.out.println("IF/704---strUnderSection="+strUnderSection+"-----"+hmSectionMap.get(strSectionId)+"--strInvestmentId="+strInvestmentId); %> --%>
								<tr id="<%=count %>">
									<td valign="top" class="reportLabel" <%=strStyle%>><%=strUnderSection %></td>
									<td valign="top" class="reportLabel" <%=strStyle%>>
										<input type="hidden" name="strInvestmentId" value="<%=strInvestmentId%>" /> 
										<input type="hidden" name="strSectionId" value="<%=strSectionId%>" />
										<%=(String) hmSectionMap.get(strSectionId)%> 
										<a href="javascript:void(0)" onclick="addRow('<%=strSectionId%>',this.parentNode.parentNode.rowIndex)" class="add-font"></a>
									</td>
									<td valign="top" class="reportLabel" <%=strStyle%>>
										<input style="width: 75px !important; text-align: right;" type="text" name="strAmountPaid" id="strAmountPaid_<%=strSectionId%>" onkeypress="return isNumberKey(event)"
											onfocus="getSectionInfo(document.frmAddInvestment.strSectionId[<%=i%>].value);" class="validateNumber"
											value="<%=uF.showData((String) hmInvestment.get(strSectionId), "0")%>" <%=(uF.parseToBoolean((String) hmInvestmentStatus.get(strInvestmentId)) || alAddedSubSections.size()>0) ? "readonly" : "" %> />
											<%=(uF.parseToBoolean((String) hmInvestmentStatus.get(strInvestmentId)) ? "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" style=\"float:right;\"></i>" : "")%> 
									</td>
									<td valign="top" class="reportLabel" <%=strStyle %>>
										<div id="div_section_<%=strSectionId %>">
											<%=uF.showData((String) hmInvestmentDocuments.get(strSectionId), "") %>
											<%=(uF.parseToBoolean((String) hmInvestmentStatus.get(strInvestmentId)) ? "" : "<a href=\"javascript:void(0)\" onclick=\"addSectionDocument(" + strSectionId + ")\" class=\"add-font\"></a>")%>
										</div>
									</td>
								</tr>
								
								<%
									List<Map<String, String>> subSectionList = (List<Map<String, String>>)hmSubSectionData.get(""+strSectionId);
									for(int j=0;subSectionList!=null && j<subSectionList.size(); j++) {
										Map<String, String> hm = (Map<String, String>)subSectionList.get(j);
										if(hm != null && !alAddedSubSections.contains(hm.get("SUB_SECTION_ID"))) {
											count++;
											cntAdd++;
									%>
									<tr id="<%=count %>">
										<td valign="top" class="reportLabel">&nbsp;</td>
										<td valign="top" class="reportLabel"><%=(j+1) %>
											<input type="hidden" name="strSubInvestmentId_<%=strSectionId%>" value="0" />
											<input type="hidden" name="strSubSectionNo_<%=strSectionId%>" value="<%=hm.get("SUB_SECTION_ID") %>" />
											<input type="hidden" name="strSubSectionAmount_<%=strSectionId%>" value="<%=hm.get("SUB_SECTION_AMOUNT") %>" />
											<input type="hidden" name="strSubSectionLimitType_<%=strSectionId%>" value="<%=hm.get("SUB_SECTION_LIMIT_TYPE") %>" /> 
											<input style="width: 150px; text-align: right;" type="text" name="strSubSectionId_<%=strSectionId%>" id="strSubSectionId_<%=strSectionId%>_<%=cntAdd%>" 
												onkeyup="calAmt('<%=strSectionId%>', '', '<%=cntAdd%>')" value="<%=uF.showData(hm.get("SUB_SECTION_NAME"),"") %>" readonly="readonly"/>
										</td>
										<td valign="top" class="reportLabel">
											<input style="width: 75px !important; text-align: right;" type="text" name="strAmountPaid_<%=strSectionId%>" id="strAmountPaid_<%=strSectionId%>_<%=cntAdd%>" 
												onkeypress="return isNumberKey(event)" onkeyup="calAmt('<%=strSectionId%>', '', '<%=cntAdd%>')" value="<%=uF.showData(hm.get("PAID_AMOUNT"),"")%>"/>
											<%=(uF.parseToBoolean(hm.get("STATUS")) ? "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" style=\"float:right;\"></i>" : "")%>
										</td>
										<td valign="top" class="reportLabel">&nbsp;</td>
									</tr>
								<% } } %>
								
								<%
									//List<Map<String, String>> subInvestList = (List<Map<String, String>>)hmSubInvestment.get(""+strSectionId);
									for(int j=0;subInvestList!=null && j<subInvestList.size(); j++) {
										Map<String, String> hm = (Map<String, String>)subInvestList.get(j);
										String strSubSecAmt = hm.get("SUB_SECTION_AMOUNT");
										String strSubSecLimitType = hm.get("SUB_SECTION_LIMIT_TYPE");
										String strSubSecName = hm.get("SECTION_NAME");
										String strCombineSubSec = "";
										if(uF.parseToInt(hm.get("SUB_SECTION_ID"))>0) {
											Map<String, String> hmSubSec = hmSubSecDetails.get(hm.get("SUB_SECTION_ID"));
											if(hmSubSec == null) hmSubSec = new HashMap<String, String>();
											strSubSecAmt = hmSubSec.get("SUB_SECTION_AMOUNT");
											strSubSecLimitType = hmSubSec.get("SUB_SECTION_LIMIT_TYPE");
											strSubSecName = hmSubSec.get("SUB_SECTION_NAME");
											strCombineSubSec = hmSubSec.get("COMBINE_SUB_SECTION");
										}
										count++;
										cntAdd++;
								%>
								<tr id="<%=count %>">
									<td valign="top" class="reportLabel">&nbsp;</td>
									<td valign="top" class="reportLabel"><%=(j+1) %>
										<input type="hidden" name="strSubInvestmentId_<%=strSectionId%>" value="<%=hm.get("INVESTMENT_ID")%>" /> 
										<input type="hidden" name="strSubSectionNo_<%=strSectionId%>" value="<%=hm.get("SUB_SECTION_ID") %>" />
										<input type="hidden" name="strSubSectionAmount_<%=strSectionId%>" value="<%=strSubSecAmt %>" />
										<input type="hidden" name="strSubSectionLimitType_<%=strSectionId%>" value="<%=strSubSecLimitType %>" />
										<input type="hidden" name="strSubSecCnt_<%=strSectionId%>_<%=hm.get("SUB_SECTION_ID") %>" id="strSubSecCnt_<%=strSectionId%>_<%=hm.get("SUB_SECTION_ID") %>" value="<%=cntAdd %>" /> 
										<input style="width: 150px; text-align: right;" type="text" name="strSubSectionId_<%=strSectionId%>" id="strSubSectionId_<%=strSectionId%>_<%=cntAdd%>" 
										onkeyup="calAmt('<%=strSectionId%>', '<%=strCombineSubSec %>', '<%=cntAdd%>');" value="<%=uF.showData(strSubSecName, "") %>" readonly="readonly" />
										<% if(uF.parseToInt(hm.get("SUB_SECTION_ID"))==0) { %>
											<a href="javascript:void(0)" onclick="deleteRow(this.parentNode.parentNode.rowIndex,'<%=strSectionId%>','<%=hm.get("INVESTMENT_ID")%>');" class="remove-font"></a>
										<% } %>
									</td>
									<td valign="top" class="reportLabel">
										<input style="width: 75px !important; text-align: right;" type="text" name="strAmountPaid_<%=strSectionId%>" 
										id="strAmountPaid_<%=strSectionId%>_<%=cntAdd%>" onkeypress="return isNumberKey(event)" 
										onkeyup="calAmt('<%=strSectionId%>', '<%=strCombineSubSec %>', '<%=cntAdd%>');" value="<%=uF.showData(hm.get("PAID_AMOUNT"),"")%>"/>
											<%=(uF.parseToBoolean(hm.get("STATUS")) ? "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" style=\"float:right;\"></i>" : "")%>
									</td>
									<td valign="top" class="reportLabel">&nbsp;</td>
								</tr>
								<% } %>
								<%
									i++;
								}
									count++;
								%>
				
								<%
									//other investment
									int i1 = 0;
									ii=0;
									int cntAdd1=0;
										Set set1 = hmOtherInvestmentId.keySet();
										Iterator it1 = set1.iterator();
										List<String> alOtherUnderSection = new ArrayList<String>();
										while (it1.hasNext()) {
											String strOtherSectionId = (String) it1.next();
											String strOtherInvestmentId = (String) hmOtherInvestmentId.get(strOtherSectionId);
											count++;
											String strOtherUnderSection = "";
											String strOtherStyle = "";
									  		if(ii==0) {
									  			alOtherUnderSection.add(hmOtherUnderSection.get(strOtherSectionId));
												strOtherUnderSection = hmOtherUnderSection.get(strOtherSectionId);
									  		} else if(!alOtherUnderSection.contains(hmOtherUnderSection.get(strOtherSectionId))) {
												alOtherUnderSection.add(hmOtherUnderSection.get(strOtherSectionId));
												strOtherUnderSection = hmOtherUnderSection.get(strOtherSectionId);
												strOtherStyle = "style=\"border-top: 2px solid #CCCCCC;\""; //616161
											}
									  		ii++;
								%>
								<tr id="<%=count %>">
									<td valign="top" class="reportLabel" <%=strOtherStyle%>><%=strOtherUnderSection %></td>
									<td valign="top" class="reportLabel" <%=strOtherStyle%>>
										<input type="hidden" name="strOtherInvestmentId" value="<%=strOtherInvestmentId%>" /> 
										<input type="hidden" name="strOtherSectionId" value="<%=strOtherSectionId%>" />
										<%=(String) hmOtherSectionMap.get(strOtherSectionId)%> 
										<a href="javascript:void(0)" onclick="addOtherRow('<%=strOtherSectionId%>',this.parentNode.parentNode.rowIndex)" class="add-font"></a>
									</td>
									<td valign="top" class="reportLabel" <%=strOtherStyle%>>
										<input style="width: 75px !important; text-align: right;" type="text" name="strOtherAmountPaid" id="strOtherAmountPaid_<%=strOtherSectionId%>" onkeypress="return isNumberKey(event)"
											onfocus="getSectionInfo(document.frmAddInvestment.strOtherSectionId[<%=i%>].value);"
											value="<%=uF.showData((String) hmOtherInvestment.get(strOtherSectionId), "0")%>"
											<%=(uF.parseToBoolean((String) hmOtherInvestmentStatus.get(strOtherInvestmentId)) ? "readonly" : "")%> />
											<%=(uF.parseToBoolean((String) hmOtherInvestmentStatus.get(strOtherInvestmentId)) ? "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" style=\"float:right;\"></i>" : "")%>
									</td>
									<td valign="top" class="reportLabel" <%=strOtherStyle%>>
										<div id="div_other_section_<%=strOtherSectionId%>">
											<%=uF.showData((String) hmOtherInvestmentDocuments.get(strOtherSectionId), "")%>
											<%=(uF.parseToBoolean((String) hmOtherInvestmentStatus.get(strOtherInvestmentId)) ? "" : "<a href=\"javascript:void(0)\" onclick=\"addOtherSectionDocument(" + strOtherSectionId + ")\" class=\"add-font\"></a>")%>
										</div>
									</td>
								</tr>   
								
								<%
									List<Map<String, String>> subOtherInvestList = (List<Map<String, String>>)hmOtherSubInvestment.get(""+strOtherSectionId);
									for(int j=0;subOtherInvestList!=null && j<subOtherInvestList.size();j++) {
										Map<String, String> hmOther = (Map<String, String>)subOtherInvestList.get(j);
										count++;
										cntAdd1++; 
								%>
								<tr id="<%=count %>">
									<td valign="top" class="reportLabel">&nbsp;</td>
									<td valign="top" class="reportLabel"><%=(j+1) %>
										<input type="hidden" name="strSubOtherInvestmentId_<%=strOtherSectionId%>" value="<%=hmOther.get("INVESTMENT_ID")%>" /> 
										<input style="width: 150px; text-align: right;" type="text" name="strSubOtherSectionId_<%=strOtherSectionId%>" 
										id="strSubOtherSectionId_<%=strOtherSectionId%>_<%=cntAdd1%>" onkeyup="calOtherAmt('<%=strOtherSectionId%>')" 
										value="<%=uF.showData(hmOther.get("SECTION_NAME"),"") %>"/>
										<a href="javascript:void(0)" onclick="deleteOtherRow(this.parentNode.parentNode.rowIndex,'<%=strOtherSectionId%>','<%=hmOther.get("INVESTMENT_ID")%>');" class="remove-font" ></a>
									</td>
									<td valign="top" class="reportLabel">
										<input style="width: 75px !important; text-align: right;" type="text" name="strOtherAmountPaid_<%=strOtherSectionId%>" 
										id="strOtherAmountPaid_<%=strOtherSectionId%>_<%=cntAdd1%>" onkeypress="return isNumberKey(event)" 
										onkeyup="calOtherAmt('<%=strOtherSectionId%>')" value="<%=uF.showData(hmOther.get("PAID_AMOUNT"),"")%>"/>
										<%=(uF.parseToBoolean(hmOther.get("STATUS")) ? "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" style=\"float:right;\"></i>" : "") %>
										
									</td>
									<td valign="top" class="reportLabel">&nbsp;</td>
								</tr>
								<% } %>
								<%
									i++;
								}
									count++;
								%>
				
				
								<tr id="<%=count %>">
									<td colspan="4"><input type="checkbox" id="isAgree" value="true" name="isAgree" class="validateRequired" style="width: 10px; height: 10px">
										<sup>*</sup> By clicking this check box you agree that the information you entered above is correct.
										
									</td> 
								</tr>
				
								<%count++; %>
								<tr id="<%=count %>">
									<td colspan="4" align="center">
										<%if(isApproveRelease){ %>
											<input type="button" class="btn btn-primary" id="btnSubmit" value="Submit your declaration" onclick="alert('Already approved and released Form16.');" />
										<%} else { %>
											<input type="submit" class="btn btn-primary" id="btnSubmit" value="Submit your declaration"/>
										<%} %>
									</td>
								</tr>
							</table>
							
							<input type="hidden" name="count" id="count" value="<%=count%>" /> 
							<input type="hidden" name="cntAdd" id="cntAdd" value="<%=cntAdd%>" />
							<input type="hidden" name="cntAdd1" id="cntAdd1" value="<%=cntAdd1%>" /> 
							<div id="myDiv" style="float: left;"></div>
				
						</s:form>
					<% } else { %>
						<div class="nodata msg">Please select employee.</div>
					<%-- <% } 
			    	   
			    	 } else { %>
					  <div class="nodata msg">You are not eligible for IT decleration Submission </div> --%>
					  <% } %>
					
					<hr style="width: 100%; float: left">
					
				  </div>
				
                <!-- /.box-body -->
            </div>
        </section>
        
        <section class="col-lg-4 connectedSortable">
        	<%-- <div class="box box-primary">
                <%
					List alPastInvestments = (List) request.getAttribute("alPastInvestments");
					if (alPastInvestments == null)
						alPastInvestments = new ArrayList();
				%>
                <div class="box-header with-border">
                    <h3 class="box-title"><% if(strtUserType != null && strtUserType.equals(IConstants.EMPLOYEE)) { %>My<%} %> Past Investments</h3>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto;max-height:400px;">
                    <div>
						<table>
							<%
								int x = 0;
								for (; x < alPastInvestments.size(); x++) {
							%>
							<tr>
								<td class="alignLeft" style="padding-left: 10px"><%=(String) alPastInvestments.get(x)%></td>
							</tr>
							<% } %>
							<% if (x == 0) { %>
							<tr>
								<td class="alignLeft">No previous information available</td>
							</tr>
							<% } %>
						</table>
				
					</div>
                </div>
                <!-- /.box-body -->
            </div> --%>
            
            <% if(strtUserType != null && strtUserType.equals(IConstants.EMPLOYEE)) { %>
	            <div class="box box-primary">
	                <div class="box-header with-border">
	                    <h3 class="box-title">My Form 16</h3>
	                </div>
	                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto;max-height:400px;">
						
						<s:action name="MyForm16" executeResult="true"></s:action>
						
	                </div>
	                <!-- /.box-body -->
	            </div>
            <% } %>
            
       <!-- ===start parvez date: 29-07-2022=== -->     
            <% if(strtUserType != null && strtUserType.equals(IConstants.EMPLOYEE)) { %>
	            <div class="box box-primary">
	            	<div class="box-header with-border">
	            		<h3 class="box-title">Select Income Text Type</h3>
	            	</div>
		            <!-- /.box-header -->
		            <%
		            	Map<String, String> hmEmpAccessData = (Map<String, String>) request.getAttribute("hmEmpAccessData");
		    			if(hmEmpAccessData == null) hmEmpAccessData = new HashMap<String, String>();
		    			
		    			String strSlabType = hmEmpAccessData.get(strEmpId);
		    			String strStandardCheck = "";
						String strNewCheck = "";
						String strStandardNewVal = "0";
						if(strSlabType != null && uF.parseToInt(strSlabType)==0) {
							strStandardCheck = "checked";
							strStandardNewVal = "0";
						}
						if(strSlabType != null && uF.parseToInt(strSlabType)==1) {
							strNewCheck = "checked";
							strStandardNewVal = "1";
						}
		            %>
		            <div class="box-body" style="padding: 5px; overflow-y: auto;max-height:400px;">
		            	<s:form name="frmAddIncomeTaxSlab" id="frmAddIncomeTaxSlab" action="InvestmentForm" method="post" enctype="multipart/form-data">
			            	<!-- <input type="hidden" name="operation" id="operation" value="A" />
			            	<input type="hidden" name="dataType" id="dataType" value="ITS" /> -->
			            	<input type="hidden" name="chboxStandardNew" id="chboxStandardNew" value="<%=strStandardNewVal %>"/>
			            	<table id="lt" class="table table-noborder" style="width:100%; margin-top: 10px; clear:both;">
			            		<thead>
									<tr>
										<th class="alignCenter" nowrap>Old Regime<br/></th>
										<th class="alignCenter" nowrap>New Regime<br/></th>
									</tr>
								</thead>
			            		<tbody>
			            			<tr>
			            				<td class="alignCenter"><%-- <input type="radio" id="chboxStandard_<%=innerList.get(0) %>" name="chboxStandardNew_<%=innerList.get(0) %>" value="0" <%=strStandardCheck %> onclick="selectOne(this,'chboxStandard_<%=innerList.get(0) %>')" /> --%>
											<input type="checkbox" name="chboxStandard" id="chboxStandard" style="width:10px; height:10px" <%=strStandardCheck %> onclick="selectOne(this, '0')" />
										</td>
										<td class="alignCenter"><%-- <input type="radio" id="chboxNew_<%=innerList.get(0) %>" name="chboxStandardNew_<%=innerList.get(0) %>" value="1" <%=strNewCheck %> onclick="selectOne(this,'chboxNew_<%=innerList.get(0) %>')" /> --%>
											<input type="checkbox" name="chboxNew" id="chboxNew" style="width:10px; height:10px" <%=strNewCheck %> onclick="selectOne(this, '1')" />
										</td>
			            			</tr>
			            			<tr>
			            			<td colspan="2" align="right">
			            				<input type="button" value="Submit" class="btn btn-primary" onclick="submitIncomTaxSlab('ITS','A');"/>
			            			</td>
			            			</tr>
			            		</tbody>
			            	</table>
		            	</s:form>
		            </div>
		            <!-- /.box-body -->
		       </div>
	       <% } %>
	 <!-- ===end parvez date: 29-07-2022=== -->      
            
            <div class="box box-primary">
                <%	List alMonth = (List)request.getAttribute("alMonth");
					List alEmp = (List)request.getAttribute("alEmp");
					Map hmTDSPaidEmp = (Map)request.getAttribute("hmTDSPaidEmp");
					
					Map hmTDSEmp = (Map)request.getAttribute("hmTDSEmp");
				
				 %>
                <div class="box-header with-border">
                    <h3 class="box-title"><% if(strtUserType != null && strtUserType.equals(IConstants.EMPLOYEE)) { %>My<%} %> TDS Projection</h3>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto;">
                    <table class="table">
					<%
		    		int i=0;
		    		for(i=0; alEmp!=null && i<alEmp.size(); i++) {
		    			double total=0;
		    		%>
    				<% for(int j=0; j<alMonth.size(); j++) {
    					String strTDSStatus = "Projected";
    					if(hmPrevOrgTDSDetails!=null && uF.parseToDouble(hmPrevOrgTDSDetails.get((String)alEmp.get(i)+"_TDS_AMT_"+(String)alMonth.get(j)))>0) {
    						strTDSStatus = "Paid in Prev Org";
    					}
    				%>
    				<tr>
   						<th class="reportHeading alignCenter" nowrap="nowrap"><%=uF.getMonth(uF.parseToInt((String)alMonth.get(j)))%></th>
						<td nowrap="nowrap">
						<% if(hmPrevOrgTDSDetails!=null && uF.parseToDouble(hmPrevOrgTDSDetails.get((String)alEmp.get(i)+"_TDS_AMT_"+(String)alMonth.get(j)))>0) {
    						strTDSStatus = "Paid in Prev Org";
    					 %>
    					 <%=uF.parseToDouble(hmPrevOrgTDSDetails.get((String)alEmp.get(i)+"_TDS_AMT_"+(String)alMonth.get(j))) %>
						<%} else if(hmTDSPaidEmp.containsKey((String)alEmp.get(i)+"_"+(String)alMonth.get(j))) {
							total+=uF.parseToDouble((String)hmTDSPaidEmp.get((String)alEmp.get(i)+"_"+(String)alMonth.get(j)));
							strTDSStatus = "Paid";
						%>
							<%=hmTDSPaidEmp.get((String)alEmp.get(i)+"_"+(String)alMonth.get(j)) %>
						<% } else if(j<alMonth.size()-1) {
							total+=uF.parseToDouble((String)hmTDSEmp.get((String)alEmp.get(i)+"_"+(String)alMonth.get(j)));%>
						<%=uF.showData((String)hmTDSEmp.get((String)alEmp.get(i)+"_"+(String)alMonth.get(j)), "0") %>
							
						<% } else if(j==alMonth.size()-1) {
							total+=uF.parseToDouble((String)hmTDSEmp.get((String)alEmp.get(i)+"_"+(String)alMonth.get(j)));
						%>
							<%=uF.showData((String)hmTDSEmp.get((String)alEmp.get(i)+"_"+(String)alMonth.get(j)), "0") %><sup>*</sup>
						<% } %>	
						</td>
						<td nowrap="nowrap"><%=strTDSStatus %></td>
					</tr>
					<% } %>
		    		<tr>
			    		<th class="reportHeading alignCenter" nowrap="nowrap">Total</th>
			    		<td nowrap="nowrap"><%=uF.formatIntoTwoDecimalWithOutComma(total) %></td>
			    		<td nowrap="nowrap">&nbsp;</td>
		    		</tr>
	    			<% } %>
		    		
				</table>
				<br/>
				<div>TDS projection is based on approved IT declaration.<br/>
					<%if(uF.parseToInt(strEmpId) > 0){ %>
						<a href="javascript:void(0);" onclick="viewEmpTDSProjection('<%=uF.showData(strEmpName,"") %>','<%=strEmpId %>','<%=uF.showData(strFinancialYearStart, "") %>', '<%=uF.showData(strFinancialYearEnd, "") %>')">Check TDS summary with calculation.</a>
					<%} %>
				</div>
			</div>
			<!-- /.box-body -->
            </div>
            
        </section>
        
		<div class="clr"></div>
		<%} %><!-- Ended By Dattatray Date:21-10-21 -->
	<script>
		$(window).load(function(){
			$(".validateNumber").prop('type', 'number');$(".validateNumber").prop('step', 'any');
		});
	</script>

	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">Candidate Information</h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>