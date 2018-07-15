package in.nimbo;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;


public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            unUseCertificateForSSL();

            ShellFactory.createConsoleShell("RSS Reader", "Enter '?list' to list all commands",
                    new App()).commandLoop();


        } catch (Exception e) {
            logger.error("Program Exited With Error", e);
            System.out.println("There Were Some Problems! :(  Please See Log File For More Information");
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
        try {
            SiteUpdater siteUpdater = new SiteUpdater(new URL(rssLink));
            siteUpdater.update();
        } catch (MalformedURLException e) {
            System.out.println("Please Enter a valid RSS URL");
        } catch (Exception e) {
            logger.warn("Crawling was unsuccessful for site " + rssLink, e);
            System.out.println("Crawling failed. for more information see the logs!");
        }
    }

    @Command(description = "add or update site configs")
    public void addConfig(@Param(name = "site link") String siteLink,
                          @Param(name = "body pattern") String bodyPattern) {
        SiteConfig siteConfig = new DatabaseSiteConfig(siteLink, bodyPattern, null);
        try {
            siteConfig.save();
            System.out.println("Adding config was successful");
        } catch (Exception e) {
            logger.warn("saving config was unsuccessful for site " + siteLink, e);
            System.out.println("Adding Config Failed. for more information see the logs!");
        }
    }

    @Command
    public void addConfig(@Param(name = "site link") String siteLink, @Param(name = "body pattern") String bodyPattern,
                          @Param(name = "ad patterns") String adPatterns) {
        SiteConfig siteConfig = new DatabaseSiteConfig(siteLink, bodyPattern, adPatterns.split(";"));
        try {
            siteConfig.save();
            System.out.println("Adding config was successful");
        } catch (Exception e) {
            logger.warn("saving config was unsuccessful for site " + siteLink, e);
            System.out.println("Adding config failed. for more information see the logs!");
        }
    }
}
