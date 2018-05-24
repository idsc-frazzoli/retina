// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.demo.mg.util.TransformUtil;
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
  public Scalar aUp = RealScalar.of(0.12);
  public Scalar aDown = RealScalar.of(0.08);
  public Scalar scoreThreshold = RealScalar.of(4e-4f);
  public Scalar alphaOne = RealScalar.of(0.9);
  public Scalar alphaTwo = RealScalar.of(0.998);
  public Scalar alphaAttr = RealScalar.of(0.002);
  public Scalar dAttr = RealScalar.of(50);
  public Scalar dMerge = RealScalar.of(20);
  public Scalar boundaryDistance = RealScalar.of(1);
  public Scalar tau = RealScalar.of(40000); // [us]
  // feature selection
  public Scalar upperBoarder = RealScalar.of(height.number());
  // TransformUtil
  public final String calibrationFileName = "/demo/mg/" + logFileName.substring(0, logFileName.length() - 1) + ".csv"; // relative to main/resources/
  public final Boolean calibrationAvailable = !(ResourceData.of(calibrationFileName.toString()) == null);
  // image saving
  public final Scalar saveImagesConfig = RealScalar.of(0); // 0: no saving, 1: saving in testing, 2: saving for handlabeling
  public final Scalar savingInterval = RealScalar.of(50); // [ms]
  // handlabeling tool
  public final String handLabelFileName = logFileName + "_labeledFeatures"; // file must be present to evaluate performance
  public final Scalar initAxis = RealScalar.of(400);
  // tracking collector
  public final Boolean collectEstimatedFeatures = true;
  public final Scalar iterationLength = RealScalar.of(22);
  public String estimatedLabelFileName = logFileName + "_estimatedFeatures";
  // performance evaluation
  public Boolean saveEvaluationFrame = false;
  public final Scalar maxDistance = width.add(height); // [pixel] upper bound for distance between features
  public final Scalar truePositiveThreshold = RealScalar.of(30); // [pixel]
  // visualization
  public Boolean visualizePipeline = false;
  public final Boolean rotateFrame = false; // for early recordings the DAVIS was mounted upside down
  public final Scalar visualizationInterval = RealScalar.of(50); // [ms]
  // physical world visualization
  public final Scalar frameWidth = RealScalar.of(400); // [pixel] for physical frame
  public final Scalar frameHeight = RealScalar.of(450); // [pixel] for physical frame
  public final Scalar scaleFactor = RealScalar.of(35); // [pixel/m] to map physical coordinates onto image coordinates
  public final Scalar originPosX = frameWidth.divide(RealScalar.of(2)); // [pixel]
  public final Scalar originPosY = RealScalar.of(400); // [pixel]
  public final Scalar objectSize = RealScalar.of(30); // [pixel]
  public final Scalar gokartSize = RealScalar.of(35); // [pixel]

  /***************************************************/
  /** @return file specified by parameter {@link #logFileName} */
  public File getLogFile() {
    LogFileLocations logFileLocations = LogFileLocations.valueOf(logFileName);
    if (Objects.isNull(logFileLocations))
      throw new RuntimeException("invalid logFileName: " + logFileName);
    return logFileLocations.getFile();
  }

  /** @return new instance of {@link TransformUtil} derived from parameters in pipeline config */
  public TransformUtil createTransformUtil() {
    return TransformUtil.fromMatrix(ResourceData.of(calibrationFileName), unitConversion);
  }
}
