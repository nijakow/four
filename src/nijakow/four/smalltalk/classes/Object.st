Object
<<<
value [ ^ self ]
---
value: v1 [ self value ]
---
value: v1 value: v2 [ self value: v1 ]
---
value: v1 value: v2 value: v3 [ self value: v1 value: v2 ]
---
value: v1 value: v2 value: v3 value: v4 [ self value: v1 value: v2 value: v3 ]
---
class
[
  ^ <primitive:class>
]
---
isKindOf: class
[
  ^ <primitive:isKindOf>
]
---
asBool
[
  ^ true
]
---
not
[
    self  ifTrue: [ ^ false ]
         ifFalse: [ ^ true  ]
]
---
= other
[
  ^ <primitive:=>
]
---
!= other
[
  ^ (self = other) not
]
---
isNil: aBlock
[
  ^ (self = nil) ifTrue: [ aBlock value ]
]
---
notNil: aBlock
[
  ^ (self != nil) ifTrue: [ aBlock value ]
]
---
throw
[
    <primitive:throw>
]
---
toString
[
  ^ <primitive:toString>
]
---
writeOn: w
[
    w out: 'Instance of ';
      out: self class
]
---
storeOn: w
[
    w out: self
]
---
-> other
[
  ^ Association key: self value: other
]
---
browse
[
    "TODO: Fire up Apps/InstanceBrowser"
  ^ nil
]
