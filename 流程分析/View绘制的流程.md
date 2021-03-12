# View 树的测绘流程分析

View的测绘流程一直是面试重点，首先我们提出问题。

1、触发View三大流程的入口在哪里

2、onCreate,onResume中为什么获取不到View的宽高

3、onCreate中使用View.post为什么可以获取到宽高

4、子线程更新UI真的不行吗

由源码得出下图

![alt 属性文本](../view/../images/view/view01.png)

通过阅读源码，我们得知整个View的绘制在handleResumeActivity里进行触发

```
    public void handleResumeActivity(IBinder token, boolean finalStateRequest, boolean isForward,
            String reason) {

        // ...
        if (r.window == null && !a.mFinished && willBeVisible) {
            r.window = r.activity.getWindow();
            View decor = r.window.getDecorView();
            decor.setVisibility(View.INVISIBLE);
            ViewManager wm = a.getWindowManager();
            WindowManager.LayoutParams l = r.window.getAttributes();
            a.mDecor = decor;
            // ...
            if (a.mVisibleFromClient) {
                if (!a.mWindowAdded) {
                    a.mWindowAdded = true;
                    //此处用于添加DecorView，wm 是 WindowManagerImpl
                    wm.addView(decor, l);
                } else {
                  // ...
                }
            }

            // If the window has already been added, but during resume
            // we started another activity, then don't yet make the
            // window visible.
        } else if (!willBeVisible) {
            if (localLOGV) Slog.v(TAG, "Launch " + r + " mStartedActivity set");
            r.hideForNow = true;
        }
        // ...

    }
```
注意，此处的decorview中所包含的View是在我们熟悉的onCreate生命周期中的setContentView()传入的。