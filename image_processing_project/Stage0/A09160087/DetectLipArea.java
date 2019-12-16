import ij.ImagePlus;
import ij.IJ;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;
import ij.plugin.filter.PlugInFilter;
import java.awt.Color;

public class DetectLipArea implements PlugInFilter {
  ImagePlus inputImage;

  public int setup(String args, ImagePlus im) {
    inputImage = im;
    return DOES_RGB;
  }

  public void run(ImageProcessor inputIp) {
    ImageProcessor copyImageProcessor = inputIp.duplicate();
    binaryLayerThree(copyImageProcessor);

    int[] centroid = getCentroid(copyImageProcessor);
    inputIp.putPixel(centroid[0], centroid[1], 0);

    int averageDistanceFromCentroid = getAverageDistanceFromCentroid(centroid[0], centroid[1], copyImageProcessor);

    drawLipBoundingBox(centroid[0], centroid[1], averageDistanceFromCentroid, inputIp);

    (new ImagePlus("centroid_" + inputImage.getShortTitle(), inputIp)).show();
  }

  private int[] getCentroid(ImageProcessor inputIp) {
    int xPos = 0, yPos = 0, pixelsWithWeight = 0;
    int height = inputIp.getHeight(), width = inputIp.getWidth();

    int[] centroid = new int[2];

    for (int u = 0; u < inputIp.getWidth(); u++) {
      for (int v = 0; v < inputIp.getHeight(); v++) {
        int pixel = inputIp.getPixel(u, v);
        if (pixel == 0) {
          xPos += u;
          yPos += v;
          pixelsWithWeight++;
        }
      }
    }

    centroid[0] = (int) (xPos / (pixelsWithWeight));
    centroid[1] = (int) (yPos / (pixelsWithWeight));

    return centroid;
  }

  private int getAverageDistanceFromCentroid(int x, int y, ImageProcessor inputIp) {
    double totalDistance = 0;
    int pixelsWithWeight = 0;

    for (int u = 0; u < inputIp.getWidth(); u++) {
      for (int v = 0; v < inputIp.getHeight(); v++) {
        int pixel = inputIp.getPixel(u, v);
        if (pixel == 0) {
          pixelsWithWeight++;
          totalDistance += Math.sqrt((x - u) * (x - u) + (v - y) * (v - y));
        }
      }
    }

    return (int) (totalDistance / pixelsWithWeight);
  }

  private void binaryLayerThree(ImageProcessor inputIp) {
    int width = inputIp.getWidth(), height = inputIp.getHeight(), pixel, r, g, b;
    double rb;
    Color color;

    for (int row = 0; row < height; row++)
      for (int col = 0; col < width; col++) {
        color = new Color(inputIp.getPixel(col, row));
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        rb = (r + b) / 2.;
        if (b < g && g < r && rb >= bottom(g) && rb <= top(g))
          inputIp.putPixel(col, row, 0); // BLACK
        else
          inputIp.putPixel(col, row, 16777215); // WHITE
      }
  }

  private void drawLipBoundingBox(int x, int y, int dist, ImageProcessor inputIp) {
    for (int u = x - dist; u < x + dist; u++) {
      inputIp.putPixel(u, y + dist, 255);
      inputIp.putPixel(u, y - dist, 255);
    }

    for (int v = y - dist; v < y + dist; v++) {
      inputIp.putPixel(x - dist, v, 255);
      inputIp.putPixel(x + dist, v, 255);
    }
  }

  public double bottom(int x) {
    return -0.0013 * x * x + 1.2608 * x + 12.067;
  }

  public double top(int x) {
    return -0.0026 * x * x + 1.5713 * x + 14.8;
  }

}