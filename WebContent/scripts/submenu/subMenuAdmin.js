	function fun() {
		
		var url = document.URL;
		
		var slashIndex = url.lastIndexOf('/');
		var dotIndex = url.lastIndexOf('.', slashIndex);
		var action;
		var arrow = "<a href=\"#\"> > </a>";
		var top_link_menuSelection1="top_link menuSelection1";
		var top_link_menuSelection2="topR_link menuSelection2";


		var arrMenu = new Array("<a href=\"MyDashboard.action\">Dashboard</a>", 
 				"<a href=\"#\">Master Entries</a>",
 				"<a href=\"#\">Time Management</a> ",
 				"<a href=\"#\">Roster Management</a>",
 				"<a href=\"#\">Employee Management</a>",
 				"<a href=\"#\">Payroll Management</a>",
 				"<a href=\"#\">Reports</a> ");	
		 
		var arrSubMenu_2 = new Array("<a href=\"MenuNavigationInner.action?NN=ME11\">Company Location</a>",
				"<a href=\"MenuNavigationInner.action?NN=ME12\">Employee Management</a>",
				"<a href=\"MenuNavigationInner.action?NN=ME13\">Roster Management</a> ",
				"<a href=\"MenuNavigationInner.action?NN=ME14\">Policy Management</a> ",
				"<a href=\"MenuNavigationInner.action?NN=ME15\">Payroll Management</a>");
		
		var arrSubSubMenu_2_1 = new Array(
				"<a href=\"AddWLocation.action\">Add Office Location</a>",
				" <a href=\"AddCountry.action\">Add Country</a>",
				" <a href=\"AddState.action\">Add State</a>",
				"  <a href=\"AddCity.action\">Add City</a>");

		
		var arrSubSubMenu_2_2 = new Array(
				"<a href=\"AddService.action\">Add Cost Center</a>",
				"<a href=\"AddDepartment.action\">Add Department</a>",
				"<a href=\"AddNotice.action\">Add Notice</a>");

		
		var arrSubSubMenu_2_3 = new Array(
		"<a href=\"RosterPolicy.action\">Add Roster Policy</a>");
		
		var arrSubSubMenu_2_4 = new Array(
				"<a href=\"AddHolidays.action\">Add  Holidays</a>");

		var arrSubSubMenu_2_5 = new Array(
				" <a href=\"AddDeduction.action\">Add Tax Deduction</a>",
				"  <a href=\"AddAllowance.action\">Add Allowance</a>");

		var arrSubMenu_3 = new Array(
				"<a href=\"PayCycleList.action?T=C\">Clock Entries</a>",
				"<a href=\"PayCycleList.action?T=O\">Clock Of/On Exception</a>",
				"<a href=\"PayCycleList.action?T=T\">Timesheet</a>");

	
		var arrSubMenu_4 = new Array(
				"<a href=\"RosterReport.action\">Company Roster</a>",
				"<a href=\"RosterDependency.action\">Roster Dependency</a>",
				"<a href=\"UploadRoster.action\">Import Roster Data</a>"); 
		

	
		var arrSubMenu_5 = new Array(
				"<a href=\"AddUser.action\">Add User</a>",
				"<a href=\"AddEmployee.action\">Add Employee</a>",
				"<a href=\"ChangePassword.action\">ChangePassword</a>");

		
			var arrSubMenu_6 = new Array(
					"<a href=\"PayCycleList.action?T=A\">Approve Payroll</a>");

		var arrSubMenu_7 = new Array(
				"<a href=\"MenuNavigationInner.action?NN=R11\">Company</a>",
				"<a href=\"MenuNavigationInner.action?NN=R12\">Roster Management</a>",
				"<a href=\"MenuNavigationInner.action?NN=R13\">Policy Management</a>",
				"<a href=\"MenuNavigationInner.action?NN=R14\">Employee Management</a>",
				"<a href=\"MenuNavigationInner.action?NN=R15\">Exceptions</a>",
				"<a href=\"MenuNavigationInner.action?NN=R16\">KPI</a>",
				"<a href=\"MenuNavigationInner.action?NN=R17\">Roster vs Actual</a>",
				"<a href=\"MyPay.action\">Payslips & Myob</a>");
		

		var arrSubSubMenu_7_1 = new Array(
				"<a href=\"WLocationReport.action\">Work Locations</a>",
				" <a href=\"CountryReport.action\">Countries</a>",
				" <a href=\"StateReport.action\">States</a>",
				"  <a href=\"CityReport.action\">Cities</a>",
				" <a href=\"ServiceReport.action\">Cost Centers</a>",
				" <a href=\"DepartmentReport.action\">Departments</a>");
		
		var arrSubSubMenu_7_2 = new Array(
				"<a href=\"RosterPolicyReport.action\">Roster Policy</a>");
		
		var arrSubSubMenu_7_3 = new Array(
				"<a href=\"AllowanceReport.action\">Allowance Report</a>",
				" <a href=\"DeductionReport.action\">Deduction Report</a>",
				"  <a href=\"HolidayReport.action\">View Holidays</a>");

		var arrSubSubMenu_7_4 = new Array(
		"<a href=\"EmployeeReport.action\">Employee</a>",
		" <a href=\"UserReport.action\">Users</a>",
		" <a href=\"Announcements.action\">Notices</a>");

		var arrSubSubMenu_7_5 = new Array(
				 "<a href=\"ServicesEmployeeReport.action\">Employee vs Cost Centre</a>",
				"<a href=\"PayCycleList.action?T=RRA\">Roster vs Actual</a>",
				" <a href=\"PayCycleList.action?T=RE\">Exceptions</a>",
				" <a href=\"EmployeeHours.action?P=EH\">Employee Hours</a>",
				"  <a href=\"EmployeeHours.action?P=ESH\">Employee Service Hours</a>"
				);			

		
			var divStrart ="<div class='linkBack'>";
			var divEnd ="</div>";
		
		
	//	if (dotIndex == -1) {
			//action = url.substring(slashIndex + 1);
			action = url;
			
//Dashboard
			
			
			
			
			if (action.indexOf("MyDashboard.action")>=0)
			    {
				 document.getElementById("dashboard").setAttribute("class",top_link_menuSelection1);
				 document.getElementById("dashboard").className="top_link menuSelection1";
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[0]+divEnd;
				}
			
//Master Entries>>Company Location
			
			else if (action.indexOf("MenuNavigationInner.action?NN=ME11")>=0 || action.indexOf("MenuNavigationInner.action?NN=ME12")>=0 || action.indexOf("MenuNavigationInner.action?NN=ME13")>=0 || action.indexOf("MenuNavigationInner.action?NN=ME14")>=0 || action.indexOf("MenuNavigationInner.action?NN=ME15")>=0) 
			{
				
				document.getElementById('masterId').setAttribute("class",top_link_menuSelection1);
				document.getElementById("masterId").className="top_link menuSelection1";
				//document.getElementById('mentry').style.backgroundColor='#4FAFEE';
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[0]+ divEnd;
			} 
			
			else if (action.indexOf("AddWLocation.action")>=0 || action.indexOf("AddCountry.action")>=0 || action.indexOf("AddState.action")>=0 || action.indexOf("AddCity.action")>=0) 
			{
				
				document.getElementById('masterId').setAttribute("class",top_link_menuSelection1);
				document.getElementById("masterId").className="top_link menuSelection1";
				//document.getElementById('mentry').style.backgroundColor='#4FAFEE';
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[1]+arrow+arrSubMenu_2[0]+ divEnd;
			} 

//Master Entries>>Employee Management
			 else if (action.indexOf("AddService.action")>=0 || action.indexOf("AddDepartment.action")>=0 || action.indexOf("AddNotice.action")>=0) {
				 
				document.getElementById('masterId').setAttribute("class",top_link_menuSelection1)>=0;
				document.getElementById("masterId").className="top_link menuSelection1";
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[1]+arrow+ arrSubMenu_2[1]+divEnd;
			} 
			
////Master Entries>>Roster Management
			else if (action.indexOf("RosterPolicy.action")>=0) {
				document.getElementById('masterId').setAttribute("class",top_link_menuSelection1);
				document.getElementById("masterId").className="top_link menuSelection1";
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[1]+arrow+ arrSubMenu_2[2]+	divEnd;
			}
			
////Master Entries>>Policy Management
			else if (action.indexOf("AddHolidays.action")>=0) {
				document.getElementById('masterId').setAttribute("class",top_link_menuSelection1);
				document.getElementById("masterId").className="top_link menuSelection1";
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[1]+arrow+ arrSubMenu_2[3]+	divEnd;
			}
//Master Entries>>Payroll Management
			else if (action.indexOf("AddDeduction.action")>=0 || action.indexOf("AddAllowance.action")>=0) {
					document.getElementById('masterId').setAttribute("class",top_link_menuSelection1);
					document.getElementById("masterId").className="top_link menuSelection1";
					document.getElementById('menuLink').innerHTML =divStrart+arrMenu[1]+arrow+ arrSubMenu_2[4]+divEnd;
				} 
			
///Time Management 

			else if (action.indexOf("PayCycleList.action?T=C")>=0 || action.indexOf("PayCycleList.action?T=O")>=0 || action.indexOf("PayCycleList.action?T=T")>=0
					||  action.indexOf("EmployeeReportPayCycle.action")>=0 ||  action.indexOf("UpdateClockEntries.action?PAY=Y")>=0
					|| action.indexOf("ClockEntries.action?T")>=0 || action.indexOf("ClockEntries.action?O")>=0 || action.indexOf("ClockEntries.action?C")>=0) {
				
				
				
				document.getElementById('timeId').setAttribute("class",top_link_menuSelection1);
				document.getElementById("timeId").className="top_link menuSelection1";
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[2]+divEnd;
			}
				
//Roster

			else if (action.indexOf("RosterReport.action")>=0 || action.indexOf("RosterDependency.action")>=0 || action.indexOf("UploadRoster.action")>=0) {
				document.getElementById('RosterId').setAttribute("class",top_link_menuSelection1);
				document.getElementById("RosterId").className="top_link menuSelection1";
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[3]+divEnd;
			}
			
//Employees Management
			else if (action.indexOf("AddUser.action")>=0 || action.indexOf("AddEmployee.action")>=0 || action.indexOf("ChangePassword.action")>=0) {
				document.getElementById('EmpId').setAttribute("class",top_link_menuSelection1);
				document.getElementById("EmpId").className="top_link menuSelection1";
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[4]+divEnd;
			} 
				
//Payroll Management
			else if (action.indexOf("ApprovePayroll.action")>=0) {
				document.getElementById('payList').setAttribute("class",top_link_menuSelection1);
				document.getElementById("payList").className="top_link menuSelection1";
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[5]+divEnd;
			}

//Reports				
	//Reports>>Company	

			else if (action.indexOf("WLocationReport.action")>=0 || action.indexOf("CountryReport.action")>=0 || action.indexOf("StateReport.action")>=0 || action.indexOf("CityReport.action")>=0
				|| action.indexOf("ServiceReport.action")>=0 || action.indexOf("DepartmentReport.action")>=0) {
				document.getElementById('reportId').setAttribute("class", top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[6]+arrow+ arrSubMenu_7[0]+divEnd;
			} 
				
//Reports>>Roster
			else if (action.indexOf("RosterPolicyReport.action")>=0) {
				document.getElementById('reportId').setAttribute("class", top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[6]+arrow+ arrSubMenu_7[1]+divEnd;
			}
				
//Reports>>Policy Management
			else if (action.indexOf("AllowanceReport.action")>=0 || action.indexOf("DeductionReport.action")>=0 || action.indexOf("HolidayReport.action")>=0) {
				document.getElementById('reportId').setAttribute("class", top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[6]+arrow+ arrSubMenu_7[2]+divEnd;
			}
		
//Emp report
		
		
		 else if (action.indexOf("EmployeeReport.action")>=0 || action.indexOf("UserReport.action")>=0 || action.indexOf("Announcements.action")>=0) {
				document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[6]+arrow+ arrSubMenu_7[3]+divEnd;
			}
				
//Exceptions report
			
			
	 else if (action.indexOf("LateClockReport.action")>=0 || action.indexOf("LateEmpClockReport.action")>=0) {
			document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
			document.getElementById('menuLink').innerHTML =divStrart+arrMenu[6]+arrow+ arrSubMenu_7[4]+divEnd;
		}

//KPI report
				
		else if (action.indexOf("AmountPaidEmployee.action")>=0 || action.indexOf("ServicesEmployeeReport.action")>=0 || action.indexOf("AdditionalEmpHours.action")>=0 || action.indexOf("EmployeeHours.action?P=ESH")>=0) {
			
			document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
			document.getElementById('menuLink').innerHTML =divStrart+arrMenu[6]+arrow+ arrSubMenu_7[5]+divEnd;
		}
				
//Roster vs Actual
				
			else if (action.indexOf("RosterActualHours.action")>=0 || action.indexOf("RosterActualHoursEmp.action")>=0 || action.indexOf("EmployeeHours.action?P=EH")>=0) {
				
				document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[6]+arrow+ arrSubMenu_7[6]+divEnd;
			}
			 
//Payslips ViewPaySlips.action
			else if (action.indexOf("MyPay.action")>=0) {
				
				document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[6]+divEnd;
			}

			else if (action.indexOf("MenuNavigationInner.action?NN=ME11")>=0 || action.indexOf("MenuNavigationInner.action?NN=ME12")>=0 || action.indexOf("MenuNavigationInner.action?NN=ME13")>=0 || action.indexOf("MenuNavigationInner.action?NN=ME14")>=0 || action.indexOf("MenuNavigationInner.action?NN=ME15")>=0) {
				document.getElementById('masterId').setAttribute("class",top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[1]+divEnd;
			}else if (action.indexOf("MenuNavigationInner.action?NN=R11")>=0 || action.indexOf("MenuNavigationInner.action?NN=R12")>=0 || action.indexOf("MenuNavigationInner.action?NN=R13")>=0 || action.indexOf("MenuNavigationInner.action?NN=R14")>=0 || action.indexOf("MenuNavigationInner.action?NN=R15")>=0 || action.indexOf("MenuNavigationInner.action?NN=R16")>=0 || action.indexOf("MenuNavigationInner.action?NN=R17")>=0) {
				document.getElementById('reportId').setAttribute("class",top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML =divStrart+arrMenu[6]+divEnd;
			}
		
	//}
	}