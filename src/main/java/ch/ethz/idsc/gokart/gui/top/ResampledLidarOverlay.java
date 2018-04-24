package ch.ethz.idsc.gokart.gui.top;

public enum ResampledLidarOverlay {
  ;
  // BufferedImage biglobal = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
  // LidarRayBlockListener lrbl = new LidarRayBlockListener() {
  // @Override
  // public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
  // int dim = lidarRayBlockEvent.dimensions;
  // System.out.println("lidar dim=" + dim);
  // FloatBuffer fb = lidarRayBlockEvent.floatBuffer;
  // int length = fb.remaining();
  // GeometricLayer gl = new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));
  // Tensor state = mappedPoseInterface.getPose(); // {x[m],y[m],angle[]}
  // Tensor pose = GokartPoseHelper.toSE2Matrix(state);
  // BufferedImage bi = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
  // Graphics2D gfx = bi.createGraphics();
  // // gfx.setColor(Color.black);
  // // gfx.fillRect(0, 0, 640, 640);
  // gfx.setColor(Color.red);
  // gl.pushMatrix(pose);
  // gl.pushMatrix(LIDAR);
  // SpacialObstaclePredicate spacialObstaclePredicate = SimpleSpacialObstaclePredicate.createVlp16();
  // for (int count = 0; count < length; count += 3) {
  // Tensor x = Tensors.vectorDouble(fb.get(), fb.get(), fb.get());
  // if (spacialObstaclePredicate.isObstacle(x)) {
  // Point2D p = gl.toPoint2D(x);
  // gfx.fillRect((int) p.getX(), (int) p.getY(), 1, 1);
  // }
  // }
  // gl.popMatrix();
  // gl.popMatrix();
  // biglobal = bi;
  // }
  // };
  // graphics.drawImage(biglobal, 0, 0, null);
}
