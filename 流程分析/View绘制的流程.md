# View 树的测绘流程分析

View的测绘流程一直是面试重点，首先我们提出问题。

1、触发View三大流程的入口在哪里

2、onCreate,onResume中为什么获取不到View的宽高

3、onCreate中使用View.post为什么可以获取到宽高

4、子线程更新UI真的不行吗

由源码得出下图

![alt 属性文本](../view/../images/view/view01.png)

## 第一阶段 装弹 准备好VIew并送到指定地方

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

继续进入addView()。这个方法的实现在WindowmanagerImpl

```
    public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
        applyDefaultToken(params);
        mGlobal.addView(view, params, mContext.getDisplay(), mParentWindow);
    }

```
此处继续调用addView()。此处是WindowManagerGlobal的addView()

```
 public void addView(View view, ViewGroup.LayoutParams params,
            Display display, Window parentWindow) {
        ViewRootImpl root;
        View panelParentView = null;

        synchronized (mLock) {
            // 十分重要的ViewRootImpl类在此处创建
            root = new ViewRootImpl(view.getContext(), display);

            view.setLayoutParams(wparams);

            mViews.add(view);
            mRoots.add(root);
            mParams.add(wparams);

            // do this last because it fires off messages to start doing things
            try {
                //将View set到WindowManagerImpl中
                root.setView(view, wparams, panelParentView);
            } catch (RuntimeException e) {
            }
        }
    }

```
最后一步调用ViewRootImpl中的setView

```
 public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
     ...
      requestLayout();
      //此方法的调用标志着View的绘制的开始，VIew到这里也就到了需要的地方
       // 这里的 view 是 DecorView
      view.assignParent(this);
      
    }
```

分析到这里我们就得出了我们第一个问题的答案View三大流程的入口

## ViewRootImpl 浅析

ViewRootImpl 是一个很重要的类，具备以下重要职能。而它的创建时机我们可以从之前的流程中看到是在ActivityThread.handleResumeActivity里

1、WindowSession将Window添加到WindowManagerService

2、页面View树的顶层节点，关联Window和View。但本身不是View

3、Choreographer 接收Vsync同步信号触发View的三大流程

4、WindowInputEventReceiver 接收屏幕输入事件，分发手势。

5、接收Vsync同步信号，触发View的动画重绘


## 第二阶段 上膛发射 开始View的绘制

进入requestLayout方法。这里的逻辑很简单

```
    public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            //进行线程检查
            checkThread();
            mLayoutRequested = true;
            scheduleTraversals();
        }
    }

    void checkThread() {
        if (mThread != Thread.currentThread()) {
            throw new CalledFromWrongThreadException(
                    "Only the original thread that created a view hierarchy can touch its views.");
        }
    }

    void scheduleTraversals() {
        if (!mTraversalScheduled) {
            mTraversalScheduled = true;
            mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();
            //发送屏障消息
            mChoreographer.postCallback(
                    Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
            //发送异步消息
            if (!mUnbufferedInputDispatch) {
                scheduleConsumeBatchedInput();
            }
            notifyRendererOfFramePending();
            pokeDrawLockIfNeeded();
        }
    }
```

可以看到在进行简单的线程检查后就开始调用scheduleTraversals();在scheduleTraversals()方法中我们发送了一个 __屏障消息__。（此处屏障消息的作用在于让异步消息优先执行。从而使得ViewRootImpl中的UI测量，布局，绘制尽早执行）

我们继续看mTraversalRunnable，在它的run方法中调用了doTraversal()

```
void doTraversal() {
        if (mTraversalScheduled) {
            mTraversalScheduled = false;
            //移除消息屏障
            mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);

            if (mProfile) {
                Debug.startMethodTracing("ViewAncestor");
            }
            //关键方法
            performTraversals();

            if (mProfile) {
                Debug.stopMethodTracing();
                mProfile = false;
            }
        }
    }
```
这个doTraversal()的核心就是我们的performTraversals()。这个方法的代码巨多。简单的介绍作用就是，根据当前界面的View树，判断有没有View发生变化，需不需要重新布局测绘。而我们在分析View初次绘制流程时需要关注的三个核心方法如下

```
performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);

performLayout(lp, mWidth, mHeight);

performDraw();
```

到了这里就开始逐步递归调用我们View的Measure，Layout，Draw方法完成View的绘制