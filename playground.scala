object Foo extends Selectable:
  type Fields = (hello: String, meaningOfLife: Int)

  def selectDynamic(field: String): Any =
    field match
      case "hello" => "world"
      case "meaningOfLife" => 42
      case _ => sys.error("cannot happen unless `selectDynamic` is called directly")

@main def playground(): Unit =
   val a: String = Foo.hello
   val b: Int = Foo.meaningOfLife

   println(a)
   println(b)
   // println(Foo.stuff) // does not compile
   println(Foo.selectDynamic("stuff")) // unfortunately compiles

