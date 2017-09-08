// code by swisstrolley+
package ch.ethz.idsc.retina.sys;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/** ModuleWatchdog runs every PERIOD_S and checks if all the modules that should
 * be running are running as given by their threads. If not it will try to
 * restart the module.
 *
 * The initialisation of this INSTANCE starts the watchdog. checkAlive is called
 * by the main method and will restart the module if the timeOut is exceeded.
 * This should be reliable as if the main fails... (it will never happen). */
public enum ModuleAuto {
  INSTANCE;
  // Maps for holding the module list
  private static Map<String, AbstractModule> moduleMap = new LinkedHashMap<>();
  private static Map<String, AbstractModule> bgModuleMap = new LinkedHashMap<>();
  private static Map<String, AbstractModule> watchList = new LinkedHashMap<>();
  // Variables for timed checking
  private double PERIOD_S = 1.0;
  private long lastRun = 0;
  private long timeOut = Math.round((PERIOD_S * 1000) * 2);
  private Timer timerThread;

  /** Methods for the watchdog */
  ModuleAuto() {
    launch();
  }

  private void launch() {
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        lastRun = System.currentTimeMillis();
        // try {
        // DO CHECKING HERE
        // watchList.forEach((s, aM) -> {
        // if (aM instanceof AbstractClockedModule) {
        // if (((AbstractClockedModule) aM).isLagging()) {
        // System.out.println(SystemTimestamp.termFmt() + "
        // ModuleWatchdog: " + s + " is not alive");
        // try {
        // terminateOne(aM.getClass());
        // runOne(aM.getClass());
        // } catch (Exception e) {
        // System.out.println("Error terminating");
        // }
        // }
        // return;
        // }
        //
        // System.out.println(aM.getThread().isAlive());
        // if (!aM.getThread().isAlive())
        // System.out.println(SystemTimestamp.termFmt() + "
        // ModuleWatchdog: " + s + " is not alive");
        // });
        // } catch (Exception e) {
        // System.out.println(SystemTimestamp.termFmt() + " ModuleWatchdog:
        // not okay");
        // return;
        // }
      }
    };
    timerThread = new Timer("ModuleWatchdog");
    System.out.println(new Date() + " ModuleWatchdog launching");
    timerThread.schedule(timerTask, 0, Math.round((PERIOD_S * 1000)));
  }

  /** Main thread calls this to poll the modules */
  public void checkAlive() {
    if (System.currentTimeMillis() - lastRun <= timeOut) {
      return;
    }
    System.out.println(new Date() + " ModuleWatchdog was dead, restarting");
    timerThread.cancel();
    launch();
  }

  /** Methods for launching the modules */
  public static void runAll(List<Class<?>> modules) {
    System.out.println(new Date() + " Module Auto: Launch all");
    for (Class<?> module : modules)
      runOne(module);
    System.out.println(new Date() + " Module Auto: Launch all done");
  }

  public static void terminateAll() {
    System.out.println(new Date() + " Module Auto: Terminate all");
    moduleMap.values().stream().parallel() //
        .forEach(AbstractModule::terminate);
    moduleMap.clear();
    System.out.println(new Date() + " Module Auto: Terminate all done");
  }

  public static void runOne(Class<?> module) {
    final String key = getKey(module);
    if (moduleMap.containsKey(key) || bgModuleMap.containsKey(key)) {
      System.out.println(new Date() + " Module Auto: Already launched: " + module);
      return;
    }
    AbstractModule instance;
    try {
      instance = (AbstractModule) module.newInstance();
      System.out.println(new Date() + " Module Auto: Launching: " + key);
      instance.launch();
      moduleMap.put(key, instance);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void runOne(Class<?> module, Boolean background) {
    final String key = getKey(module);
    if (moduleMap.containsKey(key) || bgModuleMap.containsKey(key)) {
      System.out.println(new Date() + " Module Auto: Already launched: " + module);
      return;
    }
    if (background) {
      AbstractModule instance;
      try {
        instance = (AbstractModule) module.newInstance();
        System.out.println(new Date() + " Module Auto: Launching in background: " + key);
        instance.launch();
        bgModuleMap.put(key, instance);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      runOne(module);
    }
  }

  public static void terminateOne(Class<?> module) {
    final String key = getKey(module);
    if (watchList.containsKey(key)) {
      watchList.remove(key);
    }
    if (moduleMap.containsKey(key)) {
      AbstractModule abstractModule = moduleMap.get(key);
      moduleMap.remove(key);
      System.out.println(new Date() + " Module Auto: Terminating: " + key);
      abstractModule.terminate();
    }
  }

  public static void watch(Class<?> module, AbstractModule abstractModule) {
    final String key = getKey(module);
    if (watchList.containsKey(key)) {
      System.out.println(new Date() + " Module Auto: Already watching: " + module);
      return;
    }
    watchList.put(key, abstractModule);
  }

  private static String getKey(Class<?> module) {
    return module.getName();
  }
  // static class ModuleProperties {
  // private AbstractModule abstractModule;
  // private Thread thread;
  // private long threadID;
  // private String threadName;
  // private long exceptionCount = 0;
  // private long failureCount = 0;
  // private boolean restart = true;
  //
  // ModuleProperties(AbstractModule abstractModule, Thread thread, long
  // threadID, String threadName) {
  // this.abstractModule = abstractModule;
  // this.thread = thread;
  // this.threadID = threadID;
  // this.threadName = threadName;
  // }
  //
  // boolean checkModule() {
  // return thread.isAlive();
  // }
  //
  // void restartModule() {
  // if (checkModule()) {
  // abstractModule.terminate();
  // }
  // }
  // }
}
