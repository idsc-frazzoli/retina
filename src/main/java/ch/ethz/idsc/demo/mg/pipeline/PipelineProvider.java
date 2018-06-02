// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.List;

import ch.ethz.idsc.demo.mg.eval.TrackingCollector;
import ch.ethz.idsc.demo.mg.gui.AccumulatedEventFrame;
import ch.ethz.idsc.demo.mg.gui.PhysicalBlobFrame;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// implements the control pipeline
public class PipelineProvider implements DavisDvsListener {
  // pipeline modules
  private final EventFiltering eventFiltering;
  final BlobTracking tracking;
  final ImageBlobSelector blobSelector;
  private BlobTransform transformer = null; // default initialization if unused
  private TrackingCollector collector = null;
  // visualization
  private boolean visualizePipeline;
  AccumulatedEventFrame[] eventFrames = null;
  PhysicalBlobFrame[] physicalFrames = null;
  // pipeline configuration
  private boolean calibrationAvailable;
  private boolean collectEstimatedFeatures;
  // log summary
  float eventCount = 0;
  float filteredEventCount;

  public PipelineProvider(PipelineConfig pipelineConfig) {
    visualizePipeline = pipelineConfig.visualizePipeline;
    calibrationAvailable = pipelineConfig.calibrationAvailable;
    collectEstimatedFeatures = pipelineConfig.collectEstimatedFeatures;
    // initialize pipeline modules
    eventFiltering = pipelineConfig.createEventFiltering();
    tracking = new BlobTracking(pipelineConfig);
    blobSelector = pipelineConfig.createImageBlobSelector();
    // calibration required for transformation to physical space
    if (calibrationAvailable)
      transformer = new BlobTransform(pipelineConfig);
    // optional evaluation
    if (collectEstimatedFeatures)
      collector = new TrackingCollector(pipelineConfig);
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
    ++eventCount;
    // visualization of raw events
    if (visualizePipeline) {
      eventFrames[0].receiveEvent(davisDvsEvent);
    }
    // evaluation tool
    if (collectEstimatedFeatures && collector.isGroundTruthAvailable(davisDvsEvent)) {
      collector.setEstimatedFeatures(blobSelector.getSelectedBlobs());
    }
    // filtering returns a boolean
    if (eventFiltering.filterPipeline(davisDvsEvent)) {
      ++filteredEventCount;
      // control pipeline
      tracking.receiveEvent(davisDvsEvent);
      blobSelector.receiveActiveBlobs(tracking.getActiveBlobs());
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

  // for visualization in PresenterLcmModule
  public List<PhysicalBlob> getProcessedblobs() {
    return transformer.getPhysicalBlobs();
  }
}
