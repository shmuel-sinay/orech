import com.android.tools.smali.baksmali.Adaptors.ClassDefinition;
import com.android.tools.smali.baksmali.BaksmaliOptions;
import com.android.tools.smali.dexlib2.DexFileFactory;
import com.android.tools.smali.dexlib2.Opcodes;
import com.android.tools.smali.dexlib2.iface.ClassDef;
import com.android.tools.smali.dexlib2.iface.DexFile;
import com.android.tools.smali.dexlib2.immutable.ImmutableDexFile;
import com.android.tools.smali.dexlib2.writer.pool.DexPool;
import com.android.tools.smali.dexlib2.writer.io.FileDataStore;
import com.android.tools.smali.smali.Smali;
import com.android.tools.smali.smali.SmaliOptions;
import com.android.tools.smali.baksmali.formatter.BaksmaliWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

/**
 * Replace single methods/classes inside a classes*.dex of a jar/apk, without touching other classes.
 *   show  <in> <classType> [method]
 *   edit  <in> <out> <classType> <method|-> <smaliFile>   (repeatable group of 3 after out)
 * method "-" means smaliFile is a whole replacement class (.smali).
 */
public class DexEdit {
    static String disassemble(ClassDef c) throws Exception {
        BaksmaliOptions o = new BaksmaliOptions();
        StringWriter sw = new StringWriter();
        BaksmaliWriter w = new BaksmaliWriter(sw); new ClassDefinition(o, c).writeTo(w); w.flush();
        return sw.toString();
    }

    static String replaceMethod(String text, String name, String body) {
        Matcher m = Pattern.compile("\\.method[^\\n]*\\s" + Pattern.quote(name) + "\\(").matcher(text);
        if (!m.find()) return null;
        int start = text.lastIndexOf(".method", m.start());
        int end = text.indexOf(".end method", m.end());
        if (start < 0 || end < 0) return null;
        return text.substring(0, start) + body.trim() + text.substring(end + ".end method".length());
    }

    static String extractMethod(String text, String name) {
        Matcher m = Pattern.compile("\\.method[^\\n]*\\s" + Pattern.quote(name) + "\\(").matcher(text);
        if (!m.find()) return null;
        int start = text.lastIndexOf(".method", m.start());
        int end = text.indexOf(".end method", m.end());
        return text.substring(start, end + ".end method".length());
    }

    static List<String> dexNames(ZipFile z) {
        List<String> n = new ArrayList<>();
        for (Enumeration<? extends ZipEntry> e = z.entries(); e.hasMoreElements(); ) {
            String s = e.nextElement().getName();
            if (s.matches("classes\\d*\\.dex")) n.add(s);
        }
        Collections.sort(n);
        return n;
    }

    static File extract(ZipFile z, String entry) throws Exception {
        File f = File.createTempFile("dex", ".dex");
        f.deleteOnExit();
        try (InputStream in = z.getInputStream(z.getEntry(entry))) { Files.copy(in, f.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        return f;
    }

    public static void main(String[] a) throws Exception {
        Opcodes ops = Opcodes.getDefault();
        if (a[0].equals("show")) {
            try (ZipFile z = new ZipFile(a[1])) {
                for (String dn : dexNames(z)) {
                    DexFile d = DexFileFactory.loadDexFile(extract(z, dn), ops);
                    for (ClassDef c : d.getClasses()) {
                        if (c.getType().equals(a[2])) {
                            String s = disassemble(c);
                            System.out.println("# in " + dn);
                            if (a.length > 3) { String mth = extractMethod(s, a[3]); System.out.println(mth == null ? "# method not found" : mth); }
                            else System.out.println(s);
                            return;
                        }
                    }
                }
                System.out.println("# class not found");
            }
            return;
        }
        // edit
        String in = a[1], out = a[2];
        Map<String, String> newClassText = new LinkedHashMap<>();   // type -> smali text
        String dexEntry = null;
        try (ZipFile z = new ZipFile(in)) {
            List<String[]> edits = new ArrayList<>();
            for (int i = 3; i + 2 < a.length; i += 3) edits.add(new String[]{a[i], a[i + 1], a[i + 2]});
            Set<String> wanted = new HashSet<>();
            for (String[] e : edits) wanted.add(e[0]);
            DexFile dex = null; File dexFile = null;
            for (String dn : dexNames(z)) {
                File f = extract(z, dn);
                DexFile d = DexFileFactory.loadDexFile(f, ops);
                boolean has = false;
                for (ClassDef c : d.getClasses()) if (wanted.contains(c.getType())) { has = true; break; }
                if (has) { dex = d; dexFile = f; dexEntry = dn; break; }
            }
            if (dex == null) throw new RuntimeException("no dex contains the requested classes");
            System.out.println("dex: " + dexEntry);
            Map<String, ClassDef> byType = new HashMap<>();
            for (ClassDef c : dex.getClasses()) byType.put(c.getType(), c);
            for (String[] e : edits) {
                String type = e[0];
                String body = new String(Files.readAllBytes(Paths.get(e[2])), StandardCharsets.UTF_8);
                if (e[1].equals("-")) { newClassText.put(type, body); continue; }
                if (e[1].startsWith("subst:")) {
                    String mname = e[1].substring(6);
                    ClassDef sc = byType.get(type);
                    if (sc == null) throw new RuntimeException("class not found in dex: " + type);
                    String curText = newClassText.containsKey(type) ? newClassText.get(type) : disassemble(sc);
                    String methodText = extractMethod(curText, mname);
                    if (methodText == null) throw new RuntimeException("method not found: " + type + "->" + mname);
                    String[] parts = body.split("\\r?\\n====\\r?\\n", -1);
                    String[] patLines = parts[0].trim().split("\\r?\\n");
                    StringBuilder rx = new StringBuilder();
                    for (int li = 0; li < patLines.length; li++) {
                        if (li > 0) rx.append("\\s+(?:\\.line\\s+\\d+\\s+)*");
                        rx.append(Pattern.quote(patLines[li].trim()));
                    }
                    Matcher pm = Pattern.compile(rx.toString()).matcher(methodText);
                    int cnt = 0; while (pm.find()) cnt++;
                    if (cnt != 1) throw new RuntimeException("pattern matched " + cnt + " times (need exactly 1) in " + mname);
                    String newMethod = Pattern.compile(rx.toString()).matcher(methodText).replaceFirst(Matcher.quoteReplacement(parts.length > 1 ? parts[1].trim() : ""));
                    newClassText.put(type, curText.replace(methodText, newMethod));
                    System.out.println("substituted in " + type + "->" + mname);
                    continue;
                }
                ClassDef c = byType.get(type);
                if (c == null) throw new RuntimeException("class not found in dex: " + type);
                String cur = newClassText.containsKey(type) ? newClassText.get(type) : disassemble(c);
                String repl = replaceMethod(cur, e[1], body);
                if (repl == null) throw new RuntimeException("method not found: " + type + "->" + e[1]);
                newClassText.put(type, repl);
                System.out.println("replaced " + type + "->" + e[1]);
            }
            // assemble the replaced classes alone
            Path tmp = Files.createTempDirectory("smali");
            for (Map.Entry<String, String> e : newClassText.entrySet()) {
                Path p = tmp.resolve(e.getKey().substring(1, e.getKey().length() - 1) + ".smali");
                Files.createDirectories(p.getParent());
                Files.write(p, e.getValue().getBytes(StandardCharsets.UTF_8));
            }
            File mini = File.createTempFile("mini", ".dex");
            SmaliOptions so = new SmaliOptions();
            so.outputDexFile = mini.getAbsolutePath();
            if (!Smali.assemble(so, Collections.singletonList(tmp.toString()))) throw new RuntimeException("smali assemble failed");
            DexFile miniDex = DexFileFactory.loadDexFile(mini, ops);
            // merge: original minus replaced types, plus assembled classes
            List<ClassDef> all = new ArrayList<>();
            for (ClassDef c : dex.getClasses()) if (!newClassText.containsKey(c.getType())) all.add(c);
            int added = 0;
            for (ClassDef c : miniDex.getClasses()) { all.add(c); added++; }
            System.out.println("classes: original=" + dex.getClasses().size() + " merged=" + all.size() + " assembled=" + added);
            File merged = File.createTempFile("merged", ".dex");
            DexPool.writeTo(new FileDataStore(merged), new ImmutableDexFile(ops, all));
            // rewrite zip
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(out))) {
                for (Enumeration<? extends ZipEntry> en = z.entries(); en.hasMoreElements(); ) {
                    ZipEntry ze = en.nextElement();
                    ZipEntry ne = new ZipEntry(ze.getName());
                    ne.setTime(ze.getTime());
                    if (ze.getMethod() == ZipEntry.STORED && !ze.getName().equals(dexEntry)) {
                        ne.setMethod(ZipEntry.STORED); ne.setSize(ze.getSize()); ne.setCompressedSize(ze.getCompressedSize()); ne.setCrc(ze.getCrc());
                    }
                    zos.putNextEntry(ne);
                    if (ze.getName().equals(dexEntry)) Files.copy(merged.toPath(), zos);
                    else try (InputStream is = z.getInputStream(ze)) { is.transferTo(zos); }
                    zos.closeEntry();
                }
            }
            System.out.println("written " + out);
        }
    }
}

