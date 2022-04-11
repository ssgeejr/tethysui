package com.eightfourteennorth.tethysui;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class TethysServlet extends HttpServlet {
	private String message;

	public void init() throws ServletException {
      // Do required initialization
      message = "Shamalamadingdong";
	}
	
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		Context ctx = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		response.setContentType("text/html");
		try{
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/scds");
			con = ds.getConnection();
			stmt = con.createStatement();
			
			rs = stmt.executeQuery("select distinct host as dh, pluginid, name from scorecard limit 10");
			while(rs.next()){            
				out.println(rs.getString("dh") + " * " + rs.getString("pluginid") + " * " + rs.getString("name") + "<br>");
            }
		}catch(NamingException e){
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				stmt.close();
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