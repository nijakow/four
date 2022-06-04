ArrayedCollection subclass: #Interval;
instanceVariableNames: 'from to'
---
from: _from to: _to
[
    from := _from.
    to := _to.
  ^ self
]
---
species
[
  ^ Array
]
---
size
[
  ^ (from max: to) - (from min: to)
]
---
at: index
[
  ^ from + (index - 1)
]
---
at: index put: value
[
    self notImplementedError.
]
