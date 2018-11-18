---
layout: docs
title: orderBy
permalink: /docs/aql/orderby/
---

{:.beginner}
beginner

## orderBy

`orderBy` and `orderMap` allows ordering selected data based on the actual data type `Order<A>` implementation. Data may be requested to be ordered ascending or descending. Ordering should be explicitly set as `Ord.Asc` for ascending order or `Ord.Desc` for descending order.

`orderBy` over `List`

{:data-executable='true'}
```kotlin
import arrow.aql.instances.list.select.*
import arrow.aql.instances.list.orderBy.*
import arrow.aql.instances.listk.select.select
import arrow.instances.order
import arrow.aql.Ord

fun main(args: Array<String>) {
//sampleStart
val result = 
  listOf(1, 2, 3).query {
    select { this * 10 } orderBy Ord.Asc(Int.order())
  }
//sampleEnd
println(result)
}
```

`orderMap` over `List`

{:data-executable='true'}
```kotlin
import arrow.aql.instances.list.select.*
import arrow.aql.instances.list.where.*
import arrow.aql.instances.list.groupBy.*
import arrow.aql.instances.list.orderBy.*
import arrow.aql.instances.listk.select.selectAll
import arrow.aql.instances.id.select.value
import arrow.instances.order
import arrow.aql.Ord

data class Student(val name: String, val age: Int)

val john = Student("John", 30)
val jane = Student("Jane", 32)
val jack = Student("Jack", 32)

fun main(args: Array<String>) {
//sampleStart
val result = 
  listOf(john, jane, jack).query {
     selectAll() groupBy { age } orderMap Ord.Desc(Int.order())
  }
//sampleEnd
println(result)
}
```

{:.intermediate}
intermediate

`orderBy` works with any data type that provides an instance of `Foldable<F>` where `F` is the higher kinded representation of the data type. For example `ForOption` when targeting the `Option<A>` data type or `ForListK` when targeting the `List<A>` data type. The contained data inside the data type should also provide an instance for `Order` as in the examples above `Int.order() : Order<Int>`

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
|__arrow.core__|[Either]({{ '/docs/arrow/core/either' | relative_url }}), [Option]({{ '/docs/arrow/core/option' | relative_url }}), [Try]({{ '/docs/arrow/core/try' | relative_url }})|
|__arrow.data__|[ListK]({{ '/docs/arrow/data/listk' | relative_url }}), [NonEmptyList]({{ '/docs/arrow/data/nonemptylist' | relative_url }}), [SequenceK]({{ '/docs/arrow/data/sequencek' | relative_url }})|

{:.advanced}
advanced

[Adapt AQL to your own _custom data types_](/docs/aql/custom/)
