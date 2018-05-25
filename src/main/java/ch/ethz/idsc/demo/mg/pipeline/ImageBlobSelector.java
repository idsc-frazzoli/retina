// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.Scalar;

// this class incorporates prior knowledge to recognize the features we want to track.
// filter has no memory --> a further filtering step is conducted by the estimation algorithm in physical space.
public class ImageBlobSelector {
  // parameters
  private final int upperBoarder; // [pixel] blobs with larger pos[1] are neglected (probably wall features)
  // fields
  private List<ImageBlob> imageBlobs;

  ImageBlobSelector(Scalar upperBoarder) {
    this.upperBoarder = upperBoarder.number().intValue();
    imageBlobs = new ArrayList<>();
  }

  public void receiveActiveBlobs(List<ImageBlob> imageblobs) {
    this.imageBlobs = imageblobs;
    // only consider region of interest, i.e. floor
    checkPosition();
    // shape must correspond to prior knowledge
    // checkShape();
  }

  // compare aspect ratio between eigenvalues and also look at eigenvectors.
  private void checkShape() {
    for (int i = 0; i < imageBlobs.size(); i++) {
      // imageBlobs.get(i).getEigenVectors();
      // imageBlobs.get(i).getStandardDeviation();
    }
  }

  private void checkPosition() {
    for (int i = 0; i < imageBlobs.size(); i++) {
      if (imageBlobs.get(i).getPos()[1] < upperBoarder) {
        imageBlobs.get(i).setIsRecognized(true);
      }
    }
  }

  // return both selected and neglected blobs for visualization
  public List<ImageBlob> getProcessedBlobs() {
    return imageBlobs;
  }

  // return selected blobs for next module in pipeline
  public List<ImageBlob> getSelectedBlobs() {
    List<ImageBlob> selectedBlobs = new ArrayList<>();
    for (int i = 0; i < imageBlobs.size(); i++) {
      if (imageBlobs.get(i).getIsRecognized()) {
        selectedBlobs.add(imageBlobs.get(i));
      }
    }
    return selectedBlobs;
  }
}
