IntegerLike;
category: 'Kernel-Numbers'
<<<
< other
[
    self subclassResponsibility
]
---
<= other
[
  ^ (self < other) or: (self = other)
]
---
> other
[
  ^ (self <= other) not
]
---
>= other
[
  ^ (self < other) not
]
---
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
---
between: lower and: upper
[
  ^ [ self >= lower ] and: [ self <= upper ]
]
