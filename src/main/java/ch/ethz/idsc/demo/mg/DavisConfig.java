// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.File;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.util.calibration.GokartToImageUtil;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartLookup;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

/** provides general parameters not specific to SLAM or object detection algorithms */
public class DavisConfig {
  // log file parameters
  /** must match name in LogFileLocations and be an extract of a recording */
  public LogFileLocations logFileLocations = LogFileLocations.DUBI15a;

  public String logFileName() {
    return logFileLocations.name();
  }

  /** maxDuration */
  public final Scalar maxDuration = Quantity.of(15, SI.SECOND);
  // general parameters
  public final Scalar width = RealScalar.of(240);
  public final Scalar height = RealScalar.of(180);
  public final Scalar unitConversion = RealScalar.of(1000);
  /** event filtering
   * 0 == background activity filter, 1 == FAST corner filter */
  public final Scalar filterConfig = RealScalar.of(0);
  /** [us] for background activity filter */
  public Scalar filterConstant = RealScalar.of(1000);
  /** [-] for FAST corner filter */
  public final Scalar margin = RealScalar.of(4);

  /** @return file specified by parameter {@link #logFileName} */
  public File getLogFile() {
    LogFileLocations logFileLocations = LogFileLocations.valueOf(logFileName());
    if (Objects.isNull(logFileLocations))
      throw new RuntimeException("invalid logFileName: " + logFileName());
    return logFileLocations.getFile();
  }

  /** relative to src/main/resources/ */
  public String calibrationFileName() {
    return "/demo/mg/" + logFileName().substring(0, logFileName().length() - 1) + ".csv";
  }

  /** @return new instance of {@link ImageToGokartLookup} derived from parameters in pipelineConfig */
  public ImageToGokartLookup createImageToGokartUtilLookup() {
    return ImageToGokartLookup.fromMatrix(ResourceData.of(calibrationFileName()), unitConversion, width, height);
  }

  /** @return new instance of {@link GokartToImageUtil} derived from parameters in pipelineConfig */
  public GokartToImageUtil createGokartToImageUtil() {
    return GokartToImageUtil.fromMatrix(ResourceData.of(calibrationFileName()), unitConversion);
  }
}
