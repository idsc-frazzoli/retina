// code by jph
package ch.ethz.idsc.retina.lidar;

public enum LidarRayBlockEvents {
  ;
  public static String toInfoString(LidarRayBlockEvent lidarRayBlockEvent) {
    return String.format("d=%d f=%d i=%d", //
        lidarRayBlockEvent.dimensions, //
        lidarRayBlockEvent.floatBuffer.limit(), //
        lidarRayBlockEvent.byteBuffer.limit() //
    );
  }
}
