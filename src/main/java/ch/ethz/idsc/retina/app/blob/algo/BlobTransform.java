// code by mg, jph
package ch.ethz.idsc.retina.app.blob.algo;

import java.util.List;

import ch.ethz.idsc.retina.app.blob.ImageBlob;
import ch.ethz.idsc.retina.app.blob.PhysicalBlob;

/* package */ interface BlobTransform {
  /** transform between image plane and go kart frame
   * 
   * @param imageBlobs
   * @return list of PhysicalBlob objects */
  List<PhysicalBlob> transform(List<ImageBlob> imageBlobs);
}
