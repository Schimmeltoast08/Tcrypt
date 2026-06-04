package tcrypt.crypto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import tcrypt.gui.MyFrame;
import tcrypt.util.Log;

public class Tcrypt{
    static String key = "";
    public static void main(String[] args) {

        if (args.length > 0 && args[0].equals("--gui")){
            javax.swing.SwingUtilities.invokeLater(() -> new MyFrame(true));
            return;
        }

        if (args.length > 1 && args[0].equals("--encrypt")){
            encryptFile(args[1]);
            return;
        }

        if (args.length > 2 && args[0].equals("--decrypt")){
            decryptFile(args[1], args[2]);
            return;
        }
        

        if (args.length > 0 && args[0].equals("--help")){
             IO.println("""
                    flags:
                    --encrypt [file]          | encrypts the file\n
                    --decrypt [.tcrt] [.tkey] | decrypts the file\n
                    --gui                     | opens the graphical environment
                    """);
            return;
        }


        boolean doExit = false;
        boolean doTry = true;
        Scanner scanner = new Scanner(System.in);
        //repl
        while (!doExit){
            IO.print("> ");
            String prompt = scanner.nextLine();
            if (prompt.toLowerCase().startsWith("exit") || prompt.toLowerCase().startsWith("q")){
                Log.log("Exiting application", Level.INFO);
                doExit = true;
                doTry = false;
            }
            if (prompt.toLowerCase().startsWith("help")){
                IO.println("D     Decrypt\nE     Encrypt\nG     Gui\nQ     Exit");
            }

            if (prompt.toLowerCase().startsWith("g")){
                MyFrame myFrame = new MyFrame(true);

            }

            
            if (doTry){
                if (prompt.toLowerCase().startsWith("e")){
                    encryptFile((prompt.substring(prompt.indexOf(" ") + 1)));
                }

                if (prompt.toLowerCase().startsWith("d")){
                    try{
                    String filepath = prompt.split(" ")[1];
                    String keyPath = prompt.split(" ")[2];
                    decryptFile(filepath, keyPath);
                    } catch (ArrayIndexOutOfBoundsException e){
                        JOptionPane.showMessageDialog(null, "Please enter 2 components:\nthe file to be decrypted and the key");
                    }

                } 


            }
        }

    }
/////////////////////////////////

public static void encryptFile(String filepath){
    try{
    Log.log("Encrypting file " + filepath, Level.INFO);
    FileInputStream input = new FileInputStream(filepath);
    byte[] fileBytes = input.readAllBytes();
    input.close();
//

    byte[] key = new byte[fileBytes.length];
    SecureRandom random = new SecureRandom(); 
    random.nextBytes(key);  // populate key
// 
    byte[] encrypted = new byte[fileBytes.length];

    for (int i = 0; i < fileBytes.length; i++){
        encrypted[i] = (byte) (fileBytes[i] ^ key[i]);
    }
//
    FileOutputStream fileOut = new FileOutputStream(filepath + ".tcrt");
    fileOut.write(encrypted);
    fileOut.close();
//
    FileOutputStream keyOut = new FileOutputStream(filepath + ".tkey");
    keyOut.write(key);
    keyOut.close();
    IO.println("Encryption complete!");
    Log.log("Encryption OK!", Level.INFO);

    VerificationEngine.saveHash(filepath);

    } catch (IOException e){
        JOptionPane.showMessageDialog(null, "Error at writing file to disk");
        Log.log("Encryption failed!", Level.SEVERE);
    }
}

public static void decryptFile(String filePath, String keyPath){
    try {
        Log.log("Decrypting file " + filePath + " with key " + keyPath, Level.INFO);
        FileInputStream fileInput = new FileInputStream(filePath);
        byte[] fileBytes = fileInput.readAllBytes();
        fileInput.close(); // Added to safely close the stream

        FileInputStream keyInput = new FileInputStream(keyPath);
        byte[] keyBytes = keyInput.readAllBytes();
        keyInput.close();    

        if (keyBytes.length != fileBytes.length){
            Log.log("Key length does not match file length: Key " + keyBytes.length + " | File " + fileBytes.length, Level.WARNING);
        }

        byte[] decrypted = new byte[fileBytes.length];

        for (int i = 0; i < fileBytes.length; i++){
            decrypted[i] = (byte)(fileBytes[i] ^ keyBytes[i]);
        }


        String decryptedPath = filePath.replace(".tcrt", "");

        FileOutputStream resOut = new FileOutputStream(decryptedPath);
        resOut.write(decrypted);
        resOut.close();

        if (!VerificationEngine.verify(decryptedPath)) {        
            IO.println("unverified file! Continue with caution");
        }
        

        IO.println("Decryption complete!");
        Log.log("Decryption OK!", Level.INFO);
    } catch (IOException e){
        JOptionPane.showMessageDialog(null, "Error at writing file to disk");
        Log.log("Decryption failed!", Level.SEVERE);
    }
}

}