package groovySci.math.plot.components;

import java.awt.event.*;
import java.security.*;

import javax.swing.*;

import groovySci.math.plot.*;


public class DataToolBar extends JToolBar {
    protected JButton buttonPasteToClipboard;
    protected JButton buttonSaveFile;
    private boolean denySaveSecurity;
    private JFileChooser fileChooser;
    private DataPanel dataPanel;

    public DataToolBar(DataPanel dp) {
        dataPanel = dp;
        try {
            fileChooser = new JFileChooser();
        } catch (AccessControlException ace) {
        denySaveSecurity = true;
        }
        buttonPasteToClipboard = new JButton(new ImageIcon(groovySci.math.plot.PlotPanel.class.getResource("icons/toclipboard.png")));
        buttonPasteToClipboard.setToolTipText("Copy data to clipboard");

        buttonSaveFile = new JButton(new ImageIcon(groovySci.math.plot.PlotPanel.class.getResource("icons/tofile.png")));
        buttonSaveFile.setToolTipText("Save data into ASCII file");

        buttonPasteToClipboard.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
    	dataPanel.toClipBoard();
	}
    });
        buttonSaveFile.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
	chooseFile();
	}
    });

        add(buttonPasteToClipboard, null);
        add(buttonSaveFile, null);

        if (!denySaveSecurity) {
	fileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
	saveFile();
	}
    });
 } else {
    buttonSaveFile.setEnabled(false);
    }
}

    void saveFile() {
        java.io.File file = fileChooser.getSelectedFile();
        dataPanel.toASCIIFile(file);
    }

    void chooseFile() {
        fileChooser.showSaveDialog(this);
    }

}
