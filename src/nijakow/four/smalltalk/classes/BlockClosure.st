BlockClosure
---
withBreak
[
  ^ [ self value: [ ^ self ] ] repeat
]
---
withContinue
[
     [
       ^ self withBreak
     ] repeat
]
