---
layout: docs
title: where
permalink: /docs/aql/where/
---

{:.beginner}
beginner

## where

`where` allows filtering data from any given data source providing a predicate function of the shape `(A) -> Boolean`.
Whenever `true` is returned from an expression affecting one of the items in the data source the item is collected and included in the selection.

`where` over `List`

{: data-executable='true'}
```kotlin
import arrow.aql.instances.list.select.*
import arrow.aql.instances.list.where.*
import arrow.aql.instances.listk.select.select

data class Student(val name: String, val age: Int)

val john = Student("John", 30)
val jane = Student("Jane", 32)
val jack = Student("Jack", 32)

fun main(args: Array<String>) {
//sampleStart
val result: List<String> =
  listOf(john, jane, jack).query {
    select { name } where { age > 20 }
  }.value()
//sampleEnd
println(result)
}
```

{:.intermediate}
intermediate

`where` works with any data type that provides an instance of `FunctorFilter<F>` where `F` is the higher kinded representation of the data type. For example `ForOption` when targeting the `Option<A>` data type or `ForListK` when targeting the `List<A>` data type

Learn more about the `AQL` combinators

- [_select_](/docs/aql/select/)
- [_from_](/docs/aql/from/)
- [_where_](/docs/aql/where/)
- [_groupBy_](/docs/aql/groupby/)
- [_orderBy_](/docs/aql/orderby/)
- [_sum_](/docs/aql/sum/)
- [_union_](/docs/aql/union/)

### Supported Data types

| Module | Data types |
|__arrow.core__|[Option]({{ '/docs/arrow/core/option' | relative_url }}), [Try]({{ '/docs/arrow/core/try' | relative_url }})|
|__arrow.data__|[ListK]({{ '/docs/arrow/data/listk' | relative_url }}), [NonEmptyList]({{ '/docs/arrow/data/nonemptylist' | relative_url }}), [SequenceK]({{ '/docs/arrow/data/sequencek' | relative_url }})|

{:.advanced}
advanced

[Adapt AQL to your own _custom data types_](/docs/aql/custom/)