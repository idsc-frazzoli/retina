//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public class DavisSingleBlob {
  private int[] position = new int[] { 0, 0 };
  private int[] velocity;
  private int[][] covariance;
  private boolean boundarySide;
  private int importance;
  private boolean isActiveBlob;

  // sets the status of the blob to active
  void setActiveBlob() {
    this.isActiveBlob = true;
  }

  // updates the blob with a new event that is associated to that blob
  void updateSingleBlob(DavisDvsEvent davisDvsEvent) {
    // bla bla bla
  }
}
