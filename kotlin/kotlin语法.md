# Kotlin

## 基础语法

### 函数定义

函数定义关键字 **fun** 参数格式 参数：类型

```
fun sum(a: Int, b: Int): Int {   // Int 参数，返回值 Int
    return a + b
}
```

表达式作为函数体，返回类型自动推断

```
fun sum(a: Int, b: Int) = a + b

public fun sum(a: Int, b: Int): Int = a + b   // public 方法则必须明确写出返回类型,返回为空可以省略

```

### 可变长参数函数

```
fun vars(vararg v:Int){
    for(vt in v){
        print(vt)
    }
}

// 测试
fun main(args: Array<String>) {
    vars(1,2,3,4,5)  // 输出12345
}
```

### lambda

```
// 测试
fun main(args: Array<String>) {
    val sumLambda: (Int, Int) -> Int = {x,y -> x+y}
    println(sumLambda(1,2))  // 输出 3
}
```

### 常量变量

var：可变变量
val：不可变变量（常量）

常量与变量都可以没有初始化值,但是在引用前必须初始化。  
编译器支持自动类型判断,即声明时可以不指定类型,由编译器判断。
```
val a: Int = 1
val b = 1       // 系统自动推断变量类型为Int
val c: Int      // 如果不在声明时初始化则必须提供变量类型
c = 1           // 明确赋值


var x = 5        // 系统自动推断变量类型为Int
x += 1           // 变量可修改
```
### 字符串模板

$ 表示一个变量名或者变量值

$varName 表示变量值

${varName.fun()} 表示变量的方法返回值:

### NULL检查机制

判空处理有两种  
字段后加!!像Java一样抛出空异常  
字段后加?可不做处理返回值为 null

### 类型检测和自动类型转换

我们可以使用 is 运算符检测一个表达式是否某类型的一个实例(类似于Java中的instanceof关键字)。

```
fun getStringLength(obj: Any): Int? {
  if (obj is String) {
    // 做过类型判断以后，obj会被系统自动转换为String类型
    return obj.length 
  }

  //在这里还有一种方法，与Java中instanceof不同，使用!is
  // if (obj !is String){
  //   // XXX
  // }

  // 这里的obj仍然是Any类型的引用
  return null
}
```

### 区间

区间表达式由操作符 .. 的 rangeTo 函数和 in 和 !in 形成。

区间是为任何可比较类型定义的，但对于整型原生类型，它有一个优化的实现。以下是使用区间的一些示例:

```
for (i in 1..4) print(i) // 输出“1234”

for (i in 4..1) print(i) // 什么都不输出

if (i in 1..10) { // 等同于 1 <= i && i <= 10
    println(i)
}

// 使用 step 指定步长
for (i in 1..4 step 2) print(i) // 输出“13”

for (i in 4 downTo 1 step 2) print(i) // 输出“42”


// 使用 until 函数排除结束元素
for (i in 1 until 10) {   // i in [1, 10) 排除了 10
     println(i)
}
```

## 基本数据类型

Kotlin 的基本数值类型包括 Byte、Short、Int、Long、Float、Double 等。不同于 Java 的是，字符不属于数值类型，是一个独立的数据类型。

| 类型  | 位宽度 |
|:------:|:--------:|
|Double	|64
|Float	|32
|Long	|64
|Int	|32
|Short	|16
|Byte	|8

### 数值比较

由于Kotlin 中没有基础数据类型，只有封装的数字类型。所以比较的时候都是比较的类的对象。  
在kotlin语言中 === 表示比较对象地址，两个 == 表示比较两个值大小。

### 类型转换

较小类型不能隐式转换成较大类型，但可以显示转换

```
val b: Byte = 1 // OK, 字面值是静态检测的
val i: Int = b // 错误

val b: Byte = 1 // OK, 字面值是静态检测的
val i: Int = b.toInt() // OK
```

### 字符

和 Java 不一样，Kotlin 中的 Char 不能直接和数字操作，Char 必需是单引号 ' 包含起来的。比如普通字符 '0'，'a'

```
fun check(c: Char) {
    if (c == 1) { // 错误：类型不兼容
        // ……
    }
}
```

### 数组

数组用类 Array 实现，并且还有一个 size 属性及 get 和 set 方法，由于使用 [] 重载了 get 和 set 方法，所以我们可以通过下标很方便的获取或者设置数组对应位置的值。

```
fun main(args: Array<String>) {
    //[1,2,3]
    val a = arrayOf(1, 2, 3)
    //[0,2,4]
    val b = Array(3, { i -> (i * 2) })

    //读取数组内容
    println(a[0])    // 输出结果：1
    println(b[1])    // 输出结果：2
}
```

### 字符串

和 Java 一样，String 是不可变的。方括号 [] 语法可以很方便的获取字符串中的某个字符，也可以通过 for 循环来遍历：
```
for (c in str) {
    println(c)
}
```

Kotlin 支持三个引号 """ 扩起来的字符串，支持多行字符串，比如：

```
fun main(args: Array<String>) {
    val text = """
    多行字符串
    多行字符串
    """
    println(text)   // 输出有一些前置空格
}
```

String 可以通过 trimMargin() 方法来删除多余的空白。

```
fun main(args: Array<String>) {
    val text = """
    |多行字符串
    |菜鸟教程
    |多行字符串
    |Runoob
    """.trimMargin()
    println(text)    // 前置空格删除了
}
```

## 条件控制

### if表达式

```
// 传统用法
var max = a 
if (a < b) max = b

// 使用 else 
var max: Int
if (a > b) {
    max = a
} else {
    max = b
}
 
// 表达式
val max = if (a > b) a else b
```

### when表达式

```
when (x) {
    1 -> print("x == 1")
    2 -> print("x == 2")
    else -> { // 注意这个块
        print("x 不是 1 ，也不是 2")
    }
}
```

## 循环控制

### For 循环

for 循环可以对任何提供迭代器的对象进行遍历，语法如下:

```
for (item in collection) print(item)
```
循环体可以是代码块形式:

```
for (item: Int in ints) {
    // ……
}
```

### while 与 do...while 循环

```
while( 布尔表达式 ) {
  //循环内容
}

do {
       //代码语句
}while(布尔表达式);
```

### 循环跳转与返回

return。默认从最直接包围它的函数或者匿名函数返回。  
break。终止最直接包围它的循环。   
continue。继续下一次最直接包围它的循环。

## 类和对象

### 类的修饰符

```
abstract    // 抽象类  
final       // 类不可继承，默认属性
enum        // 枚举类
open        // 类可继承，类默认是final的
annotation  // 注解类


abstract    // 抽象类  
final       // 类不可继承，默认属性
enum        // 枚举类
open        // 类可继承，类默认是final的
annotation  // 注解类

```

### 一般类

kotlin类示例
``` 
class Person constructor(firstName: String){  // 类名为 Person 构造器为类头的一部分
    // 大括号内是类体构成
    
    var age:Int
    var name:String = "set函数"
    get() = field //get函数
    set(value){//set函数
        field = value
    }
    //类属性
    
    
    fun foo() { print("我是 $name") }
    //成员函数
    
    init{
        println("主构造器FirstName is $firstName")
    }
    
    constructor(parent: Person) {//次级构造函数要加constructor关键字
        parent.children.add(this) 
    }
    
    constructor (name: String, age:Int) : this(name) {
        // 初始化...
    }
    //如果类有主构造函数，每个次构造函数都要，或直接或间接通过另一个次构造函数代理主构造函数
}

var site = Runoob()
site.name
//用.调用

```
kotlin的主构造函数只有一个，而可以有多个次级构造函数

### 抽象类
```
abstract class Derived : Base() {
    override abstract fun f()
}

open class Base {
    open fun f() {}
}
```

### 嵌套类

```
class Outer {                  // 外部类
    private val bar: Int = 1
    class Nested {             // 嵌套类
        fun foo() = 2
    }
}

fun main(args: Array<String>) {
    val demo = Outer.Nested().foo() // 调用格式：外部类.嵌套类.嵌套类方法/属性
    println(demo)    // == 2
}
```

### 内部类

```
class Outer {
    private val bar: Int = 1
    var v = "成员属性"
    /**嵌套内部类**/
    inner class Inner {
        fun foo() = bar  // 访问外部类成员
        fun innerTest() {
            var o = this@Outer //获取外部类的成员变量
            println("内部类可以引用外部类的成员，例如：" + o.v)
        }
    }
}

fun main(args: Array<String>) {
    val demo = Outer().Inner().foo()
    println(demo) //   1
    val demo2 = Outer().Inner().innerTest()   
    println(demo2)   // 内部类可以引用外部类的成员，例如：成员属性
}
```
内部类自带外部类的引用

### 匿名内部类

```
class Test {
    var v = "成员属性"

    fun setInterFace(test: TestInterFace) {
        test.test()
    }
}

/**
 * 定义接口
 */
interface TestInterFace {
    fun test()
}

fun main(args: Array<String>) {
    var test = Test()

    /**
     * 采用对象表达式来创建接口对象，即匿名内部类的实例。
     */
    test.setInterFace(object : TestInterFace {
        override fun test() {
            println("对象表达式创建匿名内部类的实例")
        }
    })
}
```

## 继承

被继承的类必须有open关键字

### Any类

Any类是所有类的超类。没有超类声明的默认超类为Any

Any类的三个默认函数
```
equals()

hashCode()

toString()
```

### 构造函数

#### 子类有主构造函数

如果子类有主构造函数则基类必须在主构造函数中初始化

```
open class Person(var name : String, var age : Int){// 基类

}

class Student(name : String, age : Int, var no : String, var score : Int) : Person(name, age) {

}

//初始化基类指  ：基类名（参数，参数）
```

#### 子类没有主构造函数

没有则必须在二级构造函数中调用super超类的构造函数
```
class Student : Person {

    constructor(ctx: Context) : super(ctx) {
    } 

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx,attrs) {
    }
}
```

### 重写

被final修饰的函数不能被重写。需要被重写的函数，手动添加open关键字，在子类中重写则需要override关键字。

```
/**用户基类**/
open class Person{
    open fun study(){       // 允许子类重写
        println("我毕业了")
    }
}

/**子类继承 Person 类**/
class Student : Person() {

    override fun study(){    // 重写方法
        println("我在读大学")
    }
}

fun main(args: Array<String>) {
    val s =  Student()
    s.study();

}
```

### 属性重写

属性重写使用override关键字

```
open class Foo {
    open val x: Int get { …… }
}

class Bar1 : Foo() {
    override val x: Int = ……
}
```

你可以用一个var属性重写一个val属性，但是反过来不行。因为val属性本身定义了getter方法，重写为var属性会在衍生类中额外声明一个setter方法

## 接口

与java相似，用interface关键字来标识接口。允许默认实现

```
interface MyInterface {
    fun bar()    // 未实现
    fun foo() {  //已实现
      // 可选的方法体
      println("foo")
    }
}
```

### 实现接口

```
class Child : MyInterface {
//类名后面直接加冒号
    override fun bar() {
        // 方法体加override关键字
    }
}
```

### 接口中的属性

```
interface MyInterface{
    var name:String //name 属性, 抽象的
}
 
class MyImpl:MyInterface{
    override var name: String = "runoob" //重写属性
}
```

### 函数重写

当一个类实现两个或以上接口时，这些接口中有命名相同的函数

```
interface A {
    fun foo() { print("A") }   // 已实现
    fun bar()                  // 未实现，没有方法体，是抽象的
}
 
interface B {
    fun foo() { print("B") }   // 已实现
    fun bar() { print("bar") } // 已实现
}
 
class C : A {
    override fun bar() { print("bar") }   // 重写
}
 
class D : A, B {
    override fun foo() {
        super<A>.foo()
        super<B>.foo()
    }
 
    override fun bar() {
        super<B>.bar()
    }
}
```

## kotlin扩展

kotlin可以对类的属性和函数扩展，而且不用继承或者使用Decorator。

```
class User(var name:String)

/**扩展函数**/
fun User.Print(){
    print("用户名 $name")
}

fun main(arg:Array<String>){
    var user = User("Runoob")
    user.Print()
}
```
### 静态解析

扩展函数为静态解析在调用扩展函数时，具体被调用的的是哪一个函数，由调用函数的的对象表达式来决定的，而不是动态的类型决定的

```
open class C

class D: C()

fun C.foo() = "c"   // 扩展函数 foo

fun D.foo() = "d"   // 扩展函数 foo

fun printFoo(c: C) {
    println(c.foo())  // 类型是 C 类
}

fun main(arg:Array<String>){
    printFoo(D())  //传入D
}
```

在上述例子中，传入的是D，但实际是调用C所以输出c。不能动态的做出解析。 

ps：扩展函数与成员函数一致时，会优先成员函数

### 扩展空对象


在扩展函数内， 可以通过 this 来判断接收者是否为 NULL,做一个空值保护,这样，即使接收者为 NULL,也可以调用扩展函数

```
fun Any?.toString(): String {
    if (this == null) return "null"
    // 空检测之后，“this”会自动转换为非空类型，所以下面的 toString()
    // 解析为 Any 类的成员函数
    return toString()
}
fun main(arg:Array<String>){
    var t = null
    println(t.toString())
    //输出为null
}
```

### 伴生对象的扩展

如果一个类定义有一个伴生对象 ，你也可以为伴生对象定义扩展函数和属性。伴生对象通过"类名."形式调用伴生对象，伴生对象声明的扩展函数，通过用类名限定符来调用：

```
class MyClass {
    companion object { }  // 将被称为 "Companion"
}

fun MyClass.Companion.foo() {
    println("伴随对象的扩展函数")
}

val MyClass.Companion.no: Int
    get() = 10

fun main(args: Array<String>) {
    println("no:${MyClass.no}")
    MyClass.foo()
}
```

### 扩展作用域
    
通常扩展函数或属性定义在顶级包下，而如果要使用所定义包之外的一个扩展, 通过import导入扩展的函数名进行使用:

```
package foo.bar

fun Baz.goo() { …… } 

package com.example.usage

import foo.bar.goo // 导入所有名为 goo 的扩展
                   // 或者
import foo.bar.*   // 从 foo.bar 导入一切

fun usage(baz: Baz) {
    baz.goo()
}
```

## 数据类

kotlin可以定义一个只有数据的类。数据类关键字为 data。数据类要求:
* 主构造函数至少包含一个参数。
* 构造函数必须为val和var
* 数据类不可以声明为 abstract, open, sealed 或者 inner;
* 数据类不能继承其他类 (但是可以实现接口)。

```
data class User(val name: String, val age: Int)
```

数据类会被编译器提取以下函数

* equals() / hashCode()
* toString() 格式如 "User(name=John, age=42)"
* componentN() functions 对应于属性，按声明顺序排列
* copy() 函数 

### 复制

复制使用 copy() 函数，我们可以使用该函数复制对象并修改部分属性

```
data class User(val name: String, val age: Int)

fun main(args: Array<String>) {
    val jack = User(name = "Jack", age = 1)
    val olderJack = jack.copy(age = 2)
    println(jack)
    println(olderJack)

}
```

## 密封类

密封类用来表示受限的类继承结构：当一个值为有限几种的类型, 而不能有任何其他类型时。在某种意义上，他们是枚举类的扩展：枚举类型的值集合 也是受限的，但每个枚举常量只存在一个实例，而密封类 的一个子类可以有可包含状态的多个实例。

声明一个密封类，使用 sealed 修饰类，密封类可以有子类，但是所有的子类都必须要内嵌在密封类中。

sealed 不能修饰 interface ,abstract class(会报 warning,但是不会出现编译错误)

```
sealed class Expr
data class Const(val number: Double) : Expr()
data class Sum(val e1: Expr, val e2: Expr) : Expr()
object NotANumber : Expr()

fun eval(expr: Expr): Double = when (expr) {
    is Const -> expr.number
    is Sum -> eval(expr.e1) + eval(expr.e2)
    NotANumber -> Double.NaN
}
```
使用密封类的关键好处在于使用 when 表达式 的时候，如果能够 验证语句覆盖了所有情况，就不需要为该语句再添加一个 else 子句了。

## 泛型

与 Java 一样，Kotlin 也提供泛型，为类型安全提供保证，消除类型强转的烦恼。

```
class Box<T>(t : T) {
    var value = t
}

fun main(args: Array<String>) {
    var boxInt = Box<Int>(10)
    var boxString = Box<String>("Runoob")

    println(boxInt.value)
    println(boxString.value)
}
```

### 泛型约束

可以对泛型的上限进行约束（必须是谁的子类）

```
fun <T : Comparable<T>> sort(list: List<T>) {
    //Comparable 的子类型可以替代 T。 例如:
}
```

默认的上界是 Any?。

对于多个上界约束条件，可以用 where 子句：

```
fun <T> copyWhenGreater(list: List<T>, threshold: T): List<String>
    where T : CharSequence,
          T : Comparable<T> {
    return list.filter { it > threshold }.map { it.toString() }
}
```

### 型变

Kotlin 中没有通配符类型，它有两个其他的东西：声明处型变（declaration-site variance）与类型投影（type projections）。

#### 声明处型变

如果泛型只需要出现在方法的返回值声明中（不出现在形参声明中），那么该方法就只是取出泛型对象，因此该方法就支持泛型协变（相当于通配符上限）；如果一个类的所有方法都支持泛型协变，那么该类的泛型参数可使用out修饰。 

```
package test0709

class User<out T> {
    //此处不能用var，否则就有setter方法
    //setter方法会导致T出现在方法形参中
    val info: T

    constructor(info: T) {
        this.info = info
    }

    fun test(): T {
        println("执行test方法")
        return info
    }
}

fun main(args: Array<String>) {
    //此时T的类型是String
    var user = User<String>("Kotlin")
    println(user.info)
    //对于u2而言，它的类型是User<Any>,此时T的类型是Any
    //由于程序声明了T支持协变，因此User<String>可当成User<Any>使用
    var u2: User<Any> = user
    println(u2.info)
}

//输出结果
Kotlin
Kotlin
```

如果泛型只需要出现在方法的形参声明中（不出现在返回值声明中），那么该方法就只是传入泛型对象，因此该方法就支持泛型逆变（相当于通配符下限）；如果一个类的所有方法都支持泛型逆变，那么该类的泛型参数可使用in修饰。

```
package test0709

class Item<in T> {
    fun foo(t: T) {
        println(t)
    }
}

fun main(args: Array<String>) {
    //此时T的类型是Any
    var item = Item<Any>()
    item.foo(20)
    var im2:Item<String> = item
    //im2的实际类型是Item<Any>,因此它的参数只要是Any即可
    //声明了im2的类型为Item<String>
    //因此传入的参数只可能是String
    im2.foo("Kotlin")
}
//输出结果
20
Kotlin
```

#### 类型投射

如果不能使用声明处型变，则可以使用Kotlin提供的“使用处型变”。所谓使用处型变，就是在使用泛型时对其使用out或in修饰。

#### 星号投影

星号投影是为了处理Java的原始类型。
```
fun main(args: Array<String>) {
    //<*>必不可少，相当于Java的原始类型
    var list: ArrayList<*> = arrayListOf(1, "Kotlin")
    println(list)
}

//输出
[1, Kotlin]
```

假如声明了支持两个泛型参数的Foo<in T,out U>类型，关于星号投影的解释如下：

对于Foo<*，String>，相当于Foo<in Nothing,String>  

对于Foo<Int,*>，相当于Foo<Int,out Any?>  

对于Foo<,>，相当于Foo<in Nothing,out Any?> 

## Kotlin 枚举类

枚举类最基本的用法是实现一个类型安全的枚举。 

枚举常量用逗号分隔,每个枚举常量都是一个对象。

```
enum class Color{
    RED,BLACK,BLUE,GREEN,WHITE
}
```

### 枚举类初始化

每一个枚举都是枚举类的实例，都可以被初始化。

```
enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF)
}
```

枚举还支持以声明自己的匿名类及相应的方法、以及覆盖基类的方法。如：

```
enum class ProtocolState {
    WAITING {
        override fun signal() = TALKING
    },

    TALKING {
        override fun signal() = WAITING
    };

    abstract fun signal(): ProtocolState
}
```

## Kotlin 对象表达式和对象声明

Kotlin 用对象表达式和对象声明来实现创建一个对某个类做了轻微改动的类的对象，且不需要去声明一个新的子类。

###  对象表达式

通过对象表达式实现一个匿名内部类的对象用于方法的参数中：

```
window.addMouseListener(object : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {
        // ...
    }
    override fun mouseEntered(e: MouseEvent) {
        // ...
    }
})
```

匿名对象可以用作只在本地和私有作用域中声明的类型。公有的函数返回类型就会时any导致无法解析

```
class C {
    // 私有函数，所以其返回类型是匿名对象类型
    private fun foo() = object {
        val x: String = "x"
    }

    // 公有函数，所以其返回类型是 Any
    fun publicFoo() = object {
        val x: String = "x"
    }

    fun bar() {
        val x1 = foo().x        // 没问题
        val x2 = publicFoo().x  // 错误：未能解析的引用“x”
    }
}
```

### 对象声明

Kotlin 使用 object 关键字来声明一个对象。

Kotlin 中我们可以方便的通过对象声明来获得一个单例。

```
object Site {
    var url:String = ""
    val name: String = "菜鸟教程"
}
fun main(args: Array<String>) {
    var s1 =  Site
    var s2 = Site
    s1.url = "www.runoob.com"
    println(s1.url)
    println(s2.url)
}
//输出结果
www.runoob.com
www.runoob.com
```

与对象表达式不同，当对象声明在另一个类的内部时，这个对象并不能通过外部类的实例访问到该对象，而只能通过类名来访问，同样该对象也不能直接访问到外部类的方法和变量。

```
object DefaultListener : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {
        // ……
    }

    override fun mouseEntered(e: MouseEvent) {
        // ……
    }
}
```

### 伴生对象

类内部的对象声明可以用 companion 关键字标记，这样它就与外部类关联在一起，我们就可以直接通过外部类访问到对象的内部元素

```
class MyClass {
    companion object Factory {
        fun create(): MyClass = MyClass()
    }
}

val instance = MyClass.create()   // 访问到对象的内部元素
```

注意：一个类里面只能声明一个内部关联对象，即关键字 companion 只能使用一次

## kotlin委托

委托模式是软件设计模式中的一项基本技巧。在委托模式中，有两个对象参与处理同一个请求，接受请求的对象将请求委托给另一个对象来处理。

Kotlin 直接支持委托模式，更加优雅，简洁。Kotlin 通过关键字 by 实现委托。

### 类委托

一个类中定义的方法实际由调用另一个类的对象实现

```
// 创建接口
interface Base {   
    fun print()
}

// 实现此接口的被委托的类
class BaseImpl(val x: Int) : Base {
    override fun print() { print(x) }
}

// 通过关键字 by 建立委托类
class Derived(b: Base) : Base by b

fun main(args: Array<String>) {
    val b = BaseImpl(10)
    Derived(b).print() // 输出 10
}
```

### 属性委托

一个类的属值不在类中定义，而是托付给一个代理类进行统一管理。val/var <属性名>: <类型> by <表达式>

```
import kotlin.reflect.KProperty
// 定义包含属性委托的类
class Example {
    var p: String by Delegate()
}

// 委托的类
class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, 这里委托了 ${property.name} 属性"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$thisRef 的 ${property.name} 属性赋值为 $value")
    }
}
fun main(args: Array<String>) {
    val e = Example()
    println(e.p)     // 访问该属性，调用 getValue() 函数

    e.p = "Runoob"   // 调用 setValue() 函数
    println(e.p)
}
```

### 标准委托

Kotlin 的标准库中已经内置了很多工厂方法来实现属性的委托。

#### 延迟属性 Lazy

lazy() 是一个函数, 接受一个 Lambda 表达式作为参数, 返回一个 Lazy <T> 实例的函数，返回的实例可以作为实现延迟属性的委托： 第一次调用 get() 会执行已传递给 lazy() 的 lamda 表达式并记录结果， 后续调用 get() 只是返回记录的结果。

```
val lazyValue: String by lazy {
    println("computed!")     // 第一次调用输出，第二次调用不执行
    "Hello"
}

fun main(args: Array<String>) {
    println(lazyValue)   // 第一次执行，执行两次输出表达式
    println(lazyValue)   // 第二次执行，只输出返回值
}
```

#### 可观察属性 Observable

利用该属性可以用于实现观察者模式。  
Delegates.observable() 函数接受两个参数:   
第一个是初始化值    
第二个是属性值变化事件的响应器(handler)。   
在属性赋值后会执行事件的响应器(handler)，它有三个参数：被赋值的属性、旧值和新值：
```
import kotlin.properties.Delegates

class User {
    var name: String by Delegates.observable("初始值") {
        prop, old, new ->
        println("旧值：$old -> 新值：$new")
    }
}

fun main(args: Array<String>) {
    val user = User()
    user.name = "第一次赋值"
    user.name = "第二次赋值"
}

//输出结果
//旧值：初始值 -> 新值：第一次赋值
//旧值：第一次赋值 -> 新值：第二次赋值
```

#### 映射属性map

常见操作：在映射map中存储属性的值

```
class Site(val map: Map<String, Any?>) {
    val name: String by map
    val url: String  by map
}

fun main(args: Array<String>) {
    // 构造函数接受一个映射参数
    val site = Site(mapOf(
        "name" to "菜鸟教程",
        "url"  to "www.runoob.com"
    ))
    
    // 读取映射值
    println(site.name)
    println(site.url)
}
//输出结果
//菜鸟教程
//www.runoob.com
```

#### Not Null

notNull 适用于那些无法在初始化阶段就确定属性值的场合。?
如果属性在赋值前就被访问的话则会抛出异常
```
class Foo {
    var notNullBar: String by Delegates.notNull<String>()
}

foo.notNullBar = "bar"
println(foo.notNullBar)
```

#### 局部委托属性

可以将局部变量申请为委托属性。

```
fun example(computeFoo: () -> Foo) {
    val memoizedFoo by lazy(computeFoo)

    if (someCondition && memoizedFoo.isValid()) {
        memoizedFoo.doSomething()
    }
}
//memoizedFoo 变量只会在第一次访问时计算。 如果 someCondition 失败，那么该变量根本不会计算。
```

#### 属性委托要求

对于只读属性(也就是说val属性), 它的委托必须提供一个名为getValue()的函数。该函数接受以下参数：

thisRef —— 必须与属性所有者类型（对于扩展属性——指被扩展的类型）相同或者是它的超类型    
property —— 必须是类型 KProperty<*> 或其超类型
这个函数必须返回与属性相同的类型（或其子类型）。

对于一个值可变(mutable)属性(也就是说,var 属性),除 getValue()函数之外,它的委托还必须 另外再提供一个名为setValue()的函数, 这个函数接受以下参数:

property —— 必须是类型 KProperty<*> 或其超类型  
new value —— 必须和属性同类型或者是它的超类型。