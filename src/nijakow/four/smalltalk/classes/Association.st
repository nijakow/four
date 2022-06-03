Object subclass: #Association;
instanceVariableNames: 'key value'
---
init
[
]
---
key
[
  ^ key
]
---
key: k
[
    key := k.
  ^ self
]
---
value
[
  ^ value
]
---
value: v
[
    value := v.
  ^ self
]
---
key: k value: v
[
    key := k.
    value := v.
  ^ self
]
---
writeOn: w
[
    w << key
      << ' -> '
      << value.
]
