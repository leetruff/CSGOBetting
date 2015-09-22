import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
 
 /**
  * Klasse um die Tabellenreihen entsprechend nach Winnerteam einzufaerben. Und ja, das geht leider
  * nicht einfacher in Swing.
  * @author Lars
  *
  */
@SuppressWarnings("serial")
public class Team1TableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
           
            /**
             * Wenns ausgewaehlt ist, soll es blau sein.
             */
            if(!isSelected){
            	
            	
            	/**
            	 * -1 bisher nicht beendet
            	 *  0 unentschieden
            	 *  1 Team 1 siegt
            	 *  2 Team 2 siegt
            	 */
	            if ((int)table.getModel().getValueAt(row, 0) == -1){
	                label.setBackground(new Color(0xd9d9d9)); //hellgrau
	            } else {
	            if ((int)table.getModel().getValueAt(row, 0) == 0){
	            	label.setBackground(new Color(0xa3ebff)); //hellcyan
	            } else {
	            if ((int)table.getModel().getValueAt(row, 0) == 1){
	                label.setBackground(new Color(0x94ffa6)); //hellgruen
	            } else {
	            if ((int)table.getModel().getValueAt(row, 0) == 2){
	            	label.setBackground(new Color(0xff9494)); //hellrot
	            } else {
	                label.setBackground(new Color(0xd9d9d9)); //hellgrau
	            }}}}   
           
            }
            return label;
            }
 
}