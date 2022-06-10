OutputStream
<<<
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
---
normal
[
    self out: '\{RESET\}'
]
---
foreground: color
[
    self out: '\{FG_0x'; out: color toHex; out: $\}.
]
---
background: color
[
    self out: '\{BG_0x'; out: color toHex; out: $\}.
]
