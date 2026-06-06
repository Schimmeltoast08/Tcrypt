package tcrypt.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import tcrypt.gui.MyFrame;
import tcrypt.util.Log;

public class Tcrypt{
     private static final byte[] MAGIC = "TCRYPT".getBytes(StandardCharsets.UTF_8); // so only TCRYPT files may be decrypted, not random garbage files //metadata
     public static int TcryptFormatVersion = 2;
     public static final int TcryptVersion = 2;
     public static int percent;
    public static void main(String[] args) {

        if (args.length > 0 && args[0].equals("--gui")){
            javax.swing.SwingUtilities.invokeLater(() -> new MyFrame());
            return;
        }

        if (args.length > 1 && args[0].equals("--encrypt")){
            encryptFile(args[1], null);
            return;
        }

        if (args.length > 2 && args[0].equals("--decrypt")){
            decryptFile(args[1], args[2]);
            return;
        }

        if (args.length > 2 && args[0].equals("--verify")){
            verifyFile(args[1], args[2]);
            return;
        }

        if (args.length > 0 && args[0].equals("--version")){
            IO.println("Tcrypt version: " + TcryptVersion);
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
                IO.println("D     Decrypt\nE     Encrypt\nG     Gui\nV     Verify\nQ     Exit");
            }

            if (prompt.toLowerCase().startsWith("g")){
                MyFrame myFrame = new MyFrame();

            }

            if (prompt.startsWith("v")){
                    String filepath = prompt.split(" ")[1];
                    String keyPath = prompt.split(" ")[2];
                    verifyFile(filepath, keyPath);
            }

            if (prompt.toLowerCase().startsWith("version")){
                IO.println("Tcrypt verion: " + TcryptVersion);
            }

            if (prompt.toLowerCase().startsWith("v")){

            }

            
            if (doTry){
                if (prompt.toLowerCase().startsWith("e")){
                    encryptFile((prompt.substring(prompt.indexOf(" ") + 1)), null);
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

public static void encryptFile(String filepath, MyFrame myFrame){
    try{
    Log.log("Encrypting file " + filepath, Level.INFO);
    long start = System.nanoTime();
    String originalHash = hashFile(filepath);
    long progess = 0;
    long fileLength = new File(filepath).length();


    FileInputStream input = new FileInputStream(filepath);
    FileOutputStream fileOut = new FileOutputStream(filepath + ".tcrt");
    FileOutputStream keyOut = new FileOutputStream(filepath + ".tkey");

    String fileName = new File(filepath).getName();
    fileOut.write(MAGIC); // HEADER
    fileOut.write(fileName.getBytes(StandardCharsets.UTF_8).length); // 256 max
    fileOut.write(fileName.getBytes(StandardCharsets.UTF_8));
    fileOut.write((byte) TcryptFormatVersion); // Tcrypt File Version Number
    File f = new File(filepath);
    Long filesize = f.length(); // capital L to avoid deref error
    Log.log("Filesize: " + filesize.toString(), Level.INFO);

    JPasswordField passwordField = new JPasswordField();

    Object[] message = {
        "Password:",
        passwordField
    };
    String password = new String();
    long intermediateTime1 = System.nanoTime();
    int result = JOptionPane.showConfirmDialog(
            null,
            message,
            "Enter Password",
            JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
        password = new String(passwordField.getPassword());
    }
    long intermediateTime2 = System.nanoTime();

//
    final String ALGORITHM = "SHA-256";
    byte[] passwordHash = MessageDigest.getInstance(ALGORITHM).digest(password.getBytes(StandardCharsets.UTF_8));
    SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
    rng.setSeed(passwordHash);

    SecureRandom random = new SecureRandom(); 

    final int BUFFER_SIZE = 8192;

    byte[] fileBuffer = new byte[BUFFER_SIZE];
    byte[] keyBuffer = new byte[BUFFER_SIZE];
    byte[] encryptedBuffer = new byte[BUFFER_SIZE];
    byte[] maskBuffer = new byte[BUFFER_SIZE];

    int bytesRead;

    while ((bytesRead = input.read(fileBuffer)) != -1) {
        random.nextBytes(keyBuffer);
        rng.nextBytes(maskBuffer);
        for (int i = 0; i < bytesRead; i++) {

            encryptedBuffer[i] = (byte)(fileBuffer[i] ^ keyBuffer[i]);
            keyBuffer[i] = (byte)(keyBuffer[i] ^ maskBuffer[i]);
            }
        progess += bytesRead;
        percent = (int) ((int) (progess * 100) / fileLength); // idk why, but double casting works
        if (myFrame != null) {
           myFrame.setProgressBar(percent);
        }
        try {
        } catch (Exception e) {Log.log("Error at setting percent bar", Level.WARNING);}
        //IO.print(percent  + " "); works
        fileOut.write(encryptedBuffer, 0, bytesRead);

        keyOut.write(keyBuffer, 0, bytesRead);
    }

    fileOut.write(originalHash.getBytes(StandardCharsets.UTF_8)); //////////////////////////////////

    //keyOut.write(originalHash.getBytes(StandardCharsets.UTF_8));
    input.close();
    fileOut.close();
    keyOut.close();


    IO.println("Encryption complete!");
    Log.log("Encryption OK!", Level.INFO);

    long end = System.nanoTime();
    Log.log("Time to Encrypt: " + (((end - start) - (intermediateTime2 - intermediateTime1)) / 1_000_000_000.0) + "s", Level.INFO);
    } catch (IOException e){
        JOptionPane.showMessageDialog(null, "Error at writing file to disk");
        Log.log("Encryption failed!", Level.SEVERE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "General Encryption Error");
        Log.log("Encryption failed!: " + e, Level.SEVERE);
    }
}

public static void decryptFile(String filePath, String keyPath){
    try {
        Log.log("Decrypting file " + filePath + " with key " + keyPath, Level.INFO);
        long start = System.nanoTime();
        FileInputStream fileInput = new FileInputStream(filePath);


        byte[] header = new byte[MAGIC.length];
        fileInput.read(header);

        int fileNameLength = fileInput.read();
        byte[] fileName = new byte[fileNameLength];
        fileInput.read(fileName);
        String originalFileName = new String(fileName);
        TcryptFormatVersion = fileInput.read();

        if (TcryptFormatVersion > TcryptVersion) {
        JOptionPane.showMessageDialog(null,
        """
        This file was created by a newer version of Tcrypt.

        File format: %d
        Supported: %d
        """
        .formatted(TcryptFormatVersion, TcryptVersion));
        fileInput.close();
        return;
        }

        if (!Arrays.equals(header,MAGIC)){
            Log.log("File is not a Tcrypt file", Level.WARNING);
            IO.println("File was not encrypted with Tcrypt");
            JOptionPane.showMessageDialog(null, "File was not encrypted with Tcrypt!");
        }

        File file = new File(filePath);
        long fileLength = file.length();

        long payloadLength = file.length() - MAGIC.length - 2 - 64 - fileNameLength; // 2 bcs fileNameByte and Version Byte

        byte[] fileBytes = new byte[(int) payloadLength];
        fileInput.readNBytes(fileBytes, 0, fileBytes.length);

        byte[] shaSum = new byte[64];
        fileInput.readNBytes(shaSum, 0, 64);

        String storedHash = new String(shaSum, StandardCharsets.UTF_8);




        //byte[] fileBytes = null;
        //byte[] shaSum = new byte[64];
        //fileInput.read(shaSum, ((int) fileLength - 64), (int) fileLength);
        //fileInput.read(fileBytes, (MAGIC.length + fileNameLength + 2 ), (int) (fileLength - 64));
        fileInput.close();



        FileInputStream keyInput = new FileInputStream(keyPath);
        //byte[] keyFileBytes = keyInput.readAllBytes();
        //keyInput.close();


        final int hashLength = 64; //sha256 is always 64 long
        //String storedHash = new String(keyFileBytes, keyFileBytes.length - hashLength, hashLength);
        //byte[] keyBytes = Arrays.copyOfRange(keyFileBytes, 0, keyFileBytes.length - hashLength); // seperate key from hash    
        byte[] keyBytes = keyInput.readAllBytes();

        final String ALGORITHM = "SHA-256";

        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
            "Password:",
            passwordField
        };
        String password = new String();
        long intermediateTime1 = System.nanoTime();
        int result = JOptionPane.showConfirmDialog(
            null,
            message,
            "Enter Password",
            JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            password = new String(passwordField.getPassword());
        }
        long intermediateTime2 = System.nanoTime();

        byte[] passwordHash = MessageDigest.getInstance(ALGORITHM).digest(password.getBytes(StandardCharsets.UTF_8));
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
        rng.setSeed(passwordHash);
        byte[] mask = new byte[keyBytes.length];
        rng.nextBytes(mask);

        for (int i = 0; i < keyBytes.length; i++){
            keyBytes[i] ^= mask[i];
        }




        if (keyBytes.length != fileBytes.length){
            JOptionPane.showMessageDialog(null,
            """
            The key file appears corrupted.

            Expected key length: %d
            Actual key length: %d
            """
            .formatted(fileBytes.length, keyBytes.length));

            Log.log("Corrupted key file",Level.WARNING);
            return;
        
        }

        byte[] decrypted = new byte[fileBytes.length];

        for (int i = 0; i < fileBytes.length; i++){
            decrypted[i] = (byte)(fileBytes[i] ^ keyBytes[i]);
        }

        String[] decryptedPathElements = filePath.split(File.separator);
        String decryptedPath = filePath.replace(decryptedPathElements[decryptedPathElements.length - 1], originalFileName);

        
        File outputFile = getAvailableFile(decryptedPath);
        FileOutputStream resOut = new FileOutputStream(outputFile);
        resOut.write(decrypted);
        resOut.close();
        
        String currentHash = hashFile(decryptedPath);

       if (!storedHash.equals(currentHash)){
        IO.println("WARNING: File did not pass Integrity check! Most likely: Wrong password");

       }

        IO.println("Decryption complete!");
        long end = System.nanoTime();
        Log.log("Decryption OK!", Level.INFO);
        Log.log("Decrypted Format Version: " + TcryptFormatVersion + " Tcrypt Version: " + TcryptVersion, Level.INFO);
        Log.log("Time to Decrypt: " + (((end - start) - (intermediateTime2 - intermediateTime1)) / 1_000_000_000.0) + "s", Level.INFO); // Intermediate time --> so slow user
    } catch (IOException e){                                                                                                            // is not recorded too
        JOptionPane.showMessageDialog(null, "Error at writing file to disk");
        Log.log("Decryption failed!", Level.SEVERE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "General Error at Decryption");
        Log.log("Decryption failed!: " + e, Level.SEVERE);
    }
}

    private static String hashFile(String filePath) throws Exception {

        final String ALGORITHM = "SHA-256";
        final int BUFFER_SIZE = 8192;
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);

        File file = new File(filePath);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;

            while ((read = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        StringBuilder sb = new StringBuilder();

        byte[] bytes = digest.digest();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

private static File getAvailableFile(String filePath) {

    File file = new File(filePath);

    if (!file.exists()) {
        return file;
    }

    String name = file.getName();
    String parent = file.getParent();

    int dot = name.lastIndexOf('.');

    String baseName;
    String extension;

    if (dot == -1) {
        baseName = name;
        extension = "";
    } else {
        baseName = name.substring(0, dot);
        extension = name.substring(dot);
    }

    int counter = 1;

    while (true) {

        String newName = baseName + " (" + counter + ")" + extension;

        File candidate =
                parent == null ? new File(newName) : new File(parent, newName);

        if (!candidate.exists()) {
            return candidate;
        }

        counter++;
    }
}

public static void verifyFile(String filepath, String encryptedFilePath) {
    try {
        Log.log("Verifying file integrity: " + filepath + " Against: " + encryptedFilePath, Level.INFO);

        byte[] encryptedBytes;

        try (FileInputStream in = new FileInputStream(encryptedFilePath)) {
            encryptedBytes = in.readAllBytes();
        }

        if (encryptedBytes.length < 64) {
            JOptionPane.showMessageDialog(null, "Encrypted file is invalid.");
            Log.log("Invalid encrypted file", Level.WARNING);
            return;
        }

        String storedHash = new String(Arrays.copyOfRange(encryptedBytes, encryptedBytes.length - 64, encryptedBytes.length),
            StandardCharsets.UTF_8);

        String calculatedHash = hashFile(filepath);

        Log.log("Stored hash: " + storedHash, Level.INFO);
        Log.log("Calculated hash: " + calculatedHash, Level.INFO);

        if (calculatedHash.equals(storedHash)) {
            JOptionPane.showMessageDialog(null, "Verification passed!\nOriginal file matches encrypted file");
            Log.log("Verification passed", Level.INFO);
        } else {
            JOptionPane.showMessageDialog(null, "Verification failed!");
            Log.log("Verification failed", Level.WARNING);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error during verification");
        Log.log("Verification error: " + e, Level.SEVERE);
    }
}


}