import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

public class LoCGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFile;
	private JButton btnBrowse;
	private JScrollPane scrollPane;
	private JList<String> list;
	private DefaultListModel<String> listModel;
	private JLabel lblFile;
	private JLabel lblResults;
	private JButton btnCountLoc;
	private JFileChooser openFileChooser, saveFileChooser;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmSaveResults;
	private JsonObject saveableResults = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					LoCGUI frame = new LoCGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoCGUI() {
		setTitle("Lines of Code");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 330);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		saveFileChooser = new JFileChooser() {

			private static final long serialVersionUID = 1L;

			@Override
			public void approveSelection() {
				File f = getSelectedFile();
				if (f.exists() && getDialogType() == SAVE_DIALOG) {
					int result = JOptionPane.showConfirmDialog(this,
							"The file exists, overwrite?", "Existing file",
							JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.NO_OPTION:
						return;
					case JOptionPane.CLOSED_OPTION:
						return;
					case JOptionPane.CANCEL_OPTION:
						cancelSelection();
						return;
					}
				}
				super.approveSelection();
			}
		};
		saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		saveFileChooser.setMultiSelectionEnabled(false);
		saveFileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "JSON Files (*.json)";
			}

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".json") || f.isDirectory();
			}
		});

		mntmSaveResults = new JMenuItem("Save Results");
		mntmSaveResults.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
		mntmSaveResults.setEnabled(false);
		mntmSaveResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int returnVal = saveFileChooser.showSaveDialog(LoCGUI.this);
				if (returnVal != JFileChooser.APPROVE_OPTION)
					return;

				try (JsonWriter jsonWriter = Json
						.createWriter(new FileOutputStream(saveFileChooser
								.getSelectedFile()
								+ (saveFileChooser.getSelectedFile().toString()
										.endsWith(".json") ? "" : ".json")))) {
					jsonWriter.writeObject(saveableResults);
				} catch (FileNotFoundException e1) {
					System.err.println("Saving results to json file failed.");
					// e1.printStackTrace();
				}

			}
		});
		mnFile.add(mntmSaveResults);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 20, 0, 100, 75, 50, 30, 40,
				20, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 10, 0, 20, 0, 10, 0, 0, 0, 0,
				0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		lblFile = new JLabel("File:");
		GridBagConstraints gbc_lblFile = new GridBagConstraints();
		gbc_lblFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblFile.gridx = 1;
		gbc_lblFile.gridy = 1;
		contentPane.add(lblFile, gbc_lblFile);

		txtFile = new JTextField();
		GridBagConstraints gbc_txtTest = new GridBagConstraints();
		gbc_txtTest.gridwidth = 4;
		gbc_txtTest.insets = new Insets(0, 0, 5, 5);
		gbc_txtTest.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTest.gridx = 2;
		gbc_txtTest.gridy = 1;
		contentPane.add(txtFile, gbc_txtTest);
		txtFile.setColumns(10);

		openFileChooser = new JFileChooser();
		openFileChooser
				.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		openFileChooser.setMultiSelectionEnabled(false);

		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int returnVal = openFileChooser.showOpenDialog(LoCGUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					listModel.clear();
					txtFile.setText(openFileChooser.getSelectedFile()
							.getAbsolutePath());
				} else {
					// no need to handle
				}
			}
		});
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.anchor = GridBagConstraints.WEST;
		gbc_btnBrowse.gridwidth = 2;
		gbc_btnBrowse.insets = new Insets(0, 0, 5, 5);
		gbc_btnBrowse.gridx = 6;
		gbc_btnBrowse.gridy = 1;
		contentPane.add(btnBrowse, gbc_btnBrowse);

		btnCountLoc = new JButton("Count!");
		btnCountLoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				listModel.clear();

				ArrayList<LoCResult> result = LoCCounter.count(Paths
						.get(txtFile.getText()));

				int totalCodeLines = 0, totalCommentLines = 0, totalBlankLines = 0;

				JsonObjectBuilder jb = Json.createObjectBuilder();

				for (LoCResult res : result) {
					if (!res.error()) {
						listModel.addElement("===============");
						listModel.addElement("File: "
								+ res.getPath().toAbsolutePath());
						listModel.addElement("   (" + res.getLanguage() + ")");
						listModel.addElement("   " + res.getCodeLines()
								+ " Lines of Code");
						listModel.addElement("   " + res.getCommentLines()
								+ " Comment lines");
						listModel.addElement("   " + res.getBlankLines()
								+ " Blank lines");
						totalCodeLines += res.getCodeLines();
						totalCommentLines += res.getCommentLines();
						totalBlankLines += res.getBlankLines();

						jb.add(res.getPath().toAbsolutePath().toString(),
								res.toJsonObject());
						;
					} else {
						listModel.addElement("ERROR CODE 42");
					}
				}

				saveableResults = jb.build();

				listModel.addElement("===============");
				listModel.addElement("===============");
				listModel.addElement("Total in "
						+ Paths.get(txtFile.getText()).toAbsolutePath());
				listModel.addElement("   " + totalCodeLines + " Lines of Code");
				listModel.addElement("   " + totalCommentLines
						+ " Comment lines");
				listModel.addElement("   " + totalBlankLines + " Blank lines");

				mntmSaveResults.setEnabled(true);
			}
		});
		GridBagConstraints gbc_btnCountLoc = new GridBagConstraints();
		gbc_btnCountLoc.gridwidth = 2;
		gbc_btnCountLoc.insets = new Insets(0, 0, 5, 5);
		gbc_btnCountLoc.gridx = 3;
		gbc_btnCountLoc.gridy = 3;
		contentPane.add(btnCountLoc, gbc_btnCountLoc);

		lblResults = new JLabel("Results:");
		GridBagConstraints gbc_lblResults = new GridBagConstraints();
		gbc_lblResults.anchor = GridBagConstraints.WEST;
		gbc_lblResults.gridwidth = 2;
		gbc_lblResults.insets = new Insets(0, 0, 5, 5);
		gbc_lblResults.gridx = 1;
		gbc_lblResults.gridy = 5;
		contentPane.add(lblResults, gbc_lblResults);

		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);

		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(200, 100));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 3;
		gbc_scrollPane.gridwidth = 6;
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 6;
		contentPane.add(scrollPane, gbc_scrollPane);

		scrollPane.setViewportView(list);
	}

}
