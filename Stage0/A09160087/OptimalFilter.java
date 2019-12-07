import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.GaussianBlur;
import java.awt.Color;
import java.util.Arrays;

public class OptimalFilter implements PlugInFilter {
  private ImageProcessor outputImage;
  private ImageProcessor inputImage;

  private void meadianFilter(int radius) {
    int M = this.outputImage.getWidth();
    int N = this.outputImage.getHeight();
    ImageProcessor copy = this.outputImage.duplicate();

    int[] medianArray = new int[(2 * radius + 1) * (2 * radius + 1)];
    int n = 2 * (radius * radius + radius);

    for (int u = radius; u <= M - radius - 2; u++) {
      for (int v = radius; v <= N - radius - 2; v++) {
        int k = 0;
        for (int i = -radius; i <= radius; i++) {
          for (int j = -radius; j <= radius; j++) {
            medianArray[k] = copy.getPixel(u + i, v + j);
            k++;
          }
        }
        Arrays.sort(medianArray);
        this.outputImage.putPixel(u, v, medianArray[n]);
      }
    }
  }

  private void gaussianBlur(double sigmaX, double sigmaY, double accuracy) {
    GaussianBlur blurFilter = new GaussianBlur();
    blurFilter.blurGaussian(this.outputImage, sigmaX, sigmaY, accuracy);
  }

  public int setup(String args, ImagePlus image) {
    this.inputImage = image.getProcessor();
    this.outputImage = this.inputImage.duplicate();

    return DOES_RGB;
  }

  public void run(ImageProcessor ip) {
    // gaussianBlur(3, 3, 0.01);
    meadianFilter(15);
    (new ImagePlus("OptimalImage", outputImage)).show();
  }
}