App subclass: #Apps/Browser/ClassBrowser;
instanceVariableNames: 'target'
<<<
target: t
[
    target := t
]
---
showMethods | menu methods
[
    Transcript cr;
        out: 'Methods of '; store: target name; cr.
    menu := UI/Menu new.
    methods := target methods.
    methods := methods sortBy: [ :a :b | (a name asString) < (b name asString) ].
    methods do: [ :m | menu entry: m name does: [ :i | (methods at: i) browse ] ].
    menu entry: 'New...' does: [ :name |
        name := Transcript prompt: 'Method name? '.
        name ifNotNil: [ target edit: name asSymbol ]
    ].
    menu entry: 'Exit' does: [].
    menu run
]
---
run | running
[
    running := true.
    [ running ] whileTrue: [
        Transcript cr;
            out: 'The class '; store: target name; cr.
        UI/Menu new;
            entry: 'Methods...' does: [ self showMethods ];
            entry: 'Exit' does: [ running := false ];
        run
    ].
]
---
Apps/Browser/ClassBrowser class
<<<
browse: class
[
  ^ (self new); target: class; run
]
