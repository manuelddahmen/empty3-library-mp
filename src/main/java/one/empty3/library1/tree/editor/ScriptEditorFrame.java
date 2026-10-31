package one.empty3.library1.tree.editor;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ScriptEditorFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTextArea consoleArea;
    private File currentFile;

    public ScriptEditorFrame() {
        setTitle("Algebraic Script Editor with Tabs");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New Tab");
        JMenuItem openItem = new JMenuItem("Open...");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        JMenuItem exitItem = new JMenuItem("Exit");

        newItem.addActionListener(e -> addNewTab("Untitled.calcmath", ""));
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        saveAsItem.addActionListener(e -> saveAsFile());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu cloudMenu = new JMenu("Cloud");
        JMenuItem loadCloudItem = new JMenuItem("Load from URL...");
        JMenuItem saveCloudItem = new JMenuItem("Save to URL...");
        loadCloudItem.addActionListener(e -> loadCloud());
        saveCloudItem.addActionListener(e -> saveCloud());
        cloudMenu.add(loadCloudItem);
        cloudMenu.add(saveCloudItem);
        menuBar.add(cloudMenu);

        setJMenuBar(menuBar);

        // Toolbar (Icon bar / Actions)
        JToolBar toolBar = new JToolBar();
        JButton newButton = new JButton("New");
        JButton runButton = new JButton("Run");
        JButton checkButton = new JButton("Check Syntax");
        JButton closeTabButton = new JButton("Close Tab");

        newButton.addActionListener(e -> addNewTab("Untitled.calcmath", ""));
        runButton.addActionListener(e -> runScript());
        checkButton.addActionListener(e -> checkSyntax());
        closeTabButton.addActionListener(e -> closeCurrentTab());

        toolBar.add(newButton);
        toolBar.add(runButton);
        toolBar.add(checkButton);
        toolBar.addSeparator();
        toolBar.add(closeTabButton);

        add(toolBar, BorderLayout.NORTH);

        // Tabbed pane for editors
        tabbedPane = new JTabbedPane();
        addNewTab("Script 1.calcmath", "sin(x) * 2");

        // Console output
        consoleArea = new JTextArea();
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(30, 30, 30));
        consoleArea.setForeground(new Color(220, 220, 220));
        JScrollPane consoleScroll = new JScrollPane(consoleArea);
        consoleScroll.setPreferredSize(new Dimension(1000, 200));
        // Menu Theme / Appearance
        JMenu themeMenu = new JMenu("Theme");
        JMenuItem lightThemeItem = new JMenuItem("FlatLaf Light");
        JMenuItem darkThemeItem = new JMenuItem("FlatLaf Dark");
        JMenuItem intelliJThemeItem = new JMenuItem("FlatLaf IntelliJ");
        JMenuItem darculaThemeItem = new JMenuItem("FlatLaf Darcula");
        JMenuItem systemThemeItem = new JMenuItem("System Default");

        lightThemeItem.addActionListener(e -> setTheme("com.formdev.flatlaf.FlatLightLaf"));
        darkThemeItem.addActionListener(e -> setTheme("com.formdev.flatlaf.FlatDarkLaf"));
        intelliJThemeItem.addActionListener(e -> setTheme("com.formdev.flatlaf.FlatIntelliJLaf"));
        darculaThemeItem.addActionListener(e -> setTheme("com.formdev.flatlaf.FlatDarculaLaf"));
        systemThemeItem.addActionListener(e -> setTheme(UIManager.getSystemLookAndFeelClassName()));

        themeMenu.add(lightThemeItem);
        themeMenu.add(darkThemeItem);
        themeMenu.add(intelliJThemeItem);
        themeMenu.add(darculaThemeItem);
        themeMenu.addSeparator();
        themeMenu.add(systemThemeItem);
        menuBar.add(themeMenu);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, consoleScroll);
        splitPane.setDividerLocation(450);

        add(splitPane, BorderLayout.CENTER);
    }

    private void addNewTab(String title, String content) {
        JTextPane editorPane = new JTextPane();
        editorPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editorPane.setText(content);
        AdvancedEditorFeatures.applySyntaxHighlighting(editorPane);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        tabbedPane.addTab(title, scrollPane);
        tabbedPane.setSelectedComponent(scrollPane);
    }

    private JTextPane getCurrentEditorPane() {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof JScrollPane scrollPane) {
            JViewport viewPort = scrollPane.getViewport();
            Component view = viewPort.getView();
            if (view instanceof JTextPane textPane) {
                return textPane;
            }
        }
        return null;
    }

    private void closeCurrentTab() {
        int index = tabbedPane.getSelectedIndex();
        if (index >= 0) {
            tabbedPane.remove(index);
        }
    }

    private void runScript() {
        JTextPane editorPane = getCurrentEditorPane();
        if (editorPane == null) return;
        String code = editorPane.getText();
        consoleArea.setText("Executing script...\n");
        String result = ScriptExecutor.execute(code);
        consoleArea.append(result);
    }

    private void checkSyntax() {
        JTextPane editorPane = getCurrentEditorPane();
        if (editorPane == null) return;
        String code = editorPane.getText();
        String status = AdvancedEditorFeatures.checkSyntax(code);
        consoleArea.setText(status + "\n");
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            try {
                String content = CloudStorageManager.loadFromFile(currentFile);
                addNewTab(currentFile.getName(), content);
                consoleArea.setText("Loaded file: " + currentFile.getAbsolutePath() + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        JTextPane editorPane = getCurrentEditorPane();
        if (editorPane == null) return;
        if (currentFile == null) {
            saveAsFile();
        } else {
            try {
                CloudStorageManager.saveToFile(currentFile, editorPane.getText());
                consoleArea.setText("Saved file: " + currentFile.getAbsolutePath() + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAsFile() {
        JTextPane editorPane = getCurrentEditorPane();
        if (editorPane == null) return;
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), currentFile.getName());
            saveFile();
        }
    }

    private void loadCloud() {
        String url = JOptionPane.showInputDialog(this, "Enter Cloud URL (GET):");
        if (url != null && !url.trim().isEmpty()) {
            try {
                String content = CloudStorageManager.loadFromCloud(url.trim());
                addNewTab("CloudScript", content);
                consoleArea.setText("Loaded from cloud: " + url + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading from cloud: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveCloud() {
        JTextPane editorPane = getCurrentEditorPane();
        if (editorPane == null) return;
        String url = JOptionPane.showInputDialog(this, "Enter Cloud URL (POST):");
        if (url != null && !url.trim().isEmpty()) {
            try {
                CloudStorageManager.saveToCloud(url.trim(), editorPane.getText());
                consoleArea.setText("Saved to cloud: " + url + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving to cloud: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void setTheme(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Could not apply theme: " + className + "\n(Make sure FlatLaf dependency is added to build.gradle)",
                    "Theme Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ScriptEditorFrame().setVisible(true);
        });
    }
}