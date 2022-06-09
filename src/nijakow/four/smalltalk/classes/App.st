Object subclass: #App
<<<
init
[
    super init.
  ^ self
]
---
run
[
    self subclassResponsibility
]
---
exec: app
[
  ^ app run
]
---
App class
<<<
launch
[
  ^ (self new) run
]
