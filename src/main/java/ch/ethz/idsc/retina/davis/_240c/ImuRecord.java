// code by jph
package ch.ethz.idsc.retina.davis._240c;

public class ImuRecord {
  // TODO units!!!
  public final float accelX;
  public final float accelY;
  public final float accelZ;
  public final float temperature;
  public final float gyroX;
  public final float gyroY;
  public final float gyroZ;

  public ImuRecord(float accelX, float accelY, float accelZ, float temperature, //
      float gyroX, float gyroY, float gyroZ) {
    this.accelX = accelX;
    this.accelY = accelY;
    this.accelZ = accelZ;
    this.temperature = temperature;
    this.gyroX = gyroX;
    this.gyroY = gyroY;
    this.gyroZ = gyroZ;
  }

  public void print() {
    System.out.println(String.format("accelX %f", accelX));
    System.out.println(String.format("accelY %f", accelY));
    System.out.println(String.format("accelZ %f", accelZ));
    System.out.println(String.format("temp.  %f", temperature));
    System.out.println(String.format("gyroX  %f", gyroX));
    System.out.println(String.format("gyroY  %f", gyroY));
    System.out.println(String.format("gyroZ  %f", gyroZ));
  }
}
