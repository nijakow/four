Integer
<<<
asChar
[
  ^ <primitive:integer/asChar>
]
---
asInt
[
  ^ self
]
---
+ other
[
  ^ <primitive:integer/plus:>
]
---
- other
[
  ^ <primitive:integer/minus:>
]
---
* other
[
  ^ <primitive:integer/mul:>
]
---
/ other
[
  ^ <primitive:integer/div:>
]
---
mod: other
[
  ^ <primitive:integer/mod:>
]
---
negate
[
  ^ self * -1
]
---
bitAnd: other
[
  ^ <primitive:integer/bitAnd:>
]
---
bitOr: other
[
  ^ <primitive:integer/bitOr:>
]
---
bitXor: other
[
  ^ <primitive:integer/bitXor:>
]
---
bitNot
[
  ^ <primitive:integer/bitNot>
]
---
leftShift: other
[
  ^ <primitive:integer/leftShift:>
]
---
rightShift: other
[
  ^ <primitive:integer/rightShift:>
]
---
< other
[
  ^ <primitive:integer/less:>
]
---
writeOn: w
[
    w out: self toString
]
