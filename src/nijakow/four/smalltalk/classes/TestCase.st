Object subclass: #TestCase;
instanceVariableNames: 'testSelector';
category: 'SUnit-Kernel'
<<<
init
[
]
---
setUp [ self subclassResponsibility ]
---
performTest [ self subclassResponsibility ]
---
tearDown [ self subclassResponsibility ]
---
runCase
[
    "TODO: Timeout and #ensure!"
    self setUp.
    self performTest.
    self tearDown.
]
---
run: aResult
[
    aResult runCase: self.
]
---
assert: aBooleanOrBlock
[
    aBooleanOrBlock value ifFalse: [ 'Assertion failed!' throw ]
]
---
deny: aBooleanOrBlock
[
    self assert: aBooleanOrBlock value not
]
---
should: aBlock
[
    self assert: aBlock value
]
---
shouldnt: aBlock
[
    self deny: aBlock value
]
---
fail
[
  ^ self assert: false
]
