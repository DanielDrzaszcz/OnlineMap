package com.dandrzas.onlinemap;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.scene.transform.Scale;
import javax.imageio.ImageIO;

/**
 *
 * @author Daniel Drząszcz
 */
public class ImageMenager {

    public File originalFile;
    private BufferedImage originalImage;
    private BufferedImage newImage;

    public ImageMenager(String inputFilePath) {
        originalFile = new File(inputFilePath);
        try {
            originalImage = ImageIO.read(originalFile);
        } catch (IOException e) {
            System.out.println(e + "\nBlad otwarcia pliku: " + inputFilePath);
        }
    }

    public File getOriginalFile() {
        
        return originalFile;
    }

    public File cut(int p1x, int p1y, int p2x, int p2y, String outputFilePath) {
        newImage = originalImage.getSubimage(p1x, p1y, p2x, p2y);
        File outputFile = new File(outputFilePath);
        try {
            ImageIO.write(newImage, "png", outputFile);
        } catch (Exception e) {
            System.out.println(e + "\nBlad zapisu pliku: " + outputFilePath);
        }
        return outputFile;
    }

    public File getScalledFile(double scale, String outputFilePath) {
 
        newImage = new BufferedImage((int)(scale*1000), (int)(scale*1000), BufferedImage.TYPE_INT_ARGB);
        final AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
        final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        newImage = ato.filter(originalImage, newImage);
        File outputFile = new File(outputFilePath);
        try {
            ImageIO.write(newImage, "png", outputFile);
        } catch (Exception e) {
            System.out.println(e + "\nBlad zapisu pliku: " + outputFilePath);
        }
        return outputFile;
    }

}
