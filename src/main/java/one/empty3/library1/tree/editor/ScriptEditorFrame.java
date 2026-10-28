/*
 *
 *  *
 *  *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2026 Manuel Daniel Dahmen
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *  *
 *
 *
 *
 *  * Created by $user $date
 *
 *
 */

package one.empty3.library1.tree.editor;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;

public class ScriptEditorFrame extends JFrame {
    private JTextPane editorPane;
    private JTextArea consoleArea;
    private File currentFile;

    public ScriptEditorFrame() {
        setTitle("Algebraic Script Editor");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open...");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        JMenuItem exitItem = new JMenuItem("Exit");

        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        saveAsItem.addActionListener(e -> saveAsFile());
        exitItem.addActionListener(e -> System.exit(0));

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

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton runButton = new JButton("Run Script");
        runButton.addActionListener(e -> runScript());
        toolBar.add(runButton);

        add(toolBar, BorderLayout.NORTH);

        // Editor & Console Split Pane
        editorPane = new JTextPane();
        editorPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane editorScroll = new JScrollPane(editorPane);

        consoleArea = new JTextArea();
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(30, 30, 30));
        consoleArea.setForeground(new Color(220, 220, 220));
        JScrollPane consoleScroll = new JScrollPane(consoleArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorScroll, consoleScroll);
        splitPane.setDividerLocation(350);

        add(splitPane, BorderLayout.CENTER);
    }

    private void runScript() {
        String code = editorPane.getText();
        consoleArea.setText("Executing script...\n");
        String result = ScriptExecutor.execute(code);
        consoleArea.append(result);
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            try {
                String content = CloudStorageManager.loadFromFile(currentFile);
                editorPane.setText(content);
                consoleArea.setText("Loaded file: " + currentFile.getAbsolutePath() + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            saveAsFile();
        } else {
            try {
                CloudStorageManager.saveToFile(currentFile, editorPane.getText());
                consoleArea.setText("Saved file: " + currentFile.getAbsolutePath() + "\n");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                consoleArea.append("Error saving file: " + ex.getMessage() + "\n");
            }
        }
    }

    private void saveAsFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            saveFile();
        }
    }

    private void loadCloud() {
        String url = JOptionPane.showInputDialog(this, "Enter Cloud URL (GET):");
        if (url != null && !url.trim().isEmpty()) {
            try {
                String content = CloudStorageManager.loadFromCloud(url.trim());
                editorPane.setText(content);
                consoleArea.setText("Loaded from cloud: " + url + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading from cloud: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveCloud() {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ScriptEditorFrame().setVisible(true);
        });
    }
}