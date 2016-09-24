import com.bizo.mighty.csv.CSVReader
import java.io.InputStream

package object parser {
  type Row = Map[String, String]

  val read: InputStream => Stream[Row] = inputStream => {
    val reader = CSVReader(inputStream).toStream
    val (header, body) = (reader.head, reader.tail)
    body map {row =>
      row.zipWithIndex.map{case(value, i) => (header(i) -> value)}.toMap
    }
  }
}
