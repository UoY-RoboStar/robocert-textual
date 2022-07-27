# RoboCert textual plugins

This repository contains the parts of the RoboCert prototype that are not
the [metamodel](https://github.com/UoY-RoboStar/robocert-metamodel).  This
includes, as the name suggests, the textual editor; temporarily, it also
contains the unit tests and CSP generator.

_This is pre-release material._  We greatly appreciate any suggestions,
comments, and issues.


## Dependencies

- Java 17.  This should now be readily available for modern systems (I was able
  to install it on Ubuntu 20.04, for instance), but likely won't be your
  default install at time of writing.
- An Eclipse setup with plugin development tools and the latest version of
  RoboChart's metamodel, textual editor, and CSP generator checked out (see,
  for instance, the
  [RoboChart textual editor](https://github.com/UoY-RoboStar/robochart-textual)
  dependency notes);
- For tests, Hamcrest 2.2.  At time of writing, Eclipse and JUnit
  tend to ship Hamcrest 1.3.  If using Eclipse, add the Eclipse Orbit
  repository (eg [2021-12](https://download.eclipse.org/tools/orbit/downloads/2021-12/))
  and get Hamcrest from there.  Not doing this will result in Eclipse
  pulling in the wrong Hamcrest and spouting very cryptic errors.


## How to build and run

Please inform us of any build failures.

### Maven

1. `$ mvn clean install`

Known issues:

- If Maven fails at the test stage with a very large log backtrace mentioning
  some combination of `circus.robocalc.robochart.generator.csp` and `SWTError`,
  you may need to check the target environment triples Tycho is configured with;
  we depend on the RoboChart CSP generator, which depends on the Eclipse UI,
  which depends on SWT, and therefore needs to be told in advance if it's being
  built for an exotic platform (ie, not x86 Windows/Linux/macOS or Apple Silicon
  macOS).

### Eclipse

1. Right click `robostar.robocert.textual/src/robostar.robocert.textual/GenerateRoboCert.mwe2`
    1. select 'Run As' > 'MWE2 Workflow'
2. To run: 
    1. Right click `robostar.robocert.textual.parent`
        1. select 'Run As'
        2. double click 'Eclipse Application'
    2. Select the new configuration
        1. click 'Run'


## Protocol for updating the tool

Whenever updating the tool, follow these steps:

1. Perform regression testing
2. Change the [language reference manual](https://github.com/UoY-RoboStar/robocert-reference-manual)

When the tool manual is created, there will be a further step to change that,
too.

If changes to documentations are not possible immediately, create issues
indicating exactly what needs to be done.