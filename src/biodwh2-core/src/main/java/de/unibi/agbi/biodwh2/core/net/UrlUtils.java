package de.unibi.agbi.biodwh2.core.net;

import java.io.IOException;

public final class UrlUtils {
    private static final String[] BROWSERS = {
            "google-chrome", "firefox", "mozilla", "epiphany", "konqueror", "netscape", "opera", "links", "lynx"
    };
    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean openInBrowser(final String url) {
        try {
            final Runtime rt = Runtime.getRuntime();
            if (OS.contains("win")) {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                return true;
            } else if (OS.contains("mac")) {
                rt.exec("open " + url);
                return true;
            } else if (OS.contains("nix") || OS.contains("nux")) {
                final var cmd = new StringBuilder();
                for (int i = 0; i < BROWSERS.length; i++) {
                    if (i > 0)
                        cmd.append(" || ");
                    cmd.append(BROWSERS[i]).append(" \"").append(url).append('"');
                }
                rt.exec(new String[]{"sh", "-c", cmd.toString()});
                return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }
}
