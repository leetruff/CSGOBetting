import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JTextField;

import com.sun.xml.internal.ws.util.StringUtils;

import Comparators.DoubleComparator;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * 
 * @author Lars
 *
 */
public class MainWindow {

	private ListenController listCtrl;
	
	private JFrame frmCsgoBettingCalculator;
	private JTable table;
	private JTextField txtSuche;
	private ArrayList<Match> matchList;
	private ArrayList<Match> suchList;
	private ArrayList<Match> aktuelleList;

	private JLabel lblTeam;

	private JLabel lblVs;

	private JLabel lblTeam_1;

	private JLabel lblCsgl;

	private JLabel lblCsgl_1;

	private JLabel lblEgb;

	private JLabel lblEgb_1;

	private JLabel lblRecommendedBet;

	private JLabel lblTimeLeftTill;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			/**
			 * Setzt das LAF, finde Nimbus ganz nett
			 */
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmCsgoBettingCalculator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "serial", "deprecation" })
	private void initialize() {
		
		try {
			listCtrl = new ListenController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/**
		 * Das Hauptfenster, hier koennen wir spaeter mit der Aufloesung rumspielen
		 */
		frmCsgoBettingCalculator = new JFrame();
		frmCsgoBettingCalculator.setTitle("CSGO Betting Calculator V0.1");
		frmCsgoBettingCalculator.setBounds(100, 100, 1700, 956);
		frmCsgoBettingCalculator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCsgoBettingCalculator.getContentPane().setLayout(null);
		
		/**
		 * Scrollpane fuer unsere Tabelle
		 */
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(34, 109, 1189, 792);
		frmCsgoBettingCalculator.getContentPane().add(scrollPane);
		
		/**
		 * Table mit unseren Matches
		 */
		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				table.repaint();
				showMatchInformation();
			}
		});
		scrollPane.setViewportView(table);
		
		/**
		 * Eintraege der Tabelle
		 */
		table.setModel(new DefaultTableModel(
			new Object[][] {},
			new String[] {
				"Winner", "Team 1", "Team 1 Odds", "Team 2 Odds", "Team 2", "Matchtype", "Event", "Zeit"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, false, true, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		
		/**
		 * Setzen der Spaltenbreite (hehehe)
		 */
		table.getColumnModel().getColumn(0).setPreferredWidth(115);
		table.getColumnModel().getColumn(1).setPreferredWidth(75);
		table.getColumnModel().getColumn(5).setPreferredWidth(25);
		table.getColumnModel().getColumn(6).setPreferredWidth(100);
		
		
		/**
		 * TeamTableCellRenderer fuer Team 1 setzen
		 */
		table.getColumnModel().getColumn(1).setCellRenderer(new Team1TableCellRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(new Team1TableCellRenderer());
		
		/**
		 * TeamTableCellRenderer fuer Team 2 setzen
		 */
		table.getColumnModel().getColumn(3).setCellRenderer(new Team2TableCellRenderer());
		table.getColumnModel().getColumn(4).setCellRenderer(new Team2TableCellRenderer());
		
		/**
		 * Column mit Winnerindex [-1,0,1,2] ist noch im Table enthalten,
		 * wird jedoch hier ausgeblendet mit remove() 
		 */
		table.getColumnModel().removeColumn(table.getColumn("Winner"));
		
		/**
		 * Sortiert unsere Spalten
		 */
		//table.setAutTableModelSorter(true);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		sorter.setComparator(2, new DoubleComparator());
		sorter.setComparator(3, new DoubleComparator());
		table.setRowSorter(sorter);
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		/**
		 * Liste von <Matches> aus dem ListenController beziehen
		 */
		matchList = listCtrl.getMatches();
		aktuelleList = matchList;
		
		
		/**
		 * Tabelle mit den Eintraegen aus der Match Liste befuellen. Matches, welche Odds von 0 enthalten werden nicht
		 * mit aufgenommen, da diese offensichtlich nicht relevant sind. 
		 */
		for(int i = aktuelleList.size()-1; i >= 0; i--){
			
			if(!(Integer.parseInt(aktuelleList.get(i).getTeam1LoungeOdds()) == 0 || Integer.parseInt(aktuelleList.get(i).getTeam1LoungeOdds()) == 0)){
				double team1 = Double.parseDouble(aktuelleList.get(i).getTeam1LoungeOdds());
				double team2 = Double.parseDouble(aktuelleList.get(i).getTeam2LoungeOdds());
				
				/**
				 * Odds als prozentuale Werte berechnen
				 */
				double team1Odds = team1 / (team1 + team2) *100;
				double team2Odds = team2 / (team1 + team2) *100;
				
				/**
				 * Hier wird bestimmt, auf wieviele Nachkommastellen wir die Odds runden. Fuer mehr Nachkommestellen einfach
				 * die Anzahl der 0 in der Rechnung auf beiden Seiten erhoehen.
				 */
				team1Odds = Math.round(team1Odds * 100) / 100.0;
				team2Odds = Math.round(team2Odds * 100) / 100.0;

				/**
				 * Tatsaechliches hinzufuegen der Matches in die Tabelle
				 */
				model.addRow(new Object[]{aktuelleList.get(i).getWinner(), aktuelleList.get(i).getTeam1Name(), team1Odds+"%",
					team2Odds+"%", aktuelleList.get(i).getTeam2Name(),"BO" + aktuelleList.get(i).getMatchType(), aktuelleList.get(i).getEventName(),
					aktuelleList.get(i).getDatum().toGMTString()});
				}
		}
		
		
		/**
		 * Knopf, welcher alle aktuellen Matches updated
		 */
		JButton btnUpdateAll = new JButton("Update All");
		btnUpdateAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Gesamte Liste updaten, wir sollten dazu die Launcher.java nicht als static main benutzen,
				//sondern uns Objekte davon erzeugen.
			}
		});
		btnUpdateAll.setBounds(34, 34, 107, 45);
		frmCsgoBettingCalculator.getContentPane().add(btnUpdateAll);
		
		/**
		 * Panel am rechten Rand mit Matchinformationen
		 */
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBounds(1256, 109, 422, 792);
		frmCsgoBettingCalculator.getContentPane().add(panel);
		panel.setLayout(null);
		
		/**
		 * Knopf, welcher nur das aktuell ausgewaehlte Match updated (zwecks Performance)
		 */
		JButton btnUpdateThis = new JButton("Update this");
		btnUpdateThis.setBounds(325, 6, 91, 28);
		panel.add(btnUpdateThis);
		
		lblTeam = new JLabel("Team 1");
		lblTeam.setBounds(40, 43, 91, 16);
		panel.add(lblTeam);
		
		lblVs = new JLabel("vs.");
		lblVs.setBounds(160, 43, 55, 16);
		panel.add(lblVs);
		
		lblTeam_1 = new JLabel("Team 2");
		lblTeam_1.setBounds(287, 43, 91, 16);
		panel.add(lblTeam_1);
		
		lblCsgl = new JLabel("CSGL:");
		lblCsgl.setBounds(40, 101, 103, 16);
		panel.add(lblCsgl);
		
		lblCsgl_1 = new JLabel("CSGL:");
		lblCsgl_1.setBounds(287, 101, 91, 16);
		panel.add(lblCsgl_1);
		
		lblEgb = new JLabel("EGB:");
		lblEgb.setBounds(40, 129, 117, 16);
		panel.add(lblEgb);
		
		lblEgb_1 = new JLabel("EGB:");
		lblEgb_1.setBounds(287, 129, 91, 16);
		panel.add(lblEgb_1);
		
		lblRecommendedBet = new JLabel("Recommended bet:\r\n");
		lblRecommendedBet.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblRecommendedBet.setBounds(40, 331, 243, 28);
		panel.add(lblRecommendedBet);
		
		lblTimeLeftTill = new JLabel("Time left till match start:\r\n");
		lblTimeLeftTill.setBounds(40, 236, 175, 16);
		panel.add(lblTimeLeftTill);
		
		JButton btnOpenInBrowser = new JButton("Open in Browser");
		btnOpenInBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openWebPage("http://csgolounge.com/match?m=" + getMarkedMatchup().getLoungeID());
			}
		});
		btnOpenInBrowser.setBounds(263, 748, 153, 38);
		panel.add(btnOpenInBrowser);
		
		/**
		 * Suchfeld um Matcharchiv nach Stichworten zu durchsuchen
		 */
		txtSuche = new JTextField();
		txtSuche.setText("Suche...");
		txtSuche.setBounds(1007, 69, 151, 28);
		frmCsgoBettingCalculator.getContentPane().add(txtSuche);
		txtSuche.setColumns(10);
		
		txtSuche.setToolTipText("<html> Eingabefeld für Stichworte. <br> Matchup Syntax: Team1 + Team2 "
				+ "<br> Match auf Event Syntax: Team1 + Team2 + Event </html>");
		
		/**
		 * Startet die Suche
		 */
		JButton btnNewButton = new JButton("go!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createSuchList(txtSuche.getText());
				//table.repaint();
			}

		});
		
		btnNewButton.setBounds(1170, 69, 53, 28);
		frmCsgoBettingCalculator.getContentPane().add(btnNewButton);
		
		/**
		 * Wenn ins Suchfeld geklickt wird, wird der Inhalt geloescht, jedoch nur falls bisher
		 * nichts eingegeben wurde
		 */
        txtSuche.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(txtSuche.getText().equals("Suche...")){
                   txtSuche.setText(null);
                }
            }
        });
        
        /**
         * Emuliert einen Klick auf den Suchbutton
         */
        txtSuche.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		btnNewButton.doClick();
        	}
        });
        
        /**
         * Setzt den Text des Feldes wieder auf "Suche...",
         * falls nichts eingegeben wurde und man woanders hinklickt
         */
        txtSuche.addFocusListener(new FocusAdapter() {
        	@Override
        	public void focusLost(FocusEvent arg0) {
        		if(txtSuche.getText().equals("")){
        			txtSuche.setText("Suche...");
        		}
        	}
        });
        
	}
	
	/**
	 * Erstellt die Suchliste
	 * @param text Suchbegriffe
	 */
	protected void createSuchList(String text) {
		

		int count = 0;
		
		for (int i = 0; i < text.length(); i++) {
		    if (text.charAt(i) == '+') {
		        count++;
		    }
		    
		}
		
		if(text.equals("") || text == null || text.equals("Suche...")){
			aktuelleList = listCtrl.getMatches();
		}
		
		else if(count == 2){
			ArrayList<Match> suchListe = listCtrl.erwSuchListeEvent(text.split(" "));
			aktuelleList = suchListe;
		}
		
		else if(text.contains("+")){
			ArrayList<Match> suchListe = listCtrl.erwSuchListe(text.split(" "));
			aktuelleList = suchListe;
		}
		
		else{
			ArrayList<Match> suchListe = listCtrl.einfSuchListe(text.split(" "));
			aktuelleList = suchListe;
		}
		
		updateTable();
	}

	@SuppressWarnings("serial")
	private void updateTable() {
		table.setModel(new DefaultTableModel(
				new Object[][] {},
				new String[] {
					"Winner", "Team 1", "Team 1 Odds", "Team 2 Odds", "Team 2", "Matchtype", "Event", "Zeit"
				}
			) {
				boolean[] columnEditables = new boolean[] {
					false, false, false, false, false, true, false, false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			});
		
//            table.getColumnModel().getColumn(0).setPreferredWidth(50);
//            table.getColumnModel().getColumn(1).setPreferredWidth(296);
//            table.getColumnModel().getColumn(2).setPreferredWidth(134);
//            table.getColumnModel().getColumn(3).setPreferredWidth(93);
//            table.getColumnModel().getColumn(4).setPreferredWidth(95);
            
            
            
    		/**
    		 * Setzen der Spaltenbreite (hehehe)
    		 */
    		table.getColumnModel().getColumn(0).setPreferredWidth(115);
    		table.getColumnModel().getColumn(1).setPreferredWidth(75);
    		table.getColumnModel().getColumn(5).setPreferredWidth(25);
    		table.getColumnModel().getColumn(6).setPreferredWidth(100);
    		
    		
    		/**
    		 * TeamTableCellRenderer fuer Team 1 setzen
    		 */
    		table.getColumnModel().getColumn(1).setCellRenderer(new Team1TableCellRenderer());
    		table.getColumnModel().getColumn(2).setCellRenderer(new Team1TableCellRenderer());
    		
    		/**
    		 * TeamTableCellRenderer fuer Team 2 setzen
    		 */
    		table.getColumnModel().getColumn(3).setCellRenderer(new Team2TableCellRenderer());
    		table.getColumnModel().getColumn(4).setCellRenderer(new Team2TableCellRenderer());
    		
    		/**
    		 * Column mit Winnerindex [-1,0,1,2] ist noch im Table enthalten,
    		 * wird jedoch hier ausgeblendet mit remove() 
    		 */
    		table.getColumnModel().removeColumn(table.getColumn("Winner"));
    		
    		/**
    		 * Sortiert unsere Spalten
    		 */
    		//table.setAutTableModelSorter(true);
    		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
    		sorter.setComparator(2, new DoubleComparator());
    		sorter.setComparator(3, new DoubleComparator());
    		table.setRowSorter(sorter);
            
            
            
        	
        	DefaultTableModel model = (DefaultTableModel) table.getModel();
        	
    		/**
    		 * Tabelle mit den Eintraegen aus der Match Liste befuellen. Matches, welche Odds von 0 enthalten werden nicht
    		 * mit aufgenommen, da diese offensichtlich nicht relevant sind. 
    		 */
    		for(int i = aktuelleList.size()-1; i >= 0; i--){
    			
    			if(!(Integer.parseInt(aktuelleList.get(i).getTeam1LoungeOdds()) == 0 || Integer.parseInt(aktuelleList.get(i).getTeam1LoungeOdds()) == 0)){
    				double team1 = Double.parseDouble(aktuelleList.get(i).getTeam1LoungeOdds());
    				double team2 = Double.parseDouble(aktuelleList.get(i).getTeam2LoungeOdds());
    				
    				/**
    				 * Odds als prozentuale Werte berechnen
    				 */
    				double team1Odds = team1 / (team1 + team2) *100;
    				double team2Odds = team2 / (team1 + team2) *100;
    				
    				/**
    				 * Hier wird bestimmt, auf wieviele Nachkommastellen wir die Odds runden. Fuer mehr Nachkommestellen einfach
    				 * die Anzahl der 0 in der Rechnung auf beiden Seiten erhoehen.
    				 */
    				team1Odds = Math.round(team1Odds * 100) / 100.0;
    				team2Odds = Math.round(team2Odds * 100) / 100.0;

    				/**
    				 * Tatsaechliches hinzufuegen der Matches in die Tabelle
    				 */
    				model.addRow(new Object[]{aktuelleList.get(i).getWinner(), aktuelleList.get(i).getTeam1Name(), team1Odds+"%",
    					team2Odds+"%", aktuelleList.get(i).getTeam2Name(),"BO" + aktuelleList.get(i).getMatchType(), aktuelleList.get(i).getEventName(),
    					aktuelleList.get(i).getDatum().toGMTString()});
    				}
    		}
	}

	/**
	 * Liest aus, welches Match gerade in der Tabelle markiert ist.
	 * @return Das aktuelle Match Objekt
	 */
	private Match getMarkedMatchup(){

		int index = table.getRowSorter().convertRowIndexToModel(table.getSelectedRow());
		
		return aktuelleList.get(aktuelleList.size()-1 - index);
	}
	
	/**
	 * Aktualisiert die Informationen in dem rechten Panel
	 */
	private void showMatchInformation(){
		Match match = getMarkedMatchup();
		
		lblTeam.setText(match.getTeam1Name());
		lblTeam_1.setText(match.getTeam2Name());
		
		double team1 = Double.parseDouble(match.getTeam1LoungeOdds());
		double team2 = Double.parseDouble(match.getTeam2LoungeOdds());
		
		/**
		 * Odds als prozentuale Werte berechnen
		 */
		double team1Odds = team1 / (team1 + team2) *100;
		double team2Odds = team2 / (team1 + team2) *100;
		
		/**
		 * Hier wird bestimmt, auf wieviele Nachkommastellen wir die Odds runden. Fuer mehr Nachkommestellen einfach
		 * die Anzahl der 0 in der Rechnung auf beiden Seiten erhoehen.
		 */
		team1Odds = Math.round(team1Odds * 100) / 100.0;
		team2Odds = Math.round(team2Odds * 100) / 100.0;
		
		lblCsgl.setText("CSGL: " + team1Odds + "%");
		lblCsgl_1.setText("CSGL: " + team2Odds + "%");
		
	}
	
	public void openWebPage(String url){
		   try {         
		     java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		   }
		   catch (java.io.IOException e) {
		       System.out.println(e.getMessage());
		   }
		}
}
