// code by niam jen wei
// modified by jph
package ch.ethz.idsc.retina.util.sys;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Set;

import javax.swing.JButton;

/** Class to return the individual thread status currently running in the JVM. */
public class TaskManagerStatus implements ActionListener {
  public static String countThreads() {
    StringBuilder msg = new StringBuilder();
    msg.append("Total threads: " + Thread.activeCount() + "\n");
    System.out.println(new Date() + msg.toString());
    return msg.toString();
  }

  public static String getThreadStatus() {
    StringBuilder msg = new StringBuilder();
    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
    for (Thread t : threadSet) {
      if (t.getThreadGroup() == Thread.currentThread().getThreadGroup()) {
        msg.append("Thread: " + t.getName() + //
            " | state: " + t.getState() + //
            " | pid: " + t.getId() + //
            "\n");
      }
    }
    System.out.println(new Date() + msg.toString());
    return msg.toString();
  }

  // ---
  public final JButton jButton = new JButton("Status");

  public TaskManagerStatus() {
    jButton.addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    countThreads();
    getThreadStatus();
  }
}