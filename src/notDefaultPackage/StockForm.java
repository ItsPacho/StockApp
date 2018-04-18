package notDefaultPackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONTokener;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.json.JSONObject;

// JFREECHART
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

// INI reader
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class StockForm {
 
    // UI imports
    private JComboBox dataComboBox;
    private JComboBox timeSeriesComboBox;
    private JComboBox symbolComboBox;
    private JComboBox symbolComboBox2;
    private JComboBox timeIntervalComboBox;
    private JComboBox outputComboBox;
    private JButton doQueryButton;
    private JButton PearsonButton;
    JPanel StockPanel;
    private JTextArea textArea;
    private JScrollPane textAreaScroll;
    private JTextField textField;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    
    
    public boolean disabled = false;
    public Color lightGray = Color.lightGray;
    public Color black = Color.black;
    
    
    // DATA TO POPULATE FIELDS
    String apiKeyxx = "";
    ArrayList<String> timeSeriesxx = new ArrayList<String>();
    ArrayList<String> symbol1xx = new ArrayList<String>();
    ArrayList<String> timeIntervalxx = new ArrayList<String>();
    ArrayList<String> outputSizexx = new ArrayList<String>();
    ArrayList<String> symbol2xx = new ArrayList<String>();
    
    // START AND STOP DATE
    String startDatexx = "";
    String stopDatexx = "";
    
    // ArrayLists FOR DOUBLE SYMBOLS
    ArrayList<String> symbolListValues1 = new ArrayList<String>();
    ArrayList<String> symbolListValues2 = new ArrayList<String>();
    
    // IF BOTH SYMBOLS CHOSEN
    boolean bothSymbols = false;
    
    public double calculatePearson(ArrayList<String> vals1, ArrayList<String> vals2) {
    	double[] symbolListArray1 = new double[vals1.size()];
    	double[] symbolListArray2 = new double[vals2.size()];
    	
    	for (int i = 0; i < vals1.size(); i++) {
    		double valel1 = Double.parseDouble(vals1.get(i));
    		double valel2 = Double.parseDouble(vals2.get(i));
    		
    		symbolListArray1[i] = valel1;
    		symbolListArray2[i] = valel2;
    	}
    	
    	// CALCULATING SYMBOL1 MEAN
    	double val1sum = 0;
    	for (int i = 0; i < symbolListArray1.length; i++) {
    		val1sum = val1sum + symbolListArray1[i];
    	}
    	double val1mean = val1sum / symbolListArray1.length;
    	//////////////////////////////////////////////////////////
    	System.out.println("val1mean: " + val1mean);
    	
    	// CALCULATING SYMBOL2 MEAN
    	double val2sum = 0;
    	for (int i = 0; i < symbolListArray2.length; i++) {
    		val2sum = val2sum + symbolListArray2[i];
    	}
    	double val2mean = val2sum / symbolListArray2.length;
    	///////////////////////////////////////////////////////////
    	System.out.println("val12mean: " + val2mean);
    	
    	// CALCULATING COVARSUM
    	double covarSum = 0;
    	for (int i = 0; i < symbolListArray1.length; i++) {
    		
    		double covarel1 = symbolListArray1[i] - val1mean;
    		double covarel2 = symbolListArray2[i] - val2mean;
    		
    		double covarProd = covarel1 * covarel2;
    		covarSum = covarSum + covarProd;
    		
    	}
    	////////////////////////////////////////////////////////////
    	System.out.println("covarSum: " + covarSum);
    	
    	// CALCULATING COVARIANCE
    	double covariance = covarSum / (symbolListArray1.length - 1);
    	////////////////////////////////////////////////////////////
    	System.out.println("covariance: " + covariance );
    	
    	// CALCULATING STANDARD DEVIATION OF SYMBOL 1
    	double stdSum1 = 0;
    	for (int i = 0; i < symbolListArray1.length; i++) {
    		
    		double stdel1 = Math.pow((symbolListArray1[i] - val1mean), 2);
    		stdSum1 = stdSum1 + stdel1;
    		
    	}
    	double stdDev1 = Math.sqrt(stdSum1 / (symbolListArray1.length));
    	///////////////////////////////////////////////////////////////
    	System.out.println("stdDev1: " + stdDev1);
    	
    	// CALCULATING STANDARD DEVIATION OF SYMBOL 2
    	double stdSum2 = 0;
    	for (int i = 0; i < symbolListArray2.length; i++) {
    		
    		double stdel2 = Math.pow((symbolListArray2[i] - val2mean), 2);
    		stdSum2 = stdSum2 + stdel2;
    		
    	}
    	double stdDev2 = Math.sqrt(stdSum2 / (symbolListArray2.length));
    	/////////////////////////////////////////////////////////////////
    	System.out.println("stdDev2: " + stdDev2);
    	
    	
    	/////////////////////////////////////////////////////////////////
    	System.out.println("PEARSONS: " + covariance / (stdDev1 * stdDev2));
    	return covariance / (stdDev1 * stdDev2);
    	
    	
    }
    
    
    
    public void getDataFromIni() throws InvalidFileFormatException, IOException {
    	
    	Wini ini = new Wini(new File("iniFiles/StockAnalyzer.ini"));
    	
    	// GET API KEY
        apiKeyxx = ini.get("MY_DATA_HERE", "API_KEY", String.class);
        apiKeyxx = apiKeyxx.trim();
        
        // GET STRINGS OF THE OTHER VALUES
        String timeSeriesString = ini.get("MY_DATA_HERE", "TIME_SERIES", String.class);
        String symbolString1 = ini.get("MY_DATA_HERE", "SYMBOL1", String.class);
        String symbolString2 = ini.get("MY_DATA_HERE", "SYMBOL2", String.class);
        String timeIntervalString = ini.get("MY_DATA_HERE", "TIME_INTERVAL", String.class);
        String outputSizeString = ini.get("MY_DATA_HERE", "OUTPUT_SIZE", String.class);
        
        // MAKE STRINGS ARRAYLISTS
        String[] timeSeriesArray = timeSeriesString.split(",");
        String[] symbolArray1 = symbolString1.split(",");
        String[] symbolArray2 = symbolString2.split(",");
        String[] timeIntervalArray = timeIntervalString.split(",");
        String[] outputSizeArray = outputSizeString.split(",");
        
        // TRIMMING AND ADDING TIME SERIES TO ARRAYLIST
        for (int i = 0; i < timeSeriesArray.length; i++) {
        	timeSeriesxx.add(timeSeriesArray[i].trim());
        }
        
        // TRIMMING AND ADDING SYMBOL1 TO ARRAY LIST
        for (int i = 0; i < symbolArray1.length; i++) {
        	symbol1xx.add(symbolArray1[i].trim());
        }
        
        // TRIMMING AND ADDING SYMBOL2 TO ARRAY LIST
        for (int i = 0; i < symbolArray2.length; i++) {
        	symbol2xx.add(symbolArray2[i].trim());
        }
        
        // TRIMMING AND ADDING TIME INTERVAL TO ARRAY LIST
        for (int i = 0; i < timeIntervalArray.length; i++) {
        	timeIntervalxx.add(timeIntervalArray[i].trim());
        }
        
        // TRIMMING AND ADDING OUTPUT SIZE TO ARRAY LIST
        for (int i = 0; i < outputSizeArray.length; i++) {
        	outputSizexx.add(outputSizeArray[i].trim());
        }
    }

    public StockForm() {

        // ARRAY TO STORE THE STATE OF SELECTED OPTIONS
        String[] stateArray = new String[7];

        // DEFAULT VALUES
        stateArray[0] = "open";
        stateArray[1] = timeSeriesxx.get(0);
        stateArray[2] = "";
        stateArray[3] = timeIntervalxx.get(0);
        stateArray[4] = outputSizexx.get(0);
        stateArray[5] = apiKeyxx;
        stateArray[6] = "";
        
        
        // get start date
        textField2.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				String selectedData = (String) textField2.getText();
				startDatexx = selectedData;
			}

			@Override
			public void focusLost(FocusEvent e) {
				String selectedData = (String) textField2.getText();
				startDatexx = selectedData;
			}
        	
        });
        
        // get stop date
        textField3.addFocusListener(new FocusListener() {
        	
        	@Override
        	public void focusGained(FocusEvent e) {
        		String selectedData = (String) textField3.getText();
        		stopDatexx = selectedData;
        	}

			@Override
			public void focusLost(FocusEvent e) {
				String selectedData = (String) textField3.getText();
        		stopDatexx = selectedData;
			}
        	
        });
        
        // sets apiKey
        textField.addFocusListener(new FocusListener() {
        	
        	public void focusGained(FocusEvent e) {
        		String selectedData = (String) textField.getText();
        		stateArray[5] = selectedData;
        	}

			@Override
			public void focusLost(FocusEvent bb) {
				String selectedData = (String) textField.getText();
        		stateArray[5] = selectedData;
			}
        });
        
        
        // PEARSON BUTTON
        PearsonButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		if (bothSymbols) {
        			
        			
        			double pearsonValue = calculatePearson(symbolListValues1, symbolListValues2);
        			String pearsonValueString = pearsonValue + "";
        			textField4.setText(pearsonValueString);
        			
        		}
        		
        	}
        });

        // sets data for dataComboBox
        dataComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedData = (String) dataComboBox.getSelectedItem();
                stateArray[0] = selectedData;
            }
        });
        
        // sets data for timeSeriesComboBox
        timeSeriesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTimeSeries = (String) timeSeriesComboBox.getSelectedItem();
                stateArray[1] = selectedTimeSeries;
                // Changes the color of timeIntervalComboBox to light gray and disables it
            if (!selectedTimeSeries.equals("TIME_SERIES_INTRADAY")) {
            	outputComboBox.setEnabled(false);
            	timeIntervalComboBox.setEnabled(false);
             	} else {
             		outputComboBox.setEnabled(true);
                	timeIntervalComboBox.setEnabled(true);
               }
            }
        });
        
        // sets data for symbolComboBox
        symbolComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSymbol = (String) symbolComboBox.getSelectedItem();
                stateArray[2] = selectedSymbol;
            }
        });
        
        // sets data for symbolComboBox2
        symbolComboBox2.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String selectedSymbol = (String) symbolComboBox2.getSelectedItem();
        		stateArray[6] = selectedSymbol;
        	}
        });
        
        // sets data for timeIntervalComboBox
        timeIntervalComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
	                String selectedInterval = (String) timeIntervalComboBox.getSelectedItem();
	                stateArray[3] = selectedInterval;
            	
            }
        });
        
        // sets data for outputComboBox
        outputComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            		String selectedOutput = (String) outputComboBox.getSelectedItem();
                    stateArray[4] = selectedOutput;
            }
        });
        
        // doqueryButton
        doQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                URL url = null;
                URL url1 = null;
                URL url2 = null;
                
                JSONObject newObject1 = null;
                JSONObject newObject2 = null;
                
                try {
                	
                	textArea.setText(null);
                	textField4.setText(null);
                	
                	
                	// IF ONLY SYMBOL 1 IS CHOSEN
                	if (!stateArray[2].equals("") && stateArray[6].equals("")) {
                		
                		bothSymbols = false;
                		
                		url = new URL("https://www.alphavantage.co/query?function=" + stateArray[1] + "&" +
                                "symbol=" + stateArray[2] + "&" + "interval=" + stateArray[3] +
                                "&" + "outputsize=" + stateArray[4] + "&" + "apikey=" + stateArray[5]
                        );
                		
                	// IF ONLY SYMBOL 2 IS CHOSEN
                	}else if (stateArray[2].equals("") && !stateArray[6].equals("")) {
                		
                		bothSymbols = false;
                		
                		url = new URL("https://www.alphavantage.co/query?function=" + stateArray[1] + "&" +
                                "symbol=" + stateArray[6] + "&" + "interval=" + stateArray[3] +
                                "&" + "outputsize=" + stateArray[4] + "&" + "apikey=" + stateArray[5]
                        );
                		
                	// IF BOTH SYMBOLS ARE CHOSEN
                	}else if (!stateArray[2].equals("") && !stateArray[6].equals("")) {
                		
                		bothSymbols = true;
                		
                		url1 = new URL("https://www.alphavantage.co/query?function=" + stateArray[1] + "&" +
                                "symbol=" + stateArray[2] + "&" + "interval=" + stateArray[3] +
                                "&" + "outputsize=" + stateArray[4] + "&" + "apikey=" + stateArray[5]
                        );
                		
                		url2 = new URL("https://www.alphavantage.co/query?function=" + stateArray[1] + "&" +
                                "symbol=" + stateArray[6] + "&" + "interval=" + stateArray[3] +
                                "&" + "outputsize=" + stateArray[4] + "&" + "apikey=" + stateArray[5]
                        );
                		
                		// FIRST SYMBOL!!!!!!!!!!!!
                		//
                		//
                		HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                        con1.setRequestMethod("GET");

                        // READING THE RESPONSE
                        BufferedReader in1 = new BufferedReader(
                                new InputStreamReader(con1.getInputStream()));
                        String inputLine1;
                        StringBuffer content1 = new StringBuffer();
                        while ((inputLine1 = in1.readLine()) != null) {
                            content1.append(inputLine1 + "\n");
                        }
                        
                        in1.close();
                        con1.disconnect();
                        String JSONString1 = content1.toString();
                        
                        // CREATE A JSON OBJECT
                        JSONTokener newTokener1 = new JSONTokener(JSONString1);
                        newObject1 = new JSONObject(newTokener1);
                        
                        // PUT THREAD TO SLEEP
                        Thread.sleep(1000);
                        
                        // SECOND SYMBOL !!!
                        //
                        //
                        HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
                        con2.setRequestMethod("GET");

                        // READING THE RESPONSE
                        BufferedReader in2 = new BufferedReader(
                                new InputStreamReader(con2.getInputStream()));
                        String inputLine2;
                        StringBuffer content2 = new StringBuffer();
                        while ((inputLine2 = in2.readLine()) != null) {
                            content2.append(inputLine2 + "\n");
                        }
                        
                        in2.close();
                        con2.disconnect();
                        String JSONString2 = content2.toString();
                        
                        // CREATE A JSON OBJECT
                        JSONTokener newTokener2 = new JSONTokener(JSONString2);
                        newObject2 = new JSONObject(newTokener2);
                		
                	}
                    
                	JSONObject newObject = null;
                	if (!bothSymbols) {
                		HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");

                        // READING THE RESPONSE
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer content = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine + "\n");
                        }
                        
                        in.close();
                        con.disconnect();
                        String JSONString = content.toString();
                        
                        // CREATE A JSON OBJECT
                        JSONTokener newTokener = new JSONTokener(JSONString);
                        newObject = new JSONObject(newTokener);
                	}

                    // DECIDE WHICH KIND OF TIME-SERIES-OBJECT TO GET
                    String keyForObject = "";
                    if (stateArray[1].equals("TIME_SERIES_INTRADAY")) {
                    	if (stateArray[3].equals("1min")) {
                    		keyForObject = "Time Series (1min)";
                    	}
                    	if (stateArray[3].equals("5min")) {
                    		keyForObject = "Time Series (5min)";
                    	}
                    	if (stateArray[3].equals("15min")) {
                    		keyForObject = "Time Series (15min)";
                    	}
                    	if (stateArray[3].equals("30min")) {
                    		keyForObject = "Time Series (30min)";
                    	}
                    	if (stateArray[3].equals("60min")) {
                    		keyForObject = "Time Series (60min)";
                    	}
                    }
                    if (stateArray[1].equals("TIME_SERIES_DAILY")) {
                    	keyForObject = "Time Series (Daily)";
                    }
                    if (stateArray[1].equals("TIME_SERIES_DAILY_ADJUSTED")) {
                    	keyForObject = "Time Series (Daily)";
                    }
                    if (stateArray[1].equals("TIME_SERIES_WEEKLY")) {
                    	keyForObject = "Weekly Time Series";
                    }
                    if (stateArray[1].equals("TIME_SERIES_WEEKLY_ADJUSTED")) {
                    	keyForObject = "Weekly Adjusted Time Series";
                    }
                    if (stateArray[1].equals("TIME_SERIES_MONTHLY")) {
                    	keyForObject = "Monthly Time Series";
                    }
                    if (stateArray[1].equals("TIME_SERIES_MONTHLY_ADJUSTED")) {
                    	keyForObject = "Monthly Adjusted Time Series";
                    }
                    
                    // GET THE CORRECT TIME SERIES OBJECT
                    
                    JSONObject metaObject = null;
                    JSONObject metaObject1 = null;
                    JSONObject metaObject2 = null;
                    if (!bothSymbols) {
                    	metaObject = newObject.getJSONObject(keyForObject);
                    }else if (bothSymbols) {
                    	metaObject1 = newObject1.getJSONObject(keyForObject);
                    	metaObject2 = newObject2.getJSONObject(keyForObject);
                    }
                    
                    
                    ///////////////////////////////////////////////////////////////////////////
                    // ONLY ONE SYMBOL!!!!
                    ///////////////////////////////////////////////////////////////////////////
                    
                    String toPrintString = "";
                    if (!bothSymbols) {
                    	// SAVE WHAT TO OUTPUT IN AN ARRAY LIST
                        ArrayList<StockWithDate> toPrintList = new ArrayList<StockWithDate>();
                        for (String key: metaObject.keySet()) {
                        	
                        	char[] dateToSplit = key.toCharArray();
                        	
                        	// YEAR
                        	String year1 = dateToSplit[0] + "";
                        	String year2 = dateToSplit[1] + "";
                        	String year3 = dateToSplit[2] + "";
                        	String year4 = dateToSplit[3] + "";
                        	String yearString = year1 + year2 + year3 + year4;
                        	int yearInt = Integer.parseInt(yearString);
                        	
                        	// MONTH
                        	String month1 = dateToSplit[5] + "";
                        	String month2 = dateToSplit[6] + "";
                        	String monthString = month1 + month2;
                        	int monthInt = Integer.parseInt(monthString);
                        	
                        	// DAY
                        	String day1 = dateToSplit[8] + "";
                        	String day2 = dateToSplit[9] + "";
                        	String dayString = day1 + day2;
                        	int dayInt = Integer.parseInt(dayString);
                        	
                        	int hourInt = 0;
                        	int minuteInt = 0;
                        	int secondInt = 0;
                        	if (stateArray[1].equals("TIME_SERIES_INTRADAY")) {
                        		
                        		// HOUR
                        		String hour1 = dateToSplit[11] + "";
                        		String hour2 = dateToSplit[12] + "";
                        		String hourString = hour1 + hour2;
                        		hourInt = Integer.parseInt(hourString);
                        		
                        		// MINUTE
                        		String minute1 = dateToSplit[14] + "";
                        		String minute2 = dateToSplit[15] + "";
                        		String minuteString = minute1 + minute2;
                        		minuteInt = Integer.parseInt(minuteString);
                        		
                        		// SECOND
                        		String second1 = dateToSplit[17] + "";
                        		String second2 = dateToSplit[18] + "";
                        		String secondString = second1 + second2;
                        		secondInt = Integer.parseInt(secondString);
                        		
                        	}
                        	
                        	JSONObject stockValueObject = metaObject.getJSONObject(key);
                        	
                        	StockWithDate newStock = new StockWithDate(yearInt, monthInt, dayInt, hourInt, minuteInt, secondInt, stockValueObject);
                        	toPrintList.add(newStock);
                        }
                        
                        Collections.sort(toPrintList);
                        
                        ArrayList<StockWithDate> startDateList = new ArrayList<StockWithDate>();
                        ArrayList<StockWithDate> stopDateList = new ArrayList<StockWithDate>();
                        // CHECKING THE START AND STOP PARAMETERS
                        if (!(startDatexx.equals("") && stopDatexx.equals("") || stateArray[1].equals("TIME_SERIES_INTRADAY"))) {
                        	
                        	Date startDateDate = null;
                        	Date stopDateDate = null;
                        	
                        	if (!(startDatexx.equals(""))) {
                        		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        		startDateDate = df.parse(startDatexx);
                        	}
                        	
                        	if (!(stopDatexx.equals(""))) {
                        		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        		stopDateDate = df.parse(stopDatexx);
                        	}
                        	
                        	
                        	// TAKING AWAY EVERY OBJBECT AFTER STOP DATE
                        	for (int i = 0; i < toPrintList.size(); i++) {
                        		if (!(toPrintList.get(i).getDate().after(stopDateDate))) {
                        			startDateList.add(toPrintList.get(i));
                        		}
                        	}
                        	
                        	// TAKING AWAY EVERY OBJECT BEFORE START DATE
                        	for (int i = 0; i < startDateList.size(); i++) {
                        		if (!(startDateList.get(i).getDate().before(startDateDate))) {
                        			stopDateList.add(startDateList.get(i));
                        		}
                        	}
                        	
                        }
                        
                        toPrintString = "";
                        
                        // IF DATE TEXTFIELDS ARE LEFT EMPTY or time series is TIME_SERIES_INTRADAY
                        if ((startDatexx.equals("") && stopDatexx.equals("")) || stateArray[1].equals("TIME_SERIES_INTRADAY")) {
                        	stopDateList = toPrintList;
                        }
                        
                        String symbolToPrint = "";
                        if (!stateArray[2].equals("")) {
                        	symbolToPrint = stateArray[2];
                        }else if (!stateArray[6].equals("")) {
                        	symbolToPrint = stateArray[6];
                        }
                        for (int i = 0; i < stopDateList.size(); i++) {
                        	
                        	String toChoose = "";
                        	if (stateArray[0].equals("open")) {
                        		toChoose = "1. open";
                        	}
                        	if (stateArray[0].equals("high")) {
                        		toChoose = "2. high";
                        	}
                        	if (stateArray[0].equals("low")) {
                        		toChoose = "3. low";
                        	}
                        	if (stateArray[0].equals("close")) {
                        		toChoose = "4. close";
                        	}
                        	if (stateArray[0].equals("volume")) {
                        		toChoose = "5. volume";
                        	}
                        	toPrintString = toPrintString + stopDateList.get(i).getDate() + " " + symbolToPrint + " " + stopDateList.get(i).getData().get(toChoose) + "\n";
                        }
                    }
                    
                    ////////////////////////////////////////////////////////////////
                    // IF BOTH SYMBOLS ARE CHOSEN
                    ////////////////////////////////////////////////////////////////
                    if (bothSymbols) {
                    	// SAVE WHAT TO OUTPUT IN AN ARRAY LIST
                        ArrayList<StockWithDate> toPrintList1 = new ArrayList<StockWithDate>();
                        for (String key: metaObject1.keySet()) {
                        	
                        	char[] dateToSplit = key.toCharArray();
                        	
                        	// YEAR
                        	String year1 = dateToSplit[0] + "";
                        	String year2 = dateToSplit[1] + "";
                        	String year3 = dateToSplit[2] + "";
                        	String year4 = dateToSplit[3] + "";
                        	String yearString = year1 + year2 + year3 + year4;
                        	int yearInt = Integer.parseInt(yearString);
                        	
                        	// MONTH
                        	String month1 = dateToSplit[5] + "";
                        	String month2 = dateToSplit[6] + "";
                        	String monthString = month1 + month2;
                        	int monthInt = Integer.parseInt(monthString);
                        	
                        	// DAY
                        	String day1 = dateToSplit[8] + "";
                        	String day2 = dateToSplit[9] + "";
                        	String dayString = day1 + day2;
                        	int dayInt = Integer.parseInt(dayString);
                        	
                        	int hourInt = 0;
                        	int minuteInt = 0;
                        	int secondInt = 0;
                        	if (stateArray[1].equals("TIME_SERIES_INTRADAY")) {
                        		
                        		// HOUR
                        		String hour1 = dateToSplit[11] + "";
                        		String hour2 = dateToSplit[12] + "";
                        		String hourString = hour1 + hour2;
                        		hourInt = Integer.parseInt(hourString);
                        		
                        		// MINUTE
                        		String minute1 = dateToSplit[14] + "";
                        		String minute2 = dateToSplit[15] + "";
                        		String minuteString = minute1 + minute2;
                        		minuteInt = Integer.parseInt(minuteString);
                        		
                        		// SECOND
                        		String second1 = dateToSplit[17] + "";
                        		String second2 = dateToSplit[18] + "";
                        		String secondString = second1 + second2;
                        		secondInt = Integer.parseInt(secondString);
                        		
                        	}
                        	
                        	JSONObject stockValueObject = metaObject1.getJSONObject(key);
                        	
                        	StockWithDate newStock = new StockWithDate(yearInt, monthInt, dayInt, hourInt, minuteInt, secondInt, stockValueObject);
                        	toPrintList1.add(newStock);
                        }
                        
                        Collections.sort(toPrintList1);
                        
                        ArrayList<StockWithDate> startDateList1 = new ArrayList<StockWithDate>();
                        ArrayList<StockWithDate> stopDateList1 = new ArrayList<StockWithDate>();
                        // CHECKING THE START AND STOP PARAMETERS
                        if (!(startDatexx.equals("") && stopDatexx.equals("") || stateArray[1].equals("TIME_SERIES_INTRADAY"))) {
                        	
                        	Date startDateDate = null;
                        	Date stopDateDate = null;
                        	
                        	if (!(startDatexx.equals(""))) {
                        		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        		startDateDate = df.parse(startDatexx);
                        	}
                        	
                        	if (!(stopDatexx.equals(""))) {
                        		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        		stopDateDate = df.parse(stopDatexx);
                        	}
                        	
                        	
                        	// TAKING AWAY EVERY OBJBECT AFTER STOP DATE
                        	for (int i = 0; i < toPrintList1.size(); i++) {
                        		if (!(toPrintList1.get(i).getDate().after(stopDateDate))) {
                        			startDateList1.add(toPrintList1.get(i));
                        		}
                        	}
                        	
                        	// TAKING AWAY EVERY OBJECT BEFORE START DATE
                        	for (int i = 0; i < startDateList1.size(); i++) {
                        		if (!(startDateList1.get(i).getDate().before(startDateDate))) {
                        			stopDateList1.add(startDateList1.get(i));
                        		}
                        	}
                        	
                        }
                        
                        // IF DATE TEXTFIELDS ARE LEFT EMPTY or time series is TIME_SERIES_INTRADAY
                        if ((startDatexx.equals("") && stopDatexx.equals("")) || stateArray[1].equals("TIME_SERIES_INTRADAY")) {
                        	stopDateList1 = toPrintList1;
                        }
                        
                        //
                        //
                        //SECOND SYMBOL
                        ArrayList<StockWithDate> toPrintList2 = new ArrayList<StockWithDate>();
                        for (String key: metaObject2.keySet()) {
                        	
                        	char[] dateToSplit = key.toCharArray();
                        	
                        	// YEAR
                        	String year1 = dateToSplit[0] + "";
                        	String year2 = dateToSplit[1] + "";
                        	String year3 = dateToSplit[2] + "";
                        	String year4 = dateToSplit[3] + "";
                        	String yearString = year1 + year2 + year3 + year4;
                        	int yearInt = Integer.parseInt(yearString);
                        	
                        	// MONTH
                        	String month1 = dateToSplit[5] + "";
                        	String month2 = dateToSplit[6] + "";
                        	String monthString = month1 + month2;
                        	int monthInt = Integer.parseInt(monthString);
                        	
                        	// DAY
                        	String day1 = dateToSplit[8] + "";
                        	String day2 = dateToSplit[9] + "";
                        	String dayString = day1 + day2;
                        	int dayInt = Integer.parseInt(dayString);
                        	
                        	int hourInt = 0;
                        	int minuteInt = 0;
                        	int secondInt = 0;
                        	if (stateArray[1].equals("TIME_SERIES_INTRADAY")) {
                        		
                        		// HOUR
                        		String hour1 = dateToSplit[11] + "";
                        		String hour2 = dateToSplit[12] + "";
                        		String hourString = hour1 + hour2;
                        		hourInt = Integer.parseInt(hourString);
                        		
                        		// MINUTE
                        		String minute1 = dateToSplit[14] + "";
                        		String minute2 = dateToSplit[15] + "";
                        		String minuteString = minute1 + minute2;
                        		minuteInt = Integer.parseInt(minuteString);
                        		
                        		// SECOND
                        		String second1 = dateToSplit[17] + "";
                        		String second2 = dateToSplit[18] + "";
                        		String secondString = second1 + second2;
                        		secondInt = Integer.parseInt(secondString);
                        		
                        	}
                        	
                        	JSONObject stockValueObject = metaObject2.getJSONObject(key);
                        	
                        	StockWithDate newStock = new StockWithDate(yearInt, monthInt, dayInt, hourInt, minuteInt, secondInt, stockValueObject);
                        	toPrintList2.add(newStock);
                        }
                        
                        Collections.sort(toPrintList2);
                        
                        ArrayList<StockWithDate> startDateList2 = new ArrayList<StockWithDate>();
                        ArrayList<StockWithDate> stopDateList2 = new ArrayList<StockWithDate>();
                        // CHECKING THE START AND STOP PARAMETERS
                        if (!(startDatexx.equals("") && stopDatexx.equals("") || stateArray[1].equals("TIME_SERIES_INTRADAY"))) {
                        	
                        	Date startDateDate = null;
                        	Date stopDateDate = null;
                        	
                        	if (!(startDatexx.equals(""))) {
                        		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        		startDateDate = df.parse(startDatexx);
                        	}
                        	
                        	if (!(stopDatexx.equals(""))) {
                        		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        		stopDateDate = df.parse(stopDatexx);
                        	}
                        	
                        	
                        	// TAKING AWAY EVERY OBJBECT AFTER STOP DATE
                        	for (int i = 0; i < toPrintList2.size(); i++) {
                        		if (!(toPrintList2.get(i).getDate().after(stopDateDate))) {
                        			startDateList2.add(toPrintList2.get(i));
                        		}
                        	}
                        	
                        	// TAKING AWAY EVERY OBJECT BEFORE START DATE
                        	for (int i = 0; i < startDateList2.size(); i++) {
                        		if (!(startDateList2.get(i).getDate().before(startDateDate))) {
                        			stopDateList2.add(startDateList2.get(i));
                        		}
                        	}
                        	
                        }

                        
                        // IF DATE TEXTFIELDS ARE LEFT EMPTY or time series is TIME_SERIES_INTRADAY
                        if ((startDatexx.equals("") && stopDatexx.equals("")) || stateArray[1].equals("TIME_SERIES_INTRADAY")) {
                        	stopDateList2 = toPrintList2;
                        }
                        
                        String symbolToPrint1 = stateArray[2];
                        String symbolToPrint2 = stateArray[6];

                        for (int i = 0; i < stopDateList1.size(); i++) {
                        	
                        	String toChoose = "";
                        	if (stateArray[0].equals("open")) {
                        		toChoose = "1. open";
                        	}
                        	if (stateArray[0].equals("high")) {
                        		toChoose = "2. high";
                        	}
                        	if (stateArray[0].equals("low")) {
                        		toChoose = "3. low";
                        	}
                        	if (stateArray[0].equals("close")) {
                        		toChoose = "4. close";
                        	}
                        	if (stateArray[0].equals("volume")) {
                        		toChoose = "5. volume";
                        	}
                        	toPrintString = toPrintString + stopDateList1.get(i).getDate() + " " + symbolToPrint1 + " " + stopDateList1.get(i).getData().get(toChoose) +
                        			 " " + symbolToPrint2 + " " + stopDateList2.get(i).getData().getDouble(toChoose) + "\n";
                        }
                        
                        String[] valueStringArrayX = toPrintString.split("\n");
                        
                        symbolListValues1.clear();
                        symbolListValues2.clear();
                        for (int i = 0; i < valueStringArrayX.length; i++) {
                        	
                        	String[] valStrArr = valueStringArrayX[i].split(" ");
                        	String valEl1 = valStrArr[valStrArr.length - 3];
                        	String valEl2 = valStrArr[valStrArr.length - 1];
                        	
                        	symbolListValues1.add(valEl1);
                        	symbolListValues2.add(valEl2);
                        	
                        }
                    }
                    
                    
                    // OUTPUT
                    textArea.append(toPrintString);
                    
                    if (!bothSymbols) {
                    	String[] chartArray = toPrintString.split("\n");
                        double[] valueArray = new double[chartArray.length];
                        for (int i = 0; i < chartArray.length; i++) {
                        	
                        	String[] eleValStrArray = chartArray[i].split(" ");
                        	String eleValStr = eleValStrArray[eleValStrArray.length - 1];
                        	double eleVal = Double.parseDouble(eleValStr);
            
            				valueArray[i] = eleVal;
                        }
                        
                        // JFREECHART
                        XYDataset dataset = createDataset(valueArray);
                        JFreeChart chart = createChart(dataset);
                        ChartPanel chartPanel = new ChartPanel(chart);
                        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                        chartPanel.setPreferredSize(new Dimension(400,400));
                        
                        StockPanel.add(chartPanel);
                    }
                    
                    if (bothSymbols) {
                    	
                    	
                    	
                    }
                    
                    
                    
                    
                    
                    

                } catch (MalformedURLException malformedError) {
                    malformedError.printStackTrace();
                } catch (ProtocolException protocolError) {
                    protocolError.printStackTrace();
                } catch (IOException IOError) {
                	JOptionPane.showMessageDialog(null, "Check your internet connection and try again!");
                } catch (org.json.JSONException JSONError) {
                	JOptionPane.showMessageDialog(null, "You have selected a wrong attribute or have not selected a symbol, check your ini file!");
                } catch (ParseException parseError) {
                	JOptionPane.showMessageDialog(null, "Check your date!");
				} catch (NullPointerException NullError) {
					JOptionPane.showMessageDialog(null, "You have to choose a symbol mate!");
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
    }
    
    public static void main (String[] args) {
	    JFrame frame = new JFrame("Stock Analyzer");
	    frame.setContentPane(new StockForm().StockPanel);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.pack();	
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
    }


    {
    	try {
			getDataFromIni();
		} catch (InvalidFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	$$$setupUI$$$();
    }
    
    

    private void $$$setupUI$$$() {

    	// stockPanel
        StockPanel = new JPanel();
        StockPanel.setLayout(new GridBagLayout());
        
        StockPanel.setOpaque(false);
        
        StockPanel.setRequestFocusEnabled(true);
        GridBagConstraints gbc;
        
        // APIKEY TEXTFIELD
        textField = new JTextField();
        textField.setOpaque(false);
        textField.setPreferredSize(new Dimension(400, 21));
        textField.setText(apiKeyxx);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(textField, gbc);
        
     // Start date textField2
        textField2 = new JTextField();
        textField2.setOpaque(false);
        textField2.setPreferredSize(new Dimension(400, 21));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(textField2, gbc);
        
     // Stop date textField3
        textField3 = new JTextField();
        textField3.setOpaque(false);
        textField3.setPreferredSize(new Dimension(400, 21));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(textField3, gbc);
        
     // Stop date textField4
        textField4 = new JTextField();
        textField4.setOpaque(false);
        textField4.setPreferredSize(new Dimension(400, 21));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 22;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(textField4, gbc);
        
        
        // dataComboBox
        dataComboBox = new JComboBox();
        dataComboBox.setAutoscrolls(false);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("open");
        defaultComboBoxModel1.addElement("high");
        defaultComboBoxModel1.addElement("low");
        defaultComboBoxModel1.addElement("close");
        defaultComboBoxModel1.addElement("volume");
        dataComboBox.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(dataComboBox, gbc);
        
        // timeSeriesComboBox
        timeSeriesComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        
        for (int i = 0; i < timeSeriesxx.size(); i++) {
        	defaultComboBoxModel2.addElement(timeSeriesxx.get(i));
        }
        timeSeriesComboBox.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(timeSeriesComboBox, gbc);
        
        // symbolComboBox
        symbolComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("");
        for (int i = 0; i < symbol1xx.size(); i++) {
        	defaultComboBoxModel3.addElement(symbol1xx.get(i));
        }
        symbolComboBox.setModel(defaultComboBoxModel3);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(symbolComboBox, gbc);
        
        // symbolComboBox2
        symbolComboBox2 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("");
        for (int i = 0; i < symbol2xx.size(); i++) {
        	defaultComboBoxModel6.addElement(symbol2xx.get(i));
        }
        symbolComboBox2.setModel(defaultComboBoxModel6);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(symbolComboBox2, gbc);
        
        // timeIntervalComboBox
        timeIntervalComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        for (int i = 0;i < timeIntervalxx.size(); i++) {
        	defaultComboBoxModel4.addElement(timeIntervalxx.get(i));
        }
        timeIntervalComboBox.setModel(defaultComboBoxModel4);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(timeIntervalComboBox, gbc);
        
        // outputComboBox
        outputComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        for (int i = 0; i < outputSizexx.size(); i++) {
        	defaultComboBoxModel5.addElement(outputSizexx.get(i));
        }
        outputComboBox.setModel(defaultComboBoxModel5);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(outputComboBox, gbc);
        
        // doQueryButton
        doQueryButton = new JButton();
        doQueryButton.setPreferredSize(new Dimension(160, 22));
        doQueryButton.setText("Do Query");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 20;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(doQueryButton, gbc);
        
     // Pearson correlation Button
        PearsonButton = new JButton();
        PearsonButton.setPreferredSize(new Dimension(0, 22));
        PearsonButton.setText("Pearson Correlation");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 22;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(PearsonButton, gbc);
        
        // Resize window label
        final JLabel label = new JLabel();
        label.setText("Resize the window after every query");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.CENTER;
        StockPanel.add(label, gbc);
        
        // API key label
        final JLabel label0 = new JLabel();
        label0.setText("API key");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label0, gbc);
        
        // dataLabel
        final JLabel label1 = new JLabel();
        label1.setText("Data Series");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label1, gbc);
        
        // timeLabel
        final JLabel label2 = new JLabel();
        label2.setText("Time Series");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label2, gbc);
        
        // symbolLabel
        final JLabel label3 = new JLabel();
        label3.setText("Symbol 1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label3, gbc);
        
        // symbolLabel
        final JLabel label3_2 = new JLabel();
        label3_2.setText("Symbol 2");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label3_2, gbc);
        
        // timeLabel
        final JLabel label4 = new JLabel();
        label4.setText("Time Interval");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label4, gbc);
        
        // outputLabel
        final JLabel label5 = new JLabel();
        label5.setText("Output Size");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label5, gbc);
        
        // Start dateLabel
        final JLabel label6 = new JLabel();
        label6.setText("Start date in form month/day/year (MM/dd/yyyy)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label6, gbc);
        
        // Stop dateLabel
        final JLabel label7 = new JLabel();
        label7.setText("Stop date in form month/day/year (MM/dd/yyyy)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label7, gbc);
        
        // Spacers
        final JPanel spacer0 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        StockPanel.add(spacer0, gbc);
        
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(spacer1, gbc);
        
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(spacer2, gbc);
        
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(spacer3, gbc);
        
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer6, gbc);
        
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer8, gbc);
        
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer9, gbc);
        
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer10, gbc);
        
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer5, gbc);
        
        final JPanel spacer14 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer14, gbc);
        
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer11, gbc);
        
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 23;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer12, gbc);
        
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 20;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer13, gbc);
        
        final JPanel spacer15 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 16;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer15, gbc);
        
        final JPanel spacer16 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 19;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer16, gbc);
        
        final JPanel spacer17 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 21;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer17, gbc);
        
        // textArea with scrollpane
        textAreaScroll = new JScrollPane();
        textAreaScroll.setAutoscrolls(true);
        textAreaScroll.setHorizontalScrollBarPolicy(31);
        textAreaScroll.setPreferredSize(new Dimension(420, 330));
        textAreaScroll.setVerticalScrollBarPolicy(22);
        textAreaScroll.setWheelScrollingEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 24;
        gbc.fill = GridBagConstraints.BOTH;
        StockPanel.add(textAreaScroll, gbc);
        textArea = new JTextArea();
        textArea.setOpaque(false);
        textArea.setPreferredSize(new Dimension(400, 600000));
        textArea.setRows(5);
        textArea.setWrapStyleWord(false);
        textAreaScroll.setViewportView(textArea);
        

                
    }
    // JFREECHART
    private XYDataset createDataset(double[] valueData) {

        XYSeries series = new XYSeries("Stock Data");
        
        for (int i = 0; i < valueData.length; i++) {
        	series.add((i + 1), valueData[i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }
    
    
    //JFREECHART
    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                null, 
                null, 
                null, 
                dataset, 
                PlotOrientation.VERTICAL,
                true, 
                true, 
                false 
        );

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        return chart;

    }
    
    

    public JComponent $$$getRootComponent$$$() {
        return StockPanel;
    }
    
}