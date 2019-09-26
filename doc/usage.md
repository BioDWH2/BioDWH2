# BioDWH2 usage



## The workspace

BioDWH2 is centered around the concept of a workspace containing all data sources, configurations and outputs in one central location. This enables all data to be available during processing and easy to backup if necessary.

The basic structure is visualized below:

```
.
+-- sources
|   +-- DrugBank
|   |   +-- source
|   |   |   +-- drugbank_full.xml
|   |   |   +-- drugbank vocabulary.csv
|   |   +-- intermediate.ttl
|   |   +-- metadata.json
|   +-- HGNC
|   |   +-- source
|   |   |   +-- hgnc_complete_set.txt
|   |   +-- intermediate.ttl
|   |   +-- metadata.json
|   ...
+-- neo4j_import
|   +-- node.csv
|   +-- relationships.csv
+-- merged.ttl
+-- fusion.ttl
+-- config.json
```

## Command line interface parameters

| Short parameter | Long parameter | Values         | Description                  |
| --------------- | -------------- | -------------- | ---------------------------- |
| -h              | --help         | -              | Print the help message       |
| -c              | --create       | workspace path | Create a new empty workspace |
| -s              | --status       | workspace path | Check and output the state of a workspace |
| -u              | --update       | workspace path | Update all data sources of a workspace |
|                 |                |                |                              |
