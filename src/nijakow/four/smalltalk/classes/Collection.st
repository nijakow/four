Collection
---
init
[
]
---
isEmpty
[
  ^ self size = 0
]
---
writeOn: w
[
    w << '#('.
    self do: [ :v | w << v << ' ' ].
    w << ')'.
]
---
asArray
[
  ^ (Array new: self size) takeFrom: self
]
---
addTo: collection
[
    self do: [ :v :k | collection at: k put: v ].
  ^ self
]
---
takeFrom: collection
[
    collection do: [ :v :k | self at: k put: v ].
  ^ self
]
---
addAll: collection
[
    collection do: [ :v | self add: v ].
  ^ collection
]
---
species
[
  ^ self class
]
---
select: block | c
[
    c := self species new.
    self do: [ :v | (block value: v) ifTrue: [ c add: v ]. ].
  ^ c
]
---
reject: block | c
[
    c := self species new.
    self do: [ :v | (block value: v) ifFalse: [ c add: v ]. ].
  ^ c
]
---
collect: block | c
[
    c := self species new.
    self do: [ :v | c add: (block value: v) ].
  ^ c
]
---
detect: block
[
  ^ self detect: block ifNone: [ 'detect: expected a value!' throw ]
]
---
detect: block ifNone: exceptionBlock
[
    self do: [ :v | (block value: v) ifTrue: [ ^ v ] ].
  ^ exceptionBlock value
]
