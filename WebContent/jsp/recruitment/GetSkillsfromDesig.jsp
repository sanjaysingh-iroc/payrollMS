<%@ taglib prefix="s" uri="/struts-tags"%>

<s:select cssClass="validateRequired" name="skills"  theme="simple"
							listKey="skillsName" listValue="skillsName" list="skillslist"
							 multiple="true" cssClass="chosen-select-no-results" />
	

 <script type="text/javascript">
   var config = {
			  '.chosen-select'           : {},
			  '.chosen-select-deselect'  : {allow_single_deselect:true},
			  '.chosen-select-no-single' : {disable_search_threshold:10},
			  '.chosen-select-no-results': {no_results_text:'Oops, nothing found!'},
			  '.chosen-select-width'     : {width:"95%"}
			}
			for (var selector in config) {
			  $(selector).chosen(config[selector]);
			}
</script> 	