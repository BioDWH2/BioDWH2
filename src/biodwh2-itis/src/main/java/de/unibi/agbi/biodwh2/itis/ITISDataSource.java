package de.unibi.agbi.biodwh2.itis;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;

import de.unibi.agbi.biodwh2.itis.etl.*;
import de.unibi.agbi.biodwh2.itis.model.*;

import java.util.List;
import java.util.Map;

public class ITISDataSource extends DataSource {
    public List<Comment> comments;
    public List<Expert> experts;
    public List<GeographicDivision> geographicDivisions;
    public List<Hierarchy> hierarchies;
    public List<Jurisdiction> jurisdictions;
    public List<Kingdom> kingdoms;
    public Map<Integer, String> longNames;
    public Map<Integer, String> nodcIds;
    public List<OtherSource> otherSources;
    public List<Publication> publications;
    public List<ReferenceLink> referenceLinks;
    public Map<Integer, Integer> synonymLinks;
    public List<TaxonAuthorLkp> taxonAuthorsLkps;
    public List<TaxonUnitType> taxonUnitTypes;
    public List<TaxonomicUnit> taxonomicUnits;
    public List<TaxonomicUnitCommentLink> taxonomicUnitCommentLinks;
    public List<VernacularReferenceLink> vernacularReferenceLinks;
    public List<Vernacular> vernaculars;

    @Override
    public String getId() {
        return "ITIS";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<ITISDataSource> getUpdater() {
        return new ITISUpdater(this);
    }

    @Override
    public Parser<ITISDataSource> getParser() {
        return new ITISParser(this);
    }

    @Override
    public GraphExporter<ITISDataSource> getGraphExporter() {
        return new ITISGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ITISMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        comments = null;
        experts = null;
        geographicDivisions = null;
        hierarchies = null;
        jurisdictions = null;
        kingdoms = null;
        longNames = null;
        nodcIds = null;
        otherSources = null;
        publications = null;
        referenceLinks = null;
        synonymLinks = null;
        taxonAuthorsLkps = null;
        taxonUnitTypes = null;
        taxonomicUnits = null;
        taxonomicUnitCommentLinks = null;
        vernacularReferenceLinks = null;
        vernaculars = null;
    }
}
