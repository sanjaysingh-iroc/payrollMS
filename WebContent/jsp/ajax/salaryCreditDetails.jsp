<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<s:if test="headName!=null">
<table>
		<tr>
			<td><s:label name="headName" ></s:label></td>
			
			<td>
				<select id='<s:property value="headName" />' style="width:50px" name="headType" onchange="changeLabelValue()">
      	 				<option value="P">%</option>
      	 				<option value="A">A</option>
	      	 	</select>
			</td>
			<td>
				<input type="text" id='txt_<s:property value="headName" />' name="headValue" 
						style="width:80px" onkeyup="changeLabelValue()"/ value='<s:property value="headValue" />' > 
				
				<%-- <s:textfield id="headValue" name="headValue" /> --%>
			</td>
			<td>
				<label style="color:green" id='lbl_<s:property value="headName" />' >0.00</label>	
			</td>
		</tr>
</table>
</s:if>