---
layout: docs
title: Bracket
permalink: /docs/typeclasses/bracket/
video: EUqg3fSahhk
---

## Bracket

{:.intermediate}
intermediate

The `Bracket` Type class abstracts the ability to safely **acquire**, **use**, and then **release** a resource. 

Essentially, it could be considered the functional programming equivalent to the well known imperative 
`try/catch/finally` structure.

`Bracket` extends [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}).

It ensures that the acquired resource is released at the end after using it, **even if the using action throws an error**. 
That ensures that no errors are swallowed.

Another key point of `Bracket` would be the ability to abstract over whether the resource is going to be used 
synchronously or asynchronously. 

### Example

Let's say we want to work with a file. Let's use a mock `File` API to avoid messing with the real one here.

These methods would allow to open a File, close it and also read it's content as a string.

```kotlin
import arrow.effects.IO

/**
 * Mock File API.
 */
class File(url: String) {
    fun open(): File = this
    fun close(): Unit {}
    override fun toString(): String = "This file contains some interesting content!"
}

fun openFile(uri: String): IO<File> = IO { File(uri).open() }

fun closeFile(file: File): IO<Unit> = IO { file.close() }

fun fileToString(file: File): IO<String> = IO { file.toString() }
```

Note that we wrapped them into [`IO`]({{ '/docs/effects/io' | relative_url }}). [`IO`]({{ '/docs/effects/io' | relative_url }}) 
is able to wrap any side effecting computation to make it pure. In a real world system, these operations would contain 
side effects since they'd end up accessing the file system.
 
Read the [`IO`]({{ '/docs/effects/io' | relative_url }}) docs for more context on this.
 
Now, let's say we want to open a file, do some work with it, and then close it. With `Bracket`, we could make that 
process look like this:

{: data-executable='true'}

```kotlin
import arrow.effects.IO

class File(url: String) {
    fun open(): File = this
    fun close(): Unit {}
    override fun toString(): String = "This file contains some interesting content!"
}

fun openFile(uri: String): IO<File> = IO { File(uri).open() }

fun closeFile(file: File): IO<Unit> = IO { file.close() }

fun fileToString(file: File): IO<String> = IO { file.toString() }

fun main(args: Array<String>) {
//sampleStart
val safeComputation = openFile("data.json").bracket(
    release = { file -> closeFile(file) },
    use = { file -> fileToString(file) })
//sampleEnd
println(safeComputation)
}
```

This would ensure the file gets closed right after completing the `use` operation, which would be `fileToString(file)` 
here. If that operation throws an error, the file would also be closed.

Note that the result is still an `IO.Async` operation, which means it's still deferred (not executed yet).

### Polymorphic example

We've mentioned that `Bracket` is agnostic of whether the `use` lambda is computed synchronously or asynchronously. 
That's because it's able to run over any data type `F` that can support synchronous and asynchronous 
computations, like [`IO`]({{ '/docs/effects/io' | relative_url }}), [`Observable`]({{ '/docs/integrations/rx2' | relative_url }}) 
or [`Deferred`]({{ '/docs/integrations/kotlinxcoroutines' | relative_url }}). 

It basically targets what in Functional Programming is known as a "Higher Kind".

There's a complete [section about this pattern]({{ '/docs/patterns/polymorphic_programs' | relative_url }}) in the 
docs.

Let's learn more about how `Bracket` can support this pattern as a Type class with a basic example showcasing this high level of abstraction technique.

```kotlin
import arrow.Kind
import arrow.effects.typeclasses.Bracket

class File(url: String) {
  fun open(): File = this
  fun close(): Unit {}
  override fun toString(): String = "This file contains some interesting content!"
}

class Program<F>(BF: Bracket<F, Throwable>) : Bracket<F, Throwable> by BF {

  fun openFile(uri: String): Kind<F, File> = just(File(uri).open())

  fun closeFile(file: File): Kind<F, Unit> = just(file.close())

  fun fileToString(file: File): Kind<F, String> = just(file.toString())
}
```

This is basically the same program from previous examples, but defined over any `F` data type that there is an instance 
of `Bracket` for. In other words, this program is constrained by the capabilities that `Bracket` can provide. 

We are also fixing the error type from `Bracket<F, E>` to be `Throwable`.

Let's run the program for the three mentioned data types as an example of polymorphism now.

We can run the program for `IO`:

{: data-executable='true'}

```kotlin
import arrow.effects.IO
import arrow.effects.instances.io.bracket.bracket
import arrow.Kind
import arrow.effects.typeclasses.Bracket

class File(url: String) {
  fun open(): File = this
  fun close(): Unit {}
  override fun toString(): String = "This file contains some interesting content!"
}

class Program<F>(BF: Bracket<F, Throwable>) : Bracket<F, Throwable> by BF {

  fun openFile(uri: String): Kind<F, File> = just(File(uri).open())

  fun closeFile(file: File): Kind<F, Unit> = just(file.close())

  fun fileToString(file: File): Kind<F, String> = just(file.toString())
}

fun main(args: Array<String>) {
//sampleStart
val ioProgram = Program(IO.bracket())

val safeComputation = with (ioProgram) {
  openFile("data.json").bracket(
    release = { file -> closeFile(file) },
    use = { file -> fileToString(file) })
}
//sampleEnd
println(safeComputation)
}
```

Let's now run the same exact program also for `ObservableK`:

{: data-executable='true'}

```kotlin
import arrow.effects.ObservableK
import arrow.effects.observablek.bracket.bracket
import arrow.Kind
import arrow.effects.typeclasses.Bracket

class File(url: String) {
  fun open(): File = this
  fun close(): Unit {}
  override fun toString(): String = "This file contains some interesting content!"
}

class Program<F>(BF: Bracket<F, Throwable>) : Bracket<F, Throwable> by BF {

  fun openFile(uri: String): Kind<F, File> = just(File(uri).open())

  fun closeFile(file: File): Kind<F, Unit> = just(file.close())

  fun fileToString(file: File): Kind<F, String> = just(file.toString())
}

fun main(args: Array<String>) {
//sampleStart
val observableProgram = Program(ObservableK.bracket())

val safeComputation = with (observableProgram) {
  openFile("data.json").bracket(
    release = { file -> closeFile(file) },
    use = { file -> fileToString(file) })
}
//sampleEnd
println(safeComputation)
}
```

And finally over `DeferredK`:

{: data-executable='true'}

```kotlin
import arrow.effects.DeferredK
import arrow.effects.deferredk.bracket.bracket
import arrow.Kind
import arrow.effects.typeclasses.Bracket

class File(url: String) {
  fun open(): File = this
  fun close(): Unit {}
  override fun toString(): String = "This file contains some interesting content!"
}

class Program<F>(BF: Bracket<F, Throwable>) : Bracket<F, Throwable> by BF {

  fun openFile(uri: String): Kind<F, File> = just(File(uri).open())

  fun closeFile(file: File): Kind<F, Unit> = just(file.close())

  fun fileToString(file: File): Kind<F, String> = just(file.toString())
}

fun main(args: Array<String>) {
//sampleStart
val deferredProgram = Program(DeferredK.bracket())

val safeComputation= with (deferredProgram) {
  openFile("data.json").bracket(
    release = { file -> closeFile(file) },
    use = { file -> fileToString(file) })
}
//sampleEnd
println(safeComputation)
}
```

Note that we're running the exact same program passing in three different data types. All of them can provide an 
instance of `Bracket`, which means that they can support asynchronous and synchronous computations.

This is the style you'd usually use in a Functional Program.

### Combinators

#### Kind<F, A>#bracket

Requires passing `release` and `use` lambdas. It ensures acquiring, using and releasing the resource at the end.

`fun <A, B> Kind<F, A>.bracket(release: (A) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B>`

{: data-executable='true'}

```kotlin
import arrow.effects.IO

class File(url: String) {
    fun open(): File = this
    fun close(): Unit {}
    override fun toString(): String = "This file contains some interesting content!"
}

fun openFile(uri: String): IO<File> = IO { File(uri).open() }

fun closeFile(file: File): IO<Unit> = IO { file.close() }

fun fileToString(file: File): IO<String> = IO { file.toString() }

fun main(args: Array<String>) {
//sampleStart
val safeComputation = openFile("data.json").bracket(
  release = { file -> closeFile(file) },
  use = { file -> fileToString(file) })
//sampleEnd
println(safeComputation)
}
```

#### Kind<F, A>#bracketCase

It's a generalized version of `bracket()` which uses `ExitCase` to distinguish between different exit cases when 
releasing the acquired resource. `ExitCase` can take the values `Completed`, `Canceled`, or `Error(e)`.  So depending 
how the `use` execution finalizes, the corresponding `ExitCase` value will be passed to the `release` lambda.

Requires passing `release` and `use` lambdas. It ensures acquiring, using and releasing the resource at the end.

`fun <A, B> Kind<F, A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B>`

{: data-executable='true'}

```kotlin
import arrow.effects.IO
import arrow.effects.typeclasses.ExitCase

class File(url: String) {
    fun open(): File = this
    fun close(): Unit {}
    override fun toString(): String = "This file contains some interesting content!"
}

fun openFile(uri: String): IO<File> = IO { File(uri).open() }

fun closeFile(file: File): IO<Unit> = IO { file.close() }

fun fileToString(file: File): IO<String> = IO { file.toString() }

fun main(args: Array<String>) {
//sampleStart
val safeComputation = openFile("data.json").bracketCase(
    release = { file, exitCase ->
      when (exitCase) {
        is ExitCase.Completed -> { /* do something */ }
        is ExitCase.Cancelled -> { /* do something */ }
        is ExitCase.Error -> { /* do something */ }
      }
      closeFile(file)
    },
    use = { file ->
      fileToString(file)
    })
//sampleEnd
println(safeComputation)
}
```

#### Other combinators

For a full list of other useful combinators available in `Bracket` see the [Source][bracket_source]{:target="_blank"}

### Laws

Arrow provides [`BracketLaws`][bracket_laws_source]{:target="_blank"} in the form of test cases for internal 
verification of lawful instances and third party apps creating their own Bracket instances.

#### Creating your own `Bracket` instances

Arrow already provides Bracket instances for most common datatypes both in Arrow and the Kotlin stdlib.
Oftentimes you may find the need to provide your own for unsupported datatypes.

You may create or automatically derive instances of `Bracket` for your own datatypes which you will be able to use in 
the context of abstract polymorphic code.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

### Data types

| Module | Data types |
|__arrow.effects__|[DeferredK]({{ '/docs/arrow/effects/deferredk' | relative_url }}), [FlowableK]({{ '/docs/arrow/effects/flowablek' | relative_url }}), [FluxK]({{ '/docs/arrow/effects/fluxk' | relative_url }}), [MaybeK]({{ '/docs/arrow/effects/maybek' | relative_url }}), [MonoK]({{ '/docs/arrow/effects/monok' | relative_url }}), [ObservableK]({{ '/docs/arrow/effects/observablek' | relative_url }}), [SingleK]({{ '/docs/arrow/effects/singlek' | relative_url }}), [IO]({{ '/docs/arrow/effects/io' | relative_url }})|

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>



[bracket_source]: https://github.com/arrow-kt/arrow/blob/master/modules/effects/arrow-effects/src/main/kotlin/arrow/effects/typeclasses/Bracket.kt
[bracket_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/BracketLaws.kt
