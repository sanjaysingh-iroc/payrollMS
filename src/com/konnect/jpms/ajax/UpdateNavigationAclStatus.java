package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateNavigationAclStatus extends ActionSupport implements IStatements, ServletRequestAware {

	public String execute() throws Exception {

		String strStatus = (String)request.getParameter("S");
		String strUserType = (String)request.getParameter("UT");
		String strNavId = (String)request.getParameter("NID");
		String strC = (String)request.getParameter("C");
		String strType = (String)request.getParameter("type");
		
		updateNavigationStatus(strStatus, strUserType, strNavId, strC, strType);

		return SUCCESS;
	}


	public boolean updateNavigationStatus(String strStatus, String strUserType, String strNavId, String strC, String strType){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		boolean isExist = false;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from navigation_1 where navigation_id=?");			
			pst.setInt(1, uF.parseToInt(strNavId));			
			rst = pst.executeQuery();
			
			String strUserTypeId = null;
			
			while(rst.next()){
				strUserTypeId = rst.getString("user_type_id");
			}
            rst.close();
            pst.close();
			
			
			StringBuilder sb = new StringBuilder();
			
			/*if(uF.parseToInt(strStatus)==1 && strUserTypeId!=null){
				sb.append(strUserTypeId+strUserType+",");
			}else if(uF.parseToInt(strStatus)==-1 && strUserTypeId!=null && strUserType!=null){
				String []arr = strUserTypeId.split(",");
				
				for(int i=0; arr!=null && i<arr.length; i++){
					if(!strUserType.equalsIgnoreCase(arr[i])){
						sb.append(arr[i]+",");
					}
				}
			}*/
			
			
			if(strType!=null && strType.equalsIgnoreCase("A")){
				pst = con.prepareStatement("update navigation_acl set is_add =? where navigation_id=? and user_id=?");
				pst.setBoolean(1, uF.parseToBoolean(strStatus));
				pst.setInt(2, uF.parseToInt(strNavId));
				pst.setInt(3, uF.parseToInt(strUserType));
				pst.execute();
	            pst.close();
				if(uF.parseToInt(strStatus)==1 && strUserTypeId!=null){
					/*request.setAttribute("STATUS_MSG", "<img src=\"images1/tick.png\" title=\"Disable\" onclick=\"getContent('myDiv_A_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=A&S=-1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\">");*/
					request.setAttribute("STATUS_MSG", "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Disable\" onclick=\"getContent('myDiv_A_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=A&S=-1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\"></i>");
				}else if(uF.parseToInt(strStatus)==-1 && strUserTypeId!=null && strUserType!=null){
					 /*request.setAttribute("STATUS_MSG", "<img src=\"images1/cross.png\" title=\"Enable\" onclick=\"getContent('myDiv_A_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=A&S=1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\">");*/
					request.setAttribute("STATUS_MSG", "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Enable\"  onclick=\"getContent('myDiv_A_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=A&S=1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\"></i>");
					
				}
				
			}else if(strType!=null && strType.equalsIgnoreCase("U")){
				pst = con.prepareStatement("update navigation_acl set is_update =? where navigation_id=? and user_id=?");
				pst.setBoolean(1, uF.parseToBoolean(strStatus));
				pst.setInt(2, uF.parseToInt(strNavId));
				pst.setInt(3, uF.parseToInt(strUserType));
				pst.execute();
	            pst.close();
				if(uF.parseToInt(strStatus)==1 && strUserTypeId!=null){
					/*request.setAttribute("STATUS_MSG", "<img src=\"images1/tick.png\" title=\"Disable\" onclick=\"getContent('myDiv_U_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=U&S=-1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\">");*/
					request.setAttribute("STATUS_MSG", "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Disable\" onclick=\"getContent('myDiv_U_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=U&S=-1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\"></i>");
					
				}else if(uF.parseToInt(strStatus)==-1 && strUserTypeId!=null && strUserType!=null){
					/*request.setAttribute("STATUS_MSG", "<img src=\"images1/cross.png\" title=\"Enable\" onclick=\"getContent('myDiv_U_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=U&S=1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\">");*/
					request.setAttribute("STATUS_MSG", "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Enable\" onclick=\"getContent('myDiv_U_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=U&S=1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\"></i>");
				}
			}else if(strType!=null && strType.equalsIgnoreCase("D")){
				pst = con.prepareStatement("update navigation_acl set is_delete =? where navigation_id=? and user_id=?");
				pst.setBoolean(1, uF.parseToBoolean(strStatus));
				pst.setInt(2, uF.parseToInt(strNavId));
				pst.setInt(3, uF.parseToInt(strUserType));
				pst.execute();
	            pst.close();
				if(uF.parseToInt(strStatus)==1 && strUserTypeId!=null){
					/*request.setAttribute("STATUS_MSG", "<img src=\"images1/tick.png\" title=\"Disable\" onclick=\"getContent('myDiv_D_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=D&S=-1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\">");*/
					request.setAttribute("STATUS_MSG", "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Disable\" onclick=\"getContent('myDiv_D_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=D&S=-1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\"></i>");
					
				}else if(uF.parseToInt(strStatus)==-1 && strUserTypeId!=null && strUserType!=null){
					/*request.setAttribute("STATUS_MSG", "<img src=\"images1/cross.png\" title=\"Enable\" onclick=\"getContent('myDiv_D_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=D&S=1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\">");*/
					request.setAttribute("STATUS_MSG", "<i class=\"fa fa-times cross\" aria-hidden=\"true\"  title=\"Enable\" onclick=\"getContent('myDiv_D_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=D&S=1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\"></i>");
					
				}
			}else if(strType!=null && strType.equalsIgnoreCase("V")){
				pst = con.prepareStatement("update navigation_acl set is_view =? where navigation_id=? and user_id=?");
				pst.setBoolean(1, uF.parseToBoolean(strStatus));
				pst.setInt(2, uF.parseToInt(strNavId));
				pst.setInt(3, uF.parseToInt(strUserType));
				pst.execute();
	            pst.close();
				if(uF.parseToInt(strStatus)==1 && strUserTypeId!=null){
					/*request.setAttribute("STATUS_MSG", "<img src=\"images1/tick.png\" title=\"Disable\" onclick=\"getContent('myDiv_V_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=V&S=-1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\">");*/
					request.setAttribute("STATUS_MSG", "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Disable\" onclick=\"getContent('myDiv_V_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=V&S=-1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\"></i>");
				}else if(uF.parseToInt(strStatus)==-1 && strUserTypeId!=null && strUserType!=null){
					/*request.setAttribute("STATUS_MSG", "<img src=\"images1/cross.png\" title=\"Enable\" onclick=\"getContent('myDiv_V_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=V&S=1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\">");*/
					request.setAttribute("STATUS_MSG", "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Enable\" onclick=\"getContent('myDiv_V_"+strC+"_"+strUserType+"', 'UpdateNavigationStatus.action?type=V&S=1&UT="+strUserType+"&NID="+strNavId+"&C="+strC+"');\"></i>");
					
				}
			}
			
			
			pst = con.prepareStatement("select * from navigation_acl where navigation_id=? and user_id =? ");
			pst.setInt(1, uF.parseToInt(strNavId));
			pst.setInt(2, uF.parseToInt(strUserType));
			rst = pst.executeQuery();
			boolean isDelete = false;
			boolean isUpdate = false;
			boolean isView = false;
			boolean isAdd = false;
			int count = 0;
			
			
			while(rst.next()){
				count++;
				isDelete = uF.parseToBoolean(rst.getString("is_delete"));
				isUpdate = uF.parseToBoolean(rst.getString("is_update"));
				isView = uF.parseToBoolean(rst.getString("is_view"));
				isAdd = uF.parseToBoolean(rst.getString("is_add"));
			}
            rst.close();
            pst.close();
			
			
			
			if(count==0){
				pst = con.prepareStatement("insert into navigation_acl (navigation_id, user_id) values (?,?) ");
				pst.setInt(1, uF.parseToInt(strNavId));
				pst.setInt(2, uF.parseToInt(strUserType));
				pst.execute();
	            pst.close();
			}
			
			
			
			String []arr = null;
			if(strUserTypeId.length()>0){
				arr = strUserTypeId.split(",");
			}
			
			for(int i=0; arr!=null && i<arr.length; i++){
				
				if(!isDelete && !isUpdate && !isView && !isAdd && !strUserType.equalsIgnoreCase(arr[i])){
					sb.append(arr[i]+",");
				}
				
//				if(isDelete || isUpdate || isView || isAdd && !strUserType.equalsIgnoreCase(arr[i])){
//					sb.append(arr[i]+",");
//				}
				
				
			}
			
			if(uF.parseToInt(strUserType)>0 && ArrayUtils.contains(arr, strUserType)<0 && (isDelete || isUpdate || isView || isAdd)){
				sb.append(strUserTypeId+strUserType+",");
			}else if(isDelete || isUpdate || isView || isAdd){
				sb.append(strUserTypeId);
			}
			
			
			
			
			
			pst = con.prepareStatement("update navigation_1 set user_type_id =? where navigation_id=?");			
			pst.setString(1, sb.toString());
			pst.setInt(2, uF.parseToInt(strNavId));
			pst.execute();
            pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return isExist;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
