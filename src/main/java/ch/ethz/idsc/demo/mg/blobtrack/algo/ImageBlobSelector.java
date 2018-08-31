// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.tensor.Scalar;

/** this class incorporates prior knowledge to recognize the features we want to track */
public class ImageBlobSelector {
  private final int upperBoarder;
  private List<ImageBlob> imageBlobs;

  public ImageBlobSelector(Scalar upperBoarder) {
    this.upperBoarder = upperBoarder.number().intValue();
    imageBlobs = new ArrayList<>();
  }

  public void receiveImageBlobs(List<ImageBlob> imageblobs) {
    this.imageBlobs = imageblobs;
    checkPosition();
  }

  /** checks if blob is in defined region of interest */
  private void checkPosition() {
    for (int i = 0; i < imageBlobs.size(); i++) {
      if (imageBlobs.get(i).getPos()[1] < upperBoarder) {
        imageBlobs.get(i).setIsRecognized(true);
      }
    }
  }

  /** @return all detected imageBlobs */
  public List<ImageBlob> getImageBlobs() {
    return imageBlobs;
  }

  /** @return imageBlobs that lie within region of interest */
  public List<ImageBlob> getSelectedBlobs() {
    List<ImageBlob> selectedBlobs = new ArrayList<>();
    for (int i = 0; i < imageBlobs.size(); ++i) {
      if (imageBlobs.get(i).getIsRecognized())
        selectedBlobs.add(imageBlobs.get(i));
    }
    return selectedBlobs;
  }
}
