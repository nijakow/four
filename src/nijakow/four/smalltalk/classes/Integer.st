Integer
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
