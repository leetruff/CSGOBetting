package Renderer;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
 
 /**
  * Gegenstueck zu {@link Team1TableCellRenderer Team1TableCellRenderer}
  * @author Lars
  *
  */
@SuppressWarnings("serial")
public class Team2TableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int modelRow = table.getRowSorter().convertRowIndexToModel(row);
            
            if(!isSelected){
            	
            	
            	/**
            	 * -1 bisher nicht beendet
            	 *  0 unentschieden
            	 *  1 Team 1 siegt
            	 *  2 Team 2 siegt
            	 */
                if ((int)table.getModel().getValueAt(modelRow, 0) == -1){
                    label.setBackground(new Color(0xd9d9d9)); //hellgrau
                } else {
                if ((int)table.getModel().getValueAt(modelRow, 0) == 0){
                    label.setBackground(new Color(0xa3ebff)); //hellcyan
                } else {
                if ((int)table.getModel().getValueAt(modelRow, 0) == 1){
                    label.setBackground(new Color(0xff9494)); //hellrot
                } else {
                if ((int)table.getModel().getValueAt(modelRow, 0) == 2){
                	label.setBackground(new Color(0x94ffa6)); //hellgruen
                } else {
                    label.setBackground(new Color(0xd9d9d9)); //hellgrau
                }}}} 
            	
            }
           
            return label;
            }
 
}