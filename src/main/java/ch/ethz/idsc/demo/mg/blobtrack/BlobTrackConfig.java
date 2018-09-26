// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.DavisConfig;
import ch.ethz.idsc.demo.mg.blobtrack.algo.ImageBlobSelector;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

/** defines all parameters of the control pipeline and optionally saves them to a .properties file */
public class BlobTrackConfig {
  // general parameters
  public final DavisConfig davisConfig = new DavisConfig();
  // visualization and image saving
  public final Scalar visualizationInterval = Quantity.of(0.1, SI.SECOND);
  public final Scalar savingInterval = Quantity.of(0.3, SI.SECOND);
  /***************************************************/
  // feature tracking algorithm parameters
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
  public Scalar tau = RealScalar.of(20000); // [us]
  // feature selection
  public Scalar upperBoarder = RealScalar.of(davisConfig.height.number()); // with this number, all features are selected
  // image saving
  public final Scalar saveImagesConfig = RealScalar.of(0); // 0: no saving, 1: saving in testing, 2: saving for handlabeling
  // hand-labeling tool
  public final String handLabelFileName = davisConfig.logFilename() + "_labeledFeatures"; // file must be present to collect tracking estimates
  public final Scalar initAxis = RealScalar.of(400);
  public final Scalar positionDifference = RealScalar.of(2); // [pixel]
  public final Scalar sizeMultiplier = RealScalar.of(20); // [covariance of ImageBlob]
  public final Scalar defaultBlobID = RealScalar.of(0);
  // tracking collector
  public final Boolean collectEstimatedFeatures = false;
  public final Scalar iterationLength = RealScalar.of(10);
  public String estimatedLabelFileName = davisConfig.logFilename() + "_estimatedFeatures";
  // performance evaluation
  public final Boolean saveEvaluationFrame = false;
  public final String evaluationResultFileName = "evaluationResults"; // for csv file containing multirun results
  public final Scalar maxDistance = davisConfig.width.add(davisConfig.height); // [pixel] upper bound for distance between features
  public final Scalar truePositiveThreshold = RealScalar.of(30); // [pixel]
  // visualization
  public Boolean visualizePipeline = true;
  public Boolean saveFrame = false;
  public final Boolean rotateFrame = false; // for early recordings the DAVIS was mounted upside down
  // physical world visualization
  public final Scalar frameWidth = RealScalar.of(400); // [pixel] for physical frame
  public final Scalar frameHeight = RealScalar.of(450); // [pixel] for physical frame
  public final Scalar scaleFactor = RealScalar.of(35); // [pixel/m] to map physical coordinates onto image coordinates
  public final Scalar originPosX = frameWidth.divide(RealScalar.of(2)); // [pixel]
  public final Scalar originPosY = RealScalar.of(400); // [pixel]
  public final Scalar objectSize = RealScalar.of(30); // [pixel]
  public final Scalar gokartSize = RealScalar.of(35); // [pixel]

  /** @return new instance of {@link ImageBlobSelector} derived from parameters in pipelineConfig */
  public ImageBlobSelector createImageBlobSelector() {
    return new ImageBlobSelector(upperBoarder);
  }

  public boolean isCalibrationAvailable() {
    String logFilename = davisConfig.logFilename();
    String calibrationFileName = "/demo/mg/" + logFilename.substring(0, logFilename.length() - 1) + ".csv";
    return Objects.nonNull(ResourceData.of(calibrationFileName));
  }
}
