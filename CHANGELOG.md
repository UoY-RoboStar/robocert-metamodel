# 0.2.0 (unreleased)

## Added

- Added `TimeoutFragment`, equivalent to a `DoUntilFragment` with a
  `WaitFragment` trigger.
  (2022-12-14)
- Added `AnyUntilFragment`, which is semantically equivalent to the
  old behaviour of `UntilFragment`.
  (2022-12-14)
- Added `Synchronicity` enum and `synchronicity` feature for `Message`s.
  (2022-12-09)
- Added `SequentialFragment`, which marks fragments as requiring strict
  ordering semantics, and `AnyFragment` (which is a `SequentialFragment`).
  (2022-12-05; github #10)
- Added `importedEnums` to `SpecificationGroup`, to allow for a scoping
  workaround in resolving `Enumeration`s that are not otherwise in scope.
  (2022-11-02)

## Changed

- `DeadlineFragment` is now an instance of `LifelineFragment`.
  (2022-12-09)
- (BREAKING) Removed `Occurrence` and `OccurrenceFragment`; anything that was
  named `*Occurrence` is now `*Fragment`.
  (2022-12-09)
- (BREAKING) Removed `DeadlockOccurrence`.
  (2022-12-09)
- (BREAKING) Separated `UntilFragment` and `AnyFragment`.  The latter retains
  the `intraMessages` (now `allowed`) but is not a `BlockFragment`; the former
  is now a `SequentialFragment` called `DoUntilFragment` and the old `body` is
  now `trigger`.  (`body` now refers to the part of the `UntilFragment` which
  runs _before_ `trigger`, ie that which was previously implicitly an
  `AnyFragment`.
  (2022-11-05; github #1)
- (BREAKING) Distinction made between `Actor`s and `Lifeline`s.  Lifelines
  contain information about an `Actor` that is forming a lifeline on a diagram,
  alongside information about variables defined on it.  Conversely,
  `Interaction`s may no longer have directly-defined variables.
  (some time before 2023-01-31)
- (BREAKING) Distinction made between `Actor`s and `MessageEnd`s.  The
  erstwhile `World` actor is now a `MessageEnd` called `Gate`.  A new
  `MessageOccurrence` (not to be confused with the now-renamed
  `MessageFragment`) is a `MessageEnd` over an `Actor`.  This new
  naming convention better reflects UML.
  (2023-01-31)
- (BREAKING) Node resolution for ends and worlds now requires the set of
  active lifelines.  The world calculation has changed to allow any components
  of the target that are not active lifelines to be considered.
  (2023-01-31)

# 0.1.0

Initial release.
