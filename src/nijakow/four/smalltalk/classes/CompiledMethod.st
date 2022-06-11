CompiledMethod
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
edit
[
    self holdingClass edit: self name
]
---
browse
[
    Apps/Browser/MethodBrowser browse: self
]
