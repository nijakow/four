OutputStream
---
<< obj
[
    obj writeOn: self.
  ^ self
]
---
store<< obj
[
    obj storeOn: self.
  ^ self
]
---
cr
[
    self << $\n.
]
---
normal
[
    self << '\{RESET\}'
]
---
foreground: color
[
    self << '\{FG_0x'; out: color toHex; out: $\}.
]
---
background: color
[
    self << '\{BG_0x'; out: color toHex; out: $\}.
]
