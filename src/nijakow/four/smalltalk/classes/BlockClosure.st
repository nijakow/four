BlockClosure;
category: 'Kernel-Methods'
<<<
whileTrue: body
[
    [
        (self value) ifFalse: [ ^ self ].
        body value.
    ] repeat.
]
---
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
