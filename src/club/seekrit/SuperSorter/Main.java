package club.seekrit.SuperSorter;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Window w = new Window();
        if (args.length > 1) {
            File root = new File(args[0]);
            File unsorted = new File(args[1]);

            if (!(root.exists() && root.isDirectory())) {
                System.err.println("Root directory provided not found or not a directory");
                return;
            }
            if (!(unsorted.exists() && unsorted.isDirectory())) {
                System.err.println("Unsorted images directory provided not found or not a directory");
                return;
            }

            w.run(root, unsorted);
        } else {
            w.getDirsThenRun();
        }
    }
}
