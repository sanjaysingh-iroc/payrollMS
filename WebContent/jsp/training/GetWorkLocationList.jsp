<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


		<s:select theme="simple" name="plan_idwlocation" listKey="wLocationId" listValue="wLocationName"
		 		headerKey="" headerValue="Select Location" list="workList" key="" multiple="true" size="4" 
		 		value="plan_idwlocationvalue"/>