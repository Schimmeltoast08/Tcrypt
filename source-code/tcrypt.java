import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class tcrypt{
    static String key = "";
    public static void main(String[] args) {
        boolean doExit = false;
        Scanner scanner = new Scanner(System.in);
        //repl
        while (!doExit){
            IO.print("> ");
            String prompt = scanner.nextLine();
            if (prompt.toLowerCase().startsWith("exit") || prompt.toLowerCase().startsWith("q")){
                doExit = true;
            }
            if (prompt.toLowerCase().startsWith("help")){
                IO.println("D     Decrypt\nE     Encrypt\nG     Gui\nQ     Exit");
            }

            if (prompt.toLowerCase().startsWith("g")){
                MyFrame myFrame = new MyFrame(true);

            }

            

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
/////////////////////////////////

static void encryptFile(String filepath){
    try(BufferedReader sourceFileReader = new BufferedReader(new FileReader(filepath))){
        //
        String sourceFileLine;
        ArrayList<String> sourceFile = new ArrayList<>();
        while ((sourceFileLine = sourceFileReader.readLine()) != null){
            sourceFile.add(sourceFileLine);
            sourceFile.add("\n");
        }
        //
        String productBinaryString = "";
        for (String s : sourceFile){
            byte[] b = s.getBytes();
            for (byte b2 : b){
                String binaryString = String.format("%8s", Integer.toBinaryString(b2 & 0xFF)).replace(' ', '0');
                productBinaryString += binaryString;
            }
        }
        //
        Random random = new Random();
        for (int i = 0; i < productBinaryString.length(); i++){
        key += Integer.toString(random.nextInt(2));
        }
  


        //
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < productBinaryString.length(); i++){
            if (key.charAt(i) == productBinaryString.charAt(i)){
                result.append('0');
            } else {
                result.append('1');
            }
            
        }



//


        String productPath = filepath + ".tcrt";
        try(FileWriter productWriter = new FileWriter(productPath, false)){


            productWriter.write(result.toString());
            IO.println(productPath);

        } catch (Exception f){
            JOptionPane.showMessageDialog(null, "Error at writing encrypted File to disk");
        }


       String keyPath = filepath + ".tkey";
        try(FileWriter keyWriter = new FileWriter(keyPath, false)){


            keyWriter.write(key);
            key = "";

        };
        


    IO.println("Encryption complete!");
    } catch (Exception e){
        JOptionPane.showMessageDialog(null, "Error at encrypting the file");
    }



}

static void decryptFile(String filepath, String keypath){
    try (BufferedReader fileReader = new BufferedReader(new FileReader(filepath))){
        String fileLine;
        String encryptedFile = "";
        while ((fileLine = fileReader.readLine()) != null){
            encryptedFile += fileLine;
        }
        
        String keyFile = "";
        
        try (BufferedReader keyReader = new BufferedReader(new FileReader(keypath))){
        
        String keyLine;
        while ((keyLine = keyReader.readLine()) != null){
            keyFile += keyLine;
        } 

        StringBuilder res = new StringBuilder();
        for (int i = 0; i < encryptedFile.length(); i++){
            if (encryptedFile.charAt(i) == keyFile.charAt(i)){
                res.append('0');
            } else {
                res.append('1');
            }


        }





        int l = 8; // 8 bit in 1 byte
        ArrayList<String> resStr = new ArrayList<>();
        for (int i = 0; i < res.length(); i += l){
            String sub = res.substring(i, Math.min(i + l, res.length()));
            resStr.add(sub);
        }

        ArrayList<Integer> resInt = new ArrayList<>();

        for (int i = 0; i < resStr.size(); i++){
            resInt.add(Integer.parseInt(resStr.get(i), 2));
            
        }

        StringBuilder sb = new StringBuilder();
        for (int i : resInt){
           sb.append((char) i);
        }
        String finalString = sb.toString();

        
        
        String finalPath = filepath.replace(".tcrt", ".tmsg");
        
        try(FileWriter messageWriter = new FileWriter(finalPath, false)){


            messageWriter.write(finalString);
            finalString = "";

        };



        IO.println("Decryption complete!");
        } catch (Exception f){IO.print(f);}
             


    } catch (Exception e){IO.print(e);}


}

}