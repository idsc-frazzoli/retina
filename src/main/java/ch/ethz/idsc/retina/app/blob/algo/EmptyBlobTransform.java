// code by jph
package ch.ethz.idsc.retina.app.blob.algo;

import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.retina.app.blob.ImageBlob;
import ch.ethz.idsc.retina.app.blob.PhysicalBlob;

/* package */ enum EmptyBlobTransform implements BlobTransform {
  INSTANCE;
  // ---
  @Override // from BlobTransform
  public List<PhysicalBlob> transform(List<ImageBlob> imageBlobs) {
    return Collections.emptyList();
  }
}
