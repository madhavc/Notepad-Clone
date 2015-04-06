//
// Name: Chhura, Madhav
// Homework: #3
// Due: 02-17-15
// Course: cs-245-01-w15
//
// Description:
// JFontChooser class.
//
package jnotepad;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 *
 * @author madhavchhura
 */
public class JFontChooser {
    
    String fontName;
    int fontStyle, fontSize;
    JPanel mainPanel;
    JFrame frame;
  
    DefaultListModel model;
    JList list;
    JComboBox box, box1;
    JScrollPane pane;
    final JLabel fontLabel, styleLabel, sizeLabel;
    JLabel previewLabel;
    Font font;
    
    JFontChooser(){
//        fontName = "Courier";
//        fontStyle = 1;
//        fontSize = 14;
//        font = new Font(fontName,fontStyle,fontSize);  

        frame = new JFrame("Font");
        frame.setSize(550, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        fontLabel = new JLabel("Font:");
        styleLabel = new JLabel("Font Style:");
        sizeLabel = new JLabel("Size");


        JPanel mainPanel = new JPanel();
        
        model = new DefaultListModel();
        list = new JList(model);
        pane = new JScrollPane(list);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        model.addElement("Arial");
        model.addElement("Arial Black");
        model.addElement("Arial Narrow");
        model.addElement("Arial Rounded MT Bold");
        model.addElement("Arial Unicode MS");
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
                setDefaultStyle((box.getSelectedItem().toString()));
            }
        });
        
        
        

        box1 = new JComboBox();
        box1.addItem("8");
        box1.addItem("10");
        box1.addItem("12");
        box1.addItem("14");
        box1.addItem("18");
        box1.addItem("20");
        
        box1.setSelectedItem(12);
        
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
        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");
    
        GroupLayout jPanel1Layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(pane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                            .addComponent(fontLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(box, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(styleLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
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
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
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

        frame.add(mainPanel);
        
        frame.setVisible(true);
    }
    public void setDefaultName(String name){
        fontName = name;
        updateFont();
    }
    
    public void setDefaultStyle(int style) {
        fontStyle = style;
        updateFont();
    }
    public void setDefaultStyle(String style) {
        if(style.equals("Plain"))
            fontStyle = Font.PLAIN;
        else if(style.equals("Italic"))
            fontStyle = Font.ITALIC;
        else if(style.equals("Bold"))
            fontStyle = Font.BOLD;
        else
            fontStyle = Font.BOLD + Font.ITALIC;
        
        updateFont();
    }
    
    public void setDefaultSize(int size){
        fontSize = size;
        updateFont();
    }
    
    public boolean showSelectDialog(JFrame frame){
        frame.setVisible(true);
        return true;
    }
    
    public String getSelectedFontName(){
        return fontName;
    }
    
    public int getSelectedFontStyle(){
        return fontStyle;
    }
    
    public int getSelectedFontSize(){
        return fontSize;
    }

    private void updateFont() {
        previewLabel.setFont(new Font(fontName,fontStyle,fontSize));
    }

    
    
}
