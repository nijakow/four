App subclass: #Apps/Browser
<<<
createClass | name parent
[
    name := Transcript prompt: 'Class name? '.
    name ifNil: [ ^ self ].
    parent := Transcript prompt: 'Parent? '.
    parent ifNil: [ ^ self ].
    "TODO: Category and instance variable names"
    (parent asSymbol globalValue) subclass: name asSymbol.
    Transcript out: 'Successfully created class '; out: name; out: '!'; cr.
    "TODO: Start an editor for this class?"
]
---
run | running
[
    running := true.
    [ running ] whileTrue: [
        UI/Menu new;
            entry: 'Create a new class' does: [ self createClass ];
            entry: 'Exit' does: [ running := false ];
        run value
    ]
]
