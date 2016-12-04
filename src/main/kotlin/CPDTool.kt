import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.int
import org.json.XML
import java.io.File

/*
Sample cpd xml
<pmd-cpd>
   <duplication lines="486" tokens="2504">
      <file line="6651"
            path="/Users/naveen/abc.java"/>
      <file line="5396"
            path="/Users/naveen/dfe.java"/>
      <codefragment><![CDATA[            obj.setId(null);
            saveList.add(obj);
          }
        }

      }
  }

 */

/*
Command line options
old CPD file :- "/Users/naveen/build/reports/cpd/cpdCheck-old.xml"
new CPD file :-  "/Users/naveen/build/reports/cpd/cpdCheck.xml"
filter Path :- "/core"
 */
fun main(args: Array<String>) {
    if (args.count() < 2) {
        val commandLineOptions = """
Requires 3 parameters oldcpdfile.xml newcpdfile.xml FileFilterPath
example  "/Users/naveen/build/reports/cpd/cpdCheck-old.xml"
         "/Users/naveen/build/reports/cpd/cpdCheck.xml"
         "/core"
                    """
        println(commandLineOptions)
        return
    }

    data class CPDFile(val name: String, val path: String, val line: Int)
    data class CPD(val codefragment: String, val tokens: Int?, val lines: Int,
                   val fileInfo: Pair<CPDFile, CPDFile>)

    fun cpdFile(files: JsonArray<JsonObject>, path: String): Pair<CPDFile, CPDFile> {
        val first = files.first().map["path"].toString().substringAfter(path)
        val second = files.last().map["path"].toString().substringAfter(path)
        return Pair(CPDFile(File(first).name,
                            File(first).path,
                            files.first()["line"] as Int),
                    CPDFile(File(second).name,
                            File(second).path,
                            files.last()["line"] as Int))
    }

    @Suppress("UNCHECKED_CAST")
    fun cpd(fileName: String, path: String): List<CPD> {
        val text = File(fileName).readText()
        val json = Parser().parse(StringBuilder(XML.toJSONObject(text).toString())) as JsonObject
        val duplications = (json["pmd-cpd"] as JsonObject)["duplication"] as JsonArray<JsonObject>
        return duplications.map {
            CPD(it["codefragment"] as String,
                it.int("tokens"),
                it.int("lines") as Int,
                cpdFile((it.map["file"] as JsonArray<JsonObject>),
                        path))
        }
    }

    val newCPD = cpd(args[1],
                     args[2])
    val originialCPD = cpd(args[0],
                           args[2])

    val subtract = newCPD.toHashSet().subtract(originialCPD)
    if (subtract.count() > 0) {
        val diff = subtract.map { it.fileInfo.toString() }.reduce { s1, s2 -> s1 + s2 }
        System.err.println(diff)
        System.exit(1)
    }
}
