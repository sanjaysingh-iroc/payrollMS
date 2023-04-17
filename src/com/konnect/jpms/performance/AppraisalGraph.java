package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AppraisalGraph extends ActionSupport implements
ServletRequestAware, IStatements {
	HttpSession session;
	private HttpServletRequest request;
	private CommonFunctions CF;
	
	private String appraisal;
	public String getAppraisal() {
		return appraisal;
	}

	public void setAppraisal(String appraisal) {
		this.appraisal = appraisal;
	}

	public String execute() {
//		System.out.println("sdkjsdfjkdsfgsdhhjsfhjh");

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/AppraisalGraph.jsp");
		request.setAttribute(TITLE, "Appraisal BellsChart");
		getData();
		return SUCCESS;

	}
	
	public void getData(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			if(appraisal!=null && appraisal.length()>1){
				if(appraisal.charAt(appraisal.length()-1)==','){
					appraisal=appraisal.substring(0,appraisal.length()-1);
				}
			}
			
			if(appraisal!=null){
				pst=con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id "
						+"  and (is_delete is null or is_delete = false) and  appraisal_details_id in("+appraisal+")");
			}else{
				pst=con.prepareStatement("select * from appraisal_details  a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id "
						+"  and (is_delete is null or is_delete = false) order by appraisal_details_id");

			}
			Map<String,String> appraisalMp=new HashMap<String,String> ();
			rs=pst.executeQuery();
			while(rs.next()){
				appraisalMp.put(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"),rs.getString("appraisal_name")+" ("+rs.getString("appraisal_freq_name")+")");
			}
			rs.close();
			pst.close();
			
			
			if(appraisal!=null){
				pst=con.prepareStatement("select count(*)as emp,appraisal_id,average,appraisal_freq_id from(select *,cast(marks*10/weightage as integer) as average from ( "+
						"select sum(marks) as marks,sum(weightage) as weightage,emp_id,appraisal_id,appraisal_freq_id from appraisal_question_answer where appraisal_id in("+appraisal+") and weightage>0 group by emp_id,appraisal_id,appraisal_freq_id"+
						") as a ) as b group by appraisal_id,average,appraisal_freq_id order by average");
			}else{
				pst=con.prepareStatement("select count(*)as emp,appraisal_id,average,appraisal_freq_id from(select *,cast(marks*10/weightage as integer) as average from ( "+
						"select sum(marks) as marks,sum(weightage) as weightage,emp_id,appraisal_id,appraisal_freq_id from appraisal_question_answer where weightage>0 group by emp_id,appraisal_id,appraisal_freq_id"+
						") as a ) as b group by appraisal_id,average,appraisal_freq_id order by average");
			}
		
			Map<String,List<List<String>>> appraisalData=new HashMap();
			rs=pst.executeQuery();
			
			while(rs.next()){
				List<List<String>> outerList=appraisalData.get(rs.getString("appraisal_id")+"_"+ rs.getString("appraisal_freq_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("emp"));
				innerList.add(rs.getString("average"));
				outerList.add(innerList);
				appraisalData.put(rs.getString("appraisal_id")+"_"+ rs.getString("appraisal_freq_id"),outerList);

			}
			rs.close();
			pst.close();
			
			Set<String> keys=appraisalData.keySet();
			
			Iterator<String> it=keys.iterator();
			String data="";
			while(it.hasNext()){
				String key=it.next();
				List<List<String>> outerList=appraisalData.get(key);
				StringBuilder sb=new StringBuilder();
				sb.append("{ name: '"+appraisalMp.get(key) +"' ,");
				sb.append("data: [");
				for(int i=0;i<outerList.size();i++){
					List<String> innerList=outerList.get(i);
					sb.append("[");
					sb.append(innerList.get(1));
					sb.append(",");
					sb.append(innerList.get(0));
					if(i==outerList.size()-1)
					sb.append("]");
					else
						sb.append("],");
				}
				
				
				sb.append("]},");
				
//				{
//	                name: 'Winter 2008-2009',
//	                data: [
//	                    [1, 0   ],
//	                    [3, 0.2 ],
//	                    [4, 0.47]
//	                   
//	                ]
//	            }, 
				
				data+=sb.toString();
			}
//			System.out.println("===="+data);
			request.setAttribute("data", data);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
