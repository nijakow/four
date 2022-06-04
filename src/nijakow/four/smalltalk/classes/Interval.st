ArrayedCollection subclass: #Interval;
instanceVariableNames: 'from to'
<<<
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
  ^ (from max: to) asInt - (from min: to) asInt + 1
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
---

Interval class
<<<
from: lower to: upper
[
  ^ (Interval new) from: lower to: upper
]
