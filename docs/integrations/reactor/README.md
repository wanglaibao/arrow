---
layout: docs
title: Reactor
permalink: /docs/integrations/reactor/
---

## Project Reactor

{:.intermediate}
intermediate

Arrow aims to enhance the user experience when using Project Reactor. While providing other datatypes that are capable of handling effects, like IO, the style of programming encouraged by the library allows users to generify behavior for any existing abstractions.

One of such abstractions is Project Reactor, a library that, like RxJava, offers reactive streams.

```kotlin
val flux = Flux.just(7, 4, 11 ,3)
    .map { it + 1 }
    .filter { it % 2 == 0 }
    .scan { acc, value -> acc + value }
    .collectList()
    .subscribeOn(Schedulers.parallel())
    .block()
//[8, 20, 24]
```

### Integration with your existing Flux chains

The largest quality of life improvement when using Flux streams in Arrow is the introduction of the [Monad Comprehension]({{ '/docs/patterns/monad_comprehensions' | relative_url }}). This library construct allows expressing asynchronous Flux sequences as synchronous code using binding/bind.

#### Arrow Wrapper

To wrap any existing Flux in its Arrow Wrapper counterpart you can use the extension function `k()`.

```kotlin
import arrow.effects.*
import reactor.core.publisher.*

val flux = Flux.just(1, 2, 3, 4, 5).k()
flux
// FluxK(flux=FluxArray)
```

```kotlin
val mono = Mono.just(1).k()
mono
// MonoK(mono=MonoJust)
```

You can return to their regular forms using the function `value()`.

```kotlin
flux.value()
// FluxArray
```

```kotlin
mono.value()
// MonoJust
```

### Observable comprehensions

The library provides instances of [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) and [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}).

[`MonadDefer`]({{ '/docs/effects/async' | relative_url }}) allows you to generify over datatypes that can run asynchronous code. You can use it with `FluxK` or `MonoK`.

```kotlin
fun <F> getSongUrlAsync(MS: MonadDefer<F>) =
  MS { getSongUrl() }
  
val songFlux: FluxKOf<Url> = getSongUrlAsync(FluxK.monadDefer())
val songMono: MonoKOf<Url> = getSongUrlAsync(MonoK.monadDefer())
```

[`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) can be used to start a [Monad Comprehension]({{ '/docs/patterns/monad_comprehensions' | relative_url }}) using the method `bindingCatch`, with all its benefits.

Let's take an example and convert it to a comprehension. We'll create an observable that loads a song from a remote location, and then reports the current play % every 100 milliseconds until the percentage reaches 100%:

```kotlin
getSongUrlAsync()
  .map { songUrl -> MediaPlayer.load(songUrl) }
  .flatMap {
    val totalTime = musicPlayer.getTotaltime()
    Flux.interval(Duration.ofMillis(100))
      .flatMap {
        Flux.create { musicPlayer.getCurrentTime() }
          .map { tick -> (tick / totalTime * 100).toInt() }
      }
      .takeUntil { percent -> percent >= 100 }
  }
```

When rewritten using `bindingCatch` it becomes:

```kotlin
import arrow.effects.*
import arrow.typeclasses.*

ForFluxK extensions {
  bindingCatch {
    val songUrl = getSongUrlAsync().bind()
    val musicPlayer = MediaPlayer.load(songUrl)
    val totalTime = musicPlayer.getTotaltime()
    
    val end = DirectProcessor.create<Unit>()
    Flux.interval(Duration.ofMillis(100)).takeUntilOther(end).bind()
    
    val tick = musicPlayer.getCurrentTime().bind()
    val percent = (tick / totalTime * 100).toInt()
    if (percent >= 100) {
      end.onNext(Unit)
    }
    
    percent
  }.fix()
}
```

Note that any unexpected exception, like `AritmeticException` when `totalTime` is 0, is automatically caught and wrapped inside the flux.

### Subscription and cancellation

Flux streams created with comprehensions like `bindingCatch` behave the same way regular flux streams do, including cancellation by disposing the subscription.

```kotlin
val disposable =
  songFlux.value()
    .subscribe({ println("Song $it") }, { System.err.println("Error $it") })
    
disposable.dispose()
```
Note that [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}) provides an alternative to `bindingCatch` called `bindingCancellable` returning a `arrow.Disposable`.
Invoking this `Disposable` causes an `BindingCancellationException` in the chain which needs to be handled by the subscriber, similarly to what `Deferred` does.

```kotlin
import arrow.effects.instances.defferred.monad.*

val (flux, disposable) =
  bindingCancellable {
    val userProfile = Flux.create { getUserProfile("123") }
    val friendProfiles = userProfile.friends().map { friend ->
        bindDefer { getProfile(friend.id) }
    }
    listOf(userProfile) + friendProfiles
  }

flux.value()
    .subscribe({ println("User $it") }, { System.err.println("Boom! caused by $it") })
    
disposable()
// Boom! caused by BindingCancellationException
```

### Supported Type Classes

| Module | Type classes |
|__arrow.effects.typeclasses__|[Async]({{ '/docs/arrow/effects/typeclasses/async' | relative_url }}), [Bracket]({{ '/docs/arrow/effects/typeclasses/bracket' | relative_url }}), [ConcurrentEffect]({{ '/docs/arrow/effects/typeclasses/concurrenteffect' | relative_url }}), [Effect]({{ '/docs/arrow/effects/typeclasses/effect' | relative_url }}), [MonadDefer]({{ '/docs/arrow/effects/typeclasses/monaddefer' | relative_url }})|
|__arrow.typeclasses__|[ApplicativeError]({{ '/docs/arrow/typeclasses/applicativeerror' | relative_url }}), [Applicative]({{ '/docs/arrow/typeclasses/applicative' | relative_url }}), [Foldable]({{ '/docs/arrow/typeclasses/foldable' | relative_url }}), [Functor]({{ '/docs/arrow/typeclasses/functor' | relative_url }}), [MonadError]({{ '/docs/arrow/typeclasses/monaderror' | relative_url }}), [Monad]({{ '/docs/arrow/typeclasses/monad' | relative_url }}), [MonadThrow]({{ '/docs/arrow/typeclasses/monadthrow' | relative_url }}), [Traverse]({{ '/docs/arrow/typeclasses/traverse' | relative_url }})|
