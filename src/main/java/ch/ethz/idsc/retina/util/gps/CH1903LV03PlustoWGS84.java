/*
 * *********************************************************************** *
 * project: org.matsim.*                                                   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** *
 */
package ch.ethz.idsc.retina.util.gps;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** Transforms coordinates from the new Swiss-Grid coordinate system to WGS84.
 *
 * @author boescpa
 * @author mrieser */
public enum CH1903LV03PlustoWGS84 {
  ;
  /** .
   * @param coord_getX quantity with unit compatible with [m]
   * @param coord_getY quantity with unit compatible with [m]
   * @return */
  public static Tensor transform(Scalar coord_getX, Scalar coord_getY) {
    return transform( //
        Magnitude.METER.toDouble(coord_getX), //
        Magnitude.METER.toDouble(coord_getY));
  }

  // function is the original from Matsim except the return statement and type
  private static Tensor transform(double coord_getX, double coord_getY) {
    /* Important Note: in the Swiss Grid, y describes easting and x describes
     * northing, contrary to the usual naming conventions! */
    double yNorm = (coord_getX - 2600000.0) / 1000000.0;
    double xNorm = (coord_getY - 1200000.0) / 1000000.0;
    double longitude10000Sec = 2.6779094 //
        + 4.728982 * yNorm //
        + 0.791484 * yNorm * xNorm //
        + 0.1306 * yNorm * Math.pow(xNorm, 2) //
        - 0.0436 * Math.pow(yNorm, 3);
    double latitude10000Sec = 16.9023892 //
        + 3.238272 * xNorm //
        - 0.270978 * Math.pow(yNorm, 2) //
        - 0.002528 * Math.pow(xNorm, 2) //
        - 0.0447 * Math.pow(yNorm, 2) * xNorm //
        - 0.0140 * Math.pow(xNorm, 3);
    return Tensors.of( //
        Quantity.of(longitude10000Sec * 100.0 / 36.0, NonSI.DEGREE_ANGLE), //
        Quantity.of(latitude10000Sec * 100.0 / 36.0, NonSI.DEGREE_ANGLE));
  }
}
