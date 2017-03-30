package bootcamp

case class Measurement(
  measurement_id: String,
  detector_id: Int,
  galaxy_id: Int,
  astrophysicist_id: Int,
  measurement_time: Long,
  amplitude_1: Float,
  amplitude_2: Float,
  amplitude_3: Float) {
  def isAnomaly = (amplitude_1>0.995 && amplitude_3 > 0.995 && amplitude_2 < 0.005)
}

object MeasurementString {
  val MeasurementRegex = "([\\w-]+),(\\d+),(\\d+),(\\d+),([\\d]+),([\\d\\.]+),([\\d\\.]+),([\\d\\.]+)".r
  def unapply(str: String): Option[Measurement] = str match {
    case MeasurementRegex(measurement_id,
          AsInt(detector_id),
          AsInt(galaxy_id),
          AsInt(astrophysicist_id),
          AsLong(measurement_time),
          AsFloat(amplitude_1),
          AsFloat(amplitude_2),
          AsFloat(amplitude_3))
        =>
        Some(Measurement(measurement_id,
          detector_id,
          galaxy_id,
          astrophysicist_id,
          measurement_time,
          amplitude_1,
          amplitude_2,
          amplitude_3)
      )
    case _ => None
  }
}

object AsInt {
  def unapply(s: String) = try{ Some(s.toInt) } catch {
    case e: NumberFormatException => None
  }
}
object AsFloat {
  def unapply(s: String) = try{ Some(s.toFloat) } catch {
    case e: NumberFormatException => None
  }
}
object AsLong {
  def unapply(s: String) = try{ Some(s.toLong) } catch {
    case e: NumberFormatException => None
  }
}