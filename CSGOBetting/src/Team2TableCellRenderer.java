import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
 
 
@SuppressWarnings("serial")
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
                label.setBackground(new Color(0xd9d9d9)); //hellgrau
            } else {
            if ((int)table.getModel().getValueAt(row, 0) == 0){
                label.setBackground(new Color(0xa3ebff)); //hellcyan
            } else {
            if ((int)table.getModel().getValueAt(row, 0) == 1){
                label.setBackground(new Color(0xff9494)); //hellrot
            } else {
            if ((int)table.getModel().getValueAt(row, 0) == 2){
            	label.setBackground(new Color(0x94ffa6)); //hellgruen
            } else {
                label.setBackground(new Color(0xd9d9d9)); //hellgrau
            }}}}   
           
            return label;
            }//getTableCellRendererComponent
 
}//ColoredTableCellRenderer