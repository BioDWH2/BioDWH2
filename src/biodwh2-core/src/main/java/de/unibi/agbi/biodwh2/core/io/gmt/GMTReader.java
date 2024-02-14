package de.unibi.agbi.biodwh2.core.io.gmt;

import de.unibi.agbi.biodwh2.core.io.BaseReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class GMTReader extends BaseReader<GeneSet> {
    public GMTReader(final String filePath, final Charset charset) throws IOException {
        super(filePath, charset);
    }

    public GMTReader(final InputStream stream) {
        super(stream, StandardCharsets.UTF_8);
    }

    public GMTReader(final InputStream stream, final Charset charset) {
        super(stream, charset);
    }

    @Override
    protected GeneSet readNextEntry() {
        String line;
        while ((line = readLineSafe()) != null) {
            if (StringUtils.isBlank(line))
                continue;
            final String[] parts = StringUtils.split(line, '\t');
            if (parts.length < 2)
                continue;
            final String[] genes = Arrays.stream(parts).skip(2).filter(StringUtils::isNotEmpty).toArray(String[]::new);
            return new GeneSet(parts[0], parts[1], genes);
        }
        return null;
    }
}
