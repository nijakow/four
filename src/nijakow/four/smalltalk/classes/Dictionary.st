--- needs: Collection
Collection subclass: #Dictionary;
instanceVariableNames: 'vector'
---
init
[
    vector := Vector new.
]
---
do: block
[
    vector do: [ :v | block value: v value value: v key ]
  ^ self
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
