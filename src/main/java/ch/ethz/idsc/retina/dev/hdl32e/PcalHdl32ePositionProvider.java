// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.io.File;

import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

public class PcalHdl32ePositionProvider implements Hdl32eFiringProvider {
  private final Hdl32ePositionListener hdl32ePositionListener;
  private final File file;

  /** @param hdl32ePositionListener
   * @param file pcap format */
  public PcalHdl32ePositionProvider( //
      Hdl32ePositionListener hdl32ePositionListener, //
      File file) {
    this.hdl32ePositionListener = hdl32ePositionListener;
    this.file = file;
  }

  @Override
  public void start() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        PacketConsumer packetConsumer = new Hdl32ePacketConsumer( //
            new Hdl32ePositionCollector(hdl32ePositionListener));
        try {
          PcapParse.of(file, packetConsumer);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  @Override
  public void stop() {
    // FIXME how to stop?
  }
}
