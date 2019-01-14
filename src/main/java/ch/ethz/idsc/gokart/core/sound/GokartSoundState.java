// code by mh
package ch.ethz.idsc.gokart.core.sound;

public class GokartSoundState {
  public final float speed;
  public final float power;
  public final float torquevectoring;

  public GokartSoundState(float speed, float power, float torquevectoring) {
    this.speed = speed;
    this.power = power;
    this.torquevectoring = torquevectoring;
  }
}