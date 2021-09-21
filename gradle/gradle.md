# Gradle构建脚本基础

整个gradle可以理解为三部分

* groovy语法：继承自java语法，不需要专门学习。

* gradle api：类似jdk、sdk一样的存在，在开发gradle插件时提供底层支持。

* buildscript:声明gradle脚本构建项目时所需要的使用的依赖项，仓库地址、第三方插件等。构建项目时会优先执行buildscript代码块中的内容。

## 项目构建的生命周期

项目构建分为三个阶段

* __初始化阶段__：会执行项目根目录下的Setting.gradle文件，分析哪些project参与本次构建

* __配置阶段__：加载所有参加本次构建项目下的build.gradle文件，会将build.gradle文件解析并实例化为一个Gradle的Project对象，然后分析Project之间的依赖关系，分析Project下的Task之间的依赖关系，生成有向无环拓扑结构图TaskGraph；

* __执行阶段__：这是Task真正被执行的阶段，Gradle会根据依赖关系决定哪些Task需要被执行，以及执行的先后顺序。 

## Gradle的Task和Transform

### Task的定义与配置

Task是Gradle项目构建的最小执行单元，Gradle通过将一个个Task串联起来完成具体的构建任务，每个Task都属于一个Project。Gradle在构建的过程中，会构建一个TaskGraph任务依赖图，这个依赖图会保证各个Task的执行顺序关系。

|  配置项  |  描述  |  默认值  |
|  ----  | ----  | ----  |
| type | 基于一个存在的Task来构建，和我们的类继承差不多 | DefaultTask |
| action | 添加到任务的一个Action或者一个闭包 | |
| description | 任务描述 | |
| group | 任务分组 |
| enabled | 是否可用，如果为false，则任务会被跳过 | true |
| dependsOn | 配置任务的依赖关系，如果A依赖B，则必须执行B才能执行A | |
| mustRunAfter | 设置任务执行顺序。如果任务A和B都存在的场景下，任务A必须先于任务B执行| |
| finalizedBy | 为任务A添加了一个当前任务结束后立马执行任务B。和dependsOn相反|

```
配置方式1
task tinyPingTask(group:'imooc',description:'compress images') {
    println 'this is TinyPngTask'
}

配置方式2
project.tasks.create(name:'tinyPingTask') {
    setGroup('imooc')
    setDescription('compress images')
    println 'this is TinyPngTask2'
}
```

* __TaskActions执行动作__

doFirst ：给Task添加一个执行动作，在该Task的执行阶段，是最先执行的。

doLast ：给Task添加一个执行动作，在该Task的执行阶段，是最后执行。

### Transform 的定义和配置

如果我们想对编译时产生的Class文件，在转换成Dex之前做一些处理。我们可以通过Gradle插件来注册我们编写的Transform。注册后的Transform也会被Gradle包装成一个Task，这个Task会在java compile Task执行完毕后运行。一般使用Transform会有下面两种场景

* 我们需要对编译生成的class文件做自定义处理
* 我们需要读取编译产生的class文件，做一些其他事情，但是不需要修改它。

* 比如Hilt DI 框架中会修改superclass为特定的class
* Hugo耗时统计库会在每个方法中插入代码来做耗时统计
* InstantPatch热修复，在所有方法前插入一个预留函数，可以将有bug的方法替换成下发的方法。
* CodeCheck代码检查，都是用transform来做的。

自定义Transform

* 输入输出

    TransformInput是指输入文件的一个抽象，包括：

    DirectoryInput集合是指以源码的方式参与项目编译的所有目录结构以及其下的源码文件
    
    JarInput集合是指以jar包方式参与项目编译的所有本地jar包和远程jar包

    TransformOutputProvider通过它可以获取到输出路径信息

    