Object subclass: #Bootstrapper
<<<
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
    self load: 'Object.st';
         load: 'Class.st';
         load: 'Nil.st';
         load: 'Boolean.st';
         load: 'Number.st';
         load: 'IntegerLike.st';
         load: 'Integer.st';
         load: 'Character.st';
         load: 'String.st';
         load: 'Symbol.st';
         load: 'collections/Array.st';
         load: 'Method.st';
         load: 'BlockClosure.st';
         load: 'CompiledMethod.st';
         load: 'Port.st';
         load: 'collections/Iterator.st';
         load: 'collections/Collection.st';
         load: 'collections/SequenceableCollection.st';
         load: 'collections/ArrayedCollection.st';
         load: 'collections/ArrayedCollectionIterator.st';
         load: 'collections/Vector.st';
         load: 'collections/Set.st';
         load: 'collections/Association.st';
         load: 'collections/Dictionary.st';
         load: 'collections/Interval.st';
         load: 'Exception.st';
         load: 'Color.st';
         load: 'InputStream.st';
         load: 'OutputStream.st';
         load: 'StringOutputStream.st';
         load: 'IO.st';
         load: 'Image.st';
         load: 'TestCase.st';
         load: 'TestResult.st';
         load: 'TestSuite.st';
         load: 'testsuites/BasicTests.st';
         load: 'testsuites/BasicTestSuite.st';
         load: 'User.st';
         load: 'App.st';
         load: 'ui/Menu.st';
         load: 'apps/Shell.st';
         load: 'apps/browser/Browser.st';
         load: 'apps/browser/AbstractObjectBrowser.st';
         load: 'apps/browser/ClassBrowser.st';
         load: 'apps/browser/MethodBrowser.st'.
    self setupColors.
]
