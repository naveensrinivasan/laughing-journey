#Catch new CPD errors and ignore old ones

In an existing project there is always lots of copy paste code and when integrating tool like [CPD](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html) into existing codebase the tool generates a report like this

``` xml
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
```
The goal is to ignore old CPD errors and if there are any new Copy Paste Code is introduced then flag that as an error.

The command line options are

```
Requires 3 parameters oldcpdfile.xml newcpdfile.xml FileFilterPath 

example  "/Users/naveen/build/reports/cpd/cpdCheck-old.xml" 
         "/Users/naveen/build/reports/cpd/cpdCheck.xml"
         "/core"
```