Object subclass: #TestSuite;
instanceVariableNames: 'tests';
category: 'SUnit-Kernel'
<<<
init
[
    tests := Set new.
  ^ self
]
---
addTest: case
[
    tests add: case
]
---
addTests: cases
[
    cases do: [ :eachTest | self addTest: eachTest ]
]
---
resultClass
[
  ^ TestResult
]
---
run | result
[
    result := self resultClass new.
    "TODO: The original Squeak SUnit implementation uses #ensure: here to protect resetting resources!"
    self run: result.
  ^ result
]
---
run: aResult
[
    tests do: [ :each |
        each run: aResult
    ].
]
