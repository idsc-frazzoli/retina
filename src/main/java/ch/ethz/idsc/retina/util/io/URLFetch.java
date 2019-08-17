// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/URLFetch.html">URLFetch</a> */
public class URLFetch implements AutoCloseable {
  private final HttpURLConnection httpURLConnection;
  private final String contentType;
  private final int contentLength;

  /** @param url
   * @throws IOException */
  public URLFetch(URL url) throws IOException {
    httpURLConnection = (HttpURLConnection) url.openConnection();
    int responseCode = httpURLConnection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      contentType = httpURLConnection.getContentType();
      contentLength = httpURLConnection.getContentLength();
    } else
      throw new IOException("" + responseCode);
  }

  public URLFetch(String url) throws IOException {
    this(new URL(url));
  }

  /** @param file to download web content to
   * @throws IOException */
  public void downloadIfNotExists(File file) throws IOException {
    if (file.isFile() && file.length() == contentLength)
      return;
    download(file);
  }

  /** @param file
   * @throws IOException if function was already called */
  public void download(File file) throws IOException {
    byte[] buffer = new byte[4096]; // buffer size
    try (InputStream inputStream = httpURLConnection.getInputStream()) {
      try (OutputStream outputStream = new FileOutputStream(file)) {
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1)
          outputStream.write(buffer, 0, bytesRead);
      }
    }
    httpURLConnection.disconnect();
  }

  /** @return number of bytes to download */
  public int contentLength() {
    return contentLength;
  }

  /** @return */
  public String contentType() {
    return contentType;
  }

  @Override // from AutoCloseable
  public void close() throws IOException {
    httpURLConnection.disconnect();
  }
}