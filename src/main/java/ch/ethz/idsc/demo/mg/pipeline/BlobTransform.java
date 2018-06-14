// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.util.ImageToWorldUtil;

// Transformation of ImageBlobs to PhysicalBlobs.
// TODO switch to TransformUtilLookup, maybe use interpolation?
public class BlobTransform {
  private List<PhysicalBlob> physicalBlobs;
  private final ImageToWorldUtil imageToWorldUtil;

  BlobTransform(PipelineConfig pipelineConfig) {
    physicalBlobs = new ArrayList<>();
    imageToWorldUtil = pipelineConfig.createTransformUtil();
  }

  public void transformSelectedBlobs(List<ImageBlob> blobs) {
    List<PhysicalBlob> physicalBlobs = new ArrayList<>();
    for (int i = 0; i < blobs.size(); i++) {
      double[] physicalPos = imageToWorldUtil.imageToWorld(blobs.get(i).getPos()[0], blobs.get(i).getPos()[1]);
      PhysicalBlob singlePhysicalBlob = new PhysicalBlob(physicalPos, blobs.get(i).getBlobID());
      physicalBlobs.add(singlePhysicalBlob);
    }
    this.physicalBlobs = physicalBlobs;
  }

  public List<PhysicalBlob> getPhysicalBlobs() {
    return physicalBlobs;
  }
}
