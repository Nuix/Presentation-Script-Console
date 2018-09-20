package com.nuix.scriptdemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import com.google.common.base.Joiner;

import nuix.Case;
import nuix.Utilities;
import javax.swing.JCheckBox;

/***
 * This dialog provides provides functionality similar to the script console built into Nuix already, with features
 * more oriented towards demoing Ruby code in a presentation.
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class ScriptDemoDialog extends JFrame implements Consumer<String> {

	private int editorFontSize = 0;
	private int outputFontSize = 0;
	private final JPanel contentPanel = new JPanel();
	private RSyntaxTextArea codeEditor;
	private JTextArea txtrOutput;
	private JMenu mnSnippets;
	private RTextScrollPane rtextScrollPane;
	private JMenuItem mntmRunScript;
	private JButton runButton;
	private Thread scriptThread;
	private JSplitPane splitPane;
	private String defaultScriptLocation = "C:\\";

	private Utilities utilities = null;
	private Case currentCase = null;
	private Object currentSelectedItems = null;
	private nuix.Window currentWindow = null;
	private ScriptingContainer currentScriptingContainer = null;
	private OutputStreamForwarder outputForwarder = null;

	public ScriptDemoDialog() {
		this(null,null,null,null);
	}

	public ScriptDemoDialog(Utilities utilities, Case currentCase, Object currentSelectedItems, nuix.Window window) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/nuix_icon.png")));
		this.utilities = utilities;
		this.currentCase = currentCase;
		this.currentSelectedItems = currentSelectedItems;
		this.currentWindow = window;
		setTitle("Presentation Script Console");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 801, 640);
		//setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{424, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 218, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			{
				{
					try {
						{
							JPanel panel = new JPanel();
							GridBagConstraints gbc_panel = new GridBagConstraints();
							gbc_panel.insets = new Insets(0, 0, 5, 0);
							gbc_panel.fill = GridBagConstraints.BOTH;
							gbc_panel.gridx = 0;
							gbc_panel.gridy = 0;
							contentPanel.add(panel, gbc_panel);
							GridBagLayout gbl_panel = new GridBagLayout();
							gbl_panel.columnWidths = new int[]{0, 0, 0};
							gbl_panel.rowHeights = new int[]{0, 0};
							gbl_panel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
							gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
							panel.setLayout(gbl_panel);
							{
								JToolBar toolBar = new JToolBar();
								GridBagConstraints gbc_toolBar = new GridBagConstraints();
								gbc_toolBar.anchor = GridBagConstraints.WEST;
								gbc_toolBar.insets = new Insets(0, 0, 0, 5);
								gbc_toolBar.gridx = 0;
								gbc_toolBar.gridy = 0;
								panel.add(toolBar, gbc_toolBar);
								{
									JButton btnIncreaseFontSize = new JButton("");
									btnIncreaseFontSize.setToolTipText("Increase Font");
									btnIncreaseFontSize.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent arg0) {
											setEditorFontSize(editorFontSize+2);
										}
									});
									{
										runButton = new JButton("Run");
										runButton.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/ruby_go.png")));
										toolBar.add(runButton);
										runButton.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent arg0) {
												runScript();
											}
										});
										runButton.setBackground(UIManager.getColor("Button.background"));
										runButton.setActionCommand("");
									}
									{
										btnAbortScript = new JButton("Abort Script");
										btnAbortScript.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent arg0) {
												outputForwarder.clearConsumer();
												currentScriptingContainer.terminate();
												try {
													currentScriptingContainer.finalize();
												} catch (Throwable e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												accept("Script terminated by user");
												currentScriptingContainer = null;
												scriptThread = null;
												setLockedForRunningScript(false);
											}
										});
										btnAbortScript.setEnabled(false);
										btnAbortScript.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/cancel.png")));
										toolBar.add(btnAbortScript);
									}
									{
										JSeparator separator = new JSeparator();
										separator.setOrientation(SwingConstants.VERTICAL);
										toolBar.add(separator);
									}
									btnIncreaseFontSize.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/font_add.png")));
									toolBar.add(btnIncreaseFontSize);
								}
								{
									JButton btnResetFontSize = new JButton("");
									btnResetFontSize.setToolTipText("Reset Font to Size 18");
									btnResetFontSize.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											setEditorFontSize(18);
										}
									});
									btnResetFontSize.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/font.png")));
									toolBar.add(btnResetFontSize);
								}
								{
									JButton btnDecreseFontSize = new JButton("");
									btnDecreseFontSize.setToolTipText("Decrease Font");
									btnDecreseFontSize.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											setEditorFontSize(editorFontSize-2);
										}
									});
									btnDecreseFontSize.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/font_delete.png")));
									toolBar.add(btnDecreseFontSize);
								}
								{
									JButton btnMakeWindowSmall = new JButton("");
									btnMakeWindowSmall.setToolTipText("Make window smaller");
									btnMakeWindowSmall.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											makeWindowSmall();
										}
									});
									{
										JSeparator separator = new JSeparator();
										separator.setOrientation(SwingConstants.VERTICAL);
										toolBar.add(separator);
									}
									btnMakeWindowSmall.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/arrow_in.png")));
									toolBar.add(btnMakeWindowSmall);
								}
								{
									JButton btnMakeWindowBig = new JButton("");
									btnMakeWindowBig.setToolTipText("Maximize Window");
									btnMakeWindowBig.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											makeWindowBig();
										}
									});
									btnMakeWindowBig.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/arrow_out.png")));
									toolBar.add(btnMakeWindowBig);
								}
								{
									JSeparator separator = new JSeparator();
									separator.setOrientation(SwingConstants.VERTICAL);
									toolBar.add(separator);
								}
								{
									JButton btnShowOutputOnly = new JButton("");
									btnShowOutputOnly.setToolTipText("Show output only");
									btnShowOutputOnly.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											splitPane.setDividerLocation(splitPane.getLocation().x+30);
										}
									});
									btnShowOutputOnly.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/resultset_first.png")));
									toolBar.add(btnShowOutputOnly);
								}
								{
									JButton btnShowCodeOnly = new JButton("");
									btnShowCodeOnly.setToolTipText("Show code only");
									btnShowCodeOnly.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											splitPane.setDividerLocation(splitPane.getLocation().x+splitPane.getWidth()-30);
										}
									});
									{
										JButton btnResetSplit = new JButton("");
										btnResetSplit.setToolTipText("Show code and output");
										btnResetSplit.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												splitPane.setDividerLocation(splitPane.getLocation().x+(splitPane.getWidth()/2));
											}
										});
										btnResetSplit.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/application_tile_horizontal.png")));
										toolBar.add(btnResetSplit);
									}
									btnShowCodeOnly.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/resultset_last.png")));
									toolBar.add(btnShowCodeOnly);
								}
							}
							{
								JToolBar toolBar = new JToolBar();
								GridBagConstraints gbc_toolBar = new GridBagConstraints();
								gbc_toolBar.anchor = GridBagConstraints.WEST;
								gbc_toolBar.gridx = 1;
								gbc_toolBar.gridy = 0;
								panel.add(toolBar, gbc_toolBar);
								{
									JButton btnIncreaseOutputFont = new JButton("");
									btnIncreaseOutputFont.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											setOutputFontSize(outputFontSize+2);
										}
									});
									btnIncreaseOutputFont.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/font_add.png")));
									toolBar.add(btnIncreaseOutputFont);
								}
								{
									JButton btnResetOutputFont = new JButton("");
									btnResetOutputFont.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											setOutputFontSize(18);
										}
									});
									btnResetOutputFont.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/font.png")));
									toolBar.add(btnResetOutputFont);
								}
								{
									JButton btnDecreaseOutputFont = new JButton("");
									btnDecreaseOutputFont.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											setOutputFontSize(outputFontSize-2);
										}
									});
									btnDecreaseOutputFont.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/font_delete.png")));
									toolBar.add(btnDecreaseOutputFont);
								}
								{
									JButton btnClearOutput = new JButton("Clear Output");
									btnClearOutput.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent arg0) {
											txtrOutput.setText("");
										}
									});
									btnClearOutput.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/bin_empty.png")));
									toolBar.add(btnClearOutput);
								}
								{
									chckbxAutoClearOutput = new JCheckBox("Auto Clear Output");
									chckbxAutoClearOutput.setSelected(true);
									toolBar.add(chckbxAutoClearOutput);
								}
							}
						}
						splitPane = new JSplitPane();
						splitPane.setContinuousLayout(true);
						splitPane.setResizeWeight(0.5);
						GridBagConstraints gbc_splitPane = new GridBagConstraints();
						gbc_splitPane.fill = GridBagConstraints.BOTH;
						gbc_splitPane.gridx = 0;
						gbc_splitPane.gridy = 1;
						contentPanel.add(splitPane, gbc_splitPane);
						{
							JScrollPane scrollPane_1 = new JScrollPane();
							scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
							splitPane.setRightComponent(scrollPane_1);
							{
								txtrOutput = new JTextArea();
								txtrOutput.setMargin(new Insets(10,10,10,10));
								txtrOutput.setForeground(Color.WHITE);
								txtrOutput.setBackground(Color.BLACK);
								txtrOutput.setEditable(false);
								txtrOutput.setFont(new Font("Consolas", Font.PLAIN, 18));
								scrollPane_1.setViewportView(txtrOutput);
							}
						}
						rtextScrollPane = new RTextScrollPane();
						rtextScrollPane.setLineNumbersEnabled(true);
						rtextScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
						splitPane.setLeftComponent(rtextScrollPane);
						codeEditor = new RSyntaxTextArea();
						codeEditor.setPaintTabLines(true);
						codeEditor.setTabSize(4);
						codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
						rtextScrollPane.setViewportView(codeEditor);
						//Theme theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
						Theme theme = Theme.load(getClass().getResourceAsStream("/com/nuix/scriptdemo/monokai.xml"));
						theme.apply(codeEditor);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
		}

		{
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			{
				JMenu mnFile = new JMenu("File");
				menuBar.add(mnFile);
				{
					mntmRunScript = new JMenuItem("Run Script");
					mntmRunScript.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							runScript();
						}
					});
					{
						JMenuItem mntmSaveAs = new JMenuItem("Save As...");
						mntmSaveAs.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File outputFile = CommonDialogs.saveFileDialog(defaultScriptLocation, "Ruby Script", "rb", "Save Ruby Script");
								if(outputFile != null){
									FileWriter fw = null;
									PrintWriter pw = null;
									try{
										fw = new FileWriter(outputFile);
										pw = new PrintWriter(fw);
										pw.print(codeEditor.getText());
									}catch(Exception exc){
										CommonDialogs.showError("Error while saving file: "+exc.getMessage());
									}
									finally{
										try {
											fw.close();
										} catch (IOException exc) {}
										pw.close();
									}
								}
							}
						});
						mnFile.add(mntmSaveAs);
					}
					{
						JMenuItem mntmOpen = new JMenuItem("Open...");
						mntmOpen.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								File inputFile = CommonDialogs.openFileDialog(defaultScriptLocation, "Ruby Script", "rb", "Open Ruby Script");
								if(inputFile != null){
									List<String> lines;
									try {
										lines = Files.readAllLines(Paths.get(inputFile.getPath()));
										codeEditor.setText(Joiner.on("\n").join(lines));
									} catch (IOException e) {
										CommonDialogs.showError("Error while opening file: "+e.getMessage());
									}

								}
							}
						});
						mnFile.add(mntmOpen);
					}
					mntmRunScript.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/ruby_go.png")));
					mntmRunScript.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
					mnFile.add(mntmRunScript);
				}
			}
			{
				mnSnippets = new JMenu("Snippets");
				menuBar.add(mnSnippets);
			}
			{
				JMenu mnFontSize = new JMenu("Font Size");
				menuBar.add(mnFontSize);
				{
					JMenuItem menuItemFontSize18 = new JMenuItem("18");
					menuItemFontSize18.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							setEditorFontSize(18);
							setOutputFontSize(18);
						}
					});
					mnFontSize.add(menuItemFontSize18);
				}
				{
					JMenuItem menuItemFontSize24 = new JMenuItem("24");
					menuItemFontSize24.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							setEditorFontSize(24);
							setOutputFontSize(24);
						}
					});
					mnFontSize.add(menuItemFontSize24);
				}
				{
					JMenuItem menuItemFontSize32 = new JMenuItem("32");
					menuItemFontSize32.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							setEditorFontSize(32);
							setOutputFontSize(32);
						}
					});
					mnFontSize.add(menuItemFontSize32);
				}
			}
		}

		setEditorFontSize(18);
		setOutputFontSize(18);
	}

	/***
	 * Sets the font size of the editor text area.
	 * @param size Desired font size.  Provided value is clamped between 11 and 64.
	 */
	public void setEditorFontSize(int size){
		if(size < 11) size = 11;
		if(size > 64) size = 64;
		if(size == editorFontSize) return;

		SyntaxScheme ss = codeEditor.getSyntaxScheme();
		ss = (SyntaxScheme) ss.clone();
		for (int i = 0; i < ss.getStyleCount(); i++) {
			if (ss.getStyle(i) != null && ss.getStyle(i).font != null) {
				Font font = ss.getStyle(i).font;
				Font enlargedFont = new Font(font.getName(), font.getStyle(), size);
				ss.getStyle(i).font = enlargedFont;
			}
		}
		codeEditor.setSyntaxScheme(ss);
		editorFontSize = size;
	}

	/***
	 * Sets the font size of the output text area.
	 * @param size Desired font size.  Provided value is clamped between 11 and 64.
	 */
	public void setOutputFontSize(int size){
		if(size < 11) size = 11;
		if(size > 64) size = 64;
		if(size == outputFontSize) return;
		final int targetSize = size;
		SwingUtilities.invokeLater(()->{
			txtrOutput.setFont(new Font("Consolas", Font.PLAIN, targetSize));
			outputFontSize = targetSize;	
		});
	}

	/***
	 * Switches the interface between locked down and not locked down.  When a value of true is provided
	 * (interface should be locked down) run button and run menu item are disabled and abort button is enabled.
	 * When a value of false is provide, run button and run menu item are enabled and abort button is disabled.
	 * @param value Whether the interface should be locked down or not.
	 */
	private void setLockedForRunningScript(boolean value){
		SwingUtilities.invokeLater(()->{
			runButton.setEnabled(!value);
			mntmRunScript.setEnabled(!value);
			btnAbortScript.setEnabled(value);
		});
	}

	/***
	 * Executes the Ruby code currently present in the input text area.  May also clear the output depending
	 * on whether auto clear output is checked.
	 */
	private void runScript(){
		if(chckbxAutoClearOutput.isSelected()){
			txtrOutput.setText("");
		}
		setLockedForRunningScript(true);
		//currentScriptingContainer = new ScriptingContainer(LocalContextScope.THREADSAFE, LocalVariableBehavior.TRANSIENT);
		currentScriptingContainer = new ScriptingContainer(LocalVariableBehavior.TRANSIENT);
		outputForwarder = new OutputStreamForwarder(ScriptDemoDialog.this);
		currentScriptingContainer.setWriter(outputForwarder);
		currentScriptingContainer.setErrorWriter(outputForwarder);
		currentScriptingContainer.put("$utilities", utilities);
		currentScriptingContainer.put("$current_case", currentCase);
		currentScriptingContainer.put("$current_selected_items", currentSelectedItems);
		currentScriptingContainer.put("$window",currentWindow);
		String script = codeEditor.getText();
		scriptThread = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					ScriptDemoDialog.this.currentScriptingContainer.runScriptlet(script);
				} catch (Exception e1) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e1.printStackTrace(pw);
					accept(sw.toString());
					e1.printStackTrace();
				} finally {
					currentScriptingContainer.terminate();
				}
				setLockedForRunningScript(false);
			}

		});
		scriptThread.start();
	}

	@Override
	public void accept(String text){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				try {
					System.out.print(text);
					txtrOutput.append(text);
					txtrOutput.setCaretPosition(txtrOutput.getDocument().getLength());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/***
	 * Makes the window "small" by changing dimensions to 800x600.
	 */
	public void makeWindowSmall(){
		this.setExtendedState(JFrame.NORMAL); 
		setSize(800,600);
		setLocationRelativeTo(null);
	}

	/***
	 * Makes the window "big" my putting it into a maximized state.
	 */
	public void makeWindowBig(){
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
	}
	
	/***
	 * Maximizes the window to match the screen size, less the specified margin on each side.
	 * @param margins The margins between each side of the window and the dialog windows edges.
	 */
	public void makeWindowBigWithMargins(int margins){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension fillSize = new Dimension((int) screenSize.getWidth() - (margins * 2),
				(int) screenSize.getHeight() - (margins * 2));
		setSize(fillSize);
		setLocationRelativeTo(null);
	}

	private static Object lock = new Object();
	private JButton btnAbortScript;
	private JCheckBox chckbxAutoClearOutput;
	public void display() throws InterruptedException{
		WindowAdapter adapter = new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				synchronized (lock) {
					ScriptDemoDialog.this.setVisible(false);
					lock.notify();
				}
			}

		};
		this.addWindowListener(adapter);
		makeWindowBig();
		setVisible(true);
		Thread t = new Thread() {
			public void run() {
				synchronized(lock) {
					while (ScriptDemoDialog.this.isVisible()){
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		t.start();
		t.join();
	}

	/***
	 * Convenience method for adding a new snippet entry to the snippet menu.
	 * @param label Label the snippet should have in the menu.
	 * @param path Path to the file which should be loaded when the menu entry is clicked.
	 */
	public void addSnippetToMenu(String label, String path){
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					List<String> lines = Files.readAllLines(Paths.get(path));
					String snippet = Joiner.on("\n").join(lines);
					codeEditor.setText(snippet);
					//codeEditor.replaceSelection(snippet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		menuItem.setIcon(new ImageIcon(ScriptDemoDialog.class.getResource("/com/nuix/scriptdemo/ruby.png")));
		mnSnippets.add(menuItem);
	}

	/***
	 * Gets the default directory when saving or loading through the file menu.
	 * @return The default directory when saving or loading through the file menu.
	 */
	public String getDefaultScriptLocation() {
		return defaultScriptLocation;
	}

	/***
	 * Sets the default directory used when saving or loading through the file menu.
	 * @param defaultScriptLocation The default directory to used when saving or loading through the file menu.
	 */
	public void setDefaultScriptLocation(String defaultScriptLocation) {
		this.defaultScriptLocation = defaultScriptLocation;
	}
}
