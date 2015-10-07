package Interface;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Controller.ListenController;
import MatchInformation.MatchInformation;

import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DevFrame extends JFrame {

	private JPanel contentPane;
	Timer timer = new Timer();
	boolean isAutoUpdateActive = false;
	boolean isUpdateProcessRunning = false;
	MatchInformation Info = new MatchInformation();
	private JTextField txtBet;
	private JTextField txtInventory;
	MainWindow mainWindow;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DevFrame frame = new DevFrame(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DevFrame(MainWindow window) {
		mainWindow = window;
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        timer.cancel();
		    }
		});
		setTitle("Tobis Developer Playground");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnButton = new JButton("Get CSGOLounge line");
		btnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO Was soll Button 1 machen?
				MatchInformation Info = new MatchInformation();
				System.out.println(Info.getLoungeMatchInfo(200));
				Info = null;
			}
		});
		btnButton.setBounds(10, 26, 213, 23);
		contentPane.add(btnButton);
		
		JLabel lblDebugstring = new JLabel("Debugstring");
		lblDebugstring.setBounds(10, 236, 338, 14);
		contentPane.add(lblDebugstring);
		
		JButton btnButton_1 = new JButton("Update everything");
		btnButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Was soll Button 1 machen?
				long timer = System.currentTimeMillis();
				MatchInformation Info = new MatchInformation();
				Info.createLoungeFile();
				Info.createEGBFile();
				Info.createClosedBetLinkList();
				Info.createOpenBetLinkList();
				Info = null;
				lblDebugstring.setText("Finished in " + (System.currentTimeMillis() - timer) + "ms");
			}
		});
		btnButton_1.setBounds(10, 60, 213, 23);
		contentPane.add(btnButton_1);
		
		JButton btnButton_2 = new JButton("Get EGB line");
		btnButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Was soll Button 1 machen?
				MatchInformation Info = new MatchInformation();
				System.out.println(Info.getEGBMatchInfo(2000));
				Info=null;
			}
		});
		btnButton_2.setBounds(10, 94, 213, 23);
		contentPane.add(btnButton_2);
		
		JButton btnNewButton = new JButton("Start auto-update");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isAutoUpdateActive){
					lblDebugstring.setText("Auto-update started");
					btnNewButton.setText("Stop auto-update");
					isAutoUpdateActive = true;
					timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						  @Override
						  public void run() {
							Info.createLoungeFile();
							Info.createEGBFile();
							Info.createClosedBetLinkList();
							Info.createOpenBetLinkList();
							if(mainWindow != null){
								mainWindow.updateTableNumbers();
							}
						  }
						}, 10, 5*1000);
				}else{
					lblDebugstring.setText("Auto-update stopped");
					btnNewButton.setText("Start auto-update");
					isAutoUpdateActive = false;
					timer.cancel();
				}
			}
		});
		btnNewButton.setBounds(10, 128, 213, 23);
		contentPane.add(btnNewButton);
		
		txtBet = new JTextField();
		txtBet.setText("bet %");
		txtBet.setBounds(338, 27, 86, 20);
		contentPane.add(txtBet);
		txtBet.setColumns(10);
		
		txtInventory = new JTextField();
		txtInventory.setText("inventory");
		txtInventory.setBounds(338, 61, 86, 20);
		contentPane.add(txtInventory);
		txtInventory.setColumns(10);
		
		JButton btnBet = new JButton("$ bet");
		btnBet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double percent = 0.01*Double.parseDouble(txtBet.getText());
				double inventory = Double.parseDouble(txtInventory.getText());
				btnBet.setText(""+(percent*inventory));
			}
		});
		btnBet.setBounds(338, 94, 89, 23);
		contentPane.add(btnBet);
	}
}
