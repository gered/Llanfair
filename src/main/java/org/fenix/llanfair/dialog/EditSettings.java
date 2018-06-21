package org.fenix.llanfair.dialog;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Llanfair;
import org.fenix.utils.gui.GBC;
import org.fenix.utils.locale.LocaleDelegate;
import org.fenix.utils.locale.LocaleEvent;
import org.fenix.utils.locale.LocaleListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

/**
 * ConfigDialog
 *
 * @author Xavier Sencert
 * @date   22 juil. 2012
 */
public class EditSettings extends LlanfairDialog 
		implements ActionListener, LocaleListener, WindowListener {

	// ATTRIBUTS

	final private Llanfair master;

	/**
	 * Bouton permettant de valider et de fermer la boîte de dialogue.
	 */
	private JButton actionOK;

	private JButton reset;

	private List<SettingsTab> settingsTabs;

	// CONSTRUCTEURS

	/**
	 * Construction d’une boîte de dialogue d’édition de paramètres.
	 */
	public EditSettings(Llanfair master) {
		this.master = master;

		settingsTabs = new ArrayList<SettingsTab>();
		settingsTabs.add(new TabGeneral());
		settingsTabs.add(new TabLook());
		settingsTabs.add(new TabHotkeys());
		settingsTabs.add(new TabHistory());
		settingsTabs.add(new TabComponents());

		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		createResources();
		placeComponents();
		setPersistentBehavior();

		LocaleDelegate.addLocaleListener(this);
	}

	@Override public void localeChanged(LocaleEvent event) {
//        for (SettingsTab tab : settingsTabs) {
//            tab.processLocaleEvent(event);
//        }
	}

	// MÉTHODES

	/**
	 * Instanciation des sous-composants utilisés par cette boîte de dialogue.
	 */
	private void createResources() {
		reset     = new JButton("" + Language.setting_hotkey_reset);
		actionOK  = new JButton(Language.ACCEPT.get());
	}

	/**
	 * Dispose les sous-composants au sein de ce panneau. Les sous-composants
	 * sont placés à l’aide d’un {@link GridBagLayout} dont l’accès est
	 * simplifié par la classe-proxy {@link GBC}.
	 */
	private void placeComponents() {
		setLayout(new GridBagLayout());

		JTabbedPane tabPane = new JTabbedPane(); {
			for (SettingsTab tab : settingsTabs) {
				tabPane.add(tab.toString(), tab);
			}
		}
		JPanel controls = new JPanel(); {
			controls.add(actionOK);
			controls.add(reset);
		}
		add(tabPane, GBC.grid(0, 0));
		add(controls, GBC.grid(0, 1).insets(6, 0, 4, 0));
	}

	/**
	 * Définit le comportement persistant (non sujet aux variations d’états du
	 * modèle ou de la configuration de l’application) pour ce composant et
	 * ses sous-composants.
	 */
	private void setPersistentBehavior() {
		setResizable(false);
		setTitle(Language.menuItem_settings.get());
		actionOK.addActionListener(this);
		reset.addActionListener(this);
		addWindowListener(this);
	}


	/**
	 * Procédure à invoquer lorsqu’un sous-composant réalise une action. Cela
	 * signifie pour nous que l’utilisateur à effectuer un réglage de paramètre.
	 *
	 * @param   evt   - l’évènement d’action.
	 * @see     ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(actionOK)) {
			try {
				for (SettingsTab tab : settingsTabs) {
					tab.doDelayedSettingChange();
				}
				dispose();
			} catch (InvalidSettingException ex) {
				ex.tab.requestFocusInWindow();
				ex.field.requestFocusInWindow();
				master.showError(ex.getMessage());
			}
		} else if (source.equals(reset)) {
			int option = JOptionPane.showConfirmDialog(this,
					"" + Language.WARN_RESET_SETTINGS);
			if (option == JOptionPane.YES_OPTION) {
//                Settings.reset();
				dispose();
			}
		}

	}

	@Override public void windowActivated(WindowEvent e) {}
	@Override public void windowClosed(WindowEvent e) {}
	@Override public void windowDeactivated(WindowEvent e) {}
	@Override public void windowDeiconified(WindowEvent e) {}
	@Override public void windowIconified(WindowEvent e) {}
	@Override public void windowOpened(WindowEvent e) {}

	@Override public void windowClosing(WindowEvent e) {
		try {
			for (SettingsTab tab : settingsTabs) {
				tab.doDelayedSettingChange();
			}
			dispose();
		} catch (InvalidSettingException ex) {
			ex.tab.grabFocus();
			master.showError(ex.getMessage());
		}
	}

}
