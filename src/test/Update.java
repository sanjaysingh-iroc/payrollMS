package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;


import com.opensymphony.xwork2.ActionSupport;






@SuppressWarnings("serial")
public class Update extends ActionSupport implements ServletRequestAware 
{
	HttpSession session;
	
	//String  str ="&amp;left-to-right[0][id]=117&amp;left-to-right[0][children][0][id]=127&amp;left-to-right[0][children][0][children][0][id]=128";
	     
	ResultSet rs=null;
	private String parent;
	int i=-1;
	int j=0;
	int k=0,t=0;
	String val;
	static String val1;

	public String getAbcd() {
		
		return val;
	}




	public void setAbcd(String val) {
		
		val1=val;
		this.val = val;
	}

	private String[][][] array1= new String[30][30][30];
	String  str;
	public void UpdateFun() {
		
		String  str =getAbcd();
		//String  str="&amp;left-to-right[0][id]=137&amp;left-to-right[0][children][0][id]=138&amp;left-to-right[0][children][0][children][0][id]=139&amp;left-to-right[1][id]=117&amp;left-to-right[2][id]=132";
		System.out.println("request.getParameter"+str);
		/*	if(str!=null)
		{
			System.out.println(str);
		}else{
			System.out.println("str null");
		}*/
		
		String sub[]=str.split("&amp;left-to-right");
		
		int a = sub.length,b=0;
		System.out.println(sub[0]+sub[1]+"a"+a);
		while(b<a)
		{
			//System.out.println(sub[b]);
			if(!sub[b].equals(""))
			{
				String sub1[]=sub[b].split("=");
				
				if( (sub1[0].indexOf("children")>0) )//children
				{
					if(sub1[0].indexOf("children") != sub1[0].lastIndexOf("children" ) ) //children
					{	
						k++;
						if(!sub[b].equals("") &&  sub[b]!=null){
						array1[i][j][k]=sub1[1];
						System.out.println("array1["+i+"]["+j+"]["+k+"]"+"sub"+array1[i][j][k]);
						}
						//children=$sub1[1];
						//echo "Sub children=".$children."<br />";
	
					}
					else //subMenu
					{
						
						j++;
						k=0;
						if(!sub[b].equals("") &&  sub[b]!=null){
						array1[i][j][k]=sub1[1];
						System.out.println("array1["+i+"]["+j+"]["+k+"]"+"sub"+array1[i][j][k]);
						}
					}
				}
				else
				{
					i++;
					j=0;
					k=0;
					
					if(!sub[b].equals("") &&  sub[b]!=null){
					array1[i][j][k]=sub1[1];//121
					System.out.println("array1["+i+"]["+j+"]["+k+"]"+"main"+array1[i][j][k]);
					}
				}
			}
			b++;
		}//while
				
		insertUpdate();
	}




	String insertUpdate()
	{
		
		
		Connection con = null;
		PreparedStatement pst = null;
		datacon db = new datacon();
		con = db.makeConnection(con);

		int a=array1.length;
		System.out.println("length"+a);
		try{
			for(int m=0;m<a;m++)
			{	
				int n=0;
				int k=0;
				int weight=m;
				String  parent1=array1[m][n][k];
				
				System.out.println("array1["+m+"]["+n+"]["+k+"]"+"sub"+array1[m][n][k]);
				System.out.println("in parent");

				String qry="update payroll_navigation set weight='"+weight+"',parent='0',sub_parent=0,expand=0,menu_type='Primary',sub_expand='0' where menu_id='"+parent1+"'";
				pst = con.prepareStatement(qry);
				pst.executeUpdate();//mysql_query(qry);


			while(n<20)
				{ 


				if(array1[m][n+1][k]!=null)
					{	
					n++;
					while(array1[m][n][k]!=null)
					{
						System.out.println("in sub");
						System.out.println("while array1["+m+"]["+(n)+"]["+k+"]"+"main"+array1[m][n][k]);
						k=0;
						String children1=array1[m][n][k];
						
						int weight1=n-1;
						pst = con.prepareStatement("update payroll_navigation set expand='1' where menu_id='"+parent1+"'");
						pst.executeUpdate();//mysql_query(qry);

						String qry1="update payroll_navigation set menu_type='Sub_Primary', sub_expand='0', sub_parent='0' ,weight='"+weight1+"',parent='"+parent1+"' where menu_id='"+children1+"'";
						pst = con.prepareStatement(qry1);
						pst.executeUpdate();

						if(array1[m][n][k+1]!=null)
						{
							k++;

							while(array1[m][n][k]!=null)
							{

								System.out.println("in child");
								String children2=array1[m][n][k];
								System.out.println(" children2 array1["+m+"]["+(n)+"]["+k+"]"+"main"+array1[m][n][k]);
								int weight2=k-1;

								pst = con.prepareStatement("update payroll_navigation set sub_expand='1' where menu_id='"+children1+"'");
								pst.executeUpdate();//mysql_query(qry);
								System.out.println("in children1********");
								String qry2="update payroll_navigation set menu_type='Sub_Child', weight='"+weight2+"',parent=0,sub_parent='"+children1+"' where menu_id='"+children2+"'"; 
								pst = con.prepareStatement(qry2);
								pst.executeUpdate();
								System.out.println("update child");
								k++;
							}

						}
						n++;	
					}
						/*}
						else
						{
							//System.out.println("children1 else");
							//break;
						}*/
				}
				else
				{
					System.out.println("else");
					break;
				}

				}

			}
		}catch (SQLException e) {

		}

		return "SUCCESS";
	}
	/* public void display(){   	

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

	}*/

	public String execute() {
		//display();
		UpdateFun();
		System.out.println("execute****************");
		  
		if(str!=null){
			
			System.out.println("str!=null****************");
			return "SUCCESS";
		}
		else
			return ERROR;
	}
	
	HttpServletRequest request; 

	public void setServletRequest(HttpServletRequest request) {
		this.request=request;

	}
	public static void main(String[] args) {


		Update up= new Update();
		up.UpdateFun();
	}

}
