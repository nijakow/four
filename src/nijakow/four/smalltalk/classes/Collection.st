Collection
---
init
[
]
---
isEmpty
[
  ^ self size = 0
]
---
storeOn: w
[
    w out: '#('.
    self do: [ :v | w out: v; out: ' ' ].
    w out: ')'.
]
