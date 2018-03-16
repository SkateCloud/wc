import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class WordCount {

    class Arg { // 解析参数列表所需类
        public char flag;
        public String content;

        public Arg(char flag, String content) {
            this.flag = flag;
            this.content = content;
        }

        public boolean isCWL() {
            return "cwl".indexOf(flag) != -1;
        }

    }

    public static void main(String[] args) {
        new WordCount().start(args);
    }

    private final List<Arg> argList = new ArrayList<>(); // 解析后的列表

    private void start(String[] args) { // 主函数
        parseArgs(args);

        StringBuilder result = new StringBuilder();

        for (int i = argList.size() - 1; i >= 0; --i) {
            Arg arg = argList.get(i);
            if (arg.isCWL()) {
                if (arg.content.isEmpty()) {
                    throw new RuntimeException("Flag " + arg.flag + " doesn't have file specified.");
                } else {
                    for (int j = i - 1; j >= 0; --j) {
                        Arg otherArg = argList.get(j);
                        if (otherArg.isCWL() && otherArg.content.isEmpty()) {
                            otherArg.content = arg.content;
                        }
                    }
                    break;
                }
            }
        }

        boolean recursive = findArg('s') != null;

        {
            Arg arg = findArg('c');
            if (arg != null) {
                for (String path : findFiles(arg.content, recursive)) {
                    String content = readFile(path);
                    result.append(path).append(" 字符数: ").append(content.length()).append('\n');
                }
            }
        }
        {
            Arg arg = findArg('w');
            Arg arg1 = findArg('e');

            Set<String> blacklist = new HashSet<>();
            if (arg1 != null) {
                blacklist.addAll(Arrays.asList(readFile(arg1.content).split(" ")));
            }

            if (arg != null) {
                for (String path : findFiles(arg.content, recursive)) {
                    String content = readFile(path);
                    int words = (int) Arrays.stream(content.split(" |\\n"))
                            .filter(it -> !blacklist.contains(it))
                            .filter(it -> !it.trim().isEmpty())
                            .count();
                    result.append(path).append(" 单词数: ").append(words).append('\n');
                }
            }
        }
        {
            Arg arg = findArg('l');
            if (arg != null) {
                for (String path : findFiles(arg.content, recursive)) {
                    String content = readFile(path);
                    int lines = 1 + (int) content.chars().filter(it -> it == '\n').count();
                    result.append(path).append(" 行数: ").append(lines).append('\n');
                }
            }
        }
        {
            Arg arg = findArg('a');
            if (arg != null) {
                for (String path : findFiles(arg.content, recursive)) {
                    String content = readFile(path);
                    int codeline = 0, emptyline = 0, commentline = 0;
                    String[] lines = content.split(System.getProperty("line.separator"));
                    for (int i = 0 ; i < lines.length ; ++i) {
                        if (lines[i].trim().length() <= 1) {
                            emptyline += 1;
                        } else if (lines[i].indexOf("//") >= 1) {
                            commentline += 1;
                        } else {
                            codeline += 1;
                        }
                    }
                    result.append(path).append(" 代码行/空行/注释行: ").append(codeline).append('/').append(emptyline).append('/').append(commentline).append('\n');
                }
            }
        }
        {
            Arg arg = findArg('o');
            String filename = arg == null ? "result.txt" : arg.content;
            try (FileWriter fout = new FileWriter(filename)) {
                fout.append(result.toString());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void parseArgs(String[] args) { // 解析参数
        int state = 0;
        char readFlag = '\0';
        for (int i = 0; i < args.length; ++i) {
            String cur = args[i];
            boolean isFlag = cur.length() == 2 && cur.charAt(0) == '-';
            if (state == 0 && !isFlag) {
                throw new RuntimeException("Can't parse argument " + cur);
            } else if (state == 0) {
                readFlag = cur.charAt(1);
                state = 1;
            } else {
                if (isFlag) {
                    argList.add(new Arg(readFlag, ""));
                    readFlag = cur.charAt(1);
                } else {
                    argList.add(new Arg(readFlag, cur));
                    state = 0;
                }
            }
        }
        if (state == 1)
            argList.add(new Arg(readFlag, ""));
    }

    private List<String> findFiles(String filter, boolean recursive) {
        filter = filter.replace(".", "\\.").replace("*", ".*");
        return findFiles("./", Pattern.compile(filter), recursive);
    }

    private List<String> findFiles(String basePath, Pattern filter, boolean recursive) {
        List<String> ret = new ArrayList<>();
        for (File f : new File(basePath).listFiles()) {
            if (f.isDirectory()) {
                if (recursive) {
                    ret.addAll(findFiles(f.getAbsolutePath(), filter, true));
                }
            } else {
                if (filter.matcher(f.toPath().getFileName().toString()).matches()) {
                    ret.add(f.getPath().replace("./", ""));
                }
            }
        }
        return ret;
    }

    private Arg findArg(char flag) { // 搜索参数
        return argList.stream().filter(it -> it.flag == flag).findAny().orElse(null);
    }

    private String readFile(String path) { // 读文件
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
