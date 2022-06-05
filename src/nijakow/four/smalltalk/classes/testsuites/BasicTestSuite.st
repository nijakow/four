TestSuite subclass: #BasicTestSuite
---
init
[
    super init.
    self addTest: (BasicTests new).
  ^ self
]
