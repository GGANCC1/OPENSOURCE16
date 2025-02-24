package application;

import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.TreeSet;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;




public class Data_base {
	ResultSet rs;
	Connection con = null;
	Main M;
	
	
	LinkedList<Entry<String>> Private_Schedule_list = new LinkedList<Entry<String>>();
	LinkedList<Entry<String>> University_Schedule_list = new LinkedList<Entry<String>>();
	LinkedList<Entry<String>> BlackBord_Schedule_list = new LinkedList<Entry<String>>();
	LinkedList<Entry<String>> BlackBord_Movie_Schedule_list = new LinkedList<Entry<String>>();
	LinkedList<Entry<String>> BlackBord_Movie_Finished_list = new LinkedList<Entry<String>>();
	Statement stat;
	SeleniumCrawl selCrawl;
	
	public Data_base() {
		
		selCrawl = new SeleniumCrawl();
		
		// TODO Auto-generated constructor stub
		try {
			Class.forName("org.sqlite.JDBC");
			String dbFile = "lib\\db\\test_db.db"; 
			try {
					con = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
					stat = con.createStatement();
					
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		
	}
	
	public void reload_schedule(Calendar cal, String query_string)
	{
	
				try {
					rs = stat.executeQuery("SELECT * from "+query_string );
				
				System.out.println("SELECT * from " + query_string);
				
				while(rs.next()) 
				{ 
					String Title = rs.getString("Title"); 
					String StartDate = rs.getString("StartDate"); 
					String EndDate = rs.getString("EndDate"); 
					String StartTime = rs.getString("StartTime"); 
					String EndTime = rs.getString("EndTime"); 
					int FullDay = rs.getInt("FullDay"); 
					System.out.println(Title + " " + StartDate+ " " + EndDate+ " " + StartTime+ " " +EndTime); 
					System.out.println(StartTime);
					
					
					Date date=Date.valueOf(StartDate);
					
					
		
					
					

			        //스케줄 등록 방법, 데이터베이스에서 가져온 스케줄을 등록 할 때(프로그램 실행 시)
			        Entry<String> entry = new Entry<>(Title);
			       // entry.setInterval(LocalDate.now());
			        if(FullDay == 1)
			        entry.setFullDay(true);
			        else
			        entry.setFullDay(false);
			        entry.changeStartDate(LocalDate.parse(StartDate, DateTimeFormatter.ISO_DATE));
			        entry.changeEndDate(LocalDate.parse(EndDate, DateTimeFormatter.ISO_DATE));
			        entry.changeStartTime(LocalTime.parse(StartTime, DateTimeFormatter.ISO_TIME));
			        entry.changeEndTime(LocalTime.parse(EndTime, DateTimeFormatter.ISO_TIME));
			        cal.addEntry(entry);
			     
					
				
				}
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				

				
				
	
		

		
	}
	
	
	
	public void delete_entry(LinkedList<Entry<String>> my_list, String delete_id) 
	{
		
		for(int i=0; i<my_list.size(); i++)
		{
			
			if(my_list.get(i).getId() == delete_id)
			{
				System.out.println("리스트 안의 아이디값:" + my_list.get(i).getId() + "지울 아이디 값" +delete_id );
				my_list.remove(i);
				break;
			}
				
			
		}
		
	return;		
		
	}
	
	public void save_calendar()
	{
		try {
			stat.executeUpdate("DELETE FROM Private_Schedule;");
			stat.executeUpdate("DELETE FROM University_Schedule;");
			stat.executeUpdate("DELETE FROM BlackBord_Schedule;");
			stat.executeUpdate("DELETE FROM BlackBord_Movie_Schedule;");
			stat.executeUpdate("DELETE FROM BlackBord_Finished_Schedule;");
			
			save_calendar_method(Private_Schedule_list, "Private_Schedule");
			save_calendar_method(University_Schedule_list, "University_Schedule");
			save_calendar_method(BlackBord_Schedule_list, "BlackBord_Schedule");
			save_calendar_method(BlackBord_Movie_Schedule_list, "BlackBord_Movie_Schedule");
			save_calendar_method(BlackBord_Movie_Finished_list, "BlackBord_Finished_Schedule");
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	public void save_calendar_method(LinkedList<Entry<String>> my_entry_list, String type)
	{
		
					
					try {
						
					
					
					for(int i=0; i<my_entry_list.size(); i++)
					{
							
							
							
						
							if( my_entry_list.get(i).isFullDay())
							{
								String s1 = "INSERT INTO "; 
								String s2 =  type + "(Title,Fullday,StartDate,EndDate,StartTime,EndTime) VALUES";
								String s3 = "("+ "\"" + my_entry_list.get(i).getTitle()+ "\"" +"," +"1"+","+ "\""+ my_entry_list.get(i).getStartDate() + "\""+ "," + "\""+ my_entry_list.get(i).getEndDate() + "\"" + "," 
								+  "\""+ my_entry_list.get(i).getStartTime()+ "\""+","+ "\""+ my_entry_list.get(i).getEndTime()+ "\"" +");";
							
								stat.executeUpdate(s1+s2+s3);
							}
							else
							{
								String s1 = "INSERT INTO "; 
								String s2 =  type + "(Title,Fullday,StartDate,EndDate,StartTime,EndTime) VALUES";
								String s3 = "("+ "\"" + my_entry_list.get(i).getTitle()+ "\"" +"," +"0"+","+ "\""+ my_entry_list.get(i).getStartDate() + "\""+ "," + "\""+ my_entry_list.get(i).getEndDate() + "\"" + "," 
								+  "\""+ my_entry_list.get(i).getStartTime()+ "\""+","+ "\""+ my_entry_list.get(i).getEndTime()+ "\"" +");";
								
							
								stat.executeUpdate(s1+s2+s3);
								
								
							}
						
					}
					
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
			
				
	
	

		
		
		
	return;
	}
	
	
	public void sync_selenium()
	{
		selCrawl.crawl();
		
		
		for(int i=0; i<selCrawl.lessonList.size(); i++)
		{
			 
			
			 //스케줄 등록 방법, 데이터베이스에서 가져온 스케줄을 등록 할 때(프로그램 실행 시)
	        Entry<String> entry = new Entry<>(
	        		selCrawl.lessonList.get(i).getSubject() +" " +
	        		selCrawl.lessonList.get(i).getWeek() + " " +
	        		selCrawl.lessonList.get(i).getTitle()
			);
	        
	      
	       // entry.setInterval(LocalDate.now());
	        entry.changeStartDate(LocalDate.parse(selCrawl.lessonList.get(i).getStartDate(), DateTimeFormatter.ISO_DATE));
	        entry.changeEndDate(LocalDate.parse(selCrawl.lessonList.get(i).getDeadlineDate(), DateTimeFormatter.ISO_DATE));
	        entry.changeStartTime( LocalTime.parse(selCrawl.lessonList.get(i).getStartTime(), DateTimeFormatter.ISO_TIME));
	        entry.changeEndTime( LocalTime.parse(selCrawl.lessonList.get(i).getDeadlineTime(), DateTimeFormatter.ISO_TIME));
	        entry.setFullDay(true);
	        
	        if( selCrawl.lessonList.get(i).getPass() == true)
	        {
	        	M.BlackBord_Movie_Finished.addEntry(entry);
	        }
	        else
	        {
	        	M.BlackBord_Movie_Schedule.addEntry(entry);
	        }
	        
	      
			
		}
		
		
		
		for(int i=0; i<selCrawl.assignmentList.size(); i++)
		{
			 
			
			 //스케줄 등록 방법, 데이터베이스에서 가져온 스케줄을 등록 할 때(프로그램 실행 시)
	        Entry<String> entry = new Entry<>(
	        		selCrawl.assignmentList.get(i).getSubject() +" " +
	    	        selCrawl.assignmentList.get(i).getName()     
			);
	 
	        entry.changeStartDate(LocalDate.now());
	        entry.changeStartTime( LocalTime.now());
	     
	        String enddate = selCrawl.assignmentList.get(i).getDate(); 
	        enddate = enddate.replace('년', '-');
	        enddate = enddate.replace('월', '-');
	        enddate = enddate.replace("일", "");
		    String []str2 = enddate.split("-");
		    LocalDate ld = LocalDate.of(Integer.parseInt(str2[0]),Integer.parseInt(str2[1]), Integer.parseInt(str2[2]));
	        entry.changeEndDate(ld);
	        entry.changeEndTime( LocalTime.parse(selCrawl.assignmentList.get(i).getTime(), DateTimeFormatter.ISO_TIME));
	        entry.setFullDay(true);
	        M.BlackBord_Schedule.addEntry(entry);
	       
			
		}
		
		
		for(int i=0; i<selCrawl.academicList.size(); i++)
		{
			 
			
			 
	        Entry<String> entry = new Entry<>(
	        		selCrawl.academicList.get(i).getEvent()  
			);
	        
	        String start_date = selCrawl.academicList.get(i).getStartYear() +"-" + 
	        				   selCrawl.academicList.get(i).getStartMonth() +"-" + 
	        				   selCrawl.academicList.get(i).getStartDay();
	        
	        String end_date =   selCrawl.academicList.get(i).getEndYear() +"-" + 
 				   			   selCrawl.academicList.get(i).getEndMonth() +"-" + 
 				   			   selCrawl.academicList.get(i).getEndDay(); 
	       
	        String []str2 = start_date.split("-");
		    LocalDate startdat = LocalDate.of(Integer.parseInt(str2[0]),Integer.parseInt(str2[1]), Integer.parseInt(str2[2]));
		    
		    String []str3 = end_date.split("-");
		    LocalDate enddat = LocalDate.of(Integer.parseInt(str3[0]),Integer.parseInt(str3[1]), Integer.parseInt(str3[2]));
	      
	        entry.setFullDay(true);
	        entry.changeStartDate(startdat);
	        entry.changeEndDate(enddat);
	      
	        M.University_Schedule.addEntry(entry);
	       
			
		}
		
	
		
	}
	public void reset_selenium() {
		try {
			stat.executeUpdate("DELETE FROM University_Schedule;");
			stat.executeUpdate("DELETE FROM BlackBord_Schedule;");
			stat.executeUpdate("DELETE FROM BlackBord_Movie_Schedule;");
			stat.executeUpdate("DELETE FROM BlackBord_Finished_Schedule;");
			
			M.BlackBord_Movie_Schedule.clear();
			M.BlackBord_Schedule.clear();
			M.University_Schedule.clear();
			M.BlackBord_Movie_Finished.clear();
			
			University_Schedule_list.clear();
			BlackBord_Schedule_list.clear();
			BlackBord_Movie_Schedule_list.clear();
			BlackBord_Movie_Finished_list.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}		
}

