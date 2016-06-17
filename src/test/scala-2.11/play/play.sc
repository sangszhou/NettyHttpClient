class MyMonad[A](value: A) {

  def map[B](f: A => B): MyMonad[B] = new MyMonad[B](f(value))
  def flatMap[B](f: A => MyMonad[B]): MyMonad[B] = f(value)

  override def toString: String = value.toString
}

for(a <- new MyMonad(1);
    b <- new MyMonad(2)) yield a + b

