# BioDWH2 usage

The first step is to create a blank workspace in a new location:

~~~BASH
> BioDWH2.jar -c /path/to/workspace
~~~

Now the ```config.json``` needs to be adjusted for BioDWH2 to know, which data sources are to be included. A simple example with the HGNC and MED-RT database may look as follows:

```
{
  "version" : 1,
  "creationDateTime" : "2019-09-26T09:30:36.568",
  "dataSourceIds" : ["HGNC", "MED-RT"]
}
```

Now, the workspace can be automatically updated, parsed, exported, merged, and mapped:

~~~BASH
> BioDWH2.jar -u /path/to/workspace
~~~

To check the current state of the workspace, whether new versions are available, something is missing, etc. execute:

~~~BASH
> BioDWH2.jar -s /path/to/workspace
~~~

## The workspace

BioDWH2 is centered around the concept of a workspace containing all data sources, configurations and outputs in one central location. This enables all data to be available during processing and easy to backup if necessary.

The basic structure is visualized below:

```
.
+-- config.json
+-- sources
|   +-- DrugBank
|   |   +-- source
|   |   |   +-- drugbank_all_full_database.xml.zip
|   |   |   +-- drugbank_all_metabolite-structures.sdf.zip
|   |   +-- intermediate.graphml
|   |   +-- metadata.json
|   +-- HGNC
|   |   +-- source
|   |   |   +-- hgnc_complete_set.txt
|   |   +-- intermediate.graphml
|   |   +-- metadata.json
|   ...
|   +-- merged.graphml
|   +-- mapped.graphml
```

## Command line interface parameters

| Short parameter | Long parameter | Values         | Description                               |
| --------------- | -------------- | -------------- | ----------------------------------------- |
| -h              | --help         | -              | Print the help message                    |
| -su             | --skip-update  | -              | Skip update, only parse and export        |
| -v              | --verbose      | -              | Enable additional logging output          |
| -ds             | --data-sources | -              | List all available data sources           |
| -c              | --create       | workspace path | Create a new empty workspace              |
| -s              | --status       | workspace path | Check and output the state of a workspace |
| -u              | --update       | workspace path | Update all data sources of a workspace    |
|                 |                |                |                                           |
