import ij.ImagePlus;
import ij.IJ;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;
import ij.plugin.filter.PlugInFilter;
import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

public class HSVSpecification implements PlugInFilter {
	ImagePlus inputImage;
	ImagePlus referenceImage;
	ImageProcessor refImageProcessor;
  TreeMap<Float, Double> hMap = new TreeMap<Float, Double>();
  TreeMap<Float, Double> sMap = new TreeMap<Float, Double>();
  TreeMap<Float, Double> vMap = new TreeMap<Float, Double>();

  TreeMap<Float, Double> hRefMap = new TreeMap<Float, Double>();
  TreeMap<Float, Double> sRefMap = new TreeMap<Float, Double>();
  TreeMap<Float, Double> vRefMap = new TreeMap<Float, Double>();

	public int setup(String args, ImagePlus im) {
		inputImage = im;
		return DOES_RGB;
	}

	public void run(ImageProcessor inputIP) {
		getRefImageDialog();
    computeHistograms(inputIP, hMap, sMap, vMap);
    computeHistograms(refImageProcessor, hRefMap, sRefMap, vRefMap);
    computeCumulativeHistogram(hMap, inputIP.getWidth() * inputIP.getHeight());
    computeCumulativeHistogram(sMap, inputIP.getWidth() * inputIP.getHeight());
    computeCumulativeHistogram(vMap, inputIP.getWidth() * inputIP.getHeight());
    computeCumulativeHistogram(hRefMap, refImageProcessor.getWidth() * refImageProcessor.getHeight());
    computeCumulativeHistogram(sRefMap, refImageProcessor.getWidth() * refImageProcessor.getHeight());
    computeCumulativeHistogram(vRefMap, refImageProcessor.getWidth() * refImageProcessor.getHeight());
    specification(inputIP, refImageProcessor);
	}

	private void getRefImageDialog() {
		referenceImage = IJ.openImage();
		refImageProcessor = referenceImage.getProcessor();
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
  
  private void computeCumulativeHistogram(TreeMap<Float, Double> resource, int size) {
    for (Map.Entry<Float, Double> e: resource.entrySet()) {
      if (e.getKey() != resource.firstKey()) {
        Map.Entry<Float, Double> prevEntry = resource.lowerEntry(e.getKey());

        if (prevEntry != null) {
          resource.put(e.getKey(), e.getValue() / size + prevEntry.getValue());
        }
      } else {
        resource.put(e.getKey(), e.getValue() / size);
      }

      IJ.log("" + e.getValue());
    }
  }

	private void specification(ImageProcessor processor, ImageProcessor refProcessor) {
    int height = processor.getHeight(), width = processor.getWidth();
    int refHeight = refProcessor.getHeight(), refWidth = refProcessor.getWidth();
		ImageProcessor outputIP = new ColorProcessor(width, height);
		Color pixel;

		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				pixel = new Color(processor.getPixel(u, v));
				int r = pixel.getRed();
				int g = pixel.getGreen();
        int b = pixel.getBlue();

        float[] hsv;
        hsv = Color.RGBtoHSB(r, g, b, null);

        float newH = getClosestValueBasedOnCount(hMap.getOrDefault(hsv[0], 0.), hRefMap);
        float newS = getClosestValueBasedOnCount(sMap.getOrDefault(hsv[1], 0.), sRefMap);
        float newV = getClosestValueBasedOnCount(vMap.getOrDefault(hsv[2], 0.), vRefMap);

        int newRGB = Color.HSBtoRGB(newH, newS, newV);

				outputIP.putPixel(u, v, newRGB);
			}
		}

		(new ImagePlus("balanced_all_" + inputImage.getShortTitle(), outputIP)).show();
  }
  
  private Float getClosestValueBasedOnCount(double value, TreeMap<Float, Double> resource) {
    float resultKey = resource.firstKey();

    for (float key: resource.descendingKeySet()) {
      if (resource.get(key) <= value) {
        return key;
      } else {
        resultKey = key;
      }
    }

    return resultKey;
  }
}