Object subclass: #TestResult;
instanceVariableNames: 'failures passed'
<<<
init
[
    failures := Set new.
    passed := Set new.
  ^ self
]
---
runCase: aTestCase
[
    Object handle: [ aTestCase runCase. passed add: aTestCase ]
               do: [ failures add: aTestCase                  ].
]
---
succeeded
[
  ^ failures isEmpty
]
---
writeOn: w
[
    (self succeeded) ifTrue: [
        w out: 'a successful '.
    ] ifFalse: [
        w out: 'a failed '.
    ].
    super writeOn: w.
]
