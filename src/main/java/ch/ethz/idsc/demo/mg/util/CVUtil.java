// code by mg
package ch.ethz.idsc.demo.mg.util;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.slam.MapProvider;

/** provides methods to convert to and from mat objects */
// TODO put restrictions on input type
public enum CVUtil {
  ;
  /** convert mat object to byteArray
   * 
   * @param mat
   * @return */
  public static byte[] matToByteArray(Mat mat) {
    int width = mat.arrayWidth();
    int height = mat.arrayHeight();
    int channels = mat.channels();
    byte[] dataArray = new byte[width * height * channels];
    mat.data().get(dataArray);
    return dataArray;
  }

  /** convert binary image to a mat object
   * 
   * @param mapProvider
   * @return */
  public static Mat mapProviderToMat(MapProvider mapProvider) {
    double[] mapArray = mapProvider.getMapArray();
    byte[] byteArray = new byte[mapArray.length];
    Mat mat = new Mat(mapProvider.getWidth(), mapProvider.getHeight(), opencv_core.CV_8UC1);
    for (int i = 0; i < byteArray.length; i++) {
      if (mapArray[i] == 0) {
        byteArray[i] = 0;
      } else {
        byteArray[i] = 1;
      }
    }
    mat.data().put(byteArray);
    return mat;
  }
}
