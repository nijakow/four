--- needs: Collection
Collection subclass: #Dictionary;
instanceVariableNames: 'vector'
---
init
[
    vector := Vector new.
  ^ self
]
---
do: block
[
    vector do: [ :v | block value: v value value: v key ].
  ^ self
]
---
size
[
  ^ vector size
]
---
at: key
[
    vector do: [ :v |
        (v key = key) ifTrue: [ ^ v value ]
    ].
  ^ nil
]
---
at: key put: value
[
    vector do: [ :v |
        (v key = key) ifTrue: [
            v value: value.
          ^ self
        ].
    ].
    vector add: ((Association new) key: key value: value).
  ^ self
]
---
keys | s
[
    s := Set new.
    self do: [ :v :k | s add: k ].
  ^ s
]
---
values | s
[
    s := Set new.
    self do: [ :v :k | s add: v ].
  ^ s
]
