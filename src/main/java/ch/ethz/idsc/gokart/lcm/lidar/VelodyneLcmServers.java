// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

/** implementation listens to live device for firing and positioning data on
 * given ports. the received packets are forwarded via lcm protocol
 * 
 * MODEL is either "HDL32E", "VLP16"
 * LIDARID describes the
 * function/position of the sensor on the robot, for example "center" */
public enum VelodyneLcmServers {
  ;
  /** @param velodyneModel
   * @param lidarId
   * @param port UDP port on which the the device publishes firing packets
   * @return */
  public static StartAndStoppable ray(VelodyneModel velodyneModel, String lidarId, int port) {
    DatagramSocketManager datagramSocketManager = //
        DatagramSocketManager.local(new byte[VelodyneStatics.RAY_PACKET_LENGTH], port);
    datagramSocketManager.addListener(new BinaryBlobPublisher(VelodyneLcmChannels.ray(velodyneModel, lidarId)));
    return datagramSocketManager;
  }

  /** @param velodyneModel
   * @param lidarId
   * @param port UDP port on which the the device publishes positioning packets
   * @return */
  public static StartAndStoppable pos(VelodyneModel velodyneModel, String lidarId, int port) {
    DatagramSocketManager datagramSocketManager = //
        DatagramSocketManager.local(new byte[VelodyneStatics.POS_PACKET_LENGTH], port);
    datagramSocketManager.addListener(new BinaryBlobPublisher(VelodyneLcmChannels.pos(velodyneModel, lidarId)));
    return datagramSocketManager;
  }

  /** main function for use as command line tool
   *
   * @param args */
  public static void main(String[] args) {
    VelodyneModel velodyneModel = VelodyneModel.HDL32E;
    String channel = "center";
    int portRay = VelodyneStatics.RAY_PORT_DEFAULT;
    if (1 <= args.length)
      velodyneModel = VelodyneModel.valueOf(args[1].toUpperCase());
    if (2 <= args.length)
      channel = args[1];
    if (3 <= args.length) {
      portRay = Integer.parseInt(args[2]);
    }
    StartAndStoppable startAndStoppable = ray(velodyneModel, channel, portRay);
    startAndStoppable.start();
  }
}
