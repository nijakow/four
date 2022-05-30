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
storeOn: w
[
    w out: '#('.
    self do: [ :v | w out: v; out: ' ' ].
    w out: ')'.
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
select: block | c
[
    c := self class new.
    self do: [ :v :i | (block value: v) ifTrue: [ c add: v ]. ].
  ^ c
]
---
reject: block | c
[
    c := self class new.
    self do: [ :v :i | (block value: v) ifFalse: [ c add: v ]. ].
  ^ c
]
---
collect: block | c
[
    c := self class new.
    self do: [ :v :i | c at: i put: (block value: v) ].
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
