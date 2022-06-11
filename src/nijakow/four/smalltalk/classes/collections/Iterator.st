Object subclass: #Iterator;
instanceVariableNames: '';
category: 'Collections-Iterators'
---
do: block | i
[
    i := 1.
    [ self hasNext ] whileTrue: [
        block value: (self next)
              value: i.
        i := i + 1.
    ].
  ^ self
]
