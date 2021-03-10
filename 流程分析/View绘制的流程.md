# View 树的测绘流程分析

View的测绘流程一直是面试重点，首先我们提出问题。

1、触发View三大流程的入口在哪里

2、onCreate,onResume中为什么获取不到View的宽高

3、onCreate中使用View.post为什么可以获取到宽高

4、子线程更新UI真的不行吗