ArrayedCollection
---
do: block
[
    1 to: self size do: [ :index | block value: (self at: index) value: index ].
  ^ self
]
---
iterator
[
  ^ (ArrayedCollectionIterator new) initWithIndexable: self
]
