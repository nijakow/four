Object subclass: #IO;
instanceVariableNames: 'input output port';
category: 'IO'
<<<
initWithI: _input O: _output Port: _port
[
    input := _input.
    output := _output.
    port := _port.
  ^ self
]
---
initWithI: _input O: _output
[
  ^ self initWithI: _input O: _output Port: nil
]
---
in
[
  ^ input
]
---
out
[
  ^ output
]
---
port
[
  ^ port
]
