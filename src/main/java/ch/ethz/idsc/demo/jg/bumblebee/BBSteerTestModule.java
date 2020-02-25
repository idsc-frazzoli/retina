// code by gjoel
package ch.ethz.idsc.demo.jg.bumblebee;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

import ch.ethz.idsc.demo.jg.bumblebee.steer.BBSteerGetEvent;
import ch.ethz.idsc.demo.jg.bumblebee.steer.BBSteerPutEvent;
import ch.ethz.idsc.demo.jg.bumblebee.steer.BBSteerSocket;
import ch.ethz.idsc.gokart.core.GetListener;
import ch.ethz.idsc.gokart.core.PutListener;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

public class BBSteerTestModule extends AbstractClockedModule implements GetListener<BBSteerGetEvent>, PutProvider<BBSteerPutEvent>,
    PutListener<BBSteerPutEvent> {
  private BBSteerPutEvent putEvent = null;
  private Scalar torque = Quantity.of(0, BBSteerPutEvent.UNIT_TRQ);

  @Override // from AbstractModule
  public void first() {
    WindowConfiguration windowConfiguration = //
        AppCustomization.load(getClass(), new WindowConfiguration());
    JFrame jFrame = new JFrame();
    {
      JPanel jPanel = new JPanel();
      {
        JTextField jTextField = new JTextField();
        jTextField.setText(torque.toString());
        jTextField.addActionListener(actionEvent -> torque = Scalars.fromString(jTextField.getText()));
        jPanel.add(jTextField);
      }
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        ModuleAuto.INSTANCE.endAll();
      }
    });
    jFrame.setVisible(true);
    // ---
    BBSteerSocket.INSTANCE.start();
    // ---
    BBSteerSocket.INSTANCE.addPutListener(this);
    BBSteerSocket.INSTANCE.addGetListener(this);
    BBSteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractClockedModule
  public void runAlgo() {
    putEvent = BBSteerPutEvent.create(torque);
  }

  @Override // from AbstractModule
  public void last() {
    BBSteerSocket.INSTANCE.removePutListener(this);
    BBSteerSocket.INSTANCE.removeGetListener(this);
    BBSteerSocket.INSTANCE.removePutProvider(this);
    // ---
    BBSteerSocket.INSTANCE.stop();
  }

  @Override // from AbstractClockedModule
  public Scalar getPeriod() {
    return Quantity.of(0.5, SI.SECOND);
  }

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  @Override // from PutProvider
  public Optional<BBSteerPutEvent> putEvent() {
    return Optional.ofNullable(putEvent);
  }

  @Override // from GetListener
  public void getEvent(BBSteerGetEvent getEvent) {
    System.out.println("GetEvent:");
    System.out.println("\tMotor angle speed: " + getEvent.angSpd + " -> " + getEvent.angSpd());
    System.out.println("\tColumn torque sensor signal: " + getEvent.tsuTrq + " -> " + getEvent.tsuTrq());
    System.out.println("\tReference motor torque: " + getEvent.refMotTrq + " -> " + getEvent.refMotTrq());
    System.out.println("\tEstimated motor torque: " + getEvent.estMotTrq + " -> " + getEvent.estMotTrq());
  }

  @Override // from PutListener
  public void putEvent(BBSteerPutEvent putEvent) {
    System.out.println("PutEvent:");
    System.out.println("\tRequested motor torque: " + putEvent.getTorque());
  }

  public static void main(String[] args) {
    // byte[] data = new byte[] { 0x00, 0x01, 0x00, 0x02 };
    // ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, data.length);
    // byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    //
    // short a = byteBuffer.getShort();
    // short b = byteBuffer.getShort();
    //
    // byteBuffer.clear();
    // short c = 1;
    // short d = 515;
    // byteBuffer.putShort(c);
    // byteBuffer.putShort(d);
    //
    // byteBuffer.clear();

    try {
      ModuleAuto.INSTANCE.runOne(BBSteerTestModule.class);
    } catch (Exception e) {
      e.printStackTrace();
      ModuleAuto.INSTANCE.endAll();
    }
  }
}
