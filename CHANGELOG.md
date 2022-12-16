# Changelog

## Unreleased (2022-11-21)

### Changed

**Breaking**: This version greatly and incompatibly changes the top-level
syntax of RoboCert to align closer to the other RoboStar textual languages.  In
specific:

- Whitespace is no longer significant.
- `group`s and `sequence`s now require opening and closing braces for
  block delimiting (the interior of sequences still use Mermaid-style
  syntax and therefore do not use braces).
- The use of `:` to denote a one-liner combined fragment in sequences
  has been abolished, and should be replaced with an `end` at the end of
  the line instead.
- Any use of `:` in branch fragments should be removed without replacement.
- `deadline (X units) on Y` is now `deadline (X units on Y)`.
- `wait (X units) on Y` is now `wait (X units on Y)`.

Another **breaking change** is that the CSP generator, TikZ generator, and
textual plugin are all now separate; the former two will be provided as
separate plugins from now.

### Added

- Added `use enum package::EnumName` to pull enumerations into scope, to
  avoid the fact that in RoboChart expressions `package::EnumName::X`
  would be parsed as an enumeration `package`.
- The word `set` may now be omitted in a reference to a message set (but not
  a definition of one).  For instance, `any except SetName until deadlock end`
  is now legal.  The valid forms are `message set`, `set`, or no keyword.
- `specification group`s may now be called `property group`s.  The latter term
  is preferred and may become the sole valid name in future versions.
- Added `until ... do ... end` and `timeout (X units) ... end`, corresponding
  to the new `DoUntilFragment` and `UntilFragment` forms.
- Added `anything (in X except Y)`, etc. (eg, without a corresponding `until`),
  corresponding to the new `AnyFragment`.  Note that such an `anything` must be
  at the end of its operand or interaction; for this reason, any attempt to put
  an `anything` before an `until ... do ... end` block will fail to parse.
- Lists that previously accepted `x, y, z`, `x, y, and z`, and `x, y and z`
  now admit any arbitrary combination of `,`, `and`, and `, and`.

### Removed

- `deadlock on Y` is no longer available.

### Fixed

- Changed scoping rules to allow enumerations to be scoped properly.
- Expression generator now emits enumerations properly.
- **BREAKING**: Validation rules for well-formedness conditions `SMA1`
  (message argument lists must be same size as topics' parameter lists) and
  `SMA2` (message arguments must be type-compatible with parameters) have
  been added to the validator.  Previously ill-formed but silently accepted
  input will now be rejected.


## 0.1.0

Initial release.
