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
import javax.swing.JTextField;

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

		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		/**
		 * Liste von <Matches> aus dem ListenController beziehen
		 */
		ArrayList<Match> matchList = listCtrl.getMatches();
		
		
		
		/**
		 * Tabelle mit den Eintraegen aus der Match Liste befuellen. Matches, welche Odds von 0 enthalten werden nicht
		 * mit aufgenommen, da diese offensichtlich nicht relevant sind. 
		 */
		for(int i = matchList.size()-1; i >= 0; i--){
			
			if(!(Integer.parseInt(matchList.get(i).getTeam1LoungeOdds()) == 0 || Integer.parseInt(matchList.get(i).getTeam1LoungeOdds()) == 0)){
				double team1 = Double.parseDouble(matchList.get(i).getTeam1LoungeOdds());
				double team2 = Double.parseDouble(matchList.get(i).getTeam2LoungeOdds());
				
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
				model.addRow(new Object[]{matchList.get(i).getWinner(), matchList.get(i).getTeam1Name(), team1Odds+"%",
					team2Odds+"%", matchList.get(i).getTeam2Name(),"BO" + matchList.get(i).getMatchType(), matchList.get(i).getEventName(),
					matchList.get(i).getDatum().toGMTString()});
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
		
		/**
		 * Label im rechten Panel fuer Team 1 Name
		 */
		JLabel lblTeam = new JLabel("Team 1");
		lblTeam.setBounds(40, 43, 55, 16);
		panel.add(lblTeam);
		
		/**
		 * Rein optisches, nicht funktionales Label, wird nie veraendert
		 */
		JLabel lblVs = new JLabel("vs.");
		lblVs.setBounds(160, 43, 55, 16);
		panel.add(lblVs);
		
		/**
		 * Label im rechten Panel fuer Team 2 Name
		 */
		JLabel lblTeam_1 = new JLabel("Team 2");
		lblTeam_1.setBounds(287, 43, 55, 16);
		panel.add(lblTeam_1);
		
		/**
		 * CSGL Odds fuer Team 1
		 */
		JLabel lblCsgl = new JLabel("CSGL:");
		lblCsgl.setBounds(40, 101, 55, 16);
		panel.add(lblCsgl);
		
		/**
		 * CSGL Odds fuer Team 2
		 */
		JLabel lblCsgl_1 = new JLabel("CSGL:");
		lblCsgl_1.setBounds(287, 101, 55, 16);
		panel.add(lblCsgl_1);
		
		/**
		 * EGB Odds fuer Team 1
		 */
		JLabel lblEgb = new JLabel("EGB:");
		lblEgb.setBounds(40, 129, 55, 16);
		panel.add(lblEgb);
		
		/**
		 * EGB Odds fuer Team 2
		 */
		JLabel lblEgb_1 = new JLabel("EGB:");
		lblEgb_1.setBounds(287, 129, 55, 16);
		panel.add(lblEgb_1);
		
		/**
		 * RecommendetBet nach Kelly Formel, entweder hier den Labeltext setzen oder ein neues
		 * Sublabel erstellen
		 */
		JLabel lblRecommendedBet = new JLabel("Recommended bet:\r\n");
		lblRecommendedBet.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblRecommendedBet.setBounds(40, 331, 243, 28);
		panel.add(lblRecommendedBet);
		
		/**
		 * Restliche Zeit bis Matchbeginn
		 */
		JLabel lblTimeLeftTill = new JLabel("Time left till match start:\r\n");
		lblTimeLeftTill.setBounds(40, 236, 175, 16);
		panel.add(lblTimeLeftTill);
		
		/**
		 * Suchfeld um Matcharchiv nach Stichworten zu durchsuchen
		 */
		txtSuche = new JTextField();
		txtSuche.setText("Suche...");
		txtSuche.setBounds(1007, 69, 151, 28);
		frmCsgoBettingCalculator.getContentPane().add(txtSuche);
		txtSuche.setColumns(10);
		
		txtSuche.setToolTipText("Eingabefeld für Stichworte");
		
		/**
		 * Startet die Suche
		 */
		JButton btnNewButton = new JButton("go!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Suchfeld auslesen, und entsprechende Ergebnisliste in Tabelle
				//anzeigen
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
	 * Liest aus, welches Match gerade in der Tabelle markiert ist.
	 * @return Das aktuelle Match Objekt
	 */
	private Match getMarkedMatchup(){
		//TODO
		return null;
	}
}
