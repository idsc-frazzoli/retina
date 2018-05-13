// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** defines all parameters of the control pipeline and optionally saves them to a .properties file */
public class PipelineConfig {
  // log file parameters
  public String logFileName = "DUBI12a"; // must match name in LogFileLocations
  public final Scalar maxDuration = RealScalar.of(5000); // [ms]
  // general parameters
  public final Scalar width = RealScalar.of(240);
  public final Scalar height = RealScalar.of(180);
  public final Scalar unitConversion = RealScalar.of(1000);
  /** filterConfig can currently be 0 or 1 */
  public final Scalar filterConfig = RealScalar.of(0);
  public final Scalar boarder = RealScalar.of(4);
  public Scalar filterConstant = RealScalar.of(500); // [us]
  // feature tracking
  public Scalar initNumberOfBlobs = RealScalar.of(24);
  public Scalar numberRows = RealScalar.of(6);
  public Scalar initVariance = RealScalar.of(250);
  public Scalar aUp = RealScalar.of(0.15);
  public Scalar aDown = RealScalar.of(0.12);
  public Scalar scoreThreshold = RealScalar.of(4e-4f);
  public Scalar alphaOne = RealScalar.of(0.9);
  public Scalar alphaTwo = RealScalar.of(0.998);
  public Scalar alphaAttr = RealScalar.of(0.002);
  public Scalar dAttr = RealScalar.of(50);
  public Scalar dMerge = RealScalar.of(20);
  public Scalar boundaryDistance = RealScalar.of(1);
  public Scalar tau = RealScalar.of(8000);
  // feature selection
  public Scalar upperBoarder = RealScalar.of(100);
  // image to world transform
  public String calibrationFileName = "/demo/mg/dubi0008.csv"; // path in main/resources/..
  /** image saving
   * used as boolean: 0 == false, else == true
   * access via function isImageSaved() */
  public final Scalar saveImages = RealScalar.of(0);
  public final Scalar savingInterval = RealScalar.of(1000); // [ms]
  public final String handLabelFileName = logFileName + "_labeledFeatures.csv";
  /** used as boolean: 0 == false, else == true
   * access via function isPerformanceEvaluated() */
  public final Scalar evaluatePerformance = RealScalar.of(1);
  /** visualization used as boolean: 0 == false, else == true
   * access via function isVisualized() */
  public Scalar visualizePipeline = RealScalar.of(1);
  public Scalar visualizationInterval = RealScalar.of(33); // [ms]
  // handlabeling tool
  public final String comma_delimiter = ",";
  public final String new_line = "\n";
  public final Scalar initXAxis = RealScalar.of(400);
  // test if Boolean now tracked
  public Boolean testBool = false;

  /***************************************************/
  public File getLogFile() {
    LogFileLocations logFileLocations = LogFileLocations.valueOf(logFileName);
    if (Objects.isNull(logFileLocations))
      throw new RuntimeException("invalid logFileName: " + logFileName);
    return logFileLocations.getFile();
  }

  public boolean isImageSaved() {
    return Scalars.nonZero(saveImages);
  }

  public boolean isVisualized() {
    return Scalars.nonZero(visualizePipeline);
  }

  public boolean isPerformanceEvaluated() {
    return Scalars.nonZero(evaluatePerformance);
  }

  // for testing
  public static void main(String[] args) throws IOException {
    PipelineConfig test = new PipelineConfig();
    TensorProperties.manifest(UserHome.file("config2.properties"), test);
    // private final PipelineConfig pipelineConfig = TensorProperties.retrieve(UserHome.file("config.properties"), new PipelineConfig());
  }
}
