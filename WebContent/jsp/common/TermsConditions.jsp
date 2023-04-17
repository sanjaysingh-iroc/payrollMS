<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Terms And Conditions" name="title"/>
</jsp:include>
<%@ taglib uri="/struts-tags" prefix="s"%>


<script>
jQuery(document).ready(function(){
    jQuery("#formID").validationEngine();
});

</script>


<style>

ol{
padding-right:30px;
margin-left:60px;
}
 
ol li{
list-style: decimal outside none;
padding-left:20px;
padding-top: 20px;
}

</style>



<div class="reportWidth">


<s:form action="TermsConditions" id="formID" theme="simple">




<p style="font-size:40px;font-weight:bold;text-align:center;font-family:verdana;padding:10px;margin:10px">Trial Agreement - Terms and Conditions</p>

<p style="padding-bottom: 20px; font-style: italic; font-size: 16px;">Please read the following text carefully:</p> 

<p>
THIS SOFTWARE TRIAL AGREEMENT (this "Trial Agreement") is by and between Workrig Solutions Pvt. Ltd.. ("TSPL") and the company identified for the trial request ("Company").
</p>

<p>&nbsp;</p>

<p>
Company requests that DSSLP arrange a week (7) day trial period (the "Trial Period") for Company to evaluate the Workrig software product indicated on the trial request (the "Software") in accordance with the terms and conditions set forth below, and, therefore, the parties agree as follows:
</p>

<ol>
<li>Limited Use. DSSLP grants to Company a non exclusive, non-transferable, revocable right to use the Software on Company's Server(s) and system(s) specified in the submitted trial request only (collectively, the "System"), solely for the purpose of evaluation for possible licensing from DSSLP and only for the Trial Period, which period shall begin upon providing access in the form of User name and Password to the Company unless otherwise indicated on the trial request. Company shall not modify the Software in any way and Company shall not copy concepts, copy code, sell, rent, sublicense, give away or otherwise transfer the Software or any portion thereof, provided, however, that Company may make administrative changes such as creating more user's or profiles within the Software only to the extent necessary to use it on Company's System in accordance with the limited right granted hereunder, and the Software shall not be installed or used in, for or by any service bureau, time-sharing or outsourcing service. Company further agrees that it shall not reverse-compile, disassemble, reverse-assemble or trace the Software. 
Company shall not rely upon the Software for business applications during the Trial Period and Company shall be solely responsible for the protection and backup of its data used in conjunction with the Software during the Trial Period. As used in this Trial Agreement - which includes the submitted trial request - the word "Software" includes all programs and documentation distributed to Company (including all tools, utilities, algorithms, ideas and concepts embodied therein) and any related information disclosed by DSSLP to Company and any portion thereof in any form.</li>
<li>Ownership and Nondisclosure. Company acknowledges and agrees that DSSLP is the owner, or the authorized distributor of, the Software and, except as set out above, this Trial Agreement does not grant to Company any right, title or interest in or to this Software, including in or to any intellectual property or proprietary right in or to the Software, and Company shall not obtain or claim any right, title or interest in or to the Software, or any portion thereof, or any intellectual property or proprietary right therein. Company further acknowledges and agrees that the Software embodies confidential information and trade secrets of DSSLP or the owner and Company shall not disclose any portion of the Software to any third party, including any person who is not Company's full-time employee having a need to know for the purpose of this Trial Agreement, unless any such third party or person has executed a nondisclosure agreement reasonably acceptable to DSSLP and covering the Workrig Software.</li>
<li>DISCLAIMER. DSSLP MAKES NO WARRANTIES, EXPRESS OR IMPLIED, OF ANY KIND, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, WITH RESPECT TO THE SOFTWARE PROVIDED HEREUNDER. Without limiting any other provision of this Trial Agreement, DSSLP shall not be liable for any direct, special, indirect or consequential damages resulting from the installation, use performance or nonperformance of the Software during the Trial Period.</li>
<li>Term. This Trial Agreement shall commence as of the date on which the trial request has been received by DSSLP and, unless terminated earlier in accordance with this Agreement or extended agreement of the parties, shall automatically terminate at 11:59 p.m. on the seventh (7th) day of the, access to the Trial Period. Any provision of this Trial Agreement intended to survive the termination of this Agreement shall survive termination of this Trial Agreement and continue in full force and effect. At the end of the Trial Period, Company shall either purchase a license to use the Software, in accordance with DSSLP' standard terms and conditions therefore, or shall return the use of the Software and related materials, to DSSLP with an executed letter, stating that Company has used the software during the trial period, and has maintained all clauses from &#35;1 to &#35;4.</li>

</ol> 






<s:checkbox name="strTermsConditions" cssClass="validateRequired" />
I accept the terms and conditions mentioned above.


<div style="width:100%;text-align:center">
<s:submit cssClass="input_button" value="Click to Accept Terms And Conditions"></s:submit>
</div>
</s:form>


<p style="padding:10px;font-weight:bold;">PLEASE PRINT THIS PAGE FOR YOUR RECORDS, USING YOUR BROWSER PRINT FUNCTION</p>

</div>