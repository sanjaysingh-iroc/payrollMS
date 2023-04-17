package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;

public class CheckDate implements IConstants {
	
	public static void main(String[] args){
//		UtilityFunctions uF = new UtilityFunctions();
//		CommonFunctions CF = new CommonFunctions();
		
		/*String currDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		System.out.println("currentDate="+uF.getFutureDate(uF.getDateFormatUtil(currDate, DATE_FORMAT), 5));*/
		/*String s = "1,2,3";
		String[] arr = s.split(",");
		for(int i=0; i<arr.length;i++){
			System.out.println("i=="+arr[i]);
		}*/
		try {
			String strDate = "2023-01-17";
			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date ud = null;
			ud = sFormat.parse(strDate);
			java.sql.Date sqlDate = new java.sql.Date(ud.getTime());
			System.out.println("ud=="+sqlDate);
		} catch (Exception var5) {
            var5.printStackTrace();
        }
		/*Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		datacon db = new datacon();
		con = db.makeConnection(con);
		try{
			pst = con.prepareStatement("select * from projectmntnc");
			rs = pst.executeQuery();
			while (rs.next()) {
				int proId = rs.getInt("pro_id");
				String proOwner = ","+rs.getString("project_owner")+",";
				pst1 = con.prepareStatement("update projectmntnc set project_owners=? where pro_id=?");
				pst1.setString(1, proOwner);
				pst1.setInt(2, proId);
				pst1.executeUpdate();
				pst1.close();
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}*/
		
	}
}
