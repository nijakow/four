CompiledMethod;
category: 'Kernel-Methods'
<<<
holdingClass
[
  ^ <primitive:method/holdingClass>
]
---
name
[
  ^ <primitive:method/name>
]
---
category
[
  ^ <primitive:method/category>
]
---
doc
[
  ^ <primitive:method/doc>
]
---
source
[
  ^ <primitive:method/source>
]
---
disassemble
[
  ^ <primitive:method/disassemble>
]
---
edit
[
    self holdingClass edit: self name
]
---
browse
[
    Apps/Browser/MethodBrowser browse: self
]
