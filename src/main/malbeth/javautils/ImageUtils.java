package malbeth.javautils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {
    public static final BufferedImage blur(BufferedImage image) {
        int kernelSize = 6;
        float[] blurKernel = new float[kernelSize * kernelSize];
        for (int i = 0; i < blurKernel.length; i++)
            blurKernel[i] = 1.0f / blurKernel.length;

        Map<RenderingHints.Key, Object> hintKeys = new HashMap<RenderingHints.Key, Object>();
        hintKeys.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hintKeys.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hintKeys.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        BufferedImageOp operation = new ConvolveOp(new Kernel(kernelSize, kernelSize, blurKernel), ConvolveOp.EDGE_NO_OP, new RenderingHints(hintKeys));

        if (image.getType() != BufferedImage.TYPE_INT_RGB) {
            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = newImage.createGraphics();
            graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            graphics.dispose();
            return operation.filter(newImage, null);
        } else
            return operation.filter(image, null);
    }

    public static final BufferedImage resize(BufferedImage image, int width, int height, boolean center) {
        float ratio = Math.min(Math.min((float) height / image.getHeight(), (float) width / image.getWidth()), 1);
        int resizedWidth = (int) (image.getWidth() * ratio);
        int resizedHeight = (int) (image.getHeight() * ratio);
        if (ratio <= 0.3)
            image = blur(image);
        if (center) {
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImage.createGraphics();
            graphics.setPaint(Color.white);
            graphics.fillRect(0, 0, width, height);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(image, (width - resizedWidth) / 2, (height - resizedHeight) / 2, resizedWidth, resizedHeight, null);
            graphics.dispose();
            return resizedImage;
        } else if (ratio < 1) {
            BufferedImage resizedImage = new BufferedImage(resizedWidth, resizedHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(image, 0, 0, resizedWidth, resizedHeight, null);
            graphics.dispose();
            return resizedImage;
        } else
            return image;
    }

}
