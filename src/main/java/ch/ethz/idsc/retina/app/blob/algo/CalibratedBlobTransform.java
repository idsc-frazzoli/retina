// code by mg
package ch.ethz.idsc.retina.app.blob.algo;

import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.retina.app.blob.ImageBlob;
import ch.ethz.idsc.retina.app.blob.PhysicalBlob;
import ch.ethz.idsc.retina.app.calib.ImageToGokartLookup;
import ch.ethz.idsc.retina.app.calib.ImageToGokartUtil;

/** Transformation of ImageBlobs to PhysicalBlobs.
 * No lookup table is used because {@link ImageToGokartLookup} is only implemented for
 * integer pixel values. When the lookup table will accept float values as input
 * (e.g. by using interpolation between closest integer values) we can switch to that faster solution. */
/* package */ class CalibratedBlobTransform implements BlobTransform {
  private final ImageToGokartUtil imageToGokartUtil;

  public CalibratedBlobTransform(ImageToGokartUtil imageToGokartUtil) {
    this.imageToGokartUtil = imageToGokartUtil;
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
        imageToGokartUtil.imageToGokart(pos[0], pos[1]), //
        imageBlob.getBlobID());
  }
}
