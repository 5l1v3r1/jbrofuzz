/**
 * JbroFuzz 2.5
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.encode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>
 * Window inspired from Paros Proxy, in terms of providing an encoder/decoder
 * for a variety of different schemes, as well as hashing functionality.
 * </p>
 * 
 * @author daemonmidi@gmail.com, subere@uncon.org, ranulf
 * @version 2.5
 * @since 1.5
 */
public class EncoderHashFrame extends JFrame implements KeyListener {

	private static final long serialVersionUID = 4722716158445936723L;
	// Dimensions of the frame
	private static final int SIZE_X = 650;
	private static final int SIZE_Y = 400;

	private static final Preferences PREFS = Preferences.userRoot().node("owasp/jbrofuzz");

	private JSplitPane horizontalSplitPane, verticalSplitPaneLeft,
	verticalSplitPaneRight, commentSplitPane;

	private JTextPane enTextPane, deTextPane;

	// The tree
	private int listCounter = 0;
	private JTree tree;

	private JButton swap, encode, decode, clear, close;

	private HashPanel commentPanel;

	private JPanel recordingPanel;
	private String[][] recordingPanelData;
	private String[] columnNames;
	private JTable recordingTable;

	public EncoderHashFrame() {

		// really inspired from Paros Proxy, but as a frame
		setTitle(" JBroFuzz - Encoder/Hash ");
		setJMenuBar(new EncoderHashMenuBar(this));

		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setLayout(new BorderLayout());

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode(
		"Codes/Hashes");
		setFont(new Font("SansSerif", Font.PLAIN, 12));
		// Create a tree that allows one selection at a time
		tree = new JTree(top);
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// Selection can only contain one path at a time
		tree.getSelectionModel().setSelectionMode(1);

		// Create the scroll pane and add the tree to it.
		final JScrollPane leftScrollPane = new JScrollPane(tree);

		// Create all the right hand panels
		for (int i = 0; i < EncoderHashCore.CODES.length; i++) {
			top.add(new DefaultMutableTreeNode(EncoderHashCore.CODES[i]));
		}

		final JPanel encoderPanel = new JPanel(new BorderLayout());
		final JPanel decoderPanel = new JPanel(new BorderLayout());

		encoderPanel
		.setBorder(BorderFactory
				.createCompoundBorder(
						BorderFactory
						.createTitledBorder(" Enter the plain text below to be encoded / hashed "),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		decoderPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Enter the text below to be decoded "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Text panes -> Encode
		enTextPane = new JTextPane();

		enTextPane.putClientProperty("charset", "UTF-8");
		enTextPane.setEditable(true);
		enTextPane.setVisible(true);
		enTextPane.setFont(new Font("Verdana", Font.PLAIN, 12));

		enTextPane.setMargin(new Insets(1, 1, 1, 1));
		enTextPane.setBackground(Color.WHITE);
		enTextPane.setForeground(new Color(51, 102, 102));

		// Set the right click for the encode text area
		HashPanelRightClick.popupText(enTextPane);

		final JScrollPane encodeScrollPane = new JScrollPane(enTextPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		encoderPanel.add(encodeScrollPane, BorderLayout.CENTER);

		// Text panes -> Decode
		deTextPane = new JTextPane();

		deTextPane.putClientProperty("charset", "UTF-8");
		deTextPane.setEditable(true);
		deTextPane.setVisible(true);
		deTextPane.setFont(new Font("Verdana", Font.PLAIN, 12));

		deTextPane.setMargin(new Insets(1, 1, 1, 1));
		deTextPane.setBackground(Color.WHITE);
		deTextPane.setForeground(new Color(204, 51, 0));

		// Set the right click for the decode text area
		HashPanelRightClick.popupText(deTextPane);

		final JScrollPane decodeScrollPane = new JScrollPane(deTextPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		decoderPanel.add(decodeScrollPane, BorderLayout.CENTER);

		commentPanel = new HashPanel("");
		recordingPanel = new JPanel(new BorderLayout());
		// Text panes -> Comment
		recordingPanelData = refreshRecordingPane();
		columnNames = new String[] { "Nr", "Encoded", "Decoded", "Codes/Hashes" };
		recordingTable = new JTable(recordingPanelData, columnNames);
		recordingTable.setEnabled(true);

		final JScrollPane recordingScrollPane = new JScrollPane(recordingTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		recordingPanel.add(recordingScrollPane, BorderLayout.CENTER);

		enTextPane.setForeground(new Color(51, 102, 102));

		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		verticalSplitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		commentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		commentSplitPane.setLeftComponent(verticalSplitPaneLeft);
		commentSplitPane.setRightComponent(verticalSplitPaneRight);

		verticalSplitPaneLeft.setTopComponent(encoderPanel);
		verticalSplitPaneLeft.setBottomComponent(decoderPanel);

		verticalSplitPaneRight.setTopComponent(commentPanel);
		verticalSplitPaneRight.setBottomComponent(recordingPanel);

		horizontalSplitPane.setLeftComponent(leftScrollPane);
		horizontalSplitPane.setRightComponent(commentSplitPane);

		// Set the minimum size for all components
		leftScrollPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		verticalSplitPaneLeft.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		commentSplitPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		encoderPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		decoderPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		commentPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		horizontalSplitPane.setDividerLocation(180);
		verticalSplitPaneLeft.setDividerLocation(SIZE_Y / 2);
		verticalSplitPaneRight.setDividerLocation(SIZE_Y / 2);
		commentSplitPane.setDividerLocation(280);

		// Traverse tree from root
		final TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), true);

		// Bottom three buttons
		swap = new JButton(" Swap ");
		encode = new JButton(" Encode/Hash ");
		decode = new JButton(" Decode ");
		clear = new JButton(" Clear ");
		close = new JButton(" Close ");

		swap
		.setToolTipText(" Swap the contents of encoded text with the decoded text ");
		final String desc = "Select an encoding or hashing scheme from the left hard side";
		encode.setToolTipText(desc);
		decode.setToolTipText(desc);
		clear.setToolTipText(" Clear content");
		close.setToolTipText(" Close this window ");

		// recording table selection listener.
		recordingTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				listCounter = recordingTable.getSelectedRow();
				final String clear = (String) recordingTable.getValueAt(
						recordingTable.getSelectedRow(), 1);
				final String enc = (String) recordingTable.getValueAt(
						recordingTable.getSelectedRow(), 2);
				enTextPane.setText(clear);
				deTextPane.setText(enc);

			}
		});

		swap.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						swapTexts();

					}

				});
			}
		});

		encode.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						calculate(true);
						saveValues();
					}
				});
			}
		});

		decode.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						calculate(false);
						saveValues();
					}
				});
			}
		});

		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numRows = recordingTable.getRowCount();
				int numCols = recordingTable.getColumnCount();
				for(int i=0;i<numRows;i++){
					for(int j=0;j<numCols;j++){
						recordingTable.setValueAt("", i, j);	
						
					}
					// Delete the values of the encode/decode as a preference
					PREFS.remove(JBroFuzzPrefs.ENCODER[0] + "." + i);	
					PREFS.remove(JBroFuzzPrefs.ENCODER[1] + "." + i);
					PREFS.remove(JBroFuzzPrefs.ENCODER[2] + "." + i);
				
					try {
						PREFS.sync();
					} catch (final BackingStoreException ex) {
						ex.printStackTrace();
					}


				}
			}
		});


		close.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						saveValues();
						dispose();
					}
				});
			}
		});

		// Keyboard listener for escape to close the window
		tree.addKeyListener(this);
		deTextPane.addKeyListener(this);
		enTextPane.addKeyListener(this);
		recordingTable.addKeyListener(this);
		encode.addKeyListener(this);
		decode.addKeyListener(this);
		clear.addKeyListener(this);
		close.addKeyListener(this);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(final TreeSelectionEvent e) {
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

				if (node == null) {
					return;
				}
				final String coderName = node.toString();

				decode.setEnabled(EncoderHashCore.isDecoded(coderName));
				commentPanel.setText(EncoderHashCore.getComment(coderName));
			}
		});

		// alt+enter to encode
		final Action doEncode = new AbstractAction() {

			private static final long serialVersionUID = -7686474340015136816L;

			public void actionPerformed(final ActionEvent e) {
				calculate(true);
			}
		};

		enTextPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
		.put(KeyStroke.getKeyStroke(Event.ENTER, Event.ALT_MASK),
		"doEncode");
		enTextPane.getActionMap().put("doEncode", doEncode);

		// alt+backspace to decode
		final Action doDecode = new AbstractAction() {

			private static final long serialVersionUID = 3083350774016663021L;

			public void actionPerformed(final ActionEvent e) {
				calculate(false);
			}
		};

		enTextPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(Event.BACK_SPACE, Event.ALT_MASK),
		"doDecode");
		enTextPane.getActionMap().put("doDecode", doDecode);

		// Bottom buttons

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(swap);
		buttonPanel.add(encode);
		buttonPanel.add(decode);
		buttonPanel.add(clear);
		buttonPanel.add(close);

		// Add the split pane to this panel
		getContentPane().add(horizontalSplitPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Where to show the encoder/hash frame
		this.setLocation(111, 111);

		this.setSize(EncoderHashFrame.SIZE_X, EncoderHashFrame.SIZE_Y);
		setMinimumSize(new Dimension(SIZE_X / 2, SIZE_Y / 2));

		setResizable(true);
		setVisible(true);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				closeFrame();
			}
		});

		/*
		 * // acutally replaced by new window on right side. // Load the values
		 * of encode/decode from the preferences
		 * enTextPane.setText(JBroFuzz.PREFS.get(JBroFuzzPrefs.ENCODER[0] +
		 * ".0", ""));
		 * deTextPane.setText(JBroFuzz.PREFS.get(JBroFuzzPrefs.ENCODER[1] +".0",
		 * ""));
		 * 
		 * final String encoder_type =
		 * JBroFuzz.PREFS.get(JBroFuzzPrefs.ENCODER[2]+".1", "");
		 * 
		 * for (i=0; i < EncoderHashCore.CODES.length; i++) if (
		 * EncoderHashCore.CODES[i].equalsIgnoreCase(encoder_type) ) {
		 * tree.setSelectionRow( i+1 ); break; }
		 */
	}

	public void keyTyped(final KeyEvent kEvent) {
		// 
	}

	public void keyPressed(final KeyEvent kEvent) {
		if (kEvent.getKeyCode() == 27) {

			EncoderHashFrame.this.dispose();

		}
	}

	public void keyReleased(final KeyEvent kEvent) {
		// 
	}


	/**
	 * <p>Method called for saving the preferences of each 
	 * encode/decode message and closing the frame.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public void closeFrame() {

		saveValues();
		dispose();

	}

	/**
	 * <p>
	 * Calculate the value to be encoded/decoded, based on the selected scheme
	 * from the left hand side tree.
	 * </p>
	 * 
	 * @param enDecode
	 *            false implies decode true implies encode
	 * 
	 * @version 1.6
	 * @since 1.5
	 */
	private void calculate(boolean isToEncode) {

		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
		.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		final String s = node.toString();
		if (isToEncode) {
			final String encodeText = enTextPane.getText();
			deTextPane.setText(EncoderHashCore.encode(encodeText, s));
		} else {
			final String decodeText = deTextPane.getText();
			enTextPane.setText(EncoderHashCore.decode(decodeText, s));
		}
	}

	/**
	 * <p>
	 * update recordingPaneTextField
	 * </p>
	 * 
	 * @return context updated Text for recordingTextPane
	 * 
	 * @author daemonmidi@gmail.com
	 * @version 1.0
	 * @since 2.3
	 */
	private String[][] refreshRecordingPane() {
		final String[][] returnObject = new String[50][4];
		int loose = 0;
		for (int i = 0; i < 50; i++) {
			final String encValue = PREFS.get(JBroFuzzPrefs.ENCODER[0]
			                                                        + "." + i, "");
			final String decValue = PREFS.get(JBroFuzzPrefs.ENCODER[1]
			                                                        + "." + i, "");
			final String engineValue = PREFS.get(
					JBroFuzzPrefs.ENCODER[2] + "." + i, "");
			if (encValue.length() > 0) {
				returnObject[i][0] = String.valueOf(i);
				returnObject[i][1] = encValue;
				returnObject[i][2] = decValue;
				returnObject[i][3] = engineValue;
			} else {
				loose++;
				returnObject[i][0] = String.valueOf(i);
				returnObject[i][1] = "";
				returnObject[i][2] = "";
				returnObject[i][3] = "";
			}
		}

		return returnObject;
	}

	/**
	 * <p>Save Key/Value/Encoder Data to UserPrefs and store them 
	 * for late usage.</p>
	 * 
	 * @author daemonmidi@gmail.com, subere@uncon.org 
	 * @version 2.4
	 * @since 1.0
	 */
	private void saveValues() {

		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
		.getLastSelectedPathComponent();
		// Save the values of the encode/decode as a preference
		PREFS.put(JBroFuzzPrefs.ENCODER[0] + "." + listCounter,
				enTextPane.getText());
		PREFS.put(JBroFuzzPrefs.ENCODER[1] + "." + listCounter,
				deTextPane.getText());
		if (node != null)
			PREFS.put(JBroFuzzPrefs.ENCODER[2] + "." + listCounter,
					node.toString());

		try {
			PREFS.sync();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}

		if (listCounter >= 50)
			listCounter = listCounter - 50;
		recordingTable.setValueAt(enTextPane.getText(), listCounter, 1);
		recordingTable.setValueAt(deTextPane.getText(), listCounter, 2);
		if (node != null) {
			recordingTable.setValueAt(node.toString(), listCounter, 3);
		} else {
			recordingTable.setValueAt("", listCounter, 3);
		}
		recordingTable.getSelectionModel().setSelectionInterval(listCounter, listCounter);

	}

	/**
	 * <p>
	 * Swap the texts in the encoding and decoding panels.
	 * </p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.3
	 * @since 2.3
	 */
	private void swapTexts() {

		final String enText = enTextPane.getText();
		final String deText = deTextPane.getText();

		enTextPane.setText(deText);
		deTextPane.setText(enText);

	}

	/**
	 * <p>
	 * Method for completely expanding or collapsing a given <code>JTree</code>.
	 * </p>
	 * 
	 * <p>
	 * Originally, from the Java Developers Almanac 1.4.
	 * </p>
	 * 
	 * @param tree
	 *            The JTree to be expanded/collapsed
	 * @param parent
	 *            The parent TreePath from which to begin
	 * @param expand
	 *            If true, expands all nodes in the tree, else collapse all
	 *            nodes.
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.2
	 */

	@SuppressWarnings("unchecked")
	public void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		final TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (final Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
				final TreeNode n = e.nextElement();
				final TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}
}
