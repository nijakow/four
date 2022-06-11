Class;
category: 'Kernel-Classes'
<<<
name | glv
[
  ^ <primitive:class/name>
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
instanceVariableNames | sos
[
    sos := StringOutputStream new.
    self instanceVariables do: [ :v | sos out: v asString; out: ' ' ].
  ^ sos close
]
---
category
[
  ^ <primitive:class/category>
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
methodsDo: aBlock
[
    self methods do: aBlock
]
---
methodsInCategory: category Do: aBlock
[
    self methodsDo: [ :method |
        (method category = category) ifTrue: [
            aBlock value: method
        ].
    ].
]
---
methodsInCategory: category | methods
[
    methods := Vector new.
    self methodsInCategory: category
         Do: [ :m | methods add: m ].
  ^ methods asArray
]
---
categories | cats
[
    cats := Set new.
    self methodsDo: [ :method | method category notNil: [ cats add: method category ] ].
  ^ cats asArray
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
    (name != nil) ifTrue: [ w out: name asString. ^ self ].
    w out: 'a class'.
]
---
saveHeaderTo: w
[
    w out: self superclass name asString; out: ' subclass: '; out: self name; out: ';'; cr;
      out: 'instanceVariableNames: '; store: self instanceVariableNames.
      self category notNil: [ w out: ';'; cr; out: 'category: '; store: self category asString ].
    w cr.
]
---
saveToWithoutStatics: w Name: n
[
    w out: n; cr;
      out: '<<<'; cr.
    self methods do: [ :m |
        (m isKindOf: CompiledMethod) ifTrue: [
            w out: m source;
              out: '---'; cr.
        ]
    ].
]
---
saveTo: w | n
[
    n := self name.
    n notNil: [
        self saveToWithoutStatics: w Name: n asString.
        (self != self class) ifTrue: [
            self class saveToWithoutStatics: w Name: n asString + ' class'
        ].
    ].
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
---
byCategory | cats
[
    cats := Dictionary new.
    self allNamedDo: [ :class |
        (cats at: class category) isNil: [ cats at: class category put: Set new ].
        (cats at: class category) add: class.
    ].
  ^ cats
]
---
inCategory: category | classes
[
    classes := Vector new.
    self allNamedDo: [ :class |
        (class category = category) ifTrue: [
            classes add: class
        ]
    ].
  ^ classes asArray
]
---
allCategories | categories
[
    categories := Set new.
    self allNamedDo: [ :class | class category notNil: [ categories add: class category ] ].
  ^ categories asArray
]
