package malbeth.javautils.swing;

import org.jdesktop.application.ResourceMap;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class DefaultContextMenu {
    private DefaultContextMenu() {
    }

    public static void addTo(javax.swing.JTextField target, ResourceMap resourceMap) {
        // Initializer context menu
        javax.swing.JPopupMenu meContextMenu = new javax.swing.JPopupMenu();

        javax.swing.JMenuItem miCopy = new javax.swing.JMenuItem(target.getActionMap().get(javax.swing.text.DefaultEditorKit.copyAction));
        miCopy.setText(resourceMap.getString("copy.text"));
        miCopy.setMnemonic(resourceMap.getString("copy.hkey").charAt(0));
        miCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

        javax.swing.JMenuItem miCut = new javax.swing.JMenuItem(target.getActionMap().get(javax.swing.text.DefaultEditorKit.cutAction));
        miCut.setText(resourceMap.getString("cut.text"));
        miCut.setMnemonic(resourceMap.getString("cut.hkey").charAt(0));
        miCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

        javax.swing.JMenuItem miPaste = new javax.swing.JMenuItem(target.getActionMap().get(javax.swing.text.DefaultEditorKit.pasteAction));
        miPaste.setText(resourceMap.getString("paste.text"));
        miPaste.setMnemonic(resourceMap.getString("paste.hkey").charAt(0));
        miPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));

        javax.swing.JMenuItem miSelectAll = new javax.swing.JMenuItem(target.getActionMap().get(javax.swing.text.DefaultEditorKit.selectAllAction));
        miSelectAll.setText(resourceMap.getString("selectAll.text"));
        miSelectAll.setMnemonic(resourceMap.getString("selectAll.hkey").charAt(0));
        miSelectAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        meContextMenu.add(miCut);
        meContextMenu.add(miCopy);
        meContextMenu.add(miPaste);
        meContextMenu.addSeparator();
        meContextMenu.add(miSelectAll);

        // Add popup menu to field
        target.setComponentPopupMenu(meContextMenu);
    }

    public static void addTo(javax.swing.JTextArea target, ResourceMap resourceMap) {
        // Initializer context menu
        javax.swing.JPopupMenu meContextMenu = new javax.swing.JPopupMenu();

        javax.swing.JMenuItem miCopy = new javax.swing.JMenuItem(target.getActionMap().get(javax.swing.text.DefaultEditorKit.copyAction));
        miCopy.setText(resourceMap.getString("copy.text"));

        javax.swing.JMenuItem miCut = new javax.swing.JMenuItem(target.getActionMap().get(javax.swing.text.DefaultEditorKit.cutAction));
        miCut.setText(resourceMap.getString("cut.text"));

        javax.swing.JMenuItem miPaste = new javax.swing.JMenuItem(target.getActionMap().get(javax.swing.text.DefaultEditorKit.pasteAction));
        miPaste.setText(resourceMap.getString("paste.text"));

        javax.swing.JMenuItem miSelectAll = new javax.swing.JMenuItem(target.getActionMap().get(javax.swing.text.DefaultEditorKit.selectAllAction));
        miSelectAll.setText(resourceMap.getString("selectAll.text"));

        meContextMenu.add(miCut);
        meContextMenu.add(miCopy);
        meContextMenu.add(miPaste);
        meContextMenu.add(miSelectAll);

        // Add popup menu to field
        target.setComponentPopupMenu(meContextMenu);
    }
}
