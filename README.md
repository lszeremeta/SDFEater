# SDFEater

[SDF](https://pubs.acs.org/doi/abs/10.1021/ci00007a012) parser written in Java running from command-line interface (CLI). SDFEater not only ~~eats~~ parse your SDF files, but also can add additional data to the output.

## How to start?
Simply download one of [ready to use JAR file](https://github.com/lszeremeta/SDFEater/releases) from project releases. You can also [clone this repository](https://help.github.com/articles/cloning-a-repository/) and build a project yourself.

### Build project yourself

1. Clone this repository:
```
git clone https://github.com/lszeremeta/SDFEater.git
```

2. Build SDFEater using [Apache Maven](https://maven.apache.org/):
```
cd SDFEater
mvn clean package
```

Built JAR files can be found in the target directory.

## Example usage
```
java -jar SDFEater-version-jar-with-dependencies.jar -i ./examples/chebi_special_char_test.sdf -f cypher -up
```
Example above reads input SDF file, adds periodic table data for atoms, try to replace chemical database IDs with URL and give [Cypher](https://neo4j.com/developer/cypher-query-language/) file in the output.

In _examples_ directory you can find example SDF files based on data from [ChEBI](https://www.ebi.ac.uk/chebi/init.do) ([CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)) and [DrugBank  open structures](https://www.drugbank.ca/releases/latest#open-data) ([CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/)) databases.

## CLI options
Running SDFEater without parameters displays help.

* `-i,--input <arg>` - input SDF file path (required)
* `-f,--format <arg>` - output format (`cypher`, `cvme`, `smiles`, `inchi`) (required)
* `-p,--periodic` - add additional atoms data from [periodic table](https://github.com/lszeremeta/SDFEater/blob/master/src/main/resources/pl/edu/uwb/ii/sdfeater/periodic_table.json) (for `cypher` output format)
* `-u,--urls` - try to generate full database URLs instead of IDs _(enabled in cvme)_

## Output formats
You can specify the output format using `-f,--format`. Available output formats:
* `cypher` - [Cypher](https://neo4j.com/developer/cypher-query-language/) compound, atoms, bonds and relation to [import to Neo4j graph database](https://neo4j.com/developer/kb/export-sub-graph-to-cypher-and-import/),
* `cvme` - [CVME](http://cs.aalto.fi/en/current/events/2017-09-22-002/) file format based on SKOS,
* `smiles` - plain text SMILES (if available in the compound property)
* `inchi` - plain text InChI (if available in the compound property)

## Used open source projects
- [Apache Commons CLI](https://github.com/apache/commons-cli) as CLI controller ([Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)),
- [Gson](https://github.com/google/gson) as periodic table JSON parser ([Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)),
- [periodic-table](https://github.com/andrejewski/periodic-table) - base JSON file with periodic table data ([ISC License](https://choosealicense.com/licenses/isc/)).

The sample SDF files in the examples directory are based on data from [ChEBI](https://www.ebi.ac.uk/chebi/init.do) ([CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)) and [DrugBank](https://www.drugbank.ca/releases/latest#open-data) open structures ([CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/)) databases.

## Contribution
Would you like to improve the SDFEater? Great! We are waiting for your help and suggestions. If you are new in open source contributions, read [How to Contribute to Open Source](https://opensource.guide/how-to-contribute/).

## License
Distributed under [MIT license](https://github.com/lszeremeta/chebi-sdf-parser/blob/master/LICENSE.txt).
