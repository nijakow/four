Iterator subclass: #ArrayedCollectionIterator;
instanceVariableNames: 'indexable index';
category: 'Collections-Iterators'
---
initWithIndexable: _indexable
[
    super init.
    indexable := _indexable.
    index := 1.
  ^ self
]
---
peek
[
  ^ indexable at: index
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
