// code by niam jen wei
package ch.ethz.idsc.retina.sys;

import java.util.Date;
import java.util.Set;

import javax.swing.JToggleButton;

/** Class to return the individual thread status currently running in the JVM. */
public class TaskManagerStatus {
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

  public JToggleButton toggle;

  public TaskManagerStatus() {
    toggle = new JToggleButton("Thread Status");
    toggle.addActionListener(e -> {
      boolean isSelected = toggle.isSelected();
      if (isSelected) {
        toggle.setSelected(false);
        countThreads();
        getThreadStatus();
      }
    });
  }
}