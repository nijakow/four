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
