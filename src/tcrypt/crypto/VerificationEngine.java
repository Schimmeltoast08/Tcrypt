package tcrypt.crypto;

import tcrypt.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.*;
import java.util.logging.Level;

public class VerificationEngine {

    private static final String HASH_FILE =
            System.getProperty("user.home") + "/.local/share/tcrypt/hashes.txt";

    private static final String ALGORITHM = "SHA-256";

    private static final String OS =
            System.getProperty("os.name").toLowerCase(Locale.ROOT);


    public static boolean verify(String filePath) {
        try {
            ensureStorageReady();

            Map<String, String> knownHashes = loadHashes();

            String currentHash = hashFile(filePath);

            String storedHash = knownHashes.get(normalize(filePath));

            if (storedHash == null) {
                Log.log("No stored hash found for file", Level.WARNING);
                return false;
            }

            boolean valid = storedHash.equals(currentHash);

            if (valid) {
                Log.log("Integrity check successful", Level.INFO);
            } else {
                Log.log("Integrity check FAILED", Level.WARNING);
            }

            return valid;

        } catch (Exception e) {
            Log.log("Verification error", Level.SEVERE);
            e.printStackTrace();
            return false;
        }
    }

    public static void saveHash(String filePath) {
        try {
            ensureStorageReady();

            Path path = Path.of(HASH_FILE);
            Map<String, String> hashes = loadHashes();

            String normalizedPath = normalize(filePath);
            String fileHash = hashFile(filePath);

            // update or insert
            hashes.put(normalizedPath, fileHash);

            // rewrite file fully (prevents duplicates)
            List<String> lines = new ArrayList<>();
            for (Map.Entry<String, String> entry : hashes.entrySet()) {
                lines.add(entry.getKey() + "|" + entry.getValue());
            }

            Files.write(
                    path,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            Log.log("Saved SHA-256 hash", Level.INFO);

        } catch (Exception e) {
            Log.log("Could not save hash", Level.SEVERE);
            e.printStackTrace();
        }
    }


    private static String hashFile(String filePath) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);

        File file = new File(filePath);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;

            while ((read = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }

        return bytesToHex(digest.digest());
    }

    private static Map<String, String> loadHashes() throws Exception {
        Map<String, String> map = new HashMap<>();

        File file = new File(HASH_FILE);
        if (!file.exists()) return map;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || !line.contains("|")) continue;

                String[] parts = line.split("\\|", 2);

                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }
        }

        return map;
    }


    private static void ensureStorageReady() throws Exception {
        Path path = Path.of(HASH_FILE);

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
    }

    private static String normalize(String filePath) {
        return new File(filePath).getAbsolutePath();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}