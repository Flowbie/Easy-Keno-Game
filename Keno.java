/******************************************************************************************************************
* @Austin Dolan
* ICS 211
* Keno.java
* 2/18/2023
******************************************************************************************************************/
import java.awt.*; // For graphical elements
import java.awt.event.*; // For Listeners
import javax.swing.*; // For JFrame
import java.io.*; // For input/output of data
import java.util.*;
import java.util.Set; // To generate unique random numbers
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Keno extends JFrame{
   
   // Final vars
   private final int SIZE = 40; // Number of Buttons
   private final int WIDTH = 800;
   private final int HEIGHT = 550; 
   private final int MIN = 1;
   private final int MAX = 41;
   // String Vars
   private String lineIn = null;
   private String instructions = null;
   // JLabels
   private JLabel labelBet = new JLabel("Bet");
   private JLabel labelCurBet = new JLabel("Current Bet:");
   private JLabel labelTotalWon = new JLabel("Total Won:");
   private JLabel labelBank = new JLabel("Bank:");
   private JLabel labelPicks = new JLabel("Numbers Selected:");
   private JLabel labelWinnings = new JLabel("Won/Lost:");
   private JLabel labelMatched = new JLabel("Matched:");
   // JTextFields
   private JTextField tfMatched = new JTextField(2);
   private JTextField tfWinnings = new JTextField(10);
   private JTextField tfTotalWon = new JTextField(10);
   private JTextField tflabelBank = new JTextField(10);
   private JTextField tfPicks = new JTextField(2);
   private JTextField tfBet = new JTextField(4);
   private JTextField tfBank = new JTextField(10);
   // JButtons
   private JButton bInstructions = new JButton("Instructions");
   private JButton bPlay = new JButton("Play");
   private JButton bClear = new JButton("Reset");
   private JButton bEnd = new JButton("Cash Out");
   // Arrs
   private JButton[] arrNumbers = new JButton[SIZE];
   private String[] arrBets = {"$10", "$20", "$50", "$100"};
   private Integer[] arrCompNumbers = new Integer[10];
   // JOptionPane
   private JOptionPane opStartGame = new JOptionPane();
   private JOptionPane opInstructions = new JOptionPane();
   private JOptionPane opReceipt = new JOptionPane();
   // JComboBox
   private JComboBox cbBet = new JComboBox(arrBets);
   // Vars
   private int winnings = 0; // Display values of the winnings of each round
   private int totalWon = 0; // Track total won from beginning to cash out
   private int bank = 500; // initial value of bank at new game
   private int count = 0; // Track number of user buttons selected
   private int currentBet = 10; // Initial bet set to 10
   private int colorCount = 0; // Track number of matches made with color
   private int saveGame = 0; // 0 value to start new game 1 to load save game
   // Values displayed on receipt
   private int totalRounds = 0; 
   private int startBank = 500;
   private int endBank = 0;
   private int roundsWon = 0;
   private int roundsLost = 0;

   
   public Keno(){ // Construct Keno
      this.setSize(WIDTH, HEIGHT);  
      this.setTitle("Keno");
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setVisible(true);
      this.setResizable(false);
   }
   
   public void initializeGUI() throws Exception, IOException{
   
      // Import KenoSave or start new game
      String[] responses = {"New Game", "Load Game"};  // response buttons for new game optionpane
      // Pop up window to load save game or start new game
      int saveGame = opStartGame.showOptionDialog(null, "Play a new game or start from your saved bank", "Play Keno", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, responses, 0);
      if(saveGame == 1){ // Load a saved game
         try {
         // Load saved game
         Scanner scan = new Scanner(new File("KenoSave.txt"));
         lineIn = scan.nextLine();
         bank = Integer.parseInt(lineIn);
         startBank = bank; // Set value of startBank for receipt
         }
         catch(IOException e) { // You try to load a saved game but no save file is found
            JOptionPane.showMessageDialog(null, "No Save File Found, Start a New Game?", "Warning", JOptionPane.WARNING_MESSAGE);
            saveGame = 0;
         }
      }
      

      // Add action listeners to graphic elements
      ActionListener ears = new MyListener( );
      bPlay.addActionListener(ears);
      bClear.addActionListener(ears);
      bEnd.addActionListener(ears);
      bInstructions.addActionListener(ears);
      
      // Add item listner to drop down menu
      ItemListener click = new MyItemListener();
      cbBet.addItemListener(click);
      
      // Display count of picks made
      tfPicks.setText(String.valueOf(count));
      
      // Create main Panel using Border Layout 
      JPanel mainPanel = new JPanel( );
      mainPanel.setLayout(new BorderLayout( ));
      
      // Top
      JPanel top = new JPanel();
      tfPicks.setEditable(false);  // Don't let user change this value manually
      tfPicks.setHorizontalAlignment(JTextField.CENTER); // Center text
      bInstructions.setFocusable(false); // Take away focus lines
      tfMatched.setEditable(false);  // Don't let user change this value manually
      tfMatched.setText("0");  // Start matches at 0
      tfMatched.setHorizontalAlignment(JTextField.CENTER); // Center text
      top.add(labelPicks); // Add elements
      top.add(tfPicks);
      top.add(labelMatched);
      top.add(tfMatched);
      top.add(bInstructions);
      mainPanel.add(top, BorderLayout.PAGE_START); // Add top panel to mainPanel
      
      // Left
      JPanel left = new JPanel();
      left.setPreferredSize(new Dimension(100, 100)); // Set panel size to 100, 100 
      left.add(labelBet); // Add elements
      left.add(cbBet);
      cbBet.setSelectedItem(arrBets[0]); // Starting element is $10
      mainPanel.add(left, BorderLayout.LINE_START); // Add left panel to mainPanel
      
      // Right
      JPanel right = new JPanel();
      right.setPreferredSize(new Dimension(100, 100)); // Set panel size to 100, 100 
      tfWinnings.setEditable(false); // Don't let user change this value manually
      tfWinnings.setText("$0"); // Set starting winnings text to $0
      tfWinnings.setHorizontalAlignment(JTextField.CENTER); // Align text to center
      right.add(labelWinnings); // Add elements
      right.add(tfWinnings);
      mainPanel.add(right, BorderLayout.LINE_END); // Add right panel to mainPanel
      
      // Bottom
      JPanel bottom = new JPanel( ); 
      tfBet.setEditable(false);  // Don't let user change these values manually
      bPlay.setEnabled(false);
      bPlay.setFocusable(false);
      bClear.setFocusable(false);
      bEnd.setFocusable(false);
      tfTotalWon.setEditable(false);
      tfBank.setEditable(false);
      tfTotalWon.setText("$0"); // Set starting text to $0
      tfBet.setText("$10"); // Set starting bet text to $10
      tfBank.setText("$" + String.valueOf(bank));  // Set value to bank of new game or saved game
      tfTotalWon.setHorizontalAlignment(JTextField.CENTER); // Center elements
      tfBet.setHorizontalAlignment(JTextField.CENTER);
      tfBank.setHorizontalAlignment(JTextField.CENTER);
      bottom.add(labelCurBet);  // Add elements
      bottom.add(tfBet);
      bottom.add(labelTotalWon);
      bottom.add(tfTotalWon); 
      bottom.add(bClear);
      bottom.add(bPlay);
      bottom.add(bEnd);
      bottom.add(labelBank);
      bottom.add(tfBank);
      mainPanel.add(bottom, BorderLayout.PAGE_END);   // Add bottom panel to mainPanel
      
      // Center Panel uses Grid Layout  
      JPanel center = new JPanel( );
      center.setLayout(new GridLayout(5,8));
      // Initialize number JButtons: populate arrNumbers[] with buttons numbered 1-40
      for(int i = 0; i < SIZE; i++) { 
         arrNumbers[i] = new JButton(String.valueOf(i+1));  // Label each button with numbers 1-40
         arrNumbers[i].addActionListener(ears);             // Add action listener to each button
         arrNumbers[i].setBackground(Color.cyan);           // Set starting color to cyan
         arrNumbers[i].setFocusable(false);                 // Take away focusable border around number
         arrNumbers[i].setBorder(BorderFactory.createEtchedBorder()); // Add border to button
         center.add(arrNumbers[i]);                         // Add all buttons to center panel
      }
      mainPanel.add(center, BorderLayout.CENTER); // Add center panel to mainPanel
      this.add(mainPanel); // Add mainPanel to Keno Frame    
   }
   
   // Item Listener to change user bet from JComboBox
   private class MyItemListener implements ItemListener {
      @Override
      public void itemStateChanged(ItemEvent iEvent) {
         // Set current bet
         int cbBetIndex = cbBet.getSelectedIndex();               // Assign bet index value
         
         switch(cbBetIndex) {
            case 0:
               currentBet = 10;
               tfBet.setText("$" + String.valueOf(currentBet));  // Set bet to $10
               break;
            case 1:
               currentBet = 20;
               tfBet.setText("$" + String.valueOf(currentBet));  // Set bet to $20
               break;
            case 2:
               currentBet = 50;
               tfBet.setText("$" + String.valueOf(currentBet));  // Set bet to $50
               break;
            case 3:
               currentBet = 100;
               tfBet.setText("$" + String.valueOf(currentBet));  // Set bet to $100
               break;
            default:
               currentBet = 10;
               tfBet.setText("$" + String.valueOf(currentBet));  // Default bet to $10       
               break;
         }
      }      
   }
   
   // Action Listener calls events within Keno when buttons are clicked
   private class MyListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent event){
       
         // Pop-up dialog box for bInstructions
         if(event.getSource() == bInstructions){
            instructions = "<html><style>h1{text-align: center;}</style><h1>~~~~~~~Instructions~~~~~~~</h1></html>\n";
            instructions += "<html><style>p{text-align: center;}</style><p>Begin the game by selected a bet: $10, $20, $50, or $100</p></html>\n";
            instructions += "<html><center>Select 10 different numbers on the Keno Board</center></html>\n";
            instructions += "If you want to change one of your selected numbers just press it again to deselect it\n";
            instructions += "When you have 10 numbers and a bet selected press Play\n";
            instructions += "The computer selects 10 numbers too and if they match with yours you can win some $$$\n";
            instructions += "After each round click Reset to clear your selections\n";
            instructions += "When you are finished playing click Cash Out\n";
            instructions += "<html><font color=#00FFFF>Color1</font></html>";
            opInstructions.showMessageDialog(null, instructions, "Instructions", JOptionPane.PLAIN_MESSAGE);
         }
            
         // Increase tfPicks for every number button selected   
         for(int i = 0; i < SIZE; i++) {
            Color color = arrNumbers[i].getBackground();     // Get current color of button
            if (color == Color.cyan && count < 10) {         // If button is cyan and count is less than 10
               if(event.getSource() == arrNumbers[i]) {      // If button is clicked
                  count++;                                   // Increase count
                  arrNumbers[i].setBackground(Color.blue);   // Change color of button to blue
                  tfPicks.setText(String.valueOf(count));    // Increase value of tfPicks
               }   
            }
            // Decrease tfPicks for every deselected button
            if (color == Color.blue) {                       // If color of button is blue
               if(event.getSource() == arrNumbers[i]) {      // If button is clicked
                  count--;                                   // Decrease count
                  arrNumbers[i].setBackground(Color.cyan);   // Change color back to cyan
                  tfPicks.setText(String.valueOf(count));    // Decrease value of tfPicks
               }
            }
         }
         
         // If 10 buttons pressed enable the bPlay button
         if(count == 10 && currentBet > 0)
            bPlay.setEnabled(true);
         
         // If a number is deselected after picking 10
         if(count < 10)
            bPlay.setEnabled(false);
         
         // If bClear pressed reset count, buttons pressed, tfWinnings, colorCount, and tfMatched
         if(event.getSource() == bClear) {                              // User clicks reset
            count = 0;                                                  // Set count to 0
            tfPicks.setText(String.valueOf(count));                     // Reset tfPicks to count
            cbBet.setSelectedIndex(0);                                  // Reset bet selection to $10
            currentBet = 10;                                            // Reset bet value
            bInstructions.setEnabled(true);                             // Enable Instructions button
            bPlay.setEnabled(false);                                    // Disable play button
            for(JButton number:arrNumbers) {                            // Reset every number button to cyan and Etched border
               number.setBackground(Color.cyan);
               number.setBorder(BorderFactory.createEtchedBorder());
               number.setEnabled(true);
            }
            tfWinnings.setText("$0");                                   // Reset text fields of winnings and matched
            tfMatched.setText("0");
            colorCount = 0;                                             // Reset value of matched
         }
         
         // If you click bPlay 
         if(event.getSource() == bPlay) {
            // Disable bPlay
            bPlay.setEnabled(false);
            bInstructions.setEnabled(false);
            for(int i = 0; i < SIZE; i++) 
               arrNumbers[i].setEnabled(false);
            // Fill arrCompNumbers with random arrNumbers from set
            Set<Integer> set = new Random().ints(MIN, MAX)              // Use set to create random unique values
               .distinct()                                              // Use unique numbers
               .limit(10)                                               // 10 numbers
               .boxed()                                                 // Return stream of elements boxed to Integer
               .collect(Collectors.toSet());                            // Collect elements to set
            Integer[] arrCompNumbers = set.toArray(new Integer[10]);    // Convert set to arr[] arrCompNumbers
            // Loop through arrCompNumbers to change colors of winning numbers
            for(int i = 0; i < arrCompNumbers.length; i++){
               int wNumSelection = arrCompNumbers[i];
               Color color = arrNumbers[wNumSelection-1].getBackground();    // Assign location value of color selected
               // Compare values and change winning button to green and losing button to red
               for(int j = 0; j < SIZE; j++){
                  if(color == Color.blue)
                     arrNumbers[wNumSelection-1].setBackground(Color.green);  // Button was selected by user
                  else
                     arrNumbers[wNumSelection-1].setBackground(Color.red);    // Button not selected by user
               }
            }
            
            // Calculate total matches
            for(int c = 0; c < SIZE; c++){
               Color color = arrNumbers[c].getBackground();                  // Assign location value to color
               if(color == Color.green)                                      // Count number of green buttons assign to colorCount
                  colorCount++;
                  tfMatched.setText(String.valueOf(colorCount));             // Set tfMatched to colorCount value
            }
            // Calculate total Winnings/Losses adjust totals
            winnings = betCalculator(currentBet, colorCount);                // Run betCalculator and return winnings
            if(winnings > 0)
               roundsWon++;                                                  // Increase roundsWon for positive winnings
            else
               roundsLost++;                                                 // Increase roundsLost for negative winnings
            tfWinnings.setText("$" + String.valueOf(winnings));              // Adjust totals of Winnings, totalWon, tfTotalWon, bank, tfBank, and totalRounds
            totalWon = totalWon + winnings;
            tfTotalWon.setText("$" + String.valueOf(totalWon));
            bank = bank + winnings;
            tfBank.setText("$" + String.valueOf(bank));
            totalRounds++;
            if(bank <= 0){                                                   // If bank hits 0 or below end game and tell user they lost
               JOptionPane.showMessageDialog(null, "Your Bank is Empty!", "Loser", JOptionPane.PLAIN_MESSAGE);
               bEnd.dispatchEvent(event);
            }
         }
         // If user selects bEnd
         if(event.getSource() == bEnd){
            // Display JOptionPane for user with game stats
            endBank = bank;
            // Run cashOut which saves bank to KenoSave.txt and receipt to KenoFile.txt
            String sReceipt = "~~~Receipt~~~\n";
            sReceipt += "Rounds Played: " + String.valueOf(totalRounds) + "\n";
            sReceipt += "Starting Bank: $" + String.valueOf(startBank) + "\n";
            sReceipt += "Rounds Won: " + String.valueOf(roundsWon) + "\n";
            sReceipt += "Rounds Lost: " + String.valueOf(roundsLost) + "\n";
            sReceipt += "Total Won: $" + String.valueOf(totalWon) + "\n";
            sReceipt += "Final Bank: $" + String.valueOf(endBank) + "\n";
            sReceipt += "Receipt located next to Keno.java file";
            try{   
               cashOut(bank, sReceipt);
               opReceipt.showMessageDialog(null, sReceipt, "Receipt", JOptionPane.PLAIN_MESSAGE);
               System.exit(0);
            }
            catch(Exception e){
               System.out.println(e);
               return;
            }
         }
      }
   }
   // Calculate winnings from current bet and number of matches
   private int betCalculator(int currentBet, int colorCount) {
      switch(colorCount) {
         case 0:
            winnings = currentBet*2;
            break;
         case 1:
            winnings = currentBet*-1;                  // Lost bet
            break;
         case 2:
            winnings = currentBet*-1;                  // Lost bet
            break;
         case 3:
            winnings = currentBet;
            break;
         case 4:
            winnings = currentBet*2;
            break;
         case 5:
            winnings = currentBet*3;
            break;
         case 6:
            winnings = currentBet*7;
            break;
         case 7:
            winnings = currentBet*30;
            break;
         case 8:
            winnings = currentBet*200;
            break;
         case 9:
            winnings = currentBet*1000;
            break;
         case 10:
            winnings = currentBet*10000;
            break;
         default:
            break;
      } return winnings;
   }
   // Writes save to KenoSave and receipt to KenoFile when user hits cash out or user runs out of bank funds
   private void cashOut(int bank, String sReceipt) throws Exception, IOException{

      try{
         // Export Variables for save game
         File file = new File("KenoSave.txt");
         FileWriter fw = new FileWriter(file);
         BufferedWriter bw = new BufferedWriter(fw);
         PrintWriter pw = new PrintWriter(bw);
         
         pw.print(bank);
         pw.close();  
      }
      catch(Exception e){
         System.out.println(e);
         return;
      }
      
      try{
         // Export Variables for KenoFile receipt
         File kenoFile = new File("KenoFile.txt");
         FileWriter kf = new FileWriter(kenoFile);
         BufferedWriter buffKeno = new BufferedWriter(kf);
         PrintWriter printKeno = new PrintWriter(buffKeno);
         
         printKeno.print(sReceipt);
         printKeno.close();  
      }
      catch(Exception e){
         System.out.println(e);
         return;
      }
   }
   // Main Initializes Keno Frame and sets to visible
   public static void main(String[] arg)throws Exception{
      Keno f = new Keno();
      f.initializeGUI();
      f.setVisible(true);
   }
}
