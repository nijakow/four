Class
<<<
name | glv
[
    Symbol instances do: [ :sym |
        glv := sym globalValue.
        (self = glv) ifTrue: [ ^ sym asString ].
    ].
    Symbol instances do: [ :sym |
        glv := sym globalValue.
        ([ glv isKindOf: Class ] and: [ self = glv class ]) ifTrue: [ ^ sym asString + ' class' ].
    ].
  ^ nil
]
---
methods | md
[
    md := Dictionary new.
    self selectors do: [ :sel | md at: sel put: (self method: sel) ].
  ^ md
]
---
edit: name | text
[
    ((self method: name) = nil) ifTrue: [
        text := (name name) + ' | "Local variables"\n[\n  ^ self\n]\n'.
    ] ifFalse: [
        text := (self method: name) source.
    ].
    text := (Transcript smalltalk: ('Method ' + (name name) + ' > ') text: text).
    (text = nil) ifTrue: [ ^ self ].
    (text isWhitespace) ifTrue: [ self removeMethod: name ]
                       ifFalse: [ self addMethod: text    ].
  ^ self
]
---
writeOn: w | name
[
    name := self name.
    (name != nil) ifTrue: [ w out: name. ^ self ].
    w out: 'a class'.
]
---
saveTo: w | n
[
    n := self name.
    (n != nil) ifTrue: [
        w out: n; cr;
          out: '<<<'; cr.
        self methods do: [ :m |
            (m isKindOf: CompiledMethod) ifTrue: [
                w out: m source;
                  out: '---'; cr.
            ]
        ].
    ]
]
---
Class class
<<<
allInstancesDo: aBlock
[
    self instances do: aBlock
]
---
allNamedDo: aBlock
[
    self instances do: [ :i |
        (i name != nil) ifTrue: [ aBlock value: i ]
    ]
]
