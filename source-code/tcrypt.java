import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.security.SecureRandom;
import javax.swing.JOptionPane;


public class tcrypt{
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

static void encryptFile(String filepath){
    try{
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
    } catch (Exception e){}
}

static void decryptFile(String filePath, String keyPath){
    try {
        FileInputStream fileInput = new FileInputStream(filePath);
        byte[] fileBytes = fileInput.readAllBytes();

//

    FileInputStream keyInput = new FileInputStream(keyPath);
    byte[] keyBytes = keyInput.readAllBytes();
    keyInput.close();    

//

    byte[] decrypted = new byte[fileBytes.length];

    for (int i = 0; i < fileBytes.length; i++){
        decrypted[i] = (byte)(fileBytes[i] ^ keyBytes[i]);
    }
//
    FileOutputStream resOut = new FileOutputStream(filePath.replace(".tcrt", ".tmsg"));
        resOut.write(decrypted);
        resOut.close();
        IO.println("Decryption complete!");
    } catch (Exception e){JOptionPane.showMessageDialog(null, "Error at writing file to disk");}


}

}