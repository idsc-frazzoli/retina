// code by jph
package ch.ethz.idsc.demo.jph.can;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ enum CanProcess {
  ;
  private static final String CAN_RX_EVENT = "CAN Rx Event";

  public static void main(String[] args) throws FileNotFoundException, IOException {
    File file = HomeDirectory.file("Documents", "steering", "active_operations.txt");
    // try (PrintWriter printWriter = new PrintWriter())
    TableBuilder tableBuilder = new TableBuilder();
    // TODO JPH TENSOR 075
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
      String prefix = "";
      int maxline = 1700_000;
      int linecount = 0;
      while (true) {
        String line = bufferedReader.readLine();
        ++linecount;
        if (Objects.nonNull(line) && linecount < maxline) {
          if (line.endsWith(CAN_RX_EVENT)) {
            prefix += line.substring(0, line.length() - 12);
          } else {
            String string = prefix + line;
            StringTokenizer stringTokenizer = new StringTokenizer(string);
            // System.out.println(string);
            // string.indexOf("flags")
            try {
              String scount = stringTokenizer.nextToken();
              int count = Integer.parseInt(scount.substring(1, scount.length() - 1));
              String sid = stringTokenizer.nextToken();
              int id = Integer.parseInt(sid.substring(3));
              String dlc = stringTokenizer.nextToken();
              int length = Integer.parseInt(dlc.substring(4));
              // String data =
              stringTokenizer.nextToken(); // data:
              // StringBuilder stringBuilder = new StringBuilder();
              List<Integer> data = new ArrayList<>();
              for (int index = 0; index < length; ++index)
                data.add(Integer.parseInt(stringTokenizer.nextToken(), 16));
              // String flags =
              stringTokenizer.nextToken(); // flags:
              String stime = stringTokenizer.nextToken();
              int time = Integer.parseInt(stime.substring(5));
              tableBuilder.appendRow(Tensors.vector(time, count, id), Tensors.vector(data));
              // String format = Join.of().toString();
              // String.format("%7d%7d%4d%26s", time, count, id, stringBuilder);
              // if (id == 10)
              // {
              // System.out.println(format);
              // printWriter.println(format);
              // }
            } catch (Exception exception) {
              System.err.println(string);
            }
            prefix = "";
          }
        } else
          break;
      }
    }
    String name2 = file.getName();
    name2 = name2.substring(0, name2.length() - 4) + ".csv";
    Export.of(HomeDirectory.file(name2), tableBuilder.toTable());
  }
}
