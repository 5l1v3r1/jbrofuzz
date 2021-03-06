package org.owasp.jbrofuzz.db;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.MessageContainer;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>
 * The open database dialog.
 * </p>
 * 
 * @author daemonmidi@gmail.com
 * @version 2.5
 */
public class OpenDatabaseDialog extends JDialog implements MouseListener,
KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5815321460026044259L;

	// Dimensions of the generator dialog box
	private static final int SIZE_X = 440;
	private static final int SIZE_Y = 280;

	// The buttons
	private final JButton ok, cancel;
	private final JButton getSessions;
	
	final JPanel propertiesPanel = new JPanel(new FlowLayout(
			FlowLayout.RIGHT, 15, 15));
	
	// The frame that the sniffing panel is attached
	//private final JBroFuzzWindow m;

	private final JPopupMenu popmenu;

	private final JComboBox databaseBox, methodBox;

	private JComboBox sessionsSQLiteBox;

	// private JComboBox sessionsCouchBox;

	private final String methods[] = { "SQLite", "CouchDB"};
	
	private String sessionsSQLite[] = {"not loaded yet"};
	

	private JBroFuzzWindow parent = null;
	/**
	 * <p>
	 * Constructs a dialog box for (Ctrl+L) input of URL fields.
	 * </p>
	 * 
	 * @param parent
	 *            JBroFuzzWindow The main window
	 * 
	 * @author daemonmidi@gmail.com
	 * @version 2.5
	 * @since 2.5
	 */
	public OpenDatabaseDialog(final JBroFuzzWindow parent) {

		super(parent, " Open Database ", true);
		this.parent = parent;
		// setFont(new Font("SansSerif", Font.BOLD, 10));
		setFont(new Font("Verdana", Font.BOLD, 12));
		setIconImage(ImageCreator.IMG_FRAME.getImage());

		setLayout(new BorderLayout());
	//	m = parent;

		// Components

		databaseBox = new JComboBox();
		databaseBox.setPreferredSize(new Dimension(250, 20));
		databaseBox.setEditable(true);

		databaseBox.getEditor().getEditorComponent().addMouseListener(this);
		databaseBox.setToolTipText("Copy/Paste a Databasename / URL from your browser");
		databaseBox.setFont(new Font("Verdana", Font.BOLD, 12));

		databaseBox.getEditor().getEditorComponent().addKeyListener(this);


		methodBox = new JComboBox(methods);
		// sessionsCouchBox = new JComboBox(sessionsCouch);
		sessionsSQLiteBox = new JComboBox(sessionsSQLite);

		methodBox.setFont(new Font("Verdana", Font.BOLD, 10));
		// sessionsCouchBox.setFont(new Font("Verdana", Font.BOLD, 10));
		sessionsSQLiteBox.setFont(new Font("Verdana", Font.BOLD, 10));

		
		methodBox.addKeyListener(this);
		// sessionsCouchBox.addKeyListener(this);
		sessionsSQLiteBox.addKeyListener(this);

		
		methodBox.setMaximumRowCount(3);
		// sessionsCouchBox.setMaximumRowCount(3);
		sessionsSQLiteBox.setMaximumRowCount(3);
		
		methodBox.setBackground(Color.BLACK);
		// sessionsCouchBox.setBackground(Color.BLACK);
		sessionsSQLiteBox.setBackground(Color.BLACK);
		
		
		methodBox.setForeground(Color.WHITE);
		// sessionsCouchBox.setForeground(Color.WHITE);
		sessionsSQLiteBox.setForeground(Color.WHITE);

		// sessionsCouchBox.setSelectedIndex(0);
		sessionsSQLiteBox.setSelectedIndex(0);
		
		// Buttons

		ok = new JButton("  OK  ");
		ok.setBounds(515, 305, 140, 40);
		ok.setToolTipText("Open the Database location in JBroFuzz");

		cancel = new JButton("Cancel");
		cancel.setBounds(515, 305, 140, 40);
		cancel.setToolTipText("Cancel opening a Database location");

		
		getSessions = new JButton("Get SessionIds");
		getSessions.setBounds(515, 305, 140, 40);
		getSessions.setToolTipText("Load all SessionIds from Database");
		
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						clickOK(parent);
						OpenDatabaseDialog.this.dispose();

					}
				});
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						OpenDatabaseDialog.this.dispose();

					}
				});
			}
		});

		getSessions.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						clickGetSessions();
					}
				});
			}
		});
		
		
		// Pop-up menu
		popmenu = new JPopupMenu();

		final JMenuItem i1 = new JMenuItem("Cut");
		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i3 = new JMenuItem("Paste");
		final JMenuItem i4 = new JMenuItem("Select All");

		popmenu.add(i1);
		popmenu.add(i2);
		popmenu.add(i3);
		popmenu.addSeparator();
		popmenu.add(i4);

		// Cut
		i1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
				final Transferable clipData = clipboard.getContents(clipboard);

				if (clipData != null) {
					try {
						if (clipData
								.isDataFlavorSupported(DataFlavor.stringFlavor)) {
							((JTextComponent) databaseBox.getEditor().getEditorComponent()).cut();
						}
					} catch (final Exception e1) {

						Logger.log("Open Location: An error occured while cutting",2);

					}
				}
			}
		});

		// Copy
		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
				final Transferable clipData = clipboard.getContents(clipboard);

				if (clipData != null) {
					try {
						if (clipData
								.isDataFlavorSupported(DataFlavor.stringFlavor)) {
							((JTextComponent) databaseBox.getEditor().getEditorComponent()).copy();
						}
					} catch (final Exception e1) {

						Logger.log("Open Location: An error occured while copying",2);

					}
				}
			}
		});

		// Paste
		i3.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
				final Transferable clipData = clipboard.getContents(clipboard);

				if (clipData != null) {
					try {
						if (clipData
								.isDataFlavorSupported(DataFlavor.stringFlavor)) {
							((JTextComponent) databaseBox.getEditor().getEditorComponent()).replaceSelection((String) (clipData.getTransferData(DataFlavor.stringFlavor)));
						}
					} catch (final Exception e1) {
						Logger.log("Open Location: An error occured while pasting",2);
					}
				}
			}
		});

		// Select All
		i4.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				databaseBox.getEditor().selectAll();

			}
		});

		// Final panels

		final JPanel targetPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		targetPanel.add(new JLabel("Database: "));
		targetPanel.add(databaseBox);

		propertiesPanel.add(new JLabel("DatabaseType: "));
		propertiesPanel.add(methodBox);
		
		propertiesPanel.add(new JLabel("SessionIds: "));
		
		if (JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "").toLowerCase().trim().equals("sqlite")){
			propertiesPanel.add(sessionsSQLiteBox);
		}
		else if (JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "").toLowerCase().trim().equals("couchdb")){
			// propertiesPanel.add(sessionsCouchBox);
		}

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add(getSessions);
		buttonPanel.add(ok);
		buttonPanel.add(cancel);

		
		add(targetPanel, BorderLayout.NORTH);
		add(propertiesPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Set the Database
		((JTextComponent) databaseBox.getEditor().getEditorComponent()).setText(JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), ""));
		((JTextComponent) databaseBox.getEditor().getEditorComponent()).selectAll();

		// Global frame issues
		setLocation(parent.getLocation().x + 20, parent.getLocation().y + 40);

		setSize(OpenDatabaseDialog.SIZE_X, OpenDatabaseDialog.SIZE_Y);
		setResizable(true);
		setVisible(true);

	}

	private void checkForTriggerEvent(final MouseEvent e) {

		if (e.isPopupTrigger()) {
			popmenu.show(e.getComponent(), e.getX(), e.getY());
		}

	}
	
	
	private void clickGetSessions(){
		if (JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "").toLowerCase().trim().equals("sqlite")){
			SQLiteHandler sqlH = new SQLiteHandler();
			String[] lSessionId = null;
			sessionsSQLiteBox.removeAllItems();
			
				String dbName = "";
				if (databaseBox.getSelectedItem().toString().length() > 0 && !databaseBox.getSelectedItem().toString().equals("")){
					dbName = databaseBox.getSelectedItem().toString();
				}
				else{
					dbName = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "");
				}
				try {
					lSessionId = sqlH.getSessionIds(sqlH.getConnection(dbName));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			
			for (int i = 0; i < lSessionId.length; i++){
				sessionsSQLiteBox.addItem(String.valueOf(lSessionId[i]));
			}
			
			sessionsSQLiteBox.setSelectedIndex(0);
			sessionsSQLite = lSessionId;
			this.repaint();
		}
		else if (JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "").toLowerCase().trim().equals("couchdb")){
			CouchDBHandler cdbH = new CouchDBHandler();
			String[] sessionsCouch = cdbH.getDocumentIds(JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), ""));
			// sessionsCouchBox.removeAllItems();
			
			for (int i = 0; i < sessionsCouch.length; i++){
				// sessionsCouchBox.addItem(sessionsCouch[i]);
			}
			
			// sessionsCouchBox.setSelectedIndex(0);
			this.repaint();
		}
	}

	private void clickOK(JBroFuzzWindow mWindow) {
		Vector<MessageContainer> mcv = mWindow.getJBroFuzz().getStorageHandler().readFuzzFile(null, sessionsSQLiteBox.getSelectedItem().toString(), mWindow);

		mWindow.getPanelFuzzing().setTextURL(mcv.get(0).getTextURL());
		mWindow.getPanelFuzzing().setTextRequest(mcv.get(0).getEncodedPayload());

		mWindow.getPanelFuzzing().getOutputPanel().getOutputTableModel().clearAllRows();
	
		mWindow.getPanelFuzzing().setSessionName(sessionsSQLiteBox.getSelectedItem().toString());
		
		JBroFuzz.PREFS.put("sessionId", sessionsSQLiteBox.getSelectedItem().toString());
		
		
		for (int i = 0; i < mcv.size(); i++){
			
			mWindow.getPanelFuzzing().getOutputPanel().getOutputTableModel().addNewRow(mcv.get(i));
		}
		
		mWindow.doLayout();
		mWindow.repaint();

	}

	@Override
	public void keyPressed(KeyEvent ke) {

		if (ke.getKeyCode() == 27) {
			OpenDatabaseDialog.this.dispose();
		}
		if (ke.getKeyCode() == 10) {
			clickOK(parent);
			OpenDatabaseDialog.this.dispose();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		checkForTriggerEvent(e);

	}

	@Override
	public void mouseEntered(MouseEvent e) {

		checkForTriggerEvent(e);

	}

	@Override
	public void mouseExited(MouseEvent e) {

		checkForTriggerEvent(e);

	}

	@Override
	public void mousePressed(MouseEvent e) {

		checkForTriggerEvent(e);

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		checkForTriggerEvent(e);

	}

}
