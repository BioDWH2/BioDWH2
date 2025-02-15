![Java CI](https://github.com/BioDWH2/BioDWH2/actions/workflows/maven.yml/badge.svg?branch=develop) ![Release](https://img.shields.io/github/v/release/BioDWH2/BioDWH2) ![Downloads](https://img.shields.io/github/downloads/BioDWH2/BioDWH2/total) ![License](https://img.shields.io/github/license/BioDWH2/BioDWH2)

# BioDWH2
BioDWH2 is an easy-to-use, automated, graph-based data warehouse and mapping tool for bioinformatics and medical informatics. Whether you need a data warehouse for running research analyses or information systems, BioDWH2 can support you in setting things up.

![BioDWH2-overview](doc/img/BioDWH-complete-overview.png)

## Data sources
Multiple data sources are readily available with many more under development or planned. A list of officially supported data sources is provided [here](doc/data_sources.md).

Missing a data source important to you? Feel free to open a discussion [here](https://github.com/BioDWH2/BioDWH2/discussions/categories/data-source-requests)! Want to develop your own data source module? In that case consult the [development documentation](doc/development.md).

## Download
The latest release version of BioDWH2 can be downloaded [here](https://github.com/BioDWH2/BioDWH2/releases/latest).

A list of changes for each version can be found in the [Changelog](CHANGELOG.md).

For using generated data warehouses inside Neo4j please see the separate [BioDWH2-Neo4j-Server repository](https://github.com/BioDWH2/BioDWH2-Neo4j-Server).

## Usage
BioDWH2 is developed to be usable out of the box without any prerequisites except the Java Runtime Environment version 11 or higher. The JRE 11 is available [here](https://adoptium.net/temurin/releases/?package=jre&version=11).

For detailed information on how to use BioDWH2 please see the separate [documentation](doc/usage.md).

## Developer docs
If you either want to help in the development of BioDWH2 directly, or write a new data source module please consult the [development documentation](doc/development.md).

## Citing BioDWH2
If you make use of BioDWH2, or it's companion tools as part or your research cite the BioDWH2 manuscript in any resulting publications.

> Friedrichs M. BioDWH2: an automated graph-based data warehouse and mapping tool. Journal of Integrative Bioinformatics. 2021;18(2):167-176.
doi: [10.1515/jib-2020-0033](https://dx.doi.org/10.1515/jib-2020-0033)

> Friedrichs M. Automation in graph-based data integration and mapping. In: Chen M, Hofestädt R, eds. Integrative Bioinformatics. Springer Singapore; 2022:97-110.
doi: [10.1007/978-981-16-6795-4_5](https://dx.doi.org/10.1007/978-981-16-6795-4_5)
