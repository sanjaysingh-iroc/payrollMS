<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
	$(function () {
		$("#strSalaryHeadId").multiselect().multiselectfilter();
	});

</script>

<s:select theme="simple" name="strSalaryHeadId" id="strSalaryHeadId" list="salaryHeadList" listKey="salaryHeadId"
     listValue="salaryHeadName" key="" multiple="true" size="4"/>