App subclass: #Apps/Browser
<<<
createClass | text class
[
    text := Transcript edit: 'Object subclass: #NewClassName;\ninstanceVariableNames: \'\'\n' title: 'New Method'.
    text notNil: [
        class := text compile value.
        (class isKindOf: Class)  ifTrue: [ self editClass: class                             ]
                                ifFalse: [ Transcript out: 'Class could not be created!'; cr ]
    ].
]
---
searchClass | name class
[
    name := Transcript prompt: 'Class name? '.
    class := name trim asSymbol globalValue.
    (class isKindOf: Class)  ifTrue: [ self editClass: class              ]
                            ifFalse: [ Transcript out: 'Not a class!'; cr ].
]
---
editClass: class
[
    Apps/Browser/ClassBrowser new;
      target: class;
      run
]
---
uploadClass | text
[
    text := Transcript downloadFromUser.
    text isNil: [ ^ nil ].
  ^ text load
]
---
classes: list title: title | menu
[
    Transcript cr;
        out: title; cr.
    list := list sortBy: [ :a :b | a name asString < b name asString ].
    menu := UI/Menu new.
    list do: [ :c | menu entry: c name asString does: [ :i | (list at: i) browse ] ].
    menu entry: 'Exit' does: [].
    menu run.
]
---
classesInCategory: category | name
[
    (category = nil)  ifTrue: [ name := 'Uncategorized'                 ]
                     ifFalse: [ name := 'Category ' + category asString ].
    self classes: (Class inCategory: category) title: name
]
---
categories | list menu
[
    Transcript cr;
        out: 'Categories'; cr.
    list := Class allCategories.
    list := list sortBy: [ :a :b | a asString < b asString ].
    menu := UI/Menu new.
    list do: [ :c | menu entry: c name asString does: [ :i | self classesInCategory: (list at: i) ] ].
    menu entry: '<Uncategorized>' does: [ self classesInCategory: nil ].
    menu entry: 'Exit' does: [].
    menu run.
]
---
classes | running menu list
[
    [ running ] whileTrue: [
        Transcript cr;
            out: 'All Classes'; cr.
        list := Class allNamed.
        list := list sortBy: [ :a :b | a name asString < b name asString ].
        menu := UI/Menu new.
        list do: [ :c | menu entry: c name asString does: [ :i | (list at: i) browse ] ].
        menu entry: 'Search...' does: [ self searchClass ].
        menu entry: 'New...' does: [ self createClass ].
        menu entry: 'Upload...' does: [ :c | (c := self uploadClass). c notNil: [ c browse ] ].
        menu entry: 'Exit' does: [ running := false ].
        menu run.
    ]
]
---
run | running
[
    running := true.
    [ running ] whileTrue: [
        Transcript cr;
            out: 'The Browser'; cr.
        UI/Menu new;
            entry: 'All Classes...' does: [ self classes ];
            entry: 'Categories...' does: [ self categories ];
            entry: 'Launch a Shell' does: [ Apps/Shell launch ];
            entry: 'Exit' does: [ running := false ];
        run
    ]
]
