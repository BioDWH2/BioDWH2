# Graph Analysis

Coming soon

## Examples

  * [Cancer drug analysis with DrugCentral and CancerDrugsDB](./examples/CancerDrugs.md)

## Finding Connections

To help finding connections of interest between entities, this list is intended to provide pointers. Of course more information are present in the different data sources.

### Disease - Gene

|      |   |
| ---- | - |
| OMIM | ()-[]->() |
| HPO  | (d:Disease)<-[ASSOCIATED_WITH]-(Term)<-[tg:ASSOCIATED_WITH]-(HPO_Gene) WHERE tg.source_disease_id=d.id |
|      |   |

### Disease - Drug

|               |   |
| ------------- | - |
| DrugCentral   | ()-[]->() |
| CancerDrugsDB | (Drug)-[INDICATES]->(Disease) |
| ReDO-DB       | (Drug)-[INDICATES]->(Disease) |
| Sider         | (Drug)-[INDICATES]->(MeddraTerm) |
| MED-RT        | (Drug)-[INDUCES]->(Disease) |
| MED-RT        | (Drug)-[CI_WITH]->(Disease) |
| MED-RT        | (Drug)-[MAY_TREAT]->(Disease) |
| NDF-RT        | (Drug)-[INDUCES]->(Disease) |
| NDF-RT        | (Drug)-[CI_WITH]->(Disease) |
| NDF-RT        | (Drug)-[MAY_TREAT]->(Disease) |
|               |   |

### Drug - Drug

|               |   |
| ------------- | - |
| DrugBank      | (Drug)-[INTERACTS_WITH_DRUG]->(Drug) |
| DrugCentral   | ()-[]->() |
| MED-RT        | (Drug)-[EFFECT_MAY_BE_INHIBITED_BY]->(Drug) |
| NDF-RT        | (Drug)-[EFFECT_MAY_BE_INHIBITED_BY]->(Drug) |
|               |   |

### Drug - Gene

|               |   |
| ------------- | - |
| DrugBank      | (Drug)-[TARGETS]->(TargetMetadata)-[HAS_TARGET]->(Target)-[IS_POLYPEPTIDE]->(Polypeptide) |
| DrugBank      | (Drug)-[TARGETS]->(TargetMetadata)-[HAS_TARGET]->(Enzyme)-[IS_POLYPEPTIDE]->(Polypeptide) |
| DrugBank      | (Drug)-[TARGETS]->(TargetMetadata)-[HAS_TARGET]->(Carrier)-[IS_POLYPEPTIDE]->(Polypeptide) |
| DrugBank      | (Drug)-[TARGETS]->(TargetMetadata)-[HAS_TARGET]->(Transporter)-[IS_POLYPEPTIDE]->(Polypeptide) |
| DrugCentral   | ()-[]->() |
| CancerDrugsDB | (Drug)-[TARGETS]->(Gene) |
| ReDO-DB       | (Drug)-[TARGETS]->(Gene) |
|               |   |

### Drug - ADR

|        |   |
| ------ | - |
| SIDER  | (Drug)-[HAS_SIDE_EFFECT]->(MeddraTerm) |
| SIDER  | (Drug)-[HAS_SIDE_EFFECT_FREQUENCY]->(MeddraTerm) |
| ADReCS | (Drug)-[ASSOCIATED_WITH]->(ADR) |
|        |   |

### Gene - Protein

|      |   |
| ---- | - |
| HGNC | (Gene)-[CODES_FOR]->(Protein) |
|      |   |

### Pathway - Gene

|                |   |
| -------------- | - |
| PathwayCommons | (Gene)-[ASSOCIATED_WITH]->(Pathway) |
|                |   |

### Pathway - Protein

|                |   |
| -------------- | - |
| PathwayCommons | (Protein)-[ASSOCIATED_WITH]->(Pathway) |
|                |   |

### Pathway - Drug

|                |   |
| -------------- | - |
| DrugBank       | (Drug)-[IS_IN_PATHWAY]->(Pathway) |
|                |   |
