Port
<<<
createIO
[
  ^ (IO new) initWithI: (PortInputStream new init: self)
                     O: (PortOutputStream new init: self)
                  Port: self
]
---
confirm: text | result
[
    [
        result := self prompt: text.
        result := result trim.
        result isEmpty ifFalse: [
                ((result at: 1) = $y)
            or: ((result at: 1) = $Y);
                ifTrue: [ ^ true ].
                ((result at: 1) = $n)
            or: ((result at: 1) = $N);
                ifTrue: [ ^ false ].
        ].
        Transcript out: 'Please answer with [Y]es or [N]o.'; cr.
    ] repeat.
]
