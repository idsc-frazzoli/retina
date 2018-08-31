// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import org.bytedeco.javacpp.opencv_core.Mat;

/** provides methods to convert from mat objects */
// TODO put restrictions on input type
public enum SlamOpenCVUtil {
  ;
  /** convert mat object to byteArray
   * 
   * @param mat
   * @return byteArray containing image information */
  public static byte[] matToByteArray(Mat mat) {
    int width = mat.arrayWidth();
    int height = mat.arrayHeight();
    int channels = mat.channels();
    byte[] dataArray = new byte[width * height * channels];
    mat.data().get(dataArray);
    return dataArray;
  }
}
