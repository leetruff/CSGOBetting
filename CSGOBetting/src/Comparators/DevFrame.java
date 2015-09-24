package Comparators;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class DevFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DevFrame frame = new DevFrame();
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
	public DevFrame() {
		setTitle("Tobis Developer Playground");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnButton = new JButton("Button 1");
		btnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO Was soll Button 1 machen?
			}
		});
		btnButton.setBounds(10, 26, 89, 23);
		contentPane.add(btnButton);
		
		JButton btnButton_1 = new JButton("Button 2");
		btnButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Was soll Button 1 machen?
			}
		});
		btnButton_1.setBounds(173, 26, 89, 23);
		contentPane.add(btnButton_1);
		
		JButton btnButton_2 = new JButton("Button 3");
		btnButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Was soll Button 1 machen?
			}
		});
		btnButton_2.setBounds(335, 26, 89, 23);
		contentPane.add(btnButton_2);
	}
}
