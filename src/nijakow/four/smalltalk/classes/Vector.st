ArrayedCollection subclass: #Vector;
instanceVariableNames: 'elements fill'
---
init
[
    elements := Array new: 8.
    fill := 0.
]
---
resizeTo: newsize | newelements
[
    newelements := Array new: newsize.
    elements do: [ :v :i | newelements at: i put: v ].
    elements := newelements.
  ^ self
]
---
size
[
  ^ fill
]
---
at: index
[
  ^ elements at: index
]
---
at: i put: v
[
    (i > elements size) ifTrue: [ self resizeTo: i ].
    (i > fill) ifTrue: [ fill := i ].
    elements at: i put: v.
  ^ self
]
---
add: v
[
    (fill >= elements size) ifTrue: [
        self resizeTo: (elements size * 2).
    ].
    elements at: (fill + 1) put: v.
    fill := fill + 1.
  ^ self
]
---
erase: index
[
    index to: fill do: [ :v :i | self at: index put: (self at: index + 1) ].
    fill := fill - 1.
  ^ self
]
---
remove: element
[
    1 to: fill do: [ :i |
        ((elements at: i) = element) ifTrue: [
            self erase: i.
          ^ self
        ]
    ].
  ^ self
]
