TestCase subclass: #BasicTests
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
