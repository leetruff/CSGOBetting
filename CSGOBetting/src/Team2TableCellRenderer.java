import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
 
 
public class Team2TableCellRenderer extends DefaultTableCellRenderer {
    //----------------------------------------------
    //###           Attributes                   ###
    //----------------------------------------------
           
    //----------------------------------------------
    //###           Constructors                 ###
    //----------------------------------------------   
           
           
           
    //----------------------------------------------
    //###           Methods                      ###
    //----------------------------------------------
   
        //-----------------------------
        //getTableCellRendererComponent
        //-----------------------------
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
           
            if ((int)table.getModel().getValueAt(row, 0) == -1){
                label.setBackground(Color.GRAY);
            } else {
            if ((int)table.getModel().getValueAt(row, 0) == 0){
                label.setBackground(Color.CYAN);
            } else {
            if ((int)table.getModel().getValueAt(row, 0) == 1){
                label.setBackground(Color.RED);
            } else {
            if ((int)table.getModel().getValueAt(row, 0) == 2){
            	label.setBackground(Color.GREEN);
            } else {
                label.setBackground(Color.GRAY);
            }}}}   
           
            return label;
            }//getTableCellRendererComponent
 
}//ColoredTableCellRenderer