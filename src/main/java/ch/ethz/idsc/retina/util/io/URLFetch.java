// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ch.ethz.idsc.tensor.io.Timing;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/URLFetch.html">URLFetch</a> */
public class URLFetch {
  private final URL url;
  private final ContentType contentType;

  /** @param url
   * @param contentType */
  public URLFetch(URL url, ContentType contentType) {
    this.url = url;
    this.contentType = contentType;
  }

  /** @param url
   * @param contentType
   * @throws MalformedURLException */
  public URLFetch(String url, ContentType contentType) throws MalformedURLException {
    this(new URL(url), contentType);
  }

  /** @param file to download web content to
   * @throws IOException */
  public void to(File file) throws IOException {
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    int responseCode = httpURLConnection.getResponseCode();
    // always check HTTP response code first
    if (responseCode == HttpURLConnection.HTTP_OK) {
      // tests show that often: disposition == null
      String disposition = httpURLConnection.getHeaderField("Content-Disposition");
      String content_type = httpURLConnection.getContentType();
      if (contentType.matches(content_type)) {
        int contentLength = httpURLConnection.getContentLength();
        System.out.println("Content-Type = " + content_type);
        System.out.println("Content-Disposition = " + disposition);
        System.out.println("Content-Length = " + contentLength);
        if (file.isFile() && //
            file.length() == contentLength) {
          System.out.println("file exists and has same size.");
        } else {
          byte[] buffer = new byte[4096]; // buffer size
          // opens input stream from the HTTP connection
          Timing timing = Timing.started();
          try (InputStream inputStream = httpURLConnection.getInputStream()) {
            // opens an output stream to save into file
            try (OutputStream outputStream = new FileOutputStream(file)) {
              int bytesRead = -1;
              while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);
              System.out.println("url downloaded in " + timing.seconds() + "[s]");
            }
          }
        }
      } else {
        httpURLConnection.disconnect();
        throw new RuntimeException(content_type);
      }
    } else {
      System.err.println("No file to download. Server replied HTTP code: " + responseCode);
    }
    httpURLConnection.disconnect();
  }

  /** @return true if URL is available for download
   * @throws IOException */
  public boolean ping() throws IOException {
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    int responseCode = httpURLConnection.getResponseCode();
    // always check HTTP response code first
    if (responseCode == HttpURLConnection.HTTP_OK) {
      // tests show that often: disposition == null
      String content_type = httpURLConnection.getContentType();
      return contentType.matches(content_type);
    }
    return false;
  }
}