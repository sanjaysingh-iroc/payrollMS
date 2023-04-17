<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript">
    $(document).ready(function() {

        $('a.poplight[href^=#]').click(function() {
            var popID = $(this).attr('rel'); //Get Popup Name
            var popURL = $(this).attr('href'); //Get Popup href to define size

            //Pull Query & Variables from href URL
            var query= popURL.split('?');
            var dim= query[1].split('&');
            var popWidth = dim[0].split('=')[1]; //Gets the first query string value

            //Fade in the Popup and add close button
            $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

            //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
            var popMargTop = ($('#' + popID).height() + 80) / 2;
            var popMargLeft = ($('#' + popID).width() + 80) / 2;

            //Apply Margin to Popup
            $('#' + popID).css({
                'margin-top' : -popMargTop,
                'margin-left' : -popMargLeft
            });

            //Fade in Background
            $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
            $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies

            return false;
        });

        //Close Popups and Fade Layer
        $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
            $('#fade , .popup_block').fadeOut(function() {
                $('#fade, a.close').remove();  //fade them both out
            });
            return false;
        });

    });
    
    $(function() {
        $( "#idDate" ).datepicker();
    });

</script>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Employee vs Services Report" name="title"/>
</jsp:include>



<form action="ServicesEmployeeReport.action" method="POST">
Select Date &nbsp;&nbsp;&nbsp;<input style="width:100px" type="text" id="idDate" name="strDate"/>
<input type="submit" value="Submit" class="button"/>
</form>

<div id="printDiv" class="leftbox reportWidth">

<display:table name="reportList" cellspacing="1" class="itis" export="true"
	pagesize="15" id="lt" requestURI="ServicesEmployeeReport.action">
	
	<display:setProperty name="export.excel.filename" value="EmployeeServiceReport.xls" />
	<display:setProperty name="export.xml.filename" value="EmployeeServiceReport.xml" />
	<display:setProperty name="export.csv.filename" value="EmployeeServiceReport.csv" />
	
	<display:setProperty name="paging.banner.item_name" value="employee rostered" />
	<display:setProperty name="paging.banner.items_name" value="employees rostered" />
	<display:setProperty name="basic.msg.empty_list" value="No employee rostered for today for any service." />
	
	<display:column title="Date" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<display:column title="Service"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	<display:column title="Emp Name"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
	<display:column title="Start Time" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
	<display:column title="End Time"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
		
</display:table>

</div>

