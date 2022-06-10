Class
<<<
name | glv
[
    Symbol boundInstancesDo: [ :sym :glv |
        (self = glv) ifTrue: [ ^ sym asString ].
    ].
    Symbol boundInstancesDo: [ :sym :glv |
        (self = glv class) ifTrue: [ ^ sym asString + ' class' ].
    ].
  ^ nil
]
---
instances
[
  ^ <primitive:class/instances>
]
---
superclass
[
  ^ <primitive:class/superclass>
]
---
instanceVariables
[
  ^ <primitive:class/instanceVariables>
]
---
category
[
  ^ <primitive:class/category>
]
---
category: newCategory
[
  ^ <primitive:class/category:>
]
---
method: name
[
  ^ <primitive:class/method:>
]
---
>> name
[
  ^ self method: name
]
---
methods | md
[
    md := Vector new.
    self selectors do: [ :sel | md add: (self method: sel) ].
  ^ md asArray
]
---
addMethod: source
[
  ^ <primitive:class/addMethod:>
]
---
removeMethod: name
[
  ^ <primitive:class/removeMethod:>
]
---
selectors
[
  ^ <primitive:class/selectors>
]
---
edit: name | text
[
    ((self method: name) = nil) ifTrue: [
        (name = #init)  ifTrue: [
            text := (name name) + ' | "Local variables"\n[\n    super init.\n  ^ self\n]\n'.
        ] ifFalse: [
            text := (name name) + ' | "Local variables"\n[\n  ^ self\n]\n'.
        ].
    ] ifFalse: [
        text := (self method: name) source.
    ].
    text := (Transcript edit: text title: ('Method ' + (name name))).
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
handle: block do: handler
[
  ^ <primitive:class/handle:do:>
]
---
browse
[
  ^ Apps/Browser/ClassBrowser browse: self
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
---
allNamed | v
[
    v := Vector new.
    self allNamedDo: [ :c | v add: c ].
  ^ v asArray
]
