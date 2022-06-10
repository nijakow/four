Apps/Browser/AbstractObjectBrowser subclass: #Apps/Browser/ClassBrowser
<<<
showMethods | menu methods
[
    Transcript cr;
        out: 'Methods of '; store: self target name; cr.
    menu := UI/Menu new.
    methods := self target methods.
    methods := methods sortBy: [ :a :b | (a name asString) < (b name asString) ].
    methods do: [ :m | menu entry: m name does: [ :i | (methods at: i) browse ] ].
    menu entry: 'New...' does: [ :name |
        name := Transcript prompt: 'Method name? '.
        name ifNotNil: [ self target edit: name asSymbol ]
    ].
    menu entry: 'Exit' does: [].
    menu run
]
---
downloadSnapshot | sos
[
    sos := StringOutputStream new.
    self target saveTo: sos.
    Transcript uploadToUser: sos close.
]
---
run | running
[
    running := true.
    [ running ] whileTrue: [
        Transcript cr;
            out: 'The class '; store: self target name; cr.
        UI/Menu new;
            entry: 'Methods...' does: [ self showMethods ];
            entry: 'Edit Statics...' does: [ self target class browse ];
            entry: 'Edit Superclass...' does: [ self target superclass browse ];
            entry: 'Download a Snapshot' does: [ self downloadSnapshot ];
            entry: 'Exit' does: [ running := false ];
        run
    ].
]
