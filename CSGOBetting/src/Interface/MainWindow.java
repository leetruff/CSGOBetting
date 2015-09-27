package Interface;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import Comparators.DoubleComparator;
import Controller.ListenController;
import MatchInformation.Match;
import Renderer.Team1TableCellRenderer;
import Renderer.Team2TableCellRenderer;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

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
	private ArrayList<Match> loungeMatchList;
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

	private JButton btnNewButton;
	private JButton btnDevtools;
	private JComboBox comboBox;


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
	 * @throws IOException 
	 */
	public MainWindow() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	@SuppressWarnings({ "serial", "deprecation", "rawtypes", "unchecked" })
	private void initialize() throws IOException {
		
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
		frmCsgoBettingCalculator.setBounds(100, 100, 1280, 720);
		frmCsgoBettingCalculator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/**
		 * Scrollpane fuer unsere Tabelle
		 */
		frmCsgoBettingCalculator.getContentPane().setLayout(new MigLayout("", "[83.00px][88.00][-18.00][318.00px][151px][61.00px][127.00px][468.00px]", "[63px][824.00px]"));
		
		btnDevtools = new JButton("DevTools");
		btnDevtools.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DevFrame devFrame = new DevFrame();
				devFrame.setVisible(true);
			}
		});
		frmCsgoBettingCalculator.getContentPane().add(btnDevtools, "cell 1 0");
		
		
		txtSuche = new JTextField();
		txtSuche.setText("Suche...");
		frmCsgoBettingCalculator.getContentPane().add(txtSuche, "cell 4 0,growx,aligny bottom");
		txtSuche.setColumns(10);
		
		txtSuche.setToolTipText("<html> Eingabefeld für Stichworte. <br> Matchup Syntax: Team1 + Team2 "
				+ "<br> Match auf Event Syntax: Team1 + Team2 + Event </html>");
		txtSuche.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if(txtSuche.getText().equals("Suche...")){
		           txtSuche.setText(null);
		        }
		    }
		});
		txtSuche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton.doClick();
			}
		});
		txtSuche.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if(txtSuche.getText().equals("")){
					txtSuche.setText("Suche...");
				}
			}
		});
		
		btnNewButton = new JButton("go!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createSuchList(txtSuche.getText());
				//table.repaint();
			}

		});
		frmCsgoBettingCalculator.getContentPane().add(btnNewButton, "cell 5 0,alignx center,aligny bottom");
		JScrollPane scrollPane = new JScrollPane();
		frmCsgoBettingCalculator.getContentPane().add(scrollPane, "cell 0 1 7 1,grow");
		
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
		loungeMatchList = listCtrl.getLoungeMatches();
		aktuelleList = listCtrl.getBothMatches();
		
		comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				/**
				 * Auslesen, was in der Combobox ausgewaehlt ist und entsprechend die aktuelleListe setzen
				 */
				String currentItem = (String) comboBox.getSelectedItem();
				switch (currentItem) {
				case "CSGL + EGB":
					try {
						aktuelleList = listCtrl.getBothMatches();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					listCtrl.setAktuelleList(aktuelleList);
					updateTable();
					break;
					
				case "CSGL + EGB Archive":
					try {
						aktuelleList = listCtrl.getBothMatchesArchive();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					listCtrl.setAktuelleList(aktuelleList);
					updateTable();
					break;
					
				case "CSGL":
					aktuelleList = listCtrl.getLoungeMatches();
					listCtrl.setAktuelleList(aktuelleList);
					updateTable();
					break;
					
					
				case "EGB":
					aktuelleList = listCtrl.getEGBMatches();
					listCtrl.setAktuelleList(aktuelleList);
					updateTable();
					break;

				default:
					break;
				}
				
			}
		});
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"CSGL + EGB", "CSGL + EGB Archive", "CSGL", "EGB"}));
		frmCsgoBettingCalculator.getContentPane().add(comboBox, "cell 2 0 2 1,alignx left");
		
		/**
		 * Auslesen, was in der Combobox ausgewaehlt ist und entsprechend die aktuelleListe setzen
		 */
		String currentItem = (String) comboBox.getSelectedItem();
		switch (currentItem) {
		case "CSGL + EGB":
			aktuelleList = listCtrl.getBothMatches();
			listCtrl.setAktuelleList(aktuelleList);
			updateTable();
			break;
			
		case "CSGL + EGB Archive":
			aktuelleList = listCtrl.getBothMatchesArchive();
			listCtrl.setAktuelleList(aktuelleList);
			updateTable();
			break;
			
		case "CSGL":
			aktuelleList = listCtrl.getLoungeMatches();
			listCtrl.setAktuelleList(aktuelleList);
			updateTable();
			break;
			
		case "EGB":
			aktuelleList = listCtrl.getEGBMatches();
			listCtrl.setAktuelleList(aktuelleList);
			updateTable();
			break;

		default:
			break;
		}
		
		
		/**
		 * Tabelle mit den Eintraegen aus der Match Liste befuellen. Matches, welche Odds von 0 enthalten werden nicht
		 * mit aufgenommen, da diese offensichtlich nicht relevant sind. 
		 */
		for(int i = aktuelleList.size()-1; i >= 0; i--){
			
			if(!(Integer.parseInt(aktuelleList.get(i).getTeam1Odds()) == 0 || Integer.parseInt(aktuelleList.get(i).getTeam1Odds()) == 0)){
				double team1 = Double.parseDouble(aktuelleList.get(i).getTeam1Odds());
				double team2 = Double.parseDouble(aktuelleList.get(i).getTeam2Odds());
				
				double team1Odds;
				double team2Odds;
				/**
				 * Odds als prozentuale Werte berechnen
				 */
				if(!aktuelleList.get(i).getTeam1Odds().contains(".")){
					team1Odds = team1 / (team1 + team2) *100;
					team2Odds = team2 / (team1 + team2) *100;
				}
				
				else{
					team1Odds = team2 / (team1 + team2) *100;
					team2Odds = team1 / (team1 + team2) *100;
				}
				
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
		frmCsgoBettingCalculator.getContentPane().add(btnUpdateAll, "cell 0 0,growx,aligny center");
		
		/**
		 * Panel am rechten Rand mit Matchinformationen
		 */
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		frmCsgoBettingCalculator.getContentPane().add(panel, "cell 7 1,grow");
		
		/**
		 * Knopf, welcher nur das aktuell ausgewaehlte Match updated (zwecks Performance)
		 */
		panel.setLayout(new MigLayout("", "[117px][][3px][55px][][48px][20px][4px][129px]", "[28px][16px][16px][16px][16px][28px][38px][][][][][385.00]"));
		JButton btnUpdateThis = new JButton("Update this");
		panel.add(btnUpdateThis, "cell 8 0,alignx right,aligny top");
		
		lblTeam = new JLabel("Team 1");
		panel.add(lblTeam, "flowx,cell 0 1,growx,aligny top");
		
		lblTeam_1 = new JLabel("Team 2");
		panel.add(lblTeam_1, "cell 8 1,growx,aligny top");
		
		lblCsgl = new JLabel("CSGL:");
		panel.add(lblCsgl, "cell 0 3,growx,aligny top");
		
		lblCsgl_1 = new JLabel("CSGL:");
		panel.add(lblCsgl_1, "cell 8 3,growx,aligny top");
		
		lblEgb = new JLabel("EGB:");
		panel.add(lblEgb, "cell 0 5,growx,aligny top");
		
		lblEgb_1 = new JLabel("EGB:");
		panel.add(lblEgb_1, "cell 8 5,growx,aligny top");
		
		lblTimeLeftTill = new JLabel("Time left till match start:\r\n");
		panel.add(lblTimeLeftTill, "cell 0 7 5 1,growx,aligny top");
		
		JButton btnOpenInBrowser = new JButton("Open in Browser");
		btnOpenInBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openWebPage("http://csgolounge.com/match?m=" + getMarkedMatchup().getID());
			}
		});
		
		lblRecommendedBet = new JLabel("Recommended bet:\r\n");
		lblRecommendedBet.setFont(new Font("SansSerif", Font.BOLD, 22));
		panel.add(lblRecommendedBet, "cell 0 10,growx,aligny center");
		panel.add(btnOpenInBrowser, "cell 8 11,growx,aligny bottom");
		
		lblVs = new JLabel("vs.");
		panel.add(lblVs, "cell 0 1,alignx center,aligny top");
		
		
		/**
		 * Suchfeld um Matcharchiv nach Stichworten zu durchsuchen
		 */
		
		/**
		 * Wenn ins Suchfeld geklickt wird, wird der Inhalt geloescht, jedoch nur falls bisher
		 * nichts eingegeben wurde
		 */
        
        /**
         * Emuliert einen Klick auf den Suchbutton
         */
        
        /**
         * Setzt den Text des Feldes wieder auf "Suche...",
         * falls nichts eingegeben wurde und man woanders hinklickt
         */
        
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
		
		/**
		 * Falls nichts eingegeben wurde, soll die normale Liste angezeigt werden, welche in der comboBox ausgewaehlt wurde
		 */
		if(text.equals("") || text == null || text.equals("Suche...")){
			
			String currentItem = (String) comboBox.getSelectedItem();
			switch (currentItem) {
			case "CSGL + EGB":
				try {
					aktuelleList = listCtrl.getBothMatches();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				listCtrl.setAktuelleList(aktuelleList);
				updateTable();
				break;
				
			case "CSGL + EGB Archive":
				try {
					aktuelleList = listCtrl.getBothMatchesArchive();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				listCtrl.setAktuelleList(aktuelleList);
				updateTable();
				break;
				
			case "CSGL":
				aktuelleList = listCtrl.getLoungeMatches();
				listCtrl.setAktuelleList(aktuelleList);
				updateTable();
				break;
				
			case "EGB":
				aktuelleList = listCtrl.getEGBMatches();
				listCtrl.setAktuelleList(aktuelleList);
				updateTable();
				break;

			default:
				break;
			}
			
		}
		
		else if(count == 2){
			ArrayList<Match> suchListe = listCtrl.erwSuchListeEvent(text.split(" "));
			aktuelleList = suchListe;
			listCtrl.setAktuelleList(aktuelleList);
		}
		
		else if(text.contains("+")){
			ArrayList<Match> suchListe = listCtrl.erwSuchListe(text.split(" "));
			aktuelleList = suchListe;
			listCtrl.setAktuelleList(aktuelleList);
		}
		
		else{
			ArrayList<Match> suchListe = listCtrl.einfSuchListe(text.split(" "));
			aktuelleList = suchListe;
			listCtrl.setAktuelleList(aktuelleList);
		}
		
		updateTable();
	}

	/**
	 * Updated unsere Tabelle, nachdem wir eine Suchanfrage gestartet haben oder die anzuzeigende Liste veraendern
	 */
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
    			
    			if(!(aktuelleList.get(i).getTeam1Odds().startsWith("0") || aktuelleList.get(i).getTeam2Odds().startsWith("0"))){
    				double team1 = Double.parseDouble(aktuelleList.get(i).getTeam1Odds());
    				double team2 = Double.parseDouble(aktuelleList.get(i).getTeam2Odds());
    				
    				double team1Odds;
    				double team2Odds;
    				/**
    				 * Odds als prozentuale Werte berechnen
    				 */
    				if(!aktuelleList.get(i).getTeam1Odds().contains(".")){
    					team1Odds = team1 / (team1 + team2) *100;
    					team2Odds = team2 / (team1 + team2) *100;
    				}
    				
    				else{
    					team1Odds = team2 / (team1 + team2) *100;
    					team2Odds = team1 / (team1 + team2) *100;
    				}
    				
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
		
		double team1 = Double.parseDouble(match.getTeam1Odds());
		double team2 = Double.parseDouble(match.getTeam2Odds());
		
		/**
		 * Odds als prozentuale Werte berechnen
		 */
		double team1Odds = team1 / (team1 + team2) *100;
		double team2Odds = team2 / (team1 + team2) *100;
		
		/**
		 * Hier wird bestimmt, auf wieviele Nachkommastellen wir die Odds runden. Fuer mehr Nachkommastellen einfach
		 * die Anzahl der 0 in der Rechnung auf beiden Seiten erhoehen.
		 */
		team1Odds = Math.round(team1Odds * 100) / 100.0;
		team2Odds = Math.round(team2Odds * 100) / 100.0;
		
		lblCsgl.setText("CSGL: " + team1Odds + "%");
		lblCsgl_1.setText("CSGL: " + team2Odds + "%");
		
		lblEgb.setText("EGB: ");
		lblEgb_1.setText("EGB: ");
		
		String item = (String) comboBox.getSelectedItem();
		if(item.equals("EGB")){
			
			lblCsgl.setText("CSGL: " );
			lblCsgl_1.setText("CSGL: ");
			
			lblEgb.setText("EGB: " + team2Odds + "%");
			lblEgb_1.setText("EGB: " + team1Odds + "%");
		}
		
		/**
		 * Wird nur aufgerufen, wenn das aktuelle Match ein relatiertes EGB Match hat
		 */
		if(match.getRelatedEGBMatch() != null){
			
			double team1EGB = Double.parseDouble(match.getRelatedEGBMatch().getTeam1Odds());
			double team2EGB = Double.parseDouble(match.getRelatedEGBMatch().getTeam2Odds());
			
			/**
			 * Odds als prozentuale Werte berechnen
			 */

			double team1OddsEGB;
			double team2OddsEGB;
			
			if(match.getRelatedEGBMatch().isSwitched){
				team1OddsEGB = team1EGB / (team1EGB + team2EGB) *100;
				team2OddsEGB = team2EGB / (team1EGB + team2EGB) *100;
			}
			
			else{
				team1OddsEGB = team2EGB / (team1EGB + team2EGB) *100;
				team2OddsEGB = team1EGB / (team1EGB + team2EGB) *100;
			}
			
			/**
			 * Hier wird bestimmt, auf wieviele Nachkommastellen wir die Odds runden. Fuer mehr Nachkommastellen einfach
			 * die Anzahl der 0 in der Rechnung auf beiden Seiten erhoehen.
			 */
			team1OddsEGB = Math.round(team1OddsEGB * 100) / 100.0;
			team2OddsEGB = Math.round(team2OddsEGB * 100) / 100.0;
			
			lblEgb.setText("EGB: " + team1OddsEGB + "%");
			lblEgb_1.setText("EGB: " + team2OddsEGB + "%");
			
		}
		

		
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
