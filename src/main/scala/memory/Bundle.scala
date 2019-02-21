package memory

import chisel3._
import chisel3.util._

class MemAddrBundle extends Bundle {
  val addr = Output(UInt(32.W))
  val size = Output(UInt(3.W))
}

class MemDataBundle(val dataBits: Int) extends Bundle {
  val data = Output(UInt(dataBits.W))
}

class MemMaskDataBundle(dataBits: Int) extends MemDataBundle(dataBits) {
  val mask = Output(UInt((dataBits / 8).W))
}

class MemIO(val dataBits: Int = 32) extends Bundle {
  val a = Decoupled(new MemAddrBundle)
  val r = Flipped(Decoupled(new MemDataBundle(dataBits)))
  val w = Valid(new MemMaskDataBundle(dataBits))

  def isRead (): Bool = a.valid && !w.valid
  def isWrite(): Bool = a.valid &&  w.valid

  def toAHBLite(): AHBLiteIO = {
    val mem2ahb = Module(new MemIO2AHBLiteConverter)
    mem2ahb.io.in <> this
    mem2ahb.io.out
  }

  def toAXI4(): AXI4 = {
    val mem2axi = Module(new MemIO2AXI4Converter)
    mem2axi.io.in <> this
    mem2axi.io.out
  }
}
