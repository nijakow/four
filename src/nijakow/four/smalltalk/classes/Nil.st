Nil;
category: 'Kernel-Objects'
<<<
isNil: aBlock
[
  ^ aBlock value
]
---
notNil: aBlock
[
  ^ self
]
---
otherwise: obj
[
  ^ obj
]
---
writeOn: w
[
  ^ w out: 'nil'
]
