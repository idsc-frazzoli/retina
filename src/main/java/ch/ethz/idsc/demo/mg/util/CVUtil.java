// code by mg
package ch.ethz.idsc.demo.mg.util;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.slam.MapProvider;

// provides methods to convert to and from mat objects
// TODO maybe switch to static void methods
public class CVUtil {
  // some colors for BufferedImage.TYPE_BYTE_INDEXED
  private static final byte CLEAR_BYTE = (byte) 255;
  private static final byte ORANGE = (byte) -52;
  private static final byte GREEN = (byte) 30;

  // convert mat object to byteArray
  public static byte[] matToByteArray(Mat mat) {
    int width = mat.arrayWidth();
    int height = mat.arrayHeight();
    int channels = mat.channels();
    byte[] dataArray = new byte[width * height * channels];
    mat.data().get(dataArray);
    return dataArray;
  }

  // convert binary image to a mat object
  public static Mat mapProviderToMat(MapProvider mapProvider) {
    double[] mapArray = mapProvider.getMapArray();
    byte[] byteArray = new byte[mapArray.length];
    Mat mat = new Mat(mapProvider.getWidth(), mapProvider.getHeight(), opencv_core.CV_8U);
    for (int i = 0; i < byteArray.length; i++) {
      if (mapArray[i] == 0) {
        byteArray[i] = CLEAR_BYTE;
      } else {
        byteArray[i] = GREEN;
      }
    }
    mat.data().put(byteArray);
    return mat;
  }
}
