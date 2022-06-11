Object subclass: #Exception;
instanceVariableNames: 'message';
category: 'Kernel-Exceptions-Kernel'
<<<
message
[
  ^ message
]
---
message: _message
[
    message := _message
]
---
signal: message
[
    self message: message.
  ^ self throw
]
---
writeOn: w
[
    w out: (self class);
      out: ': ';
      out: self message.
]
---
Exception class
<<<
signal: message
[
  ^ self new signal: message
]
