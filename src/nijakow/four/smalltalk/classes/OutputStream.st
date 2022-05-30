OutputStream
---
show: obj
[
    self out: obj.
  ^ self
]
---
out: obj
[
    obj writeOn: self.
  ^ self
]
---
store: obj
[
    obj storeOn: self.
  ^ self
]
---
cr
[
    self out: $\n.
]
