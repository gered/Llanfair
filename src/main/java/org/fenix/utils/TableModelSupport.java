//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TableModelSupport {
	private TableModel source;
	private EventListenerList listeners;

	public TableModelSupport(TableModel source) {
		if(source == null) {
			throw new NullPointerException("Source TableModel is null");
		} else {
			this.source = source;
			this.listeners = new EventListenerList();
		}
	}

	public TableModel getSource() {
		return this.source;
	}

	public void addTableModelListener(TableModelListener listener) {
		if(listener == null) {
			throw new NullPointerException("TableModelListener is null");
		} else {
			this.listeners.add(TableModelListener.class, listener);
		}
	}

	public void removeTableModelListener(TableModelListener listener) {
		this.listeners.remove(TableModelListener.class, listener);
	}

	public void fireTableDataChanged() {
		this.fire(new TableModelEvent(this.source, 0, this.source.getRowCount() - 1));
	}

	public void fireTableStructureChanged() {
		this.fire(new TableModelEvent(this.source, -1));
	}

	public void fireTableRowsInserted(int firstRow, int lastRow) {
		this.fire(new TableModelEvent(this.source, firstRow, lastRow, -1, 1));
	}

	public void fireTableRowsUpdated(int firstRow, int lastRow) {
		this.fire(new TableModelEvent(this.source, firstRow, lastRow, -1, 0));
	}

	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		this.fire(new TableModelEvent(this.source, firstRow, lastRow, -1, -1));
	}

	public void fireTableCellUpdated(int row, int col) {
		this.fire(new TableModelEvent(this.source, row, row, col, 0));
	}

	private void fire(TableModelEvent event) {
		TableModelListener[] tableListeners = this.listeners.getListeners(TableModelListener.class);
		TableModelListener[] arr$ = tableListeners;
		int len$ = tableListeners.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			TableModelListener listener = arr$[i$];
			listener.tableChanged(event);
		}

	}
}
