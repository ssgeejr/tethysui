package com.eightfourteennorth.tethysui;


import java.sql.*;
import java.text.DecimalFormat;

public class DataValidation{
	private Connection conn = null;
	
	public DataValidation(){
		try{
			PreparedStatement fetchGroupTotals = null;
			Statement stmt = null;
			
			String alpha = "0822B";
			String beta = "0722B";
			System.out.println("ALPHA [" + alpha + "]");
			System.out.println("BETA [" + beta + "]");	
			
			int total = 0;
			int totalNew = 0;
			int totalClosed = 0;
			
			int[] totalItems = new int[4];
			int[] newItems = new int[4];
			int[] closedItems = new int[4];			


			conn = DriverManager.getConnection("jdbc:mysql://tethys:3306/scorecard","scorecard","scorecard");  
			System.out.println("Is Connected: " + !conn.isClosed());
			stmt = conn.createStatement();
		
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
				
			fetchGroupTotals = conn.prepareStatement(queryTotals);
			fetchGroupTotals.setString(1,alpha);
			ResultSet rs = fetchGroupTotals.executeQuery();
			System.out.println("************************************");
			System.out.println("Issues");
			while(rs.next()){            
				total+= rs.getInt("total");
				totalItems[rs.getInt("rid")] = rs.getInt("total");				
            }
			
			System.out.println("************************************");

			fetchGroupTotals = conn.prepareStatement("select "
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

			System.out.println("****************-TOTALS-****************");
			System.out.println("Total: " + total + "");
			System.out.println("Total New: " + totalNew + "");
			System.out.println("Total Closed: " + totalClosed + "");
			System.out.println("************************************");
			
			System.out.println("****************-CRITICAL-****************");
			System.out.println("Critical: " + totalItems[0] + "");
			System.out.println("Critical New: " + newItems[0] + "");
			System.out.println("Critical Closed: " + closedItems[0] + "");
					
			System.out.println("****************-HIGH-****************");
			System.out.println("High: " + totalItems[1] + "");
			System.out.println("High New: " + newItems[1] + "");
			System.out.println("High Closed: " + closedItems[1] + "");
			
			System.out.println("****************-MEDIUM-****************");
			System.out.println("Medium: " + totalItems[2] + "");
			System.out.println("Medium New: " + newItems[2] + "");
			System.out.println("Medium Closed: " + closedItems[2] + "");
						
			System.out.println("****************-LOW-****************");
			System.out.println("Low: " + totalItems[3] + "");
			System.out.println("Low New: " + newItems[3] + "");
			System.out.println("Low Closed: " + closedItems[3] + "");
						
			System.out.println("************************************");
			System.out.println("************************************");
		
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{conn.close();}catch(Exception ce){}
		}
		
	}
	
	
	public static void main(String args[]){ new DataValidation();}

}