Port
---
createIO
[
  ^ (IO new) initWithI: (PortInputStream new init: self)
                     O: (PortOutputStream new init: self)
]
