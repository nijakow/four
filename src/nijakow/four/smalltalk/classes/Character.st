Character;
category: 'Kernel-Numbers'
<<<
asInt
[
  ^ <primitive:char/asInt>
]
---
asString
[
  ^ <primitive:char/asString>
]
---
+ other
[
  ^ <primitive:char/plus:>
]
---
- other
[
  ^ <primitive:char/minus:>
]
---
< other
[
  ^ <primitive:char/less:>
]
---
isWhitespace
[
  ^   (self = $\s);
  or: (self = $\t);
  or: (self = $\n);
  or: (self = $\r).
]
---
writeOn: w
[
    w charOut: self
]
---
storeOn: w
[
    (self = $\s)  ifTrue: [ w out: '$\\s'                 ]
                 ifFalse: [ w out: '$'; out: self escaped ].
]
---
escaped
[
    (self = $\n) ifTrue: [ ^ '\\n' ].
    (self = $\r) ifTrue: [ ^ '\\r' ].
    (self = $\t) ifTrue: [ ^ '\\t' ].
    (self = $\e) ifTrue: [ ^ '\\e' ].
    (self = $\b) ifTrue: [ ^ '\\b' ].
    (self = $\{) ifTrue: [ ^ '\\{' ].
    (self = $\}) ifTrue: [ ^ '\\}' ].
  ^ self asString
]
---
isDigit
[
  ^ self between: $0 and: $9
]
---
digitValue
[
  ^ self asInt - $0 asInt
]
