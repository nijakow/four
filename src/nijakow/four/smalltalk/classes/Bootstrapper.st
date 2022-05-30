Object subclass: #Bootstrapper
---
load: name
[
    ('/nijakow/four/smalltalk/classes/' + name) openResource load.
  ^ self
]
---
run
[
    self load: 'Iterator.st';
         load: 'Collection.st';
         load: 'SequenceableCollection.st';
         load: 'ArrayedCollection.st';
         load: 'ArrayedCollectionIterator.st';
         load: 'Vector.st';
         load: 'Set.st';
         load: 'Association.st';
         load: 'Dictionary.st';
         load: 'Shell.st'.
]
