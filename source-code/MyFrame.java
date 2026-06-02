
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

public class MyFrame extends JFrame implements ActionListener {
    
    JButton exitButton;
    JButton fileChooserButton;
    JButton fileChooserButton2;
    JButton submitButton;

    JRadioButton encryptRadioButton;
    JRadioButton decryptRadioButton;
    Font font = new Font("Arial", Font.PLAIN, 20);


    boolean encryptionMode;
    boolean doExitForRunnable = false;
    File keyFile;
    File file;

    MyFrame(boolean encryptionMode){
       this.encryptionMode = encryptionMode;



        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Tcrypt");
        this.setVisible(true);
        this.setSize(1000, 400);
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
        encryptRadioButton.setFont(font);




        decryptRadioButton = new JRadioButton("Decryption");
        decryptRadioButton.setBounds(0,50,200,20);
        decryptRadioButton.addActionListener(this);
        decryptRadioButton.setFont(font);


        ButtonGroup deEnCryptGroup = new ButtonGroup();
        deEnCryptGroup.add(encryptRadioButton);
        deEnCryptGroup.add(decryptRadioButton);
    //

    // File Chooser -- Encryption
        fileChooserButton = new JButton("Choose a file");
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


    // add-stack
    this.add(exitButton);

    this.add(fileChooserButton);


    this.add(submitButton);




    this.add(encryptRadioButton);
    this.add(decryptRadioButton);

    }











    @Override
    public void actionPerformed(ActionEvent e){

        if (e.getSource() == exitButton){
            doExitForRunnable = true;
            dispose();
            
        }

        if (e.getSource() == decryptRadioButton){
            encryptionMode = false;
            showFileChooserButton();
        }

        if (e.getSource() == encryptRadioButton){
            encryptionMode = true;
            hideFileChooserButton();
        }

        if (e.getSource() == fileChooserButton){
            
            JFileChooser fileChooser = new JFileChooser();
            int fileChooserExitCode = fileChooser.showOpenDialog(null); // null bcs no parent // batman

            if (fileChooserExitCode == JFileChooser.APPROVE_OPTION){ // same as the integer '0' but more clear
                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }

        if (e.getSource() == fileChooserButton2){
            
            JFileChooser fileChooser = new JFileChooser();
            int fileChooserExitCode = fileChooser.showOpenDialog(null); 

            if (fileChooserExitCode == JFileChooser.APPROVE_OPTION){
                keyFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }

        if (e.getSource() == submitButton){
            try{
                if (encryptionMode){
                    //String path = file.getPath();
                    //Path basedir = Paths.get(System.getProperty("user.dir"));
                    tcrypt.encryptFile(file.getAbsolutePath()/*.toString()*/);
                    IO.println(file.getAbsolutePath());
                    JOptionPane.showMessageDialog(null, "File Encrypted");

                } else {
                    //String path1 = file.getPath();
                   // String keyPath = keyFile.getPath();
                    //Path basedir = Paths.get(System.getProperty("user.dir"));
                    tcrypt.decryptFile(file.getAbsolutePath()/*.toString()*/, keyFile.getAbsolutePath()/*.toString()*/);
                    IO.println(file.getAbsolutePath() + " " + keyFile.getAbsolutePath());
                    JOptionPane.showMessageDialog(null, "File Decrypted");
                }
            }
            catch (Exception f){
            IO.println(f);
            JOptionPane.showMessageDialog(null, "Error at File Operation " + f);
            
        } 
        }

    }

    public void showFileChooserButton(){
        this.add(fileChooserButton2);
        revalidate();
        repaint();
    }

    public void hideFileChooserButton(){
        this.remove(fileChooserButton2);
        revalidate();
        repaint();
    }
    
}