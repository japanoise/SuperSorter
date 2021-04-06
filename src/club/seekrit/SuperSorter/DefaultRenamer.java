package club.seekrit.SuperSorter;

import java.io.File;

public class DefaultRenamer implements Renamer {
    public DefaultRenamer(){}

    @Override
    public String rename(String filename, File file, File destDir) {
        String desired = filename;
        int i = 0;
        while (new File(destDir + Os.SEPARATOR + desired).exists()) {
            desired = filename + "_" + i++ + Os.extension(file);
        }
        return desired;
    }
}
