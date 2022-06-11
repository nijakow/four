Symbol
<<<
asSymbol
[
  ^ self
]
---
asString
[
  ^ <primitive:symbol/asString>
]
---
name
[
  ^ self asString
]
---
globalValue
[
  ^ <primitive:symbol/globalValue>
]
---
globalValue: val
[
  ^ <primitive:symbol/globalValue:>
]
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
