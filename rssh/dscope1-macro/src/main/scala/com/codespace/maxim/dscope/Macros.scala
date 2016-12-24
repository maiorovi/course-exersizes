package com.codespace.maxim.scope

import scala.reflect.macros.blackbox.Context
import scala.reflect.api._
import com.codespace.maxim.dscope._


object Macros {

  def contextImpl(c: Context)(block: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._

    //block.tree
    //    .toString()
    //    .replaceAll("dscope.this.`package`.", "")
    val transformer = new Transformer() {
         
          override def transform(tree:Tree):Tree = {
            System.err.println(s"MACRO:transform tree: ${tree}")
            tree match {
              case q"$f(...$argss)" =>
                System.err.println(s"f=$f  argss=$argss")
                f match {
                  case q"dscope.this.`package`.scope" => q"scope1(...$argss)"
                  case _ => super.transform(tree)
                }
              case _ => super.transform(tree)
            }
          }

    }

    val newTree = q"""ScopeContext() { 
                     scope1 => ${transformer.transform(block.tree)}
                     }
                   """
    println("MACRO:contextImpl:"+show(newTree))

    c.Expr[Unit](newTree)
  }

  def scopeImpl(c: Context)(state:c.Expr[State])(action: c.Expr[Unit]): c.Expr[Unit] = {
    import c.universe._
    val newTree = q"implicitly[com.codespace.maxim.dscope.scope].apply($state)(${action})"
    println("MACRO:scopeImpl:"+show(newTree))
    c.Expr[Unit](newTree)
  }

}
