// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import java.io.File;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.util.calibration.GokartToImageUtil;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartLookup;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartUtil;
import ch.ethz.idsc.gokart.lcm.davis.DvsLcmClient;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** provides general parameters not specific to SLAM or object detection algorithms */
public class DvsConfig {
  public final Scalar unitConversion = RealScalar.of(1000);
  public LogFileLocations logFileLocations;
  public DvsLcmClient dvsLcmClient;
  public String channel_DVS;
  public Scalar logFileDuration;
  public Scalar filterConstant;
  public Scalar margin;
  public Scalar width;
  public Scalar height;

  public AbstractFilterHandler createBackgroundActivityFilter() {
    return new BackgroundActivityFilter( //
        Scalars.intValueExact(width), //
        Scalars.intValueExact(height), //
        Magnitude.MICRO_SECOND.toInt(filterConstant));
  }

  public String logFilename() {
    return logFileLocations.name();
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
    return ImageToGokartUtil.fromMatrix(calibration(), unitConversion, Scalars.intValueExact(width));
  }

  /** @return new instance of {@link ImageToGokartLookup} derived from parameters in pipelineConfig */
  public ImageToGokartInterface createImageToGokartInterface() {
    return ImageToGokartLookup.fromMatrix( //
        calibration(), //
        unitConversion, //
        Scalars.intValueExact(width), //
        Scalars.intValueExact(height));
  }

  public Tensor calibration() {
    return logFileLocations.calibration();
  }

  /** @return new instance of {@link GokartToImageUtil} derived from parameters in pipelineConfig */
  public GokartToImageUtil createGokartToImageUtil() {
    return GokartToImageUtil.fromMatrix(calibration(), unitConversion);
  }
}
