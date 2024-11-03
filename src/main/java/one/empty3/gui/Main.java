/*
 *
 *  * Copyright (c) 2024. Manuel Daniel Dahmen
 *  *
 *  *
 *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 *
 */

package one.empty3.gui;

import net.miginfocom.swing.MigLayout;
import one.empty3.*;
import one.empty3.ECImage;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library.core.script.Loader;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * Created by JFormDesigner on Thu Jun 27 11:40:57 CEST 2019
 */


/**
 * My class description missing
 *
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class Main implements PropertyChangeListener {
    private boolean drawingPointCoords;
    private Point click;
    private String toDraw;
    private boolean running = true;
    private boolean refreshXMLactioned;
    private ThreadGraphicalEditor threadGraphicalEditor;
    private boolean selectAndRotate = false;
    private boolean translateR;
    private boolean rotateR;
    private int graphicEditROTATE = 0;
    private int graphicEditMOVE = 1;
    private int graphicEditROTATER = 2;
    private int graphicEditTRANSLATER = 3;
    private int graphicEdit = -1;
    private GraphicalEdit2 graphicalEdit2 = new GraphicalEdit2();
    private MyObservableList<Representable> translate;
    private HashMap<ParametricSurface, Double> U = new HashMap<>();
    private HashMap<ParametricSurface, Double> UV = new HashMap<>();
    private Object threadGrapcalEdition;
    private String jtextfieldU;
    private GraphicalEdit2 loadSave;
    private MeshGEditorThread meshGeditorThread;
    private MeshGraphicalEdit graphicalEditMesh;
    MeshEditorBean meshEditorProps;

    public static void main(String[] args) {
        Main main = new Main();
    }

    public Main() {
        dataModel = new DataModel();
        initComponents();
        getUpdateView().setFF(this);
        getTextureEditor().setMain(this);
        //getREditor().addPropertyChangeListener(this);
        getUpdateView().getzRunner().addPropertyChangeListener(this);
        ThreadDrawingCoords threadDrawingCoords = new ThreadDrawingCoords();
        meshEditorProps = new MeshEditorBean();
        meshEditorProps.setMain(this);
        threadDrawingCoords.start();
        getLoadSave().setMain(this);

        JDialog licence = new JDialog(MainWindow, "Licence");
        JTextArea area = null;
        licence.add(area = new JTextArea("Création d'objets simples\n" +
                "    Copyright (C) 2019-2021  Manuel Dahmen\n" +
                "\n" +
                "    This program is free software: you can redistribute it and/or modify\n" +
                "    it under the terms of the GNU General Public License as published by\n" +
                "    the Free Software Foundation, either version 3 of the License, or\n" +
                "    (at your option) any later version.\n" +
                "\n" +
                "    This program is distributed in the hope that it will be useful,\n" +
                "    but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                "    GNU General Public License for more details.\n" +
                "\n" +
                "    You should have received a copy of the GNU General Public License\n" +
                "    along with this program.  If not, see <https://www.gnu.org/licenses/>."));
        licence.pack();
        area.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                licence.setVisible(false);
                licence.dispose();
            }
        });
        licence.setVisible(true);
        running = true;
        graphicalEdit2 = new GraphicalEdit2();
        graphicalEdit2.setActiveGraphicalEdit(false);
        graphicalEdit2.setMain(this);
        threadGraphicalEditor = new ThreadGraphicalEditor(this);
        threadGraphicalEditor.start();
        treeSelIn = new JList(new ListModelSelection(graphicalEdit2.getSelectionIn()));
        treeSelIn.setCellRenderer(new Rendu());
        treeSelOut = new JList(new ListModelSelection(graphicalEdit2.getSelectionOut()));
        treeSelOut.setCellRenderer(new Rendu());
        graphicalEditMesh = new MeshGraphicalEdit(this);
        meshGeditorThread = new MeshGEditorThread(this);
        meshGeditorThread.start();
    }

    private TextureEditor getTextureEditor() {
        return textureEditor1;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public UpdateViewMain getUpdateView() {
        return updateViewMain;
    }

    public void setUpdateView(UpdateViewMain updateView) {
        this.updateViewMain = updateView;
    }

    private DataModel dataModel;


    private void menuItemNewActionPerformed(ActionEvent e) {
        dataModel = new DataModel();
        dataModel.setScene(new Scene());
    }

    private void ScriptPanelPropertyChange(PropertyChangeEvent e) {
        // TODO add your code here
    }

    /*
        private void menuItemLoadActionPerformed(ActionEvent e) {
            JFileChooser jFileChooser = new JFileChooser(".");
            jFileChooser.setDialogTitle("Open file");
            jFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            jFileChooser.showDialog(MainWindow, "Load");
            File selectedFile = jFileChooser.getSelectedFile();
            Scene scene = new Scene();
            try {
                new Loader().load(selectedFile, scene);
                setDataModel(new DataModel(selectedFile));
            } catch (VersionNonSupporteeException e1) {
                e1.printStackTrace();
            } catch (ExtensionFichierIncorrecteException e1) {
                e1.printStackTrace();
            }
        }
    */
    private void menuItemSaveActionPerformed(ActionEvent e) {
        JFileChooser jFileChooser = new JFileChooser(".");
        jFileChooser.setDialogTitle("Save file");
        jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        jFileChooser.showDialog(MainWindow, "Save");
        File selectedFile = jFileChooser.getSelectedFile();
        Scene scene = new Scene();
        try {
            new Loader().saveTxt(selectedFile, scene);
            dataModel = new DataModel();
            dataModel.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void editorPropertyChange(PropertyChangeEvent e) {
        // TODO add your code here
    }

    private void MainWindowKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            getUpdateView().getzRunner().setStopCurrentRender(true);
        }
    }

    private void updateViewMouseClicked(MouseEvent e) {
        try {
            Logger.getAnonymousLogger().log(Level.INFO, "" + e.getX());
            Logger.getAnonymousLogger().log(Level.INFO, "" + e.getY());
            Point3D point3D = getUpdateView().getzRunner().getzBuffer().clickAt(
                    (int) e.getX(), (int) e.getY());

            if (point3D == null && point3D.equals(ZBufferImpl.INFINITY)) {
                toDraw = "background(texture)";
            } else {
                String[] ps = new String[3];
                for (int i = 0; i < 3; i++)
                    ps[i] = String.format("%.3f", point3D.get(i));
                toDraw = "p3(" + ps[0] + ", " + ps[1] + ", " + ps[2] + ")";
            }
            click = e.getPoint();
            drawingPointCoords = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void buttonSaveRActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Thread() {

            @Override
            public void run() {
                super.run();
                Integer imageWidth;
                Integer imageHeight;
                try {
                    imageWidth = Integer.parseInt(textFieldXres.getText());
                    imageHeight = Integer.parseInt(textFieldYres.getText());
                } catch (NumberFormatException ex) {
                    imageWidth = -1;
                    imageHeight = -1;
                    Logger.getAnonymousLogger().info("Invalid numbers (must be int>0 => default");
                }
                if (imageHeight <= 0 || imageWidth <= 0) {
                    imageWidth = 1920;
                    imageHeight = 1080;

                }
                ZBufferImpl zBuffer = new ZBufferImpl(imageWidth, imageHeight);
                zBuffer.scene(getDataModel().getScene());
                zBuffer.setDisplayType(updateViewMain.getView().getzDiplayType());
                zBuffer.draw();
                ECImage image = zBuffer.image();
                try {
                    File file = new File(getDataModel().getNewImageFile());
                    ImageIO.write(image, "jpg", file);
                    Logger.getAnonymousLogger().info("Image rendered and saved as <a href=" + file.getCanonicalPath() + "\">" + file.getCanonicalPath() + "</a>");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

        });
    }

    private void checkBoxBindToPreviewActionPerformed(ActionEvent e) {
        if (checkBoxBindToPreview.isSelected()) {
            bindingGroup.bind();
        } else {
            bindingGroup.unbind();
        }
    }

    private void buttonRenderActionPerformed(ActionEvent e) {
        Logger.getAnonymousLogger().info("Nothing here");
    }

    private void tabbedPaneXMLComponentAdded(ContainerEvent e) {
        new Thread() {
            @Override
            public void run() {
                while (isRunning()) {
                    if (isRefreshXMLactioned())
                        try {
                            Scene scene = getDataModel().getScene();
                            StringBuilder stringBuilder = new StringBuilder();
                            scene.xmlRepresentation(getDataModel().getDirectory(false), scene, stringBuilder);

                            textAreaXML.setText(stringBuilder.toString());

                            setRefreshXMLactioned(false);
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public boolean isRunning() {
        return running;
    }

    private void buttonRefreshXMLActionPerformed(ActionEvent e) {
        setRefreshXMLactioned(true);
    }

    private void setRefreshXMLactioned(boolean value) {
        this.refreshXMLactioned = value;
    }

    public boolean isRefreshXMLactioned() {
        return refreshXMLactioned;
    }

    private void checkBoxActiveItemStateChanged(ItemEvent e) {

    }

    private void buttonXMLActionPerformed(ActionEvent e) {
        new Thread(() -> {
            while (isRunning()) {
                if (isRefreshXMLactioned())
                    try {
                        Scene scene = getDataModel().getScene();
                        StringBuilder stringBuilder = new StringBuilder();
                        scene.xmlRepresentation(getDataModel().getDirectory(false), scene, stringBuilder);

                        textAreaXML.setText(stringBuilder.toString());

                        setRefreshXMLactioned(false);
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    private void updateViewMouseDragged(MouseEvent e) {
        // TODO add your code here
    }

    private void updateViewMouseMoved(MouseEvent e) {
        // TODO add your code here
    }

    private void updateViewMouseWheelMoved(MouseWheelEvent e) {
        // TODO add your code here
    }

    public boolean isSelectAndRotate() {
        return getUpdateView().getzRunner().getSelRot();

    }

    private void checkBoxSelRotActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(GraphicalEdit2.Action.ROTATE);
        getUpdateView().getzRunner().setSelRot(true);
    }

    private void checkBoxActiveActionPerformed(ActionEvent e) {
        boolean isSelected;
        if (isSelected = ((JCheckBox) e.getSource()).isSelected()) {
            Logger.getAnonymousLogger().log(Level.INFO, "Graphical edition enabled");
        } else {
            if (getGraphicalEdit2() == null) {
                graphicalEdit2 = new GraphicalEdit2();
                graphicalEdit2.setMain(this);
            }
            Logger.getAnonymousLogger().log(Level.INFO, "Graphical edition disabled");
        }
        graphicalEdit2.setActiveGraphicalEdit(isSelected);


    }

    private void radioButton1ActionPerformed(ActionEvent e) {
        this.getGraphicalEdit2().setSelection(((JRadioButton) e.getSource()).isSelected());
        graphicalEdit2.setActionToPerform(GraphicalEdit2.Action.SELECT);
    }

    public GraphicalEdit2 getGraphicalEdit2() {
        return graphicalEdit2;
    }


    public void setGraphicalEdit2(GraphicalEdit2 graphicalEdit2) {
        this.graphicalEdit2 = graphicalEdit2;
    }

    private void checkBoxSelMultipleObjectsActionPerformed(ActionEvent e) {
        graphicalEdit2.setSelectingMultipleObjects(((JCheckBox) e.getSource()).isSelected());

    }

    private void checkBoxSelMultiplePointsActionPerformed(ActionEvent e) {
        graphicalEdit2.setStartSel1(true);
        graphicalEdit2.setSelectArbitraryPointsIn(((JCheckBox) e.getSource()).isSelected());
    }

    private void checkBoxSelArbitraryPointsActionPerformed(ActionEvent e) {
        graphicalEdit2.setSelectArbitraryPointsIn(((JCheckBox) e.getSource()).isSelected());
    }

    private void radioButtonTranslateActionPerformed(ActionEvent e) {
        getMeshEditorProps().setOpType(0);
    }

    private void radioButtonRotateActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(GraphicalEdit2.Action.ROTATE);
    }

    private void checkBoxEndSelActionPerformed(ActionEvent e) {
        graphicalEdit2.setEndSel1(((JCheckBox) e.getSource()).isSelected());
    }

    private void buttonDuplicateOnPointsActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(((JRadioButton) e.getSource()).isSelected() ? GraphicalEdit2.Action.duplicateOnPoints : null);
    }

    private void buttonDuplicateOnCurveActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(((JRadioButton) e.getSource()).isSelected() ? GraphicalEdit2.Action.duplicateOnCurve : null);
    }

    private void buttonDuplicateOnSurfaceActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(((JRadioButton) e.getSource()).isSelected() ? GraphicalEdit2.Action.duplicateOnSurface : null);
    }

    private void buttonExtrudeSelActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(((JRadioButton) e.getSource()).isSelected() ? GraphicalEdit2.Action.extrude : null);
    }

    private void buttonSelGoActionPerformed(ActionEvent e) {
        graphicalEdit2.performAction();
    }

    private void radioButtonSel1ActionPerformed(ActionEvent e) {
        graphicalEdit2.setActiveSelection(0);

    }

    private void radioButtonSel2ActionPerformed(ActionEvent e) {
        graphicalEdit2.setActiveSelection(1);
    }

    public void setSelectAndRotate(boolean selectAndRotate) {
        this.selectAndRotate = selectAndRotate;
    }

    private void textFieldOnSurfaceUActionPerformed(ActionEvent e) {
        getTreeSelIn().getModel().getElementAt(getTreeSelIn().getSelectedIndex());
    }

    private void textFieldOnSurfaceVActionPerformed(ActionEvent e) {
        getTreeSelIn().getModel().getElementAt(getTreeSelIn().getSelectedIndex());
    }

    private void textFieldOnCurveUActionPerformed(ActionEvent e) {
        getTreeSelIn().getModel().getElementAt(getTreeSelIn().getSelectedIndex());
    }

    private void textField1ActionPerformed(ActionEvent e) {
        getGraphicalEdit2().getSelectionIn().forEach(new Consumer<one.empty3.library.Representable>() {
            @Override
            public void accept(one.empty3.library.Representable representable) {

            }
        });
    }

    private void button1ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void toggleButtonTransSelActionPerformed(ActionEvent e) {
        getGraphicalEdit2().setTransSel(((JToggleButton) e.getSource()).isSelected());
    }

    private void toggleButtonRotSelActionPerformed(ActionEvent e) {
        getGraphicalEdit2().setRotSel(((JToggleButton) e.getSource()).isSelected());
    }

    private void tabbedPaneCameraFocusGained(FocusEvent e) {
        // TODO add your code here
    }

    public ThreadGraphicalEditor getThreadGrapcalEdition() {
        return threadGraphicalEditor;
    }

    private void createUIComponents() {
        // TODO: add custom component creation code here
    }

    public String getJtextfieldU0() {
        return textFieldU.getText();
    }

    public String getJtextfieldU() {
        return textFieldU0.getText();
    }

    public String getJtextfield0V() {
        return textField0V.getText();
    }

    public JList<Representable> getTreeSelOut() {
        return treeSelOut;
    }

    public REditor getREditor() {
        return editor;
    }

    public LoadSave getLoadSave() {
        return loadSave1;
    }

    public MyObservableList getMyObservableListSelIn() {
        return this.myObservableListSelIn;
    }

    public MyObservableList getMyObservableListSelOut() {
        return this.myObservableListSelOut;
    }

    private void buttonClearSelActionPerformed(ActionEvent e) {
        getGraphicalEdit2().getCurrentSelection().clear();
    }

    public MeshGEditorThread getMeshGEditorThread() {
        return meshGeditorThread;
    }

    public MeshGraphicalEdit getGraphicalEditMesh() {
        return graphicalEditMesh;
    }

    public JList<one.empty3.library.Representable> getTreeSelIn() {
        return this.treeSelIn;
    }

    private void radioButton5ActionPerformed(ActionEvent e) {
        this.getMeshEditorProps().setOpType(1);
    }

    private void radioButton6ActionPerformed(ActionEvent e) {
        this.getMeshEditorProps().setOpType(2);
    }

    private void checkBoxActive2ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void checkBoxActive2ActionPerformed() {
        // TODO add your code here
    }


    private class ThreadDrawingCoords extends Thread {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        this.MainWindow = new JFrame();
        this.panel6 = new JPanel();
        this.menu1 = new JMenu();
        this.menuItemNew = new JMenuItem();
        this.menuItemLoad = new JMenuItem();
        this.menuItemSave = new JMenuItem();
        this.panel5 = new JSplitPane();
        this.panel4 = new JSplitPane();
        this.editor = new REditor(this, dataModel.getScene());
        this.updateViewMain = new UpdateViewMain();
        this.tabbedPane1 = new JTabbedPane();
        this.textureEditor1 = new TextureEditor();
        this.panel1 = new JPanel();
        this.objectEditorBase1 = new ObjectEditorBase();
        this.panel2 = new JPanel();
        this.labelX = new JLabel();
        this.textFieldXres = new JTextField();
        this.checkBoxBindToPreview = new JCheckBox();
        this.labelY = new JLabel();
        this.textFieldYres = new JTextField();
        this.label3 = new JLabel();
        this.comboBox1 = new JComboBox<>();
        this.buttonRender = new JButton();
        this.buttonSaveR = new JButton();
        this.panel3 = new JPanel();
        this.loadSave1 = new LoadSave();
        this.panel7 = new JPanel();
        this.buttonXML = new JButton();
        this.buttonRefreshXML = new JButton();
        this.scrollPane1 = new JScrollPane();
        this.textAreaXML = new JTextArea();
        this.panel8 = new JPanel();
        this.checkBoxActive = new JCheckBox();
        this.label1 = new JLabel();
        this.radioButtonSel1 = new JRadioButton();
        this.scrollPane2 = new JScrollPane();
        this.treeSelIn = new JList<>();
        this.label2 = new JLabel();
        this.radioButton1 = new JRadioButton();
        this.checkBoxSelMultipleObjects = new JCheckBox();
        this.radioButtonSel2 = new JRadioButton();
        this.buttonDuplicateOnPoints = new JRadioButton();
        this.radioButtonTranslate = new JRadioButton();
        this.checkBoxSelMultiplePoints = new JCheckBox();
        this.buttonDuplicateOnCurve = new JRadioButton();
        this.textFieldU = new JTextField();
        this.label4 = new JLabel();
        this.toggleButtonTransSel = new JToggleButton();
        this.checkBoxSelArbitraryPoints = new JCheckBox();
        this.buttonDuplicateOnSurface = new JRadioButton();
        this.textFieldU0 = new JTextField();
        this.textField0V = new JTextField();
        this.radioButtonRotate = new JRadioButton();
        this.checkBoxEndSel = new JCheckBox();
        this.buttonExtrudeSel = new JRadioButton();
        this.label5 = new JLabel();
        this.toggleButtonRotSel = new JToggleButton();
        this.scrollPane3 = new JScrollPane();
        this.treeSelOut = new JList<>();
        this.buttonClearSel = new JButton();
        this.button1 = new JButton();
        this.panel9 = new JPanel();
        this.panel10 = new JPanel();
        this.panel11 = new JPanel();
        this.panel12 = new JPanel();
        this.label6 = new JLabel();
        this.meshType = new JComboBox<>();
        this.panelMeshEdit = new JPanel();
        this.checkBoxActive2 = new JCheckBox();
        this.scrollPane6 = new JScrollPane();
        this.list2 = new JList();
        this.scrollPane4 = new JScrollPane();
        this.list1 = new JList();
        this.radioButton4 = new JRadioButton();
        this.radioButton5 = new JRadioButton();
        this.radioButton6 = new JRadioButton();
        this.label7 = new JLabel();
        this.spinnerX = new JSpinner();
        this.label13 = new JLabel();
        this.textFieldRowI = new JTextField();
        this.label11 = new JLabel();
        this.spinnerU = new JSpinner();
        this.label9 = new JLabel();
        this.spinnerY = new JSpinner();
        this.label8 = new JLabel();
        this.textFieldRowJ = new JTextField();
        this.label12 = new JLabel();
        this.spinnerV = new JSpinner();
        this.label10 = new JLabel();
        this.spinnerZ = new JSpinner();
        this.button2 = new JButton();
        this.button3 = new JButton();
        this.texturesDrawEditMapOnObjectPart1 = new TexturesDrawEditMapOnObjectPart();
        this.myObservableListSelIn = new MyObservableList();
        this.myObservableListSelOut = new MyObservableList();

        //======== MainWindow ========
        {
            this.MainWindow.setTitle("Empty3");
            this.MainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.MainWindow.setBackground(new Color(204, 255, 255));
            this.MainWindow.setVisible(true);
            this.MainWindow.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
            this.MainWindow.setMinimumSize(new Dimension(1000, 400));
            this.MainWindow.setIconImage(new ImageIcon(getClass().getResource("/one/empty3/library/mite.png")).getImage());
            this.MainWindow.addPropertyChangeListener("fieldFunctionsPropertyChanged", e -> ScriptPanelPropertyChange(e));
            this.MainWindow.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    MainWindowKeyPressed(e);
                }
            });
            var MainWindowContentPane = this.MainWindow.getContentPane();
            MainWindowContentPane.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]" +
                            "[fill]",
                    // rows
                    "[]" +
                            "[]" +
                            "[]"));

            //======== panel6 ========
            {
                this.panel6.setLayout(new MigLayout(
                        "fill,hidemode 3",
                        // columns
                        "[fill]",
                        // rows
                        "[]" +
                                "[]" +
                                "[]"));

                //======== menu1 ========
                {
                    this.menu1.setText("File");

                    //---- menuItemNew ----
                    this.menuItemNew.setText("New");
                    this.menuItemNew.addActionListener(e -> {
                        menuItemNewActionPerformed(e);
                        menuItemNewActionPerformed(e);
                    });
                    this.menu1.add(this.menuItemNew);

                    //---- menuItemLoad ----
                    this.menuItemLoad.setText("Load");
                    this.menu1.add(this.menuItemLoad);

                    //---- menuItemSave ----
                    this.menuItemSave.setText("Save");
                    this.menuItemSave.addActionListener(e -> menuItemSaveActionPerformed(e));
                    this.menu1.add(this.menuItemSave);
                }
                this.panel6.add(this.menu1, "pad 5,cell 0 0 1 3,aligny top,growy 0,wmin 100,hmin 20");

                //======== panel5 ========
                {
                    this.panel5.setOrientation(JSplitPane.VERTICAL_SPLIT);

                    //======== panel4 ========
                    {

                        //---- editor ----
                        this.editor.addPropertyChangeListener(e -> editorPropertyChange(e));
                        this.panel4.setLeftComponent(this.editor);

                        //---- updateViewMain ----
                        this.updateViewMain.setBackground(new Color(204, 255, 204));
                        this.updateViewMain.setMinimumSize(new Dimension(400, 400));
                        this.updateViewMain.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                updateViewMouseClicked(e);
                                updateViewMouseClicked(e);
                            }
                        });
                        this.updateViewMain.addMouseMotionListener(new MouseMotionAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                updateViewMouseDragged(e);
                            }

                            @Override
                            public void mouseMoved(MouseEvent e) {
                                updateViewMouseMoved(e);
                            }
                        });
                        this.updateViewMain.addMouseWheelListener(e -> updateViewMouseWheelMoved(e));
                        this.panel4.setRightComponent(this.updateViewMain);
                    }
                    this.panel5.setTopComponent(this.panel4);

                    //======== tabbedPane1 ========
                    {
                        this.tabbedPane1.setMinimumSize(new Dimension(400, 300));
                        this.tabbedPane1.addContainerListener(new ContainerAdapter() {
                            @Override
                            public void componentAdded(ContainerEvent e) {
                                tabbedPaneXMLComponentAdded(e);
                            }
                        });
                        this.tabbedPane1.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusGained(FocusEvent e) {
                                tabbedPaneCameraFocusGained(e);
                            }
                        });

                        //---- textureEditor1 ----
                        this.textureEditor1.setMinimumSize(new Dimension(400, 300));
                        this.tabbedPane1.addTab("Textures", this.textureEditor1);

                        //======== panel1 ========
                        {
                            this.panel1.setLayout(new MigLayout(
                                    "hidemode 3",
                                    // columns
                                    "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]",
                                    // rows
                                    "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]"));
                            this.panel1.add(this.objectEditorBase1, "cell 0 0 6 6,dock center");
                        }
                        this.tabbedPane1.addTab("Position", this.panel1);

                        //======== panel2 ========
                        {
                            this.panel2.setLayout(new MigLayout(
                                    "hidemode 3",
                                    // columns
                                    "[fill]" +
                                            "[fill]" +
                                            "[fill]",
                                    // rows
                                    "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]"));

                            //---- labelX ----
                            this.labelX.setText("res.x");
                            this.panel2.add(this.labelX, "cell 0 0");
                            this.panel2.add(this.textFieldXres, "cell 1 0");

                            //---- checkBoxBindToPreview ----
                            this.checkBoxBindToPreview.setText("Binds to preview");
                            this.checkBoxBindToPreview.setSelected(true);
                            this.checkBoxBindToPreview.addActionListener(e -> checkBoxBindToPreviewActionPerformed(e));
                            this.panel2.add(this.checkBoxBindToPreview, "cell 2 0");

                            //---- labelY ----
                            this.labelY.setText("res.y");
                            this.panel2.add(this.labelY, "cell 0 1");
                            this.panel2.add(this.textFieldYres, "cell 1 1");

                            //---- label3 ----
                            this.label3.setText("Rendering type");
                            this.panel2.add(this.label3, "cell 0 2");

                            //---- comboBox1 ----
                            this.comboBox1.setModel(new DefaultComboBoxModel<>(new String[]{
                                    "DISPLAY_ALL",
                                    "SURFACE_DISPLAY_TEXT_QUADS",
                                    "SURFACE_DISPLAY_TEXT_TRI",
                                    "SURFACE_DISPLAY_COL_QUADS",
                                    "SURFACE_DISPLAY_COL_TRI",
                                    "SURFACE_DISPLAY_LINES",
                                    "SURFACE_DISPLAY_POINTS"
                            }));
                            this.panel2.add(this.comboBox1, "cell 1 2");

                            //---- buttonRender ----
                            this.buttonRender.setText("Render");
                            this.buttonRender.addActionListener(e -> buttonRenderActionPerformed(e));
                            this.panel2.add(this.buttonRender, "cell 0 3");

                            //---- buttonSaveR ----
                            this.buttonSaveR.setText("Save");
                            this.buttonSaveR.addActionListener(e -> buttonSaveRActionPerformed(e));
                            this.panel2.add(this.buttonSaveR, "cell 1 3");
                        }
                        this.tabbedPane1.addTab("Rendu", this.panel2);

                        //======== panel3 ========
                        {
                            this.panel3.setLayout(new MigLayout(
                                    "hidemode 3",
                                    // columns
                                    "[fill]" +
                                            "[fill]",
                                    // rows
                                    "[]" +
                                            "[]" +
                                            "[]"));
                            this.panel3.add(this.loadSave1, "cell 0 0 2 3,dock center");
                        }
                        this.tabbedPane1.addTab("LOad/save/export", this.panel3);

                        //======== panel7 ========
                        {
                            this.panel7.setLayout(new MigLayout(
                                    "hidemode 3",
                                    // columns
                                    "[fill]" +
                                            "[fill]" +
                                            "[fill]",
                                    // rows
                                    "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]"));

                            //---- buttonXML ----
                            this.buttonXML.setText("Open");
                            this.buttonXML.addActionListener(e -> {
                                buttonXMLActionPerformed(e);
                                buttonXMLActionPerformed(e);
                            });
                            this.panel7.add(this.buttonXML, "cell 0 0");

                            //---- buttonRefreshXML ----
                            this.buttonRefreshXML.setText("Refresh");
                            this.buttonRefreshXML.addActionListener(e -> buttonRefreshXMLActionPerformed(e));
                            this.panel7.add(this.buttonRefreshXML, "cell 0 0");

                            //======== scrollPane1 ========
                            {

                                //---- textAreaXML ----
                                this.textAreaXML.setLineWrap(true);
                                this.textAreaXML.setTabSize(4);
                                this.textAreaXML.setWrapStyleWord(true);
                                this.scrollPane1.setViewportView(this.textAreaXML);
                            }
                            this.panel7.add(this.scrollPane1, "cell 1 0,dock center");
                        }
                        this.tabbedPane1.addTab("XML", this.panel7);

                        //======== panel8 ========
                        {
                            this.panel8.setLayout(new MigLayout(
                                    "fill,hidemode 3",
                                    // columns
                                    "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]" +
                                            "[fill]",
                                    // rows
                                    "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]" +
                                            "[]"));

                            //---- checkBoxActive ----
                            this.checkBoxActive.setText("Active graphical markers");
                            this.checkBoxActive.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                            this.checkBoxActive.setBorderPainted(true);
                            this.checkBoxActive.addActionListener(e -> {
                                checkBoxActiveActionPerformed(e);
                                checkBoxActiveActionPerformed(e);
                                checkBoxActiveActionPerformed(e);
                                checkBoxActiveActionPerformed(e);
                                checkBoxActiveActionPerformed(e);
                            });
                            this.panel8.add(this.checkBoxActive, "cell 1 0");

                            //---- label1 ----
                            this.label1.setText("Selections (1 & 2) options ");
                            this.panel8.add(this.label1, "cell 3 0");

                            //---- radioButtonSel1 ----
                            this.radioButtonSel1.setText("Selection 1 (IN)");
                            this.radioButtonSel1.setSelected(true);
                            this.radioButtonSel1.addActionListener(e -> {
                                radioButtonSel1ActionPerformed(e);
                                radioButtonSel1ActionPerformed(e);
                            });
                            this.panel8.add(this.radioButtonSel1, "cell 4 0");

                            //======== scrollPane2 ========
                            {

                                //---- treeSelIn ----
                                this.treeSelIn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                this.scrollPane2.setViewportView(this.treeSelIn);
                            }
                            this.panel8.add(this.scrollPane2, "cell 5 0 1 4");

                            //---- label2 ----
                            this.label2.setText("Duplicate");
                            this.panel8.add(this.label2, "cell 7 0");

                            //---- radioButton1 ----
                            this.radioButton1.setText("S\u00e9lection");
                            this.radioButton1.setSelected(true);
                            this.radioButton1.addActionListener(e -> {
                                radioButton1ActionPerformed(e);
                                radioButton1ActionPerformed(e);
                            });
                            this.panel8.add(this.radioButton1, "cell 1 1");

                            //---- checkBoxSelMultipleObjects ----
                            this.checkBoxSelMultipleObjects.setText("Selection Multiple objects");
                            this.checkBoxSelMultipleObjects.addActionListener(e -> {
                                checkBoxSelMultipleObjectsActionPerformed(e);
                                checkBoxSelMultipleObjectsActionPerformed(e);
                                checkBoxSelMultipleObjectsActionPerformed(e);
                                checkBoxSelMultipleObjectsActionPerformed(e);
                            });
                            this.panel8.add(this.checkBoxSelMultipleObjects, "cell 3 1");

                            //---- radioButtonSel2 ----
                            this.radioButtonSel2.setText("Selection 2 (OUT)");
                            this.radioButtonSel2.addActionListener(e -> {
                                radioButtonSel2ActionPerformed(e);
                                radioButtonSel2ActionPerformed(e);
                                radioButtonSel2ActionPerformed(e);
                                radioButtonSel2ActionPerformed(e);
                            });
                            this.panel8.add(this.radioButtonSel2, "cell 4 1");

                            //---- buttonDuplicateOnPoints ----
                            this.buttonDuplicateOnPoints.setText("Duplicate object on p");
                            this.buttonDuplicateOnPoints.addActionListener(e -> {
                                buttonDuplicateOnPointsActionPerformed(e);
                                buttonDuplicateOnPointsActionPerformed(e);
                            });
                            this.panel8.add(this.buttonDuplicateOnPoints, "cell 7 1");

                            //---- radioButtonTranslate ----
                            this.radioButtonTranslate.setText("Translate");
                            this.radioButtonTranslate.addActionListener(e -> {
                                radioButtonTranslateActionPerformed(e);
                                radioButtonTranslateActionPerformed(e);
                                radioButtonTranslateActionPerformed(e);
                                radioButtonTranslateActionPerformed(e);
                            });
                            this.panel8.add(this.radioButtonTranslate, "cell 1 2");

                            //---- checkBoxSelMultiplePoints ----
                            this.checkBoxSelMultiplePoints.setText("Select multiple (?)");
                            this.checkBoxSelMultiplePoints.setActionCommand("Select multiple points");
                            this.checkBoxSelMultiplePoints.addActionListener(e -> checkBoxSelMultiplePointsActionPerformed(e));
                            this.panel8.add(this.checkBoxSelMultiplePoints, "cell 3 2");

                            //---- buttonDuplicateOnCurve ----
                            this.buttonDuplicateOnCurve.setText("on curve");
                            this.buttonDuplicateOnCurve.addActionListener(e -> {
                                buttonDuplicateOnCurveActionPerformed(e);
                                buttonDuplicateOnCurveActionPerformed(e);
                            });
                            this.panel8.add(this.buttonDuplicateOnCurve, "cell 7 2");

                            //---- textFieldU ----
                            this.textFieldU.setToolTipText("u");
                            this.textFieldU.setText("0.0");
                            this.textFieldU.addActionListener(e -> {
                                textFieldOnCurveUActionPerformed(e);
                                textField1ActionPerformed(e);
                            });
                            this.panel8.add(this.textFieldU, "cell 8 2 2 1");

                            //---- label4 ----
                            this.label4.setText("Translate all selection");
                            this.panel8.add(this.label4, "cell 1 3");

                            //---- toggleButtonTransSel ----
                            this.toggleButtonTransSel.setText("Yes or no");
                            this.toggleButtonTransSel.addActionListener(e -> toggleButtonTransSelActionPerformed(e));
                            this.panel8.add(this.toggleButtonTransSel, "cell 1 3");

                            //---- checkBoxSelArbitraryPoints ----
                            this.checkBoxSelArbitraryPoints.setText("Select arb points");
                            this.checkBoxSelArbitraryPoints.addActionListener(e -> checkBoxSelArbitraryPointsActionPerformed(e));
                            this.panel8.add(this.checkBoxSelArbitraryPoints, "cell 3 3");

                            //---- buttonDuplicateOnSurface ----
                            this.buttonDuplicateOnSurface.setText("on surface");
                            this.buttonDuplicateOnSurface.addActionListener(e -> buttonDuplicateOnSurfaceActionPerformed(e));
                            this.panel8.add(this.buttonDuplicateOnSurface, "cell 7 3");

                            //---- textFieldU0 ----
                            this.textFieldU0.setToolTipText("u");
                            this.textFieldU0.setText("0.0");
                            this.textFieldU0.addActionListener(e -> textFieldOnSurfaceUActionPerformed(e));
                            this.panel8.add(this.textFieldU0, "cell 8 3 2 1");

                            //---- textField0V ----
                            this.textField0V.setToolTipText("v");
                            this.textField0V.setText("0.0");
                            this.textField0V.addActionListener(e -> textFieldOnSurfaceVActionPerformed(e));
                            this.panel8.add(this.textField0V, "cell 10 3 2 1");

                            //---- radioButtonRotate ----
                            this.radioButtonRotate.setText("Rotate");
                            this.radioButtonRotate.addActionListener(e -> {
                                radioButtonRotateActionPerformed(e);
                                radioButtonRotateActionPerformed(e);
                            });
                            this.panel8.add(this.radioButtonRotate, "cell 1 4");

                            //---- checkBoxEndSel ----
                            this.checkBoxEndSel.setText("End selection (?)");
                            this.checkBoxEndSel.addActionListener(e -> checkBoxEndSelActionPerformed(e));
                            this.panel8.add(this.checkBoxEndSel, "cell 3 4");

                            //---- buttonExtrudeSel ----
                            this.buttonExtrudeSel.setText("Extrude selection");
                            this.buttonExtrudeSel.addActionListener(e -> buttonExtrudeSelActionPerformed(e));
                            this.panel8.add(this.buttonExtrudeSel, "cell 7 4");

                            //---- label5 ----
                            this.label5.setText("Rotate all selection");
                            this.panel8.add(this.label5, "cell 1 5");

                            //---- toggleButtonRotSel ----
                            this.toggleButtonRotSel.setText("Yes or no");
                            this.toggleButtonRotSel.addActionListener(e -> toggleButtonRotSelActionPerformed(e));
                            this.panel8.add(this.toggleButtonRotSel, "cell 1 5");

                            //======== scrollPane3 ========
                            {

                                //---- treeSelOut ----
                                this.treeSelOut.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                this.scrollPane3.setViewportView(this.treeSelOut);
                            }
                            this.panel8.add(this.scrollPane3, "cell 5 5 1 3");

                            //---- buttonClearSel ----
                            this.buttonClearSel.setText("Clear selection");
                            this.buttonClearSel.addActionListener(e -> buttonClearSelActionPerformed(e));
                            this.panel8.add(this.buttonClearSel, "cell 1 6");

                            //---- button1 ----
                            this.button1.setText("GO");
                            this.button1.addActionListener(e -> buttonSelGoActionPerformed(e));
                            this.panel8.add(this.button1, "cell 7 7");
                        }
                        this.tabbedPane1.addTab("Modify", this.panel8);

                        //======== panel9 ========
                        {
                            this.panel9.setLayout(new MigLayout(
                                    "hidemode 3",
                                    // columns
                                    "[fill]" +
                                            "[fill]",
                                    // rows
                                    "[]" +
                                            "[]" +
                                            "[]"));
                        }
                        this.tabbedPane1.addTab("Copy/PasteTranslate/Rotate", this.panel9);

                        //======== panel10 ========
                        {
                            this.panel10.setLayout(new MigLayout(
                                    "hidemode 3",
                                    // columns
                                    "[fill]" +
                                            "[fill]",
                                    // rows
                                    "[]" +
                                            "[]" +
                                            "[]"));
                        }
                        this.tabbedPane1.addTab("Copy/Paste on Object", this.panel10);

                        //======== panel11 ========
                        {
                            this.panel11.setLayout(new MigLayout(
                                    "hidemode 3",
                                    // columns
                                    "[fill]" +
                                            "[fill]",
                                    // rows
                                    "[]" +
                                            "[]" +
                                            "[]"));
                        }
                        this.tabbedPane1.addTab("Camera move & orientation", this.panel11);

                        //======== panel12 ========
                        {
                            this.panel12.setLayout(new MigLayout(
                                    "fill,hidemode 3",
                                    // columns
                                    "[fill]" +
                                            "[fill]",
                                    // rows
                                    "[]" +
                                            "[]" +
                                            "[]"));

                            //---- label6 ----
                            this.label6.setText("New mesh");
                            this.panel12.add(this.label6, "cell 0 0");

                            //---- meshType ----
                            this.meshType.setModel(new DefaultComboBoxModel<>(new String[]{
                                    "Parametric Surface",
                                    "Sphere",
                                    "Cube",
                                    "Plane"
                            }));
                            this.panel12.add(this.meshType, "cell 0 0");

                            //======== panelMeshEdit ========
                            {
                                this.panelMeshEdit.setLayout(new MigLayout(
                                        "fill,hidemode 3",
                                        // columns
                                        "[fill]" +
                                                "[fill]" +
                                                "[fill]" +
                                                "[fill]" +
                                                "[fill]" +
                                                "[fill]" +
                                                "[fill]" +
                                                "[fill]" +
                                                "[fill]" +
                                                "[fill]" +
                                                "[fill]" +
                                                "[fill]",
                                        // rows
                                        "[]" +
                                                "[]" +
                                                "[]" +
                                                "[]" +
                                                "[]" +
                                                "[]" +
                                                "[]" +
                                                "[]" +
                                                "[]" +
                                                "[]"));

                                //---- checkBoxActive2 ----
                                this.checkBoxActive2.setText("Active graphical markers");
                                this.checkBoxActive2.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                                this.checkBoxActive2.setBorderPainted(true);
                                this.checkBoxActive2.addActionListener(e -> {
                                    checkBoxActiveActionPerformed(e);
                                    checkBoxActiveActionPerformed(e);
                                    checkBoxActiveActionPerformed(e);
                                    checkBoxActiveActionPerformed(e);
                                    checkBoxActiveActionPerformed(e);
                                    checkBoxActive2ActionPerformed(e);
                                    checkBoxActive2ActionPerformed(e);
                                    checkBoxActive2ActionPerformed();
                                    checkBoxActive2ActionPerformed(e);
                                });
                                this.panelMeshEdit.add(this.checkBoxActive2, "cell 0 0");

                                //======== scrollPane6 ========
                                {
                                    this.scrollPane6.setViewportView(this.list2);
                                }
                                this.panelMeshEdit.add(this.scrollPane6, "cell 1 0 1 3");

                                //======== scrollPane4 ========
                                {
                                    this.scrollPane4.setViewportView(this.list1);
                                }
                                this.panelMeshEdit.add(this.scrollPane4, "cell 2 0 1 3");

                                //---- radioButton4 ----
                                this.radioButton4.setText("Translate selection");
                                this.radioButton4.setSelected(true);
                                this.radioButton4.addActionListener(e -> radioButtonTranslateActionPerformed(e));
                                this.panelMeshEdit.add(this.radioButton4, "cell 0 3");

                                //---- radioButton5 ----
                                this.radioButton5.setText("Create new row");
                                this.radioButton5.addActionListener(e -> radioButton5ActionPerformed(e));
                                this.panelMeshEdit.add(this.radioButton5, "cell 1 3");

                                //---- radioButton6 ----
                                this.radioButton6.setText("Translate p on S");
                                this.radioButton6.addActionListener(e -> radioButton6ActionPerformed(e));
                                this.panelMeshEdit.add(this.radioButton6, "cell 2 3");

                                //---- label7 ----
                                this.label7.setText("Translate new point X");
                                this.panelMeshEdit.add(this.label7, "cell 0 5");
                                this.panelMeshEdit.add(this.spinnerX, "cell 0 5");

                                //---- label13 ----
                                this.label13.setText("new point or row");
                                this.panelMeshEdit.add(this.label13, "cell 1 5");
                                this.panelMeshEdit.add(this.textFieldRowI, "cell 1 5");

                                //---- label11 ----
                                this.label11.setText("Translate u of p");
                                this.panelMeshEdit.add(this.label11, "cell 2 5");
                                this.panelMeshEdit.add(this.spinnerU, "cell 2 5");

                                //---- label9 ----
                                this.label9.setText("Translate new point Y");
                                this.panelMeshEdit.add(this.label9, "cell 0 6");
                                this.panelMeshEdit.add(this.spinnerY, "cell 0 6");

                                //---- label8 ----
                                this.label8.setText("new point or col");
                                this.panelMeshEdit.add(this.label8, "cell 1 6");
                                this.panelMeshEdit.add(this.textFieldRowJ, "cell 1 6");

                                //---- label12 ----
                                this.label12.setText("translate v of p");
                                this.panelMeshEdit.add(this.label12, "cell 2 6");
                                this.panelMeshEdit.add(this.spinnerV, "cell 2 6");

                                //---- label10 ----
                                this.label10.setText("translate new point Z");
                                this.panelMeshEdit.add(this.label10, "cell 0 7");
                                this.panelMeshEdit.add(this.spinnerZ, "cell 0 7");

                                //---- button2 ----
                                this.button2.setText("Ok");
                                this.panelMeshEdit.add(this.button2, "cell 0 8 3 1");

                                //---- button3 ----
                                this.button3.setText("Cancel");
                                this.panelMeshEdit.add(this.button3, "cell 0 8 3 1");
                            }
                            this.panel12.add(this.panelMeshEdit, "cell 0 1");
                        }
                        this.tabbedPane1.addTab("Mesh", this.panel12);
                        this.tabbedPane1.addTab("map edit draw", this.texturesDrawEditMapOnObjectPart1);
                    }
                    this.panel5.setBottomComponent(this.tabbedPane1);
                }
                this.panel6.add(this.panel5, "cell 0 2,dock center");
            }
            MainWindowContentPane.add(this.panel6, "cell 0 0,dock center");
            this.MainWindow.setSize(1620, 1085);
            this.MainWindow.setLocationRelativeTo(this.MainWindow.getOwner());
        }

        //---- buttonGroup3 ----
        var buttonGroup3 = new ButtonGroup();
        buttonGroup3.add(this.radioButtonSel1);
        buttonGroup3.add(this.radioButtonSel2);

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(this.radioButton1);
        buttonGroup1.add(this.radioButtonTranslate);
        buttonGroup1.add(this.radioButtonRotate);
        buttonGroup1.add(this.radioButton4);
        buttonGroup1.add(this.radioButton5);
        buttonGroup1.add(this.radioButton6);

        //---- buttonGroup2 ----
        var buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(this.buttonDuplicateOnPoints);
        buttonGroup2.add(this.buttonDuplicateOnCurve);
        buttonGroup2.add(this.buttonDuplicateOnSurface);
        buttonGroup2.add(this.buttonExtrudeSel);

        //---- bindings ----
        this.bindingGroup = new BindingGroup();
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("width"),
                this.textFieldXres, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST")));
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("height"),
                this.textFieldYres, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST")));
        {
            var binding = Bindings.createAutoBinding(UpdateStrategy.READ,
                    this.comboBox1, BeanProperty.create("selectedIndex"),
                    this.updateViewMain, BeanProperty.create("view.zDiplayType"));
            binding.setSourceNullValue(0);
            binding.setSourceUnreadableValue(0);
            binding.setTargetNullValue(0);
            this.bindingGroup.addBinding(binding);
            binding.bind();
        }
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("view.meshEditorBean.activateMarkers"),
                this.checkBoxActive2, BeanProperty.create("selected")));
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("zRunner.main.meshEditorProps.meshType"),
                this.meshType, BeanProperty.create("selectedItem")));
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("view.meshEditorBean.translateOnSu"),
                this.spinnerU, BeanProperty.create("value")));
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("view.meshEditorBean.translateOnSv"),
                this.spinnerV, BeanProperty.create("value")));
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("view.meshEditorBean.inSelection"),
                this.list1, BeanProperty.create("selectedElements")));
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("view.meshEditorBean.actionOk"),
                this.button2, BeanProperty.create("selected")));
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("view.meshEditorBean.opType"),
                this.radioButton5, BeanProperty.create("selected")));
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                this.updateViewMain, BeanProperty.create("view.opType"),
                this.radioButton4, BeanProperty.create("selected")));
        this.bindingGroup.bind();
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

    }

    private void initComponentBindings() {
        // JFormDesigner - Component bindings initialization - DO NOT MODIFY  //GEN-BEGIN:initBindings
        // Generated using JFormDesigner non-commercial license
        // JFormDesigner - End of component bindings initialization  //GEN-END:initBindings
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JFrame MainWindow;
    private JPanel panel6;
    private JMenu menu1;
    private JMenuItem menuItemNew;
    private JMenuItem menuItemLoad;
    private JMenuItem menuItemSave;
    private JSplitPane panel5;
    private JSplitPane panel4;
    private REditor editor;
    private UpdateViewMain updateViewMain;
    private JTabbedPane tabbedPane1;
    private TextureEditor textureEditor1;
    private JPanel panel1;
    private ObjectEditorBase objectEditorBase1;
    private JPanel panel2;
    private JLabel labelX;
    private JTextField textFieldXres;
    private JCheckBox checkBoxBindToPreview;
    private JLabel labelY;
    private JTextField textFieldYres;
    private JLabel label3;
    private JComboBox<String> comboBox1;
    private JButton buttonRender;
    private JButton buttonSaveR;
    private JPanel panel3;
    private LoadSave loadSave1;
    private JPanel panel7;
    private JButton buttonXML;
    private JButton buttonRefreshXML;
    private JScrollPane scrollPane1;
    private JTextArea textAreaXML;
    private JPanel panel8;
    private JCheckBox checkBoxActive;
    private JLabel label1;
    private JRadioButton radioButtonSel1;
    private JScrollPane scrollPane2;
    private JList<one.empty3.library.Representable> treeSelIn;
    private JLabel label2;
    private JRadioButton radioButton1;
    private JCheckBox checkBoxSelMultipleObjects;
    private JRadioButton radioButtonSel2;
    private JRadioButton buttonDuplicateOnPoints;
    private JRadioButton radioButtonTranslate;
    private JCheckBox checkBoxSelMultiplePoints;
    private JRadioButton buttonDuplicateOnCurve;
    private JTextField textFieldU;
    private JLabel label4;
    private JToggleButton toggleButtonTransSel;
    private JCheckBox checkBoxSelArbitraryPoints;
    private JRadioButton buttonDuplicateOnSurface;
    private JTextField textFieldU0;
    private JTextField textField0V;
    private JRadioButton radioButtonRotate;
    private JCheckBox checkBoxEndSel;
    private JRadioButton buttonExtrudeSel;
    private JLabel label5;
    private JToggleButton toggleButtonRotSel;
    private JScrollPane scrollPane3;
    private JList<one.empty3.library.Representable> treeSelOut;
    private JButton buttonClearSel;
    private JButton button1;
    private JPanel panel9;
    private JPanel panel10;
    private JPanel panel11;
    private JPanel panel12;
    private JLabel label6;
    private JComboBox<String> meshType;
    private JPanel panelMeshEdit;
    private JCheckBox checkBoxActive2;
    private JScrollPane scrollPane6;
    JList list2;
    private JScrollPane scrollPane4;
    private JList list1;
    private JRadioButton radioButton4;
    private JRadioButton radioButton5;
    private JRadioButton radioButton6;
    private JLabel label7;
    private JSpinner spinnerX;
    private JLabel label13;
    private JTextField textFieldRowI;
    private JLabel label11;
    private JSpinner spinnerU;
    private JLabel label9;
    private JSpinner spinnerY;
    private JLabel label8;
    private JTextField textFieldRowJ;
    private JLabel label12;
    private JSpinner spinnerV;
    private JLabel label10;
    private JSpinner spinnerZ;
    private JButton button2;
    private JButton button3;
    private TexturesDrawEditMapOnObjectPart texturesDrawEditMapOnObjectPart1;
    private MyObservableList myObservableListSelIn;
    private MyObservableList myObservableListSelOut;
    private BindingGroup bindingGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public void setEditor(REditor editor) {
        this.editor = editor;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //Logger.getAnonymousLogger().info("Property main changed= "+evt.getPropertyName());
        if (evt.getPropertyName().equals("representable")) {
            Logger.getAnonymousLogger().info("representable changed");
            RepresentableEditor[] representableEditors = {getREditor(), getUpdateView(), getPositionEditor()};
            for (RepresentableEditor representableEditor : representableEditors) {
                if (!evt.getSource().equals(representableEditor) && evt.getNewValue() instanceof Representable) {
                    representableEditor.initValues((one.empty3.library.Representable) evt.getNewValue());
                    Logger.getAnonymousLogger().info(representableEditor.getClass().getName() + ".initValue()");
                }
            }
        } else if (evt.getPropertyName().equals("renderedImageOK")) {
            if (evt.getNewValue() == null) {
            } else {
                if (evt.getNewValue().equals(-1)) {
                    try {
                        this.getUpdateView().getzRunner().setLastImage(new one.empty3.libs.Image(ImageIO.read(new File("resources/img/FAILED.PNG"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (evt.getNewValue().equals(0)) {
                    try {
                        this.getUpdateView().getzRunner().setLastImage(new one.empty3.libs.Image(ImageIO.read(new File("resources/img/WAITING.PNG"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public RepresentableEditor getPositionEditor() {
        return objectEditorBase1;
    }

    public boolean isTranslateR() {
        return translateR;
    }

    public boolean isRotateR() {
        return rotateR;
    }

    public MeshEditorBean getMeshEditorProps() {
        return meshEditorProps;
    }

    public void setMeshEditorProps(MeshEditorBean meshEditorProps) {
        this.meshEditorProps = meshEditorProps;
    }
}
