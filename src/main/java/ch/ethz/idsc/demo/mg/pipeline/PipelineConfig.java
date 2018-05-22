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
import ch.ethz.idsc.tensor.io.ResourceData;

/** defines all parameters of the control pipeline and optionally saves them to a .properties file */
public class PipelineConfig {
  // log file parameters
  public String logFileName = "DUBI15a"; // must match name in LogFileLocations and be an extract of a recording
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
  public Scalar upperBoarder = RealScalar.of(height.number());
  // TransformUtil
  public final String calibrationFileName = "/demo/mg/" + logFileName.substring(0, logFileName.length() - 1) + ".csv"; // relative to main/resources/
  public final Boolean calibrationAvailable = !(ResourceData.of(calibrationFileName.toString()) == null);
  // image saving
  public final Boolean saveImages = false;
  public final Scalar savingInterval = RealScalar.of(300); // [ms]
  // handlabeling tool
  public final String handLabelFileName = logFileName + "_labeledFeatures"; // file must be present to evaluate performance
  public final Scalar initAxis = RealScalar.of(400);
  // tracking collector
  public final Boolean collectEstimatedFeatures = true;
  public Boolean saveEvaluationFrame = true;
  public final Scalar iterationLength = RealScalar.of(1);
  public String estimatedLabelFileName = logFileName + "_estimatedFeatures"; // TODO will be varied for evaluation of different param
  // performance evaluation
  public final Scalar maxDistance = width.add(height); // [pixel] upper bound for distance between features
  public final Scalar truePositiveThreshold = RealScalar.of(30); // [pixel]
  // visualization
  public Boolean visualizePipeline = true;
  public final Boolean rotateFrame = false; // for early recordings the DAVIS was mounted upside down
  public final Scalar visualizationInterval = RealScalar.of(100); // [ms]
  public final Scalar frameWidth = RealScalar.of(400); // [pixel] for physical frame
  public final Scalar frameHeight = RealScalar.of(400); // [pixel] for physical frame

  /***************************************************/
  public File getLogFile() {
    LogFileLocations logFileLocations = LogFileLocations.valueOf(logFileName);
    if (Objects.isNull(logFileLocations))
      throw new RuntimeException("invalid logFileName: " + logFileName);
    return logFileLocations.getFile();
  }

  // for testing
  public static void main(String[] args) throws IOException {
    PipelineConfig test = new PipelineConfig();
    TensorProperties.manifest(UserHome.file("config2.properties"), test);
    // private final PipelineConfig pipelineConfig = TensorProperties.retrieve(UserHome.file("config.properties"), new PipelineConfig());
  }
}
