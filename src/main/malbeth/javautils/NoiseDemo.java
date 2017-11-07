package malbeth.javautils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class NoiseDemo extends JFrame {
    public NoiseDemo() throws ClassNotFoundException {
        initialize();
        setTitle("Noise tester");
        setSize(652, 572);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void initialize() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);

        final JLabel picture = new JLabel();
        picture.setBounds(120, 20, 512, 512);
        picture.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        panel.add(picture);

        final JTextField widthField = new JTextField();
        widthField.setBounds(20, 20, 35, 24);
        widthField.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        widthField.setText("512");
        panel.add(widthField);

        final JLabel sizeLabel = new JLabel();
        sizeLabel.setBounds(56, 20, 9, 24);
        sizeLabel.setText("x");
        panel.add(sizeLabel);

        final JTextField heightField = new JTextField();
        heightField.setBounds(65, 20, 35, 24);
        heightField.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        heightField.setText("512");
        panel.add(heightField);

        final JButton generateButton = new JButton("Map A");
        generateButton.setBounds(20, 60, 80, 30);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int height = Math.max(Math.min(Integer.parseInt(heightField.getText()), 512), 0);
                int width = Math.max(Math.min(Integer.parseInt(widthField.getText()), 512), 0);
                double[] data = Noise.cut(Noise.smoothNoise(width, height, width / 10), 0.6);

                for (int i = 0; i < data.length; i++)
                    data[i] = 255 * data[i];

                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                img.getRaster().setPixels(0, 0, width, height, data);

                picture.setIcon(new ImageIcon(img));
            }
        });
        panel.add(generateButton);

        final JButton mapButton2 = new JButton("Map B");
        mapButton2.setBounds(20, 100, 80, 30);
        mapButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int height = Math.max(Math.min(Integer.parseInt(heightField.getText()), 512), 0);
                int width = Math.max(Math.min(Integer.parseInt(widthField.getText()), 512), 0);
                double[] data = Noise.cut(Noise.perlinNoise(width, height, 5), 0.2);

                for (int i = 0; i < data.length; i++)
                    data[i] = 255 * data[i];

                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                img.getRaster().setPixels(0, 0, width, height, data);

                picture.setIcon(new ImageIcon(img));
            }
        });
        panel.add(mapButton2);

        final JButton mapButton3 = new JButton("Map C");
        mapButton3.setBounds(20, 140, 80, 30);
        mapButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int height = Math.max(Math.min(Integer.parseInt(heightField.getText()), 512), 0);
                int width = Math.max(Math.min(Integer.parseInt(widthField.getText()), 512), 0);
                double[] data = Noise.cut(Noise.blend(Noise.perlinNoise(width, height, 3), Noise.normalize(Noise.turbulence(width, height, width / 10)), 0.5), 0.4);

                for (int i = 0; i < data.length; i++)
                    data[i] = 255 * data[i];

                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                img.getRaster().setPixels(0, 0, width, height, data);

                picture.setIcon(new ImageIcon(img));
            }
        });
        panel.add(mapButton3);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    NoiseDemo application = new NoiseDemo();
                    application.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}