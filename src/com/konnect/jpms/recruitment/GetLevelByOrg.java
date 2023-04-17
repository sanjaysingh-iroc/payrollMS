package com.konnect.jpms.recruitment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLevelByOrg extends ActionSupport implements ServletRequestAware{

	String strOrg;
	List<FillLevel> levelslist;

	private static final long serialVersionUID = 1L;

	String fromPage;
	  
	public String execute() {
		UtilityFunctions uF=new UtilityFunctions();
		levelslist = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		return SUCCESS;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillLevel> getLevelslist() {
		return levelslist;
	}

	public void setLevelslist(List<FillLevel> levelslist) {
		this.levelslist = levelslist;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }

}
