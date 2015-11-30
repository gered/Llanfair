package org.fenix.llanfair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.fenix.llanfair.Language;

import org.fenix.utils.TableModelSupport;

/**
 *
 * @author  Xavier "Xunkar" Sencert
 */
public class Counters implements TableModel, Serializable {
    
    // -------------------------------------------------------------- CONSTANTES
    
    public static final long serialVersionUID = 1000L;
    
    public static final int COLUMN_ICON = 0;
    
    public static final int COLUMN_NAME = 1;
    
    public static final int COLUMN_START = 2;
    
    public static final int COLUMN_INCREMENT = 3;
    
    public static final int COLUMN_COUNT = 4;
    
    // -------------------------------------------------------------- ATTRIBUTS
    
    private List<Counter> data;
    
    private TableModelSupport tmSupport;
    
    // ---------------------------------------------------------- CONSTRUCTEURS
    
    public Counters() {
        data      = new ArrayList<Counter>();
        tmSupport = new TableModelSupport(this);
    }
    
    public int getColumnCount() {
        return COLUMN_COUNT;
    }
    
    public int getRowCount() {
        return data.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= data.size()) {
            throw new IllegalArgumentException("illegal counter id " + rowIndex);
        }
        return data.get(rowIndex).get(columnIndex);
    }
    
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case COLUMN_ICON:       return "" + Language.ICON;
            case COLUMN_INCREMENT:  return "" + Language.INCREMENT;
            case COLUMN_NAME:       return "" + Language.NAME;
            case COLUMN_START:      return "" + Language.START_VALUE;
        }
        return null;
    }
    
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COLUMN_ICON:       return Icon.class;
            case COLUMN_INCREMENT:  return Integer.class;
            case COLUMN_NAME:       return String.class;
            case COLUMN_START:      return Integer.class;
        }
        return null;
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= data.size()) {
            throw new IllegalArgumentException("illegal counter id " + rowIndex);
        }
        data.get(rowIndex).set(columnIndex, aValue);
    }
    
    public void addTableModelListener(TableModelListener l) {
        tmSupport.addTableModelListener(l);
    }
    
    public void removeTableModelListener(TableModelListener l) {
        tmSupport.removeTableModelListener(l);
    }
    
    // ----------------------------------------------------------- CLASSES
    
    public static class Counter implements Serializable {
        
        public static final long serialVersionUID = 1000L;
        
        private String name;
        
        private Icon icon;
        
        private int increment;
        
        private int start;
        
        private int saved;
        
        private int live;
        
        public Counter() {
            name      = "" + Language.UNTITLED;
            icon      = null;
            start     = 0;
            live      = 0; 
            increment = 1;
        }
        
        public Object get(int columnIndex) {
            switch (columnIndex) {
                case COLUMN_ICON:       return icon;
                case COLUMN_INCREMENT:  return increment;
                case COLUMN_NAME:       return name;
                case COLUMN_START:      return start;
                default:                return name;
            }
        }
        
        public void set(int columnIndex, Object value) {
            switch (columnIndex) {
                case COLUMN_ICON:       icon = (Icon) value; break;
                case COLUMN_INCREMENT:  increment = (Integer) value; break;
                case COLUMN_NAME:       name = (String) value; break;
                case COLUMN_START:      start = (Integer) value; break;
            }
        }
        
        public Icon getIcon() {
            return icon;
        }
        
        public String getName() {
            return name;
        }
        
        public int getLive() {
            return live;
        }
        
        public int getStart() {
            return start;
        }
        
        public int getSaved() {
            return saved;
        }
        
        public void nextStep() {
            live += increment;
        }
    }

}
