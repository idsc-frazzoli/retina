// code by az
package ch.ethz.idsc.retina.demo.az;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsGrayImage;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public class UDPpythonpublisher implements TimedImageListener {
  DatagramSocketManager dgsm;
  final int S2_PORT = 6785;
  JFrame jframe = new JFrame();
  BufferedImage img = null;
  JComponent jcomponent = new JComponent() {
    @Override
    protected void paintComponent(java.awt.Graphics g) {
      if (img != null)
        g.drawImage(img, 0, 0, null);
    };
  };

  public UDPpythonpublisher() {
    final int S1_PORT = 6780;
    final String LADDR = "localhost";
    dgsm = DatagramSocketManager.local(new byte[5], S1_PORT, LADDR);
    dgsm.start();
    jframe.setContentPane(jcomponent);
    jframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jframe.setBounds(100, 100, 240, 180);
  }

  public static void main(String[] args) throws InterruptedException {
    createUDPpublisher("overview", 50000);
  }

  public static void createUDPpublisher(String cameraId, int period) throws InterruptedException {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisLcmClient davisLcmClient = new DavisLcmClient(cameraId);
    // handle dvs
    AccumulatedEventsGrayImage accumulatedEventsImage = new AccumulatedEventsGrayImage(davisDevice, period);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(new TimedImageListener() {
      @Override
      public void timedImage(TimedImageEvent timedImageEvent) {
        System.out.println("encoding" + timedImageEvent.time);
      }
    });
    UDPpythonpublisher p = new UDPpythonpublisher();
    accumulatedEventsImage.addListener(p);
    // start to listen
    davisLcmClient.startSubscriptions();
    // return davisLcmViewer;
    p.jframe.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        davisLcmClient.stopSubscriptions();
        p.close();
      }
    });
    p.jframe.setVisible(true);
  }

  private void close() {
    dgsm.stop();
  }

  @Override
  public void timedImage(TimedImageEvent timedImageEvent) {
    WritableRaster writableRaster = timedImageEvent.bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    byte[] data = dataBufferByte.getData();
    try {
      dgsm.send(new DatagramPacket(data, data.length, InetAddress.getByName("localhost"), S2_PORT));
    } catch (IOException e) {
      e.printStackTrace();
    }
    img = timedImageEvent.bufferedImage;
    jcomponent.repaint();
  }
}
