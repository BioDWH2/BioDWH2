package de.unibi.agbi.biodwh2.core;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import de.unibi.agbi.biodwh2.core.exceptions.DataSourceException;

public class ParallelBehaviourTest {

	public static void main(String[] args) {

		System.out.println("--- TESTING PARALLEL BEHAVIOUR ---");
		// mock workspace and data sources
		Workspace workspace = new Workspace("");
		final String[] ids = { "Mock1", "Mock2", "Mock3" };
		final DataSource[] datasources = new DataSourceLoader().getDataSources(ids);
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		
		// => SEQUENTIAL
		long start = System.currentTimeMillis();
		for(DataSource ds : datasources) {
			try {
				ds.prepare(workspace);
			} catch (DataSourceException e) {
				e.printStackTrace();
			}
			ds.parse(workspace);
			ds.export(workspace);
		}
		long stop = System.currentTimeMillis();
		long elapsed = stop - start;
		System.out.println("=> SEQUENTIAL TIME TAKEN: " + elapsed + " ms ( " + df.format((float) elapsed / 1000) + " SECONDS)");
		
		// => PARALLEL
		start = System.currentTimeMillis();
		Stream.of(datasources).parallel().forEach(
				ds -> {
					try {
						ds.prepare(workspace);
					} catch (DataSourceException e) {
						e.printStackTrace();
					}
					ds.parse(workspace);
					ds.export(workspace);				
				});
		stop = System.currentTimeMillis();
		elapsed = stop - start;
		System.out.println("=> PARALLEL TIME TAKEN: " + elapsed + " ms (" + df.format((float) elapsed / 1000) + " SECONDS)");

		// cleanup ...
		try {
			FileUtils.forceDelete(new File("./sources/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
