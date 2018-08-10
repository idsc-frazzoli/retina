// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.demo.mg.DavisConfig;
import ch.ethz.idsc.demo.mg.util.calibration.GokartToImageLookup;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartUtil;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

/** defines all parameters of the control pipeline and optionally saves them to a .properties file */
// TODO probably split into file for pipeline and file for slam
public class PipelineConfig {
  // general parameters
  public final DavisConfig davisConfig = new DavisConfig();
  // visualization and image saving
  public final Scalar visualizationInterval = RealScalar.of(100); // [ms]
  public final Scalar savingInterval = RealScalar.of(200); // [ms]
  //
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
  // TransformUtil
  public final String calibrationFileName = "/demo/mg/" + davisConfig.logFileName().substring(0, davisConfig.logFileName().length() - 1) + ".csv"; // relative
                                                                                                                                                   // to
  // main/resources/
  public final Boolean calibrationAvailable = !(ResourceData.of(calibrationFileName.toString()) == null);
  // image saving
  public final Scalar saveImagesConfig = RealScalar.of(0); // 0: no saving, 1: saving in testing, 2: saving for handlabeling
  // hand-labeling tool
  public final String handLabelFileName = davisConfig.logFileName() + "_labeledFeatures"; // file must be present to collect tracking estimates
  public final Scalar initAxis = RealScalar.of(400);
  public final Scalar positionDifference = RealScalar.of(2); // [pixel]
  public final Scalar sizeMultiplier = RealScalar.of(20); // [covariance of ImageBlob]
  public final Scalar defaultBlobID = RealScalar.of(0);
  // tracking collector
  public final Boolean collectEstimatedFeatures = false;
  public final Scalar iterationLength = RealScalar.of(10);
  public String estimatedLabelFileName = davisConfig.logFileName() + "_estimatedFeatures";
  // performance evaluation
  public final Boolean saveEvaluationFrame = false;
  public final String evaluationResultFileName = "evaluationResults"; // for csv file containing multirun results
  public final Scalar maxDistance = davisConfig.width.add(davisConfig.height); // [pixel] upper bound for distance between features
  public final Scalar truePositiveThreshold = RealScalar.of(30); // [pixel]
  // visualization
  public Boolean visualizePipeline = false;
  public final Boolean rotateFrame = false; // for early recordings the DAVIS was mounted upside down
  // physical world visualization
  public final Scalar frameWidth = RealScalar.of(400); // [pixel] for physical frame
  public final Scalar frameHeight = RealScalar.of(450); // [pixel] for physical frame
  public final Scalar scaleFactor = RealScalar.of(35); // [pixel/m] to map physical coordinates onto image coordinates
  public final Scalar originPosX = frameWidth.divide(RealScalar.of(2)); // [pixel]
  public final Scalar originPosY = RealScalar.of(400); // [pixel]
  public final Scalar objectSize = RealScalar.of(30); // [pixel]
  public final Scalar gokartSize = RealScalar.of(35); // [pixel]
  //
  /***************************************************/
  // SLAM algorithm parameters
  public final Boolean localizationMode = false; // in localization mode, a previously saved map is used
  public final Boolean lidarMappingMode = true; // pose provided by lidar instead of particle filter
  public final Boolean saveSlamMap = false;
  public final Scalar alpha = RealScalar.of(0.99); // [-] for update of state estimate
  public final Scalar numberOfParticles = RealScalar.of(40);
  public final Scalar relevantParticles = RealScalar.of(4); // only these particles are used for occurrence map update
  public final Scalar lookAheadDistance = RealScalar.of(9); // [m] events further away are neglected
  public final Scalar normalizationUpdateRate = RealScalar.of(0.025); // [s]
  public final Scalar linVelAvg = RealScalar.of(4); // [m] for initial particle distribution
  public final Scalar linVelStd = RealScalar.of(1); // [m/s] for initial particle distribution
  public final Scalar angVelStd = RealScalar.of(0.2); // [rad/s] for initial particle distribution
  // SLAM map parameters
  public final Scalar cellDim = RealScalar.of(0.06); // [m] single cell dimension
  public final Scalar dimX = RealScalar.of(30); // [m] x 'width' of map
  public final Scalar dimY = RealScalar.of(30); // [m] y 'height' of map
  public final Tensor corner = Tensors.vector(35, 35); // [m] coordinates of lower left point in map
  // SLAM visualization parameters
  public final Boolean saveSlamFrame = false;
  public final Scalar kartSize = RealScalar.of(1.5); // [m]

  /** @return new instance of {@link ImageToGokartUtil} derived from parameters in pipelineConfig */
  public ImageToGokartUtil createImageToGokartUtil() {
    return ImageToGokartUtil.fromMatrix(ResourceData.of(calibrationFileName), davisConfig.unitConversion);
  }

  /** @return new instance of {@link GokartToImageLookup} derived form parameters in pipelineConfig */
  public GokartToImageLookup createGokartToImageLookup() {
    return GokartToImageLookup.fromMatrix(ResourceData.of(calibrationFileName), davisConfig.unitConversion, cellDim, lookAheadDistance);
  }

  /** @return new instance of {@link ImageBlobSelector} derived from parameters in pipelineConfig */
  public ImageBlobSelector createImageBlobSelector() {
    return new ImageBlobSelector(upperBoarder);
  }
}
