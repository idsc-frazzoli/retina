// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.util.TransformUtil;

// Transformation of ImageBlobs to PhysicalBlobs.
public class BlobTransform {
  private List<PhysicalBlob> physicalBlobs;
  private TransformUtil transformUtil;

  BlobTransform(PipelineConfig pipelineConfig) {
    physicalBlobs = new ArrayList<>();
    transformUtil = new TransformUtil(pipelineConfig);
  }

  public void transformSelectedBlobs(List<ImageBlob> blobs) {
    List<PhysicalBlob> physicalBlobs = new ArrayList<>();
    for (int i = 0; i < blobs.size(); i++) {
      double[] physicalPos = transformUtil.imageToWorld(blobs.get(i).getPos()[0], blobs.get(i).getPos()[1]);
      PhysicalBlob singlePhysicalBlob = new PhysicalBlob(physicalPos);
      physicalBlobs.add(singlePhysicalBlob);
    }
    this.physicalBlobs = physicalBlobs;
  }

  public List<PhysicalBlob> getPhysicalBlobs() {
    return physicalBlobs;
  }
}
