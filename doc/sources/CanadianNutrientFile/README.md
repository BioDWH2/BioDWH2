# CanadianNutrientFile

Health Canada publishes databases which list nutrient values in Canadian foods. The Canadian Nutrient File (CNF) is a comprehensive, computerized bilingual database that reports up to 152 nutrients in over 5690 foods. The database can help you find values for nutrients such as vitamins, minerals, protein, energy, fat and many more, and is updated periodically.

## Particularities

  * Some files are broken and contain empty lines, lines with only ',' and some lines are split into multiple lines. This is handled and fixed when importing the files.
  * Sometimes a links are wrong and point to not existing entries. This concerns measurements, nutrients and food source. This broken links will be ignored.

## Website

[https://www.canada.ca/en/health-canada/services/food-nutrition/healthy-eating/nutrient-data.html](https://www.canada.ca/en/health-canada/services/food-nutrition/healthy-eating/nutrient-data.html)

## License

[copyright-guidelines-canadian-nutrient-file](https://www.canada.ca/en/health-canada/services/food-nutrition/healthy-eating/nutrient-data/copyright-guidelines-canadian-nutrient-file.html)

## Files used

  * ```cnf-fcen-csv.zip```

## Configuration properties

| Property       | Values     | Description |
| -------------- | ---------- | ----------- |
| forceExport    | true/false | Force graph export even if nothing changed |
|                |            |             |
