import com.reandroid.apk.ApkModule;
import com.reandroid.archive.InputSource;
import com.reandroid.archive.ByteInputSource;
import com.reandroid.arsc.chunk.xml.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class XmlKeys {
  static List<ResXmlElement> withKey(ResXmlDocument doc, java.util.function.Predicate<String> p) {
    List<ResXmlElement> out = new ArrayList<>();
    Iterator<ResXmlElement> it = doc.recursiveElements();
    while (it.hasNext()) {
      ResXmlElement e = it.next();
      ResXmlAttribute a = e.searchAttributeByName("key");
      if (a != null && p.test(a.getValueAsString())) out.add(e);
    }
    return out;
  }
  static byte[] refreshed(ResXmlDocument d) { d.refresh(); return d.getBytes(); }
  public static void main(String[] a) throws Exception {
    ApkModule m = ApkModule.loadApkFile(new File(a[1]));
    if (a[0].equals("scan")) {
      Pattern pat = Pattern.compile(a[2], Pattern.CASE_INSENSITIVE);
      for (InputSource s : m.getInputSources()) {
        String n = s.getAlias();
        if (!n.startsWith("res/xml/") || !n.endsWith(".xml")) continue;
        ResXmlDocument d;
        try { d = m.getResXmlDocument(n); } catch (Throwable t) { continue; }
        if (d == null) continue;
        for (ResXmlElement e : withKey(d, k -> k != null && pat.matcher(k).find()))
          System.out.println(n + " | " + e.searchAttributeByName("key").getValueAsString());
      }
    } else {
      int removed = 0; java.util.Map<String, ResXmlDocument> edited = new java.util.LinkedHashMap<>();
      for (String line : Files.readAllLines(Paths.get(a[3]))) {
        line = line.trim(); if (line.isEmpty()) continue;
        String[] pk = line.split("\\|");
        ResXmlDocument d = edited.containsKey(pk[0]) ? edited.get(pk[0]) : m.getResXmlDocument(pk[0]);
        if (d == null) { System.out.println("MISSING FILE " + pk[0]); continue; }
        if (pk[1].equals("*")) { d.getDocumentElement().removeElements(e -> true); edited.put(pk[0], d); removed++; System.out.println("EMPTIED " + pk[0]); continue; }
        List<ResXmlElement> t = withKey(d, k -> pk[1].equals(k));
        if (t.isEmpty()) { System.out.println("NOT FOUND " + line); continue; }
        for (ResXmlElement e : t) e.removeSelf(); edited.put(pk[0], d);
        removed += t.size();
        System.out.println("REMOVED " + line + " x" + t.size());
      }
      System.out.println("TOTAL " + removed);
      for (java.util.Map.Entry<String, ResXmlDocument> en : edited.entrySet()) { m.add(new ByteInputSource(refreshed(en.getValue()), en.getKey())); }
      if (removed > 0) m.writeApk(new File(a[2]));
    }
  }
}




