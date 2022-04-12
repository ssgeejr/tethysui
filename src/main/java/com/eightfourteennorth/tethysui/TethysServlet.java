package com.eightfourteennorth.tethysui;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.text.DecimalFormat;

public class TethysServlet extends HttpServlet {
	private String message;

	public void init() throws ServletException {
      // Do required initialization
      message = "Shamalamadingdong";
	}
		
	public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
		  doPost(request,response);
	  }
	  
	public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		Context ctx = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		response.setContentType("text/html");
		out.println("<a href=\"/tethysui/\">[HOME]</a><br>");
		String alpha = request.getParameter("alpha").trim();
		String beta = request.getParameter("beta").trim();
		beta = (beta.length() < 5)?alpha:beta;
		out.println(alpha + "<br>");
		out.println(beta + "<br>");
		
		//Input Validation
		if(alpha.length() != 5 || beta.length() != 5) {
			out.println("Invalid format, must match MMYY[A|B]<br>");
			return;
		}
			
		PreparedStatement fetchGroupTotals = null;
		int total = 0;
		int totalNew = 0;
		int totalClosed = 0;
		
		int[] totalItems = new int[4];
		int[] newItems = new int[4];
		int[] closedItems = new int[4];
	
		try{
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/scds");
			con = ds.getConnection();
			stmt = con.createStatement();
			
			String queryTotals = "select"
				+"\n riskid as rid,"
				+"\n count(distinct hash) as total"
				+"\n from"
				+"\n scorecard"
				+"\n where"
				+"\n dtkey = ?"
				+"\n group by"
				+"\n riskid"
				+"\n order by"
				+"\n riskid";
				
			fetchGroupTotals = con.prepareStatement(queryTotals);
			fetchGroupTotals.setString(1,alpha);
			rs = fetchGroupTotals.executeQuery();
			out.println("************************************<br>");
			out.println("<b>Issues</b><br>");
			while(rs.next()){            
				total+= rs.getInt("total");
				totalItems[rs.getInt("rid")] = rs.getInt("total");				
            }
			out.println("Total: " + total + "<br>");
			out.println("Critical: " + totalItems[0] + "<br>");
			out.println("High: " + totalItems[1] + "<br>");
			out.println("Medium: " + totalItems[2] + "<br>");
			out.println("Low: " + totalItems[3] + "<br>");
		
			out.println("************************************<br>");

			fetchGroupTotals = con.prepareStatement("select "
				+"\n riskid as rid,"
				+"\n count(distinct hash) as total"
				+"\n from"
				+"\n scorecard"
				+"\n where"
				+"\n dtkey = ?"
				+"\n and riskid = ?"
				+"\n and hash not in ("
				+"\n select"
				+"\n distinct hash"
				+"\n from "
				+"\n scorecard"
				+"\n where"
				+"\n dtkey = ?"
				+"\n and riskid = ?"
				+"\n )"
				+"\n group by"
				+"\n rid");
				
			fetchGroupTotals.setString(1,alpha);
			fetchGroupTotals.setString(3,beta);
			for( int loc = 0; loc<4; loc++){
				fetchGroupTotals.setInt(2,loc);
				fetchGroupTotals.setInt(4,loc);
				rs = fetchGroupTotals.executeQuery();
				if(rs.next()){ 
					newItems[loc] = rs.getInt("total");
					totalNew += rs.getInt("total");
				}
			}
			out.println("************************************<br>");
			out.println("<b>New Issues</b><br>");
			out.println("Total New: " + totalNew + "<br>");
			out.println("Critical New: " + newItems[0] + "<br>");
			out.println("High New: " + newItems[1] + "<br>");
			out.println("Medium New: " + newItems[2] + "<br>");
			out.println("Low New: " + newItems[3] + "<br>");
			out.println("************************************<br>");
			
			
			fetchGroupTotals.setString(3,alpha);
			fetchGroupTotals.setString(1,beta);
			for( int loc = 0; loc<4; loc++){
				fetchGroupTotals.setInt(2,loc);
				fetchGroupTotals.setInt(4,loc);
				rs = fetchGroupTotals.executeQuery();
				if(rs.next()){ 
					closedItems[loc] = rs.getInt("total");
					totalClosed += rs.getInt("total");
				}
			}
			out.println("************************************<br>");
			out.println("<b>Closed Issues</b><br>");
			out.println("Total New: " + totalClosed + "<br>");
			out.println("Critical New: " + closedItems[0] + "<br>");
			out.println("High New: " + closedItems[1] + "<br>");
			out.println("Medium New: " + closedItems[2] + "<br>");
			out.println("Low New: " + closedItems[3] + "<br>");
			out.println("************************************<br>");


			fetchGroupTotals = con.prepareStatement("select "
				+"\n count(distinct hash) as total,"
				+"\n pluginid,"
				+"\n name  "
				+"\n from"
				+"\n scorecard"
				+"\n where"
				+"\n dtkey = ?"
				+"\n and riskid = ?"
				+"\n group by"
				+"\n name, pluginid"
				+"\n order by"
				+"\n total desc");
				
				
				
			String[] riskv = {"Critical", "High", "Medium", "Low"};
			fetchGroupTotals.setString(1,alpha);
			out.println("************************************<br>");
			out.println("************<b>TOP 10 RISKS</b>*************<br>");
			
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
//			System.out.println() );
			
			for(int risks = 0; risks < 4; risks++){
				out.println("************************************<br>");
				out.println("****" + riskv[risks] + "****<br>");
				fetchGroupTotals.setInt(2,risks);
				rs = fetchGroupTotals.executeQuery();
				float denom = totalItems[risks];
				float numer = 0f;
				for(int top = 0; top < 10; top++){
					rs.next();
					numer = rs.getFloat("total");
					out.println(df.format( ((numer/denom)*100))  + "% * " + rs.getInt("total") + " * " + rs.getString("pluginid") + " * " + rs.getString("name") + "<br>");
				}
				out.println("************************************<br>");
			}
			
				
				
/*			
			T
			N
			C
			Critical
			T
			N
			C
			High
			T
			N
			C
			Medium
			T
			N
			C
			Low
			T
			N
			C
*/			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}catch(NamingException e){
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("XXXXX--------------------------------------------XXXXX");
			e.printStackTrace();
			System.out.println("XXXXX--------------------------------------------XXXXX");
		}finally{
			try {
				rs.close();
				stmt.close();
				fetchGroupTotals.close();
				con.close();
				ctx.close();
			} catch (SQLException e) {
				System.out.println("Exception in closing DB resources");
			} catch (NamingException e) {
				System.out.println("Exception in closing Context");
			}
			
		}
			
      // Actual logic goes here.
      
      out.println("<h1>" + message + "</h1>");
   }
	
}