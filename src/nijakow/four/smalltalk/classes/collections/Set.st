Collection subclass: #Set;
instanceVariableNames: 'vector';
category: 'Collections-Unordered'
---
init
[
    vector := Vector new.
  ^ self
]
---
do: block
[
    vector do: [ :v | block value: v value: v ].
  ^ self
]
---
size
[
  ^ vector size
]
---
add: e
[
    (self contains: e) ifFalse: [ vector add: e ].
  ^ self
]
---
remove: e
[
    0 to: (vector size - 1) do: [ :i |
        ((vector at: i) = e) ifTrue: [
            vector remove: i.
          ^ self
        ].
    ].
  ^ self
]
---
contains: e
[
    vector do: [ :v :i |
        (v = e) ifTrue: [ ^ true ].
    ].
  ^ false
]
