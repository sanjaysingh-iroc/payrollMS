<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@page import="java.util.ArrayList" buffer="128kb" %>
<%@page import="java.sql.*"%>
<%@ page import="com.konnect.jpms.util.Database"%>

<title>Insert title here</title>
</head>
<body>
<% System.out.println("All saved! refresh the page to see the changes"+request.getAttribute("item"));%>
<script type="text/javascript">
alert("I am in jsp");
</script>
	<%!
	   /* Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database dc= new Database();
		ArrayList draglist=null;*/
	%>
<%
/*draglist=new ArrayList<String>();
	
	try {
		draglist=(ArrayList)request.getAttribute("item");
		System.out.println("draglist1==========="+draglist);
		con = dc.makeConnection(con);
		if(draglist.contains("update")){
			int count = 1;
			for (int i=0;i<draglist.size();i++) {
				pst = con.prepareStatement("UPDATE dragdrop SET listorder = " + count + " WHERE id = " + i);
				rs = pst.executeQuery();
				count ++;	
			}
			System.out.println("All saved! refresh the page to see the changes");
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}finally{
		rs.close();
		pst.close();
	}
*/
%>
</body>
</html>