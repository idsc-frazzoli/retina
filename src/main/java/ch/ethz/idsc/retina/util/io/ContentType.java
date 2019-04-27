/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package ch.ethz.idsc.retina.util.io;

public enum ContentType {
  APPLICATION_ZIP("application/zip"), //
  APPLICATION_OCTETSTREAM("application/octet-stream"), //
  TEXT_HTML("text/html"), //
  IMAGE_XICON("image/x-icon"), //
  ;
  private final String expression;

  private ContentType(String expression) {
    this.expression = expression;
  }

  public boolean matches(String string) {
    // text/html; charset=UTF-8
    String first = string.split(";")[0];
    return expression.equalsIgnoreCase(first);
  }
}
