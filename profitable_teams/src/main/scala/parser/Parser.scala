package parser


case class &[+A, +B](_1: A, _2: B)

sealed trait ParseResult[+A] {
  def map[B](f: A => B): ParseResult[B] = this match {
    case Success(a) => Success(f(a))
    case e@Error(_) => e
  }

  def isSuccess: Boolean

}
case class Success[A](a: A) extends ParseResult[A] { val isSuccess = true }
case class Error(message: String) extends ParseResult[Nothing] { val isSuccess = false }

trait Parser[A] extends (Row => ParseResult[A]) {
  parent =>

  def map[B](f: A => B): Parser[B] = Parser(parent.andThen(_.map(f)))

  def &[B](p: Parser[B]): Parser[A & B] = Parser(
    row => {
      val result = (parent(row), p(row))
      result match {
        case (Success(a), Success(b)) => Success(new &(a, b))
        case _ => Error(result.productIterator.collect { case Error(e) => e }.mkString(", "))
      }
    }
  )

}

object Parser {

  def apply[A](f: Row => ParseResult[A]): Parser[A] = new Parser[A] {
    def apply(row: Row): ParseResult[A] = f(row)
  }

  def get[A](field: String)(implicit extract: String => Option[A]): Parser[A] = Parser(f => {
    val x: Either[A, String] = for {
      value <- f.get(field).toLeft(s"$field not found").left
      result <- extract(value).toLeft(s"$field has invalid value '$value'").left
    } yield result
    x.fold(a => Success(a), e => Error(e))
  })
}