package tcrypt.crypto;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.security.MessageDigest;

import tcrypt.util.Log;

public class VerificationEngine{



private static String os = null; // cache


public boolean verify(String filePath){ // returns isValid
    try {
        if (os == null){
            os = System.getProperty("os.name");
        }
        if (os != "Linux"){
            Log.log("Os is not Linux, hash verification skipped", Level.WARNING);
        } else {

            String line;
            ArrayList<String> shafile = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader("/opt/tcrypt/hashes.txt"));

            while ((line = reader.readLine()) != null){
                shafile.add(line);
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            File file = new File(filePath);

            digest.update(file.toString().getBytes());
            byte[] shaSum = digest.digest();

            String shaString = shaSum.toString();
            
            if(shafile.contains(shaString)){
                return true;
                Log.log("Integrity check successfull", Level.INFO);
            } else {
                return false;
                Log.log("Integrity check unsuccessfull", Level.WARNING);
            }






        }
    } catch (Exception e) {
    }

}




}