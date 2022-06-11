TestCase subclass: #BasicTests;
category: 'KernelTests-Basic'
---
setUp
[
]
---
tearDown
[
]
---
performTest
[
    self should:   [ 42  = 42 ].
    self shouldnt: [ 42 != 42 ].
]
