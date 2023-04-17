function fun(){

		var url = document.URL;
		var slashIndex = url.lastIndexOf('/');
		var dotIndex = url.lastIndexOf('.', slashIndex);
		var action;
		var arrow = "<a href=\"#\"> > </a>";
		var top_link_menuSelection1="top_link menuSelection1";
		var top_link_menuSelection2="topR_link menuSelection2";

		
		
		var arrMenu = new Array("<a href=\"MyDashboard.action\">Dashboard</a>", 
 				"<a href=\"RosterReport.action\">My Roster Details</a>",
 				"<a href=\"MyProfile.action\">My Profile</a> ",
 				"<a href=\"Announcements.action\">Notices</a> ",
 				"<a href=\"#\">Timesheet</a>",
 				"<a href=\"ChangePassword.action\">Change Password</a>",
 				"<a href=\"MenuNavigationInner.action?NN=R10\">Reports</a>");		

		
		var arrSubMenu_5 = new Array("<a href=\"PayCycleList.action?T=T\">My Attendence</a>", 
 				"<a href=\"PayCycleList.action?T=C\">My Clock Entries</a>",
 				"<a href=\"ApproveClockEntries.action\">My Issues</a> ");
		

		var arrSubMenu_7 = new Array("<a href=\"HolidayReport.action\">My Public Holidays</a>", 
 				"<a href=\"ServicesEmployeeReport.action\">Employee Availability</a> ");

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
			else if (action.indexOf("RosterReport.action")>=0) {
				 document.getElementById('rostId').setAttribute("class", top_link_menuSelection1);
				//document.getElementById('rostId').style.backgroundColor='#4FAFEE';
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[1]+divEnd;
				}
			else if (action.indexOf("MyProfile.action")>=0 || action.indexOf("AddEmployeeE.action?P=Y")>=0) {
				 document.getElementById('profId').setAttribute("class",  top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[2]+divEnd;
				}
			else if (action.indexOf("Announcements.action")>=0) {
				 document.getElementById('noticId').setAttribute("class", top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[3]+divEnd;
				}

			else if (action.indexOf("PayCycleList.action?T=T")>=0 || action.indexOf("ClockEntries.action?T=T")>=0) {
				 document.getElementById('timesheetId').setAttribute("class", top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[4] + arrow + arrSubMenu_5[0] +divEnd;
				}
			else if (action.indexOf("PayCycleList.action?T=C")>=0 || action.indexOf("ClockEntries.action?T=C")>=0) {
				 document.getElementById('timesheetId').setAttribute("class", top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[4] + arrow + arrSubMenu_5[1] +divEnd;
				}
			else if (action.indexOf("ApproveClockEntries.action")>=0) {
				 document.getElementById('timesheetId').setAttribute("class", top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[4] + arrow + arrSubMenu_5[2] +divEnd;
				}
			
			else if (action.indexOf("UpdateClockEntries.action")>=0) {
				 document.getElementById('timesheetId' ).setAttribute("class", top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+arrMenu[4]+divEnd;
				}
			else if (action.indexOf("ChangePassword.action")>=0) {
				 document.getElementById('changePasswd').setAttribute("class", top_link_menuSelection1);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[5]+divEnd;
				}
			else if (action.indexOf("MyPay.action")>=0 || action.indexOf("HolidayReport.action")>=0 || action.indexOf("MenuNavigationInner.action?NN=R")>=0) {
				 document.getElementById('repoId').setAttribute("class", top_link_menuSelection2);
				document.getElementById('menuLink').innerHTML=divStrart+ arrMenu[6]+divEnd;
				}
			
			
	 				
	//	}
	}