<%@ taglib prefix="s" uri="/struts-tags"%>
<s:select cssClass="validateRequired" name="services"   theme="simple"
							listKey="serviceId" listValue="serviceName" headerKey=""
							headerValue="Select Service" list="serviceslist" />