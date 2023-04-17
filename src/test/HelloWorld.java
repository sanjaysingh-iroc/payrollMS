package test;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import com.opensymphony.xwork2.ActionSupport;


public class HelloWorld  extends ActionSupport implements ServletRequestAware{

	/**
	 * 
	 */
	String query2;
	private static final long serialVersionUID = 1L;

	HttpSession session;

	public HelloWorld() {


	}

	//Variables used for insert
	String passId;
	/* public String getPassId() {
		return passId;
	}

	public void setPassId(String passId) {
		this.passId = passId;
	}*/

	private String menuName;
	private String status;
	private String menuType;
	private int primary_menu;

	private String inter_link;

	private String external_link;

	private int parent;
	private int sub_parent;   
	List menuTypeList;




	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	public String getMenuType() {
		return menuType;
	}

	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	public int getPrimary_menu() {
		return primary_menu;
	}

	public void setPrimary_menu(int primaryMenu) {
		primary_menu = primaryMenu;
	}

	public String getInter_link() {

		return inter_link;
	}

	public void setInter_link(String interLink) {
		inter_link = interLink;
	//	System.out.println("=========inter_link======="+inter_link);
	}

	public String getExternal_link() {
		return external_link;
	}

	public void setExternal_link(String externalLink) {
		external_link = externalLink;
		//System.out.println("=========external_link======="+external_link);
	}


	public void insert(){
		boolean flag=true;
		// menuTypeArr=getList();
		session = request.getSession(true);
		String id=request.getParameter("passId");
		//System.out.println("================"+id);

		try{
			Connection con=null;
			datacon dc= new datacon();
			PreparedStatement ps =null;
			PreparedStatement pst=null;
			con = dc.makeConnection(con);  

			String query ="insert into payroll_navigation(menu_name,parent,sub_parent,enable,expand,menu_type,external_link) values (?,?,?,?,?,?,?)"; 
			pst = con.prepareStatement(query); 



			if(getMenuType().equals("Primary")){
				//System.out.println("primary=====================");
				parent=0;
				sub_parent=0;
				flag=false;

			}
			else if(getMenuType().equals("Sub_Primary")){
				query2 ="UPDATE payroll_navigation SET expand='1' WHERE menu_id = ? ";

				parent=getPrimary_menu();
				sub_parent=0;
			}
			else if(getMenuType().equals("Sub_Child")){
				query2 ="UPDATE payroll_navigation SET sub_expand='1' WHERE menu_id = ? ";
				parent=0;
				sub_parent=getPrimary_menu();
			}

			pst.setString(1,getMenuName());
			pst.setInt(2,parent);
			pst.setInt(3,sub_parent);
			pst.setInt(4,1);
			pst.setInt(5,0);
			pst.setString(6,getMenuType());


			//	pst.setString(7,getExternal_link());

			System.out.println("getExternal_link()"+getExternal_link());
			if(!getExternal_link().equals("")){
				pst.setString(7,getExternal_link());
				//System.out.println("getExternal_link==============blank======");
			}

			if(!getInter_link().equals("-1")){
				pst.setString(7,getInter_link());
				//System.out.println("getInter_link==============blank======");
			}


			if(pst.executeUpdate()>0)
			{
				if(flag)
				{
					PreparedStatement pst1= con.prepareStatement(query2);
					//	System.out.println("The value has been inserted");
					pst1.setInt(1,getPrimary_menu());
					pst1.executeUpdate();
				}
			}

		}catch(SQLException ex){
			ex.printStackTrace();
		}   	   	

	}

	public void display(){    	

		try{
			Connection con=null;
			datacon dc= new datacon();
			PreparedStatement pst = null;
			ResultSet rs = null;
			String query ="select * from payroll_navigation WHERE parent=0 AND sub_parent=0";
			con = dc.makeConnection(con);        	
			pst = con.prepareStatement(query);
			rs = pst.executeQuery();
			session.setAttribute("menuResult", rs);
			session.setAttribute("connection",con);        	


		}catch(SQLException ex){
			ex.printStackTrace();
		}   	   	

	}
	public String execute() {

		session = request.getSession(true);
		String add = request.getParameter("Add");
		//System.out.println("add====================="+add);

		if(add!=null)
			insert();
		else
			display();        

		System.out.println("Hello");
		return "SUCCESS";
	}


/*

	public void validate() {

		System.out.println("getStatus====================="+getStatus());

		if (getMenuName()!=null && getMenuName().length() == 0) {
			addFieldError("menuName", "Menu Name is required");
			// loadLaVidateUser();
		}
		if (getStatus()!=null && getStatus().equalsIgnoreCase("false") ) {
			addFieldError("status", "Status is required");
			//  loadLaVidateUser();
		}
		if (getMenuType()!=null && getMenuType().equalsIgnoreCase("-1")) {
			addFieldError("menuType", "Select Menu Type is required");
			//  loadLaVidateUser();
		} 
		//        if (getPrimary_menu()!=null && getPrimary_menu().equals("0") &&  && !getMenuType().equalsIgnoreCase("-1")) {
		//            addFieldError("primary_menu", "Select Primary Menu is required");
		//           // loadLaVidateUser();
		//        }
		if(passId==null)
		{

		}
		if(passId!=null){
			if ( getInter_link()!=null && getInter_link().length() == 0) {
				addFieldError("inter_link", "New link is required");
			}
		}			
		else{
			if ( getExternal_link()!=null && getExternal_link().length() == 0) {
				addFieldError("external_link", "New link is required");
			}
		}

	}*/
	HttpServletRequest request; 

	public void setServletRequest(HttpServletRequest request) {
		this.request=request;

	}
}