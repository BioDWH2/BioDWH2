# SIDER

SIDER contains information on marketed medicines and their recorded adverse drug reactions. The information is extracted from public documents and package inserts. The available information include side effect frequency, drug and side effect classifications as well as links to further information, for example drug–target relations.

> ⚠️ Please note that SIDER hasn't been updated since 2015 (last checked 06.2021). As the information were extracted using text-mining techniques, data errors are inevitable and some are already known. However, SIDER is still one of the best, freely available side effect resources available.

## Data issues

  * [carvedilol](http://sideeffects.embl.de/drugs/2585/?labels=all)
    * SIDER lists Asthma as indication, however, the [drug label](http://sideeffects.embl.de/labels/dpdonline/0TF8oZKGp0k=/C0004096/) lists Asthma under contra-indications and other data sources such as [DrugCentral](https://drugcentral.org/drugcard/522?q=carvedilol#druguse) agree.

## Website

[http://sideeffects.embl.de](http://sideeffects.embl.de)

## License

The files ```meddra_all_indications.tsv.gz```, ```meddra_all_se.tsv.gz```, and ```meddra_freq.tsv.gz``` are licensed under a Creative Commons Attribution-Noncommercial-Share Alike 4.0 License [![](https://licensebuttons.net/l/by-nc-sa/4.0/80x15.png)](http://creativecommons.org/licenses/by-nc-sa/4.0/)

The files ```drug_names.tsv``` and ```drug_atc.tsv``` are licensed under a CC0 1.0 Universal Public Domain Dedication license [![](https://licensebuttons.net/l/zero/1.0/80x15.png)](https://creativecommons.org/publicdomain/zero/1.0/)

For commercial use or customized versions, please contact biobyte solutions GmbH at [http://www.biobyte.de](http://www.biobyte.de).

## Citation

> Kuhn M, Letunic I, Jensen LJ, Bork P. The SIDER database of drugs and side effects. Nucleic Acids Res. 2015 Oct 19. doi: [10.1093/nar/gkv1075](http://dx.doi.org/10.1093/nar/gkv1075)

> Kuhn M, Campillos M, Letunic I, Jensen LJ, Bork P. A side effect resource to capture phenotypic effects of drugs. Mol Syst Biol. 2010;6:343. Epub 2010 Jan 19.

## Files used

  * ```meddra_all_indications.tsv.gz```
  * ```meddra_all_se.tsv.gz```
  * ```meddra_freq.tsv.gz```
  * ```drug_names.tsv```
  * ```drug_atc.tsv```

## Configuration properties

| Property       | Values     | Description |
| -------------- | ---------- | ----------- |
| forceExport    | true/false | Force graph export even if nothing changed |
|                |            |             |
