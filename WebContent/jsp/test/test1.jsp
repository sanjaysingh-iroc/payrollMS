<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<sx:head/>


<s:file name="empImage" label="Employee Image" />

<s:url id="d_url" action="DetailAction" /> 
<sx:div id="details" href="%{d_url}" listenTopics="show_states" formId="frm_emp" showLoadingText=""></sx:div>