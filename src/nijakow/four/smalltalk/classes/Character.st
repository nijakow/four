Character
---
writeOn: w
[
    w char<< self
]
---
storeOn: w
[
    (self = $\s)  ifTrue: [ w << '$\\s'              ]
                 ifFalse: [ w << '$' << self escaped ].
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
