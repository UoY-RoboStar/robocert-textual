# RoboCert sequence experiment

This repository contains an attempt to build a sequence diagram language
for eventual inclusion into the RoboStar assertions language (ie RoboCert).

_Warning:_ I'm using this project to learn about the various workflows needed
to bring up a RoboStar language, so expect the following:

- things in strange locations;
- build scripts that aren't quite right;
- things that work on my computer that don't work on anyone else's.

I greatly appreciate any suggestions, comments, and issues.

## Example

(This example will go out of date fairly quickly as the language improves.)

```
sequence ExampleSeq
  for module ExampleRobot as R, world as W {	
    anything except { operation deviceOn() from D to P } until
      operation deviceOff() from D to P
    then anything except { operation deviceOff() from D to P } until
      operation deviceOn() from D to P
    then anything until
      end
}
assertion Foo: ExampleSeq is not observed
assertion Bar: ExampleSeq holds
```

## Layout

Packages are subject to renaming and moving at any time.

- `robocalc.robocert.model` is the metamodel for the sequence diagram language.
- `robocalc.robocert` is the textual (Xtext) language for the above metamodel.
- Everything else is, generally, artefacts derived from `robocalc.robocert`

## Dependencies

- The RoboChart metamodel (see below - you may need to check out a local copy);
- The RoboChart textual grammar (we currently inherit from it in Xtext - this
  may change).

## How to use

I haven't yet been able to get Maven to work on my machine, so the easiest
approach for now is to use Eclipse.  Maven may work for you - please let me
know how you get on.

- You may need to have the RoboChart metamodel checked out as
  `circus.robocalc.robochart.parent` in the same directory as where you
  checked out this project.  There are some references in the `genmodel` that
  are path-relative, and I'm not yet sure how to make them more robust.
- The authoritative source for the metamodel is the Emfatic file:
  `robocalc.robocert.model/model/RoboCert.emf`.  Please generate the Ecore
  model from this, not the other way round.
- There is no automation for generating Ecore from Emfatic set up yet; in
  Eclipse, you can right-click the Emfatic file in the browser and select
  `Generate Ecore Model` for now.
- There are two MWE2 workflows: one in `robocalc.robocert.model` generates the
  metamodel Java files from the Ecore (_not_ the Emfatic file), and another
  in `robocalc.robocert` generates the Xtext artefacts from the grammar.
- Once everything has been generated, you should be able to run
  `robocalc.robocert` as an Eclipse plugin.