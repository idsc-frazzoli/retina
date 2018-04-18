package ch.ethz.idsc.demo.mg;

import java.io.Serializable;

/** class used only for storage and retrieval
 * info in BlobContainer creates TrackedBlob
 * see BlobContainerIO */
public class BlobContainer implements Serializable {
  public float[] pos;
  public double[][] covariance;
  public int timeStamp;
  // CLASS DOES NOT HAVE ANY MEMBER FUNCTIONS
}
