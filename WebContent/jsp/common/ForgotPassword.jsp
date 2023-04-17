<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

           
  <%!
String showMessage(String str){
	if(str!=null){
		return str;
	}else{
		return "";
	}
}
%>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title">Forgot Password</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <%=showMessage((String)request.getAttribute("MESSAGE")) %>
		           <s:form action="ForgotPassword" method="POST" theme="simple">
			           <table class="table table_no_border autoWidth">
				           <tr><td colspan="2">
				           <strong>Don't recall your password?</strong>
								<p>
								No problem! Just enter the email address that 
								you provided and we'll mail 
								you instructions for logging in.
								</p>
							<td></tr>
				           	<tr><td class="alignRight">Enter your registered email</td><td><input type="text" name="userEmail" id="ForgotPassword_userEmail" style="width: 300px !important;"></td></tr>
				           	<tr>
				           		<td colspan="2" class="alignCenter">
				           			<s:submit cssClass="btn btn-primary" value="Request Login Details" align="center"/>
				           			<a href="Login.action" class="btn btn-default">Back to Login</a>
				           		</td>
				           	</tr>
			           </table>
		    		</s:form>
                </div>
            </div>
        </section>
    </div>
</section>