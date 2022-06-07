String
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
parseInt
[
    "TODO"
  ^ nil
]
