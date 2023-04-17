package com.konnect.jpms.util;

public interface IPages {
 
	final public static String PMenu 				= "/jsp/common/Menu.jsp";
	
	final public static String PMenuAdmin 			= "/jsp/common/MenuAdmin.jsp";
	final public static String PMenuManager 		= "/jsp/common/MenuManager.jsp";
	final public static String PMenuEmployee 		= "/jsp/common/MenuEmployee.jsp";
	
	final public static String PSubMenuAdmin 		= "/jsp/common/SubMenuAdmin.jsp";
	final public static String PSubMenuManager 		= "/jsp/common/SubMenuManager.jsp";
	final public static String PSubMenuEmployee 	= "/jsp/common/SubMenuEmployee.jsp";
	  
	final public static String PAdminDashboard 		= "/jsp/employee/DashboardAdmin.jsp";
	final public static String PEmpDashboard 		= "/jsp/employee/DashboardEmp.jsp";
	final public static String PManagerDashboard 	= "/jsp/employee/DashboardManager.jsp";
	final public static String PAccountantDashboard = "/jsp/employee/DashboardAccountant.jsp";
	final public static String PHRManagerDashboard 	= "/jsp/employee/DashboardHRManager.jsp";
	final public static String PCEODashboard 		= "/jsp/employee/DashboardCEO.jsp";
	final public static String PCFODashboard 		= "/jsp/employee/DashboardCFO.jsp";
	final public static String PArticleDashboard 	= "/jsp/employee/DashboardArticle.jsp";
	final public static String PConsultantDashboard	= "/jsp/employee/DashboardConsultant.jsp";
	    
	final public static String PForgotPassword 		= "/jsp/common/ForgotPassword.jsp";
	final public static String PAddNotice 			= "/jsp/common/AddNotice.jsp";
	final public static String PConfigSettings 		= "/jsp/common/ConfigSettings.jsp";
	final public static String PNotificationSettings = "/jsp/common/NotificationSettings.jsp";
	
//	final public static String PMenuNavigationInnerA = "/jsp/common/MenuNavigationInnerA.jsp";
//	final public static String PMenuNavigationInnerM = "/jsp/common/MenuNavigationInnerM.jsp";
//	final public static String PMenuNavigationInnerE = "/jsp/common/MenuNavigationInnerE.jsp";
	final public static String PMenuNavigationInner = "/jsp/common/MenuNavigationInner.jsp";
	
	final public static String PAddCountry 			= "/jsp/location/AddCountry.jsp";
	final public static String PAddCity 			= "/jsp/location/AddCity.jsp";
	final public static String PAddState 			= "/jsp/location/AddState.jsp";
	final public static String PAddWLocation 		= "/jsp/location/AddWorkLocation.jsp";

	final public static String PAddClient 		= "/jsp/employee/AddClient.jsp";
	final public static String PAddDepartment 		= "/jsp/employee/AddDepartment.jsp";
	final public static String PAddDesignation 		= "/jsp/master/AddDesig.jsp";
	final public static String PAddGrade 			= "/jsp/master/AddGrade.jsp";
	final public static String PAddEmployee 		= "/jsp/employee/AddEmployee.jsp";
	final public static String PAddEmployeeP 		= "/jsp/employee/AddEmployeeP.jsp";
	final public static String PAddService 			= "/jsp/employee/AddService.jsp";
	final public static String PAddUser 			= "/jsp/employee/AddUser.jsp";
	final public static String PAddUserType 		= "/jsp/employee/AddUserType.jsp";
	
	final public static String PAddEmployeeActivity	= "/jsp/employee/EmployeeActivity.jsp";
	final public static String PEmployeeComparison	= "/jsp/employee/EmployeeComparison.jsp";
	final public static String PEmployeeResignation	= "/jsp/employee/ResignationEntry.jsp";
	final public static String PReportResignation	= "/jsp/reports/ResignationReport.jsp";
	
	final public static String PReportCountry 		= "/jsp/reports/master/CountryReport.jsp";
	final public static String PReportCity 			= "/jsp/reports/CityReport.jsp";
	final public static String PReportState 		= "/jsp/reports/master/StateReport.jsp";
	final public static String PReportWLocation 	= "/jsp/reports/master/WLocationReport.jsp";
	final public static String PReportEmployeeServiceRate = "/jsp/reports/EmployeeServiceRateReport.jsp";
	final public static String PReportNotice 		= "/jsp/reports/NoticeReport.jsp";
	final public static String PReportRosterVsActual = "/jsp/reports/RosterVsActualReport.jsp";
	final public static String PReportRosterVsActualE = "/jsp/reports/RosterVsActualReportE.jsp";
	final public static String PReportLateClockEntries = "/jsp/reports/LateClockEntries.jsp";
	final public static String PReportLateClockEntriesE = "/jsp/reports/LateClockEntriesE.jsp";
	final public static String PReportAdditionalHoursReportE = "/jsp/reports/AdditionalHoursReportE.jsp";
	final public static String PReportStatutaryIdAndRegInfo 	= "/jsp/reports/master/StatutoryIDAndRegistrationInfoReport.jsp";
	
	final public static String PWorkForce 			= "/jsp/reports/WorkForce.jsp";
	final public static String PWorkForceJoin		= "/jsp/reports/WorkForceJoin.jsp";
	final public static String PWorkForceTerminate	= "/jsp/reports/WorkForceTerminate.jsp";
	final public static String PEmployeePerformance	= "/jsp/employee/EmployeePerformance.jsp";
	final public static String POrganisationalChart	= "/jsp/employee/OrganisationalChart.jsp";
	final public static String POrganisationalChart1	= "/jsp/employee/OrganisationalChart1.jsp";
	
	final public static String PSearchByServices 	= "/jsp/reports/SearchByServices.jsp";
	
	final public static String PUploadRosterEntries = "/jsp/roster/UploadRosterEntries.jsp";
	
	
	final public static String PReportServicesEmployee = "/jsp/reports/ServicesEmployeeReport.jsp";
	
	final public static String PEmployeeRReport 		= "/jsp/reports/EmployeeRReport.jsp";
	 
	final public static String PReportClient 			= "/jsp/task/ClientReport.jsp";
	
	final public static String PReportPolicy 		= "/jsp/reports/PolicyReport.jsp";
	final public static String PReportDepartment 	= "/jsp/reports/master/DepartmentReport.jsp";
	final public static String PReportDesignation 	= "/jsp/reports/master/DesignationReport.jsp";
	final public static String PReportService 		= "/jsp/reports/master/ServiceReport.jsp";
	final public static String PReportUser 			= "/jsp/reports/master/UserReport.jsp";
	final public static String PReportUserType 		= "/jsp/reports/master/UserTypeReport.jsp";
	final public static String PReportEmployee 		= "/jsp/reports/EmployeeReport.jsp";
	final public static String PReportEmployeePayCycle = "/jsp/reports/EmployeeReportPayCycle.jsp";
	final public static String PReportRoster 		= "/jsp/reports/RosterReport.jsp";
	final public static String PReportRosterE 		= "/jsp/reports/RosterReportE.jsp";
	final public static String PRosterPolicyReport 	= "/jsp/reports/RosterPolicyReport.jsp"; 
	final public static String PLoanPolicyReport 	= "/jsp/loan/LoanPolicyReport.jsp";
	final public static String PPayrollPolicyReport = "/jsp/reports/PayrollPolicyReport.jsp";	
	final public static String PReportHoliday 		= "/jsp/reports/HolidayReport.jsp";
	final public static String PReportHoliday1 		= "/jsp/reports/HolidayReport1.jsp";
	final public static String PReportHolidayE 		= "/jsp/reports/HolidayReportE.jsp";
	final public static String PReportDeduction 	= "/jsp/reports/master/DeductionReport.jsp"; 
	final public static String PReportDeductionIndia = "/jsp/reports/master/DeductionReportIndia.jsp"; 
	final public static String PReportHRA 			= "/jsp/reports/master/HRAReport.jsp";
	final public static String PReportGratuity 		= "/jsp/reports/master/GratuityReport.jsp";
	final public static String PReportDeductionTax 	= "/jsp/reports/master/DeductionReportTax.jsp";
	final public static String PReportDeductionTaxM 	= "/jsp/reports/master/DeductionReportTaxM.jsp"; 
	final public static String PReportDeductionTaxF 	= "/jsp/reports/master/DeductionReportTaxF.jsp"; 
	final public static String PReportAllowance 	= "/jsp/reports/master/AllowanceReport.jsp";
	final public static String PReportClockEntriesApproval = "/jsp/reports/ClockEntriesApprovalReport.jsp";
	final public static String PReportEmployeeHours = "/jsp/reports/EmployeeHours.jsp";
	final public static String PReportEmployeeHoursVar = "/jsp/reports/EmployeeHoursVar.jsp";
	final public static String PReportEmployeeHours1 = "/jsp/reports/EmployeeHours1.jsp";
	final public static String PReportEmployeeAmountPaid = "/jsp/reports/EmployeeAmountPaid.jsp";
	final public static String PReportAttendance 	= "/jsp/reports/AttendanceReport.jsp";
	final public static String PReportEmpGratuity 	= "/jsp/reports/EmpGratuityReport.jsp";
	final public static String PPayPayroll		 	= "/jsp/payroll/PayPayroll.jsp";
	final public static String PPayArears		 	= "/jsp/payroll/PayArears.jsp";
	
	
	final public static String PMail 				= "/jsp/mail/mail.zul";
	final public static String PMailCount			= "/jsp/mail/mailCount.zul"; 
	
	
	
	
	final public static String PAddDates 			= "/jsp/util/AddDates.jsp";
	
	final public static String PMyProfile 			= "/jsp/reports/MyProfile.jsp";
	final public static String PMyProfileEdit		= "/jsp/reports/MyProfileEdit.jsp";
	
	
	final public static String PLoanApplicationForm		= "/jsp/loan/LoanApplicationForm.jsp";
	final public static String PLoanApplicationReport	= "/jsp/loan/LoanApplicationReport.jsp";
	final public static String PLoanBalanceReport		= "/jsp/loan/LoanBalanceReport.jsp";
	
	final public static String PReportClock 		= "/jsp/tms/ClockEntries.jsp";
	final public static String PReportClock2 		= "/jsp/tms/ClockEntries2.jsp";
	final public static String PReportClock2Emp		= "/jsp/tms/ClockEntries2Emp.jsp";
	final public static String PReportClock3 		= "/jsp/tms/ClockEntries3.jsp";
	final public static String PReportClockManager 	= "/jsp/tms/ClockEntriesManager.jsp";
	final public static String PReportClockSummary 	= "/jsp/tms/ClockEntriesSummary.jsp";
	final public static String PUpdateClockEntries 	= "/jsp/tms/ViewClockEntries.jsp";
	final public static String PAttendanceRegister 	= "/jsp/tms/AttendanceRegister.jsp";
	
	final public static String PLeaveRegister 		= "/jsp/leave/LeaveRegister.jsp";
	final public static String PProllRegister 	= "/jsp/payroll/PayrollRegister.jsp";
	
	final public static String PUpdateClockEntries1 = "/jsp/tms/ViewClockEntries1.jsp";
	final public static String PUpdateClockEntries1Emp = "/jsp/tms/ViewClockEntries1Emp.jsp";
	final public static String PEmployeeAnalysis 	= "/jsp/tms/EmployeeAnalysis.jsp"; 
	final public static String PExtraWork 			= "/jsp/tms/ExtraWork.jsp";
	final public static String PPayCycleList 		= "/jsp/tms/PayCycleList.jsp";
	
	final public static String PChangePassword 		= "/jsp/common/ChangePassword.jsp";
	final public static String PCommonClock 		= "/jsp/common/CommonClock.jsp";
	
	final public static String PRosterRequired 		= "/jsp/roster/RosterDependency.jsp";
	
	
	final public static String PEPFSetting 			= "/jsp/tax/india/EPFSetting.jsp";
	final public static String PESISetting 			= "/jsp/tax/india/ESISetting.jsp";
	final public static String PLWFSetting 			= "/jsp/tax/india/LWFSetting.jsp";
	final public static String PMiscSetting 		= "/jsp/tax/india/MiscSetting.jsp";
	final public static String PTaxSetting 		= "/jsp/task/tax/TaxSetting.jsp";
	
	
	
	final public static String PReportPTax 			= "/jsp/payroll/reports/PTaxReport.jsp";
	final public static String PReportIncentive		= "/jsp/payroll/reports/IncentiveReport.jsp";
	final public static String PReportTDS 			= "/jsp/payroll/reports/TDSReport.jsp";
	final public static String PReportBonus			= "/jsp/payroll/reports/BonusReport.jsp";
	final public static String PPaymentHeld			= "/jsp/payroll/reports/PaymentHeld.jsp";
	final public static String PReportSalaryYearly	= "/jsp/payroll/reports/SalaryYearlyReport.jsp";
	final public static String PReportEmpSalaryYearly	= "/jsp/payroll/reports/EmpSalaryYearlyReport.jsp";
	final public static String PReportEmpSalarySummaryYearly	= "/jsp/payroll/reports/EmpSalaryMonthlySummaryReport.jsp";
	final public static String PReportSalarySummaryYearly	= "/jsp/payroll/reports/SalaryMonthlySummaryReport.jsp";
	final public static String PReportEmpCTC		= "/jsp/payroll/reports/EmpCTCReport.jsp";
	final public static String PReportESICSalary	= "/jsp/payroll/reports/ESICSalaryReport.jsp";
	final public static String PReportEPFSalary	= "/jsp/payroll/reports/EPFSalaryReport.jsp";
	final public static String PReportIncenitveSummary	= "/jsp/payroll/reports/IncenitveSummary.jsp";
	final public static String PReportLWFSalary	= "/jsp/payroll/reports/LWFSalaryReport.jsp";
	
	
	
	
	final public static String PApproveClockEntries = "/jsp/payroll/ClockEntriesPayrollApproval.jsp";
	final public static String PApprovePayroll 		= "/jsp/payroll/ApprovePayroll.jsp";
	final public static String PViewPaySlip			= "/jsp/payroll/ViewPaySlip.jsp";
	final public static String PViewPaySlipE		= "/jsp/payroll/ViewPaySlipE.jsp"; 
	final public static String PGeneratePaySlip		= "/jsp/export/GeneratePaySlip.jsp";
	final public static String PExportMyob			= "/jsp/export/ExportMyob.jsp";
	
	final public static String PPolicyRoster 		= "/jsp/policies/RosterPloicy.jsp";
	final public static String PPolicyRosterHD 		= "/jsp/policies/RosterPloicyHD.jsp";
	final public static String PPolicyRosterBreak	= "/jsp/policies/RosterPolicyBreak.jsp";
	final public static String PPolicyPayroll 		= "/jsp/policies/PayrollPloicy.jsp";
	final public static String PPolicyPayroll1 		= "/jsp/policies/PayrollPloicy1.jsp";
	final public static String PPolicyPayroll2 		= "/jsp/policies/PayrollPloicy2.jsp";
	final public static String PAddHolidays 		= "/jsp/policies/AddHolidays.jsp";
	final public static String PAddHolidays1 		= "/jsp/policies/AddHolidays1.jsp";
	final public static String PAddDeduction 		= "/jsp/policies/AddDeduction.jsp";
	final public static String PAddDeductionIndia	= "/jsp/policies/AddDeductionIndia.jsp";
	final public static String PAddDeductionTax		= "/jsp/policies/AddDeductionTax.jsp";
	final public static String PAddAllowance 		= "/jsp/policies/AddAllowance.jsp";
	 
	
	//=============================================  Leave Module Start =========================================
	final public static String PTravelApproval		 			= "/jsp/leave/TravelApproval.jsp";
	final public static String PTravelAdvanceReport	 			= "/jsp/leave/TravelAdvanceReport.jsp";
	final public static String PTravelAdvanceEligibilityReport	= "/jsp/leave/TravelAdvanceEligibilityReport.jsp";
	final public static String PSettleAdvance		 			= "/jsp/leave/SettleAdvance.jsp";
	
	final public static String PManagerLeaveApproval 			= "/jsp/leave/ManagerLeaveApproval.jsp";
	final public static String PManagerLeaveApprovalReport	    = "/jsp/reports/ManagerLeaveApprovalReport.jsp";
	final public static String ProccessingDashboard_1	   		= "/jsp/payroll/ProccessingDashboard_1.jsp";

	final public static String PEmployeeLeaveBreakdown	    	= "/jsp/reports/EmployeeLeaveBreakdown.jsp";
	
	final public static String PApplyTravel		 				= "/jsp/leave/ApplyTravel.jsp";
	final public static String PApplyOnDuty		 				= "/jsp/leave/ApplyOnDuty.jsp";
	final public static String PEmployeeLeaveEntry 				= "/jsp/leave/EmployeeLeaveEntry.jsp";
	final public static String PEmployeeLeaveEntryReport		= "/jsp/reports/LeaveEntryReport.jsp";
	final public static String PEmployeeLeaveEntryDetailsReport		= "/jsp/reports/LeaveEntryDetailsReport.jsp";
	
	final public static String PEmployeeIssueLeave 				= "/jsp/leave/EmployeeIssueLeave.jsp";
	final public static String PEmployeeIssueLeaveReport		= "/jsp/reports/EmployeeIssueLeaveReport.jsp";
	
	final public static String PAddLeaveType       				= "/jsp/leave/AddLeaveType.jsp";
	final public static String PAddLeaveBreakType  				= "/jsp/leave/AddLeaveBreakType.jsp";
	final public static String PLeaveTypeReport    				= "/jsp/reports/LeaveTypeReport.jsp";
	final public static String PLeaveTypeBreakReport    		= "/jsp/leave/LeaveTypeBreakReport.jsp"; 
	final public static String PLeaveTypeReportLevelWise		= "/jsp/reports/LeaveTypeReportLevelWise.jsp";
	final public static String PLeaveBreakTypeReportLevelWise	= "/jsp/leave/LeaveBreakTypeReportLevelWise.jsp";
	
	final public static String PLeaveEncashment    				= "/jsp/leave/LeaveEncashment.jsp";
	
	final public static String PLeaveApprovedReport				= "/jsp/leave/LeaveApprovedReport.jsp";
	final public static String PLeaveCancelledReport			= "/jsp/leave/LeaveCancelledReport.jsp";
	final public static String PLeavePendingReport			    = "/jsp/leave/LeavePendingReport.jsp";
	
	//============================================= Leave Module End =========================================

	final public static String PSalaryDetails    				= "/jsp/salary/SalaryDetails.jsp";
	final public static String PEmployeeSalaryDetails  			= "/jsp/salary/EmployeeSalaryDetails.jsp";
	final public static String PEmployeeCharts    				= "/jsp/chart/EmployeeCharts.jsp";
	final public static String PAddACL    						= "/jsp/common/AddACL.jsp";
	final public static String PBroadcastMessage				= "/jsp/common/BroadcastMessage.jsp";
	final public static String PAccessDenied 					= "/jsp/errorPages/AccessDenied.jsp";
	final public static String PLevel							= "/jsp/reports/LevelReport.jsp";
	final public static String PApproveIncrement				= "/jsp/reports/ApproveIncrementReport.jsp";
	final public static String PDesig							= "/jsp/reports/master/DesigReport.jsp";
	final public static String PGrade							= "/jsp/reports/master/GradeReport.jsp";
	final public static String PCategory						= "/jsp/reports/master/CategoryReport.jsp";
	final public static String PWlocationType					= "/jsp/reports/master/WlocationTypeReport.jsp";
	final public static String PPerk							= "/jsp/reports/master/PerkReport.jsp";
	final public static String PIncrement						= "/jsp/reports/master/IncrementReport.jsp";
	final public static String PIncrementDA						= "/jsp/reports/master/IncrementReportDA.jsp";
	final public static String PExemption						= "/jsp/reports/master/ExemptionReport.jsp";
	final public static String PSection							= "/jsp/reports/master/SectionReport.jsp";
	final public static String PLTA								= "/jsp/reports/master/LTAReport.jsp";
	final public static String PInvestment						= "/jsp/reports/master/InvestmentForm.jsp";
	final public static String PInvestmentReport				= "/jsp/reports/master/InvestmentReport.jsp";
	final public static String PBonus							= "/jsp/reports/master/BonusReport.jsp";
	final public static String POverTime						= "/jsp/reports/master/OverTimeReport.jsp";
	final public static String PBank	 						= "/jsp/reports/master/BankReport.jsp";
	final public static String PSkills							= "/jsp/reports/master/SkillsReport.jsp";
	final public static String PSuccessionPlan					= "/jsp/reports/master/SuccessionPlanReport.jsp";
	final public static String PUserActivity					= "/jsp/common/UserActivity.jsp";
	final public static String PCompanyManual					= "/jsp/common/CompanyManual.jsp";
	final public static String PAddCompanyManual				= "/jsp/common/AddCompanyManual.jsp";
	final public static String PViewCompanyManual 				= "/jsp/common/ViewCompanyManual.jsp";
	final public static String PSetUpCompany	 				= "/jsp/common/SetUpCompany.jsp";
	final public static String PReportList		 				= "/jsp/reports/ReportList.jsp";
	
	// Added by M@yuri 18-Oct-2016
	final public static String PDailyOverTimeReport		 		= "/jsp/reports/master/DailyOverTimeReport.jsp";
   // Added by M@yuri 19-Oct-2016
	final public static String PQuarterlyOverTimeReport		 	= "/jsp/reports/master/QuarterlyOverTimeReport.jsp";
	final public static String PMonthlyOverTimeReport		 	= "/jsp/reports/master/MonthlyOverTimeReport.jsp";
	// Added by M@yuri 21-Oct-2016
	final public static String PWeeklyHoursReport		 		= "/jsp/reports/master/WeeklyHoursReport.jsp";
	// Added by M@yuri 21-Oct-2016
	final public static String PLeaveBlanceReport		 		= "/jsp/reports/master/LeaveBalanceReport.jsp";
	
	final public static String PCubeReport						= "/jsp/reports/cubes/CubeReport.jsp";
	final public static String PCubeReportDefault				= "/jsp/reports/cubes/CubeReportDefault.jsp";

	final public static String PCubeLeaveTable						= "jsp/reports/cubes/CubeReportLeaveTable.jsp";
	final public static String PCubeSalaryTable						= "jsp/reports/cubes/CubeReportSalaryTable.jsp";
	final public static String PCubeTimeTable						= "jsp/reports/cubes/CubeReportTimeTable.jsp";
	
	final public static String PCubeLeaveChart						= "jsp/reports/cubes/CubeReportLeaveChart.jsp";
	final public static String PCubeSalaryChart						= "jsp/reports/cubes/CubeReportSalaryChart.jsp";
	final public static String PCubeTimeChart						= "jsp/reports/cubes/CubeReportTimeChart.jsp";
	 
	
	final public static String PForm16							= "/jsp/itforms/Form16.jsp";
	final public static String PForm3A							= "/jsp/itforms/Form3A.jsp";
	final public static String PForm6A							= "/jsp/itforms/Form6A.jsp";
	final public static String PForm5							= "/jsp/itforms/Form5.jsp";
	final public static String PForm10							= "/jsp/itforms/Form10.jsp";
	final public static String PForm12A							= "/jsp/itforms/Form12A.jsp";
	
	
	final public static String PRequisitions					= "/jsp/requisitions/Requisitions.jsp";
	final public static String PReqBonafide						= "/jsp/requisitions/BonafideRequest.jsp";
	final public static String PMyRequisitionsReport			= "/jsp/requisitions/MyRequisitionsReport.jsp";
	final public static String PRequisitionsReport				= "/jsp/requisitions/RequisitionsReport.jsp";
	final public static String PInfrastructureRequest			= "/jsp/requisitions/InfrastructureRequest.jsp";
	final public static String POtherRequest					= "/jsp/requisitions/OtherRequest.jsp"; 
	final public static String PMyReimbursements				= "/jsp/requisitions/MyReimbursements.jsp";
	final public static String PPaidUnpaidReimbursements		= "/jsp/requisitions/PaidUnpaidReimbursements.jsp";
	final public static String PReimbursementStatement			= "/jsp/requisitions/ReimbursementStatement.jsp";
	final public static String PReimbursementTransactions		= "/jsp/requisitions/ReimbursementTransactions.jsp";
	final public static String PMyPerks							= "/jsp/requisitions/MyPerks.jsp";
	final public static String PPaidUnpaidPerks					= "/jsp/requisitions/PaidUnpaidPerks.jsp";
	
	
	final public static String PAddEmployeeMode					= "/jsp/employee/AddEmployeeMode.jsp";
	final public static String PSearchEmployeeReport			= "/jsp/employee/SearchEmployeeReport.jsp";
	final public static String PSearchEmployeeSkills			= "/jsp/employee/SearchEmployeeSkills.jsp";
	
	
	
	final public static String PProjectAvailabilityCalendar 	= "/jsp/task/ProjectAvailabilityCalendar.jsp";
	final public static String PProjectEngagementCalendar 	= "/jsp/task/ProjectEngagementCalendar.jsp";
	
	
	
	final public static String PAdultWorkerReport 	= "/jsp/reports/factory/AdultWorkerReport.jsp";
	final public static String PChildWorkerReport 	= "/jsp/reports/factory/ChildWorkerReport.jsp";
	final public static String PCompensatroyHolidayReport 	= "/jsp/reports/factory/CompensatoryHolidayReport.jsp";
	final public static String PAttendanceMuster 	= "/jsp/reports/factory/AttendanceMuster.jsp";
	final public static String PLeaveRegisterWages 	= "/jsp/reports/factory/LeaveRegisterWages.jsp";
	final public static String POvertimeMuster 	= "/jsp/reports/factory/OvertimeMuster.jsp";
	
	final public static String PAddBook 	= "/jsp/library/AddBook.jsp";
	final public static String PBookReport 	= "/jsp/library/BookReport.jsp";
	final public static String PRateBook 	= "/jsp/library/RateBook.jsp";
	final public static String PPurchaseBook 	= "/jsp/library/PurchaseBook.jsp";
	final public static String PApproveOrDenyBookPurchase 	= "/jsp/library/ApproveOrDenyBookPurchase.jsp";
	final public static String PIssueBook 	= "/jsp/library/IssueBook.jsp";
	final public static String PBookIssueReport 	= "/jsp/library/BookIssueReport.jsp";
	final public static String PApproveOrDenyBookIssueRequest 	= "/jsp/library/ApproveOrDenyBookIssueRequest.jsp";
	
	
	final public static String PAddDish = "/jsp/cafeteria/AddDish.jsp";
	final public static String PViewCafeteria = "/jsp/cafeteria/ViewCafeteria.jsp";
	final public static String POrderDish = "/jsp/cafeteria/PlaceOrderForDish.jsp";
	final public static String PViewOrders = "/jsp/cafeteria/ViewCafeteriaOrders.jsp";
	final public static String PConfirmOrders = "/jsp/cafeteria/ConfirmDishOrder.jsp";
	final public static String PAddEmpOrders = "/jsp/cafeteria/AddEmpFoodRequests.jsp";
	final public static String PAddGuestsOrders = "/jsp/cafeteria/AddGuests.jsp";
}

