<%
String strReqAlphaValue = (String)request.getParameter("alphaValue");
if(strReqAlphaValue==null){
	strReqAlphaValue="";
}

%>





<input type="hidden" name="alphaValue">	

<ul>
<li><a onclick="temp('A');" <%=(strReqAlphaValue.equalsIgnoreCase("A"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=A">A</a></li>
<li><a onclick="temp('B');" <%=(strReqAlphaValue.equalsIgnoreCase("B"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=B">B</a></li>
<li><a onclick="temp('C');" <%=(strReqAlphaValue.equalsIgnoreCase("C"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=C">C</a></li>
<li><a onclick="temp('D');" <%=(strReqAlphaValue.equalsIgnoreCase("D"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=D">D</a></li>
<li><a onclick="temp('E');" <%=(strReqAlphaValue.equalsIgnoreCase("E"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=E">E</a></li>
<li><a onclick="temp('F');" <%=(strReqAlphaValue.equalsIgnoreCase("F"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=F">F</a></li>
<li><a onclick="temp('G');" <%=(strReqAlphaValue.equalsIgnoreCase("G"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=G">G</a></li>
<li><a onclick="temp('H');" <%=(strReqAlphaValue.equalsIgnoreCase("H"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=H">H</a></li>
<li><a onclick="temp('I');" <%=(strReqAlphaValue.equalsIgnoreCase("I"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=I">I</a></li>
<li><a onclick="temp('J');" <%=(strReqAlphaValue.equalsIgnoreCase("J"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=J">J</a></li>
<li><a onclick="temp('K');" <%=(strReqAlphaValue.equalsIgnoreCase("K"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=K">K</a></li>
<li><a onclick="temp('L');" <%=(strReqAlphaValue.equalsIgnoreCase("L"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=L">L</a></li>
<li><a onclick="temp('M');" <%=(strReqAlphaValue.equalsIgnoreCase("M"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=M">M</a></li>
<li><a onclick="temp('N');" <%=(strReqAlphaValue.equalsIgnoreCase("N"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=N">N</a></li>
<li><a onclick="temp('O');" <%=(strReqAlphaValue.equalsIgnoreCase("O"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=O">O</a></li>
<li><a onclick="temp('P');" <%=(strReqAlphaValue.equalsIgnoreCase("P"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=P">P</a></li>
<li><a onclick="temp('Q');" <%=(strReqAlphaValue.equalsIgnoreCase("Q"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=Q">Q</a></li>
<li><a onclick="temp('R');" <%=(strReqAlphaValue.equalsIgnoreCase("R"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=R">R</a></li>
<li><a onclick="temp('S');" <%=(strReqAlphaValue.equalsIgnoreCase("S"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=S">S</a></li>
<li><a onclick="temp('T');" <%=(strReqAlphaValue.equalsIgnoreCase("T"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=T">T</a></li>
<li><a onclick="temp('U');" <%=(strReqAlphaValue.equalsIgnoreCase("U"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=U">U</a></li>
<li><a onclick="temp('V');" <%=(strReqAlphaValue.equalsIgnoreCase("V"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=V">V</a></li>
<li><a onclick="temp('W');" <%=(strReqAlphaValue.equalsIgnoreCase("W"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=W">W</a></li>
<li><a onclick="temp('X');" <%=(strReqAlphaValue.equalsIgnoreCase("X"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=X">X</a></li>
<li><a onclick="temp('Y');" <%=(strReqAlphaValue.equalsIgnoreCase("Y"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=Y">Y</a></li>
<li><a onclick="temp('Z');" <%=(strReqAlphaValue.equalsIgnoreCase("Z"))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=Z">Z</a></li>
<li><a onclick="temp('');" <%=(strReqAlphaValue==null || "".equalsIgnoreCase(strReqAlphaValue))?"class=\"alpha_selected\"":"" %> href="#?alphaValue=">All</a></li>
</ul>
