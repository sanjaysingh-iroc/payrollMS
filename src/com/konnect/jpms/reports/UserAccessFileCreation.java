package com.konnect.jpms.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

public class UserAccessFileCreation implements ServletRequestAware {
	String value;
	String flag;
	String machineSerial;

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	HttpServletRequest request;

	public String execute() {
		if(flag!=null)
			fileCreation();
		else{
			enrollFileCreation();
		}
		return "success";
	}
	
	public void enrollFileCreation() {
		System.out.println("enrollFileCreation==");

//		UtilityFunctions uF = new UtilityFunctions();
		String filePath = null;
//		
		if(System.getProperty("os.name").equals("Linux")){
			filePath = "/home/konnect/biometric/";
		}else{
			filePath = "E:/Dailyhrz\\biometric/";
		}
//		String uploadDir = ServletActionContext.getServletContext().getRealPath("/biometric") + "/";

		
		File file = null;

		try {

			file = new File(filePath, "EnrollNew("+value+").txt");
			
			
			if (!file.exists()) {
				file.createNewFile();
			}

			request.setAttribute(
					"STATUS_MSG",
					"<img src=\"images1/icons/exclamation_mark_icon.png\" width=\"20\" height=\"20\"  />");


		} catch (Exception e) {
			request.setAttribute(
					"STATUS_MSG",
					"<a href=\"javascript:void(0)\" onclick=\"changeStatus('"+value+"')\"><img src=\"images1/icons/hd_cross_20x20.png\" width=\"20\" height=\"20\"  /></a>");

			e.printStackTrace();
		} finally {
		}
	}


	public String getMachineSerial() {
		return machineSerial;
	}

	public void setMachineSerial(String machineSerial) {
		this.machineSerial = machineSerial;
	}

	public void fileCreation() {
		System.out.println("fileCreation==");

		String[] arrValue = value.split("_");
		UtilityFunctions uF = new UtilityFunctions();
		FileOutputStream fop = null;
		
		String filePath = null;
//		
		
		if(System.getProperty("os.name").equals("Linux")){
			filePath = "/home/konnect/biometric/"+arrValue[0];
		}else{
			filePath = "E:/Dailyhrz\\biometric/"+arrValue[0];
		}
		
		
//		if(System.getProperty("os.name").equals("Linux")){
//			filePath = "/home/konnect/Desktop/"+arrValue[0]+ "/";
//		}else{
//			filePath = "D:\\"+arrValue[0]+ "/";
//		}
		
		
		
		
//		String uploadDir = ServletActionContext.getServletContext().getRealPath("/biometric") +"/"+  arrValue[0] +"/";
//		System.out.println("uploadDir=="+uploadDir);
		
		
// write the file to the file specified
		File dirPath = new File(filePath);

		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}


		
		
//		File dirPath = new File(filePath);

//		if (!dirPath.exists()) {
//			dirPath.mkdirs();
//		}
		
		File file = null;
		String content = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			con = db.makeConnection(con);
			pst = con
					.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where eod.emp_id=epd.emp_per_id and emp_per_id=?");
			pst.setInt(1, uF.parseToInt(arrValue[1]));
			rs = pst.executeQuery();
			String name = null;
			String code=null;
			while (rs.next()) {
				name = rs.getString("emp_fname") + " "
						+ rs.getString("emp_lname");
				code=rs.getString("biometrix_id");
			}
			rs.close();
			pst.close();

			if (uF.parseToBoolean(flag)) {
				content = "Enroll\n" + code +"\n" + name
						+ "\n192.168.1.201\nN";
			} else {
				content = "Delete\n" + code + "\n" + name
						+ "\n192.168.1.201\nY";
			}
			
		   

			file = new File(filePath, "Read"+code+".txt");
			
			
			if (!file.exists()) {
				file.createNewFile();
			}

			fop = new FileOutputStream(file);

			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			
			fop.flush();
			fop.close();
			request.setAttribute(
					"STATUS_MSG",
					"<span style=\"color: green; font-size: 10px; float: right; width: 200px;\">File Created successfully!!!</span>");

			pst = con
					.prepareStatement("select * from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(arrValue[1]));
			rs = pst.executeQuery();
			while (rs.next()) {
				name = rs.getString("biometrix_access");
			}
			rs.close();
			pst.close();

			if (name == null) {

				name = arrValue[0] + "=";
				if (uF.parseToBoolean(flag)) {
					name += "T,";
				} else {
					name += "F,";
				}
			} else {

				StringBuilder sb = new StringBuilder();
				String[] aa = name.split(",");
				boolean f1 = false;
				for (String a : aa) {

					boolean f = false;
					String[] bb = a.split("=");
					if (uF.parseToInt(bb[0]) == uF.parseToInt(arrValue[0])) {
						f = true;
						f1 = true;
					}

					if (f) {
						sb.append(bb[0] + "=");
						if (uF.parseToBoolean(flag)) {
							sb.append("T,");
						} else {
							sb.append("F,");
						}

					} else {
						sb.append(a + ",");
					}

				}

				if (!f1) {

					sb.append(arrValue[0] + "=");
					if (uF.parseToBoolean(flag)) {
						sb.append("T,");
					} else {
						sb.append("F,");
					}

				}

				name = sb.toString();

			}

			pst = con
					.prepareStatement("update employee_official_details set biometrix_access=? where emp_id=?");
			pst.setString(1, name);
			pst.setInt(2, uF.parseToInt(arrValue[1]));
			pst.executeUpdate();
			pst.close();

		} catch (Exception e) {
			request.setAttribute(
					"STATUS_MSG",
					"<span style=\"color: green; font-size: 10px; float: right; width: 200px;\">Unable to create the file. Please create it manually.</span>");

			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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
