package malbeth.javautils.swing;

public abstract class ButtonTabComponent extends javax.swing.JPanel {
    private static final long serialVersionUID = 1420709494475168530L;

    private class TabButton extends javax.swing.JButton implements java.awt.event.ActionListener {
        private static final long serialVersionUID = -3180803151450157935L;

        private class ButtonBorder implements javax.swing.border.Border {
            private boolean inverted;

            public ButtonBorder(boolean inverted) {
                this.inverted = inverted;
            }

            public java.awt.Insets getBorderInsets(java.awt.Component c) {
                return new java.awt.Insets(2, 2, 2, 2);
            }

            public boolean isBorderOpaque() {
                return true;
            }

            public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int width, int height) {
                g.setColor(inverted ? java.awt.SystemColor.controlDkShadow : java.awt.SystemColor.controlLtHighlight);
                g.drawLine(x, y + height - 1, x, y);
                g.drawLine(x, y, x + width - 1, y);

                g.setColor(inverted ? java.awt.SystemColor.controlLtHighlight : java.awt.SystemColor.controlDkShadow);
                g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
                g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
            }
        }

        private class ButtonMouseListener extends java.awt.event.MouseAdapter {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                java.awt.Component component = e.getComponent();
                if (component instanceof TabButton) {
                    TabButton button = (TabButton) component;
                    button.hover = true;
                    button.setContentAreaFilled(true);
                    button.setBorderPainted(true);
                    button.setForeground(java.awt.SystemColor.controlText);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                java.awt.Component component = e.getComponent();
                if (component instanceof TabButton) {
                    TabButton button = (TabButton) component;
                    button.hover = false;
                    if (!button.pressed) {
                        button.setContentAreaFilled(false);
                        button.setBorderPainted(false);
                        button.setForeground(java.awt.SystemColor.textInactiveText);
                    }
                }
            }

            public void mousePressed(java.awt.event.MouseEvent e) {
                java.awt.Component component = e.getComponent();
                if (component instanceof TabButton) {
                    TabButton button = (TabButton) component;
                    button.pressed = true;
                    button.setBorder(new ButtonBorder(true));
                }
            }

            public void mouseReleased(java.awt.event.MouseEvent e) {
                java.awt.Component component = e.getComponent();
                if (component instanceof TabButton) {
                    TabButton button = (TabButton) component;
                    button.pressed = false;
                    button.setBorder(new ButtonBorder(false));
                    if (!button.hover) {
                        button.setContentAreaFilled(false);
                        button.setBorderPainted(false);
                        button.setForeground(java.awt.SystemColor.textInactiveText);
                    }
                }
            }
        }

        ;

        private boolean hover = false;
        private boolean pressed = false;

        public TabButton() {
            setPreferredSize(new java.awt.Dimension(17, 17));
            setToolTipText("Close this tab");
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(new ButtonBorder(false));
            setBorderPainted(false);
            addMouseListener(new ButtonMouseListener());
            setRolloverEnabled(false);
            addActionListener(this);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            ButtonTabComponent.this.closeTab();
        }

        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            if (getModel().isPressed())
                g2.translate(1, 1);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.setColor(getForeground());
            int delta = 5;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    private class TabLabel extends javax.swing.JLabel {
        private static final long serialVersionUID = -6571650719879511987L;

        public TabLabel() {
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        }

        public String getText() {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1)
                return pane.getTitleAt(i);
            return null;
        }
    }

    private final javax.swing.JTabbedPane pane;

    public ButtonTabComponent(final javax.swing.JTabbedPane pane) {
        super(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        if (pane != null) {
            this.pane = pane;

            setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 0, 0));
            setOpaque(false);

            add(new TabLabel());
            add(new TabButton());
        } else
            throw new NullPointerException("TabbedPane is null");
    }

    protected abstract void closeTab();
}
