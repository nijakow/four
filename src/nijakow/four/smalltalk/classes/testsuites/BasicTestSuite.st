TestSuite subclass: #BasicTestSuite;
category: 'KernelTests-Basic'
---
init
[
    super init.
    self addTest: (BasicTests new).
  ^ self
]
