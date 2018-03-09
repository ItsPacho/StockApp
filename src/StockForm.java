import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONTokener;
import org.json.JSONObject;

public class StockForm {
 
    // UI imports
    private JComboBox dataComboBox;
    private JComboBox timeSeriesComboBox;
    private JComboBox symbolComboBox;
    private JComboBox timeIntervalComboBox;
    private JComboBox outputComboBox;
    private JButton doQueryButton;
    private JPanel StockPanel;
    private JTextArea textArea;
    private JScrollPane textAreaScroll;

    public StockForm() {

        // ARRAY TO STORE THE STATE OF SELECTED OPTIONS
        String[] stateArray = new String[5];

        // DEFAULT VALUES
        stateArray[0] = "open";
        stateArray[1] = "TIME_SERIES_INTRADAY";
        stateArray[2] = "A";
        stateArray[3] = "1min";
        stateArray[4] = "full";

        dataComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedData = (String) dataComboBox.getSelectedItem();
                stateArray[0] = selectedData;
            }
        });
        timeSeriesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTimeSeries = (String) timeSeriesComboBox.getSelectedItem();
                stateArray[1] = selectedTimeSeries;
            }
        });
        symbolComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSymbol = (String) symbolComboBox.getSelectedItem();
                stateArray[2] = selectedSymbol;
            }
        });
        timeIntervalComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedInterval = (String) timeIntervalComboBox.getSelectedItem();
                stateArray[3] = selectedInterval;
            }
        });
        outputComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOutput = (String) outputComboBox.getSelectedItem();
                stateArray[4] = selectedOutput;
            }
        });
        doQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //////////////////////
                URL url = null;
                try {
                	
                	textArea.setText(null);

                    // DOING THE REQUEST
                    url = new URL("https://www.alphavantage.co/query?function=" + stateArray[1] + "&" +
                            "symbol=" + stateArray[2] + "&" + "interval=" + stateArray[3] +
                            "&" + "outputsize=" + stateArray[4] + "&" + "apikey=XVT9GOFLC4DOQYGY"
                    );
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
                    JSONObject newObject = new JSONObject(newTokener);
                    
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
                    JSONObject metaObject = newObject.getJSONObject(keyForObject);
                    
                    // SAVE WHAT TO OUTPUT IN AN ARRAY LIST
                    ArrayList<String> toPrintList = new ArrayList<String>();
                    for (String key: metaObject.keySet()) {
                    	
                    	toPrintList.add(key + " " + metaObject.getJSONObject(key).get("1. open"));
                    }
                    
                    // Sort by date
                    ArrayList<String> byYearList = new ArrayList<String>();
                    
                    System.out.println(toPrintList.size());
                    
                    int numberOfTimes = toPrintList.size();
                    for (int i = 0; i < numberOfTimes; i++) {
                    	
                    	// Get year of first element
                    	char[] lowestYearArray = toPrintList.get(0).toCharArray();
                    	int lowestIndex = 0;
                    	String lowestYearString = lowestYearArray[0] + lowestYearArray[1] +
                    						lowestYearArray[2] + lowestYearArray[3] + "";
                    	int lowestYearInt = Integer.parseInt(lowestYearString);
                    	
                    	for (int j = 0; j < toPrintList.size(); j++) {
                    		
                    		char[] lowestYearArrayCompare = toPrintList.get(j).toCharArray();
                        	String lowestYearStringCompare = lowestYearArrayCompare[0] + lowestYearArrayCompare[1] +
                        						lowestYearArrayCompare[2] + lowestYearArrayCompare[3] + "";
                        	int lowestYearIntCompare = Integer.parseInt(lowestYearStringCompare);
                        	
                        	if (lowestYearIntCompare < lowestYearInt) {
                        		lowestIndex = j;
                        	}
                        	
                    	}
                    	
                    	byYearList.add(toPrintList.get(lowestIndex));
                    	toPrintList.remove(lowestIndex);
                    	
                    }
                    
                    System.out.println(byYearList.size());
                    
                    
                    
                    // SAVE WHAT TO OUTPUT IN A STRING
                    String toPrintString = "";
                    for (int i = 0; i < byYearList.size(); i++) {
                    	toPrintString = toPrintString + "Date: " + byYearList.get(i) + "\n";
                    }
                    
        
                    // OUTPUT
                    textArea.append(toPrintString);

                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (ProtocolException protocolError) {
                    protocolError.printStackTrace();
                } catch (IOException IOError) {
                    IOError.printStackTrace();
                } catch (org.json.JSONException JSONError) {
                	JOptionPane.showMessageDialog(null, "Push The Button Again Please!");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Stock Analyzer");
        frame.setContentPane(new StockForm().StockPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        StockPanel = new JPanel();
        StockPanel.setLayout(new GridBagLayout());
        StockPanel.setMinimumSize(new Dimension(522, 552));
        StockPanel.setOpaque(false);
        StockPanel.setPreferredSize(new Dimension(552, 572));
        StockPanel.setRequestFocusEnabled(true);
        dataComboBox = new JComboBox();
        dataComboBox.setAutoscrolls(false);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("open");
        defaultComboBoxModel1.addElement("high");
        defaultComboBoxModel1.addElement("low");
        defaultComboBoxModel1.addElement("close");
        defaultComboBoxModel1.addElement("volume");
        dataComboBox.setModel(defaultComboBoxModel1);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(dataComboBox, gbc);
        timeSeriesComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("TIME_SERIES_INTRADAY");
        defaultComboBoxModel2.addElement("TIME_SERIES_DAILY");
        defaultComboBoxModel2.addElement("TIME_SERIES_DAILY_ADJUSTED");
        defaultComboBoxModel2.addElement("TIME_SERIES_WEEKLY");
        defaultComboBoxModel2.addElement("TIME_SERIES_WEEKLY_ADJUSTED");
        defaultComboBoxModel2.addElement("TIME_SERIES_MONTHLY");
        defaultComboBoxModel2.addElement("TIME_SERIES_MONTHLY_ADJUSTED");
        timeSeriesComboBox.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(timeSeriesComboBox, gbc);
        symbolComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("A");
        defaultComboBoxModel3.addElement("AAPL");
        defaultComboBoxModel3.addElement("C");
        defaultComboBoxModel3.addElement("GOOG");
        defaultComboBoxModel3.addElement("HOG");
        defaultComboBoxModel3.addElement("HPQ");
        defaultComboBoxModel3.addElement("INTC");
        defaultComboBoxModel3.addElement("KO");
        defaultComboBoxModel3.addElement("LUV");
        defaultComboBoxModel3.addElement("MMM");
        defaultComboBoxModel3.addElement("MSFT");
        defaultComboBoxModel3.addElement("T");
        defaultComboBoxModel3.addElement("TGT");
        defaultComboBoxModel3.addElement("TXN");
        defaultComboBoxModel3.addElement("WMT");
        defaultComboBoxModel3.addElement("TSLA");
        symbolComboBox.setModel(defaultComboBoxModel3);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(symbolComboBox, gbc);
        timeIntervalComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("1min");
        defaultComboBoxModel4.addElement("5min");
        defaultComboBoxModel4.addElement("15min");
        defaultComboBoxModel4.addElement("30min");
        defaultComboBoxModel4.addElement("60min");
        timeIntervalComboBox.setModel(defaultComboBoxModel4);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(timeIntervalComboBox, gbc);
        outputComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("full");
        defaultComboBoxModel5.addElement("compact");
        outputComboBox.setModel(defaultComboBoxModel5);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(outputComboBox, gbc);
        doQueryButton = new JButton();
        doQueryButton.setPreferredSize(new Dimension(160, 32));
        doQueryButton.setText("Do Query");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        StockPanel.add(doQueryButton, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Data Series");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Time Series");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Symbol");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Time Interval");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Output Size");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        StockPanel.add(label5, gbc);
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
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer6, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer9, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer10, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        StockPanel.add(spacer11, gbc);
        textAreaScroll = new JScrollPane();
        textAreaScroll.setAutoscrolls(true);
        textAreaScroll.setHorizontalScrollBarPolicy(31);
        textAreaScroll.setPreferredSize(new Dimension(420, 330));
        textAreaScroll.setVerticalScrollBarPolicy(22);
        textAreaScroll.setWheelScrollingEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.BOTH;
        StockPanel.add(textAreaScroll, gbc);
        textArea = new JTextArea();
        textArea.setOpaque(false);
        textArea.setPreferredSize(new Dimension(400, 600000));
        textArea.setRows(5);
        textArea.setWrapStyleWord(false);
        textAreaScroll.setViewportView(textArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return StockPanel;
    }
    
}































