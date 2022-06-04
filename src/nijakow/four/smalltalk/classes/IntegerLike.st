IntegerLike
<<<
to: upper
[
  ^ Interval from: self to: upper
]
---
to: upper do: block | v
[
    v := self.
    [ v <= upper ] whileTrue: [
        block value: v.
        v := v + 1.
    ].
  ^ self
]
---
min: other
[
    (self < other)  ifTrue: [ ^ self  ]
                   ifFalse: [ ^ other ]
]
---
max: other
[
    (self > other)  ifTrue: [ ^ self  ]
                   ifFalse: [ ^ other ]
]
