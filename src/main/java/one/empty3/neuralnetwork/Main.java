/*
 *
 *  *
 *  *  * Copyright (c) 2025. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2024 Manuel Daniel Dahmen
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

/*
 * Created by JFormDesigner on Sun Jan 23 11:27:24 CET 2022
 */

package one.empty3.neuralnetwork;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import one.empty3.libs.*;

import java.awt.*;
import java.io.File;

/**
 * My class description missing
 *
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class Main extends JFrame {
    private File[] files;


    public Main() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        button1 = new JButton();
        textField1 = new JTextField();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        panel1 = new JPanel();
        label6 = new JLabel();
        textField4 = new JTextField();
        label3 = new JLabel();
        textField2 = new JTextField();
        label4 = new JLabel();
        textField3 = new JTextField();
        label5 = new JLabel();
        textField5 = new JTextField();
        label2 = new JLabel();
        label7 = new JLabel();
        this2 = new JFrame();
        label8 = new JLabel();
        button2 = new JButton();
        textField6 = new JTextField();
        scrollPane2 = new JScrollPane();
        table2 = new JTable();
        panel2 = new JPanel();
        label9 = new JLabel();
        textField7 = new JTextField();
        label10 = new JLabel();
        textField8 = new JTextField();
        label11 = new JLabel();
        textField9 = new JTextField();
        label12 = new JLabel();
        textField10 = new JTextField();
        label13 = new JLabel();
        label14 = new JLabel();

        //======== this ========

        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                        "[fill]",
                // rows
                "[]" +
                        "[]" +
                        "[]" +
                        "[]"));

        //---- label1 ----
        label1.setText("S\u00e9lection du listing CSV(\"\\t,\\n\") id,path,{(coordonn\u00e9es),} {(label)}");
        contentPane.add(label1, "cell 0 0");

        //---- button1 ----
        button1.setText("Choisissez le fichier ...");
        contentPane.add(button1, "cell 1 0");
        contentPane.add(textField1, "cell 0 1");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }
        contentPane.add(scrollPane1, "cell 0 2");

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]" +
                            "[fill]" +
                            "[fill]",
                    // rows
                    "[]" +
                            "[]" +
                            "[]" +
                            "[]"));

            //---- label6 ----
            label6.setText("id");
            panel1.add(label6, "cell 0 0");
            panel1.add(textField4, "cell 1 0");

            //---- label3 ----
            label3.setText("path");
            panel1.add(label3, "cell 0 1");
            panel1.add(textField2, "cell 1 1");

            //---- label4 ----
            label4.setText("coordonn\u00e9es");
            panel1.add(label4, "cell 0 2");
            panel1.add(textField3, "cell 1 2");

            //---- label5 ----
            label5.setText("labels");
            panel1.add(label5, "cell 0 3");
            panel1.add(textField5, "cell 1 3");
        }
        contentPane.add(panel1, "cell 1 2,dock center");

        //---- label2 ----
        label2.setText("Pr\u00e9c\u00e9dent");
        contentPane.add(label2, "cell 0 3");

        //---- label7 ----
        label7.setText("Suivant");
        contentPane.add(label7, "cell 0 3");
        pack();
        setLocationRelativeTo(getOwner());

        //======== this2 ========
        {
            Container this2ContentPane = this2.getContentPane();
            this2ContentPane.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[fill]" +
                            "[fill]",
                    // rows
                    "[]" +
                            "[]" +
                            "[]" +
                            "[]"));

            //---- label8 ----
            label8.setText("S\u00e9lection du listing CSV(\"\\t,\\n\") id,path,{(coordonn\u00e9es),} {(label)}");
            this2ContentPane.add(label8, "cell 0 0");

            //---- button2 ----
            button2.setText("Choisissez le fichier ...");
            this2ContentPane.add(button2, "cell 1 0");
            this2ContentPane.add(textField6, "cell 0 1");

            //======== scrollPane2 ========
            {
                scrollPane2.setViewportView(table2);
            }
            this2ContentPane.add(scrollPane2, "cell 0 2");

            //======== panel2 ========
            {
                panel2.setLayout(new MigLayout(
                        "fill,hidemode 3",
                        // columns
                        "[fill]" +
                                "[fill]" +
                                "[fill]",
                        // rows
                        "[]" +
                                "[]" +
                                "[]" +
                                "[]"));

                //---- label9 ----
                label9.setText("id");
                panel2.add(label9, "cell 0 0");
                panel2.add(textField7, "cell 1 0");

                //---- label10 ----
                label10.setText("path");
                panel2.add(label10, "cell 0 1");
                panel2.add(textField8, "cell 1 1");

                //---- label11 ----
                label11.setText("coordonn\u00e9es");
                panel2.add(label11, "cell 0 2");
                panel2.add(textField9, "cell 1 2");

                //---- label12 ----
                label12.setText("labels");
                panel2.add(label12, "cell 0 3");
                panel2.add(textField10, "cell 1 3");
            }
            this2ContentPane.add(panel2, "cell 1 2,dock center");

            //---- label13 ----
            label13.setText("Pr\u00e9c\u00e9dent");
            this2ContentPane.add(label13, "cell 0 3");

            //---- label14 ----
            label14.setText("Suivant");
            this2ContentPane.add(label14, "cell 0 3");
            this2.pack();
            this2.setLocationRelativeTo(this2.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public void chooseFile() {
        javax.swing.JFileChooser jFileChooser = new javax.swing.JFileChooser();
        jFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.showDialog(null, "Choose file");
        if (jFileChooser.getSelectedFiles() != null) {
            this.files = jFileChooser.getSelectedFiles();
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JButton button1;
    private JTextField textField1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JPanel panel1;
    private JLabel label6;
    private JTextField textField4;
    private JLabel label3;
    private JTextField textField2;
    private JLabel label4;
    private JTextField textField3;
    private JLabel label5;
    private JTextField textField5;
    private JLabel label2;
    private JLabel label7;
    private JFrame this2;
    private JLabel label8;
    private JButton button2;
    private JTextField textField6;
    private JScrollPane scrollPane2;
    private JTable table2;
    private JPanel panel2;
    private JLabel label9;
    private JTextField textField7;
    private JLabel label10;
    private JTextField textField8;
    private JLabel label11;
    private JTextField textField9;
    private JLabel label12;
    private JTextField textField10;
    private JLabel label13;
    private JLabel label14;

    // JFormDesigner - End of variables declaration  //GEN-END:variables
    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
}
