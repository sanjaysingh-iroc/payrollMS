

<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready( function () {
	$("#submitButton").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	
</script>

<div class="cat_heading">
	<p><strong>Important Notice:</strong></p>
	<p style="background-color: yellow; padding: 4px; border: 1px solid #cccccc;">Please note, that if you wish to edit the document, every time you EDIT the system tags, instead of editing on the existing text, 
	please add or type a fresh rather than edit the existing text. Editing might provide a false entry due to which you can encounter errors 
	during printing of the documents. The System tags are provide below in [*****] format.</p>
</div>

<div class="row row_without_margin">
	<div class="col-lg-9 col-md-9 col-sm-9">
		<s:form theme="simple" id="formAddDocument" name="formAddDocument" action="AddDocument" method="POST" cssClass="formcss">
			<table class="table"> 
			<s:hidden name="strDocId"/>
			<s:hidden name="orgId"/>
			<s:hidden name="userscreen" />
			<s:hidden name="navigationId" />
			<s:hidden name="toPage" />
			
				<tr>
					<td width="17%">Document Name:<sup>*</sup></td>
					<td width="57%"><s:textfield name="strDocName" cssClass="validateRequired" cssStyle="width:535px"/></td>
				</tr>
				<tr>
					<td width="13%">Document Node:</td>
					<td width="57%"><s:select list="nodeList" listKey="nodeId" listValue="nodeName" name="strNode" headerKey="0" headerValue="Select Node"></s:select> </td>
				</tr>
				<tr>
					<td width="17%">Collateral Header:</td>
					<td width="57%"><s:select list="collHeaderList" listKey="collateralId" listValue="collateralName" name="strCollateralHeader" headerKey="0" headerValue="Select Header"></s:select> </td>
				</tr>
				<tr>
					<td width="13%">Collateral Footer:</td>
					<td width="57%"><s:select list="collFooterList" listKey="collateralId" listValue="collateralName" name="strCollateralFooter" headerKey="0" headerValue="Select Footer"></s:select> </td>
				</tr>
				   
				<tr> 
					<td>Document Body:</td>
					<td>&nbsp;</td>
				</tr> 
			
				<tr> 
					<td colspan="2"><s:textarea name="strDocBody" rows="50" cols="5" cssStyle="width:450px;height:700px;" id="editor1"> </s:textarea>
					
					<script>
						// Replace the <textarea id="editor1"> with an CKEditor instance.
						CKEDITOR.replace( 'editor1', {
							on: {
								focus: onFocus,
								blur: onBlur,
			
								// Check for availability of corresponding plugins.
								pluginsLoaded: function( evt ) {
									var doc = CKEDITOR.document, ed = evt.editor;
									if ( !ed.getCommand( 'bold' ) )
										doc.getById( 'exec-bold' ).hide();
									if ( !ed.getCommand( 'link' ) )
										doc.getById( 'exec-link' ).hide();
								}
							}
						});
						</script>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="2">
						<s:submit value="Publish Document" name="publishDoc" cssClass="btn btn-primary" id="submitButton"></s:submit> 
						<s:submit value="Save as Draft" name="draftDoc" cssClass="btn btn-primary" id="submitButton"></s:submit>
					</td>
				</tr>
			</table>
		</s:form>

	</div>
	
	
	<div class="col-lg-3 col-md-3 col-sm-3">
		<div>
	
					[EMPCODE]<br/>
					[SALUTATION]<br/>
					[EMPFNAME]<br/>
					[EMPMNAME]<br/>
					[EMPLNAME]<br/>
					[FATHER_NAME]<br/>
					[EMP_EMAIL_ID]<br/>
					[EMP_CONTACT_NO]<br/>
					[EMP_ADDRESS]<br/>
					[EMP_PANCARD_NO]<br/>
					[EMP_DATE_OF_BIRTH]<br/>
					[EMP_EMERGENCY_CONTACT_DETAILS]<br/>
					[MGRNAME]<br/>
					[JOINING_DATE]<br/>
					[HR_SIGNATURE]<br/>
					[AUTHORITY_SIGNATURE]<br/>
					[EMP_ORGANISATION]<br/>
					[ORGANISATION_ADDRESS]<br/>
					[LEGAL_ENTITY_NAME]<br/>
					[LEGAL_ENTITY_ADDRESS]<br/>
					[WLOCATION]<br/>
					[DEPARTMENT_NAME]<br/>
					[SBU_NAME]<br/>
					
					[LEVEL]<br/>
					[DESIGNATION]<br/>
					[REPORT_EMP_DESIGNATION]<br/>
					[GRADE]<br/>
					 
					[SKILLS]<br/>
					[EFFECTIVE_DATE]<br/>
					[PROMOTION_GRADE]<br/>
					[PROMOTION_LEVEL]<br/>
					[PROMOTION_DESIGNATION]<br/>
					[PROMOTION_DATE]<br/>
					[INCREMENT_AMOUNT_MONTH]<br/>
					[INCREMENT_AMOUNT_ANNUAL]<br/>
					[INCREMENT_PERCENT]<br/>
					[TERMINATION_DATE]<br/>
					[LAST_DATE_AT_OFFICE]<br/>
					[OFFER_ACCEPTANCE_LAST_DATE]<br/>
					
					[SALARY_STRUCTURE]<br/>
					[ANNUAL_BONUS_STRUCTURE]<br/>
					<!-- [PAY_STRUCTURE]<br/> -->
					
					[EMP_CTC]<br/>
					[EMP_ANNUAL_CTC]<br/>
					[PREVIOUS_MONTH_CTC]<br/>
					[PREVIOUS_ANNUAL_CTC]<br/>
					[PAYCYCLE]<br/>
					<!-- [EMP_KRAS]<br/> -->
					
					[PAYROLL_AMOUNT]<br/>
					[PAYROLL_AMOUNT_WORDS]<br/>
					[PAY_YEAR]<br/>
					[PAY_MONTH]<br/>
					
					<!-- [ALLOWANCE_AMOUNT]<br/>
					[ALLOWANCE_AMOUNT_WORDS]<br/> -->
					[PROBATION_END_DATE]<br/>
					[CONFIRMATION_DATE]<br/>
					
					<!-- [BANKS]<br/>
					[BANK_CODE]<br/> -->
					
					<br/>
					<strong>Tags only for Recruitment</strong><br/>					
					[RECRUITMENT_LEVEL]<br/>
					[RECRUITMENT_DESIG]<br/>
					[RECRUITMENT_GRADE]<br/>
					[RECRUITMENT_SKILL]<br/>
					[RECRUITMENT_WLOCATION]<br/>
					
					[SALUTATION]<br/>
					[CANDIDATE_ID]<br/><!-- Created By Dattatray Date : 05-10-21 -->
					[CANDIFNAME]<br/>
					[CANDILNAME]<br/>
					[CANDI_EMAIL_ID]<br/>
					[CANDI_CONTACT_NO]<br/>
					[CANDI_ADDRESS]<br/>
					[CANDI_CTC]<br/>
					[CANDI_CTC_WORDS]<br/>
					[CANDI_ANNUAL_CTC]<br/>
					[CANDI_JOINING_DATE]<br/>
					[CANDI_INTRVIEW_DATE]<br/>
					[OFFERED_SALARY_STRUCTURE]<br/>
					[HR_SIGNATURE]<br/>
					[AUTHORITY_SIGNATURE]<br/>
					[RECRUITER_SIGNATURE]<br/>
					[CLOSE_DATE]<br/>
					[ADDITIONAL_COMMENT]<br/>			<!-- Created by Parvez date: 10-01-2022 -->
					
					<br/>
					<strong>Tags only for Bank Statements</strong><br/>
					[DATE]<br/>
					<!-- [PAYCYCLE]<br/> -->
					[PAYROLL_AMOUNT]<br/>
					[PAYROLL_AMOUNT_WORDS]<br/>
					[PAY_YEAR]<br/>
					[PAY_MONTH]<br/>
					[LEGAL_ENTITY_NAME]<br/>
					<%=((request.getAttribute("sbBankCodes")!=null) ? request.getAttribute("sbBankCodes"): "") %>
					
	
	</div>
	</div>
</div>