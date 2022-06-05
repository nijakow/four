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
