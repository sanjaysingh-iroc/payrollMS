package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillVideoLink implements IStatements {
	
	String videoId;
	String videoName;
	
	public FillVideoLink(String videoId, String videoName) {
		this.videoId = videoId;
		this.videoName = videoName;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	
	HttpServletRequest request;
	public FillVideoLink(HttpServletRequest request) {
		this.request = request;
	}
	public FillVideoLink() {
	}
	
	public List<FillVideoLink> fillVideoLink() {

		List<FillVideoLink> al = new ArrayList<FillVideoLink>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from video_link_details");
			rst= pst.executeQuery();
			while (rst.next()) {
				al.add(new FillVideoLink(rst.getString("video_link_id"), rst.getString("video_name")));
			}	
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;

	}

}
