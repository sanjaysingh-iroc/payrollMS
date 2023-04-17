

 <%
 String docRetriveLocation = (String) request.getAttribute("imagepath");
 System.out.println("image jsp path--"+docRetriveLocation);
 %>
 
   <div class="gallery"> 
               <img height="550" width="780" src="<%=docRetriveLocation %>">
  </div>