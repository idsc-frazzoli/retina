// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.File;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.util.calibration.GokartToImageUtil;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartLookup;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartUtil;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.io.DvsLcmClient;
import ch.ethz.idsc.retina.dev.davis.io.SeyeAeDvsLcmClient;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

/** provides general parameters not specific to SLAM or object detection algorithms */
public class DavisConfig {
  // log file parameters
  /** must match name in LogFileLocations and be an extract of a recording */
  public LogFileLocations logFileLocations = LogFileLocations.DUBISiliconEyeC;
  /** which camera is used */
  public String cameraType = "siliconEye";
  /** maxDuration */
  public final Scalar logFileDuration = Quantity.of(10, SI.SECOND);
  // general parameters
  /** conversion factor from [mm] to [m] */
  public final Scalar unitConversion = RealScalar.of(1000);
  /** time threshold for background activity filter
   * the report 20180225_davis240c_event_distribution concludes:
   * 1) a 4[s] recording of rapid turning contains 975 intervals
   * of duration at least 1[ms] during which no events occur
   * 2) for a bin of width 500[us] chances are p=0.30283 that the bin is empty
   * 3) for a bin size of 2397[us] there is a 99% chance that it’s non-empty */
  public Scalar filterConstant = Quantity.of(500, NonSI.MICRO_SECOND);
  /** [-] for FAST corner filter */
  public final Scalar margin = RealScalar.of(4);

  public AbstractFilterHandler createBackgroundActivityFilter() {
    return new BackgroundActivityFilter( //
        Scalars.intValueExact(width()), //
        Scalars.intValueExact(height()), //
        Magnitude.MICRO_SECOND.toInt(filterConstant));
  }

  public Scalar width() {
    CameraType cameraType2 = CameraType.valueOf(cameraType);
    switch (cameraType2) {
    case davis:
      return RealScalar.of(240);
    case siliconEye:
      return RealScalar.of(320);
    default:
      throw new RuntimeException();
    }
  }

  public Scalar height() {
    CameraType cameraType2 = CameraType.valueOf(cameraType);
    switch (cameraType2) {
    case davis:
      return RealScalar.of(180);
    case siliconEye:
      return RealScalar.of(264);
    default:
      throw new RuntimeException();
    }
  }

  public DvsLcmClient getLcmClient() {
    CameraType cameraType2 = CameraType.valueOf(cameraType);
    switch (cameraType2) {
    case davis:
      return new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
    case siliconEye:
      return new SeyeAeDvsLcmClient(GokartLcmChannel.SEYE_OVERVIEW);
    default:
      throw new RuntimeException();
    }
  }

  public String getChannel_DVS() {
    CameraType cameraType2 = CameraType.valueOf(cameraType);
    switch (cameraType2) {
    case davis:
      return "davis240c.overview.dvs";
    case siliconEye:
      return "seye.overview.aedvs";
    default:
      throw new RuntimeException();
    }
  }

  public String logFilename() {
    return logFileLocations.name();
  }

  private Tensor calibration() {
    CameraType cameraType2 = CameraType.valueOf(cameraType);
    switch (cameraType2) {
    case davis:
      return logFileLocations.calibration();
    case siliconEye:
      // for SiliconEye, we currently only have one calibration file
      return ResourceData.of("/demo/mg/DUBISiliconEye.csv");
    default:
      throw new RuntimeException();
    }
  }

  /** @return file specified by parameter {@link #logFileName} */
  public File getLogFile() {
    LogFileLocations logFileLocations = LogFileLocations.valueOf(logFilename());
    if (Objects.isNull(logFileLocations))
      throw new RuntimeException("invalid logFileName: " + logFilename());
    return logFileLocations.getFile();
  }

  /** @return new instance of {@link ImageToGokartUtil} derived from parameters in pipelineConfig */
  public ImageToGokartUtil createImageToGokartUtil() {
    return ImageToGokartUtil.fromMatrix(calibration(), unitConversion, Scalars.intValueExact(width()));
  }

  /** @return new instance of {@link ImageToGokartLookup} derived from parameters in pipelineConfig */
  public ImageToGokartInterface createImageToGokartInterface() {
    return ImageToGokartLookup.fromMatrix( //
        calibration(), //
        unitConversion, //
        Scalars.intValueExact(width()), //
        Scalars.intValueExact(height()));
  }

  /** @return new instance of {@link GokartToImageUtil} derived from parameters in pipelineConfig */
  public GokartToImageUtil createGokartToImageUtil() {
    return GokartToImageUtil.fromMatrix(calibration(), unitConversion);
  }
}
