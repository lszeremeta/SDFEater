# <img src="https://raw.githubusercontent.com/lszeremeta/SDFEater/master/logo/SDFEater.png" alt="SDFEater logo" width="300">

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/fc5d5e2e22ce4616a041d97cdf1f3a11)](https://www.codacy.com/gh/lszeremeta/SDFEater/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lszeremeta/SDFEater&amp;utm_campaign=Badge_Grade) [![Docker Image Size (latest by date)](https://img.shields.io/docker/image-size/lszeremeta/sdfeater?label=Docker%20image%20size)](https://hub.docker.com/r/lszeremeta/sdfeater)

[SDF](https://pubs.acs.org/doi/abs/10.1021/ci00007a012) parser written in Java running from the command-line interface (CLI). You don't need to have new Java installed! Java 8 and above are supported. Do you love️ Docker? You can use a lightweight [SDFEater container](https://hub.docker.com/r/lszeremeta/sdfeater)! SDFEater not only ~~eats~~ parse your SDF files but also can add additional data to the output. The choice of output formats is really wide.

## Quick start

Use SDFEater in 3 easy steps. In this example, we will use the [ChEBI](https://www.ebi.ac.uk/chebi/init.do) dataset and ready to use JAR file. You need Java 8+ installed.

1. Download the ready-to-use JAR `SDFEater-VERSION-jar-with-dependencies.jar` file from [project release](https://github.com/lszeremeta/SDFEater/releases) asset.

SDFEater is also available as a [Docker image](#docker-image). In most scenarios, JAR file or the Docker image should be sufficient and convenient to run SDFEater but you may want to [build everything yourself](https://github.com/lszeremeta/SDFEater/wiki/Manual-project-build).

2. Download [ChEBI complete 3-star dataset file](https://www.ebi.ac.uk/chebi/downloadsForward.do) and unpack downloaded gz archive. ChEBI datasets are shared via FTP, so if your browser or operating system does not support FTP, you may need an additional program such as [FileZilla](https://filezilla-project.org/).
3. Assuming the `ChEBI_complete_3star.sdf` file is in the current directory and the output format you're interested in is RDFa, the command will be as follows:

```shell
    java -jar SDFEater-VERSION-jar-with-dependencies.jar -f rdfa -i ChEBI_complete_3star.sdf > ChEBI_complete_3star_rdfa.html
```

That's all. Now you have the RDFa file ready in the current directory. You can try other output formats and options as described below. You can also use SDFEater to convert [DrugBank SDF files](https://go.drugbank.com/releases/latest#structures).

## Docker image

If you have [Docker](https://docs.docker.com/engine/install/) installed, you can use a tiny SDFEater Docker image from [Docker Hub](https://hub.docker.com/r/lszeremeta/sdfeater).

Because the tool is closed inside the container, you have to [mount](https://docs.docker.com/storage/bind-mounts/#start-a-container-with-a-bind-mount) local directory with your input file. The default working directory of the image is `/app`. You need to mount your the local directory inside it (e.g. `/app/input`):

```shell
docker run -it --rm --name sdfeater-app --mount type=bind,source=/home/user/input,target=/app/input,readonly lszeremeta/sdfeater:latest
```

In this case, the local directory `/home/user/input` has been mounted under `/app/input`.

You can also simply mount the current working directory using `$(pwd)` sub-command:

```shell
docker run -it --rm --name sdfeater-app --mount type=bind,source="$(pwd)",target=/app/input,readonly lszeremeta/sdfeater:latest
```

## CLI options

Running SDFEater without parameters displays help.

* `-i,--input <arg>` - input SDF file path (required)
* `-f,--format <arg>` - output format (e.g. `cypher`, `jsonld`, `cvme`, `smiles`, `inchi`) (required; full list below)
* `-s,--subject <arg>` - subject type (`iri`, `uuid`, `bnode`; `iri` by default; for all formats excluding cypher, cvme, smiles, inchi)
* `-b,--base <arg>` - molecule subject base for 'iri' subject type ('https://example.com/molecule#entity' by default)

Remember about the appropriate file path when using the Docker image. Suppose you mounted your local directory `/home/user/input` under `/app/input` and the path to the SDF file you want to use in SDFEater is `/home/user/input/file.sdf`. In this case, enter the path `/app/input/file.sdf` or `input/file.sdf` as the value of the `-i` argument.

## Output formats

You can specify the output format using `-f,--format`. Available output formats:

* `cypher` - [Cypher](https://neo4j.com/developer/cypher-query-language/) molecule, atoms, bonds and relation ready to [import to the Neo4j graph database](https://neo4j.com/developer/kb/export-sub-graph-to-cypher-and-import/),
* `cypheru` - the same as `cypher` option but try to generate full database URLs instead of IDs,
* `cypherp` - the same as `cypher` option but add additional atoms data from [periodic table](https://github.com/lszeremeta/SDFEater/blob/master/src/main/resources/pl/edu/uwb/ii/sdfeater/periodic_table.json),
* `cypherup` - the same as `cypher` option but added URLs and additional atoms data from [periodic table](https://github.com/lszeremeta/SDFEater/blob/master/src/main/resources/pl/edu/uwb/ii/sdfeater/periodic_table.json),
* `cvme` - [CVME](http://cs.aalto.fi/en/current/events/2017-09-22-002/) file format based on SKOS,
* `smiles` - plain text SMILES (if available in the molecule property)
* `inchi` - plain text InChI (if available in the molecule property)
* `turtle` - [Terse RDF Triple Language](https://www.w3.org/TR/turtle/) (based on [MolecularEntity profile](https://bioschemas.org/profiles/MolecularEntity/0.5-RELEASE/))
* `ntriples` - [N-Triples](https://www.w3.org/TR/n-triples/) (based on [MolecularEntity profile](https://bioschemas.org/profiles/MolecularEntity/0.5-RELEASE/))
* `rdfxml` - [RDF/XML](https://www.w3.org/TR/rdf-syntax-grammar/) (based on [MolecularEntity profile](https://bioschemas.org/profiles/MolecularEntity/0.5-RELEASE/))
* `rdfthrift` - [RDF Binary encoding using Thrift](https://afs.github.io/rdf-thrift/rdf-binary-thrift.html) (based on [MolecularEntity profile](https://bioschemas.org/profiles/MolecularEntity/0.5-RELEASE/))
* `jsonldhtml` - [JSON-LD](https://json-ld.org/) with HTML (based on [MolecularEntity profile](https://bioschemas.org/profiles/MolecularEntity/0.5-RELEASE/))
* `jsonld` - [JSON-LD](https://json-ld.org/) (based on [MolecularEntity profile](https://bioschemas.org/profiles/MolecularEntity/0.5-RELEASE/))
* `rdfa` - Simple HTML with [RDFa](http://rdfa.info/) (based on [MolecularEntity profile](https://bioschemas.org/profiles/MolecularEntity/0.5-RELEASE/))
* `microdata` - Simple HTML with [Microdata](https://www.w3.org/TR/microdata/) (based on [MolecularEntity profile](https://bioschemas.org/profiles/MolecularEntity/0.5-RELEASE/))

## What is structured data

[Structured data](https://developers.google.com/search/docs/guides/intro-structured-data) are additional data placed on websites. They are not visible to ordinary internet users but can be easily processed by machines. There are 3 formats that we can use to save structured data - [JSON-LD](https://json-ld.org/), [RDFa](http://rdfa.info/), and [Microdata](https://www.w3.org/TR/microdata/). SDFEater supports them all and uses the [MolecularEntity profile](https://bioschemas.org/profiles/MolecularEntity/0.5-RELEASE/).

## Additional examples

```shell
java -jar SDFEater-VERSION-jar-with-dependencies.jar -i ../examples/chebi_special_char_test.sdf -f cypherup
```

Returns [Cypher](https://neo4j.com/developer/cypher-query-language/) with added periodic table data for atoms and replaced chemical database IDs with URL. SDFEater run from a JAR file.

```shell
java -jar SDFEater-VERSION-jar-with-dependencies.jar -i ../examples/chebi_test.sdf -f jsonld  > molecules.jsonld
```

Returns [JSON-LD](https://json-ld.org/) and redirects output to `molecules.jsonld` file. SDFEater runs from a JAR file.

```shell
docker run -it --rm --name sdfeater-app --mount type=bind,source=/home/user/input,target=/app/input,readonly lszeremeta/sdfeater:latest -i input/chebi_test.sdf -f microdata  > molecules.html
```

Returns simple HTML with added [Microdata](https://www.w3.org/TR/microdata/) and redirects output to `molecules.html` file. Run from pre-build Docker image.

In the `examples` directory you can find example SDF files based on data from [ChEBI](https://www.ebi.ac.uk/chebi/init.do) and [DrugBank  open structures](https://www.drugbank.ca/releases/latest#open-data) databases.

## Publications and resources

If you need more detailed information, take a look at these publications and resources. There you will find a detailed description of the parser, performance tests, and example Cypher outputs.

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

The sample SDF files in the examples and test directory are based on data from [ChEBI](https://www.ebi.ac.uk/chebi/init.do) ([CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)) and [DrugBank](https://www.drugbank.ca/releases/latest#open-data) open structures ([CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/)) databases.

## Contribution

Would you like to improve the SDFEater? Great! We are waiting for your help and suggestions. If you are new to open source contributions, read [How to Contribute to Open Source](https://opensource.guide/how-to-contribute/).

## License

Distributed under [MIT License](https://github.com/lszeremeta/chebi-sdf-parser/blob/master/LICENSE).

## See also

These projects can also be useful:

* [Molstruct](https://github.com/lszeremeta/molstruct) - Convert chemical molecule data CSV files to structured data formats
* [MEgen](https://github.com/lszeremeta/MEgen) - Convenient online form to generate structured data about molecules
