# RoboCert textual plugins

This repository contains the parts of the RoboCert prototype that are not
the [metamodel](https://github.com/UoY-RoboStar/robocert-metamodel).

_Warning:_ I'm using this project to learn about the various workflows needed
to bring up a RoboStar language, so expect the following:

- things in strange locations;
- build scripts that aren't quite right;
- things that work on my computer that don't work on anyone else's.

I greatly appreciate any suggestions, comments, and issues.

## Dependencies

- The RoboChart metamodel (see below - you may need to check out a local copy);
- The RoboCert metamodel;
- The RoboChart textual grammar (we currently inherit from it in Xtext - this
  may change).
- Java 17.  This should now be readily available for modern systems (I was able
  to install it on Ubuntu 20.04, for instance), but likely won't be your
  default install at time of writing.

## How to use

I haven't yet been able to get Maven to work on my machine, so the easiest
approach for now is to use Eclipse.  Maven may work for you - please let me
know how you get on.

- The MWE2 workflow in `robostar.robocert.textual` generates the Xtext artefacts
- from the grammar.
- Once everything has been generated, you should be able to run
  `robostar.robocert.textual` as an Eclipse plugin.
- For the tests, we need Hamcrest 2.2; at time of writing, Eclipse and JUnit
  tend to ship Hamcrest 1.3.  If using Eclipse, add the Eclipse Orbit
  repository (eg [2021-12](https://download.eclipse.org/tools/orbit/downloads/2021-12/))
  and get Hamcrest from there.  Not doing this will result in Eclipse
  pulling in the wrong Hamcrest and spouting very cryptic errors.

