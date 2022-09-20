package club.seekrit.SuperSorter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Window {
    private static final String VERSION_STRING = "1.0.2";
    private Mover mover;
    private File rootDir;
    private File unsortedDir;
    private File openFile;
    private JLabel displayImage;
    private JScrollPane imageScrollPane;
    private List<String> filesToSort;
    private Iterator<String> filesIterator;
    private int sorted = 0;
    private int totalFiles;
    private JLabel statusBar;

    private JFrame frame;
    private Component sidebar;

    protected Window() {}

    protected void getDirsThenRun() {
        getDirs();

        run(rootDir, unsortedDir);
    }

    protected void run(File root, File unsorted) {
        rootDir = root;
        unsortedDir = unsorted;
        mover = new Mover(rootDir);
        JOptionPane.showMessageDialog(null,
                "This program operates destructively. There is no 'undo'."
                + " It is highly recommended that you backup your files before continuing.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);

        String[] files = unsortedDir.list();
        if (files == null) {
            filesToSort = new ArrayList<>();
        } else {
            filesToSort = Arrays.stream(files).filter(s -> !Os.subToFile(rootDir, s).isDirectory()).sorted()
                    .collect(Collectors.toList());
        }
        totalFiles = filesToSort.size();
        filesIterator = filesToSort.listIterator();

        frame = new JFrame("SuperSorter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createMenuBar());
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());
        sidebar = createSidebar();
        pane.add(sidebar, BorderLayout.LINE_START);
        pane.add(createImageBox(), BorderLayout.CENTER);
        statusBar = new JLabel("Ready.");
        pane.add(statusBar, BorderLayout.PAGE_END);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuItem menuItem;
        JMenuBar mb = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(a -> System.exit(0));
        fileMenu.add(menuItem);

        JMenu prefMenu = new JMenu("Prefs");
        menuItem = new JMenuItem("Use simple renamer");
        menuItem.addActionListener(a -> mover.setRenamer(new DefaultRenamer()));
        prefMenu.add(menuItem);
        menuItem = new JMenuItem("Use waifuname-style renamer");
        menuItem.addActionListener(a -> {
            String waifuName = "";
            while (waifuName.equals("")) {
                waifuName = JOptionPane.showInputDialog(frame, "Please enter the waifu's name");
                if (waifuName == null) {
                    // User pressed cancel
                    return;
                }
            }
            mover.setRenamer(new WaifuRenamer(waifuName));
        });
        prefMenu.add(menuItem);

        JMenu helpMenu = new JMenu("Help");
        menuItem = new JMenuItem("About");
        menuItem.addActionListener(a -> JOptionPane.showMessageDialog(frame,
                "SuperSorter\n" +
                "Version " + VERSION_STRING + "\nUnleashed on an unsuspecting world by the Princess Japanoise",
                "About",
                JOptionPane.PLAIN_MESSAGE));
        helpMenu.add(menuItem);

        mb.add(fileMenu);
        mb.add(prefMenu);
        mb.add(helpMenu);
        return mb;
    }

    private void getDirs() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);

        fc.setDialogTitle("Select a root directory into which you want to sort images");
        int i;
        do {
            i = fc.showOpenDialog(null);
        } while (i != JFileChooser.APPROVE_OPTION);
        rootDir = fc.getSelectedFile();
        fc.setCurrentDirectory(rootDir);

        fc.setDialogTitle("Select a directory of unsorted images");
        do {
            i = fc.showOpenDialog(null);
        } while (i != JFileChooser.APPROVE_OPTION);
        unsortedDir = fc.getSelectedFile();
    }

    private Component createSidebar() {
        String[] rootList = rootDir.list();
        if (rootList == null) {
            return new JLabel("Error: Rootdir has no subdirectories.");
        }

        JPanel panel = new JPanel();

        GridLayout layout = new GridLayout(0, 1);
        panel.setLayout(layout);

        LinkedList<JComponent> buttons = new LinkedList<>();
        JButton skipButton = new JButton("Skip this one");
        skipButton.addActionListener(a->{loadNextImage(); sorted++; updateStatusBar();});
        JButton newDirButton = new JButton("New Directory");
        newDirButton.addActionListener(a->{
            String newPath = JOptionPane.showInputDialog(frame, "Name of new directory?", "New Directory",
                    JOptionPane.QUESTION_MESSAGE);
            if (newPath == null || newPath.equals("")) {
                return;
            }
            String absoluteNewPath = rootDir.getAbsolutePath() + File.separator + newPath;
            try {
                Files.createDirectories(Paths.get(absoluteNewPath));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, e.getMessage(), "Erorr", JOptionPane.ERROR_MESSAGE);
                return;
            }
            SwingUtilities.invokeLater(() -> {
                frame.getContentPane().remove(sidebar);
                sidebar = createSidebar();
                frame.getContentPane().add(sidebar, BorderLayout.LINE_START);
                frame.revalidate();
            });
        });
        buttons.add(skipButton);
        buttons.add(newDirButton);
        buttons.add(new JLabel());

        List<String> subDirs = Arrays.stream(rootList).filter((s) -> Os.subToFile(rootDir, s).isDirectory()).sorted()
                .collect(Collectors.toList());
        if (subDirs.size() == 0) {
            return new JLabel("Error: Rootdir has no subdirectories.");
        }

        for (String subDir: subDirs) {
            JButton button = new JButton(subDir);
            button.addActionListener((a)->doFileRename(subDir));
            buttons.add(button);
        }

        for (JComponent button : buttons) {
            panel.add(button);
        }

        return new JScrollPane(panel);
    }

    private void doFileRename(String subDir) {
        if (!mover.moveFileToDir(openFile, subDir)) {
            JOptionPane.showMessageDialog(frame,
                    "File renaming failed",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        loadNextImage();
        sorted++;
        updateStatusBar();
    }

    private void updateStatusBar() {
        if (filesIterator.hasNext()) {
            statusBar.setText(sorted + " items sorted, " + totalFiles + " total, "
                    + (totalFiles - sorted) + " remaining.");
        } else if (sorted != totalFiles) {
            statusBar.setText("One file left to sort!");
        } else {
            statusBar.setText("All files sorted! Time to brag!");
        }
    }

    private void loadNextImage() {
        if (!filesIterator.hasNext()) {
            JOptionPane.showMessageDialog(frame,
                    "All files sorted. You can exit the program.",
                    "Done",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        openFile = new File(unsortedDir + Os.SEPARATOR + filesIterator.next());

        // This should really not be in the swing thread. But, the code to make it so is so bloody annoying I've
        // decided to just leave it be for now. An EXPERT PROGRAMMER can look at the upstream example:
        // https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/IconDemoProject/src/components/IconDemoApp.java
        ImageIcon img = createImageIcon(openFile, openFile.getName());
        if (img == null) {
            displayImage.setText("Image failed to load. It could be a video or an unsupported filetype.");
        } else {
            displayImage.setIcon(img);
        }
    }

    private Component createImageBox() {
        displayImage = new JLabel();
        imageScrollPane = new JScrollPane(displayImage);
        loadNextImage();
        return imageScrollPane;
    }

    protected ImageIcon createImageIcon(File file, String description) {
        try {
            java.net.URL imgURL = file.toURI().toURL();
            return new ImageIcon(imgURL, description);
        } catch (MalformedURLException e) {
            System.err.println("MalformedURLException when loading " + description + ": " + e);
            return null;
        }
    }

    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
}
