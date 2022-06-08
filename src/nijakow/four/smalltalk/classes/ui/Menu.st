App subclass: #UI/Menu;
instanceVariableNames: 'entries'
<<<
init
[
    super init.
    entries := Vector new.
]
---
entry: text does: action
[
    entries add: text -> action.
  ^ self
]
---
runOnce | i selection
[
    i := 1.
    entries do: [ :each |
        Transcript out: i; out: ') '; out: each key; cr.
        i := i + 1.
    ].
    selection := (Transcript prompt: 'Choice > ') trim parseInt.
    ([ selection != nil ] and: [ selection between: 1 and: i - 1 ]) ifTrue: [
        ^ (entries at: selection) value value
    ].
  ^ nil
]
---
run
[
  ^ self runOnce
]
