// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;
import ch.ethz.idsc.demo.mg.blobtrack.eval.TrackingCollector;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.filter.FilterInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** implements the object detection and tracking algorithm as described in TODO MG find reference */
public class BlobTrackProvider implements DavisDvsListener {
  private final FilterInterface filterInterface;
  private final BlobTracking blobTracking;
  private final ImageBlobSelector imageBlobSelector;
  private final boolean calibrationAvailable;
  private final boolean collectEstimatedFeatures;
  private BlobTransform blobTransform = null; // default initialization if unused
  private TrackingCollector trackingCollector = null;

  public BlobTrackProvider(BlobTrackConfig blobTrackConfig) {
    calibrationAvailable = blobTrackConfig.calibrationAvailable;
    collectEstimatedFeatures = blobTrackConfig.collectEstimatedFeatures;
    filterInterface = new BackgroundActivityFilter(blobTrackConfig.davisConfig);
    blobTracking = new BlobTracking(blobTrackConfig);
    imageBlobSelector = blobTrackConfig.createImageBlobSelector();
    if (calibrationAvailable)
      blobTransform = new BlobTransform(blobTrackConfig);
    if (collectEstimatedFeatures)
      trackingCollector = new TrackingCollector(blobTrackConfig);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (collectEstimatedFeatures && trackingCollector.isGroundTruthAvailable(davisDvsEvent)) {
      trackingCollector.setEstimatedFeatures(imageBlobSelector.getSelectedBlobs());
    }
    if (filterInterface.filter(davisDvsEvent)) {
      blobTracking.receiveEvent(davisDvsEvent);
      imageBlobSelector.receiveActiveBlobs(blobTracking.getActiveBlobs());
      if (calibrationAvailable) {
        blobTransform.transformSelectedBlobs(imageBlobSelector.getSelectedBlobs());
      }
    }
  }

  public List<PhysicalBlob> getPhysicalblobs() {
    return blobTransform.getPhysicalBlobs();
  }

  public BlobTracking getBlobTracking() {
    return blobTracking;
  }

  public ImageBlobSelector getBlobSelector() {
    return imageBlobSelector;
  }
}
