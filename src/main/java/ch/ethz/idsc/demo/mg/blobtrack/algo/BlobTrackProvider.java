// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.filter.DavisDvsEventFilter;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** implements the object detection and tracking algorithm as described in TODO MG find reference */
public class BlobTrackProvider implements DavisDvsListener {
  private final DavisDvsEventFilter davisDvsEventFilter;
  private final BlobTracking blobTracking;
  private final ImageBlobSelector imageBlobSelector;
  private final BlobTransform blobTransform; // default initialization if unused
  private List<PhysicalBlob> physicalBlobs = new ArrayList<>();

  public BlobTrackProvider(BlobTrackConfig blobTrackConfig) {
    davisDvsEventFilter = new BackgroundActivityFilter(blobTrackConfig.davisConfig);
    blobTracking = new BlobTracking(blobTrackConfig);
    imageBlobSelector = blobTrackConfig.createImageBlobSelector();
    blobTransform = blobTrackConfig.isCalibrationAvailable() //
        ? new CalibratedBlobTransform(blobTrackConfig.davisConfig.createImageToGokartUtil())
        : EmptyBlobTransform.INSTANCE;
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (davisDvsEventFilter.filter(davisDvsEvent)) {
      blobTracking.receiveEvent(davisDvsEvent);
      imageBlobSelector.receiveActiveBlobs(blobTracking.getActiveBlobs());
      physicalBlobs = blobTransform.transform(imageBlobSelector.getSelectedBlobs());
    }
  }

  public List<PhysicalBlob> getPhysicalBlobs() {
    return physicalBlobs;
  }

  public BlobTracking getBlobTracking() {
    return blobTracking;
  }

  public ImageBlobSelector getBlobSelector() {
    return imageBlobSelector;
  }
}
