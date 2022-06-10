Object subclass: #User;
instanceVariableNames: 'name'
<<<
name: aString
[
    name := aString
]
---
User class
<<<
findByName: name
[
  ^ self new;
        name: name
]
