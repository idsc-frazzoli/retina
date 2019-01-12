// code by mg
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JToggleButton;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.AbstractAccumulatedImage;
import ch.ethz.idsc.retina.davis.app.AccumulatedEventsGrayImage;
import ch.ethz.idsc.retina.util.img.ImageCopy;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

public class AccumulatedEventRender extends AbstractGokartRender implements TimedImageListener, ActionListener {
  private final DavisDevice davisDevice = Davis240c.INSTANCE;
  public final AbstractAccumulatedImage abstractAccumulatedImage = AccumulatedEventsGrayImage.of(davisDevice);
  private final ImageToGokartInterface imageToWorldLookup;
  private final ImageCopy imageCopy;
  private final BlobTrackConfig pipelineConfig;
  private final int width;
  private final int height;
  // ..
  final JToggleButton jToggleButton = new JToggleButton("events");
  public boolean isSelected = false;

  public AccumulatedEventRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
    abstractAccumulatedImage.setInterval(25_000);
    abstractAccumulatedImage.addListener(this);
    pipelineConfig = new BlobTrackConfig();
    imageToWorldLookup = pipelineConfig.davisConfig.createImageToGokartInterface();
    width = pipelineConfig.davisConfig.width.number().intValue();
    height = pipelineConfig.davisConfig.height.number().intValue();
    imageCopy = new ImageCopy();
    jToggleButton.setSelected(isSelected);
    jToggleButton.addActionListener(this);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (!isSelected)
      return;
    // visualize events
    BufferedImage bufferedImage = imageCopy.get(); // TODO may need to make another copy
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    byte[] bytes = dataBufferByte.getData();
    int index = 0;
    if (bytes.length == width * height) {
      final double mapAheadDistance = //
          Magnitude.METER.toDouble(SensorsConfig.GLOBAL.davis_frustum.Get(1));
      for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
          if (bytes[index] == 0 || bytes[index] == (byte) 255) {
            Tensor mappedEvent = imageToWorldLookup.imageToGokartTensor(index);
            if (mappedEvent.Get(0).number().doubleValue() < mapAheadDistance) {
              Point2D point = geometricLayer.toPoint2D(mappedEvent);
              Color eventColor = bytes[index] == 0 ? Color.GREEN : Color.RED;
              graphics.setColor(eventColor);
              graphics.fillRect((int) point.getX(), (int) point.getY(), 1, 1);
            }
          }
          ++index;
        }
      }
    } else
      System.err.println("unexpected image dimensions");
  }

  @Override
  public void timedImage(TimedImageEvent timedImageEvent) {
    imageCopy.update(timedImageEvent.bufferedImage);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    isSelected = jToggleButton.isSelected();
  }
}
