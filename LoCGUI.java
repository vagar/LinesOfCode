import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;


public class LoCGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFile;
	private JButton btnBrowse;
	private JList<String> list;
	private DefaultListModel<String> listModel;
	private JLabel lblFile;
	private JLabel lblResults;
	private JButton btnCountLoc;
	private JFileChooser fileChooser;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
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
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{20, 0, 100, 75, 50, 30, 80, 20, 0, 0};
		gbl_contentPane.rowHeights = new int[]{10, 0, 20, 0, 10, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		
		
		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		
		
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int returnVal = fileChooser.showOpenDialog(LoCGUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					listModel.clear();
					txtFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
				} else {
					//no need to handle
				}
			}
		});
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.insets = new Insets(0, 0, 5, 5);
		gbc_btnBrowse.gridx = 6;
		gbc_btnBrowse.gridy = 1;
		contentPane.add(btnBrowse, gbc_btnBrowse);
		
		btnCountLoc = new JButton("Count!");
		btnCountLoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				listModel.clear();
				
				LoCResult res = LoCCounter.count(txtFile.getText());
				if(!res.error()){
					listModel.addElement("File: "+new File(txtFile.getText()).getName());
					listModel.addElement("   "+res.getCodeLines()+" Lines of Code");
					listModel.addElement("   "+res.getCommentLines()+" Comment lines");
					listModel.addElement("   "+res.getBlankLines()+" Blank lines");	
				}
				else{
					listModel.addElement("ERROR CODE 42");	
				}
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
		list.setPreferredSize(new Dimension(200, 100));
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.gridheight = 3;
		gbc_list.gridwidth = 5;
		gbc_list.insets = new Insets(0, 0, 0, 5);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 2;
		gbc_list.gridy = 6;
		contentPane.add(list, gbc_list);
	}

}
