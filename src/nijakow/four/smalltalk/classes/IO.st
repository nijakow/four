Object subclass: #IO;
instanceVariableNames: 'input output'
---
initWithI: _input O: _output
[
    input := _input.
    output := _output.
  ^ self
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
