// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.StringScalar;

public class PipelineConfig {
  // general parameters
  public final Scalar width = RealScalar.of(240);
  public final Scalar height = RealScalar.of(180);
  public final Scalar unitConversion = RealScalar.of(1000);
  // event filtering
  public final Scalar filterConstant = RealScalar.of(500); // [us]
  public final Scalar boarder = RealScalar.of(4);
  // feature tracking
  public final Scalar initNumberOfBlobs = RealScalar.of(24);
  public final Scalar numberRows = RealScalar.of(6);
  public final Scalar initVariance = RealScalar.of(250);
  public final Scalar aUp = RealScalar.of(0.15);
  public final Scalar aDown = RealScalar.of(0.12);
  public final Scalar scoreThreshold = RealScalar.of(4e-4f);
  public final Scalar alphaOne = RealScalar.of(0.9);
  public final Scalar alphaTwo = RealScalar.of(0.998);
  public final Scalar alphaAttr = RealScalar.of(0.002);
  public final Scalar dAttr = RealScalar.of(50);
  public final Scalar dMerge = RealScalar.of(20);
  public final Scalar boundaryDistance = RealScalar.of(1);
  public final Scalar tau = RealScalar.of(8000);
  // feature selection
  public final Scalar upperBoarder = RealScalar.of(100);
  // image to world transform
  public final Scalar calibrationFileName = StringScalar.of("/demo/mg/dubi0008.csv"); // path in main/resources/..
  // tracking evaluation
  public final Scalar handLabelFileName = StringScalar.of("Dubi9e_labeledFeatures.csv"); // path set in HandLabelFileLocations
  public final Scalar evaluatePerformance = RealScalar.of(0); // used as boolean: 0 == false, else == true
  // image saving
  public final Scalar saveImages = RealScalar.of(0); // used as boolean: 0 == false, else == true
  public final Scalar imagePrefix = StringScalar.of("dubi10d"); // image name structure: "%s_%04d_%d.png", imagePrefix, imageCount, timeStamp
  public final Scalar savingInterval = RealScalar.of(33); // [ms]
  // log file configuration
  public final Scalar maxDuration = RealScalar.of(10000); // [ms]
  public final Scalar visualizationInterval = RealScalar.of(33); // [ms]

  PipelineConfig() {
  }

  // for testing
  public static void main(String[] args) throws IOException {
    PipelineConfig test = new PipelineConfig();
    TensorProperties.manifest(UserHome.file("config2.properties"), test);
  }
}
