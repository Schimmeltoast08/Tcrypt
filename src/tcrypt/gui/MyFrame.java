package tcrypt.gui;

import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import tcrypt.crypto.Tcrypt;
import tcrypt.util.Log;


public class MyFrame extends JFrame implements ActionListener {
    
    JButton exitButton;
    JButton fileChooserButton;
    JButton fileChooserButton2;
    JButton submitButton;

    JRadioButton encryptRadioButton;
    JRadioButton decryptRadioButton;
    JRadioButton verifyRadioButton;
    JRadioButton inspectRadioButton;

    JLabel fileSelectedLabel;
    JLabel keySelectedLabel;
    JLabel filePathLabel;
    JLabel keyPathLabel;

    JProgressBar progressBar;


    Font font = new Font("Arial", Font.PLAIN, 20);


    boolean encryptionMode;
    File keyFile;
    File file;
    static int barValue = 0;

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 400;   
    public static Mode mode = Mode.ENCRYPT;

    public MyFrame(){
        Log.log("New GUI Frame created", Level.INFO);



        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Tcrypt");
        this.setVisible(true);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);
        this.setLayout(null); // for absolute positioning
        //this.getContentPane().setBackground(new Color(47,47,47));


    //Exitbutton
        exitButton = new JButton("Exit");
        exitButton.setBounds(900,320,100,50);
        exitButton.addActionListener(this);
    //

    // De or Encrypt
        encryptRadioButton = new JRadioButton("Encryption");
        encryptRadioButton.setBounds(0,10,200,20);
        encryptRadioButton.addActionListener(this);
        encryptRadioButton.setSelected(true);
        encryptRadioButton.setFont(font);
        



        decryptRadioButton = new JRadioButton("Decryption");
        decryptRadioButton.setBounds(0,50,200,20);
        decryptRadioButton.addActionListener(this);
        decryptRadioButton.setFont(font);

        verifyRadioButton = new JRadioButton("Verification");
        verifyRadioButton.setBounds(230,10,200,20);
        verifyRadioButton.addActionListener(this);
        verifyRadioButton.setFont(font);

        inspectRadioButton = new JRadioButton("Inspection");
        inspectRadioButton.setBounds(230,50,200,20);
        inspectRadioButton.addActionListener(this);
        inspectRadioButton.setFont(font);



        ButtonGroup deEnCryptGroup = new ButtonGroup();
        deEnCryptGroup.add(encryptRadioButton);
        deEnCryptGroup.add(decryptRadioButton);
        deEnCryptGroup.add(verifyRadioButton);
        deEnCryptGroup.add(inspectRadioButton);
    //

    // File Chooser -- Encryption
        fileChooserButton = new JButton("Choose a File");
        fileChooserButton.setBounds(0,90,200,50);
        fileChooserButton.addActionListener(this);
        fileChooserButton.setFont(font);

    //

    // File Chooser 2 -- Decryption
        fileChooserButton2 = new JButton("Choose a Key");
        fileChooserButton2.setBounds(0,150,200,50);
        fileChooserButton2.addActionListener(this);
        fileChooserButton2.setFont(font);

    //



    // Submit Button
    submitButton = new JButton("Submit");
    submitButton.setBounds(0,320,200,50); 
    submitButton.addActionListener(this);
    submitButton.setFont(font);

    //

    // File Selected Label
    fileSelectedLabel = new JLabel("<html><font color='green'>File selected:</font></html>");
    fileSelectedLabel.setBounds(230,90,200,50);
    fileSelectedLabel.setFont(font);

    //

    // Key Selected Label
    keySelectedLabel = new JLabel("<html><font color='green'>Key selected:</font></html>");
    keySelectedLabel.setBounds(230,150,200,50);
    keySelectedLabel.setFont(font);

    //

    // FilePath Label
    filePathLabel = new JLabel();
    filePathLabel.setBounds(400,90,350,50);
    filePathLabel.setFont(font);

    //

    // KeyPath Label
    keyPathLabel = new JLabel();
    keyPathLabel.setBounds(400,150,350,50);
    keyPathLabel.setFont(font);

    //

    //
    progressBar = new JProgressBar();
    progressBar.setBounds(230, 320, 640, 50);
    progressBar.setFont(font);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);



    // add-stack
    this.add(exitButton);

    this.add(fileChooserButton);

    this.add(submitButton);

    this.add(encryptRadioButton);
    this.add(decryptRadioButton);
    this.add(verifyRadioButton);
    this.add(inspectRadioButton);

    this.add(progressBar);

        revalidate();
        repaint();
    }



    @Override
    public void actionPerformed(ActionEvent e){

        if (e.getSource() == exitButton){
            Log.log("Exiting application", Level.INFO);
            dispose();
            
        }

        if (e.getSource() == decryptRadioButton){
            mode = Mode.DECRYPT;
            showElement(fileChooserButton2);
            fileChooserButton.setText("Choose a File");
            fileChooserButton2.setText("Choose a Key");
        }

        if (e.getSource() == encryptRadioButton){
            mode = Mode.ENCRYPT;
            fileChooserButton.setText("Choose a File");
            hideElement(fileChooserButton2);

                hideElement(keySelectedLabel);
                hideElement(keyPathLabel);
        }

        if (e.getSource() == verifyRadioButton){
            mode = Mode.VERIFY;
            showElement(fileChooserButton2);
            fileChooserButton.setText("Original File");
            fileChooserButton2.setText("Encrypted File");

                hideElement(keySelectedLabel);
                hideElement(keyPathLabel);
        }

        if (e.getSource() == inspectRadioButton){
            mode = Mode.INSPECT;
            hideElement(fileChooserButton2);
            fileChooserButton.setText("Choose a File");

                hideElement(keySelectedLabel);
                hideElement(keyPathLabel);
        }

        if (e.getSource() == fileChooserButton){
            
            JFileChooser fileChooser = new JFileChooser();
            int fileChooserExitCode = fileChooser.showOpenDialog(null); // null bcs no parent // batman

            if (fileChooserExitCode == JFileChooser.APPROVE_OPTION){ // same as the integer '0' but more clear
                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                showElement(fileSelectedLabel);
                setElementText(filePathLabel, file.getName());
                showElement(filePathLabel);
            }
        }

        if (e.getSource() == fileChooserButton2){
            
            JFileChooser fileChooser = new JFileChooser();
            int fileChooserExitCode = fileChooser.showOpenDialog(null); 

            if (fileChooserExitCode == JFileChooser.APPROVE_OPTION){
                keyFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                showElement(keySelectedLabel);
                setElementText(keyPathLabel, keyFile.getName());
                showElement(keyPathLabel);
                
            }
        }

        if (e.getSource() == submitButton){
            try{
                if (file == null){
                    JOptionPane.showMessageDialog(null, "Please select a file");
                } else {                       
                        switch (mode) {
                        case ENCRYPT -> {
                            startThread(file.getAbsolutePath(), this);
                            JOptionPane.showMessageDialog(null, "File Encrypted");
                            progressBar.setValue(0);
                        }

                        case DECRYPT -> {
                            if (keyFile == null){ JOptionPane.showMessageDialog(null, "Please select a key"); return;}
                            
                            //Tcrypt.decryptFile(file.getAbsolutePath(), keyFile.getAbsolutePath());
                            startThread(file.getAbsolutePath(), this, keyFile.getAbsolutePath());
                            JOptionPane.showMessageDialog(null, "File Decrypted");
                            }

                        case VERIFY -> Tcrypt.verifyFile(file.getAbsolutePath(), keyFile.getAbsolutePath()); // in this instance, keyfile is the .tcrt file!!! It's chuffed, but it works
                        
                        case INSPECT -> {
                            String[] info = Tcrypt.inspectFileHeader(file.getAbsolutePath());
                            JOptionPane.showMessageDialog(null, "File info:\nHeader: " + info[0]
                                    + "\nOriginal File name: " + info[1]
                                    + "\nEncrypted in Tcrypt Format Version: " + info[2]
                                    + "\nStored Hash: " + info[3]
                                    + "\nEncrypted Filesize: " + info[4] + "kb");
                                

                                    IO.println("Stored Hash: " + info[5]);
                        }
                    
                        default -> {
                            Log.log("No Mode option Selected", Level.SEVERE);
                            JOptionPane.showMessageDialog(null, "No option selected\nSelect en/decrypt or verify");
                        }
                    }
                    }  
                    
            }
            catch (HeadlessException f){
            IO.println(f);
            JOptionPane.showMessageDialog(null, "Error at File Operation " + f);
            Log.log("Error at file Operation: " + f, Level.SEVERE);
            
        } 
        }

    }


private void showElement(JComponent element) {
    
    if (this.getContentPane() != element.getParent()){
        this.add(element);
    }
    
    
    revalidate();
    repaint();
}

private void hideElement(JComponent element) {
    this.remove(element);   
    revalidate();
    repaint();
}

private void setElementText(JLabel label, String text){
    label.setText(text);
    revalidate();
    repaint();
}

private static void startThread(String filePath, MyFrame myFrame){
    SwingWorker swingWorker = new SwingWorker(){
        
        @Override
        protected String doInBackground() throws Exception{
                Tcrypt.encryptFile(filePath, myFrame);
            return "1"; // stupid, should be void
        }
        protected void process(){
         myFrame.progressBar.setValue(barValue);   
        }
    };
    swingWorker.execute();
}

private static void startThread(String filePath, MyFrame myFrame, String keyPath){
    SwingWorker swingWorker = new SwingWorker(){
        
        @Override
        protected String doInBackground() throws Exception{
                Tcrypt.decryptFile(filePath, keyPath, myFrame);
            return "1";
        }
        protected void process(){
         myFrame.progressBar.setValue(barValue);   
        }
    };
    swingWorker.execute();
}


public void setProgressBar(int value) {
    SwingUtilities.invokeLater(() -> {
        progressBar.setValue(value);
    });
}

enum Mode{
    ENCRYPT,
    DECRYPT,
    VERIFY,
    INSPECT
}

    
}