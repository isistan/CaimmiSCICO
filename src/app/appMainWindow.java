package app;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.jgoodies.forms.layout.FormLayout;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.optionpane.WebOptionPane;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import model.Project;
import model.causalRelationship.CausalRelationship;
import model.conceptualComponent.ConceptualComponent;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import pipeline.PipelineParalelo;
import pipeline.PipelineParalelo.TOPROCESS;
import utils.io.ProjectIO;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import java.awt.Font;
import javax.swing.border.LineBorder;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JCheckBoxMenuItem;

public class appMainWindow {

	// Variables 

	private JFrame mainFrame;
	private String appName = "Req2Arch";
	public JTable tableRequirements;
	public JTable tableResponsibilities;
	public JTable tableRelations;
	public JTable tableComponents;
	private JTabbedPane tabbedPane;
	public JTextPane txtRequirements;
	public JTextPane txtResponsibilities;
	public JTextPane txtRelations;
	public JTextPane txtComponents;
	private JTextPane txtDiagram;
	private Project actualProject = null;
	private Project projectToProcess;
	private PipelineParalelo pipe;
	private static final String stanfordOnlineIP = "http://corenlp.run";
	private static final Integer stanfordOnlinePort = 80;
	private String myIP = "http://10.1.4.166";
	private Integer myPort = 9000;
	private String IP = stanfordOnlineIP;
	private Integer PORT = stanfordOnlinePort;
	private JRadioButtonMenuItem rdbStanford;
	private JRadioButtonMenuItem rdbMy;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					// Look and feel
					WebLookAndFeel.install();

					appMainWindow window = new appMainWindow();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public appMainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(appMainWindow.class.getResource("/resources/icon.png")));
		mainFrame.setTitle("Req2Arch");
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.setBounds(100, 100, 1382, 745);
		mainFrame.setMinimumSize(new Dimension(760,560));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);

		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpenProject = new JMenuItem("Open Project");
		mntmOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmOpenProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openProject();
			}
		});
		mnFile.add(mntmOpenProject);

		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenu mnConfiguration = new JMenu("Configuration");
		menuBar.add(mnConfiguration);
		
		rdbStanford = new JRadioButtonMenuItem("http://corenlp.run:80");
		rdbStanford.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				useStanford();
			}
		});
		rdbStanford.setSelected(true);
		mnConfiguration.add(rdbStanford);
		
		rdbMy = new JRadioButtonMenuItem("192.168.2.69:9000");
		rdbMy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				useMyServer();
			}
		});
		mnConfiguration.add(rdbMy);
		
		JSeparator separator_2 = new JSeparator();
		mnConfiguration.add(separator_2);
		
		JMenu mnClusteringAlgorithm = new JMenu("Clustering Algorithm");
		mnConfiguration.add(mnClusteringAlgorithm);
		
		JMenuItem mntmDbscan = new JMenuItem("DBScan");
		mnClusteringAlgorithm.add(mntmDbscan);
		
		JMenuItem mntmEm = new JMenuItem("EM");
		mnClusteringAlgorithm.add(mntmEm);
		
		JMenuItem mntmHac = new JMenuItem("HAC");
		mnClusteringAlgorithm.add(mntmHac);
		
		JMenuItem mntmPam = new JMenuItem("PAM");
		mnClusteringAlgorithm.add(mntmPam);
		
		JMenuItem mntmKmeans = new JMenuItem("K-Means");
		mnClusteringAlgorithm.add(mntmKmeans);
		
		JMenuItem mntmXmeans = new JMenuItem("X-Means");
		mntmXmeans.setSelected(true);
		mnClusteringAlgorithm.add(mntmXmeans);

		mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// Panel de Requerimientos

		JPanel panelRequirements = new JPanel();
		tabbedPane.addTab("(0) Software Requirements", null, panelRequirements, null);
		panelRequirements.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("center:150dlu"),
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("180px:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("25dlu"),
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		JScrollPane scrollPaneRequirementsText = new JScrollPane();
		panelRequirements.add(scrollPaneRequirementsText, "2, 2, fill, fill");

		txtRequirements = new JTextPane();
		txtRequirements.setToolTipText("");
		txtRequirements.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtRequirements.setMargin(new Insets(10, 10, 10, 10));
		scrollPaneRequirementsText.setViewportView(txtRequirements);

		JScrollPane scrollPaneTableRequirements = new JScrollPane();

		tableRequirements = new JTable();
		tableRequirements.setModel(new DefaultTableModel(
				new Object[][] {},
				new String[] {
						"ID Requirement", "Select"
				}
				) {
			Class[] columnTypes = new Class[] {
					Object.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableRequirements.getColumnModel().getColumn(0).setPreferredWidth(125);
		tableRequirements.getColumnModel().getColumn(0).setMaxWidth(125);
		tableRequirements.getColumnModel().getColumn(1).setPreferredWidth(125);
		tableRequirements.getColumnModel().getColumn(1).setMaxWidth(125);
		scrollPaneTableRequirements.setViewportView(tableRequirements);

		panelRequirements.add(scrollPaneTableRequirements, "4, 2, fill, fill");

		JButton btnProcess = new JButton("Process (Stage 1)");
		btnProcess.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				processTab1();
			}
		});
		panelRequirements.add(btnProcess, "4, 4, fill, fill");


		// Panel de Responsabilidades

		JPanel panelResponsibilities = new JPanel();
		tabbedPane.addTab("(1) UCM Responsibilities", null, panelResponsibilities, null);

		panelResponsibilities.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("center:150dlu"),
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("180px:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("25dlu"),
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		JScrollPane scrollPaneResponsibilitiesText = new JScrollPane();
		panelResponsibilities.add(scrollPaneResponsibilitiesText, "2, 2, fill, fill");

		txtResponsibilities = new JTextPane();
		txtResponsibilities.setToolTipText("");
		txtResponsibilities.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtResponsibilities.setMargin(new Insets(10, 10, 10, 10));
		scrollPaneResponsibilitiesText.setViewportView(txtResponsibilities);

		JScrollPane scrollPaneTableResponsibilities = new JScrollPane();

		tableResponsibilities = new JTable();
		tableResponsibilities.setModel(new DefaultTableModel(
				new Object[][] {},
				new String[] {
						"ID Responsibility", "Select"
				}
				) {
			Class[] columnTypes = new Class[] {
					Object.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableResponsibilities.getColumnModel().getColumn(0).setPreferredWidth(125);
		tableResponsibilities.getColumnModel().getColumn(0).setMaxWidth(125);
		tableResponsibilities.getColumnModel().getColumn(1).setPreferredWidth(125);
		tableResponsibilities.getColumnModel().getColumn(1).setMaxWidth(125);
		scrollPaneTableResponsibilities.setViewportView(tableResponsibilities);

		panelResponsibilities.add(scrollPaneTableResponsibilities, "4, 2, fill, fill");

		JButton btnProcess_2 = new JButton("Process (Stage 2)");
		btnProcess_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnProcess_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				processTab2();
			}
		});
		panelResponsibilities.add(btnProcess_2, "4, 4, fill, fill");

		// Panel Relaciones causales

		JPanel panelRelationships = new JPanel();
		tabbedPane.addTab("(2) UCM Causal Relationships", null, panelRelationships, null);

		panelRelationships.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("center:150dlu"),
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("180px:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("25dlu"),
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		JScrollPane scrollPaneRelationText = new JScrollPane();
		panelRelationships.add(scrollPaneRelationText, "2, 2, fill, fill");

		txtRelations = new JTextPane();
		txtRelations.setToolTipText("");
		txtRelations.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtRelations.setMargin(new Insets(10, 10, 10, 10));
		scrollPaneRelationText.setViewportView(txtRelations);

		JScrollPane scrollPaneTableRelation = new JScrollPane();

		tableRelations = new JTable();
		tableRelations.setModel(new DefaultTableModel(
				new Object[][] {},
				new String[] {
						"ID Causal Relation", "Select"
				}
				) {
			Class[] columnTypes = new Class[] {
					Object.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableRelations.getColumnModel().getColumn(0).setPreferredWidth(125);
		tableRelations.getColumnModel().getColumn(0).setMaxWidth(125);
		tableRelations.getColumnModel().getColumn(1).setPreferredWidth(125);
		tableRelations.getColumnModel().getColumn(1).setMaxWidth(125);
		scrollPaneTableRelation.setViewportView(tableRelations);

		panelRelationships.add(scrollPaneTableRelation, "4, 2, fill, fill");

		JButton btnProcess_3 = new JButton("Process (Stage 3)");
		btnProcess_3.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnProcess_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				processTab3();
			}
		});
		panelRelationships.add(btnProcess_3, "4, 4, fill, fill");

		// Panel de Alocación de responsabilidades

		JPanel panelClustering = new JPanel();
		tabbedPane.addTab("(3) UCM Conceptual Components", null, panelClustering, null);

		panelClustering.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("center:150dlu"),
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("180px:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("25dlu"),
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		JScrollPane scrollPaneComponentsText = new JScrollPane();
		panelClustering.add(scrollPaneComponentsText, "2, 2, fill, fill");

		txtComponents = new JTextPane();
		txtComponents.setToolTipText("");
		txtComponents.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtComponents.setMargin(new Insets(10, 10, 10, 10));
		scrollPaneComponentsText.setViewportView(txtComponents);

		JScrollPane scrollPaneTableComponents = new JScrollPane();

		tableComponents = new JTable();
		tableComponents.setModel(new DefaultTableModel(
				new Object[][] {},
				new String[] {
						"ID Component", "Select"
				}
				) {
			Class[] columnTypes = new Class[] {
					Object.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableComponents.getColumnModel().getColumn(0).setPreferredWidth(125);
		tableComponents.getColumnModel().getColumn(0).setMaxWidth(125);
		tableComponents.getColumnModel().getColumn(1).setPreferredWidth(125);
		tableComponents.getColumnModel().getColumn(1).setMaxWidth(125);
		scrollPaneTableComponents.setViewportView(tableComponents);

		panelClustering.add(scrollPaneTableComponents, "4, 2, fill, fill");

		JButton btnProcess_4 = new JButton("Generate UCM");
		btnProcess_4.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnProcess_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				processTab4();
			}
		});
		panelClustering.add(btnProcess_4, "4, 4, fill, fill");

		// Panel de Diagrama

		JPanel panelDiagram = new JPanel();
		tabbedPane.addTab("(4) UCM Diagram", null, panelDiagram, null);

		panelDiagram.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("center:150dlu"),
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("180px:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("25dlu"),
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		JScrollPane scrollPaneDiagramText = new JScrollPane();
		panelDiagram.add(scrollPaneDiagramText, "2, 2, fill, fill");

		txtDiagram = new JTextPane();
		txtDiagram.setToolTipText("");
		txtDiagram.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtDiagram.setMargin(new Insets(10, 10, 10, 10));
		scrollPaneDiagramText.setViewportView(txtDiagram);


	}	

	protected void useStanford() {
		this.IP = stanfordOnlineIP;
		this.PORT = stanfordOnlinePort;	
		rdbStanford.setSelected(true);
		rdbMy.setSelected(false);
	}
	
	protected void useMyServer() {
		this.IP = myIP;
		this.PORT = myPort;	
		rdbStanford.setSelected(false);
		rdbMy.setSelected(true);
	}

	// Methods

	private void processTab1() {

		// Ver si cargamos un proyecto
		if (actualProject == null)
			return;

		// Analizar los requerimientos que tengan el tick!

		// Creamos un nuevo proyecto con los requerimientos que queremos analizar
		projectToProcess = new Project();
		projectToProcess.setProjectName(actualProject.getProjectName());

		DefaultTableModel model = (DefaultTableModel) tableRequirements.getModel();

		for (int i = 0; i < actualProject.getRequirements().size(); i++){
			if ((Boolean) model.getValueAt(i, 1)){
				projectToProcess.addRequirement(actualProject.getRequirements().get(i));
			}
		}

		// Pasar al siguiente tab donde se muestra la lista de responsabilidades

		tabbedPane.setSelectedIndex(1);

		// Mostrar en algún lugar que se está procesando

		// Realizar el procesamiento
		pipe = new PipelineParalelo(projectToProcess, this);
		pipe.setNlpIP(this.IP);
		pipe.setNlpPort(this.PORT);
		pipe.firstStep();

		Thread t1 = new Thread(pipe);
		t1.start();

	}

	private void processTab2() {

		// Ver si cargamos un proyecto
		if (actualProject == null && projectToProcess == null)
			return;

		// Pasar al siguiente tab donde se muestra la lista de responsabilidades

		tabbedPane.setSelectedIndex(2);

		// Mostrar en algún lugar que se está procesando

		// Realizar el procesamiento
		pipe.setType(TOPROCESS.STAGE2);

		Thread t1 = new Thread(pipe);
		t1.start();

	}

	private void processTab3() {

		// Ver si cargamos un proyecto
		if (actualProject == null && projectToProcess == null)
			return;

		// Pasar al siguiente tab donde se muestra la lista de responsabilidades

		tabbedPane.setSelectedIndex(3);

		// Mostrar en algún lugar que se está procesando

		// Realizar el procesamiento
		pipe.setType(TOPROCESS.STAGE3);

		Thread t1 = new Thread(pipe);
		t1.start();
	}

	protected void processTab4() {
		// Ver si cargamos un proyecto
		if (actualProject == null && projectToProcess == null)
			return;

		// Pasar al siguiente tab donde se muestra la lista de responsabilidades

		tabbedPane.setSelectedIndex(4);

		// Mostrar en algún lugar que se está procesando

		// Realizar el procesamiento
		pipe.setType(TOPROCESS.STAGE4);

		Thread t1 = new Thread(pipe);
		t1.start();
	}


	// Metodos auxiliares

	private void openProject() {

		//Pasamos a cargar el archivo
		WebFileChooser fileChooser = new WebFileChooser("examples");
		//fileChooser.setFileFilter(new FiltroCvr());
		fileChooser.setMultiSelectionEnabled ( false );
		fileChooser.setDialogTitle("Open project");
		fileChooser.setApproveButtonText("Open");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if ( fileChooser.showOpenDialog ( mainFrame ) == WebFileChooser.APPROVE_OPTION )
		{
			ProjectIO reader = new ProjectIO();
			actualProject = reader.readFromFile(fileChooser.getSelectedFile().getAbsolutePath());	
			mainFrame.setTitle(appName + "  -  Project [" + actualProject.getProjectName() + "]");
			tabbedPane.setSelectedIndex(0);
			loadRequirementsInRequirementsTable(actualProject);
			loadProjectInTextPane(actualProject);
		}

	}

	private void appendToPane(JTextPane tp, String msg, Color c){

		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}

	private void loadProjectInTextPane(Project project){

		txtRequirements.setText("");

		appendToPane(txtRequirements, "@ProjectName\n", Color.BLUE);
		appendToPane(txtRequirements, project.getProjectName() + "\n\n", Color.BLACK);

		for (Requirement requirement : project.getRequirements()){
			appendToPane(txtRequirements, "@Requirement=" + requirement.getId() + "\n", Color.BLUE);
			appendToPane(txtRequirements, requirement.getText() + "\n\n", Color.BLACK);
		}

	}


	private void loadRequirementsInRequirementsTable(Project project){

		DefaultTableModel model = (DefaultTableModel) tableRequirements.getModel();

		for (Requirement requirement : project.getRequirements()){
			model.addRow(new Object[]{requirement.getId(), new Boolean(true)});
		}

	}


	public void appendResponsibilityToView(Requirement requirement){

		appendToPane(txtResponsibilities, "@Requirement=" + requirement.getId() + "\n", Color.BLUE);
		appendToPane(txtResponsibilities, requirement.getText() + "\n", Color.BLACK);
		appendToPane(txtResponsibilities, "@Responsibilities=" + requirement.getResponsibilities().size() + "\n", Color.RED);

		for (Responsibility resp : requirement.getResponsibilities()){
			appendToPane(txtResponsibilities, "\t Resp ["+resp.getId()+"] " + resp + "\n", Color.BLACK);
		}

		appendToPane(txtResponsibilities, "\n", Color.RED);

		DefaultTableModel model = (DefaultTableModel) tableResponsibilities.getModel();

		for (Responsibility resp : requirement.getResponsibilities()){
			model.addRow(new Object[]{resp.getId(), new Boolean(true)});
		}

	}

	public void appendRelationsToView(Requirement requirement, ArrayList<CausalRelationship> out) {

		appendToPane(txtRelations, "@Requirement=" + requirement.getId() + "\n", Color.BLUE);
		appendToPane(txtRelations, requirement.getText() + "\n", Color.BLACK);
		appendToPane(txtRelations, "@Responsibilities=" + requirement.getResponsibilities().size() + "\n", Color.RED);

		for (Responsibility resp : requirement.getResponsibilities()){
			appendToPane(txtRelations, "\t Resp ["+resp.getId()+"] " + resp + "\n", Color.BLACK);
		}

		appendToPane(txtRelations, "\n", Color.RED);
		appendToPane(txtRelations, "@Causal Relationships=" + out.size() + "\n", Color.RED);

		for (CausalRelationship rel : out){
			appendToPane(txtRelations, "\t Causal Relationship : " + rel + "\n\n", Color.BLACK);
		}

	}

	public void appendConceptualComponentsToView(ArrayList<ConceptualComponent> results) {

		appendToPane(txtComponents, "@Conceptual Components=" + results.size() + "\n", Color.RED);

		for (ConceptualComponent comp : results){
			appendToPane(txtComponents, "\t Conceptual Component : " + comp.getName() + "\n", Color.BLACK);
			for (Responsibility rel : comp.getResponsibilities()){
				appendToPane(txtComponents, "\t\t Responsibility: [" + rel.getId() + "] [" + rel.getLongForm() +"]\n", Color.BLACK);
			}
			appendToPane(txtComponents, "\n", Color.BLACK);
		}

	}
	
	public void appendUCMToView(String filePathName) {
		appendToPane(txtDiagram, "@Use Case Map\n", Color.RED);
		appendToPane(txtDiagram, "\t JUCM File : " + filePathName +" \n", Color.BLACK);
		appendToPane(txtDiagram, "\t Open this JUCM File using the JUCMNav Plugin", Color.BLACK);
		
	}
}
