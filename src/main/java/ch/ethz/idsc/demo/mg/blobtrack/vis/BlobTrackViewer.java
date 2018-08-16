// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.vis;

import java.io.File;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.algo.BlobTrackProvider;
import ch.ethz.idsc.demo.mg.blobtrack.eval.EvaluationFileLocations;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.filter.FilterInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** wrapper for blob tracking algorithm visualization */
public class BlobTrackViewer implements DavisDvsListener {
  private final FilterInterface filterInterface;
  private final BlobTrackProvider blobTrackProvider;
  private final BlobTrackGUI blobTrackGUI;
  private final AccumulatedEventFrame[] eventFrames;
  private final PhysicalBlobFrame[] physicalFrames;
  private final String imagePrefix;
  private final File parentFilePath;
  private final double visualizationInterval;
  private final double savingInterval;
  private double lastImagingTimeStamp;
  private double lastSavingTimeStamp;
  private int imageCount;

  public BlobTrackViewer(BlobTrackConfig blobTrackConfig, BlobTrackProvider blobTrackProvider) {
    filterInterface = new BackgroundActivityFilter(blobTrackConfig.davisConfig);
    this.blobTrackProvider = blobTrackProvider;
    blobTrackGUI = new BlobTrackGUI();
    eventFrames = new AccumulatedEventFrame[3];
    for (int i = 0; i < eventFrames.length; i++)
      eventFrames[i] = new AccumulatedEventFrame(blobTrackConfig);
    physicalFrames = new PhysicalBlobFrame[3];
    for (int i = 0; i < physicalFrames.length; i++)
      physicalFrames[i] = new PhysicalBlobFrame(blobTrackConfig);
    imagePrefix = blobTrackConfig.davisConfig.logFilename();
    parentFilePath = EvaluationFileLocations.images(imagePrefix);
    visualizationInterval = Magnitude.SECOND.toDouble(blobTrackConfig.visualizationInterval);
    savingInterval = Magnitude.SECOND.toDouble(blobTrackConfig.savingInterval);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double timeStamp = davisDvsEvent.time / 1000000.0;
    eventFrames[0].receiveEvent(davisDvsEvent);
    if (filterInterface.filter(davisDvsEvent)) {
      eventFrames[1].receiveEvent(davisDvsEvent);
      eventFrames[2].receiveEvent(davisDvsEvent);
    }
    if (timeStamp - lastImagingTimeStamp > visualizationInterval) {
      blobTrackGUI.setFrames(StaticHelper.constructFrames(eventFrames, physicalFrames, blobTrackProvider, true));
      StaticHelper.resetFrames(eventFrames);
      lastImagingTimeStamp = timeStamp;
    }
    if (timeStamp - lastSavingTimeStamp > savingInterval) {
      imageCount++;
      // VisGeneralUtil.saveFrame(eventFrames[1].getAccumulatedEvents(), parentFilePath, imagePrefix, timeStamp, imageCount);
      lastSavingTimeStamp = timeStamp;
    }
  }
}
