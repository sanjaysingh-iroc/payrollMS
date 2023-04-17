package com.konnect.jpms.util;

public interface IStatementsPostgres {


	final static public String selectForgotPassword = "SELECT * FROM user_details ud, employee_personal_details epd where epd.emp_per_id = ud.emp_id and epd.is_alive=true and (upper(epd.emp_email)= ? or upper(epd.emp_email_sec)= ?) ";

	final static public String selectCountry = "SELECT * FROM country order by country_name";
	final static public String selectCountryV = "SELECT * FROM country WHERE country_id=?";
	final static public String selectCountryDef = "SELECT * FROM country WHERE upper(country_name)=?";
	final static public String insertCountry = "INSERT INTO country (country_name) VALUES (?)";
	final static public String deleteCountry = "DELETE FROM country WHERE country_id=?";
	final static public String updateCountry = "UPDATE country SET country_name=? WHERE country_id=?";
	//********query for employee structure*********//
//	final static public String updateEmployeeStructure = "update employee_official_details set supervisor_emp_id=?,hod_emp_id=?,emp_hr=? where emp_id=?";
    //final static public String insertLeaveforEmployeeStructure="INSERT INTO probation_policy(emp_id, leaves_types_allowed) VALUES(?,?)";
	//final static public String updateLeaveforEmployeeStructure="update probation_policy set leaves_types_allowed=? where emp_id=?";
	
	final static public String selectState = "SELECT * FROM state order by state_name";
	final static public String selectState_WLocation = "SELECT distinct(s.state_name), s.state_id FROM work_location_info wi, state s where wi.wlocation_state_id = s.state_id order by s.state_name";
	final static public String selectStateV = "SELECT * FROM state s, country c WHERE s.country_id=c.country_id and state_id=?";
	final static public String selectStateDef = "SELECT * FROM state  WHERE upper(state_name)=?";
	final static public String selectStateR = "SELECT * FROM state s LEFT JOIN country c ON s.country_id=c.country_id order by state_name";
	final static public String insertState = "INSERT INTO state (state_name, country_id) VALUES (?, ?)";
	final static public String deleteState = "DELETE FROM state WHERE state_id=?";
	final static public String updateState = "UPDATE state SET state_name=?,country_id=? WHERE state_id=?";

	final static public String selectCity = "SELECT * FROM city order by city_name";
	final static public String selectCity_stateId = "SELECT * FROM city WHERE state_id=? order by city_name";
	final static public String selectCityV = "SELECT * FROM city c, state s, country co WHERE co.country_id=s.country_id AND s.state_id=c.state_id AND city_id=?";
	final static public String selectCityR = "SELECT * FROM( SELECT * FROM city c LEFt JOIN state s ON s.state_id=c.state_id order by city_name )acs LEFT JOIN country co ON co.country_id=acs.country_id";
	final static public String insertCity = "INSERT INTO city (city_name, state_id) VALUES (?, ?)";
	final static public String deleteCity = "DELETE FROM city WHERE city_id=?";
	final static public String updateCity = "UPDATE city SET city_name=?, state_id=? WHERE city_id=?";

	final static public String selectDesignation = "SELECT * FROM designation_info order by desig_name";
	final static public String selectDesignationR = "SELECT * FROM designation_info order by desig_name";
	final static public String selectDesignationV = "SELECT * FROM designation_info WHERE desig_id=?";
	final static public String selectMaxDesignation = "SELECT max(desig_id) as desig_id FROM designation_info";
	final static public String insertDesignation = "INSERT INTO designation_info (desig_id, desig_name) VALUES (?,?)";
	final static public String deleteDesignation = "DELETE FROM designation_info WHERE desig_id=?";
	final static public String updateDesignation = "UPDATE designation_info SET desig_name=? WHERE desig_id=?";

	final static public String selectWLocation = "SELECT * FROM work_location_info order by wlocation_name";
	final static public String selectWLocation1 = "SELECT * FROM work_location_info where org_id = ? order by wlocation_name";
	
	final static public String selectWLocationState = "select * from work_location_info wi,state s where wi.wlocation_state_id = s.state_id and wi.org_id=? and s.state_id=?";

	
	final static public String selectWLocation2 = "SELECT * FROM work_location_info where org_id = ? and wlocation_id in (?) order by wlocation_name";
	final static public String selectWLocation_HRManager = "SELECT * FROM work_location_info wi where wlocation_id = (select wlocation_id from employee_official_details where emp_id=?) order by wlocation_name";
	final static public String selectWLocationV = "Select * from ( Select * from ( Select * from ( Select * from work_location_info  WHERE wlocation_id= ? ) ac left join city c on ac.wlocation_city_id = c.city_id ) ast left join state s on ast.wlocation_state_id = s.state_id ) aco left join country co on aco.wlocation_country_id = co.country_id";

	final static public String selectMetroWLocationR = "select * from state sd, work_location_info wd, employee_official_details eod where eod.wlocation_id=wd.wlocation_id and wd.wlocation_state_id=sd.state_id";
	final static public String selectWLocationR = "SELECT * FROM( SELECT * FROM(SELECT * FROM ( SELECT * FROM ( SELECT * FROM work_location_info wl, timezones tz  WHERE tz.timezone_id=wl.timezone_id ) wl left join state s on wl.wlocation_state_id=s.state_id ) wl left join country co on wl.wlocation_country_id=co.country_id ) awt LEFT JOIN work_location_type wt ON awt.wlocation_type_id = wt.wlocation_type_id order by wlocation_name) a left join bank_details bd on bd.bank_id=a.wlocation_bank_id";
	final static public String selectWLocationR1 = "SELECT * FROM( SELECT * FROM(SELECT * FROM ( SELECT * FROM ( SELECT * FROM work_location_info wl, timezones tz  WHERE tz.timezone_id=wl.timezone_id ) wl left join state s on wl.wlocation_billing_state_id=s.state_id ) wl left join country co on wl.wlocation_billing_country_id=co.country_id ) awt LEFT JOIN work_location_type wt ON awt.wlocation_type_id = wt.wlocation_type_id order by wlocation_name) a left join bank_details bd on bd.bank_id=a.wlocation_bank_id";
	final static public String insertWLocation = "INSERT INTO work_location_info (wlocation_name, wlocation_city, wlocation_state_id, wlocation_country_id, wlocation_pincode, wlocation_contactno, wlocation_faxno, wlocation_logo, wlocation_tan_no, wlocation_pan_no, wlocation_bank_id, wlocation_bank_acct_nbr, timezone_id, wlocation_email, wloacation_code, wlocation_address, wlocation_type_id, wlocation_start_time, wlocation_end_time, wlocation_weeklyoff1, wlocation_weeklyofftype1, wlocation_weeklyoff2, wlocation_weeklyofftype2, ismetro, currency_id, wlocation_start_time_halfday,wlocation_end_time_halfday, wlocation_weeklyoff3, wlocation_weeklyofftype3, wlocation_weeknos3, wlocation_ecc_code_1, wlocation_ecc_code_2, wlocation_reg_no) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static public String insertWLocation1 = "INSERT INTO work_location_info (wlocation_name, wlocation_city, wlocation_state_id, wlocation_country_id, wlocation_pincode, wlocation_contactno, wlocation_faxno, wlocation_logo, wlocation_tan_no, wlocation_pan_no, wlocation_bank_id, wlocation_bank_acct_nbr, timezone_id, wlocation_email, wloacation_code, wlocation_address, wlocation_type_id, wlocation_start_time, wlocation_end_time, wlocation_weeklyoff1, wlocation_weeklyofftype1, wlocation_weeklyoff2, wlocation_weeklyofftype2, ismetro, currency_id, wlocation_start_time_halfday,wlocation_end_time_halfday, wlocation_weeklyoff3, wlocation_weeklyofftype3, wlocation_weeknos3, wlocation_ecc_code_1, wlocation_ecc_code_2, wlocation_reg_no, org_id,biometric_info,wlocation_weeknos1,wlocation_weeknos2,wlocation_start_time_halfday1,wlocation_end_time_halfday1,wlocation_start_time_halfday2,wlocation_end_time_halfday2,wlocation_ptreg_no) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)";
	final static public String deleteWLocation = "DELETE FROM work_location_info WHERE wlocation_id=?";
	final static public String updateWLocation = "UPDATE work_location_info SET wlocation_name=?, wlocation_city_id=?, wlocation_state_id=?, wlocation_country_id=?, wlocation_pincode=?, wlocation_contactno=?, wlocation_faxno=?, wlocation_logo=?, timezone_id=? WHERE wlocation_id=?";

	final static public String selectEmpDetails1 = "select * from (select * from employee_personal_details epd, user_details ud where ud.emp_id = epd.emp_per_id and emp_per_id = ?) a left join employee_official_details eod on a.emp_per_id = eod.emp_id";
	final static public String selectEmpDetails = "SELECT * FROM employee_personal_details where joining_date is not null and approved_flag = true";
	final static public String selectBirthDay = "SELECT emp_per_id, emp_date_of_birth,emp_gender, EXTRACT( YEAR FROM AGE(emp_date_of_birth) ) as their_age FROM employee_personal_details  WHERE  EXTRACT( YEAR FROM AGE(emp_date_of_birth) ) < EXTRACT( YEAR FROM AGE(?, emp_date_of_birth ) ) and is_alive=true ORDER BY their_age, emp_per_id";
	final static public String selectLogin_timestamp = "SELECT * FROM login_timestamp where user_id_key = ?";
	final static public String selectUser = "SELECT * FROM user_details ud, user_type ut, employee_personal_details epd WHERE  ut.user_type_id=ud.usertype_id and ud.emp_id=epd.emp_per_id and upper(username)=? and password=?";
	final static public String selectUserV01 = "SELECT * FROM user_details WHERE  emp_id=? and username=?";
	final static public String selectUserV1 = "SELECT * FROM user_details WHERE  emp_id=?";
	final static public String selectUserV2 = "SELECT * FROM user_details WHERE  username=?";
	final static public String selectUserV3 = "SELECT * FROM user_details WHERE  emp_id=? and usertype_id = (select user_type_id from user_type where upper(user_type)=?)";
	final static public String selectUserV = "SELECT * FROM user_details ud, user_type ut WHERE  ut.user_type_id=ud.usertype_id AND user_id=?";
	final static public String selectUserR = "SELECT * FROM user_details ud, user_type ut, employee_personal_details e WHERE  ut.user_type_id=ud.usertype_id  and e.emp_per_id = ud.emp_id order by status, emp_fname, emp_lname";
	final static public String selectUserRAlpha = "SELECT * FROM user_details ud, user_type ut, employee_personal_details e WHERE ut.user_type_id=ud.usertype_id and e.emp_per_id = ud.emp_id and is_delete=flase and upper(username) like ? order by username";
	final static public String insertUser = "INSERT INTO user_details (username, password, usertype_id, emp_id, status,added_timestamp) VALUES (?,?,?,?,?,?)";
	final static public String deleteUser = "DELETE FROM user_details WHERE user_id=?";
	final static public String deleteUserEmp = "DELETE FROM user_details WHERE emp_id=?";
	final static public String updateUser = "UPDATE user_details SET username=?, password=?, usertype_id=?, emp_id=?, status=? WHERE user_id=?";
	final static public String updateUser1 = "UPDATE user_details SET username=?, password=? WHERE emp_id=? and usertype_id=?";
	final static public String updateUser1E = "UPDATE user_details SET password=? WHERE emp_id=? and usertype_id = (select user_type_id from user_type where upper(user_type)=?)";
	final static public String updateUserDetailsStatus = "UPDATE user_details SET status=? where emp_id=?";
	final static public String updateUserStatus = "UPDATE employee_personal_details SET emp_status=? where emp_per_id=?";
	final static public String updateUserStatus1 = "UPDATE employee_personal_details SET emp_status=?, is_alive = ? where emp_per_id=?";
	final static public String updateUserStatus2 = "UPDATE employee_personal_details SET emp_status=?, is_alive = ?, employment_end_date=? where emp_per_id=?";

	final static public String selectUserType = "SELECT * FROM user_type where visibility_id>0 order by user_type";
	final static public String selectUserType1 = "SELECT * FROM user_type order by user_type where visibility_id = ?";
	final static public String selectUserTypeR = "SELECT * FROM user_type order by user_type";
	final static public String selectUserTypeV = "SELECT * FROM user_type WHERE user_type_id=?";
	final static public String insertUserType = "INSERT INTO user_type (user_type) VALUES (?)";
	final static public String deleteUserType = "DELETE FROM user_type WHERE user_type_id=?";
	final static public String updateUserType = "UPDATE user_type SET user_type=? WHERE user_type_id=?";

	final static public String selectService = "SELECT * FROM services order by service_name";
	final static public String selectServiceR = "SELECT s.*,od.org_name,od.org_code FROM services s, org_details od where s.org_id = od.org_id order by service_name";
	final static public String selectServiceR1 = "SELECT s.*,od.org_name,od.org_code FROM services s, org_details od where s.org_id = od.org_id and s.org_id = ? order by service_name";
	final static public String selectServiceV = "SELECT * FROM services WHERE service_id=?";
	final static public String insertService = "INSERT INTO services (service_name, service_code, service_desc, org_id) VALUES (?,?,?,?)";
	final static public String deleteService = "DELETE FROM services WHERE service_id=?";
	final static public String updateService = "UPDATE services SET service_name=?, service_code=?, service_desc=? WHERE service_id=?";

	final static public String selectHolidays = "SELECT * FROM holidays where (is_optional_holiday is null or is_optional_holiday=false) order by _date desc ";
	final static public String selectHolidaysR = "SELECT * FROM holidays where (is_optional_holiday is null or is_optional_holiday=false) order by _date desc ";
	final static public String selectHolidaysR3 = "SELECT * FROM holidays where org_id =? and (is_optional_holiday is null or is_optional_holiday=false) order by _date desc ";
	final static public String selectHolidaysE = "SELECT * FROM holidays where wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and (is_optional_holiday is null or is_optional_holiday=false) order by _date desc ";
	final static public String selectHolidaysR1 = "SELECT * FROM holidays WHERE _year=? and (is_optional_holiday is null or is_optional_holiday=false) order by _date desc";
	final static public String selectHolidaysR2 = "SELECT * FROM holidays WHERE _date BETWEEN ? AND ? and (is_optional_holiday is null or is_optional_holiday=false) order by _date desc";
	final static public String selectHolidaysV = "SELECT * FROM holidays WHERE holiday_id=? ";
	final static public String selectHolidaysV1 = "SELECT * FROM holidays WHERE _date=? and (is_optional_holiday is null or is_optional_holiday=false)";
	final static public String insertHolidays = "INSERT INTO holidays (_date, _year, description, wlocation_id, colour_code, org_id) VALUES (?,?,?,?,?,?)";
	final static public String deleteHolidays = "DELETE FROM holidays WHERE holiday_id=?";
	final static public String updateHolidays = "UPDATE holidays SET _date=?, _year=?, description=?, colour_code=? WHERE holiday_id=?";

	final static public String selectGratuity = "SELECT * FROM gratuity_details where org_id=? order by gratuity_id desc";
	final static public String insertGratuity = "INSERT INTO gratuity_details (service_from, service_to, gratuity_days, max_amount,entry_date, user_id) VALUES (?,?,?,?,?,?)";
	final static public String deleteGratuity = "DELETE FROM gratuity_details WHERE gratuity_id=?";

	final static public String selectDeductionRTax = "SELECT * FROM deduction_tax_details where financial_year_from=? and financial_year_to =? order by age_from, _from";
	final static public String insertDeductionTax = "INSERT INTO deduction_tax_details (age_from, age_to, gender, _from, _to, deduction_amount, deduction_type, financial_year_from, financial_year_to, entry_date, user_id, slab_type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	final static public String deleteDeductionTax = "DELETE FROM deduction_tax_details WHERE deduction_tax_id=?";

	final static public String selectDeductionRIndia = "SELECT * FROM deduction_details_india ddi, state s where s.state_id=ddi.state_id and financial_year_from=? and financial_year_to=? order by ddi.state_id, income_from desc, ddi.gender";
	final static public String insertDeductionIndia = "INSERT INTO deduction_details_india (income_from, income_to, deduction_paycycle, deduction_amount, state_id, user_id, financial_year_from, financial_year_to, entry_date, gender) VALUES (?,?,?,? ,?,?,?,? ,?,?)";
	final static public String deleteDeductionIndia = "DELETE FROM deduction_details_india WHERE deduction_id=?";

	final static public String selectHRA = "SELECT * FROM hra_exemption_details where financial_year_from=? and financial_year_to=?";
	final static public String insertHRA = "INSERT INTO hra_exemption_details (condition1, condition1_type, condition2, condition2_type, condition3, condition3_type, financial_year_from, financial_year_to, entry_date, user_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
	final static public String deleteHRA = "DELETE FROM hra_exemption_details WHERE hra_id=?";

	final static public String selectDeductionR = "SELECT * FROM deduction_details order by income_from desc";
	final static public String selectDeductionR1 = "SELECT * FROM deduction_details WHERE income_from<= ? and income_to >= ? order by deduction_id LIMIT 1";
	final static public String selectDeductionV = "SELECT * FROM deduction_details WHERE deduction_id=?";
	final static public String insertDeduction = "INSERT INTO deduction_details (income_from, income_to, deduction_amount, user_id) VALUES (?,?,?,?)";
	final static public String deleteDeduction = "DELETE FROM deduction_details WHERE deduction_id=?";
	final static public String updateDeduction = "UPDATE deduction_details SET income_from=?, income_to=?, deduction_amount=?, user_id=? WHERE deduction_id=?";

	final static public String selectAllowanceR1 = "SELECT * FROM allowance where hours_completed<= ? order by hours_completed desc Limit 1";
	final static public String selectAllowanceR2 = "SELECT * FROM allowance where hours_completed<= ? and desig_id = ? order by hours_completed desc Limit 1";
	final static public String selectAllowanceR = "SELECT * FROM allowance a, designation_info di where di.desig_id=a.desig_id and di.desig_id in (select emp_per_id from employee_personal_details) order by hours_completed desc";
	final static public String selectAllowanceRAlpha = "SELECT * FROM allowance a, designation_info di where di.desig_id=a.desig_id and upper(desig_name) like ? and di.desig_id in (select emp_per_id from employee_personal_details) order by hours_completed desc";
	final static public String selectAllowanceV = "SELECT * FROM allowance WHERE allowance_id=?";
	final static public String insertAllowance = "INSERT INTO allowance (hours_completed, allowance_value, allowance_type, desig_id) VALUES (?,?,?,?)";
	final static public String deleteAllowance = "DELETE FROM allowance WHERE allowance_id=?";
	final static public String updateAllowance = "UPDATE allowance SET hours_completed=?, allowance_value=?, allowance_type=?, desig_id=? WHERE allowance_id=?";

	final static public String selectDepartment = "SELECT * FROM department_info order by dept_name";
	final static public String selectDepartment_HRManager = "SELECT * FROM department_info wi where dept_id = (select depart_id from employee_official_details where emp_id=?) order by dept_name";
//	final static public String selectDepartmentWlocationName = "SELECT * from (SELECT * FROM department_info) awli LEFT JOIN work_location_info wli ON awli.wlocation_id = wli.wlocation_id";
//	final static public String selectDepartment_WLocationId = "SELECT * FROM department_info WHERE wlocation_id=? order by dept_name";
	final static public String selectDepartmentV = "SELECT * FROM department_info WHERE dept_id=?";
//	final static public String selectDepartmentR = "Select * from ( Select * from ( Select * from department_info di Left Join work_location_info wi on wi.wlocation_id = di.wlocation_id )  ast left join state s on ast.wlocation_state_id = s.state_id ) aco left join country co on aco.wlocation_country_id = co.country_id";
	final static public String selectDepartmentR1 = "select * from department_info di where org_id = ? order by dept_name";
//	final static public String insertDepartment = "INSERT INTO department_info (dept_name, dept_code, dept_desc, wlocation_id, dept_contactno, dept_faxno) VALUES (?,?,?,?,?,?)";
//	final static public String insertDepartment1 = "INSERT INTO department_info (dept_name, dept_code, dept_desc, wlocation_id, dept_contactno, dept_faxno, org_id, service_id) VALUES (?,?,?,?,?,?,?,?)";
	final static public String deleteDepartment = "DELETE FROM department_info WHERE dept_id=?";
	final static public String deleteDepartment1 = "DELETE FROM department_info WHERE parent=?";
	final static public String updateDepartment = "UPDATE department_info SET dept_name=?, dept_desc=?, wlocation_id=?, dept_contactno=?, dept_faxno=? WHERE dept_id=?";

	final static public String selectTardyTime = "SELECT * FROM roster_policy where time_type = ? order by roster_policy_id desc";

	final static public String selectNotice = "SELECT * FROM notices order by display_date desc";
	final static public String selectNoticeV1 = "SELECT * FROM notices WHERE ? between display_date and display_end_date and ispublish=true order by display_date desc";
	final static public String selectNoticeV = "SELECT * FROM notices WHERE notice_id =?";
	final static public String insertNotice = "INSERT INTO notices (heading, content, _date, display_date) VALUES (?,?,?,?)";
	final static public String deleteNotice = "DELETE FROM notices WHERE notice_id=?";
	final static public String updateNotice = "UPDATE notices SET heading=?, content=?, display_date=? WHERE notice_id=?";

	final static public String updatePassword = "UPDATE user_details SET password=? where usertype_id = (SELECT user_type_id from user_type where user_type=?) and emp_id=? and upper(username)=?";

	final static public String selectEmployee_Max = "SELECT max(emp_per_id) as emp_per_id FROM employee_personal_details";
	final static public String selectEmployee_OCode = "SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id = eod.emp_id and is_alive = true order by empcode";
	final static public String selectEmployee_OName = "SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id = eod.emp_id and is_alive = true order by emp_fname";
	final static public String selectEmployee_OName1 = "SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id = eod.emp_id and is_alive = true and org_id = ? order by emp_fname";
	final static public String selectSupervisor_OName = "SELECT * FROM employee_personal_details epd, user_details ud WHERE is_alive = true and epd.emp_per_id = ud.emp_id and usertype_id != (select user_type_id from user_type where user_type = ?  ) order by emp_fname";

	final static public String selectEmployee_OCode_Manager = "SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.is_alive = true and epd.emp_per_id = eod.emp_id and eod.supervisor_emp_id = ? order by empcode";
	final static public String selectEmployee_OName_Manager = "SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.is_alive = true and epd.emp_per_id = eod.emp_id and eod.supervisor_emp_id = ? order by emp_fname, emp_mname, emp_lname";
	final static public String selectEmployee_OName_Manager1 = "SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.is_alive = true and epd.emp_per_id = eod.emp_id and eod.supervisor_emp_id = ? and org_id =? order by emp_fname, emp_lname";

	final static public String selectEmployee_OCode_HRManager = "SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.is_alive = true and epd.emp_per_id = eod.emp_id and eod.wlocation_id = (select wlocation_id from employee_official_details where emp_id=?) order by empcode";
	final static public String selectEmployee_OName_HRManager = "SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.is_alive = true and epd.emp_per_id = eod.emp_id and eod.wlocation_id = (select wlocation_id from employee_official_details where emp_id=?) order by emp_fname, emp_lname";
	final static public String selectEmployee_OName_HRManager1 = "SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.is_alive = true and epd.emp_per_id = eod.emp_id and eod.wlocation_id = (select wlocation_id from employee_official_details where emp_id=?) and org_id =? order by emp_fname, emp_lname";
	final static public String selectSupervisor_OName_HRManager = "SELECT * FROM employee_personal_details epd, employee_official_details eod, user_details ud WHERE is_alive = true and eod.emp_id = epd.emp_per_id and eod.emp_id = ud.emp_id and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and epd.emp_per_id = ud.emp_id and usertype_id != (select user_type_id from user_type where user_type = ? ) order by emp_fname";

	final static public String selectEmployee_OName_HRManager_Attendance = "SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.wlocation_id = (select wlocation_id from employee_official_details where emp_id=?) and eod.emp_id in (select  distinct emp_id from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?)  order by emp_fname, emp_lname";

	final static public String selectEmployee_EmpCode = "SELECT * FROM employee_personal_details WHERE empcode=?";
	final static public String selectEmployeeV = "SELECT * FROM employee_personal_details epd, employee_official_details eod, city c, state s, country co WHERE epd.emp_per_id=eod.emp_id and epd.emp_city_id=c.city_id AND epd.emp_state_id=s.state_id AND epd.emp_country_id=co.country_id AND emp_per_id = ?";

	final static public String selectEmployeeR1 = "select * from(Select * from ( Select * from ( Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive = ? and epd.emp_per_id=eod.emp_id and approved_flag = ? order by emp_status, emp_fname,emp_lname) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id ) aud left join user_details ud on aud.emp_id = ud.emp_id) pr LEFT JOIN city cc on pr.emp_city_id=cast(cc.city_id as text)";
	final static public String selectEmployeeR8 = "select * from employee_personal_details epd left join employee_official_details eod on eod.emp_id = epd.emp_per_id where approved_flag = ? and is_alive = ?";
	final static public String selectEmployeeManagerR1 = "select * from(Select * from ( Select * from ( Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id and supervisor_emp_id = ? and epd.is_alive = ? and approved_flag = ? order by emp_fname,emp_lname) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id) aud left join user_details ud on aud.emp_id = ud.emp_id) pr LEFT JOIN city cc on pr.emp_city_id=cast(cc.city_id as text)";
	final static public String selectEmployeeR11 = "select * from(Select * from ( Select * from ( Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id and upper(emp_fname) like ? order by emp_fname,emp_lname) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id ) aud left join user_details ud on aud.emp_id = ud.emp_id) pr LEFT JOIN city cc on pr.emp_city_id=cast(cc.city_id as text)";
	final static public String selectEmployeeManagerR11 = "select * from(Select * from ( Select * from ( Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id and supervisor_emp_id = ? and upper(emp_fname) like ? order by emp_fname,emp_lname) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id) aud left join user_details ud on aud.emp_id = ud.emp_id) pr LEFT JOIN city cc on pr.emp_city_id=cast(cc.city_id as text)";

	final static public String selectEmployeeByServiceId = "SELECT * FROM (SELECT * FROM employee_official_details where service_id  = ? ) aepd LEFT JOIN employee_personal_details epd ON aepd.emp_id = epd.emp_per_id WHERE epd.is_alive= true order by emp_fname";
	final static public String selectEmployeeByServiceIdManager = "SELECT * FROM (SELECT * FROM employee_official_details where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and service_id  = ? ) aepd LEFT JOIN employee_personal_details epd ON aepd.emp_id = epd.emp_per_id WHERE epd.is_alive= true order by emp_fname";
	final static public String selectEmployeeByServiceIdHRManager = "SELECT * FROM (SELECT * FROM employee_official_details where wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and service_id  = ? ) aepd LEFT JOIN employee_personal_details epd ON aepd.emp_id = epd.emp_per_id WHERE epd.is_alive= true order by emp_fname";

	final static public String selectEmployeeR2 = "(Select * from (Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id) awli left join work_location_info wli on awli.wlocation_id = wli.wlocation_id) adi left join department_info di on adi.depart_id = di.dept_id) order by emp_fname, emp_lname";
	final static public String selectEmployeeR2Alpha = "Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id and upper(emp_fname) like ?) adi left join department_info di on adi.depart_id=di.dept_id order by emp_fname, emp_lname";
	final static public String selectEmployeeManagerR2 = "Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id and supervisor_emp_id = ?) adi left join department_info di on adi.depart_id=di.dept_id order by emp_fname, emp_lname";
	final static public String selectEmployeeManagerR2Alpha = "Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id and supervisor_emp_id = ? and emp_fname like ?) adi left join department_info di on adi.depart_id=di.dept_id order by emp_fname, emp_lname";
	final static public String selectEmployeeR3 = "SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id order by emp_id";
	final static public String selectEmployeeR31 = "SELECT salutation, emp_id, emp_fname,emp_mname, emp_lname, empcode, emp_image, emp_email, emp_date_of_birth, joining_date, employment_end_date, emp_pf_no, emp_gender, marital_status,emp_contactno,emp_contactno_mob FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id order by emp_id";
	final static public String selectEmployeeDesig = "select * from grades_details gd, designation_details dd, level_details ld, employee_official_details eod where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id and gd.grade_id = eod.grade_id";
	final static public String selectEmployeeDesig1 = "select * from grades_details gd, designation_details dd, level_details ld, employee_official_details eod where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id and gd.grade_id = eod.grade_id and eod.emp_id = ?";

	final static public String selectEmployeeR3_Attendance = "SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id  and eod.emp_id in (select distinct emp_id from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?) order by emp_id";

	final static public String selectEmployeeR4 = "SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id ORDER BY emp_fname";
	final static public String selectEmployeeR5 = "Select ea.emp_id, designation_id, service_id, service_name, emptype from employee_official_details eod, (SELECT emp_id, service_name from (SELECT emp_per_id as emp_id, service_id from employee_personal_details epd LEFT JOIN ( select rd.emp_id, rd.service_id  from roster_details rd, employee_personal_details epd where rd.emp_id=epd.emp_per_id group by emp_id, service_id order by emp_id ) a ON a.emp_id=epd.emp_per_id order by emp_id) sa LEFT JOIN services s ON sa.service_id=s.service_id) ea where ea.emp_id=eod.emp_id order by emp_id";
	final static public String selectEmployeeR6 = "Select * from employee_official_details order by emp_id";
	final static public String selectEmployeeR7 = "SELECT * FROM employee_personal_details  where joining_date<=? ORDER BY emp_fname";
	final static public String selectEmployeeManagerR7 = "SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and joining_date<=? and supervisor_emp_id=? ORDER BY emp_fname";
	final static public String selectEmployeeR7alpha = "SELECT * FROM employee_personal_details where joining_date<=? and upper(emp_fname) like ? ORDER BY emp_fname";
	final static public String selectEmployeeManagerR7alpha = "SELECT * FROM employee_personal_details epd, employee_official_details eod  where epd.emp_per_id = eod.emp_id and joining_date<=? and supervisor_emp_id = ? and upper(emp_fname) like ? ORDER BY emp_fname";
	final static public String selectEmployeeOfficialDetails = "SELECT * FROM employee_personal_details WHERE emp_per_id = ?";

	final static public String deleteEmployee_P = "DELETE FROM employee_personal_details WHERE emp_per_id=?";
	final static public String deleteEmployee_O = "DELETE FROM employee_official_details WHERE emp_id=?";

	final static public String selectEmployeeR1V = "Select * from ( Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id and eod.emp_id=?) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id ";
	final static public String selectEmployeeR2V = "SELECT * FROM employee_official_details eod, employee_personal_details epd, department_info d WHERE eod.depart_id=d.dept_id and epd.emp_per_id=supervisor_emp_id AND emp_off_id=?";
	final static public String selectEmployeeR2V1 = "SELECT * FROM employee_official_details eod, employee_personal_details epd, designation_info di, department_info d WHERE eod.designation_id=di.desig_id and eod.depart_id=d.dept_id and epd.emp_per_id=supervisor_emp_id AND emp_id=?";

	final static public String selectEmployee1V = "select * from work_location_info wl RIGHT JOIN (select *,d.wlocation_id as wlocationl_id from department_info de RIGHT JOIN (SELECT * FROM  employee_personal_details epd, employee_official_details eod WHERE eod.emp_id=epd.emp_per_id and epd.emp_per_id=?) d ON d.depart_id=de.dept_id) w ON wl.wlocation_id =w.wlocationl_id ";

	final static public String selectEmployee1Details = "SELECT * FROM employee_personal_details WHERE emp_per_id = ?";
	final static public String selectEmployee2Details = "SELECT * FROM employee_official_details WHERE emp_id = ? ";
	final static public String selectEmployeeDetails = "SELECT * FROM employee_official_details";
	final static public String selectEmployee2V = "SELECT * FROM employee_official_details eod, employee_personal_details epd, designation_info di, department_info d, work_location_info wl WHERE eod.designation_id=di.desig_id and eod.depart_id=d.dept_id and epd.emp_per_id=eod.emp_id AND wl.wlocation_id =d.wlocation_id AND emp_per_id = ? ";
	final static public String selectEmployee3V = "SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE eod.supervisor_emp_id = epd.emp_per_id AND emp_id = ? ";

	final static public String selectAttendanceRoster = "select * from employee_personal_details epd left join roster_details rd on rd.emp_id = epd.emp_per_id and _date between ? AND ? order by emp_id";
	final static public String selectAttendanceActual = "select * from employee_personal_details epd left join attendance_details ad on ad.emp_id = epd.emp_per_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ? order by emp_id,to_date(in_out_timestamp::text, 'YYYY-MM-DD'),in_out";
	final static public String selectActualEmployees = "SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id and is_alive=true order by emp_fname, emp_lname";
	final static public String selectMaxEmpId = "SELECT max(emp_per_id) from employee_personal_details";

//	final static public String insertEmployeeP = "INSERT INTO employee_personal_details (empcode, emp_fname,emp_mname, emp_lname, emp_email, emp_address1, emp_address2, " +
//			" emp_city_id, emp_state_id, emp_country_id, emp_pincode, emp_address1_tmp, emp_address2_tmp, emp_city_id_tmp, emp_state_id_tmp, emp_country_id_tmp, emp_pincode_tmp, " +
//			" emp_contactno, joining_date, emp_pan_no,emp_pf_no,emp_gpf_no, emp_gender, emp_date_of_birth, emp_bank_name, emp_bank_acct_nbr, emp_email_sec, skype_id, " +
//			" emp_contactno_mob, emergency_contact_name, emergency_contact_no, passport_no, passport_expiry_date, blood_group, marital_status,emp_date_of_marriage, " +
//			" approved_flag, emp_entry_date, emp_status, salutation, doctor_name, doctor_contact_no, uid_no, uan_no, emp_esic_no, added_by,emp_bank_name2,emp_bank_acct_nbr_2) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, " +
//			"?,?,?,?, ?,?,?,?, ?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?,?,?, ?,?,?,?) ";

//===start parvez date: 12-08-2022 Note add emergency_contact_relation,emp_other_bank_name,emp_other_bank_name2===	
	final static public String insertEmployeeP = "INSERT INTO employee_personal_details (empcode, emp_fname,emp_mname, emp_lname, emp_email, emp_address1, emp_address2, " +
		" emp_city_id, emp_state_id, emp_country_id, emp_pincode, emp_address1_tmp, emp_address2_tmp, emp_city_id_tmp, emp_state_id_tmp, emp_country_id_tmp, emp_pincode_tmp, " +
		" emp_contactno, joining_date, emp_pan_no,emp_pf_no,emp_gpf_no, emp_gender, emp_date_of_birth, emp_bank_name, emp_bank_acct_nbr, emp_email_sec, skype_id, " +
		" emp_contactno_mob, emergency_contact_name, emergency_contact_no, passport_no, passport_expiry_date, blood_group, marital_status,emp_date_of_marriage, " +
		" approved_flag, emp_entry_date, emp_status, salutation, doctor_name, doctor_contact_no, uid_no, uan_no, emp_esic_no, added_by,emp_bank_name2,emp_bank_acct_nbr_2," +
		"is_medical_professional,emp_kmc_no,emp_knc_no,renewal_date,emp_mrd_no,emp_other_bank_acct_ifsc_code,emp_other_bank_acct_ifsc_code_2,pf_start_date,emergency_contact_relation,emp_other_bank_name,emp_other_bank_name2,emp_other_bank_branch,emp_other_bank_branch2) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, " +
		"?,?,?,?, ?,?,?,?, ?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?) ";
//===end parvez date: 12-08-2022===
	
	final static public String insertEmployeeO = "INSERT INTO employee_official_details (depart_id, supervisor_emp_id,hod_emp_id, service_id, available_days, emp_id, wlocation_id, is_roster, is_attendance, emptype, first_aid_allowance, grade_id,paycycle_duration,payment_mode, org_id,emp_hr,biometrix_id ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	final static public String updateEmployeeP = "UPDATE employee_personal_details SET emp_fname=?,emp_mname=?, emp_lname=?, emp_email=?, emp_address1=?, emp_address2=?, emp_city_id=?, emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_address1_tmp=?, emp_address2_tmp=?, emp_city_id_tmp=?, emp_state_id_tmp=?, emp_country_id_tmp=?, emp_pincode_tmp=?, emp_contactno=?, emp_pan_no = ?, emp_pf_no = ?, emp_gpf_no = ?, emp_gender=?, emp_date_of_birth=?, emp_bank_name=?, emp_bank_acct_nbr=?, emp_contactno_mob=?, emergency_contact_name=?, emergency_contact_no=?, passport_no=?, passport_expiry_date=?, blood_group=?, marital_status=?, emp_date_of_marriage=?, salutation=? WHERE emp_per_id=?";
	final static public String updateEmployeePE = "UPDATE employee_personal_details SET emp_fname=?,emp_mname=?, emp_lname=?, emp_email=?, emp_address1=?, emp_address2=?, emp_city_id=?, emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_address1_tmp=?, emp_address2_tmp=?, emp_city_id_tmp=?, emp_state_id_tmp=?, emp_country_id_tmp=?, emp_pincode_tmp=?, emp_contactno=?, emp_pan_no = ?, emp_pf_no = ?, emp_gpf_no = ?, emp_gender=?, emp_date_of_birth=?, emp_bank_name=?, emp_bank_acct_nbr=?,  emp_contactno_mob=?, emergency_contact_name=?, emergency_contact_no=?, passport_no=?, passport_expiry_date=?, blood_group=?, marital_status=?, emp_date_of_marriage=?, salutation=? WHERE emp_per_id=?";
	final static public String updateEmployeePE1 = "UPDATE employee_personal_details SET emp_fname=?, emp_lname=?, emp_email=?, emp_address1=?, emp_address2=?, emp_city_id=?, emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_contactno=? WHERE emp_per_id=?";
	final static public String updateEmployeeO = "UPDATE employee_official_details SET designation_id =?, depart_id=?, supervisor_emp_id=?, service_id=?, available_days=?, wlocation_id=?, is_roster=?, emptype=?, first_aid_allowance=? WHERE emp_id=?";
	final static public String updateEmployeeO1 = "UPDATE employee_official_details SET depart_id=?, wlocation_id=?, emptype=?, grade_id=? WHERE emp_id=?";
	final static public String updateEmployeeRoster = "UPDATE employee_official_details SET is_roster=? WHERE emp_id=?";

	final static public String selectNotifications = "Select * FROM notifications where notification_code=?";
	final static public String selectNotifications1 = "Select * FROM notifications";
	final static public String selectSettings = "select * FROM settings";
	final static public String insertLogin_timestamp = "Insert into login_timestamp (login_timestamp, emp_id, client_ip) values (?, ?, ?)";

	final static public String selectFinancialYears = "select * FROM financial_year_details order by financial_year_to desc";

	final static public String insertRosterData = "insert into roster_details ( _date, emp_id, service_id, _from, _to,  actual_hours, user_id) values (?,(select emp_per_id from employee_personal_details where empcode=?),(select service_id from services where service_code=?), ?,?,?, ?)";

	final static public String selectSearchServices = "SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and service_id=? and _date=? order by emp_id, _date desc";
	final static public String selectRosterHSDetails = "SELECT * FROM roster_details rd, services s WHERE rd.service_id = s.service_id and _date between ? and ?  order by emp_id, _date desc";
	final static public String selectRosterDetails = "SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and _date between ? and ? and service_id>0  order by emp_fname, emp_id, _date desc, _from";
	final static public String selectRosterDetails_WL = "SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and _date between ? and ? and service_id>0  order by emp_fname, emp_id, _date desc, _from";
	final static public String selectRosterDetailsPayroll = "SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and _date between ? and ?  order by emp_id, _date desc";
	final static public String selectRosterDetails_Alpha = "SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and _date between ? and ? and emp_fname like ?  order by emp_id, _date desc";

	final static public String selectRosterClockDetails = "SELECT * FROM roster_details WHERE emp_id = ? and _date = ?";
	final static public String selectRosterClockDetails12 = "SELECT * FROM roster_details WHERE emp_id = ? and _date = ? and service_id=?";

	final static public String selectRosterClockDetails_N_IN = "SELECT * FROM roster_details WHERE emp_id = ? and _date = ? and (attended = 0 or attended = 2) and (_to>? OR _from>_to) order by _from limit 1";
	final static public String selectRosterClockDetails_N1_IN = "SELECT * FROM roster_details WHERE emp_id = ? and _date = ? ";
	final static public String selectRosterClockDetails_N2_IN = "SELECT * FROM roster_details WHERE emp_id = ? and _date = ? and (attended = 0 or attended = 2) order by _from desc limit 1";
	final static public String selectRosterClockDetails_N_OUT = "SELECT * FROM roster_details WHERE emp_id = ? and _date = ? and attended = 1 order by _from limit 1";

	final static public String selectRosterClockDetails_PREV_N_OUT = "SELECT * FROM roster_details WHERE emp_id = ? and _date = ? and attended = 1 order by _from limit 1";
	final static public String selectRoster_N_COUNT = "SELECT count(*) as cnt FROM roster_details WHERE emp_id = ? and _date = ?";
	final static public String selectRosterClockDetails_N = "SELECT * FROM roster_details WHERE emp_id = ? and _date = ? and attended < ? ";
	final static public String selectRosterClockDetails_N1 = "SELECT * FROM roster_details WHERE emp_id = ? and _date = ? and service_id=? ";

	final static public String selectRosterClockDetails1 = "SELECT * FROM roster_details WHERE emp_id = ? and (_date = ? OR _date = ?) order by _date desc limit 1";
	final static public String selectAttendenceClockDetails_N = "SELECT * FROM attendance_details WHERE to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ? and in_out = ? and emp_id =? and service_id =?";
	final static public String selectAttendenceClockDetailsInOut_N = "SELECT * FROM attendance_details WHERE to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ? and (in_out = ? OR in_out = ?) and emp_id =? and service_id=?";
	final static public String selectAttendenceClockDetailsInOut_N1 = "SELECT * FROM attendance_details WHERE to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ? and (in_out = ? OR in_out = ?) and emp_id =?";
	final static public String selectAttendenceClockDetails1_N = "SELECT * FROM attendance_details WHERE to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ? and emp_id =? and service_id =?";

	final static public String selectRosterDependent = "SELECT * FROM employee_official_details where emp_id = ?";

	final static public String selectRosterDetails_M = "select * from (SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and _date between ? and ? and emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) order by emp_id, _date desc) e order by emp_fname";

	final static public String selectRosterDetails1 = "SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and _date between ? and ?  and emp_id=? order by emp_id, _date desc, roster_id";
	final static public String selectRosterDetails11 = "SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and _date between ? and ? order by emp_id, _date desc, roster_id";
	final static public String selectRosterDetails2 = "SELECT * FROM roster_details WHERE _date=? AND emp_id=(select emp_per_id from employee_personal_details where empcode=?) and service_id=(select service_id from services where service_code=?)";
	final static public String selectRosterDetails3 = "SELECT * FROM employee_official_details eod, roster_details rd WHERE eod.emp_id=rd.emp_id and _date=? AND eod.emp_id=?";
	final static public String updateRosterDetails2 = "UPDATE roster_details set _date=?,emp_id=(select emp_per_id from employee_personal_details where empcode=?) , service_id= (select service_id from services where service_code=?), _from=?, _to=?, actual_hours=?, user_id=? where roster_id=? ";

	final static public String selectRosterDetailsV = "SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id AND rd.emp_id=? and _date between ? and ? order by _date desc";
	final static public String selectRosterDetailsED = "SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id AND rd.emp_id=? and _date>= ? order by _date desc LIMIT 3";
	final static public String selectRosterEmployeeDetails = "SELECT * FROM (SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and _date>= ? and emp_id=? order by _date, _from)a LIMIT 3";

	final static public String selectClockEntriesAID = "SELECT * FROM attendance_details where atten_id = ?";
	final static public String selectClockEntries_A = "SELECT * FROM attendance_details WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY emp_id, in_out_timestamp desc";
	final static public String selectEarlyLateEntries = "SELECT * FROM attendance_details WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY emp_id, in_out_timestamp desc";
	final static public String selectEarlyLateEntries_Exception = "select * from roster_details rd left join attendance_details ad on rd.emp_id=ad.emp_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') where rd._date BETWEEN ? AND ? and rd.emp_id>0 order by rd.emp_id, _date desc, to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') desc";
	final static public String selectClockEntries = "SELECT * FROM attendance_details WHERE  emp_id = ? AND TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD'),service_id, in_out";
	final static public String selectClockEntriesR = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id ORDER BY emp_id, in_out_timestamp desc";
	final static public String selectClockEntriesRR = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id and approved=? ORDER BY emp_id, in_out_timestamp desc";
	final static public String selectClockEntriesR1 = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc";
	final static public String selectClockEntriesR1_A = "SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date  and rd.service_id=a.service_id) t WHERE t._date BETWEEN ? AND ?  ORDER BY empl_id, in_out_timestamp";
	final static public String selectClockEntriesR1_M = "SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a RIGHT JOIN roster_details rd ON a.emp_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ? AND empl_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) ORDER BY empl_id, in_out_timestamp";
	final static public String selectClockEntriesR1_HRM = "SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_official_details eod WHERE eod.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a RIGHT JOIN roster_details rd ON a.empl_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ? AND wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) ORDER BY empl_id, in_out_timestamp";

//===start parvez date: 30-11-2021===
	final static public String selectClockEntriesR1_E = "SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? AND emp_id = ?  ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ? AND t.empid = ?  order by _date desc, serviceid, in_out";
//===end parvez date: 30-11-2021===
	final static public String selectClockEntriesR1_ES = "SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? AND emp_id = ?  ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ? AND t.empid = ? and t.serviceid=?  order by _date, serviceid, in_out";
	final static public String selectClockEntriesR1_E2 = "select * from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab, employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details where is_roster=true) order by _date desc, empid, serviceid, in_out";
	final static public String selectClockEntriesR1_E1 = "SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? AND emp_id = ?  ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date and rd.service_id=a.service_id ) t WHERE TO_DATE(t.in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? order by in_out_timestamp, in_out";
	final static public String selectClockEntriesR2_E = "SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a RIGHT JOIN roster_details rd ON a.emp_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ? AND empl_id = ?  and hours_worked > actual_hours order by in_out_timestamp";
	final static public String selectClockEntriesR3_E = "SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a LEFT JOIN roster_details rd ON a.emp_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date and rd.service_id=a.service_id) t WHERE t._date BETWEEN ? AND ? order by  emp_fname, empl_id, in_out_timestamp desc";

	final static public String selectClockEntriesR1_E2_Admin = "select * from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab, employee_personal_details epd  where ab.empid=epd.emp_per_id order by emp_fname, emp_lname, empid, serviceid, in_out";
	final static public String selectClockEntriesR1_E2_Manager = "select * from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab, employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details where supervisor_emp_id = ?) order by emp_fname, emp_lname, empid, serviceid, in_out";
	final static public String selectClockEntriesR1_E2_HR = "select * from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab, employee_personal_details epd, employee_official_details eod where ab.empid = eod.emp_id and ab.empid=epd.emp_per_id and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) order by emp_fname, emp_lname, empid, serviceid, in_out";

	final static public String selectClockEntriesR3_E_Actual = "SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT' order by  emp_fname, emp_lname, empl_id, in_out_timestamp desc";
	final static public String selectClockEntriesR3_E_Roster = "SELECT *, rd.emp_id as empl_id FROM roster_details rd, employee_personal_details epd  where rd.emp_id=epd.emp_per_id  and _date between ? AND ? order by  emp_fname, empl_id, _date desc";

	final static public String selectClockEntriesR3_EManager = "SELECT * FROM (SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a LEFT JOIN roster_details rd ON a.emp_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ? order by  emp_fname, empl_id, in_out_timestamp desc) emp, employee_official_details eod where emp.empl_id=eod.emp_id and supervisor_emp_id =?";

	final static public String selectClockEntriesR3_EManager_Actual = "SELECT * FROM (SELECT * FROM (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a order by  a.emp_fname, empl_id, a.in_out_timestamp desc) emp, employee_official_details eod where emp.empl_id=eod.emp_id and supervisor_emp_id =?";
	final static public String selectClockEntriesR3_EManager_Roster = "SELECT * FROM (SELECT *, rd.emp_id as empl_id FROM employee_personal_details epd, roster_details rd where epd.emp_per_id=rd.emp_id and rd._date BETWEEN ? AND ? order by emp_fname, empl_id, _date desc ) t, employee_official_details eod where t.empl_id=eod.emp_id and supervisor_emp_id =?";

	final static public String selectClockEntriesR1L = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and early_late > 0 ORDER BY in_out_timestamp desc";
	final static public String selectClockEntriesR1E = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and early_late < 0 ORDER BY in_out_timestamp desc";
	final static public String selectClockEntriesR2 = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? AND in_out=? ORDER BY in_out_timestamp desc";
	final static public String selectClockEntriesR2L = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? AND in_out=? and early_late > 0  ORDER BY in_out_timestamp desc";
	final static public String selectClockEntriesR2E = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? AND in_out=?  and early_late < 0 ORDER BY in_out_timestamp desc";
	final static public String selectClockEntriesR3 = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id and early_late != 0 ORDER BY in_out_timestamp desc";
	final static public String selectClockEntriesR4 = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id  and atten_id=?";
	final static public String selectClockEntriesR5 = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id and emp_id =? and To_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and early_late != 0 ORDER BY in_out_timestamp desc";

	final static public String selectClockEntriesManagerR = "SELECT * FROM attendance_details ad, employee_personal_details epd, roster_details rd  WHERE epd.emp_per_id=ad.emp_id  and ad.emp_id=rd.emp_id and rd.emp_id = epd.emp_per_id and rd._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY_MM_DD') and epd.emp_per_id in (SELECT emp_id FROM employee_official_details where supervisor_emp_id=?) and TO_DATE(in_out_timestamp::text, 'YYYY_MM_DD') between ? and ?  ORDER BY rd.emp_id, in_out_timestamp";
	final static public String selectClockEntriesAdminRO = "select * from (SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id and TO_DATE(in_out_timestamp::text, 'YYYY_MM_DD') between ? and ?  ORDER BY emp_id, in_out_timestamp) a order by emp_fname, emp_lname, TO_DATE(in_out_timestamp::text, 'YYYY_MM_DD'), service_id";
	final static public String selectClockEntriesAdminROAlpha = "select * from (SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id and TO_DATE(in_out_timestamp::text, 'YYYY_MM_DD') between ? and ?  and upper(emp_fname) like ? ORDER BY emp_id, in_out_timestamp) a order by emp_fname, emp_lname, TO_DATE(in_out_timestamp::text, 'YYYY_MM_DD'), service_id";
	final static public String selectClockEntriesAdminR_N = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id and TO_DATE(in_out_timestamp::text, 'YYYY_MM_DD') between ? and ?  ORDER BY emp_fname, emp_lname, emp_id, to_date(In_out_timestamp::text, 'YYYY-MM-DD'), service_id, in_out";
	final static public String selectClockEntriesUA = "SELECT * FROM attendance_details ad, employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id and approved=0 ORDER BY in_out_timestamp desc";
	final static public String updateClockEntries = "UPDATE attendance_details SET approved=?, comments=?, in_out_timestamp=? WHERE atten_id=?";
	final static public String updateClockEntries1 = "UPDATE attendance_details SET in_out_timestamp_actual=?, in_out_timestamp=?, hours_worked=? WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ?";
	final static public String updateClockEntries2 = "UPDATE attendance_details SET in_out_timestamp=?, hours_worked=? WHERE emp_id=? and in_out=? and service_id = ? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ?";
	final static public String updateClockEntries22 = "UPDATE attendance_details SET in_out_timestamp=?, hours_worked=?, early_late=? WHERE emp_id=? and in_out=? and service_id = ? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ?";

	final static public String updateClockEntries21 = "UPDATE attendance_details SET in_out_timestamp=?, hours_worked=?, early_late=? WHERE emp_id=? and in_out=? and service_id = ? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ?";

	final static public String updateClockEntries2_N = "UPDATE attendance_details SET in_out_timestamp=?, hours_worked=? , approved=?, approval_emp_id=?  WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? and service_id=?";
	final static public String updateClockEntries3_N = "UPDATE attendance_details SET hours_worked=? WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? and service_id=?";
	final static public String selectClockEntries1_N = "select * from attendance_details  WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? and service_id=?";

	final static public String insertClockEntries1 = "INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual , hours_worked, in_out) VALUES (?,?,?,?,?)";
	final static public String insertClockEntries1_N = "INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual , hours_worked, in_out, service_id, approved, approval_emp_id) VALUES (?,?,?,?,?,?,?,?)";
	final static public String insertClockEntries1_N1 = "INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual , hours_worked, in_out, service_id, approval_emp_id, early_late, approved) VALUES (?,?,?,?,?,?,?,?,?)";

	final static public String insertClockEntries11_N = "INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual , hours_worked, in_out, service_id, approved, approval_emp_id, early_late) VALUES (?,?,?,?,?,?,?,?,?)";

	final static public String selectRosterHours = "select sum(actual_hours) as roster_hours, _date from roster_details where _date between ? and ? group by _date";
	final static public String selectRosterHoursE = "select sum(actual_hours) as roster_hours, _date from roster_details where _date between ? and ? and emp_id = ? group by _date";
	final static public String selectActualHours = "select sum(hours_worked) as hours_worked, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' group by to_date(in_out_timestamp::text, 'YYYY-MM-DD')";
	final static public String selectActualHoursE = "select sum(hours_worked) as hours_worked, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and emp_id = ? group by to_date(in_out_timestamp::text, 'YYYY-MM-DD')";
	final static public String selectRosterHoursManager = "select sum(actual_hours) as roster_hours, _date from roster_details rd, employee_official_details eod where rd.emp_id = eod.emp_id and supervisor_emp_id =? and _date between ? and ? group by _date";
	final static public String selectActualHoursManager = "select sum(hours_worked) as hours_worked, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date from attendance_details ad, employee_official_details eod where ad.emp_id = eod.emp_id and supervisor_emp_id =? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' group by to_date(in_out_timestamp::text, 'YYYY-MM-DD') ";

	final static public String selectLateHours = "select sum(early_late) as latecount, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, in_out from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and early_late>0 group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out";
	final static public String selectLateHoursManager = "select sum(early_late) as latecount, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, in_out from attendance_details ad, employee_official_details eod where eod.emp_id=ad.emp_id and supervisor_emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and early_late>0 group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out";

	final static public String selectLateHoursEmp = "select * from (select sum(early_late) as latecount, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, in_out, emp_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and early_late>0 group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out, emp_id) a, employee_official_details eod where eod.emp_id=a.emp_id order by a.emp_id";
	final static public String selectLateHoursWLocation = "select * from (select sum(early_late) as latecount, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, in_out, emp_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD')  between ? and ? group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out, emp_id) a, employee_official_details eod where eod.emp_id=a.emp_id order by wlocation_id";
	final static public String selectLateHoursDepartment = "select * from (select sum(early_late) as latecount, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, in_out, emp_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD')  between ? and ? and early_late>0 group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out, emp_id) a, employee_official_details eod where eod.emp_id=a.emp_id order by depart_id";
	final static public String selectLateHoursService = "select *, a.service_id as serviceid from (select sum(early_late) as latecount, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, in_out, emp_id, service_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD')  between ? and ? and early_late>0 group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out, emp_id, service_id) a, employee_official_details eod where eod.emp_id=a.emp_id order by a.service_id";
	final static public String selectLateHoursUserType = "select * from (select sum(early_late) as latecount, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, in_out, emp_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and early_late>0 group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out, emp_id) a, employee_official_details eod, user_details ud where eod.emp_id =ud.emp_id and ud.emp_id = a.emp_id and eod.emp_id=a.emp_id order by usertype_id";

	final static public String selectLateHoursEmpManager = "select * from (select sum(early_late) as latecount, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, in_out, ad.emp_id  from attendance_details ad, employee_official_details eod where ad.emp_id=eod.emp_id and supervisor_emp_id =? and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and  early_late>0 group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out, ad.emp_id) a, employee_personal_details epd where epd.emp_per_id=a.emp_id order by emp_fname";

	final static public String selectEmployeeRosterHours = "select * from (select sum(actual_hours) as roster_hours, _date, emp_id from roster_details where _date between ? and ? group by _date, emp_id) a, employee_personal_details epd where a.emp_id=epd.emp_per_id order by emp_fname, emp_id";
	final static public String selectEmployeeRosterHoursManager = "select * from (select * from (select sum(actual_hours) as roster_hours, _date, emp_id from roster_details where _date between ? and ? group by _date, emp_id) a, employee_personal_details epd where a.emp_id=epd.emp_per_id order by emp_fname, emp_id) emp, employee_official_details eod where emp.emp_id=eod.emp_id and supervisor_emp_id =?";
	final static public String selectEmployeeActualHours = "select sum(hours_worked) as hours_worked, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, emp_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), emp_id";
	final static public String selectEmployeeActualHoursManager = "select * from (select sum(hours_worked) as hours_worked, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date, emp_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' group by to_date(in_out_timestamp::text, 'YYYY-MM-DD'), emp_id) emp, employee_official_details eod where emp.emp_id=eod.emp_id and supervisor_emp_id =?";

	final static public String selectMyClockEntries = "select * from attendance_details WHERE emp_id=? order by in_out_timestamp_actual desc, service_id, in_out desc limit 8";

	final static public String selectRosterPolicy = "SELECT * FROM roster_policy where org_id =? order by mode, time_type,abs(time_value), roster_policy_id";
	final static public String selectRosterPolicyV = "SELECT * FROM roster_policy WHERE roster_policy_id=?";
	final static public String insertRosterPolicy = "INSERT INTO roster_policy (time_value, message, time_type, mode, isapproval, user_id, effective_date, entry_date, org_id) VALUES (?,?,?,?,?,?,?,?,?)";
	final static public String deleteRosterPolicy = "DELETE FROM roster_policy WHERE roster_policy_id=?";
	final static public String updateRosterPolicy = "UPDATE roster_policy SET time_value=?, message=?, time_type=?, mode=?, isapproval=?, user_id=? WHERE roster_policy_id=?";

	final static public String insertRosterDetails = "INSERT INTO roster_details (_date, emp_id, service_id, _from, _to, actual_hours, entry_date) VALUES (?,?,?,?,?,?,?)";
	final static public String updateRosterDetails = "UPDATE roster_details SET _from=?, _to=?, actual_hours=?, entry_date=? WHERE roster_id=?";
	final static public String updateRosterDetails_FROM = "UPDATE roster_details SET _from=?, actual_hours=? WHERE roster_id=?";
	final static public String updateRosterDetails_TO = "UPDATE roster_details SET _to=?, actual_hours=? WHERE roster_id=?";
	final static public String deleteRosterDetails = "DELETE FROM roster_details WHERE roster_id=?";

	final static public String selectPayrollEmpId = "SELECT * FROM payroll_generation p, employee_personal_details epd, salary_details sd WHERE sd.salary_head_id=p.salary_head_id and p.emp_id = epd.emp_per_id and entry_date between ? AND ? order by emp_fname, emp_lname, emp_id";
	final static public String selectPayrollDeptId = "SELECT * FROM payroll_generation p, employee_personal_details epd, employee_official_details eod, salary_details sd WHERE sd.salary_head_id=p.salary_head_id and eod.emp_id=p.emp_id and p.emp_id=epd.emp_per_id and entry_date between ? AND ? order by depart_id";
	final static public String selectPayrollServiceId = "SELECT *, p.service_id as servid FROM payroll_generation p, employee_personal_details epd, employee_official_details eod, salary_details sd WHERE sd.salary_head_id=p.salary_head_id and eod.emp_id=p.emp_id and p.emp_id=epd.emp_per_id and entry_date between ? AND ? order by p.service_id";
	final static public String selectPayrollWLocationId = "SELECT * FROM payroll_generation p, employee_personal_details epd, employee_official_details eod, salary_details sd WHERE sd.salary_head_id=p.salary_head_id and eod.emp_id=p.emp_id and p.emp_id=epd.emp_per_id and entry_date between ? AND ? order by wlocation_id";
	final static public String selectPayrollUserTypeId = "SELECT * FROM payroll_generation p, employee_personal_details epd, user_details ud, salary_details sd WHERE sd.salary_head_id=p.salary_head_id and ud.emp_id=p.emp_id and p.emp_id=epd.emp_per_id and entry_date between ? AND ? order by usertype_id";

	final static public String selectPayrollPolicy = "SELECT * FROM payroll_policy pp, designation_info di, services s WHERE di.desig_id=pp.desig_id and s.service_id=pp.service_id order by paymode, s.service_id";
	final static public String selectPayrollPolicy1 = "SELECT * FROM payroll_policy order by payroll_policy_id";
	final static public String selectPayrollPolicy2 = "SELECT * FROM payroll_policy where desig_id=? order by payroll_policy_id";
	final static public String selectPayrollPolicyV1 = "SELECT * FROM payroll_policy WHERE payroll_policy_id=?";
	final static public String selectPayrollPolicyV = "SELECT * FROM payroll_policy WHERE desig_id=? and service_id=? ";
	final static public String selectPayrollPolicyV2 = "SELECT * FROM payroll_policy WHERE desig_id=? ";
	final static public String insertPayrollPolicy = "INSERT INTO payroll_policy (user_id, desig_id, paymode, fxdamount, service_id, monamount, tuesamount, wedamount, thursamount, friamount, satamount, sunamount, loading, loading_mon, loading_tue, loading_wed, loading_thurs, loading_fri, loading_sat, loading_sun) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static public String deletePayrollPolicy = "DELETE FROM payroll_policy WHERE payroll_policy_id=?";
	final static public String updatePayrollPolicy = "UPDATE payroll_policy SET user_id=?, paymode=?, fxdamount=?, monamount=?, tuesamount=?, wedamount=?, thursamount=?, friamount=?, satamount=?, sunamount=?, loading=?, loading_mon=?, loading_tue=?, loading_wed=?, loading_thurs=?, loading_fri=?, loading_sat=?, loading_sun=? WHERE payroll_policy_id=?";

	final static public String selectEmployeeServiceRateDetails = "SELECT * FROM payroll_policy p, employee_personal_details epd, employee_official_details eod, services s where  epd.emp_per_id=eod.emp_id and p.desig_id=eod.designation_id and  eod.emptype=p.emptype and  p.service_id=s.service_id order by eod.emp_id";

	final static public String insertPayroll = "INSERT INTO payroll (emp_id, generate_date, user_id, _date, is_approved, _in, _out, total_time, date_from, date_to, pay_mode, service_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	final static public String selectPaySlipDate = "select sum(income_amount) as amount, generate_date, date_from, date_to from payroll where emp_id =? group by generate_date, date_from, date_to";
	final static public String selectPaySlipDateAdmin = "select sum(income_amount) as amount, generate_date, date_from, date_to from payroll group by generate_date, date_from, date_to";

	final static public String selectTimeSheet1 = "SELECT * FROM ( SELECT * FROM attendance_details ad, employee_personal_details epd WHERE ad.emp_id = epd.emp_per_id and approved != 0 and early_late !=0 order by emp_id, in_out_timestamp desc) a, roster_details rd WHERE  rd.emp_id = a.emp_id and rd._date = TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')";
	final static public String selectTimeSheet2 = "SELECT * FROM ( SELECT * FROM attendance_details ad, employee_personal_details epd WHERE ad.emp_id = epd.emp_per_id and approved != 0 and early_late <0 and in_out='IN' order by emp_id, in_out_timestamp desc) a, roster_details rd WHERE  rd.emp_id = a.emp_id and rd._date = TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')";
	final static public String selectTimeSheet3 = "SELECT * FROM ( SELECT * FROM attendance_details ad, employee_personal_details epd WHERE ad.emp_id = epd.emp_per_id and approved != 0 and early_late >0 and in_out='IN' order by emp_id, in_out_timestamp desc) a, roster_details rd WHERE  rd.emp_id = a.emp_id and rd._date = TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')";
	final static public String selectPresentDays1 = "SELECT count(distinct TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD')) as count FROM attendance_details WHERE emp_id =? and TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ";
	final static public String selectPresentDays1_Manager = "SELECT count(distinct TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD')) as count FROM attendance_details WHERE emp_id in (select emp_id from employee_official_details where supervisor_emp_id=?) and TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ";
	final static public String selectPresentDays1_HRManager = "SELECT count(distinct TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD')) as count FROM attendance_details ad, employee_official_details eod WHERE eod.emp_id = ad.emp_id and  wlocation_id = (select wlocation_id from employee_official_details where emp_id= ?) and TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? and ?";
	final static public String selectPresentDays2 = "Select count(emp_id) as present, wlocation_name  FROM (Select distinct eod.emp_id, wlocation_name FROM attendance_details ad, work_location_info wi, employee_official_details eod  WHERE eod.emp_id=ad.emp_id and eod.wlocation_id = wi.wlocation_id and (wi.wlocation_name =? OR wi.wlocation_name = ?) and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD' ) = ?) as aa group by wlocation_name order by wlocation_name LIMIT 2";
	final static public String selectApprovalsCount = "SELECT approved, count(approved) as count FROM attendance_details where TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ? and emp_id = ? group by approved";
	final static public String selectApprovalsCount_All = "SELECT approved, count(approved) as count FROM attendance_details where emp_id = ? group by approved";
	final static public String selectApprovalsCountAdmin = "SELECT approved, count(approved) as count FROM attendance_details where TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') = ? group by approved";
	final static public String selectApprovals = "SELECT * FROM attendance_details ad, employee_personal_details epd  where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') = ? and ad.emp_id = epd.emp_per_id";
	final static public String selectApprovalsManager = "SELECT * FROM attendance_details ad, employee_personal_details epd  where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and ad.emp_id = epd.emp_per_id order by in_out_timestamp desc";
	final static public String selectPendingApprovalsHRManager = "SELECT * FROM attendance_details ad, employee_official_details eod  where wlocation_id in (select wlocation_id from employee_official_details where emp_id = ?) and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and ad.emp_id = eod.emp_id and approved = -2 order by in_out_timestamp desc";
	final static public String selectApprovalsCountForManager = "SELECT approved, count(approved) as count FROM attendance_details where TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ? and emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) group by approved";
	final static public String selectApprovalsCountForHRManager = "SELECT approved, count(approved) as count FROM attendance_details ad, employee_official_details eod  where eod.emp_id=ad.emp_id and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ? and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) group by approved";
	final static public String selectEmployeeCount = "Select count(eod.emp_id) as empcount, wlocation_name FROM work_location_info wi, employee_official_details eod, employee_personal_details epd  WHERE epd.emp_per_id=eod.emp_id and eod.wlocation_id=wi.wlocation_id group by wlocation_name order by wlocation_name LIMIT 2";

	final static public String selectDatesPrev = "Select * from (SELECT * FROM alldates where _date< ? order by _date) a order by _date desc LIMIT 14";
	final static public String selectDatesNext = "SELECT * FROM (SELECT * FROM alldates where _date> ? order by _date LIMIT 14) a order by _date desc";
	final static public String selectDatesAsc = "SELECT * FROM alldates where _date>=? order by _date LIMIT 14";
	final static public String selectDatesDesc = "SELECT * FROM alldates where _date<=?  and _date>=? order by _date desc LIMIT 14";
	final static public String selectDates = "SELECT * FROM alldates where _date  BETWEEN ? AND ? order by _date";

	final static public String selectDatesDescPayroll = "select * from (SELECT * FROM alldates where _date> (SELECT MAX(_date) from payroll) order by _date LIMIT 14 ) as payroll order by _date desc ";
	final static public String selectDatesAscPayroll = "SELECT * FROM alldates where _date> (SELECT MAX(_date) from payroll) order by _date LIMIT 14";
	final static public String selectDatesED = "SELECT * FROM alldates where _date>=? order by _date LIMIT 3";

	final static public String selectDataForEmployeeAnalysis_Emp = "select *, rd_epd.emp_id as empl_id from attendance_details ad RIGHT JOIN (select * from roster_details rd, employee_personal_details epd where epd.emp_per_id = rd.emp_id ) rd_epd ON rd_epd._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') order by empl_id, _date desc";
	final static public String selectDataForEmployeeAnalysis = "select *, rd_epd.emp_id as empl_id from attendance_details ad RIGHT JOIN (select * from roster_details rd, employee_personal_details epd where epd.emp_per_id = rd.emp_id and _date = ?) rd_epd ON rd_epd._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_epd.emp_id and ad.in_out='OUT' order by empl_id";

	final static public String selectAdditionalHoursEmp = "select *, rd_eod.rempid as empl_id from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod where eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT' order by empl_id";
	final static public String selectAdditionalHoursWLocation = "select *, rd_eod.rempid as empl_id from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod where eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT' order by wlocation_id";
	final static public String selectAdditionalHoursDepartment = "select *, rd_eod.rempid as empl_id from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod where eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT' order by depart_id";
	final static public String selectAdditionalHoursService = "select *, ad.service_id as serviceid from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod where eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT' order by ad.service_id";
	final static public String selectAdditionalHoursUserType = "select *, rd_eod.rempid as empl_id from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod, user_details ud where eod.emp_id=ud.emp_id and ud.emp_id=rd.emp_id and eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT' order by usertype_id";

	final static public String selectAdditionalHoursEmpManager = "SELECT *FROM (select *, rd_epd.emp_id as empl_id from attendance_details ad, (select * from roster_details rd, employee_personal_details epd where epd.emp_per_id = rd.emp_id and _date between ? and ?) rd_epd WHERE rd_epd._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_epd.emp_id and ad.in_out='OUT' order by emp_fname, empl_id) emp, employee_official_details eod where emp.empl_id=eod.emp_id and supervisor_emp_id =?";
	final static public String selectDataForEmployeeAnalysis_M = "select *, rd_epd.emp_id as empl_id from attendance_details ad RIGHT JOIN (select * from roster_details rd, employee_personal_details epd where epd.emp_per_id = rd.emp_id and _date = ? and emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?)) rd_epd ON rd_epd._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_epd.emp_id and ad.in_out='OUT' order by empl_id";
	final static public String selectMaxPayrollDate = "select max(_date) as _date from payroll";

	final static public String selectPayrollRatesForUpdate1_N = "select * from payroll p where (rate is null OR is_approved = false) order by emp_id";
	final static public String selectPayrollRatesForUpdate2 = "select * from payroll_policy pp, employee_official_details eod where eod.designation_id = pp.desig_id and eod.emp_id=? and pp.service_id =? order by payroll_policy_id desc limit 1";
	final static public String updatePayrollRate_N = "UPDATE payroll SET rate =?, loading=? where _date=? and emp_id=? and service_id=?";
	final static public String updatePayrollRate1 = "update payroll set income_amount = ?, deduction_amount=?, pay_amount=?, first_aid_allowance=?, is_approved=? where payroll_id=?";

	final static public String selectMyobData = "SELECT * FROM payroll p, employee_personal_details epd, services s where p.emp_id = epd.emp_per_id and p.service_id=s.service_id and p.generate_date=?";

	final static public String selectServicesEmployeeRoster = "SELECT * FROM roster_details rd, services s, employee_personal_details epd WHERE epd.emp_per_id = rd.emp_id and rd.service_id = s.service_id and _date = ?";

	// ================Leave Module Start======================

	final static public String insertManagerApproval = "INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,leave_type_id,reason,is_approved) VALUES (?,?,?,?,?,?,?,?)";
	final static public String updateManagerApproval = "UPDATE emp_leave_entry SET emp_id=?,entrydate=?, leave_from=?, leave_to=? ,leave_type_id=?,reason=?,approval_from=?, approval_to_date=?,emp_no_of_leave=?,manager_reason=?,is_approved=?,user_id=?,ishalfday=? WHERE leave_id=?";
	final static public String deleteManagerApproval = "DELETE FROM emp_leave_entry WHERE leave_id=?";
	final static public String selectManagerApproval = "select * from emp_leave_entry ee, emp_leave_type et,leave_type lt where et.leave_type_id=ee.leave_type_id and et.leave_type_id=lt.leave_type_id and ee.leave_id=? and et.level_id = ? order by ee.entrydate desc";
	final static public String selectManagerApprovalR = "select * from employee_personal_details epd, emp_leave_entry ee,leave_type lt where ee.leave_type_id = lt.leave_type_id and epd.emp_per_id = ee.emp_id and upper(epd.emp_fname) like ? order by ee.leave_from desc";
	final static public String selectManagerApprovalR1 = "select * from employee_personal_details epd, emp_leave_entry ee,leave_type lt where ee.leave_type_id = lt.leave_type_id and epd.emp_per_id = ee.emp_id order by ee.leave_from desc";
	final static public String selectManagerApprovalR1Manager = "select * from employee_official_details eod, emp_leave_entry ee,leave_type lt where ee.leave_type_id = lt.leave_type_id and eod.emp_id = ee.emp_id and eod.emp_id in (select emp_id from employee_official_details where supervisor_emp_id  = ?) order by ee.leave_from desc";
	final static public String selectLeaveDoumentPolicy = "select * from leave_type where is_document_required = ?";
	final static public String selectManagerApprovalRP = "select ee.emp_id,lt.leave_type_id,lt.leave_type_name,et.no_of_leave,sum(emp_no_of_leave)as taken from emp_leave_entry ee,emp_leave_type et,user_type ut ,leave_type lt where ut.user_type_id=et.user_type_id and ee.leave_type_id=lt.leave_type_id and ee.leave_type_id=et.leave_type_id and emp_id=? and et.user_type_id=? group by ee.emp_id,lt.leave_type_id,lt.leave_type_name,et.no_of_leave";

	final static public String insertEmployeeLeaveEntry = "INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,leave_type_id,reason,approval_from,approval_to_date, ishalfday, session_no, document_attached, is_approved, ispaid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static public String updateEmployeeLeaveEntry = "UPDATE emp_leave_entry SET leave_from=?, leave_to=?, emp_no_of_leave=? ,leave_type_id=?,reason=?,ishalfday=?, session_no=? WHERE leave_id=?";
	final static public String deleteEmployeeLeaveEntry = "DELETE FROM emp_leave_entry WHERE leave_id=?";
	final static public String selectEmployeeLeaveEntry = "SELECT * FROM emp_leave_entry where leave_id=?";
	final static public String selectEmployeeLeaveEntryR = "select * from (SELECT * FROM emp_leave_entry ee right join leave_type lt on lt.leave_type_id = ee.leave_type_id order by emp_id) a right join employee_personal_details epd on epd.emp_per_id=a.emp_id where is_alive=true order by emp_fname,emp_lname, emp_id";

	final static public String selectEmployeeLeaveEntryV = "select * from (Select ee.emp_id,ee.leave_type_id,et.no_of_leave,et.effective_date_type, sum(emp_no_of_leave)as taken, is_approved, et.effective_date  from emp_leave_entry ee,emp_leave_type et where ee.leave_type_id=et.leave_type_id and level_id= (select dd.level_id from level_details ld, designation_details dd, grades_details gd where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?) limit 1 ) group by ee.emp_id,et.no_of_leave,ee.leave_type_id, is_approved, et.effective_date_type, et.effective_date) a, leave_type lt  where a.leave_type_id = lt.leave_type_id and a.emp_id = ? order by lt.leave_type_name, lt.leave_type_id ";
	final static public String selectEmployeeLeaveEntryV1 = "select * from (Select ee.emp_id,et.leave_type_id,et.no_of_leave,et.effective_date_type, sum(emp_no_of_leave)as taken, is_approved, et.effective_date  from emp_leave_entry ee right  join emp_leave_type et  on ee.leave_type_id=et.leave_type_id where level_id= (select dd.level_id from level_details ld, designation_details dd, grades_details gd where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id and  gd.grade_id = (select grade_id from employee_official_details where emp_id = ?) limit 1 )  group by ee.emp_id,et.no_of_leave,et.leave_type_id, is_approved, et.effective_date_type, et.effective_date) a, leave_type lt  where  a.leave_type_id = lt.leave_type_id  order by lt.leave_type_name, lt.leave_type_id  ";

	final static public String selectEmployeeLeaveEntryVP = "select *, ud.emp_id as approvedby from (select * from emp_leave_entry ee,leave_type lt where  ee.leave_type_id=lt.leave_type_id and ee.leave_type_id=? and ee.emp_id=? order by entrydate desc) a left join user_details ud on ud.user_id = a.user_id  order by entrydate desc, leave_from desc";

	final static public String selectEmployeeIssueLeave = "SELECT * from emp_leave_type e,leave_type lt,user_type ut where e.user_type_id=ut.user_type_id and e.leave_type_id=lt.leave_type_id  and e.emp_leave_type_id=?";
	final static public String insertEmployeeIssueLeave = "INSERT INTO emp_leave_type (level_id, leave_type_id, no_of_leave, is_paid, is_carryforward, entrydate, effective_date, user_id, effective_date_type, monthly_limit, consecutive_limit, is_monthly_carryforward, is_approval, org_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static public String updateEmployeeIssueLeave = "UPDATE emp_leave_type SET user_type_id=?, leave_type_id=?, no_of_leave=?, entrydate=?, effective_date_type=?, is_paid=? WHERE emp_leave_type_id=?";
	final static public String deleteEmployeeIssueLeave = "DELETE FROM emp_leave_type WHERE emp_leave_type_id=?";
	final static public String selectEmployeeIssueLeaveV = "SELECT ut.user_type,sum(e.no_of_leave) as total_no_of_leave,e.user_type_id from emp_leave_type e,user_type ut,leave_type lt where e.user_type_id=ut.user_type_id and e.leave_type_id=lt.leave_type_id group by ut.user_type,e.user_type_id order by e.user_type_id";
	final static public String selectEmployeeIssueLeaveVP = "SELECT * from emp_leave_type e,leave_type lt,user_type ut where e.user_type_id=ut.user_type_id and e.leave_type_id=lt.leave_type_id  and ut.user_type_id=?";

	final static public String selectLeaveTypeE1 = "select * from emp_leave_type where level_id = (select dd.level_id from level_details ld, designation_details dd, grades_details gd where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?) limit 1 )";
	final static public String selectLeaveTypeF = "SELECT * FROM leave_type  where leave_type_id>0 order by leave_type_name";
	final static public String selectLeaveTypeF1 = "SELECT * FROM leave_type  where leave_type_id>0 and org_id =? order by leave_type_name";
	final static public String selectLeaveTypeF01 = "SELECT * FROM leave_type  where leave_type_id>0 and is_compensatory = true order by leave_type_name";
	final static public String selectLeaveTypeL = "SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id and level_id=?";
	final static public String selectLeaveTypeR = "SELECT * FROM leave_type where org_id=? order by leave_type_name";
	final static public String selectLeaveTypeV = "SELECT * FROM leave_type WHERE leave_type_id=?";
	final static public String selectLeaveTypeE = "select * from leave_type lt,emp_leave_type elt where elt.leave_type_id = lt.leave_type_id and user_type_id=?";
	final static public String insertLeaveType = "INSERT INTO leave_type (leave_type_name, leave_type_code, leave_type_colour,is_document_required, is_compensatory, is_leave_encashment, min_leave_encashment, org_id) VALUES (?,?,?,?,?,?,?,?)";
	final static public String deleteLeaveType = "DELETE FROM leave_type WHERE leave_type_id=?";
	final static public String updateLeaveType = "UPDATE leave_type SET leave_type_name=? WHERE leave_type_id=?";

	final static public String selectLeaveDates = "select * from emp_leave_entry ele, leave_type lt where ele.leave_type_id = lt.leave_type_id and (approval_from, approval_to_date) overlaps (to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD') +1) and is_approved = 1";
	final static public String selectLeaveDatesPaid = "select * from emp_leave_entry ele, leave_type lt where ele.leave_type_id = lt.leave_type_id and (approval_from, approval_to_date) overlaps (to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD') +1) and is_approved = 1 and ispaid= true and is_compensate = false";
	final static public String selectEmpLeaveDates = "select * from emp_leave_entry ele, leave_type lt where ele.leave_type_id = lt.leave_type_id and (approval_from, approval_to_date) overlaps (to_date(?::text, 'YYYY-MM-DD')-1,to_date(?::text, 'YYYY-MM-DD') +1) and emp_id = ? and ele.leave_type_id = ?";
	final static public String selectUpcomingLeaveManager = "select * from emp_leave_entry where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and ((approval_from, approval_to_date) overlaps (to_date(?::text, 'YYYY-MM-DD')-1,to_date(?::text, 'YYYY-MM-DD') +1) OR approval_from >= ?)  and is_approved=1";
	final static public String selectLeaveRequestManager = "select * from emp_leave_entry where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and is_approved =0 and entrydate is not null and encashment_status = false order by entrydate desc limit 10";

	final static public String selectUpcomingLeaveHRManager = "select * from emp_leave_entry elt, employee_official_details eod where elt.emp_id = eod.emp_id and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and ((approval_from, approval_to_date) overlaps (to_date(?::text, 'YYYY-MM-DD')-1,to_date(?::text, 'YYYY-MM-DD') +1) OR approval_from >= ?)  and is_approved=1";
	final static public String selectLeaveRequestHRManager = "select * from emp_leave_entry elt, employee_official_details eod where eod.emp_id = elt.emp_id and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and is_approved =0 and entrydate is not null and encashment_status = false order by entrydate desc ";

	final static public String selectLeaveType = "select * from (Select ee.emp_id,ee.leave_type_id,et.no_of_leave,et.effective_date_type, sum(emp_no_of_leave)as taken, is_approved  from emp_leave_entry ee,emp_leave_type et where ee.leave_type_id=et.leave_type_id and emp_id=? and et.level_id in ( select dd.level_id from level_details ld, designation_details dd, grades_details gd where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?) limit 1 ) group by ee.emp_id,et.no_of_leave,ee.leave_type_id, is_approved, et.effective_date_type) a, leave_type lt  where a.leave_type_id = lt.leave_type_id order by lt.leave_type_name, lt.leave_type_id";
	final static public String selectLeaveType1 = "select * from emp_leave_type elt, leave_type lt where lt.leave_type_id = elt.leave_type_id and level_id = (select dd.level_id from level_details ld, designation_details dd, grades_details gd where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ? and emp_id>0)) limit 1 ";
	final static public String selectEmpLeaveType = "SELECT * from emp_leave_type e, leave_type lt, level_details ld where e.level_id = ld.level_id and e.leave_type_id = lt.leave_type_id order by lt.leave_type_name, lt.leave_type_id, ld.level_code, ld.level_id ";

	// ================Leave Module End========================

	/**************************** New Leave Statements **********************************/

	final static public String selectLeaveEntries = "SELECT * FROM (SELECT * FROM (SELECT * FROM emp_leave_entry WHERE emp_id=55) alt LEFT JOIN leave_type lt ON alt.type_of_leave = lt.leave_type_id) aelt LEFT JOIN emp_leave_type elt ON aelt.type_of_leave = elt.leave_type_id WHERE user_type_id=?";

	/**************************** New Leave Statements **********************************/

	final static public String insertSalaryDetails = "INSERT INTO salary_details (salary_head_name, earning_deduction, salary_head_amount_type, sub_salary_head_id, salary_head_amount) VALUES (?,?,?,?,?)";
	final static public String updateSalaryDetails = "UPDATE salary_details set salary_head_name = ?, earning_deduction = ?, salary_head_amount_type = ?, sub_salary_head_id = ?, salary_head_amount = ? where salary_head_id = ? and level_id =? and salary_id=?";
	final static public String selectSalaryDetails = "SELECT * FROM salary_details where (level_id = ? OR level_id = 0) and (is_delete is null or is_delete=false) and (is_reimbursement_ctc is null or is_reimbursement_ctc=false) order by weight ";
//	final static public String selectSalaryDetails1 = "SELECT * FROM salary_details order by weight ";
//	final static public String selectSalaryDetails1 = "SELECT salary_head_id, salary_head_name FROM salary_details where (is_delete is null or is_delete=false) group by salary_head_id,salary_head_name order by salary_head_id";
	final static public String selectSalaryDetails1 = "SELECT salary_head_id, salary_head_name FROM salary_details group by salary_head_id,salary_head_name order by salary_head_id";
	final static public String selectSalaryDetails11 = "SELECT * FROM salary_details where level_id =? order by weight ";
//	final static public String selectSalaryDetails2 = "SELECT distinct(salary_head_id), salary_head_name, earning_deduction FROM salary_details where (is_delete is null or is_delete=false) and (is_reimbursement_ctc is null or is_reimbursement_ctc=false) order by earning_deduction desc, salary_head_name";
	final static public String selectSalaryDetails2 = "select * from (SELECT salary_head_id, salary_head_name,earning_deduction FROM salary_details where (is_delete is null or is_delete=false) and (is_reimbursement_ctc is null or is_reimbursement_ctc=false) group by salary_head_id,salary_head_name,earning_deduction) a order by earning_deduction desc, salary_head_name";

	final static public String selectReimbursementHead = "SELECT * FROM reimbursement_head_details";
	final static public String deleteSalaryDetails = "DELETE FROM salary_details WHERE salary_id = ?";

	final static public String selectLeavesTaken = "select leave_type_name, sum(emp_no_of_leave) as leaves_taken from emp_leave_entry ele LEFT JOIN leave_type lt on ele.type_of_leave = lt.leave_type_id where emp_id=? group by leave_type_name";
	final static public String selectTotalLeaves = "select leave_type_name, no_of_leave from emp_leave_type elt LEFT JOIN leave_type lt on elt.leave_type_id = lt.leave_type_id where user_type_id=?";

	final static String salaryDetailsLeftValues = "Select * from (Select * from (Select * from(select * from payroll, services where emp_id=? and generate_date between ? and ? and payroll.service_id=services.service_id) pr LEFT JOIN employee_personal_details epd on epd.emp_per_id=pr.emp_id) apd LEFT JOIN employee_official_details eod on apd.emp_per_id=eod.emp_id) adi LEFT JOIN department_info di on adi.depart_id = di.dept_id";
	final static String countWorkingDays = "select count(*) from payroll where generate_date between ? and ? and emp_id=?";
	final static String countApprovedLeaves = "select approval_from , approval_to_date from emp_leave_entry where emp_id=? and is_approved=1 and approval_from between ? and ? OR approval_to_date between ? and ?";
	final static String absentButPayableDays = "select distinct  * from(select distinct * from (select *from payroll  where emp_id=? and generate_date between ? and ? and _in is null) pr LEFT JOIN services sr on sr.service_id=pr.service_id)ss where service_name=?";
	final static String countWeekWorkingDaysInTheMonth = "select distinct count(*) as weekDaysWorked from (select distinct * from (select * from payroll where emp_id=? and  generate_date between ? and ? and _in is not null) pr  LEFT JOIN services sr on sr.service_id=pr.service_id)ss where service_name=?";
	final static String getRates = "select *  from(select distinct * from (select *from payroll where emp_id=? and generate_date between ? and ? and _in is not null) pr LEFT JOIN services sr on sr.service_id=pr.service_id)ss where service_name=?";
	final static String getAddress = "select * from(select * from(select * from (select * from employee_official_details where emp_id= ? ) eod LEFT JOIN work_location_info wli ON eod.wlocation_id=wli.wlocation_id) cc LEFT JOIN state s ON cc.wlocation_state_id=s.state_id) ss LEFT JOIN country co ON ss.wlocation_country_id=co.country_id";
	final static String getEmail = "select * from employee_personal_details where emp_per_id =?";

	final static String selectNavigationInner = "select * from navigation_1 where parent = ? and _exist = 1 order by weight";

	final static String selectNavigationInnerTitle = "select * from navigation_1 where navigation_id = ?";

	final static String selectNavigation = "select * from navigation where position=? and user_type_id =? order by parent,weight";
	final static String selectNavigationACL = "select * from navigation_acl where user_id=?";
	final static String insertNavigationACL = "insert into navigation_acl (user_id, navigation) values (?,?)";
	final static String updateNavigationACL = "update navigation_acl set navigation=? where user_id=?";

	final static String salaryDetails = "SELECT employee_personal_details.emp_fname, employee_personal_details.emp_lname, employee_personal_details.joining_date, designation_info.desig_name,employee_personal_details.emp_email,(SELECT COUNT(*) FROM roster_details WHERE emp_id=? and _date between ? and ?) as no_of_working_days, (select COUNT(*)  from payroll where emp_id=? and generate_date between ? and ?) as days_present FROM employee_personal_details  INNER JOIN employee_official_details  ON employee_personal_details.emp_per_id = employee_official_details.emp_id  JOIN designation_info ON designation_info.desig_id=employee_official_details.designation_id  WHERE employee_personal_details.emp_per_id=?";

	/******************************************** MAIL CENTRE START ******************************************/

	final static String selectMaxMailNo = "select MAX(mail_no) from mail";
	final static String insertMail = "insert into mail (mail_no,mail_from,mail_to, mail_subject, mail_upload, mail_body,mail_drafts,mail_trash, mail_type, read_unread, emp_id)values (?,?,?,?,?,?,?,?,?,?,?)";

	final static String insertDraftMail = "insert into mail (mail_no,mail_from,mail_to, mail_subject, mail_upload, mail_body,mail_drafts,mail_trash)values(?,?,?,?,?,?,?,?)";
	final static String getAllTrashQuery = "Select * from mail where mail_to=? and mail_trash=? and mail_drafts=? ORDER BY mail_id DESC";

	final static String getAllSentQuery = "Select * from mail where mail_from=? and mail_drafts=?  and mail_trash=? ORDER BY mail_id DESC";
	final static String moveToTrash = "update mail set mail_trash=? where mail_id=?";
	final static String getAllDraftsQuery = "Select * from mail where mail_from=? and mail_drafts=? ORDER BY mail_id DESC ";
	final static String getAllMailsquery = "Select * from mail where mail_to=?  and mail_trash=? and mail_drafts=? ORDER BY mail_id DESC";

	final static String getMailNo = "select mail_no from mail where mail_id=?";
	final static String markReadQuery = "update mail set read_unread=? where mail_no=? and mail_to=? and mail_drafts='0' and mail_trash='0'";

	final static String getMailId = "select mail_no from mail where mail_id=?";
	final static String getToAndCCQuery = "select mail_to,mail_type from mail where mail_no=?";
	final static String checkUpdateInboxMails = "Select * from mail where mail_to=? and mail_trash=? and mail_drafts=? ORDER BY mail_id DESC";
	final static String getStaticEmployeeData = "Select * from (select * from(select * from employee_personal_details where emp_per_id=?) epr LEFT JOIN  employee_official_details eod ON epr.emp_per_id=eod.emp_id) adi LEFT JOIN department_info on adi.depart_id = dept_id";
	final static String getReadUnreadCount = "select count(distinct(mail_no)) from mail where mail_to=?and read_unread=FALSE";
	final static String getUnreadMailCount = "select count(distinct(mail_no)) as count from mail where emp_id=? and read_unread=FALSE";
	final static String getAllContactsQuery = "Select * from employee_personal_details";
	final static String deleteMailForever = "delete from mail where mail_id=?";
	final static String selectThought = "select * from daythoughts where day_id=? and added_by is null and year is null";

	/******************************************** MAIL CENTRE END ******************************************/

	final static String getApprovedEmp = "select * from payroll where payroll_id=?";

	final static public String selectTimezone = "SELECT * FROM timezones order by timezone_region, timezone_country1, timezone_country2";
	final static public String selectTimezoneEmp = "SELECT timezone_region, timezone_country1, timezone_country2 FROM employee_official_details eod, work_location_info wl, timezones tz where tz.timezone_id = wl.timezone_id and wl.wlocation_id = eod.wlocation_id and emp_id = ?";
	final static public String insertProbationPolicy = "INSERT INTO probation_policy(emp_id, leaves_types_allowed, probation_duration) VALUES(?,?,?)";

	final static public String selectProbationPolicy = "SELECT * FROM probation_policy WHERE emp_id = ?";
	final static public String updateProbationPolicy = "UPDATE probation_policy SET leaves_types_allowed=?, probation_duration=? WHERE emp_id = ?";
	final static public String selectColourCode = "SELECT * FROM colour_codes";

	final static public String updateEmployeeImage = "update employee_personal_details set emp_image =? where emp_per_id = ?";
	final static public String updateCompanyLogo = "update settings set value=? where options=?";
	final static public String updateOrgLogo = "update org_details set org_logo=? where org_id=?";
	final static public String updateCandidateImage = "update candidate_personal_details set emp_image =? where emp_per_id = ?";

	// Queries for charts =======>>

	final static public String selectDatesPrev1 = "Select * FROM (SELECT * FROM alldates where _date<= ? order by _date) a order by _date desc LIMIT 14";
	final static public String selectEmpInWlocation = "SELECT emp_id FROM employee_official_details WHERE wlocation_id = (SELECT wlocation_id FROM employee_official_details WHERE emp_id = ?)";

	final static public String selectLeaveRequests = "SELECT emp_id, entrydate FROM emp_leave_entry WHERE entrydate = ?";
	final static public String selectLeaveRequestsPerWlocation = "SELECT emp_id, entrydate FROM emp_leave_entry WHERE entrydate = ? AND emp_id in ("
			+ selectEmpInWlocation + ")";

	final static public String selectLateIn = "SELECT * FROM attendance_details where in_out = 'IN' AND early_late > 0 AND to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ? order by in_out_timestamp desc LIMIT 7";
	final static public String selectLateInPerWlocation = "SELECT * FROM attendance_details where in_out = 'IN' AND early_late > 0 AND to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ? AND emp_id in ("
			+ selectEmpInWlocation + ") order by in_out_timestamp desc LIMIT 7";

	final static public String selectEarlyOut = "SELECT * FROM attendance_details where in_out = 'OUT' AND early_late < 0 AND to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ? order by in_out_timestamp desc LIMIT 7";
	final static public String selectEarlyOutPerWlocation = "SELECT * FROM attendance_details where in_out = 'OUT' AND early_late < 0 AND to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ? AND emp_id in ("
			+ selectEmpInWlocation + ") order by in_out_timestamp desc LIMIT 7";

	final static public String selectAbsent = "SELECT * FROM (SELECT * from roster_details where _date = ? ) aad LEFT JOIN attendance_details ad ON aad.emp_id = ad.emp_id order by _date";
	final static public String selectAbsentPerWlocation = "SELECT * FROM (SELECT * from roster_details where _date = ? AND emp_id in (" + selectEmpInWlocation
			+ ")) aad LEFT JOIN attendance_details ad ON aad.emp_id = ad.emp_id order by _date";

	final static public String selectLeaveRequestsBetween = "SELECT count(*) FROM emp_leave_entry WHERE entrydate BETWEEN ? AND ?  ";
	final static public String selectLeaveRequestsBetweenPerWlocation = "SELECT count(*) FROM emp_leave_entry WHERE emp_id in (" + selectEmpInWlocation
			+ ") AND entrydate BETWEEN ? AND ?  ";

	final static public String selectLeaveRequestsOn = "SELECT count(*) FROM emp_leave_entry WHERE entrydate = ?";
	final static public String selectLeaveRequestsOnPerWlocation = "SELECT count(*) FROM emp_leave_entry WHERE emp_id in (" + selectEmpInWlocation
			+ ") AND entrydate = ?";

	final static public String selectLeaveStatusBetween = "SELECT count(*) FROM emp_leave_entry WHERE is_approved = ? AND entrydate BETWEEN ? AND ?  ";
	final static public String selectLeaveStatusBetweenPerWlolcation = "SELECT count(*) FROM emp_leave_entry WHERE is_approved = ? AND emp_id in ("
			+ selectEmpInWlocation + ") AND entrydate BETWEEN ? AND ?  ";

	final static public String selectLeaveStatusOn = "SELECT count(*) FROM emp_leave_entry WHERE is_approved = ? AND entrydate = ?";
	final static public String selectLeaveStatusOnPerWlocation = "SELECT count(*) FROM emp_leave_entry WHERE is_approved = ? AND emp_id in ("
			+ selectEmpInWlocation + ") AND entrydate = ?";

	final static public String selectCostCenter = "SELECT service_id from employee_official_details WHERE emp_id = ?";
	final static public String selectEmpFromCostCenter = "SELECT count(*) FROM employee_official_details WHERE service_id like ?";

	final static public String selectEmpLevelDetails = "select * from level_details ld right join (select * from designation_details dd right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd where gd.grade_id=eod.grade_id) a on a.designationid=dd.designation_id) a on a.level_id=ld.level_id";

	/*------------------------------------------*/

	final static public String selectCategory = "SELECT * FROM category_details order by category_code";
	final static public String insertCategory = "INSERT INTO category_details (category_code, category_description) VALUES (?,?)";
	final static public String deleteCategory = "DELETE FROM category_details WHERE category_id=?";

	final static public String selectLevel = "SELECT * FROM level_details order by level_id";
	final static public String selectLevel1 = "SELECT * FROM level_details where org_id =? order by level_name";

	final public static String insertLevel = "INSERT INTO level_details (level_code, level_name, level_description,standard_working_hours,standard_overtime_hours, flat_deduction, org_id, level_parent) VALUES (?,?,?,?,?,?,?,?)";
	final static public String deleteLevel = "DELETE FROM level_details WHERE level_id=?";

	final static public String selectDesig = "SELECT * FROM designation_details ald INNER JOIN level_details ld ON ald.level_id = ld.level_id order by designation_name";
	final static public String insertDesig = "INSERT INTO designation_details (designation_code, designation_name, designation_description, level_id) VALUES (?,?,?,?)";
	final static public String deleteDesig = "DELETE FROM designation_details WHERE designation_id=?";
	final static public String deleteDesig1 = "DELETE FROM designation_details WHERE level_id=?";
	final static public String selectDesigFromLevel = "SELECT * FROM designation_details WHERE level_id = ? order by designation_name";

	final static public String selectGrade = "SELECT * FROM (SELECT * FROM (SELECT * FROM grades_details) add LEFT JOIN designation_details dd ON add.designation_id = dd.designation_id) ald LEFT JOIN level_details ld ON ald.level_id = ld.level_id order by grade_code";
	final static public String insertGrade = "INSERT INTO grades_details (grade_code, grade_name, grade_description, designation_id) VALUES (?,?,?,?)";
	final static public String deleteGrade = "DELETE FROM grades_details WHERE grade_id=?";
	final static public String deleteGrade1 = "DELETE FROM grades_details WHERE designation_id=?";
	final static public String selectGrade1 = "select * from grades_details gd, designation_details dd, level_details ld where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id";
	final static public String selectGradeFromDesignation = "SELECT * FROM grades_details where designation_id = ? order by grade_code";

	final static public String updateGradeDesigLevel = "UPDATE employee_activity_details SET grade_id = ?, " + "wlocation_id=?, department_id=?,"
			+ "desig_id = (SELECT designation_id from grades_details WHERE grade_id = ?), "
			+ "level_id = (SELECT level_id from designation_details WHERE designation_id = "
			+ "(SELECT designation_id from grades_details WHERE grade_id = ?)) WHERE emp_id = ? and "
			+ "entry_date = (select max(entry_date) from employee_activity_details WHERE emp_id = ?)";

	final static public String selectShift = "SELECT * FROM shift_details where org_id=? order by shift_id";
	final static public String insertShift = "INSERT INTO shift_details (_from,_to,shift_code, break_start, break_end, colour_code, shift_type,org_id,shift_name) VALUES (?,?,?,?, ?,?,?,?, ?)";
	final static public String deleteShiftDetail = "DELETE FROM shift_details WHERE shift_id=?";

	final static public String selectOrg = "SELECT * FROM org_details order by org_name";
	final static public String selectOrgV = "SELECT * FROM org_details where org_id= ?";

	final static public String selectWlocationType = "SELECT * FROM work_location_type order by wlocation_type_code";
	final static public String insertWlocationType = "INSERT INTO work_location_type (wlocation_type_code, wlocation_type_name, wlocation_type_description, org_id) VALUES (?,?,?,?)";
	final static public String deleteWlocationType = "DELETE FROM work_location_type WHERE wlocation_type_id=?";

	final static public String selectPerk = "SELECT * FROM (SELECT * FROM perk_details) ald LEFT JOIN level_details ld ON ald.level_id = ld.level_id order by ld.level_id, perk_code";
	final static public String insertPerk = "INSERT INTO perk_details (perk_code, perk_name, perk_description, perk_type, perk_payment_cycle, level_id, max_amount, entry_date, user_id, org_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
	final static public String deletePerk = "DELETE FROM perk_details WHERE perk_id=?";

	final static public String selectIncrement = "SELECT * FROM increment_details where org_id =? order by increment_from";
	final static public String selectIncrementV = "SELECT * FROM increment_details where increment_id=?";
	final static public String insertIncrement = "INSERT INTO increment_details (increment_from, increment_to, increment_amount, due_month, entry_date, user_id, org_id) VALUES (?,?,?,?,?,?,?)";
	final static public String deleteIncrement = "DELETE FROM increment_details WHERE increment_id=?";

	final static public String selectIncrementDA = "SELECT * FROM increment_details_da order by increment_from";
	final static public String selectIncrementDAV = "SELECT * FROM increment_details_da where increment_id=?";
	final static public String insertIncrementDA = "INSERT INTO increment_details_da (increment_from, increment_to, increment_amount, increment_amount_type, due_month, entry_date, user_id) VALUES (?,?,?,?,?,?,?)";
	final static public String deleteIncrementDA = "DELETE FROM increment_details_da WHERE increment_id=?";

	final static public String selectExemption = "SELECT * FROM exemption_details where exemption_from=? and exemption_to=? order by exemption_code";
	final static public String insertExemption = "INSERT INTO exemption_details (exemption_code, exemption_name, exemption_description, exemption_from, exemption_to, exemption_limit, entry_date, user_id) VALUES (?,?,?,?,?,?,?,?)";
	final static public String deleteExemption = "DELETE FROM exemption_details WHERE exemption_id=?";

	final static public String selectskills = "SELECT * FROM skills_description WHERE emp_id=? ORDER BY skills_id";
	final static public String insertSkill = "INSERT INTO skills_description (skill_id, skills_value, emp_id) VALUES (?,?,?)";
	final static public String deleteSkills = "DELETE from skills_description where emp_id =?";

	final static public String selectHobbies = "SELECT * FROM hobbies_details WHERE emp_id=? ORDER BY hobbies_name";
	final static public String insertHobbies = "INSERT INTO hobbies_details (hobbies_name, emp_id) VALUES (?,?)";
	final static public String deleteHobbies = "DELETE from hobbies_details where emp_id =?";

	final static public String selectDocuments = "SELECT * FROM documents_details where emp_id = ?";
	final static public String insertDocuments = "INSERT INTO documents_details (documents_name, documents_type, emp_id, documents_file_name,added_by,entry_date) values (?,?,?,?,?,?)";
	final static public String deleteDocuments = "DELETE FROM documents_details WHERE emp_id = ?";
	final static public String updateDocuments = "UPDATE documents_details SET documents_name=?, documents_file_name=? where documents_id = ?";

	final static public String selectSectionV = "SELECT * FROM section_details where section_id=?";
	final static public String selectSection = "SELECT * FROM section_details order by section_code";
	final static public String insertSection = "INSERT INTO section_details (section_code, section_description, section_exemption_limit, section_limit_type, entry_date, user_id) values (?,?,?,?,?,?)";
	final static public String deleteSection = "DELETE FROM section_details WHERE section_id = ?";

	final static public String selectLTA = "SELECT * FROM lta_details order by lta_from desc";
	final static public String insertLTA = "INSERT INTO lta_details (lta_from, lta_to, lta_limit) values (?,?,?)";
	final static public String deleteLTA = "DELETE FROM lta_details WHERE lta_id = ?";

	final static public String selectInvestment = "SELECT *, asd.entry_date as entrydate  FROM (SELECT * FROM investment_details WHERE emp_id =? AND status = ?) asd LEFT JOIN section_details sd ON asd.section_id = sd.section_id order by  fy_from desc, emp_id";
	final static public String selectInvestment2 = "SELECT *, asd.entry_date as entrydate, sd.section_id as sectionid  FROM (SELECT * FROM investment_details WHERE emp_id =?) asd RIGHT JOIN section_details sd ON asd.section_id = sd.section_id order by  fy_from desc, emp_id";
	final static public String selectInvestmentEmp1 = "SELECT * FROM (SELECT * FROM investment_details WHERE emp_id =? AND status = ? and fy_from = ? and fy_to=?) asd right JOIN section_details sd ON asd.section_id = sd.section_id ";
	final static public String selectInvestmentEmp = "SELECT sum(amount_paid) as amount_paid, fy_from, fy_to, agreed_date FROM investment_details WHERE emp_id =? AND status = ? group by fy_from, fy_to, agreed_date";
	final static public String selectInvestment1 = "SELECT * FROM (SELECT * FROM investment_details WHERE status = ?) asd LEFT JOIN section_details sd ON asd.section_id = sd.section_id order by  fy_from desc, emp_id";
	final static public String insertInvestment = "INSERT INTO investment_details (section_id, amount_paid, status, emp_id, entry_date) values (?,?,?,?,?)";
	final static public String deleteInvestment = "DELETE FROM investment_details WHERE investment_id = ?";
	final static public String updateInvestment = "UPDATE investment_details SET status = ?, fy_from=?, fy_to=?, agreed_date=? WHERE investment_id = ?";

	final static public String selectOverTime = "SELECT * FROM overtime_details order by overtime_code";
	final static public String insertOverTime = "INSERT INTO overtime_details (overtime_code, overtime_description, level_id, overtime_type, overtime_payment_type, salaryhead_id, date_from, date_to, overtime_payment_amount) values (?,?,?,?,?,?,?,?,?)";
	final static public String deleteOverTime = "DELETE FROM overtime_details WHERE overtime_id = ?";

	final static public String selectUserActivity = "select login_timestamp_id,emp_fname,emp_lname, username, login_timestamp, client_ip from login_timestamp lt, user_details ud, employee_personal_details epd where lt.emp_id = ud.emp_id and ud.emp_id = epd.emp_per_id order by login_timestamp desc";

	final static public String selectBank = "SELECT * FROM bank_details order by bank_code";
	final static public String selectBankV = "SELECT * FROM bank_details where bank_id=?";
	final static public String insertBank = "INSERT INTO bank_details (bank_code, bank_name, bank_description, bank_address,bank_city, bank_state_id,bank_country_id,bank_branch,bank_email,bank_fax,bank_contact,bank_ifsc_code,bank_account_no, bank_pincode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static public String deleteBank = "DELETE FROM bank_details WHERE bank_id = ?";

	final static public String selectBonus = "SELECT * FROM(SELECT * FROM bonus_details where date_from =? and date_to=?) ald LEFT JOIN  level_details ld ON ald.level_id = ld.level_id order by date_from desc";
	final static public String selectBonus1 = "SELECT * FROM bonus_details where date_from =? and date_to=? and level_id=? order by bonus_id desc";
	final static public String selectBonus2 = "SELECT * FROM bonus_details where date_from =? and date_to=? ";
	final static public String insertBonus = "INSERT INTO bonus_details (level_id, date_from, date_to, bonus_minimum, bonus_maximum, bonus_type, salary_head_id, bonus_minimum_days, bonus_amount, bonus_period, entry_date, user_id, org_id, salary_calculation, salary_effective_year) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	final static public String deleteBonus = "DELETE FROM bonus_details WHERE bonus_id = ?";

	final static public String updateEmpSalaryDetails = "UPDATE emp_salary_details SET amount = ? , entry_date = ? , isdisplay = ? WHERE emp_salary_id = ?";
	final static public String selectUpdateEmpSalaryDetails = "SELECT weight,isdisplay,pay_type,user_id,entry_date,amount,emp_salary_id,salary_head_amount,sd.earning_deduction,salary_head_amount_type,sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation,salary_calculate_amount FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? AND service_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved=true) AND effective_date <= ?) asd RIGHT JOIN salary_details sd ON sd.salary_head_id = asd.salary_head_id WHERE sd.level_id = ? and (sd.is_delete is null or sd.is_delete=false)  order by sd.earning_deduction desc, weight";
	final static public String selectEmpSalaryDetailsEarning1 = "SELECT * FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) AND effective_date <= ?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id WHERE sd.earning_deduction = 'E' and level_id = ? and asd.salary_head_id not in ("+ IConstants.GROSS + ") order by weight";

	final static public String selectEmpSalaryDetails = "SELECT * FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? AND service_id = ?) asd LEFT JOIN salary_details sd ON sd.salary_head_id = asd.salary_head_id order by salary_head_name";
	final static public String deleteEmpSalaryDetails = "DELETE FROM emp_salary_details WHERE emp_salary_id = ?";
	final static public String insertEmpSalaryDetails = "INSERT INTO emp_salary_details (emp_id , salary_head_id, amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, earning_deduction, salary_type) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

	final static public String selectWorkForceR3_E = "select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and joining_date<=? and is_alive=true order by wlocation_id";
	final static public String selectWorkForceR4_E = "select * from employee_personal_details epd, employee_official_details eod, user_details ud where epd.emp_per_id = eod.emp_id and ud.emp_id=eod.emp_id and ud.emp_id = epd.emp_per_id and joining_date<=? and is_alive=true order by usertype_id";
	final static public String selectWorkForceR5_E = "select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and joining_date<=? and is_alive=true order by depart_id";

	final static public String selectWorkForceJoinR3_E = "select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and joining_date between ? and ? and is_alive=true order by wlocation_id";
	final static public String selectWorkForceJoinR4_E = "select * from employee_personal_details epd, employee_official_details eod, user_details ud where epd.emp_per_id = eod.emp_id and ud.emp_id=eod.emp_id and ud.emp_id = epd.emp_per_id and joining_date between ? and ? and is_alive=true order by usertype_id";
	final static public String selectWorkForceJoinR5_E = "select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and joining_date between ? and ? and is_alive=true order by depart_id";

	final static public String selectWorkForceTerminateR3_E = "select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and employment_end_date between ? and ? and is_alive=false order by wlocation_id";
	final static public String selectWorkForceTerminateR4_E = "select * from employee_personal_details epd, employee_official_details eod, user_details ud where epd.emp_per_id = eod.emp_id and ud.emp_id=eod.emp_id and ud.emp_id = epd.emp_per_id and employment_end_date between ? and ? and is_alive=false order by usertype_id";
	final static public String selectWorkForceTerminateR5_E = "select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and  employment_end_date between ? and ? and is_alive=false order by depart_id";

	final static public String selectClockEntriesR4_E_Actual = "SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_official_details eod WHERE eod.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT' order by empl_id, in_out_timestamp desc";
	final static public String selectClockEntriesR4_E_Roster = "SELECT *, rd.emp_id as empl_id FROM roster_details rd, employee_official_details eod  where rd.emp_id=eod.emp_id and _date between ? AND ? order by empl_id, _date desc";

	final static public String selectClockEntriesR5_E_Actual = "SELECT *, ad.emp_id as empl_id FROM attendance_details ad, user_details ud WHERE ud.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT' order by empl_id, in_out_timestamp desc";
	final static public String selectClockEntriesR5_E_Roster = "SELECT *, rd.emp_id as empl_id FROM roster_details rd, user_details ud  where rd.emp_id=ud.emp_id and _date between ? AND ? order by empl_id, _date desc";

	final static public String selectClockEntriesR4_EManager_Actual = "SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_official_details eod WHERE eod.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT' and supervisor_emp_id =? order by empl_id, in_out_timestamp desc";
	final static public String selectClockEntriesR4_EManager_Roster = "SELECT *, rd.emp_id as empl_id FROM roster_details rd, employee_official_details eod  where rd.emp_id=eod.emp_id and _date between ? AND ?  and supervisor_emp_id =? order by empl_id, _date desc";

	final static public String selectClockEntriesR5_EManager_Actual = "SELECT *, ad.emp_id as empl_id FROM attendance_details ad, user_details ud WHERE ud.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT' and ad.emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) order by empl_id, in_out_timestamp desc";
	final static public String selectClockEntriesR5_EManager_Roster = "SELECT *, rd.emp_id as empl_id FROM roster_details rd, user_details ud  where rd.emp_id=ud.emp_id and _date between ? AND ? and ad.emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) order by empl_id, _date desc";

	final static public String insertPayrollGeneration = "insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	final static public String insertEmpActivity = "insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static public String selectEmpActivityDetails = "select * from status_details s right join( select * from work_location_info wl right join( select * from department_info d right join( select * from grades_details gd right join( select * from designation_details dd right join( select * from level_details ld right join( SELECT *, wlocation_id as wloc_id FROM employee_activity_details ead, activity_details ad WHERE ad.activity_id=ead.activity_id and emp_id = ? and emp_activity_id= (select max(emp_activity_id) from employee_activity_details where emp_id=?) order by emp_activity_id desc limit 1 ) a on a.level_id=ld.level_id ) a on a.desig_id=dd.designation_id ) a on a.grade_id=gd.grade_id ) a on a.department_id=d.dept_id ) a on a.wloc_id=wl.wlocation_id ) a on a.emp_status_code=s.status_code order by effective_date desc, entry_date desc";
	final static public String selectEmpActivityDetails1 = "SELECT *, wlocation_id as wloc_id FROM employee_activity_details ead WHERE emp_id = ? and effective_date= (select max(effective_date) from employee_activity_details where emp_id= ? )  order by emp_activity_id desc limit 1";
	final static public String selectEmpIncrementDetails = "select * from (select * from status_details s right join( select * from work_location_info wl right join( select * from department_info d right join( select * from grades_details gd right join( select * from designation_details dd right join( select * from level_details ld right join( SELECT *, wlocation_id as wloc_id FROM employee_activity_details ead WHERE emp_id = ? and effective_date= (select max(effective_date) from employee_activity_details where emp_id=?) ) a on a.level_id=ld.level_id ) a on a.desig_id=dd.designation_id ) a on a.grade_id=gd.grade_id ) a on a.department_id=d.dept_id ) a on a.wloc_id=wl.wlocation_id ) a on a.emp_status_id=s.status_id ) b where b.activity_id = ? order by effective_date desc, entry_date desc";
	final static public String insertEmpActivityMail = "insert into mail (mail_from, mail_to, emp_id, mail_subject, mail_upload, mail_body, mail_type, mail_trash, mail_drafts) values (?,?,?,?,?,?,?,?,?)";

	final static public String selectEmpStatus = "SELECT * FROM status_details order by status_code";

	final static public String selectSalaryHeadsByBasicSalary = "SELECT * FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? and service_id = ?) asd LEFT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id";
	final static public String selectEmployeeSalary = "select * from (select max(entry_date) as entry_date, emp_id from emp_salary_details where entry_date<=? group by emp_id) a, (select *, sd.salary_head_id as salary_id from salary_details sd left join emp_salary_details esd on esd.salary_head_id=sd.salary_head_id ) b where a.entry_date=b.entry_date and a.emp_id = b.emp_id and b.emp_id=? b.earning_deduction desc";
	final static public String selectEmployeeSalaryCalculation = "select * from emp_salary_details esd, salary_details sd,(select max(entry_date) as entry_date, emp_id from emp_salary_details group by emp_id) a where sd.salary_head_id = esd.salary_head_id and a.emp_id = esd.emp_id and a.entry_date = esd.entry_date and esd.emp_id = ? order by sd.salary_head_id";
	final static public String selectEmployeeCurrentSalary = "select * from emp_salary_details esd, (select max(entry_date) as entry_date, emp_id from emp_salary_details group by emp_id) a where esd.entry_date = a.entry_date and esd.emp_id = a.emp_id order by a.emp_id";
	final static public String selectIncrementAmount = "select * from increment_details where increment_from <= ? and ?<=increment_to";

	final static public String selectEmployeeSalaryHeadCalculation = "select * from (select max(effective_date) as effective_date, emp_id as empl_id from emp_salary_details where effective_date<=? group by emp_id) a, (select *, sd.salary_head_id as salary_id, sd.earning_deduction as ed from salary_details sd left join emp_salary_details esd on esd.salary_head_id=sd.salary_head_id ) b where a.effective_date=b.effective_date and a.empl_id = b.emp_id and emp_id in (select emp_per_id from employee_personal_details where is_alive=true) order by b.emp_id, b.ed desc, weight ";

	final public static String selectEmpPerLevelPerPC = "SELECT * FROM (SELECT * FROM employee_official_details WHERE grade_id in "
			+ "(SELECT grade_id FROM (SELECT * FROM (SELECT * FROM level_details WHERE level_id = ? ) add "
			+ "LEFT JOIN designation_details dd ON add.level_id = dd.level_id ) agd "
			+ "INNER JOIN grades_details gd ON agd.designation_id = gd.designation_id) ) aepd "
			+ "LEFT JOIN employee_personal_details epd ON epd.emp_per_id = aepd.emp_id "
			+ "WHERE epd.is_alive= true and joining_date <= ?  order by emp_fname,emp_mname, emp_lname";

	final public static String selectEmpPerPC = "SELECT * FROM (SELECT * FROM employee_official_details ) aepd "
			+ "LEFT JOIN employee_personal_details epd ON epd.emp_per_id = aepd.emp_id "
			+ "WHERE epd.is_alive= true and joining_date <= ?  order by emp_fname,emp_mname, emp_lname";

	final public static String insertAllDates = "insert into alldates (_date) values (?)";
	final public static String selectAllDates = "select max(_date) as _date from alldates";

	final public static String insertResignation = "insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date) values (?,?,?,?,?,?)";

	// ----------------------------------------------- NEW
	// -----------------------------------------------------------------

	final public static String selectBreakTypes = "SELECT * from emp_leave_break_type e, level_details ld where e.level_id = ld.level_id and wlocation_id =? order by break_type_id";
	final public static String selectBreakTypesLevel = "SELECT * from emp_leave_break_type e, level_details ld where e.level_id = ld.level_id order by ld.level_id";
	final static public String selectBreakTypeF = "SELECT * FROM leave_break_type order by break_type_name";
	final static public String selectBreakTypeR = "SELECT * FROM leave_break_type where org_id=? and break_type_id > 0 order by break_type_name";

	final static public String insertBreakType = "INSERT INTO leave_break_type (break_type_name, break_type_code, break_type_colour,org_id) VALUES (?,?,?,?)";
	final static public String updateBreakType = "UPDATE leave_break_type SET break_type_name=?, break_type_code=?, break_type_colour=? where break_type_id=?";
	final static public String selectBreakTypeE1 = "select * from emp_leave_break_type where level_id = (select dd.level_id from level_details ld, designation_details dd, grades_details gd where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?) limit 1 )";
	final static public String selectBreakTypeF1 = "SELECT * FROM leave_break_type  where break_type_id>0 and org_id =? order by break_type_name";
	final static public String insertLeaveBreakType = "INSERT INTO emp_leave_break_type (level_id, break_type_id, no_of_leave, is_paid, is_carryforward, entrydate, effective_date, user_id, monthly_limit, is_monthly_carryforward, is_approval, org_id,policy_id, no_of_break_monthly, accrual_system, accrual_from, wlocation_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static public String deleteLeaveBreakType = "DELETE FROM leave_break_type WHERE break_type_id=?";

	final static public String updateBreakPolicy = "UPDATE break_policy SET time_value=?,_mode=?,days=?, months=?,effective_date=?, entry_date=?, user_id=?, break_type_id=? WHERE break_policy_id=?";
	final static public String selectBreakPolicy = "select * from break_policy where break_policy_id=?";
	final static public String insertBreakPolicy = "insert into break_policy (time_value,_mode,days, months,effective_date, user_id, entry_date, org_id, wlocation_id, break_type_id) values (?,?,?,?,?,?,?,?,?,?)";
	final static public String deleteBreakPolicy = "delete from break_policy where break_policy_id=?";

	final static public String selectIndustry = "select * from client_industry_details order by industry_name";
	final static public String selectClientPOC = "select * from client_poc order by contact_fname,contact_lname";
//	final static public String selectClients = "select * from client_details order by client_name";
	final static public String selectClients = "select * from client_details where isdisabled =false order by client_name";
	final static public String selectAllClients = "select * from client_details order by client_name";
//	final static public String selectClients1 = "select * from projectmntnc pcmc, client_details cd where cd.client_id = pcmc.client_id and pro_id in (select pro_id from activity_info where emp_id = ?)";
//	final static public String selectClients1 = "select * from client_details cd where client_id in (select distinct client_id from projectmntnc where pro_id in (select distinct pro_id from activity_info where emp_id = ?))";

	final static public String updateReimbursementPayroll = "update emp_reimbursement set ispaid =?, paid_by=?, paid_date=? where emp_id =? and ispaid = false and approval_2=1";
	final static public String updateReimbursementPayroll1 = "update emp_reimbursement set ispaid =?, paid_by=?, paid_date=? where emp_id =? and ispaid = false and approval_2=1 and reimbursement_info in ('Conveyance Bill', 'Travel')";
	final static public String updateReimbursementPayroll2 = "update emp_reimbursement set ispaid =?, paid_by=?, paid_date=? where emp_id =? and ispaid = false and approval_2=1 and reimbursement_info in ('Mobile Bill')";
	final static public String updateReimbursementPayroll3 = "update emp_reimbursement set ispaid =?, paid_by=?, paid_date=? where emp_id =? and ispaid = false and approval_2=1 and reimbursement_info not in ('Mobile Bill', 'Conveyance Bill', 'Travel')";

	final static public String selectLoanPyroll = "select * from loan_details ld, loan_applied_details lad where lad.loan_id = ld.loan_id and emp_id =? and approved_date<=? and is_completed = false";
	final static public String selectLoanPyroll2 = "select * from loan_details ld, loan_applied_details lad where lad.loan_id = ld.loan_id and emp_id =? and is_completed = false";
	final static public String updateLoanPyroll = "update loan_applied_details set balance_amount =?, is_completed=? where emp_id =? and approved_date<=? and is_completed = false";
	final static public String insertLoanPyroll = "insert into loan_payments (emp_id, loan_id, amount_paid, paid_date, pay_source, loan_applied_id, paycycle_start, paycycle_end,sal_effective_date) values (?,?,?,?, ?,?,?,?, ?)";
	final static public String selectLeaveRegisterPyroll = "select * from leave_register where from_date<= ? and to_date>= ? order  by emp_id";

	final static public String selectEmployeeSalaryHeadCalculation1 = "select * from emp_salary_details esd, ( select max(effective_date) as effective_date, emp_id from emp_salary_details where effective_date<=? and is_approved= true group by emp_id ) a where a.effective_date = esd.effective_date and esd.emp_id = a.emp_id order by a.emp_id, esd.earning_deduction desc ";
	final static public String selectEmployeeSalaryHeadCalculation2 = "select * from emp_salary_details esd, ( select max(effective_date) as effective_date, emp_id from emp_salary_details where effective_date<=? and is_approved= true group by emp_id ) a where a.effective_date = esd.effective_date and esd.emp_id = a.emp_id and a.emp_id in (select emp_id from employee_official_details where org_id = ?) order by a.emp_id, esd.earning_deduction desc ";
	final static public String selectLoanPayroll1 = "select * from loan_applied_details lad, loan_details ld where lad.loan_id = ld.loan_id and emp_id = ? and is_approved=1 and approved_date<=? and is_completed = false";
	final static public String selectLoanPayroll2 = "select * from loan_applied_details lad, loan_details ld where lad.loan_id = ld.loan_id and emp_id = ? and is_approved=1 and is_completed = false";

	final static public String selectESI = "select * from esi_details where financial_year_start= ? and financial_year_end = ? and state_id=?";
	final static public String selectERESI = "select * from esi_details where financial_year_start= ? and financial_year_end = ? and state_id =?";

	final static public String selectLWF = "select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? and org_id=?";
	final static public String selectERLWF = "select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? and org_id=?";
	final static public String selectERLWFC = "select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id =? and min_limit<=? and max_limit>=? and org_id=?";

	final static public String selectEEPF = "select * from epf_details where financial_year_start= ? and financial_year_end = ?";
	final static public String selectERPF = "select * from epf_details where financial_year_start= ? and financial_year_end = ?";
	final static public String selectEESI = "select * from esi_details where financial_year_start= ? and financial_year_end = ? and state_id=?";
	final static public String selectTDS = "select sum(amount) as tds from payroll_generation where salary_head_id = ? and emp_id = ? and financial_year_from_date = ? and financial_year_to_date = ?";
	final static public String selectTDS1 = "select sum(amount) as amount from payroll_generation where emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ?";
	final static public String selectTDS2 = "select amount from tds_projections where salary_head_id = ? and emp_id = ? and fy_year_from = ? and fy_year_end = ? and _date<=?";
	final static public String selectDeduction = "select * from deduction_tax_details where age_from<=? and age_to>? and gender=? and financial_year_from=? and financial_year_to=? and _from<=? and _to>? and slab_type=? order by _from limit 1";

	final static public String selectLoanDetails = "select * from  loan_details ld, loan_applied_details lad where lad.loan_id = ld.loan_id and lad.loan_applied_id=?";
	final static public String selectLoanPayments = "select * from loan_payments where loan_applied_id =?";
	final static public String selectLoanPayments1 = "select *, lp.amount_paid as amount_paid_p, lad.amount_paid as amount_paid_a from loan_applied_details lad, loan_details ld, loan_payments lp where lp.loan_applied_id = lad.loan_applied_id and ld.loan_id = lad.loan_id  and lp.emp_id = lad.emp_id  and is_approved = 1 order by lp.emp_id ";
	final static public String insertLoanDetails = "insert into loan_details (loan_code, loan_description, min_service_years, loan_interest, fine_amount, times_salary, entry_date, user_id, org_id) values (?,?,?,?,?,?,?,?,?)";
	final static public String updateLoanDetails = "update loan_details set loan_code=?, loan_description=?, min_service_years=?, loan_interest=?, fine_amount=?, times_salary=?, entry_date=?, user_id=?,is_check_previous_loan=? where loan_id=?";
	final static public String selectLoanDetails1 = "select * from loan_details where loan_id =?";
	final static public String deleteLoanDetails = "delete from loan_details where loan_id=?";
	final static public String updateLoanDetails1 = "update loan_applied_details set loan_acc_no=? where loan_applied_id=?";
	final static public String updateLoanDetails2 = "update loan_applied_details set approved_by=? , approved_date=?, is_approved=?,approve_reason=? where loan_applied_id=?";
	final static public String selectLoanApplied = "select * from loan_applied_details lad, loan_details ld where  lad.loan_id = ld.loan_id and loan_applied_id=?";
	final static public String updateLoanApplied1 = "update loan_applied_details set balance_amount=?, loan_emi=? where loan_applied_id=?";
	final static public String selectLoanApplied2 = "select * from  loan_applied_details where balance_amount>0 and is_approved=1 and emp_id=?";
	final static public String selectEmpSalaryDetails1 = "select * from emp_salary_details where emp_id = ? and entry_date = (select max(entry_date) from emp_salary_details where emp_id = ? and entry_date<= ? )";
	final static public String insertLoanAppliedDetails = "insert into loan_applied_details (loan_id, emp_id, applied_by, applied_date, loan_desc, duration_months, amount_paid,effective_date) values (?,?,?,?, ?,?,?,?)";
	final static public String selectLoanDetails2 = "select * from loan_details where org_id =? order by loan_code";
	final static public String insertLoanpayment = "insert into loan_payments (emp_id, loan_id, amount_paid, paid_date, pay_source, loan_applied_id, user_id, ins_no, ins_date) values (?,?,?,?,?,?,?,?,?)";
	final static public String updateLoanPayment = "update loan_applied_details set balance_amount=?, is_completed=?  where loan_applied_id=?";

	final static public String deleteTDSProjection = "delete from tds_projections where month=? and emp_id=? and fy_year_from=? and fy_year_end=?";
	final static public String updateTDSProjection = "update tds_projections set amount=? where month=? and emp_id=? and fy_year_from=? and fy_year_end=?";
	final static public String insertTDSProjection = "insert into tds_projections (amount,month,emp_id,salary_head_id, fy_year_from, fy_year_end) values (?,?,?,?,?,?)";

	final static public String selectUpcomingEventsDashboard = "select * from events where event_date >= ? order by event_date desc limit 5";
	final static public String selectReimbursementForDashboard = "select * from emp_reimbursement where approval_1=0 order  by entry_date desc limit 5";
	final static public String selectRequisitionForDashboard = "select * from requisition_details where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and status=0 order by requisition_date desc";
	final static public String pendingExceptionDahsboard = "select count(*) as count from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and approved = -2 ";
	final static public String pendingReimbursementDashboardCount = "select count(*) as count from emp_reimbursement where (approval_2 = 0  or approval_2 is null)";
	final static public String pendingRequisitionDashboardCount = "select count(*) as count from requisition_details where status = 0";
	final static public String pendingTaskDashboardCount = "select count(*) as count from activity_info where finish_task = 'y' and approve_status = 'n'";
	final static public String pendingReportDashboardCount = "select count(*) as count from task_activity where issent_report = true and task_date=?";
	final static public String pendingTopEmployeeDashboardCount = "select sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hours, rd.emp_id from attendance_details ad, roster_details rd, employee_official_details eod where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and eod.emp_id = rd.emp_id and eod.emp_id = ad.emp_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' and rd._date between ? and ? group by rd.emp_id order by hours_worked desc limit 5";
	final static public String pendingServiceEmployeeDashboardCount = "select service_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id  = eod.emp_id and epd.is_alive=true";
	final static public String pendingWLocationDashboardCount = "select count(eod.emp_id) as count, wlocation_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.is_alive=true and wlocation_id > 0 group by wlocation_id";
	final static public String pendingSkillEmployeeDashboardCount = "";
	final static public String pendingEmployeeTurnoverDashboardCount = "select count(*) as joining_count from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id and joining_date between ? and ? and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?)";
	final static public String pendingEmployeeLeavingDashboardCount = "select count(*) as leaving_count from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id and employment_end_date between ? and ? and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?)";
	final static public String pendingPendingApprovalDashboardCount = "SELECT approved, count(approved) as count FROM attendance_details ad, employee_official_details eod where ad.emp_id = eod.emp_id and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ? group by approved";
	final static public String pendingPendingApprovalDashboardCount1 = "select approval_2, count(approval_2) from emp_reimbursement er, employee_official_details eod where er.emp_id = eod.emp_id group By approval_2;";

	final static public String pendingPendingApprovalDashboardCount2 = "select count(*) as count, is_approved from emp_leave_entry elt, employee_official_details eod where eod.emp_id = elt.emp_id and entrydate between ? AND ? group by is_approved ";

	final static public String selectDesigOrgId = "SELECT * FROM designation_details ald INNER JOIN level_details ld ON ald.level_id = ld.level_id where org_id =? order by designation_name";
	final static public String selectDepartmentR2 = "select * from department_info di where org_id = ? order by parent";
	final static public String updateLoanPyroll1 = "update loan_applied_details set balance_amount =?, is_completed=? where loan_applied_id =?";
	final static public String departmentEmployeeDashboardCount = "select count(*),depart_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id  = eod.emp_id and epd.is_alive=true group by depart_id";
	final static public String pendingExceptions = "select * from attendance_details ad, employee_official_details eod where eod.emp_id = ad.emp_id and ad.emp_id in (select emp_id from employee_official_details where is_roster = true) and approved = ? and wlocation_id = ? order by in_out_timestamp desc";
	final static public String pendingExceptionsManager = "select * from attendance_details ad, employee_official_details eod where eod.emp_id = ad.emp_id and ad.emp_id in (select emp_id from employee_official_details where is_roster = true) and approved = ? and supervisor_emp_id = ? order by in_out_timestamp desc";
	final static public String selectLoanDetails3 = "select * from loan_payments where paycycle_start=? and paycycle_end=? and pay_source=?";

	final static public String pendingExceptionDahsboardHR = "select count(*) as count from attendance_details ad, employee_official_details eod where eod.emp_id = ad.emp_id and ad.emp_id in (select emp_id from employee_official_details where is_roster = true) and approved = ? and wlocation_id = ? ";

	final static public String resignedemployeeorganisationwise = "select e.*,wfd.user_type_id as user_type from (select * from emp_off_board eob, employee_official_details eod, employee_personal_details epd where eod.emp_id = eob.emp_id  and epd.emp_per_id = eob.emp_id and eod.emp_id = epd.emp_per_id and epd.emp_per_id in (select emp_id from user_details where status != 'INACTIVE')  and eod.org_id = ? ) e, work_flow_details wfd where e.off_board_id = wfd.effective_id and (wfd.effective_type = 'Resign' or wfd.effective_type = 'Termination')  order by e.entry_date desc";
}