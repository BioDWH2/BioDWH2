package de.unibi.agbi.biodwh2.core.io;

import de.unibi.agbi.biodwh2.core.model.Version;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

public final class ResourceUtils {
    private ResourceUtils() {
    }

    public static Version getManifestBioDWH2Version() {
        try {
            final ClassLoader classLoader = ResourceUtils.class.getClassLoader();
            final Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                final Manifest manifest = new Manifest(resources.nextElement().openStream());
                final Version version = Version.tryParse(manifest.getMainAttributes().getValue("BioDWH2-version"));
                if (version != null)
                    return version;
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
