package club.seekrit.SuperSorter;

import java.io.File;

public class Mover {
    private File rootDir;
    private Renamer renamer;

    protected Mover(File rootDir) {
        this(rootDir, new DefaultRenamer());
    }

    protected Mover(File rootDir, Renamer renamer) {
        this.rootDir = rootDir;
        this.renamer = renamer;
    }

    protected void setRenamer(Renamer renamer) {
        this.renamer = renamer;
    }

    protected boolean moveFileToDir(File file, String subDir) {
        File destDir = Os.subToFile(rootDir, subDir);
        String newFileName = renamer.rename(file.getName(), file, destDir);
        return file.renameTo(Os.newFile(rootDir, subDir, newFileName));
    }
}
