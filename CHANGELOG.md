# Changelog

## 📦 Version [v0.6.1](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.6.1)

Released: upcoming

### 📚 Data Sources

* ```[HGNC]``` Fix HGNC updater with new download URL and version retrieval
* ```[PharmGKB]``` Ignore missing genes for variant annotations

## 📦 Version [v0.6.0](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.6.0)

Released: 27.08.2024

### 📚 Data Sources

* ```[HPO]``` Split HPO data source into HPO and HPOAnnotations
* ```[GeneOntology]``` Split GeneOntology data source into GeneOntology and GeneOntologyAnnotations
* ```[AgronomyOntology]``` Add agronomy ontology data source
* ```[PathwayCommons]``` Fix download URL
* ```[miRBase]``` Fix updater and graph exporter
* ```[IntAct]``` Add more species downloads configurable using speciesFilter
* ```[CMAUP]``` Fix data model
* ```[RNALocate]``` Fix data model and download
* ```[ITIS]``` Fix version retrieval

### 🔧 Bug fixes and other changes

* ```[feature]``` Modularize output format writers
* ```[feature]``` Add GEXF output format writer
* ```[feature]``` Add JSONGraph output format writer
* ```[feature]``` Introduce ontology proxy nodes resolved during graph merging
* ```[feature]``` Add OBO Foundry ontology data source abstraction
* ```[feature]``` Add automatic ROBOT tool download and OWL to OBO conversion
* ```[feature]``` Validate downloaded file sizes if size is known
* ```[feature]``` Finish web configurator API
* ```[feature]``` All config parameters can be set via command line
* ```[fix]``` Fix GraphMapper path traversal going back already traversed edges in some scenarios

## 📦 Version [v0.5.1](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.5.1)

Released: 24.05.2024

### 📚 Data Sources

* ```[TarBase]``` Mark TarBase as usable
* ```[USDA-PLANTS]``` Implement new version number for USDA-PLANTS
* ```[nSIDES]``` Rename Tatonetti2012 data source module to nSIDES and update OFFSIDES/TWOSIDES data

### 🔧 Bug fixes and other changes

* ```[feature]``` Expose unsafe iterators in MVStoreCollection
* ```[feature]``` Export OAI datasource identifiers in mapped graph metadata nodes

## 📦 Version [v0.5.0](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.5.0)

Released: 16.05.2024

### 📚 Data Sources

* ```[Mondo]``` Mondo data source ID is now MondoDiseaseOntology
* ```[EFO]``` EFO data source ID is now ExperimentalFactorOntology and marked as usable
* ```[GenCC]``` Mark GenCC as usable
* ```[HMDB]``` Mark HMDB as usable
* ```[RNAInter]``` Mark RNAInter as usable
* ```[HERB]``` Mark HERB as usable
* ```[CMAUP]``` Mark CMAUP as usable
* ```[IID]``` Mark IID as usable
* ```[CancerDrugsDB]``` Fix download URLs
* ```[ReDO-DB]``` Fix download URLs
* ```[ReDOTrialsDB]``` Fix download URLs
* ```[InterPro]``` Fix data model
* ```[PharmGKB]``` Fix PharmGKB exporter with newly missing data
* ```[Negatome]``` Switch updater to internet archive as database is no longer online
* ```[HPRD]``` Switch updater to internet archive as database is no longer online
* ```[DGIdb]``` Fix updater and data model and mark as usable
* ```[Mir2disease]``` Fix Mir2disease parser
* ```[PlantTraitOntology]``` Add PlantTraitOntology data source module
* ```[PlantOntology]``` Add PlantOntology data source module
* ```[PlantStressOntology]``` Add PlantStressOntology data source module
* ```[BIONDA]``` Add BIONDA data source module
* ```[TheMarker]``` Add TheMarker data source module
* ```[MarkerDB]``` Add MarkerDB data source module
* ```[ChEBI]``` Add ChEBI data source module
* ```[DrugMap]``` Add DrugMap data source module

### 🔧 Bug fixes and other changes

* ```[feature]``` Gzip GraphML exports to save disk space
* ```[feature]``` Species filters can now be configured per data source
* ```[feature]``` Add download progress reporting for all updaters
* ```[feature]``` Allow additional properties in path mapping descriptions
* ```[feature]``` Add addEdgeFromModel to graph
* ```[feature]``` Add MySQL parser and converter to TSV
* ```[feature]``` Update data sources without version only once per day
* ```[fix]``` Fix graph exporter re-runs despite nothing changed
* ```[fix]``` Improve graph merge speed
* ```[fix]``` Fix updater not reporting a new version error
* ```[chore]``` Switch to Java 11
* ```[chore]``` Update dependencies
* ```[chore]``` Update docs

## 📦 Version [v0.4.15](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.15)

Released: 23.01.2024

### 📚 Data Sources

* ```[PharmGKB]``` Update PharmGKB data source model
* ```[CMAUP]``` Update CMAUP data source model
* ```[UNII]``` Update UNII data source model
* ```[EMA]``` Update EMA updater URLs
* ```[BRENDA]``` Add BRENDA mappings and mark module as usable
* ```[PathogenTransmissionOntology]``` Update PathogenTransmissionOntology data source module
* ```[SymptomOntology]``` Add SymptomOntology data source module
* ```[TarBase]``` Update TarBase to version 9

### 🔧 Bug fixes and other changes

* ```[feature]``` Handle duplicate SubsetDef and SynonymTypeDef in ontology graph exporter

## 📦 Version [v0.4.14](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.14)

Released: 19.01.2024

### 📚 Data Sources

* ```[HPO]``` #24 Fix HPO data model
* ```[OMIM]``` #57 Fix OMIM updater
* ```[TTD]``` #69 Fix TTD data model
* ```[HMDB]``` #92 Add HMDB data source module
* ```[CPDB]``` Add CPDB data source module
* ```[Tatonetti2012]``` Add OFFSIDES/TWOSIDES data source module
* ```[BioM2MetDisease]``` Add BioM2MetDisease data source module
* ```[miR2Disease]``` Add miR2Disease data source module
* ```[MassSpectrometryOntology]``` Add MassSpectrometryOntology data source module
* ```[BiologicalSpatialOntology]``` Add BiologicalSpatialOntology data source module
* ```[NeuroBehaviourOntology]``` Add NeuroBehaviourOntology data source module
* ```[ProteinModificationOntology]``` Add ProteinModificationOntology data source module
* ```[BRENDATissueOntology]``` Add BRENDATissueOntology data source module
* ```[CellOntology]``` Add CellOntology data source module
* ```[UnitOntology]``` Add UnitOntology data source module
* ```[MammalianPhenotypeOntology]``` Add MammalianPhenotypeOntology data source module
* ```[PhenotypeAndTraitOntology]``` Add PhenotypeAndTraitOntology data source module
* ```[UberonOntology]``` Add UberonOntology data source module

### 🔧 Bug fixes and other changes

* ```[feature]``` Add retries to HTTPClient getWebsiteSource
* ```[feature]``` Generic OBO ontology updater uses data as fallback if data-version is missing
* ```[feature]``` Graph mapper now uses node ids instead of nodes improving performance
* ```[chore]``` Update dependencies
* ```[chore]``` Update docs

## 📦 Version [v0.4.13](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.13)

Released: 16.05.2023

### 📚 Data Sources

* ```[DrugBank]``` #9 BREAKING: Change external identifier and links from nodes to properties
* ```[DrugBank]``` #9 Deduplicate DrugBank dosages
* ```[HGNC]``` #13 BREAKING: Change HGNC property hgnc_id to id
* ```[HGNC]``` #13 BREAKING: Change HGNC CODES_FOR edge to TRANSLATES_TO
* ```[HGNC]``` #13 Add TRANSCRIBES_TO path mapping
* ```[TTD]``` #69 Add TTD mapping descriptions
* ```[GenCC]``` #70 Fix GenCC submitted PMIDs formatting issues
* ```[HPRD]``` #89 Add HPRD path mapping descriptions

### 🔧 Bug fixes and other changes

* ```[fix]``` Improve DOI extraction using regex

## 📦 Version [v0.4.12](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.12)

Released: 20.04.2023

### 📚 Data Sources

* ```[PharmGKB]``` #8 Fix PharmGKB arrays and move most parsing to jackson

### 🔧 Bug fixes and other changes

* ```[chore]``` Update dependencies
* ```[chore]``` Update docs

## 📦 Version [v0.4.11](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.11)

Released: 13.04.2023

### 📚 Data Sources

* ```[PharmGKB]``` #8 Fix PharmGKB array delimiters and optional array quoting

### 🔧 Bug fixes and other changes

* ```[fix]``` Class mapping quoted array elements now works with optional quoting
* ```[feature]``` Add ability to declare data source dependencies
* ```[feature]``` Ontology data sources are now processed first
* ```[feature]``` Add adjacency highlighting on hover to echarts meta graph visualization
* ```[chore]``` Update dependencies

## 📦 Version [v0.4.9](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.9)

Released: 11.04.2023

### 📚 Data Sources

* ```[PharmGKB]``` #8 Fix PharmGKB data model and exporter
* ```[HPO]``` #24 Fix HPO data model and exporter
* ```[UNII]``` #29 Fix UNII data model and exporter
* ```[USDA-PLANTS]``` #35 Fix USDA-PLANTS update url
* ```[TTD]``` #69 Fix TTD array properties and placeholder
* ```[GenCC]``` #70 Add GenCC gene and disease mapping
* ```[TarBase]``` #73 Map TarBase genes to RNA where Genbank mRNA ids are available
* ```[miRDB]``` #78 Replace miRDB Gene nodes with mRNA nodes as it provides Genbank mRNA ids 
* ```[DISEASES]``` #81 Add DISEASES data source module
* ```[RefSeq]``` #82 Add RefSeq data source module
* ```[RNALocate]``` #83 Add RNALocate data source module
* ```[RNADisease]``` #84 Add RNADisease data source module
* ```[SequenceOntology]``` #85 Add SequenceOntology data source module
* ```[ENZYME]``` #86 Add ENZYME data source module
* ```[PROSITE]``` #87 Add PROSITE data source module
* ```[BRENDA]``` #88 Add BRENDA data source module
* ```[HPRD]``` #89 Add HPRD data source module
* ```[DiseaseOntology]``` #90 Add DiseaseOntology data source module
* ```[BasicFormalOntology]``` #91 Add BasicFormalOntology data source module

### 🔧 Bug fixes and other changes

* ```[fix]``` **Breaking change** of ENSEMBL_Gene mapping identifier type to ENSEMBL
* ```[fix]``` Fix identifier java types in many data source modules
* ```[feature]``` Check expected java type for identifier types in node mapping
* ```[feature]``` Switch generated html meta graph visualization to echarts
* ```[feature]``` Html meta graph visualization shows node/edge counts
* ```[feature]``` Html meta graph visualization shows node categories which can be toggled
* ```[feature]``` Path mapping edges now expose the path edge ids as a property
* ```[feature]``` Node mappings now allow more properties via specific node mapping descriptions
* ```[feature]``` Add RNA type node mapping property
* ```[feature]``` Speedup meta graph edge generation
* ```[feature]``` Move from logback to log4j2 as a logging framework
* ```[feature]``` Add minimal, modified h2-mvstore to project
* ```[chore]``` Update dependencies
* ```[chore]``` Update documentation
* ```[chore]``` Add third party license readme

## 📦 Version [v0.4.8](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.8)

Released: 05.12.2022

### 🔧 Bug fixes and other changes

* ```[fix]``` Fix workspace creation with species filter

## 📦 Version [v0.4.7](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.7)

Released: 04.12.2022

### 📚 Data Sources

* ```[NDF-RT]``` #11 Fix NDF-RT updater
* ```[HGNC]``` #13 Change HGNC entrez id to Integer and export miRNA nodes with mapping
* ```[DrugCentral]``` #18 Fix DrugCentral updater and export new Vet information
* ```[UNII]``` #29 Fix UNII updater and data model
* ```[ClinicalTrials.gov]``` #50 Add ClinicalTrials.gov data source module, superseding AACT module
* ```[Gene2Phenotype]``` #51 Add new Cardiac file to Gene2Phenotype
* ```[ADReCS]``` #60 Fix ADReCS updater and data model
* ```[TTD]``` #69 Add TTD data source module
* ```[T3DB]``` #72 Add T3DB data source module
* ```[TarBase]``` #73 Add TarBase data source module
* ```[miRBase]``` #74 Add miRBase data source module
* ```[miRTarBase]``` #75 Add miRTarBase data source module
* ```[RNAInter]``` #77 Add RNAInter data source module
* ```[miRDB]``` #78 Add miRDB data source module
* ```[TRRUST]``` #79 Add TRRUST data source module

### 🔧 Bug fixes and other changes

* ```[docs]``` Update docs
* ```[feature]``` Improve meta graph generation performance
* ```[feature]``` Add retries and more date formats to MultiFileFTPWebUpdater
* ```[feature]``` XLSX mapping iterator is now available in core module
* ```[feature]``` Add FASTA file format reader to core module
* ```[feature]``` Add FlatFile format reader to core module
* ```[feature]``` Add Collection utilities to core module
* ```[feature]``` Add SpeciesLookup to core module
* ```[feature]``` BaseGraph now supports getNodeIds and getEdgeIds methods
* ```[feature]``` Add GraphNumberProperty annotation for class mapping
* ```[feature]``` Exports now store a config properties hash to re-export on config changes
* ```[feature]``` Add global species filter to config file (data source modules will need to support it)
* ```[feature]``` Add GraphNumberProperty annotation for class mapping
* ```[feature]``` Fix html encoded characters for HTTPFTPClient
* ```[chore]``` Update dependencies

## 📦 Version [v0.4.6](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.6)

Released: 12.09.2022

### 🔧 Bug fixes and other changes

* ```[feature]``` Move functions from Graph to BaseGraph

## 📦 Version [v0.4.5](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.5)

Released: 12.09.2022

### 📚 Data Sources

* ```[Negatome]``` #67 Add Negatome Pfam domains and relationships. Mark module as usable.
* ```[GenCC]``` #70 Add GenCC data source module

### 🔧 Bug fixes and other changes

* ```[feature]``` Allow delay of edge index creation to speedup graph exporter performance
* ```[chore]``` Update dependencies

## 📦 Version [v0.4.4](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.4)

Released: 22.08.2022

### 📚 Data Sources

 * ```[ADReCS]``` #60 Fix ADReCS new data format
 * ```[DrugCentral]``` #18 Map human targets to gene and protein and add drug targets edge mapping
 * ```[DrugBank]``` #9 Change organism id to integer (breaking change)
 * ```[DrugBank]``` #9 Only map polypeptide if human; Add path mapping for drug targets polypeptide
 * ```[InterPro]``` #68 Add InterPro data source module 

## 📦 Version [v0.4.3](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.3)

Released: 11.08.2022

### 📚 Data Sources

 * ```[ADReCS]``` #60 Add ADReCS data source module
 * ```[UNII]``` #29 Fix UNII updater for new website
 * ```[HGNC]``` #13 Remove previous symbols and add ensembl gene id from HGNC gene mapping
 * ```[PharmGKB]``` #8 Remove ensembl gene ids from PharmGKB gene mapping
 * ```[UNII]``` #29 Fix UNII species merging with NCBI taxid
 * ```[DrugCentral]``` #18 Remove redundancy in DrugCentral active ingredients
 * ```[AACT]``` #50 Fix updated AACT schema model
 * ```[DrugBank]``` #9 Flag DrugBank data source as usable
 * ```[SIDER]``` #25 Flag SIDER data source as usable and change ID to uppercase
 * ```[HERB]``` #62 Add HERB data source module
 * ```[CMAUP]``` #64 Add CMAUP data source module
 * ```[TISSUES]``` #65 Add TISSUES data source module
 * ```[GuideToPharmacology]``` #66 Add GuideToPharmacology data source module
 * ```[Negatome]``` #67 Add Negatome data source module

### 🔧 Bug fixes and other changes

 * ```[docs]``` Update docs
 * ```[docs]``` Update analysis docs
 * ```[feature]``` Graph mapping now generates a mapping log for later analysis
 * ```[feature]``` Add PSI-MI TAB file format models

## 📦 Version [v0.4.2](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.2)

Released: 05.01.2022

### 📚 Data Sources

 * ```[OMIM]``` #57 Add OMIM data source module
 * ```[EFO]``` #58 Add EFO data source module
 * ```[GeneOntology]``` #28 Flag GeneOntology data source as usable
 * ```[STITCH]``` #59 Add empty STITCH data source module
 * ```[STRING]``` #55 Add STRING data source module
 * ```[DrugBank]``` #9 Add ```skipDrugInteractions``` option to DrugBank
 * ```[UNII]``` #29 Fix UNII graph exporter
 * ```[USDA-PLANTS]``` #35 Fix USDA-PLANTS graph exporter
 * ```[Gene2Phenotype]``` #51 Update Gene2Phenotype model

### 🔧 Bug fixes and other changes

 * ```[feature]``` Graph merge now reuses previous merge status and merges from scratch as fallback
 * ```[feature]``` Add ```removeNode```, ```removeEdge``` to Graph
 * ```[feature]``` Add ```removeNodeLabel```, ```removeEdgeLabel``` to Graph
 * ```[feature]``` Allow class mappings in ```EdgeBuilder```
 * ```[feature]``` Allow path mapping to be processed in parallel (experimental)
 * ```[chore]``` Update dependencies

## 📦 Version [v0.4.1](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.1)

Released: 22.11.2021

### 📚 Data Sources

 * ```[CancerDrugsDB]``` #40 Add generic field to entry model and graph exporter
 * ```[HGNC]``` #13 Add gencc field to gene model and graph exporter

### 🔧 Bug fixes and other changes

 * ```[feature]``` Generate metadata nodes in merged graph for data source version information
 * ```[feature]``` Use metadata nodes to determine whether the merged graph can be extended or needs to be recreated
 * ```[feature]``` Use online version cache if live update failed

## 📦 Version [v0.4.0](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.4.0)

Released: 22.10.2021

### 📚 Data Sources

 * ```[OpenTargets]``` #47 Added OpenTargets data source module
 * ```[AACT]``` #50 Added AACT data source module
 * ```[Gene2Phenotype]``` #51 Added Gene2Phenotype data source module
 * ```[CanadianNutrientFile]``` #52 Added CanadianNutrientFile (CNF) data source module
 * ```[UNII]``` #29 Fixed table columns
 * ```[PharmGKB]``` #8 Fixed occurrence export
 * ```[DrugCentral]``` #18 Fixed updater

### 🔧 Bug fixes and other changes

 * ```[fix]``` HTTPFTPClient is able to handle more formats
 * ```[feature]``` Improve manual update behaviour
 * ```[feature]``` Allow data sources to be processed in parallel (experimental)

## 📦 Version [v0.3.9](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.9)

Released: 13.08.2021

### 📚 Data Sources

 * ```[ReDOTrialsDB]``` #42 Fixed version detection
 * ```[UNII]``` #29 Fixed table columns
 * ```[PharmGKB]``` #8 Fixed removed StudyParameters column
 * ```[DrugCentral]``` #18 Marked as usable

### 🔧 Bug fixes and other changes

 * ```[feature]``` Add --version command line option to print the current BioDWH2 version and check for updates
 * ```[feature]``` #33 Updater checks existence of expected files if version is up to date

## 📦 Version [v0.3.8](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.8)

Released: 05.08.2021

### 📚 Data Sources

 * ```[ReDOTrialsDB]``` Added ReDOTrialsDB data source module

### 🔧 Bug fixes and other changes

 * ```[feature]``` Unindexed numeric (long, int, short, byte) properties are comparable across java types
 * ```[feature]``` Added CLINICAL_TRIAL node mapping type and INVESTIGATES path mapping type
 * ```[feature]``` Added many international clinical trial mapping identifier types
 * ```[docs]``` Improve development documentation

## 📦 Version [v0.3.7](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.7)

Released: 30.06.2021

### 📚 Data Sources

 * ```[GWASCatalog]``` Started work on GWASCatalog data source module

### 🔧 Bug fixes and other changes

 * ```[feature]``` Graph exposes index descriptions
 * ```[feature]``` Graph stores and exposes node and edge property keys with types

## 📦 Version [v0.3.6](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.6)

Released: 25.06.2021

### 📚 Data Sources

 * ```[HGNC]``` Use UniProt ids of genes as Protein nodes and add ```CODES_FOR``` relationships. These are also represented in the mapping layer.
 * ```[PathwayCommons]``` Add PathwayCommons data source module
 * ```[ReDO-DB]``` Add ReDO_DB data source module
 * ```[Sider]``` Reworked graph exporter

### 🔧 Bug fixes and other changes

 * ```[feature]``` Added GMT gene set format reader
 * ```[feature]``` Added updater to check for new BioDWH2 versions
 * ```[docs]``` Improve data sources documentation

## 📦 Version [v0.3.5](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.5)

Released: 16.06.2021

### 📚 Data Sources

 * ```[HGNC]``` Add new mane_select column to gene model
 * ```[MED-RT]``` Specify more drug and disease nodes from concepts
 * ```[MED-RT]``` Add Drug, disease, and path mappings
 * ```[MED-RT]``` Marked MED-RT as usable
 * ```[PharmGKB]``` Add guideline annotations and clinical annotation evidences
 * ```[PharmGKB]``` Split occurrences into literature and webpage
 * ```[UniProt]``` Implemented updater and parser for UniProt
 * ```[DrugBank]``` Replaced loading the whole XML database into ram with streaming
 * ```[EMA]``` Implemented EMA (EPAR, HMPC) data source module
 * ```[HPO]``` Marked HPO as usable
 * ```[USDA-PLANTS]``` Add HAS_SYNONYM edges between plant nodes
 * ```[GeneOntology]``` Add Protein and RNA mapping
 * ```[GeneOntology]``` Annotation edges are now named after annotation types

### 🔧 Bug fixes and other changes

 * ```[feature]``` Added Trie, LongTrie, and ReverseLongTrie data structures
 * ```[feature]``` Added generated html page for dynamic meta graph visualizations
 * ```[feature]``` Implemented new index structure for edges and nodes based on labels
 * ```[feature]``` Added unique index support
 * ```[feature]``` Nodes only support one label again for new index structure
 * ```[feature]``` IO format readers now use Charset class instead of charset name
 * ```[feature]``` OBO ontologies now share a unified graph exporter
 * ```[feature]``` CSV and TSV format reader in FileUtils now ignores missing columns
 * ```[fix]``` Fix missing ids in node mapping layer
 * ```[fix]``` Fix HTTP FTP pre web source parsing
 * ```[docs]``` Data source modules are now listed as usable instead of done, as always something might change
 * ```[docs]``` Updated development docs with new IntelliJ Idea version
 * ```[docs]``` Started adding data source specific README files with additional information

---
## 📦 Version [v0.3.4](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.4)

Released: 09.06.2021

### 🔧 Bug fixes and other changes

 * ```[fix]``` Removed GUI until missing javafx issues are resolved

---
## 📦 Version [v0.3.3](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.3)

Released: 18.05.2021

### 📚 Data Sources

 * ```[PharmGKB]``` Updated data source module for new download files and data schema changes
 * ```[PharmGKB]``` Literature nodes are now mapped as publication

### 🔧 Bug fixes and other changes

 * ```[feature]``` Edge path mappings now allow directional information to prevent redundantly mapped relationships
 * ```[feature]``` Class mapping annotations from superclasses are now used in node models

---

## 📦 Version [v0.3.2](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.2)

Released: 07.05.2021

### 🔧 Bug fixes and other changes

 * ```[fix]``` Fixed a sanity error blocking addition of new data sources for newly created workspaces

---

## 📦 Version [v0.3.1](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.1)

Released: 07.05.2021

### 📚 Data Sources

 * ```[USDA-PLANTS]``` Update download URL for new USDA PLANTS website
 * ```[DrugCentral]``` Map structures as both drug and compound
 * ```[DrugCentral]``` Map drug interaction, indication, and contraindication paths

### 🔧 Bug fixes and other changes

 * ```[feature]``` GraphExporter now has an export version to trigger a reexport if a data source exporter changed
 * ```[feature]``` Mapped edges are now added between all mapping nodes of source and target node
 * ```[mapping]``` Node type SIDE_EFFECT is renamed to ADVERSE_EVENT
 * ```[docs]``` Add simple analysis example documentation for DrugCentral and CancerDrugsDB

---

## 📦 Version [v0.3.0](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.3.0)

Released: 29.04.2021

### 📚 Data Sources

 * ```[CancerDrugsDB]``` Finished new CancerDrugsDB data source module
 * ```[ITIS]``` Finish graph exporter
 * ```[HPO]``` Implemented HPO updater
 * ```[ABDAMED2]``` Implemented K2 file format reader
 * ```[DrugBank]``` Remove redundant nodes and match article dois for mapping

### 🔧 Bug fixes and other changes

 * ```[feature]``` Meta graph generation and GraphML export can now be skipped in the config.json
 * ```[feature]``` Add generation and packaging of maven dependency attribution xml
 * ```[feature]``` Add CodeQL analysis GitHub action
 * ```[feature]``` Graphs can now be opened as readonly as done for merging
 * ```[fix]``` Not all HTTPClient methods followed redirects
 * ```[fix]``` Meta graph generation now skips for empty graphs

---

## 📦 Version [v0.2.1](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.2.1)

Released: 01.03.2021

### 🔧 Bug fixes and other changes

 * ```[feature]``` Implemented GFF3 file format reader
 * ```[feature]``` Add subclasses for specific OBO file format entries
 * ```[feature]``` Improve graph MVStoreIndex performance

---

## 📦 Version [v0.2.0](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.2.0)

---

## 📦 Version [v0.1.8](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.1.8)

---

## 📦 Version [v0.1.7](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.1.7)

---

## 📦 Version [v0.1.6](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.1.6)

---

## 📦 Version [v0.1.5](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.1.5)

---

## 📦 Version [v0.1.4](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.1.4)

---

## 📦 Version [v0.1.3](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.1.3)

---

## 📦 Version [v0.1.2](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.1.2)

---

## 📦 Version [v0.1.1](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.1.1)

---

## 📦 Version [v0.1](https://github.com/BioDWH2/BioDWH2/releases/tag/v0.1)
 