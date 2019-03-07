// code by az
package ch.ethz.idsc.demo.az;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.AbstractAccumulatedImage;
import ch.ethz.idsc.retina.davis.app.SAEGaussDecayImage;
import ch.ethz.idsc.retina.util.img.ImageCopy;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

/* package */ class UdpPythonPublisher implements TimedImageListener {
  /** port to send data to */
  private static final int S2_PORT = 6785;
  // ---
  private final DatagramSocketManager dsm;
  private final JFrame jFrame = new JFrame();
  private final ImageCopy imageCopy = new ImageCopy();
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(imageCopy.get(), 0, 0, null);
    }
  };
  private final InetAddress inetAddress;

  UdpPythonPublisher() throws UnknownHostException {
    final int S1_PORT = 6780; // port on which to listen
    final String LADDR = "localhost";
    dsm = DatagramSocketManager.local(new byte[4], S1_PORT, LADDR); // we don't expect to receive data
    dsm.start();
    jFrame.setContentPane(jComponent);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 240, 180);
    inetAddress = InetAddress.getByName("localhost");
  }

  @Override
  public void timedImage(TimedImageEvent timedImageEvent) {
    WritableRaster writableRaster = timedImageEvent.bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    byte[] data = dataBufferByte.getData();
    try {
      dsm.send(new DatagramPacket(data, data.length, inetAddress, S2_PORT));
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    imageCopy.update(timedImageEvent.bufferedImage);
    jComponent.repaint();
  }

  private void close() {
    dsm.stop();
  }

  public static void createUDPpublisher(String cameraId, int period) throws Exception {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisLcmClient davisLcmClient = new DavisLcmClient(cameraId);
    // handle dvs
    AbstractAccumulatedImage accumulatedEventsImage = SAEGaussDecayImage.of(davisDevice, period);
    davisLcmClient.addDvsListener(accumulatedEventsImage);
    UdpPythonPublisher udpPythonPublisher = new UdpPythonPublisher();
    accumulatedEventsImage.addListener(udpPythonPublisher);
    // start to listen
    davisLcmClient.startSubscriptions();
    udpPythonPublisher.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        davisLcmClient.stopSubscriptions();
        udpPythonPublisher.close();
      }
    });
    udpPythonPublisher.jFrame.setVisible(true);
  }

  public static void main(String[] args) throws Exception {
    createUDPpublisher("overview", 50_000); // 50_000 us == 50 ms
  }
}
