package ActionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Interface.DevFrame;
import Interface.MainWindow;

public class DevFrameActionListener implements ActionListener{
	MainWindow mainWindow;
	
	public DevFrameActionListener(MainWindow window) {
        this.mainWindow = window;
    }

    public void actionPerformed(ActionEvent e) {
		DevFrame devFrame = new DevFrame(mainWindow);
		devFrame.setVisible(true);
    }
}
