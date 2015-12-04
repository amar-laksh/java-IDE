import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
//import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

@SuppressWarnings("serial")
public class CodingWindow extends JFrame implements ActionListener, KeyListener
{
private JTextArea ta;
private JMenuBar menuBar;
private JMenu fileM,editM,modeM,testM,helpM,themesM;
private JScrollPane scpane;
private JMenuItem newI,openI,saveI,saveasI,printI,exitI;
private JMenuItem cutI,copyI,pasteI,undoI,redoI,findI,fontI,selectI;
private JMenuItem javaI,htmlI,sqlI,cI;
private JMenuItem runAndcompileI,compileI,executeI,setI;
private JMenuItem contentsI,aboutI;
private JCheckBoxMenuItem darkI,lightI;
private String pad;
private String old_title;
private Boolean save = false;
private String path;
private String ext;
private JToolBar toolBar;
protected Frame frame;
private JTextArea lines;
private JScrollPane bottomPanel;
private String[] undo = new String[500];
private String last;
private int no = 0;
private int mode = 0;
private Label status;
private JScrollPane outpane;
private JScrollPane errorpane;
private Label outlabel;
private Label errorlabel;
private JPanel bottompanel;
private String[] keys =  {
		"abstract", 	
		"continue", 	
		"for", 	
		"new", 	
		"switch",
		"assert", 
		"default", 
		"goto", 	
		"package", 
		"synchronized",
		"boolean", 
		"do", 
		"if", 
		"private", 
		"this",
		"break", 
		"double", 
		"implements", 
		"protected", 
		"throw",
		"byte", 	
		"else", 	
		"import", 	
		"public", 	
		"throws",
		"case", 	
		"enum", 	
		"instanceof", 	
		"return", 	
		"transient",
		"catch", 	
		"extends", 	
		"int", 	
		"short", 	
		"try",
		"char", 	
		"final", 	
		"interface", 	
		"static",	
		"void",
		"class", 	
		"finally", 	
		"long", 	
		"strictfp", 	
		"volatile",
		"const", 	
		"float", 	
		"native", 	
		"super", 	
		"while"
};
public CodingWindow()
{	
    super("All-in-One Text Editor");
    try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException | InstantiationException| IllegalAccessException | UnsupportedLookAndFeelException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    
    //setSize(600, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container pane = getContentPane();
    super.setExtendedState(MAXIMIZED_BOTH);
    pane.setLayout(new BorderLayout());
    super.setAutoRequestFocus(isEnabled());
    super.setFont(getFont());
    pad = " ";
    ta = new JTextArea(){
    	public void addNotify() {
        super.addNotify();
        requestFocus();
    }}; 
    scpane = new JScrollPane();
    bottomPanel = new JScrollPane();
    lines = new JTextArea("1");
    toolBar = new JToolBar();
    menuBar = new JMenuBar();

	outpane = new JScrollPane();
	errorpane = new JScrollPane();
	outlabel = new Label();
	errorlabel = new Label();
	bottompanel = new JPanel();
    
    JTextArea lines = new JTextArea("1");
    status = new Label();
	lines.setForeground(Color.black);
	lines.setEditable(false);

	
	 // DOCUMENT LISTENER FOR LINE NUMBERS	////////////////////////////////////////////////////////////
	ta.getDocument().addDocumentListener(new DocumentListener(){
		public String getText(){
			int caretPosition = ta.getDocument().getLength();
			Element root = ta.getDocument().getDefaultRootElement();
			String text = "1" + System.getProperty("line.separator");
			for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++){
				text += i + System.getProperty("line.separator");
			}
			return text;
		}
		@Override
		public void changedUpdate(DocumentEvent de) {
			lines.setText(getText());
		}

		@Override
		public void insertUpdate(DocumentEvent de) {
			lines.setText(getText());
		}

		@Override
		public void removeUpdate(DocumentEvent de) {
			lines.setText(getText());
		}

	});
	
	scpane.getViewport().add(ta);
	scpane.setRowHeaderView(lines);
	scpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	
	
	 // CARETLISENTER FOR STATUS BAR	////////////////////////////////////////////////////////////
	status.setVisible(true);
	CaretListener listen = new CaretListener() {
        // Each time the caret is moved, it will trigger the listener and its method caretUpdate.
        // It will then pass the event to the update method including the source of the event (which is our textarea control)
        public void caretUpdate(CaretEvent e) {
            JTextArea editArea = (JTextArea)e.getSource();
             
            // Lets start with some default values for the line and column.
            int linenum = 1;
            int columnnum = 1;
            int caretPosition= 1;

            // We create a try catch to catch any exceptions. We will simply ignore such an error for our demonstration.
            try {
                // First we find the position of the caret. This is the number of where the caret is in relation to the start of the JTextArea
                // in the upper left corner. We use this position to find offset values (eg what line we are on for the given position as well as
                // what position that line starts on.
            	caretPosition = ta.getDocument().getLength();
                int caretpos = editArea.getCaretPosition();
                linenum = editArea.getLineOfOffset(caretpos);
       
       
                // We subtract the offset of where our line starts from the overall caret position.
                // So lets say that we are on line 5 and that line starts at caret position 100, if our caret position is currently 106
                // we know that we must be on column 6 of line 5.
                columnnum = caretpos - editArea.getLineStartOffset(linenum);

                // We have to add one here because line numbers start at 0 for getLineOfOffset and we want it to start at 1 for display.
                linenum += 1;
            }
            catch(Exception ex) { }

            // Once we know the position of the line and the column, pass it to a helper function for updating the status bar.
            try {
            	//System.out.println(columnnum);
            	String lastwords;
            	if(columnnum != 0){
            		lastwords = ta.getText(0,caretPosition);
                	}
            	else{
            		lastwords = ta.getText(0,caretPosition);
                	}
            	System.out.println(lastwords.substring(caretPosition-1, caretPosition));
        		
            	if (lastwords.substring(caretPosition-1, caretPosition).equalsIgnoreCase("{")) {
            		//System.out.println((int)columnnum+1);
            		//ta.setCaretPosition((int)(columnnum+1))
            		ta.setText(lastwords+"}");
            	}
				updateStatus(caretPosition,linenum, columnnum);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    private void updateStatus(int total,int linenumber, int columnnumber) throws BadLocationException {
           status.setText("Total Characters: "+total+" | Line: " + linenumber + " |  Column: " + columnnumber);
           
        }
    };
    
	ta.addCaretListener(listen);
	bottomPanel.setColumnHeaderView(status);
	bottomPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	super.add(scpane);
	super.add(bottomPanel,BorderLayout.PAGE_END);
	
	    
    
    
    
    // THIS IS FOR THE FILE MENU	////////////////////////////////////////////////////////////
    fileM = new JMenu("File"); 
    newI = new JMenuItem("New");
    openI = new JMenuItem("Open");
    saveI = new JMenuItem("Save");
    saveasI = new JMenuItem("Save As");
    printI = new JMenuItem("Print");
    exitI = new JMenuItem("Exit");
    fileM.add(newI);
    fileM.add(openI);
    fileM.add(saveI);
    fileM.add(saveasI);
    fileM.add(printI);
    fileM.add(exitI);
    
    newI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    openI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    saveI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    printI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
    exitI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
    
    newI.addActionListener(this);
    openI.addActionListener(this);
    saveI.addActionListener(this);
    printI.addActionListener(this);
    exitI.addActionListener(this);
    old_title = super.getTitle();
    
    ta.addKeyListener(this);
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    
    
   
    
    // THIS IS FOR THE EDIT MENU	////////////////////////////////////////////////////////////
    editM = new JMenu("Edit"); //edit menu
    cutI = new JMenuItem("Cut");
    copyI = new JMenuItem("Copy");
    pasteI = new JMenuItem("Paste");
    undoI = new JMenuItem("Undo");
    redoI = new JMenuItem("Redo");
    findI = new JMenuItem("Find");
    fontI = new JMenuItem("Font");
    selectI = new JMenuItem("Select All");  
    editM.add(cutI);
    editM.add(copyI);
    editM.add(pasteI);        
    editM.add(undoI);
    editM.add(redoI);
    editM.add(findI);
    editM.add(fontI);        
    editM.add(selectI);
   
    cutI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    copyI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    pasteI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
    selectI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    undoI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
    redoI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
    fontI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
    
    cutI.addActionListener(this);
    copyI.addActionListener(this);
    pasteI.addActionListener(this);
    selectI.addActionListener(this);
    undoI.addActionListener(this);
    redoI.addActionListener(this);
    fontI.addActionListener(this);
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    // THIS IS FOR THE MODE MENU	////////////////////////////////////////////////////////////
    modeM = new JMenu("Modes"); //edit menu
    javaI = new JMenuItem("Java");
    htmlI = new JMenuItem("Web D");
    sqlI = new JMenuItem("SQL (Interactive)");
    cI = new JMenuItem("C/C++");
    modeM.add(javaI);
    modeM.add(htmlI);
    modeM.add(sqlI);
    modeM.add(cI);
    
    javaI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.ALT_MASK));
    htmlI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.ALT_MASK));
    sqlI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
    cI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
    
    javaI.addActionListener(this);
    htmlI.addActionListener(this);
    sqlI.addActionListener(this);
    cI.addActionListener(this);
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    
    // THIS IS FOR THE TEST MENU	////////////////////////////////////////////////////////////
    testM = new JMenu("Test"); //edit menu
    runAndcompileI = new JMenuItem("Run & Compile");
    compileI = new JMenuItem("Compile");
    executeI = new JMenuItem("Execute Query");
    setI = new JMenuItem("Set Environment");
    testM.add(runAndcompileI);
    testM.add(compileI);
    testM.add(executeI);
    testM.add(setI);
    
    runAndcompileI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, ActionEvent.CTRL_MASK));
    compileI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, ActionEvent.ALT_MASK));
    executeI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK));
    setI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, ActionEvent.CTRL_MASK));
    
    runAndcompileI.addActionListener(this);
    compileI.addActionListener(this);
    executeI.addActionListener(this);
    setI.addActionListener(this);
    
    // THIS IS FOR THE THEMES MENU	////////////////////////////////////////////////////////////
    themesM = new JMenu("Themes"); 
    darkI = new JCheckBoxMenuItem("DarKnight");
    lightI = new JCheckBoxMenuItem("LightSaber");
    
    themesM.add(darkI);
    themesM.add(lightI);
    
    darkI.addActionListener(this);
    lightI.addActionListener(this);
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    
    // THIS IS FOR THE HELP MENU	////////////////////////////////////////////////////////////
    helpM = new JMenu("Help"); //edit menu
    contentsI = new JMenuItem("Contents");
    aboutI = new JMenuItem("About us");
    helpM.add(contentsI);
    helpM.add(aboutI);
    
    contentsI.addActionListener(this);
    aboutI.addActionListener(this);
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    
    setJMenuBar(menuBar);
    menuBar.add(fileM);
    menuBar.add(editM);
    menuBar.add(themesM);
    menuBar.add(modeM);
    menuBar.add(testM);
    menuBar.add(helpM);
    ta.setCaretColor(Color.GREEN);
    ta.setForeground(Color.GREEN);
    ta.setBackground(Color.BLACK);
    ta.setLineWrap(true);
    ta.setWrapStyleWord(true);
    
    


    pane.add(scpane,BorderLayout.CENTER);
    pane.add(toolBar,BorderLayout.SOUTH);

    setVisible(true);
}
public void keyPressed(KeyEvent e){
	   super.setTitle(old_title+"*");
       
    }

@Override
public void keyTyped(KeyEvent e) {
	// TODO Auto-generated method stub
	
}
@Override
public void keyReleased(KeyEvent e) {
	// TODO Auto-generated method stub
	//old_title = old_title.substring(0,old_title.lastIndexOf("*")+1);
    
}



public void actionPerformed(ActionEvent e) 
{
    JMenuItem choice = (JMenuItem) e.getSource();
    if (choice == newI){
    	newdialog();
    }
    else if (choice == saveI){
    	savedialog();
    }
    else if (choice == openI){   
    	opendialog();
    }
    else if (choice == exitI){
        System.exit(0);
    }
    else if (choice == cutI){
    	cutfunc();
    }
    else if (choice == copyI){
    	copyfunc();
    }
    else if (choice == pasteI){
    	pastefunc();
    }
    else if (choice == selectI){
    	selectfunc();
    }
    else if (choice == undoI){
    	undofunc();
    }
    else if (choice == aboutI){
    	aboutfunc();
    }
    else if	(choice == runAndcompileI){
    	runAndcompilefunc();
    }
    else if (choice == darkI){
    	darkfunc();
    }
    else if	(choice == lightI){
    	lightfunc();
    }
    else if	(choice == htmlI){
    	htmlfunc();
    }
    else if	(choice == javaI){
    	javafunc();
    } 
    else if	(choice == cI){
    	cfunc();
    }
}

///////////////////  gives a confirmation dialog box when new is selected  ///////////////////////////
private void newdialog(){
	
	int result=JOptionPane.showConfirmDialog((Component) null, "Are you sure?","You sure boss?", JOptionPane.OK_CANCEL_OPTION);
	switch(result) {
	case 0:
		ta.setText(null);
		super.setTitle(old_title);
		save = false;
	case 1:
		break;
	}
}	

public CodingWindow(String extension) {
    ext = "." + extension;
  }

  public boolean accept(File dir, String name) {
    return name.endsWith(ext);
  }

///////////////  gives a dialog box for save function  //////////////////////
private void savedialog(){
	try {
	if (save == false){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");  
		FileFilter javaf = new FileNameExtensionFilter("Java Source (.java)", new String[] {"java"});
		FileFilter htmlf = new FileNameExtensionFilter("HTML Source (.htm | .html)", new String[] {"htm","html"});
		FileFilter cssf = new FileNameExtensionFilter("CSS Stylesheet (.css)", new String[] {"css"});
		FileFilter javascriptf = new FileNameExtensionFilter("JavaScript Source (.js | .jsp)", new String[] {"js","jsp"});
		FileFilter sqlf = new FileNameExtensionFilter("SQL Query (.sql)", new String[] {"sql"});
		FileFilter cf = new FileNameExtensionFilter("C/C++ Programs (.c | .cpp)", new String[] {"c","cpp"});
		
		fileChooser.addChoosableFileFilter(javaf);
	    fileChooser.addChoosableFileFilter(htmlf);
	    fileChooser.addChoosableFileFilter(cssf);
	    fileChooser.addChoosableFileFilter(javascriptf);
	    fileChooser.addChoosableFileFilter(sqlf);
	    fileChooser.addChoosableFileFilter(cf);
	    
		int userSelection = fileChooser.showSaveDialog(frame);
		File fileToSave = fileChooser.getSelectedFile();
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			System.out.println("Save as file: " + fileToSave.getAbsolutePath());
		}
		
		path = fileToSave.getAbsolutePath();
		String areaText = ta.getText();
		if(fileChooser.getSelectedFile()!=null){
        	super.setTitle("All-in-One Text Editor - \"" + fileToSave.getAbsolutePath()+"\"");
        	/* Different Modes */
	        if(path.endsWith(".html")|| path.endsWith(".htm")){
	       	 mode = 1;
	        }
	        else if(path.endsWith(".java")){
	        	mode = 2;
	        	}
	        
	        else if(path.endsWith(".c")|| path.endsWith(".cpp")){
	        	mode = 3;
	        }
	        PrintWriter writer;
			try {
				writer = new PrintWriter(path);
				writer.write(areaText);
				writer.close();
				save = true;
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	       }
        else {
        	super.setTitle("All-in-One Text Editor");
        	
        }
	
        
	}
	else if (save == true) {
		super.setTitle(old_title);
		/* Different Modes */
        if(path.endsWith(".html")|| path.endsWith(".htm")){
       	 mode = 1;
        }
        else if(path.endsWith(".java")){
        	mode = 2;
        	}
        
        else if(path.endsWith(".c")|| path.endsWith(".cpp")){
        	mode = 3;
        }
        String areaText = ta.getText();
        PrintWriter writer;
		try {
			writer = new PrintWriter(path);
			writer.append(areaText);
			writer.close();
		
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	}
	catch (Exception e) {
		
	}
}

//////////////////////  gives a dialog for open function  /////////////////////
private void opendialog(){
	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setDialogTitle("Specify a file to open");  
	FileFilter javaf = new FileNameExtensionFilter("Java Source (.java)", new String[] {"java"});
	FileFilter htmlf = new FileNameExtensionFilter("HTML Source (.htm | .html)", new String[] {"htm","html"});
	FileFilter cssf = new FileNameExtensionFilter("CSS Stylesheet (.css)", new String[] {"css"});
	FileFilter javascriptf = new FileNameExtensionFilter("JavaScript Source (.js | .jsp)", new String[] {"js","jsp"});
	FileFilter sqlf = new FileNameExtensionFilter("SQL Query", new String[] {"sql"});
	FileFilter cf = new FileNameExtensionFilter("C/C++ Programs (.c | .cpp)", new String[] {"c","cpp"});
	fileChooser.addChoosableFileFilter(javaf);
    fileChooser.addChoosableFileFilter(htmlf);
    fileChooser.addChoosableFileFilter(cssf);
    fileChooser.addChoosableFileFilter(javascriptf);
    fileChooser.addChoosableFileFilter(sqlf);
    fileChooser.addChoosableFileFilter(cf);
 
	
	int userSelection = fileChooser.showOpenDialog(frame);
	File fileToSave = fileChooser.getSelectedFile();
	
	String path = fileToSave.getAbsolutePath();
    BufferedReader br;
    if(fileChooser.getSelectedFile()!=null) {
    	try {
    		this.path = path;
        	br = new BufferedReader(new FileReader(path));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			save = true;
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		        /* Different Modes */
		        if(path.endsWith(".html")|| path.endsWith(".htm")){
		       	 mode = 1;
		        }
		        else if(path.endsWith(".java")){
		        	mode = 2;
		        	}
		        
		        else if(path.endsWith(".c")|| path.endsWith(".cpp")){
		        	mode = 3;
		        }
		    }
		    String everything = sb.toString();
		    ta.setText(everything);
		 
    	}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
}

/////////////////  for the cut operation  //////////////////////////
private void cutfunc(){
	
	 pad = ta.getSelectedText();
     ta.replaceRange("", ta.getSelectionStart(), ta.getSelectionEnd());
}

/////////////////  for the Dark theme operation  //////////////////////////
private void darkfunc(){
if (darkI.isSelected()==false) {
	darkI.setSelected(true);
	themesM.add(darkI);
	ta.setBackground(Color.WHITE);
	ta.setForeground(Color.BLACK);
	ta.setCaretColor(Color.BLACK);
}
else {
	lightI.setSelected(false);
	themesM.add(lightI);
	ta.setBackground(Color.BLACK);
	ta.setForeground(Color.GREEN);
	ta.setCaretColor(Color.GREEN);
}

}

/////////////////  for the Light theme operation  //////////////////////////
private void lightfunc(){
if (lightI.isSelected()==false) {
	lightI.setSelected(true);
	themesM.add(lightI);
	ta.setBackground(Color.BLACK);
	ta.setForeground(Color.GREEN);
	ta.setCaretColor(Color.GREEN);
}
else {
	darkI.setSelected(false);
	themesM.add(darkI);
	ta.setBackground(Color.WHITE);
	ta.setForeground(Color.BLACK);
	ta.setCaretColor(Color.BLACK);
}

}

/////////////////  for the undo operation  //////////////////////////
private void undofunc(){
}



///////////////////  for the copy operation  ////////////////////////////
private void copyfunc()
{
	pad = ta.getSelectedText();
}


///////////////////  for the html operation  ////////////////////////////
private void htmlfunc()
{
 if(path.endsWith(".html")|| path.endsWith(".htm")){
	 mode = 1;
 }
 else {
	 mode = -99;
 }
System.out.println("mode set: "+mode);
}

///////////////////  for the c operation  ////////////////////////////
private void cfunc()
{
if(path.endsWith(".c")|| path.endsWith(".cpp")){
mode = 3;
}
else {
	mode = -99;
}
}

///////////////////  for the java operation  ////////////////////////////
private void javafunc()
{
	 if(path.endsWith(".java")){
		 mode = 2;
	 }
	 else {
		 mode = -99;
	 }
	 System.out.println("mode set: "+mode);
}


////////////////////   for the paste operation  ////////////////////////
private void pastefunc(){
	ta.insert(pad, ta.getCaretPosition());
}


////////////////////for the about func  ////////////////////////
private void aboutfunc(){
	JOptionPane.showMessageDialog(aboutI, "This is an IDE developed soley for the purposes of an IT Student.\n (Apparently by an IT Student name Amar Lakshya)","About Us",no);
	
}

///////////////////  for the select all func   ////////////////////////
private void selectfunc(){
	
	ta.selectAll();
}


////////////////////for the run operation  ////////////////////////
private void runAndcompilefunc(){
	if (mode == 1 && mode >=0 ) {
		try {
			this.path=path.replace(" ", "%20");
			this.path=path.replace("\\", "/");
			URI uri = new URL("file:///"+path).toURI();
            System.out.println("path set: "+uri);
    		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
                desktop.browse(uri);} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	else if (mode == 2 && mode >=0) {
		Runtime rt = Runtime.getRuntime();
		String java2 = path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".java"));
		String javapath = path.substring(0, path.lastIndexOf("\\"))+"\\";
		System.out.println("java app: "+java2);
		System.out.println("javapath:"+javapath);
		String c1 = "javac "+path;
		String c2 = "java -classpath "+javapath+" "+java2;
		String[] commands = {c1,c2};
		Process proc;
		System.out.println("command 1: "+c1.toString());
		System.out.println("command 2: "+c2.toString());
		String val2 = null;
		try {
			java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(commands[0]).getInputStream()).useDelimiter("\\A");
			 String val = "";
		        if (s.hasNext()) {
		            val = s.next();
		            System.out.println("Output:"+val);
		            outlabel.setText(val);
		        }
		        else {
		            val = "";
		        }
		        
		        java.util.Scanner er = new java.util.Scanner(Runtime.getRuntime().exec(commands[0]).getErrorStream()).useDelimiter("\\A");
				val2 = "";
			        if (er.hasNext()) {
			            val2 = er.next();
			            System.out.println("Error:"+val2);
			            errorlabel.setText(val2);
			        }
			        else {
			            val2 = "";
			        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(val2==null || val2 == "") {
			try {
				errorlabel.setText("");
				java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(commands[1]).getInputStream()).useDelimiter("\\A");
				 String val = "";
			        if (s.hasNext()) {
			            val = s.next();
			            System.out.println("Output:"+val);
			            outlabel.setText(val);
			        }
			        else {
			            val = "";
			        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			errorlabel.setText(val2);
		}
		
		outlabel.setVisible(true);
		errorlabel.setVisible(true);
		status.setText(status.getText()+" | ERRORS: "+errorlabel.getText()+"  | OUTPUT: "+outlabel.getText());				
		
	}
	
	
	else if (mode == 3 && mode >=0) {
		Runtime rt = Runtime.getRuntime();
		String cprog2 = "";
		if(path.endsWith(".c")){
			cprog2 = path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".c"));
		}
		else if(path.endsWith(".cpp")){
			cprog2 = path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".cpp"));
		}
		String cprogpath = path.substring(0,path.lastIndexOf("\\"))+"\\";
		
		System.out.println("c app: "+cprog2);
		System.out.println("c path:"+cprogpath);
		String c1 = "gcc -o "+cprogpath+cprog2+" "+path;
		String c2 = cprogpath+cprog2;
		String[] commands = {c1,c2};
		Process proc;
		System.out.println("command 1: "+c1.toString());
		String val2 = null;
		try {
			java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(commands[0]).getInputStream()).useDelimiter("\\A");
			 String val = "";
		        if (s.hasNext()) {
		            val = s.next();
		            System.out.println("Output:"+val);
		            outlabel.setText(val);
		        }
		        else {
		            val = "";
		        }
		        
		        java.util.Scanner er = new java.util.Scanner(Runtime.getRuntime().exec(commands[0]).getErrorStream()).useDelimiter("\\A");
				val2 = "";
			        if (er.hasNext()) {
			            val2 = er.next();
			            System.out.println("Error:"+val2);
			            errorlabel.setText(val2);
			        }
			        else {
			            val2 = "";
			        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(val2==null || val2 == "") {
			try {
				errorlabel.setText("");
				java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(commands[1]).getInputStream()).useDelimiter("\\A");
				 String val = "";
			        if (s.hasNext()) {
			            val = s.next();
			            System.out.println("Output:"+val);
			            outlabel.setText(val);
			        }
			        else {
			            val = "";
			        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			errorlabel.setText(val2);
		}
		
		outlabel.setVisible(true);
		errorlabel.setVisible(true);
		status.setText(status.getText()+" | ERRORS: "+errorlabel.getText()+"  | OUTPUT: "+outlabel.getText());				
		
	}
}

public static void main(String[] args) 
{
	new CodingWindow();
}
}