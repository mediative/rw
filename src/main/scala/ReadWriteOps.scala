object RW {
	trait Repr[T]

	trait SelOps[location, repr[_]] {
		def select: location => repr[location]
	}

	trait ReadOps[location, content, repr[_]] {
		def read: repr[location] => repr[content]
	}

	trait WriteOps[location, content, repr[_]] {
	  def write: repr[location] => repr[content]
	}

	case class Debug[T](value: T) extends Repr[T]
	case class MockLocation(path: String)
	case class MockContent(content: String)

	implicit object DebugSel extends SelOps[MockLocation, Debug] {
		//: MockLocation => Debug[MockLocation]
		def select = Debug(_)
	  	
	}

	implicit object DebugRead extends ReadOps[MockLocation, MockContent, Debug] {
		//: Debug[MockLocation] => Debug[MockContent]
	 	def read = loc => Debug(MockContent("-"))
	}

	implicit object DebugWrite extends WriteOps[MockLocation, MockContent, Debug] {
		//: Debug[MockLocation] => Debug[MockContent]
	 	def write = loc => Debug(MockContent("-"))
	}


	def Read[content, location, repr[_]](loc: location)(
		implicit r: ReadOps[location, content, repr],
	  				 s:  SelOps[location, repr]): repr[content] = {

		r.read(s.select(loc))
	}

	Read(MockLocation("bob"))

	// Read(1)
}