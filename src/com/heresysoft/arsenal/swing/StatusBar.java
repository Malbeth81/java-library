package com.heresysoft.arsenal.swing;

import java.util.Vector;

public class StatusBar extends javax.swing.JPanel {
    private static final long serialVersionUID = 2798287611819731498L;

    private Vector<javax.swing.JLabel> labels = new Vector<javax.swing.JLabel>();

    public StatusBar(int panels) {
        super();
        super.setPreferredSize(new java.awt.Dimension(0, 22));

        this.setBorder(new javax.swing.border.EmptyBorder(4, 0, 4, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        javax.swing.GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        javax.swing.GroupLayout.ParallelGroup vGroup = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        this.setLayout(layout);

        hGroup.addContainerGap();
        for (int i = 0; i < panels; i++) {
            javax.swing.JLabel label = new javax.swing.JLabel();
            hGroup.addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
            hGroup.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE);
            vGroup.addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
            labels.add(label);
            this.add(label);
        }
        hGroup.addContainerGap();

        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);
    }

    public void setText(int panel, String text) {
        javax.swing.JLabel label = labels.elementAt(panel);
        if (label != null)
            label.setText(text);
    }
}
