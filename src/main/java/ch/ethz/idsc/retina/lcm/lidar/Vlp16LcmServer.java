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
  private final UniversalDatagramClient hdl32eRayDatagramClient;
  private final UniversalDatagramClient hdl32ePosDatagramClient;

  public Vlp16LcmServer(String lidarId, int portRay, int portPos) {
    hdl32eRayDatagramClient = new UniversalDatagramClient(portRay, new byte[Vlp16Statics.RAY_PACKET_LENGTH]); // 1206
    hdl32ePosDatagramClient = new UniversalDatagramClient(portPos, new byte[Vlp16Statics.POS_PACKET_LENGTH]); // TODO magic const
    hdl32eRayDatagramClient.addListener(new BinaryBlobPublisher(Vlp16LcmChannels.ray(lidarId)));
    hdl32ePosDatagramClient.addListener(new BinaryBlobPublisher(Vlp16LcmChannels.pos(lidarId)));
  }

  @Override
  public void start() {
    hdl32eRayDatagramClient.start();
    hdl32ePosDatagramClient.start();
  }

  @Override
  public void stop() {
    hdl32eRayDatagramClient.stop();
    hdl32ePosDatagramClient.stop();
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
    Vlp16LcmServer hdl32eLcmServer = new Vlp16LcmServer(channel, portRay, portPos);
    hdl32eLcmServer.start();
  }
}
