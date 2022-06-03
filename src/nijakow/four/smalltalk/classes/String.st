String
---
writeOn: w
[
    self do: [ :c | w << c ]
]
---
storeOn: w
[
    w << $'.
    self do: [ :c | w << c escaped ].
    w << $'.
]
---
isWhitespace
[
    self do: [ :c | (c isWhitespace) ifFalse: [ ^ false ] ].
  ^ true
]
