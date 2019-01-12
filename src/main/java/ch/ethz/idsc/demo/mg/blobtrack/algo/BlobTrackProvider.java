// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;
import ch.ethz.idsc.demo.mg.filter.DavisDvsEventFilter;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** implementation of the tracking algorithm
 * " asynchronous event-based multikernel algorithm for high-speed visual features tracking"
 * by Xavier Lagorce et al.
 * https://ieeexplore.ieee.org/document/6899691 */
public class BlobTrackProvider implements DavisDvsListener {
  private final DavisDvsEventFilter davisDvsEventFilter;
  private final BlobTransform blobTransform;
  private final BlobTracking blobTracking;
  private final ImageBlobSelector imageBlobSelector;
  // ---
  private List<PhysicalBlob> physicalBlobs = new ArrayList<>();

  public BlobTrackProvider(BlobTrackConfig blobTrackConfig) {
    davisDvsEventFilter = blobTrackConfig.davisConfig.createBackgroundActivityFilter();
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
      imageBlobSelector.receiveImageBlobs(blobTracking.getActiveBlobs());
      physicalBlobs = blobTransform.transform(imageBlobSelector.getSelectedBlobs());
    }
  }

  public List<PhysicalBlob> getPhysicalBlobs() {
    return physicalBlobs;
  }

  public BlobTracking getBlobTracking() {
    return blobTracking;
  }

  public ImageBlobSelector getImageBlobSelector() {
    return imageBlobSelector;
  }
}
