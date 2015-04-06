
package jnotepad;;
import java.awt.*;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

/**
 *
 * @author madhavchhura
 */
public class JNotepad {
    JMenu fileMenu, editMenu, formatMenu, viewMenu, helpMenu;
    JMenuBar menuBar;
    JMenuItem newMenuItem, copyMenuItem, pasteMenuItem, deleteMenuItem, findMenuItem, find_nextMenuItem;
    JMenuItem replaceMenuItem, go_toMenuItem, selectallMenuItem, time_dateMenuItem;
    JMenuItem word_wrapMenuItem, fontMenuItem, openMenuItem, statusMenuItem;
    JMenuItem view_helpMenuItem, aboutMenuItem, saveMenuItem, saveAsMenuItem, page_setupMenuItem;
    JMenuItem printMenuItem, undoMenuItem, cutMenuItem, exitMenuItem;
    JFileChooser jFileChooser1;
    JFrame frame;
    JTextArea textArea;
    File file;
    PrinterJob job = PrinterJob.getPrinterJob();
    boolean fileSaved = true;
    boolean fileModified = false;
    boolean frameOpenedBefore = false;
    boolean savedBefore = false;
    final JPopupMenu popupMenu;
    findDialog findDialogBox;
    final Action[] textActions = { new DefaultEditorKit.CutAction(),
        new DefaultEditorKit.CopyAction(), new DefaultEditorKit.PasteAction()};
    JNotepad(){
    
        //Frame Settings
        frame = new JFrame("Untitled - JNotepad");
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(!fileSaved){
                    if(fileNotSaved())
                        System.exit(0);
                }
                else
                    System.exit(0);
            }
        });
        
        
        //File Chooser Settings
        jFileChooser1 = new JFileChooser();
        jFileChooser1.setMultiSelectionEnabled(false);
        jFileChooser1.setAcceptAllFileFilterUsed(true);
        jFileChooser1.setFileFilter(new FileNameExtensionFilter("Java (*.java)", "java"));
        jFileChooser1.setFileFilter(new FileNameExtensionFilter("Text Documents (*.txt)", "txt"));
        
        //Cut, Copy and Paste - PopMenu
        popupMenu = new JPopupMenu();
        textActions[0].setEnabled(false);
        textActions[1].setEnabled(false);
        textActions[2].setEnabled(true);
        popupMenu.add(new JMenuItem(textActions[0]));
        popupMenu.add(new JMenuItem(textActions[1]));
        popupMenu.add(new JMenuItem(textActions[2]));
        
        UndoManager undoManager = new UndoManager();
        
        textArea= new JTextArea();
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);
      
        textArea.setComponentPopupMenu(popupMenu);
        textArea.getDocument().addUndoableEditListener(undoManager);
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
               showPopupMenu(e);
            }
            
            private void showPopupMenu(MouseEvent e){
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(),e.getX(), e.getY());
                }
            }
        });
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableMenuItems(); 
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableMenuItems();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
               enableMenuItems();
            }
        });
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent arg0) {
                if(textArea.getSelectedText() != null){
                    copyMenuItem.setEnabled(true);
                    cutMenuItem.setEnabled(true);
                    deleteMenuItem.setEnabled(true);
                    textActions[0].setEnabled(true);
                    textActions[1].setEnabled(true);
                }
                else{
                    copyMenuItem.setEnabled(false);
                    cutMenuItem.setEnabled(false);
                    deleteMenuItem.setEnabled(false);
                    textActions[0].setEnabled(false);
                    textActions[1].setEnabled(false);
                }
            }
        });
        

        //Instatiate all menu, menuItems, and set their texts.
        setMenu();

        fileMenu.setMnemonic('F');
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        newMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!fileSaved){
                    if(fileNotSaved())
                      textArea.setText("");  
                }
                else
                    textArea.setText("");
            }
        });
        fileMenu.add(newMenuItem);

        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        openMenuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!fileSaved){
                    if(fileNotSaved())
                      openFile();  
                }
                else
                    openFile(); 
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                    saveFile();
                    //file = null;
                
            }
        });
        fileMenu.add(saveMenuItem);
        
        saveAsMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();

            }
        });
        fileMenu.add(saveAsMenuItem);
        
        
        fileMenu.addSeparator();

        page_setupMenuItem.setMnemonic('u');
        fileMenu.add(page_setupMenuItem);
      
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        printMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(job.printDialog()){
                    try {
                        job.setJobName(file.getName());
                        job.print();
                    } catch (PrinterException ae) {
                        // The job did not successfully complete
                    }
                }
            }
        });
        fileMenu.add(printMenuItem);
        fileMenu.addSeparator();
        
        
        exitMenuItem.setMnemonic('x');
        exitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!fileSaved){
                    if(fileNotSaved())
                        System.exit(0);
                }
                else
                    System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        
        menuBar.add(fileMenu);

        //Make the Edit Menu.
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(undoManager.canUndo()){
                    undoManager.undo();
                    if(!undoManager.canUndo())
                        undoMenuItem.setEnabled(false);
                }
            }
        });
        editMenu.add(undoMenuItem);
        editMenu.addSeparator();
        
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        editMenu.add(cutMenuItem);
        
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        editMenu.add(copyMenuItem);

        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        editMenu.add(pasteMenuItem);
        
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
        deleteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(textArea.getText() != null)
                    textArea.setText(textArea.getText().replace(textArea.getSelectedText(),""));
            }
        });
        editMenu.add(deleteMenuItem);
        editMenu.addSeparator();

        findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        findMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                findDialogBox = new findDialog(frame);
                frameOpenedBefore = true;
            }
        });
        
        editMenu.add(findMenuItem);

    
        find_nextMenuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(!frameOpenedBefore)
                    findDialogBox = new findDialog(frame);
                else{
                    findDialogBox.findNextButtonAction();
                }    
            }
        });
        editMenu.add(find_nextMenuItem);

        replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        replaceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               replaceBox box = new replaceBox();
            }
        });
        editMenu.add(replaceMenuItem);

        go_toMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
        go_toMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               
            }
        });
        editMenu.add(go_toMenuItem);
        editMenu.addSeparator();
       

        selectallMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        selectallMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(textArea.getText() != null)
                    textArea.selectAll();
            }
        });
        editMenu.add(selectallMenuItem);
        time_dateMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        time_dateMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Date date = new Date();
                SimpleDateFormat ft = new SimpleDateFormat ("hh:mm a M/dd/yyyy");
                textArea.insert(ft.format(date), textArea.getCaretPosition());
            }
        });
        editMenu.add(time_dateMenuItem);
        menuBar.add(editMenu);
        
        //Format Menu
        formatMenu.setMnemonic('o');
        word_wrapMenuItem.setMnemonic('W');
        word_wrapMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }
        });
        formatMenu.add(word_wrapMenuItem);
        fontMenuItem.setMnemonic('F');
        fontMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFontChooser fontChooser = new JFontChooser();
                fontChooser.setDefaultName(textArea.getFont().getName());
                fontChooser.setDefaultStyle(textArea.getFont().getStyle());
                fontChooser.setDefaultSize(textArea.getFont().getSize());
                
                fontChooser.okButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                       fontChooser.fontChooserFrame.dispose();
                       textArea.setFont(new Font(
                            fontChooser.getSelectedFontName(),
                            fontChooser.getSelectedFontStyle(), 
                            fontChooser.getSelectedFontSize()));
                    }
                });
                
                fontChooser.cancelButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fontChooser.fontChooserFrame.dispose();
                    }
                });
            }
        });
        formatMenu.add(fontMenuItem);
        menuBar.add(formatMenu);
        
        //View Menu
        viewMenu.setMnemonic('V');
        statusMenuItem.setMnemonic('S');
        viewMenu.add(statusMenuItem);
        menuBar.add(viewMenu);

        //Help Menu
        helpMenu.setMnemonic('H');
        view_helpMenuItem.setMnemonic('H');
        helpMenu.add(view_helpMenuItem);
        helpMenu.addSeparator();
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(frame, "About Notepad");

                dialog.setSize(200, 125);
                dialog.setLocationRelativeTo(null);
                dialog.setModal(true);
                
                JLabel aboutLabel = new JLabel("(c) Madhav Chhura");
                aboutLabel.setHorizontalAlignment(JLabel.CENTER);
                
                dialog.add(aboutLabel);
                
                dialog.setVisible(true);
            }
        });
        helpMenu.add(aboutMenuItem);
        
        menuBar.add(helpMenu);

        frame.add(scrollPane);
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }
    
    private void enableMenuItems(){
        if(textArea.getText().length() > 0){
            fileSaved = false;
            undoMenuItem.setEnabled(true);
            findMenuItem.setEnabled(true);
            find_nextMenuItem.setEnabled(true);
        }
        else{
            fileSaved = false;
            undoMenuItem.setEnabled(false);
            findMenuItem.setEnabled(false);
            find_nextMenuItem.setEnabled(false);
        }
    }
    
    private void setMenu(){
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newMenuItem = new JMenuItem("New");
        openMenuItem = new JMenuItem("Open...");
        saveMenuItem = new JMenuItem("Save");
        saveAsMenuItem = new JMenuItem("Save As...");
        page_setupMenuItem = new JMenuItem("Page Setup");
        page_setupMenuItem.setEnabled(false);
        printMenuItem = new JMenuItem("Print");
        exitMenuItem = new JMenuItem("Exit");
        editMenu = new JMenu("Edit");
        undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setEnabled(false);
        cutMenuItem = new JMenuItem(textActions[0]);
        cutMenuItem.setText("Cut");
        cutMenuItem.setEnabled(false);
        copyMenuItem = new JMenuItem(textActions[1]);
        copyMenuItem.setText("Copy");
        copyMenuItem.setEnabled(false);
        pasteMenuItem = new JMenuItem(textActions[2]);
        pasteMenuItem.setText("Paste");
        pasteMenuItem.setEnabled(true);
        deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setEnabled(false);
        findMenuItem = new JMenuItem("Find");
        findMenuItem.setEnabled(false);
        find_nextMenuItem = new JMenuItem("Find Next");
        find_nextMenuItem.setEnabled(false);
        replaceMenuItem = new JMenuItem("Replace");
        go_toMenuItem = new JMenuItem("Go To");
        selectallMenuItem = new JMenuItem("Select All");
        time_dateMenuItem = new JMenuItem("Time/Date");
        formatMenu = new JMenu("Format");
        word_wrapMenuItem = new JMenuItem("Word Wrap");
        fontMenuItem = new JMenuItem("Font");
        viewMenu = new JMenu("View");
        statusMenuItem = new JMenuItem("Status Bar");
        statusMenuItem.setEnabled(false);
        helpMenu = new JMenu("Help");
        view_helpMenuItem = new JMenuItem("View Help");
        view_helpMenuItem.setEnabled(false);
        aboutMenuItem = new JMenuItem("About JNotepad");
    }
    
    private boolean fileNotSaved() {
        String filePath = "Untitled";
        if(file != null)
            filePath = file.getPath();
        int result = JOptionPane.showOptionDialog(frame,
                "Do you want to save changes to " + filePath,
                "JNotepad", 
                JOptionPane.YES_NO_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE, 
                null,
                new String[]{"Cancel","Don't Save", "Save"}, null);
 
        if (result == 2) 
            return saveFile();
        
        else if(result == 1)
            return true;
        
        return false;
    }
    
    private void openFile(){
        if (JFileChooser.APPROVE_OPTION == jFileChooser1.showOpenDialog(frame)) {
            file = jFileChooser1.getSelectedFile();
            frame.setTitle(file.getName() + "- JNotepad");
            textArea.setText("");
            Scanner in = null;
            try {
                in = new Scanner(file);
                while (in.hasNext()) {
                    String line = in.nextLine();
                    textArea.append(line + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                in.close();
            }
            fileSaved = true;
        }
    }
   
    
    private boolean saveFile(){
        if(file != null){
            if(!fileSaved){
                System.out.println("In this method.");
                File tempFile = new File("tempfile.txt");
                
                PrintWriter out = null;
                
                
                try {
                    out = new PrintWriter(tempFile);
                    String output = textArea.getText();
                    out.println(output);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        out.flush();
                        out.close();
                    } catch (Exception ex1) {
                    }
                }
                file.delete();
                tempFile.renameTo(file);
                file.delete();
                file = tempFile;
                
                fileSaved = true;
                return true;
            }
            return false;
        }
        else{
            if (JFileChooser.APPROVE_OPTION == jFileChooser1.showSaveDialog(frame)) {
                file = jFileChooser1.getSelectedFile();
                if (file.exists()) {
                    int result = JOptionPane.showConfirmDialog(frame,
                            file.getName() + " already exists." + "\n Do you want to replace it ",
                            "Confirm Save As",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                 
                    if(result == JOptionPane.YES_OPTION){
                        PrintWriter out = null;
                        try {
                            out = new PrintWriter(file + "."+ jFileChooser1.getFileFilter().getDescription());
                            String output = textArea.getText();
                            out.println(output);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            try {
                                out.flush();
                                out.close();
                            } catch (Exception ex1) {
                            }
                        }
                        frame.setTitle(file.getName() + "- JNotepad"); 
                        fileSaved = true;
                        return true;
                        }
                    else 
                        return false;
                }
                else{
                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(file + ".txt");
                        String output = textArea.getText();
                        out.println(output);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            out.flush();
                        } catch (Exception ex1) {
                        }
                        try {
                            out.close();
                        } catch (Exception ex1) {
                        }
                    }
                    frame.setTitle(file.getName() + "- JNotepad");
                    fileSaved = true;
                    return true;
                }
                
                
            }
//            if (JFileChooser.APPROVE_OPTION == jFileChooser1.showSaveDialog(frame)) {
//                file = jFileChooser1.getSelectedFile();
//                
//            }
//            return false;
        }
        return false;
    
    }
    
    class findDialog{
        private JButton cancelButton;
        private JRadioButton downRButton;
        private JButton findNextButton;
        private JLabel findWhatLabel;
        private JCheckBox jCheckBox1;
        private JPanel panel, panel1;
        private JTextField findField;
        private JRadioButton upRButton;
        //private JFrame frame;
        private JDialog dialog;
        private int pos = 0;
        private boolean matchCaseSet = false;
        
        findDialog(JFrame parentFrame){
            dialog = new JDialog(parentFrame, "Find");
            dialog.setSize(497,112);
            dialog.setLocationRelativeTo(null);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dialog.setVisible(false);
                }
            });
            dialog.setModal(true);
            dialog.setModalityType(Dialog.ModalityType.MODELESS);
            
            findWhatLabel = new JLabel();
            findField = new JTextField();
            findNextButton = new JButton();
            jCheckBox1 = new JCheckBox();
            cancelButton = new JButton();
            panel = new JPanel();
            panel1 = new JPanel();
            panel1.setBorder(BorderFactory.createTitledBorder("Direction"));
            ButtonGroup group = new ButtonGroup();
            upRButton = new JRadioButton();
            downRButton = new JRadioButton();
            group.add(upRButton);
            group.add(downRButton);
            panel1.add(upRButton);
            panel1.add(downRButton);

            findWhatLabel.setText("Find What:");
            findNextButton.setText("Find Next");
            findNextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    findNextButtonAction();
                }
            });

            jCheckBox1.setText("Match Case");
            jCheckBox1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if(jCheckBox1.isSelected())
                        matchCaseSet = true;
                    else 
                        matchCaseSet = false;
                }
            });

            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });
            upRButton.setText("Up");
            downRButton.setText("Down");
         
            GroupLayout jPanel1Layout = new GroupLayout(panel);
            panel.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jCheckBox1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                            .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(120, 120, 120))
                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(findWhatLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(findField, GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(findNextButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap(49, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1, GroupLayout.Alignment.TRAILING)
                            .addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap())
                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(findWhatLabel)
                                        .addComponent(findField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(findNextButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton)
                                .addContainerGap(29, Short.MAX_VALUE)))
            );
            
            dialog.setResizable(false);
            dialog.add(panel);
            dialog.setVisible(true);
            
        }
        private void findNextButtonAction(){
            // Get the text to find...convert it to lower case for eaiser comparision
            if(matchCaseSet){
                String find = findField.getText();
                find(find);
            }
            else{
                String find = findField.getText().toLowerCase();
                find(find);
            }  
        } 

        private void find(String find) {
            // Focus the text area, otherwise the highlighting won't show up
            textArea.requestFocusInWindow();
            // Make sure we have a valid search term
            if (find != null && find.length() > 0) {
                Document document = textArea.getDocument();
                int findLength = find.length();
                try {
                    boolean found = false;

                    if (pos + findLength > document.getLength()) {
                        pos = 0;
                    }

                    while (pos + findLength <= document.getLength()) {
                        // Extract the text from teh docuemnt
                        //String match = document.getText(pos, findLength).toLowerCase();
                        String match = document.getText(pos, findLength);
                        // Check to see if it matches or request
                        if (match.equals(find)) {
                            found = true;
                            break;
                        } else if (pos + findLength == document.getLength()) {
                            JOptionPane.showMessageDialog(frame,
                                    "Cannot find \"" + find + "\"",
                                    "JNotepad",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        pos++;
                    }

                    // Did we find something...
                    if (found) {
                        // Get the rectangle of the where the text would be visible...
                        Rectangle viewRect = textArea.modelToView(pos);
                        // Scroll to make the rectangle visible
                        textArea.scrollRectToVisible(viewRect);
                        // Highlight the text
                        textArea.setCaretPosition(pos + findLength);
                        textArea.moveCaretPosition(pos);
                        // Move the search position beyond the current match
                        pos += findLength;
                    }

                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        }
    }
    
    class replaceBox extends JDialog implements ActionListener {

        private JLabel lbl_replace, lbl_replacewith;
        private JTextField txt_replace, txt_replacewith;
        private JButton btn_change, btn_close;
        private JFrame replaceFrame;

        public replaceBox() {
            replaceFrame = new JFrame("Replace");
            //replaceFrame.setSize(200,200);
            lbl_replace = new JLabel("Replace");
            lbl_replace.setBounds(50, 50, 100, 30);
            replaceFrame.add(lbl_replace);

            txt_replace = new JTextField();
            txt_replace.setBounds(100, 50, 100, 30);
            replaceFrame.add(txt_replace);

            lbl_replacewith = new JLabel("ReplaceWith");
            lbl_replacewith.setBounds(50, 100, 100, 30);
            replaceFrame.add(lbl_replacewith);

            txt_replacewith = new JTextField();
            txt_replacewith.setBounds(100, 100, 100, 30);
            replaceFrame.add(txt_replacewith);

            btn_change = new JButton("Change");
            btn_change.setBounds(50, 150, 50, 30);
            replaceFrame.add(btn_change);

            btn_close = new JButton("Close");
            btn_close.setBounds(75, 150, 125, 30);
            replaceFrame.add(btn_close);

            replaceFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    System.exit(0);

                }
            });
            replaceFrame.setVisible(true);
            
        }

        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == btn_change) {

            }
        }
    }
    
    class JFontChooser {

        String fontName = "Aerial";
        int fontStyle = 1, fontSize = 12;
        JPanel mainPanel;
        JFrame fontChooserFrame;

        DefaultListModel model;
        JList list;
        JComboBox box, box1;
        JScrollPane pane;

        final JLabel fontLabel, styleLabel, sizeLabel;
        JLabel previewLabel;
        Font font;
        boolean changesMade = false;
        JButton okButton, cancelButton;

        JFontChooser() {

            fontChooserFrame = new JFrame("Font");
            fontChooserFrame.setSize(550, 300);
            fontChooserFrame.setLocationRelativeTo(null);
            fontChooserFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    fontChooserFrame.dispose();
                }
            });

            fontLabel = new JLabel("Font:");
            styleLabel = new JLabel("Font Style:");
            sizeLabel = new JLabel("Size");

            JPanel mainPanel = new JPanel();

            model = new DefaultListModel();
            list = new JList(model);
            pane = new JScrollPane(list);
            pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames();

            for (String fontName1 : fontNames) {
                model.addElement(fontName1);
            }
            list.setSelectedValue("Arial", true);
            list.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                     setDefaultName(list.getSelectedValue().toString());
                }
            });

            box = new JComboBox();
            box.addItem("Plain");
            box.addItem("Italic");
            box.addItem("Bold");
            box.addItem("Bold Italic");
            box.setSelectedItem("Plain");
            box.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                     setDefaultStyle(box.getSelectedItem().toString());
                }
            });

            box1 = new JComboBox();
            box1.addItem("8");
            box1.addItem("10");
            box1.addItem("11");
            box1.addItem("12");
            box1.addItem("14");
            box1.addItem("18");
            box1.addItem("20");
            
            box1.setSelectedItem("12");
            
            box1.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                     setDefaultSize(Integer.parseInt(box1.getSelectedItem().toString()));
                }
            });

            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createTitledBorder("Preview"));
            previewLabel = new JLabel("The quick brown fox jumps over the lazy dog 0123456890");
            previewLabel.setAlignmentX(JLabel.CENTER);
            panel.add(previewLabel);

            okButton = new JButton("Ok");
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    changesMade = false;
                    fontChooserFrame.dispose();
                }
            });

            GroupLayout jPanel1Layout = new GroupLayout(mainPanel);
            mainPanel.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(pane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(fontLabel))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(box, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(styleLabel))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 5, 5)
                                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(box1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(sizeLabel)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(105, 175, 175)
                                            .addComponent(okButton)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(cancelButton)
                                            .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(panel, 100, 530, 550)))
                            .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(fontLabel)
                                    .addComponent(styleLabel)
                                    .addComponent(sizeLabel))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(box, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(box1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addComponent(pane, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(panel, GroupLayout.PREFERRED_SIZE, 90, 90)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                    .addComponent(okButton)
                                    .addComponent(cancelButton))
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            fontChooserFrame.add(mainPanel);

            fontChooserFrame.setVisible(true);
        }

        public void setDefaultName(String name) {
            fontName = name;
            updateFont();
        }

        public void setDefaultStyle(int style) {
            fontStyle = style;
            updateFont();
       
        }

        public void setDefaultStyle(String style) {
            //box.setSelectedItem(style);
            if (style.equals("Plain")) {
                fontStyle = Font.PLAIN;
            } else if (style.equals("Italic")) {
                fontStyle = Font.ITALIC;
            } else if (style.equals("Bold")) {
                fontStyle = Font.BOLD;
            } else {
                fontStyle = Font.BOLD + Font.ITALIC;
            }
            updateFont();
        }

        public void setDefaultSize(int size) {
            fontSize = size;
            updateFont();
        }

        public boolean showSelectDialog(JFrame frame) {  
            return changesMade;
        }

        public String getSelectedFontName() {
            return fontName;
        }

        public int getSelectedFontStyle() {
            return fontStyle;
        }

        public int getSelectedFontSize() {
            return fontSize;
        }

        private void updateFont() {
            list.setSelectedValue(fontName, true);
            box.setSelectedItem(fontStyle);
            box1.setSelectedItem(fontSize);
            previewLabel.setFont(new Font (fontName,fontStyle,fontSize));
        }

    }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
               new JNotepad();
            }
        });
    }
    
}
