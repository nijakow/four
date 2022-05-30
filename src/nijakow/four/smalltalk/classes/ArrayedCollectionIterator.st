Iterator subclass: #ArrayedCollectionIterator;
instanceVariableNames: 'indexable index'
---
initWithIndexable: _indexable
[
    super init.
    indexable := _indexable.
    index := 1.
  ^ self
]
---
hasNext
[
  ^ index <= indexable size
]
---
next | n
[
    n := indexable at: index.
    index := index + 1.
  ^ n
]
---
withReset: block | i
[
    i := index.
  ^ block value: [ index := i ].
]
