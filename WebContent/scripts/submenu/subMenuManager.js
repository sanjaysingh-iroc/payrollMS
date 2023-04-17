function fun(){

		var url = document.URL;
		var slashIndex = url.lastIndexOf('/');
		var dotIndex = url.lastIndexOf('.', slashIndex);
		var action;
		var arrow = "<a href=\"#\"> > </a>";
		var top_link_menuSelection1="top_link menuSelection1";
		var top_link_menuSelection2="topR_link menuSelection2";

		
		
		var arrMenu = new Array("<a href=\"MyDashboard.action\">Dashboard</a>",
				"<a href=\"#\">Time Management</a>",
 				"<a href=\"#\">Roster Management</a>",
 				"<a href=\"MyProfile.action\">My Profile</a> ",
 				"<a href=\"Announcements.action\">Notices</a> ",
 				"<a href=\"#\">Payslips</a>",
 				"<a href=\"ChangePassword.action\">Change Password</a>",
 				"<a href=\"MenuNavigationInner.action?NN=R10\">Reports</a>");		

		
		var arrSubMenu_2 = new Array(
				"<a href=\"PayCycleList.action?T=C\">Clock Entries</a>",
				"<a href=\"PayCycleList.action?T=O\">Clock Of/On Exception</a>",
				"<a href=\"PayCycleList.action?T=T\">Timesheet</a>");
		
		var arrSubMenu_3 = new Array(
				"<a href=\"RosterReport.action\">My Staff Roster</a>",
				"<a href=\"RosterDependency.action\">My Roster Dependency</a>",
				"<a href=\"UploadRoster.action\">Import Roster Data</a>"); 

		
		var arrSubMenu_7 = new Array(
				"<a href=\"MenuNavigationInner.action?NN=R11\">Employee Management</a>",
				"<a href=\"MenuNavigationInner.action?NN=R12\">Exceptions</a>",
				"<a href=\"MenuNavigationInner.action?NN=R13\">KPI</a>",
				"<a href=\"MenuNavigationInner.action?NN=R14\">Roster vs Actual</a>");
		
		var arrSubSubMenu_7_1 = new Array(
		"<a href=\"EmployeeReport.action\">Employee</a>",
		" <a href=\"Announcements.action\">Notices</a>");
		
		var arrSubSubMenu_7_2 = new Array(
				"<a href=\"LateClockReport.action\">Daywise</a>",
				" <a href=\"LateEmpClockReport.action\">per employee/day</a>");

		var arrSubSubMenu_7_3 = new Array(
				"<a href=\"ServicesEmployeeReport.action\">Employee vs cost center</a>",
				" <a href=\"AdditionalEmpHours.action\">Additional Hours</a>",
				" <a href=\"EmployeeHours.action?P=ESH\">Employee Service Hours</a>");

		var arrSubSubMenu_7_4 = new Array(
				"<a href=\"RosterActualHours.action\">Daywise</a>",
				" <a href=\"RosterActualHoursEmp.action\">Per employee/day</a>",
				" <a href=\"EmployeeHours.action?P=EH\">Per employee/paycycle</a>");

		
		var arrSubSubMenu_7_5 = new Array(
				 "<a href=\"ServicesEmployeeReport.action\">Employee vs Cost Centre</a>",
				"<a href=\"PayCycleList.action?T=RRA\">Roster vs Actual</a>",
				" <a href=\"PayCycleList.action?T=RE\">Exceptions</a>",
				" <a href=\"EmployeeHours.action?P=EH\">Employee Hours</a>",
				"  <a href=\"EmployeeHours.action?P=ESH\">Employee Service Hours</a>"
				);			


		var divStrart ="<div class='linkBack'>";
		var divEnd ="</div>";
		
		//if(dotIndex.indexOf(-1){
			//action = url.substring(slashIndex + 1);
		action = url;
			
			//id= dashId
			if (action.indexOf("MyDashboard.action")>=0) {
				 document.getElementById('dashboard').setAttribute("class",top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[0]+divEnd;
				}
		
			else if (action.indexOf("PayCycleList.action?T=C")>=0 || action.indexOf("PayCycleList.action?T=O")>=0 || action.indexOf("PayCycleList.action?T=T")>=0
					||  action.indexOf("EmployeeReportPayCycle.action")>=0 ||  action.indexOf("UpdateClockEntries.action?PAY=Y")>=0
					|| action.indexOf("ClockEntries.action?T")>=0 || action.indexOf("ClockEntries.action?O")>=0 || action.indexOf("ClockEntries.action?C")>=0) {
				
				
				
				document.getElementById('timeId').setAttribute("class",top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[1]+divEnd;
			}
			
			//Roster

			else if (action.indexOf("RosterReport.action")>=0 || action.indexOf("RosterDependency.action")>=0 || action.indexOf("UploadRoster.action")>=0) {
				document.getElementById('RosterId').setAttribute("class",top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[2]+divEnd;
			}
			
			
			else if (action.indexOf("MyProfile.action")>=0 || action.indexOf("AddEmployeeE.action?P=Y")>=0) {
				 document.getElementById('profId').setAttribute("class",  top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[3]+divEnd;
				}
			
			else if (action.indexOf("MyPay.action")>=0 || action.indexOf("HolidayReport.action")>=0) {
				 document.getElementById('payslipId').setAttribute("class", top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[5]+divEnd;
				}
			else if (action.indexOf("ChangePassword.action")>=0) {
				 document.getElementById('changePasswd').setAttribute("class", top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[6]+divEnd;
				}
			 
					
	//Reports
			
			
	//Emp report
			
			
			 else if (action.indexOf("/EmployeeReport.action")>=0 || action.indexOf("/Announcements.action")>=0) {
					document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
					document.getElementById('menuLink').innerHTML =divStrart+arrMenu[7]+arrow+ arrSubMenu_7[0]+divEnd;
				}
					
	//Exceptions report
				
				
		 else if (action.indexOf("/LateClockReport.action")>=0 || action.indexOf("/LateEmpClockReport.action")>=0) {
				document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[7]+arrow+ arrSubMenu_7[1]+divEnd;
			}

	//KPI report
					
			else if (action.indexOf("/AmountPaidEmployee.action")>=0 || action.indexOf("/ServicesEmployeeReport.action")>=0 || action.indexOf("/AdditionalEmpHours.action")>=0 || action.indexOf("/EmployeeHours.action?P=ESH")>=0) {
				
				document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[7]+arrow+ arrSubMenu_7[2]+divEnd;
			}
					
	//Roster vs Actual
					
				else if (action.indexOf("/RosterActualHours.action")>=0 || action.indexOf("/RosterActualHoursEmp.action")>=0 || action.indexOf("/EmployeeHours.action?P=EH")>=0) {
					
					document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
					document.getElementById('menuLink').innerHTML =divStrart+arrMenu[7]+arrow+ arrSubMenu_7[3]+divEnd;
				}
				 
	

				else if (action.indexOf("/MenuNavigationInner.action?NN=ME11")>=0 || action.indexOf("/MenuNavigationInner.action?NN=ME12")>=0 || action.indexOf("/MenuNavigationInner.action?NN=ME13")>=0 || action.indexOf("MenuNavigationInner.action?NN=ME14")>=0 || action.indexOf("/MenuNavigationInner.action?NN=ME15")>=0) {
					document.getElementById('masterId').setAttribute("class",top_link_menuSelection1);
					document.getElementById('menuLink').innerHTML =divStrart+arrMenu[1]+divEnd;
				}else if (action.indexOf("/MenuNavigationInner.action?NN=R11")>=0 || action.indexOf("/MenuNavigationInner.action?NN=R12")>=0 || action.indexOf("/MenuNavigationInner.action?NN=R13")>=0 || action.indexOf("/MenuNavigationInner.action?NN=R14")>=0) {
					document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
					document.getElementById('menuLink').innerHTML =divStrart+arrMenu[7]+divEnd;
				}
			
	 				
	//	}
	}