package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateVDAIndex extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strOrgId;
	String strSessionEmpId;
	
	String userscreen;
	String navigationId;
	String toPage;
	String paycycle;
	String operation;
	String strOrgName;
	String strVdaRateId;
	
	List<String> strDesigIds;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		System.out.println("getOperation ============>> " + getOperation());
		if (getOperation()!=null && getOperation().equals("VIEW")) {
			return viewVDAPaycycleAmount();
		} else if (getOperation()!=null && getOperation().equals("E")) {
			return viewVDAIndexDetails();
		} else if (getOperation()!=null && getOperation().equals("U")) {
			calculateAndUpdateVDAAmountDesigAndPaycyclewise();
			return updateVDAIndex();
		}
		return LOAD;
	}
	
	
	private String viewVDAPaycycleAmount() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			strOrgName = CF.getOrgNameById(con, getStrOrgId());
			
			pst = con.prepareStatement("select * from vda_rate_details where vda_rate_id=?");
			pst.setInt(1, uF.parseToInt(getStrVdaRateId()));
			rs = pst.executeQuery();
			Map<String, String> hmPaycycleData = new HashMap<String, String>();
			while (rs.next()) {
				hmPaycycleData.put("PAYCYCLE_ID", rs.getString("paycycle"));
				hmPaycycleData.put("FROM_DATE", rs.getString("from_date"));
				hmPaycycleData.put("TO_DATE", rs.getString("to_date"));
			}
			rs.close();
			pst.close();
			System.out.println("hmPaycycleData ===>> " + hmPaycycleData);
			
			Map<String, Map<String, String>> hmVDAPaycycleAmountData = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from designation_details dd, vda_rate_details vrd where dd.designation_id=vrd.desig_id and " +
				"dd.level_id in (select level_id from level_details where org_id=?) and vrd.paycycle=? and vrd.from_date=? and vrd.to_date=?");
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			pst.setInt(2, uF.parseToInt(hmPaycycleData.get("PAYCYCLE_ID")));
			pst.setDate(3, uF.getDateFormat(hmPaycycleData.get("FROM_DATE"), DBDATE));
			pst.setDate(4, uF.getDateFormat(hmPaycycleData.get("TO_DATE"), DBDATE));
			System.out.println("pst ===>> " + pst);
			rs=pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("DESIG_NAME", rs.getString("designation_name") + "[" + rs.getString("designation_code") + "]");
				hm.put("VDA_AMOUNT_PROBATION", uF.showData(uF.formatIntoOneDecimal(rs.getDouble("vda_amount_probation")), "0"));
				hm.put("VDA_AMOUNT_PERMANENT", uF.showData(uF.formatIntoOneDecimal(rs.getDouble("vda_amount_permanent")), "0"));
				hm.put("VDA_AMOUNT_TEMPORARY", uF.showData(uF.formatIntoOneDecimal(rs.getDouble("vda_amount_temporary")), "0"));
				hmVDAPaycycleAmountData.put(rs.getString("designation_id"), hm);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmVDAPaycycleAmountData", hmVDAPaycycleAmountData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}


	public String viewVDAIndexDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			strOrgName = CF.getOrgNameById(con, getStrOrgId());
			Map<String, Map<String, String>> hmVDAIndexData = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from designation_details where level_id in (select level_id from level_details where org_id=?)");
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			rs=pst.executeQuery();
			while (rs.next()) {
				setOperation("U");
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("DESIG_NAME", rs.getString("designation_name") + "[" + rs.getString("designation_code") + "]");
				hm.put("VDA_INDEX_PROBATION", uF.showData(rs.getString("vda_index_probation"), "0"));
				hm.put("VDA_INDEX_PERMANENT", uF.showData(rs.getString("vda_index_permanent"), "0"));
				hm.put("VDA_INDEX_TEMPORARY", uF.showData(rs.getString("vda_index_temporary"), "0"));
				hmVDAIndexData.put(rs.getString("designation_id"), hm);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmVDAIndexData", hmVDAIndexData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	
	public String updateVDAIndex() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			boolean flag = false;
			pst = con.prepareStatement("update designation_details set vda_index_probation=?,vda_index_permanent=?,vda_index_temporary=? where designation_id=?");
			for(int i=0; i<getStrDesigIds().size(); i++) {
				String strDesigId = getStrDesigIds().get(i);
				String vdaIndexProbation = request.getParameter(strDesigId+"_vdaRateProbation");
				String vdaIndexPermanent = request.getParameter(strDesigId+"_vdaRatePermanent");
				String vdaIndexTemporary = request.getParameter(strDesigId+"_vdaRateTemporary");
				pst.setDouble(1, uF.parseToDouble(vdaIndexProbation));
				pst.setDouble(2, uF.parseToDouble(vdaIndexPermanent));
				pst.setDouble(3, uF.parseToDouble(vdaIndexTemporary));
				pst.setInt(4, uF.parseToInt(strDesigId));
				pst.addBatch();
				flag = true;
			}
			if (flag) {
				int[] x = pst.executeBatch();
				if (x.length > 0) {
					con.commit();
					session.setAttribute(MESSAGE, SUCCESSM + "VDA Index is updated successfully." + END);
				} else {
					con.rollback();
					session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Index. Please,try again." + END);
				}
			} else {
				con.rollback();
				session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Index. Please,try again." + END);
			}
			
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	public String calculateAndUpdateVDAAmountDesigAndPaycyclewise() {
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			boolean flag = false;
			boolean flag1 = false;
			pst = con.prepareStatement("select * from vda_rate_details where org_id=? and (desig_id is null or desig_id=0) order by from_date desc,to_date desc limit 1");
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			rs = pst.executeQuery();
			Map<String, String> hmExistDesigForLastPaycycle = new HashMap<String, String>();
			while (rs.next()) {
				hmExistDesigForLastPaycycle.put(rs.getString("paycycle")+"_"+rs.getString("from_date")+"_"+rs.getString("to_date"), rs.getString("vda_rate"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from vda_rate_details where org_id=? order by from_date,to_date");
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			rs = pst.executeQuery();
			Map<String, List<String>> hmVDARateData = new HashMap<String, List<String>>();
			Map<String, List<String>> hmVDARateDesigData = new HashMap<String, List<String>>();
			Map<String, String> hmExistDesigForPaycycle = new HashMap<String, String>();
			while (rs.next()) {
				setOperation("U");
				if(rs.getString("desig_id") == null || rs.getInt("desig_id") == 0) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("paycycle"));
					innerList.add(rs.getString("from_date"));
					innerList.add(rs.getString("to_date"));
					innerList.add(rs.getString("vda_rate"));
					hmVDARateData.put(rs.getString("vda_rate_id"), innerList);
					
				} else {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("paycycle"));
					innerList.add(rs.getString("from_date"));
					innerList.add(rs.getString("to_date"));
					innerList.add(rs.getString("vda_rate"));
					innerList.add(rs.getString("desig_id"));
					innerList.add(rs.getString("vda_index_probation")); //5
					innerList.add(rs.getString("vda_index_permanent")); //6
					innerList.add(rs.getString("vda_index_temporary")); //7
					innerList.add(rs.getString("vda_amount_probation")); //8
					innerList.add(rs.getString("vda_amount_permanent")); //9
					innerList.add(rs.getString("vda_amount_temporary")); //10
					
					hmVDARateDesigData.put(rs.getString("desig_id")+"_"+rs.getString("paycycle")+"_"+rs.getString("from_date"), innerList);
				}
				System.out.println(rs.getString("desig_id") + " -- vda_index_probation ===>> " + rs.getDouble("vda_index_probation"));
				if(rs.getDouble("vda_index_probation")>0 || rs.getDouble("vda_index_permanent")>0 || rs.getDouble("vda_index_temporary")>0) {
					System.out.println(rs.getString("desig_id") + " -- IN -- vda_index_probation ===>> " + rs.getDouble("vda_index_probation"));
					hmExistDesigForPaycycle.put(rs.getString("paycycle")+"_"+rs.getString("from_date")+"_"+rs.getString("to_date"), rs.getString("desig_id"));
				}
			}
			rs.close();
			pst.close();
			
			System.out.println("hmVDARateData ===>> " + hmVDARateData);
			System.out.println("hmExistDesigForPaycycle ===>> " + hmExistDesigForPaycycle);
			Iterator<String> it = hmVDARateData.keySet().iterator();
			pst = con.prepareStatement("update vda_rate_details set vda_index_probation=?,vda_index_permanent=?,vda_index_temporary=?," +
				"vda_amount_probation=?,vda_amount_permanent=?,vda_amount_temporary=?,updated_by=?,update_date=? where desig_id=? and " +
				"paycycle=? and from_date=? and to_date=?");
			pst1 = con.prepareStatement("insert into vda_rate_details (org_id,paycycle,from_date,to_date,vda_rate,desig_id,vda_index_probation," +
				"vda_index_permanent,vda_index_temporary,vda_amount_probation,vda_amount_permanent,vda_amount_temporary,added_by,entry_date)" +
				"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			while (it.hasNext()) {
				String vdaRateId = it.next();
				List<String> innerList = hmVDARateData.get(vdaRateId);
				if(hmExistDesigForPaycycle.get(innerList.get(0)+"_"+innerList.get(1)+"_"+innerList.get(2)) == null || hmExistDesigForPaycycle.get(innerList.get(0)+"_"+innerList.get(1)+"_"+innerList.get(2)).isEmpty() 
						|| hmExistDesigForLastPaycycle.get(innerList.get(0)+"_"+innerList.get(1)+"_"+innerList.get(2)) != null) {
					String vdaRate = innerList.get(3);
					boolean updateFlag = false;
					boolean insertFlag = false;
					System.out.println("innerList ===>> " + innerList);
					for(int i=0; i<getStrDesigIds().size(); i++) {
						String strDesigId = getStrDesigIds().get(i);
						String vdaIndexProbation = request.getParameter(strDesigId+"_vdaRateProbation");
						String vdaIndexPermanent = request.getParameter(strDesigId+"_vdaRatePermanent");
						String vdaIndexTemporary = request.getParameter(strDesigId+"_vdaRateTemporary");
						List<String> innList = hmVDARateDesigData.get(strDesigId+"_"+innerList.get(0)+"_"+innerList.get(1));
						double vdaAmtProbation = 0.0d;
						double vdaAmtPermanent = 0.0d;
						double vdaAmtTemporary = 0.0d;
						if(innList == null || innList.size()==0 || (innList != null && innList.size()>0 && uF.parseToDouble(vdaIndexProbation) != uF.parseToDouble(innList.get(5)))) {
							vdaAmtProbation = ((uF.parseToDouble(vdaRate) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(vdaIndexProbation);
							if(innList != null && innList.size()>0) {
								updateFlag = true;
							} else {
								insertFlag = true;
							}
						} else {
							vdaAmtProbation = uF.parseToDouble(innList.get(8));
						}
						if(innList == null || innList.size()==0 || (innList != null && innList.size()>0 && uF.parseToDouble(vdaIndexPermanent) != uF.parseToDouble(innList.get(6)))) {
							vdaAmtPermanent = ((uF.parseToDouble(vdaRate) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(vdaIndexPermanent);
							if(innList != null && innList.size()>0) {
								updateFlag = true;
							} else {
								insertFlag = true;
							}
						} else {
							vdaAmtPermanent = uF.parseToDouble(innList.get(9));
						}
						if(innList == null || innList.size()==0 || (innList != null && innList.size()>0 && uF.parseToDouble(vdaIndexTemporary) != uF.parseToDouble(innList.get(7)))) {
							vdaAmtTemporary = ((uF.parseToDouble(vdaRate) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(vdaIndexTemporary);
							if(innList != null && innList.size()>0) {
								updateFlag = true;
							} else {
								insertFlag = true;
							}
						} else {
							vdaAmtTemporary = uF.parseToDouble(innList.get(10));
						}
						System.out.println(" updateFlag ===>> " + updateFlag);
						System.out.println(" insertFlag ===>> " + insertFlag);
						if(updateFlag) {
							pst.setDouble(1, uF.parseToDouble(vdaIndexProbation));
							pst.setDouble(2, uF.parseToDouble(vdaIndexPermanent));
							pst.setDouble(3, uF.parseToDouble(vdaIndexTemporary));
							pst.setDouble(4, vdaAmtProbation);
							pst.setDouble(5, vdaAmtPermanent);
							pst.setDouble(6, vdaAmtTemporary);
							pst.setInt(7, uF.parseToInt(strSessionEmpId));
							pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(9, uF.parseToInt(strDesigId));
							pst.setInt(10, uF.parseToInt(innerList.get(0)));
							pst.setDate(11, uF.getDateFormat(innerList.get(1), DBDATE));
							pst.setDate(12, uF.getDateFormat(innerList.get(2), DBDATE));
							pst.addBatch();
							flag = true;
						}
						if(insertFlag) {
							pst1.setInt(1, uF.parseToInt(getStrOrgId()));
							pst1.setInt(2, uF.parseToInt(innerList.get(0)));
							pst1.setDate(3, uF.getDateFormat(innerList.get(1), DBDATE));
							pst1.setDate(4, uF.getDateFormat(innerList.get(2), DBDATE));
							pst1.setDouble(5, uF.parseToDouble(vdaRate));
							pst1.setInt(6, uF.parseToInt(strDesigId));
							pst1.setDouble(7, uF.parseToDouble(vdaIndexProbation));
							pst1.setDouble(8, uF.parseToDouble(vdaIndexPermanent));
							pst1.setDouble(9, uF.parseToDouble(vdaIndexTemporary));
							pst1.setDouble(10, vdaAmtProbation);
							pst1.setDouble(11, vdaAmtPermanent);
							pst1.setDouble(12, vdaAmtTemporary);
							pst1.setInt(13, uF.parseToInt(strSessionEmpId));
							pst1.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
							pst1.addBatch();
							flag1 = true;
						}
					}
				}
			}
			
			if (flag) {
				int[] x = pst.executeBatch();
				if (x.length > 0) {
					con.commit();
				} else {
					con.rollback();
					session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Index. Please,try again." + END);
				}
			}
			
			if (flag1) {
				int[] x = pst1.executeBatch();
				if (x.length > 0) {
					con.commit();
				} else {
					con.rollback();
					session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Index. Please,try again." + END);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrOrgName() {
		return strOrgName;
	}

	public void setStrOrgName(String strOrgName) {
		this.strOrgName = strOrgName;
	}

	public List<String> getStrDesigIds() {
		return strDesigIds;
	}

	public void setStrDesigIds(List<String> strDesigIds) {
		this.strDesigIds = strDesigIds;
	}

	public String getStrVdaRateId() {
		return strVdaRateId;
	}

	public void setStrVdaRateId(String strVdaRateId) {
		this.strVdaRateId = strVdaRateId;
	}


}