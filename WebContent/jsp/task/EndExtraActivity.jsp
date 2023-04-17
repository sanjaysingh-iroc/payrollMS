<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>


<form action="TaskUpdateTime.action?id=${id}" method="post">
	<s:hidden name="type" value="end" />
	<center>
	<table>
		<tr>
			<s:textfield name="per" label="Enter completion status of task (%)"/>
		</tr>
	</table>
	</center>
	<center>
		<input type="submit" class="input_button" value="end" />
	</center>
</form>
     