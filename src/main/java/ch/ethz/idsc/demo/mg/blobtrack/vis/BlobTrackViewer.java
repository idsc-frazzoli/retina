// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.vis;

import java.io.File;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.algo.BlobTrackProvider;
import ch.ethz.idsc.demo.mg.blobtrack.eval.MgEvaluationFolders;
import ch.ethz.idsc.demo.mg.filter.DavisDvsEventFilter;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** wrapper for blob tracking algorithm visualization */
public class BlobTrackViewer implements DavisDvsListener {
  private final DavisDvsEventFilter davisDvsEventFilter;
  private final BlobTrackProvider blobTrackProvider;
  private final BlobTrackGUI blobTrackGUI;
  private final AccumulatedEventFrame[] eventFrames;
  private final PhysicalBlobFrame[] physicalFrames;
  private final String logFilename;
  private final File parentFilePath;
  private final double visualizationInterval;
  private final double savingInterval;
  private final boolean saveFrame;
  // ---
  private double lastImagingTimeStamp;
  private double lastSavingTimeStamp;
  private int imageCount;
  private boolean isInitialized;

  public BlobTrackViewer(BlobTrackConfig blobTrackConfig, BlobTrackProvider blobTrackProvider) {
    davisDvsEventFilter = blobTrackConfig.davisConfig.createBackgroundActivityFilter();
    this.blobTrackProvider = blobTrackProvider;
    blobTrackGUI = new BlobTrackGUI();
    eventFrames = new AccumulatedEventFrame[3];
    for (int i = 0; i < eventFrames.length; i++)
      eventFrames[i] = new AccumulatedEventFrame(blobTrackConfig);
    physicalFrames = new PhysicalBlobFrame[3];
    for (int i = 0; i < physicalFrames.length; i++)
      physicalFrames[i] = new PhysicalBlobFrame(blobTrackConfig);
    saveFrame = blobTrackConfig.saveFrame;
    logFilename = blobTrackConfig.davisConfig.logFilename();
    parentFilePath = MgEvaluationFolders.HANDLABEL.subfolder(logFilename);
    visualizationInterval = Magnitude.SECOND.toDouble(blobTrackConfig.visualizationInterval);
    savingInterval = Magnitude.SECOND.toDouble(blobTrackConfig.savingInterval);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double timeStamp = davisDvsEvent.time / 1000000.0;
    if (!isInitialized) {
      lastImagingTimeStamp = timeStamp;
      lastSavingTimeStamp = timeStamp;
      isInitialized = true;
    }
    eventFrames[0].receiveEvent(davisDvsEvent);
    if (davisDvsEventFilter.filter(davisDvsEvent)) {
      eventFrames[1].receiveEvent(davisDvsEvent);
      eventFrames[2].receiveEvent(davisDvsEvent);
    }
    if (timeStamp - lastImagingTimeStamp > visualizationInterval) {
      blobTrackGUI.setFrames(StaticHelper.constructFrames(eventFrames, physicalFrames, blobTrackProvider, true));
      StaticHelper.resetFrames(eventFrames);
      lastImagingTimeStamp = timeStamp;
    }
    if (saveFrame)
      if (timeStamp - lastSavingTimeStamp > savingInterval) {
        ++imageCount;
        VisGeneralUtil.saveFrame(eventFrames[0].getAccumulatedEvents(), parentFilePath, logFilename, timeStamp, imageCount);
        lastSavingTimeStamp = timeStamp;
      }
  }
}
