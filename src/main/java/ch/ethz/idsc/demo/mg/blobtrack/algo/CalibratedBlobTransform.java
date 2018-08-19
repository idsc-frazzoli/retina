// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartUtil;

// TODO MG is the class comment still up-to-date?
/** Transformation of ImageBlobs to PhysicalBlobs.
 * Currently, no lookup table is used because the ImageToGokartLookup is only implemented for
 * integer pixel values. When the lookup table will accept float values as input
 * (e.g. by using interpolation between closest integer values) we can switch to that faster solution. */
/* package */ class CalibratedBlobTransform implements BlobTransform {
  private final ImageToGokartUtil imageToWorldUtil;

  public CalibratedBlobTransform(ImageToGokartUtil imageToWorldUtil) {
    this.imageToWorldUtil = imageToWorldUtil;
  }

  @Override // from BlobTransform
  public List<PhysicalBlob> transform(List<ImageBlob> imageBlobs) {
    return imageBlobs.parallelStream() //
        .map(this::toPhysicalBlob) //
        .collect(Collectors.toList());
  }

  private PhysicalBlob toPhysicalBlob(ImageBlob imageBlob) {
    float[] pos = imageBlob.getPos();
    return new PhysicalBlob( //
        imageToWorldUtil.imageToGokart(pos[0], pos[1]), //
        imageBlob.getBlobID());
  }
}
