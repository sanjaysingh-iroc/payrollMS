<%@page import="com.konnect.jpms.select.FillFinancialYears"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function(){
		$('#closeButton1').click(function(){
			$(".modal-dialog2").removeAttr('style');
			$("#modalInfo2").hide();
	    });
	});
	
	function updateResource(desigID, yearStart, yearEnd, f_financialYearStart, f_financialYearEnd) {
		
		var resourceValue = "";
		var j = 0;
		for ( var i = 0; i < 12; i++) {
			j = i + 1;
			if (i > 2) {
				var valid = "require" + desigID + "" + j.toString() + "" + yearStart;
			} else {
				var valid = "require" + desigID + "" + j.toString() + "" + yearEnd;
			}
			var val = document.getElementById(valid).value;
			resourceValue = resourceValue + "," + val;
		}
		var finansyr = document.getElementById("finansyr").value;
		var orgid1 = document.getElementById("orgid").value;
		var lvlid = document.getElementById("lvlid").value;
		var currUserType = document.getElementById("currUserType").value;
		var fromPage = document.getElementById("fromPage").value;
		
		var action = "AddResourcePlanner.action?editResource="+ resourceValue + "&editResourceDesig=" + desigID+ "&editResourceYearStart=" + yearStart
			+ "&editResourceYearEnd=" + yearEnd + "&f_financialYearStart="+ f_financialYearStart + "&f_financialYearEnd="+ f_financialYearEnd
			+"&finansyr="+finansyr+"&orgid="+orgid1+"&lvlid="+lvlid+"&currUserType="+currUserType+"&fromPage="+fromPage;
		//alert("fromPage ===>> " + fromPage);
		if(fromPage !="" && fromPage == "WF") {
			$("#divWFResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: action,
				cache:false,
				success: function(result){
					$.ajax({
						url: 'ResourcePlanner.action?finansyr='+finansyr+'&orgid='+orgid1+'&lvlid='+lvlid+'&currUserType='+currUserType+'&fromPage='+fromPage,
						cache: true,
						success: function(result){
							$("#divWFResult").html(result);
				   		}
					});
		   		},
				error: function(result){
					$.ajax({
						url: 'ResourcePlanner.action?finansyr='+finansyr+'&orgid='+orgid1+'&lvlid='+lvlid+'&currUserType='+currUserType+'&fromPage='+fromPage,
						cache: true,
						success: function(result){
							$("#divWFResult").html(result);
				   		}
					});
		   		}
			});
			
		} else {
			window.location = action;
		}
		
	}

	function desigTotal(id, year, yearManipulation) {
		var total = 0;
		var j = 0;
		for ( var i = 0; i < 12; i++) {
			j = i + 1;
			var year1 = year;

			if (i < 3 && yearManipulation == 0) {
				year1 = parseInt(year1) + 1;
			} else if (i > 2 && yearManipulation == 1) {
				year1 = parseInt(year1) - 1;
			}
			var valid = "require" + id + "" + j.toString() + "" + year1;
			var val = document.getElementById(valid).value;

			if ((isNaN(parseInt(val)))) {
				document.getElementById(valid).value = '0';

				if (val.length > 0) {
					alert("Not a Number");
				}
			} else {
				document.getElementById(valid).value = parseInt(val);
			}
			if (i == 0) {
				total = parseInt(document.getElementById(valid).value);
			} else {
				total += parseInt(document.getElementById(valid).value);
			}
		}

		var totalID = 'total' + id;
		//===start parvez on 29/06/2021====
		//if (total > 0) { 
		if (total >= 0) {
		//===end parvez on 29/06/2021====
			document.getElementById(totalID).innerHTML = parseInt(total);
		}
	}

	function getDesignationDetailsPopup(desigId, desigName) {
		var dialogEdit = '#modalBody2';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo2").show();
		$('.modal-title1').html('' + desigName);

		$.ajax({
			url : "DesignationDetails.action?desig_id=" + desigId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	List<List<String>> outerList = (List<List<String>>) request.getAttribute("designationList");

	String monthStart = (String) request.getAttribute("monthStart");
	String yearStart = (String) request.getAttribute("yearStart");
	String monthEnd = (String) request.getAttribute("monthEnd");
	String yearEnd = (String) request.getAttribute("yearEnd");

	String f_financialYearStart = (String) request.getAttribute("f_financialYearStart");
	String f_financialYearEnd = (String) request.getAttribute("f_financialYearEnd");
	String fromPage = (String) request.getAttribute("fromPage");
	
%>

<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			<div class="attendance">
				<s:form name="frm_AddResourcePlanner" action="AddResourcePlanner" id="frm_AddResourcePlanner" theme="simple">
					<input type="hidden" name="yearStart" value="<%=yearStart%>" />
					<input type="hidden" name="yearEnd" value="<%=yearEnd%>" />
					<s:hidden name="f_strFinancialYear"></s:hidden>
					<s:hidden name="f_financialYearStart"></s:hidden>
					<s:hidden name="f_financialYearEnd"></s:hidden>
					<s:hidden name="finansyr" id="finansyr"></s:hidden>
					<s:hidden name="orgid" id="orgid"></s:hidden>
					<s:hidden name="lvlid" id="lvlid"></s:hidden>
					<s:hidden name="currUserType" id="currUserType"></s:hidden>
					<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>"/>
					<table width="100%" cellspacing="0" cellpadding="2" align="left"
						class="table table-bordered">
						<tbody>
							<tr class="darktable">
								<td style="text-align: center;">Designation</td>
								<td style="text-align: center;">Total</td>
								<td style="width: 75px !important; text-align: center;">April</td>
								<td style="width: 75px !important; text-align: center;">May</td>
								<td style="width: 75px !important; text-align: center;">June</td>

								<td style="width: 75px !important; text-align: center;">July</td>
								<td style="width: 75px !important; text-align: center;">August</td>
								<td style="width: 75px !important; text-align: center;">September</td>

								<td style="width: 75px !important; text-align: center;">October</td>
								<td style="width: 75px !important; text-align: center;">November</td>
								<td style="width: 75px !important; text-align: center;">December</td>

								<td style="width: 75px !important; text-align: center;">January</td>
								<td style="width: 75px !important; text-align: center;">February</td>
								<td style="width: 75px !important; text-align: center;">March</td>
								<td style="width: 75px !important; text-align: center;">Edit</td>
							</tr>

							<%
								Map<String, String> hmResourceReq = (Map<String, String>) request.getAttribute("hmResourceReq");
								Map<String, String> hmResourceTotal = (Map<String, String>) request.getAttribute("hmResourceTotal");
								Map<String, String> hmCheckDesigData = (Map<String, String>) request.getAttribute("hmCheckDesigData");
								String currentMonth = (String) request.getAttribute("currentMonth");
								String currentYear = (String) request.getAttribute("currentYear");

								for (int i = 0; outerList != null && i < outerList.size(); i++) {
									List<String> innerList = outerList.get(i);
							%>
							<tr class="lighttable">
								<td nowrap="nowrap">
									<input type="hidden" name="desigid" value="<%=uF.showData(innerList.get(0), "0")%>" /> 
									<a href="javascript:void(0);" onclick="getDesignationDetailsPopup('<%=innerList.get(0)%>','<%="[" + innerList.get(1) + "] " + innerList.get(2)%>');"><%="[" + innerList.get(1) + "] " + innerList.get(2)%></a>
								</td>

								<td align="center">
									<div id="total<%=innerList.get(0)%>"><%=uF.showData(hmResourceTotal.get(innerList.get(0)), "0")%></div>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 4 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
									<%=uF.showData(hmResourceReq.get(innerList.get(0) + "4"), "0")%>
									<% } else { %>
									<input type="text" name="require<%=innerList.get(0) + "4" + yearStart%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "4"), "0")%>" id="require<%=innerList.get(0) + "4" + yearStart%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearStart%>',0)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 5 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "5"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "5" + yearStart%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "5"), "0")%>" id="require<%=innerList.get(0) + "5" + yearStart%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearStart%>',0)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 6 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "6"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "6" + yearStart%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "6"), "0")%>" id="require<%=innerList.get(0) + "6" + yearStart%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearStart%>',0)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 7 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "7"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "7" + yearStart%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "7"), "0")%>" id="require<%=innerList.get(0) + "7" + yearStart%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearStart%>',0)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 8 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "8"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "8" + yearStart%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "8"), "0")%>" id="require<%=innerList.get(0) + "8" + yearStart%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearStart%>',0)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 9 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "9"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "9" + yearStart%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "9"), "0")%>" id="require<%=innerList.get(0) + "9" + yearStart%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearStart%>',0)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 10 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "10"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "10" + yearStart%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "10"), "0")%>" id="require<%=innerList.get(0) + "10" + yearStart%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearStart%>',0)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 11 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData( hmResourceReq.get(innerList.get(0) + "11"), "0")%>
									<% } else { %> 
										<input type="text" name="require<%=innerList.get(0) + "11" + yearStart%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "11"), "0")%>" id="require<%=innerList.get(0) + "11" + yearStart%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearStart%>',0)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 12 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "12"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "12" + yearStart%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "12"), "0")%>" id="require<%=innerList.get(0) + "12" + yearStart%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearStart%>',0)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 1 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "1"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "1" + yearEnd%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "1"), "0")%>" id="require<%=innerList.get(0) + "1" + yearEnd%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearEnd%>',1)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 2 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "2"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "2" + yearEnd%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "2"), "0")%>" id="require<%=innerList.get(0) + "2" + yearEnd%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearEnd%>',1)" />
									<% } %>
								</td>

								<td align="center">
									<% if (uF.parseToInt(currentMonth) > 3 && uF.parseToInt(currentYear) >= uF.parseToInt(yearEnd)) { %>
										<%=uF.showData(hmResourceReq.get(innerList.get(0) + "3"), "0")%>
									<% } else { %>
										<input type="text" name="require<%=innerList.get(0) + "3" + yearEnd%>" value="<%=uF.showData(hmResourceReq.get(innerList.get(0) + "3"), "0")%>" id="require<%=innerList.get(0) + "3" + yearEnd%>" style="width: 36px !important;" onkeyup="desigTotal('<%=innerList.get(0)%>','<%=yearEnd%>',1)" />
									<% } %>
								</td>

								<% if (hmResourceReq.keySet().size() == 0) { %>
								<td align="center"></td>
								<% } else if (hmCheckDesigData.get(innerList.get(0)) == null || hmCheckDesigData.get(innerList.get(0)).isEmpty() || hmCheckDesigData.get(innerList.get(0)).equals("")) { %>
								<td align="center">
									<input type="button" name="add" id="" class="btn btn-primary" value="SAVE" onclick="updateResource('<%=innerList.get(0)%>','<%=yearStart%>','<%=yearEnd%>','<%=f_financialYearStart%>','<%=f_financialYearEnd%>');" />
								</td>
								<% } else { %>
								<td align="center">
									<input type="button" name="update" id="" class="btn btn-primary" value="UPDATE" onclick="updateResource('<%=innerList.get(0)%>','<%=yearStart%>','<%=yearEnd%>','<%=f_financialYearStart%>','<%=f_financialYearEnd%>');" />
								</td>
								<% } %>
							</tr>
							<% } %>
						</tbody>
					</table>
					<div>
						<% if (hmResourceReq.keySet().size() == 0) { %>
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
						<% } %>
					</div>
				</s:form>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	</section>
</div>
</section>

<div class="modal" id="modalInfo2" role="dialog">
    <div class="modal-dialog modal-dialog2">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title1">Employee Information</h4>
            </div>
            <div class="modal-body1" id="modalBody2" style="height:auto;overflow-y:auto;padding-left: 25px;padding-top: 20px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>
<%if(fromPage != null && fromPage.equalsIgnoreCase("WF")) {%>
	 $("#frm_AddResourcePlanner").submit(function(event){
		 event.preventDefault();
		 var form_data = $("#frm_AddResourcePlanner").serialize();
		 $("#divWFResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'AddResourcePlanner.action',
				data:form_data+"&submit=Save",
				success: function(result){
					$("#divWFResult").html(result);
		   		},
				error: function(result){
					$("#divWFResult").html(result);
		   		}
			});
	 });
<%}%>
</script>