<%@ taglib prefix="s" uri="/struts-tags"%>


 


<s:if test="dashboardClockLabel=='Clock On'">

<a onclick="" href="#">    
<s:property value="dashboardClockLabel"/>
</a>
</s:if>


<s:elseif  test="dashboardClockLabel=='Clock Off'">
<a onclick="" href="#">
<s:property value="dashboardClockLabel"/>
</a>
</s:elseif >



<s:else>
<a href="#">
<s:property value="dashboardClockLabel"/>
</a>
</s:else>








