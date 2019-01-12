// code by jph
package ch.ethz.idsc.retina.lidar;

/** in order to configure the device
 * Velodyne HDL32E, and Velodyne VLP16
 * connect the running device to the local network.
 * Then, use a browser to visit http://192.168.1.201 */
public enum VelodyneStatics {
  ;
  // CONSTANTS
  /** length of firing packet */
  public static final int RAY_PACKET_LENGTH = 1206;
  /** length of positioning packet */
  public static final int POS_PACKET_LENGTH = 512;
  // ---
  // DEFAULT VALUES
  /** default port on which vlp16/hdl32e publishes firing data */
  public static final int RAY_DEFAULT_PORT = 2368;
  /** default port on which vlp16/hdl32e publishes positioning data */
  public static final int POS_DEFAULT_PORT = 8308;
  // ---
  // PROTOCOL CONSTANTS
  /** resolution of one full rotation */
  public static final int AZIMUTH_RESOLUTION = 36000;
  /** "report distance to the nearest 0.2 cm" => 2 mm */
  public static final double TO_METER = 0.002;
  public static final float TO_METER_FLOAT = (float) TO_METER;

  /** @param azimuth in the interval [-36000, 36000]
   * @return [0, 1, ..., 36000) */
  public static int lookupAzimuth(int azimuth) {
    return (azimuth + AZIMUTH_RESOLUTION) % AZIMUTH_RESOLUTION;
  }
}
