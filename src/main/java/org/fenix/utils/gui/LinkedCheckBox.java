//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils.gui;

import org.fenix.utils.MixedPair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinkedCheckBox extends JCheckBox implements ItemListener {
	private boolean purgesSelectState = true;
	private List<MixedPair<JCheckBox, Boolean>> constraints = new ArrayList();
	private List<MixedPair<JCheckBox, Boolean>> sideEffects = new ArrayList();

	public LinkedCheckBox() {
		this.addItemListener(this);
	}

	public boolean purgesSelectState() {
		return this.purgesSelectState;
	}

	public void activates(JCheckBox box) {
		if(box == null) {
			throw new NullPointerException("Target CheckBox is null");
		} else {
			this.sideEffects.add(new MixedPair(box, Boolean.TRUE));
		}
	}

	public void deactivates(JCheckBox box) {
		if(box == null) {
			throw new NullPointerException("Target CheckBox is null");
		} else {
			this.sideEffects.add(new MixedPair(box, Boolean.FALSE));
		}
	}

	public void requires(JCheckBox box, boolean state) {
		if(box == null) {
			throw new NullPointerException("Required CheckBox is null");
		} else {
			this.constraints.add(new MixedPair(box, Boolean.valueOf(state)));
			box.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LinkedCheckBox.this.checkConstraints();
				}
			});
			box.addPropertyChangeListener("enabled", new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					LinkedCheckBox.this.checkConstraints();
				}
			});
			this.checkConstraints();
		}
	}

	public void setPurgesSelectState(boolean purgesSelectState) {
		this.purgesSelectState = purgesSelectState;
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if(!b && this.purgesSelectState) {
			this.setSelected(false);
		}

	}

	public void itemStateChanged(ItemEvent event) {
		boolean selected = this.isSelected();
		Iterator i$ = this.sideEffects.iterator();

		while(true) {
			while(i$.hasNext()) {
				MixedPair pair = (MixedPair)i$.next();
				if(((Boolean)pair.second).booleanValue()) {
					((JCheckBox)pair.first).setSelected(selected);
				} else {
					((JCheckBox)pair.first).setSelected(!selected && ((JCheckBox)pair.first).isSelected());
				}
			}

			return;
		}
	}

	protected void checkConstraints() {
		boolean enabled = true;

		MixedPair pair;
		for(Iterator i$ = this.constraints.iterator(); i$.hasNext(); enabled &= ((JCheckBox)pair.first).isSelected() == ((Boolean)pair.second).booleanValue()) {
			pair = (MixedPair)i$.next();
		}

		this.setEnabled(enabled);
	}
}
