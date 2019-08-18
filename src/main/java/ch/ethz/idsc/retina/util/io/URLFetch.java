// TODO JPH TENSOR 078 obsolete
// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** Example:
 * <pre>
 * try (URLFetch urlFetch = new URLFetch(new URL("http://www.hakenberg.de/favicon.ico"))) {
 * urlFetch.downloadIfMissing(HomeDirectory.file("favicon.ico"));
 * }
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/URLFetch.html">URLFetch</a> */
public class URLFetch implements AutoCloseable {
  private final HttpURLConnection httpURLConnection;
  private final String contentType;
  private final int length;

  /** @param url
   * @throws IOException */
  public URLFetch(URL url) throws IOException {
    httpURLConnection = (HttpURLConnection) url.openConnection();
    int responseCode = httpURLConnection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      contentType = httpURLConnection.getContentType();
      length = httpURLConnection.getContentLength();
    } else {
      httpURLConnection.disconnect();
      throw new IOException("" + responseCode);
    }
  }

  public URLFetch(String url) throws IOException {
    this(new URL(url));
  }

  /** @return
   * @throws IOException */
  public InputStream inputStream() throws IOException {
    return httpURLConnection.getInputStream();
  }

  /** @param file to download web content to if file does not already exist,
   * or has the wrong length
   * @throws IOException */
  public void downloadIfMissing(File file) throws IOException {
    if (file.isFile() && //
        file.length() == length)
      return;
    download(file);
  }

  /** @param file to download web content to
   * @throws IOException if function was already called */
  public void download(File file) throws IOException {
    try (InputStream inputStream = httpURLConnection.getInputStream()) {
      try (OutputStream outputStream = new FileOutputStream(file)) {
        byte[] buffer = new byte[4096]; // buffer size
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1)
          outputStream.write(buffer, 0, bytesRead);
      }
    }
    httpURLConnection.disconnect();
  }

  /** @return number of bytes to download */
  public int length() {
    return length;
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