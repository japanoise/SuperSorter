package club.seekrit.SuperSorter;

import java.io.File;

public interface Renamer {
    String rename(String filename, File file, File destDir);
}
