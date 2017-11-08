package malbeth.javautils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class NoiseDemo extends JFrame {
    private double[][] noise;

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
        picture.setBounds(200, 20, 512, 512);
        picture.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        panel.add(picture);

        final JTextField widthField = new JTextField();
        widthField.setBounds(20, 20, 75, 24);
        widthField.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        widthField.setText("512");
        panel.add(widthField);

        final JLabel sizeLabel = new JLabel();
        sizeLabel.setBounds(96, 20, 9, 24);
        sizeLabel.setText("x");
        panel.add(sizeLabel);

        final JTextField heightField = new JTextField();
        heightField.setBounds(105, 20, 75, 24);
        heightField.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        heightField.setText("512");
        panel.add(heightField);

        final JLabel octaveLabel = new JLabel();
        octaveLabel.setBounds(20, 50, 75, 24);
        octaveLabel.setText("Octave:");
        panel.add(octaveLabel);

        final JTextField octaveField = new JTextField();
        octaveField.setBounds(105, 50, 75, 24);
        octaveField.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        octaveField.setText("5");
        panel.add(octaveField);

        final JButton mapButton1 = new JButton("White Noise");
        mapButton1.setBounds(20, 90, 160, 30);
        mapButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int height = Math.max(Math.min(Integer.parseInt(heightField.getText()), 512), 0);
                int width = Math.max(Math.min(Integer.parseInt(widthField.getText()), 512), 0);
                noise = Noise.whiteNoise(width, height);
                picture.setIcon(generateImage(noise));
            }
        });
        panel.add(mapButton1);

        final JButton mapButton2 = new JButton("Smooth Noise");
        mapButton2.setBounds(20, 130, 160, 30);
        mapButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int octave = Math.max(Math.min(Integer.parseInt(octaveField.getText()),10), 0);
                double[][] data = Noise.smoothNoise(noise, octave);
                picture.setIcon(generateImage(data));
            }
        });
        panel.add(mapButton2);

        final JButton mapButton3 = new JButton("Perlin Noise");
        mapButton3.setBounds(20, 170, 160, 30);
        mapButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int octave = Math.max(Math.min(Integer.parseInt(octaveField.getText()),10), 0);
                double[][] data = Noise.perlinNoise(noise, 1+octave);
                picture.setIcon(generateImage(data));
            }
        });
        panel.add(mapButton3);

        final JButton mapButton4 = new JButton("Smooth/Perlin");
        mapButton4.setBounds(20, 210, 160, 30);
        mapButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int octave = Math.max(Math.min(Integer.parseInt(octaveField.getText()),10), 0);
                double[][] data = Noise.blend(Noise.smoothNoise(noise, 1+octave), Noise.perlinNoise(noise, 1+octave), .5);
                picture.setIcon(generateImage(data));
            }
        });
        panel.add(mapButton4);

        final JButton mapButton5 = new JButton("Smooth/Smooth");
        mapButton5.setBounds(20, 250, 160, 30);
        mapButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int octave = Math.max(Math.min(Integer.parseInt(octaveField.getText()),10), 0);
                double[][] data = Noise.blend(Noise.smoothNoise(noise, 1+octave), Noise.smoothNoise(noise, octave), .5);
                picture.setIcon(generateImage(data));
            }
        });
        panel.add(mapButton5);
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

    private ImageIcon generateImage(double[][] data) {
        int width = data.length;
        int height = data[0].length;

        double[] imgData = new double[width * height];
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++)
                imgData[j + i * width] = 255 * data[i][j];

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setPixels(0, 0, width, height, imgData);

        return new ImageIcon(img);
    }

}