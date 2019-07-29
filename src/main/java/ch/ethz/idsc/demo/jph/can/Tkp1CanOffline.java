// code by jph
package ch.ethz.idsc.demo.jph.can;

import java.io.File;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.io.CanFrame;
import ch.ethz.idsc.subare.util.HtmlUtf8;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class Tkp1CanOffline implements OfflineLogListener, AutoCloseable {
  private final HtmlUtf8 htmlUtf8;

  public Tkp1CanOffline(String name) {
    htmlUtf8 = HtmlUtf8.page(HomeDirectory.file(name + ".htm"));
    htmlUtf8.appendln("<pre>");
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("can.tkp1")) {
      CanFrame canFrame = new CanFrame(byteBuffer);
      if (canFrame.id == 1)
        htmlUtf8.append("<span style=\"background-color: #a0ffa0\">");
      htmlUtf8.append(String.format("%12s %2d  %02x %02x %02x %02x %02x %02x %02x %02x", //
          time.map(Round._6), //
          canFrame.id, //
          canFrame.get(0), canFrame.get(1), canFrame.get(2), canFrame.get(3), //
          canFrame.get(4), canFrame.get(5), canFrame.get(6), canFrame.get(7)));
      if (canFrame.id == 11) {
        byteBuffer.position(2);
        byteBuffer.getInt();
        int message = byteBuffer.getInt();
        int extr = message & 0x001fe000;
        extr >>= 13;
        htmlUtf8.append(String.format("   #=%3d", extr & 0xff));
      } else //
      if (canFrame.id == 10) {
        short value0 = (short) (((canFrame.get(1) & 0xff) << 9) + ((canFrame.get(0) & 0xff) << 1));
        value0 >>= 1;
        short value1 = (short) (((canFrame.get(3) & 0xff) << 9) + ((canFrame.get(2) & 0xff) << 1));
        value1 >>= 1;
        // TODO JPH ONE bit removed
        short value2 = (short) (((canFrame.get(5) & 0x7f) << 9) + ((canFrame.get(4) & 0xff) << 1));
        value2 >>= 2;
        boolean lo2 = (canFrame.get(4) & 0x01) == 0x01;
        boolean hi2 = (canFrame.get(5) & 0x80) == 0x80;
        short value3 = (short) (((canFrame.get(7) & 0xff) << 8) + (canFrame.get(6) & 0xff));
        htmlUtf8.append(String.format(" %6d %6d %6d %6d %3s %3s", //
            value0, value1, value2, value3, lo2 ? "LO2" : "", hi2 ? "HI2" : ""));
      } else //
      if (canFrame.id == 1) {
        byteBuffer.position(2);
        int all = byteBuffer.getInt();
        boolean active = (canFrame.get(0) & 1) == 1;
        int cta = all & 0x007f8000;
        cta >>= 15;
        int trq = all & 0x00007ffe;
        trq <<= 17;
        trq >>= 18;
        htmlUtf8.append(String.format("   #=%3d  %5s %5d", cta & 0xff, active ? "ACT" : "", trq));
        htmlUtf8.append("</span>");
      }
      htmlUtf8.appendln();
    } else //
      System.out.println(channel);
  }

  @Override
  public void close() throws Exception {
    htmlUtf8.appendln("</pre>");
    htmlUtf8.close();
  }

  public static void main(String[] args) throws Exception {
    File folder = new File("/media/datahaki/media/ethz/steering actuator/can_binary");
    String name = null;
    name = "tkp1_on_passive_static";
    name = "tkp1_on_passive_turning";
    name = "tkp1_complete_bootsequence_v1";
    try (Tkp1CanOffline steerCanOffline = new Tkp1CanOffline(name)) {
      OfflineLogPlayer.process(new File(folder, name + ".lcm.00"), steerCanOffline);
    }
  }
}
