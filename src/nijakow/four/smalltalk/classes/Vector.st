--- needs: Collection
Collection subclass: #Vector;
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
    elements at: i put: v.
  ^ self
]
---
add: v
[
    (fill >= elements size) ifTrue: [
        self resizeTo: (elements size * 2).
    ].
    elements at: fill put: v.
    fill := fill + 1.
  ^ self
]
---
remove: index
[
    index to: (fill - 1) do: [ :v :i | self at: index put: (self at: index + 1) ].
    fill := fill - 1.
  ^ self
]
---
do: block
[
    0 to: (self size - 1) do: [ :i | block value: (self at: i) value: i ].
  ^ self
]
