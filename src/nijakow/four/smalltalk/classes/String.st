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
