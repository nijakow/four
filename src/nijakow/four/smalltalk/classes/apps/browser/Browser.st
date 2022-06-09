App subclass: #Apps/Browser
<<<
createClass | name parent class
[
    name := Transcript prompt: 'Class name? '.
    name ifNil: [ ^ self ].
    parent := Transcript prompt: 'Parent? '.
    parent ifNil: [ ^ self ].
    "TODO: Category and instance variable names"
    class := (parent asSymbol globalValue) subclass: name asSymbol.
    Transcript out: 'Successfully created class '; out: name; out: '!'; cr.
    self editClass: class.
]
---
editClass: class
[
    Apps/Browser/ClassBrowser new;
      target: class;
      run
]
---
run | running
[
    running := true.
    [ running ] whileTrue: [
        Transcript cr;
            out: 'The Browser'; cr.
        UI/Menu new;
            entry: 'Create a new class' does: [ self createClass ];
            entry: 'Exit' does: [ running := false ];
        run
    ]
]
