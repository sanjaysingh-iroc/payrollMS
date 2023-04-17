<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>

         <s:select theme="simple" name="f_WLocation" id="f_WLocation" listKey="wLocationId" cssStyle="width:160px" 
	             listValue="wLocationName" headerKey="-1" headerValue="All Locations" 
	             list="wLocationList" key="" onchange="getContent('myDiv','GetArrearEmployees.action?f_org='+document.frm_AddArear.f_org.options[document.frm_AddArear.f_org.selectedIndex].value+'&wlocationId='+document.frm_AddArear.f_WLocation.options[document.frm_AddArear.f_WLocation.selectedIndex].value+'&departmentId='+document.frm_AddArear.f_department.options[document.frm_AddArear.f_department.selectedIndex].value+'&serviceId='+document.frm_AddArear.f_service.options[document.frm_AddArear.f_service.selectedIndex].value+'&levelId='+document.frm_AddArear.f_level.options[document.frm_AddArear.f_level.selectedIndex].value)"/>
::::
          <s:select theme="simple" name="f_department" id="f_department" listKey="deptId" cssStyle="width:160px"
	             listValue="deptName" headerKey="-1" headerValue="All Departments" 
	             list="departmentList" key="" onchange="getContent('myDiv','GetArrearEmployees.action?f_org='+document.frm_AddArear.f_org.options[document.frm_AddArear.f_org.selectedIndex].value+'&wlocationId='+document.frm_AddArear.f_WLocation.options[document.frm_AddArear.f_WLocation.selectedIndex].value+'&departmentId='+document.frm_AddArear.f_department.options[document.frm_AddArear.f_department.selectedIndex].value+'&serviceId='+document.frm_AddArear.f_service.options[document.frm_AddArear.f_service.selectedIndex].value+'&levelId='+document.frm_AddArear.f_level.options[document.frm_AddArear.f_level.selectedIndex].value)"/>
::::
          <s:select theme="simple" name="f_service" id="f_service" listKey="serviceId" cssStyle="width:160px"
	             listValue="serviceName" headerKey="-1" headerValue="All Services" 
	             list="serviceList" key="" onchange="getContent('myDiv','GetArrearEmployees.action?f_org='+document.frm_AddArear.f_org.options[document.frm_AddArear.f_org.selectedIndex].value+'&wlocationId='+document.frm_AddArear.f_WLocation.options[document.frm_AddArear.f_WLocation.selectedIndex].value+'&departmentId='+document.frm_AddArear.f_department.options[document.frm_AddArear.f_department.selectedIndex].value+'&serviceId='+document.frm_AddArear.f_service.options[document.frm_AddArear.f_service.selectedIndex].value+'&levelId='+document.frm_AddArear.f_level.options[document.frm_AddArear.f_level.selectedIndex].value)"/>
::::
          <s:select theme="simple" name="f_level" id="f_level" listKey="levelId" cssStyle="width:160px"
	             listValue="levelCodeName" headerKey="-1" headerValue="All Levels" 
	             list="levelList" key="" onchange="getContent('myDiv','GetArrearEmployees.action?f_org='+document.frm_AddArear.f_org.options[document.frm_AddArear.f_org.selectedIndex].value+'&wlocationId='+document.frm_AddArear.f_WLocation.options[document.frm_AddArear.f_WLocation.selectedIndex].value+'&departmentId='+document.frm_AddArear.f_department.options[document.frm_AddArear.f_department.selectedIndex].value+'&serviceId='+document.frm_AddArear.f_service.options[document.frm_AddArear.f_service.selectedIndex].value+'&levelId='+document.frm_AddArear.f_level.options[document.frm_AddArear.f_level.selectedIndex].value)"/>
