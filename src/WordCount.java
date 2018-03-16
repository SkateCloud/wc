import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WordCount {

    class Arg {
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

    private final List<Arg> argList = new ArrayList<>();

    private void start(String[] args) {
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

        {
            Arg arg = findArg('c');
            if (arg != null) {
                String content = readFile(arg.content);
                result.append("Character count: ").append(content.length()).append('\n');
            }
        }
        {
            Arg arg = findArg('w');
            if (arg != null) {
                String content = readFile(arg.content);
                int words = content.split(" |\\n").length;
                result.append("Word count: ").append(words).append('\n');
            }
        }
        {
            Arg arg = findArg('l');
            if (arg != null) {
                String content = readFile(arg.content);
                int lines = 1 + (int) content.chars().filter(it -> it == '\n').count();
                result.append("Line count: ").append(lines).append('\n');
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

    private void parseArgs(String[] args) {
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

    private Arg findArg(char flag) {
        return argList.stream().filter(it -> it.flag == flag).findAny().orElse(null);
    }

    private String readFile(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
