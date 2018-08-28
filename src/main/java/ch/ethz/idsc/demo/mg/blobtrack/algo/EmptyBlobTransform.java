// code by jph
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;

/* package */ enum EmptyBlobTransform implements BlobTransform {
  INSTANCE;
  // ---
  @Override // from BlobTransform
  public List<PhysicalBlob> transform(List<ImageBlob> imageBlobs) {
    return Collections.emptyList();
  }
}
