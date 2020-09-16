package de.unibi.agbi.biodwh2.itis;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;

import de.unibi.agbi.biodwh2.itis.etl.*;
import de.unibi.agbi.biodwh2.itis.model.*;

import java.util.List;

public class ITISDataSource extends DataSource {
    public List<Comment> comments;
    public List<Expert> experts;
    public List<GeographicDivision> geographicDivisions;
    public List<Hierarchy> hierarchies;
    public List<Jurisdiction> jurisdictions;
    public List<Kingdom> kingdoms;
    public List<LongName> longNames;
    public List<NodcId> nodcIds;
    public List<OtherSource> otherSources;
    public List<Publication> publications;
    public List<ReferenceLink> referenceLinks;
    public List<StrippedAuthor> strippedAuthors;
    public List<SynonymLink> synonymLinks;
    public List<TaxonAuthorLkp> taxonAuthorsLkps;
    public List<TaxonUnitType> taxonUnitTypes;
    public List<TaxonomicUnit> taxonomicUnits;
    public List<TuCommentLink> tuCommentsLinks;
    public List<VernacularReferenceLink> vernRefLinks;
    public List<Vernacular> vernaculars;

    @Override
    public String getId() {
        return "ITIS";
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
        strippedAuthors = null;
        synonymLinks = null;
        taxonAuthorsLkps = null;
        taxonUnitTypes = null;
        taxonomicUnits = null;
        tuCommentsLinks = null;
        vernRefLinks = null;
        vernaculars = null;
    }
}
