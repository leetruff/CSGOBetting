import java.awt.EventQueue;
import java.awt.Font;
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

/**
 * 
 * @author Lars
 *
 */
public class MainWindow {

	private ListenController listCtrl;
	
	private JFrame frmCsgoBettingCalculator;
	private JTable table;


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
		
		frmCsgoBettingCalculator = new JFrame();
		frmCsgoBettingCalculator.setTitle("CSGO Betting Calculator V0.1");
		frmCsgoBettingCalculator.setBounds(100, 100, 1700, 956);
		frmCsgoBettingCalculator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCsgoBettingCalculator.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(34, 109, 1189, 792);
		frmCsgoBettingCalculator.getContentPane().add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		/**
		 * Eintraege der Tabelle, initialisierung mit null-values damit Scrollbalken angezeigt wird
		 */
		table.setModel(new DefaultTableModel(
			new Object[][] {},
			new String[] {
				"Team 1", "Team 1 Odds", "Team 2 Odds", "Team 2", "Matchtype", "Event", "Zeit"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, true, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(115);
		table.getColumnModel().getColumn(1).setPreferredWidth(127);
		table.getColumnModel().getColumn(5).setPreferredWidth(105);
		table.getColumnModel().getColumn(6).setPreferredWidth(127);
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		ArrayList<Match> matchList = listCtrl.getMatches();
		
		for(int i = 0; i < matchList.size(); i++){
			
			if(!(Integer.parseInt(matchList.get(i).getTeam1LoungeOdds()) == 0 || Integer.parseInt(matchList.get(i).getTeam1LoungeOdds()) == 0)){
				double team1 = Double.parseDouble(matchList.get(i).getTeam1LoungeOdds());
				double team2 = Double.parseDouble(matchList.get(i).getTeam2LoungeOdds());
				
				double team1Odds = team1 / (team1 + team2) *100;
				double team2Odds = team2 / (team1 + team2) *100;
				
				team1Odds = Math.round(team1Odds * 100) / 100.0;
				team2Odds = Math.round(team2Odds * 100) / 100.0;

				
				model.addRow(new Object[]{matchList.get(i).getTeam1Name(), team1Odds+"%",
					team2Odds+"%", matchList.get(i).getTeam2Name(),"BO" + matchList.get(i).getMatchType(), matchList.get(i).getEventName(), matchList.get(i).getDatum().toGMTString()});
				}
		}
		
		
		/**
		 * Knopf, welcher alle aktuellen Matches updated
		 */
		JButton btnUpdateAll = new JButton("Update All");
		btnUpdateAll.setBounds(34, 34, 107, 45);
		frmCsgoBettingCalculator.getContentPane().add(btnUpdateAll);
		
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
		
		JLabel lblTeam = new JLabel("Team 1");
		lblTeam.setBounds(40, 43, 55, 16);
		panel.add(lblTeam);
		
		JLabel lblVs = new JLabel("vs.");
		lblVs.setBounds(160, 43, 55, 16);
		panel.add(lblVs);
		
		JLabel lblTeam_1 = new JLabel("Team 2");
		lblTeam_1.setBounds(287, 43, 55, 16);
		panel.add(lblTeam_1);
		
		JLabel lblCsgl = new JLabel("CSGL:");
		lblCsgl.setBounds(40, 101, 55, 16);
		panel.add(lblCsgl);
		
		JLabel lblCsgl_1 = new JLabel("CSGL:");
		lblCsgl_1.setBounds(287, 101, 55, 16);
		panel.add(lblCsgl_1);
		
		JLabel lblEgb = new JLabel("EGB:");
		lblEgb.setBounds(40, 129, 55, 16);
		panel.add(lblEgb);
		
		JLabel lblEgb_1 = new JLabel("EGB:");
		lblEgb_1.setBounds(287, 129, 55, 16);
		panel.add(lblEgb_1);
		
		JLabel lblRecommendedBet = new JLabel("Recommended bet:\r\n");
		lblRecommendedBet.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblRecommendedBet.setBounds(40, 331, 243, 28);
		panel.add(lblRecommendedBet);
		
		JLabel lblTimeLeftTill = new JLabel("Time left till match start:\r\n");
		lblTimeLeftTill.setBounds(40, 236, 175, 16);
		panel.add(lblTimeLeftTill);
	}
	
	private String[] getMarkedMatchup(){
		
		
		return null;
	}
}
