// code by mh
package ch.ethz.idsc.gokart.core.sound;

public class GokartSoundState {
  public final float speed;
  public final float power;
  public final float torqueVectoring;

  public GokartSoundState(float speed, float power, float torqueVectoring) {
    this.speed = speed;
    this.power = power;
    this.torqueVectoring = torqueVectoring;
  }
}