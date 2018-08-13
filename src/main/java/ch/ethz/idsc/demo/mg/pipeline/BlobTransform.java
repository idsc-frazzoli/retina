// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartUtil;

/** Transformation of ImageBlobs to PhysicalBlobs. */
// TODO switch to TransformUtilLookup, maybe use interpolation?
// TODO MG implementation does not use lookup table? -> optimize?
/* package */ class BlobTransform {
  // TODO JAN mental note class design
  private List<PhysicalBlob> physicalBlobs = new ArrayList<>();
  private final ImageToGokartUtil imageToWorldUtil;

  public BlobTransform(PipelineConfig pipelineConfig) {
    imageToWorldUtil = pipelineConfig.createImageToGokartUtil();
  }

  public void transformSelectedBlobs(List<ImageBlob> imageBlobs) {
    this.physicalBlobs = imageBlobs.stream() // TODO MG parallel?
        .map(this::toPhysicalBlob) //
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
