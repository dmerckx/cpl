package parser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

sealed abstract class Book;

case class Book1 (name: String, isbn: String) extends Book

case class BookWrap(book1: Book) extends Book

object BookParser extends StandardTokenParsers {
  
    
  lexical.delimiters += ("{", "}", ":", ":")
  lexical.reserved += ("wrap","book","has","isbn")

  def bookSpec(): Parser[Book]  = "book" ~ ident ~ "has" ~ "isbn" ~ ident ^^ {
            case "book" ~ name ~ "has" ~ "isbn" ~ isbn => new Book1(name, isbn)} 
  def mkParser(): Parser[Book] =
    "wrap" ~> bookSpec() ^^ {case b => new BookWrap(b)}
  
  def makeBookWrap(b:Book):BookWrap = {
    new BookWrap(b)
  }
 
            
  def parse (s: String) = {
    val tokens = new lexical.Scanner(s)
    phrase(mkParser)(tokens)
  }

  def test (exprString : String) = {
     parse (exprString) match {
       case Success(book, _) => println("Book: " + book)
       case Failure(msg, _) => println("Failure: " + msg)
       case Error(msg, _) => println("Error: " + msg)
     }
  }

  def main (args: Array[String]) = {
     test ("wrap book ABC has isbn DEF")
  }   
}