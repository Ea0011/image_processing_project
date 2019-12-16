import ij.ImagePlus;
import ij.IJ;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;
import ij.plugin.filter.PlugInFilter;
import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

public class HSVHistogram implements PlugInFilter {
	ImagePlus inputImage;
  TreeMap<Float, Double> hMap = new TreeMap<Float, Double>();
  TreeMap<Float, Double> sMap = new TreeMap<Float, Double>();
  TreeMap<Float, Double> vMap = new TreeMap<Float, Double>();

	public int setup(String args, ImagePlus im) {
		inputImage = im;
		return DOES_RGB;
	}

	public void run(ImageProcessor inputIP) {
    computeHistograms(inputIP, hMap, sMap, vMap);
    computeCumulativeHistogram(hMap, inputIP.getWidth() * inputIP.getHeight(), "hue");
    computeCumulativeHistogram(sMap, inputIP.getWidth() * inputIP.getHeight(), "sat");
    computeCumulativeHistogram(vMap, inputIP.getWidth() * inputIP.getHeight(), "val");
	}

  private void computeHistograms(
    ImageProcessor processor,
    TreeMap<Float, Double> hMap,
    TreeMap<Float, Double> sMap,
    TreeMap<Float, Double> vMap
  ) {
		int height = processor.getHeight(), width = processor.getWidth();
		Color pixel;
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				pixel = new Color(processor.getPixel(u, v));
				int r = pixel.getRed();
				int g = pixel.getGreen();
        int b = pixel.getBlue();
        
        float[] hsv;
        hsv = Color.RGBtoHSB(r, g, b, null);

        double prevHueCount = hMap.getOrDefault(hsv[0], 0.);
        double prevSatCount = sMap.getOrDefault(hsv[1], 0.);
        double prevValCount = vMap.getOrDefault(hsv[2], 0.);

        hMap.put(hsv[0], prevHueCount + 1);
        sMap.put(hsv[1], prevSatCount + 1);
        vMap.put(hsv[2], prevValCount + 1);
      }
		}
  }
  
  private void computeCumulativeHistogram(TreeMap<Float, Double> resource, int size, String infoLog) {
    for (Map.Entry<Float, Double> e: resource.entrySet()) {
      if (e.getKey() != resource.firstKey()) {
        Map.Entry<Float, Double> prevEntry = resource.lowerEntry(e.getKey());

        if (prevEntry != null) {
          resource.put(e.getKey(), e.getValue() / size + prevEntry.getValue());
        }
      } else {
        resource.put(e.getKey(), e.getValue() / size);
      }

      IJ.log(infoLog + " value " + e.getKey() + " denisty " + e.getValue());
    }
  }
}