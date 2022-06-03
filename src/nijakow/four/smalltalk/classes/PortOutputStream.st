OutputStream subclass: #PortOutputStream;
instanceVariableNames: 'port'
---
init: _port
[
    port := _port.
  ^ self
]
---
charOut: c
[
    port charOut: c
]
