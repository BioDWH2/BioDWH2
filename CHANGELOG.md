# Changelog

## 📦 Version v0.3.9

Released: Upcoming

### 📚 Data Sources

 * ```[ReDOTrialsDB]``` Fixed version detection
 * ```[UNII]``` Fixed table columns
 * ```[PharmGKB]``` Fixed removed StudyParameters column

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
 