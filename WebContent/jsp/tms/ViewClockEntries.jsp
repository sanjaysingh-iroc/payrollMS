<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags"	prefix="s" %>
<%	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); %>

<s:head theme=""></s:head>

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
        $( "#idFrom" ).datepicker();
    });

    $(function() {
        $( "#idDate" ).datepicker();
    });

    
</script>


<%

String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String strTimeZone = (String)session.getAttribute(IConstants.O_TIME_ZONE);

String DATE = request.getParameter("DATE");
String date = request.getParameter("strDATE");
String strDate = request.getParameter("strDate");
UtilityFunctions uF = new UtilityFunctions();
if (strDate != null) {
	date = strDate;
} else if(DATE!=null) {
	date = DATE;
} else if (date == null || (date != null && date.equalsIgnoreCase(""))) {
	date = uF.getDateFormat(uF.getCurrentDate(strTimeZone) + "", IConstants.DBDATE, CF.getStrReportDateFormat());
}
%>



<%String strTitle = (strUserType!=null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))?"My ":"" +" Clock Entries for "+ date ;%>	   
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title"/>
</jsp:include>		

    <div class="leftbox">
			<h5>Please enter time in HH:mm format only. For example, 3:00PM should be entered as 15:00 and 3:00AM should be entered as 03:00</h5>
			<%=((request.getAttribute("TIMESHEET")!=null) ? request.getAttribute("TIMESHEET") : "") %>
	</div>
	
	<div class="rightbox">
		<s:form action="UpdateClockEntries" method="POST">
		<s:datetimepicker name="strDATE" label="Search" type="date" displayFormat="dd/MM/yyyy" required="true"></s:datetimepicker>
		<s:submit value="Search" cssClass="button" />
		</s:form>
	</div>     
