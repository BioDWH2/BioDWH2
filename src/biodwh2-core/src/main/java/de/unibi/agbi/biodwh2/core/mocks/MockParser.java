package de.unibi.agbi.biodwh2.core.mocks;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;

public class MockParser<T extends DataSource> extends Parser<T> {
	
    public MockParser(final T dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) {
    	System.out.println("PARSED " + dataSource.getClass().getSimpleName());
        return true;
    }
}
