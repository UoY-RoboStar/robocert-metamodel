# RoboCert Metamodel

This repository contains the Eclipse plugins that implement that metamodel of RoboCert.

_This is pre-release material._  We greatly appreciate any suggestions,
comments, and issues.


## What this repository contains

This repository contains:

- the `robostar.robocert` project, containing:
  - the metamodel;
  - utility classes;
  - well-formedness condition checks;
- the `robostar.robocert.tests` project, containing unit tests;
- various other projects either generated from the metamodel or used to support deployment.


### Metamodel

Packages `robostar.robocert` and `robostar.robocert.impl` contain the metamodel proper.
The EMF tooling generates this semi-automatically from the model files.

The main representation of the metamodel is
`robostar.robocert/model/RoboCert.emf`.  From this, one can generate
`robostar.robocert/model/RoboCert.ecore` by right-clicking in Eclipse and
selecting 'Generate Ecore Model'.  The two files _must_ be kept in sync, and
the Emfatic version _must_ remain the primary source of truth.


### Utility classes

Package `robostar.robocert.util` contains various utility classes and methods for both RoboChart
and RoboCert, assisting in:

- resolving and finding various aspects of a model from other parts of a model;
- constructing synthetic pieces of model (especially for testing purposes);
- other miscellaneous activities (such as stream construction).


### Well-formedness conditions

Package `robostar.robocert.wfc` contains logic for checking well-formedness of RoboCert model
elements.  We intend this to be connected to the Xtext validator logic in the textual plugin, but
keep it in the metamodel so that it can be tested alongside the metamodel.


## Dependencies

- Java 17.  This should now be readily available for modern systems (I was able
  to install it on Ubuntu 20.04, for instance), but likely won't be your
  default install at time of writing.
- An Eclipse setup (2021-12+) with plugin development tools and the latest
  version of RoboChart's metamodel checked
  out (see, for instance, the
  [RoboChart metamodel](https://github.com/UoY-RoboStar/robochart-metamodel)
  dependency notes);
- [Emfatic](https://www.eclipse.org/emfatic/) (you will need 1.1.0.202207260534
  or higher; at time of writing, this means **you will need to use the interim
  branch**.  This is because of a known bug with string escaping in previous
  versions.)
* [Eclipse Modelling Framework](https://www.eclipse.org/modeling/emf/) Ecore
  processing plugins (tested with 2.27.0)
* [Maven](https://maven.apache.org) Maven (tested with 3.8.5)
* Git

Other items may be required.  If the list above is incomplete, please file an
issue.


## How to build and run

Please inform us of any build failures.

### Maven

1. `$ mvn clean install`


### Eclipse

1. Right click `robostar.robocert/model/RoboCert.emf`
        1. Click `Generate Ecore Model`
2. Right click `robostar.robocert/model/GenerateRoboCertModel.mwe2`
        1. Click `Run As > MWE2 Workflow`


## Protocol for updating the metamodel

Whenever updating the metamodel, follow these steps:

1. Perform regression testing
2. Change the [language reference manual](https://github.com/UoY-RoboStar/robocert-reference-manual)

If changes to documentations are not possible immediately, create issues
indicating exactly what needs to be done.