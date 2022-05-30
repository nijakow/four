Object subclass: #Bootstrapper
---
load: name
[
    ('/nijakow/four/smalltalk/classes/' + name) openResource load.
  ^ self
]
---
setupColors
[
    Black := Color new; r: 0 g: 0 b: 0.
    White := Color new; r: 0xff g: 0xff b: 0xff.
    Red   := Color new; r: 0xc0 g: 0 b: 0.
    Green := Color new; r: 0 g: 0xc0 b: 0.
    Blue  := Color new; r: 0 g: 0 b: 0xf0.
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
         load: 'Color.st';
         load: 'OutputStream.st';
         load: 'Shell.st'.
    self setupColors.
]
