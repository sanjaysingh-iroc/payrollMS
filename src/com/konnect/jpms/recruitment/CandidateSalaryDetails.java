package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CandidateSalaryDetails extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5589342412086925839L;


	public String getCandID() {
		return CandID;
	}

	public void setCandID(String candID) {
		CandID = candID;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getCtcAmt() {
		return ctcAmt;
	}

	public void setCtcAmt(String ctcAmt) {
		this.ctcAmt = ctcAmt;
	}

	HttpSession session;
	CommonFunctions CF;
	String CandID;
	String recruitId;
	
	private String ctcAmt;
	
	private List<FillSalaryHeads> salaryHeadList;
	
	private boolean disableSalaryStructure;

	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE,"/jsp/recruitment/CandidateSalaryDetails.jsp");
		request.setAttribute("empId", getCandID());

//		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

		viewCandidateSalaryDetails();
		return "tab";

	}
	
	private void viewCandidateSalaryDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);

			String level_id = null;
			pst = con.prepareStatement("select level_id,is_disable_sal_calculate from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("pst level id ===> " + pst);
//			System.out.println("new Date ===> " + new Date());
			if (rs.next()) {
				level_id = rs.getString("level_id");
				setDisableSalaryStructure(uF.parseToBoolean(rs.getString("is_disable_sal_calculate")));
			}
			rs.close();
			pst.close();
			
			String salaryBandId = CF.getSalaryBandId(con, getCtcAmt(), level_id);
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con, uF.parseToInt(level_id));
			
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(level_id);
			
//			System.out.println("getRecruitId ===>> "+getRecruitId()+" --- getCandID ===>> "+getCandID()+" --- level_id ===> " + level_id);
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id=? and salary_band_id=? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(level_id));
			pst.setInt(2, uF.parseToInt(salaryBandId));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))){
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();

			List<String> alSalaryDuplicationTracer = new ArrayList<String>();
			List<List<String>> al = new ArrayList<List<String>>();			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id=? and salary_band_id=? and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(level_id));
			pst.setInt(2, uF.parseToInt(salaryBandId));
//			System.out.println("pst Salary deatils ===> " + pst);
			rs = pst.executeQuery();
//			double dblTotGrossSal = 0;
//			double dblTotDeductSal = 0;
			while (rs.next()) {
//				if(rs.getString("salary_head_amount_type") != null && !rs.getString("salary_head_amount_type").equals("P")) {
//					if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("E")) {
//						dblTotGrossSal += uF.parseToDouble(rs.getString("salary_head_amount"));
//					} else if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("D")) {
//						dblTotDeductSal += uF.parseToDouble(rs.getString("salary_head_amount"));
//					}
//				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("salary_head_id"));	//0
				alInner.add(rs.getString("salary_head_name"));	//1
				alInner.add(rs.getString("earning_deduction"));	//2
				alInner.add(rs.getString("salary_head_amount_type"));	//3
				String rsHeadId = rs.getInt("sub_salary_head_id") + "";
				alInner.add(rsHeadId);	//4
				
				alInner.add("0");	//5
				if(rs.getInt("salary_head_id") == CTC && uF.parseToDouble(getCtcAmt())>0) {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(getCtcAmt())));	//6
				} else {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//6
				}
				StringBuilder sbMulcalType = new StringBuilder();
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))){
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
				}
				alInner.add(rs.getString("multiple_calculation")); //7
				alInner.add(sbMulcalType.toString()); //8
				alInner.add(rs.getString("max_cap_amount"));	//9
				alInner.add(rs.getBoolean("is_contribution") ? "T" : "F");	//10
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				if(index>=0){
					al.remove(index);
					al.add(index, alInner);
				}else{
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					al.add(alInner);
				}

			}
			rs.close();
			pst.close();
			
//			System.out.println("al ===> "+al);
//			System.out.println("dblTotGrossSal ===>> j " + dblTotGrossSal);
//			System.out.println("dblTotDeductSal ===>> j " + dblTotDeductSal);
//			request.setAttribute("dblTotGrossSal", uF.formatIntoTwoDecimalWithOutComma(dblTotGrossSal));
//			request.setAttribute("dblTotDeductSal", uF.formatIntoTwoDecimalWithOutComma(dblTotDeductSal));
			request.setAttribute("reportList", al);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public boolean isDisableSalaryStructure() {
		return disableSalaryStructure;
	}

	public void setDisableSalaryStructure(boolean disableSalaryStructure) {
		this.disableSalaryStructure = disableSalaryStructure;
	}
}