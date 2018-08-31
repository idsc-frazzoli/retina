// code by mg, jph
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;

interface BlobTransform {
  /** transform between image plane and go kart frame
   * 
   * @param imageBlobs
   * @return list of PhysicalBlob objects */
  List<PhysicalBlob> transform(List<ImageBlob> imageBlobs);
}
