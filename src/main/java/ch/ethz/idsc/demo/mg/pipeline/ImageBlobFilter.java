// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

// this class incorporates prior knowledge to recognize the features we want to track.
// filter has no memory --> a further filtering step is conducted by the estimation algorithm in physical space.
public class ImageBlobFilter {
  // parameters
  private static final int upperBoarder = 100; // [pixel] blobs with larger pos[1] are neglected (probably wall features)
  // fields
  private List<ImageBlob> imageBlobs;

  ImageBlobFilter() {
    imageBlobs = new ArrayList<>();
  }

  public void receiveBlobList(List<ImageBlob> imageblobs) {
    this.imageBlobs = imageblobs;
    // only consider region of interest, i.e. floor
    checkPosition();
    // shape must correspond to prior knowledge
    checkShape();
  }

  // compare aspect ratio between eigenvalues and also look at eigenvectors.
  private void checkShape() {
    for (int i = 0; i < imageBlobs.size(); i++) {
        imageBlobs.get(i).getEigenVectors();
        imageBlobs.get(i).getStandardDeviation();

    }
  }

  private void checkPosition() {
    for (int i = 0; i < imageBlobs.size(); i++) {
      if (imageBlobs.get(i).getPos()[1] < upperBoarder) {
        imageBlobs.get(i).setIsRecognized(true);
      }
    }
  }

  public List<ImageBlob> getTrackedBlobs() {
    return imageBlobs;
  }
}
