Symbol
---
writeOn: w
[
    w out: $#;
      store: self name.
]
---
Symbol class
<<<
boundInstancesDo: aBlock
[
    self allInstancesDo: [ :sym |
        (sym globalValue != nil) ifTrue: [
            aBlock value: sym value: sym globalValue
        ]
    ]
]
