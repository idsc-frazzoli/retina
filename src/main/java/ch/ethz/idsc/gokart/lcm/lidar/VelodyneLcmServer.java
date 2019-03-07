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
 * server can be launched from the command line with the arguments: MODEL
 * LIDARID PORT_RAY PORT_POS
 * 
 * MODEL is either of the strings: "HDL32E", "VLP16" LIDARID describes the
 * function/position of the sensor on the robot, for example "center" PORT_RAY
 * is the UDP port on which the the device publishes firing packets PORT_POS is
 * the UDP port on which the the device publishes positioning packets
 * 
 * if no arguments are provided, the following default arguments are used:
 * HDL32E center 2368 8308 */
public class VelodyneLcmServer implements StartAndStoppable {
  private final DatagramSocketManager rayDatagramClient;
  private final DatagramSocketManager posDatagramClient;

  /** @param velodyneModel
   * @param lidarId
   * @param portRay
   * @param portPos */
  public VelodyneLcmServer(VelodyneModel velodyneModel, String lidarId, int portRay, int portPos) {
    rayDatagramClient = DatagramSocketManager.local(new byte[VelodyneStatics.RAY_PACKET_LENGTH], portRay);
    posDatagramClient = DatagramSocketManager.local(new byte[VelodyneStatics.POS_PACKET_LENGTH], portPos);
    rayDatagramClient.addListener(new BinaryBlobPublisher(VelodyneLcmChannels.ray(velodyneModel, lidarId)));
    posDatagramClient.addListener(new BinaryBlobPublisher(VelodyneLcmChannels.pos(velodyneModel, lidarId)));
  }

  @Override
  public void start() {
    rayDatagramClient.start();
    posDatagramClient.start();
  }

  @Override
  public void stop() {
    rayDatagramClient.stop();
    posDatagramClient.stop();
  }

  /** main function for use as command line tool
   * 
   * @param args */
  public static void main(String[] args) {
    VelodyneModel velodyneModel = VelodyneModel.HDL32E;
    String channel = "center";
    int portRay = VelodyneStatics.RAY_DEFAULT_PORT;
    int portPos = VelodyneStatics.POS_DEFAULT_PORT;
    if (1 <= args.length)
      velodyneModel = VelodyneModel.valueOf(args[1].toUpperCase());
    if (2 <= args.length)
      channel = args[1];
    if (4 <= args.length) {
      portRay = Integer.parseInt(args[2]);
      portPos = Integer.parseInt(args[3]);
    }
    VelodyneLcmServer velodyneLcmServer = new VelodyneLcmServer(velodyneModel, channel, portRay, portPos);
    velodyneLcmServer.start();
  }
}
