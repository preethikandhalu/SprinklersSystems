package project.db.pkg;

import java.sql.*;
import java.text.*;
import java.util.*;

import project.backend.pkg.*;

/**
 * @author shilpita_roy
 * SELECT SPRINKLERID , WATERCONSUMPTION FROM SPRINKLER_GROUP WHERE FUNCTIONALSTATUS ='true' AND LOCATION = 'North';
 * 
 */

public class QueryDB {
	private ResultSet rs;
	private ArrayList<String> sprinklerList; 
	private DayAndTime dateTime;
	//default Constructor
    public QueryDB(){
    	this.sprinklerList = new ArrayList<String>();
    	this.dateTime = new DayAndTime();
    }
  
    
    /**
     * Query to get All sprinkler in a group.
     * @param con
     * @param group
     * @return HashMap of 
     */
    public ArrayList<String> getAllSprinklers(Connection con,String group){
			try {
							Statement stmt 	= con.createStatement();
							rs				= stmt.executeQuery("SELECT SPRINKLERID "   
																+ "FROM SPRINKLER_GROUP "
																+ "WHERE FUNCTIONALSTATUS ='true' AND LOCATION = '"+group+"'");
							while ( rs.next() ) {
								sprinklerList.add(rs.getString("SPRINKLERID"));    
				            }
			    } catch ( SQLException e) {
			    			e.printStackTrace();
			    } 
			
		return sprinklerList;
    }
    
    /**
     * Query to get active schedule for current date and time
     * @param con
     * @return List of sprinkler to be activated
     */
    public ArrayList<Sprinkler> getActiveScheduleSprinklerGroup(Connection con ){ 
    	String todayDate = dateTime.getFormattedDate().toUpperCase() ;
    	System.out.println(todayDate);
    	ArrayList<Sprinkler> activeSprinklerList = new ArrayList<Sprinkler>();
    	try{
    			Statement stmt 	= con.createStatement();
    			rs				= stmt.executeQuery("SELECT SPRINKLERID,LOCATION,STARTTIME,ENDTIME,WATERFLOW  FROM SPRINKLER_SCHEDULE" 
    												+" WHERE "  
    												+"SCHEDULESTARTDATE <= '"+ todayDate
    												+"' AND SCHEDULEENDDATE >= '"+ todayDate +"'");
    			while(rs.next()){
    				boolean flag = dateTime.checkToStartSprinkler(rs.getString("STARTTIME"),rs.getString("ENDTIME"));
    				if(!activeSprinklerList.contains(rs.getString("SPRINKLERID"))){
    					activeSprinklerList.add(new Sprinkler(rs.getString("SPRINKLERID"), rs.getString("LOCATION")
    														  , true, flag, rs.getString("WATERFLOW")));
    				}		
    			}
    	} catch ( SQLException | ParseException e) {
			e.printStackTrace();
    	} 
    	return activeSprinklerList;
    }
    
    /**
     * Query the water consumed per month by each group.
     * @param con
     * @return List of water consumption per month for each group
     */

    public ArrayList<WaterConsumptionLog> getMonthlyWaterConsump(Connection con){
    	ArrayList<WaterConsumptionLog> waterLogList = new ArrayList<WaterConsumptionLog>();
    	try{
    			Statement stmt 	= con.createStatement();
    			rs				= stmt.executeQuery("SELECT SCHEDULEMONTH , LOCATION, SUM(TOTALWATERCONSUMPTION) AS TOTALWATERPERMONTH"
    												+" FROM SPRINKLER_SCHEDULE"
    												+" GROUP BY SCHEDULEMONTH,LOCATION"
    												+" ORDER BY LOCATION");
    			while(rs.next()){
    					waterLogList.add(new WaterConsumptionLog(rs.getString("LOCATION")
    															,rs.getString("SCHEDULEMONTH")
    															,Long.parseLong(rs.getString("TOTALWATERPERMONTH"))));	
    			}
    	} catch ( SQLException e) {
			e.printStackTrace();
    	} 
    	return waterLogList;
    }
    
    /**
     * Query the water consumed for given month by each group.
     * @param con
     * @param month
     * @return List of water consumption per month for each group
     */
    
    public ArrayList<WaterConsumptionLog> getWaterConsumpByGroup(Connection con ,String month){
    	ArrayList<WaterConsumptionLog> waterLogList = new ArrayList<WaterConsumptionLog>();
    	try{
    			Statement stmt 	= con.createStatement();
    			rs				= stmt.executeQuery("SELECT LOCATION, SUM(TOTALWATERCONSUMPTION) AS TOTALWATERPERMONTH"
    												+" FROM SPRINKLER_SCHEDULE WHERE SCHEDULEMONTH = '"+ month+"'"
    												+" GROUP BY LOCATION"
    												+" ORDER BY LOCATION");
    			while(rs.next()){
    					waterLogList.add(new WaterConsumptionLog(rs.getString("LOCATION")
    															,month   //rs.getString("SCHEDULEMONTH")
    															,Long.parseLong(rs.getString("TOTALWATERPERMONTH"))));	
    			}
    	} catch ( SQLException e) {
			e.printStackTrace();
    	} 
    	return waterLogList;
    }
    

}