OutputStream subclass: #StringOutputStream;
instanceVariableNames: 'chars';
category: 'Kernel-IO'
<<<
init
[
    chars := Vector new.
  ^ self
]
---
close
[
  ^ chars asString
]
---
charOut: c
[
    chars add: c.
  ^ self
]
---
clear
[
    chars clear.
]
---
isEmpty
[
  ^ chars isEmpty
]
