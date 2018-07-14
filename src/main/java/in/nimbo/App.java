package in.nimbo;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;
import com.rometools.rome.io.FeedException;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;


public class App {
    public static void main(String[] args) throws IOException, FeedException {
        try {
            unUseCertificateForSSL();

            ShellFactory.createConsoleShell("RSS Reader", "Enter '?list' to list all commands",
                    new App()).commandLoop();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void unUseCertificateForSSL() throws NoSuchAlgorithmException, KeyManagementException, IOException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = (hostname, session) -> true;

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    @Command
    public void crawl(@Param(name = "RSS Link", description = "rss link for site to crawl.") String rssLink) throws MalformedURLException {
        SiteUpdater siteUpdater = new SiteUpdater(new URL("https://www.mehrnews.com/rss.aspx"));
        siteUpdater.update();
    }
}
