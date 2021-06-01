# HPO

The Human Phenotype Ontology (HPO) provides a standardized vocabulary of phenotypic abnormalities encountered in human disease.

## Website

[https://hpo.jax.org](https://hpo.jax.org)

## License

License information for HPO can be found at [https://hpo.jax.org/app/license](https://hpo.jax.org/app/license).

## Files used

At the moment, the ontology is downloaded in OBO format (```hp.obo```).

> Please note that the HPO OBO version lacks some of the logical reasoning which is present in the OWL format version ([info](https://hpo.jax.org/app/download/ontology)). Once a suitable OWL parser is implemented, the ontology file will likely be switched!

For annotations, the files ```phenotype.hpoa``` and ```phenotype_to_genes.txt``` are parsed and used in the graph exporter.

> The file ```genes_to_phenotype.txt``` is stated to be analogous to ```phenotype_to_genes.txt``` although the file size differs dramatically. Therefore, this file is currently ignored.

## Configuration properties

| Property       | Values     | Description |
| -------------- | ---------- | ----------- |
| forceExport    | true/false | Force graph export even if nothing changed |
| ignoreObsolete | true/false | Ignore OBO ontology entries if they are flagged obsolete |
| omimLicensed   | true/false | Annotations with evidence IEA require contacting OMIM ([info](https://hpo.jax.org/app/help/annotations)). By setting this flag to true, you agree that this has happened! |
|                |            |             |
