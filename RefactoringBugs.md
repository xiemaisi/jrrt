Here is a list of bugs we found in the refactoring engines of Eclipse and other popular Java IDEs. All test cases were evaluated on various versions of Eclipse and IntelliJ and bug reports were filed; we indicate which bugs were eventually fixed. Some cases were also evaluated on JBuilder 2007, NetBeans 6.0, and JDeveloper 11g, but so far have not been reported.

This list was started by Ran Ettinger and Mathieu Verbaere; in its current form it is due to Torbjörn Ekman and Max Schäfer.

# Bug in Rename Package #
<ol>
<li> <b>No Check for Name Clash with Class</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>    package p;<br>
    class r { }<br>
    ...<br>
    package p.q; <br>
    class s { }<br>
</code></pre></blockquote>

<blockquote>Eclipse allows renaming package <code>p.q</code> to <code>p.r</code>,<br>
resulting in uncompilable code due to name clash with class<br>
<code>p.r</code>. When undoing the refactoring, the error marker is<br>
not removed; it vanishes only after further editing and saving the files<br>
involved.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=208200'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, <a href='http://www.jetbrains.net/jira/browse/IDEA-20574'>IntelliJ</a> (fixed), and JDeveloper.</li>
</ol></blockquote>

# Bugs in Rename Type #
<ol>
<li> <b>Renaming Top Level Type Leads to Undiagnosed Shadowing of Library Type</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>  public class A {<br>
    public static void main(String[] args) {<br>
      new Thread() {<br>
        public void run() {<br>
          System.out.println(23);<br>
	}<br>
      }.start();<br>
    }<br>
  }<br>
<br>
  class MyThread {<br>
    public void start() {<br>
      System.out.println(42);<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>Renaming <code>MyThread</code> to <code>Thread</code> shadows the library class <code>java.lang.Thread</code>, which<br>
is not noticed by Eclipse. Thus the program changes from printing <code>23</code> before refactoring to printing<br>
<code>42</code> after.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=222848'>Eclipse Bugs</a>, turned out to be a duplicate.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, <a href='http://www.jetbrains.net/jira/browse/IDEA-20603'>IntelliJ</a>, and JDeveloper.</li></blockquote>

<li> <b>Renaming Member Type in Superclass Leads to Undiagnosed Shadowing</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>    class A {<br>
      static class B {<br>
        static int x = 42;<br>
      }<br>
    }<br>
	  <br>
    public class D {<br>
      static class C extends A {<br>
        static int x = 23;<br>
	static int m() { return C.x; }<br>
      }<br>
      public static void main(String[] args) {<br>
	System.out.println(C.m());<br>
      }<br>
    }<br>
</code></pre></blockquote>

<blockquote>This prints <code>23</code>. Renaming the class <code>B</code> inside<br>
<code>A</code> to <code>C</code> does not notice that the<br>
reference <code>C.x</code> in method <code>C.m</code> will now refer to<br>
<code>A.C.x</code> instead of <code>D.C.x</code>, so the resulting<br>
program prints <code>42</code>.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=220781'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, <a href='http://www.jetbrains.net/jira/browse/IDEA-20634'>IntelliJ</a> (fixed), and JDeveloper.</li></blockquote>

<li><b>Renaming a Member Type Does Not Check for Shadowing of Inherited Member Types</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>  import java.util.Map;<br>
<br>
  abstract class MyMap implements Map {<br>
    abstract static class MyEntry implements Entry {<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>Renaming the member class <code>MyEntry</code> to <code>Entry</code>
does not notice that this shadows the member type <code>Map.Entry</code>.<br>
The resulting program is</blockquote>

<pre><code>  import java.util.Map;<br>
<br>
  abstract class MyMap implements Map {<br>
    abstract static class Entry implements Entry {<br>
    }<br>
  }<br>
</code></pre>

<blockquote>which does not compile. It is possible to construct examples where the<br>
resulting code would still compile, but have a different behaviour.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=222045'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, JDeveloper, but not in IntelliJ.</li></blockquote>


<li><b>Renaming a Type Does Not Check for Capture by Type Variable</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>  public class A {<br>
    public static void main(String[] args) {<br>
      C&lt;A&gt; b = new D&lt;String&gt;();<br>
    }<br>
  }<br>
  class C&lt;T&gt; { }<br>
  class D&lt;B&gt; extends C&lt;A&gt; { }<br>
</code></pre></blockquote>

<blockquote>Renaming class <code>A</code> to <code>B</code> yields the following output:<br>
<pre><code>  public class B {<br>
    public static void main(String[] args) {<br>
      C&lt;B&gt; b = new D&lt;String&gt;();<br>
    }<br>
  }<br>
  class C&lt;T&gt; { }<br>
  class D&lt;B&gt; extends C&lt;B&gt; { }<br>
</code></pre></blockquote>

<blockquote>Note the capture of the argument type <code>A</code> by the type variable<br>
<code>B</code>:<br>
The program is not compilable, since <code>D&lt;String&gt;()</code> can no<br>
longer be cast to <code>D&lt;A&gt;</code>, but only to <code>D&lt;String&gt;</code>.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=220780'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, JDeveloper, but not in IntelliJ.</li>
</ol></blockquote>

# Bug in Rename Variable #
<ol>
<li><b>Renaming Field Can Shadow Static Import</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>  import static java.lang.Math.PI;<br>
<br>
  class Indiana {<br>
    static double myPI = 3.2;<br>
    static double circle_area(double r) {<br>
      return PI*r*r;<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>Renaming the field <code>myPI</code> to <code>PI</code> shadows the import from <code>java.lang.Math</code>.<br>
This problem is correctly diagnosed by Eclipse and JBuilder, but not by NetBeans and JDeveloper. A<br>
<a href='http://www.jetbrains.net/jira/browse/IDEA-20665'>slightly more complex example</a> fools IntelliJ.</li></blockquote>

<li><b>Renaming Local Variable Can Lead to Shadowing By Field</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>  public class A {<br>
    Object m() {<br>
      final int i = 23;<br>
      return new Object() {<br>
        int j = 42;<br>
        public String toString() {<br>
          return i+"";<br>
        }<br>
      };<br>
    }<br>
    public static void main(String[] args) {<br>
      System.out.println(new A().m());<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>This prints <code>23</code>. Renaming the local variable <code>i</code> in<br>
method <code>A.m</code> to <code>j</code> does not notice that the reference<br>
in the <code>toString()</code> method of the anonymous class will now<br>
refer to the field <code>j</code>, so the resulting program prints<br>
<code>42</code>.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=213144'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug also occurs in JBuilder, <a href='http://www.jetbrains.net/jira/browse/IDEA-20700'>IntelliJ</a> (fixed), and JDeveloper,<br>
but not in NetBeans. If we instead try to<br>
rename <code>j</code> to <code>i</code>, Eclipse and JBuilder both report the danger of shadowing, whereas<br>
NetBeans and IntelliJ do not. JDeveloper does not implement this second renaming.</li>
</ol></blockquote>

# Bug in Rename Method #
<ol>
<li><b>Renaming Method Can Lead to Shadowing of Statically Imported Method</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>    import static java.lang.String.*;<br>
<br>
    public class A {<br>
      static String m(int i) { return "42"; }<br>
      public static void main(String[] args) {<br>
        System.out.println(valueOf(23));<br>
      }<br>
    }<br>
</code></pre></blockquote>

<blockquote>This prints <code>23</code>. Renaming the method <code>A.m</code> to<br>
<code>valueOf</code> shadows the statically imported method<br>
<code>String.valueOf</code>, so the resulting program prints <code>42</code>.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=220779'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, and JDeveloper, but not in IntelliJ.</li>
</ol></blockquote>

# Bugs in Encapsulate Field #
<ol>
<li> <b>Should Not Allow Encapsulating Fields in Interfaces</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>     interface A {<br>
       int f = 23;<br>
     }<br>
</code></pre></blockquote>

<blockquote>Encapsulating field <code>A.f</code> should fail, since it is in an interface and hence no getter method can be<br>
created. This is, however, not detected, resulting in uncompilable code.</blockquote>

<blockquote>This bug was reported by someone else <a href='https://bugs.eclipse.org/bugs/show_bug.cgi?id=34310'>quite some time ago</a>,<br>
but judged "not critical".</blockquote>

<blockquote>The same bug also occurs in JBuilder and JDeveloper, but not in NetBeans and IntelliJ.</li></blockquote>

<li> <b>No Check for Method Hiding</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>     class A {<br>
       void getI(String s) { }<br>
<br>
       class C { class B { void m() { getI(""); } } private int i; }<br>
     }<br>
</code></pre></blockquote>

<blockquote>When encapsulating field <code>C.i</code>, Eclipse generates a getter method<br>
<code>getI()</code> in class C which shadows the access in<br>
method <code>C.B.m()</code> and leads to uncompilable code.</blockquote>


<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=209315'>Eclipse Bugs</a>, turned out to be a duplicate.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, <a href='http://www.jetbrains.net/jira/browse/IDEA-20720'>IntelliJ</a> (fixed), and JDeveloper.</li></blockquote>

<li> <b>No Check for Overloading Problems</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>     class A {<br>
       public int i;<br>
     }<br>
     class B extends A {<br>
       public int getI() { return 42; }<br>
       public static void main(String[] args) {<br>
         A a = new B();<br>
	 a.i = 23; <br>
	 System.out.println("i == "+a.i);<br>
       }<br>
     }<br>
</code></pre></blockquote>

<blockquote>This code prints <code>i == 23</code>, as it should. Now we can encapsulate<br>
field <code>A.i</code>, yielding the following code:</blockquote>

<pre><code>     class A {<br>
       private int i;<br>
       public void setI(int i) { this.i = i; }<br>
       public int getI() { return i; }<br>
     }<br>
<br>
     class B extends A {<br>
       public int getI() { return 42; }<br>
       public static void main(String[] args) {<br>
         A a = new B();<br>
	 a.setI(23);<br>
	 System.out.println("i == "+a.getI());<br>
     }<br>
</code></pre>

<blockquote>This code compiles and prints <code>i == 42</code>.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=209316'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, <a href='http://www.jetbrains.net/jira/browse/IDEA-20745'>IntelliJ</a> (fixed), and JDeveloper.</li></blockquote>

<li><b>No Check for Invalid Overloading</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>     class A {<br>
       public int i;<br>
     }<br>
     class B extends A {<br>
       private int getI() { return 42; }<br>
     }<br>
</code></pre></blockquote>

<blockquote>Encapsulating field <code>A.i</code> generates a public getter method<br>
<code>A.getI()</code> which cannot be overloaded by <code>B.getI()</code>,<br>
resulting in a compiler error. The<br>
same kind of problem occurs if, say, <code>B.getI()</code> were<br>
declared as having return type "float".</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=209317'>Eclipse Bugs</a>, turned out to be a duplicate.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, IntelliJ (fixed together with above), and JDeveloper.</li>
</ol></blockquote>

# Bugs in Extract Method #
<ol>
<li> Applying the Extract Method refactoring to the following selection results in a new method that fails to compile.<br>
<br>
<blockquote>Original program:<br>
<pre><code>  private int f(boolean b1, boolean b2) {<br>
    int n = 0;<br>
    int i = 0;<br>
    // Extract Method from here<br>
    if (b1)<br>
      i = 1;<br>
    if (b2)<br>
      n = n + i;<br>
    // to here<br>
    return n;<br>
  }<br>
</code></pre>
Applying Extract Method on the selected code (signalled by the comments)<br>
results with the following code:<br>
<pre><code>  private int f(boolean b1, boolean b2) {<br>
    int n = 0;<br>
    int i = 0;<br>
    n = newMethod(b1, b2, n);<br>
    return n;<br>
  }<br>
<br>
  private int newMethod(boolean b1, boolean b2, int n) {<br>
    int i;<br>
    if (b1)<br>
      i = 1;<br>
    if (b2)<br>
      n = n + i;<br>
    return n;<br>
  }<br>
</code></pre>
Problem: Eclipse did not identify that the local variable i should be sent as<br>
a parameter. The program doesn't compile anymore, because in the statement <code>n = n + i</code>; in the new method, the local variable <code>i</code> may not have been initialized.</blockquote>

<blockquote>Instead, the refactored source should look like this:<br>
<pre><code>  private int f(boolean b1, boolean b2) {<br>
    int n = 0;<br>
    int i = 0;<br>
    n = newMethod(b1, b2, n, i);<br>
    return n;<br>
  }<br>
<br>
  private int newMethod(boolean b1, boolean b2, int n, int i) {<br>
    if (b1)<br>
      i = 1;<br>
    if (b2)<br>
      n = n + i;<br>
    return n;<br>
  }<br>
</code></pre></blockquote>

<blockquote>Reported to <a href='https://bugs.eclipse.org/bugs/show_bug.cgi?id=109280'>Eclipse Bugs</a> (fixed).</blockquote>

<blockquote>This bug does not occur in JBuilder, NetBeans, IntelliJ, and JDeveloper.</li>
<li>Trying to apply the Extract Method refactoring on the following selection results in an unnecessary rejection.</blockquote>

<blockquote>Original program:<br>
<pre><code>  private int g(boolean b) {<br>
    int n = 10;<br>
    int i = 0;<br>
    while (i &lt; n) {<br>
      if (b) {<br>
        // Extract Method from here<br>
        i++;<br>
        n -= i;<br>
	// to here<br>
        break;<br>
      }<br>
      else i++;<br>
    }<br>
    return n;<br>
  }<br>
</code></pre></blockquote>

<blockquote>Applying Extract Method on the selected code (signalled by the comments)<br>
results in a rejection with the message: "Ambiguous return value: selected<br>
block contains more than one assignment to local variable".</blockquote>

<blockquote>In fact, because of the <code>break</code> statement, only the modified local variable <code>n</code>
should be returned. The local variable <code>i</code> will never be relevant on return from<br>
the extracted method. As a result, variable <code>i</code> can be localized in the new<br>
method.</blockquote>

<blockquote>The refactored source should look like this:<br>
<pre><code>  private int g(boolean b) {<br>
    int n = 10;<br>
    int i = 0;<br>
    while (i &lt; n) {<br>
      if (b) {<br>
        n = newMethod(i, n);<br>
        break;<br>
      }<br>
      else i++;<br>
    }<br>
    return n;<br>
  }<br>
<br>
  private int newMethod(int i, int n) {<br>
    i++;<br>
    n -= i;<br>
    return n;<br>
  }<br>
</code></pre>
Track this bug at <a href='https://bugs.eclipse.org/bugs/show_bug.cgi?id=109282'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug also occurs in JBuilder, NetBeans, and JDeveloper, but not in IntelliJ.</li></blockquote>

<li><b>Liveness Analysis in Extract Method Is Not Precise Enough</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>    public class A {<br>
      void m() {<br>
        int y;<br>
        int z;<br>
        //from<br>
	try {<br>
	  if(3==3)<br>
	    y = 1;<br>
	  else<br>
	    throw new Exception("boo");<br>
	} catch(Throwable t) {<br>
	  y=2;<br>
	}<br>
	z=y;<br>
        //to<br>
      }<br>
    }<br>
</code></pre></blockquote>

<blockquote>If we extract the statements between <code>//from</code> and<br>
<code>//to</code> into a method, Eclipse infers the local variable<br>
<code>y</code> to be live and passes it as a parameter to the<br>
newly created method. In fact, however, <code>y</code> is not live,<br>
so the resulting code does not compile due to a violation of the<br>
definite assignment restriction.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=220777'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug also occurs in JBuilder and NetBeans, but not in IntelliJ and JDeveloper.</li></blockquote>

<li><b>Local types are not handled correctly</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>    public class A {<br>
      void m() {<br>
        // from<br>
        class X { }<br>
        X x;<br>
        // to<br>
        x = null;<br>
      }<br>
    }<br>
</code></pre></blockquote>

<blockquote>If we extract the statements between <code>//from</code> and<br>
<code>//to</code> into a method <code>n</code> in Eclipse, we get</blockquote>

<pre><code>    public class A {<br>
      void m() {<br>
        X x;<br>
        n();<br>
        x = null;<br>
      }<br>
<br>
      void n() {<br>
        class X { }<br>
        X x;<br>
      }<br>
    }<br>
</code></pre>

<blockquote>which fails to compile.</blockquote>

<blockquote>The same bug occurs in <a href='http://www.jetbrains.net/jira/browse/IDEA-20769'>IntelliJ</a> (fixed).</li>
<li> <b>Incorrect dataflow analysis</b>:</blockquote>

<blockquote>Test case:<br>
<pre><code>  public class A {<br>
    void m(boolean b) {<br>
      int x = 42;<br>
      try {<br>
        // from<br>
        if(b) {<br>
          x = 23;<br>
          throw new Exception();<br>
        }<br>
        // to<br>
      } catch(Exception e) {<br>
        System.out.println(x);<br>
      }<br>
    }<br>
<br>
    public static void main(String[] args) {<br>
        new A().m(true);<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>This program prints <code>23</code>. Extracting the statement between the comments to a method <code>n</code> in Eclipse yields</blockquote>

<pre><code>  public class A {<br>
    void m(boolean b) {<br>
      int x = 42;<br>
      try {<br>
        x = n(b, x);<br>
      } catch(Exception e) {<br>
        System.out.println(x);<br>
      }<br>
    }<br>
<br>
    private int n(boolean b, int x) throws Exception {<br>
      if(b) {<br>
	x = 23;<br>
	throw new Exception();<br>
      }<br>
      return x;<br>
    }<br>
<br>
    public static void main(String[] args) {<br>
        new A().m(true);<br>
    }<br>
  }<br>
</code></pre>

<blockquote>Eclipse has <code>n</code> return the value of <code>x</code>, which is unnecessary, but not our main concern. More importantly, note that the<br>
value printed in the <code>catch</code> clause is the unmodified value of <code>x</code>, hence the refactored program prints <code>42</code>.</blockquote>

<blockquote>The same bug occurs in <a href='http://www.jetbrains.net/jira/browse/IDEA-21120'>IntelliJ</a>, although it correctly diagnoses that the value of <code>x</code> does not need to be returned. The bug is fixed in the newest release.</li>
<li><b>No overriding check for extracted method</b>:</blockquote>

<blockquote>Test case:<br>
<pre><code>  public class A {<br>
    int foo() {<br>
      int r = 42; // extract this line<br>
      return r;<br>
    }<br>
<br>
    public static void main(String[] args) {<br>
      System.out.println(new B().foo());<br>
    }<br>
  }<br>
<br>
  class B extends A {<br>
    public int m() {<br>
      return 23;<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>This program prints <code>42</code>. Extracting the indicated line into a new method <code>m</code> yields the following program, which prints <code>23</code>:<br>
<pre><code>  public class A {<br>
    int foo() {<br>
      int r = m();<br>
      return r;<br>
    }<br>
<br>
    public int m() {<br>
      int r = 42;<br>
      return r;<br>
    }<br>
<br>
    public static void main(String[] args) {<br>
      System.out.println(new B().foo());<br>
    }<br>
  }<br>
<br>
  class B extends A {<br>
    public int m() {<br>
      return 23;<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>This bug has been reported to <a href='https://bugs.eclipse.org/bugs/show_bug.cgi?id=367543'>Eclipse Bugs</a> and <a href='http://youtrack.jetbrains.net/issue/IDEA-79297'>the IntelliJ bug tracker</a>.</li>
</ol></blockquote>

# Bugs in Inline Local Variable #
<ol>
<li> <b>Incorrect handling of references to final variables</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>  class A {<br>
    void m() {<br>
      int j = 23;<br>
      final int i = j;<br>
      new Object() {<br>
	{ System.out.println(i); }<br>
      };<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>Inlining variable <code>i</code> replaces the initializer of the anonymous class<br>
with<br>
<pre><code>        { System.out.println(j); }<br>
</code></pre></blockquote>

<blockquote>which fails to compile, since <code>j</code> is not final and hence inaccessible<br>
from inside the anonymous class.</blockquote>

<blockquote>The same bug occurs in <a href='http://www.jetbrains.net/jira/browse/IDEA-20790'>IntelliJ</a>.</li>
<li> <b>Incorrect handling of references to shadowed fields</b>:</blockquote>

<blockquote>Test case:<br>
<pre><code>  public class A {<br>
    int f = 42;<br>
    int foo() {<br>
      int r = f;<br>
      int f = 23;<br>
      return r;<br>
    }<br>
	<br>
    public static void main(String[] args) {<br>
      System.out.println(new A().foo());<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>This program prints <code>42</code>.</blockquote>

<blockquote>Inlining variable <code>r</code> yields the following program, which prints <code>23</code>:<br>
<pre><code>  public class A {<br>
    int f = 42;<br>
    int foo() {<br>
      int f = 23;<br>
      return f;<br>
    }<br>
	<br>
    public static void main(String[] args) {<br>
      System.out.println(new A().foo());<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>This bug has been reported to <a href='https://bugs.eclipse.org/bugs/show_bug.cgi?id=367534'>Eclipse Bugs</a>.</blockquote>

<blockquote>This bug does not occur in IntelliJ.<br>
</li>
<li> <b>Incorrect handling of references to obscured types</b>:</blockquote>

<blockquote>Test case:<br>
<pre><code>  public class A {<br>
    int f = 23;<br>
    static class B {<br>
        static int f = 42;<br>
    }<br>
    int foo() {<br>
        int r = B.f;<br>
        A B = this;<br>
        return r;<br>
    }<br>
<br>
    public static void main(String[] args) {<br>
        System.out.println(new A().foo());<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>This program prints <code>42</code>.</blockquote>

<blockquote>Inlining variable <code>r</code> yields the following program, which prints <code>23</code>:<br>
<pre><code>  public class A {<br>
    int f = 23;<br>
    static class B {<br>
        static int f = 42;<br>
    }<br>
    int foo() {<br>
        A B = this;<br>
        return B.f;<br>
    }<br>
<br>
    public static void main(String[] args) {<br>
        System.out.println(new A().foo());<br>
    }<br>
  }<br>
</code></pre></blockquote>

<blockquote>Reported to <a href='https://bugs.eclipse.org/bugs/show_bug.cgi?id=367536'>Eclipse Bugs</a> and <a href='http://youtrack.jetbrains.net/issue/IDEA-79290'>IntelliJ</a>.<br>
</li>
</ol></blockquote>

# Bugs in Push Down Method #
Note: None of these example refactorings could be tested in JDeveloper.
<ol>
<li> <b>Incorrect handling of <code>super</code> accesses</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>     class A {<br>
       void k() { System.out.println(23); }<br>
     }<br>
     class B extends A {<br>
       void m() { super.k(); }<br>
       void k() { System.out.println(42); } <br>
     }<br>
     class C extends B {<br>
       public static void main(String[] args) {<br>
         new C().m();<br>
       }<br>
     }<br>
</code></pre></blockquote>

<blockquote>This prints <code>23</code>. Pushing down <code>B.m</code> into<br>
<code>C</code> just moves the method without any<br>
adjustments, so the resulting program prints <code>42</code>.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=211755'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug occurs in JBuilder, NetBeans, and <a href='http://www.jetbrains.net/jira/browse/IDEA-20816'>IntelliJ</a> (fixed).</li></blockquote>

<li> <b>Incorrect handling of field accesses</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>     class A {<br>
       protected int x = 23;<br>
       void m() { System.out.println(x); }<br>
     }<br>
     class B extends A {<br>
       protected int x = 42;<br>
       public static void main(String[] args) {<br>
         new B().m();<br>
       }<br>
     }<br>
</code></pre></blockquote>

<blockquote>This prints <code>23</code>. Pushing down <code>A.m</code> into<br>
<code>B</code> just moves the method without any</blockquote>

<blockquote>adjustments, so the resulting program prints <code>42</code>.</blockquote>


<blockquote>Reported as a comment to the previous bug.</blockquote>

<blockquote>The same bug occurs in JBuilder, NetBeans, and <a href='http://www.jetbrains.net/jira/browse/IDEA-20848'>IntelliJ</a> (fixed).<br>
<li> <b>Incorrect Handling of Type Accesses</b>:</blockquote>

<blockquote>Test case:<br>
<pre><code>     class A {<br>
       class B { public B() { System.out.println(23); } }<br>
       B m() { return new B(); }<br>
     }<br>
     class C extends A {<br>
       class B { public B() { System.out.println(42); } }<br>
       public static void main(String[] args) {<br>
         new C().m();<br>
       }<br>
     }<br>
</code></pre></blockquote>

<blockquote>This prints <code>23</code>. Pushing down <code>A.m</code> into<br>
<code>B</code> just moves the method without any<br>
adjustments, so the resulting program prints <code>42</code>.<br>
A similar issue exists with <code>throws</code> clauses.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=211859'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug occurs in JBuilder and NetBeans, but not in IntelliJ.</li></blockquote>

<li><b>Incorrect Handling of Private Method Accesses</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>     class A {<br>
       private void k() { System.out.println(23); }<br>
       void m() { k(); }<br>
     }<br>
     class B extends A {<br>
       protected void k() { System.out.println(42); }<br>
       public static void main(String[] args) {<br>
         new B().m();<br>
       }<br>
     }<br>
</code></pre></blockquote>

<blockquote>This prints <code>23</code>. Pushing down <code>A.m</code> into<br>
<code>B</code> just moves the method without any<br>
adjustments, so the resulting program prints <code>42</code>.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=211860'>Eclipse Bugs</a>.</blockquote>

<blockquote>The same bug occurs in JBuilder, NetBeans, and <a href='http://www.jetbrains.net/jira/browse/IDEA-20873'>IntelliJ</a> (fixed).</li></blockquote>

<li><b>No Check for References to Moved Method</b>:<br>
<br>
<blockquote>Test case:<br>
<pre><code>     class A {<br>
       void m() { }<br>
     }<br>
     class B extends A {<br>
       public static void main(String[] args) {<br>
         new A().m();<br>
       }<br>
     }<br>
</code></pre></blockquote>

<blockquote>Pushing down <code>A.m</code> into <code>B</code> does not detect the<br>
reference to <code>A.m</code> in <code>B.main</code>, so<br>
the resulting program fails to compile.</blockquote>

<blockquote>Reported to <a href='http://bugs.eclipse.org/bugs/show_bug.cgi?id=211861'>Eclipse Bugs</a>, turned out to be a duplicate.</blockquote>

<blockquote>The same bug also occurs in JBuilder and NetBeans, but not in IntelliJ.</li>