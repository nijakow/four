String
<<<
size
[
  ^ <primitive:string/size>
]
---
at: index
[
  ^ <primitive:string/at:>
]
---
from: from to: to
[
  ^ <primitive:string/from:to:>
]
---
char+ c
[
  ^ <primitive:string/charplus:>
]
---
* n | v
[
    v := ''.
    n timesDo: [ v := v + self ].
  ^ v
]
---
compile
[
  ^ <primitive:string/compile>
]
---
asString
[
  ^ self
]
---
asSymbol
[
  ^ <primitive:string/asSymbol>
]
---
< other
[
  ^ <primitive:string/less:>
]
---
<= other
[
  ^ [ self < other ] or: [ self = other ]
]
---
> other
[
  ^ (self <= other) not
]
---
>= other
[
  ^ (self < other) not
]
---
writeOn: w
[
    self do: [ :c | w out: c ]
]
---
storeOn: w
[
    w out: $'.
    self do: [ :c | w out: c escaped ].
    w out: $'.
]
---
isWhitespace
[
    self do: [ :c | (c isWhitespace) ifFalse: [ ^ false ] ].
  ^ true
]
---
splitOn: aBlock | strs os
[
    strs := Vector new.
    os := StringOutputStream new.
    self do: [ :c |
        (aBlock value: c)  ifTrue: [ strs add: os close. os clear. ]
                          ifFalse: [ os out: c.                    ].
    ].
    (os isEmpty) ifFalse: [ strs add: os close ].
  ^ strs asArray
]
---
split
[
  ^ self splitOn: [ :c | c isWhitespace ]
]
---
trim
[
    "TODO"
  ^ self
]
---
parseUInt | v
[
    v := 0.
    self do: [ :c |
        (c isDigit) ifFalse: [ ^ nil ].
        v := (v * 10) + (c digitValue).
    ].
  ^ v
]
---
parseInt
[
    "TODO"
  ^ self parseUInt
]
