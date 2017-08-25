// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import ch.ethz.idsc.retina.dev.vlp16.Vlp16Statics;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.UniversalDatagramClient;

/** implementation listens to live device for firing and positioning data
 * on given ports. the received packets are forwarded via lcm protocol
 * 
 * server can be launched from the command line with the arguments:
 * LIDARID PORT_RAY PORT_POS
 * 
 * if no arguments are provided, the following default arguments are used:
 * center 2368 8308 */
public class Vlp16LcmServer implements StartAndStoppable {
  private final UniversalDatagramClient rayDatagramClient;
  private final UniversalDatagramClient posDatagramClient;

  public Vlp16LcmServer(String lidarId, int portRay, int portPos) {
    rayDatagramClient = new UniversalDatagramClient(portRay, new byte[Vlp16Statics.RAY_PACKET_LENGTH]);
    posDatagramClient = new UniversalDatagramClient(portPos, new byte[Vlp16Statics.POS_PACKET_LENGTH]);
    rayDatagramClient.addListener(new BinaryBlobPublisher(Vlp16LcmChannels.ray(lidarId)));
    posDatagramClient.addListener(new BinaryBlobPublisher(Vlp16LcmChannels.pos(lidarId)));
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
    String channel = "center";
    int portRay = Vlp16Statics.RAY_DEFAULT_PORT;
    int portPos = Vlp16Statics.POS_DEFAULT_PORT;
    if (1 <= args.length)
      channel = args[0];
    if (3 <= args.length) {
      portRay = Integer.parseInt(args[1]);
      portPos = Integer.parseInt(args[2]);
    }
    Vlp16LcmServer lcmServer = new Vlp16LcmServer(channel, portRay, portPos);
    lcmServer.start();
  }
}
