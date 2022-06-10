App subclass: #Apps/Browser
<<<
createClass | text class
[
    text := Transcript edit: 'Object subclass: #NewClassName;\ninstanceVariableNames: \'\'\n' title: 'New Method'.
    text ifNotNil: [
        class := text compile value.
        (class isKindOf: Class)  ifTrue: [ self editClass: class                             ]
                                ifFalse: [ Transcript out: 'Class could not be created!'; cr ]
    ].
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
            entry: 'Launch a Shell' does: [ Apps/Shell launch ];
            entry: 'Exit' does: [ running := false ];
        run
    ]
]
