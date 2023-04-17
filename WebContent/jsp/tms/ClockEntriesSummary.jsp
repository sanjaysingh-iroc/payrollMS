<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net/" prefix="display"%>
<s:head theme=""/>
 
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
        $( "#idTo" ).datepicker();
    });
 
</script>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
                
                <div class="box-header with-border">
                    <h3 class="box-title">Upcoming Event</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div style="margin-top:65px;" class="belowform">

					<s:form action="ApproveClockEntries" method="POST">
					
					<s:datetimepicker name="strFROM" label="From"  type="date"
							displayFormat="dd/MM/yyyy" required="true"></s:datetimepicker>
					
					<s:datetimepicker name="strTO" label="To"  type="date" 
							displayFormat="dd/MM/yyyy" required="true"></s:datetimepicker>
							
							
							<s:select cssStyle="width:100px" label="In OUT" name="inOUT"
							listKey="in_out_Id" listValue="in_out_Name" headerKey="A"
							headerValue="ALL" list="inOUTList" key=""
							required="true" />
							
							<s:select cssStyle="width:100px" label="Late/Early" name="le"
							listKey="leId" listValue="leName" headerKey="A"
							headerValue="ALL" list="leList" key=""
							required="true" />
							
					<s:hidden name="strFilterEmpId" />
					<s:submit value="Filter" cssClass="button" />
					
					</s:form>
					</div>
					
					<%
					String strText = "";
					String strUserType= (String)session.getAttribute(IConstants.USERTYPE);
					if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){
						strText = "My Issues";
					}else{
						strText = "Exception Report";
					}
					%>
					
					<h4 style="width:630px"> <%=strText%> </h4>
					
					
					
					
					<display:table width="65%" name="alreportList" cellspacing="1" class="itis"
						pagesize="15" id="lt" requestURI="ApproveClockEntries.action">
						
						<display:column title="Name"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
						<display:column title="Date"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
						<display:column title="Time"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
						<display:column title="In/Out"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
						<display:column title="Early/Late"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
						<display:column title="Emp Reason"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
						<display:column title="Hours Worked" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
						<display:column title="Action"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
					
					</display:table>
					
					</div>
					
					
					
					
					<div id="popup_name" class="popup_block">
					<h2>Please enter reason for denial.</h2>
					<form action="ApproveClockEntries.action" name="frmDeny" method="POST">
					
					
					
					<input type="hidden" name="DID"> <label>Employee's
					Reason</label> <textarea rows="5" cols="40" readonly="readonly" name="Empreason"></textarea><br />
					
					
					<label>Manager's Comment</label> <textarea rows="5" cols="40"
						name="reason"></textarea><br />
					<input type="submit" value="Submit" /></form>
					
					
					
					</div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>

