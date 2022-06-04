Character
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