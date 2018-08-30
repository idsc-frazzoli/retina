// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

/** blob object in physical space */
public class PhysicalBlob {
  private final double[] pos; // interpreted as [m] in gokart reference frame
  private final int blobID; // default blobID == 0
  private double[] imageCoord; // interpreted as [pixel] position in PhysicalBlobFrame

  public PhysicalBlob(double[] pos, int blobID) {
    this.pos = pos;
    this.blobID = blobID;
  }

  public void setImageCoord(double[] imageCoord) {
    this.imageCoord = imageCoord;
  }

  public double[] getPos() {
    return pos;
  }

  public int getBlobID() {
    return blobID;
  }

  public double[] getImageCoord() {
    return imageCoord;
  }
}
