package org.fenix.llanfair.dialog;

import org.fenix.llanfair.*;
import org.fenix.llanfair.config.Accuracy;
import org.fenix.utils.gui.GBC;

import org.fenix.WorldRecord.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Boîte de dialogue permettant l’édition d’une course. {@code EditDialog}
 * permet de modifier le titre de la course, d’ajouter ou de retirer des 
 * segments et d’éditer l’icône, le nom ainsi que les temps de chaque segment.
 *
 * @author  Xavier "Xunkar" Sencert
 * @see     AbstractDialog
 * @see     Run
 */
public class EditRun extends LlanfairDialog
implements ActionListener, ListSelectionListener {

	// ------------------------------------------------------------- CONSTANTES

	/**
	 * Largeurs en pixels des colonnes de la table d’édition des segments. Les
	 * largeurs sont données dans l’odre du modèle.
	 */
	private static final int[] TABLE_COLUMN_WIDTHS = new int[] {
		Segment.ICON_MAX_SIZE, 130, 100, 100, 100
	};

	/**
	 * Dimension d’un petit bouton dont l’étiquette n’est qu’un caractère.
	 */
	private static final Dimension SMALL_BUTTON_SIZE = new Dimension(40, 25);

	// -------------------------------------------------------------- ATTRIBUTS

	/**
	 * Course éditée par cette boîte de dialogue.
	 */
	private Run run;

	/**
	 * Zone d’édition du titre de la course.
	 */
	private JTextArea runTitle;

	/**
	 * Étiquette du {@link runTitle}.
	 */
	private JLabel runTitleLabel;

	private JTextField runSubTitle;

	/**
	 * Table d’édition des segments de la course.
	 */
	private JTable segments;

	/**
	 * Panneau avec ascenseur dans lequel est inséré la table {@link segments}.
	 */
	private JScrollPane scrollPane;

	/**
	 * Étiquette de {@code segments}.
	 */
	private JLabel segmentsLabel;

	/**
	 * Bouton permettant d’insérer un nouveau segment.
	 */
	private JButton addSegment;

	/**
	 * Bouton permettant de supprimer un segment.
	 */
	private JButton remSegment;

	private JCheckBox segmented;

	/**
	 * Bouton permettant d’enregistrer l’édition et de quitter le dialogue.
	 */
	private JButton save;

	/**
	 * Bouton permettant de quitter le dialogue sans modifier la course.
	 */
	private JButton cancel;

	/**
	 * Button moving the currently selected segment one position up.
	 */
	private JButton moveUp;

	/**
	 * Button moving the currently selected segment one position down.
	 */
	private JButton moveDown;

	private JTextField runDelayedStart;

	/**
	 * World record data from spedrun.com
	 */
	private JLabel recordLabel;
	private JButton selectRecord;
	private RecordDialog recordSelector;


	// ----------------------------------------------------------- CONSTRUCTEURS

	/**
	 * Création d’une boîte de dialogue permettant d’éditer la course fournie.
	 *
	 * @param   run   - la course a éditer.
	 */
	public EditRun(Run run) {
		super();
		if (run == null) {
			throw new NullPointerException("EditDialog.EditDialog(): null run");
		}
		this.run  = run;
		run.saveBackup();

		setTitle(Language.EDITING.get());

		String delayedStartString = new Time(run.getDelayedStart()).toString(Accuracy.HUNDREDTH);

		runTitle       = new JTextArea(run.getName(), 2, 61);
		runTitleLabel  = new JLabel(Language.RUN_TITLE.get());
		runSubTitle     = new JTextField(run.getSubTitle(), 61);
		runDelayedStart = new JTextField(delayedStartString, 5);
		segments       = new JTable(run) {
			@Override protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					@Override public String getToolTipText(MouseEvent event) {
						int col = columnModel.getColumnIndexAtX(event.getX());
						int ind = columnModel.getColumn(col).getModelIndex();
						switch (ind) {
							case Run.COLUMN_BEST:
								return "" + Language.TT_COLUMN_BEST;
							case Run.COLUMN_SEGMENT:
								return "" + Language.TT_COLUMN_SEGMENT;
							case Run.COLUMN_TIME:
								return "" + Language.TT_COLUMN_TIME;
							default:
								return null;
						}
					}
				};
			}
		};
		segmentsLabel  = new JLabel("" + Language.SEGMENTS);
		addSegment     = new JButton(Llanfair.getResources().getIcon("PLUS.png"));
		remSegment     = new JButton(Llanfair.getResources().getIcon("MINUS.png"));
		save           = new JButton("" + Language.menuItem_save);
		cancel         = new JButton("" + Language.CANCEL);
		scrollPane     = new JScrollPane(segments);
		moveUp         = new JButton(Llanfair.getResources().getIcon("ARROW_UP.png"));
		moveDown       = new JButton(Llanfair.getResources().getIcon("ARROW_DOWN.png"));
		segmented      = new JCheckBox("" + Language.ED_SEGMENTED, run.isSegmented());
		recordLabel    = new JLabel("World record");
		selectRecord   = new JButton("Select record");
		recordSelector = new RecordDialog();

		placeComponents();
		setBehavior();
	}

	// ---------------------------------------------------------------- MÉTHODES

	/**
	 * Dispose les sous-composants au sein de la boîte de dialogue.
	 */
	private void placeComponents() {
		setLayout(new GridBagLayout());
		add(runTitleLabel, GBC.grid(0, 0).insets(4, 4, 0, 4).anchor(GBC.LINE_END));
		add(runTitle, GBC.grid(1, 0, 3, 1).insets(4, 0, 0, 4).anchor(GBC.LINE_START));
		add(new JLabel("" + Language.LB_SUBTITLE), GBC.grid(0, 1).insets(4, 4, 0, 4).anchor(GBC.LINE_END));
		add(runSubTitle, GBC.grid(1, 1).insets(4, 0, 0, 4).anchor(GBC.LINE_START));
		add(new JLabel("" + Language.ED_DELAYED_START), GBC.grid(0, 2).insets(4, 4, 0, 4).anchor(GBC.LINE_END));
		add(runDelayedStart, GBC.grid(1, 2).insets(4, 0, 0, 4).anchor(GBC.LINE_START));
		add(segmented, GBC.grid(2, 2, 2, 1).insets(4, 0, 0, 4).anchor(GBC.LINE_START));
		add(segmentsLabel, GBC.grid(0, 3).insets(5, 4, 4, 0)
				.anchor(GBC.LINE_END));
		add(scrollPane, GBC.grid(1, 3, 3, 4).insets(4, 4, 0, 0).anchor(GBC.LINE_START));
		add(addSegment, GBC.grid(3, 3).insets(0, 4).anchor(GBC.FIRST_LINE_START));
		add(remSegment, GBC.grid(3, 4).insets(4, 4).anchor(GBC.FIRST_LINE_START));
		add(moveUp, GBC.grid(3, 5).insets(0, 4).anchor(GBC.FIRST_LINE_START));
		add(moveDown, GBC.grid(3, 6).insets(4, 4).anchor(GBC.FIRST_LINE_START));

		add(recordLabel, GBC.grid(0,7).insets(5,4,4,0).anchor(GBC.LINE_END));
		add(selectRecord, GBC.grid(1,7).insets(4,0,0,4).anchor(GBC.LINE_START));

		JPanel controls = new JPanel();
		controls.add(save);
		controls.add(cancel);
		add(controls, GBC.grid(0, 8, 4, 1));
	}

	/**
	 * Définit le comportement des sous-composants du dialogue.
	 */
	private void setBehavior() {
		// Ne pas permettre la fermeture du dialogue par la croix pour forcer
		// l’utilisateur à utiliser les boutons save et cancel.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Définir les dimensions des composants.
		setResizable(false);
		addSegment.setPreferredSize(SMALL_BUTTON_SIZE);
		remSegment.setPreferredSize(SMALL_BUTTON_SIZE);
		moveUp.setPreferredSize(SMALL_BUTTON_SIZE);
		moveDown.setPreferredSize(SMALL_BUTTON_SIZE);

		segments.setRowHeight(Segment.ICON_MAX_SIZE);
		for (int i = 0; i < run.getColumnCount(); i++) {
			TableColumn column = segments.getColumnModel().getColumn(i);
			column.setPreferredWidth(TABLE_COLUMN_WIDTHS[i]);
		}

		int totalWidth = 0;
		for (int width : TABLE_COLUMN_WIDTHS) {
			totalWidth += width;
		}
		Dimension size = new Dimension(totalWidth, Segment.ICON_MAX_SIZE * 5);
		segments.setPreferredScrollableViewportSize(size);

		// Enregistrement des écouteurs des composants.
		addSegment.addActionListener(this);
		remSegment.addActionListener(this);
		moveUp.addActionListener(this);
		moveDown.addActionListener(this);
		cancel.addActionListener(this);
		save.addActionListener(this);
		selectRecord.addActionListener(this);

		// Insertion des délégués de rendus et d’édition.
		segments.setDefaultRenderer(Icon.class, new IconRenderer());
		segments.setDefaultEditor(Icon.class, new FileChooserEditor(this));
		segments.setDefaultEditor(Time.class, new TimeEditor());

		// Ajuster le comportement de la table.
		segments.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		segments.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		segments.getSelectionModel().addListSelectionListener(this);

		addSegment.setToolTipText("" + Language.TT_ADD_SEGMENT);
		remSegment.setToolTipText("" + Language.TT_REMOVE_SEGMENT);
		moveDown.setToolTipText("" + Language.TT_MOVE_SEGMENT_DOWN);
		moveUp.setToolTipText("" + Language.TT_MOVE_SEGMENT_UP);
		segmented.setToolTipText("" + Language.TT_ED_SEGMENTED);

		// parse the entered delayed start time whenever focus leaves the field
		// (this lets us prompt up errors if the user enters something invalid immediately)
		runDelayedStart.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				long result = parseDelayedStartTime(runDelayedStart.getText());
				if (result == -1)
					runDelayedStart.setBackground(Color.RED);
				else
					runDelayedStart.setBackground(Color.WHITE);
			}
		});

		updateButtons();
	}

	private long parseDelayedStartTime(String text) {
		long result;
		try {
			Time time = new Time(text);
			result = time.getMilliseconds();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Language.ERROR.get(), JOptionPane.ERROR_MESSAGE);
			result = -1;
		}

		return result;
	}

	/**
	 * Procédure à invoquer lorsque ce composant réalise une action. Sont donc
	 * capturés ici, tous les évènements d’action des sous-composants, comme
	 * l’appui sur un bouton.
	 *
	 * @param   event   - l’évènement d’action.
	 * @see     ActionListener
	 */
	@Override public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source.equals(addSegment)) {
			run.addSegment(new Segment());
			Rectangle rect = segments.getCellRect(run.getRowCount() - 1, 0, true);
			segments.scrollRectToVisible(rect);
			updateButtons();

		} else if (source.equals(remSegment)) {
			run.removeSegment(segments.getSelectedRow());
			updateButtons();

		} else if (source.equals(save)) {
			run.setName(runTitle.getText());
			run.setSubTitle(runSubTitle.getText());
			run.setSegmented(segmented.isSelected());

			long delayedStart = parseDelayedStartTime(runDelayedStart.getText());
			run.setDelayedStart(delayedStart == -1 ? 0 : delayedStart);

			dispose();

		} else if (source.equals(cancel)) {
			if (!segments.isEditing()) {
				run.loadBackup();
				dispose();
			}
		} else if (source.equals(moveUp)) {
			int selected = segments.getSelectedRow();
			run.moveSegmentUp(selected);
			segments.setRowSelectionInterval(selected - 1, selected - 1);

		} else if (source.equals(moveDown)) {
			int selected = segments.getSelectedRow();
			run.moveSegmentDown(selected);
			segments.setRowSelectionInterval(selected + 1, selected + 1);
		} else if (source.equals(selectRecord)) {
			recordSelector.setVisible(true);
		}
	}

	/**
	 * Méthode invoquée lors d’un changement de sélection dans la table.
	 *
	 * @param   event - l’évènement de sélection.
	 */
	public void valueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			updateButtons();
		}
	}

	private void updateButtons() {
		int     selected = segments.getSelectedRow();
		boolean enabled  = (selected >= 0);
		remSegment.setEnabled(enabled);
		moveUp.setEnabled(enabled && selected > 0);
		moveDown.setEnabled(enabled && selected < run.getRowCount() - 1);
	}

	// ---------------------------------------------------------- CLASSE INTERNE

	/**
	 * Gestionnaire de rendu capable d’afficher une valeur de type {@link Icon}
	 * au sein d’une table.
	 *
	 * @author  Xavier Sencert
	 * @see     TableCellRenderer
	 */
	private class IconRenderer extends DefaultTableCellRenderer {

		/**
		 * Construction d’un gestionnaire de rendu d’icônes.
		 */
		public IconRenderer() {
			super();
		}

		/**
		 * Retourne le composant de rendu à afficher dans la table. Le composant
		 * est ici préparé selon la valeur de la cellule et différentes
		 * informations sur l’état du composant.
		 */
		@Override public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			// Préparer le label comme un label par défaut.
			JLabel label = (JLabel) super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			// Modifier l’icône du label par celle de la valeur.
			label.setIcon((value == null) ? null : (Icon) value);
			label.setText("");
			label.setHorizontalAlignment(JLabel.CENTER);
			return label;
		}
	}

	// ---------------------------------------------------------- CLASSE INTERNE

	/**
	 * Délégué d’édition des temps de course et de segments. Cette classe permet
	 * de récupérer les exceptions potentiellement levés lorsque l’édition se
	 * termine et que la valeur est modifié dans la table des segments.
	 *
	 * @author  Xavier "Xunkar" Sencert
	 */
	private class TimeEditor extends DefaultCellEditor {

		/**
		 * Composant d’édition. Conservé ici en doublon pour éviter de devoir le
		 * caster à chaque fois.
		 */
		private JTextField editor;

		/**
		 * Construction d’un délégué par défaut.
		 */
		public TimeEditor() {
			super(new JTextField());
			editor = (JTextField) getComponent();
		}

		/**
		 * Retourne la valeur entrée par l’utilisateur.
		 *
		 * @return  la valeur entrée par l’utilisateur.
		 */
		public Object getCellEditorValue() {
			String text = editor.getText();
			return text.equals("") ? null : new Time(text);
		}

		/**
		 * Retourne le composant d’édition, formatté comme il se doit selon les
		 * informations de la table et de la cellule.
		 *
		 * @param   table       - la table source demandant l’édition.
		 * @param   get       - la valeur actuellement présente à représenter.
		 * @param   isSelected  - indique si la ligne est sélectionnée.
		 * @param   row         - indice de ligne la cellule éditée.
		 * @param   column      - indice de colonne de la cellule éditée.
		 * @return  le composant d’édition.
		 */
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			editor.setText(value == null ? "" : value.toString());
			editor.selectAll();
			return editor;
		}

		/**
		 * Arrête l’édition de la cellule. Le délégué récupère ici les exceptions
		 * levées et les remonte à l’utilisateur. Tant que l’édition est en erreur
		 * l’édition persiste.
		 *
		 * @return  {@code true} si l’édition s’est arrêtée.
		 */
		public boolean stopCellEditing() {
			try {
				return super.stopCellEditing();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(editor, e.getMessage(),
						Language.ERROR.get(), JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

	}

	// ---------------------------------------------------------- CLASSE INTERNE

	/**
	 * Éditeur de cellules permettant de sélectionner un fichier via un
	 * {@code JFileChooser}.
	 *
	 * @author  Xavier Sencert
	 * @see     TableCellEditor
	 * @see     ActionListener
	 */
	private class FileChooserEditor extends AbstractCellEditor
	implements TableCellEditor, ActionListener {

		/**
		 * Gestionnaire de sélection de fichier.
		 */
		private JFileChooser chooser;

		/**
		 * Composant d’édition de l’éditeur. Il s’agit d’un {@code JButton}
		 * ce qui permet de capturer le clic de l’utilisateur et ainsi d’ouvrir
		 * le gestionnaire de sélection de fichier.
		 */
		private JButton editor;

		private Window owner;

		/**
		 * Création d’un éditeur par défaut.
		 */
		public FileChooserEditor(Window owner) {
			super();
			this.owner = owner;
			chooser = new JFileChooser(".");
			editor  = new JButton();

			chooser.setFileFilter(new FileNameExtensionFilter(
					"" + Language.IMAGE, "gif", "jpg", "jpeg", "png"));
			editor.addActionListener(this);
		}

		/**
		 * Retourne le composant d’édition à afficher au sein de la table, il
		 * s’agit donc du bouton {@link #editor}.
		 */
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			return editor;
		}

		/**
		 * Retourne la valeur stockée par l’éditeur à savoir le fichier
		 * sélectionner par l’utilisateur s’il y en a un.
		 */
		public Object getCellEditorValue() {
			if (chooser.getSelectedFile() == null) {
				return null;
			}
			return new ImageIcon(chooser.getSelectedFile().getPath());
		}

		/**
		 * Lors du clic de l’utilisateur sur le bouton, on affiche le
		 * gestionnaire de sélection de fichier puis l’on force la fin de
		 * l’édition lorsque celui-ci retourne.
		 */
		@Override public void actionPerformed(ActionEvent e) {
			chooser.showOpenDialog(owner);
			fireEditingStopped();
		}

	}
}
