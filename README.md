# robocert-metamodel

This repository contains the Eclipse plugins that implement that metamodel of
RoboCert.  It also includes Java code that implements custom functionality on
the model, as well as common helper functionality.

The main representation of the metamodel is
`robostar.robocert/model/RoboCert.emf`.  From this, one can generate
`robostar.robocert/model/RoboCert.ecore` by right-clicking in Eclipse and
selecting 'Generate Ecore Model'.  The two files _must_ be kept in sync, and
the Emfatic version _must_ remain the primary source of truth.

## Development platform requirements

* [Eclipse IDE](https://www.eclipse.org/downloads/) (tested with 2022-03)
* [Emfatic](https://www.eclipse.org/emfatic/) (tested with 1.0)
* [Eclipse Modelling Framework](https://www.eclipse.org/modeling/emf/) Ecore
  processing plugins (tested with 2.27.0)
* [Maven](https://maven.apache.org) Maven (tested with 3.8.5)
* Git

Other items may be required.  If the list above is incomplete, please file an
issue.

### Build (maven) ###

TODO: this is almost certainly broken at the moment.

1. `mvn clean install`

### Build (eclipse) ###

You may need to make sure that `circus.robocalc.robochart` is available two
levels up from the models directory, possibly by symlinking it from wherever
your source copy of
[robochart-metamodel](https://github.com/UoY-RoboStar/robochart-metamodel) is.

1. Right click `robostar.robocert/model/RoboCert.emf`
        1. Click `Generate Ecore Model`
2. Right click `robostar.robocert/model/GenerateRoboCertModel.mwe2`
        1. Click `Run As > MWE2 Workflow`
