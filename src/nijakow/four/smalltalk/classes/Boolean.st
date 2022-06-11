Boolean;
category: 'Kernel-Objects'
<<<
and: other
[
    self  ifFalse: [ ^ false ].
    other ifFalse: [ ^ false ].
  ^ true
]
---
or: other
[
    self ifTrue: [ ^ self ].
  ^ other
]
---
writeOn: w
[
    self  ifTrue: [ w out: 'true'  ]
         ifFalse: [ w out: 'false' ].
]
