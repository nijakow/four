Array;
category: 'Collections-Arrayed'
<<<
size
[
  ^ <primitive:array/size>
]
---
at: index
[
  ^ <primitive:array/at:>
]
---
at: index put: value
[
  ^ <primitive:array/at:put:>
]
---
constructString
[
  ^ <primitive:array/constructString>
]
---
asString
[
  ^ self constructString
]
---
copy | a
[
  ^ self asArray
]
---
destructivelySortBy: comparator | tmp runAgain
[
    runAgain := true.
    [ runAgain ] whileTrue: [
        runAgain := false.
        1 to: (self size - 1) do: [ :i |
            (comparator value: (self at: i)
                        value: (self at: (i + 1)))
                ifFalse: [
                    runAgain := true.
                    tmp := self at: i.
                    self at: i put: (self at: (i + 1)).
                    self at: (i + 1) put: tmp.
                ]
        ].
    ].
  ^ self
]
---
sortBy: comparator
[
  ^ self copy destructivelySortBy: comparator
]
---
Array class
<<<
new: size
[
  ^ <primitive:array/new>
]
