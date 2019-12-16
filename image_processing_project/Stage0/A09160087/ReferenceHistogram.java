import ij.ImagePlus;
import ij.IJ;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;
import ij.plugin.filter.PlugInFilter;
import java.awt.Color;

public class ReferenceHistogram implements PlugInFilter {
  ImagePlus inputImage;
  int hist[][] = new int[3][256];
  double cumulativeHist[][] = new double[3][256];

  public int setup(String args, ImagePlus im) {
    inputImage = im;
    return DOES_RGB;
  }

  public void run(ImageProcessor inputIP) {
    computeHistogram(inputIP, hist);
    computeCumulativeHist(inputIP, hist, cumulativeHist);
  }

  private void computeHistogram(ImageProcessor processor, int[][] destination) {
    int height = processor.getHeight(), width = processor.getWidth();
    Color pixel;
    for (int u = 0; u < width; u++) {
      for (int v = 0; v < height; v++) {
        pixel = new Color(processor.getPixel(u, v));
        int r = pixel.getRed();
        int g = pixel.getGreen();
        int b = pixel.getBlue();

        destination[0][r]++;
        destination[1][g]++;
        destination[2][b]++;
      }
    }
  }

  private void computeCumulativeHist(ImageProcessor processor, int[][] hist, double[][] destination) {
    for (int channel = 0; channel < 3; channel++) {
      destination[channel][0] = hist[channel][0];
      for (int intensity = 1; intensity < 256; intensity++) {
        destination[channel][intensity] = destination[channel][intensity - 1] + hist[channel][intensity];
      }

      for (int intensity = 0; intensity < 256; intensity++) {
        destination[channel][intensity] /= (double) processor.getHeight() * processor.getWidth();
        IJ.log("Intensity " + intensity + " Channel " + channel + " Value " + destination[channel][intensity]);
      }
    }
  }
}