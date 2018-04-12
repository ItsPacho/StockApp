package notDefaultPackage;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

public class StockWithDate implements Comparable<StockWithDate>{
	
	private Date stockDate;
	private JSONObject stockData;
	
	public StockWithDate(int year, int month, int day, int hour, int minute, int second, JSONObject stockData) throws ParseException{
		
		String hourString = hour + "";
		String minuteString = minute + "";
		String secondString = second + "";
		if (hour == 0 && minute == 0 && second == 0) {
			hourString = "00";
			minuteString = "00";
			secondString = "00";
		}
		
		String DateString = month + "/" + day + "/" + year + "/" + hourString + "/" + minuteString + "/" + secondString;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy/HH/mm/ss"); 
		this.stockDate = df.parse(DateString);
		this.stockData = stockData;
	}
	
	public Date getDate() {
		return this.stockDate;
	}
	
	public JSONObject getData(){
		return this.stockData;
	}

	@Override
	public int compareTo(StockWithDate o) {
		return getDate().compareTo(o.getDate());
	}
}
