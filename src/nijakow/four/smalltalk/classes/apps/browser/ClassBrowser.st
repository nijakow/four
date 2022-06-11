Apps/Browser/AbstractObjectBrowser subclass: #Apps/Browser/ClassBrowser
<<<
showMethods: methods | menu
[
    Transcript cr;
        out: 'Methods of '; out: self target; cr.
    menu := UI/Menu new.
    methods := methods sortBy: [ :a :b | (a name asString) < (b name asString) ].
    methods do: [ :m | menu entry: m name does: [ :i | (methods at: i) browse ] ].
    menu entry: 'New...' does: [ :name |
        name := Transcript prompt: 'Method name? '.
        name notNil: [ self target edit: name asSymbol ]
    ].
    menu entry: 'Exit' does: [].
    menu run
]
---
showMethodsInCategory: category
[
    self showMethods: (self target methodsInCategory: category)
]
---
showMethods
[
    self showMethods: self target methods.
]
---
downloadSnapshot | sos
[
    sos := StringOutputStream new.
    self target saveTo: sos.
    Transcript uploadToUser: sos close.
]
---
showCategories | list menu
[
    list := self target categories.
    list := list sortBy: [ :a :b | a asString < b asString ].
    menu := UI/Menu new.
    list do: [ :m | menu entry: m asString does: [ :i | self showMethodsInCategory: (list at: i) ] ].
    menu entry: '<Uncategorized>' does: [ :i | self showMethodsInCategory: nil ].
    menu entry: 'Exit' does: [].
    menu run.
]
---
run | running
[
    running := true.
    [ running ] whileTrue: [
        Transcript cr;
            out: 'The class '; out: self target; cr.
        UI/Menu new;
            entry: 'Methods...' does: [ self showMethods ];
            entry: 'Methods by Category...' does: [ self showCategories ];
            entry: 'Edit Statics...' does: [ self target class browse ];
            entry: 'Edit Superclass...' does: [ self target superclass browse ];
            entry: 'Download a Snapshot' does: [ self downloadSnapshot ];
            entry: 'Exit' does: [ running := false ];
        run
    ].
]
