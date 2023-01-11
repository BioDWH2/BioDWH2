# DrugCentral

DrugCentral is an online drug information resource created and maintained by the Division of Translational Informatics at University of New Mexico in collaboration with the IDG.

## Website

[https://drugcentral.org](https://drugcentral.org)

## License

> CC BY-SA 4.0

## Citation

> Ursu O, Holmes J, Knockel J, et al. DrugCentral: online drug compendium. Nucleic Acids Res. 2017;45(D1):D932-D939. doi: [10.1093/nar/gkw993](https://doi.org/10.1093/nar/gkw993)

## Files used

The SQL database dump file `drugcentral-pgdump_[YYYYMMDD].sql.gz` is used to extract the database tables into TSV files.

## Configuration properties

| Property               | Values     | Description                                                                   |
|------------------------|------------|-------------------------------------------------------------------------------|
| forceExport            | true/false | Force graph export even if nothing changed                                    |
| skipDrugLabelFullTexts | true/false | Skip export of drug label full text information to reduce graph database size |
| skipLINCSSignatures    | true/false | Skip export of LINCS signatures to reduce graph database size                 |
| skipFAERSReports       | true/false | Skip export of FAERS reports to reduce graph database size                    |
