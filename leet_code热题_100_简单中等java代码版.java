import java.util.*;

/**
 * LeetCode 热题 100：简单 + 中等 Java 代码合集
 *
 * 说明：
 * 1. 这是一个复习合集，不是所有方法都能直接同时提交到 LeetCode。
 * 2. 真正提交时，把对应方法复制到 LeetCode 的 class Solution 中即可。
 * 3. LRUCache、Trie、MinStack 是独立类题，已写成内部类。
 */
public class Hot100EasyMediumJava {

    // =========================
    // 公共结构定义
    // =========================

    public static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public static class Node {
        int val;
        Node next;
        Node random;
        Node(int val) { this.val = val; }
    }

    // =========================
    // 一、哈希表
    // =========================

    /**
     * 1. 两数之和
     * 思路：一次遍历 + 哈希表。遍历时先查哈希表中是否存在 target - nums[i]，
     * 存在则直接返回两个下标；不存在则把当前元素和下标存入表中，供后续元素查找。
     * 时间复杂度：O(n)；空间复杂度：O(n)。
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            if (map.containsKey(need)) {
                return new int[]{map.get(need), i};
            }
            map.put(nums[i], i);
        }
        return new int[0];
    }

    /**
     * 49. 字母异位词分组
     * 思路：互为异位词的字符串排序后完全相同。把每个字符串排序后的结果作为哈希表的 key，
     * 原字符串加入对应 key 的分组列表，最后返回所有分组。
     * 时间复杂度：O(n * k log k)，n 为字符串个数，k 为最长字符串长度；空间复杂度：O(n * k)。
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }

    /**
     * 128. 最长连续序列
     * 思路：全部元素放入 HashSet 去重。只从"连续段的起点"（即 num-1 不在集合中的数）开始向后
     * 逐个 +1 扩展并计数，这样每个数最多被访问两次，避免重复扫描。
     * 时间复杂度：O(n)；空间复杂度：O(n)。
     */
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) set.add(num);

        int ans = 0;
        for (int num : set) {
            if (!set.contains(num - 1)) {
                int cur = num;
                int len = 1;
                while (set.contains(cur + 1)) {
                    cur++;
                    len++;
                }
                ans = Math.max(ans, len);
            }
        }
        return ans;
    }

    // =========================
    // 二、双指针
    // =========================

    /**
     * 283. 移动零
     * 思路：快慢指针。快指针遍历数组，遇到非零元素就依次填到慢指针位置；
     * 遍历结束后，慢指针之后的位置全部补 0，即可保持非零元素相对顺序不变。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public void moveZeroes(int[] nums) {
        int slow = 0;
        for (int num : nums) {
            if (num != 0) nums[slow++] = num;
        }
        while (slow < nums.length) nums[slow++] = 0;
    }

    /**
     * 11. 盛最多水的容器
     * 思路：首尾双指针。面积 = 两指针间距 × 较矮的高度。每次移动较矮的一侧指针，
     * 因为移动较高一侧不可能得到更大面积（宽变小、高不会超过矮的一侧）。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int ans = 0;
        while (left < right) {
            int h = Math.min(height[left], height[right]);
            ans = Math.max(ans, h * (right - left));
            if (height[left] < height[right]) left++;
            else right--;
        }
        return ans;
    }

    /**
     * 15. 三数之和
     * 思路：先排序。固定第一个数 nums[i]（跳过重复值，且 nums[i] > 0 时可提前结束），
     * 剩下两数用首尾双指针在有序区间内找和为 -nums[i] 的组合，找到后左右指针同时收缩并跳过重复值去重。
     * 时间复杂度：O(n^2)；空间复杂度：O(log n)（排序栈空间，不含结果集）。
     */
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        int n = nums.length;

        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            if (nums[i] > 0) break;

            int left = i + 1, right = n - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    ans.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) left++;
                    while (left < right && nums[right] == nums[right + 1]) right--;
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return ans;
    }

    // =========================
    // 三、滑动窗口
    // =========================

    /**
     * 3. 无重复字符的最长子串
     * 思路：滑动窗口 + 记录每个字符最后出现的位置。右指针遍历字符串，
     * 若当前字符上次出现的位置在窗口内，则左边界直接跳到"上次出现位置 + 1"，
     * 每一步用窗口长度更新答案。
     * 时间复杂度：O(n)；空间复杂度：O(1)（字符集大小固定 128）。
     */
    public int lengthOfLongestSubstring(String s) {
        int[] last = new int[128];
        Arrays.fill(last, -1);
        int left = 0, ans = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            left = Math.max(left, last[c] + 1);
            ans = Math.max(ans, right - left + 1);
            last[c] = right;
        }
        return ans;
    }

    /**
     * 438. 找到字符串中所有字母异位词
     * 思路：固定长度为 m（p 的长度）的滑动窗口。用两个 26 长度的计数数组分别记录
     * p 的字符频次和当前窗口的字符频次，窗口每右移一格：加入新字符、移除最左字符，
     * 两数组相等时窗口起点即为一个答案。
     * 时间复杂度：O(26n) ≈ O(n)；空间复杂度：O(26) = O(1)。
     */
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> ans = new ArrayList<>();
        int n = s.length(), m = p.length();
        if (m > n) return ans;

        int[] need = new int[26];
        int[] window = new int[26];
        for (int i = 0; i < m; i++) {
            need[p.charAt(i) - 'a']++;
            window[s.charAt(i) - 'a']++;
        }
        if (Arrays.equals(need, window)) ans.add(0);

        for (int i = m; i < n; i++) {
            window[s.charAt(i) - 'a']++;
            window[s.charAt(i - m) - 'a']--;
            if (Arrays.equals(need, window)) ans.add(i - m + 1);
        }
        return ans;
    }

    // =========================
    // 四、前缀和 / 子数组
    // =========================

    /**
     * 560. 和为 K 的子数组
     * 思路：前缀和 + 哈希表。子数组 (j, i] 的和为 k 等价于 prefix[i] - prefix[j] = k。
     * 遍历时累加前缀和 sum，查表中 sum - k 出现的次数累加到答案，再把当前 sum 计入表中。
     * 初始放入 (0, 1) 处理从下标 0 开始的子数组。
     * 时间复杂度：O(n)；空间复杂度：O(n)。
     */
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> count = new HashMap<>();
        count.put(0, 1);
        int sum = 0, ans = 0;

        for (int num : nums) {
            sum += num;
            ans += count.getOrDefault(sum - k, 0);
            count.put(sum, count.getOrDefault(sum, 0) + 1);
        }
        return ans;
    }

    // =========================
    // 五、普通数组
    // =========================

    /**
     * 53. 最大子数组和
     * 思路：Kadane 动态规划。cur 表示以当前元素结尾的最大子数组和：
     * 若前面的累加和为负则丢弃重新开始（取 nums[i] 本身），否则继续累加；每步更新全局最大值。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int maxSubArray(int[] nums) {
        int cur = nums[0], ans = nums[0];
        for (int i = 1; i < nums.length; i++) {
            cur = Math.max(nums[i], cur + nums[i]);
            ans = Math.max(ans, cur);
        }
        return ans;
    }

    /**
     * 56. 合并区间
     * 思路：按区间左端点升序排序，然后顺序扫描：若当前区间左端点大于结果集中最后一个区间
     * 的右端点，则无重叠直接加入；否则有重叠，用当前区间右端点更新（取较大值）最后一个区间的右端点。
     * 时间复杂度：O(n log n)（排序为主）；空间复杂度：O(log n)（排序栈空间，不含结果集）。
     */
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> ans = new ArrayList<>();

        for (int[] cur : intervals) {
            if (ans.isEmpty() || cur[0] > ans.get(ans.size() - 1)[1]) {
                ans.add(cur);
            } else {
                ans.get(ans.size() - 1)[1] = Math.max(ans.get(ans.size() - 1)[1], cur[1]);
            }
        }
        return ans.toArray(new int[ans.size()][]);
    }

    /**
     * 189. 轮转数组
     * 思路：三次翻转法。先整体翻转数组，再分别翻转前 k 个元素和后 n-k 个元素，
     * 即可实现向右轮转 k 位。注意先对 k 取模防止 k >= n。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        k %= n;
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }

    private void reverse(int[] nums, int left, int right) {
        while (left < right) {
            int tmp = nums[left];
            nums[left++] = nums[right];
            nums[right--] = tmp;
        }
    }

    /**
     * 238. 除自身以外数组的乘积
     * 思路：前缀积 × 后缀积。第一次正向遍历，ans[i] 先存 i 左侧所有元素的乘积；
     * 第二次反向遍历，用变量 suffix 累乘 i 右侧所有元素的乘积并乘到 ans[i] 上，全程不使用除法。
     * 时间复杂度：O(n)；空间复杂度：O(1)（输出数组不计入）。
     */
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] ans = new int[n];

        int prefix = 1;
        for (int i = 0; i < n; i++) {
            ans[i] = prefix;
            prefix *= nums[i];
        }

        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            ans[i] *= suffix;
            suffix *= nums[i];
        }
        return ans;
    }

    // =========================
    // 六、矩阵
    // =========================

    /**
     * 73. 矩阵置零
     * 思路：用矩阵的第一行和第一列作为标记位，记录该行/该列是否需要置零；
     * 先用两个布尔变量单独记录第一行、第一列自身是否含 0，
     * 再根据标记把内部区域置零，最后处理第一行和第一列。
     * 时间复杂度：O(m*n)；空间复杂度：O(1)。
     */
    public void setZeroes(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean firstRowZero = false, firstColZero = false;

        for (int j = 0; j < n; j++) {
            if (matrix[0][j] == 0) firstRowZero = true;
        }
        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) firstColZero = true;
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
            }
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }

        if (firstRowZero) {
            for (int j = 0; j < n; j++) matrix[0][j] = 0;
        }
        if (firstColZero) {
            for (int i = 0; i < m; i++) matrix[i][0] = 0;
        }
    }

    /**
     * 54. 螺旋矩阵
     * 思路：维护上下左右四个边界，按"上边从左到右 → 右边从上到下 → 下边从右到左 → 左边从下到上"
     * 的顺序遍历一圈后收缩对应边界；遍历下边和左边前需检查边界是否仍然有效，防止单行/单列重复输出。
     * 时间复杂度：O(m*n)；空间复杂度：O(1)（不含结果集）。
     */
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> ans = new ArrayList<>();
        int top = 0, bottom = matrix.length - 1;
        int left = 0, right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            for (int j = left; j <= right; j++) ans.add(matrix[top][j]);
            top++;

            for (int i = top; i <= bottom; i++) ans.add(matrix[i][right]);
            right--;

            if (top <= bottom) {
                for (int j = right; j >= left; j--) ans.add(matrix[bottom][j]);
                bottom--;
            }

            if (left <= right) {
                for (int i = bottom; i >= top; i--) ans.add(matrix[i][left]);
                left++;
            }
        }
        return ans;
    }

    /**
     * 48. 旋转图像
     * 思路：顺时针旋转 90° = 先沿主对角线转置（matrix[i][j] 与 matrix[j][i] 交换），
     * 再将每一行左右翻转，全程原地完成。
     * 时间复杂度：O(n^2)；空间复杂度：O(1)。
     */
    public void rotate(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
        for (int i = 0; i < n; i++) {
            reverse(matrix[i], 0, n - 1);
        }
    }

    /**
     * 240. 搜索二维矩阵 II
     * 思路：利用"每行升序、每列升序"的性质，从右上角出发：
     * 当前值比 target 大则左移一列（排除该列），比 target 小则下移一行（排除该行），
     * 相等则找到。每步排除一行或一列。
     * 时间复杂度：O(m + n)；空间复杂度：O(1)。
     */
    public boolean searchMatrixII(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int row = 0, col = n - 1;

        while (row < m && col >= 0) {
            if (matrix[row][col] == target) return true;
            if (matrix[row][col] > target) col--;
            else row++;
        }
        return false;
    }

    // =========================
    // 七、链表
    // =========================

    /**
     * 160. 相交链表
     * 思路：双指针消除长度差。pA 走完链表 A 后接着走链表 B，pB 走完 B 后接着走 A，
     * 两指针总路程相同（lenA + lenB），若相交必在交点相遇；不相交则同时到达 null。
     * 时间复杂度：O(m + n)；空间复杂度：O(1)。
     */
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode pA = headA, pB = headB;
        while (pA != pB) {
            pA = pA == null ? headB : pA.next;
            pB = pB == null ? headA : pB.next;
        }
        return pA;
    }

    /**
     * 206. 反转链表
     * 思路：迭代法。用 prev、cur 两个指针沿链表前进，每步先保存 cur.next，
     * 再把 cur.next 指向 prev，然后两指针整体后移；结束时 prev 即新头结点。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public ListNode reverseList(ListNode head) {
        ListNode prev = null, cur = head;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    /**
     * 234. 回文链表
     * 思路：快慢指针找到链表中点，反转后半段链表，再从头和从中点同时向后逐一比较值；
     * 全部相等即为回文。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) return true;

        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode second = reverseList(slow);
        ListNode p1 = head, p2 = second;
        while (p2 != null) {
            if (p1.val != p2.val) return false;
            p1 = p1.next;
            p2 = p2.next;
        }
        return true;
    }

    /**
     * 141. 环形链表
     * 思路：Floyd 快慢指针。慢指针每次走一步、快指针每次走两步，
     * 若链表有环两者必然相遇；快指针先到 null 则无环。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    /**
     * 21. 合并两个有序链表
     * 思路：哑结点 + 双指针归并。每次取两链表头部较小的节点接到结果尾部，
     * 一方走完后把另一方剩余部分整体接上。
     * 时间复杂度：O(m + n)；空间复杂度：O(1)。
     */
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                cur.next = list1;
                list1 = list1.next;
            } else {
                cur.next = list2;
                list2 = list2.next;
            }
            cur = cur.next;
        }
        cur.next = list1 != null ? list1 : list2;
        return dummy.next;
    }

    /**
     * 142. 环形链表 II
     * 思路：快慢指针相遇后，把一个指针放回头结点，两指针改为同速前进，
     * 再次相遇的位置就是入环点（由"头到入环点距离 = 相遇点沿环走到入环点距离"的数学结论保证）。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public ListNode detectCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                ListNode p = head;
                while (p != slow) {
                    p = p.next;
                    slow = slow.next;
                }
                return p;
            }
        }
        return null;
    }

    /**
     * 2. 两数相加
     * 思路：模拟竖式加法。两链表同步向后遍历，逐位相加并维护进位 carry，
     * 每位的结果为 sum % 10，新进位为 sum / 10；循环条件包含 carry != 0 以处理最高位进位。
     * 时间复杂度：O(max(m, n))；空间复杂度：O(1)（不含结果链表）。
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        int carry = 0;

        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            cur.next = new ListNode(sum % 10);
            carry = sum / 10;
            cur = cur.next;
        }
        return dummy.next;
    }

    /**
     * 19. 删除链表的倒数第 N 个结点
     * 思路：快慢指针一次遍历。快指针先走 n 步，然后快慢指针同速前进，
     * 快指针到达末尾时，慢指针正好停在待删节点的前驱上，直接跳过目标节点即可；
     * 哑结点用于统一处理删除头结点的情况。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy, slow = dummy;

        for (int i = 0; i < n; i++) fast = fast.next;
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }
        slow.next = slow.next.next;
        return dummy.next;
    }

    /**
     * 24. 两两交换链表中的节点
     * 思路：哑结点 + 迭代。每轮取 prev 后面的两个节点 a、b，
     * 按 prev→b→a→next 重新接线完成交换，然后 prev 移动到 a 继续处理下一对。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;

        while (prev.next != null && prev.next.next != null) {
            ListNode a = prev.next;
            ListNode b = a.next;
            ListNode next = b.next;

            prev.next = b;
            b.next = a;
            a.next = next;

            prev = a;
        }
        return dummy.next;
    }

    /**
     * 138. 随机链表的复制
     * 思路：哈希表两次遍历。第一次遍历为每个原节点创建对应新节点并存入 map（原节点 → 新节点）；
     * 第二次遍历通过 map 把新节点的 next 和 random 指向对应的新节点。
     * 时间复杂度：O(n)；空间复杂度：O(n)。
     */
    public Node copyRandomList(Node head) {
        if (head == null) return null;

        Map<Node, Node> map = new HashMap<>();
        Node cur = head;
        while (cur != null) {
            map.put(cur, new Node(cur.val));
            cur = cur.next;
        }

        cur = head;
        while (cur != null) {
            map.get(cur).next = map.get(cur.next);
            map.get(cur).random = map.get(cur.random);
            cur = cur.next;
        }
        return map.get(head);
    }

    /**
     * 148. 排序链表
     * 思路：归并排序（自顶向下）。快慢指针找中点把链表断成两半，
     * 分别递归排序后用"合并两个有序链表"的方法合并。
     * 时间复杂度：O(n log n)；空间复杂度：O(log n)（递归栈）。
     */
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;

        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode right = slow.next;
        slow.next = null;

        ListNode leftSorted = sortList(head);
        ListNode rightSorted = sortList(right);
        return mergeTwoLists(leftSorted, rightSorted);
    }

    /**
     * 146. LRU 缓存
     * 思路：哈希表 + 双向链表。哈希表实现 O(1) 查找节点；双向链表维护访问顺序，
     * 头部是最近使用、尾部是最久未使用。get/put 命中时把节点移到头部；
     * 容量超限时删除尾部节点并同步从哈希表移除。head/tail 哨兵节点简化边界处理。
     * 时间复杂度：get/put 均为 O(1)；空间复杂度：O(capacity)。
     */
    public static class LRUCache {
        private static class DNode {
            int key;
            int value;
            DNode prev;
            DNode next;
            DNode(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }

        private final int capacity;
        private final Map<Integer, DNode> map = new HashMap<>();
        private final DNode head = new DNode(0, 0);
        private final DNode tail = new DNode(0, 0);

        public LRUCache(int capacity) {
            this.capacity = capacity;
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            DNode node = map.get(key);
            if (node == null) return -1;
            moveToHead(node);
            return node.value;
        }

        public void put(int key, int value) {
            DNode node = map.get(key);
            if (node != null) {
                node.value = value;
                moveToHead(node);
                return;
            }

            DNode newNode = new DNode(key, value);
            map.put(key, newNode);
            addToHead(newNode);

            if (map.size() > capacity) {
                DNode removed = removeTail();
                map.remove(removed.key);
            }
        }

        private void moveToHead(DNode node) {
            remove(node);
            addToHead(node);
        }

        private void addToHead(DNode node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        private void remove(DNode node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private DNode removeTail() {
            DNode node = tail.prev;
            remove(node);
            return node;
        }
    }

    // =========================
    // 八、二叉树
    // =========================

    /**
     * 94. 二叉树的中序遍历
     * 思路：栈模拟迭代中序遍历。沿左子树一路入栈直到最左端，弹栈访问节点值，
     * 再转向该节点的右子树重复上述过程，直到栈空且指针为空。
     * 时间复杂度：O(n)；空间复杂度：O(h)，h 为树高，最坏 O(n)。
     */
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;

        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            ans.add(cur.val);
            cur = cur.right;
        }
        return ans;
    }

    /**
     * 104. 二叉树的最大深度
     * 思路：递归。最大深度 = 左右子树最大深度中的较大值 + 1；空节点深度为 0。
     * 时间复杂度：O(n)；空间复杂度：O(h)（递归栈，最坏 O(n)）。
     */
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
    }

    /**
     * 226. 翻转二叉树
     * 思路：递归。先分别翻转左右子树，再交换当前节点的左右孩子指针。
     * 时间复杂度：O(n)；空间复杂度：O(h)（递归栈，最坏 O(n)）。
     */
    public TreeNode invertTree(TreeNode root) {
        if (root == null) return null;
        TreeNode left = invertTree(root.left);
        TreeNode right = invertTree(root.right);
        root.left = right;
        root.right = left;
        return root;
    }

    /**
     * 101. 对称二叉树
     * 思路：递归判断两棵子树是否互为镜像：两节点值相等，
     * 且 a 的左子树与 b 的右子树互为镜像、a 的右子树与 b 的左子树互为镜像。
     * 从根的左右孩子开始比较。
     * 时间复杂度：O(n)；空间复杂度：O(h)（递归栈，最坏 O(n)）。
     */
    public boolean isSymmetric(TreeNode root) {
        if (root == null) return true;
        return isMirror(root.left, root.right);
    }

    private boolean isMirror(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && isMirror(a.left, b.right) && isMirror(a.right, b.left);
    }

    /**
     * 543. 二叉树的直径
     * 思路：DFS 求深度的过程中顺便更新答案。对每个节点，经过它的最长路径 = 左子树深度 + 右子树深度，
     * 用全局变量记录最大值；函数本身返回以该节点为根的深度（max(左, 右) + 1）供父节点使用。
     * 时间复杂度：O(n)；空间复杂度：O(h)（递归栈，最坏 O(n)）。
     */
    private int diameter;

    public int diameterOfBinaryTree(TreeNode root) {
        diameter = 0;
        diameterDepth(root);
        return diameter;
    }

    private int diameterDepth(TreeNode root) {
        if (root == null) return 0;
        int left = diameterDepth(root.left);
        int right = diameterDepth(root.right);
        diameter = Math.max(diameter, left + right);
        return Math.max(left, right) + 1;
    }

    /**
     * 108. 将有序数组转换为二叉搜索树
     * 思路：分治。每次取当前区间的中间元素作为根节点（保证左右子树节点数量平衡），
     * 递归构建左半区间为左子树、右半区间为右子树。
     * 时间复杂度：O(n)；空间复杂度：O(log n)（递归栈）。
     */
    public TreeNode sortedArrayToBST(int[] nums) {
        return buildBST(nums, 0, nums.length - 1);
    }

    private TreeNode buildBST(int[] nums, int left, int right) {
        if (left > right) return null;
        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = buildBST(nums, left, mid - 1);
        root.right = buildBST(nums, mid + 1, right);
        return root;
    }

    /**
     * 102. 二叉树的层序遍历
     * 思路：BFS。用队列逐层处理：每轮先记录当前队列长度（即该层节点数），
     * 弹出该层所有节点收集值，并把它们的非空子节点入队作为下一层。
     * 时间复杂度：O(n)；空间复杂度：O(n)（队列最多存一层节点）。
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) return ans;

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            ans.add(level);
        }
        return ans;
    }

    /**
     * 98. 验证二叉搜索树
     * 思路：递归 + 上下界。每个节点的值必须落在开区间 (lower, upper) 内；
     * 向左递归时收紧上界为当前值，向右递归时收紧下界为当前值。
     * 用 long 类型边界避免节点值恰为 int 极值时的边界问题。
     * 时间复杂度：O(n)；空间复杂度：O(h)（递归栈，最坏 O(n)）。
     */
    public boolean isValidBST(TreeNode root) {
        return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean isValidBST(TreeNode root, long lower, long upper) {
        if (root == null) return true;
        if (root.val <= lower || root.val >= upper) return false;
        return isValidBST(root.left, lower, root.val) && isValidBST(root.right, root.val, upper);
    }

    /**
     * 230. 二叉搜索树中第 K 小的元素
     * 思路：BST 的中序遍历结果是升序序列，用栈迭代中序遍历，
     * 每弹出一个节点计数减一，第 k 个弹出的节点值即为答案，可提前终止。
     * 时间复杂度：O(h + k)，最坏 O(n)；空间复杂度：O(h)。
     */
    public int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;

        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            if (--k == 0) return cur.val;
            cur = cur.right;
        }
        return -1;
    }

    /**
     * 199. 二叉树的右视图
     * 思路：BFS 层序遍历，每层只取最后一个（最右侧）节点的值加入答案。
     * 时间复杂度：O(n)；空间复杂度：O(n)（队列）。
     */
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        if (root == null) return ans;

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (i == size - 1) ans.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return ans;
    }

    /**
     * 114. 二叉树展开为链表
     * 思路：反向前序遍历（右 → 左 → 根）。用全局变量 flattenPrev 记录已处理部分的头结点，
     * 访问每个节点时把它的 right 指向 flattenPrev、left 置空，再更新 flattenPrev 为当前节点，
     * 相当于从链表尾部往前逐个头插。
     * 时间复杂度：O(n)；空间复杂度：O(h)（递归栈，最坏 O(n)）。
     */
    private TreeNode flattenPrev;

    public void flatten(TreeNode root) {
        flattenPrev = null;
        flattenDfs(root);
    }

    private void flattenDfs(TreeNode root) {
        if (root == null) return;
        flattenDfs(root.right);
        flattenDfs(root.left);
        root.right = flattenPrev;
        root.left = null;
        flattenPrev = root;
    }

    /**
     * 105. 从前序与中序遍历序列构造二叉树
     * 思路：分治。前序序列第一个元素是根节点，在中序序列中定位根的位置（用哈希表 O(1) 查找），
     * 即可算出左子树的节点数，从而划分出前序/中序中左右子树对应的区间，递归构建。
     * 时间复杂度：O(n)；空间复杂度：O(n)（哈希表 + 递归栈）。
     */
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) indexMap.put(inorder[i], i);
        return buildTree(preorder, 0, preorder.length - 1, inorder, 0, inorder.length - 1, indexMap);
    }

    private TreeNode buildTree(
            int[] preorder, int preLeft, int preRight,
            int[] inorder, int inLeft, int inRight,
            Map<Integer, Integer> indexMap
    ) {
        if (preLeft > preRight) return null;

        int rootVal = preorder[preLeft];
        TreeNode root = new TreeNode(rootVal);
        int rootIndex = indexMap.get(rootVal);
        int leftSize = rootIndex - inLeft;

        root.left = buildTree(
                preorder, preLeft + 1, preLeft + leftSize,
                inorder, inLeft, rootIndex - 1,
                indexMap
        );
        root.right = buildTree(
                preorder, preLeft + leftSize + 1, preRight,
                inorder, rootIndex + 1, inRight,
                indexMap
        );
        return root;
    }

    /**
     * 437. 路径总和 III
     * 思路：前缀和 + 哈希表 + DFS（560 题在树上的推广）。DFS 时维护从根到当前节点的路径和 curSum，
     * 表中查 curSum - targetSum 的出现次数即为以当前节点结尾的合法路径数；
     * 进入子树前把 curSum 计入表中，回溯时撤销计数，保证只统计祖先链上的前缀和。
     * 时间复杂度：O(n)；空间复杂度：O(n)（哈希表 + 递归栈）。
     */
    public int pathSum(TreeNode root, int targetSum) {
        Map<Long, Integer> prefix = new HashMap<>();
        prefix.put(0L, 1);
        return pathSumDfs(root, 0L, targetSum, prefix);
    }

    private int pathSumDfs(TreeNode root, long curSum, int targetSum, Map<Long, Integer> prefix) {
        if (root == null) return 0;

        curSum += root.val;
        int ans = prefix.getOrDefault(curSum - targetSum, 0);
        prefix.put(curSum, prefix.getOrDefault(curSum, 0) + 1);

        ans += pathSumDfs(root.left, curSum, targetSum, prefix);
        ans += pathSumDfs(root.right, curSum, targetSum, prefix);

        prefix.put(curSum, prefix.get(curSum) - 1);
        return ans;
    }

    /**
     * 236. 二叉树的最近公共祖先
     * 思路：递归后序遍历。若当前节点为空或等于 p/q 直接返回；
     * 分别在左右子树中查找 p、q：两侧都找到说明当前节点就是 LCA；
     * 只有一侧找到则结果在那一侧（或那一侧的节点本身就是 LCA）。
     * 时间复杂度：O(n)；空间复杂度：O(h)（递归栈，最坏 O(n)）。
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) return root;
        return left != null ? left : right;
    }

    // =========================
    // 九、图论 / BFS / Trie
    // =========================

    /**
     * 200. 岛屿数量
     * 思路：遍历网格，每遇到一个 '1' 就计数加一，并从该点 DFS 把整个连通的岛屿全部
     * 改为 '0'（淹没标记），避免重复计数。
     * 时间复杂度：O(m*n)；空间复杂度：O(m*n)（DFS 递归栈最坏情况）。
     */
    public int numIslands(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        int ans = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    ans++;
                    dfsIsland(grid, i, j);
                }
            }
        }
        return ans;
    }

    private void dfsIsland(char[][] grid, int i, int j) {
        int m = grid.length, n = grid[0].length;
        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] != '1') return;
        grid[i][j] = '0';
        dfsIsland(grid, i + 1, j);
        dfsIsland(grid, i - 1, j);
        dfsIsland(grid, i, j + 1);
        dfsIsland(grid, i, j - 1);
    }

    /**
     * 994. 腐烂的橘子
     * 思路：多源 BFS。先把所有腐烂橘子入队、统计新鲜橘子数；
     * 然后逐层（每层代表一分钟）向四个方向感染新鲜橘子并入队，
     * BFS 结束后若还有新鲜橘子返回 -1，否则返回经过的分钟数。
     * 时间复杂度：O(m*n)；空间复杂度：O(m*n)（队列）。
     */
    public int orangesRotting(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        Queue<int[]> queue = new ArrayDeque<>();
        int fresh = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 2) queue.offer(new int[]{i, j});
                else if (grid[i][j] == 1) fresh++;
            }
        }

        int minutes = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty() && fresh > 0) {
            int size = queue.size();
            minutes++;
            for (int s = 0; s < size; s++) {
                int[] cur = queue.poll();
                for (int[] d : dirs) {
                    int x = cur[0] + d[0];
                    int y = cur[1] + d[1];
                    if (x < 0 || x >= m || y < 0 || y >= n || grid[x][y] != 1) continue;
                    grid[x][y] = 2;
                    fresh--;
                    queue.offer(new int[]{x, y});
                }
            }
        }
        return fresh == 0 ? minutes : -1;
    }

    /**
     * 207. 课程表
     * 思路：拓扑排序（Kahn 算法 / BFS）。建邻接表并统计每门课的入度，
     * 把入度为 0 的课入队；每出队一门课（视为已学），把它指向的课程入度减一，
     * 减到 0 就入队。最终学完的课程数等于总数则无环，可以完成。
     * 时间复杂度：O(V + E)；空间复杂度：O(V + E)。
     */
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
        int[] indegree = new int[numCourses];

        for (int[] p : prerequisites) {
            int course = p[0];
            int pre = p[1];
            graph.get(pre).add(course);
            indegree[course]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) queue.offer(i);
        }

        int learned = 0;
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            learned++;
            for (int next : graph.get(cur)) {
                if (--indegree[next] == 0) queue.offer(next);
            }
        }
        return learned == numCourses;
    }

    /**
     * 208. 实现 Trie 前缀树
     * 思路：多叉树，每个节点含 26 个子指针（对应 a-z）和一个 isEnd 结束标记。
     * insert 沿字符路径逐层创建/走到节点并在末尾标记 isEnd；
     * search 要求路径存在且末尾节点 isEnd 为 true；startsWith 只要求路径存在。
     * 时间复杂度：三个操作均为 O(L)，L 为单词长度；空间复杂度：O(总字符数 × 26)。
     */
    public static class Trie {
        private static class TrieNode {
            TrieNode[] children = new TrieNode[26];
            boolean isEnd;
        }

        private final TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        public void insert(String word) {
            TrieNode cur = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) cur.children[idx] = new TrieNode();
                cur = cur.children[idx];
            }
            cur.isEnd = true;
        }

        public boolean search(String word) {
            TrieNode node = findNode(word);
            return node != null && node.isEnd;
        }

        public boolean startsWith(String prefix) {
            return findNode(prefix) != null;
        }

        private TrieNode findNode(String s) {
            TrieNode cur = root;
            for (char c : s.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) return null;
                cur = cur.children[idx];
            }
            return cur;
        }
    }

    // =========================
    // 十、回溯
    // =========================

    /**
     * 46. 全排列
     * 思路：回溯。用 used 数组标记已选元素，每层从未使用的元素中选一个加入路径，
     * 路径长度等于数组长度时收集一个排列；回退时撤销选择（移除路径末尾并取消标记）。
     * 时间复杂度：O(n × n!)；空间复杂度：O(n)（路径 + 标记数组 + 递归栈，不含结果集）。
     */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        permuteDfs(nums, used, new ArrayList<>(), ans);
        return ans;
    }

    private void permuteDfs(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> ans) {
        if (path.size() == nums.length) {
            ans.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            path.add(nums[i]);
            permuteDfs(nums, used, path, ans);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    /**
     * 78. 子集
     * 思路：回溯枚举。每次进入递归先把当前路径收集为一个子集，
     * 然后从 start 开始依次尝试加入每个元素并递归，用 start 保证元素只向后选取、不重复枚举。
     * 时间复杂度：O(n × 2^n)；空间复杂度：O(n)（路径 + 递归栈，不含结果集）。
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        subsetsDfs(nums, 0, new ArrayList<>(), ans);
        return ans;
    }

    private void subsetsDfs(int[] nums, int start, List<Integer> path, List<List<Integer>> ans) {
        ans.add(new ArrayList<>(path));
        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);
            subsetsDfs(nums, i + 1, path, ans);
            path.remove(path.size() - 1);
        }
    }

    /**
     * 17. 电话号码的字母组合
     * 思路：回溯。用映射表存每个数字对应的字母，按位递归：
     * 对当前数字的每个候选字母，追加到路径后处理下一位，处理完回退；
     * 路径长度等于 digits 长度时收集一个组合。
     * 时间复杂度：O(4^n × n)，n 为数字个数（单个数字最多映射 4 个字母）；空间复杂度：O(n)（递归栈，不含结果集）。
     */
    public List<String> letterCombinations(String digits) {
        List<String> ans = new ArrayList<>();
        if (digits == null || digits.length() == 0) return ans;
        String[] map = {
                "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
        };
        letterDfs(digits, 0, new StringBuilder(), map, ans);
        return ans;
    }

    private void letterDfs(String digits, int index, StringBuilder path, String[] map, List<String> ans) {
        if (index == digits.length()) {
            ans.add(path.toString());
            return;
        }
        String letters = map[digits.charAt(index) - '0'];
        for (char c : letters.toCharArray()) {
            path.append(c);
            letterDfs(digits, index + 1, path, map, ans);
            path.deleteCharAt(path.length() - 1);
        }
    }

    /**
     * 39. 组合总和
     * 思路：排序 + 回溯。每层从 start 开始枚举候选数，同一个数可重复选取（递归时仍传 i），
     * 用 start 避免产生顺序不同的重复组合；剩余目标 remain 减到 0 收集答案，
     * 排序后遇到 candidates[i] > remain 可直接剪枝跳出。
     * 时间复杂度：O(所有可行组合的总长度)，最坏指数级；空间复杂度：O(target/最小候选数)（递归深度）。
     */
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> ans = new ArrayList<>();
        combinationDfs(candidates, target, 0, new ArrayList<>(), ans);
        return ans;
    }

    private void combinationDfs(int[] candidates, int remain, int start, List<Integer> path, List<List<Integer>> ans) {
        if (remain == 0) {
            ans.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remain) break;
            path.add(candidates[i]);
            combinationDfs(candidates, remain - candidates[i], i, path, ans);
            path.remove(path.size() - 1);
        }
    }

    /**
     * 22. 括号生成
     * 思路：回溯 + 剪枝。维护已使用的左括号数 left 和右括号数 right：
     * left < n 时可以放左括号；right < left 时才可以放右括号（保证任意前缀合法）；
     * 长度达到 2n 时即为一个有效组合。
     * 时间复杂度：O(4^n / √n)（第 n 个卡特兰数量级）；空间复杂度：O(n)（递归栈，不含结果集）。
     */
    public List<String> generateParenthesis(int n) {
        List<String> ans = new ArrayList<>();
        parenthesisDfs(n, 0, 0, new StringBuilder(), ans);
        return ans;
    }

    private void parenthesisDfs(int n, int left, int right, StringBuilder path, List<String> ans) {
        if (path.length() == 2 * n) {
            ans.add(path.toString());
            return;
        }
        if (left < n) {
            path.append('(');
            parenthesisDfs(n, left + 1, right, path, ans);
            path.deleteCharAt(path.length() - 1);
        }
        if (right < left) {
            path.append(')');
            parenthesisDfs(n, left, right + 1, path, ans);
            path.deleteCharAt(path.length() - 1);
        }
    }

    /**
     * 79. 单词搜索
     * 思路：以每个格子为起点做回溯 DFS。当前格字符与 word 当前位匹配时，
     * 标记 visited 后向四个方向继续匹配下一位，回退时取消标记；
     * index 走到 word 末尾说明全部匹配成功。
     * 时间复杂度：O(m*n × 3^L)，L 为单词长度（除起点外每步最多 3 个方向）；空间复杂度：O(m*n + L)。
     */
    public boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        boolean[][] visited = new boolean[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (existDfs(board, word, 0, i, j, visited)) return true;
            }
        }
        return false;
    }

    private boolean existDfs(char[][] board, String word, int index, int i, int j, boolean[][] visited) {
        if (index == word.length()) return true;
        int m = board.length, n = board[0].length;
        if (i < 0 || i >= m || j < 0 || j >= n || visited[i][j] || board[i][j] != word.charAt(index)) {
            return false;
        }

        visited[i][j] = true;
        boolean found = existDfs(board, word, index + 1, i + 1, j, visited)
                || existDfs(board, word, index + 1, i - 1, j, visited)
                || existDfs(board, word, index + 1, i, j + 1, visited)
                || existDfs(board, word, index + 1, i, j - 1, visited);
        visited[i][j] = false;
        return found;
    }

    /**
     * 131. 分割回文串
     * 思路：回溯。从 start 位置开始枚举所有可能的切割终点 end，
     * 若子串 s[start..end] 是回文则加入路径并递归处理剩余部分，回退时移除；
     * start 到达字符串末尾时收集一种分割方案。
     * 时间复杂度：O(n × 2^n)（最坏每个位置都可切且都是回文）；空间复杂度：O(n)（递归栈 + 路径，不含结果集）。
     */
    public List<List<String>> partition(String s) {
        List<List<String>> ans = new ArrayList<>();
        partitionDfs(s, 0, new ArrayList<>(), ans);
        return ans;
    }

    private void partitionDfs(String s, int start, List<String> path, List<List<String>> ans) {
        if (start == s.length()) {
            ans.add(new ArrayList<>(path));
            return;
        }
        for (int end = start; end < s.length(); end++) {
            if (isPalindromeString(s, start, end)) {
                path.add(s.substring(start, end + 1));
                partitionDfs(s, end + 1, path, ans);
                path.remove(path.size() - 1);
            }
        }
    }

    private boolean isPalindromeString(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left++) != s.charAt(right--)) return false;
        }
        return true;
    }

    // =========================
    // 十一、二分查找
    // =========================

    /**
     * 35. 搜索插入位置
     * 思路：标准二分查找下界（lower_bound）。在左闭右开区间 [left, right) 上二分，
     * 找到第一个 >= target 的位置，即为目标所在位置或应插入的位置。
     * 时间复杂度：O(log n)；空间复杂度：O(1)。
     */
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) left = mid + 1;
            else right = mid;
        }
        return left;
    }

    /**
     * 74. 搜索二维矩阵
     * 思路：整个矩阵按行拼接后是一个有序一维数组，直接在 [0, m*n-1] 上二分，
     * 下标 mid 映射回矩阵坐标为 (mid / n, mid % n)。
     * 时间复杂度：O(log(m*n))；空间复杂度：O(1)。
     */
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int left = 0, right = m * n - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int value = matrix[mid / n][mid % n];
            if (value == target) return true;
            if (value < target) left = mid + 1;
            else right = mid - 1;
        }
        return false;
    }

    /**
     * 34. 在排序数组中查找元素的第一个和最后一个位置
     * 思路：两次二分求下界。第一个位置 = lowerBound(target)；
     * 最后一个位置 = lowerBound(target + 1) - 1；
     * 若下界越界或对应值不是 target，说明不存在，返回 [-1, -1]。
     * 时间复杂度：O(log n)；空间复杂度：O(1)。
     */
    public int[] searchRange(int[] nums, int target) {
        int left = lowerBound(nums, target);
        if (left == nums.length || nums[left] != target) return new int[]{-1, -1};
        int right = lowerBound(nums, target + 1) - 1;
        return new int[]{left, right};
    }

    private int lowerBound(int[] nums, int target) {
        int left = 0, right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) left = mid + 1;
            else right = mid;
        }
        return left;
    }

    /**
     * 33. 搜索旋转排序数组
     * 思路：二分。旋转数组被 mid 分成两半后必有一半是有序的：
     * 通过 nums[left] <= nums[mid] 判断哪半有序，再看 target 是否落在有序的那半内，
     * 决定收缩到哪一侧继续查找。
     * 时间复杂度：O(log n)；空间复杂度：O(1)。
     */
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;

            if (nums[left] <= nums[mid]) {
                if (nums[left] <= target && target < nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {
                if (nums[mid] < target && target <= nums[right]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return -1;
    }

    /**
     * 153. 寻找旋转排序数组中的最小值
     * 思路：二分，把 nums[mid] 与 nums[right] 比较：
     * mid 处值更大说明最小值在 mid 右侧（left = mid + 1）；
     * 否则最小值在 mid 或其左侧（right = mid）。收敛时 left 即最小值位置。
     * 时间复杂度：O(log n)；空间复杂度：O(1)。
     */
    public int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) left = mid + 1;
            else right = mid;
        }
        return nums[left];
    }

    // =========================
    // 十二、栈 / 单调栈
    // =========================

    /**
     * 20. 有效的括号
     * 思路：栈匹配。遇到左括号入栈；遇到右括号时弹出栈顶检查是否为对应的左括号，
     * 不匹配或栈已空则无效；遍历结束后栈必须为空。
     * 时间复杂度：O(n)；空间复杂度：O(n)。
     */
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;
                char left = stack.pop();
                if (c == ')' && left != '(') return false;
                if (c == ']' && left != '[') return false;
                if (c == '}' && left != '{') return false;
            }
        }
        return stack.isEmpty();
    }

    /**
     * 155. 最小栈
     * 思路：辅助栈。data 栈正常存数据，min 栈与之同步压入/弹出，
     * 每次压入"当前值与旧栈顶最小值中的较小者"，因此 min 栈顶始终是当前所有元素的最小值。
     * 时间复杂度：push/pop/top/getMin 均为 O(1)；空间复杂度：O(n)。
     */
    public static class MinStack {
        private final Deque<Integer> data = new ArrayDeque<>();
        private final Deque<Integer> min = new ArrayDeque<>();

        public void push(int val) {
            data.push(val);
            if (min.isEmpty()) min.push(val);
            else min.push(Math.min(val, min.peek()));
        }

        public void pop() {
            data.pop();
            min.pop();
        }

        public int top() {
            return data.peek();
        }

        public int getMin() {
            return min.peek();
        }
    }

    /**
     * 394. 字符串解码
     * 思路：双栈。遇到数字累积倍数 num；遇到 '[' 把当前倍数和已拼接的字符串分别入栈并重新开始；
     * 遇到 ']' 弹出倍数和外层字符串，把当前串重复 count 次拼接到外层串后面；
     * 普通字符直接追加到当前串。
     * 时间复杂度：O(输出串长度)；空间复杂度：O(输出串长度)。
     */
    public String decodeString(String s) {
        Deque<Integer> countStack = new ArrayDeque<>();
        Deque<StringBuilder> strStack = new ArrayDeque<>();
        StringBuilder cur = new StringBuilder();
        int num = 0;

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '[') {
                countStack.push(num);
                strStack.push(cur);
                cur = new StringBuilder();
                num = 0;
            } else if (c == ']') {
                int count = countStack.pop();
                StringBuilder prev = strStack.pop();
                for (int i = 0; i < count; i++) prev.append(cur);
                cur = prev;
            } else {
                cur.append(c);
            }
        }
        return cur.toString();
    }

    /**
     * 739. 每日温度
     * 思路：单调递减栈（存下标）。遍历温度数组，当前温度高于栈顶下标对应温度时，
     * 不断弹栈并计算等待天数（当前下标 - 弹出下标）；然后把当前下标入栈。
     * 每个下标最多入栈出栈各一次。
     * 时间复杂度：O(n)；空间复杂度：O(n)。
     */
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] ans = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prev = stack.pop();
                ans[prev] = i - prev;
            }
            stack.push(i);
        }
        return ans;
    }

    // =========================
    // 十三、堆 / 快速选择
    // =========================

    /**
     * 215. 数组中的第 K 个最大元素
     * 思路：快速选择。第 K 大即升序排序后下标为 n-k 的元素。每轮 partition 把基准放到最终位置，
     * 若该位置正好是 n-k 则返回，否则只递归/迭代处理包含目标下标的那一侧。
     * 时间复杂度：平均 O(n)，最坏 O(n^2)；空间复杂度：O(1)（迭代实现）。
     */
    public int findKthLargest(int[] nums, int k) {
        int target = nums.length - k;
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int pivot = quickSelectPartition(nums, left, right);
            if (pivot == target) return nums[pivot];
            if (pivot < target) left = pivot + 1;
            else right = pivot - 1;
        }
        return -1;
    }

    private int quickSelectPartition(int[] nums, int left, int right) {
        int pivot = nums[right];
        int i = left;
        for (int j = left; j < right; j++) {
            if (nums[j] <= pivot) {
                swap(nums, i, j);
                i++;
            }
        }
        swap(nums, i, right);
        return i;
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }

    /**
     * 347. 前 K 个高频元素
     * 思路：哈希表统计频次 + 容量为 k 的小顶堆（按频次排序）。
     * 依次把 (元素, 频次) 入堆，堆大小超过 k 就弹出频次最小的，
     * 最终堆中留下的就是频次最高的 k 个元素。
     * 时间复杂度：O(n log k)；空间复杂度：O(n)。
     */
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) freq.put(num, freq.getOrDefault(num, 0) + 1);

        PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            heap.offer(new int[]{entry.getKey(), entry.getValue()});
            if (heap.size() > k) heap.poll();
        }

        int[] ans = new int[k];
        for (int i = 0; i < k; i++) {
            ans[i] = heap.poll()[0];
        }
        return ans;
    }

    // =========================
    // 十四、贪心
    // =========================

    /**
     * 121. 买卖股票的最佳时机
     * 思路：一次遍历，维护到当前为止的历史最低价 minPrice，
     * 每天用"当天价格 - 历史最低价"更新最大利润。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int ans = 0;
        for (int price : prices) {
            minPrice = Math.min(minPrice, price);
            ans = Math.max(ans, price - minPrice);
        }
        return ans;
    }

    /**
     * 55. 跳跃游戏
     * 思路：贪心，维护能到达的最远位置 maxReach。顺序遍历，
     * 若当前下标超过 maxReach 说明走不到这里，返回 false；
     * 否则用 i + nums[i] 更新最远可达位置。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public boolean canJump(int[] nums) {
        int maxReach = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) return false;
            maxReach = Math.max(maxReach, i + nums[i]);
        }
        return true;
    }

    /**
     * 45. 跳跃游戏 II
     * 思路：贪心分层。end 表示当前这一跳能覆盖的右边界，遍历中不断更新下一跳可达的最远位置 maxReach；
     * 走到 end 时说明必须再跳一次，步数加一并把边界推进到 maxReach。
     * 注意遍历到 n-2 即可（到达终点前的最后一跳已计入）。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int jump(int[] nums) {
        int steps = 0;
        int end = 0;
        int maxReach = 0;

        for (int i = 0; i < nums.length - 1; i++) {
            maxReach = Math.max(maxReach, i + nums[i]);
            if (i == end) {
                steps++;
                end = maxReach;
            }
        }
        return steps;
    }

    /**
     * 763. 划分字母区间
     * 思路：贪心。先记录每个字母最后一次出现的下标；再次遍历时维护当前片段的右边界 end
     * （取片段内所有字母最后出现位置的最大值），当 i == end 时当前片段可以闭合，
     * 记录长度并开始下一段。
     * 时间复杂度：O(n)；空间复杂度：O(26) = O(1)。
     */
    public List<Integer> partitionLabels(String s) {
        int[] last = new int[26];
        for (int i = 0; i < s.length(); i++) {
            last[s.charAt(i) - 'a'] = i;
        }

        List<Integer> ans = new ArrayList<>();
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, last[s.charAt(i) - 'a']);
            if (i == end) {
                ans.add(end - start + 1);
                start = i + 1;
            }
        }
        return ans;
    }

    // =========================
    // 十五、动态规划
    // =========================

    /**
     * 70. 爬楼梯
     * 思路：动态规划（斐波那契）。到第 i 阶的方法数 = 到第 i-1 阶 + 到第 i-2 阶的方法数，
     * 用两个滚动变量代替数组。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int climbStairs(int n) {
        if (n <= 2) return n;
        int a = 1, b = 2;
        for (int i = 3; i <= n; i++) {
            int c = a + b;
            a = b;
            b = c;
        }
        return b;
    }

    /**
     * 118. 杨辉三角
     * 思路：逐行构造。每行首尾为 1，中间元素等于上一行相邻两数之和：
     * row[j] = prev[j-1] + prev[j]。
     * 时间复杂度：O(numRows^2)；空间复杂度：O(1)（不含结果集）。
     */
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> ans = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                if (j == 0 || j == i) row.add(1);
                else row.add(ans.get(i - 1).get(j - 1) + ans.get(i - 1).get(j));
            }
            ans.add(row);
        }
        return ans;
    }

    /**
     * 198. 打家劫舍
     * 思路：动态规划。设 dp[i] 为偷到第 i 家的最大金额：
     * dp[i] = max(dp[i-1]（不偷这家）, dp[i-2] + nums[i]（偷这家）)，
     * 用 prev1/prev2 两个滚动变量优化空间。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int rob(int[] nums) {
        int prev2 = 0, prev1 = 0;
        for (int num : nums) {
            int cur = Math.max(prev1, prev2 + num);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    /**
     * 279. 完全平方数
     * 思路：完全背包 DP。物品是所有不超过 n 的完全平方数，dp[j] 表示凑出 j 所需的最少
     * 平方数个数：dp[j] = min(dp[j], dp[j - square] + 1)，内层正序遍历允许物品重复使用。
     * 时间复杂度：O(n√n)；空间复杂度：O(n)。
     */
    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[0] = 0;

        for (int i = 1; i * i <= n; i++) {
            int square = i * i;
            for (int j = square; j <= n; j++) {
                dp[j] = Math.min(dp[j], dp[j - square] + 1);
            }
        }
        return dp[n];
    }

    /**
     * 322. 零钱兑换
     * 思路：完全背包 DP。dp[j] 表示凑出金额 j 所需的最少硬币数，初始化为 amount+1 表示不可达：
     * dp[j] = min(dp[j], dp[j - coin] + 1)。最终 dp[amount] 仍为 amount+1 则返回 -1。
     * 时间复杂度：O(amount × 硬币种数)；空间复杂度：O(amount)。
     */
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int coin : coins) {
            for (int j = coin; j <= amount; j++) {
                dp[j] = Math.min(dp[j], dp[j - coin] + 1);
            }
        }
        return dp[amount] == amount + 1 ? -1 : dp[amount];
    }

    /**
     * 139. 单词拆分
     * 思路：动态规划。dp[i] 表示前缀 s[0..i) 能否被字典单词拼出：
     * 枚举最后一个单词的起点 j，若 dp[j] 为 true 且 s[j..i) 在字典中，则 dp[i] 为 true。
     * 字典先转 HashSet 加速查找。
     * 时间复杂度：O(n^2)（不计子串哈希开销）；空间复杂度：O(n + 字典大小)。
     */
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> set = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;

        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && set.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[s.length()];
    }

    /**
     * 300. 最长递增子序列
     * 思路：贪心 + 二分（耐心排序）。tails[i] 保存长度为 i+1 的递增子序列的最小结尾值，
     * tails 天然有序；对每个数二分找到它在 tails 中的插入位置并覆盖，
     * 插到末尾则长度加一。最终 size 即 LIS 长度。
     * 时间复杂度：O(n log n)；空间复杂度：O(n)。
     */
    public int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;

        for (int num : nums) {
            int left = 0, right = size;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (tails[mid] < num) left = mid + 1;
                else right = mid;
            }
            tails[left] = num;
            if (left == size) size++;
        }
        return size;
    }

    /**
     * 152. 乘积最大子数组
     * 思路：动态规划，同时维护以当前元素结尾的最大乘积 max 和最小乘积 min
     * （负数乘以最小值可能变成最大值）。遇到负数先交换 max 和 min，
     * 再分别用当前值更新，每步用 max 更新全局答案。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int maxProduct(int[] nums) {
        int max = nums[0], min = nums[0], ans = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            if (x < 0) {
                int tmp = max;
                max = min;
                min = tmp;
            }
            max = Math.max(x, max * x);
            min = Math.min(x, min * x);
            ans = Math.max(ans, max);
        }
        return ans;
    }

    /**
     * 416. 分割等和子集
     * 思路：转化为 0-1 背包：能否从数组中选出和为 sum/2 的子集（sum 为奇数直接返回 false）。
     * dp[j] 表示能否凑出和 j：dp[j] = dp[j] || dp[j - num]，
     * 内层倒序遍历保证每个数只用一次。
     * 时间复杂度：O(n × target)，target = sum/2；空间复杂度：O(target)。
     */
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int num : nums) sum += num;
        if ((sum & 1) == 1) return false;

        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int num : nums) {
            for (int j = target; j >= num; j--) {
                dp[j] = dp[j] || dp[j - num];
            }
        }
        return dp[target];
    }

    // =========================
    // 十六、多维 DP
    // =========================

    /**
     * 62. 不同路径
     * 思路：动态规划 + 一维滚动数组。dp[j] 表示到达当前行第 j 列的路径数，
     * 转移为 dp[j] += dp[j-1]（上方来的路径数 + 左方来的路径数）；首行首列均初始化为 1。
     * 时间复杂度：O(m*n)；空间复杂度：O(n)。
     */
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] += dp[j - 1];
            }
        }
        return dp[n - 1];
    }

    /**
     * 64. 最小路径和
     * 思路：动态规划 + 一维滚动数组。dp[j] 表示到达当前行第 j 列的最小路径和：
     * dp[j] = min(dp[j]（上方）, dp[j-1]（左方）) + grid[i][j]；第一行只能从左边累加。
     * 时间复杂度：O(m*n)；空间复杂度：O(n)。
     */
    public int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] dp = new int[n];
        dp[0] = grid[0][0];

        for (int j = 1; j < n; j++) dp[j] = dp[j - 1] + grid[0][j];

        for (int i = 1; i < m; i++) {
            dp[0] += grid[i][0];
            for (int j = 1; j < n; j++) {
                dp[j] = Math.min(dp[j], dp[j - 1]) + grid[i][j];
            }
        }
        return dp[n - 1];
    }

    /**
     * 5. 最长回文子串
     * 思路：中心扩展法。回文中心共 2n-1 个（每个字符为中心的奇长度回文、
     * 每两个相邻字符间隙为中心的偶长度回文），从每个中心向两侧扩展求最长回文长度，
     * 记录最长回文的起点和长度，最后截取子串。
     * 时间复杂度：O(n^2)；空间复杂度：O(1)。
     */
    public String longestPalindrome(String s) {
        if (s == null || s.length() < 2) return s;
        int start = 0, maxLen = 1;

        for (int i = 0; i < s.length(); i++) {
            int len1 = expandAroundCenter(s, i, i);
            int len2 = expandAroundCenter(s, i, i + 1);
            int len = Math.max(len1, len2);
            if (len > maxLen) {
                maxLen = len;
                start = i - (len - 1) / 2;
            }
        }
        return s.substring(start, start + maxLen);
    }

    private int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    /**
     * 1143. 最长公共子序列
     * 思路：二维动态规划。dp[i][j] 表示 text1 前 i 个字符与 text2 前 j 个字符的 LCS 长度：
     * 末尾字符相等时 dp[i][j] = dp[i-1][j-1] + 1；
     * 不等时 dp[i][j] = max(dp[i-1][j], dp[i][j-1])。
     * 时间复杂度：O(m*n)；空间复杂度：O(m*n)。
     */
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    // =========================
    // 十七、技巧 / 位运算 / 排序
    // =========================

    /**
     * 136. 只出现一次的数字
     * 思路：异或运算。相同的数异或为 0，0 异或任何数为其本身，且异或满足交换律，
     * 全部异或后剩下的就是只出现一次的数字。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int singleNumber(int[] nums) {
        int ans = 0;
        for (int num : nums) ans ^= num;
        return ans;
    }

    /**
     * 169. 多数元素
     * 思路：Boyer-Moore 摩尔投票。维护候选人和票数：票数为 0 时换当前元素为候选人；
     * 遇到相同元素票数加一，不同则减一。由于多数元素超过半数，最终候选人必为多数元素。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int majorityElement(int[] nums) {
        int candidate = 0;
        int count = 0;
        for (int num : nums) {
            if (count == 0) candidate = num;
            count += num == candidate ? 1 : -1;
        }
        return candidate;
    }

    /**
     * 75. 颜色分类
     * 思路：荷兰国旗问题，三指针一次遍历。left 左边全是 0，right 右边全是 2，i 为扫描指针：
     * 遇 0 与 left 交换（两指针都前进），遇 2 与 right 交换（只有 right 左移，
     * 因为换来的值还未检查），遇 1 直接前进。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public void sortColors(int[] nums) {
        int left = 0, i = 0, right = nums.length - 1;
        while (i <= right) {
            if (nums[i] == 0) {
                swap(nums, left++, i++);
            } else if (nums[i] == 2) {
                swap(nums, i, right--);
            } else {
                i++;
            }
        }
    }

    /**
     * 31. 下一个排列
     * 思路：三步法。① 从后向前找第一个升序相邻对位置 i（nums[i] < nums[i+1]）；
     * ② 若存在，再从后向前找第一个大于 nums[i] 的数与之交换；
     * ③ 把 i 之后的降序段整体翻转成升序，得到字典序恰好大一位的排列；
     * 若整个数组已是最大排列（i < 0），直接翻转为最小排列。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public void nextPermutation(int[] nums) {
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) i--;

        if (i >= 0) {
            int j = nums.length - 1;
            while (nums[j] <= nums[i]) j--;
            swap(nums, i, j);
        }
        reverse(nums, i + 1, nums.length - 1);
    }

    /**
     * 287. 寻找重复数
     * 思路：把数组看成链表（i → nums[i]），重复数字意味着链表有环，转化为"环形链表 II"求入环点：
     * 快慢指针相遇后，一个指针回到起点同速前进，再次相遇处即重复的数字。
     * 不修改数组、只用常数空间。
     * 时间复杂度：O(n)；空间复杂度：O(1)。
     */
    public int findDuplicate(int[] nums) {
        int slow = nums[0];
        int fast = nums[nums[0]];

        while (slow != fast) {
            slow = nums[slow];
            fast = nums[nums[fast]];
        }

        slow = 0;
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }
}
