import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class UnitTest {
    // test -c
    public static void main(String[] args) throws IOException {
        // 基本功能测试
        // 测试-c
        test_c("abcd fgh", "wordcounttest.txt 字符数: 8\n"); // 测试-c基本功能
        test_c("","wordcounttest.txt 字符数: 0\n"); // 测试当遇到空文件时，是否运行正常
        // 测试-w
        test_w("hello world","wordcounttest.txt 单词数: 2\n"); // 测试-w基本功能
        test_w("", "wordcounttest.txt 单词数: 0\n"); // 测试当遇到空文件时，是否运行正常
        // 测试-l
        test_l("hello\nworld\n!","wordcounttest.txt 行数: 3\n"); // 测试-l基本功能
        test_l("","wordcounttest.txt 行数: 1\n"); // 测试当遇到空文件时，是否正常运行
        // 测试-o
        FileWriter fout = new FileWriter("wordcounttest.txt"); // 测试-o基本功能
        fout.append("hello\nworld\n!!!");
        fout.close(); // 将测试用例写入测试文件
        WordCount.main(new String[] {"-l","wordcounttest.txt","-o","word2.txt"}); // 执行函数
        String content = readFile("word2.txt"); // 读取输出文件内容
        String answer = "wordcounttest.txt 行数: 3\n";
        if (Objects.equals(content, answer)) { // 比较结果与预测结果
            System.out.println("-o test : " + " YES"); // 如果结果与预测结果相同，则输出YES
        } else {
            System.out.println("-o test : " + " NO"); // 如果结果与预测结果不同，则输出NO
        }
        // 测试多参数
        FileWriter fout_1 = new FileWriter("wordcounttest.txt"); // 将测试用例写入测试文件
        fout_1.append("hello\nworld\n!!!");
        fout_1.close();
        WordCount.main(new String[] {"-c","w","-l","wordcounttest.txt","-o","word2.txt"}); // 执行函数
        String content_1 = readFile("word2.txt"); // 读出测试结果
        String answer_1 = "wordcounttest.txt 行数: 3\n"; // 预测结果
        if (Objects.equals(content_1, answer_1)) { // 将测试结果与预测结果比较
            System.out.println("basic test : " + " YES"); // 如果结果与预测结果相同，则输出YES
        } else {
            System.out.println("basic test : " + " NO"); // 如果结果与预测结果不同，则输出NO
        }

        // 扩展功能测试
        //-a
        FileWriter fout_2 = new FileWriter("wordcounttest.txt"); // 将测试用例写入测试文件
        fout_2.append("hello\nworld\n!!!");
        fout_2.close();
        WordCount.main(new String[] {"-a","wordcounttest.txt"}); // 执行函数
        String content_2 = readFile("result.txt"); // 读出测试结果
        String answer_2 = "wordcounttest.txt 代码行/空行/注释行: 3/0/0\n"; // 预测结果
        if (Objects.equals(content_2, answer_2)) { // 将测试结果与预测结果比较
            System.out.println("-a test : " + " YES"); // 如果结果与预测结果相同，则输出YES
        } else {
            System.out.println("-a test : " + " NO"); // 如果结果与预测结果不同，则输出NO
        }
        // -s
        WordCount.main(new String[] {"-s","-a","*.txt"}); // 执行函数
        String content_3 = readFile("result.txt"); // 读出测试结果
        // 预测结果
        String answer_3 = "result.txt 代码行/空行/注释行: 1/0/0\nstop.txt 代码行/空行/注释行: 1/0/0\nwordcounttest.txt 代码行/空行/注释行: 3/0/0\nword2.txt 代码行/空行/注释行: 1/0/0\n";
        if (Objects.equals(content_3, answer_3)) { // 将测试结果与预测结果比较
            System.out.println("-s test : " + " YES"); // 如果结果与预测结果相同，则输出YES
        } else {
            System.out.println("-s test : " + " NO"); // 如果结果与预测结果不同，则输出NO
        }
        // -e
        FileWriter fout_4 = new FileWriter("wordcounttest.txt"); // 将测试用例写入测试文件
        fout_4.append("hello world !!! hello there");
        fout_4.close();
        WordCount.main(new String[] {"-w","wordcounttest.txt","-e","stop.txt"}); // 执行函数
        String content_4 = readFile("result.txt"); // 读出测试结果
        String answer_4 = "wordcounttest.txt 单词数: 3\n"; // 预测结果
        if (Objects.equals(content_4, answer_4)) { // 将测试结果与预测结果比较
            System.out.println("-e test : " + " YES"); // 如果结果与预测结果相同，则输出YES
        } else {
            System.out.println("-e test : " + " NO"); // 如果结果与预测结果不同，则输出NO
        }
    }
    private static void test_c(String input, String answer) throws IOException { // 简单操作-c测试函数
        FileWriter fout = new FileWriter("wordcounttest.txt");
        fout.append(input);
        fout.close();
        WordCount.main(new String[] {"-c","wordcounttest.txt"});
        String content = readFile("result.txt");
        if (Objects.equals(content, answer)) {
            System.out.println("-c test : " + " YES");
        } else {
            System.out.println("-c test : " + " NO");
        }
    }
    private static void test_w(String input, String answer) throws IOException { // 简单操作-w测试函数
        FileWriter fout = new FileWriter("wordcounttest.txt");
        fout.append(input);
        fout.close();
        WordCount.main(new String[] {"-w","wordcounttest.txt"});
        String content = readFile("result.txt");
        if (Objects.equals(content, answer)) {
            System.out.println("-w test : "+" YES");
        } else {
            System.out.println("-w test : " + " NO");
        }
    }
    private static void test_l(String input, String answer) throws IOException { // 简单操作-l测试函数
        FileWriter fout = new FileWriter("wordcounttest.txt");
        fout.append(input);
        fout.close();
        WordCount.main(new String[] {"-l","wordcounttest.txt"});
        String content = readFile("result.txt");
        if (content.equals(answer)) {
            System.out.println("-l test : " + " YES");
        } else {
            System.out.println("-l test : " + " NO");
        }
    }
    private static String readFile(String path) { // 读文件
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
