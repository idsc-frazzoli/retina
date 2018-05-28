// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

// provides blob object in physical space
public class PhysicalBlob {
  // fields
  private double[] pos; // [m] in gokart reference frame
  private double[] vel;
  private double[] imageCoord; // [pixel] position in visualization
  private final int blobID; // default blobID == 0

  PhysicalBlob(double[] pos, int blobID) {
    this.pos = pos;
    this.blobID = blobID;
    vel = new double[] { 0, 0 };
  }

  public void setImageCoord(double[] imageCoord) {
    this.imageCoord = imageCoord;
  }

  public double[] getPos() {
    return pos;
  }

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
