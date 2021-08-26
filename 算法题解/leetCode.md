# 题解

## 双指针的运用

快慢指针是指，一个指针先走或者快走，一个指针后走或者慢走

题型举例 

1.判断链表是否有环[leetCode](https://leetcode-cn.com/problems/linked-list-cycle/description/)

```
//一个指针每次移动一个节点，一个指针每次移动两个节点，如果存在环，那么这两个指针一定会相遇。
public boolean hasCycle(ListNode head) {
    if (head == null) {
        return false;
    }
    ListNode l1 = head, l2 = head.next;
    while (l1 != null && l2 != null && l2.next != null) {
        if (l1 == l2) {
            return true;
        }
        l1 = l1.next;
        l2 = l2.next.next;
    }
    return false;
}

```

2.删除倒数k节点[leetCode](https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list/description/)

```
//一个节点先走k步，另一个再后走
public ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode fast = head;
    while (n-- > 0) {
        fast = fast.next;
    }
    if (fast == null) return head.next;
    ListNode slow = head;
    while (fast.next != null) {
        fast = fast.next;
        slow = slow.next;
    }
    slow.next = slow.next.next;
    return head;
}

```

3.回文链表[leetCode](https://leetcode-cn.com/problems/palindrome-linked-list/description/)

```
//先用快慢双指针得到中间节点，然后拆开成两个链表，最后比对
 public boolean isPalindrome(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while(fast != null) {
            slow = slow.next;
            fast = fast.next!=null ? fast.next.next : fast.next; 
        }

        ListNode midHead = null;

        while(slow != null) {
            ListNode temp = slow.next;
            slow.next = midHead;
            midHead = slow;
            slow = temp;
        }

        while(midHead != null) {
            if(midHead.val != head.val){
                return false;
            }
            midHead = midHead.next;
            head = head.next;
        }

        return true;
    }
```

### 打乱数组 [leetCode](https://leetcode-cn.com/problems/shuffle-an-array/)

b站社招面试题

```
class Solution {

    private int[] array;//存储打乱后的数组
    private int[] origin;//存储原始数组

    Random rand = new Random();

    private int randRange(int min,int max) {
        return rand.nextInt(max -min)+min;
    }
    private void swapAt(int i,int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public Solution(int[] nums) {
        array = nums;
        origin = nums.clone();
    }
    
    /** Resets the array to its original configuration and return it. */
    public int[] reset() {
        array = origin;
        origin = origin.clone();
        return origin;
    }
    
    /** Returns a random shuffling of the array. */
    public int[] shuffle() {
        for(int i = 0; i < array.length; i++) {
            swapAt(i,randRange(i,array.length));//洗牌算法
        }
        return array;
    }
}
```

### 按序打印 [leetCode](https://leetcode-cn.com/problems/print-in-order/)

蓝厂面试题

```
class Foo {

    private AtomicInteger step = new AtomicInteger(0);//简简单单的原子类

    public Foo() {
        
    }

    public void first(Runnable printFirst) throws InterruptedException {
        
        
        printFirst.run();
        step.incrementAndGet();
    }

    public void second(Runnable printSecond) throws InterruptedException {
        
        while(step.get() != 1) {

        }
        printSecond.run();
        step.incrementAndGet();
    }

    public void third(Runnable printThird) throws InterruptedException {
        while(step.get() != 2) {

        }
       
        printThird.run();
    }
}

```

### 下一个更大  [leetCode](https://leetcode-cn.com/problems/print-in-order/)

简单算法题无它

```
class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {

        int[] result = new int[nums1.length];
        int j =0 , i =0;
        for( i = 0; i < nums1.length; i++) {

            boolean isfind = false;

            for(j= 0 ; j < nums2.length; j++) {
                if(nums1[i] == nums2[j]){
                    isfind = true;
                    continue;
                }
                if(nums1[i] != nums2[j] && isfind == false){
                    continue;
                }
                if(nums1[i] < nums2[j] ){
                    break;
                }
            }
            if(j == nums2.length) {
                result[i] = -1;
            } else {
                result[i] = nums2[j];
            }
        }
       return result;

    }
}
```


