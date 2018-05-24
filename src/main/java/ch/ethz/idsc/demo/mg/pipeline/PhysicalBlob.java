// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

// provides blob object in physical space
public class PhysicalBlob {
  // fields
  private double[] pos; // [m] in gokart reference frame
  private double[] vel;
  private double[] imageCoord; // [pixel] position in visualization

  PhysicalBlob(double[] pos) {
    this.pos = pos;
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

  public double[] getImageCoord() {
    return imageCoord;
  }
}
