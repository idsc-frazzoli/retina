// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartUtil;

/** Transformation of ImageBlobs to PhysicalBlobs.
 * Currently, no lookup table is used because the ImageToGokartLookup is only implemented for
 * integer pixel values. When the lookup table will accept float values as input
 * (e.g. by using interpolation between closest integer values) we can switch to that faster solution. */
/* package */ class BlobTransform {
  // TODO JPH mental note class design
  private List<PhysicalBlob> physicalBlobs = new ArrayList<>();
  private final ImageToGokartUtil imageToWorldUtil;

  public BlobTransform(BlobTrackConfig pipelineConfig) {
    imageToWorldUtil = pipelineConfig.davisConfig.createImageToGokartUtil();
  }

  public void transformSelectedBlobs(List<ImageBlob> imageBlobs) {
    this.physicalBlobs = imageBlobs.parallelStream().map(this::toPhysicalBlob) //
        .collect(Collectors.toList());
  }

  private PhysicalBlob toPhysicalBlob(ImageBlob imageBlob) {
    float[] pos = imageBlob.getPos();
    return new PhysicalBlob( //
        imageToWorldUtil.imageToGokart(pos[0], pos[1]), //
        imageBlob.getBlobID());
  }

  public List<PhysicalBlob> getPhysicalBlobs() {
    return physicalBlobs;
  }
}
