package club.seekrit.SuperSorter;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Os {
    public static final String SEPARATOR = System.getProperty("file.separator");

    public static File subToFile(File root, String sub) {
        return new File(root.getPath() + Os.SEPARATOR + sub);
    }

    public static File newFile(File root, String subdir, String destination) {
        return new File(root.getPath() + Os.SEPARATOR + subdir + Os.SEPARATOR + destination);
    }

    public static String extension(File file) {
        // https://www.baeldung.com/java-file-extension
        // Could use guava or some shit, or just straight up port the Golang library
        String filename = file.getName();
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    // https://stackoverflow.com/a/30925550
    public static String calcSHA1(File file) throws FileNotFoundException,
            IOException, NoSuchAlgorithmException {

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        try (InputStream input = new FileInputStream(file)) {

            byte[] buffer = new byte[8192];
            int len = input.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }

            return new HexBinaryAdapter().marshal(sha1.digest());
        }
    }
}
