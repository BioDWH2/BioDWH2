# HPO

The Human Phenotype Ontology (HPO) provides a standardized vocabulary of phenotypic abnormalities encountered in human disease.

## Website

[https://hpo.jax.org](https://hpo.jax.org)

## License

License information for HPO can be found at [https://hpo.jax.org/app/license](https://hpo.jax.org/app/license).

## Citation

> Sebastian Köhler, Michael Gargano, Nicolas Matentzoglu, Leigh C Carmody, David Lewis-Smith, Nicole A Vasilevsky, Daniel Danis, Ganna Balagura, Gareth Baynam, Amy M Brower, Tiffany J Callahan, Christopher G Chute, Johanna L Est, Peter D Galer, Shiva Ganesan, Matthias Griese, Matthias Haimel, Julia Pazmandi, Marc Hanauer, Nomi L Harris, Michael J Hartnett, Maximilian Hastreiter, Fabian Hauck, Yongqun He, Tim Jeske, Hugh Kearney, Gerhard Kindle, Christoph Klein, Katrin Knoflach, Roland Krause, David Lagorce, Julie A McMurry, Jillian A Miller, Monica C Munoz-Torres, Rebecca L Peters, Christina K Rapp, Ana M Rath, Shahmir A Rind, Avi Z Rosenberg, Michael M Segal, Markus G Seidel, Damian Smedley, Tomer Talmy, Yarlalu Thomas, Samuel A Wiafe, Julie Xian, Zafer Yüksel, Ingo Helbig, Christopher J Mungall, Melissa A Haendel, Peter N Robinson, The Human Phenotype Ontology in 2021, Nucleic Acids Research, Volume 49, Issue D1, 8 January 2021, Pages D1207–D1217. doi: [10.1093/nar/gkaa1043](https://doi.org/10.1093/nar/gkaa1043)

## Files used

At the moment, the ontology is downloaded in OBO format (```hp.obo```).

> Please note that the HPO OBO version lacks some of the logical reasoning which is present in the OWL format version ([info](https://hpo.jax.org/app/download/ontology)). Once a suitable OWL parser is implemented, the ontology file will likely be switched!

For annotations, the files ```phenotype.hpoa``` and ```phenotype_to_genes.txt``` are parsed and used in the graph exporter.

> The file ```genes_to_phenotype.txt``` is stated to be analogous to ```phenotype_to_genes.txt``` although the file size differs dramatically. Therefore, this file is currently ignored.

## Configuration properties

| Property       | Values     | Description                                                                                                                                                               |
|----------------|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| forceExport    | true/false | Force graph export even if nothing changed                                                                                                                                |
| ignoreObsolete | true/false | Ignore OBO ontology entries if they are flagged obsolete                                                                                                                  |
| omimLicensed   | true/false | Annotations with evidence IEA require contacting OMIM ([info](https://hpo.jax.org/app/help/annotations)). By setting this flag to true, you agree that this has happened! |
