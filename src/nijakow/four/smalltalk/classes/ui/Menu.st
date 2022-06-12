App subclass: #UI/Menu;
instanceVariableNames: 'entries default'
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
default: text does: action
[
    default := text -> action.
  ^ self
]
---
runOnce | i selection
[
    i := 1.
    entries do: [ :each |
        Transcript out: '  '; out: i; out: ') '; out: each key; cr.
        i := i + 1.
    ].
    default notNil: [ Transcript out: '  0) '; out: default key; cr ].
    selection := (Transcript prompt: 'Choice > ') trim parseInt.
    ([ selection != nil ] and: [ selection between: 1 and: i - 1 ]) ifTrue: [
        ^ (entries at: selection) value value: selection
    ].
    ((selection = 0) and: (default != nil)) ifTrue: [
        ^ default value value: selection
    ].
  ^ nil
]
---
run
[
  ^ self runOnce
]
