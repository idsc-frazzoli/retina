// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

/** blob object in physical space */
public class PhysicalBlob {
  private final double[] pos; // [m] in gokart reference frame
  private final double[] vel = new double[2];
  private final int blobID; // default blobID == 0
  private double[] imageCoord; // [pixel] position in PhysicalBlobFrame

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

  // TODO MG function/vel is not used
  public double[] getVel() {
    return vel;
  }

  public int getblobID() {
    return blobID;
  }

  public double[] getImageCoord() {
    return imageCoord;
  }
}
