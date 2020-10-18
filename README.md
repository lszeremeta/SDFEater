# <img src="https://raw.githubusercontent.com/lszeremeta/SDFEater/master/logo/SDFEater.png" alt="SDFEater logo" width="300">

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/fc5d5e2e22ce4616a041d97cdf1f3a11)](https://www.codacy.com/gh/lszeremeta/SDFEater/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lszeremeta/SDFEater&amp;utm_campaign=Badge_Grade) [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.1467892.svg)](https://doi.org/10.5281/zenodo.1467892)

[SDF](https://pubs.acs.org/doi/abs/10.1021/ci00007a012) parser written in Java running from command-line interface (CLI). SDFEater not only ~~eats~~ parse your SDF files, but also can add additional data to the output.

## How to start

You can use JAR file, run SDFEater from Docker image or build everything yourself. If you don't know what to choose, choose one of the first two options.

### JAR file

You need Java installed.

1. Download the ready-to-use JAR `SDFEater-VERSION-jar-with-dependencies.jar` file from [project release](https://github.com/lszeremeta/SDFEater/releases) asset.

2. Run SDFEater

```shell
java -jar SDFEater-VERSION-jar-with-dependencies.jar
```

### Docker image

If you have [Docker](https://docs.docker.com/engine/install/) installed, you can use tiny SDFEater Docker image from [Docker Hub](https://hub.docker.com/r/lszeremeta/sdfeater).

Because the tool is closed inside the container, you have to [mount](https://docs.docker.com/storage/bind-mounts/#start-a-container-with-a-bind-mount) local directory with your input file. The default working directory of the image is `/app`. You need to mount your local directory inside it (e.g. `/app/input`):

```shell
docker run -it --rm --name sdfeater-app --mount type=bind,source=/home/user/input,target=/app/input,readonly lszeremeta/sdfeater:latest
```

In this case, the local directory `/home/user/input` has been mounted under `/app/input`.

You can also simply mount current working directory using `$(pwd)` sub-command:

```shell
docker run -it --rm --name sdfeater-app --mount type=bind,source="$(pwd)",target=/app/input,readonly lszeremeta/sdfeater:latest
```

### Build SDFEater from sources

You need Java with Maven installed.

1. Clone this repository:

```shell
git clone https://github.com/lszeremeta/SDFEater.git
```

If you don't want or can't use git, you can [download the zip archive](https://github.com/lszeremeta/SDFEater/archive/master.zip) and extract it. 

2. Go to the project directory and build SDFEater using [Apache Maven](https://maven.apache.org/):

```shell
cd SDFEater
mvn clean package
```

Built JAR files can be found in the _target_ directory.

### Local Docker build

You need [Docker](https://docs.docker.com/engine/install/) installed.

1. Clone this repository:

```shell
git clone https://github.com/lszeremeta/SDFEater.git
```

If you don't want or can't use git, you can [download the zip archive](https://github.com/lszeremeta/SDFEater/archive/master.zip) and extract it. 

2. Go to the project directory and Docker image:

```shell
cd SDFEater
docker build -t sdfeater .
```

3. Run Docker container:

```shell
docker run -it --rm --name sdfeater-app --mount type=bind,source=/home/user/input,target=/app/input,readonly sdfeater
```

In this case, your local directory `/home/user/input` has been mounted under `/app/input`.

## CLI options

Running SDFEater without parameters displays help.

* `-i,--input <arg>` - input SDF file path (required)
* `-f,--format <arg>` - output format (e.g. `cypher`, `jsonld`, `cvme`, `smiles`, `inchi`) (required; full list below)
* `-p,--periodic` - add additional atoms data from [periodic table](https://github.com/lszeremeta/SDFEater/blob/master/src/main/resources/pl/edu/uwb/ii/sdfeater/periodic_table.json) (for `cypher` output format)
* `-u,--urls` - try to generate full database URLs instead of IDs (enabled in `cvme`)

Remember about the appropriate file path when using Docker image. Suppose you mounted your local directory `/home/user/input` under `/app/input` and the path to the SDF file you want to use in SDFEater is `/home/user/input/file.sdf`. In this case, enter the path `/app/input/file.sdf` or `input/file.sdf` as the value of the `-i` argument.

## Output formats

You can specify the output format using `-f,--format`. Available output formats:

* `cypher` - [Cypher](https://neo4j.com/developer/cypher-query-language/) molecule, atoms, bonds and relation ready to [import to the Neo4j graph database](https://neo4j.com/developer/kb/export-sub-graph-to-cypher-and-import/),
* `cvme` - [CVME](http://cs.aalto.fi/en/current/events/2017-09-22-002/) file format based on SKOS,
* `smiles` - plain text SMILES (if available in the molecule property)
* `inchi` - plain text InChI (if available in the molecule property)
* `turtle` - [Terse RDF Triple Language](https://www.w3.org/TR/turtle/) (based on [MolecularEntitly](https://bioschemas.org/types/MolecularEntity/) type)
* `ntriples` - [N-Triples](https://www.w3.org/TR/n-triples/) (based on [MolecularEntitly](https://bioschemas.org/types/MolecularEntity/) type)
* `rdfxml` - [RDF/XML](https://www.w3.org/TR/rdf-syntax-grammar/) (based on [MolecularEntitly](https://bioschemas.org/types/MolecularEntity/) type)
* `rdfthrift` - [RDF Binary encoding using Thrift](https://afs.github.io/rdf-thrift/rdf-binary-thrift.html) (based on [MolecularEntitly](https://bioschemas.org/types/MolecularEntity/) type)
* `jsonldhtml` - [JSON-LD](https://json-ld.org/) with HTML (based on [MolecularEntitly](https://bioschemas.org/types/MolecularEntity/) type)
* `jsonld` - [JSON-LD](https://json-ld.org/) (based on [MolecularEntitly](https://bioschemas.org/types/MolecularEntity/) type)
* `rdfa` - Simple HTML with [RDFa](http://rdfa.info/) (based on [MolecularEntitly](https://bioschemas.org/types/MolecularEntity/) type)
* `microdata` - Simple HTML with [Microdata](https://www.w3.org/TR/microdata/) (based on [MolecularEntitly](https://bioschemas.org/types/MolecularEntity/) type)

## Examples

```shell
java -jar SDFEater-VERSION-jar-with-dependencies.jar -i ../examples/chebi_special_char_test.sdf -f cypher -up
```

Returns [Cypher](https://neo4j.com/developer/cypher-query-language/) with added periodic table data for atoms and replaced chemical database IDs with URL. SDFEater run from a JAR file.

```shell
java -jar SDFEater-VERSION-jar-with-dependencies.jar -i ../examples/chebi_test.sdf -f jsonld  > molecules.jsonld
```

Returns [JSON-LD](https://json-ld.org/) and redirect output to `molecules.jsonld` file. SDFEater run from a JAR file.

```shell
docker run -it --rm --name sdfeater-app --mount type=bind,source=/home/user/input,target=/app/input,readonly lszeremeta/sdfeater:latest -i input/chebi_test.sdf -f microdata  > molecules.html
```

Returns simple HTML with added [Microdata](https://www.w3.org/TR/microdata/) and redirect output to `molecules.html` file. Run from pre-build Docker image.

In `examples` directory in the this repository you can find example SDF files based on data from [ChEBI](https://www.ebi.ac.uk/chebi/init.do) and [DrugBank  open structures](https://www.drugbank.ca/releases/latest#open-data) databases.

## Publications and resources

If you need more detailed information, take a look at these publications and resources. There you will find detailed description of the parser, performance tests and example Cypher outputs.

1. Ł. Szeremeta, "SDFEater: A Parser for Chemoinformatics Formats" 9 2018 \[Online]. Available: <https://doi.org/10.26434/chemrxiv.7123193>.
2. D. Tomaszuk and Ł. Szeremeta, "Named Property Graphs" in Proceedings of the 2018 Federated Conference on Computer Science and Information Systems, ser. Annals of Computer Science and Information Systems, M. Ganzha, L. Maciaszek, and M. Paprzycki, Eds., vol. 15. IEEE, 2018, pp. 173–177. (2018) \[Online]. Available: <http://dx.doi.org/10.15439/2018F103>.
3. Ł. Szeremeta and D. Tomaszuk, “SDFParser example Cypher outputs”. figshare, 10-May-2018 \[Online]. Available: <https://doi.org/10.6084/m9.figshare.6249962>.
4. D. Tomaszuk, “chemskos”. figshare, 29-Aug-2018 \[Online]. Available: <https://doi.org/10.6084/m9.figshare.7022144>.

## Used open source projects

* [Apache Commons CLI](https://github.com/apache/commons-cli) as CLI controller ([Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)),
* [Gson](https://github.com/google/gson) as periodic table JSON parser ([Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)),
* [periodic-table](https://github.com/andrejewski/periodic-table) - base JSON periodic table file ([ISC License](https://choosealicense.com/licenses/isc/)),
* [Apache Jena](https://jena.apache.org/) - for some output formats ([Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)),
* [Apache Commons Text](https://commons.apache.org/proper/commons-text/) - to HTML escape for RDFa and Microdata formats ([Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)).

The sample SDF files in the examples directory are based on data from [ChEBI](https://www.ebi.ac.uk/chebi/init.do) ([CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)) and [DrugBank](https://www.drugbank.ca/releases/latest#open-data) open structures ([CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/)) databases.

## Contribution

Would you like to improve the SDFEater? Great! We are waiting for your help and suggestions. If you are new in open source contributions, read [How to Contribute to Open Source](https://opensource.guide/how-to-contribute/).

## License

Distributed under [MIT license](https://github.com/lszeremeta/chebi-sdf-parser/blob/master/LICENSE.txt).

## See also

These projects can also be useful:

* [Molstruct](https://github.com/lszeremeta/molstruct) - Convert chemical molecule data CSV files to structured data formats
* [MEgen](https://github.com/lszeremeta/MEgen) - Convenient online form to generate structured data about molecules
