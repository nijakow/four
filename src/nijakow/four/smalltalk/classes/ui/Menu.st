App subclass: #UI/Menu;
instanceVariableNames: 'entries'
<<<
entries: _e
[
    entries := e
]
---
run | i selection
[
    i := 1.
    entries do: [ :each |
        Transcript out: i; out: ') '; out: each key; cr.
        i := i + 1.
    ].
    selection := (Transcript prompt: 'Choice > ') parseInt.
    ([ selection != nil ] and: [ selection between: 1 and: i - 1 ]) ifTrue: [
        ^ (entries at: i) value
    ]
  ^ nil
]
