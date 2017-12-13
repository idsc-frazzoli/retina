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

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Transforms coordinates from WGS84 to the new Swiss-Grid coordinate system.
 * 
 * @author boescpa
 * @author mrieser */
public enum WGS84toCH1903LV03Plus {
  ;
  public static Tensor transform(double coord_getX, double coord_getY) {
    double lonNorm = (coord_getX * 3600 - 26782.5) / 10000;
    double latNorm = (coord_getY * 3600 - 169028.66) / 10000;
    double CH1903X = 1200147.07 //
        + 308807.95 * latNorm + //
        3745.25 * Math.pow(lonNorm, 2) //
        + 76.63 * Math.pow(latNorm, 2) //
        - 194.56 * Math.pow(lonNorm, 2) * latNorm //
        + 119.79 * Math.pow(latNorm, 3);
    double CH1903Y = 2600072.37 //
        + 211455.93 * lonNorm //
        - 10938.51 * lonNorm * latNorm //
        - 0.36 * lonNorm * Math.pow(latNorm, 2) //
        - 44.54 * Math.pow(lonNorm, 3);
    /* Important Note: in the Swiss Grid, y describes easting and x describes
     * northing, contrary to the usual naming conventions! */
    return Tensors.vector(CH1903Y, CH1903X);
  }
}
