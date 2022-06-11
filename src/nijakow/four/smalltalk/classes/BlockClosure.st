BlockClosure;
category: 'Kernel-Methods'
<<<
withBreak
[
  ^ [ self value: [ ^ self ] ] repeat
]
---
withContinue
[
     [
       ^ self withBreak
     ] repeat
]
---
and: aBlock
[
    (self value) ifFalse: [ ^ false ].
  ^ aBlock value
]
---
or: aBlock | v
[
    v := self value.
    v ifTrue: [ ^ v ].
  ^ aBlock value
]
