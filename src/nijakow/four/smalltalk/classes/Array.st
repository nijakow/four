Array
---
copy | a
[
    a := (self class) new: (self size).
    self do: [ :v :i | a at: i put: v ].
  ^ a
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
