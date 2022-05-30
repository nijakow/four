Object subclass: #Color;
instanceVariableNames: 'r g b'
---
r [ ^ r ]
---
g [ ^ g ]
---
b [ ^ b ]
---
r: rv g: gv b: bv
[
    r := rv.
    g := gv.
    b := bv.
  ^ self
]
---
asInt
[
  ^ (r leftShift: 16) bitOr: (g leftShift: 8); bitOr: b
]
---
"This implementation is not pretty, but it works :D"
toHex | i digits
[
    digits := '0123456789abcdef'.
    i := self asInt.
    ''
  char+ (digits at: ((i rightShift: 20; mod: 16) + 1))
  char+ (digits at: ((i rightShift: 16; mod: 16) + 1))
  char+ (digits at: ((i rightShift: 12; mod: 16) + 1))
  char+ (digits at: ((i rightShift: 8; mod: 16) + 1))
  char+ (digits at: ((i rightShift: 4; mod: 16) + 1))
  char+ (digits at: ((i rightShift: 0; mod: 16) + 1))
]
