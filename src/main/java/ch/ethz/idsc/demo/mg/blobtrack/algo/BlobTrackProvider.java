// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;
import ch.ethz.idsc.demo.mg.blobtrack.eval.TrackingCollector;
import ch.ethz.idsc.demo.mg.blobtrack.vis.AccumulatedEventFrame;
import ch.ethz.idsc.demo.mg.blobtrack.vis.PhysicalBlobFrame;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.filter.FilterInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** implements the object detection and tracking algorithm as described in TODO MG find reference */
public class BlobTrackProvider implements DavisDvsListener {
  // pipeline modules
  private final FilterInterface filterInterface;
  private final BlobTracking blobTracking;
  private final ImageBlobSelector blobSelector;
  private BlobTransform transformer = null; // default initialization if unused
  private TrackingCollector trackingCollector = null;
  // visualization
  private final boolean visualizePipeline;
  private AccumulatedEventFrame[] eventFrames = null;
  private PhysicalBlobFrame[] physicalFrames = null;
  // pipeline configuration
  private final boolean calibrationAvailable;
  private final boolean collectEstimatedFeatures;

  public BlobTrackProvider(BlobTrackConfig pipelineConfig) {
    visualizePipeline = pipelineConfig.visualizePipeline;
    calibrationAvailable = pipelineConfig.calibrationAvailable;
    collectEstimatedFeatures = pipelineConfig.collectEstimatedFeatures;
    // initialize pipeline modules
    filterInterface = new BackgroundActivityFilter(pipelineConfig.davisConfig);
    blobTracking = new BlobTracking(pipelineConfig);
    blobSelector = pipelineConfig.createImageBlobSelector();
    // calibration required for transformation to physical space
    if (calibrationAvailable)
      transformer = new BlobTransform(pipelineConfig);
    // optional evaluation
    if (collectEstimatedFeatures)
      trackingCollector = new TrackingCollector(pipelineConfig);
    // optional visualization
    if (visualizePipeline) {
      eventFrames = new AccumulatedEventFrame[3];
      for (int i = 0; i < eventFrames.length; i++)
        eventFrames[i] = new AccumulatedEventFrame(pipelineConfig);
      if (calibrationAvailable) {
        physicalFrames = new PhysicalBlobFrame[3];
        for (int i = 0; i < physicalFrames.length; i++)
          physicalFrames[i] = new PhysicalBlobFrame(pipelineConfig);
      }
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    // visualization of raw events
    if (visualizePipeline) {
      eventFrames[0].receiveEvent(davisDvsEvent);
    }
    // evaluation tool
    if (collectEstimatedFeatures && trackingCollector.isGroundTruthAvailable(davisDvsEvent)) {
      trackingCollector.setEstimatedFeatures(blobSelector.getSelectedBlobs());
    }
    // filtering returns a boolean
    if (filterInterface.filter(davisDvsEvent)) {
      // control pipeline
      blobTracking.receiveEvent(davisDvsEvent);
      blobSelector.receiveActiveBlobs(blobTracking.getActiveBlobs());
      if (calibrationAvailable) {
        transformer.transformSelectedBlobs(blobSelector.getSelectedBlobs());
      }
      // visualization
      if (visualizePipeline) {
        eventFrames[1].receiveEvent(davisDvsEvent);
        eventFrames[2].receiveEvent(davisDvsEvent);
      }
    }
  }

  public List<PhysicalBlob> getPhysicalblobs() {
    return transformer.getPhysicalBlobs();
  }

  public AccumulatedEventFrame[] getEventFrames() {
    return eventFrames;
  }

  public PhysicalBlobFrame[] getPhysicalFrames() {
    return physicalFrames;
  }

  public BlobTracking getBlobTracking() {
    return blobTracking;
  }

  public ImageBlobSelector getBlobSelector() {
    return blobSelector;
  }

  public FilterInterface getFilterInterface() {
    return filterInterface;
  }
}
